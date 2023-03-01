package com.pennant.backend.model.payment;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class PaymentInstUploadDetail extends UploadDetails {

	private String excessType;
	private String feeType;
	private BigDecimal payAmount;
	private String remarks;
	private String overRide;
	private FinanceMain fm;

	public PaymentInstUploadDetail() {
		super();
	}

	public String getExcessType() {
		return excessType;
	}

	public void setExcessType(String excessType) {
		this.excessType = excessType;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOverRide() {
		return overRide;
	}

	public void setOverRide(String overRide) {
		this.overRide = overRide;
	}

	public FinanceMain getFm() {
		return fm;
	}

	public void setFm(FinanceMain fm) {
		this.fm = fm;
	}

}
