package com.pennant.datamigration.model;

import java.math.BigDecimal;
import java.util.Date;

public class BlockedFinance {

	private String finReference;
	private Date blockedDate;
	private String remarks;
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public Date getBlockedDate() {
		return blockedDate;
	}
	public void setBlockedDate(Date blockedDate) {
		this.blockedDate = blockedDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
