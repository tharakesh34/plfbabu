package com.pennanttech.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.pennant.app.util.DateUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.SystemDateRestService;
import com.pennanttech.pffws.SystemDateSoapService;
import com.pennanttech.ws.model.systemDate.SystemDate;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class SystemDateWebServiceImpl implements SystemDateRestService, SystemDateSoapService {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public SystemDate getSystemDate() throws ServiceException {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		APIErrorHandlerService.logReference("SystemDate");
		SystemDate systemDate = new SystemDate();
		try {
			systemDate.setAppDate(DateUtility.getAppDate());
			systemDate.setValueDate(DateUtility.getAppValueDate());
			systemDate.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			systemDate.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);
		return systemDate;
	}
}
