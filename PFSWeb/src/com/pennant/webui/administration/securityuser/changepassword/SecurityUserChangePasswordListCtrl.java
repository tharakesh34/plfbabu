
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
 * FileName    		:  SecurityUserListCtrl.java                                            * 	  
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

package com.pennant.webui.administration.securityuser.changepassword;

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
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserChangePasswordList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityUserChangePasswordListCtrl extends GFCBaseListCtrl<SecurityUser>
implements Serializable {

	private static final long serialVersionUID = -1643080116803514723L;
	private final static Logger logger = Logger
	.getLogger(SecurityUserChangePasswordListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SecurityUserChangePassWordList;             // autowired
	protected Borderlayout borderLayout_SecurityUserList;                     // autowired
	private Paging         pagingSecurityUserList;                            // autowired
	private Listbox        listBoxSecurityUser;                               // autowired
	// List headers
	protected Listheader   listheader_UsrLogin;                                // autowired
	protected Listheader   listheader_UsrFName;                                // autowired
	protected Listheader   listheader_UsrMName;                                // autowired
	protected Listheader   listheader_UsrLName;                                // autowired
	protected Listheader   listheader_UsrCanOverrideLimits;                    // autowired
	protected Listheader   listheader_UsrAcExp;                                // autowired
	protected Listheader   listheader_UsrCredentialsExp;                       // autowired
	protected Listheader   listheader_UsrAcLocked;                             // autowired
	protected Listheader   listheader_UsrDftAppCode;                           // autowired
	protected Listheader   listheader_UsrBranchCode;                           // autowired
	protected Listheader   listheader_UsrDeptCode;                             // autowired
	protected Listheader   listheader_RecordStatus;                            // autowired
	protected Listheader   listheader_RecordType;                              // autowired
	protected Panel        securityUserSeekPanel;                             // autowired
	protected Panel        securityUserListPanel;                             // autowired
	// checkRights
	protected Button       btnHelp;                                            // autowired
	protected Button       button_SecurityUserList_SecurityUserSearchDialog; // autowired
	protected Button       button_SecurityUserList_PrintList;                 // autowired
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SecurityUser> searchObj;
	private transient SecurityUserService securityUserService;
	private transient WorkFlowDetails      workFlowDetails=null;

	/**
	 * default constructor.<br>
	 * 
	 */
	public  SecurityUserChangePasswordListCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected SecurityUser object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityUserChangePassWordList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		this.button_SecurityUserList_PrintList.setVisible(false);

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
		this.getPagingSecurityUserList().setPageSize(getListRows());
		this.getPagingSecurityUserList().setDetailed(true);
		this.listheader_UsrLogin.setSortAscending(new FieldComparator("usrLogin", true));
		this.listheader_UsrLogin.setSortDescending(new FieldComparator("usrLogin", false));
		this.listheader_UsrFName.setSortAscending(new FieldComparator("usrFName", true));
		this.listheader_UsrFName.setSortDescending(new FieldComparator("usrFName", false));
		this.listheader_UsrMName.setSortAscending(new FieldComparator("usrMName", true));
		this.listheader_UsrMName.setSortDescending(new FieldComparator("usrMName", false));
		this.listheader_UsrLName.setSortAscending(new FieldComparator("usrLName", true));
		this.listheader_UsrLName.setSortDescending(new FieldComparator("usrLName", false));
		this.listheader_UsrCanOverrideLimits.setSortAscending(
				new FieldComparator("usrCanOverrideLimits", true));
		this.listheader_UsrCanOverrideLimits.setSortDescending(
				new FieldComparator("usrCanOverrideLimits", false));
		this.listheader_UsrAcExp.setSortAscending(new FieldComparator("usrAcExp", true));
		this.listheader_UsrAcExp.setSortDescending(new FieldComparator("usrAcExp", false));
		this.listheader_UsrCredentialsExp.setSortAscending(
				new FieldComparator("usrCredentialsExp", true));
		this.listheader_UsrCredentialsExp.setSortDescending(
				new FieldComparator("usrCredentialsExp", false));
		this.listheader_UsrAcLocked.setSortAscending(new FieldComparator("usrAcLocked", true));
		this.listheader_UsrAcLocked.setSortDescending(new FieldComparator("usrAcLocked", false));
		this.listheader_UsrDftAppCode.setSortAscending(new FieldComparator("usrDftAppCode", true));
		this.listheader_UsrDftAppCode.setSortDescending(new FieldComparator("usrDftAppCode", false));
		this.listheader_UsrBranchCode.setSortAscending(new FieldComparator("usrBranchCode", true));
		this.listheader_UsrBranchCode.setSortDescending(new FieldComparator("usrBranchCode", false));
		this.listheader_UsrDeptCode.setSortAscending(new FieldComparator("usrDeptCode", true));
		this.listheader_UsrDeptCode.setSortDescending(new FieldComparator("usrDeptCode", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(
					new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<SecurityUser>(SecurityUser.class,getListRows());
		this.searchObj.addSort("usrLogin", false);
		this.searchObj.addTabelName("SecUsers_View");
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){

			this.button_SecurityUserList_SecurityUserSearchDialog.setVisible(false);
			this.button_SecurityUserList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj
					,this.getListBoxSecurityUser(),this.getPagingSecurityUserList());
			// set the itemRenderer
			this.getListBoxSecurityUser().setItemRenderer(new SecurityUserListModelItemRenderer());
		}	
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("SecurityUserList");
		this.button_SecurityUserList_SecurityUserSearchDialog
		.setVisible(getUserWorkspace().isAllowed("button_SecurityUserList_SecurityUserFindDialog"));
		this.button_SecurityUserList_PrintList
		.setVisible(getUserWorkspace().isAllowed("button_SecurityUserList_PrintList"));
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
	 */
	public void onClick$button_SecurityUserList_NewSecurityUser(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// create a new SecurityUser object, We GET it from the back end.
		showDetailView(getSecurityUserService().getNewSecurityUser());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param SecurityUser (aSecurityUser)
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
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SecurityUserListbox from the
		 * dialog when we do a delete, edit or insert a SecurityUser.
		 */
		map.put("SecurityUserCpwdListCtr", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/PasswordReset" +
					"/SecurityUser/SecurityUserChangePasswordDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_SecurityUserChangePassWordList);
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
		this.getPagingSecurityUserList().setActivePage(0);
		Events.postEvent("onCreate", this.window_SecurityUserChangePassWordList, event);
		this.window_SecurityUserChangePassWordList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the SecurityUser dialog
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$button_SecurityUserList_SecurityUserSearchDialog(Event event)
	throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our SecurityUserDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SecurityUser. For handed over
		 * these parameter only a Map is accepted. So we put the SecurityUser object
		 * in a HashMap.
		 */
		final HashMap map = new HashMap();
		map.put("securityUserList", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxSecurityUser", this.listBoxSecurityUser);
		map.put("pagingSecurityUserList", this.pagingSecurityUserList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityUser" +
					"/SecurityUserSearchDialog.zul",null,map);
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
	public void onClick$button_SecurityUserList_PrintList(Event event) 
	throws InterruptedException {
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

	public void setListBoxSecurityUser(Listbox listBoxSecurityUser) {
		this.listBoxSecurityUser = listBoxSecurityUser;
	}

	public Listbox getListBoxSecurityUser() {
		return listBoxSecurityUser;
	}

	public void setPagingSecurityUserList(Paging pagingSecurityUserList) {
		this.pagingSecurityUserList = pagingSecurityUserList;
	}

	public Paging getPagingSecurityUserList() {
		return pagingSecurityUserList;
	}
}