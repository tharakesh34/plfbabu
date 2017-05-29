package com.pennant.backend.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the class used for web services from pff-api.
 * This class will be included in all the bean objects to
 * return the code and description in web services response messages.
 */
@XmlRootElement(name="returnStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class WSReturnStatus {

	String returnCode;
	String returnText;
	
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
