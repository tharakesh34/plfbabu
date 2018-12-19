package com.pennant.backend.model.configuration;

import java.math.BigDecimal;

public class VASPremiumCalcDetails {
	private long batchId = Long.MIN_VALUE;
	private String productCode;
	private long manufacturerId;
	private String manufacturerName;
	private int customerAge;
	private String gender;
	private int policyAge;
	private BigDecimal premiumPercentage;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private BigDecimal finAmount;
	private String finType;
	private String premiumPercentageF;
	private String minAmountF;
	private String maxAmountF;
	private String customerAgeF;
	private String policyAgeF;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public long getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public int getCustomerAge() {
		return customerAge;
	}

	public void setCustomerAge(int customerAge) {
		this.customerAge = customerAge;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getPolicyAge() {
		return policyAge;
	}

	public void setPolicyAge(int policyAge) {
		this.policyAge = policyAge;
	}

	public BigDecimal getPremiumPercentage() {
		return premiumPercentage;
	}

	public void setPremiumPercentage(BigDecimal premiumPercentage) {
		this.premiumPercentage = premiumPercentage;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getPremiumPercentageF() {
		return premiumPercentageF;
	}

	public void setPremiumPercentageF(String premiumPercentageF) {
		this.premiumPercentageF = premiumPercentageF;
	}

	public String getMinAmountF() {
		return minAmountF;
	}

	public void setMinAmountF(String minAmountF) {
		this.minAmountF = minAmountF;
	}

	public String getMaxAmountF() {
		return maxAmountF;
	}

	public void setMaxAmountF(String maxAmountF) {
		this.maxAmountF = maxAmountF;
	}

	public String getCustomerAgeF() {
		return customerAgeF;
	}

	public void setCustomerAgeF(String customerAgeF) {
		this.customerAgeF = customerAgeF;
	}

	public String getPolicyAgeF() {
		return policyAgeF;
	}

	public void setPolicyAgeF(String policyAgeF) {
		this.policyAgeF = policyAgeF;
	}

}
