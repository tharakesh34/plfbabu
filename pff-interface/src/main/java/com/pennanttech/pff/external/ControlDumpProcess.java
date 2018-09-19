package com.pennanttech.pff.external;

import com.pennanttech.dataengine.model.DataEngineStatus;

public interface ControlDumpProcess {
	public static final DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("CONTROL_DUMP_REQUEST");

	public void process();
}
