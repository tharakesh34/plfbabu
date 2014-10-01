package com.pennant.test.mdd_adjmdt.mcr_tillmdt.rc_curprd;

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

public class GraceMDDMCRMRCTest extends GenericDataProcess {

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

		// 1. Change Repayment's Process
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

		// 2. Change Repayment's Process
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
					DateUtility.getDate("30/12/2013"));
			schedule.getFinanceMain().setRecalType(
					CalculationConstants.RPYCHG_TILLMDT);
			schedule.getFinanceMain().setRecalToDate(
					DateUtility.getDate("31/12/2013"));
			schedule.getFinanceMain().setRecalFromDate(
					DateUtility.getDate("31/12/2013"));
			schedule = ScheduleCalculator.changeRepay(schedule, chgRpyAmt,
					rpySchMethod);
		}
		
		// Change rate by adding margin for the review period 
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("31/03/2012"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("30/06/2012"));
		schedule.getFinanceMain().setRecalType(
				CalculationConstants.RPYCHG_CURPRD);
		schedule = ScheduleCalculator.changeRate(schedule, "L1", "S1",
				BigDecimal.ONE, BigDecimal.ZERO, true);

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
				new Object[] { "RR_GRCPFT_MDDMCRMRC_EQUAL_REQ",
						BigDecimal.valueOf(2500000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(7728783),
						BigDecimal.valueOf(22745058),
						BigDecimal.valueOf(25000000),
						BigDecimal.valueOf(6950000) },
				new Object[] { "RR_GRCPFT_MDDMCRMRC_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(6980208),
						BigDecimal.valueOf(22691171),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO },
				new Object[] { "RR_GRCPFT_MDDMCRMRC_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(156317539),
						BigDecimal.valueOf(33441827),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO },
				new Object[] { "RR_GRCPFT_MDDMCRMRC_PRI_REQ",
						BigDecimal.valueOf(8000000), CalculationConstants.PRI,
						false, false, BigDecimal.valueOf(8127527),
						BigDecimal.valueOf(22557909),
						BigDecimal.valueOf(25000000),
						BigDecimal.valueOf(6925000) },
				new Object[] { "RR_GRCPFT_MDDMCRMRC_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(7837678),
						BigDecimal.valueOf(22537319),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO },
				new Object[] { "RR_GRCPFT_MDDMCRMRC_PRIPFT_REQ",
						BigDecimal.valueOf(2500000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(7041469),
						BigDecimal.valueOf(22447802),
						BigDecimal.valueOf(25000000),
						BigDecimal.valueOf(6450000) },
				new Object[] { "RR_GRCPFT_MDDMCRMRC_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(6513241),
						BigDecimal.valueOf(22408899),
						BigDecimal.valueOf(25000000), BigDecimal.ZERO } };
	}
}
