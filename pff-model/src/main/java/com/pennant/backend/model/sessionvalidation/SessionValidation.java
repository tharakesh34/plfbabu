package com.pennant.backend.model.sessionvalidation;

import java.sql.Timestamp;

public class SessionValidation {

	private String entityCode = "";
	private long agentId;
	private String userToken;
	private Timestamp userTokenExpiry;
	private String registrationId;

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public Timestamp getUserTokenExpiry() {
		return userTokenExpiry;
	}

	public void setUserTokenExpiry(Timestamp userTokenExpiry) {
		this.userTokenExpiry = userTokenExpiry;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

}
