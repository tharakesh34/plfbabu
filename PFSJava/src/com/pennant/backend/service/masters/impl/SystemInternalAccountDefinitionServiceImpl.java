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
 * FileName    		:  SystemInternalAccountDefinitionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.masters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.masters.SystemInternalAccountDefinitionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>SystemInternalAccountDefinition</b>.<br>
 * 
 */
public class SystemInternalAccountDefinitionServiceImpl extends GenericService<SystemInternalAccountDefinition> implements SystemInternalAccountDefinitionService {
	private final static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private SystemInternalAccountDefinitionDAO systemInternalAccountDefinitionDAO;

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
	 * @return the systemInternalAccountDefinitionDAO
	 */
	public SystemInternalAccountDefinitionDAO getSystemInternalAccountDefinitionDAO() {
		return systemInternalAccountDefinitionDAO;
	}
	/**
	 * @param systemInternalAccountDefinitionDAO the systemInternalAccountDefinitionDAO to set
	 */
	public void setSystemInternalAccountDefinitionDAO(SystemInternalAccountDefinitionDAO systemInternalAccountDefinitionDAO) {
		this.systemInternalAccountDefinitionDAO = systemInternalAccountDefinitionDAO;
	}

	/**
	 * @return the systemInternalAccountDefinition
	 */
	@Override
	public SystemInternalAccountDefinition getSystemInternalAccountDefinition() {
		return getSystemInternalAccountDefinitionDAO().getSystemInternalAccountDefinition();
	}
	/**
	 * @return the systemInternalAccountDefinition for New Record
	 */
	@Override
	public SystemInternalAccountDefinition getNewSystemInternalAccountDefinition() {
		return getSystemInternalAccountDefinitionDAO().getNewSystemInternalAccountDefinition();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SystemInternalAccountDef/SystemInternalAccountDef_Temp 
	 * 			by using SystemInternalAccountDefinitionDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SystemInternalAccountDefinitionDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSystemInternalAccountDef by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		SystemInternalAccountDefinition systemInternalAccountDefinition = (SystemInternalAccountDefinition) auditHeader.getAuditDetail().getModelData();
		
		if (systemInternalAccountDefinition.isWorkflow()) {
			tableType="_TEMP";
		}

		if (systemInternalAccountDefinition.isNew()) {
			getSystemInternalAccountDefinitionDAO().save(systemInternalAccountDefinition,tableType);
		}else{
			getSystemInternalAccountDefinitionDAO().update(systemInternalAccountDefinition,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SystemInternalAccountDef by using SystemInternalAccountDefinitionDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSystemInternalAccountDef by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		SystemInternalAccountDefinition systemInternalAccountDefinition = (SystemInternalAccountDefinition) auditHeader.getAuditDetail().getModelData();
		getSystemInternalAccountDefinitionDAO().delete(systemInternalAccountDefinition,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSystemInternalAccountDefinitionById fetch the details by using SystemInternalAccountDefinitionDAO's getSystemInternalAccountDefinitionById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SystemInternalAccountDefinition
	 */
	
	@Override
	public SystemInternalAccountDefinition getSystemInternalAccountDefinitionById(String id) {
		return getSystemInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(id,"_View");
	}
	/**
	 * getApprovedSystemInternalAccountDefinitionById fetch the details by using SystemInternalAccountDefinitionDAO's getSystemInternalAccountDefinitionById method .
	 * with parameter id and type as blank. it fetches the approved records from the SystemInternalAccountDef.
	 * @param id (String)
	 * @return SystemInternalAccountDefinition
	 */
	
	public SystemInternalAccountDefinition getApprovedSystemInternalAccountDefinitionById(String id) {
		return getSystemInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param SystemInternalAccountDefinition (systemInternalAccountDefinition)
 	 * @return systemInternalAccountDefinition
	 */
	@Override
	public SystemInternalAccountDefinition refresh(SystemInternalAccountDefinition systemInternalAccountDefinition) {
		logger.debug("Entering");
		getSystemInternalAccountDefinitionDAO().refresh(systemInternalAccountDefinition);
		getSystemInternalAccountDefinitionDAO().initialize(systemInternalAccountDefinition);
		logger.debug("Leaving");
		return systemInternalAccountDefinition;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSystemInternalAccountDefinitionDAO().delete with parameters systemInternalAccountDefinition,""
	 * 		b)  NEW		Add new record in to main table by using getSystemInternalAccountDefinitionDAO().save with parameters systemInternalAccountDefinition,""
	 * 		c)  EDIT	Update record in the main table by using getSystemInternalAccountDefinitionDAO().update with parameters systemInternalAccountDefinition,""
	 * 3)	Delete the record from the workFlow table by using getSystemInternalAccountDefinitionDAO().delete with parameters systemInternalAccountDefinition,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSystemInternalAccountDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSystemInternalAccountDef by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		SystemInternalAccountDefinition systemInternalAccountDefinition = new SystemInternalAccountDefinition();
		BeanUtils.copyProperties((SystemInternalAccountDefinition) auditHeader.getAuditDetail().getModelData(), systemInternalAccountDefinition);

		if (systemInternalAccountDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getSystemInternalAccountDefinitionDAO().delete(systemInternalAccountDefinition,"");
				
			} else {
				systemInternalAccountDefinition.setRoleCode("");
				systemInternalAccountDefinition.setNextRoleCode("");
				systemInternalAccountDefinition.setTaskId("");
				systemInternalAccountDefinition.setNextTaskId("");
				systemInternalAccountDefinition.setWorkflowId(0);
				
				if (systemInternalAccountDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					systemInternalAccountDefinition.setRecordType("");
					getSystemInternalAccountDefinitionDAO().save(systemInternalAccountDefinition,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					systemInternalAccountDefinition.setRecordType("");
					getSystemInternalAccountDefinitionDAO().update(systemInternalAccountDefinition,"");
				}
			}
			
			getSystemInternalAccountDefinitionDAO().delete(systemInternalAccountDefinition,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(systemInternalAccountDefinition);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSystemInternalAccountDefinitionDAO().delete with parameters systemInternalAccountDefinition,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSystemInternalAccountDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject");
			if (!auditHeader.isNextProcess()) {
				logger.debug("Leaving");
				return auditHeader;
			}

			SystemInternalAccountDefinition systemInternalAccountDefinition = (SystemInternalAccountDefinition) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getSystemInternalAccountDefinitionDAO().delete(systemInternalAccountDefinition,"_TEMP");
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getSystemInternalAccountDefinitionDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)    
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			SystemInternalAccountDefinition systemInternalAccountDefinition= (SystemInternalAccountDefinition) auditDetail.getModelData();
			
			SystemInternalAccountDefinition tempSystemInternalAccountDefinition= null;
			if (systemInternalAccountDefinition.isWorkflow()){
				tempSystemInternalAccountDefinition = getSystemInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(systemInternalAccountDefinition.getId(), "_Temp");
			}
			SystemInternalAccountDefinition befSystemInternalAccountDefinition= getSystemInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(systemInternalAccountDefinition.getId(), "");
			
			SystemInternalAccountDefinition oldSystemInternalAccountDefinition= systemInternalAccountDefinition.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=systemInternalAccountDefinition.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_SIACode")+":"+valueParm[0];
			
			if (systemInternalAccountDefinition.isNew()){ // for New record or new record into work flow
				
				if (!systemInternalAccountDefinition.isWorkflow()){// With out Work flow only new records  
					if (befSystemInternalAccountDefinition !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (systemInternalAccountDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befSystemInternalAccountDefinition !=null || tempSystemInternalAccountDefinition!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befSystemInternalAccountDefinition ==null || tempSystemInternalAccountDefinition!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!systemInternalAccountDefinition.isWorkflow()){	// With out Work flow for update and delete
				
					if (befSystemInternalAccountDefinition ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldSystemInternalAccountDefinition!=null && !oldSystemInternalAccountDefinition.getLastMntOn().equals(befSystemInternalAccountDefinition.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempSystemInternalAccountDefinition==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (oldSystemInternalAccountDefinition!=null && !oldSystemInternalAccountDefinition.getLastMntOn().equals(tempSystemInternalAccountDefinition.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !systemInternalAccountDefinition.isWorkflow()){
				systemInternalAccountDefinition.setBefImage(befSystemInternalAccountDefinition);	
			}

			return auditDetail;
		}

}