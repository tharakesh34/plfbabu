package com.pennant.test.ratechange;

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
import com.pennant.util.ScheduleData;

public class GraceAdjustMaturityTest extends ScheduleData {

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
	 * 		8. Recalculation Type</br>
	 * <font>
	 */
	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, boolean calRepay, boolean eqRepay,
			BigDecimal expLastRepayAmt, BigDecimal expTotProfit, String recalType)
			throws IllegalAccessException, InvocationTargetException {

		excelFileName = fileName;

		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setCalculateRepay(calRepay);
		schedule.getFinanceMain().setEqualRepay(eqRepay);

		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		// Change rate by adding margin for the review period
		schedule.getFinanceMain().setEventFromDate(
				DateUtility.getDate("31/12/2011"));
		schedule.getFinanceMain().setEventToDate(
				DateUtility.getDate("30/06/2012"));
		schedule.getFinanceMain().setRecalType(recalType);
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
	 * 		8. Recalculation Type</br>
	 * <font>
	 */
	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "RR_GRCPFT_MRCAM_EQUAL_REQ",
						BigDecimal.valueOf(4500000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(4838294),
						BigDecimal.valueOf(15668157),
						CalculationConstants.RPYCHG_ADJMDT },
				new Object[] { "RR_GRCPFT_MRCAM_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(5004767),
						BigDecimal.valueOf(15680162),
						CalculationConstants.RPYCHG_ADJMDT },
				new Object[] { "RR_GRCPFT_MRCAM_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(100624246),
						BigDecimal.valueOf(22548630),
						CalculationConstants.RPYCHG_ADJMDT },
				new Object[] { "RR_GRCPFT_MRCAM_PRI_REQ",
						BigDecimal.valueOf(4500000), CalculationConstants.PRI,
						false, false, BigDecimal.valueOf(4679245),
						BigDecimal.valueOf(15509108),
						CalculationConstants.RPYCHG_ADJMDT },
				new Object[] { "RR_GRCPFT_MRCAM_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(5539010),
						BigDecimal.valueOf(15570290),
						CalculationConstants.RPYCHG_ADJMDT },
				new Object[] { "RR_GRCPFT_MRCAM_PRIPFT_REQ",
						BigDecimal.valueOf(4200000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(3421224),
						BigDecimal.valueOf(15379259),
						CalculationConstants.RPYCHG_ADJMDT },
				new Object[] { "RR_GRCPFT_MRCAM_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(4192669),
						BigDecimal.valueOf(15436158),
						CalculationConstants.RPYCHG_ADJMDT } };
	}
}
