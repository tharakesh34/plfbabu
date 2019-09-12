package com.pennant.backend.service.filedownload.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.service.filedownload.LimitDownloadService;
import com.pennanttech.pff.external.GLEMSCustomerLimitProcess;

public class LimitDownloadServiceImpl implements LimitDownloadService {
	@Autowired(required = false)
	private GLEMSCustomerLimitProcess gLEMSCustomerLimitProcess;

	@Override
	public boolean processDownload(List<Long> limitHeaderIds) throws Exception {
		if (gLEMSCustomerLimitProcess != null) {
			return gLEMSCustomerLimitProcess.processDownload(limitHeaderIds);
		}

		return false;
	}

	@Override
	public String getFileName() {
		try {
			if (gLEMSCustomerLimitProcess != null) {
				return gLEMSCustomerLimitProcess.getFilePath();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
