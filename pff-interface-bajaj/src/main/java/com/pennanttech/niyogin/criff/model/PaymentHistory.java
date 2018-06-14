package com.pennanttech.niyogin.criff.model;

import java.util.Date;

public class PaymentHistory {	
	private Date	paymentDate;
	private String	type;
	private String	dpd;

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDpd() {
		return dpd;
	}

	public void setDpd(String dpd) {
		this.dpd = dpd;
	}
}
