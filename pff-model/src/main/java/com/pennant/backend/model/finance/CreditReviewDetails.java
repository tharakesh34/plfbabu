package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerExtLiability;

public class CreditReviewDetails {

	private long id;
	private String finReference;
	private String businessVertical;
	private String product;
	private String finCategory;
	private String employmentType;
	private String eligibilityMethod;
	private String section;
	private String templateName;
	private int templateVersion;
	private String fields;
	private String protectedCells;
	private String surrogateType;
	private BigDecimal roi = BigDecimal.ZERO;
	private BigDecimal totalLoanAmount = BigDecimal.ZERO;
	private int tenor = 0;
	private String segment = "";
	private BigDecimal totalObligations = BigDecimal.ZERO;
	private BigDecimal totalSalary = BigDecimal.ZERO;
	private int CustAge = 0;
	private String natureOfBusiness = "";
	private List<CustomerExtLiability> obligationsList = new ArrayList<>();
	private BigDecimal multiplier1 = BigDecimal.ZERO;
	private BigDecimal multiplier2 = BigDecimal.ZERO;
	private BigDecimal basicOfferAmount1 = BigDecimal.ZERO;
	private BigDecimal basicOfferAmount2 = BigDecimal.ZERO;
	private String bankCategory1;
	private String bankCategory2;
	private BigDecimal agreementValOfProperty = BigDecimal.ZERO;
	private BigDecimal avgBankBal = BigDecimal.ZERO;
	private String desc;
	private BigDecimal maxEligibleTenor = BigDecimal.ZERO;
	private Date custDOB;
	private String custName;
	private List<JointAccountDetail> jointAccountDetails = new ArrayList<>();
	private List<JointAccountDetail> extLiabilitiesjointAccDetails = new ArrayList<>();
	private List<CustomerExtLiability> appCoAppObligations = new ArrayList<>();
	private String typeOfDegree;
	private String finBranchDesc = "";
	private BigDecimal sanctionedAmt = BigDecimal.ZERO;
	private BigDecimal outStandingLoanAmt = BigDecimal.ZERO;
	private BigDecimal accountLimit = BigDecimal.ZERO;
	private BigDecimal loanAmount = BigDecimal.ZERO;
	private BigDecimal chequeBncOthEmi = BigDecimal.ZERO;
	private BigDecimal maxEmi = BigDecimal.ZERO;
	private BigDecimal debitEqutiRatio = BigDecimal.ZERO;
	private BigDecimal dsrcValue = BigDecimal.ZERO;
	private BigDecimal grossRecipt = BigDecimal.ZERO;
	private String fieldKeys;
	private String formulaCells;

