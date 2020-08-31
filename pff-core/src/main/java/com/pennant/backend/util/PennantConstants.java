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
 * This stores all constants required for running the application
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
	public static final String DBDateFormat = "yyyy-MM-dd";
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
	public static final String DFT_THREAD_COUNT = "BULKRATECHANGE_DFT_THREAD_COUNT";

	// List Maintenance Workflow Record Type codes
	public static final String RCD_ADD = "ADD";
	public static final String RCD_SAV = "SAVE";
	public static final String RCD_UPD = "MAINTAIN";
	public static final String RCD_DEL = "DELETE";

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
	public static final String method_doPoliceCase = "doPoliceCase";
	public static final String method_CheckLimits = "CheckLimits";
	public static final String method_doCheckExceptions = "doCheckExceptions";
	public static final String method_doSendNotification = "doSendNotification";
	public static final String method_doCheckLimits = "doCheckLimits";
	public static final String method_doConfirm = "doConfirm";
	public static final String method_RejectStatus = "rejectStatus";
	public static final String method_doClearQueues = "doClearQueues";
	public static final String method_doFundsAvailConfirmed = "doFundsAvailConfirmed";
	public static final String method_doCheckProspectCustomer = "doCheckProspectCustomer";
	public static final String method_doCheckDeviations = "doCheckDeviations";
	public static final String method_doCheckAuthLimit = "doCheckAuthLimit";
	public static final String method_doCheckScore = "doCheckScore";
	public static final String method_doCheckLPOApproval = "doCheckLPOApproval";
	public static final String WF_SEND_LPO_REQUETS = "LPORequest";
	public static final String method_doCheckSMECustomer = "doCheckSMECustomer";
	public static final String method_doCheckCADRequired = "doCheckCADRequired";
	public static final String method_sendDDARequest = "sendDDARequest";
	public static final String method_DDAMaintenance = "ddaMaintenance";
	public static final String method_checkDDAResponse = "checkDDAResponse";
	public static final String method_doCheckShariaRequired = "doCheckShariaRequired";
	public static final String method_doCheckPaymentToBank = "doCheckPaymentToBank";
	public static final String method_doCheckOtherPayments = "doCheckOtherPayments";
	public static final String method_doCheckCollaterals = "doCheckCollaterals";
	public static final String method_doCheckDepositProc = "doCheckDeposit";
	public static final String method_doCheckFurtherWF = "doCheckFurtherWF";

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

	// Application Level label Definitions, can be moved to labels TODO
	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String ALLOW_ACCESS_TO_APP = "ALLOW_ACCESS"; // TODO
																		// What
																		// are
																		// the
																		// other
																		// options?

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

	public static final String DOC_TYPE_CODE_PHOTO = "CUSTPHOTO";

	public static final String DOC_TYPE_PDF_EXT = ".pdf";
	public static final String DOC_TYPE_WORD_EXT = ".docx";

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

	// Customer De-dup Field Details
	public static final String CUST_DEDUP_LIST_FIELDS = "CustCIF,CustShrtName,CustCtgCode,CustDOB,CustNationality";
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

	// This code is Considered as EID for AHB or As CPR for AIB
	public static final String CPRCODE = "01";
	public static final String PASSPORT = "02";
	public static final String TRADELICENSE = "15";
	public static final String PANNUMBER = "03";

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

	public static final String IBD_Branch = "2010";// TODO

	public static final String EXPENSE_FOR_EDUCATION = "E";
	public static final String EXPENSE_FOR_ADVANCEBILLING = "A";

	// LPO Documents for multiple agreements generation
	public static final String DOCCTG_LPO = "LPOISSUE";
	public static final String DOCCTG_LPO_FLEET = "LPOISSUEFLEET";
	public static final String DOCCTG_LPO_R = "LPOREISSUE";

	// DDA Document Type Codes
	public static final String DOCCTG_DDA_FORM = "ECS";

	public static final String TAKEOVERAGRDATA = "TAKEOVER";
	public static final String ADVANCEPAYMENTAGRDATA = "ADVANCEPAY";
	public static final String JOINSCUSTAGRDATA = "JOINTCUST";
	public static final String LPOFORFLEETVEHICLE_DEALER = "LPOFLTDLR";
	public static final String LPOFORFLEETVEHICLE_PRIVATE = "LPOFLTPRI";

	// Rating Type Details
	public static final String DEFAULT_RATE_TYPE = "MOODY";
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

	// Account Holding Type
	public static final String HOLDTYPE_OVERDUE = "P";
	public static final String HOLDTYPE_FUTURE = "F";

	public static final String KYC_PRIORITY_VERY_HIGH = "5";
	public static final String KYC_PRIORITY_HIGH = "4";
	public static final String KYC_PRIORITY_MEDIUM = "3";
	public static final String KYC_PRIORITY_NORMAL = "2";
	public static final String KYC_PRIORITY_LOW = "1";

	// Car Loan Details
	public static final String AHBACCOUNT = "AHBACC";
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

	public static final String COMMISSION_TYPE_FLAT = "F";
	public static final String COMMISSION_TYPE_PERCENTAGE = "P";

	public static final Double SQUAREFEET = 0.09290304;
	public static final Double SQUAREMETER = 10.7639;

	public static final String COMMODITY_SOLD = "SOLD";
	public static final String COMMODITY_ALLOCATED = "ALLOCATED";
	public static final String COMMODITY_CANCELLED = "CANCELLED";

	public static final String HOLIDAY_CATEGORY_DEFAULT = "NOTAPP";

	public static final String AGREEMENT_GEN = "UAE Master Commodity Murabaha Agreement (Revolving VIP).docx";
	public static final String AGREEMENT_IJA = "UAE Master Commodity Murabaha Agreement (Revolving).docx";
	public static final String AGREEMENT_Master_SCM = "UAE Master Commodity Murabaha Agreement (Term VIP).docx";
	public static final String AGREEMENT_LEASE = "UAE Master Commodity Murabaha Agreement (Term).docx";

	public static final String AGREEMENT_WAKALA = "SCHEDULE5";
	public static final String AGREEMENT_MURABAHA_FAC = "SCHEDULE6";
	public static final String AGREEMENT_UAE_MASTER_COM_MUR_RE_VIP = "SCHEDULE7";

	public static final String COMM_MUR = "COMM_MUR";
	public static final String STRU_MUR = "STRU_MUR";
	public static final String QARD_HAN = "QARD_HAN";
	public static final String CORP_MUR = "CORP_MUR";
	public static final String CORP_MUD = "CORP_MUD";
	public static final String WAKALA = "WAKALA";
	public static final String DIM_MUSH = "DIM_MUSH";
	public static final String IJARAH = "IJARAH";
	public static final String TAWARRUQ = "TAWARRUQ";

	public static final String PO_AUTHORIZATION_AUTHORIZE = "A";
	public static final String PO_AUTHORIZATION_DECLINE = "D";
	public static final String PO_AUTHORIZATION_CLOSE = "C";
	public static final String PO_AUTHORIZATION_CHEQUE_ISSUED = "I";
	public static final String PO_AUTHORIZATION_CHEQUE_REJECTED = "R";

	public static final String POAUTH_ROLE_TCS = "PO_AUTHORIZATION_TCS";
	public static final String POAUTH_ROLE_CHEQUE_ISSUE = "PO_AUTHORIZATION_CHEQUE_ISSUE";
	public static final String CONSTRUCTIONSTAGE = "CONSTSTAGE";
	public static final String USAGE = "USAGE";
	public static final String MORTSTATUS = "MORTSTATUS";

	public static final String ACCOUNTTYPE_CA = "CA";
	public static final String ACCOUNTTYPE_SA = "SA";

	public static final String REQ_TYPE_VALIDATE = "VALIDATE";
	public static final String REQ_TYPE_REG = "REGISTRATION";
	public static final String REQ_TYPE_CAN = "CANCELLATION";
	public static final String RES_TYPE_SUCCESS = "0000";
	public static final String DDA_ACK_REJECTED = "ACK";

	public static final String DDA_ACK = "ACK";
	public static final String DDA_NAK = "NAK";
	public static final String DDA_REJECTED = "REJECTED";
	public static final String DDA_PENDING = "PENDING";
	public static final String DDA_PENDING_CODE = "PTIDDS04";

	public static final String MORTGAGE_TRANS_TYPE = "NEW";
	public static final String DDA_ACK_APPROVED = "ACKAPPROVED";

	// Card Types
	public static final String CARDTYPE_CLASSIC = "C";
	public static final String CARDTYPE_GOLD = "G";
	public static final String CARDTYPE_PLATINUM = "P";
	public static final String CARDTYPE_NIL = "N";

	// Card Class Types
	public static final String CARDCLASS_UJARAH = "U";
	public static final String CARDCLASS_FREEFORLIFE = "F";
	public static final String CARDCLASS_QIBLA = "Q";
	public static final String CARDCLASS_LAHA = "L";
	public static final String CARDCLASS_NIL = "N";

	public static final String CUSTOMERSTATUS_NORMAL = "N";
	public static final String CUSTOMERSTATUS_VIP = "V";
	public static final String MORTGAGE_SOURCE_CODE = "MORT_SOURCE_CODE";

	public static final String EVALUATION_STATUS_PENDING = "P";
	public static final String EVALUATION_STATUS_VALUATOR = "V";
	public static final String EVALUATION_STATUS_COMPLETED = "C";
	public static final String EVALUATION_STATUS_NOTPROGRESSED = "N";

	public static final String ADDRESS_TYPE_RESIDENCE = "CURRES";
	public static final String LEGEL_FEES = "Legal Fees";
	public static final String FINES = "Fines";
	public static final String OTHERS = "Others";
	public static final int defaultCCYDecPos = 2;

	public static final String EASYBUY = "EASYBUY";
	public static final String REFINANCE = "REFINANC";
	public static final String SWITCHOVER = "SWTCHOVR";
	public static final String TOPUP = "TOPUP";
	public static final String LANDANDCONSTRUTON = "LANDCONS";
	public static final String CONSTRUCTIONONLY = "CONSTRTN";
	public static final String GOVERNMENTHOUSINGSCHEME = "GOVNSCHM";

	public static final String SELFUSE = "SELFUSE";
	public static final String INVESTMENTUSE = "INVSTMNT";
	public static final String COMMERCIALINVESTMENTUSE = "COMMINVS";

	public static final String FREEHOLD = "FREEHOLD";
	public static final String LEASEHOLD = "LEASEHLD";
	public static final String FREEHOLDANDGIFETEDLAND = "FREEGIFT";
	public static final String GIFTEDLAND = "GIFTLAND";

	public static final String APARTMENT = "APARTMNT";
	public static final String VILLA = "VILLA";
	public static final String COMPOUNDVILLAS = "COMPNDVL";
	public static final String TOWNHOUSES = "TOWNHOUS";
	public static final String LAND = "LAND";

	public static final String PRIMARY = "PRIMARY";
	public static final String SECONDARY = "SECONDRY";
	public static final String PRIVATEVILLA = "PRIVTVIL";

	public static final String NORMAL = "NORMAL";
	public static final String URGENT = "URGENT";

	public static final String RENT = "RENT";
	public static final String SALARY = "SALINCM";
	public static final String SALARYINCOMERENT = "SALINRNT";
	public static final String PENSION = "PENSION";

	public static final String FIRSTDEGREE = "FIRSTDEG";
	public static final String SECONDDEGREE = "SECONDDG";
	public static final String THIRDDEGREE = "THIRDDEG";
	public static final String AHBREGISTERED = "AHBREGIS";
	public static final String IJARAHAREGISTERD = "IJARREGS";
	public static final String TRIPARTITEASSIGNMENT = "TRIPASAG";

	public static final String SOLE = "SOLE";
	public static final String JOINT = "JOINT";
	public static final String FREEZONE = "FREEZONE";
	public static final String CORPORATION = "CORPRATN";
	public static final String LIMITEDPARTNERSHIP = "LIMPARSP";
	public static final String NONPROFITCORPORATION = "NONPFTCO";
	public static final String LIMITEDLIABILITYCOMPANY = "LMTLBCMP";

	public static final String HOMEFINANCE = "HOMEFIN";
	public static final String CONSTRUCTIONFINANCE = "CONSTFIN";

	public static final String FULLPROPERTYMANAGEMENT = "FULLPROP";
	public static final String RENTALCOLLECTION = "RENTALCO";
	public static final String RENTALASSIGNMENT = "RENTALAS";
	public static final String FACILITYMANAGEMENT = "FACLTYMG";
	public static final String NOTAPPLICABLE = "NOTAPP";

	// Web Service Related
	public static final String MQ_SUCCESS_CODE = "0000";

	// National Bonds
	public static final String BOND_PURCHASE = "PURCHASE";
	public static final String BOND_TRANSFER_M = "TRANS_MAKER";
	public static final String BOND_TRANSFER_C = "TRANS_CHECKER";

	// Auto hunting
	public static final String AUTOHUNT_RUNNING = "R";
	public static final String AUTOHUNT_STOPPED = "S";
	public static final String AUTOHUNT_BATCH = "B";

	// Insurance Status Details
	public static final String TAKAFUL_STATUS_APPROVED = "APPROVED";
	public static final String TAKAFUL_STATUS_DECLINED = "DECLINED";
	public static final String TAKAFUL_STATUS_APPROVED_EXCEPTIONS = "APPEXCEP";

	public static final String TAKEOVERFROM_BANK = "BANK";
	public static final String TAKEOVERFROM_THIRDPARTY = "TRDPARTY";

	// Insurance CLAIM PAID STATUS
	public static final String TAKAFUL_PAIDSTATUS_PAID = "PAID";
	public static final String TAKAFUL_PAIDSTATUS_REJECTED = "REJECTED";
	public static final String TAKAFUL_PAIDSTATUS_PENDING = "PENDING";

	// Insurance Claim Reason
	public static final String TAKAFUL_CLAIMREASON_DEATH = "Disease";
	public static final String TAKAFUL_CLAIMREASON_PTD = "PTD";

	public static final String EVENTBASE = "FinEvent";
	public static final String ACCNO = "Account";
	public static final String POSTDATE = "PostDate";
	public static final String VALUEDATE = "ValueDate";
	public static boolean CHANGE_SEGMENT = true;

	public static final String HOME_PHONE = "HOMEPHN";
	public static final String OFFICE_PHONE = "OFFICE";
	public static final String CASDOC = "CASDOCUMENT";

	public static final String SHARIA_STATUS_NOTREQUIRED = "NotRequired";
	public static final String SHARIA_STATUS_PENDING = "Pending";
	public static final String SHARIA_STATUS_APPROVED = "Approved";
	public static final String SHARIA_STATUS_DECLINED = "Declined";
	public static final String PREAPPROVAL_TABLE_TYPE = "_PA";

	public static final String PO_STATUS_ISSUE = "I";
	public static final String PO_STATUS_PENDING = "P";

	public static final String SUSP_TRIG_AUTO = "AUTO";
	public static final String SUSP_TRIG_MAN = "MANUAL";

	public static final String TYPE_OF_VALUATION_FULL = "F";
	public static final String TYPE_OF_VALUATION_DRIVEBY = "R";
	public static final String TYPE_OF_VALUATION_DESKTOP = "D";
	public static final String TYPE_OF_VALUATION_INTERIM = "I";

	public static final String PROPERTY_STATUS_READY_FOR_HANDOVER = "R";
	public static final String PROPERTY_STATUS_COMPLETED_PROPERTY = "C";
	public static final String PROPERTY_STATUS_UNDER_CONSTRUCTION = "U";

	public static final String REU_DECISION_APPROVED = "A";
	public static final String REU_DECISION_APPROVED_SUBJECTTO = "S";
	public static final String REU_DECISION_DECLINE = "D";
	public static final String REU_DECISION_PENDING = "P";

	public static final String ADDRESS_TYPE_OFFICE = "OFFICE";

	public static final String FIXED_DEPOSIT = "FD";
	public static final String SECURITY_CHEQUE = "SC";

	// ManagerCheques
	public static final String FIN_MGRCHQ__CHQPURPOSECODE = "FINDISB";

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

	// Cheque Module Status
	public static final String CHEQUESTATUS_NEW = "NEW";
	public static final String CHEQUESTATUS_PRESENT = "PRESENT";
	public static final String CHEQUESTATUS_BOUNCE = "BOUNCE";
	public static final String CHEQUESTATUS_REALISE = "REALISE";
	public static final String CHEQUESTATUS_REALISED = "REALISED";
	public static final String CHEQUESTATUS_FAILED = "FAILED";
	public static final String CHEQUE_AC_TYPE_CA = "11";
	public static final String CHEQUE_AC_TYPE_SA = "10";
	public static final String CHEQUE_AC_TYPE_CC = "12";

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

	public static final String COLLATERAL_LTV_CHECK_DISBAMT = "LTVDISBAMT";
	public static final String COLLATERAL_LTV_CHECK_FINAMT = "LTVFINAMT";

	public static final boolean ALLOW_LOAN_APP_LOCK = false;

	public static final int RECEIPT_DEFAULT = 0;
	public static final int RECEIPT_DOWNLOADED = 1;
	public static final int RECEIPT_APPROVED = 2;
	public static final int RECEIPT_REJECTED = 3;

	// active and inactive
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	public static final String AVERAGE = "AVG";
	public static final String GOOD = "GOOD";
	public static final String VERYGOOD = "VGOOD";

	public static final String DSA = "DSA";
	public static final String PSF = "PSF";
	public static final String ONLINE = "ONL";
	public static final String OFFLINE = "OFFL";
	public static final String REFERRAL = "REF";
	public static final String NTB = "NTB";
	public static final String ASM = "ASM";
	public static final String KVI = "KVI";
	public static final String DEFAULT = "DEFAULT";

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

	public static final String PROCESS_PRESENTMENT = "P";
	public static final String PROCESS_REPRESENTMENT = "R";
	public static final int BULKPROCESSING_SIZE = 500;

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

	// Final Valuation Amount Decisions
	public static final String OK = "OK";
	public static final String NOTOK = "NOTOK";

	public static final String REASON_CODE_EARLYSETTLEMENT = "FC";
	// Temporary Purpose
	public static final String OLD_CREDITREVIEWTAB = "OLDCREDITREVIEWTAB";
	public static final String NEW_CREDITREVIEWTAB = "NEWCREDITREVIEWTAB";
	// Credit Review Inside Tab Names
	public static final String BALANCE_SHEET = "Balance Sheet";
	public static final String PROFIT_AND_LOSS = "Profit and Loss";
	public static final String SUMMARY_AND_RATIOS = "Summary and Ratios";

	public static final String CHEQUE_AC_TYPE_NRE = "13";
	public static final String CHEQUE_AC_TYPE_NRO = "14";

	public static final String FIN_CLOSE_STATUS_WRITEOFF = "W";
	//CD Schemes
	public static final String DBD_AND_MBD_SEPARATELY = "SEPRT";
	public static final String DBD_AND_MBD_TOGETHER = "TOGTHR";
	public static final String DBD_PERCENTAGE_CALCULATED_ON = "INVAMT";
	public static final String NPA_PAYMENT_APPORTIONMENT_YES = "Y";
	public static final String NPA_PAYMENT_APPORTIONMENT_NO = "N";
	public static final String SELECT_LABEL = "---Select---";
	//Emi Clearance
	public static final String WAITING_CLEARANCE = "WTCLR";
	public static final String CLEARED = "CLRD";
	public static final String BOUNCED = "BNCD";
	public static final String COVENANTS_UPLOADBY_REFERENCE = "COVENANTS_UPLOADBY_REFERENCE";
	public static final String NEWCOVENANTS_UPLOADBY_REFERENCE = "NEWCOVENANTS_UPLOADBY_REFERENCE";

	public static boolean EOD_DELAY_REQ = false;

}
