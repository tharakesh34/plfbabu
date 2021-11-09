package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RescheduleLog implements Serializable {
	private static final long serialVersionUID = 1L;

	private String custName;
	private String finBranch;
	private Date transactionDate;
	private String transactionType;
	private long finID;
	private String finReference;
	private int oldTenure;
	private int newTenure;
	private BigDecimal oldEMIAmt = BigDecimal.ZERO;
	private BigDecimal newEMIAmt = BigDecimal.ZERO;

	public RescheduleLog() {
		super();
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
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

	public int getOldTenure() {
		return oldTenure;
	}

	public void setOldTenure(int oldTenure) {
		this.oldTenure = oldTenure;
	}

	public int getNewTenure() {
		return newTenure;
	}

	public void setNewTenure(int newTenure) {
		this.newTenure = newTenure;
	}

	public BigDecimal getOldEMIAmt() {
		return oldEMIAmt;
	}

	public void setOldEMIAmt(BigDecimal oldEMIAmt) {
		this.oldEMIAmt = oldEMIAmt;
	}

	public BigDecimal getNewEMIAmt() {
		return newEMIAmt;
	}

	public void setNewEMIAmt(BigDecimal newEMIAmt) {
		this.newEMIAmt = newEMIAmt;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
}
