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
 * FileName    		:  CustomerTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.customertype;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.customertype.model.CustomerTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/CustomerType/CustomerTypeList.zul file.
 */
public class CustomerTypeListCtrl extends GFCBaseListCtrl<CustomerType> {
	private static final long serialVersionUID = 5954194788863085861L;
	private static final Logger logger = Logger.getLogger(CustomerTypeListCtrl.class);

	protected Window window_CustomerTypeList;
	protected Borderlayout borderLayout_CustomerTypeList;
	protected Paging pagingCustomerTypeList;
	protected Listbox listBoxCustomerType;

	protected Listheader listheader_CustTypeCode;
	protected Listheader listheader_CustTypeDesc;
	protected Listheader listheader_CustTypeCtg;
	protected Listheader listheader_CustTypeIsActive;

	protected Textbox custTypeCode;
	protected Textbox custTypeDesc;
	protected Combobox custTypeCtg;
	protected Checkbox custTypeIsActive;

	protected Listbox sortOperator_custTypeCode;
	protected Listbox sortOperator_custTypeDesc;
	protected Listbox sortOperator_custTypeCtg;
	protected Listbox sortOperator_custTypeIsActive;
	protected Label label_CustomerTypeSearchResult;

	protected Button button_CustomerTypeList_NewCustomerType;
	protected Button button_CustomerTypeList_CustomerTypeSearchDialog;

	private transient CustomerTypeService customerTypeService;

	/**
	 * default constructor.<br>
	 */
	public CustomerTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerType";
		super.pageRightName = "CustomerTypeList";
		super.tableName = "RMTCustTypes_AView";
		super.queueTableName = "RMTCustTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerTypeList, borderLayout_CustomerTypeList, listBoxCustomerType,
				pagingCustomerTypeList);
		setItemRender(new CustomerTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerTypeList_NewCustomerType, "button_CustomerTypeList_NewCustomerType", true);
		registerButton(button_CustomerTypeList_CustomerTypeSearchDialog);

		fillComboBox(this.custTypeCtg, "", PennantAppUtil.getcustCtgCodeList(), "");

		registerField("custTypeCode", listheader_CustTypeCode, SortOrder.ASC, custTypeCode, sortOperator_custTypeCode,
				Operators.STRING);
		registerField("custTypeDesc", listheader_CustTypeDesc, SortOrder.NONE, custTypeDesc, sortOperator_custTypeDesc,
				Operators.STRING);
		registerField("custTypeCtg", listheader_CustTypeCtg, SortOrder.NONE, custTypeCtg, sortOperator_custTypeCtg,
				Operators.STRING);
		registerField("custTypeIsActive", listheader_CustTypeIsActive, SortOrder.NONE, custTypeIsActive,
				sortOperator_custTypeIsActive, Operators.BOOLEAN);

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
	public void onClick$button_CustomerTypeList_CustomerTypeSearchDialog(Event event) {
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
	public void onClick$button_CustomerTypeList_NewCustomerType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerType aCustomerType = new CustomerType();
		aCustomerType.setNewRecord(true);
		aCustomerType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCustomerType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CustomerType aCustomerType = customerTypeService.getCustomerTypeById(id);

		if (aCustomerType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustTypeCode='" + aCustomerType.getCustTypeCode() + "' AND version="
				+ aCustomerType.getVersion() + " ";

		if (doCheckAuthority(aCustomerType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aCustomerType.getWorkflowId() == 0) {
				aCustomerType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aCustomerType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerType aCustomerType) {
		logger.debug("Entering");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerType", aCustomerType);
		arg.put("customerTypeListCtrl", this);

		try {
			Executions
					.createComponents("/WEB-INF/pages/SolutionFactory/CustomerType/CustomerTypeDialog.zul", null, arg);
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

	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}
}