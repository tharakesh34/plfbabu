package com.pennanttech.pff.mmfl.cd.webui;

import java.sql.Timestamp;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.mmfl.cd.model.MerchantDetails;
import com.pennanttech.pff.mmfl.cd.service.MerchantDetailsService;

public class MerchantDetailsDialogueCtrl extends GFCBaseCtrl<MerchantDetails> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MerchantDetailsDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_merchantDetailsDialogue;

	protected Textbox merchantName;
	protected CurrencyBox avgNumOfTransperMnth;
	protected Intbox storeId;
	protected CurrencyBox avgTranAmtperMnth;
	protected Textbox storeName;
	protected CurrencyBox transAmtLmtPerTran;
	protected Textbox addressLine1;
	protected CurrencyBox transAmtLmtPerDay;
	protected Textbox addressLine2;
	protected Intbox peakTransPerDay;
	protected Textbox addressLine3;
	protected Checkbox refundAllowed;
	protected ExtendedCombobox country;
	protected ExtendedCombobox city;
	protected Textbox txtchannel;
	protected Button btnChannel;
	protected ExtendedCombobox state;
	protected ExtendedCombobox pincode;
	protected Checkbox active;

	protected MerchantDetails merchantDetails;

	private transient MerchantDetailsListCtrl merchantDetailsListCtrl;
	private transient MerchantDetailsService merchantDetailsService;

	public MerchantDetailsDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MerchantDialogue";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.merchantDetails.getMerchantId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_merchantDetailsDialogue(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_merchantDetailsDialogue);

		try {
			this.merchantDetails = (MerchantDetails) arguments.get("MerchantDetails");
			this.merchantDetailsListCtrl = (MerchantDetailsListCtrl) arguments.get("merchantDetailsListCtrl");

			if (this.merchantDetails == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			MerchantDetails merchantDetails = new MerchantDetails();
			BeanUtils.copyProperties(this.merchantDetails, merchantDetails);
			this.merchantDetails.setBefImage(merchantDetails);

			doLoadWorkFlow(this.merchantDetails.isWorkflow(), this.merchantDetails.getWorkflowId(),
					this.merchantDetails.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "MerchantDialogue");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.merchantDetails);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		setStatusDetails();

		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		this.avgNumOfTransperMnth.setProperties(false, formatter);
		this.avgTranAmtperMnth.setProperties(false, formatter);
		this.transAmtLmtPerTran.setProperties(false, formatter);
		this.transAmtLmtPerDay.setProperties(false, formatter);

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });

		this.city.setMandatoryStyle(true);
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });

		this.state.setMandatoryStyle(true);
		this.state.setModuleName("Province");
		this.state.setValueColumn("CPProvince");
		this.state.setDescColumn("CPProvinceName");
		this.state.setValidateColumns(new String[] { "CPProvince" });

		this.pincode.setMandatoryStyle(true);
		this.pincode.setModuleName("PinCode");
		this.pincode.setValueColumn("PinCode");
		this.pincode.setDescColumn("City");
		this.pincode.setValidateColumns(new String[] { "PinCode" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MerchantDialogue_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MerchantDialogue_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MerchantDialogue_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MerchantDialogue_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.merchantDetails);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		merchantDetailsListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.merchantDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param covenantType
	 * 
	 */
	public void doWriteBeanToComponents(MerchantDetails merchantDetails) {
		logger.debug(Literal.ENTERING);

		this.merchantName.setText(merchantDetails.getMerchantName());
		this.storeId.setValue(merchantDetails.getStoreId());
		this.storeName.setText(merchantDetails.getStoreName());
		this.addressLine1.setText(merchantDetails.getStoreAddressLine1());
		this.addressLine2.setText(merchantDetails.getStoreAddressLine2());
		this.addressLine3.setText(merchantDetails.getStoreAddressLine3());
		this.avgNumOfTransperMnth.setValue(PennantApplicationUtil.formateAmount(merchantDetails.getAvgTranAmtPerMnth(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.avgTranAmtperMnth.setValue(PennantApplicationUtil.formateAmount(merchantDetails.getAvgTranPerMnth(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.transAmtLmtPerTran.setValue(PennantApplicationUtil.formateAmount(merchantDetails.getTranAmtPerTran(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.transAmtLmtPerDay.setValue(PennantApplicationUtil.formateAmount(merchantDetails.getTranAmtPerDay(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.peakTransPerDay.setValue(merchantDetails.getPeakTransPerDay());
		this.refundAllowed.setChecked(merchantDetails.isAllowRefund());
		this.txtchannel.setText(merchantDetails.getChannel());
		this.active.setChecked(merchantDetails.isActive());
		if (merchantDetails.isNew() || (merchantDetails.getRecordType() != null ? merchantDetails.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.city.setValue(merchantDetails.getStoreCity());
		this.city.setDescription(merchantDetails.getCityName());
		this.state.setValue(merchantDetails.getStoreState());
		this.state.setDescription(merchantDetails.getStateName());
		this.country.setValue(merchantDetails.getStoreCountry());
		this.country.setDescription(merchantDetails.getCountryName());
		this.pincode.setValue(merchantDetails.getPOSId());

		this.recordStatus.setValue(merchantDetails.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(MerchantDetails merchantDetails) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			merchantDetails.setMerchantName(this.merchantName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreId(this.storeId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreName(this.storeName.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreAddressLine1(this.addressLine1.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreAddressLine2(this.addressLine2.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreAddressLine3(this.addressLine3.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreCountry(this.country.getValidatedValue());
			merchantDetails.setCountryName(this.country.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreState(this.state.getValidatedValue());
			merchantDetails.setStateName(this.state.getDescription());
			;
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setStoreCity(this.city.getValidatedValue());
			merchantDetails.setCityName(this.city.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setPOSId(this.pincode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setChannel(this.txtchannel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setAvgTranPerMnth(PennantApplicationUtil.unFormateAmount(
					this.avgNumOfTransperMnth.getActualValue(), CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setAvgTranAmtPerMnth(PennantApplicationUtil.unFormateAmount(
					this.avgTranAmtperMnth.getActualValue(), CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setTranAmtPerTran(PennantApplicationUtil.unFormateAmount(
					this.transAmtLmtPerTran.getActualValue(), CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setTranAmtPerDay(PennantApplicationUtil.unFormateAmount(
					this.transAmtLmtPerDay.getActualValue(), CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.peakTransPerDay.getValue() != null) {
				merchantDetails.setPeakTransPerDay(this.peakTransPerDay.getValue());
			} else {
				merchantDetails.setPeakTransPerDay(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setAllowRefund(this.refundAllowed.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			merchantDetails.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param covenantType
	 *            The entity that need to be render.
	 */
	public void doShowDialog(MerchantDetails consumerProduct) {
		logger.debug(Literal.ENTERING);

		if (consumerProduct.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.merchantName.setFocus(true);
		} else {
			this.storeId.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(consumerProduct.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(consumerProduct);

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.merchantName.isReadonly()) {
			this.merchantName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MerchantDetails_MerchantName.value"),
							PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
		}

		if (!this.storeId.isReadonly()) {
			this.storeId.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_MerchantDetails_PeakTransPerDay.value"), true, false));
		}

		if (!this.storeName.isReadonly()) {
			this.storeName.setConstraint(new PTStringValidator(Labels.getLabel("label_MerchantDetails_StoreName.value"),
					PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
		}

		if (!this.addressLine1.isReadonly()) {
			this.addressLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MerchantDetails_AddressLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.addressLine2.isReadonly()) {
			if (!this.addressLine2.getText().equals("")) {
				this.addressLine2.setConstraint(
						new PTStringValidator(Labels.getLabel("label_MerchantDetails_AddressLine2.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
		}

		if (!this.addressLine3.isReadonly()) {
			if (!this.addressLine3.getText().equals("")) {
				this.addressLine3.setConstraint(
						new PTStringValidator(Labels.getLabel("label_MerchantDetails_AddressLine3.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
		}

		if (!this.avgNumOfTransperMnth.isReadonly()) {
			if (this.avgNumOfTransperMnth.getActualValue() != null) {
				this.avgNumOfTransperMnth.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_MerchantDetails_AvgNumOfTraPerMnth.value"), 2, false, false));
			}

		}

		if (!this.avgTranAmtperMnth.isReadonly()) {
			if (this.avgTranAmtperMnth.getAction() != null) {
				this.avgTranAmtperMnth.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_MerchantDetails_AvgTraAmtPerMnth.value"), 2, false, false));
			}
		}

		if (!this.transAmtLmtPerTran.isReadonly()) {
			if (this.transAmtLmtPerTran.getActualValue() != null) {
				this.transAmtLmtPerTran.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_MerchantDetails_TransAmtLmtPerMnth.value"), 2, false, false));
			}
		}

		if (!this.transAmtLmtPerDay.isReadonly()) {
			if (this.transAmtLmtPerDay.getActualValue() != null) {
				this.transAmtLmtPerDay.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_MerchantDetails_TransAmtLmtPerDay.value"), 2, false, false));
			}
		}

		if (!this.peakTransPerDay.isReadonly()) {
			if (this.peakTransPerDay.getValue() != 0) {
				this.peakTransPerDay.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_MerchantDetails_PeakTransPerDay.value"), true, false));
			}

		}

		if (!this.country.isReadonly()) {
			this.country.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MerchantDetails_Country.value"), null, true, true));
		}
		if (!this.state.isReadonly()) {
			this.state.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MerchantDetails_State.value"), null, true, true));
		}
		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MerchantDetails_City.value"), null, true, true));
		}
		if (!this.pincode.isReadonly()) {
			this.pincode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MerchantDetails_Pincode.value"), null, true, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.merchantName.setConstraint("");
		this.storeId.setConstraint("");
		this.storeName.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.avgNumOfTransperMnth.setConstraint("");
		this.avgTranAmtperMnth.setConstraint("");
		this.transAmtLmtPerTran.setConstraint("");
		this.transAmtLmtPerDay.setConstraint("");
		this.peakTransPerDay.setConstraint("");
		this.country.setConstraint("");
		this.state.setConstraint("");
		this.pincode.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a CovenantType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final MerchantDetails consumerProduct = new MerchantDetails();
		BeanUtils.copyProperties(this.merchantDetails, consumerProduct);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ consumerProduct.getMerchantId();
		if (MessageUtil.confirm(msg) != MessageUtil.YES) {
			return;
		}

		if (StringUtils.trimToEmpty(consumerProduct.getRecordType()).equals("")) {
			consumerProduct.setVersion(consumerProduct.getVersion() + 1);
			consumerProduct.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (isWorkFlowEnabled()) {
				consumerProduct.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				consumerProduct.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), consumerProduct.getNextTaskId(),
						consumerProduct);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (doProcess(consumerProduct, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.merchantDetails.isNewRecord()) {
			this.merchantName.setDisabled(false);
		} else {
			this.merchantName.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("MerchantDialogue_StoreId"), this.storeId);
		readOnlyComponent(isReadOnly("MerchantDialogue_StoreName"), this.storeName);
		readOnlyComponent(isReadOnly("MerchantDialogue_AddressLine1"), this.addressLine1);
		readOnlyComponent(isReadOnly("MerchantDialogue_AddressLine2"), this.addressLine2);
		readOnlyComponent(isReadOnly("MerchantDialogue_AddressLine3"), this.addressLine3);
		readOnlyComponent(isReadOnly("MerchantDialogue_City"), this.city);
		readOnlyComponent(isReadOnly("MerchantDialogue_State"), this.state);
		readOnlyComponent(isReadOnly("MerchantDialogue_Pincode"), this.pincode);
		readOnlyComponent(isReadOnly("MerchantDialogue_Active"), this.active);
		readOnlyComponent(isReadOnly("MerchantDialogue_City"), this.country);
		readOnlyComponent(isReadOnly("MerchantDialogue_TransactionsPerMonth"), this.avgNumOfTransperMnth);
		readOnlyComponent(isReadOnly("MerchantDialogue_AmountPerMonth"), this.avgTranAmtperMnth);
		readOnlyComponent(isReadOnly("MerchantDialogue_LimitPerTransaction"), this.transAmtLmtPerTran);
		readOnlyComponent(isReadOnly("MerchantDialogue_LimitPerDay"), this.transAmtLmtPerDay);
		readOnlyComponent(isReadOnly("MerchantDialogue_TransactionPerDay"), this.peakTransPerDay);
		readOnlyComponent(isReadOnly("MerchantDialogue_RefundAllowed"), this.refundAllowed);
		readOnlyComponent(isReadOnly("MerchantDialogue_Channel"), this.btnChannel);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.merchantDetails.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.storeId);
		readOnlyComponent(true, this.storeName);
		readOnlyComponent(true, this.addressLine1);
		readOnlyComponent(true, this.addressLine2);
		readOnlyComponent(true, this.addressLine3);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.state);
		readOnlyComponent(true, this.country);
		readOnlyComponent(true, this.pincode);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.avgNumOfTransperMnth);
		readOnlyComponent(true, this.avgTranAmtperMnth);
		readOnlyComponent(true, this.transAmtLmtPerTran);
		readOnlyComponent(true, this.transAmtLmtPerDay);
		readOnlyComponent(true, this.peakTransPerDay);
		readOnlyComponent(true, this.refundAllowed);
		readOnlyComponent(true, this.txtchannel);
		readOnlyComponent(true, this.btnChannel);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.merchantName.setValue("");
		this.storeId.setValue(0);
		this.storeName.setValue("");
		this.addressLine1.setValue("");
		this.addressLine2.setValue("");
		this.addressLine3.setValue("");
		this.country.setValue("");
		this.city.setValue("");
		this.state.setValue("");
		this.pincode.setValue("");
		this.avgNumOfTransperMnth.setValue("");
		this.avgTranAmtperMnth.setValue("");
		this.transAmtLmtPerTran.setValue("");
		this.transAmtLmtPerDay.setValue("");
		this.peakTransPerDay.setValue(0);
		this.refundAllowed.setChecked(false);
		this.txtchannel.setValue("");
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final MerchantDetails consumerProduct = new MerchantDetails();
		BeanUtils.copyProperties(this.merchantDetails, consumerProduct);

		doSetValidation();
		doWriteComponentsToBean(consumerProduct);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(consumerProduct.getRecordType())) {
				consumerProduct.setVersion(consumerProduct.getVersion() + 1);
				if (consumerProduct.isNew()) {
					consumerProduct.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					consumerProduct.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					consumerProduct.setNewRecord(true);
				}
			}
		} else {
			consumerProduct.setVersion(consumerProduct.getVersion() + 1);
			if (consumerProduct.isNew()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(consumerProduct, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(MerchantDetails consumenrProduct, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		consumenrProduct.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		consumenrProduct.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		consumenrProduct.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			consumenrProduct.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(consumenrProduct.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, consumenrProduct);
				}
				if (isNotesMandatory(taskId, consumenrProduct)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");
				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {
						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}
			consumenrProduct.setTaskId(taskId);
			consumenrProduct.setNextTaskId(nextTaskId);
			consumenrProduct.setRoleCode(getRole());
			consumenrProduct.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(consumenrProduct, tranType);
			String operationRefs = getServiceOperations(taskId, consumenrProduct);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(consumenrProduct, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(consumenrProduct, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		MerchantDetails consumerProduct = (MerchantDetails) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = merchantDetailsService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = merchantDetailsService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = merchantDetailsService.doApprove(auditHeader);

						if (consumerProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = merchantDetailsService.doReject(auditHeader);
						if (consumerProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_merchantDetailsDialogue, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_merchantDetailsDialogue, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.merchantDetails), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(MerchantDetails consumerProduct, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, consumerProduct.getBefImage(), consumerProduct);
		return new AuditHeader(getReference(), null, null, null, auditDetail, consumerProduct.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnChannel(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_merchantDetailsDialogue, "ChannelTypes",
				String.valueOf(this.txtchannel.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.txtchannel.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onFulfill$city(Event event) throws InterruptedException {
		logger.debug("Entering");
		doRemoveValidation();
		doClearMessage();
		Object dataObject = city.getObject();
		String cityValue = null;
		if (!(dataObject instanceof String)) {
			City details = (City) dataObject;
			if (details == null) {
				fillPindetails(null, null);
			}
			if (details != null) {
				this.state.setValue(details.getPCProvince());
				this.state.setDescription(details.getLovDescPCProvinceName());
				this.country.setValue(details.getPCCountry());
				this.country.setDescription(details.getLovDescPCCountryName());
				this.pincode.setValue("");
				this.pincode.setDescription("");
				cityValue = details.getPCCity();
				fillPindetails(cityValue, this.state.getValue());
			} else {
				this.city.setObject("");
				this.pincode.setObject("");
				this.pincode.setValue("");
				this.pincode.setDescription("");
				this.state.setErrorMessage("");
				this.country.setErrorMessage("");
				fillPindetails(null, this.state.getValue());
			}
		} else if ("".equals(dataObject)) {
			this.pincode.setValue("");
			this.pincode.setDescription("");
			this.state.setObject("");
		}
		logger.debug("Leaving");
	}

	private void fillPindetails(String id, String province) {
		this.pincode.setModuleName("PinCode");
		this.pincode.setValueColumn("PinCode");
		this.pincode.setDescColumn("AreaName");
		this.pincode.setValidateColumns(new String[] { "PinCode" });
		Filter[] filters1 = new Filter[1];

		if (id != null) {
			filters1[0] = new Filter("City", id, Filter.OP_EQUAL);
		} else if (province != null && !province.isEmpty()) {
			filters1[0] = new Filter("PCProvince", province, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pincode.setFilters(filters1);
	}

	public void onFulfill$pincode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pincode.getObject();
		if (dataObject instanceof String) {

		} else {
			PinCode details = (PinCode) dataObject;

			if (details != null) {

				this.country.setValue(details.getpCCountry());
				this.country.setDescription(details.getLovDescPCCountryName());
				this.city.setValue(details.getCity());
				this.city.setDescription(details.getPCCityName());
				this.state.setValue(details.getPCProvince());
				this.state.setDescription(details.getLovDescPCProvinceName());
				this.city.setErrorMessage("");
				this.state.setErrorMessage("");
				this.country.setErrorMessage("");
				;
			}

		}
		Filter[] filters1 = new Filter[1];
		if (this.city.getValue() != null && !this.city.getValue().isEmpty()) {
			filters1[0] = new Filter("City", this.city.getValue(), Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pincode.setFilters(filters1);

		logger.debug("Leaving");
	}

	public void onFulfill$state(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = state.getObject();
		String pcProvince = this.state.getValue();
		if (dataObject instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
			this.pincode.setValue("");
			this.pincode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.state.setErrorMessage("");
				pcProvince = this.state.getValue();
				this.country.setValue(province.getCPCountry());
				this.country.setDescription(province.getLovDescCPCountryName());
				this.city.setValue("");
				this.city.setDescription("");
				this.pincode.setValue("");
				this.pincode.setDescription("");
				fillPindetails(null, pcProvince);
			} else {
				this.city.setObject("");
				this.pincode.setObject("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pincode.setValue("");
				this.pincode.setDescription("");
			}
		}
		fillCitydetails(pcProvince);
		logger.debug("Leaving" + event.toString());
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[1];

		if (state == null || state.isEmpty()) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		this.city.setFilters(filters1);
	}

	public void onFulfill$country(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = country.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.state.setValue("");
			this.state.setDescription("");
			this.city.setValue("");
			this.city.setDescription("");
			this.pincode.setValue("");
			this.pincode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject;
			if (country == null) {
				fillProvinceDetails(null);
			}
			if (country != null) {
				this.state.setErrorMessage("");
				pcProvince = country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.state.setObject("");
				this.city.setObject("");
				this.pincode.setObject("");
				this.state.setValue("");
				this.state.setDescription("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pincode.setValue("");
				this.pincode.setDescription("");
			}
			fillPindetails(null, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void fillProvinceDetails(String country) {
		this.state.setMandatoryStyle(true);
		this.state.setModuleName("Province");
		this.state.setValueColumn("CPProvince");
		this.state.setDescColumn("CPProvinceName");
		this.state.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.state.setFilters(filters1);
	}

	public void setMerchantDetailsListCtrl(MerchantDetailsListCtrl merchantDetailsListCtrl) {
		this.merchantDetailsListCtrl = merchantDetailsListCtrl;
	}

	public void setMerchantDetailsService(MerchantDetailsService merchantDetailsService) {
		this.merchantDetailsService = merchantDetailsService;
	}

}
