package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.BeanFactory;

public class GraceProfitReducingRateTestFactory {
	@Factory(dataProvider = "dataset")
	public Object[] createObjects(String name, String scheduleMethod,
			long reqRepayAmt, long expLastRepayAmt, long expTotalProfit) {
		FinScheduleData schedule = BeanFactory.getSchedule();

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

		result[0] = new Object[] { "SN01_RR_EQUAL_REQ", "EQUAL", 7500000,
				11250745, 10347622 };
		result[1] = new Object[] { "SN01_RR_EQUAL", "EQUAL", 0, 7802114,
				10222300 };
		result[2] = new Object[] { "SN01_RR_PFT", "PFT", 0, 90561822, 13230000 };
		result[3] = new Object[] { "SN01_RR_PRI_REQ", "PRI", 7500000, 11182143,
				10279020 };
		result[4] = new Object[] { "SN01_RR_PRI", "PRI", 0, 8718137, 10190530 };
		result[5] = new Object[] { "SN01_RR_PRIPFT_REQ", "PRI_PFT", 7000000,
				13081152, 10385450 };
		result[6] = new Object[] { "SN01_RR_PRIPFT", "PRI_PFT", 0, 7546818,
				10182267 };

		return result;
	}
}
