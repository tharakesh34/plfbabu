package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.util.Date;

public class CoreBankingCustomer implements Serializable {

	private static final long serialVersionUID = 384180539764860246L;
	
	public CoreBankingCustomer() {
    	super();
    }
	
	private String customerMnemonic;
	private String customerLocation;
	private String customerFullName;
	private String defaultAccountShortName;
	private String customerType;
	private String customerBlocked;
	private String customerClosed;
	private String customerDeceased;
	private String customerInactive;
	private String languageCode;
	private String parentCountry;
	private String riskCountry;
	private String residentCountry;
	private Date   closedDate;
	private String customerBranchMnemonic;
	private String groupStatus;
	private String groupName;
	private String segmentIdentifier;
	private String salutation;
	private Date   custDOB;
	private String genderCode;
	private String custPOB;
	private String custPassportNum;
	private Date   custPassportExpiry;
	private String minor;
	private String tradeLicNumber;
	private Date   tradeLicExpiry;
	private String visaNumber;
	private Date   visaExpiry;
	private String nationality;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getCustomerMnemonic() {
		return customerMnemonic;
	}
	public void setCustomerMnemonic(String customerMnemonic) {
		this.customerMnemonic = customerMnemonic;
	}
	
	public String getCustomerLocation() {
		return customerLocation;
	}
	public void setCustomerLocation(String customerLocation) {
		this.customerLocation = customerLocation;
	}
	
	public String getCustomerFullName() {
		return customerFullName;
	}
	public void setCustomerFullName(String customerFullName) {
		this.customerFullName = customerFullName;
	}
	
	public String getDefaultAccountShortName() {
		return defaultAccountShortName;
	}
	public void setDefaultAccountShortName(String defaultAccountShortName) {
		this.defaultAccountShortName = defaultAccountShortName;
	}
	
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	public String getCustomerBlocked() {
		return customerBlocked;
	}
	public void setCustomerBlocked(String customerBlocked) {
		this.customerBlocked = customerBlocked;
	}
	
	public String getCustomerClosed() {
		return customerClosed;
	}
	public void setCustomerClosed(String customerClosed) {
		this.customerClosed = customerClosed;
	}
	
	public String getCustomerDeceased() {
		return customerDeceased;
	}
	public void setCustomerDeceased(String customerDeceased) {
		this.customerDeceased = customerDeceased;
	}
	
	public String getCustomerInactive() {
		return customerInactive;
	}
	public void setCustomerInactive(String customerInactive) {
		this.customerInactive = customerInactive;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	public String getParentCountry() {
		return parentCountry;
	}
	public void setParentCountry(String parentCountry) {
		this.parentCountry = parentCountry;
	}
	
	public String getRiskCountry() {
		return riskCountry;
	}
	public void setRiskCountry(String riskCountry) {
		this.riskCountry = riskCountry;
	}
	
	public String getResidentCountry() {
		return residentCountry;
	}
	public void setResidentCountry(String residentCountry) {
		this.residentCountry = residentCountry;
	}
	
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
	public String getCustomerBranchMnemonic() {
		return customerBranchMnemonic;
	}
	public void setCustomerBranchMnemonic(String customerBranchMnemonic) {
		this.customerBranchMnemonic = customerBranchMnemonic;
	}
	
	public String getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getSegmentIdentifier() {
		return segmentIdentifier;
	}
	public void setSegmentIdentifier(String segmentIdentifier) {
		this.segmentIdentifier = segmentIdentifier;
	}
	
	public String getSalutation() {
		return salutation;
	}
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}
	
	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}
	
	public String getGenderCode() {
		return genderCode;
	}
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}
	
	public String getCustPOB() {
		return custPOB;
	}
	public void setCustPOB(String custPOB) {
		this.custPOB = custPOB;
	}
	
	public String getCustPassportNum() {
		return custPassportNum;
	}
	public void setCustPassportNum(String custPassportNum) {
		this.custPassportNum = custPassportNum;
	}
	
	public Date getCustPassportExpiry() {
		return custPassportExpiry;
	}
	public void setCustPassportExpiry(Date custPassportExpiry) {
		this.custPassportExpiry = custPassportExpiry;
	}
	
	public String getMinor() {
		return minor;
	}
	public void setMinor(String minor) {
		this.minor = minor;
	}
	
	public String getTradeLicNumber() {
		return tradeLicNumber;
	}
	public void setTradeLicNumber(String tradeLicNumber) {
		this.tradeLicNumber = tradeLicNumber;
	}
	
	public Date getTradeLicExpiry() {
		return tradeLicExpiry;
	}
	public void setTradeLicExpiry(Date tradeLicExpiry) {
		this.tradeLicExpiry = tradeLicExpiry;
	}
	
	public String getVisaNumber() {
		return visaNumber;
	}
	public void setVisaNumber(String visaNumber) {
		this.visaNumber = visaNumber;
	}
	
	public Date getVisaExpiry() {
		return visaExpiry;
	}
	public void setVisaExpiry(Date visaExpiry) {
		this.visaExpiry = visaExpiry;
	}
	
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	
}
