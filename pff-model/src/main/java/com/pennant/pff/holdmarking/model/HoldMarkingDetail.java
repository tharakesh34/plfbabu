package com.pennant.pff.holdmarking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class HoldMarkingDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long iD;
	private long headerID;
	private long holdID = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private String holdType;
	private String marking;
	private Date movementDate;
	private String Status;
	private BigDecimal Amount = BigDecimal.ZERO;
	private long logID = Long.MIN_VALUE;
	private String HoldReleaseReason;
	private Long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;
	private String accountNumber;
	private BigDecimal holdAmount = BigDecimal.ZERO;
	private BigDecimal releaseAmount = BigDecimal.ZERO;
	private BigDecimal balance = BigDecimal.ZERO;
	private long holdReference = Long.MIN_VALUE;

	public HoldMarkingDetail() {
		super();
	}

	public long getiD() {
		return iD;
	}

	public void setiD(long iD) {
		this.iD = iD;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
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

	public String getHoldType() {
		return holdType;
	}

	public void setHoldType(String holdType) {
		this.holdType = holdType;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public BigDecimal getAmount() {
		return Amount;
	}

	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}

	public long getLogID() {
		return logID;
	}

	public void setLogID(long logID) {
		this.logID = logID;
	}

	public String getHoldReleaseReason() {
		return HoldReleaseReason;
	}

	public void setHoldReleaseReason(String holdReleaseReason) {
		HoldReleaseReason = holdReleaseReason;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
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

	public long getHoldReference() {
		return holdReference;
	}

	public void setHoldReference(long holdReference) {
		this.holdReference = holdReference;
	}
}