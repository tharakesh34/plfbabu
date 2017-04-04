package com.pennanttech.bajaj.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonPropertyOrder({"REJ_MATCHED_ID__c","REJ_MATCHED_PRCNTG__c","REJ_RESASON__c","FRAUD_MATCHED_ID__c","FRAUD_MATCHED_PRCNTG__c","Dedupe_Source__c","Source_Or_Target__c",
	"Loan_Application__c","Lead__c","Lead_Applicants__c","TERR_MATCHED_ID__c","Customer_Status__c","LAN__c","Product__c","Loan_Status__c","FDD__c","Current_Bucket1__c",
	"Balance_Amount__c","EMI_Amount__c","Bank_Account_No__c","DPD_String__c","Tenure__c","Month_On_Book__c","Loan_Amount__c",
	"custDGDetails","custAddressDetails","custEmailDetails","custContactDetails","custLoanDetails","ReportDetails"})
@JsonSerialize
public class DemographicDetail {
	@XmlElement(name="REJ_MATCHED_ID__c")
	private String rejectionMatchid;
	@XmlElement(name="REJ_MATCHED_PRCNTG__c")
	private String rejectionPercentage;
	@XmlElement(name="REJ_RESASON__c")
	private String rejectReasion;
	@XmlElement(name="FRAUD_MATCHED_ID__c")
	private String fraudmatchId;
	@XmlElement(name="FRAUD_MATCHED_PRCNTG__c")
	private String fraudPercentage;
	@XmlElement(name="Dedupe_Source__c")
	private String dedupeSource;
	@XmlElement(name="Source_Or_Target__c")
	private String sourceOrTarget;
	@XmlElement(name="Loan_Application__c")
	private String applicationNo;
	@XmlElement(name="Lead__c")
	private String lead;
	@XmlElement(name="Lead_Applicants__c")
	private String leadApplicants;
	@XmlElement(name="TERR_MATCHED_ID__c")
	private String terrMatchedId;
	@XmlElement(name="Customer_Status__c")
	private String customerStatus;
	@XmlElement(name="LAN__c")
	private String customerLan;
	@XmlElement(name="Product__c")
	private String loanProduct;
	@XmlElement(name="Loan_Status__c")
	private String loanStatus;
	@XmlElement(name="FDD__c")
	private String firstDueDate;
	@XmlElement(name="Current_Bucket1__c")
	private String currentBucket;
	@XmlElement(name="Balance_Amount__c")
	private String loanBalanceAmount;
	@XmlElement(name="EMI_Amount__c")
	private String EMIAmount;
	@XmlElement(name="Bank_Account_No__c")
	private String repayBankAccount;
	@XmlElement(name="DPD_String__c")
	private String loanDPDString;
	@XmlElement(name="Tenure__c")
	private String tenure;
	@XmlElement(name="Month_On_Book__c")
	private String disbursementDate;
	@XmlElement(name="Loan_Amount__c")
	private String loanAmount;
	
	@XmlElement(name="custDGDetails")
	private List<CustDGDetail> custDGDetails;
	
	@XmlElement(name="custAddressDetails")
	List<CustAddressDetail> custAddressDetails;
	
	@XmlElement(name="custEmailDetails")
	List<CustEmailDetail> custEmailDetails;
	
	@XmlElement(name="custContactDetails")
	List<CustContactDetail> custContactDetails;
	
	@XmlElement(name="custLoanDetails")
	List<CustLoanDetail> custLoanDetails;
	
	@XmlElement(name="ReportDetails")
	ReportDetail reportDetails;
	
