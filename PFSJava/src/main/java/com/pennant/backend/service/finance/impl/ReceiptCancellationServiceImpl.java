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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
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
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
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
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
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
import com.pennant.pff.eod.cache.BounceConfigCache;
import com.pennant.pff.eod.cache.FeeTypeConfigCache;
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptCancellationServiceImpl extends GenericFinanceDetailService implements ReceiptCancellationService {
	private static final Logger logger = LogManager.getLogger(ReceiptCancellationServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private LimitManagement limitManagement;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private DepositChequesDAO depositChequesDAO;
	private DepositDetailsDAO depositDetailsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private LatePayMarkingService latePayMarkingService;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private PresentmentDetailDAO presentmentDetailDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FeeReceiptService feeReceiptService;
	private AuditHeaderDAO auditHeaderDAO;
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldExtensionService extendedFieldExtensionService;
	private FinanceMainDAO financeMainDAO;

	public ReceiptCancellationServiceImpl() {
		super();
	}

	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment) {
		logger.debug(Literal.ENTERING);

		String tableType = "_View";
		if (isFeePayment) {
			tableType = "_FCView";
		}

		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, tableType);

		if (rch == null) {
			return null;
		}

		long finID = rch.getFinID();

		List<FinReceiptDetail> rcdList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_AView");
		rch.setReceiptDetails(rcdList);

		List<FinRepayHeader> rpyHeaderList = financeRepaymentsDAO.getFinRepayHeadersByRef(finID, "");

		// Fetch List of Repay Schedules
		if (!isFeePayment) {
			List<RepayScheduleDetail> rpySchList = financeRepaymentsDAO.getRpySchdList(finID, "");
			if (rpySchList != null && !rpySchList.isEmpty()) {
				for (FinRepayHeader finRepayHeader : rpyHeaderList) {
					for (RepayScheduleDetail repaySchd : rpySchList) {
						if (finRepayHeader.getRepayID() == repaySchd.getRepayID()) {
							finRepayHeader.getRepayScheduleDetails().add(repaySchd);
						}
					}
				}
			}
		}

		// Repay Headers setting to Receipt Details
		for (FinReceiptDetail rcd : rcdList) {
			for (FinRepayHeader finRepayHeader : rpyHeaderList) {
				if (finRepayHeader.getReceiptSeqID() == rcd.getReceiptSeqID()) {
					rcd.setRepayHeader(finRepayHeader);
				}
			}
		}
		rch.setReceiptDetails(rcdList);

		// Bounce reason Code
		if ((StringUtils.isNotEmpty(rch.getRecordType())
				&& RepayConstants.MODULETYPE_BOUNCE.equals(rch.getReceiptModeStatus()))
				|| (isFeePayment && RepayConstants.RECEIPTMODE_CHEQUE.equalsIgnoreCase(rch.getReceiptMode()))) {
			rch.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_TView"));
		}

		if (isFeePayment) {
			rch.setPaidFeeList(feeReceiptService.getPaidFinFeeDetails(rch.getReference(), receiptID, "_View"));

		}

		// Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(receiptID),
				PennantConstants.FEE_DOC_MODULE_NAME, FinServiceEvent.RECEIPT, "_View");
		if (CollectionUtils.isNotEmpty(rch.getDocumentDetails())) {
			rch.getDocumentDetails().addAll(documentList);
		} else {
			rch.setDocumentDetails(documentList);
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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
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
						render.getSeqNo(), "_Temp", auditTranType, extendedDetails));
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception {
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
		if (!SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)
				&& finReceiptHeader == null) {
			fd = rd.getFinanceDetail();
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			processSuccessPostings(rch, "", fm);
		}

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
			} else {
				finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);
				for (FinReceiptDetail finRecpt : rch.getReceiptDetails()) {
					finReceiptDetailDAO.save(finRecpt, TableType.MAIN_TAB);

				}
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

	private boolean processSuccessPostings(FinReceiptHeader receiptHeader, String postBranch, FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes = new AEAmountCodes();
		String paymentType = "";
		String partnerBankActype = "";
		String partnerbank = "";
		for (FinReceiptDetail recDtl : receiptHeader.getReceiptDetails()) {
			if (recDtl.getPayAgainstID() > 0) {
				continue;
			}
			partnerbank = recDtl.getPartnerBankAc();
			paymentType = recDtl.getPaymentType();
			partnerBankActype = recDtl.getPartnerBankAcType();
		}
		long postingId = postingsDAO.getPostingId();
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setFinType(fm.getFinType());
		aeEvent.setPromotion(fm.getPromotionCode());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(receiptHeader.getCashierBranch());
		aeEvent.setLinkedTranId(0);
		aeEvent.setAccountingEvent(AccountingEvent.REPAY);
		aeEvent.setValueDate(receiptHeader.getValueDate());
		aeEvent.setPostRefId(receiptHeader.getReceiptID());
		aeEvent.setPostingId(postingId);
		aeEvent.setEntityCode(fm.getEntityCode());

		amountCodes.setUserBranch(receiptHeader.getCashierBranch());
		amountCodes.setFinType(fm.getFinType());
		amountCodes.setPartnerBankAc(partnerbank);
		amountCodes.setPartnerBankAcType(partnerBankActype);
		amountCodes.setToExcessAmt(BigDecimal.ZERO);
		amountCodes.setToEmiAdvance(BigDecimal.ZERO);
		amountCodes.setPaymentType(paymentType);

		String eventCode = "";

		if (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinServiceEvent.SCHDRPY)) {
			eventCode = AccountingEvent.REPAY;

		} else if (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
			eventCode = AccountingEvent.EARLYPAY;

		} else if (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE)) {
			eventCode = AccountingEvent.EARLYSTL;

		}

		aeEvent.getAcSetIDList().clear();

		aeEvent.getAcSetIDList().add(
				AccountingConfigCache.getAccountSetID(fm.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));
		amountCodes.setFinType(fm.getFinType());

		HashMap<String, Object> extDataMap = (HashMap<String, Object>) amountCodes.getDeclaredFieldValues();
		extDataMap.put("PB_ReceiptAmount", receiptHeader.getReceiptAmount());
		amountCodes.setManualTds(receiptHeader.getTdsAmount());
		aeEvent.setDataMap(extDataMap);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		logger.debug(Literal.LEAVING);
		return aeEvent.isPostingSucess();
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();

		if (financeDetail != null) {
			String usrLanguage = repayData.getReceiptHeader().getUserDetails().getLanguage();
			if (financeDetail.getExtendedFieldRender() != null) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
				ExtendedFieldHeader extHeader = financeDetail.getExtendedFieldHeader();
				details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
				auditDetails.addAll(details);
			}

			if (financeDetail.getExtendedFieldExtension() != null) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldExtension");
				details = extendedFieldExtensionService.vaildateDetails(details, usrLanguage);
				auditDetails.addAll(details);
			}
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
		FinReceiptData rd = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();

		FinReceiptHeader tempReceiptHeader = null;
		if (rch.isWorkflow()) {
			tempReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(rch.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(rch.getReceiptID(), "");
		FinReceiptHeader oldReceiptHeader = rch.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(rch.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		if (rch.isNewRecord()) { // for New record or new record into work
									// flow

			if (!rch.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in
													// the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (rch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (beFinReceiptHeader != null || tempReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (beFinReceiptHeader == null || tempReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!rch.isWorkflow()) { // With out Work flow for update
				// and delete

				if (beFinReceiptHeader == null) { // if records not exists in
													// the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldReceiptHeader != null
							&& !oldReceiptHeader.getLastMntOn().equals(beFinReceiptHeader.getLastMntOn())) {
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

				if (tempReceiptHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempReceiptHeader != null && oldReceiptHeader != null
						&& !oldReceiptHeader.getLastMntOn().equals(tempReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Fee Payment Cancellation or Bounce cancellation stopped When Loan is
		// not in Workflow Process
		if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.FEEPAYMENT)) {
			if (RepayConstants.RECEIPTTO_FINANCE.equals(rch.getRecAgainst())) {
				boolean rcdAvailable = financeMainDAO.isFinReferenceExists(rch.getReference(), "_Temp", false);
				if (!rcdAvailable) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60209", null), usrLanguage));
				}
			}
		}

		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			if (!PennantConstants.method_doReject.equals(method)
					&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(rch.getRecordStatus())
					&& RepayConstants.RECEIPTMODE_CASH.equals(rch.getReceiptMode())) {

				DepositMovements movement = depositDetailsDAO.getDepositMovementsByReceiptId(rch.getReceiptID(),
						"_AView");
				if (movement != null) {
					// Find Amount of Deposited Request
					BigDecimal reqAmount = BigDecimal.ZERO;
					for (FinReceiptDetail rcptDetail : rch.getReceiptDetails()) {
						if (RepayConstants.RECEIPTMODE_CASH.equals(rcptDetail.getPaymentType())) { // CASH
							reqAmount = reqAmount.add(rcptDetail.getAmount());
						}
					}
					// getDepositDetailsById
					DepositDetails depositDetails = depositDetailsDAO.getDepositDetailsById(movement.getDepositId(),
							"");

					// if deposit Details amount is less than Requested amount
					// throw
					// an error
					if (depositDetails != null && reqAmount.compareTo(depositDetails.getActualAmount()) > 0) {
						auditDetail
								.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65036", null), usrLanguage));
					}
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !rch.isWorkflow()) {
			rch.setBefImage(beFinReceiptHeader);
		}

		return auditDetail;
	}

	@Override
	public PresentmentDetail presentmentCancellation(PresentmentDetail pd, CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = custEODEvent.getFinEODEvents().get(0).getFinanceMain();
		Customer customer = custEODEvent.getCustomer();

		String bounceCode = StringUtils.trimToNull(pd.getBounceCode());
		String bounceRemarks = pd.getBounceRemarks();

		FinReceiptHeader receiptHeader = getFinReceiptHeaderById(pd.getReceiptID(), false);

		if (receiptHeader == null) {
			pd.setErrorDesc(PennantJavaUtil.getLabel("label_FinReceiptHeader_Notavailable"));
			return pd;
		}

		if (bounceCode == null) {
			pd.setErrorDesc("Bounc Code is mandatory..");
			return pd;
		}

		BounceReason bounceReason = BounceConfigCache.getCacheBounceReason(bounceCode);
		if (bounceReason == null) {
			pd.setErrorDesc(PennantJavaUtil.getLabel("label_BounceReason_Notavailable") + bounceCode);
			return pd;
		}

		FinReceiptDetail finReceiptDetail = null;
		if (receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail item : receiptHeader.getReceiptDetails()) {
				if (item.getPaymentType().equals(RepayConstants.RECEIPTMODE_PRESENTMENT)) {
					finReceiptDetail = item;
					break;
				}
			}
		}

		if (finReceiptDetail == null) {
			pd.setErrorDesc(PennantJavaUtil.getLabel("label_FinReceiptDetails_Notavailable") + pd.getMandateType());
			return pd;
		}

		ManualAdvise manualAdvise = getManualAdvise(receiptHeader, bounceReason, finReceiptDetail,
				pd.getPresentmentType(), bounceRemarks, pd.getAppDate());

		if (manualAdvise == null) {
			pd.setErrorDesc(PennantJavaUtil.getLabel("label_ManualAdvise_Notavailable") + pd.getMandateType());
			return pd;
		}
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setLastMntBy(pd.getLastMntBy());
		manualAdvise.setVersion(manualAdvise.getVersion() + 1);

		receiptHeader.setManualAdvise(manualAdvise);
		receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_BOUNCE);
		receiptHeader.setBounceDate(pd.getAppDate());
		receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		receiptHeader.setLastMntBy(pd.getLastMntBy());
		receiptHeader.setVersion(receiptHeader.getVersion() + 1);

		// Receipts Cancellation Process
		String errorMsg = procReceiptCancellation(receiptHeader, PennantConstants.APP_PHASE_EOD, fm);

		if (StringUtils.trimToNull(errorMsg) == null) {

			// Update ReceiptHeader
			finReceiptHeaderDAO.update(receiptHeader, TableType.MAIN_TAB);

			// Limits update against PresentmentCancellation
			if (ImplementationConstants.LIMIT_INTERNAL) {

				BigDecimal priAmt = BigDecimal.ZERO;
				for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
					FinRepayHeader header = receiptDetail.getRepayHeader();
					for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
						priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				}
				if (priAmt.compareTo(BigDecimal.ZERO) > 0) {
					// Update Limit Exposures
					String pc = StringUtils.trimToEmpty(fm.getProductCategory());
					limitManagement.processLoanRepayCancel(fm, customer, priAmt, pc);
				}
			}
		}

		pd.setErrorDesc(errorMsg);
		manualAdvise = receiptHeader.getManualAdvise();
		pd.setBounceID(manualAdvise.getBounceID());
		pd.setBounceCode(bounceReason.getReason());
		pd.setManualAdviseId(manualAdvise.getAdviseID());

		logger.debug(Literal.LEAVING);
		return pd;
	}

	private ManualAdvise getManualAdvise(FinReceiptHeader rch, BounceReason bounceReason, FinReceiptDetail rcd,
			String presentmentType, String bounceRemarks, Date appDate) {
		logger.debug(Literal.ENTERING);

		Rule rule = RuleConfigCache.getCacheRule(bounceReason.getRuleID());

		BigDecimal bounceAmt = BigDecimal.ZERO;

		Map<String, Object> fieldsAndValues = rcd.getDeclaredFieldValues();
		bounceReason.getDeclaredFieldValues(fieldsAndValues);

		long finID = rch.getFinID();
		String finReference = rch.getReference();

		if (rule != null) {
			Map<String, Object> eventMapping = null;
			String sqlRule = StringUtils.trimToEmpty(rule.getSQLRule());

			if (sqlRule.contains("emptype") || sqlRule.contains("branchcity") || sqlRule.contains("fincollateralreq")
					|| sqlRule.contains("btloan") || sqlRule.contains("ae_businessvertical")
					|| sqlRule.contains("ae_alwflexi") || sqlRule.contains("ae_finbranch")
					|| sqlRule.contains("ae_entitycode")) {
				eventMapping = financeMainDAO.getGLSubHeadCodes(finID);

			}
			fieldsAndValues.put("br_finType", rch.getFinType());
			fieldsAndValues.put("br_dpdcount", DateUtil.getDaysBetween(rch.getReceiptDate(), appDate));

			fieldsAndValues.put("br_presentmentType", presentmentType);
			if (eventMapping != null && eventMapping.size() > 0) {
				fieldsAndValues.put("emptype", eventMapping.get("EMPTYPE"));
				fieldsAndValues.put("branchcity", eventMapping.get("BRANCHCITY"));
				fieldsAndValues.put("fincollateralreq", eventMapping.get("FINCOLLATERALREQ"));
				fieldsAndValues.put("btloan", eventMapping.get("BTLOAN"));
				fieldsAndValues.put("ae_businessvertical", eventMapping.get("BUSINESSVERTICAL"));
				fieldsAndValues.put("ae_alwflexi", eventMapping.get("ALWFLEXI"));
				fieldsAndValues.put("ae_finbranch", eventMapping.get("FINBRANCH"));
				fieldsAndValues.put("ae_entitycode", eventMapping.get("ENTITYCODE"));
			}

			bounceAmt = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues, rch.getFinCcy(),
					RuleReturnType.DECIMAL);
		}

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
		manualAdvise.setFinReference(finReference);
		manualAdvise.setFeeTypeID(0);
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(
				PennantApplicationUtil.unFormateAmount(bounceAmt, CurrencyUtil.getFormat(rch.getFinCcy())));
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks(bounceRemarks);
		manualAdvise.setReceiptID(rch.getReceiptID());
		manualAdvise.setBounceID(bounceReason.getBounceID());
		manualAdvise.setValueDate(appDate);
		manualAdvise.setPostDate(appDate);
		logger.debug(Literal.LEAVING);

		return manualAdvise;
	}

	private String procReceiptCancellation(FinReceiptHeader rch, String postBranch, FinanceMain fm) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean alwSchdReversalByLog = false;
		long receiptID = rch.getReceiptID();
		String curStatus = finReceiptHeaderDAO.getReceiptModeStatus(receiptID);
		Date appDate = null;
		EventProperties eventProperties = fm.getEventProperties();
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		}

		if (appDate == null) {
			appDate = SysParamUtil.getAppDate();
		}

		// Valid Check for Finance Reversal On Active Finance Or not with
		// ValueDate CheckUp
		long finID = rch.getFinID();
		String finReference = rch.getReference();

		/*
		 * if (!financeMain.isFinIsActive()) { ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new
		 * ErrorDetail("60204", "", null), PennantConstants.default_Language); // Not Allowed for Inactive Finances
		 * return errorDetail.getMessage(); }
		 */

		boolean isRcdFound = false;
		boolean isBounceProcess = false;
		if (RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus())) {
			isBounceProcess = true;
		}

		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		List<RepayScheduleDetail> tempRpySchdList = new ArrayList<>();
		long logKey = 0;
		BigDecimal totalPriAmount = BigDecimal.ZERO;
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(rch.getReceiptDetails());

		// Posting Reversal Case Program Calling in Equation
		// ============================================
		FeeType penalityFeeType = null;
		long linkedTranID = postReversalTransactions(rch, appDate);
		long postingId = postingsDAO.getPostingId();

		if (!ImplementationConstants.PRESENTMENT_STAGE_ACCOUNTING_REQ) {
			List<ReturnDataSet> returnDataSets = null;
			returnDataSets = postingsPreparationUtil.postReversalsByPostRef(String.valueOf(receiptID), postingId,
					appDate);
			if (CollectionUtils.isNotEmpty(returnDataSets)) {
				linkedTranID = returnDataSets.get(0).getLinkedTranId();
			}
		}

		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail detail : rch.getReceiptDetails()) {
				if (StringUtils.equals(detail.getPaymentType(), rch.getReceiptMode())
						&& !StringUtils.equals(rch.getReceiptMode(), RepayConstants.RECEIPTMODE_EXCESS)) {
					String receiptNumber = detail.getPaymentRef();
					if (StringUtils.isNotBlank(receiptNumber)) {
						List<Long> tranIdList = finStageAccountingLogDAO.getTranIdListByReceipt(receiptNumber);
						if (tranIdList != null && !tranIdList.isEmpty()) {
							for (Long stageLinkTranID : tranIdList) {
								postingsPreparationUtil.postReversalsByLinkedTranID(stageLinkTranID);
							}
							finStageAccountingLogDAO.deleteByReceiptNo(receiptNumber);
						}
					}
				}
			}
		}

		BigDecimal unRealizeAmz = BigDecimal.ZERO;
		BigDecimal unRealizeLpp = BigDecimal.ZERO;
		BigDecimal unRealizeLppGst = BigDecimal.ZERO;

		List<FinTaxIncomeDetail> taxIncomeList = new ArrayList<>();
		if (receiptDetails != null && !receiptDetails.isEmpty()) {
			for (int i = receiptDetails.size() - 1; i >= 0; i--) {

				FinReceiptDetail receiptDetail = receiptDetails.get(i);

				// GST Invoice debit note for Payable Advise Usage
				if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
					long payAgainstID = receiptDetail.getPayAgainstID();

					// Payable Advise Amount make utilization
					if (payAgainstID != 0) {
						ManualAdviseMovements advMov = manualAdviseDAO.getAdvMovByReceiptSeq(
								receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(),
								receiptDetail.getPayAgainstID(), "");

						if (advMov != null && advMov.getTaxHeaderId() != null) {
							TaxHeader header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(advMov.getTaxHeaderId(), "");
							if (header != null) {
								header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(advMov.getTaxHeaderId(), ""));
							}
							advMov.setTaxHeader(header);
						}
						receiptDetail.setPayAdvMovement(advMov);
					}
				}

				if (isBounceProcess && (StringUtils.equals(receiptDetail.getPaymentType(),
						RepayConstants.RECEIPTMODE_EXCESS)
						|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
						|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE))) {
					continue;
				}

				// Fetch Log Entry Details Greater than this Repayments Entry ,
				// which are having Schedule Recalculation
				// If Any Exist Case after this Repayments with Schedule
				// Recalculation then Stop Process
				// ============================================
				if (alwSchdReversalByLog) {
					List<FinLogEntryDetail> list = finLogEntryDetailDAO.getFinLogEntryDetailList(finID,
							receiptDetail.getLogKey());
					if (list != null && !list.isEmpty()) {
						ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60206", "", null),
								PennantConstants.default_Language);
						return errorDetail.getMessage();
					}
				}

				// Finance Repayments Amount Updation if Principal Amount Exists
				long linkedTranId = 0;
				FinRepayHeader rpyHeader = receiptDetail.getRepayHeader();
				isRcdFound = true;
				if (rpyHeader != null) {
					linkedTranId = rpyHeader.getLinkedTranId();

					if (rpyHeader.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {

						// Fetch Excess Amount Details
						FinExcessAmount excess = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
								rch.getExcessAdjustTo());

						if (StringUtils.equals(RepayConstants.PAYSTATUS_DEPOSITED, curStatus)) {
							if (excess == null || excess.getReservedAmt().compareTo(rpyHeader.getExcessAmount()) < 0) {
								ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null),
										PennantConstants.default_Language);
								return errorDetail.getMessage();
							}

						} else {

							if (excess == null || excess.getBalanceAmt().compareTo(rpyHeader.getExcessAmount()) < 0) {
								ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null),
										PennantConstants.default_Language);
								return errorDetail.getMessage();
							}
						}

						// Update Reserve Amount in FinExcessAmount

						// Excess Amounts reversal Updations
						if (StringUtils.equals(RepayConstants.PAYSTATUS_DEPOSITED, curStatus)) {
							finExcessAmountDAO.deductExcessReserve(excess.getExcessID(), rpyHeader.getExcessAmount());
						} else {

							finExcessAmountDAO.updateExcessBal(excess.getExcessID(),
									rpyHeader.getExcessAmount().negate());
						}

						isRcdFound = true;
					}

					if (rpyHeader.getPriAmount().compareTo(BigDecimal.ZERO) > 0) {
						totalPriAmount = totalPriAmount.add(rpyHeader.getPriAmount());
					}

					isRcdFound = true;

					// Remove Repayments Terms based on Linked Transaction ID
					// ============================================
					if (linkedTranId > 0) {
						financeRepaymentsDAO.deleteRpyDetailbyLinkedTranId(linkedTranId, finID);
					}
					// Remove FinRepay Header Details
					// financeRepaymentsDAO.deleteFinRepayHeaderByTranId(finReference,
					// linkedTranId, "");

					// Remove Repayment Schedule Details
					// financeRepaymentsDAO.deleteFinRepaySchListByTranId(finReference,
					// linkedTranId, "");

					// Gathering All repayments Schedule List
					if (rpyHeader.getRepayScheduleDetails() != null && !rpyHeader.getRepayScheduleDetails().isEmpty()) {
						rpySchdList.addAll(rpyHeader.getRepayScheduleDetails());
						tempRpySchdList.addAll(rpyHeader.getRepayScheduleDetails());
					}

					// Update Profit Details for UnRealized Income
					unRealizeAmz = unRealizeAmz.add(rpyHeader.getRealizeUnAmz());

					// Update Profit Details for UnRealized LPI
					// unRealizeLpi =
					// unRealizeLpi.add(rpyHeader.getRealizeUnLPI());
					// unRealizeLpiGst =
					// unRealizeLpiGst.add(rpyHeader.getRealizeUnLPIGst());

					// Update Profit Details for UnRealized LPP
					FinTaxIncomeDetail taxIncome = finODAmzTaxDetailDAO.getFinTaxIncomeDetail(receiptID, "LPP");
					if (taxIncome != null) {
						taxIncomeList.add(taxIncome);
						unRealizeLpp = unRealizeLpp.add(taxIncome.getReceivedAmount());
						unRealizeLppGst = unRealizeLppGst.add(CalculationUtil.getTotalGST(taxIncome));
					}
				}

				// Update Log Entry Based on FinPostDate and Reference
				// ============================================
				if (receiptDetail.getLogKey() != 0 && receiptDetail.getLogKey() != Long.MIN_VALUE) {
					FinLogEntryDetail detail = finLogEntryDetailDAO
							.getFinLogEntryDetailByLog(receiptDetail.getLogKey());
					if (detail == null) {
						ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60207", "", null),
								PennantConstants.default_Language);
						return errorDetail.getMessage();
					}
					logKey = detail.getLogKey();
					detail.setReversalCompleted(true);
					finLogEntryDetailDAO.updateLogEntryStatus(detail);
				}

				// Manual Advise Movements Reversal
				List<ManualAdviseMovements> advMovements = manualAdviseDAO.getAdvMovementsByReceiptSeq(
						receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(), "_AView");
				List<ManualAdviseMovements> receivableAdvMovements = new ArrayList<ManualAdviseMovements>();
				List<ManualAdviseMovements> payableMovements = new ArrayList<>();
				List<ManualAdviseMovements> waiverAdvMovements = new ArrayList<>();

				if (CollectionUtils.isNotEmpty(advMovements)) {
					FinanceDetail financeDetail = new FinanceDetail();
					financeDetail.getFinScheduleData().setFinanceMain(fm);

					isRcdFound = true;
					for (ManualAdviseMovements mam : advMovements) {

						Long taxHeaderId = mam.getTaxHeaderId();
						if (taxHeaderId > 0) {
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

						boolean dueCreated = false;
						if (StringUtils.isBlank(manualAdvise.getFeeTypeCode()) && manualAdvise.getBounceID() > 0) {
							FeeType boucneFeeType = null;

							if (eventProperties.isCacheLoaded()) {
								boucneFeeType = FeeTypeConfigCache
										.getCacheFeeTypeByCode(PennantConstants.FEETYPE_BOUNCE);
							} else {
								boucneFeeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
							}

							if (boucneFeeType == null) {
								throw new AppException(String.format(
										"Fee Type code %s not found, please conatact system admin to configure.",
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
						dueCreated = manualAdvise.isDueCreation();

						if (!dueCreated) {
							if (manualAdvise.getAdviseType() == FinanceConstants.MANUAL_ADVISE_RECEIVABLE) {
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

						manualAdviseDAO.updateAdvPayment(advise, TableType.MAIN_TAB);
					}

					// Update Movement Status
					manualAdviseDAO.updateMovementStatus(receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(),
							rch.getReceiptModeStatus(), "");

					// GST Invoice Preparation
					if (CollectionUtils.isNotEmpty(receivableAdvMovements)
							|| CollectionUtils.isNotEmpty(payableMovements)
							|| CollectionUtils.isNotEmpty(waiverAdvMovements)) {

						InvoiceDetail invoiceDetail = new InvoiceDetail();
						invoiceDetail.setLinkedTranId(linkedTranID);
						invoiceDetail.setFinanceDetail(financeDetail);
						invoiceDetail.setWaiver(false);

						// Receivable Advise Movements
						if (CollectionUtils.isNotEmpty(receivableAdvMovements)) {
							invoiceDetail.setMovements(receivableAdvMovements);
							invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
							this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
						}

						// Payable Advise Movements
						if (CollectionUtils.isNotEmpty(payableMovements)) {
							invoiceDetail.setMovements(payableMovements);
							invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
							this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
						}

						// Waiver Advise Movements
						if (CollectionUtils.isNotEmpty(waiverAdvMovements)) {
							// Preparing the Waiver GST movements

						}
					}
				}
			}

			if (!rpySchdList.isEmpty()) {

				// Making Single Set of Repay Schedule Details and sent to
				// Rendering
				Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
				for (RepayScheduleDetail rpySchd : rpySchdList) {

					RepayScheduleDetail curRpySchd = null;
					if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
						curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
						curRpySchd.setPrincipalSchdPayNow(
								curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
						curRpySchd.setProfitSchdPayNow(
								curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
						curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
						curRpySchd.setLatePftSchdPayNow(
								curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
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

				rpySchdList = sortRpySchdDetails(new ArrayList<>(rpySchdMap.values()));
				List<FinanceScheduleDetail> updateSchdList = new ArrayList<>();
				List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
				Map<String, FinanceScheduleDetail> schdMap = null;

				FinScheduleData schdData = null;
				if (!alwSchdReversalByLog) {

					schdMap = new HashMap<>();
					schdData = new FinScheduleData();
					schdData.setFinanceScheduleDetails(
							financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
					schdData.setFinanceType(FinanceConfigCache.getFinanceType(fm.getFinType()));
					schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

					for (FinanceScheduleDetail schd : schdData.getFinanceScheduleDetails()) {
						schdMap.put(DateUtility.format(schd.getSchDate(), PennantConstants.DBDateFormat), schd);
					}
				}

				for (RepayScheduleDetail rpySchd : rpySchdList) {

					// Schedule Detail Reversals
					if (!alwSchdReversalByLog) {

						FinanceScheduleDetail curSchd = null;
						boolean schdUpdated = false;
						if (schdMap
								.containsKey(DateUtility.format(rpySchd.getSchDate(), PennantConstants.DBDateFormat))) {
							curSchd = schdMap
									.get(DateUtility.format(rpySchd.getSchDate(), PennantConstants.DBDateFormat));

							// Principal Payment
							if (rpySchd.getPrincipalSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setSchdPriPaid(
										curSchd.getSchdPriPaid().subtract(rpySchd.getPrincipalSchdPayNow()));
								curSchd.setSchPriPaid(false);
								schdUpdated = true;
							}

							// Profit Payment
							if (rpySchd.getProfitSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setSchdPftPaid(
										curSchd.getSchdPftPaid().subtract(rpySchd.getProfitSchdPayNow()));
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
						List<FinFeeScheduleDetail> feeList = finFeeScheduleDetailDAO
								.getFeeScheduleBySchDate(finReference, rpySchd.getSchDate());
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

				// Schedule Details Updation
				if (!updateSchdList.isEmpty()) {
					financeScheduleDetailDAO.updateListForRpy(updateSchdList);
				}

				// Fee Schedule Details Updation
				if (!updateFeeList.isEmpty()) {
					finFeeScheduleDetailDAO.updateFeeSchdPaids(updateFeeList);
				}

				rpySchdList = null;
				rpySchdMap = null;
				updateSchdList = null;
				updateFeeList = null;

				// Deletion of Finance Schedule Related Details From Main Table
				FinanceProfitDetail pftDetail = financeProfitDetailDAO.getFinProfitDetailsById(finID);

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
						for (int i = 0; i < receiptDetails.size(); i++) {
							listDeletion(finID, "_Log", false, receiptDetails.get(i).getLogKey());
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
				List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayListByFinRef(fm.getFinID(), false,
						"");
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

					Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm.getFinID());

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
						incMovement.setPaidAmount(
								incMovement.getPaidAmount().add(taxIncomeList.get(i).getReceivedAmount()));
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

					Taxes cgstTaxLPP = getTaxDetail(RuleConstants.CODE_CGST,
							taxPercentages.get(RuleConstants.CODE_CGST));
					Taxes sgstTaxLPP = getTaxDetail(RuleConstants.CODE_SGST,
							taxPercentages.get(RuleConstants.CODE_SGST));
					Taxes igstTaxLPP = getTaxDetail(RuleConstants.CODE_IGST,
							taxPercentages.get(RuleConstants.CODE_IGST));
					Taxes ugstTaxLPP = getTaxDetail(RuleConstants.CODE_UGST,
							taxPercentages.get(RuleConstants.CODE_UGST));
					Taxes cessTaxLPP = getTaxDetail(RuleConstants.CODE_CESS,
							taxPercentages.get(RuleConstants.CODE_CESS));

					// Paid GST Calculations
					for (RepayScheduleDetail rpySchd : tempRpySchdList) {
						BigDecimal gstAmount = BigDecimal.ZERO;

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
											cgstTaxLPP
													.setWaivedTax(cgstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
										}
									} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
										if (sgstTaxLPP == null) {
											sgstTaxLPP = taxes;
										} else {
											sgstTaxLPP.setPaidTax(sgstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
											sgstTaxLPP
													.setWaivedTax(sgstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
										}
									} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
										if (igstTaxLPP == null) {
											igstTaxLPP = taxes;
										} else {
											igstTaxLPP.setPaidTax(igstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
											igstTaxLPP
													.setWaivedTax(igstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
										}
									} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
										if (ugstTaxLPP == null) {
											ugstTaxLPP = taxes;
										} else {
											ugstTaxLPP.setPaidTax(ugstTaxLPP.getPaidTax().add(taxes.getPaidTax()));
											ugstTaxLPP
													.setWaivedTax(ugstTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
										}
									} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
										if (cessTaxLPP == null) {
											cessTaxLPP = taxes;
										} else {
											cessTaxLPP.setPaidTax(cessTaxLPP.getPaidTax().add(taxes.getPaidTax()));
											cessTaxLPP
													.setWaivedTax(cessTaxLPP.getWaivedTax().add(taxes.getWaivedTax()));
										}
									}
								}
							}
						}

						movement.setMovementAmount(movement.getMovementAmount().add(rpySchd.getPenaltyPayNow())
								.add(rpySchd.getWaivedAmt()));
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
						finEodEvent.getFinODDetails(), null, false);

				// Overdue Details Updation after Recalculation with Current
				// Data
				if (finEodEvent.getFinODDetails() != null && !finEodEvent.getFinODDetails().isEmpty()) {
					List<FinODDetails> updateODlist = new ArrayList<>();
					List<FinODDetails> saveODlist = new ArrayList<>();

					for (FinODDetails od : finEodEvent.getFinODDetails()) {
						if (StringUtils.equals("I", od.getRcdAction())) {
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
			}
		}
		// FIXME:Added the below condition to skip the validation if we mark the CHEQUE/DD as Bounce since it is not
		// realized based on below sys param
		if (!isRcdFound && !SysParamUtil.isAllowed(SMTParameterConstants.CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION)) {
			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60208", "", null),
					PennantConstants.default_Language);
			return errorDetail.getMessage();
		} else {

			if (rch.getManualAdvise() != null) {

				// Bounce Charge Due Postings
				ManualAdvise manualAdvise = rch.getManualAdvise();
				if (manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {

					if (manualAdvise != null && manualAdvise.getAdviseID() <= 0) {
						manualAdvise.setAdviseID(this.manualAdviseDAO.getNewAdviseID());
					}

					AEEvent aeEvent = executeBounceDueAccounting(fm, rch.getBounceDate(), manualAdvise, postBranch,
							RepayConstants.ALLOCATION_BOUNCE);

					if (aeEvent != null && StringUtils.isNotEmpty(aeEvent.getErrorMessage())) {
						logger.debug(Literal.LEAVING);
						return aeEvent.getErrorMessage();
					}
					if (aeEvent != null) {
						manualAdvise.setLinkedTranId(aeEvent.getLinkedTranId());
					}

				}

				manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				String adviseId = manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);
				manualAdvise.setAdviseID(Long.parseLong(adviseId));
			}

			// Update Receipt Details based on Receipt Mode
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
				if (!isBounceProcess
						|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PRESENTMENT)
						|| (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DD))) {
					finReceiptDetailDAO.updateReceiptStatus(receiptDetail.getReceiptID(),
							receiptDetail.getReceiptSeqID(), rch.getReceiptModeStatus());

					// Receipt Reversal for Excess or Payable
					if (!isBounceProcess) {

						if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
								|| StringUtils.equals(receiptDetail.getPaymentType(),
										RepayConstants.RECEIPTMODE_EMIINADV)
								|| StringUtils.equals(receiptDetail.getPaymentType(),
										RepayConstants.RECEIPTMODE_CASHCLT)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DSF)) {

							// Excess utilize Reversals
							finExcessAmountDAO.updateExcessAmount(receiptDetail.getPayAgainstID(), "U",
									receiptDetail.getAmount().negate());

						} else if (StringUtils.equals(receiptDetail.getPaymentType(),
								RepayConstants.RECEIPTMODE_PAYABLE)) {

							// Payable Utilize reversals
							manualAdviseDAO.reverseUtilise(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
						}
					}
				}
			}

			// Accounting Execution Process for Deposit Reversal for CASH
			if (ImplementationConstants.DEPOSIT_PROC_REQ) {
				if (RepayConstants.RECEIPTMODE_CASH.equals(rch.getReceiptMode())) {

					DepositMovements movement = depositDetailsDAO.getDepositMovementsByReceiptId(receiptID, "_AView");
					if (movement != null) {
						// Find Amount of Deposited Request
						BigDecimal reqAmount = BigDecimal.ZERO;
						for (FinReceiptDetail rcptDetail : rch.getReceiptDetails()) {
							if (RepayConstants.RECEIPTMODE_CASH.equals(rcptDetail.getPaymentType())) { // CASH
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
				if (RepayConstants.RECEIPTMODE_CHEQUE.equals(rch.getReceiptMode())
						|| RepayConstants.RECEIPTMODE_DD.equals(rch.getReceiptMode())) {

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
								if (RepayConstants.RECEIPTMODE_CHEQUE.equals(rcptDetail.getPaymentType())
										|| RepayConstants.RECEIPTMODE_DD.equals(rcptDetail.getPaymentType())) { // Cheque/DD
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
			if (!RepayConstants.RECEIPTMODE_PRESENTMENT.equals(receiptMode)) {
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

	private void updateTaxReceivable(long finID, boolean accrualDiffPostReq, FinTaxReceivable newTaxRcv) {
		boolean isSaveRcv = false;
		FinTaxReceivable taxRcv = finODAmzTaxDetailDAO.getFinTaxReceivable(finID, "LPP");

		// if receipt done before month end and Already month end crossed with
		// paids,
		// Now Old receipt which was done before month end came for cancellation
		if (taxRcv == null && accrualDiffPostReq) {
			isSaveRcv = true;
			taxRcv = new FinTaxReceivable();
			taxRcv.setFinID(taxRcv.getFinID());
			taxRcv.setFinReference(taxRcv.getFinReference());
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
			FinTaxReceivable newTaxRcv, Date appDate, Date valueDate) throws Exception {
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
				lppFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(RepayConstants.ALLOCATION_ODC);
			} else {
				lppFeeType = feeTypeDAO.getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
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

				newTaxRcv.setReceivableAmount(
						newTaxRcv.getReceivableAmount().add(aeEvent.getAeAmountCodes().getdLPPAmz()));
				newTaxRcv.setCGST(newTaxRcv.getCGST().add(odTaxDetail.getCGST()));
				newTaxRcv.setSGST(newTaxRcv.getSGST().add(odTaxDetail.getSGST()));
				newTaxRcv.setUGST(newTaxRcv.getUGST().add(odTaxDetail.getUGST()));
				newTaxRcv.setIGST(newTaxRcv.getIGST().add(odTaxDetail.getIGST()));

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
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);

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
			Collections.sort(rpySchdList, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail rpySchd1, RepayScheduleDetail rpySchd2) {
					return DateUtility.compare(rpySchd1.getSchDate(), rpySchd2.getSchDate());
				}
			});
		}

		return rpySchdList;
	}

	public List<ReceiptCancelDetail> sortReceipts(List<ReceiptCancelDetail> receipts) {

		if (receipts != null && receipts.size() > 0) {
			Collections.sort(receipts, new Comparator<ReceiptCancelDetail>() {
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

	/**
	 * Method for Sorting Receipt Details From Receipts
	 * 
	 * @param receipts
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts) {

		if (receipts != null && receipts.size() > 1) {
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
		}
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
		String finReference = rch.getReference();
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
		if (BigDecimal.ZERO.compareTo(excessAmt) < 0) {
			FinExcessAmount excess = null;
			excess = finExcessAmountDAO.getExcessAmountsByRefAndType(finID, RepayConstants.EXCESSADJUSTTO_EXCESS);
			// Creating Excess
			if (excess == null) {
				// TODO:
				// Throw Exception
			} else {
				excess.setBalanceAmt(excess.getBalanceAmt().subtract(excessAmt));
				excess.setAmount(excess.getAmount().subtract(excessAmt));
				finExcessAmountDAO.updateExcess(excess);
			}
			// Creating ExcessMoment
			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excess.getExcessID());
			excessMovement.setAmount(excessAmt);
			excessMovement.setReceiptID(receiptId);
			excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
			excessMovement.setTranType(AccountConstants.TRANTYPE_DEBIT);
			excessMovement.setMovementFrom("UPFRONT");
			finExcessAmountDAO.saveExcessMovement(excessMovement);
			logger.debug(Literal.LEAVING);
		}
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
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() <= 0) {
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
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() <= 0) {
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

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

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
	public AuditHeader doApproveNonLanReceipt(AuditHeader aAuditHeader) throws Exception {
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
			String bounceRemarks) throws Exception {
		return null;
	}

	@Override
	public FinanceMain getFinBasicDetails(String reference) {
		return this.financeMainDAO.getFinanceMainByRef(reference, TableType.VIEW.getSuffix(), false);
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
