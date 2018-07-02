package com.pennanttech.pff.dao.customer.liability;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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
		sql.append(" principaloutstanding, overdueamount, finstatus, foir, source, checkedby, securitydetails, loanpurpose, repaybank,otherName,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		
		sql.append(" values(:Id, :LinkId, :SeqNo, :FinType, :finDate, :loanBank, :rateOfInterest,");
		sql.append(" :tenure, :originalAmount, :instalmentAmount, :outstandingBalance, :balanceTenure, :bounceInstalments,");
		sql.append(" :principalOutstanding, :overdueAmount, :finStatus, :foir, :source, :checkedBy, :securityDetails, :loanPurpose, :repayBank,:otherName ,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode,");
		sql.append(" :taskId, :nextTaskId, :recordType, :workflowId");
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
		sql.append(" loanpurpose=:loanPurpose, repaybank=:repayBank, otherName=:otherName,");
		sql.append(" version=:version, lastmntby=:lastMntBy,lastmnton=:lastMntOn, recordstatus=:recordStatus,");
		sql.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode,");
		sql.append(" taskid=:taskId, nexttaskid=:nextTaskId, recordtype=:recordType, workflowid=:workflowId");
		sql.append(" where id = :Id ");
		if (!type.endsWith("_Temp")) {
			sql.append("and Version= :Version-1");
		}

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
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public CustomerExtLiability getLiability(CustomerExtLiability liability, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		if (type.contains("view")) {
			sql.append(" select id, linkId, seqno, custid, custcif, custshrtname,");
			sql.append(" fintype, fintypedesc, findate, loanbank, loanbankname,");
			sql.append(" rateofinterest, tenure, originalamount, instalmentamount,");
			sql.append(" outstandingbalance, balancetenure, bounceinstalments, principaloutstanding,");
			sql.append(" overdueamount, finstatus, custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, loanpurposedesc,");
			sql.append(" repaybank, repayfrombankname,");
			sql.append(" version, lastMntOn, lastMntBy, recordStatus, roleCode, nextRoleCode,");
			sql.append(" taskId, nextTaskId, recordType, workflowId");
			sql.append(" from");
			sql.append(" customer_ext_liabilities").append(type);
			sql.append(" where custId = :custId and seqno = :seqno");
		} else {
			sql.append(" select id, el.linkId, seqno, cel.custid, custcif, custshrtname,");
			sql.append(" el.fintype, fintypedesc, findate, loanbank, lb.bankname loanbankname,el.otherName,");
			sql.append(" rateofinterest, tenure, originalamount, instalmentamount,");
			sql.append(" outstandingbalance, balancetenure, bounceinstalments, principaloutstanding,");
			sql.append(" overdueamount, finstatus, custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, loanpurposedesc,");
			sql.append(" repaybank, rb.bankname repayfrombankname,");
			sql.append(" el.version, el.lastMntOn, el.lastMntBy, el.recordStatus, el.roleCode, el.nextRoleCode,");
			sql.append(" el.taskId, el.nextTaskId, el.recordType, el.workflowId");
			sql.append(" from");
			sql.append(" external_liabilities").append(type).append(" el");
			sql.append(" inner join customer_ext_liabilities").append(type).append(" cel on cel.linkid = el.linkid");

			sql.append(" inner join customers cu on cu.custid = cel.custid");
			sql.append(" inner join bmtbankdetail lb on lb.bankcode = el.loanbank");
			sql.append(" inner join otherbankfinancetype ft on ft.fintype = el.fintype");
			sql.append(" inner join loanpurposes lp on lp.loanpurposecode = el.loanpurpose");
			sql.append(" inner join bmtbankdetail rb on rb.bankcode = el.repaybank");
			sql.append(" left join bmtcuststatuscodes cs on cs.custstscode = el.finstatus");
			sql.append(" where cel.custId = :custId and seqno = :seqNo");
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(liability);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerExtLiability.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
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
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerExtLiability.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public BigDecimal getTotalLiabilityByLinkId(Long linkid) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(instalmentamount), 0)");
		sql.append(" from external_liabilities");
		sql.append(" where linkid = :linkid ");
		logger.debug(Literal.SQL + sql.toString());

		BigDecimal emiSum = BigDecimal.ZERO;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("linkid", linkid);
		
		try {
			emiSum = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return emiSum;
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
		sql.append(" select jointaccountid custid,finreference from finjointaccountdetails_view");
		sql.append(") t where t.finreference = :keyReference)");
		
		logger.debug(Literal.SQL + sql.toString());
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("keyReference", keyReference);
		BigDecimal totalIncome = BigDecimal.ZERO;
		try {
			totalIncome = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, BigDecimal.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
			totalIncome = BigDecimal.ZERO;
		}

		logger.debug(Literal.LEAVING);
		return totalIncome;
	
	}

	@Override
	public List<CustomerExtLiability> getLiabilities(long linkId) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select * from external_liabilities_view where linkid = :linkid");
		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("linkid", linkId);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
}
