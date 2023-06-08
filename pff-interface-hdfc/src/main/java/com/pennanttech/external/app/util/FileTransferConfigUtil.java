package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.config.model.FileTransferConfig;

public class FileTransferConfigUtil {

	private static List<FileTransferConfig> fileTrasferConfigList = new ArrayList<FileTransferConfig>();

	public FileTransferConfigUtil(ExtGenericDao extGenericDao) {
		fileTrasferConfigList = extGenericDao.getFileTransferConfig();
	}

	public List<FileTransferConfig> getFileInterfaceConfigList() {
		return fileTrasferConfigList;
	}

	public static void setTransferConfig(FileInterfaceConfig config) {
		FileTransferConfig fileTransferConfig = new FileTransferConfig();
		if (fileTrasferConfigList == null) {
			config.setFileTransferConfig(fileTransferConfig);
		}
		for (FileTransferConfig transferConfig : fileTrasferConfigList) {
			if (transferConfig.getFicName().equals(config.getFicNames())) {
				config.setFileTransferConfig(transferConfig);
			}
		}
		config.setFileTransferConfig(fileTransferConfig);
	}
}
