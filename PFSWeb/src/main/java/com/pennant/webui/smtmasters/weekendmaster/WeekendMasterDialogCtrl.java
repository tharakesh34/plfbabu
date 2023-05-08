/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : WeekendMasterDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-07-2011 * *
 * Modified Date : 11-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.smtmasters.weekendmaster;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.service.smtmasters.WeekendMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/WeekendMaster/weekendMasterDialog.zul file.
 */
public class WeekendMasterDialogCtrl extends GFCBaseCtrl<WeekendMaster> {
	private static final long serialVersionUID = -4145707224044632347L;
	private static final Logger logger = LogManager.getLogger(WeekendMasterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_WeekendMasterDialog; // autowired

	protected Uppercasebox weekendCode; // autowired
	protected Textbox weekendDesc; // autowired
	protected Listbox weekend; // autowired
	protected Textbox weekendText; // autowired

	// not auto wired vars
	private WeekendMaster weekendMaster; // overhanded per param
	private transient WeekendMasterListCtrl weekendMasterListCtrl; // overhanded
	// per param

	private transient boolean validationOn;

	private List<ValueLabel> weekendList = null;
	protected Paging paging;
	protected PagedListWrapper<ValueLabel> listWrapper;
	private Map<String, String> checkMap = new HashMap<String, String>();

	// ServiceDAOs / Domain Classes
	private transient WeekendMasterService weekendMasterService;
	private boolean isdisable = false;

	/**
	 * default constructor.<br>
	 */
	public WeekendMasterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "WeekendMasterDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected WeekendMaster object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_WeekendMasterDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_WeekendMasterDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();
			setListWrapper();

			weekendList = PennantStaticListUtil.getWeekName();

			// READ OVERHANDED params !
			if (arguments.containsKey("weekendMaster")) {
				this.weekendMaster = (WeekendMaster) arguments.get("weekendMaster");
				WeekendMaster befImage = new WeekendMaster();
				BeanUtils.copyProperties(this.weekendMaster, befImage);
				this.weekendMaster.setBefImage(befImage);

				setWeekendMaster(this.weekendMaster);
			} else {
				setWeekendMaster(null);
			}

			doLoadWorkFlow(this.weekendMaster.isWorkflow(), this.weekendMaster.getWorkflowId(),
					this.weekendMaster.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "WeekendMasterDialog");
			}

