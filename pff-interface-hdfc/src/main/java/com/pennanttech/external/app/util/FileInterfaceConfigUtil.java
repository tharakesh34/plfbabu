package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;

public class FileInterfaceConfigUtil {

	private static List<FileInterfaceConfig> fileInterfaceConfigList = new ArrayList<FileInterfaceConfig>();

	public FileInterfaceConfigUtil(ExtGenericDao extGenericDao) {
		fileInterfaceConfigList = extGenericDao.getExternalConfig();
	}

	public List<FileInterfaceConfig> getFileInterfaceConfigList() {
		return fileInterfaceConfigList;
	}

	public static FileInterfaceConfig getFIConfig(String key) {
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

}
