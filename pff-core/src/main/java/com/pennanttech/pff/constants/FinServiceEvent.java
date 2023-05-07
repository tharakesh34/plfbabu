package com.pennanttech.pff.constants;

/**
 * This stores all finance service events required for running the application
 */
public class FinServiceEvent {
	private FinServiceEvent() {
		super();
	}

	public static final String ORG = "Origination";
	public static final String PREAPPROVAL = "PreApproval";
	public static final String RATECHG = "AddRateChange";
	public static final String CHGRPY = "ChangeRepay";
	public static final String ADDDISB = "AddDisbursement";
	public static final String ADDFLEXIDISB = "AddFlexiDisbursement";
	public static final String RLSDISB = "RlsHoldDisbursement";
	public static final String POSTPONEMENT = "Postponement";
	public static final String UNPLANEMIH = "UnPlannedEMIH";
	public static final String ADDTERM = "AddTerms";
	public static final String RMVTERM = "RmvTerms";
	public static final String RECALCULATE = "Recalculate";
	public static final String SUBSCHD = "SubSchedule";
	public static final String CHGPFT = "ChangeProfit";
	public static final String CHGFRQ = "ChangeFrequency";
	public static final String RESCHD = "ReSchedule";
	public static final String CHGGRCEND = "ChangeGestation";
	public static final String INSCHANGE = "InsuranceChange";
	public static final String RECEIPT = "Receipt";
	public static final String COVENANT = "Covenants";
	public static final String COLLATERAL = "Collaterals";
	public static final String RECEIPTFORECLOSURE = "ReceiptForeClosure"; // not using
	public static final String SCHDRPY = "SchdlRepayment";
	public static final String EARLYRPY = "EarlyPayment";
	public static final String RECEIPTKNOCKOFF = "ReceiptKnockOff"; // not using
	public static final String RECEIPTKNOCKOFF_CAN = "ReceiptKnockOffCan"; // not using
	public static final String EARLYSETTLE = "EarlySettlement";
	public static final String WRITEOFF = "WriteOff";
	public static final String WRITEOFFPAY = "WriteOffPay";
	public static final String PROVISION = "Provision";
	public static final String SUSPHEAD = "Suspense";
	public static final String CANCELFIN = "CancelFinance";
	public static final String DUEALERTS = "DueAlerts";
	public static final String BASICMAINTAIN = "MaintainBasicDetail";
	public static final String RPYBASICMAINTAIN = "MaintainRepayDetail";
	public static final String CANCELRPY = "CancelRepay";
	public static final String LIABILITYREQ = "LiabilityRequest";
	public static final String NOCISSUANCE = "NOCIssuance";
	public static final String TIMELYCLOSURE = "TimelyClosure";
	public static final String INSCLAIM = "InsuranceClaim";
	public static final String EARLYSTLENQ = "EarlySettlementEnq";
	public static final String FINFLAGS = "FinanceFlag";
	public static final String REINSTATE = "ReInstate";
	public static final String REAGING = "ReAging";
	public static final String CANCELDISB = "CancelDisbursement";
	public static final String OVERDRAFTSCHD = "OverdraftSchedule";
	public static final String PLANNEDEMI = "PlannedEMI";
	public static final String HOLDEMI = "HoldEMI";
	public static final String FEEPAYMENT = "FeePayment";
	public static final String CHGSCHDMETHOD = "ChangeSchdlMethod";
	public static final String GOLDLOANCANCEL = "GoldLoanCancel"; // not using
	public static final String STORAGE = "StorageDetails"; // not using
	public static final String NOMINEE = "NomineeDetails"; // not using
	public static final String REPLEDGE = "Repledge"; // not using
	public static final String HOLDDISB = "HoldDisbursement";
	public static final String STARTPERIODHDAY = "StartPeriodHoliday"; // not using
	public static final String LOANDOWNSIZING = "LoanDownsizing";
	public static final String LINKDELINK = "LinkDelink";
	public static final String PART_CANCELLATION = "PartCancellation";
	// This value is Hard coded in View "CovenantsMaintenance_View"
	public static final String COVENANTS = "Covenants"; // not using
	// This value is Hard coded in View "CovenantsMaintenance_View"
	public static final String FINOPTION = "FinOptions";
	public static final String RECADV = "RecAdvise"; // not using
	public static final String PAYADV = "PayAdvise"; // not using
	public static final String RESTRUCTURE = "Restructure";
	public static final String DISBINST = "DisbInstruction";
	public static final String GSTDETAILS = "GSTDetails";
	public static final String CHEQUEDETAILS = "ChequeDetails";
	public static final String MANUALADVISE = "ManualAdvise";
	public static final String FEEPOSTING = "FeePosting";
	public static final String JVPOSTING = "JVPosting";
	public static final String PAYMENTINST = "PaymentInst";
	public static final String CHANGETDS = "ChangeTDS";
	public static final String FEEWAIVERS = "FeeWaivers";
	public static final String PUTCALL = "Putcall";
	public static final String COLLATERAL_LTV_BREACHS = "CollateralLTVBreachs"; // not using
	public static final String REALIZATION = "Realization";
	public static final String UPFRONT_FEE = "UpfrontFee";
	public static final String UPFRONT_FEE_CAN = "UpfrontFeeCancel";
	public static final String EXTENDEDFIELDS_MAINTAIN = "MaintExtendedFields";
	public static final String PRINH = "PrincipleHoliday";
	public static final String FEEREFUNDINST = "FeeRefundInst";
	public static final String CROSS_LOAN_KNOCKOFF = "CROSSLOANKNOCKOFF";

	public static final String NOCLTR = "NOCLTR";
	public static final String CANCLLTR = "CANCLLTR";
	public static final String CLOSELTR = "CLOSELTR";

}