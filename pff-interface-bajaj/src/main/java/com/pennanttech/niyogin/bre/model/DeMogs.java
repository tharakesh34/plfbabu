package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "dateOfInc", "typeOfIndustry", "operationalOffcPincode", "registeredOffcPincode", "gstin",
		"categoryOfApplicant", "panNumber", "residenceTypeOfMDorPROPTRYorMNGNGPARTNER", "mobileNumber", "email",
		"applicantAdhaar", "udyogadhaar", "yrsAtCurResidencePROPorMPorMDetc", "zipCode" })
@XmlRootElement(name = "DEMOGS")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeMogs {
	@XmlElement(name = "DATEOFINC")
	private String	dateOfInc;

	@XmlElement(name = "TYPEOFINDUSTRY")
	private String	typeOfIndustry;

	@XmlElement(name = "OPERATIONALOFFICEPINCODE")
	private String	operationalOffcPincode;

	@XmlElement(name = "REGISTEREDOFFICEPINCODE")
	private String	registeredOffcPincode;

	@XmlElement(name = "GSTIN")
	private String	gstin;

	@XmlElement(name = "CATEGORYOFAPPLICANT")
	private String	categoryOfApplicant;

	@XmlElement(name = "PANNUMBER")
	private String	panNumber;

	@XmlElement(name = "RESIDENCETYPEOFMDORPROPREITORORMANAGINGPARTNER")
	private String	residenceTypeOfMDorPROPTRYorMNGNGPARTNER;

	@XmlElement(name = "MOBILENUMBER")
	private String	mobileNumber;

	@XmlElement(name = "EMAIL")
	private String	email;

	@XmlElement(name = "APPLICANTADHAAR")
	private String	applicantAdhaar;

	@XmlElement(name = "UDYOGADHAAR")
	private String	udyogadhaar;

	@XmlElement(name = "YEARSATCURRENTRESIDENCEPROPORMPORMDETC")
	private int		yrsAtCurResidencePROPorMPorMDetc;

	@XmlElement(name = "ZIPCODE")
	private String	zipCode;

	public String getDateOfInc() {
		return dateOfInc;
	}

	public void setDateOfInc(String dateOfInc) {
		this.dateOfInc = dateOfInc;
	}

	public String getTypeOfIndustry() {
		return typeOfIndustry;
	}

	public void setTypeOfIndustry(String typeOfIndustry) {
		this.typeOfIndustry = typeOfIndustry;
	}

	public String getOperationalOffcPincode() {
		return operationalOffcPincode;
	}

	public void setOperationalOffcPincode(String operationalOffcPincode) {
		this.operationalOffcPincode = operationalOffcPincode;
	}

	public String getRegisteredOffcPincode() {
		return registeredOffcPincode;
	}

	public void setRegisteredOffcPincode(String registeredOffcPincode) {
		this.registeredOffcPincode = registeredOffcPincode;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getCategoryOfApplicant() {
		return categoryOfApplicant;
	}

	public void setCategoryOfApplicant(String categoryOfApplicant) {
		this.categoryOfApplicant = categoryOfApplicant;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getResidenceTypeOfMDorPROPTRYorMNGNGPARTNER() {
		return residenceTypeOfMDorPROPTRYorMNGNGPARTNER;
	}

	public void setResidenceTypeOfMDorPROPTRYorMNGNGPARTNER(String residenceTypeOfMDorPROPTRYorMNGNGPARTNER) {
		this.residenceTypeOfMDorPROPTRYorMNGNGPARTNER = residenceTypeOfMDorPROPTRYorMNGNGPARTNER;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApplicantAdhaar() {
		return applicantAdhaar;
	}

	public void setApplicantAdhaar(String applicantAdhaar) {
		this.applicantAdhaar = applicantAdhaar;
	}

	public String getUdyogadhaar() {
		return udyogadhaar;
	}

	public void setUdyogadhaar(String udyogadhaar) {
		this.udyogadhaar = udyogadhaar;
	}

	public int getYrsAtCurResidencePROPorMPorMDetc() {
		return yrsAtCurResidencePROPorMPorMDetc;
	}

	public void setYrsAtCurResidencePROPorMPorMDetc(int yrsAtCurResidencePROPorMPorMDetc) {
		this.yrsAtCurResidencePROPorMPorMDetc = yrsAtCurResidencePROPorMPorMDetc;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
