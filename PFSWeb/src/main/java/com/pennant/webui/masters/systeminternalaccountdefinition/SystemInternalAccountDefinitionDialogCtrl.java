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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.masters.SystemInternalAccountDefinitionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Account/SystemInternalAccountDefinition
 * /systemInternalAccountDefinitionDialog.zul file.
 */
public class SystemInternalAccountDefinitionDialogCtrl extends GFCBaseCtrl<SystemInternalAccountDefinition> {
	private static final long serialVersionUID = -3353102918724238160L;
	private final static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionDialogCtrl.class);

	/*
	 * All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting 
	 * autowired by our 'extends GFCBaseCtrl' GenericForwardComposer. 
	 */
	protected Window	   window_SystemInternalAccountDefinitionDialog;// autowired
	protected Textbox	   sIACode;	                                    // autowired
	protected Textbox	   sIAName;	                                    // autowired
	protected Textbox	   sIAShortName;	                            // autowired
	protected ExtendedCombobox	   sIAAcType;	                                // autowired
	protected Textbox	   sIANumber;	                                // autowired

	// not auto wired vars
	private SystemInternalAccountDefinition	systemInternalAccountDefinition;	                                                           // overhanded
	private transient SystemInternalAccountDefinitionListCtrl systemInternalAccountDefinitionListCtrl;	                                               // overhanded

	private transient boolean	                              validationOn;
	
	//protected Button	                                      btnSearchSIAAcType;	                                                                       // autowire
	//protected Textbox	                                      lovDescSIAAcTypeName;

	// ServiceDAOs / Domain Classes
	private transient SystemInternalAccountDefinitionService	systemInternalAccountDefinitionService;
	private HashMap<String, ArrayList<ErrorDetails>>	      overideMap	          = new HashMap<String, ArrayList<ErrorDetails>>();

	protected Textbox	                                      sIAheadCode;	                                                                               // autowired
	protected Textbox	                                      sIASeqNumber;	                                                                           // autowired

	/**
	 * default constructor.<br>
	 */
	public SystemInternalAccountDefinitionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SystemInternalAccountDefinitionDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected
	 * SystemInternalAccountDefinition object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SystemInternalAccountDefinitionDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SystemInternalAccountDefinitionDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("systemInternalAccountDefinition")) {
				this.systemInternalAccountDefinition = (SystemInternalAccountDefinition) arguments
						.get("systemInternalAccountDefinition");
				SystemInternalAccountDefinition befImage = new SystemInternalAccountDefinition();
				BeanUtils.copyProperties(this.systemInternalAccountDefinition,
						befImage);
				this.systemInternalAccountDefinition.setBefImage(befImage);

				setSystemInternalAccountDefinition(this.systemInternalAccountDefinition);
			} else {
				setSystemInternalAccountDefinition(null);
			}

			doLoadWorkFlow(this.systemInternalAccountDefinition.isWorkflow(),
					this.systemInternalAccountDefinition.getWorkflowId(),
					this.systemInternalAccountDefinition.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SystemInternalAccountDefinitionDialog");
			}

			// READ OVERHANDED params !
			// we get the systemInternalAccountDefinitionListWindow controller.
			// So
			// we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete systemInternalAccountDefinition here.
			if (arguments.containsKey("systemInternalAccountDefinitionListCtrl")) {
				setSystemInternalAccountDefinitionListCtrl((SystemInternalAccountDefinitionListCtrl) arguments
						.get("systemInternalAccountDefinitionListCtrl"));
			} else {
				setSystemInternalAccountDefinitionListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSystemInternalAccountDefinition());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_SystemInternalAccountDefinitionDialog.onClose();
		}
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
		if ("Y".equals(SysParamUtil.getValueAsString("CBI_AVAIL"))) {
			this.sIAheadCode.setValue("00");
			this.sIAheadCode.setVisible(false);
		}
		int numLength = SysParamUtil.getValueAsInt("SYSINT_ACCOUNT_LEN");
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionDialog_btnSave"));
		this.btnCancel.setVisible(false);

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
		MessageUtil.showHelpWindow(event, window_SystemInternalAccountDefinitionDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.systemInternalAccountDefinition.getBefImage());
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
			if(StringUtils.isEmpty(this.sIASeqNumber.getValue())){
				this.sIAheadCode.setValue("");
			}else{
				this.sIAheadCode.setValue("00");
			}
			this.sIANumber.setValue(this.sIAheadCode.getValue().trim() + this.sIASeqNumber.getValue());

			if (!this.sIANumber.isReadonly()) {
				if (StringUtils.isEmpty(this.sIANumber.getValue()) || "00".equals(this.sIANumber.getValue())) {
					throw new WrongValueException(this.sIASeqNumber, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value")}));
				}
				String Regex_Numer = "[0-9]+" ;
				if(!this.sIANumber.getValue().matches(Regex_Numer)){
					throw new WrongValueException(this.sIASeqNumber, Labels.getLabel("FIELD_NUMBER",
							new String[] { Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value")}));	
				}else if (this.sIANumber.getValue().length() < 8) {
						throw new WrongValueException(this.sIASeqNumber, Labels.getLabel("REQUIRED_LENGTH",
								new String[] { Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value"), "6" }));
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
	 * @throws Exception
	 */
	public void doShowDialog(SystemInternalAccountDefinition aSystemInternalAccountDefinition) throws Exception {
		logger.debug("Entering");

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

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SystemInternalAccountDefinitionDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
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
		/*if (!this.sIANumber.isReadonly() && this.sIANumber.getValue().equals("")) {
			throw new WrongValueException(this.sIASeqNumber, Labels.getLabel("FIELD_NO_EMPTY",
			        new String[] { Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value"), Labels.getLabel("REGEX_NUM") }));
			this.sIANumber.setConstraint(new PTNumberValidator(Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIANumber.value"),true));
		}*/

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

	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIACode.value")+" : "+aSystemInternalAccountDefinition.getSIACode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aSystemInternalAccountDefinition.getRecordType())) {
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
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				MessageUtil.showErrorMessage(e);
			}

		}
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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aSystemInternalAccountDefinition.getRecordType())) {
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
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(SystemInternalAccountDefinition aSystemInternalAccountDefinition, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSystemInternalAccountDefinition.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aSystemInternalAccountDefinition.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSystemInternalAccountDefinition.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSystemInternalAccountDefinition.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSystemInternalAccountDefinition.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSystemInternalAccountDefinition);
				}

				if (isNotesMandatory(taskId, aSystemInternalAccountDefinition)) {
					try {
						if (!notesEntered) {
							MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aSystemInternalAccountDefinition.setTaskId(taskId);
			aSystemInternalAccountDefinition.setNextTaskId(nextTaskId);
			aSystemInternalAccountDefinition.setRoleCode(getRole());
			aSystemInternalAccountDefinition.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSystemInternalAccountDefinition, tranType);

			String operationRefs = getServiceOperations(taskId, aSystemInternalAccountDefinition);

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

				if (StringUtils.isBlank(method)) {
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
						deleteNotes(getNotes(this.systemInternalAccountDefinition), true);
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
			logger.error("Exception: ", e);
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	private AuditHeader getAuditHeader(SystemInternalAccountDefinition aSystemInternalAccountDefinition, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSystemInternalAccountDefinition.getBefImage(), aSystemInternalAccountDefinition);
		return new AuditHeader(aSystemInternalAccountDefinition.getSIACode(), null, null, null, auditDetail, aSystemInternalAccountDefinition.getUserDetails(), getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SystemInternalAccountDefinitionDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.systemInternalAccountDefinition);
	}

	private void doSetLOVValidation() {
		this.sIAAcType.setConstraint(new PTStringValidator(Labels.getLabel("label_SystemInternalAccountDefinitionDialog_SIAAcType.value"), null, true,true));
	}

	private void doRemoveLOVValidation() {
		this.sIAAcType.setConstraint("");
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.systemInternalAccountDefinition.getSIACode());
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.sIACode.setErrorMessage("");
		this.sIAName.setErrorMessage("");
		this.sIAShortName.setErrorMessage("");
		this.sIAAcType.setErrorMessage("");
		this.sIANumber.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSystemInternalAccountDefinitionListCtrl().search();
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
