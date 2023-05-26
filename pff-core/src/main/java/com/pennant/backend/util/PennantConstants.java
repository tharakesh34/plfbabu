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

import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * This stores all constants required for running the applicationcheque
 */
public class PennantConstants {
	private PennantConstants() {
		super();
	}

	public static boolean CITY_FREETEXT = false;
	public static final String default_Language = "EN";
	public static final int searchGridSize = 10;
	public static final int listGridSize = 5;
	public static final String List_Select = "#";
	public static final int borderlayoutMainNorth = 100;
	public static final String mandateSclass = "mandatory";
	public static final int branchCode_maxValue = 20;

	// Date Formats
	public static final String dateFormat = DateFormat.SHORT_DATE.getPattern();
	public static final String DBTimeFormat = DateFormat.LONG_TIME.getPattern();
	public static final String monthYearFormat = "MMM/yyyy";
	public static final String timeFormat = "hh:mm:ss";
	public static final String dateTimeFormat = "dd/MM/yyyy HH:mm:ss";
	public static final String dateTimeAMPMFormat = "dd/MM/yyyy  hh:mm:ss a";
	public static final String DBDateFormat = DateFormat.FULL_DATE.getPattern();
	public static final String DBDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	public static final String AS400DateFormat = "yyMMdd";
	public static final String dateAndTimeFormat = "dd-MMM-yyyy HH:mm";
	public static final String XMLDateFormat = "yyyy-MM-dd HH:mm";
	public static final String APIDateFormatter = "yyyy-MM-dd'T'HH:mm:ss";

	// Amount , Rate & Percentage Formats

	public static final String defaultAmountFormate = "#,##0.##";
	public static final String defaultNoFormate = "#,###";
	public static final String defaultNoLimiterFormate = "####";
	public static final String DFTNUMCONVFMT = "B";
	public static final int rateFormate = 9;

	public static final String amountFormate4 = "#,##0.0000";
	public static final String amountFormate3 = "#,##0.000";
	public static final String amountFormate2 = "#,##0.00";
	public static final String amountFormate1 = "#,##0.0";
	public static final String amountFormate0 = "#,##0";

	public static final String integralAmtFormate4 = "#,###.0000";
	public static final String integralAmtFormate3 = "#,###.000";
	public static final String integralAmtFormate2 = "#,###.00";
	public static final String integralAmtFormate1 = "#,###.0";
	public static final String integralAmtFormate0 = "#,###";

	public static final String integralAmtFormat4 = "#,###.0000";
	public static final String integralAmtFormat3 = "#,###.000";
	public static final String integralAmtFormat2 = "#,###.00";
	public static final String integralAmtFormat1 = "#,###.0";
	public static final String integralAmtFormat0 = "#,###";

	public static final String in_amountFormate4 = "#,##0.0000";
	public static final String in_amountFormate3 = "#,##0.000";
	public static final String in_amountFormate2 = "#,##0.00";
	public static final String in_amountFormate1 = "#,##0.0";
	public static final String in_amountFormate0 = "#,##0";

	public static final String in_integralAmtFormat4 = "#,###.0000";
	public static final String in_integralAmtFormat3 = "#,###.000";
	public static final String in_integralAmtFormat2 = "#,###.00";
	public static final String in_integralAmtFormat1 = "#,###.0";
	public static final String in_integralAmtFormat0 = "#,###";

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

	// Message Definition Types
	public static final int porcessCONTINUE = -1;
	public static final int porcessOVERIDE = 0;
	public static final int porcessCANCEL = 1;

	// Audit Transaction Codes
	public static final String TRAN_ADD = "A";
	public static final String TRAN_UPD = "M";
	public static final String TRAN_DEL = "D";
	public static final String TRAN_WF = "W";
	public static final String TRAN_BEF_IMG = "B";
	public static final String TRAN_AFT_IMG = "A";
	public static final String TRAN_WF_IMG = "W";

	// Record Type Codes
	public static final String RECORD_TYPE_NEW = "NEW";
	public static final String RECORD_TYPE_UPD = "EDIT";
	public static final String RECORD_TYPE_DEL = "DELETE";
	public static final String RECORD_TYPE_CAN = "CANCEL";
	public static final String RECORD_TYPE_MDEL = "MDELET";

	// Record Status Codes
	public static final String RCD_STATUS_SAVED = "Saved";
	public static final String RCD_STATUS_APPROVED = "Approved";
	public static final String RCD_STATUS_REJECTED = "Rejected";
	public static final String RCD_STATUS_DECLINED = "Declined";
	public static final String RCD_STATUS_CANCELLED = "Cancelled";
	public static final String RCD_STATUS_REJECTAPPROVAL = "Reject Approval";
	public static final String RCD_STATUS_SUBMITTED = "Submitted";
	public static final String RCD_STATUS_RESUBMITTED = "Resubmitted";
	public static final String RCD_STATUS_FINALIZED = "Finalized";
	public static final String RCD_STATUS_REASSIGNED = "Reassigned";
	public static final String RCD_STATUS_MANUALASSIGNED = "ManuallyAssigned";

	// List Maintenance Workflow Record Type codes
	public static final String RCD_ADD = "ADD";
	public static final String RCD_SAV = "SAVE";
	public static final String RCD_UPD = "MAINTAIN";
	public static final String RCD_DEL = "DELETE";
	public static final String RCD_REM = "REMOVE";
	public static final String RCD_EDT = "EDIT";

