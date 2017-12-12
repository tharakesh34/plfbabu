package com.pennanttech.niyogin.bureau.consumer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "applicationId", "StgUnqRefId", "address", "personal" })
@XmlRootElement(name = "bureauconsumer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BureauConsumer implements Serializable {

	private static final long serialVersionUID = -8306856974384474724L;
	@XmlElement(name = "APPLICATION_ID")
	private String applicationId;
	@XmlElement(name = "STG_UNQ_REF_ID")
    private String StgUnqRefId;	
	private Address address;
	private PersonalDetails personal;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getStgUnqRefId() {
		return StgUnqRefId;
	}

	public void setStgUnqRefId(String stgUnqRefId) {
		StgUnqRefId = stgUnqRefId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public PersonalDetails getPersonal() {
		return personal;
	}

	public void setPersonal(PersonalDetails personal) {
		this.personal = personal;
	}

	@Override
	public String toString() {
		return "BureauConsumer [applicationId=" + applicationId + ", StgUnqRefId=" + StgUnqRefId + ", address="
				+ address + ", personal=" + personal + "]";
	}

}
