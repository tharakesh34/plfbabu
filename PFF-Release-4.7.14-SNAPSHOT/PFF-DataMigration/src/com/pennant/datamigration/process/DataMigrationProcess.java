package com.pennant.datamigration.process;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.datamigration.service.DMTransactionService;

public class DataMigrationProcess {
	private static boolean isSuccess = false;
	private static DMTransactionService detailService;

	public static DMTransactionService getDetailService() {
		return detailService;
	}
	public static void setDetailService(DMTransactionService detailService) {
		DataMigrationProcess.detailService = detailService;
	}
	
	
	public static boolean processFinances(ApplicationContext mainContext) {
		try {

			setDetailService((DMTransactionService)mainContext.getBean("dmFinanceDetailService"));
			
			//Fetching List of all Finance Reference Details
			List<String> finRefList = getDetailService().getFinanceReferenceList();
			
			for (String finReference : finRefList) {
				FinScheduleData financeDetails = getDetailService().getFinanceDetails(finReference, "");

				// Process Finances
				doProcess(financeDetails);

				// Update Finances
				getDetailService().updateFinanceDetails(financeDetails, "");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccess;
	}
	
	
	private static void doProcess(FinScheduleData financeDetails){
		FinanceMain finMain = financeDetails.getFinanceMain();
		finMain.setFinRemarks("Testing");
	}
	
}
