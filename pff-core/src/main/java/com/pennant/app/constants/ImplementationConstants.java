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

		IMPLEMENTATION_CONVENTIONAL = getValueAsBoolean(extensions, "IMPLEMENTATION_CONVENTIONAL", true);
		IMPLEMENTATION_ISLAMIC = getValueAsBoolean(extensions, "IMPLEMENTATION_ISLAMIC", false);
		ALLOW_FINACTYPES = getValueAsBoolean(extensions, "ALLOW_FINACTYPES", false);
		ALLOW_CAPITALIZE = getValueAsBoolean(extensions, "ALLOW_CAPITALIZE", true);
		LIMIT_INTERNAL = getValueAsBoolean(extensions, "LIMIT_INTERNAL", true);
		ONLINE_IRL_CHECK = getValueAsBoolean(extensions, "ONLINE_IRL_CHECK", false);
		ALLOW_MULTIPLE_EMPLOYMENTS = getValueAsBoolean(extensions, "ALLOW_MULTIPLE_EMPLOYMENTS", true);
		ALLOW_CUSTOMER_MAINTENANCE = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_MAINTENANCE", true);
		ALLOW_CUSTOMER_RATINGS = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_RATINGS", false);
		ALLOW_CUSTOMER_INCOMES = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_INCOMES", true);
		ALLOW_CUSTOMER_SHAREHOLDERS = getValueAsBoolean(extensions, "ALLOW_CUSTOMER_SHAREHOLDERS", true);
		INITATE_VERIFICATION_DURING_SAVE = getValueAsBoolean(extensions, "INITATE_VERIFICATION_DURING_SAVE", true);
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
		INITATE_VERI_RCU_GRP_BY_AGENCY = getValueAsBoolean(extensions, "INITATE_VERI_RCU_GRP_BY_AGENCY", true);
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
		ALLOW_IND_AS = getValueAsBoolean(extensions, "ALLOW_IND_AS", false);

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
	public static boolean INITATE_VERIFICATION_DURING_SAVE;
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
	public static boolean INITATE_VERI_RCU_GRP_BY_AGENCY;
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
	public static boolean ALLOW_IND_AS;
	public static boolean ALLOW_AUTO_KNOCK_OFF;

	/*
	 * public static boolean ALLOW_FI_INITIATION_LOS; public static boolean ALLOW_TV_INITIATION_LOS; public static
	 * boolean ALLOW_LV_INITIATION_LOS; public static boolean ALLOW_RCU_INITIATION_LOS;
	 */

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
	public static String LPP_GST_DUE_ON;

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

}
