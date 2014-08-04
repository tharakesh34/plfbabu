package com.pennant.backend.model.finance;

import java.util.Date;
import java.util.List;

public class ScheduleMapDetails implements java.io.Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	
	private String finReference = null;
    private Date schdFromDate;
    private Date schdToDate;
    private List<String> lovDescFinRefences;
    
	// Getter and Setter methods
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	public Date getSchdFromDate() {
    	return schdFromDate;
    }
	public void setSchdFromDate(Date schdFromdate) {
    	this.schdFromDate = schdFromdate;
    }
	public Date getSchdToDate() {
    	return schdToDate;
    }
	public void setSchdToDate(Date schdTodate) {
    	this.schdToDate = schdTodate;
    }
	public List<String> getLovDescFinRefences() {
    	return lovDescFinRefences;
    }
	public void setLovDescFinRefences(List<String> lovDescFinRefences) {
    	this.lovDescFinRefences = lovDescFinRefences;
    }
    
}
