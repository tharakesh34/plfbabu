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
 * FileName    		:SecurityUserOperationDialogCtrl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-03-2014    														*
 *                                                                  						*
 * Modified Date    :  19-03-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-03-2014       Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityuseroperations;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.administration.securityuser.SecurityUserDialogCtrl;
import com.pennant.webui.administration.securityuser.SecurityUserListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUsersOperations
 * /SecurityUserOperationsDialog.zul file.
 */
public class SecurityUserOperationsDialogCtrl extends GFCBaseCtrl<SecurityOperation> {
	private static final long serialVersionUID = 4149506032336052235L;
	private static final Logger logger = Logger.getLogger(SecurityUserOperationsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window win_SecurityUserOperationsDialog; // autoWired

	protected Borderlayout borderLayout_SecurityUsersOperations; // autoWired
	protected Borderlayout borderlayout_SecurityRoleGroups; // autoWired
	protected Listbox listbox_UnAssignedRoles; // autoWired
	protected Listbox listbox_SecurityRoleGroups; // autoWired
	protected Listbox listbox_SecurityOperationRoles; // autoWired
	protected Listbox listbox_AssignedRoles; // autoWired
	protected Panel panel_SecurityRoleGroups; // autoWired
	protected Panel panel_SecurityOperationRoles; // autoWired

	
	protected Button btnSelectRoles; // autoWired
	protected Button btnUnSelectRoles; // autoWired
	protected Button btnUnSelectAllRoles; // autoWired
	protected Button btn_SearchOperations; // autoWired

	protected Label label_UserLogin; // autoWired
	protected Label label_UserDept; // autoWired
	protected Label label_FirstName; // autoWired
	protected Label label_MiddleName; // autoWired
	protected Label label_LastName; // autoWired
	protected Label recordType;
	private boolean enqModule = false;
	
	private transient WorkFlowDetails workFlowDetails = null;

	// private variables and service classes
	private transient SecurityUserOperationsService securityUserOperationsService;
	private SecurityUser securityUser;
	private SecurityUserDialogCtrl securityUserDialogCtrl;
	private SecurityUserOperations securityUserOperations;
	private HashMap<Long, SecurityUserOperations> assignedHashMap;
	private HashMap<Long, SecurityUserOperations> unAssignedHashMap;

	private HashMap<Long, SecurityUserOperations> addHashMap = null;
	private HashMap<Long, SecurityUserOperations> delHashMap = null;
	private HashMap<Long, SecurityUserOperations> cancilHashMap = null;

	private HashMap<Long, SecurityUserOperations> newAssignedMap = new HashMap<Long, SecurityUserOperations>();
	private HashMap<Long, SecurityUserOperations> oldAssignedMap = new HashMap<Long, SecurityUserOperations>();

	private PagedListWrapper<SecurityUserOperations> assigneListWrapper;
	private PagedListWrapper<SecurityUserOperations> unAssigneListWrapper;
	private PagedListWrapper<SecurityRoleGroups> roleGroupsListWrapper;
	private PagedListWrapper<SecurityOperationRoles> operationRolesListWrapper;
	private transient SecurityUserListCtrl securityUserListCtrl;

	private int filterCode = -1;
	private String filterValue = "";

	/**
	 * default constructor.<br>
	 */
	public SecurityUserOperationsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityUserOperationsDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityUsers object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$win_SecurityUserOperationsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(win_SecurityUserOperationsDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// get the parameters map that are over handed by creation.
			if (arguments.containsKey("securityUserListCtrl")) {
				setSecurityUserListCtrl((SecurityUserListCtrl) arguments.get("securityUserListCtrl"));
			} else {
				setSecurityUserListCtrl(null);
			}

			// get the parameters map that are over handed by creation.
			if (arguments.containsKey("securityUser")) {
				setSecurityUser((SecurityUser) arguments.get("securityUser"));
			} else {
				setSecurityUser(null);
			}

			ModuleMapping moduleMapping = PennantJavaUtil
					.getModuleMap("SecurityUserOperations");
			if (moduleMapping.getWorkflowType() != null) {
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityUserOperations");

				if (workFlowDetails == null) {
					setWorkFlowEnabled(false);
				} else {
					setWorkFlowEnabled(true);
					setFirstTask(getUserWorkspace().isRoleContains(
							workFlowDetails.getFirstTaskOwner()));
					setWorkFlowId(workFlowDetails.getId());
				}
			}

			doLoadWorkFlow(this.securityUser.isWorkflow(),
					this.securityUser.getWorkflowId(),
					this.securityUser.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);

			}

			doCheckRights();

			doShowDialog();
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			win_SecurityUserOperationsDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		MessageUtil.showHelpWindow(event,
				this.win_SecurityUserOperationsDialog);
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
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering ");
		doSetPanelProperties();

		displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(),
				getSecurityUser().isNewRecord()));

		// set the paging parameters
		this.listbox_UnAssignedRoles.getItems().clear();
		this.label_UserDept.setValue(getSecurityUser().getUsrDeptCode());
		this.label_FirstName.setValue(getSecurityUser().getUsrFName());
		this.label_MiddleName.setValue(getSecurityUser().getUsrMName());
		this.label_LastName.setValue(getSecurityUser().getUsrLName());
		this.label_UserLogin.setValue(String.valueOf(getSecurityUser()
				.getUsrLogin()));

		assignedHashMap = new HashMap<Long, SecurityUserOperations>();
		unAssignedHashMap = new HashMap<Long, SecurityUserOperations>();

		for (SecurityUserOperations detail : getSecurityUser()
				.getSecurityUserOperationsList()) {
			Long key = detail.getOprID();

			if (PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType())) {
				unAssignedHashMap.put(key, detail);
				if (delHashMap == null) {
					delHashMap = new HashMap<Long, SecurityUserOperations>();
				}

				delHashMap.put(key, detail);
			} else {
				assignedHashMap.put(key, detail);
				// For Data Change Operation
				this.oldAssignedMap.put(key, detail);
				if (PennantConstants.RECORD_TYPE_NEW.equals(detail
						.getRecordType())) {
					if (addHashMap == null) {
						addHashMap = new HashMap<Long, SecurityUserOperations>();
					}
					addHashMap.put(key, detail);
				}
			}
		}

		List<SecurityOperation> securityOperations = getSecurityUserOperationsService()
				.getApprovedOperations();

		for (SecurityOperation operations : securityOperations) {

			Long key = operations.getOprID();

			if (!assignedHashMap.containsKey(key)
					&& !unAssignedHashMap.containsKey(key)) {
				SecurityUserOperations detail = new SecurityUserOperations();
				detail.setUsrID(getSecurityUser().getUsrID());
				detail.setOprID(operations.getOprID());
				detail.setLovDescOprCd(operations.getOprCode());
				detail.setLovDescOprDesc(operations.getOprDesc());
				unAssignedHashMap.put(key, detail);
			}
		}

		// For Data Change Operation
		setOldAssignedMap(this.oldAssignedMap);
		getNewAssignedMap().putAll(oldAssignedMap);

		doWriteBeanToComponents(securityUser);
		
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
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes,
				isWorkFlowEnabled(), isFirstTask(), this.userAction,
				this.listbox_UnAssignedRoles, this.listbox_UnAssignedRoles));

		if (StringUtils.isNotBlank(getSecurityUser().getRecordType())){
			this.btnNotes.setVisible(true);
		}else{
			this.btnNotes.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void doReadOnly(boolean readOnly) {
		int accessType = 1;

		if (isWorkFlowEnabled()) {
			accessType = getUserWorkspace().getAccessType(
					"SecurityUserOperationsDialog_SelectRole");
		}

		if (readOnly || accessType != 1) {
			this.btnSelectRoles.setVisible(true);
			this.btnUnSelectRoles.setVisible(true);
			this.btnUnSelectAllRoles.setVisible(true);
		} else {
			this.btnSelectRoles.setVisible(true);
			this.btnUnSelectRoles.setVisible(true);
			this.btnUnSelectAllRoles.setVisible(true);
		}
		if ("Submitted".equals(getSecurityUser().getRecordStatus())
				|| ("Saved".equals(getSecurityUser().getRecordStatus()) && "MSTGRP1_APPROVER".equals(getSecurityUser()
						.getNextRoleCode()))) {
			this.btnSelectRoles.setVisible(false);
			this.btnUnSelectRoles.setVisible(false);
			this.btnUnSelectAllRoles.setVisible(false);
		}
		//this.btnUnSelectAllRoles.setVisible(false);
	}

	/**
	 * This method sets panel properties
	 */
	public void doSetPanelProperties() {
		logger.debug("Entering ");
		this.panel_SecurityRoleGroups.setOpen(false);
		this.panel_SecurityOperationRoles.setOpen(false);
		this.listbox_SecurityRoleGroups.getItems().clear();
		this.listbox_SecurityOperationRoles.getItems().clear();
		this.panel_SecurityRoleGroups.setTitle(Labels
				.getLabel("panel_RoleGroups.title"));
		this.panel_SecurityOperationRoles.setTitle(Labels
				.getLabel("panel_OperationRoles.title"));
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ Helpers ++++++++++++++++++++++++++
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

		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(super.pageRightName); 
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserOperationsDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed(
				"button_SecurityUserOperationsDialog_btnCancel"));

		if (getUserWorkspace().isAllowed(
				"SecurityUserOperationsDialog_SelectRole")) {
			this.btnSelectRoles.setVisible(true);
			this.btnUnSelectAllRoles.setVisible(true);
			this.btnUnSelectRoles.setVisible(true);
		} else {
			this.btnSelectRoles.setVisible(false);
			this.btnUnSelectAllRoles.setVisible(false);
			this.btnUnSelectRoles.setVisible(false);
		}

		logger.debug("Leaving ");
	}

	public void doWriteBeanToComponents(SecurityUser aSecurityUser) {

		refreshListBox(getAssigneListWrapper(),new ArrayList<SecurityUserOperations>(assignedHashMap.values()),this.listbox_AssignedRoles);
		refreshListBox(getUnAssigneListWrapper(),new ArrayList<SecurityUserOperations>(unAssignedHashMap.values()), this.listbox_UnAssignedRoles);

		this.recordStatus.setValue(aSecurityUser.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aSecurityUser
				.getRecordType()));
	}

	/**
	 * This method do the following 1)compare oldAssigned map and new assigned
	 * map a)if roleId not in oldselectedMap and in new selectedMap creates new
	 * SecurityUserOperations Object, sets data and add it to SecurityUser
	 * LovDescAssignedOperations b)if roleId in oldselectedMap and not in new
	 * selectedMap gets the SecurityUserOperations from back end , sets
	 * RecordStatus "DELETE" add it to SecurityUser LovDescAssignedOperations
	 */
	public void doWriteComponentsToBean(SecurityUser aSecurityUser) {

		List<SecurityUserOperations> secUserOperations = new ArrayList<SecurityUserOperations>();

		if (addHashMap != null && !addHashMap.isEmpty()) {

			Iterator<SecurityUserOperations> addList = addHashMap.values()
					.iterator();

			while (addList.hasNext()) {
				SecurityUserOperations securityUserOperations = addList.next();
				secUserOperations.add(securityUserOperations);
			}
		}

		if (delHashMap != null && !delHashMap.isEmpty()) {

			Iterator<SecurityUserOperations> delList = delHashMap.values()
					.iterator();

			while (delList.hasNext()) {
				SecurityUserOperations securityUserOperations = delList.next();

				secUserOperations.add(securityUserOperations);
			}
		}
		if (cancilHashMap != null && !cancilHashMap.isEmpty()) {

			Iterator<SecurityUserOperations> cancilList = cancilHashMap
					.values().iterator();

			while (cancilList.hasNext()) {
				SecurityUserOperations securityUserOperations = cancilList
						.next();

				secUserOperations.add(securityUserOperations);
			}
		}
		aSecurityUser.setSecurityUserOperationsList(secUserOperations);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSecurityUserListCtrl().search();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aSecurityUser
				.getRecordType()) && isValidation()) {
			// doSetValidation();
			// fill the Branch object with the components data
			doWriteComponentsToBean(aSecurityUser);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aSecurityUser.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityUser.getRecordType())) {
				aSecurityUser.setVersion(aSecurityUser.getVersion() + 1);
				if (isNew) {
					aSecurityUser
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSecurityUser
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityUser.setNewRecord(true);
				}
			}
		} else {
			aSecurityUser.setVersion(aSecurityUser.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
				try {
				
						if (doProcess(aSecurityUser, tranType)) {
							// doWriteComponentsToBean();
							refreshList();
							closeDialog();
						}
					

				} catch (final DataAccessException e) {
			MessageUtil.showError(e);
				}

				logger.debug("Leaving ");
			}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aSecurityUserOperations
	 *            (SecurityUserOperations)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(SecurityUser aSecurityUser, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSecurityUser.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aSecurityUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityUser.setUserDetails(getUserWorkspace().getLoggedInUser());
	
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityUser.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityUser
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityUser);
				}

				if (isNotesMandatory(taskId, aSecurityUser)) {
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

			aSecurityUser.setTaskId(taskId);
			aSecurityUser.setNextTaskId(nextTaskId);
			aSecurityUser.setRoleCode(getRole());
			aSecurityUser.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSecurityUser, tranType);

			String operationRefs = getServiceOperations(taskId, aSecurityUser);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSecurityUser,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSecurityUser, tranType);
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
		SecurityUser aSecurityUser = (SecurityUser) auditHeader
				.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					
					auditHeader = getSecurityUserOperationsService()
							.saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSecurityUserOperationsService()
								.doApprove(auditHeader);

						if (aSecurityUser.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSecurityUserOperationsService()
								.doReject(auditHeader);
						if (aSecurityUser.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.win_SecurityUserOperationsDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.win_SecurityUserOperationsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.securityUserOperations), true);
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
			logger.warn("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		
		logger.debug("Leaving ");
		return processCompleted;
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ OnClick Events+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when "reset" button is clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		this.listbox_UnAssignedRoles.getItems().clear();
		refreshListBox(
				getUnAssigneListWrapper(),
				new ArrayList<SecurityUserOperations>(unAssignedHashMap
						.values()), this.listbox_UnAssignedRoles);
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * when clicks on "btn_SearchUnAssignedRoles"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btn_SearchOperations(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SecurityUserOperationsDialogCtrl", this);
		map.put("FILTERTYPE","USERROLE");
		map.put("FILTERCODE",filterCode);
		map.put("FILTERVALUE",filterValue);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul",this.win_SecurityUserOperationsDialog,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on "btnSelectRoles"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSelectRoles(Event event) throws Exception {
		logger.debug(event.toString());
		setAssignedRoles();

	}

	/**
	 * when clicks on "btnUnSelectRoles"
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onClick$btnUnSelectRoles(Event event) throws Exception {
		logger.debug(event.toString());
		setUnAssignedRoles();

	}

	/**
	 * when clicks on "btnUnSelectRoles"
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onClick$btnUnSelectAllRoles(Event event) throws Exception {
		logger.debug(event.toString());
		if (this.listbox_AssignedRoles.getItemCount() > 0) {
			this.listbox_AssignedRoles.setMultiple(true);
			
			this.listbox_AssignedRoles.selectAll();
		}
		setUnAssignedRoles();

	}

	private void setAssignedRoles() {

		Set<Listitem> set = this.listbox_UnAssignedRoles.getSelectedItems();
		Iterator<Listitem> selecctedItems =  set.iterator();
		
		while(selecctedItems.hasNext()){
			Listitem item = selecctedItems.next();
			SecurityUserOperations detail =  (SecurityUserOperations) item.getAttribute("data");
			Long key = detail.getOprID();
			
			String recStatus = StringUtils.trimToEmpty(detail.getRecordStatus());
			String recType = StringUtils.trimToEmpty(detail.getRecordType());
			
			if (StringUtils.isEmpty(recType) && StringUtils.isEmpty(recStatus)) {
				detail.setNewRecord(true);
				detail.setRecordStatus(PennantConstants.RCD_ADD);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				assignedHashMap.put(key, detail);
				unAssignedHashMap.remove(key);
				if(addHashMap==null){
					addHashMap= new HashMap<Long, SecurityUserOperations>();
				}
				addHashMap.put(key, detail);
			}
			
			if (recType.equals(PennantConstants.RECORD_TYPE_DEL) && recStatus.equals(PennantConstants.RCD_ADD)) {
				detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				detail.setRecordType("");
				assignedHashMap.put(key, detail);
				unAssignedHashMap.remove(key);
			}
			
			if (recType.equals(PennantConstants.RECORD_TYPE_DEL) && !recStatus.equals(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				assignedHashMap.put(key, detail);
				unAssignedHashMap.remove(key);
				if(cancilHashMap == null) {
					cancilHashMap= new HashMap<Long, SecurityUserOperations>();
				}
				cancilHashMap.put(key, detail);		
				delHashMap.remove(key);
			}
			
			if (recType.equals(PennantConstants.RECORD_TYPE_CAN) ) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				assignedHashMap.put(key, detail);
				unAssignedHashMap.remove(key);
				cancilHashMap.remove(key);
			}
		}
		
		if (assignedHashMap.size() > 0) {
			getAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							assignedHashMap.values()),
					this.listbox_AssignedRoles,
					new Paging(assignedHashMap.size(), assignedHashMap.size()));
			this.listbox_AssignedRoles
					.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		} else {
			getAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							assignedHashMap.values()),
					this.listbox_AssignedRoles, new Paging());
			this.listbox_AssignedRoles
					.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		}

		if (unAssignedHashMap.size() > 0) {
			getUnAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							unAssignedHashMap.values()),
					this.listbox_UnAssignedRoles,
					new Paging(unAssignedHashMap.size(), unAssignedHashMap
							.size()));
			this.listbox_UnAssignedRoles
					.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		} else {
			getUnAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							unAssignedHashMap.values()),
					this.listbox_UnAssignedRoles, new Paging());
			this.listbox_UnAssignedRoles
					.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		}

	}

	private void setUnAssignedRoles() {
		logger.debug("Entering");
		Set<Listitem> set = this.listbox_AssignedRoles.getSelectedItems();
		Iterator<Listitem> selecctedItems =  set.iterator();
		
		while(selecctedItems.hasNext()){
			Listitem item = selecctedItems.next();
			SecurityUserOperations detail =  (SecurityUserOperations) item.getAttribute("data");
			if(detail != null){
			Long key = detail.getOprID();
			
			
			String recStatus=StringUtils.trimToEmpty(detail.getRecordStatus());
			String recType=StringUtils.trimToEmpty(detail.getRecordType());
			
			if (recType.equals(PennantConstants.RECORD_TYPE_NEW) && recStatus.equals(PennantConstants.RCD_ADD)) {
				detail.setNewRecord(false);
				detail.setRecordStatus("");
				detail.setRecordType("");
				unAssignedHashMap.put(key, detail);
				assignedHashMap.remove(key);
				addHashMap.remove(key);
			}
			
			if (recType.equals(PennantConstants.RECORD_TYPE_NEW) && !recStatus.equals(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				unAssignedHashMap.put(key, detail);
				assignedHashMap.remove(key);
				if(cancilHashMap==null){
					cancilHashMap= new HashMap<Long, SecurityUserOperations>();
				}
				cancilHashMap.put(key, detail);
				addHashMap.remove(key);
			}
			
			
			if (StringUtils.isEmpty(recType) && recStatus.equals(PennantConstants.RCD_STATUS_APPROVED)) {
				detail.setNewRecord(true);
				detail.setRecordStatus(PennantConstants.RCD_ADD);
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				unAssignedHashMap.put(key, detail);
				assignedHashMap.remove(key);
				if(delHashMap==null){
					delHashMap= new HashMap<Long, SecurityUserOperations>();
				}
				delHashMap.put(key, detail);
			}
			
			if (recType.equals(PennantConstants.RECORD_TYPE_CAN) ) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				unAssignedHashMap.put(key, detail);
				assignedHashMap.remove(key);
				cancilHashMap.remove(key);
			}
			
		}
	}
		if (assignedHashMap.size() > 0) {
			getAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							assignedHashMap.values()),
					this.listbox_AssignedRoles,
					new Paging(assignedHashMap.size(), assignedHashMap.size()));
			this.listbox_AssignedRoles
					.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		} else {
			getAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							assignedHashMap.values()),
					this.listbox_AssignedRoles, new Paging());
			this.listbox_AssignedRoles
					.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		}

		if (unAssignedHashMap.size() > 0) {
			getUnAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							unAssignedHashMap.values()),
					this.listbox_UnAssignedRoles,
					new Paging(unAssignedHashMap.size(), unAssignedHashMap
							.size()));
			this.listbox_UnAssignedRoles
					.setItemRenderer(new SecurityOperationListModelItemRenderer());
		} else {
			getUnAssigneListWrapper().initList(
					new ArrayList<SecurityUserOperations>(
							unAssignedHashMap.values()),
					this.listbox_UnAssignedRoles, new Paging());
			this.listbox_UnAssignedRoles
					.setItemRenderer(new SecurityOperationListModelItemRenderer());
		}

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityRoleItemDoubleClicked(ForwardEvent event)
			throws Exception {
		logger.debug("Entering ");
		this.panel_SecurityRoleGroups.setOpen(true);
		doShowRoleGroups(event);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method display the list of Roles Assigned to selected Operation
	 * 
	 * @throws InterruptedException
	 */
	public void doShowRoleGroups(ForwardEvent event)
			throws InterruptedException {

		logger.debug("Entering ");

		Listitem item = (Listitem) event.getOrigin().getTarget();

		if (item != null) {
			SecurityOperationRoles aSecurityOperationRoles = (SecurityOperationRoles) item
					.getAttribute("data");
			this.panel_SecurityRoleGroups.setTitle(Labels
					.getLabel("listbox_SecurityUserRoleGroups.value")
					+ " - "
					+ aSecurityOperationRoles.getLovDescOprCode());
			this.listbox_SecurityRoleGroups.setVisible(true);
			this.panel_SecurityRoleGroups.setTitle(Labels.getLabel("listbox_SecurityUserOperationRole.value")+"  :  "+aSecurityOperationRoles.getLovDescRoleDesc());

			List<SecurityRoleGroups> roleGroupsList = getSecurityUserOperationsService().getApprovedRoleGroupsByRoleId(aSecurityOperationRoles.getRoleID());
			
			Paging paging = new Paging();
			if(roleGroupsList.size() > 0){
				paging.setPageSize(roleGroupsList.size());
			}
			
			getRoleGroupsListWrapper().initList(new ArrayList<SecurityRoleGroups>(roleGroupsList),this.listbox_SecurityRoleGroups, new Paging());
			this.listbox_SecurityRoleGroups.setItemRenderer(new SecurityOprRoleListModelItemRenderer());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityOprRoleItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		this.panel_SecurityOperationRoles.setOpen(true);
		doShowOperationRoles(event);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method display the list of groups assigned to selected role.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowOperationRoles(ForwardEvent event) throws InterruptedException {

		logger.debug("Entering ");

		Listitem item = (Listitem) event.getOrigin().getTarget();

		if (item != null) {
			SecurityUserOperations aSecurityUserOperations = (SecurityUserOperations) item
					.getAttribute("data");
			this.panel_SecurityOperationRoles.setTitle(Labels
					.getLabel("listbox_SecurityUserOperationRole.value")
					+ " - "
					+ aSecurityUserOperations.getLovDescOprCd());
			this.listbox_SecurityOperationRoles.setVisible(true);
			this.listbox_SecurityRoleGroups.getItems().clear();
			this.panel_SecurityRoleGroups.setOpen(false);

			List<SecurityOperationRoles> oprRolesList = getSecurityUserOperationsService()
					.getOperationRolesByOprId(aSecurityUserOperations);
			
			getOperationRolesListWrapper().initList(
					new ArrayList<SecurityOperationRoles>(oprRolesList),
					this.listbox_SecurityOperationRoles, new Paging());
			this.listbox_SecurityOperationRoles
					.setItemRenderer(new OperationRolesListModelItemRenderer());
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityUserOperations
	 */
	public class SecurityOperationListModelItemRenderer implements
	ListitemRenderer<SecurityUserOperations>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item,
				SecurityUserOperations userOperations, int count)
				throws Exception {

			Listcell listCell;

			listCell = new Listcell(userOperations.getLovDescOprCd());
			listCell.setParent(item);

			listCell = new Listcell(userOperations.getLovDescOprDesc());
			listCell.setParent(item);
			item.setAttribute("data", userOperations);
			item.addForward("onDoubleClick", win_SecurityUserOperationsDialog,
					"onSecurityOprRoleItemDoubleClicked", item);
		}
	}


	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityUserOperations
	 */
	public class SecurityUserOperationListModelItemRenderer implements
			ListitemRenderer<SecurityUserOperations>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item,
				SecurityUserOperations userOperations, int count)
				throws Exception {

			Listcell listCell;

			listCell = new Listcell(userOperations.getLovDescOprCd());
			listCell.setParent(item);

			listCell = new Listcell(userOperations.getLovDescOprDesc());
			listCell.setParent(item);
			item.setAttribute("data", userOperations);
			item.addForward("onDoubleClick", win_SecurityUserOperationsDialog,
					"onSecurityOprRoleItemDoubleClicked", item);
		}
	}

	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityUserOperations
	 */
	public class SecurityOprRoleListModelItemRenderer implements
			ListitemRenderer<SecurityRoleGroups>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, SecurityRoleGroups securityRoleGroups,
				int count) throws Exception {

			Listcell listCell;

			listCell = new Listcell(securityRoleGroups.getLovDescGrpCode());
			listCell.setParent(item);
			
			listCell = new Listcell(securityRoleGroups.getLovDescGrpDesc());
			listCell.setParent(item);
			item.setAttribute("data", securityRoleGroups);
			
		}
	}

	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityUserOperations
	 */
	public class OperationRolesListModelItemRenderer implements ListitemRenderer<SecurityOperationRoles>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, SecurityOperationRoles secOperationRoles, int count) throws Exception {

			Listcell listCell;

			listCell = new Listcell(secOperationRoles.getLovDescRoleCd());
			listCell.setParent(item);
			
			listCell = new Listcell(secOperationRoles.getLovDescRoleDesc());
			listCell.setParent(item);

			item.setAttribute("data", secOperationRoles);
			item.addForward("onDoubleClick", win_SecurityUserOperationsDialog, "onSecurityRoleItemDoubleClicked", item);
		}
	}

	public void filterOperations(int filterCode, String filterValue) {

		this.filterCode = filterCode;
		this.filterValue = filterValue;

		List<SecurityUserOperations> unassignedList = new ArrayList<SecurityUserOperations>();
		if (filterCode == -1) {
			unassignedList = new ArrayList<SecurityUserOperations>(unAssignedHashMap.values());
		} else {
			for (SecurityUserOperations userOperations : unAssignedHashMap.values()) {

				switch (filterCode) {
				case Filter.OP_EQUAL:
					if (userOperations.getLovDescOprCd().equalsIgnoreCase(filterValue)) {
						unassignedList.add(userOperations);
					}
					break;
				case Filter.OP_NOT_EQUAL:
					if (!userOperations.getLovDescOprCd().equals(filterValue)) {
						unassignedList.add(userOperations);
					}
					break;
				case Filter.OP_LIKE:
					if (userOperations.getLovDescOprCd().toUpperCase().contains(filterValue)) {
						unassignedList.add(userOperations);
					}
					break;
				}
			}
		}

		refreshListBox(getUnAssigneListWrapper(), unassignedList, this.listbox_UnAssignedRoles);
	}
	/**
	 * This method Refreshes the listBox for AssignedOperations and UnAssignedOperations 
	 * @param listbox
	 * @param SecurityUserOperations
	 */
	private void refreshListBox(PagedListWrapper<SecurityUserOperations> listWrapper, List<SecurityUserOperations> userOperations,Listbox listBox){
		logger.debug("Entering");
		Paging paging=   new Paging();

		if(userOperations.size() > 0) {
			paging.setPageSize(userOperations.size());
		}

		listWrapper.initList(userOperations, listBox, paging);
		listBox.setItemRenderer(new SecurityUserOperationListModelItemRenderer());
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * This method creates and returns AuditHeader Object
	 * 
	 * @param aSecurityRight
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);
		AuditHeader auditHeader = new AuditHeader(String.valueOf(aSecurityUser.getId()), null, null, null, auditDetail, aSecurityUser.getUserDetails(), getOverideMap());
		auditHeader.setAuditModule(SecurityUserOperations.class.getSimpleName());
		return auditHeader;
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
		doShowNotes(this.securityUser);
	}

	// Get the notes entered for rejected reason
	/*
	 * private Notes getNotes(){ Notes notes = new Notes();
	 * notes.setModuleName("EmirateDetail");
	 * notes.setReference(getEmirateDetail().getEmirateId());
	 * notes.setVersion(getEmirateDetail().getVersion()); return notes; }
	 */


	
	
	/**
	 * This method displays  the filtered data in unAssigned Roles panel .
	 * @param searchResult
	 */
	public int doShowSearchResult(Object[] searchResult){
		logger.debug("Entering");
		
		int searchOperator = -1;
		String searchValue = "";
		if(searchResult != null && searchResult.length > 0) {
			searchOperator = (Integer)searchResult[0];
			searchValue = (String)searchResult[1];
		}
		filterRoles(searchOperator, searchValue);
		
		logger.debug("Leaving");
		return listbox_UnAssignedRoles.getItemCount();
	}
	
