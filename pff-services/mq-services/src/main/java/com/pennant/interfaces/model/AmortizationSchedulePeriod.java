package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AmortizationSchedulePeriod")
public class AmortizationSchedulePeriod {

	private int installmentNo;
	private Date scheduleDate;
	private BigDecimal openingBalance;
	private BigDecimal installmentAmount;
	private BigDecimal principalAmount;
	private BigDecimal profitAmount;
	private BigDecimal pastDueAmount;
	private BigDecimal chargeAmount;
	private BigDecimal closingBalance;
	private String status;
	private int ccyEditField;
	private String finCategory;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setInstallmentNo(int installmentNo) {
		this.installmentNo = installmentNo;
	}

	@XmlElement(name = "InstallmentNo")
	public int getInstallmentNo() {
		return this.installmentNo;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	@XmlElement(name = "ScheduleDate")
	public Date getScheduleDate() {
		return this.scheduleDate;
	}

	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}

	@XmlElement(name = "OpeningBalance")
	public BigDecimal getOpeningBalance() {
		return this.openingBalance;
	}

	public void setInstallmentAmount(BigDecimal installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	@XmlElement(name = "InstallmentAmount")
	public BigDecimal getInstallmentAmount() {
		return this.installmentAmount;
	}

	public void setPrincipalAmount(BigDecimal principalAmount) {
		this.principalAmount = principalAmount;
	}

	@XmlElement(name = "PrincipalAmount")
	public BigDecimal getPrincipalAmount() {
		return this.principalAmount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	@XmlElement(name = "ProfitAmount")
	public BigDecimal getProfitAmount() {
		return this.profitAmount;
	}

	public void setPastDueAmount(BigDecimal pastDueAmount) {
		this.pastDueAmount = pastDueAmount;
	}

	@XmlElement(name = "PastDueAmount")
	public BigDecimal getPastDueAmount() {
		return this.pastDueAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	@XmlElement(name = "ChargeAmount")
	public BigDecimal getChargeAmount() {
		return this.chargeAmount;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	@XmlElement(name = "ClosingBalance")
	public BigDecimal getClosingBalance() {
		return this.closingBalance;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement(name = "Status")
	public String getStatus() {
		return this.status;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

}
