package com.pennanttech.bajaj.services;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.process.PosidexResponseProcess;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.services.PosidexResponseService;

public class PosidexResponseServiceImpl extends BajajService implements PosidexResponseService {
	private static final Logger logger = Logger.getLogger(PosidexResponseServiceImpl.class);

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
