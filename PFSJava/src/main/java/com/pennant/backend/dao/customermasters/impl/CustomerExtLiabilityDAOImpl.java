package com.pennant.backend.dao.customermasters.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAOImpl;

public class CustomerExtLiabilityDAOImpl extends SequenceDao<CustomerExtLiability> implements CustomerExtLiabilityDAO {
	private static Logger logger = LogManager.getLogger(CustomerExtLiabilityDAOImpl.class);

	@Override
	public CustomerExtLiability getLiability(CustomerExtLiability liability, String type, String inputSource) {
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
			sql.append(" select id, linkId, seqno, custid, custcif, custshrtname,");
			sql.append(" fintype, fintypedesc, findate, loanbank, loanbankname,");
			sql.append(" rateofinterest, tenure, originalamount, instalmentamount,");
			sql.append(" outstandingbalance, balancetenure, bounceinstalments, principaloutstanding,");
			sql.append(" overdueamount, finstatus, custstsdescription,");
			sql.append(" foir, source, checkedby, securitydetails, loanpurpose, loanpurposedesc,");
			sql.append(" repaybank, repayfrombankname, imputedEmi, ownerShip, lastTwentyFourMonths,");
			sql.append(" lastSixMonths, lastThreeMonths, currentOverDue,");
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
			sql.append(" repaybank, rb.bankname repayfrombankname,imputedEmi, ownerShip, lastTwentyFourMonths,");
			sql.append(" lastSixMonths, lastThreeMonths, currentOverDue,");
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
		RowMapper<CustomerExtLiability> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in {} for the specified LinkId >> {} and CustId >> {} and SeqNo >> {}", view,
					liability.getLinkId(), liability.getCustId(), liability.getSeqNo());
		}
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

		return recordCount > 0;

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

		return recordCount > 0;

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

		return recordCount > 0;

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
	public int getVersion(long linkId, int liabilitySeq) {
		int recordCount = 0;
		String sql = "Select Version From External_Liabilities where LinkId = ? and seqno = ? ";

		logger.debug(Literal.SQL + sql.toString());

		try {
			recordCount = this.jdbcOperations.queryForObject(sql.toString(), Integer.class, linkId, liabilitySeq);
		} catch (EmptyResultDataAccessException dae) {
			recordCount = 0;
		}

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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custids);
		source.addValue("foir", 1);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);

		return jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
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
		RowMapper<CustomerExtLiability> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

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
		RowMapper<CustomerExtLiability> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerExtLiability.class);

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
		sql.append(" (LiabilityId, EmiType, EmiClearance, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(" , RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, EmiClearedDay)");
		sql.append(" Values(:LiabilityId, :EmiType, :EmiClearance, :Version, :LastMntBy");
		sql.append(" , :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType");
		sql.append(" , :WorkflowId, :EmiClearedDay)");

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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, LiabilityId, EmiType, EmiClearance, Version, LastMntOn, LastMntBy");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, EmiClearedDay");
		sql.append(" from EXTERNAL_LIABILITIES_PD_VIEW");
		sql.append(" Where LiabilityId = ?");

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index, custId);
			}
		}, new RowMapper<ExtLiabilityPaymentdetails>() {
			@Override
			public ExtLiabilityPaymentdetails mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExtLiabilityPaymentdetails epd = new ExtLiabilityPaymentdetails();

				epd.setId(rs.getLong("Id"));
				epd.setLiabilityId(rs.getLong("LiabilityId"));
				epd.setEmiType(rs.getString("EmiType"));
				epd.setEmiClearance(rs.getString("EmiClearance"));
				epd.setVersion(rs.getInt("Version"));
				epd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				epd.setLastMntBy(rs.getLong("LastMntBy"));
				epd.setRecordStatus(rs.getString("RecordStatus"));
				epd.setRoleCode(rs.getString("RoleCode"));
				epd.setNextRoleCode(rs.getString("NextRoleCode"));
				epd.setTaskId(rs.getString("TaskId"));
				epd.setNextTaskId(rs.getString("NextTaskId"));
				epd.setRecordType(rs.getString("RecordType"));
				epd.setWorkflowId(rs.getLong("WorkflowId"));
				epd.setEmiClearedDay(rs.getInt("EmiClearedDay"));
				return epd;
			}
		});
	}

	@Override
	public void update(ExtLiabilityPaymentdetails installmentDetails, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append(" update EXTERNAL_LIABILITIES_PD");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set EmiType = :EmiType, EmiClearance = :EmiClearance, EmiClearedDay = :EmiClearedDay");
		sql.append(
				", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(", Rolecode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, Workflowid = :WorkflowId");
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

	@Override
	public BigDecimal getSumAmtCustomerInternalExtLiabilityById(Set<Long> custids) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(custids)) {
			return BigDecimal.ZERO;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("  select coalesce(sum(NSchdPri+NSchdPft), 0) ");
		sql.append(" from FinPftdetails Where custid in (:custid) ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custids);
		source.addValue("foir", 1);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
	}

	@Override
	public BigDecimal getSumCredtAmtCustomerBankInfoById(Set<Long> custId) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(custId)) {
			return BigDecimal.ZERO;
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(CreditAmt),0) as CreditAmt  ");
		sql.append(" FROM  BankInfoDetail ");
		sql.append(" where bankid ");
		sql.append(" in (select bankid from CustomerBankInfo ");
		sql.append(" Where custid in (:custId)) ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custId", custId);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
	}

	@Override
	public void delete(long linkId, String type) {
		StringBuilder sql = new StringBuilder(" Delete From EXTERNAL_LIABILITIES_PD");
		sql.append(type);
		sql.append(" Where LiabilityId IN (select ID from EXTERNAL_LIABILITIES");
		sql.append(type);
		sql.append(" Where linkId =:linkId)");
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("linkId", linkId);
		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public boolean getExtendedComboData(String sql, String code) {
		logger.trace(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, code, 1) > 0;
	}
}
