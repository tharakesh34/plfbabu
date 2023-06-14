package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class AccountingExtension {

	private AccountingExtension() {
		super();
	}

	public static final boolean LOAN_TYPE_GL_MAPPING = getValueAsBoolean("LOAN_TYPE_GL_MAPPING", true);
	public static final boolean NORMAL_GL_MAPPING = getValueAsBoolean("NORMAL_GL_MAPPING", true);
	public static final boolean VERIFY_ACCOUNTING = getValueAsBoolean("VERIFY_ACCOUNTING", true);
	public static final boolean LOAN_DOWNSIZING_ACCOUNTING_REQ = getValueAsBoolean("LOAN_DOWNSIZING_ACCOUNTING_REQ",
			false);

	public static final boolean ACCOUNTING_VALIDATION = getValueAsBoolean("ACCOUNTING_VALIDATION", false);
	public static final boolean IND_AS_ACCOUNTING_REQ = getValueAsBoolean("IND_AS_ACCOUNTING_REQ", false);

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.ACCOUNTING, key, defaultValue);
	}

}
