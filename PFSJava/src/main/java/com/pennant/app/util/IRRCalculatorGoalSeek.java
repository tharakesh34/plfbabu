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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class IRRCalculatorGoalSeek {
	private static final Logger logger = LogManager.getLogger(IRRCalculatorGoalSeek.class);
	private static IRRFeeTypeDAO iRRFeeTypeDAO;
	private static IRRFinanceTypeDAO irrFinanceTypeDAO;

	public IRRCalculatorGoalSeek() {
		super();
	}

	/**
	 * http://stackoverflow.com/questions/36789967/java-program-to-calculate-xirr-without-using-excel-or-any-other-library
	 * 
	 * @param payments
	 * @param repayDates
	 * @return
	 */
	public static BigDecimal calculateXIRR(List<IRRCashFlow> irrCFList) {
		logger.debug(Literal.ENTERING);

		// Formula SUM(Payment(i) / ((1+XIRR) ** ((Date(i) - Date(0))/365)) = 0. Guess XIRR to achieve this
		BigDecimal xIRRLow = BigDecimal.ZERO;
		BigDecimal xIRRHigh = BigDecimal.valueOf(9999.9999);
		BigDecimal xirr = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		Date dateStart = irrCFList.get(0).getCfDate();
		int days = 0;

		for (int i = 0; i < 50; i++) {
			xirr = xIRRLow.add(xIRRHigh).divide(number2);
			xirr = xirr.setScale(9, RoundingMode.HALF_DOWN);

			if (xirr.compareTo(xIRRHigh) == 0 || xirr.compareTo(xIRRLow) == 0) {
				logger.debug(Literal.LEAVING);
				return xirr;
			}

			BigDecimal netOfValue = BigDecimal.ZERO;

			for (IRRCashFlow irrCF : irrCFList) {
				BigDecimal payment = irrCF.getCfAmount();

				days = DateUtil.getDaysBetween(dateStart, irrCF.getCfDate());

				BigDecimal divisor = BigDecimal.ZERO;

				// IN case of IRR VALUE is more than actual Number allowed,
				// we are getting Higher number which cannot be maintainable on calculation
				// So , to calculate again we are reducing Higher IRR value and do the reconcile again
				try {
					divisor = BigDecimal.valueOf(Math.pow((xirr.divide(big100).doubleValue() + 1.0), (days / 365.0)));
				} catch (NumberFormatException e) {
					xIRRHigh = xirr;
					break;
				}
				BigDecimal payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);

			if (netOfValue.abs().compareTo(tolarance) <= 0) {
				logger.debug(Literal.LEAVING);
				return xirr;
			}

			if (netOfValue.compareTo(BigDecimal.ZERO) < 0) {
				xIRRHigh = xirr;
			} else {
				xIRRLow = xirr;
			}
		}

		logger.debug(Literal.LEAVING);
		return xirr;
	}

	public static BigDecimal calculateIRR(List<IRRCashFlow> irrCFList) {
		logger.debug(Literal.ENTERING);

		BigDecimal irrLow = BigDecimal.ZERO;
		BigDecimal irrHigh = BigDecimal.valueOf(9999.9999);
		BigDecimal irr = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		/* double cashFlows = payments.size(); */

		for (int i = 0; i < 50; i++) {
			irr = irrLow.add(irrHigh).divide(number2);
			irr = irr.setScale(9, RoundingMode.HALF_DOWN);

			if (irr.compareTo(irrHigh) == 0 || irr.compareTo(irrLow) == 0) {
				logger.debug(Literal.LEAVING);
				irr = irr.multiply(BigDecimal.valueOf(12));
				return irr;
			}

			BigDecimal netOfValue = BigDecimal.ZERO;

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
				IRRCashFlow irrCF = irrCFList.get(iCf);
				BigDecimal payment = irrCF.getCfAmount();

				BigDecimal divisor = BigDecimal.ZERO;
				// IN case of IRR VALUE is more than actual Number allowed,
				// we are getting Higher number which cannot be maintainable on calculation
				// So , to calculate again we are reducing Higher IRR value and do the reconcile again
				try {
					divisor = BigDecimal.valueOf(Math.pow((irr.divide(big100).doubleValue() + 1.0), iCf));
				} catch (NumberFormatException e) {
					irrHigh = irr;
					break;
				}

				BigDecimal payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);

			if (netOfValue.abs().compareTo(tolarance) <= 0) {
				logger.debug(Literal.LEAVING);
				irr = irr.multiply(BigDecimal.valueOf(12));
				return irr;
			}

			if (netOfValue.compareTo(BigDecimal.ZERO) < 0) {
				irrHigh = irr;
			} else {
				irrLow = irr;
			}
		}

		logger.debug(Literal.LEAVING);
		irr = irr.multiply(BigDecimal.valueOf(12));
		return irr;
	}

	/**
	 * Method for Calculating IRR Value based on guess Rate
	 * 
	 * @param irrCFList
	 * @param guessAdjust
	 * @return
	 */
	public static BigDecimal calculateCashIRRByGuess(List<IRRCashFlow> irrCFList, BigDecimal guessAdjust) {
		logger.debug(Literal.ENTERING);

		// Convert IRR to monthly Rate. It is only initial guess value to reduce the iterations
		BigDecimal irr = BigDecimal.ZERO;
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		guessAdjust = guessAdjust.divide(BigDecimal.valueOf(12), 9, RoundingMode.HALF_DOWN);

		BigDecimal netCashFlow = BigDecimal.ZERO;
		for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
			IRRCashFlow irrCF = irrCFList.get(iCf);
			BigDecimal payment = irrCF.getCfAmount();
			netCashFlow = netCashFlow.add(payment);
		}

		for (int i = 0; i < 50; i++) {

			if (netCashFlow.compareTo(BigDecimal.ZERO) < 0) {
				irr = irr.subtract(guessAdjust);
			} else {
				irr = irr.add(guessAdjust);
			}

			BigDecimal netOfValue = BigDecimal.ZERO;

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
				IRRCashFlow irrCF = irrCFList.get(iCf);
				BigDecimal payment = irrCF.getCfAmount();

				BigDecimal divisor = BigDecimal.ZERO;

				try {
					divisor = BigDecimal.valueOf(Math.pow((irr.divide(big100).doubleValue() + 1.0), iCf));
				} catch (NumberFormatException e) {
					break;
				}

				BigDecimal payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);
			guessAdjust = netOfValue.divide(netCashFlow, 9, RoundingMode.HALF_DOWN);

			if (netOfValue.abs().compareTo(tolarance) <= 0 || guessAdjust.compareTo(BigDecimal.ZERO) == 0) {
				logger.debug(Literal.LEAVING);
				irr = irr.multiply(BigDecimal.valueOf(12));
				return irr;
			}
		}

		logger.debug(Literal.LEAVING);
		irr = irr.multiply(BigDecimal.valueOf(12));
		return irr;
	}

	/**
	 * Method for Calculating IRR Value based on guess Rate
	 * 
	 * @param irrCFList
	 * @param guessAdjust
	 * @return
	 */
	public static BigDecimal calculateCashXIRRByGuess(List<IRRCashFlow> irrCFList, BigDecimal guessAdjust) {
		logger.debug(Literal.ENTERING);

		// Convert IRR to monthly Rate. It is only initial guess value to reduce the iterations
		BigDecimal xirr = BigDecimal.ZERO;
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		guessAdjust = guessAdjust.divide(BigDecimal.valueOf(12), 9, RoundingMode.HALF_DOWN);

		BigDecimal netCashFlow = BigDecimal.ZERO;
		for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
			IRRCashFlow irrCF = irrCFList.get(iCf);
			BigDecimal payment = irrCF.getCfAmount();
			netCashFlow = netCashFlow.add(payment);
		}

		Date dateStart = irrCFList.get(0).getCfDate();
		int days = 0;

		for (int i = 0; i < 50; i++) {

			if (netCashFlow.compareTo(BigDecimal.ZERO) < 0) {
				xirr = xirr.subtract(guessAdjust);
			} else {
				xirr = xirr.add(guessAdjust);
			}

			BigDecimal netOfValue = BigDecimal.ZERO;

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {

				IRRCashFlow irrCF = irrCFList.get(iCf);
				BigDecimal payment = irrCF.getCfAmount();
				days = DateUtil.getDaysBetween(dateStart, irrCF.getCfDate());

				BigDecimal divisor = BigDecimal.ZERO;
				// IN case of IRR VALUE is more than actual Number allowed,
				// we are getting Higher number which cannot be maintainable on calculation
				// So , to calculate again we are reducing Higher IRR value and do the reconcile again
				try {
					divisor = BigDecimal.valueOf(Math.pow((xirr.divide(big100).doubleValue() + 1.0), (days / 365.0)));
				} catch (NumberFormatException e) {
					break;
				}

				BigDecimal payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);
			guessAdjust = netOfValue.divide(netCashFlow, 9, RoundingMode.HALF_DOWN);

			if (netOfValue.abs().compareTo(tolarance) <= 0 || guessAdjust.compareTo(BigDecimal.ZERO) == 0) {
				logger.debug(Literal.LEAVING);
				xirr = xirr.multiply(BigDecimal.valueOf(12));
				return xirr;
			}
		}

		logger.debug(Literal.LEAVING);
		xirr = xirr.multiply(BigDecimal.valueOf(12));
		return xirr;
	}

	public static BigDecimal irrDoubleNew(List<IRRCashFlow> irrCFList, BigDecimal irrWithoutFee) {
		double[] values = new double[irrCFList.size()];
		for (int iCf = 0; iCf < irrCFList.size(); iCf++) {

			IRRCashFlow irrCF = irrCFList.get(iCf);
			values[iCf] = irrCF.getCfAmount().doubleValue();
		}

		final int maxIterationCount = 200;
		final double absoluteAccuracy = 1E-7;

		double x0 = 0;
		double x1;

		int i = 0;
		while (i < maxIterationCount) {

			// the value of the function (NPV) and its derivate can be calculated in the same loop
			final double factor = 1.0 + x0;
			int k = 0;
			double fValue = values[k];
			double fDerivative = 0;
			for (double denominator = factor; ++k < values.length;) {
				final double value = values[k];
				fValue += value / denominator;
				denominator *= factor;
				fDerivative -= k * value / denominator;
			}

			// the essense of the Newton-Raphson Method
			x1 = x0 - fValue / fDerivative;

			if (Math.abs(x1 - x0) <= absoluteAccuracy) {
				return BigDecimal.valueOf(x1);
			}

			x0 = x1;
			++i;
		}
		// maximum number of iterations is exceeded
		return BigDecimal.ZERO;
	}

	public static void calculateXIRRAndIRR(FinScheduleData fsData, List<FinAdvancePayments> finAdvancePaymentsList,
			boolean isCalXIrr) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fsData.getFinanceMain();
		// XIRR && IRR

		List<IRRFinanceType> irrFT = getIrrFinanceTypeDAO().getIRRFinanceTypeByFinType(fm.getFinType(), "_AView");
		fsData.setiRRDetails(new ArrayList<FinIRRDetails>());
		List<IRRCashFlow> irrCFList = prepareScheduleCashFlow(fsData, finAdvancePaymentsList);

		// Guess Rate finding based on schedule Rates
		List<FinanceScheduleDetail> schdList = fsData.getFinanceScheduleDetails();
		BigDecimal guessRate = schdList.get(0).getCalculatedRate();
		for (FinanceScheduleDetail curSchd : schdList) {
			if (curSchd.getCalculatedRate().compareTo(guessRate) < 0) {
				guessRate = curSchd.getCalculatedRate();
			}
		}

		if (irrCFList.size() == 0) {
			return;
		}

		BigDecimal irr = BigDecimal.ZERO;
		// Annualized IRR
		if (isCalXIrr) {
			irr = IRRCalculatorGoalSeek.calculateCashXIRRByGuess(irrCFList, guessRate);
		} else {
			irr = IRRCalculatorGoalSeek.calCashIRRByNewtonGuess(irrCFList, guessRate, fm);
		}

		fm.setAnualizedPercRate(irr.setScale(9));

		// Effective Rate of Return & Multi IRR
		if (irrFT.isEmpty()) {
			return;
		}

		// ADD CASH BACK ENTRY TO THE MASTER CASH FLOW
		irrCFList = addcashBacktoFlow(fsData, irrCFList);

		for (int iFT = 0; iFT < irrFT.size(); iFT++) {
			List<IRRCashFlow> irrFeeCFList = new ArrayList<>();
			for (IRRCashFlow irrCashFlow : irrCFList) {
				irrFeeCFList.add(irrCashFlow.copyEntity());
			}

			IRRFinanceType irrFinanceType = irrFT.get(iFT);

			irrFeeCFList = prepareFeeCashFlow(fsData, irrFeeCFList, irrFinanceType);

			if (isCalXIrr) {
				irr = IRRCalculatorGoalSeek.calculateCashXIRRByGuess(irrCFList, guessRate);
			} else {
				irr = IRRCalculatorGoalSeek.calCashIRRByNewtonGuess(irrFeeCFList, guessRate, fm);
			}

			if (iFT == 0) {
				fm.setEffectiveRateOfReturn(irr.setScale(9));
				prepareIRRSchedule(fsData, irrFeeCFList);
			}

			if (irrFT != null) {
				FinIRRDetails irrDetail = new FinIRRDetails();
				irrDetail.setiRRID(irrFinanceType.getIRRID());
				irrDetail.setiRRCode(irrFinanceType.getIrrCode());
				irrDetail.setIrrCodeDesc(irrFinanceType.getIrrCodeDesc());
				irrDetail.setIRR(irr.setScale(9));
				fsData.getiRRDetails().add(irrDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public static List<IRRCashFlow> prepareScheduleCashFlow(FinScheduleData schdData,
			List<FinAdvancePayments> advPayList) {
		List<IRRCashFlow> irrCFList = new ArrayList<>(1);
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();

		BigDecimal subvention = BigDecimal.ZERO;

		if (schdData.getPromotion() != null) {
			FinanceMain fm = schdData.getFinanceMain();
			BigDecimal totPayment = fm.getFinAmount();
			BigDecimal totDuePayment = totPayment.subtract(fm.getDownPayment());
			BigDecimal presentValue = fsdList.get(0).getClosingBalance();
			subvention = totDuePayment.subtract(presentValue);
		}

		List<FinFeeDetail> feeDtlList = schdData.getFinFeeDetailList();
		BigDecimal subvenFee = BigDecimal.ZERO;
		if (!FinanceConstants.PRODUCT_CD.equals(schdData.getFinanceMain().getProductCategory())
				&& CollectionUtils.isNotEmpty(feeDtlList)) {
			String subventionFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_SUBVENTION);
			for (FinFeeDetail fee : feeDtlList) {
				if (StringUtils.equals(fee.getFeeTypeCode(), subventionFeeCode)) {
					subvention = fee.getNetAmountOriginal();
					subvenFee = fee.getNetAmountOriginal();
					break;
				}
			}
		}

		boolean bpiImpactOnIRR = SysParamUtil.isAllowed(SMTParameterConstants.BPI_IMPACT_ON_IRR);
		for (FinanceScheduleDetail schd : fsdList) {
			if (schd.getDisbAmount().compareTo(BigDecimal.ZERO) == 0
					&& schd.getCpzAmount().compareTo(BigDecimal.ZERO) == 0
					&& schd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {

				if (schd.isRepayOnSchDate() && schd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					IRRCashFlow irrCF = new IRRCashFlow();
					irrCF.setCfDate(schd.getSchDate());
					irrCF.setCfAmount(BigDecimal.ZERO);
					irrCFList.add(irrCF);
				}
				continue;
			}

			// Whether BPI Amount include on IR calculation or not depends on System parameter
			if (!bpiImpactOnIRR && FinanceConstants.FLAG_BPI.equals(schd.getBpiOrHoliday())) {
				continue;
			}

			IRRCashFlow irrCF = new IRRCashFlow();
			irrCF.setCfDate(schd.getSchDate());
			irrCF.setCfAmount(schd.getRepayAmount().add(schd.getDownPaymentAmount()).subtract(schd.getCpzAmount())
					.add(subvention).subtract(schd.getDisbAmount()));

			irrCFList.add(irrCF);
			subvention = BigDecimal.ZERO;
		}

		if (CollectionUtils.isNotEmpty(advPayList)) {
			irrCFList = sortIRRDetails(irrCFList);

			BigDecimal initialDisbAmt = BigDecimal.ZERO;
			for (FinAdvancePayments advPay : advPayList) {
				if (advPay.getDisbSeq() == 1) {
					initialDisbAmt = initialDisbAmt.add(advPay.getAmtToBeReleased());
				}
			}

			BigDecimal diffDeductedFromDisb = schdData.getFinanceMain().getFinAmount().subtract(initialDisbAmt);

			for (int i = 0; i < advPayList.size(); i++) {
				FinAdvancePayments advPay = advPayList.get(i);

				if (PennantConstants.RECORD_TYPE_CAN.equals(advPay.getRecordType())
						|| PennantConstants.RECORD_TYPE_DEL.equals(advPay.getRecordType())) {
					continue;
				}

				boolean rcdFound = false;
				for (int iCf = 0; iCf < irrCFList.size(); iCf++) {
					IRRCashFlow irrCF = irrCFList.get(iCf);
					if (DateUtil.compare(irrCF.getCfDate(), advPay.getLlDate()) == 0) {
						if (iCf != 0) {
							irrCF.setCfAmount(irrCF.getCfAmount().subtract(advPay.getAmtToBeReleased()).add(subvenFee));
							irrCFList.get(0).setCfAmount(irrCFList.get(0).getCfAmount().add(advPay.getAmtToBeReleased())
									.subtract(subvenFee));
							if (i == 0) {
								irrCF.setCfAmount(irrCF.getCfAmount().subtract(diffDeductedFromDisb));
								irrCFList.get(0).setCfAmount(irrCFList.get(0).getCfAmount().add(diffDeductedFromDisb));
							}
						}
						rcdFound = true;
					} else if (DateUtil.compare(irrCF.getCfDate(), advPay.getLlDate()) > 0) {
						break;
					}
				}

				if (rcdFound) {
					continue;
				}
				IRRCashFlow irrCF = new IRRCashFlow();
				irrCF.setCfDate(advPay.getLlDate());
				irrCF.setCfAmount(advPay.getAmtToBeReleased().negate());
				irrCF.setNonSchdRcd(true);
				irrCFList.add(irrCF);
				irrCFList.get(0).setCfAmount(irrCFList.get(0).getCfAmount().add(advPay.getAmtToBeReleased()));

				if (i == 0) {
					irrCF.setCfAmount(irrCF.getCfAmount().subtract(diffDeductedFromDisb));
					irrCFList.get(0).setCfAmount(irrCFList.get(0).getCfAmount().add(diffDeductedFromDisb));
				}

				irrCFList = sortIRRDetails(irrCFList);
			}
		}
		return irrCFList;
	}

	public static List<IRRCashFlow> sortIRRDetails(List<IRRCashFlow> irrCFList) {
		if (CollectionUtils.isEmpty(irrCFList)) {
			return irrCFList;
		}

		return irrCFList.stream().sorted((icf1, icf2) -> DateUtil.compare(icf1.getCfDate(), icf2.getCfDate()))
				.collect(Collectors.toList());
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
			BigDecimal gst = GSTCalculator.calculateGST(fm.getFinID(), fm.getFinCcy(), "I", cashBack).gettGST();
			cashBack = cashBack.subtract(gst);
		}

		fm.setCbAmount(cashBack);

		for (FinanceScheduleDetail fsd : fsdList) {
			if (fsd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (fsd.getInstNumber() < cashBackInst) {
				continue;
			}

			for (IRRCashFlow irrCashFlow : irrCFList) {
				if (irrCashFlow.getCfDate().compareTo(fsd.getSchDate()) == 0) {
					irrCashFlow.setCfAmount(irrCashFlow.getCfAmount().add(cashBack));
					break;
				}
			}

			break;
		}

		return irrCFList;
	}

	private static List<IRRCashFlow> prepareFeeCashFlow(FinScheduleData fsData, List<IRRCashFlow> irrFeeCFList,
			IRRFinanceType irrFT) {

		IRRCashFlow irrFeeCF = irrFeeCFList.get(0);

		List<FinFeeDetail> ffdList = fsData.getFinFeeDetailList();
		List<IRRFeeType> irrFeeList = null;

		if (irrFT != null) {
			irrFeeList = iRRFeeTypeDAO.getIRRFeeTypeList(irrFT.getIRRID(), "");
		}

		if (ffdList.isEmpty()) {
			return irrFeeCFList;
		}

		BigDecimal refundFee = BigDecimal.ZERO;

		// AS OF NOW ONLY DISBURSEMENTS CONSIDERED. IN FUTURE IT SHOUD CONSIDER
		// ALL FEE RECEIPTS AND REFUNDS TOO
		for (FinFeeDetail ffd : ffdList) {
			if (!AccountingEvent.ADDDBSP.equals(ffd.getFinEvent())) {
				continue;
			}

			if (!CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(ffd.getFeeScheduleMethod())) {
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

			BigDecimal calFeeAmount = ffd.getNetAmountOriginal();
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

	public static void prepareIRRSchedule(FinScheduleData fsData, List<IRRCashFlow> irrFeeCFList) {
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		List<IRRScheduleDetail> irrSDList = new ArrayList<IRRScheduleDetail>();
		BigDecimal irr = fsData.getFinanceMain().getEffectiveRateOfReturn();

		BigDecimal fraction = BigDecimal.ZERO;
		BigDecimal interest = BigDecimal.ZERO;
		BigDecimal roundedInterest = BigDecimal.ZERO;
		BigDecimal openBalance = BigDecimal.ZERO;
		BigDecimal dayFactor = BigDecimal.valueOf(30).divide(BigDecimal.valueOf(360), 9, RoundingMode.HALF_DOWN);

		// Prepare Dummy Schedule
		for (int iCf = 0; iCf < irrFeeCFList.size(); iCf++) {
			IRRCashFlow irrCF = irrFeeCFList.get(iCf);
			IRRScheduleDetail irrSD = new IRRScheduleDetail();
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

			if (irrSD.isNonSchdRcd()) {
				irrSD.setGapInterst(irrSD.getProfitCalc());
			} else {
				FinanceScheduleDetail fsd = fsdList.get(schdSize);
				irrSD.setGapInterst(irrSD.getProfitCalc().subtract(fsd.getProfitCalc()));
				schdSize = schdSize + 1;
			}
		}

		fsData.setIrrSDList(irrSDList);
	}

	/**
	 * Method for Calculating IRR Value based on guess Rate
	 * 
	 * @param irrCFList
	 * @param guessAdjust
	 * @return
	 */
	public static BigDecimal calCashIRRByNewtonGuess(List<IRRCashFlow> irrCFList, BigDecimal guessAdjust,
			FinanceMain fm) {
		logger.debug("Entering");

		// Convert IRR to monthly Rate. It is only initial guess value to reduce the iterations
		BigDecimal irr = BigDecimal.ZERO;
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = BigDecimal.ZERO;

		BigDecimal payment = BigDecimal.ZERO;
		BigDecimal frqDivisor = getFactionDivisor(fm.getRepayFrq());
		BigDecimal prvGuessRate = BigDecimal.ZERO;

		BigDecimal netCashFlow = BigDecimal.ZERO;

		for (int i = 0; i < 50; i++) {

			prvGuessRate = prvGuessRate.add(guessAdjust);
			irr = prvGuessRate.divide(frqDivisor, 9, RoundingMode.HALF_DOWN);

			netCashFlow = BigDecimal.ZERO;
			BigDecimal netOfValue = BigDecimal.ZERO;

			for (int iCf = 0; iCf < irrCFList.size(); iCf++) {

				IRRCashFlow irrCF = irrCFList.get(iCf);
				payment = irrCF.getCfAmount();

				// Net Cash flows
				BigDecimal divisor = BigDecimal.ZERO;
				try {
					divisor = BigDecimal.valueOf(Math.pow((irr.divide(big100).doubleValue() + 1.0), iCf));
				} catch (NumberFormatException e) {
					break;
				}

				BigDecimal payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				netCashFlow = netCashFlow.add(payOfValue);

				// Net Of Values
				try {
					divisor = BigDecimal.valueOf(Math.pow((irr.divide(big100).doubleValue() + 1.0), iCf + 1));
				} catch (NumberFormatException e) {
					break;
				}

				payOfValue = payment.divide(divisor, 9, RoundingMode.HALF_DOWN);
				payOfValue = payOfValue.multiply(BigDecimal.valueOf(iCf));
				netOfValue = netOfValue.add(payOfValue);
			}

			netCashFlow = netCashFlow.setScale(0, RoundingMode.HALF_DOWN);
			netCashFlow = netCashFlow.multiply(big100);
			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);

			guessAdjust = netCashFlow.divide(netOfValue, 9, RoundingMode.HALF_DOWN);

			if (netCashFlow.abs().compareTo(tolarance) <= 0 || guessAdjust.compareTo(BigDecimal.ZERO) == 0) {
				logger.debug("Leaving");
				irr = irr.multiply(frqDivisor);
				return irr;
			}

			guessAdjust = guessAdjust.multiply(frqDivisor);
		}

		logger.debug("Leaving");
		irr = irr.multiply(frqDivisor);
		return irr;
	}

	public static BigDecimal getFactionDivisor(String frequency) {
		BigDecimal frqDays = BigDecimal.ZERO;
		String frqMonth = StringUtils.substring(frequency, 0, 1);
		switch (frqMonth) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			frqDays = BigDecimal.ONE;
			break;
		case FrequencyCodeTypes.FRQ_2YEARLY:
			frqDays = new BigDecimal(1 / 2);
			break;
		case FrequencyCodeTypes.FRQ_3YEARLY:
			frqDays = new BigDecimal(1 / 3);
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			frqDays = new BigDecimal(2);
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			frqDays = new BigDecimal(4);
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			frqDays = new BigDecimal(6);
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			frqDays = new BigDecimal(12);
			break;
		case FrequencyCodeTypes.FRQ_FORTNIGHTLY:
			frqDays = new BigDecimal(21);
			break;
		case FrequencyCodeTypes.FRQ_BIWEEKLY:
			frqDays = new BigDecimal(21);
			break;
		case FrequencyCodeTypes.FRQ_WEEKLY:
			frqDays = new BigDecimal(52);
			break;
		case FrequencyCodeTypes.FRQ_DAILY:
			frqDays = new BigDecimal(365);
			break;
		default:
			break;
		}

		return frqDays;
	}

	public static IRRFeeTypeDAO getiRRFeeTypeDAO() {
		return iRRFeeTypeDAO;
	}

	public static void setiRRFeeTypeDAO(IRRFeeTypeDAO iRRFeeTypeDAO) {
		IRRCalculatorGoalSeek.iRRFeeTypeDAO = iRRFeeTypeDAO;
	}

	public static IRRFinanceTypeDAO getIrrFinanceTypeDAO() {
		return irrFinanceTypeDAO;
	}

	public static void setIrrFinanceTypeDAO(IRRFinanceTypeDAO irrFinanceTypeDAO) {
		IRRCalculatorGoalSeek.irrFinanceTypeDAO = irrFinanceTypeDAO;
	}
}