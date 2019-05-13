package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class FeeTypeVsGLMapping implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private long feeTypeID;
	private String feeTypeCode;
	private String feeTypeDesc;
	private String glHead;
	private String glPrefix;

	private BigDecimal feePaid = BigDecimal.ZERO;

	public long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getGlHead() {
		return glHead;
	}

	public void setGlHead(String glHead) {
		this.glHead = glHead;
	}

	public String getGlPrefix() {
		return glPrefix;
	}

	public void setGlPrefix(String glPrefix) {
		this.glPrefix = glPrefix;
	}

	public BigDecimal getFeePaid() {
		return feePaid;
	}

	public void setFeePaid(BigDecimal feePaid) {
		this.feePaid = feePaid;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

}
