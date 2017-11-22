package com.pennant.app.constants;

public class ImplementationConstants {
	private ImplementationConstants() {
		super();
	}

	//IMPLEMENTATION TYPES
	public static final boolean	IMPLEMENTATION_CONVENTIONAL		= true;
	public static final boolean	IMPLEMENTATION_ISLAMIC			= false;

	public static final boolean	ALLOW_FINACTYPES				= false;
	public static final boolean	ALLOW_CAPITALIZE				= true;
	public static final boolean	LIMIT_INTERNAL					= true;		// Limit Module Exists or not
	public static final boolean	ONLINE_IRL_CHECK				= false;

	// Customer Modules
	public static final boolean	ALLOW_MULTIPLE_EMPLOYMENTS		= true;
	public static final boolean	ALLOW_CUSTOMER_MAINTENANCE		= true;
	public static final boolean	ALLOW_CUSTOMER_RATINGS			= false;
	public static final boolean	ALLOW_CUSTOMER_INCOMES			= true;
	public static final boolean	ALLOW_CUSTOMER_SHAREHOLDERS		= true;

	//Currency format Changes 
	public static final boolean	INDIAN_IMPLEMENTATION			= true;

	//AHB Implementation for Core Customer creation which includes reserve CIF and  Create CIF
	public static final boolean	VALIDATE_CORE_CUST_UPDATE		= false;
	public static final boolean	ALLOW_COVENANT_TYPES			= true;		// Covenants Allowed or not
	public static final boolean	COLLATERAL_INTERNAL				= false;	// Which Collateral We are using Internal or Third Party Collaterals
	public static final boolean	ALLOW_VAS						= false;	// VAS required or not
	// F = Administration/Insurance/Other Fees
	// C = Past due Penalty
	// I = Profit
	// P = Principal
	public static final String	REPAY_HIERARCHY_METHOD			= "FIPCS";
	// L = Late pay Profit
	// I = Profit
	public static final String	REPAY_INTEREST_HIERARCHY		= "LI";
	//Calculate PastDue on Day zero
	public static final boolean	LP_MARK_FIRSTDAY				= false;

	//Re payment methods
	public static final boolean	AUTO_ALLOWED					= false;
	public static final boolean	DDA_ALLOWED						= false;
	public static final boolean	DDM_ALLOWED						= true;
	public static final boolean	ECS_ALLOWED						= true;
	public static final boolean	NACH_ALLOWED					= true;
	//Pre-Approval Required
	public static final boolean	PREAPPROVAL_ALLOWED				= false;
	// Allow Quick Disbursement
	public static final boolean	ALLOW_QUICK_DISB				= false;
	// Allow Deviations
	public static final boolean	ALLOW_DEVIATIONS				= true;

	// Rebate
	public static final boolean	LATEPAY_PROFIT_CAL_ON_DAYZERO	= true;
	public static final boolean	REBATE_CAPPED_BY_FINANCE		= false;

	public static final boolean	ALLOW_EXPENSE_TRACKING			= false;					// JV Postings
	public static final boolean	ADD_FEEINFTV_ONCALC				= true;						// Fee Amount Added to Net Finance Amount in Display
	public static final boolean	ALLOW_FIN_SALARY_PAYMENT		= true;						// Salaried Payment Recording
	public static final boolean	ALLOW_SPECIALRATE				= false;					// Special Rate Allowed or Not in Referential Rate Component
	public static final boolean	ACCOUNTS_APPLICABLE				= false;					// Application Level Accounting Component Allowed or not
	public static final boolean	ALLOW_MANUAL_SCHEDULE			= false;					// Manual Schedule Allowed or not
	public static final boolean	ALLOW_INDICATIVE_RATE			= false;					// Allow Indicative Rate
	public static final boolean	CAPTURE_APPLICATION_NUMBER		= true;						// Allow Separate Application Number in finance main
	public static final boolean	ALLOW_PLANNED_EMIHOLIDAY		= true;						// Allow Planned EMI holidays 
	public static final boolean	ALLOW_UNPLANNED_EMIHOLIDAY		= true;						// Allow UnPlanned EMI holidays 
	public static final boolean	ALLOW_REAGE						= true;						// Allow UnPlanned EMI holidays 
	public static final boolean	ALLOW_DOWNPAY_SUPPORTPGM		= false;					// Allow DownPay Support Program 
	public static final boolean	ALLOW_BPI_TREATMENT				= true;						// Allow Broken Period Interest
	public static final boolean	INTERESTON_PASTDUE_PRINCIPAL	= true;						// Allow Interest on Past Due Principal
	public static final boolean	ALLOW_PLANNED_DEFERMENTS		= false;					// Allow Planned Deferments
	public static final boolean	ALLOW_PRICINGPOLICY				= false;					// Allow pricing policy
	
	public static final boolean	ALLOW_CREDITBUREAU				= false;					// Allow Credit Bureau
	public static final boolean	ALLOW_BUNDLEDPRODUCT			= false;					// Allow Bundled Product
	public static final boolean	ALLOW_COMMITMENT				= false;					// Allow Commitment Details
	public static final boolean	ALLOW_PFTUNCHG					= false;					// Allow Profit Unchanged
	public static final boolean	COLLATERAL_DELINK_AUTO			= false;					// Allow Auto Collateral Delink Process

	//Client Names and Current Client
	public static final String	CLIENT_AIB						= "AIB";
	public static final String	CLIENT_AHB						= "AHB";
	public static final String	CLIENT_BFL						= "BFL";
	public static final String	CLIENT_NAME						= CLIENT_BFL;
	public static final boolean	CLIENT_NFL						= true;

	// Address Type Details
	public static final boolean	ALLOW_ADDRESSTYPE_PRIORITY		= false;

	//Email Type Details
	public static final boolean	ALLOW_EMIALTYPE_PRIORITY		= false;

	//Phone Type Details
	public static final boolean	ALLOW_PHONETYPE_PRIORITY		= false;
	public static final boolean	ALLOW_DEPRECIATION				= false;		//DEPRECIATION   Required or Not 

	public static final String	NBFC							= "NBFC";
	public static final String	BANK							= "BANK";
	public static final String	CLIENTTYPE						= NBFC;

	// Partial Payment only with Principal Amount Adjustment
	public static final boolean	EARLYPAY_ADJ_PRI				= true;

	public static final boolean	ALLOW_INSURANCE					= false;		// Insurance Required or Not 
	public static final boolean	ALLOW_RIA						= false;		// RIA Required or Not 
	public static final boolean	ALLOW_ADDDBSF					= false;					// Add Disbursement future date Required or Not 
	
	public static final boolean FRQ_DATE_VALIDATION				= true;   //Constant to check Frequency and Date Match/Not

	public static final boolean	UPFRONT_ADJUST_PAYABLEADVISE	= false;		// Excess upfront fees paid by customer to be created as Payable Advise/Excess.
}
