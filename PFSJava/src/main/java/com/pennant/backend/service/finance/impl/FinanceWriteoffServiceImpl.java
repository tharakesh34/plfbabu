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
 *
 * FileName : FinanceWriteoffServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 *
 * 
 * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
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
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class FinanceWriteoffServiceImpl extends GenericFinanceDetailService implements FinanceWriteoffService {

	private static final Logger logger = LogManager.getLogger(FinanceWriteoffServiceImpl.class);

	private FinanceWriteoffDAO financeWriteoffDAO;
	private ProvisionDAO provisionDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private ManualAdviseDAO manualAdviseDAO;

	public FinanceWriteoffServiceImpl() {
		super();
	}

	@Override
	public FinanceWriteoffHeader getFinanceWriteoffDetailById(long finID, String type, String userRole,
			String procEdtEvent) {
		logger.debug(Literal.LEAVING);

		FinanceWriteoffHeader writeoffHeader = new FinanceWriteoffHeader();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		if (fm == null) {
			return writeoffHeader;
		}

		String finReference = fm.getFinReference();
		String finType = fm.getFinType();
		String promotionCode = fm.getPromotionCode();
		long custID = fm.getCustID();

		writeoffHeader.setFinReference(finReference);
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		writeoffHeader.setFinanceDetail(financeDetail);
		scheduleData.setFinanceMain(fm);

		// Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		scheduleData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false));

		scheduleData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finID, FinServiceEvent.WRITEOFF, false, ""));

		if (StringUtils.isNotBlank(promotionCode)) {
			financeDetail.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(promotionCode, FinServiceEvent.WRITEOFF,
					"_AView", false, FinanceConstants.MODULEID_PROMOTION));
		} else {
			financeDetail.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(finType, FinServiceEvent.WRITEOFF,
					"_AView", false, FinanceConstants.MODULEID_FINTYPE));
		}

		scheduleData.setRepayDetails(financeRepaymentsDAO.getFinRepayListByFinRef(finID, false, ""));
		scheduleData.setPenaltyDetails(recoveryDAO.getFinancePenaltysByFinRef(finID, ""));
		scheduleData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));

		// Finance Customer Details

		if (custID != 0 && custID != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}

		// Finance Fee Details
		scheduleData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_TView"));

		// Finance Check List Details
		// =======================================
		checkListDetailService.setFinanceCheckListDetails(financeDetail, finType, procEdtEvent, userRole);

		// Finance Fee Charge Details
		// =======================================
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));
		if (!accSetIdList.isEmpty()) {
			financeDetail.setFeeCharges(
					transactionEntryDAO.getListFeeChargeRules(accSetIdList, AccountingEvent.WRITEOFF, "_AView", 0));
		}

		// Finance Stage Accounting Posting Details
		// =======================================
		financeDetail.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Docuument Details
		financeDetail.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View"));

		if (StringUtils.isNotBlank(fm.getRecordType())) {

			// Finance Writeoff Details
			writeoffHeader.setFinanceWriteoff(financeWriteoffDAO.getFinanceWriteoffById(finID, "_Temp"));

		} else {

			fm.setNewRecord(true);

			// Finance Writeoff Details
			FinanceWriteoff financeWriteoff = financeScheduleDetailDAO.getWriteoffTotals(finID);
			FinanceProfitDetail detail = profitDetailsDAO.getProfitDetailForWriteOff(finID);

			if (detail != null) {
				financeWriteoff.setCurODPri(detail.getODPrincipal());
				financeWriteoff.setCurODPft(detail.getODProfit());
				financeWriteoff.setPenaltyAmount(detail.getPenaltyDue());
			}

			if (SysParamUtil.getAppDate().compareTo(detail.getMaturityDate()) < 0) {
				financeWriteoff.setUnPaidSchdPft(detail.getPftAccrued());
			}

			Provision provision = provisionDAO.getProvisionByFinId(finReference, TableType.MAIN_TAB, false);
			if (provision != null) {
				financeWriteoff.setProvisionedAmount(provision.getProvisionedAmt());
			}

			writeoffHeader.setFinanceWriteoff(financeWriteoff);
		}

		logger.debug(Literal.LEAVING);
		return writeoffHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = header.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date curBDay = SysParamUtil.getAppDate();

		long serviceUID = Long.MIN_VALUE;
		if (schdData.getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(fm.getFinReference());
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

		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		fm.setRcdMaintainSts(FinServiceEvent.WRITEOFF);
		if (tableType == TableType.MAIN_TAB) {
			fm.setRcdMaintainSts("");
		}

		// Repayments Postings Details Process Execution
		long linkedTranId = 0;
		FinanceProfitDetail profitDetail = null;

		if (!fm.isWorkflow()) {
			profitDetail = profitDetailsDAO.getFinProfitDetailsById(finID);

			AEEvent aeEvent = AEAmounts.procAEAmounts(fm, schedules, profitDetail, AccountingEvent.WRITEOFF, curBDay,
					fm.getMaturityDate());

			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
			aeEvent.setDataMap(dataMap);
			try {
				aeEvent = postingsPreparationUtil.processPostingDetails(aeEvent);
			} catch (AccountNotFoundException e) {
				logger.error(Literal.EXCEPTION, e);
			}

			if (!aeEvent.isPostingSucess()) {
				String errParm = aeEvent.getErrorMessage();
				throw new InterfaceException("9999", errParm);
			}

			linkedTranId = aeEvent.getLinkedTranId();
		}

		FinanceWriteoff writeoff = header.getFinanceWriteoff();
		writeoff.setLinkedTranId(linkedTranId);

		// Finance Main Details Save And Update
		// =======================================
		if (fm.isNewRecord()) {

			getFinanceMainDAO().save(fm, tableType, false);

			int seqNo = financeWriteoffDAO.getMaxFinanceWriteoffSeq(finID, writeoff.getWriteoffDate(), "");
			writeoff.setSeqNo(seqNo + 1);
			financeWriteoffDAO.save(writeoff, tableType.getSuffix());

		} else {
			financeMainDAO.update(fm, tableType, false);
			financeWriteoffDAO.update(writeoff, tableType.getSuffix());
		}

		if (!fm.isNewRecord()) {
			listDeletion(finID, tableType.getSuffix());
			listSave(schdData, tableType.getSuffix(), 0);
		} else {
			listDeletion(finID, tableType.getSuffix());
			listSave(schdData, tableType.getSuffix(), 0);
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType.getSuffix(), fm, fd.getModuleDefiner(),
					serviceUID);
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType.getSuffix(), serviceUID));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public void listDeletion(long finID, String tableType) {
		financeScheduleDetailDAO.deleteByFinReference(finID, tableType, false, 0);
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long finID) {
		return financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = PennantConstants.TRAN_DEL;

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = header.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(finReference, fd.getModuleDefiner());

		financeWriteoffDAO.delete(finID, "_Temp");

		// Save Document Details
		List<DocumentDetails> documents = fd.getDocumentDetailsList();
		if (documents != null && documents.size() > 0) {
			for (DocumentDetails document : documents) {
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", fm, fd.getModuleDefiner(), serviceUID);
			auditHeader.setAuditDetails(details);
		}

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		auditHeader.getAuditDetails().addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		// ScheduleDetails deletion
		listDeletion(finID, "_Temp");
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(header);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		Date appDate = SysParamUtil.getAppDate();
		Date curBDay = appDate;

		FinanceDetail fd = header.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		long serviceUID = Long.MIN_VALUE;

		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Fetch Next Payment Details from Finance for Salaried Postings Verification
		FinanceScheduleDetail orgNextSchd = financeScheduleDetailDAO.getNextSchPayment(finID, curBDay);

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		fm.setRcdMaintainSts("");
		fm.setRoleCode("");
		fm.setNextRoleCode("");
		fm.setTaskId("");
		fm.setNextTaskId("");
		fm.setWorkflowId(0);

		executeAccountingProcess(aAuditHeader);

		tranType = PennantConstants.TRAN_UPD;
		fm.setRecordType("");
		fm.setFinIsActive(true);
		financeMainDAO.updateWriteOffStatus(finID, true);
		profitDetailsDAO.updateClosingSts(finID, true);

		// Save Finance WriteOff Details
		FinanceWriteoff financeWriteoff = header.getFinanceWriteoff();
		// financeWriteoff.setLinkedTranId(aeEvent.getLinkedTranId());
		financeWriteoffDAO.save(financeWriteoff, "");

		listDeletion(finID, "");
		listSave(schdData, "", 0);

		// Save Fee Charges List
		// =======================================
		saveFeeChargeList(schdData, fd.getModuleDefiner(), false, "");

		List<DocumentDetails> docuemnts = fd.getDocumentDetailsList();
		if (docuemnts != null && docuemnts.size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", fm, fd.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
			listDocDeletion(header.getFinanceDetail(), "_Temp");
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
		}

		// Update Profit Details
		// getProfitDetailsDAO().update(profitDetail, false);

		// Schedule Details delete
		// =======================================
		listDeletion(finID, "_Temp");

		// Fee charges deletion
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		tempAuditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		// Finance Writeoff Details
		financeWriteoffDAO.delete(finID, "_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);

		// Adding audit as deleted from TEMP table
		String auditTranType = aAuditHeader.getAuditTranType();

		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(tempAuditDetailList);
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.getAuditDetail().setModelData(header);

		saveFinSalPayment(schdData, orgNextSchd, true);
		finStageAccountingLogDAO.update(finID, fd.getModuleDefiner(), false);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader executeAccountingProcess(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		FinanceWriteoffHeader writeoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceWriteoff writeoff = writeoffHeader.getFinanceWriteoff();
		FinanceDetail fd = writeoffHeader.getFinanceDetail();
		fd.setAccountingEventCode(AccountingEvent.WRITEOFF);
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		String finReference = fm.getFinReference();
		long finID = fm.getFinID();

		AEEvent aeEvent = new AEEvent();
		FinanceProfitDetail pftDetail = new FinanceProfitDetail();

		pftDetail = profitDetailsDAO.getFinProfitDetailsById(finID);

		try {
			aeEvent = prepareAccountingData(fd, aeEvent, pftDetail);
			aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		Map<String, Object> dataMap = aeEvent.getDataMap();

		amountCodes.setWriteOff(true);
		aeEvent.getAeAmountCodes().setTotalWriteoff(
				writeoff.getWriteoffPrincipal().add(writeoff.getWriteoffProfit().add(writeoff.getWrittenoffSchFee())));
		dataMap = prepareFeeRulesMap(amountCodes, dataMap, fd);
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		writeoff.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		postingsPreparationUtil.postAccounting(aeEvent);
		writeoff.setLinkedTranId(aeEvent.getLinkedTranId());

		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {
			// save Postings
			if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
				getPostingsDAO().saveBatch(accountingSetEntries);
			}

			// Save/Update Finance Profit Details
			boolean isNew = false;

			if (StringUtils.equals(fm.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				isNew = true;
			}

			doSave_PftDetails(pftDetail, isNew);

			// Account Details Update
			if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
				accountProcessUtil.procAccountUpdate(accountingSetEntries);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AEEvent prepareAccountingData(FinanceDetail fd, AEEvent aeEvent, FinanceProfitDetail pd) {
		Date curBDay = SysParamUtil.getAppDate();
		String eventCode = fd.getAccountingEventCode();

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		String promotionCode = fm.getPromotionCode();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(fm.getFinStartDate());
		}

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();

		if (pd != null) {
			BeanUtils.copyProperties(pd, newProfitDetail);
			totalPftSchdOld = pd.getTotalPftSchd();
			totalPftCpzOld = pd.getTotalPftCpz();
		}

		aeEvent = AEAmounts.procAEAmounts(fm, schedules, pd, eventCode, curBDay, curBDay);

		if (StringUtils.isNotBlank(promotionCode)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(promotionCode, eventCode,
					FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(fm, schedules, newProfitDetail, curBDay);
		if (!FinanceConstants.BPI_NO.equals(fm.getBpiTreatment())) {
			amountCodes.setBpi(fm.getBpiAmount());
		}

		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(FinServiceEvent.ORG);
		amountCodes.setDisburse(fm.getFinCurrAssetValue());

		if (fm.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
			aeEvent.setNewRecord(true);
		}
		// setting entity code
		aeEvent.setEntityCode(fm.getEntityCode());
		return aeEvent;
	}

	public void listSave(FinScheduleData schdData, String tableType, long logKey) {
		logger.debug(Literal.ENTERING);

		Map<Date, Integer> mapDateSeq = new HashMap<>();

		FinanceMain fm = schdData.getFinanceMain();
		long lastMntBy = fm.getLastMntBy();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchd : schedules) {

			curSchd.setLastMntBy(lastMntBy);
			curSchd.setFinID(fm.getFinID());
			curSchd.setFinReference(fm.getFinReference());

			int seqNo = 0;

			if (mapDateSeq.containsKey(curSchd.getSchDate())) {
				seqNo = mapDateSeq.get(curSchd.getSchDate());
				mapDateSeq.remove(curSchd.getSchDate());
			}

			seqNo = seqNo + 1;
			mapDateSeq.put(curSchd.getSchDate(), seqNo);
			curSchd.setSchSeq(seqNo);
			curSchd.setLogKey(logKey);
		}

		financeScheduleDetailDAO.saveList(schedules, tableType, false);

		logger.debug(Literal.LEAVING);
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

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditDetail.getModelData();

		FinanceDetail fd = header.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		FinanceMain tempFinanceMain = null;

		if (fm.isWorkflow()) {
			tempFinanceMain = financeMainDAO.getFinanceMainById(finID, "_Temp", false);
		}

		FinanceMain befFinanceMain = financeMainDAO.getFinanceMainById(finID, "", false);
		FinanceMain oldFinanceMain = fm.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = fm.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (fm.isNewRecord()) { // for New record or new record into work flow

			if (!fm.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!fm.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null
							&& !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
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

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(fm.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !fm.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceWriteoffHeader writeoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();

		FinanceDetail fd = writeoffHeader.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (fm.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Document Details
		List<DocumentDetails> documents = fd.getDocumentDetailsList();
		if (documents != null && documents.size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(fd, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		List<FinanceCheckListReference> financeCheckList = fd.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (StringUtils.isNotEmpty(fm.getRecordType())
					&& PennantConstants.RECORD_TYPE_DEL.equals(fm.getRecordType())) {
				tableType = "";
			}

			String finReference = schdData.getFinReference();
			financeCheckList = checkListDetailService.getCheckListByFinRef(finReference, tableType);
			fd.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		}

		fd.setAuditDetailMap(auditDetailMap);
		writeoffHeader.setFinanceDetail(fd);
		auditHeader.getAuditDetail().setModelData(writeoffHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public int getMaxFinanceWriteoffSeq(long finID, Date writeoffDate, String string) {
		return financeWriteoffDAO.getMaxFinanceWriteoffSeq(finID, writeoffDate, string);
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(long finID, int adviseType, String type) {
		return this.manualAdviseDAO.getManualAdviseByRef(finID, adviseType, type);
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

}
