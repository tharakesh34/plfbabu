package com.pennanttech.pff.eod.step;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class StepUtil {
	public enum Step {
		microEOD,
		microEODMonitor,
		beforeEOD,
		loanCancel,
		prepareCustomerQueue,
		masterStep,
		processInActiveFinances,
		prepareCustomerGroupQueue,
		limitCustomerGroupsUpdate,
		institutionLimitUpdate,
		limitsUpdate,
		datesUpdate,
		snapShotPreparation,
		ledgerDownLoad,
		ledgerNotification,
		gstDownload,
		collectionDataDownLoad,
		collectionNotification,
		loadCollateralRevaluationData,
		collateralRevaluation,
		endOfMonthDecider,
		retailcibil,
		corporatecibil,
		prepareIncomeAMZDetails,
		autoKnockOffProcess
	}

	public static final DataEngineStatus COLLATERAL_REVALUATION = new DataEngineStatus("COLLATERAL_REVALUATION");
	public static final DataEngineStatus BEFORE_EOD = new DataEngineStatus("BEFORE_EOD");
	public static final DataEngineStatus AUTO_CANCELLATION = new DataEngineStatus("AUTO_CANCELLATION");
	public static final DataEngineStatus COLLECTION_DOWNLOAD = new DataEngineStatus("COLLECTION_DOWNLOAD");
	public static final DataEngineStatus COLLECTION_NOTIFICATION = new DataEngineStatus("COLLECTION_NOTIFICATION");
	public static final DataEngineStatus CIBIL_EXTRACT_CORPORATE = new DataEngineStatus("CIBIL_EXTRACT_CORPORATE");
	public static final DataEngineStatus CIBIL_EXTRACT_RETAIL = new DataEngineStatus("CIBIL_EXTRACT_RETAIL");
	public static final DataEngineStatus DATES_UPDATE = new DataEngineStatus("DATES_UPDATE");
	public static final DataEngineStatus GST_DOWNLOAD = new DataEngineStatus("GST_DOWNLOAD");
	public static final DataEngineStatus LEDGER_DOWNLOAD = new DataEngineStatus("LEDGER_DOWNLOAD");
	public static final DataEngineStatus LEDGER_NOTIFICATION = new DataEngineStatus("LEDGER_NOTIFICATION");
	public static final DataEngineStatus CUSTOMER_GROUP_LIMITS_UPDATE = new DataEngineStatus(
			"CUSTOMER_GROUP_LIMITS_UPDATE");
	public static final DataEngineStatus CUSTOMER_LIMITS_UPDATE = new DataEngineStatus("CUSTOMER_LIMITS_UPDATE");
	public static final DataEngineStatus INSTITUTION_LIMITS_UPDATE = new DataEngineStatus("INSTITUTION_LIMITS_UPDATE");
	public static final DataEngineStatus PREPARE_CUSTOMER_QUEUE = new DataEngineStatus("PREPARE_CUSTOMER_QUEUE");
	public static final DataEngineStatus PREPARE_CUSTOMER_GROUP_QUEUE = new DataEngineStatus(
			"PREPARE_CUSTOMER_GROUP_QUEUE");
	public static final DataEngineStatus PROCESS_INACTIVE_FINANCES = new DataEngineStatus("PROCESS_INACTIVE_FINANCES");
	public static final DataEngineStatus SNAPSHOT_PREPARATION = new DataEngineStatus("SNAPSHOT_PREPARATION");

	public static final DataEngineStatus PREPARE_INCOME_AMZ_DETAILS = new DataEngineStatus(
			"PREPARE_INCOME_AMZ_DETAILS");
	public static final DataEngineStatus PREPARE_AMORTIZATION_QUEUE = new DataEngineStatus(
			"PREPARE_AMORTIZATION_QUEUE");

	public static final DataEngineStatus AUTO_KNOCKOFF_PROCESS = new DataEngineStatus("AUTO_KNOCKOFF_PROCESS");

	private StepUtil() {
		super();
	}
}
