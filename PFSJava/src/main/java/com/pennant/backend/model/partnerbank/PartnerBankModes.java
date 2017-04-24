package com.pennant.backend.model.partnerbank;

public class PartnerBankModes {
	private long 				partnerBankId = Long.MIN_VALUE;
	private String				purpose;
	private String				paymentMode;
	private String				partnerBankName;
	private String				partnerBankCode;

	public PartnerBankModes() {
		super();
	}
	
	public PartnerBankModes(long partnerBankId) {
		super();
		this.partnerBankId = partnerBankId;
	}
	
	public long getPartnerBankId() {
		return partnerBankId;
	}

	
	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

}
