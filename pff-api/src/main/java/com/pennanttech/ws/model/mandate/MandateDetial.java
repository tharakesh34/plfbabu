package com.pennanttech.ws.model.mandate;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
}