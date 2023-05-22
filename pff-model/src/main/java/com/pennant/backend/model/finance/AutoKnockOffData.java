package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.eventproperties.EventProperties;

public class AutoKnockOffData implements Serializable {
	private static final long serialVersionUID = -388636206896888118L;

	private long finID;
	private String finReference;
	private Date valueDate;
	private List<AutoKnockOffFeeMapping> feeMappingList;
	private long receiptId;
	private boolean isfirst = true;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal utilzedAmount = BigDecimal.ZERO;
	private BigDecimal balAmount = BigDecimal.ZERO;
	private long payableId;
	private String payableType;
	private String reason;
	private EventProperties eventProperties = new EventProperties();
	private boolean crossLoanAutoKnockOff;

	public AutoKnockOffData() {
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

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public List<AutoKnockOffFeeMapping> getFeeMappingList() {
		return feeMappingList;
	}

	public void setFeeMappingList(List<AutoKnockOffFeeMapping> feeMappingList) {
		this.feeMappingList = feeMappingList;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public boolean isIsfirst() {
		return isfirst;
	}

	public void setIsfirst(boolean isfirst) {
		this.isfirst = isfirst;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getUtilzedAmount() {
		return utilzedAmount;
	}

	public void setUtilzedAmount(BigDecimal utilzedAmount) {
		this.utilzedAmount = utilzedAmount;
	}

	public BigDecimal getBalAmount() {
		return balAmount;
	}

	public void setBalAmount(BigDecimal balAmount) {
		this.balAmount = balAmount;
	}

	public long getPayableId() {
		return payableId;
	}

	public void setPayableId(long payableId) {
		this.payableId = payableId;
	}

	public String getPayableType() {
		return payableType;
	}

	public void setPayableType(String payableType) {
		this.payableType = payableType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public boolean isCrossLoanAutoKnockOff() {
		return crossLoanAutoKnockOff;
	}

	public void setCrossLoanAutoKnockOff(boolean crossLoanAutoKnockOff) {
		this.crossLoanAutoKnockOff = crossLoanAutoKnockOff;
	}

}
