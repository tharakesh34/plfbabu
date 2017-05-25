package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.ALMRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class ALMRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ALMRequestProcess service = new ALMRequestProcess(dataSource, (Long) params[0], getValueDate(), getAppDate());
		service.process("ALM_REQUEST");
	}
}
