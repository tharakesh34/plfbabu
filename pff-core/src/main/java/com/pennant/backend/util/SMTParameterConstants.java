/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * 
 * FileName : PennantConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.util;

/**
 * This stores all constants required for running the application
 */
public class SMTParameterConstants {
	private SMTParameterConstants() {
		super();
	}

	public static final String SUSP_CHECK_REQ = "SUSP_CHECK_REQ";
	public static final String EOD_THREAD_COUNT = "EOD_THREAD_COUNT";
	public static final String IGNORING_BUCKET = "IGNORING_BUCKET";
	public static final String EOD_CHUNK_SIZE = "EOD_CHUNK_SIZE";
	public static final String ACCRUAL_CAL_ON = "ACCRUAL_CAL_ON";

	/**
	 * This constant will be used whether the accrula calculation is based on the effective date or vale date. If the
	 * values is Y then Effective date will be consider otherwise Value date will be consider
	 * 
	 */
	public static final String ACC_EFF_VALDATE = "ACC_EFF_VALDATE";
	public static final String ACCREV_EFF_POSTDATE = "ACCREV_EFF_POSTDATE";
	public static final String ROUND_LASTSCHD = "ROUND_LASTSCHD";
	public static final String KYC_PRIORITY = "DEFAULT_KYC_PRIORITY";
	public static final String ID_PANCARD = "PAN_DOC_TYPE";
	public static final String DEFAULT_KYC_PRIORITY = "DEFAULT_KYC_PRIORITY";
	public static final int PRESENTATION_HOLD_DAYS = 2;
	public static final String ALMEXTRACT_FROMFINSTARTDATE = "ALMEXTRACT_FROMFINSTARTDATE";
	public static final String PANCARD_REQ = "PAN_REQ";
	public static final String LOAN_REF_FORMAT = "LOAN_REF_FORMAT";
	public static final String LOAN_REF_PREFIX = "LOAN_REF_PREFIX";
	public static final String FEESALLOWZERO = "FEESALLOWZERO";
	public static final String ELGMETHOD = "ELGMETHOD";
	public static final String ALW_DIFF_RPYHCY_NPA = "ALW_DIFF_RPYHCY_NPA";
	public static final String DISB_PAID_STATUS = "DISB_PAID_STATUS";
	public static final String IS_CREDITREVIEW_TAB_REQ = "IS_CREDITREVIEW_TAB_REQ";
	public static final String PRESENTMENT_RESPONSE_ROW_LENGTH = "PRESENTMENT_RESPONSE_ROW_LENGTH";
	public static final String NUMBEROF_UNDATED_CHEQUES = "NUMBEROF_UNDATED_CHEQUES";
	public static final String NUMBEROF_PDC_CHEQUES = "NUMBEROF_PDC_CHEQUES";
	public static final String QRY_MGMT_TEMPLATE = "QRY_MGMT_TEMPLATE";
	public static final String DISB_ALLOW_WITH_OTC_UNDER_COVENANT_FULLFILL = "COVENANT DETAILS REQUIRED";
	public static final String BAJAJFINANCE_STARTDATE = "1/1/2017";
	public static final String CUSTBRANCH = "CUSTBRANCH";
	public static final String BANKINFO_DAYS = "BANKINFO_DAYS";
	public static final String BANKINFO_MONTH_YEAR = "BANKINFO_MONTH_YEAR";
	public static final String MONTHLY_INCOME_REQ = "MONTHLY_INCOME_REQ";
	public static final String EARLYPAY_FY_STARTMONTH = "EARLYPAY_FY_STARTMONTH";
	public static final String ALWD_EARLYPAY_PERC_BYYEAR = "ALWD_EARLYPAY_PERC_BYYEAR";
	public static final String LEGAL_DETAILS_DISPLAY_INACTIVE_RECORDS = "LEGAL_DETAILS_DISPLAY_INACTIVE_RECORDS";
	public static final String PRESENTMENT_RESPONSE_ALLOW_INSTRUMENT_TYPE = "PRESENTMENT_RESPONSE_ALLOW_INSTRUMENT_TYPE";
	public static final String DISBURSEMENT_RESPONSE_ALLOW_PAYMENT_TYPE = "DISBURSEMENT_RESPONSE_ALLOW_PAYMENT_TYPE";
	public static final String CUST_CHANGE_ROLES = "CUST_CHANGE_ROLES";
	public static final String CREDITREVIEW_TAB = "CREDITREVIEW_TAB";
	public static final String CUST_EXT_DEVIATIONS = "CUST_EXT_DEVIATIONS";

