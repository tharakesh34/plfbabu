package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.BeanFactory;

public class GraceNoRepayFlatRateTestFactory {
	@Factory(dataProvider = "dataset")
	public Object[] createObjects(String name, String scheduleMethod,
			long reqRepayAmt, long expLastRepayAmt, long expTotalProfit) {
		FinScheduleData schedule = BeanFactory.getSchedule();
		schedule.getFinanceMain().setGrcRateBasis("F");
		schedule.getFinanceMain().setRepayRateBasis("F");
		schedule.getFinanceMain().setAllowGrcRepay(false);

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

		result[0] = new Object[] { "SN06_FR_EQUAL_REQ", "EQUAL", 8000000,
				15845979, 13845979 };
		result[1] = new Object[] { "SN06_FR_EQUAL", "EQUAL", 0, 8653827,
				13845979 };
		result[2] = new Object[] { "SN06_FR_PFT", "PFT", 0, 97321506, 13845979 };
		result[3] = new Object[] { "SN06_FR_PRI_REQ", "PRI", 8000000, 15977315,
				13977315 };
		result[4] = new Object[] { "SN06_FR_PRI", "PRI", 0, 12070126, 13977315 };
		result[5] = new Object[] { "SN06_FR_PRIPFT_REQ", "PRI_PFT", 8000000,
				9321506, 13845979 };
		result[6] = new Object[] { "SN06_FR_PRIPFT", "PRI_PFT", 0, 8663574,
				13845979 };

		return result;
	}
}
