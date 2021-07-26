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
 * FileName    		:  RateCalculation.java                                                 * 	  
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
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.IRRCashFlow;
import com.pennant.backend.model.finance.IRRScheduleDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennanttech.pff.constants.AccountingEvent;
import com.rits.cloning.Cloner;

public class IRRCalculator {
	private static final Logger logger = LogManager.getLogger(IRRCalculator.class);
	private static IRRFeeTypeDAO iRRFeeTypeDAO;
	private static IRRFinanceTypeDAO irrFinanceTypeDAO;

	/**
	 * http://stackoverflow.com/questions/36789967/java-program-to-calculate-xirr-without-using-excel-or-any-other-library
	 * 
	 * @param payments
	 * @param repayDates
	 * @return
	 */
	public static BigDecimal calculateXIRR(List<IRRCashFlow> irrCFList) {

		logger.debug("Entering");
		//Formula SUM(Payment(i) / ((1+XIRR) ** ((Date(i) - Date(0))/365))  = 0. Guess XIRR to achieve this
		BigDecimal xIRRLow = BigDecimal.ZERO;
		BigDecimal xIRRHigh = new BigDecimal(9999.9999);
		BigDecimal xirr = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		BigDecimal payment = BigDecimal.ZERO;
		Date dateStart = irrCFList.get(0).getCfDate();
		int days = 0;

		for (int i = 0; i < 50; i++) {
			xirr = xIRRLow.add(xIRRHigh).divide(number2);
			xirr = xirr.setScale(9, RoundingMode.HALF_DOWN);

			if (xirr.compareTo(xIRRHigh) == 0 || xirr.compareTo(xIRRLow) == 0) {
				logger.debug("Leaving");
				return xirr;
			}

			BigDecimal netOfValue = BigDecimal.ZERO;
			BigDecimal payOfValue = BigDecimal.ZERO;
			BigDecimal divisor = BigDecimal.ZERO;

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
				IRRCashFlow irrCF = irrCFList.get(iCf);
				payment = irrCF.getCfAmount();
				days = DateUtility.getDaysBetween(dateStart, irrCF.getCfDate());
				divisor = BigDecimal.valueOf(Math.pow((xirr.divide(big100).doubleValue() + 1.0), (days / 365.0)));
				payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);

			if (netOfValue.abs().compareTo(tolarance) <= 0) {
				logger.debug("Leaving");
				return xirr;
			}

			if (netOfValue.compareTo(BigDecimal.ZERO) < 0) {
				xIRRHigh = xirr;
			} else {
				xIRRLow = xirr;
			}

		}

