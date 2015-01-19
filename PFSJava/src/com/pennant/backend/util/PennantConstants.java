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
public class PennantConstants {

	/* DatabaseSystem
	  1 = MSSQL Server 
	  2 = Oracle Server
	  3 = DB2 Server
	  4 = MYSQL Server
	 */
 	public static final String  PROJECT_VERSION ="Pennant Finance Factory Version :1.0";

 	public static final int DatabaseSystem = 1;
 	public static final String applicationCode = "PLF";
 	public static final String default_Language = "EN";
	public static final int searchGridSize = 10;
	public static final int listGridSize = 5;
	public static final String List_Select = "#";
	public static final int borderlayoutMainNorth = 100;
	public static final String LOCAL_CCY = "APP_DFT_CURR"; 
	public static final String LOCAL_CCY_FORMAT = "APP_DFT_CURR_EDIT_FIELD"; 
	public static final String mandateSclass = "mandatory";
	
	//Date Formats
	public static final String dateFormat = "dd/MM/yyyy";
	public static final String timeFormat = "hh:mm:ss";
	public static final String dateTimeFormat = "dd/MM/yyyy HH:mm:ss";
	public static final String dateTimeAMPMFormat = "dd/MM/yyyy  hh:mm:ss a";
	public static final String dateFormate = "dd-MMM-yyyy";
	public static final String DBDateTimeFormat = "yyyy-MM-dd HH:mm:ss:SSS";
	public static final String DBDateTimeFormat1 = "yyyy-MM-dd HH:mm:ss";
	public static final String DBDateFormat = "yyyy-MM-dd";
	public static final String AS400DateFormat = "yyMMdd";
	public static final String DBTimeFormat = "HH:mm:ss";
	public static final String DateTimeFormat = "yyyy-MM-dd hhmmss";
	
	// Amount , Rate & Percentage Formats

	public static final String defaultAmountFormate = "#,##0.##";
	public static final String defaultNoFormate = "#,###";

	public static final String DFTNUMCONVFMT = "B";
	public static final int defaultCCYDecPos = 2;
	public static final int rateFormate = 9;

	public static final String amountFormate4 = "#,##0.0000";
	public static final String amountFormate3 = "#,##0.000";
	public static final String amountFormate2 = "#,##0.00";
	public static final String amountFormate1 = "#,##0.0";
	public static final String amountFormate0 = "#,##0";

	public static final String rateFormate10 = "##0.##########";
	public static final String rateFormate9 = "##0.00#######";
	public static final String rateFormate8 = "##0.########";
	public static final String rateFormate7 = "##0.#######";
	public static final String rateFormate6 = "##0.######";
	public static final String rateFormate5 = "##0.#####";
	public static final String rateFormate4 = "##0.####";
	public static final String rateFormate3 = "##0.###";
	public static final String rateFormate2 = "##0.##";
	public static final String percentageFormate2 = "##0.##";

	//Message Definition Types
	public static final int porcessCONTINUE = -1;
	public static final int porcessOVERIDE = 0;
	public static final int porcessCANCEL = 1;

	//Audit Transaction Codes
	public static final String TRAN_ADD = "A";
	public static final String TRAN_UPD = "M";
	public static final String TRAN_DEL = "D";
	public static final String TRAN_WF = "W";
	public static final String TRAN_BEF_IMG = "B";
	public static final String TRAN_AFT_IMG = "A";
	public static final String TRAN_WF_IMG = "W";

	//Record Type Codes
	public static final String RECORD_TYPE_NEW = "NEW";
	public static final String RECORD_TYPE_UPD = "EDIT";
	public static final String RECORD_TYPE_DEL = "DELETE";
	public static final String RECORD_TYPE_CAN = "CANCEL";
	public static final String RECORD_TYPE_MDEL = "MDELET";

	//Record Status Codes
	public static final String RCD_STATUS_SAVED= "Saved";
	public static final String RCD_STATUS_APPROVED = "Approved";
	public static final String RCD_STATUS_REJECTED = "Rejected";
	public static final String RCD_STATUS_CANCELLED = "Cancelled";
	public static final String RCD_STATUS_REJECTAPPROVAL = "Reject Approval";
	public static final String RCD_STATUS_SUBMITTED = "Submitted";
	public static final String RCD_STATUS_RESUBMITTED = "Resubmitted";

