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

}
