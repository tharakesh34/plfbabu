package com.pennanttech.bajaj.services;

import java.util.Date;

import com.pennanttech.bajaj.process.PosidexRequestProcess;
import com.pennanttech.pff.core.services.PosidexRequestService;

public class PosidexRequestServiceImpl extends BajajService implements PosidexRequestService {
	@Override
	public void sendReqest(Object... params) throws Exception {
		PosidexRequestProcess service = new PosidexRequestProcess(dataSource, (Long) params[0], (Date)params[1], (Date)params[2]);
		service.process("POSIDEX_CUSTOMER_UPDATE_REQUEST");
	}
}
