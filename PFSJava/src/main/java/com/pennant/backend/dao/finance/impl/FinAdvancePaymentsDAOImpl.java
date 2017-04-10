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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinAdvancePayments model</b> class.<br>
 * 
 */

public class FinAdvancePaymentsDAOImpl extends BasisNextidDaoImpl<FinAdvancePayments> implements FinAdvancePaymentsDAO {

	private static Logger logger = Logger.getLogger(FinAdvancePaymentsDAOImpl.class);
	
	public FinAdvancePaymentsDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinAdvancePayments 
	 * @return FinAdvancePayments
	 */

	@Override
	public FinAdvancePayments getFinAdvancePayments() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinAdvancePayments");
		FinAdvancePayments finAdvancePayments= new FinAdvancePayments();
		if (workFlowDetails!=null){
			finAdvancePayments.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finAdvancePayments;
	}


	/**
	 * This method get the module from method getFinAdvancePayments() and set the new record flag as true and return FinAdvancePayments()   
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
	 * Fetch the Record  Goods Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinAdvancePayments
	 */
	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference, PaymentSeq,DisbSeq, PaymentDetail, AmtToBeReleased, LiabilityHoldName, BeneficiaryName,  ");
		selectSql.append(" BeneficiaryAccNo, Description, PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks, ");
		selectSql.append(" BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode, ");
		selectSql.append(" PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy,POIssued, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,BranchCode,BranchBankCode,BranchBankName,BranchDesc,BankName,City,IFSC");
		}
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and PaymentSeq = :PaymentSeq ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
		
		try{
			finAdvancePayments = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
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
		selectSql.append("Select FinReference,PaymentId, PaymentSeq,DisbSeq, PaymentDetail, AmtToBeReleased, LiabilityHoldName, BeneficiaryName,  ");
		selectSql.append(" BeneficiaryAccNo, Description, PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks, ");
		selectSql.append(" BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode, ");
		selectSql.append(" PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy,POIssued, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",BranchCode,BranchBankCode,BranchBankName,BranchDesc,BankName,City,IFSC");
		}
		selectSql.append(" From FinAdvancePayments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinAdvancePayments or FinAdvancePayments_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods Details (FinAdvancePayments)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinAdvancePayments finAdvancePayments,String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From FinAdvancePayments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and PaymentSeq = :PaymentSeq");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",finAdvancePayments.getFinReference() ,finAdvancePayments.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinAdvancePayments or FinAdvancePayments_Temp.
	 *
	 * save Goods Details 
	 * 
	 * @param Goods Details (FinAdvancePayments)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinAdvancePayments finAdvancePayments,String type) {
		logger.debug("Entering");
		finAdvancePayments.setId(getNextId("SeqAdvpayment"));
		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into FinAdvancePayments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PaymentId,FinReference, PaymentSeq ,DisbSeq, PaymentDetail, AmtToBeReleased, LiabilityHoldName, BeneficiaryName, BeneficiaryAccNo, " );
		insertSql.append(" Description, PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks,");
		insertSql.append(" BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode, ");
		insertSql.append(" PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy,POIssued, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:PaymentId, :FinReference, :PaymentSeq ,:DisbSeq, :PaymentDetail, :AmtToBeReleased, :LiabilityHoldName, :BeneficiaryName,");
		insertSql.append(" :BeneficiaryAccNo, :Description, :PaymentType, :LlReferenceNo, :LlDate, :CustContribution,");
		insertSql.append(" :SellerContribution, :Remarks,");
		insertSql.append(" :BankCode, :PayableLoc, :PrintingLoc, :ValueDate, :BankBranchID, :PhoneCountryCode, :PhoneAreaCode, ");
		insertSql.append(" :PhoneNumber, :ClearingDate, :Status, :Active, :InputDate, :DisbCCy, :POIssued,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finAdvancePayments.getFinReference();
	}
	
	/**
	 * This method updates the Record FinAdvancePayments or FinAdvancePayments_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (FinAdvancePayments)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(FinAdvancePayments finAdvancePayments,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update FinAdvancePayments");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append("  Set PaymentDetail = :PaymentDetail,");
		updateSql.append("  PaymentId = :PaymentId,AmtToBeReleased = :AmtToBeReleased, LiabilityHoldName = :LiabilityHoldName, BeneficiaryName = :BeneficiaryName,DisbSeq =:DisbSeq,");
		updateSql.append("  BeneficiaryAccNo = :BeneficiaryAccNo, Description = :Description, PaymentType = :PaymentType, LlReferenceNo = :LlReferenceNo,");
		updateSql.append("  LlDate = :LlDate, CustContribution = :CustContribution, SellerContribution = :SellerContribution, Remarks = :Remarks,");
		updateSql.append(" BankCode = :BankCode, PayableLoc = :PayableLoc, PrintingLoc = :PrintingLoc, ValueDate = :ValueDate, BankBranchID = :BankBranchID, ");
		updateSql.append(" PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode,PhoneNumber = :PhoneNumber, ClearingDate = ClearingDate, Status = :Status, Active = :Active, InputDate = :InputDate, DisbCCy = :DisbCCy, POIssued = :POIssued, ");
		updateSql.append("  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where FinReference = :FinReference and PaymentSeq = :PaymentSeq");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvancePayments);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",finAdvancePayments.getFinReference() ,finAdvancePayments.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
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
	
}