package com.pennant.testing.schedule;

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
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;

public class GraceNoPayFlatRateTest {
	static FinScheduleData schedule;

	@Test(dataProvider = "dataset")
	public void testSchedule(String name, String schMethod, long reqRepayAmt,
			long expLastRepayAmt, long expTotProfit) {
		schedule.getFinanceMain().setScheduleMethod(schMethod);
		schedule.getFinanceMain().setReqRepayAmount(
				BigDecimal.valueOf(reqRepayAmt));
		if (reqRepayAmt == 0) {
			schedule.getFinanceMain().setCalculateRepay(true);
			schedule.getFinanceMain().setEqualRepay(true);
		} else {
			schedule.getFinanceMain().setCalculateRepay(false);
			schedule.getFinanceMain().setEqualRepay(false);
		}

		// Generate the schedule.
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule);

		BigDecimal actLastRepayAmt = schedule.getFinanceScheduleDetails()
				.get(schedule.getFinanceScheduleDetails().size() - 1)
				.getRepayAmount();
		BigDecimal actTotProfit = schedule.getFinanceMain().getTotalGrossPft();

		Assert.assertEquals(actLastRepayAmt.longValue(), expLastRepayAmt);
		Assert.assertEquals(actTotProfit.longValue(), expTotProfit);
	}

	@BeforeMethod
	public void createObject() {
		schedule = new FinScheduleData();
		schedule.setFinanceMain(new FinanceMain());
		schedule.getDisbursementDetails().add(new FinanceDisbursement());

		FinanceMain finance = schedule.getFinanceMain();
		finance.setNumberOfTerms(12);
		finance.setAllowGrcPeriod(true);
		finance.setGraceBaseRate("L1");
		finance.setGraceSpecialRate("S1");
		finance.setGrcPftRate(BigDecimal.ZERO);
		finance.setGrcPftFrq("M0031");
		finance.setNextGrcPftDate(DateUtility.getDate("31/01/2011"));
		finance.setAllowGrcPftRvw(true);
		finance.setGrcPftRvwFrq("Q0331");
		finance.setNextGrcPftRvwDate(DateUtility.getDate("31/03/2011"));
		finance.setAllowGrcCpz(true);
		finance.setGrcCpzFrq("H0631");
		finance.setNextGrcCpzDate(DateUtility.getDate("30/06/2011"));
		finance.setRepayBaseRate("L1");
		finance.setRepaySpecialRate("S1");
		finance.setRepayProfitRate(BigDecimal.ZERO);
		finance.setRepayFrq("M0031");
		finance.setNextRepayDate(DateUtility.getDate("31/01/2012"));
		finance.setRepayPftFrq("M0031");
		finance.setNextRepayPftDate(DateUtility.getDate("31/01/2012"));
		finance.setAllowRepayRvw(true);
		finance.setRepayRvwFrq("Q0331");
		finance.setNextRepayRvwDate(DateUtility.getDate("31/03/2012"));
		finance.setAllowRepayCpz(true);
		finance.setRepayCpzFrq("H0631");
		finance.setNextRepayCpzDate(DateUtility.getDate("30/06/2012"));
		finance.setMaturityDate(DateUtility.getDate("31/12/2012"));
		finance.setCpzAtGraceEnd(true);
		finance.setDownPayment(new BigDecimal(10000000));
		finance.setTotalProfit(BigDecimal.ZERO);
		finance.setTotalGrossPft(BigDecimal.ZERO);
		finance.setGrcRateBasis("F");
		finance.setRepayRateBasis("F");
		finance.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		finance.setReqTerms(12);
		finance.setIncreaseTerms(false);
		finance.setEventFromDate(DateUtility.getDate("01/01/2011"));
		finance.setEventToDate(DateUtility.getDate("31/12/2012"));
		finance.setRecalType("CURPRD");
		finance.setGrcPeriodEndDate(DateUtility.getDate("31/12/2011"));
		finance.setAllowGrcRepay(true);
		finance.setGrcSchdMthd(CalculationConstants.NOPAY);
		finance.setFinStartDate(DateUtility.getDate("01/01/2011"));
		finance.setExcludeDeferedDates(false);

		FinanceDisbursement disbursement = schedule.getDisbursementDetails()
				.get(0);
		disbursement.setDisbAmount(new BigDecimal(100000000));
		disbursement.setDisbDate(DateUtility.getDate("01/01/2011"));
	}

	@AfterMethod
	public void destroyObject() {
		schedule = null;
	}

	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "SN04_FR_EQUAL_REQ", CalculationConstants.EQUAL,
						8000000, 15845979, 13845979 },
				new Object[] { "SN04_FR_EQUAL", CalculationConstants.EQUAL, 0,
						8653827, 13845979 },
				new Object[] { "SN04_FR_PFT", CalculationConstants.PFT, 0,
						97321506, 13845979 },
				new Object[] { "SN04_FR_PRI_REQ", CalculationConstants.PRI,
						7500000, 21477315, 13977315 },
				new Object[] { "SN04_FR_PRI", CalculationConstants.PRI, 0,
						12070126, 13977315 },
				new Object[] { "SN04_FR_PRIPFT_REQ",
						CalculationConstants.PRI_PFT, 7500000, 14821506,
						13845979 },
				new Object[] { "SN04_FR_PRIPFT", CalculationConstants.PRI_PFT,
						0, 8663574, 13845979 } };
	}
}
