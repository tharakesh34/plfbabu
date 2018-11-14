package com.pennanttech.external.services;

import java.io.Serializable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class JsonServiceDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private String reference;
	private String serviceUrl;
	private HttpHeaders headers;
	private boolean excludeNull;
	private boolean excludeEmpty;
	private HttpMethod method;
	private String serviceName;

	private transient Object requestData;
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

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public boolean isExcludeNull() {
		return excludeNull;
	}

	public void setExcludeNull(boolean excludeNull) {
		this.excludeNull = excludeNull;
	}

	public boolean isExcludeEmpty() {
		return excludeEmpty;
	}

	public void setExcludeEmpty(boolean excludeEmpty) {
		this.excludeEmpty = excludeEmpty;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public Object getRequestData() {
		return requestData;
	}

	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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
