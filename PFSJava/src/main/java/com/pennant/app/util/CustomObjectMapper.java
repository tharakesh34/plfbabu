package com.pennant.app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.PennantConstants;

/**
 * The mapper provides required additional functionality for converting between Java objects and matching JSON/XML
 * constructs.
 */
public class CustomObjectMapper extends ObjectMapper {
	public CustomObjectMapper() {
		configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat(PennantConstants.APIDateFormatter);
		dateFormat.setLenient(false);
		setDateFormat(dateFormat);
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		addMixIn(AuditDetail.class, AuditDetail.class);
	}
}
