package com.pennant.test.schedule;

import java.io.IOException;
import java.math.BigDecimal;

import jxl.Sheet;
import jxl.read.biff.BiffException;

import org.testng.annotations.Factory;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.test.Dataset;
import com.pennant.util.BeanFactory;

public class NoGraceReducingRateTestFactory {
	final String SHEET_NAME = "NoGraceReducingRate";

	@Factory
	public Object[] createObjects() throws BiffException, IOException {
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
