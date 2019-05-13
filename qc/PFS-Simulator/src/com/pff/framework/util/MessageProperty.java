package com.pff.framework.util;

public class MessageProperty {


	private String messageFormate;
	private int serviceCode=0;
	private Class<?> processingClass;

	public MessageProperty(String messageFormate, int serviceCode,Class<?> processingClass) {
		super();
		this.messageFormate = messageFormate;
		this.serviceCode = serviceCode;
		this.processingClass = processingClass;
	}

	public String getMessageFormate() {
		return messageFormate;
	}
	public void setMessageFormate(String messageFormate) {
		this.messageFormate = messageFormate;
	}
	public int getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(int serviceCode) {
		this.serviceCode = serviceCode;
	}
	public Class<?> getProcessingClass() {
		return processingClass;
	}
	public void setProcessingClass(Class<?> processingClass) {
		this.processingClass = processingClass;
	}




}
