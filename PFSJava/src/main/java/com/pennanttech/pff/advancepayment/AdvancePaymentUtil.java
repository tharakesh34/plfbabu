package com.pennanttech.pff.advancepayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.advancepayment.model.AdvancePayment;

public class AdvancePaymentUtil {

	private AdvancePaymentUtil() {
		super();
	}

	public enum AdvanceType {
		AE("AE", "Advance EMI"),
		UF("UF", "Upfront Full Tenor"),
		UT("UT", "Upfront Few Terms"),
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

	}

	public enum AdvanceStage {
		FE("FE", "Front End"), RE("RE", "Rear End"), AD("AD", "Deposit");

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

		public static boolean isDeposit(String stage) {
			AdvanceStage advstage = AdvanceStage.getStage(stage);
			return advstage == AdvanceStage.AD;
		}

		public static boolean hasDeposit(String stage) {
			return AdvanceStage.getStage(stage) == AdvanceStage.AD;
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
	 * 
	 * @param advancePayment
	 */
	public static void calculateDue(AdvancePayment advancePayment) {
		calculate(advancePayment, false);
	}

	/**
	 * 
	 * @param advancePayment
	 */
	public static void calculatePresement(AdvancePayment advancePayment) {
		calculate(advancePayment, true);
	}

	private static void calculate(AdvancePayment advancePayment, boolean presement) {
		AdvanceType advanceType;
		if (advancePayment.getValueDate().compareTo(advancePayment.getGrcPeriodEndDate()) <= 0) {
			advanceType = AdvanceType.getType(advancePayment.getGrcAdvType());
		} else {
			advanceType = AdvanceType.getType(advancePayment.getAdvType());
		}

		if (advanceType == null) {
			return;
		}

		switch (advanceType) {
		case UT:
		case UF:
		case AF:
			for (FinExcessAmount excessAmount : advancePayment.getExcessAmounts()) {
				if (AdvanceRuleCode.getRule(excessAmount.getAmountType()) == AdvanceRuleCode.ADVINT) {
					advancePayment.setAvailableAmt(excessAmount.getAmount());
					advancePayment.setAdvancePaymentType(excessAmount.getAmountType());
					advancePayment.setFinExcessAmount(excessAmount);
					calculateAdvanceInterest(advancePayment);
					break;
				}
			}

			break;
		case AE:
			for (FinExcessAmount excessAmount : advancePayment.getExcessAmounts()) {
				if (AdvanceRuleCode.getRule(excessAmount.getAmountType()) == AdvanceRuleCode.ADVEMI) {
					advancePayment.setAdvancePaymentType(excessAmount.getAmountType());
					advancePayment.setAvailableAmt(excessAmount.getAmount());
					advancePayment.setFinExcessAmount(excessAmount);
					calculateAdvanceEMI(advancePayment);
					break;
				}
			}
			break;
		default:
			break;
		}
	}

	private static void calculateAdvanceInterest(AdvancePayment advancePayment) {
		BigDecimal intAdjusted;
		BigDecimal intDue;
		BigDecimal intAdvAvailable = advancePayment.getAvailableAmt();

		intDue = advancePayment.getSchdIntDue();
		if (advancePayment.getAvailableAmt().compareTo(intDue) >= 0) {
			intAdjusted = intDue;
		} else {
			intAdjusted = advancePayment.getAvailableAmt();
		}

		intAdvAvailable = intAdvAvailable.subtract(intAdjusted);
		intDue = intDue.subtract(intAdjusted);

		advancePayment.setIntAdjusted(intAdjusted);
		advancePayment.setIntAdvAvailable(intAdvAvailable);
		advancePayment.setIntDue(intDue);
	}

	private static void calculateAdvanceEMI(AdvancePayment advancePayment) {
		BigDecimal emiAdjusted;
		BigDecimal emiDue;
		BigDecimal emiAdvAvailable = advancePayment.getAvailableAmt();

		emiDue = advancePayment.getSchdPriDue();
		if (emiAdvAvailable.compareTo(emiDue) >= 0) {
			emiAdjusted = emiDue;
		} else {
			emiAdjusted = emiAdvAvailable;
		}

		emiAdvAvailable = emiAdvAvailable.subtract(emiAdjusted);
		emiDue = emiDue.subtract(emiAdjusted);

		advancePayment.setEmiAdjusted(emiAdjusted);
		advancePayment.setEmiAdvAvailable(emiAdvAvailable);
		advancePayment.setEmiDue(emiDue);
	}

	/**
	 * This method will calculate the Advance Interest for LMS in the below case
	 * <p>
	 * Consider only Repayments terms
	 * </p>
	 * <p>
	 * Consider only Full Terms
	 * </p>
	 * 
	 * @param finScheduleData
	 * @param fee
	 */
	public static void calculateLMSAdvPayment(final FinScheduleData finScheduleData, FinFeeDetail fee) {
		AdvanceRuleCode advanceRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());

		if (advanceRule == null && (advanceRule != AdvanceRuleCode.ADVINT)) {
			return;
		}

		FinanceMain fm = finScheduleData.getFinanceMain();
		String repayAdvType = fm.getAdvType();

		AdvanceType advanceType = AdvanceType.getType(repayAdvType);

		if (advanceType != AdvanceType.UF) {
			return;
		}

		BigDecimal advanceIntrest = finScheduleData.getPftChg();
		fee.setActualAmount(advanceIntrest);
		fee.setCalculatedAmount(advanceIntrest);
		fee.setActualAmountOriginal(advanceIntrest);
		fee.setNetAmountOriginal(advanceIntrest);
	}

