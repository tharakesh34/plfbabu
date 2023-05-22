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
 * * FileName : InterfaceConfigurationListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-08-2019 *
 * * Modified Date : 10-08-2019 * * Description : * *
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
import com.pennant.backend.service.externalinterface.InterfaceConfigurationService;
import com.pennant.webui.externalinterface.InterfaceConfiguration.model.InterfaceConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ExternalInterface/InterfaceConfiguration/InterfaceConfigurationList.zul file.
 */
public class InterfaceConfigurationListCtrl extends GFCBaseListCtrl<InterfaceConfiguration> {
	private static final long serialVersionUID = 1L;

	protected Window window_InterfaceConfigurationList;
	protected Borderlayout borderLayout_InterfaceConfigurationList;
	protected Paging pagingInterfaceConfigurationList;
	protected Listbox listBoxInterfaceConfiguration;

	// List headers
	protected Listheader listheader_InterfaceConfigurationList_Code;
	protected Listheader listheader_InterfaceConfigurationList_Description;
	protected Listheader listheader_InterfaceConfigurationList_NotificationType;
	protected Listheader listheader_InterfaceConfigurationList_active;
	protected Listheader listheader_InterfaceConfigurationList_Type;
	// checkRights
	protected Button button_InterfaceConfigurationList_New;
	protected Button button_InterfaceConfigurationList_Search;

	// Search Fields
	protected Textbox code;
	protected Textbox description;
	protected Combobox type;
	protected Combobox notificationType;
	protected Checkbox active;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Type;
	protected Listbox sortOperator_NotificationType;
	protected Listbox sortOperator_active;

	private transient InterfaceConfigurationService interfaceConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public InterfaceConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InterfaceConfiguration";
		super.pageRightName = "InterfaceConfigurationList";
		super.tableName = "EXTINTERFACECONF_AVIEW";
		super.queueTableName = "EXTINTERFACECONF_AVIEW";
		super.enquiryTableName = "EXTINTERFACECONF_AVIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_InterfaceConfigurationList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_InterfaceConfigurationList, borderLayout_InterfaceConfigurationList,
				listBoxInterfaceConfiguration, pagingInterfaceConfigurationList);
		setItemRender(new InterfaceConfigurationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InterfaceConfigurationList_Search);
		registerButton(button_InterfaceConfigurationList_New, "button_InterfaceConfigurationList_New", true);

		registerField("id");
		registerField("code", listheader_InterfaceConfigurationList_Code, SortOrder.NONE, code, sortOperator_Code,
				Operators.STRING);
		registerField("description", listheader_InterfaceConfigurationList_Description, SortOrder.NONE, description,
				sortOperator_Description, Operators.STRING);
		registerField("type", listheader_InterfaceConfigurationList_Type, SortOrder.NONE, sortOperator_Type,
				Operators.STRING);
		registerField("errorCodes");
		registerField("active", listheader_InterfaceConfigurationList_active, SortOrder.NONE, active,
				sortOperator_active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_InterfaceConfigurationList_Search(Event event) {
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

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onInterfaceConfigurationItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxInterfaceConfiguration.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		InterfaceConfiguration interfaceConfiguration = interfaceConfigurationService.getInterfaceConfiguration(id);

		if (interfaceConfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(interfaceConfiguration, whereCond.toString(),
				new Object[] { interfaceConfiguration.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && interfaceConfiguration.getWorkflowId() == 0) {
				interfaceConfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(interfaceConfiguration);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param interfaceConfiguration The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(InterfaceConfiguration interfaceConfiguration) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("interfaceConfiguration", interfaceConfiguration);
		arg.put("interfaceConfigurationListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ExternalInterface/ExternalInterfaceConfiguration/InterfaceServiceList.zul", null,
					arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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

	public void setInterfaceConfigurationService(InterfaceConfigurationService interfaceConfigurationService) {
		this.interfaceConfigurationService = interfaceConfigurationService;
	}

}