package com.pennanttech.bajaj.services;

import java.util.Date;

import com.pennanttech.bajaj.process.ALMRequestProcess;
import com.pennanttech.pff.core.services.ALMRequest;

public class ALMRequestService extends BajajService implements ALMRequest {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ALMRequestProcess service = new ALMRequestProcess(dataSource, (Long) params[0], (Date)params[1], (Date)params[2]);
		service.process("ALM_REQUEST");
	}
}
