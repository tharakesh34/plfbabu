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
public class FinanceConstants {
	private FinanceConstants() {
		super();
	}

	public static final String	MODULE_NAME						= "Finance";

	// Finance Product Codes
	public static final String	PRODUCT_MUDARABA				= "MUDARABA";
	public static final String	PRODUCT_SALAM					= "SALAM";
	public static final String	PRODUCT_ISTISNA					= "ISTISNA";
	public static final String	PRODUCT_MUSHARAKA				= "MUSHARKA";
	public static final String	PRODUCT_IJARAH					= "IJARAH";
	public static final String	PRODUCT_MURABAHA				= "MURABAHA";
	public static final String	PRODUCT_SUKUK					= "SUKUK";
	public static final String	PRODUCT_TAWARRUQ				= "TAWARRUQ";
	public static final String	PRODUCT_WAKALA					= "WAKALA";
	public static final String	PRODUCT_ISTNORM					= "ISTNORM";
	public static final String	PRODUCT_SUKUKNRM				= "SUKUKNRM";
	public static final String	PRODUCT_MUSAWAMA				= "MUSAWAMA";
	public static final String	PRODUCT_CONVENTIONAL			= "CONV";
	public static final String	PRODUCT_QARDHASSAN				= "QHASSAN";
	public static final String	PRODUCT_STRUCTMUR				= "STRMUR";
	public static final String	PRODUCT_FWIJARAH				= "FWIJARAH";
	public static final String	PRODUCT_ODFACILITY				= "ODFCLITY";
	public static final String	PRODUCT_DISCOUNT				= "DISCOUNT";

	// Finance Division Details
	public static final String	FIN_DIVISION_RETAIL				= "BFSD";
	public static final String	FIN_DIVISION_CORPORATE			= "CL";
	public static final String	FIN_DIVISION_COMMERCIAL			= "COM";
	public static final String	FIN_DIVISION_FACILITY			= "FACILITY";
	public static final String	FIN_DIVISION_TREASURY			= "TREASURY";

	// Process Editor Condition Constants
	public static final int		PROCEDT_CHECKLIST				= 1;
	public static final int		PROCEDT_AGREEMENT				= 2;
	public static final int		PROCEDT_ELIGIBILITY				= 3;
	public static final int		PROCEDT_RTLSCORE				= 4;
	public static final int		PROCEDT_STAGEACC				= 5;
	public static final int		PROCEDT_TEMPLATE				= 6;
	public static final int		PROCEDT_CORPSCORE				= 7;
	public static final int		PROCEDT_FINDEDUP				= 8;
	public static final int		PROCEDT_BLACKLIST				= 9;
	public static final int		PROCEDT_POLICEDEDUP				= 10;
	public static final int		PROCEDT_CUSTDEDUP				= 11;
	public static final int		PROCEDT_LIMIT					= 12;
	public static final int		PROCEDT_TATNOTIFICATION			= 13;
	public static final int		PROCEDT_RETURNCHQ				= 14;

	// Process Editor Stage Conditions
	public static final int		PROCEDT_SHOWINSTAGE				= 1;
	public static final int		PROCEDT_ALWINPUTSTAGE			= 2;
	public static final int		PROCEDT_MANDINPUTSTAGE			= 3;

	// Dedup Field SubCode Constants
	public static final String	DEDUP_CUSTOMER					= "Customer";
	public static final String	DEDUP_FINANCE					= "Finance";
	public static final String	DEDUP_BLACKLIST					= "BlackList";
	public static final String	DEDUP_POLICE					= "Police";
	public static final String	DEDUP_RETCHQ					= "Cheque";
	public static final String	DEDUP_LIMITS					= "Limits";
	public static final String	DEDUP_COLLATERAL				= "Collateral";

	// Schedule Overdue Calculation Types
	public static final String	ODCALON_SPRI					= "SPRI";
	public static final String	ODCALON_SPFT					= "SPFT";
	public static final String	ODCALON_STOT					= "STOT";

	// Schedule Apportionment Types
	public static final String	PAY_APPORTIONMENT_SPRI			= "SPRI";
	public static final String	PAY_APPORTIONMENT_SPFT			= "SPFT";
	public static final String	PAY_APPORTIONMENT_STOT			= "STOT";

	// Schedule Apportionment Types
	public static final String	PAY_APPORTIONMENT_TO_NONE		= "NONE";
	public static final String	PAY_APPORTIONMENT_TO_PASTDUE	= "PASTDUE";
	public static final String	PAY_APPORTIONMENT_TO_SUSPENSE	= "SUSPENSE";
	public static final String	PAY_APPORTIONMENT_TO_ALL		= "ALL";