	// WorkFlow Service Method Types
	public static final String method_doFinnov = "doFinnovCheck";
	public static final String method_doDms = "doDmsService";
	public static final String method_doPrfHunter = "doPrfHunterService";
	public static final String method_doApprove = "doApprove";
	public static final String method_saveOrUpdate = "saveOrUpdate";
	public static final String method_doReversal = "doReversal";
	public static final String method_doPreApprove = "doPreApprove";
	public static final String method_doReject = "doReject";
	public static final String WF_CIBIL = "cibilCheck";
	public static final String method_doDedup = "doDedup";
	public static final String method_doBlacklist = "doBlacklist";
	public static final String method_CheckLimits = "CheckLimits";
	public static final String method_doCheckExceptions = "doCheckExceptions";
	public static final String method_doSendNotification = "doSendNotification";
	public static final String method_doCheckLimits = "doCheckLimits";
	public static final String method_doConfirm = "doConfirm";
	public static final String method_RejectStatus = "rejectStatus";
	public static final String method_doClearQueues = "doClearQueues";
	public static final String method_doCheckProspectCustomer = "doCheckProspectCustomer";
	public static final String method_doCheckDeviations = "doCheckDeviations";
	public static final String method_doCheckAuthLimit = "doCheckAuthLimit";
	public static final String method_doCheckScore = "doCheckScore";
	public static final String method_doCheckSMECustomer = "doCheckSMECustomer";
	public static final String method_doCheckCollaterals = "doCheckCollaterals";
	public static final String method_doCheckDepositProc = "doCheckDeposit";
	public static final String method_doCheckFurtherWF = "doCheckFurtherWF";
	/* Sanctioning date and reversals for sanction date functioning */
	public static final String method_execSanctionExpData = "execSanctionExpData";
	public static final String method_doRevSanctionExpData = "doRevSanctionExpData";

	public static final String method_externalDedup = "executeExperianDedup";
	public static final String method_hunter = "executeHunter";
	public static final String method_Experian_Bureau = "executeBureau";
	public static final String method_Crif_Bureau = "executeCrif";
	public static final String method_Cibil_Bureau = "executeCibilConsumer";
	public static final String method_LegalDesk = "executeLegalDesk";
	public static final String method_HoldFinance = "executeHold";
	public static final String method_bre = "executeBRE";
	public static final String method_notifyCrm = "notifyCrm";
	public static final String METHOD_DO_CHECK_DEVIATION_APPROVAL = "doCheckDeviationApproval";
	public static final String METHOD_DO_VALIDATE_QUERYMGMT_APPROVAL = "doValidateQueryMGMTApproval";
	public static final String METHOD_DO_VALIDATE_LEGAL_APPROVAL = "executeLegalApproval";
	public static final String METHOD_OFFERLETTER = "executeofferLetter";
	public static final String METHOD_UPDATE_ATTRIBUTE = "doUpdateAttribute";
	public static final String METHOD_REVERT_QUEUE = "doRevertQueue";
	public static final String method_HunterService = "doHunterService";
	public static final String METHOD_INTIATEHUNTERSERVICE = "doInitiateHunterService";
	public static final String METHOD_DOMAINCHECKSERVICE = "doDomainCheckService";
	public static final String METHOD_DOCUMENTVERIFICATION = "doDocumentVerification";
	public static final String METHOD_LOAN_DATA_SYNC = "executeLoanDataSync";
	public static final String method_pushToEFS = "pushToEFS";

	// Error Severity Codes
	public static final String ERR_SEV_INFO = "I";
	public static final String ERR_SEV_WARNING = "W";
	public static final String ERR_SEV_ERROR = "E";
	public static final String KEY_SEPERATOR = "-";

	// Common Usage Parameters
	public static final String NONE = "NONE";
	public static final String KEY_FIELD = "Key";
	public static final String ERR_9999 = "99999";
	public static final String ERR_UNDEF = "000000";

	// SMT Parameter Values
	public static final String LOCAL_CCY = "APP_DFT_CURR";
	public static final String LOCAL_CCY_FORMAT = "APP_DFT_CURR_EDIT_FIELD";
	public static final String APP_PHASE = "PHASE";
	public static final String APP_PHASE_EOD = "EOD";
	public static final String APP_PHASE_DAY = "DAY";
	public static final String APP_DATE_LAST = "APP_LAST_BUS_DATE";
	public static final String APP_DATE_NEXT = "APP_NEXT_BUS_DATE";
	public static final String APP_DFT_START_DATE = "APP_DFT_START_DATE";
	public static final String ALW_PAST_SCHDATE = "ALW_PAST_SCHDATE";
	public static final String PROC_STATUS_RETRY = "R";
	public static final String PROC_STATUS_COMPLETE = "C";
	public static final String DEFAULT_COUNTRY = "APP_DFT_COUNTRY";
	public static final String SET_POSTDATE_TO = "SET_POSTDATE_TO";

	// Application Level label Definitions, can be moved to labels TODO
	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String ALLOW_ACCESS_TO_APP = "ALLOW_ACCESS";

	// Document Types
	public static final String DOC_TYPE_IMAGE = "IMG";
	public static final String DOC_TYPE_JPG = "JPG";
	public static final String DOC_TYPE_PDF = "PDF";
	public static final String DOC_TYPE_WORD = "WORD";
	public static final String DOC_TYPE_MSG = "MSG";
	public static final String DOC_TYPE_DOC = "DOC";
	public static final String DOC_TYPE_DOCX = "DOCX";
	public static final String DOC_TYPE_EXCEL = "EXCEL";
	public static final String DOC_TYPE_ZIP = "ZIP";
	public static final String DOC_TYPE_7Z = "7Z";
	public static final String DOC_TYPE_RAR = "RAR";
	public static final String DOC_TYPE_TXT = "TXT";
	public static final String DOC_TYPE_PNG = "PNG";
	public static final String DOC_TYPE_JSON = "JSON";

	public static final String DOC_TYPE_CODE_PHOTO = "CUSTPHOTO";

	public static final String DOC_TYPE_PDF_EXT = ".pdf";
	public static final String DOC_TYPE_WORD_EXT = ".docx";
	public static final String DOC_TYPE_JPG_EXT = ".jpg";
	public static final String DOC_TYPE_PNG_EXT = ".png";

	// Commitment Constants
	public static final String CMT_TOTALCMT = "TotCommitments";
	public static final String CMT_TOTALCMTAMT = "TotComtAmount";
	public static final String CMT_TOTALUTZAMT = "TotUtilizedAmoun";

	// Income Types
	public static final String INCOME = "INCOME";
	public static final String EXPENSE = "EXPENSE";
	public static final String FININSTA = "FININSTA";

