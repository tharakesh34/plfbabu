package com.pennanttech.extension.implementation;

import java.util.HashMap;
import java.util.Map;

import com.pennanttech.pff.npa.NpaScope;
import com.pennanttech.pff.provision.ProvisionBook;
import com.pennanttech.pff.provision.ProvisionReversalStage;

public class FeatureExtension implements IFeatureExtension {
	static Map<String, Object> customConstants = new HashMap<>();
	static Map<String, Object> customerExtensions = new HashMap<>();
	static Map<String, Object> mandateExtensions = new HashMap<>();
	static Map<String, Object> presentmentExtensions = new HashMap<>();
	static Map<String, Object> accountingExtensions = new HashMap<>();
	static Map<String, Object> feeExtensions = new HashMap<>();
	static Map<String, Object> dpdExtensions = new HashMap<>();
	static Map<String, Object> partnerBankExtensions = new HashMap<>();
	static Map<String, Object> receiptExtensions = new HashMap<>();

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

		customConstants.put("ALLOW_AUTO_GRACE_EXT", true);

		customConstants.put("ALLOW_OCR", true);

		customConstants.put("IND_AS_ACCOUNTING_REQ", true);

		customConstants.put("DISB_PAID_CANCELLATION_ALLOW", true);

		customConstants.put("LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS", false);

		customConstants.put("INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS", false);

		customConstants.put("PRESENTMENT_STAGE_ACCOUNTING_REQ", false);

		customConstants.put("AGGR_EMI_AMOUNT_ON_SANCTIONED_AMT", true);

		customConstants.put("ALLOW_AUTO_GRACE_EXT", true);

		customConstants.put("ALLOW_LOAN_DOWNSIZING", true);

		customConstants.put("ALLOW_RESTRUCTURING", true);

		customConstants.put("ALLOW_LOAN_SPLIT", true);

		customConstants.put("SCHD_INST_CAL_ON_DISB_RELIZATION", true);

		customConstants.put("ALLOW_SUBVENTION", true);

		customConstants.put("PRESENT_RECEIPTS_ON_RESP", false);

		customConstants.put("PRESENT_RESP_BOUNCE_REMARKS_MAN", true);

		customConstants.put("DISBURSEMENT_AUTO_DOWNLOAD", false);

		customConstants.put("DISBURSEMENT_ALLOW_CO_APP", true);

		customConstants.put("CHEQUE_ALLOW_CO_APP", true);

		customConstants.put("CUST_ADDR_AUTO_FILL", true);

		customConstants.put("VAS_INST_ON_DISB", true);

		customConstants.put("ALLOW_BUILDER_BENEFICIARY_DETAILS", true);

		customConstants.put("POPULATE_DFT_INCOME_DETAILS", true);

		customConstants.put("RESTRUCTURE_DFT_APP_DATE", true);

		customConstants.put("RESTRUCTURE_DATE_ALW_EDIT", true);

		customConstants.put("RESTRUCTURE_RATE_CHG_ALW", true);

		customConstants.put("RESTRUCTURE_ALW_CHARGES", true);

		customConstants.put("DISB_REQ_RES_FILE_GEN_MODE", true);

		customConstants.put("ALLOW_ESCROW_MODE", true);

		customConstants.put("ALLOW_NPA", true);

		customConstants.put("ALLOW_PROVISION", false);

		customConstants.put("PROVISION_REVERSAL_REQ", false);

		customConstants.put("PROVISION_REVERSAL_STAGE", ProvisionReversalStage.SOM);

		customConstants.put("NPA_SCOPE", NpaScope.LOAN);

		customConstants.put("PROVISION_BOOKS", ProvisionBook.NO_PROVISION);

		customConstants.put("ALLOW_MANUAL_SCHEDULE", true);

		customConstants.put("RETAIL_CUST_PAN_MANDATORY", false);

		customConstants.put("ALLOW_DFS_CASH_COLLATERAL_EXCESS_HEADS", true);

		customConstants.put("ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS", true);

		customConstants.put("ALLOW_ISRA_DETAILS", true);

		customConstants.put("RECEIPT_ALLOW_FULL_WAIVER", true);

		customConstants.put("COLLECTION_DOWNLOAD_REQ", false);

		customConstants.put("AUTO_EOD_REQUIRED", true);

		customerExtensions();

		mandateExtensaions();

		presentmentExtensaions();

		accountingExtensions();

		feeExtensions();

		dpdExtensions();

		partnerBankExtensions();

		receiptExtensions();
	}

	@Override
	public Map<String, Object> getCustomConstants() {
		return customConstants;
	}

	@Override
	public Map<String, Object> getCustomerExtensions() {
		return customerExtensions;
	}

	@Override
	public Map<String, Object> getMandateExtensions() {
		return mandateExtensions;
	}

	@Override
	public Map<String, Object> getPresentmentExtensions() {
		return presentmentExtensions;

	}

	@Override
	public Map<String, Object> getAccountingExtensions() {
		return accountingExtensions;
	}

	@Override
	public Map<String, Object> getFeeExtensions() {
		return feeExtensions;
	}

	@Override
	public Map<String, Object> getDPDExtensions() {
		return dpdExtensions;
	}

	@Override
	public Map<String, Object> getPartnerBankExtensions() {
		return partnerBankExtensions;
	}

	@Override
	public Map<String, Object> getReceiptExtensions() {
		return receiptExtensions;
	}

	private void customerExtensions() {
		customerExtensions.put("CUST_CORE_BANK_ID", true);
		customerExtensions.put("ALLOW_DUPLICATE_PAN", true);
	}

	private void mandateExtensaions() {
		mandateExtensions.put("PARTNER_BANK_REQ", true);
		mandateExtensions.put("ALLOW_CONSECUTIVE_BOUNCE", true);
		mandateExtensions.put("EXPIRY_DATE_MANDATORY", false);
		mandateExtensions.put("MANDATE_SPLIT_COUNT", 100);
		mandateExtensions.put("PARTNER_BANK_WISE_EXTARCTION", true);
		mandateExtensions.put("AUTO_UPLOAD", true);
		mandateExtensions.put("BR_INST_TYPE_MAN", true);
	}

	private void presentmentExtensaions() {
		presentmentExtensions.put("DUE_DATE_RECEIPT_CREATION", false);
		presentmentExtensions.put("AUTO_EXTRACTION", true);
		presentmentExtensions.put("AUTO_APPROVAL", true);
	}

	private void accountingExtensions() {
		accountingExtensions.put("LOAN_TYPE_GL_MAPPING", false);
		accountingExtensions.put("NORMAL_GL_MAPPING", true);
	}

	private void feeExtensions() {
		feeExtensions.put("FEE_SERVICEING_STAMPIN_ON_ORG", true);
		feeExtensions.put("ALLOW_SINGLE_FEE_CONFIG", true);
	}

	private void dpdExtensions() {
		dpdExtensions.put("EXCLUDE_VD_PART_PAYMENT", true);
	}

	private void partnerBankExtensions() {
		partnerBankExtensions.put("BRANCH_WISE_MAPPING", true);
		partnerBankExtensions.put("BRANCH_OR_CLUSTER", "C");
		partnerBankExtensions.put("CLUSTER_TYPE", "BO");
	}

	private void receiptExtensions() {
		receiptExtensions.put("STOP_BACK_DATED_EARLY_SETTLE", false);
	}
}
