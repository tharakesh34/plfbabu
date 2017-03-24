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
package com.pennanttech.pff.core.util;

import java.lang.reflect.Field;

import org.apache.commons.lang.ArrayUtils;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link Class} object.
 * </p>
 */
public final class ClassUtil {
	private ClassUtil() {
		super();
	}

	private static <E> Field[] getAllFields(Class<E> clazz) {
		Field[] fields = clazz.getDeclaredFields();

		if (clazz.getSuperclass() != null) {
			return (Field[]) ArrayUtils.addAll(fields, getAllFields(clazz.getSuperclass()));
		}

		return fields;
	}

	/**
	 * Returns an array of Field objects reflecting all the fields declared by the class or interface represented by the
	 * object. This doesn't include inherited fields. This method returns an array of length 0 if the class or interface
	 * declares no fields.
	 * 
	 * @param object
	 *            The object to be checked.
	 * @return The array of Field objects representing all the declared fields of the class.
	 */
	public static Field[] getFields(Object object) {
		return object.getClass().getDeclaredFields();
	}

	/**
	 * Returns an array of Field objects reflecting all the fields declared by the class or interface represented by the
	 * object. This includes inherited fields as well. This method returns an array of length 0 if the class or
	 * interface declares no fields.
	 * 
	 * @param object
	 *            The object to be checked.
	 * @return The array of Field objects representing all the declared fields of the class and it's superclass.
	 */
	public static Field[] getAllFields(Object object) {
		return getAllFields(object.getClass());
	}
}
