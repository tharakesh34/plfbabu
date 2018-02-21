package com.pennanttech.niyogin.cibil.consumer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "stgUniqueRefId", "applicationId", "custCIF", "finReference", "address", "personalDetails" })
@XmlRootElement(name = "cibilConsumerRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CibilConsumerRequest implements Serializable {

	private static final long serialVersionUID = -2400900921015801759L;

	@XmlElement(name = "STG_UNQ_REF_ID")
	private long					stgUniqueRefId;

	@XmlElement(name = "APPLICATION_ID")
	private long					applicationId;
	@XmlElement(name = "CIF")
	private String					custCIF;
	private String					finReference;
	@XmlElement(name = "personal")
	private CibilPersonalDetails	personalDetails;

	private CibilConsumerAddress	address;

	public long getStgUniqueRefId() {
		return stgUniqueRefId;
	}

	public void setStgUniqueRefId(long stgUniqueRefId) {
		this.stgUniqueRefId = stgUniqueRefId;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}

	public CibilPersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(CibilPersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	public CibilConsumerAddress getAddress() {
		return address;
	}

	public void setAddress(CibilConsumerAddress address) {
		this.address = address;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

}
