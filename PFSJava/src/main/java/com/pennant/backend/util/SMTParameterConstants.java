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
	public static final String ACCRUAL_CAL_ON = "ACCRUAL_CAL_ON";//0-eod, 1-sod
	public static final String ACC_EFF_VALDATE = "ACC_EFF_VALDATE";// Y - Accrual Effective Date will be Value Date, N - Accrual Effective Date will be APP Date
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

	/**
	 * NO_ADJ = No Adjustment, leave the Profit Fraction ADJ_LAST_INST = All Profit Fractions adjust to last schedule
	 * ADJ_NEXT_INST = Profit Fraction adjust to immediate next schedule
	 * 
	 */
	public static final String ROUND_ADJ_METHOD = "ROUND_ADJ_METHOD";
	
	public static final String MAX_ODDAYS_ADDDISB = "MAX_ODDAYS_ADDDISB";//Max od days for add disbursement.
	public static final String MAX_DAYS_FIN_AUTO_REJECT = "MAX_DAYS_FIN_AUTO_REJECT";//Cancel Loan for not adding disbursement.
	public static final String ADD_DISB_DUES_WARNG = "ADD_DISB_DUES_WARNG";// add disbursement warning
	// Amount field integral part default value requires zero or not. EX: If requires, value will be like 0.00, if not, value will be like .00 
	public static final String ALLOW_AMT_FLD_INTEGRAL_PART_DEF_VAL_ZERO = "ALLOW_AMT_FLD_INTEGRAL_PART_DEF_VAL_ZERO";
	public static final String LMS_SERVICE_LOG_REQ = "LMS_SERVICE_LOG_REQ";//LMS service log required or not
	public static final String ALLOW_LOWER_TAX_DED_REQ = "ALLOW_LOWER_TAX_DED_REQ";//LMS service log required or not
}
