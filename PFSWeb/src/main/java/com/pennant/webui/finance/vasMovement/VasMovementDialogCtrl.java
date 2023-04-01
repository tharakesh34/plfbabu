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
 * * FileName : VasMovementDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * *
 * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.vasMovement;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.VasMovement;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.service.applicationmaster.VasMovementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/VasMovement/vasMovementDialog.zul file.
 */
public class VasMovementDialogCtrl extends GFCBaseCtrl<VasMovement> {
	private static final Logger logger = LogManager.getLogger(VasMovementDialogCtrl.class);
	private static final long serialVersionUID = 3545862467364688600L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VasMovementDialog;
	protected Listbox listbox_VasMovementDetails;
	protected Paging pagingVasMvmtDetailsList;
	protected Grid grid_Basicdetails;
	protected Button btnNew_VasMovementDetail;

	// not auto wired variables
	private VasMovement vasMovement; // overHanded per parameter
	private transient VasMovementListCtrl vasMovementListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_RecordType;

	// Button controller for the CRUD buttons
	private transient boolean isEditable = false;

	protected Label lbl_LoanReference;
	protected Label lbl_LoanType;
	protected Label lbl_CustCIF;
	protected Label lbl_FinAmount;
	protected Label lbl_startDate;
	protected Label lbl_MaturityDate;

