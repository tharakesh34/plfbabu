package com.pennant.test.changerepay;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.ScheduleData;

public class GraceNoPayTest extends ScheduleData {

	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;
	static String excelFileName;

	@BeforeMethod
	public void createObject() {
		schedule = super.getScheduleData(true);
		schedule.getFinanceMain().setDownPayment(BigDecimal.ZERO);
		schedule.getFinanceMain().setAllowGrcRepay(true);
		schedule.getFinanceMain().setGrcSchdMthd(CalculationConstants.PFT);
		schedule.getFinanceMain().setGrcRateBasis("R");
		schedule.getFinanceMain().setRepayRateBasis("R");
	}

	@AfterMethod
	public void destroyObject() {
		//ExcelFile.writeExcel(excelFileName, schedule);
		schedule = null;
	}

	/**
	 * <b>Properties Order :</br></b>
	 *  <font style="color:green">
	 * 		1. File Name (Either Excel file Name or Finance Reference while saving to DB) </br>
	 * 		2. Requested Repayment Amount</br>
	 * 		3. Schedule Repayment Method</br>
	 * 		4. Calculated Repay</br>
	 * 		5. Equal Repay</br>
	 * 		6. Expected Last Schedule Repayment Amount</br>
	 * 		7. Total Expected Finance Schedule Profit</br>
	 * <font>
	 */
	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, boolean calRepay, boolean eqRepay,
			BigDecimal expLastRepayAmt, BigDecimal expTotProfit)
			throws IllegalAccessException, InvocationTargetException {

		excelFileName = fileName;

		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setCalculateRepay(calRepay);
		schedule.getFinanceMain().setEqualRepay(eqRepay);

		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		BigDecimal amount = BigDecimal.ZERO;
		String schdMethod = CalculationConstants.NOPAY;

		// 1. Change Repayment's Process
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("30/04/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("30/06/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_ADJMDT);
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/12/2012"));
		schedule.getFinanceMain().setRecalFromDate(
				DateUtility.getDate("31/12/2012"));
		schedule = ScheduleCalculator.changeRepay(schedule, amount, schdMethod);

		// 2. Change Repayment's Process
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("31/10/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("31/12/2011"));
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/12/2012"));
		schedule.getFinanceMain().setRecalFromDate(
				DateUtility.getDate("31/12/2012"));
		schedule = ScheduleCalculator.changeRepay(schedule, amount, schdMethod);

		actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		Assert.assertTrue(actLastRepayAmt.compareTo(expLastRepayAmt) == 0
				&& actTotProfit.compareTo(expTotProfit) == 0);
	}

	/**
	 * <b>Properties Order :</br></b>
	 *  <font style="color:green">
	 * 		1. File Name (Either Excel file Name or Finance Reference while saving to DB) </br>
	 * 		2. Requested Repayment Amount</br>
	 * 		3. Schedule Repayment Method</br>
	 * 		4. Calculated Repay</br>
	 * 		5. Equal Repay</br>
	 * 		6. Expected Last Schedule Repayment Amount</br>
	 * 		7. Total Expected Finance Schedule Profit</br>
	 * <font>
	 */
	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "RR_GRCPFT_MCR_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(26015735),
						BigDecimal.valueOf(12194478) },
				new Object[] { "RR_GRCPFT_MCR_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(12671581),
						BigDecimal.valueOf(11709555) },
				new Object[] { "RR_GRCPFT_MCR_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(104366480),
						BigDecimal.valueOf(15041994) },
				new Object[] { "RR_GRCPFT_MCR_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						false, false, BigDecimal.valueOf(25930900),
						BigDecimal.valueOf(12109643) },
				new Object[] { "RR_GRCPFT_MCR_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(13684977),
						BigDecimal.valueOf(11669855) },
				new Object[] { "RR_GRCPFT_MCR_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(21351476),
						BigDecimal.valueOf(11994261) },
				new Object[] { "RR_GRCPFT_MCR_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(12127591),
						BigDecimal.valueOf(11655624) } };
	}
}
