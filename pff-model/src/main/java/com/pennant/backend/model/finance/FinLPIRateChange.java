package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class FinLPIRateChange {

	private String finReference;
	private long finID = 0;
	private BigDecimal rate = BigDecimal.ZERO;
	private BigDecimal margin = BigDecimal.ZERO;
	private Date effectiveDate = null;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}
