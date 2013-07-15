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

import java.util.Date;

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
 	public static final String  PROJECT_VERSION ="Pennant Finance Factory Version :1.0";//LINUX if linux OS

 	public static final int DatabaseSystem = 1;
	public static final int searchGridSize = 10;
	public static final int listGridSize = 5;
	public static final String List_Select = "#";
	public static final String applicationCode = "PLF";
	public static final String default_Language = "EN";
	public static final String default_LanguageDesc = "English";
	public static final String dateFormat = "dd/MM/yyyy";
	public static final String timeFormat = "hh:mm:ss";
	public static final String dateTimeFormat = "dd/MM/yyyy HH:mm:ss";
	public static final String dateFormate = "dd-MMM-yyyy";
	public static final String DBDateTimeFormat = "yyyy-MM-dd HH:mm:ss:SSS";
	public static final String DBDateTimeFormat1 = "yyyy-MM-dd HH:mm:ss";
	public static final String DBDateFormat = "yyyy-MM-dd";
	public static final String AS400DateFormat = "yyMMdd";
	public static final int borderlayoutMainNorth = 100;

	public static int REPORT_OPEN = 1;
	public static int REPORT_PRINT = 2;

	public static final String defaultAmountFormate = "#,##0.##";
	public static final String defaultNoFormate = "#,###";

	/* DatabaseSystem
	  B = Million
	  L = Lakh
	 */
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

	public static final int porcessCONTINUE = -1;
	public static final int porcessOVERIDE = 0;
	public static final int porcessCANCEL = 1;

	public static final String TRAN_ADD = "A";
	public static final String TRAN_UPD = "M";
	public static final String TRAN_DEL = "D";
	public static final String TRAN_WF = "W";
	public static final String TRAN_BEF_IMG = "B";
	public static final String TRAN_AFT_IMG = "A";
	public static final String TRAN_WF_IMG = "W";

	public static final String RECORD_TYPE_NEW = "NEW";
	public static final String RECORD_TYPE_UPD = "EDIT";
	public static final String RECORD_TYPE_DEL = "DELET";
	public static final String RECORD_TYPE_CAN = "CANCEL";

	public static final String RCD_STATUS_APPROVED = "Approved";

	public static final String RCD_ADD = "ADD";
	public static final String RCD_SAV = "SAVE";
	public static final String RCD_UPD = "MAINTAIN";
	public static final String RCD_DEL = "DELETE";

	public static final String method_doApprove = "doApprove";
	public static final String method_doReject = "doReject";
	public static final String WF_Audit_Notes = "Notes;";

	public static final String ERR_SEV_INFO = "I";
	public static final String ERR_SEV_WARNING = "W";
	public static final String ERR_SEV_ERROR = "E";
	public static final String KEY_SEPERATOR = "-";

	//public static final String workFlowfilePath = "E:\\Projects\\Signavio\\";

	public static final String NAME_REGEX = "^[A-Za-z]+[A-Za-z.\\s]*";
	public static final String ALPHANUM_REGEX = "^\\S[A-Za-z0-9]*";
	public static final String ALPHANUM_SPACE_REGEX = "^\\S[A-Za-z0-9\\s]*";
	public static final String HNO_FNO_REGEX = "^\\S[a-zA-Z0-9\\/\\-\\s\\,]*";
	public static final String PATH_REGEX = "^\\S[a-zA-Z0-9\\/\\-\\s\\,\\:\\.]*";
	public static final String NM_HNO_FNO_REGEX = "[a-zA-Z0-9\\/\\-\\s\\,]*";
	public static final String NUM_REGEX = "[0-9]+";
	public static final String PH_REGEX = "^\\+[0-9\\s]+";
	public static final String PH_REGEX_WITHOUT_COUNTRY_CODE = "^\\S[0-9\\s]+";
	public static final String ZIP_REGEX = "^\\S[0-9]{5,6}";
	public static final String NM_NAME_REGEX = "[a-zA-Z.\\s]*\\s*";
	public static final String NM_ALPHANUM_REGEX = "[A-Za-z0-9\\s]*";
	public static final String NM_NUM_REGEX = "[0-9]*";
	public static final String PPT_VISA_REGEX = "[A-Za-z0-9]*";
	public static final String TRADE_LICENSE_REGEX = "^\\w((((\\/?[A-Za-z0-9\\s]+)*)|((\\\\?[A-Za-z0-9\\s]+))*)?)";
	public static final String MAN_LIC_REGEX = "^\\S[A-Z0-9]+";
	public static final String MAIL_REGEX = "^[a-zA-Z]+[0-9]*((\\.?[a-zA-Z0-9]+)*|(\\_?[a-zA-Z0-9]+)*)?\\@{1}[a-zA-z]+[0-9]*(\\.?[a-zA-Z]{2,4})?\\.{1}[a-zA-Z]{2,3}";
	public static final String ADDRESS_LINE1_REGEX = "^\\S[a-zA-Z0-9\\/\\-\\s\\;\\,]*";
	public static final String NM_ADDRESS_LINE1_REGEX = "[a-zA-Z0-9\\/\\-\\s\\;\\,]*";
	public static final String VISA_REGEX = "^\\S[A-Za-z0-9]*";
	public static final String ALPHA_REGEX = "^\\S[A-Za-z]*";
	public static final String AC_NAME_REGEX = "^[A-Za-z\\@\\#\\&\\(]+[A-Za-z.\\s\\@\\#\\&\\-\\(\\)]*";
	//public static final String MINORCCYUNITS_REGEX= "^\\S(^0$)|(^10$)|(^100$)|(^1000$)|(^10000$)";

	//Use Only With UpperCase textBox
	public static final String ALPHA_CAPS_REGEX = "^\\S[a-zA-Z]*";
	public static final String QUESTION_DESC_REGEX = "\\S[a-zA-Z0-9\\/\\-\\.\\?\\,\\s]+";
	public static final String DESC_REGEX = "^\\S[A-Za-z0-9\\.\\,\\-\\s\\(\\)]*";
	public static final String NM_DESC_REGEX = "^[A-Za-z0-9\\.\\,\\-\\s\\(\\)]*";
	public static final String ALPHANUM_CAPS_REGEX = "^\\S[a-z0-9A-Z]*";
	public static final String ALPHA_CAPS_FL3_REGEX = "[a-zA-z]{3}";
	public static final String ALPHANUM_CAPS_FL2_REGEX = "[a-z0-9A-Z]{2}";
	public static final String ALPHANUM_CAPS_FL3_REGEX = "[a-z0-9A-Z]{3}";
	public static final String ALPHANUM_CAPS_FL4_REGEX = "[a-z0-9A-Z]{4}";
	public static final String NUM_FL2_REGEX = "[0-9]{2}";
	public static final String NUM_FL3_REGEX = "[0-9]{3}";
	public static final String NUM_FL4_REGEX = "[0-9]{4}";
	public static final String NUM_X_REGEX = "[0-9]";
	public static final String ALPHANUM_UNDERSCORE_REGEX = "^\\S[A-Za-z0-9\\_]*";
	public static final String ALPHA_SPACE_UNDERSCORE_REGEX = "^\\S[A-Za-z\\s\\_]*";
	public static final String DB_FIELD_NAME_REGEX = "^[A-Za-z]+[A-Za-z0-9\\_]*";

	public static final int ERRCODE_PASSWORDS_SAME = 2;
	public static final int ERRCODE_PASSWORDS_NOTSAME = 3;
	public static final int ERRCODE_PWD_NOT_MATCHED_CRITERIA = 4;
	public static final int PWD_VALID = 1;

	public static final String WEEKEND_DESC = "Weekend";
	public static String NONE = "NONE";
	public static int ZERO = 0;
	public static String KEY_FIELD = "Key";

	public static String ERR_9999 = "99999";
	public static String ERR_UNDEF = "000000";

	public static final String defaultScreenCode = "DDE";
	public static final String defaultInternalCustomerAccount = "6";

	
	public static final int CheckList=1;
	public static final int Aggrement=2;
	public static final int Eligibility=3;
	public static final int ScoringGroup=4;
	public static final int Accounting=5;
	public static final int Template=6;
	public static final int CorpScoringGroup=7;
	
	public static final int ShowInStage=1;
	public static final int AllowInputInStage=2;
	public static final int MandInputInStage=3;
	
	public static final String DedupCust="Customer";
	public static final String Deduploan="Loan";
	
	public static int branchLength=4;
	public static int headCodeLength=4;
	public static int cCyLength=3;
	public static int accountLength=16;  //BBBBBHHHNSSSSSSS OR BBBBBHHHNSSSSCCY
	public static final String CARLOAN="CARLOAN";
	public static final String HOMELOAN="HOMELOAN";
	public static final String EDULOAN="EDULOAN";
	public static final String MORTLOAN="MORTLOAN";
	
	public static final String USER_LOGIN_REGIX="[a-zA-Z0-9]{5,}";
	public static final String PASSWORD_PATTERN="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=[^\\s]+$)(?=.*[!@#$%^&*_-])";
	public static final int PWD_STATUSBAR_CHAR_LENGTH = 12;
	public static final int PWD_STATUSBAR_SPLCHAR_COUNT = 3;

	//Finance Management Code
	public static final String ADD_RATE_CHG = "AddRateChange";
	public static final String CHG_REPAY = "ChangeRepay";
	public static final String ADD_DISB = "AddDisbursement";
	public static final String ADD_DEFF = "AddDefferment";
	public static final String RMV_DEFF = "RmvDefferment";
	public static final String ADD_TERMS = "AddTerms";
	public static final String RMV_TERMS = "RmvTerms";
	public static final String RECALC = "Recalculate";
	public static final String SCH_REPAY = "SchdlRepayment";
	public static final String SCH_EARLYPAY = "EarlySettlement";
	public static final String WRITEOFF = "WriteOff";

	//Transaction Entry Accounts
	public static final String DISB = "DISB";
	public static final String REPAY = "REPAY";
	public static final String GLNPL = "GLNPL";
	public static final String INVSTR = "INVSTR";
	public static final String CUSTSYS = "CUSTSYS";
	public static final String FIN = "FIN";
	public static final String COMMIT = "COMMIT";

	public static final String SYSCUST = "SYSCUST";
	public static final String SYSCNTG = "SYSCNTG";
	public static final String CUSTCNTG = "CUSCNTG";

	public static final String FEES = "FEES";
	public static final String CLAAMT = "CLAAMT";

	public static final String SPRI = "SPRI";
	public static final String SPFT = "SPFT";
	public static final String STOT = "STOT";

	public static final String FLAT = "F";
	public static final String PERCENTAGE = "P";

	public static final String DEFERED = "D";
	public static final String SCHEDULE = "S";

	//SMT Parameter Values
	public static final String APP_PHASE = "PHASE";
	public static final String APP_PHASE_SOD = "SOD";
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

	public static final String RVW_UNPAID_INST = "RVWUPI";
	public static final String RVW_UNPAID_REVIEWS = "RVWUPR";
	public static final String RVW_ALL = "RVWALL";

	//server Operating system 
	public static final String server_OperatingSystem = "WINDOWS"; //if Os is Linux set "LINUX"

	public static final String AGREEMENT_DEFINITION_DOCS = "DOC, TXT, PNG, JPEG, GIF, PDF";

	public static final String TEMPLATE_TYPE_SMS = "S";
	public static final String TEMPLATE_TYPE_EMAIL = "M";
	public static final String TEMPLATE_FORMAT_PLAIN = "P";
	public static final String TEMPLATE_FORMAT_HTML = "H";
	public static final String DEFAULT_CHARSET = "UTF-16";
	public static final String TEMPLATE_FOR_CN = "CN";
	public static final String TEMPLATE_FOR_AE = "AE";

	public static final String DOC_TYPE_IMAGE = "IMG";
	public static final String DOC_TYPE_PDF = "PDF";

	public static final String RECORD_TYPE_MDEL = "MDELET";
	public static final String dateTimeAMPMFormat = "dd/MM/yyyy  hh:mm:ss a";
	public static final String DBTimeFormat = "HH:mm:ss";
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
	@SuppressWarnings("deprecation")
	public static final Date MAXIMUM_DATE = new Date("2020/01/01");
	public static final String CMT_TOTALCMT = "TotCommitments";
	public static final String CMT_TOTALCMTAMT = "TotComtAmount";
	public static final String CMT_TOTALUTZAMT = "TotUtilizedAmoun";
	
	public static final String  NEWCMT="NEWCMT";
	public static final String  MNTCMT="MNTCMT";

	public static final String NOTES_TYPE_NORMAL = "N";
	public static final String NOTES_TYPE_IMPORTANT = "I";
	
	public static final String FINANCE_PRODUCT_MUDARABA = "MUDARABA";
	public static final String FINANCE_PRODUCT_SALAM = "SALAM";
	public static final String FINANCE_PRODUCT_ISTISNA = "ISTISNA";

	public static final String CREDIT = "C";
	public static final String DEBIT = "D";

	
	
	
	
}
