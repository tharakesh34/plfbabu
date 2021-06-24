/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinAdvancePaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinAdvancePayments model</b> class.<br>
 * 
 */

public class FinAdvancePaymentsDAOImpl extends SequenceDao<FinAdvancePayments> implements FinAdvancePaymentsDAO {
	private static Logger logger = LogManager.getLogger(FinAdvancePaymentsDAOImpl.class);

	public FinAdvancePaymentsDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinAdvancePayments
	 * 
	 * @return FinAdvancePayments
	 */

	@Override
	public FinAdvancePayments getFinAdvancePayments() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinAdvancePayments");
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		if (workFlowDetails != null) {
			finAdvancePayments.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finAdvancePayments;
	}

	/**
	 * This method get the module from method getFinAdvancePayments() and set the new record flag as true and return
	 * FinAdvancePayments()
	 * 
	 * @return FinAdvancePayments
	 */

	@Override
	public FinAdvancePayments getNewFinAdvancePayments() {
		logger.debug("Entering");
		FinAdvancePayments finAdvancePayments = getFinAdvancePayments();
		finAdvancePayments.setNewRecord(true);
		logger.debug("Leaving");
		return finAdvancePayments;
	}

	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PaymentId = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper(type);

		logger.trace(Literal.SQL + sql.toString());

