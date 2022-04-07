package com.pennanttech.extension.implementation;

import java.util.HashMap;
import java.util.Map;

public class FeatureExtension implements IFeatureExtension {
	static Map<String, Object> customConstants = new HashMap<>();

	/**
	 * <p>
	 * Override the implementation constants. Here the constant name should match with implementation constant variable
	 * name.
	 * </p>
	 * 
	 * example
	 * <p>
	 * <code>customConstants.put("CLIENT_NAME", "Pennant Technologies Private Limited");</code>
	 * </p>
	 */
	public FeatureExtension() {
		super();

		/* Override the implementation constants here as specified in example. */

		customConstants.put("ALLOW_IND_AS", true);

		customConstants.put("ALLOW_ADV_INT", true);

		customConstants.put("ALLOW_ADV_EMI", true);

		customConstants.put("ALLOW_DSF_CASHCLT", true);

		customConstants.put("ALLOW_TDS_ON_FEE", true);

		customConstants.put("ALLOW_OD_LOANS", false);

		customConstants.put("ALLOW_CD_LOANS", true);

		customConstants.put("ALLOW_SCHOOL_ORG", true);

		customConstants.put("ALLOW_SAMPLING", true);

		customConstants.put("ALLOW_AUTO_KNOCK_OFF", true);

		customConstants.put("ALLOW_PMAY", true);

		customConstants.put("ALLOW_OCR", true);

		customConstants.put("IND_AS_ACCOUNTING_REQ", true);

		customConstants.put("DISB_PAID_CANCELLATION_ALLOW", true);

		customConstants.put("MANDATE_PTNRBNK_IN_DWNLD", false);

		customConstants.put("LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS", true);

		customConstants.put("GROUP_BATCH_BY_PARTNERBANK", true);

		customConstants.put("PRESENTMENT_STAGE_ACCOUNTING_REQ", false);

		customConstants.put("AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT", true);

		customConstants.put("ALLOW_AUTO_GRACE_EXT", true);

		customConstants.put("ALLOW_LOAN_DOWNSIZING", true);

		customConstants.put("ALLOW_RESTRUCTURING", true);

		customConstants.put("PRESENT_RECEIPTS_ON_RESP", false);

		customConstants.put("PRESENT_RESP_BOUNCE_REMARKS_MAN", true);

		customConstants.put("DISBURSEMENT_AUTO_DOWNLOAD", false);

		// Temporary Enabled, need to disable once the testing is done
		customConstants.put("MANDATE_ALLOW_CO_APP", true);
		customConstants.put("DISBURSEMENT_ALLOW_CO_APP", true);
		customConstants.put("CHEQUE_ALLOW_CO_APP", true);
		customConstants.put("CUST_ADDR_AUTO_FILL", true);
		customConstants.put("HOLD_DISB_INST_POST", true);
		customConstants.put("VAS_INST_ON_DISB", true);
		customConstants.put("ALLOW_BUILDER_BENEFICIARY_DETAILS", true);
		customConstants.put("POPULATE_DFT_INCOME_DETAILS", true);
		customConstants.put("ALLOW_SINGLE_FEE_CONFIG", true);

		customConstants.put("RESTRUCTURE_DFT_APP_DATE", true);

		customConstants.put("RESTRUCTURE_DATE_ALW_EDIT", true);

		customConstants.put("RESTRUCTURE_RATE_CHG_ALW", true);

		customConstants.put("RESTRUCTURE_ALW_CHARGES", true);

		customConstants.put("MANDATE_REQ_RES_FILE_GEN_PARTNERBNAK", true);

		customConstants.put("DISB_REQ_RES_FILE_GEN_MODE", true);

		customConstants.put("ALLOW_ESCROW_MODE", false);

	}

	@Override
	public Map<String, Object> getCustomConstants() {
		return customConstants;
	}
}
