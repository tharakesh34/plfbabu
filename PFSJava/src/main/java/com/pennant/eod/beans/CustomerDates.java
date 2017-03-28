package com.pennant.eod.beans;

import java.util.Date;

public class CustomerDates {

	private Date	appDate;
	private Date	valueDate;
	private Date	nextBusinessDate;

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getNextBusinessDate() {
		return nextBusinessDate;
	}

	public void setNextBusinessDate(Date nextBusinessDate) {
		this.nextBusinessDate = nextBusinessDate;
	}

}
