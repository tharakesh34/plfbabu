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
 * FileName    		:  AccountingSetServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>AccountingSet</b>.<br>
 * 
 */
public class AccountingSetServiceImpl extends GenericService<AccountingSet> implements AccountingSetService {
	private final static Logger logger = Logger.getLogger(AccountingSetServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AccountingSetDAO accountingSetDAO;
	private TransactionEntryDAO transactionEntryDAO;
	private TransactionEntryValidation transactionEntryValidation;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}
	
	@Override
	public TransactionEntry getNewTransactionEntry() {
		return getTransactionEntryDAO().getNewTransactionEntry();
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public TransactionEntryValidation getTransactionEntryValidation() {
		if (transactionEntryValidation == null) {
			this.transactionEntryValidation = new TransactionEntryValidation(transactionEntryDAO);
		}
		return this.transactionEntryValidation;
	}

	@Override
	public AccountingSet getAccountingSet() {
		return getAccountingSetDAO().getAccountingSet();
	}

	@Override
	public AccountingSet getNewAccountingSet() {
		return getAccountingSetDAO().getNewAccountingSet();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTAccountingSet/RMTAccountingSet_Temp by using AccountingSetDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using AccountingSetDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTAccountingSet by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		AccountingSet accountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();

		if (accountingSet.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (accountingSet.isNew()) {
			accountingSet.setId(getAccountingSetDAO().save(accountingSet, tableType));
			auditHeader.getAuditDetail().setModelData(accountingSet);
			auditHeader.setAuditReference(String.valueOf(accountingSet.getAccountSetid()));
		} else {
			getAccountingSetDAO().update(accountingSet, tableType);
		}

		if (accountingSet.getTransactionEntries() != null && accountingSet.getTransactionEntries().size() > 0) {
			List<AuditDetail> details = accountingSet.getAuditDetailMap().get("TransactionEntry");
			details = processTransactionEntry(details, accountingSet.getAccountSetid(), tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTAccountingSet by using AccountingSetDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTAccountingSet by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountingSet accountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();
		getAccountingSetDAO().delete(accountingSet, "");

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(accountingSet, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAccountingSetById fetch the details by using AccountingSetDAO's
	 * getAccountingSetById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountingSet
	 */

	@Override
	public AccountingSet getAccountingSetById(long id) {

		AccountingSet accountingSet = getAccountingSetDAO().getAccountingSetById(id, "_View");
		accountingSet.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(id,"_View",false));
		return accountingSet;

	}

	/**
	 * getApprovedAccountingSetById fetch the details by using
	 * AccountingSetDAO's getAccountingSetById method . with parameter id and
	 * type as blank. it fetches the approved records from the RMTAccountingSet.
	 * 
	 * @param id
	 *            (int)
	 * @return AccountingSet
	 */

	public AccountingSet getApprovedAccountingSetById(long id) {
		AccountingSet accountingSet = getAccountingSetDAO().getAccountingSetById(id, "_AView");
		accountingSet.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(id,"_AView",false));
		return accountingSet;
	}
	
	public AccountingSet getAccSetSysDflByEvent(String event,String type){
		return  getAccountingSetDAO().getAccSetSysDflByEvent(event,type);
	}
	/**
	 * This method refresh the Record.
	 * 
	 * @param AccountingSet
	 *            (accountingSet)
	 * @return accountingSet
	 */
	@Override
	public AccountingSet refresh(AccountingSet accountingSet) {
		logger.debug("Entering");
		getAccountingSetDAO().refresh(accountingSet);
		getAccountingSetDAO().initialize(accountingSet);
		logger.debug("Leaving");
		return accountingSet;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAccountingSetDAO().delete with parameters accountingSet,"" b)
	 * NEW Add new record in to main table by using getAccountingSetDAO().save
	 * with parameters accountingSet,"" c) EDIT Update record in the main table
	 * by using getAccountingSetDAO().update with parameters accountingSet,"" 3)
	 * Delete the record from the workFlow table by using
	 * getAccountingSetDAO().delete with parameters accountingSet,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTAccountingSet by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTAccountingSet by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountingSet accountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();

		if (accountingSet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(accountingSet, "", auditHeader.getAuditTranType()));
			getAccountingSetDAO().delete(accountingSet, "");
		} else {
			accountingSet.setRoleCode("");
			accountingSet.setNextRoleCode("");
			accountingSet.setTaskId("");
			accountingSet.setNextTaskId("");
			accountingSet.setWorkflowId(0);

			if (accountingSet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountingSet.setRecordType("");
				getAccountingSetDAO().save(accountingSet, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountingSet.setRecordType("");
				getAccountingSetDAO().update(accountingSet, "");
			}

			if (accountingSet.getTransactionEntries() != null && accountingSet.getTransactionEntries().size() > 0) {
				List<AuditDetail> details = accountingSet.getAuditDetailMap().get("TransactionEntry");
				details = processTransactionEntry(details, accountingSet.getAccountSetid(), "");
				auditDetails.addAll(details);
			}
		}

		getAccountingSetDAO().delete(accountingSet, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(accountingSet, "_TEMP", auditHeader.getAuditTranType())));
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, accountingSet.getBefImage(), accountingSet));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountingSet);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, accountingSet.getBefImage(), accountingSet));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAccountingSetDAO().delete with parameters
	 * accountingSet,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTAccountingSet by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountingSet accountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountingSetDAO().delete(accountingSet, "_TEMP");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, accountingSet.getBefImage(), accountingSet));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(accountingSet, "_TEMP", auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getAccountingSetDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		AccountingSet accountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = accountingSet.getUserDetails().getUsrLanguage();

		// FeeTier Validation
		if (accountingSet.getTransactionEntries() != null && accountingSet.getTransactionEntries().size() > 0) {
			List<AuditDetail> details = accountingSet.getAuditDetailMap().get("TransactionEntry");
			details = getTransactionEntryValidation().transactionEntryListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		AccountingSet accountingSet = (AccountingSet) auditDetail.getModelData();

		AccountingSet tempAccountingSet = null;
		if (accountingSet.isWorkflow()) {
			tempAccountingSet = getAccountingSetDAO().getAccountingSetById(accountingSet.getId(), "_Temp");
		}
		AccountingSet befAccountingSet = getAccountingSetDAO().getAccountingSetById(accountingSet.getId(), "");

		AccountingSet old_AccountingSet = accountingSet.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(accountingSet.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_AccountSetid") + ":" + valueParm[0];

		if (accountingSet.isNew()) {
			// for New record or new record into work flow
			if (!accountingSet.isWorkflow()) {
				// With out Work flow only new records
				if (befAccountingSet != null) { 
					// Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (accountingSet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { 
					// if records type is new
					if (befAccountingSet != null || tempAccountingSet != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befAccountingSet == null || tempAccountingSet != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!accountingSet.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befAccountingSet == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (old_AccountingSet != null && !old_AccountingSet.getLastMntOn().equals(befAccountingSet.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempAccountingSet == null) { // if records not exists in the
													// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (old_AccountingSet != null && !old_AccountingSet.getLastMntOn().equals(tempAccountingSet.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !accountingSet.isWorkflow()) {
			accountingSet.setBefImage(befAccountingSet);
		}

		return auditDetail;
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

		AccountingSet accountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (accountingSet.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (accountingSet.getTransactionEntries() != null && accountingSet.getTransactionEntries().size() > 0) {
			auditDetailMap.put("TransactionEntry", setTransactionEntryAuditData(accountingSet, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("TransactionEntry"));
		}

		accountingSet.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(accountingSet);
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
	private List<AuditDetail> setTransactionEntryAuditData(AccountingSet accountingSet, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new TransactionEntry());

		for (int i = 0; i < accountingSet.getTransactionEntries().size(); i++) {

			TransactionEntry transactionEntry = accountingSet.getTransactionEntries().get(i);
			transactionEntry.setWorkflowId(accountingSet.getWorkflowId());
			transactionEntry.setAccountSetid(accountingSet.getAccountSetid());

			boolean isRcdType = false;

			if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				transactionEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				transactionEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				transactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				transactionEntry.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			transactionEntry.setRecordStatus(accountingSet.getRecordStatus());
			transactionEntry.setUserDetails(accountingSet.getUserDetails());
			transactionEntry.setLastMntOn(accountingSet.getLastMntOn());

			if (!StringUtils.trimToEmpty(transactionEntry.getRecordType()).equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], transactionEntry.getBefImage(), transactionEntry));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processTransactionEntry(List<AuditDetail> auditDetails, long accountSetid, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			TransactionEntry transactionEntry = (TransactionEntry) auditDetails.get(i).getModelData();
			transactionEntry.setAccountSetid(accountSetid);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				transactionEntry.setRoleCode("");
				transactionEntry.setNextRoleCode("");
				transactionEntry.setTaskId("");
				transactionEntry.setNextTaskId("");
			}
			
			//Fee Rules Verification for existence Entry usage
			String[] feeCodeList = transactionEntry.getAmountRule().split("[^a-zA-Z0-9_]+");
			String feeCode = "";
			for (int k = 0; k < feeCodeList.length; k++) {
				if(!(StringUtils.trimToEmpty(feeCodeList[k]).equals("") || feeCodeList[k].equalsIgnoreCase("Result"))
						&& (feeCodeList[k].trim().endsWith("_W") ||  feeCodeList[k].trim().endsWith("_C") ||
								 feeCodeList[k].trim().endsWith("_P"))){
					if(!feeCode.contains(feeCodeList[k].trim().substring(0, feeCodeList[k].trim().indexOf('_'))+",")){
						feeCode = feeCode+feeCodeList[k].trim().substring(0, feeCodeList[k].trim().indexOf('_'))+",";
					}
				}
			}
			
			if(feeCode.endsWith(",")){
				transactionEntry.setFeeCode(feeCode.substring(0, feeCode.length()-1));
			}else{
				transactionEntry.setFeeCode(feeCode);
			}
			
			transactionEntry.setWorkflowId(0);

			if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (transactionEntry.isNewRecord()) {
				saveRecord = true;
				if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					transactionEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					transactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					transactionEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (transactionEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (transactionEntry.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = transactionEntry.getRecordType();
				recordStatus = transactionEntry.getRecordStatus();
				transactionEntry.setRecordType("");
				transactionEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				transactionEntryDAO.save(transactionEntry, type);
			}

			if (updateRecord) {
				transactionEntryDAO.update(transactionEntry, type);
			}

			if (deleteRecord) {
				transactionEntryDAO.delete(transactionEntry, type);
			}

			if (approveRec) {
				transactionEntry.setRecordType(rcdType);
				transactionEntry.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(transactionEntry);
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
	public List<AuditDetail> listDeletion(AccountingSet accountingSet, String tableType, String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (accountingSet.getTransactionEntries() != null && accountingSet.getTransactionEntries().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new TransactionEntry());
			for (int i = 0; i < accountingSet.getTransactionEntries().size(); i++) {
				TransactionEntry feeTier = accountingSet.getTransactionEntries().get(i);
				if (!feeTier.getRecordType().equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeTier.getBefImage(), feeTier));
				}
			}
			getTransactionEntryDAO().deleteByAccountingSetId(accountingSet.getAccountSetid(), tableType);
		}
		return auditList;
	}

	/**
	 * Common Method for Customers list validation
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

		if (list != null & list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				TransactionEntry transactionEntry = (TransactionEntry) ((AuditDetail) list.get(i)).getModelData();

				rcdType = transactionEntry.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (!(transType.equals(""))) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), transactionEntry.getBefImage(), transactionEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}
	
	@Override
    public List<TransactionEntry> getODTransactionEntries() {
	    return getTransactionEntryDAO().getODTransactionEntries();
    }

}