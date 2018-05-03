package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.util.Date;

public class EquationMasterMissedDetail implements Serializable { 

    private static final long serialVersionUID = -7305078754814792618L;
    
	public EquationMasterMissedDetail() {
    	super();
    }
	
	private String module;
	private String fieldName;
	private String description;
	private Date lastMntOn;
	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Date lastMntOn) {
		this.lastMntOn = lastMntOn;
	}
	
}
