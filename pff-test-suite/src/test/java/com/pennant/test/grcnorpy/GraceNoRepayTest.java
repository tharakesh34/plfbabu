package com.pennant.test.grcnorpy;

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
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.util.ExcelFile;
import com.pennant.util.GenericDataProcess;

public class GraceNoRepayTest extends GenericDataProcess {
	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;
	static String excelFileName;

	@BeforeMethod
	public void createObject() {
		schedule = super.prepareCommonData(true);
		FinanceMain finance = schedule.getFinanceMain();
		finance.setAllowGrcRepay(false);
		// finance.setGrcSchdMthd(null);
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

		schedule.getFinanceMain().setFinReference(fileName);
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
				new Object[] { "RR_CHK_GRCNORPY_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, false, false, false,
						BigDecimal.valueOf(18480672),
						BigDecimal.valueOf(10980672) },
				new Object[] { "RR_CHK_GRCNORPY_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true, false,
						BigDecimal.valueOf(8384477),
						BigDecimal.valueOf(10613779) },
				new Object[] { "RR_CHK_GRCNORPY_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true, false,
						BigDecimal.valueOf(97321506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "RR_CHK_GRCNORPY_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						false, false, false, BigDecimal.valueOf(18404121),
						BigDecimal.valueOf(10904121) },
				new Object[] { "RR_CHK_GRCNORPY_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true, false,
						BigDecimal.valueOf(9368869),
						BigDecimal.valueOf(10579637) },
				new Object[] { "RR_CHK_GRCNORPY_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, false, false, false,
						BigDecimal.valueOf(14306502),
						BigDecimal.valueOf(10798246) },
				new Object[] { "RR_CHK_GRCNORPY_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true, false,
						BigDecimal.valueOf(8110129),
						BigDecimal.valueOf(10570758) },
				new Object[] { "FR_CHK_GRCNORPY_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, false, false, true,
						BigDecimal.valueOf(21345979),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_CHK_GRCNORPY_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, true, true, true,
						BigDecimal.valueOf(8653827),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_CHK_GRCNORPY_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, true, true, true,
						BigDecimal.valueOf(97321506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_CHK_GRCNORPY_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						false, false, true, BigDecimal.valueOf(21477315),
						BigDecimal.valueOf(13977315) },
				new Object[] { "FR_CHK_GRCNORPY_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, true, true, true,
						BigDecimal.valueOf(12070126),
						BigDecimal.valueOf(13977315) },
				new Object[] { "FR_CHK_GRCNORPY_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, false, false, true,
						BigDecimal.valueOf(14821506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_CHK_GRCNORPY_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, true, true, true,
						BigDecimal.valueOf(8663574),
						BigDecimal.valueOf(13845979) } };
	}
}
