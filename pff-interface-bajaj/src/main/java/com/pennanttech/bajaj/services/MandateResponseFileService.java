package com.pennanttech.bajaj.services;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.file.service.FileService;
import com.pennanttech.pff.core.services.MandateResponseService;

public class MandateResponseFileService extends BajajService implements FileService {
	private static final Logger logger = Logger.getLogger(MandateResponseFileService.class);

	public MandateResponseFileService() {
		super();
	}
	
	@Autowired
	private MandateResponseService	mandateResponse;
	
	@Override
	public void processFile(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);
		long userId = (Long)params[0];
		DataEngineStatus status = (DataEngineStatus)params[1];
		File file = (File)params[2];
		Media media = (Media)params[3];
		boolean auto = (Boolean)params[4];
		
		String configName = status.getName();

		if (!auto && BajajInterfaceConstants.autoMandateResFileJob) {
			throw new Exception("Auto Mandate Reponse file [ " + status.getFileName() + " ] is inprogress please wait..");
		}

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Mandate response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(getValueDate());
		dataEngine.importData(configName);
		
		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				mandateResponse.receiveResponse(status.getId());
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));
		
		
		logger.debug(Literal.LEAVING);
	}

}
