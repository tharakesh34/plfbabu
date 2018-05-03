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
 * FileName    		:  CustomerNotesTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.customernotestype;

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

import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.service.applicationmaster.CustomerNotesTypeService;
import com.pennant.webui.applicationmaster.customernotestype.model.CustomerNotesTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CustomerNotesType/CustomerNotesTypeList.zul
 * file.
 */
public class CustomerNotesTypeListCtrl extends GFCBaseListCtrl<CustomerNotesType> {
	private static final long serialVersionUID = -9149300436300750011L;
	private static final Logger logger = Logger.getLogger(CustomerNotesTypeListCtrl.class);

	protected Window window_CustomerNotesTypeList;
	protected Borderlayout borderLayout_CustomerNotesTypeList;
	protected Paging pagingCustomerNotesTypeList;
	protected Listbox listBoxCustomerNotesType;

	protected Listheader listheader_CustNotesTypeCode;
	protected Listheader listheader_CustNotesTypeDesc;
	protected Listheader listheader_CustNotesTypeIsPerminent;
	protected Listheader listheader_CustNotesTypeArchiveFrq;

	protected Button button_CustomerNotesTypeList_NewCustomerNotesType;
	protected Button button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog;

	protected Textbox custNotesTypeCode;
	protected Textbox custNotesTypeDesc;
	protected Checkbox custNotesTypeIsPerminent;
	protected Checkbox custNotesTypeIsActive;
	protected Textbox custNotesTypeArchiveFrq;

	protected Listbox sortOperator_custNotesTypeCode;
	protected Listbox sortOperator_custNotesTypeDesc;
	protected Listbox sortOperator_custNotesTypeIsPerminent;
	protected Listbox sortOperator_custNotesTypeIsActive;
	protected Listbox sortOperator_custNotesTypeArchiveFrq;

	private transient CustomerNotesTypeService customerNotesTypeService;

	/**
	 * default constructor.<br>
	 */
	public CustomerNotesTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerNotesType";
		super.pageRightName = "CustomerNotesTypeList";
		super.tableName = "BMTCustNotesTypes_AView";
		super.queueTableName = "BMTCustNotesTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerNotesTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerNotesTypeList, borderLayout_CustomerNotesTypeList, listBoxCustomerNotesType,
				pagingCustomerNotesTypeList);
		setItemRender(new CustomerNotesTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerNotesTypeList_NewCustomerNotesType,
				"button_CustomerNotesTypeList_NewCustomerNotesType", true);
		registerButton(button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog);

		registerField("custNotesTypeCode", listheader_CustNotesTypeCode, SortOrder.ASC, custNotesTypeCode,
				sortOperator_custNotesTypeCode, Operators.STRING);
		registerField("custNotesTypeDesc", listheader_CustNotesTypeDesc, SortOrder.NONE, custNotesTypeDesc,
				sortOperator_custNotesTypeDesc, Operators.STRING);
		registerField("custNotesTypeIsPerminent", listheader_CustNotesTypeIsPerminent, SortOrder.NONE,
				custNotesTypeIsPerminent, sortOperator_custNotesTypeIsPerminent, Operators.BOOLEAN);
		registerField("custNotesTypeIsActive", custNotesTypeIsActive, SortOrder.NONE,
				sortOperator_custNotesTypeIsActive, Operators.BOOLEAN);
		registerField("custNotesTypeArchiveFrq", listheader_CustNotesTypeArchiveFrq, SortOrder.NONE,
				custNotesTypeArchiveFrq, sortOperator_custNotesTypeArchiveFrq, Operators.STRING);

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
	public void onClick$button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog(Event event) {
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
	public void onClick$button_CustomerNotesTypeList_NewCustomerNotesType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerNotesType customerNotesType = new CustomerNotesType();
		customerNotesType.setNewRecord(true);
		customerNotesType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerNotesType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerNotesTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerNotesType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CustomerNotesType customerNotesType = customerNotesTypeService.getCustomerNotesTypeById(id);

		if (customerNotesType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustNotesTypeCode='" + customerNotesType.getCustNotesTypeCode() + "' AND version="
				+ customerNotesType.getVersion() + " ";

		if (doCheckAuthority(customerNotesType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customerNotesType.getWorkflowId() == 0) {
				customerNotesType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customerNotesType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerNotesType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerNotesType customerNotesType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerNotesType", customerNotesType);
		arg.put("customerNotesTypeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerNotesType/CustomerNotesTypeDialog.zul", null, arg);
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

	public void setCustomerNotesTypeService(CustomerNotesTypeService customerNotesTypeService) {
		this.customerNotesTypeService = customerNotesTypeService;
	}
}