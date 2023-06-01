package com.pennant.backend.util;

public class RuleConstants {
	public static final String RULEFIELD_CCY = "CCY";

	// Rule Module Codes
	public static final String MODULE_FEES = "FEES";
	public static final String MODULE_FEEPERC = "FEEPERC";
	public static final String MODULE_AGRRULE = "AGRRULE";
	public static final String MODULE_VERRULE = "VERRULE";
	public static final String MODULE_CLRULE = "CLRULE";
	public static final String MODULE_ELGRULE = "ELGRULE";
	public static final String MODULE_RATERULE = "RATERULE";
	public static final String MODULE_PROVSN = "PROVSN";
	public static final String MODULE_REFUND = "REFUND";
	public static final String MODULE_SCORES = "SCORES";
	public static final String MODULE_SUBHEAD = "SUBHEAD";
	public static final String MODULE_DOWNPAYRULE = "DOWNPAY";
	public static final String MODULE_LMTLINE = "LMTLINE";
	public static final String MODULE_IRLFILTER = "IRFILTER";
	public static final String MODULE_LTVRULE = "LTVRULE";
	public static final String MODULE_BOUNCE = "BOUNCE";
	public static final String MODULE_GSTRULE = "GSTRULE"; // GST Rules
	public static final String MODULE_LPPRULE = "LPPRULE"; // LPP Rules
	public static final String MODULE_AMORTIZATIONMETHOD = "AMZMTH";
	public static final String MODULE_STGACRULE = "STGRULE"; // Stage Accounting
	public static final String MODULE_DUEDATERULE = "DDLRULE";
	public static final String MODULE_BRERULE = "BRERULE";
	public static final String MODULE_AUTOREFUND = "FEE_AUTO_REFUND";

	// Rule Event Codes
	public static final String EVENT_ADDDBS = "ADDDBS";
	public static final String EVENT_AGRRULE = "AGRRULE";
	public static final String EVENT_VERRULE = "VERRULE";
	public static final String EVENT_CLRULE = "CLRULE";
	public static final String EVENT_DEFFRQ = "DEFFRQ";
	public static final String EVENT_DEFRPY = "DEFRPY";
	public static final String EVENT_DPRCIATE = "DPRCIATE";
	public static final String EVENT_EARLYPAY = "EARLYPAY";
	public static final String EVENT_EARLYSTL = "EARLYSTL";
	public static final String EVENT_ELGRULE = "ELGRULE";
	public static final String EVENT_RATERULE = "RATERULE";
	public static final String EVENT_FSCORE = "FSCORE";
	public static final String EVENT_MNTCMT = "MNTCMT";
	public static final String EVENT_NEWCMT = "NEWCMT";
	public static final String EVENT_PROVSN = "PROVSN";
	public static final String EVENT_REFUND = "REFUND";
	public static final String EVENT_REPAY = "REPAY";
	public static final String EVENT_RSCORE = "RSCORE";
	public static final String EVENT_SUBHEAD = "SUBHEAD";
	public static final String EVENT_WRITEOFF = "WRITEOFF";
	public static final String EVENT_DOWNPAYRULE = "DOWNPAY";
	public static final String EVENT_CUSTOMER = "CUSTOMER";
	public static final String EVENT_BANK = "BANK";
	public static final String EVENT_LTVRULE = "LTVRULE";
	public static final String EVENT_BOUNCE = "BOUNCE";
	public static final String EVENT_AMORTIZATIONMETHOD = "AMZMTH";
	public static final String EVENT_STAGEACCOUNTING = "STGRULE"; // Stage
																	// Accounting
	public static final String EVENT_DUEDATERULE = "DDLRULE";
	public static final String EVENT_AUTOTREFUND = "FEE_AUTO_REFUND";

	public static final String RETURNTYPE_DECIMAL = "D";
	public static final String RETURNTYPE_STRING = "S";
	public static final String RETURNTYPE_BOOLEAN = "B";
	public static final String RETURNTYPE_INTEGER = "I";
	public static final String RETURNTYPE_OBJECT = "O";
	public static final String RETURNTYPE_CALCSTRING = "C";

	// Eligibility Constant Rules
	public static final String ELGRULE_DSRCAL = "DSRCAL";
	public static final String ELGRULE_PDDSRCAL = "PDDSRCAL";
	public static final String ELGRULE_SURPLUS = "SURPLUS";
	public static final String ELGRULE_FOIR = "FOIRELG";
	public static final String ELGRULE_LTV = "LTVELG";

