package com.pennanttech.pff.overdue.constants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class PenaltyCalculator {

	public static FinODPenaltyRate getEffectiveRate(Date dueDate, List<FinODPenaltyRate> penaltyRates) {
		FinODPenaltyRate penlrate = new FinODPenaltyRate();

		if (penaltyRates == null) {
			return new FinODPenaltyRate();
		}

		for (FinODPenaltyRate penaltyRate : penaltyRates) {
			if (dueDate.compareTo(penaltyRate.getFinEffectDate()) > 0) {
				penlrate = penaltyRate;
			}
		}

		return penlrate;
	}

	public static FinODPenaltyRate getEffectiveRate(FinanceScheduleDetail schd, List<FinODPenaltyRate> penaltyRates) {
		return getEffectiveRate(schd.getSchDate(), penaltyRates);
	}

	public static BigDecimal getEffectiveODCharge(FinanceMain fm, Date movementDate) {
		FinODPenaltyRate defPenaltyRate = new FinODPenaltyRate();
		List<FinODPenaltyRate> penaltyRates = fm.getPenaltyRates();
		BigDecimal odRate = BigDecimal.ZERO;

		if (penaltyRates.size() > 0) {
			defPenaltyRate = penaltyRates.get(0);
			odRate = defPenaltyRate.getODChargeAmtOrPerc();
		}

		for (FinODPenaltyRate penaltyRate : penaltyRates) {
			if (movementDate.compareTo(penaltyRate.getFinEffectDate()) > 0) {
				odRate = penaltyRate.getODChargeAmtOrPerc();
			}
		}
		return odRate;
	}
}
