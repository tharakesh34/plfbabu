package com.pennanttech.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ForeClosureLetter;
import com.pennant.backend.model.finance.ForeClosureResponse;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinStatementController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pffws.FinStatementRestService;
import com.pennanttech.pffws.FinStatementSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinStatementWebServiceImpl implements FinStatementRestService, FinStatementSoapService {
	private static final Logger logger = LogManager.getLogger(FinStatementWebServiceImpl.class);

	private FinStatementController finStatementController;
	private CustomerDetailsService customerDetailsService;
	private FinanceMainDAO financeMainDAO;
	private FinanceMainService financeMainService;
	private SOAReportGenerationService soaReportGenerationService;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeTypeDAO feeTypeDAO;

	/**
	 * get the FinStatement Details by the given FinReference/CustCif.
	 * 
	 * @param statementRequest
	 * @throws ServiceException
	 */
	@Override
	public FinStatementResponse getStatementOfAccount(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
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
		FinStatementResponse response = finStatementController.getStatement(finReferences,
				APIConstants.STMT_REPAY_SCHD);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
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
			if (finMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return finStatementResponse;
			} else {
				if (finMain.isFinIsActive() && !(StringUtils.equals(finMain.getClosingStatus(),
						FinanceConstants.CLOSE_STATUS_MATURED)
						|| StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_EARLYSETTLE))) {
					finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90403"));
					return finStatementResponse;
				}
			}
		}
		// call controller to get NOC details 
		FinStatementResponse response = finStatementController.getStatement(statementRequest, APIConstants.STMT_NOC);

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method to fetch fore-closure letter for specific finance.<br>
	 * - Provide fore-closure details for max allowed days of 7.
	 * 
	 * @param statementRequest
	 * @return FinStatementResponse
	 */
	@Override
	public FinStatementResponse getForeclosureLetter(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

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

		if (statementRequest.getDays() <= 0 || statementRequest.getDays() > 7) {
			String[] valueParm = new String[3];
			valueParm[0] = "Days";
			valueParm[1] = "1";
			valueParm[2] = "7";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("65031", valueParm));
			return finStatementResponse;
		}
		// call controller to get fore-closure letter 
		FinStatementResponse response = finStatementController.getStatement(statementRequest,
				APIConstants.STMT_FORECLOSURE);
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * get the List of FinReferences Based on the CustID.
	 * 
	 * @param statementRequest
	 */
	private List<String> getFinanceReferences(FinStatementRequest statementRequest) {
		logger.debug(Literal.ENTERING);
		List<String> referencesList = new ArrayList<>();
		if (StringUtils.isNotBlank(statementRequest.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(statementRequest.getCif());
			referencesList = financeMainService.getFinReferencesByCustID(customer.getCustID(),
					statementRequest.getFinActiveStatus());
		} else {
			referencesList.add(statementRequest.getFinReference());
		}
		logger.debug(Literal.LEAVING);
		return referencesList;
	}

	/**
	 * Basic Validations
	 * 
	 * @param statementRequest
	 */
	private WSReturnStatus validateStatementRequest(FinStatementRequest statementRequest) {
		logger.debug(Literal.ENTERING);
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
						|| StringUtils.equals(statementRequest.getFinActiveStatus(),
								APIConstants.CLOSE_STATUS_ACTIVE))) {
					String[] valueParm = new String[1];
					valueParm[0] = statementRequest.getFinActiveStatus();
					return returnStatus = APIErrorHandlerService.getFailedStatus("90501", valueParm);
				}
				if (StringUtils.equals(APIConstants.CLOSE_STATUS_ACTIVE, statementRequest.getFinActiveStatus())) {
					statementRequest.setFinActiveStatus(null);
				}
			}
		} else if (StringUtils.isNotBlank(statementRequest.getFinReference())) {
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
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * get the SOA Report.
	 * 
	 * @param statementRequest
	 */
	@Override
	public FinStatementResponse getStatement(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

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

		FinanceMain finMain = financeMainDAO.getFinanceMainForPftCalc(statementRequest.getFinReference());

		if (finMain == null) {
			finStatementResponse = new FinStatementResponse();
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getFinReference();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return finStatementResponse;
		}
		String requestType = statementRequest.getType();
		if (StringUtils.equals(requestType, APIConstants.STMT_NOC)
				|| StringUtils.equals(requestType, APIConstants.STMT_NOC_REPORT)) {
			finStatementResponse = new FinStatementResponse();

			if (finMain.isFinIsActive() || (!StringUtils.equals(finMain.getClosingStatus(),
					FinanceConstants.CLOSE_STATUS_MATURED)
					&& !StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_EARLYSETTLE))) {
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90403"));
				return finStatementResponse;
			}
		}
		// Sell-Off Loan not allowed to generate States reports.
		if (StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_SELLOFF)) {

			String[] valueParm = new String[1];
			valueParm[0] = "finReference: " + statementRequest.getFinReference();
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("BL001", valueParm));
			return finStatementResponse;

		}
		if (StringUtils.equals(requestType, APIConstants.REPORT_SOA)
				|| StringUtils.equals(requestType, APIConstants.REPORT_SOA_REPORT)) {
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
				/*
				 * Date appDate = DateUtility.getAppDate(); if (DateUtility.compare(toDate, appDate) > 0) { String[]
				 * valueParm = new String[2]; valueParm[0] = "toDate:" + DateUtility.formatDate(toDate,
				 * PennantConstants.XMLDateFormat); valueParm[1] = "ApplicationDate:" + DateUtility.formatDate(appDate,
				 * PennantConstants.XMLDateFormat); finStatementResponse = new FinStatementResponse();
				 * finStatementResponse.setReturnStatus(APIErrorHandlerService. getFailedStatus("90220", valueParm));
				 * return finStatementResponse; }
				 */
			}
			if (DateUtility.compare(fromDate, toDate) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "fromDate:" + DateUtility.format(fromDate, PennantConstants.XMLDateFormat);
				valueParm[1] = "toDate:" + DateUtility.format(toDate, PennantConstants.XMLDateFormat);
				finStatementResponse = new FinStatementResponse();
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
				return finStatementResponse;
			}
		}
		if (StringUtils.equals(requestType, APIConstants.STMT_INST_CERT_REPORT)
				|| StringUtils.equals(requestType, APIConstants.STMT_PROV_INST_CERT_REPORT)) {
			fromDate = statementRequest.getFromDate();
			if (fromDate != null) {
				if (finMain != null && finMain.getFinStartDate() != null) {
					if (DateUtility.compare(fromDate, finMain.getFinStartDate()) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "fromDate:" + DateUtility.format(fromDate, PennantConstants.XMLDateFormat);
						valueParm[1] = "Loan startDate:"
								+ DateUtility.format(finMain.getFinStartDate(), PennantConstants.XMLDateFormat);
						finStatementResponse = new FinStatementResponse();
						finStatementResponse
								.setReturnStatus(APIErrorHandlerService.getFailedStatus("30507", valueParm));
						return finStatementResponse;
					}
					if (finMain != null && finMain.getMaturityDate() != null) {
						if (DateUtility.compare(finMain.getMaturityDate(), fromDate) < 0) {
							String[] valueParm = new String[2];
							valueParm[1] = "MaturityDate:"
									+ DateUtility.format(finMain.getMaturityDate(), PennantConstants.XMLDateFormat);
							valueParm[0] = "fromDate:" + DateUtility.format(fromDate, PennantConstants.XMLDateFormat);
							finStatementResponse = new FinStatementResponse();
							finStatementResponse
									.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
							return finStatementResponse;
						}
					}
				}
			}
		}

		if (StringUtils.isBlank(requestType)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Type";
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		}
		if (!(APIConstants.REPORT_SOA.equals(requestType) || StringUtils.equals(requestType, APIConstants.STMT_NOC)
				|| APIConstants.STMT_REPAY_SCHD.equals(requestType)
				|| APIConstants.REPORT_SOA_REPORT.equals(requestType)
				|| APIConstants.STMT_NOC_REPORT.equals(requestType)
				|| APIConstants.STMT_REPAY_SCHD_REPORT.equals(requestType)
				|| APIConstants.STMT_INST_CERT_REPORT.equals(requestType)
				|| APIConstants.STMT_FORECLOSURE_REPORT.equals(requestType)
				|| APIConstants.STMT_PROV_INST_CERT_REPORT.equals(requestType))) {
			String[] valueParm = new String[2];
			valueParm[0] = "Type: " + requestType;
			valueParm[1] = APIConstants.REPORT_SOA + "," + APIConstants.STMT_NOC + "," + APIConstants.STMT_REPAY_SCHD;
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return finStatementResponse;
		}

		if (StringUtils.isBlank(statementRequest.getTemplate())) {
			statementRequest.setTemplate(APIConstants.REPORT_TEMPLATE_API);
		}

		int count = financeMainDAO.getFinanceCountById(statementRequest.getFinReference());
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getFinReference();
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90260", valueParm));
			return finStatementResponse;
		}
		if (APIConstants.REPORT_TEMPLATE_API.equals(statementRequest.getTemplate())) {
			if (StringUtils.equals(requestType, APIConstants.REPORT_SOA)
					|| StringUtils.equals(requestType, APIConstants.REPORT_SOA_REPORT)) {

				List<String> finTypes = soaReportGenerationService.getSOAFinTypes();

				if (finMain.isAlwFlexi()) {
					statementRequest.setTemplate("FINENQ_StatementOfAccount_FinType_Hybrid");
				} else if (finTypes != null && finTypes.contains(finMain.getFinType())) {
					statementRequest.setTemplate("FINENQ_StatementOfAccount_FinType");
				} else {
					statementRequest.setTemplate("FINENQ_StatementOfAccount");
				}
			} else if (StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD)
					|| StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD_REPORT)) {
				statementRequest.setTemplate("menu_Item_PaymentSchedule");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_NOC)
					|| StringUtils.equals(requestType, APIConstants.STMT_NOC_REPORT)) {
				statementRequest.setTemplate("menu_Item_NoObjectionCertificate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_INST_CERT_REPORT)) {
				statementRequest.setTemplate("menu_Item_InterestCertficate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_FORECLOSURE_REPORT)) {
				statementRequest.setTemplate("menu_Item_ForeclosureTerminationReport");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_PROV_INST_CERT_REPORT)) {
				statementRequest.setTemplate("menu_Item_ProvisionalCertificate");
			}
		} else if (statementRequest.getTemplate().equals(APIConstants.REPORT_TEMPLATE_APPLICATION)) {
			if (StringUtils.equals(requestType, APIConstants.REPORT_SOA)
					|| StringUtils.equals(requestType, APIConstants.REPORT_SOA_REPORT)) {
				statementRequest.setTemplate("menu_Item_AccountStmt");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD)
					|| StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD_REPORT)) {
				statementRequest.setTemplate("menu_Item_PaymentSchedule");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_NOC)
					|| StringUtils.equals(requestType, APIConstants.STMT_NOC_REPORT)) {
				statementRequest.setTemplate("menu_Item_NoObjectionCertificate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_INST_CERT_REPORT)) {
				statementRequest.setTemplate("menu_Item_InterestCertficate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_FORECLOSURE_REPORT)) {
				statementRequest.setTemplate("menu_Item_ForeclosureTerminationReport");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_PROV_INST_CERT_REPORT)) {
				statementRequest.setTemplate("menu_Item_ProvisionalCertificate");
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "template";
			valueParm[1] = APIConstants.REPORT_TEMPLATE_API + "  " + APIConstants.REPORT_TEMPLATE_APPLICATION;
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return finStatementResponse;
		}

		// call the controller for report genaration
		finStatementResponse = finStatementController.getReportSatatement(statementRequest);
		logger.debug(Literal.LEAVING);
		return finStatementResponse;
	}

	@Override
	public StatementOfAccount getStatementOfAcc(FinStatementRequest statementRequest)
			throws ServiceException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);
		FinStatementResponse finStatementResponse = validateSOARequest(statementRequest, false);
		if (finStatementResponse != null) {
			StatementOfAccount statementOfAccount = new StatementOfAccount();
			statementOfAccount.setReturnStatus(finStatementResponse.getReturnStatus());
			return statementOfAccount;
		}
		int count = financeMainDAO.getFinanceCountById(statementRequest.getFinReference());
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getFinReference();
			StatementOfAccount statementOfAccount = new StatementOfAccount();
			statementOfAccount.setReturnStatus(APIErrorHandlerService.getFailedStatus("90260", valueParm));
			return statementOfAccount;
		}
		StatementOfAccount statementOfAccount = finStatementController.getStatementOfAcc(statementRequest);
		return statementOfAccount;
	}

	private FinStatementResponse validateSOARequest(FinStatementRequest statementRequest, boolean isTypeReq) {
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
			Date appDate = SysParamUtil.getAppDate();
			if (DateUtility.compare(toDate, appDate) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "toDate:" + DateUtility.format(toDate, PennantConstants.XMLDateFormat);
				valueParm[1] = "ApplicationDate:" + DateUtility.format(appDate, PennantConstants.XMLDateFormat);
				finStatementResponse = new FinStatementResponse();
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
				return finStatementResponse;
			}
		}
		if (StringUtils.isBlank(statementRequest.getType()) && isTypeReq) {
			String[] valueParm = new String[1];
			valueParm[0] = "Type";
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		}
		if (!StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA) && isTypeReq) {
			String[] valueParm = new String[2];
			valueParm[0] = "Type: " + statementRequest.getType();
			valueParm[1] = APIConstants.REPORT_SOA;
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return finStatementResponse;
		}
		if (DateUtility.compare(fromDate, toDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "fromDate:" + DateUtility.format(fromDate, PennantConstants.XMLDateFormat);
			valueParm[1] = "toDate:" + DateUtility.format(toDate, PennantConstants.XMLDateFormat);
			finStatementResponse = new FinStatementResponse();
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
			return finStatementResponse;
		}
		return finStatementResponse;
	}

	@Override
	public ForeClosureResponse getForeclosureStmt(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ForeClosureResponse response = new ForeClosureResponse();
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);
		FinanceMain financeMain = null;
		Date fromDate = statementRequest.getFromDate();
		String finReference = statementRequest.getFinReference();

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			financeMain = financeMainDAO.getFinanceMainForPftCalc(finReference);
			if (financeMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			}
		}

		if (fromDate != null) {
			if (DateUtil.compare(fromDate, financeMain.getFinStartDate()) < 0
					|| DateUtil.compare(financeMain.getMaturityDate(), fromDate) < 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "FromDate";
				valueParm[1] = "FinStartDate";
				valueParm[2] = "MaturityDate";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30567", valueParm));
				return response;
			}
		}

		statementRequest.setDays(1);

		// call controller to get fore-closure letter 
		FinStatementResponse finStatement = null;
		try {
			finStatement = finStatementController.getStatement(statementRequest, APIConstants.STMT_FORECLOSURE);
			FinanceDetail financeDetail = finStatement.getFinance().get(0);
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			List<FinFeeDetail> fees = finScheduleData.getFeeDues();

			List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdvisesByFinRef(finReference, "_View");
			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finReference);
			TaxAmountSplit taxSplit;
			BigDecimal totalADgstAmt = BigDecimal.ZERO;
			BigDecimal totalBCgstFee = BigDecimal.ZERO;

			if (!CollectionUtils.isEmpty(manualAdviseFees)) {
				for (FinFeeDetail feeDetail : fees) {
					for (ManualAdvise advisedFees : manualAdviseFees) {
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advisedFees.getTaxComponent())) {
							if (StringUtils.equals(feeDetail.getFeeTypeCode(), advisedFees.getFeeTypeCode())) {
								BigDecimal actualOriginal = feeDetail.getActualAmount();
								taxSplit = GSTCalculator.getExclusiveGST(actualOriginal, taxPercentages);
								feeDetail.setActualAmount(actualOriginal.add(taxSplit.gettGST()));
								totalADgstAmt = totalADgstAmt.add(taxSplit.gettGST());
							}
							if (feeDetail.getFeeID() == advisedFees.getAdviseID()) {
								if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advisedFees.getTaxComponent())) {
									BigDecimal actualOriginal = feeDetail.getActualAmount();
									taxSplit = GSTCalculator.getExclusiveGST(actualOriginal, taxPercentages);
									totalBCgstFee = totalBCgstFee.add(taxSplit.gettGST());
								}
							}
						}
					}
				}
			}

			FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference, "E");

			ForeClosureLetter letter = new ForeClosureLetter();
			if (excessAmount != null) {
				letter.setExcessAmount(excessAmount.getBalanceAmt());
			}

			FinanceSummary summary = finScheduleData.getFinanceSummary();
			letter.setOutStandPrincipal(summary.getOutStandPrincipal());
			response.setForeClosureFees(finScheduleData.getForeClosureFees());
			response.setFeeDues(finScheduleData.getFeeDues());

			for (ForeClosure foreClosure : financeDetail.getForeClosureDetails()) {
				letter.setAccuredIntTillDate(foreClosure.getAccuredIntTillDate());
				letter.setValueDate(foreClosure.getValueDate());
				letter.setChargeAmount(foreClosure.getChargeAmount());
				letter.setForeCloseAmount(foreClosure.getForeCloseAmount());
				letter.setBounceCharge(foreClosure.getBounceCharge().add(totalBCgstFee));
				letter.setTotalLPIAmount(foreClosure.getLPIAmount());
				letter.setReceivableAdviceAmt(foreClosure.getReceivableADFee().add(totalADgstAmt));
			}

			FeeType taxDetail = feeTypeDAO.getApprovedFeeTypeByFeeCode(RepayConstants.ALLOCATION_ODC);
			BigDecimal totPenaltyGstAmt = BigDecimal.ZERO;
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxDetail.getTaxComponent())) {
				taxSplit = GSTCalculator.getExclusiveGST(letter.getChargeAmount(), taxPercentages);
				totPenaltyGstAmt = totPenaltyGstAmt.add(taxSplit.gettGST());
				letter.setChargeAmount(letter.getChargeAmount().add(totPenaltyGstAmt));
			}

			letter.setForeCloseAmount(letter.getForeCloseAmount().add(totalBCgstFee).add(totalADgstAmt)
					.add(letter.getTotalLPIAmount().add(totPenaltyGstAmt)));
			response.setFinReference(finReference);
			response.setForeClosure(letter);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		logger.debug(Literal.LEAVING);
		return response;
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

	@Autowired
	public void setSoaReportGenerationService(SOAReportGenerationService soaReportGenerationService) {
		this.soaReportGenerationService = soaReportGenerationService;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}