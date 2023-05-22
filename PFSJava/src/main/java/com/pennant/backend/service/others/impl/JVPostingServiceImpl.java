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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
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
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;

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
	private FinanceMainService financeMainService;
	private CurrencyService currencyService;
	private TransactionCodeService transactionCodeService;
	private AccountMappingService accountMappingService;
	private FinanceMainDAO financeMainDAO;
	private FinanceWriteoffDAO financeWriteoffDAO;

	public JVPostingServiceImpl() {
		super();
	}

	@Override
	public JVPosting getJVPosting() {
		return jVPostingDAO.getJVPosting();
	}

	@Override
	public JVPosting getNewJVPosting() {
		return jVPostingDAO.getNewJVPosting();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

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
			jVPosting.setBatchReference(jVPostingDAO.save(jVPosting, tableType));
			if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
				for (int i = 0; i < jVPosting.getJVPostingEntrysList().size(); i++) {
					jVPosting.getJVPostingEntrysList().get(i).setBatchReference(jVPosting.getBatchReference());
					jVPosting.getJVPostingEntrysList().get(i).setVersion(jVPosting.getVersion());
				}
			}
		} else {
			jVPostingDAO.update(jVPosting, tableType);
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
		auditHeaderDAO.addAudit(auditHeader);

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

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();
		jVPostingDAO.delete(jVPosting, "");

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public JVPosting getJVPostingById(long id) {
		JVPosting jvPosting = jVPostingDAO.getJVPostingById(id, "_View");
		if (jvPosting != null) {
			String debitAc = null;
			List<JVPostingEntry> entries = jVPostingEntryDAO.getJVPostingEntryListById(jvPosting.getBatchReference(),
					"_View");
			if (entries != null && !entries.isEmpty()) {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getDerivedTxnRef() == 0) {
						debitAc = entries.get(i).getAccount();
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
		return jVPostingDAO.getJVPostingByFileName(fileName);
	}

	public JVPosting getApprovedJVPostingById(long id) {
		JVPosting jvPosting = jVPostingDAO.getJVPostingById(id, "_AView");
		if (jvPosting != null) {
			jvPosting.setJVPostingEntrysList(
					jVPostingEntryDAO.getJVPostingEntryListById(jvPosting.getBatchReference(), "_AView"));
		}
		return jvPosting;
	}

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
				dbList.addAll(postingsPreparationUtil.prepareJVPostingEntry(entry, jVPosting.getCurrency(),
						CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
						CurrencyUtil.getFormat(jVPosting.getCurrency()), false));
			}

			financeMainDAO.updateMaintainceStatus(jVPosting.getReference(), "");

			// Processing Account Postings from Approver level
			Collections.sort(dbList, new EntryComparator());
			long linkedTranId = Long.MIN_VALUE;
			try {

				if (jVPosting.getBatchReference() <= 0) {
					jVPosting.setBatchReference(jVPostingDAO.createBatchReference());
				}

				List<ReturnDataSet> list = postingsPreparationUtil.processEntryList(dbList, jVPosting);

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

					postingsDAO.saveBatch(list);
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
				if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, jVPosting.getFinSourceID())
						&& !RequestSource.UPLOAD.equals(jVPosting.getRequestSource())) {
					// Update Child Records Status
					jVPostingEntryDAO.updateListPostingStatus(jVPosting.getJVPostingEntrysList(), "_Temp", true);
					// Updating Header Status
					jVPostingDAO.updateBatchPostingStatus(jVPosting, "_Temp");
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

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		jVPostingDAO.delete(jVPosting, "_Temp");
		financeMainDAO.updateMaintainceStatus(jVPosting.getReference(), "");

		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, jVPosting.getBefImage(), jVPosting));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "_Temp", auditTranType)));

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

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

		if (!RequestSource.UPLOAD.equals(jVPosting.getRequestSource())) {
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
									new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
									usrLanguage));
						}
					} else { // if records not exists in the Main flow table
						if (befJVPosting == null || tempJVPosting != null) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
									usrLanguage));
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
				LegalExpenses expenses = legalExpensesDAO.getLegalExpensesById(jVPosting.getExpReference(), "_Aview");
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
			FinanceMain fm = financeMainDAO.getFinanceMain(reference, TableType.MAIN_TAB);

			String rcdMntnSts = fm.getRcdMaintainSts();

			if (StringUtils.isNotEmpty(rcdMntnSts) && !FinServiceEvent.JVPOSTING.equals(rcdMntnSts)) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = rcdMntnSts;
				auditDetail.setErrorDetail(new ErrorDetail("LMS001", valueParm1));
			}

			if (financeWriteoffDAO.isWriteoffLoan(fm.getFinID(), "")) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = "";
				auditDetail.setErrorDetail(new ErrorDetail("FWF001", valueParm1));
			}
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

	@Override
	public void deleteIAEntries(long batchReference) {
		logger.debug("Entering");
		jVPostingEntryDAO.deleteIAEntries(batchReference);
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

		FinanceMain financeMain = financeMainService.getFinanceMain(posting.getReference(), TableType.BOTH_TAB);
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

			if (txnAmount.compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "transactionAmount";
				valueParm[1] = "Zero";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));

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

	@Override
	public JVPostingEntry getNewJVPostingEntry() {
		return jVPostingEntryDAO.getNewJVPostingEntry();
	}

	@Override
	public List<JVPostingEntry> getJVPostingEntryListById(long id) {
		return jVPostingEntryDAO.getJVPostingEntryListById(id, "_View");
	}

	@Override
	public List<JVPostingEntry> getFailureJVPostingEntryListById(long batchRef) {
		return jVPostingEntryDAO.getFailureJVPostingEntryListById(batchRef, "_View");
	}

	@Override
	public JVPostingEntry getJVPostingEntryById(long id, long txnRef, long acEntryRef) {
		return jVPostingEntryDAO.getJVPostingEntryById(id, txnRef, acEntryRef, "_View");
	}

	@Override
	public JVPostingEntry getApprovedJVPostingEntryById(long id, long txnRef, long acEntryRef) {
		return jVPostingEntryDAO.getJVPostingEntryById(id, txnRef, acEntryRef, "_AView");
	}

	@Override
	public long save(JVPostingEntry externalAcEntry, String baseCcy, String baseCcyNumber, int baseCcyEditField,
			boolean addIAEntry) {
		long txnReference = jVPostingEntryDAO.save(externalAcEntry, "_Temp");
		if (addIAEntry) {
			for (JVPostingEntry entry : postingsPreparationUtil.prepareJVPostingEntry(externalAcEntry, baseCcy,
					baseCcyNumber, baseCcyEditField, false)) {
				entry.setTxnReference(externalAcEntry.getTxnReference());
				jVPostingEntryDAO.save(entry, "_Temp");
			}
		}
		return txnReference;

	}

	@Override
	public void update(JVPostingEntry externalAcEntry, String baseCcy, String baseCcyNumber, int baseCcyEditField,
			boolean addIAEntry, String type) {
		jVPostingEntryDAO.update(externalAcEntry, "_Temp");
		if (addIAEntry) {
			for (JVPostingEntry entry : postingsPreparationUtil.prepareJVPostingEntry(externalAcEntry, baseCcy,
					baseCcyNumber, baseCcyEditField, false)) {
				entry.setNewRecord(false);
				jVPostingEntryDAO.update(entry, "_Temp");
			}
		}
	}

	@Override
	public void deleteByID(JVPostingEntry jVPostingEntry, String type) {
		jVPostingEntryDAO.deleteByID(jVPostingEntry, "_Temp");
	}

	@Override
	public JVPostingEntry getJVPostingEntryById(long batchRef, long txnReference, String account, String txnEntry,
			BigDecimal txnAmount) {
		return jVPostingEntryDAO.getJVPostingEntryById(batchRef, txnReference, account, txnEntry, txnAmount, "_View");
	}

	@Override
	public JVPosting getJVPostingBatchById(long id) {
		return jVPostingDAO.getJVPostingById(id, "_TView");
	}

	@Override
	public List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef) {
		return jVPostingEntryDAO.getDeletedJVPostingEntryListById(batchRef, "_TView");
	}

	@Override
	public void updateDeleteFlag(JVPostingEntry jVPostingEntry) {
		jVPostingEntryDAO.updateDeleteFlag(jVPostingEntry, "_Temp");
	}

	@Override
	public void updateValidationStatus(JVPosting jVPosting) {
		jVPostingDAO.updateValidationStatus(jVPosting, "_Temp");
	}

	@Override
	public int getMaxSeqNumForCurrentDay(JVPostingEntry jVPostingEntry) {
		return jVPostingEntryDAO.getMaxSeqNumForCurrentDay(jVPostingEntry);
	}

	@Override
	public void upDateSeqNoForCurrentDayBatch(JVPostingEntry jVPostingEntry) {
		jVPostingEntryDAO.upDateSeqNoForCurrentDayBatch(jVPostingEntry);
	}

	@Override
	public void updateWorkFlowDetails(JVPostingEntry jVPostingEntry) {
		jVPostingEntryDAO.updateWorkFlowDetails(jVPostingEntry, "_Temp");
	}

	@Override
	public long getBatchRerbyExpRef(String expReference) {
		return jVPostingDAO.getBatchRerbyExpRef(expReference);
	}

	@Override
	public void processData(AuditHeader auditHeader, boolean postingSuccess) {
		JVPosting jVPosting = new JVPosting();

		BeanUtils.copyProperties((JVPosting) auditHeader.getAuditDetail().getModelData(), jVPosting);

		List<JVPostingEntry> jvList = new ArrayList<>();

		String ccyNumber = CurrencyUtil.getCcyNumber(jVPosting.getCurrency());
		int format = CurrencyUtil.getFormat(jVPosting.getCurrency());

		jVPosting.getJVPostingEntrysList().forEach(jv -> {
			jvList.add(jv);
			jvList.addAll(postingsPreparationUtil.prepareJVPostingEntry(jv, jVPosting.getCurrency(), ccyNumber, format,
					false));
		});

		List<JVPostingEntry> dbList = jvList.stream()
				.sorted((jv1, jv2) -> Long.compare(jv1.getTxnReference(), jv2.getTxnReference()))
				.collect(Collectors.toList());

		long linkedTranId = Long.MIN_VALUE;

		try {
			List<ReturnDataSet> list = postingsPreparationUtil.processEntryList(dbList, jVPosting);

			if (CollectionUtils.isNotEmpty(list)) {
				List<ErrorDetail> errorDetails = new ArrayList<>();
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

				postingsDAO.saveBatch(list);
				auditHeader.setErrorList(errorDetails);
			}
		} catch (InterfaceException e) {
			postingSuccess = false;
			logger.error(Literal.EXCEPTION, e);
		}

		if (postingSuccess) {
			jVPosting.setJVPostingEntrysList(dbList);
			jVPosting.setBatchPostingStatus(PennantConstants.POSTSTS_SUCCESS);
			Date appDate = SysParamUtil.getAppDate();

			for (JVPostingEntry entry : jVPosting.getJVPostingEntrysList()) {
				entry.setPostingStatus(PennantConstants.POSTSTS_SUCCESS);
				entry.setLinkedTranId(linkedTranId);
				entry.setPostingDate(appDate);
			}

			try {
				jVPostingEntryDAO.updateListPostingStatus(jVPosting.getJVPostingEntrysList(), "_Temp", true);
				jVPostingDAO.updateBatchPostingStatus(jVPosting, "_Temp");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			approveRecords(auditHeader, jVPosting);
		}
	}

	private void approveRecords(AuditHeader auditHeader, JVPosting jVPosting) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		JVPosting rePosting = jVPostingDAO.getJVPostingById(jVPosting.getBatchReference(), "_AView");
		if (rePosting != null) {
			jVPosting.setVersion(jVPosting.getVersion() + 1);
			jVPostingDAO.update(jVPosting, "");
		} else {
			jVPosting.setRoleCode("");
			jVPosting.setNextRoleCode("");
			jVPosting.setTaskId("");
			jVPosting.setNextTaskId("");
			jVPosting.setWorkflowId(0);
			jVPosting.setRecordType("");
			jVPosting.setValidationStatus(PennantConstants.POSTSTS_SUCCESS);
			jVPostingDAO.delete(jVPosting, "_Temp");
			jVPostingDAO.save(jVPosting, "");
		}

		if (CollectionUtils.isNotEmpty(jVPosting.getJVPostingEntrysList())) {
			auditDetails.addAll(processJVPostingEntry(jVPosting.getAuditDetailMap().get("JVPostingEntry"), "",
					jVPosting, false, true));
		}

		if (!PennantConstants.FINSOURCE_ID_API.equals(jVPosting.getFinSourceID())
				&& !UploadConstants.MISC_POSTING_UPLOAD.equals("MiscPostingUpload")) {
			jVPostingDAO.delete(jVPosting, "_Temp");
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader
				.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "_Temp", auditHeader.getAuditTranType())));
		auditHeader
				.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, jVPosting.getBefImage(), jVPosting));
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.ENTERING);
	}

	private List<AuditDetail> processJVPostingEntry(List<AuditDetail> auditDetails, String type, JVPosting jVPosting,
			boolean deleteUpdateFlag, boolean generateEntry) {
		logger.debug(Literal.ENTERING);

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
					for (JVPostingEntry entry : postingsPreparationUtil.prepareJVPostingEntry(jVPostingEntry,
							jVPosting.getCurrency(), CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
							CurrencyUtil.getFormat(jVPosting.getCurrency()), false)) {
						entry.setTxnReference(jVPostingEntry.getTxnReference());
						jVPostingEntryDAO.save(entry, type);
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
						for (JVPostingEntry entry : postingsPreparationUtil.prepareJVPostingEntry(jVPostingEntry,
								jVPosting.getCurrency(), CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
								CurrencyUtil.getFormat(jVPosting.getCurrency()), false)) {
							entry.setTxnReference(jVPostingEntry.getTxnReference());
							jVPostingEntryDAO.update(entry, type);
						}
					}
				}
			}

			if (deleteRecord) {
				jVPostingEntryDAO.delete(jVPostingEntry, type);
				if (generateEntry) {
					for (JVPostingEntry entry : postingsPreparationUtil.prepareJVPostingEntry(jVPostingEntry,
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

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetailsList = new ArrayList<>();

		if (CollectionUtils.isEmpty(list)) {
			logger.debug(Literal.LEAVING);
			return auditDetailsList;
		}

		for (AuditDetail ad : list) {
			JVPostingEntry jVPostingEntry = (JVPostingEntry) ((AuditDetail) ad).getModelData();

			String rcdType = jVPostingEntry.getRecordType();
			String transType = "";

			switch (rcdType) {
			case PennantConstants.RECORD_TYPE_NEW:
				transType = PennantConstants.TRAN_ADD;
				break;
			case PennantConstants.RECORD_TYPE_DEL:
			case PennantConstants.RECORD_TYPE_CAN:
				transType = PennantConstants.TRAN_DEL;
				break;
			default:
				transType = PennantConstants.TRAN_UPD;
				break;
			}

			auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) ad).getAuditSeq(),
					jVPostingEntry.getBefImage(), jVPostingEntry));
		}

		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	public List<AuditDetail> listDeletion(JVPosting jvPosting, String tableType, String adtTran) {
		List<AuditDetail> auditList = new ArrayList<>();
		List<JVPostingEntry> list = jvPosting.getJVPostingEntrysList();

		if (CollectionUtils.isEmpty(list)) {
			return auditList;
		}

		List<JVPostingEntry> entryList = new ArrayList<>();

		String[] fields = PennantJavaUtil.getFieldDetails(new JVPostingEntry(),
				new JVPostingEntry().getExcludeFields());

		String currency = jvPosting.getCurrency();
		String ccy = CurrencyUtil.getCcyNumber(currency);
		int format = CurrencyUtil.getFormat(currency);

		list.forEach(jv -> {
			entryList.add(jv);
			entryList.addAll(postingsPreparationUtil.prepareJVPostingEntry(jv, currency, ccy, format, false));
		});

		for (int i = 0; i < entryList.size(); i++) {
			JVPostingEntry jvEntry = entryList.get(i);
			if (StringUtils.isNotEmpty(jvEntry.getRecordType()) || StringUtils.isEmpty(tableType)) {
				auditList.add(new AuditDetail(adtTran, i + 1, fields[0], fields[1], jvEntry.getBefImage(), jvEntry));
			}

			jVPostingEntryDAO.deleteByID(jvEntry, tableType);
		}

		return auditList;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setjVPostingDAO(JVPostingDAO jVPostingDAO) {
		this.jVPostingDAO = jVPostingDAO;
	}

	@Autowired
	public void setjVPostingEntryDAO(JVPostingEntryDAO jVPostingEntryDAO) {
		this.jVPostingEntryDAO = jVPostingEntryDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setLegalExpensesDAO(LegalExpensesDAO legalExpensesDAO) {
		this.legalExpensesDAO = legalExpensesDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
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

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

}