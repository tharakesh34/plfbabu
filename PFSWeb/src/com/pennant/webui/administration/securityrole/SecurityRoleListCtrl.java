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
 * FileName    		:  SecurityRoleListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011    Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityrole;

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
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.administration.securityrole.model.SecurityRoleListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityRole/SecurityRoleList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityRoleListCtrl extends GFCBaseListCtrl<SecurityRole> implements Serializable {


	private static final long serialVersionUID = 2911491293408952170L;
	private final static Logger logger = Logger.getLogger(SecurityRoleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SecurityRoleList;                           // autoWired
	protected Borderlayout borderLayout_SecurityRoleList;                     // autoWired
	protected Paging       pagingSecurityRoleList;                            // autoWired
	protected Listbox      listBoxSecurityRole;                               // autoWired
	
	// List headers
	protected Listheader   listheader_RoleApp;                                 // autoWired
	protected Listheader   listheader_RoleCd;                                  // autoWired
	protected Listheader   listheader_RoleDesc;                                // autoWired
	protected Listheader   listheader_RoleCategory;                            // autoWired
	protected Listheader   listheader_RecordStatus;                            // autoWired
	protected Listheader   listheader_RecordType;                              // autoWired
	protected Panel        securityRolesSeekPanel;                             // autoWired
	protected Panel        securityRolesListPanel;                             // autoWired
	
	//Buttons
	protected Button       btnHelp;                                            // autoWired
	protected Button       button_SecurityRoleList_NewSecurityRole;            // autoWired
	protected Button       button_SecurityRoleList_SecurityRoleSearchDialog;   // autoWired
	protected Button       button_SecurityRoleList_PrintList;                  // autoWired
	protected JdbcSearchObject<SecurityRole>  searchObj;                       // NEEDED for the ReUse in the SearchWindow
	private transient SecurityRoleService securityRoleService;
	private transient WorkFlowDetails      workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SecurityRoleListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 *  Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRole object in a
	 * Map.
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityRoleList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SecurityRole");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityRole");

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
		this.borderLayout_SecurityRoleList.setHeight(getBorderLayoutHeight());
		// set the paging parameters
		this.pagingSecurityRoleList.setPageSize(getListRows());
		this.pagingSecurityRoleList.setDetailed(true);
		this.listheader_RoleApp.setSortAscending(new FieldComparator("roleApp", true));
		this.listheader_RoleApp.setSortDescending(new FieldComparator("roleApp", false));
		this.listheader_RoleCd.setSortAscending(new FieldComparator("roleCd", true));
		this.listheader_RoleCd.setSortDescending(new FieldComparator("roleCd", false));
		this.listheader_RoleDesc.setSortAscending(new FieldComparator("roleDesc", true));
		this.listheader_RoleDesc.setSortDescending(new FieldComparator("roleDesc", false));
		this.listheader_RoleCategory.setSortAscending(new FieldComparator("roleCategory", true));
		this.listheader_RoleCategory.setSortDescending(new FieldComparator("roleCategory", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<SecurityRole>(SecurityRole.class,getListRows());
		this.searchObj.addSort("roleCd", false);

		this.searchObj.addTabelName("SecRoles_View");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_SecurityRoleList_NewSecurityRole.setVisible(true);
			} else {
				button_SecurityRoleList_NewSecurityRole.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SecurityRoleList_NewSecurityRole.setVisible(false);
			this.button_SecurityRoleList_SecurityRoleSearchDialog.setVisible(false);
			this.button_SecurityRoleList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSecurityRole,this.pagingSecurityRoleList);
			// set the itemRenderer
			this.listBoxSecurityRole.setItemRenderer(new SecurityRoleListModelItemRenderer());
		}	
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		
		getUserWorkspace().alocateAuthorities("SecurityRoleList");
		this.button_SecurityRoleList_NewSecurityRole.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityRoleList_NewSecurityRole"));
		this.button_SecurityRoleList_SecurityRoleSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityRoleList_SecurityRoleFindDialog"));
		this.button_SecurityRoleList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityRoleList_PrintList"));
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.administration.securityroles.model.SecurityRoleListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityRoleItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected SecurityRole object
		final Listitem item = this.listBoxSecurityRole.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SecurityRole aSecurityRole = (SecurityRole) item.getAttribute("data");
			final SecurityRole securityRole = getSecurityRoleService().getSecurityRoleById(aSecurityRole.getId());
			if(securityRole==null){
				String[] errorParm= new String[3];
				errorParm[0]=PennantJavaUtil.getLabel("label_RoleID") + ":"+ aSecurityRole.getRoleID();
				errorParm[1] = PennantJavaUtil.getLabel("label_RoleCode") + ":"+ aSecurityRole.getRoleCd();

				String[] valueParm= new String[3];
				valueParm[0]	=	String.valueOf(aSecurityRole.getRoleID());
				valueParm[1] 	= 	aSecurityRole.getRoleCd();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005"
						, errorParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				String whereCond =  " AND RoleID="+ securityRole.getRoleID()+" AND version=" + securityRole.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "SecurityRole", whereCond, securityRole.getTaskId(), 
							securityRole.getNextTaskId());
					if (userAcces){
						showDetailView(securityRole);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(securityRole);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the SecurityRole dialog with a new empty entry. <br>
	 */
	public void onClick$button_SecurityRoleList_NewSecurityRole(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// create a new SecurityRole object, We GET it from the back end.
		final SecurityRole aSecurityRole = getSecurityRoleService().getNewSecurityRole();
		showDetailView(aSecurityRole);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param SecurityRole (aSecurityRole)
	 * @throws Exception
	 */
	private void showDetailView(SecurityRole aSecurityRole) throws Exception {
		logger.debug("Entering ");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aSecurityRole.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSecurityRole.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityRole", aSecurityRole);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox List model. This is
		 * fine for synchronizing the data in the SecurityRoleListbox from the
		 * dialog when we do a delete, edit or insert a SecurityRole.
		 */
		map.put("securityRoleListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityRole/SecurityRoleDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_SecurityRoleList);
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
		this.pagingSecurityRoleList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SecurityRoleList, event);
		this.window_SecurityRoleList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 *  call the SecurityRole dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SecurityRoleList_SecurityRoleSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		/*
		 * we can call our SecurityRoleDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SecurityRole. For handed over
		 * these parameter only a Map is accepted. So we put the SecurityRole object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityRoleCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxSecurityRole", this.listBoxSecurityRole);
		map.put("pagingSecurityRoleList",this.pagingSecurityRoleList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityRole/SecurityRoleSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the securityRoles print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecurityRoleList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		new PTListReportUtils("SecRoles", getSearchObj(),this.pagingSecurityRoleList.getTotalSize()+1);
		logger.debug("Leaving " + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}
	public SecurityRoleService getSecurityRoleService() {
		return this.securityRoleService;
	}

	public JdbcSearchObject<SecurityRole> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SecurityRole> searchObj) {
		this.searchObj = searchObj;
	}
}