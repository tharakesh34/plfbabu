package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinInsuranceDialogCtrl extends GFCBaseCtrl<FinInsurances> {
	private static final long		serialVersionUID	= -6945930303723518608L;
	private static final Logger		logger				= Logger.getLogger(FinInsuranceDialogCtrl.class);

	protected Window				window_FinInsurnaceDialog;
	protected ExtendedCombobox		insuranceType;
	protected ExtendedCombobox		insurancePolicy;
	protected Checkbox				insuranceReq;
	protected ExtendedCombobox		provider;
	protected Combobox				paymentMethod;
	protected Space					space_paymentmethod;
	protected Combobox				calcType;
	protected Decimalbox			insuranceRate;
	protected Uppercasebox			insReference;
	protected Combobox				waiverReason;
	protected Combobox				insuranceStatus;
	protected FrequencyBox			insuranceFrq;
	protected CurrencyBox			amount;
	protected ExtendedCombobox		calRule;
	protected Combobox				calOn;
	protected Decimalbox			calPercentage;

	private FinInsurances			finInsurance;
	private FinFeeDetailListCtrl	finFeeDetailListCtrl;
	private String					userRole;
	private int						amountFormatter;
	private List<FinInsurances>		finInsuranceList;

	protected Row					row_Provider;
	protected Row					row_Amount;
	protected Row					row_InsuranceFrq;
	protected Row					row_waiverReason;
	protected Row					row_calRule;
	protected Row					row_CalculaedOn;
	protected Row					row_Percentage;
	protected Row					row_calcType;

	protected Label					label_FinInsuranceDialog_PaymentMethod;

	//Old Variables
	private boolean					isWIF				= false;

	/**
	 * default constructor.<br>
	 */
	public FinInsuranceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinInsuranceDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_FinInsurnaceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinInsurnaceDialog);
		if (arguments.containsKey("finInsurance")) {
			this.finInsurance = (FinInsurances) arguments.get("finInsurance");
			FinInsurances befImage = new FinInsurances();
			BeanUtils.copyProperties(this.finInsurance, befImage);
			this.finInsurance.setBefImage(befImage);
			setFinInsurance(this.finInsurance);

		} else {
			setFinInsurance(null);
		}
		this.finInsurance.setWorkflowId(0);
		doLoadWorkFlow(this.finInsurance.isWorkflow(), this.finInsurance.getWorkflowId(),
				this.finInsurance.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FinInsuranceDialog");
		}

		if (arguments.containsKey("role")) {
			userRole = arguments.get("role").toString();
			getUserWorkspace().allocateRoleAuthorities(arguments.get("role").toString(), "FinInsuranceDialog");
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		doCheckRights();
		// READ OVERHANDED params !
		// we get the transactionEntryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete transactionEntry here.
		if (arguments.containsKey("finFeeDetailListCtrl")) {
			setFinFeeDetailListCtrl((FinFeeDetailListCtrl) arguments.get("finFeeDetailListCtrl"));
			amountFormatter = CurrencyUtil.getFormat(getFinFeeDetailListCtrl().getFinanceMain().getFinCcy());
		} else {
			setFinFeeDetailListCtrl(null);
		}

		//set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinInsurance());
		logger.debug("Leaving");

	}

	private void doShowDialog(FinInsurances aFinInsurance) throws InterruptedException, ParseException {

		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinInsurance.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.insurancePolicy.focus();

		} else {
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinInsuranceDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinInsurance);
			dosetFieldVisibility(aFinInsurance.isInsuranceReq());

			this.window_FinInsurnaceDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	private void doEdit() {
		logger.debug("Entering");
		if (!isWIF) {
			if (getFinInsurance().isNewRecord()) {
				this.space_paymentmethod.setSclass("mandatory");
				this.insReference.setReadonly(isReadOnly("FinInsuranceDialog_insReference"));
				this.insurancePolicy.setReadonly(isReadOnly("FinInsuranceDialog_InsuranceType"));

			} else {
				this.insReference.setReadonly(true);
				this.insurancePolicy.setReadonly(true);
				this.insurancePolicy.setTextBoxWidth(175);
			}

			readOnlyComponent(isReadOnly("FinInsuranceDialog_insuranceReq"), this.insuranceReq);
			this.paymentMethod.setDisabled(isReadOnly("FinInsuranceDialog_paymentMethod"));
			this.insuranceRate.setReadonly(isReadOnly("FinInsuranceDialog_providerRate"));
			this.amount.setReadonly(isReadOnly("FinInsuranceDialog_amount"));
			this.insuranceFrq.setDisabled(isReadOnly("FinInsuranceDialog_insuranceFrq"));
			this.waiverReason.setDisabled(isReadOnly("FinInsuranceDialog_waiverReason"));

			this.calcType.setDisabled(isReadOnly("FinInsuranceDialog_calcType"));
			this.calPercentage.setReadonly(isReadOnly("FinInsuranceDialog_percentage"));
			this.calOn.setDisabled(isReadOnly("FinInsuranceDialog_calculatedOn"));
			this.insuranceStatus.setDisabled(isReadOnly("FinInsuranceDialog_InsuranceStatus"));
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finInsurance.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving");

	}

	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	private void doWriteComponentsToBean(FinInsurances aFinInsurance) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (!this.insuranceReq.isChecked()) {
			try {
				aFinInsurance.setPolicyCode(this.insurancePolicy.getValue());
				aFinInsurance.setPolicyDesc(this.insurancePolicy.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinInsurance.setInsuranceType(this.insuranceType.getValue());
				aFinInsurance.setInsuranceTypeDesc(this.insuranceType.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinInsurance.setInsReference(this.insReference.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {

				if ("#".equals(getComboboxValue(this.waiverReason))) {
					throw new WrongValueException(this.waiverReason, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinInsuranceDialog_WaiverReason.value") }));
				} else {
					aFinInsurance.setWaiverReason(getComboboxValue(this.waiverReason));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

		} else {

			try {
				aFinInsurance.setPolicyCode(this.insurancePolicy.getValue());
				aFinInsurance.setPolicyDesc(this.insurancePolicy.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinInsurance.setInsuranceType(this.insuranceType.getValue());
				aFinInsurance.setInsuranceTypeDesc(this.insuranceType.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinInsurance.setInsReference(this.insReference.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinInsurance.setProvider(this.provider.getValue());
				aFinInsurance.setProviderName(this.provider.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinInsurance.setCalType(this.calcType.getSelectedItem().getValue().toString());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {

				aFinInsurance.setPaymentMethod(this.paymentMethod.getSelectedItem().getValue().toString());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinInsurance.setInsuranceRate(this.insuranceRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.row_InsuranceFrq.isVisible() && this.insuranceFrq.isValidComboValue()) {
					aFinInsurance.setInsuranceFrq(this.insuranceFrq.getValue() == null ? "" : this.insuranceFrq
							.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.row_Amount.isVisible()) {
					aFinInsurance.setAmount(PennantAppUtil.unFormateAmount(
							this.amount.isReadonly() ? this.amount.getActualValue() : this.amount.getValidateValue(),
							amountFormatter));
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinInsurance.setCalRule(this.calRule.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinInsurance.setCalPerc(this.calPercentage.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {

				aFinInsurance.setCalOn(this.calOn.getSelectedItem().getValue().toString());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			aFinInsurance.setAlwRateChange(this.insuranceRate.isReadonly());

		}
		try {
			aFinInsurance.setInsuranceStatus(this.insuranceStatus.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinInsurance.setInsuranceReq(this.insuranceReq.isChecked());

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinInsurance.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");

	}

	public void onCheck$insuranceReq(Event event) {
		dosetFieldVisibility(this.insuranceReq.isChecked());
	}

	public void dosetFieldVisibility(boolean insreqd) {
		if (insreqd) {
			if (this.calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_CON_AMT)) {
				this.insuranceRate.setReadonly(true);
				this.row_Amount.setVisible(true);
				this.amount.setReadonly(getFinInsurance().isAlwRateChange() || isReadOnly("FinInsuranceDialog_amount"));
			} else if (this.calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
				this.row_Percentage.setVisible(true);
				this.calPercentage.setReadonly(getFinInsurance().isAlwRateChange()
						|| isReadOnly("FinInsuranceDialog_percentage"));
				this.insuranceRate.setReadonly(true);
				this.row_CalculaedOn.setVisible(true);
			} else if (this.calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
				this.row_CalculaedOn.setVisible(true);
				this.insuranceRate.setReadonly(getFinInsurance().isAlwRateChange()
						|| isReadOnly("FinInsuranceDialog_providerRate"));
			} else if (this.calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_RULE)) {
				this.row_calRule.setVisible(true);
			}
			this.row_calcType.setVisible(true);
			this.row_Provider.setVisible(true);
			this.row_waiverReason.setVisible(false);
			this.label_FinInsuranceDialog_PaymentMethod.setVisible(true);
			this.paymentMethod.setVisible(true);
			if (this.paymentMethod.getSelectedItem() != null
					&& this.paymentMethod.getSelectedItem().equals(InsuranceConstants.PAYTYPE_SCH_FRQ)) {
				this.row_InsuranceFrq.setVisible(true);
			}
			this.space_paymentmethod.setSclass("mandatory");
		} else {
			this.row_Amount.setVisible(false);
			this.row_InsuranceFrq.setVisible(false);
			this.row_Provider.setVisible(false);
			this.row_waiverReason.setVisible(true);
			this.label_FinInsuranceDialog_PaymentMethod.setVisible(false);
			this.paymentMethod.setVisible(false);
			this.paymentMethod.setVisible(false);
			this.row_calcType.setVisible(false);
			this.space_paymentmethod.setSclass("");
			this.row_Percentage.setVisible(false);
			this.row_CalculaedOn.setVisible(false);

		}

	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.insuranceType.setConstraint("");
		this.provider.setConstraint("");
		this.calcType.setConstraint("");
		this.paymentMethod.setConstraint("");
		this.insuranceRate.setConstraint("");
		this.insReference.setConstraint("");
		this.waiverReason.setConstraint("");

		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.insuranceType.setErrorMessage("");
		this.provider.setErrorMessage("");
		this.calcType.setErrorMessage("");
		this.paymentMethod.setErrorMessage("");
		this.insuranceRate.setErrorMessage("");
		this.insReference.setErrorMessage("");
		this.waiverReason.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doWriteBeanToComponents(FinInsurances aFinInsurance) {
		logger.debug("Entering");
		this.insuranceType.setValue(aFinInsurance.getInsuranceType(), aFinInsurance.getInsuranceTypeDesc());
		this.insurancePolicy.setValue(aFinInsurance.getPolicyCode(), aFinInsurance.getPolicyDesc());
		if (!aFinInsurance.isNew()) {
			doSetFilterProviderType();
		}
		this.provider.setValue(aFinInsurance.getProvider(), aFinInsurance.getProviderName());
		
		String excldMthds = "";
		if(StringUtils.equals(aFinInsurance.getCalType(), InsuranceConstants.CALTYPE_RULE)){
			excldMthds = ","+InsuranceConstants.PAYTYPE_SCH_FRQ+",";
		}
		fillComboBox(this.paymentMethod, aFinInsurance.getPaymentMethod(),
				PennantStaticListUtil.getInsurancePaymentType(),excldMthds);
		fillComboBox(this.calcType, aFinInsurance.getCalType(), PennantStaticListUtil.getInsuranceCalType(), "");
		this.insuranceRate.setValue(aFinInsurance.getInsuranceRate());
		this.insReference.setValue(aFinInsurance.getInsReference());
		fillComboBox(this.waiverReason, aFinInsurance.getWaiverReason(),
				PennantStaticListUtil.getInsWaiverReasonList(), "");
		fillComboBox(this.insuranceStatus, aFinInsurance.getInsuranceStatus(),
				PennantStaticListUtil.getInsStatusList(), "");

		if (aFinInsurance.isNew()) {
			this.insuranceReq.setChecked(true);
		} else {
			this.insuranceReq.setChecked(aFinInsurance.isInsuranceReq());
		}

		this.amount.setValue(PennantAppUtil.formateAmount(aFinInsurance.getAmount(), amountFormatter));
		this.calRule.setValue(aFinInsurance.getCalRule());
		this.calPercentage.setValue(aFinInsurance.getCalPerc());

		fillComboBox(this.calOn, aFinInsurance.getCalOn(), PennantStaticListUtil.getInsuranceCalculatedOn(), "");

		if (aFinInsurance.isNew()) {
			this.insuranceFrq.setValue(InsuranceConstants.DEFAULT_FRQ);
		} else {
			if (StringUtils.equals(aFinInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {
				this.row_InsuranceFrq.setVisible(true);
				this.insuranceFrq.setValue(aFinInsurance.getInsuranceFrq());
			}
		}

		logger.debug("Leaving");

	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinInsurances aFinInsurance = new FinInsurances();
		BeanUtils.copyProperties(getFinInsurance(), aFinInsurance);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();

		// fill the TransactionEntry object with the components data
		doWriteComponentsToBean(aFinInsurance);

		// Write the additional validations as per below example
		// get the selected branch object from the lisBox
		// Do data level validations here

		isNew = aFinInsurance.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinInsurance.getRecordType())) {
				aFinInsurance.setVersion(aFinInsurance.getVersion() + 1);
				if (isNew) {
					aFinInsurance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinInsurance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinInsurance.setNewRecord(true);
				}
			}
		} else {

			if (isNew) {
				aFinInsurance.setVersion(1);
				aFinInsurance.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aFinInsurance.getRecordType())) {
				aFinInsurance.setVersion(aFinInsurance.getVersion() + 1);
				aFinInsurance.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aFinInsurance.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newFinInsurnaceEntryProcess(aFinInsurance, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinInsurnaceDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinFeeDetailListCtrl().doFillFinInsurances(this.finInsuranceList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private void doSetValidation() {

		if (!this.insurancePolicy.isReadonly()) {
			this.insurancePolicy.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinInsuranceDialog_InsurancePolicy.value"), null, true, true));
		}
		if (!this.insuranceType.isReadonly()) {
			this.insuranceType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinInsuranceDialog_InsuranceType.value"), null, true, true));
		}
		if (this.row_Provider.isVisible() && !this.provider.isReadonly()) {
			this.provider.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinInsuranceDialog_Provider.value"), null, true, true));
		}

		if (this.paymentMethod.isVisible() && !this.paymentMethod.isDisabled()) {
			this.paymentMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getInsurancePaymentType(),
					Labels.getLabel("label_FinInsuranceDialog_PaymentMethod.value")));
		}

		if (this.row_Provider.isVisible() && !this.insuranceRate.isReadonly()) {
			this.insuranceRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinInsuranceDialog_InsuranceRate.value"), 9, true));

		}
		if (!this.insReference.isReadonly()) {

			this.insReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinInsuranceDialog_Reference.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.insuranceReq.isChecked() && !this.waiverReason.isReadonly()) {
			this.waiverReason.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinInsuranceDialog_WaiverReason.value"), null, true));
		}
		if (this.row_Amount.isVisible() && !this.amount.isReadonly()) {
			this.amount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinInsuranceDialog_Amount.value"),
					amountFormatter, true, false));
		}

	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinInsurnaceDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	private AuditHeader newFinInsurnaceEntryProcess(FinInsurances aFinInsurance, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFinInsurance, tranType);
		finInsuranceList = new ArrayList<FinInsurances>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aFinInsurance.getInsuranceType();
		valueParm[1] = aFinInsurance.getInsReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinInsuranceDialog_InsuranceType.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("listheader_FeeDetailDialog_Reference.label") + ":" + valueParm[1];
		List<FinInsurances> list = getFinFeeDetailListCtrl().getFinInsuranceList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				FinInsurances finInsurance = list.get(i);
				if (finInsurance.getInsuranceType().equals(aFinInsurance.getInsuranceType())
						&& finInsurance.getInsReference().equals(aFinInsurance.getInsReference())) {
					// Both Current and Existing list rating same
					if (aFinInsurance.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aFinInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aFinInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finInsuranceList.add(aFinInsurance);
						} else if (aFinInsurance.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aFinInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aFinInsurance.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finInsuranceList.add(aFinInsurance);
						} else if (aFinInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							List<FinInsurances> savedList = getFinFeeDetailListCtrl().getFinInsuranceList();
							for (int j = 0; j < savedList.size(); j++) {
								FinInsurances finIns = savedList.get(j);
								if (finIns.getInsuranceType().equals(finIns.getInsuranceType())) {
									finInsuranceList.add(finIns);
								}
							}
						} else if (aFinInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aFinInsurance.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							finInsuranceList.add(finInsurance);
						}
					}
				} else {
					finInsuranceList.add(finInsurance);
				}
			}
		}
		if (!recordAdded) {
			finInsuranceList.add(aFinInsurance);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditHeader(FinInsurances aFinInsurance, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinInsurance.getBefImage(), aFinInsurance);
		return new AuditHeader(aFinInsurance.getInsuranceType(), null, null, null, auditDetail,
				aFinInsurance.getUserDetails(), getOverideMap());
	}

	private void doSetFieldProperties() {

		this.insurancePolicy.setMandatoryStyle(true);
		this.insurancePolicy.setModuleName("FinTypeInsurances");
		this.insurancePolicy.setValueColumn("PolicyType");
		this.insurancePolicy.setDescColumn("PolicyDesc");
		this.insurancePolicy.setTextBoxWidth(144);
		this.insurancePolicy.setValidateColumns(new String[] { "PolicyType" });

		Filter filter[] = new Filter[2];
		filter[0] = new Filter("FinType", getFinFeeDetailListCtrl().getFinanceMain().getFinType(), Filter.OP_EQUAL);
		filter[1] = new Filter("RecordStatus", PennantConstants.RCD_STATUS_APPROVED, Filter.OP_EQUAL);
		this.insurancePolicy.setFilters(filter);

		this.insuranceType.setTextBoxWidth(175);
		this.insuranceType.setReadonly(true);

		this.provider.setTextBoxWidth(175);
		this.provider.setReadonly(true);

		this.calRule.setTextBoxWidth(144);
		this.calRule.getTextbox().setReadonly(true);
		this.calRule.getButton().setDisabled(true);

		this.insuranceRate.setMaxlength(13);
		this.insuranceRate.setFormat(PennantConstants.rateFormate9);
		this.insuranceRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.insuranceRate.setScale(9);

		this.amount.setMandatory(true);
		this.amount.setFormat(PennantApplicationUtil.getAmountFormate(amountFormatter));
		this.amount.setScale(amountFormatter);

		this.insuranceFrq.setMandatoryStyle(true);

		this.calcType.setDisabled(true);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

	}

	private void doSetFilterProviderType() {
		Filter filter[] = new Filter[2];
		if (this.insuranceType.getValue() != null) {
			filter[0] = new Filter("InsuranceType", this.insuranceType.getValue(), Filter.OP_EQUAL);
			filter[1] = new Filter("RecordStatus", PennantConstants.RCD_STATUS_APPROVED, Filter.OP_EQUAL);
			this.provider.setValue("");
			this.provider.setDescription("");
			this.provider.setFilters(filter);
			this.insuranceRate.setValue(BigDecimal.ZERO);
		}
	}

	public void onFulfill$provider(Event event) throws InterruptedException {
		logger.debug("Entering");
		Object dataObject = provider.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.provider.setValue(dataObject.toString());
				this.provider.setDescription("");
			}
			this.insuranceRate.setValue(BigDecimal.ZERO);
		} else {
			InsuranceTypeProvider detail = (InsuranceTypeProvider) dataObject;
			if (detail != null) {
				this.insuranceRate.setValue(detail.getInsuranceRate());
			}
		}
		logger.debug("Leaving");

	}

	public void onFulfill$insurancePolicy(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = insurancePolicy.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.insurancePolicy.setValue(dataObject.toString());
				this.insurancePolicy.setDescription("");

			}
			this.insuranceType.setValue("", "");
			this.provider.setValue("", "");
			this.insuranceRate.setText("");
			this.insuranceRate.setReadonly(true);
			this.row_InsuranceFrq.setVisible(false);
			this.row_calRule.setVisible(false);
			this.row_Percentage.setVisible(false);
			this.row_CalculaedOn.setVisible(false);
			this.row_Amount.setVisible(false);
			this.paymentMethod.setSelectedIndex(0);
			this.calcType.setSelectedIndex(0);
		} else {

			FinTypeInsurances detail = (FinTypeInsurances) dataObject;
			if (detail != null) {
				this.insuranceType.setValue(detail.getInsuranceType(), detail.getInsuranceTypeDesc());
				this.provider.setValue(detail.getInsuranceProvider(), detail.getTakafulName());
				this.insuranceRate.setValue(detail.getPolicyRate());

				fillComboBox(this.paymentMethod, detail.getDftPayType(), PennantStaticListUtil.getInsurancePaymentType(), "");
				if (this.insuranceReq.isChecked()
						&& this.paymentMethod.getSelectedItem().getValue().equals(InsuranceConstants.PAYTYPE_SCH_FRQ)) {
					this.row_InsuranceFrq.setVisible(true);
					this.insuranceFrq.setValue(InsuranceConstants.DEFAULT_FRQ);
					this.insuranceFrq.setMandatoryStyle(true);
				} else {
					this.row_InsuranceFrq.setVisible(false);
				}
				fillComboBox(this.calcType, detail.getCalType(), PennantStaticListUtil.getInsuranceCalType(), "");
				if (this.insuranceReq.isChecked() && detail.getCalType().equals(InsuranceConstants.CALTYPE_RULE)) {
					this.calRule.setValue(detail.getAmountRule());
					this.insuranceRate.setReadonly(true);
					this.calRule.setDescription(detail.getRuleCodeDesc());
					this.row_calRule.setVisible(true);
					this.row_Percentage.setVisible(false);
					this.row_CalculaedOn.setVisible(false);
					this.row_Amount.setVisible(false);
					
					String excldMthds = "";
					if(StringUtils.equals(detail.getCalType(), InsuranceConstants.CALTYPE_RULE)){
						excldMthds = ","+InsuranceConstants.PAYTYPE_SCH_FRQ+",";
					}
					
					fillComboBox(this.paymentMethod, getFinInsurance().getPaymentMethod(),PennantStaticListUtil.getInsurancePaymentType(), excldMthds);
					
				} else if (this.insuranceReq.isChecked()
						&& detail.getCalType().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
					this.calPercentage.setValue(detail.getPercentage());
					fillComboBox(this.calOn, detail.getCalculateOn(), PennantStaticListUtil.getInsuranceCalculatedOn(),	"");
					this.insuranceRate.setReadonly(true);
					this.row_calRule.setVisible(false);
					this.row_Percentage.setVisible(true);
					this.row_CalculaedOn.setVisible(true);
					this.row_Amount.setVisible(false);
					if (detail.isAlwRateChange()) {
						this.calPercentage.setReadonly(isReadOnly("FinInsuranceDialog_percentage"));
						this.insuranceRate.setReadonly(true);
					} else {
						this.calPercentage.setReadonly(true);
					}
				} else if (this.insuranceReq.isChecked()
						&& detail.getCalType().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
					fillComboBox(this.calOn, detail.getCalculateOn(), PennantStaticListUtil.getInsuranceCalculatedOn(), "");
					this.row_calRule.setVisible(false);
					this.row_Percentage.setVisible(false);
					this.row_Amount.setVisible(false);
					this.row_CalculaedOn.setVisible(true);
					if (detail.isAlwRateChange()) {
						this.insuranceRate.setReadonly(isReadOnly("FinInsuranceDialog_providerRate"));
					} else {
						this.insuranceRate.setReadonly(true);
					}

				} else if (this.insuranceReq.isChecked()
						&& detail.getCalType().equals(InsuranceConstants.CALTYPE_CON_AMT)) {
					this.insuranceRate.setReadonly(true);
					this.row_Amount.setVisible(true);
					this.row_calRule.setVisible(false);
					this.row_Percentage.setVisible(false);
					this.row_CalculaedOn.setVisible(false);
					this.amount.setValue(PennantAppUtil.formateAmount(detail.getConstAmt(), amountFormatter));
					
					if (detail.isAlwRateChange()) {
						this.amount.setReadonly(isReadOnly("FinInsuranceDialog_amount"));
						this.insuranceRate.setReadonly(true);
					} else {
						this.amount.setReadonly(true);
					}
				}
			}

		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinInsuranceDialog", userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinInsuranceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinInsuranceDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinInsuranceDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinInsuranceDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	public void onChange$paymentMethod(Event event) {
		logger.debug("Entering");
		if (StringUtils.equals(this.paymentMethod.getSelectedItem().getValue().toString(),
				InsuranceConstants.PAYTYPE_SCH_FRQ)) {
			this.row_InsuranceFrq.setVisible(true);
			this.insuranceFrq.setValue(InsuranceConstants.DEFAULT_FRQ);
			this.insuranceFrq.setDisabled(false);
			this.insuranceFrq.setMandatoryStyle(true);
		} else {
			this.row_InsuranceFrq.setVisible(false);
			this.insuranceFrq.setDisabled(true);
			this.insuranceFrq.setMandatoryStyle(false);
		}

		logger.debug("Leaving");

	}
	public void onChange$calcType(Event event) {
		logger.debug("Entering");
		if(StringUtils.equals((String)this.calcType.getSelectedItem().getValue(), InsuranceConstants.CALTYPE_RULE)){			
			fillComboBox(this.paymentMethod, getFinInsurance().getPaymentMethod(),
					PennantStaticListUtil.getInsurancePaymentType(), ","+InsuranceConstants.PAYTYPE_SCH_FRQ+",");
		}else{
			fillComboBox(this.paymentMethod, getFinInsurance().getPaymentMethod(),
					PennantStaticListUtil.getInsurancePaymentType(), "");
		}
		logger.debug("Leaving");
		
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinTypeAccount object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinInsurances finInsurance = new FinInsurances();
		BeanUtils.copyProperties(getFinInsurance(), finInsurance);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_FinInsuranceDialog_InsuranceType.value") + " : "
				+ finInsurance.getInsuranceType() + "," + Labels.getLabel("label_FinInsuranceDialog_Reference.value")
				+ " : " + finInsurance.getInsReference();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(finInsurance.getRecordType())) {
				finInsurance.setVersion(finInsurance.getVersion() + 1);
				finInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					finInsurance.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(finInsurance.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				finInsurance.setVersion(finInsurance.getVersion() + 1);
				finInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFinInsurnaceEntryProcess(finInsurance, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinInsurnaceDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinFeeDetailListCtrl().doFillFinInsurances(this.finInsuranceList);
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	public FinInsurances getFinInsurance() {
		return finInsurance;
	}

	public void setFinInsurance(FinInsurances finInsurance) {
		this.finInsurance = finInsurance;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public List<FinInsurances> getFinInsuranceList() {
		return finInsuranceList;
	}

	public void setFinInsuranceList(List<FinInsurances> finInsuranceList) {
		this.finInsuranceList = finInsuranceList;
	}

}
