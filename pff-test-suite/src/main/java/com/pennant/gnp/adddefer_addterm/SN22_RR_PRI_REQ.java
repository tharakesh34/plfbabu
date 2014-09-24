package com.pennant.gnp.adddefer_addterm;

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

public class SN22_RR_PRI_REQ extends TestingUtil {

	private static boolean isSuccess = false;
	private static BigDecimal expectedResult = new BigDecimal(9420442);
	private static BigDecimal expectedTotPft = new BigDecimal(9620442);
	private static BigDecimal expectedDefPri = new BigDecimal(8200000);
	private static BigDecimal expectedDefPft = BigDecimal.ZERO;
	private static BigDecimal zeroValue = BigDecimal.ZERO;
	private static BigDecimal resultedTotPft = BigDecimal.ZERO;
	private static BigDecimal resultDefPft = BigDecimal.ZERO;
	private static BigDecimal resultDefPri = BigDecimal.ZERO;

	private static String getFile() {
		return getFileLoc() + SN22_RR_PRI_REQ.class.getName() + ".xls";

	}

	public static boolean RunTestCase() {
		try {

			// Tesing Code
			FinanceMain sh = new FinanceMain();
			sh.setNumberOfTerms(12);
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
			sh.setMaturityDate(DateUtility.getDate("31/12/2012"));
			sh.setCpzAtGraceEnd(true);
			sh.setDownPayment(new BigDecimal(10000000));
			sh.setReqRepayAmount(new BigDecimal(8200000));
			sh.setTotalProfit(BigDecimal.ZERO);
			sh.setTotalGrossPft(BigDecimal.ZERO);
			sh.setGrcRateBasis("R");
			sh.setRepayRateBasis("R");
			sh.setScheduleMethod(CalculationConstants.PRI);
			sh.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
			sh.setCalculateRepay(false);
			sh.setEqualRepay(false);
			sh.setReqTerms(12);
			sh.setIncreaseTerms(false);
			sh.setEventFromDate(DateUtility.getDate("01/01/2011"));
			sh.setEventToDate(DateUtility.getDate("31/12/2012"));
			sh.setRecalType("CURPRD");
			sh.setGrcPeriodEndDate(DateUtility.getDate("31/12/2011"));
			sh.setAllowGrcRepay(false);
			// sh.setGrcSchdMthd(CalculationConstants.NOPAY);
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

			sh2.getFinanceMain().setEventFromDate(
					DateUtility.getDate("29/02/2012"));
			sh2.getFinanceMain().setEventToDate(
					DateUtility.getDate("29/02/2012"));
			sh2.getFinanceMain().setRecalType(
					CalculationConstants.RPYCHG_ADDTERM);
			sh2 = ScheduleCalculator.addDeferment(sh2);

			// File file = new
			// File(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Schedule.xls"));
			File file = new File(getFile());
			FileWriter txt;
			txt = new FileWriter(file);
			PrintWriter out = new PrintWriter(txt);

			out.print("Date \t CPZ \t PFT \t RVW \t RPY \tRate\t DisAmount \t DWPAY   \t OPBAL   \t CLOSBAL \t"
					+ " NODAYS \t ProfitCalc \t ProfitPaid \t PrincipalPaid \t RepayAmount \t ProfitBalance \tCpzAmount \tDefPriSchd\tDefPriSchd");

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
						+ sd.getNoOfDays() + "  \t  " + (sd.getProfitCalc())
						+ "  \t  " + (sd.getProfitSchd()) + "  \t  "
						+ (sd.getPrincipalSchd()) + "  \t  "
						+ (sd.getRepayAmount()) + "  \t  "
						+ (sd.getProfitBalance()) + "  \t  "
						+ (sd.getCpzAmount()) + "  \t  "
						+ (sd.getDefPrincipalSchd()) + "  \t  "
						+ (sd.getDefProfitSchd()) + "  \t  ");

				resultedTotPft = resultedTotPft.add(sd.getProfitCalc());
				resultDefPri = resultDefPri.add(sd.getDefPrincipalSchd());
				resultDefPft = resultDefPft.add(sd.getDefProfitSchd());

				if (i == (sdSize - 1) && resultedTotPft.equals(expectedTotPft)
						&& resultDefPri.equals(expectedDefPri)
						&& resultDefPft.equals(expectedDefPft)
						&& sd.getClosingBalance().equals(zeroValue)) {

					if (sh2.getFinanceScheduleDetails().get(sdSize - 2)
							.getRepayAmount().equals(expectedResult)) {
						isSuccess = true;
					}

				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

}
