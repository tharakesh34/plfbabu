package com.pennanttech.ws.model.covenantStatus;

import com.pennant.backend.model.WSReturnStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finreference", "docStauts", "covenantTypeId", "category", "covenantType", "returnStatus" })
@XmlRootElement(name = "covenantStatus")
@XmlAccessorType(XmlAccessType.NONE)
public class CovenantStatus {
	@XmlElement
	private String finreference;
	@XmlElement
	private String docStauts;
	@XmlElement
	private long covenantTypeId;
	@XmlElement
	private String category;
	@XmlElement
	private String covenantType;
	@XmlElement
	private WSReturnStatus returnStatus;

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

	public String getDocStauts() {
		return docStauts;
	}

	public void setDocStauts(String docStauts) {
		this.docStauts = docStauts;
	}

	public long getCovenantTypeId() {
		return covenantTypeId;
	}

	public String getCategory() {
		return category;
	}

	public String getCovenantType() {
		return covenantType;
	}

	public void setCovenantType(String covenantType) {
		this.covenantType = covenantType;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCovenantTypeId(long covenantTypeId) {
		this.covenantTypeId = covenantTypeId;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
