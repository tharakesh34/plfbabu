package com.pennant.test.mdd_tillmdt.mcr_tillmdt;

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

public class GraceMDDMCRTest extends ScheduleData {

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
	 * 		8. Disbursement Amount on Each maintenance</br>
	 * 		9. Change Repayment Amount value</br>
	 * <font>
	 */
	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, boolean calRepay, boolean eqRepay,
			BigDecimal expLastRepayAmt, BigDecimal expTotProfit,
			BigDecimal disbAmt, BigDecimal chgRpyAmt)
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
				CalculationConstants.RPYCHG_TILLDATE);
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
		
		// Add Disbursement with recalculation Till Maturity
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("15/02/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("15/02/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_TILLMDT);
		schedule.getFinanceMain().setRecalToDate(null);
		schedule = ScheduleCalculator.addDisbursement(schedule, disbAmt,
				CalculationConstants.ADDTERM_AFTMDT, BigDecimal.ZERO);

		// Add Disbursement with recalculation Till Maturity
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("15/05/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("15/05/2011"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_TILLMDT);
		schedule.getFinanceMain().setRecalToDate(null);
		schedule = ScheduleCalculator.addDisbursement(schedule, disbAmt,
				CalculationConstants.ADDTERM_AFTMDT, BigDecimal.ZERO);
		
		// 3. Change Repayment's Process
		if (reqRepayAmt.compareTo(BigDecimal.ZERO) > 0) {
			schedule.getFinanceMain().setEventFromDate(
					DateUtility.getDate("31/01/2012"));
			schedule.getFinanceMain().setEventToDate(
					DateUtility.getDate("30/12/2012"));
			schedule.getFinanceMain().setRecalType(
					CalculationConstants.RPYCHG_TILLMDT);
			schedule.getFinanceMain().setRecalToDate(
					DateUtility.getDate("31/12/2012"));
			schedule.getFinanceMain().setRecalFromDate(
					DateUtility.getDate("31/12/2012"));
			schedule = ScheduleCalculator.changeRepay(schedule, chgRpyAmt,
					rpySchMethod);
		}

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
	 * 		9. Change Repayment Amount value</br>
	 * <font>
	 */
	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "RR_GRCPFT_MDDMCR_EQUAL_REQ",
						BigDecimal.valueOf(8000000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(18799352),
						BigDecimal.valueOf(16638681),
						BigDecimal.valueOf(25000000),
						BigDecimal.valueOf(13000000) },
				new Object[] { "RR_GRCPFT_MDDMCR_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(167008082),
						BigDecimal.valueOf(21847411),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO },
				new Object[] { "RR_GRCPFT_MDDMCR_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(156317540),
						BigDecimal.valueOf(21636460),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO },
				new Object[] { "RR_GRCPFT_MDDMCR_PRI_REQ",
						BigDecimal.valueOf(8000000), CalculationConstants.PRI,
						false, false, BigDecimal.valueOf(21533652),
						BigDecimal.valueOf(16622981),
						BigDecimal.valueOf(25000000),
						BigDecimal.valueOf(12750000) },
				new Object[] { "RR_GRCPFT_MDDMCR_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(167008082),
						BigDecimal.valueOf(21847411),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO },
				new Object[] { "RR_GRCPFT_MDDMCR_PRIPFT_REQ",
						BigDecimal.valueOf(8000000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(17959200),
						BigDecimal.valueOf(16556905),
						BigDecimal.valueOf(25000000),
						BigDecimal.valueOf(12500000) },
				new Object[] { "RR_GRCPFT_MDDMCR_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(167008082),
						BigDecimal.valueOf(21847411),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO } };
	}
}
