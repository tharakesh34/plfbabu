package com.pff.vo;

import java.util.ArrayList;
import java.util.List;

public class CustomerInforamtion {



	//Retail CustomerPersonalInformation

	private String customerStatus;
	private String customerStatusISO;
	private String title;
	private String titleISO;
	private String fullName;
	private String shortName;
	private String mnemonic;
	private String motherName;
	private String firstName;
	private String familyName;
	private String secondName;
	private String thirdName;
	private String fourthName;
	private String dateOfBirth;
	private String placeOfBirth;
	private String language;
	private String languageISO;
	private String sector;
	private String sectorISO;
	private String industry;
	private String industryISO;
	private String segment;
	private String segmentISO;
	private String residencyType;
	private String residencyTypeISO;
	private String fatherName;
	private String gender;
	private String genderISO;
	private String nationality;
	private String nationalityISO;
	private String dualNationality;
	private String dualNationalityISO;
	private String countryOfbirth;
	private String countryOfbirthISO;
	private String maritalStatus;
	private String maritalStatusISO;
	private String dependents;
	private String noOfDependents;
	private String yearsInUAE;
	private String relationshipDate;
	private String relationshipManager;
	private String relatedParty;
	private String introducer;
	private String branchCode;
	private String branchCodeISO;
	private String lineManager;

	//Retail EmploymentInformation

	private String  employmentInfo;
	private String  empStatus;
	private String  empStatusISO;
	private String  empName;
	private String  occupation;
	private String  department;
	private String  empStartDate;
	private String  salaryCurrency;
	private String  salary;
	private String  salaryDateFreq;
	private String  businessType;
	private String  nameOfBusiness;

	List<CustomerInfoVo> dependentsList=new ArrayList<>();

	//SME CustDetails

	private String nameOfEstablishment;
	private String establishmentShortName;
	private String typeOfEstablishment;
	private String typeOfEstablishmentISO;
	private String target;
	private String targetISO;
	private String custStatus; 
	private String custStatusISO;
	private String incorpType;
	private String incorpTypeISO;
	private String countryOfIncorp;
	private String countryOfIncorpISO;
	private String dateOfIncorporation;
	private String parentCoCIF;
	private String auditor;
	private String useChequeBook;

