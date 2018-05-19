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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.webui.administration.securityuser.model.SecurityUserListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserList.zul file.
 */
public class SecurityUserListCtrl extends GFCBaseListCtrl<SecurityUser> {
	private static final long serialVersionUID = 3104549665882133520L;
	private static final Logger logger = Logger.getLogger(SecurityUserListCtrl.class);

	protected Window window_SecurityUserList;
	protected Borderlayout borderLayout_SecurityUserList;
	protected Paging pagingSecurityUserList;
	protected Listbox listBoxSecurityUser;

	protected Listheader listheader_UsrLogin;
	protected Listheader listheader_UsrFName;
	protected Listheader listheader_UsrMName;
	protected Listheader listheader_UsrLName;
	protected Listheader listheader_UsrCanOverrideLimits;
	protected Listheader listheader_UsrAcExp;
	protected Listheader listheader_UsrCredentialsExp;
	protected Listheader listheader_UsrAcLocked;
	protected Listheader listheader_UsrDftAppCode;
	protected Listheader listheader_UsrBranchCode;
	protected Listheader listheader_UsrDeptCode;
	protected Panel securityUserSeekPanel;
	protected Panel securityUserListPanel;

	protected Button button_SecurityUserList_NewSecurityUser;
	protected Button button_SecurityUserList_SecurityUserSearch;

	protected Textbox usrLogin;
	protected Textbox usrFName;
	protected Textbox usrMName;
	protected Textbox usrLName;
	protected Textbox usrMobile;
	protected Textbox usrEmail;
	protected Checkbox usrEnabled;
	protected Checkbox usrAcExp;
	protected Checkbox usrCredentialsExp;
	protected Checkbox usrAcLocked;
	protected Textbox usrDeptCode;

	protected Listbox sortOperator_UsrLogin;
	protected Listbox sortOperator_UsrFName;
	protected Listbox sortOperator_UsrMName;
	protected Listbox sortOperator_UsrLName;
	protected Listbox sortOperator_UsrMobile;
	protected Listbox sortOperator_UsrEmail;
	protected Listbox sortOperator_UsrEnabled;
	protected Listbox sortOperator_UsrAcExp;
	protected Listbox sortOperator_UsrCredentialsExp;
	protected Listbox sortOperator_UsrAcLocked;
	protected Listbox sortOperator_UsrDeptCode;

	private transient SecurityUserService securityUserService;

	private transient String moduleType;

	/**
	 * default constructor.<br>
	 */
	protected SecurityUserListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if ("PSWDRST".equals(this.moduleType)) {
			this.searchObject.addFilterEqual("authType", AuthenticationType.DAO.name());
		}
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "SecurityUser";
		this.moduleType = getArgument("moduleType");

		if ("USER".equals(this.moduleType) || "PSWDRST".equals(this.moduleType)) {
			super.pageRightName = "SecurityUserList";
		} else {
			super.pageRightName = "SecurityUserRolesList";
		}
		
		if("USEROPR".equals(this.moduleType)){
			super.queueTableName = "SecUsers_RView";
		}else{
			super.queueTableName = "SecUsers_View";
		}

		super.tableName = "SecUsers_AView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SecurityUserList(Event event) {
		// Set the page level components.
		setPageComponents(window_SecurityUserList, borderLayout_SecurityUserList, listBoxSecurityUser,
				pagingSecurityUserList);
		setItemRender(new SecurityUserListModelItemRenderer());

		// Register buttons and fields.

		if ("USER".equals(this.moduleType)) {
			registerButton(button_SecurityUserList_NewSecurityUser, "button_SecurityUserList_NewSecurityUser", true);
			registerButton(button_SecurityUserList_SecurityUserSearch);
		} else if ("PSWDRST".equals(this.moduleType)) {
			registerButton(button_SecurityUserList_SecurityUserSearch);
		} else {
			registerButton(button_SecurityUserList_SecurityUserSearch);
		}

		registerField("UsrID");
		registerField("UsrLogin", listheader_UsrLogin, SortOrder.ASC, usrLogin, sortOperator_UsrLogin, Operators.STRING);
		registerField("UsrFName", listheader_UsrFName, SortOrder.NONE, usrFName, sortOperator_UsrFName,
				Operators.STRING);
		registerField("UsrMName", listheader_UsrMName, SortOrder.NONE, usrMName, sortOperator_UsrMName,
				Operators.STRING);
		registerField("UsrLName", listheader_UsrLName, SortOrder.NONE, usrLName, sortOperator_UsrLName,
				Operators.STRING);
		registerField("UsrDeptCode", listheader_UsrDeptCode, SortOrder.NONE, usrDeptCode, sortOperator_UsrDeptCode,
				Operators.STRING);
		registerField("lovDescUsrDeptCodeName");
		registerField("usrCanOverrideLimits");
		registerField("usrAcExp", listheader_UsrAcExp, SortOrder.NONE, usrAcExp, sortOperator_UsrAcExp,
				Operators.SIMPLE);
		registerField("usrAcLocked", listheader_UsrAcLocked, SortOrder.NONE, usrAcLocked, sortOperator_UsrAcLocked,
				Operators.SIMPLE);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_SecurityUserList_SecurityUserSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_SecurityUserList_NewSecurityUser(Event event) {
		logger.debug("Entering " + event.toString());
		// Create a new entity.
		SecurityUser aSecurityUser = new SecurityUser();
		aSecurityUser.setNewRecord(true);
		aSecurityUser.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aSecurityUser);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSecurityUserItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSecurityUser.getSelectedItem();
		SecurityUser aSecurityUser = new SecurityUser();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		if ("USER".equals(this.moduleType)) {
			aSecurityUser = securityUserService.getSecurityUserById(id);
		} else if ("USERENQ".equals(this.moduleType)) {
			aSecurityUser = securityUserService.getApprovedSecurityUserById(id);
		} else if ("USEROPR".equals(this.moduleType)) {
			aSecurityUser = securityUserService.getSecurityUserOperationsById(id);
		} else if ("USEROPRENQ".equals(this.moduleType)) {
			aSecurityUser = securityUserService.getApprovedSecurityUserOperationsById(id);
		} else if ("PSWDRST".equals(this.moduleType)) {
			aSecurityUser = securityUserService.getSecurityUserById(id);
		}

		if (aSecurityUser == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND UsrID=" + aSecurityUser.getUsrID() + " AND version=" + aSecurityUser.getVersion()
				+ " ";

		if (doCheckAuthority(aSecurityUser, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aSecurityUser.getWorkflowId() == 0) {
				aSecurityUser.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aSecurityUser);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSecurityUser
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SecurityUser aSecurityUser) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("securityUser", aSecurityUser);
		arg.put("securityUserListCtrl", this);

		try {
			if ("USEROPR".equals(this.moduleType) || "USEROPRENQ".equals(this.moduleType)) {
				Executions.createComponents(
						"/WEB-INF/pages/Administration/SecurityUserOperations/SecurityUserOperationsDialog.zul", null,
						arg);
			} else if ("PSWDRST".equals(this.moduleType)) {
				Executions.createComponents(
						"/WEB-INF/pages/PasswordReset/SecurityUser/SecurityUserChangePasswordDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Administration/SecurityUser/SecurityUserDialog.zul", null,
						arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
}