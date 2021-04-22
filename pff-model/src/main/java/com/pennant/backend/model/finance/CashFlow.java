package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class CashFlow extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;
	private Date date;
	private String lan;
	private BigDecimal disb;
	private BigDecimal subventionAmount;
	private BigDecimal pfReceipt;
	private BigDecimal principalCollection;
	private BigDecimal interestCollection;
	private BigDecimal prePayment;
	private BigDecimal forClosure;
	private String type;

	public CashFlow() {
		super();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLan() {
		return lan;
	}

	public void setLan(String lan) {
		this.lan = lan;
	}

	public BigDecimal getDisb() {
		return disb;
	}

	public void setDisb(BigDecimal disb) {
		this.disb = disb;
	}

	public BigDecimal getSubventionAmount() {
		return subventionAmount;
	}

	public void setSubventionAmount(BigDecimal subventionAmount) {
		this.subventionAmount = subventionAmount;
	}

	public BigDecimal getPfReceipt() {
		return pfReceipt;
	}

	public void setPfReceipt(BigDecimal pfReceipt) {
		this.pfReceipt = pfReceipt;
	}

	public BigDecimal getPrincipalCollection() {
		return principalCollection;
	}

	public void setPrincipalCollection(BigDecimal principalCollection) {
		this.principalCollection = principalCollection;
	}

	public BigDecimal getInterestCollection() {
		return interestCollection;
	}

	public void setInterestCollection(BigDecimal interestCollection) {
		this.interestCollection = interestCollection;
	}

	public BigDecimal getPrePayment() {
		return prePayment;
	}

	public void setPrePayment(BigDecimal prePayment) {
		this.prePayment = prePayment;
	}

	public BigDecimal getForClosure() {
		return forClosure;
	}

	public void setForClosure(BigDecimal forClosure) {
		this.forClosure = forClosure;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
