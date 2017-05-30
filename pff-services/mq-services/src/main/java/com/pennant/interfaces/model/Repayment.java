package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Repayment")
public class Repayment {
	
	private String financeType;
	private String financeRef;
	private Date scheduleDate;
	private String currency;
	private BigDecimal installmentAmount;
	private int ccyEditField;	
	
	@XmlElement(name = "FinanceType")
	public String getFinanceType() {
		return financeType;
	}
	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}
	
	@XmlElement(name = "FinanceRef")
	public String getFinanceRef() {
		return financeRef;
	}
	public void setFinanceRef(String financeRef) {
		this.financeRef = financeRef;
	}
	
	@XmlElement(name = "ScheduleDate")
	public Date getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	
	@XmlElement(name = "Currency")
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@XmlElement(name = "InstallmentAmount")
	public BigDecimal getInstallmentAmount() {
		return installmentAmount;
	}
	public void setInstallmentAmount(BigDecimal installmentAmount) {
		this.installmentAmount = installmentAmount;
	}
	
	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

}
