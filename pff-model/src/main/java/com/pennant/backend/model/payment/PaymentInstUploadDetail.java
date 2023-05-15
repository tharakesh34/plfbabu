package com.pennant.backend.model.payment;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class PaymentInstUploadDetail extends UploadDetails {
	private static final long serialVersionUID = -4126772392388677874L;

	private String excessType;
	private String feeType;
	private BigDecimal payAmount;
	private String remarks;
	private String overRideOverDue;
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

	public FinanceMain getFm() {
		return fm;
	}

	public void setFm(FinanceMain fm) {
		this.fm = fm;
	}

	public String getOverRideOverDue() {
		return overRideOverDue;
	}

	public void setOverRideOverDue(String overRideOverDue) {
		this.overRideOverDue = overRideOverDue;
	}

}
