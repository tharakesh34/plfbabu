package com.pennant.test.schedule;

import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.read.biff.BiffException;

import org.testng.annotations.Factory;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.test.Dataset;
import com.pennant.util.BeanFactory;

public class AddTermTestFactory {
	final String SHEET_NAME = "AddTerm";
	final int DATA_OFFSET = 2;

	@Factory
	public Object[] createObjects() throws BiffException, IOException {
		// Prepare the tests
		Sheet dataset = Dataset.getSchedule(SHEET_NAME);
		int testCount = dataset.getColumns() - DATA_OFFSET;
		Object[] result = new Object[testCount];

		for (int i = 0; i < testCount; i++) {
			Cell[] cells = dataset.getColumn(i + DATA_OFFSET);

			boolean allowGracePeriod = Dataset.getBoolean(cells, 2);

			FinScheduleData schedule = BeanFactory
					.getSchedule(allowGracePeriod);

			result[i] = new AddTermTest(schedule, cells);
		}

		return result;
	}
}
