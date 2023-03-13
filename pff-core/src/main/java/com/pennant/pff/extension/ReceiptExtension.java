package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class ReceiptExtension {
	private ReceiptExtension() {
		super();
	}

	/**
	 * Feature extension whether to allow back dated Early Settlement.
	 */
	public static boolean STOP_BACK_DATED_EARLY_SETTLE;

	static {
		STOP_BACK_DATED_EARLY_SETTLE = getValueAsBoolean("STOP_BACK_DATED_EARLY_SETTLE", true);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.RECEIPT, key, defaultValue);
	}

	public static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.RECEIPT, key, defaultValue);
	}
}
