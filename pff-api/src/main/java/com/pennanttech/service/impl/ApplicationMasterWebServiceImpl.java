package com.pennanttech.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.applicationmaster.ReasonCodeResponse;
import com.pennant.backend.service.applicationmaster.ReasonCodeService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.ApplicationMasterRestService;
import com.pennanttech.pffws.ApplicationMasterSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class ApplicationMasterWebServiceImpl implements ApplicationMasterRestService, ApplicationMasterSoapService {
	private final Logger logger = Logger.getLogger(getClass());

	private ReasonCodeService reasonCodeService;

	public ApplicationMasterWebServiceImpl() {
		super();
	}

	@Override
	public ReasonCodeResponse getReasonCodeDetails(String reasonTypeCode) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ReasonCodeResponse response = new ReasonCodeResponse();
		List<ReasonCode> reasonDetails = null;

		if (StringUtils.isBlank(reasonTypeCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "reasonTypeCode";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		reasonDetails = reasonCodeService.getReasonDetails(reasonTypeCode);

		if (CollectionUtils.isEmpty(reasonDetails)) {
			String[] valueParm = new String[1];
			valueParm[0] = "reasonTypeCode";
			//no records founds
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		} else {
			response.setReasonCode(reasonDetails);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Autowired
	public void setReasonCodeService(ReasonCodeService reasonCodeService) {
		this.reasonCodeService = reasonCodeService;
	}
}
