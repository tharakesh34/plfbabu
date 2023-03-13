package com.pennanttech.pff.core.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class SchdUtil {

	private SchdUtil() {
		super();
	}

	public static List<FinanceScheduleDetail> sort(List<FinanceScheduleDetail> schedules) {
		Collections.sort(schedules, new Comparator<FinanceScheduleDetail>() {
			@Override
			public int compare(FinanceScheduleDetail schd1, FinanceScheduleDetail schd2) {
				return DateUtil.compare(schd1.getSchDate(), schd2.getSchDate());
			}
		});
		return schedules;
	}

	public static int getFutureInstalments(Date businessDate, List<FinanceScheduleDetail> schedules) {
		int futureInstalments = 0;
		for (FinanceScheduleDetail schd : schedules) {
			if (businessDate.compareTo(schd.getSchDate()) <= 0 && schd.isRepayOnSchDate()) {
				futureInstalments++;
			}
		}
		return futureInstalments;
	}

	public static BigDecimal getNextEMI(Date businessDate, List<FinanceScheduleDetail> schedules) {
		FinanceScheduleDetail nextInstalment = getNextInstalment(businessDate, schedules);

		if (nextInstalment != null) {
			return nextInstalment.getRepayAmount();
		}

		return BigDecimal.ZERO;
	}

	public static FinanceScheduleDetail getNextInstalment(Date businessDate, List<FinanceScheduleDetail> schedules) {

		for (FinanceScheduleDetail schd : sort(schedules)) {
			if (businessDate.compareTo(schd.getSchDate()) < 0 && schd.isRepayOnSchDate()) {

				return schd;
			}
		}

		return null;
	}

	public static BigDecimal getTotalPrincipalSchd(List<FinanceScheduleDetail> schedules) {
		BigDecimal principalSchd = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schedules) {
			if (schd.isRepayOnSchDate()) {
				principalSchd = principalSchd.add(schd.getPrincipalSchd());
			}
		}
		return principalSchd;
	}

	public static BigDecimal getTotalRepayAmount(List<FinanceScheduleDetail> schedules) {
		BigDecimal totalRepayAmount = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schedules) {
			totalRepayAmount = totalRepayAmount.add(schd.getProfitSchd());
			totalRepayAmount = totalRepayAmount.subtract(schd.getSchdPftPaid());

			totalRepayAmount = totalRepayAmount.add(schd.getPrincipalSchd());
			totalRepayAmount = totalRepayAmount.subtract(schd.getSchdPriPaid());

			totalRepayAmount = totalRepayAmount.add(schd.getFeeSchd());
			totalRepayAmount = totalRepayAmount.subtract(schd.getSchdFeePaid());
		}

		return totalRepayAmount;

	}
}
