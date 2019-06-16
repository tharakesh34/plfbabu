package com.pennanttech.pff.external;

import java.io.File;

import org.zkoss.util.media.Media;

import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.dataengine.model.DataEngineStatus;

public interface MandateProcesses {
	public static DataEngineStatus MANDATES_IMPORT = new DataEngineStatus("MANDATES_IMPORT");
	public static DataEngineStatus MANDATES_ACK = new DataEngineStatus("MANDATES_ACK");

	public void sendReqest(Object... object) throws Exception;

	public boolean registerMandate(Mandate mandate) throws Exception;

	public void updateMandateStatus() throws Exception;

	public void processResponseFile(long userId, File file, Media media, DataEngineStatus status) throws Exception;

	void receiveResponse(long respBatchId, DataEngineStatus status) throws Exception;

}
