
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
 * FileName    		:SecurityUserRoleDailogCtrl.java                                        *
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
package com.pennant.webui.administration.securityuserroles;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserRolesService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.administration.securityuser.SecurityUserDialogCtrl;
import com.pennant.webui.administration.securityuser.SecurityUserListCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityUsersroles/SecurityUserRolesDailog.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityUserRolesDailogCtrl extends GFCBaseListCtrl<SecurityRole> implements Serializable {

	private static final long serialVersionUID = 4149506032336052235L;
	private final static Logger logger = Logger.getLogger(SecurityUserRolesDailogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window        win_SecurityUserRolesDialog;                // autoWired
	
	protected Borderlayout  borderLayout_SecurityUsersRoles;            // autoWired
	protected Borderlayout  borderlayout_SecurityRoleGroups;            // autoWired
	protected Listbox       listbox_UnAssignedRoles;                    // autoWired
	protected Listbox       listbox_SecurityRoleGroups;                 // autoWired
	protected Listbox       listbox_SecurityGroupRights;                // autoWired
	protected Listbox       listbox_AssignedRoles;                      // autoWired
	protected Panel         panel_SecurityRoleGroups;                   // autoWired
	protected Panel         panel_SecurityGroupRights;                  // autoWired
	
	protected Button        btnSave;                                    // autoWired
	protected Button        btnRefresh;                                 // autoWired
	protected Button        btnClose;                                   // autoWired
	protected Button        btnSelectRoles;                             // autoWired
	protected Button        btnUnSelectRoles;                           // autoWired
	protected Button        btnUnSelectAllRoles;                        // autoWired
	protected Button        btn_SearchRoles;                            // autoWired
	
	protected Label         label_UserLogin;                            // autoWired
	protected Label         label_UserDept;                             // autoWired
	protected Label         label_FirstName;                            // autoWired
	protected Label         label_MiddleName;                           // autoWired
	protected Label         label_LastName;                             // autoWired
	
	protected Button        btnNew;                                     // autoWired
	protected Button        btnEdit;                                    // autoWired
	protected Button        btnDelete;                                  // autoWired
	protected Button        btnCancel;                                  // autoWired
	protected Button        btnHelp;                                    // autoWired
	protected Button        btnNotes;                                   // autoWired
	
	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;
	private boolean notes_Entered=false;
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SecurityUserRolesDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	private transient WorkFlowDetails	 workFlowDetails = null;
	
	//private variables and service classes
	private transient SecurityUserRolesService     securityUserRolesService;
	private SecurityUser             securityUser;
	private SecurityUserDialogCtrl securityUserDialogCtrl;
	private SecurityUserRoles        securityUserRoles;
	private HashMap<Long, SecurityUserRoles> assignedHashMap;
	private HashMap<Long, SecurityUserRoles> unAssignedHashMap;

	private HashMap<Long, SecurityUserRoles> addHashMap=null;
	private HashMap<Long, SecurityUserRoles> delHashMap=null;
	private HashMap<Long, SecurityUserRoles> cancilHashMap=null;
	

	private PagedListWrapper<SecurityUserRoles>    assigneListWrapper;
	private PagedListWrapper<SecurityUserRoles>    unAssigneListWrapper;
	private PagedListWrapper<SecurityRoleGroups>    roleGroupsListWrapper;
	private PagedListWrapper<SecurityGroupRights>    groupRightsListWrapper;
	private transient SecurityUserListCtrl securityUserListCtrl; 

	private int filterCode=-1;
	private String filterValue="";	
	
	/**
	 * default constructor.<br>
	 */
	public SecurityUserRolesDailogCtrl() {
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityUsers object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$win_SecurityUserRolesDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		try{
			/* set components visible dependent of the users rights */
			final Map<String, Object> args = getCreationArgsMap(event);
			
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}
		
		// get the parameters map that are over handed by creation.
		if (args.containsKey("securityUserListCtrl")) {
			setSecurityUserListCtrl((SecurityUserListCtrl) args.get("securityUserListCtrl"));
		} else {
			setSecurityUserListCtrl(null);
		}
		

		// get the parameters map that are over handed by creation.
		if (args.containsKey("securityUser")) {
			setSecurityUser((SecurityUser) args.get("securityUser"));
		} else {
			setSecurityUser(null);
		}
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SecurityUserRoles");
		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityUserRoles");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		}
		
		
		doLoadWorkFlow(this.securityUser.isWorkflow(),this.securityUser.getWorkflowId(),this.securityUser.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "SecurityUserRolesDialog");
		}else{
			getUserWorkspace().alocateAuthorities("SecurityUserRolesDialog");
			
		}
		
		doCheckRights();
	
		doShowDialog();
		setDialog(this.win_SecurityUserRolesDialog);
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			win_SecurityUserRolesDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when "save" button is clicked
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
		PTMessageUtils.showHelpWindow(event, this.win_SecurityUserRolesDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when "close" button is clicked
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		try{
			doClose();
		}catch (final Exception e) {
			logger.debug("Error Occured while closing"+e.toString());
			closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRolesDialog"); //SecurityUserRoles
		}
		logger.debug("Leaving " + event.toString());
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * @throws Exception 
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering ");
		doSetPanelProperties();
		
		displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),getSecurityUser().isNewRecord()));
		
		// set the paging parameters	
		this.listbox_UnAssignedRoles.getItems().clear();	
		this.label_UserDept.setValue(getSecurityUser().getUsrDeptCode());
		this.label_FirstName.setValue(getSecurityUser().getUsrFName());
		this.label_MiddleName.setValue(getSecurityUser().getUsrMName());
		this.label_LastName.setValue(getSecurityUser().getUsrLName());
		this.label_UserLogin.setValue(String.valueOf(getSecurityUser().getUsrLogin()));
		
		assignedHashMap = new HashMap<Long, SecurityUserRoles>();
		unAssignedHashMap= new HashMap<Long, SecurityUserRoles>();

		for (SecurityUserRoles detail:getSecurityUser().getSecurityUserRolesList()) {
			Long key = detail.getRoleID();
			
			if(PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType())){
				unAssignedHashMap.put(key, detail);
				if(delHashMap==null){
					delHashMap= new HashMap<Long, SecurityUserRoles>();
				}

				delHashMap.put(key, detail);
			}else{
				assignedHashMap.put(key, detail);
				if(PennantConstants.RECORD_TYPE_NEW.equals(detail.getRecordType())){
					if(addHashMap==null){
						addHashMap= new HashMap<Long, SecurityUserRoles>();
					}
					addHashMap.put(key, detail);					
				}
			}
		}

		List<SecurityRole> securityRoles = getSecurityUserRolesService().getApprovedRoles();
		
		for (SecurityRole roles:securityRoles) {
			
			Long key = roles.getRoleID();
			
			if(!assignedHashMap.containsKey(key) && !unAssignedHashMap.containsKey(key)){
				SecurityUserRoles detail= new SecurityUserRoles();
				detail.setUsrID(getSecurityUser().getUsrID());
				detail.setRoleID(roles.getRoleID());
				detail.setLovDescRoleCd(roles.getRoleCd());
				detail.setLovDescRoleDesc(roles.getRoleDesc());
				unAssignedHashMap.put(key, detail);
			}
		}
		
		doWriteBeanToComponents(securityUser);

		logger.debug("Leaving ");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	private void displayComponents(int mode){
		logger.debug("Entering");
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.listbox_UnAssignedRoles,this.listbox_UnAssignedRoles));
		
		if (!StringUtils.trimToEmpty(getSecurityUser().getRecordType()).equals("")){
			this.btnNotes.setVisible(true);
		}else{
			this.btnNotes.setVisible(false);
		}
			
		logger.debug("Leaving");
	} 
	
	public void doReadOnly(boolean readOnly) {
		int accessType = 1;
		
		if(isWorkFlowEnabled()){
			accessType = getUserWorkspace().getAccessType("SecurityUserRolesDialog_SelectRole");	
		}
		
		if(readOnly || accessType!=1){
			this.btnSelectRoles.setVisible(true); //false
			this.btnUnSelectRoles.setVisible(true); //false
			this.btnUnSelectAllRoles.setVisible(true); //false
		}else{
			this.btnSelectRoles.setVisible(true);
			this.btnUnSelectRoles.setVisible(true);
			this.btnUnSelectAllRoles.setVisible(true);
		}
		if(getSecurityUser().getRecordStatus() != null){
		if(getSecurityUser().getRecordStatus().equals("Submitted") || 
				(getSecurityUser().getRecordStatus().equals("Saved") && getSecurityUser().getNextRoleCode().equals("MSTGRP1_APPROVER"))) {
			this.btnSelectRoles.setVisible(false);
			this.btnUnSelectRoles.setVisible(false);
			this.btnUnSelectAllRoles.setVisible(false);
		}
		}
	}
	
	/**
	 * This method sets panel properties
	 */
	public void doSetPanelProperties(){
		logger.debug("Entering ");
		this.panel_SecurityRoleGroups.setOpen(false);
		this.panel_SecurityGroupRights.setOpen(false);
		this.listbox_SecurityRoleGroups.getItems().clear();
		this.listbox_SecurityGroupRights.getItems().clear();	
		this.panel_SecurityRoleGroups.setTitle(Labels.getLabel("panel_RoleGroups.title"));
		this.panel_SecurityGroupRights.setTitle(Labels.getLabel("panel_GroupRights.title"));
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
		this.btnSave.setVisible(true);//getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnCancel"));
		
		if(getUserWorkspace().isAllowed("SecurityUserRolesDialog_SelectRole")){
			this.btnSelectRoles.setVisible(true);
			this.btnUnSelectAllRoles.setVisible(true);
			this.btnUnSelectRoles.setVisible(true);
		}else{
			this.btnSelectRoles.setVisible(false);
			this.btnUnSelectAllRoles.setVisible(false);
			this.btnUnSelectRoles.setVisible(false);

		}
		
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		
		logger.debug("Leaving ");
	}

	public void doWriteBeanToComponents(SecurityUser aSecurityUser) {

		fillListBoxWithData(new ArrayList<SecurityUserRoles>(assignedHashMap.values()), this.listbox_AssignedRoles);
		fillListBoxWithData(new ArrayList<SecurityUserRoles>(unAssignedHashMap.values()), this.listbox_UnAssignedRoles);

		this.recordStatus.setValue(aSecurityUser.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aSecurityUser.getRecordType()));
	}
	
	/**
	 * This method do the following 
	 * 1)compare oldAssigned map and new assigned map 
	 *    a)if roleId not in oldselectedMap and in new selectedMap creates new SecurityUserRoles
	 *     Object, sets data and add it to SecurityUser LovDescAssignedRoles 
	 *    b)if roleId  in oldselectedMap and not in new selectedMap gets the SecurityUserRoles
	 *       from back end , sets RecordStatus "DELETE" add it to SecurityUser LovDescAssignedRoles 
	 */
	public void doWriteComponentsToBean(SecurityUser aSecurityUser){

		List<SecurityUserRoles> secUserRoles= new ArrayList<SecurityUserRoles>();
		
		if(addHashMap!=null && !addHashMap.isEmpty()){
			
			Iterator<SecurityUserRoles> addList = addHashMap.values().iterator();
			
			while(addList.hasNext()){
				SecurityUserRoles securityUserRoles=addList.next();
				secUserRoles.add(securityUserRoles);
			}
		}

		if(delHashMap!=null && !delHashMap.isEmpty()){
			
			Iterator<SecurityUserRoles> delList = delHashMap.values().iterator();
			
			while(delList.hasNext()){
				SecurityUserRoles securityUserRoles=delList.next();
				
				secUserRoles.add(securityUserRoles);
			}
		}
		if(cancilHashMap!=null && !cancilHashMap.isEmpty()){
			
			Iterator<SecurityUserRoles> cancilList = cancilHashMap.values().iterator();
			
			while(cancilList.hasNext()){
				SecurityUserRoles securityUserRoles=cancilList.next();
				
				secUserRoles.add(securityUserRoles);
			}
		}
		aSecurityUser.setSecurityUserRolesList(secUserRoles);
	}

	/**
	 * This method checks whether data changed or not 
	 * @return true If changed ,otherwise false
	 */
	public boolean isdatachanged() {
		logger.debug("Entering ");
	
		logger.debug("Leaving ");
		return false;
	}
	
	/**
	 * Refreshes the list
	 */
	private void refreshList(){
		logger.debug("Entering ");
		
		final JdbcSearchObject<SecurityUser> soSecUser = getSecurityUserListCtrl().getSearchObj();
		getSecurityUserListCtrl().getPagingSecurityUserList().setActivePage(0);
		getSecurityUserListCtrl().getPagedListWrapper().setSearchObject(soSecUser);
		if (getSecurityUserListCtrl().getListBoxSecurityUser() != null) {
			getSecurityUserListCtrl().getListBoxSecurityUser().getListModel();
		}
		
		logger.debug("Leaving ");
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
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aSecurityUser.getRecordType()) && isValidation()) {
			//doSetValidation();
			// fill the Branch object with the components data
			doWriteComponentsToBean(aSecurityUser);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aSecurityUser.isNew();
		String tranType="";
		
		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSecurityUser.getRecordType()).equals("")){
				aSecurityUser.setVersion(aSecurityUser.getVersion()+1);
				if(isNew){
					aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityUser.setNewRecord(true);
				}
			}
		}else{
			aSecurityUser.setVersion(aSecurityUser.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			/*if(aSecurityUser.getSecurityUserRolesList().size()==0){
				closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRoles");
			}else{*/
				
				if(doProcess(aSecurityUser,tranType)){
					//doWriteComponentsToBean();
					refreshList();
					closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRolesDialog"); //SecurityUser
				}
			//}
			

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aSecurityUserRoles
	 *            (SecurityUserRoles)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	
	private boolean doProcess(SecurityUser aSecurityUser,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aSecurityUser.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSecurityUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityUser.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aSecurityUser.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityUser.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSecurityUser);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aSecurityUser))) {
					try {
						if (!isNotes_Entered()){
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

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aSecurityUser.setTaskId(taskId);
			aSecurityUser.setNextTaskId(nextTaskId);
			aSecurityUser.setRoleCode(getRole());
			aSecurityUser.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSecurityUser, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aSecurityUser);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aSecurityUser, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aSecurityUser, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}	
	/**	
	 * Get the result after processing DataBase Operations 
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method) {
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		SecurityUser aSecurityUser=(SecurityUser)auditHeader.getAuditDetail().getModelData();
		
		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					auditHeader = getSecurityUserRolesService().saveOrUpdate(auditHeader);
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getSecurityUserRolesService().doApprove(auditHeader);

						if(aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getSecurityUserRolesService().doReject(auditHeader);
						if(aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999
								, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.win_SecurityUserRolesDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.win_SecurityUserRolesDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
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
	public void doClose() throws  InterruptedException {

		logger.debug("Entering ");
		boolean close = true;
		// before close check whether data changed.
		if (isdatachanged()) {
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,MultiLineMessageBox.YES
					| MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		}
		if(close){
			closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRolesDialog"); //SecurityUserRoles
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method cancels the previous operations
	 * @throws Exception
	 */
	private void doCancel() throws Exception{
	/*	tempUnAsgnRoleMap.clear();
		newAssignedMap.clear();
		unAssignedRoleList=tempUnAssignedRoleList;
		doShowDialog();*/
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ OnClick Events+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when "reset" button is clicked
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		fillListBoxWithData( new ArrayList<SecurityUserRoles>(unAssignedHashMap.values()), this.listbox_UnAssignedRoles);
		logger.debug("Leaving " + event.toString());
	}	
	
	/**
	 * when clicks on "btn_SearchUnAssignedRoles"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btn_SearchRoles(Event event)throws Exception{
		logger.debug("Entering " + event.toString());
		logger.debug("Entering " + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SecurityUserRolesDialogCtrl", this);   //dialogCtrl
		map.put("FILTERTYPE","USERROLE");
		map.put("FILTERCODE",filterCode);
		map.put("FILTERVALUE",filterValue);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul",this.win_SecurityUserRolesDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 *  when clicks on "btnSelectRoles"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSelectRoles(Event event) throws Exception {
		logger.debug(event.toString());			
		setAssignedRoles();
		
	}
	
	/**
	 * when clicks on "btnUnSelectRoles"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnUnSelectRoles(Event event) throws Exception {
		logger.debug(event.toString());		
		setUnAssignedRoles();

	}

	/**
	 * when clicks on "btnUnSelectRoles"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnUnSelectAllRoles(Event event) throws Exception {
		logger.debug(event.toString());
		if(this.listbox_AssignedRoles .getItemCount() > 0){
			this.listbox_AssignedRoles.setMultiple(true);
			this.listbox_AssignedRoles.selectAll();
		}
		setUnAssignedRoles();
		
	}

	private void setAssignedRoles(){

		Set<Listitem> set = this.listbox_UnAssignedRoles.getSelectedItems();
		Iterator<Listitem> selecctedItems =  set.iterator();
		
		while(selecctedItems.hasNext()){
			Listitem item = selecctedItems.next();
			SecurityUserRoles detail =  (SecurityUserRoles) item.getAttribute("data");
			Long key = detail.getRoleID();
			
			String recStatus = StringUtils.trimToEmpty(detail.getRecordStatus());
			String recType = StringUtils.trimToEmpty(detail.getRecordType());
			
			if (recType.equals("") && recStatus.equals("")) {
				detail.setNewRecord(true);
				detail.setRecordStatus(PennantConstants.RCD_ADD);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				assignedHashMap.put(key, detail);
				unAssignedHashMap.remove(key);
				if(addHashMap==null){
					addHashMap= new HashMap<Long, SecurityUserRoles>();
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
					cancilHashMap= new HashMap<Long, SecurityUserRoles>();
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
		
		fillListBoxWithData( new ArrayList<SecurityUserRoles>(assignedHashMap.values()), this.listbox_AssignedRoles);
		fillListBoxWithData( new ArrayList<SecurityUserRoles>(unAssignedHashMap.values()), this.listbox_UnAssignedRoles);
	}
	
	private void setUnAssignedRoles(){
		logger.debug("Entering");
		
		Set<Listitem> set = this.listbox_AssignedRoles.getSelectedItems();
		Iterator<Listitem> selecctedItems =  set.iterator();
		
		while(selecctedItems.hasNext()){
			Listitem item = selecctedItems.next();
			SecurityUserRoles detail =  (SecurityUserRoles) item.getAttribute("data");
			if(detail != null){
			Long key = detail.getRoleID();
			
			
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
					cancilHashMap= new HashMap<Long, SecurityUserRoles>();
				}
				cancilHashMap.put(key, detail);
				addHashMap.remove(key);
			}
			
			
			if (recType.equals("") && recStatus.equals(PennantConstants.RCD_STATUS_APPROVED)) {
				detail.setNewRecord(true);
				detail.setRecordStatus(PennantConstants.RCD_ADD);
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				unAssignedHashMap.put(key, detail);
				assignedHashMap.remove(key);
				if(delHashMap==null){
					delHashMap= new HashMap<Long, SecurityUserRoles>();
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
		fillListBoxWithData( new ArrayList<SecurityUserRoles>(assignedHashMap.values()), this.listbox_AssignedRoles);
		fillListBoxWithData( new ArrayList<SecurityUserRoles>(unAssignedHashMap.values()), this.listbox_UnAssignedRoles);

		logger.debug("Leaving");
	}
	
	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityRoleItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering ");
		this.panel_SecurityRoleGroups.setOpen(true);
		doShowRoleGroups(event);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This  method display the list of groups Assigned to selected role
	 * 
	 * @throws InterruptedException
	 */
	public void doShowRoleGroups(ForwardEvent event) throws InterruptedException {

		logger.debug("Entering ");
		
		Listitem item=(Listitem) event.getOrigin().getTarget();

		if (item != null) {
			SecurityUserRoles  aSecurityUserRoles= (SecurityUserRoles) item.getAttribute("data");
			this.panel_SecurityRoleGroups.setTitle(Labels.getLabel("listbox_SecurityUserRoleGroups.value") + " - "+ aSecurityUserRoles.getLovDescRoleCd());
			this.listbox_SecurityRoleGroups.setVisible(true);
			this.listbox_SecurityRoleGroups.getItems().clear();
			this.listbox_SecurityGroupRights.getItems().clear();
			this.panel_SecurityGroupRights.setOpen(false);
			this.panel_SecurityGroupRights.setTitle(Labels.getLabel("listbox_SecurityUserRoleGroupRights.value"));

			List<SecurityRoleGroups> roleGroupsList=getSecurityUserRolesService().getApprovedRoleGroupsByRoleId(aSecurityUserRoles.getRoleID());
			
			getRoleGroupsListWrapper().initList(new ArrayList<SecurityRoleGroups>(roleGroupsList) ,this.listbox_SecurityRoleGroups, new Paging());
			this.listbox_SecurityRoleGroups.setItemRenderer(new SecurityGroupListModelItemRenderer());
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
	 * This method   display the list of rights assigned to selected group.
	 * @throws InterruptedException
	 */
	public void doShowGroupsRights() throws InterruptedException {

		logger.debug("Entering ");
		
		this.listbox_SecurityGroupRights.setVisible(true);
		this.listbox_SecurityGroupRights.getItems().clear();
		final Listitem item = this.listbox_SecurityRoleGroups.getSelectedItem();
		SecurityRoleGroups SecurityRoleGroups = new SecurityRoleGroups();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			SecurityRoleGroups = (SecurityRoleGroups) item.getAttribute("data");
		}
		this.panel_SecurityGroupRights.setTitle(Labels.getLabel("listbox_SecurityUserRoleGroupRights.value") + " - "+ SecurityRoleGroups.getLovDescGrpCode());

		SecurityGroup secGroups=new SecurityGroup();
		secGroups.setGrpID(SecurityRoleGroups.getGrpID());
		List<SecurityGroupRights> grpRightsList=getSecurityUserRolesService().getGroupRightsByGrpId(secGroups);
		
		if(grpRightsList.size() > 0){
		getGroupRightsListWrapper().initList(grpRightsList ,this.listbox_SecurityGroupRights, new Paging(grpRightsList.size(),grpRightsList.size()));
		this.listbox_SecurityGroupRights.setItemRenderer(new GroupRightListModelItemRenderer());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method displays  the filtered data in unAssigned Roles panel .
	 * @param searchResult
	 */
	public int doShowSearchResult(Object[] searchResult){
		logger.debug("Entering ");
		filterRoles((Integer)searchResult[0], (String)searchResult[1]);
		
		logger.debug("Leaving ");
		return listbox_UnAssignedRoles.getItemCount();
	}



//	/**
//	 * This method Renders the listBox 
//	 * @param listbox
//	 * @param SecurityUserRoles
//	 */
//	public class SecurityUserRoleListModelItemRenderer implements ListitemRenderer<SecurityUserRoles>, Serializable {
//
//		private static final long serialVersionUID = 1L;
//		@Override
//		public void render(Listitem item, SecurityUserRoles userRoles,int count) throws Exception {
//
//			Listcell listCell;	
//			
//			listCell= new Listcell(userRoles.getLovDescRoleCd());
//			listCell.setParent(item);
//			
//			listCell = new Listcell(userRoles.getLovDescRoleDesc());
//			listCell.setParent(item);
//			item.setAttribute("data", userRoles);
//			item.addForward("onDoubleClick", win_SecurityUserRolesDialog, "onSecurityRoleItemDoubleClicked", item);
//		}
//	}

	/**
	 * This method Renders the listBox 
	 * @param listbox
	 * @param SecurityUserRoles
	 */
	public class SecurityGroupListModelItemRenderer implements ListitemRenderer<SecurityRoleGroups>, Serializable {

		private static final long serialVersionUID = 1L;
		@Override
		public void render(Listitem item, SecurityRoleGroups roleGroups,int count) throws Exception {

			Listcell listCell;	
			
			listCell= new Listcell(roleGroups.getLovDescGrpCode());
			listCell.setParent(item);
			
			listCell = new Listcell(roleGroups.getLovDescRoleCode());
			listCell.setParent(item);
			item.setAttribute("data", roleGroups);
			item.addForward("onDoubleClick", win_SecurityUserRolesDialog, "onSecurityGroupItemDoubleClicked", item);
		}
	}
	
	/**
	 * This method Renders the listBox 
	 * @param listbox
	 * @param SecurityUserRoles
	 */
	public class GroupRightListModelItemRenderer implements ListitemRenderer<SecurityGroupRights>, Serializable {

		private static final long serialVersionUID = 1L;
		@Override
		public void render(Listitem item, SecurityGroupRights groupRights,int count) throws Exception {

			Listcell listCell;	
			
			listCell= new Listcell(groupRights.getLovDescRightName());
			listCell.setParent(item);
			
			System.out.println(groupRights.getLovDescRightName());
			
			item.setAttribute("data", groupRights);
		}
	}
	
	public void filterRoles(int filterCode, String filterValue){
		
		this.filterCode=filterCode;
		this.filterValue=filterValue;
		
		List<SecurityUserRoles> unassignedList= new ArrayList<SecurityUserRoles>();
		if(filterCode==-1){
			unassignedList = new ArrayList<SecurityUserRoles>(unAssignedHashMap.values());
		}else{
			for (SecurityUserRoles userRoles:unAssignedHashMap.values()) {
				
				switch (filterCode) {
					case Filter.OP_EQUAL:
						if(userRoles.getLovDescRoleCd().equals(filterValue)){
							unassignedList.add(userRoles);
						}
					break;
					case Filter.OP_NOT_EQUAL:
						if(!userRoles.getLovDescRoleCd().equals(filterValue)){
							unassignedList.add(userRoles);
						}
					break;
					case Filter.OP_LIKE:
						if(userRoles.getLovDescRoleCd().contains(filterValue)){
							unassignedList.add(userRoles);
						}
					break;
				}	
			}
		}

		fillListBoxWithData(unassignedList, this.listbox_UnAssignedRoles);
	}
	
//	private void refreshListBox(PagedListWrapper<SecurityUserRoles> listWrapper, List<SecurityUserRoles> userRoles,Listbox listBox) {
//		if(userRoles.size() > 0){
//			listWrapper.initList(userRoles, listBox, new Paging(userRoles.size(),userRoles.size()));
//		}else {
//			listWrapper.initList(userRoles, listBox, new Paging());
//		}
//		listWrapper.setMultiple(true);
//		listBox.setItemRenderer(new SecurityUserRoleListModelItemRenderer());
//	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * This method  creates and returns AuditHeader Object
	 * @param aSecurityRight
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(),aSecurityUser);   
		
		AuditHeader auditHeader = new AuditHeader(String.valueOf(aSecurityUser.getId()),null,null,null ,auditDetail,aSecurityUser.getUserDetails(),getOverideMap());
		auditHeader.setAuditModule(SecurityUserRoles.class.getSimpleName());
		return auditHeader ;
	}	

	/**
	 * This method shows error message
	 */
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.win_SecurityUserRolesDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("error "+e.toString());
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
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		
		// call the ZUl-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	
	
	// Get the notes entered for rejected reason
	private Notes getNotes(){
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("SecurityUser");
		notes.setReference(String.valueOf(getSecurityUser().getUsrID()));
		notes.setVersion(getSecurityUser().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public SecurityUser getSecurityUser() {
		return securityUser;
	}
	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	public SecurityUserRolesService getSecurityUserRolesService() {
		return securityUserRolesService;
	}
	public void setSecurityUserRolesService(
			SecurityUserRolesService securityUserRolesService) {
		this.securityUserRolesService = securityUserRolesService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityUserRoles> getAssigneListWrapper() {
		if(this.assigneListWrapper == null){
			this.assigneListWrapper = (PagedListWrapper<SecurityUserRoles>) SpringUtil.getBean("pagedListWrapper");;
		}
		
		return assigneListWrapper;
	}
	
	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityUserRoles> getUnAssigneListWrapper() {
		if(this.unAssigneListWrapper == null){
			this.unAssigneListWrapper = (PagedListWrapper<SecurityUserRoles>) SpringUtil.getBean("pagedListWrapper");;
		}

		return unAssigneListWrapper;
	}

	public void setRoleGroupsListWrapper(PagedListWrapper<SecurityRoleGroups> roleGroupsListWrapper) {
		this.roleGroupsListWrapper = roleGroupsListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityRoleGroups> getRoleGroupsListWrapper() {
		if(this.roleGroupsListWrapper == null){
			this.roleGroupsListWrapper = (PagedListWrapper<SecurityRoleGroups>) SpringUtil.getBean("pagedListWrapper");;
		}
		return roleGroupsListWrapper;
	}

	public void setGroupRightsListWrapper(PagedListWrapper<SecurityGroupRights> groupRightsListWrapper) {
		this.groupRightsListWrapper = groupRightsListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SecurityGroupRights> getGroupRightsListWrapper() {
		if(this.groupRightsListWrapper == null){
			this.groupRightsListWrapper = (PagedListWrapper<SecurityGroupRights>) SpringUtil.getBean("pagedListWrapper");;
		}
		return groupRightsListWrapper;
	}

	public void setSecurityUserRoles(SecurityUserRoles securityUserRoles) {
		this.securityUserRoles = securityUserRoles;
	}

	public SecurityUserRoles getSecurityUserRoles() {
		return securityUserRoles;
	}

	public void setSecurityUserDialogCtrl(SecurityUserDialogCtrl securityUserDialogCtrl) {
		this.securityUserDialogCtrl = securityUserDialogCtrl;
	}

	public SecurityUserDialogCtrl getSecurityUserDialogCtrl() {
		return securityUserDialogCtrl;
	}

	public void setSecurityUserListCtrl(SecurityUserListCtrl securityUserListCtrl) {
		this.securityUserListCtrl = securityUserListCtrl;
	}

	public SecurityUserListCtrl getSecurityUserListCtrl() {
		return securityUserListCtrl;
	}
	
	
	private void fillListBoxWithData(List<SecurityUserRoles> userRoles, Listbox listbox) {
		listbox.getItems().clear();
		if (userRoles!=null && !userRoles.isEmpty()) {
			for (SecurityUserRoles securityUserRoles : userRoles) {
				Listitem item = new Listitem();
				Listcell listCell;
				listCell = new Listcell(securityUserRoles.getLovDescRoleCd());
				listCell.setParent(item);

				listCell = new Listcell(securityUserRoles.getLovDescRoleDesc());
				listCell.setParent(item);
				item.setAttribute("data", securityUserRoles);
				item.addForward("onDoubleClick", win_SecurityUserRolesDialog,
						"onSecurityRoleItemDoubleClicked", item);
				listbox.appendChild(item);
			}
		}

	}

}
