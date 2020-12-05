package com.pennanttech.ws.model.customer;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class LimitData {

	@XmlElement
	private Date expiryDate;
	@XmlElement
	private String limitGroup;
	@XmlElement(name = "availableLimit")
	private BigDecimal actualLimit = BigDecimal.ZERO;

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getLimitGroup() {
		return limitGroup;
	}

	public void setLimitGroup(String limitGroup) {
		this.limitGroup = limitGroup;
	}

	public BigDecimal getActualLimit() {
		return actualLimit;
	}

	public void setActualLimit(BigDecimal actualLimit) {
		this.actualLimit = actualLimit;
	}

}
