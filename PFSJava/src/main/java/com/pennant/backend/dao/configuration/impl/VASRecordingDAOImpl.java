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
 * * FileName : VASRecordingDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * * Modified
 * Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.configuration.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>VASRecording model</b> class.<br>
 * 
 */

public class VASRecordingDAOImpl extends BasicDao<VASRecording> implements VASRecordingDAO {
	private static Logger logger = LogManager.getLogger(VASRecordingDAOImpl.class);

	public VASRecordingDAOImpl() {
		super();
	}

	@Override
	public VASRecording getVASRecording() {
		VASRecording recording = new VASRecording();

		return recording;
	}

	@Override
	public VASRecording getNewVASRecording() {
		VASRecording vASRecording = getVASRecording();

		vASRecording.setNewRecord(true);

		return vASRecording;
	}

	@Override
	public VASRecording getVASRecordingByReference(String vasReference, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where VasReference = ?");

		VASRecordingRowMapper rowMapper = new VASRecordingRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, vasReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public VASRecording getVASRecording(String vasReference, String vasStatus, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where VasReference = ? and VasStatus = ?");

		VASRecordingRowMapper rowMapper = new VASRecordingRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, vasReference, vasStatus);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private String commaJoin(List<String> finReferences) {
		return finReferences.stream().map(e -> "?").collect(Collectors.joining(","));
	}

	@Override
	public List<VASRecording> getVASRecordingsByLinkRef(String finReference, String type) {
		List<String> finReferences = new ArrayList<>();
		finReferences.add(finReference);

		return getVASRecordingsByLinkRef(finReferences, type);
	}

	@Override
	public List<VASRecording> getVASRecordingsByLinkRef(List<String> finReferences, String type) {
		if (CollectionUtils.isEmpty(finReferences)) {
			return new ArrayList<>();
		}

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PrimaryLinkRef In(");
		sql.append(commaJoin(finReferences));
		sql.append(")");

		VASRecordingRowMapper rowMapper = new VASRecordingRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (String finReference : finReferences) {
				ps.setString(index++, finReference);
			}
		}, rowMapper);
	}