		long paymentId = finAdvancePayments.getPaymentId();
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentId }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinAdvancePayments{} table for the specified PaymentId >> {}", type,
					paymentId);
		}

		return null;

	}
	
	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(long paymentId) {
		StringBuilder sql = getSqlQuery("");
		sql.append(" Where PaymentId = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper("");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentId }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinAdvancePayments table for the specified PaymentId >> {}", paymentId);
		}

		return null;

	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PaymentId, FinReference, PaymentSeq, DisbSeq, PaymentDetail, AmtToBeReleased");
		sql.append(", LiabilityHoldName, BeneficiaryName, BeneficiaryAccNo, ReEnterBeneficiaryAccNo");
		sql.append(", Description, PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution");
		sql.append(", Remarks, BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode");
		sql.append(", PhoneAreaCode, PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy");
		sql.append(", VasReference ");
		sql.append(", POIssued, PartnerBankID, TransactionRef, RealizationDate, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", LinkedTranId, RecordType, WorkflowId, HoldDisbursement");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BranchCode, BranchBankCode, BranchBankName, BranchDesc");
			sql.append(", BankName, City, IFSC, PartnerbankCode, PartnerBankName, PartnerBankAcType");
			sql.append(", PartnerBankAc, PrintingLocDesc, RejectReason");
		}

		sql.append(" from FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(final String id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinReference = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper(type);

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, id);

		}, rowMapper);
	}

	/**
	 * This method Deletes the Record from the FinAdvancePayments or FinAdvancePayments_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete by key LoanRefNumber
	 * 
	 * @param Goods
	 *            Details (FinAdvancePayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From FinAdvancePayments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PaymentId =:PaymentId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinAdvancePayments or FinAdvancePayments_Temp.
	 * 
	 * save Goods Details
	 * 
	 * @param Goods
	 *            Details (FinAdvancePayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");
		if (finAdvancePayments.getPaymentId() == Long.MIN_VALUE) {
			finAdvancePayments.setPaymentId(getNextValue("SeqAdvpayment"));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinAdvancePayments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PaymentId,FinReference, PaymentSeq ,DisbSeq, PaymentDetail, AmtToBeReleased,");
		insertSql.append(
				" LiabilityHoldName, BeneficiaryName, BeneficiaryAccNo,reEnterBeneficiaryAccNo,Description, PaymentType, ");
		insertSql.append("  LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks,BankCode, ");
		insertSql.append(" PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode, ");
		insertSql.append(
				" PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy,POIssued,PartnerBankID,LinkedTranId,TransactionRef, RealizationDate, ");
		insertSql.append(" VasReference, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, HoldDisbursement)");
		insertSql.append(" Values(:PaymentId, :FinReference, :PaymentSeq ,:DisbSeq, :PaymentDetail, :AmtToBeReleased,");
		insertSql.append(
				"  :LiabilityHoldName, :BeneficiaryName,:BeneficiaryAccNo, :reEnterBeneficiaryAccNo, :Description, :PaymentType, ");
		insertSql.append(" :LlReferenceNo, :LlDate, :CustContribution,:SellerContribution, :Remarks, :BankCode,");
		insertSql.append(" :PayableLoc, :PrintingLoc, :ValueDate, :BankBranchID, :PhoneCountryCode, :PhoneAreaCode, ");
		insertSql.append(
				" :PhoneNumber, :ClearingDate, :Status, :Active, :InputDate, :DisbCCy, :POIssued, :PartnerBankID, :LinkedTranId, :TransactionRef, :RealizationDate ,");
		insertSql.append(" :VasReference, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :HoldDisbursement)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finAdvancePayments.getFinReference();
	}

	/**
	 * This method updates the Record FinAdvancePayments or FinAdvancePayments_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Details by key LoanRefNumber and Version
	 * 
	 * @param Goods
	 *            Details (FinAdvancePayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinAdvancePayments finAdvancePayments, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinAdvancePayments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set PaymentDetail = :PaymentDetail,");
		updateSql.append(" AmtToBeReleased = :AmtToBeReleased, LiabilityHoldName = :LiabilityHoldName,");
		updateSql.append(" BeneficiaryName = :BeneficiaryName, DisbSeq =:DisbSeq,");
		updateSql.append(
				" BeneficiaryAccNo = :BeneficiaryAccNo, reEnterBeneficiaryAccNo = :reEnterBeneficiaryAccNo, Description = :Description,");
		updateSql.append(" PaymentType = :PaymentType, LlReferenceNo = :LlReferenceNo,");
		updateSql.append(" LlDate = :LlDate, CustContribution = :CustContribution,");
		updateSql.append(" SellerContribution = :SellerContribution, Remarks = :Remarks,");
		updateSql.append(" BankCode = :BankCode, PayableLoc = :PayableLoc, PrintingLoc = :PrintingLoc,");
		updateSql.append(" ValueDate = :ValueDate, BankBranchID = :BankBranchID,");
		updateSql.append(" PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode,");
		updateSql.append(" PhoneNumber = :PhoneNumber, ClearingDate = ClearingDate, Status = :Status,");
		updateSql.append(
				" Active = :Active, InputDate = :InputDate, DisbCCy = :DisbCCy, POIssued = :POIssued, PartnerBankID =:PartnerBankID,TransactionRef = :TransactionRef, RealizationDate = :RealizationDate, ");
		updateSql.append(" VasReference = :VasReference, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,");
		updateSql.append("  HoldDisbursement = :HoldDisbursement,LinkedTranId = :LinkedTranId");
		updateSql.append("  Where PaymentId = :PaymentId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateStatus(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinAdvancePayments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set Status = :Status");
		updateSql.append("  Where FinReference = :FinReference and DisbSeq = :DisbSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updatePaymentStatus(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinAdvancePayments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set Status = :Status");
		updateSql.append("  Where FinReference = :FinReference and PaymentId = :PaymentId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void deleteByFinRef(String loanReference, String tableType) {
		logger.debug("Entering");
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setFinReference(loanReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinAdvancePayments");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public int getBranch(long bankBranchID, String type) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setBankBranchID(bankBranchID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankBranchID =:BankBranchID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getAdvancePaymentsCountByPartnerBank(long partnerBankID, String type) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setPartnerBankID(partnerBankID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankID =:partnerBankID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public void update(long paymentId, long linkedTranId) {
		int recordCount = 0;
		logger.debug("Entering");
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setLinkedTranId(linkedTranId);
		finAdvancePayments.setPaymentId(paymentId);

		StringBuilder updateSql = new StringBuilder("Update FinAdvancePayments");
		updateSql.append(" Set LinkedTranId = :LinkedTranId ");
		updateSql.append(" Where PaymentId = PaymentId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public int updateDisbursmentStatus(FinAdvancePayments fap) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FINADVANCEPAYMENTS Set");
		sql.append(" Status = ?, ClearingDate = ?, TransactionRef = ?, RealizationDate = ?, RejectReason = ?");

		String paymentType = fap.getPaymentType();
		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
			sql.append(", LlReferenceNo = ?, LlDate = ?");
		}

		if (fap.getLastMntOn() != null) {
			sql.append(", LastMntOn = ?");
		}

		sql.append("  Where PaymentId = ? and Status = ?");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fap.getStatus());
			ps.setDate(index++, JdbcUtil.getDate(fap.getClearingDate()));
			ps.setString(index++, fap.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(fap.getRealizationDate()));
			ps.setString(index++, fap.getRejectReason());

			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
				ps.setString(index++, fap.getLlReferenceNo());
				ps.setDate(index++, JdbcUtil.getDate(fap.getLlDate()));
			}

			if (fap.getLastMntOn() != null) {
				ps.setDate(index++, JdbcUtil.getDate(fap.getLastMntOn()));
			}

			ps.setLong(index++, fap.getPaymentId());
			ps.setString(index++, DisbursementConstants.STATUS_AWAITCON);

		});

	}

	@Override
	public int getBankCode(String bankCode, String type) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getMaxPaymentSeq(String finReference) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("select Max(paymentseq)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		int recordCount = 0;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);

		logger.debug("Leaving");
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (Exception dae) {
			recordCount = 0;
		}

		return recordCount;
	}

	@Override
	public int getFinAdvCountByRef(String finReference, String type) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setFinReference(finReference);

		List<String> status = new ArrayList<>();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("select COUNT(*)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(type);
		selectSql.append(" Where FinReference = :FinReference");

		if (ImplementationConstants.ALW_QDP_CUSTOMIZATION) {
			status.add(DisbursementConstants.STATUS_APPROVED);
			status.add(DisbursementConstants.STATUS_AWAITCON);
			status.add(DisbursementConstants.STATUS_PAID);
			status.add(DisbursementConstants.STATUS_REALIZED);

			selectSql.append(" AND Status  in(:Status)");
			paramSource.addValue("Status", status);
		}

		logger.debug("selectSql: " + selectSql.toString());
		int recordCount = 0;
		paramSource.addValue("FinReference", finReference);

		logger.debug("Leaving");
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), paramSource, Integer.class);
		} catch (Exception dae) {
			recordCount = 0;
		}

		return recordCount;
	}

	/**
	 * Method for Fetching Count for Assigned partnerBankId to Different Finances/Commitments
	 */
	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		logger.debug("Entering");

		int assignedCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder(" Select Count(1) ");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankId = :PartnerBankId ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			assignedCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			assignedCount = 0;
		}
		logger.debug("Leaving");
		return assignedCount;
	}

	@Override
	public int getCountByFinReference(String finReference) {
		StringBuilder sql = new StringBuilder("select Max(PaymentSeq)");
		sql.append(" From FinAdvancePayments");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, Integer.class);
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(String finRefernce, Date toDate, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinReference = ? and LlDate = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper(type);

		logger.trace(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finRefernce);
					ps.setDate(index++, JdbcUtil.getDate(toDate));
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void updateLinkedTranId(FinAdvancePayments finAdvancePayments) {
		StringBuilder sql = new StringBuilder("Update FinAdvancePayments");
		sql.append(" Set LinkedTranId = :linkedTranId");
		sql.append(" Where PaymentId = :PaymentId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	@Override
	public int getCountByPaymentId(String finReference, long paymentId) {
		StringBuilder sql = new StringBuilder("select COUNT(*)");
		sql.append(" From FinAdvancePayments");
		sql.append(" Where FinReference = ? AND PaymentID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, paymentId },
				Integer.class);
	}

	@Override
	public int getFinAdvanceByVasRef(String finReference, String vasReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder(" Select Count(1) ");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference AND VasReference = :VasReference ");

		logger.debug(Literal.SQL + selectSql.toString());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("VasReference", vasReference);

		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), parameterSource, Integer.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return count;
	}

	private class FinAdvancePaymentsRowMapper implements RowMapper<FinAdvancePayments> {
		private String type;

		private FinAdvancePaymentsRowMapper(String type) {
			this.type = type;
		}

		@Override
		public FinAdvancePayments mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinAdvancePayments finAdvPayments = new FinAdvancePayments();

			finAdvPayments.setPaymentId(rs.getLong("PaymentId"));
			finAdvPayments.setFinReference(rs.getString("FinReference"));
			finAdvPayments.setPaymentSeq(rs.getInt("PaymentSeq"));
			finAdvPayments.setDisbSeq(rs.getInt("DisbSeq"));
			finAdvPayments.setPaymentDetail(rs.getString("PaymentDetail"));
			finAdvPayments.setAmtToBeReleased(rs.getBigDecimal("AmtToBeReleased"));
			finAdvPayments.setLiabilityHoldName(rs.getString("LiabilityHoldName"));
			finAdvPayments.setBeneficiaryName(rs.getString("BeneficiaryName"));
			finAdvPayments.setBeneficiaryAccNo(rs.getString("BeneficiaryAccNo"));
			finAdvPayments.setReEnterBeneficiaryAccNo(rs.getString("ReEnterBeneficiaryAccNo"));
			finAdvPayments.setDescription(rs.getString("Description"));
			finAdvPayments.setPaymentType(rs.getString("PaymentType"));
			finAdvPayments.setLLReferenceNo(rs.getString("LlReferenceNo"));
			finAdvPayments.setLLDate(rs.getTimestamp("LlDate"));
			finAdvPayments.setCustContribution(rs.getBigDecimal("CustContribution"));
			finAdvPayments.setSellerContribution(rs.getBigDecimal("SellerContribution"));
			finAdvPayments.setRemarks(rs.getString("Remarks"));
			finAdvPayments.setBankCode(rs.getString("BankCode"));
			finAdvPayments.setPayableLoc(rs.getString("PayableLoc"));
			finAdvPayments.setPrintingLoc(rs.getString("PrintingLoc"));
			finAdvPayments.setValueDate(rs.getTimestamp("ValueDate"));
			finAdvPayments.setBankBranchID(rs.getLong("BankBranchID"));
			finAdvPayments.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			finAdvPayments.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			finAdvPayments.setPhoneNumber(rs.getString("PhoneNumber"));
			finAdvPayments.setClearingDate(rs.getTimestamp("ClearingDate"));
			finAdvPayments.setStatus(rs.getString("Status"));
			finAdvPayments.setActive(rs.getBoolean("Active"));
			finAdvPayments.setInputDate(rs.getTimestamp("InputDate"));
			finAdvPayments.setDisbCCy(rs.getString("DisbCCy"));
			finAdvPayments.setVasReference(rs.getString("VasReference"));
			finAdvPayments.setpOIssued(rs.getBoolean("POIssued"));
			finAdvPayments.setPartnerBankID(rs.getLong("PartnerBankID"));
			finAdvPayments.setTransactionRef(rs.getString("TransactionRef"));
			finAdvPayments.setRealizationDate(rs.getTimestamp("RealizationDate"));
			finAdvPayments.setVersion(rs.getInt("Version"));
			finAdvPayments.setLastMntBy(rs.getLong("LastMntBy"));
			finAdvPayments.setLastMntOn(rs.getTimestamp("LastMntOn"));
			finAdvPayments.setRecordStatus(rs.getString("RecordStatus"));
			finAdvPayments.setRoleCode(rs.getString("RoleCode"));
			finAdvPayments.setNextRoleCode(rs.getString("NextRoleCode"));
			finAdvPayments.setTaskId(rs.getString("TaskId"));
			finAdvPayments.setNextTaskId(rs.getString("NextTaskId"));
			finAdvPayments.setLinkedTranId(rs.getLong("LinkedTranId"));
			finAdvPayments.setRecordType(rs.getString("RecordType"));
			finAdvPayments.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				finAdvPayments.setBranchCode(rs.getString("BranchCode"));
				finAdvPayments.setBranchBankCode(rs.getString("BranchBankCode"));
				finAdvPayments.setBranchBankName(rs.getString("BranchBankName"));
				finAdvPayments.setBranchDesc(rs.getString("BranchDesc"));
				finAdvPayments.setBankName(rs.getString("BankName"));
				finAdvPayments.setCity(rs.getString("City"));
				finAdvPayments.setiFSC(rs.getString("IFSC"));
				finAdvPayments.setPartnerbankCode(rs.getString("PartnerbankCode"));
				finAdvPayments.setPartnerBankName(rs.getString("PartnerBankName"));
				finAdvPayments.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
				finAdvPayments.setPartnerBankAc(rs.getString("PartnerBankAc"));
				finAdvPayments.setPrintingLocDesc(rs.getString("PrintingLocDesc"));
				finAdvPayments.setRejectReason(rs.getString("RejectReason"));
			}

			return finAdvPayments;

		}
	}

	@Override
	public void updateLLDate(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinAdvancePayments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set LLDate = :llDate, Status = :status, LinkedTranId = :linkedTranId");
		updateSql.append("  Where FinReference = :FinReference and PaymentSeq = :paymentSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
}