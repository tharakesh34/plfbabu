package com.pennanttech.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.transactionmapping.TransactionMappingDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.TransactionMappingRestService;
import com.pennanttech.pffws.TransactionMappingSoapervice;
import com.pennanttech.ws.model.transactionMapping.TransactionMappingRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class TransactionMappingWebServiceImpl extends ExtendedTestClass
		implements TransactionMappingRestService, TransactionMappingSoapervice {

	private static final Logger logger = LogManager.getLogger(TransactionMappingWebServiceImpl.class);

	private TransactionMappingDAO transactionMappingDAO;

	/**
	 * Method for authentication in PLF system.
	 * 
	 * @param transactionMappingRequest
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus doAuthentication(TransactionMappingRequest transactionMappingRequest)
			throws ServiceException {
		WSReturnStatus response = null;

		logger.debug(Literal.ENTERING);
		response = validateTransactionMappingRequest(transactionMappingRequest);
		if (response != null) {
			return response;
		}

		int count = 0;
		count = transactionMappingDAO.getCountByPhoneAndStroeId(transactionMappingRequest.getMobileNumber(),
				transactionMappingRequest.getStoreId(), transactionMappingRequest.getPosId());
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "StroeId";
			valueParm[1] = "POSID AND Mobile Number Combination";
			return APIErrorHandlerService.getFailedStatus("41002", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();

	}

	@Override
	public WSReturnStatus doMobileAuthentication(TransactionMappingRequest transactionMappingRequest)
			throws ServiceException {
		logger.debug(Literal.ENTERING);
		if (StringUtils.isBlank(transactionMappingRequest.getMobileNumber())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Mobile Number";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		int count = transactionMappingDAO.getCountByPhoneNumber(transactionMappingRequest.getMobileNumber());
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Mobile Number ";
			return APIErrorHandlerService.getFailedStatus("90266", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();

	}

	private WSReturnStatus validateTransactionMappingRequest(TransactionMappingRequest transactionMappingRequest) {
		logger.debug(Literal.ENTERING);
		if (StringUtils.isBlank(transactionMappingRequest.getMobileNumber())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Mobile Number";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (transactionMappingRequest.getPosId() <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "POSID";
			valueParm[1] = "Zero";
			return APIErrorHandlerService.getFailedStatus("91121", valueParm);
		}
		if (transactionMappingRequest.getStoreId() <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "StoreId";
			valueParm[1] = "Zero";
			return APIErrorHandlerService.getFailedStatus("91121", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Autowired
	public void setTransactionMappingDAO(TransactionMappingDAO transactionMappingDAO) {
		this.transactionMappingDAO = transactionMappingDAO;
	}

}
