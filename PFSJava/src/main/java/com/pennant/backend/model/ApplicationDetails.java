/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : ApplicationDetails.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ApplicationDetails implements Serializable {

	private static final long serialVersionUID = -292875688509626740L;

	private long appId = Long.MIN_VALUE;
	private String appCode = "";
	private String appDescription = "";
	private String appLink = "";
	private long LastMntBy;
	private Timestamp lastMntOn;
	private String lovValue;

	public ApplicationDetails() {
	    super();
	}

	public ApplicationDetails(long appId) {
		super();
		this.appId = appId;
	}

	public ApplicationDetails(long appId, String appCode, String appDescription, String appLink) {
		this.appId = appId;
		this.appCode = appCode;
		this.appDescription = appDescription;
		this.appLink = appLink;
	}

	public long getId() {
		return this.appId;
	}

	public void setId(long id) {
		this.appId = id;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppDescription() {
		return appDescription;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}

	public String getAppLink() {
		return appLink;
	}

	public void setAppLink(String appLink) {
		this.appLink = appLink;
	}

	public long getLastMntBy() {
		return LastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		LastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

}
