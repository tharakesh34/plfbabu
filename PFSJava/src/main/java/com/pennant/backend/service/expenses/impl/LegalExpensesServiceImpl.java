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
 * FileName    		:  LegalExpensesServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.expenses.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.expenses.LegalExpensesDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.expenses.LegalExpensesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>LegalExpenses</b>.<br>
 * 
 */
public class LegalExpensesServiceImpl extends GenericService<LegalExpenses> implements LegalExpensesService {
	private static final Logger logger = Logger.getLogger(LegalExpensesServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private LegalExpensesDAO legalExpensesDAO;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	
	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the legalExpensesDAO
	 */
	public LegalExpensesDAO getLegalExpensesDAO() {
		return legalExpensesDAO;
	}
	/**
	 * @param legalExpensesDAO the legalExpensesDAO to set
	 */
	public void setLegalExpensesDAO(LegalExpensesDAO legalExpensesDAO) {
		this.legalExpensesDAO = legalExpensesDAO;
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinLegalExpenses/FinLegalExpenses_Temp 
	 * 			by using LegalExpensesDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using LegalExpensesDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinLegalExpenses by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinLegalExpenses/FinLegalExpenses_Temp 
	 * 			by using LegalExpensesDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using LegalExpensesDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinLegalExpenses by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		LegalExpenses legalExpenses = (LegalExpenses) auditHeader.getAuditDetail().getModelData();
		
		if (legalExpenses.isWorkflow()) {
			tableType="_Temp";
		}

		if (legalExpenses.isNew()) {
			getLegalExpensesDAO().save(legalExpenses,tableType);
		}else{
			getLegalExpensesDAO().update(legalExpenses,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinLegalExpenses by using LegalExpensesDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinLegalExpenses by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		LegalExpenses legalExpenses = (LegalExpenses) auditHeader.getAuditDetail().getModelData();
		getLegalExpensesDAO().delete(legalExpenses,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLegalExpensesById fetch the details by using LegalExpensesDAO's getLegalExpensesById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LegalExpenses
	 */
	
	@Override
	public LegalExpenses getLegalExpensesById(String id) {
		return getLegalExpensesDAO().getLegalExpensesById(id,"_View");
	}
	/**
	 * getApprovedLegalExpensesById fetch the details by using LegalExpensesDAO's getLegalExpensesById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinLegalExpenses.
	 * @param id (String)
	 * @return LegalExpenses
	 */
	
	public LegalExpenses getApprovedLegalExpensesById(String id) {
		return getLegalExpensesDAO().getLegalExpensesById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getLegalExpensesDAO().delete with
	 * parameters legalExpenses,"" b) NEW Add new record in to main table by using getLegalExpensesDAO().save with
	 * parameters legalExpenses,"" c) EDIT Update record in the main table by using getLegalExpensesDAO().update with
	 * parameters legalExpenses,"" 3) Delete the record from the workFlow table by using getLegalExpensesDAO().delete
	 * with parameters legalExpenses,"_Temp" 4) Audit the record in to AuditHeader and AdtFinLegalExpenses by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinLegalExpenses
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LegalExpenses legalExpenses = new LegalExpenses();
		BeanUtils.copyProperties((LegalExpenses) auditHeader.getAuditDetail().getModelData(), legalExpenses);

		if (legalExpenses.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getLegalExpensesDAO().delete(legalExpenses, "");

		} else {
			legalExpenses.setRoleCode("");
			legalExpenses.setNextRoleCode("");
			legalExpenses.setTaskId("");
			legalExpenses.setNextTaskId("");
			legalExpenses.setWorkflowId(0);

			if (legalExpenses.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				legalExpenses.setRecordType("");
				getLegalExpensesDAO().save(legalExpenses, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				legalExpenses.setRecordType("");
				getLegalExpensesDAO().update(legalExpenses, "");
			}
		}

		getLegalExpensesDAO().delete(legalExpenses, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(legalExpenses);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getLegalExpensesDAO().delete with parameters legalExpenses,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinLegalExpenses by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			LegalExpenses legalExpenses = (LegalExpenses) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getLegalExpensesDAO().delete(legalExpenses,"_Temp");
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	validate the audit detail 
		 * 2)	if any error/Warnings  then assign the to auditHeader
		 * 3)   identify the nextprocess
		 *  
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean onlineRequest){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		/**
		 * Validation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getLegalExpensesDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			LegalExpenses legalExpenses= (LegalExpenses) auditDetail.getModelData();
			
			LegalExpenses tempLegalExpenses= null;
			if (legalExpenses.isWorkflow()){
				tempLegalExpenses = getLegalExpensesDAO().getLegalExpensesById(legalExpenses.getExpReference(), "_Temp");
			}
			LegalExpenses befLegalExpenses= getLegalExpensesDAO().getLegalExpensesById(legalExpenses.getExpReference(), "");
			
			LegalExpenses oldLegalExpenses= legalExpenses.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=legalExpenses.getFinReference();
			errParm[0]=PennantJavaUtil.getLabel("label_ExpReference")+":"+valueParm[0];
			
			if (legalExpenses.isNew()){ // for New record or new record into work flow
				
				if (!legalExpenses.isWorkflow()){// With out Work flow only new records  
					if (befLegalExpenses !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (legalExpenses.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befLegalExpenses !=null || tempLegalExpenses!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befLegalExpenses ==null || tempLegalExpenses!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				
					
				}
				
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!legalExpenses.isWorkflow()){	// With out Work flow for update and delete
				
					if (befLegalExpenses ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldLegalExpenses!=null && !oldLegalExpenses.getLastMntOn().equals(befLegalExpenses.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempLegalExpenses==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempLegalExpenses!=null && oldLegalExpenses!=null && !oldLegalExpenses.getLastMntOn().equals(tempLegalExpenses.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !legalExpenses.isWorkflow()){
				auditDetail.setBefImage(befLegalExpenses);	
			}

			return auditDetail;
		}

		@Override
		public BigDecimal getTotalCharges(String finReference) {
			return getLegalExpensesDAO().getTotalCharges(finReference);
		}

}