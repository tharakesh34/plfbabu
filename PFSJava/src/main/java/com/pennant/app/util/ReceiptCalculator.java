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
 ********************************************************************************************************
 *                                 FILE HEADER                                              			*
 ********************************************************************************************************
 *
 * FileName    		:  ReceiptCalculator.java															*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES															*
 *                                                                  		
 * Creation Date    :  26-04-2011																		*
 *                                                                  
 * Modified Date    :  30-07-2011																		*
 *                                                                  
 * Description 		:												 									*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 26-04-2018		Vinay					 0.2     	As discussed with siva kumar 		*
 * 														In Suspense (NPA) case to
 * 														Allow different repay Heirarchy		* 
 *                                                                                          * 
 * 09-06-2018       Siva					 0.3        GST Rounding issues with Bounce on 
 * 														exclusive case on top  of GST       * 
 *                                                                                          * 
 * 13-06-2018       Siva					 0.4        Partial Settlement Amount 
 * 														Double Entry                        * 
 * 
 * 26-06-2018       Siva					 0.5        Early Settlement balance Amount 
 * 														not closing fully(127641)           * 
 * 
 * 26-06-2018       Siva					 0.6        Early Settlement balance Amount 
 * 														not closing fully(127641)           * 
 * 
 * 14-07-2018		Siva			 		 0.7		Payable GST, Bounce refer changes, 
 * 																					LPP GST * 
 * 
 * 26-07-2018		Siva			 		 0.8		TDS ROunding for Auto Allocation 	*
 * 
 * 01-08-2018  		Mangapathi				 0.9		  PSD - Ticket : 125445, 125588					*
 * 														  Mail Sub : Freezing Period, Dt : 30-May-2018  *
 *                                                        To address Freezing period case when schedule *
 *														  term is in Presentment.
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.NPAService;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.rits.cloning.Cloner;

public class ReceiptCalculator implements Serializable {
	private static final long serialVersionUID = 8062681791631293126L;
	private static Logger logger = Logger.getLogger(ReceiptCalculator.class);

	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FeeTypeDAO feeTypeDAO;
	private AccrualService accrualService;
	private FeeCalculator feeCalculator;
	private FinanceMain financeMain;
	private LatePayMarkingService latePayMarkingService;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private NPAService npaService;
	private BigDecimal actualOdPaid = BigDecimal.ZERO;
	private List<FinanceScheduleDetail> finSchdDtls = new ArrayList<>();
	private List<Long> inProcessReceipts = new ArrayList<>();
	public BigDecimal big100 = new BigDecimal(100);
	public String tdsRoundMode = null;
	public int tdsRoundingTarget = 0;
	public BigDecimal tdsPerc = BigDecimal.ZERO;
	public BigDecimal tdsMultiplier = BigDecimal.ZERO;

	private static final String DESC_INC_TAX = " (Inclusive)";
	private static final String DESC_EXC_TAX = " (Exclusive)";

	String taxRoundMode = null;
	int taxRoundingTarget = 0;
	int receiptPurposeCtg = -1;

	BigDecimal cgstPerc = BigDecimal.ZERO;
	BigDecimal sgstPerc = BigDecimal.ZERO;
	BigDecimal ugstPerc = BigDecimal.ZERO;
	BigDecimal igstPerc = BigDecimal.ZERO;
	BigDecimal cessPerc = BigDecimal.ZERO;
	BigDecimal tgstPerc = BigDecimal.ZERO;

	List<ReceiptAllocationDetail> allocated = null;

	//boolean isAdjSchedule = false;
	int rcdIdx = -1;
	int rphIdx = -1;
	int rsdIdx = -1;
	int fodIdx = -1;

	// Default Constructor
	public ReceiptCalculator() {
		super();
	}

	/*
	 * ___________________________________________________________________________________________
	 * 
	 * Main Methods ___________________________________________________________________________________________
	 */

	public FinReceiptData initiateReceipt(FinReceiptData receiptData, boolean isPresentment) {
		setReceiptCategory(receiptData.getReceiptHeader().getReceiptPurpose());
		return procInitiateReceipt(receiptData, isPresentment);
	}

