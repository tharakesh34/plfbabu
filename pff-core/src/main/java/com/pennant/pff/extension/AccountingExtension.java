package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class AccountingExtension {

	private AccountingExtension() {
		super();
	}

	public static boolean LOAN_TYPE_GL_MAPPING;
	public static boolean NORMAL_GL_MAPPING;

	static {
		LOAN_TYPE_GL_MAPPING = getValueAsBoolean("LOAN_TYPE_GL_MAPPING", true);
		NORMAL_GL_MAPPING = getValueAsBoolean("NORMAL_GL_MAPPING", true);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.ACCOUNTING, key, defaultValue);
	}

}
