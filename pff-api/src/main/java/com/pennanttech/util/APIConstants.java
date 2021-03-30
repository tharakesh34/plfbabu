package com.pennanttech.util;

public class APIConstants {

	// Request Types
	public static final String REQTYPE_INQUIRY = "Inquiry";
	public static final String REQTYPE_POST = "Post";

	// Response codes
	public static final String RES_SUCCESS_CODE = "0000";
	public static final String RES_SUCCESS_DESC = "Success";
	public static final String RES_FAILURE_DESC = "Failure";
	public static final String RES_DUPLICATE_MSDID_CODE = "9998";
	public static final String RES_DUPLICATE_MSDID = "Duplicate message id";

	public static final String RES_FAILED_CODE = "9999";
	public static final String RES_FAILED_DESC = "Unable to process request.";

	// Finance or WIFFinance
	public static final String FINANCE_ORIGINATION = "Origination";
	public static final String FINANCE_WIF = "WIF";

	// FinSource Id
	public static final String FINSOURCE_ID_API = "API";

	// Get Finance Inquiry
	public static final String FINANCE_INQUIRY_CUSTOMER = "CUSTOMER";

	// Disbursement status
	public static final String FIN_DISB_FULLY = "Fully Disbursed";
	public static final String FIN_DISB_PARTIAL = "Partially Disbursed";

	// Statement related
	public static final String CLOSE_STATUS_ACTIVE = "A";
	public static final String STMT_INST_CERT = "INSTCERT";
	public static final String STMT_ACCOUNT = "STMTACC";
	public static final String STMT_REPAY_SCHD = "REPYSCHD";
	public static final String STMT_NOC = "NOC";
	public static final String STMT_FORECLOSURE = "FORECLOSURE";
	public static final String SERVICE_TYPE_CREATE = "Create";
	public static final String SERVICE_TYPE_UPDATE = "Update";
	public static final String REPORT_SOA = "SOA";
	public static final String REPORT_TEMPLATE_APPLICATION = "Format1";
	public static final String REPORT_TEMPLATE_API = "Format2";
	public static final String CUST_AGR_NAME = "SANCTIONAGREEMENT";
	public static final String STMT_FORECLOSUREV1 = "FORECLOSUREV1";
	
	public static final String FIN_WEL_LETTER = "WELCOMELETR";
	public static final String FIN_SANC_LETTER = "SNCTNLTR";

	public static final String REPORT_SOA_REPORT = "SOAREPORT";
	public static final String STMT_REPAY_SCHD_REPORT = "REPYSCHDREPORT";
	public static final String STMT_NOC_REPORT = "NOCREPORT";
	public static final String STMT_INST_CERT_REPORT = "INSTCERTREPORT";
	public static final String STMT_FORECLOSURE_REPORT = "FORECLOSUREREPORT";
	public static final String STMT_PROV_INST_CERT_REPORT = "PROVINSTCERTREPORT";

	// Get SRM Customer Details
	public static final String SRM_SOURCE = "SRM";
	public static final String SRM_CUSTOMER_TYPE = "Customer";
	public static final String SRM_MOBILE_TYPE = "Phone";
	public static final String SRM_EMAIL_TYPE = "Email";
	public static final String SRM_LOAN_TYPE = "Loan";
	public static final String COB_SOURCE = "COB";

	public static final String COVENANT_MODULE_NAME = "Loan";

	//verifications
	public static final int FI = 1;
	public static final int TV = 2;
	public static final int LV = 3;
	public static final int RCU = 4;
	public static final int PD = 5;
}
