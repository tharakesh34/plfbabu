package com.pennant.app.constants;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.pennanttech.extension.implementation.IFeatureExtension;
import com.pennanttech.pennapps.core.FactoryException;

@Component
public class ImplementationConstants {

	private ImplementationConstants() {
		super();
	}

	/**
	 * The static block to initialise the implementation constants.
	 */
	static {
		// Get the constants specified in the extension layer.
		// Notes:
		// The below are the steps that are required to extend the constants.
		// 1. Interface, IFeatureExtension, will be available in pff-core.
		// 2. The implementation class, FeatureExtension, for the above interface will be available in both
		//    - pff-extension-core (dummy implementation and can be used for testing the constants as well)
		//    - pff-extension-client

		Map<String, Object> extensions = getFeatureExtensions();

		ALLOW_MULTI_CCY = getValueAsBoolean(extensions, "ALLOW_MULTI_CCY", false);
		IMPLEMENTATION_CONVENTIONAL = getValueAsBoolean(extensions, "IMPLEMENTATION_CONVENTIONAL", true);
		IMPLEMENTATION_ISLAMIC = getValueAsBoolean(extensions, "IMPLEMENTATION_ISLAMIC", false);
		LIST_RENDER_ON_LOAD = getValueAsBoolean(extensions, "LIST_RENDER_ON_LOAD", true);
		ALLOW_FINACTYPES = getValueAsBoolean(extensions, "ALLOW_FINACTYPES", false);
		ALLOW_CAPITALIZE = getValueAsBoolean(extensions, "ALLOW_CAPITALIZE", true);
		LIMIT_INTERNAL = getValueAsBoolean(extensions, "LIMIT_INTERNAL", true);
		ONLINE_IRL_CHECK = getValueAsBoolean(extensions, "ONLINE_IRL_CHECK", false);
		ALLOW_MULTIPLE_EMPLOYMENTS = getValueAsBoolean(extensions, "ALLOW_MULTIPLE_EMPLOYMENTS", true);
		ALLOW_CUSTOMER_MAINTENANCE = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_MAINTENANCE", true);
		ALLOW_CUSTOMER_RATINGS = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_RATINGS", false);
		ALLOW_CUSTOMER_INCOMES = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_INCOMES", true);
		ALLOW_CUSTOMER_SHAREHOLDERS = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_SHAREHOLDERS", true);
		ALLOW_COLLATERAL_VALUE_UPDATION = getValueAsBoolean(extensions, "ALLOW_COLLATERAL_VALUE_UPDATION", false);
		INDIAN_IMPLEMENTATION = getValueAsBoolean(extensions, "INDIAN_IMPLEMENTATION", true);
		VALIDATE_CORE_CUST_UPDATE = getValueAsBoolean(extensions, "VALIDATE_CORE_CUST_UPDATE", false);
		ALLOW_COVENANT_TYPES = getValueAsBoolean(extensions, "ALLOW_COVENANT_TYPES", true);
		COLLATERAL_INTERNAL = getValueAsBoolean(extensions, "COLLATERAL_INTERNAL", true);
		ALLOW_VAS = getValueAsBoolean(extensions, "ALLOW_VAS", true);
		LP_MARK_FIRSTDAY = getValueAsBoolean(extensions, "LP_MARK_FIRSTDAY", true);
		LPP_CALC_SOD = getValueAsBoolean(extensions, "LPP_CALC_SOD", true);
		AUTO_ALLOWED = getValueAsBoolean(extensions, "AUTO_ALLOWED", false);
		DDA_ALLOWED = getValueAsBoolean(extensions, "DDA_ALLOWED", false);
		DDM_ALLOWED = getValueAsBoolean(extensions, "DDM_ALLOWED", true);
		ECS_ALLOWED = getValueAsBoolean(extensions, "ECS_ALLOWED", true);
		NACH_ALLOWED = getValueAsBoolean(extensions, "NACH_ALLOWED", true);
		PDC_ALLOWED = getValueAsBoolean(extensions, "PDC_ALLOWED", true);
		PREAPPROVAL_ALLOWED = getValueAsBoolean(extensions, "PREAPPROVAL_ALLOWED", false);
		ALLOW_DEVIATIONS = getValueAsBoolean(extensions, "ALLOW_DEVIATIONS", true);
		LATEPAY_PROFIT_CAL_ON_DAYZERO = getValueAsBoolean(extensions, "LATEPAY_PROFIT_CAL_ON_DAYZERO", true);
		REBATE_CAPPED_BY_FINANCE = getValueAsBoolean(extensions, "REBATE_CAPPED_BY_FINANCE", false);
		ALLOW_EXPENSE_TRACKING = getValueAsBoolean(extensions, "ALLOW_EXPENSE_TRACKING", false);
		ADD_FEEINFTV_ONCALC = getValueAsBoolean(extensions, "ADD_FEEINFTV_ONCALC", true);
		ALLOW_FIN_SALARY_PAYMENT = getValueAsBoolean(extensions, "ALLOW_FIN_SALARY_PAYMENT", true);
		ALLOW_SPECIALRATE = getValueAsBoolean(extensions, "ALLOW_SPECIALRATE", false);
		ACCOUNTS_APPLICABLE = getValueAsBoolean(extensions, "ACCOUNTS_APPLICABLE", false);
		ALLOW_MANUAL_SCHEDULE = getValueAsBoolean(extensions, "ALLOW_MANUAL_SCHEDULE", false);
		ALLOW_INDICATIVE_RATE = getValueAsBoolean(extensions, "ALLOW_INDICATIVE_RATE", false);
		CAPTURE_APPLICATION_NUMBER = getValueAsBoolean(extensions, "CAPTURE_APPLICATION_NUMBER", true);
		ALLOW_PLANNED_EMIHOLIDAY = getValueAsBoolean(extensions, "ALLOW_PLANNED_EMIHOLIDAY", true);
		ALLOW_UNPLANNED_EMIHOLIDAY = getValueAsBoolean(extensions, "ALLOW_UNPLANNED_EMIHOLIDAY", true);
		ALLOW_REAGE = getValueAsBoolean(extensions, "ALLOW_REAGE", true);
		ALLOW_DOWNPAY_SUPPORTPGM = getValueAsBoolean(extensions, "ALLOW_DOWNPAY_SUPPORTPGM", false);
		ALLOW_BPI_TREATMENT = getValueAsBoolean(extensions, "ALLOW_BPI_TREATMENT", true);
		INTERESTON_PASTDUE_PRINCIPAL = getValueAsBoolean(extensions, "INTERESTON_PASTDUE_PRINCIPAL", true);
		ALLOW_PLANNED_DEFERMENTS = getValueAsBoolean(extensions, "ALLOW_PLANNED_DEFERMENTS", false);
		ALLOW_PRICINGPOLICY = getValueAsBoolean(extensions, "ALLOW_PRICINGPOLICY", false);
		ALLOW_CREDITBUREAU = getValueAsBoolean(extensions, "ALLOW_CREDITBUREAU", false);
		ALLOW_BUNDLEDPRODUCT = getValueAsBoolean(extensions, "ALLOW_BUNDLEDPRODUCT", false);
		ALLOW_COMMITMENT = getValueAsBoolean(extensions, "ALLOW_COMMITMENT", false);
		ALLOW_PFTUNCHG = getValueAsBoolean(extensions, "ALLOW_PFTUNCHG", false);
		COLLATERAL_DELINK_AUTO = getValueAsBoolean(extensions, "COLLATERAL_DELINK_AUTO", false);
		CLIENT_NFL = getValueAsBoolean(extensions, "CLIENT_NFL", false);
		ALLOW_ADDRESSTYPE_PRIORITY = getValueAsBoolean(extensions, "ALLOW_ADDRESSTYPE_PRIORITY", false);
		ALLOW_EMIALTYPE_PRIORITY = getValueAsBoolean(extensions, "ALLOW_EMIALTYPE_PRIORITY", false);
		ALLOW_PHONETYPE_PRIORITY = getValueAsBoolean(extensions, "ALLOW_PHONETYPE_PRIORITY", false);
		ALLOW_DEPRECIATION = getValueAsBoolean(extensions, "ALLOW_DEPRECIATION", false);
		EARLYPAY_ADJ_PRI = getValueAsBoolean(extensions, "EARLYPAY_ADJ_PRI", true);
		ALLOW_INSURANCE = getValueAsBoolean(extensions, "ALLOW_INSURANCE", false);
		ALLOW_RIA = getValueAsBoolean(extensions, "ALLOW_RIA", false);
		ALLOW_ADDDBSF = getValueAsBoolean(extensions, "ALLOW_ADDDBSF", false);
		UPFRONT_ADJUST_PAYABLEADVISE = getValueAsBoolean(extensions, "UPFRONT_ADJUST_PAYABLEADVISE", false);
		CO_APP_ENQ_SAME_AS_CUST_ENQ = getValueAsBoolean(extensions, "CO_APP_ENQ_SAME_AS_CUST_ENQ", true);
		PAN_DUPLICATE_NOT_ALLOWED = getValueAsBoolean(extensions, "PAN_DUPLICATE_NOT_ALLOWED", true);
		ALLOW_AUTO_DISBURSEMENTS = getValueAsBoolean(extensions, "ALLOW_AUTO_DISBURSEMENTS", false);
		VARTUAL_DPD = getValueAsBoolean(extensions, "VARTUAL_DPD", true);
		ALLOW_COSTOFFUNDS = getValueAsBoolean(extensions, "ALLOW_COSTOFFUNDS", true);
		ALLOW_IRRCODES = getValueAsBoolean(extensions, "ALLOW_IRRCODES", true);
		ALLOW_FEES_RECALCULATE = getValueAsBoolean(extensions, "ALLOW_FEES_RECALCULATE", true);
		ALLOW_PAID_FEE_SCHEDULE_METHOD = getValueAsBoolean(extensions, "ALLOW_PAID_FEE_SCHEDULE_METHOD", false);
		ALLOW_BARCODE = getValueAsBoolean(extensions, "ALLOW_BARCODE", false);
		PPPERCENT_VALIDATION_REQ = getValueAsBoolean(extensions, "PPPERCENT_VALIDATION_REQ", false);
		DEPOSIT_PROC_REQ = getValueAsBoolean(extensions, "DEPOSIT_PROC_REQ", false);
		ENTITY_REQ_TRAIL_BAL = getValueAsBoolean(extensions, "ENTITY_REQ_TRAIL_BAL", false);
		ALW_LPP_RULE_FIXED = getValueAsBoolean(extensions, "ALW_LPP_RULE_FIXED", false);
		ALW_LOAN_AUTO_CANCEL = getValueAsBoolean(extensions, "ALW_LOAN_AUTO_CANCEL", false);
		DFT_CPZ_RESET_ON_RECAL_LOCK = getValueAsBoolean(extensions, "DFT_CPZ_RESET_ON_RECAL_LOCK", false);
		ALW_LPP_MIN_CAP_AMT = getValueAsBoolean(extensions, "ALW_LPP_MIN_CAP_AMT", false);
		SEND_NOTIFICATION_ON_CREATE_LOAN_API = getValueAsBoolean(extensions, "SEND_NOTIFICATION_ON_CREATE_LOAN_API",
				false);
		ALW_APPROVED_MANDATE_IN_ORG = getValueAsBoolean(extensions, "ALW_APPROVED_MANDATE_IN_ORG", false);
		ALW_PROFIT_SCHD_INVOICE = getValueAsBoolean(extensions, "ALW_PROFIT_SCHD_INVOICE", true);
		NEGATE_SIGN_TB = getValueAsBoolean(extensions, "NEGATE_SIGN_TB", false);
		ALLOW_ACCESS_CONTROL_TYPE = getValueAsBoolean(extensions, "ALLOW_ACCESS_CONTROL_TYPE", true);
		APPLY_FDDLOCKPERIOD_AFTERGRACE = getValueAsBoolean(extensions, "APPLY_FDDLOCKPERIOD_AFTERGRACE", false);
		ALW_FLEXI = getValueAsBoolean(extensions, "ALW_FLEXI", false);
		ALW_SUBVENSION = getValueAsBoolean(extensions, "ALW_SUBVENSION", false);
		ALLOW_ADVEMI_FREQUENCY = getValueAsBoolean(extensions, "ALLOW_ADVEMI_FREQUENCY", false);
		ALLOW_ADVINT_FREQUENCY = getValueAsBoolean(extensions, "ALLOW_ADVINT_FREQUENCY", true);
		RCVADV_CREATE_ON_INTEMI = getValueAsBoolean(extensions, "RCVADV_CREATE_ON_INTEMI", true);
		PYBADV_CREATE_ON_INTEMI = getValueAsBoolean(extensions, "PYBADV_CREATE_ON_INTEMI", true);
		COVENANT_REQUIRED = getValueAsBoolean(extensions, "COVENANT_REQUIRED", false);
		QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES = getValueAsBoolean(extensions, "QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES",
				true);
		UPFRONT_FEE_REVERSAL_REQ = getValueAsBoolean(extensions, "UPFRONT_FEE_REVERSAL_REQ", false);
		GAP_INTEREST_REQUIRED = getValueAsBoolean(extensions, "GAP_INTEREST_REQUIRED", true);
		ALLOW_ALL_SERV_RCDS = getValueAsBoolean(extensions, "ALLOW_ALL_SERV_RCDS", false);
		LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS = getValueAsBoolean(extensions, "LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS",
				false);
		ENTITYCODE_REQ_FOR_MANDATE_PROCESS = getValueAsBoolean(extensions, "ENTITYCODE_REQ_FOR_MANDATE_PROCESS", false);
		DISBURSEMENT_AUTO_DOWNLOAD = getValueAsBoolean(extensions, "DISBURSEMENT_AUTO_DOWNLOAD", false);
		DISBURSEMENT_AUTO_UPLOAD = getValueAsBoolean(extensions, "DISBURSEMENT_AUTO_UPLOAD", false);
		MANDATE_AUTO_DOWNLOAD = getValueAsBoolean(extensions, "MANDATE_AUTO_DOWNLOAD", false);
		MANDATE_AUTO_UPLOAD = getValueAsBoolean(extensions, "MANDATE_AUTO_UPLOAD", false);
		PRESENTMENT_AUTO_DOWNLOAD = getValueAsBoolean(extensions, "PRESENTMENT_AUTO_DOWNLOAD", false);
		PRESENTMENT_AUTO_UPLOAD = getValueAsBoolean(extensions, "PRESENTMENT_AUTO_UPLOAD", false);
		VALIDATE_BENFICIARY_ACCOUNT = getValueAsBoolean(extensions, "VALIDATE_BENFICIARY_ACCOUNT", false);
		AUTO_EOD_REQUIRED = getValueAsBoolean(extensions, "AUTO_EOD_REQUIRED", false);
		ALW_ADV_INTEMI_ADVICE_CREATION = getValueAsBoolean(extensions, "ALW_ADV_INTEMI_ADVICE_CREATION", false);
		TAX_DFT_CR_INV_REQ = getValueAsBoolean(extensions, "TAX_DFT_CR_INV_REQ", false);
		ALLOW_IND_AS = getValueAsBoolean(extensions, "ALLOW_IND_AS", false);
		IND_AS_ACCOUNTING_REQ = getValueAsBoolean(extensions, "IND_AS_ACCOUNTING_REQ", false);
		ALLOW_AUTO_KNOCK_OFF = getValueAsBoolean(extensions, "ALLOW_AUTO_KNOCK_OFF", false);
		ALLOW_OLDEST_DUE = getValueAsBoolean(extensions, "ALLOW_OLDEST_DUE", true);
		ALLOW_DSF_CASHCLT = getValueAsBoolean(extensions, "ALLOW_DSF_CASHCLT", false);
		ALLOW_ADV_INT = getValueAsBoolean(extensions, "ALLOW_ADV_INT", false);
		ALLOW_ADV_EMI = getValueAsBoolean(extensions, "ALLOW_ADV_EMI", false);
		ALLOW_TDS_ON_FEE = getValueAsBoolean(extensions, "ALLOW_TDS_ON_FEE", false);
		ALLOW_NPA_PROVISION = getValueAsBoolean(extensions, "ALLOW_NPA_PROVISION", false);
		ALLOW_NPA_PROVISION = getValueAsBoolean(extensions, "ALLOW_NPA_PROVISION", false);
		ALLOW_UNACCURED_PENALITY_SOA = getValueAsBoolean(extensions, "ALLOW_UNACCURED_PENALITY_SOA", true);
		ALLOW_AUTO_GRACE_EXT = getValueAsBoolean(extensions, "ALLOW_AUTO_GRACE_EXT", false);
		ALLOW_LOAN_DOWNSIZING = getValueAsBoolean(extensions, "ALLOW_LOAN_DOWNSIZING", false);
		ALLOW_RESTRUCTURING = getValueAsBoolean(extensions, "ALLOW_RESTRUCTURING", false);
		LOAN_DOWNSIZING_ACCOUNTING_REQ = getValueAsBoolean(extensions, "LOAN_DOWNSIZING_ACCOUNTING_REQ", false);

		// FROM HL
		ALLOW_LOAN_SPLIT = getValueAsBoolean(extensions, "ALLOW_LOAN_SPLIT", false);
		ALLOW_INST_BASED_SCHD = getValueAsBoolean(extensions, "ALLOW_INST_BASED_SCHD", false);

		SHOW_CUSTOM_BLACKLIST_FIELDS = getValueAsBoolean(extensions, "SHOW_CUSTOM_BLACKLIST_FIELDS", false);
		DSA_CODE_READONLY_FIELD = getValueAsBoolean(extensions, "DSA_CODE_READONLY_FIELD", false);
		COAPP_PANNUMBER_NON_MANDATORY = getValueAsBoolean(extensions, "COAPP_PANNUMBER_NON_MANDATORY", false);
		GENERATECIBIL_BTN_MANDATORY = getValueAsBoolean(extensions, "GENERATECIBIL_BTN_MANDATORY", false);
		ALLOW_ALL_SERV_RCDS = getValueAsBoolean(extensions, "ALLOW_ALL_SERV_RCDS", false);
		TV_FINALVAL_AMOUNT_VALD = getValueAsBoolean(extensions, "TV_FINALVAL_AMOUNT_VALD", false);
		FEE_CAL_ON_RULE = getValueAsBoolean(extensions, "FEE_CAL_ON_RULE", false);
		CUST_ADDR_AUTO_FILL = getValueAsBoolean(extensions, "CUST_ADDR_AUTO_FILL", true);
		SHOW_CUST_EMP_DETAILS = getValueAsBoolean(extensions, "SHOW_CUST_EMP_DETAILS", true);
		SHOW_CUST_SHARE_HOLDER_DETAILS = getValueAsBoolean(extensions, "SHOW_CUST_SHARE_HOLDER_DETAILS", true);
		ALLOW_SIMILARITY = getValueAsBoolean(extensions, "ALLOW_SIMILARITY", false);

		SOA_SHOW_UNACCURED_PENALITY = getValueAsBoolean(extensions, "SOA_SHOW_UNACCURED_PENALITY", true); // Default value should be "true"
		GROUP_BATCH_BY_PARTNERBANK = getValueAsBoolean(extensions, "GROUP_BATCH_BY_PARTNERBANK", false);
		CUST_EMP_TYPE_MANDATORY = getValueAsBoolean(extensions, "CUST_EMP_TYPE_MANDATORY", false);
		DEDUP_BLACKLIST_COAPP = getValueAsBoolean(extensions, "DEDUP_BLACKLIST_COAPP", false);
		POPULATE_DFT_INCOME_DETAILS = getValueAsBoolean(extensions, "POPULATE_DFT_INCOME_DETAILS", false);
		CUST_MOB_MANDATORY = getValueAsBoolean(extensions, "CUST_MOB_MANDATORY", true);

		NOC_GENERATION_MULTIPLE = getValueAsBoolean(extensions, "NOC_GENERATION_MULTIPLE", false);
		NOC_LINKED_LOANS_CHECK_REQ = getValueAsBoolean(extensions, "NOC_LINKED_LOANS_CHECK_REQ", false);

		ACCOUNTING_VALIDATION = getValueAsBoolean(extensions, "ACCOUNTING_VALIDATION", false);
		RCU_DOC_FIELDS_DISABLED = getValueAsBoolean(extensions, "RCU_DOC_FIELDS_DISABLED", false);
		COLLATERAL_DEDUP_WARNING = getValueAsBoolean(extensions, "COLLATERAL_DEDUP_WARNING", false);

		CUSTOMIZED_TEMPLATES = getValueAsBoolean(extensions, "CUSTOMIZED_TEMPLATES", false);
		DERIVED_EMI_REQ = getValueAsBoolean(extensions, "DERIVED_EMI_REQ", false);
		IS_DATA_SYNC_REQ_BY_APP_DATE = getValueAsBoolean(extensions, "IS_DATA_SYNC_REQ_BY_APP_DATE", false);
		CUSTOM_BLACKLIST_PARAMS = getValueAsBoolean(extensions, "CUSTOM_BLACKLIST_PARAMS", false);
		MANDATE_ALLOW_CO_APP = getValueAsBoolean(extensions, "MANDATE_ALLOW_CO_APP", false);
		DISBURSEMENT_ALLOW_CO_APP = getValueAsBoolean(extensions, "DISBURSEMENT_ALLOW_CO_APP", false);
		CHEQUE_ALLOW_CO_APP = getValueAsBoolean(extensions, "CHEQUE_ALLOW_CO_APP", false);
		PERC_REQ_FOR_FINTYPE_FEE = getValueAsBoolean(extensions, "PERC_REQ_FOR_FINTYPE_FEE", false);
		ALW_VERIFICATION_SYNC = getValueAsBoolean(extensions, "ALW_VERIFICATION_SYNC", false);
		SOA_SHOW_UNACCURED_PENALITY = getValueAsBoolean(extensions, "SOA_SHOW_UNACCURED_PENALITY", true); // Default value should be "true"
		GROUP_BATCH_BY_PARTNERBANK = getValueAsBoolean(extensions, "GROUP_BATCH_BY_PARTNERBANK", false);
		ALLOW_EOD_INTERVAL_VALIDATION = getValueAsBoolean(extensions, "ALLOW_EOD_INTERVAL_VALIDATION", false);
		DEFAULT_VAS_MODE_OF_PAYMENT = getValueAsBoolean(extensions, "DEFAULT_VAS_MODE_OF_PAYMENT", false);
		PRESENTMENT_EXTRACT_DEALER_MAN = getValueAsBoolean(extensions, "PRESENTMENT_EXTRACT_DEALER_MAN", true);
		ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE = getValueAsBoolean(extensions,
				"ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE", false);
		DEFAULT_VAS_MODE_OF_PAYMENT = getValueAsBoolean(extensions, "DEFAULT_VAS_MODE_OF_PAYMENT", false);
		SUSP_CHECK_REQ = getValueAsBoolean(extensions, "SUSP_CHECK_REQ", false);
		VER_INITATE_REMARKS_MANDATORY = getValueAsBoolean(extensions, "VER_INITATE_REMARKS_MANDATORY", false);
		ALLOW_NEGATIVE_VALUES_EXTFIELDS = getValueAsBoolean(extensions, "ALLOW_NEGATIVE_VALUES_EXTFIELDS", false);
		FINREFERENCE_ALW_SWIFT_CODE = getValueAsBoolean(extensions, "FINREFERENCE_ALW_SWIFT_CODE", false);
		DEFAULT_PRESENTMENT_UPLOAD = getValueAsBoolean(extensions, "DEFAULT_PRESENTMENT_UPLOAD", true);
		DISB_PAID_CANCELLATION_REQ = getValueAsBoolean(extensions, "DISB_PAID_CANCELLATION_REQ", false);
		MANDATE_PTNRBNK_IN_DWNLD = getValueAsBoolean(extensions, "MANDATE_PTNRBNK_IN_DWNLD", false);
		PRESENTMENT_STAGE_ACCOUNTING_REQ = getValueAsBoolean(extensions, "PRESENTMENT_STAGE_ACCOUNTING_REQ", false);
		ALLOW_LOAN_VAS_RATIO_CALC = getValueAsBoolean(extensions, "ALLOW_LOAN_VAS_RATIO_CALC", false);
		ALLOW_ED_FIELDS_IN_NPA = getValueAsBoolean(extensions, "ALLOW_ED_FIELDS_IN_NPA", false);
		FRQ_15DAYS_REQ = getValueAsBoolean(extensions, "FRQ_15DAYS_REQ", false);
		NON_FRQ_CAPITALISATION = getValueAsBoolean(extensions, "NON_FRQ_CAPITALISATION", false);
		ALW_DOWNPAY_IN_LOANENQ_AND_SOA = getValueAsBoolean(extensions, "ALW_DOWNPAY_IN_LOANENQ_AND_SOA", false);
		/*
		 * ALLOW_FI_INITIATION_LOS = getValueAsBoolean(extensions, "ALLOW_FI_INITIATION_LOS", true);
		 * ALLOW_TV_INITIATION_LOS = getValueAsBoolean(extensions, "ALLOW_TV_INITIATION_LOS", true);
		 * ALLOW_LV_INITIATION_LOS = getValueAsBoolean(extensions, "ALLOW_LV_INITIATION_LOS", true);
		 * ALLOW_RCU_INITIATION_LOS = getValueAsBoolean(extensions, "ALLOW_RCU_INITIATION_LOS", true);
		 */

		REPAY_HIERARCHY_METHOD = getValueAsString(extensions, "REPAY_HIERARCHY_METHOD", "FCIP");
		REPAY_INTEREST_HIERARCHY = getValueAsString(extensions, "REPAY_INTEREST_HIERARCHY", "LI");
		CLIENT_AIB = getValueAsString(extensions, "CLIENT_AIB", "AIB");
		CLIENT_AHB = getValueAsString(extensions, "CLIENT_AHB", "AHB");
		CLIENT_BFL = getValueAsString(extensions, "CLIENT_BFL", "BFL");
		CLIENT_NAME = getValueAsString(extensions, "CLIENT_NAME", "BFL");
		NBFC = getValueAsString(extensions, "NBFC", "NBFC");
		BANK = getValueAsString(extensions, "BANK", "BANK");
		CLIENTTYPE = getValueAsString(extensions, "CLIENTTYPE", "NBFC");
		COLLATERAL_ADJ = getValueAsString(extensions, "COLLATERAL_ADJ", "NO_ADJ");
		LPP_GST_DUE_ON = getValueAsString(extensions, "LPP_GST_DUE_ON", "A");
		GST_SCHD_CAL_ON = getValueAsString(extensions, "GST_SCHD_CAL_ON", "I");
		ALLOW_AMOIUNT_INTEGRAL_PART = getValueAsString(extensions, "ALLOW_AMOIUNT_INTEGRAL_PART", "Y");

		BASE_CCY = getValueAsString(extensions, "BASE_CCY", "INR");
		PRESENTMENT_EXPORT_STATUS_MIN_LENGTH = getValueAsInt(extensions, "PRESENTMENT_EXPORT_STATUS_MIN_LENGTH", 1);
		PRESENTMENT_EXPORT_STATUS_MAX_LENGTH = getValueAsInt(extensions, "PRESENTMENT_EXPORT_STATUS_MAX_LENGTH", 1);
		BASE_CCY_EDT_FIELD = getValueAsInt(extensions, "BASE_CCY_EDT_FIELD", 2);
		// FIXME HL >>

		//<<

		VAS_INST_EDITABLE = getValueAsBoolean(extensions, "VAS_INST_EDITABLE", false);
		ALLOW_DISB_ENQUIRY = getValueAsBoolean(extensions, "ALLOW_DISB_ENQUIRY", false);
		ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE = getValueAsBoolean(extensions,
				"ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE", false);
		CUSTOM_EXT_LIABILITIES = getValueAsBoolean(extensions, "CUSTOM_EXT_LIABILITIES", false);
		DISB_REVERSAL_REQ_BEFORE_LOAN_CANCEL = getValueAsBoolean(extensions, "DISB_REVERSAL_REQ_IN_LOAN_CANCEL", false);
		UPDATE_METADATA_IN_DMS = getValueAsBoolean(extensions, "UPDATE_METADATA_IN_DMS", false);
		CHEQUENO_MANDATORY_DISB_INS = getValueAsBoolean(extensions, "CHEQUENO_MANDATORY_DISB_INS", false);
		ALW_QDP_CUSTOMIZATION = getValueAsBoolean(extensions, "ALW_QDP_CUSTOMIZATION", false);
		ALLOW_OLDEST_DUE = getValueAsBoolean(extensions, "ALLOW_OLDEST_DUE", false);
		CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER = getValueAsBoolean(extensions, "CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER",
				true);

		ALLOW_PMAY = getValueAsBoolean(extensions, "ALLOW_PMAY", false);
		ALLOW_OCR = getValueAsBoolean(extensions, "ALLOW_OCR", false);

		setVerificationConstants(extensions);

		ADVANCE_PAYMENT_INT = getValueAsBoolean(extensions, "ADVANCE_PAYMENT_INT", false);
		ADVANCE_PAYMENT_EMI = getValueAsBoolean(extensions, "ADVANCE_PAYMENT_EMI", false);
		COVENANT_MODULE_NEW = getValueAsBoolean(extensions, "COVENANT_MODULE_NEW", true);
		ADV_EMI_STAGE_FRONT_END = getValueAsBoolean(extensions, "ADV_EMI_STAGE_FRONT_END", true);
		ADV_EMI_STAGE_REAR_END = getValueAsBoolean(extensions, "ADV_EMI_STAGE_REAR_END", false);
		ADV_EMI_STAGE_REPAY_TERMS = getValueAsBoolean(extensions, "ADV_EMI_STAGE_REPAY_TERMS", false);

		AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT = getValueAsBoolean(extensions, "AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT", false);
		RECEIPTS_SHOW_ACCOUNTING_TAB = getValueAsBoolean(extensions, "RECEIPTS_SHOW_ACCOUNTING_TAB", true);
		BRANCHWISE_RCU_INITIATION = getValueAsBoolean(extensions, "BRANCHWISE_RCU_INITIATION", false);
		ALLOW_NON_LAN_RECEIPTS = getValueAsBoolean(extensions, "ALLOW_NON_LAN_RECEIPTS", false);
		ALLOW_BUILDER_BENEFICIARY_DETAILS = getValueAsBoolean(extensions, "ALLOW_BUILDER_BENEFICIARY_DETAILS", false);
		ALLOW_AUTO_KNOCK_OFF = getValueAsBoolean(extensions, "ALLOW_AUTO_KNOCK_OFF", false);
		SOA_SHOW_UNACCURED_PENALITY = getValueAsBoolean(extensions, "SOA_SHOW_UNACCURED_PENALITY", true);
		COVENANT_ADTNL_REMARKS = getValueAsBoolean(extensions, "COVENANT_ADTNL_REMARKS", false);
		PRESEMENT_STOP_RECEIPTS_ON_EOD = getValueAsBoolean(extensions, "PRESEMENT_STOP_RECEIPTS_ON_EOD", false);
		HOLD_DISB_INST_POST = getValueAsBoolean(extensions, "HOLD_DISB_INST_POST", false);
		VAS_INST_ON_DISB = getValueAsBoolean(extensions, "VAS_INST_ON_DISB", false);

	}

