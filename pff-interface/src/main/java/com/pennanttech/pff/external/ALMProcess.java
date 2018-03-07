package com.pennanttech.pff.external;

import com.pennanttech.dataengine.model.DataEngineStatus;

public interface ALMProcess {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("ALM_REQUEST");

	public void process() throws Exception;
}
