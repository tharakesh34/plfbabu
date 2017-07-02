package com.pennanttech.bajaj.services;

import java.util.Date;

import com.pennanttech.bajaj.process.ControlDumpRequestProcess;
import com.pennanttech.pff.core.services.ControlDumpRequestService;

public class ControlDumpRequestServiceImpl extends BajajService implements ControlDumpRequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ControlDumpRequestProcess process = new ControlDumpRequestProcess(dataSource, (Long) params[0],
				(Date) params[1], (Date) params[2], (Date) params[3], (Date) params[4]);
		process.process("CONTROL_DUMP_REQUEST");
	}
}