	public static final String RCD_ADD = "ADD";
	public static final String RCD_SAV = "SAVE";
	public static final String RCD_UPD = "MAINTAIN";
	public static final String RCD_DEL = "DELETE";

	public static final String CUSTRELATION_CONNECTED = "C";
	public static final String CUSTRELATION_RELATED = "R";
	public static final String CUSTRELATION_NOTRELATED = "N";

	//WorkFlow Service Method Types
	public static final String method_doApprove = "doApprove";
	public static final String method_doReject = "doReject";
	public static final String method_doDedup = "doDedup";
	public static final String method_doBlacklist = "doBlacklist";
	public static final String method_CheckLimits = "CheckLimits";
	public static final String method_doCheckExceptions = "doCheckExceptions";
	public static final String method_doSendNotification = "doSendNotification";
	public static final String method_doDiscrepancy = "doDiscrepancy";
	public static final String method_doCheckLimits = "doCheckLimits";
	public static final String method_doConfirm = "doConfirm";
	public static final String method_RejectStatus = "rejectStatus";
	public static final String method_doClearQueues = "doClearQueues";
	public static final String method_doFundsAvailConfirmed = "doFundsAvailConfirmed";
	public static final String method_doCheckProspectCustomer = "doCheckProspectCustomer";
	public static final String WF_Audit_Notes = "Notes;";
	public static final String WF_DiscrepancyCheck = "Discrepancy";
	
	//Work Flow Conditional Checking Codes
	public static final String WF_PAST_DUE_OVERRIDE 		= "Past Due with Override";
	public static final String WF_PAST_DUE 					= "Past Due";
	public static final String WF_LIMIT_EXPIRED 			= "Limit Expired";
	public static final String WF_EXCESS_GREATER_THAN_20 	= "Excess > 20%";
	public static final String WF_EXCESS_LESS_THAN_10 		= "Excess < 10%";
	public static final String WF_EXCESS_BETWEEN_10_20 		= "Excess 10-20%";
	public static final String WF_NO_LIMIT 					= "No Limit";
	
	public static final String DISCREPANCY_WARNING		    = "Warning";
	public static final String DISCREPANCY_ERROR		    = "Error";

	//Error Severity Codes
	public static final String ERR_SEV_INFO = "I";
	public static final String ERR_SEV_WARNING = "W";
	public static final String ERR_SEV_ERROR = "E";
	public static final String KEY_SEPERATOR = "-";

	//Regular Expression Constants
	public static final String PPT_VISA_REGEX = "[A-Za-z0-9]*";//Need to think & remove TODO
	public static final String TRADE_LICENSE_REGEX = "^\\w((((\\/?[A-Za-z0-9\\s]+)*)|((\\\\?[A-Za-z0-9\\s]+))*)?)";//Need to think & remove TODO
	public static final String VISA_REGEX = "^\\S[A-Za-z0-9]*";//Need to think & remove TODO

	public static final String USER_LOGIN_REGIX="[a-zA-Z0-9]{5,}";
	public static final String PASSWORD_PATTERN="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=[^\\s]+$)(?=.*[!@#$%^&*_-])";
	public static final int PWD_STATUSBAR_CHAR_LENGTH = 12;
	public static final int PWD_STATUSBAR_SPLCHAR_COUNT = 3;
	
	//Password Criteria Condition
	public static final int ERRCODE_PASSWORDS_SAME = 2;
	public static final int ERRCODE_PASSWORDS_NOTSAME = 3;
	public static final int ERRCODE_PWD_NOT_MATCHED_CRITERIA = 4;
	public static final int PWD_VALID = 1;

	public static final String WEEKEND_DESC = "Weekend";
	public static String NONE = "NONE";
	public static String KEY_FIELD = "Key";

	public static String ERR_9999 = "99999";
	public static String ERR_UNDEF = "000000";

	public static final String defaultScreenCode = "DDE";
	
	//Process Editor Condition Constants
	public static final int CheckList=1;
	public static final int Aggrement=2;
	public static final int Eligibility=3;
	public static final int ScoringGroup=4;
	public static final int Accounting=5;
	public static final int Template=6;
	public static final int CorpScoringGroup=7;
	
