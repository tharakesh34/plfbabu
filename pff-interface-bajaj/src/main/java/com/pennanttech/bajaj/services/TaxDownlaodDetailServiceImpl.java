package com.pennanttech.bajaj.services;

import java.util.Date;

import com.pennanttech.bajaj.process.TaxDownlaodDetailProcess;
import com.pennanttech.pff.core.taxdownload.TaxDownlaodDetailService;

public class TaxDownlaodDetailServiceImpl extends BajajService implements TaxDownlaodDetailService {
	@Override
	public void sendReqest(Object... params) throws Exception {
		TaxDownlaodDetailProcess service = new TaxDownlaodDetailProcess(dataSource, (Long) params[0], (Date)params[1], (Date)params[2], (Date)params[3]);
		service.process("GST_TAXDOWNLOAD_DETAILS");
	}
}
