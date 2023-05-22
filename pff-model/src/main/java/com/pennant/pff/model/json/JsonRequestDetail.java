package com.pennant.pff.model.json;

import java.io.Serializable;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class JsonRequestDetail implements Serializable {
	private static final long serialVersionUID = 4396639202256693271L;

	private String name;
	private String reference = "";
	private String serviceName = "";
	private String serviceUrl = "";
	private String serviceEndpointUrl;
	private String requestedString;
	private String authentcationToken;
	private MediaType contentType;
	private HttpMethod method;
	private Map<String, String> headerMap;
	private String fileName;
	private String password;

	public JsonRequestDetail() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getServiceEndpointUrl() {
		return serviceEndpointUrl;
	}

	public void setServiceEndpointUrl(String serviceEndpointUrl) {
		this.serviceEndpointUrl = serviceEndpointUrl;
	}

	public String getRequestedString() {
		return requestedString;
	}

	public void setRequestedString(String requestedString) {
		this.requestedString = requestedString;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public void setContentType(MediaType contentType) {
		this.contentType = contentType;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
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

	public String getAuthentcationToken() {
		return authentcationToken;
	}

	public void setAuthentcationToken(String authentcationToken) {
		this.authentcationToken = authentcationToken;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
