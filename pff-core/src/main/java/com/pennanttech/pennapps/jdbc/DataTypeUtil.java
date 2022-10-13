/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.jdbc;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link DataType}.
 * </p>
 */
public final class DataTypeUtil {
	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private DataTypeUtil() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Get the value with the specified data type.
	 * 
	 * @param value The value.
	 * @param type  The required data type.
	 * @return The value with the specified data type.
	 */
	public static Object getValueAsObject(String value, DataType type) {
		value = StringUtils.trimToNull(value);

		if (value == null) {
			return null;
		}

		try {
			if (type == DataType.LONG) {
				if (StringUtils.isNumeric(value)) {
					return Long.valueOf(value);
				} else {
					return null;
				}
			} else {
				return value;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Field[] getDeclaredFields(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	private static Field[] getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();

		do {
			for (Field field : getDeclaredFields(clazz)) {
				fields.add(field);
			}
			clazz = clazz.getSuperclass();
		} while (clazz instanceof Object);

		return fields.toArray(new Field[] {});
	}

	public static Object getValueAsObject(String fieldName, String value, Class<?> clazz) {
		value = StringUtils.trimToNull(value);

		if (value == null) {
			return null;
		}

		Field[] fields = getFields(clazz);

		for (Field field : fields) {
			if (StringUtils.equalsIgnoreCase(fieldName, field.getName())) {
				String fieldType = field.getType().getSimpleName();
				if (DataType.valueOf(fieldType.toUpperCase()) == DataType.BIGDECIMAL && StringUtils.isNumeric(value)) {
					return new BigDecimal(value);
				} else if (DataType.valueOf(fieldType.toUpperCase()) == DataType.LONG && StringUtils.isNumeric(value)) {
					return Long.parseLong(value);
				} else if (DataType.valueOf(fieldType.toUpperCase()) == DataType.INT && StringUtils.isNumeric(value)) {
					return Integer.parseInt(value);
				} else if (DataType.valueOf(fieldType.toUpperCase()) == DataType.STRING) {
					return value;
				} else if (DataType.valueOf(fieldType.toUpperCase()) == DataType.OBJECT) {
					return value;
				}
				break;
			}
		}

		return null;
	}
}
