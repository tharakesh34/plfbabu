package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;

public class ChangeRepayTest {
	String name;
	FinScheduleData schedule;
	long expLastRepayAmt;
	long expTotalProfit;
	String recalType;
	long expFinalLastRepayAmt;
	long expFinalTotalProfit;

	public ChangeRepayTest(String name, FinScheduleData schedule,
			long expLastRepayAmt, long expTotalProfit, String recalType,
			long expFinalLastRepayAmt, long expFinalTotalProfit) {
		super();

		this.name = name;
		this.schedule = schedule;
		this.expLastRepayAmt = expLastRepayAmt;
		this.expTotalProfit = expTotalProfit;
		this.recalType = recalType;
		this.expFinalLastRepayAmt = expFinalLastRepayAmt;
		this.expFinalTotalProfit = expFinalTotalProfit;
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
	}

	@Test(dependsOnMethods = { "createSchedule" })
	public void changeRepay() throws Exception {
		// Change re-payment amount
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("30/04/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("30/06/2011"));
		schedule.getFinanceMain().setRecalType(recalType);
		schedule.getFinanceMain().setRecalFromDate(
				DateUtility.getDate("31/12/2012"));
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/12/2012"));

		schedule = ScheduleCalculator.changeRepay(schedule, BigDecimal.ZERO,
				CalculationConstants.NOPAY);

		// Change re-payment amount again
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("31/10/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("31/12/2011"));
		schedule.getFinanceMain().setRecalFromDate(
				DateUtility.getDate("31/12/2012"));
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/12/2012"));

		schedule = ScheduleCalculator.changeRepay(schedule, BigDecimal.ZERO,
				CalculationConstants.NOPAY);

		// Get the actual results
		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		Assert.assertEquals(actLastRepayAmt.longValue(), expFinalLastRepayAmt);
		Assert.assertEquals(actTotProfit.longValue(), expFinalTotalProfit);
	}
}
