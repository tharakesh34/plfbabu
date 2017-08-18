package com.pennanttech.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
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

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private ManualAdviseDAO				manualAdviseDAO;
	private ReceiptService				receiptService;


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
					finScheduleData.setFinanceScheduleDetails(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails());
					// setting old values
					financeDetail.setCustomerDetails(aFinanceDetail.getCustomerDetails());
					finScheduleData.setFinanceMain(aFinanceDetail.getFinScheduleData().getFinanceMain());
					finScheduleData.setFinODDetails(aFinanceDetail.getFinScheduleData().getFinODDetails());
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
		List<FinFeeDetail> foreClosureFees = new ArrayList<FinFeeDetail>();
		List<FinFeeDetail> feeDues = new ArrayList<FinFeeDetail>();
		try {
			for (int i = 0; i < days; i++) {
				Cloner cloner = new Cloner();
				FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
				serviceInstruction.setFromDate(DateUtility.addDays(DateUtility.getAppDate(), i));
				aFinanceDetail = doProcessPayments(aFinanceDetail, serviceInstruction);

				scheduleData.setOutstandingPri(aFinanceDetail.getFinScheduleData().getOutstandingPri());
				feeDues = aFinanceDetail.getFinScheduleData().getFeeDues();
				foreClosureFees = aFinanceDetail.getFinScheduleData().getForeClosureFees();
				foreClosureList.add(aFinanceDetail.getForeClosureDetails().get(0));
				finOdDetaiList.add(aFinanceDetail.getFinScheduleData().getFinODDetails().get(0));
			}

			scheduleData.setFinReference(finReference);
			scheduleData.setFinODDetails(finOdDetaiList);
			scheduleData.setForeClosureFees(foreClosureFees);
			scheduleData.setFeeDues(feeDues);

			// process origination fees
			scheduleData.getFeeDues().addAll(getUpdatedFees(financeDetail.getFinScheduleData().getFinFeeDetailList()));

			finStmtDetail.setForeClosureDetails(foreClosureList);
			finStmtDetail.setFinScheduleData(scheduleData);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}

		return finStmtDetail;
	}

	private void processFeesAndCharges(FinScheduleData scheduleData,
			List<FinFeeDetail> finFeeDetails) throws IllegalAccessException, InvocationTargetException {
		// finance level fees and charges
		for (FinFeeDetail feeDetail : finFeeDetails) {
			feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
		}

		scheduleData.setFeeDues(finFeeDetails);
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
					feeDetail.setSchdDate(getBounceDueDate(advisedFees.getReceiptID()));
				} else {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_ADVISE);
				}
				feeDetail.setFeeTypeCode(advisedFees.getFeeTypeCode());
				feeDetail.setActualAmount(advisedFees.getAdviseAmount());
				feeDetail.setPaidAmount(advisedFees.getPaidAmount());
				feeDetail.setRemainingFee(advisedFees.getBalanceAmt());

				feeDues.add(feeDetail);
			}
			scheduleData.getFeeDues().addAll(feeDues);
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

	private void prepareResponse(FinanceDetail financeDetail, String servicName) throws IllegalAccessException, InvocationTargetException {
		financeDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
		financeDetail.getFinScheduleData().setFinReference(null);
		financeDetail.getFinScheduleData().setInsuranceList(null);
		financeDetail.getFinScheduleData().setStepPolicyDetails(null);
		financeDetail.getFinScheduleData().setPlanEMIHDates(null);
		financeDetail.getFinScheduleData().setPlanEMIHmonths(null);
		/*		if (!StringUtils.equals(APIConstants.STMT_REPAY_SCHD, servicName)) {
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(null);
		}*/
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
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			finScheduleData.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			processFeesAndCharges(finScheduleData, finFeeDetail);
			financeDetail.setFinFeeDetails(getUpdatedFees(financeDetail.getFinScheduleData().getFeeDues()));
			financeDetail.getFinScheduleData().setFeeDues(null);
		}

		// Fetch summary details
		FinanceSummary summary = getFinanceSummary(financeDetail);
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		summary.setAdvPaymentAmount(getTotalAdvAmount(finReference));
		summary.setOutStandPrincipal(financeDetail.getFinScheduleData().getOutstandingPri());
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

		//Repayments Posting Process Execution
		//=====================================
		Date valueDate = finServiceInst.getFromDate();

		List<ReceiptAllocationDetail> allocations = calEarlySettleAmount(finScheduleData, valueDate);
		BigDecimal totPriPayNow = BigDecimal.ZERO;
		BigDecimal totPftPayNow = BigDecimal.ZERO;
		BigDecimal totTdsReturn = BigDecimal.ZERO;
		BigDecimal totLatePftPayNow = BigDecimal.ZERO;
		BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
		BigDecimal totFeePayNow = BigDecimal.ZERO;

		for (ReceiptAllocationDetail ad : allocations) {
			if (StringUtils.equals(ad.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
				totPriPayNow = ad.getPaidAmount();
			} else if (StringUtils.equals(ad.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
				totPftPayNow = ad.getPaidAmount();
			} else if (StringUtils.equals(ad.getAllocationType(), RepayConstants.ALLOCATION_LPFT)) {
				totLatePftPayNow = ad.getPaidAmount();
			} else if (StringUtils.equals(ad.getAllocationType(), RepayConstants.ALLOCATION_ODC)) {
				totPenaltyPayNow = ad.getPaidAmount();
			} else if (StringUtils.equals(ad.getAllocationType(), RepayConstants.ALLOCATION_TDS)) {
				totTdsReturn = ad.getPaidAmount();
			} else if (StringUtils.equals(ad.getAllocationType(), RepayConstants.ALLOCATION_FEE)) {
				totFeePayNow = ad.getPaidAmount();
			}
		}

		// fore closure details
		List<ForeClosure> foreClosureList = new ArrayList<ForeClosure>();
		ForeClosure foreClosure = new ForeClosure();
		foreClosure.setValueDate(finServiceInst.getFromDate());
		BigDecimal foreCloseAmt = totPriPayNow.add(totPenaltyPayNow).add(totPftPayNow).add(totLatePftPayNow).add(totFeePayNow).subtract(totTdsReturn);
		BigDecimal totServFees = BigDecimal.ZERO;

		// foreclosure fees
		List<FinFeeDetail> foreClosureFees = new ArrayList<FinFeeDetail>();
		finScheduleData.getFinanceMain().setFinSourceID(AccountEventConstants.ACCEVENT_EARLYSTL);
		feeDetailService.doExecuteFeeCharges(financeDetail, AccountEventConstants.ACCEVENT_EARLYSTL, null);
		if (finScheduleData.getFinFeeDetailList() != null) {
			for (FinFeeDetail fee : finScheduleData.getFinFeeDetailList()) {
				if (StringUtils.equals(fee.getFinEvent(), AccountEventConstants.ACCEVENT_EARLYSTL)) {
					totServFees = totServFees.add(fee.getActualAmount());
					if (StringUtils.equals(fee.getFeeScheduleMethod(), PennantConstants.List_Select)) {
						fee.setFeeScheduleMethod(null);
					}
					foreClosureFees.add(fee);
				}
				fee.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
			}
			finScheduleData.setForeClosureFees(foreClosureFees);
		}

		// Bounce and manual advice fees if applicable
		List<FinFeeDetail> feeDues = new ArrayList<>();
		String finReference = finScheduleData.getFinReference();
		BigDecimal totBounceFees = BigDecimal.ZERO;

		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finReference,
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_View");
		if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
			for (ManualAdvise advisedFees : manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				if (advisedFees.getBounceID() > 0) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_BOUNCE);
					feeDetail.setSchdDate(getBounceDueDate(advisedFees.getReceiptID()));
				} else {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_ADVISE);
				}
				feeDetail.setFeeTypeCode(advisedFees.getFeeTypeCode());
				feeDetail.setActualAmount(advisedFees.getAdviseAmount());
				feeDetail.setPaidAmount(advisedFees.getPaidAmount());
				feeDetail.setRemainingFee(advisedFees.getBalanceAmt());
				feeDues.add(feeDetail);

				totBounceFees = totBounceFees.add(advisedFees.getAdviseAmount().subtract(advisedFees.getPaidAmount()));
			}
			finScheduleData.getFeeDues().addAll(feeDues);
		}

		foreCloseAmt = foreCloseAmt.add(totServFees).add(totBounceFees);
		foreClosure.setForeCloseAmount(foreCloseAmt);
		foreClosure.setAccuredIntTillDate(totPftPayNow);
		foreClosure.setChargeAmount(totPenaltyPayNow);
		foreClosureList.add(foreClosure);

		// penalty details
		List<FinODDetails> finODDetailList = new ArrayList<FinODDetails>();
		FinODDetails finoDetail = new FinODDetails();
		finoDetail.setFinCurODAmt(totPenaltyPayNow);
		finODDetailList.add(finoDetail);

		finScheduleData.setOutstandingPri(totPriPayNow);
		financeDetail.setForeClosureDetails(foreClosureList);
		finScheduleData.setFinODDetails(finODDetailList);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for fetch Schedule Date against the presentment bounce charge
	 * 
	 * @param receiptId
	 * @return
	 */
	private Date getBounceDueDate(long receiptId) {
		Date schdDate = manualAdviseDAO.getPresentmentBounceDueDate(receiptId);
		return schdDate;
	}

	/**
	 * Method for calculating Schedule Total and Unpaid amounts based on Schedule Details
	 */
	private List<ReceiptAllocationDetail> calEarlySettleAmount(FinScheduleData finScheduleData, Date valueDate) {
		logger.debug("Entering");

		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if(finScheduleData.getFinanceMain().isTDSApplicable()){

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
			}
		}

		List<FinanceScheduleDetail> scheduleDetails = finScheduleData.getFinanceScheduleDetails();
		List<ReceiptAllocationDetail> allocations = new ArrayList<>();
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);

		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal pftAccruedTillNow = BigDecimal.ZERO;
		BigDecimal tdsAccruedTillNow = BigDecimal.ZERO;
		BigDecimal priBalance = BigDecimal.ZERO;
		BigDecimal totFeeAmount = BigDecimal.ZERO;
		boolean partAccrualReq = true;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;
		for (int i = 0; i < tempScheduleDetails.size(); i++) {
			curSchd = tempScheduleDetails.get(i);
			if(i != 0){
				prvSchd = tempScheduleDetails.get(i - 1);
			}
			Date schdDate = curSchd.getSchDate();

			// Accrued Profit Calculation
			if (DateUtility.compare(schdDate, valueDate) < 0) {
				pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitSchd());
				priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));

				if(finScheduleData.getFinanceMain().isTDSApplicable()){
					BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
					BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
					tdsAccruedTillNow = tdsAccruedTillNow.add(pft.subtract(actualPft));
				}

			}else if (DateUtility.compare(valueDate, schdDate) == 0) {

				BigDecimal remPft = curSchd.getProfitCalc();
				pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitCalc());
				if(prvSchd != null){
					remPft = remPft.add(prvSchd.getProfitBalance());
					pftAccruedTillNow = pftAccruedTillNow.add(prvSchd.getProfitBalance());
				}
				priBalance = priBalance.add(curSchd.getPrincipalSchd().add(curSchd.getClosingBalance())).subtract(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid());

				if(finScheduleData.getFinanceMain().isTDSApplicable()){
					BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
					tdsAccruedTillNow = tdsAccruedTillNow.add(remPft.subtract(actualPft));
				}
				partAccrualReq = false;

			} else {
				if(partAccrualReq && prvSchd != null){
					partAccrualReq = false;
					BigDecimal accruedPft = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate, curSchd.getBalanceForPftCal(),
							prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
					accruedPft = accruedPft.add(prvSchd.getProfitFraction());
					accruedPft = CalculationUtil.roundAmount(accruedPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
							finScheduleData.getFinanceMain().getRoundingTarget());
					pftAccruedTillNow = pftAccruedTillNow.add(accruedPft).add(prvSchd.getProfitBalance());

					priBalance = priBalance.add(prvSchd.getClosingBalance());

					if(finScheduleData.getFinanceMain().isTDSApplicable()){
						BigDecimal actualPft = (accruedPft.add(prvSchd.getProfitBalance())).divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						tdsAccruedTillNow = tdsAccruedTillNow.add(accruedPft.add(prvSchd.getProfitBalance()).subtract(actualPft));
					}
				}
			}
			pftPaid = pftPaid.add(curSchd.getSchdPftPaid());
			totFeeAmount = totFeeAmount.add(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()));
		}

		// Principal Amount
		BigDecimal pftAmt = BigDecimal.ZERO;
		pftAmt = pftAccruedTillNow.subtract(pftPaid);

		ReceiptAllocationDetail ad = new ReceiptAllocationDetail();
		ad.setAllocationType(RepayConstants.ALLOCATION_PRI);
		ad.setPaidAmount(priBalance);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(RepayConstants.ALLOCATION_PFT);
		ad.setPaidAmount(pftAmt);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(RepayConstants.ALLOCATION_TDS);
		ad.setPaidAmount(tdsAccruedTillNow);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(RepayConstants.ALLOCATION_FEE);
		ad.setPaidAmount(totFeeAmount);
		allocations.add(ad);

		// Calculate overdue Penalties
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
		if (DateUtility.compare(valueDate, DateUtility.getAppDate()) != 0) {
			if(overdueList != null) {
				for(FinODDetails odDetail: overdueList) {
					if(odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
						overdueList = getReceiptService().getValueDatePenalties(finScheduleData, priBalance.add(pftAmt).add(totFeeAmount),
								valueDate, null, true);
					}
				}
			}
		}

		// Calculating Actual Sum of Penalty Amount & Late Pay Interest
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal penaltyBal = BigDecimal.ZERO;
		if(overdueList != null && !overdueList.isEmpty()){
			for (int i = 0; i < overdueList.size(); i++) {
				FinODDetails finODDetail = overdueList.get(i);
				if (finODDetail.getFinODSchdDate().compareTo(valueDate)>0) {
					continue;
				}
				latePayPftBal = latePayPftBal.add(finODDetail.getLPIBal());
				penaltyBal = penaltyBal.add(finODDetail.getTotPenaltyBal());
			}
		}

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(RepayConstants.ALLOCATION_ODC);
		ad.setPaidAmount(penaltyBal);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(RepayConstants.ALLOCATION_LPFT);
		ad.setPaidAmount(latePayPftBal);
		allocations.add(ad);

		logger.debug("Leaving");
		return allocations;
	}

	/**
	 * Method for Sorting Schedule Details
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
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

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}
}
