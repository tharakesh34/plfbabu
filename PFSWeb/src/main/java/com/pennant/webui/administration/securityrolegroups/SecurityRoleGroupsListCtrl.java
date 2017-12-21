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
 * FileName    		:  SecurityRoleGroupsListCtrl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-07-2011													        *
 *                                                                   						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011    	Pennant	                 0.1                                            * 
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.administration.securityrole.model.SecurityRoleListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityRoleGroups/SecurityRoleGroupsList.zul
 * file.
 */
public class SecurityRoleGroupsListCtrl extends GFCBaseListCtrl<SecurityRole> {
	private static final long serialVersionUID = 9180819829963663964L;
	private static final Logger logger = Logger.getLogger(SecurityRoleGroupsListCtrl.class);

	protected Window window_SecurityRoleGroupsList;
	protected Borderlayout borderLayout_SecurityRoleList;
	private Listbox listBoxSecurityRole;
	private Paging pagingSecurityRoleList;

	protected Intbox roleID;
	protected Combobox roleApp;
	protected Textbox roleCd;
	protected Textbox roleDesc;
	protected Textbox roleCategory;

	protected Listbox sortOperator_roleID;
	protected Listbox sortOperator_roleApp;
	protected Listbox sortOperator_roleCd;
	protected Listbox sortOperator_roleDesc;
	protected Listbox sortOperator_roleCategory;

	protected Listheader listheader_RoleApp;
	protected Listheader listheader_RoleCd;
	protected Listheader listheader_RoleDesc;
	protected Listheader listheader_RoleCategory;
	protected Panel securityRoleSeekPanel;
	protected Panel securityRoleListPanel;

	protected Button button_SecurityRoleList_SecurityRoleSearchDialog;
	protected Button button_SecurityRoleList_PrintList;

	private transient SecurityRoleService securityRoleService;

	/**
	 * default constructor.<br>
	 */
	public SecurityRoleGroupsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SecurityRole";
		super.pageRightName = "SecurityRoleList";
		super.tableName = "SecRoles_View";
		super.queueTableName = "SecRoles_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SecurityRoleGroupsList(Event event) {
		// Set the page level components.
		setPageComponents(window_SecurityRoleGroupsList, borderLayout_SecurityRoleList, listBoxSecurityRole,
				pagingSecurityRoleList);
		setItemRender(new SecurityRoleListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SecurityRoleList_SecurityRoleSearchDialog);

		registerField("roleID");
		registerField("lovDescRoleAppName");
		registerField("roleCd", listheader_RoleCd, SortOrder.ASC, roleCd, sortOperator_roleCd, Operators.STRING);

		fillComboBox(this.roleApp, "", PennantStaticListUtil.getAppCodes(), "");
		registerField("roleApp", listheader_RoleApp, SortOrder.NONE, roleApp, sortOperator_roleApp, Operators.STRING);

		registerField("roleDesc", listheader_RoleDesc, SortOrder.NONE, roleDesc, sortOperator_roleDesc,
				Operators.STRING);
		registerField("roleCategory", listheader_RoleCategory, SortOrder.NONE, roleCategory, sortOperator_roleCategory,
				Operators.STRING);

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
	public void onClick$button_SecurityRoleList_SecurityRoleSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSecurityRoleItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSecurityRole.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		SecurityRole aSecurityRole = securityRoleService.getSecurityRoleById(id);

		if (aSecurityRole == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND RoleID=" + aSecurityRole.getRoleID() + " AND version=" + aSecurityRole.getVersion()
				+ " ";

		if (doCheckAuthority(aSecurityRole, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aSecurityRole.getWorkflowId() == 0) {
				aSecurityRole.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aSecurityRole);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSecurityRole
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SecurityRole aSecurityRole) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("securityRole", aSecurityRole);
		arg.put("securityRoleListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityRoleGroups"
					+ "/SecurityRoleGroupsDialog.zul", null, arg);
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

	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}
}