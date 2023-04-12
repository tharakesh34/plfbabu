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

import java.math.BigDecimal;

/**
 * This stores all constants required for running the application
 */
public class FinanceConstants {
	private FinanceConstants() {
		super();
	}

	public static final String MODULE_NAME = "Finance";

	// Finance Product Codes
	public static final String PRODUCT_CONVENTIONAL = "CONV";
	public static final String PRODUCT_ODFACILITY = "ODFCLITY";
	public static final String PRODUCT_DISCOUNT = "DISCOUNT";
	public static final String PRODUCT_GOLD = "GOLD";
	public static final String PRODUCT_CD = "CD";
	public static final String PRODUCT_PL = "PL";

	public static final String PRODUCT_HYBRID_FLEXI = "HFLEXI";

	// Finance Division Details
	public static final String FIN_DIVISION_RETAIL = "BFSD";
	public static final String FIN_DIVISION_CORPORATE = "CL";
	public static final String FIN_DIVISION_COMMERCIAL = "COM";
	public static final String FIN_DIVISION_FACILITY = "FACILITY";

	// Process Editor Condition Constants
	public static final int PROCEDT_CHECKLIST = 1;
	public static final int PROCEDT_AGREEMENT = 2;
	public static final int PROCEDT_ELIGIBILITY = 3;
	public static final int PROCEDT_RTLSCORE = 4;
	public static final int PROCEDT_STAGEACC = 5;
	public static final int PROCEDT_TEMPLATE = 6;
	public static final int PROCEDT_CORPSCORE = 7;
	public static final int PROCEDT_FINDEDUP = 8;
	public static final int PROCEDT_BLACKLIST = 9;
	public static final int PROCEDT_CUSTDEDUP = 11;
	public static final int PROCEDT_LIMIT = 12;
	public static final int PROCEDT_TATNOTIFICATION = 13;
	public static final int PROCEDT_RETURNCHQ = 14;
	public static final int PROCEDT_FINANCETABS = 15;

	// Process Editor Stage Conditions
	public static final int PROCEDT_SHOWINSTAGE = 1;
	public static final int PROCEDT_ALWINPUTSTAGE = 2;
	public static final int PROCEDT_MANDINPUTSTAGE = 3;

	// Dedup Field SubCode Constants
	public static final String DEDUP_CUSTOMER = "Customer";
	public static final String DEDUP_FINANCE = "Finance";
	public static final String DEDUP_BLACKLIST = "BlackList";
	public static final String DEDUP_RETCHQ = "Cheque";
	public static final String DEDUP_LIMITS = "Limits";
	public static final String DEDUP_COLLATERAL = "Collateral";

	// Schedule Overdue Calculation Types
	public static final String ODCALON_SPRI = "SPRI";
	public static final String ODCALON_SPFT = "SPFT";
	public static final String ODCALON_STOT = "STOT";
	public static final String ODCALON_PIPD_FRQ = "PIPDF";
	public static final String ODCALON_PIPD_EOM = "PIPDM";
	// public static final String ODCALON_PIPD = "PIPD";

	// Schedule Apportionment Types
	public static final String PAY_APPORTIONMENT_SPRI = "SPRI";
	public static final String PAY_APPORTIONMENT_SPFT = "SPFT";
	public static final String PAY_APPORTIONMENT_STOT = "STOT";

	// Schedule Apportionment Types
	public static final String PAY_APPORTIONMENT_TO_NONE = "NONE";
	public static final String PAY_APPORTIONMENT_TO_PASTDUE = "PASTDUE";
	public static final String PAY_APPORTIONMENT_TO_SUSPENSE = "SUSPENSE";
	public static final String PAY_APPORTIONMENT_TO_ALL = "ALL";

	// Schedule Types
	public static final String SCH_TYPE_SCHEDULE = "S";
	public static final String SCH_TYPE_LATEPAYPROFIT = "I";

	// Finance Reference Generation Constants
	public static final String REF_DIVISION_RETAIL = "PB";
	public static final String REF_DIVISION_CORP = "WB";

	// Finance Status Reason Codes
	public static final String FINSTSRSN_SYSTEM = "S";
	public static final String FINSTSRSN_MANUAL = "M";
	public static final String FINSTSRSN_OTHER = "O";

	// Finance Queue Priority
	public static final String QUEUEPRIORITY_HIGH = "3";
	public static final String QUEUEPRIORITY_MEDIUM = "2";
	public static final String QUEUEPRIORITY_LOW = "1";
	public static final String QUEUEPRIORITY_NORMAL = "0";

	// Finance Collateral Details
	public static final String COLLATERAL_FIXEDDEPOSIT = "FD";
	public static final String COLLATERAL_SECURITYCHEQUE = "SC";

