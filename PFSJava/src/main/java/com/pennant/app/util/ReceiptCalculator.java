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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
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
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.rits.cloning.Cloner;

public class ReceiptCalculator implements Serializable {
	private static final long serialVersionUID = 8062681791631293126L;
	private static Logger logger = LogManager.getLogger(ReceiptCalculator.class);

	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FeeTypeDAO feeTypeDAO;
	private AccrualService accrualService;
	private FeeCalculator feeCalculator;
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
	private FinODPenaltyRateDAO finODPenaltyRateDAO;

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

	// boolean isAdjSchedule = false;
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

		if (receiptPurpose == null) {
			return receiptPurposeCtg;
		}
		switch (receiptPurpose) {
		case FinanceConstants.FINSER_EVENT_SCHDRPY:
			receiptPurposeCtg = 0;
			break;
		case FinanceConstants.FINSER_EVENT_EARLYRPY:
			receiptPurposeCtg = 1;
			break;
		case FinanceConstants.FINSER_EVENT_EARLYSETTLE:
			receiptPurposeCtg = 2;
			break;
		case FinanceConstants.FINSER_EVENT_EARLYSTLENQ:
			receiptPurposeCtg = 3;
			break;
		default:
			receiptPurposeCtg = -1;
			break;
		}
		return receiptPurposeCtg;
	}

	/** To Calculate the Amounts for given schedule */
	private FinReceiptData procInitiateReceipt(FinReceiptData receiptData, boolean isPresentment) {
		// Initialize Repay
		receiptData.setPresentment(isPresentment);
		if ("I".equals(receiptData.getBuildProcess())) {
			receiptData = initializeReceipt(receiptData);
		} else if ("R".equals(receiptData.getBuildProcess())) {
			receiptData = recalReceipt(receiptData, isPresentment);
		}

		receiptData = setTotals(receiptData, 0);

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

	private FinReceiptData initializeReceipt(FinReceiptData rd) {
		FinScheduleData schdData = rd.getFinanceDetail().getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		rd.setFinReference(fm.getFinReference());
		rd.setRepayMain(null);
		boolean isAllocated = false;
		Date valueDate = rd.getValueDate();

		if (receiptPurposeCtg == 2) {
			schdData.setFeeEvent(AccountEventConstants.ACCEVENT_EARLYSTL);
		} else if (receiptPurposeCtg == 1) {
			schdData.setFeeEvent(AccountEventConstants.ACCEVENT_EARLYPAY);
		}

		rd.getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);

		if (valueDate == null) {
			// Temporary fix for API call
			FinServiceInstruction fsi = schdData.getFinServiceInstruction();
			if (fsi != null) {
				valueDate = fsi.getValueDate();
			} else {
				rd.getReceiptHeader().setValueDate(rd.getValueDate());
			}
		}

		RepayMain rm = new RepayMain();
		rm.setFinReference(fm.getFinReference());
		rm.setFinCcy(fm.getFinCcy());
		rm.setProfitDaysBais(fm.getProfitDaysBasis());
		rm.setFinType(fm.getFinType());
		rm.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		rm.setFinBranch(fm.getFinBranch());
		rm.setLovDescFinBranchName(fm.getLovDescFinBranchName());
		rm.setCustID(fm.getCustID());
		rm.setLovDescCustCIF(fm.getLovDescCustCIF());
		rm.setLovDescSalutationName(fm.getLovDescSalutationName());
		rm.setLovDescCustFName(fm.getLovDescCustFName());
		rm.setLovDescCustLName(fm.getLovDescCustLName());
		rm.setLovDescCustShrtName(fm.getLovDescCustShrtName());
		rm.setDateStart(fm.getFinStartDate());
		rm.setDateMatuirty(fm.getMaturityDate());
		rm.setAccrued(rd.getAccruedTillLBD());
		rm.setPendindODCharges(rd.getPendingODC());
		rm.setEarlyPayEffectOn(fm.getLovDescFinScheduleOn());

		rm.setDateLastFullyPaid(fm.getFinStartDate());
		rm.setDateNextPaymentDue(fm.getMaturityDate());
		rm.setPrincipalPayNow(BigDecimal.ZERO);
		rm.setProfitPayNow(BigDecimal.ZERO);
		rm.setRefundNow(BigDecimal.ZERO);

		rm.setRepayAmountNow(BigDecimal.ZERO);
		rm.setRepayAmountExcess(BigDecimal.ZERO);
		rd.setRepayMain(rm);

		List<FinanceScheduleDetail> schdDetails = schdData.getFinanceScheduleDetails();
		if (rd.isPresentment()) {
			if (!ImplementationConstants.ALLOW_OLDEST_DUE) {
				return initializePresentment(rd);
			}
		}

		List<FinanceScheduleDetail> schedules = new ArrayList<>(1);
		for (FinanceScheduleDetail schd : schdDetails) {
			schedules.add(schd.copyEntity());
		}

		FinReceiptHeader rch = rd.getReceiptHeader();
		FinanceProfitDetail pfd = schdData.getFinPftDeatil();
		processCIP(rd);
		if (receiptPurposeCtg == 2 && rd.getOrgFinPftDtls() == null) {
			FinanceProfitDetail orgFinPftDtls = pfd.copyEntity();
			FinanceScheduleDetail prvSchd = financeScheduleDetailDAO.getPrvSchd(fm.getFinReference(), valueDate);
			Date prvSchdDate = valueDate;

			if (prvSchd != null) {
				prvSchdDate = prvSchd.getSchDate();
			}

			orgFinPftDtls = accrualService.calProfitDetails(fm, schdDetails, orgFinPftDtls, prvSchdDate);
			rd.setOrgFinPftDtls(orgFinPftDtls.copyEntity());
		}

		if (rch.getValueDate() == null || rch.getReceiptDate().compareTo(rch.getValueDate()) != 0) {
			pfd = accrualService.calProfitDetails(fm, schdData.getFinanceScheduleDetails(), pfd, valueDate);
			rch.setValueDate(valueDate);
		}
		if (CollectionUtils.isNotEmpty(rd.getAllocList())) {
			allocated = rd.getAllocList();
			isAllocated = true;

		}
		List<Date> presentmentDates = getPresentmentDates(rd, valueDate);
		rd = calSummaryDetail(rd, valueDate);
		List<ReceiptAllocationDetail> allocationsList = resetAllocationList(rd);
		rd.getReceiptHeader().setAllocations(allocationsList);

		rd = fetchODPenalties(rd, valueDate, presentmentDates);
		rd = fetchManualAdviseDetails(rd, valueDate);

		rd = setXcessPayables(rd);

		if (CollectionUtils.isNotEmpty(allocated)) {
			allocated = rd.getAllocList();
			rd = setPaidValues(rd);
		}

		rd = setTotals(rd, 0);
		rd = fetchEventFees(rd, isAllocated);

		if (CollectionUtils.isNotEmpty(allocated)) {
			allocated = rd.getAllocList();
			rd = setPaidValues(rd);
			rd = setWaivedValues(rd);
		}

		return rd;
	}

	private FinReceiptData setPaidValues(FinReceiptData receiptData) {
		FinReceiptHeader rh = receiptData.getReceiptHeader();
		String allocationType = rh.getAllocationType();

		if (receiptData.isForeClosure() || RepayConstants.ALLOCATIONTYPE_MANUAL.equals(allocationType)) {
			receiptData.setFCDueChanged(false);

			List<ReceiptAllocationDetail> allocations = rh.getAllocations();
			for (ReceiptAllocationDetail allocate : allocations) {
				for (ReceiptAllocationDetail alloc : allocated) {
					if (allocate.getAllocationType().equals(alloc.getAllocationType())
							&& allocate.getAllocationTo() == alloc.getAllocationTo()) {
						if (!receiptData.isForeClosure()) {
							if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_FEE)) {
								allocate.setWaivedAmount(alloc.getWaivedAmount());
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

							allocate.setWaivedAmount(alloc.getWaivedAmount());
							allocate.setWaivedGST(alloc.getWaivedGST());
						} else {
							if (allocate.getTotalDue().compareTo(alloc.getTotalDue()) != 0) {
								receiptData.setFCDueChanged(true);
							}
							// BUGFIX 144481
							allocate.setPaidAmount(alloc.getPaidAmount());
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
												allocate.getTaxType());

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

			List<ReceiptAllocationDetail> radList = allocations;
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
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		if (RepayConstants.ALLOCATIONTYPE_AUTO.equals(receiptHeader.getAllocationType())) {
			for (ReceiptAllocationDetail allocate : receiptHeader.getAllocations()) {
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

	private FinReceiptData calSummaryDetail(FinReceiptData rchd, Date valueDate) {
		FinScheduleData schd = rchd.getFinanceDetail().getFinScheduleData();
		RepayMain rm = rchd.getRepayMain();
		FinanceMain fm = schd.getFinanceMain();
		FinanceProfitDetail pfd = schd.getFinPftDeatil();

		rm.setEarlyPayOnSchDate(valueDate);
		// repayMain.setDownpayment(finPftDeatils.getDownpay());
		rm.setDownpayment(pfd.getDownPayment());

		rm.setTotalCapitalize(pfd.getTotalPftCpz());

		EventProperties eventProperties = fm.getEventProperties();
		boolean cpzPosIntact = false;

		if (eventProperties.isParameterLoaded()) {
			cpzPosIntact = eventProperties.isCpzPosIntact();
		} else {
			cpzPosIntact = SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT);
		}
		if (cpzPosIntact) {
			rm.setFinAmount(pfd.getTotalpriSchd());
			rm.setCurFinAmount(pfd.getTotalPriBal());
		} else {
			rm.setFinAmount(pfd.getTotalpriSchd().subtract(rm.getTotalCapitalize()));
			rm.setCurFinAmount(pfd.getTotalPriBal().subtract(rm.getTotalCapitalize()).add(pfd.getTdPftCpz()));
		}

		rm.setPrincipal(pfd.getTotalpriSchd());
		rm.setProfit(pfd.getTotalPftSchd());
		rm.setTotalFeeAmt(fm.getFeeChargeAmt());
		rm.setPrincipalPaid(pfd.getTotalPriPaid());
		rm.setProfitPaid(pfd.getTotalPftPaid());

		if (receiptPurposeCtg == 2) {
			rm.setPrincipalBalance(rm.getCurFinAmount());
			rm.setProfitBalance(pfd.getPftAccrued());
		} else {
			rm.setPrincipalBalance(pfd.getTotalPriBal());
			rm.setProfitBalance(pfd.getTotalPftBal());
		}

		rm.setOverduePrincipal(pfd.getTdSchdPriBal());
		rm.setOverdueProfit(pfd.getTdSchdPftBal());
		rm.setDateLastFullyPaid(pfd.getFullPaidDate());
		rm.setDateNextPaymentDue(pfd.getNSchdDate());
		rm.setAccrued(pfd.getPftAccrued());

		return rchd;
	}

	public FinReceiptData fetchEventFees(FinReceiptData rd, boolean isAllocated) {
		if (rd.isPresentment()) {
			return rd;
		}
		if (receiptPurposeCtg < 0) {
			return rd;
		}

		if (receiptPurposeCtg > 1) {
			rd.setEarlySettle(true);
		}
		FinanceMain fm = rd.getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<FinFeeDetail> oldFinFeeDtls = rd.getFinFeeDetails();
		List<FinFeeDetail> finFeedetails = null;

		rd = feeCalculator.calculateFees(rd);
		finFeedetails = rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeedetails)) {
			return rd;
		}

		if (CollectionUtils.isNotEmpty(oldFinFeeDtls)) {
			for (FinFeeDetail oldFinFeeDtl : oldFinFeeDtls) {
				for (FinFeeDetail actualFeeDtl : finFeedetails) {
					if (oldFinFeeDtl.getFeeTypeID() == actualFeeDtl.getFeeTypeID()
							&& "PERCENTG".equals(actualFeeDtl.getCalculationType())) {
						actualFeeDtl.setFeeID(oldFinFeeDtl.getFeeID());
						if ("PERCENTG".equals(actualFeeDtl.getCalculationType())) {
							actualFeeDtl.setActPercentage(oldFinFeeDtl.getActPercentage());
							actualFeeDtl.setPercentage(oldFinFeeDtl.getActPercentage());

							Map<String, BigDecimal> taxPercentages = GSTCalculator
									.getTaxPercentages(fm.getFinReference());
							feeCalculator.calculateFeePercentageAmount(rd, taxPercentages);
						}
					}
				}
			}
		}

		finFeedetails = rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		List<ReceiptAllocationDetail> allocationsList = rd.getReceiptHeader().getAllocations();

		for (FinFeeDetail feeDtls : finFeedetails) {
			// 27-08-19 PSD:140172 Issue in GST calculation for Fore closure
			BigDecimal feeAmount = feeDtls.getActualAmount();
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(feeDtls.getTaxComponent())) {
				feeAmount = feeDtls.getActualAmountOriginal();
			}
			ReceiptAllocationDetail allocDetail = setAllocationRecord(rd, RepayConstants.ALLOCATION_FEE, 1, feeAmount,
					feeDtls.getFeeTypeDesc(), -(feeDtls.getFeeTypeID()), feeDtls.getTaxComponent(), true, true);
			allocDetail.setFeeTypeCode(feeDtls.getFeeTypeCode());

			BigDecimal tdsAmount = BigDecimal.ZERO;
			if (TDSCalculator.isTDSApplicable(fm, feeDtls)) {
				BigDecimal taxableAmount = BigDecimal.ZERO;
				if (StringUtils.isNotEmpty(feeDtls.getTaxComponent())) {
					taxableAmount = allocDetail.getTotRecv().subtract(allocDetail.getDueGST());
				} else {
					taxableAmount = allocDetail.getTotRecv();
				}

				tdsAmount = getTDSAmount(fm, taxableAmount);
				allocDetail.setPercTds(tdsPerc);
				allocDetail.setTdsReq(true);
				allocDetail.setTdsDue(tdsAmount);
			}
			allocDetail.setTotalDue(allocDetail.getTotalDue().subtract(tdsAmount.add(allocDetail.getInProcess())));

			if (feeDtls.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
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
		rd.getReceiptHeader().setAllocations(allocationsList);
		return rd;
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
		List<ReceiptAllocationDetail> allocationsList = new ArrayList<>(1);
		FinanceProfitDetail pfd = receiptData.getFinanceDetail().getFinScheduleData().getFinPftDeatil();
		RepayMain rm = receiptData.getRepayMain();

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
			futPri = rm.getCurFinAmount().subtract(priDue);
			futPft = pfd.getTdSchdPftBal().subtract(pftDue);
			futTds = pfd.getTdTdsBal().subtract(tdsDue);
			futNPft = futPft.subtract(futTds);
		} else {
			tdsDue = pfd.getTdTdsAmount().subtract(pfd.getTdTdsPaid());
			priDue = pfd.getTdSchdPriBal();
			pftDue = pfd.getTdSchdPft().subtract(pfd.getTdSchdPftPaid());
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
		List<ReceiptAllocationDetail> allocationsList = receiptData.getReceiptHeader().getAllocations();

		setReceiptCategory(receiptData.getReceiptHeader().getReceiptPurpose());
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal nPftDue = BigDecimal.ZERO;
		BigDecimal totNetPastDue = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : allocationsList) {
			switch (allocate.getAllocationType()) {
			case RepayConstants.ALLOCATION_PRI:
				priDue = allocate.getPaidAmount();
				break;
			case RepayConstants.ALLOCATION_PFT:
				nPftDue = allocate.getPaidAmount();
				break;
			case RepayConstants.ALLOCATION_EMI:
				break;
			default:
				totNetPastDue = totNetPastDue.add(allocate.getPaidAmount());
				break;
			}

		}

		totNetPastDue = totNetPastDue.add(priDue).add(nPftDue);
		return totNetPastDue;
	}

	public List<Date> getPresentmentDates(FinReceiptData rd, Date valueDate) {
		List<Date> presentmentDates = new ArrayList<>();
		BigDecimal inPresPri = BigDecimal.ZERO;
		BigDecimal inPresPft = BigDecimal.ZERO;
		BigDecimal inPresTds = BigDecimal.ZERO;

		if (receiptPurposeCtg != 0 || rd.isPresentment()) {
			return presentmentDates;
		}

		FinScheduleData schData = rd.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> schedules = schData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = null;

		for (int i = 0; i < schedules.size() - 1; i++) {
			curSchd = schedules.get(i);

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
		rd.setInPresPri(inPresPri);
		rd.setInPresPft(inPresPft);
		rd.setInPresTds(inPresTds);
		rd.setInPresNpft(inPresPft.subtract(inPresTds));

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

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			allocation.setTypeDesc(desc + DESC_EXC_TAX);
			allocation.setTotalDue(due.add(taxSplit.gettGST()));
			allocation.setTotRecv(due.add(taxSplit.gettGST()));
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			allocation.setTypeDesc(desc + DESC_INC_TAX);
		} else {
			allocation.setTypeDesc(desc);
		}

		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType(taxType);

		// GST Calculations
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

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			allocation.setTypeDesc(desc + DESC_EXC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			allocation.setTypeDesc(desc + DESC_INC_TAX);
			allocation.setTotalDue(curDue.add(taxSplit.gettGST()));
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else {
			allocation.setTypeDesc(desc);
		}

		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType(taxType);
		// CESS Calculations
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

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			allocation.setTypeDesc(desc + DESC_EXC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			allocation.setTypeDesc(desc + DESC_INC_TAX);
			allocation.setTotRecv(taxSplit.getNetAmount());
		} else {
			allocation.setTypeDesc(desc);
		}

		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType(taxType);
		// CESS Calculations
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
			if (PennantConstants.YES.equals(allocation.getWaiverAccepted())) {
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

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocation.getTaxType())) {
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

	public FinReceiptData fetchODPenalties(FinReceiptData rd, Date valueDate, List<Date> presentmentDates) {
		FinScheduleData schdData = rd.getFinanceDetail().getFinScheduleData();

		if (rd.isPresentment()) {
			return rd;
		}

		boolean isGoldLoan = false;

		FinanceMain fm = schdData.getFinanceMain();

		if (FinanceConstants.PRODUCT_GOLD.equals(fm.getProductCategory())) {
			isGoldLoan = true;
		}

		// For Gold Loan Calculate only after MDT. No calculation Required
		if (isGoldLoan && fm.getMaturityDate().compareTo(valueDate) > 0) {
			return rd;
		}

		List<ReceiptAllocationDetail> allocations = rd.getReceiptHeader().getAllocations();

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
		List<FinODDetails> overdueList = getValueDatePenalties(schdData, rd.getTotReceiptAmount(), reqMaxODDate, null,
				true);

		// No Overdue penalty exit
		if (CollectionUtils.isEmpty(overdueList)) {
			return rd;
		}

		if (receiptPurposeCtg == 2 && !isGoldLoan && !CollectionUtils.isEmpty(rd.getInProcRepayments())) {
			for (FinODDetails od : overdueList) {
				for (FinanceRepayments repay : rd.getInProcRepayments()) {
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

		FinODPenaltyRate finODPenaltyRate = schdData.getFinODPenaltyRate();
		if (finODPenaltyRate == null) {
			finODPenaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(fm.getFinReference(), "_AView");
		}

		if (ObjectUtils.isNotEmpty(finODPenaltyRate) && !finODPenaltyRate.isoDTDSReq()) {
			lppFeeType.setTdsReq(false);
		}

		String taxType = null;

		if (lppFeeType != null && lppFeeType.isTaxApplicable()) {
			taxType = lppFeeType.getTaxComponent();
		}
		BigDecimal tdsAmount = BigDecimal.ZERO;
		for (FinODDetails fod : overdueList) {
			if (fod.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
				break;
			}

			lpiBal = lpiBal.add(fod.getLPIBal());
			if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
				lppBal = lppBal.add(fod.getTotPenaltyBal());
				BigDecimal taxableAmount = fod.getTotPenaltyBal();

				if (StringUtils.isNotEmpty(taxType)) {
					TaxAmountSplit taxSplit = new TaxAmountSplit();
					taxSplit.setAmount(taxableAmount);
					taxSplit.setTaxType(taxType);
					taxSplit = getGST(rd.getFinanceDetail(), taxSplit);
					if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
						taxableAmount = taxableAmount.subtract(taxSplit.gettGST());
					}

				}

				if (TDSCalculator.isTDSApplicable(fm, lppFeeType)) {
					tdsAmount = tdsAmount.add(getTDSAmount(fm, taxableAmount));
				}

			}

		}

		String desc = Labels.getLabel("label_RecceiptDialog_AllocationType_LPFT");
		// Fetch Late Pay Profit Details
		if (lpiBal.compareTo(BigDecimal.ZERO) > 0) {
			allocations.add(setAllocRecord(rd, RepayConstants.ALLOCATION_LPFT, 5, lpiBal, desc, 0, "", true, true));
		}

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_ODC");
		// Fetch Sum of Overdue Charges
		if (lppBal.compareTo(BigDecimal.ZERO) > 0) {
			rd.setPendingODC(lppBal);
			ReceiptAllocationDetail lpp = setAllocationRecord(rd, RepayConstants.ALLOCATION_ODC, 6, lppBal, desc, 0,
					taxType, true, true);
			if (TDSCalculator.isTDSApplicable(fm, lppFeeType)) {
				if (ObjectUtils.isNotEmpty(finODPenaltyRate) && finODPenaltyRate.isoDTDSReq()) {
					lpp.setPercTds(tdsPerc);
					lpp.setTdsReq(true);
					lpp.setTdsDue(tdsAmount);
				}
			}
			lpp.setTotalDue(lpp.getTotalDue().subtract(tdsAmount.add(lpp.getInProcess())));
			allocations.add(lpp);
		}

		schdData.setFinODDetails(overdueList);
		rd.getReceiptHeader().setAllocations(allocations);
		return rd;
	}

	public FinReceiptData fetchManualAdviseDetails(FinReceiptData rd, Date valueDate) {
		// Manual Advises
		if (rd.isPresentment()) {
			return rd;
		}
		FinScheduleData fsd = rd.getFinanceDetail().getFinScheduleData();
		List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdviseByRef(rd.getReceiptHeader().getReference(),
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");

		if (CollectionUtils.isEmpty(adviseList)) {
			return rd;
		}

		List<ReceiptAllocationDetail> allocationsList = rd.getReceiptHeader().getAllocations();

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
			ReceiptAllocationDetail allocDetail = setAllocationRecord(rd, type, id, adviseDue, desc, advID, taxType,
					true, true);
			allocDetail.setFeeTypeCode(advise.getFeeTypeCode());
			BigDecimal tdsAmount = BigDecimal.ZERO;
			if (TDSCalculator.isTDSApplicable(fsd.getFinanceMain(), isTdsApplicable)) {
				BigDecimal taxableAmount = BigDecimal.ZERO;
				if (StringUtils.isNotEmpty(taxType)) {
					taxableAmount = allocDetail.getTotRecv().subtract(allocDetail.getDueGST());
				} else {
					taxableAmount = allocDetail.getTotRecv();
				}

				FinanceMain fm = rd.getFinanceDetail().getFinScheduleData().getFinanceMain();
				tdsAmount = getTDSAmount(fm, taxableAmount);
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

		rd.getReceiptHeader().setAllocations(allocationsList);
		rd.getReceiptHeader().setReceivableAdvises(adviseList);

		return rd;
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
		List<XcessPayables> xcessPayableList = new ArrayList<>(1);

		// Load FinExcess Details
		List<FinExcessAmount> excessList = receiptData.getReceiptHeader().getExcessAmounts();

		if (CollectionUtils.isEmpty(excessList)) {
			receiptData.getReceiptHeader().setXcessPayables(xcessPayableList);
			return receiptData;
		}

		List<FinExcessAmountReserve> excessAmtRev = receiptData.getReceiptHeader().getExcessReserves();

		for (FinExcessAmount excess : excessList) {
			XcessPayables xcessPayable = new XcessPayables();
			String excessLabel = "label_RecceiptDialog_ExcessType_";

			// Add Dummy EMI in Advance Record
			xcessPayable.setPayableType(excess.getAmountType());
			xcessPayable.setPayableDesc(Labels.getLabel(excessLabel + excess.getAmountType()));

			xcessPayable.setPayableID(excess.getExcessID());
			xcessPayable.setAmount(xcessPayable.getAmount()
					.add(excess.getAmount().subtract(excess.getUtilisedAmt().add(excess.getReservedAmt()))));

			if (CollectionUtils.isNotEmpty(excessAmtRev)) {
				for (FinExcessAmountReserve reserve : excessAmtRev) {
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

		if (CollectionUtils.isEmpty(payableList)) {
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

			if (CollectionUtils.isNotEmpty(payableReserveList)) {
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
				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(xcessPayable.getTaxType())) {
					feeDesc = feeDesc + DESC_EXC_TAX;
				} else {
					feeDesc = feeDesc + DESC_INC_TAX;
				}
			}

			xcessPayable.setPayableDesc(feeDesc);
			xcessPayable.setGstAmount(taxSplit.gettGST());
			xcessPayable.setTotPaidNow(xcessPayable.getReserved());
			if (xcessPayable.isTdsApplicable()) {
				FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
				xcessPayable.setTdsAmount(getTDSAmount(fm, taxSplit.getNetAmount()));
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
		for (XcessPayables payable : xcessPayables) {
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

		if (RepayConstants.ALLOCATIONTYPE_AUTO.equals(rch.getAllocationType())) {
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

		if (allocated == null) {
			allocated = new ArrayList<>();
		}

		for (ReceiptAllocationDetail rad : rch.getAllocations()) {
			allocated.add(rad.copyEntity());
		}

		if (RepayConstants.ALLOCATIONTYPE_AUTO.equals(rch.getAllocationType())) {
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

			partPayAmount = adjustAdvIntPayment(receiptData, partPayAmount);
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

	private BigDecimal adjustAdvIntPayment(FinReceiptData receiptData, BigDecimal partPayAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		AdvanceType advanceType = AdvanceType.getType(fm.getAdvType());
		if (advanceType == AdvanceType.UF || advanceType == AdvanceType.UT || advanceType == AdvanceType.AF) {
			if (partPayAmount.compareTo(BigDecimal.ZERO) > 0) {
				for (ReceiptAllocationDetail rad : rch.getAllocations()) {
					String allocationType = rad.getAllocationType();
					if (RepayConstants.ALLOCATION_FUT_TDS.equals(allocationType)) {
						partPayAmount = partPayAmount.subtract(rad.getDueAmount());
						partPayAmount = partPayAmount.subtract(rad.getDueAmount());
					}
				}
			}
		}
		return partPayAmount;
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * 
	 */
	private FinReceiptData recalReceipt(FinReceiptData receiptData, boolean isPresentment) {
		receiptData.setAdjSchedule(true);
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		// Rendering
		if (CollectionUtils.isEmpty(rcdList)) {
			return receiptData;
		}

		// Treat Paid Amounts as dues
		List<ReceiptAllocationDetail> radList = receiptData.getReceiptHeader().getAllocations();
		BigDecimal totWaived = BigDecimal.ZERO;
		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail rad = radList.get(i);
			if (RepayConstants.ALLOCATION_PP.equals(rad.getAllocationType())) {
				rch.setPpIdx(i);
			}
			if (rad.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
				radList.remove(i);
				i = i - 1;
				continue;
			}

			rad.setTotalPaid(BigDecimal.ZERO);

			if (!"N".equals(rad.getWaiverAccepted())) {
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
				rch.setBalAmount(payNow.add(rch.getRefWaiverAmt()));
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
		setSMTParms(finMain.getEventProperties());
		BigDecimal netAmount = amount.multiply(tdsMultiplier);
		netAmount = CalculationUtil.roundAmount(netAmount, tdsRoundMode, tdsRoundingTarget);

		return netAmount;
	}

	public BigDecimal getTDSAmount(FinanceMain finMain, BigDecimal amount) {
		// Fetch and store Tax percentages one time
		setSMTParms(finMain.getEventProperties());
		BigDecimal netAmount = amount.multiply(tdsPerc.divide(big100));
		netAmount = CalculationUtil.roundAmount(netAmount, tdsRoundMode, tdsRoundingTarget);

		return netAmount;
	}

	// Return TDS Amount on Paid Interest. (i.e Total Paid = Net Due + TDS
	// Returned)
	public BigDecimal getTDS(FinanceMain finMain, BigDecimal amount) {
		// Fetch and store Tax percentages one time
		setSMTParms(finMain.getEventProperties());

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
		EventProperties eventProperties = financeDetail.getFinScheduleData().getFinanceMain().getEventProperties();

		setSMTParms(eventProperties);

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

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			return getInclusiveGST(financeDetail, taxSplit);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
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
		logger.debug(Literal.ENTERING);

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		setReceiptCategory(rch.getReceiptPurpose());
		List<ReceiptAllocationDetail> allocationsList = rch.getAllocations();
		List<FinODDetails> odList = receiptData.getFinanceDetail().getFinScheduleData().getFinODDetails();

		List<FinODDetails> tempOdList = new ArrayList<>(1);
		for (FinODDetails fod : odList) {
			tempOdList.add(fod.copyEntity());
		}
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
			logger.debug(Literal.LEAVING);
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

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private FinReceiptData foreClosureAllocation(FinReceiptData receiptData) {
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

		return receiptData;
	}

	private FinReceiptData adjustAdvanceInt(FinReceiptData receiptData) {
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

		for (ReceiptAllocationDetail allocate : allocationList) {
			if (RepayConstants.ALLOCATION_EMI.equals(allocate.getAllocationType())) {
				BigDecimal[] emisplit = null;
				if (allocate.getTotalDue().compareTo(allocate.getPaidAmount()) == 0) {
					emisplit = getCompEmiSplit(receiptData);
				} else {
					emisplit = getEmiSplit(receiptData, allocate.getPaidAmount());
				}
				allocate.setTotalPaid(emisplit[0].add(emisplit[2]));
				for (ReceiptAllocationDetail alloc : rch.getAllocations()) {
					if (RepayConstants.ALLOCATION_PFT.equals(alloc.getAllocationType())) {
						alloc.setTotalPaid(emisplit[1]);
						alloc.setPaidAmount(emisplit[2]);
						alloc.setTdsPaid(emisplit[1].subtract(emisplit[2]));
					} else if (RepayConstants.ALLOCATION_PRI.equals(alloc.getAllocationType())) {
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
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocationList = rch.getAllocations();
		BigDecimal totalReceiptAmount = amount;
		if (totalReceiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}
		for (ReceiptAllocationDetail allocate : allocationList) {
			String allocationType = allocate.getAllocationType();

			if (RepayConstants.ALLOCATION_PFT.equals(allocationType)
					|| RepayConstants.ALLOCATION_PRI.equals(allocationType)) {
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

			// allocate.setTotalPaid(allocate.getTotalPaid().add(payNow));
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
		FinanceDetail fd = receiptData.getFinanceDetail();

		for (ReceiptAllocationDetail allocate : allocationList) {
			BigDecimal payNow = BigDecimal.ZERO;
			BigDecimal waiveNow = BigDecimal.ZERO;
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
				calAllocationWaiverGST(fd, waiveNow, allocate);
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

			updateAllocationWithTds(allocate, payNow, waiveNow, fd, BigDecimal.ZERO);

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

	private FinReceiptData updateFinFeeDetails(FinReceiptData rd, ReceiptAllocationDetail allocate) {
		List<FinFeeDetail> feeDtls = rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isNotEmpty(feeDtls)) {
			FinanceMain fm = rd.getFinanceDetail().getFinScheduleData().getFinanceMain();
			for (FinFeeDetail feeDtl : feeDtls) {
				if (allocate.getAllocationTo() == -(feeDtl.getFeeTypeID())) {
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
						feeDtl.setPaidAmountOriginal(
								allocate.getPaidAmount().add(allocate.getTdsPaid()).subtract(allocate.getPaidGST()));
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

			Map<String, BigDecimal> map = GSTCalculator.getTaxPercentages(fm.getCustID(), fm.getFinCcy(), null,
					fm.getFinBranch(), rd.getFinanceDetail().getFinanceTaxDetail());
			feeCalculator.calculateFeeDetail(rd, map);
		}

		return rd;
	}

	public BigDecimal getPaidAmount(ReceiptAllocationDetail allocate, BigDecimal netAmount) {
		setSMTParms(new EventProperties());
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal totalPerc = allocate.getPercCGST().add(allocate.getPercSGST())
				.add(allocate.getPercUGST().add(allocate.getPercIGST())).add(allocate.getPercCESS())
				.subtract(allocate.getPercTds());
		BigDecimal percMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).add(totalPerc), 20,
				RoundingMode.HALF_DOWN);
		paidAmount = netAmount.multiply(percMultiplier);
		paidAmount = CalculationUtil.roundAmount(paidAmount, tdsRoundMode, tdsRoundingTarget);
		return paidAmount;
	}

	public BigDecimal getPaidAmountAbs(ReceiptAllocationDetail allocate, BigDecimal netAmount) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		paidAmount = allocate.getTotalDue().subtract(allocate.getDueGST().subtract(allocate.getTdsDue()));
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

		movement.setPaidCGST(allocate.getPaidCGST());
		movement.setPaidSGST(allocate.getPaidSGST());
		movement.setPaidIGST(allocate.getPaidIGST());
		movement.setPaidCESS(allocate.getPaidCESS());

		movement.setWaivedCGST(allocate.getWaivedCGST());
		movement.setWaivedSGST(allocate.getWaivedSGST());
		movement.setWaivedIGST(allocate.getWaivedIGST());
		movement.setWaivedCESS(allocate.getWaivedCESS());

		// CESS Calculations
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

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
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

	private FinReceiptData scheduleApportion(FinReceiptData rd, boolean isPresentment) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		Date valueDate = rch.getValueDate();

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schData = fd.getFinScheduleData();
		List<FinanceScheduleDetail> schedules = schData.getFinanceScheduleDetails();
		setReceiptCategory(rch.getReceiptPurpose());

		// Penal after schedule collection OR along
		/* String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy(); */

		FinanceMain fm = schData.getFinanceMain();
		String finReference = fm.getFinReference();
		EventProperties eventProperties = fm.getEventProperties();
		String repayHierarchy = getRepayHierarchyOnNPA(finReference, eventProperties);

		if ("".equals(repayHierarchy)) {
			repayHierarchy = schData.getFinanceType().getRpyHierarchy();
		}

		if (repayHierarchy.contains("CS")) {
			rch.setPenalSeparate(true);
		} else {
			rch.setPenalSeparate(false);
		}

		char[] rpyOrder = repayHierarchy.replace("CS", "").toCharArray();
		setSMTParms(eventProperties);

		// Load Pending Schedules until balance available for payment
		for (int i = 1; i < schedules.size(); i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);
			Date schdDate = curSchd.getSchDate();
			if (schdDate.compareTo(valueDate) > 0) {
				break;
			}

			// If Presentment Process, No other schedule should be effected.
			if (ImplementationConstants.ALLOW_OLDEST_DUE) {
				// Adjust Oldest Dues first
			} else {
				if (isPresentment && (DateUtility.compare(valueDate, schdDate) != 0)) {
					continue;
				}
			}

			rch.setSchdIdx(i);
			rd = calApportion(rpyOrder, rd);

			// No more Receipt amount left for next schedules
			if (!rd.isAdjSchedule()) {
				if (rd.getReceiptHeader().getBalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}
		}

		return rd;
	}

	private String getRepayHierarchyOnNPA(String finReference, EventProperties eventProperties) {
		String repayHierarchy = "";
		String npaRepayHierarchy = null;
		int dpdbucket = 0;
		boolean alwDiffRepayOnNpa = false;
		if (eventProperties.isParameterLoaded()) {
			npaRepayHierarchy = eventProperties.getNpaRepayHierarchy();
			dpdbucket = eventProperties.getDpdBucket();
			alwDiffRepayOnNpa = eventProperties.isAlwDiffRepayOnNpa();
		} else {
			npaRepayHierarchy = SysParamUtil.getValueAsString(SMTParameterConstants.RPYHCY_ON_NPA);
			dpdbucket = SysParamUtil.getValueAsInt(SMTParameterConstants.RPYHCY_ON_DPD_BUCKET);
			alwDiffRepayOnNpa = SysParamUtil.isAllowed(SMTParameterConstants.ALW_DIFF_RPYHCY_NPA);
		}

		if (ImplementationConstants.ALLOW_NPA_PROVISION) {
			if (StringUtils.trimToNull(npaRepayHierarchy) != null) {
				boolean isNPARepayHierarchyReq = npaService.isNAPRepayHierarchyReq(finReference);
				if (isNPARepayHierarchyReq) {
					repayHierarchy = npaRepayHierarchy;
				}
			}
		} else {
			if (alwDiffRepayOnNpa) {
				// FIXME:Currently NPA is updating on EOD, so for temporary purpose
				// we are changing repay hierarchy on basis of DPD.
				int dueBucket = financeScheduleDetailDAO.getDueBucket(finReference);
				if (dueBucket >= dpdbucket) {
					repayHierarchy = npaRepayHierarchy;
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
				// Common issue 16
				if (receiptData.isAdjSchedule() && receiptPurposeCtg == 2 && rch.getPftIdx() != -1
						&& rch.getFutPftIdx() != -1 && rch.getAllocations().get(rch.getPftIdx()).getPaidAvailable()
								.compareTo(BigDecimal.ZERO) <= 0) {
					receiptData = repayIntApportion(receiptData);
				}
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
			if (RepayConstants.ALLOCATION_PFT.equals(alloc.getAllocationType())) {
				emiSplit[2] = alloc.getTotalDue();
				emiSplit[1] = alloc.getTotalDue().add(alloc.getTdsDue());
			} else if (RepayConstants.ALLOCATION_PRI.equals(alloc.getAllocationType())) {
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
		FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();

		for (int i = 0; i < schData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail schd = schData.getFinanceScheduleDetails().get(i);
			if (DateUtil.compare(schd.getSchDate(), rch.getValueDate()) == 0) {
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
			// rph.setPartialPaidAmount(rph.getPartialPaidAmount().add(paidNow));
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		if (curSchd.isTDSApplicable()) {
			tds = getTDS(fm, curSchd.getProfitSchd());
			tds = tds.subtract(curSchd.getTDSPaid());
		}

		nBalPft = balPft.subtract(tds);

		if (balPft.compareTo(tds) <= 0) {
			nBalPft = balPft;
		}

		if (allocatePft.getWaivedAvailable().compareTo(nBalPft) > 0) {
			npftWaived = nBalPft;
		} else {
			npftWaived = allocatePft.getWaivedAvailable();
		}

		nBalPft = nBalPft.subtract(npftWaived);
		if (curSchd.isTDSApplicable()) {
			tdsWaived = getNetOffTDS(fm, npftWaived).subtract(npftWaived);
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
					BigDecimal pftNow = getNetOffTDS(finScheduleData.getFinanceMain(), balAmount);
					tdsPaidNow = pftNow.subtract(balAmount);

					if (tdsPaidNow.compareTo(BigDecimal.ZERO) <= 0) {
						tdsPaidNow = BigDecimal.ZERO;
					}
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

	public FinReceiptData lateIntApportion(FinReceiptData rd) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		// LPI Not exits
		if (rch.getLpiIdx() < 0) {
			return rd;
		}

		BigDecimal balAmount = BigDecimal.ZERO;

		int schdIdx = rch.getSchdIdx();
		FinScheduleData schData = rd.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail schd = schData.getFinanceScheduleDetails().get(schdIdx);

		List<ReceiptAllocationDetail> allocationList = rd.getReceiptHeader().getAllocations();
		ReceiptAllocationDetail allocate = allocationList.get(rch.getLpiIdx());

		FinRepayHeader rph = null;
		if (rd.isAdjSchedule()) {
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
		List<FinODDetails> finODDetails = schData.getFinODDetails();
		for (int i = 0; i < finODDetails.size(); i++) {
			FinODDetails fod = finODDetails.get(i);

			if (fod.getFinODSchdDate().compareTo(schd.getSchDate()) == 0) {
				finOd = finODDetails.get(i);
				rch.setOdIdx(i);
				balLPI = fod.getLPIBal();
				isLPIFound = true;
				break;
			}

			if (fod.getFinODSchdDate().compareTo(schd.getSchDate()) > 0) {
				break;
			}
		}

		if (!isLPIFound || balLPI.compareTo(BigDecimal.ZERO) <= 0) {
			return rd;
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
		updateAllocation(allocate, paidNow, waivedNow, BigDecimal.ZERO, BigDecimal.ZERO, rd.getFinanceDetail());

		if (rd.isAdjSchedule() && paidNow.add(waivedNow).compareTo(BigDecimal.ZERO) > 0) {
			rph.setRepayAmount(rph.getRepayAmount().add(paidNow));
			rph.setLpiAmount(rph.getLpiAmount().add(paidNow));
			rph.setTotalWaiver(rph.getTotalWaiver().add(waivedNow));
			rd = updateRPS(rd, allocate, "LPI");
			allocate.setPaidNow(BigDecimal.ZERO);
			allocate.setWaivedNow(BigDecimal.ZERO);
		}
		if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
			rch.setBalAmount(rch.getBalAmount().subtract(paidNow));
		}
		return rd;
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

		FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();

		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		ReceiptAllocationDetail allocate = allocationList.get(rch.getLppIdx());
		if (allocate.getTotalDue().compareTo(BigDecimal.ZERO) <= 0) {
			return receiptData;
		}

		Date valueDate = rch.getValueDate();

		FinRepayHeader rph = null;

		List<FinODDetails> finODDetails = schData.getFinODDetails();
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

			// allocate.setWaivedAvailable(allocate.getWaivedAvailable().subtract(odWaiveNow));
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
			// always we are taking the inclusive type here because we are doing reverse calculation here
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
			// allocate.setWaivedGST(taxSplit.gettGST());
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

	public FinReceiptData updateRPS(FinReceiptData receiptData, ReceiptAllocationDetail alloc, String updFor) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		int schdIdx = rch.getSchdIdx();
		int odIdx = rch.getOdIdx();
		FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceScheduleDetail schd = schData.getFinanceScheduleDetails().get(schdIdx);
		FinRepayHeader rph = rch.getReceiptDetails().get(rcdIdx).getRepayHeader();
		List<RepayScheduleDetail> rpsList = rph.getRepayScheduleDetails();
		RepayScheduleDetail rps = new RepayScheduleDetail();

		Date schdDate = schd.getSchDate();
		boolean isRPSFound = false;
		boolean isOd = false;

		if (StringUtils.equals(updFor, "LPI") || StringUtils.equals(updFor, "LPP")) {
			schdDate = schData.getFinODDetails().get(odIdx).getFinODSchdDate();
			isOd = true;
		}

		// Find schedule Existence in the Repayment Schedule Record
		for (RepayScheduleDetail rsd : rpsList) {
			if (rsd.getSchDate().compareTo(schdDate) == 0) {
				rps = rsd;
				isRPSFound = true;
				break;
			}
		}

		// If RPS not found set RPS data from schedule record
		if (!isRPSFound) {
			if (isOd) {
				rps = setODRPSData(receiptData, schData.getFinODDetails().get(odIdx));
			} else {
				rps = setRPSData(receiptData, schd);
			}
		}

		// Update RPH, RPS, SCHEDULE
		// RPS
		switch (updFor) {
		case "PRI":
			rps.setPrincipalSchdPayNow(rps.getPrincipalSchdPayNow().add(alloc.getPaidNow()));
			rps.setPriSchdWaivedNow(rps.getPriSchdWaivedNow().add(alloc.getWaivedNow()));
			schd.setSchdPriPaid(schd.getSchdPriPaid().add(alloc.getPaidNow().add(alloc.getWaivedNow())));

			break;
		case "INT":
			rps.setProfitSchdPayNow(rps.getProfitSchdPayNow().add(alloc.getPaidNow().add(alloc.getTdsPaidNow())));
			rps.setPftSchdWaivedNow(rps.getPftSchdWaivedNow().add(alloc.getWaivedNow().add(alloc.getTdsWaivedNow())));
			schd.setSchdPftPaid(schd.getSchdPftPaid().add(alloc.getPaidNow().add(alloc.getWaivedNow())
					.add(alloc.getTdsPaidNow()).add(alloc.getTdsWaivedNow())));
			rps.setTdsSchdPayNow(alloc.getTdsPaidNow().add(alloc.getTdsWaivedNow()));
			schd.setTDSPaid(schd.getTDSPaid().add(alloc.getTdsPaidNow().add(alloc.getTdsWaivedNow())));

			break;
		case "LPI":
			rps.setLatePftSchdPayNow(alloc.getPaidNow());
			rps.setLatePftSchdWaivedNow(alloc.getWaivedNow());

			break;
		case "LPP":
			// if Exclusive type GST is there we have to subtract the gst amount
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(alloc.getTaxType())) {
				rps.setPenaltyPayNow(alloc.getPaidNow().add(alloc.getTdsPaidNow()).subtract(alloc.getPaidGST()));
				if (alloc.getWaivedNow().compareTo(BigDecimal.ZERO) > 0) {
					rps.setWaivedAmt(alloc.getWaivedNow().subtract(alloc.getWaivedGST()));
				}
			} else {
				rps.setPenaltyPayNow(alloc.getPaidNow().add(alloc.getTdsPaidNow()));
				rps.setWaivedAmt(alloc.getWaivedNow());
			}
			rps.setWaivedAmt(alloc.getWaivedNow().add(alloc.getTdsWaivedNow()));

			// GST Calculations
			TaxHeader taxHeader = alloc.getTaxHeader();
			if (taxHeader != null) {
				rps.setTaxHeader(taxHeader.copyEntity());
			}

			break;
		default:
			break;
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

		for (FinODDetails fod : fodList) {
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
		FinScheduleData schData = financeDetail.getFinScheduleData();
		FinanceMain fm = schData.getFinanceMain();

		Map<String, Object> dataMap = GSTCalculator.getGSTDataMap(fm.getFinReference());

		schData.setGstExecutionMap(dataMap);
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

		ReceiptAllocationDetail alloc = new ReceiptAllocationDetail();
		List<XcessPayables> xcessPayables = receiptData.getReceiptHeader().getXcessPayables();

		for (XcessPayables payable : xcessPayables) {
			alloc.setDueAmount(alloc.getDueAmount().add(payable.getAmount()));
			alloc.setDueGST(alloc.getDueGST().add(payable.getGstAmount()));
			alloc.setTotalDue(alloc.getTotalDue().add(payable.getAvailableAmt()));
			alloc.setPaidAmount(alloc.getPaidAmount().add(payable.getTotPaidNow()).subtract(payable.getPaidGST()));
			alloc.setPaidGST(alloc.getPaidGST().add(payable.getPaidGST()));
			alloc.setPaidCGST(alloc.getPaidCGST().add(payable.getPaidCGST()));
			alloc.setPaidSGST(alloc.getPaidSGST().add(payable.getPaidSGST()));
			alloc.setPaidIGST(alloc.getPaidIGST().add(payable.getPaidIGST()));
			alloc.setPaidUGST(alloc.getPaidUGST().add(payable.getPaidUGST()));
			alloc.setPaidCESS(alloc.getPaidCESS().add(payable.getPaidCESS()));
			alloc.setTotalPaid(alloc.getTotalPaid().add(payable.getTotPaidNow()));
			alloc.setBalance(alloc.getTotalDue().subtract(alloc.getTotalPaid()));
		}

		receiptData.getReceiptHeader().setTotalXcess(alloc);
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
		List<ReceiptAllocationDetail> allocations = receiptData.getReceiptHeader().getAllocations();

		if (CollectionUtils.isEmpty(allocations)) {
			return receiptData;
		}

		// To set summary by due categories
		ReceiptAllocationDetail totalPastDues = new ReceiptAllocationDetail();
		ReceiptAllocationDetail totalAdvises = new ReceiptAllocationDetail();
		ReceiptAllocationDetail totalFees = new ReceiptAllocationDetail();
		ReceiptAllocationDetail totalBounces = new ReceiptAllocationDetail();

		List<ReceiptAllocationDetail> allocationList = allocations;
		List<ReceiptAllocationDetail> allocSummary = new ArrayList<ReceiptAllocationDetail>(1);
		for (ReceiptAllocationDetail rad : allocationList) {

			boolean exclusiveTax = false;
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(rad.getTaxType())) {
				exclusiveTax = true;
			}
			rad.setBalance(rad.getTotalDue().subtract(rad.getPaidAmount()).subtract(rad.getWaivedAmount()));
			allocSummary = setAllocationSummary(allocSummary, rad);

			String allocationType = rad.getAllocationType();
			if (RepayConstants.ALLOCATION_PFT.equals(allocationType)
					|| RepayConstants.ALLOCATION_PRI.equals(allocationType)) {
				continue;
			}

			if (RepayConstants.ALLOCATION_BOUNCE.equals(allocationType)) {
				totalBounces.setDueAmount(totalBounces.getDueAmount().add(rad.getDueAmount()));
				totalBounces.setDueGST(totalBounces.getDueGST().add(rad.getDueGST()));
				totalBounces.setTotalDue(totalBounces.getTotalDue().add(rad.getTotalDue()));
				totalBounces.setPaidAmount(totalBounces.getPaidAmount().add(rad.getPaidAmount()));
				totalBounces.setTotalPaid(totalBounces.getTotalPaid().add(rad.getTotalPaid()));
				totalBounces.setWaivedAmount(totalBounces.getWaivedAmount().add(rad.getWaivedAmount()));
				totalBounces.setWaivedAvailable(totalBounces.getWaivedAvailable().add(rad.getWaivedAvailable()));
				totalBounces.setBalance(totalBounces.getBalance().add(rad.getBalance()));
				totalBounces.setTdsDue(totalBounces.getTdsDue().add(rad.getTdsDue()));
				totalBounces.setTdsPaid(totalBounces.getTdsPaid().add(rad.getTdsPaid()));

				// Paid GST Amounts
				totalBounces.setPaidCGST(totalBounces.getPaidCGST().add(rad.getPaidCGST()));
				totalBounces.setPaidSGST(totalBounces.getPaidSGST().add(rad.getPaidSGST()));
				totalBounces.setPaidIGST(totalBounces.getPaidIGST().add(rad.getPaidIGST()));
				totalBounces.setPaidUGST(totalBounces.getPaidUGST().add(rad.getPaidUGST()));
				totalBounces.setPaidCESS(totalBounces.getPaidCESS().add(rad.getPaidCESS()));
				totalBounces.setPaidGST(totalBounces.getPaidGST().add(rad.getPaidGST()));

				// Waiver GST Amounts
				totalBounces.setWaivedCGST(totalBounces.getWaivedCGST().add(rad.getWaivedCGST()));
				totalBounces.setWaivedSGST(totalBounces.getWaivedSGST().add(rad.getWaivedSGST()));
				totalBounces.setWaivedIGST(totalBounces.getWaivedIGST().add(rad.getWaivedIGST()));
				totalBounces.setWaivedUGST(totalBounces.getWaivedUGST().add(rad.getWaivedUGST()));
				totalBounces.setWaivedCESS(totalBounces.getWaivedCESS().add(rad.getWaivedCESS()));

				if (exclusiveTax) {
					totalBounces.setWaivedGST(totalBounces.getWaivedGST().add(rad.getWaivedGST()));
				}

				// Prepare CESS Tax Details
				prepareTaxDetails(totalBounces, rad);

				continue;
			}

			if (rad.getAllocationTo() < 0) {
				totalFees.setDueAmount(totalFees.getDueAmount().add(rad.getDueAmount()));
				totalFees.setDueGST(totalFees.getDueGST().add(rad.getDueGST()));
				totalFees.setTotalDue(totalFees.getTotalDue().add(rad.getTotalDue()));
				totalFees.setPaidAmount(totalFees.getPaidAmount().add(rad.getPaidAmount()));
				totalFees.setTotalPaid(totalFees.getTotalPaid().add(rad.getTotalPaid()));
				totalFees.setWaivedAmount(totalFees.getWaivedAmount().add(rad.getWaivedAmount()));
				totalFees.setWaivedAvailable(totalFees.getWaivedAvailable().add(rad.getWaivedAvailable()));
				totalFees.setBalance(totalFees.getBalance().add(rad.getBalance()));
				totalFees.setTdsDue(totalFees.getTdsDue().add(rad.getTdsDue()));
				totalFees.setTdsPaid(totalFees.getTdsPaid().add(rad.getTdsPaid()));

				// Paid GST Amounts
				totalFees.setPaidCGST(totalFees.getPaidCGST().add(rad.getPaidCGST()));
				totalFees.setPaidSGST(totalFees.getPaidSGST().add(rad.getPaidSGST()));
				totalFees.setPaidIGST(totalFees.getPaidIGST().add(rad.getPaidIGST()));
				totalFees.setPaidUGST(totalFees.getPaidUGST().add(rad.getPaidUGST()));
				totalFees.setPaidCESS(totalFees.getPaidCESS().add(rad.getPaidCESS()));
				totalFees.setPaidGST(totalFees.getPaidGST().add(rad.getPaidGST()));

				// Waiver GST Amounts
				totalFees.setWaivedCGST(totalFees.getWaivedCGST().add(rad.getWaivedCGST()));
				totalFees.setWaivedSGST(totalFees.getWaivedSGST().add(rad.getWaivedSGST()));
				totalFees.setWaivedIGST(totalFees.getWaivedIGST().add(rad.getWaivedIGST()));
				totalFees.setWaivedUGST(totalFees.getWaivedUGST().add(rad.getWaivedUGST()));
				totalFees.setWaivedCESS(totalFees.getWaivedCESS().add(rad.getWaivedCESS()));
				if (exclusiveTax && rad.getWaivedGST().compareTo(BigDecimal.ZERO) > 0) {
					totalFees.setWaivedGST(totalFees.getWaivedGST().add(rad.getWaivedGST()));
				}
				// Prepare CESS Tax Details
				prepareTaxDetails(totalFees, rad);

			} else if (rad.getAllocationTo() == 0) {
				totalPastDues.setDueAmount(totalPastDues.getDueAmount().add(rad.getDueAmount()));
				totalPastDues.setDueGST(totalPastDues.getDueGST().add(rad.getDueGST()));
				totalPastDues.setTotalDue(totalPastDues.getTotalDue().add(rad.getTotalDue()));
				totalPastDues.setPaidAmount(totalPastDues.getPaidAmount().add(rad.getPaidAmount()));
				totalPastDues.setTotalPaid(totalPastDues.getTotalPaid().add(rad.getTotalPaid()));
				totalPastDues.setWaivedAmount(totalPastDues.getWaivedAmount().add(rad.getWaivedAmount()));
				totalPastDues.setWaivedAvailable(totalPastDues.getWaivedAvailable().add(rad.getWaivedAvailable()));
				totalPastDues.setBalance(totalPastDues.getBalance().add(rad.getBalance()));
				totalPastDues.setTdsDue(totalPastDues.getTdsDue().add(rad.getTdsDue()));
				totalPastDues.setTdsPaid(totalPastDues.getTdsPaid().add(rad.getTdsPaid()));

				// Paid GST Amounts
				totalPastDues.setPaidCGST(totalPastDues.getPaidCGST().add(rad.getPaidCGST()));
				totalPastDues.setPaidSGST(totalPastDues.getPaidSGST().add(rad.getPaidSGST()));
				totalPastDues.setPaidIGST(totalPastDues.getPaidIGST().add(rad.getPaidIGST()));
				totalPastDues.setPaidUGST(totalPastDues.getPaidUGST().add(rad.getPaidUGST()));
				totalPastDues.setPaidGST(totalPastDues.getPaidGST().add(rad.getPaidGST()));
				totalPastDues.setPaidCESS(totalPastDues.getPaidCESS().add(rad.getPaidCESS()));

				// Waiver GST Amounts
				totalPastDues.setWaivedCGST(totalPastDues.getWaivedCGST().add(rad.getWaivedCGST()));
				totalPastDues.setWaivedSGST(totalPastDues.getWaivedSGST().add(rad.getWaivedSGST()));
				totalPastDues.setWaivedIGST(totalPastDues.getWaivedIGST().add(rad.getWaivedIGST()));
				totalPastDues.setWaivedUGST(totalPastDues.getWaivedUGST().add(rad.getWaivedUGST()));
				totalPastDues.setWaivedCESS(totalPastDues.getWaivedCESS().add(rad.getWaivedCESS()));
				if (exclusiveTax) {
					totalPastDues.setWaivedGST(totalPastDues.getWaivedGST().add(rad.getWaivedGST()));
				}
				// Prepare CESS Tax Details
				prepareTaxDetails(totalPastDues, rad);

			} else if (rad.getAllocationTo() > 0) {
				totalAdvises.setDueAmount(totalAdvises.getDueAmount().add(rad.getDueAmount()));
				totalAdvises.setDueGST(totalAdvises.getDueGST().add(rad.getDueGST()));
				totalAdvises.setTotalDue(totalAdvises.getTotalDue().add(rad.getTotalDue()));
				totalAdvises.setPaidAmount(totalAdvises.getPaidAmount().add(rad.getPaidAmount()));
				totalAdvises.setTotalPaid(totalAdvises.getTotalPaid().add(rad.getTotalPaid()));
				totalAdvises.setWaivedAmount(totalAdvises.getWaivedAmount().add(rad.getWaivedAmount()));
				totalAdvises.setWaivedAvailable(totalAdvises.getWaivedAvailable().add(rad.getWaivedAvailable()));
				totalAdvises.setBalance(totalAdvises.getBalance().add(rad.getBalance()));
				totalAdvises.setTdsDue(totalPastDues.getTdsDue().add(rad.getTdsDue()));
				totalAdvises.setTdsPaid(totalPastDues.getTdsPaid().add(rad.getTdsPaid()));

				// Paid GST Amounts
				totalAdvises.setPaidCGST(totalAdvises.getPaidCGST().add(rad.getPaidCGST()));
				totalAdvises.setPaidSGST(totalAdvises.getPaidSGST().add(rad.getPaidSGST()));
				totalAdvises.setPaidIGST(totalAdvises.getPaidIGST().add(rad.getPaidIGST()));
				totalAdvises.setPaidUGST(totalAdvises.getPaidUGST().add(rad.getPaidUGST()));
				totalAdvises.setPaidCESS(totalAdvises.getPaidCESS().add(rad.getPaidCESS()));
				totalAdvises.setPaidGST(totalAdvises.getPaidGST().add(rad.getPaidGST()));

				// Waiver GST Amounts
				totalAdvises.setWaivedCGST(totalAdvises.getWaivedCGST().add(rad.getWaivedCGST()));
				totalAdvises.setWaivedSGST(totalAdvises.getWaivedSGST().add(rad.getWaivedSGST()));
				totalAdvises.setWaivedIGST(totalAdvises.getWaivedIGST().add(rad.getWaivedIGST()));
				totalAdvises.setWaivedUGST(totalAdvises.getWaivedUGST().add(rad.getWaivedUGST()));
				totalAdvises.setWaivedCESS(totalAdvises.getWaivedCESS().add(rad.getWaivedCESS()));

				// Prepare CESS Tax Details
				prepareTaxDetails(totalAdvises, rad);

				if (exclusiveTax) {
					totalAdvises.setWaivedGST(totalAdvises.getWaivedGST().add(rad.getWaivedGST()));
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

	private void prepareTaxDetails(ReceiptAllocationDetail rad, ReceiptAllocationDetail allocation) {
		TaxHeader header = allocation.getTaxHeader();
		if (header != null && CollectionUtils.isNotEmpty(header.getTaxDetails())) {
			TaxHeader taxHeader = new TaxHeader();
			rad.setTaxHeader(taxHeader);
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
		ReceiptAllocationDetail tempAlloc = allocation.copyEntity();

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

	public FinReceiptData splitAllocSummary(FinReceiptData rd, int idx) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		ReceiptAllocationDetail sumAlloc = rch.getAllocationsSummary().get(idx);
		BigDecimal paidForAdjustment = sumAlloc.getPaidAmount();
		BigDecimal waivedForAdjustment = sumAlloc.getWaivedAmount();
		String allocType = sumAlloc.getAllocationType();
		String feeTypeCode = sumAlloc.getFeeTypeCode();
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> radList = rch.getAllocations();

		for (ReceiptAllocationDetail rad : radList) {
			if (!allocType.equals(rad.getAllocationType()) || !feeTypeCode.equals(rad.getFeeTypeCode())) {
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

				calAllocationGST(rd.getFinanceDetail(), paidNow, rad, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

				paidForAdjustment = paidForAdjustment.subtract(paidNow);
			} else {
				paidNow = paidForAdjustment;
				rad.setTotalPaid(paidNow);
				rad.setPaidAmount(paidNow);

				calAllocationGST(rd.getFinanceDetail(), paidNow, rad, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

				paidForAdjustment = BigDecimal.ZERO;

			}
		}

		rch.setAllocations(radList);
		return rd;
	}

	public FinReceiptData splitNetAllocSummary(FinReceiptData rd, int idx) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		ReceiptAllocationDetail sumAlloc = rch.getAllocationsSummary().get(idx);
		BigDecimal paidForAdjustment = sumAlloc.getPaidAmount();
		BigDecimal waivedForAdjustment = sumAlloc.getWaivedAmount();
		String allocType = sumAlloc.getAllocationType();
		String feeTypeCode = sumAlloc.getFeeTypeCode();
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal waivedNow = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> radList = rch.getAllocations();

		for (ReceiptAllocationDetail rad : radList) {
			if (!allocType.equals(rad.getAllocationType()) || !feeTypeCode.equals(rad.getFeeTypeCode())) {
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

				calAllocationGST(rd.getFinanceDetail(), paidNow, rad, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);

				BigDecimal tdsPaidNow = BigDecimal.ZERO;
				if (rad.isTdsReq()) {
					tdsPaidNow = getTDSAmount(rd.getFinanceDetail().getFinScheduleData().getFinanceMain(), totalPaid);
					rad.setTdsPaid(tdsPaidNow);
					rad.setTotalPaid(paidNow.add(tdsPaidNow));
				}

				paidForAdjustment = paidForAdjustment.subtract(paidNow);
			} else {
				paidNow = paidForAdjustment;
				rad.setTotalPaid(paidNow);
				rad.setPaidAmount(paidNow);

				BigDecimal totalPaid = getPaidAmount(rad, paidNow);

				calAllocationGST(rd.getFinanceDetail(), paidNow, rad, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);

				BigDecimal tdsPaidNow = BigDecimal.ZERO;
				if (rad.isTdsReq()) {
					tdsPaidNow = getTDSAmount(rd.getFinanceDetail().getFinScheduleData().getFinanceMain(), totalPaid);
					rad.setTdsPaid(tdsPaidNow);
					rad.setTotalPaid(paidNow.add(tdsPaidNow));
				}

				paidForAdjustment = BigDecimal.ZERO;

			}
		}
		rch.setAllocations(radList);
		return rd;
	}

	/**
	 * GST Calculation for Allocation
	 * 
	 * @param fd
	 * @param paidNow
	 * @param allocation
	 */
	public void calAllocationGST(FinanceDetail fd, BigDecimal paidNow, ReceiptAllocationDetail allocation,
			String taxType) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(allocation.getTaxType())) {
			return;
		}

		calAllocationPaidGST(fd, paidNow, allocation, taxType);
		calAllocationWaiverGST(fd, allocation.getWaivedAmount(), allocation);

		logger.debug(Literal.LEAVING);
	}

	public void calAllocationPaidGST(FinanceDetail financeDetail, BigDecimal paidAmount,
			ReceiptAllocationDetail allocation, String taxType) {
		if (StringUtils.isBlank(taxType)) {
			return;
		}

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

		if (allocation.getTaxHeader() == null) {
			return;
		}

		List<Taxes> taxDetails = allocation.getTaxHeader().getTaxDetails();
		if (CollectionUtils.isEmpty(taxDetails)) {
			return;
		}
		for (Taxes tax : taxDetails) {
			switch (tax.getTaxType()) {
			case RuleConstants.CODE_CGST:
				tax.setPaidTax(taxSplit.getcGST());
				break;
			case RuleConstants.CODE_SGST:
				tax.setPaidTax(taxSplit.getsGST());
				break;
			case RuleConstants.CODE_UGST:
				tax.setPaidTax(taxSplit.getuGST());
				break;
			case RuleConstants.CODE_IGST:
				tax.setPaidTax(taxSplit.getiGST());
				break;
			case RuleConstants.CODE_CESS:
				tax.setPaidTax(taxSplit.getCess());
				break;
			default:
				break;
			}

			tax.setRemFeeTax(tax.getNetTax().subtract(tax.getPaidTax()));
		}
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

		if (StringUtils.isBlank(allocation.getTaxType())) {
			return;
		}

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

		if (allocation.getTaxHeader() == null) {
			return;
		}
		List<Taxes> taxDetails = allocation.getTaxHeader().getTaxDetails();

		if (CollectionUtils.isEmpty(taxDetails)) {
			return;
		}
		for (Taxes tax : taxDetails) {

			switch (tax.getTaxType()) {
			case RuleConstants.CODE_CGST:
				tax.setWaivedTax(taxSplit.getcGST());
				break;
			case RuleConstants.CODE_SGST:
				tax.setWaivedTax(taxSplit.getsGST());
				break;
			case RuleConstants.CODE_UGST:
				tax.setWaivedTax(taxSplit.getuGST());
				break;
			case RuleConstants.CODE_IGST:
				tax.setWaivedTax(taxSplit.getiGST());
				break;
			case RuleConstants.CODE_CESS:
				tax.setWaivedTax(taxSplit.getCess());
				break;
			default:
				break;
			}

			tax.setNetTax(tax.getActualTax().subtract(tax.getWaivedTax()));
		}
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
			if (allocType.equals(inProcRad.getAllocationType())
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

		for (ReceiptAllocationDetail rad : inProcRadList) {
			if (allocType.equals(rad.getAllocationType()) && allacationTo == rad.getAllocationTo()) {
				inProcAmount = rad.getPaidGST();
				break;
			}
		}

		return inProcAmount;
	}

	public FinReceiptData removeUnwantedManAloc(FinReceiptData rd) {
		List<ReceiptAllocationDetail> radList = rd.getInProcRadList();

		if (CollectionUtils.isEmpty(radList)) {
			return rd;
		}

		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail inProcRad = radList.get(i);
			if (StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_PFT)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_TDS)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_NPFT)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_PRI)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_FUT_TDS)
					|| StringUtils.equals(inProcRad.getAllocationType(), RepayConstants.ALLOCATION_FUT_PFT)) {
				radList.remove(i);
				i = i - 1;
				continue;
			}
		}

		return rd;
	}

	// Prepare Allocation List
	public List<ReceiptAllocationDetail> prepareAlocList(FinScheduleData schData) {
		logger.debug(Literal.ENTERING);

		List<ReceiptAllocationDetail> radList = new ArrayList<>();
		String repayHierarchy = schData.getFinanceType().getRpyHierarchy();

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
				radList.add(ad);
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
						radList.add(ad);
					} else {
						ad.setAllocationType(RepayConstants.ALLOCATION_LPFT);
						ad.setAllocationID(seqID);
						seqID = seqID + 1;
						radList.add(ad);
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
				radList.add(ad);
				continue;
			}

			// Other Fee and Charges
			if (repayTo == RepayConstants.REPAY_OTHERS) {

				List<ManualAdvise> advises = manualAdviseDAO.getManualAdviseByRef(
						schData.getFinanceMain().getFinReference(), FinanceConstants.MANUAL_ADVISE_RECEIVABLE,
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
					radList.add(ad);
				}

				// Bounce charges
				if (bounceAmt.compareTo(BigDecimal.ZERO) > 0) {
					ad = new ReceiptAllocationDetail();
					ad.setAllocationType(RepayConstants.ALLOCATION_BOUNCE);
					ad.setDueAmount(bounceAmt);
					ad.setAllocationID(seqID);
					radList.add(ad);
				}
			}
		}

		logger.debug("Leaving");
		return radList;

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
		if (allocate.getTdsPaid().compareTo(BigDecimal.ZERO) <= 0) {
			allocate.setTdsPaid(BigDecimal.ZERO);
		}
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

			if (allocate.getTdsPaid().compareTo(BigDecimal.ZERO) <= 0) {
				allocate.setTdsPaid(BigDecimal.ZERO);
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

		BigDecimal paidAmount = BigDecimal.ZERO;
		if (allocate.getTotalDue().compareTo(paidNow) == 0) {
			paidAmount = getPaidAmountAbs(allocate, paidNow);
		} else {
			paidAmount = getPaidAmount(allocate, paidNow);
		}
		// GST calculation for Paid and waived amounts(always Paid Amount we are
		// taking the inclusive type here because we are doing reverse
		// calculation here)

		BigDecimal waivedAmount = BigDecimal.ZERO;

		if (allocate.getTotalDue().compareTo(waivedNow) == 0) {
			waivedAmount = getPaidAmountAbs(allocate, waivedNow);
		} else {
			waivedAmount = getPaidAmount(allocate, waivedNow);
		}
		if (allocate.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
			if (paidNow.compareTo(BigDecimal.ZERO) > 0) {
				calAllocationPaidGST(detail, paidAmount, allocate, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
			}
			if (waivedNow.compareTo(BigDecimal.ZERO) > 0) {
				// String taxType= allocate.getTaxType();
				allocate.setTaxType(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
				calAllocationWaiverGST(detail, waivedAmount, allocate);
				// allocate.setTaxType(taxType);
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

	public void setSMTParms(EventProperties eventProperties) {
		if (eventProperties.isParameterLoaded()) {
			taxRoundMode = eventProperties.getTaxRoundMode();
			taxRoundingTarget = eventProperties.getTaxRoundingTarget();

			tdsRoundMode = eventProperties.getTdsRoundMode();
			tdsRoundingTarget = eventProperties.getTdsRoundingTarget();

			tdsPerc = eventProperties.getTdsPerc();
			tdsMultiplier = big100.divide(big100.subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
		} else {
			if (StringUtils.isEmpty(taxRoundMode) || StringUtils.isEmpty(tdsRoundMode)) {
				taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
				taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);

				tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
				tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

				tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
				tdsMultiplier = big100.divide(big100.subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
			}
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
			// if (RepayConstants.EXAMOUNTTYPE_ADVINT.equals(payable.getPayableType())) {
			excessAmount = excessAmount.add(payable.getBalanceAmt());
			// }
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		String finReference = fm.getFinReference();

		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(finReference);

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
			repayments = financeRepaymentsDAO.getFinRepayListByFinRef(finReference, false, "");
		}

		overdueList = latePayMarkingService.calPDOnBackDatePayment(fm, overdueList, valueDate, finSchdDtls, repayments,
				resetReq, true);

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

		if (receiptData.getReceiptHeader().getPresentmentSchDate() != null) {
			valueDate = receiptData.getReceiptHeader().getPresentmentSchDate();
		}

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

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

}