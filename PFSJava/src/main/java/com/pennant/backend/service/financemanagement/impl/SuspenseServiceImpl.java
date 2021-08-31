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
 * * FileName : SuspenseServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified
 * Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.financemanagement.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * Service implementation for methods that depends on <b>FinanceSuspHead</b>.<br>
 * 
 */
public class SuspenseServiceImpl extends GenericFinanceDetailService implements SuspenseService {
	private static final Logger logger = LogManager.getLogger(SuspenseServiceImpl.class);

	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;

	public SuspenseServiceImpl() {
		super();
	}

	@Override
	public FinanceSuspHead getFinanceSuspHead() {
		return financeSuspHeadDAO.getFinanceSuspHead();
	}

	@Override
	public FinanceSuspHead getNewFinanceSuspHead() {
		return financeSuspHeadDAO.getNewFinanceSuspHead();
	}

	@Override
	public FinanceSuspHead getFinanceSuspHeadById(String finRef, boolean isEnquiry, String userRole,
			String procEdtEvent) {
		FinanceSuspHead suspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finRef, "_View");
		if (suspHead == null) {
			return null;
		}

		if (isEnquiry) {
			suspHead.setSuspDetailsList(financeSuspHeadDAO.getFinanceSuspDetailsListById(finRef));
			suspHead.setSuspPostingsList(postingsDAO.getPostingsByFinRefAndEvent(suspHead.getFinReference(),
					"'PIS_NORM','NORM_PIS'", true, "", "_View"));
		}

		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();

		scheduleData.setFinanceMain(financeMainDAO.getFinanceMainById(finRef, "_AView", false));

