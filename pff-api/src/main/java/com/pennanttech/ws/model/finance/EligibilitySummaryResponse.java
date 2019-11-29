package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;
@XmlAccessorType(XmlAccessType.FIELD)
public class EligibilitySummaryResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@XmlElement
	private String summary;
	@XmlElement
	private List<EligibilityRespone> eligibilityResponeList = new ArrayList<EligibilityRespone>();
	@XmlElement
	private WSReturnStatus returnStatus;

	public List<EligibilityRespone> getEligibilityResponeList() {
		return eligibilityResponeList;
	}

	public void setEligibilityResponeList(List<EligibilityRespone> eligibilityResponeList) {
		this.eligibilityResponeList = eligibilityResponeList;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
	
	

}
