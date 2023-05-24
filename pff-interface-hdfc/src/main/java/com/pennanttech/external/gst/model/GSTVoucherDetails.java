package com.pennanttech.external.gst.model;

import java.math.BigDecimal;

public class GSTVoucherDetails {
	private long gstVoucherId;
	private String finreference;
	private String amountType;
	private long referenceField1;
	private long referenceField2;
	private BigDecimal referenceAmount;
	private BigDecimal actualAmount;

	public long getGstVoucherId() {
		return gstVoucherId;
	}

	public void setGstVoucherId(long gstVoucherId) {
		this.gstVoucherId = gstVoucherId;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public long getReferenceField1() {
		return referenceField1;
	}

	public void setReferenceField1(long referenceField1) {
		this.referenceField1 = referenceField1;
	}

	public long getReferenceField2() {
		return referenceField2;
	}

	public void setReferenceField2(long referenceField2) {
		this.referenceField2 = referenceField2;
	}

	public BigDecimal getReferenceAmount() {
		return referenceAmount;
	}

	public void setReferenceAmount(BigDecimal referenceAmount) {
		this.referenceAmount = referenceAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

}
