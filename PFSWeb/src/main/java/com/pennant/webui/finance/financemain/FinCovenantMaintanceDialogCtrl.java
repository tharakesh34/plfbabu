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
 * * FileName : FinCovenantMaintanceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 *
 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinCovenantMaintanceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinCovenantMaintanceDialog.zul file.
 */
public class FinCovenantMaintanceDialogCtrl extends GFCBaseCtrl<FinMaintainInstruction> {

	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(FinCovenantMaintanceDialogCtrl.class);

	protected Window window_finCovenantMaintanceDialog;
	protected Listbox listBoxFinCovenantType;
	protected Button btnNew_NewFinCovenantType;

	protected Groupbox finBasicdetails;

	private FinanceDetail financeDetail;
	private FinanceMain financeMain;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	private FinMaintainInstruction finMaintainInstruction;
	private transient FinCovenantMaintanceService finCovenantMaintanceService;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	private Object financeMainDialogCtrl;

	private List<FinCovenantType> finCovenantTypesDetailList = new ArrayList<FinCovenantType>();

	// private transient boolean recSave = false;
	private boolean isEnquiry = false;
	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;

	/**
	 * default constructor.<br>
	 */
	public FinCovenantMaintanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCovenantMaintanceDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_finCovenantMaintanceDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_finCovenantMaintanceDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
				this.financeMainDialogCtrl = (Object) arguments.get("financeSelectCtrl");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				this.financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("finMaintainInstruction")) {
				setFinMaintainInstruction((FinMaintainInstruction) arguments.get("finMaintainInstruction"));
				this.finMaintainInstruction = getFinMaintainInstruction();
			}

			if (this.finMaintainInstruction == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			BeanUtils.copyProperties(this.finMaintainInstruction, finMaintainInstruction);
			this.finMaintainInstruction.setBefImage(finMaintainInstruction);

			// Render the page and display the data.
			doLoadWorkFlow(this.finMaintainInstruction.isWorkflow(), this.finMaintainInstruction.getWorkflowId(),
					this.finMaintainInstruction.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else {
				this.south.setHeight("0px");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finMaintainInstruction);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole(), menuItemRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinCovenantMaintanceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinCovenantMaintanceDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinCovenantMaintanceDialog_btnSave"));
		this.btnCancel.setVisible(false);

		this.btnNew_NewFinCovenantType
				.setVisible(getUserWorkspace().isAllowed("btnNew_FinCovenantMaintanceDialog_NewFinCovenantType"));

		// Schedule related buttons
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
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
		logger.debug("Entering");

		doWriteBeanToComponents(this.finMaintainInstruction.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(FinMaintainInstruction finMaintainInstruction) {
		logger.debug("Entering");

		doFillFinCovenantTypeDetails(finMaintainInstruction.getFinCovenantTypeList());
		if (CollectionUtils.isNotEmpty(finMaintainInstruction.getFinCovenantTypeList())) {
			for (FinCovenantType covenantType : finMaintainInstruction.getFinCovenantTypeList()) {
				FinCovenantType befImage = new FinCovenantType();
				BeanUtils.copyProperties(covenantType, befImage);
				covenantType.setBefImage(befImage);
				this.recordStatus.setValue(covenantType.getRecordStatus());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinMaintainInstruction
	 */
	public void doWriteComponentsToBean(FinMaintainInstruction finMaintainInstruction) {
		logger.debug("Entering");

		finMaintainInstruction.setFinID(this.financeMain.getFinID());
		finMaintainInstruction.setFinReference(this.financeMain.getFinReference());
		finMaintainInstruction.setEvent(this.moduleDefiner);

		// List
		finMaintainInstruction.setFinCovenantTypeList(getFinCovenantTypesDetailList());

		finMaintainInstruction.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aFinMaintainInstruction The entity that need to be render.
	 */
	public void doShowDialog(FinMaintainInstruction finMaintainInstruction) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (finMaintainInstruction.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finMaintainInstruction.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		this.listBoxFinCovenantType.setHeight(borderLayoutHeight - 165 + "px");

		appendFinBasicDetails(this.financeMain);

		// fill the components with the data
		doWriteBeanToComponents(finMaintainInstruction);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finMaintainInstruction.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
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
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug("Entering");

		FinMaintainInstruction aFinMaintainInstruction = new FinMaintainInstruction();
		aFinMaintainInstruction = ObjectUtil.clone(getFinMaintainInstruction());

		doWriteComponentsToBean(aFinMaintainInstruction);

		boolean isNew;
		isNew = aFinMaintainInstruction.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinMaintainInstruction.getRecordType())) {
				aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
				if (isNew) {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinMaintainInstruction.setNewRecord(true);
				}
			}
		} else {
			aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFinMaintainInstruction, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aFinMaintainInstruction.getRoleCode(),
						aFinMaintainInstruction.getNextRoleCode(), aFinMaintainInstruction.getFinReference() + "",
						" Covenant Details ", aFinMaintainInstruction.getRecordStatus());
				if (StringUtils.equals(aFinMaintainInstruction.getRecordStatus(),
						PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Covenant Detail with Reference " + aFinMaintainInstruction.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

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
	 * @param aFinMaintainInstruction (FinMaintainInstruction)
	 * 
	 * @param tranType                (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFinMaintainInstruction.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinMaintainInstruction.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinMaintainInstruction.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinMaintainInstruction.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinMaintainInstruction);
				}

				if (isNotesMandatory(taskId, aFinMaintainInstruction)) {
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

			aFinMaintainInstruction.setTaskId(taskId);
			aFinMaintainInstruction.setNextTaskId(nextTaskId);
			aFinMaintainInstruction.setRoleCode(getRole());
			aFinMaintainInstruction.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			String operationRefs = getServiceOperations(taskId, aFinMaintainInstruction);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinMaintainInstruction, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
		AuditHeader aAuditHeader = auditHeader;
		FinMaintainInstruction aFinMaintainInstruction = (FinMaintainInstruction) aAuditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = finCovenantMaintanceService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = finCovenantMaintanceService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = finCovenantMaintanceService.doApprove(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = finCovenantMaintanceService.doReject(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_finCovenantMaintanceDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_finCovenantMaintanceDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finMaintainInstruction), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinMaintainInstruction
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinMaintainInstruction.getBefImage(),
				aFinMaintainInstruction);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinMaintainInstruction.getUserDetails(),
				getOverideMap());
	}

	public void doFillFinCovenantTypeDetails(List<FinCovenantType> finCovenantTypeList) {
		logger.debug("Entering");

		this.listBoxFinCovenantType.getItems().clear();
		setFinCovenantTypesDetailList(finCovenantTypeList);

		if (finCovenantTypeList != null && !finCovenantTypeList.isEmpty()) {
			for (FinCovenantType detail : finCovenantTypeList) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getCovenantTypeDesc());
				lc.setParent(item);
				lc = new Listcell(detail.getMandRoleDesc());
				lc.setParent(item);
				Checkbox cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwWaiver());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwPostpone());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwOtc());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick = onFinCovenantTypeItemDoubleClicked");
				this.listBoxFinCovenantType.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	public void onClick$btnNew_NewFinCovenantType(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		final FinCovenantType aFinCovenantType = new FinCovenantType();
		aFinCovenantType.setFinReference(getFinanceDetail().getFinScheduleData().getFinReference());
		aFinCovenantType.setNewRecord(true);
		aFinCovenantType.setWorkflowId(0);

		Map<String, Object> map = getDefaultArguments();
		map.put("finCovenantTypes", aFinCovenantType);
		map.put("newRecord", "true");
		map.put("moduleDefiner", moduleDefiner);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinCovenantTypeItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		Listitem listitem = this.listBoxFinCovenantType.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinCovenantType aFinCovenantType = (FinCovenantType) listitem.getAttribute("data");
			if (isDeleteRecord(aFinCovenantType.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				if (validateDocumentExistance(aFinCovenantType.getCovenantType())) {
					MessageUtil.showError(Labels.getLabel("document_AlreadyCaptured"));
					return;
				}
				aFinCovenantType.setNewRecord(false);

				Map<String, Object> map = getDefaultArguments();
				map.put("finCovenantTypes", aFinCovenantType);

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param dcoType
	 */
	private boolean validateDocumentExistance(String docTypeCode) {
		logger.debug("Entering");
		// validate the document selected exists with the customer/Finance
		if (getFinanceDetail().getCustomerDetails() != null
				&& !getFinanceDetail().getCustomerDetails().getCustomerDocumentsList().isEmpty()) {

			for (CustomerDocument custdocument : getFinanceDetail().getCustomerDetails().getCustomerDocumentsList()) {
				if (custdocument.getCustDocCategory().equals(docTypeCode)) {
					return true;
				}
			}

		}

		if (getFinanceDetail().getDocumentDetailsList() != null
				&& !getFinanceDetail().getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails documentdetail : getFinanceDetail().getDocumentDetailsList()) {
				if (StringUtils.equals(documentdetail.getDocCategory(), docTypeCode)) {
					return true;
				}
			}

		}

		logger.debug("Leaving");
		return false;
	}

	public Map<String, Object> getDefaultArguments() {

		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeMain.getFinCcy()));
		map.put("finCovenantMaintanceDialogCtrl", this);
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
		map.put("moduleDefiner", moduleDefiner);
		map.put("enqModule", isEnquiry);
		map.put("roleCode", getRole());
		map.put("allowedRoles",
				StringUtils.join(getWorkFlow().getActors(false), ';').replace(getRole().concat(";"), ""));
		map.put("financeDetail", getFinanceDetail());

		return map;
	}

	/**
	 * 
	 * @param rcdType
	 * @return
	 */
	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finMaintainInstruction);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(FinanceMain aFinanceMain) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", getHeaderBasicDetails(this.financeMain));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails(FinanceMain aFinanceMain) {

		ArrayList<Object> arrayList = new ArrayList<Object>();
		Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
		arrayList.add(0, aFinanceMain.getFinType());
		arrayList.add(1, aFinanceMain.getFinCcy());
		arrayList.add(2, aFinanceMain.getScheduleMethod());
		arrayList.add(3, aFinanceMain.getFinReference());
		arrayList.add(4, aFinanceMain.getProfitDaysBasis());
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, customer == null ? "" : customer.getCustShrtName());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finMaintainInstruction.getFinMaintainId());
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinMaintainInstruction getFinMaintainInstruction() {
		return finMaintainInstruction;
	}

	public void setFinMaintainInstruction(FinMaintainInstruction finMaintainInstruction) {
		this.finMaintainInstruction = finMaintainInstruction;
	}

	public List<FinCovenantType> getFinCovenantTypesDetailList() {
		return finCovenantTypesDetailList;
	}

	public void setFinCovenantTypesDetailList(List<FinCovenantType> finCovenantTypesDetailList) {
		this.finCovenantTypesDetailList = finCovenantTypesDetailList;
	}

	public FinCovenantMaintanceService getFinCovenantMaintanceService() {
		return finCovenantMaintanceService;
	}

	public void setFinCovenantMaintanceService(FinCovenantMaintanceService finCovenantMaintanceService) {
		this.finCovenantMaintanceService = finCovenantMaintanceService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
}
