package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.payment.PaymentInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class MigrationData implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private FinanceType finType;
	private FinanceMain financeMain;
	private List<FinanceDisbursement> finDisbursements = new ArrayList<FinanceDisbursement>(1);
	private List<FinAdvancePayments> finAdvancePayments = new ArrayList<FinAdvancePayments>(1);
	private List<FinanceScheduleDetail> finScheduleDetails = new ArrayList<FinanceScheduleDetail>(1);
	private List<FinServiceInstruction> finServiceInstructions;
	private List<FinReceiptHeader> finReceiptHeaders = new ArrayList<FinReceiptHeader>(1);
	private List<FinReceiptDetail> finReceiptDetails = new ArrayList<FinReceiptDetail>(1);
	private List<ReceiptAllocationDetail> receiptAllocationDetails = new ArrayList<ReceiptAllocationDetail>(1);
	private List<FinRepayHeader> finRepayHeaders = new ArrayList<FinRepayHeader>(1);
	private List<RepayScheduleDetail> repayScheduleDetails = new ArrayList<RepayScheduleDetail>(1);
	private List<PresentmentHeader> presentmentHeaders;
	private List<PresentmentDetail> presentmentDetails = new ArrayList<PresentmentDetail>(1);;
	private List<ManualAdvise> manualAdvises = new ArrayList<ManualAdvise>(1);
	private List<ManualAdviseMovements> manualAdviseMovements = new ArrayList<ManualAdviseMovements>(1);;
	private List<PaymentHeader> paymentHeaders;
	private List<PaymentDetail> paymentDetails;
	private List<PaymentInstruction> paymentInstructions;
	private List<FinODDetails> finODDetails;
	private FinODPenaltyRate penaltyrate;
	private Provision provision;
	private List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>(1);
	private List<FinFeeScheduleDetail> finFeeScheduleDetails;
	private List<FinPlanEmiHoliday> finPlanEMIHolidays;
	private List<Mandate> mandates;
	private List<FinExcessAmount> finExcessAmounts = new ArrayList<FinExcessAmount>(1);
	private List<FinExcessMovement> finExcessMovements = new ArrayList<FinExcessMovement>(1);
	private List<JointAccountDetail> jointAccountDeatils;
	private List<GuarantorDetail> guarantorDetails;

	private List<RepayInstruction> repayInstructions = new ArrayList<RepayInstruction>(1);
	private List<FinanceRepayments> repayDetails = new ArrayList<FinanceRepayments>(1);
	private List<ReturnDataSet> postEntries = new ArrayList<ReturnDataSet>(1);

	private BasicLoanRecon basicLoanRecon = new BasicLoanRecon();
	private SourceReport sourceReport = new SourceReport();
	private FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();

	private BigDecimal rpyPri = BigDecimal.ZERO;
	private BigDecimal rpyInt = BigDecimal.ZERO;
	private BigDecimal rpyODC = BigDecimal.ZERO;
	private BigDecimal rpyOther = BigDecimal.ZERO;
	private BigDecimal rpyExcessAmount = BigDecimal.ZERO;
	private int prvPaidIndex = 0;
	private BigDecimal intBal = BigDecimal.ZERO;
	private BigDecimal priBal = BigDecimal.ZERO;
	private BigDecimal intPaidNow = BigDecimal.ZERO;
	private BigDecimal priPaidNow = BigDecimal.ZERO;
	private int repaySchID = 0;
	private boolean workOnEMI = false;
	private BigDecimal rpyEMI = BigDecimal.ZERO;
	private BigDecimal rpyEMIBal = BigDecimal.ZERO;
	private BigDecimal rpyEMIPaidNow = BigDecimal.ZERO;

	public BigDecimal getIntBal() {
		return intBal;
	}

	public void setIntBal(BigDecimal intBal) {
		this.intBal = intBal;
	}

	public BigDecimal getPriBal() {
		return priBal;
	}

	public void setPriBal(BigDecimal priBal) {
		this.priBal = priBal;
	}

	public BigDecimal getIntPaidNow() {
		return intPaidNow;
	}

	public void setIntPaidNow(BigDecimal intPaidNow) {
		this.intPaidNow = intPaidNow;
	}

	public BigDecimal getPriPaidNow() {
		return priPaidNow;
	}

	public void setPriPaidNow(BigDecimal priPaidNow) {
		this.priPaidNow = priPaidNow;
	}

	public FinanceType getFinType() {
		return finType;
	}

	public void setFinType(FinanceType finType) {
		this.finType = finType;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public List<FinanceDisbursement> getFinDisbursements() {
		return finDisbursements;
	}

	public void setFinDisbursements(List<FinanceDisbursement> finDisbursements) {
		this.finDisbursements = finDisbursements;
	}

	public List<FinAdvancePayments> getFinAdvancePayments() {
		return finAdvancePayments;
	}

	public void setFinAdvancePayments(List<FinAdvancePayments> finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public List<FinanceScheduleDetail> getFinScheduleDetails() {
		return finScheduleDetails;
	}

	public void setFinScheduleDetails(List<FinanceScheduleDetail> finScheduleDetails) {
		this.finScheduleDetails = finScheduleDetails;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return finServiceInstructions;
	}

	public void setFinServiceInstructions(List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public List<FinReceiptHeader> getFinReceiptHeaders() {
		return finReceiptHeaders;
	}

	public void setFinReceiptHeaders(List<FinReceiptHeader> finReceiptHeaders) {
		this.finReceiptHeaders = finReceiptHeaders;
	}

	public List<FinReceiptDetail> getFinReceiptDetails() {
		return finReceiptDetails;
	}

	public void setFinReceiptDetails(List<FinReceiptDetail> finReceiptDetails) {
		this.finReceiptDetails = finReceiptDetails;
	}

	public List<ReceiptAllocationDetail> getReceiptAllocationDetails() {
		return receiptAllocationDetails;
	}

	public void setReceiptAllocationDetails(List<ReceiptAllocationDetail> receiptAllocationDetails) {
		this.receiptAllocationDetails = receiptAllocationDetails;
	}

	public List<FinRepayHeader> getFinRepayHeaders() {
		return finRepayHeaders;
	}

	public void setFinRepayHeaders(List<FinRepayHeader> finRepayHeaders) {
		this.finRepayHeaders = finRepayHeaders;
	}

	public List<RepayScheduleDetail> getRepayScheduleDetails() {
		return repayScheduleDetails;
	}

	public void setRepayScheduleDetails(List<RepayScheduleDetail> repayScheduleDetails) {
		this.repayScheduleDetails = repayScheduleDetails;
	}

	public List<PresentmentHeader> getPresentmentHeaders() {
		return presentmentHeaders;
	}

	public void setPresentmentHeaders(List<PresentmentHeader> presentmentHeaders) {
		this.presentmentHeaders = presentmentHeaders;
	}

	public List<PresentmentDetail> getPresentmentDetails() {
		return presentmentDetails;
	}

	public void setPresentmentDetails(List<PresentmentDetail> presentmentDetails) {
		this.presentmentDetails = presentmentDetails;
	}

	public List<ManualAdvise> getManualAdvises() {
		return manualAdvises;
	}

	public void setManualAdvises(List<ManualAdvise> manualAdvises) {
		this.manualAdvises = manualAdvises;
	}

	public List<ManualAdviseMovements> getManualAdviseMovements() {
		return manualAdviseMovements;
	}

	public void setManualAdviseMovements(List<ManualAdviseMovements> manualAdviseMovements) {
		this.manualAdviseMovements = manualAdviseMovements;
	}

	public List<PaymentHeader> getPaymentHeaders() {
		return paymentHeaders;
	}

	public void setPaymentHeaders(List<PaymentHeader> paymentHeaders) {
		this.paymentHeaders = paymentHeaders;
	}

	public List<PaymentDetail> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(List<PaymentDetail> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public List<PaymentInstruction> getPaymentInstructions() {
		return paymentInstructions;
	}

	public void setPaymentInstructions(List<PaymentInstruction> paymentInstructions) {
		this.paymentInstructions = paymentInstructions;
	}

	public List<FinODDetails> getFinODDetails() {
		return finODDetails;
	}

	public void setFinODDetails(List<FinODDetails> finODDetails) {
		this.finODDetails = finODDetails;
	}

	public FinODPenaltyRate getPenaltyrate() {
		return penaltyrate;
	}

	public void setPenaltyrate(FinODPenaltyRate penaltyrate) {
		this.penaltyrate = penaltyrate;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

	public List<FinFeeScheduleDetail> getFinFeeScheduleDetails() {
		return finFeeScheduleDetails;
	}

	public void setFinFeeScheduleDetails(List<FinFeeScheduleDetail> finFeeScheduleDetails) {
		this.finFeeScheduleDetails = finFeeScheduleDetails;
	}

	public List<FinPlanEmiHoliday> getFinPlanEMIHolidays() {
		return finPlanEMIHolidays;
	}

	public void setFinPlanEMIHolidays(List<FinPlanEmiHoliday> finPlanEMIHolidays) {
		this.finPlanEMIHolidays = finPlanEMIHolidays;
	}

	public List<Mandate> getMandates() {
		return mandates;
	}

	public void setMandates(List<Mandate> mandates) {
		this.mandates = mandates;
	}

	public List<FinExcessAmount> getFinExcessAmounts() {
		return finExcessAmounts;
	}

	public void setFinExcessAmounts(List<FinExcessAmount> finExcessAmounts) {
		this.finExcessAmounts = finExcessAmounts;
	}

	public List<JointAccountDetail> getJointAccountDeatils() {
		return jointAccountDeatils;
	}

	public void setJointAccountDeatils(List<JointAccountDetail> jointAccountDeatils) {
		this.jointAccountDeatils = jointAccountDeatils;
	}

	public List<GuarantorDetail> getGuarantorDetails() {
		return guarantorDetails;
	}

	public void setGuarantorDetails(List<GuarantorDetail> guarantorDetails) {
		this.guarantorDetails = guarantorDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void destroy() {
		this.financeMain = null;
		this.finType = null;
		this.finDisbursements.clear();
		this.finAdvancePayments.clear();
		this.finScheduleDetails.clear();
		this.finServiceInstructions.clear();
		this.finReceiptHeaders.clear();
		this.finReceiptDetails.clear();
		this.receiptAllocationDetails.clear();
		this.finRepayHeaders.clear();
		this.repayScheduleDetails.clear();
		this.presentmentHeaders.clear();
		this.presentmentDetails.clear();
		this.manualAdvises.clear();
		this.manualAdviseMovements.clear();
		this.paymentHeaders.clear();
		this.paymentDetails.clear();
		this.paymentInstructions.clear();
		this.finODDetails.clear();
		this.penaltyrate = null;
		this.provision = null;
		this.finFeeDetails.clear();
		this.finFeeScheduleDetails.clear();
		this.finPlanEMIHolidays.clear();
		this.mandates.clear();
		this.finExcessAmounts.clear();
		this.jointAccountDeatils.clear();
		this.guarantorDetails.clear();
	}

	public List<RepayInstruction> getRepayInstructions() {
		return repayInstructions;
	}

	public void setRepayInstructions(List<RepayInstruction> repayInstructions) {
		this.repayInstructions = repayInstructions;
	}

	public List<FinanceRepayments> getRepayDetails() {
		return repayDetails;
	}

	public void setRepayDetails(List<FinanceRepayments> repayDetails) {
		this.repayDetails = repayDetails;
	}

	public BasicLoanRecon getBasicLoanRecon() {
		return basicLoanRecon;
	}

	public void setBasicLoanRecon(BasicLoanRecon basicLoanRecon) {
		this.basicLoanRecon = basicLoanRecon;
	}

	public List<FinExcessMovement> getFinExcessMovements() {
		return finExcessMovements;
	}

	public void setFinExcessMovements(List<FinExcessMovement> finExcessMovements) {
		this.finExcessMovements = finExcessMovements;
	}

	public BigDecimal getRpyPri() {
		return rpyPri;
	}

	public void setRpyPri(BigDecimal rpyPri) {
		this.rpyPri = rpyPri;
	}

	public BigDecimal getRpyInt() {
		return rpyInt;
	}

	public void setRpyInt(BigDecimal rpyInt) {
		this.rpyInt = rpyInt;
	}

	public BigDecimal getRpyODC() {
		return rpyODC;
	}

	public void setRpyODC(BigDecimal rpyODC) {
		this.rpyODC = rpyODC;
	}

	public BigDecimal getRpyExcessAmount() {
		return rpyExcessAmount;
	}

	public void setRpyExcessAmount(BigDecimal rpyExcessAmount) {
		this.rpyExcessAmount = rpyExcessAmount;
	}

	public int getPrvPaidIndex() {
		return prvPaidIndex;
	}

	public void setPrvPaidIndex(int prvPaidIndex) {
		this.prvPaidIndex = prvPaidIndex;
	}

	public BigDecimal getRpyOther() {
		return rpyOther;
	}

	public void setRpyOther(BigDecimal rpyOther) {
		this.rpyOther = rpyOther;
	}

	public int getRepaySchID() {
		return repaySchID;
	}

	public void setRepaySchID(int repaySchID) {
		this.repaySchID = repaySchID;
	}

	public FinanceProfitDetail getFinProfitDetails() {
		return finProfitDetails;
	}

	public void setFinProfitDetails(FinanceProfitDetail finProfitDetails) {
		this.finProfitDetails = finProfitDetails;
	}

	public BigDecimal getRpyEMI() {
		return rpyEMI;
	}

	public void setRpyEMI(BigDecimal rpyEMI) {
		this.rpyEMI = rpyEMI;
	}

	public BigDecimal getRpyEMIBal() {
		return rpyEMIBal;
	}

	public void setRpyEMIBal(BigDecimal rpyEMIBal) {
		this.rpyEMIBal = rpyEMIBal;
	}

	public BigDecimal getRpyEMIPaidNow() {
		return rpyEMIPaidNow;
	}

	public void setRpyEMIPaidNow(BigDecimal rpyEMIPaidNow) {
		this.rpyEMIPaidNow = rpyEMIPaidNow;
	}

	public SourceReport getSourceReport() {
		return sourceReport;
	}

	public void setSourceReport(SourceReport sourceReport) {
		this.sourceReport = sourceReport;
	}

	public List<ReturnDataSet> getPostEntries() {
		return postEntries;
	}

	public void setPostEntries(List<ReturnDataSet> postEntries) {
		this.postEntries = postEntries;
	}

	public boolean isWorkOnEMI() {
		return workOnEMI;
	}

	public void setWorkOnEMI(boolean workOnEMI) {
		this.workOnEMI = workOnEMI;
	}

}
