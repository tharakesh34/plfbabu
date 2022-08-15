package com.pennanttech.pff.model;

import java.util.Date;

public class InsPremiumCalculatorRequest {
	private String applicationId;
	private String gender;
	private Date dateOfBirth;
	private int age;
	private Double loanAmount;
	private String loanTenure;
	private String coverageTerm;
	private String source;

	// Getter Methods

	public String getApplicationId() {
		return applicationId;
	}

	public String getGender() {
		return gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public int getAge() {
		return age;
	}

	public Double getLoanAmount() {
		return loanAmount;
	}

	public String getLoanTenure() {
		return loanTenure;
	}

	public String getCoverageTerm() {
		return coverageTerm;
	}

	public String getSource() {
		return source;
	}

	// Setter Methods

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public void setLoanTenure(String loanTenure) {
		this.loanTenure = loanTenure;
	}

	public void setCoverageTerm(String coverageTerm) {
		this.coverageTerm = coverageTerm;
	}

	public void setSource(String source) {
		this.source = source;
	}
}