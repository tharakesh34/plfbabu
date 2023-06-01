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
 * * FileName : GuarantorDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
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
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>GuarantorDetail model</b> class.<br>
 * 
 */

public class GuarantorDetailDAOImpl extends SequenceDao<GuarantorDetail> implements GuarantorDetailDAO {
	private static Logger logger = LogManager.getLogger(GuarantorDetailDAOImpl.class);

	public GuarantorDetailDAOImpl() {
		super();
	}

	@Override
	public GuarantorDetail getGuarantorDetail() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GuarantorDetail");

		GuarantorDetail guarantorDetail = new GuarantorDetail();

		if (workFlowDetails != null) {
			guarantorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return guarantorDetail;
	}

	@Override
	public GuarantorDetail getNewGuarantorDetail() {
		GuarantorDetail guarantorDetail = getGuarantorDetail();
		guarantorDetail.setNewRecord(true);

		return guarantorDetail;
	}

	@Override
	public GuarantorDetail getGuarantorDetailById(final long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where GuarantorId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new GuarantorDetailRowMapper(type), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(GuarantorDetail gd, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinGuarantorsDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where GuarantorId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, gd.getGuarantorId()));

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(GuarantorDetail gd, String type) {
		if (gd.getId() == Long.MIN_VALUE) {
			gd.setId(getNextValue("SeqFinGuarantorsDetails"));
		}

		if (gd.getGuarantorProof() == null) {
			gd.setGuarantorProof(new byte[] { Byte.MIN_VALUE });
		}

		StringBuilder sql = new StringBuilder("Insert Into FinGuarantorsDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (GuarantorId, FinID, FinReference, BankCustomer, GuarantorCIF, GuarantorIDType");
		sql.append(", GuarantorIDNumber, GuarantorCIFName, GuranteePercentage, MobileNo, EmailId");
		sql.append(", GuarantorProof, GuarantorProofName, Remarks, AddrHNbr, FlatNbr, AddrStreet, AddrLine1");
		sql.append(", AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, GuarantorGenderCode");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, gd.getGuarantorId());
			ps.setLong(index++, gd.getFinID());
			ps.setString(index++, gd.getFinReference());
			ps.setBoolean(index++, gd.isBankCustomer());
			ps.setString(index++, gd.getGuarantorCIF());
			ps.setString(index++, gd.getGuarantorIDType());
			ps.setString(index++, gd.getGuarantorIDNumber());
			ps.setString(index++, gd.getGuarantorCIFName());
			ps.setBigDecimal(index++, gd.getGuranteePercentage());
			ps.setString(index++, gd.getMobileNo());
			ps.setString(index++, gd.getEmailId());
			ps.setBytes(index++, gd.getGuarantorProof());
			ps.setString(index++, gd.getGuarantorProofName());
			ps.setString(index++, gd.getRemarks());
			ps.setString(index++, gd.getAddrHNbr());
			ps.setString(index++, gd.getFlatNbr());
			ps.setString(index++, gd.getAddrStreet());
			ps.setString(index++, gd.getAddrLine1());
			ps.setString(index++, gd.getAddrLine2());
			ps.setString(index++, gd.getPOBox());
			ps.setString(index++, gd.getAddrCity());
			ps.setString(index++, gd.getAddrProvince());
			ps.setString(index++, gd.getAddrCountry());
			ps.setString(index++, gd.getAddrZIP());
			ps.setString(index++, gd.getGuarantorGenderCode());
			ps.setInt(index++, gd.getVersion());
			ps.setLong(index++, gd.getLastMntBy());
			ps.setTimestamp(index++, gd.getLastMntOn());
			ps.setString(index++, gd.getRecordStatus());
			ps.setString(index++, gd.getRoleCode());
			ps.setString(index++, gd.getNextRoleCode());
			ps.setString(index++, gd.getTaskId());
			ps.setString(index++, gd.getNextTaskId());
			ps.setString(index++, gd.getRecordType());
			ps.setLong(index, gd.getWorkflowId());
		});

		return gd.getId();
	}

	@Override
	public void update(GuarantorDetail gd, String type) {
		StringBuilder sql = new StringBuilder("Update FinGuarantorsDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinID = ?, FinReference = ?, BankCustomer = ?, GuarantorCIF = ?");
		sql.append(", GuarantorIDType = ?, GuarantorIDNumber = ?, GuarantorCIFName = ?, GuranteePercentage = ?");
		sql.append(", MobileNo = ?, EmailId = ?, GuarantorProofName = ?");

		if (gd.getGuarantorProof() != null) {
			sql.append(", GuarantorProof = ?");
		}

		sql.append(", AddrHNbr = ?, FlatNbr = ?, AddrStreet = ?, AddrLine1 = ?");
		sql.append(", AddrLine2 = ?, POBox = ?, AddrCity = ?, AddrProvince = ?");
		sql.append(", AddrCountry = ?, AddrZIP = ?, GuarantorGenderCode = ?, Remarks = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where GuarantorId = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version= ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, gd.getFinID());
			ps.setString(index++, gd.getFinReference());
			ps.setBoolean(index++, gd.isBankCustomer());
			ps.setString(index++, gd.getGuarantorCIF());
			ps.setString(index++, gd.getGuarantorIDType());
			ps.setString(index++, gd.getGuarantorIDNumber());
			ps.setString(index++, gd.getGuarantorCIFName());
			ps.setBigDecimal(index++, gd.getGuranteePercentage());
			ps.setString(index++, gd.getMobileNo());
			ps.setString(index++, gd.getEmailId());
			ps.setString(index++, gd.getGuarantorProofName());

			if (gd.getGuarantorProof() != null) {
				ps.setBytes(index++, gd.getGuarantorProof());

			}

			ps.setString(index++, gd.getAddrHNbr());
			ps.setString(index++, gd.getFlatNbr());
			ps.setString(index++, gd.getAddrStreet());
			ps.setString(index++, gd.getAddrLine1());
			ps.setString(index++, gd.getAddrLine2());
			ps.setString(index++, gd.getPOBox());
			ps.setString(index++, gd.getAddrCity());
			ps.setString(index++, gd.getAddrProvince());
			ps.setString(index++, gd.getAddrCountry());
			ps.setString(index++, gd.getAddrZIP());
			ps.setString(index++, gd.getGuarantorGenderCode());
			ps.setString(index++, gd.getRemarks());
			ps.setInt(index++, gd.getVersion());
			ps.setLong(index++, gd.getLastMntBy());
			ps.setTimestamp(index++, gd.getLastMntOn());
			ps.setString(index++, gd.getRecordStatus());
			ps.setString(index++, gd.getRoleCode());
			ps.setString(index++, gd.getNextRoleCode());
			ps.setString(index++, gd.getTaskId());
			ps.setString(index++, gd.getNextTaskId());
			ps.setString(index++, gd.getRecordType());
			ps.setLong(index++, gd.getWorkflowId());

			ps.setLong(index++, gd.getGuarantorId());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, gd.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public GuarantorDetail getGuarantorDetailByRefId(long finID, long guarantorId, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ? and GuarantorId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new GuarantorDetailRowMapper(type), finID,
					guarantorId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<GuarantorDetail> getGuarantorDetailByFinRef(long finID, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, new GuarantorDetailRowMapper(type));
	}

	@Override
	public GuarantorDetail getGuarantorProof(GuarantorDetail gd) {
		String sql = "Select GuarantorProof From FinGuarantorsDetails_View Where GuarantorId = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, (rs, num) -> {
			GuarantorDetail g = new GuarantorDetail();
			g.setGuarantorProof(rs.getBytes("GuarantorProof"));

			return g;
		}, gd.getGuarantorId());
	}

	@Override
	public List<FinanceExposure> getPrimaryExposureList(GuarantorDetail gd) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  T1.FinType, T1.FinID, T1.FinReference, T1.FinStartDate, T1.MaturityDate");
		sql.append(",  (T1.FinAmount + T3.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T3.FeeChargeAmt - T1.DownPayment - T3.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy, T1.CustCIF, T2.CcyEditField CcyEditField");
		sql.append(", coalesce((Select sum(FinCurODAmt) From FinODDetails Where FinID = T1.FinID), 0) OverdueAmt");
		sql.append(", coalesce((Select max(FinCurODDays) From FinODDetails Where FinID = T1.FinID), 0) PastdueDays");
		sql.append(" From FinPftDetails T1 Inner Join RMTCurrencies T2 ON T2.CcyCode = T1.FinCcy");
		sql.append(" Inner Join FinanceMain T3 ON T1.FinID = T3.FinID");
		sql.append(" Where T1.CustCIF = ? and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceExposure> feList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, gd.getGuarantorCIF());
			ps.setInt(index, 1);
		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCustCif(rs.getString("CustCIF"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setOverdueAmt(rs.getBigDecimal("OverdueAmt"));
			fe.setPastdueDays(rs.getString("PastdueDays"));

			return fe;
		});

		return feList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getFinStartDate(), f2.getFinStartDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<FinanceExposure> getSecondaryExposureList(GuarantorDetail gd) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T1.FinID, T1.FinReference, T1.FinStartDate, T1.MaturityDate");
		sql.append(", (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment - T4.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy, T1.CustCIF, T2.CcyEditField CcyEditField");
		sql.append(", coalesce((Select sum(FinCurODAmt) From FinODDetails Where FinID = T1.FinID), 0) OverdueAmt");
		sql.append(", coalesce((Select max(FinCurODDays) From FinODDetails Where FinID = T1.FinID), 0) PastdueDays");
		sql.append(" From FinPftDetails T1");
		sql.append(" Inner Join RMTCurrencies T2 ON T2.CcyCode = T1.FinCcy");
		sql.append(" Inner Join FinJointAccountDetails_View T3 on T1.FinID = T3.FinID");
		sql.append(" Inner Join FinanceMain T4 on T1.FinID = T4.FinID");
		sql.append(" Where T3.CustCIF = ? and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceExposure> feList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, gd.getGuarantorCIF());
			ps.setInt(index, 1);
		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCustCif(rs.getString("CustCIF"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setOverdueAmt(rs.getBigDecimal("OverdueAmt"));
			fe.setPastdueDays(rs.getString("PastdueDays"));

			return fe;
		});

		return feList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getFinStartDate(), f2.getFinStartDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<FinanceExposure> getGuarantorExposureList(GuarantorDetail gd) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinType, T1.FinID, T1.FinReference, T1.FinStartDate, T1.MaturityDate");
		sql.append(", (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment) FinanceAmt");
		sql.append(", (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment - T4.FinRepaymentAmount) CurrentExpoSure");
		sql.append(", T1.FinCcy, T1.CustCIF, T2.CcyEditField CcyEditField");
		sql.append(", coalesce((Select sum(FinCurODAmt) From FinODDetails Where FinID = T1.FinID), 0) OverdueAmt");
		sql.append(", coalesce((Select max(FinCurODDays) From FinODDetails Where FinID = T1.FinID), 0) PastdueDays");
		sql.append(" From FinPftDetails T1 Inner Join RMTCurrencies T2 ON T2.CcyCode = T1.FinCcy");
		sql.append(" Inner Join FinGuarantorsDetails_View T3 on T1.FinID = T3.FinID");
		sql.append(" Inner Join FinanceMain T4 on T1.FinID = T4.FinID");
		sql.append(" Where T3.GuarantorCIF = ? and T1.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceExposure> feList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, gd.getGuarantorCIF());
			ps.setInt(index, 1);
		}, (rs, num) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setFinType(rs.getString("FinType"));
			fe.setFinID(rs.getLong("FinID"));
			fe.setFinReference(rs.getString("FinReference"));
			fe.setFinStartDate(rs.getDate("FinStartDate"));
			fe.setMaturityDate(rs.getDate("MaturityDate"));
			fe.setFinanceAmt(rs.getBigDecimal("FinanceAmt"));
			fe.setCurrentExpoSure(rs.getBigDecimal("CurrentExpoSure"));
			fe.setFinCCY(rs.getString("FinCcy"));
			fe.setCustCif(rs.getString("CustCIF"));
			fe.setCcyEditField(rs.getInt("CcyEditField"));
			fe.setOverdueAmt(rs.getBigDecimal("OverdueAmt"));
			fe.setPastdueDays(rs.getString("PastdueDays"));

			return fe;
		});

		return feList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getFinStartDate(), f2.getFinStartDate()))
				.collect(Collectors.toList());
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" GuarantorId, FinID, FinReference, BankCustomer, GuarantorCIF, GuarantorIDType, GuarantorIDNumber");
		sql.append(", GuarantorCIFName, GuranteePercentage, MobileNo, EmailId, GuarantorProof, GuarantorProofName");
		sql.append(", AddrHNbr, FlatNbr, AddrStreet, AddrLine1, AddrLine2, POBox, AddrCity, AddrProvince");
		sql.append(", AddrCountry, AddrZIP, GuarantorGenderCode, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", GuarantorIDTypeName, CustID, CustShrtName, LovCustDob");
		}

		sql.append(" From FinGuarantorsDetails");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class GuarantorDetailRowMapper implements RowMapper<GuarantorDetail> {
		private String type;

		private GuarantorDetailRowMapper(String type) {
			this.type = type;
		}

		@Override
		public GuarantorDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			GuarantorDetail gd = new GuarantorDetail();

			gd.setGuarantorId(rs.getLong("GuarantorId"));
			gd.setFinID(rs.getLong("FinID"));
			gd.setFinReference(rs.getString("FinReference"));
			gd.setBankCustomer(rs.getBoolean("BankCustomer"));
			gd.setGuarantorCIF(rs.getString("GuarantorCIF"));
			gd.setGuarantorIDType(rs.getString("GuarantorIDType"));
			gd.setGuarantorIDNumber(rs.getString("GuarantorIDNumber"));
			gd.setGuarantorCIFName(rs.getString("GuarantorCIFName"));
			gd.setGuranteePercentage(rs.getBigDecimal("GuranteePercentage"));
			gd.setMobileNo(rs.getString("MobileNo"));
			gd.setEmailId(rs.getString("EmailId"));
			gd.setGuarantorProof(rs.getBytes("GuarantorProof"));
			gd.setGuarantorProofName(rs.getString("GuarantorProofName"));
			gd.setAddrHNbr(rs.getString("AddrHNbr"));
			gd.setFlatNbr(rs.getString("FlatNbr"));
			gd.setAddrStreet(rs.getString("AddrStreet"));
			gd.setAddrLine1(rs.getString("AddrLine1"));
			gd.setAddrLine2(rs.getString("AddrLine2"));
			gd.setPOBox(rs.getString("POBox"));
			gd.setAddrCity(rs.getString("AddrCity"));
			gd.setAddrProvince(rs.getString("AddrProvince"));
			gd.setAddrCountry(rs.getString("AddrCountry"));
			gd.setAddrZIP(rs.getString("AddrZIP"));
			gd.setGuarantorGenderCode(rs.getString("GuarantorGenderCode"));
			gd.setRemarks(rs.getString("Remarks"));
			gd.setVersion(rs.getInt("Version"));
			gd.setLastMntBy(rs.getLong("LastMntBy"));
			gd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			gd.setRecordStatus(rs.getString("RecordStatus"));
			gd.setRoleCode(rs.getString("RoleCode"));
			gd.setNextRoleCode(rs.getString("NextRoleCode"));
			gd.setTaskId(rs.getString("TaskId"));
			gd.setNextTaskId(rs.getString("NextTaskId"));
			gd.setRecordType(rs.getString("RecordType"));
			gd.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				gd.setGuarantorIDTypeName(rs.getString("GuarantorIDTypeName"));
				gd.setCustID(rs.getLong("CustID"));
				gd.setCustShrtName(rs.getString("CustShrtName"));
				gd.setLovCustDob(rs.getDate("LovCustDob"));
			}

			return gd;
		}
	}

	@Override
	public List<FinanceEnquiry> getGuarantorsFin(String custCif, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinType, fm.FinStatus, fm.FinStartDate, fm.FinCcy, fm.FinAmount");
		sql.append(", fm.DownPayment, fm.FeeChargeAmt, fm.FinCurrAssetValue ");
		sql.append(", fm.FinRepaymentAmount, fm.NumberOfTerms, ft.FintypeDesc as LovDescFinTypeName");
		sql.append(", coalesce(t6.MaxinstAmount, 0) MaxInstAmount");
		sql.append(" from FinanceMain fm");
		sql.append(" inner join RMTfinanceTypes ft on ft.Fintype = fm.FinType");
		sql.append(" left join (select FinID, (NSchdPri+NSchdPft) MaxInstAmount");
		sql.append(" from FinPftdetails) t6 on t6.FinID = fm.FinID");
		sql.append(" inner join FinGuarantorsDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" fgd on fgd.FinID = fm.FinID");
		sql.append(" Where fgd.Guarantorcif = ?");

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
			fm.setCustomerType("Guarantor");

			return fm;
		});
	}

	@Override
	public boolean isGuarantor(long finID, String custCIF) {
		String sql = "Select Count(GuarantorCif) From FinGuarantorsDetails Where FinID = ? and GuarantorCif = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, finID, custCIF) > 0;
	}
}