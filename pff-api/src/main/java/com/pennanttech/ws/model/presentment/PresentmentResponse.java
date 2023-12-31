package com.pennanttech.ws.model.presentment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

@XmlType(propOrder = { "presentmentDetails", "approvedPresentments", "presentment", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "presentmentResponse")
public class PresentmentResponse {

	@XmlElement
	private List<PresentmentDetail> presentmentDetails;

	@XmlElement
	private Long approvedPresentments;

	@XmlElement
	private Presentment presentment;

	@XmlElement
	private WSReturnStatus returnStatus;

	public PresentmentResponse() {
		super();
	}

	public List<PresentmentDetail> getPresentmentDetails() {
		return presentmentDetails;
	}

	public void setPresentmentDetails(List<PresentmentDetail> presentmentDetails) {
		this.presentmentDetails = presentmentDetails;
	}

	public Long getApprovedPresentments() {
		return approvedPresentments;
	}

	public void setApprovedPresentments(Long approvedPresentments) {
		this.approvedPresentments = approvedPresentments;
	}

	public Presentment getPresentment() {
		return presentment;
	}

	public void setPresentment(Presentment presentment) {
		this.presentment = presentment;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
