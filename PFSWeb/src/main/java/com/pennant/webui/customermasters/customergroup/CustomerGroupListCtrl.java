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
 * FileName    		:  CustomerGroupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customergroup;

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

import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.webui.customermasters.customergroup.model.CustomerGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupList.zul file.
 */
public class CustomerGroupListCtrl extends GFCBaseListCtrl<CustomerGroup> {
	private static final long serialVersionUID = 8090581617957622077L;
	private static final Logger logger = Logger.getLogger(CustomerGroupListCtrl.class);

	protected Window window_CustomerGroupList;
	protected Borderlayout borderLayout_CustomerGroupList;
	protected Paging pagingCustomerGroupList;
	protected Listbox listBoxCustomerGroup;

	protected Listheader listheader_CustGrpCode;
	protected Listheader listheader_CustGrpDesc;
	protected Listheader listheader_CustGrpRO1;
	protected Listheader listheader_CustGrpIsActive;

	protected Textbox custCIF;
	protected Textbox custGrpCode;
	protected Textbox custGrpDesc;
	protected Textbox custGrpRO1;
	protected Checkbox custGrpIsActive;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custGrpCode;
	protected Listbox sortOperator_custGrpDesc;
	protected Listbox sortOperator_custGrpRO1;
	protected Listbox sortOperator_custGrpIsActive;

	protected Button button_CustomerGroupList_NewCustomerGroup;
	protected Button button_CustomerGroupList_CustomerGroupSearchDialog;

	private transient CustomerGroupService customerGroupService;

	/**
	 * default constructor.<br>
	 */
	public CustomerGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerGroup";
		super.pageRightName = "CustomerGroupList";
		super.tableName = "CustomerGroups_View";
		super.queueTableName = "CustomerGroups_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerGroupList(Event event) {
		setPageComponents(window_CustomerGroupList, borderLayout_CustomerGroupList, listBoxCustomerGroup,
				pagingCustomerGroupList);
		setItemRender(new CustomerGroupListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerGroupList_NewCustomerGroup, "button_CustomerGroupList_NewCustomerGroup", true);
		registerButton(button_CustomerGroupList_CustomerGroupSearchDialog);

		registerField("custGrpID");
		registerField("custGrpCode", listheader_CustGrpCode, SortOrder.ASC, custGrpCode, sortOperator_custGrpCode,
				Operators.STRING);
		registerField("custGrpDesc", listheader_CustGrpDesc, SortOrder.NONE, custGrpDesc, sortOperator_custGrpDesc,
				Operators.STRING);
		registerField("custGrpRO1", listheader_CustGrpRO1, SortOrder.NONE, custGrpRO1, sortOperator_custGrpRO1,
				Operators.STRING);
		registerField("custGrpIsActive", listheader_CustGrpIsActive, SortOrder.NONE, custGrpIsActive,
				sortOperator_custGrpIsActive, Operators.BOOLEAN);

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
	public void onClick$button_CustomerGroupList_CustomerGroupSearchDialog(Event event) {
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
	public void onClick$button_CustomerGroupList_NewCustomerGroup(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerGroup aCustomerGroup = new CustomerGroup();
		aCustomerGroup.setNewRecord(true);
		aCustomerGroup.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCustomerGroup);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerGroup.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		CustomerGroup customerGroup = customerGroupService.getCustomerGroupById(id);

		if (customerGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND custGrpID='" + customerGroup.getCustGrpID() + "' AND version="
				+ customerGroup.getVersion() + " ";

		if (doCheckAuthority(customerGroup, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customerGroup.getWorkflowId() == 0) {
				customerGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customerGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerGroup
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerGroup customerGroup) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerGroup", customerGroup);
		arg.put("customerGroupListCtrl", this);
		arg.put("newRecord", customerGroup.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupDialog.zul", null,
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

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}
}