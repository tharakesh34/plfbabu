package com.pennanttech.extension.implementation;

import java.util.Map;

public interface IFeatureExtension {
	Map<String, Object> getCustomConstants();

	Map<String, Object> getMandateExtensions();
}
