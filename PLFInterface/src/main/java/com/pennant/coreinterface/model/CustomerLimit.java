package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CustomerLimit implements Serializable {
	
	private static final long serialVersionUID = 6854633859577732726L;

    public CustomerLimit() {
    	super();
    }
    
	private String custMnemonic;
	private String custName;
	private String custLocation;
	private String custGrpCode;
	private String custGrpDesc;
	private String custCountry;
	private String custCountryDesc;
	private String limitCountry;
	private String limitCategory;
	private String limitCategoryDesc;
	private String limitCurrency; 
	private int    limitCcyEdit; 

	private Date limitExpiry;
	private BigDecimal limitAmount;
	private BigDecimal availAmount;
	private BigDecimal riskAmount;

	private String limitBranch;
	private String repeatThousands;
	private String checkLimit;
	private String seqNum;

	private String groupLimit;
	private String errorId;
	private String errorMsg;
	private String remarks;

	public String getCustMnemonic() {
		return custMnemonic;
	}

	public void setCustMnemonic(String custMnemonic) {
		this.custMnemonic = custMnemonic;
	}

	public String getCustLocation() {
		return custLocation;
	}

	public void setCustLocation(String custLocation) {
		this.custLocation = custLocation;
	}

	public String getLimitCountry() {
		return limitCountry;
	}

	public void setLimitCountry(String limitCountry) {
		this.limitCountry = limitCountry;
	}

	public String getLimitCategory() {
		return limitCategory;
	}

	public void setLimitCategory(String limitCategory) {
		this.limitCategory = limitCategory;
	}

	public String getLimitCategoryDesc() {
		return limitCategoryDesc;
	}

	public void setLimitCategoryDesc(String limitCategoryDesc) {
		this.limitCategoryDesc = limitCategoryDesc;
	}

	public String getLimitCurrency() {
		return limitCurrency;
	}

	public void setLimitCurrency(String limitCurrency) {
		this.limitCurrency = limitCurrency;
	}

	public Date getLimitExpiry() {
		return limitExpiry;
	}

	public void setLimitExpiry(Date limitExpiry) {
		this.limitExpiry = limitExpiry;
	}

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	public BigDecimal getAvailAmount() {
		return availAmount;
	}

	public void setAvailAmount(BigDecimal availAmount) {
		this.availAmount = availAmount;
	}

	public BigDecimal getRiskAmount() {
		return riskAmount;
	}

	public void setRiskAmount(BigDecimal riskAmount) {
		this.riskAmount = riskAmount;
	}

	public String getGroupLimit() {
		return groupLimit;
	}

	public void setGroupLimit(String groupLimit) {
		this.groupLimit = groupLimit;
	}

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustName() {
		return custName;
	}

	public String getLimitBranch() {
		return limitBranch;
	}

	public void setLimitBranch(String limitBranch) {
		this.limitBranch = limitBranch;
	}

	public String getRepeatThousands() {
		return repeatThousands;
	}

	public void setRepeatThousands(String repeatThousands) {
		this.repeatThousands = repeatThousands;
	}

	public String getCheckLimit() {
		return checkLimit;
	}

	public void setCheckLimit(String checkLimit) {
		this.checkLimit = checkLimit;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setLimitCcyEdit(int limitCcyEdit) {
		this.limitCcyEdit = limitCcyEdit;
	}

	public int getLimitCcyEdit() {
		return limitCcyEdit;
	}

	public String getCustGrpCode() {
		return custGrpCode;
	}

	public void setCustGrpCode(String custGrpCode) {
		this.custGrpCode = custGrpCode;
	}

	public String getCustGrpDesc() {
		return custGrpDesc;
	}

	public void setCustGrpDesc(String custGrpDesc) {
		this.custGrpDesc = custGrpDesc;
	}

	public String getCustCountry() {
		return custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getCustCountryDesc() {
		return custCountryDesc;
	}

	public void setCustCountryDesc(String custCountryDesc) {
		this.custCountryDesc = custCountryDesc;
	}



}
