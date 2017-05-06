package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

public class FinStatementController extends SummaryDetailService {

	private final static Logger		logger	= Logger.getLogger(FinStatementController.class);

	private FinanceDetailService	financeDetailService;
	private PostingsDAO				postingsDAO;
	private CollateralSetupService	collateralSetupService;

	// TODO: cleanup required(DDP)
	private FinServiceInstController finServiceInstController;
	private FinanceScheduleDetailDAO 	financeScheduleDetailDAO;

	/**
	 * get the FinStatement Details by the given FinReferences.
	 * 
	 * @param finreferencecList
	 * @throws ServiceException
	 */
	public FinStatementResponse getStatement(List<String> finReferences, String serviceName) {
		logger.debug("Enetring");

		FinStatementResponse stmtResponse = new FinStatementResponse();
		List<FinanceDetail> finDetailList = new ArrayList<>();
		try {
			for (String finReference : finReferences) {
				FinanceDetail financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
						FinanceConstants.FINSER_EVENT_ORG, "");
				if (financeDetail == null) {
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					return stmtResponse;
				}

				if (StringUtils.equals(APIConstants.STMT_ACCOUNT, serviceName)) {
					List<ReturnDataSet> postingsList = postingsDAO.getPostingsByFinRef(finReference);
					financeDetail.setReturnDataSetList(postingsList);
				}
				if (StringUtils.equals(APIConstants.STMT_INST_CERT, serviceName)) {
					long custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
					if (custId != 0) {
						List<CollateralSetup> collatSetupList = collateralSetupService.getCollateralSetupByCustId(custId);
						financeDetail.setCollateralSetup(collatSetupList);
					}
				}

				// generate response info
				prepareResponse(financeDetail, serviceName);

				stmtResponse.setCustomer(financeDetail.getCustomerDetails());
				financeDetail.setCustomerDetails(null);
				finDetailList.add(financeDetail);
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stmtResponse;
		}
		stmtResponse.setFinance(finDetailList);
		stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug("Leaving");
		return stmtResponse;
	}

