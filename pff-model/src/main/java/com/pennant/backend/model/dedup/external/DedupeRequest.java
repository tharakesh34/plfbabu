package com.pennant.backend.model.dedup.external;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonPropertyOrder({ "DEAL_ID", "ORG", "REQUEST_TYPE", "DATASOURCE", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME",
		"ADDRESS_1", "ADDRESS_2", "ADDRESS_3", "AREA", "LANDMARK", "CITY", "PIN", "DOB", "LANDLINE_1", "LANDLINE_2",
		"MOBILE", "STD", "PAN", "EMAIL", "ACCOUNT_NUMBER", "VOTER_ID", "CREDIT_CARD_NUMBER", "CUSTOMER_NO", "LAN_NO",
		"LAN_2", "CUSTOMER_TYPE", "TAN_NO", "CUST_SR_NO", "APPLN_NO", "FATHER_NAME", "EMPOYER_NAME",
		"DRIVING_LICENSE_NUMBER", "PASSPORT_NO", "ADDRESS1_OFFICE", "ADDRESS2_OFFICE", "ADDRESS3_OFFICE", "AREA_OFFICE",
		"CITY_OFFICE", "PIN_OFFICE", "LANDLINE1_OFFICE", "LANDLINE2_OFFICE", "STD_OFFICE", "PRODUCT", "BATCH",
		"STAYING_SINCE", "CITY_CLASSIFICATION", "EMPLOYMENT_BUSINESS", "AGE", "RESIDENT_TYPE", "CREDIT_PROGRAM",
		"ASSET_CATEGORY", "REQUEST_ID_FIN", "MATCH_PROFILE", "SEGMENT", "APPLICANT_TYPE", "DATE_OF_INCORPORATION",
		"LANDMARK_OFFICE", "MOBILE_OFFICE", "EMAIL_OFFICE", "LOAN_APP_NO", "CustomerStatusYN", "DemoDtlYN",
		"AppscoreYN" })
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class DedupeRequest {
	@XmlElement(name = "DEAL_ID")
	private String dealId;
	@XmlElement(name = "ORG")
	private String org;
	@XmlElement(name = "REQUEST_TYPE")
	private String requestType;
	@XmlElement(name = "DATASOURCE")
	private String dataSource;
	@XmlElement(name = "FIRST_NAME")
	private String firstName;
	@XmlElement(name = "MIDDLE_NAME")
	private String middleName;
	@XmlElement(name = "LAST_NAME")
	private String lastName;
	@XmlElement(name = "ADDRESS_1")
	private String address1;
	@XmlElement(name = "ADDRESS_2")
	private String address2;
	@XmlElement(name = "ADDRESS_3")
	private String address3;
	@XmlElement(name = "AREA")
	private String area;
	@XmlElement(name = "LANDMARK")
	private String landMark;
	@XmlElement(name = "CITY")
	private String city;
	@XmlElement(name = "PIN")
	private String pinCode;
	@XmlElement(name = "DOB")
	private String dateOfBirth;
	@XmlElement(name = "LANDLINE_1")
	private String landLine1;
	@XmlElement(name = "LANDLINE_2")
	private String landLine2;
	@XmlElement(name = "MOBILE")
	private String mobile;
	@XmlElement(name = "STD")
	private String stdCode;
	@XmlElement(name = "PAN")
	private String panNumber;
	@XmlElement(name = "EMAIL")
	private String email;
	@XmlElement(name = "ACCOUNT_NUMBER")
	private String accountNumber;
	@XmlElement(name = "VOTER_ID")
	private String voterId;
	@XmlElement(name = "CREDIT_CARD_NUMBER")
	private String creditCardNumber;
	@XmlElement(name = "CUSTOMER_NO")
	private Integer customerNumber;
	@XmlElement(name = "LAN_NO")
	private String lanNo;
	@XmlElement(name = "LAN_2")
	private String lan2;
	@XmlElement(name = "CUSTOMER_TYPE")
	private String customerType;
	@XmlElement(name = "TAN_NO")
	private String tanNo;
	@XmlElement(name = "CUST_SR_NO")
	private String customerSrNo;
	@XmlElement(name = "APPLN_NO")
	private Integer applicationNo;
	@XmlElement(name = "FATHER_NAME")
	private String fatherName;
	@XmlElement(name = "EMPOYER_NAME")
	private String employerName;
	@XmlElement(name = "DRIVING_LICENSE_NUMBER")
	private String drivingLicense;
	@XmlElement(name = "PASSPORT_NO")
	private String passportNo;
	@XmlElement(name = "ADDRESS1_OFFICE")
	private String officeAddress1;
	@XmlElement(name = "ADDRESS2_OFFICE")
	private String officeAddress2;
	@XmlElement(name = "ADDRESS3_OFFICE")
	private String officeAddress3;
	@XmlElement(name = "AREA_OFFICE")
	private String officeArea;
	@XmlElement(name = "CITY_OFFICE")
	private String officeCity;
	@XmlElement(name = "PIN_OFFICE")
	private String officePinCode;
	@XmlElement(name = "LANDLINE1_OFFICE")
	private String officeLanLine1;
	@XmlElement(name = "LANDLINE2_OFFICE")
	private String officeLanLine2;
	@XmlElement(name = "STD_OFFICE")
	private String officeStdCode;
	@XmlElement(name = "PRODUCT")
	private String product;
	@XmlElement(name = "BATCH")
	private String batch;
	@XmlElement(name = "STAYING_SINCE")
	private String stayingSince;
	@XmlElement(name = "CITY_CLASSIFICATION")
	private String cityClassification;
	@XmlElement(name = "EMPLOYMENT_BUSINESS")
	private String employmentBusiness;
	@XmlElement(name = "AGE")
	private String age;
	@XmlElement(name = "RESIDENT_TYPE")
	private String residentType;
	@XmlElement(name = "CREDIT_PROGRAM")
	private String creditProgram;
	@XmlElement(name = "ASSET_CATEGORY")
	private String assetCategory;
	@XmlElement(name = "REQUEST_ID_FIN")
	private String finRequestId;
	@XmlElement(name = "MATCH_PROFILE")
	private String matchProfile = "5";
	@XmlElement(name = "SEGMENT")
	private String segment;
	@XmlElement(name = "APPLICANT_TYPE")
	private String applicatntType;
	@XmlElement(name = "DATE_OF_INCORPORATION")
	private String dateOfIncorporation;
	@XmlElement(name = "LANDMARK_OFFICE")
	private String officelandMark;
	@XmlElement(name = "MOBILE_OFFICE")
	private String officeMobile;
	@XmlElement(name = "EMAIL_OFFICE")
	private String officeMail;
	@XmlElement(name = "LOAN_APP_NO")
	private String loanApplicationNo;
	@XmlElement(name = "CustomerStatusYN")
	private String customerStatus;
	@XmlElement(name = "DemoDtlYN")
	private String demoDtl;

	@XmlElement(name = "AppscoreYN")
	private String appscore;

	public String getDealId() {
		return dealId;
	}

	public void setDealId(String dealId) {
		this.dealId = dealId;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLandMark() {
		return landMark;
	}

	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getLandLine1() {
		return landLine1;
	}

	public void setLandLine1(String landLine1) {
		this.landLine1 = landLine1;
	}

	public String getLandLine2() {
		return landLine2;
	}

	public void setLandLine2(String landLine2) {
		this.landLine2 = landLine2;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStdCode() {
		return stdCode;
	}

	public void setStdCode(String stdCode) {
		this.stdCode = stdCode;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getVoterId() {
		return voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public Integer getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Integer customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getLanNo() {
		return lanNo;
	}

	public void setLanNo(String lanNo) {
		this.lanNo = lanNo;
	}

	public String getLan2() {
		return lan2;
	}

	public void setLan2(String lan2) {
		this.lan2 = lan2;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getTanNo() {
		return tanNo;
	}

	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}

	public String getCustomerSrNo() {
		return customerSrNo;
	}

	public void setCustomerSrNo(String customerSrNo) {
		this.customerSrNo = customerSrNo;
	}

	public Integer getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(Integer applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public String getDrivingLicense() {
		return drivingLicense;
	}

	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getOfficeAddress1() {
		return officeAddress1;
	}

	public void setOfficeAddress1(String officeAddress1) {
		this.officeAddress1 = officeAddress1;
	}

	public String getOfficeAddress2() {
		return officeAddress2;
	}

	public void setOfficeAddress2(String officeAddress2) {
		this.officeAddress2 = officeAddress2;
	}

	public String getOfficeAddress3() {
		return officeAddress3;
	}

	public void setOfficeAddress3(String officeAddress3) {
		this.officeAddress3 = officeAddress3;
	}

	public String getOfficeArea() {
		return officeArea;
	}

	public void setOfficeArea(String officeArea) {
		this.officeArea = officeArea;
	}

	public String getOfficeCity() {
		return officeCity;
	}

	public void setOfficeCity(String officeCity) {
		this.officeCity = officeCity;
	}

	public String getOfficePinCode() {
		return officePinCode;
	}

	public void setOfficePinCode(String officePinCode) {
		this.officePinCode = officePinCode;
	}

	public String getOfficeLanLine1() {
		return officeLanLine1;
	}

	public void setOfficeLanLine1(String officeLanLine1) {
		this.officeLanLine1 = officeLanLine1;
	}

	public String getOfficeLanLine2() {
		return officeLanLine2;
	}

	public void setOfficeLanLine2(String officeLanLine2) {
		this.officeLanLine2 = officeLanLine2;
	}

	public String getOfficeStdCode() {
		return officeStdCode;
	}

	public void setOfficeStdCode(String officeStdCode) {
		this.officeStdCode = officeStdCode;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getStayingSince() {
		return stayingSince;
	}

	public void setStayingSince(String stayingSince) {
		this.stayingSince = stayingSince;
	}

	public String getCityClassification() {
		return cityClassification;
	}

	public void setCityClassification(String cityClassification) {
		this.cityClassification = cityClassification;
	}

	public String getEmploymentBusiness() {
		return employmentBusiness;
	}

	public void setEmploymentBusiness(String employmentBusiness) {
		this.employmentBusiness = employmentBusiness;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getResidentType() {
		return residentType;
	}

	public void setResidentType(String residentType) {
		this.residentType = residentType;
	}

	public String getCreditProgram() {
		return creditProgram;
	}

	public void setCreditProgram(String creditProgram) {
		this.creditProgram = creditProgram;
	}

	public String getAssetCategory() {
		return assetCategory;
	}

	public void setAssetCategory(String assetCategory) {
		this.assetCategory = assetCategory;
	}

	public String getFinRequestId() {
		return finRequestId;
	}

	public void setFinRequestId(String finRequestId) {
		this.finRequestId = finRequestId;
	}

	public String getMatchProfile() {
		return matchProfile;
	}

	public void setMatchProfile(String matchProfile) {
		this.matchProfile = matchProfile;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getApplicatntType() {
		return applicatntType;
	}

	public void setApplicatntType(String applicatntType) {
		this.applicatntType = applicatntType;
	}

	public String getDateOfIncorporation() {
		return dateOfIncorporation;
	}

	public void setDateOfIncorporation(String dateOfIncorporation) {
		this.dateOfIncorporation = dateOfIncorporation;
	}

	public String getOfficelandMark() {
		return officelandMark;
	}

	public void setOfficelandMark(String officelandMark) {
		this.officelandMark = officelandMark;
	}

	public String getOfficeMobile() {
		return officeMobile;
	}

	public void setOfficeMobile(String officeMobile) {
		this.officeMobile = officeMobile;
	}

	public String getOfficeMail() {
		return officeMail;
	}

	public void setOfficeMail(String officeMail) {
		this.officeMail = officeMail;
	}

	public String getLoanApplicationNo() {
		return loanApplicationNo;
	}

	public void setLoanApplicationNo(String loanApplicationNo) {
		this.loanApplicationNo = loanApplicationNo;
	}

	public String getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getAppscore() {
		return appscore;
	}

	public void setAppscore(String appscore) {
		this.appscore = appscore;
	}

	public String getDemoDtl() {
		return demoDtl;
	}

	public void setDemoDtl(String demoDtl) {
		this.demoDtl = demoDtl;
	}

}
