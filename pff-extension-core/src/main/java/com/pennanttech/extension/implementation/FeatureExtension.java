package com.pennanttech.extension.implementation;

import java.util.HashMap;
import java.util.Map;

import com.pennanttech.pff.npa.NpaScope;
import com.pennanttech.pff.provision.ProvisionBook;
import com.pennanttech.pff.provision.ProvisionReversalStage;

public class FeatureExtension implements IFeatureExtension {
	static Map<String, Object> defaultExtensions = new HashMap<>();
	static Map<String, Object> mandateExtensions = new HashMap<>();
	static Map<String, Object> presentmentExtensions = new HashMap<>();


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

		defaultExtensions.put("ALLOW_IND_AS", true);

		defaultExtensions.put("ALLOW_ADV_INT", true);

		defaultExtensions.put("ALLOW_ADV_EMI", true);

		defaultExtensions.put("ALLOW_DSF_CASHCLT", true);

		defaultExtensions.put("ALLOW_TDS_ON_FEE", true);

		defaultExtensions.put("ALLOW_OD_LOANS", false);

		defaultExtensions.put("ALLOW_CD_LOANS", true);

		defaultExtensions.put("ALLOW_SCHOOL_ORG", true);

		defaultExtensions.put("ALLOW_SAMPLING", true);

		defaultExtensions.put("ALLOW_AUTO_KNOCK_OFF", true);

		defaultExtensions.put("ALLOW_PMAY", true);
		defaultExtensions.put("ALLOW_AUTO_GRACE_EXT", true);

		defaultExtensions.put("ALLOW_OCR", true);

		defaultExtensions.put("IND_AS_ACCOUNTING_REQ", true);

		defaultExtensions.put("DISB_PAID_CANCELLATION_ALLOW", true);

		defaultExtensions.put("LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS", false);

		defaultExtensions.put("INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS", false);

		defaultExtensions.put("GROUP_BATCH_BY_PARTNERBANK", true);

		defaultExtensions.put("PRESENTMENT_STAGE_ACCOUNTING_REQ", false);

		defaultExtensions.put("AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT", true);

		defaultExtensions.put("ALLOW_AUTO_GRACE_EXT", true);

		defaultExtensions.put("ALLOW_LOAN_DOWNSIZING", true);

		defaultExtensions.put("ALLOW_RESTRUCTURING", true);

		defaultExtensions.put("ALLOW_LOAN_SPLIT", true);

		defaultExtensions.put("SCHD_INST_CAL_ON_DISB_RELIZATION", true);

		defaultExtensions.put("ALLOW_SUBVENTION", true);

		defaultExtensions.put("PRESENT_RECEIPTS_ON_RESP", false);

		defaultExtensions.put("PRESENT_RESP_BOUNCE_REMARKS_MAN", true);

		defaultExtensions.put("DISBURSEMENT_AUTO_DOWNLOAD", false);

		// Temporary Enabled, need to disable once the testing is done
		defaultExtensions.put("DISBURSEMENT_ALLOW_CO_APP", true);
		defaultExtensions.put("CHEQUE_ALLOW_CO_APP", true);
		defaultExtensions.put("CUST_ADDR_AUTO_FILL", true);
		defaultExtensions.put("HOLD_DISB_INST_POST", true);
		defaultExtensions.put("VAS_INST_ON_DISB", true);
		defaultExtensions.put("ALLOW_BUILDER_BENEFICIARY_DETAILS", true);
		defaultExtensions.put("POPULATE_DFT_INCOME_DETAILS", true);
		defaultExtensions.put("ALLOW_SINGLE_FEE_CONFIG", true);

		defaultExtensions.put("RESTRUCTURE_DFT_APP_DATE", true);

		defaultExtensions.put("RESTRUCTURE_DATE_ALW_EDIT", true);

		defaultExtensions.put("RESTRUCTURE_RATE_CHG_ALW", true);

		defaultExtensions.put("RESTRUCTURE_ALW_CHARGES", true);

		defaultExtensions.put("DISB_REQ_RES_FILE_GEN_MODE", true);

		defaultExtensions.put("ALLOW_ESCROW_MODE", true);

		defaultExtensions.put("ALLOW_NPA", true);

		defaultExtensions.put("ALLOW_PROVISION", true);

		defaultExtensions.put("PROVISION_REVERSAL_REQ", true);

		defaultExtensions.put("PROVISION_REVERSAL_STAGE", ProvisionReversalStage.SOM);

		defaultExtensions.put("NPA_SCOPE", NpaScope.CO_APPLICANT);

		defaultExtensions.put("PROVISION_BOOKS", ProvisionBook.REGULATORY);

		defaultExtensions.put("ALLOW_MANUAL_SCHEDULE", true);

		///////// For Enhancements from Axis and Clix///////////////

		defaultExtensions.put("RETAIL_CUST_PAN_MANDATORY", false);

		defaultExtensions.put("ALLOW_DFS_CASH_COLLATERAL_EXCESS_HEADS", true);

		defaultExtensions.put("ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS", true);

		defaultExtensions.put("ALLOW_ISRA_DETAILS", true);

		defaultExtensions.put("RECEIPT_ALLOW_FULL_WAIVER", true);

		mandateExtensaions();

	}

	private void mandateExtensaions() {
		mandateExtensions.put("MANDATE_PTNRBNK_IN_DWNLD", false);
		mandateExtensions.put("MANDATE_REQ_RES_FILE_GEN_PARTNERBNAK", false);
	}

	@Override
	public Map<String, Object> getCustomConstants() {
		return defaultExtensions;
	}

	@Override
	public Map<String, Object> getMandateExtensions() {
		return mandateExtensions;
	}

	@Override
	public Map<String, Object> getPresentmentExtensions() {
		return presentmentExtensions;

	}
}
