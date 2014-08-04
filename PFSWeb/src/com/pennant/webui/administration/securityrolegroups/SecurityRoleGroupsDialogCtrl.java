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
 *
 * FileName    		:  SecurityRoleGroupsDialogCtrl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    :  10-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  10-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityrolegroups;

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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityRoleGroupsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class SecurityRoleGroupsDialogCtrl extends GFCBaseListCtrl<SecurityGroup> implements Serializable{


	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * This is the controller class for the
	 * /WEB-INF/pages/Administration/SecurityRoleGroups/SecurityRoleGroupsDialog.zul
	 * file.<br>
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * 
	 */

	private static final long serialVersionUID = 2544107887397060565L;
	private  final static Logger logger = Logger.getLogger(SecurityRoleGroupsDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window        win_SecRoleGroupsDialog;         // autoWired
	protected Borderlayout  borderLayout_SecurityRoleGroups; // autoWired
	protected Listbox       listbox_UnAssignedGroups;        // autoWired
	protected Listbox       listbox_AssignedGroups;          // autoWired
	protected Listbox       listbox_GroupsRights;            // autoWired
	// List headers
	protected Listheader    listheader_SelectGroup;           // autoWired
	protected Listheader    listheader_GroupDesc;             // autoWired
	protected Listheader    listheader_RightName;             // autoWired
	protected Button        btnSave;                          // autoWired
	protected Button        Refresh;                          // autoWired     
	protected Button        btnClose;                         // autoWired
	protected Button        btnSelectGroups;                  // autoWired
	protected Button        btnUnSelectGroups;                // autoWired
	protected Button        btnUnSelectAllGroups;             // autoWired
	protected Button        btn_SearchGroups;                 // autoWired
	protected Panel         panel_secrolesGroups;             // autoWired
	protected Panel         panel_SecurityGroupRights;        // autoWired
	protected Label         label_RoleCode;                   // autoWired
	protected Label         label_RoleDesc;                   // autoWired
	protected Label         label_RoleCategory;               // autoWired
	protected Button        btnNew;                           // autoWired
	protected Button        btnEdit;                          // autoWired
	protected Button        btnDelete;                        // autoWired
	protected Button        btnCancel;                        // autoWired
	protected Button        btnNotes;                         // autoWired
	@SuppressWarnings("unused")
	private transient ButtonStatusCtrl             btnCtrl;
	private transient final String     btnCtroller_ClassPrefix = "button_SecRoleGroupsDialog_";
	private   transient SecurityRoleGroupsService   securityRoleGroupsService;
	private   SecurityRole                          securityRole;
	private   SecurityRoleGroups                    secRoleGroups;
	List<SecurityGroup>                    assignedGroupsList =new ArrayList<SecurityGroup>();
	List<SecurityGroup>                    unAssignedGroupsList =new ArrayList<SecurityGroup>();
	private List<SecurityGroup>             tempUnAssignedGroupsList=new ArrayList<SecurityGroup>(); 
	private Map<Long, SecurityGroup>       newAssignedMap = new HashMap<Long, SecurityGroup>();
	private Map<Long, SecurityGroup>       oldAssignedMap = new HashMap<Long, SecurityGroup>();
	private Map<Long, SecurityRoleGroups>  selectedMap;
	private Map<Long, SecurityRoleGroups>  deletedMap ;
	private Map<Long, SecurityGroup>       tempUnAsgnGroupsMap  =new HashMap<Long, SecurityGroup>();
	private Object filters[]=new Object[2];

	/**
	 * default constructor.<br>
	 */

	public SecurityRoleGroupsDialogCtrl(){
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRole object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$win_SecRoleGroupsDialog(Event event) throws Exception {

		logger.debug("Entering " + event.toString());
		/* set components visible dependent of the users rights */
		doCheckRights();
		final Map<String, Object> args = getCreationArgsMap(event);
		// get the parameters map that are overHanded by creation.
		if (args.containsKey("securityRole")) {
			setSecurityRole((SecurityRole)args.get("securityRole"));

		} else {
			setSecurityRole(null);
		}
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace()
				,this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
		/* put the logic for working with panel in the ApplicationWorkspace*/
		this.borderLayout_SecurityRoleGroups.setHeight(getBorderLayoutHeight());
		this.label_RoleCode.setValue(getSecurityRole().getRoleCd());
		this.label_RoleDesc.setValue(getSecurityRole().getRoleDesc());
		this.label_RoleCategory.setValue(getSecurityRole().getRoleCategory());
		/*Fetch all AssignedGroups*/
		assignedGroupsList= getSecurityRoleGroupsService()
		.getGroupsByRoleId(getSecurityRole().getRoleID(),true);
		/*Fetch all UnAssignedGroups*/
		unAssignedGroupsList = getSecurityRoleGroupsService()
		.getGroupsByRoleId(getSecurityRole().getRoleID(),false);
		tempUnAssignedGroupsList=unAssignedGroupsList;
		doShowDialog();
		setDialog(this.win_SecRoleGroupsDialog);
		logger.debug("Leaving ");
	}

	/**
	 * This method calls when user clicks on reset button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSetPanelProperties();
		doShowUnAssignedGroups();
		logger.debug("Leaving " + event.toString());

	}
	/**
	 * When user clicks on "Close" button
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		try {
			doClose();
		} catch ( final WrongValueException e) {
			logger.debug("Error Occured while closing"+e.toString());
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * This method calls when user clicks on save button
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException{
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());

	}
	/**
	 *  when clicks on "btnSelectGroups"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnSelectGroups(Event event) throws Exception {
		logger.debug(event.toString());			

		if(this.listbox_UnAssignedGroups.getSelectedCount()!=0){	

			Listitem li=new Listitem();             //To read List Item
			Set SeletedSet= new HashSet();           //To get Selected Items
			SeletedSet=this.listbox_UnAssignedGroups.getSelectedItems();
			List list=new ArrayList(SeletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				li=(Listitem)it.next();		
				final SecurityGroup aSecGroup= (SecurityGroup)li.getAttribute("data");
				Listcell slecteditem=new Listcell();
				Listcell slecteditemDesc=new Listcell();					
				List SelectedRowValues=new ArrayList();//TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);						
				slecteditemDesc=(Listcell)SelectedRowValues.get(1);	
				tempUnAsgnGroupsMap.remove(Long.valueOf(aSecGroup.getGrpID()));
				getNewAssignedMap().put(Long.valueOf(aSecGroup.getGrpID()),aSecGroup);
				doFillListbox(this.listbox_AssignedGroups,slecteditem.getLabel()
						,slecteditemDesc.getLabel(),aSecGroup);
				if(true){
					listbox_UnAssignedGroups.removeItemAt(li.getIndex());
				}
			}							
		}		
	}

	/**
	 *  when clicks on "btnUnSelectGroups"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnUnSelectGroups(Event event) throws Exception {
		logger.debug(event.toString());			

		if(this.listbox_AssignedGroups.getSelectedCount()!=0){	

			Listitem li=new Listitem();              //To read List Item
			Set SeletedSet= new HashSet();           //To get Selected Items
			SeletedSet=this.listbox_AssignedGroups.getSelectedItems();
			List list=new ArrayList(SeletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				li=(Listitem)it.next();		
				final SecurityGroup aSecGroup= (SecurityGroup)li.getAttribute("data");
				Listcell slecteditem=new Listcell();
				Listcell slecteditemDesc=new Listcell();					
				List SelectedRowValues=new ArrayList();//TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);						
				slecteditemDesc=(Listcell)SelectedRowValues.get(1);	
				tempUnAsgnGroupsMap.put(Long.valueOf(aSecGroup.getGrpID()), aSecGroup);
				getNewAssignedMap().remove(Long.valueOf(aSecGroup.getGrpID()));
				doFillListbox(this.listbox_UnAssignedGroups,slecteditem.getLabel()
						,slecteditemDesc.getLabel(),aSecGroup);
				if(true){
					listbox_AssignedGroups.removeItemAt(li.getIndex());
				}
			}							
		}		
	}

	/**
	 *  when clicks on "btnUnSelectAllGroups"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnUnSelectAllGroups(Event event) throws Exception {
		logger.debug(event.toString());			
		this.listbox_AssignedGroups.selectAll();
		if(this.listbox_AssignedGroups.getSelectedCount()!=0){	

			Listitem li=new Listitem();             //To read List Item
			Set SeletedSet= new HashSet();           //To get Selected Items
			SeletedSet=this.listbox_AssignedGroups.getSelectedItems();
			List list=new ArrayList(SeletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				li=(Listitem)it.next();		
				final SecurityGroup aSecGroup= (SecurityGroup)li.getAttribute("data");
				Listcell slecteditem=new Listcell();
				Listcell slecteditemDesc=new Listcell();					
				List SelectedRowValues=new ArrayList();//TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);						
				slecteditemDesc=(Listcell)SelectedRowValues.get(1);	
				tempUnAsgnGroupsMap.put(Long.valueOf(aSecGroup.getGrpID()), aSecGroup);
				getNewAssignedMap().remove(Long.valueOf(aSecGroup.getGrpID()));
				doFillListbox(this.listbox_UnAssignedGroups,slecteditem.getLabel()
						,slecteditemDesc.getLabel(),aSecGroup);
				if(true){
					listbox_AssignedGroups.removeItemAt(li.getIndex());
				}
			}							
		}		
	}

	/**
	 * This method invokes when double clicked on Groups item and calls showGroupRights() method
	 */

	public void onSecurityGroupItemDoubleClicked(ForwardEvent event) throws Exception{

		logger.debug("Entering " + event.toString());
		this.panel_SecurityGroupRights.setOpen(true);
		doShowGroupRights(event);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on "onClick$btn_SearchUnAssignedRoles"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btn_SearchGroups(Event event) 
	throws Exception{
		logger.debug("Entering " + event.toString());
		doSetPanelProperties();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dialogCtrl", this);
		map.put("dataMap",tempUnAsgnGroupsMap);
		map.put("prevFilters", filters);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
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
		PTMessageUtils.showHelpWindow(event, this.win_SecRoleGroupsDialog);
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
		getUserWorkspace().alocateAuthorities("SecurityRoleGroupsDialog");
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleGroupsDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleGroupsDialog_btnCancel"));
		this.btnSelectGroups.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleGroupsDialog_btnSelectGroups"));
		this.btnUnSelectGroups.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleGroupsDialog_btnUnSelectGroups"));
		this.btnUnSelectAllGroups.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleGroupsDialog_btnUnSelectAllGroups"));
		logger.debug("Leaving ");
	}
	/**
	 * Opens the Dialog window modal.
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException{
		doSetPanelProperties();
		for(SecurityGroup secGroup:unAssignedGroupsList){
			tempUnAsgnGroupsMap.put(Long.valueOf(secGroup.getGrpID()),secGroup);
		}
		doShowAssignedGroups();
		doShowUnAssignedGroups();
	}
	/**
	 * This method displays Assigned groups in Assigned groups panel by calling doFillListbox()
	 */
	@SuppressWarnings("unchecked")
	public void doShowAssignedGroups(){
		logger.debug("Entering ");
		this.listbox_AssignedGroups.getItems().clear();
		Comparator<SecurityGroup> comp = new BeanComparator("grpCode");
		Collections.sort(assignedGroupsList, comp);
		for (int i = 0; i < assignedGroupsList.size(); i++) {
			SecurityGroup aSecurityGroup = assignedGroupsList.get(i);
			doFillListbox(listbox_AssignedGroups,aSecurityGroup.getGrpCode()
					,aSecurityGroup.getGrpDesc(),aSecurityGroup);
			this.oldAssignedMap.put(Long.valueOf(aSecurityGroup.getGrpID()), aSecurityGroup);
		}
		setOldAssignedMap(this.oldAssignedMap);
		getNewAssignedMap().putAll(oldAssignedMap);
	}
	/**
	 * This method displays UnAssigned groups in UnAssigned groups panel by calling doFillListbox()
	 */
	@SuppressWarnings("unchecked")
	public void doShowUnAssignedGroups(){
		logger.debug("Entering");
		this.listbox_UnAssignedGroups.getItems().clear();
		unAssignedGroupsList=new ArrayList<SecurityGroup>(tempUnAsgnGroupsMap.values());
		Comparator<SecurityGroup> comp = new BeanComparator("grpCode");
		Collections.sort(unAssignedGroupsList, comp);
		for (int i = 0; i < unAssignedGroupsList.size(); i++) {
			SecurityGroup aSecurityGroup = unAssignedGroupsList.get(i);

			doFillListbox(listbox_UnAssignedGroups,aSecurityGroup.getGrpCode()
					,aSecurityGroup.getGrpDesc(),aSecurityGroup);

		}
		logger.debug("Leaving");
	}
	/**
	 * This method Renders the listBox 
	 * @param listbox
	 * @param value1
	 * @param Value2
	 * @param SecurityRole
	 */
	private void doFillListbox(Listbox listbox,String value1,String Value2,SecurityGroup SecurityGroup){
		Listitem item=new Listitem(); //To Create List item
		Listcell lc;
		lc=new Listcell();	
		lc.setLabel(value1);
		lc.setParent(item);
		lc = new Listcell();
		lc.setLabel(Value2);
		lc.setParent(item);	
		item.setAttribute("data", SecurityGroup);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityGroupItemDoubleClicked");
		listbox.appendChild(item);
	} 
	/**
	 *  This Method display All groups for selected Right 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public void doShowGroupRights(ForwardEvent event)throws InterruptedException{

		logger.debug("Entering ");
		Listitem item=(Listitem) event.getOrigin().getTarget();
		SecurityGroup securityGroup = new SecurityGroup();
		if (item != null) {
			/* CAST AND STORE THE SELECTED OBJECT*/
			securityGroup = (SecurityGroup) item.getAttribute("data");
		}
		this.panel_SecurityGroupRights.setTitle(Labels.getLabel("panel_SecRoleGroupsDailog_GroupRights.title")
				+ " - "+ securityGroup.getGrpCode());

		this.listbox_GroupsRights.getItems().clear();
		List<SecurityGroupRights> groupRightsList=getSecurityRoleGroupsService()
		.getSecurityGroupRightsByGrpId(securityGroup);
		Comparator<SecurityGroupRights> comp = new BeanComparator("lovDescRightName");
		Collections.sort(groupRightsList, comp);
		for(int i=0;i<groupRightsList.size();i++){
			SecurityGroupRights secGroupRights=(SecurityGroupRights)groupRightsList.get(i);
			Listitem li=new Listitem(); //To Create List item
			Listcell lc;
			lc=new Listcell();	
			lc.setLabel(secGroupRights.getLovDescRightName());
			lc.setParent(li);	
			li.setAttribute("data", secGroupRights);
			this.listbox_GroupsRights.appendChild(li);	 
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
	public void doClose() throws InterruptedException {
		logger.debug("Entering ");
		boolean close = true;
		// before close check whether data changed.
		if (isdatachanged()) {

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,MultiLineMessageBox.YES 
					| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				doSave();
				close = false;
				logger.debug("doClose: Yes");
			} else {

				logger.debug("doClose: No");
			}
		}
		if(close){
			closeDialog(this.win_SecRoleGroupsDialog, "SecurityRoleGroups");
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method do the following 
	 * 1)compare oldAssigned map and new assigned map 
	 *    a)if GrpId not in oldselectedMap and in new selectedMap creates new SecurityUserRoles
	 *     Object, sets data and add it to SecurityRole LovDescAssignedGroups
	 *    b)if GrpId  in oldselectedMap and not in new selectedMap gets the SecurityUserRoles
	 *       from back end , sets RecordStatus "DELETE" it to SecurityRole LovDescAssignedGroups
	 */
	public void doWriteComponentsToBean(){

		selectedMap = new HashMap<Long, SecurityRoleGroups>();
		deletedMap  = new HashMap<Long, SecurityRoleGroups>();
		for (Object grpId : getNewAssignedMap().keySet()) {
			if (!getOldAssignedMap().containsKey(grpId)) {

				SecurityRoleGroups aSecRoleGroups=new SecurityRoleGroups();
				aSecRoleGroups.setGrpID(getNewAssignedMap().get(grpId).getGrpID());
				aSecRoleGroups.setLovDescGrpCode(getNewAssignedMap().get(grpId).getGrpCode());
				aSecRoleGroups.setRoleID(getSecurityRole().getRoleID());
				aSecRoleGroups.setLovDescRoleCode(getSecurityRole().getRoleCd());
				aSecRoleGroups.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aSecRoleGroups.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				aSecRoleGroups.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aSecRoleGroups.setNextRoleCode("");
				aSecRoleGroups.setNextTaskId("");
				aSecRoleGroups.setTaskId("");
				aSecRoleGroups.setRoleCode("");
				selectedMap.put(Long.valueOf(getNewAssignedMap().get(grpId).getGrpID()), aSecRoleGroups);

			}
		}
		//for Delete
		for (Object grpId : getOldAssignedMap().keySet()) {
			if (!getNewAssignedMap().containsKey(grpId)) {
				SecurityRoleGroups aSecRoleGroups=new SecurityRoleGroups();
				aSecRoleGroups.setRoleID(getSecurityRole().getRoleID());
				aSecRoleGroups.setGrpID(getOldAssignedMap().get(grpId).getGrpID());
				aSecRoleGroups.setLovDescRoleCode(getSecurityRole().getRoleCd());
				aSecRoleGroups.setLovDescGrpCode(getOldAssignedMap().get(grpId).getGrpCode());
				aSecRoleGroups.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aSecRoleGroups.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aSecRoleGroups.setNextRoleCode("");
				aSecRoleGroups.setNextTaskId("");
				aSecRoleGroups.setTaskId("");
				aSecRoleGroups.setRoleCode("");
				aSecRoleGroups.setRecordStatus("");
				aSecRoleGroups.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				deletedMap.put(Long.valueOf(getOldAssignedMap().get(grpId).getGrpID()), aSecRoleGroups);			
			}	 
		}	 
	}
	/**
	 * This method cancels the previous operations
	 * @throws InterruptedException
	 */
	private void doCancel() throws InterruptedException{
		tempUnAsgnGroupsMap.clear();
		newAssignedMap.clear();
		unAssignedGroupsList=tempUnAssignedGroupsList;
		doShowDialog();

	}
	/**
	 * This method displays the filtered data in unAssigned Groups panel .
	 * @param searchResult
	 */
	@SuppressWarnings("unchecked")
	public void doShowSearchResult(Object[] searchResult){
		logger.debug("Entering");
		List<Object> searchResultList=(List<Object>)searchResult[0];
		/*we get the last used filters from SecuritySearchDialogCtrl and we send these filters to
		 * SecuritySearchDialogCtrl on event  "onClick$btnSearchRights" for set previous search filters */
		filters=(Object[]) searchResult[1];
		this.listbox_UnAssignedGroups.getItems().clear();
		Comparator<Object> comp = new BeanComparator("grpCode");
		Collections.sort(searchResultList, comp);
		for (int i = 0; i < searchResultList.size(); i++) {
			SecurityGroup aSecurityGroup = (SecurityGroup)searchResultList.get(i);
			doFillListbox(listbox_UnAssignedGroups,aSecurityGroup.getGrpCode()
					,aSecurityGroup.getGrpDesc(),aSecurityGroup);
		}
		logger.debug("Leaving");
	}
	/**
	 * This method sets panel properties 
	 */
	public void doSetPanelProperties(){
		logger.debug("Entering ");
		this.panel_SecurityGroupRights.setOpen(false);
		this.listbox_GroupsRights.getItems().clear();
		this.panel_SecurityGroupRights.setTitle(
				Labels.getLabel("panel_SecRoleGroupsDailog_GroupRights.title"));
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
	public void doSave() throws InterruptedException{
		logger.debug("Entering ");
		try {

			doWriteComponentsToBean();
			AuditHeader auditHeader = getAuditHeader(getSecurityRole(), "");
			auditHeader.setAuditDetails(getAuditDetails());
			if(doSaveProcess(auditHeader)){
				closeDialog(this.win_SecRoleGroupsDialog, "SecurityRoleGroups");	
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
	 * @param method (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader) {
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		try{
			while(retValue==PennantConstants.porcessOVERIDE){

				auditHeader= getSecurityRoleGroupsService().Save(auditHeader);;
				auditHeader = ErrorControl.showErrorDetails(this.win_SecRoleGroupsDialog, auditHeader);
				retValue=auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
				}
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
				setOverideMap(auditHeader.getOverideMap());
			}
		}
		catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;

	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * This method checks whether data changed or not 
	 * @return true If changed ,otherwise false
	 */
	public boolean isdatachanged() {
		logger.debug("Entering ");
		/*check for sizes of newSelectedMap and oldSelectedMap of SecurityRole */
		if (getNewAssignedMap().size() != getOldAssignedMap().size()) {
			return true;
		}
		/*check for all keys are same in  newSelectedMap and oldSelectedMap of SecurityRole */
		if (getNewAssignedMap().size() == getOldAssignedMap().size()) {

			for (Object key : getNewAssignedMap().keySet()) {
				if (! getOldAssignedMap().containsKey(key)) {
					return true;
				}
			}
		}
		logger.debug("Leaving ");
		return false;
	}
	/**
	 * This method shows message box with error message
	 * @param e
	 */
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.win_SecRoleGroupsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
		logger.debug("Leaving ");
	}
	/**
	 * This method  creates and returns AuditHeader Object
	 * @param aSecRight
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityRole aSecurityRole, String tranType) {

		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityRole.getBefImage()
				,aSecurityRole);   
		return new AuditHeader(String.valueOf(aSecurityRole.getId()),null,null,null
				,auditDetail,getUserWorkspace().getLoginUserDetails(),getOverideMap());
	}

	/**
	 * This method prepares the audit details list and sets different auditSequence for 
	 * newly inserted records and deleted records
	 * 
	 * @return
	 */
	private List<AuditDetail> getAuditDetails(){
		logger.debug("Entering ");
		List<AuditDetail> auditDetails= new ArrayList<AuditDetail>();;

		int count = 1;
		String[] fields = PennantJavaUtil.getFieldDetails(new SecurityRoleGroups());

		if(selectedMap!=null && selectedMap.size()>0){
			Collection<SecurityRoleGroups> collection =  selectedMap.values();
			for (final  SecurityRoleGroups securityRolesGroups : collection) {
				AuditDetail auditDetail =getAuditDetail(securityRolesGroups,count,fields);
				if(auditDetail!=null){
					auditDetails.add(auditDetail);
					count++;
				}
			}
		}

		if(deletedMap!=null && deletedMap.size()>0){
			Collection<SecurityRoleGroups> collection =  deletedMap.values();
			count=1;
			for (final  SecurityRoleGroups securityRolesGroups : collection) {
				AuditDetail auditDetail =getAuditDetail(securityRolesGroups,count,fields);
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
	 * This method returns audit detail
	 * @param securityRolesGroups
	 * @param auditSeq
	 * @param fields
	 * @return
	 */
	private AuditDetail getAuditDetail(SecurityRoleGroups securityRolesGroups,int auditSeq,String[] fields){
		logger.debug("Entering ");
		if(securityRolesGroups==null ){
			return null;	
		}
		String auditImage = "";
		Object befImage=null;
		if(securityRolesGroups.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			auditImage=PennantConstants.TRAN_ADD;
		}
		if(securityRolesGroups.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
			auditImage=PennantConstants.TRAN_DEL;
			befImage=securityRolesGroups;
		}
		logger.debug("Leaving ");
		return new AuditDetail(auditImage, auditSeq, fields[0], fields[1], befImage, securityRolesGroups);
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public SecurityRole getSecurityRole() {
		return securityRole;
	}

	public void setSecurityRole(SecurityRole securityRole) {
		this.securityRole = securityRole;
	}

	public SecurityRoleGroups getSecRoleGroups() {
		return secRoleGroups;
	}

	public void setSecRoleGroups(SecurityRoleGroups secRoleGroups) {
		this.secRoleGroups = secRoleGroups;
	}

	public SecurityRoleGroupsService getSecurityRoleGroupsService() {
		return securityRoleGroupsService;
	}

	public void setSecurityRoleGroupsService(
			SecurityRoleGroupsService securityRoleGroupsService) {
		this.securityRoleGroupsService = securityRoleGroupsService;
	}

	public Map<Long, SecurityGroup> getNewAssignedMap() {
		return newAssignedMap;
	}
	public void setNewAssignedMap(Map<Long, SecurityGroup> newAssignedMap) {
		this.newAssignedMap = newAssignedMap;
	}
	public Map<Long, SecurityGroup> getOldAssignedMap() {
		return oldAssignedMap;
	}
	public void setOldAssignedMap(Map<Long, SecurityGroup> oldAssignedMap) {
		this.oldAssignedMap = oldAssignedMap;
	}	
}
