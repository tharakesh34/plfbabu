package com.pennanttech.extendedfield;

import java.io.Serializable;

public class ExtendedFieldsMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private String moduleCode;
	private String fieldCode;
	private String fieldName;

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
