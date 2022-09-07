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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : UpdateAttributeServiceTask.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-11-2018 * *
 * Modified Date : 22-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-11-2018 Sai Krishna 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.FinanceDetail;

/**
 * The service task implementation that will be used for "Update Attribute".
 */
public final class UpdateAttributeServiceTask {
	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private UpdateAttributeServiceTask() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static Map<String, String> getAttributes(String parameters, FinanceDetail financeDetail) {
		Map<String, String> result = new HashMap<>();

		if (StringUtils.isBlank(parameters)) {
			return result;
		}

		// Loop through each parameter.
		for (String param : StringUtils.trimToEmpty(parameters).split(",")) {
			if (StringUtils.isBlank(param)) {
				continue;
			}

			Property property = getProperty(param, financeDetail);

			if (property != null) {
				result.put(property.getKey().toString(), property.getValue());
			}
		}

		return result;
	}

	public static Property getProperty(String parameter, FinanceDetail financeDetail) {
		// Get key and value parts.
		String[] args = StringUtils.trimToEmpty(parameter).split("=");

		if (args.length != 2 || StringUtils.isBlank(args[0])) {
			return null;
		}

		// Get the attributes of the property.
		String placeholder;
		String key;
		String value = StringUtils.trimToEmpty(args[1]);

		if (args[0].contains("::")) {
			String[] keys = StringUtils.trimToEmpty(args[0]).split("::");

			if (keys.length != 2 || StringUtils.isBlank(keys[0]) || StringUtils.isBlank(keys[1])) {
				return null;
			}

			key = StringUtils.trimToEmpty(keys[0]);
			placeholder = StringUtils.trimToEmpty(keys[1]);

			value = getPropertyValue(placeholder, value, financeDetail);
		} else {
			key = StringUtils.trimToEmpty(args[0]);
		}

		return new Property(key, value);
	}

	public static String getPropertyValue(String parameter, String value, FinanceDetail financeDetail) {
		if ("LEGAL_TECH_VALUE_CHANGED".equals(parameter)) {
			BigDecimal ltv;
			BigDecimal updatedLtv;
			boolean ltvChanged = false;
			Map<String, Object> values;
			String suffix;

			if (financeDetail.getExtendedFieldRender() != null) {
				values = financeDetail.getExtendedFieldRender().getMapValues();

				for (int i = 1; i < 6; i++) {
					suffix = String.valueOf(i);

					if (values.get("LTV_".concat(suffix)) == null
							|| values.get("UPDATED_LTV_".concat(suffix)) == null) {
						break;
					}

					ltv = (BigDecimal) values.get("LTV_".concat(suffix));
					updatedLtv = (BigDecimal) values.get("UPDATED_LTV_".concat(suffix));

					if (ltv.compareTo(updatedLtv) != 0) {
						ltvChanged = true;

						break;
					}
				}
			}

			return ltvChanged ? value : "";
		}

		return "";
	}
}
