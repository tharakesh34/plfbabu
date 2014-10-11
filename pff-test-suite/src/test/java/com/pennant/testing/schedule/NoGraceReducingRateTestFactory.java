package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.BeanFactory;

public class NoGraceReducingRateTestFactory {
	@Factory(dataProvider = "dataset")
	public Object[] createObjects(String name, String scheduleMethod,
			long reqRepayAmt, long expLastRepayAmt, long expTotalProfit) {
		FinScheduleData schedule = BeanFactory.getSchedule(false);
		schedule.getFinanceMain().setGrcPeriodEndDate(
				DateUtility.getDate("01/01/2011"));
		schedule.getFinanceMain().setNextRepayDate(
				DateUtility.getDate("31/01/2011"));
		schedule.getFinanceMain().setNextRepayPftDate(
				DateUtility.getDate("31/01/2011"));
		schedule.getFinanceMain().setNextRepayRvwDate(
				DateUtility.getDate("31/03/2011"));
		schedule.getFinanceMain().setNextRepayCpzDate(
				DateUtility.getDate("30/06/2011"));
		schedule.getFinanceMain().setRepayRateBasis("R");

		schedule.getFinanceMain().setScheduleMethod(scheduleMethod);
		schedule.getFinanceMain().setReqRepayAmount(
				BigDecimal.valueOf(reqRepayAmt));
		if (reqRepayAmt == 0) {
			schedule.getFinanceMain().setCalculateRepay(true);
			schedule.getFinanceMain().setEqualRepay(true);
		} else {
			schedule.getFinanceMain().setCalculateRepay(false);
			schedule.getFinanceMain().setEqualRepay(false);
		}

		return new Object[] { new ScheduleTest(name, schedule, expLastRepayAmt,
				expTotalProfit) };
	}

	@DataProvider
	public Object[][] dataset() {
		Object[][] result = new Object[1][];

		result[0] = new Object[] { "SN08_RR_EQUAL_REQ", "EQUAL", 4400000,
				6769352, 7969352 };

		return result;
	}
}
