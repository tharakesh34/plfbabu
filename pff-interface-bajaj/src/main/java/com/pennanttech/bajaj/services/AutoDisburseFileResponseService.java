package com.pennanttech.bajaj.services;

import java.io.File;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.disbursement.DisbursementResponse;

public class AutoDisburseFileResponseService extends BajajService {
	private static final Logger		logger				= Logger.getLogger(AutoDisburseFileResponseService.class);

	@Autowired
	private DisbursementResponse	disbursementResponse;

	public AutoDisburseFileResponseService() {
		super();
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {

		if (BajajInterfaceConstants.autoDisbResFileJob) {
			return;
		}


		File directory = null;
		directory = new File(BajajInterfaceConstants.autoDisbFileLoaction);

		if (!directory.exists()) {
			throw new JobExecutionException("Auto disbursement response file location not available.");
		}

		BajajInterfaceConstants.autoDisbResFileJob = true;
		try {
			for (File file : directory.listFiles()) {
				if (!file.isFile()) {
					continue;
				}

				processFile(1000, "DISB_HDFC_IMPORT", file, null);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			BajajInterfaceConstants.autoDisbResFileJob = false;
		}

	}

	public DataEngineStatus processFile(long userId, String configName, File file, Media media) throws Exception {
		DataEngineStatus status = null;

		if ("DISB_HDFC_IMPORT".equals(configName)) {
			status = BajajInterfaceConstants.autoDisbResFileStatus;
		} else {
			status = BajajInterfaceConstants.manualDisbResFileStatus;
		}
		
		String name = "";
		
		if(file != null) {
			name = file.getName();
		} else if (media!= null) {
			name = media.getName();
		}

		status.reset();

		DataEngineImport dataEngine;
		logger.info("Start processing the file " + name);
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), status, null);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(getAppDate());

		dataEngine.importData(configName);

		do {

			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				disbursementResponse.receiveResponse(status.getId());
				break;
			}

		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(name + " file processing completed");

		return status;
	}
}