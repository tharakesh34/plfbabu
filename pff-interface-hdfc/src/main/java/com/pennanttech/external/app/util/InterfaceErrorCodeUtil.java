package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;

import com.pennanttech.external.app.config.model.InterfaceErrorCode;

public class InterfaceErrorCodeUtil {

	private InterfaceErrorCodeUtil() {
		super();
	}

	private List<InterfaceErrorCode> interfaceErrorsList = new ArrayList<InterfaceErrorCode>();

	public static InterfaceErrorCodeUtil errorCodes;

	public static InterfaceErrorCodeUtil getInstance() {
		if (errorCodes == null) {
			errorCodes = new InterfaceErrorCodeUtil();
		}
		return errorCodes;
	}

	public List<InterfaceErrorCode> getInterfaceErrorsList() {
		return interfaceErrorsList;
	}

	public void setInterfaceErrorsList(List<InterfaceErrorCode> interfaceErrorsList) {
		this.interfaceErrorsList = interfaceErrorsList;
	}

}
