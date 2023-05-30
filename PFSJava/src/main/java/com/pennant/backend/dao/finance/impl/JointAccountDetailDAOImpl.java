/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : JointAccountDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>JointAccountDetail model</b> class.<br>
 * 
 */
public class JointAccountDetailDAOImpl extends SequenceDao<JointAccountDetail> implements JointAccountDetailDAO {
	private static Logger logger = LogManager.getLogger(JointAccountDetailDAOImpl.class);

	public JointAccountDetailDAOImpl() {
		super();
	}

	@Override
	public JointAccountDetail getJointAccountDetail() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("JointAccountDetail");

		JointAccountDetail jointAccountDetail = new JointAccountDetail();

		if (workFlowDetails != null) {
			jointAccountDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return jointAccountDetail;
	}

	@Override
	public JointAccountDetail getNewJointAccountDetail() {
		JointAccountDetail jointAccountDetail = getJointAccountDetail();
		jointAccountDetail.setNewRecord(true);

		return jointAccountDetail;
	}

	@Override
	public JointAccountDetail getJointAccountDetailById(long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where JointAccountId = ?");

		logger.debug(Literal.SQL + sql.toString());

		JointAccountDetailRowMapper rowMapper = new JointAccountDetailRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(JointAccountDetail jad, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinJointAccountDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where JointAccountId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, jad.getJointAccountId()));

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(JointAccountDetail jad, String type) {
		if (jad.getId() == Long.MIN_VALUE) {
			jad.setId(getNextValue("SeqFinJointAccountDetails"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinJointAccountDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (JointAccountId, FinID, FinReference, CustCIF, IncludeRepay, RepayAccountId");
		sql.append(", CatOfcoApplicant, AuthoritySignatory, Sequence, IncludeIncome");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, jad.getJointAccountId());
			ps.setLong(index++, jad.getFinID());
			ps.setString(index++, jad.getFinReference());
			ps.setString(index++, jad.getCustCIF());
			ps.setBoolean(index++, jad.isIncludeRepay());
			ps.setString(index++, jad.getRepayAccountId());
			ps.setString(index++, jad.getCatOfcoApplicant());
			ps.setBoolean(index++, jad.isAuthoritySignatory());
			ps.setInt(index++, jad.getSequence());
			ps.setBoolean(index++, jad.isIncludeIncome());
			ps.setInt(index++, jad.getVersion());
			ps.setLong(index++, jad.getLastMntBy());
			ps.setTimestamp(index++, jad.getLastMntOn());
			ps.setString(index++, jad.getRecordStatus());
			ps.setString(index++, jad.getRoleCode());
			ps.setString(index++, jad.getNextRoleCode());
			ps.setString(index++, jad.getTaskId());
			ps.setString(index++, jad.getNextTaskId());
			ps.setString(index++, jad.getRecordType());
			ps.setLong(index, jad.getWorkflowId());
		});

		return jad.getId();
	}

	@Override
	public void update(JointAccountDetail jad, String type) {
		StringBuilder sql = new StringBuilder("Update FinJointAccountDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinID = ?, FinReference = ?, CustCIF = ?, IncludeRepay = ?, RepayAccountId = ?");
		sql.append(", CatOfcoApplicant = ?, AuthoritySignatory = ?, Sequence = ?, IncludeIncome = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where JointAccountId = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, jad.getFinID());
			ps.setString(index++, jad.getFinReference());
			ps.setString(index++, jad.getCustCIF());
			ps.setBoolean(index++, jad.isIncludeRepay());
			ps.setString(index++, jad.getRepayAccountId());
			ps.setString(index++, jad.getCatOfcoApplicant());
			ps.setBoolean(index++, jad.isAuthoritySignatory());
			ps.setInt(index++, jad.getSequence());
			ps.setBoolean(index++, jad.isIncludeIncome());
			ps.setInt(index++, jad.getVersion());
			ps.setLong(index++, jad.getLastMntBy());
			ps.setTimestamp(index++, jad.getLastMntOn());
			ps.setString(index++, jad.getRecordStatus());
			ps.setString(index++, jad.getRoleCode());
			ps.setString(index++, jad.getNextRoleCode());
			ps.setString(index++, jad.getTaskId());
			ps.setString(index++, jad.getNextTaskId());
			ps.setString(index++, jad.getRecordType());
			ps.setLong(index++, jad.getWorkflowId());

			ps.setLong(index++, jad.getJointAccountId());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, jad.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public JointAccountDetail getJointAccountDetailByRefId(long finID, long jointAccountId, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ? and JointAccountId = ?");

		logger.debug(Literal.SQL + sql.toString());

		JointAccountDetailRowMapper rowMapper = new JointAccountDetailRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, jointAccountId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<JointAccountDetail> getJointAccountDetailByFinRef(long finID) {
		String sql = "Select JointAccountId, FinID, FinReference, CustCIF From FinJointAccountDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			JointAccountDetail ad = new JointAccountDetail();

			ad.setJointAccountId(rs.getLong("JointAccountId"));
			ad.setFinID(rs.getLong("FinID"));
			ad.setFinReference(rs.getString("FinReference"));
			ad.setCustCIF(rs.getString("CustCIF"));

			return ad;
		});
	}

	@Override
	public List<JointAccountDetail> getJointAccountDetailByFinRef(long finID, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		JointAccountDetailRowMapper rowMapper = new JointAccountDetailRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, rowMapper);
	}

	@Override
	public List<JointAccountDetail> getJointAccountDetailByFinRef(String finReference, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		JointAccountDetailRowMapper rowMapper = new JointAccountDetailRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, finReference);
		}, rowMapper);
	}

	@Override
	public List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jad) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T6.FinTypeDesc, T1.FinID, T1.FinReference");
		sql.append(", T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy FinCcy, T7.CcyEditField CcyEditField, T4.CustStsDescription Status");
		sql.append(", T5.CustStsDescription WorstStatus, T3.CustCIF");
		sql.append(" From  FinanceMain T1");
		sql.append(" Inner Join FinPftDetails T2 on T1.FinID = T2.FinID");
		sql.append(" Inner Join Customers T3 on T3.CustId = T1.CustID");
		sql.append(" Left Join BMTCustStatusCodes T4 on T4.CustStsCode = T3.CustSts");
		sql.append(" Left Join BMTCustStatusCodes T5 on T5.CustStsCode = T1.FinStatus");
		sql.append(" Inner Join RMTFinanceTypes T6 on T6.FinType = T1.FinType");
		sql.append(" Inner Join RMTCurrencies T7 on T7.CcyCode = T1.FinCcy");
		sql.append(" Where T3.CustCIF= ? and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceExposure> feList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jad.getCustCIF());
			ps.setInt(index, 1);
		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinTypeDesc(rs.getString("FinTypeDesc"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setStatus(rs.getString("Status"));
			fe.setWorstStatus(rs.getString("WorstStatus"));
			fe.setCustCif(rs.getString("CustCIF"));

			return fe;
		});

		return feList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getFinStartDate(), f2.getFinStartDate()))
				.collect(Collectors.toList());
	}

	private String commaJoin(List<String> listCIF) {
		return listCIF.stream().map(e -> "?").collect(Collectors.joining(", "));
	}

	@Override
	public List<FinanceExposure> getPrimaryExposureList(List<String> listCIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T6.FinTypeDesc, T1.FinID, T1.FinReference");
		sql.append(", T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy FinCcy, T7.CcyEditField CcyEditField, T4.CustStsDescription Status");
		sql.append(", T5.CustStsDescription WorstStatus, T3.CustCIF");
		sql.append(" From  FinanceMain T1");
		sql.append(" Inner Join FinPftDetails T2 on T1.FinID = T2.FinID");
		sql.append(" Inner Join Customers T3 on T3.CustId = T1.CustID");
		sql.append(" Left Join BMTCustStatusCodes T4 on T4.CustStsCode = T3.CustSts");
		sql.append(" Left Join BMTCustStatusCodes T5 on T5.CustStsCode = T1.FinStatus");
		sql.append(" Inner Join RMTFinanceTypes T6 on T6.FinType = T1.FinType");
		sql.append(" Inner Join RMTCurrencies T7 on T7.CcyCode = T1.FinCcy ");
		sql.append(" Where T3.CustCIF IN (");
		sql.append(commaJoin(listCIF));
		sql.append(" )");
		sql.append(" and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceExposure> feList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (String custCIF : listCIF) {
				ps.setString(index++, custCIF);
			}

			ps.setInt(index, 1);
		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinTypeDesc(rs.getString("FinTypeDesc"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setStatus(rs.getString("Status"));
			fe.setWorstStatus(rs.getString("WorstStatus"));
			fe.setCustCif(rs.getString("CustCIF"));

			return fe;
		});

		return feList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getFinStartDate(), f2.getFinStartDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jad) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T6.FinTypeDesc, T1.FinID, T1.FinReference");
		sql.append(", T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy FinCcy, T8.CcyEditField CcyEditField, T4.custStsDescription Status");
		sql.append(", T5.CustStsDescription WorstStatus, T3.CustCIF");
		sql.append(" From  FinanceMain T1");
		sql.append(" Inner Join FinPftDetails T2 on T1.FinID = T2.FinID");
		sql.append(" Inner Join Customers T3 on T3.CustId = T1.CustID ");
		sql.append(" Left Join BMTCustStatusCodes T4 on T4.CustStsCode = T3.CustSts");
		sql.append(" Left Join BMTCustStatusCodes T5 on T5.CustStsCode = T1.FinStatus");
		sql.append(" Inner Join RMTFinanceTypes T6 on T6.FinType = T1.FinType ");
		sql.append(" Inner Join FinJointAccountDetails_View T7  on T7.FinID = T1.FinID");
		sql.append(" Inner Join RMTCurrencies T8 ON T8.CcyCode = T1.FinCcy");
		sql.append(" Where T7.CustCIF = ?  and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jad.getCustCIF());
			ps.setInt(index, 1);

		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinTypeDesc(rs.getString("FinTypeDesc"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setStatus(rs.getString("Status"));
			fe.setWorstStatus(rs.getString("WorstStatus"));
			fe.setCustCif(rs.getString("CustCIF"));

			return fe;
		});
	}

	@Override
	public List<FinanceExposure> getSecondaryExposureList(List<String> listCIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T6.FinTypeDesc, T1.FinID, T1.FinReference");
		sql.append(", T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy FinCcy, T8.CcyEditField CcyEditField, T4.custStsDescription Status");
		sql.append(", T5.CustStsDescription WorstStatus, T3.CustCIF");
		sql.append(" From  FinanceMain T1");
		sql.append(" Inner Join FinPftDetails T2 on T1.FinID = T2.FinID");
		sql.append(" Inner Join Customers T3 on T3.CustId = T1.CustID ");
		sql.append(" Left Join BMTCustStatusCodes T4 on T4.CustStsCode = T3.CustSts");
		sql.append(" Left Join BMTCustStatusCodes T5 on T5.CustStsCode = T1.FinStatus ");
		sql.append(" Inner Join RMTFinanceTypes T6 on T6.FinType = T1.FinType ");
		sql.append(" Inner Join FinJointAccountDetails_View T7  on T7.FinID = T1.FinID");
		sql.append(" Inner Join RMTCurrencies T8 on T8.CcyCode = T1.FinCcy ");
		sql.append(" Where T7.CustCIF IN ( ");
		sql.append(commaJoin(listCIF));
		sql.append(" )");
		sql.append(" and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (String custCIF : listCIF) {
				ps.setString(index++, custCIF);
			}

			ps.setInt(index, 1);

		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinTypeDesc(rs.getString("FinTypeDesc"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setStatus(rs.getString("Status"));
			fe.setWorstStatus(rs.getString("WorstStatus"));
			fe.setCustCif(rs.getString("CustCIF"));

			return fe;
		});
	}

	@Override
	public List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jad) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T6.FinTypeDesc, T1.FinID, T1.FinReference");
		sql.append(", T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy FinCcy, T8.CcyEditField CcyEditField, T4.CustStsDescription Status");
		sql.append(", T5.custStsDescription WorstStatus, T3.CustCIF");
		sql.append(" From  FinanceMain T1");
		sql.append(" Inner Join  FinPftDetails T2 on T1.FinID = T2.FinID");
		sql.append(" Inner Join Customers T3 on T3.CustId = T1.CustID");
		sql.append(" Left Join BMTCustStatusCodes T4 on T4.CustStsCode = T3.CustSts");
		sql.append(" Left Join BMTCustStatusCodes T5 on T5.CustStsCode = T1.FinStatus");
		sql.append(" Inner Join RMTFinanceTypes T6 on T6.FinType = T1.FinType");
		sql.append(" Inner Join FinGuarantorsDetails_View T7  on T7.FinID = T1.FinID");
		sql.append(" Inner Join RMTCurrencies T8 on T8.CcyCode = T1.FinCcy ");
		sql.append(" Where T7.GuarantorCIF = ?  and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jad.getCustCIF());
			ps.setInt(index, 1);
		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinTypeDesc(rs.getString("FinTypeDesc"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setStatus(rs.getString("Status"));
			fe.setWorstStatus(rs.getString("WorstStatus"));
			fe.setCustCif(rs.getString("CustCIF"));

			return fe;
		});
	}

	@Override
	public FinanceExposure getOverDueDetails(FinanceExposure exposer) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" sum(FinCurODAmt) OverdueAmt, max(FinCurODDays) PastdueDays");
		sql.append(" From  FinanceMain FM");
		sql.append(" Inner Join FinODDetails OD on OD.FinID = FM.FinID");
		sql.append(" Where FM.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setOverdueAmt(rs.getBigDecimal("OverdueAmt"));
			fe.setPastdueDays(rs.getString("PastdueDays"));

			return fe;
		}, exposer.getFinID());
	}

	public JointAccountDetail getJointAccountDetailByRef(long finID, String custCIF, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ? and CustCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		JointAccountDetailRowMapper rowMapper = new JointAccountDetailRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, custCIF);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public JointAccountDetail getJointAccountDetailByRef(String finReference, String custCIF, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinReference = ? and CustCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		JointAccountDetailRowMapper rowMapper = new JointAccountDetailRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finReference, custCIF);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Map<String, Integer> getCustCtgCount(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Custctgcode, Count(*) Count");
		sql.append(" From FinJointAccountDetails_View F");
		sql.append(" Inner Join Customers C on C.CustCIF =  F.CustCIF Where FinID = ? group by CustCtgCode");

		return this.jdbcOperations.query(sql.toString(), new ResultSetExtractor<Map<String, Integer>>() {
			Map<String, Integer> map = new HashMap<String, Integer>();

			@Override
			public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					map.put(rs.getString(1), rs.getInt(2));
				}
				return map;
			}

		}, finID);
	}

	@Override
	public List<Long> getCustIdsByFinID(long finID) {
		String sql = "Select CustID From FinJointAccountDetails_View Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, (rs, rowNum) -> rs.getLong(1), finID);
	}

	@Override
	public List<FinanceEnquiry> getCoApplicantsFin(String custCif) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinType, fm.FinStatus, fm.FinStartDate, fm.FinCcy, fm.FinAmount");
		sql.append(", fm.DownPayment, fm.FeeChargeAmt, fm.FinCurrAssetValue ");
		sql.append(", fm.FinRepaymentAmount, fm.NumberOfTerms, ft.FintypeDesc as LovDescFinTypeName");
		sql.append(", coalesce(t6.MaxinstAmount, 0) MaxInstAmount");
		sql.append(" from FinanceMain fm");
		sql.append(" inner join RMTfinanceTypes ft on ft.Fintype = fm.FinType");
		sql.append(" left join (select FinID, (NSchdPri+NSchdPft) MaxInstAmount");
		sql.append(" from FinPftdetails) t6 on t6.FinID = fm.FinID");
		sql.append(" inner join FinJointAccountDetails  fja on fja.FinID = fm.FinID");
		sql.append(" Where fja.custcif = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, custCif);
		}, (rs, rowNum) -> {
			FinanceEnquiry fm = new FinanceEnquiry();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setDownPayment(rs.getBigDecimal("DownPayment"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
			fm.setMaxInstAmount(rs.getBigDecimal("MaxInstAmount"));
			fm.setCustomerType("Co Applicant");

			return fm;
		});
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" JointAccountId, FinID, FinReference, CustCIF, IncludeRepay, RepayAccountId");
		sql.append(", CatOfcoApplicant, AuthoritySignatory, Sequence, IncludeIncome");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCIFName, CustID, CustCoreBank, LovCustDob");
		}

		sql.append(" From FinJointAccountDetails");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class JointAccountDetailRowMapper implements RowMapper<JointAccountDetail> {
		private String type;

		private JointAccountDetailRowMapper(String type) {
			this.type = type;
		}

		@Override
		public JointAccountDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			JointAccountDetail jad = new JointAccountDetail();

			jad.setJointAccountId(rs.getLong("JointAccountId"));
			jad.setFinID(rs.getLong("FinID"));
			jad.setFinReference(rs.getString("FinReference"));
			jad.setCustCIF(rs.getString("CustCIF"));
			jad.setIncludeRepay(rs.getBoolean("IncludeRepay"));
			jad.setRepayAccountId(rs.getString("RepayAccountId"));
			jad.setCatOfcoApplicant(rs.getString("CatOfcoApplicant"));
			jad.setAuthoritySignatory(rs.getBoolean("AuthoritySignatory"));
			jad.setSequence(rs.getInt("Sequence"));
			jad.setIncludeIncome(rs.getBoolean("IncludeIncome"));
			jad.setVersion(rs.getInt("Version"));
			jad.setLastMntBy(rs.getLong("LastMntBy"));
			jad.setLastMntOn(rs.getTimestamp("LastMntOn"));
			jad.setRecordStatus(rs.getString("RecordStatus"));
			jad.setRoleCode(rs.getString("RoleCode"));
			jad.setNextRoleCode(rs.getString("NextRoleCode"));
			jad.setTaskId(rs.getString("TaskId"));
			jad.setNextTaskId(rs.getString("NextTaskId"));
			jad.setRecordType(rs.getString("RecordType"));
			jad.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				jad.setLovDescCIFName(rs.getString("LovDescCIFName"));
				jad.setCustID(rs.getLong("CustID"));
				jad.setCustCoreBank(rs.getString("CustCoreBank"));
				jad.setLovCustDob(rs.getTimestamp("LovCustDob"));
			}

			return jad;
		}
	}

	@Override
	public boolean isCoApplicant(long finID, String custCIF) {
		String sql = "Select Count(CustCIF) From FinJointAccountDetails Where FinID = ? and CustCIF = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, finID, custCIF) > 0;
	}
}