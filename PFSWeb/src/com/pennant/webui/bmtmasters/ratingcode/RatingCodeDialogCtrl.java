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
 * FileName    		:  RatingCodeDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.bmtmasters.ratingcode;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/RatingCode/ratingCodeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class RatingCodeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6289141323349585417L;
	private final static Logger logger = Logger.getLogger(RatingCodeDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RatingCodeDialog; 	// autoWired

	protected Textbox 		ratingType; 				// autoWired
	protected Textbox 		ratingCode; 				// autoWired
	protected Textbox 		ratingCodeDesc; 			// autoWired
	protected Checkbox 		ratingIsActive; 			// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not autoWired Var's
	private RatingCode 		mratingCode; 				// over handed per parameter
	private transient 		RatingCodeListCtrl ratingCodeListCtrl; // over handed per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_ratingType;
	private transient String  		oldVar_ratingCode;
	private transient String  		oldVar_ratingCodeDesc;
	private transient boolean  		oldVar_ratingIsActive;
	private transient String 		oldVar_recordStatus;
	private transient String 		oldVar_lovDescRatingTypeName;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_RatingCodeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 				// autoWired
	protected Button btnEdit; 				// autoWired
	protected Button btnDelete; 			// autoWired
	protected Button btnSave; 				// autoWired
	protected Button btnCancel; 			// autoWired
	protected Button btnClose; 				// autoWired
	protected Button btnHelp; 				// autoWired
	protected Button btnNotes; 				// autoWired
	
	protected Button btnSearchRatingType; 	// autoWired
	protected Textbox lovDescRatingTypeName;
	
	
	// ServiceDAOs / Domain Classes
	private transient RatingCodeService ratingCodeService;
	private transient PagedListService  pagedListService;
	

	/**
	 * default constructor.<br>
	 */
	public RatingCodeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RatingCodeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,  this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("ratingCode")) {
			this.mratingCode = (RatingCode) args.get("ratingCode");
			RatingCode befImage = new RatingCode();
			BeanUtils.copyProperties(this.mratingCode, befImage);
			this.mratingCode.setBefImage(befImage);

			setRatingCode(this.mratingCode);
		} else {
			setRatingCode(null);
		}

		doLoadWorkFlow(this.mratingCode.isWorkflow(), this.mratingCode.getWorkflowId(), this.mratingCode.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "RatingCodeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the ratingCodeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete ratingCode here.
		if (args.containsKey("ratingCodeListCtrl")) {
			setRatingCodeListCtrl((RatingCodeListCtrl) args.get("ratingCodeListCtrl"));
		} else {
			setRatingCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getRatingCode());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.ratingType.setMaxlength(8);
		this.ratingCode.setMaxlength(8);
		this.ratingCodeDesc.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("RatingCodeDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnSave"));
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
	public void onClose$window_RatingCodeDialog(Event event) throws Exception {
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
		// remember the old Var's
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
		PTMessageUtils.showHelpWindow(event, window_RatingCodeDialog);
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
			logger.debug("doClose isDataChanged(): true");

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
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
			logger.debug("doClose isDataChanged(): false");
		}
		if (close) {
			closeDialog(this.window_RatingCodeDialog, "RatingCode");
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
	 * @param aRatingCode
	 *            RatingCode
	 */
	public void doWriteBeanToComponents(RatingCode aRatingCode) {
		logger.debug("Entering");

		this.ratingType.setValue(aRatingCode.getRatingType());
		this.ratingCode.setValue(aRatingCode.getRatingCode());
		this.ratingCodeDesc.setValue(aRatingCode.getRatingCodeDesc());
		this.ratingIsActive.setChecked(aRatingCode.isRatingIsActive());

		if (aRatingCode.isNewRecord()) {
			this.lovDescRatingTypeName.setValue("");
		} else {
			this.lovDescRatingTypeName.setValue(aRatingCode.getRatingType()	+ "-" + aRatingCode.getLovDescRatingTypeName());
		}
		this.recordStatus.setValue(aRatingCode.getRecordStatus());
		
		if(aRatingCode.isNew() || aRatingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.ratingIsActive.setChecked(true);
			this.ratingIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRatingCode
	 */
	public void doWriteComponentsToBean(RatingCode aRatingCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aRatingCode.setLovDescRatingTypeName(this.lovDescRatingTypeName.getValue());
			aRatingCode.setRatingType(this.ratingType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingCode.setRatingCode(this.ratingCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingCode.setRatingCodeDesc(this.ratingCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingCode.setRatingIsActive(this.ratingIsActive.isChecked());
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

		aRatingCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRatingCode
	 * @throws InterruptedException
	 */
	public void doShowDialog(RatingCode aRatingCode) throws InterruptedException {
		logger.debug("Entering");

		// if aRatingCode == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aRatingCode == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aRatingCode = getRatingCodeService().getNewRatingCode();

			setRatingCode(aRatingCode);
		} else {
			setRatingCode(aRatingCode);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aRatingCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.lovDescRatingTypeName.focus();
		} else {
			this.ratingCodeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			this.ratingCode.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aRatingCode);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_RatingCodeDialog);
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
	 * Stores the initialize values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_ratingType = this.ratingType.getValue();
		this.oldVar_lovDescRatingTypeName = this.lovDescRatingTypeName.getValue();
		this.oldVar_ratingCode = this.ratingCode.getValue();
		this.oldVar_ratingCodeDesc = this.ratingCodeDesc.getValue();
		this.oldVar_ratingIsActive = this.ratingIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.ratingType.setValue(this.oldVar_ratingType);
		this.lovDescRatingTypeName.setValue(this.oldVar_lovDescRatingTypeName);
		this.ratingCode.setValue(this.oldVar_ratingCode);
		this.ratingCodeDesc.setValue(this.oldVar_ratingCodeDesc);
		this.ratingIsActive.setChecked(this.oldVar_ratingIsActive);
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

		if (this.oldVar_ratingType != this.ratingType.getValue()) {
			return true;
		}
		if (this.oldVar_ratingCode != this.ratingCode.getValue()) {
			return true;
		}
		if (this.oldVar_ratingCodeDesc != this.ratingCodeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_ratingIsActive != this.ratingIsActive.isChecked()) {
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

		if (!this.ratingCode.isReadonly()){
			this.ratingCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingCodeDialog_RatingCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}		
		
		if (!this.ratingCodeDesc.isReadonly()){
			this.ratingCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingCodeDialog_RatingCodeDesc.value"), 
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
		this.ratingCode.setConstraint("");
		this.ratingCodeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescRatingTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingCodeDialog_RatingType.value"),null,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescRatingTypeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.ratingCode.setErrorMessage("");
		this.ratingCodeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a RatingCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final RatingCode aRatingCode = new RatingCode();
		BeanUtils.copyProperties(getRatingCode(), aRatingCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aRatingCode.getRatingType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aRatingCode.getRecordType()).equals("")) {
				aRatingCode.setVersion(aRatingCode.getVersion() + 1);
				aRatingCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRatingCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aRatingCode, tranType)) {
					refreshList();
					closeDialog(this.window_RatingCodeDialog, "RatingCode");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");

	}

	/**
	 * Create a new RatingCode object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new RatingCode() in the front end.
		// we get it from the back end.
		final RatingCode aRatingCode = getRatingCodeService().getNewRatingCode();
		aRatingCode.setNewRecord(true);
		setRatingCode(aRatingCode);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.ratingType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getRatingCode().isNewRecord()) {
			this.ratingType.setDisabled(false);
			this.btnSearchRatingType.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.ratingType.setDisabled(true);
			this.btnSearchRatingType.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.ratingCode.setReadonly(isReadOnly("RatingCodeDialog_ratingCode"));
		this.ratingCodeDesc.setReadonly(isReadOnly("RatingCodeDialog_ratingCodeDesc"));
		this.ratingIsActive.setDisabled(isReadOnly("RatingCodeDialog_ratingIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.mratingCode.isNewRecord()) {
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

		this.btnSearchRatingType.setDisabled(true);
		this.ratingCode.setReadonly(true);
		this.ratingCodeDesc.setReadonly(true);
		this.ratingIsActive.setDisabled(true);

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
		this.ratingType.setValue("");
		this.lovDescRatingTypeName.setValue("");
		this.ratingCode.setValue("");
		this.ratingCodeDesc.setValue("");
		this.ratingIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final RatingCode aRatingCode = new RatingCode();
		BeanUtils.copyProperties(getRatingCode(), aRatingCode);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the RatingCode object with the components data
		doWriteComponentsToBean(aRatingCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aRatingCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aRatingCode.getRecordType()).equals("")) {
				aRatingCode.setVersion(aRatingCode.getVersion() + 1);
				if (isNew) {
					aRatingCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRatingCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRatingCode.setNewRecord(true);
				}
			}
		} else {
			aRatingCode.setVersion(aRatingCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aRatingCode, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_RatingCodeDialog, "RatingCode");
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
	 * @param aRatingCode
	 *            (RatingCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RatingCode aRatingCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRatingCode.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aRatingCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRatingCode.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aRatingCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRatingCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aRatingCode);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aRatingCode))) {
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
			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
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

			aRatingCode.setTaskId(taskId);
			aRatingCode.setNextTaskId(nextTaskId);
			aRatingCode.setRoleCode(getRole());
			aRatingCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRatingCode, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aRatingCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRatingCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aRatingCode, tranType);
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
		RatingCode aRatingCode = (RatingCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getRatingCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRatingCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRatingCodeService().doApprove(auditHeader);

						if (aRatingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRatingCodeService().doReject(auditHeader);

						if (aRatingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RatingCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RatingCodeDialog, auditHeader);
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
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchRatingType(Event event) {
		logger.debug("Entering" + event.toString());

		this.ratingType.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_RatingCodeDialog, "RatingType");
		if (dataObject instanceof String) {
			this.ratingType.setValue(dataObject.toString());
			this.lovDescRatingTypeName.setValue("");
		} else {
			RatingType details = (RatingType) dataObject;
			if (details != null) {
				this.ratingType.setValue(details.getLovValue());
				this.lovDescRatingTypeName.setValue(details.getLovValue() + "-" + details.getRatingTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ WorkFlow Details +++++++++++++++++++//
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
	private AuditHeader getAuditHeader(RatingCode aRatingCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aRatingCode.getBefImage(), aRatingCode);
		return new AuditHeader(String.valueOf(aRatingCode.getId()), null, null,
				null, auditDetail, aRatingCode.getUserDetails(), getOverideMap());
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
			System.out.println(e);
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RatingCodeDialog,  auditHeader);
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
		final JdbcSearchObject<RatingCode> soRatingCode = getRatingCodeListCtrl().getSearchObj();
		getRatingCodeListCtrl().pagingRatingCodeList.setActivePage(0);
		getRatingCodeListCtrl().getPagedListWrapper().setSearchObject(soRatingCode);
		if (getRatingCodeListCtrl().listBoxRatingCode != null) {
			getRatingCodeListCtrl().listBoxRatingCode.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("RatingCode");
		notes.setReference(getRatingCode().getRatingType() + PennantConstants.KEY_SEPERATOR + getRatingCode().getRatingCode());
		notes.setVersion(getRatingCode().getVersion());
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

	public RatingCode getRatingCode() {
		return this.mratingCode;
	}
	public void setRatingCode(RatingCode ratingCode) {
		this.mratingCode = ratingCode;
	}

	public void setRatingCodeService(RatingCodeService ratingCodeService) {
		this.ratingCodeService = ratingCodeService;
	}
	public RatingCodeService getRatingCodeService() {
		return this.ratingCodeService;
	}

	public void setRatingCodeListCtrl(RatingCodeListCtrl ratingCodeListCtrl) {
		this.ratingCodeListCtrl = ratingCodeListCtrl;
	}
	public RatingCodeListCtrl getRatingCodeListCtrl() {
		return this.ratingCodeListCtrl;
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
