package com.pennanttech.external.app.config.dao;

import java.util.List;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.config.model.FileTransferConfig;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;

public interface ExtGenericDao {

	public List<InterfaceErrorCode> fetchInterfaceErrorCodes();

	public void resetAllSequences();

	List<FileInterfaceConfig> getExternalConfig();

	List<FileTransferConfig> getFileTransferConfig();
}