	// Schedule Overdue Charge Types
	public static final String	PENALTYTYPE_FLAT				= "F";
	public static final String	PENALTYTYPE_PERC_ONETIME		= "P";
	public static final String	PENALTYTYPE_PERC_ON_DUEDAYS		= "D";
	public static final String	PENALTYTYPE_PERC_ON_PD_MTH		= "M";
	public static final String	PENALTYTYPE_FLAT_ON_PD_MTH		= "A";

	// Schedule Types
	public static final String	SCH_TYPE_SCHEDULE				= "S";
	public static final String	SCH_TYPE_LATEPAYPROFIT			= "I";

	// Repayments Method Types
	public static final String	REPAYMTH_AUTO					= "CASA";
	public static final String	REPAYMTH_AUTODDA				= "DDA";
	public static final String	REPAYMTH_MANUAL					= "MANUAL";
	public static final String	REPAYMTH_PDC					= "PDC";
	public static final String	REPAYMTH_UDC					= "UDC";

	// Finance Reference Generation Constants
	public static final String	REF_DIVISION_RETAIL				= "PB";
	public static final String	REF_DIVISION_CORP				= "WB";

	// Finance Status Reason Codes
	public static final String	FINSTSRSN_SYSTEM				= "S";
	public static final String	FINSTSRSN_MANUAL				= "M";
	public static final String	FINSTSRSN_OTHER					= "O";

	// Finance Premium Types
	public static final String	PREMIUMTYPE_PREMIUM				= "P";
	public static final String	PREMIUMTYPE_DISCOUNT			= "D";

	// Finance Queue Priority
	public static final String	QUEUEPRIORITY_HIGH				= "3";
	public static final String	QUEUEPRIORITY_MEDIUM			= "2";
	public static final String	QUEUEPRIORITY_LOW				= "1";
	public static final String	QUEUEPRIORITY_NORMAL			= "0";

	// Finance Service Event Actions
	public static final String	FINSER_EVENT_ORG				= "Origination";
	public static final String	FINSER_EVENT_PREAPPROVAL		= "PreApproval";
	public static final String	FINSER_EVENT_RATECHG			= "AddRateChange";
	public static final String	FINSER_EVENT_ADVRATECHG			= "AdvPftRateChange";
	public static final String	FINSER_EVENT_CHGRPY				= "ChangeRepay";
	public static final String	FINSER_EVENT_ADDDISB			= "AddDisbursement";
	public static final String	FINSER_EVENT_RLSDISB			= "RlsHoldDisbursement";
	public static final String	FINSER_EVENT_POSTPONEMENT		= "Postponement";
	public static final String	FINSER_EVENT_UNPLANEMIH			= "UnPlannedEMIH";
	public static final String	FINSER_EVENT_ADDTERM			= "AddTerms";
	public static final String	FINSER_EVENT_RMVTERM			= "RmvTerms";
	public static final String	FINSER_EVENT_RECALCULATE		= "Recalculate";
	public static final String	FINSER_EVENT_SUBSCHD			= "SubSchedule";
	public static final String	FINSER_EVENT_CHGPFT				= "ChangeProfit";
	public static final String	FINSER_EVENT_CHGFRQ				= "ChangeFrequency";
	public static final String	FINSER_EVENT_RESCHD				= "ReSchedule";
	public static final String	FINSER_EVENT_CHGGRCEND			= "ChangeGestation";
	public static final String	FINSER_EVENT_FAIRVALREVAL		= "FairValueRevaluation";
	public static final String	FINSER_EVENT_INSCHANGE			= "InsuranceChange";
	public static final String	FINSER_EVENT_RECEIPT			= "Receipt";
	public static final String	FINSER_EVENT_SCHDRPY			= "SchdlRepayment";
	public static final String	FINSER_EVENT_EARLYRPY			= "EarlyPayment";
	public static final String	FINSER_EVENT_EARLYSETTLE		= "EarlySettlement";
	public static final String	FINSER_EVENT_WRITEOFF			= "WriteOff";
	public static final String	FINSER_EVENT_WRITEOFFPAY		= "WriteOffPay";
	public static final String	FINSER_EVENT_PROVISION			= "Provision";
	public static final String	FINSER_EVENT_SUSPHEAD			= "Suspense";
	public static final String	FINSER_EVENT_CANCELFIN			= "CancelFinance";
	public static final String	FINSER_EVENT_ROLLOVER			= "Rollover";
	public static final String	FINSER_EVENT_BASICMAINTAIN		= "MaintainBasicDetail";
	public static final String	FINSER_EVENT_RPYBASICMAINTAIN	= "MaintainRepayDetail";
	public static final String	FINSER_EVENT_CANCELRPY			= "CancelRepay";
	public static final String	FINSER_EVENT_LIABILITYREQ		= "LiabilityRequest";
	public static final String	FINSER_EVENT_NOCISSUANCE		= "NOCIssuance";
	public static final String	FINSER_EVENT_TIMELYCLOSURE		= "TimelyClosure";
	public static final String	FINSER_EVENT_INSCLAIM			= "InsuranceClaim";
	public static final String	FINSER_EVENT_EARLYSTLENQ		= "EarlySettlementEnq";
	public static final String	FINSER_EVENT_TFPREMIUMEXCL		= "TakafulPremiumExclude";
	public static final String	FINSER_EVENT_COMPOUND			= "FairValueRevaluation";
	public static final String	FINSER_EVENT_FINFLAGS			= "FinanceFlag";
	public static final String	FINSER_EVENT_REINSTATE			= "ReInstate";
	public static final String	FINSER_EVENT_SUPLRENTINCRCOST	= "SuplRentIncrCost";
	public static final String	FINSER_EVENT_REAGING			= "ReAging";
	public static final String	FINSER_EVENT_CANCELDISB			= "CancelDisbursement";
	public static final String	FINSER_EVENT_OVERDRAFTSCHD		= "OverdraftSchedule";
	public static final String	FINSER_EVENT_PLANNEDEMI			= "PlannedEMI";
	public static final String	FINSER_EVENT_HOLDEMI			= "HoldEMI";
	public static final String	BULK_RATE_CHG					= "BulkRateChange";
	public static final String	FINSER_EVENT_FEEPAYMENT			= "FeePayment";
	public static final String	FINSER_EVENT_CHGSCHDMETHOD		= "ChangeSchdlMethod";


