package com.pennanttech.util;

public class APIConstants {

	// Request Types
	public static final String REQTYPE_INQUIRY = "Inquiry";
	public static final String REQTYPE_POST = "Post";
	
	// Response codes
	public static final String RES_SUCCESS_CODE = "0000";
	public static final String RES_SUCCESS_DESC = "Success";
	
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

}
