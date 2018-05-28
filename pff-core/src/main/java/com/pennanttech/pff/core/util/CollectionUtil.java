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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * A suite of utility methods for collections.
 * </p>
 */
public final class CollectionUtil {
	private CollectionUtil() {
		super();
	}

	/**
	 * Answers true if a predicate is true for at least one element of a collection (delimited string).
	 * 
	 * @param delimtedString
	 *            The collection to get the input from, may be null.
	 * @param delimiter
	 *            The delimiter used to define the collection.
	 * @param predicate
	 *            The predicate to use, may be null
	 * @return <code>true</code> if at least one element of the collection matches the predicate.
	 */
	public static boolean exists(String delimtedString, String delimiter, String predicate) {
		if (StringUtils.isBlank(delimtedString) || StringUtils.isBlank(predicate)) {
			return false;
		}
		
		List<String> collection = Arrays.asList(StringUtils.split(delimtedString, delimiter));

		return collection.contains(predicate);
	}
}
