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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
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
		if (StringUtils.equals(inputSource, "sampling")) {
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
			sql.append(" overdueamount, finstatus, custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, loanpurposedesc,");
			sql.append(" repaybank, repayfrombankname,");
			sql.append(" version, lastMntOn, lastMntBy, recordStatus, roleCode, nextRoleCode,");
			sql.append(" taskId, nextTaskId, recordType, workflowId");
			sql.append(" from ");
			sql.append(view);
			sql.append(" where linkid =:linkId and custId = :custId and seqNo = :seqNo");
		} else {
			sql.append(" select id, el.linkId, seqno, cel.custid, cu.custcif, cu.custshrtname,");
			sql.append(" el.fintype, ft.fintypedesc, findate, loanbank, lb.bankname loanbankname,");
			sql.append(" rateofinterest, tenure, originalamount, instalmentamount,");
			sql.append(" outstandingbalance, balancetenure, bounceinstalments, principaloutstanding,");
			sql.append(" overdueamount, finstatus, cs.custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, lp.loanpurposedesc,");
			sql.append(" repaybank, rb.bankname repayfrombankname,");
			sql.append(" el.version, el.lastMntOn, el.lastMntBy, el.recordStatus, el.roleCode, el.nextRoleCode,");
			sql.append(" el.taskId, el.nextTaskId, el.recordType, el.workflowId");
			sql.append(" from external_liabilities");
			sql.append(type).append(" el");
			sql.append(" inner join ").append(table).append(" cel on cel.linkid = el.linkid");
			sql.append(" inner join (");
			sql.append(" select custid, custcif, custshrtname from customers_temp");
			sql.append(" union");
			sql.append(" select custid,custcif,custshrtname from customers cu");
			sql.append(" where not exists (select 1 from customers_temp where custid=cu.custid)");
			sql.append(" ) cu on cu.custid = cel.custid");
			sql.append(" inner join bmtbankdetail lb on lb.bankcode = el.loanbank");
			sql.append(" inner join otherbankfinancetype ft on ft.fintype = el.fintype");
			sql.append(" left join loanpurposes lp on lp.loanpurposecode = el.loanpurpose");
			sql.append(" left join bmtbankdetail rb on rb.bankcode = el.repaybank");
			sql.append(" left join bmtcuststatuscodes cs on cs.custstscode = el.finstatus");
			sql.append(" where el.linkid =:linkId and cel.custId = :custId and seqNo = :seqNo");
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
		sql.append("select count(*) from bmtcuststatuscodes where custstscode= :finStatus");
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

	@Override
	public long getLinkId(long custId) {
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
		query.append("select * from customer_ext_liabilities_view cl");
		query.append(" inner join");
		query.append(" (select custid, finreference from financemain_view");
		query.append(" union all");
		query.append(" select jointaccountid custid, finreference from finjointaccountdetails_view) fm");
		query.append(" on fm.custid=cl.custid where fm.finreference = :finreference");

		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("finreference", finReference);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerExtLiability.class);

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
		query.append("select * from external_liabilities_view cl");
		query.append(" inner join link_sampling_liabilities sl on sl.linkid = cl.linkid");
		query.append(" where sl.samplingid = :samplingid");

		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("samplingid", samplingId);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerExtLiability.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public void save(List<ExtLiabilityPaymentdetails> installmentDetails, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into EXTERNAL_LIABILITIES_PD");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (LiabilityId, EmiType, InstallmentCleared, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(" , RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:LiabilityId, :EMIType, :InstallmentCleared, :Version, :LastMntBy");
		sql.append(" , :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType");
		sql.append(" , :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(installmentDetails.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), params);
	}

	@Override
	public void delete(List<ExtLiabilityPaymentdetails> installmentDetails, String type) {
		StringBuilder sql = new StringBuilder(" Delete From EXTERNAL_LIABILITIES_PD");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LiabilityId =:LiabilityId ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(installmentDetails.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), params);
	}

	@Override
	public List<ExtLiabilityPaymentdetails> getExtLiabilitySubDetailById(long custId, String type) {
		logger.debug("Entering");
		ExtLiabilityPaymentdetails extLiabilitiesPaymentdetails = new ExtLiabilityPaymentdetails();
		extLiabilitiesPaymentdetails.setLiabilityId(custId);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Id, LiabilityId, EmiType, InstallmentCleared, Version, LastMntOn, LastMntBy");
		sql.append(" , RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  EXTERNAL_LIABILITIES_PD");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LiabilityId = :LiabilityId ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extLiabilitiesPaymentdetails);
		RowMapper<ExtLiabilityPaymentdetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExtLiabilityPaymentdetails.class);

		List<ExtLiabilityPaymentdetails> liabilitiesPaymentdetails = this.jdbcTemplate.query(sql.toString(),
				beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return liabilitiesPaymentdetails;
	}

	@Override
	public void update(ExtLiabilityPaymentdetails installmentDetails, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append(" update EXTERNAL_LIABILITIES_PD");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set EMIType=:EMIType, installmentCleared=:installmentCleared,");
		sql.append(" version=:version, lastmntby=:lastMntBy,lastmnton=:lastMntOn, recordstatus=:recordStatus,");
		sql.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode,");
		sql.append(" taskid=:taskId, nexttaskid=:nextTaskId, recordtype=:recordType, workflowid=:workflowId");
		sql.append(" where liabilityId = :liabilityId ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(installmentDetails);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public int getExtLiabilityVersion(long linkId, int liabilitySeq) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select version from external_liabilities ");
		sql.append(" where linkId = :linkId and seqNo = :seqNo");
		logger.trace(Literal.SQL + sql.toString());

		int recordCount = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("linkId", linkId);
		mapSqlParameterSource.addValue("seqNo", liabilitySeq);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);
		return recordCount;

	
	}

}
