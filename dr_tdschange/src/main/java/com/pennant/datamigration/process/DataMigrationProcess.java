package com.pennant.datamigration.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.IRRCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.datamigration.dao.DRFinanceDetailsDAO;
import com.pennant.datamigration.model.DREMIHoliday;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.service.DMTransactionFetch;
import com.pennant.datamigration.service.DMTransactionService;
import com.pennanttech.pennapps.core.App;

public class DataMigrationProcess {
	private static boolean isSuccess;
	private static DMTransactionService detailService;
	private static DRFinanceDetailsDAO drFinanceDetailsDAO;
	private static DMTransactionFetch fetchService;

	private static DataSourceTransactionManager transManager;
	private static DefaultTransactionDefinition transDef;

	static {
		DataMigrationProcess.isSuccess = false;
	}

	public static DMTransactionFetch getFetchService() {
		return DataMigrationProcess.fetchService;
	}

	public static void setFetchService(final DMTransactionFetch fetchService) {
		DataMigrationProcess.fetchService = fetchService;
	}

	public static DMTransactionService getDetailService() {
		return DataMigrationProcess.detailService;
	}

	public static void setDetailService(final DMTransactionService detailService) {
		DataMigrationProcess.detailService = detailService;
	}

	public static void setDrFinanceDetailsDAO(final DRFinanceDetailsDAO drFinanceDetailsDAO) {
		DataMigrationProcess.drFinanceDetailsDAO = drFinanceDetailsDAO;
	}

	public static boolean processFinances(final ApplicationContext mainContext) {
		final String correctionType = "EMIHLD";
		try {
			setDataSource((DataSource) mainContext.getBean("pfsDatasource"));
			setDetailService((DMTransactionService) mainContext.getBean("dmFinanceDetailService"));
			setDrFinanceDetailsDAO((DRFinanceDetailsDAO) mainContext.getBean("drFinanceDetailsDAO"));
			IRRCalculator.setiRRFeeTypeDAO((IRRFeeTypeDAO) mainContext.getBean("iRRFeeTypeDAO"));
			IRRCalculator.setIrrFinanceTypeDAO((IRRFinanceTypeDAO) mainContext.getBean("irrFinanceTypeDAO"));
			setFetchService((DMTransactionFetch) mainContext.getBean("fetchService"));

			if (StringUtils.equals(correctionType, "EMIHLD")) {
				return processEMIHoliday();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataMigrationProcess.isSuccess;
	}

	public static boolean processEMIHoliday() throws Exception {
		Date appDate = SysParamUtil.getAppDate();
		System.out.println("START: Program On : " + appDate);

		ReferenceID rid = new ReferenceID();
		List<Exception> exceptions = new ArrayList<Exception>(1);

		String roundAdjMth = SysParamUtil.getValueAsString("ROUND_ADJ_METHOD");
		int valueToadd = SysParamUtil.getValueAsInt("ACCRUAL_CAL_ON");

		rid.setAppDate(appDate);
		rid.setRoundAdjMth(roundAdjMth);
		rid.setValueToadd(valueToadd);
		rid.setMonthStart(DateUtility.getMonthStart(appDate));

		int count = 0;
		System.out.println("Started At: " + new Date().getTime());

		List<DREMIHoliday> drEHList = detailService.getDREHListList();

		for (final DREMIHoliday drEH : drEHList) {
			try {
				++count;
				final String finID = drEH.getFinReference();
				System.out.println("Count - " + count + " : " + finID + " - Start ");
				rid.setMiscDate1(drEH.getEHStartDate());
				
				/*if(!StringUtils.equals(finID, "PL000004543")){
					continue;
				}*/

				// Data Preparation for the EMI Holiday Requested Reference
				MigrationData sMD = DataMigrationProcess.fetchService.getEHFinanceDetails(finID, rid);

				sMD.setDrEH(drEH);

				TransactionStatus txnStatus = transManager.getTransaction(transDef);
				try {
					DataMigrationProcess.detailService.procEHSchedule(sMD, rid);
					transManager.commit(txnStatus);
				} catch (Exception e) {
					e.printStackTrace();
					if (txnStatus != null) {
						transManager.rollback(txnStatus);
					}
					drEH.setEHStatus("F");
					String message = StringUtils.trimToEmpty(e.getMessage());

					if (message.length() > 49) {
						message = message.substring(0, 49);
					}
					drEH.setEhStatusRemarks(message);
					e.printStackTrace();
					drFinanceDetailsDAO.updateDREMIHoliday(drEH);
				}

			} catch (Exception e) {
				logError(e);
				exceptions.add(e);
			}

		}
		if (!exceptions.isEmpty()) {
			final Exception exception = new Exception(exceptions.get(0));
			exceptions.clear();
			exceptions = null;
			throw exception;
		}
		System.out.println("COMPLETE: " + new Date().getTime());
		return true;
	}

	private static void logError(final Exception exp) {
		System.out.println("Cause : " + exp.getCause());
		System.out.println("Message : " + exp.getMessage());
		System.out.println("LocalizedMessage : " + exp.getLocalizedMessage());
	}

	public static void setDataSource(DataSource dataSource) {
		DataMigrationProcess.transManager = new DataSourceTransactionManager(dataSource);
		DataMigrationProcess.transDef = new DefaultTransactionDefinition();
		DataMigrationProcess.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		DataMigrationProcess.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		DataMigrationProcess.transDef.setTimeout(180);
	}

}