	/**
	 * This method will calculate the Advance Interest/EMI for LOS
	 * 
	 * @param finScheduleData
	 * @param fee
	 */
	public static void calculateLOSAdvPayment(final FinScheduleData finScheduleData, FinFeeDetail fee) {
		AdvanceRuleCode advanceRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());

		if (advanceRule == null && (advanceRule != AdvanceRuleCode.ADVINT || advanceRule != AdvanceRuleCode.ADVEMI)) {
			return;
		}

		FinanceMain fm = finScheduleData.getFinanceMain();
		Date gracePeriodEndDate = fm.getGrcPeriodEndDate();
		String grcAdvType = fm.getGrcAdvType();
		String repayAdvType = fm.getAdvType();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		AdvanceType advanceType;
		int terms = 0;
		BigDecimal advancePayment = BigDecimal.ZERO;
		BigDecimal graceAdvPayment = BigDecimal.ZERO;
		BigDecimal repayAdvPayment = BigDecimal.ZERO;

		if (grcAdvType != null) {
			// calculate grace advance interest
			advanceType = AdvanceType.getType(grcAdvType);

			if (advanceType != null) {
				terms = fm.getGrcAdvTerms();
				terms = getTerms(advanceType, terms);
				graceAdvPayment = getGraceAdvPayment(schedules, terms, gracePeriodEndDate);
			}
		}

		if (repayAdvType != null) {
			// calculate repayments advance interest/EMI
			advanceType = AdvanceType.getType(repayAdvType);

			if (advanceType != null) {
				terms = fm.getAdvTerms();
				terms = getTerms(advanceType, terms);
				repayAdvPayment = getRepayAdvPayment(schedules, terms, gracePeriodEndDate, advanceType);
			}

		}

		advancePayment = advancePayment.add(graceAdvPayment).add(repayAdvPayment);

		fee.setActualAmount(advancePayment);
		fee.setCalculatedAmount(advancePayment);
		fee.setActualAmountOriginal(advancePayment);
		fee.setNetAmountOriginal(advancePayment);
	}

	private static int getTerms(AdvanceType advanceType, int terms) {
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
						BigDecimal principalSchd = sd.getPrincipalSchd();
						advanceProfit = advanceProfit.add(profitSchd).add(principalSchd);
					} else {
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
