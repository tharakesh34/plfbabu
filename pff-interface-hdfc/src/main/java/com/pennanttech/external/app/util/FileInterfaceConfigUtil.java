package com.pennanttech.external.app.util;

import java.util.List;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;

public class FileInterfaceConfigUtil {

	private static List<FileInterfaceConfig> fileInterfaceConfigList = null;
	private static ExtGenericDao extGenericDao;

	public FileInterfaceConfigUtil() {
		super();
	}

	public List<FileInterfaceConfig> getFileInterfaceConfigList() {
		return fileInterfaceConfigList;
	}

	public static FileInterfaceConfig getFIConfig(String key) {

		if (fileInterfaceConfigList == null) {
			fileInterfaceConfigList = extGenericDao.getExternalConfig();
		}
		if (fileInterfaceConfigList == null) {
			return null;
		}

		for (FileInterfaceConfig interfaceErrorCode : fileInterfaceConfigList) {
			if (interfaceErrorCode.getInterfaceName().equals(key)) {
				return interfaceErrorCode;
			}
		}
		return null;
	}

	public void setExtGenericDao(ExtGenericDao extGenericDao) {
		FileInterfaceConfigUtil.extGenericDao = extGenericDao;
	}

}