	/**
	 * Total ABB / EMI (of applied loan)
	 * 
	 */
	private BigDecimal totalAbb = BigDecimal.ZERO;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(String businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getEligibilityMethod() {
		return eligibilityMethod;
	}

	public void setEligibilityMethod(String eligibilityMethod) {
		this.eligibilityMethod = eligibilityMethod;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(int templateVersion) {
		this.templateVersion = templateVersion;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getProtectedCells() {
		return protectedCells;
	}

	public void setProtectedCells(String protectedCells) {
		this.protectedCells = protectedCells;
	}

	public String getSurrogateType() {
		return surrogateType;
	}

	public void setSurrogateType(String surrogateType) {
		this.surrogateType = surrogateType;
	}

	public BigDecimal getRoi() {
		return roi;
	}

	public void setRoi(BigDecimal roi) {
		this.roi = roi;
	}

	public BigDecimal getTotalLoanAmount() {
		return totalLoanAmount;
	}

	public void setTotalLoanAmount(BigDecimal totalLoanAmount) {
		this.totalLoanAmount = totalLoanAmount;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public BigDecimal getTotalObligations() {
		return totalObligations;
	}

	public void setTotalObligations(BigDecimal totalObligations) {
		this.totalObligations = totalObligations;
	}

	public BigDecimal getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(BigDecimal totalSalary) {
		this.totalSalary = totalSalary;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getCustAge() {
		return CustAge;
	}

	public void setCustAge(int custAge) {
		CustAge = custAge;
	}

	public String getNatureOfBusiness() {
		return natureOfBusiness;
	}

	public void setNatureOfBusiness(String natureOfBusiness) {
		this.natureOfBusiness = natureOfBusiness;
	}

	public List<CustomerExtLiability> getObligationsList() {
		return obligationsList;
	}

	public void setObligationsList(List<CustomerExtLiability> obligationsList) {
		this.obligationsList = obligationsList;
	}

	public BigDecimal getMultiplier1() {
		return multiplier1;
	}

	public void setMultiplier1(BigDecimal multiplier1) {
		this.multiplier1 = multiplier1;
	}

	public BigDecimal getMultiplier2() {
		return multiplier2;
	}

	public void setMultiplier2(BigDecimal multiplier2) {
		this.multiplier2 = multiplier2;
	}

	public BigDecimal getBasicOfferAmount1() {
		return basicOfferAmount1;
	}

	public void setBasicOfferAmount1(BigDecimal basicOfferAmount1) {
		this.basicOfferAmount1 = basicOfferAmount1;
	}

	public BigDecimal getBasicOfferAmount2() {
		return basicOfferAmount2;
	}

	public void setBasicOfferAmount2(BigDecimal basicOfferAmount2) {
		this.basicOfferAmount2 = basicOfferAmount2;
	}

	public String getBankCategory1() {
		return bankCategory1;
	}

	public void setBankCategory1(String bankCategory1) {
		this.bankCategory1 = bankCategory1;
	}

	public String getBankCategory2() {
		return bankCategory2;
	}

	public void setBankCategory2(String bankCategory2) {
		this.bankCategory2 = bankCategory2;
	}

	public BigDecimal getAgreementValOfProperty() {
		return agreementValOfProperty;
	}

	public void setAgreementValOfProperty(BigDecimal agreementValOfProperty) {
		this.agreementValOfProperty = agreementValOfProperty;
	}

	public BigDecimal getAvgBankBal() {
		return avgBankBal;
	}

	public void setAvgBankBal(BigDecimal avgBankBal) {
		this.avgBankBal = avgBankBal;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public BigDecimal getMaxEligibleTenor() {
		return maxEligibleTenor;
	}

	public void setMaxEligibleTenor(BigDecimal maxEligibleTenor) {
		this.maxEligibleTenor = maxEligibleTenor;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public List<JointAccountDetail> getJointAccountDetails() {
		return jointAccountDetails;
	}

	public void setJointAccountDetails(List<JointAccountDetail> jointAccountDetails) {
		this.jointAccountDetails = jointAccountDetails;
	}

	public List<JointAccountDetail> getExtLiabilitiesjointAccDetails() {
		return extLiabilitiesjointAccDetails;
	}

	public void setExtLiabilitiesjointAccDetails(List<JointAccountDetail> extLiabilitiesjointAccDetails) {
		this.extLiabilitiesjointAccDetails = extLiabilitiesjointAccDetails;
	}

	public List<CustomerExtLiability> getAppCoAppObligations() {
		return appCoAppObligations;
	}

	public void setAppCoAppObligations(List<CustomerExtLiability> appCoAppObligations) {
		this.appCoAppObligations = appCoAppObligations;
	}

	public String getTypeOfDegree() {
		return typeOfDegree;
	}

	public void setTypeOfDegree(String typeOfDegree) {
		this.typeOfDegree = typeOfDegree;
	}

	public String getFinBranchDesc() {
		return finBranchDesc;
	}

	public void setFinBranchDesc(String finBranchDesc) {
		this.finBranchDesc = finBranchDesc;
	}

	public BigDecimal getSanctionedAmt() {
		return sanctionedAmt;
	}

	public void setSanctionedAmt(BigDecimal sanctionedAmt) {
		this.sanctionedAmt = sanctionedAmt;
	}

	public BigDecimal getOutStandingLoanAmt() {
		return outStandingLoanAmt;
	}

	public void setOutStandingLoanAmt(BigDecimal outStandingLoanAmt) {
		this.outStandingLoanAmt = outStandingLoanAmt;
	}

	public BigDecimal getAccountLimit() {
		return accountLimit;
	}

	public void setAccountLimit(BigDecimal accountLimit) {
		this.accountLimit = accountLimit;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmout(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public BigDecimal getChequeBncOthEmi() {
		return chequeBncOthEmi;
	}

	public void setChequeBncOthEmi(BigDecimal chequeBncOthEmi) {
		this.chequeBncOthEmi = chequeBncOthEmi;
	}

	public BigDecimal getMaxEmi() {
		return maxEmi;
	}

	public void setMaxEmi(BigDecimal maxEmi) {
		this.maxEmi = maxEmi;
	}

	public BigDecimal getDebitEqutiRatio() {
		return debitEqutiRatio;
	}

	public void setDebitEqutiRatio(BigDecimal debitEqutiRatio) {
		this.debitEqutiRatio = debitEqutiRatio;
	}

	public BigDecimal getDsrcValue() {
		return dsrcValue;
	}

	public void setDsrcValue(BigDecimal dsrcValue) {
		this.dsrcValue = dsrcValue;
	}

	public BigDecimal getGrossRecipt() {
		return grossRecipt;
	}

	public void setGrossRecipt(BigDecimal grossRecipt) {
		this.grossRecipt = grossRecipt;
	}

	public BigDecimal getTotalAbb() {
		return totalAbb;
	}

	public void setTotalAbb(BigDecimal totalAbb) {
		this.totalAbb = totalAbb;
	}

	public String getFieldKeys() {
		return fieldKeys;
	}

	public void setFieldKeys(String fieldKeys) {
		this.fieldKeys = fieldKeys;
	}

	public String getFormulaCells() {
		return formulaCells;
	}

	public void setFormulaCells(String formulaCells) {
		this.formulaCells = formulaCells;
	}

}