	// This value is Hard coded in View "CovenantsMaintenance_View"
	public static final String	FINSER_EVENT_COVENANTS			= "Covenants";

	// Finance Collateral Details
	public static final String	COLLATERAL_FIXEDDEPOSIT			= "FD";
	public static final String	COLLATERAL_SECURITYCHEQUE		= "SC";

	// Deferment Method procedure(Recalculation/Adjustment in Schedule) Constant
	public static final String	DEF_METHOD_RECALRATE			= "RECALRATE";

	// Limit details Request Types(Used for Deal Type)
	public static final String	PREDEAL_CHECK					= "PREDEAL_CHECK";
	public static final String	CONFIRM							= "CONFIRM";
	public static final String	CANCEL_UTILIZATION				= "CANCEL_UTILIZATION";
	public static final String	RESERVE							= "RESERVE";
	public static final String	RESERVE_OVERRIDE				= "OVERRIDE_RESERVE";
	public static final String	CANCEL_RESERVE					= "CANCEL_RESERVE";
	public static final String	AMENDEMENT						= "AMENDEMENT";

	//Deal Online Request(Limit Checking) override options
	public static final String	LIMIT_GO						= "GO";
	public static final String	LIMIT_NOGO						= "NOGO";

	// Limit Check Service Type Codes (Used in Limit Checking Configuration)
	public static final String	QUICK_DISBURSEMENT				= "QDISBMT";
	public static final String	PRECHECK						= "PRECHECK";
	public static final String	RESUTIL							= "RESUTIL";
	public static final String	ORESUTIL						= "ORESUTIL";
	public static final String	CONFIRM_RESERVATION				= "CONRES";
	public static final String	CANCEL_RESERVATION				= "CANRES";
	public static final String	CANCEL_UTILIZE					= "CANUTIL";
	public static final String	MANUAL_DEVIATION				= "MDEVTR";

	// Collateral Mark & DeMark status
	public static final String	COLLATERAL_MARK					= "MARK";
	public static final String	COLLATERAL_DEMARK				= "DEMARK";

	// Finance Maintenance Handling Instruction Codes
	public static final String	INSTCODE_EARLYSTLMNT			= "ES";
	public static final String	INSTCODE_PARSTLMNT				= "PS";
	public static final String	INSTCODE_AMENDINSTLMNT			= "AI";
	public static final String	INSTCODE_RESCHDPAY				= "RP";
	public static final String	INSTCODE_POSTPONEMNT			= "PP";
	public static final String	INSTCODE_TENUREREDUCTN			= "TR";

	// Finance Cancellation process
	public static final boolean	ACCOUNTING_TOTALREVERSAL		= false;

	// Customer Max Eligibility DSR Value
	public static final int		CUST_MAX_DSR					= 9999;

	// Expense Type for Finance
	public static final String	EXPENSE_FOR_EDUCATION			= "E";
	public static final String	EXPENSE_FOR_ADVANCEBILLING		= "A";

	// Closing Status Details for the Finance
	public static final String	CLOSE_STATUS_MATURED			= "M";
	public static final String	CLOSE_STATUS_CANCELLED			= "C";
	public static final String	CLOSE_STATUS_WRITEOFF			= "W";
	public static final String	CLOSE_STATUS_EARLYSETTLE		= "E";

