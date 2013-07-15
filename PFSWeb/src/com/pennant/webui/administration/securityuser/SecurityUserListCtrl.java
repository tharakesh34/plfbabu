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
 * FileName    		:  SecurityUserListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityuser;

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
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.administration.securityuser.model.SecurityUserListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityUserListCtrl extends GFCBaseListCtrl<SecurityUser> implements Serializable {

	private static final long serialVersionUID = 3104549665882133520L;
	private final static Logger logger = Logger.getLogger(SecurityUserListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SecurityUserList;                           // autoWired
	protected Borderlayout borderLayout_SecurityUserList;                     // autoWired
	protected Paging       pagingSecurityUserList;                            // autoWired
	protected Listbox      listBoxSecurityUser;                               // autoWired
	
	// List headers
	protected Listheader   listheader_UsrLogin;                                // autoWired
	protected Listheader   listheader_UsrFName;                                // autoWired
	protected Listheader   listheader_UsrMName;                                // autoWired
	protected Listheader   listheader_UsrLName;                                // autoWired
	protected Listheader   listheader_UsrCanOverrideLimits;                    // autoWired
	protected Listheader   listheader_UsrAcExp;                                // autoWired
	protected Listheader   listheader_UsrCredentialsExp;                       // autoWired
	protected Listheader   listheader_UsrAcLocked;                             // autoWired
	protected Listheader   listheader_UsrDftAppCode;                           // autoWired
	protected Listheader   listheader_UsrBranchCode;                           // autoWired
	protected Listheader   listheader_UsrDeptCode;                             // autoWired
	protected Listheader   listheader_RecordStatus;                            // autoWired
	protected Listheader   listheader_RecordType;                              // autoWired
	protected Panel        securityUserSeekPanel;                              // autoWired
	protected Panel        securityUserListPanel;                              // autoWired
	
	// checkRights
	protected Button       btnHelp;                                            // autoWired
	protected Button       button_SecurityUserList_NewSecurityUser;            // autoWired
	protected Button       button_SecurityUserList_SecurityUserSearchDialog;   // autoWired
	protected Button       button_SecurityUserList_PrintList;                  // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SecurityUser> searchObj;

	private transient SecurityUserService securityUserService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SecurityUserListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 *  Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityUser object in a
	 * Map.
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityUserList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SecurityUser");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityUser");

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

		this.borderLayout_SecurityUserList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSecurityUserList.setPageSize(getListRows());
		this.pagingSecurityUserList.setDetailed(true);
		this.listheader_UsrLogin.setSortAscending(new FieldComparator("usrLogin", true));
		this.listheader_UsrLogin.setSortDescending(new FieldComparator("usrLogin", false));
		this.listheader_UsrFName.setSortAscending(new FieldComparator("usrFName", true));
		this.listheader_UsrFName.setSortDescending(new FieldComparator("usrFName", false));
		this.listheader_UsrMName.setSortAscending(new FieldComparator("usrMName", true));
		this.listheader_UsrMName.setSortDescending(new FieldComparator("usrMName", false));
		this.listheader_UsrLName.setSortAscending(new FieldComparator("usrLName", true));
		this.listheader_UsrLName.setSortDescending(new FieldComparator("usrLName", false));
		this.listheader_UsrCanOverrideLimits.setSortAscending(new FieldComparator("usrCanOverrideLimits", true));
		this.listheader_UsrCanOverrideLimits.setSortDescending(new FieldComparator("usrCanOverrideLimits", false));
		this.listheader_UsrAcExp.setSortAscending(new FieldComparator("usrAcExp", true));
		this.listheader_UsrAcExp.setSortDescending(new FieldComparator("usrAcExp", false));
		this.listheader_UsrCredentialsExp.setSortAscending(new FieldComparator("usrCredentialsExp", true));
		this.listheader_UsrCredentialsExp.setSortDescending(new FieldComparator("usrCredentialsExp", false));
		this.listheader_UsrAcLocked.setSortAscending(new FieldComparator("usrAcLocked", true));
		this.listheader_UsrAcLocked.setSortDescending(new FieldComparator("usrAcLocked", false));
		this.listheader_UsrDftAppCode.setSortAscending(new FieldComparator("usrDftAppCode", true));
		this.listheader_UsrDftAppCode.setSortDescending(new FieldComparator("usrDftAppCode", false));
		this.listheader_UsrBranchCode.setSortAscending(new FieldComparator("usrBranchCode", true));
		this.listheader_UsrBranchCode.setSortDescending(new FieldComparator("usrBranchCode", false));
		this.listheader_UsrDeptCode.setSortAscending(new FieldComparator("usrDeptCode", true));
		this.listheader_UsrDeptCode.setSortDescending(new FieldComparator("usrDeptCode", false));

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
		this.searchObj = new JdbcSearchObject<SecurityUser>(SecurityUser.class,getListRows());
		this.searchObj.addSort("usrLogin", false);
		this.searchObj.addTabelName("SecUsers_View");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_SecurityUserList_NewSecurityUser.setVisible(true);
			} else {
				button_SecurityUserList_NewSecurityUser.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SecurityUserList_NewSecurityUser.setVisible(false);
			this.button_SecurityUserList_SecurityUserSearchDialog.setVisible(false);
			this.button_SecurityUserList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSecurityUser,this.pagingSecurityUserList);
			// set the itemRenderer
			this.listBoxSecurityUser.setItemRenderer(new SecurityUserListModelItemRenderer());

		}	logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 * @return void
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		
		getUserWorkspace().alocateAuthorities("SecurityUserList");
		this.button_SecurityUserList_NewSecurityUser.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityUserList_NewSecurityUser"));
		this.button_SecurityUserList_SecurityUserSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityUserList_SecurityUserFindDialog"));
		this.button_SecurityUserList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityUserList_PrintList"));
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.administration.securityusers.model.SecurityUserListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityUserItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected SecurityUser object
		final Listitem item = this.listBoxSecurityUser.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SecurityUser aSecurityUser = (SecurityUser) item.getAttribute("data");
			final SecurityUser securityUser = getSecurityUserService().getSecurityUserById(aSecurityUser.getId());

			if(securityUser==null){
				String[] errorParm= new String[3];
				errorParm[0]=PennantJavaUtil.getLabel("label_UsrID") + ":"+ aSecurityUser.getUsrID();
				errorParm[1] = PennantJavaUtil.getLabel("label_UsrLogin") + ":"+ aSecurityUser.getUsrLogin();

				String[] valueParm= new String[3];
				valueParm[0]	=	String.valueOf(aSecurityUser.getUsrID());
				valueParm[1] 	= 	aSecurityUser.getUsrLogin();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005"
						, errorParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{

				String whereCond =  " AND UsrID="+ securityUser.getUsrID()+" AND version=" + securityUser.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "SecurityUser", whereCond, securityUser.getTaskId()
							, securityUser.getNextTaskId());
					if (userAcces){
						showDetailView(securityUser);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(securityUser);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the SecurityUser dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SecurityUserList_NewSecurityUser(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// create a new SecurityUser object, We GET it from the back end.
		final SecurityUser aSecurityUser = getSecurityUserService().getNewSecurityUser();
		showDetailView(aSecurityUser);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param aSecurityUser (SecurityUser)
	 * @throws Exception
	 */
	private void showDetailView(SecurityUser aSecurityUser) throws Exception {
		logger.debug("Entering ");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aSecurityUser.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSecurityUser.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityUser", aSecurityUser);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox List model. This is
		 * fine for synchronizing the data in the SecurityUserListbox from the
		 * dialog when we do a delete, edit or insert a SecurityUser.
		 */
		map.put("securityUserListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityUser/SecurityUserDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_SecurityUserList);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedExceptiono
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		this.pagingSecurityUserList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SecurityUserList, event);
		this.window_SecurityUserList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * call the SecurityUser dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SecurityUserList_SecurityUserSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our SecurityUserDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SecurityUser. For handed over
		 * these parameter only a Map is accepted. So we put the SecurityUser object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityUserList", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxSecurityUser", this.listBoxSecurityUser);
		map.put("pagingSecurityUserList", this.pagingSecurityUserList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityUser/SecurityUserSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the securityUser print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecurityUserList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTReportUtils.getReport("SecurityUser", getSearchObj());
		logger.debug("Leaving " + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
	public SecurityUserService getSecurityUserService() {
		return this.securityUserService;
	}

	public JdbcSearchObject<SecurityUser> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SecurityUser> searchObj) {
		this.searchObj = searchObj;
	}
}