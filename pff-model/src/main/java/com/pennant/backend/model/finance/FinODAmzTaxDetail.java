package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinODAmzTaxDetail implements Serializable {

	private static final long serialVersionUID = -5722811453434523809L;

	private long taxSeqId = 0;
	private long finID;
	private String finReference;
	private Date valueDate;
	private Date postDate;
	private String taxFor;
	private BigDecimal amount = BigDecimal.ZERO;
	private String taxType;
	private BigDecimal CGST = BigDecimal.ZERO;
	private BigDecimal SGST = BigDecimal.ZERO;
	private BigDecimal UGST = BigDecimal.ZERO;
	private BigDecimal IGST = BigDecimal.ZERO;
	private BigDecimal CESS = BigDecimal.ZERO;
	private BigDecimal TotalGST = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private Long invoiceID;

	public FinODAmzTaxDetail() {
	    super();
	}

	public long getTaxSeqId() {
		return taxSeqId;
	}

	public void setTaxSeqId(long taxSeqId) {
		this.taxSeqId = taxSeqId;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCGST() {
		return CGST;
	}

	public void setCGST(BigDecimal cGST) {
		CGST = cGST;
	}

	public BigDecimal getSGST() {
		return SGST;
	}

	public void setSGST(BigDecimal sGST) {
		SGST = sGST;
	}

	public BigDecimal getUGST() {
		return UGST;
	}

	public void setUGST(BigDecimal uGST) {
		UGST = uGST;
	}

	public BigDecimal getIGST() {
		return IGST;
	}

	public void setIGST(BigDecimal iGST) {
		IGST = iGST;
	}

	public BigDecimal getCESS() {
		return CESS;
	}

	public void setCESS(BigDecimal cESS) {
		CESS = cESS;
	}

	public BigDecimal getTotalGST() {
		return TotalGST;
	}

	public void setTotalGST(BigDecimal totalGST) {
		TotalGST = totalGST;
	}

	public String getTaxFor() {
		return taxFor;
	}

	public void setTaxFor(String taxFor) {
		this.taxFor = taxFor;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
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

	public Long getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(Long invoiceID) {
		this.invoiceID = invoiceID;
	}

}
