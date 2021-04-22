package com.pennant.backend.model;

import org.graalvm.polyglot.HostAccess;

public class ScriptError {
	
	@HostAccess.Export
	private String code;
	@HostAccess.Export
	private String property;
	@HostAccess.Export
	private String value;

	public ScriptError() {
		super();
	}

	/**
	 * Constructor for showing error Details in Post Validation Process
	 * 
	 * @param code
	 * @param errorMsg
	 * @param property
	 */
	@HostAccess.Export
	public ScriptError(String code, String errorMsg, String property) {
		super();
		this.code = code;
		this.property = property;
		this.value = errorMsg;
	}

	/**
	 * Constructor for preparing default value Details in Pre-Validation Process
	 * 
	 * @param property
	 * @param value
	 */
	@HostAccess.Export
	public ScriptError(String property, String value) {
		super();
		this.property = property;
		this.value = value;
	}

	public String getCode() {
		return code;
	}
	
	@HostAccess.Export
	public void setCode(String code) {
		this.code = code;
	}

	public String getProperty() {
		return property;
	}
	
	@HostAccess.Export
	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}
	
	@HostAccess.Export
	public void setValue(String value) {
		this.value = value;
	}
}
