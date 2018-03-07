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
package com.pennant.webui.interfacemapping;

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

import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennant.backend.service.interfacemapping.InterfaceMappingService;
import com.pennant.webui.interfacemapping.model.InterfaceMappingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/InterfaceMapping/InterfaceMappingList.zul file.
 */
public class InterfaceMappingListCtrl extends GFCBaseListCtrl<InterfaceMapping> {
	private static final long	serialVersionUID	= 5327118548986437717L;
	private static final Logger	logger				= Logger.getLogger(InterfaceMappingListCtrl.class);

	protected Window							window_InterfaceMappingList;
	protected Borderlayout						borderLayout_InterfaceMappingList;
	protected Listbox							listBoxInterfaceMapping;
	protected Paging							pagingInterfaceMappingList;

	protected Listheader						listheader_InterfaceName;
	protected Listheader						listheader_InterfaceField;

	protected Button							button_InterfaceMappingList_NewInterfaceMapping;
	protected Button							button_InterfaceMappingList_InterfaceMappingSearchDialog;

	protected Textbox							interfaceName;
	protected Textbox							interfaceField;

	protected Listbox							sortOperator_interfaceName;
	protected Listbox							sortOperator_interfaceField;

	private transient InterfaceMappingService	interfaceMappingService;

	/**
	 * The default constructor.
	 */
	public InterfaceMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InterfaceMapping";
		super.pageRightName = "InterfaceMappingList";
		super.tableName = "InterfaceMapping_AView";
		super.queueTableName = "InterfaceMapping_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_InterfaceMappingList(Event event) {
		// Set the page level components.
		setPageComponents(window_InterfaceMappingList, borderLayout_InterfaceMappingList, listBoxInterfaceMapping,
				pagingInterfaceMappingList);
		setItemRender(new InterfaceMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InterfaceMappingList_NewInterfaceMapping,
				"button_InterfaceMappingList_NewInterfaceMapping", true);
		registerButton(button_InterfaceMappingList_InterfaceMappingSearchDialog);

		registerField("interfaceMappingId");

		registerField("interfaceName", listheader_InterfaceName, SortOrder.ASC, interfaceName,
				sortOperator_interfaceName, Operators.STRING);
		registerField("interfaceField", listheader_InterfaceField, SortOrder.NONE, interfaceField,
				sortOperator_interfaceField, Operators.STRING);
		
		
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
	public void onClick$button_InterfaceMappingList_InterfaceMappingSearchDialog(Event event) {
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
	public void onClick$button_InterfaceMappingList_NewInterfaceMapping(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		InterfaceMapping interfaceMapping = new InterfaceMapping();
		interfaceMapping.setNewRecord(true);
		interfaceMapping.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(interfaceMapping);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onInterfaceMappingItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record. 

		Listitem selectedItem = this.listBoxInterfaceMapping.getSelectedItem();

		// Get the selected entity. 
		long id = (long) selectedItem.getAttribute("id");
		InterfaceMapping interfaceMapping = interfaceMappingService.getInterfaceMappingById(id);

		if (interfaceMapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record. 
		String whereCond = " AND InterfaceMappingID='" + interfaceMapping.getInterfaceMappingId() + "' AND version="
				+ interfaceMapping.getVersion() + " ";

		if (doCheckAuthority(interfaceMapping, whereCond)) { // Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && interfaceMapping.getWorkflowId() == 0) {
				interfaceMapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(interfaceMapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param interfaceMapping
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(InterfaceMapping interfaceMapping) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("interfaceMapping", interfaceMapping);
		arg.put("interfaceMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/InterfaceMapping/InterfaceMappingDialog.zul", null,
					arg);
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

	
	public void setInterfaceMappingService(InterfaceMappingService interfaceMappingService) {
		this.interfaceMappingService = interfaceMappingService;
	}
	 
}