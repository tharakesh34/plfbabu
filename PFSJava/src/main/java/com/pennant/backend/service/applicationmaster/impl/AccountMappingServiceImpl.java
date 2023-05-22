/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : AccountMappingServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AccountMapping</b>.<br>
 */
public class AccountMappingServiceImpl extends GenericService<AccountMapping> implements AccountMappingService {
	private static final Logger logger = LogManager.getLogger(AccountMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AccountMappingDAO accountMappingDAO;
	private TransactionEntryDAO transactionEntryDAO;

	public AccountMappingServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setAccountMappingDAO(AccountMappingDAO accountMappingDAO) {
		this.accountMappingDAO = accountMappingDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * AccountMapping/AccountMapping_Temp by using AccountMappingDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using AccountMappingDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtAccountMapping by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (accountMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (accountMapping.isNewRecord()) {
			accountMapping.setCreatedBy(accountMapping.getUserDetails().getUserId());
			accountMapping.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			accountMapping.setAccount(accountMappingDAO.save(accountMapping, tableType));
			auditHeader.getAuditDetail().setModelData(accountMapping);
			auditHeader.setAuditReference(accountMapping.getAccount());
		} else {
			accountMappingDAO.update(accountMapping, tableType);
		}

		if (accountMapping.getAccountMappingList() != null && accountMapping.getAccountMappingList().size() > 0) {
			List<AuditDetail> accountMappingDetails = accountMapping.getAuditDetailMap().get("AccountMapping");
			accountMappingDetails = processAccountMappingDetails(accountMappingDetails, tableType);
			auditDetailsList.addAll(accountMappingDetails);
		}

		auditHeader.setAuditDetails(auditDetailsList);
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	public List<AuditDetail> processAccountMappingDetails(List<AuditDetail> auditDetails, TableType tableType) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			AccountMapping accountMapping = (AccountMapping) auditDetails.get(i).getModelData();

			if (accountMapping.isWorkflow()) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (TableType.MAIN_TAB == tableType) {
					approveRec = true;
					accountMapping.setRoleCode("");
					accountMapping.setNextRoleCode("");
					accountMapping.setTaskId("");
					accountMapping.setNextTaskId("");
					accountMapping.setWorkflowId(0);
				}
				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(accountMapping.getRecordType())) {
					deleteRecord = true;
				} else if (accountMapping.isNewRecord()) {
					saveRecord = true;
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(accountMapping.getRecordType())) {
						accountMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(accountMapping.getRecordType())) {
						accountMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(accountMapping.getRecordType())) {
						accountMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(accountMapping.getRecordType())) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(accountMapping.getRecordType())) {
					updateRecord = true;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(accountMapping.getRecordType())) {
					if (approveRec) {
						deleteRecord = true;
					} else if (accountMapping.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = accountMapping.getRecordType();
					recordStatus = accountMapping.getRecordStatus();
					accountMapping.setRecordType("");
					accountMapping.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					accountMappingDAO.save(accountMapping, tableType);
				}
				if (updateRecord) {
					accountMappingDAO.update(accountMapping, tableType);
				}
				if (deleteRecord) {
					accountMappingDAO.delete(accountMapping, tableType);
				}
				if (approveRec) {
					accountMapping.setRecordType(rcdType);
					accountMapping.setRecordStatus(recordStatus);
				}
			} else {
				if (accountMapping.isNewRecord()) {
					accountMappingDAO.save(accountMapping, tableType);
				} else {
					accountMappingDAO.update(accountMapping, tableType);
				}
			}
			auditDetails.get(i).setModelData(accountMapping);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * AccountMapping by using AccountMappingDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtAccountMapping by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		accountMappingDAO.delete(accountMapping, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getAccountMapping fetch the details by using AccountMappingDAO's getAccountMappingById method.
	 * 
	 * @param account account of the AccountMapping.
	 * @return AccountMapping
	 */
	@Override
	public AccountMapping getAccountMapping(String account) {
		return accountMappingDAO.getAccountMapping(account, "_View");
	}

	/**
	 * getApprovedAccountMappingById fetch the details by using AccountMappingDAO's getAccountMappingById method . with
	 * parameter id and type as blank. it fetches the approved records from the AccountMapping.
	 * 
	 * @param account account of the AccountMapping. (String)
	 * @return AccountMapping
	 */
	public AccountMapping getApprovedAccountMapping(String account) {
		return accountMappingDAO.getAccountMapping(account, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAccountMappingDAO().delete with
	 * parameters accountMapping,"" b) NEW Add new record in to main table by using getAccountMappingDAO().save with
	 * parameters accountMapping,"" c) EDIT Update record in the main table by using getAccountMappingDAO().update with
	 * parameters accountMapping,"" 3) Delete the record from the workFlow table by using getAccountMappingDAO().delete
	 * with parameters accountMapping,"_Temp" 4) Audit the record in to AuditHeader and AdtAccountMapping by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtAccountMapping by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AccountMapping accountMapping = new AccountMapping();
		BeanUtils.copyProperties((AccountMapping) auditHeader.getAuditDetail().getModelData(), accountMapping);