	//Process Editor Stage Conditions
	public static final int ShowInStage=1;
	public static final int AllowInputInStage=2;
	public static final int MandInputInStage=3;
	
	//Dedup Field SubCode Constants
	public static final String DedupCust="Customer";
	public static final String DedupFinance="Finance";
	
	//Account Formation Constants
	public static int branchLength=4;
	public static int cCyLength=3;
	public static int headCodeLength=4;
	public static int accountLength=16;  //BBBBBHHHNSSSSSSS OR BBBBBHHHNSSSSCCY
	
	//Asset Category Constant Codes
	public static final String AUTH_DEFAULT = "DEFAULT";
	public static final String CARLOAN = "VEHICLE";
	public static final String HOMELOAN = "HOME";
	public static final String EDUCATON = "EDUCATON";
	public static final String MORTLOAN = "EQPMENT";
	public static final String GOODS = "GOODS";
	public static final String COMMIDITY = "COMIDITY";
	public static final String SHARES = "SHARES";
	public static final String GENGOODS = "GENGOODS";
	
	
	//Finance Management Codes
	public static final String MNT_BASIC_DETAIL 	= "MaintainBasicDetail";
	public static final String ADD_RATE_CHG	 		= "AddRateChange";
	public static final String CHG_REPAY 			= "ChangeRepay";
	public static final String ADD_DISB 			= "AddDisbursement";
	public static final String ADD_DEFF 			= "AddDefferment";
	public static final String RMV_DEFF 			= "RmvDefferment";
	public static final String ADD_TERMS 			= "AddTerms";
	public static final String RMV_TERMS 			= "RmvTerms";
	public static final String RECALC 				= "Recalculate";
	public static final String SUBSCH 				= "SubSchedule";
	public static final String CHGPFT 				= "ChangeProfit";
	public static final String CHGFRQ 				= "ChangeFrequency";
	public static final String CHGGRC 				= "ChangeGestation";
	public static final String DATEDSCHD 			= "AddDatedSchedule";
	public static final String COMPOUND 			= "FairValueRevaluation";
	public static final String SCH_REPAY 			= "SchdlRepayment";
	public static final String SCH_EARLYPAY 		= "EarlySettlement";
	public static final String SCH_EARLYPAYENQ 		= "EarlySettlementEnq";
	public static final String WRITEOFF 			= "WriteOff";
	public static final String CANCELFINANCE 		= "CancelFinance";
	public static final String CANCELREPAY 			= "CancelRepay";
	public static final String TAKAFULPREMIUMEXCLUDE =  "TakafulPremiumExclude";

	//Transaction Entry Accounts
	public static final String DISB = "DISB";
	public static final String REPAY = "REPAY";
	public static final String DOWNPAY = "DOWNPAY";
	public static final String GLNPL = "GLNPL";
	public static final String INVSTR = "INVSTR";
	public static final String CUSTSYS = "CUSTSYS";
	public static final String FIN = "FIN";
	public static final String UNEARN = "UNEARN";
	public static final String SUSP = "SUSP";
	public static final String PROVSN = "PROVSN";
	public static final String COMMIT = "COMMIT";
	public static final String BUILD = "BUILD";
	public static final String EXPN = "EXPN";
	public static final String BILL = "BILL";
	public static final String ADVP = "ADVP";
	
	public static final String FinanceAccount_DISB = "DISB";  
	public static final String FinanceAccount_REPY = "REPAY";  
	public static final String FinanceAccount_DWNP = "DWNP";  
	public static final String FinanceAccount_ERLS = "ERLS";  
	public static final String FinanceAccount_ISCONTADV = "CONTADV";  
	public static final String FinanceAccount_ISBILLACCT = "BILLACCT";  
	public static final String FinanceAccount_ISCNSLTACCT = "CNSLTFEE";  
	public static final String FinanceAccount_ISEXPACCT = "EXPENSE";

	public static final String SYSCUST = "SYSCUST";
	public static final String SYSCNTG = "SYSCNTG";
	public static final String CUSTCNTG = "CUSCNTG";

	//Schedule Overdue Calculation Types
	public static final String SPRI = "SPRI";
	public static final String SPFT = "SPFT";
	public static final String STOT = "STOT";

	//Schedule Overdue Charge Types
	public static final String FLAT = "F";
	public static final String PERCONETIME = "P";
	public static final String PERCONDUEDAYS = "D";

