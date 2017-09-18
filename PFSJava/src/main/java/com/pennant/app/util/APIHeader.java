package com.pennant.app.util;

import java.util.Date;
import java.util.Map;

public class APIHeader {

	// Private Variables
	private String serviceName;
    private String serviceVersion;
    private String entityId;
    private String ipAddress;
    private String channelId;
    private String userId;
    private String language;
    private String securityInfo;
    private String messageId;
    private Date requestTime;
    private Map<String, String> additionalInfo;
    private String returnCode;
    private String returnDesc;
    private Date responseTime;
    
    // Public Constants
    public static final String API_HEADER_KEY = "HeaderKey";
	public static final String API_AUTHORIZATION = "Authorization";
	public static final String API_SERVICENAME = "ServiceName";
	public static final String API_SERVICEVERSION = "ServiceVersion";
	public static final String API_ENTITYID = "EntityId";
	public static final String API_MESSAGEID = "MessageId";
	public static final String API_LANGUAGE = "Language";
	public static final String API_REQ_TIME = "RequestTime";
	public static final String API_RES_TIME = "ResponseTime";
	public static final String API_RETURNCODE = "ReturnCode";
	public static final String API_RETURNDESC = "ReturnText";
	public static final String API_LOG_KEY = "LogDetails";
	public static final String API_EXCEPTION_KEY = "Exception";


	/***** Setters / Getters ******/
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSecurityInfo() {
		return securityInfo;
	}

	public void setSecurityInfo(String securityInfo) {
		this.securityInfo = securityInfo;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getReturnDesc() {
		return returnDesc;
	}

	public void setReturnDesc(String returnDesc) {
		this.returnDesc = returnDesc;
	}

	public Date getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String toString() {
		return "APIHeader [serviceName=" + serviceName + ", serviceVersion=" + serviceVersion + ", entityId="
				+ entityId + ", ipAddress=" + ipAddress + ", channelId=" + channelId + ", userId=" + userId
				+ ", language=" + language + ", securityInfo=" + securityInfo + ", messageId=" + messageId
				+ ", requestTime=" + requestTime + ", additionalInfo=" + additionalInfo + ", returnCode=" + returnCode
				+ ", returnDesc=" + returnDesc + ", responseTime=" + responseTime + "]";
	}
	
}
