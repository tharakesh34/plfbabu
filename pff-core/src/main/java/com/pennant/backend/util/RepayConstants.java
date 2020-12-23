package com.pennant.backend.util;

public class RepayConstants {

	private RepayConstants() {
		super();
	}

	// C - Past Due Charge
	// F – Admin/Insurance/Other Fee
	// I – Profit
	// P - Principal
	// CS - Past Due Charge separately

	// Repayment Hierarchy methods
	// Chareges First
	public static final String REPAY_HIERARCHY_CFIP = "CFIP";
	public static final String REPAY_HIERARCHY_CFPI = "CFPI";
	public static final String REPAY_HIERARCHY_CIFP = "CIFP";
	public static final String REPAY_HIERARCHY_CIPF = "CIPF";
	public static final String REPAY_HIERARCHY_CPFI = "CPFI";
	public static final String REPAY_HIERARCHY_CPIF = "CPIF";
	public static final String REPAY_HIERARCHY_FCIP = "FCIP";
	public static final String REPAY_HIERARCHY_FCPI = "FCPI";
	public static final String REPAY_HIERARCHY_FICP = "FICP";
	public static final String REPAY_HIERARCHY_FIPC = "FIPC";
	public static final String REPAY_HIERARCHY_FPIC = "FPIC";
	public static final String REPAY_HIERARCHY_FPCI = "FPCI";
	public static final String REPAY_HIERARCHY_IPFC = "IPFC";
	public static final String REPAY_HIERARCHY_IPCF = "IPCF";
	public static final String REPAY_HIERARCHY_IFPC = "IFPC";
	public static final String REPAY_HIERARCHY_IFCP = "IFCP";
	public static final String REPAY_HIERARCHY_ICFP = "ICFP";
	public static final String REPAY_HIERARCHY_ICPF = "ICPF";
	public static final String REPAY_HIERARCHY_PICF = "PICF";
	public static final String REPAY_HIERARCHY_PIFC = "PIFC";
	public static final String REPAY_HIERARCHY_PFIC = "PFIC";
	public static final String REPAY_HIERARCHY_PFCI = "PFCI";
	public static final String REPAY_HIERARCHY_PCIF = "PCIF";
	public static final String REPAY_HIERARCHY_PCFI = "PCFI";
	public static final String REPAY_HIERARCHY_CSIFP = "CSIFP";
	public static final String REPAY_HIERARCHY_CSPFI = "CSPFI";
	public static final String REPAY_HIERARCHY_CSFPI = "CSFPI";
	public static final String REPAY_HIERARCHY_CSIPF = "CSIPF";
	public static final String REPAY_HIERARCHY_IFPCS = "IFPCS";
	public static final String REPAY_HIERARCHY_PFICS = "PFICS";
	public static final String REPAY_HIERARCHY_IPFCS = "IPFCS";

	public static final String REPAY_HIERARCHY_FPICS = "FPICS";
	public static final String REPAY_HIERARCHY_FIPCS = "FIPCS"; // FIXME not
																// there
																// in the PPT

	// Repayment Type Constants
	public static final char REPAY_PROFIT = 'I';
	public static final char REPAY_TDS = 'T';
	public static final char REPAY_PRINCIPAL = 'P';
	public static final char REPAY_PENALTY = 'C';
	public static final char REPAY_OTHERS = 'F';
	public static final char REPAY_LATEPAY_PROFIT = 'L';

	// Manual Receipts Usage
	public static final char REPAY_FEE = 'F';
	public static final char REPAY_INS = 'N';

	// Excess Amounts to in Receipts
	public static final String EXAMOUNTTYPE_EXCESS = "E";
	public static final String EXAMOUNTTYPE_EMIINADV = "A";
	public static final String EXAMOUNTTYPE_PAYABLE = "P";
	public static final String EXAMOUNTTYPE_ADVINT = "ADVINT";
	public static final String EXAMOUNTTYPE_ADVEMI = "ADVEMI";
	public static final String EXAMOUNTTYPE_CASHCLT = "CASHCLT";
	public static final String EXAMOUNTTYPE_DSF = "DSF";

	// Excess Adjustment to in Receipts
	public static final String EXCESSADJUSTTO_EXCESS = "E";
	public static final String EXCESSADJUSTTO_EMIINADV = "A";
	public static final String EXCESSADJUSTTO_PAYABLE = "P";
	public static final String EXCESSADJUSTTO_BOUNCE = "B";
	public static final String EXCESSADJUSTTO_SETTLEMENT = "S";
	public static final String EXCESSADJUSTTO_PARTPAY = "S";

