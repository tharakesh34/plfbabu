package com.pennant.backend.util;

import java.nio.charset.StandardCharsets;

public class NotificationConstants {

	private NotificationConstants() {
		super();
	}

	public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

	public static final String TEMPLATE_FORMAT_PLAIN = "P";
	public static final String TEMPLATE_FORMAT_HTML = "H";
	public static final String TEMPLATE_FOR_CN = "CN";
	public static final String TEMPLATE_FOR_AE = "AE";
	public static final String TEMPLATE_FOR_DN = "DN";
	public static final String TEMPLATE_FOR_TP = "TP";
	public static final String TEMPLATE_FOR_QP = "QP";
	public static final String TEMPLATE_FOR_GE = "GE";
	public static final String TEMPLATE_FOR_PO = "PO";
	public static final String TEMPLATE_FOR_TAT = "TA";
	public static final String TEMPLATE_FOR_LIMIT = "LT";
	public static final String TEMPLATE_FOR_SP = "SP";
	public static final String TEMPLATE_FOR_DSAN = "DN";
	public static final String TEMPLATE_FOR_PVRN = "PN";
	public static final String TEMPLATE_FOR_OTP = "OTP";
	public static final String TEMPLATE_FOR_SU = "SU";
	public static final String TEMPLATE_FOR_UPD = "UPD";

	// Module Code for Notification to maintain parameters
	public static final String MAIL_MODULE_FIN = "FIN";
	public static final String MAIL_MODULE_CAF = "CAF";
	public static final String MAIL_MODULE_CREDIT = "CRD";
	public static final String MAIL_MODULE_PROVISION = "PRV";
	public static final String MAIL_MODULE_MANUALSUSPENSE = "MSP";
	public static final String MAIL_MODULE_POAUTHORIZATION = "POA";
	public static final String MAIL_MODULE_PROVIDER = "PVR";
	public static final String SYSTEM_NOTIFICATION = "SYS_NOTIFICATION";

	// TAT Alert Notification related
	public static final String TAT_ALT_CNT = "TAT_ALERT_COUNT";
	public static final String PRESENTMENT_BOUNCE_MAIL_NOTIFICATION = "PRESENTMENT_BOUNCE_MAIL_NOTIFICATION";
	public static final String PRESENTMENT_SUCCESS_MAIL_NOTIFICATION = "PRESENTMENT_SUCCESS_MAIL_NOTIFICATION";
	public static final String LIMITHEADER_SUCCESS_NOTIFICATION = "LIMITHEADER_SUCCESS_NOTIFICATION";
	public static final String ADD_RATE_CHANGE_NOTIFICATION = "ADD_RATE_CHANGE_NOTIFICATION";
	public static final String CREATE_LOAN_API_MAIL_NOTIFICATION = "CREATE_LOAN_NSTP_API_TEMP";
	public static final String LIMITHEADER_SUCCESS_NOTIFICATION_SMS = "43";

	// EOD Automation
	public static final String NONE = "NONE";
	public static final String SSL = "SSL";
	public static final String TLS = "TLS";
	public static final String AUTO = "AUTO";
}