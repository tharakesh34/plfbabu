package com.pennant.util;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;

public class BeanFactory {
	final static Date NEXT_GRACE_PROFIT_DATE = DateUtility
			.getDate("31/01/2011");
	final static Date NEXT_GRACE_PROFIT_REVIEW_DATE = DateUtility
			.getDate("31/03/2011");
	final static Date NEXT_GRACE_CPZ_DATE = DateUtility.getDate("30/06/2011");

	public static FinScheduleData getSchedule(boolean allowGracePeriod) {
		FinScheduleData schedule = new FinScheduleData();
		schedule.setFinanceMain(new FinanceMain());
		schedule.getDisbursementDetails().add(new FinanceDisbursement());

		FinanceMain finance = schedule.getFinanceMain();
		finance.setNumberOfTerms(12);
		if (allowGracePeriod) {
			finance.setAllowGrcPeriod(true);
			finance.setGraceBaseRate("L1");
			finance.setGraceSpecialRate("S1");
			finance.setGrcPftRate(BigDecimal.ZERO);
			finance.setGrcPftFrq("M0031");
			finance.setNextGrcPftDate(NEXT_GRACE_PROFIT_DATE);
			finance.setAllowGrcPftRvw(true);
			finance.setGrcPftRvwFrq("Q0331");
			finance.setNextGrcPftRvwDate(NEXT_GRACE_PROFIT_REVIEW_DATE);
			finance.setAllowGrcCpz(true);
			finance.setGrcCpzFrq("H0631");
			finance.setNextGrcCpzDate(NEXT_GRACE_CPZ_DATE);
		}
		finance.setRepayBaseRate("L1");
		finance.setRepaySpecialRate("S1");
		finance.setRepayProfitRate(BigDecimal.ZERO);
		finance.setRepayFrq("M0031");
		finance.setRepayPftFrq("M0031");
		finance.setAllowRepayRvw(true);
		finance.setRepayRvwFrq("Q0331");
		finance.setAllowRepayCpz(true);
		finance.setRepayCpzFrq("H0631");
		finance.setMaturityDate(DateUtility.getDate("31/12/2012"));
		if (allowGracePeriod) {
			finance.setCpzAtGraceEnd(true);
		}
		finance.setDownPayment(new BigDecimal(10000000));
		finance.setTotalProfit(BigDecimal.ZERO);
		finance.setTotalGrossPft(BigDecimal.ZERO);
		finance.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		finance.setReqTerms(12);
		finance.setEventFromDate(DateUtility.getDate("01/01/2011"));
		finance.setEventToDate(DateUtility.getDate("31/12/2012"));
		finance.setRecalType("CURPRD");
		finance.setFinStartDate(DateUtility.getDate("01/01/2011"));

		FinanceDisbursement disbursement = schedule.getDisbursementDetails()
				.get(0);
		disbursement.setDisbAmount(new BigDecimal(100000000));
		disbursement.setDisbDate(DateUtility.getDate("01/01/2011"));

		return schedule;
	}
}
