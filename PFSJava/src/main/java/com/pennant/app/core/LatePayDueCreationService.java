/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AccrualService.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class LatePayDueCreationService extends ServiceHelper {
	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = Logger.getLogger(LatePayDueCreationService.class);

	private FinFeeDetailService finFeeDetailService;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerAddresDAO customerAddresDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;

	//GST Invoice Report changes
	private GSTInvoiceTxnService gstInvoiceTxnService;

	public LatePayDueCreationService() {
		super();
	}

	public CustEODEvent processLatePayAccrual(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		// Post LPP / LPI Accruals on Application Extended Month End OR Application Month End OR Daily
		boolean isAmzPostToday = false;
		if (custEODEvent.getEodDate().compareTo(DateUtility.getMonthEnd(custEODEvent.getEodDate())) == 0) {
			isAmzPostToday = true;
		}

		if (isAmzPostToday) {
			for (FinEODEvent finEODEvent : finEODEvents) {
				postLatePayAccruals(finEODEvent, custEODEvent);
			}
		}
		logger.debug(" Leaving ");
		return custEODEvent;
	}

	/**
	 * @param financeMain
	 * @param resultSet
	 * @throws Exception
	 */
	public void postLatePayAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		String lppEventCode = AccountEventConstants.ACCEVENT_LPPAMZ;
		String lpiEventCode = AccountEventConstants.ACCEVENT_LPIAMZ;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		long lppAccountingID = getAccountingID(main, lppEventCode);
		long lpiAccountingID = getAccountingID(main, lpiEventCode);

		if (lppAccountingID == Long.MIN_VALUE && lpiAccountingID == Long.MIN_VALUE) {
			return;
		}

		// Setting LPI Amount from Overdue Details for LPI amortization
		Map<String, Object> gstExecutionMap = null;
		Map<String, BigDecimal> taxPercmap = null;
		FeeType lpiFeeType = null;
		FeeType lppFeeType = null;

		//Prepare Finance Detail
		FinanceDetail detail = new FinanceDetail();
		detail.getFinScheduleData().setFinanceMain(main);
		detail.getFinScheduleData().setFinanceType(finEODEvent.getFinType());
		prepareFinanceDetail(detail, custEODEvent);

		if (StringUtils.equals(ImplementationConstants.LPP_GST_DUE_ON, "A")) {

			if (!finEODEvent.getFinODDetails().isEmpty()) {

				List<FinODDetails> odList = finEODEvent.getFinODDetails();
				BigDecimal lppAmount = BigDecimal.ZERO;
				BigDecimal lpiAmount = BigDecimal.ZERO;
				for (int i = 0; i < odList.size(); i++) {
					lppAmount = lppAmount.add(odList.get(i).getTotPenaltyAmt());
					lpiAmount = lpiAmount.add(odList.get(i).getLPIAmt());
				}

				// GST parameters for State wise Account Number building
				gstExecutionMap = GSTCalculator.getGSTDataMap(main.getFinReference());

				finPftDetail.setLpiAmount(lpiAmount);
				finPftDetail.setLppAmount(lppAmount);

				// Calculate GSTAmount 
				boolean gstCalReq = false;

				// LPI GST Amount calculation
				if (finPftDetail.getLpiAmount().compareTo(BigDecimal.ZERO) > 0) {
					lpiFeeType = getFeeTypeDAO().getTaxDetailByCode(RepayConstants.ALLOCATION_LPFT);
					if (lpiFeeType != null) {
						if (lpiFeeType.isTaxApplicable()) {
							gstCalReq = true;
						}
						if (!lpiFeeType.isAmortzReq()) {
							finPftDetail.setLpiAmount(BigDecimal.ZERO);
						}
					} else {
						finPftDetail.setLpiAmount(BigDecimal.ZERO);
					}
				}

				// LPP GST Amount calculation
				if (finPftDetail.getLppAmount().compareTo(BigDecimal.ZERO) > 0) {
					lppFeeType = getFeeTypeDAO().getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
					if (lppFeeType != null) {
						if (lppFeeType.isTaxApplicable()) {
							gstCalReq = true;
						}
						if (!lppFeeType.isAmortzReq()) {
							finPftDetail.setLppAmount(BigDecimal.ZERO);
						}
					} else {
						finPftDetail.setLppAmount(BigDecimal.ZERO);
					}
				}

				// IF GST Calculation Required for LPI or LPP 
				if (gstCalReq) {

					taxPercmap = GSTCalculator.getTaxPercentages(gstExecutionMap, main.getFinCcy());

					// Calculate LPI GST Amount
					if (finPftDetail.getLpiAmount().compareTo(BigDecimal.ZERO) > 0 && lpiFeeType != null
							&& lpiFeeType.isTaxApplicable() && lpiFeeType.isAmortzReq()) {
						BigDecimal gstAmount = getTotalTaxAmount(taxPercmap, finPftDetail.getLpiAmount(),
								lpiFeeType.getTaxComponent());
						finPftDetail.setGstLpiAmount(gstAmount);
					}

					// Calculate LPP GST Amount
					if (finPftDetail.getLppAmount().compareTo(BigDecimal.ZERO) > 0 && lppFeeType != null
							&& lppFeeType.isTaxApplicable() && lppFeeType.isAmortzReq()) {
						BigDecimal gstAmount = getTotalTaxAmount(taxPercmap, finPftDetail.getLppAmount(),
								lppFeeType.getTaxComponent());
						finPftDetail.setGstLppAmount(gstAmount);
					}
				}
			}
		}

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = new AEAmountCodes();

		aeEvent.setFinReference(finPftDetail.getFinReference());
		aeEvent.setPostDate(custEODEvent.getEodValueDate());
		aeEvent.setValueDate(custEODEvent.getEodValueDate());
		aeEvent.setSchdDate(custEODEvent.getEodValueDate());
		aeEvent.setBranch(finPftDetail.getFinBranch());
		aeEvent.setCcy(finPftDetail.getFinCcy());
		aeEvent.setFinType(finPftDetail.getFinType());
		aeEvent.setCustID(finPftDetail.getCustId());

		// Finance Fields
		amountCodes.setFinType(finPftDetail.getFinType());

		// LPI Amortization calculation
		if (finPftDetail.getLpiAmount().compareTo(BigDecimal.ZERO) > 0) {
			amountCodes.setdLPIAmz(finPftDetail.getLpiAmount().subtract(finPftDetail.getLpiTillLBD()));

			// Calculate GST Amount on LPI Amount 
			if (finPftDetail.getGstLpiAmount().compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdGSTLPIAmz(finPftDetail.getGstLpiAmount().subtract(finPftDetail.getGstLpiTillLBD()));
			}
		}

		// LPP Amortization calculation
		if (finPftDetail.getLppAmount().compareTo(BigDecimal.ZERO) > 0) {
			amountCodes.setdLPPAmz(finPftDetail.getLppAmount().subtract(finPftDetail.getLppTillLBD()));

			// Calculate GST Amount on LPP Amount 
			if (finPftDetail.getGstLppAmount().compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdGSTLPPAmz(finPftDetail.getGstLppAmount().subtract(finPftDetail.getGstLppTillLBD()));
			}
		}
		aeEvent.setAeAmountCodes(amountCodes);
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());

		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					aeEvent.getDataMap().put(key, gstExecutionMap.get(key));
				}
			}
		}

		// LPI GST Amount for Postings
		Map<String, BigDecimal> calGstMap = null;
		boolean addGSTInvoice = false;

		if (StringUtils.equals(ImplementationConstants.LPP_GST_DUE_ON, "A")) {
			calGstMap = new HashMap<>();

			if (aeEvent.getAeAmountCodes().getdGSTLPIAmz().compareTo(BigDecimal.ZERO) > 0 && lpiFeeType != null
					&& lpiFeeType.isTaxApplicable()) {

				if (taxPercmap == null) {
					detail.getFinScheduleData().setFinanceMain(main);
					taxPercmap = GSTCalculator.getTaxPercentages(gstExecutionMap, main.getFinCcy());
				}

				FinODAmzTaxDetail taxDetail = getTaxDetail(taxPercmap, aeEvent.getAeAmountCodes().getdGSTLPIAmz(),
						lpiFeeType.getTaxComponent());
				taxDetail.setFinReference(finPftDetail.getFinReference());
				taxDetail.setTaxFor("LPI");
				taxDetail.setAmount(aeEvent.getAeAmountCodes().getdLPIAmz());
				taxDetail.setValueDate(DateUtility.addDays(custEODEvent.getEodValueDate(), -1));
				taxDetail.setPostDate(new Timestamp(System.currentTimeMillis()));

				calGstMap.put("LPI_CGST_R", taxDetail.getCGST());
				calGstMap.put("LPI_SGST_R", taxDetail.getSGST());
				calGstMap.put("LPI_UGST_R", taxDetail.getUGST());
				calGstMap.put("LPI_IGST_R", taxDetail.getIGST());

				// Save Tax Details
				getFinODAmzTaxDetailDAO().save(taxDetail);

				String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");
				if (StringUtils.equals(isGSTInvOnDue, PennantConstants.YES)) {
					addGSTInvoice = true;
				}

			} else {
				addZeroifNotContains(calGstMap, "LPI_CGST_R");
				addZeroifNotContains(calGstMap, "LPI_SGST_R");
				addZeroifNotContains(calGstMap, "LPI_UGST_R");
				addZeroifNotContains(calGstMap, "LPI_IGST_R");
			}

			// LPP GST Amount for Postings
			if (aeEvent.getAeAmountCodes().getdGSTLPPAmz().compareTo(BigDecimal.ZERO) > 0 && lppFeeType != null
					&& lppFeeType.isTaxApplicable()) {

				if (taxPercmap == null) {
					detail.getFinScheduleData().setFinanceMain(main);
					taxPercmap = GSTCalculator.getTaxPercentages(gstExecutionMap, main.getFinCcy());
				}

				FinODAmzTaxDetail taxDetail = getTaxDetail(taxPercmap, aeEvent.getAeAmountCodes().getdGSTLPPAmz(),
						lppFeeType.getTaxComponent());
				taxDetail.setFinReference(finPftDetail.getFinReference());
				taxDetail.setTaxFor("LPP");
				taxDetail.setAmount(aeEvent.getAeAmountCodes().getdLPPAmz());
				taxDetail.setValueDate(DateUtility.addDays(custEODEvent.getEodValueDate(), -1));
				taxDetail.setPostDate(new Timestamp(System.currentTimeMillis()));

				calGstMap.put("LPP_CGST_R", taxDetail.getCGST());
				calGstMap.put("LPP_SGST_R", taxDetail.getSGST());
				calGstMap.put("LPP_UGST_R", taxDetail.getUGST());
				calGstMap.put("LPP_IGST_R", taxDetail.getIGST());

				// Save Tax Details
				getFinODAmzTaxDetailDAO().save(taxDetail);

				String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");
				if (StringUtils.equals(isGSTInvOnDue, PennantConstants.YES)) {
					addGSTInvoice = true;
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
		}

		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		if (lppAccountingID != Long.MIN_VALUE) {
			aeEvent.getAcSetIDList().add(lppAccountingID);

			//Postings Process and save all postings related to finance for one time accounts update
			aeEvent.setAccountingEvent(lppEventCode);
			aeEvent = postAccountingEOD(aeEvent);
			finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

			//GST Invoice Preparation
			if (aeEvent.getLinkedTranId() > 0) {

				// LPP Receivable Data Update for Future Accounting
				if (aeEvent.getAeAmountCodes().getdLPPAmz().compareTo(BigDecimal.ZERO) > 0) {

					// Save Tax Receivable Details
					FinTaxReceivable taxRcv = getFinODAmzTaxDetailDAO()
							.getFinTaxReceivable(finPftDetail.getFinReference(), "LPP");
					boolean isSave = false;
					if (taxRcv == null) {
						taxRcv = new FinTaxReceivable();
						taxRcv.setFinReference(finPftDetail.getFinReference());
						taxRcv.setTaxFor("LPP");
						isSave = true;
					}

					if (calGstMap != null) {
						taxRcv.setCGST(taxRcv.getCGST().add(calGstMap.get("LPP_CGST_R")));
						taxRcv.setSGST(taxRcv.getSGST().add(calGstMap.get("LPP_SGST_R")));
						taxRcv.setUGST(taxRcv.getUGST().add(calGstMap.get("LPP_UGST_R")));
						taxRcv.setIGST(taxRcv.getIGST().add(calGstMap.get("LPP_IGST_R")));
					}

					taxRcv.setReceivableAmount(
							taxRcv.getReceivableAmount().add(aeEvent.getAeAmountCodes().getdLPPAmz()));

					if (isSave) {
						getFinODAmzTaxDetailDAO().saveTaxReceivable(taxRcv);
					} else {
						getFinODAmzTaxDetailDAO().updateTaxReceivable(taxRcv);
					}
				}

				// GST Invoice Generation
				if (addGSTInvoice) {

					List<FinFeeDetail> feesList = prepareFeesList(lppFeeType, null, taxPercmap, calGstMap, aeEvent);

					if (CollectionUtils.isNotEmpty(feesList)) {
						this.gstInvoiceTxnService.gstInvoicePreparation(aeEvent.getLinkedTranId(), detail, feesList,
								null, PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT, false, false);
					}
				}
			}
		}

		if (lpiAccountingID != Long.MIN_VALUE) {
			aeEvent.getAcSetIDList().add(lpiAccountingID);
			aeEvent.getReturnDataSet().clear();

			//Postings Process and save all postings related to finance for one time accounts update
			aeEvent.setAccountingEvent(lpiEventCode);
			aeEvent = postAccountingEOD(aeEvent);
			finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

			//GST Invoice Preparation
			if (aeEvent.getLinkedTranId() > 0) {

				// LPP Receivable Data Update for Future Accounting
				if (aeEvent.getAeAmountCodes().getdLPIAmz().compareTo(BigDecimal.ZERO) > 0) {

					// Save Tax Receivable Details
					FinTaxReceivable taxRcv = getFinODAmzTaxDetailDAO()
							.getFinTaxReceivable(finPftDetail.getFinReference(), "LPI");
					boolean isSave = false;
					if (taxRcv == null) {
						taxRcv = new FinTaxReceivable();
						taxRcv.setFinReference(finPftDetail.getFinReference());
						taxRcv.setTaxFor("LPI");
						isSave = true;
					}

					if (calGstMap != null) {
						taxRcv.setCGST(taxRcv.getCGST().add(calGstMap.get("LPI_CGST_R")));
						taxRcv.setSGST(taxRcv.getSGST().add(calGstMap.get("LPI_SGST_R")));
						taxRcv.setUGST(taxRcv.getUGST().add(calGstMap.get("LPI_UGST_R")));
						taxRcv.setIGST(taxRcv.getIGST().add(calGstMap.get("LPI_IGST_R")));
					}

					taxRcv.setReceivableAmount(
							taxRcv.getReceivableAmount().add(aeEvent.getAeAmountCodes().getdLPIAmz()));

					if (isSave) {
						getFinODAmzTaxDetailDAO().saveTaxReceivable(taxRcv);
					} else {
						getFinODAmzTaxDetailDAO().updateTaxReceivable(taxRcv);
					}
				}

				// GST Invoice Generation
				if (addGSTInvoice) {
					List<FinFeeDetail> feesList = prepareFeesList(null, lpiFeeType, taxPercmap, calGstMap, aeEvent);
					if (CollectionUtils.isNotEmpty(feesList)) {
						this.gstInvoiceTxnService.gstInvoicePreparation(aeEvent.getLinkedTranId(), detail, feesList,
								null, PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT, false, false);
					}
				}
			}
		}

		finEODEvent.setUpdLBDPostings(true);

		// LPP & LPI Due Amount , Which is already marked as Income/Receivable should be updated
		finPftDetail.setLppTillLBD(finPftDetail.getLppTillLBD().add(aeEvent.getAeAmountCodes().getdLPPAmz()));
		finPftDetail.setGstLppTillLBD(finPftDetail.getGstLppTillLBD().add(aeEvent.getAeAmountCodes().getdGSTLPPAmz()));
		finPftDetail.setLpiTillLBD(finPftDetail.getLpiTillLBD().add(aeEvent.getAeAmountCodes().getdLPIAmz()));
		finPftDetail.setGstLpiTillLBD(finPftDetail.getGstLpiTillLBD().add(aeEvent.getAeAmountCodes().getdGSTLPIAmz()));

		logger.debug(" Leaving ");
	}

	private List<FinFeeDetail> prepareFeesList(FeeType lppFeeType, FeeType lpiFeeType,
			Map<String, BigDecimal> taxPercMap, Map<String, BigDecimal> calGstMap, AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetailsList = new ArrayList<FinFeeDetail>();
		FinFeeDetail finFeeDetail = null;

		//LPP Fees
		if (lppFeeType != null) {
			finFeeDetail = new FinFeeDetail();
			FinTaxDetails finTaxDetails = new FinTaxDetails();
			finFeeDetail.setFinTaxDetails(finTaxDetails);

			finFeeDetail.setFeeTypeCode(lppFeeType.getFeeTypeCode());
			finFeeDetail.setFeeTypeDesc(lppFeeType.getFeeTypeDesc());
			finFeeDetail.setTaxApplicable(true);
			finFeeDetail.setOriginationFee(false);
			finFeeDetail.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPPAmz());

			if (taxPercMap != null && calGstMap != null) {
				finFeeDetail.setCgst(taxPercMap.get(RuleConstants.CODE_CGST));
				finFeeDetail.setSgst(taxPercMap.get(RuleConstants.CODE_SGST));
				finFeeDetail.setIgst(taxPercMap.get(RuleConstants.CODE_IGST));
				finFeeDetail.setUgst(taxPercMap.get(RuleConstants.CODE_UGST));

				finTaxDetails.setNetCGST(calGstMap.get("LPP_CGST_R"));
				finTaxDetails.setNetSGST(calGstMap.get("LPP_SGST_R"));
				finTaxDetails.setNetIGST(calGstMap.get("LPP_IGST_R"));
				finTaxDetails.setNetUGST(calGstMap.get("LPP_UGST_R"));

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(lppFeeType.getTaxComponent())) {
					BigDecimal gstAmount = finTaxDetails.getNetCGST().add(finTaxDetails.getNetSGST())
							.add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetUGST());
					finFeeDetail.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPPAmz().subtract(gstAmount));
				}
			}

			finFeeDetailsList.add(finFeeDetail);
		}

		//LPI Fees

		if (lpiFeeType != null) {
			finFeeDetail = new FinFeeDetail();
			FinTaxDetails finTaxDetails = new FinTaxDetails();
			finFeeDetail.setFinTaxDetails(finTaxDetails);

			finFeeDetail.setFeeTypeCode(lpiFeeType.getFeeTypeCode());
			finFeeDetail.setFeeTypeDesc(lpiFeeType.getFeeTypeDesc());
			finFeeDetail.setTaxApplicable(true);
			finFeeDetail.setOriginationFee(false);
			finFeeDetail.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPIAmz());

			if (taxPercMap != null && calGstMap != null) {
				finFeeDetail.setCgst(taxPercMap.get(RuleConstants.CODE_CGST));
				finFeeDetail.setSgst(taxPercMap.get(RuleConstants.CODE_SGST));
				finFeeDetail.setIgst(taxPercMap.get(RuleConstants.CODE_IGST));
				finFeeDetail.setUgst(taxPercMap.get(RuleConstants.CODE_UGST));

				finTaxDetails.setNetCGST(calGstMap.get("LPI_CGST_R"));
				finTaxDetails.setNetSGST(calGstMap.get("LPI_SGST_R"));
				finTaxDetails.setNetIGST(calGstMap.get("LPI_IGST_R"));
				finTaxDetails.setNetUGST(calGstMap.get("LPI_UGST_R"));

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(lpiFeeType.getTaxComponent())) {
					BigDecimal gstAmount = finTaxDetails.getNetCGST().add(finTaxDetails.getNetSGST())
							.add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetUGST());
					finFeeDetail.setNetAmountOriginal(aeEvent.getAeAmountCodes().getdLPIAmz().subtract(gstAmount));
				}
			}

			finFeeDetailsList.add(finFeeDetail);
		}

		logger.debug(Literal.LEAVING);
		return finFeeDetailsList;
	}

	/**
	 * Method for Prepare FianceDetail for GST Invoice Report Preparation
	 * 
	 * @param financeDetail
	 * @param custEODEvent
	 */
	private void prepareFinanceDetail(FinanceDetail financeDetail, CustEODEvent custEODEvent) {

		// Set Tax Details if Already exists
		if (financeDetail.getFinanceTaxDetail() == null) {
			financeDetail.setFinanceTaxDetail(getFinanceTaxDetailDAO()
					.getFinanceTaxDetail(financeDetail.getFinScheduleData().getFinanceMain().getFinReference(), ""));
		}

		CustomerAddres addres = getCustomerAddresDAO()
				.getHighPriorityCustAddr(financeDetail.getFinScheduleData().getFinanceMain().getCustID(), "_AView");
		if (addres != null) {
			CustomerDetails customerDetails = new CustomerDetails();
			List<CustomerAddres> addressList = new ArrayList<CustomerAddres>();
			addressList.add(addres);
			customerDetails.setAddressList(addressList);
			customerDetails.setCustomer(custEODEvent.getCustomer());
			financeDetail.setCustomerDetails(customerDetails);
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

	public FeeTypeDAO getFeeTypeDAO() {
		return feeTypeDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public FinODAmzTaxDetailDAO getFinODAmzTaxDetailDAO() {
		return finODAmzTaxDetailDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

}
