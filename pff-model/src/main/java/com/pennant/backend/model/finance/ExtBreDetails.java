package com.pennant.backend.model.finance;

import java.sql.Timestamp;

public class ExtBreDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5260030121706927931L;
	private String finReference;
	private String request;
	private String response;
	private Timestamp lastMntOn;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

}