	//Schedule Types
	public static final String DEFERED = "D";
	public static final String SCHEDULE = "S";
	
	//Repayment Method Types
	public static final String REPAYMTH_AUTO = "AUTO";
	public static final String REPAYMTH_MANUAL = "MANUAL";

	//SMT Parameter Values
	public static final String APP_PHASE = "PHASE";
	public static final String APP_PHASE_EOD = "EOD";
	public static final String APP_PHASE_DAY = "DAY";
	public static final String APP_DATE_CUR = "APP_DATE";
	public static final String APP_DATE_LAST = "APP_LAST_BUS_DATE";
	public static final String APP_DATE_NEXT = "APP_NEXT_BUS_DATE";
	public static final String APP_DATE_VALUE = "APP_VALUEDATE";
	public static final String PROC_STATUS_RETRY = "R";
	public static final String PROC_STATUS_COMPLETE = "C";
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String DEF_METHOD_RECALRATE = "RECALRATE";

	//Review Category Codes
	public static final String RVW_UNPAID_INST = "RVWUPI";
	public static final String RVW_UNPAID_REVIEWS = "RVWUPR";
	public static final String RVW_ALL = "RVWALL";

	public static final String AGREEMENT_DEFINITION_DOCS = "DOC, TXT, PNG, JPEG, GIF, PDF";

	//Mail Template Configuration Type Codes
	public static final String TEMPLATE_TYPE_SMS = "S";
	public static final String TEMPLATE_TYPE_EMAIL = "M";
	public static final String TEMPLATE_FORMAT_PLAIN = "P";
	public static final String TEMPLATE_FORMAT_HTML = "H";
	public static final String DEFAULT_CHARSET = "UTF-16";
	public static final String TEMPLATE_FOR_CN = "CN";
	public static final String TEMPLATE_FOR_AE = "AE";

	//Document Types
	public static final String DOC_TYPE_IMAGE = "IMG";
	public static final String DOC_TYPE_PDF = "PDF";
	public static final String DOC_TYPE_WORD = "WORD";
	public static final String DOC_TYPE_MSG = "MSG";
	
	//Chart Detail Configuration Constants
	public static final int CHART_TYPE_LINE = 1;
	public static final int CHART_TYPE_BAR = 2;
	public static final int CHART_TYPE_COLUMN = 3;
	public static final int CHART_TYPE_AREA = 4;
	public static final int CHART_TYPE_PIE = 5;
	public static final int CHART_TYPE_DOUGHNUT = 6;
	public static final int CHART_TYPE_PARETO = 7;
	public static final int CHART_TYPE_MARIMEKKO = 8;
	public static final int CHART_TYPE_STACKED_BAR = 9;
	public static final int CHART_TYPE_STACKED_COLUMN = 10;
	public static final int CHART_TYPE_STACKED_AREA = 11;

	public static final int CHART_TYPE_WIDGETS_AGAUGE = 12;
	public static final int CHART_TYPE_WIDGETS_CGAUGE = 13;
	public static final int CHART_TYPE_WIDGETS_LGAUGE = 14;

	public static final int CHART_TYPE_WIDGETS_FUNNEL = 15;
	public static final int CHART_TYPE_WIDGETS_PYRAMID = 16;
	public static final int CHART_TYPE_WIDGETS_REALTIME = 17;

	public static final int CHART_TYPE_POWER_WATERFALL = 18;
	public static final int CHART_TYPE_POWER_SPLINE = 19;
	public static final int CHART_TYPE_POWER_SPLINEAREA = 20;
	
	//Commitment Constants
	public static final String CMT_TOTALCMT = "TotCommitments";
	public static final String CMT_TOTALCMTAMT = "TotComtAmount";
	public static final String CMT_TOTALUTZAMT = "TotUtilizedAmoun";
	
	public static final String  NEWCMT="NEWCMT";
	public static final String  MNTCMT="MNTCMT";

	//Notes Type Codes
	public static final String NOTES_TYPE_NORMAL = "N";
	public static final String NOTES_TYPE_IMPORTANT = "I";
	
