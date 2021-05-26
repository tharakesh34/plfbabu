package com.pennanttech.pff.external;

import java.io.File;

import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.model.DataEngineStatus;

public interface DisbursementResponse {
	public void processAutoResponseFiles();

	public void receiveResponse(DataEngineStatus dEStatus);

	public void processResponseFile(long userId, DataEngineStatus status, File file, Media media);
}
