package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "avgBankBalance", "inwardChequeReturns", "odccLimit", "amotOfCreditTransactions", "intoDcc" })
@XmlRootElement(name = "Perfios")
@XmlAccessorType(XmlAccessType.FIELD)
public class Perfios {

	@XmlElement(name = "averagebankbalance")
	private BigDecimal	avgBankBalance	= BigDecimal.ZERO;

	@XmlElement(name = "inwardchequereturns")
	private int			inwardChequeReturns;

	@XmlElement(name = "odcclimit")
	private BigDecimal	odccLimit		= BigDecimal.ZERO;

	@XmlElement(name = "amountofcredittransactions")
	private int			amotOfCreditTransactions;

	@XmlElement(name = "intodcc")
	private String		intoDcc;

	public BigDecimal getAvgBankBalance() {
		return avgBankBalance;
	}

	public void setAvgBankBalance(BigDecimal avgBankBalance) {
		this.avgBankBalance = avgBankBalance;
	}

	public int getInwardChequeReturns() {
		return inwardChequeReturns;
	}

	public void setInwardChequeReturns(int inwardChequeReturns) {
		this.inwardChequeReturns = inwardChequeReturns;
	}

	public BigDecimal getOdccLimit() {
		return odccLimit;
	}

	public void setOdccLimit(BigDecimal odccLimit) {
		this.odccLimit = odccLimit;
	}

	public int getAmotOfCreditTransactions() {
		return amotOfCreditTransactions;
	}

	public void setAmotOfCreditTransactions(int amotOfCreditTransactions) {
		this.amotOfCreditTransactions = amotOfCreditTransactions;
	}

	public String getIntoDcc() {
		return intoDcc;
	}

	public void setIntoDcc(String intoDcc) {
		this.intoDcc = intoDcc;
	}

}
