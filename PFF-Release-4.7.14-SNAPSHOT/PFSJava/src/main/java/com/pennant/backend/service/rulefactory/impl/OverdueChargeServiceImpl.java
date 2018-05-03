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
 * FileName    		:  OverdueChargeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.rulefactory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.OverdueChargeDAO;
import com.pennant.backend.dao.rulefactory.OverdueChargeDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.model.rulefactory.OverdueChargeDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rulefactory.OverdueChargeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>OverdueCharge</b>.<br>
 * 
 */
public class OverdueChargeServiceImpl extends GenericService<OverdueCharge> implements OverdueChargeService {
	private static final Logger logger = Logger.getLogger(OverdueChargeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private OverdueChargeDAO overdueChargeDAO;
	private OverdueChargeDetailDAO overdueChargeDetailDAO;
	private OverdueChargeDetailValidation overdueChargeDetailValidation;
	private TransactionEntryDAO transactionEntryDAO;

	public OverdueChargeServiceImpl() {
		super();
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public OverdueChargeDAO getOverdueChargeDAO() {
		return overdueChargeDAO;
	}
	public void setOverdueChargeDAO(OverdueChargeDAO overdueChargeDAO) {
		this.overdueChargeDAO = overdueChargeDAO;
	}

	@Override
	public OverdueCharge getOverdueCharge() {
		return getOverdueChargeDAO().getOverdueCharge();
	}
	
	@Override
	public OverdueCharge getNewOverdueCharge() {
		return getOverdueChargeDAO().getNewOverdueCharge();
	}

	@Override
	public OverdueChargeDetail getNewOverdueChargeDetail() {
		return getOverdueChargeDetailDAO().getNewOverdueChargeDetail();
	}

	public void setOverdueChargeDetailDAO(OverdueChargeDetailDAO overdueChargeDetailDAO) {
		this.overdueChargeDetailDAO = overdueChargeDetailDAO;
	}
	public OverdueChargeDetailDAO getOverdueChargeDetailDAO() {
		return overdueChargeDetailDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinODCHeader/FinODCHeader_Temp 
	 * 			by using OverdueChargeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using OverdueChargeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinODCHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		OverdueCharge overdueCharge = (OverdueCharge) auditHeader.getAuditDetail().getModelData();

		if (overdueCharge.isWorkflow()) {
			tableType="_Temp";
		}

		if (overdueCharge.isNew()) {
			overdueCharge.setId(getOverdueChargeDAO().save(overdueCharge,tableType));
			auditHeader.getAuditDetail().setModelData(overdueCharge);
			auditHeader.setAuditReference(overdueCharge.getODCRuleCode());
		}else{
			getOverdueChargeDAO().update(overdueCharge,tableType);
		}

		if (overdueCharge.getChargeDetailEntries() != null && overdueCharge.getChargeDetailEntries().size() > 0) {
			List<AuditDetail> details = overdueCharge.getAuditDetailMap().get("OverdueChargeDetail");
			details = processTransactionEntry(details, overdueCharge.getODCRuleCode(), tableType);
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
	 * 2)	delete Record for the DB table FinODCHeader by using OverdueChargeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinODCHeader by using auditHeaderDAO.addAudit(auditHeader)    
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

		OverdueCharge overdueCharge = (OverdueCharge) auditHeader.getAuditDetail().getModelData();
		getOverdueChargeDAO().delete(overdueCharge,"");

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(overdueCharge, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getOverdueChargeById fetch the details by using OverdueChargeDAO's getOverdueChargeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OverdueCharge
	 */

	@Override
	public OverdueCharge getOverdueChargeById(String id) {
		OverdueCharge overdueCharge = getOverdueChargeDAO().getOverdueChargeById(id, "_View");
		overdueCharge.setChargeDetailEntries(getOverdueChargeDetailDAO().getListOverdueChargeDetailById(id, "_View"));
		return overdueCharge;
	}
	/**
	 * getApprovedOverdueChargeById fetch the details by using OverdueChargeDAO's getOverdueChargeById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinODCHeader.
	 * @param id (String)
	 * @return OverdueCharge
	 */

	public OverdueCharge getApprovedOverdueChargeById(String id) {
		OverdueCharge overdueCharge = getOverdueChargeDAO().getOverdueChargeById(id, "_AView");
		overdueCharge.setChargeDetailEntries(getOverdueChargeDetailDAO().getListOverdueChargeDetailById(id, "_AView"));
		return overdueCharge;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getOverdueChargeDAO().delete with parameters overdueCharge,""
	 * 		b)  NEW		Add new record in to main table by using getOverdueChargeDAO().save with parameters overdueCharge,""
	 * 		c)  EDIT	Update record in the main table by using getOverdueChargeDAO().update with parameters overdueCharge,""
	 * 3)	Delete the record from the workFlow table by using getOverdueChargeDAO().delete with parameters overdueCharge,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinODCHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinODCHeader by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		OverdueCharge overdueCharge = new OverdueCharge();
		BeanUtils.copyProperties((OverdueCharge) auditHeader.getAuditDetail().getModelData(), overdueCharge);

		if (overdueCharge.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getOverdueChargeDAO().delete(overdueCharge,"");
		} else {
			overdueCharge.setRoleCode("");
			overdueCharge.setNextRoleCode("");
			overdueCharge.setTaskId("");
			overdueCharge.setNextTaskId("");
			overdueCharge.setWorkflowId(0);

			if (overdueCharge.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				overdueCharge.setRecordType("");
				getOverdueChargeDAO().save(overdueCharge,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				overdueCharge.setRecordType("");
				getOverdueChargeDAO().update(overdueCharge,"");
				
				//Transaction Entry updation if Exist
				List<TransactionEntry> entries = getTransactionEntryDAO().getTransactionEntryList(overdueCharge.getODCRuleCode());
				if(entries != null && entries.size() > 0){
					for (int i = 0; i < entries.size(); i++) {
						if("PLA".equals(entries.get(i).getAccountType())){
							entries.get(i).setAccountType(overdueCharge.getODCPLAccount());
							entries.get(i).setAccountSubHeadRule(overdueCharge.getoDCPLSubHead());
						}else if("CA".equals(entries.get(i).getAccountType())){
							entries.get(i).setAccountType(overdueCharge.getODCCharityAccount());
							entries.get(i).setAccountSubHeadRule(overdueCharge.getoDCCharitySubHead());
						}
						AccountingConfigCache.clearTransactionEntryCache(entries.get(i).getAccountSetid());
					}
					getTransactionEntryDAO().updateTransactionEntryList(entries);
				}
			}

			if (overdueCharge.getChargeDetailEntries() != null && overdueCharge.getChargeDetailEntries().size() > 0) {
				List<AuditDetail> details = overdueCharge.getAuditDetailMap().get("OverdueChargeDetail");
				details = processTransactionEntry(details, overdueCharge.getODCRuleCode(), "");
				auditDetails.addAll(details);
			}
			auditHeader.setAuditDetails(auditDetails);
		}

		getOverdueChargeDAO().delete(overdueCharge,"_Temp");
		auditHeader.setAuditDetails(listDeletion(overdueCharge, "_Temp", auditHeader.getAuditTranType()));
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(overdueCharge);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getOverdueChargeDAO().delete with parameters overdueCharge,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinODCHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		OverdueCharge overdueCharge = (OverdueCharge) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getOverdueChargeDAO().delete(overdueCharge,"_Temp");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, overdueCharge.getBefImage(), overdueCharge));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(overdueCharge, "_Temp", auditHeader.getAuditTranType())));

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
	 * 5)	for any mismatch conditions Fetch the error details from getOverdueChargeDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		OverdueCharge overdueCharge = (OverdueCharge) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = overdueCharge.getUserDetails().getLanguage();

		// FeeTier Validation
		if (overdueCharge.getChargeDetailEntries() != null && overdueCharge.getChargeDetailEntries() .size() > 0) {
			List<AuditDetail> details = overdueCharge.getAuditDetailMap().get("OverdueChargeDetail");
			details = getOverdueChargeDetailValidation().overDueChargeDetailsListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}


		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		OverdueCharge overdueCharge= (OverdueCharge) auditDetail.getModelData();

		OverdueCharge tempOverdueCharge= null;
		if (overdueCharge.isWorkflow()){
			tempOverdueCharge = getOverdueChargeDAO().getOverdueChargeById(overdueCharge.getId(), "_Temp");
		}
		OverdueCharge befOverdueCharge= getOverdueChargeDAO().getOverdueChargeById(overdueCharge.getId(), "");

		OverdueCharge oldOverdueCharge= overdueCharge.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=overdueCharge.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_ODCRuleCode")+":"+valueParm[0];

		if (overdueCharge.isNew()){ // for New record or new record into work flow

			if (!overdueCharge.isWorkflow()){// With out Work flow only new records  
				if (befOverdueCharge !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (overdueCharge.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befOverdueCharge !=null || tempOverdueCharge!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befOverdueCharge ==null || tempOverdueCharge!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!overdueCharge.isWorkflow()){	// With out Work flow for update and delete

				if (befOverdueCharge ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldOverdueCharge!=null && !oldOverdueCharge.getLastMntOn().equals(befOverdueCharge.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempOverdueCharge==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempOverdueCharge!=null && oldOverdueCharge!=null && !oldOverdueCharge.getLastMntOn().equals(tempOverdueCharge.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !overdueCharge.isWorkflow()){
			overdueCharge.setBefImage(befOverdueCharge);	
		}

		return auditDetail;
	}

	private List<AuditDetail> processTransactionEntry(List<AuditDetail> auditDetails, String oDCRuleCode, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			OverdueChargeDetail overdueChargeDetail = (OverdueChargeDetail) auditDetails.get(i).getModelData();
			overdueChargeDetail.setoDCRuleCode(oDCRuleCode);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				overdueChargeDetail.setRoleCode("");
				overdueChargeDetail.setNextRoleCode("");
				overdueChargeDetail.setTaskId("");
				overdueChargeDetail.setNextTaskId("");
			}


			overdueChargeDetail.setWorkflowId(0);

			if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (overdueChargeDetail.isNewRecord()) {
				saveRecord = true;
				if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					overdueChargeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					overdueChargeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					overdueChargeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (overdueChargeDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = overdueChargeDetail.getRecordType();
				recordStatus = overdueChargeDetail.getRecordStatus();
				overdueChargeDetail.setRecordType("");
				overdueChargeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				overdueChargeDetailDAO.save(overdueChargeDetail, type);
			}

			if (updateRecord) {
				overdueChargeDetailDAO.update(overdueChargeDetail, type);
			}

			if (deleteRecord) {
				overdueChargeDetailDAO.delete(overdueChargeDetail, type);
			}

			if (approveRec) {
				overdueChargeDetail.setRecordType(rcdType);
				overdueChargeDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(overdueChargeDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	/**
	 * Method deletion of feeTier list with existing fee type
	 * 
	 * @param accountingSet
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(OverdueCharge overdueCharge, String tableType, String auditTranType) {
		OverdueChargeDetail chargeDetails = null;
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (overdueCharge.getChargeDetailEntries() != null && overdueCharge.getChargeDetailEntries().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new OverdueChargeDetail());
			for (int i = 0; i < overdueCharge.getChargeDetailEntries().size(); i++) {
				chargeDetails = overdueCharge.getChargeDetailEntries().get(i);
				if (StringUtils.isNotEmpty(chargeDetails.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], chargeDetails.getBefImage(), chargeDetails));
				}
			}
			getOverdueChargeDetailDAO().delete(chargeDetails, tableType);
		}
		return auditList;
	}

	/**
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				OverdueChargeDetail overdueChargeDetail = (OverdueChargeDetail) ((AuditDetail) list.get(i)).getModelData();

				rcdType = overdueChargeDetail.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), overdueChargeDetail.getBefImage(), overdueChargeDetail));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}
	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		OverdueCharge overdueCharge = (OverdueCharge) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (overdueCharge.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (overdueCharge.getChargeDetailEntries() != null && overdueCharge.getChargeDetailEntries().size() > 0) {
			auditDetailMap.put("OverdueChargeDetail", setChargeDetailAuditData(overdueCharge, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("OverdueChargeDetail"));
		}

		overdueCharge.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(overdueCharge);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setChargeDetailAuditData(OverdueCharge overdueCharge, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new OverdueChargeDetail());

		for (int i = 0; i < overdueCharge.getChargeDetailEntries().size(); i++) {

			OverdueChargeDetail overdueChargeDetail = overdueCharge.getChargeDetailEntries().get(i);
			overdueChargeDetail.setWorkflowId(overdueCharge.getWorkflowId());
			overdueChargeDetail.setoDCRuleCode(overdueCharge.getODCRuleCode());

			boolean isRcdType = false;

			if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				overdueChargeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				overdueChargeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				overdueChargeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && isRcdType ) {
				overdueChargeDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| overdueChargeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			overdueChargeDetail.setRecordStatus(overdueCharge.getRecordStatus());
			overdueChargeDetail.setUserDetails(overdueCharge.getUserDetails());
			overdueChargeDetail.setLastMntOn(overdueCharge.getLastMntOn());

			if (StringUtils.isNotBlank(overdueChargeDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], overdueChargeDetail.getBefImage(), overdueChargeDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	public OverdueChargeDetailValidation getOverdueChargeDetailValidation() {
		if (overdueChargeDetailValidation == null) {
			this.overdueChargeDetailValidation = new OverdueChargeDetailValidation(overdueChargeDetailDAO);
		}
		return this.overdueChargeDetailValidation;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
	    this.transactionEntryDAO = transactionEntryDAO;
    }

	public TransactionEntryDAO getTransactionEntryDAO() {
	    return transactionEntryDAO;
    }

}