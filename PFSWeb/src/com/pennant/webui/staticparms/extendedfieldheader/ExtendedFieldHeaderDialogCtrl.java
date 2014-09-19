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
 * FileName    		:  ExtendedFieldHeaderDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.extendedfieldheader;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.staticparms.ExtendedFieldHeaderService;
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
 * /WEB-INF/pages/StaticParms/ExtendedFieldHeader/extendedFieldHeaderDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ExtendedFieldHeaderDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4892656164017054696L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldHeaderDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window 		window_ExtendedFieldHeaderDialog;	// autowired

	protected Combobox 		moduleName; 						// autowired
	protected Combobox 		subModuleName; 						// autowired
	protected Textbox 		tabHeading; 						// autowired
	protected Radiogroup 	numberOfColumns; 					// autowired
	protected Radio 		radio_column1;
	protected Radio 		radio_column2;

	protected Label 		recordStatus; 						// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired vars
	private ExtendedFieldHeader extendedFieldHeader; // overhanded per param
	private transient ExtendedFieldHeaderListCtrl extendedFieldHeaderListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_moduleName;
	private transient String oldVar_subModuleName;
	private transient String oldVar_tabHeading;
	private transient String oldVar_numberOfColumns;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ExtendedFieldHeaderDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp;	// autowire
	protected Button btnNotes; 	// autowire

	// ServiceDAOs / Domain Classes
	private transient ExtendedFieldHeaderService extendedFieldHeaderService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldHeaderDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ExtendedFieldHeader
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldHeaderDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED params !
			if (args.containsKey("extendedFieldHeader")) {
				this.extendedFieldHeader = (ExtendedFieldHeader) args.get("extendedFieldHeader");
				ExtendedFieldHeader befImage = new ExtendedFieldHeader();
				BeanUtils.copyProperties(this.extendedFieldHeader, befImage);
				this.extendedFieldHeader.setBefImage(befImage);

				setExtendedFieldHeader(this.extendedFieldHeader);
			} else {
				setExtendedFieldHeader(null);
			}

			doLoadWorkFlow(this.extendedFieldHeader.isWorkflow(), this.extendedFieldHeader.getWorkflowId(),
					this.extendedFieldHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "ExtendedFieldHeaderDialog");
			}

			// READ OVERHANDED params !
			// we get the extendedFieldHeaderListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete extendedFieldHeader here.
			if (args.containsKey("extendedFieldHeaderListCtrl")) {
				setExtendedFieldHeaderListCtrl((ExtendedFieldHeaderListCtrl) args.get("extendedFieldHeaderListCtrl"));
			} else {
				setExtendedFieldHeaderListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExtendedFieldHeader());
		} catch (Exception e) {
			logger.error(e.getMessage());
			PTMessageUtils.showErrorMessage(e.getMessage());
			this.window_ExtendedFieldHeaderDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.moduleName.setMaxlength(50);
		this.subModuleName.setMaxlength(50);
		this.tabHeading.setMaxlength(20);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("ExtendedFieldHeaderDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnSave"));
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
	public void onClose$window_ExtendedFieldHeaderDialog(Event event) throws Exception {
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
		// remember the old vars
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldHeaderDialog);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++   GUI Process   +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
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
			closeDialog(this.window_ExtendedFieldHeaderDialog, "ExtendedFieldHeader");
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
	 * @param aExtendedFieldHeader
	 *            ExtendedFieldHeader
	 */
	public void doWriteBeanToComponents(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");

		fillCombobox(this.moduleName, aExtendedFieldHeader.getModuleName());
		fillsubModule(this.subModuleName, aExtendedFieldHeader.getModuleName(),
				aExtendedFieldHeader.getSubModuleName());
		this.numberOfColumns.setSelectedIndex(0);
		
		// fillCombobox(this.subModuleName,PennantAppUtil.getSubModuleName(aExtendedFieldHeader.getModuleName()),aExtendedFieldHeader.getSubModuleName());
		this.tabHeading.setValue(aExtendedFieldHeader.getTabHeading());

		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getItemAtIndex(i).getValue().equals(aExtendedFieldHeader.getNumberOfColumns()==null?"":aExtendedFieldHeader.getNumberOfColumns().trim())) {
				this.numberOfColumns.setSelectedIndex(i);
			}
		}

		this.recordStatus.setValue(aExtendedFieldHeader.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aExtendedFieldHeader
	 */
	public void doWriteComponentsToBean(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if(!this.moduleName.isDisabled() && this.moduleName.getSelectedIndex()<1){
				throw new WrongValueException(moduleName, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_ExtendedFieldHeaderDialog_ModuleName.value")}));
			}
			aExtendedFieldHeader.setModuleName(this.moduleName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(!this.subModuleName.isDisabled() && this.subModuleName.getSelectedIndex()<1){
				throw new WrongValueException(subModuleName, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_ExtendedFieldHeaderDialog_SubModuleName.value")}));
			}
			aExtendedFieldHeader.setSubModuleName(this.subModuleName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aExtendedFieldHeader.setSubModuleName(this.subModuleName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldHeader.setTabHeading(this.tabHeading.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldHeader.setNumberOfColumns(this.numberOfColumns.getSelectedItem().getValue().toString());
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

		aExtendedFieldHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aExtendedFieldHeader
	 * @throws InterruptedException
	 */
	public void doShowDialog(ExtendedFieldHeader aExtendedFieldHeader) throws InterruptedException {
		logger.debug("Entering");

		// if aExtendedFieldHeader == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aExtendedFieldHeader == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aExtendedFieldHeader = getExtendedFieldHeaderService().getNewExtendedFieldHeader();

			setExtendedFieldHeader(aExtendedFieldHeader);
		} else {
			setExtendedFieldHeader(aExtendedFieldHeader);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aExtendedFieldHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.moduleName.focus();
		} else {
			this.tabHeading.focus();
			if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aExtendedFieldHeader);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ExtendedFieldHeaderDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
			this.window_ExtendedFieldHeaderDialog.onClose();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_moduleName = this.moduleName.getValue();
		this.oldVar_subModuleName = this.subModuleName.getValue();
		this.oldVar_tabHeading = this.tabHeading.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.moduleName.setValue(this.oldVar_moduleName);
		this.subModuleName.setValue(this.oldVar_subModuleName);
		this.tabHeading.setValue(this.oldVar_tabHeading);
		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getSelectedItem().getValue().equals(this.oldVar_numberOfColumns)) {
				this.numberOfColumns.setSelectedIndex(i);
				break;
			}
			this.numberOfColumns.setSelectedIndex(0);
		}

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

		// To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_moduleName != this.moduleName.getValue()) {
			return true;
		}
		if (this.oldVar_subModuleName != this.subModuleName.getValue()) {
			return true;
		}
		if (this.oldVar_tabHeading != this.tabHeading.getValue()) {
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

		if (!this.tabHeading.isReadonly()) {
			this.tabHeading.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldHeaderDialog_TabHeading.value"), PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.moduleName.setConstraint("");
		this.subModuleName.setConstraint("");
		this.tabHeading.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ExtendedFieldHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final ExtendedFieldHeader aExtendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(getExtendedFieldHeader(), aExtendedFieldHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
		+ Labels.getLabel(aExtendedFieldHeader.getModuleName());
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aExtendedFieldHeader.getRecordType()).equals("")) {
				aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
				aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aExtendedFieldHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aExtendedFieldHeader, tranType)) {
					refreshList();
					closeDialog(this.window_ExtendedFieldHeaderDialog, "ExtendedFieldHeader");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ExtendedFieldHeader object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		
		final ExtendedFieldHeader aExtendedFieldHeader = getExtendedFieldHeaderService().getNewExtendedFieldHeader();
		setExtendedFieldHeader(aExtendedFieldHeader);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.tabHeading.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getExtendedFieldHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.moduleName.setDisabled(false);
			this.subModuleName.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.moduleName.setDisabled(true);
			this.subModuleName.setDisabled(true);
		}

		this.tabHeading.setReadonly(isReadOnly("ExtendedFieldHeaderDialog_tabHeading"));
		this.radio_column1.setDisabled(isReadOnly("ExtendedFieldHeaderDialog_numberOfColumns"));
		this.radio_column2.setDisabled(isReadOnly("ExtendedFieldHeaderDialog_numberOfColumns"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.extendedFieldHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.moduleName.setDisabled(true);
		this.subModuleName.setDisabled(true);
		this.tabHeading.setReadonly(true);
		this.radio_column1.setDisabled(true);
		this.radio_column2.setDisabled(true);


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

		this.moduleName.setValue("");
		this.subModuleName.setValue("");
		this.tabHeading.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ExtendedFieldHeader aExtendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(getExtendedFieldHeader(), aExtendedFieldHeader);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the ExtendedFieldHeader object with the components data
		doWriteComponentsToBean(aExtendedFieldHeader);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aExtendedFieldHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aExtendedFieldHeader.getRecordType()).equals("")) {
				aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
				if (isNew) {
					aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExtendedFieldHeader.setNewRecord(true);
				}
			}
		} else {
			aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aExtendedFieldHeader, tranType)) {
				refreshList();
				closeDialog(this.window_ExtendedFieldHeaderDialog, "ExtendedFieldHeader");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExtendedFieldHeader.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aExtendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExtendedFieldHeader.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aExtendedFieldHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExtendedFieldHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aExtendedFieldHeader);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aExtendedFieldHeader))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
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

			aExtendedFieldHeader.setTaskId(taskId);
			aExtendedFieldHeader.setNextTaskId(nextTaskId);
			aExtendedFieldHeader.setRoleCode(getRole());
			aExtendedFieldHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aExtendedFieldHeader, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aExtendedFieldHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aExtendedFieldHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			aExtendedFieldHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			auditHeader = getAuditHeader(aExtendedFieldHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		ExtendedFieldHeader aExtendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getExtendedFieldHeaderService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getExtendedFieldHeaderService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getExtendedFieldHeaderService().doApprove(auditHeader);

						if (aExtendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getExtendedFieldHeaderService().doReject(auditHeader);
						if (aExtendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ExtendedFieldHeaderDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldHeaderDialog, auditHeader);
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
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return this.extendedFieldHeader;
	}
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public void setExtendedFieldHeaderService(ExtendedFieldHeaderService extendedFieldHeaderService) {
		this.extendedFieldHeaderService = extendedFieldHeaderService;
	}
	public ExtendedFieldHeaderService getExtendedFieldHeaderService() {
		return this.extendedFieldHeaderService;
	}

	public void setExtendedFieldHeaderListCtrl(ExtendedFieldHeaderListCtrl extendedFieldHeaderListCtrl) {
		this.extendedFieldHeaderListCtrl = extendedFieldHeaderListCtrl;
	}
	public ExtendedFieldHeaderListCtrl getExtendedFieldHeaderListCtrl() {
		return this.extendedFieldHeaderListCtrl;
	}
	
	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}
	
	private void doClearMessage() {
		logger.debug("Entering");
		this.moduleName.setErrorMessage("");
		this.subModuleName.setErrorMessage("");
		this.tabHeading.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void fillCombobox(Combobox combobox, String value) {
		ArrayList<String> arrayList = new ArrayList<String>(PennantStaticListUtil.getModuleName().keySet());
		combobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setLabel("----Select-----");
		comboitem.setValue("");
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		if (arrayList != null) {
			for (int i = 0; i < arrayList.size(); i++) {
				comboitem = new Comboitem();
				comboitem.setLabel(Labels.getLabel("label_ExtendedField_"+arrayList.get(i)));
				comboitem.setValue(arrayList.get(i));
				combobox.appendChild(comboitem);
				if (StringUtils.trimToEmpty(value).equals(arrayList.get(i))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
	}

	private void fillsubModule(Combobox combobox, String moduleName, String value) {
		if (this.moduleName.getSelectedItem() != null) {
			HashMap<String, String> hashMap = PennantStaticListUtil.getModuleName().get(moduleName) == null ? new HashMap<String, String>()
					: PennantStaticListUtil.getModuleName().get(moduleName);
			ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
			subModuleName.getItems().clear();
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("----Select-----");
			comboitem.setValue("");
			subModuleName.appendChild(comboitem);
			subModuleName.setSelectedItem(comboitem);
			if (arrayList != null) {
				for (int i = 0; i < arrayList.size(); i++) {
					comboitem = new Comboitem();
					comboitem.setLabel(Labels.getLabel("label_ExtendedField_"+arrayList.get(i)));
					comboitem.setValue(arrayList.get(i));
					subModuleName.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(arrayList.get(i))) {
						subModuleName.setSelectedItem(comboitem);
					}
				}
			}
		} else {
			subModuleName.getItems().clear();
		}
	}

	public void onChange$moduleName(Event event) {
		if (this.moduleName.getSelectedItem() != null) {
			String module = this.moduleName.getSelectedItem().getValue().toString();
			fillsubModule(this.subModuleName, module, "");
		}
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++  Audit Changes  +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private AuditHeader getAuditHeader(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aExtendedFieldHeader.getBefImage(), aExtendedFieldHeader);
		return new AuditHeader(String.valueOf(aExtendedFieldHeader.getModuleId()), null, null, null, auditDetail,
				aExtendedFieldHeader.getUserDetails(), getOverideMap());
	}
	
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ExtendedFieldHeaderDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("ExtendedFieldHeader");
		notes.setReference(String.valueOf(getExtendedFieldHeader().getModuleId()));
		notes.setVersion(getExtendedFieldHeader().getVersion());
		return notes;
	}

	private void refreshList() {
		final JdbcSearchObject<ExtendedFieldHeader> soExtendedFieldHeader = getExtendedFieldHeaderListCtrl().getSearchObj();
		getExtendedFieldHeaderListCtrl().pagingExtendedFieldHeaderList.setActivePage(0);
		getExtendedFieldHeaderListCtrl().getPagedListWrapper().setSearchObject(soExtendedFieldHeader);
		if (getExtendedFieldHeaderListCtrl().listBoxExtendedFieldHeader != null) {
			getExtendedFieldHeaderListCtrl().listBoxExtendedFieldHeader.getListModel();
		}
	}

}