	public static boolean IMPLEMENTATION_CONVENTIONAL;
	public static boolean IMPLEMENTATION_ISLAMIC;
	public static boolean ALLOW_FINACTYPES;
	public static boolean ALLOW_CAPITALIZE;
	public static boolean LIMIT_INTERNAL;
	public static boolean ONLINE_IRL_CHECK;
	public static boolean ALLOW_MULTIPLE_EMPLOYMENTS;
	public static boolean ALLOW_CUSTOMER_MAINTENANCE;
	public static boolean ALLOW_CUSTOMER_RATINGS;
	public static boolean ALLOW_CUSTOMER_INCOMES;
	public static boolean ALLOW_CUSTOMER_SHAREHOLDERS;
	public static boolean ALLOW_COLLATERAL_VALUE_UPDATION;
	public static boolean INDIAN_IMPLEMENTATION;
	public static boolean VALIDATE_CORE_CUST_UPDATE;
	public static boolean ALLOW_COVENANT_TYPES;
	public static boolean COLLATERAL_INTERNAL;
	public static boolean ALLOW_VAS;
	public static boolean LP_MARK_FIRSTDAY;
	public static boolean LPP_CALC_SOD;
	public static boolean AUTO_ALLOWED;
	public static boolean DDA_ALLOWED;
	public static boolean DDM_ALLOWED;
	public static boolean ECS_ALLOWED;
	public static boolean NACH_ALLOWED;
	public static boolean PDC_ALLOWED;
	public static boolean PREAPPROVAL_ALLOWED;
	public static boolean ALLOW_DEVIATIONS;
	public static boolean LATEPAY_PROFIT_CAL_ON_DAYZERO;
	public static boolean REBATE_CAPPED_BY_FINANCE;
	public static boolean ALLOW_EXPENSE_TRACKING;
	public static boolean ADD_FEEINFTV_ONCALC;
	public static boolean ALLOW_FIN_SALARY_PAYMENT;
	public static boolean ALLOW_SPECIALRATE;
	public static boolean ACCOUNTS_APPLICABLE;
	public static boolean ALLOW_MANUAL_SCHEDULE;
	public static boolean ALLOW_INDICATIVE_RATE;
	public static boolean CAPTURE_APPLICATION_NUMBER;
	public static boolean ALLOW_PLANNED_EMIHOLIDAY;
	public static boolean ALLOW_UNPLANNED_EMIHOLIDAY;
	public static boolean ALLOW_REAGE;
	public static boolean ALLOW_DOWNPAY_SUPPORTPGM;
	public static boolean ALLOW_BPI_TREATMENT;
	public static boolean INTERESTON_PASTDUE_PRINCIPAL;
	public static boolean ALLOW_PLANNED_DEFERMENTS;
	public static boolean ALLOW_PRICINGPOLICY;
	public static boolean ALLOW_CREDITBUREAU;
	public static boolean ALLOW_BUNDLEDPRODUCT;
	public static boolean ALLOW_COMMITMENT;
	public static boolean ALLOW_PFTUNCHG;
	public static boolean COLLATERAL_DELINK_AUTO;
	public static boolean CLIENT_NFL;
	public static boolean ALLOW_ADDRESSTYPE_PRIORITY;
	public static boolean ALLOW_EMIALTYPE_PRIORITY;
	public static boolean ALLOW_PHONETYPE_PRIORITY;
	public static boolean ALLOW_DEPRECIATION;
	public static boolean EARLYPAY_ADJ_PRI;
	public static boolean ALLOW_INSURANCE;
	public static boolean ALLOW_RIA;
	public static boolean ALLOW_ADDDBSF;
	public static boolean UPFRONT_ADJUST_PAYABLEADVISE;
	public static boolean CO_APP_ENQ_SAME_AS_CUST_ENQ;
	public static boolean PAN_DUPLICATE_NOT_ALLOWED;
	public static boolean ALLOW_AUTO_DISBURSEMENTS;
	public static boolean VARTUAL_DPD;
	public static boolean ALLOW_COSTOFFUNDS;
	public static boolean ALLOW_IRRCODES;
	public static boolean ALLOW_FEES_RECALCULATE;
	public static boolean ALLOW_PAID_FEE_SCHEDULE_METHOD;
	public static boolean ALLOW_BARCODE;
	public static boolean PPPERCENT_VALIDATION_REQ;
	public static boolean DEPOSIT_PROC_REQ;
	public static boolean ENTITY_REQ_TRAIL_BAL;
	public static boolean ALW_LPP_RULE_FIXED;
	public static boolean ALW_LOAN_AUTO_CANCEL;
	public static boolean DFT_CPZ_RESET_ON_RECAL_LOCK;
	public static boolean ALW_LPP_MIN_CAP_AMT;
	public static boolean SEND_NOTIFICATION_ON_CREATE_LOAN_API;
	public static boolean ALW_APPROVED_MANDATE_IN_ORG;
	public static boolean ALW_PROFIT_SCHD_INVOICE;
	public static boolean NEGATE_SIGN_TB;
	public static boolean ALLOW_ACCESS_CONTROL_TYPE;
	public static boolean APPLY_FDDLOCKPERIOD_AFTERGRACE;
	public static boolean ALW_FLEXI;
	public static boolean ALW_SUBVENSION;
	public static boolean ALLOW_ADVEMI_FREQUENCY;
	public static boolean ALLOW_ADVINT_FREQUENCY;
	public static boolean RCVADV_CREATE_ON_INTEMI;
	public static boolean PYBADV_CREATE_ON_INTEMI;
	public static boolean COVENANT_REQUIRED;
	public static boolean QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES;
	public static boolean UPFRONT_FEE_REVERSAL_REQ;
	public static boolean GAP_INTEREST_REQUIRED;
	public static boolean ALLOW_ALL_SERV_RCDS;
	public static boolean LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS;
	public static boolean ENTITYCODE_REQ_FOR_MANDATE_PROCESS;
	public static boolean DISBURSEMENT_AUTO_DOWNLOAD;
	public static boolean DISBURSEMENT_AUTO_UPLOAD;
	public static boolean MANDATE_AUTO_DOWNLOAD;
	public static boolean MANDATE_AUTO_UPLOAD;
	public static boolean PRESENTMENT_AUTO_DOWNLOAD;
	public static boolean PRESENTMENT_AUTO_UPLOAD;
	public static boolean VALIDATE_BENFICIARY_ACCOUNT;
	public static boolean AUTO_EOD_REQUIRED;
	public static boolean ALW_ADV_INTEMI_ADVICE_CREATION;
	public static boolean TAX_DFT_CR_INV_REQ = false;
	public static boolean ALLOW_IND_AS;
	public static boolean IND_AS_ACCOUNTING_REQ;
	public static boolean ALLOW_AUTO_KNOCK_OFF;
	public static boolean ALLOW_OLDEST_DUE;
	public static boolean ALLOW_DSF_CASHCLT;
	public static boolean ALLOW_ADV_INT;
	public static boolean ALLOW_ADV_EMI;
	public static boolean ALLOW_TDS_ON_FEE;
	public static boolean ALLOW_CD_LOANS;
	public static boolean ALLOW_OD_LOANS;
	public static boolean ALLOW_SAMPLING;
	public static boolean ALLOW_SCHOOL_ORG;
	public static boolean ALLOW_FDD_ON_RVW_DATE;
	public static boolean SHOW_CUSTOM_BLACKLIST_FIELDS;
	public static boolean DSA_CODE_READONLY_FIELD;
	public static boolean COAPP_PANNUMBER_NON_MANDATORY;
	public static boolean GENERATECIBIL_BTN_MANDATORY;
	public static boolean TV_FINALVAL_AMOUNT_VALD;

