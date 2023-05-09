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
 * * FileName : MandateDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified Date
 * : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.mandate.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

/**
 * DAO methods implementation for the <b>Mandate model</b> class.<br>
 * 
 */
public class MandateDAOImpl extends SequenceDao<Mandate> implements MandateDAO {

	public MandateDAOImpl() {
		super();
	}

	@Override
	public Mandate getMandateById(Long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new MandateRowMapper(type), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 *
	 */
	@Override
	public Mandate getMandateByFinReference(String finReference, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where OrgReference = ? and SecurityMandate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new MandateRowMapper(type), finReference, true);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Mandate getMandateByStatus(final long id, String status, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where MandateID = ?  and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new MandateRowMapper(type), id, status);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Mandate getMandateByOrgReference(String orgReference, boolean isSecurityMandate, String status,
			String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where OrgReference = ?  and Status = ? and SecurityMandate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new MandateRowMapper(type), orgReference, status,
					isSecurityMandate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(Mandate mandate, String type) {
		StringBuilder sql = new StringBuilder("Delete From Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, mandate.getMandateID()));
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(Mandate mdt, String type) {
		if (mdt.getMandateID() == Long.MIN_VALUE) {
			mdt.setMandateID(getNextValue("SeqMandates"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (MandateID, CustID, MandateRef, MandateType, BankBranchID, AccNumber, AccHolderName");
		sql.append(", JointAccHolderName, AccType, OpenMandate, StartDate, ExpiryDate, MaxLimit");
		sql.append(", Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status, ApprovalID");
		sql.append(", InputDate, Active, Reason, MandateCcy, DocumentName, DocumentRef, ExternalRef");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId,  RecordType, WorkflowId");
		sql.append(", OrgReference, BarCodeNumber, SwapIsActive, PrimaryMandateId, EntityCode, PartnerBankId");
		sql.append(", DefaultMandate, EMandateSource, EMandateReferenceNo, HoldReason");
		sql.append(", SwapEffectiveDate, SecurityMandate,  EmployerID, EmployeeNo, ExternalMandate)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?");
		sql.append(",? ,?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, mdt.getMandateID());
			ps.setLong(index++, mdt.getCustID());
			ps.setString(index++, mdt.getMandateRef());
			ps.setString(index++, mdt.getMandateType());
			ps.setObject(index++, mdt.getBankBranchID());
			ps.setString(index++, mdt.getAccNumber());
			ps.setString(index++, mdt.getAccHolderName());
			ps.setString(index++, mdt.getJointAccHolderName());
			ps.setString(index++, mdt.getAccType());
			ps.setBoolean(index++, mdt.isOpenMandate());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(mdt.getExpiryDate()));
			ps.setBigDecimal(index++, mdt.getMaxLimit());
			ps.setString(index++, mdt.getPeriodicity());
			ps.setString(index++, mdt.getPhoneCountryCode());
			ps.setString(index++, mdt.getPhoneAreaCode());
			ps.setString(index++, mdt.getPhoneNumber());
			ps.setString(index++, mdt.getStatus());
			ps.setString(index++, mdt.getApprovalID());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getInputDate()));
			ps.setBoolean(index++, mdt.isActive());
			ps.setString(index++, mdt.getReason());
			ps.setString(index++, mdt.getMandateCcy());
			ps.setString(index++, mdt.getDocumentName());
			ps.setObject(index++, mdt.getDocumentRef());
			ps.setString(index++, mdt.getExternalRef());
			ps.setInt(index++, mdt.getVersion());
			ps.setLong(index++, mdt.getLastMntBy());
			ps.setTimestamp(index++, mdt.getLastMntOn());
			ps.setString(index++, mdt.getRecordStatus());
			ps.setString(index++, mdt.getRoleCode());
			ps.setString(index++, mdt.getNextRoleCode());
			ps.setString(index++, mdt.getTaskId());
			ps.setString(index++, mdt.getNextTaskId());
			ps.setString(index++, mdt.getRecordType());
			ps.setLong(index++, mdt.getWorkflowId());
			ps.setString(index++, mdt.getOrgReference());
			ps.setString(index++, mdt.getBarCodeNumber());
			ps.setBoolean(index++, mdt.isSwapIsActive());
			ps.setLong(index++, mdt.getPrimaryMandateId());
			ps.setString(index++, mdt.getEntityCode());
			ps.setObject(index++, mdt.getPartnerBankId());
			ps.setBoolean(index++, mdt.isDefaultMandate());
			ps.setString(index++, mdt.geteMandateSource());
			ps.setString(index++, mdt.geteMandateReferenceNo());
			ps.setObject(index++, mdt.getHoldReason());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getSwapEffectiveDate()));
			ps.setBoolean(index++, mdt.isSecurityMandate());
			ps.setObject(index++, mdt.getEmployerID());
			ps.setString(index++, mdt.getEmployeeNo());
			ps.setBoolean(index, mdt.isExternalMandate());
		});

		return mdt.getMandateID();
	}

	@Override
	public void update(Mandate mdt, String type) {
		StringBuilder sql = new StringBuilder("Update Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustID = ?, MandateRef = ?, MandateType = ?, BankBranchID = ?, AccNumber = ?");
		sql.append(", AccHolderName = ?, JointAccHolderName = ?, AccType = ?, OpenMandate = ?");
		sql.append(", StartDate = ?, ExpiryDate = ?, MaxLimit = ?, Periodicity = ?, OrgReference = ?");
		sql.append(", PhoneCountryCode = ?, PhoneAreaCode = ?, PhoneNumber = ?, Status = ?, ApprovalID = ?");
		sql.append(", Active = ?, Reason = ?, MandateCcy = ?, DocumentName = ?, DocumentRef = ?, ExternalRef = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ? ,NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", InputDate = ?, BarCodeNumber = ?, SwapIsActive = ?, PrimaryMandateId = ?, EntityCode = ?");
		sql.append(", PartnerBankId = ?, DefaultMandate = ?, EMandateSource = ?, EMandateReferenceNo = ?");
		sql.append(", HoldReason = ?, SwapEffectiveDate = ?, EmployerID = ?, EmployeeNo = ?, ExternalMandate = ?");
		sql.append(" Where MandateID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version= ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, mdt.getCustID());
			ps.setString(index++, mdt.getMandateRef());
			ps.setString(index++, mdt.getMandateType());
			ps.setObject(index++, mdt.getBankBranchID());
			ps.setString(index++, mdt.getAccNumber());
			ps.setString(index++, mdt.getAccHolderName());
			ps.setString(index++, mdt.getJointAccHolderName());
			ps.setString(index++, mdt.getAccType());
			ps.setBoolean(index++, mdt.isOpenMandate());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(mdt.getExpiryDate()));
			ps.setBigDecimal(index++, mdt.getMaxLimit());
			ps.setString(index++, mdt.getPeriodicity());
			ps.setString(index++, mdt.getOrgReference());
			ps.setString(index++, mdt.getPhoneCountryCode());
			ps.setString(index++, mdt.getPhoneAreaCode());
			ps.setString(index++, mdt.getPhoneNumber());
			ps.setString(index++, mdt.getStatus());
			ps.setString(index++, mdt.getApprovalID());
			ps.setBoolean(index++, mdt.isActive());
			ps.setString(index++, mdt.getReason());
			ps.setString(index++, mdt.getMandateCcy());
			ps.setString(index++, mdt.getDocumentName());
			ps.setObject(index++, mdt.getDocumentRef());
			ps.setString(index++, mdt.getExternalRef());
			ps.setInt(index++, mdt.getVersion());
			ps.setLong(index++, mdt.getLastMntBy());
			ps.setTimestamp(index++, mdt.getLastMntOn());
			ps.setString(index++, mdt.getRecordStatus());
			ps.setString(index++, mdt.getRoleCode());
			ps.setString(index++, mdt.getNextRoleCode());
			ps.setString(index++, mdt.getTaskId());
			ps.setString(index++, mdt.getNextTaskId());
			ps.setString(index++, mdt.getRecordType());
			ps.setLong(index++, mdt.getWorkflowId());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getInputDate()));
			ps.setString(index++, mdt.getBarCodeNumber());
			ps.setBoolean(index++, mdt.isSwapIsActive());
			ps.setLong(index++, mdt.getPrimaryMandateId());
			ps.setString(index++, mdt.getEntityCode());
			ps.setObject(index++, mdt.getPartnerBankId());
			ps.setBoolean(index++, mdt.isDefaultMandate());
			ps.setString(index++, mdt.geteMandateSource());
			ps.setString(index++, mdt.geteMandateReferenceNo());
			ps.setObject(index++, mdt.getHoldReason());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getSwapEffectiveDate()));
			ps.setObject(index++, mdt.getEmployerID());
			ps.setString(index++, mdt.getEmployeeNo());
			ps.setBoolean(index++, mdt.isExternalMandate());

