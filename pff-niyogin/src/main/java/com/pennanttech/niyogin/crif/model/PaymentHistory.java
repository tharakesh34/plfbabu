package com.pennanttech.niyogin.crif.model;

import java.util.Date;

public class PaymentHistory {

	private Date	paymentDate;
	private String	type;
	private String	ddp;

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

	public String getDdp() {
		return ddp;
	}

	public void setDdp(String ddp) {
		this.ddp = ddp;
	}

	@Override
	public String toString() {
		return "PaymentHistory [paymentDate=" + paymentDate + ", type=" + type + ", ddp=" + ddp + "]";
	}
	
	

}
