package com.pennanttech.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.util.Dataset;
import com.pennanttech.util.PrintFactory;

import jxl.Cell;

public class CrtReducingRateHighTest {
	FinScheduleData	schedule;
	Cell[]			data;
	long			t1;

	public CrtReducingRateHighTest(FinScheduleData schedule, Cell[] data, long t1) {
		super();

		this.schedule = schedule;
		this.data = data;
		this.t1 = 0;
	}

	@Test
	public void testSchedule() throws IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException {

		String name = Dataset.getString(data, 0);
		PrintFactory.toConsole(name);

		// Get the expected results

		long actLastInst = 0;
		long actPrvCloseBal = 0;

		// Calculate the schedule
		schedule = execute(schedule);
		List<FinanceScheduleDetail> schdDetails = schedule.getFinanceScheduleDetails();

		int size = schdDetails.size();
		long expLastInst = Dataset.getLong(data, 33);
		long expPrvCloseBal = Dataset.getLong(data, 31);
		actLastInst = schdDetails.get(size - 1).getRepayAmount().longValue();
		actPrvCloseBal = schdDetails.get(size - 2).getClosingBalance().longValue();
		
		PrintFactory.scheduleToExcel(name, schedule);

		Assert.assertEquals(actPrvCloseBal, expPrvCloseBal, (name + " Prv Closing Bal: "));
		Assert.assertEquals(actLastInst, expLastInst, (name + " Last Installment: "));
	}

	public static FinScheduleData execute(FinScheduleData model) throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		FinScheduleData schedule = (FinScheduleData) BeanUtils.cloneBean(model);

		//_______________________________________________________________________________________________
		// Setting to be moved to actual test class
		//_______________________________________________________________________________________________

		schedule.getFinanceMain().setGrcRateBasis(CalculationConstants.RATE_BASIS_R);
		schedule.getFinanceMain().setRepayRateBasis(CalculationConstants.RATE_BASIS_R);
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule, BigDecimal.ZERO);

		if (schedule.getFinanceMain().isPlanEMIHAlw()) {
			if (schedule.getFinanceMain().getPlanEMIHMethod().equals(FinanceConstants.EMIH_FRQ)) {
				schedule = ScheduleCalculator.getFrqEMIHoliday(schedule);
			} else {
				schedule = ScheduleCalculator.getAdhocEMIHoliday(schedule);
			}
		}

		return schedule;
	}
}
