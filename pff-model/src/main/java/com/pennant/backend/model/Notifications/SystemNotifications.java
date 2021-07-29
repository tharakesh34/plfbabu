package com.pennant.backend.model.Notifications;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class SystemNotifications extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1472467289111692722L;

	private long id;
	private String code;
	private String description;
	private String notificationType;
	private String recipientType;

	private String subject;
	private String contentLocation;
	private String contentFileName;
	private String attachmentLocation;
	private String attachmentfileNames;
	private String triggerQuery;
	private String criteriaQuery;
	private String templateName;
	private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getAttachmentLocation() {
		return attachmentLocation;
	}

	public void setAttachmentLocation(String attachmentLocation) {
		this.attachmentLocation = attachmentLocation;
	}

	public String getAttachmentfileNames() {
		return attachmentfileNames;
	}

	public void setAttachmentfileNames(String attachmentfileNames) {
		this.attachmentfileNames = attachmentfileNames;
	}

	public String getTriggerQuery() {
		return triggerQuery;
	}

	public void setTriggerQuery(String triggerQuery) {
		this.triggerQuery = triggerQuery;
	}

	public String getCriteriaQuery() {
		return criteriaQuery;
	}

	public void setCriteriaQuery(String criteriaQuery) {
		this.criteriaQuery = criteriaQuery;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