	/**
	 * get the FinStatement Details by the given FinReferences.
	 * 
	 * @param finreferencecList
	 * @throws ServiceException
	 */
	public FinStatementResponse getStatement(List<String> finReferences, String serviceName, int days) {
		logger.debug("Enetring");

		FinStatementResponse stmtResponse = new FinStatementResponse();
		List<FinanceDetail> finDetailList = new ArrayList<>();
		try {
			for (String finReference : finReferences) {
				FinanceDetail financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
						FinanceConstants.FINSER_EVENT_ORG, "");
				if (financeDetail == null) {
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					return stmtResponse;
				}

				if (StringUtils.equals(APIConstants.STMT_ACCOUNT, serviceName)) {
					List<ReturnDataSet> postingsList = postingsDAO.getPostingsByFinRef(finReference);
					financeDetail.setReturnDataSetList(postingsList);
				}
				if (StringUtils.equals(APIConstants.STMT_INST_CERT, serviceName)) {
					long custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
					if (custId != 0) {
						List<CollateralSetup> collatSetupList = collateralSetupService.getCollateralSetupByCustId(custId);
						financeDetail.setCollateralSetup(collatSetupList);
					}
				}

				if (StringUtils.equals(APIConstants.STMT_FORECLOSURE, serviceName)) {
					FinanceDetail stmtFinDetail = getForeClosureDetails(financeDetail, days);

					financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
							FinanceConstants.FINSER_EVENT_ORG, "");
					if (financeDetail == null) {
						stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
						return stmtResponse;
					}
					financeDetail.setForeClosureDetails(stmtFinDetail.getForeClosureDetails());
					financeDetail.getFinScheduleData().setFinODDetails(stmtFinDetail.getFinScheduleData().getFinODDetails());
				}

				// generate response info
				prepareResponse(financeDetail, serviceName);

				stmtResponse.setCustomer(financeDetail.getCustomerDetails());
				financeDetail.setCustomerDetails(null);
				finDetailList.add(financeDetail);
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stmtResponse;
		}
		stmtResponse.setFinance(finDetailList);
		stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug("Leaving");
		return stmtResponse;
	}

	/**
	 * 
	 * @param financeDetail
	 * @return
	 * @throws Exception
	 */
	private FinanceDetail getForeClosureDetails(FinanceDetail financeDetail, int days) throws Exception {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		String finReference = finScheduleData.getFinanceMain().getFinReference();

		//Fetch Total Repayment Amount till Maturity date for Early Settlement
		BigDecimal repayAmt = financeScheduleDetailDAO.getTotalRepayAmount(finReference);

		FinServiceInstruction serviceInstruction = new FinServiceInstruction();
		serviceInstruction.setAmount(repayAmt);
		serviceInstruction.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSTLENQ);
		if (serviceInstruction.getToDate() == null) {
			serviceInstruction.setToDate(finScheduleData.getFinanceMain().getMaturityDate());
		}
		FinanceDetail finStmtDetail = new FinanceDetail();
		FinScheduleData scheduleData = new FinScheduleData();
		List<ForeClosure> foreClosureList = new ArrayList<ForeClosure>();
		List<FinODDetails> finOdDetaiList = new ArrayList<FinODDetails>();
		try {
			for (int i = 1; i <= days; i++) {
				Cloner cloner = new Cloner();
				FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
				serviceInstruction.setFromDate(DateUtility.addDays(DateUtility.getAppDate(), i));
				aFinanceDetail = finServiceInstController.doProcessPayments(aFinanceDetail, serviceInstruction);

				foreClosureList.add(aFinanceDetail.getForeClosureDetails().get(0));
				finOdDetaiList.add(aFinanceDetail.getFinScheduleData().getFinODDetails().get(0));
			}
			
			finStmtDetail.setForeClosureDetails(foreClosureList);
			scheduleData.setFinODDetails(finOdDetaiList);
			finStmtDetail.setFinScheduleData(scheduleData);
			
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}

		return finStmtDetail;
	}

	/**
	 * 
	 * @param finReference
	 * @param stmtForeclosure
	 * @return
	 */
	public FinStatementResponse getStatement(FinStatementRequest statementRequest, String serviceName) {
		List<String> references = new ArrayList<String>();
		references.add(statementRequest.getFinReference());
		return getStatement(references, serviceName, statementRequest.getDays());
	}

	private void prepareResponse(FinanceDetail financeDetail, String servicName) {
		financeDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
		financeDetail.getFinScheduleData().setFinReference(null);
		financeDetail.getFinScheduleData().setInsuranceList(null);
		financeDetail.getFinScheduleData().setStepPolicyDetails(null);
		financeDetail.getFinScheduleData().setPlanEMIHDates(null);
		financeDetail.getFinScheduleData().setPlanEMIHmonths(null);
		if (!StringUtils.equals(APIConstants.STMT_REPAY_SCHD, servicName)) {
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(null);
		}
		financeDetail.getFinScheduleData().setRateInstruction(null);
		financeDetail.getFinScheduleData().setRepayInstructions(null);
		financeDetail.setFinFlagsDetails(null);
		financeDetail.setCovenantTypeList(null);
		financeDetail.setMandate(null);
		financeDetail.setAdvancePaymentsList(null);
		financeDetail.setGurantorsDetailList(null);
		financeDetail.setDocumentDetailsList(null);
		financeDetail.setCollateralAssignmentList(null);

		//disbursement Dates
		List<FinanceDisbursement> disbList = financeDetail.getFinScheduleData().getDisbursementDetails();
		Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
			@Override
			public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
				return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
			}
		});

		if (disbList != null && disbList.size() > 0) {
			if (disbList.size() == 1) {
				financeDetail.getFinScheduleData().getFinanceMain().setFirstDisbDate(disbList.get(0).getDisbDate());
				financeDetail.getFinScheduleData().getFinanceMain().setLastDisbDate(disbList.get(0).getDisbDate());
			} else {
				financeDetail.getFinScheduleData().getFinanceMain().setFirstDisbDate(disbList.get(0).getDisbDate());
				financeDetail.getFinScheduleData().getFinanceMain()
				.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}
		}

		if (StringUtils.equals(APIConstants.STMT_ACCOUNT, servicName)) {
			List<FinFeeDetail> finFeeDetail = financeDetail.getFinScheduleData().getFinFeeDetailList();
			financeDetail.setFinFeeDetails(finFeeDetail);
		}

		// Fetch summary details
		FinanceSummary summary = getFinanceSummary(financeDetail);
		financeDetail.getFinScheduleData().setFinanceSummary(summary);

		financeDetail.getFinScheduleData().setDisbursementDetails(null);
		financeDetail.getFinScheduleData().setFinFeeDetailList(null);
		if (!StringUtils.equals(APIConstants.STMT_FORECLOSURE, servicName)) {
			financeDetail.getFinScheduleData().setFinODDetails(null);
		}
		financeDetail.getFinScheduleData().setFinODPenaltyRate(null);

		// customer details
		CustomerDetails customerDetail = financeDetail.getCustomerDetails();
		customerDetail.setCustCIF(customerDetail.getCustomer().getCustCIF());
		customerDetail.setCustCoreBank(customerDetail.getCustomer().getCustCoreBank());
		customerDetail.setCustCtgCode(customerDetail.getCustomer().getCustCtgCode());
		customerDetail.setCustDftBranch(customerDetail.getCustomer().getCustDftBranch());
		customerDetail.setCustBaseCcy(customerDetail.getCustomer().getCustBaseCcy());
		customerDetail.setPrimaryRelationOfficer(customerDetail.getCustomer().getCustRO1());
		customerDetail.setCustomer(customerDetail.getCustomer());
		customerDetail.setCustEmployeeDetail(null);
		customerDetail.setCustomerExtLiabilityList(null);
		customerDetail.setCustomerIncomeList(null);
		customerDetail.setCustomerDocumentsList(null);
		customerDetail.setCustomerBankInfoList(null);
		customerDetail.setEmploymentDetailsList(null);
		customerDetail.setCustomerChequeInfoList(null);
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public void setFinServiceInstController(FinServiceInstController finServiceInstController) {
		this.finServiceInstController = finServiceInstController;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
}
