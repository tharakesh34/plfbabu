package com.pennant.test.mdd_adjterm;

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
import com.pennant.util.ExcelFile;
import com.pennant.util.GenericDataProcess;

public class GraceMDDTest extends GenericDataProcess {

	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;
	static String excelFileName;

	@BeforeMethod
	public void createObject() {
		schedule = super.prepareCommonData(true);
		schedule.getFinanceMain().setNumberOfTerms(24);
		schedule.getFinanceMain().setReqTerms(24);
		schedule.getFinanceMain().setDownPayment(BigDecimal.ZERO);
		schedule.getFinanceMain().setAllowGrcRepay(true);
		schedule.getFinanceMain().setGrcSchdMthd(CalculationConstants.PFT);
		schedule.getFinanceMain().setGrcRateBasis("R");
		schedule.getFinanceMain().setRepayRateBasis("R");
		schedule.getFinanceMain().setEventToDate(DateUtility.getDate("31/12/2013"));
		schedule.getFinanceMain().setMaturityDate(DateUtility.getDate("31/12/2013"));
	}

	@AfterMethod
	public void destroyObject() {
		ExcelFile.writeExcel(excelFileName, schedule);
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
	 * 		8. Disbursement Amount on Each maintenance</br>
	 * <font>
	 */
	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, boolean calRepay, boolean eqRepay,
			BigDecimal expLastRepayAmt, BigDecimal expTotProfit,
			BigDecimal disbAmt)
			throws IllegalAccessException, InvocationTargetException {

		excelFileName = fileName;

		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setCalculateRepay(calRepay);
		schedule.getFinanceMain().setEqualRepay(eqRepay);

		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		// Add Disbursement with Adjust Terms Recalculation Method
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("15/02/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("15/02/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_ADJTERMS);
		schedule.getFinanceMain().setRecalToDate(null);
		schedule = ScheduleCalculator.addDisbursement(schedule, disbAmt,
				CalculationConstants.ADDTERM_AFTMDT, BigDecimal.ZERO);

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
	 * 		8. Disbursement Amount on Each maintenance</br>
	 * <font>
	 */
	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "RR_GRCPFT_MDD_EQUAL_REQ",
						BigDecimal.valueOf(4500000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(2533839),
						BigDecimal.valueOf(17506072),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MDD_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(2723642),
						BigDecimal.valueOf(17521259),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MDD_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(100624247),
						BigDecimal.valueOf(22050000),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MDD_PRI_REQ",
						BigDecimal.valueOf(4500000), CalculationConstants.PRI,
						false, false, BigDecimal.valueOf(6849426),
						BigDecimal.valueOf(17321659),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MDD_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(3343693),
						BigDecimal.valueOf(17413180),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MDD_PRIPFT_REQ",
						BigDecimal.valueOf(4500000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(6540576),
						BigDecimal.valueOf(16539010),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MDD_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(5866215),
						BigDecimal.valueOf(17199167),
						BigDecimal.valueOf(10000000) } };
	}
}
