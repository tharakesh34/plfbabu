package com.pennanttech.bajaj.services;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.process.PosidexResponseProcess;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.PosidexResponse;

public class PosidexResponseService extends BajajService implements PosidexResponse {
	private static final Logger	logger	= Logger.getLogger(PosidexResponseService.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			receiveResponse(new Long(1000));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		PosidexResponseProcess service = new PosidexResponseProcess(dataSource, new Long(1000), getValueDate());
		service.process("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
	}
}
