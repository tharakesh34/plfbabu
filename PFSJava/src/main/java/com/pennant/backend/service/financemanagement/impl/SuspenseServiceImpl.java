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
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
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
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
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
	public FinanceSuspHead getFinanceSuspHeadById(long finID, boolean isEnquiry, String userRole, String procEdtEvent) {
		FinanceSuspHead suspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finID, "_View");
		if (suspHead == null) {
			return null;
		}

		if (isEnquiry) {
			suspHead.setSuspDetailsList(financeSuspHeadDAO.getFinanceSuspDetailsListById(finID));
			suspHead.setSuspPostingsList(postingsDAO.getPostingsByFinRefAndEvent(suspHead.getFinReference(),
					"'PIS_NORM','NORM_PIS'", true, "", "_View"));
		}

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_AView", false);

		if (fm == null) {
			return suspHead;
		}

		String finReference = fm.getFinReference();
		long custID = fm.getCustID();

		schdData.setFinanceMain(fm);

		if (StringUtils.isNotBlank(suspHead.getRecordType())) {
			fm.setNewRecord(false);
		}

		if (StringUtils.isNotEmpty(suspHead.getRecordType())) {
			fm.setNewRecord(false);
		}

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false));
		schdData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finID, procEdtEvent, false, ""));
		schdData.setRepayDetails(financeRepaymentsDAO.getFinRepayListByFinRef(finID, false, ""));
		schdData.setPenaltyDetails(recoveryDAO.getFinancePenaltysByFinRef(finID, ""));
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView"));

		// Finance Customer Details
		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}

		String finType = schdData.getFinanceType().getFinType();

		// Finance Check List Details
		// =======================================
		checkListDetailService.setFinanceCheckListDetails(fd, finType, procEdtEvent, userRole);

		// Finance Fee Charge Details
		// =======================================
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));
		if (!accSetIdList.isEmpty()) {
			fd.setFeeCharges(
					transactionEntryDAO.getListFeeChargeRules(accSetIdList, AccountingEvent.NORM_PIS, "_AView", 0));
		}

		// Finance Stage Accounting Posting Details
		// =======================================
		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Docuument Details
		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				procEdtEvent, "_View"));

		suspHead.setFinanceDetail(fd);
		return suspHead;
	}

	@Override
	public List<Long> getSuspFinanceList() {
		return financeSuspHeadDAO.getSuspFinanceList();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) throws AppException {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tableType = "";
		long serviceUID = Long.MIN_VALUE;

		FinanceSuspHead suspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = suspHead.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date appDate = SysParamUtil.getAppDate();

		if (schdData.getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(finID);
			finServInst.setFinReference(finReference);
			finServInst.setFinEvent(fd.getModuleDefiner());

			schdData.setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction finSerList : schdData.getFinServiceInstructions()) {
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

		if (suspHead.isWorkflow()) {
			tableType = "_Temp";
		}

		FinanceProfitDetail profitDetail = null;
		if (!suspHead.isWorkflow()) {
			profitDetail = profitDetailsDAO.getFinProfitDetailsById(finID);

			AEEvent aeEvent = AEAmounts.procAEAmounts(fm, schdData.getFinanceScheduleDetails(), profitDetail,
					AccountingEvent.WRITEOFF, appDate, fm.getMaturityDate());
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

		if (suspHead.isManualSusp()) {
			suspHead.setFinIsInSusp(true);
			suspHead.setFinSuspTrfDate(suspHead.getFinSuspDate());
		}

		if (suspHead.isNewRecord()) {
			financeSuspHeadDAO.save(suspHead, tableType);
			auditHeader.getAuditDetail().setModelData(suspHead);
			auditHeader.setAuditReference(suspHead.getFinReference());
		} else {
			financeSuspHeadDAO.update(suspHead, tableType);
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType, schdData.getFinanceMain(),
					fd.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType, serviceUID));
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		financeSuspHeadDAO.delete(financeSuspHead, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws AppException {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceSuspHead suspHead = new FinanceSuspHead();
		BeanUtils.copyProperties((FinanceSuspHead) auditHeader.getAuditDetail().getModelData(), suspHead);

		FinanceDetail fd = suspHead.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = suspHead.getFinID();
		String finReference = suspHead.getFinReference();
		Date appData = SysParamUtil.getAppDate();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		if (suspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			financeSuspHeadDAO.delete(suspHead, "");
		} else {
			suspHead.setRoleCode("");
			suspHead.setNextRoleCode("");
			suspHead.setTaskId("");
			suspHead.setNextTaskId("");
			suspHead.setWorkflowId(0);

			if (suspHead.isManualSusp()) {
				suspHead.setFinIsInSusp(true);
				suspHead.setFinSuspTrfDate(suspHead.getFinSuspDate());
			}

			if (suspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				suspHead.setRecordType("");
				financeSuspHeadDAO.save(suspHead, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				suspHead.setRecordType("");
				financeSuspHeadDAO.update(suspHead, "");
			}
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", fm, fd.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
		}

		// Finance Profit Details Updation
		FinanceProfitDetail finPftDetail = profitDetailsDAO.getFinProfitDetailsById(finID);

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finID, "", false);
		List<FinanceScheduleDetail> scheduleDetailList = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);

		// Commitment Set Non-Performing Status
		if (StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {
			if (suspHead.isManualSusp() || suspHead.isFinIsInSusp()) {
				commitmentDAO.updateNonPerformStatus(financeMain.getFinCommitmentRef());
			}
		}

		// Document Details delete
		// =======================================
		listDocDeletion(fd, "_Temp");

		// Checklist Details delete
		// =======================================
		checkListDetailService.delete(fd, "_Temp", tranType);

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		finPftDetail = accrualService.calProfitDetails(financeMain, scheduleDetailList, finPftDetail, appData);

		String worstSts = customerStatusCodeDAO.getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);
		profitDetailsDAO.update(finPftDetail, false);

		financeSuspHeadDAO.delete(suspHead, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(suspHead);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		// updating the processed with 1 in finstageAccountingLog
		finStageAccountingLogDAO.update(finID, fd.getModuleDefiner(), false);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	// Document Details List Maintenance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		documentDetailsDAO.deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws AppException {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceSuspHead suspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = suspHead.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		String tranType = PennantConstants.TRAN_DEL;

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(finID, FinServiceEvent.SUSPHEAD);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		financeSuspHeadDAO.delete(suspHead, "_Temp");

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", fm, fd.getModuleDefiner(), serviceUID);
			auditHeader.setAuditDetails(details);
			listDocDeletion(fd, "_Temp");
		}

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		checkListDetailService.delete(fd, "_Temp", tranType);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceSuspHead suspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = suspHead.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (fm.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(fd, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// Finance Check List Details
		// =======================================
		List<FinanceCheckListReference> financeCheckList = fd.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (schdData.getFinanceMain().getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			long finID = fm.getFinID();
			financeCheckList = checkListDetailService.getCheckListByFinRef(finID, tableType);
			fd.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		}

		fd.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(suspHead);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceSuspHead suspHead = (FinanceSuspHead) auditDetail.getModelData();

		long finID = suspHead.getFinID();
		suspHead.getFinReference();

		FinanceSuspHead tempFinanceSuspHead = null;
		if (suspHead.isWorkflow()) {
			tempFinanceSuspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finID, "_Temp");
		}
		FinanceSuspHead befFinanceSuspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finID, "");
		FinanceSuspHead oldFinanceSuspHead = suspHead.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = suspHead.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (suspHead.isNewRecord()) { // for New record or new record into work flow

			if (!suspHead.isWorkflow()) {// With out Work flow only new records
				if (befFinanceSuspHead != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (suspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
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
			if (!suspHead.isWorkflow()) { // With out Work flow for update and delete

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

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !suspHead.isWorkflow()) {
			suspHead.setBefImage(befFinanceSuspHead);
		}

		return auditDetail;
	}

}