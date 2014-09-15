/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CountryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.country;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.CountryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Country/CountryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CountryDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 223801324705386693L;

	private final static Logger logger = Logger.getLogger(CountryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CountryDialog; 		// autoWired

	protected Textbox 		countryCode; 				// autoWired
	protected Textbox 		countryDesc; 				// autoWired
	protected Decimalbox 	countryParentLimit; 		// autoWired
	protected Decimalbox 	countryResidenceLimit; 		// autoWired
	protected Decimalbox 	countryRiskLimit; 			// autoWired
	protected Checkbox 		countryIsActive; 			// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	
	// not autoWired variables
	private Country country; 					// over handed per parameter
	private transient CountryListCtrl countryListCtrl; // overHanded per
	// parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 		oldVar_countryCode;
	private transient String 		oldVar_countryDesc;
	private transient BigDecimal 	oldVar_countryParentLimit;
	private transient BigDecimal 	oldVar_countryResidenceLimit;
	private transient BigDecimal 	oldVar_countryRiskLimit;
	private transient boolean 		oldVar_countryIsActive;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CountryDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave; 		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose; 		// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired

	// ServiceDAOs / Domain Classes
	private transient CountryService countryService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public CountryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Country object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CountryDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("country")) {
			this.country = (Country) args.get("country");
			Country befImage = new Country();
			BeanUtils.copyProperties(this.country, befImage);
			this.country.setBefImage(befImage);

			setCountry(this.country);
		} else {
			setCountry(null);
		}

		doLoadWorkFlow(this.country.isWorkflow(), this.country.getWorkflowId(), this.country.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CountryDialog");
		}

		// READ OVERHANDED parameters !
		// we get the countryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete country here.
		if (args.containsKey("countryListCtrl")) {
			setCountryListCtrl((CountryListCtrl) args.get("countryListCtrl"));
		} else {
			setCountryListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCountry());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.countryCode.setMaxlength(2);
		this.countryDesc.setMaxlength(50);
		this.countryParentLimit.setMaxlength(21);
		this.countryParentLimit.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.countryParentLimit.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.countryParentLimit.setScale(0);
		this.countryResidenceLimit.setMaxlength(21);
		this.countryResidenceLimit.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.countryResidenceLimit.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.countryResidenceLimit.setScale(0);
		this.countryRiskLimit.setMaxlength(21);
		this.countryRiskLimit.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.countryRiskLimit.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.countryRiskLimit.setScale(0);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("CountryDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CountryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CountryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CountryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CountryDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CountryDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CountryDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,  MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeDialog(this.window_CountryDialog, "Country");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCountry
	 *            Country
	 */
	public void doWriteBeanToComponents(Country aCountry) {
		logger.debug("Entering");

		this.countryCode.setValue(aCountry.getCountryCode());
		this.countryDesc.setValue(aCountry.getCountryDesc());
		this.countryParentLimit.setValue(PennantAppUtil.formateAmount(aCountry.getCountryParentLimit(), 0));
		this.countryResidenceLimit.setValue(PennantAppUtil.formateAmount(aCountry.getCountryResidenceLimit(), 0));
		this.countryRiskLimit.setValue(PennantAppUtil.formateAmount(aCountry.getCountryRiskLimit(), 0));
		this.countryIsActive.setChecked(aCountry.isCountryIsActive());
		this.recordStatus.setValue(aCountry.getRecordStatus());
		
		if(aCountry.isNew() || (aCountry.getRecordType() != null ? aCountry.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.countryIsActive.setChecked(true);
			this.countryIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCountry
	 */
	public void doWriteComponentsToBean(Country aCountry) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCountry.setCountryCode(this.countryCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCountry.setCountryDesc(this.countryDesc.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCountry.setCountryParentLimit(PennantAppUtil.unFormateAmount(
					this.countryParentLimit.getValue(), 0));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCountry.setCountryResidenceLimit(PennantAppUtil.unFormateAmount(
					this.countryResidenceLimit.getValue(), 0));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCountry.setCountryRiskLimit(PennantAppUtil.unFormateAmount(
					this.countryRiskLimit.getValue(), 0));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCountry.setCountryIsActive(this.countryIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCountry.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCountry
	 * @throws InterruptedException
	 */
	public void doShowDialog(Country aCountry) throws InterruptedException {
		logger.debug("Entering");

		// if aCountry == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCountry == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aCountry = getCountryService().getNewCountry();

			setCountry(aCountry);
		} else {
			setCountry(aCountry);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aCountry.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.countryCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.countryDesc.focus();
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aCountry);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(window_CountryDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initialized values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		
		this.oldVar_countryCode = this.countryCode.getValue();
		this.oldVar_countryDesc = this.countryDesc.getValue();
		this.oldVar_countryParentLimit = this.countryParentLimit.getValue();
		this.oldVar_countryResidenceLimit = this.countryResidenceLimit.getValue();
		this.oldVar_countryRiskLimit = this.countryRiskLimit.getValue();
		this.oldVar_countryIsActive = this.countryIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialized values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.countryCode.setValue(this.oldVar_countryCode);
		this.countryDesc.setValue(this.oldVar_countryDesc);
		this.countryParentLimit.setValue(this.oldVar_countryParentLimit);
		this.countryResidenceLimit.setValue(this.oldVar_countryResidenceLimit);
		this.countryRiskLimit.setValue(this.oldVar_countryRiskLimit);
		this.countryIsActive.setChecked(this.oldVar_countryIsActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		// To clear the error Messages
		doClearMessage();

		if (this.oldVar_countryCode != this.countryCode.getValue()) {
			return true;
		}
		if (this.oldVar_countryDesc != this.countryDesc.getValue()) {
			return true;
		}
		if (this.oldVar_countryParentLimit != this.countryParentLimit.getValue()) {
			return true;
		}
		if (this.oldVar_countryResidenceLimit != this.countryResidenceLimit.getValue()) {
			return true;
		}
		if (this.oldVar_countryRiskLimit != this.countryRiskLimit.getValue()) {
			return true;
		}
		if (this.oldVar_countryIsActive != this.countryIsActive.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.countryCode.isReadonly()){
			this.countryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CountryDialog_CountryCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.countryDesc.isReadonly()){
			this.countryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CountryDialog_CountryDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.countryParentLimit.isReadonly()) {
			this.countryParentLimit.setConstraint(new AmountValidator(21, 0, Labels.getLabel(
			"label_CountryDialog_CountryParentLimit.value")));
		}
		if (!this.countryResidenceLimit.isReadonly()) {
			this.countryResidenceLimit.setConstraint(new AmountValidator(21, 0,Labels.getLabel(
			"label_CountryDialog_CountryResidenceLimit.value")));
		}
		if (!this.countryRiskLimit.isReadonly()) {
			this.countryRiskLimit.setConstraint(new AmountValidator(21, 0,Labels.getLabel(
			"label_CountryDialog_CountryRiskLimit.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.countryCode.setConstraint("");
		this.countryDesc.setConstraint("");
		this.countryParentLimit.setConstraint("");
		this.countryResidenceLimit.setConstraint("");
		this.countryRiskLimit.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.countryCode.setErrorMessage("");
		this.countryDesc.setErrorMessage("");
		this.countryParentLimit.setErrorMessage("");
		this.countryResidenceLimit.setErrorMessage("");
		this.countryRiskLimit.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Country object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Country aCountry = new Country();
		BeanUtils.copyProperties(getCountry(), aCountry);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCountry.getCountryCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCountry.getRecordType()).equals("")) {
				aCountry.setVersion(aCountry.getVersion() + 1);
				aCountry.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCountry.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCountry, tranType)) {
					refreshList();
					closeDialog(this.window_CountryDialog, "Country");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Country object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Country() in the front end.
		// we get it from the back end.
		final Country aCountry = getCountryService().getNewCountry();
		aCountry.setNewRecord(true);
		setCountry(aCountry);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.countryCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCountry().isNewRecord()) {
			this.countryCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.countryCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.countryDesc.setReadonly(isReadOnly("CountryDialog_countryDesc"));
		this.countryParentLimit.setReadonly(isReadOnly("CountryDialog_countryParentLimit"));
		this.countryResidenceLimit.setReadonly(isReadOnly("CountryDialog_countryResidenceLimit"));
		this.countryRiskLimit.setReadonly(isReadOnly("CountryDialog_countryRiskLimit"));
		this.countryIsActive.setDisabled(isReadOnly("CountryDialog_countryIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.country.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.countryCode.setReadonly(true);
		this.countryDesc.setReadonly(true);
		this.countryParentLimit.setReadonly(true);
		this.countryResidenceLimit.setReadonly(true);
		this.countryRiskLimit.setReadonly(true);
		this.countryIsActive.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.countryCode.setValue("");
		this.countryDesc.setValue("");
		this.countryParentLimit.setValue("");
		this.countryResidenceLimit.setValue("");
		this.countryRiskLimit.setValue("");
		this.countryIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Country aCountry = new Country();
		BeanUtils.copyProperties(getCountry(), aCountry);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Country object with the components data
		doWriteComponentsToBean(aCountry);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCountry.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCountry.getRecordType()).equals("")) {
				aCountry.setVersion(aCountry.getVersion() + 1);
				if (isNew) {
					aCountry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCountry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCountry.setNewRecord(true);
				}
			}
		} else {
			aCountry.setVersion(aCountry.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aCountry, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CountryDialog, "Country");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCountry
	 *            (Country)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Country aCountry, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCountry.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCountry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCountry.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCountry.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCountry.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCountry);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCountry))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCountry.setTaskId(taskId);
			aCountry.setNextTaskId(nextTaskId);
			aCountry.setRoleCode(getRole());
			aCountry.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCountry, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aCountry);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCountry, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCountry, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Country aCountry = (Country) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCountryService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCountryService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCountryService().doApprove(auditHeader);

						if (aCountry.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCountryService().doReject(auditHeader);

						if (aCountry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CountryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CountryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCountry
	 *            (Country)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Country aCountry, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCountry.getBefImage(), aCountry);
		return new AuditHeader(String.valueOf(aCountry.getId()), null, null,
				null, auditDetail, aCountry.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CountryDialog,auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {

		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<Country> soCountry = getCountryListCtrl().getSearchObj();
		getCountryListCtrl().pagingCountryList.setActivePage(0);
		getCountryListCtrl().getPagedListWrapper().setSearchObject(soCountry);
		if (getCountryListCtrl().listBoxCountry != null) {
			getCountryListCtrl().listBoxCountry.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Country");
		notes.setReference(getCountry().getCountryCode());
		notes.setVersion(getCountry().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public Country getCountry() {
		return this.country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}
	public CountryService getCountryService() {
		return this.countryService;
	}

	public void setCountryListCtrl(CountryListCtrl countryListCtrl) {
		this.countryListCtrl = countryListCtrl;
	}
	public CountryListCtrl getCountryListCtrl() {
		return this.countryListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

}
