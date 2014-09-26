package com.pennant.test;

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
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;

public class ScheduleGraceProfitTest {
	static FinScheduleData schedule;
	static BigDecimal actLastRepayAmt;
	static BigDecimal actTotProfit;

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
		finance.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		finance.setReqTerms(12);
		finance.setIncreaseTerms(false);
		finance.setEventFromDate(DateUtility.getDate("01/01/2011"));
		finance.setEventToDate(DateUtility.getDate("31/12/2012"));
		finance.setRecalType("CURPRD");
		finance.setGrcPeriodEndDate(DateUtility.getDate("31/12/2011"));
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

	@Test(dataProvider = "dataset")
	public void testSchedule(String fileName, BigDecimal reqRepayAmt,
			String rpySchMethod, String grcSchMethod, boolean alwGrcRpy,
			boolean calRepay, boolean eqRepay, boolean flatRate,
			BigDecimal expLastRepayAmt, BigDecimal expTotProfit)
			throws IllegalAccessException, InvocationTargetException {

		schedule.getFinanceMain().setReqRepayAmount(reqRepayAmt);
		schedule.getFinanceMain().setScheduleMethod(rpySchMethod);
		schedule.getFinanceMain().setAllowGrcRepay(alwGrcRpy);
		if (alwGrcRpy) {
			schedule.getFinanceMain().setGrcSchdMthd(grcSchMethod);
		}
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

	@DataProvider
	public Object[][] dataset() {
		return new Object[][] {
				new Object[] { "RR_GRCPFT_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, CalculationConstants.PFT,
						true, false, false, false,
						BigDecimal.valueOf(11250745),
						BigDecimal.valueOf(10347622) },
				new Object[] { "RR_GRCPFT_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, CalculationConstants.PFT,
						true, true, true, false, BigDecimal.valueOf(7802114),
						BigDecimal.valueOf(10222300) },
				new Object[] { "RR_GRCPFT_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, CalculationConstants.PFT,
						true, true, true, false, BigDecimal.valueOf(90561822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "RR_GRCPFT_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						CalculationConstants.PFT, true, false, false, false,
						BigDecimal.valueOf(11182143),
						BigDecimal.valueOf(10279020) },
				new Object[] { "RR_GRCPFT_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, CalculationConstants.PFT,
						true, true, true, false, BigDecimal.valueOf(8718137),
						BigDecimal.valueOf(10190530) },
				new Object[] { "RR_GRCPFT_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, CalculationConstants.PFT,
						true, false, false, false, BigDecimal.valueOf(7546818),
						BigDecimal.valueOf(10182267) },
				new Object[] { "RR_GRCPFT_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, CalculationConstants.PFT,
						true, true, true, false, BigDecimal.valueOf(7546818),
						BigDecimal.valueOf(10182267) },
				new Object[] { "FR_GRCPFT_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, CalculationConstants.PFT,
						true, false, false, true, BigDecimal.valueOf(14133123),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, CalculationConstants.PFT,
						true, true, true, true, BigDecimal.valueOf(8052763),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, CalculationConstants.PFT,
						true, true, true, true, BigDecimal.valueOf(90561822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						CalculationConstants.PFT, true, false, false, true,
						BigDecimal.valueOf(14255337),
						BigDecimal.valueOf(13352214) },
				new Object[] { "FR_GRCPFT_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, CalculationConstants.PFT,
						true, true, true, true, BigDecimal.valueOf(11231767),
						BigDecimal.valueOf(13352214) },
				new Object[] { "FR_GRCPFT_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, CalculationConstants.PFT,
						true, false, false, true, BigDecimal.valueOf(8061822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "FR_GRCPFT_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, CalculationConstants.PFT,
						true, true, true, true, BigDecimal.valueOf(8061822),
						BigDecimal.valueOf(13230000) },
				new Object[] { "RR_GRCNOPAY_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, CalculationConstants.NOPAY,
						true, false, false, false,
						BigDecimal.valueOf(18480672),
						BigDecimal.valueOf(10980672) },
				new Object[] { "RR_GRCNOPAY_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, CalculationConstants.NOPAY,
						true, true, true, false, BigDecimal.valueOf(8384477),
						BigDecimal.valueOf(10613779) },
				new Object[] { "RR_GRCNOPAY_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, CalculationConstants.NOPAY,
						true, true, true, false, BigDecimal.valueOf(97321506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "RR_GRCNOPAY_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						CalculationConstants.NOPAY, true, false, false, false,
						BigDecimal.valueOf(18404121),
						BigDecimal.valueOf(10904121) },
				new Object[] { "RR_GRCNOPAY_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, CalculationConstants.NOPAY,
						true, true, true, false, BigDecimal.valueOf(9368869),
						BigDecimal.valueOf(10579637) },
				new Object[] { "RR_GRCNOPAY_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT,
						CalculationConstants.NOPAY, true, false, false, false,
						BigDecimal.valueOf(14306502),
						BigDecimal.valueOf(10798246) },
				new Object[] { "RR_GRCNOPAY_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT,
						CalculationConstants.NOPAY, true, true, true, false,
						BigDecimal.valueOf(8110129),
						BigDecimal.valueOf(10570758) },
				new Object[] { "FR_GRCNOPAY_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, CalculationConstants.NOPAY,
						true, false, false, true, BigDecimal.valueOf(21345979),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNOPAY_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, CalculationConstants.NOPAY,
						true, true, true, true, BigDecimal.valueOf(8653827),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNOPAY_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, CalculationConstants.NOPAY,
						true, true, true, true, BigDecimal.valueOf(97321506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNOPAY_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						CalculationConstants.NOPAY, true, false, false, true,
						BigDecimal.valueOf(21477315),
						BigDecimal.valueOf(13977315) },
				new Object[] { "FR_GRCNOPAY_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, CalculationConstants.NOPAY,
						true, true, true, true, BigDecimal.valueOf(12070126),
						BigDecimal.valueOf(13977315) },
				new Object[] { "FR_GRCNOPAY_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT,
						CalculationConstants.NOPAY, true, false, false, true,
						BigDecimal.valueOf(14821506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNOPAY_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT,
						CalculationConstants.NOPAY, true, true, true, true,
						BigDecimal.valueOf(8663574),
						BigDecimal.valueOf(13845979) },
				new Object[] { "RR_GRCNORPYCHK_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, null, false, false, false,
						false, BigDecimal.valueOf(18480672),
						BigDecimal.valueOf(10980672) },
				new Object[] { "RR_GRCNORPYCHK_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, null, false, true, true,
						false, BigDecimal.valueOf(8384477),
						BigDecimal.valueOf(10613779) },
				new Object[] { "RR_GRCNORPYCHK_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, null, false, true, true,
						false, BigDecimal.valueOf(97321506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "RR_GRCNORPYCHK_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						null, false, false, false, false,
						BigDecimal.valueOf(18404121),
						BigDecimal.valueOf(10904121) },
				new Object[] { "RR_GRCNORPYCHK_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, null, false, true, true,
						false, BigDecimal.valueOf(9368869),
						BigDecimal.valueOf(10579637) },
				new Object[] { "RR_GRCNORPYCHK_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, null, false, false,
						false, false, BigDecimal.valueOf(14306502),
						BigDecimal.valueOf(10798246) },
				new Object[] { "RR_GRCNORPYCHK_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, null, false, true, true,
						false, BigDecimal.valueOf(8110129),
						BigDecimal.valueOf(10570758) },
				new Object[] { "FR_GRCNORPYCHK_EQUAL_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.EQUAL, null, false, false, false,
						true, BigDecimal.valueOf(21345979),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNORPYCHK_EQUAL", BigDecimal.ZERO,
						CalculationConstants.EQUAL, null, false, true, true,
						true, BigDecimal.valueOf(8653827),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNORPYCHK_PFT", BigDecimal.ZERO,
						CalculationConstants.PFT, null, false, true, true,
						true, BigDecimal.valueOf(97321506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNORPYCHK_PRI_REQ",
						BigDecimal.valueOf(7500000), CalculationConstants.PRI,
						null, false, false, false, true,
						BigDecimal.valueOf(21477315),
						BigDecimal.valueOf(13977315) },
				new Object[] { "FR_GRCNORPYCHK_PRI", BigDecimal.ZERO,
						CalculationConstants.PRI, null, false, true, true,
						true, BigDecimal.valueOf(12070126),
						BigDecimal.valueOf(13977315) },
				new Object[] { "FR_GRCNORPYCHK_PRIPFT_REQ",
						BigDecimal.valueOf(7500000),
						CalculationConstants.PRI_PFT, null, false, false,
						false, true, BigDecimal.valueOf(14821506),
						BigDecimal.valueOf(13845979) },
				new Object[] { "FR_GRCNORPYCHK_PRIPFT", BigDecimal.ZERO,
						CalculationConstants.PRI_PFT, null, false, true, true,
						true, BigDecimal.valueOf(8663574),
						BigDecimal.valueOf(13845979) } };
	}
}
