package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class FinTaxReceivable {

	private String finReference;
	private String taxFor;
	private BigDecimal receivableAmount = BigDecimal.ZERO;
	private BigDecimal CGST = BigDecimal.ZERO;
	private BigDecimal SGST = BigDecimal.ZERO;
	private BigDecimal UGST = BigDecimal.ZERO;
	private BigDecimal IGST = BigDecimal.ZERO;
	private BigDecimal CESS = BigDecimal.ZERO;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getTaxFor() {
		return taxFor;
	}

	public void setTaxFor(String taxFor) {
		this.taxFor = taxFor;
	}

	public BigDecimal getReceivableAmount() {
		return receivableAmount;
	}

	public void setReceivableAmount(BigDecimal receivableAmount) {
		this.receivableAmount = receivableAmount;
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

}