	/**
	 * NO_ADJ = No Adjustment, leave the Profit Fraction ADJ_LAST_INST = All Profit Fractions adjust to last schedule
	 * ADJ_NEXT_INST = Profit Fraction adjust to immediate next schedule
	 * 
	 */
	public static final String ROUND_ADJ_METHOD = "ROUND_ADJ_METHOD";

	/**
	 * This constant will be use to configure the maximum OD days allowed to add additional disbursement.
	 */
	public static final String MAX_ODDAYS_ADDDISB = "MAX_ODDAYS_ADDDISB";

	/**
	 * This constant will be used to configure the number of days to auto cancel the in case of disbursement not yet
	 * added
	 */
	public static final String MAX_DAYS_FIN_AUTO_REJECT = "MAX_DAYS_FIN_AUTO_REJECT";

	public static final String ADD_DISB_DUES_WARNG = "ADD_DISB_DUES_WARNG";
	public static final String LMS_SERVICE_LOG_REQ = "LMS_SERVICE_LOG_REQ";
	public static final String ALLOW_LOWER_TAX_DED_REQ = "ALLOW_LOWER_TAX_DED_REQ";
	public static final String ALLOW_OD_TAX_DED_REQ = "ALLOW_OD_TAX_DED_REQ";

	public static final String UDC_ALLOW_ZERO_AMT = "UDC_ALLOW_ZERO_AMT";
	public static final String GROUP_BATCH_BY_BANK = "GROUP_BATCH_BY_BANK";

	/**
	 * BPI on deduct from disbursement is incomized or Receivable
	 */
	public static final String BPI_INCOMIZED_ON_ORG = "BPI_INCOMIZED_ON_ORG";

	// BPI on Deduct from disbursement is Paid on Up-front in Origination or On
	// Installment Due date
	public static final String BPI_PAID_ON_INSTDATE = "BPI_PAID_ON_INSTDATE";
	public static final String BPI_TDS_DEDUCT_ON_ORG = "BPI_TDS_DEDUCT_ON_ORG";
	public static final String BPI_IMPACT_ON_IRR = "BPI_IMPACT_ON_IRR";

	// On Advance Interest TDS Incomize Required Upfront
	public static final String ADVANCE_TDS_INCZ_UPF = "ADVINT_TDS_INCZ_UPF";

	// Auto Build Schedule after Loan Start Date has changed
	public static final String ALW_AUTO_SCHD_BUILD = "ALW_AUTO_SCHD_BUILD";

	public static final String ALLOW_LEGAL_REQ_CHANGE = "ALLOW_LEGAL_REQ_CHANGE";

	// If True, Document Modification can be done by only document owner
	public static final String DOC_OWNER_VALIDATION = "DOC_OWNER_VALIDATION";

	// If True, Makes Frequency Dates to empty on loan startdate change
	public static final String CLEAR_FREQUENCY_DATES_ON_STARTDATE_CHANGE = "CLEAR_FREQUENCY_DATES_ON_STARTDATE_CHANGE";

	// If True, Makes Frequency Dates to empty for change gestation period
	public static final String CHANGE_GESTATION_PERIOD_CLEAR_FREQUENCY_DATES = "CHANGE_GESTATION_PERIOD_CLEAR_FREQUENCY_DATES";

	public static final String NEW_COVENANT_MODULE = "NEW_COVENANT_MODULE";

	// Upfront Fee reversal required on loan cancellation
	public static final String UPFRONT_FEE_REVERSAL_REQ = "UPFRONT_FEE_REVERSAL_REQ";

	// GST Invoice Due basis/Receipt Basis
	public static final String INVOICE_ADDRESS_ENTITY_BASIS = "INVOICE_ADDRESS_ENTITY_BASIS";
	// Added ReEnter Account Number in Disbursement at Loan Approval stage
	public static final String DISB_ACCNO_MASKING = "DISB_ACCNO_MASKING";

