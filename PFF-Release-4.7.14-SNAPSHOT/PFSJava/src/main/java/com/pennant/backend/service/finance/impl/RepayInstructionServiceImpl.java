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
 * FileName    		:  RepayInstructionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.RepayInstructionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>RepayInstruction</b>.<br>
 * 
 */
public class RepayInstructionServiceImpl extends GenericService<RepayInstruction> implements RepayInstructionService {
	private static final Logger logger = Logger.getLogger(RepayInstructionServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private RepayInstructionDAO repayInstructionDAO;

	public RepayInstructionServiceImpl() {
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
	 * @return the repayInstructionDAO
	 */
	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	/**
	 * @param repayInstructionDAO the repayInstructionDAO to set
	 */
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinRepayInstruction/FinRepayInstruction_Temp 
	 * 			by using RepayInstructionDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using RepayInstructionDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinRepayInstruction by using auditHeaderDAO.addAudit(auditHeader)
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
		RepayInstruction repayInstruction = (RepayInstruction) auditHeader.getAuditDetail().getModelData();
		
		if (repayInstruction.isWorkflow()) {
			tableType="_Temp";
		}

		if (repayInstruction.isNew()) {
			getRepayInstructionDAO().save(repayInstruction,tableType, isWIF);
		}else{
			getRepayInstructionDAO().update(repayInstruction,tableType, isWIF);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinRepayInstruction by using RepayInstructionDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinRepayInstruction by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		RepayInstruction repayInstruction = (RepayInstruction) auditHeader.getAuditDetail().getModelData();
		getRepayInstructionDAO().delete(repayInstruction,"",isWIF);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRepayInstructionById fetch the details by using RepayInstructionDAO's getRepayInstructionById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return RepayInstruction
	 */
	
	@Override
	public RepayInstruction getRepayInstructionById(String id,boolean isWIF) {
		return getRepayInstructionDAO().getRepayInstructionById(id,"_View", isWIF);
	}
	/**
	 * getApprovedRepayInstructionById fetch the details by using RepayInstructionDAO's getRepayInstructionById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinRepayInstruction.
	 * @param id (String)
	 * @return RepayInstruction
	 */
	
	public RepayInstruction getApprovedRepayInstructionById(String id,boolean isWIF) {
		return getRepayInstructionDAO().getRepayInstructionById(id,"_AView", isWIF);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getRepayInstructionDAO().delete with
	 * parameters repayInstruction,"" b) NEW Add new record in to main table by using getRepayInstructionDAO().save with
	 * parameters repayInstruction,"" c) EDIT Update record in the main table by using getRepayInstructionDAO().update
	 * with parameters repayInstruction,"" 3) Delete the record from the workFlow table by using
	 * getRepayInstructionDAO().delete with parameters repayInstruction,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtFinRepayInstruction by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtFinRepayInstruction by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", isWIF);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		RepayInstruction repayInstruction = new RepayInstruction();
		BeanUtils.copyProperties((RepayInstruction) auditHeader.getAuditDetail().getModelData(), repayInstruction);

		if (repayInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getRepayInstructionDAO().delete(repayInstruction, "", isWIF);

		} else {
			repayInstruction.setRoleCode("");
			repayInstruction.setNextRoleCode("");
			repayInstruction.setTaskId("");
			repayInstruction.setNextTaskId("");
			repayInstruction.setWorkflowId(0);

			if (repayInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				repayInstruction.setRecordType("");
				getRepayInstructionDAO().save(repayInstruction, "", isWIF);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				repayInstruction.setRecordType("");
				getRepayInstructionDAO().update(repayInstruction, "", isWIF);
			}
		}

		getRepayInstructionDAO().delete(repayInstruction, "_Temp", isWIF);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(repayInstruction);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getRepayInstructionDAO().delete with parameters repayInstruction,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinRepayInstruction by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader,boolean isWIF) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject", isWIF);
			if (!auditHeader.isNextProcess()) {
				logger.debug("Leaving");
				return auditHeader;
			}

			RepayInstruction repayInstruction = (RepayInstruction) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getRepayInstructionDAO().delete(repayInstruction,"_Temp", isWIF);
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getRepayInstructionDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)    
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean isWIF){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean isWIF){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			RepayInstruction repayInstruction= (RepayInstruction) auditDetail.getModelData();
			
			RepayInstruction tempRepayInstruction= null;
			if (repayInstruction.isWorkflow()){
				tempRepayInstruction = getRepayInstructionDAO().getRepayInstructionById(repayInstruction.getId(), "_Temp", isWIF);
			}
			RepayInstruction befRepayInstruction= getRepayInstructionDAO().getRepayInstructionById(repayInstruction.getId(), "", isWIF);
			
			RepayInstruction oldRepayInstruction= repayInstruction.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=repayInstruction.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (repayInstruction.isNew()){ // for New record or new record into work flow
				
				if (!repayInstruction.isWorkflow()){// With out Work flow only new records  
					if (befRepayInstruction !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (repayInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befRepayInstruction !=null || tempRepayInstruction!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befRepayInstruction ==null || tempRepayInstruction!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!repayInstruction.isWorkflow()){	// With out Work flow for update and delete
				
					if (befRepayInstruction ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldRepayInstruction!=null && !oldRepayInstruction.getLastMntOn().equals(befRepayInstruction.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempRepayInstruction==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempRepayInstruction!=null && oldRepayInstruction!=null && !oldRepayInstruction.getLastMntOn().equals(tempRepayInstruction.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !repayInstruction.isWorkflow()){
				repayInstruction.setBefImage(befRepayInstruction);	
			}

			return auditDetail;
		}

}