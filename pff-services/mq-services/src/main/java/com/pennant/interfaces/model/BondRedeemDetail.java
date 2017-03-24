package com.pennant.interfaces.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SukukRedeemPostingRequest")
public class BondRedeemDetail implements Serializable {

	private static final long serialVersionUID = 5680320972267526306L;

	private String referenceNum;
	private String purchaseRef;
	private String hostRef;
	private String productName;
	private BigDecimal sukukAmount = BigDecimal.ZERO;
	private boolean redeemStatus;
	private long timeStamp;

	public BondRedeemDetail() {
		super();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "PurchaseRef")
	public String getPurchaseRef() {
		return purchaseRef;
	}

	public void setPurchaseRef(String purchaseRef) {
		this.purchaseRef = purchaseRef;
	}

	@XmlElement(name = "HostRef")
	public String getHostRef() {
		return hostRef;
	}

	public void setHostRef(String hostRef) {
		this.hostRef = hostRef;
	}

	@XmlElement(name = "ProductName")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@XmlElement(name = "SukukAmount")
	public BigDecimal getSukukAmount() {
		return sukukAmount;
	}

	public void setSukukAmount(BigDecimal sukukAmount) {
		this.sukukAmount = sukukAmount;
	}
	
	public boolean isRedeemStatus() {
		return redeemStatus;
	}

	public void setRedeemStatus(boolean redeemStatus) {
		this.redeemStatus = redeemStatus;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
