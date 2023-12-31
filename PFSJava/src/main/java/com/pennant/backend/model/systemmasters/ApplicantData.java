package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicantData {

	@XmlElement
	private BigDecimal finalIncome;
	@XmlElement
	private BigDecimal finalObligation;

	public BigDecimal getFinalIncome() {
		return finalIncome;
	}

	public void setFinalIncome(BigDecimal finalIncome) {
		this.finalIncome = finalIncome;
	}

	public BigDecimal getFinalObligation() {
		return finalObligation;
	}

	public void setFinalObligation(BigDecimal finalObligation) {
		this.finalObligation = finalObligation;
	}

}
