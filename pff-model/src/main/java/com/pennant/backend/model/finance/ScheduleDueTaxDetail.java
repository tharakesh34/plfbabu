package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ScheduleDueTaxDetail implements Serializable {
	private static final long serialVersionUID = -5722811453434523809L;

	private long finID;
	private String finReference;
	private Date schDate;
	private String taxType;
	private String taxCalcOn;
	private BigDecimal amount = BigDecimal.ZERO;
	private Long invoiceID;

	public ScheduleDueTaxDetail() {
		super();
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

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public String getTaxCalcOn() {
		return taxCalcOn;
	}

	public void setTaxCalcOn(String taxCalcOn) {
		this.taxCalcOn = taxCalcOn;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(Long invoiceID) {
		this.invoiceID = invoiceID;
	}

}
