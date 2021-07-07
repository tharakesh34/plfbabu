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

		customConstants.put("AUTO_EOD_REQUIRED", true);

		customConstants.put("ALLOW_IND_AS", true);

		customConstants.put("ALLOW_ADV_INT", true);

		customConstants.put("ALLOW_ADV_EMI", true);

		customConstants.put("ALLOW_DSF_CASHCLT", true);

		customConstants.put("ALLOW_TDS_ON_FEE", true);

		customConstants.put("ALLOW_OD_LOANS", true);

		customConstants.put("ALLOW_CD_LOANS", true);

		customConstants.put("ALLOW_SCHOOL_ORG", true);

		customConstants.put("ALLOW_SAMPLING", true);

		customConstants.put("ALLOW_AUTO_KNOCK_OFF", true);

		customConstants.put("ALLOW_NPA_PROVISION", true);

		customConstants.put("ALLOW_PMAY", true);

		customConstants.put("ALLOW_OCR", true);

		customConstants.put("IND_AS_ACCOUNTING_REQ", true);

		customConstants.put("DISB_PAID_CANCELLATION_ALLOW", true);

		customConstants.put("MANDATE_PTNRBNK_IN_DWNLD", true);

		customConstants.put("LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS", true);

		customConstants.put("GROUP_BATCH_BY_PARTNERBANK", true);

		customConstants.put("PRESENTMENT_STAGE_ACCOUNTING_REQ", true);

		customConstants.put("AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT", true);

		customConstants.put("ALLOW_AUTO_GRACE_EXT", true);

		customConstants.put("ALLOW_LOAN_DOWNSIZING", true);

		customConstants.put("ALLOW_RESTRUCTURING", true);

		customConstants.put("ALLOW_LOAN_SPLIT", true);

		customConstants.put("SCHD_INST_CAL_ON_DISB_RELIZATION", true);

		customConstants.put("ALLOW_SUBVENTION", true);

		customConstants.put("PRESENT_RECEIPTS_ON_RESP", true);

		customConstants.put("PRESENT_RESP_BOUNCE_REMARKS_MAN", true);

		// Temporary Enabled, need to disable once the testing is done
		customConstants.put("MANDATE_ALLOW_CO_APP", true);
		customConstants.put("DISBURSEMENT_ALLOW_CO_APP", true);
		customConstants.put("CHEQUE_ALLOW_CO_APP", true);
		customConstants.put("CUST_ADDR_AUTO_FILL", false);
		customConstants.put("HOLD_DISB_INST_POST", true);
		customConstants.put("VAS_INST_ON_DISB", true);
		customConstants.put("FEE_SERVICEING_STAMPIN_ON_ORG", true);
		customConstants.put("ALLOW_BUILDER_BENEFICIARY_DETAILS", true);

	}

	@Override
	public Map<String, Object> getCustomConstants() {
		return customConstants;
	}
}
