package com.pennanttech.pff.mandate;

import java.util.Map;

public interface ExtMandateExtension {
	void processMandateData(Map<String, Object> rowMap) throws Exception;
}
