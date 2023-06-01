package com.pennant.pff.core.loan.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class LoanClosureCalculator {

	private LoanClosureCalculator() {
		super();
	}

	/**
	 * Returns {@link java.math.BigDecimal} for the specified loan data.
	 * <p>
	 * The below is the loan data details.
	 * <li>{@link FinanceMain} - Used for GST, TDS calculations & Rounding the calculated amounts.</li>
	 * <li>{@link FinanceScheduleDetail} - Used for Principal and Interest calculations.</li>
	 * <li>{@link ManualAdvise} - Used for Bounce and Receivable calculations.</li>
	 * <li>{@link FinODDetails} - Used for Penalty calculations.</li>
	 * <li>{@link FinFeeDetail} - Used for Servicing Fee calculations. like, Fore Closure Charges</li>
	 * <li>{@link SMTParameterConstants#ROUND_ADJ_METHOD} - value assign to roundAdjMth</li>
	 * <li>{@link FeeType} - Used for GST calculation for Penalty (only for LPP)</li>
	 * <li>{@link ValueDate} - Value Date to calculate till date interest amount</li>
	 * </p>
	 * 
	 * @param receiptDTO The DTO of loan data
	 * @param feeCalReq  The boolean variable whether the fee amount is include in closure amount.
	 * 
	 * 
	 * @return The closure amount for the specified loan data.
	 */
	public static BigDecimal computeClosureAmount(ReceiptDTO receiptDTO, boolean feeCalReq) {
		BigDecimal closureAmount = BigDecimal.ZERO;

		String roundAdjMth = receiptDTO.getRoundAdjMth();

		List<FinanceScheduleDetail> schedules = receiptDTO.getSchedules();
		FinanceMain fm = receiptDTO.getFinanceMain();
		Date valuedate = receiptDTO.getValuedate();

		BigDecimal pos = calculatePOS(schedules, valuedate);
		BigDecimal pftSchBal = calculateTillDatePftSchdBal(schedules, valuedate);
		BigDecimal pftBal = calculateTillDatePftBal(schedules, fm, valuedate);
		BigDecimal pftFraction = BigDecimal.ZERO;

		if (CalculationConstants.PFTFRACTION_ADJ_LAST_INST.equals(roundAdjMth)) {
			pftFraction = calculatePftFraction(schedules);
		}

		closureAmount = closureAmount.add(pos).add(pftSchBal).add(pftBal).add(pftFraction);

		List<ManualAdvise> manualAdvises = receiptDTO.getManualAdvises();

		List<ManualAdvise> bounces = new ArrayList<>();
		List<ManualAdvise> receivables = new ArrayList<>();

		manualAdvises.stream().forEach(ma -> {
			if (Allocation.BOUNCE.equals(ma.getFeeTypeCode())) {
				bounces.add(ma);
			} else {
				receivables.add(ma);
			}
		});

		BigDecimal bounceAmount = calculateMA(bounces, fm);
		BigDecimal receivableFeeAmount = calculateMA(receivables, fm);

		closureAmount = closureAmount.add(bounceAmount).add(receivableFeeAmount);

		BigDecimal lppAmount = calculateLPP(receiptDTO);
		BigDecimal lpiAmount = calculateLPI(receiptDTO);

		closureAmount = closureAmount.add(lppAmount).add(lpiAmount);

		if (feeCalReq) {
			BigDecimal feeAmount = calculateFees(receiptDTO);
			closureAmount = closureAmount.add(feeAmount);
		}

		return closureAmount;
	}

	private static BigDecimal calculatePOS(List<FinanceScheduleDetail> schedules, Date valuedate) {
		BigDecimal pos = BigDecimal.ZERO;

		for (FinanceScheduleDetail schedule : schedules) {
			pos = pos.add(schedule.getPrincipalSchd());
			pos = pos.subtract(schedule.getSchdPriPaid());

			if (valuedate.compareTo(schedule.getSchDate()) <= 0) {
				pos = pos.subtract(schedule.getCpzAmount());
			}
		}

		return pos;
	}

	private static BigDecimal calculateTillDatePftSchdBal(List<FinanceScheduleDetail> schedules, Date eodDate) {
		BigDecimal tillDatePftSchdBal = BigDecimal.ZERO;

		for (FinanceScheduleDetail schedule : schedules) {
			if (schedule.getSchDate().compareTo(eodDate) <= 0) {
				tillDatePftSchdBal = tillDatePftSchdBal.add(schedule.getProfitSchd().subtract(
						schedule.getSchdPftPaid().add(schedule.getTDSAmount().subtract(schedule.getTDSPaid()))));
			}
		}

		return tillDatePftSchdBal;
	}

	private static BigDecimal calculateTillDatePftBal(List<FinanceScheduleDetail> schedules, FinanceMain fm,
			Date eodDate) {
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;

		for (int i = 0; i < schedules.size(); i++) {
			curSchd = schedules.get(i);

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schedules.get(i - 1);
			}

			Date prvSchdDate = prvSchd.getSchDate();
			Date curSchDate = curSchd.getSchDate();

			if (eodDate.compareTo(prvSchdDate) > 0 && eodDate.compareTo(curSchDate) < 0) {
				BigDecimal pftBal = CalculationUtil.calInterest(prvSchdDate, eodDate, curSchd.getBalanceForPftCal(),
						curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
				pftBal = pftBal.subtract(curSchd.getTDSAmount().subtract(curSchd.getTDSPaid()));

				return CalculationUtil.roundAmount(pftBal, fm.getCalRoundingMode(), fm.getRoundingTarget());
			}
		}

		return BigDecimal.ZERO;
	}

	private static BigDecimal calculatePftFraction(List<FinanceScheduleDetail> schedules) {
		BigDecimal pftFraction = BigDecimal.ZERO;

		for (FinanceScheduleDetail schedule : schedules) {
			pftFraction = pftFraction.add(schedule.getProfitFraction());
		}

		return pftFraction;
	}

	private static BigDecimal calculateMA(List<ManualAdvise> maList, FinanceMain fm) {
		BigDecimal bounceAmount = BigDecimal.ZERO;

		long finID = fm.getFinID();
		String ccy = fm.getFinCcy();

		for (ManualAdvise ma : maList) {
			BigDecimal receivedAmt = ma.getPaidAmount().add(ma.getWaivedAmount());

			BigDecimal adviseAmt = ma.getAdviseAmount();
			BigDecimal balanceAmount = adviseAmt.subtract(receivedAmt);

			String component = ma.getTaxComponent();

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(component)) {
				BigDecimal totalgst = calculateGST(finID, ccy, component, adviseAmt);
				BigDecimal paidgst = calculateGST(finID, ccy, component, receivedAmt);

				balanceAmount = balanceAmount.add(totalgst.subtract(paidgst));
			}

			if (TDSCalculator.isTDSApplicable(fm, ma.isTdsReq())) {
				balanceAmount = balanceAmount.subtract(TDSCalculator.getTDSAmount(adviseAmt).subtract(ma.getTdsPaid()));
			}

			bounceAmount = bounceAmount.add(balanceAmount);
		}

		return bounceAmount;
	}

	private static BigDecimal calculateLPP(ReceiptDTO receiptDTO) {
		BigDecimal penalty = BigDecimal.ZERO;

		FinanceMain fm = receiptDTO.getFinanceMain();
		List<FinODDetails> odDetails = receiptDTO.getOdDetails();
		FeeType lppFeeType = receiptDTO.getLppFeeType();

		boolean taxApplicable = lppFeeType.isTaxApplicable();
		String component = lppFeeType.getTaxComponent();

		long finID = fm.getFinID();
		String ccy = fm.getFinCcy();

		for (FinODDetails od : odDetails) {
			BigDecimal odBal = od.getTotPenaltyBal();

			if (taxApplicable && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(component)) {
				BigDecimal receivedAmt = od.getTotPenaltyPaid().add(od.getTotWaived());

				BigDecimal totalgst = calculateGST(finID, ccy, component, od.getTotPenaltyAmt());
				BigDecimal paidgst = calculateGST(finID, ccy, component, receivedAmt);

				odBal = odBal.add(totalgst.subtract(paidgst));
			}

			penalty = penalty.add(odBal);
		}

		return penalty;
	}

	private static BigDecimal calculateLPI(ReceiptDTO receiptDTO) {
		BigDecimal penalty = BigDecimal.ZERO;

		FinanceMain fm = receiptDTO.getFinanceMain();
		List<FinODDetails> odDetails = receiptDTO.getOdDetails();
		FeeType lppFeeType = receiptDTO.getLppFeeType();

		boolean taxApplicable = lppFeeType.isTaxApplicable();
		String component = lppFeeType.getTaxComponent();

		long finID = fm.getFinID();
		String ccy = fm.getFinCcy();

		for (FinODDetails od : odDetails) {
			BigDecimal odBal = od.getLPIBal();

			if (taxApplicable && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(component)) {
				BigDecimal receivedAmt = od.getLPIPaid().add(od.getLPIWaived());

				BigDecimal totalgst = calculateGST(finID, ccy, component, od.getLPIAmt());
				BigDecimal paidgst = calculateGST(finID, ccy, component, receivedAmt);

				odBal = odBal.add(totalgst.subtract(paidgst));
			}

			penalty = penalty.add(odBal);
		}

		return penalty;
	}

	private static BigDecimal calculateFees(ReceiptDTO receiptDTO) {
		BigDecimal feeAmount = BigDecimal.ZERO;

		List<FinFeeDetail> fees = receiptDTO.getFees();
		if (fees.isEmpty()) {
			return feeAmount;
		}
		for (FinFeeDetail fee : fees) {
			feeAmount = feeAmount.add(fee.getRemainingFeeOriginal().add(fee.getRemainingFeeGST()));
		}

		return feeAmount;
	}

	private static BigDecimal calculateGST(long finID, String finCcy, String component, BigDecimal amount) {
		return GSTCalculator.calculateGST(finID, finCcy, component, amount).gettGST();
	}
}
