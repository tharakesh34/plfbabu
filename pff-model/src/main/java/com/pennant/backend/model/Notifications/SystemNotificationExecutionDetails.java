package com.pennant.backend.model.Notifications;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class SystemNotificationExecutionDetails extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -1472467289111692722L;

	private long id;
	private long executionId;
	private long notificationId;
	private boolean processingFlag;
	private String email;
	private String mobileNumber;
	private byte[] notificationData;
	private String attributes;

	private String notificationType;
	private String contentLocation;
	private String contentFileName;
	private String subject;
	private String notificationCode;
	private String keyReference;
	private String templateCode;
	private String attachmentLocation;
	private String attachmentFileNames;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(long executionId) {
		this.executionId = executionId;
	}

	public boolean isProcessingFlag() {
		return processingFlag;
	}

	public void setProcessingFlag(boolean processingFlag) {
		this.processingFlag = processingFlag;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getNotificationData() {
		return notificationData;
	}

	public void setNotificationData(byte[] notificationData) {
		this.notificationData = notificationData;
	}

	public long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(long notificationId) {
		this.notificationId = notificationId;
	}

	public String getContentLocation() {
		return contentLocation;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	public String getContentFileName() {
		return contentFileName;
	}

	public void setContentFileName(String contentFileName) {
		this.contentFileName = contentFileName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNotificationCode() {
		return notificationCode;
	}

	public void setNotificationCode(String notificationCode) {
		this.notificationCode = notificationCode;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getAttachmentLocation() {
		return attachmentLocation;
	}

	public void setAttachmentLocation(String attachmentLocation) {
		this.attachmentLocation = attachmentLocation;
	}

	public String getAttachmentFileNames() {
		return attachmentFileNames;
	}

	public void setAttachmentFileNames(String attachmentFileNames) {
		this.attachmentFileNames = attachmentFileNames;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

}
