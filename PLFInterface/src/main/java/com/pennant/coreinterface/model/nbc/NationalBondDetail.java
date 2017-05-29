package com.pennant.coreinterface.model.nbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.pennant.coreinterface.model.nbc.BondPurchaseDetail;
import com.pennant.coreinterface.model.nbc.BondTransferDetail;

public class NationalBondDetail implements Serializable {

	private static final long serialVersionUID = -2289296120839719944L;

	public NationalBondDetail() {
		super();
	}

	private String referenceNum;
	private BigDecimal amount = BigDecimal.ZERO;
	private String transferLevel;
	private String refNumConsumer;
	private String refNumProvider;
	private String accountTitle;
	private String custIBAN;
	private String customerName;
	private String customerType;
	private String mobileNumber;
	private String emailAddr;
	private String productName;
	private String returnCode;
	private String returnText;
	private String eventType;

	private List<BondPurchaseDetail> purchaseDetailList;
	private List<BondTransferDetail> transferDetailList;
	private long timeStamp;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransferLevel() {
		return transferLevel;
	}

	public void setTransferLevel(String transferLevel) {
		this.transferLevel = transferLevel;
	}

	public String getRefNumConsumer() {
		return refNumConsumer;
	}

	public void setRefNumConsumer(String refNumConsumer) {
		this.refNumConsumer = refNumConsumer;
	}

	public String getRefNumProvider() {
		return refNumProvider;
	}

	public void setRefNumProvider(String refNumProvider) {
		this.refNumProvider = refNumProvider;
	}

	public String getAccountTitle() {
		return accountTitle;
	}

	public void setAccountTitle(String accountTitle) {
		this.accountTitle = accountTitle;
	}

	public String getCustIBAN() {
		return custIBAN;
	}

	public void setCustIBAN(String custIBAN) {
		this.custIBAN = custIBAN;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public List<BondPurchaseDetail> getPurchaseDetailList() {
		return purchaseDetailList;
	}

	public void setPurchaseDetailList(
			List<BondPurchaseDetail> purchaseDetailList) {
		this.purchaseDetailList = purchaseDetailList;
	}

	public List<BondTransferDetail> getTransferDetailList() {
		return transferDetailList;
	}

	public void setTransferDetailList(
			List<BondTransferDetail> transferDetailList) {
		this.transferDetailList = transferDetailList;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
