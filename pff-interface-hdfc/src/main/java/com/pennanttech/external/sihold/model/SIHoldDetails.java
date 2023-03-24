package com.pennanttech.external.sihold.model;

import java.math.BigDecimal;
import java.util.Date;

public class SIHoldDetails {
	private String account;
	private String loanRef;
	private Date schDate;
	private BigDecimal holdAmt;
	private int fileStatus;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLoanRef() {
		return loanRef;
	}

	public void setLoanRef(String loanRef) {
		this.loanRef = loanRef;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public BigDecimal getHoldAmt() {
		return holdAmt;
	}

	public void setHoldAmt(BigDecimal holdAmt) {
		this.holdAmt = holdAmt;
	}

	public int getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(int fileStatus) {
		this.fileStatus = fileStatus;
	}

	@Override
	public String toString() {
		return "SIHoldDetails [account=" + account + ", loanRef=" + loanRef + ", schDate=" + schDate + ", holdAmt="
				+ holdAmt + ", fileStatus=" + fileStatus + "]";
	}

}
