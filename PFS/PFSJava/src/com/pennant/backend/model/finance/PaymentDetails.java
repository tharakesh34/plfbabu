package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class PaymentDetails {
	private String	   finReference;
	private BigDecimal	disbAmount	      = new BigDecimal(0);
	private BigDecimal	downPaymentAmount	= new BigDecimal(0);
	private BigDecimal	cpzAmount	      = new BigDecimal(0);
	private BigDecimal	schdPriPaid	      = new BigDecimal(0);
	private BigDecimal	defPrincipal	  = new BigDecimal(0);
	private BigDecimal	schdPftPaid	      = new BigDecimal(0);

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getDisbAmount() {
		if (disbAmount != null) {
			return disbAmount;
		}
		return new BigDecimal(0);
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public BigDecimal getDownPaymentAmount() {

		if (downPaymentAmount != null) {
			return downPaymentAmount;
		}
		return new BigDecimal(0);
	}

	public void setDownPaymentAmount(BigDecimal downPaymentAmount) {
		this.downPaymentAmount = downPaymentAmount;
	}

	public BigDecimal getCpzAmount() {

		if (cpzAmount != null) {
			return cpzAmount;
		}
		return new BigDecimal(0);
	}

	public void setCpzAmount(BigDecimal cpzAmount) {
		this.cpzAmount = cpzAmount;
	}

	public BigDecimal getSchdPriPaid() {

		if (schdPriPaid != null) {
			return schdPriPaid;
		}
		return new BigDecimal(0);
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getDefPrincipal() {

		if (defPrincipal != null) {
			return defPrincipal;
		}
		return new BigDecimal(0);
	}

	public void setDefPrincipal(BigDecimal defPrincipal) {
		this.defPrincipal = defPrincipal;
	}

	public BigDecimal getSchdPftPaid() {

		if (schdPftPaid != null) {
			return schdPftPaid;
		}
		return new BigDecimal(0);
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

}
