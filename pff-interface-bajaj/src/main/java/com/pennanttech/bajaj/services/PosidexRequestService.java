package com.pennanttech.bajaj.services;

import java.util.Date;

import com.pennanttech.dbengine.process.PosidexRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class PosidexRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		PosidexRequestProcess service = new PosidexRequestProcess(dataSource, (Date) params[1], (Date) params[2], getAppDate());
		service.process((Long) params[0], "POSIDEX_UPDATE_EODREQUEST");
	}
}
