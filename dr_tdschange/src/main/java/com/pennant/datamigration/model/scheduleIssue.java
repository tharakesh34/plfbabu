package com.pennant.datamigration.model;

import java.math.BigDecimal;
import java.util.Date;

public class scheduleIssue {

	private String finReference;
	private Date schDate;
	private String fieldName;
	private BigDecimal oldAmount;
	private BigDecimal newAmount;
	
	public scheduleIssue(String finReference, Date schDate, String fieldName, BigDecimal oldAmount, BigDecimal newAmount){
		this.finReference = finReference;
		this.schDate = schDate;
		this.fieldName = fieldName;
		this.oldAmount = oldAmount;
		this.newAmount = newAmount;
	}
	
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public Date getSchDate() {
		return schDate;
	}
	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public BigDecimal getOldAmount() {
		return oldAmount;
	}
	public void setOldAmount(BigDecimal oldAmount) {
		this.oldAmount = oldAmount;
	}
	public BigDecimal getNewAmount() {
		return newAmount;
	}
	public void setNewAmount(BigDecimal newAmount) {
		this.newAmount = newAmount;
	}
	 
}
