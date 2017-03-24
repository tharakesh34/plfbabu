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
 * FileName    		:  ManagerChequeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.ManagerChequeInterfaceService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.financemanagement.ManagerChequeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.ManagerChequeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ManagerCheque</b>.<br>
 * 
 */
public class ManagerChequeServiceImpl extends GenericService<ManagerCheque> implements ManagerChequeService {
	private final static Logger logger = Logger.getLogger(ManagerChequeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private ManagerChequeDAO managerChequeDAO;
	private FinanceMainDAO financeMainDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;


	private ManagerChequeInterfaceService managerChequeInterfaceService;

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
	 * @return the managerChequeDAO
	 */
	public ManagerChequeDAO getManagerChequeDAO() {
		return managerChequeDAO;
	}
	/**
	 * @param managerChequeDAO the managerChequeDAO to set
	 */
	public void setManagerChequeDAO(ManagerChequeDAO managerChequeDAO) {
		this.managerChequeDAO = managerChequeDAO;
	}

	/**
	 * @return the managerCheque
	 */
	@Override
	public ManagerCheque getManagerCheque() {
		return getManagerChequeDAO().getManagerCheque();
	}
	/**
	 * @return the managerCheque for New Record
	 */
	@Override
	public ManagerCheque getNewManagerCheque() {
		return getManagerChequeDAO().getNewManagerCheque();
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public ManagerChequeInterfaceService getManagerChequeInterfaceService() {
		return managerChequeInterfaceService;
	}	
	public void setManagerChequeInterfaceService(ManagerChequeInterfaceService managerChequeInterfaceService) {
		this.managerChequeInterfaceService = managerChequeInterfaceService;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table ManagerCheques/ManagerCheques_Temp 
	 * 			by using ManagerChequeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ManagerChequeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtManagerCheques by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table ManagerCheques/ManagerCheques_Temp 
	 * 			by using ManagerChequeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ManagerChequeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtManagerCheques by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */


	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		ManagerCheque managerCheque = (ManagerCheque) auditHeader.getAuditDetail().getModelData();


		if (managerCheque.isWorkflow()) {
			tableType="_Temp";
		}

		if (managerCheque.isNew()) {
			managerCheque.setId(getManagerChequeDAO().save(managerCheque,tableType));
			auditHeader.getAuditDetail().setModelData(managerCheque);
			auditHeader.setAuditReference(String.valueOf(managerCheque.getChequeID()));
		}else{
			getManagerChequeDAO().update(managerCheque,tableType);
		}


		// Save Document Details
		if (managerCheque.getDocumentDetailsList() != null && managerCheque.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = managerCheque.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType, String.valueOf(managerCheque.getChequeID()),managerCheque);
			auditDetails.addAll(details);
		}

		// Save Reprint ManagerCheque Details
		if (managerCheque.getReprintManagerCheque() != null ) {
			List<AuditDetail> details = managerCheque.getAuditDetailMap().get("ReprintManagerChequeDetails");
			details = processingReprintManagerChequeDetailsList(details, tableType, managerCheque.getChequeID(),managerCheque);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table ManagerCheques by using ManagerChequeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtManagerCheques by using auditHeaderDAO.addAudit(auditHeader)    
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

		ManagerCheque managerCheque = (ManagerCheque) auditHeader.getAuditDetail().getModelData();
		getManagerChequeDAO().delete(managerCheque,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getManagerChequeById fetch the details by using ManagerChequeDAO's getManagerChequeById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ManagerCheque
	 */

	@Override
	public ManagerCheque getManagerChequeById(long id) {
		//ManagerCheque
		ManagerCheque managerCheque = new ManagerCheque();
		managerCheque =  getManagerChequeDAO().getManagerChequeById(id,"_View");
		if (managerCheque != null) {
			managerCheque.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(String.valueOf(id),
					FinanceConstants.MODULE_NAME,"", "_View"));//TODO: Set Module Name
			if(managerCheque.isReprint()){
				managerCheque.setReprintManagerCheque(getManagerChequeDAO().getReprintManagerChequeById(id,"_View"));
			}
		}
		return managerCheque;
	}
	/**
	 * getApprovedManagerChequeById fetch the details by using ManagerChequeDAO's getManagerChequeById method .
	 * with parameter id and type as blank. it fetches the approved records from the ManagerCheques.
	 * @param id (int)
	 * @return ManagerCheque
	 */

	public ManagerCheque getApprovedManagerChequeById(long id) {
		//ManagerCheque
		ManagerCheque managerCheque = new ManagerCheque();
		managerCheque =  getManagerChequeDAO().getManagerChequeById(id,"_AView");
		managerCheque.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(String.valueOf(id),FinanceConstants.MODULE_NAME,"", ""));//TODO: Set Module Name
		if(managerCheque.isReprint()){
			managerCheque.setReprintManagerCheque(getManagerChequeDAO().getReprintManagerChequeById(id,"_AView"));
		}
		return managerCheque;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getManagerChequeDAO().delete with
	 * parameters managerCheque,"" b) NEW Add new record in to main table by using getManagerChequeDAO().save with
	 * parameters managerCheque,"" c) EDIT Update record in the main table by using getManagerChequeDAO().update with
	 * parameters managerCheque,"" 3) Delete the record from the workFlow table by using getManagerChequeDAO().delete
	 * with parameters managerCheque,"_Temp" 4) Audit the record in to AuditHeader and AdtManagerCheques by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtManagerCheques by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ManagerCheque managerCheque = new ManagerCheque();
		BeanUtils.copyProperties((ManagerCheque) auditHeader.getAuditDetail().getModelData(), managerCheque);
		List<Object> resultList = null;

		ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();

		// Add Stop Order Reference in case of Reprint and Cancel ManagerCheques in Approval level
		if (managerCheque.isReprint() || managerCheque.isCancel()) {
			String stopOrderRef = "";
			boolean isStopOrderAvail = false;
			try {
				stopOrderRef = getManagerChequeInterfaceService().addStopOrderInEquation(managerCheque.getNostroAccount(), managerCheque.getChequeNo(),
						managerCheque.getChequeAmount(), managerCheque.getDraftCcy());
			} catch (AccountNotFoundException e) {
				logger.error("Exception: ", e);
				isStopOrderAvail = true;
				//stopOrderRef = e.getErrorMsg();//AIB
				stopOrderRef = e.getMessage();
			}
			if (StringUtils.isNotBlank(stopOrderRef) && !isStopOrderAvail) {
				if(managerCheque.isReprint()) {
					managerCheque.setStopOrderRef(stopOrderRef);
				} else {
					managerCheque.setStopOrderRef("0077");
				}
			}else if(isStopOrderAvail) {
				errorDetails.add(new ErrorDetails(PennantConstants.KEY_FIELD, "30543", PennantConstants.ERR_SEV_ERROR, stopOrderRef, new String[] {}, new String[] {}));
			}
		}

		//Postings to the Core 
		if(errorDetails.size() == 0){
			try {
				resultList = postingsPreparationUtil.processPostings(managerCheque.getReturnDataSetList());
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			//If Reversal Posting for Overdue Recover Details are Successful 
			String error = (String)(resultList.get(1));
			if(resultList != null && error != null){
				errorDetails.add(new ErrorDetails("", error, PennantConstants.ERR_SEV_ERROR,error, new String[] {}, new String[] {}));
			}
		}

		if (errorDetails != null && errorDetails.size() > 0) {
			auditHeader.setErrorList(errorDetails);
		}

		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// Save Document Details
		if (managerCheque.getDocumentDetailsList() != null && managerCheque.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = managerCheque.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", String.valueOf(managerCheque.getChequeID()),managerCheque);
			auditHeader.setAuditDetails(details);
		}
		listDocDeletion(managerCheque, "_Temp");

		// Save Reprint ManagerCheque Details
		if (managerCheque.getReprintManagerCheque() != null ) {
			List<AuditDetail> details = managerCheque.getAuditDetailMap().get("ReprintManagerChequeDetails");
			details = processingReprintManagerChequeDetailsList(details, "", managerCheque.getChequeID(),managerCheque);
			auditHeader.setAuditDetails(details);
			deleteReprintManagerCheque(managerCheque.getReprintManagerCheque(), "_Temp");
		}


		if (managerCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getManagerChequeDAO().delete(managerCheque,"");

		} else {
			managerCheque.setRoleCode("");
			managerCheque.setNextRoleCode("");
			managerCheque.setTaskId("");
			managerCheque.setNextTaskId("");
			managerCheque.setWorkflowId(0);

			if (managerCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				managerCheque.setRecordType("");
				getManagerChequeDAO().save(managerCheque,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				managerCheque.setRecordType("");
				getManagerChequeDAO().update(managerCheque,"");
			}
		}

		getManagerChequeDAO().delete(managerCheque,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(managerCheque);

		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset ManagerCheque Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(managerCheque);

		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getManagerChequeDAO().delete with parameters managerCheque,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtManagerCheques by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ManagerCheque managerCheque = (ManagerCheque) auditHeader.getAuditDetail().getModelData();

		// Save Document Details
		if (managerCheque.getDocumentDetailsList() != null && managerCheque.getDocumentDetailsList().size() > 0) {
			for(DocumentDetails docDetails : managerCheque.getDocumentDetailsList()){
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = managerCheque.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details,  "_Temp", String.valueOf(managerCheque.getChequeID()),managerCheque);
			auditHeader.setAuditDetails(details);
			//listDocDeletion(managerCheque, "_Temp");
		}

		// Save Reprint ManagerCheque Details
		if (managerCheque.getReprintManagerCheque() != null ) {
			ManagerCheque reprintManagerCheque = managerCheque.getReprintManagerCheque();
			reprintManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			List<AuditDetail> details = managerCheque.getAuditDetailMap().get("ReprintManagerChequeDetails");
			details = processingReprintManagerChequeDetailsList(details,  "_Temp", managerCheque.getChequeID(),managerCheque);
			auditHeader.setAuditDetails(details);
			//deleteReprintManagerCheque(managerCheque.getReprintManagerCheque(), "_Temp");
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getManagerChequeDAO().delete(managerCheque,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset ManagerCheque Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(managerCheque);

		logger.debug("Leaving");
		return auditHeader;
	}

	//Document Details List Maintenance
	public void listDocDeletion(ManagerCheque managerCheque, String tableType) {
		getDocumentDetailsDAO().deleteList(
				new ArrayList<DocumentDetails>(managerCheque.getDocumentDetailsList()), tableType);
	}

	//Reprint ManagerCheque Details 
	public void deleteReprintManagerCheque(ManagerCheque managerCheque, String tableType) {
		getManagerChequeDAO().delete(managerCheque, tableType);
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = getAuditDetails(auditHeader, method);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

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
	 * 5)	for any mismatch conditions Fetch the error details from getManagerChequeDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		ManagerCheque managerCheque= (ManagerCheque) auditDetail.getModelData();

		ManagerCheque tempManagerCheque= null;
		if (managerCheque.isWorkflow()){
			tempManagerCheque = getManagerChequeDAO().getManagerChequeById(managerCheque.getId(), "_Temp");
		}
		ManagerCheque befManagerCheque= getManagerChequeDAO().getManagerChequeById(managerCheque.getId(), "");

		ManagerCheque oldManagerCheque= managerCheque.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(managerCheque.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_ChequeID")+":"+valueParm[0];

		if (managerCheque.isNew()){ // for New record or new record into work flow

			if (!managerCheque.isWorkflow()){// With out Work flow only new records  
				if (befManagerCheque !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (managerCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befManagerCheque !=null || tempManagerCheque!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befManagerCheque ==null || tempManagerCheque!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!managerCheque.isWorkflow()){	// With out Work flow for update and delete

				if (befManagerCheque ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldManagerCheque!=null && !oldManagerCheque.getLastMntOn().equals(befManagerCheque.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempManagerCheque==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldManagerCheque!=null && !oldManagerCheque.getLastMntOn().equals(tempManagerCheque.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}
		if(!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(managerCheque.getRecordStatus()) && 
				!PennantConstants.RCD_STATUS_RESUBMITTED.contains(managerCheque.getRecordStatus()) &&
				!PennantConstants.RCD_STATUS_REJECTED.contains(managerCheque.getRecordStatus()) &&
				!managerCheque.isCancel()) {
			String chequeNo = "";
			long chequeID = 0;
			if (managerCheque.getReprintManagerCheque() == null) {
				chequeNo = managerCheque.getChequeNo();
				chequeID = managerCheque.getChequeID();
			}else{
				chequeNo = managerCheque.getReprintManagerCheque().getChequeNo();
				chequeID = managerCheque.getReprintManagerCheque().getChequeID();
			}
			int count = getManagerChequeDAO().getMgrChqCountByChqNoAndAccount(chequeID, chequeNo, managerCheque.getNostroAccount(), "_View");
			if (count > 0) {
				errParm[0] = PennantJavaUtil.getLabel("label_ChequeNo") + ":" + chequeNo;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", errParm,valueParm), usrLanguage));
			}else {
				//String msg = validateChqNumberInEquation(managerCheque.getNostroAccount(), chequeNo);//AHB
				String msg = "";
				if(StringUtils.isNotBlank(msg)){
					errParm[0] = msg;
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "30550", errParm,valueParm), usrLanguage));//AHB
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !managerCheque.isWorkflow()){
			auditDetail.setBefImage(befManagerCheque);	
		}

		return auditDetail;
	}


	@Override
	public FinanceMain getFinanceMainByFinReference(String finReference) {
		return getFinanceMainDAO().getFinanceMainForManagerCheque(finReference, "_AView");//TODO
	}

	@Override
	public BigDecimal getTotalChqAmtByFinReference(String finReference) {
		return getManagerChequeDAO().getTotalChqAmtByFinReference(finReference, "");
	}
	@Override
	public DocumentDetails getManagerChequeDocDetailByDocId(long docId) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, "");
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ManagerCheque managerCheque = (ManagerCheque) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (managerCheque.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		//ManagerCheque Document Details
		if (managerCheque.getDocumentDetailsList() != null  && managerCheque.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(managerCheque, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}


		//Reprint ManagerCheque  Details
		if (managerCheque.getReprintManagerCheque()!= null ) {
			auditDetailMap.put("ReprintManagerChequeDetails", setReprintManagerChequeDetailsAuditData(managerCheque, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ReprintManagerChequeDetails"));
		}

		managerCheque.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(managerCheque);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;

	}
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(ManagerCheque detail,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getDocumentDetailsList().size(); i++) {
			DocumentDetails documentDetails = detail.getDocumentDetailsList().get(i);
			documentDetails.setWorkflowId(detail.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			//####TODO####
			documentDetails.setRecordStatus(detail.getRecordStatus());
			documentDetails.setUserDetails(detail.getUserDetails());
			documentDetails.setLastMntOn(detail.getLastMntOn());
			documentDetails.setLastMntBy(detail.getLastMntBy());

			if (StringUtils.isNotEmpty(documentDetails.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 2, fields[0], fields[1],
						documentDetails.getBefImage(), documentDetails));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}


	public List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			String type, String chequeID, ManagerCheque managerCheque) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			if(!documentDetails.isDocIsCustDoc()){
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(type)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}

				documentDetails.setWorkflowId(0);

				if(documentDetails.isDocIsCustDoc()){
					approveRec = true;
				}

				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RCD_DEL)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RCD_UPD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetails.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = documentDetails.getRecordType();
					recordStatus = documentDetails.getRecordStatus();
					documentDetails.setRecordType("");
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					if (StringUtils.isBlank(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(chequeID);
					}
					getDocumentDetailsDAO().save(documentDetails, type);
				}

				if (updateRecord) {
					getDocumentDetailsDAO().update(documentDetails, type);
				}

				if (deleteRecord) {
					getDocumentDetailsDAO().delete(documentDetails, type);
				}

				if (approveRec) {
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			}
		}
		logger.debug("Leaving");
		return auditDetails;

	}
	public List<AuditDetail> processingReprintManagerChequeDetailsList(List<AuditDetail> auditDetails,
			String type, long chequeID, ManagerCheque managerCheque) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			ManagerCheque aManagerCheque = (ManagerCheque) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				aManagerCheque.setRoleCode("");
				aManagerCheque.setNextRoleCode("");
				aManagerCheque.setTaskId("");
				aManagerCheque.setNextTaskId("");
				aManagerCheque.setWorkflowId(0);
			}

			if (aManagerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (aManagerCheque.isNewRecord()) {
				saveRecord = true;
				if (aManagerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					aManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (aManagerCheque.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_DEL)) {
					aManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (aManagerCheque.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_UPD)) {
					aManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (aManagerCheque.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (aManagerCheque.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (aManagerCheque.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (aManagerCheque.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = aManagerCheque.getRecordType();
				recordStatus = aManagerCheque.getRecordStatus();
				aManagerCheque.setRecordType("");
				aManagerCheque.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getManagerChequeDAO().save(aManagerCheque, type);
			}

			if (updateRecord) {
				getManagerChequeDAO().update(aManagerCheque, type);
			}

			if (deleteRecord) {
				getManagerChequeDAO().delete(aManagerCheque, type);
			}

			if (approveRec) {
				aManagerCheque.setRecordType(rcdType);
				aManagerCheque.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(aManagerCheque);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setReprintManagerChequeDetailsAuditData(ManagerCheque detail,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		ManagerCheque object = new ManagerCheque();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		ManagerCheque managerCheque = detail.getReprintManagerCheque();
		managerCheque.setWorkflowId(detail.getWorkflowId());
		boolean isRcdType = false;

		if (managerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			managerCheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (managerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			managerCheque.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			isRcdType = true;
		} else if (managerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			managerCheque.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			isRcdType = true;
		}

		if ("saveOrUpdate".equals(method) && isRcdType ) {
			managerCheque.setNewRecord(true);
		}

		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (managerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (managerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| managerCheque.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}

		//####TODO####
		managerCheque.setRecordStatus(detail.getRecordStatus());
		managerCheque.setUserDetails(detail.getUserDetails());
		managerCheque.setLastMntOn(detail.getLastMntOn());
		managerCheque.setLastMntBy(detail.getLastMntBy());

		managerCheque.setTaskId(detail.getTaskId());
		managerCheque.setNextTaskId(detail.getNextTaskId());
		managerCheque.setRoleCode(detail.getRoleCode());
		managerCheque.setNextRoleCode(detail.getNextRoleCode());

		if (StringUtils.isNotEmpty(managerCheque.getRecordType())) {
			auditDetails.add(new AuditDetail(auditTranType, 2, fields[0], fields[1],
					managerCheque.getBefImage(), managerCheque));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Validating the Cheque Number in Equation
	 * 
	 * @param accountNum
	 * @param chequeNum
	 * @return ErrorMessage
	 */
	public String validateChqNumberInEquation(String accountNum, String chequeNum) {
		logger.debug("Entering");
		try {
			getManagerChequeInterfaceService().validateChequeNumber(accountNum, chequeNum);
		} catch (AccountNotFoundException e) {
			logger.error("Exception: ", e);
			//return e.getErrorMsg();AIB
			return e.getMessage();
		}
		logger.debug("Leaving");
		return "";
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	/**
	 * Method for get the Next Sequence Id
	 * In Notes table ChequeID is stored in reference field.
	 */
	public long getNextId() {
		return getManagerChequeDAO().getNextId();
	}
}