package com.pennanttech.bajaj.service;

public class Disbursement {
	private long	disbusmentId;
	private String	bankCode;
	private String	paymentType;
	private String	configName;

	public long getDisbusmentId() {
		return disbusmentId;
	}

	public void setDisbusmentId(long disbusmentId) {
		this.disbusmentId = disbusmentId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

}