	public String getRejectionMatchid() {
		return rejectionMatchid;
	}
	public void setRejectionMatchid(String rejectionMatchid) {
		this.rejectionMatchid = rejectionMatchid;
	}
	public String getRejectionPercentage() {
		return rejectionPercentage;
	}
	public void setRejectionPercentage(String rejectionPercentage) {
		this.rejectionPercentage = rejectionPercentage;
	}
	public String getRejectReasion() {
		return rejectReasion;
	}
	public void setRejectReasion(String rejectReasion) {
		this.rejectReasion = rejectReasion;
	}
	public String getFraudmatchId() {
		return fraudmatchId;
	}
	public void setFraudmatchId(String fraudmatchId) {
		this.fraudmatchId = fraudmatchId;
	}
	public String getFraudPercentage() {
		return fraudPercentage;
	}
	public void setFraudPercentage(String fraudPercentage) {
		this.fraudPercentage = fraudPercentage;
	}
	public String getDedupeSource() {
		return dedupeSource;
	}
	public void setDedupeSource(String dedupeSource) {
		this.dedupeSource = dedupeSource;
	}
	public String getSourceOrTarget() {
		return sourceOrTarget;
	}
	public void setSourceOrTarget(String sourceOrTarget) {
		this.sourceOrTarget = sourceOrTarget;
	}
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}
	public String getLead() {
		return lead;
	}
	public void setLead(String lead) {
		this.lead = lead;
	}
	public String getLeadApplicants() {
		return leadApplicants;
	}
	public void setLeadApplicants(String leadApplicants) {
		this.leadApplicants = leadApplicants;
	}
	public String getTerrMatchedId() {
		return terrMatchedId;
	}
	public void setTerrMatchedId(String terrMatchedId) {
		this.terrMatchedId = terrMatchedId;
	}
	public String getCustomerStatus() {
		return customerStatus;
	}
	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
	public String getCustomerLan() {
		return customerLan;
	}
	public void setCustomerLan(String customerLan) {
		this.customerLan = customerLan;
	}
	public String getLoanProduct() {
		return loanProduct;
	}
	public void setLoanProduct(String loanProduct) {
		this.loanProduct = loanProduct;
	}
	public String getLoanStatus() {
		return loanStatus;
	}
	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
	public String getFirstDueDate() {
		return firstDueDate;
	}
	public void setFirstDueDate(String firstDueDate) {
		this.firstDueDate = firstDueDate;
	}
	public String getCurrentBucket() {
		return currentBucket;
	}
	public void setCurrentBucket(String currentBucket) {
		this.currentBucket = currentBucket;
	}
	public String getLoanBalanceAmount() {
		return loanBalanceAmount;
	}
	public void setLoanBalanceAmount(String loanBalanceAmount) {
		this.loanBalanceAmount = loanBalanceAmount;
	}
	public String getEMIAmount() {
		return EMIAmount;
	}
	public void setEMIAmount(String eMIAmount) {
		EMIAmount = eMIAmount;
	}
	public String getRepayBankAccount() {
		return repayBankAccount;
	}
	public void setRepayBankAccount(String repayBankAccount) {
		this.repayBankAccount = repayBankAccount;
	}
	public String getLoanDPDString() {
		return loanDPDString;
	}
	public void setLoanDPDString(String loanDPDString) {
		this.loanDPDString = loanDPDString;
	}
	public String getTenure() {
		return tenure;
	}
	public void setTenure(String tenure) {
		this.tenure = tenure;
	}
	public String getDisbursementDate() {
		return disbursementDate;
	}
	public void setDisbursementDate(String disbursementDate) {
		this.disbursementDate = disbursementDate;
	}
	public String getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}
	public List<CustDGDetail> getCustDGDetails() {
		return custDGDetails;
	}
	public void setCustDGDetails(List<CustDGDetail> custDGDetails) {
		this.custDGDetails = custDGDetails;
	}
	public List<CustAddressDetail> getCustAddressDetails() {
		return custAddressDetails;
	}
	public void setCustAddressDetails(List<CustAddressDetail> custAddressDetails) {
		this.custAddressDetails = custAddressDetails;
	}
	public List<CustEmailDetail> getCustEmailDetails() {
		return custEmailDetails;
	}
	public void setCustEmailDetails(List<CustEmailDetail> custEmailDetails) {
		this.custEmailDetails = custEmailDetails;
	}
	public List<CustContactDetail> getCustContactDetails() {
		return custContactDetails;
	}
	public void setCustContactDetails(List<CustContactDetail> custContactDetails) {
		this.custContactDetails = custContactDetails;
	}
	public List<CustLoanDetail> getCustLoanDetails() {
		return custLoanDetails;
	}
	public void setCustLoanDetails(List<CustLoanDetail> custLoanDetails) {
		this.custLoanDetails = custLoanDetails;
	}
	public ReportDetail getReportDetails() {
		return reportDetails;
	}
	public void setReportDetails(ReportDetail reportDetails) {
		this.reportDetails = reportDetails;
	}
	
	
}