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
 * FileName    		:  CustomerCategoryListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.customercategory;

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

import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.service.applicationmaster.CustomerCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.customercategory.model.CustomerCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CustomerCategory/CustomerCategoryList.zul file.
 */
public class CustomerCategoryListCtrl extends GFCBaseListCtrl<CustomerCategory> {
	private static final long serialVersionUID = -7662342461801640367L;
	private static final Logger logger = Logger.getLogger(CustomerCategoryListCtrl.class);

	protected Window window_CustomerCategoryList;
	protected Borderlayout borderLayout_CustomerCategoryList;
	protected Paging pagingCustomerCategoryList;
	protected Listbox listBoxCustomerCategory;

	protected Textbox custCtgCode;
	protected Textbox custCtgDesc;
	protected Textbox custCtgType;
	protected Checkbox custCtgIsActive;

	protected Listbox sortOperator_custCtgCode;
	protected Listbox sortOperator_custCtgDesc;
	protected Listbox sortOperator_custCtgType;
	protected Listbox sortOperator_custCtgIsActive;

	protected Listheader listheader_CustCtgCode;
	protected Listheader listheader_CustCtgDesc;
	protected Listheader listheader_CustCtgType;
	protected Listheader listheader_CustCtgIsActive;

	protected Button button_CustomerCategoryList_NewCustomerCategory;
	protected Button button_CustomerCategoryList_CustomerCategorySearchDialog;

	private transient CustomerCategoryService customerCategoryService;

	/**
	 * default constructor.<br>
	 */
	public CustomerCategoryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerCategory";
		super.pageRightName = "CustomerCategoryList";
		super.tableName = "BMTCustCategories_AView";
		super.queueTableName = "BMTCustCategories_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("custCtgCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerCategoryList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerCategoryList, borderLayout_CustomerCategoryList, listBoxCustomerCategory,
				pagingCustomerCategoryList);
		setItemRender(new CustomerCategoryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerCategoryList_NewCustomerCategory,
				"button_CustomerCategoryList_NewCustomerCategory", true);
		registerButton(button_CustomerCategoryList_CustomerCategorySearchDialog);

		registerField("custCtgCode", listheader_CustCtgCode, SortOrder.ASC, custCtgCode, sortOperator_custCtgCode,
				Operators.STRING);
		registerField("custCtgDesc", listheader_CustCtgDesc, SortOrder.NONE, custCtgDesc, sortOperator_custCtgDesc,
				Operators.STRING);
		registerField("custCtgType", listheader_CustCtgType, SortOrder.NONE, custCtgType, sortOperator_custCtgType,
				Operators.STRING);
		registerField("custCtgIsActive", listheader_CustCtgIsActive, SortOrder.NONE, custCtgIsActive,
				sortOperator_custCtgIsActive, Operators.BOOLEAN);

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
	public void onClick$button_CustomerCategoryList_CustomerCategorySearchDialog(Event event) {
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
	public void onClick$button_CustomerCategoryList_NewCustomerCategory(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerCategory customerCategory = new CustomerCategory();
		customerCategory.setNewRecord(true);
		customerCategory.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerCategory);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerCategoryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerCategory.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CustomerCategory customerCategory = customerCategoryService.getCustomerCategoryById(id);

		if (customerCategory == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustCtgCode='" + customerCategory.getCustCtgCode() + "' AND version="
				+ customerCategory.getVersion() + " ";

		if (doCheckAuthority(customerCategory, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customerCategory.getWorkflowId() == 0) {
				customerCategory.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customerCategory);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerCategory
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerCategory customerCategory) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerCategory", customerCategory);
		arg.put("customerCategoryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CustomerCategory/CustomerCategoryDialog.zul",
					null, arg);
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

	public void setCustomerCategoryService(CustomerCategoryService customerCategoryService) {
		this.customerCategoryService = customerCategoryService;
	}
}