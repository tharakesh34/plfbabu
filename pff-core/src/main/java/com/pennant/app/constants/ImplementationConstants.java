package com.pennant.app.constants;

public class ImplementationConstants {
	private ImplementationConstants() {
		super();
	}

	// IMPLEMENTATION TYPES
	public static final boolean IMPLEMENTATION_CONVENTIONAL = true;
	public static final boolean IMPLEMENTATION_ISLAMIC = false;

	public static final boolean ALLOW_FINACTYPES = false;
	public static final boolean ALLOW_CAPITALIZE = true;
	public static final boolean LIMIT_INTERNAL = true; // Limit Module Exists or not
	public static final boolean ONLINE_IRL_CHECK = false;

	// Customer Modules
	public static final boolean ALLOW_MULTIPLE_EMPLOYMENTS = true;
	public static final boolean ALLOW_CUSTOMER_MAINTENANCE = true;
	public static final boolean ALLOW_CUSTOMER_RATINGS = false;
	public static final boolean ALLOW_CUSTOMER_INCOMES = true;
	public static final boolean ALLOW_CUSTOMER_SHAREHOLDERS = true;

	// Currency format Changes
	public static final boolean INDIAN_IMPLEMENTATION = true;

	// AHB Implementation for Core Customer creation which includes reserve CIF and Create CIF
	public static final boolean VALIDATE_CORE_CUST_UPDATE = false;
	public static final boolean ALLOW_COVENANT_TYPES = true; // Covenants Allowed or not
	public static final boolean COLLATERAL_INTERNAL = true; // Which Collateral We are using Internal or Third Party Collaterals
	public static final boolean ALLOW_VAS = true; // VAS required or not F = Administration/Insurance/Other Fees C = Past due Penalty I = Profit P = Principal
	public static final String REPAY_HIERARCHY_METHOD = "FCIP";

	public static final String REPAY_INTEREST_HIERARCHY = "LI"; // L = Late pay Profit I = Profit
	// Calculate PastDue on Day zero
	public static final boolean LP_MARK_FIRSTDAY = true;
	// penalty calculated on SOD i.e
	public static final boolean LPP_CALC_SOD = true;
	// Re payment methods
	public static final boolean AUTO_ALLOWED = false;
	public static final boolean DDA_ALLOWED = false;
	public static final boolean DDM_ALLOWED = true;
	public static final boolean ECS_ALLOWED = true;
	public static final boolean NACH_ALLOWED = true;
	public static final boolean PDC_ALLOWED = true;
	// Pre-Approval Required
	public static final boolean PREAPPROVAL_ALLOWED = false;
	// Allow Quick Disbursement
	public static final boolean ALLOW_QUICK_DISB = false;
	// Allow Deviations
	public static final boolean ALLOW_DEVIATIONS = true;

	// Rebate
	public static final boolean LATEPAY_PROFIT_CAL_ON_DAYZERO = true;
	public static final boolean REBATE_CAPPED_BY_FINANCE = false;

	public static final boolean ALLOW_EXPENSE_TRACKING = false; // JV Postings
	public static final boolean ADD_FEEINFTV_ONCALC = true; // Fee Amount Ad to Net Finance Amount in Display

	public static final boolean ALLOW_FIN_SALARY_PAYMENT = true; // Salaried Payment Recording
	public static final boolean ALLOW_SPECIALRATE = false; // Special Rate Allowed or Not in Referential Rate Component 
	public static final boolean ACCOUNTS_APPLICABLE = false; // Application Level Accounting Component Allowed or not
	public static final boolean ALLOW_MANUAL_SCHEDULE = false; // Manual Schedule Allowed or not
	public static final boolean ALLOW_INDICATIVE_RATE = false; // Allow Indicative Rate
	public static final boolean CAPTURE_APPLICATION_NUMBER = true; // Allow Separate Application Number in finance // main
	public static final boolean ALLOW_PLANNED_EMIHOLIDAY = true; // Allow Planned EMI holidays
	public static final boolean ALLOW_UNPLANNED_EMIHOLIDAY = true; // Allow UnPlanned EMI holidays
	public static final boolean ALLOW_REAGE = true; // Allow UnPlanned EMI holidays

	public static final boolean ALLOW_DOWNPAY_SUPPORTPGM = false; // Allow DownPay Support Program
	public static final boolean ALLOW_BPI_TREATMENT = true; // Allow Broken Period Interest
	public static final boolean INTERESTON_PASTDUE_PRINCIPAL = true; // Allow Interest on Past Due Principal
	public static final boolean ALLOW_PLANNED_DEFERMENTS = false; // Allow Planned Deferments
	public static final boolean ALLOW_PRICINGPOLICY = false; // Allow pricing policy
	public static final boolean ALLOW_CREDITBUREAU = false; // Allow Credit Bureau
	public static final boolean ALLOW_BUNDLEDPRODUCT = false; // Allow Bundled Product
	public static final boolean ALLOW_COMMITMENT = false; // Allow Commitment Details
	public static final boolean ALLOW_PFTUNCHG = false; // Allow Profit Unchanged
	public static final boolean COLLATERAL_DELINK_AUTO = false; // Allow Auto Collateral DelinkProcess

	// Client Names and Current Client
	public static final String CLIENT_AIB = "AIB";
	public static final String CLIENT_AHB = "AHB";
	public static final String CLIENT_BFL = "BFL";
	public static final String CLIENT_NAME = CLIENT_BFL;
	public static final boolean CLIENT_NFL = false;

	// Address Type Details
	public static final boolean ALLOW_ADDRESSTYPE_PRIORITY = false;

	// Email Type Details
	public static final boolean ALLOW_EMIALTYPE_PRIORITY = false;

