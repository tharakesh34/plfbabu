package com.pennanttech.pff.core.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.schdule.RepaymentStatus;

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

	public static int getPaidInstalments(List<FinanceScheduleDetail> schedules) {

		schedules.forEach(schd -> {
			if (schd.isRepayOnSchDate())
				schd.setLoanEMIStatus(SchdUtil.getRepaymentStatus(schd).repaymentStatus());
		});

		return schedules.stream().filter(schd -> "Paid".equals(schd.getLoanEMIStatus())).collect(Collectors.toList())
				.size();
	}

	public static BigDecimal getOutStandingPrincipal(List<FinanceScheduleDetail> schedules, Date businessDate) {
		return schedules.stream()
				.filter(schd -> businessDate.compareTo(schd.getSchDate()) <= 0 && schd.isRepayOnSchDate())
				.collect(Collectors.toList()).stream().map(FinanceScheduleDetail::getPrincipalSchd)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal getLPPDueAmount(List<FinODDetails> odDetails) {

		return odDetails.stream().map(FinODDetails::getLppDueAmt).reduce(BigDecimal.ZERO, BigDecimal::add);

	}
}
