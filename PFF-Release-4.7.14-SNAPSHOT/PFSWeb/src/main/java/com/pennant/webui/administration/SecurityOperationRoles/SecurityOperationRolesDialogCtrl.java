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
 * FileName    		:  SecurityOperationRolesDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014        Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.SecurityOperationRoles;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityOperationRolesService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * @author S081
 *
 */
public class SecurityOperationRolesDialogCtrl extends GFCBaseCtrl<SecurityOperation> {
	private static final long serialVersionUID = 4149506032336052235L;
	private static final Logger logger = Logger.getLogger(SecurityOperationRolesDialogCtrl.class);
	
	protected Window win_Operation_Roles_Dialog;

	protected Borderlayout borderLayout_SecurityUsersRoles;
	protected Borderlayout borderlayout_SecurityRoleGroups;
	protected Listbox listbox_UnAssignedRoles;
	protected Listbox listbox_OperationRoleGroups;
	protected Listbox listbox_SecurityGroupRights;
	protected Listbox listbox_AssignedRoles;
	protected Panel panel_OperationRoleGroups;
	protected Panel panel_SecurityGroupRights;
	
	protected Button btnSelectRoles;
	protected Button btnUnSelectRoles;
	protected Button btnUnSelectAllRoles;
	protected Button btn_SearchRoles;
	protected Button btnRefresh;

	protected Label label_UserLogin;
	protected Label label_UserDept;
	protected Label label_FirstName;
	protected Label label_MiddleName;
	protected Label label_LastName;
	protected Label label_OperationCode;
	protected Label label_OperationDesc;
	
	protected JdbcSearchObject<SecurityOperationRoles> searchObj;
	protected Label recordType;
	
	// Button controller for the CRUD buttons

	private transient WorkFlowDetails workFlowDetails = null;

	// private variables and service classes
	private transient SecurityOperationRolesService securityOperationRolesService;
	private SecurityOperation securityOperation;
	private HashMap<Long, SecurityOperationRoles> assignedHashMap;
	private HashMap<Long, SecurityOperationRoles> unAssignedHashMap;

	private HashMap<Long, SecurityOperationRoles> addHashMap = null;
	private HashMap<Long, SecurityOperationRoles> delHashMap = null;
	private HashMap<Long, SecurityOperationRoles> cancilHashMap = null;

	private PagedListWrapper<SecurityOperationRoles> assigneListWrapper;
	private PagedListWrapper<SecurityOperationRoles> unAssigneListWrapper;
	private PagedListWrapper<SecurityRoleGroups> roleGroupsListWrapper;
	private PagedListWrapper<SecurityGroupRights> groupRightsListWrapper;
	private transient SecurityOperationRolesListCtrl operationRolesListCtrl;
	private int filterCode = -1;
	private String filterValue = "";

	/**
	 * default constructor.<br>
	 */
	public SecurityOperationRolesDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityOperationRolesDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Securityoperations object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$win_Operation_Roles_Dialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(win_Operation_Roles_Dialog);

		try {
			// get the parameters map that are over handed by creation.
			if (arguments.containsKey("SecurityOperationRoles")) {
				setOperationRolesListCtrl((SecurityOperationRolesListCtrl) arguments.get("SecurityOperationRoles"));
			} else {
				setOperationRolesListCtrl(null);
			}

			// get the parameters map that are over handed by creation.
			if (arguments.containsKey("securityOperation")) {
				setSecurityOperation((SecurityOperation) arguments.get("securityOperation"));
			} else {
				setSecurityOperation(null);
			}
			this.label_OperationCode.setValue(getSecurityOperation().getOprCode());
			this.label_OperationDesc.setValue(getSecurityOperation().getOprDesc());
			//
			this.searchObj = new JdbcSearchObject<SecurityOperationRoles>(
					SecurityOperationRoles.class, getListRows());
			this.searchObj.addSort("roleCd", false);
			this.searchObj.addTabelName("SecOperations");
			//

			ModuleMapping moduleMapping = PennantJavaUtil
					.getModuleMap("SecurityOperationRoles");
			if (moduleMapping.getWorkflowType() != null) {
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityOperationRoles");

				if (workFlowDetails == null) {
					setWorkFlowEnabled(false);
				} else {
					setWorkFlowEnabled(true);
					setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
					setWorkFlowId(workFlowDetails.getId());
				}
			}
			doLoadWorkFlow(this.securityOperation.isWorkflow(),	this.securityOperation.getWorkflowId(),this.securityOperation.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),"SecurityOperationRolesDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doCheckRights();

			doShowDialog();
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			win_Operation_Roles_Dialog.onClose();
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, this.win_Operation_Roles_Dialog);
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

