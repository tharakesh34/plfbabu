package com.pennanttech.bajaj.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.file.service.FileService;
import com.pennanttech.pff.core.services.DisbursementResponseService;

public class DisbursementResponseFileService extends BajajService implements FileService{
	private static final Logger		logger				= Logger.getLogger(DisbursementResponseFileService.class);

	@Autowired
	private DisbursementResponseService	disbursementResponseService;

	public DisbursementResponseFileService() {
		super();
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (BajajInterfaceConstants.autoDisbResFileJob) {
			return;
		}

		File directory = null;
		directory = new File(BajajInterfaceConstants.autoDisbFileLoaction);
		
		if(directory != null) {
			logger.debug("Auto disbursement response file location [ " + directory + " ]");
		}

		if (!directory.exists()) {
			logger.warn("Auto disbursement response file location [ " + directory + " ] not available.");
		}

		try {
			if (directory.listFiles() == null) {
				return;
			}

			for (File file : directory.listFiles()) {
				if (!file.isFile()) {
					continue;
				}

				BajajInterfaceConstants.autoDisbResFileJob = true;
				processFile(new Long(1000), new DataEngineStatus("DISB_CITI_IMPORT"), file, null, true);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			BajajInterfaceConstants.autoDisbResFileJob = false;
		}

	}
	
	@Override
	public void processFile(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		boolean auto = (Boolean) params[4];

		String configName = status.getName();

		if (!auto && BajajInterfaceConstants.autoDisbResFileJob) {
			throw new Exception("Auto disbursement file [ " + status.getFileName() + " ] is inprogress please wait..");
		}

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated disbursement response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(getValueDate());
		
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("AC", "AC");
		dataEngine.setFilterMap(filterMap);
		
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				disbursementResponseService.receiveResponse(status.getId());
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(name + " file processing completed");
	}
}