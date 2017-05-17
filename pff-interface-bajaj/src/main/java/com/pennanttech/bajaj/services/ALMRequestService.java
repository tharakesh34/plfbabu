package com.pennanttech.bajaj.services;

import java.util.Date;

import com.pennanttech.dbengine.process.ALMRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class ALMRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		ALMRequestProcess service = new ALMRequestProcess(dataSource, (Date) params[0], getAppDate());
		service.process((Long) params[1], "ALM_REQUEST");
	}
}
