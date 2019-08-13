package com.pennanttech.external.services;

public class SOAPServiceDetail {

	private String reference;
	private String serviceUrl;
	private String serviceEndPoint;
	private String serviceName;
	private String protocol; 
	private transient Object requestData;
	private transient Object responceData;
	private String requestString;
	private String responseString;
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getServiceUrl() {
		return serviceUrl;
	}
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	public String getServiceEndPoint() {
		return serviceEndPoint;
	}
	public void setServiceEndPoint(String serviceEndPoint) {
		this.serviceEndPoint = serviceEndPoint;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public Object getRequestData() {
		return requestData;
	}
	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}
	public Object getResponceData() {
		return responceData;
	}
	public void setResponceData(Object responceData) {
		this.responceData = responceData;
	}
	public String getRequestString() {
		return requestString;
	}
	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}
	public String getResponseString() {
		return responseString;
	}
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
}