		logger.debug("Leaving");
		return xirr;
	}

	public static BigDecimal calculateIRR(List<IRRCashFlow> irrCFList) {

		logger.debug("Entering");
		BigDecimal irrLow = BigDecimal.ZERO;
		BigDecimal irrHigh = new BigDecimal(9999.9999);
		BigDecimal irr = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		BigDecimal payment = BigDecimal.ZERO;
		//double cashFlows = payments.size();

		for (int i = 0; i < 50; i++) {
			irr = irrLow.add(irrHigh).divide(number2);
			irr = irr.setScale(9, RoundingMode.HALF_DOWN);

			if (irr.compareTo(irrHigh) == 0 || irr.compareTo(irrLow) == 0) {
				logger.debug("Leaving");
				irr = irr.multiply(BigDecimal.valueOf(12));
				return irr;
			}

			BigDecimal netOfValue = BigDecimal.ZERO;
			BigDecimal payOfValue = BigDecimal.ZERO;
			BigDecimal divisor = BigDecimal.ZERO;

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
				IRRCashFlow irrCF = irrCFList.get(iCf);
				payment = irrCF.getCfAmount();
				divisor = BigDecimal.valueOf(Math.pow((irr.divide(big100).doubleValue() + 1.0), iCf));
				payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);

			if (netOfValue.abs().compareTo(tolarance) <= 0) {
				logger.debug("Leaving");
				irr = irr.multiply(BigDecimal.valueOf(12));
				return irr;
			}

			if (netOfValue.compareTo(BigDecimal.ZERO) < 0) {
				irrHigh = irr;
			} else {
				irrLow = irr;
			}

		}

		logger.debug("Leaving");
		irr = irr.multiply(BigDecimal.valueOf(12));
		return irr;
	}

	public static void calculateXIRRAndIRR(FinScheduleData fsData, boolean isCalXIrr) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		// XIRR && IRR

		List<IRRFinanceType> irrFT = getIrrFinanceTypeDAO().getIRRFinanceTypeByFinType(fm.getFinType(), "_AView");
		fsData.setiRRDetails(new ArrayList<FinIRRDetails>());
		List<IRRCashFlow> irrCFList = prepareScheduleCashFlow(fsData);
		BigDecimal irr = BigDecimal.ZERO;

		if (irrCFList.size() == 0) {
			return;
		}

		//Annualized IRR
		if (isCalXIrr) {
			irr = IRRCalculator.calculateXIRR(irrCFList);
		} else {
			irr = IRRCalculator.calculateIRR(irrCFList);
		}

		fm.setAnualizedPercRate(irr.setScale(9));

		//Effective Rate of Return & Multi IRR
		if (irrFT.isEmpty()) {
			return;
		}

		//ADD CASH BACK ENTRY TO THE MASTER CASH FLOW
		irrCFList = addcashBacktoFlow(fsData, irrCFList);
		Cloner cloner = new Cloner();
		List<IRRCashFlow> irrFeeCFList = null;

		for (int iFT = 0; iFT < irrFT.size(); iFT++) {
			irrFeeCFList = cloner.deepClone(irrCFList);
			irrFeeCFList = prepareFeeCashFlow(fsData, irrFeeCFList, irrFT.get(iFT));
			irr = BigDecimal.ZERO;

			if (isCalXIrr) {
				irr = IRRCalculator.calculateXIRR(irrFeeCFList);
			} else {
				irr = IRRCalculator.calculateIRR(irrFeeCFList);
			}

			if (iFT == 0) {
				fm.setEffectiveRateOfReturn(irr.setScale(9));
				prepareIRRSchedule(fsData, irrFeeCFList);
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

	public static List<IRRCashFlow> prepareScheduleCashFlow(FinScheduleData fsData) {
		List<IRRCashFlow> irrCFList = new ArrayList<IRRCashFlow>(1);
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		BigDecimal subvention = BigDecimal.ZERO;

		if (fsData.getPromotion() != null) {
			FinanceMain fm = fsData.getFinanceMain();
			BigDecimal totPayment = fm.getFinAmount();
			BigDecimal totDuePayment = totPayment.subtract(fm.getDownPayment());
			BigDecimal presentValue = fsdList.get(0).getClosingBalance();
			subvention = totDuePayment.subtract(presentValue);
		}

		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (fsd.getDisbAmount().compareTo(BigDecimal.ZERO) == 0
					&& fsd.getCpzAmount().compareTo(BigDecimal.ZERO) == 0
					&& fsd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {

				if (fsd.isRepayOnSchDate() && fsd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					IRRCashFlow irrCF = new IRRCashFlow();
					irrCF.setCfDate(fsd.getSchDate());
					irrCF.setCfAmount(BigDecimal.ZERO);
					irrCFList.add(irrCF);
				}
				continue;
			}

			IRRCashFlow irrCF = new IRRCashFlow();
			irrCF.setCfDate(fsd.getSchDate());
			irrCF.setCfAmount(fsd.getRepayAmount().add(fsd.getDownPaymentAmount()).subtract(fsd.getDisbAmount())
					.subtract(fsd.getCpzAmount()).add(subvention));
			irrCFList.add(irrCF);
			subvention = BigDecimal.ZERO;
		}

		return irrCFList;
	}

	public static List<IRRCashFlow> addcashBacktoFlow(FinScheduleData fsData, List<IRRCashFlow> irrCFList) {

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
			IRRFinanceType irrFT) {

		IRRCashFlow irrFeeCF = irrFeeCFList.get(0);
		List<FinFeeDetail> ffdList = fsData.getFinFeeDetailList();
		List<IRRFeeType> irrFeeList = null;
		if (irrFT != null) {
			irrFeeList = getiRRFeeTypeDAO().getIRRFeeTypeList(irrFT.getIRRID(), "");
		}

		if (ffdList.isEmpty() || ffdList.size() <= 0) {
			return irrFeeCFList;
		}

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

			irrFeeCF.setCfAmount(irrFeeCF.getCfAmount().add(calFeeAmount));
		}

		return irrFeeCFList;
	}

	public static void prepareIRRSchedule(FinScheduleData fsData, List<IRRCashFlow> irrFeeCFList) {
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		List<IRRScheduleDetail> irrSDList = new ArrayList<IRRScheduleDetail>();
		BigDecimal irr = fsData.getFinanceMain().getEffectiveRateOfReturn();

		BigDecimal fraction = BigDecimal.ZERO;
		BigDecimal interest = BigDecimal.ZERO;
		BigDecimal roundedInterest = BigDecimal.ZERO;
		BigDecimal openBalance = BigDecimal.ZERO;
		BigDecimal dayFactor = BigDecimal.valueOf(30).divide(BigDecimal.valueOf(360), 9, RoundingMode.HALF_DOWN);

		//Prepare Dummy Schedule
		for (int iCf = 0; iCf < irrFeeCFList.size(); iCf++) {
			IRRCashFlow irrCF = irrFeeCFList.get(iCf);
			IRRScheduleDetail irrSD = new IRRScheduleDetail();
			irrSD.setFinReference(fsData.getFinanceMain().getFinReference());
			irrSD.setSchDate(irrCF.getCfDate());
			if (iCf == 0) {
				irrSD.setClosingBalance(BigDecimal.ZERO.subtract(irrCF.getCfAmount()));
			} else {
				irrSD.setRepayAmount(irrCF.getCfAmount());
			}
			irrSDList.add(irrSD);
		}

		int sdSize = irrSDList.size();
		//Start Calculate
		for (int iSD = 1; iSD < sdSize; iSD++) {
			IRRScheduleDetail irrSD = irrSDList.get(iSD);
			openBalance = irrSDList.get(iSD - 1).getClosingBalance();

			interest = CalculationUtil.calInterestWithDaysFactor(dayFactor, openBalance, irr);
			interest = interest.add(fraction);

			if (interest.compareTo(BigDecimal.ZERO) > 0) {
				roundedInterest = CalculationUtil.roundAmount(interest, RoundingMode.HALF_DOWN.name(), 0);
			} else {
				roundedInterest = BigDecimal.ZERO;
			}

			fraction = interest.subtract(roundedInterest);
			interest = roundedInterest;

			if (iSD == (sdSize - 1)) {
				irrSD.setProfitCalc(irrSD.getRepayAmount().subtract(openBalance));
				irrSD.setPrincipalCalc(openBalance);
				irrSD.setClosingBalance(BigDecimal.ZERO);
			} else {
				irrSD.setProfitCalc(roundedInterest);
				irrSD.setPrincipalCalc(irrSD.getRepayAmount().subtract(roundedInterest));
				irrSD.setClosingBalance(openBalance.subtract(irrSD.getPrincipalCalc()));
			}

			FinanceScheduleDetail fsd = fsdList.get(iSD);
			irrSD.setGapInterst(irrSD.getProfitCalc().subtract(fsd.getProfitCalc()));
		}

		fsData.setIrrSDList(irrSDList);
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