	// Deferment Method procedure(Recalculation/Adjustment in Schedule) Constant
	public static final String DEF_METHOD_RECALRATE = "RECALRATE";

	// Limit details Request Types(Used for Deal Type)
	public static final String PREDEAL_CHECK = "PREDEAL_CHECK";
	public static final String CONFIRM = "CONFIRM";
	public static final String CANCEL_UTILIZATION = "CANCEL_UTILIZATION";
	public static final String RESERVE = "RESERVE";
	public static final String RESERVE_OVERRIDE = "OVERRIDE_RESERVE";
	public static final String CANCEL_RESERVE = "CANCEL_RESERVE";
	public static final String AMENDEMENT = "AMENDEMENT";

	// Deal Online Request(Limit Checking) override options
	public static final String LIMIT_GO = "GO";
	public static final String LIMIT_NOGO = "NOGO";

	// Limit Check Service Type Codes (Used in Limit Checking Configuration)
	public static final String QUICK_DISBURSEMENT = "QDISBMT";
	public static final String PRECHECK = "PRECHECK";
	public static final String RESUTIL = "RESUTIL";
	public static final String ORESUTIL = "ORESUTIL";
	public static final String CONFIRM_RESERVATION = "CONRES";
	public static final String CANCEL_RESERVATION = "CANRES";
	public static final String CANCEL_UTILIZE = "CANUTIL";
	public static final String MANUAL_DEVIATION = "MDEVTR";
	public static final String COLLATERAL_VALIDATION = "COLVALD";

	// Collateral Mark & DeMark status
	public static final String COLLATERAL_MARK = "MARK";
	public static final String COLLATERAL_DEMARK = "DEMARK";

	// Finance Cancellation process
	public static final boolean ACCOUNTING_TOTALREVERSAL = false;

	// Customer Max Eligibility DSR Value
	public static final int CUST_MAX_DSR = 9999;

	// Closing Status Details for the Finance
	public static final String CLOSE_STATUS_MATURED = "M";
	public static final String CLOSE_STATUS_CANCELLED = "C";
	public static final String CLOSE_STATUS_WRITEOFF = "W";
	public static final String CLOSE_STATUS_EARLYSETTLE = "E";

	// Loan Status of Hold
	public static final String FEE_REFUND_HOLD = "H";
	public static final String FEE_REFUND_RELEASE = "R";

	// Limit Rule
	public static final String LIMITRULE_CUSTOMER = "Customer";
	public static final String LIMITRULE_FINTYPE = "FinanceType";

	/*
	 * public static final String RVW_UNPAID_INST = "RVWUPI"; public static final String RVW_ALL = "RVWALL";
	 */
	// Method code for Schedule Change exists or not on Maintenance
	public static final String method_scheduleChange = "scheduleChange";

	// SepType Method
	public static final String STEPTYPE_PRIBAL = "PRI";
	public static final String STEPTYPE_EMI = "EMI";

	// BPI Treatment Constants
	public static final String BPI_NO = "N";
	public static final String BPI_DISBURSMENT = "D";
	public static final String BPI_SCHEDULE = "S";
	public static final String BPI_CAPITALIZE = "C";
	public static final String BPI_SCHD_FIRSTEMI = "E";

	// BPI Treatment Calculation
	public static final String BPI_CAL_ON_FIRSTFRQDATE = "F";
	public static final String BPI_CAL_ON_LASTFRQDATE = "L";

	// Freezing Period Details
	public static final String FREEZEPERIOD_INTEREST = "I";
	public static final String FREEZEPERIOD_PROJECTED = "P";

	// Planned EMI holiday Details
	public static final String EMIHOLIDAY_INTERESTPAYMENT = "I";
	public static final String EMIHOLIDAY_CAPITALIZATION = "C";
	public static final String EMIHOLIDAY_ADJTONEXTEMI = "E";

	// Planned EMI holiday Method
	public static final String EMIH_FRQ = "F";
	public static final String EMIH_ADHOC = "A";

	// EMI holiday OR BPI marking flag in Schedule
	public static final String FLAG_BPI = "B";
	public static final String FLAG_ADDTNL_BPI = "I";
	public static final String FLAG_HOLIDAY = "H";
	public static final String FLAG_POSTPONE = "P";
	public static final String FLAG_REAGE = "A";
	public static final String FLAG_UNPLANNED = "U";
	public static final String FLAG_HOLDEMI = "S";
	public static final String FLAG_30DAYS_FIXED = "Z";
	public static final String FLAG_GLMINPFT = "M";
	public static final String FLAG_STRTPRDHLD = "E";
	public static final String FLAG_MORTEMIHOLIDAY = "X";

	// Recording Fee Type
	public static final String RECFEETYPE_CASH = "Cash";
	public static final String RECFEETYPE_CHEQUE = "Cheque";

