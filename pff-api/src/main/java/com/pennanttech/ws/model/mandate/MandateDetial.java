package com.pennanttech.ws.model.mandate;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.mandate.Mandate;

@XmlType(propOrder = { "mandateList", "oldMandateId", "newMandateId", "finReference", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "mandate")
public class MandateDetial {

	@XmlElementWrapper(name = "mandates")
	@XmlElement(name = "mandate")
	private List<Mandate> mandateList;

	@XmlElement
	private WSReturnStatus returnStatus;

	@XmlElement
	private Long oldMandateId;

	@XmlElement
	private Long newMandateId;

	@XmlElement
	private String finReference;

	private String mandateType;

	public List<Mandate> getMandatesList() {
		return mandateList;
	}

	public void setMandateList(List<Mandate> mandateList) {
		this.mandateList = mandateList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public Long getOldMandateId() {
		return oldMandateId;
	}

	public void setOldMandateId(Long oldMandateId) {
		this.oldMandateId = oldMandateId;
	}

	public Long getNewMandateId() {
		return newMandateId;
	}

	public void setNewMandateId(Long newMandateId) {
		this.newMandateId = newMandateId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}
}