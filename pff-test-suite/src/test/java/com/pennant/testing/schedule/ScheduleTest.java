package com.pennant.testing.schedule;

import java.math.BigDecimal;

import jxl.Cell;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.testing.Dataset;
import com.pennant.util.PrintFactory;

public class ScheduleTest {
	FinScheduleData schedule;
	Cell[] data;

	public ScheduleTest(FinScheduleData schedule, Cell[] data) {
		super();

		this.schedule = schedule;
		this.data = data;
	}

	@Test
	public void createSchedule() {
		String name = Dataset.getString(data, 1);
		PrintFactory.toConsole(name);

		// Get the expected results
		long expLastRepayAmt = Dataset.getLong(data, 4);
		long expTotalProfit = Dataset.getLong(data, 5);

		// Generate the schedule.
		schedule.getFinanceMain().setScheduleMethod(Dataset.getString(data, 2));
		schedule.getFinanceMain().setReqRepayAmount(
				Dataset.getBigDecimal(data, 3));
		if (schedule.getFinanceMain().getReqRepayAmount()
				.compareTo(BigDecimal.ZERO) == 0) {
			schedule.getFinanceMain().setCalculateRepay(true);
			schedule.getFinanceMain().setEqualRepay(true);
		} else {
			schedule.getFinanceMain().setCalculateRepay(false);
			schedule.getFinanceMain().setEqualRepay(false);
		}

		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

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

	@Test(dependsOnMethods = { "createSchedule" })
	public void changeRepay() {
		String name = Dataset.getString(data, 6);

		if (null == name) {
			throw new SkipException("Skipped!");
		}

		PrintFactory.toConsole(name);

		// Get the expected results
		long expLastRepayAmt = Dataset.getLong(data, 14);
		long expTotalProfit = Dataset.getLong(data, 15);

		// Change re-payment #1
		BigDecimal amount = Dataset.getBigDecimal(data, 10);
		String scheduleMethod = Dataset.getString(data, 11);

		schedule.getFinanceMain().setEventFromDate(Dataset.getDate(data, 7));
		schedule.getFinanceMain().setEventToDate(Dataset.getDate(data, 8));
		schedule.getFinanceMain().setRecalType(Dataset.getString(data, 9));
		schedule.getFinanceMain().setRecalFromDate(
				schedule.getFinanceMain().getMaturityDate());
		schedule.getFinanceMain().setRecalToDate(
				schedule.getFinanceMain().getMaturityDate());

		schedule = ScheduleCalculator.changeRepay(schedule, amount,
				scheduleMethod);

		// Change re-payment #2
		schedule.getFinanceMain().setEventFromDate(Dataset.getDate(data, 12));
		schedule.getFinanceMain().setEventToDate(Dataset.getDate(data, 13));

		schedule = ScheduleCalculator.changeRepay(schedule, amount,
				scheduleMethod);

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
}
