package com.pennanttech.external.app.util;

import java.util.List;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.FileTransferConfig;

public class FileTransferConfigUtil {

	private static List<FileTransferConfig> fileTrasferConfigList = null;

	private static ExtGenericDao extGenericDao;

	public FileTransferConfigUtil() {
		super();
	}

	public List<FileTransferConfig> getFileInterfaceConfigList() {
		return fileTrasferConfigList;
	}

	public static FileTransferConfig getFIConfig(String key) {

		if (fileTrasferConfigList == null) {
			fileTrasferConfigList = extGenericDao.getFileTransferConfig();
		}
		if (fileTrasferConfigList == null) {
			return null;
		}

		for (FileTransferConfig transferConfig : fileTrasferConfigList) {
			if (transferConfig.getFicName().equals(key)) {
				return transferConfig;
			}
		}
		return null;
	}

	public void setExtGenericDao(ExtGenericDao extGenericDao) {
		FileTransferConfigUtil.extGenericDao = extGenericDao;
	}
}
