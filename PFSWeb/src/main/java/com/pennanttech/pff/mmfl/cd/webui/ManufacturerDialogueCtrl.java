package com.pennanttech.pff.mmfl.cd.webui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.Manufacturer;
import com.pennanttech.pff.cd.service.ManufacturerService;

public class ManufacturerDialogueCtrl extends GFCBaseCtrl<Manufacturer> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManufacturerDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_manufacturerDialogue;

	protected Textbox name;
	protected Longbox oemId;
	protected Textbox description;
	protected Button btnchannels;
	protected Textbox txtchannel;
	protected Checkbox active;
	protected Manufacturer manufacturer;
	protected Textbox addressLine1;
	protected Textbox addressLine2;
	protected Textbox addressLine3;
	protected ExtendedCombobox country;
	protected ExtendedCombobox city;
	protected ExtendedCombobox state;
	protected ExtendedCombobox pinCode;
	protected Textbox manufacPAN;
	protected Uppercasebox gstInNumber;
	protected Textbox manfMobileNo;
	protected Textbox manfEmailId;
	protected Textbox manfacContactName;

	private transient ManufacturerListCtrl manufacturerListCtrl;
	private transient ManufacturerService manufacturerService;

	public ManufacturerDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CDManufacturersDialogue";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.manufacturer.getManufacturerId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_manufacturerDialogue(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_manufacturerDialogue);

		try {
			this.manufacturer = (Manufacturer) arguments.get("Manufacturer");
			this.manufacturerListCtrl = (ManufacturerListCtrl) arguments.get("manufacturerListCtrl");

			if (this.manufacturer == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			Manufacturer manufacturerdata = new Manufacturer();
			BeanUtils.copyProperties(this.manufacturer, manufacturerdata);
			this.manufacturer.setBefImage(manufacturerdata);

			doLoadWorkFlow(this.manufacturer.isWorkflow(), this.manufacturer.getWorkflowId(),
					this.manufacturer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CDManufacturersDialogue");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.manufacturer);

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

		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("pinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValueType(DataType.LONG);
		this.pinCode.setValidateColumns(new String[] { "pinCodeId" });
		// this.pinCode.setInputAllowed(false);

		this.txtchannel.setMaxlength(20);
		this.manufacPAN.setMaxlength(10);
		this.gstInNumber.setMaxlength(15);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.manufacturer);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		manufacturerListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.manufacturer.getBefImage());
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
	public void doWriteBeanToComponents(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		this.name.setText(manufacturer.getName());
		if (!manufacturer.isNewRecord()) {
			this.oemId.setValue(manufacturer.getManufacturerId());
		}
		this.description.setText(manufacturer.getDescription());
		this.active.setChecked(manufacturer.isActive());
		if (manufacturer.isNewRecord() || (manufacturer.getRecordType() != null ? manufacturer.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.txtchannel.setText(manufacturer.getChannel());
		this.recordStatus.setValue(manufacturer.getRecordStatus());
		this.addressLine1.setText(manufacturer.getAddressLine1());
		this.addressLine2.setText(manufacturer.getAddressLine2());
		this.addressLine3.setText(manufacturer.getAddressLine3());
		this.city.setValue(manufacturer.getCity());
		this.city.setDescription(manufacturer.getLovDescCityName());
		this.state.setValue(manufacturer.getState());
		this.state.setDescription(manufacturer.getLovDescStateName());
		this.country.setValue(manufacturer.getCountry());
		this.country.setDescription(manufacturer.getLovDescCountryName());
		this.manufacPAN.setText(manufacturer.getManufacPAN());
		this.gstInNumber.setText(manufacturer.getGstInNumber());
		this.manfMobileNo.setText(manufacturer.getManfMobileNo());
		this.manfEmailId.setText(manufacturer.getManfEmailId());
		this.manfacContactName.setText(manufacturer.getManfacContactName());

		if (manufacturer.getPinCodeId() != null) {
			this.pinCode.setAttribute("pinCodeId", manufacturer.getPinCodeId());
		} else {
			this.pinCode.setAttribute("pinCodeId", null);
		}

		this.pinCode.setValue(manufacturer.getPinCode(), manufacturer.getPinAreaDesc());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			manufacturer.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setDescription(this.description.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setChannel(this.txtchannel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setAddressLine1(this.addressLine1.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setAddressLine2(this.addressLine2.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setAddressLine3(this.addressLine3.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setCountry(this.country.getValidatedValue());
			manufacturer.setLovDescCountryName(this.country.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setState(this.state.getValidatedValue());
			manufacturer.setLovDescStateName(this.state.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setCity(this.city.getValidatedValue());
			manufacturer.setLovDescStateName(this.city.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			PinCode pinCode = (PinCode) this.pinCode.getObject();
			if (pinCode != null) {
				if (!StringUtils.isEmpty(pinCode.toString())) {
					manufacturer.setPinCodeId(pinCode.getPinCodeId());
				}
			}
			manufacturer.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setManufacPAN(this.manufacPAN.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setGstInNumber(this.gstInNumber.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setManfMobileNo(this.manfMobileNo.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setManfEmailId(this.manfEmailId.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setManfacContactName(this.manfacContactName.getText());
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
	 * @param covenantType The entity that need to be render.
	 */
	public void doShowDialog(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		if (manufacturer.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.name.setFocus(true);
		} else {
			this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(manufacturer.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(manufacturer);

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

		if (!this.name.isReadonly()) {
			this.name.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManufacturerList_ManufacturerName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManufacturerList_Description.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.txtchannel.getText().equals("") && !this.btnchannels.isDisabled()) {
			if (this.txtchannel.getText().length() > 20) {
				throw new WrongValueException(this.txtchannel,
						Labels.getLabel("label_ConsumerProductDialogue_channelLength.value"));
			}
		}

		if (!this.addressLine1.isReadonly()) {
			this.addressLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManufacturerList_AddressLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.addressLine2.isReadonly()) {
			if (!this.addressLine2.getText().equals("")) {
				this.addressLine2.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ManufacturerList_AddressLine2.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
		}

		if (!this.addressLine3.isReadonly()) {
			if (!this.addressLine3.getText().equals("")) {
				this.addressLine3.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ManufacturerList_AddressLine3.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
		}

		if (!this.country.isReadonly()) {
			this.country.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManufacturerList_Country.value"), null, true, true));
		}
		if (!this.state.isReadonly()) {
			this.state.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManufacturerList_State.value"), null, true, true));
		}
		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManufacturerList_City.value"), null, true, true));
		}
		if (!this.pinCode.isReadonly()) {
			this.pinCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManufacturerList_Pincode.value"), null, true, true));
		}

		if (!this.manufacPAN.isReadonly()) {
			this.manufacPAN
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManufacturerList_PanNumber.value"),
							PennantRegularExpressions.REGEX_PANNUMBER, true));
		}

		if (!this.gstInNumber.isReadonly()) {
			this.gstInNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManufacturerList_GSTINNumber.value"),
							PennantRegularExpressions.REGEX_GSTIN, true));
		}

		if (!this.manfMobileNo.isReadonly()) {
			this.manfMobileNo
					.setConstraint(new PTMobileNumberValidator(Labels.getLabel("label_ManufacturerList_MobileNo.value"),
							false, PennantRegularExpressions.REGEX_MOBILE));
		}

		if (!this.manfEmailId.isReadonly()) {
			this.manfEmailId.setConstraint(
					new PTEmailValidator(Labels.getLabel("label_ManufacturerList_EmailID.value"), false));
		}

		if (!this.manfacContactName.isReadonly()) {
			this.manfacContactName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManufacturerList_ContactName.value"),
							PennantRegularExpressions.REGEX_NAME, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.name.setConstraint("");
		this.description.setConstraint("");
		this.txtchannel.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.manufacPAN.setConstraint("");
		this.gstInNumber.setConstraint("");
		this.manfMobileNo.setConstraint("");
		this.manfEmailId.setConstraint("");
		this.manfacContactName.setConstraint("");
		this.country.setConstraint("");
		this.city.setConstraint("");
		this.state.setConstraint("");
		this.pinCode.setConstraint("");

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

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Manufacturer manufacturer = new Manufacturer();
		BeanUtils.copyProperties(this.manufacturer, manufacturer);

		doDelete(manufacturer.getName(), manufacturer);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.manufacturer.isNewRecord()) {
			this.name.setDisabled(false);
		} else {
			this.name.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Description"), this.description);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Channel"), this.active);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Active"), this.btnchannels);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_AddressLine1"), this.addressLine1);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_AddressLine2"), this.addressLine2);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_AddressLine3"), this.addressLine3);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_City"), this.city);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_State"), this.state);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_PinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Country"), this.country);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_PANNumber"), this.manufacPAN);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_GSTINNumber"), this.gstInNumber);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_MobileNumber"), this.manfMobileNo);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_EmailId"), this.manfEmailId);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_ContactName"), this.manfacContactName);
		this.oemId.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.manufacturer.isNewRecord()) {
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

		readOnlyComponent(true, this.name);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.txtchannel);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.addressLine1);
		readOnlyComponent(true, this.addressLine2);
		readOnlyComponent(true, this.addressLine3);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.state);
		readOnlyComponent(true, this.country);
		readOnlyComponent(true, this.pinCode);
		readOnlyComponent(true, this.manufacPAN);
		readOnlyComponent(true, this.gstInNumber);
		readOnlyComponent(true, this.manfMobileNo);
		readOnlyComponent(true, this.manfEmailId);
		readOnlyComponent(true, this.manfacContactName);

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
		this.name.setValue("");
		this.description.setValue("");
		this.txtchannel.setValue("");
		this.addressLine1.setValue("");
		this.addressLine2.setValue("");
		this.addressLine3.setValue("");
		this.country.setValue("");
		this.city.setValue("");
		this.state.setValue("");
		this.pinCode.setValue("");
		this.manufacPAN.setValue("");
		this.gstInNumber.setValue("");
		this.manfMobileNo.setValue("");
		this.manfEmailId.setValue("");
		this.manfacContactName.setValue("");

		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Manufacturer manufacturer = new Manufacturer();
		BeanUtils.copyProperties(this.manufacturer, manufacturer);

		doSetValidation();
		doWriteComponentsToBean(manufacturer);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(manufacturer.getRecordType())) {
				manufacturer.setVersion(manufacturer.getVersion() + 1);
				if (manufacturer.isNewRecord()) {
					manufacturer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					manufacturer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					manufacturer.setNewRecord(true);
				}
			}
		} else {
			manufacturer.setVersion(manufacturer.getVersion() + 1);
			if (manufacturer.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(manufacturer, tranType)) {
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
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Manufacturer manufacturer, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		manufacturer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		manufacturer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manufacturer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			manufacturer.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(manufacturer.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, manufacturer);
				}
				if (isNotesMandatory(taskId, manufacturer)) {
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
			manufacturer.setTaskId(taskId);
			manufacturer.setNextTaskId(nextTaskId);
			manufacturer.setRoleCode(getRole());
			manufacturer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(manufacturer, tranType);
			String operationRefs = getServiceOperations(taskId, manufacturer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(manufacturer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(manufacturer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void setManufacturerListCtrl(ManufacturerListCtrl manufacturerListCtrl) {
		this.manufacturerListCtrl = manufacturerListCtrl;
	}

	public void setManufacturerService(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Manufacturer manufacturer = (Manufacturer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = manufacturerService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = manufacturerService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = manufacturerService.doApprove(auditHeader);

					if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = manufacturerService.doReject(auditHeader);
					if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_manufacturerDialogue, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_manufacturerDialogue, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.manufacturer), true);
				}
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
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

	private AuditHeader getAuditHeader(Manufacturer manufacturer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, manufacturer.getBefImage(), manufacturer);
		return new AuditHeader(getReference(), null, null, null, auditDetail, manufacturer.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnchannels(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_manufacturerDialogue, "ChannelTypes",
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
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				cityValue = details.getPCCity();
				fillPindetails(cityValue, this.state.getValue());
			} else {
				this.city.setObject("");
				this.pinCode.setObject("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				this.state.setErrorMessage("");
				this.country.setErrorMessage("");
				fillPindetails(null, this.state.getValue());
			}
		} else if ("".equals(dataObject)) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			this.state.setObject("");
		}
		logger.debug("Leaving");
	}

	private void fillPindetails(String id, String province) {
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("pinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "pinCode" });
		Filter[] filters1 = new Filter[1];

		if (id != null) {
			filters1[0] = new Filter("City", id, Filter.OP_EQUAL);
		} else if (province != null && !province.isEmpty()) {
			filters1[0] = new Filter("PCProvince", province, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);
	}

	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
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
				this.pinCode.setValue(details.getPinCode());
				this.pinCode.setDescription(details.getAreaName());
				this.city.setErrorMessage("");
				this.state.setErrorMessage("");
				this.country.setErrorMessage("");

			}

		}
		Filter[] filters1 = new Filter[1];
		if (this.city.getValue() != null && !this.city.getValue().isEmpty()) {
			filters1[0] = new Filter("City", this.city.getValue(), Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);

		logger.debug("Leaving");
	}

	public void onFulfill$state(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = state.getObject();
		String pcProvince = this.state.getValue();
		if (dataObject instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
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
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				fillPindetails(null, pcProvince);
			} else {
				this.city.setObject("");
				this.pinCode.setObject("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
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
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
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
				this.pinCode.setObject("");
				this.state.setValue("");
				this.state.setDescription("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
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
}
