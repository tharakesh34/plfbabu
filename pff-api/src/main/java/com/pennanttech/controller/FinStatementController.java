package com.pennanttech.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiObjectFactoryBean;

import com.aspose.words.SaveFormat;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportCreationUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.reports.ReportConfigurationDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ForeClosureReport;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

import net.sf.jasperreports.engine.JasperRunManager;

public class FinStatementController extends SummaryDetailService {
	private static final Logger logger = LogManager.getLogger(FinStatementController.class);

	private FinanceDetailService financeDetailService;
	private PostingsDAO postingsDAO;
	private CollateralSetupService collateralSetupService;
	private FeeDetailService feeDetailService;

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private ReceiptService receiptService;
	private SOAReportGenerationService soaReportGenerationService;
	private InterestCertificateService interestCertificateService;
	private ReportConfigurationDAO reportConfigurationDAO;
	private ReportConfiguration reportConfiguration;
    private CustomerDetailsService customerDetailsService;
	/**
	 * get the FinStatement Details by the given FinReferences.
	 * 
	 * @param finreferencecList
	 * @throws ServiceException
	 */
	public FinStatementResponse getStatement(List<String> finReferences, String serviceName) {
		logger.debug(Literal.ENTERING);

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
					List<ReturnDataSet> postingsList = postingsDAO.getPostingsByFinRef(finReference, true);
					financeDetail.setReturnDataSetList(postingsList);
				}
				if (StringUtils.equals(APIConstants.STMT_INST_CERT, serviceName)) {
					long custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
					if (custId != 0) {
						List<CollateralSetup> collatSetupList = collateralSetupService
								.getCollateralSetupByCustId(custId);
						financeDetail.setCollaterals(collatSetupList);
					}
				}

				// generate response info
				prepareResponse(financeDetail, serviceName);

				// for logging purpose
				if (StringUtils.isNotBlank(financeDetail.getFinReference())) {
					APIErrorHandlerService.logReference(financeDetail.getFinReference());
				}

