package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;

public class CustomerLimitCategory implements Serializable {

	private static final long serialVersionUID = -8571649245648119467L;

	private String limitCategory;
	private BigDecimal riskAmount;
	private BigDecimal limitStatus;
	private BigDecimal availableWeight;

	public CustomerLimitCategory() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getLimitCategory() {
		return limitCategory;
	}

	public void setLimitCategory(String limitCategory) {
		this.limitCategory = limitCategory;
	}

	public BigDecimal getRiskAmount() {
		return riskAmount;
	}

	public void setRiskAmount(BigDecimal riskAmount) {
		this.riskAmount = riskAmount;
	}

	public BigDecimal getLimitStatus() {
		return limitStatus;
	}

	public void setLimitStatus(BigDecimal limitStatus) {
		this.limitStatus = limitStatus;
	}

	public BigDecimal getAvailableWeight() {
		return availableWeight;
	}

	public void setAvailableWeight(BigDecimal availableWeight) {
		this.availableWeight = availableWeight;
	}

}
