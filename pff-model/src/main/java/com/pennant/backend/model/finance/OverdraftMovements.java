package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class OverdraftMovements extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private long oDSeqID = Long.MIN_VALUE;
	private Date valueDate;
	private long finID;
	private String finReference = null;
	private Date droplineDate;
	private int tenor;
	private Date oDExpiryDate;
	private String droplineFrq = null;
	private BigDecimal limitChange = BigDecimal.ZERO;
	private BigDecimal oDLimit = BigDecimal.ZERO;

	public OverdraftMovements() {
		super();
	}

	public long getODSeqID() {
		return oDSeqID;
	}

	public void setODSeqID(long oDSeqID) {
		this.oDSeqID = oDSeqID;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public Date getODExpiryDate() {
		return oDExpiryDate;
	}

	public void setODExpiryDate(Date oDExpiryDate) {
		this.oDExpiryDate = oDExpiryDate;
	}

	public BigDecimal getODLimit() {
		return oDLimit;
	}

	public void setODLimit(BigDecimal oDLimit) {
		this.oDLimit = oDLimit;
	}

	public String getDroplineFrq() {
		return droplineFrq;
	}

	public void setDroplineFrq(String droplineFrq) {
		this.droplineFrq = droplineFrq;
	}

	public BigDecimal getLimitChange() {
		return limitChange;
	}

	public void setLimitChange(BigDecimal limitChange) {
		this.limitChange = limitChange;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

}
