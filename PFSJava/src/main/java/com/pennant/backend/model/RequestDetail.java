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
 * FileName    		:  RequestDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-07-2012    														*
 *                                                                  						*
 * Modified Date    :  24-07-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-07-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model;

import java.util.Date;

import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * @author PENNANT TECHNOLOGIES
 * 
 */
public class RequestDetail  implements java.io.Serializable {
	private static final long serialVersionUID = -9104695593398442017L;
	private String entityCode;
	private long id = Long.MIN_VALUE;
	private String channelCode;
	private long channelId;
	private String requestIP;
	private long userId;
	private String loginName;
	private String loginPassword;
	private String serviceName;
	private String messageId;
	private Date messageSentOn;
	private Date messageReceivedOn;
	private String requestMessage;
	private Date messageResponsedOn;
	private String responseMessage;
	private String returnCode;
	private String returnDescription;
	private LoggedInUser userDetails;

	public RequestDetail(String entityCode) {
		super();
		this.entityCode = entityCode;
	}

	public RequestDetail() {
		super();
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public void setRequsetId(long id) {
		this.id = id;
	}
	
	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public long getChannelId() {
		return channelId == Long.MIN_VALUE ? 9999 : channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}
	
	public String getRequestIP() {
		return requestIP;
	}

	public void setRequestIP(String requestIP) {
		this.requestIP = requestIP;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public Date getMessageSentOn() {
		return messageSentOn;
	}

	public void setMessageSentOn(Date messageSentOn) {
		this.messageSentOn = messageSentOn;
	}

	public Date getMessageReceivedOn() {
		return messageReceivedOn;
	}

	public void setMessageReceivedOn(Date messageReceivedOn) {
		this.messageReceivedOn = messageReceivedOn;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public Date getMessageResponsedOn() {
		return messageResponsedOn;
	}

	public void setMessageResponsedOn(Date messageResponsedOn) {
		this.messageResponsedOn = messageResponsedOn;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnDescription() {
		return returnDescription;
	}

	public void setReturnDescription(String returnDescription) {
		this.returnDescription = returnDescription;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
