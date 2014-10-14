package com.pennant.testing.schedule;

import java.io.IOException;
import java.math.BigDecimal;

import jxl.Sheet;
import jxl.read.biff.BiffException;

import org.testng.annotations.Factory;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.testing.Dataset;
import com.pennant.util.BeanFactory;

public class GraceNoRepayReducingRateTestFactory {
	final String SHEET_NAME = "GraceNoRepayReducingRate";

	@Factory
	public Object[] createObjects() throws BiffException, IOException {
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
		schedule.getFinanceMain().setGrcRateBasis("R");
		schedule.getFinanceMain().setRepayRateBasis("R");
		schedule.getFinanceMain().setAllowGrcRepay(false);

		// Prepare the tests
		Sheet dataset = Dataset.getSchedule(SHEET_NAME);
		int testCount = dataset.getColumns() - 3;
		Object[] result = new Object[testCount];

		for (int i = 0; i < testCount; i++) {
			result[i] = new ScheduleTest(schedule, dataset.getColumn(i + 3));
		}

		return result;
	}
}
