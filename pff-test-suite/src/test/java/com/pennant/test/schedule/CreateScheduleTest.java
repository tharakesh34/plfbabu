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
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.test.Dataset;
import com.pennant.util.PrintFactory;

public class CreateScheduleTest {
	FinScheduleData schedule;
	Cell[] data;

	public CreateScheduleTest(FinScheduleData schedule, Cell[] data) {
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
		long expLastRepayAmt = Dataset.getLong(data, 17);
		long expTotalProfit = Dataset.getLong(data, 18);

		// Calculate the schedule
		schedule = execute(schedule, Arrays.copyOfRange(data, 2, 17));

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
		boolean allowGracePeriod = Dataset.getBoolean(data, 0);
		int terms = Dataset.getInt(data, 1);
		BigDecimal downPayment = Dataset.getBigDecimal(data, 2);
		Date eventFromDate = Dataset.getDate(data, 3);
		Date eventToDate = Dataset.getDate(data, 4);
		Date grcPeriodEndDate = Dataset.getDate(data, 5);
		Date nextRepayDate = Dataset.getDate(data, 6);
		Date nextRepayPftDate = Dataset.getDate(data, 7);
		Date nextRepayRvwDate = Dataset.getDate(data, 8);
		Date nextRepayCpzDate = Dataset.getDate(data, 9);
		String grcSchdMthd = Dataset.getString(data, 10);
		String rateBasis = Dataset.getString(data, 11);
		boolean allowGrcRepay = Dataset.getBoolean(data, 12);
		String scheduleMethod = Dataset.getString(data, 13);
		BigDecimal reqRepayAmount = Dataset.getBigDecimal(data, 14);

		// Generate the schedule.
		schedule.getFinanceMain().setNumberOfTerms(terms);
		schedule.getFinanceMain().setReqTerms(terms);
		schedule.getFinanceMain().setDownPayment(downPayment);
		schedule.getFinanceMain().setEventFromDate(eventFromDate);
		schedule.getFinanceMain().setFinStartDate(eventFromDate);
		schedule.getDisbursementDetails().get(0).setDisbDate(eventFromDate);
		schedule.getFinanceMain().setMaturityDate(eventToDate);
		schedule.getFinanceMain().setEventToDate(eventToDate);
		schedule.getFinanceMain().setGrcPeriodEndDate(grcPeriodEndDate);
		schedule.getFinanceMain().setNextRepayDate(nextRepayDate);
		schedule.getFinanceMain().setNextRepayPftDate(nextRepayPftDate);
		schedule.getFinanceMain().setNextRepayRvwDate(nextRepayRvwDate);
		schedule.getFinanceMain().setNextRepayCpzDate(nextRepayCpzDate);
		if (allowGracePeriod) {
			schedule.getFinanceMain().setGrcSchdMthd(grcSchdMthd);
			schedule.getFinanceMain().setGrcRateBasis(rateBasis);
		}
		schedule.getFinanceMain().setRepayRateBasis(rateBasis);
		schedule.getFinanceMain().setAllowGrcRepay(allowGrcRepay);
		schedule.getFinanceMain().setScheduleMethod(scheduleMethod);
		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmount);
		if (reqRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
			schedule.getFinanceMain().setCalculateRepay(true);
			schedule.getFinanceMain().setEqualRepay(true);
		} else {
			schedule.getFinanceMain().setCalculateRepay(false);
			schedule.getFinanceMain().setEqualRepay(false);
		}

		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		return schedule;
	}
}
