/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : GSTRateDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-05-2019 * * Modified
 * Date : 20-05-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-05-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.gstrate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.service.rmtmasters.GSTRateService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RMTMasters/GSTRate/gSTRateDialog.zul file. <br>
 */
public class GSTRateDialogCtrl extends GFCBaseCtrl<GSTRate> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(GSTRateDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_GSTRateDialog;
	protected ExtendedCombobox fromState;
	protected ExtendedCombobox toState;
	protected Space space_TaxType;
	protected Combobox taxType;
	protected Combobox calcType;
	protected CurrencyBox amount;
	protected Decimalbox percentage;
	protected Combobox calcOn;
	protected Checkbox active;
	private GSTRate gSTRate;

	protected Label label_Amount;
	protected Label label_Percentage;
	protected Space space_Amount;
	protected Label label_CalcOn;
	protected Space space_CalcOn;

	private transient GSTRateListCtrl gstRateListCtrl;
	private transient GSTRateService gstRateService;

	private List<Property> listTaxType = PennantAppUtil.getTaxtTypeList();
	private List<ValueLabel> listCalcType = PennantStaticListUtil.getCalcTypeList();
	private List<ValueLabel> listCalcOn = PennantStaticListUtil.getCalcOnList();

	/**
	 * default constructor.<br>
	 */
	public GSTRateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GSTRateDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.gSTRate.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_GSTRateDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_GSTRateDialog);

		try {
			// Get the required arguments.
			this.gSTRate = (GSTRate) arguments.get("gSTRate");
			this.gstRateListCtrl = (GSTRateListCtrl) arguments.get("gSTRateListCtrl");

			if (this.gSTRate == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			GSTRate gSTRate = new GSTRate();
			BeanUtils.copyProperties(this.gSTRate, gSTRate);
			this.gSTRate.setBefImage(gSTRate);

			// Render the page and display the data.
			doLoadWorkFlow(this.gSTRate.isWorkflow(), this.gSTRate.getWorkflowId(), this.gSTRate.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.gSTRate);
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

		this.fromState.setProperties("Province", "CPProvince", "CPProvinceName", true, 8);
		this.toState.setProperties("Province", "CPProvince", "CPProvinceName", true, 8);

		this.amount.setProperties(false, PennantConstants.defaultCCYDecPos);

		this.percentage.setMaxlength(5);
		this.percentage.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.percentage.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.percentage.setScale(PennantConstants.defaultCCYDecPos);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_GSTRateDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_GSTRateDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GSTRateDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_GSTRateDialog_btnSave"));
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
	public void onClick$btnDelete(Event event) {
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
		doShowNotes(this.gSTRate);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		gstRateListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.gSTRate.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param gSTRate
	 * 
	 */
	public void doWriteBeanToComponents(GSTRate aGSTRate) {
		logger.debug(Literal.ENTERING);

		this.fromState.setValue(aGSTRate.getFromState());
		this.fromState.setDescription(aGSTRate.getFromStateName());
		this.toState.setValue(aGSTRate.getToState());
		this.toState.setDescription(aGSTRate.getToStateName());
		fillList(this.taxType, listTaxType, aGSTRate.getTaxType());
		fillComboBox(this.calcType, aGSTRate.getCalcType(), listCalcType, "");
		this.amount.setValue(
				PennantApplicationUtil.formateAmount(aGSTRate.getAmount(), PennantConstants.defaultCCYDecPos));
		this.percentage.setValue(aGSTRate.getPercentage());
		fillComboBox(this.calcOn, aGSTRate.getCalcOn(), listCalcOn, "");
		this.active.setChecked(aGSTRate.isActive());

		this.recordStatus.setValue(aGSTRate.getRecordStatus());

		if (aGSTRate.isNewRecord() || (aGSTRate.getRecordType() != null ? aGSTRate.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			fillComboBox(this.calcType, RuleConstants.CALCTYPE_PERCENTAGE, listCalcType, "");
			this.active.setChecked(true);
			this.active.setDisabled(true);
			onchangetaxType(RuleConstants.CALCON_TRANSACTION_AMOUNT);
		} else {
			onchangetaxType(aGSTRate.getCalcOn());
		}
		onchangeCalcType();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGSTRate
	 */
	public void doWriteComponentsToBean(GSTRate aGSTRate) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		// From State
		try {
			aGSTRate.setFromState(this.fromState.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// To State
		try {
			aGSTRate.setToState(this.toState.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tax Type
		try {
			String strTaxType = null;
			if (this.taxType.getSelectedItem() != null) {
				strTaxType = this.taxType.getSelectedItem().getValue().toString();
			}
			if (strTaxType != null && !PennantConstants.List_Select.equals(strTaxType)) {
				aGSTRate.setTaxType(strTaxType);

			} else {
				aGSTRate.setTaxType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Calc Type
		try {
			String strCalcType = null;
			if (this.calcType.getSelectedItem() != null) {
				strCalcType = this.calcType.getSelectedItem().getValue().toString();
			}
			if (strCalcType != null && !PennantConstants.List_Select.equals(strCalcType)) {
				aGSTRate.setCalcType(strCalcType);

			} else {
				aGSTRate.setCalcType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Amount
		try {
			if (this.amount.getActualValue() != null) {
				aGSTRate.setAmount(PennantApplicationUtil.unFormateAmount(this.amount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
			} else {
				aGSTRate.setAmount(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Percentage
		try {
			if (this.percentage.getValue() != null) {
				aGSTRate.setPercentage(this.percentage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Calc On
		try {
			String strCalcOn = null;
			if (this.calcOn.getSelectedItem() != null) {
				strCalcOn = this.calcOn.getSelectedItem().getValue().toString();
			}
			if (strCalcOn != null && !PennantConstants.List_Select.equals(strCalcOn)) {
				aGSTRate.setCalcOn(strCalcOn);

			} else {
				aGSTRate.setCalcOn(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aGSTRate.setActive(this.active.isChecked());
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

	public void onChange$taxType(Event event) {
		onchangetaxType("");

	}

	private void onchangetaxType(String calcValue) {
		String calcTypeVal = getComboboxValue(this.taxType);
		if (!StringUtils.equals(RuleConstants.CODE_CESS, calcTypeVal)) {
			String exlcudeFeilds = "," + RuleConstants.CODE_TOTAL_AMOUNT_INCLUDINGGST + "," + RuleConstants.CODE_CGST
					+ "," + RuleConstants.CODE_SGST + "," + RuleConstants.CODE_IGST + "," + RuleConstants.CODE_UGST
					+ "," + RuleConstants.CODE_TOTAL_GST + ",";
			fillComboBox(this.calcOn, StringUtils.isNotBlank(calcValue) ? calcValue : PennantConstants.List_Select,
					listCalcOn, exlcudeFeilds);
		} else {
			fillComboBox(this.calcOn, StringUtils.isNotBlank(calcValue) ? calcValue : PennantConstants.List_Select,
					listCalcOn, "");
		}
	}

	public void onChange$calcType(Event event) {
		onchangeCalcType();
	}

	private void onchangeCalcType() {
		String calcTypeVal = getComboboxValue(this.calcType);
		if (StringUtils.equals(RuleConstants.CALCTYPE_FIXED_AMOUNT, calcTypeVal)) {
			this.label_Amount.setVisible(true);
			this.amount.setMandatory(true);
			this.space_Amount.setVisible(false);
			this.amount.setVisible(true);

			this.percentage.setConstraint("");
			this.percentage.setErrorMessage("");
			this.percentage.setVisible(false);
			this.percentage.setValue(BigDecimal.ZERO);

			this.calcOn.setVisible(false);
			this.calcOn.setConstraint("");
			this.calcOn.setErrorMessage("");
			onchangetaxType(this.calcOn.getSelectedItem().getValue());
			this.label_CalcOn.setVisible(false);
			this.space_CalcOn.setVisible(false);

		} else if (StringUtils.equals(RuleConstants.CALCTYPE_PERCENTAGE, calcTypeVal)) {
			this.label_Amount.setVisible(true);
			this.space_Amount.setVisible(true);
			this.amount.setConstraint("");
			this.amount.setErrorMessage("");
			this.amount.setVisible(false);
			this.amount.setValue(BigDecimal.ZERO);

			this.percentage.setVisible(true);

			this.calcOn.setVisible(true);
			this.label_CalcOn.setVisible(true);
			this.space_CalcOn.setVisible(true);
		}

	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param gSTRate The entity that need to be render.
	 */
	public void doShowDialog(GSTRate gSTRate) {
		logger.debug(Literal.ENTERING);

		if (gSTRate.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fromState.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(gSTRate.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.calcType.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(gSTRate);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.fromState.isReadonly()) {
			this.fromState.setConstraint(
					new PTStringValidator(Labels.getLabel("label_GSTRateDialog_FromState.value"), null, true));
		}
		if (!this.toState.isReadonly()) {
			this.toState.setConstraint(
					new PTStringValidator(Labels.getLabel("label_GSTRateDialog_ToState.value"), null, true));
		}
		if (!this.taxType.isDisabled()) {
			this.taxType.setConstraint(
					new StaticListValidator(listTaxType, Labels.getLabel("label_GSTRateDialog_TaxType.value")));
		}
		if (!this.calcType.isDisabled()) {
			this.calcType.setConstraint(
					new StaticListValidator(listCalcType, Labels.getLabel("label_GSTRateDialog_CalcType.value")));
		}
		if (!this.calcOn.isDisabled() && this.calcOn.isVisible()) {
			this.calcOn.setConstraint(
					new StaticListValidator(listCalcOn, Labels.getLabel("label_GSTRateDialog_CalcOn.value")));
		}

		String calcTypeVal = getComboboxValue(this.calcType);
		if (StringUtils.equals(RuleConstants.CALCTYPE_FIXED_AMOUNT, calcTypeVal)) {
			if (!this.amount.isDisabled() && (BigDecimal.ZERO.compareTo(this.amount.getActualValue()) != 0)) {
				this.amount.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_GSTRateDialog_AmountOrPercentage.value"),
								PennantConstants.defaultCCYDecPos, true, false, 0));
			}
		} else if (StringUtils.equals(RuleConstants.CALCTYPE_PERCENTAGE, calcTypeVal)) {
			if (!this.percentage.isReadonly() && (BigDecimal.ZERO.compareTo(this.percentage.getValue()) != 0)) {
				this.percentage.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_GSTRateDialog_Percentage.value"), 2, true, false, 99));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.fromState.setConstraint("");
		this.toState.setConstraint("");
		this.taxType.setConstraint("");
		this.calcType.setConstraint("");
		this.amount.setConstraint("");
		this.percentage.setConstraint("");
		this.calcOn.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final GSTRate aGSTRate = new GSTRate();
		BeanUtils.copyProperties(this.gSTRate, aGSTRate);

		doDelete(String.valueOf(aGSTRate.getId()), aGSTRate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.gSTRate.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.fromState);
			readOnlyComponent(false, this.toState);
			readOnlyComponent(false, this.taxType);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.fromState);
			readOnlyComponent(true, this.toState);
			readOnlyComponent(true, this.taxType);

		}

		readOnlyComponent(isReadOnly("GSTRateDialog_CalcType"), this.calcType);
		readOnlyComponent(isReadOnly("GSTRateDialog_Amount"), this.amount);
		readOnlyComponent(isReadOnly("GSTRateDialog_Percentage"), this.percentage);
		readOnlyComponent(isReadOnly("GSTRateDialog_CalcOn"), this.calcOn);
		readOnlyComponent(isReadOnly("GSTRateDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.gSTRate.isNewRecord()) {
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

		readOnlyComponent(true, this.fromState);
		readOnlyComponent(true, this.toState);
		readOnlyComponent(true, this.taxType);
		readOnlyComponent(true, this.calcType);
		readOnlyComponent(true, this.amount);
		readOnlyComponent(true, this.percentage);
		readOnlyComponent(true, this.calcOn);
		readOnlyComponent(true, this.active);

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
		this.fromState.setValue("");
		this.toState.setValue("");
		this.taxType.setSelectedIndex(0);
		this.calcType.setSelectedIndex(0);
		this.amount.setValue("");
		this.percentage.setValue("");
		this.calcOn.setSelectedIndex(0);
		this.active.setChecked(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final GSTRate aGSTRate = new GSTRate();
		BeanUtils.copyProperties(this.gSTRate, aGSTRate);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aGSTRate);

		isNew = aGSTRate.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aGSTRate.getRecordType())) {
				aGSTRate.setVersion(aGSTRate.getVersion() + 1);
				if (isNew) {
					aGSTRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aGSTRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGSTRate.setNewRecord(true);
				}
			}
		} else {
			aGSTRate.setVersion(aGSTRate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aGSTRate, tranType)) {
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
	protected boolean doProcess(GSTRate aGSTRate, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aGSTRate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aGSTRate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aGSTRate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aGSTRate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aGSTRate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aGSTRate);
				}

				if (isNotesMandatory(taskId, aGSTRate)) {
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

			aGSTRate.setTaskId(taskId);
			aGSTRate.setNextTaskId(nextTaskId);
			aGSTRate.setRoleCode(getRole());
			aGSTRate.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aGSTRate, tranType);
			String operationRefs = getServiceOperations(taskId, aGSTRate);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aGSTRate, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aGSTRate, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
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
		GSTRate aGSTRate = (GSTRate) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = gstRateService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = gstRateService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = gstRateService.doApprove(auditHeader);

					if (aGSTRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = gstRateService.doReject(auditHeader);
					if (aGSTRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_GSTRateDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_GSTRateDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.gSTRate), true);
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

	private AuditHeader getAuditHeader(GSTRate aGSTRate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aGSTRate.getBefImage(), aGSTRate);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aGSTRate.getUserDetails(),
				getOverideMap());
	}

	public GSTRateService getGstRateService() {
		return gstRateService;
	}

	public void setGstRateService(GSTRateService gstRateService) {
		this.gstRateService = gstRateService;
	}

}
