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
 * FileName    		:  FinanceRepayPriorityServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceRepayPriorityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>FinanceRepayPriority</b>.<br>
 * 
 */
public class FinanceRepayPriorityServiceImpl extends GenericService<FinanceRepayPriority> implements FinanceRepayPriorityService {
	private static final Logger logger = Logger.getLogger(FinanceRepayPriorityServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinanceRepayPriorityDAO financeRepayPriorityDAO;

	public FinanceRepayPriorityServiceImpl() {
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
	 * @return the financeRepayPriorityDAO
	 */
	public FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}
	/**
	 * @param financeRepayPriorityDAO the financeRepayPriorityDAO to set
	 */
	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinRpyPriority/FinRpyPriority_Temp 
	 * 			by using FinanceRepayPriorityDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceRepayPriorityDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinRpyPriority by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceRepayPriority financeRepayPriority = (FinanceRepayPriority) auditHeader.getAuditDetail().getModelData();
		
		if (financeRepayPriority.isWorkflow()) {
			tableType="_Temp";
		}

		if (financeRepayPriority.isNew()) {
			getFinanceRepayPriorityDAO().save(financeRepayPriority,tableType);
		}else{
			getFinanceRepayPriorityDAO().update(financeRepayPriority,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinRpyPriority by using FinanceRepayPriorityDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinRpyPriority by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		FinanceRepayPriority financeRepayPriority = (FinanceRepayPriority) auditHeader.getAuditDetail().getModelData();
		getFinanceRepayPriorityDAO().delete(financeRepayPriority,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceRepayPriorityById fetch the details by using FinanceRepayPriorityDAO's getFinanceRepayPriorityById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceRepayPriority
	 */
	
	@Override
	public FinanceRepayPriority getFinanceRepayPriorityById(String id) {
		return getFinanceRepayPriorityDAO().getFinanceRepayPriorityById(id,"_View");
	}
	/**
	 * getApprovedFinanceRepayPriorityById fetch the details by using FinanceRepayPriorityDAO's getFinanceRepayPriorityById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinRpyPriority.
	 * @param id (String)
	 * @return FinanceRepayPriority
	 */
	
	public FinanceRepayPriority getApprovedFinanceRepayPriorityById(String id) {
		return getFinanceRepayPriorityDAO().getFinanceRepayPriorityById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceRepayPriorityDAO().delete
	 * with parameters financeRepayPriority,"" b) NEW Add new record in to main table by using
	 * getFinanceRepayPriorityDAO().save with parameters financeRepayPriority,"" c) EDIT Update record in the main table
	 * by using getFinanceRepayPriorityDAO().update with parameters financeRepayPriority,"" 3) Delete the record from
	 * the workFlow table by using getFinanceRepayPriorityDAO().delete with parameters financeRepayPriority,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtFinRpyPriority by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtFinRpyPriority by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
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

		FinanceRepayPriority financeRepayPriority = new FinanceRepayPriority();
		BeanUtils.copyProperties((FinanceRepayPriority) auditHeader.getAuditDetail().getModelData(),
				financeRepayPriority);

		if (financeRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFinanceRepayPriorityDAO().delete(financeRepayPriority, "");

		} else {
			financeRepayPriority.setRoleCode("");
			financeRepayPriority.setNextRoleCode("");
			financeRepayPriority.setTaskId("");
			financeRepayPriority.setNextTaskId("");
			financeRepayPriority.setWorkflowId(0);

			if (financeRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeRepayPriority.setRecordType("");
				getFinanceRepayPriorityDAO().save(financeRepayPriority, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeRepayPriority.setRecordType("");
				getFinanceRepayPriorityDAO().update(financeRepayPriority, "");
			}
		}

		getFinanceRepayPriorityDAO().delete(financeRepayPriority, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeRepayPriority);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceRepayPriorityDAO().delete with parameters financeRepayPriority,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinRpyPriority by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			FinanceRepayPriority financeRepayPriority = (FinanceRepayPriority) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getFinanceRepayPriorityDAO().delete(financeRepayPriority,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getFinanceRepayPriorityDAO().getErrorDetail with Error ID and language as parameters.
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
			FinanceRepayPriority financeRepayPriority= (FinanceRepayPriority) auditDetail.getModelData();
			
			FinanceRepayPriority tempFinanceRepayPriority= null;
			if (financeRepayPriority.isWorkflow()){
				tempFinanceRepayPriority = getFinanceRepayPriorityDAO().getFinanceRepayPriorityById(financeRepayPriority.getId(), "_Temp");
			}
			FinanceRepayPriority befFinanceRepayPriority= getFinanceRepayPriorityDAO().getFinanceRepayPriorityById(financeRepayPriority.getId(), "");
			
			FinanceRepayPriority oldFinanceRepayPriority= financeRepayPriority.getBefImage();
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=financeRepayPriority.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinType")+":"+valueParm[0];
			
			if (financeRepayPriority.isNew()){ // for New record or new record into work flow
				
				if (!financeRepayPriority.isWorkflow()){// With out Work flow only new records  
					if (befFinanceRepayPriority !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (financeRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befFinanceRepayPriority !=null || tempFinanceRepayPriority!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befFinanceRepayPriority ==null || tempFinanceRepayPriority!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}		
				
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!financeRepayPriority.isWorkflow()){	// With out Work flow for update and delete
				
					if (befFinanceRepayPriority ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldFinanceRepayPriority!=null && !oldFinanceRepayPriority.getLastMntOn().equals(befFinanceRepayPriority.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempFinanceRepayPriority==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempFinanceRepayPriority!=null && oldFinanceRepayPriority!=null && !oldFinanceRepayPriority.getLastMntOn().equals(tempFinanceRepayPriority.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
			
			// Checking All data , if Same Priority Finance Type Exists or not
			if(!PennantConstants.RECORD_TYPE_DEL.equals(financeRepayPriority.getRecordType()) && 
					!PennantConstants.RECORD_TYPE_CAN.equals(financeRepayPriority.getRecordType())){
				
				List<String> priorityTypeList = getFinanceRepayPriorityDAO().getFinanceRpyPriorByPriority(
						financeRepayPriority.getFinType(),financeRepayPriority.getFinPriority(), "_View");
				if (priorityTypeList != null && !priorityTypeList.isEmpty()) {	
					String[] errParm1= new String[1];
					errParm1[0]=PennantJavaUtil.getLabel("label_FinPriority")+":"+String.valueOf(financeRepayPriority.getFinPriority());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65005", errParm1,valueParm), usrLanguage));
				}
			}
			
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeRepayPriority.isWorkflow()){
				financeRepayPriority.setBefImage(befFinanceRepayPriority);	
			}

			return auditDetail;
		}

}