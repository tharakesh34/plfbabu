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
 * FileName    		:  SysNotificationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.sysnotification;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.SysNotification;
import com.pennant.backend.service.applicationmaster.SysNotificationService;
import com.pennant.webui.applicationmaster.sysnotification.model.SysNotificationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SysNotification/SysNotificationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SysNotificationListCtrl extends GFCBaseListCtrl<SysNotification> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SysNotificationListCtrl.class);

	protected Window window_SysNotificationList;
	protected Borderlayout borderLayout_SysNotificationList;
	protected Paging pagingSysNotificationList;
	protected Listbox listBoxSysNotification;

	protected Listheader listheader_QueryCode;
	protected Listheader listheader_Description;
	protected Listheader listheader_Template;

	protected Button button_SysNotificationList_NewSysNotification;
	protected Button button_SysNotificationList_SysNotificationSearch;

	protected Textbox queryCode;
	protected Textbox template;
	protected Textbox description;

	protected Listbox sortOperator_QueryCode;
	protected Listbox sortOperator_Template;
	protected Listbox sortOperator_Description;

	private transient SysNotificationService sysNotificationService;

	String sendNotification = "";

	/**
	 * default constructor.<br>
	 */
	public SysNotificationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SysNotification";
		super.tableName = "SysNotification_AView";
		super.queueTableName = "SysNotification_View";
		
		this.sendNotification = getArgument("sendNotification");
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SysNotificationList(Event event) {
		// Set the page level components.
		setPageComponents(window_SysNotificationList, borderLayout_SysNotificationList, listBoxSysNotification,
				pagingSysNotificationList);
		setItemRender(new SysNotificationListModelItemRenderer());

		// Register buttons and fields.
		if ("Y".equals(sendNotification)) {
			registerButton(button_SysNotificationList_NewSysNotification, RIGHT_NOT_ACCESSIBLE, true);
		} else {
			registerButton(button_SysNotificationList_NewSysNotification, null, true);
		}
		
		registerButton(button_SysNotificationList_SysNotificationSearch);

		registerField("sysNotificationId");
		registerField("queryCode", listheader_QueryCode, SortOrder.NONE, queryCode, sortOperator_QueryCode,
				Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("templateCode", listheader_Template, SortOrder.NONE, template, sortOperator_Template,
				Operators.STRING);
		registerField("Doctype");
		registerField("DocName");
		registerField("DocImage");

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
	public void onClick$button_SysNotificationList_SysNotificationSearch(Event event) {
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
	public void onClick$button_SysNotificationList_NewSysNotification(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SysNotification sysNotification = new SysNotification();
		sysNotification.setNewRecord(true);
		sysNotification.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(sysNotification);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSysNotificationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSysNotification.getSelectedItem();
		SysNotification sysNotification = null;

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");

		if ("Y".equals(sendNotification)) {
			sysNotification = sysNotificationService.getApprovedSysNotificationById(id, true);
		} else {
			sysNotification = sysNotificationService.getApprovedSysNotificationById(id, false);
		}

		if (sysNotification == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		doShowDialogPage(sysNotification);
		
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param sysNotification
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SysNotification sysNotification) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("sysNotification", sysNotification);
		arg.put("sysNotificationListCtrl", this);
		arg.put("sendNotification", sendNotification);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SystemNotifications/SysNotificationDialog.zul", null, arg);
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

	public void setSysNotificationService(SysNotificationService sysNotificationService) {
		this.sysNotificationService = sysNotificationService;
	}
}