	// The below variable is set for Branch screen in Entity Masters for Masters
	// menu.
	public static final String ALLOW_ORGANISATIONAL_STRUCTURE = "ALLOW_ORGANISATIONAL_STRUCTURE";
	public static final String ALLOW_DIVISION_BASED_CLUSTER = "ALLOW_DIVISION_BASED_CLUSTER";
	public static final String DOC_SPLIT_SCREEN_REQ = "DOC_SPLIT_SCREEN_REQ";
	public static final String BR_INRST_RVW_FRQ_FRQDAYVAL_REQ = "BR_INRST_RVW_FRQ_FRQDAYVAL_REQ";
	public static final String ALLOW_BR_INRST_RVW_FRQ_FRQCODEVAL_REQ = "ALLOW_BR_INRST_RVW_FRQ_FRQCODEVAL_REQ";
	public static final String HUNTER_REQ = "HUNTER_REQUIRED";

	// COMMITMENTS is Required or Not
	public static final String ALLOW_COMMITMENTS = "ALLOW_COMMITMENTS";

	public static final String CALCULATE_GST_ON_GSTRATE_MASTER = "CALCULATE_GST_ON_GSTRATE_MASTER";

	public static final String VAN_REQUIRED = "VAN_REQUIRED";
	/**
	 * Atleast One Document is Mandatory For Field Investigation
	 */
	public static final String FI_DOCUMENT_MANDATORY = "FI_DOCUMENT_MANDATORY";

	public static final String VERIFICATION_RCU_EYEBALLED_VALUE = "VERIFICATION_RCU_EYEBALLED_VALUE";

	/**
	 * Parameter code to specify the number of days to lock the user account, when the user is not logging into the
	 * application.
	 */
	public static final String USR_ACCT_LOCK_DAYS = "USR_ACCT_LOCK_DAYS";

	/**
	 * Parameter code to specify the number of days to disable the user account, when the new user is not logging into
	 * the application.
	 */
	public static final String USR_DISABLE_DAYS_NEW = "USR_DISABLE_DAYS_NEW";

	/**
	 * Parameter code to specify the number of days to disable the user account, when the existing user is not logging
	 * into the application.
	 */
	public static final String USR_DISABLE_DAYS_EXISTING = "USR_DISABLE_DAYS_EXISTING";

	/**
	 * Parameter code to allow the job to run for locking the user
	 */
	public static final String ALLOW_USR_LOCKING_JOB = "ALLOW_USR_LOCKING_JOB";

	/**
	 * Parameter code to allow the job to run for disabling the user
	 */
	public static final String ALLOW_USR_DISABLE_JOB = "ALLOW_USR_DISABLE_JOB";

	/**
	 * Parameter code to specify the cron expression for Security user account locking job.
	 */
	public static final String USR_ACCT_LOCK_CRON_EXPRESSION = "USR_ACCT_LOCK_CRON_EXPRESSION";
	public static final String ALLOW_PROFIT_WAIVER = "ALLOW_PROFIT_WAIVER"; // Profit
																			// Waiver
																			// in
																			// Fee
																			// Waivers

	// Put-call Default value need or not
	public static final String PUTCALL_DEFAULT_ALERTDAYS_REQUIRED = "PUTCALL_DEFAULT_ALERTDAYS_REQUIRED";

	// SOA cancel receipt details
	public static final String SOA_SHOW_CANCEL_RECEIPT = "SOA_SHOW_CANCEL_RECEIPT";

	public static final String PD_DOCUMENT_MANDATORY = "PD_DOCUMENT_MANDATORY";
	public static final String RCU_DOCUMENT_MANDATORY = "RCU_DOCUMENT_MANDATORY";
	public static final String TV_DOCUMENT_MANDATORY = "TV_DOCUMENT_MANDATORY";
	public static final String LV_DOCUMENT_MANDATORY = "LV_DOCUMENT_MANDATORY";

	// Closure Maker field validation
	public static final String RECEIPT_CASH_PAN_MANDATORY = "RECEIPT_CASH_PAN_MANDATORY";
	public static final String HOLD_INS_INST_POST = "HOLD_INS_INST_POST";// LMS

	public static final String RECEIPTUPLOAD_DEDUPCHECK = "RECEIPTUPLOAD_DEDUPCHECK";

	public static final String CUST_CARD_SALES_REQ = "CUST_CARD_SALES_REQ";
	public static final String LIMIT_ADDTNAL_FIELDS_REQ = "LIMIT_ADDTNAL_FIELDS_REQ";
	public static final String CUST_PAN_VALIDATION = "CUST_PAN_VALIDATION";
	public static final String CUSTOM_DEVIATION_FILE_PATH = "CUSTOM_DEVIATION_FILE_PATH";
	public static final String ALLOW_CIBIL_REQUEST = "ALLOW_CIBIL_REQUEST";
	public static final String VERIFICATIONS_CUSTOMERVIEW = "VERIFICATIONS_CUSTOMERVIEW";

