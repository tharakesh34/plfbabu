package com.pennant.backend.model.sourcingdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.WSReturnStatus;

@XmlRootElement(name = "sourcingDetails")
@XmlAccessorType(XmlAccessType.NONE)
public class SourcingDetails {

	@XmlElement
	private String finReference;
	@XmlElement
	private String finalSource;
	@XmlElement
	private String dmaCode;
	@XmlElement
	private Long primaryRelationOfficer = (long) 0;
	@XmlElement
	private String pslCategory;
	@XmlElement
	private WSReturnStatus returnStatus;

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public SourcingDetails() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinalSource() {
		return finalSource;
	}

	public void setFinalSource(String finalSource) {
		this.finalSource = finalSource;
	}

	public String getDmaCode() {
		return dmaCode;
	}

	public void setDmaCode(String dmaCode) {
		this.dmaCode = dmaCode;
	}

	public Long getPrimaryRelationOfficer() {
		return primaryRelationOfficer;
	}

	public void setPrimaryRelationOfficer(Long primaryRelationOfficer) {
		this.primaryRelationOfficer = primaryRelationOfficer;
	}

	public String getPslCategory() {
		return pslCategory;
	}

	public void setPslCategory(String pslCategory) {
		this.pslCategory = pslCategory;
	}

}
