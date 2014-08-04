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
 * FileName    		:  IndustryDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.industry;

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
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.IndustryService;
import com.pennant.backend.util.JdbcSearchObject;
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
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Industry/industryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class IndustryDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2259811281710327276L;
	private final static Logger logger = Logger.getLogger(IndustryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IndustryDialog; 	// autoWired

	protected Textbox 		industryCode; 			// autoWired
	protected Textbox 		subSectorCode; 			// autoWired
	protected Textbox 		industryDesc; 			// autoWired
	protected Decimalbox 	industryLimit; 			// autoWired
	protected Checkbox 		industryIsActive; 		// autoWired

	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not autoWired Var's
	private Industry industry; 				// overHanded per parameter
	private transient IndustryListCtrl industryListCtrl; 	// overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 		oldVar_industryCode;
	private transient String 		oldVar_subSectorCode;
	private transient String 		oldVar_industryDesc;
	private transient BigDecimal 	oldVar_industryLimit;
	private transient boolean 		oldVar_industryIsActive;
	private transient String 		oldVar_recordStatus;

	protected Button btnSearchSubSector; 		// autoWired
	protected Textbox lovDescSubSectorCodeName;
	private transient String oldVar_lovDescSubSector;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_IndustryDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	// ServiceDAOs / Domain Classes
	private transient IndustryService industryService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public IndustryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Industry object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IndustryDialog(Event event) throws Exception {
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
		if (args.containsKey("industry")) {
			this.industry = (Industry) args.get("industry");
			Industry befImage = new Industry();
			BeanUtils.copyProperties(this.industry, befImage);
			this.industry.setBefImage(befImage);
			setIndustry(this.industry);
		} else {
			setIndustry(null);
		}

		doLoadWorkFlow(this.industry.isWorkflow(), this.industry.getWorkflowId(), this.industry.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "IndustryDialog");
		}

		// READ OVERHANDED parameters !
		// we get the industryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete industry here.
		if (args.containsKey("industryListCtrl")) {
			setIndustryListCtrl((IndustryListCtrl) args.get("industryListCtrl"));
		} else {
			setIndustryListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getIndustry());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.industryCode.setMaxlength(8);
		this.subSectorCode.setMaxlength(8);
		this.industryDesc.setMaxlength(50);
		this.industryLimit.setMaxlength(21);
		this.industryLimit.setFormat(PennantAppUtil.getAmountFormate(0));
		this.industryLimit.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.industryLimit.setScale(0);

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

		getUserWorkspace().alocateAuthorities("IndustryDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnSave"));

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
	public void onClose$window_IndustryDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_IndustryDialog);
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
			logger.debug("doClose isDataChanged : true");

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
			closeDialog(this.window_IndustryDialog, "Industry");
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
	 * @param aIndustry
	 *            Industry
	 */
	public void doWriteBeanToComponents(Industry aIndustry) {
		logger.debug("Entering");
		this.industryCode.setValue(aIndustry.getIndustryCode());
		this.subSectorCode.setValue(aIndustry.getSubSectorCode());
		this.industryDesc.setValue(aIndustry.getIndustryDesc());
		this.industryLimit.setValue(PennantAppUtil.formateAmount(aIndustry.getIndustryLimit(), 0));
		this.industryIsActive.setChecked(aIndustry.isIndustryIsActive());

		if (aIndustry.isNewRecord()) {
			this.lovDescSubSectorCodeName.setValue("");
		} else {
			this.lovDescSubSectorCodeName.setValue(aIndustry.getSubSectorCode()	+ "-" + aIndustry.getLovDescSubSectorCodeName());
		}
		this.recordStatus.setValue(aIndustry.getRecordStatus());
		
		if(aIndustry.isNew() || (aIndustry.getRecordType() != null ? aIndustry.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.industryIsActive.setChecked(true);
			this.industryIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIndustry
	 */
	public void doWriteComponentsToBean(Industry aIndustry) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aIndustry.setIndustryCode(this.industryCode.getValue()
					.toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setLovDescSubSectorCodeName(this.lovDescSubSectorCodeName
					.getValue());
			aIndustry.setSubSectorCode(this.subSectorCode.getValue()
					.toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setIndustryDesc(this.industryDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setIndustryLimit(PennantAppUtil.unFormateAmount(
					this.industryLimit.getValue(), 0));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setIndustryIsActive(this.industryIsActive.isChecked());
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

		aIndustry.setRecordStatus(this.recordStatus.getValue());
		setIndustry(aIndustry);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aIndustry
	 * @throws InterruptedException
	 */
	public void doShowDialog(Industry aIndustry) throws InterruptedException {
		logger.debug("Entering");

		// if aIndustry == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aIndustry == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aIndustry = getIndustryService().getNewIndustry();

			setIndustry(aIndustry);
		} else {
			setIndustry(aIndustry);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aIndustry.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.industryCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.industryDesc.focus();
				if (!StringUtils.trimToEmpty(aIndustry.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				this.btnSearchSubSector.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aIndustry);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_IndustryDialog);
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
		this.oldVar_industryCode = this.industryCode.getValue();
		this.oldVar_subSectorCode = this.subSectorCode.getValue();
		this.oldVar_lovDescSubSector = this.lovDescSubSectorCodeName.getValue();
		this.oldVar_industryDesc = this.industryDesc.getValue();
		this.oldVar_industryLimit = this.industryLimit.getValue();
		this.oldVar_industryIsActive = this.industryIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.industryCode.setValue(this.oldVar_industryCode);
		this.subSectorCode.setValue(this.oldVar_subSectorCode);
		this.lovDescSubSectorCodeName.setValue(this.oldVar_lovDescSubSector);
		this.industryDesc.setValue(this.oldVar_industryDesc);
		this.industryLimit.setValue(this.oldVar_industryLimit);
		this.industryIsActive.setChecked(this.oldVar_industryIsActive);
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
		// To remove Error Messages
		doClearMessage();

		if (this.oldVar_industryCode != this.industryCode.getValue()) {
			return true;
		}
		if (this.oldVar_subSectorCode != this.subSectorCode.getValue()) {
			return true;
		}
		if (this.oldVar_industryDesc != this.industryDesc.getValue()) {
			return true;
		}
		if (this.oldVar_industryLimit != this.industryLimit.getValue()) {
			return true;
		}
		if (this.oldVar_industryIsActive != this.industryIsActive.isChecked()) {
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

		if (!this.industryCode.isReadonly()){
			this.industryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_IndustryDialog_IndustryCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.subSectorCode.isReadonly()) {
			this.subSectorCode.setConstraint(new PTStringValidator(Labels.getLabel(
					"label_IndustryDialog_SubSectorCode.value"), null, true));
		}

		if (!this.industryDesc.isReadonly()){
			this.industryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_IndustryDialog_IndustryDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.industryLimit.isReadonly()) {
			this.industryLimit.setConstraint(new AmountValidator(21, 0, Labels.getLabel(
			"label_IndustryDialog_IndustryLimit.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.industryCode.setConstraint("");
		this.subSectorCode.setConstraint("");
		this.industryDesc.setConstraint("");
		this.industryLimit.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescSubSectorCodeName.setConstraint("NO EMPTY:"+ Labels.getLabel(
				"FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_IndustryDialog_SubSectorCode.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescSubSectorCodeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.industryCode.setErrorMessage("");
		this.subSectorCode.setErrorMessage("");
		this.industryDesc.setErrorMessage("");
		this.industryLimit.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Industry object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Industry aIndustry = new Industry();
		BeanUtils.copyProperties(getIndustry(), aIndustry);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aIndustry.getIndustryCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aIndustry.getRecordType()).equals("")) {
				aIndustry.setVersion(aIndustry.getVersion() + 1);
				aIndustry.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aIndustry.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aIndustry, tranType)) {
					refreshList();
					closeDialog(this.window_IndustryDialog, "Industry");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Industry object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Industry() in the front end.
		// we get it from the back end.
		final Industry aIndustry = getIndustryService().getNewIndustry();
		aIndustry.setIndustryLimit(new BigDecimal(0)); // initialization
		aIndustry.setNewRecord(true);
		setIndustry(aIndustry);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.industryCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getIndustry().isNewRecord()) {
			this.industryCode.setReadonly(false);
			this.subSectorCode.setReadonly(false);
			this.btnSearchSubSector.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.industryCode.setReadonly(true);
			this.subSectorCode.setReadonly(true);
			this.btnSearchSubSector.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		this.industryDesc.setReadonly(isReadOnly("IndustryDialog_industryDesc"));
		this.industryLimit.setReadonly(isReadOnly("IndustryDialog_industryLimit"));
		this.industryIsActive.setDisabled(isReadOnly("IndustryDialog_industryIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.industry.isNewRecord()) {
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

		this.industryCode.setReadonly(true);
		this.subSectorCode.setReadonly(true);
		this.btnSearchSubSector.setDisabled(true);
		this.industryDesc.setReadonly(true);
		this.industryLimit.setReadonly(true);
		this.industryIsActive.setDisabled(true);

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
		this.industryCode.setValue("");
		this.subSectorCode.setValue("");
		this.lovDescSubSectorCodeName.setValue("");
		this.industryDesc.setValue("");
		this.industryLimit.setValue("");
		this.industryIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Industry aIndustry = new Industry();
		BeanUtils.copyProperties(getIndustry(), aIndustry);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Industry object with the components data
		doWriteComponentsToBean(aIndustry);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aIndustry.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aIndustry.getRecordType()).equals("")) {
				aIndustry.setVersion(aIndustry.getVersion() + 1);
				if (isNew) {
					aIndustry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIndustry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIndustry.setNewRecord(true);
				}
			}
		} else {
			aIndustry.setVersion(aIndustry.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aIndustry, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_IndustryDialog, "Industry");
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
	 * @param aIndustry
	 *            (Industry)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Industry aIndustry, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aIndustry.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aIndustry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIndustry.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aIndustry.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIndustry.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aIndustry);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aIndustry))) {
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

			aIndustry.setTaskId(taskId);
			aIndustry.setNextTaskId(nextTaskId);
			aIndustry.setRoleCode(getRole());
			aIndustry.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aIndustry, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aIndustry);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIndustry,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIndustry, tranType);
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
		Industry aIndustry = (Industry) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getIndustryService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getIndustryService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getIndustryService().doApprove(auditHeader);

						if (aIndustry.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getIndustryService().doReject(auditHeader);

						if (aIndustry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_IndustryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_IndustryDialog, auditHeader);
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

	public void onClick$btnSearchSubSector(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_IndustryDialog, "SubSector");
		if (dataObject instanceof String) {
			this.subSectorCode.setValue(dataObject.toString());
			this.lovDescSubSectorCodeName.setValue("");
		} else {
			SubSector details = (SubSector) dataObject;
			if (details != null) {
				this.subSectorCode.setValue(details.getSubSectorCode());
				this.lovDescSubSectorCodeName.setValue(details.getLovDescSectorCodeName() + "-" + details.getSubSectorDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aIndustry
	 *            (Industry)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Industry aIndustry, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aIndustry.getBefImage(), aIndustry);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aIndustry.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_IndustryDialog, auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,	map);
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
		final JdbcSearchObject<Industry> soIndustry = getIndustryListCtrl().getSearchObj();
		getIndustryListCtrl().pagingIndustryList.setActivePage(0);
		getIndustryListCtrl().getPagedListWrapper().setSearchObject(soIndustry);
		if (getIndustryListCtrl().listBoxIndustry != null) {
			getIndustryListCtrl().listBoxIndustry.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Industry");
		notes.setReference(getReference());
		notes.setVersion(getIndustry().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getIndustry().getIndustryCode()+PennantConstants.KEY_SEPERATOR +
					getIndustry().getSubSectorCode();
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

	public void setOldVar_subSectorCode(String oldVar_subSectorCode) {
		this.oldVar_subSectorCode = oldVar_subSectorCode;
	}
	public String getOldVar_subSectorCode() {
		return oldVar_subSectorCode;
	}

	public void setOldVar_lovDescSubSector(String oldVar_lovDescSubSector) {
		this.oldVar_lovDescSubSector = oldVar_lovDescSubSector;
	}
	public String getOldVar_lovDescSubSector() {
		return oldVar_lovDescSubSector;
	}

	public Industry getIndustry() {
		return this.industry;
	}
	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	public void setIndustryService(IndustryService industryService) {
		this.industryService = industryService;
	}
	public IndustryService getIndustryService() {
		return this.industryService;
	}

	public void setIndustryListCtrl(IndustryListCtrl industryListCtrl) {
		this.industryListCtrl = industryListCtrl;
	}
	public IndustryListCtrl getIndustryListCtrl() {
		return this.industryListCtrl;
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
