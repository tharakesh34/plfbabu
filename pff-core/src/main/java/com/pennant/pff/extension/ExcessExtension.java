package com.pennant.pff.extension;

import java.util.Set;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;
import com.pennanttech.pff.receipt.constants.ExcessType;

public class ExcessExtension {

	private ExcessExtension() {
		super();
	}

	public static final Set<String> ALLOWED_ADJUSTMENTS = getValueAsSet("ALLOWED_ADJUSTMENTS",
			ExcessType.defaultAdjustToList());

	public static final Set<String> ALLOWED_KNOCKOFF_FROM = getValueAsSet("ALLOWED_KNOCKOFF_FROM",
			ExcessType.defaultKnockOffFromList());

	@SuppressWarnings("unchecked")
	private static Set<String> getValueAsSet(String key, Set<String> defaultValue) {
		return (Set<String>) FeatureExtension.getValueAsObject(Module.EXCESS, key, defaultValue);
	}
}