			ps.setLong(index++, mdt.getMandateID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, mdt.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateFinMandate(Mandate mdt, String type) {
		StringBuilder sql = new StringBuilder("Update Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustID = ?, MandateRef = ?, MandateType = ?, BankBranchID = ?, AccNumber = ?");
		sql.append(", AccHolderName = ?, JointAccHolderName = ?, AccType = ?, OpenMandate = ?");
		sql.append(", StartDate = ?, ExpiryDate = ?, MaxLimit = ?, Periodicity = ?, OrgReference = ?");
		sql.append(", PhoneCountryCode = ?, PhoneAreaCode = ?, PhoneNumber = ?, ApprovalID = ?");
		sql.append(", Active = ?, Reason = ?, MandateCcy = ?, DocumentName = ?, DocumentRef = ?, ExternalRef = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ? ,NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", BarCodeNumber = ?, SwapIsActive = ?, EntityCode = ?, PartnerBankId = ?, DefaultMandate = ?");
		sql.append(", EMandateSource = ?, EMandateReferenceNo = ?, HoldReason = ?");
		sql.append(", SwapEffectivedate = ?, EmployerID = ?, EmployeeNo = ?, ExternalMandate = ?");
		sql.append("  Where MandateID = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, mdt.getCustID());
			ps.setString(index++, mdt.getMandateRef());
			ps.setString(index++, mdt.getMandateType());
			ps.setObject(index++, JdbcUtil.getLong(mdt.getBankBranchID()));
			ps.setString(index++, mdt.getAccNumber());
			ps.setString(index++, mdt.getAccHolderName());
			ps.setString(index++, mdt.getJointAccHolderName());
			ps.setString(index++, mdt.getAccType());
			ps.setBoolean(index++, mdt.isOpenMandate());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(mdt.getExpiryDate()));
			ps.setBigDecimal(index++, mdt.getMaxLimit());
			ps.setString(index++, mdt.getPeriodicity());
			ps.setString(index++, mdt.getOrgReference());
			ps.setString(index++, mdt.getPhoneCountryCode());
			ps.setString(index++, mdt.getPhoneAreaCode());
			ps.setString(index++, mdt.getPhoneNumber());
			ps.setString(index++, mdt.getApprovalID());
			ps.setBoolean(index++, mdt.isActive());
			ps.setString(index++, mdt.getReason());
			ps.setString(index++, mdt.getMandateCcy());
			ps.setString(index++, mdt.getDocumentName());
			ps.setObject(index++, JdbcUtil.getLong(mdt.getDocumentRef()));
			ps.setString(index++, mdt.getExternalRef());
			ps.setInt(index++, mdt.getVersion());
			ps.setLong(index++, mdt.getLastMntBy());
			ps.setTimestamp(index++, mdt.getLastMntOn());
			ps.setString(index++, mdt.getRecordStatus());
			ps.setString(index++, mdt.getRoleCode());
			ps.setString(index++, mdt.getNextRoleCode());
			ps.setString(index++, mdt.getTaskId());
			ps.setString(index++, mdt.getNextTaskId());
			ps.setString(index++, mdt.getRecordType());
			ps.setLong(index++, mdt.getWorkflowId());
			ps.setString(index++, mdt.getBarCodeNumber());
			ps.setBoolean(index++, mdt.isSwapIsActive());
			ps.setString(index++, mdt.getEntityCode());
			ps.setObject(index++, mdt.getPartnerBankId());
			ps.setBoolean(index++, mdt.isDefaultMandate());
			ps.setString(index++, mdt.geteMandateSource());
			ps.setString(index++, mdt.geteMandateReferenceNo());
			ps.setObject(index++, mdt.getHoldReason());
			ps.setDate(index++, JdbcUtil.getDate(mdt.getSwapEffectiveDate()));
			ps.setObject(index++, mdt.getEmployerID());
			ps.setString(index++, mdt.getEmployeeNo());
			ps.setBoolean(index++, mdt.isExternalMandate());

			ps.setLong(index++, mdt.getMandateID());
			ps.setString(index, mdt.getStatus());
		});
	}

