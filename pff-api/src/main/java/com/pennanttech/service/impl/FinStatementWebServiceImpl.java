package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
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

	private static final Logger		logger	= Logger.getLogger(FinStatementWebServiceImpl.class);

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
		//for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();

		FinStatementResponse finStatement = new FinStatementResponse();
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}
		List<String> finReferences = getFinanceReferences(statementRequest);
		if (finReferences.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getCif();
			finStatement.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			return finStatement;
		}
		FinStatementResponse response = finStatementController.getStatement(finReferences, APIConstants.STMT_ACCOUNT);
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
		FinStatementResponse finStatement = new FinStatementResponse();
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);

		List<String> finReferences = getFinanceReferences(statementRequest);
		if (finReferences.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getCif();
			finStatement.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			return finStatement;
		}
		FinStatementResponse response = finStatementController.getStatement(finReferences, APIConstants.STMT_INST_CERT);
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
		FinStatementResponse finStatement = new FinStatementResponse();
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);

		List<String> finReferences = getFinanceReferences(statementRequest);
		if (finReferences.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getCif();
			finStatement.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			return finStatement;
		}
		FinStatementResponse response = finStatementController.getStatement(finReferences, APIConstants.STMT_REPAY_SCHD);
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
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);

		String finReference = statementRequest.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		} else {
			FinanceMain finMain = financeMainDAO.getFinanceMainForPftCalc(finReference);
			if(finMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return finStatementResponse;
			} else {
				if (finMain.isFinIsActive() && !(StringUtils.equals(finMain.getClosingStatus(),FinanceConstants.CLOSE_STATUS_MATURED)
						|| StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_EARLYSETTLE))) {
					finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90403"));
					return finStatementResponse;
				}
			}
		}
		// call controller to get NOC details 
		FinStatementResponse response = finStatementController.getStatement(statementRequest, APIConstants.STMT_NOC);

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method to fetch fore-closure letter for specific finance.<br>
	 * 	- Provide fore-closure details for max allowed days of 7.
	 * 
	 * @param statementRequest
	 * @return FinStatementResponse
	 */
	@Override
	public FinStatementResponse getForeclosureLetter(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug("Enetring");
		
		FinStatementResponse finStatementResponse = new FinStatementResponse();
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);
		
		String finReference = statementRequest.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		} else {
			int count = financeMainDAO.getFinanceCountById(finReference, "", false);
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return finStatementResponse;
			}
		}
		
		if(statementRequest.getDays() <= 0 || statementRequest.getDays() > 7) {
			String[] valueParm = new String[3];
			valueParm[0] = "Days";
			valueParm[1] = "1";
			valueParm[2] = "7";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("65031", valueParm));
			return finStatementResponse;
		}
		// call controller to get fore-closure letter 
		FinStatementResponse response = finStatementController.getStatement(statementRequest, APIConstants.STMT_FORECLOSURE);
		logger.debug("Leaving");
		return response;
	}
	/**
	 * get the List of FinReferences Based on the CustID.
	 * 
	 * @param statementRequest
	 */
	private List<String> getFinanceReferences(FinStatementRequest statementRequest) {
		logger.debug("Entering");
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
			if (count <= 0) {
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
	/**
	 * get the SOA Report.
	 * 
	 * @param statementRequest
	 */
	@Override
	public FinStatementResponse GetStatement(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug("Enetring");

		Date fromDate = statementRequest.getFromDate();
		Date toDate = statementRequest.getToDate();
		FinStatementResponse finStatementResponse = null;
		if (StringUtils.isBlank(statementRequest.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		}
		if (fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "fromDate";
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		}
		if (toDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "toDate";
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		} else {
			Date appDate = DateUtility.getAppDate();
			if (DateUtility.compare(toDate, appDate) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "toDate:" + DateUtility.formatDate(toDate, PennantConstants.XMLDateFormat);
				valueParm[1] = "ApplicationDate:" + DateUtility.formatDate(appDate, PennantConstants.XMLDateFormat);
				finStatementResponse = new FinStatementResponse();
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
				return finStatementResponse;
			}
		}
		if (StringUtils.isBlank(statementRequest.getType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Type";
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		}
		if(!StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA)){
			String[] valueParm = new String[2];
			valueParm[0] = "Type: "+statementRequest.getType();
			valueParm[1] = APIConstants.REPORT_SOA;
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return finStatementResponse;
		}
		if (DateUtility.compare(fromDate, toDate)> 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "fromDate:" + DateUtility.formatDate(fromDate, PennantConstants.XMLDateFormat);
			valueParm[1] = "toDate:" + DateUtility.formatDate(toDate, PennantConstants.XMLDateFormat);
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
			return finStatementResponse;
		}
		if(StringUtils.isBlank(statementRequest.getTemplate())){
			statementRequest.setTemplate(APIConstants.REPORT_TEMPLATE_API);
		}
		if (statementRequest.getTemplate().equals(APIConstants.REPORT_TEMPLATE_API)) {
			statementRequest.setTemplate("FINENQ_StatementOfAccount_Template2");
		} else if (statementRequest.getTemplate().equals(APIConstants.REPORT_TEMPLATE_APPLICATION)) {
			statementRequest.setTemplate("FINENQ_StatementOfAccount");
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "template";
			valueParm[1] = APIConstants.REPORT_TEMPLATE_API +"  " +APIConstants.REPORT_TEMPLATE_APPLICATION;
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return finStatementResponse;
		}
		int count = financeMainDAO.getFinanceCountById(statementRequest.getFinReference());
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getFinReference();
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90260", valueParm));
			return finStatementResponse;
		}
		// call the controller for report genaration
		finStatementResponse = finStatementController.getReportSatatement(statementRequest);
		logger.debug("Leaving");
		return finStatementResponse;
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
