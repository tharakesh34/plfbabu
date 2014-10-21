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
 * FileName    		:  SystemInternalAccountDefinitionDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.masters.systeminternalaccountdefinition;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.masters.SystemInternalAccountDefinitionService;
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
 * /WEB-INF/pages/Account/SystemInternalAccountDefinition
 * /systemInternalAccountDefinitionDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SystemInternalAccountDefinitionDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3353102918724238160L;
	private final static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
	 * All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting 
	 * autowired by our 'extends GFCBaseCtrl' GenericForwardComposer. 
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	   window_SystemInternalAccountDefinitionDialog;// autowired
	protected Textbox	   sIACode;	                                    // autowired
	protected Textbox	   sIAName;	                                    // autowired
	protected Textbox	   sIAShortName;	                            // autowired
	protected ExtendedCombobox	   sIAAcType;	                                // autowired
	protected Textbox	   sIANumber;	                                // autowired

	protected Label	       recordStatus;	                            // autowired
	protected Radiogroup   userAction;
	protected Groupbox	   groupboxWf;

	// not auto wired vars
	private SystemInternalAccountDefinition	systemInternalAccountDefinition;	                                                           // overhanded
	private transient SystemInternalAccountDefinitionListCtrl systemInternalAccountDefinitionListCtrl;	                                               // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String	                              oldVar_sIACode;
	private transient String	                              oldVar_sIAName;
	private transient String	                              oldVar_sIAShortName;
	private transient String	                              oldVar_sIAAcType;
	private transient String	                              oldVar_sIANumber;
	private transient String	                              oldVar_recordStatus;

	private transient boolean	                              validationOn;
	private boolean	                                          notes_Entered	          = false;

	// Button controller for the CRUD buttons
	private transient final String	                          btnCtroller_ClassPrefix	= "button_SystemInternalAccountDefinitionDialog_";
	private transient ButtonStatusCtrl	                      btnCtrl;
	protected Button	                                      btnNew;	                                                                                   // autowire
	protected Button	                                      btnEdit;	                                                                                   // autowire
	protected Button	                                      btnDelete;	                                                                               // autowire
	protected Button	                                      btnSave;	                                                                                   // autowire
	protected Button	                                      btnCancel;	                                                                               // autowire
	protected Button	                                      btnClose;	                                                                               // autowire
	protected Button	                                      btnHelp;	                                                                                   // autowire
	protected Button	                                      btnNotes;	                                                                               // autowire

	//protected Button	                                      btnSearchSIAAcType;	                                                                       // autowire
	//protected Textbox	                                      lovDescSIAAcTypeName;
	private transient String	                              oldVar_lovDescSIAAcTypeName;

	// ServiceDAOs / Domain Classes
	private transient SystemInternalAccountDefinitionService	systemInternalAccountDefinitionService;
	private transient PagedListService	                      pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>>	      overideMap	          = new HashMap<String, ArrayList<ErrorDetails>>();

	protected Textbox	                                      sIAheadCode;	                                                                               // autowired
	protected Textbox	                                      sIASeqNumber;	                                                                           // autowired

	/**
	 * default constructor.<br>
	 */
	public SystemInternalAccountDefinitionDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected
	 * SystemInternalAccountDefinition object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SystemInternalAccountDefinitionDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
		        this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("systemInternalAccountDefinition")) {
			this.systemInternalAccountDefinition = (SystemInternalAccountDefinition) args.get("systemInternalAccountDefinition");
			SystemInternalAccountDefinition befImage = new SystemInternalAccountDefinition();
			BeanUtils.copyProperties(this.systemInternalAccountDefinition, befImage);
			this.systemInternalAccountDefinition.setBefImage(befImage);

			setSystemInternalAccountDefinition(this.systemInternalAccountDefinition);
		} else {
			setSystemInternalAccountDefinition(null);
		}

		doLoadWorkFlow(this.systemInternalAccountDefinition.isWorkflow(), this.systemInternalAccountDefinition.getWorkflowId(),
		        this.systemInternalAccountDefinition.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SystemInternalAccountDefinitionDialog");
		}

		// READ OVERHANDED params !
		// we get the systemInternalAccountDefinitionListWindow controller. So
		// we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete systemInternalAccountDefinition here.
		if (args.containsKey("systemInternalAccountDefinitionListCtrl")) {
			setSystemInternalAccountDefinitionListCtrl((SystemInternalAccountDefinitionListCtrl) args.get("systemInternalAccountDefinitionListCtrl"));
		} else {
			setSystemInternalAccountDefinitionListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSystemInternalAccountDefinition());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.sIACode.setMaxlength(8);
		this.sIAName.setMaxlength(50);
		this.sIAShortName.setMaxlength(15);
		
		this.sIAAcType.setMaxlength(8);
		this.sIAAcType.setMandatoryStyle(true);
		this.sIAAcType.setModuleName("SystemInternalAccountType");
		this.sIAAcType.setValueColumn("AcType");
		this.sIAAcType.setDescColumn("AcTypeDesc");
		this.sIAAcType.setValidateColumns(new String[] { "AcType" });
		
		this.sIANumber.setMaxlength(8);
		this.sIAheadCode.setMaxlength(2);
		if(SystemParameterDetails.getSystemParameterValue("CBI_AVAIL").equals("Y")){
			this.sIAheadCode.setValue("00");
			this.sIAheadCode.setVisible(false);
		}
		int numLength = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("SYSINT_ACCOUNT_LEN").toString());
		this.sIASeqNumber.setMaxlength(numLength);
		this.sIASeqNumber.setWidth((numLength*12)+"px");

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

		getUserWorkspace().alocateAuthorities("SystemInternalAccountDefinitionDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnSave"));
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
	public void onClose$window_SystemInternalAccountDefinitionDialog(Event event) throws Exception {
		logger.debug(event.toString());
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
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_SystemInternalAccountDefinitionDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
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
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
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
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// GUI Process

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
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_SystemInternalAccountDefinitionDialog, "SystemInternalAccountDefinition");
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
	 * @param aSystemInternalAccountDefinition
	 *            SystemInternalAccountDefinition
	 */
	public void doWriteBeanToComponents(SystemInternalAccountDefinition aSystemInternalAccountDefinition) {
		logger.debug("Entering");
		this.sIACode.setValue(aSystemInternalAccountDefinition.getSIACode());
		this.sIAName.setValue(aSystemInternalAccountDefinition.getSIAName());
		this.sIAShortName.setValue(aSystemInternalAccountDefinition.getSIAShortName());
		this.sIAAcType.setValue(aSystemInternalAccountDefinition.getSIAAcType());
		this.sIANumber.setValue(aSystemInternalAccountDefinition.getSIANumber());
		if (aSystemInternalAccountDefinition.getSIANumber() != null) {
			String number = aSystemInternalAccountDefinition.getSIANumber().trim();
			String headcode = number.substring(0, 2);
			String numb = number.substring(2, aSystemInternalAccountDefinition.getSIANumber().length());
			this.sIASeqNumber.setValue(numb);
			this.sIAheadCode.setValue(headcode);
		}
		if (aSystemInternalAccountDefinition.isNewRecord()) {
			this.sIAAcType.setDescription("");
		} else {
			this.sIAAcType.setDescription(aSystemInternalAccountDefinition.getLovDescSIAAcTypeName());
		}
		this.recordStatus.setValue(aSystemInternalAccountDefinition.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSystemInternalAccountDefinition
	 */
	public void doWriteComponentsToBean(SystemInternalAccountDefinition aSystemInternalAccountDefinition) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSystemInternalAccountDefinition.setSIACode(this.sIACode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSystemInternalAccountDefinition.setSIAName(this.sIAName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSystemInternalAccountDefinition.setSIAShortName(this.sIAShortName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSystemInternalAccountDefinition.setLovDescSIAAcTypeName(this.sIAAcType.getDescription());
			aSystemInternalAccountDefinition.setSIAAcType(this.sIAAcType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.sIANumber.setValue(this.sIAheadCode.getValue().trim() + this.sIASeqNumber.getValue());

			if (!this.sIANumber.isReadonly()) {
				if (this.sIANumber.getValue().equals("")) {
					throw new WrongValueException(this.sIASeqNumber, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value") }));
				} else if (this.sIANumber.getValue().length() < 8) {
					throw new WrongValueException(this.sIASeqNumber, Labels.getLabel("REQUIRED_LENGTH",
					        new String[] { Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value"), "8" }));
				}
			}

			aSystemInternalAccountDefinition.setSIANumber(this.sIANumber.getValue());
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

		aSystemInternalAccountDefinition.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSystemInternalAccountDefinition
	 * @throws InterruptedException
	 */
	public void doShowDialog(SystemInternalAccountDefinition aSystemInternalAccountDefinition) throws InterruptedException {
		logger.debug("Entering");

		// if aSystemInternalAccountDefinition == null then we opened the Dialog
		// without
		// args for a given entity, so we get a new Obj().
		if (aSystemInternalAccountDefinition == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aSystemInternalAccountDefinition = getSystemInternalAccountDefinitionService().getNewSystemInternalAccountDefinition();

			setSystemInternalAccountDefinition(aSystemInternalAccountDefinition);
		} else {
			setSystemInternalAccountDefinition(aSystemInternalAccountDefinition);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aSystemInternalAccountDefinition.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sIACode.focus();
		} else {
			this.sIAName.focus();
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
			doWriteBeanToComponents(aSystemInternalAccountDefinition);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SystemInternalAccountDefinitionDialog);
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
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_sIACode = this.sIACode.getValue();
		this.oldVar_sIAName = this.sIAName.getValue();
		this.oldVar_sIAShortName = this.sIAShortName.getValue();
		this.oldVar_sIAAcType = this.sIAAcType.getValue();
		this.oldVar_lovDescSIAAcTypeName = this.sIAAcType.getDescription();
		this.oldVar_sIANumber = this.sIANumber.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.sIACode.setValue(this.oldVar_sIACode);
		this.sIAName.setValue(this.oldVar_sIAName);
		this.sIAShortName.setValue(this.oldVar_sIAShortName);
		this.sIAAcType.setValue(this.oldVar_sIAAcType);
		this.sIAAcType.setDescription(this.oldVar_lovDescSIAAcTypeName);
		this.sIANumber.setValue(this.oldVar_sIANumber);
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
		if (this.oldVar_sIAAcType != this.sIAAcType.getValue()) {
			return true;
		}
		if (this.oldVar_sIACode != this.sIACode.getValue()) {
			return true;
		}
		if (this.oldVar_sIAName != this.sIAName.getValue()) {
			return true;
		}
		if (this.oldVar_sIANumber != this.sIANumber.getValue()) {
			return true;
		}
		if (this.oldVar_sIAShortName != this.sIAShortName.getValue()) {
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

		if (!this.sIACode.isReadonly()) {
			this.sIACode.setConstraint(new PTStringValidator(Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIACode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.sIAName.isReadonly()) {
			this.sIAName.setConstraint(new PTStringValidator(Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIAName.value"), PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL, true));

		}
		if (!this.sIAShortName.isReadonly()) {
			this.sIAShortName.setConstraint(new PTStringValidator(Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIAShortName.value"), PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.sIACode.setConstraint("");
		this.sIAName.setConstraint("");
		this.sIAShortName.setConstraint("");
		this.sIANumber.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a SystemInternalAccountDefinition object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SystemInternalAccountDefinition aSystemInternalAccountDefinition = new SystemInternalAccountDefinition();
		BeanUtils.copyProperties(getSystemInternalAccountDefinition(), aSystemInternalAccountDefinition);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aSystemInternalAccountDefinition.getSIACode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSystemInternalAccountDefinition.getRecordType()).equals("")) {
				aSystemInternalAccountDefinition.setVersion(aSystemInternalAccountDefinition.getVersion() + 1);
				aSystemInternalAccountDefinition.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSystemInternalAccountDefinition.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSystemInternalAccountDefinition, tranType)) {
					refreshList();
					closeDialog(this.window_SystemInternalAccountDefinitionDialog, "SystemInternalAccountDefinition");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new SystemInternalAccountDefinition object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final SystemInternalAccountDefinition aSystemInternalAccountDefinition = getSystemInternalAccountDefinitionService().getNewSystemInternalAccountDefinition();
		aSystemInternalAccountDefinition.setNewRecord(true);
		setSystemInternalAccountDefinition(aSystemInternalAccountDefinition);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.sIACode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getSystemInternalAccountDefinition().isNewRecord()) {
			this.sIACode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.sIACode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.sIAName.setReadonly(isReadOnly("SystemInternalAccountDefinitionDialog_sIAName"));
		this.sIAShortName.setReadonly(isReadOnly("SystemInternalAccountDefinitionDialog_sIAShortName"));
		this.sIAAcType.setReadonly(isReadOnly("SystemInternalAccountDefinitionDialog_sIAAcType"));
		this.sIANumber.setReadonly(isReadOnly("SystemInternalAccountDefinitionDialog_sIANumber"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.systemInternalAccountDefinition.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.sIACode.setReadonly(true);
		this.sIAName.setReadonly(true);
		this.sIAShortName.setReadonly(true);
		this.sIAAcType.setReadonly(true);
		this.sIANumber.setReadonly(true);

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

		this.sIACode.setValue("");
		this.sIAName.setValue("");
		this.sIAShortName.setValue("");
		this.sIAAcType.setValue("");
		this.sIAAcType.setDescription("");
		this.sIANumber.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SystemInternalAccountDefinition aSystemInternalAccountDefinition = new SystemInternalAccountDefinition();
		BeanUtils.copyProperties(getSystemInternalAccountDefinition(), aSystemInternalAccountDefinition);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SystemInternalAccountDefinition object with the components
		// data
		doWriteComponentsToBean(aSystemInternalAccountDefinition);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aSystemInternalAccountDefinition.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSystemInternalAccountDefinition.getRecordType()).equals("")) {
				aSystemInternalAccountDefinition.setVersion(aSystemInternalAccountDefinition.getVersion() + 1);
				if (isNew) {
					aSystemInternalAccountDefinition.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSystemInternalAccountDefinition.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSystemInternalAccountDefinition.setNewRecord(true);
				}
			}
		} else {
			aSystemInternalAccountDefinition.setVersion(aSystemInternalAccountDefinition.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSystemInternalAccountDefinition, tranType)) {
				doWriteBeanToComponents(aSystemInternalAccountDefinition);
				refreshList();
				closeDialog(this.window_SystemInternalAccountDefinitionDialog, "SystemInternalAccountDefinition");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(SystemInternalAccountDefinition aSystemInternalAccountDefinition, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSystemInternalAccountDefinition.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSystemInternalAccountDefinition.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSystemInternalAccountDefinition.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String
			aSystemInternalAccountDefinition.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSystemInternalAccountDefinition.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSystemInternalAccountDefinition);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aSystemInternalAccountDefinition))) {
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

			aSystemInternalAccountDefinition.setTaskId(taskId);
			aSystemInternalAccountDefinition.setNextTaskId(nextTaskId);
			aSystemInternalAccountDefinition.setRoleCode(getRole());
			aSystemInternalAccountDefinition.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSystemInternalAccountDefinition, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aSystemInternalAccountDefinition);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSystemInternalAccountDefinition, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSystemInternalAccountDefinition, tranType);
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

		SystemInternalAccountDefinition aSystemInternalAccountDefinition = (SystemInternalAccountDefinition) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSystemInternalAccountDefinitionService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSystemInternalAccountDefinitionService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSystemInternalAccountDefinitionService().doApprove(auditHeader);

						if (aSystemInternalAccountDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSystemInternalAccountDefinitionService().doReject(auditHeader);
						if (aSystemInternalAccountDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SystemInternalAccountDefinitionDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SystemInternalAccountDefinitionDialog, auditHeader);
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

	public void onFulfill$sIAAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = sIAAcType.getObject();
		if (dataObject instanceof String) {
			this.sIAAcType.setValue(dataObject.toString());
			this.sIAAcType.setDescription("");
			this.sIAheadCode.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.sIAAcType.setValue(details.getAcType());
				this.sIAAcType.setDescription(details.getAcTypeDesc());
				//this.sIAheadCode.setValue(details.getAcHeadCode().substring(0,2));
			}
		}
		logger.debug("Leaving" + event.toString());
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

	public SystemInternalAccountDefinition getSystemInternalAccountDefinition() {
		return this.systemInternalAccountDefinition;
	}

	public void setSystemInternalAccountDefinition(SystemInternalAccountDefinition systemInternalAccountDefinition) {
		this.systemInternalAccountDefinition = systemInternalAccountDefinition;
	}

	public void setSystemInternalAccountDefinitionService(SystemInternalAccountDefinitionService systemInternalAccountDefinitionService) {
		this.systemInternalAccountDefinitionService = systemInternalAccountDefinitionService;
	}

	public SystemInternalAccountDefinitionService getSystemInternalAccountDefinitionService() {
		return this.systemInternalAccountDefinitionService;
	}

	public void setSystemInternalAccountDefinitionListCtrl(SystemInternalAccountDefinitionListCtrl systemInternalAccountDefinitionListCtrl) {
		this.systemInternalAccountDefinitionListCtrl = systemInternalAccountDefinitionListCtrl;
	}

	public SystemInternalAccountDefinitionListCtrl getSystemInternalAccountDefinitionListCtrl() {
		return this.systemInternalAccountDefinitionListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(SystemInternalAccountDefinition aSystemInternalAccountDefinition, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSystemInternalAccountDefinition.getBefImage(), aSystemInternalAccountDefinition);
		return new AuditHeader(aSystemInternalAccountDefinition.getSIACode(), null, null, null, auditDetail, aSystemInternalAccountDefinition.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SystemInternalAccountDefinitionDialog, auditHeader);
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

	private void doSetLOVValidation() {
		this.sIAAcType.setConstraint(new PTStringValidator(Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIAAcType.value"), null, true));
	}

	private void doRemoveLOVValidation() {
		this.sIAAcType.setConstraint("");
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("SystemInternalAccountDefinition");
		notes.setReference(getSystemInternalAccountDefinition().getSIACode());
		notes.setVersion(getSystemInternalAccountDefinition().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.sIACode.setErrorMessage("");
		this.sIAName.setErrorMessage("");
		this.sIAShortName.setErrorMessage("");
		this.sIAAcType.setErrorMessage("");
		this.sIANumber.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		final JdbcSearchObject<SystemInternalAccountDefinition> soSystemInternalAccountDefinition = getSystemInternalAccountDefinitionListCtrl().getSearchObj();
		getSystemInternalAccountDefinitionListCtrl().pagingSystemInternalAccountDefinitionList.setActivePage(0);
		getSystemInternalAccountDefinitionListCtrl().getPagedListWrapper().setSearchObject(soSystemInternalAccountDefinition);
		if (getSystemInternalAccountDefinitionListCtrl().listBoxSystemInternalAccountDefinition != null) {
			getSystemInternalAccountDefinitionListCtrl().listBoxSystemInternalAccountDefinition.getListModel();
		}
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
