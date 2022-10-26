package com.pennant.pff.document.model;

import java.io.Serializable;

public class DocVerificationDetail implements Serializable {
	private static final long serialVersionUID = 674926398433028047L;

	private Long id;
	private Long headerId;
	private String fullName = "";
	private String fName = "";
	private String mName = "";
	private String lName = "";
	private String fatherOrHusbandName;
	private String gender;
	private String dob;
	private int age;
	private String panNumber;
	private String aadhaarNumber;

	private DocVerificationAddress docVerificatinAddress;

	public DocVerificationDetail() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(Long headerId) {
		this.headerId = headerId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFatherOrHusbandName() {
		return fatherOrHusbandName;
	}

	public void setFatherOrHusbandName(String fatherOrHusbandName) {
		this.fatherOrHusbandName = fatherOrHusbandName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getAadhaarNumber() {
		return aadhaarNumber;
	}

	public void setAadhaarNumber(String aadhaarNumber) {
		this.aadhaarNumber = aadhaarNumber;
	}

	public DocVerificationAddress getDocVerificatinAddress() {
		return docVerificatinAddress;
	}

	public void setDocVerificatinAddress(DocVerificationAddress docVerificatinAddress) {
		this.docVerificatinAddress = docVerificatinAddress;
	}

	public String getFName() {
		return fName;
	}

	public void setFName(String fName) {
		this.fName = fName;
	}

	public String getMName() {
		return mName;
	}

	public void setMName(String mName) {
		this.mName = mName;
	}

	public String getLName() {
		return lName;
	}

	public void setLName(String lName) {
		this.lName = lName;
	}

}
