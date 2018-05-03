package com.pennant.backend.model.partnerbank;

public class PartnerBranchModes {
	private long 				partnerBankId = Long.MIN_VALUE;
	private String				branchCode;
	private String				paymentMode;
	
	
	public long getPartnerBankId() {
		return partnerBankId;
	}
	
	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	public String getPaymentMode() {
		return paymentMode;
	}
	
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	
}
