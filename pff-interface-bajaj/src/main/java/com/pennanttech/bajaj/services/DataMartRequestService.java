package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.DataMartRequestProcess;
import com.pennanttech.pff.core.services.DataMartRequest;

public class DataMartRequestService extends BajajService implements DataMartRequest {

	@Override
	public void sendReqest(Object... params) throws Exception {
		DataMartRequestProcess requestProcess = new DataMartRequestProcess(dataSource, (Long) params[0], getValueDate());
		requestProcess.process("DATA_MART_REQUEST");
	}
}
