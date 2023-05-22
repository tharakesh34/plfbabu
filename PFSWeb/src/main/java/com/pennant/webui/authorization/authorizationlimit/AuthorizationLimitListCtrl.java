/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : AuthorizationLimitListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-04-2018 * *
 * Modified Date : 06-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-04-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.authorization.authorizationlimit;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.authorization.AuthorizationLimit;
import com.pennant.backend.service.authorization.AuthorizationLimitService;
import com.pennant.webui.authorization.authorizationlimit.model.AuthorizationLimitListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Authorization/AuthorizationLimit/AuthorizationLimitList.zul file.
 * 
 */
public class AuthorizationLimitListCtrl extends GFCBaseListCtrl<AuthorizationLimit> {
	private static final long serialVersionUID = 1L;

	protected Window window_AuthorizationLimitList;
	protected Borderlayout borderLayout_AuthorizationLimitList;
	protected Paging pagingAuthorizationLimitList;
	protected Listbox listBoxAuthorizationLimit;

	// List headers
	protected Listheader listheader_UserID;
	protected Listheader listheader_RoleId;
	protected Listheader listheader_LimitName;
	protected Listheader listheader_LimitAmount;
	protected Listheader listheader_ExpiryDate;
	protected Listheader listheader_HoldStartDate;
	protected Listheader listheader_HoldExpiryDate;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AuthorizationLimitList_NewAuthorizationLimit;
	protected Button button_AuthorizationLimitList_AuthorizationLimitSearch;

	// Search Fields
	protected ExtendedCombobox userID; // autowired
	protected ExtendedCombobox roleId; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_UserID;
	protected Listbox sortOperator_RoleId;
	protected Listbox sortOperator_Active;

	protected Label label_AuthorizationLimitList_UserID;
	protected Label label_AuthorizationLimitList_RoleId;
	protected Label label_AuthorizationLimitList_Active;

	private transient AuthorizationLimitService authorizationLimitService;
	private String module;
	private int limitType = 0;
	private String hold;

	/**
	 * default constructor.<br>
	 */
	public AuthorizationLimitListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		this.module = getArgument("module");
		this.hold = getArgument("hold");
		try {
			limitType = Integer.parseInt(getArgument("limitType"));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (StringUtils.equals("FIN", module) && limitType == 1) {
			if (StringUtils.equals("Y", hold)) {
				super.moduleCode = "FinanceUserAuthorizationLimitHold";
				super.pageRightName = "FinanceUserAuthorizationLimitHoldList";
				super.tableName = "Auth_Limits_AView";
				super.queueTableName = "Auth_Limits_HView";
				super.enquiryTableName = "Auth_Limits_HView";
			} else {
				super.moduleCode = "FinanceUserAuthorizationLimit";
				super.pageRightName = "FinanceUserAuthorizationLimitList";
				super.tableName = "Auth_Limits_AView";
				super.queueTableName = "Auth_Limits_View";
				super.enquiryTableName = "Auth_Limits_View";

			}
		} else if (StringUtils.equals("FIN", module) && limitType == 2) {
			if (StringUtils.equals("Y", hold)) {
				super.moduleCode = "FinanceRoleAuthorizationLimitHold";
				super.pageRightName = "FinanceRoleAuthorizationLimitHoldList";
				super.tableName = "Auth_Limits_AView";
				super.queueTableName = "Auth_Limits_HView";
				super.enquiryTableName = "Auth_Limits_HView";
			} else {
				super.moduleCode = "FinanceRoleAuthorizationLimit";
				super.pageRightName = "FinanceRoleAuthorizationLimitList";
				super.tableName = "Auth_Limits_AView";
				super.queueTableName = "Auth_Limits_View";
				super.enquiryTableName = "Auth_Limits_View";
			}

		} else {
			super.moduleCode = "FinanceAuthorizationLimit";
			super.pageRightName = "AuthorizationLimitList";
			super.tableName = "Auth_Limits_AView";
			super.queueTableName = "Auth_Limits_View";
			super.enquiryTableName = "Auth_Limits_View";
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AuthorizationLimitList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AuthorizationLimitList, borderLayout_AuthorizationLimitList, listBoxAuthorizationLimit,
				pagingAuthorizationLimitList);
		setItemRender(new AuthorizationLimitListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AuthorizationLimitList_AuthorizationLimitSearch);

		if (StringUtils.equals("Y", hold)) {
			registerButton(button_AuthorizationLimitList_NewAuthorizationLimit,
					"button_AuthorizationLimitList_NewAuthorizationLimit", false);
		} else {
			registerButton(button_AuthorizationLimitList_NewAuthorizationLimit,
					"button_AuthorizationLimitList_NewAuthorizationLimit", true);
		}

		registerField("id");
		registerField("limitType");

		if (limitType == 1) {
			registerField("usrLogin", listheader_UserID, SortOrder.NONE, userID, sortOperator_UserID,
					Operators.DEFAULT);
			registerField("userID");
			registerField("UsrFName");
			registerField("UsrMName");
			registerField("UsrLName");

			this.userID.setModuleName("SecurityUsers");
			this.userID.setValueColumn("UsrLogin");
			this.userID.setDescColumn("UsrFName");
			this.userID.setValidateColumns(new String[] { "usrLogin" });

			label_AuthorizationLimitList_UserID.setVisible(true);
			userID.setVisible(true);
			sortOperator_UserID.setVisible(true);
			listheader_UserID.setVisible(true);
			listheader_RoleId.setVisible(false);
		}
		if (limitType == 2) {
			registerField("roleId", listheader_RoleId, SortOrder.NONE, roleId, sortOperator_RoleId, Operators.DEFAULT);
			registerField("roleCd");
			registerField("roleName");

			roleId.setWidth("100px");
			this.roleId.setModuleName("SecurityRole");
			this.roleId.setValueColumn("roleID");
			this.roleId.setDescColumn("RoleDesc");
			this.roleId.setValidateColumns(new String[] { "roleID" });

			label_AuthorizationLimitList_RoleId.setVisible(true);
			roleId.setVisible(true);
			sortOperator_RoleId.setVisible(true);
			listheader_UserID.setVisible(false);
			listheader_RoleId.setVisible(true);
		}
		registerField("limitAmount");

		registerField("expiryDate");
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);
		registerField("holdStartDate");
		registerField("holdExpiryDate");

