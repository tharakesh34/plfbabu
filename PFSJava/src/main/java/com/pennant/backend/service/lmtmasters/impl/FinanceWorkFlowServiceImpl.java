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
 * FileName    		:  FinanceWorkFlowServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FacilityReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>FinanceWorkFlow</b>.<br>
 * 
 */
public class FinanceWorkFlowServiceImpl extends GenericService<FinanceWorkFlow> implements FinanceWorkFlowService {
	
	private static final Logger logger = Logger.getLogger(FinanceWorkFlowServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FacilityReferenceDetailDAO facilityReferenceDetailDAO;

	public FinanceWorkFlowServiceImpl() {
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

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}
	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}
	
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
	    return financeReferenceDetailDAO;
    }
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
	    this.financeReferenceDetailDAO = financeReferenceDetailDAO;
    }

	public FacilityReferenceDetailDAO getFacilityReferenceDetailDAO() {
	    return facilityReferenceDetailDAO;
    }
	public void setFacilityReferenceDetailDAO(FacilityReferenceDetailDAO facilityReferenceDetailDAO) {
	    this.facilityReferenceDetailDAO = facilityReferenceDetailDAO;
    }
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LMTFinanceWorkFlowDef/LMTFinanceWorkFlowDef_Temp 
	 * 			by using FinanceWorkFlowDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceWorkFlowDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTFinanceWorkFlowDef by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceWorkFlow financeWorkFlow = (FinanceWorkFlow) auditHeader.getAuditDetail().getModelData();

		if (financeWorkFlow.isWorkflow()) {
			tableType="_Temp";
		}

		if (financeWorkFlow.isNew()) {
			getFinanceWorkFlowDAO().save(financeWorkFlow,tableType);
		}else{
			getFinanceWorkFlowDAO().update(financeWorkFlow,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTFinanceWorkFlowDef by using FinanceWorkFlowDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTFinanceWorkFlowDef by using auditHeaderDAO.addAudit(auditHeader)    
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

		FinanceWorkFlow financeWorkFlow = (FinanceWorkFlow) auditHeader.getAuditDetail().getModelData();
		getFinanceWorkFlowDAO().delete(financeWorkFlow,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceWorkFlowById fetch the details by using FinanceWorkFlowDAO's getFinanceWorkFlowById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceWorkFlow
	 */
	@Override
	public FinanceWorkFlow getFinanceWorkFlowById(String id, String finEvent , String moduleName) {
		return getFinanceWorkFlowDAO().getFinanceWorkFlowById(id, finEvent, moduleName, "_View");
	}

	/**
	 * getApprovedFinanceWorkFlowById fetch the details by using FinanceWorkFlowDAO's getFinanceWorkFlowById method .
	 * with parameter id and type as blank. it fetches the approved records from the LMTFinanceWorkFlowDef.
	 * @param id (String)
	 * @return FinanceWorkFlow
	 */

	public FinanceWorkFlow getApprovedFinanceWorkFlowById(String id, String finEvent, String moduleName) {
		return getFinanceWorkFlowDAO().getFinanceWorkFlowById(id, finEvent , moduleName, "_AView");
	}
	
	/**
	 * Method for Fetching Workflow Type from Workflow Definition Details
	 */
	public String getFinanceWorkFlowType(String id, String finEvent, String moduleName) {
		return getFinanceWorkFlowDAO().getFinanceWorkFlowType(id, finEvent , moduleName, "");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceWorkFlowDAO().delete with parameters financeWorkFlow,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceWorkFlowDAO().save with parameters financeWorkFlow,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceWorkFlowDAO().update with parameters financeWorkFlow,""
	 * 3)	Delete the record from the workFlow table by using getFinanceWorkFlowDAO().delete with parameters financeWorkFlow,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTFinanceWorkFlowDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTFinanceWorkFlowDef by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinanceWorkFlow financeWorkFlow = new FinanceWorkFlow();
		BeanUtils.copyProperties((FinanceWorkFlow) auditHeader.getAuditDetail().getModelData(), financeWorkFlow);

		if (financeWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getFinanceWorkFlowDAO().delete(financeWorkFlow,"");

		} else {
			financeWorkFlow.setRoleCode("");
			financeWorkFlow.setNextRoleCode("");
			financeWorkFlow.setTaskId("");
			financeWorkFlow.setNextTaskId("");
			financeWorkFlow.setWorkflowId(0);

			if (financeWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				financeWorkFlow.setRecordType("");
				getFinanceWorkFlowDAO().save(financeWorkFlow,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				financeWorkFlow.setRecordType("");
				
				//Remove Finance Process Editor Details If Workflow Modified from Previous Version
				FinanceWorkFlow apprvFinWorkflow = getFinanceWorkFlowDAO().getFinanceWorkFlowById(financeWorkFlow.getId(),
						financeWorkFlow.getFinEvent(), financeWorkFlow.getModuleName(), "");
				if(apprvFinWorkflow != null){
					if(!apprvFinWorkflow.getWorkFlowType().equals(financeWorkFlow.getWorkFlowType())){
						if(PennantConstants.WORFLOW_MODULE_FINANCE.equals(financeWorkFlow.getModuleName()) || 
								PennantConstants.WORFLOW_MODULE_PROMOTION.equals(financeWorkFlow.getModuleName())){
							getFinanceReferenceDetailDAO().deleteByFinType(financeWorkFlow.getFinType(),financeWorkFlow.getFinEvent(), "");
						}else if(PennantConstants.WORFLOW_MODULE_FACILITY.equals(financeWorkFlow.getModuleName())){
							getFacilityReferenceDetailDAO().deleteByFinType(financeWorkFlow.getFinType(), "");
						}
					}
				}
				
				getFinanceWorkFlowDAO().update(financeWorkFlow,"");
			}
		}

		getFinanceWorkFlowDAO().delete(financeWorkFlow,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeWorkFlow);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceWorkFlowDAO().delete with parameters financeWorkFlow,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTFinanceWorkFlowDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinanceWorkFlow financeWorkFlow = (FinanceWorkFlow) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceWorkFlowDAO().delete(financeWorkFlow,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getFinanceWorkFlowDAO().getErrorDetail with Error ID and language as parameters.
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

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getFinanceWorkFlowDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		FinanceWorkFlow financeWorkFlow= (FinanceWorkFlow) auditDetail.getModelData();

		FinanceWorkFlow tempFinanceWorkFlow= null;
		if (financeWorkFlow.isWorkflow()){
			tempFinanceWorkFlow = getFinanceWorkFlowDAO().getFinanceWorkFlowById(financeWorkFlow.getId(),financeWorkFlow.getFinEvent(), financeWorkFlow.getModuleName(), "_Temp");
		}
		FinanceWorkFlow befFinanceWorkFlow= getFinanceWorkFlowDAO().getFinanceWorkFlowById(financeWorkFlow.getId(),financeWorkFlow.getFinEvent(), financeWorkFlow.getModuleName(), "");
		FinanceWorkFlow oldFinanceWorkFlow= financeWorkFlow.getBefImage();

		String[] errParm= new String[2];
		String[] valueParm= new String[2];
		valueParm[0]=financeWorkFlow.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinanceWorkFlowDialog_FinType.value")+":"+valueParm[0];
		valueParm[1]=financeWorkFlow.getFinEvent();
		errParm[1]=PennantJavaUtil.getLabel("label_FinanceWorkFlowDialog_FinEvent.value")+":"+valueParm[1];

		if (financeWorkFlow.isNew()){ // for New record or new record into work flow

			if (!financeWorkFlow.isWorkflow()){// With out Work flow only new records  
				if (befFinanceWorkFlow !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinanceWorkFlow !=null || tempFinanceWorkFlow!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinanceWorkFlow ==null || tempFinanceWorkFlow!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeWorkFlow.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceWorkFlow ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinanceWorkFlow!=null && !oldFinanceWorkFlow.getLastMntOn().equals(befFinanceWorkFlow.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinanceWorkFlow==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempFinanceWorkFlow!=null && oldFinanceWorkFlow!=null && !oldFinanceWorkFlow.getLastMntOn().equals(tempFinanceWorkFlow.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeWorkFlow.isWorkflow()){
			financeWorkFlow.setBefImage(befFinanceWorkFlow);	
		}

		return auditDetail;
	}

	@Override
	public int getVASProductCode(String finType) {
		
		return getFinanceWorkFlowDAO().getVASProductCode(finType, "");
	}

}