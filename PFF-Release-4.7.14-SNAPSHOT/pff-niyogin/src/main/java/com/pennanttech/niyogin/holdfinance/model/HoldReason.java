package com.pennanttech.niyogin.holdfinance.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "code", "description" })
@XmlRootElement(name = "holdReasons")
@XmlAccessorType(XmlAccessType.NONE)
public class HoldReason {

	@XmlElement(name = "code")
	private String	code;

	@XmlElement(name = "description")
	private String	description;

	private String	holdCatageory;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHoldCatageory() {
		return holdCatageory;
	}

	public void setHoldCatageory(String holdCatageory) {
		this.holdCatageory = holdCatageory;
	}

}
