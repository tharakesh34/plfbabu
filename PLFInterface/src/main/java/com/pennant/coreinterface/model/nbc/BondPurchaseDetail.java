package com.pennant.coreinterface.model.nbc;

import java.io.Serializable;
import java.util.Date;

public class BondPurchaseDetail implements Serializable {

	private static final long serialVersionUID = 5456533735093849493L;

	public BondPurchaseDetail() {
		super();
	}

	private String refNumProvider;
	private String productName;
	private String unitStart;
	private String unitEnd;
	private long sukukNo;
	private Date sukukExpDate;
	private long bankInvoiceNo;
	private String purchaseReceiptNo;
	private byte[] bankInvCertificate;
	private byte[] bankReceiptCertifcate;
	private byte[] bankTitleCertifcate;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getRefNumProvider() {
		return refNumProvider;
	}

	public void setRefNumProvider(String refNumProvider) {
		this.refNumProvider = refNumProvider;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUnitStart() {
		return unitStart;
	}

	public void setUnitStart(String unitStart) {
		this.unitStart = unitStart;
	}

	public String getUnitEnd() {
		return unitEnd;
	}

	public void setUnitEnd(String unitEnd) {
		this.unitEnd = unitEnd;
	}

	public long getSukukNo() {
		return sukukNo;
	}

	public void setSukukNo(long sukukNo) {
		this.sukukNo = sukukNo;
	}

	public Date getSukukExpDate() {
		return sukukExpDate;
	}

	public void setSukukExpDate(Date sukukExpDate) {
		this.sukukExpDate = sukukExpDate;
	}

	public long getBankInvoiceNo() {
		return bankInvoiceNo;
	}

	public void setBankInvoiceNo(long bankInvoiceNo) {
		this.bankInvoiceNo = bankInvoiceNo;
	}

	public String getPurchaseReceiptNo() {
		return purchaseReceiptNo;
	}

	public void setPurchaseReceiptNo(String purchaseReceiptNo) {
		this.purchaseReceiptNo = purchaseReceiptNo;
	}

	public byte[] getBankInvCertificate() {
		return bankInvCertificate;
	}

	public void setBankInvCertificate(byte[] bankInvCertificate) {
		this.bankInvCertificate = bankInvCertificate;
	}

	public byte[] getBankReceiptCertifcate() {
		return bankReceiptCertifcate;
	}

	public void setBankReceiptCertifcate(byte[] bankReceiptCertifcate) {
		this.bankReceiptCertifcate = bankReceiptCertifcate;
	}

	public byte[] getBankTitleCertifcate() {
		return bankTitleCertifcate;
	}

	public void setBankTitleCertifcate(byte[] bankTitleCertifcate) {
		this.bankTitleCertifcate = bankTitleCertifcate;
	}
}
