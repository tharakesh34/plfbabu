package com.pennant.datamigration.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BaseRateCodeDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.SplRateCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.datamigration.dao.BasicLoanReconDAO;
import com.pennant.datamigration.dao.SourceDataSummaryDAO;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.model.SourceDataSummary;
import com.pennant.datamigration.service.DMTransactionFetch;
import com.pennanttech.pff.core.TableType;

public class DMTransactionFetchImpl implements DMTransactionFetch {
	private static Logger logger = Logger.getLogger(DMTransactionFetchImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private PaymentDetailDAO paymentDetailDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private ProvisionDAO provisionDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private FinPlanEmiHolidayDAO finPlanEmiHolidayDAO;
	private MandateDAO mandateDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private GuarantorDetailDAO guarantorDetailDAO;

	// Data Not Received???
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceTypeDAO financeTypeDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private SourceDataSummaryDAO sourceDataSummaryDAO;
	private BasicLoanReconDAO basicLoanReconDAO;

	// DAOs required for validations
	private CustomerDAO customerDAO;
	private BranchDAO branchDAO;
	private BaseRateCodeDAO baseRateCodeDAO;
	private SplRateCodeDAO splRateCodeDAO;
	private FeeTypeDAO feeTypeDAO;

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void deleteFromSourceSummary() {
		SourceDataSummary dataSummary = new SourceDataSummary();
		sourceDataSummaryDAO.deleteSummary(dataSummary);
	}

	public void cleanDestination() {
		basicLoanReconDAO.cleanDestination();
	}

	public List <FinanceType> getFinTypeList(String type) {
		List <FinanceType> finTypeList = getBasicLoanReconDAO().getDMFinTypes(type);
		
		return finTypeList;
	}
	
	public List <FeeTypeVsGLMapping> getFeeVsGLList() {
		List <FeeTypeVsGLMapping> feeVsGLList = getBasicLoanReconDAO().getFeeTypeVsGLMappings();
		
		return feeVsGLList;
	}
	
	public MigrationData getFinanceDetailsFromSource(String finReference, ReferenceID rid, String type) {
		logger.debug("Entering");

		boolean printTime = false;
		MigrationData sMD = new MigrationData();

		// FinanceMain
		Date sysDateI1 = new Date();
		sMD.setFinanceMain(getFinanceMainDAO().getDMFinanceMainByRef(finReference, type));
		Date sysDateI2 = new Date();
		if (printTime) {
			System.out.println("Time for FinanceMain in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// Finance Type
		sysDateI1 = new Date();
		sMD.setFinType(new FinanceType());
		for (int i = 0; i < rid.getFinTypes().size(); i++) {
			if (StringUtils.equals(rid.getFinTypes().get(i).getFinType(), sMD.getFinanceMain().getFinType())) {
				sMD.setFinType(rid.getFinTypes().get(i));
				break;
			}
		}
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for FinanceType in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinDisbursements
		sysDateI1 = new Date();
		sMD.setFinDisbursements(getFinanceDisbursementDAO().getDMFinanceDisbursementDetails(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for FinDisbursements in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinAdvancePayments
		sysDateI1 = new Date();
		sMD.setFinAdvancePayments(getFinAdvancePaymentsDAO().getFinAdvancePaymentsByFinRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out
					.println("Time for FinAdvancePayments in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinScheduleDetails
		sysDateI1 = new Date();
		sMD.setFinScheduleDetails(getFinanceScheduleDetailDAO().getDMFinScheduleDetails(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out
					.println("Time for FinScheduleDetails in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinServiceInstructions
		sysDateI1 = new Date();
		sMD.setFinServiceInstructions(getFinServiceInstructionDAO().getFinServiceInstructions(finReference, type, ""));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println(
					"Time for FinServiceInstructions in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinReceiptHeader
		sysDateI1 = new Date();
		sMD.setFinReceiptHeaders(getFinReceiptHeaderDAO().getReceiptHeadersByRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for FinReceiptHeader in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinReceiptDetail
		sysDateI1 = new Date();
		sMD.setFinReceiptDetails(getFinReceiptDetailDAO().getDMFinReceiptDetailByFinRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for FinReceiptDetail in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// AllocationDetails
		sysDateI1 = new Date();
		sMD.setReceiptAllocationDetails(getReceiptAllocationDetailDAO().getDMAllocationsByReference(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out
					.println("Time for AllocationDetails in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// RepayHeader
		sysDateI1 = new Date();
		sMD.setFinRepayHeaders(getFinanceRepaymentsDAO().getFinRepayHeadersByRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for RepayHeader in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// PresentmentDetail
		sysDateI1 = new Date();
		sMD.setPresentmentDetails(getPresentmentDetailDAO().getDMPresentmentDetailsByRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out
					.println("Time for PresentmentDetail in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// ManualAdvise
		sMD.setManualAdvises(getManualAdviseDAO().getManualAdvisesByFinRef(finReference, type));
		sysDateI2 = new Date();
		if (printTime) {
			sysDateI1 = new Date();
			System.out.println("Time for ManualAdvise in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FionODDetails
		sysDateI1 = new Date();
		sMD.setFinODDetails(getFinODDetailsDAO().getFinODDetailsByFinRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for FionODDetails in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// ODPenaltyRates
		sysDateI1 = new Date();
		sMD.setPenaltyrate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for ODPenaltyRates in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// Provisions
		sysDateI1 = new Date();
		sMD.setProvision(getProvisionDAO().getProvisionById(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for Provisions in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinFeeDetails
		sysDateI1 = new Date();
		sMD.setFinFeeDetails(getFinFeeDetailDAO().getDMFinFeeDetailByFinRef(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println("Time for FinFeeDetails in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		logger.debug("Leaving");
		return sMD;
	}
	
	//This code never executed. only kept for back up purpose. In case data to be fetched then move the code to above method
	public MigrationData getFinanceDetailsFromSource2(String finReference, ReferenceID rid, String type) {
		logger.debug("Entering");

		boolean printTime = false;
		MigrationData sMD = new MigrationData();
		Date sysDateI1 = new Date();
		Date sysDateI2 = new Date();

		// RepayScheduleDetail
		sysDateI1 = new Date();
		sMD.setRepayScheduleDetails(getFinanceRepaymentsDAO().getDMRpySchdList(finReference, type));
		
		if (printTime) {
			sysDateI2 = new Date();
			System.out.println(
					"Time for RepayScheduleDetail in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// PresentmenHeader
		sysDateI1 = new Date();
		sMD.setPresentmentHeaders(getPresentmentDetailDAO().getPresentmentHeadersByRef(finReference, type));
		sysDateI2 = new Date();
		if (printTime) {
			System.out
					.println("Time for PresentmentHeader in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// ManualAdviseMovements
		sysDateI1 = new Date();
		sMD.setManualAdviseMovements(getManualAdviseDAO().getDMAdviseMovementsByFinRef(finReference, type));
		sysDateI2 = new Date();
		if (printTime) {
			System.out.println(
					"Time for ManualAdviseMovements in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// NO DATA AVAILABLE
		// migrationData.setPaymentHeaders(paymentHeaders);
		// migrationData.setPaymentDetails(paymentDetails);
		// migrationData.setPaymentInstructions(paymentInstructions);

		// NOT IN USE AT CLIENT
		// migrationData.setFinFeeScheduleDetails(finFeeScheduleDetails);
		// migrationData.setFinPlanEMIHolidays(finPlanEMIHolidays);

		long custID = sMD.getFinanceMain().getCustID();

		// Mandates
		sysDateI1 = new Date();
		sMD.setMandates(getMandateDAO().getApprovedMandatesByCustomerId(custID, type));
		sysDateI2 = new Date();
		if (printTime) {
			System.out.println("Time for Mandates in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// FinExcessAmounts
		sysDateI1 = new Date();
		sMD.setFinExcessAmounts(getFinExcessAmountDAO().getAllExcessAmountsByRef(finReference, type));
		sysDateI2 = new Date();
		if (printTime) {
			System.out.println("Time for FinExcessAmounts in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// JointAccounts
		sysDateI1 = new Date();
		sMD.setJointAccountDeatils(getJountAccountDetailDAO().getJountAccountDetailByFinRef(finReference, type));
		sysDateI2 = new Date();
		if (printTime) {
			System.out.println("Time for JointAccounts in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		// Gaurantors
		sysDateI1 = new Date();
		sMD.setGuarantorDetails(getGuarantorDetailDAO().getGuarantorDetailByFinRef(finReference, type));
		sysDateI2 = new Date();
		if (printTime) {
			System.out.println("Time for Gaurantors in MSeconds: " + (sysDateI2.getTime() - sysDateI1.getTime()));
		}

		logger.debug("Leaving");
		return sMD;
	}

	public SourceDataSummary setSourceSummary(MigrationData md, String type) {
		SourceDataSummary sds = new SourceDataSummary();
		sds = setSourceFinanceMain(md, sds, type);
		sds = setSourceDisbursementDetails(md, sds);
		// sds = setSourceAdvancePayments(md, sds);
		sds = setSourceFinScheduleDetails(md, sds);
		sds = setSourceFinReceiptHeader(md, sds);
		// sds = setSourceFinReceiptDetail(md, sds);
		// sds = setSourceReceiptAllocationDetail(md, sds);
		// sds = setSourceFinRepayHeader(md, sds);
		// sds = setSourceFinRepayScheduleDetail(md, sds);
		sds = setSourcePresentmentDetail(md, sds);
		sds = setSourceManualAdvise(md, sds);
		sds = setSourceManualAdviseMovements(md, sds);
		sds = setSourceFinODDetails(md, sds);
		sds = setSourceProvisions(md, sds);
		sds = setSourceDisbFinFeeDetails(md, sds);
		sds = setSourceFinExcess(md, sds);
		// sds = validateSummary(md, sds);

		// getSourceDataSummaryDAO().saveSummary(sds);

		return sds;
	}

	// Finance Main Summary
	public SourceDataSummary setSourceFinanceMain(MigrationData md, SourceDataSummary sds, String type) {

		FinanceMain fm = md.getFinanceMain();

		sds.setFm_FinReference(fm.getFinReference());
		sds.setFm_CustID(fm.getCustID());
		sds.setFm_FinBranch(fm.getFinBranch());
		sds.setFm_NumberOfTerms(fm.getNumberOfTerms());
		sds.setFm_RepayProfitRate(fm.getRepayProfitRate());
		sds.setFm_TotalGrossPft(fm.getTotalGrossPft());

		sds.setFm_TotalRepayAmt(fm.getTotalRepayAmt());
		sds.setFm_FirstRepay(fm.getFirstRepay());
		sds.setFm_LastRepay(fm.getLastRepay());
		sds.setFm_FinStartDate(fm.getFinStartDate());
		sds.setFm_FinAmount(fm.getFinAmount());
		sds.setFm_FinRepaymentAmount(fm.getFinRepaymentAmount());
		sds.setFm_FeeChargeAmt(fm.getFeeChargeAmt());
		sds.setFm_FinAssetValue(fm.getFinAssetValue());
		sds.setFm_FinCurrAssetValue(fm.getFinCurrAssetValue());
		sds.setFm_JointAc(fm.isJointAccount());
		sds.setFm_JointCustID(fm.getJointCustId());
		sds.setFm_MandateID(fm.getMandateID());
		sds.setFm_AlowBPI(fm.isAlwBPI());
		sds.setFm_BPITreatment(fm.getBpiTreatment());
		sds.setFm_AlwMultiDisb(fm.isAlwMultiDisb());
		sds.setFm_BPIAmount(fm.getBpiAmount());
		sds.setFm_DeductFeeDisb(fm.getDeductFeeDisb());
		sds.setFm_Cpz(fm.isAllowRepayCpz());
		sds.setFm_CpzAmount(fm.getTotalCpz());

		// Validate Finance Main

		// Customer Not Found
		if (customerDAO.getCustomerCountByCustID(sds.getFm_CustID(), "") <= 0) {
			sds = setRemarks(sds, "/1", "i");
		}

		// Branch Not Found
		if (!branchDAO.isDuplicateKey(sds.getFm_FinBranch(), TableType.MAIN_TAB)) {
			sds = setRemarks(sds, "/2", "i");
		}

		// Base Rate Not Found
		if (!StringUtils.isBlank(fm.getRepayBaseRate())) {
			if (getBaseRateCodeDAO().getBaseRateCodeById(fm.getRepayBaseRate(), "") == null) {
				sds = setRemarks(sds, "/3", "i");
			}
		}

		// Special Rate
		if (!StringUtils.isBlank(fm.getRepaySpecialRate())) {
			if (getSplRateCodeDAO().getSplRateCodeById(fm.getRepaySpecialRate(), "") == null) {
				sds = setRemarks(sds, "/4", "i");
			}
		}

		// Repay Rate Not Defined
		if (StringUtils.isBlank(fm.getRepayBaseRate()) && fm.getRepayProfitRate().compareTo(BigDecimal.ZERO) == 0) {
			sds = setRemarks(sds, "/5", "e");
		}

		// Repay Review
		if (fm.isAllowRepayRvw()) {
			if (StringUtils.isBlank(fm.getRepayRvwFrq())) {
				sds = setRemarks(sds, "/6", "w");
			} else if (FrequencyUtil.validateFrequency(fm.getRepayRvwFrq()) != null) {
				sds = setRemarks(sds, "/7", "w");
			}
		}

		// Capitalize Review
		if (fm.isAllowRepayCpz()) {
			if (StringUtils.isBlank(fm.getRepayCpzFrq())) {
				sds = setRemarks(sds, "/8", "w");
			} else if (FrequencyUtil.validateFrequency(fm.getRepayCpzFrq()) != null) {
				sds = setRemarks(sds, "/9", "w");
			}
		}

		// Capitalize FALSE but capitalize Amount
		if (fm.isAllowRepayCpz() && fm.getTotalCpz().compareTo(BigDecimal.ZERO) > 0) {
			sds = setRemarks(sds, "/10", "w");
		}

		// Closing Status
		if (!StringUtils.isBlank(fm.getClosingStatus())) {
			sds = setRemarks(sds, "/11", "e");
		}

		// Inactive
		if (!fm.isFinIsActive()) {
			sds = setRemarks(sds, "/12", "e");
		}

		// Joint Account
		if (fm.isJointAccount()) {
			if (fm.getJointCustId() == 0) {
				sds = setRemarks(sds, "/13", "w");
			} else if (customerDAO.getCustomerCountByCustID(fm.getJointCustId(), "") <= 0) {
				sds = setRemarks(sds, "/14", "i");
			}
		}

		// Repayment Methods
		if (!StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_DDM)
				&& !StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_ECS)
				&& !StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_NACH)
				&& !StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_PDC)) {
			sds = setRemarks(sds, "/15", "w");
		}

		// Mandate ID Mandatory
		if (StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_DDM)
				|| StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_ECS)
				|| StringUtils.equals(fm.getFinRepayMethod(), MandateConstants.TYPE_NACH)) {
			if (fm.getMandateID() == 0) {
				sds = setRemarks(sds, "/16", "w");
			} else if (getMandateDAO().checkMandates(fm.getFinReference(), fm.getMandateID())) {
				sds = setRemarks(sds, "/17", "w");
			}

		}

		return sds;
	}

	// Finance Disbursement Details Summary
	public SourceDataSummary setSourceDisbursementDetails(MigrationData md, SourceDataSummary sds) {

		List<FinanceDisbursement> fddList = md.getFinDisbursements();

		// As there is no way to link fees to exact transaction blindly fetching
		// all fees at once
		boolean isFeesFetched = false;

		// Go through Disbursement Details
		for (int i = 0; i < fddList.size(); i++) {
			FinanceDisbursement fdd = fddList.get(i);
			sds.setFdd_TotDisbCount(i);
			sds.setFdd_TotDisbAmount(sds.getFdd_TotDisbAmount().add(fdd.getDisbAmount()));
			sds.setFdd_TotFeeChargeAmt(sds.getFdd_TotFeeChargeAmt().add(fdd.getFeeChargeAmt()));

			if (fdd.getDisbDate().compareTo(sds.getFm_FinStartDate()) == 0) {
				sds.setFdd_FirstDisbAmount(sds.getFdd_FirstDisbAmount().add(fdd.getDisbAmount()));
			}

			if (!isFeesFetched) {
				sds = setSourceDisbFinFeeDetails(md, sds);
				isFeesFetched = true;
			}

			// Find Disbursement Instructions for the Disbursement
			sds = setSourceAdvancePayments(md, sds, i);

		}

		// First Disbursement Date Match with Fin Start date
		if (sds.getFdd_FirstDisbAmount().compareTo(BigDecimal.ZERO) == 0) {
			sds = setRemarks(sds, "/18", "w");
		}

		if (sds.getFdd_TotDisbAmount().compareTo(sds.getFm_FinCurrAssetValue()) != 0) {
			sds = setRemarks(sds, "/19", "w");
		}

		if (sds.getFdd_TotFeeChargeAmt().compareTo(sds.getFm_FeeChargeAmt()) != 0) {
			sds = setRemarks(sds, "/20", "w");
		}

		if (sds.getFdd_TotFeeChargeAmt().compareTo(sds.getFfd_TotalPaidFee()) != 0) {
			sds = setRemarks(sds, "/23", "w");
		}

		return sds;
	}

	// Finance Advance Payments Summary
	public SourceDataSummary setSourceAdvancePayments(MigrationData md, SourceDataSummary sds, int iDisb) {

		List<FinAdvancePayments> fapList = md.getFinAdvancePayments();
		FinanceDisbursement fdd = md.getFinDisbursements().get(iDisb);
		BigDecimal instructedAmount = BigDecimal.ZERO;
		BigDecimal instructionDueAmount = fdd.getDisbAmount().subtract(fdd.getFeeChargeAmt());

		for (int i = 0; i < fapList.size(); i++) {
			FinAdvancePayments fap = fapList.get(i);

			if (fdd.getDisbDate().compareTo(fap.getLlDate()) != 0) {
				continue;
			}

			if (fdd.getDisbSeq() != fap.getDisbSeq()) {
				continue;
			}

			if (!StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_AWAITCON)
					&& !StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_CANCEL)
					&& !StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_REJECTED)
					&& !StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_PAID)) {
				sds = setRemarks(sds, "/21", "w");
			}

			if (StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_PAID)
					|| StringUtils.equals(fap.getStatus(), DisbursementConstants.STATUS_AWAITCON)) {
				sds.setFap_TotInstructions(i);
				sds.setFap_TotInstructAmount(sds.getFap_TotInstructAmount().add(fap.getAmtToBeReleased()));
				instructedAmount = instructedAmount.add(fap.getAmtToBeReleased());
			}
		}

		if (instructedAmount.compareTo(instructionDueAmount) != 0) {
			sds = setRemarks(sds, "/22", "w");
		}

		return sds;
	}

	// Finance Schedule Details Summary
	public SourceDataSummary setSourceFinScheduleDetails(MigrationData md, SourceDataSummary sds) {

		List<FinanceScheduleDetail> fsdList = md.getFinScheduleDetails();
		FinanceMain fm = md.getFinanceMain();

		boolean isFirstRepayDate = false;

		for (int i = 0; i < fsdList.size(); i++) {
			FinanceScheduleDetail fsd = fsdList.get(i);
			sds.setFsd_ProfitSchd(sds.getFsd_ProfitSchd().add(fsd.getProfitSchd()));
			sds.setFsd_PrincipalSchd(sds.getFsd_PrincipalSchd().add(fsd.getPrincipalSchd()));
			sds.setFsd_RepayAmount(sds.getFsd_RepayAmount().add(fsd.getRepayAmount()));
			sds.setFsd_SchdPftPaid(sds.getFsd_SchdPftPaid().add(fsd.getSchdPftPaid()));
			sds.setFsd_SchdPftPaid(sds.getFsd_SchdPftPaid().add(fsd.getSchdPftPaid()));
			sds.setFsd_CpzAmount(sds.getFsd_CpzAmount().add(fsd.getCpzAmount()));

			if (fsd.getInstNumber() < 0) {
				if (fsd.getSchDate().compareTo(fm.getFinStartDate()) > 0) {
					fsd.setBpiOrHoliday(FinanceConstants.FLAG_BPI);
				}

				// FOR EQUITAS
				fm.setAlwBPI(true);
				fm.setBpiAmount(fsd.getProfitSchd());
				fm.setBpiTreatment(FinanceConstants.BPI_SCHEDULE);
				fm.setBpiResetReq(false);
				fm.setBpiPftDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
			}

			if (!isFirstRepayDate) {
				if (fsd.getSchDate().compareTo(fm.getFinStartDate()) > 0) {
					if (fsd.getBpiOrHoliday() != null && fsd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						isFirstRepayDate = true;
						sds.setFsd_FirstRepay(fsd.getRepayAmount());
					}
				}
			}

			if (fm.getMaturityDate().compareTo(fsd.getSchDate()) == 0) {
				sds.setFsd_LastRepay(fsd.getRepayAmount());
			}

		}

		return sds;
	}

	// Finance Receipt Header Summary
	public SourceDataSummary setSourceFinReceiptHeader(MigrationData md, SourceDataSummary sds) {

		List<FinReceiptHeader> rchList = md.getFinReceiptHeaders();

		for (int i = 0; i < rchList.size(); i++) {
			FinReceiptHeader rch = rchList.get(i);

			if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)
					|| StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
				sds.setRch_TotalReceiptAmount(sds.getRch_TotalReceiptAmount().add(rch.getReceiptAmount()));
				sds.setRch_TotalWaivedAmount(sds.getRch_TotalWaivedAmount().add(rch.getWaviedAmt()));
				sds.setRch_TotalFeeAmount(sds.getRch_TotalFeeAmount().add(rch.getTotFeeAmount()));
			}

			sds = setSourceFinReceiptDetail(md, sds, i);
			sds = setSourceReceiptAllocationDetail(md, sds, i);
		}

		return sds;
	}

	// Finance Receipt Detail Summary
	public SourceDataSummary setSourceFinReceiptDetail(MigrationData md, SourceDataSummary sds, int iRcpt) {

		List<FinReceiptDetail> rcdList = md.getFinReceiptDetails();
		FinReceiptHeader rch = md.getFinReceiptHeaders().get(iRcpt);

		for (int i = 0; i < rcdList.size(); i++) {
			FinReceiptDetail rcd = rcdList.get(i);
			if (rch.getReceiptID() != rcd.getReceiptID()) {
				continue;
			}

			if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)
					|| StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
				sds.setRcd_TotalReceiptAmount(sds.getRcd_TotalReceiptAmount().add(rcd.getAmount()));
			}

			if (rch.getReceiptAmount().compareTo(rcd.getAmount()) != 0) {
				sds = setRemarks(sds, "/30", "w");
			}

			sds = setSourceFinRepayHeader(md, sds, i);
		}

		return sds;
	}

	// Finance Receipt Allocation Details Summary
	public SourceDataSummary setSourceReceiptAllocationDetail(MigrationData md, SourceDataSummary sds, int iRcpt) {

		List<ReceiptAllocationDetail> radList = md.getReceiptAllocationDetails();
		FinReceiptHeader rch = md.getFinReceiptHeaders().get(iRcpt);
		BigDecimal allocatedAmount = BigDecimal.ZERO;

		for (int i = 0; i < radList.size(); i++) {
			ReceiptAllocationDetail rad = radList.get(i);

			if (rch.getReceiptID() != rad.getReceiptID()) {
				continue;
			}

			allocatedAmount = allocatedAmount.add(rad.getPaidAmount());

			if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("ADMIN")) {
				sds.setRad_TOTPAID_ADMIN(sds.getRad_TOTPAID_ADMIN().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("APF")) {
				sds.setRad_TOTPAID_APF(sds.getRad_TOTPAID_APF().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("CHDIS")) {
				sds.setRad_TOTPAID_CHDIS(sds.getRad_TOTPAID_CHDIS().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("CRS")) {
				sds.setRad_TOTPAID_CRS(sds.getRad_TOTPAID_CRS().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("CS")) {
				sds.setRad_TOTPAID_CS(sds.getRad_TOTPAID_CS().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("DOCC")) {
				sds.setRad_TOTPAID_DOCC(sds.getRad_TOTPAID_DOCC().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("EXS")) {
				sds.setRad_TOTPAID_EXS(sds.getRad_TOTPAID_EXS().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("FRE")) {
				sds.setRad_TOTPAID_FRE(sds.getRad_TOTPAID_FRE().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("GINS")) {
				sds.setRad_TOTPAID_GINS(sds.getRad_TOTPAID_GINS().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("INS")) {
				sds.setRad_TOTPAID_INS(sds.getRad_TOTPAID_INS().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("INST")) {
				sds.setRad_TOTPAID_INST(sds.getRad_TOTPAID_INST().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("INT")) {
				sds.setRad_TOTPAID_INT(sds.getRad_TOTPAID_INT().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("IT")) {
				sds.setRad_TOTPAID_IT(sds.getRad_TOTPAID_IT().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("NIA")) {
				sds.setRad_TOTPAID_NIA(sds.getRad_TOTPAID_NIA().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("ODC")) {
				sds.setRad_TOTPAID_ODC(sds.getRad_TOTPAID_ODC().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("PAC")) {
				sds.setRad_TOTPAID_PAC(sds.getRad_TOTPAID_PAC().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("PF")) {
				sds.setRad_TOTPAID_PF(sds.getRad_TOTPAID_PF().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("PRIN")) {
				sds.setRad_TOTPAID_PRIN(sds.getRad_TOTPAID_PRIN().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("RI")) {
				sds.setRad_TOTPAID_RI(sds.getRad_TOTPAID_RI().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("SC")) {
				sds.setRad_TOTPAID_SC(sds.getRad_TOTPAID_SC().add(rad.getPaidAmount()));
			} else if (StringUtils.trimToEmpty(rad.getAllocationType()).equals("TPF")) {
				sds.setRad_TOTPAID_TPF(sds.getRad_TOTPAID_TPF().add(rad.getPaidAmount()));
			}

		}

		if (allocatedAmount.compareTo(rch.getReceiptAmount()) != 0) {
			sds = setRemarks(sds, "/31", "w");
		}

		if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)
				|| StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			sds.setRad_TotalAllocated(sds.getRad_TotalAllocated().add(allocatedAmount));
		}

		return sds;
	}

	// Finance Repayment Header Summary
	public SourceDataSummary setSourceFinRepayHeader(MigrationData md, SourceDataSummary sds, int iRcpt) {

		List<FinRepayHeader> rphList = md.getFinRepayHeaders();
		FinReceiptDetail rcd = md.getFinReceiptDetails().get(iRcpt);
		BigDecimal repayAmount = BigDecimal.ZERO;
		BigDecimal repayPri = BigDecimal.ZERO;
		BigDecimal repayPft = BigDecimal.ZERO;

		for (int i = 0; i < rphList.size(); i++) {
			FinRepayHeader rph = rphList.get(i);

			if (rph.getReceiptSeqID() != rcd.getReceiptSeqID()) {
				continue;
			}

			repayAmount = repayAmount.add(rph.getRepayAmount());
			repayPri = repayPri.add(rph.getPriAmount());
			repayPft = repayPft.add(rph.getPftAmount());

			sds.setRph_TotalRepayAmt(sds.getRph_TotalRepayAmt().add(rph.getRepayAmount()));
			sds.setRph_TotalPriAmount(sds.getRph_TotalPriAmount().add(rph.getPriAmount()));
			sds.setRph_TotalPftAmount(sds.getRph_TotalPftAmount().add(rph.getPftAmount()));

			sds = setSourceFinRepayScheduleDetail(md, sds, i);
		}

		if (repayAmount.compareTo(rcd.getAmount()) != 0) {
			sds = setRemarks(sds, "/32", "w");
		}

		return sds;
	}

	// Finance Repayment Schedule Summary
	public SourceDataSummary setSourceFinRepayScheduleDetail(MigrationData md, SourceDataSummary sds, int iRpy) {

		List<RepayScheduleDetail> rpsdList = md.getRepayScheduleDetails();
		FinRepayHeader rph = md.getFinRepayHeaders().get(iRpy);
		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;

		for (int i = 0; i < rpsdList.size(); i++) {
			RepayScheduleDetail rpsd = rpsdList.get(i);

			if (rph.getRepayID() != rpsd.getRepayID()) {
				continue;
			}

			sds.setRpsd_ProfitSchd(sds.getRpsd_ProfitSchd().add(rpsd.getProfitSchd()));
			sds.setRpsd_ProfitSchdPaid(sds.getRpsd_ProfitSchdPaid().add(rpsd.getProfitSchdPaid()));
			sds.setRpsd_PrincipalSchd(sds.getRpsd_PrincipalSchd().add(rpsd.getPrincipalSchd()));
			sds.setRpsd_PrincipalSchdPaid(sds.getRpsd_PrincipalSchdPaid().add(rpsd.getPrincipalSchdPaid()));

			priPaid = priPaid.add(rpsd.getPrincipalSchdPaid());
			pftPaid = pftPaid.add(rpsd.getProfitSchdPaid());
		}

		return sds;
	}

	// Finance Presentment Detail Summary
	public SourceDataSummary setSourcePresentmentDetail(MigrationData md, SourceDataSummary sds) {

		List<PresentmentDetail> pdList = md.getPresentmentDetails();

		for (int i = 0; i < pdList.size(); i++) {
			PresentmentDetail pd = pdList.get(i);
			sds.setPd_TotalSchAmtDue(sds.getPd_TotalSchAmtDue().add(pd.getPresentmentAmt()));
			sds.setPd_TotalPresentmentAmt(sds.getPd_TotalSchAmtDue().add(pd.getPresentmentAmt()));
		}

		// Pending presentment id, mandated id and receipt id
		return sds;
	}

	// Finance Manual Advise Summary
	public SourceDataSummary setSourceManualAdvise(MigrationData md, SourceDataSummary sds) {

		List<ManualAdvise> maList = md.getManualAdvises();

		if (maList == null) {
			return sds;
		}

		for (int i = 0; i < maList.size(); i++) {
			ManualAdvise ma = maList.get(i);
			sds.setMa_TotalAdviseAmount(sds.getMa_TotalAdviseAmount().add(ma.getAdviseAmount()));
			sds.setMa_TotalPaidAmount(sds.getMa_TotalPaidAmount().add(ma.getPaidAmount()));
		}

		// Pending FeeTypeID, receipt id
		return sds;
	}

	// Finance Manual Advise Movements Summary
	public SourceDataSummary setSourceManualAdviseMovements(MigrationData md, SourceDataSummary sds) {

		List<ManualAdviseMovements> mamList = md.getManualAdviseMovements();

		for (int i = 0; i < mamList.size(); i++) {
			ManualAdviseMovements mam = mamList.get(i);
			sds.setMam_TotalMovementAmount(sds.getMam_TotalMovementAmount().add(mam.getMovementAmount()));
			sds.setMam_TotalPaidAmount(sds.getMam_TotalPaidAmount().add(mam.getPaidAmount()));
		}

		// Pending AdviseID, receipt id
		return sds;
	}

	// Finance OD Details Summary
	public SourceDataSummary setSourceFinODDetails(MigrationData md, SourceDataSummary sds) {

		List<FinODDetails> fodList = md.getFinODDetails();

		for (int i = 0; i < fodList.size(); i++) {
			FinODDetails fod = fodList.get(i);
			sds.setFod_TotalODAmount(sds.getFod_TotalODAmount().add(fod.getFinCurODAmt()));
			sds.setFod_TotalODPrincipal(sds.getFod_TotalODPrincipal().add(fod.getFinCurODPri()));
			sds.setFod_TotalODProfit(sds.getFod_TotalODProfit().add(fod.getFinCurODPft()));
			sds.setFod_TotalPenaltyAmount(sds.getFod_TotalPenaltyAmount().add(fod.getTotPenaltyAmt()));
			sds.setFod_TotalPenaltyPaid(sds.getFod_TotalPenaltyPaid().add(fod.getTotPenaltyPaid()));
			sds.setFod_TotalPenaltyBal(sds.getFod_TotalPenaltyBal().add(fod.getTotPenaltyBal()));
		}

		return sds;
	}

	// Provisions Summary
	public SourceDataSummary setSourceProvisions(MigrationData md, SourceDataSummary sds) {

		Provision prv = md.getProvision();

		if (prv == null) {
			return sds;
		}

		sds.setPrv_DueDays(prv.getDuedays());
		sds.setPrv_DPDBucketID(prv.getDpdBucketID());
		sds.setPrv_NPABucketID(prv.getNpaBucketID());
		sds.setPrv_PrincipalDue(prv.getPrincipalDue());
		sds.setPrv_ProfitDue(prv.getProfitDue());
		sds.setPrv_ProvisionAmtCal(prv.getProvisionAmtCal());
		return sds;
	}

	// Finance Fee Details Summary
	public SourceDataSummary setSourceDisbFinFeeDetails(MigrationData md, SourceDataSummary sds) {

		List<FinFeeDetail> ffdList = md.getFinFeeDetails();
		FeeType feeType = null;
		boolean isFeeTypeIssue = false;

		for (int i = 0; i < ffdList.size(); i++) {
			FinFeeDetail ffd = ffdList.get(i);

			if (!StringUtils.equals(ffd.getFinEvent(), AccountEventConstants.ACCEVENT_ADDDBSP)) {
				continue;
			}

			sds.setFfd_TotalDisbCalFee(sds.getFfd_TotalDisbCalFee().add(ffd.getCalculatedAmount()));
			sds.setFfd_TotalActualFee(sds.getFfd_TotalActualFee().add(ffd.getActualAmount()));
			sds.setFfd_TotalPaidFee(sds.getFfd_TotalPaidFee().add(ffd.getPaidAmount()));

			if (!isFeeTypeIssue) {
				feeType = getFeeTypeDAO().getFeeTypeById(ffd.getFeeID(), "");

				if (feeType == null) {
					isFeeTypeIssue = true;
					sds = setRemarks(sds, "/24", "i");
				}
			}
		}

		// Pending FeetypeID found?
		return sds;
	}

	// Finance Excess Amount Summary
	public SourceDataSummary setSourceFinExcess(MigrationData md, SourceDataSummary sds) {

		List<FinExcessAmount> feaList = md.getFinExcessAmounts();

		for (int i = 0; i < feaList.size(); i++) {
			FinExcessAmount fea = feaList.get(i);
			sds.setFea_Amount(sds.getFea_Amount().add(fea.getAmount()));
			sds.setFea_UtilizedAmount(sds.getFea_UtilizedAmount().add(fea.getUtilisedAmt()));
			sds.setFea_BalanceAmount(sds.getFea_BalanceAmount().add(fea.getUtilisedAmt()));
		}

		return sds;
	}

	public SourceDataSummary validateSummary(MigrationData md, SourceDataSummary sds) {

		if (sds.getFm_TotalGrossPft().compareTo(sds.getFsd_ProfitSchd().add(sds.getFsd_CpzAmount())) != 0) {
			sds = setRemarks(sds, "/25", "w");
		}

		if (sds.getFm_TotalRepayAmt().compareTo(sds.getFsd_RepayAmount()) != 0) {
			sds = setRemarks(sds, "/26", "w");
		}

		if (sds.getFm_FinRepaymentAmount().compareTo(sds.getFsd_PrincipalSchd()) != 0) {
			sds = setRemarks(sds, "/27", "w");
		}

		if (sds.getFm_FirstRepay().compareTo(sds.getFsd_FirstRepay()) != 0) {
			sds = setRemarks(sds, "/28", "w");
		}

		if (sds.getFm_LastRepay().compareTo(sds.getFsd_LastRepay()) != 0) {
			sds = setRemarks(sds, "/29", "w");
		}

		return sds;
	}

	public SourceDataSummary setRemarks(SourceDataSummary sds, String remarks, String remarkCode) {

		if (StringUtils.equals(remarkCode, "e")) {
			if (!StringUtils.contains(sds.getErrors(), remarks)) {
				sds.setErrors(sds.getErrors().concat(remarks));
			}
		} else if (StringUtils.equals(remarkCode, "w")) {
			if (!StringUtils.contains(sds.getWarnings(), remarks)) {
				sds.setWarnings(sds.getWarnings().concat(remarks));
			}
		} else {
			if (!StringUtils.contains(sds.getInformation(), remarks)) {
				sds.setInformation(sds.getInformation().concat(remarks));
			}
		}

		return sds;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public ReceiptAllocationDetailDAO getReceiptAllocationDetailDAO() {
		return receiptAllocationDetailDAO;
	}

	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public PaymentHeaderDAO getPaymentHeaderDAO() {
		return paymentHeaderDAO;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public PaymentDetailDAO getPaymentDetailDAO() {
		return paymentDetailDAO;
	}

	public void setPaymentDetailDAO(PaymentDetailDAO paymentDetailDAO) {
		this.paymentDetailDAO = paymentDetailDAO;
	}

	public PaymentInstructionDAO getPaymentInstructionDAO() {
		return paymentInstructionDAO;
	}

	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinPlanEmiHolidayDAO getFinPlanEmiHolidayDAO() {
		return finPlanEmiHolidayDAO;
	}

	public void setFinPlanEmiHolidayDAO(FinPlanEmiHolidayDAO finPlanEmiHolidayDAO) {
		this.finPlanEmiHolidayDAO = finPlanEmiHolidayDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public GuarantorDetailDAO getGuarantorDetailDAO() {
		return guarantorDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public SourceDataSummaryDAO getSourceDataSummaryDAO() {
		return sourceDataSummaryDAO;
	}

	public void setSourceDataSummaryDAO(SourceDataSummaryDAO sourceDataSummaryDAO) {
		this.sourceDataSummaryDAO = sourceDataSummaryDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public BaseRateCodeDAO getBaseRateCodeDAO() {
		return baseRateCodeDAO;
	}

	public void setBaseRateCodeDAO(BaseRateCodeDAO baseRateCodeDAO) {
		this.baseRateCodeDAO = baseRateCodeDAO;
	}

	public SplRateCodeDAO getSplRateCodeDAO() {
		return splRateCodeDAO;
	}

	public void setSplRateCodeDAO(SplRateCodeDAO splRateCodeDAO) {
		this.splRateCodeDAO = splRateCodeDAO;
	}

	public FeeTypeDAO getFeeTypeDAO() {
		return feeTypeDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public BasicLoanReconDAO getBasicLoanReconDAO() {
		return basicLoanReconDAO;
	}

	public void setBasicLoanReconDAO(BasicLoanReconDAO basicLoanReconDAO) {
		this.basicLoanReconDAO = basicLoanReconDAO;
	}

}
