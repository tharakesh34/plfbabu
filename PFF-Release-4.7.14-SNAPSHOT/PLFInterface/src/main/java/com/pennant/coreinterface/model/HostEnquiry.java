package com.pennant.coreinterface.model;

import java.io.Serializable;

public class HostEnquiry implements Serializable {
	
	private static final long serialVersionUID = -4208248685660948057L;

	public HostEnquiry() {
		super();
	}
	
	private String unitName;
	private String statusCode;
	private String statusDesc;
	private String nextBusDate;
	private String prevBusDate;
	private String curBusDate;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	
	public String getNextBusDate() {
		return nextBusDate;
	}
	public void setNextBusDate(String nextBusDate) {
		this.nextBusDate = nextBusDate;
	}
	
	public String getPrevBusDate() {
		return prevBusDate;
	}
	public void setPrevBusDate(String prevBusDate) {
		this.prevBusDate = prevBusDate;
	}
	
	public String getCurBusDate() {
		return curBusDate;
	}
	public void setCurBusDate(String curBusDate) {
		this.curBusDate = curBusDate;
	}
	
}
