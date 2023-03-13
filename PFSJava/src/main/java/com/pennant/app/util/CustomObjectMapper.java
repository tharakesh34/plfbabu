// customer object mapper
package com.pennant.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.pff.databind.JsonMapperUtil;

/**
 * The mapper provides required additional functionality for converting between Java objects and matching JSON/XML
 * constructs.
 */
public class CustomObjectMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;

	public CustomObjectMapper() {
		super(JsonMapperUtil.objectMapper());
	}
}