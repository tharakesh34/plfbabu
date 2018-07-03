package com.pennanttech.pennapps.pff.sampling.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAOImpl;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAOImpl;

public class SamplingDAOImpl extends SequenceDao<Sampling> implements SamplingDAO {
	private static Logger logger = LogManager.getLogger(SamplingDAOImpl.class);

	public long save(Sampling sampling, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (sampling.getId() == 0) {
			sampling.setId(getNextValue("SeqSampling"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into sampling");
		sql.append(tableType.getSuffix());
		sql.append("(id, keyreference, createdby, createdon,");
		sql.append(" tenure, interestrate, loanEligibility, foireligibility, irreligibility, emi,totalincome,");
		sql.append(" totalliability, remarks, samplingon, decision, recommendedamount, decisionon,resubmitreason,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(");
		sql.append(" :id, :keyReference, :createdBy, :createdOn,");
		sql.append(" :tenure, :interestRate, :loanEligibility, :foirEligibility, :irrEligibility, :emi,:totalIncome,");
		sql.append(
				" :totalLiability, :remarks, :samplingOn, :decision, :recommendedAmount, :decisionOn,:resubmitReason,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode, :taskId, :nextTaskId, :recordType, :workflowId)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sampling);

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return sampling.getId();
	}

	@Override
	public long getIncomeLinkId(long id, long custId) {
		logger.debug(Literal.ENTERING);

		long linkId = getIncomeLinkIdByCustId(custId, id);

		if (linkId > 0) {
			return linkId;
		}

		linkId = getNextValue(IncomeDetailDAOImpl.SEQUENCE_LINK);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_incomes values(:id, :custid, :linkid) ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("custid", custId);
		source.addValue("linkid", linkId);

		this.jdbcTemplate.update(sql.toString(), source);

		return linkId;
	}
	
	@Override
	public long getIncomeLinkIdByCustId(long custId, long samplinId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_sampling_incomes");
		sql.append(" where custid=:custid and samplingid =:id");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		source.addValue("id", samplinId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}

	@Override
	public long getLiabilityLinkId(long id, long custId) {
		logger.debug(Literal.ENTERING);

		long linkId = getLiabilityLinkId1(custId, id);

		if (linkId > 0) {
			return linkId;
		}

		linkId = getNextValue(ExternalLiabilityDAOImpl.SEQUENCE_LINK);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_liabilities values(:id, :custid, :linkid)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("custid", custId);
		source.addValue("linkid", linkId);

		this.jdbcTemplate.update(sql.toString(), source);

		return linkId;
	}

	private long getLiabilityLinkId1(long custId, long samplingId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select coalesce(max(linkid), 0) from link_sampling_liabilities where custid=:custid and samplingid = :id");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		source.addValue("id", samplingId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}

	@Override
	public long getCollateralLinkId(long id, String CollateralReference) {
		logger.debug(Literal.ENTERING);

		long linkId = getCollateralLinkId(CollateralReference, id,"");

		if (linkId > 0) {
			return linkId;
		}

		linkId = getNextValue("seqcollaterallink");
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_collaterals values(:id, :collateralreference, :linkid)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("collateralreference", CollateralReference);
		source.addValue("linkid", linkId);

		this.jdbcTemplate.update(sql.toString(), source);

		return linkId;
	}

	public long getCollateralLinkId(String collateralreference, long samplingId,String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_sampling_collaterals");
		sql.append(type);
		sql.append(" where samplingid = :id and collateralreference=:collateralreference");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("collateralreference", collateralreference);
		source.addValue("id", samplingId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}

	public void update(Sampling sampling, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update sampling");
		query.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		query.append(" set tenure =:tenure, interestrate = :interestRate, loanEligibility = :loanEligibility,");
		query.append(" foireligibility =:foirEligibility, irreligibility = :irrEligibility, emi = :emi,");
		query.append(
				" version = :version, lastmntby = :lastMntBy, lastmnton = :lastMntOn, recordstatus = :recordStatus, rolecode = :roleCode,");
		query.append(
				" nextrolecode = :nextRoleCode, taskid = :taskId, nexttaskid = :nextTaskId, recordtype = :recordType, workflowid = :WorkflowId");
		query.append(" where id = :Id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sampling);
		recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<CollateralSetup> getCollateralsBySamplingId(Long samplingId) {
		logger.debug(Literal.ENTERING);
		List<CollateralSetup> collaterals = new ArrayList<>();

		StringBuilder sql = new StringBuilder();

		sql.append(" select collateralReference collateralref, collateraltype ");
		sql.append(" from link_sampling_collaterals s");
		sql.append(" inner join (select distinct collateralref, collateraltype from collateralsetup_view)");
		sql.append(" c on c.collateralref = s.collateralReference");
		sql.append(" where samplingId = :samplingId ");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<CollateralSetup> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralSetup.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingId", samplingId);

		try {
			collaterals.addAll(this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper));
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return collaterals;
	}

	public void delete(Sampling sampling, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append(" delete from sampling");
		query.append(tableType.getSuffix());
		query.append(" where id = :id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sampling);
		int recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public Sampling getSampling(long samplingid, String type) {
		logger.debug(Literal.ENTERING);
		Sampling sampling = null;

		StringBuilder sql = new StringBuilder();
		sql.append("select * from sampling").append(type).append(" where id=:samplingid");

		RowMapper<Sampling> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Sampling.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingid", samplingid);

		try {
			sampling = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return sampling;
	}

	// Get List of Income details of customer
	public List<CustomerIncome> getIncomes(long samplingid) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from sampling_income_details_view");
		sql.append(" where linkid in(");
		sql.append(" (select coalesce(linkid, 0) from link_sampling_incomes");
		sql.append(" where samplingid = :samplingid))");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingid", samplingid);

		RowMapper<CustomerIncome> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	public List<CustomerExtLiability> getObligations(long samplingid) {
		logger.debug(Literal.LEAVING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from sampling_liabilities_view");
		sql.append(" where linkid in(");
		sql.append(" (select coalesce(linkid, 0) from link_sampling_liabilities");
		sql.append(" where samplingid = :samplingid))");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingid", samplingid);
		RowMapper<CustomerExtLiability> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerExtLiability.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	public long getLinkId(Sampling sampling, String collRef, String inputSource) {
		logger.debug(Literal.ENTERING);

		String table = null;
		if (inputSource.equals("income")) {
			table = "link_sampling_incomes";
		} else if (inputSource.equals("obligation")) {
			table = "link_sampling_liabilities";
		} else {
			table = "link_sampling_collaterals";
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid),0) from ");
		sql.append(table);
		sql.append(" where samplingid = :id");
		if (inputSource.equals("collaterals")) {
			sql.append(" and collateralreference = :collref");
		}

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", sampling.getId());
		if (collRef != null) {
			source.addValue("collref", collRef);
		}
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
			linkid = 0;
		}

		logger.debug(Literal.LEAVING);
		return linkid;
	}

	@Override
	public List<Customer> getCustomers(String keyreference, String type) {
		logger.debug(Literal.LEAVING);

		List<Customer> list = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select 1 custTypeCode, cu.custid, cu.custCif, cu.custshrtname, cu.phonenumber,");
		sql.append(" ca.custaddrtype custAddlVar1");
		sql.append(" from sampling").append(type).append(" s");
		sql.append(" inner join financemain_view fm on fm.finreference = s.keyreference");
		sql.append(" inner join customers_view cu on cu.custid = fm.custid");
		sql.append(" inner join customeraddresses_view ca on ca.custid = cu.custid");
		sql.append(" where s.keyreference = :keyreference and ca.custaddrpriority = :custaddrpriority");
		sql.append(" union all");
		sql.append(" select 2 custTypeCode, cu.custid, cu.custCif, cu.custshrtname, cu.phonenumber,");
		sql.append(" ca.custaddrtype custAddlVar1");
		sql.append(" from sampling").append(type).append(" s");
		sql.append(" inner join finjointaccountdetails_view ja on ja.finreference = s.keyreference");
		sql.append(" inner join customers_view cu on cu.custid = ja.custid");
		sql.append(" inner join customeraddresses_view ca on ca.custid = cu.custid");
		sql.append(" where s.keyreference = :keyreference and ca.custaddrpriority = :custaddrpriority");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", keyreference);
		source.addValue("custaddrpriority", 5);
		RowMapper<Customer> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		try {
			list = jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public Sampling getSampling(String keyReference, String type) {
		logger.debug(Literal.ENTERING);
		Sampling sampling = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select distinct");
		sql.append(" s.*,");
		sql.append(" ls.linkid incomeLinkId,ll.linkid liabilityLinkId ");
		sql.append(" from sampling").append(type).append(" s");
		sql.append(" left join link_sampling_incomes ls on ls.samplingid = s.id");
		sql.append(" left join link_sampling_liabilities ll on ll.samplingid=s.id");
		sql.append(" where s.keyreference = :keyreference");

		RowMapper<Sampling> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Sampling.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyReference);

		try {
			sampling = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return sampling;
	}

	@Override
	public List<CollateralSetup> getCollaterals(String keyreference) {
		StringBuilder sql = new StringBuilder();
		sql.append("select depositorcif, depositorname, cs.collateralref, cs.collateralccy,");
		sql.append(" collateraltype, collateraltypename, expirydate, nextreviewdate");
		sql.append(" from collateralassignment_view ca");
		sql.append(" inner join collateralsetup_view cs on cs.collateralref = ca.collateralref");
		sql.append(" where ca.reference = :keyreference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", keyreference);
		try {
			return jdbcTemplate.query(sql.toString(), source,
					ParameterizedBeanPropertyRowMapper.newInstance(CollateralSetup.class));
		} catch (DataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Map<String, String> getEligibilityRules() {
		Map<String, String> rules = new HashMap<>();
		rules.put(Sampling.RULE_CODE_EMI,
				"1/((1-(1/Math.pow((1+(finProfitRate/1200)),noOfTerms)))*1200/finProfitRate)");

		StringBuilder sql = new StringBuilder();
		sql.append("select rulecode, sqlrule from rules where rulemodule=:rulemodule and rulecode in (:rulecode)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("rulemodule", "ELGRULE");
		source.addValue("rulecode",
				Arrays.asList(Sampling.RULE_CODE_FOIRAMT, Sampling.RULE_CODE_IIRMAX, Sampling.RULE_CODE_EMI));

		try {
			jdbcTemplate.query(sql.toString(), source, new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					rules.put(rs.getString(1), rs.getString(2));
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return rules;
	}

	public ExtendedFieldRender getCollateralExtendedFields(String collReference, String tableName, String type) {
		ExtendedFieldRender extRender = null;
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(tableName).append(type);
		sql.append(" where reference = :collReference");
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<ExtendedFieldRender> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExtendedFieldRender.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("collReference", collReference);

		try {
			extRender = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return extRender;
	}

	public Map<String, Object> getExtendedField(long linkId, String reference, String tableName, String type) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> renderMap = null;

		type = type.toLowerCase();

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select * from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" union all select * from ");
			sql.append(tableName);
			sql.append(" T  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" where reference =T.reference)) T where T.reference = :linkid ");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
			sql.append(" where  reference = :reference order by seqno");
		}
		logger.trace(Literal.SQL + sql.toString());

		source.addValue("linkid", String.valueOf(linkId));
		source.addValue("reference", reference);
		try {
			renderMap = this.jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
			renderMap = new HashMap<>();
		}

		logger.debug(Literal.LEAVING);
		return renderMap;
	}

	@Override
	public boolean isExist(String finReference, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from sampling");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where keyreference=:keyreference");

		int count = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", finReference);
		try {
			count = jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {

		}

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public long getIncomeSnapLinkId(long samplingId, long custId) {
		logger.debug(Literal.ENTERING);

		long linkId = getIncomeSnapLink(samplingId, custId);

		if (linkId > 0) {
			return linkId;
		}

		linkId = getNextValue(IncomeDetailDAOImpl.SEQUENCE_LINK);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_incomes_snap values(:samplingid, :custid, :linkid)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		source.addValue("custid", custId);
		source.addValue("linkid", linkId);
		this.jdbcTemplate.update(sql.toString(), source);

		return linkId;
	}

	private long getIncomeSnapLink(long samplingId, long custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_sampling_incomes_snap");
		sql.append(" where samplingid = :samplingid and custid=:custid");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		source.addValue("custid", custId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}

	@Override
	public void setLiabilitySnapLinkId(CustomerExtLiability liability) {
		logger.debug(Literal.ENTERING);

		long linkId = liability.getLinkId();

		if (linkId > 0) {
			return;
		}

		linkId = getLiabilitySnapLinkId(liability.getCustId());

		if (linkId > 0) {
			liability.setLinkId(linkId);
			return;
		}

		linkId = getNextValue(ExternalLiabilityDAOImpl.SEQUENCE_LINK);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_liabilities_snap values(:custId, :linkId)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", liability.getCustId());
		source.addValue("linkid", linkId);

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public long getCollateralSnapLinkId(long samplingId, String collateralRef) {
		logger.debug(Literal.ENTERING);

		long linkId = getCollateralLinkId(collateralRef, samplingId,"_snap");

		if (linkId > 0) {
			return linkId;
		}
		
		linkId = getNextValue("seqcollaterallink");
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_collaterals_snap values(:samplingId, :collateralRef,:linkId)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingId",samplingId);
		source.addValue("collateralRef", collateralRef);
		source.addValue("linkId", linkId);

		this.jdbcTemplate.update(sql.toString(), source);
		return linkId;
	}
	
	private long getLiabilitySnapLinkId(long custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_sampling_liabilities_snap where custid=:custid");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}

	@Override
	public BigDecimal getLoanEligibility(String finReference, String eligibilityRule) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(ruleresult, 0) from financeeligibilitydetail_view er");
		sql.append(" inner join rules r on r.ruleid = er.elgrulecode");
		sql.append(" where finreference = :finreference and rulecode = :rulecode");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finreference", finReference);
		source.addValue("rulecode", eligibilityRule);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return BigDecimal.ZERO;
	}

	@Override
	public Map<String, Object> getRemarks(long samplingId) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append(" select fieldName, remarks from sampling_remarks");
		sql.append(" where samplingId=:samplingId");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingId", samplingId);

		jdbcTemplate.query(sql.toString(), paramSource, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				map.put(rs.getString(1), rs.getString(2));
			}
		});
		logger.debug(Literal.LEAVING);
		return map;
	}
	public String getCollateralRef(Sampling sampling, String linkId, String inputSource) {
		logger.debug(Literal.ENTERING);

		String table = null;
		if (inputSource.equals("income")) {
			table = "link_sampling_incomes";
		} else if (inputSource.equals("obligation")) {
			table = "link_sampling_liabilities";
		} else {
			table = "link_sampling_collaterals";
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select collateralreference from ");
		sql.append(table);
		sql.append(" where samplingid = :id");
		if (inputSource.equals("collaterals")) {
			sql.append(" and linkid = :linkId");
		}

		String collReference = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", sampling.getId());
		if (linkId != null) {
			source.addValue("linkId", Integer.parseInt(linkId));
		}
		try {
			collReference = jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (DataAccessException e) {
			collReference = null;
		}

		logger.debug(Literal.LEAVING);
		return collReference;
	}
	
	@Override
	public long getLinkId(long samplingId, String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from ");
		sql.append(tableName);
		sql.append(" where samplingid=:samplingid");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}
	
}
