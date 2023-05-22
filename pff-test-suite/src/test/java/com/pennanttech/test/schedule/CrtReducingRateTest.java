package com.pennanttech.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.util.Dataset;
import com.pennanttech.util.PrintFactory;

import jxl.Cell;

public class CrtReducingRateTest {
	FinScheduleData schedule;
	Cell[] data;
	long t1;

	public CrtReducingRateTest(FinScheduleData schedule, Cell[] data, long t1) {
		super();

		this.schedule = schedule;
		this.data = data;
		this.t1 = 0;
	}

	@Test
	public void testSchedule()
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		long t2 = DateUtil.getSysDate().getTime();

		String name = Dataset.getString(data, 0);
		PrintFactory.toConsole(name);

		// Get the expected results
		long expIntInGrace = Dataset.getLong(data, 26);
		long expCpzIntInGrace = Dataset.getLong(data, 27);
		long expTotalIntPaid = Dataset.getLong(data, 28);
		long expTotalCpz = Dataset.getLong(data, 29);
		long expLastInst = Dataset.getLong(data, 33);
		long expPrvCloseBal = Dataset.getLong(data, 31);
		int expRecords = Dataset.getInt(data, 34);
		long expFirstFee = Dataset.getInt(data, 35);
		long expFeeCharge = Dataset.getInt(data, 36);

		long actIntInGrace = 0;
		long actCpzIntInGrace = 0;
		long actTotalIntPaid = 0;
		long actTotalCpz = 0;
		long actLastInst = 0;
		long actPrvCloseBal = 0;
		int actRecords = 0;
		long actFirstFee = 0;
		long actFeeCharge = 0;

		// Calculate the schedule
		schedule = execute(schedule);
		FinanceMain fm = schedule.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = schedule.getFinanceScheduleDetails();

		actIntInGrace = fm.getTotalGrossGrcPft().longValue();
		actCpzIntInGrace = fm.getTotalGraceCpz().longValue();
		actTotalIntPaid = fm.getTotalProfit().longValue();
		actTotalCpz = fm.getTotalCpz().longValue();

		int size = schdDetails.size();
		actLastInst = schdDetails.get(size - 1).getRepayAmount().longValue();
		actPrvCloseBal = schdDetails.get(size - 2).getClosingBalance().longValue();
		actRecords = size;

		String cellStrValue = Dataset.getString(data, 10);
		actFirstFee = schdDetails.get(2).getFeeSchd().longValue();

		if (!cellStrValue.equals("DD") && !cellStrValue.equals("PS")) {
			for (int i = 2; i < size; i++) {
				actFeeCharge = actFeeCharge + schdDetails.get(i).getFeeSchd().longValue();
			}
		}

		// Get the actual results
		PrintFactory.toConsole(expIntInGrace, actIntInGrace);
		PrintFactory.toConsole(expCpzIntInGrace, actCpzIntInGrace);
		PrintFactory.toConsole(expTotalIntPaid, actTotalIntPaid);
		PrintFactory.toConsole(expTotalCpz, actTotalCpz);
		PrintFactory.toConsole(expPrvCloseBal, actPrvCloseBal);
		PrintFactory.toConsole(expLastInst, actLastInst);
		PrintFactory.toConsole(expRecords, actRecords);
		PrintFactory.toConsole(expFirstFee, actFirstFee);
		PrintFactory.toConsole(expFeeCharge, actFeeCharge);

		PrintFactory.scheduleToExcel(name, schedule);

		Assert.assertEquals(actIntInGrace, expIntInGrace, (name + " Grace Interst: "));
		Assert.assertEquals(actCpzIntInGrace, expCpzIntInGrace, (name + " Grace Cpz Interst: "));
		Assert.assertEquals(actTotalIntPaid, expTotalIntPaid, (name + " Total Interst Schd: "));
		Assert.assertEquals(actTotalCpz, expTotalCpz, (name + " Total Interst Cpz: "));
		Assert.assertEquals(actPrvCloseBal, expPrvCloseBal, (name + " Prv Closing Bal: "));
		Assert.assertEquals(actLastInst, expLastInst, (name + " Last Installment: "));
		Assert.assertEquals(actRecords, expRecords, (name + " Total Record:"));
		Assert.assertEquals(expFirstFee, actFirstFee, (name + " First Fee Amount:"));
		Assert.assertEquals(expFeeCharge, actFeeCharge, (name + " Total Fee Amount:"));

		long t3 = DateUtil.getSysDate().getTime();
		System.out.println("Time in long " + String.valueOf(t3 - t2));
		t1 = t1 + t3 - t2;
		System.out.println("total Time in long " + String.valueOf(t1));
	}

	public static FinScheduleData execute(FinScheduleData model)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		FinScheduleData schedule = (FinScheduleData) BeanUtils.cloneBean(model);

		// _______________________________________________________________________________________________
		// Setting to be moved to actual test class
		// _______________________________________________________________________________________________

		schedule.getFinanceMain().setGrcRateBasis(CalculationConstants.RATE_BASIS_R);
		schedule.getFinanceMain().setRepayRateBasis(CalculationConstants.RATE_BASIS_R);
		schedule = ScheduleGenerator.getNewSchd(schedule);

		// FIXME: Temorary
		String feeSchdMethod = schedule.getFinFeeDetailList().get(0).getFeeScheduleMethod();
		BigDecimal feeAmount = schedule.getFinFeeDetailList().get(0).getRemainingFee();

		if (StringUtils.equals(feeSchdMethod, CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
			schedule.getFinanceScheduleDetails().get(0).setFeeChargeAmt(feeAmount);
		}

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
