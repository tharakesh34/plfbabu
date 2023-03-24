package com.pennanttech.external.config;

import java.util.ArrayList;
import java.util.List;

import com.pennanttech.external.mandate.errors.ExtMandateError;

public class ExtErrorCodes {

	private ExtErrorCodes() {

	}

	private List<InterfaceErrorCode> interfaceErrorsList = new ArrayList<InterfaceErrorCode>();
	private List<ExtMandateError> extMandateErrorsList = new ArrayList<ExtMandateError>();

	public static ExtErrorCodes errorCodes;

	public static ExtErrorCodes getInstance() {
		if (errorCodes == null) {
			errorCodes = new ExtErrorCodes();
		}
		return errorCodes;
	}

	public List<InterfaceErrorCode> getInterfaceErrorsList() {
		return interfaceErrorsList;
	}

	public void setInterfaceErrorsList(List<InterfaceErrorCode> interfaceErrorsList) {
		this.interfaceErrorsList = interfaceErrorsList;
	}

	public List<ExtMandateError> getExtMandateErrorsList() {
		return extMandateErrorsList;
	}

	public void setExtMandateErrorsList(List<ExtMandateError> extMandateErrorsList) {
		this.extMandateErrorsList = extMandateErrorsList;
	}

}
