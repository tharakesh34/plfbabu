package com.pennanttech.external.dao;

import java.util.List;

import com.pennanttech.external.config.model.FileInterfaceConfig;
import com.pennanttech.external.config.model.InterfaceErrorCode;

public interface ExtGenericDao {

	public List<InterfaceErrorCode> fetchInterfaceErrorCodes();

	public void resetAllSequences();

	List<FileInterfaceConfig> getExternalConfig();
}