	// Customer Categories
	public static final String PFF_CUSTCTG_INDIV = "RETAIL";
	public static final String PFF_CUSTCTG_CORP = "CORP";
	public static final String PFF_CUSTCTG_SME = "SME";

	public static final String PFF_CIBIL_TYPE_GENERATE = "R";
	public static final String PFF_CIBIL_TYPE_ENQUIRY = "E";

	// Customer De-dup Field Details
	public static final String CUST_DEDUP_LIST_FIELDS = "CustCIF,CustShrtName,CustCtgCode,CustDOB,CustNationality, CustCompName";
	public static final String CUST_DEDUP_LIST_BUILD_EQUAL = "CustIDList = :CustIDList";
	public static final String CUST_DEDUP_LIST_BUILD_LIKE = "CustIDList LIKE :likeCustIDList";
	public static final String CUST_DEDUP_LISTFILED1 = "CustIDList";
	public static final String CUST_DEDUP_LISTFILED2 = "CustDocType";
	public static final String CUST_DEDUP_LISTFILED3 = "CustDocTitle";

	// Notes Type Codes
	public static final String NOTES_MODULE_FINANCEMAIN = "FinanceMain";
	public static final String NOTES_MODULE_FACILITY = "facility";
	public static final String NOTES_TYPE_RECOMMEND = "R";
	public static final String NOTES_TYPE_COMMENT = "C";
	public static final String NOTES_TYPE_NORMAL = "N";
	public static final String NOTES_TYPE_IMPORTANT = "I";

	// Income Category Type
	public static final String INCCATTYPE_COMMIT = "COMMIT";
	public static final String INCCATTYPE_OTHCOMMIT = "COMMITOTH";

	public static final String CPRCODE = "01";
	public static final String PASSPORT = "02";
	public static final String TRADELICENSE = "15";
	public static final String PANNUMBER = "03";
	public static final String FORM60 = "FORM60";

	// Customer Employment Status Codes
	public static final String CUSTEMPSTS_EMPLOYED = "EMPLOYED";
	public static final String CUSTEMPSTS_SME = "SME";
	public static final String CUSTEMPSTS_SELFEMP = "SELFEMP";
	public static final String EmploymentName_OTHERS = "OTHERS";

	// Modules Defining In Agreement Creation
	public static final String AGG_BASICDE = "BASICDE";
	public static final String AGG_EMPMNTD = "EMPMNTD";
	public static final String AGG_INCOMDE = "INCOMDE";
	public static final String AGG_EXSTFIN = "EXSTFIN";
	public static final String AGG_CRDTRVW = "CRDTRVW";
	public static final String AGG_SCOREDE = "SCOREDE";
	public static final String AGG_FNBASIC = "FNBASIC";
	public static final String AGG_SCHEDLD = "SCHEDLD";
	public static final String AGG_CHKLSTD = "CHKLSTD";
	public static final String AGG_RECOMMD = "RECOMMD";
	public static final String AGG_EXCEPTN = "EXCEPTN";
	public static final String AGG_VERIFIC = "VERIFIC";
	public static final String AGG_CONTACT = "CONTACT";
	public static final String AGG_COAPPDT = "COAPPDT";
	public static final String AGG_COLLTRL = "COLLTRL";
	public static final String AGG_SERVFEE = "SERVFEE";
	public static final String AGG_VAS = "VAS";
	public static final String AGG_DIRECDT = "DIRECDT";
	public static final String AGG_REPAYDT = "REPAYDT";
	public static final String AGG_CHRGDET = "CHRGDET";
	public static final String AGG_DISBURS = "DISBURS";
	public static final String AGG_DOCDTLS = "DOCDTLS";
	public static final String AGG_COVENAN = "COVENAN";
	public static final String AGG_LIABILI = "LIABILI";
	public static final String AGG_BANKING = "BANKING";
	public static final String AGG_SOURCIN = "SOURCIN";
	public static final String AGG_EXTENDE = "EXTENDE";
	public static final String AGG_IRRDTLS = "IRRDTLS";
	public static final String AGG_DEVIATI = "DEVIATI";
	public static final String AGG_ACTIVIT = "ACTIVIT";
	public static final String AGG_ELGBLTY = "ELGBLTY";
	public static final String AGG_QRYMODL = "QRYMODL";
	public static final String AGG_PSLMODL = "PSLMODL";
	public static final String AGG_SMPMODL = "SMPMODL";
	public static final String AGG_LNAPPCB = "LNAPPCB";
	public static final String AGG_KYCDT = "KYCDT";
	public static final String AGG_CHQDT = "CHQDT";

	// Workflow definition Constants

	public static final String WORFLOW_MODULE_LOAN = "LOAN";
	public static final String WORFLOW_MODULE_FINANCE = "FINANCE";
	public static final String WORFLOW_MODULE_PROMOTION = "PROMOTION";
	public static final String WORFLOW_MODULE_FACILITY = "FACILITY";
	public static final String WORFLOW_MODULE_OVERDRAFT = "OVERDRAFT";
	public static final String WORFLOW_MODULE_CD = "CD";
	public static final String WORFLOW_MODULE_COLLATERAL = "COLLATERAL";
	public static final String WORFLOW_MODULE_VAS = "VAS";
	public static final String WORFLOW_MODULE_COMMITMENT = "COMMITMENT";

	// Query Builder Constants
	public static final String STATICTEXT = "STATICTEXT";
	public static final String GLOBALVAR = "GLOBALVAR";
	public static final String FIELDLIST = "FIELDLIST";
	public static final String CALCVALUE = "CALCVALUE";
	public static final String SUBQUERY = "SUBQUERY";
	public static final String FUNCTION = "FUNCTION";
	public static final String DBVALUE = "DBVALUE";

	// Data type constants
	public static final String VARCHAR = "varchar";
	public static final String NVARCHAR = "nvarchar";
	public static final String CHAR = "char";
	public static final String NCHAR = "nchar";
	public static final String DATETIME = "datetime";
	public static final String SMALLDATETIME = "smalldatetime";
	public static final String TIMESTAMP = "timestamp";
	public static final String INT = "int";
	public static final String BIGINT = "bigint";
	public static final String DECIMAL = "decimal";
	public static final String BYTE = "byte";
	public static final String NUMERIC = "numeric";

