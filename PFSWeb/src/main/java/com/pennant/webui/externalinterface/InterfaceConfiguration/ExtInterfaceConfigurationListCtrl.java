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
 * * FileName : ExtInterfaceConfigurationListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-08-2019
 * * * Modified Date : 10-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.externalinterface.InterfaceConfiguration;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.service.externalinterface.ExtInterfaceConfigurationService;
import com.pennant.webui.externalinterface.InterfaceConfiguration.model.ExtInterfaceConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ExtInterface/InterfaceConfiguration/ExtInterfaceConfigurationList.zul file.
 * 
 */
public class ExtInterfaceConfigurationListCtrl extends GFCBaseListCtrl<InterfaceConfiguration> {
	private static final long serialVersionUID = 1L;

	protected Window window_ExtInterfaceConfigurationList;
	protected Borderlayout borderLayout_ExtInterfaceConfigurationList;
	protected Paging pagingExtInterfaceConfigurationList;
	protected Listbox listBoxExtInterfaceConfiguration;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_NotificationType;
	protected Listheader listheader_active;

	// checkRights
	protected Button button_ExtInterfaceConfigurationList_NewExtInterfaceConfiguration;
	protected Button button_ExtInterfaceConfigurationList_ExtInterfaceConfigurationSearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox description; // autowired
	protected Combobox type; // autowired
	protected Combobox notificationType; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Type;
	protected Listbox sortOperator_NotificationType;
	protected Listbox sortOperator_active;

	private transient ExtInterfaceConfigurationService ExtInterfaceConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public ExtInterfaceConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InterfaceConfiguration";
		super.pageRightName = "ExtInterfaceConfigurationList";
		super.tableName = "EXTINTERFACECONF_AVIEW";
		super.queueTableName = "EXTINTERFACECONF_VIEW";
		super.enquiryTableName = "EXTINTERFACECONF_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExtInterfaceConfigurationList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ExtInterfaceConfigurationList, borderLayout_ExtInterfaceConfigurationList,
				listBoxExtInterfaceConfiguration, pagingExtInterfaceConfigurationList);
		setItemRender(new ExtInterfaceConfigurationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ExtInterfaceConfigurationList_ExtInterfaceConfigurationSearch);
		registerButton(button_ExtInterfaceConfigurationList_NewExtInterfaceConfiguration,
				"button_ExtInterfaceConfigurationList_NewExtInterfaceConfiguration", true);

		registerField("id");
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("type", listheader_NotificationType, SortOrder.NONE, sortOperator_Type, Operators.STRING);
		registerField("notificationType", listheader_NotificationType, SortOrder.NONE, sortOperator_NotificationType,
				Operators.STRING);
		registerField("errorCodes");
		registerField("active", listheader_active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		button_ExtInterfaceConfigurationList_NewExtInterfaceConfiguration.setVisible(false);
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ExtInterfaceConfigurationList_ExtInterfaceConfigurationSearch(Event event) {
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
	public void onClick$button_ExtInterfaceConfigurationList_NewExtInterfaceConfiguration(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		InterfaceConfiguration Extinterfaceconfiguration = new InterfaceConfiguration();
		Extinterfaceconfiguration.setNewRecord(true);
		Extinterfaceconfiguration.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(Extinterfaceconfiguration);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onExtInterfaceConfigurationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxExtInterfaceConfiguration.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		InterfaceConfiguration Extinterfaceconfiguration = ExtInterfaceConfigurationService
				.getExtInterfaceConfiguration(id);

		if (Extinterfaceconfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(Extinterfaceconfiguration, whereCond.toString(),
				new Object[] { Extinterfaceconfiguration.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && Extinterfaceconfiguration.getWorkflowId() == 0) {
				Extinterfaceconfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(Extinterfaceconfiguration);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param Extinterfaceconfiguration The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(InterfaceConfiguration Extinterfaceconfiguration) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("InterfaceConfiguration", Extinterfaceconfiguration);
		arg.put("ExtInterfaceConfigurationListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ExternalInterface/ExternalInterfaceConfiguration/ExtInterfaceConfigurationDialog.zul",
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

	public void setExtInterfaceConfigurationService(ExtInterfaceConfigurationService ExtInterfaceConfigurationService) {
		this.ExtInterfaceConfigurationService = ExtInterfaceConfigurationService;
	}
}