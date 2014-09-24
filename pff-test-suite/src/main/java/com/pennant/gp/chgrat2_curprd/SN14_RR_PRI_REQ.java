package com.pennant.gp.chgrat2_curprd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import com.pennant.TestingUtil;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class SN14_RR_PRI_REQ extends TestingUtil {

	private static boolean isSuccess = false;
	private static BigDecimal expectedResult = new BigDecimal(5119555);
	private static BigDecimal expectedTotPft = new BigDecimal(14783472);
	private static BigDecimal resultedTotPft = BigDecimal.ZERO;

	private static String getFile() {
		return getFileLoc()
				+ SN14_RR_PRI_REQ.class.getName()
				+ ".xls";

	}

	public static boolean RunTestCase() {
		try {

			// Tesing Code
			FinanceMain sh = new FinanceMain();
			sh.setNumberOfTerms(24);
			sh.setAllowGrcPeriod(true);
			sh.setGraceBaseRate("L1");
			sh.setGraceSpecialRate("S1");
			sh.setGrcPftRate(BigDecimal.ZERO);
			sh.setGrcPftFrq("M0031");// Monthly
			sh.setNextGrcPftDate(DateUtility.getDate("31/01/2011"));
			sh.setAllowGrcPftRvw(true);
			sh.setGrcPftRvwFrq("Q0331");
			sh.setNextGrcPftRvwDate(DateUtility.getDate("31/03/2011"));
			sh.setAllowGrcCpz(true);
			sh.setGrcCpzFrq("H0631");
			sh.setNextGrcCpzDate(DateUtility.getDate("30/06/2011"));
			sh.setRepayBaseRate("L1");
			sh.setRepaySpecialRate("S1");
			sh.setRepayProfitRate(BigDecimal.ZERO);
			sh.setRepayFrq("M0031");// M0031
			sh.setNextRepayDate(DateUtility.getDate("31/01/2012"));
			sh.setRepayPftFrq("M0031");
			sh.setNextRepayPftDate(DateUtility.getDate("31/01/2012"));
			sh.setAllowRepayRvw(true);
			sh.setRepayRvwFrq("Q0331");
			sh.setNextRepayRvwDate(DateUtility.getDate("31/03/2012"));
			sh.setAllowRepayCpz(true);
			sh.setRepayCpzFrq("H0631");
			sh.setNextRepayCpzDate(DateUtility.getDate("30/06/2012"));
			sh.setMaturityDate(DateUtility.getDate("31/12/2013"));
			sh.setCpzAtGraceEnd(true);
			sh.setDownPayment(BigDecimal.ZERO);
			sh.setReqRepayAmount(new BigDecimal(4472500));
			sh.setTotalProfit(BigDecimal.ZERO);
			sh.setTotalGrossPft(BigDecimal.ZERO);
			sh.setGrcRateBasis("R");
			sh.setRepayRateBasis("R");
			sh.setScheduleMethod(CalculationConstants.PRI);
			sh.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
			sh.setCalculateRepay(false);
			sh.setEqualRepay(false);
			sh.setReqTerms(24);
			sh.setIncreaseTerms(false);
			sh.setEventFromDate(DateUtility.getDate("01/01/2011"));
			sh.setEventToDate(DateUtility.getDate("31/12/2012"));
			sh.setRecalType("CURPRD");
			sh.setGrcPeriodEndDate(DateUtility.getDate("31/12/2011"));
			sh.setAllowGrcRepay(true);
			sh.setGrcSchdMthd(CalculationConstants.PFT);
			sh.setFinStartDate(DateUtility.getDate("01/01/2011"));
			sh.setExcludeDeferedDates(false);

			// ADD Disbursements
			FinanceDisbursement dd = new FinanceDisbursement();
			dd.setDisbAmount(new BigDecimal(100000000));
			dd.setDisbDate(DateUtility.getDate("01/01/2011"));
			// sh.getDisbursementDetails().add(dd);

			// generate schedule
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(sh);
			financeDetail.getFinScheduleData().getDisbursementDetails().add(dd);
			FinScheduleData sh1 = ScheduleGenerator.getNewSchd(financeDetail.getFinScheduleData());

			// calculate schedule
			FinScheduleData sh2 = ScheduleCalculator.getCalSchd(sh1);


			// Change rate by adding margin for the review period jan,feb,mar'11
			String baseRate = "L1";
			String splRate = "S1";
			BigDecimal mrgRate = new BigDecimal(1.0);

			BigDecimal actRate = new BigDecimal(0.0);
			sh2.getFinanceMain().setEventFromDate(
					DateUtility.getDate("31/12/2011"));
			sh2.getFinanceMain().setEventToDate(
					DateUtility.getDate("30/06/2012"));
			sh2.getFinanceMain().setRecalType("CURPRD");
			sh2 = ScheduleCalculator.changeRate(sh1, baseRate, splRate,
					mrgRate, actRate, true);

			// File file = new
			// File(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Schedule.xls"));
			File file = new File(getFile());
			FileWriter txt;
			txt = new FileWriter(file);
			PrintWriter out = new PrintWriter(txt);

			out.print("Date \t CPZ \t PFT \t RVW \t RPY \tRate\t DisAmount \t DWPAY   \t OPBAL   \t CLOSBAL \t"
					+ " NODAYS \t DAYSFACTOR \t ProfitCalc \t ProfitPaid \t PrincipalPaid \t RepayAmount \t ProfitBalance \tCpzAmount "
					+ "\t ProfitFraction \t SchdMethod");

			int sdSize = sh2.getFinanceScheduleDetails().size();
			for (int i = 0; i < sh2.getFinanceScheduleDetails().size(); i++) {
				FinanceScheduleDetail sd = sh2.getFinanceScheduleDetails().get(
						i);
				out.write("\n" + sd.getSchDate() + "  \t  "
						+ yesrno(sd.isCpzOnSchDate()) + "  \t  "
						+ yesrno(sd.isPftOnSchDate()) + "  \t  "
						+ yesrno(sd.isRvwOnSchDate()) + "  \t  "
						+ yesrno(sd.isRepayOnSchDate()) + "  \t  "
						+ (sd.getActRate()) + " \t  " + (sd.getDisbAmount())
						+ " \t  " + (sd.getDownPaymentAmount()) + "    \t  "
						+ (sd.getBalanceForPftCal()) + "\t   "
						+ (sd.getClosingBalance()) + "  \t  "
						+ sd.getNoOfDays() + "  \t  " + (sd.getDayFactor())
						+ "  \t  " + (sd.getProfitCalc()) + "  \t  "
						+ (sd.getProfitSchd()) + "  \t  "
						+ (sd.getPrincipalSchd()) + "  \t  "
						+ (sd.getRepayAmount()) + "  \t  "
						+ (sd.getProfitBalance()) + "  \t  "
						+ (sd.getCpzAmount()) + "  \t  "
						+ (sd.getProfitFraction()) + " \t"
						+ (sd.getSchdMethod()));

				resultedTotPft = resultedTotPft.add(sd.getProfitCalc());

				if (i == (sdSize - 1)
						&& sd.getRepayAmount().equals(expectedResult)
						&& resultedTotPft.equals(expectedTotPft)) {
					isSuccess = true;
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

}
