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
 * * FileName : NotificationsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-05-2011 * *
 * Modified Date : 23-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.mail.notifications;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.pff.template.TemplateUtil;
import com.pennant.webui.mail.notifications.model.NotificationsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Notifications/NotificationsList.zul file.
 */
public class NotificationsListCtrl extends GFCBaseListCtrl<Notifications> {
	private static final long serialVersionUID = 5327118548986437717L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_NotificationsList;
	protected Borderlayout borderLayout_NotificationsList;
	protected Paging pagingNotificationsList;
	protected Listbox listBoxNotifications;

	protected Listheader listheader_NotificationsRuleCode;
	protected Listheader listheader_NotificationsRuleModule;
	protected Listheader listheader_NotificationsRuleCodeDesc;

	protected Button button_NotificationsList_NewNotifications;
	protected Button button_NotificationsList_NotificationsSearchDialog;

	protected Textbox ruleCode;
	protected Textbox ruleCodeDesc;
	protected Combobox ruleModule;

	protected Listbox sortOperator_ruleCode;
	protected Listbox sortOperator_ruleCodeDesc;
	protected Listbox sortOperator_ruleModule;

	Iframe report;

	private transient NotificationsService notificationsService;

	/**
	 * default constructor.<br>
	 */
	public NotificationsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Notifications";
		super.pageRightName = "NotificationsList";
		super.tableName = "Notifications_AView";
		super.queueTableName = "Notifications_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NotificationsList(Event event) {
		// Set the page level components.
		setPageComponents(window_NotificationsList, borderLayout_NotificationsList, listBoxNotifications,
				pagingNotificationsList);
		setItemRender(new NotificationsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_NotificationsList_NewNotifications, "button_NotificationsList_NewNotifications", true);
		registerButton(button_NotificationsList_NotificationsSearchDialog);

		fillComboBox(this.ruleModule, "", TemplateUtil.getModules(), "");

		registerField("ruleCode", listheader_NotificationsRuleCode, SortOrder.ASC, ruleCode, sortOperator_ruleCode,
				Operators.STRING);
		registerField("ruleCodeDesc", listheader_NotificationsRuleCodeDesc, SortOrder.NONE, ruleCodeDesc,
				sortOperator_ruleCodeDesc, Operators.STRING);
		registerField("ruleModule", listheader_NotificationsRuleModule, SortOrder.NONE, ruleModule,
				sortOperator_ruleModule, Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_NotificationsList_NotificationsSearchDialog(Event event) {
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
	public void onClick$button_NotificationsList_NewNotifications(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Notifications notifications = new Notifications();
		notifications.setNewRecord(true);
		notifications.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(notifications);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onNotificationsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxNotifications.getSelectedItem();

		// Get the selected entity.
		String ruleCode = (String) selectedItem.getAttribute("ruleCode");
		Notifications notifications = notificationsService.getNotificationsById(ruleCode);

		if (notifications == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where RuleCode=?";
		if (doCheckAuthority(notifications, whereCond, new Object[] { notifications.getRuleCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && notifications.getWorkflowId() == 0) {
				notifications.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(notifications);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aNotifications The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Notifications aNotifications) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("notifications", aNotifications);
		arg.put("notificationsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Mail/Notifications/NotificationsDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

}