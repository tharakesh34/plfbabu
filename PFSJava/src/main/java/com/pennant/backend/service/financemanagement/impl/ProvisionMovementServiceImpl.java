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
 * FileName    		:  ProvisionMovementServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.financemanagement.impl;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.ProvisionMovementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ProvisionMovement</b>.<br>
 * 
 */
public class ProvisionMovementServiceImpl extends GenericService<ProvisionMovement> implements ProvisionMovementService {
	private static final Logger logger = Logger.getLogger(ProvisionMovementServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private PostingsDAO postingsDAO;

	public ProvisionMovementServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}
	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinProvMovements/FinProvMovements_Temp 
	 * 			by using ProvisionMovementDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ProvisionMovementDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinProvMovements by using auditHeaderDAO.addAudit(auditHeader)
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
		ProvisionMovement provisionMovement = (ProvisionMovement) auditHeader.getAuditDetail().getModelData();
		
		if (provisionMovement.isWorkflow()) {
			tableType="_Temp";
		}

		if (provisionMovement.isNew()) {
			getProvisionMovementDAO().save(provisionMovement,tableType);
		}else{
			getProvisionMovementDAO().update(provisionMovement,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinProvMovements by using ProvisionMovementDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinProvMovements by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		ProvisionMovement provisionMovement = (ProvisionMovement) auditHeader.getAuditDetail().getModelData();
		getProvisionMovementDAO().delete(provisionMovement,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProvisionMovementById fetch the details by using ProvisionMovementDAO's getProvisionMovementById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ProvisionMovement
	 */
	
	@Override
	public ProvisionMovement getProvisionMovementById(String id, Date movementDate,long linkTransId) {
		ProvisionMovement movement = getProvisionMovementDAO().getProvisionMovementById(id,movementDate, "_AView");
		if(linkTransId != Long.MIN_VALUE){
			movement.setPostingsList( getPostingsDAO().getPostingsByLinkTransId(linkTransId));
		}
		return movement;
	}
	/**
	 * getApprovedProvisionMovementById fetch the details by using ProvisionMovementDAO's getProvisionMovementById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinProvMovements.
	 * @param id (String)
	 * @return ProvisionMovement
	 */
	
	public ProvisionMovement getApprovedProvisionMovementById(String id, Date movementDate) {
		return getProvisionMovementDAO().getProvisionMovementById(id,movementDate,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getProvisionMovementDAO().delete with
	 * parameters provisionMovement,"" b) NEW Add new record in to main table by using getProvisionMovementDAO().save
	 * with parameters provisionMovement,"" c) EDIT Update record in the main table by using
	 * getProvisionMovementDAO().update with parameters provisionMovement,"" 3) Delete the record from the workFlow
	 * table by using getProvisionMovementDAO().delete with parameters provisionMovement,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtFinProvMovements by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtFinProvMovements by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ProvisionMovement provisionMovement = new ProvisionMovement();
		BeanUtils.copyProperties((ProvisionMovement) auditHeader.getAuditDetail().getModelData(), provisionMovement);

		if (provisionMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getProvisionMovementDAO().delete(provisionMovement, "");

		} else {
			provisionMovement.setRoleCode("");
			provisionMovement.setNextRoleCode("");
			provisionMovement.setTaskId("");
			provisionMovement.setNextTaskId("");
			provisionMovement.setWorkflowId(0);

			if (provisionMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				provisionMovement.setRecordType("");
				getProvisionMovementDAO().save(provisionMovement, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				provisionMovement.setRecordType("");
				getProvisionMovementDAO().update(provisionMovement, "");
			}
		}

		getProvisionMovementDAO().delete(provisionMovement, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provisionMovement);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getProvisionMovementDAO().delete with parameters provisionMovement,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinProvMovements by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			ProvisionMovement provisionMovement = (ProvisionMovement) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getProvisionMovementDAO().delete(provisionMovement,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getProvisionMovementDAO().getErrorDetail with Error ID and language as parameters.
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
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			ProvisionMovement provisionMovement= (ProvisionMovement) auditDetail.getModelData();
			
			ProvisionMovement tempProvisionMovement= null;
			if (provisionMovement.isWorkflow()){
				tempProvisionMovement = getProvisionMovementDAO().getProvisionMovementById(provisionMovement.getId(),provisionMovement.getProvMovementDate(), "_Temp");
			}
			ProvisionMovement befProvisionMovement= getProvisionMovementDAO().getProvisionMovementById(provisionMovement.getId(),provisionMovement.getProvMovementDate(), "");
			ProvisionMovement oldProvisionMovement= provisionMovement.getBefImage();
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=provisionMovement.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (provisionMovement.isNew()){ // for New record or new record into work flow
				
				if (!provisionMovement.isWorkflow()){// With out Work flow only new records  
					if (befProvisionMovement !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (provisionMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befProvisionMovement !=null || tempProvisionMovement!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befProvisionMovement ==null || tempProvisionMovement!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!provisionMovement.isWorkflow()){	// With out Work flow for update and delete
				
					if (befProvisionMovement ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldProvisionMovement!=null && !oldProvisionMovement.getLastMntOn().equals(befProvisionMovement.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempProvisionMovement==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempProvisionMovement!=null && oldProvisionMovement!=null && !oldProvisionMovement.getLastMntOn().equals(tempProvisionMovement.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !provisionMovement.isWorkflow()){
				provisionMovement.setBefImage(befProvisionMovement);	
			}

			return auditDetail;
		}

}