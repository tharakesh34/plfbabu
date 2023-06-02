package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;

public class InterfaceErrorCodeUtil {

	private static List<InterfaceErrorCode> interfaceErrorsList = new ArrayList<InterfaceErrorCode>();

	public InterfaceErrorCodeUtil(ExtGenericDao extGenericDao) {
		interfaceErrorsList = extGenericDao.fetchInterfaceErrorCodes();
	}

	public List<InterfaceErrorCode> getInterfaceErrorsList() {
		return interfaceErrorsList;
	}

	public static InterfaceErrorCode getIFErrorCode(String key) {
		if (interfaceErrorsList == null) {
			return null;
		}

		for (InterfaceErrorCode interfaceErrorCode : interfaceErrorsList) {
			if (interfaceErrorCode.getErrorCode().equals(key)) {
				return interfaceErrorCode;
			}
		}
		return null;
	}

	public static String getErrorMessage(String errorCode) {
		if ("".equals(StringUtils.stripToEmpty(errorCode))) {
			return "";
		}

		InterfaceErrorCode interfaceErrorCode = InterfaceErrorCodeUtil.getIFErrorCode(errorCode);
		if (interfaceErrorCode != null && interfaceErrorCode.getErrorMessage() != null) {
			return interfaceErrorCode.getErrorMessage();
		}
		return "";
	}

}
