package com.pennant.util;

import java.math.BigDecimal;
import java.sql.Date;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;

public class BeanFactory {
	final static String EVENT_FROM_DATE = "01/01/2011";
	final static String EVENT_TO_DATE = "31/12/2012";
	final static String NEXT_GRACE_PROFIT_DATE = "31/01/2011";
	final static String NEXT_GRACE_PROFIT_REVIEW_DATE = "31/03/2011";
	final static String NEXT_GRACE_CPZ_DATE = "30/06/2011";

	public static FinScheduleData getSchedule(boolean allowGracePeriod) {
		FinScheduleData schedule = new FinScheduleData();
		schedule.setFinanceMain(new FinanceMain());
		schedule.getDisbursementDetails().add(new FinanceDisbursement());

		FinanceMain finance = schedule.getFinanceMain();
		if (allowGracePeriod) {
			finance.setAllowGrcPeriod(true);
			finance.setGraceBaseRate("L1");
			finance.setGraceSpecialRate("S1");
			finance.setGrcPftRate(BigDecimal.ZERO);
			finance.setGrcPftFrq("M0031");
			finance.setNextGrcPftDate(toDate(NEXT_GRACE_PROFIT_DATE));
			finance.setAllowGrcPftRvw(true);
			finance.setGrcPftRvwFrq("Q0331");
			finance.setNextGrcPftRvwDate(toDate(NEXT_GRACE_PROFIT_REVIEW_DATE));
			finance.setAllowGrcCpz(true);
			finance.setGrcCpzFrq("H0631");
			finance.setNextGrcCpzDate(toDate(NEXT_GRACE_CPZ_DATE));
		}
		finance.setRepayBaseRate("L1");
		finance.setRepaySpecialRate("S1");
		finance.setRepayFrq("M0031");
		finance.setRepayPftFrq("M0031");
		finance.setAllowRepayRvw(true);
		finance.setRepayRvwFrq("Q0331");
		finance.setAllowRepayCpz(true);
		finance.setRepayCpzFrq("H0631");
		finance.setMaturityDate(toDate(EVENT_TO_DATE));
		if (allowGracePeriod) {
			finance.setCpzAtGraceEnd(true);
		}
		finance.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		finance.setEventFromDate(toDate(EVENT_FROM_DATE));
		finance.setEventToDate(toDate(EVENT_TO_DATE));
		finance.setRecalType("CURPRD");
		finance.setFinStartDate(toDate(EVENT_FROM_DATE));

		FinanceDisbursement disbursement = schedule.getDisbursementDetails()
				.get(0);
		disbursement.setDisbAmount(new BigDecimal(100000000));
		disbursement.setDisbDate(toDate(EVENT_FROM_DATE));

		return schedule;
	}

	private static Date toDate(String date) {
		return DateUtility.getDate(date);
	}
}