	// ServiceDAOs / Domain Classes
	private transient VasMovementService vasMovementService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<>();
	private List<VasMovementDetail> vasMovementDetailList = new ArrayList<VasMovementDetail>();
	private PagedListWrapper<VasMovementDetail> chkListDetailPagedListWrapper;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public VasMovementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VasMovementDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected VasMovement object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_VasMovementDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VasMovementDialog);

		try {
			// READ OVERHANDED parameters !
			if (arguments.containsKey("vasMovement")) {
				this.vasMovement = (VasMovement) arguments.get("vasMovement");
				VasMovement befImage = new VasMovement();
				BeanUtils.copyProperties(this.vasMovement, befImage);
				this.vasMovement.setBefImage(befImage);

				setVasMovement(this.vasMovement);
			} else {
				setVasMovement(null);
			}

			// READ OVERHANDED parameters !
			// we get the vasMovementListWindow controller. So we have access to it and can synchronize the shown data
			// when we do insert, edit or delete vasMovement here.
			if (arguments.containsKey("vasMovementListCtrl")) {
				setVasMovementListCtrl((VasMovementListCtrl) arguments.get("vasMovementListCtrl"));
			} else {
				setVasMovementListCtrl(null);
			}

			doLoadWorkFlow(this.vasMovement.isWorkflow(), this.vasMovement.getWorkflowId(),
					this.vasMovement.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "VasMovementDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			setChkListDetailPagedListWrapper();
			getChkListDetailPagedListWrapper();

			// Set the DialogController Height for listBox
			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 100 + 35;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listbox_VasMovementDetails.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			if (isWorkFlowEnabled()) {
				this.listheader_RecordStatus.setVisible(true);
				this.listheader_RecordType.setVisible(true);
			} else {
				this.listheader_RecordStatus.setVisible(false);
				this.listheader_RecordType.setVisible(true);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVasMovement());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VasMovementDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

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
		logger.debug("Entering");

		// getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());

		this.btnNew_VasMovementDetail
				.setVisible(getUserWorkspace().isAllowed("button_VasMovementDialog_btnNew_VasMovementDetail"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VasMovementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VasMovementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VasMovementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VasMovementDialog_btnSave"));
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
		btnNew_VasMovementDetail.setVisible(true);
		isEditable = true;
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
		MessageUtil.showHelpWindow(event, window_VasMovementDialog);
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
		logger.debug("Entering");
		isEditable = false;
		btnNew_VasMovementDetail.setVisible(false);
		doWriteBeanToComponents(this.vasMovement.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVasMovement VasMovement
	 */
	public void doWriteBeanToComponents(VasMovement aVasMovement) {
		logger.debug("Entering");
		this.recordStatus.setValue(aVasMovement.getRecordStatus());
		this.lbl_LoanReference.setValue(aVasMovement.getFinReference());
		this.lbl_CustCIF.setValue(String.valueOf(aVasMovement.getCustCif()));
		this.lbl_LoanType.setValue(aVasMovement.getFinType());
		this.lbl_FinAmount
				.setValue(CurrencyUtil.format(aVasMovement.getFinAmount(), PennantConstants.defaultCCYDecPos));
		this.lbl_startDate
				.setValue(DateUtil.format(aVasMovement.getFinStartdate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate
				.setValue(DateUtil.format(aVasMovement.getMaturityDate(), DateFormat.LONG_DATE.getPattern()));

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVasMovement
	 */
	public void doWriteComponentsToBean(VasMovement aVasMovement) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {

			boolean isDelete = false;
			if (this.userAction.getSelectedItem() != null) {
				if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| this.userAction.getSelectedItem().getLabel().contains("Reject")
						|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
					isDelete = true;
				}
			}
			if (!isDelete && this.listbox_VasMovementDetails.getItemCount() == 0) {
				throw new WrongValueException(this.btnNew_VasMovementDetail, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_VasMovementDialog_VasMovementDetail.title") }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// vasMovement.setVasMvntList(this.list);
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aVasMovement.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVasMovement
	 */
	public void doShowDialog(VasMovement aVasMovement) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aVasMovement.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aVasMovement.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnNew_VasMovementDetail.setVisible(false);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aVasMovement);

			doFillVasMovementDetailsList(getVasMovement().getVasMvntList());

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_VasMovementDialog.onClose();
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
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final VasMovement aVasMovement = new VasMovement();
		BeanUtils.copyProperties(getVasMovement(), aVasMovement);
		String keyReference = Labels.getLabel("listheader_VasMovementFinreference") + " : "
				+ aVasMovement.getFinReference();

		doDelete(keyReference, aVasMovement);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getVasMovement().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vasMovement.isNewRecord()) {
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

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VasMovement aVasMovement = new VasMovement();
		BeanUtils.copyProperties(getVasMovement(), aVasMovement);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the VasMovement object with the components data
		doWriteComponentsToBean(aVasMovement);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aVasMovement.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVasMovement.getRecordType())) {
				aVasMovement.setVersion(aVasMovement.getVersion() + 1);
				if (isNew) {
					aVasMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVasMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVasMovement.setNewRecord(true);
				}
			}
		} else {
			aVasMovement.setVersion(aVasMovement.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aVasMovement, tranType)) {
				doWriteBeanToComponents(aVasMovement);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aVasMovement (VasMovement)
	 * 
	 * @param tranType     (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(VasMovement aVasMovement, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVasMovement.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVasMovement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVasMovement.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVasMovement.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVasMovement.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVasMovement);
				}

				if (isNotesMandatory(taskId, aVasMovement)) {
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

			aVasMovement.setTaskId(taskId);
			aVasMovement.setNextTaskId(nextTaskId);
			aVasMovement.setRoleCode(getRole());
			aVasMovement.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aVasMovement, tranType);
			String operationRefs = getServiceOperations(taskId, aVasMovement);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVasMovement, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aVasMovement, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		VasMovement aVasMovement = (VasMovement) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getVasMovementService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getVasMovementService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getVasMovementService().doApprove(auditHeader);

					if (aVasMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getVasMovementService().doReject(auditHeader);
					if (aVasMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_VasMovementDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_VasMovementDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.vasMovement), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aVasMovement
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(VasMovement aVasMovement, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVasMovement.getBefImage(), aVasMovement);
		return new AuditHeader(String.valueOf(aVasMovement.getVasMovementId()), null, null, null, auditDetail,
				aVasMovement.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.vasMovement);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getVasMovementListCtrl().search();
	}

	/**
	 * when clicks on "btnNew_DetailsOfExpense"
	 * 
	 * @param event
	 */
	public void onClick$btnNew_VasMovementDetail(Event event) {
		logger.debug("Entering " + event.toString());
		VasMovementDetail vasMovementDetail = new VasMovementDetail();
		vasMovementDetail.setVasMovementDetailId(0);
		vasMovementDetail.setNewRecord(true);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vasMovementDialogCtrl", this);
		map.put("vasMovement", getVasMovement());
		map.put("vasMovementDetail", vasMovementDetail);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/VasMovement/VasMovementDetailDialog.zul", null, map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 * @param event
	 */
	public void onVasMovementDetailItemDoubleClicked(Event event) {
		logger.debug("Entering " + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listbox_VasMovementDetails.getSelectedItem();

		if (item != null) {
			final VasMovementDetail vasMovementDetail = (VasMovementDetail) item.getAttribute("data");

			if (vasMovementDetail.getRecordType() != null && (vasMovementDetail.getRecordType()
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)))) {
				MessageUtil.showError(Labels.getLabel("RECORD_NO_MAINTAIN"));
			} else {
				vasMovementDetail.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("vasMovementDialogCtrl", this);
				map.put("vasMovement", getVasMovement());
				map.put("vasMovementDetail", vasMovementDetail);
				map.put("roleCode", getRole());
				map.put("isEditable", isEditable);
				map.put("isNewRecord", getVasMovement().isNewRecord());
				map.put("isAccessRights",
						getUserWorkspace().isAllowed("button_VasMovementDialog_btnNew_VasMovementDetail"));

				try {
					Executions.createComponents("/WEB-INF/pages/Finance/VasMovement/VasMovementDetailDialog.zul", null,
							map);

				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method fills expense details list
	 * 
	 * @param expenseDetails
	 */
	public void doFillVasMovementDetailsList(List<VasMovementDetail> vasMovementDetailList) {
		logger.debug("Entering ");
		Comparator<Object> comp = new BeanComparator<Object>("vasMovementDetailId");
		Collections.sort(vasMovementDetailList, comp);
		// FIXME should checked better to remove the paging
		this.pagingVasMvmtDetailsList.setPageSize(100);
		this.setVasMovementDetailList(vasMovementDetailList);
		getVasMovement().setVasMvntList(vasMovementDetailList);
		getChkListDetailPagedListWrapper().initList(vasMovementDetailList, this.listbox_VasMovementDetails,
				pagingVasMvmtDetailsList);
		this.listbox_VasMovementDetails
				.setModel(new GroupsModelArray(vasMovementDetailList.toArray(), new VasMovementComparator()));
		this.listbox_VasMovementDetails.setItemRenderer(new VasMovementDetailListModelItemRenderer());
		if (this.listbox_VasMovementDetails.getVisibleItemCount() != 0) {
			Clients.clearWrongValue(this.btnNew_VasMovementDetail);
		}

		int vasMovementCnt = 0;

		for (VasMovementDetail checkDetail : vasMovementDetailList) {
			if (StringUtils.isBlank(checkDetail.getRecordType())
					|| !(checkDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)
							|| checkDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL))) {
				vasMovementCnt = vasMovementCnt + 1;
			}
		}
		logger.debug("Leaving ");

	}

	public class VasMovementComparator implements Comparator<Object>, Serializable {

		private static final long serialVersionUID = 9112640872865877333L;

		public VasMovementComparator() {

		}

		@Override
		public int compare(Object o1, Object o2) {
			VasMovementDetail data = (VasMovementDetail) o1;
			VasMovementDetail data2 = (VasMovementDetail) o2;
			return String.valueOf(data.getVasReference()).compareTo(String.valueOf(data2.getVasReference()));

		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.vasMovement.getVasMovementId());
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

	public VasMovement getVasMovement() {
		return this.vasMovement;
	}

	public void setVasMovement(VasMovement vasMovement) {
		this.vasMovement = vasMovement;
	}

	public void setVasMovementService(VasMovementService vasMovementService) {
		this.vasMovementService = vasMovementService;
	}

	public VasMovementService getVasMovementService() {
		return this.vasMovementService;
	}

	public void setVasMovementListCtrl(VasMovementListCtrl vasMovementListCtrl) {
		this.vasMovementListCtrl = vasMovementListCtrl;
	}

	public VasMovementListCtrl getVasMovementListCtrl() {
		return this.vasMovementListCtrl;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	@SuppressWarnings("unchecked")
	public void setChkListDetailPagedListWrapper() {
		if (this.chkListDetailPagedListWrapper == null) {
			this.chkListDetailPagedListWrapper = (PagedListWrapper<VasMovementDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<VasMovementDetail> getChkListDetailPagedListWrapper() {
		return chkListDetailPagedListWrapper;
	}

	public List<VasMovementDetail> getVasMovementDetailList() {
		return vasMovementDetailList;
	}

	public void setVasMovementDetailList(List<VasMovementDetail> vasMovementDetailList) {
		this.vasMovementDetailList = vasMovementDetailList;
	}
}
