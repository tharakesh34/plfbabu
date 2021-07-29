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
 * * FileName : JVPostingServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified
 * Date : 21-06-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.service.others.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.expenses.LegalExpensesDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.dao.others.JVPostingEntryDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * Service implementation for methods that depends on <b>JVPosting</b>.<br>
 * 
 */
public class JVPostingServiceImpl extends GenericService<JVPosting> implements JVPostingService {
	private static final Logger logger = LogManager.getLogger(JVPostingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private JVPostingDAO jVPostingDAO;
	private JVPostingEntryDAO jVPostingEntryDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private LegalExpensesDAO legalExpensesDAO;
	private PostingsDAO postingsDAO;
	private AccountProcessUtil accountProcessUtil;

	private FinanceMainService financeMainService;
	private CurrencyService currencyService;
	private TransactionCodeService transactionCodeService;
	private AccountMappingService accountMappingService;
	private FinanceMainDAO financeMainDAO;
	private FinanceWriteoffDAO financeWriteoffDAO;

	public JVPostingServiceImpl() {
		super();
	}

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the jVPostingDAO
	 */
	public JVPostingDAO getJVPostingDAO() {
		return jVPostingDAO;
	}

	/**
	 * @param jVPostingDAO
	 *            the jVPostingDAO to set
	 */
	public void setJVPostingDAO(JVPostingDAO jVPostingDAO) {
		this.jVPostingDAO = jVPostingDAO;
	}

	/**
	 * @return the jVPosting
	 */
	@Override
	public JVPosting getJVPosting() {
		return getJVPostingDAO().getJVPosting();
	}

	/**
	 * @return the jVPosting for New Record
	 */
	@Override
	public JVPosting getNewJVPosting() {
		return getJVPostingDAO().getNewJVPosting();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table JVPostings/JVPostings_Temp by
	 * using JVPostingDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using JVPostingDAO's update method 3) Audit the record in to AuditHeader and AdtJVPostings by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table JVPostings/JVPostings_Temp by
	 * using JVPostingDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using JVPostingDAO's update method 3) Audit the record in to AuditHeader and AdtJVPostings by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean
	 *            onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();
		if (jVPosting.isWorkflow()) {
			tableType = "_Temp";
		}

		if (jVPosting.isNewRecord()) {
			jVPosting.setBatchReference(getJVPostingDAO().save(jVPosting, tableType));
			if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
				for (int i = 0; i < jVPosting.getJVPostingEntrysList().size(); i++) {
					jVPosting.getJVPostingEntrysList().get(i).setBatchReference(jVPosting.getBatchReference());
					jVPosting.getJVPostingEntrysList().get(i).setVersion(jVPosting.getVersion());
				}
			}
		} else {
			getJVPostingDAO().update(jVPosting, tableType);
			// Process Entry Details
		}
		if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
			List<AuditDetail> details = jVPosting.getAuditDetailMap().get("JVPostingEntry");
			details = processJVPostingEntry(details, tableType, jVPosting, false, true);
			auditDetails.addAll(details);
		}

