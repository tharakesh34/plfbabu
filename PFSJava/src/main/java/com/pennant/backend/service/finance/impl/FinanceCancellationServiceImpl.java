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
 * FileName : FinanceCancellationServiceImpl.java *
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.impl.CollateralAssignmentValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinChequeHeaderService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.holdmarking.service.HoldMarkingService;
import com.pennant.pff.lien.service.LienService;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.LoanCancelationUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennapps.core.util.ObjectUtil;

public class FinanceCancellationServiceImpl extends GenericFinanceDetailService implements FinanceCancellationService {
	private static final Logger logger = LogManager.getLogger(FinanceCancellationServiceImpl.class);

	private LimitCheckDetails limitCheckDetails;
	private LimitManagement limitManagement;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptService receiptService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired(required = false)
	private NotificationService notificationService;
	private VASRecordingDAO vASRecordingDAO;
	private long tempWorkflowId;
	private ReasonDetailDAO reasonDetailDAO;
	private FinChequeHeaderService finChequeHeaderService;
	private FinODDetailsDAO finODDetailsDAO;
	private LienService lienService;
	private FeeCalculator feeCalculator;
	private FinExcessAmountDAO finExcessAmountDAO;

	private HoldMarkingService holdMarkingService;

	public FinanceCancellationServiceImpl() {
		super();
	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID, String type, String userRole, String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		String finReference = fm.getFinReference();
		String finType = fm.getFinType();
		long custID = fm.getCustID();

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		schdData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finID, procEdtEvent, false, "_TView"));

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}

		checkListDetailService.setFinanceCheckListDetails(fd, finType, procEdtEvent, userRole);
		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType, procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, "_View"));
		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_View"));
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID, type));
		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				procEdtEvent, "_View"));

		fd.setAdvancePaymentsList(finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finID, "_View"));

		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(finType, AccountingEvent.CANCELFIN, "_AView", false,
				FinanceConstants.MODULEID_FINTYPE));

		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_TView"));

		fd.setChequeHeader(finChequeHeaderService.getChequeHeaderByRef(finID));

		logger.debug(Literal.LEAVING);
		return fd;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Date appDate = SysParamUtil.getAppDate();

		long serviceUID = Long.MIN_VALUE;
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

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

		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		fm.setRcdMaintainSts(FinServiceEvent.CANCELFIN);
		if (tableType == TableType.MAIN_TAB) {
			fm.setRcdMaintainSts("");
			fm.setFinIsActive(false);
			fm.setClosedDate(appDate);
			fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
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

		// Repayments Postings Details Process Execution
		if (!fm.isWorkflow()) {
			if (FinanceConstants.ACCOUNTING_TOTALREVERSAL) {
				// Cancel All Transactions for Finance Disbursement including Commitment Postings, Stage Accounting on
				// Reversal
				postingsPreparationUtil.postReveralsExceptFeePay(finReference);
				logger.debug("Reverse Transaction Success for Reference : " + finReference);
			} else {
				// Event Based Accounting on Final Stage
				auditHeader = executeAccountingProcess(auditHeader, appDate);

				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
			}
		}

		// Finance Main Details Save And Update
		// =======================================
		if (fm.isNewRecord()) {
			financeMainDAO.save(fm, tableType, false);
		} else {
			financeMainDAO.update(fm, tableType, false);
		}
		// ***cancel loans reason implemented.
		if (fm.getDetailsList().size() > 0) {
			saveCancelReasonData(fm);
		}

		// Save Fee Charges List
		// =======================================
		if (tableType == TableType.TEMP_TAB) {
			finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, tableType.getSuffix());
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType.getSuffix(), serviceUID));
		}

		// cheque Details
		ChequeHeader ch = fd.getChequeHeader();
		if (ch != null && CollectionUtils.isNotEmpty(ch.getChequeDetailList())) {
			auditDetails.addAll(finChequeHeaderService.processingChequeDetailList(
					fd.getAuditDetailMap().get("ChequeDetail"), tableType, ch.getHeaderID()));
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType.getSuffix(), schdData.getFinanceMain(),
					fd.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void saveCancelReasonData(FinanceMain financeMain) {
		reasonDetailDAO.deleteCancelReasonDetails(financeMain.getFinReference());
		ReasonHeader reasonHeader = new ReasonHeader();
		reasonHeader.setModule("FINANCE");
		reasonHeader.setReference(financeMain.getFinReference());
		reasonHeader.setRemarks(financeMain.getCancelRemarks());
		reasonHeader.setCancelType(financeMain.getCancelType());
		reasonHeader.setActivity("Cancled");
		reasonHeader.setDetailsList(financeMain.getDetailsList());
		reasonDetailDAO.save(reasonHeader);
	}

	@Override
	public List<ReasonHeader> getCancelReasonDetails(String reference) {
		return reasonDetailDAO.getCancelReasonDetails(reference);
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(long finID) {
		return finAdvancePaymentsService.getFinAdvancePaymentByFinRef(finID);
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long serviceUID = Long.MIN_VALUE;
		long finID = fm.getFinID();

		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		AccountingEngine.cancelStageAccounting(finID, fd.getModuleDefiner());

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		auditDetails.addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", schdData.getFinanceMain(), fd.getModuleDefiner(),
					serviceUID);
			auditDetails.addAll(details);
		}

		// ScheduleDetails deletion
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		reasonDetailDAO.deleteCancelReasonDetails(fm.getFinReference());

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isNotReqEOD) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (isNotReqEOD) {
			aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		}
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Date appData = SysParamUtil.getAppDate();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long serviceUID = Long.MIN_VALUE;
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Fetch Next Payment Details from Finance for Salaried Postings Verification
		fm.setFinIsActive(false);
		fm.setClosedDate(appData);
		fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
		fm.setRcdMaintainSts("");
		fm.setRoleCode("");
		fm.setNextRoleCode("");
		fm.setTaskId("");
		fm.setNextTaskId("");
		fm.setWorkflowId(0);

		// Finance Cancellation Posting Process Execution
		// =====================================
		// Event Based Accounting on Final Stage
		List<ReturnDataSet> returnDataSets = postingsPreparationUtil.postReveralsExceptFeePay(finReference);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// GST Invoice Details Reversal on Loan Cancellation
		if (returnDataSets != null && !returnDataSets.isEmpty()) {
			createGSTInvoiceForCancellLoan(returnDataSets.get(0).getLinkedTranId(), fd);
		}

		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		if (StringUtils.isNotBlank(fm.getFinCommitmentRef())) {
			commitment = commitmentDAO.getCommitmentById(fm.getFinCommitmentRef().trim(), "");

			BigDecimal cmtUtlAmt = CalculationUtil.getConvertedAmount(fm.getFinCcy(), commitment.getCmtCcy(),
					fm.getFinAmount());
			commitmentDAO.updateCommitmentAmounts(commitment.getCmtReference(), cmtUtlAmt.negate(),
					commitment.getCmtExpDate());
			CommitmentMovement cmtMovement = prepareCommitMovement(commitment, fm, cmtUtlAmt, 0);
			if (cmtMovement != null) {
				commitmentMovementDAO.save(cmtMovement, "");
			}
		}

		if (isNotReqEOD) {
			List<Long> receiptIdList = finReceiptHeaderDAO.fetchReceiptIdList(finReference);
			List<FinReceiptDetail> receiptDetailList = new ArrayList<>();
			for (Long receiptId : receiptIdList) {
				receiptDetailList.addAll(finReceiptDetailDAO.getReceiptDetailForCancelReversalByID(receiptId, "_view"));

				FinExcessAmount fea = finExcessAmountDAO.getExcessAmountsByReceiptId(receiptId);

				if (fea != null) {
					for (FinReceiptDetail rd : receiptDetailList) {
						rd.setPayAgainstID(fea.getExcessID());
					}
				}
			}
			if (receiptIdList != null && receiptIdList.size() > 0) {
				finReceiptHeaderDAO.cancelReceipts(finReference);
				finReceiptDetailDAO.cancelReceiptDetails(receiptIdList);
			}

			for (FinReceiptDetail rd : receiptDetailList) {
				FinServiceInstruction serviceInstr = createFinServInstr(finReference, rd, finID);

				FinReceiptData frd = feeCalculator
						.calculateFees(receiptService.prepareFinReceiptData(serviceInstr, fd));
				serviceInstr.setFinFeeDetails(frd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
				serviceInstr.setLoanCancellation(true);

				FinanceDetail detail = receiptService.receiptTransaction(serviceInstr);
				FinScheduleData schd = detail.getFinScheduleData();
				if (CollectionUtils.isNotEmpty(schd.getErrorDetails())) {
					ErrorDetail error = schd.getErrorDetails().get(0);
					throw new AppException(error.getError());
				}
			}
		}
		tranType = PennantConstants.TRAN_UPD;
		fm.setRecordType("");
		financeMainDAO.update(fm, TableType.MAIN_TAB, false);
		manualAdviseDAO.cancelAdvises(finID);
		finODDetailsDAO.delete(finID);
		// Profit Details Inactive status Updation
		// Bug FIX: Closing status not updated in FinPftDetails while cancel the loan.
		profitDetailsDAO.updateFinPftMaturity(finID, fm.getClosingStatus(), false);

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", schdData.getFinanceMain(), fd.getModuleDefiner(),
					serviceUID);
			auditDetails.addAll(details);
			listDocDeletion(fd, "_Temp");
		}

		// set Check list details Audit
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

		// Fee charges deletion
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
		if (isNotReqEOD) {
			finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");
		}

		// Disbursement Cancellation
		if (isNotReqEOD) {
			processDisbursmentCancellation(fd);
		}
		// Checklist Details delete
		// =======================================
		if (isNotReqEOD) {
			tempAuditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", tranType));
		}

		// Adding audit as deleted from TEMP table
		boolean uploadSource = UploadTypes.LOAN_CANCEL.name().equals(fm.getFinSourceID());
		if (isNotReqEOD && (auditHeader.getApiHeader() == null && !uploadSource)) {
			financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);
		}

		if (!uploadSource) {
			auditHeader.setAuditDetail(
					new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
			auditHeader.setAuditDetails(tempAuditDetailList);
			auditHeaderDAO.addAudit(auditHeader);
		}
		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		// Delinking collateral Assigned to Finance
		// ==========================================
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {
				getCollateralAssignmentValidation().saveCollateralMovements(fm.getFinReference());
			}
		} else {
			List<FinCollaterals> collateralList = fd.getFinanceCollaterals();
			if (collateralList != null && !collateralList.isEmpty()) {
				// Release the collateral.
			}
		}

		// send Cancel Utilization Request to ACP Interface and save log details
		// =======================================
		if (ImplementationConstants.LIMIT_INTERNAL) {
			if (isNotReqEOD) {
				limitManagement.processLoanCancel(fd, false);
			}
		} else {
			limitCheckDetails.doProcessLimits(schdData.getFinanceMain(), FinanceConstants.CANCEL_UTILIZATION);
		}

		// updating the processed with 1 in finstageAccountingLog
		finStageAccountingLogDAO.update(finID, fd.getModuleDefiner(), false);

		// Extended Field Details
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(details);
		}

		// cheque Details
		ChequeHeader ch = fd.getChequeHeader();
		if (ch != null && CollectionUtils.isNotEmpty(ch.getChequeDetailList())) {
			auditDetails.addAll(finChequeHeaderService.processingChequeDetailList(
					fd.getAuditDetailMap().get("ChequeDetail"), TableType.MAIN_TAB, ch.getHeaderID()));
		}

		saveCancelReasonData(fm);

		// Notification
		tempWorkflowId = fm.getWorkflowId();
		Notification notification = new Notification();
		notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
		notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
		notification.setModule("LOAN_CANCELLATION");
		notification.setSubModule(FinServiceEvent.CANCELFIN);
		notification.setKeyReference(fm.getFinReference());
		notification.setStage(PennantConstants.REC_ON_APPR);
		notification.setReceivedBy(fm.getLastMntBy());
		fm.setWorkflowId(tempWorkflowId);

		if (notificationService != null) {
			notificationService.sendNotifications(notification, fd, fm.getFinType(), fd.getDocumentDetailsList());
		}

		if (ImplementationConstants.ALLOW_LIEN
				&& FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			lienService.update(fd);
		}

		if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			holdMarkingService.removeHold(fm);
		}

		cancelChildLoan(finReference);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void cancelChildLoan(String finReference) {
		Date appDate = SysParamUtil.getAppDate();

		List<Long> finRefByParentRef = financeMainDAO.getChildFinRefByParentRef(finReference);

		if (CollectionUtils.isEmpty(finRefByParentRef)) {
			logger.debug(" Undisbursed Child loans are not available for  the specified finreference >> {}",
					finReference);
			return;
		}

		List<FinanceMain> list = new ArrayList<>();
		for (long finID : finRefByParentRef) {
			FinanceMain fm = new FinanceMain();
			fm.setFinID(finID);
			fm.setFinIsActive(false);
			fm.setClosedDate(appDate);
			fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
			fm.setRcdMaintainSts("");
			fm.setRoleCode("");
			fm.setNextRoleCode("");
			fm.setTaskId("");
			fm.setNextTaskId("");
			fm.setWorkflowId(0);
			fm.setRecordType("");

			list.add(fm);
		}
		financeMainDAO.updateChildFinance(list, "_Temp");
	}

	private void createGSTInvoiceForCancellLoan(long linkedTranID, FinanceDetail fd) {
		if (linkedTranID <= 0) {
			return;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (CollectionUtils.isEmpty(schdData.getFinFeeDetailList())) {
			List<FinFeeDetail> finFeedetails = finFeeDetailService.getFinFeeDetailById(fm.getFinID(), false, "_AView");
			if (CollectionUtils.isEmpty(finFeedetails)) {
				return;
			}
			schdData.setFinFeeDetailList(finFeedetails);
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranID);
		invoiceDetail.setFinanceDetail(fd);
		invoiceDetail.setFinFeeDetailsList(schdData.getFinFeeDetailList());
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
		invoiceDetail.setOrigination(true);
		invoiceDetail.setWaiver(false);
		invoiceDetail.setDbInvSetReq(true);

		// Normal Fees invoice preparation
		// In Case of Loan Cancel Approval GST Invoice is happen only for remaining fee after IMD.
		if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
				fee.setPaidFromLoanApproval(true);
			}
		}

		// Normal Fees invoice preparation
		Long dueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (int i = 0; i < schdData.getFinFeeDetailList().size(); i++) {
			FinFeeDetail finFeeDetail = schdData.getFinFeeDetailList().get(i);
			if (finFeeDetail.getTaxHeader() != null && finFeeDetail.getNetAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (dueInvoiceID == null) {
					dueInvoiceID = finFeeDetail.getTaxHeader().getInvoiceID();
				}
				finFeeDetail.getTaxHeader().setInvoiceID(dueInvoiceID);
			}
		}

		// Waiver Fees Invoice Preparation
		if (ImplementationConstants.TAX_DFT_CR_INV_REQ) {
			List<FinFeeDetail> waiverFees = new ArrayList<>();
			for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
				if (fee.isTaxApplicable() && fee.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
					waiverFees.add(fee);
				}
			}

			invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranID);
			invoiceDetail.setFinanceDetail(fd);
			invoiceDetail.setFinFeeDetailsList(waiverFees);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
			invoiceDetail.setOrigination(true);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setDbInvSetReq(true);

			if (CollectionUtils.isNotEmpty(waiverFees)) {
				invoiceDetail.setFinFeeDetailsList(waiverFees);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
				invoiceDetail.setOrigination(true);
				invoiceDetail.setWaiver(true);
				invoiceDetail.setDbInvSetReq(true);

				gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
			}
		}

	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = auditHeader.getUsrLanguage();

		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = financeDetail.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
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

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(financeDetail.getExtendedFieldRender(), auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}
		// Finance Checklist Details
		// =======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(
						checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		}

		// chequeHeader details
		ChequeHeader ch = financeDetail.getChequeHeader();
		if (ch != null) {
			if (CollectionUtils.isNotEmpty(ch.getChequeDetailList())) {
				ch.getChequeDetailList().forEach(cd -> cd.setRecordType(PennantConstants.RECORD_TYPE_DEL));
				auditDetailMap.put("ChequeDetail",
						finChequeHeaderService.setChequeDetailAuditData(ch, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("ChequeDetail"));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;

	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		FinanceMain tempFinanceMain = null;
		if (fm.isWorkflow()) {
			tempFinanceMain = financeMainDAO.getFinanceMainById(finID, "_Temp", false);
		}
		FinanceMain befFinanceMain = financeMainDAO.getFinanceMainById(finID, "", false);
		FinanceMain oldFinanceMain = fm.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finReference;
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

		List<FinAdvancePayments> list = fd.getAdvancePaymentsList();
		if (CollectionUtils.isNotEmpty(list)) {
			for (FinAdvancePayments fap : list) {
				String status = fap.getStatus();

				if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS
						&& DisbursementConstants.STATUS_PAID.equals(status)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60406", errParm, valueParm), usrLanguage));
				}

				if (DisbursementConstants.STATUS_AWAITCON.equals(status)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60408", errParm, valueParm), usrLanguage));
				}
			}

			List<VASRecording> vasRecordings = vASRecordingDAO.getVASRecordingsStatusByReference(finReference, "");
			// Checking VAS instruction status.
			if (CollectionUtils.isNotEmpty(vasRecordings)) {
				for (VASRecording vasRecording : vasRecordings) {
					if (!StringUtils.equals(vasRecording.getVasStatus(), VASConsatnts.STATUS_CANCEL)) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "60214", errParm, valueParm), usrLanguage));
					}
				}
			}
			// vallidation for manual dues
			List<ManualAdvise> manualAdvise = manualAdviseDAO.getReceivableAdvises(finID, "");
			if (CollectionUtils.isNotEmpty(manualAdvise)
					&& !UploadTypes.LOAN_CANCEL.name().equals(fm.getFinSourceID())) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60022", errParm, valueParm));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !fm.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	public CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain,
			BigDecimal postAmount, long linkedtranId) {

		CommitmentMovement movement = new CommitmentMovement();
		Date curBussDate = SysParamUtil.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(commitmentMovementDAO.getMaxMovementOrderByRef(commitment.getCmtReference()) + 1);
		movement.setMovementType("FC");// Finance Cancellation
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().subtract(postAmount));
		if (commitment.getCmtExpDate().compareTo(curBussDate) < 0) {
			movement.setCmtAvailable(BigDecimal.ZERO);
		} else {
			movement.setCmtAvailable(commitment.getCmtAvailable().add(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(9999);
		movement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		movement.setRecordStatus("Approved");
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);

		return movement;

	}

	private List<AuditDetail> processDisbursmentCancellation(FinanceDetail fd) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinAdvancePayments> list = fd.getAdvancePaymentsList();

		if (list == null) {
			return auditDetails;
		}

		int count = 0;
		for (FinAdvancePayments finAdvpay : list) {
			if (!DisbursementConstants.STATUS_CANCEL.equals(finAdvpay.getStatus())) {
				count = count + 1;
				FinAdvancePayments detailObject = new FinAdvancePayments();
				String[] fields = PennantJavaUtil.getFieldDetails(detailObject, detailObject.getExcludeFields());
				AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, count, fields[0], fields[1],
						finAdvpay.getBefImage(), finAdvpay);
				auditDetails.add(auditDetail);
				finAdvpay.setStatus(DisbursementConstants.STATUS_CANCEL);
				finAdvancePaymentsDAO.update(finAdvpay, "");

			}
		}

		if (auditDetails.size() > 0) {
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			finAdvancePaymentsDAO.deleteByFinRef(fm.getFinID(), "_Temp");
		}
		return auditDetails;
	}

	private FinServiceInstruction createFinServInstr(String finReference, FinReceiptDetail rd, long finId) {
		Date appDate = SysParamUtil.getAppDate();

		FinServiceInstruction fsi = new FinServiceInstruction();
		fsi.setFinReference(finReference);
		fsi.setModule("Receipts");
		fsi.setFinID(finId);
		fsi.setValueDate(rd.getValueDate());
		fsi.setAmount(rd.getAmount());
		fsi.setAllocationType(AllocationType.MANUAL);
		Long fundingAc = rd.getFundingAc();
		if (fundingAc != null) {
			fsi.setFundingAc(fundingAc);
		}

		LoggedInUser loggedInUser = new LoggedInUser();
		loggedInUser.setLoginUsrID(1000);
		fsi.setLoggedInUser(loggedInUser);
		fsi.setBankCode(rd.getBankCode());
		fsi.setStatus("A");
		fsi.setDepositDate(rd.getDepositDate());
		fsi.setRealizationDate(rd.getRealizationDate());
		fsi.setInstrumentDate(appDate);
		fsi.setReceivedDate(rd.getReceivedDate());
		fsi.setRemarks(LoanCancelationUtil.LOAN_CANCEL_REMARKS);
		fsi.setPaymentMode(ReceiptMode.EXCESS);
		fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		fsi.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		fsi.setReceiptdetailExits(false);
		fsi.setUploadAllocationDetails(new ArrayList<>());
		fsi.setReqType(RepayConstants.REQTYPE_POST);

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setAmount(fsi.getAmount());
		rcd.setValueDate(rd.getValueDate());
		rcd.setBankCode(rd.getBankCode());
		rcd.setDepositDate(rd.getDepositDate());
		rcd.setFundingAc(fundingAc);
		rcd.setReceivedDate(rd.getReceivedDate());
		rcd.setStatus(fsi.getStatus());
		rcd.setRemarks(LoanCancelationUtil.LOAN_CANCEL_REMARKS);
		rcd.setReference(finReference);
		rcd.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rcd.setPaymentType(ReceiptMode.EXCESS);
		rcd.setPayAgainstID(rd.getPayAgainstID());
		rcd.setNoReserve(true);

		fsi.setReceiptDetail(rcd);
		return fsi;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public void setTempWorkflowId(long tempWorkflowId) {
		this.tempWorkflowId = tempWorkflowId;
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	public CollateralAssignmentValidation getCollateralAssignmentValidation() {
		if (collateralAssignmentValidation == null) {
			this.collateralAssignmentValidation = new CollateralAssignmentValidation(collateralAssignmentDAO);
		}
		return collateralAssignmentValidation;
	}

	@Autowired
	public void setFinChequeHeaderService(FinChequeHeaderService finChequeHeaderService) {
		this.finChequeHeaderService = finChequeHeaderService;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setLienService(LienService lienService) {
		this.lienService = lienService;
	}

	@Autowired
	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setHoldMarkingService(HoldMarkingService holdMarkingService) {
		this.holdMarkingService = holdMarkingService;
	}
}