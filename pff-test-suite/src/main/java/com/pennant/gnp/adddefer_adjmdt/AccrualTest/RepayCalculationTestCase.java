/*package com.pennant.gnp.adddefer_adjmdt.AccrualTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.context.ApplicationContext;

import com.pennant.TestingUtil;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.service.finance.FinanceDetailService;


public class RepayCalculationTestCase extends TestingUtil {
	private static boolean isSuccess = false;
	
	private static String getFile() {
		return getFileLoc() + RepayCalculationTestCase.class.getName() + ".xls";
	}
	
	private static 

	public static boolean RunTestCase(ApplicationContext mainContext) {
		try {

			// Tesing Code
			setDetailService((FinanceDetailService)mainContext.getBean("repayCalculator"));
			FinScheduleData data = getDetailService().getFinSchDataByFinRef("SUSPRLS_001", "_View");

			// Profit Details Fill
			Date curBD = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
			
			Date monthEnd = curBD;
	
			FinanceProfitDetailDAO profitDetailsDAO = (FinanceProfitDetailDAO) mainContext.getBean("profitDetailsDAO");
			FinanceProfitDetail fpd = new FinanceProfitDetail();
			
			File file = new File(getFile());
			FileWriter txt;
			txt = new FileWriter(file);
			PrintWriter out = new PrintWriter(txt);

			out.print(" WHEN \t finReference \t lastMdfDate \t totalPftSchd \t totalPftCpz \t totalPftPaid \t totalPftBal \t totalPftPaidInAdv \t totalPriPaid \t totalPriBal \t tdSchdPft \t tdPftCpz \t tdSchdPftPaid \t tdSchdPftBal \t tdPftAccrued \t tdPftAccrueSusp \t tdPftAmortized \t tdPftAmortizedSusp \t tdSchdPri \t tdSchdPriPaid \t tdSchdPriBal \t acrTillLBD \t acrTillNBD \t acrTodayToNBD \t amzTillNBD \t amzTillLBD \t amzTodayToNBD \t ");
			
			for (int i = 0; i < 1; i++) {
				fpd = profitDetailsDAO.getFinProfitDetailsById(data.getFinanceMain().getFinReference());
				
				if (curBD.compareTo(monthEnd)==0) {
					data.getFinanceScheduleDetails().get(1).setSchdPftPaid(data.getFinanceScheduleDetails().get(1).getProfitSchd());
					data.getFinanceScheduleDetails().get(1).setSchdPriPaid(data.getFinanceScheduleDetails().get(1).getPrincipalSchd());
				}
				
				AEAmounts aeAmounts = new AEAmounts();
				AEAmountCodes aeAmountCodes = aeAmounts.procAEAmounts(data.getFinanceMain(), data.getFinanceScheduleDetails(), fpd, curBD);
				
				// UPDATE After Calculation
				fpd = setFD(aeAmountCodes, fpd, curBD);
				profitDetailsDAO.update(fpd);
				out.write("\n"  + "CAL \t " + fpd.getFinReference() + " \t "  + fpd.getLastMdfDate() + " \t "  + fpd.getTotalPftSchd() + " \t "  + fpd.getTotalPftCpz() + " \t "  + fpd.getTotalPftPaid() + " \t "  + fpd.getTotalPftBal() + " \t "  + fpd.getTotalPftPaidInAdv() + " \t "  + fpd.getTotalPriPaid() + " \t "  + fpd.getTotalPriBal() + " \t "  + fpd.getTdSchdPft() + " \t "  + fpd.getTdPftCpz() + " \t "  + fpd.getTdSchdPftPaid() + " \t "  + fpd.getTdSchdPftBal() + " \t "  + fpd.getTdPftAccrued() + " \t "  + fpd.getTdPftAccrueSusp() + " \t "  + fpd.getTdPftAmortized() + " \t "  + fpd.getTdPftAmortizedSusp() + " \t "  + fpd.getTdSchdPri() + " \t "  + fpd.getTdSchdPriPaid() + " \t "  + fpd.getTdSchdPriBal() + " \t "  + fpd.getAcrTillLBD() + " \t "  + fpd.getAcrTillNBD() + " \t "  + fpd.getAcrTodayToNBD() + " \t "  + fpd.getAmzTillNBD() + " \t "  + fpd.getAmzTillLBD() + " \t "  + fpd.getAmzTodayToNBD() + " \t " );
				
				// UPDATE After Posting
				fpd = setPostFD(aeAmountCodes, fpd, curBD);
				profitDetailsDAO.update(fpd);
				out.write("\n"  + "POST \t " + fpd.getFinReference() + " \t "  + fpd.getLastMdfDate() + " \t "  + fpd.getTotalPftSchd() + " \t "  + fpd.getTotalPftCpz() + " \t "  + fpd.getTotalPftPaid() + " \t "  + fpd.getTotalPftBal() + " \t "  + fpd.getTotalPftPaidInAdv() + " \t "  + fpd.getTotalPriPaid() + " \t "  + fpd.getTotalPriBal() + " \t "  + fpd.getTdSchdPft() + " \t "  + fpd.getTdPftCpz() + " \t "  + fpd.getTdSchdPftPaid() + " \t "  + fpd.getTdSchdPftBal() + " \t "  + fpd.getTdPftAccrued() + " \t "  + fpd.getTdPftAccrueSusp() + " \t "  + fpd.getTdPftAmortized() + " \t "  + fpd.getTdPftAmortizedSusp() + " \t "  + fpd.getTdSchdPri() + " \t "  + fpd.getTdSchdPriPaid() + " \t "  + fpd.getTdSchdPriBal() + " \t "  + fpd.getAcrTillLBD() + " \t "  + fpd.getAcrTillNBD() + " \t "  + fpd.getAcrTodayToNBD() + " \t "  + fpd.getAmzTillNBD() + " \t "  + fpd.getAmzTillLBD() + " \t "  + fpd.getAmzTodayToNBD() + " \t " );
				
				curBD = DateUtility.addDays(curBD, 1);
			}
			
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return isSuccess;
	}
	
	private static FinanceProfitDetail setFD(AEAmountCodes aeAmountCodes,
			FinanceProfitDetail fpd, Date curBD) {
		
		fpd.setLastMdfDate(curBD);
		fpd.setTotalPftSchd(aeAmountCodes.getPft());
		fpd.setTotalPftCpz(aeAmountCodes.getCpzTot());
		fpd.setTotalPftPaid(aeAmountCodes.getPftAP());
		fpd.setTotalPftBal(aeAmountCodes.getPftAB());
		if (aeAmountCodes.getPftSP().compareTo(aeAmountCodes.getPftS()) > 0) {
			fpd.setTotalPftPaidInAdv(aeAmountCodes.getPftSP()
					.subtract(aeAmountCodes.getPftS()));
		} else {
			fpd.setTotalPftPaidInAdv(BigDecimal.ZERO);
		}

		fpd.setTotalPriPaid(aeAmountCodes.getPriAP());
		fpd.setTotalPriBal(aeAmountCodes.getPriAB());
		fpd.setTdSchdPft(aeAmountCodes.getPftS());
		fpd.setTdPftCpz(aeAmountCodes.getCpzPrv());
		fpd.setTdSchdPftPaid(aeAmountCodes.getPftSP());
		fpd.setTdSchdPftBal(aeAmountCodes.getPftSB());
		fpd.setTdPftAccrued(aeAmountCodes.getAccrue()); 
		fpd.setTdPftAccrueSusp(aeAmountCodes.getAccrueS());
		fpd.setTdPftAmortized(aeAmountCodes.getAmz()); 
		fpd.setTdPftAccrueSusp(aeAmountCodes.getAmzS());
	
		fpd.setTdSchdPri(aeAmountCodes.getPriS());
		fpd.setTdSchdPriPaid(aeAmountCodes.getPriSP());
		fpd.setTdSchdPriBal(aeAmountCodes.getPriSB());
		
		fpd.setAcrTillNBD(aeAmountCodes.getNAccrue());
		fpd.setAcrTodayToNBD(aeAmountCodes.getDAccrue());
		
		//AMORTIZATION FIELDS
		fpd.setAmzTillNBD(aeAmountCodes.getnAmz());
		fpd.setAmzTodayToNBD(aeAmountCodes.getdAmz());
		
		return fpd;
	}
	
private static  FinanceProfitDetail setPostFD(AEAmountCodes aeAmountCodes, FinanceProfitDetail fpd, Date curBD) {
		fpd.setAcrTillLBD(fpd.getTdPftAccrued()); 
		fpd.setAcrTodayToNBD(BigDecimal.ZERO);
		
		fpd.setAmzTillLBD(fpd.getAmzTillNBD());
		fpd.setAmzTodayToNBD(BigDecimal.ZERO);
		return fpd;
	}

}
*/