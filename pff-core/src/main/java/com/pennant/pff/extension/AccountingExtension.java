package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;

public class AccountingExtension {

	private AccountingExtension() {
		super();
	}

	private static final String MODULE = "ACCOUNTING";

	public static boolean LOAN_TYPE_GL_MAPPING;
	public static boolean NORMAL_GL_MAPPING;

	static {
		LOAN_TYPE_GL_MAPPING = getValueAsBoolean("LOAN_TYPE_GL_MAPPING", true);
		NORMAL_GL_MAPPING = getValueAsBoolean("NORMAL_GL_MAPPING", true);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(MODULE, key, defaultValue);
	}

}
