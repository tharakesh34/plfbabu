package com.pennant.gnp.adddefer_adjmdt.AccrualTest;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pennant.TestingUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceDetailService;

public class EffectiveRateTestCase extends TestingUtil {
	private static boolean isSuccess = false;

	private static String getFile() {
		return getFileLoc() + EffectiveRateTestCase.class.getSimpleName() + ".xls";
	}
	
	static FinanceDetailService detailService;

	public static FinanceDetailService getDetailService() {
		return detailService;
	}

	public static void setDetailService(FinanceDetailService detailService) {
		EffectiveRateTestCase.detailService = detailService;
	}

	public static boolean RunTestCase(ApplicationContext mainContext) {
		
		try {

			// Tesing Code
			setDetailService((FinanceDetailService)mainContext.getBean("financeDetailService"));

			List<String> finRefList = getDetailService().getFinanceReferenceList();//new ArrayList<String>();
			//finRefList.add("10100007");
			File file = new File(getFile());
			FileWriter txt;
			txt = new FileWriter(file);
			PrintWriter out = new PrintWriter(txt);

			out.print(" WHEN \t FinReference \t LastRepayDate \t EffectiveRateOfReturn \t ");

			FinanceMainDAO financeMainDAO = (FinanceMainDAO) mainContext.getBean("financeMainDAO");

			for (String finReference : finRefList) {

				FinScheduleData data = getDetailService().getFinSchDataByFinRef(finReference, "_AView",0);

				data.getFinanceMain().setEventFromDate(data.getFinanceMain().getFinStartDate());
				data.getFinanceMain().setEventToDate(data.getFinanceMain().getMaturityDate());

				FinScheduleData scheduleData = new FinScheduleData();
				scheduleData = ScheduleCalculator.getCalERR(data);

				//Last Repay Date Calculation
				Date lastRepayDate = scheduleData.getFinanceMain().getFinStartDate();
				Date lastRepayPftDate = scheduleData.getFinanceMain().getFinStartDate();
				List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
				for (FinanceScheduleDetail curSchd : list) {

					if(!curSchd.isRepayOnSchDate()){
						continue;
					}

					if(curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0){
						lastRepayDate = curSchd.getSchDate();
						lastRepayPftDate = curSchd.getSchDate();
						continue;
					}

					if(curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0){
						lastRepayDate = curSchd.getSchDate();
						continue;
					}
				}

				//Finance Effective Rate ERR Updation in DB
				financeMainDAO.updateFinanceERR(scheduleData.getFinanceMain().getFinReference(), lastRepayDate, lastRepayPftDate,
						scheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate(), "");

				//EffectiveRate Calculation Data Store in Excel
				out.write("\n"  + "CAL  \t" + scheduleData.getFinanceMain().getFinReference()+  "\t" + scheduleData.getFinanceMain().getLastRepayDate()+ 
						"\t" + scheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate()+ " \t " );

				isSuccess = true;
			}

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccess;
	}


}
