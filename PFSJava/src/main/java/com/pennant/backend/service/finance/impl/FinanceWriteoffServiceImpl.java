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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
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
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.npa.service.AssetClassificationService;
import com.rits.cloning.Cloner;

public class FinanceWriteoffServiceImpl extends GenericFinanceDetailService implements FinanceWriteoffService {

	private static final Logger logger = LogManager.getLogger(FinanceWriteoffServiceImpl.class);

	private FinanceWriteoffDAO financeWriteoffDAO;
	private ProvisionDAO provisionDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private AssetClassificationService assetClassificationService;
	private FeeDetailService feeDetailService;

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

		writeoffHeader.setFinID(finID);
		writeoffHeader.setFinReference(finReference);

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		writeoffHeader.setFinanceDetail(fd);
		schdData.setFinanceMain(fm);

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false));

		schdData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finID, FinServiceEvent.WRITEOFF, false, ""));

		if (StringUtils.isNotBlank(promotionCode)) {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(promotionCode, FinServiceEvent.WRITEOFF, "_AView",
					false, FinanceConstants.MODULEID_PROMOTION));
		} else {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(finType, FinServiceEvent.WRITEOFF, "_AView", false,
					FinanceConstants.MODULEID_FINTYPE));
		}

		schdData.setRepayDetails(financeRepaymentsDAO.getFinRepayList(finID));
		schdData.setPenaltyDetails(recoveryDAO.getFinancePenaltysByFinRef(finID, ""));
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));

		// Finance Customer Details

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}

		// Finance Fee Details
		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_TView"));

		// FinServiceInstruction

		schdData.setFinServiceInstructions(
				finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", procEdtEvent));
		// Finance Check List Details
		// =======================================
		checkListDetailService.setFinanceCheckListDetails(fd, finType, procEdtEvent, userRole);

		// Finance Fee Charge Details
		// =======================================
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));
		if (!accSetIdList.isEmpty()) {
			fd.setFeeCharges(
					transactionEntryDAO.getListFeeChargeRules(accSetIdList, AccountingEvent.WRITEOFF, "_AView", 0));
		}

		// Finance Stage Accounting Posting Details
		// =======================================
		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Docuument Details
		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				procEdtEvent, "_View"));

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

			Provision provision = provisionDAO.getProvisionByFinId(finID, TableType.MAIN_TAB, false);
			if (provision != null) {
				financeWriteoff.setProvisionedAmount(provision.getProvisionedAmt());
			}

			financeWriteoff.setFinID(finID);
			financeWriteoff.setFinReference(finReference);
			financeWriteoff.setWriteoffDate(SysParamUtil.getAppDate());
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
		Date sysData = SysParamUtil.getAppDate();

		long serviceUID = Long.MIN_VALUE;

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		// Finance Stage Accounting Process
		// =======================================
		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceMain(fm);
		postingDTO.setFinanceDetail(fd);
		postingDTO.setValueDate(sysData);
		postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

		AccountingEngine.post(AccountingEvent.STAGE, postingDTO);

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

			AEEvent aeEvent = AEAmounts.procAEAmounts(fm, schedules, profitDetail, AccountingEvent.WRITEOFF, sysData,
					fm.getMaturityDate());

			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
			aeEvent.setDataMap(dataMap);

			aeEvent = postingsPreparationUtil.processPostingDetails(aeEvent);

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

			financeMainDAO.save(fm, tableType, false);

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

		List<FinServiceInstruction> finServiceInstructions = fd.getFinScheduleData().getFinServiceInstructions();
		if (CollectionUtils.isNotEmpty(finServiceInstructions) && fm.isNewRecord()) {
			finServiceInstructionDAO.saveList(finServiceInstructions, tableType.getSuffix());
		}

		// Extended field Details
		if (header.getFinanceDetail().getExtendedFieldRender() != null) {
			List<AuditDetail> details = header.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, header.getFinanceDetail().getExtendedFieldHeader().getEvent(),
					tableType.getSuffix(), serviceUID);

			auditDetails.addAll(details);
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

	private List<FinServiceInstruction> getServiceInstructions(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = fd.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinServiceInstruction> serviceInstructions = finScheduleData.getFinServiceInstructions();

		String moduleDefiner = fd.getModuleDefiner();
		if (CollectionUtils.isEmpty(serviceInstructions)) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(financeMain.getFinID());
			finServInst.setFinReference(financeMain.getFinReference());
			finServInst.setFinEvent(moduleDefiner);

			finScheduleData.setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction fsi : serviceInstructions) {
			String finEvent = fsi.getFinEvent();

			if (fsi.getInstructionUID() == Long.MIN_VALUE) {
				fsi.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(moduleDefiner) || FinServiceEvent.ORG.equals(moduleDefiner)) {
				if (!FinServiceEvent.ORG.equals(finEvent) && !StringUtils.contains(finEvent, "_O")) {
					fsi.setFinEvent(finEvent.concat("_O"));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return finScheduleData.getFinServiceInstructions();
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

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = PennantConstants.TRAN_DEL;

		Date appDate = SysParamUtil.getAppDate();

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = header.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(header.getFinanceDetail());

		long finID = fm.getFinID();

		long serviceUID = Long.MIN_VALUE;
		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(appDate);
			}
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		AccountingEngine.cancelStageAccounting(finID, fd.getModuleDefiner());

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

		finServiceInstructionDAO.deleteList(finID, fd.getModuleDefiner(), "_Temp");

		// ScheduleDetails deletion
		listDeletion(finID, "_Temp");
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(),
					fd.getExtendedFieldRender().getReference(), fd.getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), extendedDetails));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

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

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		long serviceUID = Long.MIN_VALUE;

		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(appDate);
			}
		}

		// Finance Stage Accounting Process
		// =======================================
		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceMain(fm);
		postingDTO.setFinanceDetail(fd);
		postingDTO.setValueDate(appDate);
		postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

		AccountingEngine.post(AccountingEvent.STAGE, postingDTO);

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
		fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_WRITEOFF);
		financeMainDAO.updateWriteOffStatus(finID, true);
		profitDetailsDAO.updateClosingSts(finID, true);

		// Save Finance WriteOff Details
		FinanceWriteoff financeWriteoff = header.getFinanceWriteoff();
		// financeWriteoff.setLinkedTranId(aeEvent.getLinkedTranId());
		financeWriteoffDAO.save(financeWriteoff, "");

		listDeletion(finID, "");
		listSave(schdData, "", 0);

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

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

		// Extended field Render Details.
		List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (fd.getExtendedFieldRender() != null) {
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(details);
		}

		if (!StringUtils.equals(UploadConstants.FINSOURCE_ID_AUTOPROCESS, header.getFinSource())
				|| !StringUtils.equals(UploadConstants.FINSOURCE_ID_UPLOAD, header.getFinSource())) {
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

			financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);

			finServiceInstructionDAO.deleteList(finID, fd.getModuleDefiner(), "_Temp");

			if (details != null && details.size() > 0) {
				extendedFieldDetailsService.delete(header.getFinanceDetail().getExtendedFieldHeader(),
						header.getFinanceDetail().getExtendedFieldRender().getReference(),
						header.getFinanceDetail().getExtendedFieldRender().getSeqNo(), "_Temp",
						auditHeader.getAuditTranType(), details);
			}

			for (int i = 0; i < auditDetails.size(); i++) {
				auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
			}

			auditHeader.setAuditDetail(
					new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
			auditHeader.setAuditDetails(tempAuditDetailList);
			auditHeader.setAuditModule("FinanceDetail");
			auditHeaderDAO.addAudit(auditHeader);

		}

		if (CollectionUtils.isNotEmpty(schdData.getFinServiceInstructions())) {
			finServiceInstructionDAO.saveList(schdData.getFinServiceInstructions(), "");
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.getAuditDetail().setModelData(header);

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
		dataMap = feeDetailService.prepareFeeRulesMap(amountCodes, dataMap, fd);
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		writeoff.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		postingsPreparationUtil.postAccounting(aeEvent);
		writeoff.setLinkedTranId(aeEvent.getLinkedTranId());

		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {
			// save Postings
			if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
				postingsDAO.saveBatch(accountingSetEntries);
			}

			// Save/Update Finance Profit Details
			boolean isNew = false;

			if (StringUtils.equals(fm.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				isNew = true;
			}

			doSave_PftDetails(pftDetail, isNew);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AEEvent prepareAccountingData(FinanceDetail fd, AEEvent aeEvent, FinanceProfitDetail pd) {
		Date curBDay = SysParamUtil.getAppDate();
		String eventCode = fd.getAccountingEventCode();

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();

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
		aeEvent.getAcSetIDList().add(
				AccountingConfigCache.getAccountSetID(fm.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));

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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();

		// Extended field details Validation
		FinanceDetail fd = header.getFinanceDetail();
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = fd.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method,
					auditHeader.getUsrLanguage());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

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

		FinanceMain befFinanceMain = financeMainDAO.getFinanceMainById(finID, "", false);

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = fm.getFinReference();

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = customerQueuingDAO.getProgressCountByCust(fm.getCustID());

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

			financeCheckList = checkListDetailService.getCheckListByFinRef(fm.getFinID(), tableType);
			fd.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		}

		// Extended Field Details
		if (fd.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(fd.getExtendedFieldHeader(),
							fd.getExtendedFieldRender(), auditTranType, method,
							fd.getExtendedFieldHeader().getModuleName()));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
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
	public List<ManualAdvise> getPayableAdvises(long finID, String type) {
		return this.manualAdviseDAO.getPaybleAdvises(finID, type);
	}

	@Override
	public boolean isWriteoffLoan(long finID, String type) {
		return this.financeWriteoffDAO.isWriteoffLoan(finID, type);
	}

	@Autowired
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	@Autowired
	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

}
