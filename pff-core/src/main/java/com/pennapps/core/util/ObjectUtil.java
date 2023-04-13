package com.pennapps.core.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

public class ObjectUtil {
	private ObjectUtil() {
		super();
	}

	public static <T extends Serializable> T clone(T object) {
		return SerializationUtils.clone(object);
	}

	public static <T extends Serializable> List<T> clone(List<T> objects) {
		List<T> list = new ArrayList<>();

		for (T object : objects) {
			clone(object);
		}

		return list;
	}

	public static <T extends Serializable> Map<Long, T> clone(Map<Long, T> objects) {
		Map<Long, T> map = new HashMap<>();

		return map;
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

}
