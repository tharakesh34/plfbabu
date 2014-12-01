package com.pennant.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import jxl.Cell;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.Assert;
import org.testng.SkipException;
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

		if (name.equals("SN09_RR_PRIPFT") || name.equals("SN09_RR_EQUAL")
				|| name.equals("SN09_RR_PRI") || name.equals("SN13_RR_PRIPFT")
				|| name.equals("SN13_RR_PRI") || name.equals("SN13_RR_EQUAL")
				|| name.equals("SN13_RR_EQUAL_REQ")) {

			throw new SkipException("");
		}

		// Get the expected results
		long expLastRepayAmt = Dataset.getLong(data, 44);
		long expTotalProfit = Dataset.getLong(data, 45);

		// Calculate the schedule
		schedule = CreateScheduleTest.execute(schedule,
				Arrays.copyOfRange(data, 2, 17));

		if (null != Dataset.getString(data, 17)) {
			schedule = ChangeRepayTest.execute(schedule,
					Arrays.copyOfRange(data, 17, 22));
		}

		if (null != Dataset.getString(data, 22)) {
			schedule = ChangeRepayTest.execute(schedule,
					Arrays.copyOfRange(data, 22, 27));
		}

		if (null != Dataset.getString(data, 27)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 27, 33));
		}

		if (null != Dataset.getString(data, 33)) {
			schedule = execute(schedule, Arrays.copyOfRange(data, 33, 39));
		}

		if (null != Dataset.getString(data, 39)) {
			schedule = ChangeRepayTest.execute(schedule,
					Arrays.copyOfRange(data, 39, 44));
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
		Date recalToDate = Dataset.getDate(data, 3);
		BigDecimal amount = Dataset.getBigDecimal(data, 4);
		String scheduleMethod = Dataset.getString(data, 5);

		// Generate the schedule.
		schedule.getFinanceMain().setEventFromDate(eventFromDate);
		schedule.getFinanceMain().setEventToDate(eventToDate);
		schedule.getFinanceMain().setRecalType(recalType);
		schedule.getFinanceMain().setRecalToDate(recalToDate);

		schedule = ScheduleCalculator.addDisbursement(schedule, amount,
				scheduleMethod, BigDecimal.ZERO);

		return schedule;
	}
}