	//Finance Product Codes
	public static final String FINANCE_PRODUCT_MUDARABA = "MUDARABA";
	public static final String FINANCE_PRODUCT_SALAM = "SALAM";
	public static final String FINANCE_PRODUCT_ISTISNA = "ISTISNA";
	public static final String FINANCE_PRODUCT_MUSHARAKA = "MUSHARKA";
	public static final String FINANCE_PRODUCT_IJARAH="IJARAH";
	public static final String FINANCE_PRODUCT_MURABAHA="MURABAHA";
	public static final String FINANCE_PRODUCT_SUKUK="SUKUK";
	public static final String FINANCE_PRODUCT_TAWARRUQ="TAWARRUQ";
	public static final String FINANCE_PRODUCT_WAKALA="WAKALA";
	public static final String FINANCE_PRODUCT_ISTNORM = "ISTNORM";
	public static final String FINANCE_PRODUCT_SUKUKNRM = "SUKUKNRM";

	//Transaction Types
	public static final String CREDIT = "C";
	public static final String DEBIT = "D";
	public static final String INCOME = "INCOME";
	public static final String EXPENSE = "EXPENSE";
	public static final String FININSTA = "FININSTA";
	public static final String CUST_PROSPECT = "PRCUST";
	
	public static final String INTERFACE_CUSTCTG_INDIV = "I";
	public static final String INTERFACE_CUSTCTG_CORP = "C";
	public static final String INTERFACE_CUSTCTG_BANK = "B";
	
	public static final String PFF_CUSTCTG_INDIV = "RETAIL";
	public static final String PFF_CUSTCTG_CORP = "CORP";
	public static final String PFF_CUSTCTG_BANK = "BANK";
	

	//Customer De-dup Field Details
	public static final String CUST_DEDUP_LIST_FIELDS = "CustCIF,CustShrtName,CustCtgCode,CustDOB,CustNationality,CustDocType,CustDocTitle";
	public static final String CUST_DEDUP_LIST_BUILD_EQUAL = "CustIDList = :CustIDList";
	public static final String CUST_DEDUP_LIST_BUILD_LIKE = "CustIDList LIKE :likeCustIDList";
	public static final String CUST_DEDUP_LISTFILED1 = "CustIDList";
	public static final String CUST_DEDUP_LISTFILED2 = "CustDocType";
	public static final String CUST_DEDUP_LISTFILED3 = "CustDocTitle";
	
	//Finance Status Reason Codes
	public static final String FINSTSRSN_SYSTEM 	= "S";
	public static final String FINSTSRSN_MANUAL 	= "M";
	public static final String FINSTSRSN_OTHER 		= "O";

	public static final boolean IS_LDAP_AUHRNTICATION = false;
	
	// Customer Categories 
	public static final String CUST_CAT_INDIVIDUAL	= "I";
	public static final String CUST_CAT_CORPORATE	= "C";
	public static final String CUST_CAT_BANK		= "B";
	
	public static final String NOTES_MODULE_FINANCEMAIN		= "financeMain";
	public static final String NOTES_MODULE_FACILITY		= "facility";
	public static final String NOTES_TYPE_RECOMMEND = "R";
	public static final String NOTES_TYPE_COMMENT = "C";
	
	public static final String INCCATTYPE_COMMIT = "COMMIT";
	public static final String INCCATTYPE_OTHCOMMIT = "COMMITOTH";
	
	public static final String CUSTEMPCODE = "03";
	public static final String CPRCODE = "01";
	public static final String BAHRAINI_CR = "02";
	public static final String PASSPORT = "03";
	public static final String NON_BAHRAINI_INTERNATIONAL_CR = "04";
	public static final String BAHRAINI_GOVERNMENT_ENTITY = "05";
	public static final String NON_BAHRAINI_GOVERNMENT_ENTITY = "06";
	public static final String HAFEEZA = "07";
	public static final String IQAMA = "08";
	public static final String FAMILY_CARD = "09";
	public static final String JOINT_CIF = "10";
	public static final String NEW = "11";
	public static final String COUNTRY_BEHRAIN = "BH";

	// Modules Defining In Agreement Creation 
	
