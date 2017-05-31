package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.PosidexRequestProcess;
import com.pennanttech.pff.core.services.PosidexRequest;

public class PosidexRequestService extends BajajService implements PosidexRequest {
	@Override
	public void sendReqest(Object... params) throws Exception {
		PosidexRequestProcess service = new PosidexRequestProcess(dataSource, (Long) params[0],getAppDate(), getValueDate(), true);
		service.process("POSIDEX_CUSTOMER_UPDATE_REQUEST");
	}
}
