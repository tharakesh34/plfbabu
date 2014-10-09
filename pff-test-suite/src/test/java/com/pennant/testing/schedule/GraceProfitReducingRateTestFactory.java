package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.BeanFactory;

public class GraceProfitReducingRateTestFactory {
	@Factory(dataProvider = "dataset")
	public Object[] createObjects(String name, String scheduleMethod,
			long reqRepayAmt, long expLastRepayAmt, long expTotalProfit,
			String recalType, long expFinalLastRepayAmt,
			long expFinalTotalProfit) {
		FinScheduleData schedule = BeanFactory.getSchedule();
		schedule.getFinanceMain().setGrcSchdMthd(CalculationConstants.PFT);
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

		// SN07

		return new Object[] { new ChangeRepayTest(name, schedule,
				expLastRepayAmt, expTotalProfit, recalType,
				expFinalLastRepayAmt, expFinalTotalProfit) };
	}

	@DataProvider
	public Object[][] dataset() {
		Object[][] result = new Object[1][];

		result[0] = new Object[] { "SN01_RR_EQUAL_REQ", "EQUAL", 7500000,
				11250745, 10347622, "ADJMDT", 14853053, 10663922 };

		return result;
	}
}
