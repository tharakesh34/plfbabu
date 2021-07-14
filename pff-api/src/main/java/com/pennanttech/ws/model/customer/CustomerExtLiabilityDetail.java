package com.pennanttech.ws.model.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerExtLiability;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "externalLiability", "liabilitySeq", "returnStatus" })
public class CustomerExtLiabilityDetail {

	@XmlElement
	private String cif;

	@JsonProperty("customerExtLiability")
	private CustomerExtLiability externalLiability;

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

	public CustomerExtLiability getExternalLiability() {
		return externalLiability;
	}

	public void setExternalLiability(CustomerExtLiability externalLiability) {
		this.externalLiability = externalLiability;
	}

	public int getLiabilitySeq() {
		return liabilitySeq;
	}

	public void setLiabilitySeq(int liabilitySeq) {
		this.liabilitySeq = liabilitySeq;
	}

}
