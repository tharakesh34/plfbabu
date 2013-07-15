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
 * FileName    		:  CollateralLocationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.coremasters.impl;



import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.audit.AuditDetail;

import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.coremasters.CollateralLocationService;
import com.pennant.backend.dao.coremasters.CollateralLocationDAO;
import com.pennant.backend.model.coremasters.CollateralLocation;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.app.util.ErrorUtil;

/**
 * Service implementation for methods that depends on <b>CollateralLocation</b>.<br>
 * 
 */
public class CollateralLocationServiceImpl extends GenericService<CollateralLocation> implements CollateralLocationService {
	private final static Logger logger = Logger.getLogger(CollateralLocationServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private CollateralLocationDAO collateralLocationDAO;

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
	 * @return the collateralLocationDAO
	 */
	public CollateralLocationDAO getCollateralLocationDAO() {
		return collateralLocationDAO;
	}
	/**
	 * @param collateralLocationDAO the collateralLocationDAO to set
	 */
	public void setCollateralLocationDAO(CollateralLocationDAO collateralLocationDAO) {
		this.collateralLocationDAO = collateralLocationDAO;
	}

	/**
	 * @return the collateralLocation
	 */
	@Override
	public CollateralLocation getCollateralLocation() {
		return getCollateralLocationDAO().getCollateralLocation();
	}
	/**
	 * @return the collateralLocation for New Record
	 */
	@Override
	public CollateralLocation getNewCollateralLocation() {
		return getCollateralLocationDAO().getNewCollateralLocation();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table HZPF/HZPF_Temp 
	 * 			by using CollateralLocationDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CollateralLocationDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtHZPF by using auditHeaderDAO.addAudit(auditHeader)
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
		CollateralLocation collateralLocation = (CollateralLocation) auditHeader.getAuditDetail().getModelData();
		
		if (collateralLocation.isWorkflow()) {
			tableType="_TEMP";
		}

		if (collateralLocation.isNew()) {
			getCollateralLocationDAO().save(collateralLocation,tableType);
		}else{
			getCollateralLocationDAO().update(collateralLocation,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table HZPF by using CollateralLocationDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtHZPF by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		CollateralLocation collateralLocation = (CollateralLocation) auditHeader.getAuditDetail().getModelData();
		getCollateralLocationDAO().delete(collateralLocation,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCollateralLocationById fetch the details by using CollateralLocationDAO's getCollateralLocationById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CollateralLocation
	 */
	
	@Override
	public CollateralLocation getCollateralLocationById(String id) {
		return getCollateralLocationDAO().getCollateralLocationById(id,"_View");
	}
	/**
	 * getApprovedCollateralLocationById fetch the details by using CollateralLocationDAO's getCollateralLocationById method .
	 * with parameter id and type as blank. it fetches the approved records from the HZPF.
	 * @param id (String)
	 * @return CollateralLocation
	 */
	
	public CollateralLocation getApprovedCollateralLocationById(String id) {
		return getCollateralLocationDAO().getCollateralLocationById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param CollateralLocation (collateralLocation)
 	 * @return collateralLocation
	 */
	@Override
	public CollateralLocation refresh(CollateralLocation collateralLocation) {
		logger.debug("Entering");
		getCollateralLocationDAO().refresh(collateralLocation);
		getCollateralLocationDAO().initialize(collateralLocation);
		logger.debug("Leaving");
		return collateralLocation;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCollateralLocationDAO().delete with parameters collateralLocation,""
	 * 		b)  NEW		Add new record in to main table by using getCollateralLocationDAO().save with parameters collateralLocation,""
	 * 		c)  EDIT	Update record in the main table by using getCollateralLocationDAO().update with parameters collateralLocation,""
	 * 3)	Delete the record from the workFlow table by using getCollateralLocationDAO().delete with parameters collateralLocation,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtHZPF by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtHZPF by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CollateralLocation collateralLocation = new CollateralLocation();
		BeanUtils.copyProperties((CollateralLocation) auditHeader.getAuditDetail().getModelData(), collateralLocation);

		if (collateralLocation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getCollateralLocationDAO().delete(collateralLocation,"");
				
			} else {
				collateralLocation.setRoleCode("");
				collateralLocation.setNextRoleCode("");
				collateralLocation.setTaskId("");
				collateralLocation.setNextTaskId("");
				collateralLocation.setWorkflowId(0);
				
				if (collateralLocation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					collateralLocation.setRecordType("");
					getCollateralLocationDAO().save(collateralLocation,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					collateralLocation.setRecordType("");
					getCollateralLocationDAO().update(collateralLocation,"");
				}
			}
			
			getCollateralLocationDAO().delete(collateralLocation,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(collateralLocation);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCollateralLocationDAO().delete with parameters collateralLocation,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtHZPF by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			CollateralLocation collateralLocation = (CollateralLocation) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getCollateralLocationDAO().delete(collateralLocation,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getCollateralLocationDAO().getErrorDetail with Error ID and language as parameters.
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
			CollateralLocation collateralLocation= (CollateralLocation) auditDetail.getModelData();
			
			CollateralLocation tempCollateralLocation= null;
			if (collateralLocation.isWorkflow()){
				tempCollateralLocation = getCollateralLocationDAO().getCollateralLocationById(collateralLocation.getId(), "_Temp");
			}
			CollateralLocation befCollateralLocation= getCollateralLocationDAO().getCollateralLocationById(collateralLocation.getId(), "");
			
			CollateralLocation old_CollateralLocation= collateralLocation.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=collateralLocation.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_HZCLO")+":"+valueParm[0];
			
			if (collateralLocation.isNew()){ // for New record or new record into work flow
				
				if (!collateralLocation.isWorkflow()){// With out Work flow only new records  
					if (befCollateralLocation !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (collateralLocation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befCollateralLocation !=null || tempCollateralLocation!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befCollateralLocation ==null || tempCollateralLocation!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!collateralLocation.isWorkflow()){	// With out Work flow for update and delete
				
					if (befCollateralLocation ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (old_CollateralLocation!=null && !old_CollateralLocation.getLastMntOn().equals(befCollateralLocation.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempCollateralLocation==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (old_CollateralLocation!=null && !old_CollateralLocation.getLastMntOn().equals(tempCollateralLocation.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !collateralLocation.isWorkflow()){
				collateralLocation.setBefImage(befCollateralLocation);	
			}

			return auditDetail;
		}

}