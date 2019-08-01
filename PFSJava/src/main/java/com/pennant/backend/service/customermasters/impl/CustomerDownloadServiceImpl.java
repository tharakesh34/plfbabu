package com.pennant.backend.service.customermasters.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.service.customermasters.CustomerDownloadService;
import com.pennanttech.pff.external.GLEMSCustomerProcess;

public class CustomerDownloadServiceImpl implements CustomerDownloadService {

	@Autowired(required = false)
	private GLEMSCustomerProcess gLEMSCustomerProcess;

	@Override
	public boolean processDownload(List<Long> custIds) throws Exception {
		if (gLEMSCustomerProcess != null) {
			return gLEMSCustomerProcess.processDownload(custIds);
		}
		return false;
	}

}
