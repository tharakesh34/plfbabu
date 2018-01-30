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
 * FileName    		:  ErrorDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2016    														*
 *                                                                  						*
 * Modified Date    :  05-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.errordetail.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.errordetail.ErrorDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ErrorDetail</b>.<br>
 * 
 */
public class ErrorDetailServiceImpl extends GenericService<ErrorDetail> implements ErrorDetailService {
	private AuditHeaderDAO auditHeaderDAO;	
	private ErrorDetailDAO errorDetailDAO;
	
	
	public ErrorDetailServiceImpl() {
		super(true);
	}
	

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
	 * @return the errorDetailDAO
	 */
	public ErrorDetailDAO getErrorDetailDAO() {
		return errorDetailDAO;
	}
	/**
	 * @param errorDetailDAO the errorDetailDAO to set
	 */
	public void setErrorDetailDAO(ErrorDetailDAO errorDetailDAO) {
		this.errorDetailDAO = errorDetailDAO;
	}

	

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table ErrorDetails/ErrorDetails_Temp 
	 * 			by using ErrorDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ErrorDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtErrorDetails by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table ErrorDetails/ErrorDetails_Temp 
	 * 			by using ErrorDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ErrorDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtErrorDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		log.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			log.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		ErrorDetail errorDetail = (ErrorDetail) auditHeader.getAuditDetail().getModelData();
		
		if (errorDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (errorDetail.isNew()) {
			getErrorDetailDAO().save(errorDetail,tableType);
		}else{
			getErrorDetailDAO().update(errorDetail,tableType);
		}
		
		if (!errorDetail.isWorkflow()) {
			invalidateEntity(errorDetail.getId());
		}
		
		getAuditHeaderDAO().addAudit(auditHeader);
		log.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table ErrorDetails by using ErrorDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtErrorDetails by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		log.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			log.debug("Leaving");
			return auditHeader;
		}
		
		ErrorDetail errorDetail = (ErrorDetail) auditHeader.getAuditDetail().getModelData();
		getErrorDetailDAO().delete(errorDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		invalidateEntity(errorDetail.getId());
		
		log.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getErrorDetailById fetch the details by using ErrorDetailDAO's getErrorDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ErrorDetail
	 */
	
	@Override
	public ErrorDetail getErrorDetailById(String id) {
		return getErrorDetailDAO().getErrorDetailById(id,"_View");
	}
	/**
	 * getApprovedErrorDetailById fetch the details by using ErrorDetailDAO's getErrorDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the ErrorDetails.
	 * @param id (String)
	 * @return ErrorDetail
	 */
	
	public ErrorDetail getApprovedErrorDetailById(String id) {
		return getCachedEntity(id);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getErrorDetailDAO().delete with
	 * parameters errorDetail,"" b) NEW Add new record in to main table by using getErrorDetailDAO().save with
	 * parameters errorDetail,"" c) EDIT Update record in the main table by using getErrorDetailDAO().update with
	 * parameters errorDetail,"" 3) Delete the record from the workFlow table by using getErrorDetailDAO().delete with
	 * parameters errorDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtErrorDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtErrorDetails by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		log.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ErrorDetail errorDetail = new ErrorDetail();
		BeanUtils.copyProperties((ErrorDetail) auditHeader.getAuditDetail().getModelData(), errorDetail);

		if (errorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getErrorDetailDAO().delete(errorDetail, "");

		} else {
			errorDetail.setRoleCode("");
			errorDetail.setNextRoleCode("");
			errorDetail.setTaskId("");
			errorDetail.setNextTaskId("");
			errorDetail.setWorkflowId(0);

			if (errorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				errorDetail.setRecordType("");
				getErrorDetailDAO().save(errorDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				errorDetail.setRecordType("");
				getErrorDetailDAO().update(errorDetail, "");
			}
		}
		
		invalidateEntity(errorDetail.getId());
		
		getErrorDetailDAO().delete(errorDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(errorDetail);
	
		getAuditHeaderDAO().addAudit(auditHeader);		
		log.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getErrorDetailDAO().delete with parameters errorDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtErrorDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			log.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			ErrorDetail errorDetail = (ErrorDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getErrorDetailDAO().delete(errorDetail,"_Temp");
			
			getAuditHeaderDAO().addAudit(auditHeader);
			log.debug("Leaving");
			
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
			log.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			log.debug("Leaving");
			return auditHeader;
		}

		/**
		 * Validation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getErrorDetailDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			log.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			ErrorDetail errorDetail= (ErrorDetail) auditDetail.getModelData();
			
			ErrorDetail tempErrorDetail= null;
			if (errorDetail.isWorkflow()){
				tempErrorDetail = getErrorDetailDAO().getErrorDetailById(errorDetail.getId(), "_Temp");
			}
			ErrorDetail befErrorDetail= getErrorDetailDAO().getErrorDetailById(errorDetail.getId(), "");
			
			ErrorDetail oldErrorDetail= errorDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=errorDetail.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_ErrorCode")+":"+valueParm[0];
			
			if (errorDetail.isNew()){ // for New record or new record into work flow
				
				if (!errorDetail.isWorkflow()){// With out Work flow only new records  
					if (befErrorDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (errorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befErrorDetail !=null || tempErrorDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befErrorDetail ==null || tempErrorDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!errorDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befErrorDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldErrorDetail!=null && !oldErrorDetail.getLastMntOn().equals(befErrorDetail.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempErrorDetail==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempErrorDetail!=null  && oldErrorDetail!=null && !oldErrorDetail.getLastMntOn().equals(tempErrorDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !errorDetail.isWorkflow()){
				auditDetail.setBefImage(befErrorDetail);	
			}

			return auditDetail;
		}
	
	@Override
	public ErrorDetail getErrorDetail(String errorCode) {
		return getCachedEntity(errorCode);
	}
	
	@Override
	protected ErrorDetail getEntity(String code) {
		return errorDetailDAO.getErrorDetailById(code,"_AView");
	}
}