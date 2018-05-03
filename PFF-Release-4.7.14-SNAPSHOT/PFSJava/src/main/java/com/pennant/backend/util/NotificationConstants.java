package com.pennant.backend.util;

public class NotificationConstants {

	private NotificationConstants() {
		super();
	}
	
	// Mail Template Configuration Type Codes
	public static final String TEMPLATE_FORMAT_PLAIN 				= "P";
	public static final String TEMPLATE_FORMAT_HTML 				= "H";
	public static final String DEFAULT_CHARSET 						= "UTF-16";
	public static final String TEMPLATE_FOR_CN 						= "CN";
	public static final String TEMPLATE_FOR_AE 						= "AE";
	public static final String TEMPLATE_FOR_DN 						= "DN";
	public static final String TEMPLATE_FOR_TP 						= "TP";
	public static final String TEMPLATE_FOR_QP	 					= "QP";
	public static final String TEMPLATE_FOR_GE 						= "GE";
	public static final String TEMPLATE_FOR_PO 						= "PO";
	public static final String TEMPLATE_FOR_TAT 					= "TA";
	public static final String TEMPLATE_FOR_LIMIT 					= "LT";
	public static final String	TEMPLATE_FOR_SP				        = "SP";
	
	// Module Code for Notification to maintain parameters
	public static final String MAIL_MODULE_FIN 						= "FIN";
	public static final String MAIL_MODULE_CAF 						= "CAF";
	public static final String MAIL_MODULE_CREDIT 					= "CRD";
	public static final String MAIL_MODULE_TREASURY 				= "TSR";
	public static final String MAIL_MODULE_PROVISION 				= "PRV";
	public static final String MAIL_MODULE_MANUALSUSPENSE 			= "MSP";
	public static final String MAIL_MODULE_POAUTHORIZATION 			= "POA";
	
	// TAT Alert Notification related
	public static final String TAT_ALT_CNT = "TAT_ALERT_COUNT";
	
	
}
