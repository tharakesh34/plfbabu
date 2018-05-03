package com.pennanttech.ws.log.model;

import java.sql.Timestamp;

public class APILogDetail {

	private long		seqId	= Long.MIN_VALUE;
	private int			restClientId;
	private String		reference;
	private String		keyFields;
	private String		serviceName;
	private String		endPoint;
	private String		method;
	private String		authKey;
	private String		clientIP;
	private String		request;
	private String		response;
	private Timestamp	receivedOn;
	private Timestamp	responseGiven;
	private String		statusCode;
	private String		error;
	private String		messageId;
	private String		entityId;
	private String		language;
	private int		serviceVersion;
	private Timestamp	headerReqTime;
	private boolean		processed;

	public long getSeqId() {
		return seqId;
	}

	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}

	public int getRestClientId() {
		return restClientId;
	}

	public void setRestClientId(int restClientId) {
		this.restClientId = restClientId;
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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
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

	public Timestamp getReceivedOn() {
		return receivedOn;
	}

	public void setReceivedOn(Timestamp receivedOn) {
		this.receivedOn = receivedOn;
	}

	public Timestamp getResponseGiven() {
		return responseGiven;
	}

	public void setResponseGiven(Timestamp responseGiven) {
		this.responseGiven = responseGiven;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getLanguage() {
		return language;
	}

	public String getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(String keyFields) {
		this.keyFields = keyFields;
	}

	public int getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(int serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public Timestamp getHeaderReqTime() {
		return headerReqTime;
	}

	public void setHeaderReqTime(Timestamp headerReqTime) {
		this.headerReqTime = headerReqTime;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