	// FIXME>>HL >>
	public static boolean ALLOW_LOAN_SPLIT;
	public static boolean ALLOW_INST_BASED_SCHD;

	public static boolean FEE_CAL_ON_RULE;
	public static boolean SHOW_CUST_EMP_DETAILS;
	public static boolean SHOW_CUST_SHARE_HOLDER_DETAILS;
	/* Flag to allow similarity to check the % patch match of given string values */
	public static boolean ALLOW_SIMILARITY;

	//FIXME>>HL >>

	public static boolean ALLOW_NPA_PROVISION;
	public static boolean ALLOW_UNACCURED_PENALITY_SOA;
	public static boolean SOA_SHOW_UNACCURED_PENALITY;
	public static boolean GROUP_BATCH_BY_PARTNERBANK; // this field is true for Veritas
	public static boolean CUST_EMP_TYPE_MANDATORY;
	public static boolean DEDUP_BLACKLIST_COAPP;
	public static boolean POPULATE_DFT_INCOME_DETAILS;
	public static boolean CUST_MOB_MANDATORY;
	public static boolean NOC_LINKED_LOANS_CHECK_REQ;
	public static boolean ACCOUNTING_VALIDATION;
	public static boolean RCU_DOC_FIELDS_DISABLED;
	public static boolean COLLATERAL_DEDUP_WARNING;
	public static boolean NOC_GENERATION_MULTIPLE;
	public static boolean CUSTOMIZED_TEMPLATES;
	public static boolean DERIVED_EMI_REQ;
	public static boolean IS_DATA_SYNC_REQ_BY_APP_DATE;
	public static boolean CUSTOM_BLACKLIST_PARAMS;
	public static boolean MANDATE_ALLOW_CO_APP;
	public static boolean DISBURSEMENT_ALLOW_CO_APP;
	public static boolean CHEQUE_ALLOW_CO_APP;
	public static boolean PERC_REQ_FOR_FINTYPE_FEE;
	public static boolean ALW_VERIFICATION_SYNC;
	public static boolean ALLOW_AUTO_GRACE_EXT;
	public static boolean ALLOW_LOAN_DOWNSIZING;
	public static boolean ALLOW_RESTRUCTURING;
	public static boolean LOAN_DOWNSIZING_ACCOUNTING_REQ;
	public static boolean DEFAULT_VAS_MODE_OF_PAYMENT;
	public static boolean PRESENTMENT_EXTRACT_DEALER_MAN;
	public static boolean ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE;
	public static boolean VER_INITATE_REMARKS_MANDATORY;
	public static boolean ALLOW_NEGATIVE_VALUES_EXTFIELDS;
	public static boolean FINREFERENCE_ALW_SWIFT_CODE;
	public static boolean DEFAULT_PRESENTMENT_UPLOAD;

