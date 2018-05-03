package com.pennanttech.niyogin.legaldesk.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "name", "pan" })
@XmlRootElement(name = "co_borrower")
@XmlAccessorType(XmlAccessType.NONE)
public class CoBorrower {

	@XmlElement(name = "name")
	private String	name;

	@XmlElement(name = "pan")
	private String	pan;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

}