	public static final String BPI_MONTHWISE_REQ = "BPI_MONTHWISE_REQ";

	public static final String COAPP_CUST_CREATE = "COAPP_CUST_CREATE";

	public static final String EXCLUDE_SUB_RECEIPT_MODE_VALUE = "EXCLUDE_SUB_RECEIPT_MODE_VALUE";
	public static final String ALLOW_BACK_DATED_ADD_RATE_CHANGE = "ALLOW_BACK_DATED_ADD_RATE_CHANGE";
	public static final String ALLOW_INCLUDE_FROMDATE_ADD_RATE_CHANGE = "ALLOW_INCLUDE_FROMDATE_ADD_RATE_CHANGE";
	public static final String ALLOW_EOD_SNAPSHOT = "ALLOW_EOD_SNAPSHOT";
	public static final String ALLOW_LIMIT_NOTIFICATION = "ALLOW_LIMIT_NOTIFICATION";
	public static final String COMMITE_ADDTNAL_FIELDS_REQ = "COMMITE_ADDTNAL_FIELDS_REQ";
	public static final String DEVIATION_APPROVAL_FOR_SAMEROLE = "DEVIATION_APPROVAL_FOR_SAMEROLE";
	public static final String MANUAL_DEVIATIONS_TRIGGERING_FOR_SAMEROLE = "MANUAL_DEVIATIONS_TRIGGERING_FOR_SAMEROLE";
	public static final String ALLOW_INTERNAL_SETTLEMENTS = "ALLOW_INTERNAL_SETTLEMENTS";
	public static final String ALLOW_DOCUMENTTYPE_XLS_REQ = "ALLOW_DOCUMENTTYPE_XLS_REQ";
	public static final String ALLOWED_BACKDATED_RECEIPT = "ALLOWED_BACKDATED_RECEIPT";
	public static final String ALLOW_PUSH_NOTIFICATION = "ALLOW_PUSH_NOTIFICATION";

	public static final String DMS_REQ = "DMS_REQ";
	public static final String ALLOW_PAN_VALIDATION_RULE = "ALLOW_PAN_VALIDATION_RULE";
	public static final String DMS_DOCURI_REQ = "DMS_DOCURI_REQ";
	public static final String DISB_REQUEST_REQUIRED = "DISB_REQ_REQUIRED";
	public static final String DOMAIN_CHEQ = "DOMAIN_CHEQ";
	public static final String FIN_SUMMARY_TAB_REQUIRED = "FIN_SUMMARY_TAB_REQUIRED";
	public static final String CET_BUSINESS = "CET_BUSINESS";
	public static final String CET_PL = "CET_PL";
	public static final String CET_LAEP = "CET_LAEP";
	public static final String ALLOW_CIBIL_VALIDATION_RULE = "ALLOW_CIBIL_VALIDATION_RULE";
	public static final String INSURANCE_CAL_REQUEST_URL = "INSURANCE_CAL_REQUEST_URL";
	public static final String CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ = "CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ";
	public static final String MANDATE_DOWNLOAD_STOP_CIF_VALIDATION = "MANDATE_DOWNLOAD_STOP_CIF_VALIDATION";
	// For Verification tabs in Loan Queue
	public static final String EXTERNAL_DOCUMENT_VERIFICATION_REQUIRED = "EXT_DOC_VER_REQ";
	public static final String FINANCE_DEVIATION_CHECK = "FINANCE_DEVIATION_CHECK";
	public static final String QUERY_NOTIFICATION_REQ = "QUERY_NOTIFICATION_REQ";
	public static final String GST_DEFAULT_FROM_STATE = "GST_DEFAULT_FROM_STATE";
	public static final String GST_DEFAULT_STATE_CODE = "GST_DEFAULT_STATE_CODE";
	public static final String ALW_SCH_RECAL_LOCK = "ALW_SCH_RECAL_LOCK";
	public static final String SET_POSTDATE_TO = "SET_POSTDATE_TO";
	public static final String ACCRUAL_REVERSAL_REQ = "ACCRUAL_REVERSAL_REQ";
	public static final String ALLOW_MANDATE_ACCT_DET_READONLY = "ALLOW_MANDATE_ACCT_DET_READONLY";
	public static final String ALLOW_FEE_CALC_ADJU_PRINCIPAL = "ALLOW_FEE_CALC_ADJU_PRINCIPAL";
	public static final String CUST_LASTNAME_MANDATORY = "CUST_LASTNAME_MANDATORY";
	public static final String VERIFICATION_CATEGORY_REQUIRED = "VERIFICATION_CATEGORY_REQUIRED";
	public static final String DISB_DOWNLOAD_JOB_TIME = "DISB_DOWNLOAD_JOB_TIME";
	// If TRUE Capitalize amount will not be capitalized to POS amount. It will
	// be used only for interest calculation
	public static final String CPZ_POS_INTACT = "CPZ_POS_INTACT";
	public static final String BEN_ACTNAME_LENGTH = "BEN_ACTNAME_LENGTH";
	// TDS reversal entried required or not in SOA.
	public static final String DISPLAY_TDS_REV_SOA = "DISPLAY_TDS_REV_SOA";
	public static final String ALLOW_FEE_WAIVER_IN_FORECLOSURE_ENQ = "ALLOW_FEE_WAIVER_IN_FORECLOSURE_ENQ";
	public static final String RPYHCY_ON_DPD_BUCKET = "RPYHCY_ON_DPD_BUCKET";
	public static final String ALLOW_FRQ_TERMS_VALIDATION = "ALLOW_FRQ_TERMS_VALIDATION";