	//Currency constants for Performance
	public static boolean ALLOW_MULTI_CCY;
	public static String BASE_CCY;
	public static int BASE_CCY_EDT_FIELD;
	public static boolean SUSP_CHECK_REQ;
	/*
	 * public static boolean ALLOW_FI_INITIATION_LOS; public static boolean ALLOW_TV_INITIATION_LOS; public static
	 * boolean ALLOW_LV_INITIATION_LOS; public static boolean ALLOW_RCU_INITIATION_LOS;
	 */
	public static boolean ALLOW_EOD_INTERVAL_VALIDATION;
	public static boolean DISB_PAID_CANCELLATION_REQ;
	public static boolean MANDATE_PTNRBNK_IN_DWNLD;
	public static boolean ALLOW_LOAN_VAS_RATIO_CALC;
	public static boolean ALLOW_ED_FIELDS_IN_NPA;
	public static boolean PRESENTMENT_STAGE_ACCOUNTING_REQ;
	public static boolean FRQ_15DAYS_REQ;
	public static boolean NON_FRQ_CAPITALISATION;
	public static boolean ALW_DOWNPAY_IN_LOANENQ_AND_SOA;

	public static String REPAY_HIERARCHY_METHOD;
	public static String REPAY_INTEREST_HIERARCHY;
	public static String CLIENT_AIB;
	public static String CLIENT_AHB;
	public static String CLIENT_BFL;
	public static String CLIENT_NAME;
	public static String NBFC;
	public static String BANK;
	public static String CLIENTTYPE;
	public static String COLLATERAL_ADJ;

