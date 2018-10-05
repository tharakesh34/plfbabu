package com.pennant.datamigration.process;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.model.SourceDataSummary;
import com.pennant.datamigration.service.DMTransactionFetch;
import com.pennant.datamigration.service.DMTransactionService;

public class DataMigrationProcess {
	private static boolean isSuccess = true;
	private static DMTransactionService detailService;
	private static DMTransactionFetch fetchService;

	public static boolean processFinances(ApplicationContext mainContext) {

		Date sysDate1 = new Date();

		try {
			setDetailService((DMTransactionService) mainContext.getBean("dmFinanceDetailService"));
			setFetchService((DMTransactionFetch) mainContext.getBean("dmFinanceDetailFetch"));

			String typeMain = "";
			String typeStage2 = "_STG2";
			int count = 0;
			boolean isVerify = false;
			boolean printTime = true;
			
			Date sysDateI1 = new Date();

			getFetchService().cleanDestination();
			ReferenceID rid = new ReferenceID();
			rid.setFinTypes(getFetchService().getFinTypeList(typeStage2));
			rid.setFeeVsGLList(getFetchService().getFeeVsGLList());
			Date sysDateI2 = new Date();
			
			if (printTime) {
				System.out.println("Time for Clean Destination Tables in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
			}

			// Fetching List of all Finance Reference Details
			sysDateI1 = new Date();
			List<String> finRefList = getDetailService().getFinanceReferenceList(typeStage2);
			sysDateI2 = new Date();
			
			if (printTime) {
				System.out.println("Time for List Fetch in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
			}


			sysDate1 = new Date();
			printTime = false;
			for (String finReference : finRefList) {
				count = count + 1;

				String printMsg = "Fetch for ".concat(finReference).concat(" Started.").concat(String.valueOf(count));
				System.out.println(printMsg);
				
				//*****FOR DEBUG*//
				//if (!StringUtils.equals(finReference, "SEPNMLE0117889")) { continue; }

				sysDateI1 = new Date();
				MigrationData sMD = getFetchService().getFinanceDetailsFromSource(finReference, rid, typeStage2);
				sysDateI2 = new Date();
				if (printTime) {
					System.out
							.println("Time for Data Fetch in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
				}

				if (isVerify) {
					SourceDataSummary sds = getFetchService().setSourceSummary(sMD, typeStage2);
				}

				sysDateI1 = new Date();
				MigrationData dMD = getDetailService().getFinanceDetails(sMD, rid, typeStage2);
				sysDateI2 = new Date();
				if (printTime) {
					System.out.println(
							"Time for Calculation in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
				}

				sysDateI1 = new Date();
				getDetailService().saveFinanceDetails(dMD);
				sysDateI2 = new Date();
				if (printTime) {
					System.out.println("Time for Saving in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}

		Date sysDate2 = new Date();
		System.out.println("Start AT: " + sysDate1.toString());
		System.out.println("Ends AT: " + sysDate2.toString());

		System.out.println("Total Time in Seconds: " + (sysDate2.getTime() - sysDate1.getTime()) / 1000);

		return isSuccess;
	}

	public static DMTransactionService getDetailService() {
		return detailService;
	}

	public static void setDetailService(DMTransactionService detailService) {
		DataMigrationProcess.detailService = detailService;
	}

	private static void doProcess(FinScheduleData financeDetails) {
		FinanceMain finMain = financeDetails.getFinanceMain();
		finMain.setFinRemarks("Testing");
	}

	public static DMTransactionFetch getFetchService() {
		return fetchService;
	}

	public static void setFetchService(DMTransactionFetch fetchService) {
		DataMigrationProcess.fetchService = fetchService;
	}

}
