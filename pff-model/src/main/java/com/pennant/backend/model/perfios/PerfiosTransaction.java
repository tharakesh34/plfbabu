package com.pennant.backend.model.perfios;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class PerfiosTransaction implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement
	private String transationId;
	@XmlElement
	private String perfiosTransId;
	@XmlElement
	private String status;
	private String statusDesc;

	public PerfiosTransaction() {
		super();
	}

	public String getTransationId() {
		return transationId;
	}

	public void setTransationId(String transationId) {
		this.transationId = transationId;
	}

	public String getPerfiosTransId() {
		return perfiosTransId;
	}

	public void setPerfiosTransId(String perfiosTransId) {
		this.perfiosTransId = perfiosTransId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

}