	public static final String AGG_BASICDE = "BASICDE";
	public static final String AGG_EMPMNTD = "EMPMNTD";
	public static final String AGG_INCOMDE = "INCOMDE";
	public static final String AGG_EXSTFIN = "EXSTFIN";
	public static final String AGG_CRDTRVW = "CRDTRVW";
	public static final String AGG_SCOREDE = "SCOREDE";
	public static final String AGG_CARLOAN = "CARLOAN";
	public static final String AGG_MORTGLD = "MORTGLD";
	public static final String AGG_GOODSLD = "GOODSLD";
	public static final String AGG_GENGOOD = "GENGOOD";
	public static final String AGG_COMMODT = "COMMODT";
	public static final String AGG_FNBASIC = "FNBASIC";
	public static final String AGG_SCHEDLD = "SCHEDLD";
	public static final String AGG_CHKLSTD = "CHKLSTD";
	public static final String AGG_RECOMMD = "RECOMMD";
	public static final String AGG_EXCEPTN = "EXCEPTN";

	public static final String INVESTMENT = "INVESTMENT";
	public static final String DEAL = "DEAL";
	public static final String FIN_DIVISION_TREASURY = "TREASURY";
	public static final String WORFLOW_MODULE_FINANCE = "FINANCE";
	public static final String WORFLOW_MODULE_FACILITY = "FACILITY";

	// Query Builder Constants
	public static final String STATICTEXT ="STATICTEXT";
	public static final String GLOBALVAR ="GLOBALVAR";
	public static final String FIELDLIST ="FIELDLIST";
	public static final String CALCVALUE ="CALCVALUE";
	public static final String SUBQUERY ="SUBQUERY";
	public static final String FUNCTION ="FUNCTION";
	public static final String DBVALUE ="DBVALUE";
	
	//Datatype constants
	public static final String VARCHAR ="varchar";
	public static final String NVARCHAR ="nvarchar";
	public static final String CHAR ="char";
	//sqlserver NCHAR
	//public static final String NCHAR ="nchar";
	// db2 NCHAR equvivalent
	public static final String NCHAR ="nchar";
	public static final String DATETIME ="datetime";
	public static final String SMALLDATETIME ="smalldatetime";
	public static final String TIMESTAMP ="timestamp";
	public static final String INT ="int";
	public static final String BIGINT ="bigint";
	public static final String DECIMAL ="decimal";
	public static final String BYTE="byte";
	public static final String NUMERIC="numeric";
	
	public static final String FIN_DIVISION_RETAIL = "RETAIL";
	public static final String FIN_DIVISION_FACILITY = "FACILITY";
	public static final String FIN_DIVISION_COMMERCIAL = "COM";
	public static final String FIN_DIVISION_CORPORATE = "IBD";
	
	public static final String FACILITY_CORPORATE="CORPFAC";
	public static final String FACILITY_COMMERCIAL="COMRFAC";
	public static final String FACILITY_NEW="NEW";
	public static final String FACILITY_REVIEW="RVW";
	public static final String FACILITY_AMENDMENT="AMD";
	public static final String FACILITY_PRESENTING_UNIT="Al Baraka Bahrain";
	public static final String FACILITY_BOOKING_COMM_UNIT="Commercial Branch";
	public static final String FACILITY_BOOKING_CORP_UNIT="IBD Branch";
	public static final String EOD_ACCRUAL_CALC="ACCRUAL CALCULATION";
	public static final String EOD_ACCRUAL_POSTING="ACCRUAL POSTING";
	public static final String EOD_PFT_DTL_UPLOAD="UPLOAD PROFIT DETAILS";
	
	public static final String IBD_Branch="2010";
	public static final String MONTH=" Months";
	public static final String YEAR=" Year";
	public static final String EOD_BATCH_MONITOR ="Y";
	
	public static final String EXPENSE_FOR_EDUCATION ="E";
	public static final String EXPENSE_FOR_ADVANCEBILLING ="A";
	
	public static final String CLOSE_STATUS_MATURED ="M";
	
	public static final String CREDITREVIEW_BANK_TOTASST ="TOTASST";
	public static final String CREDITREVIEW_BANK_TOTLIBNETWRTH ="TOTLIABANETWORTH";
	public static final String CREDITREVIEW_CORP_TOTASST ="TOTAST";
	public static final String CREDITREVIEW_CORP_TOTLIBNETWRTH ="TOTLBLNW";
	
