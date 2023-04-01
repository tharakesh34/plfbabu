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
 * FileName : ReceiptCancellationServiceImpl.java *
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinODCAmountDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueChargeMovement;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.eod.cache.BounceConfigCache;
import com.pennant.pff.eod.cache.FeeTypeConfigCache;
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.presentment.exception.PresentmentException;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.rits.cloning.Cloner;

public class ReceiptCancellationServiceImpl extends GenericService<FinReceiptHeader>
		implements ReceiptCancellationService {
	private static final Logger logger = LogManager.getLogger(ReceiptCancellationServiceImpl.class);

	private LimitManagement limitManagement;
	private PostingsPreparationUtil postingsPreparationUtil;
	private RepaymentPostingsUtil repaymentPostingsUtil;

	private FeeReceiptService feeReceiptService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldExtensionService extendedFieldExtensionService;
	private TdsReceivablesTxnService tdsReceivablesTxnService;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private OverdrafLoanService overdrafLoanService;
	private LatePayMarkingService latePayMarkingService;
	private AccrualService accrualService;
	private FinFeeDetailService finFeeDetailService;

	private AuditHeaderDAO auditHeaderDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinanceMainDAO financeMainDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private PostingsDAO postingsDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private CustomerDAO customerDAO;
	private DepositDetailsDAO depositDetailsDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinStageAccountingLogDAO finStageAccountingLogDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private DepositChequesDAO depositChequesDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinanceTypeDAO financeTypeDAO;
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private PaymentHeaderService paymentHeaderService;
	private FinODCAmountDAO finODCAmountDAO;

	public ReceiptCancellationServiceImpl() {
		super();
	}

	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment) {
		logger.debug(Literal.ENTERING);

		String tableType = TableType.VIEW.getSuffix();

		if (isFeePayment) {
			tableType = "_FCView";
		}

		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, tableType);

		if (rch == null) {
			return null;
		}

		rch.setFinCategory(financeMainDAO.getFinCategoryByFinType(rch.getFinType()));

		Long finID = rch.getFinID();

		List<FinReceiptDetail> receiptDetails = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_AView");
		rch.setReceiptDetails(receiptDetails);

		List<FinRepayHeader> repayHeader = financeRepaymentsDAO.getFinRepayHeadersByRef(finID, "");

		if (!isFeePayment) {
			setRepaySchedules(finID, repayHeader);
		}

		setRepayHeader(receiptDetails, repayHeader);

		String rcdModeSts = rch.getReceiptModeStatus();
		String receiptMode = rch.getReceiptMode();
		if ((StringUtils.isNotEmpty(rch.getRecordType()) && RepayConstants.MODULETYPE_BOUNCE.equals(rcdModeSts))
				|| (isFeePayment && ReceiptMode.CHEQUE.equalsIgnoreCase(receiptMode))) {
			rch.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_TView"));
		}

		if (isFeePayment) {
			rch.setPaidFeeList(feeReceiptService.getPaidFinFeeDetails(rch.getReference(), receiptID, "_View"));
			FinReceiptHeader frh = finReceiptHeaderDAO.getFinTypeByReceiptID(rch.getReceiptID());
			if (frh != null) {
				rch.setFinType(frh.getFinType());
				rch.setFinTypeDesc(frh.getFinTypeDesc());
				rch.setFinCcy(frh.getFinCcy());
				rch.setFinCcyDesc(frh.getFinCcyDesc());
			}
		}

		List<DocumentDetails> documents = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(receiptID),
				PennantConstants.FEE_DOC_MODULE_NAME, FinServiceEvent.RECEIPT, "_View");

		if (CollectionUtils.isEmpty(rch.getDocumentDetails())) {
			rch.setDocumentDetails(documents);
		} else {
			rch.getDocumentDetails().addAll(documents);
		}

		logger.debug(Literal.LEAVING);
		return rch;
	}

	@Override
	public List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList) {
		return postingsDAO.getPostingsByTransIdList(tranIdList);
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(long postRef) {
		return postingsDAO.getPostingsByPostRef(postRef);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		FinReceiptData frd = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = frd.getReceiptHeader();

		long serviceUID = Long.MIN_VALUE;
		if (rch.getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(rch.getExtendedFieldRender(),
					rch.getExtendedFieldExtension());
		}

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		receiptHeader.setRcdMaintainSts("R");
		// Receipt Header Details Save And Update
		// =======================================
		if (receiptHeader.isNewRecord()) {
			finReceiptHeaderDAO.save(receiptHeader, tableType);
		} else {
			finReceiptHeaderDAO.update(receiptHeader, tableType);
		}

		// Bounce reason Code
		if (receiptHeader.getManualAdvise() != null) {
			if (receiptHeader.getManualAdvise().isNewRecord()) {
				manualAdviseDAO.save(receiptHeader.getManualAdvise(), tableType);
			} else {
				manualAdviseDAO.update(receiptHeader.getManualAdvise(), tableType);
			}
		}

		// Document Details
		List<DocumentDetails> documentsList = receiptHeader.getDocumentDetails();
		if (CollectionUtils.isNotEmpty(documentsList)) {
			List<AuditDetail> details = receiptHeader.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, receiptHeader, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// Extended field Details
		if (receiptHeader.getExtendedFieldRender() != null) {
			List<AuditDetail> details = receiptData.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN,
					receiptData.getFinanceDetail().getExtendedFieldHeader().getEvent(), tableType.getSuffix(),
					serviceUID);
			auditDetails.addAll(details);
		}

		// Extended field Extensions Details
		if (receiptHeader.getExtendedFieldExtension() != null) {
			List<AuditDetail> details = receiptData.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.processingExtendedFieldExtList(details, receiptData, serviceUID,
					tableType);

			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinReceiptHeader rch = null;
		FinReceiptData rd = null;
		// Bug fix
		if (auditHeader.getAuditDetail().getModelData() instanceof FinReceiptData) {
			rd = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			rch = rd.getReceiptHeader();
		} else if (auditHeader.getAuditDetail().getModelData() instanceof FinReceiptHeader) {
			rch = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();
		}

		if (rch == null) {
			return auditHeader;
		}

		// Bounce Reason Code
		if (rch.getManualAdvise() != null) {
			manualAdviseDAO.delete(rch.getManualAdvise(), TableType.TEMP_TAB);
		}

		// Deleting Receipt Documents
		String auditTranType = auditHeader.getAuditTranType();
		auditDetails.addAll(listDeletion(rch, TableType.TEMP_TAB.getSuffix(), auditTranType));
		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);

		// Delete Extended field Render Details.
		if (rd != null) {
			FinanceDetail fd = rd.getFinanceDetail();
			List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");

			if (CollectionUtils.isNotEmpty(extendedDetails)) {
				ExtendedFieldRender render = fd.getExtendedFieldRender();
				auditDetails.addAll(extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(), rch.getReference(),
						render.getSeqNo(), TableType.TEMP_TAB.getSuffix(), auditTranType, extendedDetails));
			}

			if (rch.getExtendedFieldExtension() != null) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldExtension");
				details = extendedFieldExtensionService.delete(details, auditTranType, TableType.TEMP_TAB);
				auditDetails.addAll(details);
			}

			auditHeader.setAuditDetails(auditDetails);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], rch.getBefImage(), rch));
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rd = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();

		FinReceiptHeader rch = rd.getReceiptHeader();
		long receiptID = rch.getReceiptID();

		FinReceiptHeader finReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID,
				TableType.MAIN_TAB.getSuffix());

		FinanceDetail fd = rd.getFinanceDetail();

		String errorCode = "";

		long finID = rch.getFinID();

		if (FinServiceEvent.FEEPAYMENT.equals(rch.getReceiptPurpose())) {
			List<FinReceiptDetail> receiptdetails = rch.getReceiptDetails();
			FinReceiptDetail receiptdetail = receiptdetails.get(0);
			FinRepayHeader repayHeader = receiptdetail.getRepayHeader();
			rch.setLinkedTranId(repayHeader.getLinkedTranId());
			errorCode = procFeeReceiptCancellation(rch);
			if (StringUtils.isBlank(errorCode)) {
				tranType = PennantConstants.TRAN_UPD;
				rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				rch.setRecordType("");
				rch.setRoleCode("");
				rch.setNextRoleCode("");
				rch.setTaskId("");
				rch.setNextTaskId("");
				rch.setWorkflowId(0);
				rch.setRcdMaintainSts(null);
				rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				ManualAdvise advice = rch.getManualAdvise();
				if (advice != null) {
					advice.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					advice.setRecordType("");
					advice.setRoleCode("");
					advice.setNextRoleCode("");
					advice.setTaskId("");
					advice.setNextTaskId("");
					advice.setWorkflowId(0);
					advice.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					String adviseId = manualAdviseDAO.save(advice, TableType.MAIN_TAB);
					advice.setAdviseID(Long.parseLong(adviseId));
				}

				finReceiptHeaderDAO.update(rch, TableType.MAIN_TAB);

				// Document Details
				List<DocumentDetails> documentsList = rch.getDocumentDetails();
				if (CollectionUtils.isNotEmpty(documentsList)) {
					List<AuditDetail> details = rch.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, rch, TableType.MAIN_TAB.getSuffix());
					// deleting the data from temp table while Approve
					listDeletion(rch, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType());
				}
			}
		} else {
			FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(finID);
			errorCode = procReceiptCancellation(rch, auditHeader.getAuditBranchCode(), financeMain);
		}
		if (StringUtils.isNotBlank(errorCode)) {
			throw new InterfaceException("9999", errorCode);
		}

		// Receipt Header Updation
		// =======================================

		if (!FinServiceEvent.FEEPAYMENT.equals(rch.getReceiptPurpose())) {

			tranType = PennantConstants.TRAN_UPD;
			rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			rch.setRecordType("");
			rch.setRoleCode("");
			rch.setNextRoleCode("");
			rch.setTaskId("");
			rch.setNextTaskId("");
			rch.setWorkflowId(0);
			rch.setRcdMaintainSts(null);
			// FIXME:Checking the record is available in main table or not to fix PK issue
			if (FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose()) && finReceiptHeader != null) {
				finReceiptHeaderDAO.update(rch, TableType.MAIN_TAB);
				tdsReceivablesTxnService.cancelReceivablesTxnByReceiptId(rch.getReceiptID());
			} else {
				finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);
				for (FinReceiptDetail finRecpt : rch.getReceiptDetails()) {
					finReceiptDetailDAO.save(finRecpt, TableType.MAIN_TAB);
				}

				int i = 0;
				for (ReceiptAllocationDetail alloc : rch.getAllocations()) {
					alloc.setReceiptID(receiptID);
					alloc.setAllocationID(++i);
				}

				allocationDetailDAO.saveAllocations(rch.getAllocations(), TableType.MAIN_TAB);
			}

			List<FinReceiptDetail> finRcptDtlList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "");

			for (FinReceiptDetail finRecpt : finRcptDtlList) {
				List<RepayScheduleDetail> repaySchdList = finReceiptDetailDAO
						.fetchRepaySchduleList(finRecpt.getReceiptSeqID());

				for (RepayScheduleDetail rpySchd : repaySchdList) {
					if (rpySchd.getWaivedAmt().compareTo(BigDecimal.ZERO) > 0) {
						finODDetailsDAO.updateWaiverAmount(finID, rpySchd.getSchDate(), rpySchd.getWaivedAmt(),
								rpySchd.getPenaltyPayNow());
					}
				}
			}
		} else {
			// process FinFeeDetails
			processFinFeeDetails(rch);
		}

		FinanceMain fm = new FinanceMain();

		fm.setFinID(finID);
		fm.setFinReference(rch.getReference());
		fm.setAppDate(SysParamUtil.getAppDate());

		finServiceInstructionDAO.deleteList(finID, rch.getReceiptPurpose(), TableType.TEMP_TAB.getSuffix());
		saveFSI(rch, fm, auditHeader, TableType.MAIN_TAB);

		// Bounce Reason Code
		if (rch.getManualAdvise() != null) {
			manualAdviseDAO.deleteByAdviseId(rch.getManualAdvise(), TableType.TEMP_TAB);
		}

		finReceiptDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		deleteTaxHeaderId(receiptID, TableType.TEMP_TAB.getSuffix());

		// Receipt Allocation Details
		allocationDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());
		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// Status Update in Presentment Details
		presentmentDetailDAO.updateStatusAgainstReseipId(rch.getReceiptModeStatus(), receiptID);

		finFeeDetailDAO.deleteByTransactionId(String.valueOf(receiptID), false, TableType.TEMP_TAB.getSuffix());

		if (ImplementationConstants.LIMIT_INTERNAL) {
			BigDecimal priAmt = BigDecimal.ZERO;

			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				FinRepayHeader rh = rcd.getRepayHeader();

				if (rh == null) {
					continue;
				}

				for (RepayScheduleDetail rpySchd : rh.getRepayScheduleDetails()) {
					priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
				}
			}

			if (priAmt.compareTo(BigDecimal.ZERO) > 0) {
				FinanceMain main = financeMainDAO.getFinanceMainForBatch(finID);
				Customer customer = customerDAO.getCustomerByID(main.getCustID());
				limitManagement.processLoanRepayCancel(main, customer, priAmt,
						StringUtils.trimToEmpty(main.getProductCategory()));
			}

		}

		long serviceUID = Long.MIN_VALUE;
		if (fd.getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(fd.getExtendedFieldRender(),
					fd.getExtendedFieldExtension());
		}

		// Extended Field Details
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (fd.getExtendedFieldRender() != null) {
			extendedDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(extendedDetails,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(extendedDetails);
		}

		// Extended field Extensions Details
		List<AuditDetail> extensionDetails = fd.getAuditDetailMap().get("ExtendedFieldExtension");
		if (extensionDetails != null && extensionDetails.size() > 0) {
			extensionDetails = extendedFieldExtensionService.processingExtendedFieldExtList(extensionDetails, rd,
					serviceUID, TableType.MAIN_TAB);

			auditDetails.addAll(extensionDetails);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], rch.getBefImage(), rch));

		// Adding audit as deleted from TEMP table
		// auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rch.getBefImage(), rch));

		// Adding audit as Insert/Update/deleted into main table
		auditHeaderDAO.addAudit(auditHeader);
		// Delete Extended Details from Temp Table
		if (extendedDetails != null && extendedDetails.size() > 0) {
			extendedDetails = extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(), rch.getReference(),
					fd.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), extendedDetails);

			auditDetails.addAll(extendedDetails);
		}

		if (extensionDetails != null && extensionDetails.size() > 0) {
			extensionDetails = extendedFieldExtensionService.delete(extensionDetails, tranType, TableType.TEMP_TAB);
			auditDetails.addAll(extensionDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PresentmentDetail presentmentCancellation(PresentmentDetail pd, CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = custEODEvent.getFinEODEvents().get(0).getFinanceMain();
		Customer customer = custEODEvent.getCustomer();

		String bounceCode = StringUtils.trimToNull(pd.getBounceCode());

		FinReceiptHeader rch = getFinReceiptHeaderById(pd.getReceiptID(), false);

		if (rch == null) {
			throw new PresentmentException(PresentmentError.PRMNT5010);
		}

		BounceReason br = BounceConfigCache.getCacheBounceReason(bounceCode);

		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		FinReceiptDetail rcd = getPDReceipt(rcdList);

		prepareManualAdvise(rch, rcd, pd, br);

		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_BOUNCE);
		rch.setBounceDate(pd.getAppDate());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setLastMntBy(pd.getLastMntBy());
		rch.setVersion(rch.getVersion() + 1);

		String errorMsg = procReceiptCancellation(rch, PennantConstants.APP_PHASE_EOD, fm);

		if (StringUtils.trimToNull(errorMsg) == null) {
			finReceiptHeaderDAO.update(rch, TableType.MAIN_TAB);
			tdsReceivablesTxnService.cancelReceivablesTxnByReceiptId(rch.getReceiptID());

			if (ImplementationConstants.LIMIT_INTERNAL) {
				BigDecimal priAmt = BigDecimal.ZERO;
				for (FinReceiptDetail receiptDetail : rcdList) {
					FinRepayHeader header = receiptDetail.getRepayHeader();
					for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
						priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				}
				if (priAmt.compareTo(BigDecimal.ZERO) > 0) {
					limitManagement.processLoanRepayCancel(fm, customer, priAmt,
							StringUtils.trimToEmpty(fm.getProductCategory()));
				}
			}
		}

		ManualAdvise ma = rch.getManualAdvise();
		pd.setBounceCode(br.getReturnCode());
		pd.setBounceID(ma.getBounceID());
		pd.setManualAdviseId(ma.getAdviseID());

		pd.setErrorDesc(errorMsg);

		logger.debug(Literal.LEAVING);
		return pd;
	}

	private FinReceiptDetail getPDReceipt(List<FinReceiptDetail> rcdList) {
		if (CollectionUtils.isEmpty(rcdList)) {
			return null;
		}

		for (FinReceiptDetail item : rcdList) {
			if (ReceiptMode.PRESENTMENT.equals(item.getPaymentType())) {
				return item;
			}
		}
		return null;
	}

	private void prepareManualAdvise(FinReceiptHeader rch, FinReceiptDetail rcd, PresentmentDetail pd,
			BounceReason br) {
		logger.debug(Literal.ENTERING);

		Date appDate = pd.getAppDate();

		Rule rule = RuleConfigCache.getCacheRule(br.getRuleID());

		BigDecimal bounceAmt = BigDecimal.ZERO;
		if (rule != null) {
			bounceAmt = getBounceAmount(rch, rcd, pd, rule, br);
		}

		int finCcy = CurrencyUtil.getFormat(rch.getFinCcy());

		ManualAdvise ma = new ManualAdvise();
		ma.setAdviseType(AdviseType.RECEIVABLE.id());
		ma.setFinID(rch.getFinID());
		ma.setFinReference(rch.getReference());
		ma.setFeeTypeID(feeTypeDAO.getFeeTypeId(PennantConstants.FEETYPE_BOUNCE));
		ma.setSequence(0);
		ma.setAdviseAmount(PennantApplicationUtil.unFormateAmount(bounceAmt, finCcy));
		ma.setPaidAmount(BigDecimal.ZERO);
		ma.setWaivedAmount(BigDecimal.ZERO);
		ma.setRemarks(pd.getBounceRemarks());
		ma.setReceiptID(rch.getReceiptID());
		ma.setBounceID(br.getBounceID());
		ma.setValueDate(appDate);
		ma.setPostDate(appDate);
		ma.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ma.setLastMntBy(pd.getLastMntBy());
		ma.setVersion(ma.getVersion() + 1);

		rch.setManualAdvise(ma);

		logger.debug(Literal.LEAVING);
	}

	private BigDecimal getBounceAmount(FinReceiptHeader rch, FinReceiptDetail rcd, PresentmentDetail pd, Rule rule,
			BounceReason br) {
		String presentmentType = pd.getPresentmentType();
		Date appDate = pd.getAppDate();

		Map<String, Object> map = rcd.getDeclaredFieldValues();

		map.put("br_finType", rch.getFinType());
		map.put("br_dpdcount", DateUtil.getDaysBetween(rch.getReceiptDate(), appDate));
		map.put("br_presentmentType", presentmentType);
		map.put("br_bounceCode", br.getBounceCode());

		String sqlRule = StringUtils.trimToEmpty(rule.getSQLRule());
		Map<String, Object> eventMapping = getEventMapping(rch.getFinID(), sqlRule);

		if (!eventMapping.isEmpty()) {
			map.put("emptype", eventMapping.get("EMPTYPE"));
			map.put("branchcity", eventMapping.get("BRANCHCITY"));
			map.put("fincollateralreq", eventMapping.get("FINCOLLATERALREQ"));
			map.put("btloan", eventMapping.get("BTLOAN"));
			map.put("ae_businessvertical", eventMapping.get("BUSINESSVERTICAL"));
			map.put("ae_alwflexi", eventMapping.get("ALWFLEXI"));
			map.put("ae_finbranch", eventMapping.get("FINBRANCH"));
			map.put("ae_entitycode", eventMapping.get("ENTITYCODE"));
		}

		String finCcy = rch.getFinCcy();
		return (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, map, finCcy, RuleReturnType.DECIMAL);
	}

	private Map<String, Object> getEventMapping(long finID, String sqlRule) {
		if (sqlRule.contains("emptype") || sqlRule.contains("branchcity") || sqlRule.contains("fincollateralreq")
				|| sqlRule.contains("btloan") || sqlRule.contains("ae_businessvertical")
				|| sqlRule.contains("ae_alwflexi") || sqlRule.contains("ae_finbranch")
				|| sqlRule.contains("ae_entitycode")) {
			return financeMainDAO.getGLSubHeadCodes(finID);

		}
		return new HashMap<>();
	}

	private boolean isExcessUtilized(long receiptID) {
		FinExcessAmount excess = finExcessAmountDAO.getExcessAmountsByReceiptId(receiptID);

		if (excess == null) {
			return false;
		}

		BigDecimal utilizedAmt = excess.getUtilisedAmt();

		if (utilizedAmt.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		}

		return false;
	}

	private String reversalExcess(long receiptID, BigDecimal excessAmount) {
		String curStatus = finReceiptHeaderDAO.getReceiptModeStatus(receiptID);

		FinExcessAmount excess = finExcessAmountDAO.getExcessAmountsByReceiptId(receiptID);

		if (excess == null) {
			return null;
		}

		long excessID = excess.getExcessID();
		BigDecimal utilizedAmt = excess.getUtilisedAmt();
		BigDecimal reservedAmt = excess.getReservedAmt();

		if (RepayConstants.PAYSTATUS_DEPOSITED.equals(curStatus)) {
			if (reservedAmt.compareTo(excessAmount) < 0) {
				return ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null)).getMessage();
			}

			finExcessAmountDAO.deductExcessReserve(excessID, excessAmount);

			return null;
		}

		if (utilizedAmt.compareTo(BigDecimal.ZERO) > 0) {
			return ErrorUtil.getErrorDetail(new ErrorDetail("60219", "", null)).getMessage();
		}

		paymentHeaderService.cancelPaymentInstruction(receiptID);

		excess.setAmount(BigDecimal.ZERO);
		excess.setReservedAmt(BigDecimal.ZERO);
		excess.setUtilisedAmt(BigDecimal.ZERO);
		excess.setBalanceAmt(BigDecimal.ZERO);

		finExcessAmountDAO.updateExcess(excess);

		return null;
	}

	private void reversalManualAdvices(List<ManualAdviseMovements> advMovements, FinanceMain fm, long linkedTranID) {
		List<ManualAdviseMovements> receivableAdvMovements = new ArrayList<ManualAdviseMovements>();
		List<ManualAdviseMovements> payableMovements = new ArrayList<>();
		List<ManualAdviseMovements> waiverAdvMovements = new ArrayList<>();
		BigDecimal adviseAmount = BigDecimal.ZERO;

		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(fm);

		EventProperties eventProperties = fm.getEventProperties();

		for (ManualAdviseMovements mam : advMovements) {
			Long taxHeaderId = mam.getTaxHeaderId();

			if (taxHeaderId != null && taxHeaderId > 0) {
				TaxHeader taxHeader = taxHeaderDetailsDAO.getTaxHeaderDetailsById(taxHeaderId, "_AView");
				taxHeader.setHeaderId(taxHeaderId);
				List<Taxes> taxDetailById = taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, "_AView");
				taxHeader.setTaxDetails(taxDetailById);
				mam.setTaxHeader(taxHeader);
			}

			if ((mam.getPaidAmount().add(mam.getWaivedAmount())).compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			ManualAdvise advise = new ManualAdvise();
			advise.setAdviseID(mam.getAdviseID());

			TaxHeader taxHeader = mam.getTaxHeader();
			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();

			if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				for (Taxes taxes : taxDetails) {
					if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
						cgstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
						sgstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
						igstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
						ugstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
						cessTax = taxes;
					}
				}
			}

			// Paid Details
			advise.setPaidAmount(mam.getPaidAmount().negate());
			advise.setTdsPaid(mam.getTdsPaid().negate());
			advise.setPaidCGST(cgstTax.getPaidTax().negate());
			advise.setPaidSGST(sgstTax.getPaidTax().negate());
			advise.setPaidIGST(igstTax.getPaidTax().negate());
			advise.setPaidUGST(ugstTax.getPaidTax().negate());
			advise.setPaidCESS(cessTax.getPaidTax().negate());

			// Waived Details
			advise.setWaivedAmount(mam.getWaivedAmount().negate());
			advise.setWaivedCGST(cgstTax.getWaivedTax().negate());
			advise.setWaivedSGST(sgstTax.getWaivedTax().negate());
			advise.setWaivedIGST(igstTax.getWaivedTax().negate());
			advise.setWaivedUGST(ugstTax.getWaivedTax().negate());
			advise.setWaivedCESS(cessTax.getWaivedTax().negate());

			ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(mam.getAdviseID(), "_AView");

			if (StringUtils.isBlank(manualAdvise.getFeeTypeCode()) && manualAdvise.getBounceID() > 0) {
				FeeType boucneFeeType = null;

				if (eventProperties.isCacheLoaded()) {
					boucneFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(PennantConstants.FEETYPE_BOUNCE);
				} else {
					boucneFeeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
				}

				if (boucneFeeType == null) {
					throw new AppException(
							String.format("Fee Type code %s not found, please conatact system admin to configure.",
									PennantConstants.FEETYPE_BOUNCE));
				}

				mam.setFeeTypeCode(boucneFeeType.getFeeTypeCode());
				mam.setFeeTypeDesc(boucneFeeType.getFeeTypeDesc());
				mam.setTaxApplicable(boucneFeeType.isTaxApplicable());
				mam.setTaxComponent(boucneFeeType.getTaxComponent());

			} else {
				mam.setFeeTypeCode(manualAdvise.getFeeTypeCode());
				mam.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
				mam.setTaxApplicable(manualAdvise.isTaxApplicable());
				mam.setTaxComponent(manualAdvise.getTaxComponent());
			}

			boolean dueCreated = manualAdvise.isDueCreation();

			if (!dueCreated) {
				if (AdviseType.isReceivable(manualAdvise.getAdviseType())) {
					if (taxHeader != null) {
						mam.setDebitInvoiceId(taxHeader.getInvoiceID());
					}
					receivableAdvMovements.add(mam);
				} else {
					if (taxHeader != null) {
						mam.setDebitInvoiceId(taxHeader.getInvoiceID());
					}
					payableMovements.add(mam);
				}
			} else {
				if (mam.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0 && mam.isTaxApplicable()) {

					if (taxHeader != null) {
						mam.setDebitInvoiceId(taxHeader.getInvoiceID());
					}
					waiverAdvMovements.add(mam);

					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setLinkedTranId(linkedTranID);
					invoiceDetail.setFinanceDetail(financeDetail);
					invoiceDetail.setMovements(waiverAdvMovements);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
					invoiceDetail.setWaiver(true);

					this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

					waiverAdvMovements.clear();
				}
			}

			advise.setBalanceAmt((mam.getPaidAmount().add(mam.getWaivedAmount())));

			if (ProductUtil.isOverDraftChargeReq(fm)) {
				long feeType = overdrafLoanService.getOverdraftTxnChrgFeeType(fm.getFinType());
				if (feeType == manualAdvise.getFeeTypeID()) {
					adviseAmount = advise.getPaidAmount().add(adviseAmount);
				}
			}

			manualAdviseDAO.updateAdvPayment(advise, TableType.MAIN_TAB);
		}
	}

	private String procReceiptCancellation(FinReceiptHeader rch, String postBranch, FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		boolean alwSchdReversalByLog = false;
		boolean isRcdFound = false;

		long receiptID = rch.getReceiptID();
		EventProperties eventProperties = fm.getEventProperties();
		Date appDate = fm.getAppDate();

		if (appDate == null) {
			appDate = getAppDate(eventProperties);
		}

		long finID = rch.getFinID();
		String finReference = rch.getReference();

		boolean isBounceProcess = RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus());

		List<RepayScheduleDetail> rpySchedules = new ArrayList<>();
		List<RepayScheduleDetail> tRpySchedules = new ArrayList<>();
		long logKey = 0;
		BigDecimal totalPriAmount = BigDecimal.ZERO;
		List<FinReceiptDetail> receipts = sortReceiptDetails(rch.getReceiptDetails());

		FeeType penalityFeeType = null;
		long linkedTranID = postReversalTransactions(rch, appDate);
		String receiptMode = StringUtils.trimToEmpty(rch.getReceiptMode());

		if (CollectionUtils.isNotEmpty(rch.getReceiptDetails())) {
			postReversal(rch);
		}

		BigDecimal unRealizeAmz = BigDecimal.ZERO;
		BigDecimal unRealizeLpp = BigDecimal.ZERO;
		BigDecimal unRealizeLppGst = BigDecimal.ZERO;

		List<FinTaxIncomeDetail> taxIncomeList = new ArrayList<>();

		for (int i = receipts.size() - 1; i >= 0; i--) {
			FinReceiptDetail rcd = receipts.get(i);

			String payType = rcd.getPaymentType();
			if (ReceiptMode.PAYABLE.equals(payType)) {
				setTaxHeader(rcd);
			}

			if (isBounceProcess && (ReceiptMode.EXCESS.equals(payType) || ReceiptMode.EMIINADV.equals(payType)
					|| ReceiptMode.PAYABLE.equals(payType))) {
				continue;
			}

			if (alwSchdReversalByLog) {
				List<FinLogEntryDetail> list = finLogEntryDetailDAO.getFinLogEntryDetailList(finID, rcd.getLogKey());
				if (CollectionUtils.isNotEmpty(list)) {
					ErrorDetail ed = ErrorUtil.getErrorDetail(new ErrorDetail("60206", "", null),
							PennantConstants.default_Language);
					return ed.getMessage();
				}
			}

			long linkedTranId = 0;
			FinRepayHeader rpyHeader = rcd.getRepayHeader();
			isRcdFound = true;

			if (rpyHeader != null) {
				isRcdFound = true;
				linkedTranId = rpyHeader.getLinkedTranId();

				if (rpyHeader.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
					String erroMessage = reversalExcess(receiptID, rpyHeader.getExcessAmount());

					if (erroMessage != null) {
						return erroMessage;
					}
				}

				if (rpyHeader.getPriAmount().compareTo(BigDecimal.ZERO) > 0) {
					totalPriAmount = totalPriAmount.add(rpyHeader.getPriAmount());
				}

				if (linkedTranId > 0) {
					financeRepaymentsDAO.deleteRpyDetailbyLinkedTranId(linkedTranId, finID);
				}

				if (rpyHeader.getRepayScheduleDetails() != null && !rpyHeader.getRepayScheduleDetails().isEmpty()) {
					rpySchedules.addAll(rpyHeader.getRepayScheduleDetails());
					tRpySchedules.addAll(rpyHeader.getRepayScheduleDetails());
				}

				unRealizeAmz = unRealizeAmz.add(rpyHeader.getRealizeUnAmz());

				FinTaxIncomeDetail taxIncome = finODAmzTaxDetailDAO.getFinTaxIncomeDetail(receiptID, "LPP");

				if (taxIncome != null) {
					taxIncomeList.add(taxIncome);
					unRealizeLpp = unRealizeLpp.add(taxIncome.getReceivedAmount());
					unRealizeLppGst = unRealizeLppGst.add(CalculationUtil.getTotalGST(taxIncome));
				}
			}

			if (rcd.getLogKey() != 0 && rcd.getLogKey() != Long.MIN_VALUE) {
				FinLogEntryDetail detail = finLogEntryDetailDAO.getFinLogEntryDetailByLog(rcd.getLogKey());
				if (detail == null) {
					ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60207", "", null),
							PennantConstants.default_Language);
					return errorDetail.getMessage();
				}
				logKey = detail.getLogKey();
				detail.setReversalCompleted(true);
				finLogEntryDetailDAO.updateLogEntryStatus(detail);
			}

			List<ManualAdviseMovements> maList = manualAdviseDAO.getAdvMovementsByReceiptSeq(receiptID,
					rcd.getReceiptSeqID(), "_AView");

			if (CollectionUtils.isNotEmpty(maList)) {
				isRcdFound = true;
				reversalManualAdvices(maList, fm, linkedTranId);
			}
		}

		// Making Single Set of Repay Schedule Details and sent to
		// Rendering
		Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
		for (RepayScheduleDetail rpySchd : rpySchedules) {
			RepayScheduleDetail curRpySchd = null;
			if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
				curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
				curRpySchd.setPrincipalSchdPayNow(
						curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
				curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
				curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
				rpySchdMap.remove(rpySchd.getSchDate());
			} else {
				curRpySchd = rpySchd;
			}

			// Adding New Repay Schedule Object to Map after Summing
			// data
			rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
		}

		rpySchedules = sortRpySchdDetails(new ArrayList<>(rpySchdMap.values()));
		List<FinanceScheduleDetail> updateSchdList = new ArrayList<>();
		List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
		Map<String, FinanceScheduleDetail> schdMap = null;

		FinScheduleData schdData = null;
		if (!alwSchdReversalByLog) {
			schdMap = new HashMap<>();
			schdData = new FinScheduleData();
			schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
			schdData.setFinanceType(FinanceConfigCache.getFinanceType(fm.getFinType()));
			schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

			for (FinanceScheduleDetail schd : schdData.getFinanceScheduleDetails()) {
				schdMap.put(DateUtil.format(schd.getSchDate(), PennantConstants.DBDateFormat), schd);
			}
		}

		for (RepayScheduleDetail rpySchd : rpySchedules) {

			// Schedule Detail Reversals
			if (!alwSchdReversalByLog) {

				FinanceScheduleDetail curSchd = null;
				boolean schdUpdated = false;
				if (schdMap.containsKey(DateUtility.format(rpySchd.getSchDate(), PennantConstants.DBDateFormat))) {
					curSchd = schdMap.get(DateUtility.format(rpySchd.getSchDate(), PennantConstants.DBDateFormat));

					// Principal Payment
					if (rpySchd.getPrincipalSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
						curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().subtract(rpySchd.getPrincipalSchdPayNow()));
						curSchd.setSchPriPaid(false);
						schdUpdated = true;
					}

					// Profit Payment
					if (rpySchd.getProfitSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
						curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().subtract(rpySchd.getProfitSchdPayNow()));
						curSchd.setSchPftPaid(false);
						schdUpdated = true;
					}

					// TDS Payment
					if (rpySchd.getTdsSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
						curSchd.setTDSPaid(curSchd.getTDSPaid().subtract(rpySchd.getTdsSchdPayNow()));
						schdUpdated = true;
					}

					// Fee Detail Payment
					if (rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0) {
						curSchd.setSchdFeePaid(curSchd.getSchdFeePaid().subtract(rpySchd.getSchdFeePayNow()));
						schdUpdated = true;
					}

					// Prepare List Schedules which will be updated
					if (schdUpdated) {
						updateSchdList.add(curSchd);
					}
				}
			}

			// Overdue Recovery Details Reset Back to Original State ,
			// If any penalties Paid On this Repayments
			// Process
			// ============================================
			if (rpySchd.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0
					|| rpySchd.getLatePftSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
				finODDetailsDAO.updateReversals(finID, rpySchd.getSchDate(), rpySchd.getPenaltyPayNow(),
						rpySchd.getLatePftSchdPayNow());
			}

			// Update Fee Balance
			// ============================================
			if (rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0) {
				List<FinFeeScheduleDetail> feeList = finFeeScheduleDetailDAO.getFeeScheduleBySchDate(finReference,
						rpySchd.getSchDate());
				BigDecimal feebal = rpySchd.getSchdFeePayNow();
				for (int j = feeList.size() - 1; j >= 0; j--) {
					FinFeeScheduleDetail feeSchd = feeList.get(j);
					BigDecimal paidReverse = BigDecimal.ZERO;
					if (feebal.compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}
					if (feeSchd.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}

					if (feebal.compareTo(feeSchd.getPaidAmount()) > 0) {
						paidReverse = feeSchd.getPaidAmount();
					} else {
						paidReverse = feebal;
					}
					feebal = feebal.subtract(paidReverse);

					// Create list of updated objects to save one time
					FinFeeScheduleDetail updFeeSchd = new FinFeeScheduleDetail();
					updFeeSchd.setFeeID(feeSchd.getFeeID());
					updFeeSchd.setSchDate(feeSchd.getSchDate());
					updFeeSchd.setPaidAmount(paidReverse.negate());
					updateFeeList.add(updFeeSchd);
				}
			}

		}

		List<FinOverDueChargeMovement> dueMovements = finODCAmountDAO.getFinODCMovements(rch.getReceiptID());

		List<FinOverDueCharges> updatedODAmt = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(dueMovements)) {
			for (FinOverDueChargeMovement movement : dueMovements) {
				FinOverDueCharges odcAmount = new FinOverDueCharges();
				odcAmount.setPaidAmount(movement.getPaidAmount());
				odcAmount.setWaivedAmount(movement.getWaivedAmount());
				odcAmount.setId(movement.getChargeId());
				updatedODAmt.add(odcAmount);
			}

			finODCAmountDAO.updateReversals(updatedODAmt);
			finODCAmountDAO.updateMovenantStatus(rch.getReceiptID(), rch.getReceiptModeStatus());
		}

		// Schedule Details Updation
		if (!updateSchdList.isEmpty()) {
			financeScheduleDetailDAO.updateListForRpy(updateSchdList);
		}

		// Fee Schedule Details Updation
		if (!updateFeeList.isEmpty()) {
			finFeeScheduleDetailDAO.updateFeeSchdPaids(updateFeeList);
		}

		rpySchedules = null;
		rpySchdMap = null;
		updateSchdList = null;
		updateFeeList = null;

		// Deletion of Finance Schedule Related Details From Main Table
		FinanceProfitDetail pftDetail = profitDetailsDAO.getFinProfitDetailsById(finID);

		if (schdData != null) {
			schdData.setFinanceMain(fm);
			if (alwSchdReversalByLog) {
				listDeletion(finID, "", false, 0);

				// Fetching Last Log Entry Finance Details
				schdData = getFinSchDataByFinRef(finID, logKey, "_Log");
				schdData.setFinanceMain(fm);

				// Re-Insert Log Entry Data before Repayments Process
				// Recalculations
				listSave(schdData, "", 0);

				// Delete Data from Log Entry Tables After Inserting
				// into
				// Main Tables
				for (int i = 0; i < receipts.size(); i++) {
					listDeletion(finID, "_Log", false, receipts.get(i).getLogKey());
				}
			} else {
				schdData.setFinanceScheduleDetails(new ArrayList<>(schdMap.values()));
			}
		}

		// Update Profit Details for UnRealized Income & Late Payment
		// Difference
		pftDetail.setAmzTillLBD(pftDetail.getAmzTillLBD().subtract(unRealizeAmz));
		pftDetail.setLppTillLBD(pftDetail.getLppTillLBD().subtract(unRealizeLpp));
		pftDetail.setGstLppTillLBD(pftDetail.getGstLppTillLBD().subtract(unRealizeLppGst));

		Date valueDate = appDate;
		if (!ImplementationConstants.LPP_CALC_SOD) {
			valueDate = DateUtility.addDays(valueDate, -1);
		}
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(fm.getFinID());
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayList(fm.getFinID());
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

		// Check whether Accrual Reversal required for LPP or not
		if (unRealizeLpp.compareTo(BigDecimal.ZERO) > 0) {
			// prepare GST Invoice Report for Penalty reversal
			if (eventProperties.isCacheLoaded()) {
				penalityFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(PennantConstants.FEETYPE_ODC);
			} else {
				penalityFeeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_ODC);
			}

			if (penalityFeeType == null) {
				throw new AppException(
						String.format("Fee Type code %s not found, please conatact system admin to configure.",
								PennantConstants.FEETYPE_ODC));
			}

			ManualAdviseMovements incMovement = new ManualAdviseMovements();
			incMovement.setFeeTypeCode(penalityFeeType.getFeeTypeCode());
			incMovement.setFeeTypeDesc(penalityFeeType.getFeeTypeDesc());
			incMovement.setTaxApplicable(penalityFeeType.isTaxApplicable());
			incMovement.setTaxComponent(penalityFeeType.getTaxComponent());

			TaxHeader taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			if (taxHeader.getTaxDetails() == null) {
				taxHeader.setTaxDetails(new ArrayList<>());
			}

			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);

			Taxes cgstTax = getTaxDetail(RuleConstants.CODE_CGST, taxPercentages.get(RuleConstants.CODE_CGST));
			Taxes sgstTax = getTaxDetail(RuleConstants.CODE_SGST, taxPercentages.get(RuleConstants.CODE_SGST));
			Taxes igstTax = getTaxDetail(RuleConstants.CODE_IGST, taxPercentages.get(RuleConstants.CODE_IGST));
			Taxes ugstTax = getTaxDetail(RuleConstants.CODE_UGST, taxPercentages.get(RuleConstants.CODE_UGST));
			Taxes cessTax = getTaxDetail(RuleConstants.CODE_CESS, taxPercentages.get(RuleConstants.CODE_CESS));

			for (int i = 0; i < taxIncomeList.size(); i++) {
				cgstTax.setPaidTax(cgstTax.getPaidTax().add(taxIncomeList.get(i).getCGST()));
				sgstTax.setPaidTax(sgstTax.getPaidTax().add(taxIncomeList.get(i).getSGST()));
				igstTax.setPaidTax(igstTax.getPaidTax().add(taxIncomeList.get(i).getIGST()));
				ugstTax.setPaidTax(ugstTax.getPaidTax().add(taxIncomeList.get(i).getUGST()));
				cessTax.setPaidTax(cessTax.getPaidTax().add(taxIncomeList.get(i).getCESS()));
				incMovement.setMovementAmount(
						incMovement.getMovementAmount().add(taxIncomeList.get(i).getReceivedAmount()));
				incMovement.setPaidAmount(incMovement.getPaidAmount().add(taxIncomeList.get(i).getReceivedAmount()));
			}

			taxHeader.getTaxDetails().add(cgstTax);
			taxHeader.getTaxDetails().add(sgstTax);
			taxHeader.getTaxDetails().add(igstTax);
			taxHeader.getTaxDetails().add(ugstTax);
			taxHeader.getTaxDetails().add(cessTax);
			incMovement.setTaxHeader(taxHeader);

			if (incMovement.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				List<ManualAdviseMovements> movements = new ArrayList<>();
				movements.add(incMovement);
				FinanceDetail financeDetail = new FinanceDetail();
				financeDetail.getFinScheduleData().setFinanceMain(fm);

				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setLinkedTranId(linkedTranID);
				invoiceDetail.setFinanceDetail(financeDetail);
				invoiceDetail.setWaiver(false);
				invoiceDetail.setMovements(movements);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

				this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
			}

			Date rcptDate = rch.getReceiptDate();
			Date rcptMonthEndDate = DateUtility.getMonthEnd(rch.getReceiptDate());
			boolean accrualDiffPostReq = false;
			if (DateUtility.compare(rcptDate, rcptMonthEndDate) <= 0
					&& DateUtility.compare(appDate, rcptMonthEndDate) > 0) {
				accrualDiffPostReq = true;
			}

			// If No Accrual postings required,
			pftDetail.setLppTillLBD(pftDetail.getLppTillLBD().subtract(unRealizeLpp));
			pftDetail.setGstLppTillLBD(pftDetail.getGstLppTillLBD().subtract(unRealizeLppGst));

			// Total Paids - Income
			ManualAdviseMovements movement = new ManualAdviseMovements();
			movement.setFeeTypeCode(penalityFeeType.getFeeTypeCode());
			movement.setFeeTypeDesc(penalityFeeType.getFeeTypeDesc());
			movement.setTaxApplicable(penalityFeeType.isTaxApplicable());
			movement.setTaxComponent(penalityFeeType.getTaxComponent());

			Taxes cgstTaxLPP = getTaxDetail(RuleConstants.CODE_CGST, taxPercentages.get(RuleConstants.CODE_CGST));
			Taxes sgstTaxLPP = getTaxDetail(RuleConstants.CODE_SGST, taxPercentages.get(RuleConstants.CODE_SGST));
			Taxes igstTaxLPP = getTaxDetail(RuleConstants.CODE_IGST, taxPercentages.get(RuleConstants.CODE_IGST));
			Taxes ugstTaxLPP = getTaxDetail(RuleConstants.CODE_UGST, taxPercentages.get(RuleConstants.CODE_UGST));
			Taxes cessTaxLPP = getTaxDetail(RuleConstants.CODE_CESS, taxPercentages.get(RuleConstants.CODE_CESS));

			// Paid GST Calculations
			for (RepayScheduleDetail rpySchd : tRpySchedules) {
				// Total GST Amount
				if (rpySchd.getTaxHeader() != null) {
					List<Taxes> taxDetails = rpySchd.getTaxHeader().getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
								if (cgstTaxLPP == null) {
									cgstTaxLPP = taxes;
								} else {
									cgstTaxLPP.setPaidTax(cgstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
									cgstTaxLPP.setWaivedTax(cgstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
								}
							} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
								if (sgstTaxLPP == null) {
									sgstTaxLPP = taxes;
								} else {
									sgstTaxLPP.setPaidTax(sgstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
									sgstTaxLPP.setWaivedTax(sgstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
								}
							} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
								if (igstTaxLPP == null) {
									igstTaxLPP = taxes;
								} else {
									igstTaxLPP.setPaidTax(igstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
									igstTaxLPP.setWaivedTax(igstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
								}
							} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
								if (ugstTaxLPP == null) {
									ugstTaxLPP = taxes;
								} else {
									ugstTaxLPP.setPaidTax(ugstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
									ugstTaxLPP.setWaivedTax(ugstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
								}
							} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
								if (cessTaxLPP == null) {
									cessTaxLPP = taxes;
								} else {
									cessTaxLPP.setPaidTax(cessTaxLPP.getPaidTax().add(taxes.getPaidTax()));
									cessTaxLPP.setWaivedTax(cessTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
								}
							}
						}
					}
				}

				movement.setMovementAmount(
						movement.getMovementAmount().add(rpySchd.getPenaltyPayNow()).add(rpySchd.getWaivedAmt()));
				movement.setPaidAmount(movement.getPaidAmount().add(rpySchd.getPenaltyPayNow()));
				movement.setWaivedAmount(movement.getWaivedAmount().add(rpySchd.getWaivedAmt()));
			}

			FinTaxReceivable newTaxRcv = new FinTaxReceivable();
			newTaxRcv.setReceivableAmount(movement.getPaidAmount().subtract(incMovement.getPaidAmount()));
			newTaxRcv.setCGST(cgstTaxLPP.getPaidTax().subtract(cgstTax.getPaidTax()));
			newTaxRcv.setSGST(sgstTaxLPP.getPaidTax().subtract(sgstTax.getPaidTax()));
			newTaxRcv.setUGST(ugstTaxLPP.getPaidTax().subtract(ugstTax.getPaidTax()));
			newTaxRcv.setIGST(igstTaxLPP.getPaidTax().subtract(igstTax.getPaidTax()));
			newTaxRcv.setCESS(cessTaxLPP.getPaidTax().subtract(cessTax.getPaidTax()));
			newTaxRcv.setFinID(finID);
			newTaxRcv.setFinReference(finReference);

			if (accrualDiffPostReq) {
				Date dateValueDate = DateUtility.addDays(DateUtility.getMonthStart(valueDate), -1);
				latePayMarkingService.calPDOnBackDatePayment(fm, overdueList, dateValueDate,
						schdData.getFinanceScheduleDetails(), repayments, true, true);

				BigDecimal totalLPP = BigDecimal.ZERO;
				for (int i = 0; i < overdueList.size(); i++) {
					totalLPP = totalLPP.add(overdueList.get(i).getTotPenaltyAmt());
				}

				// Profit Details Recalculation Process
				pftDetail = accrualService.calProfitDetails(fm, schdData.getFinanceScheduleDetails(), pftDetail,
						appDate);
				pftDetail.setLppAmount(totalLPP);
				pftDetail = postReceiptCanAdjust(schdData, pftDetail, newTaxRcv, appDate, dateValueDate);

			} else {
				updateTaxReceivable(finID, false, newTaxRcv);
			}

		}

		// Check Current Finance Max Status For updation
		// ============================================

		FinEODEvent finEodEvent = new FinEODEvent();
		finEodEvent.setFinanceMain(schdData.getFinanceMain());
		finEodEvent.setFinanceScheduleDetails(schdData.getFinanceScheduleDetails());
		finEodEvent.setFinProfitDetail(pftDetail);

		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setEodValueDate(DateUtil.addDays(valueDate, -1));

		latePayMarkingService.findLatePay(finEodEvent, custEODEvent);

		// Status Updation
		repaymentPostingsUtil.updateStatus(fm, valueDate, schdData.getFinanceScheduleDetails(), pftDetail,
				finEodEvent.getFinODDetails(), null);

		// Overdue Details Updation after Recalculation with Current
		// Data
		if (finEodEvent.getFinODDetails() != null && !finEodEvent.getFinODDetails().isEmpty()) {
			List<FinODDetails> updateODlist = new ArrayList<>();
			List<FinODDetails> saveODlist = new ArrayList<>();

			for (FinODDetails od : finEodEvent.getFinODDetails()) {
				if (StringUtils.equals("I", od.getRcdAction())) {
					od.setLppDueTillDate(valueDate);
					od.setLppDueAmt(od.getTotPenaltyPaid().add(od.getTotWaived()));
					saveODlist.add(od);
				} else {
					updateODlist.add(od);
				}
			}

			if (!saveODlist.isEmpty()) {
				finODDetailsDAO.saveList(saveODlist);
			}
			if (!updateODlist.isEmpty()) {
				finODDetailsDAO.updateList(updateODlist);
			}
		}

		if (totalPriAmount.compareTo(BigDecimal.ZERO) > 0) {
			// Finance Main Details Update
			fm.setFinRepaymentAmount(fm.getFinRepaymentAmount().subtract(totalPriAmount));
			fm.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);
			fm.setFinIsActive(true);
			fm.setClosingStatus(null);
			fm.setWriteoffLoan(fm.isWriteoffLoan());

			financeMainDAO.updateRepaymentAmount(fm);
		}

		if (!isRcdFound && !SysParamUtil.isAllowed(SMTParameterConstants.CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION)) {
			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60208", "", null),
					PennantConstants.default_Language);
			return errorDetail.getMessage();
		} else {

			if (rch.getManualAdvise() != null) {

				// Bounce Charge Due Postings
				ManualAdvise manualAdvise = rch.getManualAdvise();
				BigDecimal advAmt = manualAdvise.getAdviseAmount();
				if (advAmt.compareTo(BigDecimal.ZERO) > 0) {

					if (manualAdvise != null && manualAdvise.getAdviseID() <= 0) {
						manualAdvise.setAdviseID(this.manualAdviseDAO.getNewAdviseID());
					}

					FeeType feeType = feeTypeDAO.getTaxDetailByCode(Allocation.BOUNCE);

					if (feeType != null && feeType.getAccountSetId() != null
							|| feeType.getAccountSetId() > 0 && feeType.isAmortzReq()) {
						PostingDTO postingDTO = new PostingDTO();

						postingDTO.setFinanceMain(fm);
						postingDTO.setUserBranch(postBranch);
						postingDTO.setValueDate(rch.getBounceDate());
						postingDTO.setManualAdvise(manualAdvise);
						postingDTO.setFeeType(feeType);

						if (feeType.isTaxApplicable()) {
							String taxType = feeType.getTaxComponent();
							postingDTO.setTaxHeader(GSTCalculator.prepareTaxHeader(fm, taxType, advAmt));
						}

						AccountingEngine.post(AccountingEvent.MANFEE, postingDTO);

						if (manualAdvise.getLinkedTranId() <= 0) {
							return "Accounting Engine Failed to Create Postings.";
						}
					}
				}

				manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				String adviseId = manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);
				manualAdvise.setAdviseID(Long.parseLong(adviseId));
			}

			// Update Receipt Details based on Receipt Mode
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
				if (!isBounceProcess || StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PRESENTMENT)
						|| (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.CHEQUE)
								|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.DD))) {
					finReceiptDetailDAO.updateReceiptStatus(receiptDetail.getReceiptID(),
							receiptDetail.getReceiptSeqID(), rch.getReceiptModeStatus());

					// Receipt Reversal for Excess or Payable
					if (!isBounceProcess) {

						if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
								|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
								|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.CASHCLT)
								|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.DSF)) {

							// Excess utilize Reversals
							finExcessAmountDAO.updateExcessAmount(receiptDetail.getPayAgainstID(), "U",
									receiptDetail.getAmount().negate());

						} else if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {

							// Payable Utilize reversals
							manualAdviseDAO.reverseUtilise(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
						}
					}
				}
			}

			// Accounting Execution Process for Deposit Reversal for CASH
			if (ImplementationConstants.DEPOSIT_PROC_REQ) {
				if (ReceiptMode.CASH.equals(receiptMode)) {

					DepositMovements movement = depositDetailsDAO.getDepositMovementsByReceiptId(receiptID, "_AView");
					if (movement != null) {
						// Find Amount of Deposited Request
						BigDecimal reqAmount = BigDecimal.ZERO;
						for (FinReceiptDetail rcptDetail : rch.getReceiptDetails()) {
							if (ReceiptMode.CASH.equals(rcptDetail.getPaymentType())) { // CASH
								reqAmount = reqAmount.add(rcptDetail.getAmount());
							}
						}

						// need to check accounting should be reversal or not
						// for
						// Bank To Cash
						/*
						 * AEEvent aeEvent = this.cashManagementAccounting.generateAccounting(
						 * AccountEventConstants.ACCEVENT_BANKTOCASH, movement.getBranchCode(),
						 * movement.getBranchCode(), movement.getReservedAmount(), movement.getPartnerBankId(),
						 * movement.getMovementId(), null);
						 */

						if (reqAmount.compareTo(BigDecimal.ZERO) > 0) {
							// DECRESE Available amount in Deposit Details
							depositDetailsDAO.updateActualAmount(movement.getDepositId(), reqAmount, false, "");

							// Movement update by Transaction Type to Reversal
							depositDetailsDAO.reverseMovementTranType(movement.getMovementId());
						}
					}
				}

				// Accounting Execution Process for Deposit Reversal for Cheque
				// / DD
				if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {

					// Verify Cheque or DD Details exists in Deposited Cheques
					DepositCheques depositCheque = depositChequesDAO.getDepositChequeByReceiptID(receiptID);

					if (depositCheque != null) {
						if (depositCheque.getLinkedTranId() > 0) {
							// Postings Reversal
							postingsPreparationUtil.postReversalsByLinkedTranID(depositCheque.getLinkedTranId());
							// Make Deposit Cheque to Reversal Status
							depositChequesDAO.reverseChequeStatus(depositCheque.getMovementId(), receiptID,
									depositCheque.getLinkedTranId());
						} else {
							logger.info("Postings Id is not available in deposit cheques");
							throw new InterfaceException("CHQ001", "Issue with deposit details postings prepartion.");
						}
					} else {
						// Available Decrease
						DepositMovements movement = depositDetailsDAO.getDepositMovementsByReceiptId(receiptID,
								"_AView");
						if (movement != null) {

							// Find Amount of Deposited Request
							BigDecimal reqAmount = BigDecimal.ZERO;
							for (FinReceiptDetail rcptDetail : rch.getReceiptDetails()) {
								if (ReceiptMode.CHEQUE.equals(rcptDetail.getPaymentType())
										|| ReceiptMode.DD.equals(rcptDetail.getPaymentType())) { // Cheque/DD
									reqAmount = reqAmount.add(rcptDetail.getAmount());
								}
							}

							// DECRESE Available amount in Deposit Details
							depositDetailsDAO.updateActualAmount(movement.getDepositId(), reqAmount, false, "");

							// Movement update by Transaction Type to Reversal
							depositDetailsDAO.reverseMovementTranType(movement.getDepositId());
						}
					}
				}
			}
		}

		return null;
	}

	private String procFeeReceiptCancellation(FinReceiptHeader receiptHeader) {
		logger.debug(Literal.ENTERING);

		// Posting Reversal Case Program Calling in Equation
		// ============================================
		long postingId = postingsDAO.getPostingId();
		List<ReturnDataSet> returnDataSetList = postingsPreparationUtil
				.postReversalsByPostRef(String.valueOf(receiptHeader.getReceiptID()), postingId, null);

		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			for (ReturnDataSet returnDataSet : returnDataSetList) {
				FinRepayHeader rpyHeader = receiptDetail.getRepayHeader();
				rpyHeader.setLinkedTranId(returnDataSet.getLinkedTranId());
			}
		}

		// Update Receipt Detail Status
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			finReceiptDetailDAO.updateReceiptStatus(receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(),
					receiptHeader.getReceiptModeStatus());
		}

		return null;
	}

	/**
	 * Method for Updating tax Receivable against Finance Reference
	 * 
	 * @param finReference
	 * @param accrualDiffPostReq
	 */
	private void updateTaxReceivable(long finID, boolean accrualDiffPostReq, FinTaxReceivable newTaxRcv) {

		// Receivable details Updation
		boolean isSaveRcv = false;
		FinTaxReceivable taxRcv = finODAmzTaxDetailDAO.getFinTaxReceivable(finID, "LPP");

		// if receipt done before month end and Already month end crossed with
		// paids,
		// Now Old receipt which was done before month end came for cancellation
		if (taxRcv == null && accrualDiffPostReq) {
			isSaveRcv = true;
			taxRcv = new FinTaxReceivable();
			taxRcv.setFinID(finID);
			taxRcv.setFinReference(newTaxRcv.getFinReference());
			taxRcv.setTaxFor("LPP");
		}

		// Update Receivable receipts for future accounting
		if (taxRcv != null) {

			// Update Receivable Tax details to make future postings correctly
			taxRcv.setReceivableAmount(taxRcv.getReceivableAmount().add(newTaxRcv.getReceivableAmount()));
			taxRcv.setCGST(taxRcv.getCGST().add(newTaxRcv.getCGST()));
			taxRcv.setSGST(taxRcv.getSGST().add(newTaxRcv.getSGST()));
			taxRcv.setUGST(taxRcv.getUGST().add(newTaxRcv.getUGST()));
			taxRcv.setIGST(taxRcv.getIGST().add(newTaxRcv.getIGST()));

			if (isSaveRcv) {
				finODAmzTaxDetailDAO.saveTaxReceivable(taxRcv);
			} else {
				finODAmzTaxDetailDAO.updateTaxReceivable(taxRcv);
			}
		}

	}

	private FinanceProfitDetail postReceiptCanAdjust(FinScheduleData scheduleData, FinanceProfitDetail profitDetail,
			FinTaxReceivable newTaxRcv, Date appDate, Date valueDate) {
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Accrual Difference Postings
		Long accountingID = AccountingConfigCache.getCacheAccountSetID(financeMain.getFinType(), AccountingEvent.AMZ,
				FinanceConstants.MODULEID_FINTYPE);

		EventProperties eventProperties = financeMain.getEventProperties();

		Date derivedAppDate = null;

		if (eventProperties.isParameterLoaded()) {
			derivedAppDate = eventProperties.getAppDate();
		} else {
			derivedAppDate = SysParamUtil.getAppDate();
		}

		if (accountingID != null && accountingID != Long.MIN_VALUE) {

			Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(financeMain.getFinID());

			FinanceDetail detail = new FinanceDetail();
			detail.setFinScheduleData(scheduleData);
			Map<String, BigDecimal> taxPercmap = GSTCalculator.getTaxPercentages(gstExecutionMap,
					financeMain.getFinCcy());

			FeeType lppFeeType = null;

			if (eventProperties.isCacheLoaded()) {
				lppFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(Allocation.ODC);
			} else {
				lppFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.ODC);
			}

			// Calculate LPP GST Amount
			if (profitDetail.getLppAmount().compareTo(BigDecimal.ZERO) > 0 && lppFeeType != null
					&& lppFeeType.isTaxApplicable() && lppFeeType.isAmortzReq()) {
				BigDecimal gstAmount = getTotalTaxAmount(taxPercmap, profitDetail.getLppAmount(),
						lppFeeType.getTaxComponent());
				profitDetail.setGstLppAmount(gstAmount);
			}

			AEEvent aeEvent = AEAmounts.procCalAEAmounts(financeMain, profitDetail,
					scheduleData.getFinanceScheduleDetails(), AccountingEvent.AMZ, derivedAppDate, derivedAppDate);

			// UnAccrual amount should not be zero in case of "UMFC" accounting
			aeEvent.getAeAmountCodes().setdAmz(BigDecimal.ZERO);
			aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());

			BigDecimal unLPPAmz = aeEvent.getAeAmountCodes().getdLPPAmz();
			BigDecimal unGstLPPAmz = aeEvent.getAeAmountCodes().getdGSTLPPAmz();

			if (gstExecutionMap != null) {
				for (String key : gstExecutionMap.keySet()) {
					if (StringUtils.isNotBlank(key)) {
						aeEvent.getDataMap().put(key, gstExecutionMap.get(key));
					}
				}
			}

			// LPI GST Amount for Postings
			Map<String, BigDecimal> calGstMap = new HashMap<>();
			boolean addGSTInvoice = false;

			// LPP GST Amount for Postings
			if (aeEvent.getAeAmountCodes().getdGSTLPPAmz().compareTo(BigDecimal.ZERO) > 0 && lppFeeType != null
					&& lppFeeType.isTaxApplicable()) {

				FinODAmzTaxDetail odTaxDetail = getTaxDetail(taxPercmap, aeEvent.getAeAmountCodes().getdGSTLPPAmz(),
						lppFeeType.getTaxComponent());

				odTaxDetail.setFinReference(profitDetail.getFinReference());
				odTaxDetail.setTaxFor("LPP");
				odTaxDetail.setAmount(aeEvent.getAeAmountCodes().getdLPPAmz());
				odTaxDetail.setValueDate(valueDate);

				calGstMap.put("LPP_CGST_R", odTaxDetail.getCGST());
				calGstMap.put("LPP_SGST_R", odTaxDetail.getSGST());
				calGstMap.put("LPP_UGST_R", odTaxDetail.getUGST());
				calGstMap.put("LPP_IGST_R", odTaxDetail.getIGST());
				calGstMap.put("LPP_CESS_R", odTaxDetail.getCESS());

				newTaxRcv.setReceivableAmount(
						newTaxRcv.getReceivableAmount().add(aeEvent.getAeAmountCodes().getdLPPAmz()));
				newTaxRcv.setCGST(newTaxRcv.getCGST().add(odTaxDetail.getCGST()));
				newTaxRcv.setSGST(newTaxRcv.getSGST().add(odTaxDetail.getSGST()));
				newTaxRcv.setUGST(newTaxRcv.getUGST().add(odTaxDetail.getUGST()));
				newTaxRcv.setIGST(newTaxRcv.getIGST().add(odTaxDetail.getIGST()));
				newTaxRcv.setCESS(newTaxRcv.getCESS().add(odTaxDetail.getCESS()));

				// Save Tax Details
				finODAmzTaxDetailDAO.save(odTaxDetail);

				if (eventProperties.isParameterLoaded()) {
					addGSTInvoice = eventProperties.isGstInvOnDue();
				} else {
					addGSTInvoice = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
				}
			} else {
				addZeroifNotContains(calGstMap, "LPP_CGST_R");
				addZeroifNotContains(calGstMap, "LPP_SGST_R");
				addZeroifNotContains(calGstMap, "LPP_UGST_R");
				addZeroifNotContains(calGstMap, "LPP_IGST_R");
				addZeroifNotContains(calGstMap, "LPP_CESS_R");
			}

			// GST Details
			if (calGstMap != null) {
				aeEvent.getDataMap().putAll(calGstMap);
			}

			aeEvent.getAcSetIDList().add(accountingID);

			// Amortization Difference Postings
			postingsPreparationUtil.postAccounting(aeEvent);

			// GST Invoice Preparation
			if (aeEvent.getLinkedTranId() > 0) {

				// GST Invoice Generation
				if (addGSTInvoice) {
					List<FinFeeDetail> feesList = prepareFeesList(lppFeeType, taxPercmap, calGstMap, aeEvent);
					if (CollectionUtils.isNotEmpty(feesList)) {
						InvoiceDetail invoiceDetail = new InvoiceDetail();
						invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
						invoiceDetail.setFinanceDetail(detail);
						invoiceDetail.setFinFeeDetailsList(feesList);
						invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
						invoiceDetail.setOrigination(false);
						invoiceDetail.setWaiver(false);
						invoiceDetail.setDbInvSetReq(false);

						this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
					}
				}
			}

			// Update Tax Receivables
			updateTaxReceivable(financeMain.getFinID(), true, newTaxRcv);

			// Unadjusted Posting amount addition
			profitDetail.setLppTillLBD(profitDetail.getLppTillLBD().add(unLPPAmz));
			profitDetail.setGstLppTillLBD(profitDetail.getGstLppTillLBD().add(unGstLPPAmz));
		}

		return profitDetail;

	}

	private List<FinFeeDetail> prepareFeesList(FeeType lppFeeType, Map<String, BigDecimal> taxPercMap,
			Map<String, BigDecimal> calGstMap, AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetailsList = new ArrayList<FinFeeDetail>();
		FinFeeDetail finFeeDetail = null;

		// LPP Fees
		if (lppFeeType != null) {
			finFeeDetail = new FinFeeDetail();
			TaxHeader taxHeader = new TaxHeader();
			finFeeDetail.setTaxHeader(taxHeader);

			finFeeDetail.setFeeTypeCode(lppFeeType.getFeeTypeCode());
			finFeeDetail.setFeeTypeDesc(lppFeeType.getFeeTypeDesc());
			finFeeDetail.setTaxApplicable(true);
			finFeeDetail.setOriginationFee(false);
			finFeeDetail.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPPAmz());

			if (taxPercMap != null && calGstMap != null) {
				Taxes cgstTax = new Taxes();
				Taxes sgstTax = new Taxes();
				Taxes igstTax = new Taxes();
				Taxes ugstTax = new Taxes();
				Taxes cessTax = new Taxes();

				cgstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_CGST));
				sgstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_SGST));
				igstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_IGST));
				ugstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_UGST));
				cessTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_CESS));

				cgstTax.setTaxType(RuleConstants.CODE_CGST);
				sgstTax.setTaxType(RuleConstants.CODE_SGST);
				igstTax.setTaxType(RuleConstants.CODE_IGST);
				ugstTax.setTaxType(RuleConstants.CODE_UGST);
				cessTax.setTaxType(RuleConstants.CODE_CESS);

				cgstTax.setNetTax(calGstMap.get("LPP_CGST_R"));
				sgstTax.setNetTax(calGstMap.get("LPP_SGST_R"));
				igstTax.setNetTax(calGstMap.get("LPP_IGST_R"));
				ugstTax.setNetTax(calGstMap.get("LPP_UGST_R"));
				cessTax.setNetTax(calGstMap.get("LPP_CESS_R"));

				taxHeader.getTaxDetails().add(cgstTax);
				taxHeader.getTaxDetails().add(sgstTax);
				taxHeader.getTaxDetails().add(igstTax);
				taxHeader.getTaxDetails().add(ugstTax);
				taxHeader.getTaxDetails().add(cessTax);

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(lppFeeType.getTaxComponent())) {
					BigDecimal gstAmount = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax())
							.add(ugstTax.getNetTax()).add(cessTax.getNetTax());
					finFeeDetail.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPPAmz().subtract(gstAmount));
				}
			}

			finFeeDetailsList.add(finFeeDetail);
		}

		logger.debug(Literal.LEAVING);
		return finFeeDetailsList;
	}

	private FinODAmzTaxDetail getTaxDetail(Map<String, BigDecimal> taxPercmap, BigDecimal actTaxAmount,
			String taxType) {

		BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
		BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
		BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
		BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
		BigDecimal cessPerc = taxPercmap.get(RuleConstants.CODE_CESS);
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc).add(cessPerc);

		FinODAmzTaxDetail taxDetail = new FinODAmzTaxDetail();
		taxDetail.setTaxType(taxType);
		BigDecimal totalGST = BigDecimal.ZERO;

		if (cgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal cgstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, cgstPerc, totalGSTPerc);
			taxDetail.setCGST(cgstAmount);
			totalGST = totalGST.add(cgstAmount);
		}

		if (sgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal sgstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, sgstPerc, totalGSTPerc);
			taxDetail.setSGST(sgstAmount);
			totalGST = totalGST.add(sgstAmount);
		}

		if (ugstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal ugstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, ugstPerc, totalGSTPerc);
			taxDetail.setUGST(ugstAmount);
			totalGST = totalGST.add(ugstAmount);
		}

		if (igstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal igstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, igstPerc, totalGSTPerc);
			taxDetail.setIGST(igstAmount);
			totalGST = totalGST.add(igstAmount);
		}

		if (cessPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal cessAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, cessPerc, totalGSTPerc);
			taxDetail.setCESS(cessAmount);
			totalGST = totalGST.add(cessAmount);
		}

		taxDetail.setTotalGST(totalGST);

		return taxDetail;
	}

	/**
	 * Method for Setting default Value to Zero
	 * 
	 * @param dataMap
	 * @param key
	 */
	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	/**
	 * Method for Calculating Total GST Amount with the Requested Amount
	 */
	private BigDecimal getTotalTaxAmount(Map<String, BigDecimal> taxPercmap, BigDecimal amount, String taxType) {
		logger.debug(Literal.ENTERING);

		TaxAmountSplit taxSplit = null;
		BigDecimal gstAmount = BigDecimal.ZERO;

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			taxSplit = GSTCalculator.getExclusiveGST(amount, taxPercmap);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			taxSplit = GSTCalculator.getInclusiveGST(amount, taxPercmap);
		}

		if (taxSplit != null) {
			gstAmount = taxSplit.gettGST();
		}

		logger.debug(Literal.LEAVING);

		return gstAmount;
	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param rpySchdList
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> rpySchdList) {

		if (rpySchdList != null && rpySchdList.size() > 1) {
			Collections.sort(rpySchdList, new Comparator<>() {
				@Override
				public int compare(RepayScheduleDetail rpySchd1, RepayScheduleDetail rpySchd2) {
					return DateUtil.compare(rpySchd1.getSchDate(), rpySchd2.getSchDate());
				}
			});
		}

		return rpySchdList;
	}

	public List<ReceiptCancelDetail> sortReceipts(List<ReceiptCancelDetail> receipts) {
		if (CollectionUtils.isNotEmpty(receipts)) {
			Collections.sort(receipts, new Comparator<>() {
				@Override
				public int compare(ReceiptCancelDetail detail1, ReceiptCancelDetail detail2) {
					if (detail1.getReceiptId() > detail2.getReceiptId()) {
						return 1;
					} else if (detail1.getReceiptId() < detail2.getReceiptId()) {
						return -1;
					}
					return 0;
				}
			});
		}

		return receipts;
	}

	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts) {
		if (receipts == null || receipts.size() < 1) {
			return new ArrayList<>();
		}

		Collections.sort(receipts, new Comparator<FinReceiptDetail>() {
			@Override
			public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
				if (detail1.getPayOrder() > detail2.getPayOrder()) {
					return 1;
				} else if (detail1.getPayOrder() < detail2.getPayOrder()) {
					return -1;
				}
				return 0;
			}
		});
		return receipts;
	}

	private FinScheduleData getFinSchDataByFinRef(long finID, long logKey, String type) {
		logger.debug(Literal.ENTERING);
		FinScheduleData schdData = new FinScheduleData();
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false, logKey));
		schdData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false, logKey));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false, logKey));
		logger.debug(Literal.LEAVING);
		return schdData;
	}

	private void listDeletion(long finID, String tableType, boolean isWIF, long logKey) {
		logger.debug(Literal.ENTERING);
		financeScheduleDetailDAO.deleteByFinReference(finID, tableType, isWIF, logKey);
		financeDisbursementDAO.deleteByFinReference(finID, tableType, isWIF, logKey);
		repayInstructionDAO.deleteByFinReference(finID, tableType, isWIF, logKey);
		logger.debug(Literal.LEAVING);
	}

	private void listSave(FinScheduleData schdData, String tableType, long logKey) {
		logger.debug("Entering ");
		Map<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinanceScheduleDetail schd : schdData.getFinanceScheduleDetails()) {
			schd.setLastMntBy(fm.getLastMntBy());
			schd.setFinID(finID);
			schd.setFinReference(finReference);
			int seqNo = 0;

			if (mapDateSeq.containsKey(schd.getSchDate())) {
				seqNo = mapDateSeq.get(schd.getSchDate());
				mapDateSeq.remove(schd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(schd.getSchDate(), seqNo);
			schd.setSchSeq(seqNo);
			schd.setLogKey(logKey);
		}
		financeScheduleDetailDAO.saveList(schdData.getFinanceScheduleDetails(), tableType, false);

		// Schedule Version Updating
		if (StringUtils.isBlank(tableType)) {
			financeMainDAO.updateSchdVersion(fm, false);
		}

		// Finance Disbursement Details
		mapDateSeq = new HashMap<>();
		Date curBDay = SysParamUtil.getAppDate();
		for (FinanceDisbursement dd : schdData.getDisbursementDetails()) {
			dd.setFinID(finID);
			dd.setFinReference(finReference);
			dd.setDisbReqDate(curBDay);
			dd.setDisbIsActive(true);
			dd.setLogKey(logKey);
			dd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			dd.setLastMntBy(schdData.getFinanceMain().getLastMntBy());
		}
		financeDisbursementDAO.saveList(schdData.getDisbursementDetails(), tableType, false);

		// Finance Repay Instruction Details
		for (RepayInstruction ri : schdData.getRepayInstructions()) {
			ri.setFinID(finID);
			ri.setFinReference(finReference);
			ri.setLogKey(logKey);
		}
		repayInstructionDAO.saveList(schdData.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}

	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	public void deleteTaxHeaderId(long receiptId, String type) {
		logger.debug(Literal.ENTERING);

		List<Long> headerIds = taxHeaderDetailsDAO.getHeaderIdsByReceiptId(receiptId, type);

		if (CollectionUtils.isNotEmpty(headerIds)) {
			for (Long headerId : headerIds) {
				if (headerId != null && headerId > 0) {
					taxHeaderDetailsDAO.delete(headerId, type);
					TaxHeader taxHeader = new TaxHeader();
					taxHeader.setHeaderId(headerId);
					taxHeaderDetailsDAO.delete(taxHeader, type);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void processFinFeeDetails(FinReceiptHeader rch) {
		List<FinFeeDetail> paidFeeList = rch.getPaidFeeList();

		if (CollectionUtils.isEmpty(paidFeeList)) {
			return;
		}

		String userBranch = rch.getUserDetails().getBranchCode();
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(rch.getCustID(), rch.getFinCcy(),
				userBranch, rch.getFinBranch());

		BigDecimal excessAmt = BigDecimal.ZERO;
		for (FinFeeDetail finFeeDetail : paidFeeList) {
			FinFeeDetail tempfinFee = new FinFeeDetail();
			BeanUtils.copyProperties(finFeeDetail, tempfinFee);
			excessAmt = excessAmt.add(finFeeDetail.getFinFeeReceipts().get(0).getPaidAmount());
			calculateGST(tempfinFee, taxPercentages);
			finFeeDetail.setTaxHeader(tempfinFee.getTaxHeader());
			tempfinFee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finFeeDetailService.updateFeesFromUpfront(tempfinFee, "_Temp");
		}

		for (FinFeeDetail finFeeDetail : rch.getPaidFeeList()) {
			if (finFeeDetail.isTaxApplicable() && finFeeDetail.getTaxHeader() != null) {
				List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();
				for (Taxes taxes : taxDetails) {
					taxHeaderDetailsDAO.update(taxes, "_Temp");
				}
			}
		}

		List<FinReceiptDetail> receiptdetails = rch.getReceiptDetails();
		FinReceiptDetail receiptdetail = receiptdetails.get(0);
		FinRepayHeader repayHeader = receiptdetail.getRepayHeader();

		processGSTInvoicePreparation(rch, repayHeader.getLinkedTranId(), taxPercentages);

		excessAmt = rch.getReceiptAmount().subtract(excessAmt);
		processExcessAmount(excessAmt, rch.getFinID(), rch.getReceiptID());

	}

	/**
	 * Method for calculate GST for Details for the given allocated amount.
	 * 
	 * @param receiptHeader
	 */
	public void calculateGST(FinFeeDetail finFeeDetail, Map<String, BigDecimal> taxPercentages) {
		FinFeeReceipt finFeeReceipt = finFeeDetail.getFinFeeReceipts().get(0);
		BigDecimal canlFeeAmt = finFeeReceipt.getPaidAmount();
		BigDecimal canlTdsAmt = finFeeReceipt.getPaidTds();
		BigDecimal remPaid = BigDecimal.ZERO;
		BigDecimal remTds = BigDecimal.ZERO;
		remPaid = finFeeDetail.getPaidAmount().subtract(canlFeeAmt);
		remTds = finFeeDetail.getPaidTDS().subtract(canlTdsAmt);

		finFeeDetail.setPaidAmount(remPaid);
		finFeeDetail.setPaidAmountOriginal(remPaid);
		finFeeDetail.setPaidTDS(remTds);
		FinanceMain financeMain = null;
		finFeeDetail.setPaidCalcReq(true);
		finFeeDetail.setPrvTaxComponent(finFeeDetail.getTaxComponent());
		finFeeDetail.setTaxComponent(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
		feeReceiptService.calculateFees(finFeeDetail, financeMain, taxPercentages);
	}

	private void processGSTInvoicePreparation(FinReceiptHeader rch, long linkedTranId,
			Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);

		long finID = rch.getFinID();
		List<FinFeeDetail> finFeeDetailsList = rch.getPaidFeeList();
		Long oldLinkTranId = rch.getLinkedTranId();

		calculateGSTForCredit(finFeeDetailsList, taxPercentages, oldLinkTranId);
		FinanceDetail fd = new FinanceDetail();
		FinanceMain fm = this.financeMainDAO.getFinanceMainById(finID, "_Temp", false);
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(fm.getFinType()));

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(fd);
		invoiceDetail.setFinFeeDetailsList(finFeeDetailsList);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
		invoiceDetail.setOrigination(false);
		invoiceDetail.setWaiver(true);
		invoiceDetail.setDbInvSetReq(true);

		Long dueInvoiceID = gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (dueInvoiceID == null) {
					dueInvoiceID = finFeeDetail.getTaxHeader().getInvoiceID();
				}
				taxHeader.setInvoiceID(dueInvoiceID);
				this.taxHeaderDetailsDAO.update(taxHeader, "_Temp");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void processExcessAmount(BigDecimal excessAmt, long finID, long receiptId) {
		logger.debug(Literal.ENTERING);

		if (excessAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		FinExcessAmount excess = finExcessAmountDAO.getExcessAmountsByReceiptId(finID,
				RepayConstants.EXCESSADJUSTTO_EXCESS, receiptId);

		if (excess != null) {
			excess.setBalanceAmt(excess.getBalanceAmt().subtract(excessAmt));
			excess.setAmount(excess.getAmount().subtract(excessAmt));
			finExcessAmountDAO.updateExcess(excess);

			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excess.getExcessID());
			excessMovement.setAmount(excessAmt);
			excessMovement.setReceiptID(receiptId);
			excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
			excessMovement.setTranType(AccountConstants.TRANTYPE_DEBIT);
			excessMovement.setMovementFrom("UPFRONT");

			finExcessAmountDAO.saveExcessMovement(excessMovement);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for calculate GST for Details for the given allocated amount.
	 * 
	 * @param receiptHeader
	 */
	public void calculateGSTForCredit(List<FinFeeDetail> finFeeDetailsList, Map<String, BigDecimal> taxPercentages,
			Long oldLinkTranId) {

		if (oldLinkTranId <= 0) {
			return;
		}

		Long invoiceId = gstInvoiceTxnDAO.getInvoiceIdByTranId(oldLinkTranId);

		if (invoiceId == null) {
			return;
		}

		List<GSTInvoiceTxnDetails> txnDetailsList = gstInvoiceTxnDAO.getTxnListByInvoiceId(invoiceId);
		for (GSTInvoiceTxnDetails txnDetails : txnDetailsList) {
			for (FinFeeDetail finFeeDetail : finFeeDetailsList) {

				if (!StringUtils.equals(finFeeDetail.getFeeTypeCode(), txnDetails.getFeeCode())) {
					continue;
				}

				finFeeDetail.setWaivedAmount(txnDetails.getFeeAmount());
				TaxHeader taxHeader = finFeeDetail.getTaxHeader();
				Taxes cgstTax = null;
				Taxes sgstTax = null;
				Taxes igstTax = null;
				Taxes ugstTax = null;
				Taxes cessTax = null;

				if (taxHeader == null) {
					taxHeader = new TaxHeader();
					taxHeader.setNewRecord(true);
					taxHeader.setRecordType(PennantConstants.RCD_ADD);
					taxHeader.setVersion(taxHeader.getVersion() + 1);
					finFeeDetail.setTaxHeader(taxHeader);
				}

				List<Taxes> taxDetails = taxHeader.getTaxDetails();

				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						String taxType = taxes.getTaxType();
						switch (taxType) {
						case RuleConstants.CODE_CGST:
							cgstTax = taxes;
							cgstTax.setWaivedTax(txnDetails.getCGST_AMT());
							break;
						case RuleConstants.CODE_SGST:
							sgstTax = taxes;
							sgstTax.setWaivedTax(txnDetails.getSGST_AMT());
							break;
						case RuleConstants.CODE_IGST:
							igstTax = taxes;
							igstTax.setWaivedTax(txnDetails.getIGST_AMT());
							break;
						case RuleConstants.CODE_UGST:
							ugstTax = taxes;
							ugstTax.setWaivedTax(txnDetails.getUGST_AMT());
							break;
						case RuleConstants.CODE_CESS:
							cessTax = taxes;
							cessTax.setWaivedTax(txnDetails.getCESS_AMT());
							break;
						default:
							break;
						}

					}
				}

				BigDecimal gstAmount = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax()).add(igstTax.getWaivedTax())
						.add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());
				finFeeDetail.setWaivedGST(gstAmount);

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equalsIgnoreCase(finFeeDetail.getTaxComponent())) {
					finFeeDetail.setWaivedAmount(txnDetails.getFeeAmount().add(finFeeDetail.getWaivedGST()));
				}
			}
		}
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(long finID) {
		return this.financeMainDAO.getGLSubHeadCodes(finID);
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param receiptHeader
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			FinReceiptHeader receiptHeader, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			documentDetails.setReferenceId(String.valueOf(receiptHeader.getId()));
			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}
			if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				boolean isTempRecord = false;
				if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(receiptHeader.getLastMntBy());
				documentDetails.setWorkflowId(0);

				if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
					approveRec = true;
				}

				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(documentDetails.getRecordType())) {
					deleteRecord = true;
					isTempRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(documentDetails.getRecordType())) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(documentDetails.getRecordType())) {
					updateRecord = true;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(documentDetails.getRecordType())) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetails.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = documentDetails.getRecordType();
					recordStatus = documentDetails.getRecordStatus();
					documentDetails.setRecordType("");
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(String.valueOf(receiptHeader.getId()));
					}
					documentDetails.setFinEvent(FinServiceEvent.RECEIPT);
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() == null) {
						saveDocument(DMSModule.FINANCE, DMSModule.RECEIPT, documentDetails);
						documentDetailsDAO.save(documentDetails, type);
					}
					if (documentDetails.getDocId() < 0) {
						documentDetails.setDocId(Long.MIN_VALUE);
					}
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() == null) {
						saveDocument(DMSModule.FINANCE, DMSModule.RECEIPT, documentDetails);
						documentDetailsDAO.update(documentDetails, type);
					}
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						documentDetailsDAO.delete(documentDetails, type);
					}
				}

				if (approveRec) {
					documentDetails.setFinEvent("");
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	// Method for Deleting all records related to receipt in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(FinReceiptHeader finReceiptHeader, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();

		// Document Details.
		List<AuditDetail> documentDetails = finReceiptHeader.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			DocumentDetails documentDetail = null;
			List<DocumentDetails> docList = new ArrayList<DocumentDetails>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				documentDetail = (DocumentDetails) documentDetails.get(i).getModelData();
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				docList.add(documentDetail);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetail.getBefImage(),
						documentDetail));
			}
			documentDetailsDAO.deleteList(docList, tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinReceiptHeader finReceiptHeader = repayData.getReceiptHeader();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finReceiptHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (financeDetail != null) {
			if (financeDetail.getExtendedFieldRender() != null) {
				auditDetailMap.put("ExtendedFieldDetails",
						extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
								financeDetail.getExtendedFieldRender(), auditTranType, method,
								ExtendedFieldConstants.MODULE_LOAN));
				auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
			}

			ExtendedFieldExtension extendedFieldExtension = financeDetail.getExtendedFieldExtension();
			if (extendedFieldExtension != null) {
				auditDetailMap.put("ExtendedFieldExtension", extendedFieldExtensionService
						.setExtendedFieldExtAuditData(extendedFieldExtension, auditTranType, method));
				financeDetail.setAuditDetailMap(auditDetailMap);
				auditDetails.addAll(auditDetailMap.get("ExtendedFieldExtension"));
			}
			financeDetail.setAuditDetailMap(auditDetailMap);
			repayData.setFinanceDetail(financeDetail);
		}

		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	public List<AuditDetail> setDocumentDetailsAuditData(FinReceiptHeader finReceiptHeader, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
		for (int i = 0; i < finReceiptHeader.getDocumentDetails().size(); i++) {
			DocumentDetails documentDetails = finReceiptHeader.getDocumentDetails().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(finReceiptHeader.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finReceiptHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(finReceiptHeader.getRecordStatus());
			documentDetails.setUserDetails(finReceiptHeader.getUserDetails());
			documentDetails.setLastMntOn(finReceiptHeader.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public AuditHeader doApproveNonLanReceipt(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		Date appDate = SysParamUtil.getAppDate();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		FinReceiptData orgReceiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData receiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

		// Finance Repayment Cancellation Posting Process Execution
		// =====================================
		long postingId = postingsDAO.getPostingId();
		postingsPreparationUtil.postReversalsByPostRef(String.valueOf(receiptHeader.getReceiptID()), postingId,
				appDate);
		String errorCode = "";
		long recSeqId = 0;

		if (StringUtils.isNotBlank(errorCode)) {
			throw new InterfaceException("9999", errorCode);
		}

		// Receipt Header Updation
		// =======================================

		if (!FinServiceEvent.FEEPAYMENT.equals(receiptHeader.getReceiptPurpose())) {
			String recType = receiptHeader.getRecordType();
			tranType = PennantConstants.TRAN_UPD;
			receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			receiptHeader.setRecordType("");
			receiptHeader.setRoleCode("");
			receiptHeader.setNextRoleCode("");
			receiptHeader.setTaskId("");
			receiptHeader.setNextTaskId("");
			receiptHeader.setWorkflowId(0);
			receiptHeader.setRcdMaintainSts(null);

			if (PennantConstants.RECORD_TYPE_NEW.equals(recType)) {
				finReceiptHeaderDAO.save(receiptHeader, TableType.MAIN_TAB);
				for (FinReceiptDetail finRecpt : receiptHeader.getReceiptDetails()) {
					recSeqId = finReceiptDetailDAO.save(finRecpt, TableType.MAIN_TAB);

				}
			} else {
				finReceiptHeaderDAO.update(receiptHeader, TableType.MAIN_TAB);
				for (FinReceiptDetail finRecpt : receiptHeader.getReceiptDetails()) {
					recSeqId = finRecpt.getReceiptSeqID();
					finReceiptDetailDAO.updateReceiptStatusByReceiptId(finRecpt.getReceiptID(),
							receiptHeader.getReceiptModeStatus());

				}
			}
		}

		ManualAdvise advise = manualAdviseDAO.getManualAdviseByReceiptId(receiptHeader.getReceiptID(), "");
		if (receiptHeader.getManualAdvise() != null) {
			if (advise == null) {
				manualAdviseDAO.save(receiptHeader.getManualAdvise(), TableType.MAIN_TAB);
			} else {
				manualAdviseDAO.update(receiptHeader.getManualAdvise(), TableType.MAIN_TAB);
			}

			ManualAdviseMovements movement = new ManualAdviseMovements();
			movement.setAdviseID(receiptHeader.getManualAdvise().getAdviseID());
			movement.setMovementDate(SysParamUtil.getAppDate());
			movement.setMovementAmount(receiptHeader.getManualAdvise().getAdviseAmount());
			movement.setPaidAmount(receiptHeader.getManualAdvise().getAdviseAmount());
			movement.setWaivedAmount(BigDecimal.ZERO);
			movement.setReceiptID(receiptHeader.getReceiptID());
			movement.setReceiptSeqID(recSeqId);
			movement.setWaiverID(0);
			long zero = 0;
			movement.setInsReceiptID(zero);
			movement.setPaidCGST(BigDecimal.ZERO);
			movement.setPaidIGST(BigDecimal.ZERO);
			movement.setPaidSGST(BigDecimal.ZERO);
			movement.setPaidUGST(BigDecimal.ZERO);

			// saving record in Manual Advise Movements table
			manualAdviseDAO.saveMovement(movement, "");
		}

		// Bounce Reason Code
		if (receiptHeader.getManualAdvise() != null) {
			manualAdviseDAO.deleteByAdviseId(receiptHeader.getManualAdvise(), TableType.TEMP_TAB);
		}

		finReceiptDetailDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB.getSuffix());
		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		// Preparing Before Image for Audit
		FinReceiptHeader befRctHeader = null;
		if (!PennantConstants.RECORD_TYPE_NEW.equals(orgReceiptData.getReceiptHeader().getRecordType())) {
			befRctHeader = (FinReceiptHeader) aAuditHeader.getAuditDetail().getBefImage();
		}
		List<FinReceiptDetail> befFinReceiptDetail = new ArrayList<>();
		if (befRctHeader != null) {
			befFinReceiptDetail = befRctHeader.getReceiptDetails();
		}

		// Audit Header for WorkFlow Image
		List<AuditDetail> tempAuditDetailList = new ArrayList<>();
		// FinReceiptDetail Audit Details Preparation
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				receiptData.getReceiptHeader().getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
			tempAuditDetailList.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rFields[0], rFields[1], null,
					orgReceiptData.getReceiptHeader().getReceiptDetails().get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
				receiptData.getReceiptHeader().getExcludeFields());
		aAuditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], null,
				orgReceiptData.getReceiptHeader()));

		// Adding audit as deleted from TEMP table
		aAuditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		aAuditHeader.setAuditDetails(tempAuditDetailList);
		aAuditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(aAuditHeader);

		if (orgReceiptData.getReceiptHeader().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		// Audit Header for Before and After Image
		List<AuditDetail> auditDetails = new ArrayList<>();
		// FinReceiptDetail Audit Details Preparation
		if (befFinReceiptDetail.isEmpty()) {
			for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], null,
						receiptData.getReceiptHeader().getReceiptDetails().get(i)));
			}
		} else {
			for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], befFinReceiptDetail.get(i),
						receiptData.getReceiptHeader().getReceiptDetails().get(i)));
			}
		}

		// FinReceiptHeader Audit
		auditHeader.setAuditDetail(
				new AuditDetail(tranType, 1, rhFields[0], rhFields[1], befRctHeader, receiptData.getReceiptHeader()));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode,
			String bounceRemarks) {
		return null;
	}

	@Override
	public FinanceMain getFinBasicDetails(String reference) {
		return this.financeMainDAO.getFinanceMainByRef(reference, TableType.VIEW.getSuffix(), false);
	}

	private void setRepayHeader(List<FinReceiptDetail> receiptDetails, List<FinRepayHeader> repayHeader) {
		for (FinReceiptDetail rcd : receiptDetails) {
			for (FinRepayHeader finRepayHeader : repayHeader) {
				if (finRepayHeader.getReceiptSeqID() == rcd.getReceiptSeqID()) {
					rcd.setRepayHeader(finRepayHeader);
				}
			}
		}
	}

	private void setRepaySchedules(long finID, List<FinRepayHeader> repayHeader) {
		List<RepayScheduleDetail> repaySchedules = financeRepaymentsDAO.getRpySchdList(finID, "");

		if (CollectionUtils.isEmpty(repaySchedules)) {
			return;
		}

		for (FinRepayHeader header : repayHeader) {
			for (RepayScheduleDetail rSchedule : repaySchedules) {
				if (header.getRepayID() == rSchedule.getRepayID()) {
					header.getRepayScheduleDetails().add(rSchedule);
				}
			}
		}
	}

	private List<AuditDetail> addExtendedFieldData(String method, FinReceiptData repayData) {
		List<AuditDetail> list = new ArrayList<>();

		FinanceDetail fd = repayData.getFinanceDetail();

		if (fd == null) {
			return list;
		}

		String usrLanguage = repayData.getReceiptHeader().getUserDetails().getLanguage();

		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = fd.getExtendedFieldHeader();
			list.addAll(extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage));
		}

		if (fd.getExtendedFieldExtension() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.vaildateDetails(details, usrLanguage);
			list.addAll(details);
		}

		return list;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		getAuditDetails(auditHeader, method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		auditDetails.addAll(addExtendedFieldData(method, repayData));

		for (AuditDetail ad : auditDetails) {
			auditHeader.setErrorList(ad.getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail ad, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		ad.setErrorDetails(new ArrayList<>());
		FinReceiptData rd = (FinReceiptData) ad.getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();

		validate(ad, usrLanguage, method);

		if (FinServiceEvent.FEEPAYMENT.equals(rch.getReceiptPurpose())
				&& RepayConstants.RECEIPTTO_FINANCE.equals(rch.getRecAgainst())
				&& !financeMainDAO.isFinReferenceExists(rch.getReference(), "_Temp", false)) {
			ad.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60209", null)));
		}

		if (isExcessUtilized(rch.getReceiptID())) {
			ad.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60219", "", null)));
		}

		if (ImplementationConstants.DEPOSIT_PROC_REQ && ReceiptMode.CASH.equals(rch.getReceiptMode())) {
			validateDeposits(ad, usrLanguage, method, rch);
		}

		long receiptid = tdsReceivablesTxnService.getPendingReceipt(rch.getReceiptID(), TableType.TEMP_TAB);
		if (receiptid == rch.getReceiptID()) {

			StringBuilder message = new StringBuilder("The Receipt : ");
			message.append(rch.getReceiptID());
			message.append(" is in TDS adjustments queue.Do you want to continue?");

			if (MessageUtil.confirm(message.toString()) == MessageUtil.YES) {
				this.tdsReceivablesTxnService.deleteTxnByReceiptId(receiptid);
				logger.debug(Literal.LEAVING);
				return ad;
			} else {
				ad.setErrorDetail(new ErrorDetail("90290", null));
			}
		}

		ad.setErrorDetails(ErrorUtil.getErrorDetails(ad.getErrorDetails(), usrLanguage));

		return ad;
	}

	private void validate(AuditDetail ad, String usrLanguage, String method) {
		FinReceiptData rd = (FinReceiptData) ad.getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();

		FinReceiptHeader tempReceiptHeader = null;

		if (rch.isWorkflow()) {
			tempReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(rch.getReceiptID(), "_Temp");
		}

		FinReceiptHeader beFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(rch.getReceiptID(), "");

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(rch.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		if (rch.isNewRecord()) {
			validateWorkFlow(ad, usrLanguage, tempReceiptHeader, beFinReceiptHeader);
		} else {
			validateNotWorkflow(ad, usrLanguage, tempReceiptHeader, beFinReceiptHeader);
		}

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !rch.isWorkflow()) {
			rch.setBefImage(beFinReceiptHeader);
		}
	}

	private void validateNotWorkflow(AuditDetail ad, String usrLanguage, FinReceiptHeader tempRCH,
			FinReceiptHeader befRCH) {
		FinReceiptData rd = (FinReceiptData) ad.getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();

		FinReceiptHeader oldRCH = rch.getBefImage();

		String errorCode = "";
		if (!rch.isWorkflow()) {
			if (befRCH == null) {
				errorCode = "41002";
			} else {
				errorCode = validateTranType(ad.getAuditTranType(), befRCH, oldRCH);
			}
		} else {
			if (tempRCH == null || (oldRCH != null && !oldRCH.getLastMntOn().equals(tempRCH.getLastMntOn()))) {
				errorCode = "41005";
			}
		}

		if (!errorCode.isBlank()) {
			setError(ad, usrLanguage, errorCode, rch.getReceiptID());
		}

	}

	private String validateTranType(String tranType, FinReceiptHeader befRCH, FinReceiptHeader oldRCH) {
		if (oldRCH == null || oldRCH.getLastMntOn().equals(befRCH.getLastMntOn())) {
			return "";
		}

		if (PennantConstants.TRAN_DEL.equalsIgnoreCase(tranType)) {
			return "41003";
		}

		return "41004";
	}

	private void validateWorkFlow(AuditDetail ad, String usrLanguage, FinReceiptHeader tempRCH,
			FinReceiptHeader befRCH) {
		FinReceiptData rd = (FinReceiptData) ad.getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();
		String errorCode = "";

		if (!rch.isWorkflow()) {
			if (befRCH != null) {
				errorCode = "41001";
			}
		} else {
			if (rch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				if (befRCH != null || tempRCH != null) {
					errorCode = "41001";
				}
			} else {
				if (befRCH == null || tempRCH != null) {
					errorCode = "41005";
				}
			}
		}

		setError(ad, usrLanguage, errorCode, rch.getReceiptID());
	}

	private void validateDeposits(AuditDetail ad, String usrLanguage, String method, FinReceiptHeader rch) {
		if (PennantConstants.method_doReject.equals(method)
				|| PennantConstants.RCD_STATUS_RESUBMITTED.equals(rch.getRecordStatus())) {
			return;
		}

		DepositMovements movement = depositDetailsDAO.getDepositMovementsByReceiptId(rch.getReceiptID(), "_AView");
		if (movement != null) {
			BigDecimal reqAmount = BigDecimal.ZERO;
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				if (ReceiptMode.CASH.equals(rcd.getPaymentType())) {
					reqAmount = reqAmount.add(rcd.getAmount());
				}
			}

			DepositDetails deposits = depositDetailsDAO.getDepositDetailsById(movement.getDepositId(), "");

			if (deposits != null && reqAmount.compareTo(deposits.getActualAmount()) > 0) {
				ad.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65036", null), usrLanguage));
			}
		}
	}

	private void setTaxHeader(FinReceiptDetail rcd) {
		long payAgainstID = rcd.getPayAgainstID();

		if (payAgainstID == 0) {
			return;
		}
		ManualAdviseMovements advMov = manualAdviseDAO.getAdvMovByReceiptSeq(rcd.getReceiptID(), rcd.getReceiptSeqID(),
				rcd.getPayAgainstID(), "");

		if (advMov == null) {
			return;
		}

		Long headerID = advMov.getTaxHeaderId();
		if (headerID != null) {
			TaxHeader header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(headerID, "");
			if (header != null) {
				header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(headerID, ""));
			}

			advMov.setTaxHeader(header);
		}

		rcd.setPayAdvMovement(advMov);
	}

	private void postReversal(FinReceiptHeader rch) {
		String receiptMode = StringUtils.trimToEmpty(rch.getReceiptMode());

		if (ReceiptMode.EXCESS.equals(receiptMode)) {
			return;
		}

		for (FinReceiptDetail detail : rch.getReceiptDetails()) {
			String paymentType = detail.getPaymentType();

			if (receiptMode.equals(paymentType)) {
				String receiptNumber = detail.getPaymentRef();

				if (StringUtils.isNotBlank(receiptNumber)) {
					List<Long> tranIdList = finStageAccountingLogDAO.getTranIdListByReceipt(receiptNumber);

					for (Long stageLinkTranID : tranIdList) {
						postingsPreparationUtil.postReversalsByLinkedTranID(stageLinkTranID);
					}

					if (CollectionUtils.isNotEmpty(tranIdList)) {
						finStageAccountingLogDAO.deleteByReceiptNo(receiptNumber);
					}
				}
			}
		}
	}

	private Date getAppDate(EventProperties eventProperties) {
		if (eventProperties.isParameterLoaded()) {
			return eventProperties.getAppDate();
		}

		return SysParamUtil.getAppDate();
	}

	private long postReversalTransactions(FinReceiptHeader rh, Date appDate) {
		String receiptID = String.valueOf(rh.getReceiptID());
		String receiptPurpose = rh.getReceiptPurpose();
		String receiptMode = rh.getReceiptMode();
		List<ReturnDataSet> rdSet = null;

		if (FinServiceEvent.EARLYRPY.equals(receiptPurpose)) {
			return 0;
		}

		if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
			return 0;
		}

		long postingId = postingsDAO.getPostingId();

		if (ImplementationConstants.PRESENTMENT_STAGE_ACCOUNTING_REQ) {
			if (!ReceiptMode.PRESENTMENT.equals(receiptMode)) {
				rdSet = postingsPreparationUtil.postReversalsByPostRef(receiptID, postingId, appDate);
			}
		} else {
			rdSet = postingsPreparationUtil.postReversalsByPostRef(receiptID, postingId, appDate);
		}

		if (CollectionUtils.isNotEmpty(rdSet)) {
			return rdSet.get(0).getLinkedTranId();
		}

		return 0;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	private String getEventCode(String receiptPurpose) {
		switch (receiptPurpose) {
		case FinServiceEvent.SCHDRPY:
			return AccountingEvent.REPAY;
		case FinServiceEvent.EARLYRPY:
			return AccountingEvent.EARLYPAY;
		case FinServiceEvent.EARLYSETTLE:
			return AccountingEvent.EARLYSTL;
		default:
			return "";
		}
	}

	private void saveFSI(FinReceiptHeader rch, FinanceMain fm, AuditHeader auditHeader, TableType tableType) {
		Date appDate = fm.getAppDate();
		Date sysDate = DateUtil.getSysDate();

		List<FinReceiptDetail> rcDtls = rch.getReceiptDetails();
		int size = rcDtls.size();
		FinRepayHeader rph = rcDtls.get(size - 1).getRepayHeader();

		FinServiceInstruction instruction = new FinServiceInstruction();
		instruction.setFinID(fm.getFinID());
		instruction.setFinReference(fm.getFinReference());
		instruction.setFinEvent(rch.getReceiptPurpose());
		instruction.setAmount(rch.getReceiptAmount());
		instruction.setAppDate(appDate);
		instruction.setSystemDate(sysDate);
		instruction.setMaker(auditHeader.getAuditUsrId());
		instruction.setMakerAppDate(appDate);
		instruction.setMakerSysDate(sysDate);
		instruction.setReference(String.valueOf(rch.getReceiptID()));

		if (StringUtils.isEmpty(rch.getNextRoleCode())) {
			instruction.setChecker(auditHeader.getAuditUsrId());
			instruction.setCheckerAppDate(appDate);
			instruction.setCheckerSysDate(sysDate);
			if (rph != null) {
				instruction.setLinkedTranID(rph.getNewLinkedTranId());
			}
		}

		finServiceInstructionDAO.save(instruction, tableType.getSuffix());
	}

	private void setError(AuditDetail ad, String usrLanguage, String code, long receiptID) {
		if (StringUtils.isEmpty(code)) {
			return;
		}

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(receiptID);
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		setError(ad, usrLanguage, code, errParm, valueParm);
	}

	private void setError(AuditDetail ad, String code, String usrLanguage, String[] errParm, String[] valueParm) {
		ErrorDetail ed = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, code, errParm, valueParm),
				usrLanguage);

		ad.setErrorDetail(ed);
	}

	@Autowired
	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}

	@Autowired
	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

	@Autowired
	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	@Autowired
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	@Autowired
	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired(required = false)
	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	@Autowired
	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	@Autowired
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired(required = false)
	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setFinODCAmountDAO(FinODCAmountDAO finODCAmountDAO) {
		this.finODCAmountDAO = finODCAmountDAO;
	}
}