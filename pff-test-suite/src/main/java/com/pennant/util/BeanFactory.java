package com.pennant.util;

import java.math.BigDecimal;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;

public class BeanFactory {
	public static FinScheduleData getSchedule() {
		FinScheduleData schedule = new FinScheduleData();
		schedule.setFinanceMain(new FinanceMain());
		schedule.getDisbursementDetails().add(new FinanceDisbursement());

		FinanceMain finance = schedule.getFinanceMain();
		finance.setNumberOfTerms(12);
		finance.setAllowGrcPeriod(true);
		finance.setGraceBaseRate("L1");
		finance.setGraceSpecialRate("S1");
		finance.setGrcPftRate(BigDecimal.ZERO);
		finance.setGrcPftFrq("M0031");
		finance.setNextGrcPftDate(DateUtility.getDate("31/01/2011"));
		finance.setAllowGrcPftRvw(true);
		finance.setGrcPftRvwFrq("Q0331");
		finance.setNextGrcPftRvwDate(DateUtility.getDate("31/03/2011"));
		finance.setAllowGrcCpz(true);
		finance.setGrcCpzFrq("H0631");
		finance.setNextGrcCpzDate(DateUtility.getDate("30/06/2011"));
		finance.setRepayBaseRate("L1");
		finance.setRepaySpecialRate("S1");
		finance.setRepayProfitRate(BigDecimal.ZERO);
		finance.setRepayFrq("M0031");
		finance.setNextRepayDate(DateUtility.getDate("31/01/2012"));
		finance.setRepayPftFrq("M0031");
		finance.setNextRepayPftDate(DateUtility.getDate("31/01/2012"));
		finance.setAllowRepayRvw(true);
		finance.setRepayRvwFrq("Q0331");
		finance.setNextRepayRvwDate(DateUtility.getDate("31/03/2012"));
		finance.setAllowRepayCpz(true);
		finance.setRepayCpzFrq("H0631");
		finance.setNextRepayCpzDate(DateUtility.getDate("30/06/2012"));
		finance.setMaturityDate(DateUtility.getDate("31/12/2012"));
		finance.setCpzAtGraceEnd(true);
		finance.setDownPayment(new BigDecimal(10000000));
		finance.setTotalProfit(BigDecimal.ZERO);
		finance.setTotalGrossPft(BigDecimal.ZERO);
		finance.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		finance.setReqTerms(12);
		finance.setIncreaseTerms(false);
		finance.setEventFromDate(DateUtility.getDate("01/01/2011"));
		finance.setEventToDate(DateUtility.getDate("31/12/2012"));
		finance.setRecalType("CURPRD");
		finance.setGrcPeriodEndDate(DateUtility.getDate("31/12/2011"));
		finance.setFinStartDate(DateUtility.getDate("01/01/2011"));
		finance.setExcludeDeferedDates(false);

		FinanceDisbursement disbursement = schedule.getDisbursementDetails()
				.get(0);
		disbursement.setDisbAmount(new BigDecimal(100000000));
		disbursement.setDisbDate(DateUtility.getDate("01/01/2011"));

		return schedule;
	}
}
