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
 * FileName    		:SecurityGroupRightsListCtrl .java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    :  2-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *2-08-2011	      Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */package com.pennant.webui.administration.securitygrouprights;

 import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.service.administration.SecurityGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.administration.securitygroup.model.SecurityGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

 /**
  * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
  * This is the controller class for the /WEB-INF/pages/Administration/SecurityGroupRights/SecurityGroupRightsList.zul
  * file.<br>
  * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
  * 
  */
 public class SecurityGroupRightsListCtrl  extends GFCBaseListCtrl<SecurityGroup> implements Serializable  {

	 private static final long serialVersionUID = -577256448245687404L;
	 private final static Logger logger = Logger.getLogger(SecurityGroupRightsListCtrl.class);

	 /*
	  * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	  * All the components that are defined here and have a corresponding
	  * component with the same 'id' in the ZUL-file are getting autoWired by our
	  * 'extends GFCBaseCtrl' GenericForwardComposer.
	  * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	  */
	 protected Window        window_SecurityGroupRightsList;                       // autowired
	 protected Borderlayout  borderLayout_SecurityGroupList;                      // autowired
	 // List headers
	 protected Listheader    listheader_GrpCode;                                   // autowired
	 protected Listheader    listheader_GrpDesc;                                   // autowired
	 protected Listheader    listheader_RecordStatus;                              // autowired
	 protected Listheader    listheader_RecordType;                                // autowired
	 protected Panel         securityGroupSeekPanel;                              // autowired
	 protected Panel         securityGroupListPanel;                              // autowired
	 protected Button        btnHelp;                                              // autowired
	 protected Button        button_SecurityGroupList_SecurityGroupSearchDialog; // autowired
	 protected Button        button_SecurityGroupList_PrintList;                  // autowired
	 private   Paging        pagingSecurityGroupList;                             // autowired
	 private   Listbox       listBoxSecurityGroup;                                // autowired
	 // NEEDED for the ReUse in the SearchWindow
	 protected JdbcSearchObject<SecurityGroup> searchObj;
	 private transient SecurityGroupService    securityGroupService;
	 private transient WorkFlowDetails          workFlowDetails=null;

	 /**
	  * default constructor.<br>
	  */
	 public SecurityGroupRightsListCtrl (){
		 super();
	 }

	 /**
	  * Before binding the data and calling the dialog window we check, if the
	  * ZUL-file is called with a parameter for a selected SecurityGroup object in a
	  * Map.
	  * 
	  * @param event
	  * @throws Exception
	  */
	 public void onCreate$window_SecurityGroupRightsList(Event event) throws Exception {
		 logger.debug("Entering " + event.toString());
		 ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SecurityGroup");
		 boolean wfAvailable=true;

		 if (moduleMapping.getWorkflowType()!=null){
			 workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityGroup");

			 if (workFlowDetails==null){
				 setWorkFlowEnabled(false);
			 }else{
				 setWorkFlowEnabled(true);
				 setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				 setWorkFlowId(workFlowDetails.getId());
			 }	
		 }else{
			 wfAvailable=false;
		 }

		 /* set components visible dependent of the users rights */
		 doCheckRights();

		 /**
		  * Calculate how many rows have been place in the listBox. Get the
		  * currentDesktopHeight from a hidden IntBox from the index.zul that are
		  * filled by onClientInfo() in the indexCtroller
		  */

		 this.borderLayout_SecurityGroupList.setHeight(getBorderLayoutHeight());

		 // set the paging parameters
		 this.getPagingSecurityGroupList().setPageSize(getListRows());
		 this.getPagingSecurityGroupList().setDetailed(true);
		 this.listheader_GrpCode.setSortAscending(new FieldComparator("grpCode", true));
		 this.listheader_GrpCode.setSortDescending(new FieldComparator("grpCode", false));
		 this.listheader_GrpDesc.setSortAscending(new FieldComparator("grpDesc", true));
		 this.listheader_GrpDesc.setSortDescending(new FieldComparator("grpDesc", false));

		 if (isWorkFlowEnabled()){
			 this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			 this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			 this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			 this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		 }else{
			 this.listheader_RecordStatus.setVisible(false);
			 this.listheader_RecordType.setVisible(false);
		 }
		 // ++ create the searchObject and initial sorting ++//
		 this.searchObj = new JdbcSearchObject<SecurityGroup>(SecurityGroup.class,getListRows());
		 this.searchObj.addSort("grpCode", false);
		 this.searchObj.addTabelName("SecGroups_View");

		 // WorkFlow
		 if (isWorkFlowEnabled()) {
			 this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles()
					 ,isFirstTask());
		 }

		 setSearchObj(this.searchObj);
		 if (!isWorkFlowEnabled() && wfAvailable){

			 this.button_SecurityGroupList_SecurityGroupSearchDialog.setVisible(false);
			 this.button_SecurityGroupList_PrintList.setVisible(false);
			 PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		 }else{
			 // Set the ListModel for the articles.
			 getPagedListWrapper().init(this.searchObj,this.getListBoxSecurityGroup()
					 ,this.getPagingSecurityGroupList());
			 // set the itemRenderer
			 this.getListBoxSecurityGroup().setItemRenderer(new SecurityGroupListModelItemRenderer());
		 }	
		 logger.debug("Leaving " + event.toString());
	 }

	 /**
	  * SetVisible for components by checking if there's a right for it.
	  */
	 private void doCheckRights() {
		 logger.debug("Entering ");
		 getUserWorkspace().alocateAuthorities("SecurityGroupList");
		 this.button_SecurityGroupList_SecurityGroupSearchDialog.setVisible(
				 getUserWorkspace().isAllowed("button_SecurityGroupList_SecurityGroupFindDialog"));
		 this.button_SecurityGroupList_PrintList.setVisible(
				 getUserWorkspace().isAllowed("button_SecurityGroupList_PrintList"));
		 logger.debug("Leaving ");
	 }

	 /**
	  * This method is forwarded from the listBoxes item renderer. <br>
	  * see: com.pennant.webui.administration.securitygroups.model.SecurityGroupListModelItemRenderer.java <br>
	  * 
	  * @param event
	  * @throws Exception
	  */
	 public void onSecurityGroupItemDoubleClicked(Event event) throws Exception {
		 logger.debug("Entering " + event.toString());

		 /* get the selected SecurityGroup object*/
		 final Listitem item = this.listBoxSecurityGroup.getSelectedItem();

		 if (item != null) {
			 /* CAST AND STORE THE SELECTED OBJECT*/
			 final SecurityGroup aSecurityGroup = (SecurityGroup) item.getAttribute("data");
			 final SecurityGroup securityGroup = getSecurityGroupService()
			 .getSecurityGroupById(aSecurityGroup.getId());

			 if(securityGroup==null){
				 String[] errorParm= new String[3];
				 errorParm[0]=PennantJavaUtil.getLabel("label_GrpID") + ":"+ aSecurityGroup.getGrpID();
				 errorParm[1] = PennantJavaUtil.getLabel("label_GrpCode") + ":"+ aSecurityGroup.getGrpCode();

				 String[] valueParm= new String[3];
				 valueParm[0]	=	String.valueOf(aSecurityGroup.getGrpID());
				 valueParm[1] 	= 	aSecurityGroup.getGrpCode();

				 ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005"
						 , errorParm,valueParm), getUserWorkspace().getUserLanguage());
				 PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			 }else{

				 String whereCond =  " AND GrpID="+ securityGroup.getGrpID()
				 +" AND version=" + securityGroup.getVersion()+" ";

				 if(isWorkFlowEnabled()){
					 boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							 .getLoginUserDetails().getLoginUsrID(), "SecurityGroup"
							 , whereCond, securityGroup.getTaskId()
							 , securityGroup.getNextTaskId());
					 if (userAcces){
						 showDetailView(securityGroup);
					 }else{
						 PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					 }
				 }else{
					 showDetailView(securityGroup);
				 }
			 }
		 }
		 logger.debug("Leaving " + event.toString());
	 }

	 /**
	  * Opens the detail view. <br>
	  * OverHanded some parameters in a map if needed. <br>
	  * 
	  * @param SecurityGroup (aSecurityGroup)
	  * @throws Exception
	  */
	 private void showDetailView(SecurityGroup aSecurityGroup) throws Exception {
		 logger.debug("Entering ");

		 /*
		  * We can call our Dialog zul-file with parameters. So we can call them
		  * with a object of the selected item. For handed over these parameter
		  * only a Map is accepted. So we put the object in a HashMap.
		  */

		 if(aSecurityGroup.getWorkflowId()==0 && isWorkFlowEnabled()){
			 aSecurityGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		 }

		 final HashMap<String, Object> map = new HashMap<String, Object>();
		 map.put("securityGroup", aSecurityGroup);
		 /*
		  * we can additionally handed over the listBox or the controller self,
		  * so we have in the dialog access to the listBox ListModel. This is
		  * fine for synchronizing the data in the SecurityGroupListbox from the
		  * dialog when we do a delete, edit or insert a SecurityGroup.
		  */
		 map.put("securityGroupListCtrl", this);

		 // call the zul-file with the parameters packed in a map
		 try {
			 Executions.createComponents(
					 "/WEB-INF/pages/Administration/SecurityGroupRights" +
					 "/SecurityGroupRightsDialog.zul",null,map);
		 } catch (final Exception e) {
			 logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			 PTMessageUtils.showErrorMessage(e.toString());
		 }
		 logger.debug("Leaving ");
	 }

	 /**
	  * when the "help" button is clicked. <br>
	  * 
	  * @param event
	  * @throws InterruptedException
	  */
	 public void onClick$btnHelp(Event event) throws InterruptedException {
		 logger.debug("Entering ");
		 PTMessageUtils.showHelpWindow(event, window_SecurityGroupRightsList);
		 logger.debug("Leaving ");
	 }

	 /**
	  * when the "refresh" button is clicked. <br>
	  * <br>
	  * Refreshes the view by calling the onCreate event manually.
	  * 
	  * @param event
	  * @throws InterruptedException
	  */
	 public void onClick$btnRefresh(Event event) throws InterruptedException {
		 logger.debug("Entering " + event.toString());
		 this.getPagingSecurityGroupList().setActivePage(0);
		 Events.postEvent("onCreate", this.window_SecurityGroupRightsList, event);
		 logger.debug("Leaving " + event.toString());
	 }

	 /**
	  * call the SecurityGroup dialog
	  * @param event
	  * @throws Exception
	  */

	 public void onClick$button_SecurityGroupList_SecurityGroupSearchDialog(Event event) 
	 throws Exception {
		 logger.debug("Entering " + event.toString());
		 /*
		  * we can call our SecurityGroupDialog ZUL-file with parameters. So we can
		  * call them with a object of the selected SecurityGroup. For handed over
		  * these parameter only a Map is accepted. So we put the SecurityGroup object
		  * in a HashMap.
		  */
		 final HashMap<String, Object> map = new HashMap<String, Object>();
		 map.put("securityGroupCtrl", this);
		 map.put("searchObject", this.searchObj);
		 map.put("listBoxSecurityGroup", this.listBoxSecurityGroup);
		 map.put("pagingSecurityGroupList", this.pagingSecurityGroupList);

		 // call the ZUL-file with the parameters packed in a map
		 try {
			 Executions.createComponents(
					 "/WEB-INF/pages/Administration/SecurityGroup" +
					 "/SecurityGroupSearchDialog.zul",null,map);
		 } catch (final Exception e) {
			 logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			 PTMessageUtils.showErrorMessage(e.toString());
		 }
		 logger.debug("Leaving " + event.toString());
	 }

	 /**
	  * When the securityGroupRights print button is clicked.
	  * 
	  * @param event
	  * @throws InterruptedException
	  */
	 public void onClick$button_SecurityGroupList_PrintList(Event event) 
	 throws InterruptedException {
		 logger.debug(event.toString());
		 PTReportUtils.getReport("SecurityGroup", getSearchObj());
	 }

	 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	 // ++++++++++++++++++ getter / setter +++++++++++++++++++//
	 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	 public void setSecurityGroupService(SecurityGroupService securityGroupService) {
		 this.securityGroupService = securityGroupService;
	 }

	 public SecurityGroupService getSecurityGroupService() {
		 return this.securityGroupService;
	 }

	 public JdbcSearchObject<SecurityGroup> getSearchObj() {
		 return this.searchObj;
	 }

	 public void setSearchObj(JdbcSearchObject<SecurityGroup> searchObj) {
		 this.searchObj = searchObj;
	 }

	 public void setListBoxSecurityGroup(Listbox listBoxSecurityGroup) {
		 this.listBoxSecurityGroup = listBoxSecurityGroup;
	 }

	 public Listbox getListBoxSecurityGroup() {
		 return listBoxSecurityGroup;
	 }

	 public void setPagingSecurityGroupList(Paging pagingSecurityGroupList) {
		 this.pagingSecurityGroupList = pagingSecurityGroupList;
	 }

	 public Paging getPagingSecurityGroupList() {
		 return pagingSecurityGroupList;
	 }
 }
