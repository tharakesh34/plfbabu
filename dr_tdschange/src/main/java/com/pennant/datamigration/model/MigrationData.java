package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Assignment;
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
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class MigrationData implements Serializable {
	private static final long serialVersionUID = 1183720618731771888L;
	private FinanceType finType;
	private FinanceMain financeMain;
	private List<FinanceDisbursement> finDisbursements;
	private List<FinAdvancePayments> finAdvancePayments;
	private List<FinanceScheduleDetail> oldFinScheduleDetails;
	private List<FinanceScheduleDetail> finScheduleDetails;
	private List<FinServiceInstruction> finServiceInstructions;
	private List<FinReceiptHeader> finReceiptHeaders;
	private List<FinReceiptDetail> finReceiptDetails;
	private List<ReceiptAllocationDetail> receiptAllocationDetails;
	private List<FinRepayHeader> finRepayHeaders;
	private List<RepayScheduleDetail> repayScheduleDetails;
	private List<PresentmentHeader> presentmentHeaders;
	private List<PresentmentDetail> presentmentDetails;
	private List<ManualAdvise> manualAdvises;
	private List<ManualAdviseMovements> manualAdviseMovements;
	private List<PaymentHeader> paymentHeaders;
	private List<PaymentDetail> paymentDetails;
	private List<PaymentInstruction> paymentInstructions;
	private List<FinODDetails> finODDetails;
	private FinODPenaltyRate penaltyrate;
	private Provision provision;
	private List<FinFeeDetail> finFeeDetails;
	private List<FinFeeScheduleDetail> finFeeScheduleDetails;
	private List<FinPlanEmiHoliday> finPlanEMIHolidays;
	private List<Mandate> mandates;
	private List<FinExcessAmount> finExcessAmounts;
	private List<FinExcessMovement> finExcessMovements;
	private List<JointAccountDetail> jointAccountDeatils;
	private List<GuarantorDetail> guarantorDetails;
	private List<RepayInstruction> repayInstructions;
	private List<FinanceRepayments> repayDetails;
	private List<ReturnDataSet> postEntries;
	private BasicLoanRecon basicLoanRecon;
	private FinanceProfitDetail finProfitDetail;
	private Assignment assignment;
	private DREMIHoliday drEH;
	private BigDecimal rpyPri;
	private BigDecimal rpyInt;
	private BigDecimal rpyODC;
	private BigDecimal rpyOther;
	private BigDecimal rpyExcessAmount;
	private int prvPaidIndex;
	private BigDecimal intBal;
	private BigDecimal priBal;
	private BigDecimal intPaidNow;
	private BigDecimal priPaidNow;
	private int repaySchID;
	private BigDecimal rpyEMI;
	private BigDecimal rpyEMIBal;
	private BigDecimal rpyEMIPaidNow;
	private String roundAdjMth;
	private DRCorrections drCorrections;
	private CutOffDateSchedule cutOffDateSchedule;
	private DRTDSChange drTDS;

	public MigrationData() {
		this.finDisbursements = new ArrayList<FinanceDisbursement>(1);
		this.finAdvancePayments = new ArrayList<FinAdvancePayments>(1);
		this.oldFinScheduleDetails = new ArrayList<FinanceScheduleDetail>(1);
		this.finScheduleDetails = new ArrayList<FinanceScheduleDetail>(1);
		this.finReceiptHeaders = new ArrayList<FinReceiptHeader>(1);
		this.finReceiptDetails = new ArrayList<FinReceiptDetail>(1);
		this.receiptAllocationDetails = new ArrayList<ReceiptAllocationDetail>(1);
		this.finRepayHeaders = new ArrayList<FinRepayHeader>(1);
		this.repayScheduleDetails = new ArrayList<RepayScheduleDetail>(1);
		this.presentmentDetails = new ArrayList<PresentmentDetail>(1);
		this.manualAdvises = new ArrayList<ManualAdvise>(1);
		this.manualAdviseMovements = new ArrayList<ManualAdviseMovements>(1);
		this.finFeeDetails = new ArrayList<FinFeeDetail>(1);
		this.finExcessAmounts = new ArrayList<FinExcessAmount>(1);
		this.finExcessMovements = new ArrayList<FinExcessMovement>(1);
		this.repayInstructions = new ArrayList<RepayInstruction>(1);
		this.repayDetails = new ArrayList<FinanceRepayments>(1);
		this.postEntries = new ArrayList<ReturnDataSet>(1);
		this.basicLoanRecon = new BasicLoanRecon();
		this.finProfitDetail = new FinanceProfitDetail();
		this.rpyPri = BigDecimal.ZERO;
		this.rpyInt = BigDecimal.ZERO;
		this.rpyODC = BigDecimal.ZERO;
		this.rpyOther = BigDecimal.ZERO;
		this.rpyExcessAmount = BigDecimal.ZERO;
		this.prvPaidIndex = 0;
		this.intBal = BigDecimal.ZERO;
		this.priBal = BigDecimal.ZERO;
		this.intPaidNow = BigDecimal.ZERO;
		this.priPaidNow = BigDecimal.ZERO;
		this.repaySchID = 0;
		this.rpyEMI = BigDecimal.ZERO;
		this.rpyEMIBal = BigDecimal.ZERO;
		this.rpyEMIPaidNow = BigDecimal.ZERO;
		this.roundAdjMth = "";
		this.drCorrections = new DRCorrections();
		this.cutOffDateSchedule = new CutOffDateSchedule();
		this.setDrEH(new DREMIHoliday());
		this.setDrTDS(new DRTDSChange());
	}

	public BigDecimal getIntBal() {
		return this.intBal;
	}

	public void setIntBal(final BigDecimal intBal) {
		this.intBal = intBal;
	}

	public BigDecimal getPriBal() {
		return this.priBal;
	}

	public void setPriBal(final BigDecimal priBal) {
		this.priBal = priBal;
	}

	public BigDecimal getIntPaidNow() {
		return this.intPaidNow;
	}

	public void setIntPaidNow(final BigDecimal intPaidNow) {
		this.intPaidNow = intPaidNow;
	}

	public BigDecimal getPriPaidNow() {
		return this.priPaidNow;
	}

	public void setPriPaidNow(final BigDecimal priPaidNow) {
		this.priPaidNow = priPaidNow;
	}

	public FinanceType getFinType() {
		return this.finType;
	}

	public void setFinType(final FinanceType finType) {
		this.finType = finType;
	}

	public FinanceMain getFinanceMain() {
		return this.financeMain;
	}

	public void setFinanceMain(final FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public List<FinanceDisbursement> getFinDisbursements() {
		return this.finDisbursements;
	}

	public void setFinDisbursements(final List<FinanceDisbursement> finDisbursements) {
		this.finDisbursements = finDisbursements;
	}

	public List<FinAdvancePayments> getFinAdvancePayments() {
		return this.finAdvancePayments;
	}

	public void setFinAdvancePayments(final List<FinAdvancePayments> finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public List<FinanceScheduleDetail> getFinScheduleDetails() {
		return this.finScheduleDetails;
	}

	public void setFinScheduleDetails(final List<FinanceScheduleDetail> finScheduleDetails) {
		this.finScheduleDetails = finScheduleDetails;
	}

	public List<FinanceScheduleDetail> getOldFinScheduleDetails() {
		return oldFinScheduleDetails;
	}

	public void setOldFinScheduleDetails(List<FinanceScheduleDetail> oldFinScheduleDetails) {
		this.oldFinScheduleDetails = oldFinScheduleDetails;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return this.finServiceInstructions;
	}

	public void setFinServiceInstructions(final List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public List<FinReceiptHeader> getFinReceiptHeaders() {
		return this.finReceiptHeaders;
	}

	public void setFinReceiptHeaders(final List<FinReceiptHeader> finReceiptHeaders) {
		this.finReceiptHeaders = finReceiptHeaders;
	}

	public List<FinReceiptDetail> getFinReceiptDetails() {
		return this.finReceiptDetails;
	}

	public void setFinReceiptDetails(final List<FinReceiptDetail> finReceiptDetails) {
		this.finReceiptDetails = finReceiptDetails;
	}

	public List<ReceiptAllocationDetail> getReceiptAllocationDetails() {
		return this.receiptAllocationDetails;
	}

	public void setReceiptAllocationDetails(final List<ReceiptAllocationDetail> receiptAllocationDetails) {
		this.receiptAllocationDetails = receiptAllocationDetails;
	}

	public List<FinRepayHeader> getFinRepayHeaders() {
		return this.finRepayHeaders;
	}

	public void setFinRepayHeaders(final List<FinRepayHeader> finRepayHeaders) {
		this.finRepayHeaders = finRepayHeaders;
	}

	public List<RepayScheduleDetail> getRepayScheduleDetails() {
		return this.repayScheduleDetails;
	}

	public void setRepayScheduleDetails(final List<RepayScheduleDetail> repayScheduleDetails) {
		this.repayScheduleDetails = repayScheduleDetails;
	}

	public List<PresentmentHeader> getPresentmentHeaders() {
		return this.presentmentHeaders;
	}

	public void setPresentmentHeaders(final List<PresentmentHeader> presentmentHeaders) {
		this.presentmentHeaders = presentmentHeaders;
	}

	public List<PresentmentDetail> getPresentmentDetails() {
		return this.presentmentDetails;
	}

	public void setPresentmentDetails(final List<PresentmentDetail> presentmentDetails) {
		this.presentmentDetails = presentmentDetails;
	}

	public List<ManualAdvise> getManualAdvises() {
		return this.manualAdvises;
	}

	public void setManualAdvises(final List<ManualAdvise> manualAdvises) {
		this.manualAdvises = manualAdvises;
	}

	public List<ManualAdviseMovements> getManualAdviseMovements() {
		return this.manualAdviseMovements;
	}

	public void setManualAdviseMovements(final List<ManualAdviseMovements> manualAdviseMovements) {
		this.manualAdviseMovements = manualAdviseMovements;
	}

	public List<PaymentHeader> getPaymentHeaders() {
		return this.paymentHeaders;
	}

	public void setPaymentHeaders(final List<PaymentHeader> paymentHeaders) {
		this.paymentHeaders = paymentHeaders;
	}

	public List<PaymentDetail> getPaymentDetails() {
		return this.paymentDetails;
	}

	public void setPaymentDetails(final List<PaymentDetail> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public List<PaymentInstruction> getPaymentInstructions() {
		return this.paymentInstructions;
	}

	public void setPaymentInstructions(final List<PaymentInstruction> paymentInstructions) {
		this.paymentInstructions = paymentInstructions;
	}

	public List<FinODDetails> getFinODDetails() {
		return this.finODDetails;
	}

	public void setFinODDetails(final List<FinODDetails> finODDetails) {
		this.finODDetails = finODDetails;
	}

	public FinODPenaltyRate getPenaltyrate() {
		return this.penaltyrate;
	}

	public void setPenaltyrate(final FinODPenaltyRate penaltyrate) {
		this.penaltyrate = penaltyrate;
	}

	public Provision getProvision() {
		return this.provision;
	}

	public void setProvision(final Provision provision) {
		this.provision = provision;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return this.finFeeDetails;
	}

	public void setFinFeeDetails(final List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

	public List<FinFeeScheduleDetail> getFinFeeScheduleDetails() {
		return this.finFeeScheduleDetails;
	}

	public void setFinFeeScheduleDetails(final List<FinFeeScheduleDetail> finFeeScheduleDetails) {
		this.finFeeScheduleDetails = finFeeScheduleDetails;
	}

	public List<FinPlanEmiHoliday> getFinPlanEMIHolidays() {
		return this.finPlanEMIHolidays;
	}

	public void setFinPlanEMIHolidays(final List<FinPlanEmiHoliday> finPlanEMIHolidays) {
		this.finPlanEMIHolidays = finPlanEMIHolidays;
	}

	public List<Mandate> getMandates() {
		return this.mandates;
	}

	public void setMandates(final List<Mandate> mandates) {
		this.mandates = mandates;
	}

	public List<FinExcessAmount> getFinExcessAmounts() {
		return this.finExcessAmounts;
	}

	public void setFinExcessAmounts(final List<FinExcessAmount> finExcessAmounts) {
		this.finExcessAmounts = finExcessAmounts;
	}

	public List<JointAccountDetail> getJointAccountDeatils() {
		return this.jointAccountDeatils;
	}

	public void setJointAccountDeatils(final List<JointAccountDetail> jointAccountDeatils) {
		this.jointAccountDeatils = jointAccountDeatils;
	}

	public List<GuarantorDetail> getGuarantorDetails() {
		return this.guarantorDetails;
	}

	public void setGuarantorDetails(final List<GuarantorDetail> guarantorDetails) {
		this.guarantorDetails = guarantorDetails;
	}

	public static long getSerialversionuid() {
		return 1183720618731771888L;
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
		return this.repayInstructions;
	}

	public void setRepayInstructions(final List<RepayInstruction> repayInstructions) {
		this.repayInstructions = repayInstructions;
	}

	public List<FinanceRepayments> getRepayDetails() {
		return this.repayDetails;
	}

	public void setRepayDetails(final List<FinanceRepayments> repayDetails) {
		this.repayDetails = repayDetails;
	}

	public BasicLoanRecon getBasicLoanRecon() {
		return this.basicLoanRecon;
	}

	public void setBasicLoanRecon(final BasicLoanRecon basicLoanRecon) {
		this.basicLoanRecon = basicLoanRecon;
	}

	public List<FinExcessMovement> getFinExcessMovements() {
		return this.finExcessMovements;
	}

	public void setFinExcessMovements(final List<FinExcessMovement> finExcessMovements) {
		this.finExcessMovements = finExcessMovements;
	}

	public BigDecimal getRpyPri() {
		return this.rpyPri;
	}

	public void setRpyPri(final BigDecimal rpyPri) {
		this.rpyPri = rpyPri;
	}

	public BigDecimal getRpyInt() {
		return this.rpyInt;
	}

	public void setRpyInt(final BigDecimal rpyInt) {
		this.rpyInt = rpyInt;
	}

	public BigDecimal getRpyODC() {
		return this.rpyODC;
	}

	public void setRpyODC(final BigDecimal rpyODC) {
		this.rpyODC = rpyODC;
	}

	public BigDecimal getRpyExcessAmount() {
		return this.rpyExcessAmount;
	}

	public void setRpyExcessAmount(final BigDecimal rpyExcessAmount) {
		this.rpyExcessAmount = rpyExcessAmount;
	}

	public int getPrvPaidIndex() {
		return this.prvPaidIndex;
	}

	public void setPrvPaidIndex(final int prvPaidIndex) {
		this.prvPaidIndex = prvPaidIndex;
	}

	public BigDecimal getRpyOther() {
		return this.rpyOther;
	}

	public void setRpyOther(final BigDecimal rpyOther) {
		this.rpyOther = rpyOther;
	}

	public int getRepaySchID() {
		return this.repaySchID;
	}

	public void setRepaySchID(final int repaySchID) {
		this.repaySchID = repaySchID;
	}

	public FinanceProfitDetail getFinProfitDetails() {
		return this.finProfitDetail;
	}

	public void setFinProfitDetails(FinanceProfitDetail finProfitDetails) {
		this.finProfitDetail = finProfitDetails;
	}

	public BigDecimal getRpyEMI() {
		return this.rpyEMI;
	}

	public void setRpyEMI(final BigDecimal rpyEMI) {
		this.rpyEMI = rpyEMI;
	}

	public BigDecimal getRpyEMIBal() {
		return this.rpyEMIBal;
	}

	public void setRpyEMIBal(final BigDecimal rpyEMIBal) {
		this.rpyEMIBal = rpyEMIBal;
	}

	public BigDecimal getRpyEMIPaidNow() {
		return this.rpyEMIPaidNow;
	}

	public void setRpyEMIPaidNow(final BigDecimal rpyEMIPaidNow) {
		this.rpyEMIPaidNow = rpyEMIPaidNow;
	}

	public List<ReturnDataSet> getPostEntries() {
		return this.postEntries;
	}

	public void setPostEntries(final List<ReturnDataSet> postEntries) {
		this.postEntries = postEntries;
	}

	public String getRoundAdjMth() {
		return this.roundAdjMth;
	}

	public void setRoundAdjMth(final String roundAdjMth) {
		this.roundAdjMth = roundAdjMth;
	}

	public DRCorrections getDrCorrections() {
		return this.drCorrections;
	}

	public void setDrCorrections(final DRCorrections drCorrections) {
		this.drCorrections = drCorrections;
	}

	public CutOffDateSchedule getCutOffDateSchedule() {
		return this.cutOffDateSchedule;
	}

	public void setCutOffDateSchedule(final CutOffDateSchedule cutOffDateSchedule) {
		this.cutOffDateSchedule = cutOffDateSchedule;
	}

	public DREMIHoliday getDrEH() {
		return drEH;
	}

	public void setDrEH(DREMIHoliday drReAge) {
		this.drEH = drReAge;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}
	
	public DRTDSChange getDrTDS() {
		return drTDS;
	}

	public void setDrTDS(DRTDSChange drTDS) {
		this.drTDS = drTDS;
	}
}