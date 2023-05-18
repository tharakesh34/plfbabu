package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;

public class InterfaceErrorCodeUtil {

	public static InterfaceErrorCodeUtil errorCodes;
	private static List<InterfaceErrorCode> interfaceErrorsList = new ArrayList<InterfaceErrorCode>();

	public InterfaceErrorCodeUtil(ExtGenericDao extGenericDao) {
		errorCodes = this;
		interfaceErrorsList = extGenericDao.fetchInterfaceErrorCodes();
	}

	public List<InterfaceErrorCode> getInterfaceErrorsList() {
		return interfaceErrorsList;
	}

	public static InterfaceErrorCodeUtil getInstance() {
		return errorCodes;
	}

}
