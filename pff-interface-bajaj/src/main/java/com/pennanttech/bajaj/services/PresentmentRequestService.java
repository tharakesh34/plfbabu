package com.pennanttech.bajaj.services;

import java.util.Date;
import java.util.List;

import com.pennanttech.bajaj.process.PresentmentRequestProcess;
import com.pennanttech.pff.core.services.PresentmentRequest;

public class PresentmentRequestService extends BajajService implements PresentmentRequest {

	@Override
	public void sendReqest(Object... params) throws Exception {
		PresentmentRequestProcess process = new PresentmentRequestProcess(dataSource, (Long) params[0], (Date) params[1], (List<Long>) params[2], (Long) params[3], (Boolean)params[4]);
		process.process("PRESENTMENT_REQUEST");
	}
}
