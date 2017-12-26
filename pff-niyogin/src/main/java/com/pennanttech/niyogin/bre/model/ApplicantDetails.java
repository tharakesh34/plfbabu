package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "typeOfIndustry", "resTypeOfMdOrPropreitorOrManagingPartner", "yrsAtCurrentResPropOrMpOrMdEtc",
		"regOfficePincode" })
@XmlRootElement(name = "Details")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicantDetails {

	@XmlElement(name = "TypeOfIndustry")
	private String	typeOfIndustry;

	@XmlElement(name = "ResidenceTypeOfMdOrPropreitorOrManagingPartner")
	private String	resTypeOfMdOrPropreitorOrManagingPartner;

	@XmlElement(name = "YearsAtCurrentResidencePropOrMpOrMdEtc")
	private int		yrsAtCurrentResPropOrMpOrMdEtc;

	@XmlElement(name = "RegisteredOfficePincode")
	private String	regOfficePincode;

	public String getTypeOfIndustry() {
		return typeOfIndustry;
	}

	public void setTypeOfIndustry(String typeOfIndustry) {
		this.typeOfIndustry = typeOfIndustry;
	}

	public String getResTypeOfMdOrPropreitorOrManagingPartner() {
		return resTypeOfMdOrPropreitorOrManagingPartner;
	}

	public void setResTypeOfMdOrPropreitorOrManagingPartner(String resTypeOfMdOrPropreitorOrManagingPartner) {
		this.resTypeOfMdOrPropreitorOrManagingPartner = resTypeOfMdOrPropreitorOrManagingPartner;
	}

	public int getYrsAtCurrentResPropOrMpOrMdEtc() {
		return yrsAtCurrentResPropOrMpOrMdEtc;
	}

	public void setYrsAtCurrentResPropOrMpOrMdEtc(int yrsAtCurrentResPropOrMpOrMdEtc) {
		this.yrsAtCurrentResPropOrMpOrMdEtc = yrsAtCurrentResPropOrMpOrMdEtc;
	}

	public String getRegOfficePincode() {
		return regOfficePincode;
	}

	public void setRegOfficePincode(String regOfficePincode) {
		this.regOfficePincode = regOfficePincode;
	}

}
