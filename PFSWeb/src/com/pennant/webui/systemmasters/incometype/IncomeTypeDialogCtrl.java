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
 * FileName    		:  IncomeTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.incometype;

import java.io.Serializable;
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
import org.zkoss.zul.Combobox;
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
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.IncomeTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/IncomeType/incomeTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class IncomeTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -9144099736284703562L;
	private final static Logger logger = Logger.getLogger(IncomeTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IncomeTypeDialog;// autoWired

	protected Combobox incomeExpense;
	protected Combobox category;
	protected Decimalbox margin;
	
	protected Textbox 		incomeTypeCode; 		// autoWired
	protected Textbox 		incomeTypeDesc; 		// autoWired
	protected Checkbox 		incomeTypeIsActive; 	// autoWired

	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not autoWired variables
	private IncomeType incomeType; // over handed per parameter
	private transient IncomeTypeListCtrl incomeTypeListCtrl; // over handed per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String 	oldVar_incomeTypeCode;
	private transient String 	oldVar_incomeTypeDesc;
	private transient boolean 	oldVar_incomeTypeIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_IncomeTypeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave;		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose; 		// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired

	// ServiceDAOs / Domain Classes
	private transient IncomeTypeService incomeTypeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public IncomeTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected IncomeType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IncomeTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("incomeType")) {
			this.incomeType = (IncomeType) args.get("incomeType");
			IncomeType befImage = new IncomeType();
			BeanUtils.copyProperties(this.incomeType, befImage);
			this.incomeType.setBefImage(befImage);

			setIncomeType(this.incomeType);
		} else {
			setIncomeType(null);
		}

		doLoadWorkFlow(this.incomeType.isWorkflow(),this.incomeType.getWorkflowId(),this.incomeType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"IncomeTypeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the incomeTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete incomeType here.
		if (args.containsKey("incomeTypeListCtrl")) {
			setIncomeTypeListCtrl((IncomeTypeListCtrl) args.get("incomeTypeListCtrl"));
		} else {
			setIncomeTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getIncomeType());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.incomeTypeCode.setMaxlength(8);
		this.incomeTypeDesc.setMaxlength(50);
		this.margin.setScale(2);
		this.margin.setFormat(PennantConstants.amountFormate2);
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

		getUserWorkspace().alocateAuthorities("IncomeTypeDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnSave"));
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
	public void onClose$window_IncomeTypeDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_IncomeTypeDialog);
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
		} catch (final WrongValueException e) {
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
			logger.debug("isDataChanged(): true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("doClose isDataChanged(): false");
		}

		if (close) {
			closeDialog(this.window_IncomeTypeDialog, "IncomeType");
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
	 * @param aIncomeType
	 *            IncomeType
	 */
	public void doWriteBeanToComponents(IncomeType aIncomeType) {
		logger.debug("Entering");
		fillComboBox(incomeExpense, aIncomeType.getIncomeExpense(), PennantStaticListUtil.getIncomeExpense(), "");
		fillComboBox(category, aIncomeType.getCategory(), PennantAppUtil.getIncomeExpenseCategory(), "");
		this.incomeTypeCode.setValue(aIncomeType.getIncomeTypeCode());
		this.incomeTypeDesc.setValue(aIncomeType.getIncomeTypeDesc());
		this.margin.setValue(PennantAppUtil.formateAmount(aIncomeType.getMargin(), 2));
		this.incomeTypeIsActive.setChecked(aIncomeType.isIncomeTypeIsActive());
		this.recordStatus.setValue(aIncomeType.getRecordStatus());
		
		if(aIncomeType.isNew() || (aIncomeType.getRecordType() != null ? aIncomeType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.incomeTypeIsActive.setChecked(true);
			this.incomeTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIncomeType
	 */
	public void doWriteComponentsToBean(IncomeType aIncomeType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.incomeExpense.getSelectedItem()==null || StringUtils.trimToEmpty(this.incomeExpense.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.incomeExpense,Labels.getLabel("STATIC_INVALID",new String[]{Labels.getLabel("label_IncomeTypeDialog_IncomeExpense.value")}));
			}
			
			aIncomeType.setIncomeExpense(this.incomeExpense.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.category.getSelectedItem()==null || StringUtils.trimToEmpty(this.category.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.category,Labels.getLabel("STATIC_INVALID",new String[]{Labels.getLabel("label_IncomeTypeDialog_Category.value")}));
			}
			aIncomeType.setCategory(this.category.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setIncomeTypeCode(this.incomeTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setIncomeTypeDesc(this.incomeTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setMargin(PennantAppUtil.unFormateAmount(this.margin.getValue(), 2));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setIncomeTypeIsActive(this.incomeTypeIsActive.isChecked());
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

		aIncomeType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aIncomeType
	 * @throws InterruptedException
	 */
	public void doShowDialog(IncomeType aIncomeType)throws InterruptedException {
		logger.debug("Entering");

		// if aIncomeType == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aIncomeType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aIncomeType = getIncomeTypeService().getNewIncomeType();
			setIncomeType(aIncomeType);
		} else {
			setIncomeType(aIncomeType);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aIncomeType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.incomeTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.incomeTypeDesc.focus();
				if (!StringUtils.trimToEmpty(aIncomeType.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aIncomeType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_IncomeTypeDialog);
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
	 * Stores the initialize values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_incomeTypeCode = this.incomeTypeCode.getValue();
		this.oldVar_incomeTypeDesc = this.incomeTypeDesc.getValue();
		this.oldVar_incomeTypeIsActive = this.incomeTypeIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.incomeTypeCode.setValue(this.oldVar_incomeTypeCode);
		this.incomeTypeDesc.setValue(this.oldVar_incomeTypeDesc);
		this.incomeTypeIsActive.setChecked(this.oldVar_incomeTypeIsActive);
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
		// To clear the Error Message
		doClearMessage();

		if(this.oldVar_incomeTypeCode != this.incomeTypeCode.getValue()){
			return true;
		}
		if (this.oldVar_incomeTypeDesc != this.incomeTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_incomeTypeIsActive != this.incomeTypeIsActive.isChecked()) {
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

		if (!this.incomeTypeCode.isReadonly()){
			this.incomeTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_IncomeTypeDialog_IncomeTypeCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.incomeTypeDesc.isReadonly()){
			this.incomeTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_IncomeTypeDialog_IncomeTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.margin.isReadonly()) {
			this.margin.setConstraint(new 	PTDecimalValidator(Labels.getLabel("label_IncomeTypeDialog_Margin.value"), 2,false,false,100));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.incomeTypeCode.setConstraint("");
		this.incomeTypeDesc.setConstraint("");
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
		this.incomeTypeCode.setErrorMessage("");
		this.incomeTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a IncomeType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final IncomeType aIncomeType = new IncomeType();
		BeanUtils.copyProperties(getIncomeType(), aIncomeType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + aIncomeType.getIncomeTypeCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aIncomeType.getRecordType()).equals("")) {
				aIncomeType.setVersion(aIncomeType.getVersion() + 1);
				aIncomeType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aIncomeType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aIncomeType, tranType)) {
					refreshList();
					closeDialog(this.window_IncomeTypeDialog, "IncomeType");
				}
			} catch (DataAccessException e) {
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new IncomeType object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new IncomeType() in the front end.
		// we get it from the back end.
		final IncomeType aIncomeType = getIncomeTypeService().getNewIncomeType();
		aIncomeType.setNewRecord(true);
		setIncomeType(aIncomeType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.incomeTypeCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getIncomeType().isNewRecord()) {
			this.incomeTypeCode.setReadonly(false);
			this.incomeExpense.setDisabled(false);
			this.category.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.incomeTypeCode.setReadonly(true);
			this.incomeExpense.setDisabled(true);
			this.category.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		this.incomeTypeDesc.setReadonly(isReadOnly("IncomeTypeDialog_incomeTypeDesc"));
		this.margin.setReadonly(isReadOnly("IncomeTypeDialog_incomeTypeDesc"));
		this.incomeTypeIsActive.setDisabled(isReadOnly("IncomeTypeDialog_incomeTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.incomeType.isNewRecord()) {
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

		this.incomeTypeCode.setReadonly(true);
		this.incomeTypeDesc.setReadonly(true);
		this.incomeTypeIsActive.setDisabled(true);

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
		this.incomeTypeCode.setValue("");
		this.incomeTypeDesc.setValue("");
		this.incomeTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final IncomeType aIncomeType = new IncomeType();
		BeanUtils.copyProperties(getIncomeType(), aIncomeType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the IncomeType object with the components data
		doWriteComponentsToBean(aIncomeType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aIncomeType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aIncomeType.getRecordType()).equals("")) {
				aIncomeType.setVersion(aIncomeType.getVersion() + 1);
				if (isNew) {
					aIncomeType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIncomeType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIncomeType.setNewRecord(true);
				}
			}
		} else {
			aIncomeType.setVersion(aIncomeType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aIncomeType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_IncomeTypeDialog, "IncomeType");
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
	 * @param aIncomeType
	 *            (IncomeType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(IncomeType aIncomeType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aIncomeType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aIncomeType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIncomeType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aIncomeType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIncomeType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aIncomeType);
				}
				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aIncomeType))) {
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
				nextRoleCode = getWorkFlow().firstTask.owner;

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
			aIncomeType.setTaskId(taskId);
			aIncomeType.setNextTaskId(nextTaskId);
			aIncomeType.setRoleCode(getRole());
			aIncomeType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aIncomeType, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aIncomeType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIncomeType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIncomeType, tranType);
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
		IncomeType aIncomeType = (IncomeType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getIncomeTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getIncomeTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getIncomeTypeService().doApprove(auditHeader);

						if (aIncomeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getIncomeTypeService().doReject(auditHeader);

						if (aIncomeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_IncomeTypeDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_IncomeTypeDialog, auditHeader);
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
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(IncomeType aIncomeType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aIncomeType.getBefImage(), aIncomeType);
		return new AuditHeader(String.valueOf(aIncomeType.getId()), null, null,
				null, auditDetail, aIncomeType.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_IncomeTypeDialog,auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		final JdbcSearchObject<IncomeType> soIncomeType = getIncomeTypeListCtrl().getSearchObj();
		getIncomeTypeListCtrl().pagingIncomeTypeList.setActivePage(0);
		getIncomeTypeListCtrl().getPagedListWrapper().setSearchObject(soIncomeType);
		if (getIncomeTypeListCtrl().listBoxIncomeType != null) {
			getIncomeTypeListCtrl().listBoxIncomeType.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("IncomeType");
		notes.setReference(getIncomeType().getIncomeTypeCode());
		notes.setVersion(getIncomeType().getVersion());
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

	public IncomeType getIncomeType() {
		return this.incomeType;
	}
	public void setIncomeType(IncomeType incomeType) {
		this.incomeType = incomeType;
	}

	public void setIncomeTypeService(IncomeTypeService incomeTypeService) {
		this.incomeTypeService = incomeTypeService;
	}
	public IncomeTypeService getIncomeTypeService() {
		return this.incomeTypeService;
	}

	public void setIncomeTypeListCtrl(IncomeTypeListCtrl incomeTypeListCtrl) {
		this.incomeTypeListCtrl = incomeTypeListCtrl;
	}
	public IncomeTypeListCtrl getIncomeTypeListCtrl() {
		return this.incomeTypeListCtrl;
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
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
}
