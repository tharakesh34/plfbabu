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
	 * @throws IllegalAccessException
	 *             If the constructor is used to create and initialize a new instance of the declaring class by
	 *             suppressing Java language access checking.
	 */
	private DataTypeUtil() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Get the value with the specified data type.
	 * 
	 * @param value
	 *            The value.
	 * @param type
	 *            The required data type.
	 * @return The value with the specified data type.
	 */
	public static Object getValueAsObject(String value, DataType type) {
		value = StringUtils.trimToNull(value);

		if (value == null) {
			return null;
		}

		if (type == DataType.LONG) {
			if (StringUtils.isNumeric(value)) {
				return Long.valueOf(value);
			} else {
				return null;
			}
		} else {
			return value;
		}
	}
}
