package com.pennanttech.bajaj.services;

import java.io.File;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.baja.InterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.services.disbursement.DisbursementResponse;

public class AutoDisburseFileResponseService extends BajajService {
	private static final Logger		logger				= Logger.getLogger(AutoDisburseFileResponseService.class);

	@Autowired
	private DisbursementResponse	disbursementResponse;

	public AutoDisburseFileResponseService() {
		super();
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {

		if (InterfaceConstants.autoDisbResFileJob) {
			return;
		}

		String responseFileLocation = null;
		try {
			responseFileLocation = (String) getSMTParameter("AUTO_DISB_RES_FILE_LOCATION", String.class);
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage());
		}

		File directory = null;
		if (responseFileLocation != null) {
			directory = new File(responseFileLocation);

			if (!directory.exists()) {
				throw new JobExecutionException("Auto disbursement response file location not avilabe.");
			}
		}

		InterfaceConstants.autoDisbResFileJob = true;
		try {
			for (File file : directory.listFiles()) {
				if (!file.isFile()) {
					continue;
				}

				processFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			InterfaceConstants.autoDisbResFileJob = false;
		}

	}

	private void processFile(File file) throws Exception {
		DataEngineStatus status = InterfaceConstants.autoDisbResFileStatus;
		status.reset();
		DataEngineImport dataEngine;
		logger.info("Start processing the file " + file.getName());
		dataEngine = new DataEngineImport(dataSource, 1000, App.DATABASE.name(), status, null);
		dataEngine.setFile(file);
		dataEngine.setValueDate(getAppDate());

		dataEngine.importData("DISB_HDFC_IMPORT");

		do {

			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				disbursementResponse.receiveResponse(status.getId());
				break;
			}

		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(file.getName() + " file processing completed");
	}
}