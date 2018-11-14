package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;

/**
 * Model class for the <b>InterestRateDetail table</b>.<br>
 * 
 */

public class InterestRateDetail {

	private int formTenure = 0;
	private int toTenure = 0;
	private int noOfMonths = 0;
	private BigDecimal interestRate = BigDecimal.ZERO;
	private String interestType;

	public int getFormTenure() {
		return formTenure;
	}

	public void setFormTenure(int formTenure) {
		this.formTenure = formTenure;
	}

	public int getToTenure() {
		return toTenure;
	}

	public void setToTenure(int toTenure) {
		this.toTenure = toTenure;
	}

	public int getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(int noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

}