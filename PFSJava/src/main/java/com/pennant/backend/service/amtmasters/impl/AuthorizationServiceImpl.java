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
 * FileName    		:  AuthorizationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2013    														*
 *                                                                  						*
 * Modified Date    :  20-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.amtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.AuthorizationDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.AuthorizationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>Authorization</b>.<br>
 * 
 */
public class AuthorizationServiceImpl extends GenericService<Authorization> implements AuthorizationService {
	private static final Logger logger = Logger.getLogger(AuthorizationServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private AuthorizationDAO authorizationDAO;

	public AuthorizationServiceImpl() {
		super();
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
	 * @return the authorizationDAO
	 */
	public AuthorizationDAO getAuthorizationDAO() {
		return authorizationDAO;
	}
	/**
	 * @param authorizationDAO the authorizationDAO to set
	 */
	public void setAuthorizationDAO(AuthorizationDAO authorizationDAO) {
		this.authorizationDAO = authorizationDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTAuthorization/AMTAuthorization_Temp 
	 * 			by using AuthorizationDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using AuthorizationDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTAuthorization by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table AMTAuthorization/AMTAuthorization_Temp 
	 * 			by using AuthorizationDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using AuthorizationDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTAuthorization by using auditHeaderDAO.addAudit(auditHeader)
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
		Authorization authorization = (Authorization) auditHeader.getAuditDetail().getModelData();
		
		if (authorization.isWorkflow()) {
			tableType="_Temp";
		}

		if (authorization.isNew()) {
			authorization.setId(getAuthorizationDAO().save(authorization,tableType));
			auditHeader.getAuditDetail().setModelData(authorization);
			auditHeader.setAuditReference(String.valueOf(authorization.getAuthUserId()));
		}else{
			getAuthorizationDAO().update(authorization,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTAuthorization by using AuthorizationDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTAuthorization by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		Authorization authorization = (Authorization) auditHeader.getAuditDetail().getModelData();
		getAuthorizationDAO().delete(authorization,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAuthorizationById fetch the details by using AuthorizationDAO's getAuthorizationById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Authorization
	 */
	
	@Override
	public Authorization getAuthorizationById(long id) {
		return getAuthorizationDAO().getAuthorizationById(id,"_View");
	}
	/**
	 * getApprovedAuthorizationById fetch the details by using AuthorizationDAO's getAuthorizationById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTAuthorization.
	 * @param id (int)
	 * @return Authorization
	 */
	
	public Authorization getApprovedAuthorizationById(long id) {
		return getAuthorizationDAO().getAuthorizationById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getAuthorizationDAO().delete with parameters authorization,""
	 * 		b)  NEW		Add new record in to main table by using getAuthorizationDAO().save with parameters authorization,""
	 * 		c)  EDIT	Update record in the main table by using getAuthorizationDAO().update with parameters authorization,""
	 * 3)	Delete the record from the workFlow table by using getAuthorizationDAO().delete with parameters authorization,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTAuthorization by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTAuthorization by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Authorization authorization = new Authorization();
		BeanUtils.copyProperties((Authorization) auditHeader.getAuditDetail().getModelData(), authorization);

		if (authorization.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getAuthorizationDAO().delete(authorization,"");
				
			} else {
				authorization.setRoleCode("");
				authorization.setNextRoleCode("");
				authorization.setTaskId("");
				authorization.setNextTaskId("");
				authorization.setWorkflowId(0);
				
			if (authorization.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType=PennantConstants.TRAN_ADD;
					authorization.setRecordType("");
					getAuthorizationDAO().save(authorization,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					authorization.setRecordType("");
					getAuthorizationDAO().update(authorization,"");
				}
			}
			
			getAuthorizationDAO().delete(authorization,"_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(authorization);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getAuthorizationDAO().delete with parameters authorization,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTAuthorization by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			Authorization authorization = (Authorization) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuthorizationDAO().delete(authorization,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getAuthorizationDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			Authorization authorization= (Authorization) auditDetail.getModelData();
			
			Authorization tempAuthorization= null;
			if (authorization.isWorkflow()){
				tempAuthorization = getAuthorizationDAO().getAuthorization(authorization.getAuthUserId(),authorization.getAuthType(),"_Temp");
			}
			Authorization befAuthorization= getAuthorizationDAO().getAuthorization(authorization.getAuthUserId(),authorization.getAuthType(), "");
			
			Authorization oldAuthorization= authorization.getBefImage();
			
			
			String[] errParm= new String[2];
			String[] valueParm= new String[2];
			valueParm[0]=String.valueOf(authorization.getAuthUserId());
			valueParm[1]=authorization.getAuthType();
			errParm[0]=PennantJavaUtil.getLabel("label_AuthUserId")+":"+valueParm[0];
			
			if (authorization.isNew()){ // for New record or new record into work flow
				
				if (!authorization.isWorkflow()){// With out Work flow only new records  
					if (befAuthorization !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // with work flow
					if (authorization.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befAuthorization !=null || tempAuthorization!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befAuthorization ==null || tempAuthorization!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!authorization.isWorkflow()){	// With out Work flow for update and delete
				
					if (befAuthorization ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldAuthorization!=null && !oldAuthorization.getLastMntOn().equals(befAuthorization.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempAuthorization==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempAuthorization!=null && oldAuthorization!=null && !oldAuthorization.getLastMntOn().equals(tempAuthorization.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !authorization.isWorkflow()){
				auditDetail.setBefImage(befAuthorization);	
			}

			return auditDetail;
		}

}