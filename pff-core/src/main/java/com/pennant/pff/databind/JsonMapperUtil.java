package com.pennant.pff.databind;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.pennant.backend.util.PennantConstants;

public class JsonMapperUtil {
	private JsonMapperUtil() {
		super();
	}

	public static ObjectMapper objectMapper(String dateFormat) {
		SimpleDateFormat defaultDateFormat = new SimpleDateFormat(dateFormat);
		defaultDateFormat.setLenient(false);

		return JsonMapper.builder()

				.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

				.serializationInclusion(Include.NON_NULL)

				.defaultDateFormat(defaultDateFormat)

				.annotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()))

				.build();
	}

	public static ObjectMapper objectMapper() {
		return objectMapper(PennantConstants.APIDateFormatter);
	}
}
