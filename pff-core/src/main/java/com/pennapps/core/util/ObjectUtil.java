package com.pennapps.core.util;

import java.math.BigDecimal;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennanttech.pennapps.core.AppException;

public class ObjectUtil {
	private ObjectUtil() {
		super();
	}

	public static String valueAsString(Object object) {
		return valueAsString(object, null);
	}

	public static String valueAsString(Object object, String defaultValue) {
		return (object != null) ? object.toString() : defaultValue;
	}

	public static int valueAsInt(Object object) {
		return valueAsInt(object, 0);
	}

	public static int valueAsInt(Object object, int defaultValue) {
		return (object != null) ? Integer.valueOf(object.toString()) : defaultValue;
	}

	public static Integer valueAsInteger(Object object) {
		return valueAsInt(object, 0);
	}

	public static Integer valueAsInteger(Object object, Integer defaultValue) {
		return (object != null) ? Integer.valueOf(object.toString()) : defaultValue;
	}

	public static long valueAslong(Object object) {
		return valueAsLong(object, Long.MIN_VALUE);
	}

	public static long valueAslong(Object object, Long defaultValue) {
		return (object != null) ? Long.valueOf(object.toString()) : defaultValue;
	}

	public static Long valueAsLong(Object object) {
		return valueAsLong(object, null);
	}

	public static Long valueAsLong(Object object, Long defaultValue) {
		return (object != null) ? Long.valueOf(object.toString()) : defaultValue;
	}

	public static BigDecimal valueAsBigDecimal(Object object) {
		return valueAsBigDecimal(object, BigDecimal.ZERO);
	}

	public static BigDecimal valueAsBigDecimal(Object object, BigDecimal defaultValue) {
		return (object != null) ? new BigDecimal(object.toString()) : defaultValue;
	}

	public static Date valueAsDate(Object object) {
		return valueAsDate(object, null);
	}

	public static Date valueAsDate(Object object, Date defaultValue) {
		return (object != null) ? (object instanceof Date ? (Date) object : defaultValue) : defaultValue;
	}

	public static <T> Object valueAsObject(MapSqlParameterSource parameter, T object) {
		ObjectMapper objectMapper = new ObjectMapper();

		JSONObject jsonObject = new JSONObject(parameter);

		JSONObject json = (JSONObject) jsonObject.get("values");

		try {
			return objectMapper.readValue(json.toString(), (Class<T>) object);
		} catch (JsonProcessingException e) {
			throw new AppException(e.getMessage());
		}
	}
}
