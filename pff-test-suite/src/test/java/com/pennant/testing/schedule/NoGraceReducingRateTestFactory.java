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
		schedule.getFinanceMain().setNumberOfTerms(24);
		schedule.getFinanceMain().setReqTerms(24);
		schedule.getFinanceMain().setDownPayment(BigDecimal.ZERO);
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
		Object[][] result = new Object[7][];

		result[0] = new Object[] { "SN08_RR_EQUAL_REQ", "EQUAL", 4400000,
				6769352, 7969352 };
		result[1] = new Object[] { "SN08_RR_EQUAL", "EQUAL", 0, 4491924,
				7806107 };
		result[2] = new Object[] { "SN08_RR_PFT", "PFT", 0, 100624247, 14700000 };
		result[3] = new Object[] { "SN08_RR_PRI_REQ", "PRI", 4400000, 6626997,
				7826997 };
		result[4] = new Object[] { "SN08_RR_PRI", "PRI", 0, 5043765, 7715006 };
		result[5] = new Object[] { "SN08_RR_PRIPFT_REQ", "PRI_PFT", 4000000,
				8049940, 7912225 };
		result[6] = new Object[] { "SN08_RR_PRIPFT", "PRI_PFT", 0, 4192669,
				7629400 };

		return result;
	}
}
