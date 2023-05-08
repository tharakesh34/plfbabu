package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ReceiptCancelDetail implements Serializable {

	private static final long serialVersionUID = -5722811453434523809L;

	private long receiptId = 0;
	private Date valueDate;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal waviedAmt = BigDecimal.ZERO;
	private String action;

	public ReceiptCancelDetail() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public BigDecimal getWaviedAmt() {
		return waviedAmt;
	}

	public void setWaviedAmt(BigDecimal waviedAmt) {
		this.waviedAmt = waviedAmt;
	}

}
