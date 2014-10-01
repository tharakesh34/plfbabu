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
import com.pennant.util.ExcelFile;
import com.pennant.util.ScheduleData;

public class GraceProfitTest extends ScheduleData {

	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;
	static String excelFileName;

	@BeforeMethod
	public void createObject() {
		schedule = super.getScheduleData(true);
		schedule.getFinanceMain().setAllowGrcRepay(true);
		schedule.getFinanceMain().setGrcSchdMthd(CalculationConstants.PFT);
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
	 * 		6. Flat Rate (true when flat rate and false when Reduce Rate)</br>
	 * 		7. Expected Last Schedule Repayment Amount</br>
	 * 		8. Total Expected Finance Schedule Profit</br>
	 * <font>
	 */
	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, boolean calRepay, boolean eqRepay,
			boolean flatRate, BigDecimal expLastRepayAmt,
			BigDecimal expTotProfit) throws IllegalAccessException,
			InvocationTargetException {

		excelFileName = fileName;

		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setCalculateRepay(calRepay);
		schedule.getFinanceMain().setEqualRepay(eqRepay);
		if (flatRate) {
			schedule.getFinanceMain().setGrcRateBasis("F");
			schedule.getFinanceMain().setRepayRateBasis("F");
		} else {
			schedule.getFinanceMain().setGrcRateBasis("R");
			schedule.getFinanceMain().setRepayRateBasis("R");
		}

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
	 * 		6. Flat Rate (true when flat rate and false when Reduce Rate)</br>
	 * 		7. Expected Last Schedule Repayment Amount</br>
	 * 		8. Total Expected Finance Schedule Profit</br>
	 * <font>
	 */
	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "RR_GRCPFT_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, false, false, false,
						BigDecimal.valueOf(11250745),
						BigDecimal.valueOf(10347622) },
				new Object[] { "RR_GRCPFT_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true, false,
						BigDecimal.valueOf(7802114),
						BigDecimal.valueOf(10222300) },
				new Object[] { "RR_GRCPFT_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true, false,
						BigDecimal.valueOf(90561822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "RR_GRCPFT_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						false, false, false, BigDecimal.valueOf(11182143),
						BigDecimal.valueOf(10279020) },
				new Object[] { "RR_GRCPFT_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true, false,
						BigDecimal.valueOf(8718137),
						BigDecimal.valueOf(10190530) },
				new Object[] { "RR_GRCPFT_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, false, false, false,
						BigDecimal.valueOf(7546818),
						BigDecimal.valueOf(10182267) },
				new Object[] { "RR_GRCPFT_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true, false,
						BigDecimal.valueOf(7546818),
						BigDecimal.valueOf(10182267) },
				new Object[] { "FR_GRCPFT_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, false, false, true,
						BigDecimal.valueOf(14133123),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true, true,
						BigDecimal.valueOf(8052763),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true, true,
						BigDecimal.valueOf(90561822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						false, false, true, BigDecimal.valueOf(14255337),
						BigDecimal.valueOf(13352214) },
				new Object[] { "FR_GRCPFT_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true, true,
						BigDecimal.valueOf(11231767),
						BigDecimal.valueOf(13352214) },
				new Object[] { "FR_GRCPFT_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, false, false, true,
						BigDecimal.valueOf(8061822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true, true,
						BigDecimal.valueOf(8061822),
						BigDecimal.valueOf(13230000) } };
	}
}