	// Phone Type Details
	public static final boolean ALLOW_PHONETYPE_PRIORITY = false;
	public static final boolean ALLOW_DEPRECIATION = false; // DEPRECIATION Required or Not
	public static final String NBFC = "NBFC";
	public static final String BANK = "BANK";
	public static final String CLIENTTYPE = NBFC;

	// Partial Payment only with Principal Amount Adjustment
	public static final boolean EARLYPAY_ADJ_PRI = true;
	public static final boolean ALLOW_INSURANCE = false; // Insurance Required or Not
	public static final boolean ALLOW_RIA = false; // RIA Required or Not
	public static final boolean ALLOW_ADDDBSF = false; // Add Disbursement future date Required or Not
	public static final boolean UPFRONT_ADJUST_PAYABLEADVISE = false; // Excess upfront fees paid by customer to be created as Payable Advise/Excess.
	public static final boolean CO_APP_ENQ_SAME_AS_CUST_ENQ = true; // Inquiry menu in the co-applicant should show screen as customer details
	public static final boolean PAN_DUPLICATE_NOT_ALLOWED = true;
	public static final boolean ALLOW_AUTO_DISBURSEMENTS = false; // Allow Auto Disbursements which will be called during the EOD.
	public static final boolean VARTUAL_DPD = true;
	public static final boolean ALLOW_COSTOFFUNDS = true; // Cost Of Funds For Loan Type.
	public static final boolean ALLOW_IRRCODES = true; // IRR Codes For Loan Type.
	public static final boolean ALLOW_FEES_RECALCULATE = true; // Fees Recalculate always
	public static final boolean ALLOW_PAID_FEE_SCHEDULE_METHOD = false; // Fee Schedule Methods for Paid by customer and Waved by Bank.
	public static final boolean ALLOW_BARCODE = false;
	public static final boolean CIBIL_BUTTON_REQ = false;
	public static final boolean PPPERCENT_VALIDATION_REQ = false; // validation based on partial payment percent

	// Deposit Process
	public static final boolean DEPOSIT_PROC_REQ = false;

	public static final boolean ENTITY_REQ_TRAIL_BAL = false;

	// Allow Schedule Recalculation lock period Changes.
	public static final boolean ALW_SCH_RECAL_LOCK = false;

	// LPP Calculation on RULE FIXED .ONLY FOR FGROUP IT SHOULD BE TRUE
	public static final boolean ALW_LPP_RULE_FIXED = false;

	// Auto Cancellation of Loans after "N" Days Allowed.ONLY FOR FGROUP IT SHOULD BE TRUE
	public static final boolean ALW_LOAN_AUTO_CANCEL = false;

	// Capitalizing on Lock Period of Schedule Profit Balance
	// 1. TRUE : Default Capitalize Balance amount when Applicable Capitalization
	// 2. FALSE : Check Schedule is on Lock Period or not, if not Capitalize amount will reset
	public static final boolean DFT_CPZ_RESET_ON_RECAL_LOCK = true;

	// Collateral Assignment Percentage calculation based on parameter
	// 1. NO_ADJ : No Adjustment
	// 2. FULL_ADJ : Full Adjustment(100%)
	// 3. REQ_ADJ : Requested Adjustment based on Utilization
	public static final String COLLATERAL_ADJ = "NO_ADJ";

	// In Penalty Calculation , If calculated amount is less than cap amount
	// Need to rest amount with CAP amount only
	public static final boolean ALW_LPP_MIN_CAP_AMT = false;

	// Allow send notification based on this variable when request is coming
	// through API.
	public static final boolean SEND_NOTIFICATION_ON_CREATE_LOAN_API = false;

	//Allow approved mandate in Loan Pending State trough approve mandate API
	public static final boolean ALW_APPROVED_MANDATE_IN_ORG = false;

	//Allow profit schedule invoice
	public static final boolean ALW_PROFIT_SCHD_INVOICE = true;

	// If True, credit amounts will be shown with "-" sign in Trial balance. Bajaj it is True
	public static final boolean NEGATE_SIGN_TB = false;

	// If True, credit amounts will be shown with "-" sign in Trial balance. Bajaj it is True
	public static final boolean COAPP_CUST_CRET = false;

	//The below variable is set to show the Reporting manager list,Branch Details tab if set false 
	//else User Hierarchy tab if set true.
	public static final boolean ALLOW_ACCESS_CONTROL_TYPE = true;

	public static final boolean APPLY_FDDLOCKPERIOD_AFTERGRACE = false;

	public static final boolean ALW_FLEXI = false;
	public static final boolean ALW_SUBVENSION = false;

	//For Advance EMI interest frequency
	public static final boolean ALLOW_ADVEMI_FREQUENCY = false;

	//For Advance interest Advance at interest frequency
	public static boolean ALLOW_ADVINT_FREQUENCY = true;

	/*
	 * For Advance Interest/EMI if there is change in the interest or EMI, due to any servicing changes , do we need to
	 * created advice (Payable /Receivable) will be depended on this flag. Defaulted to true, Since it is the default
	 * behavior
	 */
	public static final boolean ALW_ADV_INTEMI_ADVICE_CREATION = true;

	//Covenant tab visible
	public static final boolean COVENANT_REQUIRED = false;

	// Both loan and legal roles will be available for assignment from Loan &
	// Legal
	public static final boolean QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES = true;

	// Upfront Fee reversal required on loan cancellation
	public static final boolean UPFRONT_FEE_REVERSAL_REQ = false;

	//GST Invoice Due basis/Receipt Basis
	// If Due Basis creation , means LPP created as fixed amount and same should be accrued on creation date then PARAMTER = "D"
	// If Accrual Basis Creation, means LPP is calculated on daily basis then Accrual postings happen on Month End --  PARAMTER = "A"
	public static final String LPP_GST_DUE_ON = "A";

}
