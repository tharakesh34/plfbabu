/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AuthorizationDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2012    														*
 *                                                                  						*
 * Modified Date    :  21-06-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2012       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.model.masters;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>AuthorizationDetail table</b>.<br>
 * 
 */
public class AuthorizationDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long uPPAuthId = Long.MIN_VALUE;
	private long authChannelId;
	private String ChannelName;
	private String ChannelCode;
	private String authChannelIP;
	private boolean status;
	private boolean newRecord = false;
	private String lovValue;
	private AuthorizationDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public AuthorizationDetail() {
		super();
	}

	public AuthorizationDetail(long id) {
		super();
		this.setId(id);
	}

	// Getter and Setter methods

	public long getId() {
		return uPPAuthId;
	}

	public void setId(long id) {
		this.uPPAuthId = id;
	}

	public long getUPPAuthId() {
		return uPPAuthId;
	}

	public void setUPPAuthId(long uPPAuthId) {
		this.uPPAuthId = uPPAuthId;
	}

	public long getAuthChannelId() {
		return authChannelId;
	}

	public void setAuthChannelId(long authChannelId) {
		this.authChannelId = authChannelId;
	}

	public String getChannelName() {
		return ChannelName;
	}

	public void setChannelName(String channelName) {
		ChannelName = channelName;
	}

	public String getChannelCode() {
		return ChannelCode;
	}

	public void setChannelCode(String channelCode) {
		ChannelCode = channelCode;
	}

	public String getAuthChannelIP() {
		return authChannelIP;
	}

	public void setAuthChannelIP(String authChannelIP) {
		this.authChannelIP = authChannelIP;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public AuthorizationDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AuthorizationDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
