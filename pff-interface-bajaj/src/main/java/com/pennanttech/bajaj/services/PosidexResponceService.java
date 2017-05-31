package com.pennanttech.bajaj.services;

import com.pennanttech.bajaj.process.PosidexResponseProcess;
import com.pennanttech.pff.core.services.RequestService;

public class PosidexResponceService extends BajajService implements RequestService {

	@Override
	public void sendReqest(Object... params) throws Exception {
		PosidexResponseProcess service = new PosidexResponseProcess(dataSource, (Long) params[0], getValueDate(),false);
		service.process( "POSIDEX_CUSTOMER_UPDATEREQUEST");
	}
}
