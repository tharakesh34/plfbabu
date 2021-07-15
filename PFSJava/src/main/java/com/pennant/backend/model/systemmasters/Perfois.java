package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Perfois {

	private BigDecimal salary;
	@XmlElement
	private BigDecimal emi;
	@XmlElement
	private BigDecimal totalAmountofEMIbounces;
	@XmlElement
	private BigDecimal grossReceipts;

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public BigDecimal getEmi() {
		return emi;
	}

	public void setEmi(BigDecimal emi) {
		this.emi = emi;
	}

	public BigDecimal getTotalAmountofEMIbounces() {
		return totalAmountofEMIbounces;
	}

	public void setTotalAmountofEMIbounces(BigDecimal totalAmountofEMIbounces) {
		this.totalAmountofEMIbounces = totalAmountofEMIbounces;
	}

	public BigDecimal getGrossReceipts() {
		return grossReceipts;
	}

	public void setGrossReceipts(BigDecimal grossReceipts) {
		this.grossReceipts = grossReceipts;
	}

}