	@Override
	public void delete(VASRecording vasR, String type) {
		StringBuilder sql = new StringBuilder("Delete From VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index, vasR.getVasReference());
			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteByPrimaryLinkRef(String primaryLinkRef, String type) {
		StringBuilder sql = new StringBuilder("Delete From VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PrimaryLinkRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index, primaryLinkRef);
		});
	}

	@Override
	public String save(VASRecording vasR, String type) {
		StringBuilder sql = new StringBuilder("Insert Into VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode");
		sql.append(", ValueDate, AccrualTillDate, RecurringDate, EntityCode, TermInsuranceLien, ProviderName");
		sql.append(", PolicyNumber, MedicalApplicable, MedicalStatus, DsaId, DmaId, FulfilOfficerId, ReferralId");
		sql.append(", Status, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, VasStatus, FinanceProcess, PaidAmt, WaivedAmt, Remarks");
		sql.append(", Reason, CancelAmt, ServiceReqNumber, CancelAfterFLP, OldVasReference");
		sql.append(", ManualAdviseId, ReceivableAdviseId)");
		sql.append("  Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, vasR.getProductCode());
			ps.setString(index++, vasR.getPostingAgainst());
			ps.setString(index++, vasR.getPrimaryLinkRef());
			ps.setString(index++, vasR.getVasReference());
			ps.setBigDecimal(index++, vasR.getFee());
			ps.setBigDecimal(index++, vasR.getRenewalFee());
			ps.setString(index++, vasR.getFeePaymentMode());
			ps.setDate(index++, JdbcUtil.getDate(vasR.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(vasR.getAccrualTillDate()));
			ps.setDate(index++, JdbcUtil.getDate(vasR.getRecurringDate()));
			ps.setString(index++, vasR.getEntityCode());
			ps.setBoolean(index++, vasR.isTermInsuranceLien());
			ps.setString(index++, vasR.getProviderName());
			ps.setString(index++, vasR.getPolicyNumber());
			ps.setBoolean(index++, vasR.isMedicalApplicable());
			ps.setString(index++, vasR.getMedicalStatus());
			ps.setString(index++, vasR.getDsaId());
			ps.setString(index++, vasR.getDmaId());
			ps.setString(index++, vasR.getFulfilOfficerId());
			ps.setString(index++, vasR.getReferralId());
			ps.setString(index++, vasR.getStatus());
			ps.setInt(index++, vasR.getVersion());
			ps.setLong(index++, vasR.getLastMntBy());
			ps.setTimestamp(index++, vasR.getLastMntOn());
			ps.setString(index++, vasR.getRecordStatus());
			ps.setString(index++, vasR.getRoleCode());
			ps.setString(index++, vasR.getNextRoleCode());
			ps.setString(index++, vasR.getTaskId());
			ps.setString(index++, vasR.getNextTaskId());
			ps.setString(index++, vasR.getRecordType());
			ps.setLong(index++, vasR.getWorkflowId());
			ps.setString(index++, vasR.getVasStatus());
			ps.setBoolean(index++, vasR.isFinanceProcess());
			ps.setBigDecimal(index++, vasR.getPaidAmt());
			ps.setBigDecimal(index++, vasR.getWaivedAmt());
			ps.setString(index++, vasR.getRemarks());
			ps.setString(index++, vasR.getReason());
			ps.setBigDecimal(index++, vasR.getCancelAmt());
			ps.setString(index++, vasR.getServiceReqNumber());
			ps.setBoolean(index++, vasR.isCancelAfterFLP());
			ps.setString(index++, vasR.getOldVasReference());
			ps.setLong(index++, vasR.getManualAdviseId());
			ps.setLong(index, vasR.getReceivableAdviseId());

		});

		return vasR.getVasReference();
	}

	@Override
	public void update(VASRecording vasR, String type) {
		StringBuilder sql = new StringBuilder("Update VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ProductCode = ?, PostingAgainst = ?, PrimaryLinkRef = ?, Fee = ?, RenewalFee = ?");
		sql.append(", FeePaymentMode = ?, TermInsuranceLien = ?, ProviderName = ?, PolicyNumber = ?");
		sql.append(", MedicalApplicable = ?, MedicalStatus = ?, ValueDate = ?, AccrualTillDate = ?, EntityCode = ?");
		sql.append(", RecurringDate = ?, DsaId = ?, DmaId = ?, FulfilOfficerId = ?, ReferralId = ?");
		sql.append(", Remarks = ?, Reason = ?, CancelAmt = ?, ServiceReqNumber = ?");
		sql.append(", CancelAfterFLP = ?, OldVasReference = ?, ManualAdviseId = ?, ReceivableAdviseId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?, VasStatus = ?, FinanceProcess = ?, PaidAmt = ?, WaivedAmt = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(" Where VasReference = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, vasR.getProductCode());
			ps.setString(index++, vasR.getPostingAgainst());
			ps.setString(index++, vasR.getPrimaryLinkRef());
			ps.setBigDecimal(index++, vasR.getFee());
			ps.setBigDecimal(index++, vasR.getRenewalFee());
			ps.setString(index++, vasR.getFeePaymentMode());
			ps.setBoolean(index++, vasR.isTermInsuranceLien());
			ps.setString(index++, vasR.getProviderName());
			ps.setString(index++, vasR.getPolicyNumber());
			ps.setBoolean(index++, vasR.isMedicalApplicable());
			ps.setString(index++, vasR.getMedicalStatus());
			ps.setDate(index++, JdbcUtil.getDate(vasR.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(vasR.getAccrualTillDate()));
			ps.setString(index++, vasR.getEntityCode());
			ps.setDate(index++, JdbcUtil.getDate(vasR.getRecurringDate()));
			ps.setString(index++, vasR.getDsaId());
			ps.setString(index++, vasR.getDmaId());
			ps.setString(index++, vasR.getFulfilOfficerId());
			ps.setString(index++, vasR.getReferralId());
			ps.setString(index++, vasR.getRemarks());
			ps.setString(index++, vasR.getReason());
			ps.setBigDecimal(index++, vasR.getCancelAmt());
			ps.setString(index++, vasR.getServiceReqNumber());
			ps.setBoolean(index++, vasR.isCancelAfterFLP());
			ps.setString(index++, vasR.getOldVasReference());
			ps.setLong(index++, vasR.getManualAdviseId());
			ps.setLong(index++, vasR.getReceivableAdviseId());
			ps.setString(index++, vasR.getRecordType());
			ps.setLong(index++, vasR.getWorkflowId());
			ps.setString(index++, vasR.getVasStatus());
			ps.setBoolean(index++, vasR.isFinanceProcess());
			ps.setBigDecimal(index++, vasR.getPaidAmt());
			ps.setBigDecimal(index++, vasR.getWaivedAmt());
			ps.setInt(index++, vasR.getVersion());
			ps.setLong(index++, vasR.getLastMntBy());
			ps.setTimestamp(index++, vasR.getLastMntOn());
			ps.setString(index++, vasR.getRecordStatus());
			ps.setString(index++, vasR.getRoleCode());
			ps.setString(index++, vasR.getNextRoleCode());
			ps.setString(index++, vasR.getTaskId());
			ps.setString(index++, vasR.getNextTaskId());

			ps.setString(index++, vasR.getVasReference());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, vasR.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public VasCustomer getVasCustomerCif(String primaryLinkRef, String postingAgainst) {
		StringBuilder sql = new StringBuilder();
		if (VASConsatnts.VASAGAINST_FINANCE.equals(postingAgainst)) {
			sql.append(" Select CU.CustID CustomerId, CU.CustCIF, CU.CustShrtName");
			sql.append(" From FinanceMain FM Inner Join Customers CU ON FM.CustID = CU.CustID");
			sql.append(" Where FM.FinReference = ?");
		} else if (VASConsatnts.VASAGAINST_COLLATERAL.equals(postingAgainst)) {
			sql.append(" Select CU.CustID CustomerId, CU.CustCIF, CU.CustShrtName");
			sql.append(" From CollateralSetup CO Inner Join Customers CU ON CO.DepositorId = CU.CustID");
			sql.append(" Where CO.CollateralRef = ?");
		} else if (VASConsatnts.VASAGAINST_CUSTOMER.equals(postingAgainst)) {
			sql.append(" Select CustID CustomerId, CustCIF, CustShrtName From Customers");
			sql.append(" Where CustCIF = ?");
		}

		if (StringUtils.isEmpty(sql.toString())) {
			return null;
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				VasCustomer vc = new VasCustomer();

				vc.setCustomerId(rs.getLong("CustomerId"));
				vc.setCustCIF(rs.getString("CustCIF"));
				vc.setCustShrtName(rs.getString("CustShrtName"));

				return vc;
			}, primaryLinkRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateVasStatus(String status, String vasReference) {
		String sql = "Update VASRecording Set Status = ? Where VasReference = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, status);
			ps.setString(index, vasReference);
		});
	}

	@Override
	public void updateVasStatus(String reference, long paymentInsId) {
		String sql = "Update VASRecording Set PaymentInsId = ? Where VasReference = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, paymentInsId);
			ps.setString(index, reference);
		});
	}

