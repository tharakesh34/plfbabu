package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Finance")
public class Finance {
	
	private String financeRef;
	private String branch;
	private String currency;
	private Date startDate;
	private BigDecimal installmentAmount;
	private BigDecimal outstandingAmount;
	private BigDecimal financeAmount;
	private Date dueDate;
	private int remainingInstallments;
	private int totalInstallments;
	private String financeType;
	private int daysPastDue;
	private String customerName;
	private BigDecimal profitRate;
	private String productType;
	private BigDecimal marginRate;
	private BigDecimal baseRate;
	private BigDecimal allInRate;
	private BigDecimal minRate;
	private BigDecimal bankRatio;
	private BigDecimal custRatio;
	private BigDecimal bankProfit;
	private BigDecimal principalPaid;
	private BigDecimal profitPaid;
	private Date maturityDate;
	private String repaymentAccount;
	private BigDecimal profitAmount;
	private BigDecimal totalAmount;
	private BigDecimal unearnedProfit;
	private BigDecimal outstandingBalance;
	private String financeStatus;
	private int ccyEditField;

	public void setFinanceRef(String financeRef) {
		this.financeRef = financeRef;
	}

	@XmlElement(name = "FinanceRef")
	public String getFinanceRef() {
		return this.financeRef;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	@XmlElement(name = "Branch")
	public String getBranch() {
		return this.branch;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@XmlElement(name = "Currency")
	public String getCurrency() {
		return this.currency;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@XmlElement(name = "StartDate")
	public Date getStartDate() {
		return this.startDate;
	}

	public void setInstallmentAmount(BigDecimal installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	@XmlElement(name = "InstallmentAmount")
	public BigDecimal getInstallmentAmount() {
		return this.installmentAmount;
	}

	public void setOutstandingAmount(BigDecimal outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	@XmlElement(name = "OutstandingAmount")
	public BigDecimal getOutstandingAmount() {
		return this.outstandingAmount;
	}

	public void setFinanceAmount(BigDecimal financeAmount) {
		this.financeAmount = financeAmount;
	}

	@XmlElement(name = "FinanceAmount")
	public BigDecimal getFinanceAmount() {
		return this.financeAmount;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@XmlElement(name = "DueDate")
	public Date getDueDate() {
		return this.dueDate;
	}

	public void setRemainingInstallments(int remainingInstallments) {
		this.remainingInstallments = remainingInstallments;
	}

	@XmlElement(name = "RemainingInstallments")
	public int getRemainingInstallments() {
		return this.remainingInstallments;
	}

	public void setTotalInstallments(int totalInstallments) {
		this.totalInstallments = totalInstallments;
	}

	@XmlElement(name = "TotalInstallments")
	public int getTotalInstallments() {
		return this.totalInstallments;
	}

	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}

	@XmlElement(name = "FinanceType")
	public String getFinanceType() {
		return this.financeType;
	}

	public void setDaysPastDue(int daysPastDue) {
		this.daysPastDue = daysPastDue;
	}

	@XmlElement(name = "DaysPastDue")
	public int getDaysPastDue() {
		return this.daysPastDue;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@XmlElement(name = "CustomerName")
	public String getCustomerName() {
		return this.customerName;
	}

	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	@XmlElement(name = "ProfitRate")
	public BigDecimal getProfitRate() {
		return this.profitRate;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@XmlElement(name = "ProductType")
	public String getProductType() {
		return this.productType;
	}

	public void setMarginRate(BigDecimal marginRate) {
		this.marginRate = marginRate;
	}

	@XmlElement(name = "MarginRate")
	public BigDecimal getMarginRate() {
		return this.marginRate;
	}

	public void setBaseRate(BigDecimal baseRate) {
		this.baseRate = baseRate;
	}

	@XmlElement(name = "BaseRate")
	public BigDecimal getBaseRate() {
		return this.baseRate;
	}

	public void setAllInRate(BigDecimal allInRate) {
		this.allInRate = allInRate;
	}

	@XmlElement(name = "AllInRate")
	public BigDecimal getAllInRate() {
		return this.allInRate;
	}

	public void setMinRate(BigDecimal minRate) {
		this.minRate = minRate;
	}

	@XmlElement(name = "MinRate")
	public BigDecimal getMinRate() {
		return this.minRate;
	}

	public void setBankRatio(BigDecimal bankRatio) {
		this.bankRatio = bankRatio;
	}

	@XmlElement(name = "BankRatio")
	public BigDecimal getBankRatio() {
		return this.bankRatio;
	}

	public void setCustRatio(BigDecimal custRatio) {
		this.custRatio = custRatio;
	}

	@XmlElement(name = "CustRatio")
	public BigDecimal getCustRatio() {
		return this.custRatio;
	}

	public void setBankProfit(BigDecimal bankProfit) {
		this.bankProfit = bankProfit;
	}

	@XmlElement(name = "BankProfit")
	public BigDecimal getBankProfit() {
		return this.bankProfit;
	}

	public void setPrincipalPaid(BigDecimal principalPaid) {
		this.principalPaid = principalPaid;
	}

	@XmlElement(name = "PrincipalPaid")
	public BigDecimal getPrincipalPaid() {
		return this.principalPaid;
	}

	public void setProfitPaid(BigDecimal profitPaid) {
		this.profitPaid = profitPaid;
	}

	@XmlElement(name = "ProfitPaid")
	public BigDecimal getProfitPaid() {
		return this.profitPaid;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	@XmlElement(name = "MaturityDate")
	public Date getMaturityDate() {
		return this.maturityDate;
	}

	public void setRepaymentAccount(String repaymentAccount) {
		this.repaymentAccount = repaymentAccount;
	}

	@XmlElement(name = "RepaymentAccount")
	public String getRepaymentAccount() {
		return this.repaymentAccount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	@XmlElement(name = "ProfitAmount")
	public BigDecimal getProfitAmount() {
		return this.profitAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@XmlElement(name = "TotalAmount")
	public BigDecimal getTotalAmount() {
		return this.totalAmount;
	}

	public void setUnearnedProfit(BigDecimal unearnedProfit) {
		this.unearnedProfit = unearnedProfit;
	}

	@XmlElement(name = "UnearnedProfit")
	public BigDecimal getUnearnedProfit() {
		return this.unearnedProfit;
	}

	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	@XmlElement(name = "OutstandingBalance")
	public BigDecimal getOutstandingBalance() {
		return this.outstandingBalance;
	}

	public void setFinanceStatus(String financeStatus) {
		this.financeStatus = financeStatus;
	}

	@XmlElement(name = "FinanceStatus")
	public String getFinanceStatus() {
		return this.financeStatus;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}
}
