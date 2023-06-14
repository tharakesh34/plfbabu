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
	public static final boolean DUE_DATE_RECEIPT_CREATION = getValueAsBoolean("DUE_DATE_RECEIPT_CREATION", false);

	/**
	 * Feature extension to enable or disable auto extraction.
	 */
	public static final boolean AUTO_EXTRACTION = getValueAsBoolean("AUTO_EXTRACTION", true);

	/**
	 * Feature extension to enable or disable auto approval.
	 */
	public static final boolean AUTO_APPROVAL = getValueAsBoolean("AUTO_APPROVAL", true);

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.PRESENTMENT, key, defaultValue);
	}
}
