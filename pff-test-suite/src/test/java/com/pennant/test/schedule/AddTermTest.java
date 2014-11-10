package com.pennant.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;

import jxl.Cell;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.test.Dataset;
import com.pennant.util.PrintFactory;

public class AddTermTest {
	FinScheduleData schedule;
	Cell[] data;

	public AddTermTest(FinScheduleData schedule, Cell[] data) {
		super();

		this.schedule = schedule;
		this.data = data;
	}

	@Test
	public void testSchedule() throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		String name = Dataset.getString(data, 1);
		PrintFactory.toConsole(name);

		// Get the expected results
		long expLastRepayAmt = Dataset.getLong(data, 25);
		long expTotalProfit = Dataset.getLong(data, 26);

		// Calculate the schedule
		schedule = CreateScheduleTest.execute(schedule,
				Arrays.copyOfRange(data, 2, 17));

		if (null != Dataset.getString(data, 17)) {
			schedule = AddDisbursementTest.execute(schedule,
					Arrays.copyOfRange(data, 17, 24));
		}

		if (null != Dataset.getString(data, 24)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 23, 25));
		}

		// Get the actual results
		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();

		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		PrintFactory.toConsole(expLastRepayAmt, actLastRepayAmt);
		PrintFactory.toConsole(expTotalProfit, actTotProfit);
		
		PrintFactory.scheduleToExcel(name, schedule);

		if (null == Dataset.getString(data, 24)) {
			Assert.assertEquals(actLastRepayAmt.longValue(), expLastRepayAmt);
		} else {
			BigDecimal actLast2RepayAmt = schedule.getFinanceScheduleDetails()
					.get(schedule.getFinanceScheduleDetails().size() - 2)
					.getRepayAmount();

			PrintFactory.toConsole(expLastRepayAmt, actLast2RepayAmt);
			Assert.assertTrue((actLast2RepayAmt.longValue() == expLastRepayAmt || actLastRepayAmt
					.longValue() == expLastRepayAmt));
		}

		Assert.assertEquals(actTotProfit.longValue(), expTotalProfit);
	}

	public static FinScheduleData execute(FinScheduleData model, Cell[] data)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		FinScheduleData schedule = (FinScheduleData) BeanUtils.cloneBean(model);

		// Get the parameters
		int addterms = Dataset.getInt(data, 0);
		String scheduleMethod = Dataset.getString(data, 1);

		// Generate the schedule.
		schedule = ScheduleCalculator.addTerm(schedule, addterms,
				scheduleMethod);

		return schedule;
	}
}
