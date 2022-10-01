package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;

public class MandateExtension {
	private MandateExtension() {
		super();
	}

	private static final String MODULE = "MANDATE";

	/**
	 * Feature extension to allow the mandate approval even the loan in origination.
	 */
	public static boolean APPROVE_ON_LOAN_ORG;

	/**
	 * Feature extension whether the Entity-Code is mandatory or not for mandate response file upload. .
	 */
	public static boolean UPLOAD_ENITITY_CODE_MANDATORY;

	/**
	 * Feature extension for mandate registration request auto download
	 */
	public static boolean AUTO_DOWNLOAD;

	/**
	 * Feature extension for mandate registration response auto upload.
	 */
	public static boolean AUTO_UPLOAD;

	/**
	 * Feature extension to allow Co-Applicants for mandate creation in loan queue.
	 */
	public static boolean ALLOW_CO_APP;

	/**
	 * Feature extension to extract the registration file against to partner bank wise
	 */
	public static boolean PARTNER_BANK_WISE_EXTARCTION;

	/**
	 * Feature extension to change the frequency description in registration file
	 */
	public static boolean FRQ_DES_CHANGE_ON_EXTRACTION;

	static {
		APPROVE_ON_LOAN_ORG = getValueAsBoolean("APPROVE_ON_LOAN_ORG", false);
		UPLOAD_ENITITY_CODE_MANDATORY = getValueAsBoolean("UPLOAD_ENITITY_CODE_MANDATORY", false);
		AUTO_DOWNLOAD = getValueAsBoolean("AUTO_DOWNLOAD", false);
		AUTO_UPLOAD = getValueAsBoolean("AUTO_UPLOAD", false);
		ALLOW_CO_APP = getValueAsBoolean("ALLOW_CO_APP", false);
		PARTNER_BANK_WISE_EXTARCTION = getValueAsBoolean("PARTNER_BANK_WISE_EXTARCTION", false);
		FRQ_DES_CHANGE_ON_EXTRACTION = getValueAsBoolean("FRQ_DES_CHANGE_ON_EXTRACTION", false);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(MODULE, key, defaultValue);
	}
}
