package com.pennanttech.pff.external;

import com.pennanttech.dataengine.model.DataEngineStatus;

public interface DataMartProcess {
	public static final DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("DATA_MART_REQUEST");

	public void process();
}
