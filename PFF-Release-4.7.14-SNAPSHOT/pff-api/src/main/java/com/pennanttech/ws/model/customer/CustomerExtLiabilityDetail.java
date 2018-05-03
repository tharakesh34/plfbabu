package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerExtLiability;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerExtLiability", "liabilitySeq", "returnStatus" })
public class CustomerExtLiabilityDetail {

	
	@XmlElement
	private String cif;

	@XmlElement(name = "customerExtLiability")
	private  CustomerExtLiability  customerExtLiability;

	@XmlElement
	private int liabilitySeq;
	
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

	public CustomerExtLiability getCustomerExtLiability() {
		return customerExtLiability;
	}

	public void setCustomerExtLiability(CustomerExtLiability customerExtLiability) {
		this.customerExtLiability = customerExtLiability;
	}

	public int getLiabilitySeq() {
		return liabilitySeq;
	}

	public void setLiabilitySeq(int liabilitySeq) {
		this.liabilitySeq = liabilitySeq;
	}

}