	// Default Fee Type Constants
	public static final String DFT_FEE_FINANCE = "F";
	public static final String DFT_FEE_WAIVERBYBANK = "W";
	public static final String DFT_FEE_PAIDBYCUST = "P";
	public static final String DFT_FEE_DEDUCTFROMDISB = "D";

	// Rule Builder Constants
	public static final String EQUALS_LABEL = " === ";
	public static final String GREATER_LABEL = " > ";
	public static final String GREATEREQUAL_LABEL = " >= ";
	public static final String LESS_LABEL = " < ";
	public static final String LESSEQUAL_LABEL = " <= ";
	public static final String NOTEQUAL_LABEL = " !== ";

	public static final String IN_LABEL = " IN ";
	public static final String NOTIN_LABEL = " NOT IN ";

	public static final String LIKE_LABEL = " LIKE ";
	public static final String NOTLIKE_LABEL = " NOT LIKE ";

	public static final String EXISTS_LABEL = " EXISTS ";
	public static final String NOTEXISTS_LABEL = " NOT EXISTS ";

	public static final String ISNULL_LABEL = " IS NULL";
	public static final String ISNOTNULL_LABEL = " IS NOT NULL";

	public static final String AND_LABEL = " && ";
	public static final String AND_VALUE = "AND";

	public static final String OR_LABEL = " || ";
	public static final String OR_VALUE = "OR";

	public static final String LIKEEQUALWOCASE_LABEL = "LIKE EQUAL";
	public static final String LIKEEQUALWOCASE_VALUE = " LIKE= ";
	public static final String GROUPBY_LABEL = "GROUPBY";
	public static final String GROUPBY_VALUE = " GROUPBY ";

	public static final String STATICTEXT = "STATICTEXT";
	public static final String GLOBALVAR = "GLOBALVAR";
	public static final String FIELDLIST = "FIELDLIST";
	public static final String CALCVALUE = "CALCVALUE";
	public static final String SUBQUERY = "SUBQUERY";
	public static final String FUNCTION = "FUNCTION";
	public static final String DBVALUE = "DBVALUE";

	public static final int RULEMODE_SELECTFIELDLIST = 1; // Select from filed
															// list in
															// JavaScriptBuilder

	public static final String TAB_DESIGN = "DESIGN";
	public static final String TAB_SCRIPT = "SCRIPT";
	public static final String TAB_SPLSCRIPT = "SPLSCRIPT";

	public static final String fm = "FinanceMain";
	public static final String ft = "FinanceType";
	public static final String cust = "Customer";
	public static final String custEmp = "CustEmployeeDetail";
	public static final String lt = "LoanLetter";

	public static final String financeMain = "fm";
	public static final String financeType = "ft";
	public static final String customer = "ct";
	public static final String custEmployeeDetail = "custEmp";
	public static final String letter = "lt";

	// JavaScriptBuilder ComponentType Constants
	public static final String COMPONENTTYPE_COMBOBOX = "Combobox";
	public static final String COMPONENTTYPE_DECIMAL = "Decimal";
	public static final String COMPONENTTYPE_INTEGER = "Integer";
	public static final String COMPONENTTYPE_STRING = "String";
	public static final String COMPONENTTYPE_PERCENTAGE = "Percentage";
	public static final String COMPONENTTYPE_EXTENDEDCOMBOBOX = "ExtendedCombobox";

	// Rule code Constants for GST
	public static final String CODE_CGST = "CGST"; // CGST
	public static final String CODE_IGST = "IGST"; // IGST
	public static final String CODE_SGST = "SGST"; // SGST
	public static final String CODE_UGST = "UGST"; // UGST
	public static final String CODE_TOTAL_GST = "TOTAL_GST"; // TOTAL GST

	public static final String CODE_TOTAL_AMOUNT_INCLUDINGGST = "TOTAL_IGST";
	public static final String CODE_CESS = "CESS";

	public static final String CALCTYPE_FIXED_AMOUNT = "F";
	public static final String CALCTYPE_PERCENTAGE = "P";

	public static final String CALCON_TRANSACTION_AMOUNT = "TRANSCAMT";

	private RuleConstants() {
		super();
	}
}
