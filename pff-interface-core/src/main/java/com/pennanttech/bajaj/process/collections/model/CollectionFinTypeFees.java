package com.pennanttech.bajaj.process.collections.model;

import java.math.BigDecimal;

public class CollectionFinTypeFees {

	private String finType;
	private String calculationType;
	private String ruleCode;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal percentage = BigDecimal.ZERO;
	private String calculateOn;

	/**
	 * default constructor
	 */
	public CollectionFinTypeFees() {
		super();
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(String calculationType) {
		this.calculationType = calculationType;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getCalculateOn() {
		return calculateOn;
	}

	public void setCalculateOn(String calculateOn) {
		this.calculateOn = calculateOn;
	}
}
