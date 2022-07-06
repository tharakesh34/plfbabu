package com.pennanttech.pff.dao.customer.liability;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExternalLiabilityDAOImpl extends SequenceDao<CustomerExtLiability> implements ExternalLiabilityDAO {
	private static Logger logger = LogManager.getLogger(ExternalLiabilityDAOImpl.class);

	public static final String SEQUENCE = "SeqExternalLiabilities";
	public static final String SEQUENCE_LINK = "SeqExternalLiabilitiLink";

	@Override
	public long save(CustomerExtLiability custExtLiability, String type) {

		if (custExtLiability.getId() == 0) {
			custExtLiability.setId(getNextValue(SEQUENCE));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into external_liabilities");
		sql.append(type);
		sql.append(" (id, linkid, seqno, fintype, findate, loanbank, rateofinterest,");
		sql.append(" tenure, originalamount, instalmentamount, outstandingbalance, balancetenure, bounceinstalments,");
		sql.append(
				" principaloutstanding, overdueamount, finstatus, foir, source, checkedby, securitydetails, loanpurpose, repaybank, otherFinInstitute,");
		sql.append(" imputedEmi, ownerShip, lastTwentyFourMonths, lastSixMonths, lastThreeMonths, currentOverDue,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid,");
		sql.append(
				" repayFromAccNo, remarks, noOfBouncesInSixMonths, noOfBouncesInTwelveMonths, consideredBasedOnRTR, mob)");
		sql.append(" values(:Id, :LinkId, :SeqNo, :FinType, :finDate, :loanBank, :rateOfInterest,");
		sql.append(
				" :tenure, :originalAmount, :instalmentAmount, :outstandingBalance, :balanceTenure, :bounceInstalments,");
		sql.append(
				" :principalOutstanding, :overdueAmount, :finStatus, :foir, :source, :checkedBy, :securityDetails, :loanPurpose, :repayBank, :otherFinInstitute,");
		sql.append(
				" :imputedEmi, :ownerShip, :lastTwentyFourMonths, :lastSixMonths, :lastThreeMonths, :currentOverDue,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode,");
		sql.append(" :taskId, :nextTaskId, :recordType, :workflowId,");
		sql.append(
				" :repayFromAccNo, :remarks, :noOfBouncesInSixMonths, :noOfBouncesInTwelveMonths, :consideredBasedOnRTR, :mob");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custExtLiability);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return custExtLiability.getId();
	}

	@Override
	public void update(CustomerExtLiability custExtLiability, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append(" update external_liabilities");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set fintype=:finType, findate=:finDate, loanbank=:loanBank,");
		sql.append(" rateofinterest=:rateOfInterest, tenure=:tenure, originalamount=:originalAmount,");
		sql.append(" instalmentamount=:instalmentAmount, outstandingbalance=:outstandingBalance,");
		sql.append(" balancetenure=:balanceTenure, bounceinstalments=:bounceInstalments,");
		sql.append(" principaloutstanding=:principalOutstanding, overdueamount=:overdueAmount, finstatus=:finStatus,");
		sql.append(" foir=:foir, source=:source, checkedby=:checkedBy, securitydetails=:securityDetails,");
		sql.append(" loanpurpose=:loanPurpose, repaybank=:repayBank, repayFromAccNo=:repayFromAccNo,");
		sql.append(" imputedEmi=:imputedEmi, ownerShip=:ownerShip, lastTwentyFourMonths=:lastTwentyFourMonths,");
		sql.append(" otherFinInstitute=:otherFinInstitute,lastSixMonths=:lastSixMonths,");
		sql.append(" lastThreeMonths=:lastThreeMonths,currentOverDue=:currentOverDue,remarks=:remarks,");
		sql.append(" noOfBouncesInSixMonths=:noOfBouncesInSixMonths");
		sql.append(",noOfBouncesInTwelveMonths=:noOfBouncesInTwelveMonths,");
		sql.append(" consideredBasedOnRTR=:consideredBasedOnRTR, mob=:mob,");
		sql.append("version=:version, lastmntby=:lastMntBy,lastmnton=:lastMntOn, recordstatus=:recordStatus,");
		sql.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode,");
		sql.append(" taskid=:taskId, nexttaskid=:nextTaskId, recordtype=:recordType, workflowid=:workflowId");
		sql.append(" where id = :Id ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custExtLiability);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append(" delete from external_liabilities");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where Id = :id ");
		logger.trace(Literal.SQL + query.toString());

		int recordCount = 0;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("id", id);

		try {
			recordCount = this.jdbcTemplate.update(query.toString(), parameterSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByLinkId(Long linkId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" delete from external_liabilities");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where linkId =:linkid");
		logger.trace(Literal.ENTERING + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("linkid", linkId);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<CustomerExtLiability> getLiabilities(long custId, String type) {

		if (StringUtils.equalsIgnoreCase(type, "_temp")) {
			type = "_view";
		} else if (StringUtils.equalsIgnoreCase(type, "")) {
			type = "_aview";
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from customer_ext_liabilities").append(type);
		sql.append(" where custid = :custid");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		RowMapper<CustomerExtLiability> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public BigDecimal getTotalLiabilityByLinkId(Long linkid) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(instalmentamount), 0)");
		sql.append(" from external_liabilities");
		sql.append(" where linkid = :linkid ");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("linkid", linkid);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
	}

	@Override
	public BigDecimal getTotalLiabilityBySamplingId(long samplingId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(instalmentamount), 0) from external_liabilities");
		sql.append(" where linkid in  (select linkid from link_sampling_liabilities where samplingid = :id)");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", samplingId);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
	}

	@Override
	public BigDecimal getTotalLiabilityByFinReference(String keyReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(sum(instalmentamount), 0) from external_liabilities el");
		sql.append(" inner join link_cust_liabilities cl on cl.linkid = el.linkid");
		sql.append(" where custid in (select custid from (");
		sql.append(" select custid, finreference from financemain_view");
		sql.append(" union all");
		sql.append(" select custid, finreference from finjointaccountdetails_view");
		sql.append(") t where t.finreference = :keyReference)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("keyReference", keyReference);

		return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, BigDecimal.class);
	}

	@Override
	public List<CustomerExtLiability> getLiabilities(long linkId) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select * from external_liabilities_view where linkid = :linkid");
		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("linkid", linkId);
		RowMapper<CustomerExtLiability> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
}
