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
 * FileName    		:  InsurancePolicyDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2017    														*
 *                                                                  						*
 * Modified Date    :  06-02-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.InsurancePolicy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.InsurancePolicyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.PTCKeditor;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/InsurancePolicy/insurancePolicyDialog.zul file. <br>
 */
public class InsurancePolicyDialogCtrl extends GFCBaseCtrl<InsurancePolicy> implements Serializable {
	private static final long					serialVersionUID		= 1L;
	private static final Logger					logger					= Logger.getLogger(InsurancePolicyDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_InsurancePolicyDialog;
	protected Uppercasebox						policyCode;
	protected Textbox							policyDesc;
	protected ExtendedCombobox					insuranceType;
	protected ExtendedCombobox					insuranceProvider;
	protected Decimalbox						policyRate;
	protected PTCKeditor						features;
	protected Checkbox							active;

	protected Label								recordType;
	protected Groupbox							gb_statusDetails;
	private boolean								enqModule				= false;

	// not auto wired vars
	private InsurancePolicy						insurancePolicy;																// overhanded per param
	private transient InsurancePolicyListCtrl	insurancePolicyListCtrl;														// overhanded per param

	// Button controller for the CRUD buttons
	private transient final String				btnCtroller_ClassPrefix	= "button_InsurancePolicyDialog_";
	protected Button							btnHelp;

	// ServiceDAOs / Domain Classes
	private transient InsurancePolicyService	insurancePolicyService;
	private transient PagedListService			pagedListService;

	/**
	 * default constructor.<br>
	 */
	public InsurancePolicyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsurancePolicyDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected InsurancePolicy object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InsurancePolicyDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {

			// Set the page level components.
			setPageComponents(window_InsurancePolicyDialog);

			// READ OVERHANDED params !
			if (arguments.containsKey("insurancePolicy")) {
				this.insurancePolicy = (InsurancePolicy) arguments.get("insurancePolicy");
				InsurancePolicy befImage = new InsurancePolicy();
				BeanUtils.copyProperties(this.insurancePolicy, befImage);
				this.insurancePolicy.setBefImage(befImage);

				setInsurancePolicy(this.insurancePolicy);
			} else {
				setInsurancePolicy(null);
			}
			doLoadWorkFlow(this.insurancePolicy.isWorkflow(), this.insurancePolicy.getWorkflowId(),
					this.insurancePolicy.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "InsurancePolicyDialog");
			} else {
				getUserWorkspace().allocateAuthorities("InsurancePolicyDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the insurancePolicyListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete insurancePolicy here.
			if (arguments.containsKey("insurancePolicyListCtrl")) {
				setInsurancePolicyListCtrl((InsurancePolicyListCtrl) arguments.get("insurancePolicyListCtrl"));
			} else {
				setInsurancePolicyListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getInsurancePolicy());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.insurancePolicy.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_InsurancePolicyDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_InsurancePolicyDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		doShowNotes(this.insurancePolicy);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aInsurancePolicy
	 * @throws InterruptedException
	 */
	public void doShowDialog(InsurancePolicy aInsurancePolicy) throws InterruptedException {

		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aInsurancePolicy.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.policyCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.policyDesc.focus();
				if (StringUtils.isNotBlank(aInsurancePolicy.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aInsurancePolicy);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_InsurancePolicyDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");

	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.policyCode.setReadonly(true);
		this.policyDesc.setReadonly(true);
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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InsurancePolicyDialog_btnNew"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InsurancePolicyDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InsurancePolicyDialog_btnSave"));
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.policyCode.setMaxlength(8);
		this.policyDesc.setMaxlength(50);

		this.insuranceType.setMandatoryStyle(true);
		this.insuranceType.setTextBoxWidth(144);
		this.insuranceType.setModuleName("InsuranceType");
		this.insuranceType.setValueColumn("InsuranceType");
		this.insuranceType.setDescColumn("InsuranceTypeDesc");
		this.insuranceType.setValidateColumns(new String[] { "insuranceType" });

		this.insuranceProvider.setMandatoryStyle(true);
		this.insuranceProvider.setTextBoxWidth(144);
		this.insuranceProvider.setModuleName("TakafulProvider");
		this.insuranceProvider.setValueColumn("TakafulCode");
		this.insuranceProvider.setDescColumn("TakafulName");
		this.insuranceProvider.setValidateColumns(new String[] { "TakafulCode" });
		

		this.policyRate.setMaxlength(13);
		this.policyRate.setFormat(PennantConstants.rateFormate9);
		this.policyRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.policyRate.setScale(9);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aInsurancePolicy
	 *            InsurancePolicy
	 */
	public void doWriteBeanToComponents(InsurancePolicy aInsurancePolicy) {
		logger.debug("Entering");
		this.policyCode.setValue(aInsurancePolicy.getPolicyCode());
		this.policyDesc.setValue(aInsurancePolicy.getPolicyDesc());
		this.insuranceType.setValue(aInsurancePolicy.getInsuranceType());
		this.insuranceProvider.setValue(aInsurancePolicy.getInsuranceProvider());
		this.policyRate.setValue(aInsurancePolicy.getPolicyRate());
		this.features.setValue(aInsurancePolicy.getFeatures());
		if (!aInsurancePolicy.isNew()) {
			this.active.setChecked(aInsurancePolicy.isActive());
		}

		if (aInsurancePolicy.isNewRecord()) {
			this.insuranceType.setDescription("");
			this.insuranceProvider.setDescription("");
		} else {
			this.insuranceType.setDescription(aInsurancePolicy.getInsuranceTypeDesc());
			this.insuranceProvider.setDescription(aInsurancePolicy.getTakafulName());
		}
		this.recordStatus.setValue(aInsurancePolicy.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInsurancePolicy
	 */
	public void doWriteComponentsToBean(InsurancePolicy aInsurancePolicy) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Policy Code
		try {
			aInsurancePolicy.setPolicyCode(this.policyCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Policy Description
		try {
			aInsurancePolicy.setPolicyDesc(this.policyDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Insurance Type
		try {
			aInsurancePolicy.setInsuranceType(this.insuranceType.getValidatedValue());
			aInsurancePolicy.setInsuranceTypeDesc(this.insuranceType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Insurance Provider
		try {
			aInsurancePolicy.setInsuranceProvider(this.insuranceProvider.getValidatedValue());
			aInsurancePolicy.setTakafulName(this.insuranceProvider.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Policy Rate
		try {
			aInsurancePolicy.setPolicyRate(this.policyRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Features
		try {
			aInsurancePolicy.setFeatures(this.features.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Active
		try {
			aInsurancePolicy.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Policy Code
		if (!this.policyCode.isReadonly()) {
			this.policyCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InsurancePolicyDialog_PolicyCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		//Policy Description
		if (!this.policyDesc.isReadonly()) {
			this.policyDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InsurancePolicyDialog_PolicyDesc.value"), PennantRegularExpressions.REGEX_NAME,
					true));
		}
		if (!this.insuranceType.isReadonly()) {
			this.insuranceType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinTypeInsuranceDialog_InsuranceType.value"), null, true, true));
		}
		//Insurance Provider
		if (this.insuranceProvider.isButtonVisible()) {
			this.insuranceProvider.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InsurancePolicyDialog_InsuranceProvider.value"), null, true, true));
		}
		//Policy Rate
		if (!this.policyRate.isDisabled()) {
			this.policyRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_InsurancePolicyDialog_PolicyRate.value"), 4, true, false, 0, 9999));
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.policyCode.setConstraint("");
		this.policyDesc.setConstraint("");
		this.insuranceType.setConstraint("");
		this.insuranceProvider.setConstraint("");
		this.policyRate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.policyCode.setErrorMessage("");
		this.policyDesc.setErrorMessage("");
		this.insuranceType.setErrorMessage("");
		this.insuranceProvider.setErrorMessage("");
		this.policyRate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList() {
		getInsurancePolicyListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.insurancePolicy.getPolicyCode());
	}

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
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doSave();
				close = false;
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog();
		}

		logger.debug("Leaving");
	}

	/**
	 * Deletes a InsurancePolicy object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final InsurancePolicy aInsurancePolicy = new InsurancePolicy();
		BeanUtils.copyProperties(getInsurancePolicy(), aInsurancePolicy);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aInsurancePolicy.getInsuranceProvider();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if ("".equals(StringUtils.trimToEmpty(aInsurancePolicy.getRecordType()))) {
				aInsurancePolicy.setVersion(aInsurancePolicy.getVersion() + 1);
				aInsurancePolicy.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aInsurancePolicy.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aInsurancePolicy.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aInsurancePolicy.getNextTaskId(),
							aInsurancePolicy);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aInsurancePolicy, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getInsurancePolicy().isNewRecord()) {
			this.policyCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.active.setChecked(true);
			this.active.setDisabled(true);
		} else {
			this.policyCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			readOnlyComponent(isReadOnly("InsurancePolicyDialog_Active"), this.active);
		}
		this.policyDesc.setReadonly(isReadOnly("InsurancePolicyDialog_PolicyDesc"));
		this.insuranceType.setReadonly(isReadOnly("InsurancePolicyDialog_InsuranceType"));
		this.insuranceProvider.setReadonly(isReadOnly("InsurancePolicyDialog_InsuranceProvider"));
		this.policyRate.setReadonly(isReadOnly("InsurancePolicyDialog_PolicyRate"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.insurancePolicy.isNewRecord()) {
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.policyCode.setValue("");
		this.policyDesc.setValue("");
		this.insuranceType.setValue("");
		this.insuranceType.setDescription("");
		this.insuranceProvider.setValue("");
		this.insuranceProvider.setDescription("");
		this.policyRate.setValue("");
		this.features.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final InsurancePolicy aInsurancePolicy = new InsurancePolicy();
		BeanUtils.copyProperties(getInsurancePolicy(), aInsurancePolicy);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aInsurancePolicy.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aInsurancePolicy.getNextTaskId(),
					aInsurancePolicy);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aInsurancePolicy.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the InsurancePolicy object with the components data
			doWriteComponentsToBean(aInsurancePolicy);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aInsurancePolicy.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if ("".equals(StringUtils.trimToEmpty(aInsurancePolicy.getRecordType()))) {
				aInsurancePolicy.setVersion(aInsurancePolicy.getVersion() + 1);
				if (isNew) {
					aInsurancePolicy.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInsurancePolicy.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInsurancePolicy.setNewRecord(true);
				}
			}
		} else {
			aInsurancePolicy.setVersion(aInsurancePolicy.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aInsurancePolicy, tranType)) {
				//doWriteBeanToComponents(aInsurancePolicy);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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

	private boolean doProcess(InsurancePolicy aInsurancePolicy, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		aInsurancePolicy.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aInsurancePolicy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInsurancePolicy.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aInsurancePolicy.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aInsurancePolicy.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aInsurancePolicy);
				}

				if (isNotesMandatory(taskId, aInsurancePolicy)) {
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
			aInsurancePolicy.setTaskId(taskId);
			aInsurancePolicy.setNextTaskId(nextTaskId);
			aInsurancePolicy.setRoleCode(getRole());
			aInsurancePolicy.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aInsurancePolicy, tranType);
			String operationRefs = getServiceOperations(taskId, aInsurancePolicy);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aInsurancePolicy, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aInsurancePolicy, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
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

		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		InsurancePolicy insurancePolicy = (InsurancePolicy) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getInsurancePolicyService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getInsurancePolicyService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getInsurancePolicyService().doApprove(auditHeader);

						if (insurancePolicy.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getInsurancePolicyService().doReject(auditHeader);

						if (insurancePolicy.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InsurancePolicyDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InsurancePolicyDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.insurancePolicy), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;

	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(InsurancePolicy aInsurancePolicy, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aInsurancePolicy.getBefImage(), aInsurancePolicy);
		return new AuditHeader(aInsurancePolicy.getInsuranceProvider(), null, null, null, auditDetail,
				aInsurancePolicy.getUserDetails(), getOverideMap());
	}

	public InsurancePolicy getInsurancePolicy() {
		return this.insurancePolicy;
	}

	public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
		this.insurancePolicy = insurancePolicy;
	}

	public void setInsurancePolicyService(InsurancePolicyService insurancePolicyService) {
		this.insurancePolicyService = insurancePolicyService;
	}

	public InsurancePolicyService getInsurancePolicyService() {
		return this.insurancePolicyService;
	}

	public void setInsurancePolicyListCtrl(InsurancePolicyListCtrl insurancePolicyListCtrl) {
		this.insurancePolicyListCtrl = insurancePolicyListCtrl;
	}

	public InsurancePolicyListCtrl getInsurancePolicyListCtrl() {
		return this.insurancePolicyListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
