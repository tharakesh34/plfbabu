package com.pennant.backend.dao.customermasters.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAOImpl;

public class CustomerExtLiabilityDAOImpl extends SequenceDao<CustomerExtLiability> implements CustomerExtLiabilityDAO {
	private static Logger logger = Logger.getLogger(CustomerExtLiabilityDAOImpl.class);

	
	@Override
	public CustomerExtLiability getLiability(CustomerExtLiability liability, String type, String inputSource) {
		logger.debug(Literal.ENTERING);

		type = StringUtils.trimToEmpty(type).toLowerCase();

		String view = null;
		String table = null;
		if (inputSource.equals("sampling")) {
			view = "sampling_liabilities";
			table = "link_sampling_liabilities";
		} else {
			view = "customer_ext_liabilities";
			table = "link_cust_liabilities";
		}

		if (type.equals("_temp")) {
			view = view.concat("_view");
		} else {
			view = view.concat("_aview");
		}

		StringBuilder sql = new StringBuilder();
		if (type.contains("view")) {
			sql.append(" select id, linkId, seqno, custid, cu.custcif, cu.custshrtname,");
			sql.append(" fintype, el.fintypedesc, findate, loanbank, loanbankname,");
			sql.append(" rateofinterest, tenure, originalamount, instalmentamount,");
			sql.append(" outstandingbalance, balancetenure, bounceinstalments, principaloutstanding,");
			sql.append(" overdueamount, finstatus, el.custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, el.loanpurposedesc,");
			sql.append(" repaybank, repayfrombankname,");
			sql.append(" version, lastMntOn, lastMntBy, recordStatus, roleCode, nextRoleCode,");
			sql.append(" taskId, nextTaskId, recordType, workflowId");
			sql.append(" from ");
			sql.append(view);
			sql.append(" where linkid =:linkId and custId = :custId and seqno = :seqno");
		} else {
			sql.append(" select id, el.linkId, seqno, cel.custid, cu.custcif, cu.custshrtname,");
			sql.append(" el.fintype, el.fintypedesc, findate, loanbank, lb.bankname loanbankname,");
			sql.append(" rateofinterest, tenure, originalamount, instalmentamount,");
			sql.append(" outstandingbalance, balancetenure, bounceinstalments, principaloutstanding,");
			sql.append(" overdueamount, finstatus, el.custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, el.loanpurposedesc,");
			sql.append(" repaybank, rb.bankname repayfrombankname,");
			sql.append(" el.version, el.lastMntOn, el.lastMntBy, el.recordStatus, el.roleCode, el.nextRoleCode,");
			sql.append(" el.taskId, el.nextTaskId, el.recordType, el.workflowId");
			sql.append(" from external_liabilities");
			sql.append(type).append(" el");
			sql.append(" inner join ").append(table).append(" cel on cel.linkid = el.linkid");
			sql.append(" inner join customers cu on cu.custid = cel.custid");
			sql.append(" inner join bmtbankdetail lb on lb.bankcode = el.loanbank");
			sql.append(" inner join otherbankfinancetype ft on ft.fintype = el.fintype");
			sql.append(" inner join loanpurposes lp on lp.loanpurposecode = el.loanpurpose");
			sql.append(" inner join bmtbankdetail rb on rb.bankcode = el.repaybank");
			sql.append(" left join bmtcuststatuscodes cs on cs.custstscode = el.finstatus");
			sql.append(" where el.linkid =:linkId and cel.custId = :custId and seqno = :seqNo");
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
	public boolean isBankExists(String loanBank) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from bmtbankdetail where bankcode= :bankcode");
		logger.trace(Literal.SQL + sql.toString());

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("bankcode", loanBank);
		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);

		return recordCount > 0 ? true : false;

	}

	@Override
	public boolean isFinTypeExists(String finType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from otherbankfinancetype where finType= :finType");
		logger.trace(Literal.SQL + sql.toString());

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finType", finType);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);

		return recordCount > 0 ? true : false;

	}

	@Override
	public boolean isFinStatuExists(String finStatus) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from bmtcuststatuscodes where finStatus= :finStatus");
		logger.trace(Literal.SQL + sql.toString());

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finStatus", finStatus);
		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);

		return recordCount > 0 ? true : false;

	}


	@Override
	public int getCustomerExtLiabilityByBank(String loanBank, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from external_liabilities");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where loanBank =:loanBank");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("loanBank", loanBank);

		return this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Integer.class);
	}


	@Override
	public int getVersion(long custId, int liabilitySeq) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select version from customer_ext_liabilities cel");
		sql.append(" inner join external_liabilities el on el.linkid = cel.linkid");
		sql.append(" where custid = :custid and seqno = :seqno");
		logger.trace(Literal.SQL + sql.toString());

		int recordCount = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("custid", custId);
		mapSqlParameterSource.addValue("seqno", liabilitySeq);
		
		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);
		return recordCount;

	}

	@Override
	public BigDecimal getExternalLiabilitySum(long custId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(instalmentamount), 0)");
		sql.append(" from external_liabilities el");
		sql.append(" inner join external_liabilities el on el.linkid = cel.linkid");
		sql.append(" Where custid = :custid and foir = :foir");
		logger.trace(Literal.SQL + sql.toString());

		BigDecimal emiSum = BigDecimal.ZERO;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("custid", custId);
		parameterSource.addValue("foir", 1);

		try {
			emiSum = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			return emiSum;
		}
		
		logger.debug(Literal.LEAVING);
		return emiSum;
	}

	@Override
	public BigDecimal getSumAmtCustomerExtLiabilityById(Set<Long> custids) {
		logger.debug(Literal.ENTERING);
		
		if (CollectionUtils.isEmpty(custids)) {
			return BigDecimal.ZERO;
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(instalmentamount), 0)");
		sql.append(" from customer_ext_liabilities_aview");
		sql.append(" Where custid in (:custid) and foir = :foir");

		logger.trace(Literal.SQL + sql.toString());
		BigDecimal emiSum = BigDecimal.ZERO;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custids);
		source.addValue("foir", 1);
		
		try {
			emiSum = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			return emiSum;
		}
		logger.debug(Literal.LEAVING);
		return emiSum;
	}

	@Override
	public void setLinkId(CustomerExtLiability liability) {
		logger.debug(Literal.ENTERING);

		long linkId = liability.getLinkId();

		if (linkId > 0) {
			return;
		}

		linkId = getLinkId(liability.getCustId());

		if (linkId > 0) {
			liability.setLinkId(linkId);
			return;
		}

		linkId = getNextValue(ExternalLiabilityDAOImpl.SEQUENCE_LINK);
		liability.setLinkId(linkId);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_cust_liabilities values(:custid, :linkid)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", liability.getCustId());
		source.addValue("linkid", linkId);

		this.jdbcTemplate.update(sql.toString(), source);
	}
	
	private long getLinkId(long custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_cust_liabilities where custid=:custid");

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
	public List<CustomerExtLiability> getLiabilityByFinReference(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select cl.* from customer_ext_liabilities_view cl");
		query.append(" inner join");
		query.append(" select custid, finreference from financemain_view");
		query.append(" union all");
		query.append(" select jointaccountid custid, finreference from finjointaccountdetails_view) fm");
		query.append(" on fm.custid=cl.custid where fm.finreference = :finreference");

		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("finReference", finReference);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<CustomerExtLiability> getLiabilityBySamplingId(long samplingId) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select ci.* from customer_ext_liabilities_view cl");
		query.append(" inner join link_sampling_liabilities sl on sl.linkid = cl.linkid");
		query.append( "where sl.samplingid = :samplingid");

		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("samplingid", samplingId);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
}