	public static final String CREDITREVIEW_AUDITED="Aud";
	public static final String CREDITREVIEW_UNAUDITED ="UnAud";
	public static final String CREDITREVIEW_MNGRACNTS ="ManAcc";
	public static final String CREDITREVIEW_QUALIFIED="Qual";
	public static final String CREDITREVIEW_UNQUALIFIED ="UnQual";
	public static final String CREDITREVIEW_REMARKS ="R";
	public static final String CREDITREVIEW_GRP_CODE_TRUE ="TRUE";
	public static final String CREDITREVIEW_CALCULATED_FIELD ="Calc";
	public static final String CREDITREVIEW_ENTRY_FIELD ="Entry";
	public static final String CREDITREVIEW_CONSOLIDATED ="Consolidated";
	public static final String CREDITREVIEW_UNCONSOLIDATED ="UnConsolidated";
	
	
	public static final String MAIL_MODULE_FIN = "FIN";
	public static final String MAIL_MODULE_CAF = "CAF";
	public static final String MAIL_MODULE_CREDIT = "CRD";
	public static final String MAIL_MODULE_TREASURY = "TSR";
	public static final String MAIL_MODULE_PROVISION = "PRV";
	public static final String MAIL_MODULE_MANUALSUSPENSE = "MSP";
	
	
	//Rating Type Details
	public static final String DEFAULT_RATE_TYPE = "AIB";
	
	//Finance Premium Types
	public static final String PREMIUMTYPE_P = "P";
	public static final String PREMIUMTYPE_D = "D";
	
	// Facility Transaction Types
	public static final String FACILITY_TRAN_SYNDIACTION = "S";
	public static final String FACILITY_TRAN_DIRECT_OR_BILATERAL = "D";
	public static final String FACILITY_TRAN_CLUBDEAL = "C";
	public static final String FACILITY_TRAN_OTHER = "O";
	//Facility Level of Approval
	public static final String FACILITY_LOA_CEO="CEO";
	public static final String FACILITY_LOA_COMM_BANKING_CREDIT_COMMITTEE="CBCC";
	public static final String FACILITY_LOA_CREDIT_COMMITTEE="CC";
	public static final String FACILITY_LOA_EXECUTIVE_COMMITTEE="EXCOM";
	public static final String FACILITY_LOA_BOARD_OF_DIRECTORS="BOD";
	
	public static final BigDecimal BD_500_K = BigDecimal.valueOf(500000);
	public static final BigDecimal USD_5_M = BigDecimal.valueOf(5000000);
	public static final BigDecimal USD_15_M = BigDecimal.valueOf(15000000);
	public static final BigDecimal Tenor_5_Years = BigDecimal.valueOf(5.0);
	public static final BigDecimal Tenor_7_Years = BigDecimal.valueOf(7.0);
	public static final BigDecimal Tenor_10_Years = BigDecimal.valueOf(10.0);
	
	// For Cheque Printing
	public static final String CHEQUE_PRINTING_CHEQUES = "Checks";
	
	// For Commitment Details
	public static final String COMMITMENT_FIN_TYPE = "NA";
	public static final String COMMITMENT_FIN_CCY = "BHD";
	public static final String COMMITMENT_FIN_EVENT = "COMMIT";
	public static final int COMMITMENT_FIN_FORMATTER = 3;
	
	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_BHD = "BHD";
	public static final String CURRENCY_KWD = "KWD";
	
	public static final int CURRENCY_USD_FORMATTER = 2;
	public static final int CURRENCY_BHD_FORMATTER = 3;
	
	//Early Settlement Agreement Codes
	public static final String EARLYSTL_AGGCODE = "'INSRELEASELETTER', 'TRAFFICRELEASELETTER'";
	
	// Finance Postings
	public static final String Posting_success= "Success";
	public static final String InProgress= "In Progress";
	public static final String Posting_fail="Failed";
	public static final String MODULETYPE_ENQ ="ENQ";
	public static final String MODULETYPE_REPOSTING ="REPOSTING";
	
	//Account Holding Type
	public static final String HOLDTYPE_OVERDUE 	=	"P";
	public static final String HOLDTYPE_FUTURE 		=	"F";

	public static final String FINANCE_PRODUCT_CONVENTIONAL = "CONV"; 
	// Credit Review Division
	public static final String CREDIT_DIVISION_COMMERCIAL = "COMM";
	public static final String CREDIT_DIVISION_CORPORATE = "CORP";
}
