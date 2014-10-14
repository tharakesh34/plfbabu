package com.pennant.test.schedule;

import java.io.IOException;

import jxl.Sheet;
import jxl.read.biff.BiffException;

import org.testng.annotations.Factory;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.test.Dataset;
import com.pennant.util.BeanFactory;

public class ScheduleTestFactory {
	final String SHEET_NAME = "Schedule";

	@Factory
	public Object[] createObjects() throws BiffException, IOException {
		// Prepare the tests
		Sheet dataset = Dataset.getSchedule(SHEET_NAME);
		int testCount = dataset.getColumns() - 2;
		Object[] result = new Object[testCount];

		for (int i = 0; i < testCount; i++) {
			boolean allowGracePeriod = Dataset.getBoolean(
					dataset.getColumn(i + 2), 2);

			FinScheduleData schedule = BeanFactory
					.getSchedule(allowGracePeriod);

			result[i] = new ScheduleTest(schedule, dataset.getColumn(i + 2));
		}

		return result;
	}
}
