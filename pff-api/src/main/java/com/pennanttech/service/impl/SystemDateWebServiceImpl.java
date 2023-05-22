package com.pennanttech.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.pennant.app.util.SysParamUtil;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.SystemDateRestService;
import com.pennanttech.pffws.SystemDateSoapService;
import com.pennanttech.ws.model.systemDate.SystemDate;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class SystemDateWebServiceImpl extends ExtendedTestClass
		implements SystemDateRestService, SystemDateSoapService {
	private final Logger logger = LogManager.getLogger(getClass());

	@Override
	public SystemDate getSystemDate() throws ServiceException {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		APIErrorHandlerService.logReference("SystemDate");
		SystemDate systemDate = new SystemDate();
		try {
			systemDate.setAppDate(SysParamUtil.getAppDate());
			systemDate.setValueDate(SysParamUtil.getAppValueDate());
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
