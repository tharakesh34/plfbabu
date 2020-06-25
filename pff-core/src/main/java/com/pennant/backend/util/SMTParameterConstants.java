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
	public static final String RPYHCY_ON_NPA = "RPYHCY_ON_NPA";
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
	public static final String ALLOW_AMT_FLD_INTEGRAL_PART_DEF_VAL_ZERO = "ALLOW_AMT_FLD_INTEGRAL_PART_DEF_VAL_ZERO";
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

	// On Advance Interest TDS Incomize Required Upfront
	public static final String ADVANCE_TDS_INCZ_UPF = "ADVINT_TDS_INCZ_UPF";

	// Allow Partner Bank in Mandate based on this flag variable
	public static final String MANDATE_ALW_PARTNER_BANK = "MANDATE_ALW_PARTNER_BANK";

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
	public static final String HOLD_DISB_INST_POST = "HOLD_DISB_INST_POST";
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
	 * Parameter code to specify the cron expression for Security user account locking job.
	 */
	public static final String USR_ACCT_LOCK_CRON_EXPRESSION = "USR_ACCT_LOCK_CRON_EXPRESSION";
	public static final String ALLOW_PROFIT_WAIVER = "ALLOW_PROFIT_WAIVER"; // Profit
																			// Waiver
																			// in
																			// Fee
																			// Waivers

	public static final String INSURANCE_INST_ON_DISB = "INSURANCE_INST_ON_DISB";

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

	// Constants for advance Payment
	public static final String ADVANCE_PAYMENT_INT = "ADVANCE_PAYMENT_INT";
	public static final String ADVANCE_PAYMENT_EMI = "ADVANCE_PAYMENT_EMI";
	public static final String ADVANCE_PAYMENT_EMI_STAGE_FRONT_END = "ADVANCE_PAYMENT_EMI_STAGE_FRONT_END";
	public static final String ADVANCE_PAYMENT_EMI_STAGE_REARE_END = "ADVANCE_PAYMENT_EMI_STAGE_REARE_END";
	public static final String ADVANCE_PAYMENT_EMI_STAGE_REPAY_TERMS = "ADVANCE_PAYMENT_EMI_STAGE_REPAY_TERMS";
	public static final String RECEIPTUPLOAD_DEDUPCHECK = "RECEIPTUPLOAD_DEDUPCHECK";

	public static final String CUST_CARD_SALES_REQ = "CUST_CARD_SALES_REQ";
	public static final String LIMIT_ADDTNAL_FIELDS_REQ = "LIMIT_ADDTNAL_FIELDS_REQ";
	public static final String CUST_PAN_VALIDATION = "CUST_PAN_VALIDATION";
	public static final String CUSTOM_DEVIATION_FILE_PATH = "CUSTOM_DEVIATION_FILE_PATH";
	public static final String ALLOW_CIBIL_REQUEST = "ALLOW_CIBIL_REQUEST";
	public static final String VERIFICATIONS_CUSTOMERVIEW = "CLIX_VERIFICATIONS_CUSTOMERVIEW";

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
	public static final String CUST_DIALOG_EXT = "CUST_DIALOG_EXT";
	public static final String CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ = "CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ";
	public static final String MANDATE_DOWNLOAD_STOP_CIF_VALIDATION = "MANDATE_DOWNLOAD_STOP_CIF_VALIDATION";
	//For Verification tabs in Loan Queue
	public static final String VERIFICATION_INTIATION_FROM_OUTSIDE = "VERIFICATION_INTIATION_FROM_OUTSIDE";
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
	// If TRUE Capitalize amount will not be capitalized to POS amount. It will  be used only for interest calculation
	public static final String CPZ_POS_INTACT = "CPZ_POS_INTACT";
	public static final String BEN_ACTNAME_LENGTH = "BEN_ACTNAME_LENGTH";
	//TDS reversal entried required or not in SOA.
	public static final String DISPLAY_TDS_REV_SOA = "DISPLAY_TDS_REV_SOA";
	public static final String ALLOW_FEE_WAIVER_IN_FORECLOSURE_ENQ = "ALLOW_FEE_WAIVER_IN_FORECLOSURE_ENQ";
	public static final String RPYHCY_ON_DPD_BUCKET = "RPYHCY_ON_DPD_BUCKET";
	public static final String ALLOW_FRQ_TERMS_VALIDATION = "ALLOW_FRQ_TERMS_VALIDATION";

	public static final String CHECK_USER_ACCESS_AUTHORITY = "CHECK_USER_ACCESS_AUTHORITY";

	public static final String ALLOW_DEFAULT_MANDATE_REQ = "ALLOW_DEFAULT_MANDATE_REQ";

	public static final String LIST_RENDER_ON_LOAD = "LIST_RENDER_ON_LOAD";

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

	public static final String EOD_START_ON_SAMEDAY = "EOD_START_ON_SAMEDAY";

}