	public static final String EOD_ACCRUAL_CALC = "ACCRUAL CALCULATION";
	public static final String EOD_ACCRUAL_POSTING = "ACCRUAL POSTING";
	public static final String EOD_PFT_DTL_UPLOAD = "UPLOAD PROFIT DETAILS";

	public static final String TAKEOVERAGRDATA = "TAKEOVER";
	public static final String ADVANCEPAYMENTAGRDATA = "ADVANCEPAY";
	public static final String JOINSCUSTAGRDATA = "JOINTCUST";
	public static final String LPOFORFLEETVEHICLE_DEALER = "LPOFLTDLR";
	public static final String LPOFORFLEETVEHICLE_PRIVATE = "LPOFLTPRI";

	// Rating Type Details
	public static final String DEFAULT_CUST_TYPE = "EA";

	// For Commitment Details
	public static final String COMMITMENT_FIN_TYPE = "NA";
	public static final String COMMITMENT_FIN_EVENT = "COMMIT";

	// Finance Postings
	public static final String POSTSTS_SUCCESS = "Success";
	public static final String POSTSTS_INPROGRESS = "In Progress";
	public static final String POSTSTS_FAILED = "Failed";
	public static final String MODULETYPE_ENQ = "ENQ";
	public static final String MODULETYPE_REPOSTING = "REPOSTING";

	public static final String KYC_PRIORITY_VERY_HIGH = "5";
	public static final String KYC_PRIORITY_HIGH = "4";
	public static final String KYC_PRIORITY_MEDIUM = "3";
	public static final String KYC_PRIORITY_NORMAL = "2";
	public static final String KYC_PRIORITY_LOW = "1";

	// Car Loan Details
	public static final String FTS = "FTS";
	public static final String PAYORDER = "PAYORDER";
	public static final String DEALER = "DEALER";

	public static final String PRIVATE = "PRIVATE";
	public static final String DEVELOPER = "DEV";

	public static final String AUTO_ASSIGNMENT = "Auto";
	public static final String MANUAL_ASSIGNMENT = "Manual";

	// Daily Download Details
	public static final String DAILYDOWNLOAD_CURRENCY = "CCY";
	public static final String DAILYDOWNLOAD_RELATIONSHIPOFFICER = "ROF";
	public static final String DAILYDOWNLOAD_CUSTTYPE = "CTY";
	public static final String DAILYDOWNLOAD_DEPARMENT = "DPT";
	public static final String DAILYDOWNLOAD_CUSTGROUP = "CUG";
	public static final String DAILYDOWNLOAD_ACCOUNTTYPE = "ATY";
	public static final String DAILYDOWNLOAD_CUSTRATING = "CUR";
	public static final String DAILYDOWNLOAD_ABUSERS = "ABS";
	public static final String DAILYDOWNLOAD_CUSTOMERS = "CUS";
	public static final String DAILYDOWNLOAD_COUNTRY = "COU";
	public static final String DAILYDOWNLOAD_CUSTSTATUSCODES = "CSC";
	public static final String DAILYDOWNLOAD_INDUSTRY = "IND";
	public static final String DAILYDOWNLOAD_BRANCH = "BRN";
	public static final String DAILYDOWNLOAD_SYSINTACCOUNTDEF = "SIA";
	public static final String DAILYDOWNLOAD_TRANSACTIONCODE = "TXC";
	public static final String DAILYDOWNLOAD_IDENTITYTYPE = "IDT";

	// Allowed Back Value Days for Start Date in Finance Creation
	public static final String PHONETYPE_MOBILE = "MOBILE";
	public static final String EMAILTYPE_ADHRCRD = "ADHRCRD";
	public static final String DELIMITER_COMMA = ",";

	public static final Double SQUAREFEET = 0.09290304;
	public static final Double SQUAREMETER = 10.7639;

	public static final String HOLIDAY_CATEGORY_DEFAULT = "NOTAPP";

	public static final String ACCOUNTTYPE_CA = "CA";
	public static final String ACCOUNTTYPE_SA = "SA";

	public static final String REQ_TYPE_VALIDATE = "VALIDATE";
	public static final String REQ_TYPE_CAN = "CANCELLATION";
	public static final String RES_TYPE_SUCCESS = "0000";

	public static final String ADDRESS_TYPE_RESIDENCE = "CURRES";
	public static final String LEGEL_FEES = "Legal Fees";
	public static final String FINES = "Fines";
	public static final String OTHERS = "Others";
	public static final int defaultCCYDecPos = 2;

	// Insurance Status Details Refactored by PV on 26JUN20
	public static final String INSURANCE_STATUS_APPROVED = "APPROVED";
	public static final String INSURANCE_STATUS_DECLINED = "DECLINED";
	public static final String INSURANCE_STATUS_APPROVED_EXCEPTIONS = "APPEXCEP";

	// Insurance CLAIM PAID STATUS Refactored by PV on 26JUN20
	public static final String INSURANCE_PAIDSTATUS_PAID = "PAID";
	public static final String INSURANCE_PAIDSTATUS_REJECTED = "REJECTED";
	public static final String INSURANCE_PAIDSTATUS_PENDING = "PENDING";

	// Insurance Claim Reason
	public static final String INSURANCE_CLAIMREASON_DEATH = "Disease";
	public static final String INSURANCE_CLAIMREASON_PTD = "PTD";

	public static final String EVENTBASE = "FinEvent";
	public static final String ACCNO = "Account";
	public static final String POSTDATE = "PostDate";
	public static final String VALUEDATE = "ValueDate";
	public static boolean CHANGE_SEGMENT = true;

	public static final String HOME_PHONE = "HOMEPHN";
	public static final String OFFICE_PHONE = "OFFICE";
	public static final String CASDOC = "CASDOCUMENT";

	public static final String PREAPPROVAL_TABLE_TYPE = "_PA";

	public static final String PO_STATUS_ISSUE = "I";
	public static final String PO_STATUS_PENDING = "P";

