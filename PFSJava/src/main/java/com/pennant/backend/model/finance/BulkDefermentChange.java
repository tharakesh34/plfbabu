package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;

public class BulkDefermentChange implements Serializable {

    private static final long serialVersionUID = -1443490102322316620L;
	
    private String finReference;
	private String finType; 
	private String finCcy;
	private String scheduleMethod;
	private String profitDaysBasis;
	private String custCIF;
	private String finBranch;
	private String productCode;
	private Date eventFromDate;
	private Date eventToDate;
	
	public BulkDefermentChange() {
		
	}
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	
	public String getFinCcy() {
    	return finCcy;
    }
	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }
	
	public String getScheduleMethod() {
    	return scheduleMethod;
    }
	public void setScheduleMethod(String scheduleMethod) {
    	this.scheduleMethod = scheduleMethod;
    }
	
	public String getProfitDaysBasis() {
    	return profitDaysBasis;
    }
	public void setProfitDaysBasis(String profitDaysBasis) {
    	this.profitDaysBasis = profitDaysBasis;
    }
	
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }
	
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	
	public String getProductCode() {
    	return productCode;
    }
	public void setProductCode(String productCode) {
    	this.productCode = productCode;
    }
	
	public Date getEventFromDate() {
    	return eventFromDate;
    }
	public void setEventFromDate(Date eventFromDate) {
    	this.eventFromDate = eventFromDate;
    }
	
	public Date getEventToDate() {
    	return eventToDate;
    }
	public void setEventToDate(Date eventToDate) {
    	this.eventToDate = eventToDate;
    }

}