	@Override
	public List<VASRecording> getVASRecordingsStatusByReference(String primaryLinkRef, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee");
		sql.append(", FeePaymentMode, vr.EntityCode, TermInsuranceLien, ProviderName");
		sql.append(", PolicyNumber, MedicalApplicable, MedicalStatus");
		sql.append(", ValueDate, AccrualTillDate, RecurringDate, DsaId, DmaId, FulfilOfficerId, ReferralId");
		sql.append(", VasStatus, FinanceProcess, PaidAmt, WaivedAmt, vr.Status, OldVasReference");
		sql.append(", ManualAdviseId, PaymentInsId, ReceivableAdviseId, ip.Status InsStatus");
		sql.append(" From VASRecording vr");
		sql.append(" Inner Join InsurancePaymentInstructions ip on vr.paymentinsid = ip.ID");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PrimaryLinkRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index, primaryLinkRef);
		}, (rs, rowNum) -> {
			VASRecording vas = new VASRecording();

			vas.setProductCode(rs.getString("ProductCode"));
			vas.setPostingAgainst(rs.getString("PostingAgainst"));
			vas.setPrimaryLinkRef(rs.getString("PrimaryLinkRef"));
			vas.setVasReference(rs.getString("VasReference"));
			vas.setFee(rs.getBigDecimal("Fee"));
			vas.setRenewalFee(rs.getBigDecimal("RenewalFee"));
			vas.setFeePaymentMode(rs.getString("FeePaymentMode"));
			vas.setEntityCode(rs.getString("EntityCode"));
			vas.setTermInsuranceLien(rs.getBoolean("TermInsuranceLien"));
			vas.setProviderName(rs.getString("ProviderName"));
			vas.setPolicyNumber(rs.getString("PolicyNumber"));
			vas.setMedicalApplicable(rs.getBoolean("MedicalApplicable"));
			vas.setMedicalStatus(rs.getString("MedicalStatus"));
			vas.setValueDate(rs.getTimestamp("ValueDate"));
			vas.setAccrualTillDate(rs.getTimestamp("AccrualTillDate"));
			vas.setRecurringDate(rs.getTimestamp("RecurringDate"));
			vas.setDsaId(rs.getString("DsaId"));
			vas.setDmaId(rs.getString("DmaId"));
			vas.setFulfilOfficerId(rs.getString("FulfilOfficerId"));
			vas.setReferralId(rs.getString("ReferralId"));
			vas.setVasStatus(rs.getString("VasStatus"));
			vas.setFinanceProcess(rs.getBoolean("FinanceProcess"));
			vas.setPaidAmt(rs.getBigDecimal("PaidAmt"));
			vas.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			vas.setStatus(rs.getString("Status"));
			vas.setOldVasReference(rs.getString("OldVasReference"));
			vas.setManualAdviseId(rs.getLong("ManualAdviseId"));
			vas.setPaymentInsId(rs.getLong("PaymentInsId"));
			vas.setReceivableAdviseId(rs.getLong("ReceivableAdviseId"));
			vas.setInsStatus(rs.getString("InsStatus"));

			return vas;
		});
	}

	@Override
	public Long getCustomerId(String vasReference) {
		StringBuilder sql = new StringBuilder("Select Distinct CustId  From (");
		sql.append(" Select fm.CustId, VasReference");
		sql.append(" From VASRecording_Temp vr");
		sql.append(" Inner Join FinanceMain_Temp fm on fm.FinReference = vr.PrimaryLinkRef and PostingAgainst = ?");
		sql.append(" Union All");
		sql.append(" Select fm.CustId, VasReference");
		sql.append(" From VASRecording vr");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = vr.PrimaryLinkRef and PostingAgainst = ?");
		sql.append(" Union All");
		sql.append(" Select cu.CustId, VasReference");
		sql.append(" From VASRecording_Temp vr");
		sql.append(" Inner Join Customers_Temp cu on cu.CustCIF = vr.PrimaryLinkRef and PostingAgainst = ?");
		sql.append(" Union All");
		sql.append(" Select cu.CustId, VasReference");
		sql.append(" From VASRecording vr");
		sql.append(" Inner Join Customers cu on cu.CustCIF = vr.PrimaryLinkRef and PostingAgainst = ?");
		sql.append(" ) T Where T.VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> rs.getLong("CustId"),
					new Object[] { "Finance", "Finance", "Customer", "Customer", vasReference });
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updatePaidAmt(String vasReference, String primaryLinkRef, BigDecimal paidAmt, String type) {
		StringBuilder sql = new StringBuilder("Update VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PaidAmt = ?");
		sql.append(" Where VasReference = ? and PrimaryLinkRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, paidAmt);
			ps.setString(index++, vasReference);
			ps.setString(index, primaryLinkRef);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<VASRecording> getLoanReportVasRecordingByRef(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" vr.Fee, vs.ModeofPayment, vr.Productcode");
		sql.append(" From VasRecording vr");
		sql.append(" Inner Join VasStructure vs on vr.Productcode = vs.Productcode");
		sql.append(" Where PrimaryLinkRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index, finReference);
		}, (rs, rowNum) -> {
			VASRecording vr = new VASRecording();

			vr.setFee(rs.getBigDecimal("Fee"));
			// vasR.setModeofPayment(rs.getString("ModeofPayment"));
			vr.setProductCode(rs.getString("Productcode"));

			return vr;
		});
	}

	public Long getPaymentInsId(String vasReference, String type) {
		StringBuilder sql = new StringBuilder("Select PaymentInsId");
		sql.append(" From VasRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, vasReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public String getVasInsStatus(long paymentInsId) {
		String sql = "Select Status From InsurancePaymentInstructions Where Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, paymentInsId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void updateVasInsStatus(long id) {
		StringBuilder sql = new StringBuilder("Update InsurancePaymentInstructions");
		sql.append(" Set Status = (case when Status = ? then ? When Status = ? then ?");
		sql.append(" else Status end)");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, DisbursementConstants.STATUS_APPROVED);
			ps.setString(index++, DisbursementConstants.STATUS_CANCEL);
			ps.setString(index++, DisbursementConstants.STATUS_PAID);
			ps.setString(index++, DisbursementConstants.STATUS_REVERSED);

			ps.setLong(index, id);
		});
	}

	@Override
	public String getProductCodeByReference(String primaryLinkRef, String vasReference) {
		String sql = "Select ProductDesc From VasRecording_View Where PrimaryLinkRef = ? and vasReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, primaryLinkRef, vasReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode");
		sql.append(", EntityCode, TermInsuranceLien, ProviderName, PolicyNumber, MedicalApplicable");
		sql.append(", MedicalStatus, ValueDate, AccrualTillDate, RecurringDate, DsaId, DmaId, FulfilOfficerId");
		sql.append(", ReferralId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, VasStatus, FinanceProcess, PaidAmt");
		sql.append(", WaivedAmt, Status, Remarks, Reason, CancelAmt, ServiceReqNumber, CancelAfterFLP");
		sql.append(", OldVasReference, ManualAdviseId, PaymentInsId, ReceivableAdviseId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ProductDesc, DsaIdDesc, DmaIDDesc, FulfilOfficerIdDesc");
			sql.append(", ReferralIdDesc, ProductType, ProductTypeDesc, ProductCtg");
			sql.append(", ProductCtgDesc, ManufacturerDesc, FinType, FlpDays, FeeAccounting");
		}

		sql.append(" From VASRecording");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class VASRecordingRowMapper implements RowMapper<VASRecording> {
		private String type;

		public VASRecordingRowMapper(String type) {
			this.type = type;
		}

		@Override
		public VASRecording mapRow(ResultSet rs, int rowNum) throws SQLException {
			VASRecording vas = new VASRecording();

			vas.setProductCode(rs.getString("ProductCode"));
			vas.setPostingAgainst(rs.getString("PostingAgainst"));
			vas.setPrimaryLinkRef(rs.getString("PrimaryLinkRef"));
			vas.setVasReference(rs.getString("VasReference"));
			vas.setFee(rs.getBigDecimal("Fee"));
			vas.setRenewalFee(rs.getBigDecimal("RenewalFee"));
			vas.setFeePaymentMode(rs.getString("FeePaymentMode"));
			vas.setEntityCode(rs.getString("EntityCode"));
			vas.setTermInsuranceLien(rs.getBoolean("TermInsuranceLien"));
			vas.setProviderName(rs.getString("ProviderName"));
			vas.setPolicyNumber(rs.getString("PolicyNumber"));
			vas.setMedicalApplicable(rs.getBoolean("MedicalApplicable"));
			vas.setMedicalStatus(rs.getString("MedicalStatus"));
			vas.setValueDate(rs.getTimestamp("ValueDate"));
			vas.setAccrualTillDate(rs.getTimestamp("AccrualTillDate"));
			vas.setRecurringDate(rs.getTimestamp("RecurringDate"));
			vas.setDsaId(rs.getString("DsaId"));
			vas.setDmaId(rs.getString("DmaId"));
			vas.setFulfilOfficerId(rs.getString("FulfilOfficerId"));
			vas.setReferralId(rs.getString("ReferralId"));
			vas.setVersion(rs.getInt("Version"));
			vas.setLastMntBy(rs.getLong("LastMntBy"));
			vas.setLastMntOn(rs.getTimestamp("LastMntOn"));
			vas.setRecordStatus(rs.getString("RecordStatus"));
			vas.setRoleCode(rs.getString("RoleCode"));
			vas.setNextRoleCode(rs.getString("NextRoleCode"));
			vas.setTaskId(rs.getString("TaskId"));
			vas.setNextTaskId(rs.getString("NextTaskId"));
			vas.setRecordType(rs.getString("RecordType"));
			vas.setWorkflowId(rs.getLong("WorkflowId"));
			vas.setVasStatus(rs.getString("VasStatus"));
			vas.setFinanceProcess(rs.getBoolean("FinanceProcess"));
			vas.setPaidAmt(rs.getBigDecimal("PaidAmt"));
			vas.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			vas.setStatus(rs.getString("Status"));
			vas.setRemarks(rs.getString("Remarks"));
			vas.setReason(rs.getString("Reason"));
			vas.setCancelAmt(rs.getBigDecimal("CancelAmt"));
			vas.setServiceReqNumber(rs.getString("ServiceReqNumber"));
			vas.setCancelAfterFLP(rs.getBoolean("CancelAfterFLP"));
			vas.setOldVasReference(rs.getString("OldVasReference"));
			vas.setManualAdviseId(rs.getLong("ManualAdviseId"));
			vas.setPaymentInsId(rs.getLong("PaymentInsId"));
			vas.setReceivableAdviseId(rs.getLong("ReceivableAdviseId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				vas.setProductDesc(rs.getString("ProductDesc"));
				vas.setDsaIdDesc(rs.getString("DsaIdDesc"));
				vas.setDmaIdDesc(rs.getString("DmaIdDesc"));
				vas.setFulfilOfficerIdDesc(rs.getString("FulfilOfficerIdDesc"));
				vas.setReferralIdDesc(rs.getString("ReferralIdDesc"));
				vas.setProductType(rs.getString("ProductType"));
				vas.setProductTypeDesc(rs.getString("ProductTypeDesc"));
				vas.setProductCtg(rs.getString("ProductCtg"));
				vas.setProductCtgDesc(rs.getString("ProductCtgDesc"));
				vas.setManufacturerDesc(rs.getString("ManufacturerDesc"));
				vas.setFinType(rs.getString("FinType"));
				vas.setFlpDays(rs.getInt("FlpDays"));
				vas.setFeeAccounting(rs.getInt("FeeAccounting"));
			}

			return vas;
		}
	}
}