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
import com.pennant.backend.util.PennantConstants;
import com.pennant.test.Dataset;
import com.pennant.util.PrintFactory;

public class AddDefermentTest {
	FinScheduleData schedule;
	Cell[] data;
	String defMethod = PennantConstants.DEF_METHOD_RECALRATE;

	public AddDefermentTest(FinScheduleData schedule, Cell[] data) {
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
		
		if(name.equals("SN19_RR_PRIPFT")){
			throw new SkipException("skipped..");
		}
		
		if(!(name.startsWith("SN19") || name.startsWith("SN20"))){
			throw new SkipException("skipped..");
		}

		// Get the expected results
		long expLastRepayAmt = Dataset.getLong(data, 31);
		long expTotalProfit = Dataset.getLong(data, 32);
		long expTotDefPri = Dataset.getLong(data, 33);
		long expTotDefPft = Dataset.getLong(data, 34);

		// Calculate the schedule
		schedule = CreateScheduleTest.execute(schedule,
				Arrays.copyOfRange(data, 2, 17));

		if (null != Dataset.getDate(data, 17)) {
			schedule = execute(schedule,
					Arrays.copyOfRange(data, 17, 23));
		}
		
		if (null != Dataset.getDate(data, 23)) {
			schedule = execute(schedule,
					Arrays.copyOfRange(data, 23, 30));
		}
		
		if (null != Dataset.getString(data, 30)) {
			schedule = AddTermTest.execute(schedule,
					Arrays.copyOfRange(data, 29, 31));
		}

		BigDecimal actClosingBal = null;
		BigDecimal actTotDefPri = null;
		BigDecimal actTotDefPft = null;

		// Get the actual results
		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();
		if (!defMethod.equals(PennantConstants.DEF_METHOD_RECALRATE)) {
			actClosingBal = schedule.getFinanceScheduleDetails()
					.get(schedule.getFinanceScheduleDetails().size() - 1)
					.getClosingBalance();
			actTotDefPri = schedule.getFinanceScheduleDetails()
					.get(schedule.getFinanceScheduleDetails().size() - 1)
					.getDefPrincipalSchd();
			actTotDefPft = schedule.getFinanceScheduleDetails()
					.get(schedule.getFinanceScheduleDetails().size() - 1)
					.getDefProfitSchd();
		}

		PrintFactory.toConsole(expLastRepayAmt, actLastRepayAmt);
		PrintFactory.toConsole(expTotalProfit, actTotProfit);

		PrintFactory.scheduleToExcel(name, schedule);

		Assert.assertEquals(actLastRepayAmt.longValue(), expLastRepayAmt);
		Assert.assertEquals(actTotProfit.longValue(), expTotalProfit);

		if (!defMethod.equals(PennantConstants.DEF_METHOD_RECALRATE)) {
			Assert.assertEquals(actTotDefPri.longValue(), expTotDefPri);
			Assert.assertEquals(actTotDefPft.longValue(), expTotDefPft);
			Assert.assertEquals(actClosingBal.longValue(), 0);
		}
	}

	public static FinScheduleData execute(FinScheduleData model, Cell[] data)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		FinScheduleData schedule = (FinScheduleData) BeanUtils.cloneBean(model);

		// Get the parameters
		Date eventFromDate = Dataset.getDate(data, 0);
		Date eventToDate = Dataset.getDate(data, 1);
		String recalType = Dataset.getString(data, 2);
		Date recalFromDate = Dataset.getDate(data, 3);
		Date recalToDate = Dataset.getDate(data, 4);

		// Generate the schedule.
		schedule.getFinanceMain().setEventFromDate(eventFromDate);
		schedule.getFinanceMain().setEventToDate(eventToDate);
		schedule.getFinanceMain().setRecalType(recalType);
		schedule.getFinanceMain().setRecalFromDate(recalFromDate);
		schedule.getFinanceMain().setRecalToDate(recalToDate);

		schedule = ScheduleCalculator.addDeferment(schedule);
		return schedule;
	}
}