	public static final String SUSP_TRIG_AUTO = "AUTO";
	public static final String SUSP_TRIG_MAN = "MANUAL";

	public static final String ADDRESS_TYPE_OFFICE = "OFFICE";

	public static final String FIXED_DEPOSIT = "FD";
	public static final String SECURITY_CHEQUE = "SC";

	// FinSource Id
	public static final String FINSOURCE_ID_API = "API";

	// Rates
	public static final String RATE_BASE = "BASE";
	public static final String RATE_SPECIAL = "SPECIAL";
	public static final String RATE_MARGIN = "MARGIN";

	// RejectCodes
	public static final String Reject_Finance = "Finance";
	public static final String Reject_Payment = "Payment";

	// Branches
	public static final String Branch_AREAOFC = "AREAOFFICE";
	public static final String Branch_HEADOFC = "HEADOFFICE";
	public static final String Branch_REGIONALOFC = "REGIONALOFFICE";
	public static final String Branch_STATEOFC = "STATEOFFICE";
	public static final String Branch_SUBBRANCHOFC = "SUBBRANCHOFFICE";

	public static final String Branch_SOUTH = "SOUTH";
	public static final String Branch_NORTH = "NORTH";
	public static final String Branch_EAST = "EAST";
	public static final String Branch_WEST = "WEST";

	public static final String FEE_CALCULATION_TYPE_RULE = "RULE";
	public static final String FEE_CALCULATION_TYPE_FIXEDAMOUNT = "FIXEDAMT";
	public static final String FEE_CALCULATION_TYPE_PERCENTAGE = "PERCENTG";
	public static final String PARTPAYMENT_CALCULATEDON_POS = "POS CURRENT FINANCIAL YEAR";

	public static final String FEE_CALCULATEDON_TOTALASSETVALUE = "TOTAST";
	public static final String FEE_CALCULATEDON_LOANAMOUNT = "LOANAM";
	public static final String FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL = "OUTSPL";
	public static final String FEE_CALCULATEDON_PAYAMOUNT = "PAYAMT";
	public static final String FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE = "PRINCFU";
	public static final String FEE_CALCULATEDON_DROPLINEPOS = "DRPLNPOS";
	public static final String FEE_CALCULATEDON_TOTLOANAMOUNT = "TOTLAM";
	public static final String FEE_CALCULATEDON_OUTSTANDINGAMOUNT = "OUTSAM";
	public static final String FEE_CALCULATEDON_OUTSTANDINGINTEREST = "OUTSIN";
	public static final String FEE_CALCULATEDON_ADJUSTEDPRINCIPAL = "ADTP";
	public static final String FEE_CALCULATEDON_CUSTOMERSANCTIONLIMIT = "CUSTSL";

	// Validation Groups
	public static final String VLD_CRT_SCHD = "CRTSCHD";
	public static final String VLD_CRT_LOAN = "CRTLOAN";
	public static final String VLD_UPD_LOAN = "UPDLOAN";
	public static final String VLD_SRV_LOAN = "SRVLOAN";
	public static final String NOT_AVAILABLE = "NAV";

	public static final DataEngineStatus BATCH_TYPE_PRESENTMENT_IMPORT = new DataEngineStatus();
	public static final String FILESTATUS_STARTING = "STARTING";

	// GST Tax Constants TaxApplicableFor_PrimaryCustomer
	public static final String TAXAPPLICABLEFOR_PRIMAYCUSTOMER = "P";
	public static final String TAXAPPLICABLEFOR_COAPPLICANT = "C";
	public static final String TAXAPPLICABLEFOR_GUARANTOR = "G";

	// Customer Dedup source sstem
	public static final String CUSTOMER_DEDUP_SOURCE_SYSTEM_PENNANT = "PENNANT";

	// No objection certificate
	public static final String NO_OBJECT_CERT = "NOC";

	public static final String FINANCE_INQUIRY_CUSTOMER = "CUSTOMER";
	public static final String FINANCE_INQUIRY_LOAN = "LOAN";

	// Expense Uploads
	public static final String EXPENSE_UPLOAD_LOAN = "Loan";
	public static final String EXPENSE_UPLOAD_LOANTYPE = "LoanType";
	public static final String UPLOAD_INCOMETYPE_EXPENSE = "E";
	public static final String UPLOAD_STATUS_SUCCESS = "SUCCESS";
	public static final String UPLOAD_STATUS_FAIL = "FAILED";
	public static final String UPLOAD_STATUS_REJECT = "REJECT";
	public static final String EXPENSE_UPLOAD_ADD = "A";
	public static final String EXPENSE_UPLOAD_OVERRIDE = "O";
	public static final String EXPENSE_MODE_SCREEN = "Screen";
	public static final String EXPENSE_MODE_UPLOAD = "Upload";

	// FinTypeExpenses
	public static final String EXPENSE_CALCULATEDON_LOAN = "LOAN";
	public static final String EXPENSE_CALCULATEDON_ODLIMIT = "ODLIMIT";

	// Sub Category Constants
	public static final String SUBCATEGORY_DOMESTIC = "DOMESTIC"; // Domestic
	public static final String SUBCATEGORY_NRI = "NRI"; // NRI
	public static final String EMPLOYMENTTYPE_SEP = "SEP"; // Domestic
	public static final String EMPLOYMENTTYPE_SENP = "SENP";
	public static final String EMPLOYMENTTYPE_SALARIED = "SALARIED"; // Domestic
	public static final String EMPLOYMENTTYPE_NONWORKING = "NON-WORKING";

	// WorkFlow Fields
	public static final String WORKFLOW_VERSION = "VERSION";
	public static final String WORKFLOW_LASTMNTBY = "LASTMNTBY";
	public static final String WORKFLOW_LASTMNTON = "LASTMNTON";
	public static final String WORKFLOW_RECORDSTATUS = "RECORDSTATUS";
	public static final String WORKFLOW_RECORDTYPE = "RECORDTYPE";
	public static final String WORKFLOW_ROLECODE = "ROLECODE";
	public static final String WORKFLOW_NEXTROLECODE = "NEXTROLECODE";
	public static final String WORKFLOW_NEXTTASKID = "NEXTTASKID";
	public static final String WORKFLOW_TASKID = "TASKID";
	public static final String WORKFLOW_WORKFLOWID = "WORKFLOWID";

