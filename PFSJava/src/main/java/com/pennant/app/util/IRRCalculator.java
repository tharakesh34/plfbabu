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
 * * FileName : RateCalculation.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * * Modified Date
 * : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.IRRCashFlow;
import com.pennant.backend.model.finance.IRRScheduleDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class IRRCalculator {
	private static final Logger logger = LogManager.getLogger(IRRCalculator.class);
	private static IRRFeeTypeDAO iRRFeeTypeDAO;
	private static IRRFinanceTypeDAO irrFinanceTypeDAO;

	/*
	 * Method to calculate XIRR OR IRR based requirement If isCalXIrr = TRUE then compute XIRR else compute IRR
	 */

	public static void calculateXIRRAndIRR(FinScheduleData fsData, List<FinAdvancePayments> fapList,
			boolean isCalXIrr) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();

		// XIRR && IRR
		List<IRRFinanceType> irrFT = getIrrFinanceTypeDAO().getIRRFinanceTypeByFinType(fm.getFinType(), "_AView");
		fsData.setiRRDetails(new ArrayList<FinIRRDetails>());
		List<IRRCashFlow> irrCFList = prepareScheduleCashFlow(fsData, fapList);
		BigDecimal irr = BigDecimal.ZERO;

		if (irrCFList.size() == 0) {
			return;
		}

		// Find maximum from the schedule for first guess
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		BigDecimal guessRate = fsdList.get(0).getCalculatedRate();
		for (FinanceScheduleDetail curSchd : fsdList) {
			if (guessRate.compareTo(curSchd.getCalculatedRate()) < 0) {
				guessRate = curSchd.getCalculatedRate();
			}
		}

		// Annualized IRR or XIRR
		irr = IRRCalculator.calIRRByLinearInterpolation(irrCFList, guessRate, fm, isCalXIrr);

		fm.setAnualizedPercRate(irr.setScale(9));

		// Effective Rate of Return & Multi IRR
		if (irrFT.isEmpty()) {
			return;
		}

		// ADD CASH BACK ENTRY TO THE MASTER CASH FLOW
		irrCFList = addCashBacktoFlow(fsData, irrCFList);

		// Original copy before calculating multiple IRRs
		List<IRRCashFlow> irrFeeCFList = new ArrayList<>();

		for (int iFT = 0; iFT < irrFT.size(); iFT++) {
			// Prepare a copy for different IRR Calculations
			for (IRRCashFlow cf : irrCFList) {
				irrFeeCFList.add(cf.copyEntity());
			}

			irrFeeCFList = prepareFeeCashFlow(fsData, irrFeeCFList, irrFT.get(iFT), fapList);
			irr = IRRCalculator.calIRRByLinearInterpolation(irrFeeCFList, guessRate, fm, isCalXIrr);

			if (iFT == 0) {
				fm.setEffectiveRateOfReturn(irr.setScale(9));
				prepareIRRSchedule(fsData, irrFeeCFList, isCalXIrr);
			}

			if (irrFT != null) {
				FinIRRDetails irrDetail = new FinIRRDetails();
				irrDetail.setiRRID(irrFT.get(iFT).getIRRID());
				irrDetail.setiRRCode(irrFT.get(iFT).getIrrCode());
				irrDetail.setIrrCodeDesc(irrFT.get(iFT).getIrrCodeDesc());
				irrDetail.setIRR(irr.setScale(9));
				fsData.getiRRDetails().add(irrDetail);
			}
		}

		logger.debug("Leaving");
	}

	/*
	 * Prepare Cashflow from schedule FSD
	 */
	public static List<IRRCashFlow> prepareScheduleCashFlow(FinScheduleData fsData, List<FinAdvancePayments> fapList) {
		List<IRRCashFlow> irrCFList = new ArrayList<IRRCashFlow>(1);
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		BigDecimal subvention = BigDecimal.ZERO;

		if (FinanceConstants.PRODUCT_CD.equals(fsData.getFinanceMain().getProductCategory())) {
			FinanceMain fm = fsData.getFinanceMain();
			BigDecimal totPayment = fm.getFinAmount();
			BigDecimal totDuePayment = totPayment.subtract(fm.getDownPayment());
			BigDecimal presentValue = fsdList.get(0).getClosingBalance();
			subvention = totDuePayment.subtract(presentValue);
		}

		List<FinFeeDetail> ffdList = fsData.getFinFeeDetailList();
		BigDecimal subvenFee = BigDecimal.ZERO;

		// If product category <> CD get the Subvention amount
		if (!FinanceConstants.PRODUCT_CD.equals(fsData.getFinanceMain().getProductCategory())
				&& CollectionUtils.isNotEmpty(ffdList)) {

			String subventionFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_SUBVENTION);
			// If subvention fee defined then get the subvention amount
			if (!StringUtils.isEmpty(subventionFeeCode)) {
				for (FinFeeDetail fee : ffdList) {
					if (StringUtils.equals(fee.getFeeTypeCode(), subventionFeeCode)) {
						subvention = fee.getNetAmountOriginal();
						subvenFee = fee.getNetAmountOriginal();
						break;
					}
				}
			}
		}

		boolean isBPI_ImpactOnIRR = SysParamUtil.isAllowed(SMTParameterConstants.BPI_IMPACT_ON_IRR);

		// Add Schedule cash flow record
		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			// If no change in the POS AND not even due date skip adding record
			// If no change in the POS BUT due date add record with zero cash flow. Might happen events like rate change
			// on any date
			if (fsd.getDisbAmount().compareTo(BigDecimal.ZERO) == 0
					&& fsd.getCpzAmount().compareTo(BigDecimal.ZERO) == 0
					&& fsd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {

				if (fsd.isRepayOnSchDate()) {
					IRRCashFlow irrCF = new IRRCashFlow();
					irrCF.setCfDate(fsd.getSchDate());
					irrCFList.add(irrCF);
				}

				continue;
			}

			// Whether BPI Amount include on IR calculation or not depends on System parameter
			if (!isBPI_ImpactOnIRR && StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				continue;
			}

			// Add Cash flow record
			IRRCashFlow irrCF = new IRRCashFlow();
			irrCF.setCfDate(fsd.getSchDate());
			irrCF.setCfAmount(fsd.getRepayAmount().add(fsd.getDownPaymentAmount()).subtract(fsd.getCpzAmount())
					.add(subvention).subtract(fsd.getDisbAmount()));

			irrCFList.add(irrCF);
			subvention = BigDecimal.ZERO;
		}

		// If disbursement instruction impact is not required then exit
		if (CollectionUtils.isEmpty(fapList)) {
			return irrCFList;
		}

		irrCFList = sortIRRDetails(irrCFList);

		BigDecimal initialDisbAmt = BigDecimal.ZERO;
		for (FinAdvancePayments fap : fapList) {

			if (PennantConstants.RECORD_TYPE_CAN.equals(fap.getRecordType())
					|| PennantConstants.RECORD_TYPE_DEL.equals(fap.getRecordType())) {
				continue;
			}

			if (fap.getDisbSeq() != 1) {
				continue;
			}

			if ("REJECTED".equals(fap.getStatus()) || "REVERSED".equals(fap.getStatus())
					|| "CANCELED".equals(fap.getStatus())) {
				continue;
			}

			initialDisbAmt = initialDisbAmt.add(fap.getAmtToBeReleased());
		}

		BigDecimal diffDeductedFromDisb = fsData.getFinanceMain().getFinAmount().subtract(initialDisbAmt);

		for (int i = 0; i < fapList.size(); i++) {

			FinAdvancePayments fap = fapList.get(i);
			if (PennantConstants.RECORD_TYPE_CAN.equals(fap.getRecordType())
					|| PennantConstants.RECORD_TYPE_DEL.equals(fap.getRecordType())) {
				continue;
			}

			if ("REJECTED".equals(fap.getStatus()) || "REVERSED".equals(fap.getStatus())
					|| "CANCELED".equals(fap.getStatus())) {
				continue;
			}

			boolean rcdFound = false;
			IRRCashFlow irrCF0 = irrCFList.get(0);
			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
				IRRCashFlow irrCF = irrCFList.get(iCf);

				if (DateUtil.compare(irrCF.getCfDate(), fap.getLlDate()) > 0) {
					break;
				}

				if (DateUtil.compare(irrCF.getCfDate(), fap.getLlDate()) != 0) {
					continue;
				}

				if (iCf == 0) {
					continue;
				}

				irrCF.setCfAmount(irrCF.getCfAmount().subtract(fap.getAmtToBeReleased()).add(subvenFee));
				irrCF0.setCfAmount(irrCF0.getCfAmount().add(fap.getAmtToBeReleased()).subtract(subvenFee));
				if (i == 0) {
					irrCF.setCfAmount(irrCF.getCfAmount().subtract(diffDeductedFromDisb));
					irrCF0.setCfAmount(irrCF0.getCfAmount().add(diffDeductedFromDisb));
				}

				rcdFound = true;
			}

			if (rcdFound) {
				continue;
			}

			IRRCashFlow irrCF = new IRRCashFlow();
			irrCF.setCfDate(fap.getLlDate());
			irrCF.setCfAmount(fap.getAmtToBeReleased().negate());
			irrCF.setNonSchdRcd(true);
			irrCFList.add(irrCF);
			irrCF0.setCfAmount(irrCF0.getCfAmount().add(fap.getAmtToBeReleased()));

			if (i == 0) {
				irrCF.setCfAmount(irrCF.getCfAmount().subtract(diffDeductedFromDisb));
				irrCF0.setCfAmount(irrCF0.getCfAmount().add(diffDeductedFromDisb));
			}

			irrCFList = sortIRRDetails(irrCFList);
		}

		return irrCFList;
	}

	public static List<IRRCashFlow> sortIRRDetails(List<IRRCashFlow> irrCFList) {
		if (irrCFList != null && irrCFList.size() > 0) {
			Collections.sort(irrCFList, new Comparator<IRRCashFlow>() {
				@Override
				public int compare(IRRCashFlow detail1, IRRCashFlow detail2) {
					return DateUtil.compare(detail1.getCfDate(), detail2.getCfDate());
				}
			});
		}

		return irrCFList;
	}

	public static List<IRRCashFlow> addCashBacktoFlow(FinScheduleData fsData, List<IRRCashFlow> irrCFList) {
		// TODO: Should it be changed to Product category to CD or put additional condition product category <> GOLD?
		if (fsData.getPromotion() == null) {
			return irrCFList;
		}

		int cashBackInst = fsData.getPromotion().getCashBackFromDealer();

		if (cashBackInst <= 0) {
			return irrCFList;
		}

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		BigDecimal totPayment = fm.getFinAmount();
		BigDecimal cashBack = totPayment.multiply(fsData.getPromotion().getSubventionRate())
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
		cashBack = CalculationUtil.roundAmount(cashBack, fm.getCalRoundingMode(), fm.getRoundingTarget());

		if (fsData.getPromotion().isTaxApplicable()) {
			FinanceTaxDetail finTaxDtl = new FinanceTaxDetail();
			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm.getCustID(), fm.getFinCcy(),
					fm.getUserDetails().getBranchCode(), fm.getFinBranch(), finTaxDtl);
			BigDecimal gst = GSTCalculator.getTotalGST(cashBack, taxPercentages, "I");
			cashBack = cashBack.subtract(gst);
		}

		fm.setCbAmount(cashBack);

		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (fsd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (fsd.getInstNumber() < cashBackInst) {
				continue;
			}

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
				if (irrCFList.get(iCf).getCfDate().compareTo(fsd.getSchDate()) == 0) {
					irrCFList.get(iCf).setCfAmount(irrCFList.get(iCf).getCfAmount().add(cashBack));
					break;
				}
			}

			break;
		}

		return irrCFList;
	}

	public static List<IRRCashFlow> prepareFeeCashFlow(FinScheduleData fsData, List<IRRCashFlow> irrFeeCFList,
			IRRFinanceType irrFT, List<FinAdvancePayments> fapList) {

		IRRCashFlow irrFeeCF = irrFeeCFList.get(0);

		List<FinFeeDetail> ffdList = fsData.getFinFeeDetailList();
		List<IRRFeeType> irrFeeList = null;
		if (irrFT != null) {
			irrFeeList = getiRRFeeTypeDAO().getIRRFeeTypeList(irrFT.getIRRID(), "");
		}

		if (ffdList.isEmpty() || ffdList.size() <= 0) {
			return irrFeeCFList;
		}

		BigDecimal refundFee = BigDecimal.ZERO;

		// AS OF NOW ONLY DISBURSEMENTS CONSIDERED. IN FUTURE IT SHOUD CONSIDER
		// ALL FEE RECEIPTS AND REFUNDS TOO
		for (int iFfd = 0; iFfd < ffdList.size(); iFfd++) {
			FinFeeDetail ffd = ffdList.get(iFfd);
			if (!StringUtils.equals(ffd.getFinEvent(), AccountingEvent.ADDDBSP)) {
				continue;
			}

			if (!StringUtils.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE, ffd.getFeeScheduleMethod())) {
				continue;
			}

			BigDecimal feePerc = BigDecimal.ZERO;
			boolean isRcdFound = false;
			if (irrFeeList != null) {
				for (IRRFeeType irrFeeType : irrFeeList) {
					if (irrFeeType.getFeeTypeID() == ffd.getFeeTypeID()) {
						feePerc = irrFeeType.getFeePercentage();
						isRcdFound = true;
					}
				}
			}

			if (!isRcdFound) {
				continue;
			}

			BigDecimal calFeeAmount = BigDecimal.ZERO;
			calFeeAmount = ffd.getNetAmountOriginal();
			calFeeAmount = (calFeeAmount.multiply(feePerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

			if (ffd.isRefundable()) {
				refundFee = refundFee.add(calFeeAmount);
			}

			irrFeeCF.setCfAmount(irrFeeCF.getCfAmount().add(calFeeAmount));

		}

		// Refundable Amount adding on Maturity
		if (refundFee.compareTo(BigDecimal.ZERO) > 0) {
			IRRCashFlow irrFeeMCF = irrFeeCFList.get(irrFeeCFList.size() - 1);
			irrFeeMCF.setCfAmount(irrFeeMCF.getCfAmount().subtract(refundFee));
		}

		return irrFeeCFList;
	}

	public static void prepareIRRSchedule(FinScheduleData fsData, List<IRRCashFlow> irrFeeCFList, boolean isCalXIrr) {
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		List<IRRScheduleDetail> irrSDList = new ArrayList<IRRScheduleDetail>();
		BigDecimal irr = fsData.getFinanceMain().getEffectiveRateOfReturn();

		BigDecimal fraction = BigDecimal.ZERO;
		BigDecimal interest = BigDecimal.ZERO;
		BigDecimal roundedInterest = BigDecimal.ZERO;
		BigDecimal openBalance = BigDecimal.ZERO;
		BigDecimal dayFactor360 = BigDecimal.valueOf(30).divide(BigDecimal.valueOf(360), 9, RoundingMode.HALF_DOWN);
		BigDecimal dayFactor365 = BigDecimal.ZERO;
		int days = 0;

		// Prepare Dummy Schedule
		for (int iCf = 0; iCf < irrFeeCFList.size(); iCf++) {
			IRRCashFlow irrCF = irrFeeCFList.get(iCf);
			IRRScheduleDetail irrSD = new IRRScheduleDetail();
			irrSD.setFinID(fsData.getFinanceMain().getFinID());
			irrSD.setFinReference(fsData.getFinanceMain().getFinReference());
			irrSD.setSchDate(irrCF.getCfDate());
			irrSD.setNonSchdRcd(irrCF.isNonSchdRcd());
			if (iCf == 0) {
				irrSD.setClosingBalance(BigDecimal.ZERO.subtract(irrCF.getCfAmount()));
			} else {
				irrSD.setRepayAmount(irrCF.getCfAmount());
			}
			irrSDList.add(irrSD);
		}

		int sdSize = irrSDList.size();
		int schdSize = 1;
		// Start Calculate
		for (int iSD = 1; iSD < sdSize; iSD++) {
			IRRScheduleDetail curIrrSD = irrSDList.get(iSD);
			IRRScheduleDetail prvIrrSD = irrSDList.get(iSD - 1);

			openBalance = prvIrrSD.getClosingBalance();

			if (isCalXIrr) {
				days = DateUtil.getDaysBetween(prvIrrSD.getSchDate(), curIrrSD.getSchDate());
				dayFactor365 = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 9, RoundingMode.HALF_DOWN);
				interest = CalculationUtil.calInterestWithDaysFactor(dayFactor365, openBalance, irr);
			} else {
				interest = CalculationUtil.calInterestWithDaysFactor(dayFactor360, openBalance, irr);
			}

			interest = interest.add(fraction);

			if (interest.compareTo(BigDecimal.ZERO) > 0) {
				roundedInterest = CalculationUtil.roundAmount(interest, RoundingMode.HALF_DOWN.name(), 0);
			} else {
				roundedInterest = BigDecimal.ZERO;
			}

			fraction = interest.subtract(roundedInterest);
			interest = roundedInterest;

			if (iSD == (sdSize - 1)) {
				curIrrSD.setProfitCalc(curIrrSD.getRepayAmount().subtract(openBalance));
				curIrrSD.setPrincipalCalc(openBalance);
				curIrrSD.setClosingBalance(BigDecimal.ZERO);
			} else {
				curIrrSD.setProfitCalc(roundedInterest);
				curIrrSD.setPrincipalCalc(curIrrSD.getRepayAmount().subtract(roundedInterest));
				curIrrSD.setClosingBalance(openBalance.subtract(curIrrSD.getPrincipalCalc()));
			}

			if (curIrrSD.isNonSchdRcd()) {
				curIrrSD.setGapInterst(curIrrSD.getProfitCalc());
			} else {
				FinanceScheduleDetail fsd = fsdList.get(schdSize);
				curIrrSD.setGapInterst(curIrrSD.getProfitCalc().subtract(fsd.getProfitCalc()));
				schdSize = schdSize + 1;
			}
		}

		fsData.setIrrSDList(irrSDList);
	}

	/**
	 * Method for Calculating IRR Value based on guess Rate
	 * 
	 * @param irrCFList
	 * @param guessRate
	 * @return
	 */

	// Detail documentation can be found in PLF Root Finding Algorithms
	public static BigDecimal calIRRByLinearInterpolation(List<IRRCashFlow> irrCFList, BigDecimal guessRate,
			FinanceMain fm, boolean isCalXIrr) {

		logger.debug("Entering");

		BigDecimal npvTolarance = BigDecimal.valueOf(5);

		// Formula for next approximation rate
		// NewApproximation = current IRR –(Current NPV * (Current IRR - Previous IRR)/(Current NPV- Previous NPV))

		// STEP1: First Root. First root will be get NPV at 0% rate
		BigDecimal prvIrr = BigDecimal.ZERO;
		BigDecimal prvNPV = BigDecimal.ZERO;

		for (IRRCashFlow cf : irrCFList) {
			prvNPV = prvNPV.add(cf.getCfAmount());
		}

		BigDecimal curIrr = guessRate;
		BigDecimal curNPV = BigDecimal.ZERO;

		BigDecimal newIrr = BigDecimal.ZERO;

		for (int iLoop = 0; iLoop < 50; iLoop++) {
			// STEP2: Get the Second Co-ordinates for approximation

			// Calculate NPV for current cuess IRR
			curNPV = calIRR(irrCFList, curIrr, fm, isCalXIrr);

			// Exit Loop in any of these cases
			// 1) new ABS(NPV) < tolerance value 2) New IRR Guess = Current IRR 3) New IRR Guess = Previous IRR

			if (curNPV.abs().compareTo(npvTolarance) <= 0) {
				break;
			}

			// STEP3: Find New IRR Guess
			if (curNPV.compareTo(prvNPV) != 0) {
				newIrr = curNPV.multiply((curIrr.subtract(prvIrr)));
				newIrr = newIrr.divide(curNPV.subtract(prvNPV), 9, RoundingMode.HALF_DOWN);
				newIrr = curIrr.subtract(newIrr);
			}

			// STEP4: Check if new IRR is Final guess
			if (newIrr.compareTo(BigDecimal.ZERO) == 0 || newIrr.compareTo(curIrr) == 0 || newIrr.compareTo(prvIrr) == 0
					|| curIrr.compareTo(prvIrr) == 0) {
				break;
			}

			prvIrr = curIrr;
			prvNPV = curNPV;
			curIrr = newIrr;
		}

		logger.debug("Leaving");
		return curIrr;

	}

	// Detail documentation can be found in PLF Root Finding Algorithms
	public static BigDecimal calIRR(List<IRRCashFlow> irrCFList, BigDecimal irr, FinanceMain fm, boolean isCalXIrr) {
		logger.debug("Entering");
		BigDecimal termsPerAnnum = BigDecimal.ZERO;
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal big365 = new BigDecimal(365);
		BigDecimal big360 = new BigDecimal(360);

		irr = irr.divide(big100, 13, RoundingMode.HALF_DOWN);
		if (!isCalXIrr) {
			termsPerAnnum = getTermsPerAnnum(fm.getRepayFrq());
			irr = irr.divide(termsPerAnnum, 13, RoundingMode.HALF_DOWN);
		}

		// Convert % to decimal

		BigDecimal npv = BigDecimal.ZERO;
		Date cfStartDate = irrCFList.get(0).getCfDate();
		BigDecimal pv = BigDecimal.ZERO;
		BigDecimal df = BigDecimal.ZERO;

		for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
			IRRCashFlow cf = irrCFList.get(iCf);
			if (isCalXIrr) {
				int days = DateUtil.getDaysBetween(cfStartDate, cf.getCfDate());
				df = BigDecimal.valueOf(days).divide(big365, 13, RoundingMode.HALF_DOWN);
			} else {
				int days = termsPerAnnum.intValue();
				days = days * iCf * 30;
				df = BigDecimal.valueOf(days).divide(big360, 13, RoundingMode.HALF_DOWN);
			}

			// FIXME Review Required by PV
			pv = irr.add(BigDecimal.ONE);
			double pow = Math.pow(pv.doubleValue(), df.doubleValue());

			if (Double.isNaN(pow)) {
				pow = 0;
			}

			pv = BigDecimal.valueOf(pow);
			if (pv.compareTo(BigDecimal.ZERO) > 0) {
				pv = cf.getCfAmount().divide(pv, 13, RoundingMode.HALF_DOWN);
			}
			pv = CalculationUtil.roundAmount(pv, fm.getCalRoundingMode(), fm.getRoundingTarget());

			npv = npv.add(pv);
		}

		logger.debug("Leaving");
		return npv;
	}

	public static BigDecimal getTermsPerAnnum(String frequency) {
		BigDecimal termsPerAnnum = BigDecimal.ZERO;
		String frqMonth = StringUtils.substring(frequency, 0, 1);
		switch (frqMonth) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			termsPerAnnum = BigDecimal.ONE;
			break;
		case FrequencyCodeTypes.FRQ_2YEARLY:
			termsPerAnnum = new BigDecimal(1 / 2);
			break;
		case FrequencyCodeTypes.FRQ_3YEARLY:
			termsPerAnnum = new BigDecimal(1 / 3);
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			termsPerAnnum = new BigDecimal(2);
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			termsPerAnnum = new BigDecimal(4);
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			termsPerAnnum = new BigDecimal(6);
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			termsPerAnnum = new BigDecimal(12);
			break;
		case FrequencyCodeTypes.FRQ_FORTNIGHTLY:
			termsPerAnnum = new BigDecimal(21);
			break;
		case FrequencyCodeTypes.FRQ_BIWEEKLY:
			termsPerAnnum = new BigDecimal(21);
			break;
		case FrequencyCodeTypes.FRQ_WEEKLY:
			termsPerAnnum = new BigDecimal(52);
			break;
		case FrequencyCodeTypes.FRQ_DAILY:
			termsPerAnnum = new BigDecimal(365);
			break;
		default:
			break;
		}

		return termsPerAnnum;
	}

	public static IRRFeeTypeDAO getiRRFeeTypeDAO() {
		return iRRFeeTypeDAO;
	}

	public static void setiRRFeeTypeDAO(IRRFeeTypeDAO iRRFeeTypeDAO) {
		IRRCalculator.iRRFeeTypeDAO = iRRFeeTypeDAO;
	}

	public static IRRFinanceTypeDAO getIrrFinanceTypeDAO() {
		return irrFinanceTypeDAO;
	}

	public static void setIrrFinanceTypeDAO(IRRFinanceTypeDAO irrFinanceTypeDAO) {
		IRRCalculator.irrFinanceTypeDAO = irrFinanceTypeDAO;
	}
}