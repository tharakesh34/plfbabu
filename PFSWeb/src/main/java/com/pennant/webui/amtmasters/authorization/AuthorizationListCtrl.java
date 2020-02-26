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
 * FileName    		:  AuthorizationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2013    														*
 *                                                                  						*
 * Modified Date    :  20-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.authorization;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.service.amtmasters.AuthorizationService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.amtmasters.authorization.model.AuthorizationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/Authorization/AuthorizationList.zul file.
 */
public class AuthorizationListCtrl extends GFCBaseListCtrl<Authorization> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AuthorizationListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AuthorizationList;
	protected Borderlayout borderLayout_AuthorizationList;
	protected Paging pagingAuthorizationList;
	protected Listbox listBoxAuthorization;

	protected Listheader listheader_AuthType;
	protected Listheader listheader_AuthName;
	protected Listheader listheader_AuthDept;
	protected Listheader listheader_AuthDesig;

	protected Button button_AuthorizationList_NewAuthorization;
	protected Button button_AuthorizationList_AuthorizationSearch;

	protected Textbox authDept;
	protected Combobox authType;
	protected Textbox authName;
	protected Textbox authDesig;

	protected Listbox sortOperator_AuthType;
	protected Listbox sortOperator_AuthName;
	protected Listbox sortOperator_AuthDept;
	protected Listbox sortOperator_AuthDesig;

	private transient AuthorizationService authorizationService;

	/**
	 * default constructor.<br>
	 */
	public AuthorizationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Authorization";
		super.pageRightName = "AuthorizationList";
		super.tableName = "AMTAuthorization_AView";
		super.queueTableName = "AMTAuthorization_View";
		super.enquiryTableName = "AMTAuthorization_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_AuthorizationList(Event event) {
		// Set the page level components.
		setPageComponents(window_AuthorizationList, borderLayout_AuthorizationList, listBoxAuthorization,
				pagingAuthorizationList);
		setItemRender(new AuthorizationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AuthorizationList_NewAuthorization, "button_AuthorizationList_NewAuthorization", true);
		registerButton(button_AuthorizationList_AuthorizationSearch);

		fillComboBox(this.authType, "", PennantStaticListUtil.getAuthTypes(), "");

		registerField("authUserId");
		registerField("authType", listheader_AuthType, SortOrder.DESC, authType, sortOperator_AuthType,
				Operators.NUMERIC);
		registerField("authName", listheader_AuthName, SortOrder.NONE, authName, sortOperator_AuthName,
				Operators.STRING);
		registerField("authDept", listheader_AuthDept, SortOrder.NONE, authDept, sortOperator_AuthDept,
				Operators.STRING);
		registerField("authDesig", listheader_AuthDesig, SortOrder.NONE, authDesig, sortOperator_AuthDesig,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Entering");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_AuthorizationList_AuthorizationSearch(Event event) {
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
	public void onClick$button_AuthorizationList_NewAuthorization(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Authorization authorization = new Authorization();
		authorization.setNewRecord(true);
		authorization.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(authorization);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onAuthorizationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAuthorization.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Authorization authorization = authorizationService.getAuthorizationById(id);

		if (authorization == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where AuthUserId=?";

		if (doCheckAuthority(authorization, whereCond, new Object[] { authorization.getAuthUserId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && authorization.getWorkflowId() == 0) {
				authorization.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(authorization);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param authorization
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Authorization authorization) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("authorization", authorization);
		arg.put("authorizationListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/Authorization/AuthorizationDialog.zul", null, arg);
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

	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

}