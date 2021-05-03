package com.pennanttech.test.schedule;

import java.io.IOException;

import org.testng.annotations.Factory;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.util.BeanFactory;
import com.pennanttech.util.Dataset;

import jxl.Cell;
import jxl.Sheet;
import jxl.read.biff.BiffException;

public class CrtReducingRateHighTestFactory {
	final String SHEET_NAME = "CRT_ReducingRateHigh";
	final int DATA_OFFSET = 1;

	@Factory
	public Object[] createObjects() throws BiffException, IOException {
		// Prepare the tests
		Sheet dataset = Dataset.getSchedule(SHEET_NAME);

		long t1 = DateUtility.getSysDate().getTime();

		//int testCount = dataset.getColumns() - DATA_OFFSET;
		int testCount = 1;
		Object[] result = new Object[testCount];

		for (int i = 0; i < testCount; i++) {
			Cell[] cells = dataset.getColumn(i + DATA_OFFSET);

			FinScheduleData schedule = BeanFactory.getSchedule(cells, FinanceConstants.PRODUCT_CONVENTIONAL,
					"HIGHSCHD");

			result[i] = new CrtReducingRateHighTest(schedule, cells, t1);
		}

		return result;
	}
}
