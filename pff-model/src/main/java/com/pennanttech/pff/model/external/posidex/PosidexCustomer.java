package com.pennanttech.pff.model.external.posidex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PosidexCustomer extends Posidex implements Serializable {
	static final long serialVersionUID = 1L;

	private String firstName;
	private String middleName;
	private String lastName;
	private Date dob;
	private String pan;
	private String drivingLicenseNumber;
	private String voterId;
	private Date dateOfIncorporation;
	private String tanNo;
	private String applicantType;
	private String empoyerName;
	private String fatherName;
	private String passportNo;
	private String accountNumber;
	private String creditCardNumber;
	private String ucinFlag;
	private Date insertTs;
	private String gender;
	private String aadharNo;
	private String cin;
	private String din;
	private String registrationNo;
	private String caNumber;
	private String custCoreBank;

	private List<PosidexCustomerAddress> posidexCustomerAddress = new ArrayList<PosidexCustomerAddress>();
	private List<PosidexCustomerLoan> posidexCustomerLoans = new ArrayList<PosidexCustomerLoan>();

	public PosidexCustomer() {
	    super();
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

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getDrivingLicenseNumber() {
		return drivingLicenseNumber;
	}

	public void setDrivingLicenseNumber(String drivingLicenseNumber) {
		this.drivingLicenseNumber = drivingLicenseNumber;
	}

	public String getVoterId() {
		return voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}

	public Date getDateOfIncorporation() {
		return dateOfIncorporation;
	}

	public void setDateOfIncorporation(Date dateOfIncorporation) {
		this.dateOfIncorporation = dateOfIncorporation;
	}

	public String getTanNo() {
		return tanNo;
	}

	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public String getEmpoyerName() {
		return empoyerName;
	}

	public void setEmpoyerName(String empoyerName) {
		this.empoyerName = empoyerName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getUcinFlag() {
		return ucinFlag;
	}

	public void setUcinFlag(String ucinFlag) {
		this.ucinFlag = ucinFlag;
	}

	public Date getInsertTs() {
		return insertTs;
	}

	public void setInsertTs(Date insertTs) {
		this.insertTs = insertTs;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getDin() {
		return din;
	}

	public void setDin(String din) {
		this.din = din;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getCaNumber() {
		return caNumber;
	}

	public void setCaNumber(String caNumber) {
		this.caNumber = caNumber;
	}

	public List<PosidexCustomerAddress> getPosidexCustomerAddress() {
		return posidexCustomerAddress;
	}

	public void setPosidexCustomerAddress(List<PosidexCustomerAddress> posidexCustomerAddress) {
		this.posidexCustomerAddress = posidexCustomerAddress;
	}

	public List<PosidexCustomerLoan> getPosidexCustomerLoans() {
		return posidexCustomerLoans;
	}

	public void setPosidexCustomerLoans(List<PosidexCustomerLoan> posidexCustomerLoans) {
		this.posidexCustomerLoans = posidexCustomerLoans;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

}