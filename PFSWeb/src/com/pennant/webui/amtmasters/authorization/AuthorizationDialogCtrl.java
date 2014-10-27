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
 * FileName    		:  AuthorizationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2013    														*
 *                                                                  						*
 * Modified Date    :  20-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.authorization;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.amtmasters.AuthorizationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/AMTMasters/Authorization/authorizationDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AuthorizationDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(AuthorizationDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AuthorizationDialog;
	protected Row row0;
	protected Label label_AuthUserId;
	protected Hlayout hlayout_AuthUserId;
	protected Space space_AuthUserId;
	protected Longbox authUserId;
	protected Textbox authUserIdName;
	protected Button btnSearchauthUserId;
	protected Label label_AuthType;
	protected Hlayout hlayout_AuthType;
	protected Space space_AuthType;
	protected Combobox authType;
	protected Row row1;
	protected Label label_AuthName;
	protected Hlayout hlayout_AuthName;
	protected Space space_AuthName;
	protected Textbox authName;
	protected Label label_AuthDept;
	protected Hlayout hlayout_AuthDept;
	protected Space space_AuthDept;
	protected Textbox authDept;
	protected Row row2;
	protected Label label_AuthDesig;
	protected Hlayout hlayout_AuthDesig;
	protected Space space_AuthDesig;
	protected Textbox authDesig;
	protected Label label_AuthSignature;
	protected Hlayout hlayout_AuthSignature;
	protected Space space_AuthSignature;
	protected Textbox authSignature;
	protected Label recordStatus;
	protected Label recordType;
	protected Radiogroup userAction;
	protected Groupbox gb_statusDetails;
	protected Groupbox groupboxWf;
	protected South south;
	private boolean enqModule = false;
	// not auto wired vars
	private Authorization authorization; // overhanded per param
	private transient AuthorizationListCtrl authorizationListCtrl; // overhanded
																	// per param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	@SuppressWarnings("unused")
	private transient long oldVar_AuthUserId;
	private transient String oldVar_AuthType;
	private transient String oldVar_AuthName;
	private transient String oldVar_AuthDept;
	private transient String oldVar_AuthDesig;
	private transient String oldVar_AuthSignature;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AuthorizationDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSave;
	protected Button btnCancel;
	protected Button btnClose;
	protected Button btnHelp;
	protected Button btnNotes;
	protected Button btnSearchAuthDept;
	protected Textbox authDeptName;
	private transient String oldVar_AuthDeptName;
	protected Button btnSearchAuthDesig;
	protected Textbox authDesigName;
	private transient String oldVar_AuthDesigName;
	// ServiceDAOs / Domain Classes
	private transient AuthorizationService authorizationService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public AuthorizationDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Authorization object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AuthorizationDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule = (Boolean) args.get("enqModule");
			} else {
				enqModule = false;
			}
			// READ OVERHANDED params !
			if (args.containsKey("authorization")) {
				this.authorization = (Authorization) args.get("authorization");
				Authorization befImage = new Authorization();
				BeanUtils.copyProperties(this.authorization, befImage);
				this.authorization.setBefImage(befImage);
				setAuthorization(this.authorization);
			} else {
				setAuthorization(null);
			}
			doLoadWorkFlow(this.authorization.isWorkflow(), this.authorization.getWorkflowId(), this.authorization.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "AuthorizationDialog");
			} else {
				getUserWorkspace().alocateAuthorities("AuthorizationDialog");
			}
			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED params !
			// we get the authorizationListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete authorization here.
			if (args.containsKey("authorizationListCtrl")) {
				setAuthorizationListCtrl((AuthorizationListCtrl) args.get("authorizationListCtrl"));
			} else {
				setAuthorizationListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAuthorization());
		} catch (Exception e) {
			createException(window_AuthorizationDialog, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doStoreInitValues();
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
		logger.debug("Entering" + event.toString());
		doResetInitValues();
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		PTMessageUtils.showHelpWindow(event, window_AuthorizationDialog);
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
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_AuthorizationDialog(Event event) throws Exception {
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
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("Authorization", String.valueOf(getAuthorization().getAuthorizedId()), getAuthorization().getVersion()), this);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchauthUserId(Event event) {
		Object dataObject = ExtendedSearchListBox.show(this.window_AuthorizationDialog, "SecurityUsers");
		if (dataObject instanceof String) {
			this.authUserIdName.setValue("");
			this.authName.setValue("");
		} else {
			SecurityUser details = (SecurityUser) dataObject;
			if (details != null) {
				this.authUserId.setValue(details.getUsrID());
				this.authUserIdName.setValue(details.getUsrID() + "-" + details.getUsrLogin());
				this.authName.setValue(details.getUsrFName() + " " + details.getUsrLName());
			}
		}
	}

	public void onClick$btnSearchAuthDept(Event event) {
		Object dataObject = ExtendedSearchListBox.show(this.window_AuthorizationDialog, "Department");
		if (dataObject instanceof String) {
			this.authDept.setValue(dataObject.toString());
			this.authDeptName.setValue("");
		} else {
			Department details = (Department) dataObject;
			if (details != null) {
				this.authDept.setValue(details.getDeptCode());
				this.authDeptName.setValue(details.getDeptCode() + "-" + details.getDeptDesc());
			}
		}
	}

	public void onClick$btnSearchAuthDesig(Event event) {
		Object dataObject = ExtendedSearchListBox.show(this.window_AuthorizationDialog, "Designation");
		if (dataObject instanceof String) {
			this.authDesig.setValue(dataObject.toString());
			this.authDesigName.setValue("");
		} else {
			Designation details = (Designation) dataObject;
			if (details != null) {
				this.authDesig.setValue(details.getDesgCode());
				this.authDesigName.setValue(details.getDesgCode() + "-" + details.getDesgDesc());
			}
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAuthorization
	 * @throws InterruptedException
	 */
	public void doShowDialog(Authorization aAuthorization) throws InterruptedException {
		logger.debug("Entering");
		// if aAuthorization == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aAuthorization == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aAuthorization = getAuthorizationService().getNewAuthorization();
			setAuthorization(aAuthorization);
		} else {
			setAuthorization(aAuthorization);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aAuthorization);
			// set ReadOnly mode accordingly if the object is new or not.
			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aAuthorization.isNewRecord()));
			doStoreInitValues();
			if(!aAuthorization.isNewRecord()){
				this.authType.setDisabled(true);
				this.btnSearchauthUserId.setDisabled(true);
			}
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_AuthorizationDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	private void displayComponents(int mode) {
		logger.debug("Entering");
		System.out.println();
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(), this.userAction, this.authType, this.authUserId));
		if (getAuthorization().isNewRecord()) {
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");
		boolean tempReadOnly = readOnly;
		if (readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(authorization.getRecordType())))) {
			tempReadOnly = true;
		}
		setLovAccess("AuthorizationDialog_AuthUserId", tempReadOnly, this.btnSearchauthUserId, this.space_AuthUserId, this.label_AuthUserId, this.hlayout_AuthUserId, null);
		setComponentAccessType("AuthorizationDialog_AuthType", tempReadOnly, this.authType, this.space_AuthType, this.label_AuthType, this.hlayout_AuthType, null);
		setRowInvisible(this.row0, this.hlayout_AuthUserId, this.hlayout_AuthType);
		setComponentAccessType("AuthorizationDialog_AuthName", tempReadOnly, this.authName, this.space_AuthName, this.label_AuthName, this.hlayout_AuthName, null);
		setLovAccess("AuthorizationDialog_AuthDept", tempReadOnly, this.btnSearchAuthDept, this.space_AuthDept, this.label_AuthDept, this.hlayout_AuthDept, null);
		setRowInvisible(this.row1, this.hlayout_AuthName, this.hlayout_AuthDept);
		setLovAccess("AuthorizationDialog_AuthDesig", tempReadOnly, this.btnSearchAuthDesig, this.space_AuthDesig, this.label_AuthDesig, this.hlayout_AuthDesig, null);
		setComponentAccessType("AuthorizationDialog_AuthSignature", tempReadOnly, this.authSignature, this.space_AuthSignature, this.label_AuthSignature, this.hlayout_AuthSignature, null);
		setRowInvisible(this.row2, this.hlayout_AuthDesig, this.hlayout_AuthSignature);
		logger.debug("Leaving");
	}

	
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		getUserWorkspace().alocateAuthorities("AuthorizationDialog");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AuthorizationDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AuthorizationDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AuthorizationDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AuthorizationDialog_btnSave"));
		}
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.authName.setMaxlength(50);
		this.authDept.setMaxlength(10);
		this.authDesig.setMaxlength(50);
		this.authSignature.setMaxlength(50);
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_AuthType = PennantConstants.List_Select;
		if (this.authType.getSelectedItem() != null) {
			this.oldVar_AuthType = this.authType.getSelectedItem().getValue().toString();
		}
		this.oldVar_AuthName = this.authName.getValue();
		this.oldVar_AuthDept = this.authDept.getValue();
		this.oldVar_AuthDeptName = this.authDeptName.getValue();
		this.oldVar_AuthDesig = this.authDesig.getValue();
		this.oldVar_AuthDesigName = this.authDesigName.getValue();
		this.oldVar_AuthSignature = this.authSignature.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		fillComboBox(this.authType, this.oldVar_AuthType, PennantStaticListUtil.getAuthTypes(), "");
		this.authName.setValue(this.oldVar_AuthName);
		this.authDept.setValue(this.oldVar_AuthDept);
		this.authDeptName.setValue(this.oldVar_AuthDeptName);
		this.authDesig.setValue(this.oldVar_AuthDesig);
		this.authDesigName.setValue(this.oldVar_AuthDesigName);
		this.authSignature.setValue(this.oldVar_AuthSignature);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		if (isWorkFlowEnabled() & !enqModule) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAuthorization
	 *            Authorization
	 */
	public void doWriteBeanToComponents(Authorization aAuthorization) {
		logger.debug("Entering");
		this.authUserId.setValue( aAuthorization.getAuthUserId());
		this.authUserIdName.setValue( aAuthorization.getAuthUserIdName());
		fillComboBox(this.authType, aAuthorization.getAuthType(), PennantStaticListUtil.getAuthTypes(), "");
		this.authName.setValue(aAuthorization.getAuthName());
		this.authDept.setValue(aAuthorization.getAuthDept());
		this.authDesig.setValue(aAuthorization.getAuthDesig());
		this.authSignature.setValue(aAuthorization.getAuthSignature());
		if (aAuthorization.isNewRecord()) {
			this.authDeptName.setValue("");
			this.authDesigName.setValue("");
		} else {
			this.authDeptName.setValue(aAuthorization.getAuthDeptName());
			this.authDesigName.setValue(aAuthorization.getAuthDesigName());
		}
		this.recordStatus.setValue(aAuthorization.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aAuthorization.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAuthorization
	 */
	public void doWriteComponentsToBean(Authorization aAuthorization) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Auth User Id
		try {
			aAuthorization.setAuthUserId(this.authUserId.getValue());
			aAuthorization.setAuthUserIdName(this.authUserIdName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Auth Type
		try {
			String strAuthType = null;
			if (this.authType.getSelectedItem() != null) {
				strAuthType = this.authType.getSelectedItem().getValue().toString();
			}
			if (strAuthType != null && !PennantConstants.List_Select.equals(strAuthType)) {
				aAuthorization.setAuthType(strAuthType);
			} else {
				aAuthorization.setAuthType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Auth Name
		try {
			aAuthorization.setAuthName(this.authName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Auth Dept
		try {
			aAuthorization.setAuthDeptName(this.authDeptName.getValue());
			aAuthorization.setAuthDept(this.authDept.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Auth Desig
		try {
			aAuthorization.setAuthDesigName(this.authDesigName.getValue());
			aAuthorization.setAuthDesig(this.authDesig.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Auth Signature
		try {
			aAuthorization.setAuthSignature(this.authSignature.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();
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
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();

		String strAuthType = PennantConstants.List_Select;
		if (this.authType.getSelectedItem() != null) {
			strAuthType = this.authType.getSelectedItem().getValue().toString();
		}
		if (!StringUtils.trimToEmpty(this.oldVar_AuthType).equals(strAuthType)) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_AuthName).equals(StringUtils.trimToEmpty(this.authName.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_AuthDept).equals(StringUtils.trimToEmpty(this.authDept.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_AuthDesig).equals(StringUtils.trimToEmpty(this.authDesig.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_AuthSignature).equals(StringUtils.trimToEmpty(this.authSignature.getValue()))) {
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
		// Auth Type
		if (!this.authType.isDisabled()) {
			this.authType.setConstraint(new StaticListValidator(PennantStaticListUtil.getAuthTypes(), Labels.getLabel("label_AuthorizationDialog_AuthType.value")));
		}
		// Auth Name
		if (!this.authName.isReadonly()) {
			this.authName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		// Auth Signature
		if (!this.authSignature.isReadonly()) {
			this.authSignature.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthSignature.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		if(!this.authUserIdName.isDisabled()){
			this.authUserIdName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthUserId.value"), null, true));
		}
		if(!this.authDept.isDisabled()){
			this.authDeptName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthDept.value"), null, true));
		}
		if(!this.authDesig.isDisabled()){
			this.authDesigName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthDesig.value"), null, true));

		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.authUserId.setConstraint("");
		this.authType.setConstraint("");
		this.authName.setConstraint("");
		this.authSignature.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// Auth UserId
		if (!btnSearchauthUserId.isVisible()) {
			this.authUserIdName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthUserId.value"), null, true));
		}
		// Auth Dept
		if (!btnSearchAuthDept.isVisible()) {
			this.authDeptName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthDept.value"), null, true));
		}
		// Auth Desig
		if (!btnSearchAuthDesig.isVisible()) {
			this.authDesigName.setConstraint(new PTStringValidator(Labels.getLabel("label_AuthorizationDialog_AuthDesig.value"), null, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.authDeptName.setConstraint("");
		this.authDesigName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.authUserId.setErrorMessage("");
		this.authType.setErrorMessage("");
		this.authName.setErrorMessage("");
		this.authDeptName.setErrorMessage("");
		this.authDesigName.setErrorMessage("");
		this.authSignature.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList() {
		final JdbcSearchObject<Authorization> soAuthorization = getAuthorizationListCtrl().getSearchObj();
		getAuthorizationListCtrl().pagingAuthorizationList.setActivePage(0);
		getAuthorizationListCtrl().getPagedListWrapper().setSearchObject(soAuthorization);
		if (getAuthorizationListCtrl().listBoxAuthorization != null) {
			getAuthorizationListCtrl().listBoxAuthorization.getListModel();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
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
		if (!enqModule && isDataChanged()) {
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
			closeDialog(this.window_AuthorizationDialog, "Authorization");
		}
		logger.debug("Leaving");
	}

	/**
	 * Deletes a Authorization object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Authorization aAuthorization = new Authorization();
		BeanUtils.copyProperties(getAuthorization(), aAuthorization);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aAuthorization.getAuthorizedId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aAuthorization.getRecordType()).equals("")) {
				aAuthorization.setVersion(aAuthorization.getVersion() + 1);
				aAuthorization.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aAuthorization.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aAuthorization.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aAuthorization.getNextTaskId(), aAuthorization);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aAuthorization, tranType)) {
					refreshList();
					closeDialog(this.window_AuthorizationDialog, "Authorization");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_AuthorizationDialog, e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.authType.setSelectedIndex(0);
		this.authName.setValue("");
		this.authDept.setValue("");
		this.authDeptName.setValue("");
		this.authDesig.setValue("");
		this.authDesigName.setValue("");
		this.authSignature.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Authorization aAuthorization = new Authorization();
		BeanUtils.copyProperties(getAuthorization(), aAuthorization);
		boolean isNew = false;
		if (isWorkFlowEnabled()) {
			aAuthorization.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aAuthorization.getNextTaskId(), aAuthorization);
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aAuthorization.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Authorization object with the components data
			doWriteComponentsToBean(aAuthorization);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aAuthorization.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAuthorization.getRecordType()).equals("")) {
				aAuthorization.setVersion(aAuthorization.getVersion() + 1);
				if (isNew) {
					aAuthorization.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAuthorization.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAuthorization.setNewRecord(true);
				}
			}
		} else {
			aAuthorization.setVersion(aAuthorization.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aAuthorization, tranType)) {
				// doWriteBeanToComponents(aAuthorization);
				refreshList();
				closeDialog(this.window_AuthorizationDialog, "Authorization");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_AuthorizationDialog, e);
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
	private boolean doProcess(Authorization aAuthorization, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aAuthorization.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aAuthorization.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAuthorization.setUserDetails(getUserWorkspace().getLoginUserDetails());
		if (isWorkFlowEnabled()) {
			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
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
			aAuthorization.setTaskId(getTaskId());
			aAuthorization.setNextTaskId(getNextTaskId());
			aAuthorization.setRoleCode(getRole());
			aAuthorization.setNextRoleCode(getNextRoleCode());
			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aAuthorization, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aAuthorization, PennantConstants.TRAN_WF);
				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aAuthorization, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;
		Authorization aAuthorization = (Authorization) auditHeader.getAuditDetail().getModelData();
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getAuthorizationService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAuthorizationService().saveOrUpdate(auditHeader);
					}
				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getAuthorizationService().doApprove(auditHeader);
						if (PennantConstants.RECORD_TYPE_DEL.equals(aAuthorization.getRecordType())) {
							deleteNotes = true;
						}
					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getAuthorizationService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aAuthorization.getRecordType())) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AuthorizationDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_AuthorizationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes("Authorization", String.valueOf(aAuthorization.getAuthorizedId()), aAuthorization.getVersion()), true);
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
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(Authorization aAuthorization, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAuthorization.getBefImage(), aAuthorization);
		return new AuditHeader(String.valueOf(aAuthorization.getAuthorizedId()), null, null, null, auditDetail, aAuthorization.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public Authorization getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}

	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

	public void setAuthorizationListCtrl(AuthorizationListCtrl authorizationListCtrl) {
		this.authorizationListCtrl = authorizationListCtrl;
	}

	public AuthorizationListCtrl getAuthorizationListCtrl() {
		return this.authorizationListCtrl;
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
