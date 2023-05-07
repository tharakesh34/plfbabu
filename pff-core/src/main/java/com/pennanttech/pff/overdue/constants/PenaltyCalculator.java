package com.pennanttech.pff.overdue.constants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class PenaltyCalculator {

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private PenaltyCalculator() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static FinODPenaltyRate getEffectiveRate(Date dueDate, List<FinODPenaltyRate> prList) {
		return getEffectiveRate(dueDate, prList, null);
	}

	public static FinODPenaltyRate getEffectiveRate(Date dueDate, List<FinODPenaltyRate> prList, String odChargeType) {
		int idx = -1;

		if (CollectionUtils.isEmpty(prList)) {
			return new FinODPenaltyRate();
		}

		for (int i = 0; i < prList.size(); i++) {
			if (prList.get(i).getFinEffectDate().compareTo(dueDate) > 0) {
				break;
			}

			if (StringUtils.isEmpty(odChargeType)) {
				//
			} else if (!StringUtils.equals(prList.get(i).getODChargeType(), odChargeType)) {
				continue;
			}

			idx = i;
		}

		if (idx < 0) {
			return new FinODPenaltyRate();
		}

		return prList.get(idx);
	}

	public static FinODPenaltyRate getEffectiveRate(FinanceScheduleDetail schd, List<FinODPenaltyRate> penaltyRates,
			String odChargeType) {
		return getEffectiveRate(schd.getSchDate(), penaltyRates, odChargeType);
	}

	public static BigDecimal getEffectiveODCharge(FinanceMain fm, Date movementDate) {
		List<FinODPenaltyRate> penaltyRates = fm.getPenaltyRates();
		BigDecimal odRate = BigDecimal.ZERO;

		if (!penaltyRates.isEmpty()) {
			odRate = penaltyRates.get(0).getODChargeAmtOrPerc();
		}

		for (FinODPenaltyRate penaltyRate : penaltyRates) {
			if (movementDate.compareTo(penaltyRate.getFinEffectDate()) > 0) {
				odRate = penaltyRate.getODChargeAmtOrPerc();
			}
		}
		return odRate;
	}
}
