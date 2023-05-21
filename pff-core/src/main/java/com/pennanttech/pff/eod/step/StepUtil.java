package com.pennanttech.pff.eod.step;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class StepUtil {
	public enum Step {
		microEOD, microEODMonitor, beforeEOD, loanCancel, prepareCustomerQueue, masterStep, processInActiveFinances,
		assetClassification, effAssetClassification, provisionCalc, processINDASForInActiveFinances,
		prepareCustomerGroupQueue, limitCustomerGroupsUpdate, institutionLimitUpdate, limitsUpdate,
		manualAdvisesCancellation, datesUpdate, snapShotPreparation, ledgerDownLoad, ledgerNotification, gstDownload,
		collectionDataDownLoad, collectionNotification, loadCollateralRevaluationData, collateralRevaluation,
		endOfMonthDecider, retailcibil, corporatecibil, prepareIncomeAMZDetails, autoKnockOffProcess,
		notifyLoanClosureDetailsToEFS, customerDataPreperation, masterDataPreparation, prepareAmortizationQueue,
		financeHoldRelease, autoRefundExecution, autoWriteOffCalc, excessKnockOff, excessKnockOffLoading,
		crossLoanKnockOff, letterGeneration,
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

	public static final DataEngineStatus PROCESS_INDAS_INACTIVE_FINANCES = new DataEngineStatus(
			"PROCESS_INDAS_INACTIVE_FINANCES");

	public static final DataEngineStatus CANCEL_INACTIVE_FINANCES_ADVISES = new DataEngineStatus(
			"CANCEL_INACTIVE_FINANCES_ADVISES");

	public static final DataEngineStatus LOAN_CLOSURE_DETAILS = new DataEngineStatus("LOAN_CLOSURE_DETAILS");

	public static final DataEngineStatus NPA_CLASSIFICATION = new DataEngineStatus("NPA_CLASSIFICATION");

	public static final DataEngineStatus OTS = new DataEngineStatus("OTS");

	public static final DataEngineStatus EFF_NPA_CLASSIFICATION = new DataEngineStatus("EFF_NPA_CLASSIFICATION");

	public static final DataEngineStatus AUTOWRITEOFF_CALC = new DataEngineStatus("AUTOWRITEOFF_CALC");

	public static final DataEngineStatus PROVISION_CALC = new DataEngineStatus("PROVISION_CALC");

	public static final DataEngineStatus FIN_HOLD_RELEASE = new DataEngineStatus("FIN_HOLD_RELEASE");

	public static final DataEngineStatus AUTO_REFUND_PROCESS = new DataEngineStatus("AUTO_REFUND_PROCESS");

	public static final DataEngineStatus CROSS_LOAN_KNOCKOFF = new DataEngineStatus("CROSS_LOAN_KNOCKOFF");

	public static final DataEngineStatus LETTER_GENERATION = new DataEngineStatus("LETTER_GENERATION");

	private StepUtil() {
		super();
	}
}