	public static final String CHECK_USER_ACCESS_AUTHORITY = "CHECK_USER_ACCESS_AUTHORITY";

	// FIXME HL SMT Parameters
	public static final String CREDIT_ELG_PARAMS = "CREDIT_ELG_PARAMS";
	public static final String VER_RCU_VALIDITY_DAYS = "VER_RCU_VALIDITY_DAYS";
	public static final String VER_FI_VALIDITY_DAYS = "FI_VERIFICATION_VALIDITY_DAYS";
	public static final String GST_DETAILS_TAB_REQUIRED_FOR_RETAIL = "GST_DETAILS_TAB_REQUIRED_FOR_RETAIL";

	public static final String ALLOW_DEFAULT_MANDATE_REQ = "ALLOW_DEFAULT_MANDATE_REQ";
	public static final String USER_NOTIFICATION_PUBLISH = "USER_NOTIFICATION_PUBLISH";
	public static final String ALLOW_GST_RETAIL_CUSTOMER = "ALLOW_GST_RETAIL_CUSTOMER";
	public static final String MANDATE_EMANDATE_REQUIRED = "MANDATE_EMANDATE_REQUIRED";

	public static final String CD_CASHBACK_JOB_REQUIRED = "CD_CASHBACK_JOB_REQUIRED";
	public static final String CD_CASHBACK_CRON_EXPRESSION = "CD_CASHBACK_CRON_EXPRESSION";

	// Disbursement postings reversal required on loan cancellation
	public static final String DISB_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL = "DISB_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL";

	public static final String DISBURSEMENT_PROCESS_JOB_ENABLED = "DISBURSEMENT_PROCESS_JOB_ENABLED";
	public static final String DISBURSEMENT_PROCESS_JOB_FREQUENCY = "DISBURSEMENT_PROCESS_JOB_FREQUENCY";

	public static final String DISBURSEMENT_AUTO_DOWNLOAD = "DISBURSEMENT_AUTO_DOWNLOAD";
	public static final String DISBURSEMENT_AUTO_DOWNLOAD_JOB_ENABLED = "DISBURSEMENT_AUTO_DOWNLOAD_JOB_ENABLED";
	public static final String DISBURSEMENT_AUTO_DOWNLOAD_JOB_FREQUENCY = "DISBURSEMENT_AUTO_DOWNLOAD_JOB_FREQUENCY";

	public static final String DISBURSEMENT_AUTO_UPLOAD = "DISBURSEMENT_AUTO_UPLOAD";
	public static final String DISBURSEMENT_AUTO_UPLOAD_JOB_ENABLED = "DISBURSEMENT_AUTO_UPLOAD_JOB_ENABLED";
	public static final String DISBURSEMENT_AUTO_UPLOAD_JOB_FREQUENCY = "DISBURSEMENT_AUTO_UPLOAD_JOB_FREQUENCY";

