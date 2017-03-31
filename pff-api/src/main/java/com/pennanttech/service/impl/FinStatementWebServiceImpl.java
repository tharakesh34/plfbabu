package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinStatementController;
import com.pennanttech.pffws.FinStatementRestService;
import com.pennanttech.pffws.FinStatementSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinStatementWebServiceImpl implements FinStatementRestService, FinStatementSoapService {

	private final static Logger		logger	= Logger.getLogger(FinStatementWebServiceImpl.class);

	private FinStatementController	finStatementController;
	private CustomerDetailsService	customerDetailsService;
	private FinanceMainDAO			financeMainDAO;
	private FinanceMainService		financeMainService;

	/**
	 * get the FinStatement Details by the given FinReference/CustCif.
	 * 
	 * @param statementRequest
	 * @throws ServiceException
	 */
	@Override
	public FinStatementResponse getStatementOfAccount(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug("Enetring");
		// service level validations
		WSReturnStatus returnStatus = validateStatementRequest(statementRequest);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			FinStatementResponse finStatement = new FinStatementResponse();
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}
		FinStatementResponse response = finStatementController.getStatement(getFinanceReferences(statementRequest),
				APIConstants.STATEMENT_ACCOUNT);
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the FinStatement Details by the given FinReference/CustCif.
	 * 
	 * @param statementRequest
	 * @throws ServiceException
	 */
	@Override
	public FinStatementResponse getInterestCertificate(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug("Enetring");
		// service level validations
		WSReturnStatus returnStatus = validateStatementRequest(statementRequest);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			FinStatementResponse finStatement = new FinStatementResponse();
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}
		FinStatementResponse response = finStatementController.getStatement(getFinanceReferences(statementRequest),
				APIConstants.STATEMENT_INTREST_CERTIFICATE);
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the FinStatement Details by the given FinReference/CustCif.
	 * 
	 * @param statementRequest
	 * @throws ServiceException
	 */

	@Override
	public FinStatementResponse getRepaymentSchedule(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug("Enetring");
		// service level validations
		WSReturnStatus returnStatus = validateStatementRequest(statementRequest);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			FinStatementResponse finStatement = new FinStatementResponse();
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}
		FinStatementResponse response = finStatementController.getStatement(getFinanceReferences(statementRequest),
				APIConstants.STATEMENT_REPAYMENT_SCHEDULE);
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the FinStatement Details by the given FinReference/CustCif.
	 * 
	 * @param statementRequest
	 * @throws ServiceException
	 */
	@Override
	public FinStatementResponse getNOC(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug("Enetring");
		// service level validations
		FinStatementResponse finStatementResponse = new FinStatementResponse();
		if (StringUtils.isBlank(statementRequest.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		} else {
			int count = financeMainDAO.getFinanceCountById(statementRequest.getFinReference(), "", false);
			if (count > 0) {
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = statementRequest.getFinReference();
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return finStatementResponse;
			}
		}
		FinStatementResponse response = finStatementController.getStatement(getFinanceReferences(statementRequest),
				APIConstants.STATEMENT_NOC);
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the List of FinReferences Based on the CustID.
	 * 
	 * @param statementRequest
	 */
	private List<String> getFinanceReferences(FinStatementRequest statementRequest) {
		logger.debug("Enetring");
		List<String> referencesList = new ArrayList<String>();
		if (StringUtils.isNotBlank(statementRequest.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(statementRequest.getCif());
			referencesList = financeMainService.getFinReferencesByCustID(customer.getCustID(),
					statementRequest.getFinActiveStatus());
		} else {
			referencesList.add(statementRequest.getFinReference());
		}
		logger.debug("Leaving");
		return referencesList;
	}

	/**
	 * Basic Validations
	 * 
	 * @param statementRequest
	 */
	private WSReturnStatus validateStatementRequest(FinStatementRequest statementRequest) {
		logger.debug("Enetring");
		WSReturnStatus returnStatus = new WSReturnStatus();
		if (StringUtils.isNotBlank(statementRequest.getCif())) {
			if (StringUtils.isNotBlank(statementRequest.getFinReference())) {
				String[] valueParm = new String[2];
				valueParm[0] = "cif";
				valueParm[1] = "finReference";
				return returnStatus = APIErrorHandlerService.getFailedStatus("30511", valueParm);
			}
			if (StringUtils.isBlank(statementRequest.getFinActiveStatus())) {
				String[] valueParm = new String[1];
				valueParm[0] = "finActiveStatus";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			Customer customer = customerDetailsService.getCustomerByCIF(statementRequest.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = statementRequest.getCif();
				return returnStatus = APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}

			// validate FinActiveStatus
			if (StringUtils.isNotBlank(statementRequest.getFinActiveStatus())) {
				if (!(StringUtils.equals(statementRequest.getFinActiveStatus(), FinanceConstants.CLOSE_STATUS_MATURED) 
						|| StringUtils.equals(statementRequest.getFinActiveStatus(), APIConstants.CLOSE_STATUS_ACTIVE))) {
					String[] valueParm = new String[1];
					valueParm[0] = statementRequest.getFinActiveStatus();
					return returnStatus = APIErrorHandlerService.getFailedStatus("90501", valueParm);
				}
				if (StringUtils.equals(APIConstants.CLOSE_STATUS_ACTIVE, statementRequest.getFinActiveStatus())) {
					statementRequest.setFinActiveStatus(null);
				}
			}
		} else if(StringUtils.isNotBlank(statementRequest.getFinReference())) {
			int count = financeMainDAO.getFinanceCountById(statementRequest.getFinReference(), "", false);
			if (count > 0) {
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = statementRequest.getFinReference();
				return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "either finReference or cif";
			return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		logger.debug("Leaving");
		return returnStatus;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}
	@Autowired
	public void setFinStatementController(FinStatementController finStatementController) {
		this.finStatementController = finStatementController;
	}
}
