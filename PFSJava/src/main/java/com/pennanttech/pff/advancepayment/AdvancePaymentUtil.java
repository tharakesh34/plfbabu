package com.pennanttech.pff.advancepayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.GSTCalculator;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;

public class AdvancePaymentUtil {

	private AdvancePaymentUtil() {
		super();
	}

	public enum AdvanceType {
		AE("AE", "Advance EMI"),
		UF("UF", "Upfront Interest Full Tenor"),
		UT("UT", "Upfront Interest Few Terms"),
		AF("AF", "Advance at Interest Frequency");

		private final String code;
		private final String value;

		private AdvanceType(String code, String value) {
			this.code = code;
			this.value = value;
		}

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static AdvanceType getType(String code) {
			for (AdvanceType type : values()) {
				if (type.getCode().equals(code)) {
					return type;
				}
			}
			return null;
		}

		public static List<Property> getGrcList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceType type : values()) {
				if (AdvanceType.AE == type) {
					continue;
				}
				if (!ImplementationConstants.ALLOW_ADVINT_FREQUENCY) {
					if (AdvanceType.AF == type) {
						continue;
					}
				}

				list.add(new Property(type.code, type.value));
			}
			return list;
		}

		public static List<Property> getRepayList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceType type : values()) {
				if (!ImplementationConstants.ALLOW_ADVINT_FREQUENCY) {
					if (AdvanceType.AF == type) {
						continue;
					}
				}
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

		public static boolean hasAdvEMI(String advanceType) {
			return AdvanceType.getType(advanceType) == AdvanceType.AE;
		}

		public static boolean hasAdvInterest(String advanceType) {
			return AdvanceType.getType(advanceType) != null && AdvanceType.getType(advanceType) != AdvanceType.AE;
		}

	}

	public enum AdvanceStage {
		FE("FE", "Front End (POS Reduction)"), RE("RE", "Rear End"), RT("RT", "Repayment Terms");

		private final String code;
		private final String value;

		private AdvanceStage(String code, String value) {
			this.code = code;
			this.value = value;
		}

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static AdvanceStage getStage(String code) {
			for (AdvanceStage type : values()) {
				if (type.getCode().equals(code)) {
					return type;
				}
			}
			return null;
		}

		public static List<Property> getList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceStage type : values()) {
				if (!ImplementationConstants.ALLOW_ADVSTAGE_REAREND) {
					if (AdvanceStage.RE == type) {
						continue;
					}
				} else if (!ImplementationConstants.ALLOW_ADVSTAGE_DEPOSIT) {
					if (AdvanceStage.RE == type) {
						continue;
					}
				}
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

		public static boolean hasDeposit(String stage) {
			return AdvanceStage.getStage(stage) == AdvanceStage.RT;
		}

		public static boolean hasFrontEnd(String stage) {
			return AdvanceStage.getStage(stage) == AdvanceStage.FE;
		}

		public static boolean hasRearEnd(String stage) {
			return AdvanceStage.getStage(stage) == AdvanceStage.RE;
		}

	}

	public enum AdvanceRuleCode {
		ADVINT, ADVEMI, CASHCLT, DSF;

		public static AdvanceRuleCode getRule(String code) {
			for (AdvanceRuleCode type : values()) {
				if (type.name().equals(code)) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * This method will calculate the Advance Interest for LMS in the below case
	 * <p>
	 * Consider only Repayments terms
	 * </p>
	 * 
	 * @param finScheduleData
	 * @param fee
	 */
	public static void calculateLMSAdvPayment(final FinScheduleData finScheduleData, FinFeeDetail fee,
			List<FinFeeDetail> prvfeesColl) {
		AdvanceRuleCode advanceRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());

		if (advanceRule == null || (advanceRule != AdvanceRuleCode.ADVINT && advanceRule != AdvanceRuleCode.ADVEMI)) {
			return;
		}

		BigDecimal oldAmount = BigDecimal.ZERO;

		if (prvfeesColl != null && !prvfeesColl.isEmpty()) {
			for (FinFeeDetail finFeeDetail : prvfeesColl) {
				oldAmount = oldAmount.add(finFeeDetail.getActualAmount());
			}
		}

		BigDecimal amount = BigDecimal.ZERO;
		FinanceMain fm = finScheduleData.getFinanceMain();
		AdvanceType advanceType = AdvanceType.getType(fm.getAdvType());
		AdvanceStage advanceStage = AdvanceStage.getStage(fm.getAdvStage());

		if (advanceRule == AdvanceRuleCode.ADVINT) {
			BigDecimal instNow = calculateAdvanseInterest(finScheduleData);
			amount = (oldAmount.subtract(instNow)).abs();
		} else if (advanceRule == AdvanceRuleCode.ADVEMI && advanceType == AdvanceType.AE
				&& advanceStage == AdvanceStage.RT) {
			BigDecimal instNow = calculateAdvanseEMI(finScheduleData);
			amount = oldAmount.subtract(instNow).abs();
		}

		fee.setActualAmount(amount);
		fee.setCalculatedAmount(amount);
		fee.setActualAmountOriginal(amount);
		fee.setNetAmountOriginal(amount);
	}

	/**
	 * This method will calculate the Advance Interest/EMI for LOS
	 * 
	 * @param finScheduleData
	 * @param fee
	 */
	public static void calculateLOSAdvPayment(final FinScheduleData finScheduleData, FinFeeDetail fee) {
		AdvanceRuleCode advanceRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());
		if (advanceRule == null || (advanceRule != AdvanceRuleCode.ADVINT && advanceRule != AdvanceRuleCode.ADVEMI)) {
			return;
		}

		BigDecimal advancePayment = BigDecimal.ZERO;

		if (advanceRule == AdvanceRuleCode.ADVINT) {
			advancePayment = calculateAdvanseInterest(finScheduleData);
		}

		if (advanceRule == AdvanceRuleCode.ADVEMI) {
			advancePayment = calculateAdvanseEMI(finScheduleData);
		}

		fee.setActualAmount(advancePayment);
		fee.setCalculatedAmount(advancePayment);
		fee.setActualAmountOriginal(advancePayment);
		fee.setNetAmountOriginal(advancePayment);
	}

	private static BigDecimal calculateAdvanseInterest(final FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		Date gracePeriodEndDate = finMain.getGrcPeriodEndDate();

		BigDecimal amount = BigDecimal.ZERO;
		AdvanceType grcAdvType = AdvanceType.getType(finMain.getGrcAdvType());
		AdvanceType rpyAdvType = AdvanceType.getType(finMain.getAdvType());

		int grcAdvTerms = getTerms(grcAdvType, finMain.getGrcAdvTerms());
		int rpyAdvTerms = getTerms(rpyAdvType, finMain.getAdvTerms());
		int grcTerm = 0;
		int rpyTerm = 0;

		for (FinanceScheduleDetail sd : schedules) {

			if (FinanceConstants.FLAG_BPI.equals(sd.getBpiOrHoliday())) {
				continue;
			}

			if (sd.isRepayOnSchDate() || sd.isPftOnSchDate()) {
				Date schDate = sd.getSchDate();

				if (schDate.compareTo(gracePeriodEndDate) <= 0) {

					if (grcTerm == grcAdvTerms) {
						continue;
					}

					if (grcAdvTerms > 0) {
						grcTerm++;
					}

					amount = amount.add(sd.getProfitSchd().subtract(sd.getTDSAmount()));
				} else {
					if (AdvanceType.AE != rpyAdvType) {

						if (rpyTerm == rpyAdvTerms) {
							continue;
						}

						if (rpyAdvTerms > 0) {
							rpyTerm++;
						}
						amount = amount.add(sd.getProfitSchd().subtract(sd.getTDSAmount()));
					}
				}
			}
		}

		return amount;
	}

	public static BigDecimal getTDSOnAdvanseInterest(Map<String, Object> dataMap) {
		BigDecimal tds = BigDecimal.ZERO;

		BigDecimal net = (BigDecimal) dataMap.get("ADVINT_N");
		BigDecimal waived = (BigDecimal) dataMap.get("ADVINT_W");
		BigDecimal paid = (BigDecimal) dataMap.get("ADVINT_P");

		if (net == null) {
			net = BigDecimal.ZERO;
		}

		if (waived == null) {
			waived = BigDecimal.ZERO;
		}

		if (paid == null) {
			paid = BigDecimal.ZERO;
		}

		tds = tds.add(net).add(waived).add(paid);

		return GSTCalculator.getTDS(tds);
	}

	public static BigDecimal calculateTDSOnAdvanseInterest(final FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		Date gracePeriodEndDate = finMain.getGrcPeriodEndDate();

		BigDecimal amount = BigDecimal.ZERO;
		AdvanceType grcAdvType = AdvanceType.getType(finMain.getGrcAdvType());
		AdvanceType rpyAdvType = AdvanceType.getType(finMain.getAdvType());

		int grcAdvTerms = getTerms(grcAdvType, finMain.getGrcAdvTerms());
		int rpyAdvTerms = getTerms(rpyAdvType, finMain.getAdvTerms());
		int grcTerm = 0;
		int rpyTerm = 0;

		for (FinanceScheduleDetail sd : schedules) {

			if (FinanceConstants.FLAG_BPI.equals(sd.getBpiOrHoliday())) {
				continue;
			}

			if (sd.isRepayOnSchDate() || sd.isPftOnSchDate()) {
				Date schDate = sd.getSchDate();

				if (schDate.compareTo(gracePeriodEndDate) <= 0) {

					if (grcTerm == grcAdvTerms) {
						continue;
					}

					if (grcAdvTerms > 0) {
						grcTerm++;
					}

					amount = amount.add(sd.getTDSAmount());
				} else {
					if (AdvanceType.AE != rpyAdvType) {

						if (rpyTerm == rpyAdvTerms) {
							continue;
						}

						if (rpyAdvTerms > 0) {
							rpyTerm++;
						}
						amount = amount.add(sd.getTDSAmount());
					}
				}
			}
		}

		return amount;
	}

	private static BigDecimal calculateAdvanseEMI(final FinScheduleData finScheduleData) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		AdvanceType rpyAdvType = AdvanceType.getType(finMain.getAdvType());
		if (AdvanceType.AE != rpyAdvType) {
			return BigDecimal.ZERO;
		}

		Date gracePeriodEndDate = finMain.getGrcPeriodEndDate();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		BigDecimal amount = BigDecimal.ZERO;
		int rpyAdvTerms = getTerms(rpyAdvType, finMain.getAdvTerms());
		int rpyTerm = 0;

		for (FinanceScheduleDetail sd : schedules) {

			if (FinanceConstants.FLAG_BPI.equals(sd.getBpiOrHoliday())) {
				continue;
			}

			if (sd.isRepayOnSchDate() || sd.isPftOnSchDate()) {
				Date schDate = sd.getSchDate();

				if (schDate.compareTo(gracePeriodEndDate) > 0) {

					if (rpyTerm == rpyAdvTerms) {
						continue;
					}
					if (rpyAdvTerms > 0) {
						rpyTerm++;
					}
					amount = amount.add(sd.getProfitSchd()).add(sd.getPrincipalSchd());

				}
			}
		}

		return amount;
	}

	/**
	 * This method will calculate the Advance Interest/EMI.
	 * 
	 * <p>
	 * Here the calculation of advance Interest at Grace terms only and Advance Interest/EMI at Repay terms.
	 * </p>
	 * 
	 * @param finScheduleData
	 * @return the calculated Advance Interest.
	 */
	public static BigDecimal calculateGrcAdvPayment(final FinScheduleData finScheduleData) {
		FinanceMain fm = finScheduleData.getFinanceMain();
		Date gracePeriodEndDate = fm.getGrcPeriodEndDate();
		String grcAdvType = fm.getGrcAdvType();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		AdvanceType advanceType;
		int terms = 0;
		BigDecimal graceAdvPayment = BigDecimal.ZERO;

		if (grcAdvType != null) {
			// calculate grace advance interest
			advanceType = AdvanceType.getType(grcAdvType);

			if (advanceType != null) {
				terms = fm.getGrcAdvTerms();
				terms = getTerms(advanceType, terms);
				graceAdvPayment = getGraceAdvPayment(schedules, terms, gracePeriodEndDate);
			}
		}
		return graceAdvPayment;
	}

	/**
	 * This method will calculate the Advance Interest/EMI.
	 * 
	 * <p>
	 * This method will consider both Advance Interest/EMI
	 * </p>
	 * 
	 * @param finScheduleData
	 * @return the calculated Advance EMI/Interest.
	 */
	public static BigDecimal calculateRepayAdvPayment(final FinScheduleData finScheduleData) {
		FinanceMain fm = finScheduleData.getFinanceMain();
		Date gracePeriodEndDate = fm.getGrcPeriodEndDate();
		String repayAdvType = fm.getAdvType();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		AdvanceType advanceType;
		int terms = 0;
		BigDecimal repayAdvPayment = BigDecimal.ZERO;

		if (repayAdvType != null) {
			// calculate repayments advance interest/EMI
			advanceType = AdvanceType.getType(repayAdvType);

			if (advanceType != null) {
				terms = fm.getAdvTerms();
				terms = getTerms(advanceType, terms);
				repayAdvPayment = getRepayAdvPayment(schedules, terms, gracePeriodEndDate, advanceType);
			}

		}

		return repayAdvPayment;
	}

	/**
	 * This method will calculate the Advance Interest/EMI
	 * 
	 * @param finScheduleData
	 * @return The Calculated Advance Interest/EMI
	 */
	public static BigDecimal calculateAdvPayment(final FinScheduleData finScheduleData) {
		BigDecimal advancePayment = BigDecimal.ZERO;
		BigDecimal graceAdvPayment = BigDecimal.ZERO;
		BigDecimal repayAdvPayment = BigDecimal.ZERO;

		graceAdvPayment = calculateGrcAdvPayment(finScheduleData);

		repayAdvPayment = calculateRepayAdvPayment(finScheduleData);

		advancePayment = advancePayment.add(graceAdvPayment).add(repayAdvPayment);
		return advancePayment;
	}

	private static int getTerms(AdvanceType advanceType, int terms) {
		if (advanceType == null) {
			return 0;
		}

		switch (advanceType) {
		case UF:
			terms = -1;
			break;
		case AF:
			terms = 1;
			break;
		case UT:
		case AE:
			break;
		default:
			break;
		}

		return terms;
	}

	/**
	 * This Method will calculate the Advance Interest at Grace terms.
	 * 
	 * @param schedules
	 *            The number of schedules.
	 * @param terms
	 *            The number of terms consider for Advance Interest/EMI, when the terms -1 consider as full terms.
	 * @param gracePeriodEndDate
	 *            Grace Period End Date.
	 * @param advanceType
	 *            Advance Type either Advance EMI/Interest.
	 * @return The calculated Advance EMI/Interest.
	 */
	private static BigDecimal getGraceAdvPayment(List<FinanceScheduleDetail> schedules, int terms,
			Date gracePeriodEndDate) {
		BigDecimal advanceProfit = BigDecimal.ZERO;

		int term = 0;
		for (FinanceScheduleDetail sd : schedules) {
			if (sd.isRepayOnSchDate() || sd.isPftOnSchDate()) {
				Date schDate = sd.getSchDate();
				BigDecimal profitSchd = sd.getProfitSchd();
				if (schDate.compareTo(gracePeriodEndDate) <= 0) {
					advanceProfit = advanceProfit.add(profitSchd);
					term++;
				}
			}

			if (term == terms) {
				break;
			}
		}

		return advanceProfit;
	}

	/**
	 * This Method will calculate the Advance Interest/EMI for Repay Terms.
	 * 
	 * @param schedules
	 *            The number of schedules.
	 * @param terms
	 *            The number of terms consider for Advance Interest/EMI, when the terms -1 consider as full terms.
	 * @param gracePeriodEndDate
	 *            Grace Period End Date.
	 * @param advanceType
	 *            Advance Type either Advance EMI/Interest.
	 * @return The calculated Advance EMI/Interest.
	 */
	private static BigDecimal getRepayAdvPayment(List<FinanceScheduleDetail> schedules, int terms,
			Date gracePeriodEndDate, AdvanceType advanceType) {
		BigDecimal advanceProfit = BigDecimal.ZERO;

		int term = 0;

		for (FinanceScheduleDetail sd : schedules) {
			if (sd.isRepayOnSchDate() || sd.isPftOnSchDate()) {
				Date schDate = sd.getSchDate();
				BigDecimal profitSchd = sd.getProfitSchd();
				if (schDate.compareTo(gracePeriodEndDate) > 0) {
					if (advanceType == AdvanceType.AE) {
						// Advance EMI
						BigDecimal principalSchd = sd.getPrincipalSchd();
						advanceProfit = advanceProfit.add(profitSchd).add(principalSchd);
					} else {
						// Advance Interest
						advanceProfit = advanceProfit.add(profitSchd);
					}
					term++;
				}
			}

			if (term == terms) {
				break;
			}
		}

		return advanceProfit;
	}
}
