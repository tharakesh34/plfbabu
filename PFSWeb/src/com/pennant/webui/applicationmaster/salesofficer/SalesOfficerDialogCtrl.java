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
 * FileName    		:  SalesOfficerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.salesofficer;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.SalesOfficerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SalesOfficer/salesOfficerDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class SalesOfficerDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 352659757425874223L;
	private final static Logger logger = Logger.getLogger(SalesOfficerDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_SalesOfficerDialog; 	// autoWired
	protected Textbox 	salesOffCode; 				// autoWired
	protected Textbox 	salesOffFName; 				// autoWired
	protected Textbox 	salesOffMName; 				// autoWired
	protected Textbox 	salesOffLName;			    // autoWired
	protected Textbox 	salesOffShrtName; 			// autoWired
	protected ExtendedCombobox 	salesOffDept; 				// autoWired
	protected Checkbox 	salesOffIsActive; 			// autoWired

	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired variables
	private SalesOfficer salesOfficer; 							 // overHanded per parameter
	private SalesOfficer prvSalesOfficer; 						 // overHanded per parameter
	private transient SalesOfficerListCtrl salesOfficerListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_salesOffCode;
	private transient String  		oldVar_salesOffFName;
	private transient String  		oldVar_salesOffMName;
	private transient String  		oldVar_salesOffLName;
	private transient String  		oldVar_salesOffShrtName;
	private transient String  		oldVar_salesOffDept;
	private transient boolean  		oldVar_salesOffIsActive;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SalesOfficerDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 				// autoWire
	protected Button btnEdit; 				// autoWire
	protected Button btnDelete; 			// autoWire
	protected Button btnSave; 				// autoWire
	protected Button btnCancel; 			// autoWire
	protected Button btnClose; 				// autoWire
	protected Button btnHelp; 				// autoWire
	protected Button btnNotes;		 		// autoWire
	
	private transient String 		oldVar_lovDescSalesOffDeptName;
	
	// ServiceDAOs / Domain Classes
	private transient SalesOfficerService salesOfficerService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public SalesOfficerDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SalesOfficer object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalesOfficerDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("salesOfficer")) {
			this.salesOfficer = (SalesOfficer) args.get("salesOfficer");
			SalesOfficer befImage = new SalesOfficer();
			BeanUtils.copyProperties(this.salesOfficer, befImage);
			this.salesOfficer.setBefImage(befImage);

			setSalesOfficer(this.salesOfficer);
		} else {
			setSalesOfficer(null);
		}

		doLoadWorkFlow(this.salesOfficer.isWorkflow(), this.salesOfficer.getWorkflowId(),
				this.salesOfficer.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SalesOfficerDialog");
		}

		// READ OVERHANDED parameters !
		// we get the salesOfficerListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete salesOfficer here.
		if (args.containsKey("salesOfficerListCtrl")) {
			setSalesOfficerListCtrl((SalesOfficerListCtrl) args
					.get("salesOfficerListCtrl"));
		} else {
			setSalesOfficerListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSalesOfficer());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.salesOffCode.setMaxlength(8);
		this.salesOffFName.setMaxlength(50);
		this.salesOffMName.setMaxlength(50);
		this.salesOffLName.setMaxlength(50);
		this.salesOffShrtName.setMaxlength(50);
		this.salesOffDept.setMaxlength(8);

		this.salesOffDept.setMandatoryStyle(true);
		this.salesOffDept.setModuleName("Department");
		this.salesOffDept.setValueColumn("DeptCode");
		this.salesOffDept.setDescColumn("DeptDesc");
		this.salesOffDept.setValidateColumns(new String[]{"DeptCode"});
		
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

		getUserWorkspace().alocateAuthorities("SalesOfficerDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnSave"));
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
	public void onClose$window_SalesOfficerDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving");
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
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
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
		PTMessageUtils.showHelpWindow(event, window_SalesOfficerDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving");
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
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving");
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
			throw e;
		}
		logger.debug("Leaving");
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
		logger.debug("Enterring");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_SalesOfficerDialog, "SalesOfficer");
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
	 * @param aSalesOfficer
	 *            SalesOfficer
	 */
	public void doWriteBeanToComponents(SalesOfficer aSalesOfficer) {
		logger.debug("Entering");
		this.salesOffCode.setValue(aSalesOfficer.getSalesOffCode());
		this.salesOffFName.setValue(aSalesOfficer.getSalesOffFName());
		this.salesOffMName.setValue(aSalesOfficer.getSalesOffMName());
		this.salesOffLName.setValue(aSalesOfficer.getSalesOffLName());
		this.salesOffShrtName.setValue(aSalesOfficer.getSalesOffShrtName());
		this.salesOffDept.setValue(aSalesOfficer.getSalesOffDept());
		this.salesOffIsActive.setChecked(aSalesOfficer.isSalesOffIsActive());

		if (aSalesOfficer.isNewRecord()) {
			this.salesOffDept.setDescription("");
		} else {
			this.salesOffDept.setDescription(aSalesOfficer.getLovDescSalesOffDeptName());
		}
		this.recordStatus.setValue(aSalesOfficer.getRecordStatus());
		
		if(aSalesOfficer.isNew() || (aSalesOfficer.getRecordType() != null ? aSalesOfficer.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.salesOffIsActive.setChecked(true);
			this.salesOffIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSalesOfficer
	 */
	public void doWriteComponentsToBean(SalesOfficer aSalesOfficer) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSalesOfficer.setSalesOffCode(this.salesOffCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffFName(this.salesOffFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffMName(this.salesOffMName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffLName(this.salesOffLName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffShrtName(this.salesOffShrtName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setLovDescSalesOffDeptName(this.salesOffDept.getDescription());
			aSalesOfficer.setSalesOffDept(this.salesOffDept.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer
					.setSalesOffIsActive(this.salesOffIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aSalesOfficer.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSalesOfficer
	 * @throws InterruptedException
	 */
	public void doShowDialog(SalesOfficer aSalesOfficer)
			throws InterruptedException {
		logger.debug("Entering");

		// if aSalesOfficer == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSalesOfficer == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aSalesOfficer = getSalesOfficerService().getNewSalesOfficer();

			setSalesOfficer(aSalesOfficer);
		} else {
			setSalesOfficer(aSalesOfficer);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSalesOfficer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.salesOffCode.focus();
		} else {
			this.salesOffFName.focus();
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aSalesOfficer.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aSalesOfficer);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SalesOfficerDialog);
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
		this.oldVar_salesOffCode = this.salesOffCode.getValue();
		this.oldVar_salesOffFName = this.salesOffFName.getValue();
		this.oldVar_salesOffMName = this.salesOffMName.getValue();
		this.oldVar_salesOffLName = this.salesOffLName.getValue();
		this.oldVar_salesOffShrtName = this.salesOffShrtName.getValue();
		this.oldVar_salesOffDept = this.salesOffDept.getValue();
		this.oldVar_lovDescSalesOffDeptName = this.salesOffDept.getDescription();
		this.oldVar_salesOffIsActive = this.salesOffIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialized values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.salesOffCode.setValue(this.oldVar_salesOffCode);
		this.salesOffFName.setValue(this.oldVar_salesOffFName);
		this.salesOffMName.setValue(this.oldVar_salesOffMName);
		this.salesOffLName.setValue(this.oldVar_salesOffLName);
		this.salesOffShrtName.setValue(this.oldVar_salesOffShrtName);
		this.salesOffDept.setValue(this.oldVar_salesOffDept);
		this.salesOffDept.setDescription(this.oldVar_lovDescSalesOffDeptName);
		this.salesOffIsActive.setChecked(this.oldVar_salesOffIsActive);
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
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_salesOffFName != this.salesOffFName.getValue()) {
			return true;
		}
		if (this.oldVar_salesOffMName != this.salesOffMName.getValue()) {
			return true;
		}
		if (this.oldVar_salesOffLName != this.salesOffLName.getValue()) {
			return true;
		}
		if (this.oldVar_salesOffShrtName != this.salesOffShrtName.getValue()) {
			return true;
		}
		if (this.oldVar_salesOffDept != this.salesOffDept.getValue()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.salesOffCode.isReadonly()){
			this.salesOffCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		
		if (!this.salesOffFName.isReadonly()){
			this.salesOffFName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffFName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		
		if (!this.salesOffMName.isReadonly()){
			this.salesOffMName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffMName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}

		if (!this.salesOffLName.isReadonly()){
			this.salesOffLName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffLName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}

		if (!this.salesOffShrtName.isReadonly()){
			this.salesOffShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffShrtName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.salesOffCode.setConstraint("");
		this.salesOffFName.setConstraint("");
		this.salesOffMName.setConstraint("");
		this.salesOffLName.setConstraint("");
		this.salesOffShrtName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.salesOffCode.setErrorMessage("");
		this.salesOffFName.setErrorMessage("");
		this.salesOffMName.setErrorMessage("");
		this.salesOffLName.setErrorMessage("");
		this.salesOffShrtName.setErrorMessage("");
		this.salesOffDept.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a SalesOfficer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SalesOfficer aSalesOfficer = new SalesOfficer();
		BeanUtils.copyProperties(getSalesOfficer(), aSalesOfficer);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aSalesOfficer.getSalesOffCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSalesOfficer.getRecordType()).equals(
					"")) {
				aSalesOfficer.setVersion(aSalesOfficer.getVersion() + 1);
				aSalesOfficer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSalesOfficer.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSalesOfficer, tranType)) {
					refreshList();
					closeDialog(this.window_SalesOfficerDialog, "SalesOfficer");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new SalesOfficer object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final SalesOfficer aSalesOfficer = getSalesOfficerService()
				.getNewSalesOfficer();
		aSalesOfficer.setNewRecord(true);
		setSalesOfficer(aSalesOfficer);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.salesOffCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Enterring");

		if (getSalesOfficer().isNewRecord()) {
			this.salesOffCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.salesOffCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.salesOffFName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffFName"));
		this.salesOffMName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffMName"));
		this.salesOffLName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffLName"));
		this.salesOffShrtName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffShrtName"));
		this.salesOffDept.setReadonly(isReadOnly("SalesOfficerDialog_salesOffDept"));
		this.salesOffIsActive
				.setDisabled(isReadOnly("SalesOfficerDialog_salesOffIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.salesOfficer.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Enterring");
		this.salesOffCode.setReadonly(true);
		this.salesOffFName.setReadonly(true);
		this.salesOffMName.setReadonly(true);
		this.salesOffLName.setReadonly(true);
		this.salesOffShrtName.setReadonly(true);
		this.salesOffDept.setReadonly(true);
		this.salesOffIsActive.setDisabled(true);

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
		logger.debug("Enterring");
		// remove validation, if there are a save before

		this.salesOffCode.setValue("");
		this.salesOffFName.setValue("");
		this.salesOffMName.setValue("");
		this.salesOffLName.setValue("");
		this.salesOffShrtName.setValue("");
		this.salesOffDept.setValue("");
		this.salesOffDept.setDescription("");
		this.salesOffIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");
		final SalesOfficer aSalesOfficer = new SalesOfficer();
		BeanUtils.copyProperties(getSalesOfficer(), aSalesOfficer);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SalesOfficer object with the components data
		doWriteComponentsToBean(aSalesOfficer);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSalesOfficer.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSalesOfficer.getRecordType()).equals("")) {
				aSalesOfficer.setVersion(aSalesOfficer.getVersion() + 1);
				if (isNew) {
					aSalesOfficer
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSalesOfficer
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSalesOfficer.setNewRecord(true);
				}
			}
		} else {
			aSalesOfficer.setVersion(aSalesOfficer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSalesOfficer, tranType)) {
				doWriteBeanToComponents(aSalesOfficer);
				refreshList();
				closeDialog(this.window_SalesOfficerDialog, "SalesOfficer");
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
	 * @param aSalesOfficer
	 *            (SalesOfficer)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SalesOfficer aSalesOfficer, String tranType) {
		logger.debug("Enterring");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSalesOfficer.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aSalesOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSalesOfficer.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSalesOfficer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSalesOfficer
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,
							aSalesOfficer);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aSalesOfficer))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
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

			aSalesOfficer.setTaskId(taskId);
			aSalesOfficer.setNextTaskId(nextTaskId);
			aSalesOfficer.setRoleCode(getRole());
			aSalesOfficer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSalesOfficer, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,
					aSalesOfficer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSalesOfficer,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSalesOfficer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		SalesOfficer aSalesOfficer = (SalesOfficer) auditHeader
				.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getSalesOfficerService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSalesOfficerService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSalesOfficerService().doApprove(
								auditHeader);

						if (aSalesOfficer.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSalesOfficerService().doReject(
								auditHeader);
						if (aSalesOfficer.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_SalesOfficerDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_SalesOfficerDialog, auditHeader);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
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
	private AuditHeader getAuditHeader(SalesOfficer aSalesOfficer,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aSalesOfficer.getBefImage(), aSalesOfficer);
		return new AuditHeader(aSalesOfficer.getSalesOffCode(), null, null,
				null, auditDetail, aSalesOfficer.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SalesOfficerDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
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

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		final JdbcSearchObject<SalesOfficer> soSalesOfficer = getSalesOfficerListCtrl()
				.getSearchObj();
		getSalesOfficerListCtrl().pagingSalesOfficerList.setActivePage(0);
		getSalesOfficerListCtrl().getPagedListWrapper().setSearchObject(
				soSalesOfficer);
		if (getSalesOfficerListCtrl().listBoxSalesOfficer != null) {
			getSalesOfficerListCtrl().listBoxSalesOfficer.getListModel();
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("SalesOfficer");
		notes.setReference(getSalesOfficer().getSalesOffCode());
		notes.setVersion(getSalesOfficer().getVersion());
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

	public SalesOfficer getSalesOfficer() {
		return this.salesOfficer;
	}

	public void setSalesOfficer(SalesOfficer salesOfficer) {
		this.salesOfficer = salesOfficer;
	}

	public void setSalesOfficerService(SalesOfficerService salesOfficerService) {
		this.salesOfficerService = salesOfficerService;
	}

	public SalesOfficerService getSalesOfficerService() {
		return this.salesOfficerService;
	}

	public void setSalesOfficerListCtrl(
			SalesOfficerListCtrl salesOfficerListCtrl) {
		this.salesOfficerListCtrl = salesOfficerListCtrl;
	}

	public SalesOfficerListCtrl getSalesOfficerListCtrl() {
		return this.salesOfficerListCtrl;
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

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public SalesOfficer getPrvSalesOfficer() {
		return prvSalesOfficer;
	}

}
