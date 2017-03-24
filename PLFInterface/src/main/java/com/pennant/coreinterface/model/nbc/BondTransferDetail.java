package com.pennant.coreinterface.model.nbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BondTransferDetail implements Serializable {

	private static final long serialVersionUID = 5434863560912488094L;

	public BondTransferDetail() {
		super();
	}

	private String refNumProvider;
	private String unitStart;
	private String unitEnd;
	private long sukukNo;
	private String purchaseReceiptNo;
	private BigDecimal purchaseRemainBal = BigDecimal.ZERO;
	private Date sukukExpDate;
	private byte[] titleCertificate;
	private byte[] invoiceCertificate;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getRefNumProvider() {
		return refNumProvider;
	}

	public void setRefNumProvider(String refNumProvider) {
		this.refNumProvider = refNumProvider;
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

	public String getPurchaseReceiptNo() {
		return purchaseReceiptNo;
	}

	public void setPurchaseReceiptNo(String purchaseReceiptNo) {
		this.purchaseReceiptNo = purchaseReceiptNo;
	}

	public BigDecimal getPurchaseRemainBal() {
		return purchaseRemainBal;
	}

	public void setPurchaseRemainBal(BigDecimal purchaseRemainBal) {
		this.purchaseRemainBal = purchaseRemainBal;
	}

	public Date getSukukExpDate() {
		return sukukExpDate;
	}

	public void setSukukExpDate(Date sukukExpDate) {
		this.sukukExpDate = sukukExpDate;
	}

	public byte[] getTitleCertificate() {
		return titleCertificate;
	}

	public void setTitleCertificate(byte[] titleCertificate) {
		this.titleCertificate = titleCertificate;
	}

	public byte[] getInvoiceCertificate() {
		return invoiceCertificate;
	}

	public void setInvoiceCertificate(byte[] invoiceCertificate) {
		this.invoiceCertificate = invoiceCertificate;
	}
}
