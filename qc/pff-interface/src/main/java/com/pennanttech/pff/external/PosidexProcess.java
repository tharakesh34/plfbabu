package com.pennanttech.pff.external;

import com.pennanttech.dataengine.model.DataEngineStatus;

public interface PosidexProcess {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("POSIDEX_CUSTOMER_UPDATE_REQUEST");

	public void process(Object... objects);
}
