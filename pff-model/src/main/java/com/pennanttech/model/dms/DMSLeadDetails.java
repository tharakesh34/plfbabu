package com.pennanttech.model.dms;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DMSLeadDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement
	private List<String> leadIds;
	@XmlElement
	private String status;

	private String processedFlag;
	private String statusDesc;

	public DMSLeadDetails() {
		super();
	}

	public List<String> getLeadIds() {
		return leadIds;
	}

	public void setLeadIds(List<String> leadIds) {
		this.leadIds = leadIds;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessedFlag() {
		return processedFlag;
	}

	public void setProcessedFlag(String processedFlag) {
		this.processedFlag = processedFlag;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

}