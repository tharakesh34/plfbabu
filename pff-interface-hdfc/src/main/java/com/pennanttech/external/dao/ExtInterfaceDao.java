package com.pennanttech.external.dao;

import java.util.List;

import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.mandate.errors.ExtMandateError;

public interface ExtInterfaceDao {
	public List<InterfaceErrorCode> fetchInterfaceErrorCodes();

	public String getRemarkFromCode(String code);

	public List<ExtMandateError> getExtMandateErrors();

	public void resetAllSequences();

	List<ExternalConfig> getExternalConfig();
}
