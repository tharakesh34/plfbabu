package com.pennant.backend.model.mail;

import java.io.Serializable;


public class TemplateFields implements Serializable{

	private static final long serialVersionUID = 5970895533633063298L;
	
	private long id;
	private String fieldDesc;
	private String fieldFormat;
	private String field;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFieldDesc() {
		return fieldDesc;
	}
	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}
	public String getFieldFormat() {
		return fieldFormat;
	}
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}

}
