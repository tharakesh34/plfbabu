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
 * FileName    		:  FinanceCampaignServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.solutionfactory.FinanceCampaignDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.FinanceCampaign;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.solutionfactory.FinanceCampaignService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinanceCampaign</b>.<br>
 * 
 */
public class FinanceCampaignServiceImpl extends GenericService<FinanceCampaign> implements FinanceCampaignService {
	private final static Logger logger = Logger.getLogger(FinanceCampaignServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinanceCampaignDAO financeCampaignDAO;

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
	 * @return the financeCampaignDAO
	 */
	public FinanceCampaignDAO getFinanceCampaignDAO() {
		return financeCampaignDAO;
	}
	/**
	 * @param financeCampaignDAO the financeCampaignDAO to set
	 */
	public void setFinanceCampaignDAO(FinanceCampaignDAO financeCampaignDAO) {
		this.financeCampaignDAO = financeCampaignDAO;
	}

	/**
	 * @return the financeCampaign
	 */
	@Override
	public FinanceCampaign getFinanceCampaign() {
		return getFinanceCampaignDAO().getFinanceCampaign();
	}
	/**
	 * @return the financeCampaign for New Record
	 */
	@Override
	public FinanceCampaign getNewFinanceCampaign() {
		return getFinanceCampaignDAO().getNewFinanceCampaign();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table RMTFinCampaign/RMTFinCampaign_Temp 
	 * 			by using FinanceCampaignDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceCampaignDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtRMTFinCampaign by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceCampaign financeCampaign = (FinanceCampaign) auditHeader.getAuditDetail().getModelData();
		
		if (financeCampaign.isWorkflow()) {
			tableType="_TEMP";
		}

		if (financeCampaign.isNew()) {
			getFinanceCampaignDAO().save(financeCampaign,tableType);
		}else{
			getFinanceCampaignDAO().update(financeCampaign,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table RMTFinCampaign by using FinanceCampaignDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtRMTFinCampaign by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		FinanceCampaign financeCampaign = (FinanceCampaign) auditHeader.getAuditDetail().getModelData();
		getFinanceCampaignDAO().delete(financeCampaign,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceCampaignById fetch the details by using FinanceCampaignDAO's getFinanceCampaignById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceCampaign
	 */
	
	@Override
	public FinanceCampaign getFinanceCampaignById(String id) {
		return getFinanceCampaignDAO().getFinanceCampaignById(id,"_View");
	}
	/**
	 * getApprovedFinanceCampaignById fetch the details by using FinanceCampaignDAO's getFinanceCampaignById method .
	 * with parameter id and type as blank. it fetches the approved records from the RMTFinCampaign.
	 * @param id (String)
	 * @return FinanceCampaign
	 */
	
	public FinanceCampaign getApprovedFinanceCampaignById(String id) {
		return getFinanceCampaignDAO().getFinanceCampaignById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param FinanceCampaign (financeCampaign)
 	 * @return financeCampaign
	 */
	@Override
	public FinanceCampaign refresh(FinanceCampaign financeCampaign) {
		logger.debug("Entering");
		getFinanceCampaignDAO().refresh(financeCampaign);
		getFinanceCampaignDAO().initialize(financeCampaign);
		logger.debug("Leaving");
		return financeCampaign;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceCampaignDAO().delete with parameters financeCampaign,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceCampaignDAO().save with parameters financeCampaign,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceCampaignDAO().update with parameters financeCampaign,""
	 * 3)	Delete the record from the workFlow table by using getFinanceCampaignDAO().delete with parameters financeCampaign,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtRMTFinCampaign by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtRMTFinCampaign by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinanceCampaign financeCampaign = new FinanceCampaign();
		BeanUtils.copyProperties((FinanceCampaign) auditHeader.getAuditDetail().getModelData(), financeCampaign);

		if (financeCampaign.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getFinanceCampaignDAO().delete(financeCampaign,"");
				
			} else {
				financeCampaign.setRoleCode("");
				financeCampaign.setNextRoleCode("");
				financeCampaign.setTaskId("");
				financeCampaign.setNextTaskId("");
				financeCampaign.setWorkflowId(0);
				
				if (financeCampaign.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					financeCampaign.setRecordType("");
					getFinanceCampaignDAO().save(financeCampaign,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					financeCampaign.setRecordType("");
					getFinanceCampaignDAO().update(financeCampaign,"");
				}
			}
			
			getFinanceCampaignDAO().delete(financeCampaign,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(financeCampaign);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceCampaignDAO().delete with parameters financeCampaign,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtRMTFinCampaign by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			FinanceCampaign financeCampaign = (FinanceCampaign) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getFinanceCampaignDAO().delete(financeCampaign,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getFinanceCampaignDAO().getErrorDetail with Error ID and language as parameters.
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
			FinanceCampaign financeCampaign= (FinanceCampaign) auditDetail.getModelData();
			
			FinanceCampaign tempFinanceCampaign= null;
			if (financeCampaign.isWorkflow()){
				tempFinanceCampaign = getFinanceCampaignDAO().getFinanceCampaignById(financeCampaign.getId(), "_Temp");
			}
			FinanceCampaign befFinanceCampaign= getFinanceCampaignDAO().getFinanceCampaignById(financeCampaign.getId(), "");
			
			FinanceCampaign old_FinanceCampaign= financeCampaign.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=financeCampaign.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FCCode")+":"+valueParm[0];
			
			if (financeCampaign.isNew()){ // for New record or new record into work flow
				
				if (!financeCampaign.isWorkflow()){// With out Work flow only new records  
					if (befFinanceCampaign !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (financeCampaign.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befFinanceCampaign !=null || tempFinanceCampaign!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befFinanceCampaign ==null || tempFinanceCampaign!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!financeCampaign.isWorkflow()){	// With out Work flow for update and delete
				
					if (befFinanceCampaign ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (old_FinanceCampaign!=null && !old_FinanceCampaign.getLastMntOn().equals(befFinanceCampaign.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempFinanceCampaign==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (old_FinanceCampaign!=null && !old_FinanceCampaign.getLastMntOn().equals(tempFinanceCampaign.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeCampaign.isWorkflow()){
				financeCampaign.setBefImage(befFinanceCampaign);	
			}

			return auditDetail;
		}

}