			// READ OVERHANDED params !
			// we get the weekendMasterListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete weekendMaster here.
			if (arguments.containsKey("weekendMasterListCtrl")) {
				setWeekendMasterListCtrl((WeekendMasterListCtrl) arguments.get("weekendMasterListCtrl"));
			} else {
				setWeekendMasterListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getWeekendMaster());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_WeekendMasterDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.weekendCode.setMaxlength(8);
		this.weekendDesc.setMaxlength(50);
		this.weekend.setMaxlength(50);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering doCheckRights()");
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WeekendMasterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WeekendMasterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_WeekendMasterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WeekendMasterDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving doCheckRights()");
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
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_WeekendMasterDialog);
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
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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
		logger.debug("Entering doCancel()");

		doWriteBeanToComponents(this.weekendMaster.getBefImage());
		doReadOnly();

		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving doCancel()");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aWeekendMaster WeekendMaster
	 */
	public void doWriteBeanToComponents(WeekendMaster aWeekendMaster) {
		logger.debug("Entering doWriteBeanToComponents()");
		this.weekendCode.setValue(aWeekendMaster.getWeekendCode());
		this.weekendDesc.setValue(aWeekendMaster.getWeekendDesc());
		this.weekendText.setValue(aWeekendMaster.getWeekend());
		if (aWeekendMaster.isNewRecord()) {
			this.weekendCode.setValue("");
		} else {
			this.weekendCode.setValue(aWeekendMaster.getWeekendCode());
			StringTokenizer st = new StringTokenizer(aWeekendMaster.getWeekend(), ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				checkMap.put(token, token);
			}
		}
		loadWeekEnd();
		logger.debug("Leaving doWriteBeanToComponents()");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWeekendMaster
	 */
	public void doWriteComponentsToBean(WeekendMaster aWeekendMaster) {
		logger.debug("Entering doWriteComponentsToBean()");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isBlank(this.weekendCode.getValue())) {
				throw new WrongValueException(this.weekendCode, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_WeekendMasterDialog_WeekendCode.value") }));
			}
			aWeekendMaster.setWeekendCode(this.weekendCode.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isBlank(this.weekendDesc.getValue())) {
				throw new WrongValueException(this.weekendDesc, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_WeekendMasterDialog_WeekendDesc.value") }));
			}
			aWeekendMaster.setWeekendDesc(this.weekendDesc.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isEmpty(this.weekendText.getValue())) {
				throw new WrongValueException(weekend, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_WeekendMasterDialog_Weekend.value") }));
			}
			if (this.weekendText.getValue().endsWith(",")) {
				aWeekendMaster
						.setWeekend(this.weekendText.getValue().substring(0, this.weekendText.getValue().length() - 1));
			} else {
				aWeekendMaster.setWeekend(this.weekendText.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doRemoveValidation();
			doRemoveLOVValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving doWriteComponentsToBean()");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aWeekendMaster
	 */
	public void doShowDialog(WeekendMaster aWeekendMaster) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aWeekendMaster.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			btnCancel.setVisible(false);
			// setFocus
			this.weekendCode.focus();
		} else {
			this.weekendDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		String defaultCCy = SysParamUtil.getAppCurrency();
		if (StringUtils.equals(aWeekendMaster.getWeekendCode(), defaultCCy)) {
			btnDelete.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aWeekendMaster);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_WeekendMasterDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving doShowDialog()");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering doSetValidation()");
		setValidationOn(true);

		if (!this.weekendCode.isReadonly()) {
			this.weekendCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_WeekendMasterDialog_WeekendCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}
		if (!this.weekendDesc.isReadonly()) {
			this.weekendDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_WeekendMasterDialog_WeekendDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving doSetValidation()");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering doRemoveValidation()");
		setValidationOn(false);
		this.weekendCode.setConstraint("");
		this.weekendDesc.setConstraint("");
		this.weekend.setCheckmark(false);
		logger.debug("Leaving doRemoveValidation()");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final WeekendMaster aWeekendMaster = new WeekendMaster();
		BeanUtils.copyProperties(getWeekendMaster(), aWeekendMaster);

		String keyReference = Labels.getLabel("label_CountryDialog_CountryCode.value") + " : "
				+ aWeekendMaster.getWeekendCode();

		doDelete(keyReference, aWeekendMaster);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering doEdit()");
		if (getWeekendMaster().isNewRecord()) {
			this.weekendCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.weekendCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			doDisableCheckbox(isReadOnly("WeekendMasterDialog_weekendDesc"));
		}
		this.weekendDesc.setReadonly(isReadOnly("WeekendMasterDialog_weekendDesc"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.weekendMaster.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving doEdit()");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering doReadOnly()");
		this.weekendCode.setReadonly(true);
		this.weekendDesc.setReadonly(true);
		doDisableCheckbox(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving doReadOnly()");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering doClear()");
		// remove validation, if there are a save before
		this.weekendCode.setValue("");
		this.weekendDesc.setValue("");
		this.weekend.setSelectedIndex(0);
		logger.debug("Leaving doClear()");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering doSave()");
		final WeekendMaster aWeekendMaster = new WeekendMaster();
		BeanUtils.copyProperties(getWeekendMaster(), aWeekendMaster);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the WeekendMaster object with the components data
		doWriteComponentsToBean(aWeekendMaster);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aWeekendMaster.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aWeekendMaster.getRecordType())) {
				aWeekendMaster.setVersion(aWeekendMaster.getVersion() + 1);
				if (isNew) {
					aWeekendMaster.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aWeekendMaster.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aWeekendMaster.setNewRecord(true);
				}
			}
		} else {
			aWeekendMaster.setVersion(aWeekendMaster.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aWeekendMaster, tranType)) {
				refreshList();
				aWeekendMaster.setNewRecord(false);
				doWriteBeanToComponents(aWeekendMaster);
				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<WeekendMaster> soWeekendMaster = getWeekendMasterListCtrl().getSearchObject();

				// Set the ListModel
				getWeekendMasterListCtrl().pagingWeekendMasterList.setActivePage(0);
				getWeekendMasterListCtrl().getPagedListWrapper().setSearchObject(soWeekendMaster);

				// call from cusromerList then synchronize the WeekendMaster
				// listBox
				if (getWeekendMasterListCtrl().listBoxWeekendMaster != null) {
					// now synchronize the WeekendMaster listBox
					getWeekendMasterListCtrl().listBoxWeekendMaster.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving doSave()");
	}

	protected boolean doProcess(WeekendMaster aWeekendMaster, String tranType) {
		logger.debug("Entering doProcess()");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aWeekendMaster.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aWeekendMaster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aWeekendMaster.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aWeekendMaster.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aWeekendMaster.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aWeekendMaster);
				}

				if (isNotesMandatory(taskId, aWeekendMaster)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
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

			aWeekendMaster.setTaskId(taskId);
			aWeekendMaster.setNextTaskId(nextTaskId);
			aWeekendMaster.setRoleCode(getRole());
			aWeekendMaster.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aWeekendMaster, tranType);

			String operationRefs = getServiceOperations(taskId, aWeekendMaster);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aWeekendMaster, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aWeekendMaster, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving doProcess()");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		WeekendMaster aWeekendMaster = (WeekendMaster) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getWeekendMasterService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getWeekendMasterService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getWeekendMasterService().doApprove(auditHeader);

					if (aWeekendMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getWeekendMasterService().doReject(auditHeader);

					if (aWeekendMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_WeekendMasterDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_WeekendMasterDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.weekendMaster), true);
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(WeekendMaster aWeekendMaster, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aWeekendMaster.getBefImage(), aWeekendMaster);

		return new AuditHeader(String.valueOf(aWeekendMaster.getId()), null, null, null, auditDetail,
				aWeekendMaster.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.weekendMaster);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.weekendMaster.getWeekendCode());
	}

	private void loadWeekEnd() {
		logger.debug("Entering loadWeekEnd()");
		paging.setPageSize(8);
		paging.setDetailed(true);
		listWrapper.initList(weekendList, this.weekend, paging);
		this.weekend.setItemRenderer(new WeekendItemRenderer());
		logger.debug("Leaving loadWeekEnd()");
	}

	public class WeekendItemRenderer implements ListitemRenderer<ValueLabel>, Serializable {

		private static final long serialVersionUID = 1L;

		public WeekendItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, ValueLabel valueLabel, int count) {
			Listcell lc;
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(valueLabel.getValue());
			checkbox.setLabel(valueLabel.getLabel());
			checkbox.setChecked(checkMap.containsKey(valueLabel.getValue()));
			checkbox.addEventListener("onCheck", new onCheckBoxCheked());
			if (isdisable) {
				checkbox.setDisabled(true);
			} else {
				checkbox.setDisabled(false);
			}

			lc = new Listcell();
			lc.appendChild(checkbox);
			lc.setParent(item);
		}
	}

	public final class onCheckBoxCheked implements EventListener<Event> {

		public onCheckBoxCheked() {
		    super();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void onEvent(Event event) {
			logger.debug("onEvent()");
			Checkbox checkbox = (Checkbox) event.getTarget();
			if (checkbox.isChecked()) {
				checkMap.put(checkbox.getValue().toString(), checkbox.getValue().toString());
			} else {
				checkMap.remove(checkbox.getValue());
			}
			List list = new ArrayList();
			list.addAll(checkMap.keySet());
			Collections.sort(list);
			String str = "";
			for (int i = 0; i < list.size(); i++) {
				str = str + list.get(i) + ",";
			}
			weekendText.setValue(str);
			logger.debug("Leaving onEvent()");
		}

	}

	/**
	 * This method Used for Disabled/Enabled the checkboxs.
	 * 
	 * @param disable
	 */
	public void doDisableCheckbox(boolean disable) {
		logger.debug("Entering");
		isdisable = disable;
		if (this.weekend.getItems() != null && !this.weekend.getItems().isEmpty()) {
			for (Listitem listitem : this.weekend.getItems()) {
				if (listitem.getFirstChild() != null && listitem.getFirstChild().getFirstChild() != null
						&& listitem.getFirstChild().getFirstChild() instanceof Checkbox) {
					Checkbox checkbox = (Checkbox) listitem.getFirstChild().getFirstChild();
					checkbox.setDisabled(disable);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getWeekendMasterListCtrl().search();
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

	public WeekendMaster getWeekendMaster() {
		return this.weekendMaster;
	}

	public void setWeekendMaster(WeekendMaster weekendMaster) {
		this.weekendMaster = weekendMaster;
	}

	public void setWeekendMasterService(WeekendMasterService weekendMasterService) {
		this.weekendMasterService = weekendMasterService;
	}

	public WeekendMasterService getWeekendMasterService() {
		return this.weekendMasterService;
	}

	public void setWeekendMasterListCtrl(WeekendMasterListCtrl weekendMasterListCtrl) {
		this.weekendMasterListCtrl = weekendMasterListCtrl;
	}

	public WeekendMasterListCtrl getWeekendMasterListCtrl() {
		return this.weekendMasterListCtrl;
	}

	public PagedListWrapper<ValueLabel> getListWrapper() {
		return listWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setListWrapper() {
		if (this.listWrapper == null) {
			this.listWrapper = (PagedListWrapper<ValueLabel>) SpringUtil.getBean("pagedListWrapper");
		}
	}

}