				stmtResponse.setCustomer(financeDetail.getCustomerDetails());
				financeDetail.setCustomerDetails(null);
				finDetailList.add(financeDetail);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stmtResponse;
		}
		stmtResponse.setFinance(finDetailList);
		stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return stmtResponse;
	}

	/**
	 * get the FinStatement Details by the given FinReferences.
	 * 
	 * @param finreferencecList
	 * @throws ServiceException
	 */
	public FinStatementResponse getStatement(List<String> finReferences, String serviceName, int days, Date fromDate) {
		logger.debug(Literal.ENTERING);

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
					List<ReturnDataSet> postingsList = postingsDAO.getPostingsByFinRef(finReference, true);
					financeDetail.setReturnDataSetList(postingsList);
				}
				if (StringUtils.equals(APIConstants.STMT_INST_CERT, serviceName)) {
					long custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
					if (custId != 0) {
						List<CollateralSetup> collatSetupList = collateralSetupService
								.getCollateralSetupByCustId(custId);
						financeDetail.setCollaterals(collatSetupList);
					}
				}

				if (StringUtils.equals(APIConstants.STMT_FORECLOSURE, serviceName)) {
					Cloner cloner = new Cloner();
					FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
					//get FinODDetails
					List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
					aFinanceDetail.getFinScheduleData().setFinODDetails(finODDetailsList);
					financeDetail.getFinScheduleData().setFinODDetails(finODDetailsList);
					financeDetail = getForeClosureDetails(financeDetail, days, fromDate);

					FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
					finScheduleData
							.setFinanceScheduleDetails(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails());
					// setting old values
					financeDetail.setCustomerDetails(aFinanceDetail.getCustomerDetails());
					finScheduleData.setFinanceMain(aFinanceDetail.getFinScheduleData().getFinanceMain());
					finScheduleData.setFinODDetails(aFinanceDetail.getFinScheduleData().getFinODDetails());
				}
				
				if (StringUtils.equals(APIConstants.STMT_FORECLOSUREV1, serviceName)) {
					FinReceiptData receiptData = receiptService.getFinReceiptDataById(finReference,
							AccountEventConstants.ACCEVENT_EARLYSTL, FinanceConstants.FINSER_EVENT_RECEIPT, "");
					getForeClosureReport(receiptData, stmtResponse);
				}
				// generate response info
				prepareResponse(financeDetail, serviceName);

				// for logging purpose
				if (StringUtils.isNotBlank(financeDetail.getFinReference())) {
					APIErrorHandlerService.logReference(financeDetail.getFinReference());
				}

				stmtResponse.setCustomer(financeDetail.getCustomerDetails());
				financeDetail.setCustomerDetails(null);
				financeDetail.setJountAccountDetailList(null);
				financeDetail.setCollateralAssignmentList(null);
				financeDetail.getFinScheduleData().setVasRecordingList(null);
				finDetailList.add(financeDetail);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stmtResponse;
		}
		stmtResponse.setFinance(finDetailList);
		stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return stmtResponse;
	}

	/**
	 * 
	 * @param financeDetail
	 * @return
	 * @throws Exception
	 */
	private FinanceDetail getForeClosureDetails(FinanceDetail financeDetail, int days, Date fromDate) throws Exception {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		String finReference = finMain.getFinReference();

		// Fetch Total Repayment Amount till Maturity date for Early Settlement
		BigDecimal repayAmt = financeScheduleDetailDAO.getTotalRepayAmount(finReference);

		FinServiceInstruction serviceInstruction = new FinServiceInstruction();
		serviceInstruction.setAmount(repayAmt);
		serviceInstruction.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSTLENQ);
		finMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_EARLYSTLENQ);
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
			if (fromDate != null) {
				Cloner cloner = new Cloner();
				FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
				serviceInstruction.setFromDate(fromDate);
				aFinanceDetail = doProcessPayments(aFinanceDetail, serviceInstruction);

				scheduleData.setOutstandingPri(aFinanceDetail.getFinScheduleData().getOutstandingPri());
				feeDues = aFinanceDetail.getFinScheduleData().getFeeDues();
				foreClosureFees = aFinanceDetail.getFinScheduleData().getForeClosureFees();
				foreClosureList.add(aFinanceDetail.getForeClosureDetails().get(0));
				finOdDetaiList.add(aFinanceDetail.getFinScheduleData().getFinODDetails().get(0));
			} else {
				for (int i = 0; i < days; i++) {
					Cloner cloner = new Cloner();
					FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);
					serviceInstruction.setFromDate(DateUtility.addDays(SysParamUtil.getAppDate(), i));
					aFinanceDetail = doProcessPayments(aFinanceDetail, serviceInstruction);

					scheduleData.setOutstandingPri(aFinanceDetail.getFinScheduleData().getOutstandingPri());
					feeDues = aFinanceDetail.getFinScheduleData().getFeeDues();
					foreClosureFees = aFinanceDetail.getFinScheduleData().getForeClosureFees();
					foreClosureList.add(aFinanceDetail.getForeClosureDetails().get(0));
					finOdDetaiList.add(aFinanceDetail.getFinScheduleData().getFinODDetails().get(0));
				}
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
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
		return finStmtDetail;
	}

	private void processFeesAndCharges(FinScheduleData scheduleData, List<FinFeeDetail> finFeeDetails)
			throws IllegalAccessException, InvocationTargetException {
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
		return getStatement(references, serviceName, statementRequest.getDays(), statementRequest.getFromDate());
	}

	private void prepareResponse(FinanceDetail financeDetail, String servicName)
			throws IllegalAccessException, InvocationTargetException {
		financeDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
		financeDetail.getFinScheduleData().setFinReference(null);
		financeDetail.getFinScheduleData().setInsuranceList(null);
		financeDetail.getFinScheduleData().setStepPolicyDetails(null);
		financeDetail.getFinScheduleData().setPlanEMIHDates(null);
		financeDetail.getFinScheduleData().setPlanEMIHmonths(null);
		/*
		 * if (!StringUtils.equals(APIConstants.STMT_REPAY_SCHD, servicName)) {
		 * financeDetail.getFinScheduleData().setFinanceScheduleDetails(null); }
		 */
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
		} else {
			// #BUG Fix related to 134031
			for (ForeClosure detail : financeDetail.getForeClosureDetails()) {
				detail.getForeCloseAmount().subtract(summary.getAdvPaymentAmount());
				detail.setForeCloseAmount(detail.getForeCloseAmount().subtract(summary.getAdvPaymentAmount()));
			}
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
		logger.debug(Literal.ENTERING);

		if (finServiceInst.getFromDate() == null) {
			finServiceInst.setFromDate(SysParamUtil.getAppDate());
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
			String allocationType = ad.getAllocationType();
			if (RepayConstants.ALLOCATION_PRI.equals(allocationType)) {
				totPriPayNow = ad.getPaidAmount();
			} else if (RepayConstants.ALLOCATION_PFT.equals(allocationType)) {
				totPftPayNow = ad.getPaidAmount();
			} else if (RepayConstants.ALLOCATION_LPFT.equals(allocationType)) {
				totLatePftPayNow = ad.getPaidAmount();
			} else if (RepayConstants.ALLOCATION_ODC.equals(allocationType)) {
				totPenaltyPayNow = ad.getPaidAmount();
			} else if (RepayConstants.ALLOCATION_TDS.equals(allocationType)) {
				totTdsReturn = ad.getPaidAmount();
			} else if (RepayConstants.ALLOCATION_FEE.equals(allocationType)) {
				totFeePayNow = ad.getPaidAmount();
			}
		}

		// fore closure details
		List<ForeClosure> foreClosureList = new ArrayList<>();
		ForeClosure foreClosure = new ForeClosure();
		foreClosure.setValueDate(DateUtility.getTimestamp(finServiceInst.getFromDate()));
		BigDecimal foreCloseAmt = totPriPayNow.add(totPenaltyPayNow).add(totPftPayNow).add(totFeePayNow)
				.subtract(totTdsReturn);
		BigDecimal totServFees = BigDecimal.ZERO;

		// foreclosure fees
		List<FinFeeDetail> foreClosureFees = new ArrayList<>();
		finScheduleData.getFinanceMain().setFinSourceID(AccountEventConstants.ACCEVENT_EARLYSTL);
		feeDetailService.doExecuteFeeCharges(financeDetail, AccountEventConstants.ACCEVENT_EARLYSTL, null, true);
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
		BigDecimal bounceAmount = BigDecimal.ZERO;
		BigDecimal totBounceFees = BigDecimal.ZERO;
		BigDecimal totReceivableAdFee = BigDecimal.ZERO;

		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finReference,
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_View");

		if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
			for (ManualAdvise advisedFees : manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				if (advisedFees.getBounceID() > 0) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_BOUNCE);
					feeDetail.setFeeID(advisedFees.getAdviseID());
					feeDetail.setSchdDate(getBounceDueDate(advisedFees.getReceiptID()));
					bounceAmount = bounceAmount
							.add(advisedFees.getAdviseAmount().subtract(advisedFees.getPaidAmount()));
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
		totReceivableAdFee = totBounceFees.subtract(bounceAmount);

		foreClosure.setBounceCharge(bounceAmount);
		foreClosure.setReceivableADFee(totReceivableAdFee);
		foreClosure.setLPIAmount(totLatePftPayNow);
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

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	
	public FinStatementResponse getForeClosureReport(FinReceiptData receiptData,
			FinStatementResponse finStmtResponse) {
		logger.debug(Literal.ENTERING);
		ForeClosureReport closureReport = new ForeClosureReport();
		try {
			FinanceDetail finDetails = receiptData.getFinanceDetail();
			FinanceMain financeMain = finDetails.getFinScheduleData().getFinanceMain();
			FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
			receiptHeader.setReference(financeMain.getFinReference());
			receiptHeader.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptHeader.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
			receiptHeader.setReceiptDate(SysParamUtil.getAppDate());
			receiptHeader.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
			receiptHeader.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
			receiptHeader.setNewRecord(true);
			FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
			finReceiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			finReceiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptHeader.getReceiptDetails().add(finReceiptDetail);
			receiptHeader.setValueDate(SysParamUtil.getAppDate());
			receiptData.setReceiptHeader(receiptHeader);
			receiptData.setFinReference(financeMain.getFinReference());
			receiptData.setBuildProcess("I");
			receiptData.setValueDate(SysParamUtil.getAppDate());
			receiptData.setReceiptHeader(receiptHeader);
			receiptData.setForeClosureEnq(true);
			receiptData = receiptService.calcuateDues(receiptData);

			// Setting Actual Percentage in Fore closure Letter Report.
			if (CollectionUtils.isNotEmpty(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList())) {
				FinFeeDetail finFeeDetail = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()
						.get(0);
				closureReport.setActPercentage(finFeeDetail.getActPercentage());
			}

			if (financeMain != null) {
				Date applDate = SysParamUtil.getAppDate();
				String appDate = DateFormatUtils.format(applDate, "dd MMMM yyyy");
				String disDate = DateFormatUtils.format(financeMain.getFinStartDate(), "dd'th' MMMM yyyy");
				Date chrgTillDate;
				int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
				String finCCy = SysParamUtil.getAppCurrency();
				int validityDays = SysParamUtil.getValueAsInt(SMTParameterConstants.FORECLOSURE_VALIDITY_DAYS);
				Date addDays = DateUtility.addDays(applDate, validityDays);
				String validTill = DateFormatUtils.format(addDays, "dd.MM.yyyy");

				Date prvEmiDate = receiptData.getOrgFinPftDtls().getPrvRpySchDate();
				chrgTillDate = applDate;
				int noOfIntDays = DateUtility.getDaysBetween(chrgTillDate, prvEmiDate);

				closureReport.setCalDate(appDate);
				closureReport.setValidTill(validTill);
				closureReport.setFinReference(financeMain.getFinReference());
				closureReport.setVanNumber(financeMain.getVanCode() == null ? "" : financeMain.getVanCode());
				closureReport.setFinAmount(PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter));
				closureReport.setFinAmountInWords(NumberToEnglishWords.getAmountInText(
						PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter), finCCy));
				closureReport.setFinAssetValue(
						PennantApplicationUtil.formateAmount(financeMain.getFinAssetValue(), formatter));
				closureReport.setFinAssetValueInWords(NumberToEnglishWords.getAmountInText(
						PennantApplicationUtil.formateAmount(financeMain.getFinAssetValue(), formatter), finCCy));

				closureReport.setDisbursalDate(disDate);
				closureReport.setChrgTillDate(DateFormatUtils.format(chrgTillDate, "MMM  dd,yyyy"));
				if (finDetails.getCustomerDetails() != null && finDetails.getCustomerDetails().getCustomer() != null) {
					closureReport.setCustName(finDetails.getCustomerDetails().getCustomer().getCustShrtName());
					closureReport.setCustCIF(finDetails.getCustomerDetails().getCustomer().getCustCIF());
					CustomerDetails customerDetails = customerDetailsService
							.getCustomerDetailsbyIdandPhoneType(finDetails.getCustomerDetails().getCustID(), "MOBILE");
					CustomerAddres custAdd = customerDetails.getAddressList().stream()
							.filter(addr -> addr.getCustAddrPriority() == 5).findFirst().orElse(new CustomerAddres());

					String combinedString = null;
					String custflatnbr = null;
					if (StringUtils.trimToEmpty(custAdd.getCustFlatNbr()).equals("")) {
						custflatnbr = " ";
					} else {
						custflatnbr = " " + StringUtils.trimToEmpty(custAdd.getCustFlatNbr()) + " ";
					}

					combinedString = StringUtils.trimToEmpty(custAdd.getCustAddrHNbr()) + " "
							+ StringUtils.trimToEmpty(custflatnbr) + " "
							+ StringUtils.trimToEmpty(custAdd.getCustAddrStreet()) + "\n"
							+ StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCityName()) + "\n"
							+ StringUtils.trimToEmpty(custAdd.getLovDescCustAddrProvinceName()) + "-"
							+ StringUtils.trimToEmpty(custAdd.getCustAddrZIP()) + "\n"
							+ StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCountryName());

					closureReport.setAddress(combinedString);

					closureReport.setCustAddrHNbr(StringUtils.trimToEmpty(custAdd.getCustAddrHNbr()));
					closureReport.setCustFlatNo(StringUtils.trimToEmpty(custflatnbr));
					closureReport.setCustAddrStreet(StringUtils.trimToEmpty(custAdd.getCustAddrStreet()));
					closureReport.setCustAddrCityName(StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCityName()));
					closureReport
							.setCustAddrProvinceName(StringUtils.trimToEmpty(custAdd.getLovDescCustAddrProvinceName()));
					closureReport.setCustAddrZIP(StringUtils.trimToEmpty(custAdd.getCustAddrZIP()));
					closureReport
							.setCustAddrCountryName(StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCountryName()));

					String salutation = finDetails.getCustomerDetails().getCustomer()
							.getLovDescCustSalutationCodeName();
					String nameString = StringUtils.trimToEmpty(salutation).equals("")
							? StringUtils.trimToEmpty(closureReport.getCustName())
							: StringUtils.trimToEmpty(salutation) + " "
									+ StringUtils.trimToEmpty(closureReport.getCustName());
					if (CollectionUtils.isNotEmpty(finDetails.getJountAccountDetailList())) {
						for (JointAccountDetail jointAccountDetail : finDetails.getJountAccountDetailList()) {
							if (StringUtils.isNotEmpty(nameString)) {
								nameString = nameString + "\n";
							}

							if (jointAccountDetail.getCustomerDetails() != null
									&& jointAccountDetail.getCustomerDetails().getCustomer() != null) {
								nameString = nameString + (StringUtils
										.trimToEmpty(jointAccountDetail.getCustomerDetails().getCustomer()
												.getLovDescCustSalutationCodeName())
										.equals("")
												? StringUtils.trimToEmpty(
														jointAccountDetail.getCustomerDetails().getCustomer()
																.getCustShrtName())
												: StringUtils
														.trimToEmpty(jointAccountDetail.getCustomerDetails()
																.getCustomer().getLovDescCustSalutationCodeName())
														+ " " + StringUtils.trimToEmpty(jointAccountDetail
																.getCustomerDetails().getCustomer().getCustShrtName()));
							}
						}
					}
					if (CollectionUtils.isNotEmpty(finDetails.getGurantorsDetailList())) {
						for (GuarantorDetail gurantorsDetail : finDetails.getGurantorsDetailList()) {
							if (StringUtils.isNotEmpty(nameString)) {
								nameString = nameString + "\n";
							}
							nameString = nameString + "\n"
									+ StringUtils.trimToEmpty(gurantorsDetail.getGuarantorCIFName());
						}
					}
					closureReport.setNameOftheBorrowers(nameString);

					String custSalutation = finDetails.getCustomerDetails().getCustomer()
							.getLovDescCustSalutationCodeName();
					closureReport.setCustSalutation(StringUtils.trimToEmpty(custSalutation));

					List<ReceiptAllocationDetail> receiptAllocationDetails = receiptData.getReceiptHeader()
							.getAllocationsSummary();
					Cloner cloner = new Cloner();
					receiptData.getFinanceDetail().getFinScheduleData()
							.setFinanceScheduleDetails(finDetails.getFinScheduleData().getFinanceScheduleDetails());
					FinReceiptData tempReceiptData = cloner.deepClone(receiptData);
					tempReceiptData.setForeClosureEnq(true);
					// setOrgReceiptData(tempReceiptData);
					receiptAllocationDetails = tempReceiptData.getReceiptHeader().getAllocationsSummary();

					BigDecimal receivableAmt = BigDecimal.ZERO;
					BigDecimal bncCharge = BigDecimal.ZERO;
					BigDecimal profitAmt = BigDecimal.ZERO;
					BigDecimal principleAmt = BigDecimal.ZERO;
					BigDecimal tdsAmt = BigDecimal.ZERO;
					BigDecimal futTdsAmt = BigDecimal.ZERO;

					for (ReceiptAllocationDetail receiptAllocationDetail : receiptAllocationDetails) {

						// Outstanding Principle
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_FUT_PRI)) {
							closureReport.setOutstandingPri(PennantApplicationUtil
									.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
							closureReport.setOutstandingPriInWords(
									NumberToEnglishWords.getAmountInText(PennantApplicationUtil
											.formateAmount(receiptAllocationDetail.getTotRecv(), formatter), finCCy));
						}

						// Late Payment Charges
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_ODC)) {
							closureReport.setLatePayCharges(PennantApplicationUtil
									.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
							closureReport.setLatePayChargesInWords(
									NumberToEnglishWords.getAmountInText(PennantApplicationUtil
											.formateAmount(receiptAllocationDetail.getTotRecv(), formatter), finCCy));
						}

						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_BOUNCE)) {
							bncCharge = receiptAllocationDetail.getTotRecv();
						}
						// Issue Fixed 141089
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_MANADV)) {
							receivableAmt = receivableAmt.add(receiptAllocationDetail.getTotRecv());
						}

						// Interest for the month
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_FUT_PFT)) {
							closureReport.setInstForTheMonth(PennantApplicationUtil
									.formateAmount(receiptAllocationDetail.getTotRecv(), formatter));
							closureReport.setInstForTheMonthInWords(
									NumberToEnglishWords.getAmountInText(PennantApplicationUtil
											.formateAmount(receiptAllocationDetail.getTotRecv(), formatter), finCCy));
						}

						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_PFT)) {
							profitAmt = receiptAllocationDetail.getTotRecv();
						}
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_PRI)) {
							principleAmt = receiptAllocationDetail.getTotRecv();
						}

						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_TDS)) {
							tdsAmt = receiptAllocationDetail.getTotRecv();
						}
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_FUT_TDS)) {
							futTdsAmt = receiptAllocationDetail.getTotRecv();
						}
						if (StringUtils.equals(receiptAllocationDetail.getAllocationType(),
								RepayConstants.ALLOCATION_FEE)) {
							BigDecimal fcFeeAmtWithGst = receiptAllocationDetail.getTotRecv();
							BigDecimal gstAmt = receiptAllocationDetail.getDueGST();
							BigDecimal fcFeeAmtWithoutGst = fcFeeAmtWithGst.subtract(gstAmt);
							BigDecimal foreClosureFee = closureReport.getForeClosFees().add(fcFeeAmtWithGst);
							closureReport
									.setForeClosFees(PennantApplicationUtil.formateAmount(foreClosureFee, formatter));
							closureReport.setForeClosFeesInWords(NumberToEnglishWords.getAmountInText(
									PennantApplicationUtil.formateAmount(foreClosureFee, formatter), finCCy));
							// GHF Calculate 18% GST on foreclosure fees
							closureReport.setGstOnForeClosFees((PennantApplicationUtil
									.formateAmount(fcFeeAmtWithoutGst, formatter).multiply(new BigDecimal(18)))
											.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));
							closureReport.setForeClosFeesExGST(
									closureReport.getForeClosFees().subtract(closureReport.getGstOnForeClosFees()));

							closureReport.setGstOnForeClosFees(closureReport.getGstOnForeClosFees());
						}

					}
					// Other Charges
					closureReport.setManualAdviceAmt(PennantApplicationUtil.formateAmount(receivableAmt, formatter));

					// Cheque Bounce Charges
					closureReport.setCheqBncCharges(PennantApplicationUtil.formateAmount(bncCharge, formatter));
					closureReport.setCheqBncChargesInWords(NumberToEnglishWords
							.getAmountInText(PennantApplicationUtil.formateAmount(bncCharge, formatter), finCCy));

					// Pending Installments
					closureReport.setPrincipalAmt(PennantApplicationUtil.formateAmount(principleAmt, formatter));
					closureReport.setInterestAmt(PennantApplicationUtil.formateAmount(profitAmt, formatter));
					closureReport.setPendingInsts(
							PennantApplicationUtil.formateAmount(profitAmt.add(principleAmt), formatter));

					// TDS
					closureReport.setTds(PennantApplicationUtil.formateAmount(tdsAmt.add(futTdsAmt), formatter));

					List<FinExcessAmount> excessList = receiptData.getReceiptHeader().getExcessAmounts();

					// Refunds (Excess Amount + EMI in advance)
					BigDecimal refund = BigDecimal.ZERO;
					for (FinExcessAmount finExcessAmount : excessList) {
						refund = refund.add(finExcessAmount.getBalanceAmt());
					}
					closureReport.setRefund(PennantApplicationUtil.formateAmount(refund, formatter));

					// Advance EMI
					for (FinExcessAmount finExcessAmount : excessList) {
						if (StringUtils.equals(finExcessAmount.getAmountType(), RepayConstants.EXAMOUNTTYPE_EMIINADV)) {
							closureReport.setAdvInsts(
									PennantApplicationUtil.formateAmount(finExcessAmount.getBalanceAmt(), formatter));
						}
					}
					closureReport.setTotalDues(closureReport.getLatePayCharges().add(closureReport.getPendingInsts())
							.add(closureReport.getCheqBncCharges()).add(closureReport.getOutstandingPri())
							.add(closureReport.getInstForTheMonth())
							.add(closureReport.getForeClosFees().add(closureReport.getManualAdviceAmt()))
							.subtract(closureReport.getTds()).subtract(closureReport.getTotWaiver()));
					if (noOfIntDays > 0) {
						closureReport.setIntPerday(
								closureReport.getInstForTheMonth().divide(new BigDecimal(noOfIntDays), 2));
					}

					// Charges Inclusive of GST
					closureReport.setChargesIncGST(closureReport.getLatePayCharges()
							.add(closureReport.getCheqBncCharges()).add(closureReport.getManualAdviceAmt()));
					closureReport.setChargesIncGSTInWords(NumberToEnglishWords
							.getAmountInText((closureReport.getLatePayCharges().add(closureReport.getCheqBncCharges())
									.add(closureReport.getManualAdviceAmt())), finCCy));

					// Issue Fixed 141142
					List<ManualAdvise> payableList = receiptData.getReceiptHeader().getPayableAdvises();
					BigDecimal payableAmt = BigDecimal.ZERO;
					for (ManualAdvise manualAdvise : payableList) {
						payableAmt = payableAmt.add(manualAdvise.getBalanceAmt());
					}

					// Other Refunds (All payable Advise)
					closureReport.setOtherRefunds(PennantApplicationUtil.formateAmount(payableAmt, formatter));
					closureReport.setOtherRefundsInWords(NumberToEnglishWords
							.getAmountInText(PennantApplicationUtil.formateAmount(payableAmt, formatter), finCCy));

					// Refunds + other Refunds
					closureReport.setTotalRefunds(closureReport.getRefund().add(closureReport.getOtherRefunds()));
					closureReport.setTotalRefundsInWords(
							NumberToEnglishWords.getAmountInText(closureReport.getTotalRefunds(), finCCy));

					// Net Receivable
					int format = 0;
					closureReport.setNetReceivable(
							closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()).abs());
					BigDecimal netAmtRecievable = PennantApplicationUtil
							.unFormateAmount(closureReport.getNetReceivable(), format);
					closureReport
							.setNetReceivableInWords(NumberToEnglishWords.getAmountInText(netAmtRecievable, finCCy));

					if ((closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()))
							.compareTo(BigDecimal.ZERO) < 0) {
						closureReport.setTotal("Net Payable");
					} else {
						closureReport.setTotal("Net Receivable");
					}

					int defaultDays = 7;
					int noOfdays = DateUtility.getDaysBetween(chrgTillDate, financeMain.getMaturityDate());
					if (defaultDays >= noOfdays) {
						defaultDays = noOfdays;
					}

					List<FinanceMain> financeMainList = financeDetailService
							.getFinanceMainForLinkedLoans(financeMain.getFinReference());
					StringBuilder linkedFinRef = new StringBuilder(" ");
					if (financeMainList != null) {
						for (FinanceMain finance : financeMainList) {
							if (!finance.getFinReference().equals(closureReport.getFinReference())) {
								linkedFinRef.append(" " + finance.getFinReference());
								linkedFinRef.append(",");
							}
						}
						linkedFinRef.setLength(linkedFinRef.length() - 1);
					}

					// Linked Loan Reference
					closureReport.setLinkedFinRef(linkedFinRef.toString());

					closureReport.setEntityDesc(financeMain.getEntityDesc());
					finStmtResponse.setForeclosureReport(closureReport);
				}
			}
		} catch (Exception e) {
			finStmtResponse = new FinStatementResponse();
			finStmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			logger.debug(Literal.LEAVING);
			return finStmtResponse;
		}
		logger.debug(Literal.LEAVING);
		return finStmtResponse;
	}
	
	/**
	 * Method for fetch Schedule Date against the presentment bounce charge
	 * 
	 * @param receiptId
	 * @return
	 */
	private Date getBounceDueDate(long receiptId) {
		return manualAdviseDAO.getPresentmentBounceDueDate(receiptId);
	}

	/**
	 * Method for calculating Schedule Total and Unpaid amounts based on Schedule Details
	 */
	private List<ReceiptAllocationDetail> calEarlySettleAmount(FinScheduleData finScheduleData, Date valueDate) {
		logger.debug(Literal.ENTERING);

		String TDS_ROUNDING_MODE = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		int TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if (finScheduleData.getFinanceMain().isTDSApplicable()) {

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
						RoundingMode.HALF_DOWN);
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
			if (i != 0) {
				prvSchd = tempScheduleDetails.get(i - 1);
			}
			Date schdDate = curSchd.getSchDate();

			// Accrued Profit Calculation
			if (DateUtil.compare(schdDate, valueDate) < 0) {
				pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitSchd());
				priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));

				if (finScheduleData.getFinanceMain().isTDSApplicable()) {
					BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
					BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
					BigDecimal tds = pft.subtract(actualPft);
					tds = CalculationUtil.roundAmount(tds, TDS_ROUNDING_MODE, TDS_ROUNDING_TARGET);
					tdsAccruedTillNow = tdsAccruedTillNow.add(tds);
				}

			} else if (DateUtil.compare(valueDate, schdDate) == 0) {

				BigDecimal remPft = curSchd.getProfitCalc();
				pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitCalc());
				if (prvSchd != null) {
					remPft = remPft.add(prvSchd.getProfitBalance());
					pftAccruedTillNow = pftAccruedTillNow.add(prvSchd.getProfitBalance());
				}
				priBalance = priBalance.add(curSchd.getPrincipalSchd().add(curSchd.getClosingBalance()))
						.subtract(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid());

				if (finScheduleData.getFinanceMain().isTDSApplicable()) {
					BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
					BigDecimal tds = remPft.subtract(actualPft);
					tds = CalculationUtil.roundAmount(tds, TDS_ROUNDING_MODE, TDS_ROUNDING_TARGET);
					tdsAccruedTillNow = tdsAccruedTillNow.add(tds);
				}
				partAccrualReq = false;

			} else {
				if (partAccrualReq && prvSchd != null) {
					partAccrualReq = false;
					BigDecimal accruedPft = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate,
							curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
					accruedPft = accruedPft.add(prvSchd.getProfitFraction());
					accruedPft = CalculationUtil.roundAmount(accruedPft,
							finScheduleData.getFinanceMain().getCalRoundingMode(),
							finScheduleData.getFinanceMain().getRoundingTarget());
					pftAccruedTillNow = pftAccruedTillNow.add(accruedPft).add(prvSchd.getProfitBalance());

					priBalance = priBalance.add(prvSchd.getClosingBalance());

					if (finScheduleData.getFinanceMain().isTDSApplicable()) {
						BigDecimal actualPft = (accruedPft.add(prvSchd.getProfitBalance())).divide(tdsMultiplier, 0,
								RoundingMode.HALF_DOWN);
						BigDecimal tds = accruedPft.add(prvSchd.getProfitBalance()).subtract(actualPft);
						tds = CalculationUtil.roundAmount(tds, TDS_ROUNDING_MODE, TDS_ROUNDING_TARGET);
						tdsAccruedTillNow = tdsAccruedTillNow.add(tds);
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
		if (DateUtil.compare(valueDate, DateUtility.getAppDate()) != 0) {
			if (overdueList != null) {
				for (FinODDetails odDetail : overdueList) {
					if (odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
						overdueList = getReceiptService().getValueDatePenalties(finScheduleData,
								priBalance.add(pftAmt).add(totFeeAmount), valueDate, null, true);
					}
				}
			}
		}

		// Calculating Actual Sum of Penalty Amount & Late Pay Interest
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal penaltyBal = BigDecimal.ZERO;
		if (overdueList != null && !overdueList.isEmpty()) {
			for (int i = 0; i < overdueList.size(); i++) {
				FinODDetails finODDetail = overdueList.get(i);
				if (finODDetail.getFinODSchdDate().compareTo(valueDate) > 0) {
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

		logger.debug(Literal.LEAVING);
		return allocations;
	}

	/**
	 * Method for Sorting Schedule Details
	 * 
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

	/**
	 * Method for generating SOA report
	 * 
	 * @param statementRequest
	 * @return stmtResponse
	 */
	public FinStatementResponse getReportSatatement(FinStatementRequest statementRequest) {
		//TODO FIXME
		logger.debug(Literal.ENTERING);

		//Temporary FIX
		FinStatementResponse stmtResponse;
		try {
			String finRefernce = statementRequest.getFinReference();
			String whereCond1 = null;
			Date sqlFromdate = null;
			Date sqltodate = null;
			if (StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA)
					|| StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA_REPORT)) {
				try {
					String reportName = statementRequest.getTemplate();
					Date fromdate = statementRequest.getFromDate();
					Date todate = statementRequest.getToDate();
					String finReference = statementRequest.getFinReference();
					String envVariable = System.getenv("APP_ROOT_PATH");
					PathUtil.setRootPath(envVariable);
					StatementOfAccount soa = soaReportGenerationService.getStatmentofAccountDetails(finReference,
							fromdate, todate, false);

					List<Object> list = new ArrayList<Object>();

					list.add(soa.getSoaSummaryReports());
					list.add(soa.getTransactionReports());
					list.add(soa.getApplicantDetails());
					list.add(soa.getOtherFinanceDetails());
					list.add(soa.getSheduleReports());
					list.add(soa.getInterestRateDetails());

					String reportSrc = PathUtil.getPath(PathUtil.REPORTS_FINANCE) + "/" + reportName + ".jasper";
					LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
					String userName = userDetails.getUserName();

					byte[] document = ReportCreationUtil.reportGeneration(reportName, soa, list, reportSrc, userName,
							false);
					if (document != null) {
						String location = null;
						if (StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA)) {
							//location = moveToS3Bucket(document, finReference, statementRequest.getType(), soa.getFinDivision());
						}
						stmtResponse = new FinStatementResponse();

						if (StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA_REPORT)) {
							stmtResponse.setFinReference(finReference);
							stmtResponse.setDocImage(document);
							stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
							return stmtResponse;
						}
						if (StringUtils.isNotBlank(location)) {
							logger.debug("prepare response");
							stmtResponse.setFinReference(finReference);
							//stmtResponse.setLocation(location);
							stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
						} else {
							stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
						}
						logger.debug(Literal.LEAVING);
						return stmtResponse;
					} else {
						stmtResponse = new FinStatementResponse();
						stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
						logger.debug(Literal.LEAVING);
						return stmtResponse;
					}
				} catch (Exception e) {
					stmtResponse = new FinStatementResponse();
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					logger.error(Literal.EXCEPTION + e);
					logger.debug(Literal.LEAVING);
					return stmtResponse;
				}
			}
			String whereCond = null;
			byte[] document = null;
			if (!(StringUtils.equals(statementRequest.getType(), APIConstants.STMT_INST_CERT_REPORT)
					|| StringUtils.equals(statementRequest.getType(), APIConstants.STMT_PROV_INST_CERT_REPORT))) {
				whereCond = "where FinReference  =  '" + finRefernce + "'";
				if (StringUtils.equals(statementRequest.getType(), APIConstants.STMT_FORECLOSURE_REPORT)) {
					whereCond = "where T1.FinReference  =  '" + finRefernce + "'";
				}
				getReportConfiguration(statementRequest.getTemplate());
				document = doShowReport(whereCond, whereCond1, sqlFromdate, sqltodate,
						statementRequest.getFinReference());
			}
			stmtResponse = new FinStatementResponse();

			if (StringUtils.equals(statementRequest.getType(), APIConstants.STMT_INST_CERT_REPORT)
					|| StringUtils.equals(statementRequest.getType(), APIConstants.STMT_PROV_INST_CERT_REPORT)) {
				ByteArrayOutputStream oStream = doProcessInterestCertificate(statementRequest);

				if (null != oStream) {
					stmtResponse.setDocImage(oStream.toByteArray());
					stmtResponse.setFinReference(statementRequest.getFinReference());
					stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				} else {
					stmtResponse.setReturnStatus(
							APIErrorHandlerService.getFailedStatus("9999", "Retry; Invalid Configuration"));
				}

				return stmtResponse;
			}
			if (document != null) {
				String location = null;
				if (StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA)
						|| StringUtils.equals(statementRequest.getType(), APIConstants.STMT_REPAY_SCHD)
						|| StringUtils.equals(statementRequest.getType(), APIConstants.STMT_NOC)) {
					//location = moveToS3Bucket(document, statementRequest.getFinReference(), statementRequest.getType(), null);
				}

				if (StringUtils.equals(statementRequest.getType(), APIConstants.STMT_NOC_REPORT)
						|| StringUtils.equals(statementRequest.getType(), APIConstants.STMT_REPAY_SCHD_REPORT)
						|| StringUtils.equals(statementRequest.getType(), APIConstants.STMT_FORECLOSURE_REPORT)) {
					stmtResponse.setFinReference(statementRequest.getFinReference());
					stmtResponse.setDocImage(document);
					stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					return stmtResponse;
				}

				if (StringUtils.isNotBlank(location)) {
					stmtResponse.setFinReference(statementRequest.getFinReference());
					//stmtResponse.setLocation(location);
					stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				} else {
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				}
				logger.debug(Literal.LEAVING);
				return stmtResponse;
			} else {
				stmtResponse = new FinStatementResponse();
				stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				logger.debug(Literal.LEAVING);
				return stmtResponse;
			}
		} catch (Exception e) {
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			logger.error(Literal.EXCEPTION + e);
			logger.debug(Literal.LEAVING);
			return stmtResponse;
		}
	}

	private String getAddress(String seperator, String... values) {
		StringBuilder builder = new StringBuilder();

		for (String value : values) {
			if (StringUtils.isEmpty(value)) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append(seperator);
			}

			builder.append(builder);
		}

		return builder.toString();
	}

	/**
	 * This method generates a {@link ByteArrayOutputStream}, compatible for re-use
	 * 
	 * @param statementRequest
	 *            - {@link FinStatementRequest}
	 * @return {@link ByteArrayOutputStream}
	 */
	private ByteArrayOutputStream doProcessInterestCertificate(FinStatementRequest statementRequest) {

		ByteArrayOutputStream stream = null;
		Date fromdate = statementRequest.getFromDate();
		Date startDate = null;
		Date endDate = null;

		int year = 0;
		Date appDate = SysParamUtil.getAppDate();
		if (fromdate != null) {
			year = DateUtility.getYear(fromdate);
		} else {
			year = DateUtil.getYear(appDate);
		}

		startDate = DateUtil.getDate(year, 3, 1);
		endDate = DateUtil.getDate(year + 1, 2, 31);

		InterestCertificate intCert = null;
		try {
			intCert = interestCertificateService.getInterestCertificateDetails(statementRequest.getFinReference(),
					startDate, endDate, false);
		} catch (ParseException e) {
			logger.error("Unable to retrieve model from service");
		}
		if (intCert == null) {
			logger.error("Empty Model");
		}

		intCert.setAppDate(DateUtil.formatToLongDate(appDate));
		intCert.setFinStartDate(DateUtil.formatToShortDate(startDate));
		intCert.setFinEndDate(DateUtil.formatToShortDate(endDate));
		intCert.setFinPostDate(appDate);

		Method[] methods = intCert.getClass().getDeclaredMethods();

		for (Method property : methods) {
			if (property.getName().startsWith("get")) {
				String field = property.getName().substring(3);
				Object value;

				try {
					value = property.invoke(intCert);
				} catch (Exception e) {
					continue;
				}

				if (value == null) {
					try {
						String stringParameter = "";
						intCert.getClass().getMethod("set" + field, new Class[] { String.class }).invoke(intCert,
								stringParameter);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}
			}
		}

		String address = getAddress("\n", intCert.getCustAddrHnbr(), intCert.getCustFlatNbr(),
				intCert.getCustAddrStreet(), intCert.getCustAddrCity(), intCert.getCustAddrState(),
				intCert.getCustAddrZIP(), intCert.getCountryDesc());

		String certType = statementRequest.getType();
		if (APIConstants.STMT_PROV_INST_CERT_REPORT.equals(certType)) {
			address = getAddress("\n", address, intCert.getCustEmail(), intCert.getCustPhoneNumber());
			intCert.setCustAddress(address);
		}

		if (APIConstants.STMT_INST_CERT_REPORT.equals(certType)) {
			intCert.setCustAddress(address);
		}

		String agreement = null;
		if (APIConstants.STMT_PROV_INST_CERT_REPORT.equals(certType)) {
			agreement = "ProvisionalCertificate.docx";
		}

		if (APIConstants.STMT_INST_CERT_REPORT.equals(certType)) {
			agreement = "InterestCertificate.docx";
		}

		String templatePath = PathUtil.getPath(PathUtil.FINANCE_INTERESTCERTIFICATE);
		TemplateEngine engine = null;
		try {
			engine = new TemplateEngine(templatePath, templatePath);
			engine.setTemplate(agreement);
			engine.loadTemplate();
			engine.mergeFields(intCert);

			stream = new ByteArrayOutputStream();
			engine.getDocument().save(stream, SaveFormat.PDF);

		} catch (Exception e) {
			logger.error("Problem during processing 'TemplateEngine'");
		}

		return stream;
	}

	public StatementOfAccount getStatementOfAcc(FinStatementRequest statementRequest)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		StatementOfAccount statementOfAccount = null;
		Date fromdate = statementRequest.getFromDate();
		Date todate = statementRequest.getToDate();
		String finReference = statementRequest.getFinReference();
		String envVariable = System.getenv("APP_ROOT_PATH");
		PathUtil.setRootPath(envVariable);
		statementOfAccount = soaReportGenerationService.getStatmentofAccountDetails(finReference, fromdate, todate,
				true);

		if (statementOfAccount != null) {
			statementOfAccount.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			return statementOfAccount;
		} else {
			statementOfAccount = new StatementOfAccount();
			statementOfAccount.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			logger.debug(Literal.LEAVING);
		}
		return statementOfAccount;
	}

	private void getReportConfiguration(String menuName) {
		reportConfiguration = reportConfigurationDAO.getReportConfigurationByMenuName(menuName, "");
	}

	@Autowired
	@Qualifier("dataSource")
	JndiObjectFactoryBean jndiObjectFactoryBean;

	private byte[] doShowReport(String whereCond, String whereCond1, Date fromDate, Date toDate, String finRefernce) {

		logger.debug(Literal.ENTERING);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(10);
		String envVariable = System.getenv("APP_ROOT_PATH");
		PathUtil.setRootPath(envVariable);

		reportArgumentsMap.put("userName", userDetails.getUserId());
		reportArgumentsMap.put("reportHeading", reportConfiguration.getReportHeading());
		reportArgumentsMap.put("reportGeneratedBy", "Report Generated by penApps PFF");// Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		reportArgumentsMap.put("appDate", DateUtility.getAppDate());
		reportArgumentsMap.put("appCcy", SysParamUtil.getAppCurrency());
		reportArgumentsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
		reportArgumentsMap.put("unitParam", "PFF");

		if (whereCond != null) {
			reportArgumentsMap.put("whereCondition", whereCond);
		}
		if (whereCond1 != null) {
			reportArgumentsMap.put("whereCondition1", whereCond1);
		}
		if (fromDate != null) {
			reportArgumentsMap.put("fromDate", "'" + fromDate.toString() + "'");
		}
		if (toDate != null) {
			reportArgumentsMap.put("toDate", "'" + toDate.toString() + "'");
		}
		reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
		reportArgumentsMap.put("bankName", "Bajaj Finance LTD"); // Labels.getLabel("label_ClientName"));

		String searchCriteria = "Loan Reference is " + finRefernce + " ";
		if (fromDate != null) {
			searchCriteria.concat(" Date is between" + toDate + " and " + fromDate);
		}
		reportArgumentsMap.put("searchCriteria", searchCriteria);
		String reportName = reportConfiguration.getReportJasperName();// This
																		// will
																		// come
																		// dynamically
		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/" + reportName + ".jasper";

		byte[] buf = null;
		Connection con = null;

		DataSource reportDataSourceObj = null;

		try {
			File file = new File(reportSrc);
			if (file.exists()) {

				logger.debug("Buffer started");

				if (jndiObjectFactoryBean != null) {
					reportDataSourceObj = getDataSource(jndiObjectFactoryBean.getJndiName());
				}
				con = reportDataSourceObj.getConnection();
				buf = JasperRunManager.runReportToPdf(reportSrc, reportArgumentsMap, con);
				logger.debug(Literal.LEAVING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf;
	}

	private DataSource getDataSource(String jndiName) {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource(jndiName);
		return dataSource;
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

	public void setSoaReportGenerationService(SOAReportGenerationService soaReportGenerationService) {
		this.soaReportGenerationService = soaReportGenerationService;
	}

	public void setReportConfigurationDAO(ReportConfigurationDAO reportConfigurationDAO) {
		this.reportConfigurationDAO = reportConfigurationDAO;
	}

	public void setReportConfiguration(ReportConfiguration reportConfiguration) {
		this.reportConfiguration = reportConfiguration;
	}

	public void setInterestCertificateService(InterestCertificateService interestCertificateService) {
		this.interestCertificateService = interestCertificateService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

}
