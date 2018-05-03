package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerEMail;
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"cif","customerEMail","custEMailTypeCode", "returnStatus" })
public class CustEMail {
	
	@XmlElement
	private String cif;
	
	@XmlElement(name="email")
	private CustomerEMail customerEMail;
	
	@XmlElement
	private String custEMailTypeCode ;
	
	@XmlElement
	private WSReturnStatus returnStatus;
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	
	
	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
	public CustomerEMail getCustomerEMail() {
		return customerEMail;
	}
	public void setCustomerEMail(CustomerEMail customerEMail) {
		this.customerEMail = customerEMail;
	}
	public String getCustEMailTypeCode() {
		return custEMailTypeCode;
	}
	public void setCustEMailTypeCode(String custEMailTypeCode) {
		this.custEMailTypeCode = custEMailTypeCode;
	}
	
}
