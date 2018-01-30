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
 * FileName    		:  DeviationParamServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-06-2015    														*
 *                                                                  						*
 * Modified Date    :  22-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.solutionfactory.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.solutionfactory.DeviationParamDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.solutionfactory.DeviationParamService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DeviationParam</b>.<br>
 * 
 */
public class DeviationParamServiceImpl extends GenericService<DeviationParam> implements DeviationParamService {
	private static final Logger logger = Logger.getLogger(DeviationParamServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DeviationParamDAO deviationParamDAO;

	public DeviationParamServiceImpl() {
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
	 * @return the deviationParamDAO
	 */
	public DeviationParamDAO getDeviationParamDAO() {
		return deviationParamDAO;
	}
	/**
	 * @param deviationParamDAO the deviationParamDAO to set
	 */
	public void setDeviationParamDAO(DeviationParamDAO deviationParamDAO) {
		this.deviationParamDAO = deviationParamDAO;
	}

	
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table DeviationParams/DeviationParams_Temp 
	 * 			by using DeviationParamDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DeviationParamDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtDeviationParams by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table DeviationParams/DeviationParams_Temp 
	 * 			by using DeviationParamDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DeviationParamDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtDeviationParams by using auditHeaderDAO.addAudit(auditHeader)
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
		DeviationParam deviationParam = (DeviationParam) auditHeader.getAuditDetail().getModelData();
		
		if (deviationParam.isWorkflow()) {
			tableType="_Temp";
		}

		if (deviationParam.isNew()) {
			getDeviationParamDAO().save(deviationParam,tableType);
		}else{
			getDeviationParamDAO().update(deviationParam,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table DeviationParams by using DeviationParamDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtDeviationParams by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		DeviationParam deviationParam = (DeviationParam) auditHeader.getAuditDetail().getModelData();
		getDeviationParamDAO().delete(deviationParam,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDeviationParamById fetch the details by using DeviationParamDAO's getDeviationParamById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeviationParam
	 */
	
	@Override
	public DeviationParam getDeviationParamById(String id) {
		return getDeviationParamDAO().getDeviationParamById(id,"_View");
	}
	/**
	 * getApprovedDeviationParamById fetch the details by using DeviationParamDAO's getDeviationParamById method .
	 * with parameter id and type as blank. it fetches the approved records from the DeviationParams.
	 * @param id (String)
	 * @return DeviationParam
	 */
	
	public DeviationParam getApprovedDeviationParamById(String id) {
		return getDeviationParamDAO().getDeviationParamById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDeviationParamDAO().delete with
	 * parameters deviationParam,"" b) NEW Add new record in to main table by using getDeviationParamDAO().save with
	 * parameters deviationParam,"" c) EDIT Update record in the main table by using getDeviationParamDAO().update with
	 * parameters deviationParam,"" 3) Delete the record from the workFlow table by using getDeviationParamDAO().delete
	 * with parameters deviationParam,"_Temp" 4) Audit the record in to AuditHeader and AdtDeviationParams by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtDeviationParams
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

		DeviationParam deviationParam = new DeviationParam();
		BeanUtils.copyProperties((DeviationParam) auditHeader.getAuditDetail().getModelData(), deviationParam);

		if (deviationParam.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDeviationParamDAO().delete(deviationParam, "");

		} else {
			deviationParam.setRoleCode("");
			deviationParam.setNextRoleCode("");
			deviationParam.setTaskId("");
			deviationParam.setNextTaskId("");
			deviationParam.setWorkflowId(0);

			if (deviationParam.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				deviationParam.setRecordType("");
				getDeviationParamDAO().save(deviationParam, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				deviationParam.setRecordType("");
				getDeviationParamDAO().update(deviationParam, "");
			}
		}

		getDeviationParamDAO().delete(deviationParam, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(deviationParam);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getDeviationParamDAO().delete with parameters deviationParam,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtDeviationParams by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			DeviationParam deviationParam = (DeviationParam) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDeviationParamDAO().delete(deviationParam,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getDeviationParamDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			DeviationParam deviationParam= (DeviationParam) auditDetail.getModelData();
			
			DeviationParam tempDeviationParam= null;
			if (deviationParam.isWorkflow()){
				tempDeviationParam = getDeviationParamDAO().getDeviationParamById(deviationParam.getId(), "_Temp");
			}
			DeviationParam befDeviationParam= getDeviationParamDAO().getDeviationParamById(deviationParam.getId(), "");
			
			DeviationParam oldDeviationParam= deviationParam.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=deviationParam.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_Code")+":"+valueParm[0];
			
			if (deviationParam.isNew()){ // for New record or new record into work flow
				
				if (!deviationParam.isWorkflow()){// With out Work flow only new records  
					if (befDeviationParam !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (deviationParam.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befDeviationParam !=null || tempDeviationParam!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befDeviationParam ==null || tempDeviationParam!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!deviationParam.isWorkflow()){	// With out Work flow for update and delete
				
					if (befDeviationParam ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldDeviationParam!=null && !oldDeviationParam.getLastMntOn().equals(befDeviationParam.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempDeviationParam==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempDeviationParam!=null  && oldDeviationParam!=null && !oldDeviationParam.getLastMntOn().equals(tempDeviationParam.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !deviationParam.isWorkflow()){
				auditDetail.setBefImage(befDeviationParam);	
			}

			return auditDetail;
		}

}