		if (!RequestSource.UPLOAD.equals(accountMapping.getRequestSource())) {
			accountMappingDAO.delete(accountMapping, TableType.TEMP_TAB);
		}
		if (!PennantConstants.RECORD_TYPE_NEW.equals(accountMapping.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(accountMappingDAO.getAccountMapping(accountMapping.getAccount(), ""));
		}

		if (accountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			accountMappingDAO.delete(accountMapping, TableType.MAIN_TAB);
		} else {
			accountMapping.setRoleCode("");
			accountMapping.setNextRoleCode("");
			accountMapping.setTaskId("");
			accountMapping.setNextTaskId("");
			accountMapping.setWorkflowId(0);

			if (accountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountMapping.setRecordType("");
				accountMapping.setApprovedBy(accountMapping.getUserDetails().getUserId());
				accountMapping.setApprovedOn(new Timestamp(System.currentTimeMillis()));
				accountMappingDAO.save(accountMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountMapping.setRecordType("");
				accountMappingDAO.update(accountMapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountMapping);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAccountMappingDAO().delete with parameters accountMapping,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtAccountMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		accountMappingDAO.delete(accountMapping, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		// List
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));
		auditHeader = prepareChildsAudit(auditHeader, method);

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;
		List<ErrorDetail> details = null;

		if (accountMapping.getAuditDetailMap().get("AccountMapping") != null) {
			auditDetails = accountMapping.getAuditDetailMap().get("AccountMapping");

			for (AuditDetail auditDetail : auditDetails) {
				details = validation(auditDetail, usrLanguage, method).getErrorDetails();
			}
			if (details != null) {
				errorDetails.addAll(details);
			}
		}

		logger.debug("Leaving");

		return errorDetails;
	}

	// =================================== List maintain
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (accountMapping.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// accountMapping
		if (accountMapping.getAccountMappingList() != null && accountMapping.getAccountMappingList().size() > 0) {
			for (AccountMapping accMapping : accountMapping.getAccountMappingList()) {
				accMapping.setWorkflowId(accountMapping.getWorkflowId());
				accMapping.setRecordStatus(accountMapping.getRecordStatus());
				accMapping.setUserDetails(accountMapping.getUserDetails());
				accMapping.setLastMntOn(accountMapping.getLastMntOn());
				accMapping.setRoleCode(accountMapping.getRoleCode());
				accMapping.setNextRoleCode(accountMapping.getNextRoleCode());
				accMapping.setTaskId(accountMapping.getTaskId());
				accMapping.setNextTaskId(accountMapping.getNextTaskId());
			}

			auditDetailMap.put("AccountMapping",
					setAccountMappingAuditData(accountMapping.getAccountMappingList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("AccountMapping"));
		}

		accountMapping.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(accountMapping);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	public List<AuditDetail> setAccountMappingAuditData(List<AccountMapping> accountMappingList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new AccountMapping(),
				new AccountMapping().getExcludeFields());
		for (int i = 0; i < accountMappingList.size(); i++) {
			AccountMapping accountMapping = accountMappingList.get(i);

			if (accountMapping.isWorkflow()) {
				if (StringUtils.isEmpty(accountMapping.getRecordType())) {
					continue;
				}

				boolean isRcdType = false;
				if (accountMapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					accountMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (accountMapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					accountMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (accountMapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					accountMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
				if ("saveOrUpdate".equals(method) && isRcdType) {
					accountMapping.setNewRecord(true);
				}
				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (accountMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (accountMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| accountMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
			} else {
				if (accountMapping.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 2, fields[0], fields[1], accountMapping.getBefImage(),
					accountMapping));
		}

		logger.debug("Leaving");

		return auditDetails;
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
		// Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		// String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (accountMapping.isWorkflow()) {
				// auditTranType = PennantConstants.TRAN_WF;
			}
		}

		auditHeader.getAuditDetail().setModelData(accountMapping);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAccountMappingDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		AccountMapping accountMapping = (AccountMapping) auditDetail.getModelData();

		AccountMapping tempAccountMapping = null;
		if (accountMapping.isWorkflow()) {
			tempAccountMapping = accountMappingDAO.getAccountMapping(accountMapping.getId(), "_Temp");
		}
		AccountMapping befAccountMapping = accountMappingDAO.getAccountMapping(accountMapping.getId(), "");

		AccountMapping oldAccountMapping = accountMapping.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = accountMapping.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_Account") + ":" + valueParm[0];
		if (!RequestSource.UPLOAD.equals(accountMapping.getRequestSource())) {
			if (accountMapping.isNewRecord()) { // for New record or new record into workFlow

				if (!accountMapping.isWorkflow()) {// With out Work flow only new records
					if (befAccountMapping != null) { // Record Already Exists in the table then error
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // with work flow
					if (accountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						if (befAccountMapping != null || tempAccountMapping != null) { // if records already exists in
																						// the
																						// main
																						// table
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
						}
					} else { // if records not exists in the Main flow table
						if (befAccountMapping == null || tempAccountMapping != null) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
						}
					}
				}

			} else {
				// for work flow process records or (Record to update or Delete with
				// out work flow)
				if (!accountMapping.isWorkflow()) { // With out Work flow for update and delete

					if (befAccountMapping == null) { // if records not exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
					} else {
						if (oldAccountMapping != null
								&& !oldAccountMapping.getLastMntOn().equals(befAccountMapping.getLastMntOn())) {
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
									.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
								auditDetail.setErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
							} else {
								auditDetail.setErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
							}
						}
					}
				} else {
					if (tempAccountMapping == null) { // if records not exists in the WorkFlow table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
					if (tempAccountMapping != null && oldAccountMapping != null
							&& !oldAccountMapping.getLastMntOn().equals(tempAccountMapping.getLastMntOn())) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !accountMapping.isWorkflow()) {
			auditDetail.setBefImage(befAccountMapping);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public boolean isExistingHostAccount(String hostAccount) {
		return accountMappingDAO.isExistingHostAccount(hostAccount, "_View");
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	@Override
	public List<TransactionEntry> getTransactionEntriesByFintype(String finType) {
		return transactionEntryDAO.getTransactionEntriesbyFinType(finType, "");
	}

	@Override
	public Map<String, Rule> getSubheadRules(List<String> subHeadRules) {
		List<Rule> rulesList = this.transactionEntryDAO.getSubheadRules(subHeadRules, "_AView");
		Map<String, Rule> rulesMap = new HashMap<String, Rule>();
		if (rulesList != null) {
			for (Rule rule : rulesList) {
				rulesMap.put(rule.getRuleCode(), rule);
			}
		}
		return rulesMap;
	}

	@Override
	public void save(List<AccountMapping> accountMappingList, String finType) {
		List<AccountMapping> accMapList = this.accountMappingDAO.getAccountMappingFinType(finType, "");
		if (accMapList != null && !accMapList.isEmpty()) {
			this.accountMappingDAO.delete(finType, TableType.MAIN_TAB);
		}

		for (AccountMapping accountMapping : accountMappingList) {
			this.accountMappingDAO.save(accountMapping, TableType.MAIN_TAB);
		}
	}
}