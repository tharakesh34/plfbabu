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
package com.pennant.backend.delegationdeviation;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.finance.FinanceDeviations;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link com.pennant.backend.model.finance.FinanceDeviations
 * FinanceDeviations} object.
 * </p>
 */
public class DeviationUtil {
	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException
	 *             If the constructor is used to create and initialize a new instance of the declaring class by
	 *             suppressing Java language access checking.
	 */
	private DeviationUtil() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Returns <tt>true</tt> if the list contains the element with the specified code.
	 * 
	 * @param list
	 *            The list that need to be searched for.
	 * @param code
	 *            The code that need to be looked.
	 * @return <tt>true</tt> if the list contains the element with the specified code.
	 */
	public static boolean isExists(List<FinanceDeviations> list, String code) {
		for (FinanceDeviations item : list) {
			if (StringUtils.equals(code, item.getDeviationCode())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns <tt>true</tt> if the list contains the element with the specified module and code.
	 * 
	 * @param list
	 *            The list that need to be searched for.
	 * @param module
	 *            The module that need to be looked.
	 * @param code
	 *            The code that need to be looked.
	 * @return <tt>true</tt> if the list contains the element with the specified module and code.
	 */
	public static boolean isExists(List<FinanceDeviations> list, String module, String code) {
		for (FinanceDeviations item : list) {
			if (StringUtils.equals(module, item.getModule()) && StringUtils.equals(code, item.getDeviationCode())) {
				return true;
			}
		}

		return false;
	}
}
