package com.pennanttech.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.overdue.constants.ChargeType;

public class PenaltyTest {
	public static void main(String arg[]) {
		List<FinODPenaltyRate> prrates = new ArrayList<>();
		FinODPenaltyRate pr1 = new FinODPenaltyRate();
		FinODPenaltyRate pr2 = new FinODPenaltyRate();

		pr1.setFinEffectDate(DateUtil.getDate(2019, 7, 11));
		pr1.setODChargeType("E");
		pr1.setODChargeAmtOrPerc(new BigDecimal("1200"));
		prrates.add(pr1);

		pr2.setFinEffectDate(DateUtil.getDate(2019, 10, 12));
		pr2.setODChargeType("E");
		pr2.setODChargeAmtOrPerc(new BigDecimal("1600"));
		prrates.add(pr2);

		Date datePrv = DateUtil.getDate(2019, 7, 15);
		Date dateCur = DateUtil.getDate(2019, 10, 17);

		PenaltyCalculation(prrates, datePrv, dateCur);

	}

	private static void PenaltyCalculation(List<FinODPenaltyRate> rates, Date datePrv, Date dateCur) {
		BigDecimal penalty = BigDecimal.ZERO;
		String idb = CalculationConstants.IDB_ACT_360;
		BigDecimal balanceForCal = new BigDecimal("1780600");

		boolean effectivedueExsist = rates.stream()
				.anyMatch(pr -> pr.getODChargeType().equals(ChargeType.PERC_ON_EFF_DUE_DAYS));

		Date effectDatecru = null;
		BigDecimal effectiveRate = BigDecimal.ZERO;
		if (effectivedueExsist) {
			for (int i = 1; i < rates.size(); i++) {
				FinODPenaltyRate ratecur = rates.get(i);
				FinODPenaltyRate rateprv = rates.get(i - 1);

				effectDatecru = DateUtil.getDatePart(ratecur.getFinEffectDate());
				Date effectDateprv = DateUtil.getDatePart(rateprv.getFinEffectDate());

				effectiveRate = ratecur.getODChargeAmtOrPerc();

				if (datePrv.compareTo(effectDateprv) >= 0 && datePrv.compareTo(effectDatecru) <= 0) {
					BigDecimal penaltyRate = rateprv.getODChargeAmtOrPerc().divide(new BigDecimal(100), 2,
							RoundingMode.HALF_DOWN);
					penalty = penalty.add(CalculationUtil.calInterest(datePrv, DateUtil.addDays(effectDatecru, -1),
							balanceForCal, idb, penaltyRate));

					penalty = CalculationUtil.roundAmount(penalty, "HALF_UP", 100);
					datePrv = effectDatecru;

					System.out.println("PenatlyCalculated\n\n\n" + penalty);
				}
			}
		}

		if (effectDatecru.compareTo(dateCur) <= 0) {
			BigDecimal penaltyRate = effectiveRate.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN);
			penalty = penalty.add(CalculationUtil.calInterest(effectDatecru, dateCur, balanceForCal, idb, penaltyRate));

			penalty = CalculationUtil.roundAmount(penalty, "HALF_UP", 100);

			System.out.println("PenatlyCalculated\n\n\n" + penalty);
		}
	}
}
