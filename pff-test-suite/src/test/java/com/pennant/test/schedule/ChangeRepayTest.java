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

public class ChangeRepayTest {
	FinScheduleData schedule;
	Cell[] data;

	public ChangeRepayTest(FinScheduleData schedule, Cell[] data) {
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
		long expLastRepayAmt = Dataset.getLong(data, 27);
		long expTotalProfit = Dataset.getLong(data, 28);

		// Calculate the schedule
		schedule = CreateScheduleTest.execute(schedule,
				Arrays.copyOfRange(data, 2, 17));

		if (null != Dataset.getString(data, 17)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 17, 22));
		}

		if (null != Dataset.getString(data, 22)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 22, 27));
		}

		// Get the actual results
		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		PrintFactory.toConsole(expLastRepayAmt, actLastRepayAmt);
		PrintFactory.toConsole(expTotalProfit, actTotProfit);
		
		PrintFactory.scheduleToExcel(name, schedule);

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
		schedule.getFinanceMain().setRecalFromDate(
				schedule.getFinanceMain().getMaturityDate());
		schedule.getFinanceMain().setRecalToDate(
				schedule.getFinanceMain().getMaturityDate());

		schedule = ScheduleCalculator.changeRepay(schedule, amount,
				scheduleMethod);

		return schedule;
	}
}
