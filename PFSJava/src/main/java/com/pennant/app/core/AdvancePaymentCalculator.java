package com.pennant.app.core;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.AdvancePayment;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.util.AdvanceEMI.AdvanceRuleCode;
import com.pennant.backend.util.AdvanceEMI.AdvanceType;

public class AdvancePaymentCalculator {

	public static void calculateDue(AdvancePayment advancePayment) {
		calculate(advancePayment, false);
	}

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
		BigDecimal intAdjusted = BigDecimal.ZERO;
		BigDecimal intDue = BigDecimal.ZERO;
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
		BigDecimal emiAdjusted = BigDecimal.ZERO;
		BigDecimal emiDue = BigDecimal.ZERO;
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

}
