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
 * FileName    		:  WIFFinanceDisbursementServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.impl;



import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.WIFFinanceDisbursementDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.WIFFinanceDisbursementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinanceDisbursement</b>.<br>
 * 
 */
public class WIFFinanceDisbursementServiceImpl extends GenericService<FinanceDisbursement> implements WIFFinanceDisbursementService {
	private final static Logger logger = Logger.getLogger(WIFFinanceDisbursementServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private WIFFinanceDisbursementDAO wIFFinanceDisbursementDAO;

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
	 * @return the wIFFinanceDisbursementDAO
	 */
	public WIFFinanceDisbursementDAO getWIFFinanceDisbursementDAO() {
		return wIFFinanceDisbursementDAO;
	}
	/**
	 * @param wIFFinanceDisbursementDAO the wIFFinanceDisbursementDAO to set
	 */
	public void setWIFFinanceDisbursementDAO(WIFFinanceDisbursementDAO wIFFinanceDisbursementDAO) {
		this.wIFFinanceDisbursementDAO = wIFFinanceDisbursementDAO;
	}

	/**
	 * @return the wIFFinanceDisbursement
	 */
	@Override
	public FinanceDisbursement getWIFFinanceDisbursement() {
		return getWIFFinanceDisbursementDAO().getWIFFinanceDisbursement();
	}
	/**
	 * @return the wIFFinanceDisbursement for New Record
	 */
	@Override
	public FinanceDisbursement getNewWIFFinanceDisbursement() {
		return getWIFFinanceDisbursementDAO().getNewWIFFinanceDisbursement();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table WIFFinDisbursementDetails/WIFFinDisbursementDetails_Temp 
	 * 			by using WIFFinanceDisbursementDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using WIFFinanceDisbursementDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceDisbursement wIFFinanceDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
		
		if (wIFFinanceDisbursement.isWorkflow()) {
			tableType="_TEMP";
		}

		if (wIFFinanceDisbursement.isNew()) {
			getWIFFinanceDisbursementDAO().save(wIFFinanceDisbursement,tableType);
		}else{
			getWIFFinanceDisbursementDAO().update(wIFFinanceDisbursement,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table WIFFinDisbursementDetails by using WIFFinanceDisbursementDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		FinanceDisbursement wIFFinanceDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
		getWIFFinanceDisbursementDAO().delete(wIFFinanceDisbursement,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getWIFFinanceDisbursementById fetch the details by using WIFFinanceDisbursementDAO's getWIFFinanceDisbursementById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceDisbursement
	 */
	
	@Override
	public FinanceDisbursement getWIFFinanceDisbursementById(String id) {
		return getWIFFinanceDisbursementDAO().getWIFFinanceDisbursementById(id,"_View");
	}
	/**
	 * getApprovedWIFFinanceDisbursementById fetch the details by using WIFFinanceDisbursementDAO's getWIFFinanceDisbursementById method .
	 * with parameter id and type as blank. it fetches the approved records from the WIFFinDisbursementDetails.
	 * @param id (String)
	 * @return WIFFinanceDisbursement
	 */
	
	public FinanceDisbursement getApprovedWIFFinanceDisbursementById(String id) {
		return getWIFFinanceDisbursementDAO().getWIFFinanceDisbursementById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param WIFFinanceDisbursement (wIFFinanceDisbursement)
 	 * @return wIFFinanceDisbursement
	 */
	@Override
	public FinanceDisbursement refresh(FinanceDisbursement wIFFinanceDisbursement) {
		logger.debug("Entering");
		getWIFFinanceDisbursementDAO().refresh(wIFFinanceDisbursement);
		getWIFFinanceDisbursementDAO().initialize(wIFFinanceDisbursement);
		logger.debug("Leaving");
		return wIFFinanceDisbursement;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getWIFFinanceDisbursementDAO().delete with parameters wIFFinanceDisbursement,""
	 * 		b)  NEW		Add new record in to main table by using getWIFFinanceDisbursementDAO().save with parameters wIFFinanceDisbursement,""
	 * 		c)  EDIT	Update record in the main table by using getWIFFinanceDisbursementDAO().update with parameters wIFFinanceDisbursement,""
	 * 3)	Delete the record from the workFlow table by using getWIFFinanceDisbursementDAO().delete with parameters wIFFinanceDisbursement,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtWIFFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtWIFFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		BeanUtils.copyProperties((FinanceDisbursement) auditHeader.getAuditDetail().getModelData(), wIFFinanceDisbursement);

		if (wIFFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getWIFFinanceDisbursementDAO().delete(wIFFinanceDisbursement,"");
				
			} else {
				wIFFinanceDisbursement.setRoleCode("");
				wIFFinanceDisbursement.setNextRoleCode("");
				wIFFinanceDisbursement.setTaskId("");
				wIFFinanceDisbursement.setNextTaskId("");
				wIFFinanceDisbursement.setWorkflowId(0);
				
				if (wIFFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					wIFFinanceDisbursement.setRecordType("");
					getWIFFinanceDisbursementDAO().save(wIFFinanceDisbursement,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					wIFFinanceDisbursement.setRecordType("");
					getWIFFinanceDisbursementDAO().update(wIFFinanceDisbursement,"");
				}
			}
			
			getWIFFinanceDisbursementDAO().delete(wIFFinanceDisbursement,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(wIFFinanceDisbursement);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getWIFFinanceDisbursementDAO().delete with parameters wIFFinanceDisbursement,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			FinanceDisbursement wIFFinanceDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getWIFFinanceDisbursementDAO().delete(wIFFinanceDisbursement,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getWIFFinanceDisbursementDAO().getErrorDetail with Error ID and language as parameters.
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
			FinanceDisbursement wIFFinanceDisbursement= (FinanceDisbursement) auditDetail.getModelData();
			
			FinanceDisbursement tempWIFFinanceDisbursement= null;
			if (wIFFinanceDisbursement.isWorkflow()){
				tempWIFFinanceDisbursement = getWIFFinanceDisbursementDAO().getWIFFinanceDisbursementById(wIFFinanceDisbursement.getId(), "_Temp");
			}
			FinanceDisbursement befWIFFinanceDisbursement= getWIFFinanceDisbursementDAO().getWIFFinanceDisbursementById(wIFFinanceDisbursement.getId(), "");
			
			FinanceDisbursement oldWIFFinanceDisbursement= wIFFinanceDisbursement.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=wIFFinanceDisbursement.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (wIFFinanceDisbursement.isNew()){ // for New record or new record into work flow
				
				if (!wIFFinanceDisbursement.isWorkflow()){// With out Work flow only new records  
					if (befWIFFinanceDisbursement !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (wIFFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befWIFFinanceDisbursement !=null || tempWIFFinanceDisbursement!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befWIFFinanceDisbursement ==null || tempWIFFinanceDisbursement!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!wIFFinanceDisbursement.isWorkflow()){	// With out Work flow for update and delete
				
					if (befWIFFinanceDisbursement ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldWIFFinanceDisbursement!=null && !oldWIFFinanceDisbursement.getLastMntOn().equals(befWIFFinanceDisbursement.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempWIFFinanceDisbursement==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (oldWIFFinanceDisbursement!=null && !oldWIFFinanceDisbursement.getLastMntOn().equals(tempWIFFinanceDisbursement.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !wIFFinanceDisbursement.isWorkflow()){
				wIFFinanceDisbursement.setBefImage(befWIFFinanceDisbursement);	
			}

			return auditDetail;
		}

}