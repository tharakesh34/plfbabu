package com.pennanttech.pennapps.core.jdbc;

import java.sql.Date;
import java.sql.Types;

public class JdbcUtil {
	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException
	 *             If the constructor is used to create and initialize a new instance of the declaring class by
	 *             suppressing Java language access checking.
	 */
	private JdbcUtil() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Returns {@link java.sql.Date} for the specified {@link Date}.
	 * 
	 * @param date
	 *            The date object which which needs to be convert to {@link java.sql.Date} .
	 * @return A {@link java.sql.Date} or <code>null</code> if input date is <code>null</code>.
	 */
	public static Date getDate(java.util.Date date) {
		if (date == null) {
			return null;
		}

		return new java.sql.Date(date.getTime());
	}

	public static Long setLong(Long value) {
		if (value == null) {
			return (long) Types.NULL;
		}

		return value;
	}

	public static Long getLong(Object value) {
		return value == null ? null : Long.valueOf(value.toString());
	}
}
