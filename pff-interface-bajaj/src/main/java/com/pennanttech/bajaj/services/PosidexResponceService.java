package com.pennanttech.bajaj.services;

import com.pennanttech.dbengine.process.PosidexResponseProcess;
import com.pennanttech.pff.core.services.RequestService;

public class PosidexResponceService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		PosidexResponseProcess service = new PosidexResponseProcess(dataSource, getValueDate());
		service.process((Long) params[0], "POSIDEX_CUSTOMER_UPDATEREQUEST");
	}
}