	// Sampling Fields
	public static final String SAMPLING_RESUBMIT_REASON = "SAMPLINGRR";

	// Query Management
	public static final String QUERY_ORIGINATION = "Origination";
	public static final String QUERY_SAMPLING = "SAMPLING";
	public static final String QUERY_LEGAL_VERIFICATION = "LEGAL";

	public static final String OTHER_BANK = "OTHER";

	// GST Invoice Constants
	public static final String GST_INVOICE_TRANSACTION_TYPE_DEBIT = "D";
	public static final String GST_INVOICE_TRANSACTION_TYPE_CREDIT = "C";
	public static final String GST_INVOICE_TRANSACTION_TYPE_EXEMPTED = "B";
	public static final String GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT = "W";
	public static final String GST_INVOICE_STATUS_INITIATED = "I";
	public static final String GST_INVOICE_STATUS_PRINTED = "P";
	public static final String GST_INVOICE_STATUS_CANCELLED = "C";
	public static final String FEETYPE_BOUNCE = "BOUNCE";
	public static final String FEETYPE_ODC = "ODC";
	public static final String FEETYPE_EXEMPTED = "EXEMPTED_FEE";
	public static final String FEETYPE_PFT_EXEMPTED = "FEETYPE_PFT_EXEMPTED";
	public static final String FEETYPE_PRI_EXEMPTED = "FEETYPE_PRI_EXEMPTED";
	public static final String FEETYPE_FPFT_EXEMPTED = "FEETYPE_FPFT_EXEMPTED";
	public static final String FEETYPE_FPRI_EXEMPTED = "FEETYPE_FPRI_EXEMPTED";
	public static final String FEETYPE_SUBVENTION = "SUBVEN";
	public static final String FEETYPE_RESTRUCT_CPZ = "FEETYPE_RESTRUCT_CPZ";

	public static final String COLLATERAL_LTV_CHECK_DISBAMT = "LTVDISBAMT";
	public static final String COLLATERAL_LTV_CHECK_FINAMT = "LTVFINAMT";

	public static final int RECEIPT_DEFAULT = 0;
	public static final int RECEIPT_DOWNLOADED = 1;
	public static final int RECEIPT_APPROVED = 2;
	public static final int RECEIPT_REJECTED = 3;

	public static final int REPRESENTMENT_DEFAULT = 0;
	public static final int REPRESENTMENT_DOWNLOADED = 1;
	public static final int REPRESENTMENT_APPROVED = 2;
	public static final int REPRESENTMENT_REJECTED = 3;

	// active and inactive
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	public static final String AVERAGE = "AVG";
	public static final String GOOD = "GOOD";
	public static final String VERYGOOD = "VGOOD";

	public static final String DSA = "DSA";
	public static final String PSF = "PSF";
	public static final String DMA = "DMA";
	public static final String ONLINE = "ONL";
	public static final String OFFLINE = "OFFL";
	public static final String REFERRAL = "REF";
	public static final String NTB = "NTB";
	public static final String ASM = "ASM";
	public static final String KVI = "KVI";
	public static final String DEFAULT = "DEFAULT";
	public static final String COONNECTOR = "CONNECTOR";

	public static final String REC_ON_APPR = "REC_ON_APPROVAL";

	public static final String ORIGINAL = "O";
	public static final String PHOTOSTAT = "P";

	public static final String ACCESSTYPE_ENTITY = "ENTITY";
	public static final String ACCESSTYPE_CLUSTER = "CLUSTER";
	public static final String ACCESSTYPE_BRANCH = "BRANCH";

	// Opex Fees Type
	public static final String OPEX_FEE_TYPE_FIXED = "Fixed";
	public static final String OPEX_FEE_TYPE_FLOATING = "Floating";

	public static final String DROPLINE_CALCON_DISB = "D";
	public static final String DROPLINE_CALCON_SANCTION = "S";

	public static final String FLEXI_PURE = "P";
	public static final String FLEXI_DROPLINE = "D";
	public static final String FLEXI_HYBRID = "H";
	public static final String TERM_LOAN = "TL";
	public static final String COLLATERAL_VALUE_UPDATE = "COLLETARAL_VALUE_UPDATE";
	public static final String SETTLEMENT_REQUEST_UPLOAD = "SETTLEMENT_REQUEST_UPLOAD";
	public static final String SETTLEMENT_REQUEST_DOWNLOAD = "SETTLEMENT_REQUEST_DOWNLOAD";
	public static final String SUBVENTION_REQUEST_UPLOAD = "SUBVENTION_REQUEST_UPLOAD";
	public static final String SUBVENTION_REQUEST_DOWNLOAD = "SUBVENTION_REQUEST_DOWNLOAD";
	public static final String RATE_CHANGE_UPLOAD = "RATE_CHANGE_UPLOAD";
	public static final String PAYMENT_METHOD_UPLOAD = "PAYMENT_METHOD_UPLOAD";

	public static final String PROCESS_PRESENTMENT = "P";
	public static final String PROCESS_REPRESENTMENT = "R";
	public static final int CHUNK_SIZE = 500;

	// Sorting
	public static final String SortOrder_ASC = "ASC";
	public static final String SortOrder_DESC = "DESC";

	public static final int PRETTY_PRINT_INDENT_FACTOR = 4;
	// External Service
	public static final String TYPE_FILE = "FILE";
	public static final String TYPE_DB = "DB";
	public static final String TYPE_WEBSERVICE_REST = "WS_REST";
	public static final String TYPE_WEBSERVICE_SOAP = "WS_SOAP";
	public static final String TYPE_WEBSERVICE_XML = "WS_XML";
	public static final String TYPE_HTP = "HTP";
	public static final String NOTIFICATIONTYPE_NONE = "0";
	public static final String NOTIFICATIONTYPE_MOBILE = "1";
	public static final String NOTIFICATIONTYPE_EMAIL = "2";
	public static final String INTERFACE_TYPE_IDB = "IDB";
	public static final String INTERFACE_TYPE_INTERFACE = "INTERFACE";