		displayComponents(ScreenCTL.getMode(enqiryModule, isWorkFlowEnabled(), getSecurityOperation().isNewRecord()));

		// set the paging parameters
		this.listbox_UnAssignedRoles.getItems().clear();

		assignedHashMap = new HashMap<Long, SecurityOperationRoles>();
		unAssignedHashMap = new HashMap<Long, SecurityOperationRoles>();

		for (SecurityOperationRoles detail : getSecurityOperation().getSecurityOperationRolesList()) {
			Long key = detail.getRoleID();

			if (PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType())) {
				unAssignedHashMap.put(key, detail);
				if (delHashMap == null) {
					delHashMap = new HashMap<Long, SecurityOperationRoles>();
				}

				delHashMap.put(key, detail);
			} else {
				assignedHashMap.put(key, detail);
				if (PennantConstants.RECORD_TYPE_NEW.equals(detail.getRecordType())) {
					if (addHashMap == null) {
						addHashMap = new HashMap<Long, SecurityOperationRoles>();
					}
					addHashMap.put(key, detail);
				}
			}
		}

		List<SecurityRole> securityRoles = getSecurityOperationRolesService().getApprovedRoles();

		for (SecurityRole roles : securityRoles) {

			Long key = roles.getRoleID();

			if (!assignedHashMap.containsKey(key) && !unAssignedHashMap.containsKey(key)) {
				SecurityOperationRoles detail = new SecurityOperationRoles();
				detail.setOprID(getSecurityOperation().getOprID());
				detail.setRoleID(roles.getRoleID());
				detail.setLovDescRoleCd(roles.getRoleCd());
				detail.setLovDescRoleDesc(roles.getRoleDesc());
				unAssignedHashMap.put(key, detail);
			}
		}

		doWriteBeanToComponents(securityOperation);

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

		/*if (getSecurityOperation().getNextRoleCode().equals("")
				|| getSecurityOperation().getNextRoleCode().equals("MSTGRP1_MAKER")) {
			this.btnNotes.setVisible(false);
		}*/
		
		if (StringUtils.isNotBlank(getSecurityOperation().getRecordType())){
			this.btnNotes.setVisible(true);
		}else{
			this.btnNotes.setVisible(false);
		}
		logger.debug("Leaving");
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		this.btnSelectRoles.setVisible(true);
		this.btnUnSelectRoles.setVisible(true);
		this.btnUnSelectAllRoles.setVisible(true);

		if ("Submitted".equals(getSecurityOperation().getRecordStatus())
				|| ("Saved".equals(getSecurityOperation().getRecordStatus()) && "MSTGRP1_APPROVER"
						.equals(getSecurityOperation().getNextRoleCode()))) {
			this.btnSelectRoles.setVisible(false);
			this.btnUnSelectRoles.setVisible(false);
			this.btnUnSelectAllRoles.setVisible(false);
			this.btn_SearchRoles.setVisible(false);
			this.btnRefresh.setVisible(false);
		}
		// this.btnUnSelectAllRoles.setVisible(false);
	}

	/**
	 * This method sets panel properties
	 */
	public void doSetPanelProperties() {
		logger.debug("Entering ");
		this.panel_OperationRoleGroups.setOpen(false);
		this.panel_SecurityGroupRights.setOpen(false);
		this.listbox_OperationRoleGroups.getItems().clear();
		this.listbox_SecurityGroupRights.getItems().clear();
		this.panel_OperationRoleGroups.setTitle(Labels
				.getLabel("panel_RoleGroups.title"));
		this.panel_SecurityGroupRights.setTitle(Labels
				.getLabel("panel_GroupRights.title"));

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
		getUserWorkspace().allocateAuthorities("SecurityOperationRolesDialog",getRole());
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityOperationRolesDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_SecurityOperationRolesDialog_btnCancel"));

		if (getUserWorkspace().isAllowed("SecurityOperationRolesDialog_SelectRole")) {
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
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSecurityUser
	 *            SecurityOperation
	 */
	public void doWriteBeanToComponents(SecurityOperation aSecurityOperation) {

		refreshListBox(getAssigneListWrapper(),
				new ArrayList<SecurityOperationRoles>(assignedHashMap.values()),
				this.listbox_AssignedRoles);
		refreshListBox(getUnAssigneListWrapper(),
				new ArrayList<SecurityOperationRoles>(unAssignedHashMap.values()),
				this.listbox_UnAssignedRoles);

		this.recordStatus.setValue(aSecurityOperation.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aSecurityOperation.getRecordType()));
	}

	/**
	 * This method do the following 1)compare oldAssigned map and new assigned
	 * map a)if roleId not in oldselectedMap and in new selectedMap creates new
	 * SecurityOperation Object, sets data and add it to SecurityUser
	 * LovDescAssignedRoles b)if roleId in oldselectedMap and not in new
	 * selectedMap gets the SecurityOperation from back end , sets RecordStatus
	 * "DELETE" add it to SecurityUser LovDescAssignedRoles
	 */
	public void doWriteComponentsToBean(SecurityOperation aSecurityOperation) {


		List<SecurityOperationRoles> secOprRoles = new ArrayList<SecurityOperationRoles>();

		if (addHashMap != null && !addHashMap.isEmpty()) {

			Iterator<SecurityOperationRoles> addList = addHashMap.values()
					.iterator();

			while (addList.hasNext()) {
				SecurityOperationRoles securityOperationRoles = addList.next();
				secOprRoles.add(securityOperationRoles);
			}
		}

		if (delHashMap != null && !delHashMap.isEmpty()) {

			Iterator<SecurityOperationRoles> delList = delHashMap.values()
					.iterator();

			while (delList.hasNext()) {
				SecurityOperationRoles securityOperationRoles = delList.next();

				secOprRoles.add(securityOperationRoles);
			}
		}
		if (cancilHashMap != null && !cancilHashMap.isEmpty()) {

			Iterator<SecurityOperationRoles> cancilList = cancilHashMap.values()
					.iterator();

			while (cancilList.hasNext()) {
				SecurityOperationRoles securityOperationRoles = cancilList.next();

				secOprRoles.add(securityOperationRoles);
			}
		}
		aSecurityOperation.setSecurityOperationRolesList(secOprRoles);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getOperationRolesListCtrl().search();
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

		final SecurityOperation aSecurityOperation = new SecurityOperation();
		BeanUtils.copyProperties(getSecurityOperation(), aSecurityOperation);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aSecurityOperation
				.getRecordType()) && isValidation()) {
			// doSetValidation();
			// fill the Branch object with the components data
			doWriteComponentsToBean(aSecurityOperation);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aSecurityOperation.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityOperation.getRecordType())) {
				aSecurityOperation.setVersion(aSecurityOperation.getVersion() + 1);
				if (isNew) {
					aSecurityOperation
					.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSecurityOperation
					.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityOperation.setNewRecord(true);
				}
			}
		} else {
			aSecurityOperation.setVersion(aSecurityOperation.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		
		try {
         	if (doProcess(aSecurityOperation, tranType)) {
					refreshList();
					closeDialog();
				}
			

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		logger.debug("Leaving ");
	}
		
		
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aSecurityOperation
	 *            (SecurityOperation)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(SecurityOperation aSecurityOperation, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSecurityOperation.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aSecurityOperation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityOperation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityOperation.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());
			aSecurityOperation.setUserAction(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(aSecurityOperation.getUserAction())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityOperation
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityOperation);
				}

				if (isNotesMandatory(taskId, aSecurityOperation)) {
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

			aSecurityOperation.setTaskId(taskId);
			aSecurityOperation.setNextTaskId(nextTaskId);
			aSecurityOperation.setRoleCode(getRole());
			aSecurityOperation.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSecurityOperation, tranType);

			String operationRefs = getServiceOperations(taskId, aSecurityOperation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSecurityOperation,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSecurityOperation, tranType);
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
		SecurityOperation aSecurityOperation = (SecurityOperation) auditHeader
				.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {

					auditHeader = getSecurityOperationRolesService().saveOrUpdate(
							auditHeader);
					// }

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSecurityOperationRolesService().doApprove(
								auditHeader);

						if (aSecurityOperation.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSecurityOperationRolesService().doReject(
								auditHeader);
						if (aSecurityOperation.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.win_Operation_Roles_Dialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.win_Operation_Roles_Dialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.securityOperation), true);
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

		logger.debug("Leaving ");
		return processCompleted;
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
		refreshListBox(getUnAssigneListWrapper(), new ArrayList<SecurityOperationRoles>(unAssignedHashMap.values()),
				this.listbox_UnAssignedRoles);
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * when clicks on "btn_SearchUnAssignedRoles"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btn_SearchRoles(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSetPanelProperties();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SecurityOperationRolesDialogCtrl", this);
		map.put("FILTERTYPE","USERROLE");
		map.put("FILTERCODE",filterCode);
		map.put("FILTERVALUE",filterValue);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul",this.win_Operation_Roles_Dialog,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * This method displays the filtered data in unAssigned Groups panel .
	 * 
	 * @param searchResult
	 */
	public int doShowSearchResult(Object[] searchResult) {
		logger.debug("Entering");

		int searchOperator = -1;
		String searchValue = "";
		if (searchResult != null && searchResult.length > 0) {
			searchOperator = (Integer) searchResult[0];
			searchValue = (String) searchResult[1];
		}
		filterRoles(searchOperator, searchValue);

		logger.debug("Leaving");
		return listbox_UnAssignedRoles.getItemCount();
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

	/**
	 * Used to set the assingned roles
	 */
	private void setAssignedRoles() {

		Iterator<Listitem> selecctedItems = this.listbox_UnAssignedRoles
				.getSelectedItems().iterator();

		while (selecctedItems.hasNext()) {
			Listitem item = selecctedItems.next();
			SecurityOperationRoles detail = (SecurityOperationRoles) item
					.getAttribute("data");
			Long key = detail.getRoleID();

			if (StringUtils.isBlank(detail.getRecordType())) {
				detail.setNewRecord(true);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				assignedHashMap.put(key, detail);
				unAssignedHashMap.remove(key);
				if (addHashMap == null) {
					addHashMap = new HashMap<Long, SecurityOperationRoles>();
				}
				addHashMap.put(key, detail);

			} 

		}

		getAssigneListWrapper().initList(
				new ArrayList<SecurityOperationRoles>(assignedHashMap.values()),
				this.listbox_AssignedRoles, new Paging());
		getUnAssigneListWrapper().initList(
				new ArrayList<SecurityOperationRoles>(unAssignedHashMap.values()),
				this.listbox_UnAssignedRoles, new Paging());

	}
	/**
	 * Used to set the Unassingned roles
	 */
	private void setUnAssignedRoles() {
		logger.debug("Entering");
		Iterator<Listitem> selecctedItems = this.listbox_AssignedRoles
				.getSelectedItems().iterator();

		while (selecctedItems.hasNext()) {
			Listitem item = selecctedItems.next();
			SecurityOperationRoles detail = (SecurityOperationRoles) item
					.getAttribute("data");
			Long key = detail.getRoleID();

			if (StringUtils.isBlank(detail.getRecordType())) {
				detail.setNewRecord(true);
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (delHashMap == null) {
					delHashMap = new HashMap<Long, SecurityOperationRoles>();
				}
				delHashMap.put(key, detail);

			} else if (PennantConstants.RECORD_TYPE_NEW.equals(detail
					.getRecordType())) {
				addHashMap.remove(key);

				if (!detail.isNewRecord()) {
					if (cancilHashMap == null) {
						cancilHashMap = new HashMap<Long, SecurityOperationRoles>();
					}
					detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					cancilHashMap.put(key, detail);
				} else {
					detail.setRecordType("");
					detail.setNewRecord(false);
				}
			} 
			unAssignedHashMap.put(key, detail);
			assignedHashMap.remove(key);
		}

		getAssigneListWrapper().initList(
				new ArrayList<SecurityOperationRoles>(assignedHashMap.values()),
				this.listbox_AssignedRoles, new Paging());
		getUnAssigneListWrapper().initList(
				new ArrayList<SecurityOperationRoles>(unAssignedHashMap.values()),
				this.listbox_UnAssignedRoles, new Paging());

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityRoleItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering ");
		this.panel_OperationRoleGroups.setOpen(true);
		doShowRoleGroups(event);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method display the list of groups Assigned to selected role
	 * 
	 * @throws InterruptedException
	 */
	public void doShowRoleGroups(ForwardEvent event) throws InterruptedException {

		logger.debug("Entering ");

		Listitem item = (Listitem) event.getOrigin().getTarget();

		if (item != null) {
			SecurityOperationRoles aSecurityOperationRoles = (SecurityOperationRoles) item.getAttribute("data");
			this.panel_OperationRoleGroups.setTitle(Labels.getLabel("listbox_SecurityUserRoleGroups.value")
					+ " - " + aSecurityOperationRoles.getLovDescRoleCd());
			this.listbox_OperationRoleGroups.setVisible(true);
			this.listbox_OperationRoleGroups.getItems().clear();
			this.listbox_SecurityGroupRights.getItems().clear();
			this.panel_SecurityGroupRights.setOpen(false);
			this.panel_SecurityGroupRights.setTitle(Labels .getLabel("listbox_SecurityUserRoleGroupRights.value"));

			List<SecurityRoleGroups> roleGroupsList = getSecurityOperationRolesService().getApprovedRoleGroupsByRoleId(aSecurityOperationRoles.getRoleID());
			
			Paging paging = new Paging();
			if(roleGroupsList.size() > 0){
				paging.setPageSize(roleGroupsList.size());
			}
			
			getRoleGroupsListWrapper().initList( new ArrayList<SecurityRoleGroups>(roleGroupsList), this.listbox_OperationRoleGroups, paging);
			this.listbox_OperationRoleGroups .setItemRenderer(new SecurityGroupListModelItemRenderer());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityGroupItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		this.panel_SecurityGroupRights.setOpen(true);
		doShowGroupsRights();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method display the list of rights assigned to selected group.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowGroupsRights() throws InterruptedException {

		logger.debug("Entering ");

		this.listbox_SecurityGroupRights.setVisible(true);
		this.listbox_SecurityGroupRights.getItems().clear();
		final Listitem item = this.listbox_OperationRoleGroups.getSelectedItem();
		SecurityRoleGroups securityRoleGroups = new SecurityRoleGroups();
		if (item != null) {
			securityRoleGroups = (SecurityRoleGroups) item.getAttribute("data");
		}
		this.panel_SecurityGroupRights.setTitle(Labels.getLabel("listbox_SecurityUserRoleGroupRights.value")
				+ " - "+ securityRoleGroups.getLovDescGrpCode());

		SecurityGroup secGroups = new SecurityGroup();
		secGroups.setGrpID(securityRoleGroups.getGrpID());
		List<SecurityGroupRights> grpRightsList = getSecurityOperationRolesService().getGroupRightsByGrpId(secGroups);

		Paging paging=   new Paging();

		if(grpRightsList.size() > 0) {
			paging.setPageSize(grpRightsList.size());
		}

		getGroupRightsListWrapper().initList(new ArrayList<SecurityGroupRights>(grpRightsList),this.listbox_SecurityGroupRights, paging);
		this.listbox_SecurityGroupRights.setItemRenderer(new GroupRightListModelItemRenderer());
		logger.debug("Leaving ");
	}

	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityOperationRoles
	 */
	public class SecurityUserRoleListModelItemRenderer implements
	ListitemRenderer<SecurityOperationRoles>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, SecurityOperationRoles oprRoles, int count)
				throws Exception {

			Listcell listCell;

			listCell = new Listcell(oprRoles.getLovDescRoleCd());
			listCell.setParent(item);

			listCell = new Listcell(oprRoles.getLovDescRoleDesc());
			listCell.setParent(item);
			item.setAttribute("data", oprRoles);
			item.addForward("onDoubleClick", win_Operation_Roles_Dialog,
					"onSecurityRoleItemDoubleClicked", item);
		}
	}

	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityOperationRoles
	 */
	public class SecurityGroupListModelItemRenderer implements
	ListitemRenderer<SecurityRoleGroups>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, SecurityRoleGroups roleGroups,
				int count) throws Exception {

			Listcell listCell;

			listCell = new Listcell(roleGroups.getLovDescGrpCode());
			listCell.setParent(item);

			listCell = new Listcell(roleGroups.getLovDescGrpDesc());
			listCell.setParent(item);
			item.setAttribute("data", roleGroups);
			item.addForward("onDoubleClick", win_Operation_Roles_Dialog,
					"onSecurityGroupItemDoubleClicked", item);
		}
	}

	/**
	 * This method Renders the listBox
	 * 
	 * @param listbox
	 * @param SecurityUserRoles
	 */
	public static class GroupRightListModelItemRenderer implements
	ListitemRenderer<SecurityGroupRights>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, SecurityGroupRights groupRights,
				int count) throws Exception {

			Listcell listCell;

			listCell = new Listcell(groupRights.getLovDescRightName());
			listCell.setParent(item);

			item.setAttribute("data", groupRights);
		}
	}
	/**
	 * This method used when search button is clicked
	 */
	public void filterRoles(int filterCode, String filterValue) {

		this.filterCode = filterCode;
		this.filterValue = filterValue;

		List<SecurityOperationRoles> unassignedList = new ArrayList<SecurityOperationRoles>();
		if (filterCode == -1) {
			unassignedList = new ArrayList<SecurityOperationRoles>(
					unAssignedHashMap.values());
		} else {
			for (SecurityOperationRoles oprRoles : unAssignedHashMap.values()) {

				switch (filterCode) {
				case Filter.OP_EQUAL:
					if (oprRoles.getLovDescRoleCd().equals(filterValue)) {
						unassignedList.add(oprRoles);
					}
					break;
				case Filter.OP_NOT_EQUAL:
					if (!oprRoles.getLovDescRoleCd().equals(filterValue)) {
						unassignedList.add(oprRoles);
					}
					break;
				case Filter.OP_LIKE:
					if (oprRoles.getLovDescRoleCd().contains(filterValue)) {
						unassignedList.add(oprRoles);
					}	
					break;
				default:	
					
				}
				
			}
		}

		refreshListBox(getUnAssigneListWrapper(), unassignedList,
				this.listbox_UnAssignedRoles);
	}

	private void refreshListBox(PagedListWrapper<SecurityOperationRoles> listWrapper,List<SecurityOperationRoles> oprRoles, Listbox listBox) {
		Paging paging=   new Paging();

		if(oprRoles.size() > 0) {
			paging.setPageSize(oprRoles.size());
		}
		listWrapper.initList(oprRoles, listBox, paging);
		listBox.setItemRenderer(new SecurityUserRoleListModelItemRenderer());
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.securityOperation.getOprID());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * This method creates and returns AuditHeader Object
	 * 
	 * @param aSecurityOperation
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityOperation aSecurityOperation,
			String tranType) {
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aSecurityOperation.getBefImage(), aSecurityOperation);

		AuditHeader auditHeader = new AuditHeader(String.valueOf(aSecurityOperation
				.getId()), null, null, null, auditDetail,
				aSecurityOperation.getUserDetails(), getOverideMap());
		auditHeader.setAuditModule(SecurityOperationRoles.class.getSimpleName());
		return auditHeader;
	}

	/**
	 * This method shows error message
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.win_Operation_Roles_Dialog,
					auditHeader);
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
		doShowNotes(this.securityOperation);
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public SecurityOperation getSecurityOperation() {
		return securityOperation;
	}

	public void setSecurityOperation(SecurityOperation securityOperation) {
		this.securityOperation = securityOperation;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityOperationRoles> getAssigneListWrapper() {
		if (this.assigneListWrapper == null) {
			this.assigneListWrapper = (PagedListWrapper<SecurityOperationRoles>) SpringUtil
					.getBean("pagedListWrapper");
			
		}

		return assigneListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityOperationRoles> getUnAssigneListWrapper() {
		if (this.unAssigneListWrapper == null) {
			this.unAssigneListWrapper = (PagedListWrapper<SecurityOperationRoles>) SpringUtil
					.getBean("pagedListWrapper");
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
			this.roleGroupsListWrapper = (PagedListWrapper<SecurityRoleGroups>) SpringUtil
					.getBean("pagedListWrapper");
		}
		return roleGroupsListWrapper;
	}

	public void setGroupRightsListWrapper(
			PagedListWrapper<SecurityGroupRights> groupRightsListWrapper) {
		this.groupRightsListWrapper = groupRightsListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityGroupRights> getGroupRightsListWrapper() {
		if (this.groupRightsListWrapper == null) {
			this.groupRightsListWrapper = (PagedListWrapper<SecurityGroupRights>) SpringUtil
					.getBean("pagedListWrapper");
		}
		return groupRightsListWrapper;
	}

	public SecurityOperationRolesService getSecurityOperationRolesService() {
		return securityOperationRolesService;
	}

	public void setSecurityOperationRolesService(
			SecurityOperationRolesService securityOperationRolesService) {
		this.securityOperationRolesService = securityOperationRolesService;
	}

	public SecurityOperationRolesListCtrl getOperationRolesListCtrl() {
		return operationRolesListCtrl;
	}

	public void setOperationRolesListCtrl(
			SecurityOperationRolesListCtrl operationRolesListCtrl) {
		this.operationRolesListCtrl = operationRolesListCtrl;
	}

	public JdbcSearchObject<SecurityOperationRoles> getSearchObj() {
		return searchObj;
	}

	public void setSearchObj(JdbcSearchObject<SecurityOperationRoles> searchObj) {
		this.searchObj = searchObj;
	}


}
