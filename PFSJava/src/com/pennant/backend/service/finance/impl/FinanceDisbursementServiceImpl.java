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
 * FileName    		:  FinanceDisbursementServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDisbursementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinanceDisbursement</b>.<br>
 * 
 */
public class FinanceDisbursementServiceImpl extends GenericService<FinanceDisbursement> implements FinanceDisbursementService {
	private final static Logger logger = Logger.getLogger(FinanceDisbursementServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinanceDisbursementDAO financeDisbursementDAO;

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
	 * @return the financeDisbursementDAO
	 */
	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}
	/**
	 * @param financeDisbursementDAO the financeDisbursementDAO to set
	 */
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	/**
	 * @return the financeDisbursement
	 */
	@Override
	public FinanceDisbursement getFinanceDisbursement(boolean isWIF) {
		return getFinanceDisbursementDAO().getFinanceDisbursement(isWIF);
	}
	/**
	 * @return the financeDisbursement for New Record
	 */
	@Override
	public FinanceDisbursement getNewFinanceDisbursement(boolean isWIF) {
		return getFinanceDisbursementDAO().getNewFinanceDisbursement(isWIF);
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinDisbursementDetails/FinDisbursementDetails_Temp 
	 * 			by using FinanceDisbursementDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceDisbursementDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		FinanceDisbursement financeDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
		
		if (financeDisbursement.isWorkflow()) {
			tableType="_TEMP";
		}

		if (financeDisbursement.isNew()) {
			getFinanceDisbursementDAO().save(financeDisbursement,tableType,isWIF);
		}else{
			getFinanceDisbursementDAO().update(financeDisbursement,tableType,isWIF);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinDisbursementDetails by using FinanceDisbursementDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		FinanceDisbursement financeDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
		getFinanceDisbursementDAO().delete(financeDisbursement,"",isWIF);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceDisbursementById fetch the details by using FinanceDisbursementDAO's getFinanceDisbursementById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceDisbursement
	 */
	
	@Override
	public FinanceDisbursement getFinanceDisbursementById(String id,boolean isWIF) {
		return getFinanceDisbursementDAO().getFinanceDisbursementById(id,"_View",isWIF);
	}
	/**
	 * getApprovedFinanceDisbursementById fetch the details by using FinanceDisbursementDAO's getFinanceDisbursementById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinDisbursementDetails.
	 * @param id (String)
	 * @return FinanceDisbursement
	 */
	
	public FinanceDisbursement getApprovedFinanceDisbursementById(String id,boolean isWIF) {
		return getFinanceDisbursementDAO().getFinanceDisbursementById(id,"_AView",isWIF);
	}
		
	/**
	 * This method refresh the Record.
	 * @param FinanceDisbursement (financeDisbursement)
 	 * @return financeDisbursement
	 */
	@Override
	public FinanceDisbursement refresh(FinanceDisbursement financeDisbursement) {
		logger.debug("Entering");
		getFinanceDisbursementDAO().refresh(financeDisbursement);
		getFinanceDisbursementDAO().initialize(financeDisbursement);
		logger.debug("Leaving");
		return financeDisbursement;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceDisbursementDAO().delete with parameters financeDisbursement,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceDisbursementDAO().save with parameters financeDisbursement,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceDisbursementDAO().update with parameters financeDisbursement,""
	 * 3)	Delete the record from the workFlow table by using getFinanceDisbursementDAO().delete with parameters financeDisbursement,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",isWIF);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		BeanUtils.copyProperties((FinanceDisbursement) auditHeader.getAuditDetail().getModelData(), financeDisbursement);

		if (financeDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getFinanceDisbursementDAO().delete(financeDisbursement,"",isWIF);
				
			} else {
				financeDisbursement.setRoleCode("");
				financeDisbursement.setNextRoleCode("");
				financeDisbursement.setTaskId("");
				financeDisbursement.setNextTaskId("");
				financeDisbursement.setWorkflowId(0);
				
				if (financeDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					financeDisbursement.setRecordType("");
					getFinanceDisbursementDAO().save(financeDisbursement,"",isWIF);
				} else {
					tranType=PennantConstants.TRAN_UPD;
					financeDisbursement.setRecordType("");
					getFinanceDisbursementDAO().update(financeDisbursement,"",isWIF);
				}
			}
			
			getFinanceDisbursementDAO().delete(financeDisbursement,"_TEMP",isWIF);
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(financeDisbursement);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceDisbursementDAO().delete with parameters financeDisbursement,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinDisbursementDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader,boolean isWIF) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject",isWIF);
			if (!auditHeader.isNextProcess()) {
				logger.debug("Leaving");
				return auditHeader;
			}

			FinanceDisbursement financeDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getFinanceDisbursementDAO().delete(financeDisbursement,"_TEMP",isWIF);
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getFinanceDisbursementDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)    
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean isWIF){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,isWIF);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean isWIF){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			FinanceDisbursement financeDisbursement= (FinanceDisbursement) auditDetail.getModelData();
			
			FinanceDisbursement tempFinanceDisbursement= null;
			if (financeDisbursement.isWorkflow()){
				tempFinanceDisbursement = getFinanceDisbursementDAO().getFinanceDisbursementById(financeDisbursement.getId(), "_Temp",isWIF);
			}
			FinanceDisbursement befFinanceDisbursement= getFinanceDisbursementDAO().getFinanceDisbursementById(financeDisbursement.getId(), "",isWIF);
			
			FinanceDisbursement old_FinanceDisbursement= financeDisbursement.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=financeDisbursement.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (financeDisbursement.isNew()){ // for New record or new record into work flow
				
				if (!financeDisbursement.isWorkflow()){// With out Work flow only new records  
					if (befFinanceDisbursement !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (financeDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befFinanceDisbursement !=null || tempFinanceDisbursement!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befFinanceDisbursement ==null || tempFinanceDisbursement!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!financeDisbursement.isWorkflow()){	// With out Work flow for update and delete
				
					if (befFinanceDisbursement ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (old_FinanceDisbursement!=null && !old_FinanceDisbursement.getLastMntOn().equals(befFinanceDisbursement.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempFinanceDisbursement==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (old_FinanceDisbursement!=null && !old_FinanceDisbursement.getLastMntOn().equals(tempFinanceDisbursement.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeDisbursement.isWorkflow()){
				financeDisbursement.setBefImage(befFinanceDisbursement);	
			}

			return auditDetail;
		}

}