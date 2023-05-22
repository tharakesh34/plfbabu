package com.pennanttech.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ForeClosureLetter;
import com.pennant.backend.model.finance.ForeClosureResponse;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.FinStatementController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.foreclosure.service.ForeClosureService;
import com.pennanttech.pffws.FinStatementRestService;
import com.pennanttech.pffws.FinStatementSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinStatementWebServiceImpl extends ExtendedTestClass
		implements FinStatementRestService, FinStatementSoapService {
	private static final Logger logger = LogManager.getLogger(FinStatementWebServiceImpl.class);

	private FinStatementController finStatementController;
	private CustomerDetailsService customerDetailsService;
	private FinanceMainDAO financeMainDAO;
	private FinanceMainService financeMainService;
	private SOAReportGenerationService soaReportGenerationService;
	private ForeClosureService foreClosureService;

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
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();

		FinStatementResponse finStatement = new FinStatementResponse();
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			finStatement.setReturnStatus(returnStatus);
			return finStatement;
		}

		List<Long> finIDList = getFinanceReferences(statementRequest);
		if (finIDList.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getCif();
			finStatement.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			return finStatement;
		}

		FinStatementResponse response = finStatementController.getStatement(finIDList, APIConstants.STMT_ACCOUNT);
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

		List<Long> finIDList = getFinanceReferences(statementRequest);
		if (finIDList.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getCif();
			finStatement.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			return finStatement;
		}

		FinStatementResponse response = finStatementController.getStatement(finIDList, APIConstants.STMT_INST_CERT);
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

		List<Long> finIDList = getFinanceReferences(statementRequest);
		if (finIDList.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = statementRequest.getCif();
			finStatement.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			return finStatement;
		}

		FinStatementResponse response = finStatementController.getStatement(finIDList, APIConstants.STMT_REPAY_SCHD);
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
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(finReference);
		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return finStatementResponse;
		}

		if (fm.isFinIsActive() && !(FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())
				|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus()))) {
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90403"));
			return finStatementResponse;
		}

		statementRequest.setFinID(fm.getFinID());
		// call controller to get NOC details
		FinStatementResponse response = finStatementController.getStatement(statementRequest, APIConstants.STMT_NOC);

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method to fetch fore-closure letter for specific finance.<br>
	 * - Provide fore-closure details for max allowed days of 7.
	 * 
	 * @param stmtRequest
	 * @return FinStatementResponse
	 */
	@Override
	public FinStatementResponse getForeclosureLetter(FinStatementRequest stmtRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinStatementResponse finStatementResponse = new FinStatementResponse();
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = stmtRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);

		String finReference = stmtRequest.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		}

		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return finStatementResponse;
		}

		if (stmtRequest.getDays() <= 0 || stmtRequest.getDays() > 7) {
			String[] valueParm = new String[3];
			valueParm[0] = "Days";
			valueParm[1] = "1";
			valueParm[2] = "7";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("65031", valueParm));
			return finStatementResponse;
		}

		stmtRequest.setFinID(finID);
		stmtRequest.setFinReference(finReference);

		// call controller to get fore-closure letter
		FinStatementResponse response = finStatementController.getStatement(stmtRequest, APIConstants.STMT_FORECLOSURE);
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * get the List of FinReferences Based on the CustID.
	 * 
	 * @param statementRequest
	 */
	private List<Long> getFinanceReferences(FinStatementRequest statementRequest) {
		logger.debug(Literal.ENTERING);
		List<Long> finIDList = new ArrayList<>();
		if (StringUtils.isNotBlank(statementRequest.getCif())) {
			return financeMainService.getFinIDList(statementRequest.getCif(), statementRequest.getFinActiveStatus());
		} else {
			finIDList.add(financeMainService.getFinID(statementRequest.getFinReference()));
		}

		logger.debug(Literal.LEAVING);
		return finIDList;
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
			Long finID = financeMainDAO.getActiveFinID(statementRequest.getFinReference(), TableType.MAIN_TAB);
			if (finID == null) {
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
	 * @param stmtReq
	 */
	@Override
	public FinStatementResponse getStatement(FinStatementRequest stmtReq) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = stmtReq.getFinReference();
		String requestType = stmtReq.getType();
		Date fromDate = stmtReq.getFromDate();
		Date toDate = stmtReq.getToDate();

		FinStatementResponse stmtResp = null;

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return stmtResp;
		}

		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90260", valueParm));
			return stmtResp;
		}

		stmtReq.setFinID(finID);

		FinanceMain fm = financeMainDAO.getFinanceMain(finID, TableType.MAIN_TAB);

		if (fm == null) {
			stmtResp = new FinStatementResponse();
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return stmtResp;
		}

		if (APIConstants.STMT_NOC.equals(requestType) || APIConstants.STMT_NOC_REPORT.equals(requestType)) {
			stmtResp = new FinStatementResponse();

			if (fm.isFinIsActive() || (!FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())
					&& !FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus()))) {
				stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90403"));
				return stmtResp;
			}
		}
		// Sell-Off Loan not allowed to generate States reports.
		if (FinanceConstants.CLOSE_STATUS_SELLOFF.equals(fm.getClosingStatus())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference: " + finReference;
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("BL001", valueParm));
			return stmtResp;

		}

		if (APIConstants.REPORT_SOA.equals(requestType) || APIConstants.REPORT_SOA_REPORT.equals(requestType)) {
			if (fromDate == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "fromDate";
				stmtResp = new FinStatementResponse();
				stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return stmtResp;
			}

			if (toDate == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "toDate";
				stmtResp = new FinStatementResponse();
				stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return stmtResp;
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

			if (DateUtil.compare(fromDate, toDate) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "fromDate:" + DateUtil.format(fromDate, PennantConstants.XMLDateFormat);
				valueParm[1] = "toDate:" + DateUtil.format(toDate, PennantConstants.XMLDateFormat);
				stmtResp = new FinStatementResponse();
				stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
				return stmtResp;
			}
		}

		if (StringUtils.equals(requestType, APIConstants.STMT_INST_CERT_REPORT)
				|| StringUtils.equals(requestType, APIConstants.STMT_PROV_INST_CERT_REPORT)) {
			fromDate = stmtReq.getFromDate();
			if (fromDate != null) {
				if (fm != null && fm.getFinStartDate() != null) {
					if (DateUtil.compare(fromDate, fm.getFinStartDate()) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "fromDate:" + DateUtil.format(fromDate, PennantConstants.XMLDateFormat);
						valueParm[1] = "Loan startDate:"
								+ DateUtil.format(fm.getFinStartDate(), PennantConstants.XMLDateFormat);
						stmtResp = new FinStatementResponse();
						stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("30507", valueParm));
						return stmtResp;
					}
					if (fm != null && fm.getMaturityDate() != null) {
						if (DateUtil.compare(fm.getMaturityDate(), fromDate) < 0) {
							String[] valueParm = new String[2];
							valueParm[1] = "MaturityDate:"
									+ DateUtil.format(fm.getMaturityDate(), PennantConstants.XMLDateFormat);
							valueParm[0] = "fromDate:" + DateUtil.format(fromDate, PennantConstants.XMLDateFormat);
							stmtResp = new FinStatementResponse();
							stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParm));
							return stmtResp;
						}
					}
				}
			}
		}

		if (StringUtils.isBlank(requestType)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Type";
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return stmtResp;
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
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return stmtResp;
		}

		if (StringUtils.isBlank(stmtReq.getTemplate())) {
			stmtReq.setTemplate(APIConstants.REPORT_TEMPLATE_API);
		}

		if (APIConstants.REPORT_TEMPLATE_API.equals(stmtReq.getTemplate())) {
			if (StringUtils.equals(requestType, APIConstants.REPORT_SOA)
					|| StringUtils.equals(requestType, APIConstants.REPORT_SOA_REPORT)) {

				List<String> finTypes = soaReportGenerationService.getSOAFinTypes();

				if (fm.isAlwFlexi()) {
					stmtReq.setTemplate("FINENQ_StatementOfAccount_FinType_Hybrid");
				} else if (finTypes != null && finTypes.contains(fm.getFinType())) {
					stmtReq.setTemplate("FINENQ_StatementOfAccount_FinType");
				} else {
					stmtReq.setTemplate("FINENQ_StatementOfAccount");
				}
			} else if (StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD)
					|| StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD_REPORT)) {
				stmtReq.setTemplate("menu_Item_PaymentSchedule");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_NOC)
					|| StringUtils.equals(requestType, APIConstants.STMT_NOC_REPORT)) {
				stmtReq.setTemplate("menu_Item_NoObjectionCertificate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_INST_CERT_REPORT)) {
				stmtReq.setTemplate("menu_Item_InterestCertficate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_FORECLOSURE_REPORT)) {
				stmtReq.setTemplate("menu_Item_ForeclosureTerminationReport");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_PROV_INST_CERT_REPORT)) {
				stmtReq.setTemplate("menu_Item_ProvisionalCertificate");
			}
		} else if (stmtReq.getTemplate().equals(APIConstants.REPORT_TEMPLATE_APPLICATION)) {
			if (StringUtils.equals(requestType, APIConstants.REPORT_SOA)
					|| StringUtils.equals(requestType, APIConstants.REPORT_SOA_REPORT)) {
				stmtReq.setTemplate("menu_Item_AccountStmt");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD)
					|| StringUtils.equals(requestType, APIConstants.STMT_REPAY_SCHD_REPORT)) {
				stmtReq.setTemplate("menu_Item_PaymentSchedule");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_NOC)
					|| StringUtils.equals(requestType, APIConstants.STMT_NOC_REPORT)) {
				stmtReq.setTemplate("menu_Item_NoObjectionCertificate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_INST_CERT_REPORT)) {
				stmtReq.setTemplate("menu_Item_InterestCertficate");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_FORECLOSURE_REPORT)) {
				stmtReq.setTemplate("menu_Item_ForeclosureTerminationReport");
			} else if (StringUtils.equals(requestType, APIConstants.STMT_PROV_INST_CERT_REPORT)) {
				stmtReq.setTemplate("menu_Item_ProvisionalCertificate");
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "template";
			valueParm[1] = APIConstants.REPORT_TEMPLATE_API + "  " + APIConstants.REPORT_TEMPLATE_APPLICATION;
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus("90337", valueParm));
			return stmtResp;
		}

		// call the controller for report genaration
		stmtResp = finStatementController.getReportSatatement(stmtReq);
		logger.debug(Literal.LEAVING);
		return stmtResp;
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

		Long finID = financeMainDAO.getFinID(statementRequest.getFinReference(), TableType.MAIN_TAB);

		if (finID == null) {
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
			if (DateUtil.compare(toDate, appDate) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "toDate:" + DateUtil.format(toDate, PennantConstants.XMLDateFormat);
				valueParm[1] = "ApplicationDate:" + DateUtil.format(appDate, PennantConstants.XMLDateFormat);
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
		if (DateUtil.compare(fromDate, toDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "fromDate:" + DateUtil.format(fromDate, PennantConstants.XMLDateFormat);
			valueParm[1] = "toDate:" + DateUtil.format(toDate, PennantConstants.XMLDateFormat);
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
		FinanceMain fm = null;
		Date fromDate = statementRequest.getFromDate();
		String finReference = statementRequest.getFinReference();

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		fm = financeMainDAO.getFinanceMain(finReference, TableType.MAIN_TAB);
		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		if (fromDate != null) {
			if (DateUtil.compare(fromDate, fm.getFinStartDate()) < 0
					|| DateUtil.compare(fm.getMaturityDate(), fromDate) < 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "FromDate";
				valueParm[1] = "FinStartDate";
				valueParm[2] = "MaturityDate";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30567", valueParm));
				return response;
			}
		}

		// call controller to get fore-closure letter
		try {
			ForeClosureLetter letter = foreClosureService.getForeClosureAmt(statementRequest, response);

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

	@Override
	public FinStatementResponse getForeclosureStmtV1(FinStatementRequest statementRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinStatementResponse finStatementResponse = new FinStatementResponse();
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = statementRequest.getCif();
		APIErrorHandlerService.logKeyFields(logFields);

		String finReference = statementRequest.getFinReference();
		Long finID = financeMainDAO.getActiveFinID(finReference);
		statementRequest.setFinID(finID);

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return finStatementResponse;
		} else {
			int count = financeMainDAO.getFinanceCountById(finID, "", false);
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				finStatementResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return finStatementResponse;
			}
		}

		statementRequest.setDays(statementRequest.getDays());
		// call controller to get fore-closure letter
		FinStatementResponse response = finStatementController.getStatement(statementRequest,
				APIConstants.STMT_FORECLOSUREV1);
		if (response != null) {
			response.setCustomer(null);
			response.setFinance(null);
			response.setDocImage(null);
			response.setStatementSOA(null);
		} else {
			response = new FinStatementResponse();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			logger.debug(Literal.LEAVING);
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
	public void setForeClosureService(ForeClosureService foreClosureService) {
		this.foreClosureService = foreClosureService;
	}
}