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

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinAdvancePayments model</b> class.<br>
 * 
 */

public class FinAdvancePaymentsDAOImpl extends BasisNextidDaoImpl<FinAdvancePayments> implements FinAdvancePaymentsDAO {

	private static Logger	logger	= Logger.getLogger(FinAdvancePaymentsDAOImpl.class);

	public FinAdvancePaymentsDAOImpl() {
		super();
	}

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

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

	/**
	 * Fetch the Record Goods Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinAdvancePayments
	 */
	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select PaymentId,FinReference, PaymentSeq,DisbSeq, PaymentDetail,");
		selectSql.append(" AmtToBeReleased, LiabilityHoldName, BeneficiaryName,BeneficiaryAccNo, Description, ");
		selectSql.append(" PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks, ");
		selectSql.append(" BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode,");
		selectSql.append(" PhoneAreaCode, PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy, POIssued,PartnerBankID, TransactionRef,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,BranchCode,BranchBankCode,BranchBankName,BranchDesc,BankName,City,IFSC,partnerbankCode,PartnerBankName,PartnerBankAcType, PartnerBankAc");
		}
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PaymentId = :PaymentId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinAdvancePayments.class);

		try {
			finAdvancePayments = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finAdvancePayments = null;
		}
		logger.debug("Leaving");
		return finAdvancePayments;
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(final String id, String type) {
		logger.debug("Entering");
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setFinReference(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference,PaymentId, PaymentSeq,DisbSeq, PaymentDetail, AmtToBeReleased,");
		selectSql.append(" LiabilityHoldName, BeneficiaryName,BeneficiaryAccNo, Description,PaymentType,BankCode,  ");
		selectSql.append(" LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks, ");
		selectSql.append(" PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode, ");
		selectSql.append(" PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy,POIssued,PartnerBankID,LinkedTranId, TransactionRef,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BranchCode,BranchBankCode,BranchBankName,BranchDesc,BankName,City,IFSC,partnerbankCode,PartnerBankName,PartnerBankAcType,PartnerBankAc");
		}
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinAdvancePayments.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinAdvancePayments or FinAdvancePayments_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Goods Details by key LoanRefNumber
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
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
		if (finAdvancePayments.getId() == Long.MIN_VALUE) {
			finAdvancePayments.setId(getNextId("SeqAdvpayment"));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinAdvancePayments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PaymentId,FinReference, PaymentSeq ,DisbSeq, PaymentDetail, AmtToBeReleased,");
		insertSql.append(" LiabilityHoldName, BeneficiaryName, BeneficiaryAccNo,Description, PaymentType, ");
		insertSql.append("  LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks,BankCode, ");
		insertSql.append(" PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode, ");
		insertSql.append(" PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy,POIssued,PartnerBankID,LinkedTranId,TransactionRef,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:PaymentId, :FinReference, :PaymentSeq ,:DisbSeq, :PaymentDetail, :AmtToBeReleased,");
		insertSql.append("  :LiabilityHoldName, :BeneficiaryName,:BeneficiaryAccNo, :Description, :PaymentType, ");
		insertSql.append(" :LlReferenceNo, :LlDate, :CustContribution,:SellerContribution, :Remarks, :BankCode,");
		insertSql.append(" :PayableLoc, :PrintingLoc, :ValueDate, :BankBranchID, :PhoneCountryCode, :PhoneAreaCode, ");
		insertSql.append(" :PhoneNumber, :ClearingDate, :Status, :Active, :InputDate, :DisbCCy, :POIssued, :PartnerBankID, :LinkedTranId, :TransactionRef,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finAdvancePayments.getFinReference();
	}

	/**
	 * This method updates the Record FinAdvancePayments or FinAdvancePayments_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Goods Details by key LoanRefNumber and Version
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
		updateSql.append(" BeneficiaryAccNo = :BeneficiaryAccNo, Description = :Description,");
		updateSql.append(" PaymentType = :PaymentType, LlReferenceNo = :LlReferenceNo,");
		updateSql.append(" LlDate = :LlDate, CustContribution = :CustContribution,");
		updateSql.append(" SellerContribution = :SellerContribution, Remarks = :Remarks,");
		updateSql.append(" BankCode = :BankCode, PayableLoc = :PayableLoc, PrintingLoc = :PrintingLoc,");
		updateSql.append(" ValueDate = :ValueDate, BankBranchID = :BankBranchID,");
		updateSql.append(" PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode,");
		updateSql.append(" PhoneNumber = :PhoneNumber, ClearingDate = ClearingDate, Status = :Status,");
		updateSql.append(" Active = :Active, InputDate = :InputDate, DisbCCy = :DisbCCy, POIssued = :POIssued, PartnerBankID =:PartnerBankID,TransactionRef = :TransactionRef,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql
				.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where PaymentId = :PaymentId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	
	@Override
	public void update(long paymentId, long linkedTranId) {
		int recordCount = 0;
		logger.debug("Entering");
		FinAdvancePayments finAdvancePayments= new FinAdvancePayments();
		finAdvancePayments.setLinkedTranId(linkedTranId);
		finAdvancePayments.setPaymentId(paymentId);
		
		
		StringBuilder updateSql = new StringBuilder("Update FinAdvancePayments");
		updateSql.append(" Set LinkedTranId = :LinkedTranId ");
		updateSql.append(" Where PaymentId = PaymentId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	//update the Disbursement Status when DisbursementProcess Interface Calling 
	@Override
	public void updateDisbursmentStatus(FinAdvancePayments disbursement) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("Update FINADVANCEPAYMENTS");
		sql.append(" Set STATUS = :STATUS, CLEARINGDATE = :CLEARINGDATE, TRANSACTIONREF = :TRANSACTIONREF");
		sql.append(", REJECTREASON = :REJECTREASON");
		
		/*if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(disbursement.getPaymentType()) || DisbursementConstants.PAYMENT_TYPE_DD.equals(disbursement.getPaymentType())) {
			sql.append(", LLREFERENCENO = :LLREFERENCENO, LLDATE = :LLDATE");
			paramMap.addValue("LLREFERENCENO", disbursement.getLlReferenceNo());
			paramMap.addValue("LLDATE", disbursement.getClearingDate());
		}*/
		
		sql.append("  Where PAYMENTID = :PAYMENTID");
		
		paramMap.addValue("STATUS", disbursement.getStatus());
		paramMap.addValue("CLEARINGDATE", disbursement.getClearingDate());
		paramMap.addValue("TRANSACTIONREF", disbursement.getTransactionRef());
		paramMap.addValue("REJECTREASON", disbursement.getRejectReason());
		paramMap.addValue("PAYMENTID", disbursement.getPaymentId());
		

		logger.debug(Literal.SQL + sql);

		this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public int getBankCode(String bankCode, String type) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setBankCode(bankCode);;

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		}catch (Exception dae) {
			recordCount = 0;
		}
		
		return recordCount;
	}
	
	@Override
	public int getFinAdvCountByRef(String finReference, String type) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("select COUNT(*)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(type);
		selectSql.append(" Where FinReference = :FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		int recordCount = 0;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		
		logger.debug("Leaving");
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		}catch (Exception dae) {
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

		try{
			assignedCount	= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.info(e);
			assignedCount = 0;
		}
		logger.debug("Leaving");
		return assignedCount;
	}
	
	@Override
	public int getCountByFinReference(String finReference) {
		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		finAdvancePayments.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("select Max(paymentseq)");
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	

}