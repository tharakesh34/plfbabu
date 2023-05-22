package com.pennanttech.external.services;

import java.io.Serializable;
import java.util.Map;

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
	private transient Object responceData;
	private String requestString;
	private String responseString;
	private Map<String, String> queryParams;
	private Map<String, String> pathParams;
	private boolean xmlRequest = false;
	private String serviceEndPoint;

	private boolean proxyRequired = false;
	private String proxyUrl;
	private int proxyPort = 0;
	private HttpHeaders responseHeaders;
	private String certificateFileName;
	private String certificatePassword;

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

	public Object getResponceData() {
		return responceData;
	}

	public void setResponceData(Object responceData) {
		this.responceData = responceData;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	public Map<String, String> getPathParams() {
		return pathParams;
	}

	public void setPathParams(Map<String, String> pathParams) {
		this.pathParams = pathParams;
	}

	public boolean isXmlRequest() {
		return xmlRequest;
	}

	public void setXmlRequest(boolean xmlRequest) {
		this.xmlRequest = xmlRequest;
	}

	public String getServiceEndPoint() {
		return serviceEndPoint;
	}

	public void setServiceEndPoint(String serviceEndPoint) {
		this.serviceEndPoint = serviceEndPoint;
	}

	public boolean isProxyRequired() {
		return proxyRequired;
	}

	public void setProxyRequired(boolean proxyRequired) {
		this.proxyRequired = proxyRequired;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(HttpHeaders responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getCertificateFileName() {
		return certificateFileName;
	}

	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}

	public String getCertificatePassword() {
		return certificatePassword;
	}

	public void setCertificatePassword(String certificatePassword) {
		this.certificatePassword = certificatePassword;
	}

}
