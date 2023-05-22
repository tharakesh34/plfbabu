package com.pennanttech.test.schedule;

import java.io.IOException;

import org.testng.annotations.Factory;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.util.BeanFactory;
import com.pennanttech.util.Dataset;

import jxl.Cell;
import jxl.Sheet;
import jxl.read.biff.BiffException;

public class SrvReducingRateTestFactory {
	final String SHEET_NAME = "SRV_ReduceRate";
	final int DATA_OFFSET = 1;

	@Factory
	public Object[] createObjects() throws BiffException, IOException {
		// Prepare the tests
		Sheet dataset = Dataset.getSchedule(SHEET_NAME);

		long t1 = DateUtil.getSysDate().getTime();

		// int testCount = dataset.getColumns() - DATA_OFFSET;
		int testCount = (35);
		Object[] result = new Object[testCount];

		for (int i = 0; i < testCount; i++) {
			Cell[] cells = dataset.getColumn(i + DATA_OFFSET);

			FinScheduleData schedule = BeanFactory.getSchedule(cells, FinanceConstants.PRODUCT_CONVENTIONAL, "SRVSCHD");

			result[i] = new SrvReducingRateTest(schedule, cells, t1);
		}

		return result;
	}
}
