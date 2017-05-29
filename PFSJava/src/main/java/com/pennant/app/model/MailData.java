package com.pennant.app.model;

import java.io.Serializable;

public class MailData implements Serializable {
	
	private static final long serialVersionUID = -7796047632918822267L;

	private String id;
	private String mailName;
	private String mailTrigger;
	private String mailTo;
	private String mailSubject;
	private String mailBody;
	private String mailAttachment;
	private String mailAttachmentName;
	private String mailData;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMailName() {
		return mailName;
	}
	public void setMailName(String mailName) {
		this.mailName = mailName;
	}
	public String getMailTrigger() {
		return mailTrigger;
	}
	public void setMailTrigger(String mailTrigger) {
		this.mailTrigger = mailTrigger;
	}
	public String getMailTo() {
		return mailTo;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String getMailBody() {
		return mailBody;
	}
	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}
	public String getMailAttachment() {
		return mailAttachment;
	}
	public void setMailAttachment(String mailAttachment) {
		this.mailAttachment = mailAttachment;
	}
	public String getMailAttachmentName() {
		return mailAttachmentName;
	}
	public void setMailAttachmentName(String mailAttachmentName) {
		this.mailAttachmentName = mailAttachmentName;
	}
	public String getMailData() {
		return mailData;
	}
	public void setMailData(String mailData) {
		this.mailData = mailData;
	}
	
}
