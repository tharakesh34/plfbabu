package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ManualAdviseMovements implements Serializable {
	private static final long serialVersionUID = 6112694689541699540L;

	private long movementID = Long.MIN_VALUE;
	private long adviseID = Long.MIN_VALUE;
	private long receiptID = Long.MIN_VALUE;
	private long receiptSeqID = Long.MIN_VALUE;
	private String feeTypeCode;
	private String feeTypeDesc; // Used in SOA and GST Invoice
	private Date movementDate;
	private BigDecimal movementAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private String status;

	// GST Paid Fields
	private BigDecimal paidCGST = BigDecimal.ZERO;
	private BigDecimal paidSGST = BigDecimal.ZERO;
	private BigDecimal paidUGST = BigDecimal.ZERO;
	private BigDecimal paidIGST = BigDecimal.ZERO;

	// GST Waiver Fields
	private BigDecimal waivedCGST = BigDecimal.ZERO;
	private BigDecimal waivedSGST = BigDecimal.ZERO;
	private BigDecimal waivedUGST = BigDecimal.ZERO;
	private BigDecimal waivedIGST = BigDecimal.ZERO;

	private BigDecimal tdsPaid = BigDecimal.ZERO;

	private boolean taxApplicable;
	private String taxComponent;
	private int adviseType;

	// Enquiry Purpose
	private String receiptMode;
	// ### 24-05-2018 SOA Merging from Bajaj to QC
	private Date valueDate;
	private long waiverID = Long.MIN_VALUE;

	private long taxHeaderId = 0;
	private TaxHeader taxHeader;

	// Getters and Setters

	public long getMovementID() {
		return movementID;
	}

	public void setMovementID(long movementID) {
		this.movementID = movementID;
	}

	public long getAdviseID() {
		return adviseID;
	}

	public void setAdviseID(long adviseID) {
		this.adviseID = adviseID;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public BigDecimal getMovementAmount() {
		return movementAmount;
	}

	public void setMovementAmount(BigDecimal movementAmount) {
		this.movementAmount = movementAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public long getReceiptSeqID() {
		return receiptSeqID;
	}

	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public BigDecimal getPaidCGST() {
		return paidCGST;
	}

	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
	}

	public BigDecimal getPaidSGST() {
		return paidSGST;
	}

	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}

	public BigDecimal getPaidUGST() {
		return paidUGST;
	}

	public void setPaidUGST(BigDecimal paidUGST) {
		this.paidUGST = paidUGST;
	}

	public BigDecimal getPaidIGST() {
		return paidIGST;
	}

	public void setPaidIGST(BigDecimal paidIGST) {
		this.paidIGST = paidIGST;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getWaiverID() {
		return waiverID;
	}

	public void setWaiverID(long waiverID) {
		this.waiverID = waiverID;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public int getAdviseType() {
		return adviseType;
	}

	public void setAdviseType(int adviseType) {
		this.adviseType = adviseType;
	}

	public BigDecimal getWaivedCGST() {
		return waivedCGST;
	}

	public void setWaivedCGST(BigDecimal waivedCGST) {
		this.waivedCGST = waivedCGST;
	}

	public BigDecimal getWaivedSGST() {
		return waivedSGST;
	}

	public void setWaivedSGST(BigDecimal waivedSGST) {
		this.waivedSGST = waivedSGST;
	}

	public BigDecimal getWaivedUGST() {
		return waivedUGST;
	}

	public void setWaivedUGST(BigDecimal waivedUGST) {
		this.waivedUGST = waivedUGST;
	}

	public BigDecimal getWaivedIGST() {
		return waivedIGST;
	}

	public void setWaivedIGST(BigDecimal waivedIGST) {
		this.waivedIGST = waivedIGST;
	}

	public long getTaxHeaderId() {
		return taxHeaderId;
	}

	public void setTaxHeaderId(long taxHeaderId) {
		this.taxHeaderId = taxHeaderId;
	}

	public TaxHeader getTaxHeader() {
		return taxHeader;
	}

	public void setTaxHeader(TaxHeader taxHeader) {
		this.taxHeader = taxHeader;
	}

	public BigDecimal getTdsPaid() {
		return tdsPaid;
	}

	public void setTdsPaid(BigDecimal tdsPaid) {
		this.tdsPaid = tdsPaid;
	}

}
