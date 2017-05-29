package com.pennant.backend.model;

public class ScriptError {
	
	private String code;
	private String property;
	private String value;
	
	public ScriptError() {
		super();
	}

	/**
	 * Constructor for showing error Details in Post Validation Process
	 * @param code
	 * @param errorMsg
	 * @param property
	 */
	public ScriptError(String code, String errorMsg, String property) {
		super();
		this.code = code;
		this.property = property;
		this.value = errorMsg;
	}
	
	/**
	 * Constructor for preparing default value Details in Pre-Validation Process
	 * @param property
	 * @param value
	 */
	public ScriptError(String property, String value) {
		super();
		this.property = property;
		this.value = value;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
