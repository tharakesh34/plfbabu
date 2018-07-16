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
	public static final String	REPAY_HIERARCHY_CFIP	= "CFIP";
	public static final String	REPAY_HIERARCHY_CFPI	= "CFPI";
	public static final String	REPAY_HIERARCHY_CIFP	= "CIFP";
	public static final String	REPAY_HIERARCHY_CIPF	= "CIPF";
	public static final String	REPAY_HIERARCHY_CPFI	= "CPFI";
	public static final String	REPAY_HIERARCHY_CPIF	= "CPIF";
	public static final String	REPAY_HIERARCHY_FCIP	= "FCIP";
	public static final String	REPAY_HIERARCHY_FCPI	= "FCPI";
	public static final String	REPAY_HIERARCHY_FICP	= "FICP";
	public static final String	REPAY_HIERARCHY_FIPC	= "FIPC";
	public static final String	REPAY_HIERARCHY_FPIC	= "FPIC";
	public static final String	REPAY_HIERARCHY_FPCI	= "FPCI";
	public static final String	REPAY_HIERARCHY_IPFC	= "IPFC";
	public static final String	REPAY_HIERARCHY_IPCF	= "IPCF";
	public static final String	REPAY_HIERARCHY_IFPC	= "IFPC";
	public static final String	REPAY_HIERARCHY_IFCP	= "IFCP";
	public static final String	REPAY_HIERARCHY_ICFP	= "ICFP";
	public static final String	REPAY_HIERARCHY_ICPF	= "ICPF";
	public static final String	REPAY_HIERARCHY_PICF	= "PICF";
	public static final String	REPAY_HIERARCHY_PIFC	= "PIFC";
	public static final String	REPAY_HIERARCHY_PFIC	= "PFIC";
	public static final String	REPAY_HIERARCHY_PFCI	= "PFCI";
	public static final String	REPAY_HIERARCHY_PCIF	= "PCIF";
	public static final String	REPAY_HIERARCHY_PCFI	= "PCFI";
	public static final String	REPAY_HIERARCHY_CSIFP	= "CSIFP";
	public static final String	REPAY_HIERARCHY_CSPFI	= "CSPFI";
	public static final String	REPAY_HIERARCHY_CSFPI	= "CSFPI";
	public static final String	REPAY_HIERARCHY_CSIPF	= "CSIPF";
	public static final String	REPAY_HIERARCHY_IFPCS	= "IFPCS";
	public static final String	REPAY_HIERARCHY_PFICS	= "PFICS";
	public static final String	REPAY_HIERARCHY_IPFCS	= "IPFCS";

	public static final String	REPAY_HIERARCHY_FPICS	= "FPICS";
	public static final String	REPAY_HIERARCHY_FIPCS	= "FIPCS";	// FIXME not
																	// there
																	// in the PPT

	// Repayment Type Constants
	public static final char	REPAY_PROFIT			= 'I';
	public static final char	REPAY_TDS				= 'T';
	public static final char	REPAY_PRINCIPAL			= 'P';
	public static final char	REPAY_PENALTY			= 'C';
	public static final char	REPAY_OTHERS			= 'F';
	public static final char	REPAY_LATEPAY_PROFIT	= 'L';
	
	// Manual Receipts Usage
	public static final char	REPAY_FEE				= 'F';
	public static final char	REPAY_INS				= 'N';
	
	// Excess Amounts to in Receipts
	public static final String	EXAMOUNTTYPE_EXCESS		= "E";
	public static final String	EXAMOUNTTYPE_EMIINADV	= "A";
	public static final String	EXAMOUNTTYPE_PAYABLE 	= "P";
	
	// Excess Adjustment to in Receipts
	public static final String	EXCESSADJUSTTO_EXCESS	= "E";
	public static final String	EXCESSADJUSTTO_EMIINADV	= "A";
	public static final String	EXCESSADJUSTTO_PAYABLE 	= "P";
	public static final String	EXCESSADJUSTTO_PARTPAY 	= "S";
	
	// Receipt Modes
	// 1. Screen Display Modes 
	public static final String	RECEIPTMODE_CASH		= "CASH";
	public static final String	RECEIPTMODE_CHEQUE		= "CHEQUE";
	public static final String	RECEIPTMODE_DD			= "DD";
	public static final String	RECEIPTMODE_NEFT		= "NEFT";
	public static final String	RECEIPTMODE_RTGS		= "RTGS";
	public static final String	RECEIPTMODE_IMPS		= "IMPS";
	public static final String	RECEIPTMODE_EXCESS		= "EXCESS";
	public static final String  RECEIPTMODE_ESCROW		= "ESCROW";
	// 2. Back-end Process Receipt Modes
	public static final String	RECEIPTMODE_EMIINADV	= "EMIINADV";
	public static final String	RECEIPTMODE_PAYABLE		= "PAYABLE";
	public static final String	RECEIPTMODE_PRESENTMENT	= "PRESENT";
	
	// Allocation Methods
	public static final String	ALLOCATIONTYPE_AUTO		= "A";
	public static final String	ALLOCATIONTYPE_MANUAL	= "M";

	// Allocation Types
	public static final String	ALLOCATION_PRI			= "PRI";
	public static final String	ALLOCATION_PFT			= "PFT";
	public static final String	ALLOCATION_NPFT			= "NPFT";
	public static final String	ALLOCATION_TDS			= "TDS";
	public static final String	ALLOCATION_LPFT			= "LPFT";
	public static final String	ALLOCATION_ODC			= "ODC";
	public static final String	ALLOCATION_FEE			= "FEE";
	public static final String	ALLOCATION_INS			= "INS";
	public static final String	ALLOCATION_MANADV		= "MANADV";
	public static final String	ALLOCATION_BOUNCE		= "BOUNCE";
	
	// Receipt Types
	public static final String	RECEIPTTYPE_RECIPT		= "R";
	public static final String	RECEIPTTYPE_PAYABLE	    = "P";
	
	// Receipt Types
	public static final String	RECEIPTTO_FINANCE		= "F";
	public static final String	RECEIPTTO_CUSTOMER		= "C";
	
	// Receipt Payment Type

	
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
	
	// Receipt Payment Statuses
	public static final String	PAYSTATUS_APPROVED		= "A";
	public static final String	PAYSTATUS_FEES			= "F";
	public static final String	PAYSTATUS_REALIZED		= "R";
	public static final String	PAYSTATUS_BOUNCE		= "B";
	public static final String	PAYSTATUS_CANCEL		= "C";

	// Receipt Payment Statuses
	public static final String	MODULETYPE_BOUNCE		= "B";
	public static final String	MODULETYPE_CANCEL		= "C";
	public static final String	MODULETYPE_FEE			= "F";
	
	// Payment Statuses
	public static final String PAYMENT_INTIATED = "I";
	public static final String PAYMENT_APPROVE = "A";
	public static final String PAYMENT_DOWNLOAD = "D";
	public static final String PAYMENT_SUCCESS = "S";
	public static final String PAYMENT_FAILURE = "F";
	
	//presentmentResponse statuscodes
	public static final String	PRES_SUCCESS			= "PR000";
	public static final String	PRES_PENDING			= "PR001";
	public static final String	PRES_DUPLICATE			= "PR002";
	public static final String	PRES_ERROR				= "PR004";
	public static final String	PRES_LOANCLOSED			= "PR005";
	public static final String	PRES_HOLD_DAYS			= "PR006";
	public static final String	PRES_FAILED				= "PR007";


}
