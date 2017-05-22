package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.ControlDumpRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class ControlDumpRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ControlDumpRequestProcess process = new ControlDumpRequestProcess(dataSource, (Long) params[0], getValueDate());
		process.process("CONTROL_DUMP_REQUEST");
	}
}
