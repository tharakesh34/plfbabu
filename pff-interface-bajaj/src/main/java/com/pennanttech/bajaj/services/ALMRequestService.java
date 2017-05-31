package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.ALMRequestProcess;
import com.pennanttech.pff.core.services.ALMRequest;

public class ALMRequestService extends BajajService implements ALMRequest {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ALMRequestProcess service = new ALMRequestProcess(dataSource, (Long) params[0], getValueDate(), getAppDate(),
				true);
		service.process("ALM_REQUEST");
	}
}
