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
 * * FileName : ManualAdviseServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * *
 * Modified Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.eod.cache.BounceConfigCache;
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * Service implementation for methods that depends on <b>ManualAdvise</b>.<br>
 */
public class ManualAdviseServiceImpl extends GenericService<ManualAdvise> implements ManualAdviseService {
	private static final Logger logger = LogManager.getLogger(ManualAdviseServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeTypeService feeTypeService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceMainDAO financeMainDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentManagerDAO documentManagerDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (manualAdvise.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (manualAdvise.isNewRecord()) {
			manualAdvise.setId(Long.parseLong(manualAdviseDAO.save(manualAdvise, tableType)));
			auditHeader.getAuditDetail().setModelData(manualAdvise);
			auditHeader.setAuditReference(String.valueOf(manualAdvise.getAdviseID()));
		} else {
			manualAdviseDAO.update(manualAdvise, tableType);
		}

		List<DocumentDetails> documentsList = manualAdvise.getDocumentDetails();
		if (CollectionUtils.isNotEmpty(documentsList)) {
			List<AuditDetail> details = manualAdvise.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, manualAdvise, tableType.getSuffix());
			auditDetails.addAll(details);
			auditHeader.setAuditDetails(auditDetails);
		}

		String rcdMaintainSts = FinServiceEvent.MANUALADVISE;
		financeMainDAO.updateMaintainceStatus(manualAdvise.getFinID(), rcdMaintainSts);

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		manualAdviseDAO.delete(manualAdvise, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public ManualAdvise getManualAdviseById(long adviseID) {
		ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(adviseID, "_View");

		if (manualAdvise == null) {
			return null;
		}

		manualAdvise.setFeeType(this.feeTypeService.getApprovedFeeTypeById(manualAdvise.getFeeTypeID()));

		// Document Details
		List<DocumentDetails> documents = documentDetailsDAO.getDocumentDetailsByRef(
				String.valueOf(manualAdvise.getAdviseID()), PennantConstants.PAYABLE_ADVISE_DOC_MODULE_NAME,
				FinServiceEvent.RECEIPT, "_View");
		manualAdvise.getDocumentDetails().addAll(documents);

		return manualAdvise;
	}

	public ManualAdvise getApprovedManualAdvise(long adviseID) {
		ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(adviseID, "_AView");

		if (manualAdvise == null) {
			return null;
		}

		// Document Details
		List<DocumentDetails> documents = documentDetailsDAO.getDocumentDetailsByRef(
				String.valueOf(manualAdvise.getAdviseID()), PennantConstants.PAYABLE_ADVISE_DOC_MODULE_NAME,
				FinServiceEvent.RECEIPT, "_AView");
		manualAdvise.getDocumentDetails().addAll(documents);
		return manualAdvise;
	}

	@Override
	public String getTaxComponent(Long adviseID, String type) {
		return manualAdviseDAO.getTaxComponent(adviseID, type);
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = new ManualAdvise();
		BeanUtils.copyProperties((ManualAdvise) auditHeader.getAuditDetail().getModelData(), manualAdvise);

		if (StringUtils.equals(manualAdvise.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			com.pennant.backend.model.finance.FeeType feeType = manualAdvise.getFeeType();
			if (feeType != null && feeType.isDueAccReq()) {
				if ((manualAdvise.getValueDate().compareTo(SysParamUtil.getAppDate()) <= 0)) {
					manualAdvise = executeDueAccountingProcess(manualAdvise, auditHeader.getAuditBranchCode());
				}
			}
		}

		if (StringUtils.equals(manualAdvise.getFinSource(), UploadConstants.FINSOURCE_ID_PFF)) {
			manualAdviseDAO.delete(manualAdvise, TableType.TEMP_TAB);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(manualAdvise.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(manualAdviseDAO.getManualAdviseById(manualAdvise.getAdviseID(), ""));
		}

		if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			manualAdviseDAO.delete(manualAdvise, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(manualAdvise, TableType.MAIN_TAB.getSuffix(), tranType));
		} else {
			manualAdvise.setRoleCode("");
			manualAdvise.setNextRoleCode("");
			manualAdvise.setTaskId("");
			manualAdvise.setNextTaskId("");
			manualAdvise.setWorkflowId(0);

			if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				manualAdvise.setRecordType("");
				manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				manualAdvise.setRecordType("");
				manualAdviseDAO.update(manualAdvise, TableType.MAIN_TAB);
			}

			if (PennantConstants.MANUALADVISE_CANCEL.equals(manualAdvise.getStatus())) {
				postingsPreparationUtil.postReversalsByLinkedTranID(manualAdvise.getLinkedTranId());
			}

			// Document Details
			List<DocumentDetails> documentsList = manualAdvise.getDocumentDetails();
			if (CollectionUtils.isNotEmpty(documentsList)) {
				List<AuditDetail> details = manualAdvise.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, manualAdvise, TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}
		}

		if (!manualAdvise.isNewRecord()) {
			auditHeader.setAuditDetails(
					listDeletion(manualAdvise, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		}

		financeMainDAO.updateMaintainceStatus(manualAdvise.getFinID(), "");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(manualAdvise);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(listDeletion(manualAdvise, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		manualAdviseDAO.delete(manualAdvise, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(manualAdvise.getFinID(), "");
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private ManualAdvise executeDueAccountingProcess(ManualAdvise advise, String postBranch) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetails(advise.getFinID());

		AEEvent aeEvent = prepareAccSetData(advise, postBranch, financeMain);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Advise Due accounting postings failed. Please contact Adminstrator.");
		}

		long linkedTranId = aeEvent.getLinkedTranId();
		if (linkedTranId <= 0) {
			logger.debug(Literal.LEAVING);
			return advise;
		}

		// Resetting Advise Data
		advise.setLinkedTranId(linkedTranId);

		boolean isGSTInvOnDue = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
		if (!isGSTInvOnDue) {
			return advise;
		}

		advise.setDueCreation(true);

		// GST Invoice Preparation for Receivable Advise/ Bounce
		ManualAdviseMovements advMovement = aeEvent.getMovement();
		AdviseDueTaxDetail detail = new AdviseDueTaxDetail();

		BigDecimal gstAmount = BigDecimal.ZERO;
		if (advise.isTaxApplicable()) {
			List<Taxes> taxDetails = advMovement.getTaxHeader().getTaxDetails();
			for (Taxes taxes : taxDetails) {
				gstAmount = gstAmount.add(taxes.getPaidTax());
				String taxType = taxes.getTaxType();

				switch (taxType) {
				case RuleConstants.CODE_CGST:
					detail.setCGST(taxes.getPaidTax());
					break;
				case RuleConstants.CODE_SGST:
					detail.setSGST(taxes.getPaidTax());
					break;
				case RuleConstants.CODE_IGST:
					detail.setIGST(taxes.getPaidTax());
					break;
				case RuleConstants.CODE_UGST:
					detail.setUGST(taxes.getPaidTax());
					break;
				case RuleConstants.CODE_CESS:
					detail.setCESS(taxes.getPaidTax());
					break;
				default:
					break;
				}

			}
		} else {
			detail.setCGST(BigDecimal.ZERO);
			detail.setSGST(BigDecimal.ZERO);
			detail.setIGST(BigDecimal.ZERO);
			detail.setUGST(BigDecimal.ZERO);
			detail.setCESS(BigDecimal.ZERO);
		}
		detail.setAmount(advMovement.getPaidAmount());
		detail.setTotalGST(gstAmount);

		Long invoiceID = null;
		if (gstAmount.compareTo(BigDecimal.ZERO) > 0 && isGSTInvOnDue) {
			List<ManualAdviseMovements> advMovements = new ArrayList<>();
			advMovements.add(advMovement);
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setMovements(advMovements);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

		}

		saveDueTaxDetail(advise, detail, invoiceID);

		logger.debug(Literal.LEAVING);
		return advise;
	}

	private void saveDueTaxDetail(ManualAdvise advise, AdviseDueTaxDetail detail, Long invoiceID) {
		detail.setAdviseID(advise.getAdviseID());
		detail.setTaxType(advise.getTaxComponent());
		detail.setAmount(advise.getAdviseAmount());
		detail.setInvoiceID(invoiceID);
		detail.setTotalGST(CalculationUtil.getTotalGST(detail));

		manualAdviseDAO.saveDueTaxDetail(detail);
	}

	@Override
	public List<ReturnDataSet> getAccountingSetEntries(ManualAdvise manualAdvise) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetails(manualAdvise.getFinID());
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(financeMain);
		financeMain.setTaxPercentages(taxPercentages);
		AEEvent aeEvent = prepareAccSetData(manualAdvise, "", financeMain);
		aeEvent = postingsPreparationUtil.getAccounting(aeEvent);

		logger.debug(Literal.LEAVING);
		return aeEvent.getReturnDataSet();
	}

	private AEEvent prepareAccSetData(ManualAdvise advise, String postBranch, FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		boolean taxApplicable = advise.isTaxApplicable();
		String taxType = advise.getTaxComponent();
		BigDecimal adviseAmount = advise.getAdviseAmount();

		AEEvent aeEvent = new AEEvent();

		aeEvent.setAccountingEvent(AccountingEvent.ADVDUE);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		amountCodes.setFinType(fm.getFinType());
		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setValueDate(advise.getValueDate());
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		Map<String, Object> eventMapping = aeEvent.getDataMap();
		TaxHeader taxHeader = null;
		BigDecimal gstAmount = BigDecimal.ZERO;
		if (taxApplicable) {
			taxHeader = getTaxHeader(fm, taxType, adviseAmount, eventMapping);

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
				for (Taxes taxDetails : taxHeader.getTaxDetails()) {
					gstAmount = gstAmount.add(taxDetails.getPaidTax());
				}
				adviseAmount = adviseAmount.add(gstAmount);
			}

			ManualAdviseMovements mam = new ManualAdviseMovements();
			mam.setFeeTypeCode(advise.getFeeTypeCode());
			mam.setFeeTypeDesc(advise.getFeeTypeDesc());
			mam.setMovementAmount(advise.getAdviseAmount());
			mam.setTaxApplicable(taxApplicable);
			mam.setTaxComponent(taxType);
			mam.setPaidAmount(adviseAmount);
			mam.setTaxHeader(taxHeader);

			aeEvent.setMovement(mam);
		} else {
			eventMapping.put("ae_feeCGST", BigDecimal.ZERO);
			eventMapping.put("ae_feeSGST", BigDecimal.ZERO);
			eventMapping.put("ae_feeIGST", BigDecimal.ZERO);
			eventMapping.put("ae_feeUGST", BigDecimal.ZERO);
			eventMapping.put("ae_feeCESS", BigDecimal.ZERO);
			eventMapping.put("ae_feeGST_TOT", BigDecimal.ZERO);

			ManualAdviseMovements advMovement = new ManualAdviseMovements();
			advMovement.setFeeTypeCode(advise.getFeeTypeCode());
			advMovement.setFeeTypeDesc(advise.getFeeTypeDesc());
			advMovement.setMovementAmount(advise.getAdviseAmount());
			advMovement.setTaxApplicable(taxApplicable);
			advMovement.setTaxComponent(taxType);
			advMovement.setPaidAmount(adviseAmount);
			advMovement.setTaxHeader(taxHeader);
			aeEvent.setMovement(advMovement);
		}

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					eventMapping.put(key, gstExecutionMap.get(key));
				}
			}
		}

		eventMapping.put("ae_feeAmount", adviseAmount);
		aeEvent.setDataMap(eventMapping);
		aeEvent.getAcSetIDList().add(advise.getFeeType().getDueAccSet());

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	private TaxHeader getTaxHeader(FinanceMain fm, String taxType, BigDecimal adviseAmount,
			Map<String, Object> eventMapping) {
		Map<String, BigDecimal> taxPercentages = fm.getTaxPercentages();

		TaxHeader taxHeader = new TaxHeader();
		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RCD_ADD);
		taxHeader.setVersion(taxHeader.getVersion() + 1);

		TaxAmountSplit taxSplit = GSTCalculator.calculateGST(taxPercentages, taxType, adviseAmount);

		List<Taxes> taxDetails = taxHeader.getTaxDetails();

		taxDetails.add(getTax(taxPercentages, taxSplit, RuleConstants.CODE_CGST));
		taxDetails.add(getTax(taxPercentages, taxSplit, RuleConstants.CODE_SGST));
		taxDetails.add(getTax(taxPercentages, taxSplit, RuleConstants.CODE_IGST));
		taxDetails.add(getTax(taxPercentages, taxSplit, RuleConstants.CODE_UGST));
		taxDetails.add(getTax(taxPercentages, taxSplit, RuleConstants.CODE_CESS));

		eventMapping.put("ae_feeCGST", taxSplit.getcGST());
		eventMapping.put("ae_feeSGST", taxSplit.getsGST());
		eventMapping.put("ae_feeUGST", taxSplit.getuGST());
		eventMapping.put("ae_feeIGST", taxSplit.getiGST());
		eventMapping.put("ae_feeCESS", taxSplit.getCess());
		eventMapping.put("ae_feeGST_TOT", taxSplit.gettGST());

		return taxHeader;
	}

	private Taxes getTax(Map<String, BigDecimal> taxPer, TaxAmountSplit split, String taxType) {
		Taxes tax = new Taxes();
		tax.setTaxType(taxType);
		tax.setTaxPerc(taxPer.get(taxType));

		switch (taxType) {
		case RuleConstants.CODE_CGST:
			tax.setPaidTax(split.getcGST());
			tax.setRemFeeTax(tax.getActualTax().subtract(split.getcGST()));
			break;
		case RuleConstants.CODE_SGST:
			tax.setPaidTax(split.getsGST());
			tax.setRemFeeTax(tax.getActualTax().subtract(split.getsGST()));
			break;
		case RuleConstants.CODE_IGST:
			tax.setPaidTax(split.getiGST());
			tax.setRemFeeTax(tax.getActualTax().subtract(split.getiGST()));
			break;
		case RuleConstants.CODE_UGST:
			tax.setPaidTax(split.getuGST());
			tax.setRemFeeTax(tax.getActualTax().subtract(split.getuGST()));
			break;
		case RuleConstants.CODE_CESS:
			tax.setPaidTax(split.getCess());
			tax.setRemFeeTax(tax.getActualTax().subtract(split.getCess()));
			break;
		default:
			break;
		}

		return tax;
	}

	private AuditDetail validation(AuditDetail ad, String ulan, String method) {
		logger.debug(Literal.ENTERING);

		ad.setErrorDetails(new ArrayList<ErrorDetail>());
		ManualAdvise ma = (ManualAdvise) ad.getModelData();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(ma.getAdviseID());
		valueParm[1] = ma.getAdviseTypeName();

		errParm[0] = PennantJavaUtil.getLabel("label_AdviseID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AdviseType") + ":" + valueParm[1];

		if (PennantConstants.MANUALADVISE_CANCEL.equals(ma.getStatus())) {
			if (PennantConstants.method_doReject.equals(method)) {
				return ad;
			}

			ManualAdvise befImg = manualAdviseDAO.getManualAdviseById(ma.getAdviseID(), "");

			if (befImg.getReservedAmt().compareTo(BigDecimal.ZERO) > 0
					|| befImg.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0
					|| befImg.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				ad.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "CANMA10", errParm, valueParm), ulan));
			}
		}

		logger.debug(Literal.LEAVING);
		return ad;
	}

	@Override
	public List<ManualAdviseMovements> getAdivseMovements(long id) {
		return manualAdviseDAO.getAdviseMovements(id);
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		return manualAdviseDAO.getFinanceDetails(finID);
	}

	@Override
	public long getNewAdviseID() {
		return manualAdviseDAO.getNewAdviseID();
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (manualAdvise.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// Document Details
		if (CollectionUtils.isNotEmpty(manualAdvise.getDocumentDetails())) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(manualAdvise, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		manualAdvise.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(manualAdvise);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> setDocumentDetailsAuditData(ManualAdvise manualAdvise, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
		for (int i = 0; i < manualAdvise.getDocumentDetails().size(); i++) {
			DocumentDetails documentDetails = manualAdvise.getDocumentDetails().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(manualAdvise.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (manualAdvise.isWorkflow()) {
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

			documentDetails.setRecordStatus(manualAdvise.getRecordStatus());
			documentDetails.setUserDetails(manualAdvise.getUserDetails());
			documentDetails.setLastMntOn(manualAdvise.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails, ManualAdvise manualAdvise,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			documentDetails.setReferenceId(String.valueOf(manualAdvise.getId()));
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
				documentDetails.setLastMntBy(manualAdvise.getLastMntBy());
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
						documentDetails.setReferenceId(String.valueOf(manualAdvise.getId()));
					}
					documentDetails.setFinEvent(FinServiceEvent.RECEIPT);
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() == null) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
					}
					if (documentDetails.getDocId() < 0) {
						documentDetails.setDocId(Long.MIN_VALUE);
					}
					documentDetailsDAO.save(documentDetails, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() == null) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
					}
					documentDetailsDAO.update(documentDetails, type);
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
	public List<AuditDetail> listDeletion(ManualAdvise manualAdvise, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Document Details.
		List<AuditDetail> documentDetails = manualAdvise.getAuditDetailMap().get("DocumentDetails");
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

	@Override
	public void cancelFutureDatedAdvises(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);
		int closedNdays = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_CLOSED_LAN);

		List<FinanceMain> fmList = new ArrayList<>();

		custEODEvent.getFinEODEvents().forEach(fod -> {
			FinanceMain aFm = fod.getFinanceMain();
			FinanceMain fm = new FinanceMain();
			fm.setFinID(aFm.getFinID());
			if (fm.isFinIsActive()) {
				fm.setAppDate(aFm.getMaturityDate());
			} else {
				fm.setAppDate(DateUtil.addDays(custEODEvent.getEodDate(), closedNdays + 1));
			}

			fmList.add(fm);
		});

		manualAdviseDAO.cancelFutureDatedAdvises(fmList);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void prepareManualAdvisePostings(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		boolean isGSTInvOnDue = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
		custEODEvent.getFinEODEvents().forEach(fodEvent -> fodEvent.getPostingManualAdvises().forEach(ma -> {
			ma.setInvoiceReq(isGSTInvOnDue);
			postManualAdvise(fodEvent, custEODEvent, ma);
		}));

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void cancelManualAdvises(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		if (!PennantConstants.RCD_STATUS_APPROVED.equals(fm.getRecordStatus())) {
			return;
		}

		List<ManualAdvise> manualAdvise = manualAdviseDAO.getAdviseStatus(fm.getFinID());

		List<ManualAdvise> updateList = new ArrayList<>();

		if (CollectionUtils.isEmpty(manualAdvise)) {
			return;
		}

		Date appDate = fm.getAppDate();

		if (appDate == null) {
			appDate = SysParamUtil.getAppDate();
		}

		int closedNdays = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_CLOSED_LAN);

		for (ManualAdvise md : manualAdvise) {
			Date valueDate = md.getValueDate();

			if (fm.isFinIsActive()) {
				appDate = fm.getMaturityDate();
			} else {
				appDate = DateUtil.addDays(appDate, closedNdays + 1);
			}
			BigDecimal amount = md.getAdviseAmount().subtract(md.getPaidAmount().add(md.getWaivedAmount()));

			if (amount.compareTo(BigDecimal.ZERO) > 0) {
				if (DateUtil.compare(valueDate, appDate) > 0) {
					md.setStatus(PennantConstants.MANUALADVISE_CANCEL);
					md.setAdviseID(md.getAdviseID());

					updateList.add(md);
				}
			}
		}

		manualAdviseDAO.updateStatus(updateList, "");
		logger.debug(Literal.LEAVING);
	}

	private void postManualAdvise(FinEODEvent fodEvent, CustEODEvent codEvent, ManualAdvise ma) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fodEvent.getFinanceMain();

		fm.setTaxPercentages(GSTCalculator.getTaxPercentages(fm.getFinID()));

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinReference(fm.getFinReference());
		schdData.setFinanceMain(fm);
		schdData.setFinanceType(fodEvent.getFinType());
		fm.setOverdraftTxnChrgFeeType(fodEvent.getFinType().getOverdraftTxnChrgFeeType());

		AEEvent aeEvent = prepareAccSetData(ma, PennantConstants.APP_PHASE_EOD, fm);

		aeEvent.setPostDate(codEvent.getEodValueDate());
		aeEvent.setCustAppDate(codEvent.getCustomer().getCustAppDate());

		prepareAccountingProcess(ma, aeEvent, fd);

		fodEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		fodEvent.setUpdLBDPostings(true);

		logger.debug(Literal.LEAVING);
	}

	private void prepareAccountingProcess(ManualAdvise advise, AEEvent aeEvent, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		postingsPreparationUtil.postAccountingEOD(aeEvent);

		long linkedTranId = aeEvent.getLinkedTranId();

		advise.setLinkedTranId(linkedTranId);
		advise.setDueCreation(true);

		ManualAdviseMovements advMovement = new ManualAdviseMovements();
		advMovement.setFeeTypeCode(advise.getFeeTypeCode());
		advMovement.setFeeTypeDesc(advise.getFeeTypeDesc());
		advMovement.setMovementAmount(advise.getAdviseAmount());
		advMovement.setTaxApplicable(advise.isTaxApplicable());
		advMovement.setTaxComponent(advise.getTaxComponent());

		Map<String, Object> dataMap = aeEvent.getDataMap();

		AdviseDueTaxDetail detail = new AdviseDueTaxDetail();

		detail.setAmount(new BigDecimal(dataMap.get("ae_feeAmount").toString()));
		detail.setCGST(new BigDecimal(dataMap.get("ae_feeCGST").toString()));
		detail.setSGST(new BigDecimal(dataMap.get("ae_feeSGST").toString()));
		detail.setUGST(new BigDecimal(dataMap.get("ae_feeUGST").toString()));
		detail.setIGST(new BigDecimal(dataMap.get("ae_feeIGST").toString()));
		detail.setCESS(new BigDecimal(dataMap.get("ae_feeCESS").toString()));
		detail.setTotalGST(new BigDecimal(dataMap.get("ae_feeGST_TOT").toString()));

		if (!advise.isInvoiceReq()) {
			manualAdviseDAO.updateLinkedTranId(advise);

			logger.debug(Literal.LEAVING);
			return;
		}

		if (advise.isTaxApplicable() && detail.getTotalGST().compareTo(BigDecimal.ZERO) <= 0) {
			saveDueTaxDetail(advise, detail, null);
			manualAdviseDAO.updateLinkedTranId(advise);

			logger.debug(Literal.LEAVING);
			return;
		}

		BigDecimal paidAmt = advise.getAdviseAmount();
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advMovement.getTaxComponent())) {
			paidAmt = advise.getAdviseAmount().subtract(detail.getTotalGST());
		}

		advMovement.setPaidAmount(paidAmt);
		advMovement.setPaidCGST(detail.getCGST());
		advMovement.setPaidSGST(detail.getSGST());
		advMovement.setPaidIGST(detail.getIGST());
		advMovement.setPaidUGST(detail.getUGST());

		List<ManualAdviseMovements> advMovements = new ArrayList<>();
		advMovements.add(advMovement);

		if (advise.isTaxApplicable()) {
			InvoiceDetail id = new InvoiceDetail();
			id.setLinkedTranId(linkedTranId);
			id.setFinanceDetail(fd);
			id.setWaiver(false);
			id.setMovements(advMovements);
			id.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(id);

			saveDueTaxDetail(advise, detail, invoiceID);
		}

		manualAdviseDAO.updateLinkedTranId(advise);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getFutureDatedAdvises(long finID) {
		return manualAdviseDAO.getFutureDatedAdvises(finID);
	}

	@Override
	public int cancelFutureDatedAdvises() {
		return manualAdviseDAO.cancelFutureDatedAdvises();
	}

	@Override
	public BigDecimal getBalanceAmt(long finID, Date valueDate) {
		return manualAdviseDAO.getBalanceAmt(finID, valueDate);
	}

	@Override
	public BigDecimal getEligibleAmount(ManualAdvise ma, FeeType feeType) {
		logger.debug(Literal.ENTERING);

		long finID = ma.getFinID();
		Date valueDate = ma.getValueDate();

		long feeTypeID = feeType.getFeeTypeID();
		String linkTo = feeType.getPayableLinkTo();
		Long recvId = feeType.getRecvFeeTypeId();

		BigDecimal eligibleAmt = BigDecimal.ZERO;
		if (Allocation.MANADV.equals(linkTo) && recvId != null) {
			eligibleAmt = manualAdviseDAO.getPaidAmount(finID, recvId, valueDate);
			eligibleAmt = eligibleAmt.add(manualAdviseDAO.getFeePaidAmount(finID, recvId));
		} else {
			eligibleAmt = manualAdviseDAO.getPaidAmountsbyAllocation(finID, linkTo, valueDate);
		}

		eligibleAmt = eligibleAmt.subtract(manualAdviseDAO.getExistingPayableAmount(finID, feeTypeID));

		logger.debug(Literal.LEAVING);
		return eligibleAmt.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : eligibleAmt;
	}

	@Override
	public ManualAdvise getMAForBounce(FinReceiptHeader rch, FinReceiptDetail rcd, String bounceCode, String remarks,
			String presentmentType, Date appDate) {

		BounceReason br = BounceConfigCache.getCacheBounceReason(bounceCode);

		Rule rule = RuleConfigCache.getCacheRule(br.getRuleID());

		BigDecimal bounceAmt = BigDecimal.ZERO;
		if (rule != null) {
			bounceAmt = getBounceAmount(rch, rcd, rule, br, presentmentType, appDate);
		}

		int finCcy = CurrencyUtil.getFormat(rch.getFinCcy());

		ManualAdvise ma = new ManualAdvise();
		ma.setAdviseType(AdviseType.RECEIVABLE.id());
		ma.setFinID(rch.getFinID());
		ma.setFinReference(rch.getReference());
		ma.setFeeTypeID(feeTypeService.getFeeTypeIdByCode(PennantConstants.FEETYPE_BOUNCE));
		ma.setSequence(0);
		ma.setAdviseAmount(PennantApplicationUtil.unFormateAmount(bounceAmt, finCcy));
		ma.setPaidAmount(BigDecimal.ZERO);
		ma.setWaivedAmount(BigDecimal.ZERO);
		ma.setRemarks(remarks);
		ma.setReceiptID(rch.getReceiptID());
		ma.setBounceID(br.getBounceID());
		ma.setValueDate(appDate);
		ma.setPostDate(appDate);
		ma.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ma.setLastMntBy(rch.getLastMntBy());
		ma.setVersion(ma.getVersion() + 1);
		ma.setBrReturnCode(br.getReturnCode());

		logger.debug(Literal.LEAVING);
		return ma;
	}

	private BigDecimal getBounceAmount(FinReceiptHeader rch, FinReceiptDetail rcd, Rule rule, BounceReason br,
			String presentmentType, Date appDate) {

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

	@Override
	public BigDecimal getRefundedAmount(long finID, long feeTypeID) {
		return manualAdviseDAO.getRefundedAmount(finID, feeTypeID);
	}

	@Override
	public BigDecimal getRefundedAmt(long finID, long receivableID, long receivableFeeTypeID) {
		return manualAdviseDAO.getRefundedAmt(finID, receivableID, receivableFeeTypeID);
	}

	@Override
	public boolean isDuplicatePayble(long finID, long feeTypeId, String linkTo) {
		return manualAdviseDAO.isDuplicatePayble(finID, feeTypeId, linkTo);
	}

	@Override
	public boolean isPaybleExist(long finID, long feeTypeID, String linkTo) {
		return manualAdviseDAO.isPaybleExist(finID, feeTypeID, linkTo);
	}

	@Override
	public boolean isManualAdviseExist(long finID) {
		return manualAdviseDAO.isManualAdviseExist(finID);
	}

	@Override
	public boolean isAdviseUploadExist(long finID) {
		return manualAdviseDAO.isAdviseUploadExist(finID);
	}

	@Override
	public boolean isunAdjustablePayables(long finID) {
		return manualAdviseDAO.isunAdjustablePayables(finID);
	}

	@Override
	public List<ManualAdvise> getCancelledManualAdvise(long finID) {
		return manualAdviseDAO.getAdvises(finID, "_Temp");
	}

	@Override
	public void cancelAdvises(long finID) {
		manualAdviseDAO.cancelAdvises(finID);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

}