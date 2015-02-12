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
 * FileName    		:  SubSectorDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.subsector;

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
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.SubSectorService;
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
 * /WEB-INF/pages/SystemMasters/SubSector/subSectorDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SubSectorDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6126940774535492694L;
	private final static Logger logger = Logger.getLogger(SubSectorDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_SubSectorDialog; 	// autoWired
	protected ExtendedCombobox 	sectorCode; 				// autoWired
	protected Textbox 	subSectorCode; 				// autoWired
	protected Textbox 	subSectorDesc; 				// autoWired
	protected Checkbox 	subSectorIsActive; 			// autoWired

	protected Label 	recordStatus; 				// autoWired
	protected Radiogroup userAction;
	protected Groupbox 	groupboxWf;

	// not auto wired variables
	private SubSector subSector; 							// overHanded per parameter
	private transient SubSectorListCtrl subSectorListCtrl;  // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_sectorCode;
	private transient String  		oldVar_subSectorCode;
	private transient String  		oldVar_subSectorDesc;
	private transient boolean  		oldVar_subSectorIsActive;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SubSectorDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;  				// autoWire
	protected Button btnEdit; 				// autoWire
	protected Button btnDelete; 			// autoWire
	protected Button btnSave; 				// autoWire
	protected Button btnCancel; 			// autoWire
	protected Button btnClose;  			// autoWire
	protected Button btnHelp; 				// autoWire
	protected Button btnNotes;  			// autoWire

	private transient String oldVar_lovDescSectorCodeName;

	// ServiceDAOs / Domain Classes
	private transient SubSectorService subSectorService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SubSectorDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSector object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SubSectorDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("subSector")) {
			this.subSector = (SubSector) args.get("subSector");
			SubSector befImage = new SubSector();
			BeanUtils.copyProperties(this.subSector, befImage);
			this.subSector.setBefImage(befImage);

			setSubSector(this.subSector);
		} else {
			setSubSector(null);
		}

		doLoadWorkFlow(this.subSector.isWorkflow(), this.subSector.getWorkflowId(), this.subSector.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SubSectorDialog");
		}

		// READ OVERHANDED parameters !
		// we get the subSectorListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete subSector here.
		if (args.containsKey("subSectorListCtrl")) {
			setSubSectorListCtrl((SubSectorListCtrl) args.get("subSectorListCtrl"));
		} else {
			setSubSectorListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSubSector());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.sectorCode.setMaxlength(8);
		this.subSectorCode.setMaxlength(8);
		this.subSectorDesc.setMaxlength(50);

		this.sectorCode.setModuleName("Sector");
		this.sectorCode.setValueColumn("SectorCode");
		this.sectorCode.setDescColumn("SectorDesc");
		this.sectorCode.setValidateColumns(new String[]{"SectorCode"});
		
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
		getUserWorkspace().alocateAuthorities("SubSectorDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnSave"));
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
	public void onClose$window_SubSectorDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_SubSectorDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 *            (Event)
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
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_SubSectorDialog, "SubSector");
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
	 * @param aSubSector
	 *            (SubSector)
	 * 
	 */
	public void doWriteBeanToComponents(SubSector aSubSector) {
		logger.debug("Entering");
		this.sectorCode.setValue(aSubSector.getSectorCode());
		this.subSectorCode.setValue(aSubSector.getSubSectorCode());
		this.subSectorDesc.setValue(aSubSector.getSubSectorDesc());
		this.subSectorIsActive.setChecked(aSubSector.isSubSectorIsActive());
		this.sectorCode.setMandatoryStyle(true);

		if (aSubSector.isNewRecord()) {
			this.sectorCode.setDescription("");
		} else {
			this.sectorCode.setDescription(aSubSector.getLovDescSectorCodeName());
		}
		this.recordStatus.setValue(aSubSector.getRecordStatus());
		
		if(aSubSector.isNew() || (aSubSector.getRecordType() != null ? aSubSector.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.subSectorIsActive.setChecked(true);
			this.subSectorIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSubSector
	 *            (SubSector)
	 */
	public void doWriteComponentsToBean(SubSector aSubSector) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSubSector.setLovDescSectorCodeName(this.sectorCode.getDescription());
			aSubSector.setSectorCode(this.sectorCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSector.setSubSectorCode(this.subSectorCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSector.setSubSectorDesc(this.subSectorDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSector.setSubSectorIsActive(this.subSectorIsActive.isChecked());
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

		aSubSector.setRecordStatus(this.recordStatus.getValue());
		setSubSector(aSubSector);
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSubSector
	 * @throws InterruptedException
	 */
	public void doShowDialog(SubSector aSubSector) throws InterruptedException {
		logger.debug("Entering");
		// if aSubSector == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSubSector == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aSubSector = getSubSectorService().getNewSubSector();
			setSubSector(aSubSector);
		} else {
			setSubSector(aSubSector);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSubSector.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sectorCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.subSectorDesc.focus();
				if (!StringUtils.trimToEmpty(aSubSector.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			this.subSectorCode.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSubSector);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SubSectorDialog);
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
		this.oldVar_sectorCode = this.sectorCode.getValue();
		this.oldVar_lovDescSectorCodeName = this.sectorCode.getDescription();
		this.oldVar_subSectorCode = this.subSectorCode.getValue();
		this.oldVar_subSectorDesc = this.subSectorDesc.getValue();
		this.oldVar_subSectorIsActive = this.subSectorIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.sectorCode.setValue(this.oldVar_sectorCode);
		this.sectorCode.setDescription(this.oldVar_lovDescSectorCodeName);
		this.subSectorCode.setValue(this.oldVar_subSectorCode);
		this.subSectorDesc.setValue(this.oldVar_subSectorDesc);
		this.subSectorIsActive.setChecked(this.oldVar_subSectorIsActive);
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

		if (this.oldVar_sectorCode != this.sectorCode.getValue()) {
			return true;
		}
		if (this.oldVar_subSectorCode != this.subSectorCode.getValue()) {
			return true;
		}
		if (this.oldVar_subSectorDesc != this.subSectorDesc.getValue()) {
			return true;
		}
		if (this.oldVar_subSectorIsActive != this.subSectorIsActive.isChecked()) {
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

		if (!this.sectorCode.isReadonly()) {
			this.sectorCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSectorDialog_SectorCode.value"), null, true,true));
		}
		
		if (!this.subSectorCode.isReadonly()){
			this.subSectorCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSectorDialog_SubSectorCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.subSectorDesc.isReadonly()){
			this.subSectorDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSectorDialog_SubSectorDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.subSectorCode.setConstraint("");
		this.subSectorDesc.setConstraint("");
		this.sectorCode.setConstraint("");
		logger.debug("Leaving");
	}

	

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.subSectorCode.setErrorMessage("");
		this.subSectorDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a SubSector object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SubSector aSubSector = new SubSector();
		BeanUtils.copyProperties(getSubSector(), aSubSector);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aSubSector.getSectorCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSubSector.getRecordType()).equals("")) {
				aSubSector.setVersion(aSubSector.getVersion() + 1);
				aSubSector.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSubSector.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSubSector, tranType)) {
					refreshList();
					closeDialog(this.window_SubSectorDialog, "SubSector");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new SubSector object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new SubSector() in the frontEnd.
		// we get it from the backEend.
		final SubSector aSubSector = getSubSectorService().getNewSubSector();
		aSubSector.setNewRecord(true);
		setSubSector(aSubSector);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.sectorCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		logger.debug("Entering");
		if (getSubSector().isNewRecord()) {
			this.sectorCode.setReadonly(true);
			this.subSectorCode.setReadonly(false);
			this.sectorCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.sectorCode.setReadonly(true);
			this.subSectorCode.setReadonly(true);
			this.sectorCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		//this.subSectorCode.setReadonly(isReadOnly("SubSectorDialog_subSectorCode"));
		this.subSectorDesc.setReadonly(isReadOnly("SubSectorDialog_subSectorDesc"));
		this.subSectorIsActive.setDisabled(isReadOnly("SubSectorDialog_subSectorIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.subSector.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.sectorCode.setReadonly(true);
		this.subSectorCode.setReadonly(true);
		this.subSectorDesc.setReadonly(true);
		this.subSectorIsActive.setDisabled(true);

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
		this.sectorCode.setValue("");
		this.sectorCode.setDescription("");
		this.subSectorCode.setValue("");
		this.subSectorDesc.setValue("");
		this.subSectorIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SubSector aSubSector = new SubSector();
		BeanUtils.copyProperties(getSubSector(), aSubSector);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SubSector object with the components data
		doWriteComponentsToBean(aSubSector);

		// Write the additional validations as per below example
		// get the selected branch object from the listBbox
		// Do data level validations here

		isNew = aSubSector.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSubSector.getRecordType()).equals("")) {
				aSubSector.setVersion(aSubSector.getVersion() + 1);
				if (isNew) {
					aSubSector.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSubSector.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSubSector.setNewRecord(true);
				}
			}
		} else {
			aSubSector.setVersion(aSubSector.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSubSector, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_SubSectorDialog, "SubSector");
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
	 * @param aSubSector
	 *            (SubSector)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SubSector aSubSector, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSubSector.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSubSector.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSubSector.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSubSector.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSubSector.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSubSector);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aSubSector))) {
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

			aSubSector.setTaskId(taskId);
			aSubSector.setNextTaskId(nextTaskId);
			aSubSector.setRoleCode(getRole());
			aSubSector.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSubSector, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aSubSector);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSubSector, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSubSector, tranType);
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
		SubSector aSubSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSubSectorService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSubSectorService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSubSectorService().doApprove(auditHeader);

						if (aSubSector.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSubSectorService().doReject(auditHeader);

						if (aSubSector.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SubSectorDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SubSectorDialog, auditHeader);
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
	// +++++++++++++ WorkFlow Details ++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 * 
	 * @param aSubSegment
	 *            (SubSegment)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SubSector aSubSector, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSubSector.getBefImage(), aSubSector);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aSubSector.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_SubSectorDialog, auditHeader);
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
		final JdbcSearchObject<SubSector> soSubSector = getSubSectorListCtrl().getSearchObj();
		getSubSectorListCtrl().pagingSubSectorList.setActivePage(0);
		getSubSectorListCtrl().getPagedListWrapper().setSearchObject(soSubSector);
		if (getSubSectorListCtrl().listBoxSubSector != null) {
			getSubSectorListCtrl().listBoxSubSector.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("SubSector");
		notes.setReference(getReference());
		notes.setVersion(getSubSector().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getSubSector().getSubSectorCode()+PennantConstants.KEY_SEPERATOR +
					getSubSector().getSectorCode();
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

	public SubSector getSubSector() {
		return this.subSector;
	}
	public void setSubSector(SubSector subSector) {
		this.subSector = subSector;
	}

	public void setSubSectorService(SubSectorService subSectorService) {
		this.subSectorService = subSectorService;
	}
	public SubSectorService getSubSectorService() {
		return this.subSectorService;
	}

	public void setSubSectorListCtrl(SubSectorListCtrl subSectorListCtrl) {
		this.subSectorListCtrl = subSectorListCtrl;
	}
	public SubSectorListCtrl getSubSectorListCtrl() {
		return this.subSectorListCtrl;
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
