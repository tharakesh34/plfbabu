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
 * FileName    		:  CustomerCategoryDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.customercategory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.CustomerCategoryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerCategory/CustomerCategoryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerCategoryDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8072326502727052098L;
	private final static Logger logger = Logger.getLogger(CustomerCategoryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CustomerCategoryDialog; 	// autoWired

	protected Textbox 	custCtgCode; 					// autoWired
	protected Textbox 	custCtgDesc; 					// autoWired
	protected Combobox 	custCtgType; 					// autoWired
	protected Checkbox 	custCtgIsActive; 				// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not autoWired variables
	private CustomerCategory customerCategory; 		// over handed per parameter
	private transient CustomerCategoryListCtrl customerCategoryListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_custCtgCode;
	private transient String 	oldVar_custCtgDesc;
	private transient String 	oldVar_custCtgType;
	private transient boolean 	oldVar_custCtgIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerCategoryDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave; 		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose;	 	// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired

	// ServiceDAOs / Domain Classes
	private transient CustomerCategoryService customerCategoryService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public CustomerCategoryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerCategory
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerCategoryDialog(Event event) throws Exception {
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
		if (args.containsKey("customerCategory")) {
			this.customerCategory = (CustomerCategory) args.get("customerCategory");
			CustomerCategory befImage = new CustomerCategory();
			BeanUtils.copyProperties(this.customerCategory, befImage);
			this.customerCategory.setBefImage(befImage);
			setCustomerCategory(this.customerCategory);
		} else {
			setCustomerCategory(null);
		}

		doLoadWorkFlow(this.customerCategory.isWorkflow(), this.customerCategory.getWorkflowId(),
				this.customerCategory.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerCategoryDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerCategoryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerCategory here.
		if (args.containsKey("customerCategoryListCtrl")) {
			setCustomerCategoryListCtrl((CustomerCategoryListCtrl) args.get("customerCategoryListCtrl"));
		} else {
			setCustomerCategoryListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerCategory());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.custCtgCode.setMaxlength(8);
		this.custCtgDesc.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("CustomerCategoryDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerCategoryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerCategoryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerCategoryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerCategoryDialog_btnSave"));
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
	public void onClose$window_CustomerCategoryDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerCategoryDialog);
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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_CustomerCategoryDialog, "CustomerCategory");
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
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerCategory
	 *            CustomerCategory
	 */
	public void doWriteBeanToComponents(CustomerCategory aCustomerCategory) {
		logger.debug("Entering");

		this.custCtgCode.setValue(aCustomerCategory.getCustCtgCode());
		this.custCtgDesc.setValue(aCustomerCategory.getCustCtgDesc());

		String catType = "";
		if (StringUtils.trimToEmpty(aCustomerCategory.getCustCtgType()).length() > 0) {
			catType = StringUtils.trimToEmpty(aCustomerCategory.getCustCtgType()).substring(0, 1);
		}
		List<ValueLabel> categoryTypeList = PennantStaticListUtil.getCategoryType();
		Comboitem comboitem;
		for (int i = 0; i < categoryTypeList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(categoryTypeList.get(i).getValue());
			comboitem.setLabel(categoryTypeList.get(i).getLabel());
			custCtgType.appendChild(comboitem);
			if (StringUtils.trimToEmpty(catType).equals(categoryTypeList.get(i).getValue())) {
				custCtgType.setSelectedItem(comboitem);
			}
		}
		this.custCtgIsActive.setChecked(aCustomerCategory.isCustCtgIsActive());
		this.recordStatus.setValue(aCustomerCategory.getRecordStatus());
		
		if(aCustomerCategory.isNew() || (aCustomerCategory.getRecordType() != null ? aCustomerCategory.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.custCtgIsActive.setChecked(true);
			this.custCtgIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerCategory
	 */
	public void doWriteComponentsToBean(CustomerCategory aCustomerCategory) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerCategory.setCustCtgCode(this.custCtgCode.getValue()
					.toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerCategory.setCustCtgDesc(this.custCtgDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(!this.custCtgType.isDisabled() && this.custCtgType.getSelectedIndex()<0){
				throw new WrongValueException(custCtgType, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_CustomerCategoryDialog_CustCtgType.value")}));
			}
			aCustomerCategory.setCustCtgType(this.custCtgType.getSelectedItem()
					.getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerCategory.setCustCtgIsActive(this.custCtgIsActive
					.isChecked());
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

		aCustomerCategory.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerCategory
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerCategory aCustomerCategory) throws InterruptedException {
		logger.debug("Entering");

		// if aCustomerCategory == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerCategory == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aCustomerCategory = getCustomerCategoryService().getNewCustomerCategory();
			setCustomerCategory(aCustomerCategory);
		} else {
			setCustomerCategory(aCustomerCategory);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aCustomerCategory.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCtgCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.custCtgDesc.focus();
				if (!StringUtils.trimToEmpty(aCustomerCategory.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aCustomerCategory);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerCategoryDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		this.oldVar_custCtgCode = this.custCtgCode.getValue();
		this.oldVar_custCtgDesc = this.custCtgDesc.getValue();
		/*if (this.custCtgType.getSelectedItem() != null) {
			this.oldVar_custCtgType = this.custCtgType.getSelectedItem().getValue().toString();
		}*/
		this.oldVar_custCtgType = this.custCtgType.getValue();
		this.oldVar_custCtgIsActive = this.custCtgIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.custCtgCode.setValue(this.oldVar_custCtgCode);
		this.custCtgDesc.setValue(this.oldVar_custCtgDesc);
		/*for (int i = 0; i < custCtgType.getItemCount(); i++) {
			if (this.custCtgType.getSelectedItem().getValue().equals(this.oldVar_custCtgType)) {
				this.custCtgType.setSelectedIndex(i);
				break;
			}
			this.custCtgType.setSelectedIndex(0);
		}*/
		this.custCtgType.setValue(this.oldVar_custCtgType);
		this.custCtgIsActive.setChecked(this.oldVar_custCtgIsActive);
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
		// To Clear the Error Messages
		doClearMessage();

		if (this.oldVar_custCtgCode != this.custCtgCode.getValue()) {
			return true;
		}
		if (this.oldVar_custCtgDesc != this.custCtgDesc.getValue()) {
			return true;
		}
		if (this.oldVar_custCtgType != this.custCtgType.getValue()) {
			return true;
		}
		if (this.oldVar_custCtgIsActive != this.custCtgIsActive.isChecked()) {
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

		if (!this.custCtgCode.isReadonly()){
			this.custCtgCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerCategoryDialog_CustCtgCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.custCtgDesc.isReadonly()){
			this.custCtgDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerCategoryDialog_CustCtgDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.custCtgType.isReadonly()) {
			this.custCtgType.setConstraint(new PTStringValidator(Labels.getLabel(
					"label_CustomerCategoryDialog_CustCtgType.value"), null, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCtgCode.setConstraint("");
		this.custCtgDesc.setConstraint("");
		this.custCtgType.setConstraint("");
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
		this.custCtgCode.setErrorMessage("");
		this.custCtgDesc.setErrorMessage("");
		this.custCtgType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerCategory object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerCategory aCustomerCategory = new CustomerCategory();
		BeanUtils.copyProperties(getCustomerCategory(), aCustomerCategory);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerCategory.getCustCtgCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerCategory.getRecordType()).equals("")) {
				aCustomerCategory.setVersion(aCustomerCategory.getVersion() + 1);
				aCustomerCategory.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCustomerCategory.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCustomerCategory, tranType)) {
					refreshList();
					closeDialog(this.window_CustomerCategoryDialog, "CustomerCategory");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerCategory object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerCategory() in the front end.
		// we get it from the back end.
		final CustomerCategory aCustomerCategory = getCustomerCategoryService().getNewCustomerCategory();
		aCustomerCategory.setNewRecord(true);
		setCustomerCategory(aCustomerCategory);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.custCtgCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCustomerCategory().isNewRecord()) {
			this.custCtgCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custCtgCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		
		this.custCtgDesc.setReadonly(isReadOnly("CustomerCategoryDialog_custCtgDesc"));
		this.custCtgType.setDisabled(isReadOnly("CustomerCategoryDialog_custCtgType"));
		this.custCtgIsActive.setDisabled(isReadOnly("CustomerCategoryDialog_custCtgIsActive"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerCategory.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.custCtgCode.setReadonly(true);
		this.custCtgDesc.setReadonly(true);
		this.custCtgType.setDisabled(true);
		this.custCtgIsActive.setDisabled(true);

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
		this.custCtgCode.setValue("");
		this.custCtgDesc.setValue("");
		this.custCtgIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerCategory aCustomerCategory = new CustomerCategory();
		BeanUtils.copyProperties(getCustomerCategory(), aCustomerCategory);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerCategory object with the components data
		doWriteComponentsToBean(aCustomerCategory);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomerCategory.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerCategory.getRecordType())
					.equals("")) {
				aCustomerCategory.setVersion(aCustomerCategory.getVersion() + 1);
				if (isNew) {
					aCustomerCategory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerCategory.setNewRecord(true);
				}
			}
		} else {
			aCustomerCategory.setVersion(aCustomerCategory.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCustomerCategory, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerCategoryDialog, "CustomerCategory");
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
	 * @param aCustomerCategory
	 *            (CustomerCategory)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(CustomerCategory aCustomerCategory, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerCategory.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerCategory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerCategory.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerCategory.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerCategory.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerCategory);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomerCategory))) {
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

			aCustomerCategory.setTaskId(taskId);
			aCustomerCategory.setNextTaskId(nextTaskId);
			aCustomerCategory.setRoleCode(getRole());
			aCustomerCategory.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerCategory, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aCustomerCategory);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerCategory, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aCustomerCategory, tranType);
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
		CustomerCategory aCustomerCategory = (CustomerCategory) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerCategoryService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerCategoryService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerCategoryService().doApprove(auditHeader);

						if (aCustomerCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerCategoryService().doReject(auditHeader);

						if (aCustomerCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerCategoryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerCategoryDialog, auditHeader);
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
	 * @param aCustomerCategory
	 *            (CustomerCategory)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(CustomerCategory aCustomerCategory,
			String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerCategory.getBefImage(), aCustomerCategory);
		return new AuditHeader(String.valueOf(aCustomerCategory.getId()), null,
				null, null, auditDetail, aCustomerCategory.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerCategoryDialog, auditHeader);
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
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
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
		final JdbcSearchObject<CustomerCategory> soCustomerCategory = getCustomerCategoryListCtrl().getSearchObj();
		getCustomerCategoryListCtrl().pagingCustomerCategoryList.setActivePage(0);
		getCustomerCategoryListCtrl().getPagedListWrapper().setSearchObject(soCustomerCategory);
		if (getCustomerCategoryListCtrl().listBoxCustomerCategory != null) {
			getCustomerCategoryListCtrl().listBoxCustomerCategory.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerCategory");
		notes.setReference(getCustomerCategory().getCustCtgCode());
		notes.setVersion(getCustomerCategory().getVersion());
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

	public CustomerCategory getCustomerCategory() {
		return this.customerCategory;
	}
	public void setCustomerCategory(CustomerCategory customerCategory) {
		this.customerCategory = customerCategory;
	}

	public void setCustomerCategoryService(CustomerCategoryService customerCategoryService) {
		this.customerCategoryService = customerCategoryService;
	}
	public CustomerCategoryService getCustomerCategoryService() {
		return this.customerCategoryService;
	}

	public void setCustomerCategoryListCtrl(CustomerCategoryListCtrl customerCategoryListCtrl) {
		this.customerCategoryListCtrl = customerCategoryListCtrl;
	}
	public CustomerCategoryListCtrl getCustomerCategoryListCtrl() {
		return this.customerCategoryListCtrl;
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
