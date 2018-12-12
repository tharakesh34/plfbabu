package com.pennant.backend.model.configuration;

import java.math.BigDecimal;

public class VASPremiumCalcDetails {
	private String productCode;
	private long manufacturerId;
	private int customerAge;
	private String gender;
	private int policyAge;
	private BigDecimal premiumPercentage;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;

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

}
