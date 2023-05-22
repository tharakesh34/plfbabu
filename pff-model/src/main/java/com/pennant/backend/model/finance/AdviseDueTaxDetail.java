package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;

public class AdviseDueTaxDetail implements Serializable {
	private static final long serialVersionUID = -5722811453434523809L;

	private long adviseID = 0;
	private String taxType;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal CGST = BigDecimal.ZERO;
	private BigDecimal SGST = BigDecimal.ZERO;
	private BigDecimal UGST = BigDecimal.ZERO;
	private BigDecimal IGST = BigDecimal.ZERO;
	private BigDecimal CESS = BigDecimal.ZERO;
	private BigDecimal TotalGST = BigDecimal.ZERO;
	private Long invoiceID;

	public AdviseDueTaxDetail() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getAdviseID() {
		return adviseID;
	}

	public void setAdviseID(long adviseID) {
		this.adviseID = adviseID;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
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

	public BigDecimal getTotalGST() {
		return TotalGST;
	}

	public void setTotalGST(BigDecimal totalGST) {
		TotalGST = totalGST;
	}

	public BigDecimal getCESS() {
		return CESS;
	}

	public void setCESS(BigDecimal cESS) {
		CESS = cESS;
	}

	public Long getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(Long invoiceID) {
		this.invoiceID = invoiceID;
	}

}
