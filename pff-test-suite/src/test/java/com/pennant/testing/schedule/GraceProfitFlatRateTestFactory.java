package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.BeanFactory;

public class GraceProfitFlatRateTestFactory {
	@Factory(dataProvider = "dataset")
	public Object[] createObjects(String name, String scheduleMethod,
			long reqRepayAmt, long expLastRepayAmt, long expTotalProfit) {
		FinScheduleData schedule = BeanFactory.getSchedule();
		schedule.getFinanceMain().setGrcSchdMthd(CalculationConstants.PFT);
		schedule.getFinanceMain().setGrcRateBasis("F");
		schedule.getFinanceMain().setRepayRateBasis("F");
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

		result[0] = new Object[] { "SN02_FR_EQUAL_REQ", "EQUAL", 8000000,
				8633123, 13230000 };
		result[1] = new Object[] { "SN02_FR_EQUAL", "EQUAL", 0, 8052763,
				13230000 };
		result[2] = new Object[] { "SN02_FR_PFT", "PFT", 0, 90561822, 13230000 };
		result[3] = new Object[] { "SN02_FR_PRI_REQ", "PRI", 7500000, 14255337,
				13352214 };
		result[4] = new Object[] { "SN02_FR_PRI", "PRI", 0, 11231767, 13352214 };
		result[5] = new Object[] { "SN02_FR_PRIPFT_REQ", "PRI_PFT", 7000000,
				13561822, 13230000 };
		result[6] = new Object[] { "SN02_FR_PRIPFT", "PRI_PFT", 0, 8061822,
				13230000 };

		return result;
	}
}
