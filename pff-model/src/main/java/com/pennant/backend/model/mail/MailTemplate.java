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
 * * FileName : MailTemplate.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2012 * * Modified Date :
 * 10-08-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>MailTemplate table</b>.<br>
 *
 */
public class MailTemplate extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -7999948592404630380L;

	private long templateId = Long.MIN_VALUE;
	private String templateFor;
	private String module;
	private String templateCode;
	private String templateDesc;
	private boolean smsTemplate;
	private String smsContent;
	private boolean emailTemplate;
	private byte[] emailContent;
	private String emailFormat;
	private String emailSendTo;
	private String emailFormatName;
	private String emailSubject;
	private int turnAroundTime;
	private boolean repeat;
	private boolean active;
	private String lovValue;
	private MailTemplate befImage;
	private LoggedInUser userDetails;
	private String event;

	private String emailMessage = "";
	private String smsMessage = "";
	private byte[] emailAttachment;
	private String emailAttachmentName;

	private List<String> emailIds = new ArrayList<>();
	private List<String> mobileNumbers = new ArrayList<>();
	private Map<String, byte[]> attchments = new HashMap<>();

	private String notificationData;

	public MailTemplate() {
		super();
	}

	public MailTemplate(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("emailMessage");
		excludeFields.add("smsMessage");
		excludeFields.add("emailAttachment");
		excludeFields.add("emailAttachmentName");
		excludeFields.add("emailIds");
		excludeFields.add("mobileNumbers");
		excludeFields.add("attchments");
		excludeFields.add("emailFormatName");
		excludeFields.add("notificationData");

		return excludeFields;
	}

	public long getId() {
		return templateId;
	}

	public void setId(long id) {
		this.templateId = id;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getTemplateFor() {
		return templateFor;
	}

	public void setTemplateFor(String templateFor) {
		this.templateFor = templateFor;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getTemplateDesc() {
		return templateDesc;
	}

	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}

	public boolean isSmsTemplate() {
		return smsTemplate;
	}

	public void setSmsTemplate(boolean smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public boolean isEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(boolean emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public byte[] getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(byte[] emailContent) {
		this.emailContent = emailContent;
	}

	public String getEmailFormat() {
		return emailFormat;
	}

	public void setEmailFormat(String emailFormat) {
		this.emailFormat = emailFormat;
	}

	public String getEmailSendTo() {
		return emailSendTo;
	}

	public void setEmailSendTo(String emailSendTo) {
		this.emailSendTo = emailSendTo;
	}

	public String getEmailFormatName() {
		return emailFormatName;
	}

	public void setEmailFormatName(String emailFormatName) {
		this.emailFormatName = emailFormatName;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public int getTurnAroundTime() {
		return turnAroundTime;
	}

	public void setTurnAroundTime(int turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public MailTemplate getBefImage() {
		return befImage;
	}

	public void setBefImage(MailTemplate befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	public byte[] getEmailAttachment() {
		return emailAttachment;
	}

	public void setEmailAttachment(byte[] emailAttachment) {
		this.emailAttachment = emailAttachment;
	}

	public String getEmailAttachmentName() {
		return emailAttachmentName;
	}

	public void setEmailAttachmentName(String emailAttachmentName) {
		this.emailAttachmentName = emailAttachmentName;
	}

	public List<String> getEmailIds() {
		return emailIds;
	}

	public void setEmailIds(List<String> emailIds) {
		this.emailIds = emailIds;
	}

	public List<String> getMobileNumbers() {
		return mobileNumbers;
	}

	public void setMobileNumbers(List<String> mobileNumbers) {
		this.mobileNumbers = mobileNumbers;
	}

	public Map<String, byte[]> getAttchments() {
		return attchments;
	}

	public void setAttchments(Map<String, byte[]> attchments) {
		this.attchments = attchments;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getNotificationData() {
		return notificationData;
	}

	public void setNotificationData(String notificationData) {
		this.notificationData = notificationData;
	}
}
