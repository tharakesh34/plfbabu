package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "schDate", "pftRate", "baseRate", "splRate", "margin" })
@XmlAccessorType(XmlAccessType.NONE)
public class RateInstruction extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 8001757194454901203L;

	public RateInstruction() {
		super();
	}

	@XmlElement
	private Date schDate;
	@XmlElement
	private BigDecimal pftRate = BigDecimal.ZERO;
	@XmlElement
	private String baseRate;
	@XmlElement
	private String splRate;
	@XmlElement
	private BigDecimal margin = BigDecimal.ZERO;

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public BigDecimal getPftRate() {
		return pftRate;
	}

	public void setPftRate(BigDecimal pftRate) {
		this.pftRate = pftRate;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getSplRate() {
		return splRate;
	}

	public void setSplRate(String splRate) {
		this.splRate = splRate;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}
}
