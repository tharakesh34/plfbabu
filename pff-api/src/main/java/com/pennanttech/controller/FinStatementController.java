package com.pennanttech.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiObjectFactoryBean;

import com.aspose.words.SaveFormat;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.reports.ReportConfigurationDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FeeType;
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
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.reports.ReportConfiguration;
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
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.CustomerUtil;
import com.pennanttech.pff.core.util.SchdUtil;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
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

	private ReceiptService receiptService;
	private SOAReportGenerationService soaReportGenerationService;
	private InterestCertificateService interestCertificateService;
	private ReportConfigurationDAO reportConfigurationDAO;
	private ReportConfiguration reportConfiguration;
	private FeeTypeDAO feeTypeDAO;
	private CustomerDetailsService customerDetailsService;

	public FinStatementResponse getStatement(List<Long> finIDList, String serviceName) {
		logger.debug(Literal.ENTERING);

		StringBuilder logMsg = new StringBuilder();
		logMsg.append("\n");
		logMsg.append("========================================================\n");
		logMsg.append("Service-Name").append(serviceName).append("\n");
		logMsg.append("\n");
		logMsg.append("=======================================================\n");

		logger.info(logMsg);

		FinStatementResponse stmtResponse = new FinStatementResponse();
		List<FinanceDetail> fdList = new ArrayList<>();

		try {
			for (long finID : finIDList) {
				FinanceDetail fd = getFinanceDetail(finID, serviceName);

				if (fd == null) {
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					return stmtResponse;
				}

				prepareResponse(fd, serviceName);

				if (StringUtils.isNotBlank(fd.getFinReference())) {
					APIErrorHandlerService.logReference(fd.getFinReference());
				}

				stmtResponse.setCustomer(fd.getCustomerDetails());
				fd.setCustomerDetails(null);
				fdList.add(fd);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			APIErrorHandlerService.logUnhandledException(e);
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stmtResponse;
		}

		stmtResponse.setFinance(fdList);
		stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);

		return stmtResponse;
	}

	public FinStatementResponse getStatement(List<Long> finIDList, String serviceName, int days, Date fromDate) {
		logger.debug(Literal.ENTERING);

		FinStatementResponse stmtResponse = new FinStatementResponse();
		List<FinanceDetail> finDetailList = new ArrayList<>();
		try {

			for (Long finID : finIDList) {
				FinanceDetail fd = getFinanceDetail(finID, serviceName);

				if (fd == null) {
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					return stmtResponse;
				}

				FinScheduleData schdData = fd.getFinScheduleData();
				String finReference = fd.getFinScheduleData().getFinanceMain().getFinReference();

				if (APIConstants.STMT_FORECLOSURE.equals(serviceName)) {
					schdData.setFinODDetails(finODDetailsDAO.getFinODDByFinRef(finID, null));
					setForeClosureDetails(fd, days, fromDate);
				}

				if (APIConstants.STMT_FORECLOSUREV1.equals(serviceName)) {
					FinReceiptData receiptData = receiptService.getFinReceiptDataById(finReference, fromDate,
							AccountingEvent.EARLYSTL, FinServiceEvent.RECEIPT, "");
					setForeClosureReport(receiptData, stmtResponse);
				}

				prepareResponse(fd, serviceName);

				// for logging purpose
				if (StringUtils.isNotBlank(fd.getFinReference())) {
					APIErrorHandlerService.logReference(fd.getFinReference());
				}

				stmtResponse.setCustomer(fd.getCustomerDetails());
				fd.setCustomerDetails(null);
				fd.setJointAccountDetailList(null);
				fd.setCollateralAssignmentList(null);
				fd.getFinScheduleData().setVasRecordingList(null);
				finDetailList.add(fd);
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

	private FinanceDetail getFinanceDetail(long finID, String serviceName) {
		FinanceDetail fd = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");

		if (fd == null) {
			return fd;
		}

		fd.setFinID(finID);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finReference = fm.getFinReference();

		switch (serviceName) {
		case APIConstants.STMT_ACCOUNT:
			fd.setReturnDataSetList(postingsDAO.getPostingsByFinRef(finReference, true));
			break;
		case APIConstants.STMT_INST_CERT:
			fd.setCollaterals(collateralSetupService.getCollateralSetupByCustId(fm.getCustID()));
			break;
		default:
			break;
		}

		return fd;
	}

	private void setForeClosureDetails(FinanceDetail fd, int days, Date fromDate) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		BigDecimal repayAmt = SchdUtil.getTotalRepayAmount(schdData.getFinanceScheduleDetails());

		FinServiceInstruction fsi = new FinServiceInstruction();
		fsi.setAmount(repayAmt);
		fsi.setModuleDefiner(FinServiceEvent.EARLYSTLENQ);
		fm.setRcdMaintainSts(FinServiceEvent.EARLYSTLENQ);

		if (fsi.getToDate() == null) {
			fsi.setToDate(fm.getMaturityDate());
		}

		List<ForeClosure> foreClosureList = new ArrayList<>();
		List<FinODDetails> finOdDetaiList = new ArrayList<>();
		List<FinFeeDetail> foreClosureFees = new ArrayList<>();
		List<FinFeeDetail> feeDues = new ArrayList<FinFeeDetail>();

		try {

			if (fromDate != null) {
				FinanceDetail afd = new FinanceDetail();
				afd.setFinScheduleData(schdData.copyEntity());
				fsi.setFromDate(fromDate);

				doProcessPayments(afd, fsi);

				schdData.setOutstandingPri(afd.getFinScheduleData().getOutstandingPri());
				feeDues = afd.getFinScheduleData().getFeeDues();
				foreClosureFees = afd.getFinScheduleData().getForeClosureFees();
				foreClosureList.add(afd.getForeClosureDetails().get(0));
				finOdDetaiList.add(afd.getFinScheduleData().getFinODDetails().get(0));
			} else {
				for (int i = 0; i < days; i++) {
					FinanceDetail afd = new FinanceDetail();
					afd.setFinScheduleData(schdData.copyEntity());
					fsi.setFromDate(DateUtil.addDays(SysParamUtil.getAppDate(), i));

					doProcessPayments(afd, fsi);

					schdData.setOutstandingPri(afd.getFinScheduleData().getOutstandingPri());
					feeDues = afd.getFinScheduleData().getFeeDues();
					foreClosureFees = afd.getFinScheduleData().getForeClosureFees();
					foreClosureList.add(afd.getForeClosureDetails().get(0));
					finOdDetaiList.add(afd.getFinScheduleData().getFinODDetails().get(0));
				}
			}

			schdData.setFinID(finID);
			schdData.setFinReference(finReference);
			schdData.setFinODDetails(finOdDetaiList);
			schdData.setForeClosureFees(foreClosureFees);
			schdData.setFeeDues(feeDues);

			// process origination fees
			schdData.getFeeDues().addAll(getUpdatedFees(fd.getFinScheduleData().getFinFeeDetailList()));

			fd.setForeClosureDetails(foreClosureList);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	private void processFeesAndCharges(FinScheduleData schdData, List<FinFeeDetail> finFeeDetails) throws AppException {
		for (FinFeeDetail feeDetail : finFeeDetails) {
			feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
		}

		schdData.setFeeDues(finFeeDetails);
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		List<FinFeeDetail> feeDues = new ArrayList<>();

		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getReceivableAdvises(finID, "_View");

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
		schdData.getFeeDues().addAll(feeDues);
	}

	public FinStatementResponse getStatement(FinStatementRequest statementRequest, String serviceName) {
		List<Long> finIDList = new ArrayList<>();
		finIDList.add(statementRequest.getFinID());
		return getStatement(finIDList, serviceName, statementRequest.getDays(), statementRequest.getFromDate());
	}

	private void prepareResponse(FinanceDetail fd, String servicName) throws AppException {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Date appDate = SysParamUtil.getAppDate();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		fd.setFinID(finID);
		fd.setFinReference(finReference);
		schdData.setFinReference(null);
		schdData.setStepPolicyDetails(null);
		schdData.setPlanEMIHDates(null);
		schdData.setPlanEMIHmonths(null);
		schdData.setRateInstruction(null);
		schdData.setRepayInstructions(null);

		fd.setFinFlagsDetails(null);
		fd.setCovenantTypeList(null);
		fd.setMandate(null);
		fd.setAdvancePaymentsList(null);
		fd.setGurantorsDetailList(null);
		fd.setDocumentDetailsList(null);
		fd.setCollateralAssignmentList(null);
		fd.setCovenants(null);
		fd.setCollaterals(null);

		List<FinanceDisbursement> disbList = schdData.getDisbursementDetails().stream()
				.sorted((b1, b2) -> Integer.compare(b1.getDisbSeq(), b2.getDisbSeq())).collect(Collectors.toList());

		if (!disbList.isEmpty()) {
			if (disbList.size() == 1) {
				fm.setFirstDisbDate(disbList.get(0).getDisbDate());
				fm.setLastDisbDate(disbList.get(0).getDisbDate());
			} else {
				fm.setFirstDisbDate(disbList.get(0).getDisbDate());
				fm.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}
		}

		if (APIConstants.STMT_ACCOUNT.equals(servicName)) {
			List<FinFeeDetail> finFeeDetail = schdData.getFinFeeDetailList();

			schdData.setFinReference(schdData.getFinanceMain().getFinReference());

			processFeesAndCharges(schdData, finFeeDetail);

			fd.setFinFeeDetails(getUpdatedFees(schdData.getFeeDues()));
			schdData.setFeeDues(null);
		}

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		schedules.forEach(schd -> schd.setLoanEMIStatus(SchdUtil.getRepaymentStatus(schd).repaymentStatus()));

		schdData.setFinanceScheduleDetails(schedules);

		FinanceSummary summary = getFinanceSummary(fd);
		summary.setAdvPaymentAmount(getTotalAdvAmount(fm));
		summary.setOutStandPrincipal(schdData.getOutstandingPri());
		summary.setTotalPriSchd(SchdUtil.getTotalPrincipalSchd(schdData.getFinanceScheduleDetails()));

		Date businessDate = appDate;
		if (appDate.compareTo(fm.getMaturityDate()) >= 0) {
			businessDate = DateUtil.addDays(fm.getMaturityDate(), -1);
		}

		FinanceScheduleDetail curSchd = SchdUtil.getNextInstalment(businessDate, schedules);

		if (curSchd != null) {
			summary.setInstallmentNo(curSchd.getInstNumber());
			summary.setLoanEMI(curSchd.getRepayAmount());
			summary.setDueDate(curSchd.getSchDate());
			summary.setLoanTotPrincipal(curSchd.getPrincipalSchd());
			summary.setLoanTotInterest(curSchd.getProfitSchd());
		}

		schdData.setFinanceSummary(summary);

		schdData.setDisbursementDetails(null);
		schdData.setFinFeeDetailList(null);

		if (!APIConstants.STMT_FORECLOSURE.equals(servicName)) {
			schdData.setFinODDetails(null);
		} else {
			// #BUG Fix related to 134031
			fd.getForeClosureDetails()
					.forEach(cd -> cd.setChargeAmount(cd.getForeCloseAmount().subtract(summary.getAdvPaymentAmount())));

		}
		schdData.setFinODPenaltyRate(null);

		// customer details
		CustomerDetails cd = fd.getCustomerDetails();
		Customer customer = cd.getCustomer();

		customer.setLoanName(CustomerUtil.getCustomerFullName(customer));

		cd.setCustCIF(customer.getCustCIF());
		cd.setCustCoreBank(customer.getCustCoreBank());
		cd.setCustCtgCode(customer.getCustCtgCode());
		cd.setCustDftBranch(customer.getCustDftBranch());
		cd.setCustBaseCcy(customer.getCustBaseCcy());
		cd.setPrimaryRelationOfficer(customer.getCustRO1());
		cd.setCustomer(customer);
		cd.setCustEmployeeDetail(null);
		cd.setCustomerExtLiabilityList(null);
		cd.setCustomerIncomeList(null);
		cd.setCustomerDocumentsList(null);
		cd.setCustomerBankInfoList(null);
		cd.setEmploymentDetailsList(null);
		cd.setCustomerChequeInfoList(null);
	}

	private void doProcessPayments(FinanceDetail fd, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		if (fsi.getFromDate() == null) {
			fsi.setFromDate(SysParamUtil.getAppDate());
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		// Repayments Posting Process Execution
		// =====================================
		Date valueDate = fsi.getFromDate();

		List<ReceiptAllocationDetail> allocations = calEarlySettleAmount(schdData, valueDate);
		BigDecimal totPriPayNow = BigDecimal.ZERO;
		BigDecimal totPftPayNow = BigDecimal.ZERO;
		BigDecimal totTdsReturn = BigDecimal.ZERO;
		BigDecimal totLatePftPayNow = BigDecimal.ZERO;
		BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
		BigDecimal totFeePayNow = BigDecimal.ZERO;

		for (ReceiptAllocationDetail ad : allocations) {
			switch (ad.getAllocationType()) {
			case Allocation.PRI:
				totPriPayNow = ad.getPaidAmount();
				break;
			case Allocation.PFT:
				totPftPayNow = ad.getPaidAmount();
				break;
			case Allocation.LPFT:
				totLatePftPayNow = ad.getPaidAmount();
				break;
			case Allocation.ODC:
				totPenaltyPayNow = ad.getPaidAmount();
				break;
			case Allocation.TDS:
				totTdsReturn = ad.getPaidAmount();
				break;
			case Allocation.FEE:
				totFeePayNow = ad.getPaidAmount();
				break;
			default:
				break;
			}
		}

		// fore closure details
		List<ForeClosure> foreClosureList = new ArrayList<>();
		ForeClosure foreClosure = new ForeClosure();
		foreClosure.setValueDate(DateUtil.getTimestamp(fsi.getFromDate()));
		BigDecimal foreCloseAmt = totPriPayNow.add(totPenaltyPayNow).add(totPftPayNow).add(totFeePayNow)
				.subtract(totTdsReturn);
		BigDecimal totServFees = BigDecimal.ZERO;

		// foreclosure fees
		List<FinFeeDetail> foreClosureFees = new ArrayList<>();
		fm.setFinSourceID(AccountingEvent.EARLYSTL);
		feeDetailService.doExecuteFeeCharges(fd, AccountingEvent.EARLYSTL, null, true);
		if (schdData.getFinFeeDetailList() != null) {
			for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
				if (StringUtils.equals(fee.getFinEvent(), AccountingEvent.EARLYSTL)) {
					totServFees = totServFees.add(fee.getActualAmount());
					if (StringUtils.equals(fee.getFeeScheduleMethod(), PennantConstants.List_Select)) {
						fee.setFeeScheduleMethod(null);
					}
					foreClosureFees.add(fee);
				}
				fee.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
			}
			schdData.setForeClosureFees(foreClosureFees);
		}

		// Bounce and manual advice fees if applicable
		List<FinFeeDetail> feeDues = new ArrayList<>();
		BigDecimal bounceAmount = BigDecimal.ZERO;
		BigDecimal totBounceAndReceivables = BigDecimal.ZERO;
		BigDecimal totReceivableAdFee = BigDecimal.ZERO;

		List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getReceivableAdvises(finID, "_View");

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);
		TaxAmountSplit taxSplit = null;
		FeeType feeType = null;

		if (CollectionUtils.isNotEmpty(manualAdviseFees)) {
			for (ManualAdvise advisedFees : manualAdviseFees) {
				FinFeeDetail feeDetail = new FinFeeDetail();
				BigDecimal bounceGst = BigDecimal.ZERO;
				BigDecimal receivableGst = BigDecimal.ZERO;

				if (advisedFees.getBounceID() > 0) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_BOUNCE);
					feeDetail.setFeeID(advisedFees.getAdviseID());
					feeDetail.setSchdDate(getBounceDueDate(advisedFees.getReceiptID()));
					bounceAmount = bounceAmount
							.add(advisedFees.getAdviseAmount().subtract(advisedFees.getPaidAmount()));

					if (feeType == null) {
						feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
					}
					if (feeType.isTaxApplicable()
							&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(feeType.getTaxComponent())) {
						taxSplit = GSTCalculator.getExclusiveGST(advisedFees.getAdviseAmount(), taxPercentages);
						bounceGst = bounceGst.add(taxSplit.gettGST());
					}
					bounceAmount = bounceAmount.add(bounceGst);
				} else {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_ADVISE);
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advisedFees.getTaxComponent())) {
						taxSplit = GSTCalculator.getExclusiveGST(advisedFees.getAdviseAmount(), taxPercentages);
						receivableGst = taxSplit.gettGST();
					}
					totReceivableAdFee = totReceivableAdFee.add(advisedFees.getAdviseAmount().add(receivableGst));
				}
				feeDetail.setFeeTypeCode(advisedFees.getFeeTypeCode());
				feeDetail.setActualAmount(advisedFees.getAdviseAmount().add(bounceGst).add(receivableGst));
				feeDetail.setPaidAmount(advisedFees.getPaidAmount());
				feeDetail.setRemainingFee(advisedFees.getBalanceAmt().add(bounceGst).add(receivableGst));
				feeDues.add(feeDetail);
			}

			totBounceAndReceivables = totBounceAndReceivables.add(bounceAmount).add(totReceivableAdFee);
			schdData.getFeeDues().addAll(feeDues);
		}

		foreClosure.setBounceCharge(bounceAmount);
		foreClosure.setReceivableADFee(totReceivableAdFee);
		foreClosure.setLPIAmount(totLatePftPayNow);
		foreCloseAmt = foreCloseAmt.add(totServFees).add(totBounceAndReceivables);
		foreClosure.setForeCloseAmount(foreCloseAmt);
		foreClosure.setAccuredIntTillDate(totPftPayNow);
		foreClosure.setChargeAmount(totPenaltyPayNow);
		foreClosureList.add(foreClosure);

		// penalty details
		List<FinODDetails> finODDetailList = new ArrayList<>();
		FinODDetails finoDetail = new FinODDetails();
		finoDetail.setFinCurODAmt(totPenaltyPayNow);
		finODDetailList.add(finoDetail);

		schdData.setOutstandingPri(totPriPayNow);
		fd.setForeClosureDetails(foreClosureList);
		schdData.setFinODDetails(finODDetailList);

		logger.debug(Literal.LEAVING);
	}

	public void setForeClosureReport(FinReceiptData receiptData, FinStatementResponse finStmtResponse) {
		logger.debug(Literal.ENTERING);
		ForeClosureReport closureReport = new ForeClosureReport();

		Date appDate = SysParamUtil.getAppDate();

		try {
			FinanceDetail fd = receiptData.getFinanceDetail();
			FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

			FinReceiptHeader rch = receiptData.getReceiptHeader();

			rch.setFinID(fm.getFinID());
			rch.setReference(fm.getFinReference());
			rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
			rch.setReceiptDate(SysParamUtil.getAppDate());
			rch.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
			rch.setAllocationType(AllocationType.AUTO);
			rch.setNewRecord(true);

			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);

			rch.getReceiptDetails().add(rcd);
			rch.setValueDate(appDate);

			receiptData.setReceiptHeader(rch);
			receiptData.setFinID(fm.getFinID());
			receiptData.setFinReference(fm.getFinReference());
			receiptData.setBuildProcess("I");
			receiptData.setValueDate(appDate);
			receiptData.setReceiptHeader(rch);
			receiptData.setForeClosureEnq(true);
			receiptData = receiptService.calcuateDues(receiptData);

			// Setting Actual Percentage in Fore closure Letter Report.
			if (CollectionUtils.isNotEmpty(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList())) {
				FinFeeDetail finFeeDetail = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()
						.get(0);
				closureReport.setActPercentage(finFeeDetail.getActPercentage());
			}

			String disDate = DateUtil.format(fm.getFinStartDate(), "dd'th' MMMM yyyy");
			int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
			String finCCy = SysParamUtil.getAppCurrency();
			int validityDays = SysParamUtil.getValueAsInt(SMTParameterConstants.FORECLOSURE_VALIDITY_DAYS);
			String validTill = DateUtil.format(DateUtil.addDays(appDate, validityDays), "dd.MM.yyyy");

			Date prvEmiDate = receiptData.getOrgFinPftDtls().getPrvRpySchDate();
			int noOfIntDays = DateUtil.getDaysBetween(appDate, prvEmiDate);

			closureReport.setCalDate(DateUtil.format(appDate, "dd MMMM yyyy"));
			closureReport.setValidTill(validTill);
			closureReport.setFinID(fm.getFinID());
			closureReport.setFinReference(fm.getFinReference());
			closureReport.setVanNumber(fm.getVanCode() == null ? "" : fm.getVanCode());
			closureReport.setFinAmount(PennantApplicationUtil.formateAmount(fm.getFinAmount(), formatter));
			closureReport.setFinAmountInWords(NumberToEnglishWords
					.getAmountInText(PennantApplicationUtil.formateAmount(fm.getFinAmount(), formatter), finCCy));
			closureReport.setFinAssetValue(PennantApplicationUtil.formateAmount(fm.getFinAssetValue(), formatter));
			closureReport.setFinAssetValueInWords(NumberToEnglishWords
					.getAmountInText(PennantApplicationUtil.formateAmount(fm.getFinAssetValue(), formatter), finCCy));

			closureReport.setDisbursalDate(disDate);
			closureReport.setChrgTillDate(DateUtil.format(appDate, "MMM  dd,yyyy"));
			if (fd.getCustomerDetails() != null && fd.getCustomerDetails().getCustomer() != null) {
				closureReport.setCustName(fd.getCustomerDetails().getCustomer().getCustShrtName());
				closureReport.setCustCIF(fd.getCustomerDetails().getCustomer().getCustCIF());
				CustomerDetails customerDetails = customerDetailsService
						.getCustomerDetailsbyIdandPhoneType(fd.getCustomerDetails().getCustID(), "MOBILE");
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
				closureReport.setCustAddrCountryName(StringUtils.trimToEmpty(custAdd.getLovDescCustAddrCountryName()));

				String salutation = fd.getCustomerDetails().getCustomer().getLovDescCustSalutationCodeName();
				String nameString = StringUtils.trimToEmpty(salutation).equals("")
						? StringUtils.trimToEmpty(closureReport.getCustName())
						: StringUtils.trimToEmpty(salutation) + " "
								+ StringUtils.trimToEmpty(closureReport.getCustName());
				if (CollectionUtils.isNotEmpty(fd.getJointAccountDetailList())) {
					for (JointAccountDetail jointAccountDetail : fd.getJointAccountDetailList()) {
						if (StringUtils.isNotEmpty(nameString)) {
							nameString = nameString + "\n";
						}

						if (jointAccountDetail.getCustomerDetails() != null
								&& jointAccountDetail.getCustomerDetails().getCustomer() != null) {
							nameString = nameString
									+ (StringUtils
											.trimToEmpty(jointAccountDetail.getCustomerDetails().getCustomer()
													.getLovDescCustSalutationCodeName())
											.equals("")
													? StringUtils.trimToEmpty(jointAccountDetail.getCustomerDetails()
															.getCustomer().getCustShrtName())
													: StringUtils
															.trimToEmpty(jointAccountDetail.getCustomerDetails()
																	.getCustomer().getLovDescCustSalutationCodeName())
															+ " "
															+ StringUtils
																	.trimToEmpty(jointAccountDetail.getCustomerDetails()
																			.getCustomer().getCustShrtName()));
						}
					}
				}
				if (CollectionUtils.isNotEmpty(fd.getGurantorsDetailList())) {
					for (GuarantorDetail gurantorsDetail : fd.getGurantorsDetailList()) {
						if (StringUtils.isNotEmpty(nameString)) {
							nameString = nameString + "\n";
						}
						nameString = nameString + "\n" + StringUtils.trimToEmpty(gurantorsDetail.getGuarantorCIFName());
					}
				}
				closureReport.setNameOftheBorrowers(nameString);

				String custSalutation = fd.getCustomerDetails().getCustomer().getLovDescCustSalutationCodeName();
				closureReport.setCustSalutation(StringUtils.trimToEmpty(custSalutation));

				List<ReceiptAllocationDetail> receiptAllocationDetails = receiptData.getReceiptHeader()
						.getAllocationsSummary();
				Cloner cloner = new Cloner();
				receiptData.getFinanceDetail().getFinScheduleData()
						.setFinanceScheduleDetails(fd.getFinScheduleData().getFinanceScheduleDetails());
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

				for (ReceiptAllocationDetail rad : receiptAllocationDetails) {
					switch (rad.getAllocationType()) {
					case Allocation.FUT_PRI:
						closureReport
								.setOutstandingPri(PennantApplicationUtil.formateAmount(rad.getTotRecv(), formatter));
						closureReport.setOutstandingPriInWords(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(rad.getTotRecv(), formatter), finCCy));
						break;
					case Allocation.ODC:
						closureReport
								.setLatePayCharges(PennantApplicationUtil.formateAmount(rad.getTotRecv(), formatter));
						closureReport.setLatePayChargesInWords(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(rad.getTotRecv(), formatter), finCCy));
						break;
					case Allocation.BOUNCE:
						bncCharge = rad.getTotRecv();
						break;
					case Allocation.MANADV:
						receivableAmt = receivableAmt.add(rad.getTotRecv());
						break;
					case Allocation.FUT_PFT:
						closureReport
								.setInstForTheMonth(PennantApplicationUtil.formateAmount(rad.getTotRecv(), formatter));
						closureReport.setInstForTheMonthInWords(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(rad.getTotRecv(), formatter), finCCy));
						break;
					case Allocation.PFT:
						profitAmt = rad.getTotRecv();
						break;
					case Allocation.PRI:
						principleAmt = rad.getTotRecv();
						break;
					case Allocation.TDS:
						tdsAmt = rad.getTotRecv();
						break;
					case Allocation.FUT_TDS:
						futTdsAmt = rad.getTotRecv();
						break;
					case Allocation.FEE:
						BigDecimal amountWithGST = rad.getTotRecv();
						BigDecimal gstAmt = rad.getDueGST();
						BigDecimal fcFeeAmtWithoutGst = amountWithGST.subtract(gstAmt);
						BigDecimal fcFee = closureReport.getForeClosFees().add(amountWithGST);
						closureReport.setForeClosFees(PennantApplicationUtil.formateAmount(fcFee, formatter));
						closureReport.setForeClosFeesInWords(NumberToEnglishWords
								.getAmountInText(PennantApplicationUtil.formateAmount(fcFee, formatter), finCCy));
						closureReport.setGstOnForeClosFees((PennantApplicationUtil
								.formateAmount(fcFeeAmtWithoutGst, formatter).multiply(new BigDecimal(18)))
										.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));
						closureReport.setForeClosFeesExGST(
								closureReport.getForeClosFees().subtract(closureReport.getGstOnForeClosFees()));

						closureReport.setGstOnForeClosFees(closureReport.getGstOnForeClosFees());
						break;
					default:
						break;
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
				closureReport
						.setPendingInsts(PennantApplicationUtil.formateAmount(profitAmt.add(principleAmt), formatter));

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
					closureReport.setIntPerday(closureReport.getInstForTheMonth().divide(new BigDecimal(noOfIntDays),
							RoundingMode.CEILING));
				}

				// Charges Inclusive of GST
				closureReport.setChargesIncGST(closureReport.getLatePayCharges().add(closureReport.getCheqBncCharges())
						.add(closureReport.getManualAdviceAmt()));
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
				closureReport
						.setNetReceivable(closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()).abs());
				BigDecimal netAmtRecievable = PennantApplicationUtil.unFormateAmount(closureReport.getNetReceivable(),
						format);
				closureReport.setNetReceivableInWords(NumberToEnglishWords.getAmountInText(netAmtRecievable, finCCy));

				if ((closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()))
						.compareTo(BigDecimal.ZERO) < 0) {
					closureReport.setTotal("Net Payable");
				} else {
					closureReport.setTotal("Net Receivable");
				}

				int defaultDays = 7;
				int noOfdays = DateUtil.getDaysBetween(appDate, fm.getMaturityDate());
				if (defaultDays >= noOfdays) {
					defaultDays = noOfdays;
				}

				List<FinanceMain> financeMainList = financeDetailService
						.getFinanceMainForLinkedLoans(fm.getFinReference());
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

				closureReport.setEntityDesc(fm.getEntityDesc());
				finStmtResponse.setForeclosureReport(closureReport);
			}
		} catch (Exception e) {
			finStmtResponse = new FinStatementResponse();
			finStmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			logger.debug(Literal.LEAVING);
		}

		logger.debug(Literal.LEAVING);
	}

	public FinStatementResponse getForeClosureReport(FinReceiptData frd, FinStatementResponse stmtResp, int days) {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();

		ForeClosureReport closureReport = new ForeClosureReport();

		FinanceDetail fd = frd.getFinanceDetail();
		FinReceiptHeader rh = frd.getReceiptHeader();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (fm == null) {
			return stmtResp;
		}

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		rh.setReference(finReference);
		rh.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rh.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);

		rh.setReceiptDate(appDate);
		rh.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		rh.setAllocationType(AllocationType.AUTO);
		rh.setNewRecord(true);

		FinReceiptDetail rd = new FinReceiptDetail();
		rd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rh.getReceiptDetails().add(rd);

		if (days == 1) {
			days = 0;
		} else {
			days = days - 1;
		}

		Date valueDate = DateUtil.addDays(appDate, days);

		rh.setValueDate(valueDate);
		frd.setReceiptHeader(rh);
		frd.setFinID(finID);
		frd.setFinReference(finReference);
		frd.setBuildProcess("I");
		frd.setValueDate(valueDate);
		frd.setReceiptHeader(rh);
		frd.setForeClosureEnq(true);

		FinScheduleData schdData = fd.getFinScheduleData();

		try {

			frd = receiptService.calcuateDues(frd);

			List<FinFeeDetail> fees = schdData.getFinFeeDetailList();
			if (CollectionUtils.isNotEmpty(fees)) {
				closureReport.setActPercentage(fees.get(0).getActPercentage());
			}

			String calDate = DateUtil.format(appDate, "dd MMMM yyyy");
			String disDate = DateUtil.format(fm.getFinStartDate(), "dd'th' MMMM yyyy");
			Date prvEmiDate = frd.getOrgFinPftDtls().getPrvRpySchDate();
			int noOfIntDays = DateUtil.getDaysBetween(appDate, prvEmiDate);

			closureReport.setCalDate(calDate);
			closureReport.setFinReference(finReference);
			closureReport.setVanNumber(fm.getVanCode() == null ? "" : fm.getVanCode());
			closureReport.setFinAmount(fm.getFinAmount());
			closureReport.setFinAssetValue(fm.getFinAssetValue());
			closureReport.setDisbursalDate(disDate);
			closureReport.setChrgTillDate(DateUtil.format(appDate, "MMM  dd,yyyy"));

			CustomerDetails custDetails = fd.getCustomerDetails();
			if (custDetails == null || custDetails.getCustomer() == null) {
				return stmtResp;
			}

			Customer customer = custDetails.getCustomer();

			closureReport.setCustName(customer.getCustShrtName());
			closureReport.setCustCIF(customer.getCustCIF());

			long custID = customer.getCustID();
			closureReport.setAddress(getAddress(custID));

			String custName = getCustName(customer.getLovDescCustSalutationCodeName(), closureReport.getCustName());
			custName = getJointAccountDetails(fd, custName);

			for (GuarantorDetail gurantorsDetail : fd.getGurantorsDetailList()) {
				if (StringUtils.isNotEmpty(custName)) {
					custName = custName + "\n";
				}
				custName = custName + "\n" + StringUtils.trimToEmpty(gurantorsDetail.getGuarantorCIFName());
			}

			List<ReceiptAllocationDetail> radList = frd.getReceiptHeader().getAllocationsSummary();
			List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
			schdData.setFinanceScheduleDetails(schedules);

			FinReceiptData tempReceiptData = new FinReceiptData();
			tempReceiptData = frd.copyEntity();
			tempReceiptData.setForeClosureEnq(true);

			radList = tempReceiptData.getReceiptHeader().getAllocationsSummary();

			BigDecimal receivableAmt = BigDecimal.ZERO;
			BigDecimal bncCharge = BigDecimal.ZERO;
			BigDecimal profitAmt = BigDecimal.ZERO;
			BigDecimal principleAmt = BigDecimal.ZERO;
			BigDecimal tdsAmt = BigDecimal.ZERO;
			BigDecimal futTdsAmt = BigDecimal.ZERO;

			for (ReceiptAllocationDetail rad : radList) {
				switch (rad.getAllocationType()) {
				case Allocation.FUT_PRI:
					closureReport.setOutstandingPri(rad.getTotRecv());
					break;
				case Allocation.ODC:
					closureReport.setLatePayCharges(rad.getTotRecv());
					break;
				case Allocation.BOUNCE:
					bncCharge = rad.getTotRecv();
					break;
				case Allocation.MANADV:
					receivableAmt = receivableAmt.add(rad.getTotRecv());
					break;
				case Allocation.FUT_PFT:
					closureReport.setInstForTheMonth(rad.getTotRecv());
					break;
				case Allocation.PFT:
					profitAmt = rad.getTotRecv();
					break;
				case Allocation.PRI:
					principleAmt = rad.getTotRecv();
					break;
				case Allocation.TDS:
					tdsAmt = rad.getTotRecv();
					break;
				case Allocation.FUT_TDS:
					futTdsAmt = rad.getTotRecv();
					break;
				case Allocation.FEE:
					BigDecimal fcFeeAmtWithGst = rad.getTotRecv();
					closureReport.setForeClosFees(closureReport.getForeClosFees().add(fcFeeAmtWithGst));
					break;
				default:
					break;
				}
			}

			// Other Charges
			closureReport.setManualAdviceAmt(receivableAmt);

			// Cheque Bounce Charges
			closureReport.setCheqBncCharges(bncCharge);

			// Pending Installments
			closureReport.setPrincipalAmt(principleAmt);
			closureReport.setInterestAmt(profitAmt);
			closureReport.setPendingInsts(profitAmt.add(principleAmt));

			// TDS
			closureReport.setTds(tdsAmt.add(futTdsAmt));

			List<FinExcessAmount> excessList = frd.getReceiptHeader().getExcessAmounts();

			// Refunds (Excess Amount + EMI in advance)
			BigDecimal refund = BigDecimal.ZERO;
			for (FinExcessAmount finExcessAmount : excessList) {
				refund = refund.add(finExcessAmount.getBalanceAmt());
			}
			closureReport.setRefund(refund);

			// Advance EMI
			for (FinExcessAmount finExcessAmount : excessList) {
				if (StringUtils.equals(finExcessAmount.getAmountType(), RepayConstants.EXAMOUNTTYPE_EMIINADV)) {
					closureReport.setAdvInsts(finExcessAmount.getBalanceAmt());
				}
			}

			if (noOfIntDays > 0) {
				closureReport.setIntPerday(
						closureReport.getInstForTheMonth().divide(new BigDecimal(noOfIntDays), RoundingMode.CEILING));
			}

			// Issue Fixed 141142
			List<ManualAdvise> payableList = frd.getReceiptHeader().getPayableAdvises();
			BigDecimal payableAmt = BigDecimal.ZERO;

			for (ManualAdvise manualAdvise : payableList) {
				String taxType = "";
				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())) {
					taxType = FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE;
				}

				TaxAmountSplit taxSplit = GSTCalculator.calculateGST(finID, fm.getFinCcy(), taxType,
						manualAdvise.getAdviseAmount());

				payableAmt = payableAmt.add(manualAdvise.getBalanceAmt().add(taxSplit.gettGST()));

			}
			// Other Refunds (All payable Advise)
			closureReport.setOtherRefunds(payableAmt);
			// Refunds + other Refunds
			closureReport.setTotalRefunds(closureReport.getRefund().add(closureReport.getOtherRefunds()));
			closureReport.setTotalDues(closureReport.getLatePayCharges().add(closureReport.getPendingInsts())
					.add(closureReport.getCheqBncCharges()).add(closureReport.getOutstandingPri())
					.add(closureReport.getInstForTheMonth())
					.add(closureReport.getForeClosFees().add(closureReport.getManualAdviceAmt()))
					.subtract(closureReport.getTds()).subtract(closureReport.getTotWaiver())
					.subtract(closureReport.getTotalRefunds()));

			// Net Receivable
			closureReport
					.setNetReceivable(closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()).abs());

			if ((closureReport.getTotalDues().subtract(closureReport.getTotalRefunds()))
					.compareTo(BigDecimal.ZERO) < 0) {
				closureReport.setTotal("Net Payable");
			} else {
				closureReport.setTotal("Net Receivable");
			}

			int defaultDays = 7;
			int noOfdays = DateUtil.getDaysBetween(appDate, fm.getMaturityDate());
			if (defaultDays >= noOfdays) {
				defaultDays = noOfdays;
			}

			List<FinanceMain> financeMainList = financeDetailService.getFinanceMainForLinkedLoans(finReference);
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

			closureReport.setEntityDesc(fm.getEntityDesc());
			stmtResp.setForeclosureReport(closureReport);

		} catch (Exception e) {
			stmtResp = new FinStatementResponse();
			stmtResp.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			logger.debug(Literal.LEAVING);
			return stmtResp;
		}
		logger.debug(Literal.LEAVING);
		return stmtResp;

	}

	private String getJointAccountDetails(FinanceDetail fd, String custName) {
		List<JointAccountDetail> list = fd.getJointAccountDetailList();

		for (JointAccountDetail jad : list) {
			if (StringUtils.isNotEmpty(custName)) {
				custName = custName + "\n";
			}

			CustomerDetails custDetails = jad.getCustomerDetails();
			if (custDetails == null) {
				continue;
			}

			Customer customer = custDetails.getCustomer();
			if (customer == null) {
				continue;
			}

			custName = custName + getCustName(customer.getLovDescCustSalutationCodeName(), customer.getCustShrtName());
		}

		return custName;
	}

	private String getCustName(String salutation, String custName) {
		StringBuilder cName = new StringBuilder();
		cName.append(StringUtils.trimToEmpty(salutation));
		if (cName.length() > 0) {
			cName.append(" ");
		}
		cName.append(StringUtils.trimToEmpty(custName));

		return cName.toString();
	}

	private String getAddress(long custID) {
		List<CustomerAddres> caList = customerDetailsService.getCustomerDetailsbyIdandPhoneType(custID, "MOBILE")
				.getAddressList();

		CustomerAddres ca = caList.stream().filter(a -> a.getCustAddrPriority() == 5).findFirst()
				.orElse(new CustomerAddres());

		StringBuilder address = new StringBuilder();
		address.append(StringUtils.trimToEmpty(ca.getCustAddrHNbr()));
		if (StringUtils.trimToEmpty(ca.getCustAddrHNbr()).length() > 0) {
			address.append(" ");
		}
		address.append(StringUtils.trimToEmpty(ca.getCustFlatNbr()));
		if (StringUtils.trimToEmpty(ca.getCustFlatNbr()).length() > 0) {
			address.append(" ");
		}
		address.append(StringUtils.trimToEmpty(ca.getCustAddrStreet()));
		address.append("\n");
		address.append(StringUtils.trimToEmpty(ca.getLovDescCustAddrCityName()));
		if (StringUtils.trimToEmpty(ca.getLovDescCustAddrCityName()).length() > 0) {
			address.append("\n");
		}

		address.append(StringUtils.trimToEmpty(ca.getLovDescCustAddrProvinceName()));
		if (StringUtils.trimToEmpty(ca.getLovDescCustAddrProvinceName()).length() > 0) {
			address.append("\n");
		}
		address.append(StringUtils.trimToEmpty(ca.getCustAddrZIP()));
		if (StringUtils.trimToEmpty(ca.getCustAddrZIP()).length() > 0) {
			address.append("\n");
		}
		address.append(StringUtils.trimToEmpty(ca.getLovDescCustAddrCountryName()));

		return address.toString();
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
	private List<ReceiptAllocationDetail> calEarlySettleAmount(FinScheduleData schdData, Date valueDate) {
		logger.debug(Literal.ENTERING);

		String TDS_ROUNDING_MODE = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		int TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());

		BigDecimal tdsMultiplier = BigDecimal.ONE;
		FinanceMain fm = schdData.getFinanceMain();

		if (TDSCalculator.isTDSApplicable(fm)) {
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
						RoundingMode.HALF_DOWN);
			}
		}

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<FinanceScheduleDetail> tempScheduleDetails = new ArrayList<>();

		for (FinanceScheduleDetail schd : schedules) {
			tempScheduleDetails.add(schd.copyEntity());
		}

		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);

		List<ReceiptAllocationDetail> allocations = new ArrayList<>();

		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal pftAccruedTillNow = BigDecimal.ZERO;
		BigDecimal tdsAccruedTillNow = BigDecimal.ZERO;
		BigDecimal priBalance = BigDecimal.ZERO;
		BigDecimal totFeeAmount = BigDecimal.ZERO;
		boolean partAccrualReq = true;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;

		Date appDate = SysParamUtil.getAppDate();

		for (int i = 0; i < tempScheduleDetails.size(); i++) {
			curSchd = tempScheduleDetails.get(i);
			if (i != 0) {
				prvSchd = tempScheduleDetails.get(i - 1);
			}
			Date schdDate = curSchd.getSchDate();

			// Accrued Profit Calculation
			if (DateUtil.compare(schdDate, valueDate) < 0) {
				pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitSchd());

				if (tempScheduleDetails.size() == i + 1) {
					if (roundRequired == 1) {
						curSchd.setRepayAmount(CalculationUtil.roundAmount(curSchd.getRepayAmount(),
								fm.getCalRoundingMode(), fm.getRoundingTarget()));
						curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

						if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
							curSchd.setProfitSchd(BigDecimal.ZERO);
						}

						pftAccruedTillNow = curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance());
					}
				}

				priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));

				if (TDSCalculator.isTDSApplicable(fm)) {
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

				if (TDSCalculator.isTDSApplicable(fm)) {
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
					accruedPft = CalculationUtil.roundAmount(accruedPft, fm.getCalRoundingMode(),
							fm.getRoundingTarget());

					if (tempScheduleDetails.size() == i + 1) {
						if (roundRequired == 1) {
							curSchd.setRepayAmount(CalculationUtil.roundAmount(curSchd.getRepayAmount(),
									fm.getCalRoundingMode(), fm.getRoundingTarget()));
							curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

							if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
								curSchd.setProfitSchd(BigDecimal.ZERO);
							}

							accruedPft = curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance());
						}
					}

					pftAccruedTillNow = pftAccruedTillNow.add(accruedPft).add(prvSchd.getProfitBalance());
					priBalance = priBalance.add(prvSchd.getClosingBalance());

					if (TDSCalculator.isTDSApplicable(fm)) {
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
		ad.setAllocationType(Allocation.PRI);
		ad.setPaidAmount(priBalance);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(Allocation.PFT);
		ad.setPaidAmount(pftAmt);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(Allocation.TDS);
		ad.setPaidAmount(tdsAccruedTillNow);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(Allocation.FEE);
		ad.setPaidAmount(totFeeAmount);
		allocations.add(ad);

		// Calculate overdue Penalties
		Long finID = fm.getFinID();
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODDByFinRef(finID, null);
		if (DateUtil.compare(valueDate, appDate) != 0) {

			for (FinODDetails odDetail : overdueList) {
				if (odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
					overdueList = receiptService.getValueDatePenalties(schdData,
							priBalance.add(pftAmt).add(totFeeAmount), valueDate, null, true);
				}
			}

		}

		// Calculating Actual Sum of Penalty Amount & Late Pay Interest
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal penaltyBal = BigDecimal.ZERO;

		for (FinODDetails finODDetail : overdueList) {

			if (finODDetail.getFinODSchdDate().compareTo(valueDate) > 0) {
				continue;
			}

			latePayPftBal = latePayPftBal.add(finODDetail.getLPIBal());
			penaltyBal = penaltyBal.add(finODDetail.getTotPenaltyBal());
		}

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(Allocation.ODC);
		ad.setPaidAmount(penaltyBal);
		allocations.add(ad);

		ad = new ReceiptAllocationDetail();
		ad.setAllocationType(Allocation.LPFT);
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
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	public FinStatementResponse getReportSatatement(FinStatementRequest statementRequest) {
		logger.debug(Literal.ENTERING);

		FinStatementResponse stmtResponse;
		try {
			String finRefernce = statementRequest.getFinReference();
			String whereCond1 = null;
			Date sqlFromdate = null;
			Date sqltodate = null;
			if (APIConstants.REPORT_SOA.equals(statementRequest.getType())
					|| APIConstants.REPORT_SOA_REPORT.equals(statementRequest.getType())) {
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

					LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
					String userName = userDetails.getUserName();

					byte[] document = ReportsUtil.generatePDF(reportName, soa, list, userName);
					if (document != null) {
						String location = null;
						if (StringUtils.equals(statementRequest.getType(), APIConstants.REPORT_SOA)) {
							// location = moveToS3Bucket(document, finReference, statementRequest.getType(),
							// soa.getFinDivision());
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
							// stmtResponse.setLocation(location);
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
					// location = moveToS3Bucket(document, statementRequest.getFinReference(),
					// statementRequest.getType(), null);
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
					// stmtResponse.setLocation(location);
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
	 * @param statementRequest - {@link FinStatementRequest}
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
			year = DateUtil.getYear(fromdate);
		} else {
			year = DateUtil.getYear(appDate);
		}

		startDate = DateUtil.getDate(year, 3, 1);
		endDate = DateUtil.getDate(year + 1, 2, 31);

		InterestCertificate intCert = interestCertificateService
				.getInterestCertificateDetails(statementRequest.getFinReference(), startDate, endDate, false);
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

	private byte[] doShowReport(String whereCond, String whereCond1, Date fromDate, Date toDate, String finRefernce)
			throws SQLException {

		logger.debug(Literal.ENTERING);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		Map<String, Object> reportArgumentsMap = new HashMap<String, Object>(10);
		String envVariable = System.getenv("APP_ROOT_PATH");
		PathUtil.setRootPath(envVariable);

		reportArgumentsMap.put("userName", userDetails.getUserId());
		reportArgumentsMap.put("reportHeading", reportConfiguration.getReportHeading());
		reportArgumentsMap.put("reportGeneratedBy", "Report Generated by penApps PFF");// Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		reportArgumentsMap.put("appDate", SysParamUtil.getAppDate());
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
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (con != null) {
				con.close();
			}
			con = null;
			reportDataSourceObj = null;
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

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

}