	// Planned EMI Holiday Methods
	public static final String PLANEMIHMETHOD_FRQ = "F";
	public static final String PLANEMIHMETHOD_ADHOC = "A";

	// Subvention From Method
	public static final String SUBVN_FROM_MANUFACTURER = "MANF";
	public static final String SUBVN_FROM_DEALER = "DSM";
	public static final String SUBVEN_FEE = "SUBVEN";

	// ModuleId Constants
	public static final int MODULEID_FINTYPE = 1; // FinType
	public static final int MODULEID_PROMOTION = 2; // Promotions

	// Finance Disbursement Status
	public static final String DISB_STATUS_CANCEL = "C";

	public static final String FEES_AGAINST_LOAN = "FC";
	public static final String FEES_AGAINST_BOUNCE = "BC";
	public static final String FEES_AGAINST_ADVISE = "AD";

	// Fee Status Details
	public static final String FEE_STATUS_CANCEL = "C";

	// GST
	// Fee Type Tax
	public static final String FEE_TAXCOMPONENT_INCLUSIVE = "I"; // Inclusive
	public static final String FEE_TAXCOMPONENT_EXCLUSIVE = "E"; // Exclusive

	public static final String OTHERS = "OTH";

	// GDR Availablity Check Rule
	public static final String FEETYPE_GDR = "GDR";

	// Interest Subvention Method
	public static final String INTEREST_SUBVENTION_METHOD_UPFRONT = "U";
	public static final String INTEREST_SUBVENTION_METHOD_MONTHLY = "M";
	public static final String INTEREST_SUBVENTION_METHOD_DEDUCT = "D";

	// Interest Subvention Type
	public static final String INTEREST_SUBVENTION_TYPE_PARTIAL = "P";
	public static final String INTEREST_SUBVENTION_TYPE_FULL = "F";

	// FLP Calculated Types
	public static final String FLPCALCULATED_TYPE_ON_ISSUANCEDATE = "I";
	public static final String FLPCALCULATED_TYPE_ON_VASAPPROVALDATE = "A";

	// Verifications
	public static final String PROCEDT_VERIFICATION_FI_INIT = "FIINIT";
	public static final String PROCEDT_VERIFICATION_FI_APPR = "FIAPPR";
	public static final String PROCEDT_VERIFICATION_TV_INIT = "TVINIT";
	public static final String PROCEDT_VERIFICATION_TV_APPR = "TVAPPR";
	public static final String PROCEDT_VERIFICATION_LV_INIT = "LVINIT";
	public static final String PROCEDT_VERIFICATION_LV_APPR = "LVAPPR";
	public static final String PROCEDT_VERIFICATION_RCU_INIT = "RCUINIT";
	public static final String PROCEDT_VERIFICATION_RCU_APPR = "RCUAPPR";
	public static final String PROCEDT_VERIFICATION_PD_INIT = "PDINIT";
	public static final String PROCEDT_VERIFICATION_PD_APPR = "PDAPPR";
	public static final String PROCEDT_VERIFICATION_LVETTING_INIT = "LVETTINIT";
	public static final String PROCEDT_VERIFICATION_LVETTING_APPR = "LVETTAPPR";

	/**
	 * Sampling constants
	 */
	public static final String PROCEDT_SAMPLING_INIT = "SAMPINIT";
	public static final String PROCEDT_SAMPLING_APPR = "SAMPAPPR";

	/**
	 * Legal Details constants
	 */
	public static final String PROCEDT_LEGAL_INIT = "LEGALINIT";

	// Query Management
	public static final String QUERY_MANAGEMENT = "QUERY_MGMT";
	public static final String FEE_UPFRONT_REQ = "UPFTFEE";

	public static final String FIN_OPTIONS_PUT = "Put";
	public static final String FIN_OPTIONS_CALL = "Call";
	public static final String FIN_OPTIONS_PUTCALL = "Put-Call";
	public static final String FIN_OPTIONS_INTEREST_REVIEW = "Interest Review";
	public static final String FIN_ASSET_REVIEW = "Asset Review";
	public static final String FIN_OPTION_OTHERS = "Others";

	// DownSizing
	public static final String MOVEMENTTYPE_DOWNSIZING = "DS";

	// Loan Category Types.
	public static final String LOAN_CATEGORY_BT = "BT";
	public static final String LOAN_CATEGORY_FP = "FP";
	public static final String LOAN_CATEGORY_LAP = "LAP";

