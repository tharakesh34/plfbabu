package com.pennant.app.constants;

import org.springframework.stereotype.Component;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;
import com.pennanttech.pff.npa.NpaScope;
import com.pennanttech.pff.provision.ProvisionBook;
import com.pennanttech.pff.provision.ProvisionReversalStage;

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
		// - pff-extension-core (dummy implementation and can be used for testing the constants as well)
		// - pff-extension-client

		ALLOW_MULTI_CCY = getValueAsBoolean("ALLOW_MULTI_CCY", false);
		LIST_RENDER_ON_LOAD = getValueAsBoolean("LIST_RENDER_ON_LOAD", false);
		LIMIT_INTERNAL = getValueAsBoolean("LIMIT_INTERNAL", true);
		ONLINE_IRL_CHECK = getValueAsBoolean("ONLINE_IRL_CHECK", false);
		ALLOW_MULTIPLE_EMPLOYMENTS = getValueAsBoolean("ALLOW_MULTIPLE_EMPLOYMENTS", true);
		ALLOW_CUSTOMER_MAINTENANCE = getValueAsBoolean("ALLOW_CUSTOMER_MAINTENANCE", true);
		ALLOW_CUSTOMER_RATINGS = getValueAsBoolean("ALLOW_CUSTOMER_RATINGS", false);
		ALLOW_CUSTOMER_INCOMES = getValueAsBoolean("ALLOW_CUSTOMER_INCOMES", true);
		ALLOW_CUSTCIF_IN_IMD = getValueAsBoolean("ALLOW_CUSTCIF_IN_IMD", true);
		ALLOW_CUSTOMER_SHAREHOLDERS = getValueAsBoolean("ALLOW_CUSTOMER_SHAREHOLDERS", true);
		ALLOW_COLLATERAL_VALUE_UPDATION = getValueAsBoolean("ALLOW_COLLATERAL_VALUE_UPDATION", false);
		INDIAN_IMPLEMENTATION = getValueAsBoolean("INDIAN_IMPLEMENTATION", true);
		VALIDATE_CORE_CUST_UPDATE = getValueAsBoolean("VALIDATE_CORE_CUST_UPDATE", false);
		ALLOW_COVENANT_TYPES = getValueAsBoolean("ALLOW_COVENANT_TYPES", true);
		COLLATERAL_INTERNAL = getValueAsBoolean("COLLATERAL_INTERNAL", true);
		ALLOW_VAS = getValueAsBoolean("ALLOW_VAS", true);
		LP_MARK_FIRSTDAY = getValueAsBoolean("LP_MARK_FIRSTDAY", true);
		LPP_CALC_SOD = getValueAsBoolean("LPP_CALC_SOD", true);
		AUTO_ALLOWED = getValueAsBoolean("AUTO_ALLOWED", false);
		PREAPPROVAL_ALLOWED = getValueAsBoolean("PREAPPROVAL_ALLOWED", false);
		ALLOW_DEVIATIONS = getValueAsBoolean("ALLOW_DEVIATIONS", true);
		LATEPAY_PROFIT_CAL_ON_DAYZERO = getValueAsBoolean("LATEPAY_PROFIT_CAL_ON_DAYZERO", true);

		ALLOW_SPECIALRATE = getValueAsBoolean("ALLOW_SPECIALRATE", false);
		ALLOW_MANUAL_SCHEDULE = getValueAsBoolean("ALLOW_MANUAL_SCHEDULE", false);
		CAPTURE_APPLICATION_NUMBER = getValueAsBoolean("CAPTURE_APPLICATION_NUMBER", true);
		ALLOW_PLANNED_EMIHOLIDAY = getValueAsBoolean("ALLOW_PLANNED_EMIHOLIDAY", true);
		ALLOW_UNPLANNED_EMIHOLIDAY = getValueAsBoolean("ALLOW_UNPLANNED_EMIHOLIDAY", true);
		ALLOW_REAGE = getValueAsBoolean("ALLOW_REAGE", true);
		ALLOW_BPI_TREATMENT = getValueAsBoolean("ALLOW_BPI_TREATMENT", true);
		INTERESTON_PASTDUE_PRINCIPAL = getValueAsBoolean("INTERESTON_PASTDUE_PRINCIPAL", true);
		ALLOW_PLANNED_DEFERMENTS = getValueAsBoolean("ALLOW_PLANNED_DEFERMENTS", false);
		ALLOW_PRICINGPOLICY = getValueAsBoolean("ALLOW_PRICINGPOLICY", false);
		ALLOW_COMMITMENT = getValueAsBoolean("ALLOW_COMMITMENT", false);
		ALLOW_PFTUNCHG = getValueAsBoolean("ALLOW_PFTUNCHG", false);
		COLLATERAL_DELINK_AUTO = getValueAsBoolean("COLLATERAL_DELINK_AUTO", false);
		CLIENT_NFL = getValueAsBoolean("CLIENT_NFL", false);
		ALLOW_ADDRESSTYPE_PRIORITY = getValueAsBoolean("ALLOW_ADDRESSTYPE_PRIORITY", false);
		ALLOW_EMIALTYPE_PRIORITY = getValueAsBoolean("ALLOW_EMIALTYPE_PRIORITY", false);
		ALLOW_PHONETYPE_PRIORITY = getValueAsBoolean("ALLOW_PHONETYPE_PRIORITY", false);
		EARLYPAY_ADJ_PRI = getValueAsBoolean("EARLYPAY_ADJ_PRI", true);
		ALLOW_RIA = getValueAsBoolean("ALLOW_RIA", false);
		ALLOW_ADDDBSF = getValueAsBoolean("ALLOW_ADDDBSF", false);
		UPFRONT_ADJUST_PAYABLEADVISE = getValueAsBoolean("UPFRONT_ADJUST_PAYABLEADVISE", false);
		CO_APP_ENQ_SAME_AS_CUST_ENQ = getValueAsBoolean("CO_APP_ENQ_SAME_AS_CUST_ENQ", true);
		ALLOW_AUTO_DISBURSEMENTS = getValueAsBoolean("ALLOW_AUTO_DISBURSEMENTS", false);

		ALLOW_COSTOFFUNDS = getValueAsBoolean("ALLOW_COSTOFFUNDS", true);
		ALLOW_IRRCODES = getValueAsBoolean("ALLOW_IRRCODES", true);

		ALLOW_BARCODE = getValueAsBoolean("ALLOW_BARCODE", false);
		PPPERCENT_VALIDATION_REQ = getValueAsBoolean("PPPERCENT_VALIDATION_REQ", false);
		DEPOSIT_PROC_REQ = getValueAsBoolean("DEPOSIT_PROC_REQ", false);
		ENTITY_REQ_TRAIL_BAL = getValueAsBoolean("ENTITY_REQ_TRAIL_BAL", false);
		ALW_LPP_RULE_FIXED = getValueAsBoolean("ALW_LPP_RULE_FIXED", false);
		ALW_LOAN_AUTO_CANCEL = getValueAsBoolean("ALW_LOAN_AUTO_CANCEL", false);
		DFT_CPZ_RESET_ON_RECAL_LOCK = getValueAsBoolean("DFT_CPZ_RESET_ON_RECAL_LOCK", false);
		ALW_LPP_MIN_CAP_AMT = getValueAsBoolean("ALW_LPP_MIN_CAP_AMT", false);
		SEND_NOTIFICATION_ON_CREATE_LOAN_API = getValueAsBoolean("SEND_NOTIFICATION_ON_CREATE_LOAN_API", false);
		ALW_PROFIT_SCHD_INVOICE = getValueAsBoolean("ALW_PROFIT_SCHD_INVOICE", true);
		NEGATE_SIGN_TB = getValueAsBoolean("NEGATE_SIGN_TB", false);
		ALLOW_ACCESS_CONTROL_TYPE = getValueAsBoolean("ALLOW_ACCESS_CONTROL_TYPE", true);
		APPLY_FDDLOCKPERIOD_AFTERGRACE = getValueAsBoolean("APPLY_FDDLOCKPERIOD_AFTERGRACE", false);
		ALW_FLEXI = getValueAsBoolean("ALW_FLEXI", false);
		ALW_SUBVENSION = getValueAsBoolean("ALW_SUBVENSION", false);
		ALLOW_ADVEMI_FREQUENCY = getValueAsBoolean("ALLOW_ADVEMI_FREQUENCY", false);
		ALLOW_ADVINT_FREQUENCY = getValueAsBoolean("ALLOW_ADVINT_FREQUENCY", true);
		RCVADV_CREATE_ON_INTEMI = getValueAsBoolean("RCVADV_CREATE_ON_INTEMI", true);
		PYBADV_CREATE_ON_INTEMI = getValueAsBoolean("PYBADV_CREATE_ON_INTEMI", true);
		COVENANT_REQUIRED = getValueAsBoolean("COVENANT_REQUIRED", false);
		QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES = getValueAsBoolean("QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES", true);

		GAP_INTEREST_REQUIRED = getValueAsBoolean("GAP_INTEREST_REQUIRED", true);
		ALLOW_ALL_SERV_RCDS = getValueAsBoolean("ALLOW_ALL_SERV_RCDS", false);
		LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS = getValueAsBoolean("LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS", false);
		INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS = getValueAsBoolean("INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS",
				true);
		DISBURSEMENT_AUTO_DOWNLOAD = getValueAsBoolean("DISBURSEMENT_AUTO_DOWNLOAD", false);
		DISBURSEMENT_AUTO_UPLOAD = getValueAsBoolean("DISBURSEMENT_AUTO_UPLOAD", false);
		PRESENTMENT_AUTO_DOWNLOAD = getValueAsBoolean("PRESENTMENT_AUTO_DOWNLOAD", false);
		PRESENTMENT_AUTO_UPLOAD = getValueAsBoolean("PRESENTMENT_AUTO_UPLOAD", false);
		VALIDATE_BENFICIARY_ACCOUNT = getValueAsBoolean("VALIDATE_BENFICIARY_ACCOUNT", false);
		AUTO_EOD_REQUIRED = getValueAsBoolean("AUTO_EOD_REQUIRED", false);
		ALW_ADV_INTEMI_ADVICE_CREATION = getValueAsBoolean("ALW_ADV_INTEMI_ADVICE_CREATION", false);
		TAX_DFT_CR_INV_REQ = getValueAsBoolean("TAX_DFT_CR_INV_REQ", false);
		ALLOW_IND_AS = getValueAsBoolean("ALLOW_IND_AS", false);
		IND_AS_ACCOUNTING_REQ = getValueAsBoolean("IND_AS_ACCOUNTING_REQ", false);
		ALLOW_AUTO_KNOCK_OFF = getValueAsBoolean("ALLOW_AUTO_KNOCK_OFF", false);
		ALLOW_OLDEST_DUE = getValueAsBoolean("ALLOW_OLDEST_DUE", true);
		ALLOW_DSF_CASHCLT = getValueAsBoolean("ALLOW_DSF_CASHCLT", false);
		ALLOW_ADV_INT = getValueAsBoolean("ALLOW_ADV_INT", false);
		ALLOW_ADV_EMI = getValueAsBoolean("ALLOW_ADV_EMI", false);
		ALLOW_TDS_ON_FEE = getValueAsBoolean("ALLOW_TDS_ON_FEE", false);
		ALLOW_CD_LOANS = getValueAsBoolean("ALLOW_CD_LOANS", false);
		ALLOW_OD_LOANS = getValueAsBoolean("ALLOW_OD_LOANS", false);
		ALLOW_SAMPLING = getValueAsBoolean("ALLOW_SAMPLING", false);
		ALLOW_SCHOOL_ORG = getValueAsBoolean("ALLOW_SCHOOL_ORG", false);
		ALLOW_FDD_ON_RVW_DATE = getValueAsBoolean("ALLOW_FDD_ON_RVW_DATE", false);
		ALLOW_UNACCURED_PENALITY_SOA = getValueAsBoolean("ALLOW_UNACCURED_PENALITY_SOA", true);
		ALLOW_AUTO_GRACE_EXT = getValueAsBoolean("ALLOW_AUTO_GRACE_EXT", false);
		ALLOW_LOAN_DOWNSIZING = getValueAsBoolean("ALLOW_LOAN_DOWNSIZING", false);
		ALLOW_RESTRUCTURING = getValueAsBoolean("ALLOW_RESTRUCTURING", false);
		LOAN_DOWNSIZING_ACCOUNTING_REQ = getValueAsBoolean("LOAN_DOWNSIZING_ACCOUNTING_REQ", false);

		// FROM HL
		ALLOW_LOAN_SPLIT = getValueAsBoolean("ALLOW_LOAN_SPLIT", false);
		SCHD_INST_CAL_ON_DISB_RELIZATION = getValueAsBoolean("SCHD_INST_CAL_ON_DISB_RELIZATION", false);

		SHOW_CUSTOM_BLACKLIST_FIELDS = getValueAsBoolean("SHOW_CUSTOM_BLACKLIST_FIELDS", false);
		DSA_CODE_READONLY_FIELD = getValueAsBoolean("DSA_CODE_READONLY_FIELD", false);
		COAPP_PANNUMBER_NON_MANDATORY = getValueAsBoolean("COAPP_PANNUMBER_NON_MANDATORY", false);
		GENERATECIBIL_BTN_MANDATORY = getValueAsBoolean("GENERATECIBIL_BTN_MANDATORY", false);
		ALLOW_ALL_SERV_RCDS = getValueAsBoolean("ALLOW_ALL_SERV_RCDS", false);
		TV_FINALVAL_AMOUNT_VALD = getValueAsBoolean("TV_FINALVAL_AMOUNT_VALD", false);
		CUST_ADDR_AUTO_FILL = getValueAsBoolean("CUST_ADDR_AUTO_FILL", true);
		SHOW_CUST_EMP_DETAILS = getValueAsBoolean("SHOW_CUST_EMP_DETAILS", true);
		SHOW_CUST_SHARE_HOLDER_DETAILS = getValueAsBoolean("SHOW_CUST_SHARE_HOLDER_DETAILS", true);
		ALLOW_SIMILARITY = getValueAsBoolean("ALLOW_SIMILARITY", false);

		GROUP_BATCH_BY_PARTNERBANK = getValueAsBoolean("GROUP_BATCH_BY_PARTNERBANK", false);
		CUST_EMP_TYPE_MANDATORY = getValueAsBoolean("CUST_EMP_TYPE_MANDATORY", false);
		DEDUP_BLACKLIST_COAPP = getValueAsBoolean("DEDUP_BLACKLIST_COAPP", false);
		POPULATE_DFT_INCOME_DETAILS = getValueAsBoolean("POPULATE_DFT_INCOME_DETAILS", false);
		CUST_MOB_MANDATORY = getValueAsBoolean("CUST_MOB_MANDATORY", true);

		NOC_GENERATION_MULTIPLE = getValueAsBoolean("NOC_GENERATION_MULTIPLE", false);
		NOC_LINKED_LOANS_CHECK_REQ = getValueAsBoolean("NOC_LINKED_LOANS_CHECK_REQ", false);

		ACCOUNTING_VALIDATION = getValueAsBoolean("ACCOUNTING_VALIDATION", false);
		RCU_DOC_FIELDS_DISABLED = getValueAsBoolean("RCU_DOC_FIELDS_DISABLED", false);

		CUSTOMIZED_TEMPLATES = getValueAsBoolean("CUSTOMIZED_TEMPLATES", false);
		DERIVED_EMI_REQ = getValueAsBoolean("DERIVED_EMI_REQ", false);
		IS_DATA_SYNC_REQ_BY_APP_DATE = getValueAsBoolean("IS_DATA_SYNC_REQ_BY_APP_DATE", false);
		CUSTOM_BLACKLIST_PARAMS = getValueAsBoolean("CUSTOM_BLACKLIST_PARAMS", false);
		DISBURSEMENT_ALLOW_CO_APP = getValueAsBoolean("DISBURSEMENT_ALLOW_CO_APP", false);
		CHEQUE_ALLOW_CO_APP = getValueAsBoolean("CHEQUE_ALLOW_CO_APP", false);
		ALW_VERIFICATION_SYNC = getValueAsBoolean("ALW_VERIFICATION_SYNC", false);
		GROUP_BATCH_BY_PARTNERBANK = getValueAsBoolean("GROUP_BATCH_BY_PARTNERBANK", false);
		ALLOW_ED_FIELDS_IN_NPA = getValueAsBoolean("ALLOW_ED_FIELDS_IN_NPA", false);
		ALLOW_EOD_INTERVAL_VALIDATION = getValueAsBoolean("ALLOW_EOD_INTERVAL_VALIDATION", false);
		DEFAULT_VAS_MODE_OF_PAYMENT = getValueAsBoolean("DEFAULT_VAS_MODE_OF_PAYMENT", false);
		PRESENTMENT_EXTRACT_DEALER_MAN = getValueAsBoolean("PRESENTMENT_EXTRACT_DEALER_MAN", true);
		ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE = getValueAsBoolean("ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE",
				false);
		DEFAULT_VAS_MODE_OF_PAYMENT = getValueAsBoolean("DEFAULT_VAS_MODE_OF_PAYMENT", false);
		SUSP_CHECK_REQ = getValueAsBoolean("SUSP_CHECK_REQ", false);
		VER_INITATE_REMARKS_MANDATORY = getValueAsBoolean("VER_INITATE_REMARKS_MANDATORY", false);
		ALLOW_NEGATIVE_VALUES_EXTFIELDS = getValueAsBoolean("ALLOW_NEGATIVE_VALUES_EXTFIELDS", false);
		FINREFERENCE_ALW_SWIFT_CODE = getValueAsBoolean("FINREFERENCE_ALW_SWIFT_CODE", false);
		DISB_PAID_CANCELLATION_REQ = getValueAsBoolean("DISB_PAID_CANCELLATION_REQ", false);
		PRESENTMENT_STAGE_ACCOUNTING_REQ = getValueAsBoolean("PRESENTMENT_STAGE_ACCOUNTING_REQ", false);
		ALLOW_LOAN_VAS_RATIO_CALC = getValueAsBoolean("ALLOW_LOAN_VAS_RATIO_CALC", false);
		ALLOW_ED_FIELDS_IN_NPA = getValueAsBoolean("ALLOW_ED_FIELDS_IN_NPA", false);
		FRQ_15DAYS_REQ = getValueAsBoolean("FRQ_15DAYS_REQ", false);
		NON_FRQ_CAPITALISATION = getValueAsBoolean("NON_FRQ_CAPITALISATION", false);
		ALW_DOWNPAY_IN_LOANENQ_AND_SOA = getValueAsBoolean("ALW_DOWNPAY_IN_LOANENQ_AND_SOA", false);
		FA_CANCEL_CHEQUE_AUTO_OPEN = getValueAsBoolean("FA_CANCEL_CHEQUE_AUTO_OPEN", false);
		ALLOW_SUBVENTION = getValueAsBoolean("ALLOW_SUBVENTION", false);
		PRESENT_RESP_BOUNCE_REMARKS_MAN = getValueAsBoolean("PRESENT_RESP_BOUNCE_REMARKS_MAN", false);
		VAS_VALIDATION_FOR_PREMIUM_CALC = getValueAsBoolean("VAS_VALIDATION_FOR_PREMIUM_CALC", false);
		SNAP_SHOT_DATE_AS_CUR_BUS_DATE = getValueAsBoolean("SNAP_SHOT_DATE_AS_CUR_BUS_DATE", false);
		// this constant should be true for goderaj
		CHEQUE_AMOUNT_ZERO_UDC = getValueAsBoolean("CHEQUE_AMOUNT_ZERO_UDC", false);

		VALIDATION_ON_CHECKER_APPROVER_ALLOWED = getValueAsBoolean("VALIDATION_ON_CHECKER_APPROVER_ALLOWED", false);
		CIBIL_BASED_ON_ENTITY = getValueAsBoolean("CIBIL_BASED_ON_ENTITY", false);
		RESTRUCTURE_DFT_APP_DATE = getValueAsBoolean("RESTRUCTURE_DFT_APP_DATE", false);
		RESTRUCTURE_DATE_ALW_EDIT = getValueAsBoolean("RESTRUCTURE_DATE_ALW_EDIT", false);
		RESTRUCTURE_RATE_CHG_ALW = getValueAsBoolean("RESTRUCTURE_RATE_CHG_ALW", false);
		RESTRUCTURE_ALW_CHARGES = getValueAsBoolean("RESTRUCTURE_ALW_CHARGES", false);
		PENALTY_CALC_ON_REPRESENTATION = getValueAsBoolean("PENALTY_CALC_ON_REPRESENTATION", false);
		DISB_REQ_RES_FILE_GEN_MODE = getValueAsBoolean("DISB_REQ_RES_FILE_GEN_MODE", false);
		ALLOW_SHADOW_POSTINGS = getValueAsBoolean("ALLOW_SHADOW_POSTINGS", false);
		ALLOW_ESCROW_MODE = getValueAsBoolean("ALLOW_ESCROW_MODE", false);
		/*
		 * ALLOW_FI_INITIATION_LOS = getValueAsBoolean("ALLOW_FI_INITIATION_LOS", true); ALLOW_TV_INITIATION_LOS =
		 * getValueAsBoolean("ALLOW_TV_INITIATION_LOS", true); ALLOW_LV_INITIATION_LOS =
		 * getValueAsBoolean("ALLOW_LV_INITIATION_LOS", true); ALLOW_RCU_INITIATION_LOS =
		 * getValueAsBoolean("ALLOW_RCU_INITIATION_LOS", true);
		 */

		REPAY_HIERARCHY_METHOD = getValueAsString("REPAY_HIERARCHY_METHOD", "FCIP");
		REPAY_INTEREST_HIERARCHY = getValueAsString("REPAY_INTEREST_HIERARCHY", "LI");
		CLIENT_BFL = getValueAsString("CLIENT_BFL", "BFL");
		CLIENT_NAME = getValueAsString("CLIENT_NAME", "BFL");
		NBFC = getValueAsString("NBFC", "NBFC");
		BANK = getValueAsString("BANK", "BANK");
		CLIENTTYPE = getValueAsString("CLIENTTYPE", "NBFC");
		COLLATERAL_ADJ = getValueAsString("COLLATERAL_ADJ", "NO_ADJ");
		LPP_GST_DUE_ON = getValueAsString("LPP_GST_DUE_ON", "A");
		GST_SCHD_CAL_ON = getValueAsString("GST_SCHD_CAL_ON", "I");
		ALLOW_AMOIUNT_INTEGRAL_PART = getValueAsString("ALLOW_AMOIUNT_INTEGRAL_PART", "Y");
		BASE_CCY = getValueAsString("BASE_CCY", "INR");

		PRESENTMENT_EXPORT_STATUS_MIN_LENGTH = getValueAsInt("PRESENTMENT_EXPORT_STATUS_MIN_LENGTH", 1);
		PRESENTMENT_EXPORT_STATUS_MAX_LENGTH = getValueAsInt("PRESENTMENT_EXPORT_STATUS_MAX_LENGTH", 1);
		BASE_CCY_EDT_FIELD = getValueAsInt("BASE_CCY_EDT_FIELD", 2);

		VAS_INST_EDITABLE = getValueAsBoolean("VAS_INST_EDITABLE", false);
		ALLOW_DISB_ENQUIRY = getValueAsBoolean("ALLOW_DISB_ENQUIRY", false);
		ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE = getValueAsBoolean("ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE", false);
		CUSTOM_EXT_LIABILITIES = getValueAsBoolean("CUSTOM_EXT_LIABILITIES", false);
		DISB_REVERSAL_REQ_BEFORE_LOAN_CANCEL = getValueAsBoolean("DISB_REVERSAL_REQ_IN_LOAN_CANCEL", false);
		UPDATE_METADATA_IN_DMS = getValueAsBoolean("UPDATE_METADATA_IN_DMS", false);
		CHEQUENO_MANDATORY_DISB_INS = getValueAsBoolean("CHEQUENO_MANDATORY_DISB_INS", false);
		ALW_QDP_CUSTOMIZATION = getValueAsBoolean("ALW_QDP_CUSTOMIZATION", false);
		ALLOW_OLDEST_DUE = getValueAsBoolean("ALLOW_OLDEST_DUE", false);
		CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER = getValueAsBoolean("CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER", true);
		ALLOW_PMAY = getValueAsBoolean("ALLOW_PMAY", false);
		ALLOW_OCR = getValueAsBoolean("ALLOW_OCR", false);
		ADVANCE_PAYMENT_INT = getValueAsBoolean("ADVANCE_PAYMENT_INT", false);
		ADVANCE_PAYMENT_EMI = getValueAsBoolean("ADVANCE_PAYMENT_EMI", false);
		COVENANT_MODULE_NEW = getValueAsBoolean("COVENANT_MODULE_NEW", true);
		ADV_EMI_STAGE_FRONT_END = getValueAsBoolean("ADV_EMI_STAGE_FRONT_END", true);
		ADV_EMI_STAGE_REAR_END = getValueAsBoolean("ADV_EMI_STAGE_REAR_END", false);
		ADV_EMI_STAGE_REPAY_TERMS = getValueAsBoolean("ADV_EMI_STAGE_REPAY_TERMS", false);
		AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT = getValueAsBoolean("AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT", false);
		BRANCHWISE_RCU_INITIATION = getValueAsBoolean("BRANCHWISE_RCU_INITIATION", false);
		ALLOW_NON_LAN_RECEIPTS = getValueAsBoolean("ALLOW_NON_LAN_RECEIPTS", false);
		ALLOW_BUILDER_BENEFICIARY_DETAILS = getValueAsBoolean("ALLOW_BUILDER_BENEFICIARY_DETAILS", false);
		ALLOW_AUTO_KNOCK_OFF = getValueAsBoolean("ALLOW_AUTO_KNOCK_OFF", false);
		SOA_SHOW_UNACCURED_PENALITY = getValueAsBoolean("SOA_SHOW_UNACCURED_PENALITY", true);
		COVENANT_ADTNL_REMARKS = getValueAsBoolean("COVENANT_ADTNL_REMARKS", false);
		HOLD_DISB_INST_POST = getValueAsBoolean("HOLD_DISB_INST_POST", false);
		VAS_INST_ON_DISB = getValueAsBoolean("VAS_INST_ON_DISB", false);
		FUR_DISBINST_ACC_REQ = getValueAsBoolean("FUR_DISBINST_ACC_REQ", false);
		ALLOW_TDS_PERC_BASED_ON_YEAR = getValueAsBoolean("ALLOW_TDS_PERC_BASED_ON_YEAR", false);
		ALLOW_TDS_CERTIFICATE_ADJUSTMENT = getValueAsBoolean("ALLOW_TDS_CERTIFICATE_ADJUSTMENT", false);
		GST_INCLUSIVE_SYMBOL = getValueAsString("GST_INCLUSIVE_SYMBOL", "");
		GST_EXCLUSIVE_SYMBOL = getValueAsString("GST_EXCLUSIVE_SYMBOL", "");
		ALW_RATE_CHANGE = getValueAsBoolean("ALW_RATE_CHANGE", false);
		IMD_EXT_REFERENCE = getValueAsBoolean("IMD_EXT_REFERENCE", false);
		BOUNCE_CHARGE_ON_DPD_COUNT = getValueAsBoolean("BOUNCE_CHARGE_ON_DPD_COUNT", false);
		RETAIL_CUST_PAN_MANDATORY = getValueAsBoolean("RETAIL_CUST_PAN_MANDATORY", true);
		FINREFERENCE_ALW_FREE_TEXT = getValueAsBoolean("FINREFERENCE_ALW_FREE_TEXT", false);
		ALLOW_ISRA_DETAILS = getValueAsBoolean("ALLOW_ISRA_DETAILS", false);
		ALLOW_DFS_CASH_COLLATERAL_EXCESS_HEADS = getValueAsBoolean("ALLOW_DFS_CASH_COLLATERAL_EXCESS_HEADS", false);
		ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS = getValueAsBoolean("ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS",
				false);
		ALLOW_OD_POSINT_SCHD_METHOD = getValueAsBoolean("Allow_OD_POSINT_SCHD_METHOD", true);
		RECEIPT_DUPLICATE_CHECK_STOP = getValueAsBoolean("RECEIPT_DUPLICATE_CHECK_STOP", false);
		EXTENDEDFIELDS_ORG_WORKFLOW = getValueAsBoolean("EXTENDEDFIELDS_ORG_WORKFLOW", false);
		OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE = getValueAsBoolean("OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE", true);
		MANUAL_ADVISE_FUTURE_DATE = getValueAsBoolean("MANUAL_ADVISE_FUTURE_DATE", true);
		DISB_INST_POST_DWNLD = getValueAsBoolean("DISB_INST_POST_DWNLD", false);
		ACCRUAL_DIFF_ONETIME_POST = getValueAsBoolean("ACCRUAL_DIFF_ONETIME_POST", true);
		ALLOW_GST_DETAILS = getValueAsBoolean("ALLOW_GST_DETAILS", false);
		SAN_BASED_EMI_REQUIRED_STEP = getValueAsBoolean("SAN_BASED_EMI_REQUIRED_STEP", false);
		ALLOW_ZERO_STEP_AMOUNT_PERC = getValueAsBoolean("ALLOW_ZERO_STEP_AMOUNT_PERC", false);
		ALLOW_STEP_RECAL_PRORATA = getValueAsBoolean("ALLOW_STEP_RECAL_PRORATA", false);
		RECEIPT_DUPLICATE_FORMAT = getValueAsString("RECEIPT_DUPLICATE_FORMAT", "TR_VD_AMT");
		CUSTOMER_PAN_VALIDATION_STOP = getValueAsBoolean("CUSTOMER_PAN_VALIDATION_STOP", false);
		ALLOW_NPA = getValueAsBoolean("ALLOW_NPA", false);
		ALLOW_PROVISION = getValueAsBoolean("ALLOW_PROVISION", false);
		NPA_SCOPE = (NpaScope) getValueAsObject("NPA_SCOPE", NpaScope.LOAN);
		PROVISION_REVERSAL_REQ = getValueAsBoolean("PROVISION_REVERSAL_REQ", false);
		PROVISION_BOOKS = (ProvisionBook) getValueAsObject("PROVISION_BOOKS", ProvisionBook.NO_PROVISION);
		PROVISION_REVERSAL_STAGE = (ProvisionReversalStage) getValueAsObject("PROVISION_REVERSAL_STAGE",
				ProvisionReversalStage.SOM);
		ALLOW_EXTENDEDFIELDS_IN_WORKFLOW = getValueAsBoolean("ALLOW_EXTENDEDFIELDS_IN_WORKFLOW", false);
		PROVISION_POSTINGS_REQ = getValueAsBoolean("PROVISION_POSTINGS_REQ", true);
		RECEIPT_ALLOW_FULL_WAIVER_ACKNOWLEDGEMENT = getValueAsBoolean("RECEIPT_ALLOW_FULL_WAIVER_ACKNOWLEDGEMENT",
				true);
		RECEIPT_ALLOW_FULL_WAIVER = getValueAsBoolean("RECEIPT_ALLOW_FULL_WAIVER", false);
		ALLOW_CERSAI = getValueAsBoolean("ALLOW_CERSAI", false);
		DISB_STP = getValueAsBoolean("DISB_STP", false);
		COLLECTION_DOWNLOAD_REQ = getValueAsBoolean("COLLECTION_DOWNLOAD_REQ", true);

		AUTO_WAIVER_REQUIRED_FROMSCREEN = getValueAsBoolean("AUTO_WAIVER_REQUIRED_FROMSCREEN", false);

		LOAN_PURPOSE_MANDATORY = getValueAsBoolean("LOAN_PURPOSE_MANDATORY", false);
		GUARANTOR_EMAIL_MANDATORY = getValueAsBoolean("GUARANTOR_EMAIL_MANDATORY", true);
		ALLOW_IMD_WITHOUT_REALIZED = getValueAsBoolean("ALLOW_IMD_WITHOUT_REALIZED", false);

		SOA_INSTALlEMENT_BIFURCATION = getValueAsBoolean("SOA_INSTALlEMENT_BIFURCATION", false);
		AUTO_KNOCK_OFF_ON_DUE_DATE = getValueAsBoolean("AUTO_KNOCK_OFF_ON_DUE_DATE", true);
		DUE_DATE_RECEIPT_CREATION = getValueAsBoolean("DUE_DATE_RECEIPT_CREATION", true);
		AUTO_EXTRACTION = getValueAsBoolean("AUTO_EXTRACTION", true);
		AUTO_APPROVAL = getValueAsBoolean("AUTO_APPROVAL", false);
		APPROVE_ON_LOAN_ORG = getValueAsBoolean("APPROVE_ON_LOAN_ORG", false);
		UPLOAD_ENITITY_CODE_MANDATORY = getValueAsBoolean("UPLOAD_ENITITY_CODE_MANDATORY", false);
		AUTO_DOWNLOAD = getValueAsBoolean("AUTO_DOWNLOAD", false);
		AUTO_UPLOAD = getValueAsBoolean("AUTO_UPLOAD", false);
		ALLOW_CO_APP = getValueAsBoolean("ALLOW_CO_APP", false);
		PARTNER_BANK_WISE_EXTARCTION = getValueAsBoolean("PARTNER_BANK_WISE_EXTARCTION", false);
		FRQ_DES_CHANGE_ON_EXTRACTION = getValueAsBoolean("FRQ_DES_CHANGE_ON_EXTRACTION", false);
		PARTNER_BANK_REQ = getValueAsBoolean("PARTNER_BANK_REQ", false);
		SWAP_EFFECTIVE_DATE_DEFAULT = getValueAsBoolean("SWAP_EFFECTIVE_DATE_DEFAULT", false);
		ACCOUNT_DETAILS_READONLY = getValueAsBoolean("ACCOUNT_DETAILS_READONLY", false);
		ALLOW_CONSECUTIVE_BOUNCE = getValueAsBoolean("ALLOW_CONSECUTIVE_BOUNCE", false);
		EXPIRY_DATE_MANDATORY = getValueAsBoolean("EXPIRY_DATE_MANDATORY", true);
		ALW_AUTO_CROSS_LOAN_KNOCKOFF = getValueAsBoolean("ALW_AUTO_CROSS_LOAN_KNOCKOFF", false);

		setVerificationConstants();
	}

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
	public static boolean PREAPPROVAL_ALLOWED;
	public static boolean ALLOW_DEVIATIONS;
	public static boolean LATEPAY_PROFIT_CAL_ON_DAYZERO;
	public static boolean ALLOW_SPECIALRATE;
	public static boolean ALLOW_MANUAL_SCHEDULE;
	public static boolean CAPTURE_APPLICATION_NUMBER;
	public static boolean ALLOW_PLANNED_EMIHOLIDAY;
	public static boolean ALLOW_UNPLANNED_EMIHOLIDAY;
	public static boolean ALLOW_REAGE;
	public static boolean ALLOW_BPI_TREATMENT;
	public static boolean INTERESTON_PASTDUE_PRINCIPAL;
	public static boolean ALLOW_PLANNED_DEFERMENTS;
	public static boolean ALLOW_PRICINGPOLICY;
	public static boolean ALLOW_COMMITMENT;
	public static boolean ALLOW_PFTUNCHG;
	public static boolean COLLATERAL_DELINK_AUTO;
	public static boolean CLIENT_NFL;
	public static boolean ALLOW_ADDRESSTYPE_PRIORITY;
	public static boolean ALLOW_EMIALTYPE_PRIORITY;
	public static boolean ALLOW_PHONETYPE_PRIORITY;
	public static boolean EARLYPAY_ADJ_PRI;
	public static boolean ALLOW_RIA;
	public static boolean ALLOW_ADDDBSF;
	public static boolean UPFRONT_ADJUST_PAYABLEADVISE;
	public static boolean CO_APP_ENQ_SAME_AS_CUST_ENQ;
	public static boolean ALLOW_AUTO_DISBURSEMENTS;

	public static boolean ALLOW_COSTOFFUNDS;
	public static boolean ALLOW_IRRCODES;
	public static boolean ALLOW_BARCODE;
	public static boolean PPPERCENT_VALIDATION_REQ;
	public static boolean DEPOSIT_PROC_REQ;
	public static boolean ENTITY_REQ_TRAIL_BAL;
	public static boolean ALW_LPP_RULE_FIXED;
	public static boolean ALW_LOAN_AUTO_CANCEL;
	public static boolean DFT_CPZ_RESET_ON_RECAL_LOCK;
	public static boolean ALW_LPP_MIN_CAP_AMT;
	public static boolean SEND_NOTIFICATION_ON_CREATE_LOAN_API;
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
	public static boolean GAP_INTEREST_REQUIRED;
	public static boolean ALLOW_ALL_SERV_RCDS;
	public static boolean LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS;
	public static boolean INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS;
	public static boolean DISBURSEMENT_AUTO_DOWNLOAD;
	public static boolean DISBURSEMENT_AUTO_UPLOAD;
	public static boolean PRESENTMENT_AUTO_DOWNLOAD;
	public static boolean PRESENTMENT_AUTO_UPLOAD;
	public static boolean VALIDATE_BENFICIARY_ACCOUNT;
	public static boolean AUTO_EOD_REQUIRED;
	public static boolean ALW_ADV_INTEMI_ADVICE_CREATION;
	public static boolean TAX_DFT_CR_INV_REQ;
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
	public static boolean SCHD_INST_CAL_ON_DISB_RELIZATION;

	public static boolean SHOW_CUST_EMP_DETAILS;
	public static boolean SHOW_CUST_SHARE_HOLDER_DETAILS;
	/* Flag to allow similarity to check the % patch match of given string values */
	public static boolean ALLOW_SIMILARITY;

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
	public static boolean NOC_GENERATION_MULTIPLE;
	public static boolean CUSTOMIZED_TEMPLATES;
	public static boolean DERIVED_EMI_REQ;
	public static boolean IS_DATA_SYNC_REQ_BY_APP_DATE;
	public static boolean CUSTOM_BLACKLIST_PARAMS;
	public static boolean DISBURSEMENT_ALLOW_CO_APP;
	public static boolean CHEQUE_ALLOW_CO_APP;
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

	// Currency constants for Performance
	public static boolean ALLOW_MULTI_CCY;
	public static String BASE_CCY;
	public static int BASE_CCY_EDT_FIELD;
	public static boolean SUSP_CHECK_REQ;
	/* Flag to validate schedule date in range of maker & checker dates */
	public static boolean VALIDATION_ON_CHECKER_APPROVER_ALLOWED;
	/*
	 * public static boolean ALLOW_FI_INITIATION_LOS; public static boolean ALLOW_TV_INITIATION_LOS; public static
	 * boolean ALLOW_LV_INITIATION_LOS; public static boolean ALLOW_RCU_INITIATION_LOS;
	 */
	public static boolean ALLOW_EOD_INTERVAL_VALIDATION;
	public static boolean DISB_PAID_CANCELLATION_REQ;
	public static boolean ALLOW_LOAN_VAS_RATIO_CALC;
	public static boolean ALLOW_ED_FIELDS_IN_NPA;
	public static boolean PRESENTMENT_STAGE_ACCOUNTING_REQ;
	public static boolean FRQ_15DAYS_REQ;
	public static boolean NON_FRQ_CAPITALISATION;
	public static boolean ALW_DOWNPAY_IN_LOANENQ_AND_SOA;
	/* Documents tab is automatically open in FinAdvancePayments Screen when value is "true" */
	public static boolean FA_CANCEL_CHEQUE_AUTO_OPEN;
	public static boolean FUR_DISBINST_ACC_REQ;

	public static String REPAY_HIERARCHY_METHOD;
	public static String REPAY_INTEREST_HIERARCHY;
	public static String CLIENT_BFL;
	public static String CLIENT_NAME;
	public static String NBFC;
	public static String BANK;
	public static String CLIENTTYPE;
	public static String COLLATERAL_ADJ;

	public static int PRESENTMENT_EXPORT_STATUS_MIN_LENGTH;
	public static int PRESENTMENT_EXPORT_STATUS_MAX_LENGTH;
	public static boolean AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT;
	/* Cibil report must generate based on entity code */
	public static boolean CIBIL_BASED_ON_ENTITY;
	public static boolean PENALTY_CALC_ON_REPRESENTATION;

	// Restructuring Parameters
	// In case of restructure Date is allowed to enter from User / By default Setting application date
	public static boolean RESTRUCTURE_DFT_APP_DATE;
	// In case Defaulted Application Date to restructure Date is user Editable
	public static boolean RESTRUCTURE_DATE_ALW_EDIT;
	// Rate Change allow in Restructuring process
	public static boolean RESTRUCTURE_RATE_CHG_ALW;
	// Allow Charges for User Selection for POS calculation
	public static boolean RESTRUCTURE_ALW_CHARGES;
	public static boolean ALLOW_SHADOW_POSTINGS;
	public static boolean DISB_REQ_RES_FILE_GEN_MODE;
	public static boolean ALLOW_ESCROW_MODE;
	public static boolean ALLOW_CUSTCIF_IN_IMD;
	// Allow TDS percentage based on year
	public static boolean ALLOW_TDS_PERC_BASED_ON_YEAR;
	// TDS Certificate Adjustment
	public static boolean ALLOW_TDS_CERTIFICATE_ADJUSTMENT;
	public static boolean ALLOW_ISRA_DETAILS;
	public static boolean ALLOW_DFS_CASH_COLLATERAL_EXCESS_HEADS;
	public static boolean ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS;
	public static boolean ALLOW_OD_POSINT_SCHD_METHOD;
	// Allow Accrual Difference posting one Time or complete recalculation
	public static boolean ACCRUAL_DIFF_ONETIME_POST;
	public static boolean ALLOW_GST_DETAILS;
	public static boolean SAN_BASED_EMI_REQUIRED_STEP;
	public static boolean ALLOW_ZERO_STEP_AMOUNT_PERC;
	public static boolean ALLOW_STEP_RECAL_PRORATA;
	public static boolean CUSTOMER_PAN_VALIDATION_STOP;
	public static boolean LOAN_PURPOSE_MANDATORY;
	public static boolean GUARANTOR_EMAIL_MANDATORY;
	public static boolean ALLOW_IMD_WITHOUT_REALIZED;

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
	// flag to allow VAS disbursement instruction editable or not
	public static boolean VAS_INST_EDITABLE;
	public static boolean ALLOW_DISB_ENQUIRY;
	public static boolean ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE;
	public static boolean CUSTOM_EXT_LIABILITIES;
	public static boolean DISB_REVERSAL_REQ_BEFORE_LOAN_CANCEL;
	public static boolean UPDATE_METADATA_IN_DMS;
	public static boolean CHEQUENO_MANDATORY_DISB_INS;
	public static boolean ALW_QDP_CUSTOMIZATION;
	// Update the Manual Cheque Receipt status as paid at Deposit approver
	public static boolean CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER;
	public static boolean BRANCHWISE_RCU_INITIATION;
	public static boolean ALLOW_NON_LAN_RECEIPTS;
	public static boolean ALLOW_BUILDER_BENEFICIARY_DETAILS;
	public static boolean COVENANT_ADTNL_REMARKS;
	public static boolean HOLD_DISB_INST_POST;
	public static boolean VAS_INST_ON_DISB;
	public static boolean ALLOW_SUBVENTION;
	public static boolean PRESENT_RESP_BOUNCE_REMARKS_MAN;
	public static boolean VAS_VALIDATION_FOR_PREMIUM_CALC;
	public static boolean DISBURSEMENT_INSTRUCTIONS_OFF_LINE;
	public static boolean SNAP_SHOT_DATE_AS_CUR_BUS_DATE;
	public static boolean CHEQUE_AMOUNT_ZERO_UDC;
	public static String GST_INCLUSIVE_SYMBOL;
	public static String GST_EXCLUSIVE_SYMBOL;
	public static boolean ALW_RATE_CHANGE;
	public static boolean IMD_EXT_REFERENCE;
	public static boolean BOUNCE_CHARGE_ON_DPD_COUNT;
	public static boolean RETAIL_CUST_PAN_MANDATORY;
	public static boolean FINREFERENCE_ALW_FREE_TEXT;
	public static boolean RECEIPT_DUPLICATE_CHECK_STOP;
	public static boolean EXTENDEDFIELDS_ORG_WORKFLOW;

	public static boolean OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE;
	public static boolean MANUAL_ADVISE_FUTURE_DATE;
	public static String RECEIPT_DUPLICATE_FORMAT;
	/**
	 * Parameter for to allow the 'DISBINS' postings while disbursement instructions download.
	 */
	public static boolean DISB_INST_POST_DWNLD;
	public static boolean ALLOW_NPA;
	public static boolean ALLOW_PROVISION;
	public static NpaScope NPA_SCOPE;
	public static ProvisionBook PROVISION_BOOKS;
	public static boolean PROVISION_REVERSAL_REQ;
	public static ProvisionReversalStage PROVISION_REVERSAL_STAGE;
	public static boolean ALLOW_EXTENDEDFIELDS_IN_WORKFLOW;
	public static boolean PROVISION_POSTINGS_REQ;
	public static boolean RECEIPT_ALLOW_FULL_WAIVER_ACKNOWLEDGEMENT;
	public static boolean RECEIPT_ALLOW_FULL_WAIVER;
	public static boolean ALLOW_CERSAI;
	public static boolean DISB_STP;

	// FIXME MURTHT
	public static final boolean OLD_FINANCIALS_REQUIRED = false;
	// Old Phone numbers required listbox
	public static final boolean OLD_PHONENUMBERS_REQUIRED = false;
	// Old Emails required listbox
	public static final boolean OLD_EMAILS_REQUIRED = false;

	public static final boolean CUST_ADDR_AUTO_FILL;

	public static final boolean LEAD_ID_IS_MANDATORY = true;
	public static final boolean LIST_RENDER_ON_LOAD;

	public static boolean LOAN_ORG_DMS_TAB_REQ = false;
	public static boolean ALLOW_PMAY;
	public static boolean ALLOW_OCR;

	public static boolean ADVANCE_PAYMENT_INT;
	public static boolean ADVANCE_PAYMENT_EMI;
	public static boolean COVENANT_MODULE_NEW;

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

	public static boolean CUSTOMIZED_SOAREPORT;
	public static boolean COLLECTION_DOWNLOAD_REQ;
	public static boolean AUTO_WAIVER_REQUIRED_FROMSCREEN;
	public static boolean AUTO_KNOCK_OFF_ON_DUE_DATE;
	/*
	 * In Transactions Details “Instalment Due” & “Receipt” Entries to be reflected with bifurcation “Principal” &
	 * “Interest” :: true for AXIS
	 */
	public static boolean SOA_INSTALlEMENT_BIFURCATION;

	/**
	 * Feature extension to create the receipts on due date or response upload, default value is true.
	 */
	public static boolean DUE_DATE_RECEIPT_CREATION;

	/**
	 * Feature extension to enable or disable auto extraction.
	 */
	public static boolean AUTO_EXTRACTION;

	/**
	 * Feature extension to enable or disable auto approval.
	 */
	public static boolean AUTO_APPROVAL;

	/**
	 * Feature extension to allow the mandate approval even the loan in origination.
	 */
	public static boolean APPROVE_ON_LOAN_ORG;

	/**
	 * Feature extension whether the Entity-Code is mandatory or not for mandate response file upload. .
	 */
	public static boolean UPLOAD_ENITITY_CODE_MANDATORY;

	/**
	 * Feature extension for mandate registration request auto download
	 */
	public static boolean AUTO_DOWNLOAD;

	/**
	 * Feature extension for mandate registration response auto upload.
	 */
	public static boolean AUTO_UPLOAD;

	/**
	 * Feature extension to allow Co-Applicants for mandate creation in loan queue.
	 */
	public static boolean ALLOW_CO_APP;

	/**
	 * Feature extension to extract the registration file against to partner bank wise
	 */
	public static boolean PARTNER_BANK_WISE_EXTARCTION;

	/**
	 * Feature extension to change the frequency description in registration file
	 */
	public static boolean FRQ_DES_CHANGE_ON_EXTRACTION;

	/**
	 * Feature extension to capture partner bank in mandate creation
	 */
	public static boolean PARTNER_BANK_REQ;

	/**
	 * Feature extension to capture partner bank in mandate creation
	 */
	public static boolean SWAP_EFFECTIVE_DATE_DEFAULT;

	/**
	 * Feature extension to capture partner bank in mandate creation
	 */
	public static boolean ACCOUNT_DETAILS_READONLY;

	/**
	 * Feature extension to for Consecutive Hold Reason
	 */
	public static String CONSECUTIVE_HOLD_REASON = "CONSECUTIVE BOUNCE";

	public static boolean ALLOW_CONSECUTIVE_BOUNCE;

	/**
	 * Feature Extension to no mandate the expiry date
	 */
	public static boolean EXPIRY_DATE_MANDATORY;

	public static boolean ALW_AUTO_CROSS_LOAN_KNOCKOFF;

	private static void setVerificationConstants() {
		VER_INIT_FROM_OUTSIDE = getValueAsBoolean("VER_INIT_FROM_OUTSIDE", false);
		VER_INIT_AGENT_MANDATORY = getValueAsBoolean("VER_INIT_AGENT_MANDATORY", false);
		VER_AGENCY_FILTER_BY_CITY = getValueAsBoolean("VER_AGENCY_FILTER_BY_CITY", true);

		VER_TV_COLL_ED_ADDR_COLUMN = getValueAsString("VER_TV_COLL_ED_ADDR_COLUMN",
				"PROPERTYCITY"); /* HL>>FIXME "CITY" for HL */
		VER_TV_COLL_ED_PROP_VAL_COLUMN = getValueAsString("VER_TV_COLL_ED_PROP_VAL_COLUMN", "TOTALVALUATIONASPE"); // HL>>FIXME
																													// "DOCVALUE"
																													// for
																													// HL
		VER_TV_COLL_ED_PROP_COST_COLUMN = getValueAsString("VER_TV_COLL_ED_PROP_COST_COLUMN", "COSTOFPROPERTY"); // HL>>FIXME
																													// "DOCVALUE"
																													// for
																													// HL
		VER_RCU_DFT_REQ_TYPE_REQUEST = getValueAsBoolean("VER_RCU_DFT_REQ_TYPE_REQUEST", false);
		VER_REASON_CODE_FILTER_BY_REASONTYPE = getValueAsBoolean("VER_REASON_CODES_REASONTYPE_FILTER", false);
		VER_INITATE_DURING_SAVE = getValueAsBoolean("VER_INITATE_DURING_SAVE", true);
		VER_RCU_INITATE_BY_AGENCY = getValueAsBoolean("VER_RCU_INITATE_BY_AGENCY", true);
		CUSTOMIZED_SOAREPORT = getValueAsBoolean("CUSTOMIZED_SOAREPORT", false);
	}

	public static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.DEFAULT, key, defaultValue);
	}

	public static String getValueAsString(String key, String defaultValue) {
		return FeatureExtension.getValueAsString(Module.DEFAULT, key, defaultValue);
	}

	public static Object getValueAsObject(String key, Object defaultValue) {
		return FeatureExtension.getValueAsObject(Module.DEFAULT, key, defaultValue);
	}

	public static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.DEFAULT, key, defaultValue);
	}

}
