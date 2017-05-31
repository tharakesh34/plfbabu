package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.PosidexRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class PosidexRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		PosidexRequestProcess service = new PosidexRequestProcess(dataSource, (Long) params[0],getAppDate(), getValueDate(), true);
		service.process("POSIDEX_UPDATE_EODREQUEST");
	}
}
