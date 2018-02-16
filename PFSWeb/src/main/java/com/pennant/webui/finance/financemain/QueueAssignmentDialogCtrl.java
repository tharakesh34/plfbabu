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
 * FileName    		:QueueAssignmentRoleDailogCtrl.java                                        *
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            *
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
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.QueueAssignmentHeader;
import com.pennant.backend.model.administration.SecurityUserOperationRoles;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.QueueAssignmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/AssignmentDialog.zul file.
 */
public class QueueAssignmentDialogCtrl extends GFCBaseCtrl<QueueAssignment> {
	private static final long serialVersionUID = 4149506032336052235L;
	private static final Logger logger = Logger.getLogger(QueueAssignmentDialogCtrl.class);
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QueueAssignmentDialog; // autoWired
	protected Label window_AssignmentTitle;
	protected Borderlayout borderLayout_QueueAssignmentDialog; // autoWired
	protected Listbox listbox_AssignmentRecords; // autoWired
	protected Listheader listheader_ActualOwner;
	protected Uppercasebox finReference; // autoWired
	protected Uppercasebox custCIF; // autoWired
	protected ExtendedCombobox fromUser;
	protected Label userRole;
	protected ExtendedCombobox toUser;
	protected Listbox sortOperator_finReference; // autowired
	protected Listbox sortOperator_CustomerCIF; // autowired

	protected Button btn_Search; // autoWired
	protected Button btn_Refresh; // autoWired

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;
	
	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient boolean validationOn;

	// private variables and service classes
	private transient QueueAssignmentService queueAssignmentService;
	private QueueAssignment queueAssignment;
	private QueueAssignmentHeader queueAssignmentHeader;

	private PagedListWrapper<QueueAssignment> assigneListWrapper;
	private PagedListWrapper<QueueAssignment> unAssigneListWrapper;
	private transient QueueAssignmentListCtrl assignmentListCtrl;
	private transient String roleCode;
	private transient String[] references;
	private transient int assignedCount = 0;
	private transient boolean rcdAdded = false;

