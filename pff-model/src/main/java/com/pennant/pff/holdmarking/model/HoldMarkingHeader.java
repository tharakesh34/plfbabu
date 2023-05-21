package com.pennant.pff.holdmarking.model;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class HoldMarkingHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long holdID = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private long holdReference = Long.MIN_VALUE;
	private String accountNumber;
	private BigDecimal holdAmount = BigDecimal.ZERO;
	private BigDecimal releaseAmount = BigDecimal.ZERO;
	private BigDecimal balance = BigDecimal.ZERO;
	private int curODDays;
	private String FinType;
	private Date FinStartDate;
	private BigDecimal totalPriBal;
	private BigDecimal removalAmount = BigDecimal.ZERO;
	private boolean writeoffLoan = false;
	private String finRepayMethod;

	public HoldMarkingHeader() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHoldID() {
		return holdID;
	}

	public void setHoldID(long holdID) {
		this.holdID = holdID;
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

	public long getHoldReference() {
		return holdReference;
	}

	public void setHoldReference(long holdReference) {
		this.holdReference = holdReference;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getHoldAmount() {
		return holdAmount;
	}

	public void setHoldAmount(BigDecimal holdAmount) {
		this.holdAmount = holdAmount;
	}

	public BigDecimal getReleaseAmount() {
		return releaseAmount;
	}

	public void setReleaseAmount(BigDecimal releaseAmount) {
		this.releaseAmount = releaseAmount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public int getCurODDays() {
		return curODDays;
	}

	public void setCurODDays(int curODDays) {
		this.curODDays = curODDays;
	}

	public String getFinType() {
		return FinType;
	}

	public void setFinType(String finType) {
		FinType = finType;
	}

	public Date getFinStartDate() {
		return FinStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		FinStartDate = finStartDate;
	}

	public BigDecimal getTotalPriBal() {
		return totalPriBal;
	}

	public void setTotalPriBal(BigDecimal totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getRemovalAmount() {
		return removalAmount;
	}

	public void setRemovalAmount(BigDecimal removalAmount) {
		this.removalAmount = removalAmount;
	}

	public boolean isWriteoffLoan() {
		return writeoffLoan;
	}

	public void setWriteoffLoan(boolean writeoffLoan) {
		this.writeoffLoan = writeoffLoan;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}
}