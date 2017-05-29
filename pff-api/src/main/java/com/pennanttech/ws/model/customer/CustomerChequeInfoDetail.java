package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerChequeInfo", "chequeSeq", "returnStatus" })
public class CustomerChequeInfoDetail {

	
	@XmlElement
	private String cif;

	@XmlElement(name = "accountBehaviour")
	private  CustomerChequeInfo  customerChequeInfo;

	@XmlElement
	private int chequeSeq;
	
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

	public CustomerChequeInfo getCustomerChequeInfo() {
		return customerChequeInfo;
	}

	public void setCustomerChequeInfo(CustomerChequeInfo customerChequeInfo) {
		this.customerChequeInfo = customerChequeInfo;
	}

	public int getChequeSeq() {
		return chequeSeq;
	}

	public void setChequeSeq(int chequeSeq) {
		this.chequeSeq = chequeSeq;
	}

}