		if (scheduleData.getFinanceMain() != null) {

			if (StringUtils.isNotBlank(suspHead.getRecordType())) {
				scheduleData.getFinanceMain().setNewRecord(false);
			}

			if (StringUtils.isNotEmpty(suspHead.getRecordType())) {
				scheduleData.getFinanceMain().setNewRecord(false);
			}

			// Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finRef, "", false));
			scheduleData
					.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finRef, "", false));

			scheduleData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finRef, procEdtEvent, false, ""));
			scheduleData.setRepayDetails(financeRepaymentsDAO.getFinRepayListByFinRef(finRef, false, ""));
			scheduleData.setPenaltyDetails(recoveryDAO.getFinancePenaltysByFinRef(finRef, ""));

			scheduleData.setFinanceType(
					financeTypeDAO.getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

			// Finance Customer Details
			if (scheduleData.getFinanceMain().getCustID() != 0
					&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(customerDetailsService
						.getCustomerDetailsById(scheduleData.getFinanceMain().getCustID(), true, "_View"));
			}

			String finType = scheduleData.getFinanceType().getFinType();

			// Finance Check List Details
			// =======================================
			checkListDetailService.setFinanceCheckListDetails(financeDetail, finType, procEdtEvent, userRole);

			// Finance Fee Charge Details
			// =======================================
			List<Long> accSetIdList = new ArrayList<Long>();
			accSetIdList
					.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));
			if (!accSetIdList.isEmpty()) {
				financeDetail.setFeeCharges(
						transactionEntryDAO.getListFeeChargeRules(accSetIdList, AccountingEvent.NORM_PIS, "_AView", 0));
			}

			// Finance Stage Accounting Posting Details
			// =======================================
			financeDetail.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
					StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
					FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

			// Docuument Details
			financeDetail.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finRef,
					FinanceConstants.MODULE_NAME, procEdtEvent, "_View"));

			suspHead.setFinanceDetail(financeDetail);
		}
		return suspHead;
	}

	@Override
	public List<String> getSuspFinanceList() {
		return financeSuspHeadDAO.getSuspFinanceList();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		long serviceUID = Long.MIN_VALUE;
		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		Date curBDay = DateUtility.getAppDate();
		if (financeSuspHead.getFinanceDetail().getFinScheduleData().getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(finReference);
			finServInst.setFinEvent(financeSuspHead.getFinanceDetail().getModuleDefiner());

			financeSuspHead.getFinanceDetail().getFinScheduleData().setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction finSerList : financeSuspHead.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			if (finSerList.getInstructionUID() == Long.MIN_VALUE) {
				if (serviceUID == Long.MIN_VALUE) {
					serviceUID = Long.valueOf(ReferenceGenerator.generateNewServiceUID());
				}
				finSerList.setInstructionUID(serviceUID);
			} else {
				serviceUID = finSerList.getInstructionUID();
			}
		}

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		if (financeSuspHead.isWorkflow()) {
			tableType = "_Temp";
		}

		FinanceProfitDetail profitDetail = null;
		if (!financeSuspHead.isWorkflow()) {
			profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);

			AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain,
					financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), profitDetail,
					AccountingEvent.WRITEOFF, curBDay, financeMain.getMaturityDate());
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
			aeEvent.setDataMap(dataMap);

			try {
				aeEvent = postingsPreparationUtil.processPostingDetails(aeEvent);
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!aeEvent.isPostingSucess()) {
				String errParm = aeEvent.getErrorMessage();
				throw new InterfaceException("9999", errParm);
			}

		}

		if (financeSuspHead.isManualSusp()) {
			financeSuspHead.setFinIsInSusp(true);
			financeSuspHead.setFinSuspTrfDate(financeSuspHead.getFinSuspDate());
		}

		if (financeSuspHead.isNewRecord()) {
			financeSuspHeadDAO.save(financeSuspHead, tableType);
			auditHeader.getAuditDetail().setModelData(financeSuspHead);
			auditHeader.setAuditReference(financeSuspHead.getFinReference());
		} else {
			financeSuspHeadDAO.update(financeSuspHead, tableType);
		}

		// Save Document Details
		if (financeSuspHead.getFinanceDetail().getDocumentDetailsList() != null
				&& financeSuspHead.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeSuspHead.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType,
					financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					financeSuspHead.getFinanceDetail().getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (financeSuspHead.getFinanceDetail().getFinanceCheckList() != null
				&& !financeSuspHead.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(
					checkListDetailService.saveOrUpdate(financeSuspHead.getFinanceDetail(), tableType, serviceUID));
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		financeSuspHeadDAO.delete(financeSuspHead, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = new FinanceSuspHead();
		BeanUtils.copyProperties((FinanceSuspHead) auditHeader.getAuditDetail().getModelData(), financeSuspHead);
		String finReference = financeSuspHead.getFinReference();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : financeSuspHead.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			financeSuspHeadDAO.delete(financeSuspHead, "");
		} else {
			financeSuspHead.setRoleCode("");
			financeSuspHead.setNextRoleCode("");
			financeSuspHead.setTaskId("");
			financeSuspHead.setNextTaskId("");
			financeSuspHead.setWorkflowId(0);

			if (financeSuspHead.isManualSusp()) {
				financeSuspHead.setFinIsInSusp(true);
				financeSuspHead.setFinSuspTrfDate(financeSuspHead.getFinSuspDate());
			}

			if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeSuspHead.setRecordType("");
				financeSuspHeadDAO.save(financeSuspHead, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeSuspHead.setRecordType("");
				financeSuspHeadDAO.update(financeSuspHead, "");
			}
		}

		// Save Document Details
		if (financeSuspHead.getFinanceDetail().getDocumentDetailsList() != null
				&& financeSuspHead.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeSuspHead.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "",
					financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					financeSuspHead.getFinanceDetail().getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (financeSuspHead.getFinanceDetail().getFinanceCheckList() != null
				&& !financeSuspHead.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.doApprove(financeSuspHead.getFinanceDetail(), "", serviceUID));
		}

		// Finance Profit Details Updation
		FinanceProfitDetail finPftDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		Date curBussDate = DateUtility.getAppDate();

		FinanceMain financeMain = financeMainDAO.financeMainById(finReference, "", false);
		List<FinanceScheduleDetail> scheduleDetailList = financeScheduleDetailDAO()
				.getFinSchdDetailsForBatch(finReference);

		// Commitment Set Non-Performing Status
		if (StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {
			if (financeSuspHead.isManualSusp() || financeSuspHead.isFinIsInSusp()) {
				commitmentDAO.updateNonPerformStatus(financeMain.getFinCommitmentRef());
			}
		}

		// Document Details delete
		// =======================================
		listDocDeletion(financeSuspHead.getFinanceDetail(), "_Temp");

		// Checklist Details delete
		// =======================================
		checkListDetailService.delete(financeSuspHead.getFinanceDetail(), "_Temp", tranType);

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(financeMain.getFinReference(),
				financeSuspHead.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		finPftDetail = accrualService.calProfitDetails(financeMain, scheduleDetailList, finPftDetail, curBussDate);

		String worstSts = customerStatusCodeDAO.getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);
		profitDetailsDAO.update(finPftDetail, false);

		financeSuspHeadDAO.delete(financeSuspHead, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeSuspHead);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		// updating the processed with 1 in finstageAccountingLog
		finStageAccountingLogDAO.update(financeMain.getFinReference(),
				financeSuspHead.getFinanceDetail().getModuleDefiner(), false);

		logger.debug("Leaving");

		return auditHeader;
	}

	// Document Details List Maintenance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		documentDetailsDAO.deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String tranType = PennantConstants.TRAN_DEL;
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : financeSuspHead.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(financeMain.getFinReference(), FinServiceEvent.SUSPHEAD);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		financeSuspHeadDAO.delete(financeSuspHead, "_Temp");

		// Save Document Details
		if (financeSuspHead.getFinanceDetail().getDocumentDetailsList() != null
				&& financeSuspHead.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : financeSuspHead.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeSuspHead.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp",
					financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					financeSuspHead.getFinanceDetail().getModuleDefiner(), serviceUID);
			auditHeader.setAuditDetails(details);
			listDocDeletion(financeSuspHead.getFinanceDetail(), "_Temp");
		}

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(financeMain.getFinReference(),
				financeSuspHead.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		checkListDetailService.delete(financeSuspHead.getFinanceDetail(), "_Temp", tranType);

		auditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = financeSuspHead.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// Finance Check List Details
		// =======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(
						checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType()
					.equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			String finReference = financeDetail.getFinScheduleData().getFinReference();
			financeCheckList = checkListDetailService.getCheckListByFinRef(finReference, tableType);
			financeDetail.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(
						checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeSuspHead);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditDetail.getModelData();

		FinanceSuspHead tempFinanceSuspHead = null;
		if (financeSuspHead.isWorkflow()) {
			tempFinanceSuspHead = financeSuspHeadDAO.getFinanceSuspHeadById(financeSuspHead.getId(), "_Temp");
		}
		FinanceSuspHead befFinanceSuspHead = financeSuspHeadDAO.getFinanceSuspHeadById(financeSuspHead.getId(), "");
		FinanceSuspHead oldFinanceSuspHead = financeSuspHead.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeSuspHead.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeSuspHead.isNewRecord()) { // for New record or new record into work flow

			if (!financeSuspHead.isWorkflow()) {// With out Work flow only new records
				if (befFinanceSuspHead != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																								// new
					if (befFinanceSuspHead != null || tempFinanceSuspHead != null) { // if records already exists in the
																						// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceSuspHead == null || tempFinanceSuspHead != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeSuspHead.isWorkflow()) { // With out Work flow for update and delete

				if (befFinanceSuspHead == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceSuspHead != null
							&& !oldFinanceSuspHead.getLastMntOn().equals(befFinanceSuspHead.getLastMntOn())) {
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

				if (tempFinanceSuspHead == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldFinanceSuspHead != null && tempFinanceSuspHead != null
						&& !oldFinanceSuspHead.getLastMntOn().equals(tempFinanceSuspHead.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeSuspHead.isWorkflow()) {
			financeSuspHead.setBefImage(befFinanceSuspHead);
		}

		return auditDetail;
	}

}