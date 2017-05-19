package com.pennanttech.bajaj.services;

import com.pennanttech.dbengine.process.ControlDumpRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class ControlDumpRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ControlDumpRequestProcess process = new ControlDumpRequestProcess(dataSource, getValueDate());
		process.process((Long) params[0], "CONTROL_DUMP_REQUEST");
	}
}
