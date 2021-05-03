package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>ScheduleReport { table</b>.<br>
 */
public class SOAScheduleReport {

	private String finReference = "";
	private int instNumber;
	private Date dueDate;
	private BigDecimal interest;
	private BigDecimal principal;
	private BigDecimal instAmount;
	private BigDecimal oustadBal;
	private BigDecimal openBal;
	private int ccyEditField = 0;

	public int getInstNumber() {
		return instNumber;
	}

	public void setInstNumber(int instNumber) {
		this.instNumber = instNumber;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getInstAmount() {
		return instAmount;
	}

	public void setInstAmount(BigDecimal instAmount) {
		this.instAmount = instAmount;
	}

	public BigDecimal getOustadBal() {
		return oustadBal;
	}

	public void setOustadBal(BigDecimal oustadBal) {
		this.oustadBal = oustadBal;
	}

	public BigDecimal getOpenBal() {
		return openBal;
	}

	public void setOpenBal(BigDecimal openBal) {
		this.openBal = openBal;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

}
