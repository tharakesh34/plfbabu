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
	public static final String	EXCESSADJUSTTO_PARTPAY 	= "P";
	
	// Receipt Modes
	public static final String	RECEIPTMODE_CASH		= "CASH";
	public static final String	RECEIPTMODE_CHEQUE		= "CHEQUE";
	public static final String	RECEIPTMODE_DD			= "DD";
	public static final String	RECEIPTMODE_NEFT		= "NEFT";
	public static final String	RECEIPTMODE_RTGS		= "RTGS";
	public static final String	RECEIPTMODE_IMPS		= "IMPS";
	public static final String	RECEIPTMODE_EXCESS		= "EXCESS";
	
	// Allocation Methods
	public static final String	ALLOCATIONTYPE_AUTO		= "A";
	public static final String	ALLOCATIONTYPE_MANUAL	= "M";

	// Allocation Types
	public static final String	ALLOCATION_PRI			= "PRI";
	public static final String	ALLOCATION_PFT			= "PFT";
	public static final String	ALLOCATION_LPFT			= "LPFT";
	public static final String	ALLOCATION_ODC			= "ODC";
	public static final String	ALLOCATION_FEE			= "FEE";
	public static final String	ALLOCATION_INS			= "INS";
	public static final String	ALLOCATION_MANADV		= "MANADV";
	
	// Receipt Types
	public static final String	RECEIPTTYPE_RECIPT		= "R";
	public static final String	RECEIPTTYPE_PAYMENT		= "P";
	
	// Receipt Types
	public static final String	RECEIPTTO_FINANCE		= "F";
	public static final String	RECEIPTTO_CUSTOMER		= "C";
	
	// Receipt Payment Type
	public static final String	PAYTYPE_CASH			= "CASH";
	public static final String	PAYTYPE_CHEQUE			= "CHEQUE";
	public static final String	PAYTYPE_DD				= "DD";
	public static final String	PAYTYPE_NEFT			= "NEFT";
	public static final String	PAYTYPE_RTGS			= "RTGS";
	public static final String	PAYTYPE_IMPS			= "IMPS";
	public static final String	PAYTYPE_EXCESS			= "EXCESS";
	public static final String	PAYTYPE_EMIINADV		= "EMIINADV";
	public static final String	PAYTYPE_PAYABLE			= "PAYABLE";
	
	//Presentment Exclude Reasons 
	public static final int	PRESENTMENT_EXC_EMIINADVANCE = 1;
	public static final int	PRESENTMENT_EXC_EMIHOLD = 2;
	public static final int	PRESENTMENT_EXC_MANDATEHOLD = 3;
	

}
