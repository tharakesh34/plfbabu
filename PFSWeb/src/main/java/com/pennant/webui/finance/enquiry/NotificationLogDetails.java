package com.pennant.webui.finance.enquiry;



public class NotificationLogDetails implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String finReference;
	private String emailTo;
	private String emailSubject;
	private String emailSubModule;
	private String emailStage;
	private String emailNotificationCode;
	private String smsTo="SivaRam";
	private String smsMessage;
	private String smsSubModule;
	private String smsStage;
	private String smsNotificationCode;
	
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailSubModule() {
		return emailSubModule;
	}
	public void setEmailSubModule(String emailSubModule) {
		this.emailSubModule = emailSubModule;
	}
	public String getEmailStage() {
		return emailStage;
	}
	public void setEmailStage(String emailStage) {
		this.emailStage = emailStage;
	}
	public String getEmailNotificationCode() {
		return emailNotificationCode;
	}
	public void setEmailNotificationCode(String emailNotificationCode) {
		this.emailNotificationCode = emailNotificationCode;
	}
	public String getSmsTo() {
		return smsTo;
	}
	public void setSmsTo(String smsTo) {
		this.smsTo = smsTo;
	}
	public String getSmsMessage() {
		return smsMessage;
	}
	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}
	public String getSmsSubModule() {
		return smsSubModule;
	}
	public void setSmsSubModule(String smsSubModule) {
		this.smsSubModule = smsSubModule;
	}
	public String getSmsStage() {
		return smsStage;
	}
	public void setSmsStage(String smsStage) {
		this.smsStage = smsStage;
	}
	public String getSmsNotificationCode() {
		return smsNotificationCode;
	}
	public void setSmsNotificationCode(String smsNotificationCode) {
		this.smsNotificationCode = smsNotificationCode;
	}
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	}
