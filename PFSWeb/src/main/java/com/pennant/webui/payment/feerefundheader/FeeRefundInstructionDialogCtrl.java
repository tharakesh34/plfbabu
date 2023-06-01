package com.pennant.webui.payment.feerefundheader;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FeeRefundInstructionDialogCtrl extends GFCBaseCtrl<FeeRefundInstruction> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FeeRefundInstructionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DisbursementsInstructionsDialog;

	protected Datebox postDate;
	protected Combobox paymentType;
	protected CurrencyBox paymentAmount;
	protected ExtendedCombobox partnerBankID;
	protected Textbox remarks;
	protected Textbox tranReference;
	protected Textbox status;
	protected Textbox rejectReason;

	// IMPS Details
	protected ExtendedCombobox issuingBank;
	protected Textbox favouringName;
	protected Textbox payableLoc;
	protected Textbox printingLoc;
	protected Space madndatory_PrintingLoc;
	protected Datebox valueDate;
	protected Textbox chequeOrDDumber;

	// NEFT Details
	protected ExtendedCombobox bankBranchID;
	protected Textbox bank;
	protected Textbox branch;
	protected Textbox city;
	protected Textbox acctNumber;
	protected Textbox acctHolderName;
	protected Space contactNumber;
	protected Textbox phoneNumber;
	protected Label recordType;
	protected Textbox leiNumber;
	protected Groupbox gb_statusDetails;
	protected Groupbox gb_ChequeDetails;
	protected Groupbox gb_NeftDetails;
	protected Component parentTabPanel = null;
	protected Tab tabDisbInstructions = null;

	protected Caption caption_FinAdvancePaymentsDialog_NeftDetails;
	protected Caption caption_FinAdvancePaymentsDialog_ChequeDetails;

	private transient BankDetailService bankDetailService;
	private FeeRefundInstruction feeRefundInstruction;
	private FeeRefundHeader feeRefundHeader;
	private FinanceMain financeMain;

	private int ccyFormatter;
	private int maxAccNoLength;
	private int minAccNoLength;
	private BankDetail bankDetail;

	/**
	 * default constructor.<br>
	 */
	public FeeRefundInstructionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeRefundInstructionDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected DisbursementInstructionsDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_DisbursementsInstructionsDialog(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		// Set the page level components.
		setPageComponents(window_DisbursementsInstructionsDialog);

		try {
			// READ OVERHANDED params
			if (arguments.containsKey("feeRefundInstruction")) {
				this.feeRefundInstruction = (FeeRefundInstruction) arguments.get("feeRefundInstruction");
				FeeRefundInstruction befImage = new FeeRefundInstruction();
				BeanUtils.copyProperties(this.feeRefundInstruction, befImage);
				this.feeRefundInstruction.setBefImage(befImage);
				setFeeRefundInstruction(this.feeRefundInstruction);
			} else {
				setFeeRefundInstruction(null);
			}

			this.enqiryModule = (Boolean) arguments.get("enqiryModule");

			if (arguments.containsKey("feeRefundHeader")) {
				setFeeRefundHeader((FeeRefundHeader) arguments.get("feeRefundHeader"));
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			if (arguments.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) arguments.get("financeMain"));
			}

			if (event.getTarget().getParent() != null) {
				parentTabPanel = event.getTarget().getParent();
			}

			if (arguments.containsKey("tab")) {
				tabDisbInstructions = (Tab) arguments.get("tab");
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (int) arguments.get("ccyFormatter");
			}
			doLoadWorkFlow(this.feeRefundHeader.isWorkflow(), this.feeRefundHeader.getWorkflowId(),
					this.feeRefundHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.feeRefundInstruction);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public FeeRefundInstruction onSave() {
		logger.debug("Entering");

		doSetValidation();
		FeeRefundInstruction feeRefundInstruction = doWriteComponentsToBean(this.feeRefundInstruction);

		logger.debug("Entering");

		return feeRefundInstruction;
	}

	public void doShowDialog(FeeRefundInstruction feeRefundInstruction) {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled() && !this.enqiryModule) {
			doEdit();
		} else {
			doReadOnly();
		}

		doWriteBeanToComponents(feeRefundInstruction);

		if (parentTabPanel != null) {
			this.window_DisbursementsInstructionsDialog.setHeight(borderLayoutHeight - 75 + "px");
			parentTabPanel.appendChild(this.window_DisbursementsInstructionsDialog);
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		this.paymentAmount.setDisabled(isReadOnly("FeeRefundInstructionDialog_paymentAmount"));
		this.postDate.setDisabled(isReadOnly("FeeRefundInstructionDialog_postDate"));
		this.paymentType.setDisabled(isReadOnly("FeeRefundInstructionDialog_paymentType"));
		this.remarks.setReadonly(isReadOnly("FeeRefundInstructionDialog_remarks"));
		this.bankBranchID.setReadonly(isReadOnly("FeeRefundInstructionDialog_bankBranchID"));
		this.acctNumber.setReadonly(isReadOnly("FeeRefundInstructionDialog_acctNumber"));
		this.acctHolderName.setReadonly(isReadOnly("FeeRefundInstructionDialog_acctHolderName"));
		this.phoneNumber.setReadonly(isReadOnly("FeeRefundInstructionDialog_phoneNumber"));
		this.issuingBank.setReadonly(isReadOnly("FeeRefundInstructionDialog_issuingBank"));
		this.favouringName.setReadonly(isReadOnly("FeeRefundInstructionDialog_favouringName"));
		this.chequeOrDDumber.setReadonly(isReadOnly("FeeRefundInstructionDialog_chequeOrDDumber"));
		this.payableLoc.setReadonly(isReadOnly("FeeRefundInstructionDialog_payableLoc"));
		this.printingLoc.setReadonly(isReadOnly("FeeRefundInstructionDialog_printingLoc"));
		this.valueDate.setReadonly(isReadOnly("FeeRefundInstructionDialog_valueDate"));
		this.partnerBankID.setReadonly(isReadOnly("FeeRefundInstructionDialog_partnerBankID"));
		this.leiNumber.setReadonly(isReadOnly("FeeRefundInstructionDialog_leiNumber"));

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || this.feeRefundHeader.isNewRecord()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.favouringName.setReadonly(true);
		this.paymentAmount.setDisabled(true);
		this.acctHolderName.setReadonly(true);
		this.acctNumber.setReadonly(true);
		this.paymentType.setDisabled(true);
		this.chequeOrDDumber.setReadonly(true);
		this.postDate.setDisabled(true);
		this.remarks.setReadonly(true);
		this.issuingBank.setReadonly(true);
		this.bankBranchID.setReadonly(true);
		this.payableLoc.setDisabled(true);
		this.printingLoc.setDisabled(true);
		this.valueDate.setDisabled(true);
		this.phoneNumber.setReadonly(true);
		this.partnerBankID.setReadonly(true);
		this.leiNumber.setReadonly(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.favouringName.setMaxlength(100);
		this.acctHolderName.setMaxlength(100);
		this.chequeOrDDumber.setMaxlength(6);
		this.remarks.setMaxlength(100);

		this.paymentAmount.setMandatory(true);
		this.paymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.paymentAmount.setScale(ccyFormatter);
		this.paymentAmount.setTextBoxWidth(150);

		this.postDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.favouringName.setWidth("150px");
		this.acctHolderName.setWidth("150px");
		this.postDate.setWidth("150px");
		this.valueDate.setWidth("150px");
		this.chequeOrDDumber.setWidth("150px");
		this.remarks.setWidth("150px");
		this.printingLoc.setWidth("150px");

		this.partnerBankID.setButtonDisabled(true);
		this.partnerBankID.setReadonly(true);
		this.partnerBankID.setModuleName("FinTypePartner");
		this.partnerBankID.setMandatoryStyle(true);
		this.partnerBankID.setValueColumn("PartnerBankCode");
		this.partnerBankID.setDescColumn("PartnerBankName");
		this.partnerBankID.setMaxlength(8);
		this.partnerBankID.setValidateColumns(new String[] { "PartnerBankCode" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("IFSC");
		this.bankBranchID.setDescColumn("BankCode");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "IFSC" });

		this.issuingBank.setModuleName("BankDetail");
		this.issuingBank.setModuleName("BankDetail");
		this.issuingBank.setMandatoryStyle(true);
		this.issuingBank.setValueColumn("BankCode");
		this.issuingBank.setDescColumn("BankName");
		this.issuingBank.setDisplayStyle(2);
		this.issuingBank.setValidateColumns(new String[] { "BankCode" });

		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");

		if (StringUtils.isNotBlank(this.feeRefundInstruction.getBranchBankCode())) {
			bankDetail = bankDetailService.getAccNoLengthByCode(this.feeRefundInstruction.getBranchBankCode());
			this.maxAccNoLength = this.bankDetail.getAccNoLength();
			this.minAccNoLength = this.bankDetail.getMinAccNoLength();
		}
		this.acctNumber.setMaxlength(maxAccNoLength);

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(FeeRefundInstruction fri) {
		logger.debug(Literal.ENTERING);

		if (this.feeRefundHeader.isNewRecord() && fri.getPostDate() == null) {
			this.postDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.postDate.setValue(fri.getPostDate());
		}

		fillComboBox(this.paymentType, fri.getPaymentType(), PennantStaticListUtil.getPaymentTypes(), "");
		if (fri.getPartnerBankId() != Long.MIN_VALUE && fri.getPartnerBankId() != 0) {
			this.partnerBankID.getButton().setDisabled(isReadOnly("PaymentInstructionDialog_partnerBankID"));
			this.partnerBankID.setAttribute("partnerBankId", fri.getPartnerBankId());
			this.partnerBankID.setValue(fri.getPartnerBankCode(), fri.getPartnerBankName());
		}

		this.paymentAmount.setValue(
				PennantApplicationUtil.formateAmount(this.feeRefundInstruction.getPaymentAmount(), ccyFormatter));
		this.remarks.setValue(fri.getRemarks());
		this.tranReference.setValue(fri.getTransactionRef());
		this.status.setValue(fri.getStatus());
		this.rejectReason.setValue(fri.getRejectReason());

		if (fri.getBankBranchId() != Long.MIN_VALUE && fri.getBankBranchId() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", fri.getBankBranchId());
			this.bankBranchID.setValue(fri.getBankBranchIFSC(), fri.getBankBranchCode());
			this.bank.setValue(StringUtils.trimToEmpty(fri.getBankName()));
			this.branch.setValue(fri.getBranchDesc());
			this.city.setValue(StringUtils.trimToEmpty(fri.getpCCityName()));
		}

		this.acctNumber.setValue(fri.getAccountNo());
		this.acctHolderName.setValue(fri.getAcctHolderName());
		this.phoneNumber.setValue(fri.getPhoneNumber());

		this.issuingBank.setAttribute("issuingBank", fri.getIssuingBank());
		this.issuingBank.setValue(StringUtils.trimToEmpty(fri.getIssuingBank()),
				StringUtils.trimToEmpty(fri.getIssuingBankName()));

		this.chequeOrDDumber.setValue(fri.getFavourNumber());
		this.favouringName.setValue(fri.getFavourName());
		this.payableLoc.setValue(fri.getPayableLoc());
		this.printingLoc.setValue(fri.getPrintingLoc());
		this.valueDate.setValue(fri.getValueDate());
		this.leiNumber.setValue(fri.getLei());
		checkPaymentType(fri.getPaymentType());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param fri
	 * @throws InterruptedException
	 */
	public FeeRefundInstruction doWriteComponentsToBean(FeeRefundInstruction fri) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (this.feeRefundHeader.isNewRecord()) {
			fri.setStatus(DisbursementConstants.STATUS_NEW);
		}

		try {
			Date appDate = SysParamUtil.getAppDate();
			if (DateUtil.compare(this.postDate.getValue(), appDate) < 0 && !postDate.isDisabled()) {
				throw new WrongValueException(this.postDate,
						"Payment Date should be greater than or equal to :" + appDate);
			}
			fri.setPostDate(this.postDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setPaymentType(this.paymentType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setPaymentAmount(
					PennantApplicationUtil.unFormateAmount(this.paymentAmount.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.partnerBankID.getValidatedValue();
			Object obj = this.partnerBankID.getAttribute("partnerBankId");
			if (obj != null) {
				fri.setPartnerBankId(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setAccountNo(this.acctNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setAcctHolderName(this.acctHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setFavourNumber(this.chequeOrDDumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setFavourName(this.favouringName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.issuingBank.getValidatedValue();
			Object obj = this.issuingBank.getAttribute("issuingBank");
			if (obj != null) {
				fri.setIssuingBank(String.valueOf(obj));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setPayableLoc(this.payableLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setPrintingLoc(this.printingLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.valueDate.getValue() != null) {
				fri.setValueDate(this.valueDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				fri.setBankBranchId(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			boolean mandatory = false;
			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)) {
				mandatory = true;
			}
			this.phoneNumber.clearErrorMessage();
			this.phoneNumber.setErrorMessage("");
			if (!this.phoneNumber.isReadonly()) {
				this.phoneNumber.setConstraint(new PTMobileNumberValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_PhoneNumber.value"), mandatory));
			}
			fri.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setPaymentCCy(financeMain.getFinCcy());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fri.setLei(this.leiNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doClearMessage();

		if (!wve.isEmpty()) {
			tabDisbInstructions.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
		return fri;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.paymentType.isDisabled()) {
			this.paymentType.setConstraint(
					new PTListValidator<ValueLabel>(Labels.getLabel("label_DisbInstructionsDialog_DisbType.value"),
							PennantStaticListUtil.getPaymentTypesWithIST(), true));
		}

		if (this.paymentAmount.isDisabled()) {
			this.paymentAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_DisbInstructionsDialog_DisbAmount.value"), ccyFormatter, true, false));
		}

		if (!this.partnerBankID.isReadonly()) {
			this.partnerBankID.setConstraint(new PTStringValidator(
					Labels.getLabel("label_DisbInstructionsDialog_Partnerbank.value"), null, true));
		}

		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_Remarks.value"), null, false));
		}

		if (!this.chequeOrDDumber.isReadonly()) {
			this.chequeOrDDumber.setConstraint(new PTStringValidator(
					Labels.getLabel("label_DisbInstructionsDialog_ChequeOrDDumber.value"), null, false));
		}

		if (gb_ChequeDetails.isVisible()) {
			if (!this.favouringName.isReadonly()) {
				this.favouringName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_FavouringName.value"),
								PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.issuingBank.isReadonly()) {
				this.issuingBank.setConstraint(new PTStringValidator(
						Labels.getLabel("label_DisbInstructionsDialog_IssuingBank.value"), null, true));
			}
			if (!this.payableLoc.isReadonly()) {
				this.payableLoc.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_PayableLoc.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
			if (!this.printingLoc.isReadonly()) {
				this.printingLoc.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_PrintingLoc.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
			if (!this.valueDate.isReadonly()) {
				Date appDate = SysParamUtil.getAppDate();
				Date todate = DateUtil.addMonths(appDate, 6);
				this.valueDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_DisbInstructionsDialog_ValueDate.value"), true, appDate, todate, true));
			}
		} else {
			if (!this.bankBranchID.isReadonly()) {
				this.bankBranchID.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_IFSC.value"), null, true));
			}
			if (!this.acctHolderName.isReadonly()) {
				this.acctHolderName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_AccountHolderName.value"),
								PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME, true));
			}
			if (!this.acctNumber.isReadonly()) {
				this.acctNumber.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_AccountNumber.value"),
								PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, minAccNoLength, maxAccNoLength));
			}
		}

		if (this.leiNumber.isVisible()) {
			this.leiNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_LEI.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.postDate);
		this.postDate.setConstraint("");
		this.favouringName.setConstraint("");
		this.paymentAmount.setConstraint("");
		this.acctNumber.setConstraint("");
		this.paymentType.setConstraint("");
		this.acctHolderName.setConstraint("");
		this.chequeOrDDumber.setConstraint("");
		this.remarks.setConstraint("");
		this.issuingBank.setConstraint("");
		this.payableLoc.setConstraint("");
		this.printingLoc.setConstraint("");
		this.valueDate.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.phoneNumber.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.postDate.setErrorMessage("");
		this.favouringName.setErrorMessage("");
		this.paymentAmount.setErrorMessage("");
		this.acctNumber.setErrorMessage("");
		this.paymentType.setErrorMessage("");
		this.acctHolderName.setErrorMessage("");
		this.chequeOrDDumber.setErrorMessage("");
		this.issuingBank.setErrorMessage("");
		this.payableLoc.setErrorMessage("");
		this.printingLoc.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.remarks.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.favouringName.setValue("");
		this.acctHolderName.setValue("");
		this.paymentAmount.setValue("");
		this.paymentType.setValue("");
		this.chequeOrDDumber.setValue("");
		this.postDate.setText("");
		this.remarks.setValue("");
		this.issuingBank.setValue("");
		this.bankBranchID.setValue("");
		this.payableLoc.setValue("");
		this.printingLoc.setValue("");
		this.bank.setValue("");
		this.branch.setValue("");
		this.city.setValue("");
		this.valueDate.setText("");

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = bankBranchID.getObject();

		if (dataObject instanceof String) {
			this.bankBranchID.setValue(dataObject.toString());
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.feeRefundInstruction.setBankBranchCode(details.getBankName());
				this.city.setValue(details.getPCCityName());
				this.branch.setValue(details.getBranchDesc());
				this.bankBranchID.setValue(details.getIFSC());
				if (StringUtils.isNotBlank(details.getBankCode())) {
					bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
					this.maxAccNoLength = this.bankDetail.getAccNoLength();
					this.minAccNoLength = this.bankDetail.getMinAccNoLength();
				}
				this.acctNumber.setMaxlength(maxAccNoLength);

			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$issuingBank(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = issuingBank.getObject();
		if (dataObject instanceof String) {
			this.issuingBank.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.issuingBank.setAttribute("issuingBank", details.getBankCode());
				this.issuingBank.setValue(details.getBankCode());
				this.issuingBank.setDescription(details.getBankName());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Change the partnerBankID for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$partnerBankID(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = partnerBankID.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.partnerBankID.setValue(dataObject.toString());
				this.partnerBankID.setDescription("");
			}
		} else {
			FinTypePartnerBank partnerBank = (FinTypePartnerBank) dataObject;
			if (partnerBank != null) {
				this.partnerBankID.setAttribute("partnerBankId", partnerBank.getPartnerBankID());
				this.feeRefundInstruction.setPartnerBankName(partnerBank.getPartnerBankName());
				this.feeRefundInstruction.setPartnerBankAc(partnerBank.getAccountNo());
				this.feeRefundInstruction.setPartnerBankAcType(partnerBank.getAccountType());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$paymentType(Event event) {
		String dType = this.paymentType.getSelectedItem().getValue().toString();
		this.partnerBankID.setButtonDisabled(false);
		this.partnerBankID.setReadonly(false);
		Filter[] filters = new Filter[5];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", AccountConstants.PARTNERSBANK_PAYMENT, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", dType, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[4] = new Filter("EntityCode", financeMain.getLovDescEntityCode(), Filter.OP_EQUAL);
		this.partnerBankID.setFilters(filters);
		this.partnerBankID.setConstraint("");
		this.partnerBankID.setErrorMessage("");
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");

		Object obj = this.partnerBankID.getAttribute("partnerBankId");
		if (obj != null) {
			this.partnerBankID.setValue(String.valueOf(obj));
		}

		checkPaymentType(dType);
	}

	public void checkPaymentType(String type) {
		if (StringUtils.isEmpty(type) || StringUtils.equals(type, PennantConstants.List_Select)) {
			gb_ChequeDetails.setVisible(false);
			gb_NeftDetails.setVisible(false);
			this.partnerBankID.setReadonly(true);
			this.partnerBankID.setValue("");
			this.partnerBankID.setDescription("");
			return;
		} else if (type.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| type.equals(DisbursementConstants.PAYMENT_TYPE_DD)) {
			doaddFilter(type);
			this.caption_FinAdvancePaymentsDialog_ChequeDetails.setLabel(this.paymentType.getSelectedItem().getLabel());
			gb_ChequeDetails.setVisible(true);
			gb_NeftDetails.setVisible(false);
			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
			this.acctNumber.setValue("");
			this.acctHolderName.setValue("");
			this.phoneNumber.setValue("");
			if (type.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)) {
				readOnlyComponent(isReadOnly("PaymentInstructionDialog_printingLoc"), this.printingLoc);
				this.madndatory_PrintingLoc.setSclass("mandatory");
			} else {
				this.printingLoc.setValue("");
				readOnlyComponent(true, this.printingLoc);
				this.madndatory_PrintingLoc.setSclass("");
			}
		} else {
			doaddFilter(type);
			this.caption_FinAdvancePaymentsDialog_NeftDetails.setLabel(this.paymentType.getSelectedItem().getLabel());
			gb_NeftDetails.setVisible(true);
			gb_ChequeDetails.setVisible(false);
			this.issuingBank.setValue("");
			this.issuingBank.setDescription("");
			this.favouringName.setValue("");
			this.payableLoc.setValue("");
			this.printingLoc.setValue("");
			this.valueDate.setText("");
			this.chequeOrDDumber.setValue("");
			if (type.equals(DisbursementConstants.PAYMENT_TYPE_IMPS)) {
				this.contactNumber.setSclass("mandatory");
			} else {
				this.contactNumber.setSclass("");
			}
		}
	}

	public void doaddFilter(String payMode) {
		Filter[] filters = new Filter[5];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", AccountConstants.PARTNERSBANK_PAYMENT, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", payMode, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[4] = new Filter("EntityCode", financeMain.getLovDescEntityCode(), Filter.OP_EQUAL);

		this.partnerBankID.setFilters(filters);

	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public void setFeeRefundInstruction(FeeRefundInstruction feeRefundInstruction) {
		this.feeRefundInstruction = feeRefundInstruction;
	}

	public void setFeeRefundHeader(FeeRefundHeader feeRefundHeader) {
		this.feeRefundHeader = feeRefundHeader;
	}
}
