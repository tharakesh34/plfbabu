package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class MandateExtension {
	private MandateExtension() {
		super();
	}

	/**
	 * Feature extension to allow the mandate approval even the loan in origination.
	 */
	public static boolean APPROVE_ON_LOAN_ORG = getValueAsBoolean("APPROVE_ON_LOAN_ORG", false);

	/**
	 * Feature extension whether the Entity-Code is mandatory or not for mandate response file upload. .
	 */
	public static boolean UPLOAD_ENITITY_CODE_MANDATORY = getValueAsBoolean("UPLOAD_ENITITY_CODE_MANDATORY", false);

	/**
	 * Feature extension for mandate registration request auto download
	 */
	public static boolean AUTO_DOWNLOAD = getValueAsBoolean("AUTO_DOWNLOAD", false);

	/**
	 * Feature extension for mandate registration response auto upload.
	 */
	public static boolean AUTO_UPLOAD = getValueAsBoolean("AUTO_UPLOAD", false);

	/**
	 * Feature extension to allow Co-Applicants for mandate creation in loan queue.
	 */
	public static boolean ALLOW_CO_APP = getValueAsBoolean("ALLOW_CO_APP", false);

	/**
	 * Feature extension to extract the registration file against to partner bank wise
	 */
	public static boolean PARTNER_BANK_WISE_EXTARCTION = getValueAsBoolean("PARTNER_BANK_WISE_EXTARCTION", false);

	/**
	 * Feature extension to change the frequency description in registration file
	 */
	public static boolean FRQ_DES_CHANGE_ON_EXTRACTION = getValueAsBoolean("FRQ_DES_CHANGE_ON_EXTRACTION", false);

	/**
	 * Feature extension to capture partner bank in mandate creation
	 */
	public static boolean PARTNER_BANK_REQ = getValueAsBoolean("PARTNER_BANK_REQ", false);

	/**
	 * Feature extension to capture partner bank in mandate creation
	 */
	public static boolean SWAP_EFFECTIVE_DATE_DEFAULT = getValueAsBoolean("SWAP_EFFECTIVE_DATE_DEFAULT", false);

	/**
	 * Feature extension to capture partner bank in mandate creation
	 */
	public static boolean ACCOUNT_DETAILS_READONLY = getValueAsBoolean("ACCOUNT_DETAILS_READONLY", false);

	/**
	 * Feature extension to for Consecutive Hold Reason
	 */
	public static String CONSECUTIVE_HOLD_REASON = "CONSECUTIVE BOUNCE";

	public static boolean ALLOW_CONSECUTIVE_BOUNCE = getValueAsBoolean("ALLOW_CONSECUTIVE_BOUNCE", false);

	/**
	 * Feature Extension to no mandate the expiry date
	 */
	public static boolean EXPIRY_DATE_MANDATORY = getValueAsBoolean("EXPIRY_DATE_MANDATORY", true);

	public static int MANDATE_SPLIT_COUNT = getValueAsInt("MANDATE_SPLIT_COUNT", 0);;

	public static boolean BR_INST_TYPE_MAN = getValueAsBoolean("BR_INST_TYPE_MAN", false);

	public static boolean ALLOW_LIEN_MARKING = getValueAsBoolean("ALLOW_LIEN_MARKING", false);

	public static boolean ALLOW_HOLD_MARKING = getValueAsBoolean("ALLOW_HOLD_MARKING", false);

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.MANDATE, key, defaultValue);
	}

	public static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.MANDATE, key, defaultValue);
	}
}
