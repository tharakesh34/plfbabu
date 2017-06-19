package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.LatePayPenaltyService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RepayCalculator;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

public class FinStatementController extends SummaryDetailService {

	private static final Logger			logger	= Logger.getLogger(FinStatementController.class);

	private FinanceDetailService		financeDetailService;
	private PostingsDAO					postingsDAO;
	private CollateralSetupService		collateralSetupService;
	private FeeDetailService			feeDetailService;

	// TODO: cleanup required(DDP)
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private RepayCalculator				repayCalculator;
	private ManualPaymentService		manualPaymentService;
	private ManualAdviseDAO				manualAdviseDAO;
	private FinExcessAmountDAO			finExcessAmountDAO;
	private LatePayPenaltyService		latePayPenaltyService;
	private FinanceProfitDetailDAO		profitDetailsDAO;


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
						List<CollateralSetup> collatSetupList = collateralSetupService
								.getCollateralSetupByCustId(custId);
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
						List<CollateralSetup> collatSetupList = collateralSetupService
								.getCollateralSetupByCustId(custId);
						financeDetail.setCollateralSetup(collatSetupList);
					}
				}

				if (StringUtils.equals(APIConstants.STMT_FORECLOSURE, serviceName)) {
					Cloner cloner = new Cloner();
					FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
					//get FinODDetails
					List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
					aFinanceDetail.getFinScheduleData().setFinODDetails(finODDetailsList);
					financeDetail.getFinScheduleData().setFinODDetails(finODDetailsList);
					financeDetail = getForeClosureDetails(financeDetail, days);

					FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
					// setting old values
					financeDetail.setCustomerDetails(aFinanceDetail.getCustomerDetails());
					finScheduleData.setFinanceMain(aFinanceDetail.getFinScheduleData().getFinanceMain());
					finScheduleData.setFinODDetails(aFinanceDetail.getFinScheduleData().getFinODDetails());

					// fetch excess amount
					FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
							RepayConstants.EXAMOUNTTYPE_EXCESS);
					if (finExcessAmount != null) {
						finScheduleData.setExcessAmount(finExcessAmount.getAmount());
					}

					// calculate advPaymentAmount, outstandingPri, overduePft and tdPftAccured
					FinanceProfitDetail finPftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsForSummary(finReference);
					if (finPftDetail == null) {
						finPftDetail = new FinanceProfitDetail();
						finPftDetail.setFinStartDate(finScheduleData.getFinanceMain().getFinStartDate());
						finPftDetail = getAccrualService().calProfitDetails(finScheduleData.getFinanceMain(),
								financeDetail.getFinScheduleData().getFinanceScheduleDetails(), finPftDetail,
								DateUtility.getAppDate());
					}

					finScheduleData.setAdvPaymentAmount(
							finPftDetail.getTotalPftPaidInAdv().add(finPftDetail.getTotalPriPaidInAdv()));
					finScheduleData.setOutstandingPri(finPftDetail.getTotalPriBal());
					finScheduleData.setOverduePft(finPftDetail.getODProfit());
					finScheduleData
							.setTdPftAccured(finPftDetail.getAmzTillLBD().subtract(finPftDetail.getTdSchdPftPaid()));
				}

				// generate response info
				prepareResponse(financeDetail, serviceName);

				stmtResponse.setCustomer(financeDetail.getCustomerDetails());
				financeDetail.setCustomerDetails(null);
				financeDetail.setJountAccountDetailList(null);
				financeDetail.setCollateralAssignmentList(null);
				financeDetail.getFinScheduleData().setVasRecordingList(null);
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
		List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>();
		try {
			for (int i = 0; i < days; i++) {
				Cloner cloner = new Cloner();
				FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
				serviceInstruction.setFromDate(DateUtility.addDays(DateUtility.getAppDate(), i));
				aFinanceDetail = doProcessPayments(aFinanceDetail, serviceInstruction);
				finFeeDetails = aFinanceDetail.getFinScheduleData().getFinFeeDetailList();
				foreClosureList.add(aFinanceDetail.getForeClosureDetails().get(0));
				finOdDetaiList.add(aFinanceDetail.getFinScheduleData().getFinODDetails().get(0));
			}

			scheduleData.setFinReference(finReference);
			scheduleData.setFinODDetails(finOdDetaiList);

			// process fees and charges
			processFeesAndCharges(financeDetail, scheduleData, finFeeDetails);

			finStmtDetail.setForeClosureDetails(foreClosureList);
			finStmtDetail.setFinScheduleData(scheduleData);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}

		return finStmtDetail;
	}

	private void processFeesAndCharges(FinanceDetail financeDetail, FinScheduleData scheduleData,
			List<FinFeeDetail> finFeeDetails) throws IllegalAccessException, InvocationTargetException {
		// finance level fees and charges
		scheduleData.setFeeDues(finFeeDetails);
		for (FinFeeDetail feeDetail : scheduleData.getFeeDues()) {
			feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
		}

		String finReference = scheduleData.getFinReference();
		List<FinFeeDetail> feeDues = new ArrayList<>();

		// Bounce and manual advice fees if applicable
		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finReference,
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_View");
		if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
			for (ManualAdvise advisedFees : manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				if (advisedFees.getBounceID() > 0) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_BOUNCE);
				} else {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_ADVISE);
				}
				feeDetail.setFeeTypeCode(advisedFees.getFeeTypeCode());
				feeDetail.setActualAmount(advisedFees.getAdviseAmount());
				feeDetail.setPaidAmount(advisedFees.getPaidAmount());
				feeDetail.setRemainingFee(advisedFees.getBalanceAmt());

				feeDues.add(feeDetail);
			}
			scheduleData.setFeeDues(feeDues);
		}

		// foreclosure fees
		List<FinFeeDetail> foreClosureFees = new ArrayList<FinFeeDetail>();
		financeDetail.getFinScheduleData().getFinanceMain().setFinSourceID(AccountEventConstants.ACCEVENT_EARLYSTL);
		feeDetailService.doExecuteFeeCharges(financeDetail, AccountEventConstants.ACCEVENT_EARLYSTL);
		if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
			for (FinFeeDetail fee : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				if (StringUtils.equals(fee.getFinEvent(), AccountEventConstants.ACCEVENT_EARLYSTL)) {
					if (StringUtils.equals(fee.getFeeScheduleMethod(), PennantConstants.List_Select)) {
						fee.setFeeScheduleMethod(null);
					}
					foreClosureFees.add(fee);
				}
			}
			scheduleData.setForeClosureFees(foreClosureFees);
		}
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

	/**
	 * Method for process Early settlement and partial payment requests
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * @return
	 * @throws Exception
	 */
	public FinanceDetail doProcessPayments(FinanceDetail financeDetail, FinServiceInstruction finServiceInst)
			throws Exception {
		logger.debug("Entering");

		if (finServiceInst.getFromDate() == null) {
			finServiceInst.setFromDate(DateUtility.getAppDate());
		}

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		RepayData repayData = new RepayData();
		repayData.setBuildProcess("R");
		repayData.setFinanceDetail(financeDetail);
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		Date valueDate = finServiceInst.getFromDate();

		// Initiate Repay calculations
		repayData.getRepayMain().setRepayAmountNow(finServiceInst.getAmount());
		repayData = repayCalculator.initiateRepay(repayData, financeMain, financeScheduleDetails, "", null, false,
				finServiceInst.getRecalType(), valueDate, finServiceInst.getModuleDefiner());
		repayData.setRepayMain(repayData.getRepayMain());

		String finEvent = AccountEventConstants.ACCEVENT_EARLYSTL;
		repayData.setEventCodeRef(finEvent);
		// call change frequency service
		manualPaymentService.doCalcRepayments(repayData, financeDetail, finServiceInst);

		FinScheduleData scheduleData = repayData.getFinanceDetail().getFinScheduleData();

		//Repayments Posting Process Execution
		//=====================================
		FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
		financeMain.setRepayAccountId(finRepayHeader.getRepayAccountId());
		Date valuedate = finServiceInst.getFromDate();

		FinanceProfitDetail tempPftDetail = profitDetailsDAO.getFinProfitDetailsById(financeMain.getFinReference());
		getAccrualService().calProfitDetails(financeMain, scheduleData.getFinanceScheduleDetails(), tempPftDetail,
				valuedate);

		List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();
		BigDecimal totPriPayNow = BigDecimal.ZERO;
		BigDecimal totPftPayNow = BigDecimal.ZERO;
		BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
		for (RepayScheduleDetail repayShdData : repaySchdList) {
			totPriPayNow = totPriPayNow.add(repayShdData.getPrincipalSchdPayNow());
			totPenaltyPayNow = totPenaltyPayNow.add(repayShdData.getPenaltyPayNow());
		}
		totPftPayNow = totPftPayNow.add(tempPftDetail.getPftAmz().subtract(tempPftDetail.getTdSchdPftPaid()));

		// fore closure details
		List<ForeClosure> foreClosureList = new ArrayList<ForeClosure>();
		ForeClosure foreClosure = new ForeClosure();
		foreClosure.setValueDate(finServiceInst.getFromDate());
		foreClosure.setForeCloseAmount(totPriPayNow.add(totPenaltyPayNow).add(totPftPayNow));
		foreClosure.setAccuredIntTillDate(totPftPayNow);

		// calculate total penalty amount
		List<FinODDetails> finOdDetails = financeDetail.getFinScheduleData().getFinODDetails();
		BigDecimal totPenaltyAmt = BigDecimal.ZERO;
		if (finOdDetails != null) {
			for (FinODDetails odDetails : finOdDetails) {
				FinODDetails detail = latePayPenaltyService.computeLPP(odDetails, finServiceInst.getFromDate(),
						financeMain.getProfitDaysBasis(), financeScheduleDetails,null, financeMain.getCalRoundingMode(),
						financeMain.getRoundingTarget());
				totPenaltyAmt = totPenaltyAmt.add(detail.getTotPenaltyAmt());
			}
		}
		foreClosure.setChargeAmount(totPenaltyAmt);
		foreClosureList.add(foreClosure);

		// penalty details
		List<FinODDetails> finODDetailList = new ArrayList<FinODDetails>();
		FinODDetails finoDetail = new FinODDetails();
		finoDetail.setFinCurODAmt(totPenaltyPayNow);
		finODDetailList.add(finoDetail);

		financeDetail.setForeClosureDetails(foreClosureList);
		financeDetail.getFinScheduleData().setFinODDetails(finODDetailList);

		logger.debug("Leaving");
		return financeDetail;
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

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
	}

	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setLatePayPenaltyService(LatePayPenaltyService latePayPenaltyService) {
		this.latePayPenaltyService = latePayPenaltyService;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}
}
