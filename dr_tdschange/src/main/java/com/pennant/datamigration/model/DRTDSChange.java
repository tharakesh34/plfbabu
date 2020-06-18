package com.pennant.datamigration.model;

import java.math.BigDecimal;
import java.util.Date;

public class DRTDSChange {

	private String finReference;
	private String finType;
	private Date appDate;
	private String status;
	private String reason;
	private Date tDSStartDate;
	private Date schStartDate = null;
	private BigDecimal oldTDSAmt = BigDecimal.ZERO;
	private BigDecimal newTDSAmt = BigDecimal.ZERO;
	private BigDecimal tDSChange = BigDecimal.ZERO;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public BigDecimal getOldTDSAmt() {
		return oldTDSAmt;
	}

	public void setOldTDSAmt(BigDecimal oldTDSAmt) {
		this.oldTDSAmt = oldTDSAmt;
	}

	public BigDecimal getNewTDSAmt() {
		return newTDSAmt;
	}

	public void setNewTDSAmt(BigDecimal newTDSAmt) {
		this.newTDSAmt = newTDSAmt;
	}

	public BigDecimal getTDSChange() {
		return tDSChange;
	}

	public void setTDSChange(BigDecimal tDSChange) {
		this.tDSChange = tDSChange;
	}

	public Date getTDSStartDate() {
		return tDSStartDate;
	}

	public void setTDSStartDate(Date tDSStartDate) {
		this.tDSStartDate = tDSStartDate;
	}

	public Date getSchStartDate() {
		return schStartDate;
	}

	public void setSchStartDate(Date schStartDate) {
		this.schStartDate = schStartDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}