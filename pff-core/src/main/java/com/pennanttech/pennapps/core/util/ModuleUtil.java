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
package com.pennanttech.pennapps.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennanttech.pff.core.model.ModuleMapping;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link ModuleMapping} that contain information about the
 * implementation of the modules.
 * </p>
 */
public final class ModuleUtil {
	private static final Logger logger = Logger.getLogger(ModuleUtil.class);

	private static Map<String, ModuleMapping> moduleMappings = new HashMap<>();

	private ModuleUtil() {
		super();
	}

	/**
	 * Registers the specified <code>mapping</code> for the specified <code>code</code>. If the mapping was already
	 * available for the code, the old mapping is replaced.
	 * 
	 * @param code
	 *            Code of the module for which the mapping was provided.
	 * @param mapping
	 *            The {@link ModuleMapping} specifying the attributes to the module.
	 */
	public static void register(String code, ModuleMapping mapping) {
		moduleMappings.put(code, mapping);
	}

	/**
	 * Returns a String array containing all of the module codes that were registered.
	 * 
	 * @return A string array containing all the module codes that were registered.
	 */
	public static String[] getCodes() {
		return moduleMappings.keySet().toArray(new String[0]);
	}

	/**
	 * Returns the Mapping to which the specified code of the module is registered.
	 * 
	 * @param code
	 *            Code of the module for which the associated mapping to be returned.
	 * @return The mapping to which the specified code of the module is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static ModuleMapping getModuleMapping(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		ModuleMapping mapping = moduleMappings.get(code);

		if (mapping == null) {
			throw new IllegalAccessError("Module registration not available.");
		}

		return mapping;
	}

	/**
	 * Returns the name of the module to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated name of the module to be returned.
	 * @return The name of the module to which the specified code is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static String getModuleName(String code) {
		return getModuleMapping(code).getModuleName();
	}

	/**
	 * Returns the primary entity class of the module to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated primary entity class of the module to be returned.
	 * @return The primary entity class of the module to which the specified code is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static Class<?> getModuleClass(String code) {
		return getModuleMapping(code).getModuleClass();
	}

	/**
	 * Returns the physical or logical table used for the main module to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated physical or logical table used for the main module to be returned.
	 * @return The physical or logical table used for the main module to which the specified code is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static String getTableName(String code) {
		try {
			return getModuleMapping(code).getTableName();
		} catch (IllegalAccessError error) {
			logger.warn("Exception: ", error);
			return null;
		}
	}

	/**
	 * Returns the physical or logical table used for the LOV to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated physical or logical table used for the LOV to be returned.
	 * @return The physical or logical table used for the LOV to which the specified code is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static String getLovTableName(String code) {
		return getModuleMapping(code).getLovTableName();
	}

	/**
	 * Returns the process flow type used for the module to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated process flow type used to be returned.
	 * @return The process flow type used for the module to which the specified code is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static String getWorkflowType(String code) {
		return getModuleMapping(code).getWorkflowType();
	}

	/**
	 * Returns list of fields used for the LOV to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated fields used for the LOV to be returned.
	 * @return The list of fields used for the LOV to which the specified code is registered..
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static String[] getLovFields(String code) {
		return getModuleMapping(code).getLovFields();
	}

	/**
	 * Returns list of filters used for the LOV to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated filters used for the LOV to be returned.
	 * @return The list of filters used for the LOV to which the specified code is registered..
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static String[][] getLovFilters(String code) {
		return getModuleMapping(code).getLovFilters();
	}

	/**
	 * Returns the width used for the LOV to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated width used for the LOV to be returned.
	 * @return The width used for the LOV to which the specified code is registered..
	 * @throws IllegalArgumentException
	 *             - If the specified code of the module is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the module is not registered.
	 */
	public static int getLovWidth(String code) {
		return getModuleMapping(code).getLovWidth();
	}
}