	public static final String RECEIPT_MAKER = "RECEIPT_MAKER";
	public static final String DEPOSIT_MAKER = "DEPOSIT_MAKER";
	public static final String DEPOSIT_APPROVER = "DEPOSIT_APPROVER";
	public static final String REALIZATION_MAKER = "REALIZATION_MAKER";
	public static final String REALIZATION_APPROVER = "REALIZATION_APPROVER";
	public static final String RECEIPT_APPROVER = "RECEIPT_APPROVER";
	public static final String CLOSURE_MAKER = "RECEIPTCLOSURE_MAKER";
	public static final String CLOSURE_APPROVER = "RECEIPTCLOSURE_APPROVER";

	public static final String KNOCKOFF_MAKER = "RECEIPTKNOCKOFF_MAKER";
	public static final String KNOCKOFF_APPROVER = "RECEIPTKNOCKOFF_APPROVER";
	public static final String KNOCKOFFCAN_MAKER = "RECEIPTKNOCKOFFCANCEL_MAKER";
	public static final String KNOCKOFFCAN_APPROVER = "RECEIPTKNOCKOFFCANCEL_APPROVER";

	public static final String SCHEDULE_STAGE_RECEIPT = "SCHEDULE_STAGE_RECEIPT";
	public static final String SCHEDULE_PAYMENT = "SCHEDULE_PAYMENT";
	public static final String STAGE_DEPOSITAPPROVER = "STAGE_DEPOSITAPPROVER";
	public static final String STAGE_DEPOSITMAKER = "STAGE_DEPOSITMAKER";

	public static final String EARLYSETTLEMENT = "ES";
	public static final String PARTIALSETTLEMENT = "EP";

	public static final String POSTING_AGAINST_NONLOAN = "NONLAN";

	// SELLOFF
	public static final String CLOSE_STATUS_SELLOFF = "O";

	// Schedule Tax Invoice Calculated On
	// 1. I : Calculation on Interest Amount
	// 2. P : Calculation on Principal Amount
	// 3. E : Calculation on EMI(Principal + Interest) Amount
	public static final String GST_SCHD_CAL_ON_PFT = "I";
	public static final String GST_SCHD_CAL_ON_PRI = "P";
	public static final String GST_SCHD_CAL_ON_EMI = "E";

	// PMAY Validation
	public static final String PMAY_VALIDATION = "PMAYVALD";

	public static final String PAYABLE_ADVISE = "PayableAdvises";

	public static final String PMAY = "PMAY";

	public static final String HOME_PUCHASE = "HP";
	public static final String UNDER_CONSTRUCTION = "UC";
	public static final String SELF_CONSTRUCTION = "SC";
	public static final String PLOTPUCHASE = "PP+SC";
	public static final String RENOVATION_EXT = "R/E";

	public static final String HOMELOAN = "HL";
	public static final String HOMELOAN_BT = "HLBT";
	public static final String LAP = "LAP";
	public static final String LAP_TP = "LAPTP";
	public static final String HOMELOAN_TP = "HLTP";
	public static final String PERSONAL_LOAN = "PL";
	public static final String VRPL_VRBL = "PLBL";

	public static final String FLAG_RESTRUCTURE = "R";
	public static final String FLAG_RESTRUCTURE_PRIH = "N";

	public static final String FIXED_AMOUNT = "FA";
	public static final String PERCENTAGE = "P";

	public static final String OD_BILLING_DUE_PRINCIPLE = "ODBDP";
	public static final String OD_BILLING_DUE_INTEREST = "ODBDI";
	public static final String OD_BILLING_DUE_PRINCIPLE_AND_INTEREST = "ODBDPAI";

	public static final String OD_TRANCHE_AMOUNT = "Tranche Amount";

	public static final String ACCOUNTTYPE_FIN = "F";

	// Fee Refund Constants
	public static final int MANUAL_ADVISE_PAYABLE = 2;
	public static final String FEE_REFUND_APPROVAL = "FEERFND";

	public static final String SETTLEMENT = "SETTLEMENT";
	public static final String SETTLEMENT_CANCEL = "SETTLEMENT_CANCEL";
	public static final String CROSS_LOAN_KNOCKOFF_APPROVER = "CROSS_LOAN_KNOCKOFF_APPROVER";
	public static final String CROSS_LOAN_KNOCKOFF_MAKER = "CROSS_LOAN_KNOCKOFF_MAKER";
	public static final String CROSS_LOAN_KNOCKOFF_ENQUIRY = "CROSS_LOAN_KNOCKOFF_ENQUIRY";
	public static final String CROSS_LOAN_KNOCKOFF_CANCEL_MAKER = "CROSS_LOAN_KNOCKOFF_CANCEL_MAKER";
	public static final String CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER = "CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER";
	public static final BigDecimal LEI_NUM_LIMIT = new BigDecimal("50000000000");

	// Loan Cancel Types
	public static final String LOAN_CANCEL = "C";
	public static final String LOAN_CANCEL_REBOOK = "CR";
	public static final String LOAN_CANCEL_REMARKS = "Loan Cancellation Reversal";
}
