package com.pennanttech.pff.model;

import java.io.Serializable;
import java.util.Date;

public class CustomerStaging implements Serializable {
	private static final long serialVersionUID = 6932976717013018189L;

	private long headerId;
	private String cif;
	private String salutation;
	private String salutationDesc;
	private String firstName;
	private String middleName;
	private String lastName;
	private String fullName;
	private Date dob;
	private String gender;
	private String custType;
	private String custDftBranch;
	private String custDftBranchName;
	private String custStaffId;
	private String custMaritalSts;
	private String custDSA;
	private String phoneNo;
	private String altPhoneNo;
	private String email;
	private String altEmail;
	private String pan;
	private String address;
	private String aadharNo;
	private long activeLoanCount;
	private String custStatus;
	private Date statusDate;
	private Date createdOn;
	private Date lastMntOn;
	private long processFlag;
	private String remarks;

	public CustomerStaging() {
		super();
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getCustStaffId() {
		return custStaffId;
	}

	public void setCustStaffId(String custStaffId) {
		this.custStaffId = custStaffId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getActiveLoanCount() {
		return activeLoanCount;
	}

	public void setActiveLoanCount(long activeLoanCount) {
		this.activeLoanCount = activeLoanCount;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public long getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(long processFlag) {
		this.processFlag = processFlag;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSalutationDesc() {
		return salutationDesc;
	}

	public void setSalutationDesc(String salutationDesc) {
		this.salutationDesc = salutationDesc;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustDftBranchName() {
		return custDftBranchName;
	}

	public void setCustDftBranchName(String custDftBranchName) {
		this.custDftBranchName = custDftBranchName;
	}

	public String getCustMaritalSts() {
		return custMaritalSts;
	}

	public void setCustMaritalSts(String custMaritalSts) {
		this.custMaritalSts = custMaritalSts;
	}

	public String getCustDSA() {
		return custDSA;
	}

	public void setCustDSA(String custDSA) {
		this.custDSA = custDSA;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getAltPhoneNo() {
		return altPhoneNo;
	}

	public void setAltPhoneNo(String altPhoneNo) {
		this.altPhoneNo = altPhoneNo;
	}

	public String getAltEmail() {
		return altEmail;
	}

	public void setAltEmail(String altEmail) {
		this.altEmail = altEmail;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Date lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public String getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}
}
