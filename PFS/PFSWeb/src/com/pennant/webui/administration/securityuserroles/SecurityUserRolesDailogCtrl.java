
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserRolesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

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
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SecurityUserRolesDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	//private variables and service classes
	private transient SecurityUserRolesService     securityUserRolesService;
	private SecurityUser             securityUser;
	private Map<Long, SecurityRole> newAssignedMap = new HashMap<Long, SecurityRole>();
	private Map<Long, SecurityRole> oldAssignedMap = new HashMap<Long, SecurityRole>();
	private List<SecurityRole>      assignedRoleList  = new ArrayList<SecurityRole>();
	private List<SecurityRole>      unAssignedRoleList=new ArrayList<SecurityRole>();
	private List<SecurityRole>      tempUnAssignedRoleList=new ArrayList<SecurityRole>(); 
	private Map<Long, SecurityRole> tempUnAsgnRoleMap  =new HashMap<Long, SecurityRole>();
	private Map<Long, SecurityUserRoles> 		   selectedMap ;
	private Map<Long, SecurityUserRoles> 		   deletedMap ;
	private Object filters[]=new Object[2];

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

		/* set components visible dependent of the users rights */
		doCheckRights();

		final Map<String, Object> args = getCreationArgsMap(event);
		// get the parameters map that are over handed by creation.
		if (args.containsKey("securityUser")) {
			setSecurityUser((SecurityUser) args.get("securityUser"));

		} else {
			setSecurityUser(null);
		}
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
		this.borderLayout_SecurityUsersRoles.setHeight(getBorderLayoutHeight());
		// set the paging parameters	
		this.listbox_UnAssignedRoles.getItems().clear();	
		this.label_UserDept.setValue(getSecurityUser().getUsrDeptCode());
		this.label_FirstName.setValue(getSecurityUser().getUsrFName());
		this.label_MiddleName.setValue(getSecurityUser().getUsrMName());
		this.label_LastName.setValue(getSecurityUser().getUsrLName());
		this.label_UserLogin.setValue(String.valueOf(getSecurityUser().getUsrLogin()));
		/*get all assigned role*/
		assignedRoleList=getSecurityUserRolesService().getRolesByUserId(getSecurityUser().getUsrID(),true);
		/*get all unAssigned role*/
		unAssignedRoleList=getSecurityUserRolesService().getRolesByUserId(getSecurityUser().getUsrID(),false);
		tempUnAssignedRoleList=unAssignedRoleList;
		doShowDialog();
		setDialog(this.win_SecurityUserRolesDialog);
		logger.debug("Leaving " + event.toString());
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
			closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRoles");
		}
		logger.debug("Leaving " + event.toString());
	}

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
	 * when "reset" button is clicked
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSetPanelProperties();
		this.listbox_UnAssignedRoles.getItems().clear();
		doShowUnAssignedRolesList();
		logger.debug("Leaving " + event.toString());

	}	
	/**
	 * when clicks on "btn_SearchUnAssignedRoles"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btn_SearchRoles(Event event) 
	throws Exception{
		logger.debug("Entering " + event.toString());
		doSetPanelProperties();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dialogCtrl", this);
		map.put("dataMap",tempUnAsgnRoleMap);
		map.put("prevFilters", filters);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}		logger.debug("Leaving " + event.toString());

	}

	/**
	 *  when clicks on "btnSelectRoles"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnSelectRoles(Event event) throws Exception {
		logger.debug(event.toString());			

		if(this.listbox_UnAssignedRoles.getSelectedCount()!=0){	

			Listitem li=new Listitem();             //To read List Item
			Set SeletedSet= new HashSet();           //To get Selected Items
			SeletedSet=this.listbox_UnAssignedRoles.getSelectedItems();
			List list=new ArrayList(SeletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				li=(Listitem)it.next();		
				final SecurityRole aSecurityRole= (SecurityRole)li.getAttribute("data");
				Listcell slecteditem=new Listcell();
				Listcell slecteditemDesc=new Listcell();					
				List SelectedRowValues=new ArrayList();//TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);						
				slecteditemDesc=(Listcell)SelectedRowValues.get(1);	
				tempUnAsgnRoleMap.remove(Long.valueOf(aSecurityRole.getRoleID()));
				getNewAssignedMap().put(Long.valueOf(aSecurityRole.getRoleID()), aSecurityRole);
				doFillListbox(this.listbox_AssignedRoles,slecteditem.getLabel(),slecteditemDesc.getLabel(),aSecurityRole);
				if(true){
					listbox_UnAssignedRoles.removeItemAt(li.getIndex());

				}
			}							
		}		
	}
	/**
	 * when clicks on "btnUnSelectRoles"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnUnSelectRoles(Event event) throws Exception {
		logger.debug(event.toString());			
		if(this.listbox_AssignedRoles.getSelectedCount()!=0){	

			Listitem li=new Listitem();                       //To read List Item
			Set SeletedSet= new HashSet();                    //To get Selected Items
			SeletedSet=this.listbox_AssignedRoles.getSelectedItems();		
			List list=new ArrayList(SeletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				li=(Listitem)it.next();	
				final SecurityRole aSecurityRole= (SecurityRole)li.getAttribute("data");
				Listcell slecteditem=new Listcell();
				Listcell slecteditemDesc=new Listcell();					
				List SelectedRowValues=new ArrayList();      //TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);						
				slecteditemDesc=(Listcell)SelectedRowValues.get(1);	
				tempUnAsgnRoleMap.put(Long.valueOf(aSecurityRole.getRoleID()),aSecurityRole);
				getNewAssignedMap().remove(Long.valueOf(aSecurityRole.getRoleID()));
				doFillListbox(this.listbox_UnAssignedRoles,slecteditem.getLabel(),slecteditemDesc.getLabel(),aSecurityRole);
				if(true){
					listbox_AssignedRoles.removeItemAt(li.getIndex());
				}
			}							
		}		
	}

	/**
	 * when clicks on "btnUnSelectRoles"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnUnSelectAllRoles(Event event) throws Exception {
		logger.debug(event.toString());		
		this.listbox_AssignedRoles.selectAll();
		if(this.listbox_AssignedRoles.getSelectedCount()!=0){	

			Listitem li=new Listitem();       //To read List Item
			Set SeletedSet= new HashSet();    //To get Selected Items
			SeletedSet=this.listbox_AssignedRoles.getSelectedItems();		
			List list=new ArrayList(SeletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				li=(Listitem)it.next();	
				final SecurityRole aSecurityRole= (SecurityRole)li.getAttribute("data");
				Listcell slecteditem=new Listcell();
				Listcell slecteditemDesc=new Listcell();					
				List SelectedRowValues=new ArrayList();//TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);						
				slecteditemDesc=(Listcell)SelectedRowValues.get(1);	
				tempUnAsgnRoleMap.put(Long.valueOf(aSecurityRole.getRoleID()),aSecurityRole);
				getNewAssignedMap().remove(Long.valueOf(aSecurityRole.getRoleID()));
				doFillListbox(this.listbox_UnAssignedRoles,slecteditem.getLabel(),slecteditemDesc.getLabel(),aSecurityRole);
				if(true){
					listbox_AssignedRoles.removeItemAt(li.getIndex());
				}
			}							
		}		
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
	 * This method displays  the filtered data in unAssigned Roles panel .
	 * @param searchResult
	 */
	@SuppressWarnings("unchecked")
	public void doShowSearchResult(Object[] searchResult){
		logger.debug("Entering ");
		List<Object> searchResultList=(List<Object>)searchResult[0];
		/*we get the last used filters from SecuritySearchDialogCtrl and we send these filters to
		 * SecuritySearchDialogCtrl on event  "onClick$btnSearchRights" for set previous search filters */
		filters=(Object[]) searchResult[1];
		this.listbox_UnAssignedRoles.getItems().clear();
		Comparator<Object> comp = new BeanComparator("roleCd");
		Collections.sort(searchResultList, comp);
		for(int i=0;i<searchResultList.size();i++){
			SecurityRole securityRole=(SecurityRole)searchResultList.get(i);
			doFillListbox(this.listbox_UnAssignedRoles, securityRole.getRoleCd()
					, securityRole.getRoleDesc() ,securityRole);	
		}
		logger.debug("Leaving ");
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
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
		getUserWorkspace().alocateAuthorities("SecurityUserRolesDialog");
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnCancel"));
		this.btnSelectRoles.setVisible(getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnSelectRoles"));
		this.btnUnSelectAllRoles.setVisible(getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnUnSelectAllRoles"));
		this.btnUnSelectRoles.setVisible(getUserWorkspace().isAllowed("button_SecurityUserRolesDialog_btnUnSelectRoles"));

		logger.debug("Leaving ");
	}
	/**
	 * Opens the Dialog window modal.
	 * @throws Exception 
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering ");
		doSetPanelProperties();
		this.listbox_AssignedRoles.getItems().clear();
		this.listbox_UnAssignedRoles.getItems().clear();

		for(SecurityRole secrRole:unAssignedRoleList){
			tempUnAsgnRoleMap.put(Long.valueOf(secrRole.getRoleID()),secrRole);
		}
		doShowAssignedRolesList();
		doShowUnAssignedRolesList();	

		logger.debug("Leaving ");
	}
	/**
	 *1) This method fetches list of Roles to Assigned users by calling 
	 * SecurityUsersRolesService 's getRolesByUserId() method 
	 * argument "true" indicate fetch only assigned roles
	 * 2)Renders all list of roles using doFillListbox();
	 */
	@SuppressWarnings("unchecked")
	public void doShowAssignedRolesList(){

		logger.debug("Entering ");
		SecurityRole securityRole=new SecurityRole();
		Comparator<SecurityRole> comp = new BeanComparator("roleCd");
		Collections.sort(assignedRoleList, comp);
		for(int i=0;i<assignedRoleList.size();i++){
			securityRole=(SecurityRole)assignedRoleList.get(i);
			oldAssignedMap.put(Long.valueOf(securityRole.getRoleID()),securityRole);
			doFillListbox(this.listbox_AssignedRoles, securityRole.getRoleCd()
					,securityRole.getRoleDesc() ,securityRole);
		}
		setOldAssignedMap(oldAssignedMap);
		getNewAssignedMap().putAll(oldAssignedMap);
		logger.debug("Leaving ");
	}

	/**
	 *1) This method fetches list of Roles to Assigned users by calling 
	 * SecurityUsersRolesService 's getRolesByUserId() method 
	 * argument "false" indicate fetch only UnAssigned roles
	 * 2)Renders all list of roles using doFillListbox();
	 */
	@SuppressWarnings("unchecked")
	public void doShowUnAssignedRolesList() throws Exception{

		logger.debug("Entering ");
		SecurityRole securityRole=new SecurityRole();
		unAssignedRoleList=new ArrayList<SecurityRole>(tempUnAsgnRoleMap.values());
		Comparator<SecurityRole> comp = new BeanComparator("roleCd");
		Collections.sort(unAssignedRoleList, comp);
		for(int i=0;i<unAssignedRoleList.size();i++){
			securityRole=(SecurityRole)unAssignedRoleList.get(i);
			doFillListbox(this.listbox_UnAssignedRoles, securityRole.getRoleCd()
					, securityRole.getRoleDesc() ,securityRole);	

		}
		logger.debug("Leaving ");
	}
	/**
	 * This  method display the list of groups Assigned to selected role
	 * 
	 * @throws InterruptedException
	 */

	@SuppressWarnings("unchecked")
	public void doShowRoleGroups(ForwardEvent event) throws InterruptedException {

		logger.debug("Entering ");
		Listitem item=(Listitem) event.getOrigin().getTarget();


		if (item != null) {
			SecurityRole  aSecurityRole= (SecurityRole) item.getAttribute("data");
			this.panel_SecurityRoleGroups.setTitle(Labels.getLabel("panel_RoleGroups.title")
					+ " - "+ aSecurityRole.getRoleCd());
			this.listbox_SecurityRoleGroups.setVisible(true);
			this.listbox_SecurityRoleGroups.getItems().clear();
			this.listbox_SecurityGroupRights.getItems().clear();
			this.panel_SecurityGroupRights.setOpen(false);
			this.panel_SecurityGroupRights.setTitle(Labels.getLabel("title_Panel_SecurityGroupRights.value"));

			List<SecurityRoleGroups> roleGroupsList=getSecurityUserRolesService()
			.getRoleGroupsByRoleId(aSecurityRole);
			Comparator<SecurityRoleGroups> comp = new BeanComparator("lovDescGrpCode");
			Collections.sort(roleGroupsList, comp);
			for(int i=0;i<roleGroupsList.size();i++){
				SecurityRoleGroups securityRoleGroups=(SecurityRoleGroups)roleGroupsList.get(i);
				Listitem listItem=new Listitem(); //To Create List item
				Listcell listCell;
				listCell=new Listcell();	
				listCell.setLabel(securityRoleGroups.getLovDescGrpCode());
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setLabel(securityRoleGroups.getLovDescGrpCode());
				listCell.setParent(listItem);	
				listItem.setAttribute("data", securityRoleGroups);
				ComponentsCtrl.applyForward(listItem, "onDoubleClick=onSecurityGroupItemDoubleClicked");
				this.listbox_SecurityRoleGroups.appendChild(listItem);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method   display the list of rights assigned to selected group.
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
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
		this.panel_SecurityGroupRights.setTitle(Labels.getLabel("panel_GroupRights.title")
				+ " - "+ SecurityRoleGroups.getLovDescGrpCode());

		SecurityGroup secGroups=new SecurityGroup();
		secGroups.setGrpID(SecurityRoleGroups.getGrpID());
		List<SecurityGroupRights> grpRightsList=getSecurityUserRolesService()
		.getGroupRightsByGrpId(secGroups);
		Comparator<SecurityGroupRights> comp = new BeanComparator("lovDescRightName");
		Collections.sort(grpRightsList, comp);
		for(int i=0;i<grpRightsList.size();i++){
			SecurityGroupRights secGroupRights=(SecurityGroupRights)grpRightsList.get(i);
			Listitem listItem=new Listitem(); //To Create List item
			Listcell listCell;
			listCell=new Listcell();	
			listCell.setLabel(secGroupRights.getLovDescRightName());
			listCell.setParent(listItem);
			listCell.setParent(listItem);	
			listItem.setAttribute("data", secGroupRights);	
			this.listbox_SecurityGroupRights.appendChild(listItem);
		}	
		logger.debug("Leaving ");
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
			closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRoles");
		}
		logger.debug("Leaving ");
	}
	/**
	 * This method do the following 
	 * 1)compare oldAssigned map and new assigned map 
	 *    a)if roleId not in oldselectedMap and in new selectedMap creates new SecurityUserRoles
	 *     Object, sets data and add it to SecurityUser LovDescAssignedRoles 
	 *    b)if roleId  in oldselectedMap and not in new selectedMap gets the SecurityUserRoles
	 *       from back end , sets RecordStatus "DELETE" add it to SecurityUser LovDescAssignedRoles 
	 */
	public void doWriteComponentsToBean(){
		selectedMap = new HashMap<Long, SecurityUserRoles>();
		deletedMap  = new HashMap<Long, SecurityUserRoles>();
		//for insert
		for (Object roleId : getNewAssignedMap().keySet()) {
			if (!getOldAssignedMap().containsKey(roleId)) {

				SecurityUserRoles aSecurityUserRoles=getSecurityUserRolesService().getSecurityUserRoles(); 
				aSecurityUserRoles.setUsrID(getSecurityUser().getUsrID());
				aSecurityUserRoles.setLovDescUserLogin(getSecurityUser().getUsrLogin());
				aSecurityUserRoles.setRoleID(getNewAssignedMap().get(roleId).getRoleID());
				aSecurityUserRoles.setLovDescRoleCd(getNewAssignedMap().get(roleId).getRoleCd());
				aSecurityUserRoles.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aSecurityUserRoles.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				aSecurityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aSecurityUserRoles.setNextRoleCode("");
				aSecurityUserRoles.setNextTaskId("");
				aSecurityUserRoles.setTaskId("");
				aSecurityUserRoles.setRoleCode("");
				selectedMap.put(aSecurityUserRoles.getRoleID(), aSecurityUserRoles);
			}
		}
		//for Delete
		for (Object roleId : getOldAssignedMap().keySet()) {
			if (!getNewAssignedMap().containsKey(roleId)) {

				SecurityUserRoles aSecurityUserRoles=getSecurityUserRolesService().getSecurityUserRoles();
				aSecurityUserRoles.setUsrID(getSecurityUser().getUsrID());
				aSecurityUserRoles.setLovDescUserLogin(getSecurityUser().getUsrLogin());
				aSecurityUserRoles.setLovDescRoleCd(getOldAssignedMap().get(roleId).getRoleCd());
				aSecurityUserRoles.setRoleID( getOldAssignedMap().get(roleId).getRoleID());
				aSecurityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aSecurityUserRoles.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aSecurityUserRoles.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				aSecurityUserRoles.setNextRoleCode("");
				aSecurityUserRoles.setNextTaskId("");
				aSecurityUserRoles.setTaskId("");
				aSecurityUserRoles.setRoleCode("");
				aSecurityUserRoles.setRecordStatus("");
				deletedMap.put(aSecurityUserRoles.getRoleID(), aSecurityUserRoles);
			}	
		} 

	}
	/**
	 * This method cancels the previous operations
	 * @throws Exception
	 */
	private void doCancel() throws Exception{
		tempUnAsgnRoleMap.clear();
		newAssignedMap.clear();
		unAssignedRoleList=tempUnAssignedRoleList;
		doShowDialog();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * This method Renders the listBox 
	 * @param listbox
	 * @param value1
	 * @param Value2
	 * @param SecurityRole
	 */
	private void doFillListbox(Listbox listbox,String value1,String Value2,SecurityRole SecurityRole){
		Listitem item=new Listitem(); //To Create List item
		Listcell listCell;
		listCell=new Listcell();	
		listCell.setLabel(value1);
		listCell.setParent(item);
		listCell = new Listcell();
		listCell.setLabel(Value2);
		listCell.setParent(item);	
		item.setAttribute("data", SecurityRole);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityRoleItemDoubleClicked");
		listbox.appendChild(item);
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
	 * This method checks whether data changed or not 
	 * @return true If changed ,otherwise false
	 */
	public boolean isdatachanged() {
		logger.debug("Entering ");
		//compare sizes ofNewAssignedMap and OldAssignedMap
		if (getNewAssignedMap().size() != getOldAssignedMap().size()) {
			return true;
		}
		//Compare for all keys are same in  NewAssignedMap and OldAssignedMap or not
		if (getNewAssignedMap().size() == getOldAssignedMap().size()) {
			for (Object key : getNewAssignedMap().keySet()) {
				if (!getOldAssignedMap().containsKey(key)) {
					return true;
				}
			}
		}
		logger.debug("Leaving ");
		return false;
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

	/**
	 * This method  creates and returns AuditHeader Object
	 * @param aSecurityRight
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {

		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage()
				,aSecurityUser);   
		return new AuditHeader(String.valueOf(aSecurityUser.getId()),null,null,null
				,auditDetail,getUserWorkspace().getLoginUserDetails(),getOverideMap());
	}	




	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException{
		doWriteComponentsToBean();
		try {
			logger.debug("Entering ");

			AuditHeader auditHeader = getAuditHeader(getSecurityUser(), "");
			auditHeader.setAuditDetails(getAuditDetails());
			if(doSaveProcess(auditHeader)){
				closeDialog(this.win_SecurityUserRolesDialog, "SecurityUserRoles");	
			}	

		} catch (DataAccessException e) {
			logger.debug("error in Save method"+e.toString());
			showMessage(e);
		}
		logger.debug("Leaving ");
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
	private boolean doSaveProcess(AuditHeader auditHeader) {
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;

		try{
			while(retValue==PennantConstants.porcessOVERIDE){
				auditHeader=getSecurityUserRolesService().save(auditHeader);
				auditHeader = ErrorControl.showErrorDetails(this.win_SecurityUserRolesDialog, auditHeader);
				retValue =auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
				}
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		}
		catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}
	/**
	 * This method prepares the audit details list and sets different auditSequence for 
	 * newly inserted records and deleted records
	 * 
	 * @return
	 */
	private List<AuditDetail> getAuditDetails(){
		logger.debug("Entering ");
		List<AuditDetail> auditDetails=new ArrayList<AuditDetail>();;

		int count = 1;
		String[] fields = PennantJavaUtil.getFieldDetails(new SecurityUserRoles());
		/*set audit sequence number for all new records*/
		if(selectedMap!=null && selectedMap.size()>0){
			Collection<SecurityUserRoles> collection =  selectedMap.values();

			for (final  SecurityUserRoles securityUserRoles : collection) {
				AuditDetail auditDetail =getAuditDetail(securityUserRoles,count,fields);
				if(auditDetail!=null){
					auditDetails.add(auditDetail);
					count++;
				}
			}
		}
		/*set audit sequence number for all deleted records*/
		if(deletedMap!=null && deletedMap.size()>0){
			count=1;
			Collection<SecurityUserRoles> collection =  deletedMap.values();
			for (final  SecurityUserRoles securityUserRoles : collection) {
				AuditDetail auditDetail =getAuditDetail(securityUserRoles,count,fields);
				if(auditDetail!=null){
					auditDetails.add(auditDetail);
					count++;
				}
			}	
		}
		logger.debug("Leaving ");
		return auditDetails;
	}
	/**
	 * This method returns auditDetail
	 * @param securityUserRoles
	 * @param auditSeq
	 * @param fields
	 * @return AuditDetail
	 */
	private AuditDetail getAuditDetail(SecurityUserRoles securityUserRoles,int auditSeq,String[] fields){
		logger.debug("Entering ");
		if(securityUserRoles==null ){
			return null;	
		}
		String auditImage = "";
		Object befImage=null;
		if(securityUserRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			auditImage=PennantConstants.TRAN_ADD;
		}
		if(securityUserRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
			auditImage=PennantConstants.TRAN_DEL;
			befImage=securityUserRoles;
		}
		logger.debug("Leaving ");
		return new AuditDetail(auditImage, auditSeq, fields[0], fields[1], befImage, securityUserRoles);
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


	public Map<Long, SecurityRole> getNewAssignedMap() {
		return newAssignedMap;
	}

	public void setNewAssignedMap(Map<Long, SecurityRole> newAssignedMap) {
		this.newAssignedMap = newAssignedMap;
	}
	public Map<Long, SecurityRole> getOldAssignedMap() {
		return oldAssignedMap;
	}
	public void setOldAssignedMap(Map<Long, SecurityRole> oldAssignedMap) {
		this.oldAssignedMap = oldAssignedMap;
	}

	public void setTempUnAsgnRoleMap(Map<Long, SecurityRole> tempUnAsgnRoleMap) {
		this.tempUnAsgnRoleMap = tempUnAsgnRoleMap;
	}

	public Map<Long, SecurityRole> getTempUnAsgnRoleMap() {
		return tempUnAsgnRoleMap;
	}

}
