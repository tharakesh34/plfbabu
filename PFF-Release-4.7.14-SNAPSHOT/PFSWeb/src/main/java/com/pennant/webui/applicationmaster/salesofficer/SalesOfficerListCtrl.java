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
 * FileName    		:  SalesOfficerListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.salesofficer;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.service.applicationmaster.SalesOfficerService;
import com.pennant.webui.applicationmaster.salesofficer.model.SalesOfficerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SalesOfficer/SalesOfficerList.zul file.
 */

public class SalesOfficerListCtrl extends GFCBaseListCtrl<SalesOfficer> {
	private static final long serialVersionUID = 2821884142332965776L;
	private static final Logger logger = Logger.getLogger(SalesOfficerListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SalesOfficerList;
	protected Borderlayout borderLayout_SalesOfficerList;
	protected Paging pagingSalesOfficerList;
	protected Listbox listBoxSalesOfficer;

	protected Textbox salesOffCode;
	protected Textbox salesOffFName;
	protected Textbox salesOffDept;
	protected Checkbox salesOffIsActive;

	protected Listbox sortOperator_salesOffCode;
	protected Listbox sortOperator_salesOffFName;
	protected Listbox sortOperator_salesOffDept;
	protected Listbox sortOperator_salesOffIsActive;

	// List headers
	protected Listheader listheader_SalesOffCode;
	protected Listheader listheader_SalesOffFName;
	protected Listheader listheader_SalesOffDept;
	protected Listheader listheader_SalesOffIsActive;

	// checkRights
	protected Button button_SalesOfficerList_NewSalesOfficer;
	protected Button button_SalesOfficerList_SalesOfficerSearchDialog;

	private transient SalesOfficerService salesOfficerService;

	/**
	 * default constructor.<br>
	 */
	public SalesOfficerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SalesOfficer";
		super.pageRightName = "SalesOfficerList";
		super.tableName = "SalesOfficers_AView";
		super.queueTableName = "SalesOfficers_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SalesOfficerList(Event event) {
		// Set the page level components.
		setPageComponents(window_SalesOfficerList, borderLayout_SalesOfficerList, listBoxSalesOfficer,
				pagingSalesOfficerList);
		setItemRender(new SalesOfficerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SalesOfficerList_NewSalesOfficer, "button_SalesOfficerList_NewSalesOfficer", true);
		registerButton(button_SalesOfficerList_SalesOfficerSearchDialog);

		registerField("salesOffCode", listheader_SalesOffCode, SortOrder.ASC, salesOffCode, sortOperator_salesOffCode,
				Operators.STRING);
		registerField("salesOffFName", listheader_SalesOffFName, SortOrder.NONE, salesOffFName,
				sortOperator_salesOffFName, Operators.STRING);
		registerField("salesOffDept", listheader_SalesOffDept, SortOrder.NONE, salesOffDept, sortOperator_salesOffDept,
				Operators.STRING);
		registerField("lovDescSalesOffDeptName");
		registerField("salesOffIsActive", listheader_SalesOffIsActive, SortOrder.NONE, salesOffIsActive,
				sortOperator_salesOffIsActive, Operators.BOOLEAN);

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
	public void onClick$button_SalesOfficerList_SalesOfficerSearchDialog(Event event) {
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
	public void onClick$button_SalesOfficerList_NewSalesOfficer(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SalesOfficer salesOfficer = new SalesOfficer();
		salesOfficer.setNewRecord(true);
		salesOfficer.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(salesOfficer);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onSalesOfficerItemDoubleClicked(Event event) {

		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSalesOfficer.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		SalesOfficer salesOfficer = salesOfficerService.getSalesOfficerById(id);

		if (salesOfficer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SalesOffCode='" + salesOfficer.getSalesOffCode() + "' AND version="
				+ salesOfficer.getVersion() + " ";

		if (doCheckAuthority(salesOfficer, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && salesOfficer.getWorkflowId() == 0) {
				salesOfficer.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(salesOfficer);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSalesOfficer
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SalesOfficer aSalesOfficer) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("salesOfficer", aSalesOfficer);
		arg.put("salesOfficerListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/SalesOfficer/SalesOfficerDialog.zul", null,
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

	public void setSalesOfficerService(SalesOfficerService salesOfficerService) {
		this.salesOfficerService = salesOfficerService;
	}
}