	public static final String REQTYPE_INQUIRY = "Inquiry";
	public static final String REQTYPE_POST = "Post";

	// Receipt Modes
	// 1. Screen Display Modes
	public static final String RECEIPTMODE_CASH = "CASH";
	public static final String RECEIPTMODE_CHEQUE = "CHEQUE";
	public static final String RECEIPTMODE_DD = "DD";
	public static final String RECEIPTMODE_NEFT = "NEFT";
	public static final String RECEIPTMODE_RTGS = "RTGS";
	public static final String RECEIPTMODE_IMPS = "IMPS";
	public static final String RECEIPTMODE_PAYTM = "PAYTM";
	public static final String RECEIPTMODE_EXPERIA = "EXPERIA";
	public static final String RECEIPTMODE_PORTAL = "PORTAL";
	public static final String RECEIPTMODE_PAYU = "PAYU";
	public static final String RECEIPTMODE_EXCESS = "EXCESS";
	public static final String RECEIPTMODE_ESCROW = "ESCROW";
	public static final String RECEIPTMODE_REPLEDGE = "REPLEDGE";
	public static final String RECEIPTMODE_ONLINE = "ONLINE";
	public static final String RECEIPTMODE_BILLDESK = "BILLDESK";
	public static final String RECEIPTMODE_MOBILE = "MOBILE";
	public static final String RECEIPTMODE_DIGITAL = "DIGITAL";
	// 2. Back-end Process Receipt Modes
	public static final String RECEIPTMODE_EMIINADV = "EMIINADV";
	public static final String RECEIPTMODE_ADVINT = "ADVINT";
	public static final String RECEIPTMODE_ADVEMI = "ADVEMI";
	public static final String RECEIPTMODE_PAYABLE = "PAYABLE";
	public static final String RECEIPTMODE_PRESENTMENT = "PRESENT";
	public static final String RECEIPTMODE_CASHCLT = "CASHCLT";
	public static final String RECEIPTMODE_DSF = "DSF";

	public static final String ALLOCATION_PP = "PP";
	public static final String ALLOCATION_EMI = "EMI";
	public static final String ALLOCATION_FUT_PRI = "FUTPRI";
	public static final String ALLOCATION_FUT_PFT = "FUTPFT";
	public static final String ALLOCATION_FUT_NPFT = "FUTNPFT";
	public static final String ALLOCATION_FUT_TDS = "FUTTDS";

	public static final String ALLOCATION_KOEMI = "KOFFEMI";
	public static final String ALLOCATION_KOLPI = "KOFFLPI";

	// Allocation Methods
	public static final String ALLOCATIONTYPE_AUTO = "A";
	public static final String ALLOCATIONTYPE_MANUAL = "M";
	public static final String ALLOCATIONTYPE_PARK_IN_EXCESS = "PIE";

	public static final String ALLOCTYPE_AUTO = "Auto";
	public static final String ALLOCTYPE_MANUAL = "Manual";

	// Allocation Types
	public static final String ALLOCATION_PRI = "PRI";
	public static final String ALLOCATION_PFT = "PFT";
	public static final String ALLOCATION_NPFT = "NPFT";
	public static final String ALLOCATION_TDS = "TDS";
	public static final String ALLOCATION_LPFT = "LPFT";
	public static final String ALLOCATION_ODC = "ODC";
	public static final String ALLOCATION_FEE = "FEE";
	public static final String ALLOCATION_INS = "INS";
	public static final String ALLOCATION_MANADV = "MANADV";
	public static final String ALLOCATION_BOUNCE = "BOUNCE";

	// Receipt process Types
	public static final String RECEIPTTYPE_RECIPT = "R";
	public static final String RECEIPTTYPE_PAYABLE = "P";
	public static final String RECEIPTTYPE_ADJUST = "A";

	// Receipt Types
	public static final String RECEIPTTO_FINANCE = "F";
	public static final String RECEIPTTO_CUSTOMER = "C";
	public static final String RECEIPTTO_OTHER = "O";

	public static final String KNOCKOFF_TYPE_AUTO = "A";
	public static final String KNOCKOFF_TYPE_MANUAL = "M";

