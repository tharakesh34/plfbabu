package com.pennanttech.bajaj.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDescription {

	private String errorCode;
private String errorDescription;
private String dateProcessed;
private String timeProcessed;

public String getErrorCode() {
	return errorCode;
}
public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
}
public String getErrorDescription() {
	return errorDescription;
}
public void setErrorDescription(String errorDescription) {
	this.errorDescription = errorDescription;
}
public String getDateProcessed() {
	return dateProcessed;
}
public void setDateProcessed(String dateProcessed) {
	this.dateProcessed = dateProcessed;
}
public String getTimeProcessed() {
	return timeProcessed;
}
public void setTimeProcessed(String timeProcessed) {
	this.timeProcessed = timeProcessed;
}
@Override
public String toString() {
	return "ErrorDescription [errorCode=" + errorCode + ", errorDescription="
			+ errorDescription + ", dateProcessed=" + dateProcessed
			+ ", timeProcessed=" + timeProcessed + "]";
}


}
