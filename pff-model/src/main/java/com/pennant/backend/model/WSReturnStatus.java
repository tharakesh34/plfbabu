package com.pennant.backend.model;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * This is the class used for web services from pff-api. This class will be included in all the bean objects to return
 * the code and description in web services response messages.
 */
@XmlRootElement(name = "returnStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class WSReturnStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	String returnCode;
	String returnText;

	public WSReturnStatus copyEntity() {
		WSReturnStatus entity = new WSReturnStatus();
		entity.setReturnCode(this.returnCode);
		entity.setReturnText(this.returnText);
		return entity;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

}
