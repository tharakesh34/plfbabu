package com.pennanttech.bajaj.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonPropertyOrder({"MATCHED_ID","MATCHED_PERCNTG","REJ_MATCHED_ID__c","REJ_MATCHED_PRCNTG__c","REJ_RESASON__c","FRAUD_MATCHED_ID__c","FRAUD_MATCHED_PRCNTG__c","Dedupe_Source__c","Source_Or_Target__c",
	"Loan_Application__c","Lead__c","Lead_Applicants__c","TERR_MATCHED_ID__c","Customer_Status__c","LAN__c","Product__c","Loan_Status__c","FDD__c","Current_Bucket1__c",
	"Balance_Amount__c","EMI_Amount__c","Bank_Account_No__c","DPD_String__c","Tenure__c","Month_On_Book__c","Loan_Amount__c",
	"custDGDetails","custAddressDetails","custEmailDetails","custContactDetails","custLoanDetails","ReportDetails"})
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemographicDetail {
	@JsonProperty("MATCHED_ID")
	private String matchedId;
	@JsonProperty("MATCHED_PERCNTG")
	private String matchedPercntg;
	@JsonProperty("REJ_MATCHED_ID__c")
	private String rejectionMatchid;
	@JsonProperty("REJ_MATCHED_PRCNTG__c")
	private String rejectionPercentage;
	@JsonProperty("REJ_RESASON__c")
	private String rejectReasion;
	@JsonProperty("FRAUD_MATCHED_ID__c")
	private String fraudmatchId;
	@JsonProperty("FRAUD_MATCHED_PRCNTG__c")
	private String fraudPercentage;
	@JsonProperty("Dedupe_Source__c")
	private String dedupeSource;
	@JsonProperty("Source_Or_Target__c")
	private String sourceOrTarget;
	@JsonProperty("Loan_Application__c")
	private String applicationNo;
	@JsonProperty("Lead__c")
	private String lead;
	@JsonProperty("Lead_Applicants__c")
	private String leadApplicants;
	@JsonProperty("TERR_MATCHED_ID__c")
	private String terrMatchedId;
	@JsonProperty("Customer_Status__c")
	private String customerStatus;
	@JsonProperty("LAN__c")
	private String customerLan;
	@JsonProperty("Product__c")
	private String loanProduct;
	@JsonProperty("Loan_Status__c")
	private String loanStatus;
	@JsonProperty("FDD__c")
	private String firstDueDate;
	@JsonProperty("Current_Bucket1__c")
	private String currentBucket;
	@JsonProperty("Balance_Amount__c")
	private String loanBalanceAmount;
	@JsonProperty("EMI_Amount__c")
	private String EMIAmount;
	@JsonProperty("Bank_Account_No__c")
	private String repayBankAccount;
	@JsonProperty("DPD_String__c")
	private String loanDPDString;
	@JsonProperty("Tenure__c")
	private String tenure;
	@JsonProperty("Month_On_Book__c")
	private String disbursementDate;
	@JsonProperty("Loan_Amount__c")
	private String loanAmount;
	
	@JsonProperty("custDGDetails")
	private List<CustDGDetail> custDGDetails;
	
	@JsonProperty("custAddressDetails")
	List<CustAddressDetail> custAddressDetails;
	
	@JsonProperty("custEmailDetails")
	List<CustEmailDetail> custEmailDetails;
	
	@JsonProperty("custContactDetails")
	List<CustContactDetail> custContactDetails;
	
	@JsonProperty("custLoanDetails")
	List<CustLoanDetail> custLoanDetails;
	
	@JsonProperty("reportDetails")
	ReportDetail reportDetails;
	
	
	
	public String getMatchedPercntg() {
		return matchedPercntg;
	}
	public void setMatchedPercntg(String matchedPercntg) {
		this.matchedPercntg = matchedPercntg;
	}
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
	@Override
	public String toString() {
		return "DemographicDetail [rejectionMatchid=" + rejectionMatchid
				+ ", rejectionPercentage=" + rejectionPercentage
				+ ", rejectReasion=" + rejectReasion + ", fraudmatchId="
				+ fraudmatchId + ", fraudPercentage=" + fraudPercentage
				+ ", dedupeSource=" + dedupeSource + ", sourceOrTarget="
				+ sourceOrTarget + ", applicationNo=" + applicationNo
				+ ", lead=" + lead + ", leadApplicants=" + leadApplicants
				+ ", terrMatchedId=" + terrMatchedId + ", customerStatus="
				+ customerStatus + ", customerLan=" + customerLan
				+ ", loanProduct=" + loanProduct + ", loanStatus=" + loanStatus
				+ ", firstDueDate=" + firstDueDate + ", currentBucket="
				+ currentBucket + ", loanBalanceAmount=" + loanBalanceAmount
				+ ", EMIAmount=" + EMIAmount + ", repayBankAccount="
				+ repayBankAccount + ", loanDPDString=" + loanDPDString
				+ ", tenure=" + tenure + ", disbursementDate="
				+ disbursementDate + ", loanAmount=" + loanAmount
				+ ", custDGDetails=" + custDGDetails + ", custAddressDetails="
				+ custAddressDetails + ", custEmailDetails=" + custEmailDetails
				+ ", custContactDetails=" + custContactDetails
				+ ", custLoanDetails=" + custLoanDetails + ", reportDetails="
				+ reportDetails + "]";
	}
	
	
}