	public static int PRESENTMENT_EXPORT_STATUS_MIN_LENGTH;
	public static int PRESENTMENT_EXPORT_STATUS_MAX_LENGTH;
	public static boolean AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT;
	public static boolean RECEIPTS_SHOW_ACCOUNTING_TAB;

	/**
	 * GST Invoice Due basis/Receipt Basis
	 * <p>
	 * If Due Basis creation , means LPP created as fixed amount and same should be accrued on creation date then
	 * PARAMTER = "D"
	 * <p>
	 * If Accrual Basis Creation, means LPP is calculated on daily basis then Accrual postings happen on Month End --
	 * PARAMTER = "A"
	 */
	public static String LPP_GST_DUE_ON = "A";

	public static String GST_SCHD_CAL_ON = "I";
	public static String ALLOW_AMOIUNT_INTEGRAL_PART;
	//flag to allow VAS disbursement instruction editable or not
	public static boolean VAS_INST_EDITABLE;
	public static boolean ALLOW_DISB_ENQUIRY;
	public static boolean ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE;
	public static boolean CUSTOM_EXT_LIABILITIES;
	public static boolean DISB_REVERSAL_REQ_BEFORE_LOAN_CANCEL;
	public static boolean UPDATE_METADATA_IN_DMS;
	public static boolean CHEQUENO_MANDATORY_DISB_INS;
	public static boolean ALW_QDP_CUSTOMIZATION;
	//Update the Manual Cheque Receipt status as paid at Deposit approver
	public static boolean CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER;
	public static boolean BRANCHWISE_RCU_INITIATION;
	public static boolean ALLOW_NON_LAN_RECEIPTS;
	public static boolean ALLOW_BUILDER_BENEFICIARY_DETAILS;
	public static boolean COVENANT_ADTNL_REMARKS;
	public static boolean PRESEMENT_STOP_RECEIPTS_ON_EOD;
	public static boolean HOLD_DISB_INST_POST;
	public static boolean VAS_INST_ON_DISB;

