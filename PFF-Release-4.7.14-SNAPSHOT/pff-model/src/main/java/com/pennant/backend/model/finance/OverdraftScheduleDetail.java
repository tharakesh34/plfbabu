package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class OverdraftScheduleDetail extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String finReference = null;
	private Date droplineDate;
	private BigDecimal actualRate = BigDecimal.ZERO;
	private	String baseRate;
	private String  splRate;
	private BigDecimal margin = BigDecimal.ZERO;
	private BigDecimal droplineRate = BigDecimal.ZERO;
	private BigDecimal limitDrop = BigDecimal.ZERO;
	private BigDecimal oDLimit = BigDecimal.ZERO;
	private BigDecimal limitIncreaseAmt = BigDecimal.ZERO;
	
	public OverdraftScheduleDetail(){
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
