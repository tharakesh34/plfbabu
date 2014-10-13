package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.BeanFactory;

public class GraceNoPayReducingRateTestFactory {
	@Factory(dataProvider = "dataset")
	public Object[] createObjects(String name, String scheduleMethod,
			long reqRepayAmt, long expLastRepayAmt, long expTotalProfit) {
		FinScheduleData schedule = BeanFactory.getSchedule(true);
		schedule.getFinanceMain().setNumberOfTerms(12);
		schedule.getFinanceMain().setReqTerms(12);
		schedule.getFinanceMain().setDownPayment(new BigDecimal(10000000));
		schedule.getFinanceMain().setGrcPeriodEndDate(
				DateUtility.getDate("31/12/2011"));
		schedule.getFinanceMain().setNextRepayDate(
				DateUtility.getDate("31/01/2012"));
		schedule.getFinanceMain().setNextRepayPftDate(
				DateUtility.getDate("31/01/2012"));
		schedule.getFinanceMain().setNextRepayRvwDate(
				DateUtility.getDate("31/03/2012"));
		schedule.getFinanceMain().setNextRepayCpzDate(
				DateUtility.getDate("30/06/2012"));
		schedule.getFinanceMain().setGrcSchdMthd(CalculationConstants.NOPAY);
		schedule.getFinanceMain().setGrcRateBasis("R");
		schedule.getFinanceMain().setRepayRateBasis("R");
		schedule.getFinanceMain().setAllowGrcRepay(true);

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

		result[0] = new Object[] { "SN03_RR_EQUAL_REQ", "EQUAL", 8000000,
				12773266, 10773266 };
		result[1] = new Object[] { "SN03_RR_EQUAL", "EQUAL", 0, 8384477,
				10613779 };
		result[2] = new Object[] { "SN03_RR_PFT", "PFT", 0, 97321506, 13845979 };
		result[3] = new Object[] { "SN03_RR_PRI_REQ", "PRI", 8000000, 12699241,
				10699241 };
		result[4] = new Object[] { "SN03_RR_PRI", "PRI", 0, 9368869, 10579637 };
		result[5] = new Object[] { "SN03_RR_PRIPFT_REQ", "PRI_PFT", 7500000,
				14306502, 10798246 };
		result[6] = new Object[] { "SN03_RR_PRIPFT", "PRI_PFT", 0, 8110129,
				10570758 };

		return result;
	}
}
