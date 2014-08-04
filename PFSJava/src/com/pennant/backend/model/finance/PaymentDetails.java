package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class PaymentDetails {
	private String	   finReference;
	private BigDecimal	disbAmount	      = BigDecimal.ZERO;
	private BigDecimal	downPaymentAmount	= BigDecimal.ZERO;
	private BigDecimal	cpzAmount	      = BigDecimal.ZERO;
	private BigDecimal	schdPriPaid	      = BigDecimal.ZERO;
	private BigDecimal	defPrincipal	  = BigDecimal.ZERO;
	private BigDecimal	schdPftPaid	      = BigDecimal.ZERO;

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
		return BigDecimal.ZERO;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public BigDecimal getDownPaymentAmount() {

		if (downPaymentAmount != null) {
			return downPaymentAmount;
		}
		return BigDecimal.ZERO;
	}

	public void setDownPaymentAmount(BigDecimal downPaymentAmount) {
		this.downPaymentAmount = downPaymentAmount;
	}

	public BigDecimal getCpzAmount() {

		if (cpzAmount != null) {
			return cpzAmount;
		}
		return BigDecimal.ZERO;
	}

	public void setCpzAmount(BigDecimal cpzAmount) {
		this.cpzAmount = cpzAmount;
	}

	public BigDecimal getSchdPriPaid() {

		if (schdPriPaid != null) {
			return schdPriPaid;
		}
		return BigDecimal.ZERO;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getDefPrincipal() {

		if (defPrincipal != null) {
			return defPrincipal;
		}
		return BigDecimal.ZERO;
	}

	public void setDefPrincipal(BigDecimal defPrincipal) {
		this.defPrincipal = defPrincipal;
	}

	public BigDecimal getSchdPftPaid() {

		if (schdPftPaid != null) {
			return schdPftPaid;
		}
		return BigDecimal.ZERO;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

}
