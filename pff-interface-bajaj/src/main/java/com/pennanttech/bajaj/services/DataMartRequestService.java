package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.DataMartRequestProcess;
import com.pennanttech.pff.core.services.RequestService;

public class DataMartRequestService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		DataMartRequestProcess requestProcess = new DataMartRequestProcess(dataSource, (Long) params[0], getValueDate(), getAppDate());
		requestProcess.importData();
	}
}
