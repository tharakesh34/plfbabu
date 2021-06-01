package com.pennanttech.pff.advancepayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.AdvancePaymentDetail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;

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

				if ((AdvanceType.UF == type || AdvanceType.UT == type || AdvanceType.AF == type)
						&& !ImplementationConstants.ALLOW_ADV_INT) {
					continue;
				}

				list.add(new Property(type.code, type.value));
			}
			return list;
		}

		public static List<Property> getRepayList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceType type : values()) {
				if (AdvanceType.AE == type && !ImplementationConstants.ALLOW_ADV_EMI) {
					continue;
				}

				if ((AdvanceType.UF == type || AdvanceType.UT == type || AdvanceType.AF == type)
						&& !ImplementationConstants.ALLOW_ADV_INT) {
					continue;
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

		public static boolean hasAdvInterest(FinanceMain fm) {
			String grcAdvType = fm.getGrcAdvType();
			String advType = fm.getAdvType();
			return AdvanceType.hasAdvInterest(grcAdvType) || AdvanceType.hasAdvInterest(advType);
		}

		public static boolean hasAdvEMI(FinanceMain fm) {
			String grcAdvType = fm.getGrcAdvType();
			String advType = fm.getAdvType();
			return AdvanceType.hasAdvEMI(grcAdvType) || AdvanceType.hasAdvEMI(advType);
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
				if (AdvanceStage.FE == type && !ImplementationConstants.ADV_EMI_STAGE_FRONT_END) {
					continue;
				} else if (AdvanceStage.RE == type && !ImplementationConstants.ADV_EMI_STAGE_REAR_END) {
					continue;
				} else if (AdvanceStage.RT == type && !ImplementationConstants.ADV_EMI_STAGE_REPAY_TERMS) {
					continue;
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
							break;
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

	/**
	 * Method for creating Finance Advance Payment Object with the Totals if applicable
	 * 
	 * @param finScheduleData
	 * @return
	 */
	public static AdvancePaymentDetail calculateAdvancePayment(final FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		Date gracePeriodEndDate = finMain.getGrcPeriodEndDate();

		BigDecimal advInt = BigDecimal.ZERO;
		BigDecimal advIntTds = BigDecimal.ZERO;
		BigDecimal advEMI = BigDecimal.ZERO;
		BigDecimal advEMITds = BigDecimal.ZERO;

		AdvanceType grcAdvType = AdvanceType.getType(finMain.getGrcAdvType());
		AdvanceType rpyAdvType = AdvanceType.getType(finMain.getAdvType());

		int grcAdvTerms = getTerms(grcAdvType, finMain.getGrcAdvTerms());
		int rpyAdvTerms = getTerms(rpyAdvType, finMain.getAdvTerms());
		int grcTerm = 0;
		int rpyTerm = 0;

		if (grcAdvTerms == 0 && rpyAdvTerms == 0) {
			return null;
		}

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

					advInt = advInt.add(sd.getProfitSchd().subtract(sd.getTDSAmount()));
					advIntTds = advIntTds.add(sd.getTDSAmount());
				} else {
					if (AdvanceType.AE != rpyAdvType) {

						if (rpyTerm == rpyAdvTerms) {
							break;
						}

						if (rpyAdvTerms > 0) {
							rpyTerm++;
						}
						advInt = advInt.add(sd.getProfitSchd().subtract(sd.getTDSAmount()));
						advIntTds = advIntTds.add(sd.getTDSAmount());
					} else {
						if (schDate.compareTo(gracePeriodEndDate) > 0) {

							if (rpyTerm == rpyAdvTerms) {
								continue;
							}
							if (rpyAdvTerms > 0) {
								rpyTerm++;
							}
							advEMI = advEMI.add(sd.getProfitSchd()).add(sd.getPrincipalSchd())
									.subtract(sd.getTDSAmount());
							advEMITds = advEMITds.add(sd.getTDSAmount());
						}
					}
				}
			}
		}

		// Creating Finance Advance Payment with totals
		AdvancePaymentDetail advPay = new AdvancePaymentDetail();
		advPay.setFinReference(finMain.getFinReference());
		advPay.setAdvInt(advInt);
		advPay.setAdvIntTds(advIntTds);
		advPay.setAdvEMI(advEMI);
		advPay.setAdvEMITds(advEMITds);

		return advPay;
	}

	/**
	 * Method for Fetching Difference TDS Un-Incomized Amount to incomize Up-front When Interest amount Changed
	 * 
	 * @param finScheduleData
	 * @return
	 */
	public static AdvancePaymentDetail getDiffOnAdvIntAndAdvEMI(final FinScheduleData finScheduleData,
			AdvancePaymentDetail advPay, String moduleDefiner) {

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String grcAdvType = financeMain.getGrcAdvType();
		String advType = financeMain.getAdvType();
		if (!AdvanceType.hasAdvInterest(grcAdvType) && !AdvanceType.hasAdvInterest(advType)) {
			return null;
		}

		/* Calculate Total TDS Amount based on Terms from Scheduled Installments */
		AdvancePaymentDetail curAdvPay = calculateAdvancePayment(finScheduleData);
		if (curAdvPay == null) {
			return curAdvPay;
		}

		if (advPay == null) {
			return curAdvPay;
		}

		/*
		 * Loan Already In servicing Process, Need to Cross Check whether diff incomization Required or not Find out the
		 * difference and adjust to Bean for Further Process
		 */
		curAdvPay.setAdvInt(curAdvPay.getAdvInt().subtract(advPay.getAdvInt()));
		curAdvPay.setAdvIntTds(curAdvPay.getAdvIntTds().subtract(advPay.getAdvIntTds()));
		curAdvPay.setAdvEMI(curAdvPay.getAdvEMI().subtract(advPay.getAdvEMI()));
		curAdvPay.setAdvEMITds(curAdvPay.getAdvEMITds().subtract(advPay.getAdvEMITds()));

		if (!ImplementationConstants.RCVADV_CREATE_ON_INTEMI) {
			if (!StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ADDDISB)) {
				if (curAdvPay.getAdvInt().compareTo(BigDecimal.ZERO) > 0) {
					curAdvPay.setAdvInt(BigDecimal.ZERO);
					curAdvPay.setAdvIntTds(BigDecimal.ZERO);
				}
				if (curAdvPay.getAdvEMI().compareTo(BigDecimal.ZERO) > 0) {
					curAdvPay.setAdvEMI(BigDecimal.ZERO);
					curAdvPay.setAdvEMITds(BigDecimal.ZERO);
				}
			}
		}

		if (!ImplementationConstants.PYBADV_CREATE_ON_INTEMI) {
			if (curAdvPay.getAdvInt().compareTo(BigDecimal.ZERO) < 0) {
				curAdvPay.setAdvInt(BigDecimal.ZERO);
				curAdvPay.setAdvIntTds(BigDecimal.ZERO);
			}
			if (curAdvPay.getAdvEMI().compareTo(BigDecimal.ZERO) < 0) {
				curAdvPay.setAdvEMI(BigDecimal.ZERO);
				curAdvPay.setAdvEMITds(BigDecimal.ZERO);
			}
		}

		return curAdvPay;

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

	public static void setAdvancePaymentDetails(FinScheduleData finScheduleData, AEAmountCodes amountCodes,
			String moduleDefiner) {// Advance payment Details Resetting
		AdvancePaymentDetail curAdvpay = null;

		curAdvpay = getDiffOnAdvIntAndAdvEMI(finScheduleData, null, moduleDefiner);

		if (curAdvpay == null) {
			return;
		}

		amountCodes.setIntAdjusted(curAdvpay.getAdvInt());
		amountCodes.setEmiAdjusted(curAdvpay.getAdvEMI());

		if (SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF)) {
			amountCodes.setIntTdsAdjusted(curAdvpay.getAdvIntTds());
			amountCodes.setEmiTdsAdjusted(curAdvpay.getAdvEMITds());
		} else {
			amountCodes.setIntTdsAdjusted(BigDecimal.ZERO);
			amountCodes.setEmiTdsAdjusted(BigDecimal.ZERO);
		}
	}

	public static boolean advPayUpdateReq(String moduleDefiner) {
		boolean advPayUpdReq = false;
		switch (moduleDefiner) {
		case FinanceConstants.FINSER_EVENT_ORG:
		case FinanceConstants.FINSER_EVENT_ADDDISB:
		case FinanceConstants.FINSER_EVENT_RATECHG:
		case FinanceConstants.FINSER_EVENT_ADDTERM:
		case FinanceConstants.FINSER_EVENT_RMVTERM:
		case FinanceConstants.FINSER_EVENT_CANCELDISB:
		case FinanceConstants.FINSER_EVENT_CHGPFT:
		case FinanceConstants.FINSER_EVENT_CHGFRQ:
		case FinanceConstants.FINSER_EVENT_PLANNEDEMI:
		case FinanceConstants.FINSER_EVENT_UNPLANEMIH:
		case FinanceConstants.FINSER_EVENT_RESCHD:
		case FinanceConstants.FINSER_EVENT_RECALCULATE:
		case FinanceConstants.FINSER_EVENT_CHGRPY:
			advPayUpdReq = true;
			break;
		default:
			break;
		}
		return advPayUpdReq;
	}
}
