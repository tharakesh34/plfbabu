package com.pennant.backend.model.mail;

import java.io.Serializable;


public class TemplateFields implements Serializable{

	private static final long serialVersionUID = 5970895533633063298L;
	
	private String module;
	private String field;
	private String fieldDesc;
	private String fieldFormat;
	
	public TemplateFields() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getModule() {
    	return module;
    }
	public void setModule(String module) {
    	this.module = module;
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
