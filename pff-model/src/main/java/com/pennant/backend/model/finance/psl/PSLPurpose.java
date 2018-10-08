package com.pennant.backend.model.finance.psl;

import java.io.Serializable;

public class PSLPurpose implements Serializable {
	private static final long serialVersionUID = 1L;

	private String categoryCode;
	private String subCategoryCode;
	private String code;
	private String description;

	public PSLPurpose() {
		super();
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getSubCategoryCode() {
		return subCategoryCode;
	}

	public void setSubCategoryCode(String subCategoryCode) {
		this.subCategoryCode = subCategoryCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