	public static final String MANDATE_AUTO_UPLOAD = "MANDATE_AUTO_UPLOAD";
	public static final String MANDATE_AUTO_UPLOAD_JOB_ENABLED = "MANDATE_AUTO_UPLOAD_JOB_ENABLED";
	public static final String MANDATE_AUTO_UPLOAD_JOB_FREQUENCY = "MANDATE_AUTO_UPLOAD_JOB_FREQUENCY";

	public static final String MANDATE_AUTO_DOWNLOAD = "MANDATE_AUTO_DOWNLOAD";
	public static final String MANDATE_AUTO_DOWNLOAD_JOB_ENABLED = "MANDATE_AUTO_DOWNLOAD_JOB_ENABLED";
	public static final String MANDATE_AUTO_DOWNLOAD_JOB_FREQUENCY = "MANDATE_AUTO_DOWNLOAD_JOB_FREQUENCY";

	public static final String MANDATE_REGISTRATION_STATUS = "MANDATE_REGISTRATION_STATUS";
	public static final String MANDATE_AUTO_UPLOAD_ACK_JOB_ENABLED = "MANDATE_AUTO_UPLOAD_ACK_JOB_ENABLED";
	public static final String MANDATE_AUTO_UPLOAD_ACK_JOB_FREQUENCY = "MANDATE_AUTO_UPLOAD_ACK_JOB_FREQUENCY";

	public static final String PRESENTMENT_AUTO_DOWNLOAD = "PRESENTMENT_AUTO_DOWNLOAD";
	public static final String PRESENTMENT_AUTO_EXTRACT_JOB_ENABLED = "PRESENTMENT_AUTO_EXTRACT_JOB_ENABLED";
	public static final String PRESENTMENT_AUTO_EXTRACT_JOB_FREQUENCY = "PRESENTMENT_AUTO_EXTRACT_JOB_FREQUENCY";

	public static final String PRESENTMENT_AUTO_UPLOAD = "PRESENTMENT_AUTO_UPLOAD";
	public static final String PRESENTMENT_NACH_AUTO_UPLOAD_JOB_ENABLED = "PRESENTMENT_NACH_AUTO_UPLOAD_JOB_ENABLED";
	public static final String PRESENTMENT_NACH_AUTO_UPLOAD_JOB_FREQUENCY = "PRESENTMENT_NACH_AUTO_UPLOAD_JOB_FREQUENCY";

	public static final String PRESENTMENT_PDC_AUTO_UPLOAD_JOB_ENABLED = "PRESENTMENT_PDC_AUTO_UPLOAD_JOB_ENABLED";
	public static final String PRESENTMENT_PDC_AUTO_UPLOAD_JOB_FREQUENCY = "PRESENTMENT_PDC_AUTO_UPLOAD_JOB_FREQUENCY";

	public static final String PRESENTMENT_NACH_DATE_FREQUENCY = "PRESENTMENT_NACH_DATE_FREQUENCY";
	public static final String PRESENTMENT_PDC_DATE_FREQUENCY = "PRESENTMENT_PDC_DATE_FREQUENCY";

	public static final String ALLOW_EOD_START_ON_SAME_DAY = "ALLOW_EOD_START_ON_SAME_DAY";
	public static final String ALLOW_MULITIPLE_EODS_ON_SAME_DAY = "ALLOW_MULITIPLE_EODS_ON_SAME_DAY";
	public static final String AUTO_KNOCKOFF_THRESHOLD = "AUTO_KNOCKOFF_THRESHOLD";

	// Parameter to configure the loan types for provisional certificate
	public static final String PROVCERT_LOANTYPES = "PROVCERT_LOANTYPES";

	// Allow Instruction Based Schedule
	public static final String IS_INST_BASED_SCHD_REQ = "IS_INST_BASED_SCHD_REQ";

	// External BRE credit review details
	public static final String EXTCREDITREVIEW_TAB = "EXTCREDITREVIEW_TAB";
	public static final String PDF_OWNER_PASSWORD = "PDF_OWNER_PASSWORD";
	public static final String PDF_PASSWORD_FORMAT = "PDF_PASSWORD_FORMAT";

	public static final String NPA_TAGGING = "NPA_TAGGING";
	public static final String RPYHCY_ON_NPA = "RPYHCY_ON_NPA";
	public static final String PROVISION_BOOKS = "PROVISION_BOOKS";
	public static final String PROVISION_EFF_POSTDATE = "PROVISION_EFF_POSTDATE";

