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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingCollateral;
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
		sql.append(
				" tenure, interestrate, loanEligibility, foireligibility, irreligibility, lcreligibility, ltveligibility, emi, totalincome,");
		sql.append(" totalliability, remarks, samplingon, decision, recommendedamount, decisionon, resubmitreason,");
		sql.append(" totalCustomerIntObligation, totalCoApplicantsIntObligation,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(");
		sql.append(" :id, :keyReference, :createdBy, :createdOn,");
		sql.append(
				" :tenure, :interestRate, :loanEligibility, :foirEligibility, :irrEligibility, :lcrEligibility, :ltvEligibility, :emi,:totalIncome, ");
		sql.append(
				" :totalLiability, :remarks, :samplingOn, :decision, :recommendedAmount, :decisionOn, :resubmitReason,");
		sql.append(" :totalCustomerIntObligation, :totalCoApplicantsIntObligation,");
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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		source.addValue("id", samplinId);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		source.addValue("id", samplingId);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public long getCollateralLinkId(long id, String CollateralReference) {
		logger.debug(Literal.ENTERING);

		long linkId = getCollateralLinkId(CollateralReference, id, "");

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

	public long getCollateralLinkId(String collateralreference, long samplingId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_sampling_collaterals");
		sql.append(type);
		sql.append(" where samplingid = :id and collateralreference=:collateralreference");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("collateralreference", collateralreference);
		source.addValue("id", samplingId);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	public void update(Sampling sampling, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update sampling");
		query.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		query.append(
				" set tenure =:tenure, interestrate = :interestRate, loanEligibility = :loanEligibility, lcreligibility = :lcrEligibility, ltveligibility = :ltvEligibility,");
		query.append(" totalincome =:totalIncome, totalliability = :totalLiability,");
		query.append(" foireligibility =:foirEligibility, irreligibility = :irrEligibility, emi = :emi,");
		query.append(" totalCustomerIntObligation = :totalCustomerIntObligation,");
		query.append(" totalCoApplicantsIntObligation = :totalCoApplicantsIntObligation,");
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
	public List<SamplingCollateral> getCollateralTypesBySamplingId(Long samplingId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select cs.collateralref, cs.collateraltype, lsc.linkId from collateralsetup_view cs");
		sql.append(" inner join link_sampling_collaterals lsc on lsc.collateralReference = cs.collateralref");
		sql.append(" where lsc.samplingid = :samplingid");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingid", samplingId);
		RowMapper<SamplingCollateral> rowMapper = BeanPropertyRowMapper.newInstance(SamplingCollateral.class);

		return this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
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
		Sampling sampling = null;

		StringBuilder sql = new StringBuilder();
		sql.append("select * from sampling").append(type).append(" where id = :samplingid");

		RowMapper<Sampling> rowMapper = BeanPropertyRowMapper.newInstance(Sampling.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingid", samplingid);

		try {
			sampling = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Sampling not found in sampling{} table/view for the specified id >> {}", type, samplingid);
		}

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

		RowMapper<CustomerIncome> rowMapper = BeanPropertyRowMapper.newInstance(CustomerIncome.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);

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
		RowMapper<CustomerExtLiability> rowMapper = BeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);

	}

	public long getLinkId(Sampling sampling, String collRef, String inputSource) {
		logger.debug(Literal.ENTERING);

		String table = null;
		if ("income".equals(inputSource)) {
			table = "link_sampling_incomes";
		} else if ("obligation".equals(inputSource)) {
			table = "link_sampling_liabilities";
		} else {
			table = "link_sampling_collaterals";
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid),0) from ");
		sql.append(table);
		sql.append(" where samplingid = :id");
		if ("collaterals".equals(inputSource)) {
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

		StringBuilder sql = new StringBuilder();
		sql.append("select 1 custTypeCode, cu.custid, cu.custCif, cu.custshrtname, cu.phonenumber,");
		sql.append(" ca.custaddrtype custAddlVar1, 0 includeincome ");
		sql.append(" from sampling").append(type).append(" s");
		sql.append(" inner join financemain_view fm on fm.finreference = s.keyreference");
		sql.append(" inner join customers_view cu on cu.custid = fm.custid");
		sql.append(" inner join customeraddresses_view ca on ca.custid = cu.custid");
		sql.append(" where s.keyreference = :keyreference and ca.custaddrpriority = :custaddrpriority");
		sql.append(" union all");
		sql.append(" select 2 custTypeCode, cu.custid, cu.custCif, cu.custshrtname, cu.phonenumber,");
		sql.append(" ca.custaddrtype custAddlVar1, ja.includeincome ");
		sql.append(" from sampling").append(type).append(" s");
		sql.append(" inner join finjointaccountdetails_view ja on ja.finreference = s.keyreference");
		sql.append(" inner join customers_view cu on cu.custid = ja.custid");
		sql.append(" inner join customeraddresses_view ca on ca.custid = cu.custid");
		sql.append(" where s.keyreference = :keyreference and ca.custaddrpriority = :custaddrpriority");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", keyreference);
		source.addValue("custaddrpriority", 5);
		RowMapper<Customer> rowMapper = BeanPropertyRowMapper.newInstance(Customer.class);

		return jdbcTemplate.query(sql.toString(), source, rowMapper);
	}

	@Override
	public Sampling getSampling(String keyReference, String type) {
		Sampling sampling = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" select * from sampling").append(type);
		sql.append(" where keyreference = :keyreference");

		RowMapper<Sampling> rowMapper = BeanPropertyRowMapper.newInstance(Sampling.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyReference);

		try {
			sampling = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return sampling;
	}

	@Override
	public List<String> getCollateralTypes(String keyreference) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ca.collateraltype ");
		sql.append(" from collateralassignment_view ca");
		sql.append(" inner join collateralsetup_view cs on cs.collateralref = ca.collateralref");
		sql.append(" where ca.reference = :keyreference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", keyreference);
		try {
			return jdbcTemplate.queryForList(sql.toString(), source, String.class);
		} catch (DataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<SamplingCollateral> getCollaterals(String keyreference, String collateralType) {
		collateralType = collateralType.toLowerCase();
		StringBuilder sql = new StringBuilder();
		sql.append("select depositorcif, depositorname, ca.assignperc, cs.collateralref, cs.bankltv, tv.Seqno,");
		sql.append(" collateraltype, collateraltypename");
		sql.append(" from collateralassignment_view ca");
		sql.append(" inner join collateralsetup_view cs on cs.collateralref = ca.collateralref");
		sql.append(" inner join (");
		sql.append(" select reference, seqno from collateral_").append(collateralType).append("_ed");
		sql.append(" union ");
		sql.append(" select reference, seqno from collateral_").append(collateralType).append("_ed_temp");
		sql.append(" ) tv on tv.reference = cs.collateralref");

		sql.append(" where ca.reference = :keyreference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", keyreference);
		try {
			return jdbcTemplate.query(sql.toString(), source,
					BeanPropertyRowMapper.newInstance(SamplingCollateral.class));
		} catch (DataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Map<String, String> getEligibilityRules() {
		Map<String, String> rules = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select rulecode, sqlrule from rules where rulemodule=:rulemodule and rulecode in (:rulecode)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("rulemodule", "ELGRULE");
		source.addValue("rulecode", Arrays.asList(Sampling.RULE_CODE_FOIRAMT, Sampling.RULE_CODE_IIRMAX,
				Sampling.RULE_CODE_EMI, Sampling.RULE_CODE_LCRMAXEL, Sampling.RULE_CODE_LTVAMOUN));

		jdbcTemplate.query(sql.toString(), source, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				rules.put(rs.getString(1), rs.getString(2));
			}
		});

		return rules;
	}

	public ExtendedFieldRender getCollateralExtendedFields(String collReference, String tableName, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(tableName).append(type);
		sql.append(" where reference = :collReference");
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<ExtendedFieldRender> rowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldRender.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("collReference", collReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Map<String, Object> getExtendedField(String linkId, int seqNo, String tableName, String type) {
		logger.debug(Literal.ENTERING);

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
			sql.append(
					" where reference =T.reference)) T where T.reference = :linkid and seqno =:seqno order by seqno");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
			sql.append(" where  reference = :linkid  and seqno =:seqno order by seqno");
		}
		logger.trace(Literal.SQL + sql.toString());

		source.addValue("linkid", linkId);
		source.addValue("seqno", seqNo);
		try {
			return this.jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new HashMap<>();
		}
	}

	@Override
	public boolean isExist(String finReference, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from sampling");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where keyreference=:keyreference");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyreference", finReference);

		return jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		source.addValue("custid", custId);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public long getLiabilitySnapLinkId(long samplingId, long custId) {
		logger.debug(Literal.ENTERING);

		long linkId = getLiabilitySnapLink(samplingId, custId);

		if (linkId > 0) {
			return linkId;
		}

		linkId = getNextValue(ExternalLiabilityDAOImpl.SEQUENCE_LINK);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_liabilities_snap values(:samplingid, :custid, :linkid)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		source.addValue("custid", custId);
		source.addValue("linkid", linkId);
		this.jdbcTemplate.update(sql.toString(), source);

		return linkId;
	}

	private long getLiabilitySnapLink(long samplingId, long custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_sampling_liabilities_snap");
		sql.append(" where samplingid = :samplingid and custid=:custid");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		source.addValue("custid", custId);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public long getCollateralSnapLinkId(long samplingId, String collateralRef) {
		logger.debug(Literal.ENTERING);

		long linkId = getCollateralLinkId(collateralRef, samplingId, "_snap");

		if (linkId > 0) {
			return linkId;
		}

		linkId = getNextValue("seqcollaterallink");
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_sampling_collaterals_snap values(:samplingId, :collateralRef,:linkId)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingId", samplingId);
		source.addValue("collateralRef", collateralRef);
		source.addValue("linkId", linkId);

		this.jdbcTemplate.update(sql.toString(), source);
		return linkId;
	}

	@Override
	public BigDecimal getLoanEligibility(String finReference, String eligibilityRule) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(ruleresult, '0') from financeeligibilitydetail_view er");
		sql.append(" inner join rules r on r.ruleid = er.elgrulecode");
		sql.append(" where finreference = :finreference and rulecode = :rulecode");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finreference", finReference);
		source.addValue("rulecode", eligibilityRule);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
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

	public String getCollateralRef(Sampling sampling, String linkId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct collateralreference from link_sampling_collaterals");
		sql.append(" where samplingid = :id  and linkid = :linkId");

		String collReference = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", sampling.getId());
		source.addValue("linkId", Integer.parseInt(linkId));
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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public void saveIncomes(long samplingId) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into income_details_temp select * from income_details");
		sql.append(" where linkid in  (select linkid from link_sampling_incomes where samplingid = :samplingid)");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateIncomes(Sampling sampling) {
		StringBuilder sql = new StringBuilder();
		sql.append(" update income_details_temp");
		sql.append(" set lastmntby=:lastMntBy, lastmnton=:lastMntOn, recordStatus=:recordStatus, ");
		sql.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode, taskid=:taskId, nexttaskid=:nextTaskId,");
		sql.append(" recordtype=:recordType, workflowid=:workflowId");
		sql.append(" where linkid in  (select linkid from link_sampling_incomes where samplingid = :id)");

		jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(sampling));
	}

	@Override
	public void updateCollaterals(Sampling sampling, String collateralType) {
		StringBuilder sql = new StringBuilder();

		sql.append("update verification_");
		sql.append(StringUtils.trimToEmpty(collateralType));
		sql.append("_tv_temp");
		sql.append(" set lastmntby=:lastMntBy, lastmnton=:lastMntOn, recordStatus=:recordStatus, ");
		sql.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode, taskid=:taskId, nexttaskid=:nextTaskId,");
		sql.append(" recordtype=:recordType, workflowid=:workflowId");
		sql.append(" where reference in  (:reference)");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("lastMntBy", sampling.getLastMntBy());
		source.addValue("lastMntOn", sampling.getLastMntOn());
		source.addValue("recordStatus", sampling.getRecordStatus());
		source.addValue("roleCode", sampling.getRoleCode());
		source.addValue("nextRoleCode", sampling.getNextRoleCode());
		source.addValue("taskId", sampling.getTaskId());
		source.addValue("nextTaskId", sampling.getNextTaskId());
		source.addValue("nextTaskId", sampling.getNextTaskId());
		source.addValue("recordType", sampling.getRecordType());
		source.addValue("workflowId", sampling.getWorkflowId());
		source.addValue("reference", getCollateralLinkIds(sampling.getId()));

		jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public void saveLiabilities(long samplingId) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into external_liabilities_temp select * from external_liabilities");
		sql.append(" where linkid in  (select linkid from link_sampling_liabilities where samplingid = :samplingid)");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("samplingid", samplingId);
		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateLiabilities(Sampling sampling) {
		StringBuilder sql = new StringBuilder();
		sql.append(" update external_liabilities_temp");
		sql.append(" set lastmntby=:lastMntBy, lastmnton=:lastMntOn, recordStatus=:recordStatus, ");
		sql.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode, taskid=:taskId, nexttaskid=:nextTaskId,");
		sql.append(" recordtype=:recordType, workflowid=:workflowId");
		sql.append(" where linkid in  (select linkid from link_sampling_liabilities where samplingid = :id)");

		jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(sampling));
	}

	@Override
	public void saveCollateral(long samplingId, String collateralType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		List<String> linkIds = getCollateralLinkIds(samplingId);

		StringBuilder sql = new StringBuilder("insert into verification_");
		sql.append(StringUtils.trimToEmpty(collateralType));
		sql.append("_tv_temp");
		sql.append(" select * from verification_");
		sql.append(StringUtils.trimToEmpty(collateralType));
		sql.append("_tv  Where reference in(:linkIds)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("linkIds", linkIds);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<String> getCollateralLinkIds(long samplingId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();

		sql.append("select linkId from link_sampling_collaterals  where samplingId=:samplingId");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingId", samplingId);

		List<String> list = jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);

		List<String> mainList = new ArrayList<>();
		for (String linkId : list) {
			mainList.add("S".concat(linkId));
		}

		return mainList;
	}

	@Override
	public List<SamplingCollateral> getCollateralsBySamplingId(List<String> linkIds, String collateralType) {
		collateralType = collateralType.toLowerCase();
		StringBuilder sql = new StringBuilder();
		sql.append("select reference collateralref, Seqno, :collateralType collateralType");
		sql.append(" from  verification_").append(collateralType).append("_tv");
		sql.append(" where reference in(:reference)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", linkIds);
		source.addValue("collateralType", collateralType.toUpperCase());
		BeanPropertyRowMapper.newInstance(SamplingCollateral.class);
		try {
			return jdbcTemplate.query(sql.toString(), source,
					BeanPropertyRowMapper.newInstance(SamplingCollateral.class));
		} catch (DataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public int getNextLiabilitSeq(long linkId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(seqno), 0)+1 from ");
		sql.append("external_liabilities_view ");
		sql.append("where linkId=:linkId");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("linkId", linkId);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 1;
		}
	}

	@Override
	public List<CustomerIncome> getIncomesByCustId(long samplingId, long custId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from sampling_income_details");
		sql.append(type);
		sql.append(" where linkid in(");
		sql.append(" (select coalesce(linkid, 0) from link_sampling_incomes");
		sql.append(" where samplingid = :samplingid and custid = :custId))");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("samplingid", samplingId);
		paramSource.addValue("custId", custId);

		RowMapper<CustomerIncome> rowMapper = BeanPropertyRowMapper.newInstance(CustomerIncome.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}
}
