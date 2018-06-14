package com.pennanttech.bajaj.process.collections.model;

public class CollectionConstants {

	public CollectionConstants() {
		super();
	}
	
	public static final int		COLLECTION_WAIT					= 0;	//wait
	public static final int		COLLECTION_INPROGRESS			= 1;	//in-progress
	public static final int		COLLECTION_SUCCESS				= 2;	//Success
	public static final int		COLLECTION_FAILED				= 3;	//failed
	
	public static final String	INTERFACE_COLLECTION			= "C";
	
	//Collection Procedures
	public static final String	SP_CUST_PERS_INFO_V				= "SP_CUST_PERS_INFO_V";
	public static final String	SP_CUST_ADDRESS_INFO_V			= "SP_CUST_ADDRESS_INFO_V";
	
	public static final String	SP_CASE_DETAILS_V				= "SP_CASE_DETAILS_V";
	public static final String	SP_DISBURSAL_INFO_V				= "SP_DISBURSAL_INFO_V";
	public static final String	SP_GUARANTOR_DETAILS_V			= "SP_GUARANTOR_DETAILS_V";
	public static final String	SP_PAYMENT_DETAILS_V			= "SP_PAYMENT_DETAILS_V";
	public static final String	SP_REPAYMENT_SCH_V				= "SP_REPAYMENT_SCH_V";
	public static final String	SP_BOUNCE_HISTORY_V				= "SP_BOUNCE_HISTORY_V";
	public static final String	SP_FORECLOSURE_DETAILS_V		= "SP_FORECLOSURE_DETAILS_V";
	public static final String	SP_NON_DELINQ_ACCT_V			= "SP_NON_DELINQ_ACCT_V";
	public static final String	SP_PRODUCT_MASTER				= "SP_PRODUCT_MASTER";
	
	//Collection Tables
	public static final String	TN_CUST_PERS_INFO_V_TMP			= "CUST_PERS_INFO_V_TMP";
	public static final String	TN_CUST_ADDRESS_INFO_V_TMP		= "CUST_ADDRESS_INFO_V_TMP";
	
	public static final String	TN_CASE_DETAILS_V_TMP			= "CASE_DETAILS_V_TMP";
	public static final String	TN_DISBURSAL_INFO_TMP			= "DISBURSAL_INFO_TMP";	
	public static final String	TN_GUARANTOR_DETAILS_V_TMP		= "GUARANTOR_DETAILS_V_TMP";
	public static final String	TN_PAYMENT_DETAILS_V			= "PAYMENT_DETAILS_V_TMP";
	public static final String	TN_REPAYMENT_SCH_V_TMP			= "REPAYMENT_SCH_V_TMP";
	public static final String	TN_BOUNCE_HISTORY_V_TEMP		= "BOUNCE_HISTORY_V_TMP";
	public static final String	TN_FORECLOSURE_DETAILS_V_TMP	= "FORECLOSURE_DETAILS_V_TMP";
	public static final String	TN_NON_DELINQ_BOM_POSITION_ACCT	= "NON_DELINQ_BOM_POSITION_ACCT";
	public static final String	TN_PRODUCT_MASTER_TMP			= "PRODUCT_MASTER_TMP";
	
	public static final String	TN_COLLECTIONFINANCES			= "COLLECTIONFINANCES";
	public static final String	TN_DATAEXTRACTIONS				= "DATAEXTRACTIONS";
	
	// Interface Mapping
    public static final String  INTERFACEMAPPING_VALUE			= "Value";
    public static final String  INTERFACEMAPPING_COLUMN			= "Column";
    public static final String  INTERFACEMAPPING_MASTER			= "Master";
    
    
    public static final String FEE_CALCULATION_TYPE_RULE = "RULE";
	public static final String FEE_CALCULATION_TYPE_FIXEDAMOUNT = "FIXEDAMT";
	public static final String FEE_CALCULATION_TYPE_PERCENTAGE 	= "PERCENTG";

	public static final String FEE_CALCULATEDON_LOANAMOUNT 		= "LOANAM";
	public static final String FEE_CALCULATEDON_TOTALASSETVALUE = "TOTAST";
	public static final String FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL = "OUTSPL";

	public static final String EXAMOUNTTYPE_EXCESS 				= "E";
	public static final String EXAMOUNTTYPE_EMIINADV 			= "A";

	public static final int MODULEID_FINTYPE 					= 1;
	public static final String ACCEVENT_EARLYSTL 				= "EARLYSTL";
}
