package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "dob", "minAge", "maxAge", "authority", "currentResidencePincode", "permanentResidencePincode",
		"relOfScndaryCoAppWithPrimaryCoApp", "coAppPanNumber", "salToPartnerOrDirector" })
@XmlRootElement(name = "CODEMOGS")
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeMogs {
	@XmlElement(name = "DOB")
	private String		dob;

	@XmlElement(name = "MINIMUMAGE")
	private int			minAge;

	@XmlElement(name = "MAXIMUMAGE")
	private int			maxAge;

	@XmlElement(name = "AUTHORITY")
	private String		authority;

	@XmlElement(name = "CURRENTRESIDENCEPINCODE")
	private String		currentResidencePincode;

	@XmlElement(name = "PERMANENTRESIDENCEPINCODE")
	private String		permanentResidencePincode;

	@XmlElement(name = "RELATIONSHIPOFSECONDARYCOAPPLICANTWITHPRIMARYCOAPPLICANT")
	private String		relOfScndaryCoAppWithPrimaryCoApp;

	@XmlElement(name = "COAPPLICANTPANNUMBER")
	private String		coAppPanNumber;

	@XmlElement(name = "SALARYTOPARTNERORDIRECTOR")
	private BigDecimal	salToPartnerOrDirector	= BigDecimal.ZERO;

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getCurrentResidencePincode() {
		return currentResidencePincode;
	}

	public void setCurrentResidencePincode(String currentResidencePincode) {
		this.currentResidencePincode = currentResidencePincode;
	}

	public String getPermanentResidencePincode() {
		return permanentResidencePincode;
	}

	public void setPermanentResidencePincode(String permanentResidencePincode) {
		this.permanentResidencePincode = permanentResidencePincode;
	}

	public String getRelOfScndaryCoAppWithPrimaryCoApp() {
		return relOfScndaryCoAppWithPrimaryCoApp;
	}

	public void setRelOfScndaryCoAppWithPrimaryCoApp(String relOfScndaryCoAppWithPrimaryCoApp) {
		this.relOfScndaryCoAppWithPrimaryCoApp = relOfScndaryCoAppWithPrimaryCoApp;
	}

	public String getCoAppPanNumber() {
		return coAppPanNumber;
	}

	public void setCoAppPanNumber(String coAppPanNumber) {
		this.coAppPanNumber = coAppPanNumber;
	}

	public BigDecimal getSalToPartnerOrDirector() {
		return salToPartnerOrDirector;
	}

	public void setSalToPartnerOrDirector(BigDecimal salToPartnerOrDirector) {
		this.salToPartnerOrDirector = salToPartnerOrDirector;
	}

}
