package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Model class for the <b>WIFFinanceScheduleDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "finReference", "droplineDate", "actualRate", "baseRate", "splRate", "margin", "droplineRate",
		"limitDrop", "oDLimit", "limitIncreaseAmt" })
@XmlAccessorType(XmlAccessType.NONE)

public class OverdraftScheduleDetail extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private String finReference = null;
	@XmlElement
	private Date droplineDate;
	@XmlElement
	private BigDecimal actualRate = BigDecimal.ZERO;
	@XmlElement
	private String baseRate;
	@XmlElement
	private String splRate;
	@XmlElement
	private BigDecimal margin = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal droplineRate = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal limitDrop = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal oDLimit = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal limitIncreaseAmt = BigDecimal.ZERO;

	public OverdraftScheduleDetail() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getDroplineDate() {
		return droplineDate;
	}

	public void setDroplineDate(Date droplineDate) {
		this.droplineDate = droplineDate;
	}

	public BigDecimal getActualRate() {
		return actualRate;
	}

	public void setActualRate(BigDecimal actualRate) {
		this.actualRate = actualRate;
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

	public BigDecimal getLimitDrop() {
		return limitDrop;
	}

	public void setLimitDrop(BigDecimal limitDrop) {
		this.limitDrop = limitDrop;
	}

	public BigDecimal getDroplineRate() {
		return droplineRate;
	}

	public void setDroplineRate(BigDecimal droplineRate) {
		this.droplineRate = droplineRate;
	}

	public BigDecimal getODLimit() {
		return oDLimit;
	}

	public void setODLimit(BigDecimal oDLimit) {
		this.oDLimit = oDLimit;
	}

	public BigDecimal getLimitIncreaseAmt() {
		return limitIncreaseAmt;
	}

	public void setLimitIncreaseAmt(BigDecimal limitIncreaseAmt) {
		this.limitIncreaseAmt = limitIncreaseAmt;
	}

}
