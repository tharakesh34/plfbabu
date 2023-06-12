package com.pennanttech.pff.eod.step;

import com.pennanttech.pff.batch.model.BatchProcessStatus;

public class StepUtil {
	public enum Step {
		microEOD, microEODMonitor, beforeEOD, loanCancel, prepareCustomerQueue, masterStep, processInActiveFinances,
		assetClassification, effAssetClassification, provisionCalc, processINDASForInActiveFinances,
		prepareCustomerGroupQueue, limitCustomerGroupsUpdate, institutionLimitUpdate, limitsUpdate,
		manualAdvisesCancellation, datesUpdate, snapShotPreparation, ledgerDownLoad, ledgerNotification, gstDownload,
		collectionDataDownLoad, collectionNotification, loadCollateralRevaluationData, collateralRevaluation,
		endOfMonthDecider, retailcibil, corporatecibil, prepareIncomeAMZDetails, autoKnockOffProcess,
		notifyLoanClosureDetailsToEFS, customerDataPreperation, masterDataPreparation, prepareAmortizationQueue,
		financeHoldRelease, autoWriteOffCalc, excessKnockOff, excessKnockOffLoading, letterGeneration, autoRefund
	}

	public static final BatchProcessStatus COLLATERAL_REVALUATION = new BatchProcessStatus("COLLATERAL_REVALUATION");
	public static final BatchProcessStatus BEFORE_EOD = new BatchProcessStatus("BEFORE_EOD");
	public static final BatchProcessStatus AUTO_CANCELLATION = new BatchProcessStatus("AUTO_CANCELLATION");
	public static final BatchProcessStatus COLLECTION_DOWNLOAD = new BatchProcessStatus("COLLECTION_DOWNLOAD");
	public static final BatchProcessStatus COLLECTION_NOTIFICATION = new BatchProcessStatus("COLLECTION_NOTIFICATION");
	public static final BatchProcessStatus CIBIL_EXTRACT_CORPORATE = new BatchProcessStatus("CIBIL_EXTRACT_CORPORATE");
	public static final BatchProcessStatus CIBIL_EXTRACT_RETAIL = new BatchProcessStatus("CIBIL_EXTRACT_RETAIL");
	public static final BatchProcessStatus DATES_UPDATE = new BatchProcessStatus("DATES_UPDATE");
	public static final BatchProcessStatus GST_DOWNLOAD = new BatchProcessStatus("GST_DOWNLOAD");
	public static final BatchProcessStatus LEDGER_DOWNLOAD = new BatchProcessStatus("LEDGER_DOWNLOAD");
	public static final BatchProcessStatus LEDGER_NOTIFICATION = new BatchProcessStatus("LEDGER_NOTIFICATION");
	public static final BatchProcessStatus CUSTOMER_GROUP_LIMITS_UPDATE = new BatchProcessStatus(
			"CUSTOMER_GROUP_LIMITS_UPDATE");
	public static final BatchProcessStatus CUSTOMER_LIMITS_UPDATE = new BatchProcessStatus("CUSTOMER_LIMITS_UPDATE");
	public static final BatchProcessStatus INSTITUTION_LIMITS_UPDATE = new BatchProcessStatus(
			"INSTITUTION_LIMITS_UPDATE");
	public static final BatchProcessStatus PREPARE_CUSTOMER_QUEUE = new BatchProcessStatus("PREPARE_CUSTOMER_QUEUE");
	public static final BatchProcessStatus PREPARE_CUSTOMER_GROUP_QUEUE = new BatchProcessStatus(
			"PREPARE_CUSTOMER_GROUP_QUEUE");
	public static final BatchProcessStatus PROCESS_INACTIVE_FINANCES = new BatchProcessStatus(
			"PROCESS_INACTIVE_FINANCES");
	public static final BatchProcessStatus SNAPSHOT_PREPARATION = new BatchProcessStatus("SNAPSHOT_PREPARATION");

	public static final BatchProcessStatus PREPARE_INCOME_AMZ_DETAILS = new BatchProcessStatus(
			"PREPARE_INCOME_AMZ_DETAILS");
	public static final BatchProcessStatus PREPARE_AMORTIZATION_QUEUE = new BatchProcessStatus(
			"PREPARE_AMORTIZATION_QUEUE");

	public static final BatchProcessStatus AUTO_KNOCKOFF_PROCESS = new BatchProcessStatus("AUTO_KNOCKOFF_PROCESS");

	public static final BatchProcessStatus PROCESS_INDAS_INACTIVE_FINANCES = new BatchProcessStatus(
			"PROCESS_INDAS_INACTIVE_FINANCES");

	public static final BatchProcessStatus CANCEL_INACTIVE_FINANCES_ADVISES = new BatchProcessStatus(
			"CANCEL_INACTIVE_FINANCES_ADVISES");

	public static final BatchProcessStatus LOAN_CLOSURE_DETAILS = new BatchProcessStatus("LOAN_CLOSURE_DETAILS");

	public static final BatchProcessStatus NPA_CLASSIFICATION = new BatchProcessStatus("NPA_CLASSIFICATION");

	public static final BatchProcessStatus EFF_NPA_CLASSIFICATION = new BatchProcessStatus("EFF_NPA_CLASSIFICATION");

	public static final BatchProcessStatus PROVISION_CALC = new BatchProcessStatus("PROVISION_CALC");

	public static final BatchProcessStatus OTS = new BatchProcessStatus("OTS");

	public static final BatchProcessStatus LETTER_GENERATION = new BatchProcessStatus("LETTER_GENERATION");

	public static final BatchProcessStatus AUTO_WRITE_OFF = new BatchProcessStatus("AUTO_WRITE_OFF");

	public static final BatchProcessStatus CROSS_LOAN_KNOCKOFF = new BatchProcessStatus("CROSS_LOAN_KNOCKOFF");

	public static final BatchProcessStatus FIN_HOLD_RELEASE = new BatchProcessStatus("FIN_HOLD_RELEASE");

	public static final BatchProcessStatus AUTO_REFUND_PROCESS = new BatchProcessStatus("AUTO_REFUND_PROCESS");

	private StepUtil() {
		super();
	}
}
