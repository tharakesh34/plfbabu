package com.pennant.webui.payment.paymentheader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.payment.feerefundheader.FeeRefundHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PaymentInstructionDialogCtrl extends GFCBaseCtrl<PaymentInstruction> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PaymentInstructionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DisbursementsInstructionsDialog;

	protected Datebox postDate;
	protected Combobox paymentType;
	public CurrencyBox paymentAmount;
	protected ExtendedCombobox partnerBankID;
	protected Textbox remarks;
	protected Textbox tranReference;
	protected Textbox status;
	protected Textbox rejectReason;

	// IMPS Details
	protected ExtendedCombobox issuingBank;
	protected Textbox favouringName;
	protected Textbox payableLoc;
	protected ExtendedCombobox printingLoc;
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
	protected Space leiNum;
	protected Groupbox gb_statusDetails;
	protected Groupbox gb_ChequeDetails;
	protected Groupbox gb_NeftDetails;
	protected Component parentTabPanel = null;
	protected Tab tabDisbInstructions = null;

	protected Caption caption_FinAdvancePaymentsDialog_NeftDetails;
	protected Caption caption_FinAdvancePaymentsDialog_ChequeDetails;

	private transient BankDetailService bankDetailService;
	private PaymentInstruction paymentInstruction;
	private PaymentHeaderDialogCtrl paymentHeaderDialogCtrl;
	private FeeRefundHeaderDialogCtrl feeRefundHeaderDialogCtrl;
	private PaymentHeader paymentHeader;
	private FinanceMain financeMain;
	private FinTypePartnerBankService finTypePartnerBankService;
	private ClusterService clusterService;

	private int ccyFormatter;
	private int maxAccNoLength;
	private int minAccNoLength;
	private BankDetail bankDetail;

	/**
	 * default constructor.<br>
	 */
	public PaymentInstructionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PaymentInstructionDialog";
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
			if (arguments.containsKey("paymentInstruction")) {
				this.paymentInstruction = (PaymentInstruction) arguments.get("paymentInstruction");
				PaymentInstruction befImage = new PaymentInstruction();
				BeanUtils.copyProperties(this.paymentInstruction, befImage);
				this.paymentInstruction.setBefImage(befImage);
				setPaymentInstruction(this.paymentInstruction);
			} else {
				setPaymentInstruction(null);
			}

			this.enqiryModule = (Boolean) arguments.get("enqiryModule");

			if (arguments.containsKey("paymentHeaderDialogCtrl")) {
				setPaymentHeaderDialogCtrl((PaymentHeaderDialogCtrl) arguments.get("paymentHeaderDialogCtrl"));
				getPaymentHeaderDialogCtrl().setDisbursementInstructionsDialogCtrl(this);
			}

			if (arguments.containsKey("feeRefundHeaderDialogCtrl")) {
				setFeeRefundHeaderDialogCtrl((FeeRefundHeaderDialogCtrl) arguments.get("feeRefundHeaderDialogCtrl"));
				feeRefundHeaderDialogCtrl.setDisbursementInstructionsDialogCtrl(this);
			}

			if (arguments.containsKey("paymentHeader")) {
				setPaymentHeader((PaymentHeader) arguments.get("paymentHeader"));
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
			doLoadWorkFlow(this.paymentHeader.isWorkflow(), this.paymentHeader.getWorkflowId(),
					this.paymentHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.paymentInstruction);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public PaymentInstruction onSave() {
		logger.debug("Entering");

		doSetValidation();
		PaymentInstruction paymentInstruction = doWriteComponentsToBean(this.paymentInstruction);

		logger.debug("Entering");

		return paymentInstruction;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param paymentInstruction
	 * @throws InterruptedException
	 */
	public void doShowDialog(PaymentInstruction paymentInstruction) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled() && !this.enqiryModule) {
			doEdit();
		} else {
			doReadOnly();
		}
		// fill the components with the data
		doWriteBeanToComponents(paymentInstruction);

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

		this.paymentAmount.setDisabled(true/* isReadOnly("PaymentInstructionDialog_paymentAmount") */);
		this.postDate.setDisabled(isReadOnly("PaymentInstructionDialog_postDate"));
		this.paymentType.setDisabled(isReadOnly("PaymentInstructionDialog_paymentType"));
		this.remarks.setReadonly(isReadOnly("PaymentInstructionDialog_remarks"));
		this.bankBranchID.setReadonly(isReadOnly("PaymentInstructionDialog_bankBranchID"));
		this.acctNumber.setReadonly(isReadOnly("PaymentInstructionDialog_acctNumber"));
		this.acctHolderName.setReadonly(isReadOnly("PaymentInstructionDialog_acctHolderName"));
		this.phoneNumber.setReadonly(isReadOnly("PaymentInstructionDialog_phoneNumber"));
		this.issuingBank.setReadonly(true);
		this.favouringName.setReadonly(isReadOnly("PaymentInstructionDialog_favouringName"));
		this.chequeOrDDumber.setReadonly(isReadOnly("PaymentInstructionDialog_chequeOrDDumber"));
		this.payableLoc.setReadonly(isReadOnly("PaymentInstructionDialog_payableLoc"));
		this.printingLoc.setReadonly(isReadOnly("PaymentInstructionDialog_printingLoc"));
		this.valueDate.setDisabled(isReadOnly("PaymentInstructionDialog_valueDate"));
		this.partnerBankID.setReadonly(isReadOnly("PaymentInstructionDialog_partnerBankID"));
		this.leiNumber.setReadonly(isReadOnly("PaymentInstructionDialog_leiNumber"));

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || this.paymentHeader.isNewRecord()) {
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
		this.printingLoc.setReadonly(true);
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

		this.printingLoc.setModuleName("BankBranch");
		this.printingLoc.setMandatoryStyle(true);
		this.printingLoc.setValueColumn("BranchCode");
		this.printingLoc.setDescColumn("BranchDesc");
		this.printingLoc.setValidateColumns(new String[] { "BranchCode" });

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
		this.issuingBank.setReadonly(true);

		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");
		if (this.paymentInstruction.getPaymentAmount().compareTo(FinanceConstants.LEI_NUM_LIMIT) > 0) {
			this.leiNum.setClass("mandatory");
		} else {
			this.leiNum.setClass("");
		}

		if (StringUtils.isNotBlank(this.paymentInstruction.getBranchBankCode())) {
			bankDetail = bankDetailService.getAccNoLengthByCode(this.paymentInstruction.getBranchBankCode());
			this.maxAccNoLength = this.bankDetail.getAccNoLength();
			this.minAccNoLength = this.bankDetail.getMinAccNoLength();
		}
		this.acctNumber.setMaxlength(maxAccNoLength);

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(PaymentInstruction pi) {
		logger.debug(Literal.ENTERING);

		if (this.paymentHeader.isNewRecord() && pi.getPostDate() == null) {
			this.postDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.postDate.setValue(pi.getPostDate());
		}

		fillComboBox(this.paymentType, pi.getPaymentType(), PennantStaticListUtil.getPaymentTypes(), "");

		if (pi.getPartnerBankId() != Long.MIN_VALUE && pi.getPartnerBankId() != 0) {
			this.partnerBankID.setReadonly(isReadOnly("PaymentInstructionDialog_partnerBankID"));
			this.partnerBankID.setAttribute("partnerBankId", pi.getPartnerBankId());
			this.partnerBankID.setValue(pi.getPartnerBankCode(), pi.getPartnerBankName());
		}

		this.issuingBank.setReadonly(true);
		this.issuingBank.setAttribute("issuingBank", pi.getIssuingBank());
		this.issuingBank.setValue(pi.getIssuingBank(), pi.getIssuingBankName());

		this.paymentAmount.setValue(
				PennantApplicationUtil.formateAmount(this.paymentInstruction.getPaymentAmount(), ccyFormatter));
		this.remarks.setValue(pi.getRemarks());
		this.tranReference.setValue(pi.getTransactionRef());
		this.status.setValue(pi.getStatus());
		this.rejectReason.setValue(pi.getRejectReason());

		if (pi.getBankBranchId() != Long.MIN_VALUE && pi.getBankBranchId() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", pi.getBankBranchId());
			this.bankBranchID.setValue(pi.getBankBranchIFSC(), pi.getBankBranchCode());
			this.bank.setValue(StringUtils.trimToEmpty(pi.getBankName()));
			this.branch.setValue(pi.getBranchDesc());
			this.city.setValue(StringUtils.trimToEmpty(pi.getpCCityName()));
		}

		this.acctNumber.setValue(pi.getAccountNo());
		this.acctHolderName.setValue(pi.getAcctHolderName());
		this.phoneNumber.setValue(pi.getPhoneNumber());

		this.chequeOrDDumber.setValue(pi.getFavourNumber());
		this.favouringName.setValue(pi.getFavourName());
		this.payableLoc.setValue(pi.getPayableLoc());
		this.printingLoc.setValue(pi.getPrintingLoc());
		this.printingLoc.setDescription(pi.getPrintingLocDesc());
		this.valueDate.setValue(pi.getValueDate());
		this.leiNumber.setValue(pi.getLei());
		checkPaymentType(pi.getPaymentType());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param pi
	 * @throws InterruptedException
	 */
	public PaymentInstruction doWriteComponentsToBean(PaymentInstruction pi) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		if (this.paymentHeader.isNewRecord()) {
			pi.setStatus(DisbursementConstants.STATUS_NEW);
		}

		try {
			Date appDate = SysParamUtil.getAppDate();
			if (DateUtil.compare(this.postDate.getValue(), appDate) < 0 && !postDate.isDisabled()) {
				throw new WrongValueException(this.postDate,
						"Payment Date should be greater than or equal to :" + appDate);
			}
			pi.setPostDate(this.postDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setPaymentType(this.paymentType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setPaymentAmount(
					PennantApplicationUtil.unFormateAmount(this.paymentAmount.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.partnerBankID.getValidatedValue();
			Object obj = (Object) this.partnerBankID.getObject();

			if (obj != null) {
				FinTypePartnerBank ftpb = (FinTypePartnerBank) obj;
				pi.setPartnerBankId(ftpb.getPartnerBankID());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setAccountNo(this.acctNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setAcctHolderName(this.acctHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setFavourNumber(this.chequeOrDDumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setFavourName(this.favouringName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.issuingBank.getValidatedValue();
			Object obj = this.issuingBank.getAttribute("issuingBank");
			if (obj != null) {
				pi.setIssuingBank(String.valueOf(obj));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setPayableLoc(this.payableLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			boolean mandatory = false;
			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
				mandatory = true;
			}

			this.printingLoc.clearErrorMessage();

			this.printingLoc.setErrorMessage("");

			if (!this.printingLoc.isReadonly()) {
				this.printingLoc.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_PrintingLoc.value"), null, mandatory));
			}
			pi.setPrintingLoc(this.printingLoc.getValue());
			pi.setPrintingLocDesc(this.printingLoc.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.valueDate.getValue() != null) {
				pi.setValueDate(this.valueDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				pi.setBankBranchId(Long.valueOf(String.valueOf(obj)));
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
			pi.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setPaymentCCy(financeMain.getFinCcy());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			pi.setLei(this.leiNumber.getValue());
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
		return pi;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.paymentType.isDisabled()) {
			this.paymentType
					.setConstraint(new PTListValidator(Labels.getLabel("label_DisbInstructionsDialog_DisbType.value"),
							PennantStaticListUtil.getPaymentTypesWithIST(), true));
		}

		if (this.paymentAmount.isDisabled()) {
			this.paymentAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_DisbInstructionsDialog_DisbAmount.value"), ccyFormatter, true, false));
		}

		/*
		 * if (!this.partnerBankID.isReadonly()) { this.partnerBankID.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_DisbInstructionsDialog_Partnerbank.value"), null, true)); }
		 */

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
			if (!this.valueDate.isDisabled()) {
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

		if (this.leiNumber.isVisible() && !this.leiNumber.getValue().isEmpty()) {
			this.leiNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_LEI.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		if (paymentHeaderDialogCtrl.leiMandatory && this.leiNumber.getValue().isEmpty()) {
			this.leiNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DisbInstructionsDialog_LEI.value"), null, true));
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
		this.leiNumber.setConstraint("");

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
		logger.debug(Literal.ENTERING);

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
				this.paymentInstruction.setBankBranchCode(details.getBankName());
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

			doSetPartnerBank();
			logger.debug(Literal.LEAVING);
			return;
		}

		FinTypePartnerBank ftpb = (FinTypePartnerBank) dataObject;
		if (ftpb != null) {

			setFilters(ftpb, null);

			ftpb = finTypePartnerBankService.getFinTypePartnerBank(ftpb);
		}

		if (ftpb != null) {
			doSetPartnerBank(ftpb);
		} else {
			doSetPartnerBank();
		}

		this.partnerBankID.setAttribute("partnerBankId", ftpb.getPartnerBankID());
		this.paymentInstruction.setPartnerBankName(ftpb.getPartnerBankName());
		this.paymentInstruction.setPartnerBankAc(ftpb.getAccountNo());
		this.paymentInstruction.setPartnerBankAcType(ftpb.getAccountType());

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$issuingBank(Event event) {
		logger.debug(Literal.ENTERING);

		this.printingLoc.setValue("");
		Object dataObject = issuingBank.getObject();
		String paymentType = getComboboxValue(this.paymentType);

		if (dataObject instanceof String) {
			this.issuingBank.setValue(dataObject.toString());

			logger.debug(Literal.LEAVING);

			return;
		}

		BankDetail details = (BankDetail) dataObject;
		if (details != null) {
			this.issuingBank.setAttribute("bankCode", details.getBankCode());
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("BankCode", ((BankDetail) dataObject).getBankCode(), Filter.OP_EQUAL);
			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)) {
				filters[1] = new Filter("Cheque", true, Filter.OP_EQUAL);
			}
			if (DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
				filters[1] = new Filter("DD", true, Filter.OP_EQUAL);
			}
			this.printingLoc.setFilters(filters);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$paymentType(Event event) {
		String sPaymentType = this.paymentType.getSelectedItem().getValue().toString();

		doSetPartnerBank();

		if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
			doAddBranchWiseFilter(sPaymentType);
			checkPaymentType(sPaymentType);
			return;
		}

		Filter[] filters = new Filter[5];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", AccountConstants.PARTNERSBANK_PAYMENT, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", sPaymentType, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[4] = new Filter("EntityCode", financeMain.getLovDescEntityCode(), Filter.OP_EQUAL);

		this.partnerBankID.setFilters(filters);
		this.partnerBankID.setConstraint("");
		this.partnerBankID.setErrorMessage("");
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");

		checkPaymentType(sPaymentType);
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

			if (type.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
					|| type.equals(DisbursementConstants.PAYMENT_TYPE_DD)) {
				readOnlyComponent(isReadOnly("PaymentInstructionDialog_printingLoc"), this.printingLoc);
				this.printingLoc.setMandatoryStyle(true);
			} else {
				this.printingLoc.setSclass("");
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

		Filter[] filtersPrintLoc = new Filter[2];
		filtersPrintLoc[0] = new Filter("BankCode", issuingBank.getValue(), Filter.OP_EQUAL);
		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(type)) {
			filtersPrintLoc[1] = new Filter("Cheque", true, Filter.OP_EQUAL);
		}

		if (DisbursementConstants.PAYMENT_TYPE_DD.equals(type)) {
			filtersPrintLoc[1] = new Filter("DD", true, Filter.OP_EQUAL);
		}

		this.printingLoc.setFilters(filtersPrintLoc);

	}

	private void doSetPartnerBank(FinTypePartnerBank fpb) {
		this.partnerBankID.setAttribute("bankBranchID", fpb.getPartnerBankID());
		// this.partnerBankID.setAttribute("partnerBankAc", fpb.getAccountNo());
		// this.partnerBankID.setAttribute("partnerBankAcType", fpb.getAccountType());
		this.partnerBankID.setValue(fpb.getPartnerBankCode());
		this.partnerBankID.setDescription(fpb.getPartnerBankName());
		this.partnerBankID.setReadonly(true);

		this.issuingBank.setValue(fpb.getIssuingBankCode());
		this.issuingBank.setDescription(fpb.getIssuingBankName());

		this.printingLoc.setValue(fpb.getPrintingLoc());
		this.printingLoc.setDescription(fpb.getPrintingLocDesc());

		this.payableLoc.setValue(fpb.getPayableLoc());

		this.favouringName.setValue(fpb.getFavourName());

	}

	private void doSetPartnerBank() {
		this.partnerBankID.setAttribute("bankBranchID", null);
		// this.partnerBankID.setAttribute("partnerBankAc", fpb.getAccountNo());
		// this.partnerBankID.setAttribute("partnerBankAcType", fpb.getAccountType());
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");
		this.partnerBankID.setReadonly(false);

		this.issuingBank.setValue("");
		this.issuingBank.setDescription("");

		this.printingLoc.setValue("");
		this.printingLoc.setDescription("");

		this.payableLoc.setValue("");
	}

	private void setFilters(FinTypePartnerBank fpb, Filter[] filters) {
		long finID = financeMain.getFinID();
		String finType = financeMain.getFinType();
		String finBranch = financeMain.getFinBranch();
		String paymentMode = this.paymentType.getSelectedItem().getValue().toString();

		Long clusterId = null;
		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(finBranch);
		}

		if (filters != null) {
			if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
				filters[3] = new Filter("BranchCode", finBranch, Filter.OP_EQUAL);
			} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
				filters[3] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
			}
		}

		fpb.setFinID(finID);
		fpb.setFinType(finType);
		fpb.setPurpose(AccountConstants.PARTNERSBANK_PAYMENT);
		fpb.setPaymentMode(paymentMode);
		fpb.setBranchCode(finBranch);
		fpb.setClusterId(clusterId);

	}

	private void doAddBranchWiseFilter(String payMode) {
		Filter[] filters = new Filter[4];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", AccountConstants.PARTNERSBANK_PAYMENT, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", payMode, Filter.OP_EQUAL);

		FinTypePartnerBank fpb = new FinTypePartnerBank();
		setFilters(fpb, filters);

		List<FinTypePartnerBank> fintypePartnerbank = finTypePartnerBankService.getFinTypePartnerBanks(fpb);

		if (fintypePartnerbank.size() == 1) {
			fpb = fintypePartnerbank.get(0);
			doSetPartnerBank(fpb);
		} else if (fintypePartnerbank.size() > 1) {
			doSetPartnerBank();
		}

		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(payMode)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(payMode)) {

			if (valueDate.getValue() == null) {
				this.valueDate.setValue(SysParamUtil.getAppDate());
			}

		}

		this.partnerBankID.setFilters(filters);
	}

	public void doaddFilter(String payMode) {
		if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
			doAddBranchWiseFilter(payMode);
			return;
		}

		Filter[] filters = new Filter[5];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", AccountConstants.PARTNERSBANK_PAYMENT, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", payMode, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[4] = new Filter("EntityCode", financeMain.getLovDescEntityCode(), Filter.OP_EQUAL);

		this.partnerBankID.setFilters(filters);

	}

	public void setPaymentInstruction(PaymentInstruction paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public PaymentHeaderDialogCtrl getPaymentHeaderDialogCtrl() {
		return paymentHeaderDialogCtrl;
	}

	public void setPaymentHeaderDialogCtrl(PaymentHeaderDialogCtrl paymentHeaderDialogCtrl) {
		this.paymentHeaderDialogCtrl = paymentHeaderDialogCtrl;
	}

	public void setFeeRefundHeaderDialogCtrl(FeeRefundHeaderDialogCtrl feeRefundHeaderDialogCtrl) {
		this.feeRefundHeaderDialogCtrl = feeRefundHeaderDialogCtrl;
	}

	public PaymentHeader getPaymentHeader() {
		return paymentHeader;
	}

	public void setPaymentHeader(PaymentHeader paymentHeader) {
		this.paymentHeader = paymentHeader;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

}
