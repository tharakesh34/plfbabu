package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FetchFinanceDetailsResponse")
public class FetchFinanceDetailsResponse {

	private String referenceNum;
	private String returnCode;
	private String returnText;
	private String customerNo;
	private Timestamp timestamp;
	private String financeRef;
	private String financeType;
	private String branch;
	private String currency;
	private Date startDate;
	private BigDecimal installmentAmount;
	private BigDecimal outstandingAmount;
	private BigDecimal financeAmount;
	private Date dueDate;
	private int remainingInstallments;
	private int totalInstallments;
	private int daysPastDue;
	private String customerName;
	private Date contractDate;
	private String financeStatus;
	private String modelName;
	private BigDecimal disbursedAmount;
	private BigDecimal downPaymentAmount;
	private BigDecimal repaidAmount;
	private Date lastInstallmentDate;
	private BigDecimal lastInstallmentAmount;
	private int financeTenor;
	private List<Guarantor> guarantor;
	private List<JointBorrower> jointBorrower;
	private String repaymentFrequency;
	private BigDecimal profitRate;
	private BigDecimal pastDueAmount;
	private String repaymentAccount;
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
	private int ccyEditField;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return this.referenceNum;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return this.returnCode;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return this.returnText;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@XmlElement(name = "CustomerNo")
	public String getCustomerNo() {
		return this.customerNo;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@XmlElement(name = "Timestamp")
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setFinanceRef(String financeRef) {
		this.financeRef = financeRef;
	}

	@XmlElement(name = "FinanceRef")
	public String getFinanceRef() {
		return this.financeRef;
	}

	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}

	@XmlElement(name = "FinanceType")
	public String getFinanceType() {
		return this.financeType;
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

	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
	}

	@XmlElement(name = "ContractDate")
	public Date getContractDate() {
		return this.contractDate;
	}

	public void setFinanceStatus(String financeStatus) {
		this.financeStatus = financeStatus;
	}

	@XmlElement(name = "FinanceStatus")
	public String getFinanceStatus() {
		return this.financeStatus;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@XmlElement(name = "ModelName")
	public String getModelName() {
		return this.modelName;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	@XmlElement(name = "DisbursedAmount")
	public BigDecimal getDisbursedAmount() {
		return this.disbursedAmount;
	}

	public void setDownPaymentAmount(BigDecimal downPaymentAmount) {
		this.downPaymentAmount = downPaymentAmount;
	}

	@XmlElement(name = "DownPaymentAmount")
	public BigDecimal getDownPaymentAmount() {
		return this.downPaymentAmount;
	}

	public void setRepaidAmount(BigDecimal repaidAmount) {
		this.repaidAmount = repaidAmount;
	}

	@XmlElement(name = "RepaidAmount")
	public BigDecimal getRepaidAmount() {
		return this.repaidAmount;
	}

	public void setLastInstallmentDate(Date lastInstallmentDate) {
		this.lastInstallmentDate = lastInstallmentDate;
	}

	@XmlElement(name = "LastInstallmentDate")
	public Date getLastInstallmentDate() {
		return this.lastInstallmentDate;
	}

	public void setLastInstallmentAmount(BigDecimal lastInstallmentAmount) {
		this.lastInstallmentAmount = lastInstallmentAmount;
	}

	@XmlElement(name = "LastInstallmentAmount")
	public BigDecimal getLastInstallmentAmount() {
		return this.lastInstallmentAmount;
	}

	public void setFinanceTenor(int financeTenor) {
		this.financeTenor = financeTenor;
	}

	@XmlElement(name = "FinanceTenor")
	public int getFinanceTenor() {
		return this.financeTenor;
	}

	public void setGuarantor(List<Guarantor> guarantor) {
		this.guarantor = guarantor;
	}

	@XmlElement(name = "Guarantor")
	public List<Guarantor> getGuarantor() {
		return this.guarantor;
	}

	public void setJointBorrower(List<JointBorrower> jointBorrower) {
		this.jointBorrower = jointBorrower;
	}

	@XmlElement(name = "JointBorrower")
	public List<JointBorrower> getJointBorrower() {
		return this.jointBorrower;
	}

	public void setRepaymentFrequency(String repaymentFrequency) {
		this.repaymentFrequency = repaymentFrequency;
	}

	@XmlElement(name = "RepaymentFrequency")
	public String getRepaymentFrequency() {
		return this.repaymentFrequency;
	}

	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	@XmlElement(name = "ProfitRate")
	public BigDecimal getProfitRate() {
		return this.profitRate;
	}

	public void setPastDueAmount(BigDecimal pastDueAmount) {
		this.pastDueAmount = pastDueAmount;
	}

	@XmlElement(name = "PastDueAmount")
	public BigDecimal getPastDueAmount() {
		return this.pastDueAmount;
	}

	public void setRepaymentAccount(String repaymentAccount) {
		this.repaymentAccount = repaymentAccount;
	}

	@XmlElement(name = "RepaymentAccount")
	public String getRepaymentAccount() {
		return this.repaymentAccount;
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

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}
}
