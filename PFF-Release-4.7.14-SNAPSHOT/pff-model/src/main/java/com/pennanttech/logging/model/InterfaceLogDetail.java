package com.pennanttech.logging.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class InterfaceLogDetail implements Serializable {

	private static final long	serialVersionUID	= 1221821037156917579L;

	private long				seqId = Long.MIN_VALUE;
	private String				reference;
	private String				serviceName;
	private String				endPoint;
	private String				request;
	private String				response;
	private Timestamp			reqSentOn;
	private Timestamp			respReceivedOn;
	private String				status;
	private String				errorCode;
	private String				errorDesc;

	public InterfaceLogDetail() {
		super();
	}


	public long getSeqId() {
		return seqId;
	}

	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Timestamp getReqSentOn() {
		return reqSentOn;
	}

	public void setReqSentOn(Timestamp reqSentOn) {
		this.reqSentOn = reqSentOn;
	}

	public Timestamp getRespReceivedOn() {
		return respReceivedOn;
	}

	public void setRespReceivedOn(Timestamp respReceivedOn) {
		this.respReceivedOn = respReceivedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
}
