package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class CustomerExtension {
	private CustomerExtension() {
		super();
	}

	/**
	 * Feature extension to allow Customer Core Bank integration.
	 */
	public static boolean CUST_CORE_BANK_ID;

	static {
		CUST_CORE_BANK_ID = getValueAsBoolean("CUST_CORE_BANK_ID", false);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.CUSTOMER, key, defaultValue);
	}

	public static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.CUSTOMER, key, defaultValue);
	}
}