		if (StringUtils.equals("Y", hold)) {
			label_AuthorizationLimitList_Active.setVisible(false);
			sortOperator_Active.setVisible(false);
			active.setVisible(false);
			listheader_HoldStartDate.setVisible(true);
			listheader_HoldExpiryDate.setVisible(true);
		} else {
			label_AuthorizationLimitList_Active.setVisible(true);
			sortOperator_Active.setVisible(true);
			active.setVisible(true);
			listheader_HoldStartDate.setVisible(false);
			listheader_HoldExpiryDate.setVisible(false);
		}

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		this.searchObject.addFilterEqual("module", module);
		if (limitType == 1) {
			this.searchObject.addFilterNotEqual("UserID", 0);
		} else {
			this.searchObject.addFilterNotEqual("RoleId", 0);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AuthorizationLimitList_AuthorizationLimitSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AuthorizationLimitList_NewAuthorizationLimit(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AuthorizationLimit authorizationlimit = new AuthorizationLimit();
		authorizationlimit.setNewRecord(true);
		authorizationlimit.setWorkflowId(getWorkFlowId());
		authorizationlimit.setLimitType(limitType);
		authorizationlimit.setModule(module);
		authorizationlimit.setActive(true);

		// Display the dialog page.
		doShowDialogPage(authorizationlimit);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAuthorizationLimitItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAuthorizationLimit.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AuthorizationLimit authorizationlimit = authorizationLimitService.getAuthorizationLimit(id);

		if (authorizationlimit == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =? ");

		if (doCheckAuthority(authorizationlimit, whereCond.toString(), new Object[] { authorizationlimit.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && authorizationlimit.getWorkflowId() == 0) {
				authorizationlimit.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(authorizationlimit);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param authorizationlimit The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AuthorizationLimit authorizationlimit) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("authorizationLimit", authorizationlimit);
		arg.put("authorizationLimitListCtrl", this);
		arg.put("hold", hold);

		try {
			Executions.createComponents("/WEB-INF/pages/Authorization/AuthorizationLimit/AuthorizationLimitDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setAuthorizationLimitService(AuthorizationLimitService authorizationLimitService) {
		this.authorizationLimitService = authorizationLimitService;
	}
}