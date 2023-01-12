package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class AutoRefundLoan implements Serializable {
	private static final long serialVersionUID = -5852740108623344237L;

	private long finID;
	private String finReference;
	private int dpdDays;
	private BigDecimal maxRefundAmt = BigDecimal.ZERO;
	private BigDecimal minRefundAmt = BigDecimal.ZERO;
	private String finRepayMethod;
	private boolean finIsActive;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String finCcy;
	private String closingStatus;
	private String holdStatus;
	private BigDecimal refundAmt = BigDecimal.ZERO;
	private Date appDate;
	private Timestamp executionTime;
	private String status;
	private String errorCode;
	private String finType;
	private String entityCode;

	public AutoRefundLoan() {
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

	public int getDpdDays() {
		return dpdDays;
	}

	public void setDpdDays(int dpdDays) {
		this.dpdDays = dpdDays;
	}

	public BigDecimal getMaxRefundAmt() {
		return maxRefundAmt;
	}

	public void setMaxRefundAmt(BigDecimal maxRefundAmt) {
		this.maxRefundAmt = maxRefundAmt;
	}

	public BigDecimal getMinRefundAmt() {
		return minRefundAmt;
	}

	public void setMinRefundAmt(BigDecimal minRefundAmt) {
		this.minRefundAmt = minRefundAmt;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public String getHoldStatus() {
		return holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	public BigDecimal getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(BigDecimal refundAmt) {
		this.refundAmt = refundAmt;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Timestamp getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Timestamp executionTime) {
		this.executionTime = executionTime;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

}