	// cancel loan API
	public static final String LOAN_CANCEL = "LOANCANCEL";
	// Builder Company
	public static final String BRANCH_APF = "Branch APF";
	public static final String DEEMED_APF = "Deemed APF";
	public static final String NON_APF = "Non APF";
	public static final String REJECT = "Reject";
	public static final String NEGATIVE = "NEG";
	public static final String TIER1 = "Tier1";
	public static final String TIER2 = "Tier2";
	public static final String TIER3 = "Tier3";
	public static final String NAGATIVE = "NAGATIVE";
	public static final String PARTNERSHIP = "PARTNERSHIP";
	public static final String PUBLICLIMITED = "PUBLICLIMITED";
	public static final String LLP = "LLP";
	public static final String LLC = "LLC";
	public static final String PROPRIETORSHIP = "PROPRIETORSHIP";
	public static final String PRIVATELTD = "PRIVATELTD";
	public static final String rateFormate11 = "###0.00#######";

	public static final String MANUFACTURING = "MA";
	public static final String TRADING = "TR";
	public static final String SERVICES = "SE";
	public static final String RESIDENT = "RE";
	public static final String NON_RESIDENT = "NR";
	public static final String MERCHANT_NAVY = "MN";
	public static final String PIO = "PIO";
	public static final String GOVT = "GOVT";
	public static final String PUBLIC_LIMITED = "Public Limited";
	public static final String PRIVATE_LIMITED = "Private Limited";
	public static final String EDUCATION_INSTITUTE = "Education Institute";
	public static final String MNC = "MNC";
	public static final String LOCAL_CIVIC = "Local Civic";
	public static final String SEP = "9";
	public static final String FOREIGN_NATIONAL = "FORNAT";
	public static final String TRUST_SOCIETY = "TRUSTSOC";

	// Final Valuation Amount Decisions
	public static final String OK = "OK";
	public static final String NOTOK = "NOTOK";
	public static final String OTHER = "OTH";

	public static final String REASON_CODE_EARLYSETTLEMENT = "FC";
	// Temporary Purpose
	public static final String OLD_CREDITREVIEWTAB = "OLDCREDITREVIEWTAB";
	public static final String NEW_CREDITREVIEWTAB = "NEWCREDITREVIEWTAB";
	public static final String EXT_CREDITREVIEWTAB = "EXTCREDITREVIEWTAB";
	// Credit Review Inside Tab Names
	public static final String BALANCE_SHEET = "Balance Sheet";
	public static final String PROFIT_AND_LOSS = "Profit and Loss";
	public static final String SUMMARY_AND_RATIOS = "Summary and Ratios";

	public static final String CHEQUE_AC_TYPE_NRE = "13";
	public static final String CHEQUE_AC_TYPE_NRO = "14";

	public static final String FIN_CLOSE_STATUS_WRITEOFF = "W";
	// CD Schemes
	public static final String DBD_AND_MBD_SEPARATELY = "SEPRT";
	public static final String DBD_AND_MBD_TOGETHER = "TOGTHR";
	public static final String DBD_PERCENTAGE_CALCULATED_ON = "INVAMT";
	public static final String NPA_PAYMENT_APPORTIONMENT_YES = "Y";
	public static final String NPA_PAYMENT_APPORTIONMENT_NO = "N";
	public static final String SELECT_LABEL = "---Select---";
	// Emi Clearance
	public static final String WAITING_CLEARANCE = "WTCLR";
	public static final String CLEARED = "CLRD";
	public static final String BOUNCED = "BNCD";
	public static final String COVENANTS_UPLOADBY_REFERENCE = "COVENANTS_UPLOADBY_REFERENCE";
	public static final String NEWCOVENANTS_UPLOADBY_REFERENCE = "NEWCOVENANTS_UPLOADBY_REFERENCE";

	public static boolean EOD_DELAY_REQ = false;

	// OCR Static List Constants
	public static final String AGGREMENT_VALUE = "AGG";
	public static final String DOCUMENT_VALUE = "DOC";

	public static final String PRORATA_VALUE = "PRORATA";
	public static final String SEGMENTED_VALUE = "SEGMENTED";

	public static final String CUSTOMER_CONTRIBUTION = "Customer";
	public static final String FINANCER_CONTRIBUTION = "Financer";

	public static final String MODULE_NAME = "Project";
	public static final String PROJECT_DOC = "PROJECTDOC";
	// Project Types
	public static final String RESIDENTIAL = "Residential";
	public static final String COMMERCIAL = "Commercial";
	public static final String MIXED_USE = "Mixed Use";
	public static final String AWAITED = "A";

	public static final String PERC_TYPE_FIXED = "F";
	public static final String PERC_TYPE_VARIABLE = "V";

	// Unit Types
	public static final String FLAT = "Flat";
	public static final String INDEPENDENTHOUSE = "Independent House";
	public static final String CARPET_AREA = "Carpet Area";
	public static final String BUILTUP_AREA = "Built-up Area";
	public static final String SUPERBUILTUP_AREA = "Super Built-up Area";
	public static final String CARPET_AREA_RATE = "Rate as per Carpet Area";
	public static final String BUILTUP_AREA_RATE = "Rate as per Built up area";
	public static final String SUPERBUILTUP_AREA_RATE = "Rate as per Super Built up area";
	public static final String BRANCH_APF_RATE = "Rate as per Branch APF";
	public static final String COST_SHEET_RATE = "Rate as per Cost Sheet";
	public static final String RATE_PER_SQUARE_FEET = "Recommended base rate per sq ft";

	// Employer Category
	public static final String EMP_CATEGORY_NOTCAT = "NOTCAT";
	public static final String EMP_CATEGORY_CATA = "CATA";
	public static final String EMP_CATEGORY_CATB = "CATB";
	public static final String EMP_CATEGORY_CATC = "CATC";
	public static final String EMP_CATEGORY_CATD = "CATD";
	// Type of APF
	public static final String AUTO = "Auto";
	public static final String DEEMED = "Deemed";
	public static final String GENERAL = "General";

