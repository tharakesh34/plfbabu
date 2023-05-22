package com.pennanttech.pff.model.external.posidex;

import java.io.Serializable;

public class PosidexCustomerLoan extends Posidex implements Serializable {
	static final long serialVersionUID = 1L;

	private String dealID;
	private String lanNo;
	private String customerType;
	private String applnNo;
	private String productCode;
	private long psxID;

	public PosidexCustomerLoan() {
	    super();
	}

	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	public String getLanNo() {
		return lanNo;
	}

	public void setLanNo(String lanNo) {
		this.lanNo = lanNo;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getApplnNo() {
		return applnNo;
	}

	public void setApplnNo(String applnNo) {
		this.applnNo = applnNo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getPsxID() {
		return psxID;
	}

	public void setPsxID(long psxID) {
		this.psxID = psxID;
	}

}
