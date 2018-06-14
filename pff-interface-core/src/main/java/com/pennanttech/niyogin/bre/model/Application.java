package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "dateOfApplication", "applicationId", "bureau", "socialsc", "appliedLoanAmount" })
@XmlRootElement(name = "APPLICATION")
@XmlAccessorType(XmlAccessType.FIELD)
public class Application {

	@XmlElement(name = "DATEOFAPPLICATION")
	private String			dateOfApplication;

	@XmlElement(name = "APPLICATIONID")
	private String			applicationId;

	@XmlElement(name = "BUREAU")
	private Bureau			bureau;

	@XmlElement(name = "SOCIALSC")
	private List<SOCIALSC>	socialsc;

	@XmlElement(name = "APPLIEDLOANAMOUNT")
	private BigDecimal		appliedLoanAmount	= BigDecimal.ZERO;

	public String getDateOfApplication() {
		return dateOfApplication;
	}

	public void setDateOfApplication(String dateOfApplication) {
		this.dateOfApplication = dateOfApplication;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Bureau getBureau() {
		return bureau;
	}

	public void setBureau(Bureau bureau) {
		this.bureau = bureau;
	}

	public List<SOCIALSC> getSocialsc() {
		return socialsc;
	}

	public void setSocialsc(List<SOCIALSC> socialsc) {
		this.socialsc = socialsc;
	}

	public BigDecimal getAppliedLoanAmount() {
		return appliedLoanAmount;
	}

	public void setAppliedLoanAmount(BigDecimal appliedLoanAmount) {
		this.appliedLoanAmount = appliedLoanAmount;
	}

}
