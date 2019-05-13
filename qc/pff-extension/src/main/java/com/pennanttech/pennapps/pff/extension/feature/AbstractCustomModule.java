package com.pennanttech.pennapps.pff.extension.feature;

import java.util.HashMap;
import java.util.Map;

import com.pennanttech.pennapps.core.feature.model.ModuleMapping;

public abstract class AbstractCustomModule {
	private Map<String, ModuleMapping> customMappings = new HashMap<>();

	public Map<String, ModuleMapping> getCustomMappings() {
		return customMappings;
	}

	/**
	 * Registers the custom module mappings.
	 */
	protected abstract void register();
}
