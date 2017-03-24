package com.pennanttech.ws.model.beneficiary;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.beneficiary.Beneficiary;

@XmlType(propOrder = { "beneficiaryList", "returnStatus"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "mandate")
public class BeneficiaryDetail {
	@XmlElementWrapper(name="beneficiaries")
	@XmlElement(name="beneficiary")
	private List<Beneficiary>beneficiaryList;
	@XmlElement
	private WSReturnStatus returnStatus;
	
	public List<Beneficiary> getBeneficiaryList() {
		return beneficiaryList;
	}
	public void setBeneficiaryList(List<Beneficiary> beneficiaryList) {
		this.beneficiaryList = beneficiaryList;
	}
	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
	
	

}
