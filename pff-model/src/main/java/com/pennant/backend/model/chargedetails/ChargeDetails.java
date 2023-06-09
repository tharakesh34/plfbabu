package com.pennant.backend.model.chargedetails;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "loan")
@XmlAccessorType(XmlAccessType.NONE)
public class ChargeDetails {

	private long finID;
	private String finReference;
	@XmlElement
	private String chargeTypeDesc;
	@XmlElement
	private BigDecimal chargeRate = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal dueAmount = BigDecimal.ZERO;

	public ChargeDetails() {
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

	public String getChargeTypeDesc() {
		return chargeTypeDesc;
	}

	public void setChargeTypeDesc(String chargeTypeDesc) {
		this.chargeTypeDesc = chargeTypeDesc;
	}

	public BigDecimal getChargeRate() {
		return chargeRate;
	}

	public void setChargeRate(BigDecimal chargeRate) {
		this.chargeRate = chargeRate;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}
}
