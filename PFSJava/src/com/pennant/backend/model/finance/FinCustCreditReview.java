package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class FinCustCreditReview {
	private long customerId;
	private String categoryDesc;
	private String subCategoryDesc;
	private long categoryId;
	private BigDecimal year1;
	private BigDecimal year2;
	private BigDecimal year3;
	private BigDecimal year4;
	
	public long getCustomerId() {
    	return customerId;
    }
	public void setCustomerId(long customerId) {
    	this.customerId = customerId;
    }
	public String getCategoryDesc() {
    	return categoryDesc;
    }
	public void setCategoryDesc(String categoryDesc) {
    	this.categoryDesc = categoryDesc;
    }
	public long getCategoryId() {
    	return categoryId;
    }
	public void setCategoryId(long categoryId) {
    	this.categoryId = categoryId;
    }
	public String getSubCategoryDesc() {
    	return subCategoryDesc;
    }
	public void setSubCategoryDesc(String subCategoryDesc) {
    	this.subCategoryDesc = subCategoryDesc;
    }
	public BigDecimal getYear1() {
    	return year1;
    }
	public void setYear1(BigDecimal year1) {
    	this.year1 = year1;
    }
	public BigDecimal getYear2() {
    	return year2;
    }
	public void setYear2(BigDecimal year2) {
    	this.year2 = year2;
    }
	public BigDecimal getYear3() {
    	return year3;
    }
	public void setYear3(BigDecimal year3) {
    	this.year3 = year3;
    }
	public BigDecimal getYear4() {
    	return year4;
    }
	public void setYear4(BigDecimal year4) {
    	this.year4 = year4;
    }

}
