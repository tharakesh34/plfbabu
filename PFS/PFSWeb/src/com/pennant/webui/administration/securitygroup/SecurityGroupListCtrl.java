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
 * FileName    		:  SecurityGroupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011   														*
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

package com.pennant.webui.administration.securitygroup;

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
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityGroup/SecurityGroupList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityGroupListCtrl extends GFCBaseListCtrl<SecurityGroup> implements Serializable {

	private static final long serialVersionUID = -418890474385890182L;
	private final static Logger logger = Logger.getLogger(SecurityGroupListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window        window_SecurityGroupList;                           // autoWired
	protected Borderlayout  borderLayout_SecurityGroupList;                     // autoWired
	protected Paging        pagingSecurityGroupList;                            // autoWired
	protected Listbox       listBoxSecurityGroup;                               // autoWired
	
	// List headers
	protected Listheader    listheader_GrpCode;                                 // autoWired
	protected Listheader    listheader_GrpDesc;                                 // autoWired
	protected Listheader    listheader_RecordStatus;                            // autoWired
	protected Listheader    listheader_RecordType;                              // autoWired
	protected Panel         securityGroupSeekPanel;                             // autoWired
	protected Panel         securityGroupListPanel;                             // autoWired
	
	// checkRights
	protected Button        btnHelp;                                            // autoWired
	protected Button        button_SecurityGroupList_NewSecurityGroup;          // autoWired
	protected Button        button_SecurityGroupList_SecurityGroupSearchDialog; // autoWired
	protected Button        button_SecurityGroupList_PrintList;                 // autoWired
	
	/* NEEDED for the ReUse in the SearchWindow*/
	protected JdbcSearchObject<SecurityGroup> searchObj;
	
	/* row count for listBox*/
	private transient SecurityGroupService   securityGroupService;
	private transient WorkFlowDetails  workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public SecurityGroupListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityGroup object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityGroupList(Event event) throws Exception {
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
		/* set the paging parameters*/
		this.pagingSecurityGroupList.setPageSize(getListRows());
		this.pagingSecurityGroupList.setDetailed(true);
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
		
		/* create the searchObject and initial sorting */
		this.searchObj = new JdbcSearchObject<SecurityGroup>(SecurityGroup.class,getListRows());
		this.searchObj.addSort("grpCode", false);
		this.searchObj.addTabelName("SecGroups_View");
		
		/* WorkFlow*/
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_SecurityGroupList_NewSecurityGroup.setVisible(true);
			} else {
				button_SecurityGroupList_NewSecurityGroup.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SecurityGroupList_NewSecurityGroup.setVisible(false);
			this.button_SecurityGroupList_SecurityGroupSearchDialog.setVisible(false);
			this.button_SecurityGroupList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			/* Set the ListModel for the articles.*/
			getPagedListWrapper().init(this.searchObj,this.listBoxSecurityGroup
					,this.pagingSecurityGroupList);
			/* set the itemRenderer*/
			this.listBoxSecurityGroup.setItemRenderer(new SecurityGroupListModelItemRenderer());
		}	
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("SecurityGroupList");
		this.button_SecurityGroupList_NewSecurityGroup.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityGroupList_NewSecurityGroup"));
		this.button_SecurityGroupList_SecurityGroupSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityGroupList_SecurityGroupFindDialog"));
		this.button_SecurityGroupList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityGroupList_PrintList"));
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
			final SecurityGroup securityGroup = getSecurityGroupService().getSecurityGroupById(aSecurityGroup.getId());

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

				String whereCond =  " AND GrpID="+ securityGroup.getGrpID() +" AND version=" + securityGroup.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "SecurityGroup"
							, whereCond, securityGroup.getTaskId(), securityGroup.getNextTaskId());
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
	 * Call the SecurityGroup dialog with a new empty entry. <br>
	 */
	public void onClick$button_SecurityGroupList_NewSecurityGroup(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* create a new SecurityGroup object, We GET it from the back end.*/
		final SecurityGroup aSecurityGroup = getSecurityGroupService().getNewSecurityGroup();		
		showDetailView(aSecurityGroup);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param SecurityGroup (aSecurityGroup)
	 * @throws Exception
	 */
	private void showDetailView(SecurityGroup aSecurityGroup) throws Exception {
		logger.debug("Entering ");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
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
		 * so we have in the dialog access to the listBox List model. This is
		 * fine for synchronizing the data in the SecurityGroupListbox from the
		 * dialog when we do a delete, edit or insert a SecurityGroup.
		 */
		map.put("securityGroupListCtrl", this);

		/* call the ZUL-file with the parameters packed in a map*/
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityGroup" + "/SecurityGroupDialog.zul",null,map);
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
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SecurityGroupList);
		logger.debug("Leaving " + event.toString());
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
		this.pagingSecurityGroupList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SecurityGroupList, event);
		this.window_SecurityGroupList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the SecurityGroup dialog
	 */
	public void onClick$button_SecurityGroupList_SecurityGroupSearchDialog(Event event)	throws Exception {
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

		/* call the ZUL-file with the parameters packed in a map*/
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityGroup" + "/SecurityGroupSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the securityGroup print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecurityGroupList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("SecurityGroup", getSearchObj());
		logger.debug("Leaving" +event.toString());
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
}