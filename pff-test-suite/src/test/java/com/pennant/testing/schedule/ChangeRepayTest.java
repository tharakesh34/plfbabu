package com.pennant.testing.schedule;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.util.ExcelFile;

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
		schedule.getFinanceMain().setFinRemarks("ScheduleCreate" + name + schedule.hashCode());
		
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
		schedule.getFinanceMain().setFinRemarks("ScheduleCreate" + name + schedule.hashCode());
	}

	@Test(dependsOnMethods = { "createSchedule" })
	public void changeRepay() throws Exception {
		schedule.getFinanceMain().setFinRemarks("ScheduleRepay" + name + schedule.hashCode());
		System.out.println(name);

		if ("SN01_RR_EQUAL".equals(name)) {
			throw new SkipException("Skipped");
		}
		if ("SN01_RR_PFT".equals(name)) {
			throw new SkipException("Skipped");
		}
		if ("SN01_RR_PRI_REQ".equals(name)) {
			throw new SkipException("Skipped");
		}
		if ("SN01_RR_PRI".equals(name)) {
			throw new SkipException("Skipped");
		}
		if ("SN01_RR_PRIPFT_REQ".equals(name)) {
			throw new SkipException("Skipped");
		}
		if ("SN01_RR_PRIPFT".equals(name)) {
			throw new SkipException("Skipped");
		}
		
		System.out.println(name);
		ExcelFile.printSchedule(schedule);

		try {
			schedule.getFinanceMain().setEventFromDate(
					DateUtility.getDate("30/04/2011"));
			schedule.getFinanceMain().setEventToDate(
					DateUtility.getDate("30/06/2011"));
			schedule.getFinanceMain().setRecalType(recalType);
			schedule.getFinanceMain().setRecalToDate(
					DateUtility.getDate("31/12/2012"));
			
			System.out.println(recalType);
			System.out.println(schedule);

			schedule = ScheduleCalculator.changeRepay(schedule,
					BigDecimal.ZERO, CalculationConstants.NOPAY);
			
			System.out.println(name);
			ExcelFile.printSchedule(schedule);

			schedule.getFinanceMain().setEventFromDate(
					DateUtility.getDate("31/10/2011"));
			schedule.getFinanceMain().setEventToDate(
					DateUtility.getDate("31/12/2011"));
			schedule.getFinanceMain().setRecalToDate(
					DateUtility.getDate("31/12/2012"));
			schedule = ScheduleCalculator.changeRepay(schedule,
					BigDecimal.ZERO, CalculationConstants.NOPAY);
			
			System.out.println(name);
			ExcelFile.printSchedule(schedule);
			
			schedule.getFinanceMain().setFinRemarks("ScheduleRepay" + name + schedule.hashCode());

			// Get the actual results
			BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
					.get(schedule.getFinanceScheduleDetails().size() - 1)
					.getRepayAmount();
			BigDecimal actTotProfit = schedule.getFinanceMain()
					.getTotalGrossPft();

			Assert.assertEquals(actLastRepayAmt.longValue(),
					expFinalLastRepayAmt);
			Assert.assertEquals(actTotProfit.longValue(), expFinalTotalProfit);
		} catch (Exception e) {
			System.out.println(name);
			for (FinanceScheduleDetail detail : schedule
					.getFinanceScheduleDetails()) {
				System.out.print("SchDate: ");
				System.out.println(detail.getSchDate());
			}
			e.printStackTrace();
			throw e;
		}
	}
}
