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
 * * FileName : IRRCodeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2017 * * Modified
 * Date : 21-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.irrcode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.IRRCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/IRRCode/iRRCodeDialog.zul file. <br>
 */
public class IRRCodeDialogCtrl extends GFCBaseCtrl<IRRCode> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(IRRCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IRRCodeDialog;
	protected Textbox iRRCode;
	protected Textbox iRRCodeDesc;
	protected Checkbox active;
	protected Button button_IRRFeeTypeList_NewIRRFeeType;
	protected Listbox listBoxIRRFeeType;
	private IRRCode aIRRCode; // overhanded per param

	protected Grid grid_Basicdetails;
	protected Paging pagingIRRCodeDialog;

	private transient IRRCodeListCtrl iRRCodeListCtrl; // overhanded per param
	private transient IRRCodeService iRRCodeService;
	private List<IRRFeeType> irrFeeTypesList;
	private String moduleType = "";

	/**
	 * default constructor.<br>
	 */
	public IRRCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IRRCodeDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.aIRRCode.getIRRID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_IRRCodeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_IRRCodeDialog);

		try {
			// Get the required arguments.
			this.aIRRCode = (IRRCode) arguments.get("irrcode");
			this.iRRCodeListCtrl = (IRRCodeListCtrl) arguments.get("irrcodeListCtrl");

			if (this.aIRRCode == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
				enqiryModule = true;
			}

			// Store the before image.
			IRRCode iRRCode = new IRRCode();
			BeanUtils.copyProperties(this.iRRCode, iRRCode);
			this.aIRRCode.setBefImage(iRRCode);

			// Render the page and display the data.
			doLoadWorkFlow(this.aIRRCode.isWorkflow(), this.aIRRCode.getWorkflowId(), this.aIRRCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 170;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listBoxIRRFeeType.setHeight(listboxHeight + "px");
			int listRows = Math.round(listboxHeight / 24) - 1;
			pagingIRRCodeDialog.setPageSize(listRows);

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.aIRRCode);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.iRRCode.setMaxlength(8);
		this.iRRCodeDesc.setMaxlength(50);

		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IRRCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IRRCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IRRCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IRRCodeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.aIRRCode);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		iRRCodeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.aIRRCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param irrcode The entity that need to be passed to the dialog.
	 */
	public void onClick$button_IRRFeeTypeList_NewIRRFeeType(Event event) {
		logger.debug(Literal.ENTERING);

		String recordStatus = userAction.getSelectedItem().getValue();

		if (!StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_CANCELLED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_REJECTED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_RESUBMITTED)) {
			doSetValidation();
		}

		doWriteComponentsToBean(getaIRRCode());
		final IRRFeeType irrfeetype = new IRRFeeType();
		irrfeetype.setNewRecord(true);
		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("irrfeetype", irrfeetype);
		arg.put("iRRCodeDialogCtrl", this);
		arg.put("newRecord", true);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/IRRFeeType/IRRFeeTypeDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	// IRR FEE details rendering
	public void doFillIRRFeeTypeDetails(List<IRRFeeType> list) {
		logger.debug("Entering");

		this.listBoxIRRFeeType.getItems().clear();
		setIrrFeeTypesList(list);
		if (list != null && !list.isEmpty()) {
			for (IRRFeeType details : list) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(details.getFeeTypeCode());
				item.appendChild(lc);
				lc = new Listcell(details.getFeeTypeDesc());
				item.appendChild(lc);
				lc = new Listcell(details.getFeePercentage().toString());
				lc.setParent(item);
				lc = new Listcell(details.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(details.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", details);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onIRRFeeTypeItemDoubleClicked");
				this.listBoxIRRFeeType.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	// Double click IRR FEE Deatils list
	public void onIRRFeeTypeItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxIRRFeeType.getSelectedItem();
		if (item != null) {
			final IRRFeeType irrFeeType = (IRRFeeType) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(irrFeeType.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("irrfeetype", irrFeeType);
				map.put("iRRCodeDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("newRecord", false);
				map.put("isCompReadonly", isReadOnly("IRRCodeDialog_IRRCodeDesc"));
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
					map.put("enqModule", true);
				} else {
					map.put("enqModule", false);
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/ApplicationMaster/IRRFeeType/IRRFeeTypeDialog.zul",
							window_IRRCodeDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param iRRCode
	 * 
	 */
	public void doWriteBeanToComponents(IRRCode aIRRCode) {
		logger.debug(Literal.ENTERING);

		this.iRRCode.setValue(aIRRCode.getIRRCode());
		this.iRRCodeDesc.setValue(aIRRCode.getIRRCodeDesc());
		if (aIRRCode.isNewRecord()) {
			this.active.setChecked(true);
		} else {
			this.active.setChecked(aIRRCode.isActive());
		}
		this.recordStatus.setValue(aIRRCode.getRecordStatus());
		doFillIRRFeeTypeDetails(aIRRCode.getIrrFeeTypesList());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIRRCode
	 */
	public void doWriteComponentsToBean(IRRCode aIRRCode) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// IRR Code
		try {
			aIRRCode.setIRRCode(this.iRRCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// IRR Code Description
		try {
			aIRRCode.setIRRCodeDesc(this.iRRCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aIRRCode.setActive(this.active.isChecked());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param iRRCode The entity that need to be render.
	 */
	public void doShowDialog(IRRCode iRRCode) {
		logger.debug(Literal.LEAVING);

		if (iRRCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.iRRCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(iRRCode.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.iRRCodeDesc.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(iRRCode);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.iRRCode.isReadonly()) {
			this.iRRCode.setConstraint(new PTStringValidator(Labels.getLabel("label_IRRCodeDialog_IRRCode.value"),
					PennantRegularExpressions.REGEX_UPPERCASENAME, true));
		}
		if (!this.iRRCodeDesc.isReadonly()) {
			this.iRRCodeDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IRRCodeDialog_IRRCodeDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.iRRCode.setConstraint("");
		this.iRRCodeDesc.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final IRRCode aIRRCode = new IRRCode();
		BeanUtils.copyProperties(this.aIRRCode, aIRRCode);

		doDelete(aIRRCode.getIRRCode(), aIRRCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.aIRRCode.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("IRRCodeDialog_IRRCode"), this.iRRCode);
		} else {
			readOnlyComponent(true, this.iRRCode);
			this.btnCancel.setVisible(true);
		}

		if (StringUtils.equals(aIRRCode.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
			this.active.setDisabled(false);
		} else {
			this.active.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("IRRCodeDialog_IRRCodeDesc"), this.iRRCodeDesc);
		readOnlyComponent(isReadOnly("IRRCodeDialog_IRRCodeDesc"), this.button_IRRFeeTypeList_NewIRRFeeType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.aIRRCode.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
		readOnlyComponent(true, this.iRRCode);
		readOnlyComponent(true, this.iRRCodeDesc);
		readOnlyComponent(true, this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.iRRCode.setValue("");
		this.iRRCodeDesc.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final IRRCode aIRRCode = new IRRCode();
		BeanUtils.copyProperties(this.aIRRCode, aIRRCode);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aIRRCode);

		// Add the FeeType Detail list
		/*
		 * if(getIrrFeeTypesList() == null || getIrrFeeTypesList().isEmpty()){
		 * MessageUtil.showError(Labels.getLabel("label_IRRCode_Validation")); return; }
		 */
		aIRRCode.setIrrFeeTypesList(getIrrFeeTypesList());

		isNew = aIRRCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aIRRCode.getRecordType())) {
				aIRRCode.setVersion(aIRRCode.getVersion() + 1);
				if (isNew) {
					aIRRCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIRRCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIRRCode.setNewRecord(true);
				}
			}
		} else {
			aIRRCode.setVersion(aIRRCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aIRRCode, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(IRRCode aIRRCode, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aIRRCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aIRRCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIRRCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aIRRCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIRRCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aIRRCode);
				}

				if (isNotesMandatory(taskId, aIRRCode)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aIRRCode.setTaskId(taskId);
			aIRRCode.setNextTaskId(nextTaskId);
			aIRRCode.setRoleCode(getRole());
			aIRRCode.setNextRoleCode(nextRoleCode);

			// IRRCode details
			if (aIRRCode.getIrrFeeTypesList() != null && !aIRRCode.getIrrFeeTypesList().isEmpty()) {
				for (IRRFeeType details : aIRRCode.getIrrFeeTypesList()) {
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aIRRCode.getRecordStatus());
					details.setWorkflowId(aIRRCode.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aIRRCode.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aIRRCode.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(aIRRCode, tranType);
			String operationRefs = getServiceOperations(taskId, aIRRCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIRRCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIRRCode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		IRRCode aIRRCode = (IRRCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = iRRCodeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = iRRCodeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = iRRCodeService.doApprove(auditHeader);

					if (aIRRCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = iRRCodeService.doReject(auditHeader);
					if (aIRRCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_IRRCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_IRRCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.aIRRCode), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(IRRCode aIRRCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aIRRCode.getBefImage(), aIRRCode);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aIRRCode.getUserDetails(),
				getOverideMap());
	}

	public void setIRRCodeService(IRRCodeService iRRCodeService) {
		this.iRRCodeService = iRRCodeService;
	}

	public IRRCode getaIRRCode() {
		return aIRRCode;
	}

	public void setaIRRCode(IRRCode aIRRCode) {
		this.aIRRCode = aIRRCode;
	}

	public IRRCodeService getiRRCodeService() {
		return iRRCodeService;
	}

	public List<IRRFeeType> getIrrFeeTypesList() {
		return irrFeeTypesList;
	}

	public void setIrrFeeTypesList(List<IRRFeeType> irrFeeTypesList) {
		this.irrFeeTypesList = irrFeeTypesList;
	}

}
