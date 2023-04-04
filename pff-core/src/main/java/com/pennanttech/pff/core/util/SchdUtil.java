package com.pennanttech.pff.core.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.schdule.RepaymentStatus;

public class SchdUtil {

	private SchdUtil() {
		super();
	}

	public static List<FinanceScheduleDetail> sort(List<FinanceScheduleDetail> schedules) {
		return sort(schedules, false);
	}

	public static List<FinanceScheduleDetail> sort(List<FinanceScheduleDetail> schedules, boolean desc) {
		if (desc) {
			return schedules.stream().sorted((schd1, schd2) -> schd2.getSchDate().compareTo(schd1.getSchDate()))
					.collect(Collectors.toList());
		}

		return schedules.stream().sorted((schd1, schd2) -> schd1.getSchDate().compareTo(schd2.getSchDate()))
				.collect(Collectors.toList());
	}

	public static int getFutureInstalments(Date businessDate, List<FinanceScheduleDetail> schedules) {

		return schedules.stream()
				.filter(schd -> businessDate.compareTo(schd.getSchDate()) <= 0 && schd.isRepayOnSchDate())
				.collect(Collectors.toList()).size();

	}

	public static BigDecimal getNextEMI(Date businessDate, List<FinanceScheduleDetail> schedules) {
		FinanceScheduleDetail nextInstalment = getNextInstalment(businessDate, schedules);

		if (nextInstalment != null) {
			return nextInstalment.getRepayAmount();
		}

		return BigDecimal.ZERO;
	}

	public static FinanceScheduleDetail getNextInstalment(Date businessDate, List<FinanceScheduleDetail> schedules) {
		return sort(schedules).stream()
				.filter(schdedule -> businessDate.compareTo(schdedule.getSchDate()) < 0 && schdedule.isRepayOnSchDate())
				.findAny().orElse(null);

	}

	public static BigDecimal getTotalPrincipalSchd(List<FinanceScheduleDetail> schedules) {
		return schedules.stream().filter(schd -> schd.isRepayOnSchDate()).collect(Collectors.toList()).stream()
				.map(FinanceScheduleDetail::getPrincipalSchd).reduce(BigDecimal.ZERO, BigDecimal::add);

	}

	public static BigDecimal getTotalRepayAmount(List<FinanceScheduleDetail> schedules) {
		return schedules.stream()
				.map(schd -> (schd.getProfitSchd().add(schd.getPrincipalSchd()).add(schd.getFeeSchd()))
						.subtract(schd.getSchdPftPaid().add(schd.getSchdPriPaid()).add(schd.getSchdFeePaid())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static RepaymentStatus getRepaymentStatus(FinanceScheduleDetail schd) {
		if (!schd.isRepayOnSchDate()) {
			return RepaymentStatus.NA;
		}

		BigDecimal schdAmount = schd.getProfitSchd().add(schd.getPrincipalSchd()).add(schd.getFeeSchd());

		BigDecimal paidAmount = schd.getSchdPftPaid().add(schd.getSchdPriPaid()).add(schd.getSchdFeePaid());

		BigDecimal balanceAmount = schdAmount.subtract(paidAmount);

		if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return RepaymentStatus.PAID;
		}

		if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return RepaymentStatus.UNPAID;
		}

		return RepaymentStatus.PARTIALLY_PAID;
	}

	public static BigDecimal getOverDueEMI(Date businessDate, List<FinanceScheduleDetail> schedules) {
		List<FinanceScheduleDetail> list = schedules.stream()
				.filter(schd -> businessDate.compareTo(schd.getSchDate()) >= 0 && schd.isRepayOnSchDate())
				.collect(Collectors.toList());

		BigDecimal overDueEMI = list.stream()
				.map(schd -> (schd.getProfitSchd().add(schd.getPrincipalSchd())
						.subtract((schd.getSchdPftPaid().add(schd.getSchdPriPaid())))))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return overDueEMI.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : overDueEMI;
	}

	public static boolean isEMICleared(FinanceScheduleDetail schd) {
		if (!schd.isRepayOnSchDate()) {
			return true;
		}

		BigDecimal schdAmount = schd.getProfitSchd().add(schd.getPrincipalSchd());
		BigDecimal paidAmount = schd.getSchdPftPaid().add(schd.getSchdPriPaid());

		BigDecimal balanceAmount = schdAmount.subtract(paidAmount);

		if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return true;
		}

		return false;
	}

	public static boolean isEMINotCleared(FinanceScheduleDetail schd) {
		return !isEMICleared(schd);
	}

	public static int getPastDueDays(List<FinanceScheduleDetail> schedules, Date eodDate, int pastDueDays) {
		int curPastDueDays = 0;
		Date fromDate = eodDate;

		schedules = sort(schedules, true);

		for (FinanceScheduleDetail curSchd : schedules) {
			Date schDate = curSchd.getSchDate();
			int duedays = pastDueDays;

			if (eodDate.compareTo(schDate) > 0) {
				if (pastDueDays == 0) {
					duedays = DateUtil.getDaysBetween(fromDate, schDate);
					fromDate = schDate;
				}

				if (isEMINotCleared(curSchd)) {
					curPastDueDays = curPastDueDays + duedays;
				}
			}
		}

		return curPastDueDays;
	}
}
