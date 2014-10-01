package com.pennant.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.util.ScheduleData;

public class NoGraceTest extends ScheduleData {
	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;
	static String excelFileName;

	@BeforeMethod
	public void createObject() {
		schedule = super.getScheduleData(false);
		schedule.getFinanceMain().setNumberOfTerms(24);
		schedule.getFinanceMain().setDownPayment(BigDecimal.ZERO);
		schedule.getFinanceMain().setReqTerms(24);
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

		schedule.getFinanceMain().setFinReference(fileName);
		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setCalculateRepay(calRepay);
		schedule.getFinanceMain().setEqualRepay(eqRepay);

		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

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
				new Object[] { "RR_NOGRC_EQUAL_REQ",
						BigDecimal.valueOf(4400000),
						CalculationConstants.EQUAL, false, false,
						BigDecimal.valueOf(6769352),
						BigDecimal.valueOf(7969352) },
				new Object[] { "RR_NOGRC_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true,
						BigDecimal.valueOf(4491924),
						BigDecimal.valueOf(7806107) },
				new Object[] { "RR_NOGRC_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true,
						BigDecimal.valueOf(100624247),
						BigDecimal.valueOf(14700000) },
				new Object[] { "RR_NOGRC_PRI_REQ", BigDecimal.valueOf(4400000),
						CalculationConstants.PRI, false, false,
						BigDecimal.valueOf(6626997),
						BigDecimal.valueOf(7826997) },
				new Object[] { "RR_NOGRC_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true,
						BigDecimal.valueOf(5043765),
						BigDecimal.valueOf(7715006) },
				new Object[] { "RR_NOGRC_PRIPFT_REQ",
						BigDecimal.valueOf(4000000),
						CalculationConstants.PRI_PFT, false, false,
						BigDecimal.valueOf(8049940),
						BigDecimal.valueOf(7912225) },
				new Object[] { "RR_NOGRC_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true,
						BigDecimal.valueOf(4192669),
						BigDecimal.valueOf(7629400) } };
	}
}
