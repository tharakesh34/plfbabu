package com.pennant.app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.pennant.backend.util.PennantConstants;

/**
 * The mapper provides required additional functionality for converting between Java objects and matching JSON/XML
 * constructs.
 */
public class CustomObjectMapper extends ObjectMapper {
	public CustomObjectMapper() {
		configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat(PennantConstants.APIDateFormatter);
		dateFormat.setLenient(false);
		setDateFormat(dateFormat);
		setSerializationInclusion(Inclusion.NON_NULL);
		setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