	@Override
	public void updateStatus(long mandateID, String mandateStatusAwaitcon, String mandateRef, String approvalId,
			String type) {
		StringBuilder sql = new StringBuilder("Update Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set  Status = ?, MandateRef = ?, ApprovalID = ?");
		sql.append(" Where MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, mandateStatusAwaitcon);
			ps.setString(index++, mandateRef);
			ps.setString(index++, approvalId);
			ps.setLong(index, mandateID);
		});
	}

	@Override
	public void updateActive(long mandateID, String status, boolean active) {
		String sql = "Update Mandates Set Active = ?, Status= ? Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBoolean(index++, active);
			ps.setString(index++, status);
			ps.setLong(index, mandateID);
		});
	}

	@Override
	public List<FinanceEnquiry> getMandateFinanceDetailById(long mandateID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinType, FinStatus, FinStartDate, FinAmount, DownPayment, FeeChargeAmt");
		sql.append(", NumberOfTerms, LovDescFinTypeName, MaxInstAmount, FinRepaymentAmount");
		sql.append(", FinCurrAssetValue");
		sql.append(" From MandateEnquiry_view");
		sql.append(" Where MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceEnquiry mndts = new FinanceEnquiry();

			mndts.setFinReference(rs.getString("FinReference"));
			mndts.setFinType(rs.getString("FinType"));
			mndts.setFinStatus(rs.getString("FinStatus"));
			mndts.setFinStartDate(rs.getTimestamp("FinStartDate"));
			mndts.setFinAmount(rs.getBigDecimal("FinAmount"));
			mndts.setDownPayment(rs.getBigDecimal("DownPayment"));
			mndts.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			mndts.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			mndts.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
			mndts.setMaxInstAmount(rs.getBigDecimal("MaxInstAmount"));
			mndts.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			mndts.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));

			return mndts;
		}, mandateID);
	}

	@Override
	public List<Mandate> getApprovedMandatesByCustomerId(long custID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, custID), new MandateRowMapper(type));
	}

	@Override
	public void updateOrgReferecne(long mandateID, String orgReference, String type) {
		StringBuilder sql = new StringBuilder("Update Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set OrgReference = ?");
		sql.append(" Where MandateID = ? and OrgReference is null");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, orgReference);
			ps.setLong(index, mandateID);
		});
	}

	@Override
	public int getBranch(long bankBranchID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(BankBranchID)");
		sql.append(" From Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankBranchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, bankBranchID);
	}

	@Override
	public List<Mandate> getMnadateByCustID(long custID, long mandateID) {
		String sql = "Select MandateType From Mandates Where CustID = ? and OpenMandate = ? and Active = ? and MandateID != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setLong(index++, custID);
			ps.setBoolean(index++, true);
			ps.setBoolean(index++, true);
			ps.setLong(index, mandateID);
		}, (rs, rowNum) -> {
			Mandate mndts = new Mandate();

			mndts.setMandateType(rs.getString("MandateType"));

			return mndts;
		});
	}

	@Override
	public int getSecondaryMandateCount(long mandateID) {
		String sql = "Select Count(PrimaryMandateId) From Mandates Where PrimaryMandateId = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, mandateID, 1);

	}

	@Override
	public void updateStatusAfterRegistration(long mandateID, String statusInprocess) {
		String sql = "Update Mandates Set Status = ? Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, statusInprocess);
			ps.setLong(index, mandateID);
		});
	}

	@Override
	public boolean checkMandateStatus(long mandateID) {
		String sql = "Select Count(Mandate_ID) from Mandate_Registration Where Mandate_ID = ? and Machine_Flag = ? and Active_Flag = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, mandateID, "Y", 1) > 0;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public boolean checkMandates(String orgRef, long mandateId, boolean securityMandate) {
		String sql = "Select Count(MandateID) from Mandates Where OrgReference = ? and Status in (?, ?, ?) and Active = ? and SecurityMandate = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, orgRef, "AC", "INPROCESS", "NEW", 1,
				securityMandate) > 0;
	}

	@Override
	public int getBarCodeCount(String barCode, long mandateID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(MandateID)");
		sql.append(" From Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BarCodeNumber = ? And MandateID != ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, barCode, mandateID);
	}

	@Override
	public BigDecimal getMaxRepayAmount(Long mandateID) {
		StringBuilder sql = new StringBuilder("Select max(RepayAmount) From (");
		sql.append(" Select RepayAmount From FinScheduleDetails_Temp schd");
		sql.append(" Inner Join FinanceMain_Temp fm on fm.FinId = schd.FinId and MandateId = ?");
		sql.append(" Union all");
		sql.append(" Select RepayAmount From FinScheduleDetails schd");
		sql.append(" Inner Join FinanceMain fm on fm.FinId = schd.FinId and MandateId = ?) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, mandateID, mandateID);

	}

	@Override
	public boolean entityExistMandate(String entityCode, String type) {
		StringBuilder sql = new StringBuilder("Select Count(MandateID)");
		sql.append(" From Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where EntityCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, entityCode) > 0;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public Mandate getMandateStatusById(String finReference, Long mandateID) {
		String sql = "Select Status, ExpiryDate From Mandates Where MandateID = ? and OrgReference = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				Mandate mndts = new Mandate();

				mndts.setStatus(rs.getString("Status"));
				mndts.setExpiryDate(rs.getTimestamp("ExpiryDate"));

				return mndts;
			}, mandateID, finReference, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getMandateCount(long custID, long mandateID) {
		String sql = "Select COUNT(MandateID) From Mandates Where CustID = ? and DefaultMandate = ? and mandateID != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, custID, 1, mandateID);
	}

	@Override
	public int validateEmandateSource(String eMandateSource) {
		String sql = "Select Count(Code) From Mandate_Sources Where Code = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, eMandateSource);
	}

	@Override
	public int updateMandateStatus(Mandate mandate) {
		String sql = "Update Mandates Set Status = ?, MandateRef = ?, OrgReference = ?, Reason = ? Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, mandate.getStatus());
			ps.setString(index++, mandate.getMandateRef());
			ps.setString(index++, mandate.getOrgReference());
			ps.setString(index++, mandate.getReason());

			ps.setLong(index, mandate.getMandateID());
		});
	}

	@Override
	public int getMandateByMandateRef(String mandateRef) {
		String sql = "Select Count(MandateID) From Mandates Where MandateRef = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, mandateRef);
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(String finreference, long mandateID, String status) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, PresentmentId, Finreference, FinId,  SchDate, MandateId,");
		sql.append(" ExcludeReason, BounceId, Status from presentmentdetails");
		sql.append(" Where finreference =? and mandateid =? and status =?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<PresentmentDetail> list = jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail detail = new PresentmentDetail();
			detail.setId(rs.getLong("Id"));
			detail.setHeaderId(rs.getLong("PresentmentId"));
			detail.setFinReference(rs.getString("Finreference"));
			detail.setFinID(rs.getLong("FinId"));
			detail.setSchDate(rs.getDate("SchDate"));
			detail.setMandateId(rs.getLong("MandateId"));
			detail.setExcludeReason(rs.getInt("ExcludeReason"));
			detail.setBounceID(rs.getLong("BounceId"));
			detail.setStatus(rs.getString("Status"));

			return detail;
		}, finreference, mandateID, status);

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getId(), l2.getId())).collect(Collectors.toList());
	}

	public List<Mandate> getLoans(long custId, String finRepayMethod) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinType, fm.FinRepayMethod, ft.AlwdRpyMethods");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Where fm.FinIsActive = ? and fm.CustId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Mandate mandate = new Mandate();

			mandate.setFinID(rs.getLong("FinID"));
			mandate.setFinReference(rs.getString("FinReference"));
			mandate.setFinType(rs.getString("FinType"));
			mandate.setFinRepayMethod(rs.getString("FinRepayMethod"));
			mandate.setAlwdRpyMethods(rs.getString("AlwdRpyMethods"));

			return mandate;
		}, 1, custId);
	}

	@Override
	public Mandate getEmployerDetails(long custID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EmployerID, EmpName, AllowDas");
		sql.append(" From CustomerEmpDetails ced");
		sql.append(" Inner Join EmployerDetail ed on ed.EmployerID = ced.CustEmpName ");
		sql.append(" Where EmpIsActive = ? and CustID = ? and CurrentEmployer = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Mandate mdt = new Mandate();

				mdt.setEmployerID(rs.getLong("EmployerID"));
				mdt.setEmployerName(rs.getString("EmpName"));
				mdt.setAllowDAS(rs.getBoolean("AllowDas"));

				return mdt;
			}, 1, custID, 1);

		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Mandate getLoanInfo(String finReference) {
		Mandate fm = getLoanInfo(finReference, TableType.MAIN_TAB);

		if (fm == null && MandateExtension.APPROVE_ON_LOAN_ORG) {
			fm = getLoanInfo(finReference, TableType.TEMP_TAB);
		}

		return fm;
	}

	@Override
	public Mandate getLoanInfo(String finReference, long custID) {
		Mandate fm = getLoanInfo(finReference, TableType.MAIN_TAB);

		if (fm == null && MandateExtension.APPROVE_ON_LOAN_ORG) {
			fm = getLoanInfo(finReference, TableType.TEMP_TAB);
		}

		return fm;
	}

	@Override
	public List<Long> getFinanceMainbyCustId(long custId) {
		StringBuilder sql = new StringBuilder("Select FinID From FinanceMain Where FinIsActive = ? and CustID = ?");
		if (MandateExtension.APPROVE_ON_LOAN_ORG) {
			sql.append(" Union All");
			sql.append(" Select FinID From FinanceMain_Temp Where FinIsActive = ? and CustID = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setLong(index++, custId);

			if (MandateExtension.APPROVE_ON_LOAN_ORG) {
				ps.setInt(index++, 1);
				ps.setLong(index++, custId);
			}
		}, (rs, rowNum) -> rs.getLong(1));
	}

	private Mandate getLoanInfo(String finReference, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustID, fm.FinID, fm.FinReference, fm.FinType, fm.FinRepayMethod, ft.AlwdRpyMethods");
		sql.append(" From FinanceMain");
		sql.append(tableType.getSuffix()).append(" fm");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Where fm.FinReference = ? and fm.FinIsActive = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Mandate mandate = new Mandate();

				mandate.setCustID(rs.getLong("CustID"));
				mandate.setFinID(rs.getLong("FinID"));
				mandate.setFinReference(rs.getString("FinReference"));
				mandate.setFinType(rs.getString("FinType"));
				mandate.setFinRepayMethod(rs.getString("FinRepayMethod"));
				mandate.setAlwdRpyMethods(rs.getString("AlwdRpyMethods"));

				return mandate;
			}, finReference, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void holdMandate(long mandateId, String reason) {
		String sql = "Update Mandates Set Status = ?, HoldReason = ? Where MandateId = ?";

		jdbcOperations.update(sql, MandateStatus.HOLD, reason, mandateId);
	}

	@Override
	public void unHoldMandate(long mandateId) {
		String sql = "Update Mandates Set Status = ?, HoldReason = ? Where MandateId = ?";

		jdbcOperations.update(sql, MandateStatus.APPROVED, null, mandateId);
	}

	@Override
	public boolean isValidMandate(Long id) {
		String sql = "Select count(MandateID) From Mandates Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, id) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public long getCustID(Long id) {
		String sql = "Select CustId From Mandates Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public Mandate getMandateDetail(long mandateID) {
		String sql = "Select Active, MandateRef, Status From Mandates Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				Mandate mandate = new Mandate();

				mandate.setActive(rs.getBoolean("Active"));
				mandate.setMandateRef(rs.getString("MandateRef"));
				mandate.setStatus(rs.getString("Status"));

				return mandate;
			}, mandateID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MandateID, CustID, MandateRef, MandateType, BankBranchID, AccNumber, AccHolderName");
		sql.append(", JointAccHolderName, AccType, OpenMandate, StartDate, ExpiryDate, MaxLimit, Periodicity");
		sql.append(", PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status, ApprovalID, InputDate");
		sql.append(", Active, Reason, MandateCcy, OrgReference, DocumentName, DocumentRef, ExternalRef");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, BarCodeNumber, SwapIsActive, PrimaryMandateId");
		sql.append(", EntityCode, PartnerBankId, DefaultMandate, EMandateSource, EMandateReferenceNo");
		sql.append(", HoldReason, SwapEffectiveDate, SecurityMandate, EmployerID, EmployeeNo, ExternalMandate");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", finType, CustCIF, CustShrtName, BankCode, BranchCode");
			sql.append(", BranchDesc, BankName, City, MICR, IFSC, PccityName, UseExisting, EntityDesc");
			sql.append(", PartnerBankCode, PartnerBankName, EmpName, CustCoreBank");
		}

		sql.append(" From Mandates");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	@Override
	public List<Mandate> getMandatesForAutoSwap(long custID, Date appDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, m.MandateId, m.MandateType");
		sql.append(", fm.MandateId OldMandateId, fm.SecuritymandateId OldSecMandateId, m.SecurityMandate, m.AccNumber");
		sql.append(" From Mandates m");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = m.OrgReference and fm.CustID = ?");
		sql.append(" Where SwapIsActive = ? and SwapEffectivedate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, custID);
			ps.setBoolean(2, true);
			ps.setDate(3, JdbcUtil.getDate(appDate));
		}, (rs, rowNum) -> {
			Mandate m = new Mandate();

			m.setFinID(rs.getLong("FinID"));
			m.setMandateID(rs.getLong("MandateId"));
			m.setOldMandate(JdbcUtil.getLong(rs.getObject("OldMandateId")));
			m.setOldSecMandate(JdbcUtil.getLong(rs.getObject("OldSecMandateId")));
			m.setSecurityMandate(rs.getBoolean("SecurityMandate"));
			m.setAccNumber(rs.getString("AccNumber"));
			m.setMandateType(rs.getString("MandateType"));

			return m;
		});
	}

	@Override
	public List<Mandate> getMandatesForAutoSwap(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, m.MandateId, m.MandateType, fm.MandateId OldMandateId");
		sql.append(", fm.SecurityMandateId OldSecmandateId, m.SecurityMandate");
		sql.append(" From Mandates m");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = m.OrgReference and fm.FinID = ?");
		sql.append(" Where SwapIsActive = ? and m.MandateId <> fm.MandateId ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setBoolean(2, true);
		}, (rs, rowNum) -> {
			Mandate m = new Mandate();

			m.setFinID(rs.getLong("FinID"));
			m.setMandateID(rs.getLong("MandateId"));
			m.setOldMandate(JdbcUtil.getLong(rs.getObject("OldMandateId")));
			m.setOldSecMandate(JdbcUtil.getLong(rs.getObject("OldSecmandateId")));
			m.setSecurityMandate(rs.getBoolean("SecurityMandate"));
			m.setMandateType(rs.getString("MandateType"));

			return m;
		});
	}

	private class MandateRowMapper implements RowMapper<Mandate> {
		private String type;

		private MandateRowMapper(String type) {
			this.type = type;
		}

		@Override
		public Mandate mapRow(ResultSet rs, int rowNum) throws SQLException {
			Mandate mndts = new Mandate();

			mndts.setMandateID(rs.getLong("MandateID"));
			mndts.setCustID(rs.getLong("CustID"));
			mndts.setMandateRef(rs.getString("MandateRef"));
			mndts.setMandateType(rs.getString("MandateType"));
			mndts.setBankBranchID(JdbcUtil.getLong(rs.getObject("BankBranchID")));
			mndts.setAccNumber(rs.getString("AccNumber"));
			mndts.setAccHolderName(rs.getString("AccHolderName"));
			mndts.setJointAccHolderName(rs.getString("JointAccHolderName"));
			mndts.setAccType(rs.getString("AccType"));
			mndts.setOpenMandate(rs.getBoolean("OpenMandate"));
			mndts.setStartDate(rs.getTimestamp("StartDate"));
			mndts.setExpiryDate(rs.getTimestamp("ExpiryDate"));
			mndts.setMaxLimit(rs.getBigDecimal("MaxLimit"));
			mndts.setPeriodicity(rs.getString("Periodicity"));
			mndts.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			mndts.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			mndts.setPhoneNumber(rs.getString("PhoneNumber"));
			mndts.setStatus(rs.getString("Status"));
			mndts.setApprovalID(rs.getString("ApprovalID"));
			mndts.setInputDate(rs.getTimestamp("InputDate"));
			mndts.setActive(rs.getBoolean("Active"));
			mndts.setReason(rs.getString("Reason"));
			mndts.setMandateCcy(rs.getString("MandateCcy"));
			mndts.setOrgReference(rs.getString("OrgReference"));
			mndts.setFinReference(rs.getString("OrgReference"));
			mndts.setDocumentName(rs.getString("DocumentName"));
			mndts.setDocumentRef(JdbcUtil.getLong(rs.getObject("DocumentRef")));
			mndts.setExternalRef(rs.getString("ExternalRef"));
			mndts.setVersion(rs.getInt("Version"));
			mndts.setLastMntBy(rs.getLong("LastMntBy"));
			mndts.setLastMntOn(rs.getTimestamp("LastMntOn"));
			mndts.setRecordStatus(rs.getString("RecordStatus"));
			mndts.setRoleCode(rs.getString("RoleCode"));
			mndts.setNextRoleCode(rs.getString("NextRoleCode"));
			mndts.setTaskId(rs.getString("TaskId"));
			mndts.setNextTaskId(rs.getString("NextTaskId"));
			mndts.setRecordType(rs.getString("RecordType"));
			mndts.setWorkflowId(rs.getLong("WorkflowId"));
			mndts.setBarCodeNumber(rs.getString("BarCodeNumber"));
			mndts.setSwapIsActive(rs.getBoolean("SwapIsActive"));
			mndts.setPrimaryMandateId(rs.getLong("PrimaryMandateId"));
			mndts.setEntityCode(rs.getString("EntityCode"));
			mndts.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			mndts.setDefaultMandate(rs.getBoolean("DefaultMandate"));
			mndts.seteMandateSource(rs.getString("EMandateSource"));
			mndts.seteMandateReferenceNo(rs.getString("EMandateReferenceNo"));
			mndts.setHoldReason(rs.getString("HoldReason"));
			mndts.setSwapEffectiveDate(rs.getTimestamp("SwapEffectiveDate"));
			mndts.setSecurityMandate(rs.getBoolean("SecurityMandate"));
			mndts.setEmployerID(JdbcUtil.getLong(rs.getObject("EmployerID")));
			mndts.setEmployeeNo(rs.getString("EmployeeNo"));
			mndts.setExternalMandate(rs.getBoolean("ExternalMandate"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				mndts.setFinType(rs.getString("finType"));
				mndts.setCustCIF(rs.getString("CustCIF"));
				mndts.setCustShrtName(rs.getString("CustShrtName"));
				mndts.setBankCode(rs.getString("BankCode"));
				mndts.setBranchCode(rs.getString("BranchCode"));
				mndts.setBranchDesc(rs.getString("BranchDesc"));
				mndts.setBankName(rs.getString("BankName"));
				mndts.setCity(rs.getString("City"));
				mndts.setMICR(rs.getString("MICR"));
				mndts.setIFSC(rs.getString("IFSC"));
				mndts.setPccityName(rs.getString("PccityName"));
				mndts.setUseExisting(rs.getBoolean("UseExisting"));
				mndts.setEntityDesc(rs.getString("EntityDesc"));
				mndts.setPartnerBankCode(rs.getString("PartnerBankCode"));
				mndts.setPartnerBankName(rs.getString("PartnerBankName"));
				mndts.setEmployerName(rs.getString("EmpName"));
				mndts.setCustCoreBank(rs.getString("CustCoreBank"));
			}

			return mndts;
		}

	}

	@Override
	public PaymentInstruction getBeneficiary(long mandateId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" m.PartnerBankID, pb.PartnerBankCode, pb.PartnerBankName, bb.BankBranchID");
		sql.append(", bb.BankCode, bb.BranchDesc, bd.BankName, bb.Ifsc, pvc.PCCityName, m.AccNumber");
		sql.append(", m.AccHolderName, m.PhoneNumber, pb.AcType, pb.AccountNo, fm.FinType, fm.FinBranch");
		sql.append(" From Mandates m");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = m.OrgReference");
		sql.append(" Inner Join PartnerBanks pb on m.PartnerBankID = pb.PartnerBankID");
		sql.append(" Inner Join BankBranches bb on m.BankBranchID = bb.BankBranchID");
		sql.append(" Inner Join BMTBankDetail bd on bd.BankCode = bb.BankCode");
		sql.append(" Inner Join RMTProvincevsCity pvc on pvc.PCCity = bb.City");
		sql.append(" Where m.MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PaymentInstruction pi = new PaymentInstruction();

				pi.setPartnerBankId(rs.getLong("PartnerBankID"));
				pi.setPartnerBankCode(rs.getString("PartnerBankCode"));
				pi.setPartnerBankName(rs.getString("PartnerBankName"));
				pi.setBankBranchId(rs.getLong("BankBranchID"));
				pi.setBankBranchCode(rs.getString("BankCode"));
				pi.setBranchDesc(rs.getString("BranchDesc"));
				pi.setBankName(rs.getString("BankName"));
				pi.setBankBranchIFSC(rs.getString("Ifsc"));
				pi.setpCCityName(rs.getString("PCCityName"));
				pi.setAccountNo(rs.getString("AccNumber"));
				pi.setAcctHolderName(rs.getString("AccHolderName"));
				pi.setPhoneNumber(rs.getString("PhoneNumber"));
				pi.setPartnerBankAcType(rs.getString("AcType"));
				pi.setPartnerBankAc(rs.getString("AccountNo"));
				pi.setFinType(rs.getString("FinType"));
				pi.setFinBranch(rs.getString("FinBranch"));

				return pi;
			}, mandateId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public PaymentInstruction getBeneficiaryForSI(Long mandateId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" m.PartnerBankID, bb.BankBranchID, bb.BankCode, bb.BranchDesc,");
		sql.append(" bd.BankName, bb.Ifsc, pvc.PCCityName, m.AccNumber");
		sql.append(", m.AccHolderName, m.PhoneNumber, fm.FinType, fm.FinBranch");
		sql.append(" From Mandates m");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = m.OrgReference");
		sql.append(" Inner Join BankBranches bb on m.BankBranchID = bb.BankBranchID");
		sql.append(" Inner Join BMTBankDetail bd on bd.BankCode = bb.BankCode");
		sql.append(" Inner Join RMTProvincevsCity pvc on pvc.PCCity = bb.City");
		sql.append(" Where m.MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PaymentInstruction pi = new PaymentInstruction();

				pi.setPartnerBankId(rs.getLong("PartnerBankID"));
				pi.setBankBranchId(rs.getLong("BankBranchID"));
				pi.setBankBranchCode(rs.getString("BankCode"));
				pi.setBranchDesc(rs.getString("BranchDesc"));
				pi.setBankName(rs.getString("BankName"));
				pi.setBankBranchIFSC(rs.getString("Ifsc"));
				pi.setpCCityName(rs.getString("PCCityName"));
				pi.setAccountNo(rs.getString("AccNumber"));
				pi.setAcctHolderName(rs.getString("AccHolderName"));
				pi.setPhoneNumber(rs.getString("PhoneNumber"));
				pi.setFinType(rs.getString("FinType"));
				pi.setFinBranch(rs.getString("FinBranch"));

				return pi;
			}, mandateId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Long getMandateId(long finID) {
		String sql = "Select fm.MandateId From FinanceMain fm Inner Join Mandates m on fm.MandateId = m.MandateId Where fm.FinID = ? and m.Mandatetype in (?, ?, ?) and m.Status = ? and m.SecurityMandate = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, finID, InstrumentType.NACH.name(),
					InstrumentType.SI.name(), InstrumentType.EMANDATE.name(), "APPROVED", 0);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public BigDecimal getMaxRepayAmount(String finreference) {
		StringBuilder sql = new StringBuilder("Select Coalesce(max(RepayAmount),0) From (");
		sql.append(" Select RepayAmount From FinScheduleDetails_Temp schd");
		sql.append(" Inner Join FinanceMain_Temp fm on fm.FinId = schd.FinId and fm.finreference = ?");
		sql.append(" Union all");
		sql.append(" Select RepayAmount From FinScheduleDetails schd");
		sql.append(" Inner Join FinanceMain fm on fm.FinId = schd.FinId and fm.finreference = ?) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finreference, finreference);
	}

	@Override
	public String getMandateTypeById(Long mandateId, String string) {
		String sql = "Select MandateType From Mandates Where MandateID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, mandateId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateFinMandateId(Long mandateId, String finreference) {
		String sql = "Update FinanceMain Set SecurityMandateId = ? Where FinReference = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, mandateId, finreference);
	}

	@Override
	public Long getSecurityMandateIdByRef(String finreference) {
		String sql = "Select SecurityMandateId From FinanceMain Where Finreference = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finreference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getMandateStatus(long mandateId) {
		String sql = "SELECT STATUS FROM Mandates WHERE MandateId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, String.class, mandateId);
	}

	@Override
	public int getMandateType(long mandateId, String mandatetype, String reference) {
		String sql = "Select count(MandateId) From Mandates Where MandateId = ? and MandateType = ? and OrgReference = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, mandateId, mandatetype, reference);
	}

	@Override
	public String getMandateNumber(Long mandateId) {
		String sql = "Select AccNumber From Mandates Where MandateId = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, String.class, mandateId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}