	// Piramal Page Ext
	public static final String PIRAMAL = "_Piramal";

	// Income Categories
	public static final String INC_CATEGORY_SALARY = "SALARY";
	public static final String INC_CATEGORY_OTHER = "OTHERS";
	// De-dupe doc type codes
	public static final String VOTER_ID = "VOTER_ID";
	public static final String DRIVING_LICENCE = "DRIVING_LICENCE";
	public static final String DOC_TYPE = "DOC_TYPE";
	// Loan purpose type codes
	public static final String ALL = "ALL";
	public static final String SPECIFIC = "SPECIFIC";
	public static final String NOTREQUIRED = "NOTREQUIRED";
	// Up front fee receipt cancel Doc module
	public static final String FEE_DOC_MODULE_NAME = "Fee Cancellation";
	public static final String PAYABLE_ADVISE_DOC_MODULE_NAME = "Payable Advise";
	public static final String BLACKLISTCUSTOMER = "BLACKLISTCUSTOMER";
	public static final String REQ_TV_STATUS_CODES = "REQ_TV_STATUS_CODES";
	public static final String ACC_TYPE = "ACC_TYPE";
	public static final String CERSAI_IMPORT = "CERSAI_IMPORT";
	public static final String PHONE_TYPE = "PHONE_TYPE";
	// Covenant Details on Loan queue
	public static final String COVENANT_PDD = "PDD";
	public static final String COVENANT_OTC = "OTC";
	public static final String COVENANT_LOS = "LOS";

	// Step Types
	public static final String SUPER_LOAN_LOWER_EMI = "SUPER LOAN-LOWER EMI";
	public static final String SUPER_LOAN_HIGHER_LOAN = "SUPER LOAN-HIGHER LOAN";
	public static final String ADVANTAGE = "ADVANTAGE";

	public static final String method_save = "SAVE";
	public static final String method_Update = "UPDATE";
	public static final int NUMBER_OF_TERMS_LENGTH = 4;

	// TDS
	public static final String TDS_AUTO = "AUTO";
	public static final String TDS_MANUAL = "MANUAL";
	public static final String TDS_USER_SELECTION = "USER";

	// stepping details
	public static final String STEPPING_CALC_AMT = "AMT";
	public static final String STEPPING_CALC_PERC = "PERC";
	public static final String STEPPING_APPLIED_GRC = "GRCPRD";
	public static final String STEPPING_APPLIED_EMI = "REGEMI";
	public static final String STEPPING_APPLIED_BOTH = "BOTH";

	public static final String STEP_SPECIFIER_GRACE = "G";
	public static final String STEP_SPECIFIER_REG_EMI = "R";

	// ### START SFA_20210405 -->
	public static final int RECEIVABLE = 1; // Receivable
	public static final int PAYABLE = 2; // Payable
	public static final String RESULT = "Result=";
	public static final String BULKING = "(BULKING)";
	// ### END SFA_20210405 <--

	public static final String MODULETYPE_MAINT = "MAINT";
	public static final String INSTRUMENT_TYPE_PARTNER_BANK = "INSTRUMENT_TYPE_PARTNER_BANK";
	public static final String INSTRUMENT_TYPE = "INSTRUMENT_TYPE";
	// Tds Certificate Manaagement
	public static final String RECEIVABLE_CANCEL = "C";
	public static final String RECEIVABLE_ADJUSTMENT_MODULE = "TXN_ADJ";
	public static final String RECEIVABLE_CANCEL_MODULE = "REC_CANCEL";
	public static final String RECEIVABLE_ADJUSTMENT_CNCL_MODULE = "TXN_CANCEL";
	public static final String RECEIVABLE_ENQUIRY_MODULE = "ADJ_ENQUIRY";

	// Schedule Type
	public static final String MANUALSCHEDULETYPE_SCREEN = "S";
	public static final String MANUALSCHEDULETYPE_UPLOAD = "U";
	// Cashbackdetail
	public static final String MBD_RETAINED = "MBD";
	public static final String DBD_RETAINED = "DBD";
	public static final String DBMBD_RETAINED = "DBMBD";

	// Manual Advise Management
	public static final String MANUALADVISE_CANCEL = "C";
	public static final String MANUALADVISE_MAINTAIN = "M";

	public static final String MANUALADVISE_CREATE_MODULE = "MA_CREATE";
	public static final String MANUALADVISE_ENQUIRY_MODULE = "MA_ENQIRY";
	public static final String MANUALADVISE_MAINTAIN_MODULE = "MA_MAINTAIN";
	public static final String MANUALADVISE_CANCEL_MODULE = "MA_CANCEL";
	public static final String BLOCK_LIMIT_TYPE = "AUTO";

	// Report Type
	public static final String AVERAGE_YIELD_LOAN_REPORT = "Loan";
	public static final String AVERAGE_YIELD_PRODUCT_REPORT = "Product";

	public static final String PFF_CUSTCTG_IND = "Individual";
	public static final String PFF_CUSTCTG_NON_IND = "Non-Individual";

	// Posidex UCIC Type
	public static final String UCIC_NEW = "NEW";
	public static final String UCIC_EXISTING = "EXISTING";
	public static final String UCIC_RETRIGGER = "RETRIGGER";

	public static final String PREPYMT_CALCTN_TYPE_FIXEDAMT = "FIXEDAMT";
	public static final String PREPYMT_CALCTN_TYPE_PERCENTAGE = "PERCENTG";
	public static final String PREPYMT_CALCTN_TYPE_MIN_EMI = "MINEMI";
	public static final String PREPYMT_CALCTN_TYPE_MIN_POS_AMT = "POSAMT";
	public static final String PREPYMT_CALCULATEDON_SANCTIONLOANAMOUNT = "SANLAMT";

	public static final int FEE_REFUND_APPROVAL_FAILED = 0;
	public static final int FEE_REFUND_APPROVAL_SUCCESS = 1;
	public static final int FEE_REFUND_APPROVAL_HOLD = 2;
	public static final int FEE_REFUND_APPROVAL_DOWNLOADED = 3;
}