	// Limit Rule
	public static final String	LIMITRULE_CUSTOMER				= "Customer";
	public static final String	LIMITRULE_FINTYPE				= "FinanceType";

	// Review Category Codes
	//FIXME: PV: 31MAY17 Duplicate and different version present in calculation constants
	/*	public static final String	RVW_UNPAID_INST					= "RVWUPI";
	public static final String	RVW_ALL							= "RVWALL";
	 */
	// Method code for Schedule Change exists or not on Maintenance
	public static final String	method_scheduleChange			= "scheduleChange";

	// Overdraft Dropping Method
	public static final String	DROPINGMETHOD_CONSTANT			= "C";
	public static final String	DROPINGMETHOD_VARIABLE			= "V";

	//SepType Method
	public static final String	STEPTYPE_PRIBAL					= "PRI";
	public static final String	STEPTYPE_EMI					= "EMI";

	// BPI Treatment Constants
	public static final String	BPI_NO							= "N";
	public static final String	BPI_DISBURSMENT					= "D";
	public static final String	BPI_SCHEDULE					= "S";
	public static final String	BPI_CAPITALIZE					= "C";
	public static final String	BPI_SCHD_FIRSTEMI				= "E";

	//Freezing Period Details
	public static final String	FREEZEPERIOD_INTEREST			= "I";
	public static final String	FREEZEPERIOD_PROJECTED			= "P";

	//Planned EMI holiday Details
	public static final String	EMIHOLIDAY_INTERESTPAYMENT		= "I";
	public static final String	EMIHOLIDAY_CAPITALIZATION		= "C";
	public static final String	EMIHOLIDAY_ADJTONEXTEMI			= "E";

	//Planned EMI holiday Method
	public static final String	EMIH_FRQ						= "F";
	public static final String	EMIH_ADHOC						= "A";

	//EMI holiday OR BPI marking flag in Schedule
	public static final String	FLAG_BPI						= "B";
	public static final String	FLAG_HOLIDAY					= "H";
	public static final String	FLAG_POSTPONE					= "P";
	public static final String	FLAG_REAGE						= "A";
	public static final String	FLAG_UNPLANNED					= "U";
	public static final String	FLAG_HOLDEMI					= "S";

	//Recording Fee Type
	public static final String	RECFEETYPE_CASH					= "Cash";
	public static final String	RECFEETYPE_CHEQUE				= "Cheque";

	// Planned EMI Holiday Methods
	public static final String	PLANEMIHMETHOD_FRQ				= "F";
	public static final String	PLANEMIHMETHOD_ADHOC			= "A";

	//ModuleId Constants
	public static final int		MODULEID_FINTYPE				= 1;						//FinType 
	public static final int		MODULEID_PROMOTION				= 2;						//Promotions

	// Finance Disbursement Status
	public static final String	DISB_STATUS_CANCEL				= "C";

	// Manual Advise Types 
	public static final int		MANUAL_ADVISE_RECEIVABLE		= 1;						//Receivable Advise
	public static final int		MANUAL_ADVISE_PAYABLE			= 2;						//Payable Advise

	public static final String	POSTING_AGAINST_LOAN			= "L";						//Misc. Postings
	public static final String	POSTING_AGAINST_CUST			= "C";						//Misc. Postings
	public static final String	POSTING_AGAINST_COLLATERAL		= "CLT";					//Misc. Postings
	public static final String	POSTING_AGAINST_LIMIT			= "LMT";					//Misc. Postings

	public static final String	FEES_AGAINST_LOAN				= "FC";
	public static final String	FEES_AGAINST_BOUNCE				= "BC";
	public static final String	FEES_AGAINST_ADVISE				= "AD";

	// Fee Status Details
	public static final String	FEE_STATUS_CANCEL				= "C";

	//GST
	// Fee Type Tax
	public static final String FEE_TAXCOMPONENT_INCLUSIVE 		= "I"; 					// Inclusive
	public static final String FEE_TAXCOMPONENT_EXCLUSIVE 		= "E"; 					// Exclusive

	//Verifications
	public static final String PROCEDT_VERIFICATION_FI_INIT = "FIINIT";
	public static final String PROCEDT_VERIFICATION_FI_APPR = "FIAPPR";
	public static final String PROCEDT_VERIFICATION_TV_INIT = "TVINIT";
	public static final String PROCEDT_VERIFICATION_TV_APPR = "TVAPPR";
	public static final String PROCEDT_VERIFICATION_LV_INIT = "LVINIT";
	public static final String PROCEDT_VERIFICATION_LV_APPR = "LVAPPR";
}
