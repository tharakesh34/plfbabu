package com.pennant.backend.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.AdvancePayment;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

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
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

		public static List<Property> getRepayList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceType type : values()) {
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

	}

	public enum AdvanceStage {
		FE("FE", "Front End"), RE("RE", "Rear End");

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
				list.add(new Property(type.code, type.value));
			}
			return list;
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

	public static List<FinExcessAmount> getExcessAmounts(String finReference, List<FinFeeDetail> fees) {
		List<FinExcessAmount> finExcessAmounts = new ArrayList<>();

		if (fees == null) {
			return finExcessAmounts;
		}

		for (FinFeeDetail fee : fees) {
			AdvanceRuleCode advanceRuleCode = AdvanceRuleCode.getRule(fee.getFeeTypeCode());

			if (advanceRuleCode == null) {
				continue;
			}

			BigDecimal excessAmount = fee.getActualAmountOriginal();

			if (excessAmount == null) {
				excessAmount = BigDecimal.ZERO;
			}

			FinExcessAmount finExcessAmount = new FinExcessAmount();
			finExcessAmount.setFinReference(finReference);
			finExcessAmount.setAmountType(advanceRuleCode.name());
			finExcessAmount.setAmount(excessAmount);
			finExcessAmount.setUtilisedAmt(BigDecimal.ZERO);
			finExcessAmount.setReservedAmt(BigDecimal.ZERO);
			finExcessAmount.setBalanceAmt(excessAmount);

			finExcessAmounts.add(finExcessAmount);
		}

		return finExcessAmounts;
	}

	public static void setAdvancePayment(final FinScheduleData finScheduleData, FinFeeDetail fee) {
		if (fee.getFeeTypeCode().equals(AdvanceRuleCode.ADVINT.name())) {
			setAdvancePayment(finScheduleData, fee, AdvanceRuleCode.ADVINT);
		}
		if (fee.getFeeTypeCode().equals(AdvanceRuleCode.ADVEMI.name())) {
			setAdvancePayment(finScheduleData, fee, AdvanceRuleCode.ADVEMI);
		}
	}

	private static void setAdvancePayment(final FinScheduleData finScheduleData, FinFeeDetail fee,
			AdvanceRuleCode advanceRuleCode) {
		String finTypeCode = fee.getFeeTypeCode();
		String finEvent = fee.getFinEvent();

		FinanceMain fm = finScheduleData.getFinanceMain();
		Date gracePeriodEndDate = fm.getGrcPeriodEndDate();
		String grcAdvType = fm.getGrcAdvType();
		int grcTerms = fm.getGrcAdvTerms();
		String advType = fm.getAdvType();
		int advTerms = fm.getAdvTerms();

		if ((grcAdvType == null && advType == null) || !(advanceRuleCode.name().equals(finTypeCode))) {
			return;
		}

		AdvanceType advanceType = AdvanceType.getType(advType);
		AdvanceType grcadvanceType = AdvanceType.getType(grcAdvType);

		if (advanceType == null && grcadvanceType == null) {
			return;
		}

		BigDecimal advanceIntrest = BigDecimal.ZERO;
		BigDecimal graceAdvanceIntrest = BigDecimal.ZERO;
		BigDecimal repayAdvanceIntrest = BigDecimal.ZERO;

		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		if (fm.getGrcAdvType() != null) {
			graceAdvanceIntrest = getAdvanceInterst("GRACE", grcTerms, gracePeriodEndDate, grcadvanceType, schedules);
		}

		if (fm.getAdvType() != null) {
			repayAdvanceIntrest = getAdvanceInterst("REPAY", advTerms, gracePeriodEndDate, advanceType, schedules);
		}

		/*
		 * if (AccountEventConstants.ACCEVENT_ADDDBSN.equals(finEvent)) { if (advanceRuleCode == AdvanceRuleCode.ADVINT)
		 * { advanceIntrest = advanceIntrest.subtract(fm.getTotalGrossPft()); } else if (advanceRuleCode ==
		 * AdvanceRuleCode.ADVEMI) { advanceIntrest =
		 * advanceIntrest.subtract(fm.getTotalProfit().add(fm.getTotalPriAmt())); } }
		 */

		// FIXME MUR>> Need to validate Frequency and Number of Terms
		if (AccountEventConstants.ACCEVENT_ADDDBSN.equals(finEvent)) {
			advanceIntrest = finScheduleData.getPftChg();
			graceAdvanceIntrest = BigDecimal.ZERO;
			repayAdvanceIntrest = BigDecimal.ZERO;
		}

		advanceIntrest = advanceIntrest.add(graceAdvanceIntrest).add(repayAdvanceIntrest);

		fee.setActualAmount(advanceIntrest);
		fee.setCalculatedAmount(advanceIntrest);
		fee.setActualAmountOriginal(advanceIntrest);

		// FIXME MUR>> Need to validate Frequency and Number of Terms
		fm.setAdvanceEMI(advanceIntrest);
	}

	private static BigDecimal getAdvanceInterst(String type, int terms, Date gracePeriodEndDate,
			AdvanceType advanceType, List<FinanceScheduleDetail> schedules) {

		if (advanceType == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal advanceProfit;
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

		advanceProfit = getAdvanceProfit(advanceType, type, terms, gracePeriodEndDate, schedules);
		return advanceProfit;
	}

	private static BigDecimal getAdvanceProfit(AdvanceType advanceType, String type, int terms, Date gracePeriodEndDate,
			List<FinanceScheduleDetail> schedules) {
		BigDecimal advanceProfit = BigDecimal.ZERO;

		int term = 0;

		for (FinanceScheduleDetail sd : schedules) {
			if (sd.isRepayOnSchDate() || sd.isPftOnSchDate()) {
				Date schDate = sd.getSchDate();
				BigDecimal profitSchd = sd.getProfitSchd();
				if (schDate.compareTo(gracePeriodEndDate) <= 0 && "GRACE".equals(type)) {
					advanceProfit = advanceProfit.add(profitSchd);
					term++;
				}

				if (schDate.compareTo(gracePeriodEndDate) > 0 && "REPAY".equals(type)) {
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