	public int setReceiptCategory(String receiptPurpose) {
		receiptPurposeCtg = -1;
		// Receipt Purpose Category for ease of code and performance
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			receiptPurposeCtg = 0;
		} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			receiptPurposeCtg = 1;
		} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			receiptPurposeCtg = 2;
		} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			receiptPurposeCtg = 3;
		}

		return receiptPurposeCtg;
	}

	/** To Calculate the Amounts for given schedule */
	private FinReceiptData procInitiateReceipt(FinReceiptData receiptData, boolean isPresentment) {
		logger.debug("Entering");
		// Initialize Repay
		receiptData.setPresentment(isPresentment);
		if ("I".equals(receiptData.getBuildProcess())) {
			receiptData = initializeReceipt(receiptData);
		} else if ("R".equals(receiptData.getBuildProcess())) {
			receiptData = recalReceipt(receiptData, isPresentment);
		}

		receiptData = setTotals(receiptData, 0);

		logger.debug("Leaving");
		return receiptData;
	}

	public FinReceiptData addPartPaymentAlloc(FinReceiptData receiptData) {
		ReceiptAllocationDetail allocation = new ReceiptAllocationDetail();
		allocation.setAllocationID(receiptData.getReceiptHeader().getAllocations().size());
		allocation.setAllocationType(RepayConstants.ALLOCATION_PP);
		allocation.setTotalDue(receiptData.getRemBal());
		allocation.setAllocationTo(0);
		allocation.setPaidAmount(receiptData.getRemBal());
		receiptData.getReceiptHeader().getAllocations().add(allocation);
		return receiptData;
	}

	public FinReceiptData removePartPaymentAlloc(FinReceiptData receiptData) {
		List<ReceiptAllocationDetail> radList = receiptData.getReceiptHeader().getAllocations();

		for (int i = 0; i < radList.size(); i++) {

			if (radList.get(i).getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
				radList.remove(i);
				i = i - 1;
			}

		}
		return receiptData;
	}

	/**
	 * Method for Initialize the data from Finance Details to Receipt Data to render Summary
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData initializeReceipt(FinReceiptData receiptData) {
		logger.debug("Entering");
		financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		receiptData.setFinReference(financeMain.getFinReference());
		receiptData.setRepayMain(null);
		boolean isAllocated = false;
		Date valueDate = receiptData.getValueDate();

		if (receiptPurposeCtg == 2) {
			receiptData.getFinanceDetail().getFinScheduleData().setFeeEvent(AccountEventConstants.ACCEVENT_EARLYSTL);
		} else if (receiptPurposeCtg == 1) {
			receiptData.getFinanceDetail().getFinScheduleData().setFeeEvent(AccountEventConstants.ACCEVENT_EARLYPAY);
		}

		receiptData.getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);
		// Temporary fix for API call
		FinServiceInstruction fsi = receiptData.getFinanceDetail().getFinScheduleData().getFinServiceInstruction();
		if (valueDate == null) {
			if (fsi != null) {
				valueDate = fsi.getValueDate();
			} else {
				receiptData.getReceiptHeader().setValueDate(receiptData.getValueDate());
			}
		}

		RepayMain repayMain = new RepayMain();
		repayMain.setFinReference(financeMain.getFinReference());
		repayMain.setFinCcy(financeMain.getFinCcy());
		repayMain.setProfitDaysBais(financeMain.getProfitDaysBasis());
		repayMain.setFinType(financeMain.getFinType());
		repayMain.setLovDescFinTypeName(financeMain.getLovDescFinTypeName());
		repayMain.setFinBranch(financeMain.getFinBranch());
		repayMain.setLovDescFinBranchName(financeMain.getLovDescFinBranchName());
		repayMain.setCustID(financeMain.getCustID());
		repayMain.setLovDescCustCIF(financeMain.getLovDescCustCIF());
		repayMain.setLovDescSalutationName(financeMain.getLovDescSalutationName());
		repayMain.setLovDescCustFName(financeMain.getLovDescCustFName());
		repayMain.setLovDescCustLName(financeMain.getLovDescCustLName());
		repayMain.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
		repayMain.setDateStart(financeMain.getFinStartDate());
		repayMain.setDateMatuirty(financeMain.getMaturityDate());
		repayMain.setAccrued(receiptData.getAccruedTillLBD());
		repayMain.setRepayAccountId(financeMain.getRepayAccountId());
		repayMain.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		repayMain.setPendindODCharges(receiptData.getPendingODC());
		repayMain.setEarlyPayEffectOn(financeMain.getLovDescFinScheduleOn());

		repayMain.setDateLastFullyPaid(financeMain.getFinStartDate());
		repayMain.setDateNextPaymentDue(financeMain.getMaturityDate());
		repayMain.setPrincipalPayNow(BigDecimal.ZERO);
		repayMain.setProfitPayNow(BigDecimal.ZERO);
		repayMain.setRefundNow(BigDecimal.ZERO);

		repayMain.setRepayAmountNow(BigDecimal.ZERO);
		repayMain.setRepayAmountExcess(BigDecimal.ZERO);
		receiptData.setRepayMain(repayMain);

		Cloner cloner = new Cloner();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		if (receiptData.isPresentment()) {
			if (!ImplementationConstants.ALLOW_OLDEST_DUE) {
				return initializePresentment(receiptData);
			}
		}
		finSchdDtls = cloner.deepClone(schdDetails);
		FinanceMain finMain = finScheduleData.getFinanceMain();

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinanceProfitDetail finPftDeatils = finScheduleData.getFinPftDeatil();
		processCIP(receiptData);
		if (receiptPurposeCtg == 2 && receiptData.getOrgFinPftDtls() == null) {
			FinanceProfitDetail orgFinPftDtls = cloner.deepClone(finPftDeatils);
			FinanceScheduleDetail prvSchd = financeScheduleDetailDAO.getPrvSchd(finMain.getFinReference(), valueDate);
			Date prvSchdDate = valueDate;

			if (prvSchd != null) {
				prvSchdDate = prvSchd.getSchDate();
			}

			orgFinPftDtls = accrualService.calProfitDetails(finMain, schdDetails, orgFinPftDtls, prvSchdDate);
			receiptData.setOrgFinPftDtls(cloner.deepClone(orgFinPftDtls));
		}

		if (rch.getValueDate() == null || rch.getReceiptDate().compareTo(rch.getValueDate()) != 0) {
			finPftDeatils = accrualService.calProfitDetails(finMain, finScheduleData.getFinanceScheduleDetails(),
					finPftDeatils, valueDate);
			rch.setValueDate(valueDate);
		}
		if (receiptData.getAllocList() != null && receiptData.getAllocList().size() > 0) {
			allocated = receiptData.getAllocList();
			isAllocated = true;

		}
		List<Date> presentmentDates = getPresentmentDates(receiptData, valueDate);
		receiptData = calSummaryDetail(receiptData, valueDate);
		List<ReceiptAllocationDetail> allocationsList = resetAllocationList(receiptData);
		receiptData.getReceiptHeader().setAllocations(allocationsList);

		receiptData = fetchODPenalties(receiptData, valueDate, presentmentDates);
		receiptData = fetchManualAdviseDetails(receiptData, valueDate);

		receiptData = setXcessPayables(receiptData);

		if (allocated != null && allocated.size() > 0) {
			allocated = receiptData.getAllocList();
			receiptData = setPaidValues(receiptData);
		}

		receiptData = setTotals(receiptData, 0);
		receiptData = fetchEventFees(receiptData, isAllocated);

		if (allocated != null && allocated.size() > 0) {
			allocated = receiptData.getAllocList();
			receiptData = setPaidValues(receiptData);
			receiptData = setWaivedValues(receiptData);
		}

		logger.debug("Leaving");
		return receiptData;
	}

	private FinReceiptData setPaidValues(FinReceiptData receiptData) {
		if (receiptData.isForeClosure() || StringUtils.equals(receiptData.getReceiptHeader().getAllocationType(),
				RepayConstants.ALLOCATIONTYPE_MANUAL)) {
			receiptData.setFCDueChanged(false);
			for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
				for (ReceiptAllocationDetail alloc : allocated) {
					if (allocate.getAllocationType().equals(alloc.getAllocationType())
							&& allocate.getAllocationTo() == alloc.getAllocationTo()) {
						if (!receiptData.isForeClosure()) {
							if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_FEE)) {
								allocate.setPaidAmount(allocate.getDueAmount());
								allocate.setPaidGST(allocate.getDueGST());
								allocate.setTotalPaid(allocate.getDueAmount());
								allocate.setTdsPaid(alloc.getTdsPaid());
							} else {
								allocate.setPaidAmount(alloc.getPaidAmount());
								allocate.setPaidGST(alloc.getPaidGST());
								allocate.setTotalPaid(alloc.getPaidAmount().add(alloc.getTdsPaid()));
								allocate.setTdsPaid(alloc.getTdsPaid());
							}
						} else {
							if (allocate.getTotalDue().compareTo(alloc.getTotalDue()) != 0) {
								receiptData.setFCDueChanged(true);
							}
							allocate.setWaivedAmount(alloc.getWaivedAmount());
							BigDecimal dueAmount;
							BigDecimal paidAmount;

							dueAmount = allocate.getTotalDue().subtract(alloc.getWaivedAmount());

							// Waiver GST Calculation
							if (StringUtils.isNotBlank(allocate.getTaxType())
									&& allocate.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
								calAllocationWaiverGST(receiptData.getFinanceDetail(), allocate.getWaivedAmount(),
										allocate);
							}

							if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
								if (allocate.getPaidAmount().compareTo(dueAmount) >= 0) {
									paidAmount = dueAmount;
									BigDecimal totalPaid = getPaidAmount(allocate, paidAmount);
									// Paid Amount GST calculations
									if (StringUtils.isNotBlank(allocate.getTaxType())) {
										allocate.setPaidCGST(BigDecimal.ZERO);
										allocate.setPaidSGST(BigDecimal.ZERO);
										allocate.setPaidIGST(BigDecimal.ZERO);
										allocate.setPaidUGST(BigDecimal.ZERO);
										allocate.setPaidGST(BigDecimal.ZERO);
										calAllocationPaidGST(receiptData.getFinanceDetail(), totalPaid, allocate,
												FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
									}
									allocate.setTdsPaid(allocate.getTdsPaid());
									allocate.setTotalPaid(paidAmount.add(allocate.getTdsPaid()));
									allocate.setPaidAmount(paidAmount);
								}
							} else {
								allocate.setPaidAmount(BigDecimal.ZERO);
								allocate.setTotalPaid(BigDecimal.ZERO);
								allocate.setPaidCGST(BigDecimal.ZERO);
								allocate.setPaidSGST(BigDecimal.ZERO);
								allocate.setPaidUGST(BigDecimal.ZERO);
								allocate.setPaidIGST(BigDecimal.ZERO);
								allocate.setPaidCESS(BigDecimal.ZERO);
								allocate.setPaidGST(BigDecimal.ZERO);
							}
						}
					}
				}
			}

			List<ReceiptAllocationDetail> radList = receiptData.getReceiptHeader().getAllocations();
			for (int i = 0; i < radList.size(); i++) {
				ReceiptAllocationDetail rad = radList.get(i);

				if (rad.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
					radList.remove(i);
					i = i - 1;
				}

			}
		}

		return receiptData;
	}

	private FinReceiptData setWaivedValues(FinReceiptData receiptData) {
		if (StringUtils.equals(receiptData.getReceiptHeader().getAllocationType(),
				RepayConstants.ALLOCATIONTYPE_AUTO)) {
			for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
				for (ReceiptAllocationDetail alloc : allocated) {
					if (allocate.getAllocationType().equals(alloc.getAllocationType())
							&& allocate.getAllocationTo() == alloc.getAllocationTo()) {
						allocate.setWaivedAmount(alloc.getWaivedAmount());
					}
				}
			}
		}

		return receiptData;
	}

	/**
	 * Method for calculating Schedule Total and Unpaid amounts based on Schedule Details
	 */
	private FinReceiptData calSummaryDetail(FinReceiptData receiptData, Date valueDate) {
		logger.debug("Entering");
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		RepayMain repayMain = receiptData.getRepayMain();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceProfitDetail finPftDeatils = finScheduleData.getFinPftDeatil();

		repayMain.setEarlyPayOnSchDate(valueDate);
		//repayMain.setDownpayment(finPftDeatils.getDownpay());
		repayMain.setDownpayment(finPftDeatils.getDownPayment());

		repayMain.setTotalCapitalize(finPftDeatils.getTotalPftCpz());

		if (SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT)) {
			repayMain.setFinAmount(finPftDeatils.getTotalpriSchd());
			repayMain.setCurFinAmount(finPftDeatils.getTotalPriBal());
		} else {
			repayMain.setFinAmount(finPftDeatils.getTotalpriSchd().subtract(repayMain.getTotalCapitalize()));
			repayMain.setCurFinAmount(finPftDeatils.getTotalPriBal().subtract(repayMain.getTotalCapitalize())
					.add(finPftDeatils.getTdPftCpz()));
		}

		repayMain.setPrincipal(finPftDeatils.getTotalpriSchd());
		repayMain.setProfit(finPftDeatils.getTotalPftSchd());
		repayMain.setTotalFeeAmt(finMain.getFeeChargeAmt());
		repayMain.setPrincipalPaid(finPftDeatils.getTotalPriPaid());
		repayMain.setProfitPaid(finPftDeatils.getTotalPftPaid());

		if (receiptPurposeCtg == 2) {
			repayMain.setPrincipalBalance(repayMain.getCurFinAmount());
			repayMain.setProfitBalance(finPftDeatils.getPftAccrued());
		} else {
			repayMain.setPrincipalBalance(finPftDeatils.getTotalPriBal());
			repayMain.setProfitBalance(finPftDeatils.getTotalPftBal());
		}

		repayMain.setOverduePrincipal(finPftDeatils.getTdSchdPriBal());
		repayMain.setOverdueProfit(finPftDeatils.getTdSchdPftBal());
		repayMain.setDateLastFullyPaid(finPftDeatils.getFullPaidDate());
		repayMain.setDateNextPaymentDue(finPftDeatils.getNSchdDate());
		repayMain.setAccrued(finPftDeatils.getPftAccrued());

		logger.debug("Leaving");
		return receiptData;
	}

	public FinReceiptData fetchEventFees(FinReceiptData receiptData, boolean isAllocated) {
		if (receiptData.isPresentment()) {
			return receiptData;
		}
		if (receiptPurposeCtg < 0) {
			return receiptData;
		}

		if (receiptPurposeCtg > 1) {
			receiptData.setEarlySettle(true);
		}
		FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<FinFeeDetail> oldFinFeeDtls = receiptData.getFinFeeDetails();
		List<FinFeeDetail> finFeedetails = null;

		receiptData = feeCalculator.calculateFees(receiptData);
		finFeedetails = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (finFeedetails == null || finFeedetails.isEmpty()) {
			return receiptData;
		}

		if (oldFinFeeDtls != null && !oldFinFeeDtls.isEmpty()) {
			for (FinFeeDetail oldFinFeeDtl : oldFinFeeDtls) {
				for (FinFeeDetail actualFeeDtl : finFeedetails) {
					if (oldFinFeeDtl.getFeeTypeID() == actualFeeDtl.getFeeTypeID()
							&& "PERCENTG".equals(actualFeeDtl.getCalculationType())) {
						actualFeeDtl.setFeeID(oldFinFeeDtl.getFeeID());
						if ("PERCENTG".equals(actualFeeDtl.getCalculationType())) {
							actualFeeDtl.setActPercentage(oldFinFeeDtl.getActPercentage());
							actualFeeDtl.setPercentage(oldFinFeeDtl.getActPercentage());

							Map<String, BigDecimal> taxPercentages = GSTCalculator
									.getTaxPercentages(financeMain.getFinReference());
							feeCalculator.calculateFeePercentageAmount(receiptData, taxPercentages);
						}
					}
				}
			}
		}

		finFeedetails = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		List<ReceiptAllocationDetail> allocationsList = receiptData.getReceiptHeader().getAllocations();

		for (int i = 0; i < finFeedetails.size(); i++) {
			FinFeeDetail finFeeDetail = finFeedetails.get(i);

			//27-08-19 PSD:140172  Issue in GST calculation for Fore closure
			BigDecimal feeAmount = finFeeDetail.getActualAmount();
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
				feeAmount = finFeeDetail.getActualAmountOriginal();
			}
			ReceiptAllocationDetail allocDetail = setAllocationRecord(receiptData, RepayConstants.ALLOCATION_FEE, 1,
					feeAmount, finFeeDetail.getFeeTypeDesc(), -(finFeeDetail.getFeeTypeID()),
					finFeeDetail.getTaxComponent(), true, true);
			allocDetail.setFeeTypeCode(finFeeDetail.getFeeTypeCode());

			BigDecimal tdsAmount = BigDecimal.ZERO;
			if (finFeeDetail.isTdsReq() && financeMain.isTDSApplicable()) {
				BigDecimal taxableAmount = BigDecimal.ZERO;
				if (StringUtils.isNotEmpty(finFeeDetail.getTaxComponent())) {
					taxableAmount = allocDetail.getTotRecv().subtract(allocDetail.getDueGST());
				} else {
					taxableAmount = allocDetail.getTotRecv();
				}

				tdsAmount = getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
						taxableAmount);
				allocDetail.setPercTds(tdsPerc);
				allocDetail.setTdsReq(true);
				allocDetail.setTdsDue(tdsAmount);
			}
			allocDetail.setTotalDue(allocDetail.getTotalDue().subtract(tdsAmount.add(allocDetail.getInProcess())));

			if (finFeeDetail.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
				allocDetail.setWaiverAccepted(PennantConstants.YES);
			}

			int index = getIndex(allocationsList, allocDetail);
			if (index > 0) {
				ReceiptAllocationDetail feeAlloc = allocationsList.get(index);
				if (feeAlloc != null) {
					if (feeAlloc.getPaidAmount().compareTo(allocDetail.getTotalDue()) > 0) {
						allocDetail.setPaidAmount(allocDetail.getTotalDue());
					} else {
						allocDetail.setPaidAmount(feeAlloc.getPaidAmount());
					}
					allocDetail.setTotalPaid(allocDetail.getPaidAmount());
				}
				allocationsList.remove(index);
			}
			allocationsList.add(allocDetail);
		}
		receiptData.getReceiptHeader().setAllocations(allocationsList);
		return receiptData;
	}

	private int getIndex(List<ReceiptAllocationDetail> allocationsList, ReceiptAllocationDetail allocDetail) {
		int index = -1;
		for (int i = 0; i < allocationsList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationsList.get(i);
			if (allocate.getAllocationTo() == allocDetail.getAllocationTo()
					&& allocate.getAllocationType().equals(allocDetail.getAllocationType())) {
				index = i;
				break;
			}
		}
		return index;
	}

	private List<ReceiptAllocationDetail> resetAllocationList(FinReceiptData receiptData) {
		List<ReceiptAllocationDetail> allocationsList = new ArrayList<ReceiptAllocationDetail>(1);
		FinanceProfitDetail finPftDeatils = receiptData.getFinanceDetail().getFinScheduleData().getFinPftDeatil();
		RepayMain repayMain = receiptData.getRepayMain();

		setReceiptCategory(receiptData.getReceiptHeader().getReceiptPurpose());

		BigDecimal tdsDue = BigDecimal.ZERO;
		BigDecimal nPftDue = BigDecimal.ZERO;
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal pftDue = BigDecimal.ZERO;
		BigDecimal emiDue = BigDecimal.ZERO;
		BigDecimal futPri = BigDecimal.ZERO;
		BigDecimal futPft = BigDecimal.ZERO;
		BigDecimal futNPft = BigDecimal.ZERO;
		BigDecimal futTds = BigDecimal.ZERO;

		if (receiptPurposeCtg == 2) {
			tdsDue = receiptData.getOrgFinPftDtls().getTdTdsAmount()
					.subtract(receiptData.getOrgFinPftDtls().getTdTdsPaid());
			priDue = receiptData.getOrgFinPftDtls().getTdSchdPriBal();
			pftDue = receiptData.getOrgFinPftDtls().getTdSchdPft()
					.subtract(receiptData.getOrgFinPftDtls().getTdSchdPftPaid());
			futPri = repayMain.getCurFinAmount().subtract(priDue);
			futPft = finPftDeatils.getTdSchdPftBal().subtract(pftDue);
			futTds = finPftDeatils.getTdTdsBal().subtract(tdsDue);
			futNPft = futPft.subtract(futTds);
		} else {
			tdsDue = finPftDeatils.getTdTdsAmount().subtract(finPftDeatils.getTdTdsPaid());
			priDue = finPftDeatils.getTdSchdPriBal();
			pftDue = finPftDeatils.getTdSchdPft().subtract(finPftDeatils.getTdSchdPftPaid());
		}

		nPftDue = pftDue.subtract(tdsDue);
		emiDue = emiDue.add(nPftDue).add(priDue);
		String desc = null;
		int id = -1;

		if (pftDue.compareTo(BigDecimal.ZERO) > 0) {
			id = id + 1;
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PFT");
			ReceiptAllocationDetail pft = setAllocRecord(receiptData, RepayConstants.ALLOCATION_PFT, id, pftDue, desc,
					0, "", false, false);
			pft.setTdsDue(tdsDue);
			allocationsList.add(pft);
			pft.setTotalDue(pft.getTotalDue().subtract(tdsDue.add(pft.getInProcess())));

		}

		if (priDue.compareTo(BigDecimal.ZERO) > 0) {
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PRI");
			id = id + 1;
			ReceiptAllocationDetail pri = setAllocRecord(receiptData, RepayConstants.ALLOCATION_PRI, id, priDue, desc,
					0, "", false, false);
			receiptData.setTdPriBal(priDue);
			pri.setTotalDue(pri.getTotalDue().subtract(pri.getInProcess()));
			allocationsList.add(pri);
		}

		if (emiDue.compareTo(BigDecimal.ZERO) > 0) {
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_EMI");
			id = id + 1;
			ReceiptAllocationDetail emi = setAllocRecord(receiptData, RepayConstants.ALLOCATION_EMI, id, emiDue, desc,
					0, "", false, false);
			emi.setTotalDue(emi.getTotalDue().subtract(emi.getInProcess()));
			allocationsList.add(emi);
		}

		if (receiptPurposeCtg < 2) {
			return allocationsList;
		}

		if (futPri.compareTo(BigDecimal.ZERO) > 0) {
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_FUTPRI");
			id = id + 1;
			ReceiptAllocationDetail futPriAlloc = setAllocRecord(receiptData, RepayConstants.ALLOCATION_FUT_PRI, id,
					futPri, desc, 0, "", true, true);
			allocationsList.add(futPriAlloc);
		}
		if (futPft.compareTo(BigDecimal.ZERO) > 0) {
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_FUTPFT");
			id = id + 1;
			ReceiptAllocationDetail futProfit = setAllocRecord(receiptData, RepayConstants.ALLOCATION_FUT_PFT, id,
					futPft, desc, 0, "", false, false);
			futProfit.setTdsDue(futTds);
			futProfit.setTotalDue(futProfit.getTotalDue().subtract(futTds));
			allocationsList.add(futProfit);
		}

		return allocationsList;
	}

	public BigDecimal getTotalNetPastDue(FinReceiptData receiptData) {
		BigDecimal totNetPastDue = BigDecimal.ZERO;
		List<ReceiptAllocationDetail> allocationsList = receiptData.getReceiptHeader().getAllocations();
		FinanceProfitDetail finPftDeatils = receiptData.getFinanceDetail().getFinScheduleData().getFinPftDeatil();

		setReceiptCategory(receiptData.getReceiptHeader().getReceiptPurpose());
		BigDecimal tdsDue = finPftDeatils.getTdTdsBal();
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal pftDue = finPftDeatils.getTdSchdPftBal();
		BigDecimal nPftDue = BigDecimal.ZERO;

		for (int i = 0; i < allocationsList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationsList.get(i);
			if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
				priDue = (allocate.getPaidAmount());
			} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
				nPftDue = (allocate.getPaidAmount());
			} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_EMI)) {
				// do nothing
			} else {
				totNetPastDue = totNetPastDue.add(allocate.getPaidAmount());
			}
		}

		totNetPastDue = totNetPastDue.add(priDue).add(nPftDue);
		return totNetPastDue;

	}

	public List<Date> getPresentmentDates(FinReceiptData receiptData, Date valueDate) {
		List<Date> presentmentDates = new ArrayList<>();
		BigDecimal inPresPri = BigDecimal.ZERO;
		BigDecimal inPresPft = BigDecimal.ZERO;
		BigDecimal inPresTds = BigDecimal.ZERO;
		if (receiptPurposeCtg != 0 || receiptData.isPresentment()) {
			return presentmentDates;
		}

		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = null;

		for (int i = 0; i < schdDetails.size() - 1; i++) {
			curSchd = schdDetails.get(i);

			if (curSchd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (curSchd.getPresentmentId() > 0) {
				presentmentDates.add(curSchd.getSchDate());
				if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()))
								.compareTo(BigDecimal.ZERO) > 0) {
					inPresPri = inPresPri.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					inPresPft = inPresPft.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
					inPresTds = inPresTds.add(curSchd.getTDSAmount().subtract(curSchd.getTDSPaid()));
				}
			}
		}
		receiptData.setInPresPri(inPresPri);
		receiptData.setInPresPft(inPresPft);
		receiptData.setInPresTds(inPresTds);
		receiptData.setInPresNpft(inPresPft.subtract(inPresTds));
		return presentmentDates;
	}

	public ReceiptAllocationDetail setAllocationRecord(FinReceiptData receiptData, String allocType, int id,
			BigDecimal due, String desc, long allocTo, String taxType, boolean isEditable, boolean isDueAdded) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		TaxAmountSplit taxSplit = new TaxAmountSplit();

		BigDecimal inProcAmount = findInProcAllocAmount(receiptData.getInProcRadList(), allocType, allocTo);
		BigDecimal curDue = due;
		taxSplit.setAmount(curDue);
		taxSplit.setTaxType(taxType);

		taxSplit = getGST(financeDetail, taxSplit);

		ReceiptAllocationDetail allocation = new ReceiptAllocationDetail();
		allocation.setAllocationID(id);
		allocation.setAllocationType(allocType);
		allocation.setDueAmount(curDue);
		allocation.setInProcess(inProcAmount);
		allocation.setAllocationTo(allocTo);
		allocation.setDueGST(taxSplit.gettGST());
		allocation.setTotalDue(due);
		allocation.setEditable(isEditable);
		allocation.setTotRecv(due);

		if (StringUtils.isNotEmpty(taxType)) {
			allocation.setPercCGST(cgstPerc);
			allocation.setPercIGST(igstPerc);
			allocation.setPercSGST(sgstPerc);
			allocation.setPercUGST(ugstPerc);
			allocation.setPercCESS(cessPerc);

			allocation.setDueCGST(taxSplit.getcGST());
			allocation.setDueIGST(taxSplit.getiGST());
			allocation.setDueUGST(taxSplit.getuGST());
			allocation.setDueSGST(taxSplit.getsGST());
			allocation.setDueCESS(taxSplit.getCess());
		}

		if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			allocation.setTypeDesc(desc + DESC_EXC_TAX);
			allocation.setTotalDue(due.add(taxSplit.gettGST()));
			allocation.setTotRecv(due.add(taxSplit.gettGST()));
		} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
			allocation.setTypeDesc(desc + DESC_INC_TAX);
		} else {
			allocation.setTypeDesc(desc);
		}

		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType(taxType);

		//GST Calculations
		if (StringUtils.isNotBlank(taxType)) {
			TaxHeader taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_CGST, cgstPerc, taxSplit.getcGST()));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_SGST, sgstPerc, taxSplit.getsGST()));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_IGST, igstPerc, taxSplit.getiGST()));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_UGST, ugstPerc, taxSplit.getuGST()));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_CESS, cessPerc, taxSplit.getCess()));
			allocation.setTaxHeader(taxHeader);
		} else {
			allocation.setTaxHeader(null);
		}

		return allocation;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, BigDecimal taxAmount) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		taxes.setNetTax(taxAmount);
		taxes.setActualTax(taxAmount);
		return taxes;
	}

	public ReceiptAllocationDetail setODCAllocRecord(FinReceiptData receiptData, String allocType, int id,
			BigDecimal due, BigDecimal gst, String desc, long allocTo, String taxType, boolean isEditable) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		TaxAmountSplit taxSplit = new TaxAmountSplit();

		BigDecimal inProcAmount = findInProcAllocAmount(receiptData.getInProcRadList(), allocType, allocTo);
		BigDecimal curDue = due;
		taxSplit.setAmount(curDue);
		taxSplit.setTaxType(taxType);

		taxSplit = getGST(financeDetail, taxSplit);

		ReceiptAllocationDetail allocation = new ReceiptAllocationDetail();
		allocation.setAllocationID(id);
		allocation.setAllocationType(allocType);
		allocation.setDueAmount(curDue);
		allocation.setInProcess(inProcAmount);
		allocation.setAllocationTo(allocTo);
		allocation.setDueGST(taxSplit.gettGST());
		allocation.setTotalDue(curDue);
		allocation.setEditable(isEditable);
		allocation.setTotRecv(due);

		if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			allocation.setTypeDesc(desc + DESC_EXC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
			allocation.setTypeDesc(desc + DESC_INC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else {
			allocation.setTypeDesc(desc);
		}

		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType(taxType);
		//CESS Calculations
		if (StringUtils.isNotBlank(taxType)) {
			TaxHeader taxHeader = new TaxHeader();
			Taxes tax = new Taxes();
			tax.setTaxType(RuleConstants.CODE_CESS);
			tax.setTaxPerc(cessPerc);
			tax.setNetTax(taxSplit.getCess());
			tax.setActualTax(taxSplit.getCess());
			taxHeader.getTaxDetails().add(tax);
			allocation.setTaxHeader(taxHeader);
		}

		return allocation;
	}

	public ReceiptAllocationDetail setAllocRecord(FinReceiptData receiptData, String allocType, int id, BigDecimal due,
			String desc, long allocTo, String taxType, boolean isEditable, boolean isDueAdded) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		TaxAmountSplit taxSplit = new TaxAmountSplit();

		BigDecimal inProcAmount = findInProcAllocAmount(receiptData.getInProcRadList(), allocType, allocTo);
		BigDecimal curDue = due;
		taxSplit.setAmount(curDue);
		taxSplit.setTaxType(taxType);

		taxSplit = getGST(financeDetail, taxSplit);

		ReceiptAllocationDetail allocation = new ReceiptAllocationDetail();
		allocation.setAllocationID(id);
		allocation.setAllocationType(allocType);
		allocation.setDueAmount(curDue);
		allocation.setInProcess(inProcAmount);
		allocation.setAllocationTo(allocTo);
		allocation.setDueGST(taxSplit.gettGST());
		allocation.setTotalDue(curDue);
		allocation.setEditable(isEditable);
		allocation.setTotRecv(due);
		allocation.setDueCGST(taxSplit.getcGST());
		allocation.setDueIGST(taxSplit.getiGST());
		allocation.setDueUGST(taxSplit.getuGST());
		allocation.setDueSGST(taxSplit.getsGST());
		allocation.setDueCESS(taxSplit.getCess());

		if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			allocation.setTypeDesc(desc + DESC_EXC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
			allocation.setTypeDesc(desc + DESC_INC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else {
			allocation.setTypeDesc(desc);
		}

		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType(taxType);
		//CESS Calculations
		if (StringUtils.isNotBlank(taxType)) {
			TaxHeader taxHeader = new TaxHeader();
			Taxes tax = new Taxes();
			tax.setTaxType(RuleConstants.CODE_CESS);
			tax.setTaxPerc(cessPerc);
			tax.setNetTax(taxSplit.getCess());
			tax.setActualTax(taxSplit.getCess());
			taxHeader.getTaxDetails().add(tax);
			allocation.setTaxHeader(taxHeader);
		}

		return allocation;
	}

	private ReceiptAllocationDetail changeAllocRecord(FinanceDetail financeDetail, ReceiptAllocationDetail allocation,
			boolean isGoldLoan) {
		// Due tax Recalculaiton
		TaxAmountSplit taxSplit = new TaxAmountSplit();
		taxSplit.setAmount(allocation.getDueAmount());
		taxSplit.setTaxType(allocation.getTaxType());

		if (isGoldLoan) {
			if (StringUtils.equals(allocation.getWaiverAccepted(), PennantConstants.YES)) {
				taxSplit.setWaivedAmount(allocation.getWaivedAmount());
			}
		} else {
			taxSplit.setWaivedAmount(allocation.getWaivedAmount());
		}

		taxSplit = getGST(financeDetail, taxSplit);
		allocation.setDueGST(taxSplit.gettGST());
		allocation.setTotalDue(taxSplit.getNetAmount());

		// Paid Amount GST Recalculation (always paid amount we are taking the
		// inclusive type here because we are doing reverse calculation here)
		calAllocationGST(financeDetail, allocation.getTotalPaid(), allocation,
				FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

		if (StringUtils.equals(allocation.getTaxType(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			allocation.setPaidAmount(allocation.getTotalPaid().subtract(allocation.getPaidGST()));
			BigDecimal paidAndWaived = allocation.getWaivedAmount().add(allocation.getWaivedGST())
					.add(allocation.getTotalPaid());
			allocation.setBalance(allocation.getTotalDue().subtract(paidAndWaived));
		} else {
			allocation.setPaidAmount(allocation.getTotalPaid());
			allocation.setBalance(allocation.getTotalDue().subtract(allocation.getWaivedAmount())
					.subtract(allocation.getTotalPaid()));
		}

		return allocation;
	}

	public FinReceiptData fetchODPenalties(FinReceiptData receiptData, Date valueDate, List<Date> presentmentDates) {
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();

		if (receiptData.isPresentment()) {
			return receiptData;
		}

		boolean isGoldLoan = false;

		if (StringUtils.equals(finScheduleData.getFinanceMain().getProductCategory(), FinanceConstants.PRODUCT_GOLD)) {
			isGoldLoan = true;
		}

		// For Gold Loan Calculate only after MDT. No calculation Required
		if (isGoldLoan && finScheduleData.getFinanceMain().getMaturityDate().compareTo(valueDate) > 0) {
			return receiptData;
		}

		List<ReceiptAllocationDetail> allocations = receiptData.getReceiptHeader().getAllocations();

		// Fetching Actual Late Payments based on Value date passing
		BigDecimal lpiBal = BigDecimal.ZERO;
		BigDecimal lppBal = BigDecimal.ZERO;

		Date reqMaxODDate = valueDate;
		if (!ImplementationConstants.LPP_CALC_SOD) {
			if (!isGoldLoan) {
				reqMaxODDate = DateUtility.addDays(valueDate, -1);
			}
		}

		// Calculate overdue Penalties
		List<FinODDetails> overdueList = getValueDatePenalties(finScheduleData, receiptData.getTotReceiptAmount(),
				reqMaxODDate, null, true);

		// No Overdue penalty exit
		if (CollectionUtils.isEmpty(overdueList)) {
			return receiptData;
		}

		if (receiptPurposeCtg == 2 && !isGoldLoan && !CollectionUtils.isEmpty(receiptData.getInProcRepayments())) {
			for (FinODDetails od : overdueList) {
				for (FinanceRepayments repay : receiptData.getInProcRepayments()) {
					if (od.getFinODSchdDate().compareTo(repay.getFinSchdDate()) == 0) {
						od.setTotPenaltyPaid(od.getTotPenaltyPaid().subtract(repay.getPenaltyPaid()));
						od.setTotPenaltyBal(od.getTotPenaltyBal().add(repay.getPenaltyPaid()));
						break;
					}
				}
			}
		}

		// Penalty Tax Details
		FeeType lppFeeType = feeTypeDAO.getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
		String taxType = null;

		if (lppFeeType != null && lppFeeType.isTaxApplicable()) {
			taxType = lppFeeType.getTaxComponent();
		}
		BigDecimal tdsAmount = BigDecimal.ZERO;
		for (int i = 0; i < overdueList.size(); i++) {
			FinODDetails finODDetail = overdueList.get(i);
			if (finODDetail.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
				break;
			}

			lpiBal = lpiBal.add(finODDetail.getLPIBal());
			if (finODDetail.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
				lppBal = lppBal.add(finODDetail.getTotPenaltyBal());
				BigDecimal taxableAmount = finODDetail.getTotPenaltyBal();

				if (StringUtils.isNotEmpty(taxType)) {
					TaxAmountSplit taxSplit = new TaxAmountSplit();
					taxSplit.setAmount(taxableAmount);
					taxSplit.setTaxType(taxType);
					taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
					if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
						taxableAmount = taxableAmount.subtract(taxSplit.gettGST());
					}

				}

				if (lppFeeType.isTdsReq() && finScheduleData.getFinanceMain().isTDSApplicable()) {
					tdsAmount = tdsAmount.add(getTDSAmount(
							receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), taxableAmount));
				}

			}

		}

		String desc = Labels.getLabel("label_RecceiptDialog_AllocationType_LPFT");
		// Fetch Late Pay Profit Details
		if (lpiBal.compareTo(BigDecimal.ZERO) > 0) {
			allocations.add(
					setAllocRecord(receiptData, RepayConstants.ALLOCATION_LPFT, 5, lpiBal, desc, 0, "", true, true));
		}

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_ODC");
		// Fetch Sum of Overdue Charges
		if (lppBal.compareTo(BigDecimal.ZERO) > 0) {
			receiptData.setPendingODC(lppBal);
			ReceiptAllocationDetail lpp = setAllocationRecord(receiptData, RepayConstants.ALLOCATION_ODC, 6, lppBal,
					desc, 0, taxType, true, true);
			if (lppFeeType.isTdsReq() && finScheduleData.getFinanceMain().isTDSApplicable()) {
				lpp.setPercTds(tdsPerc);
				lpp.setTdsReq(true);
				lpp.setTdsDue(tdsAmount);
			}
			lpp.setTotalDue(lpp.getTotalDue().subtract(tdsAmount.add(lpp.getInProcess())));
			allocations.add(lpp);
		}

		finScheduleData.setFinODDetails(overdueList);
		receiptData.getReceiptHeader().setAllocations(allocations);
		return receiptData;
	}

	public FinReceiptData fetchManualAdviseDetails(FinReceiptData receiptData, Date valueDate) {
		// Manual Advises
		if (receiptData.isPresentment()) {
			return receiptData;
		}
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
		List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdviseByRef(
				receiptData.getReceiptHeader().getReference(), FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");

		if (CollectionUtils.isEmpty(adviseList)) {
			return receiptData;
		}

		List<ReceiptAllocationDetail> allocationsList = receiptData.getReceiptHeader().getAllocations();

		// Bounce Tax Details
		FeeType bounceFeeType = null;
		BigDecimal adviseDue = BigDecimal.ZERO;

		for (ManualAdvise advise : adviseList) {
			boolean isTdsApplicable = false;
			adviseDue = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
			String type = null;
			String desc = null;
			String taxType = null;
			long advID = 0;

			// Adding Advise Details to Map
			if (advise.getBounceID() > 0) {
				if (bounceFeeType == null) {
					bounceFeeType = feeTypeDAO.getTaxDetailByCode(RepayConstants.ALLOCATION_BOUNCE);
				}

				if (bounceFeeType != null && bounceFeeType.isTaxApplicable()) {
					taxType = bounceFeeType.getTaxComponent();
				}
				isTdsApplicable = bounceFeeType.isTdsReq();
				type = RepayConstants.ALLOCATION_BOUNCE;
				desc = "Bounce Charges";
				advID = advise.getAdviseID();
			} else {
				type = RepayConstants.ALLOCATION_MANADV;
				desc = advise.getFeeTypeDesc();
				isTdsApplicable = advise.isTdsReq();
				// Calculation Receivable Advises
				if (advise.isTaxApplicable()) {
					taxType = advise.getTaxComponent();
				}

				advID = advise.getAdviseID();
			}

			int id = allocationsList.size();
			ReceiptAllocationDetail allocDetail = setAllocationRecord(receiptData, type, id, adviseDue, desc, advID,
					taxType, true, true);
			allocDetail.setFeeTypeCode(advise.getFeeTypeCode());
			BigDecimal tdsAmount = BigDecimal.ZERO;
			if (isTdsApplicable && fsd.getFinanceMain().isTDSApplicable()) {
				BigDecimal taxableAmount = BigDecimal.ZERO;
				if (StringUtils.isNotEmpty(taxType)) {
					taxableAmount = allocDetail.getTotRecv().subtract(allocDetail.getDueGST());
				} else {
					taxableAmount = allocDetail.getTotRecv();
				}

				tdsAmount = getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
						taxableAmount);
				allocDetail.setPercTds(tdsPerc);
				allocDetail.setTdsReq(true);
				allocDetail.setTdsDue(tdsAmount);
			}
			allocDetail.setTotalDue(allocDetail.getTotalDue().subtract(tdsAmount.add(allocDetail.getInProcess())));
			allocationsList.add(allocDetail);
		}

		if (receiptPurposeCtg == 2 && inProcessReceipts.size() > 0) {
			List<ManualAdviseMovements> movements = manualAdviseDAO.getInProcManualAdvMovmnts(inProcessReceipts);
			if (movements != null && movements.size() > 0) {
				for (ManualAdvise advise : adviseList) {
					for (ManualAdviseMovements mam : movements) {
						if (mam.getAdviseID() == advise.getAdviseID()) {
							advise.setPaidAmount(advise.getPaidAmount().subtract(mam.getPaidAmount()));
							advise.setWaivedAmount(advise.getWaivedAmount().subtract(mam.getWaivedAmount()));
						}
					}
				}
			}
		}

		receiptData.getReceiptHeader().setAllocations(allocationsList);
		receiptData.getReceiptHeader().setReceivableAdvises(adviseList);
		return receiptData;
	}

	public FinReceiptData setXcessPayables(FinReceiptData receiptData) {
		receiptData = getXcessList(receiptData);
		receiptData = getPayableList(receiptData);
		return receiptData;
	}

	/*
	 * public FinReceiptData getXcessList(FinReceiptData receiptData) { List<XcessPayables> xcessPayableList = new
	 * ArrayList<XcessPayables>(1); XcessPayables xcessPayable = new XcessPayables(); String excessLabel =
	 * "label_RecceiptDialog_ExcessType_";
	 * 
	 * // Add Dummy EMI in Advance Record xcessPayable.setIdx(0);
	 * xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
	 * xcessPayable.setPayableDesc(Labels.getLabel(excessLabel + RepayConstants.EXAMOUNTTYPE_EMIINADV));
	 * xcessPayableList.add(xcessPayable);
	 * 
	 * xcessPayable = new XcessPayables(); // Add Dummy Excess Record xcessPayable.setIdx(1);
	 * xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EXCESS);
	 * xcessPayable.setPayableDesc(Labels.getLabel(excessLabel + RepayConstants.EXAMOUNTTYPE_EXCESS));
	 * xcessPayableList.add(xcessPayable);
	 * 
	 * // Load FinExcess Details List<FinExcessAmount> excessList = receiptData.getReceiptHeader().getExcessAmounts();
	 * 
	 * if (excessList == null || excessList.isEmpty()) {
	 * receiptData.getReceiptHeader().setXcessPayables(xcessPayableList); return receiptData; }
	 * 
	 * List<FinExcessAmountReserve> excessReserveList = receiptData.getReceiptHeader().getExcessReserves();
	 * 
	 * for (int i = 0; i < excessList.size(); i++) { FinExcessAmount excess = excessList.get(i); boolean isAdvance =
	 * false;
	 * 
	 * if (StringUtils.equals(excess.getAmountType(), RepayConstants.EXAMOUNTTYPE_EMIINADV)) { isAdvance = true; }
	 * 
	 * if (isAdvance) { xcessPayable = xcessPayableList.get(0); } else { xcessPayable = xcessPayableList.get(1); }
	 * 
	 * xcessPayable.setPayableID(excess.getExcessID());
	 * xcessPayable.setAmount(excess.getAmount().subtract(excess.getUtilisedAmt().add(excess.getReservedAmt())));
	 * 
	 * if (excessReserveList != null && !excessReserveList.isEmpty()) { for (int j = 0; j < excessReserveList.size();
	 * j++) { FinExcessAmountReserve reserve = excessReserveList.get(j); if (reserve.getExcessID() ==
	 * xcessPayable.getPayableID()) { xcessPayable.setReserved(reserve.getReservedAmt()); break; } } }
	 * 
	 * xcessPayable.setAvailableAmt(xcessPayable.getAmount()); xcessPayable.setTotPaidNow(BigDecimal.ZERO);
	 * xcessPayable.setReserved(BigDecimal.ZERO);
	 * xcessPayable.setBalanceAmt(xcessPayable.getAvailableAmt().subtract(xcessPayable.getTotPaidNow()));
	 * 
	 * if (isAdvance) { xcessPayableList.set(0, xcessPayable); } else { xcessPayableList.set(1, xcessPayable); } }
	 * 
	 * receiptData.getReceiptHeader().setXcessPayables(xcessPayableList); return receiptData; }
	 */

	public FinReceiptData getXcessList(FinReceiptData receiptData) {
		List<XcessPayables> xcessPayableList = new ArrayList<XcessPayables>(1);

		// Load FinExcess Details
		List<FinExcessAmount> excessList = receiptData.getReceiptHeader().getExcessAmounts();

		if (excessList == null || excessList.isEmpty()) {
			receiptData.getReceiptHeader().setXcessPayables(xcessPayableList);
			return receiptData;
		}

		List<FinExcessAmountReserve> excessReserveList = receiptData.getReceiptHeader().getExcessReserves();

		for (int i = 0; i < excessList.size(); i++) {
			FinExcessAmount excess = excessList.get(i);
			XcessPayables xcessPayable = new XcessPayables();
			String excessLabel = "label_RecceiptDialog_ExcessType_";

			// Add Dummy EMI in Advance Record
			xcessPayable.setPayableType(excess.getAmountType());
			xcessPayable.setPayableDesc(Labels.getLabel(excessLabel + excess.getAmountType()));

			xcessPayable.setPayableID(excess.getExcessID());
			xcessPayable.setAmount(xcessPayable.getAmount()
					.add(excess.getAmount().subtract(excess.getUtilisedAmt().add(excess.getReservedAmt()))));

			if (excessReserveList != null && !excessReserveList.isEmpty()) {
				for (int j = 0; j < excessReserveList.size(); j++) {
					FinExcessAmountReserve reserve = excessReserveList.get(j);
					if (reserve.getExcessID() == xcessPayable.getPayableID()) {
						xcessPayable.setReserved(reserve.getReservedAmt());
						break;
					}
				}
			}

			xcessPayable.setAvailableAmt(xcessPayable.getAmount());
			xcessPayable.setTotPaidNow(BigDecimal.ZERO);
			xcessPayable.setReserved(BigDecimal.ZERO);
			xcessPayable.setBalanceAmt(xcessPayable.getAvailableAmt().subtract(xcessPayable.getTotPaidNow()));
			xcessPayableList.add(xcessPayable);
		}

		receiptData.getReceiptHeader().setXcessPayables(xcessPayableList);
		return receiptData;
	}

	public FinReceiptData getPayableList(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();

		// Load FinExcess Details
		List<ManualAdvise> payableList = receiptData.getReceiptHeader().getPayableAdvises();

		if (payableList == null || payableList.isEmpty()) {
			return receiptData;
		}

		List<ManualAdviseReserve> payableReserveList = receiptData.getReceiptHeader().getPayableReserves();

		int prvSize = xcessPayableList.size();

		for (ManualAdvise payable : payableList) {
			XcessPayables xcessPayable = new XcessPayables();
			String feeDesc = payable.getFeeTypeDesc();

			xcessPayable.setIdx(prvSize++);
			xcessPayable.setPayableID(payable.getAdviseID());
			xcessPayable.setPayableType("P");
			xcessPayable.setAmount(payable.getBalanceAmt());
			xcessPayable.setFeeTypeCode(payable.getFeeTypeCode());
			xcessPayable.setTdsApplicable(payable.isTdsReq());

			if (payableReserveList != null && !payableReserveList.isEmpty()) {
				for (ManualAdviseReserve reserve : payableReserveList) {
					if (reserve.getAdviseID() == xcessPayable.getPayableID()) {
						xcessPayable.setReserved(reserve.getReservedAmt());
						break;
					}
				}
			}

			if (payable.isTaxApplicable()) {
				xcessPayable.setTaxType(payable.getTaxComponent());
			} else {
				xcessPayable.setTaxType(null);
			}

			xcessPayable.setAmount(xcessPayable.getAmount().add(xcessPayable.getReserved()));
			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(xcessPayable.getAmount());
			taxSplit.setTaxType(xcessPayable.getTaxType());
			taxSplit = getGST(financeDetail, taxSplit);

			xcessPayable.setAvailableAmt(taxSplit.getNetAmount());
			xcessPayable.setTaxApplicable(payable.isTaxApplicable());
			if (payable.isTaxApplicable()) {
				if (StringUtils.equals(xcessPayable.getTaxType(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
					feeDesc = feeDesc + DESC_EXC_TAX;
				} else {
					feeDesc = feeDesc + DESC_INC_TAX;
				}
			}

			xcessPayable.setPayableDesc(feeDesc);
			xcessPayable.setGstAmount(taxSplit.gettGST());
			xcessPayable.setTotPaidNow(xcessPayable.getReserved());
			if (xcessPayable.isTdsApplicable()) {
				xcessPayable.setTdsAmount(
						getTDSAmount(financeDetail.getFinScheduleData().getFinanceMain(), taxSplit.getNetAmount()));
			}
			xcessPayable.setReserved(BigDecimal.ZERO);
			xcessPayable.setBalanceAmt(xcessPayable.getAvailableAmt().subtract(xcessPayable.getTotPaidNow()));

			xcessPayableList.add(xcessPayable);
		}

		receiptData.getReceiptHeader().setXcessPayables(xcessPayableList);
		return receiptData;
	}

	public FinReceiptData doExcessAdjustments(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		ReceiptAllocationDetail totalPastDues = rch.getTotalPastDues();
		ReceiptAllocationDetail totalAdvises = rch.getTotalRcvAdvises();
		ReceiptAllocationDetail totalXcess = rch.getTotalXcess();
		ReceiptAllocationDetail totalFees = rch.getTotalFees();
		BigDecimal excessPaid = BigDecimal.ZERO;
		// NO EXCESS OR PAYABLE EXIT
		if (totalXcess.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}

		BigDecimal balance = totalPastDues.getTotalDue().add(totalAdvises.getTotalDue()).add(totalFees.getTotalDue());

		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);

			if (balance.compareTo(payable.getAvailableAmt()) > 0) {
				payable.setTotPaidNow(payable.getAvailableAmt());
			} else {
				payable.setTotPaidNow(balance);
			}

			payable.setBalanceAmt(payable.getAvailableAmt().subtract(payable.getTotPaidNow()));
			balance = balance.subtract(payable.getTotPaidNow());
			excessPaid = excessPaid.add(payable.getTotPaidNow());
			if (balance.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		rch.getTotalXcess().setTotalPaid(excessPaid);
		return receiptData;
	}

	public FinReceiptData changeXcessPaid(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		setReceiptCategory(rch.getReceiptPurpose());
		receiptData = recalXcessPayableGST(receiptData);
		receiptData = removeFeesFromAllocation(receiptData);
		receiptData = setTotals(receiptData, 0);
		receiptData = fetchEventFees(receiptData, false);

		if (StringUtils.equals(rch.getAllocationType(), RepayConstants.ALLOCATIONTYPE_AUTO)) {
			receiptData = recalAutoAllocation(receiptData, rch.getValueDate(), false);
		}

		receiptData = setTotals(receiptData, 0);
		return receiptData;
	}

	public FinReceiptData recalXcessPayableGST(FinReceiptData receiptData) {
		// Set Balance Changes
		List<XcessPayables> xcessPayables = receiptData.getReceiptHeader().getXcessPayables();
		for (XcessPayables payable : xcessPayables) {
			if (payable.getTotPaidNow().compareTo(BigDecimal.ZERO) != 0) {
				TaxAmountSplit taxSplit = new TaxAmountSplit();
				taxSplit.setAmount(payable.getTotPaidNow());
				if (payable.isTaxApplicable()) {
					taxSplit.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
				}
				taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
				payable.setPaidNow(taxSplit.getAmount().subtract(taxSplit.gettGST()));
				payable.setPaidGST(taxSplit.gettGST());
				payable.setPaidCGST(taxSplit.getcGST());
				payable.setPaidSGST(taxSplit.getsGST());
				payable.setPaidUGST(taxSplit.getuGST());
				payable.setPaidIGST(taxSplit.getiGST());
				payable.setPaidCESS(taxSplit.getCess());
			}

			payable.setBalanceAmt(payable.getAvailableAmt().subtract(payable.getTotPaidNow()));
		}

		return receiptData;
	}

	public FinReceiptData removeFeesFromAllocation(FinReceiptData receiptData) {
		// Remove Fees from Allocations
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationList.get(i);

			if (allocate.getAllocationTo() >= 0) {
				continue;
			}

			allocationList.remove(i);
			i = i - 1;
		}

		return receiptData;
	}

	public FinReceiptData changeAllocations(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		setReceiptCategory(rch.getReceiptPurpose());
		Cloner cloner = new Cloner();
		allocated = cloner.deepClone(rch.getAllocations());
		if (StringUtils.equals(rch.getAllocationType(), RepayConstants.ALLOCATIONTYPE_AUTO)) {
			receiptData = setXcessPayables(receiptData);
			receiptData.setSetPaidValues(false);
			receiptData = recalAutoAllocation(receiptData, rch.getValueDate(), false);
			receiptData = setPaidValues(receiptData);
		}

		receiptData = setTotals(receiptData, 0);
		return receiptData;
	}

	public FinReceiptData recalAllocationGST(FinReceiptData receiptData) {

		boolean isGoldLoan = false;
		String productCategory = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain()
				.getProductCategory();
		if (StringUtils.equals(productCategory, FinanceConstants.PRODUCT_GOLD)) {
			isGoldLoan = true;
		}

		// Set Balance Changes.
		List<ReceiptAllocationDetail> allocations = receiptData.getReceiptHeader().getAllocations();
		for (ReceiptAllocationDetail allocation : allocations) {
			allocation.setWaivedAvailable(allocation.getWaivedAmount());
			// PRI and Interest Records should have been set before reached here
			if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_PFT)
					|| StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_TDS)
					|| StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_NPFT)
					|| StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
				continue;
			}

			allocation = changeAllocRecord(receiptData.getFinanceDetail(), allocation, isGoldLoan);

		}
		return receiptData;
	}

	public BigDecimal getPartPaymentAmount(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		int receiptCtg = setReceiptCategory(rch.getReceiptPurpose());
		BigDecimal partPayAmount = rch.getReceiptAmount();
		if (receiptCtg == 2) {
			if (!receiptData.isForeClosure()) {
				partPayAmount = partPayAmount.add(receiptData.getExcessAvailable());
			}
			partPayAmount = partPayAmount.subtract(rch.getTotalPastDues().getPaidAmount())
					.subtract(rch.getTotalBounces().getPaidAmount()).subtract(rch.getTotalRcvAdvises().getPaidAmount());
		}
		if (receiptCtg < 2) {

			partPayAmount = partPayAmount.subtract(rch.getTotalPastDues().getPaidAmount())
					.subtract(rch.getTotalBounces().getPaidAmount()).subtract(rch.getTotalRcvAdvises().getPaidAmount());
		}

		if (partPayAmount.compareTo(BigDecimal.ZERO) <= 0) {
			partPayAmount = BigDecimal.ZERO;
		}
		return partPayAmount;
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * 
	 */
	private FinReceiptData recalReceipt(FinReceiptData receiptData, boolean isPresentment) {
		logger.debug("Entering");
		receiptData.setAdjSchedule(true);
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		// Rendering
		if (rcdList == null || rcdList.isEmpty()) {
			return receiptData;
		}

		// Treat Paid Amounts as dues
		List<ReceiptAllocationDetail> radList = receiptData.getReceiptHeader().getAllocations();
		BigDecimal totWaived = BigDecimal.ZERO;
		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail rad = radList.get(i);
			if (StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_PP)) {
				rch.setPpIdx(i);
			}
			if (rad.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
				radList.remove(i);
				i = i - 1;
				continue;
			}

			rad.setTotalPaid(BigDecimal.ZERO);

			if (!StringUtils.equals(rad.getWaiverAccepted(), "N")) {
				totWaived = totWaived.add(rad.getWaivedAvailable());
			}
		}

		// write code to reduce Part Payments and Early Settlement balances from
		// the allocation list

		// Prepare Repay Headers
		for (int i = 0; i < rcdList.size(); i++) {
			FinReceiptDetail rcd = rcdList.get(i);

			if (rcd.isDelRecord()) {
				continue;
			}
			rcdIdx = i;
			BigDecimal payNow = BigDecimal.ZERO;
			BigDecimal excess = BigDecimal.ZERO;
			if (rcd.getAmount().compareTo(rcd.getDueAmount()) >= 0) {
				payNow = rcd.getDueAmount();
				excess = rcd.getAmount().subtract(rcd.getDueAmount());
			} else {
				payNow = rcd.getAmount();
			}

			FinRepayHeader rph = new FinRepayHeader();
			rcd.setRepayHeader(rph);
			if (rcd.getDueAmount().compareTo(BigDecimal.ZERO) > 0) {
				rch.setBalAmount(payNow);
				rph.setFinReference(rch.getReference());
				rph.setValueDate(rch.getValueDate());
				if (receiptData.isEarlySettle()) {
					rph.setEarlyPayDate(rch.getValueDate());
				}
				rph.setFinEvent(rch.getReceiptPurpose());
				receiptData = recalAutoAllocation(receiptData, rch.getValueDate(), receiptData.isPresentment());
			}

			if (excess.compareTo(BigDecimal.ZERO) > 0) {
				rph.setFinEvent(rch.getReceiptPurpose());
				rph.setValueDate(rch.getValueDate());
				if (receiptData.isEarlySettle()) {
					rph.setEarlyPayDate(rch.getValueDate());
				}
				if (receiptPurposeCtg == 1) {
					rch.setBalAmount(excess);
					receiptData = partialApportion(receiptData);
					receiptData.setPartialPaidAmount(excess);
				} else {
					rph.setRepayAmount(rph.getRepayAmount().add(excess));
					rph.setExcessAmount(excess);
				}
			}

		}
		rch.setWaviedAmt(totWaived);
		receiptData.setAdjSchedule(false);
		logger.debug("Leaving");
		return receiptData;
	}

	public FinRepayHeader buildExcessHeader(FinReceiptHeader rch) {
		FinRepayHeader rph = new FinRepayHeader();
		if (rch.getBalAmount().compareTo(BigDecimal.ZERO) > 0) {
			rph.setFinReference(rch.getReference());
			rph.setValueDate(rch.getValueDate());
			rph.setFinEvent(rch.getExcessAdjustTo());
			rph.setPriAmount(rch.getBalAmount());
			rph.setRepayAmount(rch.getBalAmount());
		}
		return rph;
	}

	public String getRepayHeaderEvent(FinReceiptHeader rch) {
		String event = "";
		switch (receiptPurposeCtg) {
		case 0:
			event = rch.getExcessAdjustTo();
			break;
		case 1:
			event = FinanceConstants.FINSER_EVENT_EARLYRPY;
			break;
		case 2:
			event = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
			break;

		default:
			break;
		}
		return event;
	}

	public FinReceiptData prepareTempAllocList(FinReceiptData receiptData) {
		// Prepare Allocation List
		List<ReceiptAllocationDetail> radList = receiptData.getReceiptHeader().getAllocations();
		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail rad = radList.get(i);
			BigDecimal newDue = rad.getTotalDue().subtract(rad.getTotalPaid());

			if (newDue.compareTo(BigDecimal.ZERO) <= 0) {
				radList.remove(i);
				i = i - 1;
			}

			rad.setTotalDue(newDue);
			rad.setTotalPaid(BigDecimal.ZERO);
		}

		return receiptData;
	}

	// Return Net TDS Amount on Full Interest (i.e Net Due = INT Due - TDS
	// Returned)
	public BigDecimal getNetOffTDS(FinanceMain finMain, BigDecimal amount) {
		// Fetch and store Tax percentages one time
		setSMTParms();
		BigDecimal netAmount = amount.multiply(tdsMultiplier);
		netAmount = CalculationUtil.roundAmount(netAmount, tdsRoundMode, tdsRoundingTarget);

		return netAmount;
	}

	public BigDecimal getTDSAmount(FinanceMain finMain, BigDecimal amount) {
		// Fetch and store Tax percentages one time
		setSMTParms();
		BigDecimal netAmount = amount.multiply(tdsPerc.divide(big100));
		netAmount = CalculationUtil.roundAmount(netAmount, tdsRoundMode, tdsRoundingTarget);

		return netAmount;
	}

	// Return TDS Amount on Paid Interest. (i.e Total Paid = Net Due + TDS
	// Returned)
	public BigDecimal getTDS(FinanceMain finMain, BigDecimal amount) {
		// Fetch and store Tax percentages one time
		setSMTParms();

		/*
		 * BigDecimal tds = amount.multiply(tdsPerc); tds = tds.divide(BigDecimal.valueOf(100), 9,
		 * RoundingMode.HALF_UP); tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);
		 */
		BigDecimal netAmt = BigDecimal.ZERO;
		BigDecimal tdsAmt = BigDecimal.ZERO;

		netAmt = amount.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
		tdsAmt = amount.subtract(netAmt);
		tdsAmt = CalculationUtil.roundAmount(tdsAmt, tdsRoundMode, tdsRoundingTarget);

		return tdsAmt;
	}

	public TaxAmountSplit getGST(FinanceDetail financeDetail, TaxAmountSplit taxSplit) {
		taxSplit.setNetAmount(taxSplit.getAmount());
		String taxType = taxSplit.getTaxType();

		// No AMount to calculate Tax
		if (taxSplit.getAmount().compareTo(BigDecimal.ZERO) == 0) {
			return taxSplit;
		}

		// No Tax calculation required
		if (StringUtils.isBlank(taxType)) {
			return taxSplit;
		}

		setSMTParms();

		// Fetch and store Tax percentages one time
		if (financeDetail.getFinScheduleData().getGstExecutionMap().isEmpty()) {
			financeDetail = setGSTExecutionMap(financeDetail);
		}

		if (tgstPerc.compareTo(BigDecimal.ZERO) == 0) {
			setGSTExecutionMap(financeDetail);
		}

		if (tgstPerc == BigDecimal.ZERO) {
			return taxSplit;
		}

		if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
			return getInclusiveGST(financeDetail, taxSplit);
		} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			return getExclusiveGST(financeDetail, taxSplit);
		}

		return taxSplit;
	}

	public TaxAmountSplit getExclusiveGST(FinanceDetail financeDetail, TaxAmountSplit taxSplit) {
		BigDecimal taxableAmount = taxSplit.getAmount().subtract(taxSplit.getWaivedAmount());
		taxSplit.setcGST(GSTCalculator.getExclusiveTax(taxableAmount, cgstPerc));
		taxSplit.setsGST(GSTCalculator.getExclusiveTax(taxableAmount, sgstPerc));
		taxSplit.setuGST(GSTCalculator.getExclusiveTax(taxableAmount, ugstPerc));
		taxSplit.setiGST(GSTCalculator.getExclusiveTax(taxableAmount, igstPerc));
		taxSplit.setCess(GSTCalculator.getExclusiveTax(taxableAmount, cessPerc));
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST()).add(taxSplit.getiGST())
				.add(taxSplit.getCess()));
		taxSplit.setNetAmount(taxSplit.getAmount().add(taxSplit.gettGST()));
		return taxSplit;
	}

	public TaxAmountSplit getInclusiveGST(FinanceDetail financeDetail, TaxAmountSplit taxSplit) {
		BigDecimal taxableAmount = taxSplit.getAmount().subtract(taxSplit.getWaivedAmount());
		BigDecimal netAmount = GSTCalculator.getInclusiveAmount(taxableAmount, tgstPerc);

		taxSplit.setcGST(GSTCalculator.getExclusiveTax(netAmount, cgstPerc));
		taxSplit.setsGST(GSTCalculator.getExclusiveTax(netAmount, sgstPerc));
		taxSplit.setuGST(GSTCalculator.getExclusiveTax(netAmount, ugstPerc));
		taxSplit.setiGST(GSTCalculator.getExclusiveTax(netAmount, igstPerc));
		taxSplit.setCess(GSTCalculator.getExclusiveTax(netAmount, cessPerc));
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST()).add(taxSplit.getiGST())
				.add(taxSplit.getCess()));
		taxSplit.setNetAmount(netAmount.add(taxSplit.gettGST()));

		if (netAmount.add(taxSplit.gettGST()).compareTo(taxableAmount) != 0) {
			BigDecimal diff = taxableAmount.subtract(netAmount.add(taxSplit.gettGST()));
			taxSplit.setNetAmount(taxSplit.getNetAmount().add(diff));
		}

		return taxSplit;
	}

	/**
	 * Method for Processing Schedule Data and Receipts to Prepare Allocation Details
	 * 
	 * @param receiptData
	 * @param aFinScheduleData
	 */
	public FinReceiptData recalAutoAllocation(FinReceiptData receiptData, Date valueDate, boolean isPresentment) {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		setReceiptCategory(rch.getReceiptPurpose());
		List<ReceiptAllocationDetail> allocationsList = rch.getAllocations();
		List<FinODDetails> odList = receiptData.getFinanceDetail().getFinScheduleData().getFinODDetails();
		Cloner cloner = new Cloner();
		List<FinODDetails> tempOdList = cloner.deepClone(odList);
		setReceiptCategory(rch.getReceiptPurpose());
		rch.setSchdIdx(-1);
		rch.setLpiIdx(-1);
		rch.setLppIdx(-1);
		rch.setPftIdx(-1);
		rch.setTdsIdx(-1);
		rch.setNPftIdx(-1);
		rch.setPriIdx(-1);
		rch.setEmiIdx(-1);
		rch.setFutPriIdx(-1);
		rch.setFutNPftIdx(-1);
		rch.setFutPftIdx(-1);
		rch.setFutTdsIdx(-1);
		rch.setPpIdx(-1);

		// Reset Paid Amounts and Indexes
		for (int i = 0; i < allocationsList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationsList.get(i);

			/*
			 * if (allocate.getAllocationTo() != 0) { allocate.setPaidAvailable(allocate.getPaidAmount()); continue; }
			 */

			switch (allocate.getAllocationType()) {
			case RepayConstants.ALLOCATION_PFT:
				rch.setPftIdx(i);
				break;
			case RepayConstants.ALLOCATION_TDS:
				rch.setTdsIdx(i);
				break;
			case RepayConstants.ALLOCATION_NPFT:
				rch.setNPftIdx(i);
				break;
			case RepayConstants.ALLOCATION_PRI:
				rch.setPriIdx(i);
				break;
			case RepayConstants.ALLOCATION_LPFT:
				rch.setLpiIdx(i);
				break;
			case RepayConstants.ALLOCATION_ODC:
				rch.setLppIdx(i);
				break;
			case RepayConstants.ALLOCATION_EMI:
				rch.setEmiIdx(i);
				break;
			case RepayConstants.ALLOCATION_FUT_PFT:
				rch.setFutPftIdx(i);
				break;
			case RepayConstants.ALLOCATION_FUT_PRI:
				rch.setFutPriIdx(i);
				break;
			case RepayConstants.ALLOCATION_FUT_NPFT:
				rch.setFutNPftIdx(i);
				break;
			case RepayConstants.ALLOCATION_FUT_TDS:
				rch.setFutTdsIdx(i);
				break;
			case RepayConstants.ALLOCATION_PP:
				rch.setPpIdx(i);
				break;
			default:
				break;
			}

			if (!receiptData.isAdjSchedule()) {
				allocate.setPaidAmount(BigDecimal.ZERO);
				allocate.setTdsPaid(BigDecimal.ZERO);
				allocate.setPaidGST(BigDecimal.ZERO);
				allocate.setTotalPaid(BigDecimal.ZERO);
				allocate.setBalance(allocate.getTotalDue());
				allocate.setWaivedAvailable(allocate.getWaivedAmount());
				if (receiptPurposeCtg != 2) {
					allocate.setWaivedAmount(BigDecimal.ZERO);
					allocate.setWaivedGST(BigDecimal.ZERO);
				}
				allocate.setPaidAvailable(allocate.getTotalDue().subtract(allocate.getWaivedAmount()));
			}
		}

		if (receiptPurposeCtg == 2 && !receiptData.isAdjSchedule()) {
			receiptData = earlySettleAllocation(receiptData);
			if (receiptData.isSetPaidValues()) {
				allocated = receiptData.getAllocList();
				setPaidValues(receiptData);
			}
			receiptData.setSetPaidValues(true);
			setTotals(receiptData, 0);
			return receiptData;
		}

		BigDecimal balAmount = BigDecimal.ZERO;
		if (receiptData.isAdjSchedule()) {
			balAmount = rch.getBalAmount();
		} else {
			balAmount = rch.getReceiptAmount();
			rch.setBalAmount(balAmount);
		}

		// If no balance for repayment then return with out calculation
		if (balAmount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.debug("Leaving");
			return receiptData;
		}

		if (receiptData.isAdjSchedule()) {
			for (int i = 0; i < allocationsList.size(); i++) {
				receiptData = eventFeeAndAdviseApportion(receiptData, true);
			}
		} else {
			receiptData = eventFeeAndAdviseApportion(receiptData, true);
		}
		// Event Fee Apportionment

		// Schedules and LPP & LPI Apportionment
		if (rch.getBalAmount().compareTo(BigDecimal.ZERO) > 0) {
			receiptData = scheduleApportion(receiptData, isPresentment);
		}

		// LPP & LPI Apportionment as separate
		if (rch.isPenalSeparate()) {
			receiptData = sepratePenalApportion(receiptData);
		} else {
			for (ReceiptAllocationDetail alloc : receiptData.getReceiptHeader().getAllocations()) {
				if (RepayConstants.ALLOCATION_ODC.equals(alloc.getAllocationType())
						&& actualOdPaid.compareTo(BigDecimal.ZERO) > 0 && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE
								.equals(receiptData.getLppFeeType().getTaxComponent())) {
					calAllocationPaidGST(receiptData.getFinanceDetail(), actualOdPaid, alloc,
							FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
					TaxAmountSplit taxSplit = new TaxAmountSplit();
					taxSplit.setAmount(actualOdPaid);
					taxSplit.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
					taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
					actualOdPaid = actualOdPaid.add(taxSplit.gettGST());
					alloc.setTotalPaid(actualOdPaid);
					alloc.setPaidAmount(actualOdPaid);

				}
			}
		}

		// Advise apportionment
		if (receiptData.isAdjSchedule()) {
			for (int i = 0; i < allocationsList.size(); i++) {
				receiptData = eventFeeAndAdviseApportion(receiptData, false);
			}
		} else {
			if (rch.getBalAmount().compareTo(BigDecimal.ZERO) > 0) {
				receiptData = eventFeeAndAdviseApportion(receiptData, false);
			}
		}
		setEmi(receiptData);

		setTotals(receiptData, 0);

		if (!receiptData.isAdjSchedule()) {
			receiptData.getFinanceDetail().getFinScheduleData().setFinODDetails(tempOdList);
		}

		logger.debug("Leaving");
		return receiptData;
	}

	private FinReceiptData foreClosureAllocation(FinReceiptData receiptData) {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocationList = rch.getAllocations();
		for (ReceiptAllocationDetail allocate : allocationList) {
			BigDecimal dueAmount = new BigDecimal(0);
			dueAmount = allocate.getTotalDue();

			BigDecimal paidAmount = new BigDecimal(0);
			paidAmount = paidAmount.add(dueAmount).subtract(allocate.getWaivedAmount());

			allocate.setPaidAmount(paidAmount);
			allocate.setPaidGST(allocate.getDueGST());
			if (allocate.isTdsReq()) {
				if (allocate.getTotalDue().compareTo(allocate.getPaidAmount()) == 0) {
					allocate.setTdsPaid(allocate.getTdsDue());
				} else {
					BigDecimal amount = getPaidAmount(allocate, allocate.getPaidAmount());
					BigDecimal tdsPaidNow = getTDSAmount(
							receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), amount);
					allocate.setTdsPaid(allocate.getTdsPaid().add(tdsPaidNow));

				}
			} else {
				if (allocate.getTdsDue().compareTo(BigDecimal.ZERO) > 0) {
					if (RepayConstants.ALLOCATION_PFT.equals(allocate.getAllocationType())) {
						BigDecimal pft = getPftAmount(receiptData.getFinanceDetail().getFinScheduleData(), paidAmount);
						allocate.setTdsPaid(pft.subtract(paidAmount));
					} else if (RepayConstants.ALLOCATION_FUT_PFT.equals(allocate.getAllocationType())) {
						FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
						boolean isFullyPaid = false;
						List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
						FinanceScheduleDetail lastSchd = schdDtls.get(schdDtls.size() - 1);
						BigDecimal npftPaid = allocate.getPaidAmount();
						BigDecimal pftPaid = allocate.getTotalPaid();

						if (npftPaid.compareTo(allocate.getTotalDue()) == 0) {
							isFullyPaid = true;
						}

						if (lastSchd.isTDSApplicable()) {
							pftPaid = npftPaid.add(getTDS(fsd.getFinanceMain(), npftPaid));
						} else {
							npftPaid = pftPaid;
						}
						if (isFullyPaid) {
							allocate.setTdsPaid(allocate.getTdsDue());
						} else {
							allocate.setTdsPaid(pftPaid.subtract(npftPaid));
						}
					}
				}
			}
			allocate.setTotalPaid(paidAmount.add(allocate.getTdsDue()));
		}

		logger.debug("Leaving");
		return receiptData;
	}

	private FinReceiptData adjustAdvanceInt(FinReceiptData receiptData) {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocationList = rch.getAllocations();

		if (CollectionUtils.isEmpty(rch.getXcessPayables())) {
			return receiptData;
		}

		for (XcessPayables xcess : rch.getXcessPayables()) {
			if (!RepayConstants.EXAMOUNTTYPE_ADVINT.equals(xcess.getPayableType())) {
				continue;
			}

			BigDecimal balAmount = xcess.getBalanceAmt();
			for (int i = 0; i < allocationList.size(); i++) {
				ReceiptAllocationDetail allocate = allocationList.get(i);
				if (RepayConstants.ALLOCATION_NPFT.equals(allocate.getAllocationType())
						|| RepayConstants.ALLOCATION_FUT_NPFT.equals(allocate.getAllocationType())
						|| RepayConstants.ALLOCATION_FUT_PRI.equals(allocate.getAllocationType())) {

					BigDecimal payNow = allocate.getTotalDue()
							.subtract(allocate.getTotalPaid().add(allocate.getWaivedAmount()));
					if (payNow.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					if (balAmount.compareTo(payNow) > 0) {
						balAmount = balAmount.subtract(payNow);
					} else {
						payNow = balAmount;
						balAmount = BigDecimal.ZERO;
					}
					xcess.setBalanceAmt(xcess.getBalanceAmt().subtract(payNow));
					xcess.setTotPaidNow(xcess.getTotPaidNow().add(payNow));
					allocate.setTotalPaid(allocate.getTotalPaid().add(payNow));
					allocate.setPaidAmount(allocate.getPaidAmount().add(payNow));
					if (RepayConstants.ALLOCATION_NPFT.equals(allocate.getAllocationType())
							|| RepayConstants.ALLOCATION_FUT_PRI.equals(allocate.getAllocationType())) {
						for (ReceiptAllocationDetail all : rch.getAllocations()) {
							if (RepayConstants.ALLOCATION_EMI.equals(all.getAllocationType())) {
								all.setTotalPaid(all.getTotalPaid().add(payNow));
								all.setPaidAmount(all.getPaidAmount().add(payNow));
								break;
							}
						}
					}

					if (balAmount.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				}

			}
		}

		return receiptData;
	}

	private FinReceiptData earlySettleAllocation(FinReceiptData receiptData) {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		if (rch.getXcessPayables() != null && rch.getXcessPayables().size() > 0) {
			receiptData = adjustAdvanceInt(receiptData);
			for (XcessPayables xcess : rch.getXcessPayables()) {
				if (RepayConstants.EXAMOUNTTYPE_ADVINT.equals(xcess.getPayableType())) {
					continue;
				}
				BigDecimal balAmount = xcess.getBalanceAmt();
				recalEarlyStlAlloc(receiptData, xcess.getBalanceAmt());
				xcess.setTotPaidNow(balAmount);
				xcess.setBalanceAmt(xcess.getBalanceAmt().subtract(balAmount));
			}
		}
		if (!receiptData.isForeClosure() && rch.getReceiptAmount().compareTo(BigDecimal.ZERO) > 0) {
			recalEarlyStlAlloc(receiptData, rch.getReceiptAmount());
		}
		if (receiptData.isForeClosure()) {
			receiptData = foreClosureAllocation(receiptData);
			return receiptData;
		}
		rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocationList = rch.getAllocations();
		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationList.get(i);
			if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_EMI)) {
				BigDecimal[] emisplit = null;
				if (allocate.getTotalDue().compareTo(allocate.getPaidAmount()) == 0) {
					emisplit = getCompEmiSplit(receiptData);
				} else {
					emisplit = getEmiSplit(receiptData, allocate.getPaidAmount());
				}
				allocate.setTotalPaid(emisplit[0].add(emisplit[2]));
				for (ReceiptAllocationDetail alloc : rch.getAllocations()) {
					if (StringUtils.equals(alloc.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
						alloc.setTotalPaid(emisplit[1]);
						alloc.setPaidAmount(emisplit[2]);
						alloc.setTdsPaid(emisplit[1].subtract(emisplit[2]));
					} else if (StringUtils.equals(alloc.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
						alloc.setTotalPaid(emisplit[0]);
						alloc.setPaidAmount(emisplit[0]);
					}
				}
				continue;
			}

			if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_FUT_PFT)) {
				FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
				boolean isFullyPaid = false;
				List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
				FinanceScheduleDetail lastSchd = schdDtls.get(schdDtls.size() - 1);
				BigDecimal npftPaid = allocate.getPaidAmount();
				BigDecimal pftPaid = allocate.getTotalPaid();

				if (npftPaid.compareTo(allocate.getTotalDue()) == 0) {
					isFullyPaid = true;
				}

				if (lastSchd.isTDSApplicable()) {
					pftPaid = npftPaid.add(getTDS(fsd.getFinanceMain(), npftPaid));
				} else {
					npftPaid = pftPaid;
				}
				if (isFullyPaid) {
					allocate.setTdsPaid(allocate.getTdsDue());
				} else {
					allocate.setTdsPaid(pftPaid.subtract(npftPaid));
				}

			}
			if (allocate.isTdsReq()) {
				if (allocate.getTotalDue().compareTo(allocate.getPaidAmount()) == 0) {
					allocate.setTdsPaid(allocate.getTdsDue());
				} else {
					BigDecimal paidAmount = getPaidAmount(allocate, allocate.getPaidAmount());
					BigDecimal tdsPaidNow = getTDSAmount(
							receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), paidAmount);
					allocate.setTdsPaid(allocate.getTdsPaid().add(tdsPaidNow));

				}
			}
			allocate.setTotalPaid(allocate.getPaidAmount().add(allocate.getTdsPaid()));

		}
		return receiptData;
	}

	private FinReceiptData recalEarlyStlAlloc(FinReceiptData receiptData, BigDecimal amount) {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocationList = rch.getAllocations();
		BigDecimal totalReceiptAmount = amount;
		if (totalReceiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}
		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail allocate = allocationList.get(i);
			if (RepayConstants.ALLOCATION_PFT.equals(allocate.getAllocationType())
					|| RepayConstants.ALLOCATION_PRI.equals(allocate.getAllocationType())) {
				continue;
			}
			BigDecimal payNow = allocate.getTotalDue()
					.subtract(allocate.getPaidAmount().add(allocate.getWaivedAmount()));
			if (payNow.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			if (totalReceiptAmount.compareTo(payNow) > 0) {
				totalReceiptAmount = totalReceiptAmount.subtract(payNow);
			} else {
				payNow = totalReceiptAmount;
				totalReceiptAmount = BigDecimal.ZERO;
			}

			//allocate.setTotalPaid(allocate.getTotalPaid().add(payNow));
			allocate.setPaidAmount(allocate.getPaidAmount().add(payNow));

			// GST calculation for allocations(always Paid Amount we are taking
			// the inclusive type here because we are doing reverse calculation
			// here)
			if (allocate.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
				if (allocate.getTotalDue().compareTo(allocate.getPaidAmount()) == 0) {
					allocate.setPaidCGST(allocate.getDueCGST());
					allocate.setPaidIGST(allocate.getDueIGST());
					allocate.setPaidUGST(allocate.getDueUGST());
					allocate.setPaidSGST(allocate.getDueSGST());
					allocate.setPaidCESS(allocate.getDueCESS());
					allocate.setPaidGST(allocate.getDueGST());
				} else {
					calAllocationGST(receiptData.getFinanceDetail(), allocate.getPaidAmount(), allocate,
							FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
				}
			}

			if (totalReceiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		logger.debug("Leaving");
		return receiptData;
	}

	public FinReceiptData eventFeeAndAdviseApportion(FinReceiptData receiptData, boolean isEventFee) {
		if (receiptData.isPresentment()) {
			return receiptData;
		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		BigDecimal balAmount = rch.getBalAmount();
		BigDecimal balDue = BigDecimal.ZERO;
		FinRepayHeader rph = null;

		if (receiptData.isAdjSchedule()) {
			rph = rcdList.get(rcdIdx).getRepayHeader();
		}

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();

		for (int i = 0; i < allocationList.size(); i++) {
			BigDecimal payNow = BigDecimal.ZERO;
			BigDecimal waiveNow = BigDecimal.ZERO;
			ReceiptAllocationDetail allocate = allocationList.get(i);
			if (isEventFee) {
				if (allocate.getAllocationTo() >= 0) {
					continue;
				}
			} else {
				if (allocate.getAllocationTo() <= 0) {
					continue;
				}
			}

			if (allocate.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (receiptData.isAdjSchedule()) {
				if (rch.getBalAmount().compareTo(allocate.getPaidAvailable()) >= 0) {
					balAmount = allocate.getPaidAvailable();
				}
			}
			balDue = allocate.getBalance();

			if (allocate.getWaivedAvailable().compareTo(balDue) > 0) {
				waiveNow = balDue;
			} else {
				waiveNow = allocate.getWaivedAvailable();
			}

			balDue = balDue.subtract(waiveNow);

			// Waiver GST Calculation
			if (waiveNow.compareTo(BigDecimal.ZERO) > 0 && allocate.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
				calAllocationWaiverGST(receiptData.getFinanceDetail(), waiveNow, allocate);
			}

			// Adjust Paid Amount
			if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (balAmount.compareTo(balDue) >= 0) {
					payNow = balDue;
				} else {
					payNow = balAmount;
				}
			}

			balAmount = balAmount.subtract(payNow);
			rch.setBalAmount(rch.getBalAmount().subtract(payNow));

			updateAllocationWithTds(allocate, payNow, waiveNow, receiptData.getFinanceDetail(), BigDecimal.ZERO);

			if (receiptData.isAdjSchedule()
					&& (payNow.compareTo(BigDecimal.ZERO) > 0 || waiveNow.compareTo(BigDecimal.ZERO) > 0)) {
				allocate.setPaidNow(payNow);
				allocate.setWaivedNow(waiveNow);
				if (!isEventFee) {
					rph.setRepayAmount(rph.getRepayAmount().add(payNow));
					rph.setAdviseAmount(rph.getAdviseAmount().add(payNow));
					rph.setTotalWaiver(rph.getTotalWaiver().add(waiveNow));
					rch.getReceiptDetails().get(rcdIdx).getAdvMovements()
							.add(buildManualAdvsieMovements(allocate, rch.getValueDate()));
				} else {
					// Identify field to set event fees
					rph.setRepayAmount(rph.getRepayAmount().add(payNow));
					rph.setFeeAmount(rph.getFeeAmount().add(payNow));
					rph.setTotalWaiver(rph.getTotalWaiver().add(waiveNow));
					if ((payNow.add(waiveNow)).compareTo(BigDecimal.ZERO) > 0) {
						updateFinFeeDetails(receiptData, allocate);
					}
				}
			}

			if (rch.getBalAmount().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}

		return receiptData;
	}

	private FinReceiptData updateFinFeeDetails(FinReceiptData receiptData, ReceiptAllocationDetail allocate) {
		List<FinFeeDetail> finFeeDtls = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isNotEmpty(finFeeDtls)) {
			FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			for (FinFeeDetail feeDtl : finFeeDtls) {
				if (allocate.getAllocationTo() == -(feeDtl.getFeeTypeID())) {

					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
						feeDtl.setPaidAmountOriginal(allocate.getPaidAmount().subtract(allocate.getPaidGST()));
						feeDtl.setRemainingFeeOriginal(feeDtl.getActualAmountOriginal()
								.subtract(allocate.getWaivedNow()).subtract(feeDtl.getPaidAmountOriginal()));
					} else {
						feeDtl.setPaidAmount(feeDtl.getPaidAmount().add(allocate.getPaidNow()));
					}
					feeDtl.setWaivedAmount(feeDtl.getWaivedAmount().add(allocate.getWaivedNow()));
					feeDtl.setRemainingFee(
							feeDtl.getActualAmount().subtract(feeDtl.getPaidAmount().add(feeDtl.getWaivedAmount())));
					feeDtl.setPaidTDS(allocate.getTdsPaid());
					feeDtl.setPaidCalcReq(true);
					break;
				}
			}

			Map<String, BigDecimal> map = GSTCalculator.getTaxPercentages(finMain.getCustID(), finMain.getFinCcy(),
					null, finMain.getFinBranch(), receiptData.getFinanceDetail().getFinanceTaxDetail());
			feeCalculator.calculateFeeDetail(receiptData, map);
		}

		return receiptData;
	}

	public BigDecimal getPaidAmount(ReceiptAllocationDetail allocate, BigDecimal netAmount) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal totalPerc = allocate.getPercCGST().add(allocate.getPercSGST())
				.add(allocate.getPercUGST().add(allocate.getPercIGST())).add(allocate.getPercCESS())
				.subtract(allocate.getPercTds());
		BigDecimal percMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).add(totalPerc), 20,
				RoundingMode.HALF_DOWN);
		paidAmount = netAmount.multiply(percMultiplier);
		//paidAmount = CalculationUtil.roundAmount(paidAmount, tdsRoundMode, tdsRoundingTarget);
		return paidAmount;
	}

	public BigDecimal getExclusiveGSTAmount(ReceiptAllocationDetail allocate, BigDecimal netAmount) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal totalPerc = allocate.getPercCGST().add(allocate.getPercIGST())
				.add(allocate.getPercSGST().add(allocate.getPercIGST()));
		BigDecimal percMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).add(totalPerc), 20,
				RoundingMode.HALF_DOWN);
		paidAmount = netAmount.multiply(percMultiplier);
		paidAmount = CalculationUtil.roundAmount(paidAmount, tdsRoundMode, tdsRoundingTarget);
		return paidAmount;
	}

	private ManualAdviseMovements buildManualAdvsieMovements(ReceiptAllocationDetail allocate, Date valueDate) {
		logger.debug(Literal.ENTERING);

		ManualAdviseMovements movement = new ManualAdviseMovements();
		movement.setAdviseID(allocate.getAllocationTo());
		movement.setMovementDate(valueDate);
		movement.setMovementAmount(allocate.getPaidNow());

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
			movement.setPaidAmount(allocate.getPaidNow().subtract(allocate.getPaidGST()));
			movement.setWaivedAmount(
					allocate.getWaivedNow().subtract(allocate.getWaivedGST()).add(allocate.getTdsWaived()));
		} else {
			movement.setPaidAmount(allocate.getPaidNow());
			movement.setWaivedAmount(allocate.getWaivedNow().add(allocate.getTdsWaived()));
		}
		movement.setPaidAmount(movement.getPaidAmount().add(allocate.getTdsPaid()));
		movement.setFeeTypeCode(allocate.getFeeTypeCode());
		movement.setTdsPaid(allocate.getTdsPaid());

		//CESS Calculations
		TaxHeader taxHeader = new TaxHeader();
		TaxHeader allocTaxHeader = allocate.getTaxHeader();

		if (allocTaxHeader != null) {
			List<Taxes> taxDetails = allocTaxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxDetail : taxDetails) {
					Taxes taxes = new Taxes();
					BeanUtils.copyProperties(taxDetail, taxes);
					taxes.setId(Long.MIN_VALUE);
					taxHeader.getTaxDetails().add(taxes);
				}
			}
		}
		movement.setTaxHeader(taxHeader);

		logger.debug(Literal.LEAVING);

		return movement;
	}

	public ReceiptAllocationDetail setAllocateTax(ReceiptAllocationDetail allocate, FinanceDetail financeDetail) {

		TaxAmountSplit taxSplit = new TaxAmountSplit();
		taxSplit.setAmount(allocate.getPaidNow());
		taxSplit.setNetAmount(allocate.getPaidNow());

		if (StringUtils.isNotBlank(allocate.getTaxType())) {
			taxSplit.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

			if (allocate.getTotalPaid().compareTo(BigDecimal.ZERO) != 0) {
				taxSplit = getGST(financeDetail, taxSplit);
			}
		}

		allocate.setTotalPaid(allocate.getTotalPaid().add(taxSplit.getNetAmount()));
		allocate.setPaidGST(taxSplit.gettGST());
		allocate.setPaidCGST(taxSplit.getcGST());
		allocate.setPaidSGST(taxSplit.getsGST());
		allocate.setPaidUGST(taxSplit.getuGST());
		allocate.setPaidIGST(taxSplit.getiGST());
		allocate.setPaidCESS(taxSplit.getCess());

		if (allocate.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
			calAllocationWaiverGST(financeDetail, allocate.getWaivedAmount(), allocate);
		}

		if (StringUtils.equals(allocate.getTaxType(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			allocate.setPaidAmount(allocate.getPaidAmount().add(allocate.getPaidNow()).subtract(taxSplit.gettGST()));
			allocate.setBalance(allocate.getTotalDue().subtract(allocate.getTotalPaid())
					.subtract(allocate.getWaivedAmount()).subtract(allocate.getWaivedGST()));
		} else {
			allocate.setPaidAmount(allocate.getPaidAmount().add(allocate.getPaidNow()));
			allocate.setBalance(
					allocate.getTotalDue().subtract(allocate.getTotalPaid()).subtract(allocate.getWaivedAmount()));
		}

		return allocate;
	}

	private FinReceiptData scheduleApportion(FinReceiptData receiptData, boolean isPresentment) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date valueDate = rch.getValueDate();

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		List<FinanceScheduleDetail> schdDetails = scheduleData.getFinanceScheduleDetails();
		setReceiptCategory(rch.getReceiptPurpose());

		// Penal after schedule collection OR along
		String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();

		String finReference = scheduleData.getFinanceMain().getFinReference();
		repayHierarchy = getRepayHierarchyOnNPA(finReference);
		if (repayHierarchy.contains("CS")) {
			rch.setPenalSeparate(true);
		} else {
			rch.setPenalSeparate(false);
		}

		char[] rpyOrder = repayHierarchy.replace("CS", "").toCharArray();
		setSMTParms();

		// Load Pending Schedules until balance available for payment
		for (int i = 1; i < schdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = schdDetails.get(i);
			Date schdDate = curSchd.getSchDate();
			if (schdDate.compareTo(valueDate) > 0) {
				break;
			}

			// If Presentment Process, No other schedule should be effected.
			if (ImplementationConstants.ALLOW_OLDEST_DUE) {
				//Adjust Oldest Dues first 
			} else {
				if (isPresentment && (DateUtility.compare(valueDate, schdDate) != 0)) {
					continue;
				}
			}

			rch.setSchdIdx(i);
			if (rch.getBalAmount().compareTo(BigDecimal.ZERO) > 0) {
				receiptData = calApportion(rpyOrder, receiptData);
			}

			// No more Receipt amount left for next schedules
			if (!receiptData.isAdjSchedule()) {
				if (receiptData.getReceiptHeader().getBalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}
		}

		return receiptData;
	}

	private String getRepayHierarchyOnNPA(String finReference) {
		String repayHierarchy = "";
		if (ImplementationConstants.ALLOW_NPA_PROVISION) {
			String npaRepayHierarchy = SysParamUtil.getValueAsString(SMTParameterConstants.RPYHCY_ON_NPA);
			if (StringUtils.trimToNull(npaRepayHierarchy) != null) {
				boolean isNPARepayHierarchyReq = npaService.isNAPRepayHierarchyReq(finReference);

				if (isNPARepayHierarchyReq) {
					repayHierarchy = npaRepayHierarchy;
				}
			}
		} else {
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_DIFF_RPYHCY_NPA)) {
				// FIXME:Currently NPA is updating on EOD, so for temporary purpose
				// we are changing repay hierarchy on basis of DPD.
				int dueBucket = financeScheduleDetailDAO.getDueBucket(finReference);
				if (dueBucket >= SysParamUtil.getValueAsInt(SMTParameterConstants.RPYHCY_ON_DPD_BUCKET)) {
					repayHierarchy = SysParamUtil.getValueAsString(SMTParameterConstants.RPYHCY_ON_NPA);
				}
			}
		}

		return repayHierarchy == null ? "" : repayHierarchy;
	}

	public FinReceiptData calApportion(char[] rpyOrder, FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		for (char repayTo : rpyOrder) {
			if (repayTo == RepayConstants.REPAY_PRINCIPAL) {
				receiptData = priApportion(receiptData);
				if (receiptData.isAdjSchedule() && receiptPurposeCtg == 2 && rch.getFutPriIdx() > 0
						&& rch.getPriIdx() > 0 && rch.getAllocations().get(rch.getPriIdx()).getPaidAvailable()
								.compareTo(BigDecimal.ZERO) <= 0) {
					receiptData = priApportion(receiptData);
				}
			} else if (repayTo == RepayConstants.REPAY_PROFIT) {
				receiptData = intApportion(receiptData);
			} else if (!rch.isPenalSeparate() && repayTo == RepayConstants.REPAY_PENALTY) {
				receiptData = penalApportion(receiptData);
			} else if (repayTo == RepayConstants.REPAY_OTHERS) {
				// Code Related to Schedule Fees & Insurance Deleted
			}
			if (!receiptData.isAdjSchedule()) {
				if (receiptData.getReceiptHeader().getBalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}
		}

		return receiptData;
	}

	public BigDecimal[] getCompEmiSplit(FinReceiptData receiptData) {
		BigDecimal[] emiSplit = new BigDecimal[3];
		Arrays.fill(emiSplit, BigDecimal.ZERO);
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		for (ReceiptAllocationDetail alloc : rch.getAllocations()) {
			if (StringUtils.equals(alloc.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
				emiSplit[2] = alloc.getTotalDue();
				emiSplit[1] = alloc.getTotalDue().add(alloc.getTdsDue());
			} else if (StringUtils.equals(alloc.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
				emiSplit[0] = alloc.getTotalDue();
			}
		}

		return emiSplit;
	}

	public FinReceiptData priApportion(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		int schdIdx = rch.getSchdIdx();
		boolean isFutPri = false;
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(schdIdx);
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();

		BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
		BigDecimal balAmount = BigDecimal.ZERO;
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;
		if (balPri.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}
		FinRepayHeader rph = null;
		ReceiptAllocationDetail allocate = null;
		if (rch.getPriIdx() >= 0) {
			allocate = allocationList.get(rch.getPriIdx());
			if (allocate.getPaidAvailable().compareTo(BigDecimal.ZERO) <= 0
					&& allocate.getWaivedAvailable().compareTo(BigDecimal.ZERO) <= 0 && rch.getFutPriIdx() > 0) {
				isFutPri = true;
				allocate = allocationList.get(rch.getFutPriIdx());
			}
		} else {
			if (receiptPurposeCtg == 2) {
				isFutPri = true;
				allocate = allocationList.get(rch.getFutPriIdx());
			}
		}
		if (allocate == null) {
			return receiptData;
		}
		balAmount = rch.getBalAmount();
		if (receiptData.isAdjSchedule()) {
			rph = rcdList.get(rcdIdx).getRepayHeader();
		}
		if (allocate.getPaidAvailable().compareTo(BigDecimal.ZERO) == 0 && receiptPurposeCtg == 2
				&& allocate.getWaivedAvailable().compareTo(BigDecimal.ZERO) == 0) {
			isFutPri = true;
			allocate = allocationList.get(rch.getFutPriIdx());
		}
		if (rch.getBalAmount().compareTo(allocate.getPaidAvailable()) > 0) {
			balAmount = allocate.getPaidAvailable();
		}

		if (allocate.getWaivedAvailable().compareTo(balPri) > 0) {
			waivedNow = balPri;
		} else {
			waivedNow = allocate.getWaivedAvailable();
		}

		balPri = balPri.subtract(waivedNow);

		// Adjust Paid Amount
		if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(balPri) >= 0) {
				paidNow = balPri;
			} else {
				paidNow = balAmount;
			}
		}

		balAmount = balAmount.subtract(paidNow);
		rch.setBalAmount(rch.getBalAmount().subtract(paidNow));
		updateAllocation(allocate, paidNow, waivedNow, BigDecimal.ZERO, BigDecimal.ZERO,
				receiptData.getFinanceDetail());
		if (receiptData.isAdjSchedule() && paidNow.add(waivedNow).compareTo(BigDecimal.ZERO) > 0) {
			rph.setRepayAmount(rph.getRepayAmount().add(paidNow));
			rph.setPriAmount(rph.getPriAmount().add(paidNow));
			/*
			 * if (isFutPri) { rph.setFutPriAmount(rph.getFutPriAmount().add(paidNow)); }
			 */
			rph.setTotalWaiver(rph.getTotalWaiver().add(waivedNow));
			receiptData = updateRPS(receiptData, allocate, "PRI");
			allocate.setPaidNow(BigDecimal.ZERO);
			allocate.setWaivedNow(BigDecimal.ZERO);
		}

		return receiptData;
	}

	public FinReceiptData partialApportion(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		FinanceScheduleDetail curSchd = null;
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();

		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail schd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (DateUtility.compare(schd.getSchDate(), rch.getValueDate()) == 0) {
				rch.setSchdIdx(i);
				curSchd = schd;
				break;
			}
		}

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();

		BigDecimal balPri = BigDecimal.ZERO;
		BigDecimal balAmount = BigDecimal.ZERO;
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;

		if (curSchd != null) {
			balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
		}

		if (balPri.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}
		FinRepayHeader rph = null;
		ReceiptAllocationDetail allocate = allocationList.get(rch.getPpIdx());
		balAmount = rch.getBalAmount();
		rph = rcdList.get(rcdIdx).getRepayHeader();
		if (rch.getBalAmount().compareTo(allocate.getPaidAvailable()) > 0) {
			balAmount = allocate.getPaidAvailable();
		}

		if (allocate.getWaivedAvailable().compareTo(balPri) > 0) {
			waivedNow = balPri;
		} else {
			waivedNow = allocate.getWaivedAvailable();
		}

		balPri = balPri.subtract(waivedNow);

		// Adjust Paid Amount
		if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(balPri) >= 0) {
				paidNow = balPri;
			} else {
				paidNow = balAmount;
			}
		}

		balAmount = balAmount.subtract(paidNow);
		rch.setBalAmount(rch.getBalAmount().subtract(paidNow));

		updateAllocation(allocate, paidNow, waivedNow, BigDecimal.ZERO, BigDecimal.ZERO,
				receiptData.getFinanceDetail());

		if (receiptData.isAdjSchedule() && paidNow.add(waivedNow).compareTo(BigDecimal.ZERO) > 0) {
			rph.setRepayAmount(rph.getRepayAmount().add(paidNow));
			rph.setPriAmount(rph.getPriAmount().add(paidNow));
			//rph.setPartialPaidAmount(rph.getPartialPaidAmount().add(paidNow));
			rph.setTotalWaiver(rph.getTotalWaiver().add(waivedNow));
			receiptData = updateRPS(receiptData, allocate, "PRI");
			allocate.setPaidNow(BigDecimal.ZERO);
			allocate.setWaivedNow(BigDecimal.ZERO);
		}

		return receiptData;
	}

	public FinReceiptData intApportion(FinReceiptData receiptData) {
		String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
		char[] pftPayOrder = profit.toCharArray();
		for (char pftPayTo : pftPayOrder) {
			if (pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
				receiptData = lateIntApportion(receiptData);
			} else {
				receiptData = repayIntApportion(receiptData);
			}
		}

		return receiptData;
	}

	public FinReceiptData repayIntApportion(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		int schdIdx = rch.getSchdIdx();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(schdIdx);
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();

		BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
		BigDecimal balAmount = rch.getBalAmount();
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal tdsPaidNow = BigDecimal.ZERO;
		BigDecimal tds = BigDecimal.ZERO;
		BigDecimal nBalPft = BigDecimal.ZERO;
		BigDecimal npftWaived = BigDecimal.ZERO;
		BigDecimal tdsWaived = BigDecimal.ZERO;

		boolean isFuture = false;

		if (balPft.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;

		}
		if (receiptPurposeCtg == 2) {
			if (rch.getPftIdx() < 0 && rch.getFutPftIdx() < 0) {
				return receiptData;
			}
		} else {
			if (rch.getPftIdx() < 0) {
				return receiptData;
			}
		}
		ReceiptAllocationDetail allocatePft = null;
		if (receiptPurposeCtg == 2 && rch.getPftIdx() < 0) {
			isFuture = true;
			allocatePft = allocationList.get(rch.getFutPftIdx());
		} else {
			allocatePft = allocationList.get(rch.getPftIdx());
		}
		FinRepayHeader rph = null;

		if (receiptData.isAdjSchedule()) {
			rph = rcdList.get(rcdIdx).getRepayHeader();
		}
		if (allocatePft.getPaidAvailable().compareTo(BigDecimal.ZERO) <= 0
				&& allocatePft.getWaivedAvailable().compareTo(BigDecimal.ZERO) <= 0 && receiptPurposeCtg == 2
				&& rch.getFutPftIdx() > 0) {
			isFuture = true;
			allocatePft = allocationList.get(rch.getFutPftIdx());

		}
		if (rch.getBalAmount().compareTo(allocatePft.getPaidAvailable()) > 0) {
			balAmount = allocatePft.getPaidAvailable();
		}

		if (curSchd.isTDSApplicable()) {
			tds = getTDS(finScheduleData.getFinanceMain(), curSchd.getProfitSchd());
			tds = tds.subtract(curSchd.getTDSPaid());
		}

		nBalPft = balPft.subtract(tds);

		if (allocatePft.getWaivedAvailable().compareTo(nBalPft) > 0) {
			npftWaived = nBalPft;
		} else {
			npftWaived = allocatePft.getWaivedAvailable();
		}

		nBalPft = nBalPft.subtract(npftWaived);
		if (curSchd.isTDSApplicable()) {
			tdsWaived = getNetOffTDS(finScheduleData.getFinanceMain(), npftWaived).subtract(npftWaived);
			if (tdsWaived.compareTo(tds) > 0) {
				tdsWaived = tds;
			}
			tds = tds.subtract(tdsWaived);
		}

		// Adjust Paid Amount
		if (balAmount.compareTo(BigDecimal.ZERO) > 0 && nBalPft.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(nBalPft) >= 0) {
				paidNow = nBalPft;
				tdsPaidNow = tds;
			} else {
				paidNow = balAmount;
				if (curSchd.isTDSApplicable()) {
					BigDecimal pftNow = getNetOffTDS(finScheduleData.getFinanceMain(), paidNow);
					tdsPaidNow = pftNow.subtract(paidNow);
				}
			}
		}

		balAmount = balAmount.subtract(paidNow);

		rch.setBalAmount(rch.getBalAmount().subtract(paidNow));
		allocatePft.setTdsWaived(allocatePft.getTdsWaived().add(tdsWaived));
		updateAllocation(allocatePft, paidNow, npftWaived, tdsPaidNow, tdsWaived, receiptData.getFinanceDetail());
		if (receiptData.isAdjSchedule() && paidNow.add(npftWaived).compareTo(BigDecimal.ZERO) > 0) {
			rph.setRepayAmount(rph.getRepayAmount().add(paidNow));
			rph.setPftAmount(rph.getPftAmount().add(paidNow.add(tdsPaidNow)));
			/*
			 * if (isFuture) { rph.setFutPftAmount(rph.getFutPftAmount().add(paidNow.add(tdsPaidNow))); }
			 */
			allocatePft.setTdsPaidNow(tdsPaidNow);
			allocatePft.setTdsWaivedNow(tdsWaived);
			rph.setTotalWaiver(rph.getTotalWaiver().add(npftWaived));
			receiptData = updateRPS(receiptData, allocatePft, "INT");
			allocatePft.setPaidNow(BigDecimal.ZERO);
			allocatePft.setWaivedNow(BigDecimal.ZERO);
		}
		return receiptData;
	}

	public FinReceiptData lateIntApportion(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		// LPI Not exits
		if (rch.getLpiIdx() < 0) {
			return receiptData;
		}

		BigDecimal balAmount = BigDecimal.ZERO;

		int schdIdx = rch.getSchdIdx();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(schdIdx);

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		ReceiptAllocationDetail allocate = allocationList.get(rch.getLpiIdx());

		FinRepayHeader rph = null;
		if (receiptData.isAdjSchedule()) {
			rph = rch.getReceiptDetails().get(rcdIdx).getRepayHeader();
			balAmount = allocate.getPaidAvailable();
		} else {
			balAmount = rch.getBalAmount();
		}

		BigDecimal balLPI = BigDecimal.ZERO;
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;
		boolean isLPIFound = false;
		FinODDetails finOd = null;
		List<FinODDetails> finODDetails = finScheduleData.getFinODDetails();
		for (int i = 0; i < finODDetails.size(); i++) {
			FinODDetails fod = finODDetails.get(i);

			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				finOd = finODDetails.get(i);
				rch.setOdIdx(i);
				balLPI = fod.getLPIBal();
				isLPIFound = true;
				break;
			}

			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) > 0) {
				break;
			}
		}

		if (!isLPIFound || balLPI.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}
		if (allocate.getWaivedAvailable().compareTo(BigDecimal.ZERO) > 0) {
			if (allocate.getWaivedAvailable().compareTo(balLPI) > 0) {
				waivedNow = balLPI;
			} else {
				waivedNow = allocate.getWaivedAvailable();
			}
		}

		balLPI = balLPI.subtract(waivedNow);

		// Adjust Paid Amount
		if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(balLPI) >= 0) {
				paidNow = balLPI;
			} else {
				paidNow = balAmount;
			}
		}

		balAmount = balAmount.subtract(paidNow);
		finOd.setLPIPaid(finOd.getLPIPaid().add(paidNow));
		finOd.setLPIWaived(finOd.getLPIWaived().add(waivedNow));
		finOd.setLPIBal(finOd.getLPIBal().subtract(paidNow.add(waivedNow)));
		updateAllocation(allocate, paidNow, waivedNow, BigDecimal.ZERO, BigDecimal.ZERO,
				receiptData.getFinanceDetail());
		if (receiptData.isAdjSchedule() && paidNow.add(waivedNow).compareTo(BigDecimal.ZERO) > 0) {
			rph.setRepayAmount(rph.getRepayAmount().add(paidNow));
			rph.setLpiAmount(rph.getLpiAmount().add(paidNow));
			rph.setTotalWaiver(rph.getTotalWaiver().add(waivedNow));
			receiptData = updateRPS(receiptData, allocate, "LPI");
			allocate.setPaidNow(BigDecimal.ZERO);
			allocate.setWaivedNow(BigDecimal.ZERO);
		}
		if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
			rch.setBalAmount(rch.getBalAmount().subtract(paidNow));
		}
		return receiptData;
	}

	public FinReceiptData sepratePenalApportion(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		String taxType = null;
		BigDecimal odDuePaid = BigDecimal.ZERO;
		// LPP Not exits
		if (rch.getLppIdx() < 0) {
			return receiptData;
		}

		BigDecimal balAmount = rch.getBalAmount();
		BigDecimal waivedAmount = BigDecimal.ZERO;

		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		ReceiptAllocationDetail allocate = allocationList.get(rch.getLppIdx());
		if (allocate.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}

		Date valueDate = rch.getValueDate();

		FinRepayHeader rph = null;

		List<FinODDetails> finODDetails = finScheduleData.getFinODDetails();
		FeeType lppFeeType = null;
		for (int i = 0; i < finODDetails.size(); i++) {
			boolean isFullPaid = false;
			rch.setOdIdx(i);
			FinODDetails fod = finODDetails.get(i);
			if (fod.getFinODSchdDate().compareTo(valueDate) > 0) {
				break;
			}
			if (receiptData.isAdjSchedule()) {
				rph = rcdList.get(rcdIdx).getRepayHeader();
				balAmount = rch.getBalAmount();
				waivedAmount = allocate.getWaivedAvailable();
				if (rch.getBalAmount().compareTo(allocate.getPaidAvailable()) > 0) {
					balAmount = allocate.getPaidAvailable();
				}
				if (balAmount.compareTo(BigDecimal.ZERO) <= 0 && waivedAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			} else {
				if ((balAmount).compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}
			BigDecimal odBal = fod.getTotPenaltyBal();
			BigDecimal actualOdBal = fod.getTotPenaltyBal();
			BigDecimal odPayNow = BigDecimal.ZERO;
			BigDecimal odWaiveNow = BigDecimal.ZERO;
			if (odBal.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			// calculating GST for LPP
			if (lppFeeType == null) {
				lppFeeType = feeTypeDAO.getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
			}

			if (lppFeeType != null && lppFeeType.isTaxApplicable()) {
				taxType = lppFeeType.getTaxComponent();
			}

			BigDecimal taxableAmount = odBal;
			BigDecimal tdsAmount = BigDecimal.ZERO;

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
				TaxAmountSplit taxSplit = new TaxAmountSplit();
				taxSplit.setAmount(odBal);
				taxSplit.setTaxType(taxType);
				taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
				odBal = odBal.add(taxSplit.gettGST());
			}

			if (allocate.isTdsReq()) {
				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
					TaxAmountSplit taxSplit = new TaxAmountSplit();
					taxSplit.setAmount(taxableAmount);
					taxSplit.setTaxType(taxType);
					taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
					taxableAmount = odBal.subtract(taxSplit.gettGST());
				}

				tdsAmount = getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
						taxableAmount);
				odBal = odBal.subtract(tdsAmount);

			}

			if (allocate.getWaivedAvailable().compareTo(BigDecimal.ZERO) > 0) {
				if (allocate.getWaivedAvailable().compareTo(odBal) > 0) {
					odWaiveNow = odBal;
					waivedAmount = waivedAmount.subtract(odWaiveNow);
				} else {
					odWaiveNow = allocate.getWaivedAvailable();
					waivedAmount = BigDecimal.ZERO;
				}
			}

			odBal = odBal.subtract(odWaiveNow);

			//allocate.setWaivedAvailable(allocate.getWaivedAvailable().subtract(odWaiveNow));
			if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (balAmount.compareTo(odBal) >= 0) {
					odPayNow = odBal;
					isFullPaid = true;
				} else {
					odPayNow = balAmount;
				}
			}

			balAmount = balAmount.subtract(odPayNow);
			rch.setBalAmount(rch.getBalAmount().subtract(odPayNow));

			if (isFullPaid) {
				updateAllocationWithTds(allocate, odPayNow, odWaiveNow, receiptData.getFinanceDetail(), tdsAmount);
			} else {
				updateAllocationWithTds(allocate, odPayNow, odWaiveNow, receiptData.getFinanceDetail(),
						BigDecimal.ZERO);
			}

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
				if (isFullPaid) {
					odDuePaid = odDuePaid.add(actualOdBal);
				} else {
					odDuePaid = odDuePaid.add(getNetAmountForExclusive(receiptData.getFinanceDetail(), odPayNow,
							FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE));
				}
			} else {
				odDuePaid = odDuePaid.add(allocate.getPaidAmount());
			}

			if (receiptData.isAdjSchedule() && (odPayNow.add(odWaiveNow)).compareTo(BigDecimal.ZERO) > 0) {
				fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().add(odPayNow));
				fod.setTotWaived(fod.getTotWaived().add(odWaiveNow));
				fod.setTotPenaltyBal(fod.getTotPenaltyBal().subtract(odPayNow.add(odWaiveNow)));
				rph.setRepayAmount(rph.getRepayAmount().add(odPayNow));
				rph.setTotalPenalty(rph.getTotalPenalty().add(odPayNow));
				rph.setTotalWaiver(rph.getTotalWaiver().add(odWaiveNow));
				receiptData = updateRPS(receiptData, allocate, "LPP");
				allocate.setPaidNow(BigDecimal.ZERO);
				allocate.setWaivedNow(BigDecimal.ZERO);
			}
		}

		/*
		 * BUG FIX 138534, In Receipt Screen Penalty is showing GST Amount if there no GST configuration for Penalty. In
		 * case of Penalty checking configuration, if there is TaxApplicable then only calculating GST
		 */
		if (lppFeeType != null && lppFeeType.isTaxApplicable()) {
			//always we are taking the inclusive type here because we are doing reverse calculation here
			calAllocationPaidGST(receiptData.getFinanceDetail(), allocate.getTotalPaid(), allocate,
					FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
		}

		return receiptData;
	}

	public FinReceiptData penalApportion(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		// LPP Not exits
		if (rch.getLppIdx() < 0) {
			return receiptData;
		}

		BigDecimal balAmount = BigDecimal.ZERO;
		FeeType lppFeeType = receiptData.getLppFeeType();
		int schdIdx = rch.getSchdIdx();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(schdIdx);

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		ReceiptAllocationDetail allocate = allocationList.get(rch.getLppIdx());

		if (allocate.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}

		FinRepayHeader rph = null;
		if (receiptData.isAdjSchedule()) {
			rph = rch.getReceiptDetails().get(rcdIdx).getRepayHeader();
			balAmount = allocate.getPaidAvailable();
		} else {
			balAmount = rch.getBalAmount();
		}

		BigDecimal penaltyBal = BigDecimal.ZERO;
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;
		boolean isLPPFound = false;
		boolean isCompPaid = false;
		FinODDetails finOd = null;
		List<FinODDetails> finODDetails = finScheduleData.getFinODDetails();
		for (int i = 0; i < finODDetails.size(); i++) {
			FinODDetails fod = finODDetails.get(i);
			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				finOd = finODDetails.get(i);
				rch.setOdIdx(i);
				penaltyBal = fod.getTotPenaltyBal();
				isLPPFound = true;
				break;
			}

			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) > 0) {
				break;
			}
		}

		if (!isLPPFound || penaltyBal.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}
		if (allocate.getWaivedAvailable().compareTo(BigDecimal.ZERO) > 0) {
			if (allocate.getWaivedAvailable().compareTo(penaltyBal) > 0) {
				waivedNow = penaltyBal;
			} else {
				waivedNow = allocate.getWaivedAvailable();
			}
		}
		if (lppFeeType == null && receiptData.getLppFeeType() == null) {
			lppFeeType = feeTypeDAO.getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
			receiptData.setLppFeeType(lppFeeType);
		}
		String taxType = null;

		if (lppFeeType != null && lppFeeType.isTaxApplicable()) {
			taxType = lppFeeType.getTaxComponent();
		}
		penaltyBal = penaltyBal.subtract(waivedNow);
		BigDecimal taxableAmount = penaltyBal;
		BigDecimal tdsAmount = BigDecimal.ZERO;
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(penaltyBal);
			taxSplit.setTaxType(taxType);
			taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
			penaltyBal = penaltyBal.add(taxSplit.gettGST());
		}
		if (allocate.isTdsReq()) {
			if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
				TaxAmountSplit taxSplit = new TaxAmountSplit();
				taxSplit.setAmount(taxableAmount);
				taxSplit.setTaxType(taxType);
				taxSplit = getGST(receiptData.getFinanceDetail(), taxSplit);
				taxableAmount = penaltyBal.subtract(taxSplit.gettGST());
			}

			tdsAmount = getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					taxableAmount);
			penaltyBal = penaltyBal.subtract(tdsAmount);

		}

		// Adjust Paid Amount
		if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(penaltyBal) >= 0) {
				paidNow = penaltyBal;
				isCompPaid = true;
			} else {
				paidNow = balAmount;
			}
		}

		balAmount = balAmount.subtract(paidNow);

		if (isCompPaid) {
			updateAllocationWithTds(allocate, paidNow, waivedNow, receiptData.getFinanceDetail(), tdsAmount);
		} else {
			updateAllocationWithTds(allocate, paidNow, waivedNow, receiptData.getFinanceDetail(), BigDecimal.ZERO);
		}

		if (paidNow.compareTo(BigDecimal.ZERO) > 0 && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			actualOdPaid = actualOdPaid.add(getNetAmountForExclusive(receiptData.getFinanceDetail(),
					paidNow.add(allocate.getTdsPaidNow()), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE));
		}

		finOd.setTotPenaltyPaid(finOd.getTotPenaltyPaid().add(paidNow.add(allocate.getTdsPaidNow())));
		finOd.setTotWaived(finOd.getTotWaived().add(waivedNow));
		finOd.setTotPenaltyBal(finOd.getTotPenaltyBal().subtract(paidNow.add(waivedNow)));

		if (receiptData.isAdjSchedule() && paidNow.add(waivedNow).compareTo(BigDecimal.ZERO) > 0) {
			rph.setRepayAmount(rph.getRepayAmount().add(paidNow));
			rph.setTotalPenalty(rph.getTotalPenalty().add(paidNow));
			rph.setTotalWaiver(rph.getTotalWaiver().add(waivedNow));
			receiptData = updateRPS(receiptData, allocate, "LPP");
			allocate.setPaidNow(BigDecimal.ZERO);
			allocate.setWaivedNow(BigDecimal.ZERO);
		}
		if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
			rch.setBalAmount(rch.getBalAmount().subtract(paidNow));
		}
		return receiptData;

	}

	public FinReceiptData updateRPS(FinReceiptData receiptData, ReceiptAllocationDetail allocation, String updFor) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		int schdIdx = rch.getSchdIdx();
		int odIdx = rch.getOdIdx();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(schdIdx);
		FinRepayHeader rph = rch.getReceiptDetails().get(rcdIdx).getRepayHeader();
		List<RepayScheduleDetail> rpsList = rph.getRepayScheduleDetails();
		RepayScheduleDetail rps = new RepayScheduleDetail();

		Date schdDate = curSchd.getSchDate();
		boolean isRPSFound = false;
		boolean isOd = false;

		if (StringUtils.equals(updFor, "LPI") || StringUtils.equals(updFor, "LPP")) {
			schdDate = finScheduleData.getFinODDetails().get(odIdx).getFinODSchdDate();
			isOd = true;
		}

		// Find schedule Existence in the Repayment Schedule Record
		for (int i = 0; i < rpsList.size(); i++) {
			if (rpsList.get(i).getSchDate().compareTo(schdDate) == 0) {
				rps = rpsList.get(i);
				isRPSFound = true;
				break;
			}
		}

		// If RPS not found set RPS data from schedule record
		if (!isRPSFound) {
			if (isOd) {
				rps = setODRPSData(receiptData, finScheduleData.getFinODDetails().get(odIdx));
			} else {
				rps = setRPSData(receiptData, curSchd);
			}
		}

		// Update RPH, RPS, SCHEDULE
		// RPS
		if (StringUtils.equals(updFor, "PRI")) {
			rps.setPrincipalSchdPayNow(rps.getPrincipalSchdPayNow().add(allocation.getPaidNow()));
			rps.setPriSchdWaivedNow(rps.getPriSchdWaivedNow().add(allocation.getWaivedNow()));
			curSchd.setSchdPriPaid(
					curSchd.getSchdPriPaid().add(allocation.getPaidNow().add(allocation.getWaivedNow())));

		} else if (StringUtils.equals(updFor, "INT")) {
			rps.setProfitSchdPayNow(
					rps.getProfitSchdPayNow().add(allocation.getPaidNow().add(allocation.getTdsPaidNow())));
			rps.setPftSchdWaivedNow(
					rps.getPftSchdWaivedNow().add(allocation.getWaivedNow().add(allocation.getTdsWaivedNow())));
			curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().add(allocation.getPaidNow().add(allocation.getWaivedNow())
					.add(allocation.getTdsPaidNow()).add(allocation.getTdsWaivedNow())));
			rps.setTdsSchdPayNow(allocation.getTdsPaidNow().add(allocation.getTdsWaivedNow()));
			curSchd.setTDSPaid(curSchd.getTDSPaid().add(allocation.getTdsPaidNow().add(allocation.getTdsWaivedNow())));

		} else if (StringUtils.equals(updFor, "LPI")) {
			rps.setLatePftSchdPayNow(allocation.getPaidNow());
			rps.setLatePftSchdWaivedNow(allocation.getWaivedNow());

		} else if (StringUtils.equals(updFor, "LPP")) {
			// if Exclusive type GST is there we have to subtract the gst amount
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocation.getTaxType())) {
				rps.setPenaltyPayNow(
						allocation.getPaidNow().add(allocation.getTdsPaidNow()).subtract(allocation.getPaidGST()));
			} else {
				rps.setPenaltyPayNow(allocation.getPaidNow().add(allocation.getTdsPaidNow()));
			}
			rps.setWaivedAmt(allocation.getWaivedNow().add(allocation.getTdsWaivedNow()));

			//GST Calculations
			if (allocation.getTaxHeader() != null) {
				Cloner cloner = new Cloner();
				TaxHeader taxheader = cloner.deepClone(allocation.getTaxHeader());
				rps.setTaxHeader(taxheader);
			}
		}

		rps.setRepayNet(rps.getProfitSchdPayNow().add(rps.getPrincipalSchdPayNow()).add(rps.getLatePftSchdPayNow())
				.add(rps.getPenaltyPayNow()));
		rps.setRepayBalance(rps.getProfitSchdBal().add(rps.getPrincipalSchdBal()).add(rps.getLatePftSchdBal())
				.add(rps.getPenaltyAmt()));
		rps.setRepayBalance(rps.getRepayBalance().subtract(rps.getRepayNet()));
		rps.setRepayBalance(rps.getRefundMax().subtract(rps.getPftSchdWaivedNow()).subtract(rps.getPriSchdWaivedNow())
				.subtract(rps.getLatePftSchdWaivedNow()).subtract(rps.getWaivedAmt()));
		if (!isRPSFound) {
			rph.getRepayScheduleDetails().add(rps);
		}
		return receiptData;
	}

	public RepayScheduleDetail setRPSData(FinReceiptData receiptData, FinanceScheduleDetail curSchd) {

		RepayScheduleDetail rps = new RepayScheduleDetail();
		rps.setRepayID(1);
		rps.setFinReference(curSchd.getFinReference());
		rps.setSchDate(curSchd.getSchDate());
		rps.setDefSchdDate(curSchd.getDefSchdDate());
		rps.setSchdFor("S");

		rps.setProfitSchd(curSchd.getProfitSchd());
		rps.setProfitSchdPaid(curSchd.getSchdPftPaid());
		rps.setProfitSchdBal(rps.getProfitSchd().subtract(rps.getProfitSchdPaid()));

		// Principal Amount
		rps.setPrincipalSchd(curSchd.getPrincipalSchd());
		rps.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
		rps.setPrincipalSchdBal(rps.getPrincipalSchd().subtract(rps.getPrincipalSchdPaid()));

		rps.setValueDate(receiptData.getReceiptHeader().getValueDate());

		// Late Payment record
		List<FinODDetails> fodList = receiptData.getFinanceDetail().getFinScheduleData().getFinODDetails();
		if (fodList == null) {
			return rps;
		}
		for (int i = 0; i < fodList.size(); i++) {
			FinODDetails fod = fodList.get(i);
			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) != 0) {
				continue;
			}

			rps.setDaysLate(fod.getFinCurODDays());
			rps.setAllowWaiver(fod.isODAllowWaiver());
			rps.setMaxWaiver(fod.getODMaxWaiverPerc());
			rps.setLatePftSchd(fod.getLPIAmt());
			rps.setLatePftSchdPaid(fod.getLPIPaid());
			rps.setLatePftSchdBal(fod.getLPIBal());
			rps.setPenaltyAmt(fod.getTotPenaltyBal());
			break;
		}

		return rps;
	}

	public RepayScheduleDetail setODRPSData(FinReceiptData receiptData, FinODDetails fod) {

		RepayScheduleDetail rps = new RepayScheduleDetail();
		rps.setRepayID(1);
		rps.setFinReference(fod.getFinReference());
		rps.setSchdFor("S");
		rps.setSchDate(fod.getFinODSchdDate());
		rps.setDaysLate(fod.getFinCurODDays());
		rps.setAllowWaiver(fod.isODAllowWaiver());
		rps.setMaxWaiver(fod.getODMaxWaiverPerc());
		rps.setLatePftSchd(fod.getLPIAmt());
		rps.setLatePftSchdPaid(fod.getLPIPaid());
		rps.setLatePftSchdBal(fod.getLPIBal());
		rps.setPenaltyAmt(fod.getTotPenaltyBal());
		return rps;
	}

	/**
	 * Method for Preparing all GST fee amounts based on configurations
	 * 
	 * @param manAdvList
	 * @param financeDetail
	 * @return
	 */
	public FinanceDetail setGSTExecutionMap(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain fm = finScheduleData.getFinanceMain();

		Map<String, Object> dataMap = GSTCalculator.getGSTDataMap(fm.getFinReference());

		finScheduleData.setGstExecutionMap(dataMap);
		Map<String, BigDecimal> taxPercMap = GSTCalculator.getTaxPercentages(dataMap, fm.getFinCcy());

		cgstPerc = taxPercMap.get(RuleConstants.CODE_CGST);
		sgstPerc = taxPercMap.get(RuleConstants.CODE_SGST);
		igstPerc = taxPercMap.get(RuleConstants.CODE_IGST);
		ugstPerc = taxPercMap.get(RuleConstants.CODE_UGST);
		cessPerc = taxPercMap.get(RuleConstants.CODE_CESS);
		tgstPerc = taxPercMap.get(RuleConstants.CODE_TOTAL_GST);

		return financeDetail;
	}

	public FinReceiptData setTotals(FinReceiptData receiptData, int totalCtg) {
		// totalCtg == 0: All Totals
		// totalCtg == 1: Excess and Payable Totals
		// totalCtg == 2: Allocation Totals

		if (totalCtg == 0 || totalCtg == 1) {
			setXcessTotals(receiptData);
		}

		if (totalCtg == 0 || totalCtg == 2) {
			setAllocationTotals(receiptData);
		}

		BigDecimal remainingBal = BigDecimal.ZERO;
		BigDecimal partPayAmount = BigDecimal.ZERO;

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		int receiptCtg = setReceiptCategory(rch.getReceiptPurpose());
		partPayAmount = getPartPaymentAmount(receiptData);
		remainingBal = partPayAmount.subtract(rch.getTotalFees().getPaidAmount());
		rch.setPartPayAmount(remainingBal);
		rch.setBalAmount(remainingBal);

		receiptData.setRemBal(remainingBal);
		receiptData.setTotReceiptAmount(rch.getReceiptAmount());
		return receiptData;
	}

	public FinReceiptData setXcessTotals(FinReceiptData receiptData) {

		if (receiptData.getReceiptHeader().getXcessPayables() == null
				|| receiptData.getReceiptHeader().getXcessPayables().isEmpty()) {
			return receiptData;
		}

		ReceiptAllocationDetail totalXcess = new ReceiptAllocationDetail();
		List<XcessPayables> xcessPayables = receiptData.getReceiptHeader().getXcessPayables();
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);
			totalXcess.setDueAmount(totalXcess.getDueAmount().add(payable.getAmount()));
			totalXcess.setDueGST(totalXcess.getDueGST().add(payable.getGstAmount()));
			totalXcess.setTotalDue(totalXcess.getTotalDue().add(payable.getAvailableAmt()));
			totalXcess.setPaidAmount(
					totalXcess.getPaidAmount().add(payable.getTotPaidNow()).subtract(payable.getPaidGST()));
			totalXcess.setPaidGST(totalXcess.getPaidGST().add(payable.getPaidGST()));
			totalXcess.setPaidCGST(totalXcess.getPaidCGST().add(payable.getPaidCGST()));
			totalXcess.setPaidSGST(totalXcess.getPaidSGST().add(payable.getPaidSGST()));
			totalXcess.setPaidIGST(totalXcess.getPaidIGST().add(payable.getPaidIGST()));
			totalXcess.setPaidUGST(totalXcess.getPaidUGST().add(payable.getPaidUGST()));
			totalXcess.setPaidCESS(totalXcess.getPaidCESS().add(payable.getPaidCESS()));
			totalXcess.setTotalPaid(totalXcess.getTotalPaid().add(payable.getTotPaidNow()));
			totalXcess.setBalance(totalXcess.getTotalDue().subtract(totalXcess.getTotalPaid()));
		}

		receiptData.getReceiptHeader().setTotalXcess(totalXcess);
		return receiptData;
	}

	public FinReceiptData setEmi(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocList = rch.getAllocations();
		if (rch.getEmiIdx() < 0) {
			return receiptData;
		}
		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal priWaived = BigDecimal.ZERO;
		BigDecimal npftPaid = BigDecimal.ZERO;
		BigDecimal npftWaived = BigDecimal.ZERO;
		if (rch.getPriIdx() >= 0) {
			priPaid = allocList.get(rch.getPriIdx()).getPaidAmount();
			priWaived = allocList.get(rch.getPriIdx()).getWaivedAmount();
		}
		if (rch.getPftIdx() >= 0) {
			npftPaid = allocList.get(rch.getPftIdx()).getPaidAmount();
			npftWaived = allocList.get(rch.getPftIdx()).getWaivedAmount();
		}
		allocList.get(rch.getEmiIdx()).setTotalPaid(priPaid.add(npftPaid));
		allocList.get(rch.getEmiIdx()).setPaidAmount(priPaid.add(npftPaid));
		allocList.get(rch.getEmiIdx()).setWaivedAmount(priWaived.add(npftWaived));
		return receiptData;
	}

	public FinReceiptData setAllocationTotals(FinReceiptData receiptData) {
		// No Allocations Found. It happens when no Dues in schedule payment
		if (receiptData.getReceiptHeader().getAllocations() == null
				|| receiptData.getReceiptHeader().getAllocations().isEmpty()) {
			return receiptData;
		}

		// To set summary by due categories
		ReceiptAllocationDetail totalPastDues = new ReceiptAllocationDetail();
		ReceiptAllocationDetail totalAdvises = new ReceiptAllocationDetail();
		ReceiptAllocationDetail totalFees = new ReceiptAllocationDetail();
		ReceiptAllocationDetail totalBounces = new ReceiptAllocationDetail();

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		List<ReceiptAllocationDetail> allocSummary = new ArrayList<ReceiptAllocationDetail>(1);
		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail allocation = allocationList.get(i);
			boolean exclusiveTax = false;
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocation.getTaxType())) {
				exclusiveTax = true;
			}
			allocation.setBalance(allocation.getTotalDue().subtract(allocation.getPaidAmount())
					.subtract(allocation.getWaivedAmount()));

			allocSummary = setAllocationSummary(allocSummary, allocation);

			if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_PFT)
					|| StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
				continue;
			}

			if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)) {
				totalBounces.setDueAmount(totalBounces.getDueAmount().add(allocation.getDueAmount()));
				totalBounces.setDueGST(totalBounces.getDueGST().add(allocation.getDueGST()));
				totalBounces.setTotalDue(totalBounces.getTotalDue().add(allocation.getTotalDue()));
				totalBounces.setPaidAmount(totalBounces.getPaidAmount().add(allocation.getPaidAmount()));
				totalBounces.setTotalPaid(totalBounces.getTotalPaid().add(allocation.getTotalPaid()));
				totalBounces.setWaivedAmount(totalBounces.getWaivedAmount().add(allocation.getWaivedAmount()));
				totalBounces.setWaivedAvailable(totalBounces.getWaivedAvailable().add(allocation.getWaivedAvailable()));
				totalBounces.setBalance(totalBounces.getBalance().add(allocation.getBalance()));
				totalBounces.setTdsDue(totalBounces.getTdsDue().add(allocation.getTdsDue()));
				totalBounces.setTdsPaid(totalBounces.getTdsPaid().add(allocation.getTdsPaid()));

				// Paid GST Amounts
				totalBounces.setPaidCGST(totalBounces.getPaidCGST().add(allocation.getPaidCGST()));
				totalBounces.setPaidSGST(totalBounces.getPaidSGST().add(allocation.getPaidSGST()));
				totalBounces.setPaidIGST(totalBounces.getPaidIGST().add(allocation.getPaidIGST()));
				totalBounces.setPaidUGST(totalBounces.getPaidUGST().add(allocation.getPaidUGST()));
				totalBounces.setPaidCESS(totalBounces.getPaidCESS().add(allocation.getPaidCESS()));
				totalBounces.setPaidGST(totalBounces.getPaidGST().add(allocation.getPaidGST()));

				// Waiver GST Amounts
				totalBounces.setWaivedCGST(totalBounces.getWaivedCGST().add(allocation.getWaivedCGST()));
				totalBounces.setWaivedSGST(totalBounces.getWaivedSGST().add(allocation.getWaivedSGST()));
				totalBounces.setWaivedIGST(totalBounces.getWaivedIGST().add(allocation.getWaivedIGST()));
				totalBounces.setWaivedUGST(totalBounces.getWaivedUGST().add(allocation.getWaivedUGST()));
				totalBounces.setWaivedCESS(totalBounces.getWaivedCESS().add(allocation.getWaivedCESS()));

				if (exclusiveTax) {
					totalBounces.setWaivedGST(totalBounces.getWaivedGST().add(allocation.getWaivedGST()));
				}

				//Prepare CESS Tax Details
				prepareTaxDetails(totalBounces, allocation);

				continue;
			}

			if (allocation.getAllocationTo() < 0) {
				totalFees.setDueAmount(totalFees.getDueAmount().add(allocation.getDueAmount()));
				totalFees.setDueGST(totalFees.getDueGST().add(allocation.getDueGST()));
				totalFees.setTotalDue(totalFees.getTotalDue().add(allocation.getTotalDue()));
				totalFees.setPaidAmount(totalFees.getPaidAmount().add(allocation.getPaidAmount()));
				totalFees.setTotalPaid(totalFees.getTotalPaid().add(allocation.getTotalPaid()));
				totalFees.setWaivedAmount(totalFees.getWaivedAmount().add(allocation.getWaivedAmount()));
				totalFees.setWaivedAvailable(totalFees.getWaivedAvailable().add(allocation.getWaivedAvailable()));
				totalFees.setBalance(totalFees.getBalance().add(allocation.getBalance()));
				totalFees.setTdsDue(totalFees.getTdsDue().add(allocation.getTdsDue()));
				totalFees.setTdsPaid(totalFees.getTdsPaid().add(allocation.getTdsPaid()));

				// Paid GST Amounts
				totalFees.setPaidCGST(totalFees.getPaidCGST().add(allocation.getPaidCGST()));
				totalFees.setPaidSGST(totalFees.getPaidSGST().add(allocation.getPaidSGST()));
				totalFees.setPaidIGST(totalFees.getPaidIGST().add(allocation.getPaidIGST()));
				totalFees.setPaidUGST(totalFees.getPaidUGST().add(allocation.getPaidUGST()));
				totalFees.setPaidCESS(totalFees.getPaidCESS().add(allocation.getPaidCESS()));
				totalFees.setPaidGST(totalFees.getPaidGST().add(allocation.getPaidGST()));

				// Waiver GST Amounts
				totalFees.setWaivedCGST(totalFees.getWaivedCGST().add(allocation.getWaivedCGST()));
				totalFees.setWaivedSGST(totalFees.getWaivedSGST().add(allocation.getWaivedSGST()));
				totalFees.setWaivedIGST(totalFees.getWaivedIGST().add(allocation.getWaivedIGST()));
				totalFees.setWaivedUGST(totalFees.getWaivedUGST().add(allocation.getWaivedUGST()));
				totalFees.setWaivedCESS(totalFees.getWaivedCESS().add(allocation.getWaivedCESS()));
				if (exclusiveTax && allocation.getWaivedGST().compareTo(BigDecimal.ZERO) > 0) {
					totalFees.setWaivedGST(totalFees.getWaivedGST().add(allocation.getWaivedGST()));
				}
				//Prepare CESS Tax Details
				prepareTaxDetails(totalFees, allocation);

			} else if (allocation.getAllocationTo() == 0) {
				totalPastDues.setDueAmount(totalPastDues.getDueAmount().add(allocation.getDueAmount()));
				totalPastDues.setDueGST(totalPastDues.getDueGST().add(allocation.getDueGST()));
				totalPastDues.setTotalDue(totalPastDues.getTotalDue().add(allocation.getTotalDue()));
				totalPastDues.setPaidAmount(totalPastDues.getPaidAmount().add(allocation.getPaidAmount()));
				totalPastDues.setTotalPaid(totalPastDues.getTotalPaid().add(allocation.getTotalPaid()));
				totalPastDues.setWaivedAmount(totalPastDues.getWaivedAmount().add(allocation.getWaivedAmount()));
				totalPastDues
						.setWaivedAvailable(totalPastDues.getWaivedAvailable().add(allocation.getWaivedAvailable()));
				totalPastDues.setBalance(totalPastDues.getBalance().add(allocation.getBalance()));
				totalPastDues.setTdsDue(totalPastDues.getTdsDue().add(allocation.getTdsDue()));
				totalPastDues.setTdsPaid(totalPastDues.getTdsPaid().add(allocation.getTdsPaid()));

				// Paid GST Amounts
				totalPastDues.setPaidCGST(totalPastDues.getPaidCGST().add(allocation.getPaidCGST()));
				totalPastDues.setPaidSGST(totalPastDues.getPaidSGST().add(allocation.getPaidSGST()));
				totalPastDues.setPaidIGST(totalPastDues.getPaidIGST().add(allocation.getPaidIGST()));
				totalPastDues.setPaidUGST(totalPastDues.getPaidUGST().add(allocation.getPaidUGST()));
				totalPastDues.setPaidGST(totalPastDues.getPaidGST().add(allocation.getPaidGST()));
				totalPastDues.setPaidCESS(totalPastDues.getPaidCESS().add(allocation.getPaidCESS()));

				// Waiver GST Amounts
				totalPastDues.setWaivedCGST(totalPastDues.getWaivedCGST().add(allocation.getWaivedCGST()));
				totalPastDues.setWaivedSGST(totalPastDues.getWaivedSGST().add(allocation.getWaivedSGST()));
				totalPastDues.setWaivedIGST(totalPastDues.getWaivedIGST().add(allocation.getWaivedIGST()));
				totalPastDues.setWaivedUGST(totalPastDues.getWaivedUGST().add(allocation.getWaivedUGST()));
				totalPastDues.setWaivedCESS(totalPastDues.getWaivedCESS().add(allocation.getWaivedCESS()));
				if (exclusiveTax) {
					totalPastDues.setWaivedGST(totalPastDues.getWaivedGST().add(allocation.getWaivedGST()));
				}
				//Prepare CESS Tax Details
				prepareTaxDetails(totalPastDues, allocation);

			} else if (allocation.getAllocationTo() > 0) {
				totalAdvises.setDueAmount(totalAdvises.getDueAmount().add(allocation.getDueAmount()));
				totalAdvises.setDueGST(totalAdvises.getDueGST().add(allocation.getDueGST()));
				totalAdvises.setTotalDue(totalAdvises.getTotalDue().add(allocation.getTotalDue()));
				totalAdvises.setPaidAmount(totalAdvises.getPaidAmount().add(allocation.getPaidAmount()));
				totalAdvises.setTotalPaid(totalAdvises.getTotalPaid().add(allocation.getTotalPaid()));
				totalAdvises.setWaivedAmount(totalAdvises.getWaivedAmount().add(allocation.getWaivedAmount()));
				totalAdvises.setWaivedAvailable(totalAdvises.getWaivedAvailable().add(allocation.getWaivedAvailable()));
				totalAdvises.setBalance(totalAdvises.getBalance().add(allocation.getBalance()));
				totalAdvises.setTdsDue(totalPastDues.getTdsDue().add(allocation.getTdsDue()));
				totalAdvises.setTdsPaid(totalPastDues.getTdsPaid().add(allocation.getTdsPaid()));

				// Paid GST Amounts
				totalAdvises.setPaidCGST(totalAdvises.getPaidCGST().add(allocation.getPaidCGST()));
				totalAdvises.setPaidSGST(totalAdvises.getPaidSGST().add(allocation.getPaidSGST()));
				totalAdvises.setPaidIGST(totalAdvises.getPaidIGST().add(allocation.getPaidIGST()));
				totalAdvises.setPaidUGST(totalAdvises.getPaidUGST().add(allocation.getPaidUGST()));
				totalAdvises.setPaidCESS(totalAdvises.getPaidCESS().add(allocation.getPaidCESS()));
				totalAdvises.setPaidGST(totalAdvises.getPaidGST().add(allocation.getPaidGST()));

				// Waiver GST Amounts
				totalAdvises.setWaivedCGST(totalAdvises.getWaivedCGST().add(allocation.getWaivedCGST()));
				totalAdvises.setWaivedSGST(totalAdvises.getWaivedSGST().add(allocation.getWaivedSGST()));
				totalAdvises.setWaivedIGST(totalAdvises.getWaivedIGST().add(allocation.getWaivedIGST()));
				totalAdvises.setWaivedUGST(totalAdvises.getWaivedUGST().add(allocation.getWaivedUGST()));
				totalAdvises.setWaivedCESS(totalAdvises.getWaivedCESS().add(allocation.getWaivedCESS()));

				//Prepare CESS Tax Details
				prepareTaxDetails(totalAdvises, allocation);

				if (exclusiveTax) {
					totalAdvises.setWaivedGST(totalAdvises.getWaivedGST().add(allocation.getWaivedGST()));
				}
			}
		}

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setTotalPastDues(totalPastDues);
		rch.setTotalRcvAdvises(totalAdvises);
		rch.setTotalFees(totalFees);
		rch.setTotFeeAmount(totalFees.getTotalDue());
		rch.setTotalBounces(totalBounces);
		rch.setAllocationsSummary(allocSummary);
		return receiptData;
	}

	/**
	 * Prepare CESS Tax Details
	 */
	private void prepareTaxDetails(ReceiptAllocationDetail totalAllocation, ReceiptAllocationDetail allocation) {
		TaxHeader header = allocation.getTaxHeader();
		if (header != null && CollectionUtils.isNotEmpty(header.getTaxDetails())) {
			TaxHeader taxHeader = new TaxHeader();
			totalAllocation.setTaxHeader(taxHeader);
			List<Taxes> taxDetails = header.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxDetail : taxDetails) {
					Cloner cloner = new Cloner();
					Taxes taxes = cloner.deepClone(taxDetail);
					taxes.setId(Long.MIN_VALUE);
					taxHeader.getTaxDetails().add(taxes);
				}
			}
		}
	}

	public List<ReceiptAllocationDetail> setAllocationSummary(List<ReceiptAllocationDetail> allocSummary,
			ReceiptAllocationDetail allocation) {
		Cloner cloner = new Cloner();
		ReceiptAllocationDetail tempAlloc = cloner.deepClone(allocation);

		String allocType = tempAlloc.getAllocationType();
		String feeTypeCode = tempAlloc.getFeeTypeCode();
		int idx = -1;
		for (int i = 0; i < allocSummary.size(); i++) {
			// If requested allocation type already found in the summary. break
			// with index
			if (StringUtils.equals(allocSummary.get(i).getAllocationType(), allocType)
					&& StringUtils.equals(allocSummary.get(i).getFeeTypeCode(), feeTypeCode)) {
				idx = i;
				break;
			}
		}

		// If allocation type not found in the summary add new summary and leave
		if (idx < 0) {
			allocSummary.add(tempAlloc);
			tempAlloc.getSubList().add(allocation);
			return allocSummary;
		}

		// Allocation already found in summary then add up to the summary
		ReceiptAllocationDetail sumAlloc = allocSummary.get(idx);
		sumAlloc.setDueAmount(sumAlloc.getDueAmount().add(tempAlloc.getDueAmount()));
		sumAlloc.setTotRecv(sumAlloc.getTotRecv().add(tempAlloc.getTotRecv()));
		sumAlloc.setDueGST(sumAlloc.getDueGST().add(tempAlloc.getDueGST()));
		sumAlloc.setTotalDue(sumAlloc.getTotalDue().add(tempAlloc.getTotalDue()));
		sumAlloc.setPaidAmount(sumAlloc.getPaidAmount().add(tempAlloc.getPaidAmount()));
		sumAlloc.setTotalPaid(sumAlloc.getTotalPaid().add(tempAlloc.getTotalPaid()));
		sumAlloc.setWaivedAmount(sumAlloc.getWaivedAmount().add(tempAlloc.getWaivedAmount()));
		sumAlloc.setTdsDue(sumAlloc.getTdsDue().add(tempAlloc.getTdsDue()));
		sumAlloc.setTdsPaid(sumAlloc.getTdsPaid().add(tempAlloc.getTdsPaid()));

		// Paid GST Amounts
		sumAlloc.setPaidCGST(sumAlloc.getPaidCGST().add(tempAlloc.getPaidCGST()));
		sumAlloc.setPaidSGST(sumAlloc.getPaidSGST().add(tempAlloc.getPaidSGST()));
		sumAlloc.setPaidUGST(sumAlloc.getPaidUGST().add(tempAlloc.getPaidUGST()));
		sumAlloc.setPaidIGST(sumAlloc.getPaidIGST().add(tempAlloc.getPaidIGST()));
		sumAlloc.setPaidCESS(sumAlloc.getPaidCESS().add(tempAlloc.getPaidCESS()));
		sumAlloc.setPaidGST(sumAlloc.getPaidGST().add(tempAlloc.getPaidGST()));

		// Waiver GST Amounts
		sumAlloc.setWaivedCGST(sumAlloc.getWaivedCGST().add(tempAlloc.getWaivedCGST()));
		sumAlloc.setWaivedSGST(sumAlloc.getWaivedSGST().add(tempAlloc.getWaivedSGST()));
		sumAlloc.setWaivedUGST(sumAlloc.getWaivedUGST().add(tempAlloc.getWaivedUGST()));
		sumAlloc.setWaivedIGST(sumAlloc.getWaivedIGST().add(tempAlloc.getWaivedIGST()));
		sumAlloc.setWaivedGST(sumAlloc.getWaivedGST().add(tempAlloc.getWaivedGST()));
		sumAlloc.setWaivedCESS(sumAlloc.getWaivedCESS().add(tempAlloc.getWaivedCESS()));

		// In Process, Current Dues and Balances
		sumAlloc.setInProcess(sumAlloc.getInProcess().add(tempAlloc.getInProcess()));
		sumAlloc.setBalance(sumAlloc.getTotalDue().subtract(sumAlloc.getPaidAmount().add(sumAlloc.getWaivedAmount())));

		// Un wanted fields for summary. can be deleted if not useful after
		// thourough confirmation
		sumAlloc.setWaivedAvailable(sumAlloc.getWaivedAvailable().add(tempAlloc.getWaivedAvailable()));
		sumAlloc.setPaidAvailable(sumAlloc.getPaidAvailable().add(tempAlloc.getPaidAvailable()));
		sumAlloc.setPaidNow(sumAlloc.getPaidNow().add(tempAlloc.getPaidNow()));
		sumAlloc.setWaivedNow(sumAlloc.getWaivedNow().add(tempAlloc.getWaivedNow()));
		sumAlloc.getSubList().add(allocation);

		sumAlloc.setSubListAvailable(true);
		return allocSummary;
	}

	public FinReceiptData splitAllocSummary(FinReceiptData receiptData, int idx) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		ReceiptAllocationDetail sumAlloc = rch.getAllocationsSummary().get(idx);
		BigDecimal paidForAdjustment = sumAlloc.getPaidAmount();
		BigDecimal waivedForAdjustment = sumAlloc.getWaivedAmount();
		String allocType = sumAlloc.getAllocationType();
		String feeTypeCode = sumAlloc.getFeeTypeCode();
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> radList = rch.getAllocations();

		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail rad = radList.get(i);
			if (!StringUtils.equals(rad.getAllocationType(), allocType)
					|| !StringUtils.equals(rad.getFeeTypeCode(), feeTypeCode)) {
				continue;
			}
			BigDecimal due = rad.getTotalDue();
			if (waivedForAdjustment.compareTo(BigDecimal.ZERO) > 0) {
				if (waivedForAdjustment.compareTo(due) >= 0) {
					waivedNow = rad.getTotalDue();
					rad.setWaivedAmount(waivedNow);
					waivedForAdjustment = waivedForAdjustment.subtract(waivedNow);
				} else {
					waivedNow = waivedForAdjustment;
					rad.setWaivedAmount(waivedNow);
					waivedForAdjustment = BigDecimal.ZERO;
				}
				due = due.subtract(waivedNow);
			}
			if (due.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			if (paidForAdjustment.compareTo(due) >= 0) {
				paidNow = due;
				rad.setTotalPaid(paidNow);
				rad.setPaidAmount(paidNow);

				calAllocationGST(receiptData.getFinanceDetail(), paidNow, rad,
						FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

				paidForAdjustment = paidForAdjustment.subtract(paidNow);
			} else {
				paidNow = paidForAdjustment;
				rad.setTotalPaid(paidNow);
				rad.setPaidAmount(paidNow);

				calAllocationGST(receiptData.getFinanceDetail(), paidNow, rad,
						FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

				paidForAdjustment = BigDecimal.ZERO;

			}
		}
		rch.setAllocations(radList);
		return receiptData;
	}

	public FinReceiptData splitNetAllocSummary(FinReceiptData receiptData, int idx) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		ReceiptAllocationDetail sumAlloc = rch.getAllocationsSummary().get(idx);
		BigDecimal paidForAdjustment = sumAlloc.getPaidAmount();
		BigDecimal waivedForAdjustment = sumAlloc.getWaivedAmount();
		String allocType = sumAlloc.getAllocationType();
		String feeTypeCode = sumAlloc.getFeeTypeCode();
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> radList = rch.getAllocations();

		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail rad = radList.get(i);
			if (!StringUtils.equals(rad.getAllocationType(), allocType)
					|| !StringUtils.equals(rad.getFeeTypeCode(), feeTypeCode)) {
				continue;
			}
			BigDecimal due = rad.getTotalDue();
			if (waivedForAdjustment.compareTo(BigDecimal.ZERO) > 0) {
				if (waivedForAdjustment.compareTo(due) >= 0) {
					waivedNow = rad.getTotalDue();
					rad.setWaivedAmount(waivedNow);
					waivedForAdjustment = waivedForAdjustment.subtract(waivedNow);
				} else {
					waivedNow = waivedForAdjustment;
					rad.setWaivedAmount(waivedNow);
					waivedForAdjustment = BigDecimal.ZERO;
				}
				due = due.subtract(waivedNow);
			}
			if (due.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			if (paidForAdjustment.compareTo(due) >= 0) {
				paidNow = due;
				rad.setTotalPaid(paidNow);
				rad.setPaidAmount(paidNow);

				BigDecimal totalPaid = getPaidAmount(rad, paidNow);

				calAllocationGST(receiptData.getFinanceDetail(), paidNow, rad,
						FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);

				BigDecimal tdsPaidNow = BigDecimal.ZERO;
				if (rad.isTdsReq()) {
					tdsPaidNow = getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
							totalPaid);
					rad.setTdsPaid(tdsPaidNow);
					rad.setTotalPaid(paidNow.add(tdsPaidNow));
				}

				paidForAdjustment = paidForAdjustment.subtract(paidNow);
			} else {
				paidNow = paidForAdjustment;
				rad.setTotalPaid(paidNow);
				rad.setPaidAmount(paidNow);

				BigDecimal totalPaid = getPaidAmount(rad, paidNow);

				calAllocationGST(receiptData.getFinanceDetail(), paidNow, rad,
						FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);

				BigDecimal tdsPaidNow = BigDecimal.ZERO;
				if (rad.isTdsReq()) {
					tdsPaidNow = getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
							totalPaid);
					rad.setTdsPaid(tdsPaidNow);
					rad.setTotalPaid(paidNow.add(tdsPaidNow));
				}

				paidForAdjustment = BigDecimal.ZERO;

			}
		}
		rch.setAllocations(radList);
		return receiptData;
	}

	/**
	 * GST Calculation for Allocation
	 * 
	 * @param financeDetail
	 * @param paidNow
	 * @param allocation
	 */
	public void calAllocationGST(FinanceDetail financeDetail, BigDecimal paidNow, ReceiptAllocationDetail allocation,
			String taxType) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(allocation.getTaxType())) {
			// Set the GST Paid to allocations
			calAllocationPaidGST(financeDetail, paidNow, allocation, taxType);
			// Set the GST Waiver to allocations
			calAllocationWaiverGST(financeDetail, allocation.getWaivedAmount(), allocation);
		}

		logger.debug(Literal.LEAVING);
	}

	public void calAllocationPaidGST(FinanceDetail financeDetail, BigDecimal paidAmount,
			ReceiptAllocationDetail allocation, String taxType) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(taxType)) {

			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(paidAmount);
			taxSplit.setTaxType(taxType);
			taxSplit = getGST(financeDetail, taxSplit);

			// Set the GST to allocations
			allocation.setPaidCGST(allocation.getPaidCGST().add(taxSplit.getcGST()));
			allocation.setPaidSGST(allocation.getPaidSGST().add(taxSplit.getsGST()));
			allocation.setPaidUGST(allocation.getPaidUGST().add(taxSplit.getuGST()));
			allocation.setPaidIGST(allocation.getPaidIGST().add(taxSplit.getiGST()));
			allocation.setPaidCESS(allocation.getPaidCESS().add(taxSplit.getCess()));
			allocation.setPaidGST(allocation.getPaidGST().add(taxSplit.gettGST()));

			if (allocation.getTaxHeader() != null
					&& CollectionUtils.isNotEmpty(allocation.getTaxHeader().getTaxDetails())) {
				for (Taxes tax : allocation.getTaxHeader().getTaxDetails()) {
					if (RuleConstants.CODE_CGST.equals(tax.getTaxType())) {
						tax.setPaidTax(taxSplit.getcGST());
					} else if (RuleConstants.CODE_SGST.equals(tax.getTaxType())) {
						tax.setPaidTax(taxSplit.getsGST());
					} else if (RuleConstants.CODE_UGST.equals(tax.getTaxType())) {
						tax.setPaidTax(taxSplit.getuGST());
					} else if (RuleConstants.CODE_IGST.equals(tax.getTaxType())) {
						tax.setPaidTax(taxSplit.getiGST());
					} else if (RuleConstants.CODE_CESS.equals(tax.getTaxType())) {
						tax.setPaidTax(taxSplit.getCess());
					}
					tax.setRemFeeTax(tax.getNetTax().subtract(tax.getPaidTax()));
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public BigDecimal getNetAmountForExclusive(FinanceDetail financeDetail, BigDecimal paidAmount, String taxType) {
		logger.debug(Literal.ENTERING);
		BigDecimal netAmount = paidAmount;
		if (StringUtils.isNotBlank(taxType)) {
			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(paidAmount);
			taxSplit.setTaxType(taxType);
			taxSplit = getGST(financeDetail, taxSplit);

			// Set the GST to allocations

			BigDecimal tgst = taxSplit.gettGST();
			netAmount = netAmount.subtract(tgst);
		}

		logger.debug(Literal.LEAVING);
		return netAmount;
	}

	/**
	 * Calculate the GST for Waivers
	 * 
	 * @param financeDetail
	 * @param waivedAmount
	 * @param allocation
	 */
	public void calAllocationWaiverGST(FinanceDetail financeDetail, BigDecimal waivedAmount,
			ReceiptAllocationDetail allocation) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(allocation.getTaxType())) {
			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(waivedAmount);
			taxSplit.setTaxType(allocation.getTaxType());
			taxSplit = getGST(financeDetail, taxSplit);

			// Set the GST to allocations
			allocation.setWaivedCGST(taxSplit.getcGST());
			allocation.setWaivedSGST(taxSplit.getsGST());
			allocation.setWaivedIGST(taxSplit.getiGST());
			allocation.setWaivedUGST(taxSplit.getuGST());
			allocation.setWaivedCESS(taxSplit.getCess());
			allocation.setWaivedGST(taxSplit.gettGST());

			if (allocation.getTaxHeader() != null
					&& CollectionUtils.isNotEmpty(allocation.getTaxHeader().getTaxDetails())) {
				for (Taxes tax : allocation.getTaxHeader().getTaxDetails()) {
					if (RuleConstants.CODE_CGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getcGST());
					} else if (RuleConstants.CODE_SGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getsGST());
					} else if (RuleConstants.CODE_IGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getiGST());
					} else if (RuleConstants.CODE_UGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getuGST());
					} else if (RuleConstants.CODE_CESS.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getCess());
					}
					tax.setNetTax(tax.getActualTax().subtract(tax.getWaivedTax()));
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public BigDecimal findInProcAllocAmount(List<ReceiptAllocationDetail> inProcRadList, String allocType,
			long allacationTo) {
		BigDecimal inProcAmount = BigDecimal.ZERO;
		if (inProcRadList == null) {
			return inProcAmount;
		}

		if (inProcRadList.isEmpty()) {
			return inProcAmount;
		}

		for (int i = 0; i < inProcRadList.size(); i++) {
			ReceiptAllocationDetail inProcRad = inProcRadList.get(i);
			if (StringUtils.equals(inProcRad.getAllocationType(), allocType)
					&& !RepayConstants.ALLOCATION_FEE.equals(inProcRad.getAllocationType())
					&& allacationTo == inProcRad.getAllocationTo()) {
				inProcAmount = inProcRad.getPaidAmount().add(inProcRad.getWaivedAmount());
				break;
			}
		}

		return inProcAmount;
	}

	public BigDecimal findInProcGstAmount(List<ReceiptAllocationDetail> inProcRadList, String allocType,
			long allacationTo) {
		BigDecimal inProcAmount = BigDecimal.ZERO;
		if (inProcRadList == null) {
			return inProcAmount;
		}

		if (inProcRadList.isEmpty()) {
			return inProcAmount;
		}

		for (int i = 0; i < inProcRadList.size(); i++) {
			ReceiptAllocationDetail inProcRad = inProcRadList.get(i);
			if (StringUtils.equals(inProcRad.getAllocationType(), allocType)
					&& allacationTo == inProcRad.getAllocationTo()) {
				inProcAmount = inProcRad.getPaidGST();
				break;
			}
		}

		return inProcAmount;
	}

	public FinReceiptData removeUnwantedManAloc(FinReceiptData receiptData) {
		List<ReceiptAllocationDetail> inProcRadList = receiptData.getInProcRadList();
		if (inProcRadList == null) {
			return receiptData;
		}

		if (inProcRadList.isEmpty()) {
			return receiptData;
		}

		for (int i = 0; i < inProcRadList.size(); i++) {
			ReceiptAllocationDetail inProcRad = inProcRadList.get(i);
			if (StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_PFT)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_TDS)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_NPFT)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_PRI)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_FUT_TDS)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_FUT_PFT)) {
				inProcRadList.remove(i);
				i = i - 1;
				continue;
			}
		}

		return receiptData;
	}

	// Prepare Allocation List
	public List<ReceiptAllocationDetail> prepareAlocList(FinScheduleData scheduleData) {
		logger.debug("Starting");
		List<ReceiptAllocationDetail> receiptAlocList = new ArrayList<>();
		String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();

		char[] rpyOrder = repayHierarchy.toCharArray();
		ReceiptAllocationDetail ad = null;

		int seqID = 1;
		for (int i = 0; i < rpyOrder.length; i++) {
			char repayTo = rpyOrder[i];
			ad = new ReceiptAllocationDetail();

			// Add Principal Record
			if (repayTo == RepayConstants.REPAY_PRINCIPAL) {
				ad.setAllocationType(RepayConstants.ALLOCATION_PRI);
				ad.setAllocationID(seqID);
				seqID = seqID + 1;
				receiptAlocList.add(ad);
				continue;
			}

			// Add Interest
			if (repayTo == RepayConstants.REPAY_PROFIT) {
				String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
				char[] pftPayOrder = profit.toCharArray();

				for (int j = 0; j < pftPayOrder.length; j++) {
					ad = new ReceiptAllocationDetail();
					char pftTo = pftPayOrder[j];
					if (pftTo == RepayConstants.REPAY_PROFIT) {
						ad.setAllocationType(RepayConstants.ALLOCATION_PFT);
						ad.setAllocationID(seqID);
						seqID = seqID + 1;
						receiptAlocList.add(ad);
					} else {
						ad.setAllocationType(RepayConstants.ALLOCATION_LPFT);
						ad.setAllocationID(seqID);
						seqID = seqID + 1;
						receiptAlocList.add(ad);
					}

					continue;
				}

				continue;
			}

			// Overdue and Bounce Charges
			if (repayTo == RepayConstants.REPAY_PENALTY) {
				ad.setAllocationType(RepayConstants.ALLOCATION_ODC);
				ad.setAllocationID(seqID);
				seqID = seqID + 1;
				receiptAlocList.add(ad);
				continue;
			}

			// Other Fee and Charges
			if (repayTo == RepayConstants.REPAY_OTHERS) {

				List<ManualAdvise> advises = manualAdviseDAO.getManualAdviseByRef(
						scheduleData.getFinanceMain().getFinReference(), FinanceConstants.MANUAL_ADVISE_RECEIVABLE,
						"_AView");
				if (advises == null || advises.isEmpty()) {
					continue;
				}

				BigDecimal bounceAmt = BigDecimal.ZERO;
				for (int j = 0; j < advises.size(); j++) {
					ManualAdvise advise = advises.get(j);
					BigDecimal adviseBal = advise.getAdviseAmount().subtract(advise.getPaidAmount())
							.subtract(advise.getWaivedAmount());

					// Adding Advise Details to Map
					if (adviseBal.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					if (advise.getBounceID() > 0) {
						bounceAmt = bounceAmt.add(adviseBal);
						continue;
					}
					ad = new ReceiptAllocationDetail();
					ad.setAllocationTo(advise.getAdviseID());
					ad.setAllocationType(RepayConstants.ALLOCATION_MANADV);
					ad.setTypeDesc(advise.getFeeTypeDesc());
					ad.setDueAmount(advise.getAdviseAmount().subtract(advise.getPaidAmount())
							.subtract(advise.getWaivedAmount()));
					ad.setAllocationID(seqID);
					seqID = seqID + 1;
					receiptAlocList.add(ad);
				}

				// Bounce charges
				if (bounceAmt.compareTo(BigDecimal.ZERO) > 0) {
					ad = new ReceiptAllocationDetail();
					ad.setAllocationType(RepayConstants.ALLOCATION_BOUNCE);
					ad.setDueAmount(bounceAmt);
					ad.setAllocationID(seqID);
					receiptAlocList.add(ad);
				}
			}
		}

		logger.debug("Leaving");
		return receiptAlocList;

	}

	public BigDecimal getPftAmount(FinScheduleData finScheduleData, BigDecimal allocAmount) {

		BigDecimal pftAmount = BigDecimal.ZERO;

		List<FinanceScheduleDetail> scheduleDetails = finScheduleData.getFinanceScheduleDetails();

		for (int i = 1; i < scheduleDetails.size(); i++) {

			if (allocAmount.compareTo(BigDecimal.ZERO) > 0) {

				FinanceScheduleDetail curSchd = scheduleDetails.get(i);

				// TDS Calculation, if Applicable
				BigDecimal tdsMultiplier = BigDecimal.ONE;
				BigDecimal tdsPercentage = BigDecimal.ZERO;
				if (curSchd.isTDSApplicable()) {
					tdsPercentage = new BigDecimal(
							SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());

					if (tdsPercentage.compareTo(BigDecimal.ZERO) > 0) {
						tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPercentage), 20,
								RoundingMode.HALF_DOWN);
					}
				}

				BigDecimal unpaidPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
				BigDecimal tdsAmount = BigDecimal.ZERO;
				if (curSchd.isTDSApplicable()) {
					tdsAmount = getTDS(finScheduleData.getFinanceMain(), unpaidPft);
				}
				BigDecimal actualPft = unpaidPft.subtract(tdsAmount);
				if (allocAmount.compareTo(actualPft) >= 0) {

					pftAmount = pftAmount.add(unpaidPft);

				} else {

					BigDecimal remPft = (allocAmount).multiply(tdsMultiplier);
					pftAmount = pftAmount.add(remPft);
				}

				allocAmount = allocAmount.subtract(actualPft);
			}
		}

		pftAmount = CalculationUtil.roundAmount(pftAmount, finScheduleData.getFinanceMain().getCalRoundingMode(),
				finScheduleData.getFinanceMain().getRoundingTarget());
		return pftAmount;

	}

	public void updateAllocation(ReceiptAllocationDetail allocate, BigDecimal paidNow, BigDecimal waivedNow,
			BigDecimal tdsPaidNow, BigDecimal tdsWaivedNow, FinanceDetail detail) {
		logger.debug(Literal.ENTERING);

		allocate.setPaidAmount(allocate.getPaidAmount().add(paidNow));
		allocate.setWaivedAmount(allocate.getWaivedAmount().add(waivedNow));
		allocate.setTotalPaid(allocate.getTotalPaid().add(paidNow.add(tdsPaidNow)));
		allocate.setBalance(allocate.getBalance().subtract(paidNow.add(waivedNow)));
		allocate.setPaidNow(paidNow);
		allocate.setWaivedNow(waivedNow);
		allocate.setPaidAvailable(allocate.getPaidAvailable().subtract(paidNow));
		allocate.setWaivedAvailable(allocate.getWaivedAvailable().subtract(waivedNow));
		allocate.setTdsPaid(allocate.getTdsPaid().add(tdsPaidNow));
		allocate.setTdsWaived(allocate.getTdsWaived().add(tdsWaivedNow));

		// GST calculation for Paid and waived amounts(always Paid Amount we are
		// taking the inclusive type here because we are doing reverse
		// calculation here)
		if (allocate.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
			if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
				if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
					calAllocationPaidGST(detail, paidNow, allocate, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
				}
				if (waivedNow.compareTo(BigDecimal.ZERO) > 0) {
					String taxType = allocate.getTaxType();
					allocate.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
					calAllocationWaiverGST(detail, waivedNow, allocate);
					allocate.setTaxType(taxType);
				}
			}

			if (waivedNow.compareTo(BigDecimal.ZERO) > 0) {
				String taxType = allocate.getTaxType();
				allocate.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
				calAllocationWaiverGST(detail, waivedNow, allocate);
				allocate.setTaxType(taxType);
			}
		}

		// Set the balances
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
			allocate.setBalance(allocate.getBalance().subtract(paidNow.add(waivedNow).add(allocate.getWaivedGST())));
		} else {
			allocate.setBalance(allocate.getBalance().subtract(paidNow.add(waivedNow)));
		}

		logger.debug(Literal.LEAVING);
	}

	public void updateAllocationWithTds(ReceiptAllocationDetail allocate, BigDecimal paidNow, BigDecimal waivedNow,
			FinanceDetail detail, BigDecimal tdsPaidNow) {
		logger.debug(Literal.ENTERING);

		allocate.setPaidAmount(allocate.getPaidAmount().add(paidNow));
		allocate.setWaivedAmount(allocate.getWaivedAmount().add(waivedNow));
		allocate.setTotalPaid(allocate.getTotalPaid().add(paidNow));
		allocate.setBalance(allocate.getBalance().subtract(paidNow.add(waivedNow)));
		allocate.setPaidNow(paidNow);
		allocate.setWaivedNow(waivedNow);
		allocate.setPaidAvailable(allocate.getPaidAvailable().subtract(paidNow));
		allocate.setWaivedAvailable(allocate.getWaivedAvailable().subtract(waivedNow));

		BigDecimal paidAmount = getPaidAmount(allocate, paidNow);
		// GST calculation for Paid and waived amounts(always Paid Amount we are
		// taking the inclusive type here because we are doing reverse
		// calculation here)

		BigDecimal waivedAmount = getPaidAmount(allocate, waivedNow);
		if (allocate.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
			if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
				calAllocationPaidGST(detail, paidAmount, allocate, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
			}
			if (waivedNow.compareTo(BigDecimal.ZERO) > 0) {
				//String taxType= allocate.getTaxType();
				allocate.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
				calAllocationWaiverGST(detail, waivedAmount, allocate);
				//allocate.setTaxType(taxType);
			}
		}

		if (allocate.isTdsReq()) {
			BigDecimal tdsWaivedNow = getTDSAmount(detail.getFinScheduleData().getFinanceMain(), waivedAmount);
			allocate.setTdsWaivedNow(tdsWaivedNow);
			allocate.setTdsWaived(allocate.getTdsWaived().add(tdsWaivedNow));
		}

		if (allocate.isTdsReq() && tdsPaidNow.compareTo(BigDecimal.ZERO) == 0) {
			tdsPaidNow = getTDSAmount(detail.getFinScheduleData().getFinanceMain(), paidAmount);
		}

		allocate.setTdsPaidNow(tdsPaidNow);
		allocate.setTdsPaid(allocate.getTdsPaid().add(tdsPaidNow));
		allocate.setTotalPaid(allocate.getTotalPaid().add(tdsPaidNow));

		logger.debug(Literal.LEAVING);
	}

	public BigDecimal getNetProfit(FinReceiptData receiptData, BigDecimal pftPaid) {
		BigDecimal nBalPft = BigDecimal.ZERO;

		if (pftPaid.compareTo(BigDecimal.ZERO) <= 0) {
			return pftPaid;
		}
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> finSchdDetails = fsd.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchd : finSchdDetails) {
			BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
			BigDecimal pftNow = BigDecimal.ZERO;
			BigDecimal tdsNow = BigDecimal.ZERO;
			if (pftPaid.compareTo(BigDecimal.ZERO) <= 0) {
				return nBalPft;
			}
			if (balPft.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			if (balPft.compareTo(pftPaid) >= 0) {
				pftNow = pftPaid;
				pftPaid = BigDecimal.ZERO;
			} else {
				pftNow = balPft;
				pftPaid = pftPaid.subtract(pftNow);
			}
			if (curSchd.isTDSApplicable()) {
				tdsNow = getTDS(fsd.getFinanceMain(), pftNow);
			}

			nBalPft = nBalPft.add(pftNow.subtract(tdsNow));
		}
		return nBalPft;
	}

	public BigDecimal[] getEmiSplit(FinReceiptData receiptData, BigDecimal emiAmount) {
		BigDecimal[] emiSplit = new BigDecimal[3];
		Arrays.fill(emiSplit, BigDecimal.ZERO);
		BigDecimal pft = BigDecimal.ZERO;
		BigDecimal tds = BigDecimal.ZERO;
		BigDecimal npft = BigDecimal.ZERO;
		BigDecimal pri = BigDecimal.ZERO;
		if (emiAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return emiSplit;
		}
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> finSchdDetails = fsd.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchd : finSchdDetails) {
			BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
			BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
			BigDecimal tdsNow = BigDecimal.ZERO;
			BigDecimal nBalPft = BigDecimal.ZERO;
			BigDecimal npftPayNow = BigDecimal.ZERO;
			;
			if (curSchd.isTDSApplicable()) {
				tds = getTDS(fsd.getFinanceMain(), balPft);
			}

			nBalPft = balPft.subtract(tds);

			if (emiAmount.compareTo(nBalPft) >= 0) {
				npftPayNow = nBalPft;
				tdsNow = tds;
			} else {
				npftPayNow = emiAmount;
				if (curSchd.isTDSApplicable()) {
					BigDecimal pftNow = getNetOffTDS(fsd.getFinanceMain(), npftPayNow);
					tdsNow = pftNow.subtract(npftPayNow);
				}
			}
			npft = npft.add(npftPayNow);
			emiAmount = emiAmount.subtract(npftPayNow);
			pft = pft.add(tdsNow).add(npftPayNow);

			if (emiAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (emiAmount.compareTo(balPri) <= 0) {
					balPri = emiAmount;
				}
				pri = pri.add(balPri);
			}
			emiAmount = emiAmount.subtract(balPri);
			if (emiAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		emiSplit[0] = pri;
		emiSplit[1] = pft;
		emiSplit[2] = npft;
		return emiSplit;
	}

	public void setSMTParms() {
		if (StringUtils.isEmpty(taxRoundMode) || StringUtils.isEmpty(tdsRoundMode)) {
			taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
			taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);

			tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

			tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			tdsMultiplier = big100.divide(big100.subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
		}
	}

	public BigDecimal getExcessAmount(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();

		if (rch.getReceiptDetails() != null && rch.getReceiptDetails().size() > 0) {
			for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
				for (int i = 0; i < xcessPayables.size(); i++) {
					XcessPayables exs = xcessPayables.get(i);
					if (payType(recDtl.getPaymentType()).equals(exs.getPayableType())
							&& recDtl.getPayAgainstID() == exs.getPayableID()) {
						exs.setTotPaidNow(BigDecimal.ZERO);
						exs.setBalanceAmt(exs.getBalanceAmt().add(recDtl.getAmount()));
						exs.setAvailableAmt(exs.getAvailableAmt().add(recDtl.getAmount()));
						break;
					}
				}
			}
		}

		BigDecimal excessAmount = BigDecimal.ZERO;

		for (XcessPayables payable : xcessPayables) {
			//if (RepayConstants.EXAMOUNTTYPE_ADVINT.equals(payable.getPayableType())) {
			excessAmount = excessAmount.add(payable.getBalanceAmt());
			//}
		}

		return excessAmount;
	}

	private String payType(String mode) {
		String payType = "";
		if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_EMIINADV)) {
			payType = RepayConstants.EXAMOUNTTYPE_EMIINADV;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_EXCESS)) {
			payType = RepayConstants.EXAMOUNTTYPE_EXCESS;
			/*
			 * while doing EarlySettlement or Closure receipt is used below payments, if we again open the same record
			 * below mode of payments are shown as Zero to fix this we are adding these PayemtTypes here.
			 */
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_ADVEMI)) {
			payType = RepayConstants.RECEIPTMODE_ADVEMI;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_ADVINT)) {
			payType = RepayConstants.RECEIPTMODE_ADVINT;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_CASHCLT)) {
			payType = RepayConstants.RECEIPTMODE_CASHCLT;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_DSF)) {
			payType = RepayConstants.RECEIPTMODE_DSF;
		} else {
			payType = RepayConstants.EXAMOUNTTYPE_PAYABLE;
		}
		return payType;
	}

	/**
	 * Method for Fetch Overdue Penalty details as per passing Value Date
	 * 
	 * @param finScheduleData
	 * @param receiptData
	 * @param valueDate
	 * @return
	 */
	public List<FinODDetails> getValueDatePenalties(FinScheduleData finScheduleData, BigDecimal orgReceiptAmount,
			Date valueDate, List<FinanceRepayments> finRepayments, boolean resetReq) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(financeMain.getFinReference());
		if (CollectionUtils.isEmpty(overdueList)) {
			logger.debug("Leaving");
			return overdueList;
		}

		if (CollectionUtils.isEmpty(finSchdDtls)) {
			finSchdDtls = finScheduleData.getFinanceScheduleDetails();
		}

		// Repayment Details
		List<FinanceRepayments> repayments = new ArrayList<FinanceRepayments>();
		if (CollectionUtils.isNotEmpty(finRepayments)) {
			repayments = finRepayments;
		} else {
			repayments = financeRepaymentsDAO.getFinRepayListByFinRef(financeMain.getFinReference(), false, "");
		}

		overdueList = latePayMarkingService.calPDOnBackDatePayment(financeMain, overdueList, valueDate, finSchdDtls,
				repayments, resetReq, true);

		logger.debug("Leaving");
		return overdueList;
	}

	public FinReceiptData processCIP(FinReceiptData receiptData) {
		if (receiptPurposeCtg != 2) {
			return receiptData;
		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<Long> receiptList = finReceiptHeaderDAO.getInProcessReceiptId(rch.getReference());
		if (receiptList == null || receiptList.size() == 0) {
			return receiptData;
		}

		List<FinanceRepayments> finRepayments = financeRepaymentsDAO.getInProcessRepaymnets(rch.getReference(),
				receiptList);
		receiptData.setInProcRepayments(finRepayments);

		if (finRepayments != null && finRepayments.size() > 0) {
			receiptData = markSchedulesUnpaid(receiptData, finRepayments);
		}

		return receiptData;

	}

	private FinReceiptData markSchedulesUnpaid(FinReceiptData receiptData, List<FinanceRepayments> finRepayments) {
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
		List<FinODDetails> finOdDtls = fsd.getFinODDetails();
		for (FinanceScheduleDetail schd : schdDtls) {
			for (FinanceRepayments repay : finRepayments) {
				if (schd.getSchDate().compareTo(repay.getFinSchdDate()) == 0) {
					schd.setSchdPriPaid(schd.getSchdPriPaid().subtract(repay.getFinSchdPriPaid()));
					schd.setSchdPftPaid(schd.getSchdPftPaid().subtract(repay.getFinSchdPftPaid()));
					schd.setTDSPaid(schd.getTDSPaid().subtract(repay.getFinSchdTdsPaid()));
					schd.setSchPriPaid(false);
					schd.setSchPftPaid(false);
					break;
				}
			}
		}

		return receiptData;
	}

	private FinReceiptData initializePresentment(FinReceiptData receiptData) {
		List<ReceiptAllocationDetail> allocationsList = new ArrayList<ReceiptAllocationDetail>(1);
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		Date valueDate = receiptData.getValueDate();
		receiptData.getReceiptHeader().setValueDate(valueDate);
		BigDecimal tdsDue = BigDecimal.ZERO;
		BigDecimal nPftDue = BigDecimal.ZERO;
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal pftDue = BigDecimal.ZERO;
		BigDecimal emiDue = BigDecimal.ZERO;
		for (int i = 1; i < schdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = schdDetails.get(i);
			Date schdDate = curSchd.getSchDate();
			if (DateUtil.compare(valueDate, schdDate) != 0) {
				continue;
			}

			priDue = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
			pftDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
			if (curSchd.isTDSApplicable()) {
				tdsDue = getTDS(finScheduleData.getFinanceMain(), pftDue);
			}
			nPftDue = pftDue.subtract(tdsDue);
			emiDue = emiDue.add(nPftDue).add(priDue);
		}
		String desc = null;
		int id = -1;

		if (pftDue.compareTo(BigDecimal.ZERO) > 0) {
			id = id + 1;
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PFT");
			allocationsList.add(
					setAllocRecord(receiptData, RepayConstants.ALLOCATION_PFT, id, pftDue, desc, 0, "", false, false));
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_TDS");
			id = id + 1;
			allocationsList.add(
					setAllocRecord(receiptData, RepayConstants.ALLOCATION_TDS, id, tdsDue, desc, 0, "", false, false));
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_NPFT");
			id = id + 1;
			allocationsList.add(setAllocRecord(receiptData, RepayConstants.ALLOCATION_NPFT, id, nPftDue, desc, 0, "",
					false, false));
		}

		if (priDue.compareTo(BigDecimal.ZERO) > 0) {
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PRI");
			id = id + 1;
			allocationsList.add(
					setAllocRecord(receiptData, RepayConstants.ALLOCATION_PRI, id, priDue, desc, 0, "", false, false));
		}

		if (emiDue.compareTo(BigDecimal.ZERO) > 0) {
			desc = Labels.getLabel("label_RecceiptDialog_AllocationType_EMI");
			id = id + 1;
			allocationsList.add(
					setAllocRecord(receiptData, RepayConstants.ALLOCATION_EMI, id, emiDue, desc, 0, "", true, true));
		}

		receiptData.getReceiptHeader().setAllocations(allocationsList);

		return receiptData;
	}

	public FinReceiptData preparePayables(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.getXcessPayables().clear();
		for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
			if (recDtl.getPayAgainstID() > 0) {
				XcessPayables xcess = new XcessPayables();
				String excessLabel = "";
				if (!StringUtils.equals(RepayConstants.PAYTYPE_PAYABLE, recDtl.getPaymentType())) {
					excessLabel = "label_RecceiptDialog_ExcessType_";
					xcess.setPayableDesc(Labels.getLabel(excessLabel + recDtl.getPaymentType()));
				} else {
					ManualAdvise adv = manualAdviseDAO.getManualAdviseById(recDtl.getPayAgainstID(), "_View");
					excessLabel = adv.getFeeTypeDesc();
					xcess.setPayableDesc(excessLabel);
				}

				// Add Dummy EMI in Advance Record
				xcess.setPayableType(recDtl.getPaymentType());

				xcess.setPayableID(recDtl.getPayAgainstID());
				xcess.setAmount(recDtl.getAmount());
				rch.getXcessPayables().add(xcess);
			}
		}
		return receiptData;
	}

	public List<FinODDetails> calPenalty(FinScheduleData finScheduleData, FinReceiptData receiptData, Date valueDate,
			List<FinODDetails> overdueList) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finScheduleData.getFinanceMain();

		if (CollectionUtils.isEmpty(overdueList)) {
			return overdueList;
		}

		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		String finReference = fm.getFinReference();
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayListByFinRef(finReference, false, "");
		return latePayMarkingService.calPDOnBackDatePayment(fm, overdueList, valueDate, schdList, repayments, true,
				true);

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public NPAService getNpaService() {
		return npaService;
	}

	public void setNpaService(NPAService npaService) {
		this.npaService = npaService;
	}

}