	/**
	 * default constructor.<br>
	 */
	public QueueAssignmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssignmentDialog";
	}

	// Components events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected QueueAssignments
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_QueueAssignmentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_QueueAssignmentDialog);

		try {
			/* set components visible dependent of the users rights */

			// READ OVERHANDED params !
			// get the parameters map that are over handed by creation.
			if (arguments.containsKey("assignmentListCtrl")) {
				setAssignmentListCtrl((QueueAssignmentListCtrl) arguments.get("assignmentListCtrl"));
			} else {
				setAssignmentListCtrl(null);
			}

			// get the parameters map that are over handed by creation.
			if (arguments.containsKey("aQueueAssignmentHeader")) {
				setQueueAssignmentHeader((QueueAssignmentHeader) arguments.get("aQueueAssignmentHeader"));
			} else {
				setQueueAssignmentHeader(null);
			}

			doLoadWorkFlow(this.queueAssignmentHeader.isWorkflow(), this.queueAssignmentHeader.getWorkflowId(),
					this.queueAssignmentHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "AssignmentDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog();
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_QueueAssignmentDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.fromUser.setMaxlength(5);
		this.fromUser.setMandatoryStyle(true);
		this.fromUser.setModuleName("SecurityUser");
		this.fromUser.setValueColumn("UsrID");
		this.fromUser.setDescColumn("UsrFName");
		this.fromUser.setValidateColumns(new String[] { "UsrID" });

		this.toUser.setMaxlength(5);
		this.toUser.setMandatoryStyle(true);
		this.toUser.setModuleName("SecurityUserOperationRoles");
		this.toUser.setValueColumn("UsrID");
		this.toUser.setDescColumn("LovDescFirstName");
		Filter[] filter = new Filter[2];
		filter[0] = Filter.notEqual("UsrID", getQueueAssignmentHeader().getFromUserId());
		filter[1] = Filter.equalTo("RoleCd", getQueueAssignmentHeader().getUserRoleCode());
		this.toUser.setFilters(filter);
		this.toUser.setValidateColumns(new String[] { "UsrID" });

		this.sortOperator_CustomerCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_CustomerCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		logger.debug("Leaving");
	}

	/**
	 * when "save" button is clicked
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());

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
	 * When user clicks on "cancel" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnCancel(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, this.window_QueueAssignmentDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
		refreshList();
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering ");

		if (queueAssignmentHeader.isManualAssign()) {
			this.window_AssignmentTitle.setValue(Labels.getLabel("window_ManualAssignment.title"));
		} else {
			this.window_AssignmentTitle.setValue(Labels.getLabel("window_Reassignment.title"));
		}
		doSetPanelProperties();

		displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), getQueueAssignmentHeader().isNewRecord()));

		doWriteBeanToComponents(getQueueAssignmentHeader());
		getBorderLayoutHeight();
		this.listbox_AssignmentRecords.setHeight(this.borderLayoutHeight - 250 + "px");
		logger.debug("Leaving ");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	private void displayComponents(int mode) {
		logger.debug("Entering");
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.listbox_AssignmentRecords, this.listbox_AssignmentRecords));

		logger.debug("Leaving");
	}

	public void doReadOnly(boolean readOnly) {

		boolean tempReadOnly = readOnly;
		if (readOnly){
			tempReadOnly = true;
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(this.queueAssignment.getRecordType())) {
			tempReadOnly = true;
		}
		setExtAccess("AssignmentDialog_FromUser", true, this.fromUser, null);
		setExtAccess("AssignmentDialog_ToUser", tempReadOnly, this.toUser, null);
	}

	/**
	 * This method sets panel properties
	 */
	public void doSetPanelProperties() {
		logger.debug("Entering ");
		logger.debug("Leaving ");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {

		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssignmentDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_AssignmentDialog_btnCancel"));

		logger.debug("Leaving ");
	}

	public void doWriteBeanToComponents(QueueAssignmentHeader aQueueAssignmentHeader) {
		logger.debug("Entering ");
		// set the paging parameters
		this.listbox_AssignmentRecords.getItems().clear();
		this.fromUser.setValue(String.valueOf(aQueueAssignmentHeader.getFromUserId()));
		this.fromUser.setDescription(aQueueAssignmentHeader.getLovDescUserName());
		this.userRole.setValue(aQueueAssignmentHeader.getRoleDesc());

		fillListBoxWithData(new ArrayList<QueueAssignment>(aQueueAssignmentHeader.getQueueAssignmentsList()),
				this.listbox_AssignmentRecords);

		this.recordStatus.setValue(aQueueAssignmentHeader.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * This method do the following 1)compare oldAssigned map and new assigned
	 * map a)if roleId not in oldselectedMap and in new selectedMap creates new
	 * QueueAssignment Object, sets data and add it to QueueAssignmentr
	 * LovDescAssignedRoles b)if roleId in oldselectedMap and not in new
	 * selectedMap gets the QueueAssignment from back end , sets RecordStatus
	 * "DELETE" add it to SecurityUser LovDescAssignedRoles
	 */
	public void doWriteComponentsToBean(QueueAssignmentHeader queueAssignmentHeader) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.fromUser.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// this.toUser.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (assignedCount == 0 && !"Cancel".equals(userAction.getSelectedItem().getLabel())) {
			throw new WrongValueException(this.listbox_AssignmentRecords, Labels.getLabel("REASSIGNMENT_EMPTY"));
		}

		List<QueueAssignment> queueList = new ArrayList<QueueAssignment>();
		for (int i = 0; i < this.listbox_AssignmentRecords.getItemCount(); i++) {
			Listitem item = this.listbox_AssignmentRecords.getItems().get(i);
			QueueAssignment queue = (QueueAssignment) item.getAttribute("data");
			queue.setUserRoleCode(getQueueAssignmentHeader().getUserRoleCode());
			queue.setManualAssign(getQueueAssignmentHeader().isManualAssign());
			if (getQueueAssignmentHeader().isManualAssign()) {
				queue.setFromUserId(Long.valueOf(queue.getUserId()));
				if (queueAssignmentHeader.getUserId() == null) {
					queueAssignmentHeader.setUserId("0");
				}
			} else {
				queue.setFromUserId(Long.valueOf(queueAssignmentHeader.getUserId() == null ? "0"
						: queueAssignmentHeader.getUserId()));
			}

			if (queue.getUserId() != 0) {
				queue.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
			}

			if (!"".equals(StringUtils.trimToEmpty(queue.getModule()))) {
				queueList.add(queue);
			}
		}
		queueAssignmentHeader.setQueueAssignmentsList(queueList);
		queueAssignmentHeader.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		queueAssignmentHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fromUser.setErrorMessage("");
		this.toUser.setErrorMessage("");
		Clients.clearWrongValue(this.listbox_AssignmentRecords);
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a AssignmentDialog object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final QueueAssignmentHeader aQueueAssignmentHeader = new QueueAssignmentHeader();
		BeanUtils.copyProperties(getQueueAssignmentHeader(), aQueueAssignmentHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aQueueAssignmentHeader.getRecordType())) {
				aQueueAssignmentHeader.setVersion(aQueueAssignmentHeader.getVersion() + 1);
				aQueueAssignmentHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aQueueAssignmentHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aQueueAssignmentHeader, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final QueueAssignmentHeader aQueueAssignmentHeader = new QueueAssignmentHeader();
		BeanUtils.copyProperties(getQueueAssignmentHeader(), aQueueAssignmentHeader);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aQueueAssignmentHeader.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Branch object with the components data
			doWriteComponentsToBean(aQueueAssignmentHeader);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aQueueAssignmentHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aQueueAssignmentHeader.getRecordType())) {
				aQueueAssignmentHeader.setVersion(aQueueAssignmentHeader.getVersion() + 1);
				if (isNew) {
					aQueueAssignmentHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aQueueAssignmentHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aQueueAssignmentHeader.setNewRecord(true);
				}
			}
		} else {
			aQueueAssignmentHeader.setVersion(aQueueAssignmentHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aQueueAssignmentHeader, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		logger.debug("Leaving ");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		getAssignmentListCtrl().findSearchObject();
		if (getAssignmentListCtrl().listBoxQueueAssignment != null) {
			getAssignmentListCtrl().listBoxQueueAssignment.getListModel();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aQueueAssignmentHeader
	 *            (QueueAssignment)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(QueueAssignmentHeader aQueueAssignmentHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aQueueAssignmentHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aQueueAssignmentHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aQueueAssignmentHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aQueueAssignmentHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aQueueAssignmentHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aQueueAssignmentHeader);
				}

				if (isNotesMandatory(taskId, aQueueAssignmentHeader)) {
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

			aQueueAssignmentHeader.setTaskId(taskId);
			aQueueAssignmentHeader.setNextTaskId(nextTaskId);
			aQueueAssignmentHeader.setRoleCode(getRole());
			aQueueAssignmentHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aQueueAssignmentHeader, tranType);

			String operationRefs = getServiceOperations(taskId, aQueueAssignmentHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aQueueAssignmentHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aQueueAssignmentHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("return value :" + processCompleted);
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
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		QueueAssignmentHeader aQueueAssignmentHeader = (QueueAssignmentHeader) auditHeader.getAuditDetail()
				.getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getQueueAssignmentService().saveOrUpdate(auditHeader);
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getQueueAssignmentService().doApprove(auditHeader);

						if (aQueueAssignmentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getQueueAssignmentService().doReject(auditHeader);
						if (aQueueAssignmentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_QueueAssignmentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_QueueAssignmentDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.queueAssignmentHeader), true);
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
		return processCompleted;
	}

	/**
	 * Closes the dialog window. <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void doClose() throws InterruptedException {

		logger.debug("Entering ");
		boolean close = true;
		// before close check whether data changed.
		if (isDataChanged()) {
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doSave();
				close = false;
			}
		}
		if (close) {
			refreshList();
			closeDialog(); // QueueAssignment
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method cancels the previous operations
	 * 
	 * @throws Exception
	 */
	private void doCancel() throws Exception {
		/*
		 * tempUnAsgnRoleMap.clear(); newAssignedMap.clear();
		 * unAssignedRoleList=tempUnAssignedRoleList; doShowDialog();
		 */
	}

	// OnClick Events
	public List<QueueAssignment> filterRecords(int finFilterCode, String finValue, int custFilterCode, String custValue) {
		logger.debug("Entering ");
		ArrayList<QueueAssignment> searchList = new ArrayList<QueueAssignment>();
		List<QueueAssignment> dataList = getQueueAssignmentHeader().getQueueAssignmentsList();
		if ("".equals(finValue) && "".equals(custValue)) {
			return dataList;
		}
		for (QueueAssignment queueAssignment : dataList) {

			if (returnList(finFilterCode, finValue, queueAssignment.getReference())
					&& !foundinList(searchList, queueAssignment)) {
				searchList.add(queueAssignment);
			}
			if (returnList(custFilterCode, custValue, queueAssignment.getLovDescCustCIF())
					&& !foundinList(searchList, queueAssignment)) {
				searchList.add(queueAssignment);
			}
		}
		logger.debug("Leaving ");
		return searchList;
	}

	public boolean foundinList(ArrayList<QueueAssignment> searchList, QueueAssignment queueAssignmenttocheck) {

		if (queueAssignmenttocheck != null) {
			for (QueueAssignment queueAssignment : searchList) {
				if (queueAssignment.getReference().equals(queueAssignmenttocheck.getReference())) {
					return true;
				}
			}
		}
		return false;

	}

	public boolean returnList(int filterCode, String filterValue, String vompareVale) {

		switch (filterCode) {
		case Filter.OP_EQUAL:
			if (vompareVale.equals(filterValue)) {
				return true;
			}
			break;
		case Filter.OP_NOT_EQUAL:
			if (!vompareVale.equals(filterValue)) {
				return true;
			}
			break;
		case Filter.OP_LIKE:
			if (vompareVale.contains(filterValue)) {
				return true;
			}
			break;
		}

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.fromUser.isReadonly()) {
			this.fromUser.setConstraint(new PTStringValidator(Labels
					.getLabel("label_QueueAssignmentDialog_FromUser.value"), null, true, true));
		}

		if (!this.toUser.isReadonly()) {
			this.toUser.setConstraint(new PTStringValidator(
					Labels.getLabel("label_QueueAssignmentDialog_ToUser.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.fromUser.setConstraint("");
		this.toUser.setConstraint("");
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * This method creates and returns AuditHeader Object
	 * 
	 * @param aQueueAssignmentHeader
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(QueueAssignmentHeader aQueueAssignmentHeader, String tranType) {
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aQueueAssignmentHeader.getBefImage(),
				aQueueAssignmentHeader);

		AuditHeader auditHeader = new AuditHeader(aQueueAssignmentHeader.getReference(), null, null, null, auditDetail,
				aQueueAssignmentHeader.getUserDetails(), getOverideMap());
		auditHeader.setAuditModule(QueueAssignment.class.getSimpleName());
		return auditHeader;
	}

	/**
	 * This method shows error message
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_QueueAssignmentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Entering ");
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
		doShowNotes(this.queueAssignmentHeader);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.queueAssignmentHeader.getModule());
	}


	private void doFillToUsers(String toUser, String desc) {
		logger.debug("Entering");
		this.listbox_AssignmentRecords.getItems().clear();
		for (int i = 0; i < getQueueAssignmentHeader().getQueueAssignmentsList().size(); i++) {
			getQueueAssignmentHeader().getQueueAssignmentsList().get(i)
					.setUserId("".equals(toUser) ? 0 : Long.parseLong(toUser));
			getQueueAssignmentHeader().getQueueAssignmentsList().get(i).setLovDescUserName(desc);
		}
		fillListBoxWithData(getQueueAssignmentHeader().getQueueAssignmentsList(), this.listbox_AssignmentRecords);
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public QueueAssignmentService getQueueAssignmentService() {
		return queueAssignmentService;
	}

	public void setQueueAssignmentService(QueueAssignmentService queueAssignmentService) {
		this.queueAssignmentService = queueAssignmentService;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<QueueAssignment> getAssigneListWrapper() {
		logger.debug("Entering");
		if (this.assigneListWrapper == null) {
			this.assigneListWrapper = (PagedListWrapper<QueueAssignment>) SpringUtil.getBean("pagedListWrapper");
			;
		}
		logger.debug("Leaving");
		return assigneListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<QueueAssignment> getUnAssigneListWrapper() {
		logger.debug("Entering");
		if (this.unAssigneListWrapper == null) {
			this.unAssigneListWrapper = (PagedListWrapper<QueueAssignment>) SpringUtil.getBean("pagedListWrapper");
		}
		logger.debug("Leaving");
		return unAssigneListWrapper;
	}

	public QueueAssignmentListCtrl getAssignmentListCtrl() {
		return assignmentListCtrl;
	}

	public void setAssignmentListCtrl(QueueAssignmentListCtrl assignmentListCtrl) {
		this.assignmentListCtrl = assignmentListCtrl;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	private void fillListBoxWithData(List<QueueAssignment> queueList, Listbox listbox) {
		logger.debug("Entering");
		references = new String[queueList.size()];
		boolean sameUser = false;
		long toSingleUser = 0;
		String toUserName = "";
		QueueAssignment prvQueue = null;
		listbox.getItems().clear();
		if (!queueList.isEmpty()) {
			for (int i = 0; i < queueList.size(); i++) {
				QueueAssignment queue = queueList.get(i);

				if ("".equals(StringUtils.trimToEmpty(queue.getModule()))) {
					queue.setNewRecord(true);
					queue.setVersion(1);
					queue.setRecordType(PennantConstants.RCD_ADD);
				} else {
					if (queue.getUserId() == 0) {
						queue.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					} else {
						rcdAdded = true;
						assignedCount = assignedCount + 1;
					}
				}

				if (i == 0) {
					prvQueue = queue;
					sameUser = false;
					toSingleUser = queue.getUserId();
				} else {
					prvQueue = queueList.get(i - 1);
					if (queue.getUserId() != 0 && prvQueue.getUserId() != 0
							&& queue.getUserId() == prvQueue.getUserId()) {
						sameUser = true;
						toSingleUser = queue.getUserId();
						toUserName = queue.getLovDescUserName();
					} else {
						sameUser = false;
					}
				}

				if (queueList.size() == 1) {
					if (StringUtils.equals(String.valueOf(queue.getUserId()), toUser.getValue())) {
						sameUser = true;
					}
				}

				Listitem item = new Listitem();
				Listcell listCell;
				listCell = new Listcell(queue.getReference() + " - " + queue.getLovDescFinType() + " - "
						+ queue.getLovDescFinTypeDesc());
				references[i] = queue.getReference();
				listCell.setParent(item);

				listCell = new Listcell(queue.getLovDescCustCIF());
				listCell.setParent(item);

				listCell = new Listcell(PennantAppUtil.amountFormate(queue.getLovDescFinAmount(),
						queue.getLovDescEditField()));
				listCell.setStyle("text-align:right;");
				listCell.setParent(item);

				listCell = new Listcell(queue.getLovDescActualOwner());
				listCell.setParent(item);

				ExtendedCombobox toUser = new ExtendedCombobox();
				toUser.setMaxlength(5);
				toUser.setMandatoryStyle(false);

				this.toUser.setModuleName("SecurityUserOperationRoles");
				this.toUser.setValueColumn("UsrID");
				this.toUser.setDescColumn("LovDescFirstName");
				Filter[] filter = new Filter[2];
				filter[0] = Filter.notEqual("UsrID", getQueueAssignmentHeader().getFromUserId());
				filter[1] = Filter.equalTo("RoleCd", getQueueAssignmentHeader().getUserRoleCode());
				toUser.setFilters(filter);
				toUser.setValidateColumns(new String[] { "UsrID" });
				toUser.addForward("onFulfill", window_QueueAssignmentDialog, "onChangeToUser", queue);

				if (queue.getUserId() != 0) {
					toUser.setValue(String.valueOf(queue.getUserId()));
					toUser.setDescription(queue.getLovDescUserName());
				}
				setExtAccess("AssignmentDialog_ToUser", false, toUser, null);

				listCell = new Listcell();
				listCell.appendChild(toUser);
				listCell.setParent(item);

				listCell = new Listcell(queue.getRecordStatus());
				listCell.setParent(item);

				item.setAttribute("data", queue);
				listbox.appendChild(item);
			}
			if (getQueueAssignmentHeader().isManualAssign()) {
				listheader_ActualOwner.setVisible(false);
			}

			if (sameUser) {
				rcdAdded = false;
				getQueueAssignmentHeader().setSingleUser(sameUser);
				this.toUser.setValue(toSingleUser == 0 ? "" : String.valueOf(toSingleUser));
				this.toUser.setDescription(toUserName);
				for (int i = 0; i < this.listbox_AssignmentRecords.getItems().size(); i++) {
					Listcell listCell = (Listcell) this.listbox_AssignmentRecords.getItems().get(i).getChildren()
							.get(4);
					ExtendedCombobox toUser = (ExtendedCombobox) listCell.getChildren().get(0);
					toUser.setButtonDisabled(true);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onChangeToUser(ForwardEvent event) throws InterruptedException, IllegalAccessException,
			InvocationTargetException, InterfaceException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.listbox_AssignmentRecords);
		ExtendedCombobox combo = (ExtendedCombobox) event.getOrigin().getTarget();
		QueueAssignment queue = (QueueAssignment) event.getData();
		Object dataObject = combo.getObject();
		if (dataObject instanceof SecurityUserOperationRoles) {
			SecurityUserOperationRoles details = (SecurityUserOperationRoles) dataObject;
			if (details != null && details.getUsrID() != queue.getUserId()) {
				if (validUser(queue.getReference(), details.getUsrID(), details.getRoleCd())) {
					queue.setUserId(details.getUsrID());
					queue.setLovDescUserName(details.getLovDescFirstName());
					if (!"".equals(StringUtils.trimToEmpty(queue.getModule()))) {
						queue.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						queue.setNewRecord(false);
					}
					assignedCount = assignedCount + 1;
					rcdAdded = true;
				} else {
					combo.getTextbox().setConstraint("");
					combo.getTextbox().setErrorMessage("");
					combo.getTextbox().setValue("");
					combo.getLabel().setValue("");
					MessageUtil.showError(Labels.getLabel("MultipleUser_Invalid.value",
							new String[] { details.getLovDescFirstName() }));
					return;
				}
			}
		} else {
			if (StringUtils.isEmpty(combo.getTextbox().getValue()) && queue.getUserId() != 0) {
				queue.setUserId(0);
				queue.setLovDescUserName("");
				if (!"".equals(StringUtils.trimToEmpty(queue.getModule()))) {
					queue.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					queue.setNewRecord(true);
				}
				assignedCount = assignedCount - 1;
				rcdAdded = false;
			}
		}

		if (assignedCount > 0) {
			Clients.clearWrongValue(listbox_AssignmentRecords);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the users List From SecUsers Table
	 * 
	 * @throws InterruptedException
	 */
	public void onFulfill$toUser(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.listbox_AssignmentRecords);
		Object dataObject = toUser.getObject();
		if (dataObject instanceof SecurityUserOperationRoles) {
			SecurityUserOperationRoles details = (SecurityUserOperationRoles) dataObject;
			if (details != null
					&& !StringUtils.equals(String.valueOf(details.getUsrID()), getQueueAssignmentHeader().getUserId())) {
				if (validUser(StringUtils.join(references, ','), details.getUsrID(), details.getRoleCd())) {
					this.toUser.setValue(String.valueOf(details.getUsrID()));
					this.toUser.setDescription(details.getLovDescFirstName());
					assignedCount = getQueueAssignmentHeader().getQueueAssignmentsList().size();
					rcdAdded = false;

				} else {
					this.toUser.getTextbox().setConstraint("");
					this.toUser.getTextbox().setErrorMessage("");
					this.toUser.getTextbox().setValue("");
					this.toUser.getLabel().setValue("");
					MessageUtil.showError(Labels.getLabel("SingleUser_Invalid.value",
							new String[] { details.getLovDescFirstName() }));
					return;
				}
			}
		} else {
			if (dataObject == null && !rcdAdded) {
				rcdAdded = false;
			}
			if ((StringUtils.isEmpty(toUser.getTextbox().getValue()) || dataObject instanceof String) && !rcdAdded) {
				assignedCount = 0;
			}
		}
		if (!rcdAdded) {
			doFillToUsers(this.toUser.getValue(), this.toUser.getDescription());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btn_Search(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		Listitem itemName = this.sortOperator_finReference.getSelectedItem();
		int finFilterCode = ((SearchOperators) itemName.getAttribute("data")).getSearchOperatorId();
		String finValue = StringUtils.trimToEmpty(this.finReference.getValue());

		itemName = this.sortOperator_CustomerCIF.getSelectedItem();
		int custFilterCode = ((SearchOperators) itemName.getAttribute("data")).getSearchOperatorId();
		String custValue = StringUtils.trimToEmpty(this.custCIF.getValue());

		List<QueueAssignment> returnList = filterRecords(finFilterCode, finValue, custFilterCode, custValue);

		fillListBoxWithData(returnList, this.listbox_AssignmentRecords);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btn_Refresh(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_CustomerCIF.setSelectedIndex(0);
		this.custCIF.setValue("");
		fillListBoxWithData(getQueueAssignmentHeader().getQueueAssignmentsList(), this.listbox_AssignmentRecords);
		logger.debug("Leaving" + event.toString());
	}

	private boolean validUser(String finReferences, long selectedUserId, String roleCode) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getQueueAssignmentService().checkIfUserAlreadyAccessed(finReferences, String.valueOf(selectedUserId),
				roleCode);
	}

	public QueueAssignment getQueueAssignment() {
		return queueAssignment;
	}

	public void setQueueAssignment(QueueAssignment queueAssignment) {
		this.queueAssignment = queueAssignment;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public QueueAssignmentHeader getQueueAssignmentHeader() {
		return queueAssignmentHeader;
	}

	public void setQueueAssignmentHeader(QueueAssignmentHeader queueAssignmentHeader) {
		this.queueAssignmentHeader = queueAssignmentHeader;
	}
}
