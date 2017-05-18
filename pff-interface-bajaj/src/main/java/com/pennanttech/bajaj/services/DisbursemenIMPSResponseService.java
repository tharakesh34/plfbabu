package com.pennanttech.bajaj.services;

import com.pennanttech.dbengine.process.DisbursemenIMPSResponseProcess;
import com.pennanttech.pff.core.services.RequestService;

public class DisbursemenIMPSResponseService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		DisbursemenIMPSResponseProcess service = new DisbursemenIMPSResponseProcess(dataSource, (String) params[0], getAppDate());
		service.process((Long) params[1], "ALM_REQUEST");
	}

}