	// Receipt Payment Type
	public static final String PAYTYPE_CASH = "CASH";
	public static final String PAYTYPE_CHEQUE = "CHEQUE";
	public static final String PAYTYPE_DD = "DD";
	public static final String PAYTYPE_NEFT = "NEFT";
	public static final String PAYTYPE_RTGS = "RTGS";
	public static final String PAYTYPE_IMPS = "IMPS";
	public static final String PAYTYPE_EXCESS = "EXCESS";
	public static final String PAYTYPE_EMIINADV = "EMIINADV";
	public static final String PAYTYPE_PAYABLE = "PAYABLE";
	public static final String PAYTYPE_PRESENTMENT = "PRESENT";
	public static final String PAYTYPE_WRITEOFF = "WRITEOFF";

	// Presentment Include Exclude
	public static final String PRESENTMENT_INCLUDE = "INCLUDE";
	public static final String PRESENTMENT_MANUALEXCLUDE = "MANUALEXCLUDE";
	public static final String PRESENTMENT_AUTOEXCLUDE = "AUTOEXCLUDE";

	// Presentment Exclude Reasons
	public static final int PEXC_EMIINCLUDE = 0;
	public static final int PEXC_EMIINADVANCE = 1;
	public static final int PEXC_EMIHOLD = 2;
	public static final int PEXC_MANDATE_HOLD = 3;
	public static final int PEXC_MANDATE_NOTAPPROV = 4;
	public static final int PEXC_MANDATE_EXPIRY = 5;
	public static final int PEXC_MANUAL_EXCLUDE = 6;
	public static final int PEXC_MANDATE_REJECTED = 7;
	public static final int CHEQUESTATUS_PRESENT = 8;
	public static final int CHEQUESTATUS_BOUNCE = 9;
	public static final int CHEQUESTATUS_REALISE = 10;
	public static final int CHEQUESTATUS_REALISED = 11;
	public static final int PEXC_ADVINT = 12;
	public static final int PEXC_ADVEMI = 13;

	// Presentment Status Reasons
	public static final int PEXC_EXTRACT = 1;
	public static final int PEXC_BATCH_CREATED = 2;
	public static final int PEXC_AWAITING_CONF = 3;
	public static final int PEXC_SEND_PRESENTMENT = 4;
	public static final int PEXC_RECEIVED = 5;

	// Presentment Detail Status Reasons
	public static final String PEXC_IMPORT = "I";
	public static final String PEXC_SUCCESS = "S";
	public static final String PEXC_FAILURE = "F";
	public static final String PEXC_APPROV = "A";
	public static final String PEXC_BOUNCE = "B";
	public static final String PEXC_PAID = "PAID";
	public static final String PEXC_FAILED = "FAILED";

	// Receipt Payment Statuses
	public static final String PAYSTATUS_APPROVED = "A";
	public static final String PAYSTATUS_FEES = "F";
	public static final String PAYSTATUS_REALIZED = "R";
	public static final String PAYSTATUS_BOUNCE = "B";
	public static final String PAYSTATUS_CANCEL = "C";
	public static final String PAYSTATUS_DEPOSITED = "D";
	public static final String PAYSTATUS_INITIATED = "I";

	// Receipt Payment Statuses
	public static final String MODULETYPE_BOUNCE = "B";
	public static final String MODULETYPE_CANCEL = "C";
	public static final String MODULETYPE_FEE = "F";

	// Payment Statuses
	public static final String PAYMENT_INTIATED = "I";
	public static final String PAYMENT_APPROVE = "A";
	public static final String PAYMENT_DOWNLOAD = "D";
	public static final String PAYMENT_SUCCESS = "S";
	public static final String PAYMENT_FAILURE = "F";
	public static final String PAYMENT_PAID = "PAID";
	public static final String PAYMENT_FAILED = "FAILED";

	// presentmentResponse statuscodes
	public static final String PRES_SUCCESS = "PR000";
	public static final String PRES_PENDING = "PR001";
	public static final String PRES_DUPLICATE = "PR002";
	public static final String PRES_ERROR = "PR004";
	public static final String PRES_LOANCLOSED = "PR005";
	public static final String PRES_HOLD_DAYS = "PR006";
	public static final String PRES_FAILED = "PR007";

	// Release Type Constants in Gold Release
	public static final String RELEASETYPE_THIRD_PARTY = "T";
	public static final String RELEASETYPE_SELF = "S";

	//Received from
	public static final String RECEIVED_CUSTOMER = "Customer";
	public static final String RECEIVED_GOVT = "GOVERMENT";

	// Waiver types
	public static final String INTEREST_WAIVER = "I"; // Profit Waiver
	public static final String PRINCIPAL_WAIVER = "P"; // Principle Waiver

}