	// TODO:GANESH need to Remove once provide the data in Finadvpayments
	public static final String INSURANCE_INST_ON_INSPAYINST = "INSURANCE_INST_ON_INSPAYINST";
	public static final String ALW_ALGOFUSION_DATA_EXTRACTION = "ALW_ALGOFUSION_DATA_EXTRACTION";
	public static final String CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION = "CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION";

	public static final String PURG_CONFIG_JOB_REQUIRED = "PURG_CONFIG_JOB_REQUIRED";
	public static final String PURG_CONFIG_CRON_EXPRESSION = "PURG_CONFIG_CRON_EXPRESSION";

	// FORECLOSURE_VALIDITY_DAYS
	// To define number of calendar days that foreclosure letter will be valid from the date of generation.
	public static final String FORECLOSURE_VALIDITY_DAYS = "FORECLOSURE_VALIDITY_DAYS";
	// Update the Manual Cheque Receipt status as paid at Deposit approver
	public static final String CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER = "CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER";
	public static final String EOD_INTERVAL_TIME = "EOD_INTERVAL_TIME";
	public static final String EOD_SKIP_LATE_PAY_MARKING = "EOD_SKIP_LATE_PAY_MARKING";
	public static final String GST_INV_ON_DUE = "GST_INV_ON_DUE";
	public static final String ALLOW_ZERO_POSTINGS = "ALLOW_ZERO_POSTINGS";
	public static final String PROVISION_RULE = "PROVISION_RULE";
	public static final String EOM_ON_EOD = "EOM_ON_EOD";
	public static final String MONTHENDACC_FROMFINSTARTDATE = "MONTHENDACC_FROMFINSTARTDATE";
	public static final String DPD_CALC_INCLUDE_EXCESS = "DPD_CALC_INCLUDE_EXCESS";
	public static final String ALW_PROV_EOD = "ALW_PROV_EOD";
	public static final String ENTITY_CODE = "ENTITYCODE";

	public static final String RECEIPT_UPLOAD_RECORD_DEFAULT_SIZE = "RECEIPT_UPLOAD_RECORD_DEFAULT_SIZE";
	public static final String RECEIPT_UPLOAD_THREAD_SIZE = "RECEIPT_UPLOAD_THREAD_SIZE";
	public static final String RECEIPT_UPLOAD_THREAD_BATCH_SIZE = "RECEIPT_UPLOAD_THREAD_BATCH_SIZE";

	public static final String ALW_CASHFLOW_REPORT = "ALW_CASHFLOW_REPORT";
	public static final String TRANSACTIONREF_TXT_IN_RECEIPT = "TRANSACTIONREF_TXT_IN_RECEIPT";
	public static final String REPAY_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL = "REPAY_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL";
	public static final String CHECK_COLL_MAINTENANCE = "CHECK_COLL_MAINTENANCE";
	public static final String CIBIL_DEFAULT_AMOUNT = "CIBIL_DEFAULT_AMOUNT";
	public static final String FEE_LOS_PROCESSING = "FEE_LOS_PROCESSING";
	public static final String PRESENTMENT_RESP_PROCESS_THREAD_COUNT = "PRESENTMENT_RESP_PROCESS_THREAD_COUNT";
	public static final String PRESENTMENT_EXTRACTION_TYPE = "PRESENTMENT_EXTRACTION_TYPE";
	public static final String CUSTOMER_CORP_FINANCE_TAB_REQ = "CUSTOMER_CORP_FINANCE_TAB_REQ";
	public static final String CALC_EFFRATE_ON_XIRR = "CALC_EFFRATE_ON_XIRR";
	public static final String EARLYSETTLE_CHQ_DFT_DAYS = "EARLYSETTLE_CHQ_DFT_DAYS";
	public static final String RECEIPT_CASH_PAN_LIMIT = "RECEIPT_CASH_PAN_LIMIT";
	public static final String EARLYSETTLE_CHQ_CLR_DAYS = "EARLYSETTLE_CHQ_CLR_DAYS";
	public static final String ALW_SP_BACK_DAYS = "ALW_SP_BACK_DAYS";
	public static final String STEP_LOAN_SERVICING_REQ = "STEP_LOAN_SERVICING_REQ";
	public static final String TDS_ASSESSMENT_YEAR = "TDS_ASSESSMENT_YEAR";
	public static final String UPLOAD_FILEPATH = "UPLOAD_FILEPATH";
	public static final String BENEFICIARY_ACCOUNT_VALIDATION_REQ = "BENEFICIARY_ACCOUNT_VALIDATION_REQ";
	public static final String OVERDRAFT_LOANS_MONTHLY_LIMIT = "OVERDRAFT_LOANS_MONTHLY_LIMIT";
	public static final String RESET_FREQUENCY_DATES_REQ = "RESET_FREQUENCY_DATES_REQ";
	public static final String ALW_CONST_PRINCIPLE_SCHD_METHOD = "ALW_CONST_PRINCIPLE_SCHD_METHOD";

