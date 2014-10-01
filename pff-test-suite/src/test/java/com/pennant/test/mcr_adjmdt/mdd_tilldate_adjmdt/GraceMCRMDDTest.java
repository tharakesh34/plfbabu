package com.pennant.test.mcr_adjmdt.mdd_tilldate_adjmdt;

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

public class GraceMCRMDDTest extends ScheduleData {

	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;
	static String excelFileName;

	@BeforeMethod
	public void createObject() {
		schedule = super.getScheduleData(true);
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
	 * 		8. Disbursement Amount on Each maintenance</br>
	 * <font>
	 */
	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, boolean calRepay, boolean eqRepay,
			BigDecimal expLastRepayAmt, BigDecimal expTotProfit,
			BigDecimal disbAmt) throws IllegalAccessException,
			InvocationTargetException {

		excelFileName = fileName;

		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setCalculateRepay(calRepay);
		schedule.getFinanceMain().setEqualRepay(eqRepay);

		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		// Set NO PAYMENT for three months in Grace period
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("30/04/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("30/06/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_ADJMDT);
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/12/2013"));
		schedule.getFinanceMain().setRecalFromDate(
				DateUtility.getDate("31/12/2013"));
		schedule = ScheduleCalculator.changeRepay(schedule, BigDecimal.ZERO,
				CalculationConstants.NOPAY);

		// Set NO PAYMENT for three months in Grace period
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("31/10/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("31/12/2011"));
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/12/2013"));
		schedule.getFinanceMain().setRecalFromDate(
				DateUtility.getDate("31/12/2013"));
		schedule = ScheduleCalculator.changeRepay(schedule, BigDecimal.ZERO,
				CalculationConstants.NOPAY);

		// Add Disbursement with recalculation till date
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("15/02/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("15/02/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_TILLDATE);
		schedule.getFinanceMain().setRecalToDate(
				DateUtility.getDate("31/03/2012"));
		schedule = ScheduleCalculator.addDisbursement(schedule, disbAmt,
				CalculationConstants.ADDTERM_AFTMDT, BigDecimal.ZERO);

		// Add Disbursement with recalculation Adjust to maturity
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("15/05/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("15/05/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_ADJMDT);
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
				new Object[] { "RR_GRCPFT_MCRMDD_EQUAL_REQ",
						BigDecimal.valueOf(4500000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(20538230),
						BigDecimal.valueOf(18680076),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MCRMDD_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(20704607),
						BigDecimal.valueOf(18691985),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MCRMDD_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(125146904),
						BigDecimal.valueOf(26821014),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MCRMDD_PRI_REQ",
						BigDecimal.valueOf(4500000), CalculationConstants.PRI,
						false, false, BigDecimal.valueOf(20361765),
						BigDecimal.valueOf(18500329),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MCRMDD_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(21221047),
						BigDecimal.valueOf(18561028),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MCRMDD_PRIPFT_REQ",
						BigDecimal.valueOf(4500000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(10564162),
						BigDecimal.valueOf(17795590),
						BigDecimal.valueOf(10000000) },
				new Object[] { "RR_GRCPFT_MCRMDD_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(18278680),
						BigDecimal.valueOf(18360432),
						BigDecimal.valueOf(10000000) } };
	}
}
