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
 * * FileName : AccrualService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * * Modified Date
 * : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.core;

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

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.eod.cache.FeeTypeConfigCache;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

public class LatePayDueCreationService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(LatePayDueCreationService.class);

	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerAddresDAO customerAddresDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;

	public LatePayDueCreationService() {
		super();
	}

	public void processLatePayAccrual(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		// Post LPP / LPI Accruals on Application Extended Month End OR Application Month End OR Daily
		Date eodDate = custEODEvent.getEodDate();

		if (eodDate.compareTo(DateUtil.getMonthEnd(eodDate)) == 0) {
			for (FinEODEvent finEODEvent : finEODEvents) {
				postLatePayAccruals(finEODEvent, custEODEvent);
			}
		}

	}

	public void postLatePayAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		long custID = custEODEvent.getCustomer().getCustID();

		String lppEventCode = AccountingEvent.LPPAMZ;
		String lpiEventCode = AccountingEvent.LPIAMZ;

		FinanceProfitDetail pfd = finEODEvent.getFinProfitDetail();
		FinanceMain fm = finEODEvent.getFinanceMain();

		List<FinODDetails> odDetails = finEODEvent.getFinODDetails();

		Long lppAccountingID = getAccountingID(fm, lppEventCode);
		Long lpiAccountingID = getAccountingID(fm, lpiEventCode);

		if (lppAccountingID == null || lpiAccountingID == null) {
			return;
		}

		if (lppAccountingID == Long.MIN_VALUE && lpiAccountingID == Long.MIN_VALUE) {
			return;
		}

		logger.info("Processing Late Payment accrual for the customer ID >> {}", custID);

		FinanceDetail detail = new FinanceDetail();
		FinScheduleData schdData = detail.getFinScheduleData();

		schdData.setFinanceMain(fm);
		schdData.setFinanceType(finEODEvent.getFinType());

		prepareFinanceDetail(detail, custEODEvent);

		Map<String, Object> gstDataMap = null;
		Map<String, BigDecimal> taxPercmap = null;

		FeeType lppFeeType = null;
		BigDecimal lppAmount = BigDecimal.ZERO;
		BigDecimal lppAmz = BigDecimal.ZERO;
		BigDecimal lppGSTAmount = BigDecimal.ZERO;
		BigDecimal lppGSTAmz = BigDecimal.ZERO;
		BigDecimal lppTillLBD = pfd.getLppTillLBD();
		BigDecimal lppGSTTillLBD = pfd.getGstLppTillLBD();

		FeeType lpiFeeType = null;
		BigDecimal lpiAmount = BigDecimal.ZERO;
		BigDecimal lpiAmz = BigDecimal.ZERO;
		BigDecimal lpiGSTAmount = BigDecimal.ZERO;
		BigDecimal lpiGSTAmz = BigDecimal.ZERO;
		BigDecimal lpiTillLBD = pfd.getLpiTillLBD();
		BigDecimal lpiGSTTillLBD = pfd.getGstLpiTillLBD();

		if (ImplementationConstants.LPP_GST_DUE_ON.equals("A") && CollectionUtils.isNotEmpty(odDetails)) {
			for (FinODDetails od : odDetails) {
				lppAmount = lppAmount.add(od.getTotPenaltyAmt());
				lpiAmount = lpiAmount.add(od.getLPIAmt());
			}

			if (lppAmount.compareTo(BigDecimal.ZERO) > 0 || lpiAmount.compareTo(BigDecimal.ZERO) > 0) {
				gstDataMap = GSTCalculator.getGSTDataMap(fm.getFinID());
				taxPercmap = GSTCalculator.getTaxPercentages(gstDataMap, fm.getFinCcy());
			}

			if (lppAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (EODUtil.isEod()) {
					lppFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(Allocation.ODC);
				} else {
					lppFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.ODC);
				}
				lppGSTAmount = calculateGST(lppAmount, lppFeeType, taxPercmap);
			}

			if (lpiAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (EODUtil.isEod()) {
					lpiFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(Allocation.LPFT);
				} else {
					lpiFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.LPFT);
				}
				lpiGSTAmount = calculateGST(lpiAmount, lpiFeeType, taxPercmap);
			}
		}

		pfd.setLppAmount(lppAmount);
		pfd.setGstLpiAmount(lpiGSTAmount);

		pfd.setLpiAmount(lpiAmount);
		pfd.setGstLppAmount(lppGSTAmount);

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = new AEAmountCodes();

		Long finID = pfd.getFinID();
		String finReference = pfd.getFinReference();
		aeEvent.setFinID(finID);
		aeEvent.setFinReference(finReference);
		aeEvent.setPostDate(custEODEvent.getEodValueDate());
		aeEvent.setValueDate(custEODEvent.getEodValueDate());
		aeEvent.setSchdDate(custEODEvent.getEodValueDate());
		aeEvent.setBranch(pfd.getFinBranch());
		aeEvent.setCcy(pfd.getFinCcy());
		aeEvent.setFinType(pfd.getFinType());
		aeEvent.setCustID(pfd.getCustId());

		// Finance Fields
		amountCodes.setFinType(pfd.getFinType());

		/* LPI Amortization calculation */
		if (lpiAmount.compareTo(BigDecimal.ZERO) > 0) {
			lpiAmz = lpiAmount.subtract(lpiTillLBD);
			amountCodes.setdLPIAmz(lpiAmz);

			// Calculate GST Amount on LPI Amount
			if (lpiGSTAmount.compareTo(BigDecimal.ZERO) > 0) {
				lpiGSTAmz = lpiGSTAmount.subtract(lpiGSTTillLBD);
				amountCodes.setdGSTLPIAmz(lpiGSTAmz);
			}
		}

		/* LPP Amortization calculation */
		if (lppAmount.compareTo(BigDecimal.ZERO) > 0) {
			lppAmz = lppAmount.subtract(lppTillLBD);
			amountCodes.setdLPPAmz(lppAmz);

			// Calculate GST Amount on LPP Amount
			if (lppGSTAmount.compareTo(BigDecimal.ZERO) > 0) {
				lppGSTAmz = lppGSTAmount.subtract(lppGSTTillLBD);
				amountCodes.setdGSTLPPAmz(lppGSTAmz);
			}
		}

		aeEvent.setAeAmountCodes(amountCodes);
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());

		if (gstDataMap != null) {
			for (String key : gstDataMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					aeEvent.getDataMap().put(key, gstDataMap.get(key));
				}
			}
		}

		// LPI GST Amount for Postings
		Map<String, BigDecimal> calGstMap = new HashMap<>();
		Long lppDueInvoiceId = null;
		Long lpiDueInvoiceId = null;

		FinODAmzTaxDetail lppTaxDetail = null;
		FinODAmzTaxDetail lpiTaxDetail = null;

		if (ImplementationConstants.LPP_GST_DUE_ON.equals("A")) {
			if (lpiGSTAmz.compareTo(BigDecimal.ZERO) > 0 && lpiGSTAmount.compareTo(BigDecimal.ZERO) > 0) {
				lpiTaxDetail = getTaxDetail(taxPercmap, lpiGSTAmz, lpiFeeType.getTaxComponent());
				lpiTaxDetail.setFinReference(finReference);
				lpiTaxDetail.setTaxFor("LPI");
				lpiTaxDetail.setAmount(lpiAmz);
				lpiTaxDetail.setValueDate(DateUtil.addDays(custEODEvent.getEodValueDate(), -1));
				lpiTaxDetail.setPostDate(new Timestamp(System.currentTimeMillis()));
			}

			if (lppGSTAmz.compareTo(BigDecimal.ZERO) > 0 && lppGSTAmount.compareTo(BigDecimal.ZERO) > 0) {
				lppTaxDetail = getTaxDetail(taxPercmap, lppGSTAmz, lppFeeType.getTaxComponent());
				lppTaxDetail.setFinReference(finReference);
				lppTaxDetail.setTaxFor("LPP");
				lppTaxDetail.setAmount(lppAmz);
				lppTaxDetail.setValueDate(DateUtil.addDays(custEODEvent.getEodValueDate(), -1));
				lppTaxDetail.setPostDate(new Timestamp(System.currentTimeMillis()));
			}
		}

		if (lpiTaxDetail == null) {
			addZeroifNotContains(calGstMap, "LPI_CGST_R");
			addZeroifNotContains(calGstMap, "LPI_SGST_R");
			addZeroifNotContains(calGstMap, "LPI_UGST_R");
			addZeroifNotContains(calGstMap, "LPI_IGST_R");
			addZeroifNotContains(calGstMap, "LPI_CESS_R");
		} else {
			calGstMap.put("LPI_CGST_R", lpiTaxDetail.getCGST());
			calGstMap.put("LPI_SGST_R", lpiTaxDetail.getSGST());
			calGstMap.put("LPI_UGST_R", lpiTaxDetail.getUGST());
			calGstMap.put("LPI_IGST_R", lpiTaxDetail.getIGST());
			calGstMap.put("LPI_CESS_R", lpiTaxDetail.getCESS());
		}

		if (lppTaxDetail == null) {
			addZeroifNotContains(calGstMap, "LPP_CGST_R");
			addZeroifNotContains(calGstMap, "LPP_SGST_R");
			addZeroifNotContains(calGstMap, "LPP_UGST_R");
			addZeroifNotContains(calGstMap, "LPP_IGST_R");
			addZeroifNotContains(calGstMap, "LPP_CESS_R");
		} else {
			calGstMap.put("LPP_CGST_R", lppTaxDetail.getCGST());
			calGstMap.put("LPP_SGST_R", lppTaxDetail.getSGST());
			calGstMap.put("LPP_UGST_R", lppTaxDetail.getUGST());
			calGstMap.put("LPP_IGST_R", lppTaxDetail.getIGST());
			calGstMap.put("LPP_CESS_R", lppTaxDetail.getCESS());
		}

		aeEvent.getDataMap().putAll(calGstMap);

		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());

		long linkedTranId = 0;
		if (lppAccountingID != Long.MIN_VALUE) {
			aeEvent.getAcSetIDList().add(lppAccountingID);
			aeEvent.setAccountingEvent(lppEventCode);
			aeEvent.setEventProperties(fm.getEventProperties());
			postAccountingEOD(aeEvent);

			linkedTranId = aeEvent.getLinkedTranId();
			finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

			if (linkedTranId > 0 && lppAmz.compareTo(BigDecimal.ZERO) > 0) {
				saveOrUpdateReceivable(aeEvent, calGstMap, "LPP");
			}
		}

		if (lpiAccountingID != Long.MIN_VALUE) {
			aeEvent.getAcSetIDList().add(lpiAccountingID);
			aeEvent.setAccountingEvent(lpiEventCode);
			postAccountingEOD(aeEvent);

			linkedTranId = aeEvent.getLinkedTranId();
			finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

			if (linkedTranId > 0 && lpiAmz.compareTo(BigDecimal.ZERO) > 0) {
				saveOrUpdateReceivable(aeEvent, calGstMap, "LPI");
			}
		}

		EventProperties eventProperties = custEODEvent.getEventProperties();
		boolean gstInvOnDue = false;
		if (eventProperties.isParameterLoaded()) {
			gstInvOnDue = eventProperties.isGstInvOnDue();
		} else {
			gstInvOnDue = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
		}

		if (gstInvOnDue) {
			List<FinFeeDetail> feesList = getFeeDetail(lppFeeType, taxPercmap, calGstMap, aeEvent);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(detail);
			invoiceDetail.setFinFeeDetailsList(feesList);
			invoiceDetail.setOrigination(false);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setDbInvSetReq(false);

			if (CollectionUtils.isNotEmpty(feesList)) {
				invoiceDetail.setFinFeeDetailsList(feesList);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
				lppDueInvoiceId = gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
			}

			feesList = getFeeDetail(lpiFeeType, taxPercmap, calGstMap, aeEvent);

			if (CollectionUtils.isNotEmpty(feesList)) {
				invoiceDetail.setFinFeeDetailsList(feesList);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
				lpiDueInvoiceId = gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
			}
		}

		// Save LPP Tax Details
		if (lpiTaxDetail != null) {
			lpiTaxDetail.setInvoiceID(lpiDueInvoiceId);
			finODAmzTaxDetailDAO.save(lpiTaxDetail);
		}

		if (lppTaxDetail != null) {
			lppTaxDetail.setInvoiceID(lppDueInvoiceId);
			finODAmzTaxDetailDAO.save(lppTaxDetail);
		}

		finEODEvent.setUpdLBDPostings(true);

		// LPP & LPI Due Amount , Which is already marked as Income/Receivable should be updated
		pfd.setLppTillLBD(lppTillLBD.add(lppAmz));
		pfd.setGstLppTillLBD(lppGSTTillLBD.add(lppGSTAmz));
		pfd.setLpiTillLBD(lpiTillLBD.add(lpiAmz));
		pfd.setGstLpiTillLBD(lpiGSTTillLBD.add(lpiGSTAmz));

		logger.info("Processing Late Payment accrual for the customer ID >> {} completed.", custID);
	}

	private void saveOrUpdateReceivable(AEEvent aeEvent, Map<String, BigDecimal> calGstMap, String feeTypeCode) {
		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		long finID = aeEvent.getFinID();
		String finReference = aeEvent.getFinReference();

		BigDecimal amzAmount = BigDecimal.ZERO;

		FinTaxReceivable taxRcv = finODAmzTaxDetailDAO.getFinTaxReceivable(finID, feeTypeCode);
		boolean isSave = false;
		if (taxRcv == null) {
			taxRcv = new FinTaxReceivable();
			taxRcv.setFinID(finID);
			taxRcv.setFinReference(finReference);
			taxRcv.setTaxFor(feeTypeCode);
			isSave = true;
		}

		if ("LPI".equals(feeTypeCode)) {
			amzAmount = aeAmountCodes.getdLPIAmz();
			taxRcv.setCGST(taxRcv.getCGST().add(calGstMap.get("LPI_CGST_R")));
			taxRcv.setSGST(taxRcv.getSGST().add(calGstMap.get("LPI_SGST_R")));
			taxRcv.setUGST(taxRcv.getUGST().add(calGstMap.get("LPI_UGST_R")));
			taxRcv.setIGST(taxRcv.getIGST().add(calGstMap.get("LPI_IGST_R")));
			taxRcv.setCESS(taxRcv.getCESS().add(calGstMap.get("LPI_CESS_R")));
		} else {
			amzAmount = aeAmountCodes.getdLPPAmz();
			taxRcv.setCGST(taxRcv.getCGST().add(calGstMap.get("LPP_CGST_R")));
			taxRcv.setSGST(taxRcv.getSGST().add(calGstMap.get("LPP_SGST_R")));
			taxRcv.setUGST(taxRcv.getUGST().add(calGstMap.get("LPP_UGST_R")));
			taxRcv.setIGST(taxRcv.getIGST().add(calGstMap.get("LPP_IGST_R")));
			taxRcv.setCESS(taxRcv.getCESS().add(calGstMap.get("LPP_CESS_R")));
		}

		taxRcv.setReceivableAmount(taxRcv.getReceivableAmount().add(amzAmount));

		if (isSave) {
			logger.info("Creating Tax Receivable for FinReference >> {} and FeeCode >> {}", finReference, feeTypeCode);
			finODAmzTaxDetailDAO.saveTaxReceivable(taxRcv);
		} else {
			logger.info("updating Tax Receivable for FinReference >> {} and FeeCode >> {}", finReference, feeTypeCode);
			finODAmzTaxDetailDAO.updateTaxReceivable(taxRcv);
		}

	}

	private List<FinFeeDetail> getFeeDetail(FeeType feeType, Map<String, BigDecimal> taxPercMap,
			Map<String, BigDecimal> calGstMap, AEEvent aeEvent) {
		List<FinFeeDetail> list = new ArrayList<>();

		if (feeType == null || taxPercMap == null) {
			return list;
		}

		FinFeeDetail fee = new FinFeeDetail();

		TaxHeader taxHeader = new TaxHeader();
		fee.setTaxHeader(taxHeader);
		fee.setFeeTypeCode(feeType.getFeeTypeCode());
		fee.setFeeTypeDesc(feeType.getFeeTypeDesc());
		fee.setTaxApplicable(true);
		fee.setOriginationFee(false);
		fee.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPPAmz());

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

		if (Allocation.ODC.equals(feeType.getFeeTypeCode())) {
			cgstTax.setNetTax(calGstMap.get("LPP_CGST_R"));
			sgstTax.setNetTax(calGstMap.get("LPP_SGST_R"));
			igstTax.setNetTax(calGstMap.get("LPP_IGST_R"));
			ugstTax.setNetTax(calGstMap.get("LPP_UGST_R"));
			cessTax.setNetTax(calGstMap.get("LPP_CESS_R"));
		} else {
			cgstTax.setNetTax(calGstMap.get("LPI_CGST_R"));
			sgstTax.setNetTax(calGstMap.get("LPI_SGST_R"));
			igstTax.setNetTax(calGstMap.get("LPI_IGST_R"));
			ugstTax.setNetTax(calGstMap.get("LPI_UGST_R"));
			cessTax.setNetTax(calGstMap.get("LPI_CESS_R"));
		}

		taxHeader.getTaxDetails().add(cgstTax);
		taxHeader.getTaxDetails().add(sgstTax);
		taxHeader.getTaxDetails().add(igstTax);
		taxHeader.getTaxDetails().add(ugstTax);
		taxHeader.getTaxDetails().add(cessTax);

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(feeType.getTaxComponent())) {
			BigDecimal gstAmount = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax())
					.add(ugstTax.getNetTax()).add(cessTax.getNetTax());
			fee.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPPAmz().subtract(gstAmount));
		}

		list.add(fee);

		return list;

	}

	private void prepareFinanceDetail(FinanceDetail fd, CustEODEvent custEODEvent) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (fd.getFinanceTaxDetail() == null) {
			long finID = fm.getFinID();
			fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finID, ""));
		}

		CustomerAddres addres = customerAddresDAO.getHighPriorityCustAddr(fm.getCustID(), "_AView");

		if (addres != null) {
			CustomerDetails customerDetails = new CustomerDetails();
			List<CustomerAddres> addressList = new ArrayList<>();
			addressList.add(addres);
			customerDetails.setAddressList(addressList);
			customerDetails.setCustomer(custEODEvent.getCustomer());
			fd.setCustomerDetails(customerDetails);
		}
	}

	private BigDecimal getTotalTaxAmount(Map<String, BigDecimal> taxPercmap, BigDecimal amount, String taxType) {
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

		return gstAmount;
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

	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	private BigDecimal calculateGST(BigDecimal amount, FeeType feeType, Map<String, BigDecimal> taxPercmap) {
		BigDecimal gstAmount = BigDecimal.ZERO;

		if (feeType == null || !(feeType.isTaxApplicable() && feeType.isAmortzReq())) {
			return gstAmount;
		}

		return getTotalTaxAmount(taxPercmap, amount, feeType.getTaxComponent());
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}
}