public void filterRoles(int filterCode, String filterValue){
		
		this.filterCode=filterCode;
		this.filterValue=filterValue;
		
		List<SecurityUserOperations> unassignedList= new ArrayList<SecurityUserOperations>();
		if(filterCode==-1){
			unassignedList = new ArrayList<SecurityUserOperations>(unAssignedHashMap.values());
		}else{
			for (SecurityUserOperations useropr:unAssignedHashMap.values()) {
				
				switch (filterCode) {
					case Filter.OP_EQUAL:
						if(useropr.getLovDescOprCd().equals(filterValue)){
							unassignedList.add(useropr);
						}
					break;
					case Filter.OP_NOT_EQUAL:
						if(!useropr.getLovDescOprCd().equals(filterValue)){
							unassignedList.add(useropr);
						}
					break;
					case Filter.OP_LIKE:
						if(useropr.getLovDescOprCd().contains(filterValue)){
							unassignedList.add(useropr);
						}
					break;
				}	
			}
		}
		this.listbox_UnAssignedRoles.getItems().clear();
		fillListBoxWithData(unassignedList, this.listbox_UnAssignedRoles);
	}
	

  private void fillListBoxWithData(List<SecurityUserOperations> userOperations, Listbox listbox) {
	listbox.getItems().clear();
	if (userOperations!=null && !userOperations.isEmpty()) {
		for (SecurityUserOperations securityUserOperations : userOperations) {
			
			Listitem item = new Listitem();
			Listcell listCell;
			
			listCell = new Listcell(securityUserOperations.getLovDescOprCd());
			listCell.setParent(item);

			listCell = new Listcell(securityUserOperations.getLovDescOprDesc());
			listCell.setParent(item);
			
			item.setAttribute("data", securityUserOperations);
			item.addForward("onDoubleClick", win_SecurityUserOperationsDialog, "onSecurityRoleItemDoubleClicked", item);
			listbox.appendChild(item);
			
			refreshListBox(getUnAssigneListWrapper(), userOperations, listbox);
			
		}
	}

}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	/*
	 * public void onClick$btnNotes(Event event) throws Exception {
	 * logger.debug("Entering" +event.toString()); try {
	 * ScreenCTL.displayNotes(getNotes
	 * ("SecurityUser",getSecurityUser().getUsrLogin
	 * (),getSecurityUser().getVersion()),this); } catch (Exception e) {
	 * logger.error("Exception: Opening window", e);
	 * MessageUtil.showErrorMessage(e); } logger.debug("Leaving"
	 * +event.toString());
	 * 
	 * }
	 */

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public SecurityUser getSecurityUser() {
		return securityUser;
	}

	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityUserOperations> getAssigneListWrapper() {
		if (this.assigneListWrapper == null) {
			this.assigneListWrapper = (PagedListWrapper<SecurityUserOperations>) SpringUtil
					.getBean("pagedListWrapper");
			;
		}

		return assigneListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityUserOperations> getUnAssigneListWrapper() {
		if (this.unAssigneListWrapper == null) {
			this.unAssigneListWrapper = (PagedListWrapper<SecurityUserOperations>) SpringUtil
					.getBean("pagedListWrapper");
			;
		}

		return unAssigneListWrapper;
	}

	public void setRoleGroupsListWrapper(
			PagedListWrapper<SecurityRoleGroups> roleGroupsListWrapper) {
		this.roleGroupsListWrapper = roleGroupsListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityRoleGroups> getRoleGroupsListWrapper() {
		if (this.roleGroupsListWrapper == null) {
			this.roleGroupsListWrapper = (PagedListWrapper<SecurityRoleGroups>) SpringUtil.getBean("pagedListWrapper");
		}
		return roleGroupsListWrapper;
	}

	public void setOperationRolesListWrapper(
			PagedListWrapper<SecurityOperationRoles> operationRolesListWrapper) {
		this.operationRolesListWrapper = operationRolesListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityOperationRoles> getOperationRolesListWrapper() {
		if (this.operationRolesListWrapper == null) {
			this.operationRolesListWrapper = (PagedListWrapper<SecurityOperationRoles>) SpringUtil
					.getBean("pagedListWrapper");
			;
		}
		return operationRolesListWrapper;
	}
	
	
	@Override
	protected String getReference() {
		return String.valueOf(this.securityUser.getUsrID());
	}

	public void setSecurityUserOperations(
			SecurityUserOperations securityUserOperations) {
		this.securityUserOperations = securityUserOperations;
	}

	public SecurityUserOperations getSecurityUserOperations() {
		return securityUserOperations;
	}

	public void setSecurityUserDialogCtrl(
			SecurityUserDialogCtrl securityUserDialogCtrl) {
		this.securityUserDialogCtrl = securityUserDialogCtrl;
	}

	public SecurityUserDialogCtrl getSecurityUserDialogCtrl() {
		return securityUserDialogCtrl;
	}

	public void setSecurityUserListCtrl(
			SecurityUserListCtrl securityUserListCtrl) {
		this.securityUserListCtrl = securityUserListCtrl;
	}

	public SecurityUserListCtrl getSecurityUserListCtrl() {
		return securityUserListCtrl;
	}

	public SecurityUserOperationsService getSecurityUserOperationsService() {
		return securityUserOperationsService;
	}

	public void setSecurityUserOperationsService(
			SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public HashMap<Long, SecurityUserOperations> getNewAssignedMap() {
		return newAssignedMap;
	}

	public void setNewAssignedMap(
			HashMap<Long, SecurityUserOperations> newAssignedMap) {
		this.newAssignedMap = newAssignedMap;
	}

	public HashMap<Long, SecurityUserOperations> getOldAssignedMap() {
		return oldAssignedMap;
	}

	public void setOldAssignedMap(
			HashMap<Long, SecurityUserOperations> oldAssignedMap) {
		this.oldAssignedMap = oldAssignedMap;
	}

}
