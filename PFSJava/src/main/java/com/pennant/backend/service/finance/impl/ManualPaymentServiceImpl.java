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
 * FileName : ManualPaymentServiceImpl.java *
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ManualPaymentServiceImpl extends GenericFinanceDetailService implements ManualPaymentService {
	private static final Logger logger = LogManager.getLogger(ManualPaymentServiceImpl.class);

	private FinanceRepayPriorityDAO financeRepayPriorityDAO;
	private LimitCheckDetails limitCheckDetails;
	private RuleService ruleService;
	private FinanceDetailService financeDetailService;
	private RepayCalculator repayCalculator;
	private LimitManagement limitManagement;
	private FinTypeFeesDAO finTypeFeesDAO;

	public ManualPaymentServiceImpl() {
		super();
	}

	@Override
	public RepayData getRepayDataById(long finID, String eventCode, String procEdtEvent, String userRole) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);

		FinanceDetail fd = new FinanceDetail();

		RepayData repayData = new RepayData();
		repayData.setFinanceDetail(fd);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return repayData;
		}

		String finReference = fm.getFinReference();
		String finType = fm.getFinType();
		String promotionCode = fm.getPromotionCode();
		long custID = fm.getCustID();

		repayData.setFinReference(finReference);

		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "_View", false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "_View", false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, "_View", false));
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));

		if (StringUtils.isNotBlank(promotionCode)) {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(promotionCode, AccountingEvent.EARLYSTL, "_AView",
					false, FinanceConstants.MODULEID_PROMOTION));
		} else {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(finType, AccountingEvent.EARLYSTL, "_AView", false,
					FinanceConstants.MODULEID_FINTYPE));
		}

		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_TView"));

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}

		checkListDetailService.setFinanceCheckListDetails(fd, finType, procEdtEvent, userRole);

		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		if (StringUtils.isNotBlank(fm.getRecordType())) {
			repayData.setFinRepayHeader(financeRepaymentsDAO.getFinRepayHeader(finID, "_Temp"));
			repayData.setRepayScheduleDetails(financeRepaymentsDAO.getRpySchdList(finID, "_Temp"));
			schdData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finID, procEdtEvent, false, "_Temp"));

			fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference,
					FinanceConstants.MODULE_NAME, procEdtEvent, "_Temp"));

		} else {
			repayData.setFinRepayHeader(null);
			repayData.setRepayScheduleDetails(null);

			List<Long> accSetIdList = new ArrayList<Long>();
			Long accSetId = returnAccountingSetid(eventCode, finType);
			if (accSetId != Long.MIN_VALUE) {
				accSetIdList.add(Long.valueOf(accSetId));
			}

			accSetIdList
					.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));
			if (!accSetIdList.isEmpty()) {
				fd.setFeeCharges(transactionEntryDAO.getListFeeChargeRules(accSetIdList, eventCode, "_AView", 0));
			}

		}

		logger.debug(Literal.LEAVING);
		return repayData;
	}

	private Long returnAccountingSetid(String eventCode, String finType) {
		Long accountingSetId = Long.MIN_VALUE;

		String event = null;
		int moduleId = FinanceConstants.MODULEID_FINTYPE;

		if (AccountingEvent.EARLYSTL.equals(eventCode)) {
			event = AccountingEvent.EARLYSTL;
		} else if (AccountingEvent.EARLYPAY.equals(eventCode)) {
			event = AccountingEvent.EARLYPAY;
		} else if (AccountingEvent.REPAY.equals(eventCode)) {
			event = AccountingEvent.REPAY;
		}

		accountingSetId = finTypeAccountingDAO.getAccountSetID(finType, event, moduleId);

		return accountingSetId;
	}

	@Override
	public FinanceDetail getAccountingDetail(FinanceDetail fd, String eventCodeRef) {
		logger.debug(Literal.ENTERING);

		String commitmentRef = fd.getFinScheduleData().getFinanceMain().getFinCommitmentRef();

		if (StringUtils.isNotEmpty(commitmentRef)) {
			logger.debug(Literal.LEAVING);
			return fd;
		}

		Commitment commitment = commitmentDAO.getCommitmentById(commitmentRef, "");
		if (commitment != null && commitment.isRevolving()) {
			long accSetId = accountingSetDAO.getAccountingSetId(AccountingEvent.CMTRPY, AccountingEvent.CMTRPY);
			if (accSetId != 0) {
				fd.setCmtFinanceEntries(transactionEntryDAO.getListTransactionEntryById(accSetId, "_AEView", true));
			}
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(long finID) {
		return profitDetailsDAO.getPftDetailForEarlyStlReport(finID);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = repayData.getFinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinRepayHeader rph = repayData.getFinRepayHeader();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date appDate = SysParamUtil.getAppDate();

		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceMain(fm);
		postingDTO.setFinanceDetail(fd);
		postingDTO.setValueDate(appDate);
		postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

		AccountingEngine.post(AccountingEvent.STAGE, postingDTO);

		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		long serviceUID = Long.MIN_VALUE;

		if (schdData.getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(finID);
			finServInst.setFinReference(finReference);
			finServInst.setFinEvent(fd.getModuleDefiner());
			fd.getFinScheduleData().setFinServiceInstruction(finServInst);
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
		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		fm.setRcdMaintainSts(rph.getFinEvent());
		if (tableType == TableType.MAIN_TAB) {
			fm.setRcdMaintainSts("");
		}

		// Repayments Postings Details Process Execution
		long linkedTranId = 0;
		boolean partialPay = false;
		FinanceProfitDetail profitDetail = null;
		AEAmountCodes aeAmountCodes = null;

		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();

		boolean emptyRepayInstructions = schdData.getRepayInstructions() == null ? true : false;

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		if (!fm.isWorkflow()) {
			profitDetail = profitDetailsDAO.getFinProfitDetailsById(finID);
			List<RepayScheduleDetail> rsdList = repayData.getRepayScheduleDetails();
			List<Object> returnList = processRepaymentPostings(fm, schedules, profitDetail, rsdList,
					repayData.getEventCodeRef(), schdData.getFeeRules(), schdData.getFinanceType().getFinDivision());

			if (!(Boolean) returnList.get(0)) {
				String errParm = (String) returnList.get(1);
				throw new InterfaceException("9999", errParm);
			}

			linkedTranId = (Long) returnList.get(1);
			partialPay = (Boolean) returnList.get(2);
			aeAmountCodes = (AEAmountCodes) returnList.get(3);
			finRepayQueues = (List<FinRepayQueue>) returnList.get(5);
		}

		// Finance Main Details Save And Update
		// =======================================
		if (fm.isNewRecord()) {
			financeMainDAO.save(fm, tableType, false);

			// Save FinRepayHeader Details
			rph.setLinkedTranId(linkedTranId);
			financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

			// Save Repay Schedule Details
			financeRepaymentsDAO.saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);

		} else {
			financeMainDAO.update(fm, tableType, false);

			// Save/Update FinRepayHeader Details depends on Workflow
			if (tableType == TableType.TEMP_TAB) {
				rph.setLinkedTranId(linkedTranId);
				financeRepaymentsDAO.updateFinRepayHeader(rph, tableType.getSuffix());
				financeRepaymentsDAO.deleteRpySchdList(finID, tableType.getSuffix());
				financeRepaymentsDAO.saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
			}
		}

		// Save schedule details
		// =======================================
		if (!fm.isNewRecord()) {

			if (tableType == TableType.MAIN_TAB && fm.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				// Fetch Existing data before Modification

				FinScheduleData oldFinSchdData = null;
				if (rph.isSchdRegenerated()) {
					oldFinSchdData = getFinSchDataByFinRef(finID, "");

					oldFinSchdData.setFinanceMain(fm);

					oldFinSchdData.setFinID(finID);
					oldFinSchdData.setFinReference(finReference);
				}

				// Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinID(finID);
				entryDetail.setFinReference(finReference);
				entryDetail.setEventAction(rph.getFinEvent());
				entryDetail.setSchdlRecal(rph.isSchdRegenerated());
				entryDetail.setPostDate(appDate);
				entryDetail.setReversalCompleted(false);
				long logKey = finLogEntryDetailDAO.save(entryDetail);

				// Save Schedule Details For Future Modifications
				if (rph.isSchdRegenerated()) {
					listSave(oldFinSchdData, "_Log", logKey);
				}
			}
		}

		// Finance Schedule Details
		listDeletion(finID, tableType.getSuffix(), emptyRepayInstructions);
		listSave(schdData, tableType.getSuffix(), 0);

		// Fee Charge Details Clearing before
		if (tableType == TableType.TEMP_TAB) {
			finFeeChargesDAO.deleteChargesBatch(finID, rph.getFinEvent(), false, tableType.getSuffix());
		}

		saveFeeChargeList(repayData, repayData.getFinRepayHeader().getFinEvent(), tableType.getSuffix());

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			auditDetails.addAll(
					processingDocumentDetailsList(details, tableType.getSuffix(), fm, rph.getFinEvent(), serviceUID));
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType.getSuffix(), serviceUID));
		}

		// Process Updations For Postings
		if (!fm.isWorkflow()) {
			repaymentPostingsUtil.UpdateScreenPaymentsProcess(fm, schedules, profitDetail, finRepayQueues, linkedTranId,
					partialPay, aeAmountCodes);

			financeRepaymentsDAO.saveFinRepayHeader(rph, tableType);

			// Update Linked Transaction ID after Repayments Postings Process if workflow not found
			for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
				rpySchd.setLinkedTranId(linkedTranId);
			}
			financeRepaymentsDAO.saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public void listDeletion(long finID, String tableType, boolean emptyRepayInstructions) {
		logger.debug(Literal.ENTERING);

		financeScheduleDetailDAO.deleteByFinReference(finID, tableType, false, 0);
		if (!emptyRepayInstructions) {
			repayInstructionDAO.deleteByFinReference(finID, tableType, false, 0);
		}

		logger.debug(Literal.LEAVING);
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

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = repayData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		long serviceUID = Long.MIN_VALUE;

		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		FinRepayHeader rph = repayData.getFinRepayHeader();
		AccountingEngine.cancelStageAccounting(finID, rph.getFinEvent());

		listDeletion(finID, "_Temp", false);
		finFeeChargesDAO.deleteChargesBatch(finID, rph.getFinEvent(), false, "_Temp");
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);

		financeRepaymentsDAO.deleteFinRepayHeader(rph, "_Temp");
		financeRepaymentsDAO.deleteRpySchdList(finID, "_Temp");

		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", fm, rph.getFinEvent(), serviceUID);
			auditHeader.setAuditDetails(details);
		}

		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		auditHeader.getAuditDetails().addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.getAuditDetail().setModelData(repayData);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws AppException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		Date appDate = SysParamUtil.getAppDate();

		FinanceDetail fd = repayData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceMain(fm);
		postingDTO.setFinanceDetail(fd);
		postingDTO.setValueDate(appDate);
		postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

		AccountingEngine.post(AccountingEvent.STAGE, postingDTO);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		long linkedTranId = 0;
		boolean partialPay = false;
		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		FinRepayHeader rph = repayData.getFinRepayHeader();
		AEAmountCodes aeAmountCodes = null;

		fm.setRcdMaintainSts("");
		fm.setRoleCode("");
		fm.setNextRoleCode("");
		fm.setTaskId("");
		fm.setNextTaskId("");
		fm.setWorkflowId(0);

		long serviceUID = Long.MIN_VALUE;

		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		boolean emptyRepayInstructions = schdData.getRepayInstructions() == null ? true : false;

		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finID);

		List<RepayScheduleDetail> rsdList = repayData.getRepayScheduleDetails();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		String finDivision = schdData.getFinanceType().getFinDivision();
		List<Object> returnList = processRepaymentPostings(fm, schedules, profitDetail, rsdList,
				repayData.getEventCodeRef(), schdData.getFeeRules(), finDivision);

		if (!(Boolean) returnList.get(0)) {
			String errParm = (String) returnList.get(1);
			throw new InterfaceException("9999", errParm);
		}

		linkedTranId = (Long) returnList.get(1);
		partialPay = (Boolean) returnList.get(2);
		aeAmountCodes = (AEAmountCodes) returnList.get(3);
		finRepayQueues = (List<FinRepayQueue>) returnList.get(5);

		tranType = PennantConstants.TRAN_UPD;
		fm.setRecordType("");

		FinScheduleData oldFinSchdData = null;
		if (rph.isSchdRegenerated()) {
			oldFinSchdData = getFinSchDataByFinRef(finID, "");
			oldFinSchdData.setFinanceMain(fm);

			oldFinSchdData.setFinID(finID);
			oldFinSchdData.setFinReference(finReference);
		}

		// Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinID(finID);
		entryDetail.setFinReference(finReference);
		entryDetail.setEventAction(rph.getFinEvent());
		entryDetail.setSchdlRecal(rph.isSchdRegenerated());
		entryDetail.setPostDate(appDate);
		entryDetail.setReversalCompleted(false);
		long logKey = finLogEntryDetailDAO.save(entryDetail);

		// Save Schedule Details For Future Modifications
		if (rph.isSchdRegenerated()) {
			listSave(oldFinSchdData, "_Log", logKey);
		}

		// Repayment Postings Details Process
		returnList = repaymentPostingsUtil.UpdateScreenPaymentsProcess(fm, schedules, profitDetail, finRepayQueues,
				linkedTranId, partialPay, aeAmountCodes);

		// Finance Main Updation
		// =======================================
		fm = (FinanceMain) returnList.get(3);
		financeMainDAO.update(fm, TableType.MAIN_TAB, false);

		// ScheduleDetails delete and save
		// =======================================
		listDeletion(finID, "", emptyRepayInstructions);
		schdData.setFinanceScheduleDetails((List<FinanceScheduleDetail>) returnList.get(4));
		listSave(schdData, "", 0);

		// Save Fee Charges List
		// =======================================
		saveFeeChargeList(repayData, repayData.getFinRepayHeader().getFinEvent(), "");

		// Save Finance Repay Header Details
		rph.setLinkedTranId(linkedTranId);
		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		// Update Linked Transaction ID after Repayment Postings Process if workflow not found
		for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
			rpySchd.setLinkedTranId(linkedTranId);
			rpySchd.setFinID(fd.getFinScheduleData().getFinID());
			rpySchd.setFinReference(fd.getFinScheduleData().getFinReference());
		}
		financeRepaymentsDAO.saveRpySchdList(repayData.getRepayScheduleDetails(), TableType.MAIN_TAB);

		if (!StringUtils.equals("API", repayData.getSourceId())) {
			// Save Document Details
			if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "", fd.getFinScheduleData().getFinanceMain(),
						rph.getFinEvent(), serviceUID);
				auditDetails.addAll(details);
				listDocDeletion(fd, "_Temp");
			}

			// set Check list details Audit
			// =======================================
			if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
			}

			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

			// ScheduleDetails delete
			// =======================================
			listDeletion(finID, "_Temp", false);

			// Fee charges deletion
			List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
			finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

			// Checklist Details delete
			// =======================================
			tempAuditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", tranType));

			// Delete Finance Repay Header
			financeRepaymentsDAO.deleteFinRepayHeader(rph, "_Temp");
			financeRepaymentsDAO.deleteRpySchdList(finID, "_Temp");

			// Reset Repay Account ID On Finance Main for Correcting Audit Data
			financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);

			RepayData tempRepayData = (RepayData) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempFm = tempRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			String auditTranType = aAuditHeader.getAuditTranType();
			auditHeader.setAuditDetail(
					new AuditDetail(auditTranType, 1, fields[0], fields[1], tempFm.getBefImage(), tempFm));

			// Adding audit as deleted from TEMP table
			auditHeader.setAuditDetails(tempAuditDetailList);
			auditHeader.setAuditModule("FinanceDetail");
			auditHeaderDAO.addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.setAuditDetail(
					new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

			// Adding audit as Insert/Update/deleted into main table
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditModule("FinanceDetail");
			auditHeaderDAO.addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(repayData);

		// ===========================================
		// Fetch Total Repayment Amount till Maturity date for Early Settlement
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())) {

			// send Collateral DeMark request to Interface
			// ==========================================
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {
					getCollateralAssignmentValidation().saveCollateralMovements(fm.getFinReference());
				}
			} else {
				if (fd.getFinanceCollaterals() != null) {
					// Release the collateral.
				}
			}
		}

		// send Limit Amendment Request to ACP Interface and save log details
		// =======================================

		if (ImplementationConstants.LIMIT_INTERNAL) {

			FinanceDetail finDetails = fd;
			FinRepayHeader header = repayData.getFinRepayHeader();
			BigDecimal priAmt = BigDecimal.ZERO;

			for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
				priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
			}

			limitManagement.processLoanRepay(fm, finDetails.getCustomerDetails().getCustomer(), priAmt);
		} else {
			limitCheckDetails.doProcessLimits(fm, FinanceConstants.AMENDEMENT);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public List<Object> processRepaymentPostings(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> rsdList, String eventCodeRef,
			List<FeeRule> feeRuleList, String finDivision) throws AppException {
		logger.debug(Literal.ENTERING);

		List<Object> returnList = new ArrayList<Object>();
		try {

			Map<String, FeeRule> feeRuleDetailsMap = null;
			if (feeRuleList != null && !feeRuleList.isEmpty()) {

				feeRuleDetailsMap = new HashMap<String, FeeRule>();
				for (FeeRule feeRule : feeRuleList) {
					if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) {
						feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
					}
				}
			}

			String finType = fm.getFinType();
			FinanceRepayPriority repayPriority = financeRepayPriorityDAO.getFinanceRepayPriorityById(finType, "");

			// Check Finance is RIA Finance Type or Not
			BigDecimal totRpyPri = BigDecimal.ZERO;
			BigDecimal totRpyPft = BigDecimal.ZERO;
			BigDecimal totRpyTds = BigDecimal.ZERO;
			BigDecimal totRefund = BigDecimal.ZERO;
			BigDecimal totSchdFee = BigDecimal.ZERO;

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			Map<String, BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
			FinRepayQueue finRepayQueue = null;

			for (RepayScheduleDetail rsd : rsdList) {
				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinID(fm.getFinID());
				finRepayQueue.setFinReference(fm.getFinReference());
				finRepayQueue.setRpyDate(rsd.getSchDate());
				finRepayQueue.setFinRpyFor(rsd.getSchdFor());
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, fm, rsd, repayPriority);

				finRepayQueue.setRefundAmount(rsd.getRefundReq());
				finRepayQueue.setPenaltyPayNow(rsd.getPenaltyPayNow());
				finRepayQueue.setWaivedAmount(rsd.getWaivedAmt());
				finRepayQueue.setPenaltyBal(rsd.getPenaltyAmt().subtract(rsd.getPenaltyPayNow()));
				finRepayQueue.setChargeType(rsd.getChargeType());

				// Total Repayments Calculation for Principal, Profit & Refunds
				totRpyPri = totRpyPri.add(rsd.getPrincipalSchdPayNow());
				totRpyPft = totRpyPft.add(rsd.getProfitSchdPayNow());
				totRpyTds = totRpyTds.add(rsd.getTdsSchdPayNow());
				totRefund = totRefund.add(rsd.getRefundReq());

				// Fee Details
				totSchdFee = totSchdFee.add(rsd.getSchdFeePayNow());

				finRepayQueues.add(finRepayQueue);

			}

			totalsMap.put("totRpyTot", totRpyPri.add(totRpyPft));
			totalsMap.put("totRpyPri", totRpyPri);
			totalsMap.put("totRpyPft", totRpyPft);
			totalsMap.put("totRpyTds", totRpyTds);
			totalsMap.put("totRefund", totRefund);
			// Schedule Early Settlement Insurance Refund

			// Fee Details
			totalsMap.put("schFeePay", totSchdFee);

			// Repayments Process For Schedule Repay List
			returnList = repaymentPostingsUtil.postingsScreenRepayProcess(fm, schedules, profitDetail, finRepayQueues,
					totalsMap, eventCodeRef, feeRuleDetailsMap, finDivision);

			if ((Boolean) returnList.get(0)) {
				returnList.add(finRepayQueues);
			}

		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug(Literal.EXCEPTION);
		return returnList;
	}

	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd, FinanceRepayPriority repayPriority) {
		logger.debug(Literal.ENTERING);

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());

		if (repayPriority != null) {
			finRepayQueue.setFinPriority(repayPriority.getFinPriority());
		} else {
			finRepayQueue.setFinPriority(9999);
		}

		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdTdsPayNow(rsd.getTdsSchdPayNow());
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());

		// Fee Details
		// 1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());

		logger.debug(Literal.LEAVING);
		return finRepayQueue;
	}

	@Override
	public List<FinanceRepayments> getFinRepayList(long finID) {
		return financeRepaymentsDAO.getFinRepayList(finID);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(long finID) {
		return profitDetailsDAO.getFinProfitDetailsById(finID);
	}

	public FinScheduleData getFinSchDataByFinRef(long finID, String type) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = new FinScheduleData();
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	public void listSave(FinScheduleData schdData, String tableType, long logKey) {
		logger.debug(Literal.ENTERING);

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();
		List<RepayInstruction> repayInstructions = schdData.getRepayInstructions();

		Map<Date, Integer> mapDateSeq = new HashMap<>();

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Finance Schedule Details
		for (FinanceScheduleDetail curSchd : schedules) {
			curSchd.setLastMntBy(fm.getLastMntBy());
			curSchd.setFinID(finID);
			curSchd.setFinReference(finReference);
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

		// Schedule Version Updating
		if (StringUtils.isBlank(tableType)) {
			financeMainDAO.updateSchdVersion(fm, false);
		}

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<>();
			Date curBDay = SysParamUtil.getAppDate();
			for (FinanceDisbursement dd : disbursements) {
				dd.setFinID(finID);
				dd.setFinReference(finReference);
				dd.setDisbReqDate(curBDay);
				dd.setDisbIsActive(true);
				dd.setLogKey(logKey);
				dd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				dd.setLastMntBy(schdData.getFinanceMain().getLastMntBy());
			}
			financeDisbursementDAO.saveList(disbursements, tableType, false);

		}

		// Finance Repay Instruction Details

		if (repayInstructions != null) {
			for (RepayInstruction curSchd : repayInstructions) {
				curSchd.setFinID(finID);
				curSchd.setFinReference(finReference);
				curSchd.setLogKey(logKey);
			}
			repayInstructionDAO.saveList(repayInstructions, tableType, false);
		}

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
		RepayData repayData = (RepayData) auditDetail.getModelData();
		FinanceMain fm = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		FinanceMain tempFinanceMain = null;
		if (fm.isWorkflow()) {
			tempFinanceMain = financeMainDAO.getFinanceMainById(fm.getFinID(), "_Temp", false);
		}
		FinanceMain befFinanceMain = financeMainDAO.getFinanceMainById(fm.getFinID(), "", false);
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
		int eodProgressCount = customerQueuingDAO.getProgressCountByCust(fm.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		// Checking For Commitment , Is it In Maintenance Or not
		if (StringUtils.trimToEmpty(fm.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)
				&& "doApprove".equals(method) && StringUtils.isNotEmpty(fm.getFinCommitmentRef())) {

			Commitment tempcommitment = commitmentDAO.getCommitmentById(fm.getFinCommitmentRef(), "_Temp");
			if (tempcommitment != null && tempcommitment.isRevolving()) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "30538", errParm, valueParm), usrLanguage));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !fm.isWorkflow()) {
			fm.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	public void saveFeeChargeList(RepayData repayData, String finEvent, String tableType) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = repayData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String finReference = fm.getFinReference();

		if (CollectionUtils.isEmpty(schdData.getFeeRules())) {
			return;
		}

		for (FeeRule feeRule : schdData.getFeeRules()) {
			feeRule.setFinReference(finReference);
			feeRule.setFinEvent(finEvent);
		}

		finFeeChargesDAO.saveCharges(schdData.getFeeRules(), false, tableType);

		logger.debug(Literal.LEAVING);
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = repayData.getFinanceDetail();
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
			if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
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
		repayData.setFinanceDetail(fd);
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public RepayData doCalcRepayments(RepayData repayData, FinanceDetail fd, FinServiceInstruction finServiceInst) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		String moduleDefiner = finServiceInst.getModuleDefiner();

		repayData.setFinReference(finServiceInst.getFinReference());

		// calculate repayments
		repayData = calculateRepayments(repayData, fd, finServiceInst, false, null);

		if (moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			Cloner cloner = new Cloner();
			List<FinanceScheduleDetail> schedules = cloner.deepClone(schdData.getFinanceScheduleDetails());
			if (finServiceInst.getToDate() != null) {
				schedules = rePrepareScheduleTerms(schedules, finServiceInst.getToDate());
				schdData.setFinanceScheduleDetails(schedules);
			}
		}

		if (repayData != null) {
			repayData.setFinReference(fd.getFinReference());
			repayData.setFinanceDetail(fd);
		}

		logger.debug(Literal.LEAVING);
		return repayData;
	}

	private RepayData calculateRepayments(RepayData repayData, FinanceDetail fd, FinServiceInstruction finServiceInst,
			boolean isReCal, String method) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType ft = schdData.getFinanceType();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		String moduleDefiner = finServiceInst.getModuleDefiner();
		repayData.setBuildProcess("R");
		repayData.getRepayMain().setRepayAmountNow(finServiceInst.getAmount());

		if (moduleDefiner.equals(FinServiceEvent.EARLYRPY) || moduleDefiner.equals(FinServiceEvent.SCHDRPY)) {
			repayData.getRepayMain().setPayApportionment(PennantConstants.List_Select);
		} else {
			repayData.getRepayMain().setPayApportionment(PennantConstants.List_Select);
		}

		SubHeadRule subHeadRule = null;
		String sqlRule = null;

		if (moduleDefiner.equals(FinServiceEvent.EARLYSETTLE) || moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			Rule rule = ruleService.getApprovedRuleById("REFUND", RuleConstants.MODULE_REFUND,
					RuleConstants.EVENT_REFUND);
			if (rule != null) {
				sqlRule = rule.getSQLRule();
			}

			Customer customer = fd.getCustomerDetails().getCustomer();
			if (customer == null) {
				customer = customerDetailsService.getCustomerForPostings(fm.getCustID());
			}
			subHeadRule = new SubHeadRule();

			try {
				BeanUtils.copyProperties(subHeadRule, customer);
				// subHeadRule.setReqFinCcy(financeType.getFinCcy());
				subHeadRule.setReqProduct(ft.getFinCategory());
				subHeadRule.setReqFinType(ft.getFinType());
				subHeadRule.setReqFinPurpose(fm.getFinPurpose());
				subHeadRule.setReqFinDivision(ft.getFinDivision());

				// Profit Details
				subHeadRule.setTOTALPFT(repayData.getRepayMain().getProfit());
				subHeadRule.setTOTALPFTBAL(repayData.getRepayMain().getProfitBalance());

				// Check For Early Settlement Enquiry -- on Selecting Future Date
				BigDecimal accrueValue = financeDetailService.getAccrueAmount(fm.getFinID());
				subHeadRule.setACCRUE(accrueValue);

				// Total Tenure
				int months = DateUtil.getMonthsBetweenInclusive(fm.getMaturityDate(), fm.getFinStartDate());
				subHeadRule.setTenure(months);

			} catch (IllegalAccessException e) {
				logger.error("Exception: ", e);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}
		}

		repayData.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		repayData.getRepayMain().setProfitPayNow(BigDecimal.ZERO);
		repayData = repayCalculator.initiateRepay(repayData, fm, schedules, sqlRule, subHeadRule, isReCal, method,
				finServiceInst.getFromDate(), moduleDefiner);

		// Calculation for Insurance Refund
		if (moduleDefiner.equals(FinServiceEvent.EARLYSETTLE) || moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			int months = DateUtil.getMonthsBetween(fm.getMaturityDate(),
					repayData.getRepayMain().getRefundCalStartDate() == null ? fm.getMaturityDate()
							: repayData.getRepayMain().getRefundCalStartDate());
			subHeadRule.setRemTenure(months);
		}

		logger.debug(Literal.LEAVING);
		return repayData;
	}

	public RepayData setEarlyRepayEffectOnSchedule(RepayData repayData, FinServiceInstruction finServiceInst) {
		logger.debug(Literal.ENTERING);

		// Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail fd = repayData.getFinanceDetail();
		FinanceMain aFm = fd.getFinScheduleData().getFinanceMain();
		FinScheduleData schdData = fd.getFinScheduleData();

		String method = null;
		// Schedule remodifications only when Effective Schedule Method modified
		if (!finServiceInst.getRecalType().equals(CalculationConstants.EARLYPAY_NOEFCT)) {

			method = finServiceInst.getRecalType();

			if (CalculationConstants.EARLYPAY_RECPFI.equals(method)
					|| CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {
				aFm.setPftIntact(true);
			}

			if (repayData.getRepayMain().getEarlyRepayNewSchd() != null) {
				if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setPftOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayAmount(BigDecimal.ZERO);
				}
				schdData.getFinanceScheduleDetails().add(repayData.getRepayMain().getEarlyRepayNewSchd());
			}

			for (FinanceScheduleDetail detail : schdData.getFinanceScheduleDetails()) {
				if (detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) == 0) {
					if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
						detail.setEarlyPaid(detail.getEarlyPaid().add(repayData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					} else {
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						repayData.getRepayMain()
								.setEarlyPayAmount(repayData.getRepayMain().getEarlyPayAmount().add(earlypaidBal));
					}
				}
				if (detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) >= 0) {
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				}
			}

			schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));
			schdData.setFinanceType(repayData.getFinanceDetail().getFinScheduleData().getFinanceType());

			// Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			schdData = ScheduleCalculator.recalEarlyPaySchedule(schdData,
					repayData.getRepayMain().getEarlyPayOnSchDate(), repayData.getRepayMain().getEarlyPayNextSchDate(),
					repayData.getRepayMain().getEarlyPayAmount(), method);

			fd.setFinScheduleData(schdData);
			aFm = schdData.getFinanceMain();
			aFm.setWorkflowId(repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());
			repayData.setFinanceDetail(fd);// Object Setting for Future save purpose
			repayData.setFinanceDetail(fd);

		}

		logger.debug(Literal.LEAVING);
		return repayData;
	}

	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	private List<FinanceScheduleDetail> rePrepareScheduleTerms(List<FinanceScheduleDetail> schedules, Date toDate) {
		logger.debug(Literal.ENTERING);

		Date paidTillTerm = toDate;

		for (FinanceScheduleDetail curSchd : schedules) {
			if (curSchd.getSchDate().compareTo(paidTillTerm) > 0) {
				break;
			}

			curSchd.setSchdPriPaid(curSchd.getPrincipalSchd());
			curSchd.setSchdPftPaid(curSchd.getProfitSchd());

			curSchd.setSchPftPaid(true);
			curSchd.setSchPriPaid(true);
		}

		logger.debug(Literal.LEAVING);
		return schedules;
	}

	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}
}