	//SME Financial Information
	private String TotalNoOfPartners;
	private String ModeOfOperation;
	private String PowerOfAttorney;
	private String AuditedFinancials;
	private String FaxOfIndemity;
	private String idemityFaxCtryCode;
	private String idemityFaxCtryCodeISO;
	private String idemityFaxAreaCode;
	private String idemityFaxAreaCodeISO;
	private String idemityFaxSubsidiaryNo;
	private String EmailIndemity;
	private String IndemityEmailAddress;
	private String chequeBookRequest;
	private String currencyOfFinancials;
	private String currencyOfFinancialsISO;
	private String TurnOver;
	private String GrossProfit;
	private String NetProfit;
	private String ShareCapital;
	private String noOfEmployees;
	private String natureOfBusiness;
	private String natureOfBusinessISO;
	private String throughputAmount;
	private String throughputFrequency;
	private String throughputAccount;
	public String getEmploymentInfo() {
		return employmentInfo;
	}
	public void setEmploymentInfo(String employmentInfo) {
		this.employmentInfo = employmentInfo;
	}
	public String getEmpStatus() {
		return empStatus;
	}
	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}
	public String getEmpStatusISO() {
		return empStatusISO;
	}
	public void setEmpStatusISO(String empStatusISO) {
		this.empStatusISO = empStatusISO;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getEmpStartDate() {
		return empStartDate;
	}
	public void setEmpStartDate(String empStartDate) {
		this.empStartDate = empStartDate;
	}
	public String getSalaryCurrency() {
		return salaryCurrency;
	}
	public void setSalaryCurrency(String salaryCurrency) {
		this.salaryCurrency = salaryCurrency;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public String getSalaryDateFreq() {
		return salaryDateFreq;
	}
	public void setSalaryDateFreq(String salaryDateFreq) {
		this.salaryDateFreq = salaryDateFreq;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getNameOfBusiness() {
		return nameOfBusiness;
	}
	public void setNameOfBusiness(String nameOfBusiness) {
		this.nameOfBusiness = nameOfBusiness;
	}
	public String getTotalNoOfPartners() {
		return TotalNoOfPartners;
	}
	public void setTotalNoOfPartners(String totalNoOfPartners) {
		TotalNoOfPartners = totalNoOfPartners;
	}
	public String getModeOfOperation() {
		return ModeOfOperation;
	}
	public void setModeOfOperation(String modeOfOperation) {
		ModeOfOperation = modeOfOperation;
	}
	public String getPowerOfAttorney() {
		return PowerOfAttorney;
	}
	public void setPowerOfAttorney(String powerOfAttorney) {
		PowerOfAttorney = powerOfAttorney;
	}
	public String getAuditedFinancials() {
		return AuditedFinancials;
	}
	public void setAuditedFinancials(String auditedFinancials) {
		AuditedFinancials = auditedFinancials;
	}
	public String getFaxOfIndemity() {
		return FaxOfIndemity;
	}
	public void setFaxOfIndemity(String faxOfIndemity) {
		FaxOfIndemity = faxOfIndemity;
	}
	public String getIdemityFaxCtryCode() {
		return idemityFaxCtryCode;
	}
	public void setIdemityFaxCtryCode(String idemityFaxCtryCode) {
		this.idemityFaxCtryCode = idemityFaxCtryCode;
	}
	public String getIdemityFaxCtryCodeISO() {
		return idemityFaxCtryCodeISO;
	}
	public void setIdemityFaxCtryCodeISO(String idemityFaxCtryCodeISO) {
		this.idemityFaxCtryCodeISO = idemityFaxCtryCodeISO;
	}
	public String getIdemityFaxAreaCode() {
		return idemityFaxAreaCode;
	}
	public void setIdemityFaxAreaCode(String idemityFaxAreaCode) {
		this.idemityFaxAreaCode = idemityFaxAreaCode;
	}
	public String getIdemityFaxAreaCodeISO() {
		return idemityFaxAreaCodeISO;
	}
	public void setIdemityFaxAreaCodeISO(String idemityFaxAreaCodeISO) {
		this.idemityFaxAreaCodeISO = idemityFaxAreaCodeISO;
	}
	public String getIdemityFaxSubsidiaryNo() {
		return idemityFaxSubsidiaryNo;
	}
	public void setIdemityFaxSubsidiaryNo(String idemityFaxSubsidiaryNo) {
		this.idemityFaxSubsidiaryNo = idemityFaxSubsidiaryNo;
	}
	public String getEmailIndemity() {
		return EmailIndemity;
	}
	public void setEmailIndemity(String emailIndemity) {
		EmailIndemity = emailIndemity;
	}
	public String getIndemityEmailAddress() {
		return IndemityEmailAddress;
	}
	public void setIndemityEmailAddress(String indemityEmailAddress) {
		IndemityEmailAddress = indemityEmailAddress;
	}
	public String getChequeBookRequest() {
		return chequeBookRequest;
	}
	public void setChequeBookRequest(String chequeBookRequest) {
		this.chequeBookRequest = chequeBookRequest;
	}
	public String getCurrencyOfFinancials() {
		return currencyOfFinancials;
	}
	public void setCurrencyOfFinancials(String currencyOfFinancials) {
		this.currencyOfFinancials = currencyOfFinancials;
	}
	public String getCurrencyOfFinancialsISO() {
		return currencyOfFinancialsISO;
	}
	public void setCurrencyOfFinancialsISO(String currencyOfFinancialsISO) {
		this.currencyOfFinancialsISO = currencyOfFinancialsISO;
	}
	public String getTurnOver() {
		return TurnOver;
	}
	public void setTurnOver(String turnOver) {
		TurnOver = turnOver;
	}
	public String getGrossProfit() {
		return GrossProfit;
	}
	public void setGrossProfit(String grossProfit) {
		GrossProfit = grossProfit;
	}
	public String getNetProfit() {
		return NetProfit;
	}
	public void setNetProfit(String netProfit) {
		NetProfit = netProfit;
	}
	public String getShareCapital() {
		return ShareCapital;
	}
	public void setShareCapital(String shareCapital) {
		ShareCapital = shareCapital;
	}
	public String getNoOfEmployees() {
		return noOfEmployees;
	}
	public void setNoOfEmployees(String noOfEmployees) {
		this.noOfEmployees = noOfEmployees;
	}
	public String getNatureOfBusiness() {
		return natureOfBusiness;
	}
	public void setNatureOfBusiness(String natureOfBusiness) {
		this.natureOfBusiness = natureOfBusiness;
	}
	public String getNatureOfBusinessISO() {
		return natureOfBusinessISO;
	}
	public void setNatureOfBusinessISO(String natureOfBusinessISO) {
		this.natureOfBusinessISO = natureOfBusinessISO;
	}
	public String getThroughputAmount() {
		return throughputAmount;
	}
	public void setThroughputAmount(String throughputAmount) {
		this.throughputAmount = throughputAmount;
	}
	public String getThroughputFrequency() {
		return throughputFrequency;
	}
	public void setThroughputFrequency(String throughputFrequency) {
		this.throughputFrequency = throughputFrequency;
	}
	public String getThroughputAccount() {
		return throughputAccount;
	}
	public void setThroughputAccount(String throughputAccount) {
		this.throughputAccount = throughputAccount;
	}
	public String getCustomerStatus() {
		return customerStatus;
	}
	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
	public String getCustomerStatusISO() {
		return customerStatusISO;
	}
	public void setCustomerStatusISO(String customerStatusISO) {
		this.customerStatusISO = customerStatusISO;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleISO() {
		return titleISO;
	}
	public void setTitleISO(String titleISO) {
		this.titleISO = titleISO;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getMnemonic() {
		return mnemonic;
	}
	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getThirdName() {
		return thirdName;
	}
	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}
	public String getFourthName() {
		return fourthName;
	}
	public void setFourthName(String fourthName) {
		this.fourthName = fourthName;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLanguageISO() {
		return languageISO;
	}
	public void setLanguageISO(String languageISO) {
		this.languageISO = languageISO;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getSectorISO() {
		return sectorISO;
	}
	public void setSectorISO(String sectorISO) {
		this.sectorISO = sectorISO;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getIndustryISO() {
		return industryISO;
	}
	public void setIndustryISO(String industryISO) {
		this.industryISO = industryISO;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getSegmentISO() {
		return segmentISO;
	}
	public void setSegmentISO(String segmentISO) {
		this.segmentISO = segmentISO;
	}
	public String getResidencyType() {
		return residencyType;
	}
	public void setResidencyType(String residencyType) {
		this.residencyType = residencyType;
	}
	public String getResidencyTypeISO() {
		return residencyTypeISO;
	}
	public void setResidencyTypeISO(String residencyTypeISO) {
		this.residencyTypeISO = residencyTypeISO;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getGenderISO() {
		return genderISO;
	}
	public void setGenderISO(String genderISO) {
		this.genderISO = genderISO;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getNationalityISO() {
		return nationalityISO;
	}
	public void setNationalityISO(String nationalityISO) {
		this.nationalityISO = nationalityISO;
	}
	public String getDualNationality() {
		return dualNationality;
	}
	public void setDualNationality(String dualNationality) {
		this.dualNationality = dualNationality;
	}
	public String getDualNationalityISO() {
		return dualNationalityISO;
	}
	public void setDualNationalityISO(String dualNationalityISO) {
		this.dualNationalityISO = dualNationalityISO;
	}
	public String getCountryOfbirth() {
		return countryOfbirth;
	}
	public void setCountryOfbirth(String countryOfbirth) {
		this.countryOfbirth = countryOfbirth;
	}
	public String getCountryOfbirthISO() {
		return countryOfbirthISO;
	}
	public void setCountryOfbirthISO(String countryOfbirthISO) {
		this.countryOfbirthISO = countryOfbirthISO;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getMaritalStatusISO() {
		return maritalStatusISO;
	}
	public void setMaritalStatusISO(String maritalStatusISO) {
		this.maritalStatusISO = maritalStatusISO;
	}
	public String getDependents() {
		return dependents;
	}
	public void setDependents(String dependents) {
		this.dependents = dependents;
	}
	public String getNoOfDependents() {
		return noOfDependents;
	}
	public void setNoOfDependents(String noOfDependents) {
		this.noOfDependents = noOfDependents;
	}
	public String getYearsInUAE() {
		return yearsInUAE;
	}
	public void setYearsInUAE(String yearsInUAE) {
		this.yearsInUAE = yearsInUAE;
	}
	public String getRelationshipDate() {
		return relationshipDate;
	}
	public void setRelationshipDate(String relationshipDate) {
		this.relationshipDate = relationshipDate;
	}
	public String getRelationshipManager() {
		return relationshipManager;
	}
	public void setRelationshipManager(String relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
	public String getRelatedParty() {
		return relatedParty;
	}
	public void setRelatedParty(String relatedParty) {
		this.relatedParty = relatedParty;
	}
	public String getIntroducer() {
		return introducer;
	}
	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchCodeISO() {
		return branchCodeISO;
	}
	public void setBranchCodeISO(String branchCodeISO) {
		this.branchCodeISO = branchCodeISO;
	}
	public String getLineManager() {
		return lineManager;
	}
	public void setLineManager(String lineManager) {
		this.lineManager = lineManager;
	}
	public List<CustomerInfoVo> getDependentsList() {
		return dependentsList;
	}
	public void setDependentsList(List<CustomerInfoVo> dependentsList) {
		this.dependentsList = dependentsList;
	}
	public String getNameOfEstablishment() {
		return nameOfEstablishment;
	}
	public void setNameOfEstablishment(String nameOfEstablishment) {
		this.nameOfEstablishment = nameOfEstablishment;
	}
	public String getEstablishmentShortName() {
		return establishmentShortName;
	}
	public void setEstablishmentShortName(String establishmentShortName) {
		this.establishmentShortName = establishmentShortName;
	}
	public String getTypeOfEstablishment() {
		return typeOfEstablishment;
	}
	public void setTypeOfEstablishment(String typeOfEstablishment) {
		this.typeOfEstablishment = typeOfEstablishment;
	}
	public String getTypeOfEstablishmentISO() {
		return typeOfEstablishmentISO;
	}
	public void setTypeOfEstablishmentISO(String typeOfEstablishmentISO) {
		this.typeOfEstablishmentISO = typeOfEstablishmentISO;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getTargetISO() {
		return targetISO;
	}
	public void setTargetISO(String targetISO) {
		this.targetISO = targetISO;
	}
	public String getCustStatus() {
		return custStatus;
	}
	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}
	public String getCustStatusISO() {
		return custStatusISO;
	}
	public void setCustStatusISO(String custStatusISO) {
		this.custStatusISO = custStatusISO;
	}
	public String getIncorpType() {
		return incorpType;
	}
	public void setIncorpType(String incorpType) {
		this.incorpType = incorpType;
	}
	public String getIncorpTypeISO() {
		return incorpTypeISO;
	}
	public void setIncorpTypeISO(String incorpTypeISO) {
		this.incorpTypeISO = incorpTypeISO;
	}
	public String getCountryOfIncorp() {
		return countryOfIncorp;
	}
	public void setCountryOfIncorp(String countryOfIncorp) {
		this.countryOfIncorp = countryOfIncorp;
	}
	public String getCountryOfIncorpISO() {
		return countryOfIncorpISO;
	}
	public void setCountryOfIncorpISO(String countryOfIncorpISO) {
		this.countryOfIncorpISO = countryOfIncorpISO;
	}
	public String getDateOfIncorporation() {
		return dateOfIncorporation;
	}
	public void setDateOfIncorporation(String dateOfIncorporation) {
		this.dateOfIncorporation = dateOfIncorporation;
	}
	public String getParentCoCIF() {
		return parentCoCIF;
	}
	public void setParentCoCIF(String parentCoCIF) {
		this.parentCoCIF = parentCoCIF;
	}
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	public String getUseChequeBook() {
		return useChequeBook;
	}
	public void setUseChequeBook(String useChequeBook) {
		this.useChequeBook = useChequeBook;
	}



}
