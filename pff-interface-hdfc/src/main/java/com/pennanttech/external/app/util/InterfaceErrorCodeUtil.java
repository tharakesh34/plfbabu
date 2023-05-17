package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;

public class InterfaceErrorCodeUtil {

	private static ExtGenericDao extGenericDao;
	public static InterfaceErrorCodeUtil errorCodes;
	private static List<InterfaceErrorCode> interfaceErrorsList = new ArrayList<InterfaceErrorCode>();

	public InterfaceErrorCodeUtil() {
		super();

		if (errorCodes == null) {
			errorCodes = new InterfaceErrorCodeUtil();
			interfaceErrorsList = getExtGenericDao().fetchInterfaceErrorCodes();

		}
	}

	public List<InterfaceErrorCode> getInterfaceErrorsList() {
		return interfaceErrorsList;
	}

	public static ExtGenericDao getExtGenericDao() {
		return InterfaceErrorCodeUtil.extGenericDao;
	}

	public void setExtGenericDao(ExtGenericDao extGenericDao) {
		InterfaceErrorCodeUtil.extGenericDao = extGenericDao;
	}

	public static InterfaceErrorCodeUtil getInstance() {
		return errorCodes;
	}

}
