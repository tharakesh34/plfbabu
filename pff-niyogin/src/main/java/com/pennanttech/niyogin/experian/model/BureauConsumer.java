package com.pennanttech.niyogin.experian.model;

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
	private long applicationId;
	@XmlElement(name = "STG_UNQ_REF_ID")
    private long StgUnqRefId;	
	private ConsumerAddress address;
	private PersonalDetails personal;

	public long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}

	public long getStgUnqRefId() {
		return StgUnqRefId;
	}

	public void setStgUnqRefId(long stgUnqRefId) {
		StgUnqRefId = stgUnqRefId;
	}

	public ConsumerAddress getAddress() {
		return address;
	}

	public void setAddress(ConsumerAddress address) {
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