	private static Map<String, Object> getFeatureExtensions() {
		IFeatureExtension featureExtension;
		try {
			Object object = Class.forName("com.pennanttech.extension.implementation.FeatureExtension").newInstance();
			if (object != null) {
				featureExtension = (IFeatureExtension) object;
				return featureExtension.getCustomConstants();
			} else {
				throw new FactoryException(
						"The IFeature implimentation should be available in the client exetension layer to override the implimentation constants.");
			}
		} catch (Exception e) {
			throw new FactoryException(
					"The IFeature implimentation should be available in the client exetension layer to override the implimentation constants.");

		}

	}

	/**
	 * Returns the value as boolean from extended constants to which the specified key is mapped, or defaultValue if the
	 * extended constants contain no mapping for the key.
	 * 
	 * @param extendedConstants
	 *            The constants specified in the extension layer.
	 * @param key
	 *            The key whose associated value is to be returned.
	 * @param defaultValue
	 *            The default value that has to be used if the extended constants contain no mapping for the key.
	 * @return the value as boolean from extended constants to which the specified key is mapped, or defaultValue if
	 *         this map contain no mapping for the key.
	 */
	private static boolean getValueAsBoolean(Map<String, Object> extendedConstants, String key, boolean defaultValue) {
		try {
			return (boolean) extendedConstants.computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Returns the value as String from extended constants to which the specified key is mapped, or defaultValue if the
	 * extended constants contain no mapping for the key.
	 * 
	 * @param extendedConstants
	 *            The constants specified in the extension layer.
	 * @param key
	 *            The key whose associated value is to be returned.
	 * @param defaultValue
	 *            The default value that has to be used if the extended constants contain no mapping for the key.
	 * @return the value as String from extended constants to which the specified key is mapped, or defaultValue if this
	 *         map contain no mapping for the key.
	 */
	private static String getValueAsString(Map<String, Object> extendedConstants, String key, String defaultValue) {
		try {
			return (String) extendedConstants.computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	private static int getValueAsInt(Map<String, Object> extendedConstants, String key, int defaultValue) {
		try {
			return (int) extendedConstants.computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	// FIXME MURTHT
	public static final boolean OLD_FINANCIALS_REQUIRED = false;
	//Old Phone numbers required listbox 
	public static final boolean OLD_PHONENUMBERS_REQUIRED = false;
	// Old Emails required listbox 
	public static final boolean OLD_EMAILS_REQUIRED = false;

	public static final boolean CUST_ADDR_AUTO_FILL;

	public static final boolean LEAD_ID_IS_MANDATORY = true;
	public static final boolean LIST_RENDER_ON_LOAD;

	public static boolean LOAN_ORG_DMS_TAB_REQ = false;
	public static boolean ALLOW_PMAY;
	public static boolean ALLOW_OCR;

	public static boolean ADVANCE_PAYMENT_INT = false;
	public static boolean ADVANCE_PAYMENT_EMI = false;
	public static boolean COVENANT_MODULE_NEW = false;

	/* ADVANCE INT / EMI Constants */
	public static boolean ADV_EMI_STAGE_FRONT_END;
	public static boolean ADV_EMI_STAGE_REAR_END;
	public static boolean ADV_EMI_STAGE_REPAY_TERMS;

	/**
	 * Constants for Verification Module
	 */
	public static boolean VER_INIT_FROM_OUTSIDE;
	public static boolean VER_INIT_AGENT_MANDATORY;
	public static boolean VER_AGENCY_FILTER_BY_CITY;
	public static boolean VER_RCU_DFT_REQ_TYPE_REQUEST;
	public static boolean VER_REASON_CODE_FILTER_BY_REASONTYPE;
	/**
	 * Technical verification collateral ED Address column Name
	 */
	public static String VER_TV_COLL_ED_ADDR_COLUMN;
	/**
	 * Technical verification collateral ED Property valuation column Name
	 */
	public static String VER_TV_COLL_ED_PROP_VAL_COLUMN;

	/**
	 * Technical verification collateral ED Property valuation column Name
	 */
	public static String VER_TV_COLL_ED_PROP_COST_COLUMN;

	public static boolean VER_INITATE_DURING_SAVE;

	public static boolean VER_RCU_INITATE_BY_AGENCY;

	private static void setVerificationConstants(Map<String, Object> extensions) {
		VER_INIT_FROM_OUTSIDE = getValueAsBoolean(extensions, "VER_INIT_FROM_OUTSIDE", false);
		VER_INIT_AGENT_MANDATORY = getValueAsBoolean(extensions, "VER_INIT_AGENT_MANDATORY", false);
		VER_AGENCY_FILTER_BY_CITY = getValueAsBoolean(extensions, "VER_AGENCY_FILTER_BY_CITY", false);

		VER_TV_COLL_ED_ADDR_COLUMN = getValueAsString(extensions, "VER_TV_COLL_ED_ADDR_COLUMN", "PROPERTYCITY"); // HL>>FIXME "CITY" for HL
		VER_TV_COLL_ED_PROP_VAL_COLUMN = getValueAsString(extensions, "VER_TV_COLL_ED_PROP_VAL_COLUMN",
				"TOTALVALUATIONASPE"); // HL>>FIXME "DOCVALUE" for HL
		VER_TV_COLL_ED_PROP_COST_COLUMN = getValueAsString(extensions, "VER_TV_COLL_ED_PROP_COST_COLUMN",
				"COSTOFPROPERTY"); // HL>>FIXME "DOCVALUE" for HL
		VER_RCU_DFT_REQ_TYPE_REQUEST = getValueAsBoolean(extensions, "VER_RCU_DFT_REQ_TYPE_REQUEST", false);
		VER_REASON_CODE_FILTER_BY_REASONTYPE = getValueAsBoolean(extensions, "VER_REASON_CODES_REASONTYPE_FILTER",
				false);
		VER_INITATE_DURING_SAVE = getValueAsBoolean(extensions, "VER_INITATE_DURING_SAVE", true);
		VER_RCU_INITATE_BY_AGENCY = getValueAsBoolean(extensions, "VER_RCU_INITATE_BY_AGENCY", true);
	}

}