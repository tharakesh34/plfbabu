package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ManualAdviseMovements implements Serializable {
	private static final long serialVersionUID = 6112694689541699540L;

	private long movementID = Long.MIN_VALUE;
	private long adviseID = Long.MIN_VALUE;
	private long receiptID = Long.MIN_VALUE;
	private long receiptSeqID = Long.MIN_VALUE;
	private String feeTypeCode;
	private String feeTypeDesc;
	private Date movementDate;
	private BigDecimal movementAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private BigDecimal tdsPaid = BigDecimal.ZERO;
	private String status;

	// GST Paid Fields
	private BigDecimal paidCGST = BigDecimal.ZERO;
	private BigDecimal paidSGST = BigDecimal.ZERO;
	private BigDecimal paidUGST = BigDecimal.ZERO;
	private BigDecimal paidIGST = BigDecimal.ZERO;
	private BigDecimal paidCESS = BigDecimal.ZERO;

	// GST Waiver Fields
	private BigDecimal waivedCGST = BigDecimal.ZERO;
	private BigDecimal waivedSGST = BigDecimal.ZERO;
	private BigDecimal waivedUGST = BigDecimal.ZERO;
	private BigDecimal waivedIGST = BigDecimal.ZERO;
	private BigDecimal waivedCESS = BigDecimal.ZERO;

	private boolean taxApplicable;
	private String taxComponent;
	private int adviseType;

	private String receiptMode;
	private Date valueDate;
	private long waiverID = Long.MIN_VALUE;

	private Long taxHeaderId;
	private Long debitInvoiceId;
	private TaxHeader taxHeader;
	private Date schDate;
	private boolean lppAmzReqonME;
	private BigDecimal currWaiverGst = BigDecimal.ZERO;
	private boolean tdsReq;
	private Long insReceiptID;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("tdsReq");

		return excludeFields;
	}

	public ManualAdviseMovements copyEntity() {
		ManualAdviseMovements entity = new ManualAdviseMovements();
		entity.setMovementID(this.movementID);
		entity.setAdviseID(this.adviseID);
		entity.setReceiptID(this.receiptID);
		entity.setReceiptSeqID(this.receiptSeqID);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setFeeTypeDesc(this.feeTypeDesc);
		entity.setMovementDate(this.movementDate);
		entity.setMovementAmount(this.movementAmount);
		entity.setPaidAmount(this.paidAmount);
		entity.setWaivedAmount(this.waivedAmount);
		entity.setTdsPaid(this.tdsPaid);
		entity.setStatus(this.status);
		entity.setPaidCGST(this.paidCGST);
		entity.setPaidSGST(this.paidSGST);
		entity.setPaidUGST(this.paidUGST);
		entity.setPaidIGST(this.paidIGST);
		entity.setPaidCESS(this.paidCESS);
		entity.setWaivedCGST(this.waivedCGST);
		entity.setWaivedSGST(this.waivedSGST);
		entity.setWaivedUGST(this.waivedUGST);
		entity.setWaivedIGST(this.waivedIGST);
		entity.setWaivedCESS(this.waivedCESS);
		entity.setTaxApplicable(this.taxApplicable);
		entity.setTaxComponent(this.taxComponent);
		entity.setAdviseType(this.adviseType);
		entity.setReceiptMode(this.receiptMode);
		entity.setValueDate(this.valueDate);
		entity.setWaiverID(this.waiverID);
		entity.setTaxHeaderId(this.taxHeaderId);
		entity.setDebitInvoiceId(this.debitInvoiceId);
		entity.setTaxHeader(this.taxHeader == null ? null : this.taxHeader.copyEntity());
		entity.setSchDate(this.schDate);
		entity.setLppAmzReqonME(this.lppAmzReqonME);
		entity.setCurrWaiverGst(this.currWaiverGst);
		entity.setTdsReq(this.tdsReq);
		entity.setInsReceiptID(this.insReceiptID);
		return entity;
	}

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

	public BigDecimal getPaidCESS() {
		return paidCESS;
	}

	public void setPaidCESS(BigDecimal paidCESS) {
		this.paidCESS = paidCESS;
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

	public BigDecimal getWaivedCESS() {
		return waivedCESS;
	}

	public void setWaivedCESS(BigDecimal waivedCESS) {
		this.waivedCESS = waivedCESS;
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

	public Long getTaxHeaderId() {
		return taxHeaderId;
	}

	public void setTaxHeaderId(Long taxHeaderId) {
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

	public Long getDebitInvoiceId() {
		return debitInvoiceId;
	}

	public void setDebitInvoiceId(Long debitInvoiceId) {
		this.debitInvoiceId = debitInvoiceId;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public boolean isLppAmzReqonME() {
		return lppAmzReqonME;
	}

	public void setLppAmzReqonME(boolean lppAmzReqonME) {
		this.lppAmzReqonME = lppAmzReqonME;
	}

	public BigDecimal getCurrWaiverGst() {
		return currWaiverGst;
	}

	public void setCurrWaiverGst(BigDecimal currWaiverGst) {
		this.currWaiverGst = currWaiverGst;
	}

	public boolean isTdsReq() {
		return tdsReq;
	}

	public void setTdsReq(boolean tdsReq) {
		this.tdsReq = tdsReq;
	}

	public Long getInsReceiptID() {
		return insReceiptID;
	}

	public void setInsReceiptID(Long insReceiptID) {
		this.insReceiptID = insReceiptID;
	}

}
