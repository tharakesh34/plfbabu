package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class PresentmentExtension {
	private PresentmentExtension() {
		super();
	}

	/**
	 * Feature extension to create the receipts on due date or response upload, default value is true.
	 */
	public static boolean DUE_DATE_RECEIPT_CREATION;

	/**
	 * Feature extension to enable or disable auto extraction.
	 */
	public static boolean AUTO_EXTRACTION;

	/**
	 * Feature extension to enable or disable auto approval.
	 */
	public static boolean AUTO_APPROVAL;

	static {
		DUE_DATE_RECEIPT_CREATION = getValueAsBoolean("DUE_DATE_RECEIPT_CREATION", false);
		AUTO_EXTRACTION = getValueAsBoolean("AUTO_EXTRACTION", true);
		AUTO_APPROVAL = getValueAsBoolean("AUTO_APPROVAL", true);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.PRESENTMENT, key, defaultValue);
	}
}
