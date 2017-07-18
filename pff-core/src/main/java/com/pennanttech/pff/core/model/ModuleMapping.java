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
package com.pennanttech.pff.core.model;

import java.io.Serializable;

/**
 * The class provides mapping of class, table, process flow, and LOV attributes of the module.
 */
public class ModuleMapping implements Serializable {
	private static final long serialVersionUID = 374707365259033361L;

	private String moduleName;
	private Class<?> moduleClass;
	private String tableName;
	private String lovTableName;
	private String workflowType;
	private String[] lovFields;
	private String[][] lovFilters;
	private int lovWidth = 0;

	/**
	 * Registers the mapping attributes to the module.
	 * 
	 * @param moduleName
	 *            Name of the module for which the mapping was provided.
	 * @param moduleClass
	 *            The primary entity class of the module.
	 * @param tables
	 *            The physical or logical tables used to fetch the data. The allowed size is two and the remaining
	 *            elements will be ignored. <br/>
	 *            - Element #1: Table Name used for the main module.<br/>
	 *            - Element #2: Table Name used for LOV. If not available, Element #1 will be used.
	 * @param workflowType
	 *            The type of the process flow used for the module.
	 * @param lovFields
	 *            List of fields that need to be displayed in LOV.
	 * @param lovFilters
	 *            List of Filters required for LOV. Each {@code Filter} should contain the property, value, and operator
	 *            as specified below. Refer {@code Filter} for the available operators.<br/>
	 *            <code>Usage: { "Active", "0", "1" }</code>
	 * @param lovWidth
	 *            The width of LOV in pixels.
	 */
	public ModuleMapping(String moduleName, Class<?> moduleClass, String[] tables, String workflowType,
			String[] lovFields, String[][] lovFilters, int lovWidth) {
		super();

		this.moduleName = moduleName;
		this.moduleClass = moduleClass;
		if (tables != null && tables.length > 0) {
			this.tableName = tables[0];
			if (tables.length == 1) {
				this.lovTableName = tables[0];
			} else {
				this.lovTableName = tables[1];
			}
		}
		this.workflowType = workflowType;
		this.lovFields = lovFields == null ? null : lovFields.clone();
		this.lovFilters = lovFilters == null ? null : lovFilters.clone();
		this.lovWidth = lovWidth;
	}

	public String getModuleName() {
		return moduleName;
	}

	public Class<?> getModuleClass() {
		return moduleClass;
	}

	public String getTableName() {
		return tableName;
	}

	public String getLovTableName() {
		return lovTableName;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public String[] getLovFields() {
		return lovFields == null ? null : lovFields.clone();
	}

	public String[][] getLovFilters() {
		return lovFilters == null ? null : lovFilters.clone();
	}

	public int getLovWidth() {
		return lovWidth;
	}
}