		String rcdMaintainSts = FinServiceEvent.JVPOSTING;
		financeMainDAO.updateMaintainceStatus(jVPosting.getReference(), rcdMaintainSts);

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	public boolean doAccountValidation(JVPosting jVPosting, List<JVPostingEntry> distinctEntryList) {
		logger.debug("Entering");

		// METHOD TO CHECK ALL THE RECORDS ARE VALIDATED OR NOT
		jVPosting.setValidationStatus(PennantConstants.POSTSTS_SUCCESS);

		logger.debug("Leaving");
		return jVPosting.getValidationStatus().equals(PennantConstants.POSTSTS_SUCCESS);
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * JVPostings by using JVPostingDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtJVPostings by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();
		getJVPostingDAO().delete(jVPosting, "");

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getJVPostingById fetch the details by using JVPostingDAO's getJVPostingById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPosting
	 */

	@Override
	public JVPosting getJVPostingById(long id) {
		JVPosting jvPosting = getJVPostingDAO().getJVPostingById(id, "_View");
		if (jvPosting != null) {
			String debitAc = null;
			List<JVPostingEntry> entries = getjVPostingEntryDAO()
					.getJVPostingEntryListById(jvPosting.getBatchReference(), "_View");
			if (entries != null && !entries.isEmpty()) {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getDerivedTxnRef() == 0) {
						debitAc = entries.get(i + 1).getAccount();
						entries.get(i).setDebitAccount(debitAc);
					}
				}
			}
			jvPosting.setJVPostingEntrysList(entries);
		}
		return jvPosting;
	}

	@Override
	public JVPosting getJVPostingByFileName(String fileName) {
		return getJVPostingDAO().getJVPostingByFileName(fileName);
	}

	/**
	 * getApprovedJVPostingById fetch the details by using JVPostingDAO's getJVPostingById method . with parameter id
	 * and type as blank. it fetches the approved records from the JVPostings.
	 * 
	 * @param id
	 *            (String)
	 * @return JVPosting
	 */

	public JVPosting getApprovedJVPostingById(long id) {
		JVPosting jvPosting = getJVPostingDAO().getJVPostingById(id, "_AView");
		if (jvPosting != null) {
			jvPosting.setJVPostingEntrysList(
					getjVPostingEntryDAO().getJVPostingEntryListById(jvPosting.getBatchReference(), "_AView"));
		}
		return jvPosting;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getJVPostingDAO().delete with
	 * parameters jVPosting,"" b) NEW Add new record in to main table by using getJVPostingDAO().save with parameters
	 * jVPosting,"" c) EDIT Update record in the main table by using getJVPostingDAO().update with parameters
	 * jVPosting,"" 3) Delete the record from the workFlow table by using getJVPostingDAO().delete with parameters
	 * jVPosting,"_Temp" 4) Audit the record in to AuditHeader and AdtJVPostings by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtJVPostings by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		boolean postingSuccess = true;
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JVPosting jVPosting = new JVPosting();
		BeanUtils.copyProperties((JVPosting) auditHeader.getAuditDetail().getModelData(), jVPosting);

		// User Action is Approved record so argument (isApproved) is False
		if (StringUtils.equals(jVPosting.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {

			List<JVPostingEntry> dbList = new ArrayList<JVPostingEntry>();

			for (JVPostingEntry entry : jVPosting.getJVPostingEntrysList()) {
				dbList.add(entry);
				dbList.addAll(getPostingsPreparationUtil().prepareJVPostingEntry(entry, jVPosting.getCurrency(),
						CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
						CurrencyUtil.getFormat(jVPosting.getCurrency()), false));
			}

			financeMainDAO.updateMaintainceStatus(jVPosting.getExpReference(), "");

			// Processing Account Postings from Approver level
			Collections.sort(dbList, new EntryComparator());
			long linkedTranId = Long.MIN_VALUE;
			try {

				if (jVPosting.getBatchReference() <= 0) {
					jVPosting.setBatchReference(jVPostingDAO.createBatchReference());
				}

				List<ReturnDataSet> list = getPostingsPreparationUtil().processEntryList(dbList, jVPosting);

				getAccountProcessUtil().procAccountUpdate(list);

				if (list != null && list.size() > 0) {
					ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
					for (int i = 0; i < list.size(); i++) {
						ReturnDataSet set = list.get(i);
						if (!("0000".equals(set.getErrorId()) || StringUtils.isEmpty(set.getErrorId()))) {
							postingSuccess = true;
						} else {
							linkedTranId = set.getLinkedTranId();
							set.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
							set.setTransOrder(i);
							set.setPostingId(set.getPostingId() + i);
						}
					}

					getPostingsDAO().saveBatch(list);
					auditHeader.setErrorList(errorDetails);
				}
			} catch (Exception e) {
				postingSuccess = false;
				logger.error("Exception: ", e);
			}

			if (postingSuccess) {
				jVPosting.setJVPostingEntrysList(dbList);
				// IF ALL THE ENTRIES ARE POSTED WITH SUCCESS OR FAIL THEN
				// UPDATE
				if (postingSuccess) {
					// If All Entries got Success then Header status is success.
					jVPosting.setBatchPostingStatus(PennantConstants.POSTSTS_SUCCESS);
					for (JVPostingEntry entry : jVPosting.getJVPostingEntrysList()) {
						entry.setPostingStatus(PennantConstants.POSTSTS_SUCCESS);
						entry.setLinkedTranId(linkedTranId);
					}
				}
				if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, jVPosting.getFinSourceID())) {
					// Update Child Records Status
					getjVPostingEntryDAO().updateListPostingStatus(jVPosting.getJVPostingEntrysList(), "_Temp", true);
					// Updating Header Status
					getjVPostingDAO().updateBatchPostingStatus(jVPosting, "_Temp");
				}
				// Regular Approving Process moving total Batch into main table
				// and remove from Temp Table.
				approveRecords(auditHeader, jVPosting);
				// ### 29-06-2018 Start - PSD Ticket ID 127014
			} else {
				auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
						"Invalid accounting configuration, please contact administrator", null));
			}
			// ### 29-06-2018 END - PSD Ticket ID 127014
		}
		// Logic for posting threads creation
		return auditHeader;
	}

	private void approveRecords(AuditHeader auditHeader, JVPosting jVPosting) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType = "";

		// Checking if it is Re-Posting Process, If Yes then Update record in
		// Main Table otherwise Insert.
		JVPosting rePosting = getjVPostingDAO().getJVPostingById(jVPosting.getBatchReference(), "_AView");
		if (rePosting != null) {
			jVPosting.setVersion(jVPosting.getVersion() + 1);
			getjVPostingDAO().update(jVPosting, "");
		} else {
			// Setting Work flow details Empty for Successfully Posted Records.
			tranType = PennantConstants.TRAN_ADD;
			jVPosting.setRoleCode("");
			jVPosting.setNextRoleCode("");
			jVPosting.setTaskId("");
			jVPosting.setNextTaskId("");
			jVPosting.setWorkflowId(0);
			jVPosting.setRecordType("");
			jVPosting.setValidationStatus(PennantConstants.POSTSTS_SUCCESS);
			//Bug fix before main table saving delete data in temp table
			getjVPostingDAO().delete(jVPosting, "_Temp");
			// Saving In main Table
			getjVPostingDAO().save(jVPosting, "");
		}

		if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
			List<AuditDetail> details = jVPosting.getAuditDetailMap().get("JVPostingEntry");
			details = processJVPostingEntry(details, "", jVPosting, false, true);
			auditDetails.addAll(details);
		}

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, jVPosting.getFinSourceID())
				&& !UploadConstants.MISC_POSTING_UPLOAD.equals("MiscPostingUpload")) {
			getjVPostingDAO().delete(jVPosting, "_Temp");
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader
				.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "_Temp", auditHeader.getAuditTranType())));
		auditHeader
				.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, jVPosting.getBefImage(), jVPosting));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(jVPosting);
		auditHeader
				.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, jVPosting.getBefImage(), jVPosting));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		// getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
	}

	private class EntryComparator implements Comparator<JVPostingEntry> {
		public EntryComparator() {
			//
		}

		@Override
		public int compare(JVPostingEntry e1, JVPostingEntry e2) {
			if (e1.getTxnReference() > e2.getTxnReference()) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getJVPostingDAO().delete with parameters jVPosting,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtJVPostings by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		jVPostingDAO.delete(jVPosting, "_Temp");
		financeMainDAO.updateMaintainceStatus(jVPosting.getExpReference(), "");

		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, jVPosting.getBefImage(), jVPosting));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "_Temp", auditTranType)));

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean
	 *            onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,
				onlineRequest);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<>());
		JVPosting jVPosting = (JVPosting) auditDetail.getModelData();

		JVPosting tempJVPosting = null;
		if (jVPosting.isWorkflow()) {
			tempJVPosting = jVPostingDAO.getJVPostingById(jVPosting.getId(), "_Temp");
		}
		JVPosting befJVPosting = jVPostingDAO.getJVPostingById(jVPosting.getId(), "");

		JVPosting oldJVPosting = jVPosting.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = Long.toString(jVPosting.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_BatchReference") + ":" + valueParm[0];

		if (jVPosting.isNewRecord()) { // for New record or new record into work flow

			if (!jVPosting.isWorkflow()) {// With out Work flow only new records
				if (befJVPosting != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jVPosting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																								// records
																							// type
																							// is
																							// new
					if (befJVPosting != null || tempJVPosting != null) { // if
																				// records
																			// already
																			// exists
																			// in
																			// the
																			// main
																			// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJVPosting == null || tempJVPosting != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!jVPosting.isWorkflow()) { // With out Work flow for update and
												// delete

				if (befJVPosting == null) { // if records not exists in the main
												// table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldJVPosting != null && !oldJVPosting.getLastMntOn().equals(befJVPosting.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempJVPosting == null) { // if records not exists in the
													// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
				if (oldJVPosting != null && !StringUtils.equals(oldJVPosting.getLastMntOn().toString(),
						tempJVPosting.getLastMntOn().toString())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// To validate if the posting amount Exceeds the tracked amount for
		// expreference.
		if (jVPosting.getExpReference() != null) {
			LegalExpenses expenses = getLegalExpensesDAO().getLegalExpensesById(jVPosting.getExpReference(), "_Aview");
			if (expenses != null && jVPosting.getTotDebitsByBatchCcy().compareTo(expenses.getAmount()) > 0) {
				valueParm[0] = jVPosting.getExpReference();
				errParm[0] = PennantJavaUtil.getLabel("label_LegalExpensesList_ExpReference.value") + ":"
						+ valueParm[0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "E0061", errParm, valueParm), usrLanguage));
			}
		}

		// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?
		String reference = jVPosting.getReference();
		String rcdMntnSts = financeMainDAO.getFinanceMainByRcdMaintenance(reference, "_View");
		if (StringUtils.isNotEmpty(rcdMntnSts) && !FinServiceEvent.JVPOSTING.equals(rcdMntnSts)) {
			String[] valueParm1 = new String[1];
			valueParm1[0] = rcdMntnSts;
			auditDetail.setErrorDetail(new ErrorDetail("LMS001", valueParm1));
		}

		if (financeWriteoffDAO.isWriteoffLoan(reference, "")) {
			String[] valueParm1 = new String[1];
			valueParm1[0] = "";
			auditDetail.setErrorDetail(new ErrorDetail("FWF001", valueParm1));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jVPosting.isWorkflow()) {
			auditDetail.setBefImage(befJVPosting);
		}

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		JVPosting jvPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (jvPosting.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (jvPosting.getJVPostingEntrysList() != null && jvPosting.getJVPostingEntrysList().size() > 0) {
			auditDetailMap.put("JVPostingEntry", setJVPostingEntryAuditData(jvPosting, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("JVPostingEntry"));
		}

		jvPosting.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(jvPosting);
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
	private List<AuditDetail> setJVPostingEntryAuditData(JVPosting jVPosting, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new JVPostingEntry(),
				new JVPostingEntry().getExcludeFields());

		for (int i = 0; i < jVPosting.getJVPostingEntrysList().size(); i++) {

			JVPostingEntry jVPostingEntry = jVPosting.getJVPostingEntrysList().get(i);
			jVPostingEntry.setWorkflowId(jVPosting.getWorkflowId());

			boolean isRcdType = false;
			if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				jVPostingEntry.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			jVPostingEntry.setRecordStatus(jVPosting.getRecordStatus());
			jVPostingEntry.setUserDetails(jVPosting.getUserDetails());
			jVPostingEntry.setLastMntOn(jVPosting.getLastMntOn());

			if (StringUtils.isNotBlank(jVPostingEntry.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						jVPostingEntry.getBefImage(), jVPostingEntry));
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
	private List<AuditDetail> processJVPostingEntry(List<AuditDetail> auditDetails, String type, JVPosting jVPosting,
			boolean deleteUpdateFlag, boolean generateEntry) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			JVPostingEntry jVPostingEntry = (JVPostingEntry) auditDetails.get(i).getModelData();
			jVPostingEntry.setAccount(PennantApplicationUtil.unFormatAccountNumber(jVPostingEntry.getAccount()));
			jVPostingEntry.setBatchReference(jVPosting.getBatchReference());
			jVPostingEntry.setRoleCode(jVPosting.getRoleCode());
			jVPostingEntry.setNextRoleCode(jVPosting.getNextRoleCode());
			jVPostingEntry.setTaskId(jVPosting.getTaskId());
			jVPostingEntry.setNextTaskId(jVPosting.getNextTaskId());
			jVPostingEntry.setWorkflowId(jVPosting.getWorkflowId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
			}

			if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (jVPostingEntry.isNewRecord()) {
				saveRecord = true;
				if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (jVPostingEntry.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = jVPostingEntry.getRecordType();
				recordStatus = jVPostingEntry.getRecordStatus();
				jVPostingEntry.setRecordType("");
				jVPostingEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				if (jVPostingEntry.isNewRecord()) {
					jVPostingEntry.setTxnReference(0);
				}
				jVPostingEntryDAO.save(jVPostingEntry, type);
				if (generateEntry) {
					for (JVPostingEntry entry : getPostingsPreparationUtil().prepareJVPostingEntry(jVPostingEntry,
							jVPosting.getCurrency(), CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
							CurrencyUtil.getFormat(jVPosting.getCurrency()), false)) {
						entry.setTxnReference(jVPostingEntry.getTxnReference());
						getjVPostingEntryDAO().save(entry, type);
					}
				}
			}

			if (updateRecord) {
				if (jVPostingEntry.getTxnAmount_Ac().compareTo(BigDecimal.ZERO) < 0) {
					jVPostingEntry.setTxnAmount_Ac(jVPostingEntry.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
				}
				if (deleteUpdateFlag) {
					jVPostingEntryDAO.updateDeletedDetails(jVPostingEntry, type);
				} else {
					jVPostingEntryDAO.update(jVPostingEntry, type);
					if (generateEntry) {
						for (JVPostingEntry entry : getPostingsPreparationUtil().prepareJVPostingEntry(jVPostingEntry,
								jVPosting.getCurrency(), CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
								CurrencyUtil.getFormat(jVPosting.getCurrency()), false)) {
							entry.setTxnReference(jVPostingEntry.getTxnReference());
							getjVPostingEntryDAO().update(entry, type);
						}
					}
				}
			}

			if (deleteRecord) {
				jVPostingEntryDAO.delete(jVPostingEntry, type);
				if (generateEntry) {
					for (JVPostingEntry entry : getPostingsPreparationUtil().prepareJVPostingEntry(jVPostingEntry,
							jVPosting.getCurrency(), CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
							CurrencyUtil.getFormat(jVPosting.getCurrency()), false)) {
						entry.setTxnReference(jVPostingEntry.getTxnReference());
						jVPostingEntryDAO.delete(entry, type);
					}
				}
			}

			if (approveRec) {
				jVPostingEntry.setRecordType(rcdType);
				jVPostingEntry.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(jVPostingEntry);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method deletion of feeTier list with existing fee type
	 * 
	 * @param jvPosting
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(JVPosting jvPosting, String tableType, String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		List<JVPostingEntry> entryList = new ArrayList<JVPostingEntry>();
		if (jvPosting.getJVPostingEntrysList() != null && jvPosting.getJVPostingEntrysList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new JVPostingEntry(),
					new JVPostingEntry().getExcludeFields());
			for (int i = 0; i < jvPosting.getJVPostingEntrysList().size(); i++) {
				JVPostingEntry jvEntry = jvPosting.getJVPostingEntrysList().get(i);
				entryList.add(jvEntry);
				entryList.addAll(getPostingsPreparationUtil().prepareJVPostingEntry(jvEntry, jvPosting.getCurrency(),
						CurrencyUtil.getCcyNumber(jvPosting.getCurrency()),
						CurrencyUtil.getFormat(jvPosting.getCurrency()), false));
			}
			for (int i = 0; i < entryList.size(); i++) {
				JVPostingEntry jvEntry = entryList.get(i);
				if (StringUtils.isNotEmpty(jvEntry.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], jvEntry.getBefImage(),
							jvEntry));
				}
				getjVPostingEntryDAO().deleteByID(jvEntry, tableType);
			}
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

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				JVPostingEntry jVPostingEntry = (JVPostingEntry) ((AuditDetail) list.get(i)).getModelData();

				rcdType = jVPostingEntry.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							jVPostingEntry.getBefImage(), jVPostingEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	@Override
	public void deleteIAEntries(long batchReference) {
		logger.debug("Entering");
		getjVPostingEntryDAO().deleteIAEntries(batchReference);
		logger.debug("Leaving");
	}

	public List<ErrorDetail> doMiscellaneousValidations(final JVPosting posting) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		if (StringUtils.isBlank(posting.getBranch())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Branch";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isBlank(posting.getReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Reference";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		FinanceMain financeMain = financeMainService.getFinanceByFinReference(posting.getReference(), "_AView");
		if (null == financeMain) {
			String[] valueParm = new String[1];
			valueParm[0] = "branch " + posting.getBranch();

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", valueParm)));

			return errorsList;
		}

		if (!(StringUtils.equals(posting.getBranch(), financeMain.getFinBranch()))) {
			String[] valueParm = new String[1];
			valueParm[0] = posting.getBranch();

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));

			return errorsList;
		}

		if (StringUtils.isBlank(posting.getBatch())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Batch";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.length(posting.getBatch()) > 5) {
			String[] valueParm = new String[2];
			valueParm[0] = "batch";
			valueParm[1] = "5";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", valueParm)));

			return errorsList;
		}

		if (!(StringUtils.isBlank(posting.getCurrency()))) {
			Currency currency = currencyService.getCurrencyForCode(posting.getCurrency());
			if (null == currency) {
				String[] valueParm = new String[1];
				valueParm[0] = "currency " + posting.getCurrency();

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", valueParm)));

				return errorsList;
			}

			if (!(StringUtils.equals(posting.getCurrency(), currency.getCcyCode()))) {
				String[] valueParm = new String[1];
				valueParm[0] = "for currency " + posting.getCurrency();

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("API004", valueParm)));

				return errorsList;
			}
		}

		// Removing Validation because We should consider inactive loans also for miscellaneous postings.
		/*
		 * int count = financeMainService.getFinanceCountById(posting.getReference(), false); if (count <= 0) { String[]
		 * valueParm = new String[1]; valueParm[0] = posting.getReference();
		 * 
		 * errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90201", valueParm)));
		 * 
		 * return errorsList; }
		 */

		List<JVPostingEntry> postingEntries = posting.getJVPostingEntrysList();
		if (CollectionUtils.isEmpty(postingEntries)) {
			String[] valueParm = new String[1];
			valueParm[0] = "postingEntry";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		BigDecimal txnAmount = null;
		String txnCode = null;
		String account = null;
		for (JVPostingEntry postingEntry : postingEntries) {
			txnAmount = postingEntry.getTxnAmount();
			txnCode = postingEntry.getTxnCode();
			account = postingEntry.getAccount();

			if (null == txnAmount) {
				String[] valueParm = new String[1];
				valueParm[0] = "transactionAmount";

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30556", valueParm)));

				return errorsList;
			}

			if (txnAmount.compareTo(BigDecimal.ZERO) == 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "transactionAmount";

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90127", valueParm)));

				return errorsList;
			}

			if (StringUtils.isBlank(txnCode)) {
				String[] valueParm = new String[1];
				valueParm[0] = "transactionCode";

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return errorsList;
			}

			TransactionCode transactionCode = transactionCodeService
					.getApprovedTransactionCodeById(postingEntry.getTxnCode());
			if (null == transactionCode) {
				String[] valueParm = new String[2];
				valueParm[0] = "transactionCode";
				valueParm[1] = postingEntry.getTxnCode();

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90295", valueParm)));

				return errorsList;
			}

			if (StringUtils.isBlank(account)) {
				String[] valueParm = new String[1];
				valueParm[0] = "account";

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return errorsList;
			}

			AccountMapping accountMapping = accountMappingService.getApprovedAccountMapping(postingEntry.getAccount());
			if (null == accountMapping) {
				String[] valueParm = new String[2];
				valueParm[0] = "Account";
				valueParm[1] = postingEntry.getAccount();

				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}
		}

		if (postingEntries.size() % 2 == 0) {
			BigDecimal totalDebits = BigDecimal.ZERO;
			BigDecimal totalCredits = BigDecimal.ZERO;
			for (JVPostingEntry postingEntry : postingEntries) {
				TransactionCode transactionCode = transactionCodeService
						.getApprovedTransactionCodeById(postingEntry.getTxnCode());
				switch (transactionCode.getTranType()) {
				case "D":
					totalDebits = totalDebits.add(postingEntry.getTxnAmount());
					break;
				case "C":
					totalCredits = totalCredits.add(postingEntry.getTxnAmount());
					break;
				}
			}

			if (!(totalDebits.compareTo(totalCredits) == 0)) {
				String[] valueParm = new String[2];
				valueParm[0] = "TransactionAmount " + totalCredits.toString();
				valueParm[1] = totalDebits.toString();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));

				return errorsList;
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "Credit and Debit PostingEntry";

			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		return errorsList;
	}

	/**
	 * This method gets the max endnum
	 * 
	 * @param Inventory
	 *            (inventory)
	 * @return inventory
	 */
	public JVPostingDAO getjVPostingDAO() {
		return jVPostingDAO;
	}

	public void setjVPostingDAO(JVPostingDAO jVPostingDAO) {
		this.jVPostingDAO = jVPostingDAO;
	}

	public JVPostingEntryDAO getjVPostingEntryDAO() {
		return jVPostingEntryDAO;
	}

	public void setjVPostingEntryDAO(JVPostingEntryDAO jVPostingEntryDAO) {
		this.jVPostingEntryDAO = jVPostingEntryDAO;
	}

	@Override
	public JVPostingEntry getNewJVPostingEntry() {
		return getjVPostingEntryDAO().getNewJVPostingEntry();
	}

	@Override
	public List<JVPostingEntry> getJVPostingEntryListById(long id) {
		return getjVPostingEntryDAO().getJVPostingEntryListById(id, "_View");
	}

	@Override
	public List<JVPostingEntry> getFailureJVPostingEntryListById(long batchRef) {
		return getjVPostingEntryDAO().getFailureJVPostingEntryListById(batchRef, "_View");
	}

	@Override
	public JVPostingEntry getJVPostingEntryById(long id, long txnRef, long acEntryRef) {
		return getjVPostingEntryDAO().getJVPostingEntryById(id, txnRef, acEntryRef, "_View");
	}

	@Override
	public JVPostingEntry getApprovedJVPostingEntryById(long id, long txnRef, long acEntryRef) {
		return getjVPostingEntryDAO().getJVPostingEntryById(id, txnRef, acEntryRef, "_AView");
	}

	@Override
	public long save(JVPostingEntry externalAcEntry, String baseCcy, String baseCcyNumber, int baseCcyEditField,
			boolean addIAEntry) {
		long txnReference = getjVPostingEntryDAO().save(externalAcEntry, "_Temp");
		if (addIAEntry) {
			for (JVPostingEntry entry : getPostingsPreparationUtil().prepareJVPostingEntry(externalAcEntry, baseCcy,
					baseCcyNumber, baseCcyEditField, false)) {
				entry.setTxnReference(externalAcEntry.getTxnReference());
				getjVPostingEntryDAO().save(entry, "_Temp");
			}
		}
		return txnReference;

	}

	@Override
	public void update(JVPostingEntry externalAcEntry, String baseCcy, String baseCcyNumber, int baseCcyEditField,
			boolean addIAEntry, String type) {
		getjVPostingEntryDAO().update(externalAcEntry, "_Temp");
		if (addIAEntry) {
			for (JVPostingEntry entry : getPostingsPreparationUtil().prepareJVPostingEntry(externalAcEntry, baseCcy,
					baseCcyNumber, baseCcyEditField, false)) {
				entry.setNewRecord(false);
				getjVPostingEntryDAO().update(entry, "_Temp");
			}
		}
	}

	@Override
	public void deleteByID(JVPostingEntry jVPostingEntry, String type) {
		getjVPostingEntryDAO().deleteByID(jVPostingEntry, "_Temp");
	}

	@Override
	public JVPostingEntry getJVPostingEntryById(long batchRef, long txnReference, String account, String txnEntry,
			BigDecimal txnAmount) {
		return getjVPostingEntryDAO().getJVPostingEntryById(batchRef, txnReference, account, txnEntry, txnAmount,
				"_View");
	}

	@Override
	public JVPosting getJVPostingBatchById(long id) {
		return getJVPostingDAO().getJVPostingById(id, "_TView");
	}

	@Override
	public List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef) {
		return getjVPostingEntryDAO().getDeletedJVPostingEntryListById(batchRef, "_TView");
	}

	@Override
	public void updateDeleteFlag(JVPostingEntry jVPostingEntry) {
		getjVPostingEntryDAO().updateDeleteFlag(jVPostingEntry, "_Temp");
	}

	@Override
	public void updateValidationStatus(JVPosting jVPosting) {
		getJVPostingDAO().updateValidationStatus(jVPosting, "_Temp");
	}

	@Override
	public int getMaxSeqNumForCurrentDay(JVPostingEntry jVPostingEntry) {
		return getjVPostingEntryDAO().getMaxSeqNumForCurrentDay(jVPostingEntry);
	}

	@Override
	public void upDateSeqNoForCurrentDayBatch(JVPostingEntry jVPostingEntry) {
		getjVPostingEntryDAO().upDateSeqNoForCurrentDayBatch(jVPostingEntry);
	}

	@Override
	public void updateWorkFlowDetails(JVPostingEntry jVPostingEntry) {
		getjVPostingEntryDAO().updateWorkFlowDetails(jVPostingEntry, "_Temp");
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Override
	public long getBatchRerbyExpRef(String expReference) {
		return getjVPostingDAO().getBatchRerbyExpRef(expReference);
	}

	public LegalExpensesDAO getLegalExpensesDAO() {
		return legalExpensesDAO;
	}

	public void setLegalExpensesDAO(LegalExpensesDAO legalExpensesDAO) {
		this.legalExpensesDAO = legalExpensesDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	@Autowired
	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@Autowired
	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}

	@Autowired
	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}

	@Override
	public AuditHeader processData(AuditHeader auditHeader, boolean postingSuccess) {

		JVPosting jVPosting = new JVPosting();
		BeanUtils.copyProperties((JVPosting) auditHeader.getAuditDetail().getModelData(), jVPosting);

		List<JVPostingEntry> dbList = new ArrayList<JVPostingEntry>();

		for (JVPostingEntry entry : jVPosting.getJVPostingEntrysList()) {
			dbList.add(entry);
			dbList.addAll(getPostingsPreparationUtil().prepareJVPostingEntry(entry, jVPosting.getCurrency(),
					CurrencyUtil.getCcyNumber(jVPosting.getCurrency()), CurrencyUtil.getFormat(jVPosting.getCurrency()),
					false));
		}

		// Processing Account Postings from Approver level
		Collections.sort(dbList, new EntryComparator());
		long linkedTranId = Long.MIN_VALUE;
		try {
			List<ReturnDataSet> list = getPostingsPreparationUtil().processEntryList(dbList, jVPosting);
			// Post and save
			/*
			 * getPostingsPreparationUtil().postingsExecProcess(list, jVPosting.getBranch(), DateUtility.getAppDate(),
			 * "Y", false, false, linkedTranId, BigDecimal.ZERO, "", false);
			 */

			getAccountProcessUtil().procAccountUpdate(list);

			if (list != null && list.size() > 0) {
				ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
				for (int i = 0; i < list.size(); i++) {
					ReturnDataSet set = list.get(i);
					if (!("0000".equals(set.getErrorId()) || StringUtils.isEmpty(set.getErrorId()))) {
						postingSuccess = true;
					} else {
						linkedTranId = set.getLinkedTranId();
						set.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
						set.setTransOrder(i);
						set.setPostingId(set.getPostingId() + i);
					}
				}

				getPostingsDAO().saveBatch(list);
				auditHeader.setErrorList(errorDetails);
			}
		} catch (InterfaceException e) {
			postingSuccess = false;
			logger.error("Exception: ", e);
		}

		if (postingSuccess) {
			jVPosting.setJVPostingEntrysList(dbList);
			// IF ALL THE ENTRIES ARE POSTED WITH SUCCESS OR FAIL THEN
			// UPDATE
			if (postingSuccess) {
				// If All Entries got Success then Header status is success.
				jVPosting.setBatchPostingStatus(PennantConstants.POSTSTS_SUCCESS);
				for (JVPostingEntry entry : jVPosting.getJVPostingEntrysList()) {
					entry.setPostingStatus(PennantConstants.POSTSTS_SUCCESS);
					entry.setLinkedTranId(linkedTranId);
					entry.setPostingDate(SysParamUtil.getAppDate());
				}
			}
			try {
				// Update Child Records Status
				getjVPostingEntryDAO().updateListPostingStatus(jVPosting.getJVPostingEntrysList(), "_Temp", true);
				// Updating Header Status
				getjVPostingDAO().updateBatchPostingStatus(jVPosting, "_Temp");
			} catch (Exception e) {
				logger.error(e);
			}
			// Regular Approving Process moving total Batch into main table
			// and remove from Temp Table.
			approveRecords(auditHeader, jVPosting);
		}
		return auditHeader;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}
}