	public static final String LOAN_START_DATE_BACK_DAYS = "LOAN_START_DATE_BACK_DAYS";
	public static final String LOAN_START_DATE_FUTURE_DAYS = "LOAN_START_DATE_FUTURE_DAYS";
	public static final String FEE_POSTING_DATE_BACK_DAYS = "FEE_POSTING_DATE_BACK_DAYS";
	public static final String RATE_CHANGE_FROM_DATE_BACK_DAYS = "RATE_CHANGE_FROM_DATE_BACK_DAYS";
	public static final String USR_PWD_MAX_LEN = "USR_PWD_MAX_LEN";
	public static final String USR_PWD_MIN_LEN = "USR_PWD_MIN_LEN";
	public static final String USR_PWD_SPECIAL_CHAR_COUNT = "USR_PWD_SPECIAL_CHAR_COUNT";
	public static final String USR_EXPIRY_DAYS = "USR_EXPIRY_DAYS";
	public static final String OTP_SENDTO_MAIL = "OTP_SENDTO_MAIL";
	public static final String EXTERNAL_CUSTOMER_DEDUP = "EXTERNAL_CUSTOMER_DEDUP";
	public static final String APP_DFT_END_DATE = "APP_DFT_END_DATE";
	public static final String MANDATE_STARTDATE = "MANDATE_STARTDATE";
	public static final String BANK_CODE = "BANK_CODE";
	public static final String BOUNCE_CODES_FOR_ACCOUNT_CLOSED = "BOUNCE_CODES_FOR_ACCOUNT_CLOSED";
	public static final String PRESENTMENT_RESPONSE_THREAD_COUNT = "PRESENTMENT_RESPONSE_THREAD_COUNT";
	public static final String PRESENTMENT_EXTRACTION_THREAD_COUNT = "PRESENTMENT_EXTRACTION_THREAD_COUNT";

	// Refund Module Constants
	public static final String AUTO_REFUND_N_DAYS_CLOSED_LAN = "AUTO_REFUND_N_DAYS_CLOSED_LAN";
	public static final String AUTO_REFUND_N_DAYS_ACTIVE_LAN = "AUTO_REFUND_N_DAYS_ACTIVE_LAN";
	public static final String REMOVE_HOLD_FLAG_N_DAYS_CLOSED_LAN = "REMOVE_HOLD_FLAG_N_DAYS_CLOSED_LAN";
	public static final String AUTO_REFUND_THROUGH_CHEQUE = "AUTO_REFUND_THROUGH_CHEQUE";
	public static final String AUTO_REFUND_HOLD_DPD = "HOLD_AUTO_REFUND_DPD";
	public static final String AUTO_REFUND_OVERDUE_CHECK = "CHECK_OVERDUE_AUTO_REFUND";
	public static final String REFUND_UPLOAD_THREAD_COUNT = "REFUND_UPLOAD_THREAD_COUNT";

	public static final String PAYMENT_INSTRUCTION_DD_PAYABLE_LOCATION = "PAYMENT_INSTRUCTION_DD_PAYABLE_LOCATION";
	public static final String PAYMENT_INSTRUCTION_CHEQUE_PAYABLE_LOCATION = "PAYMENT_INSTRUCTION_CHEQUE_PAYABLE_LOCATION";
	public static final String ALW_OTS_ON_EOD = "ALW_OTS_ON_EOD";
	public static final String RECEIPTS_SHOW_ACCOUNTING_TAB = "RECEIPTS_SHOW_ACCOUNTING_TAB";
	public static final String OTS_THREAD_COUNT = "OTS_THREAD_COUNT";
	public static final String ALW_CREDIT_EDIT_DATA_STAGES = "ALW_CREDIT_EDIT_DATA_STAGES";
	public static final String DPD_STRING_CALCULATION_ON = "DPD_STRING_CALCULATION_ON";
	public static final String MAINTAIN_CANFIN_BACK_DATE = "MAINTAIN_CANFIN_BACK_DATE";
}