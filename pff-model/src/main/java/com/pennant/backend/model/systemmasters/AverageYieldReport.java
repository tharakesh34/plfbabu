package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;

public class AverageYieldReport {

	private String custShrtName;
	private String finReference;
	private String finType;
	private BigDecimal avgFunding = BigDecimal.ZERO;
	private BigDecimal intEarned = BigDecimal.ZERO;
	private BigDecimal yieldPercent = BigDecimal.ZERO;

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
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

	public BigDecimal getAvgFunding() {
		return avgFunding;
	}

	public void setAvgFunding(BigDecimal avgFunding) {
		this.avgFunding = avgFunding;
	}

	public BigDecimal getIntEarned() {
		return intEarned;
	}

	public void setIntEarned(BigDecimal intEarned) {
		this.intEarned = intEarned;
	}

	public BigDecimal getYieldPercent() {
		return yieldPercent;
	}

	public void setYieldPercent(BigDecimal yieldPercent) {
		this.yieldPercent = yieldPercent;
	}

}
