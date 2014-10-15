package com.pennant.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import jxl.Cell;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.test.Dataset;
import com.pennant.util.PrintFactory;

public class AddDisbursementTest {
	FinScheduleData schedule;
	Cell[] data;

	public AddDisbursementTest(FinScheduleData schedule, Cell[] data) {
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
		long expLastRepayAmt = Dataset.getLong(data, 40);
		long expTotalProfit = Dataset.getLong(data, 41);

		// Calculate the schedule
		schedule = CreateScheduleTest.execute(schedule,
				Arrays.copyOfRange(data, 2, 15));

		if (null != Dataset.getString(data, 15)) {
			schedule = ChangeRepayTest.execute(schedule,
					Arrays.copyOfRange(data, 15, 20));
		}

		if (null != Dataset.getString(data, 20)) {
			schedule = ChangeRepayTest.execute(schedule,
					Arrays.copyOfRange(data, 20, 25));
		}

		if (null != Dataset.getString(data, 25)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 25, 30));
		}

		if (null != Dataset.getString(data, 30)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 30, 35));
		}

		if (null != Dataset.getString(data, 35)) {
			schedule = ChangeRepayTest.execute(schedule,
					Arrays.copyOfRange(data, 35, 40));
		}

		// Get the actual results
		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		PrintFactory.toConsole(expLastRepayAmt, actLastRepayAmt);
		PrintFactory.toConsole(expTotalProfit, actTotProfit);

		Assert.assertEquals(actLastRepayAmt.longValue(), expLastRepayAmt);
		Assert.assertEquals(actTotProfit.longValue(), expTotalProfit);
	}

	public static FinScheduleData execute(FinScheduleData model, Cell[] data)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		FinScheduleData schedule = (FinScheduleData) BeanUtils.cloneBean(model);

		// Get the parameters
		Date eventFromDate = Dataset.getDate(data, 0);
		Date eventToDate = Dataset.getDate(data, 1);
		String recalType = Dataset.getString(data, 2);
		BigDecimal amount = Dataset.getBigDecimal(data, 3);
		String scheduleMethod = Dataset.getString(data, 4);

		// Generate the schedule.
		schedule.getFinanceMain().setEventFromDate(eventFromDate);
		schedule.getFinanceMain().setEventToDate(eventToDate);
		schedule.getFinanceMain().setRecalType(recalType);
		schedule.getFinanceMain().setRecalToDate(null);

		schedule = ScheduleCalculator.addDisbursement(schedule, amount,
				scheduleMethod, BigDecimal.ZERO);

		return schedule;
	}
}
