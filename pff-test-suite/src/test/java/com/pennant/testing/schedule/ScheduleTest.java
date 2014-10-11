package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;

public class ScheduleTest {
	String name;
	FinScheduleData schedule;
	long expLastRepayAmt;
	long expTotalProfit;

	public ScheduleTest(String name, FinScheduleData schedule,
			long expLastRepayAmt, long expTotalProfit) {
		super();

		this.name = name;
		this.schedule = schedule;
		this.expLastRepayAmt = expLastRepayAmt;
		this.expTotalProfit = expTotalProfit;
	}

	@Test
	public void createSchedule() {
		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		// Get the actual results
		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		Assert.assertEquals(actLastRepayAmt.longValue(), expLastRepayAmt);
		Assert.assertEquals(actTotProfit.longValue(), expTotalProfit);
		
		System.out.println(name);
		System.out.println(actLastRepayAmt);
		System.out.println(actTotProfit);
	}
}
