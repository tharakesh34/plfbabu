package com.pennant.backend.model.reports;

import java.math.BigDecimal;
import java.util.Date;

public class AvailPastDue {

	private long custID = Long.MIN_VALUE;
	private BigDecimal pastDueAmount = BigDecimal.ZERO;
	private Date pastDueFrom;
	private int dueDays = 0;

	public AvailPastDue() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public BigDecimal getPastDueAmount() {
		return pastDueAmount;
	}

	public void setPastDueAmount(BigDecimal pastDueAmount) {
		this.pastDueAmount = pastDueAmount;
	}

	public Date getPastDueFrom() {
		return pastDueFrom;
	}

	public void setPastDueFrom(Date pastDueFrom) {
		this.pastDueFrom = pastDueFrom;
	}

	public int getDueDays() {
		return dueDays;
	}

	public void setDueDays(int dueDays) {
		this.dueDays = dueDays;
	}

}
