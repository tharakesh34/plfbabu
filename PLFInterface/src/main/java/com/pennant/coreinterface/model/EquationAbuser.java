package com.pennant.coreinterface.model;

import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class EquationAbuser extends AbstractWorkflowEntity { 
    private static final long serialVersionUID = -7305078754814792618L;
    
	private String abuserIDType;
	private String abuserIDNumber;
	private Date abuserExpDate;
	
	public EquationAbuser() {
    	super();
    }
	
	public String getAbuserIDType() {
    	return abuserIDType;
    }
	public void setAbuserIDType(String abuserIDType) {
    	this.abuserIDType = abuserIDType;
    }
	
	public String getAbuserIDNumber() {
    	return abuserIDNumber;
    }
	public void setAbuserIDNumber(String abuserIDNumber) {
    	this.abuserIDNumber = abuserIDNumber;
    }
	
	public Date getAbuserExpDate() {
		return abuserExpDate;
	}
	public void setAbuserExpDate(Date abuserExpDate) {
		this.abuserExpDate = abuserExpDate;
	}
}
