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
 * FileName    		:  CustomerAdditionalDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeradditionaldetail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.service.customermasters.CustomerAdditionalDetailService;
import com.pennant.webui.customermasters.customeradditionaldetail.model.CustomerAdditionalDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail
 * /CustomerAdditionalDetailList.zul file.
 */
public class CustomerAdditionalDetailListCtrl extends GFCBaseListCtrl<CustomerAdditionalDetail> {
	private static final long serialVersionUID = -4292260671471272242L;
	private static final Logger logger = Logger.getLogger(CustomerAdditionalDetailListCtrl.class);

	protected Window window_CustomerAdditionalDetailList;
	protected Borderlayout borderLayout_CustomerAdditionalDetailList;
	protected Paging pagingCustomerAdditionalDetailList;
	protected Listbox listBoxCustomerAdditionalDetail;

	protected Listheader listheader_CustAdditionalCIF;
	protected Listheader listheader_CustAcademicLevel;
	protected Listheader listheader_AcademicDecipline;
	protected Listheader listheader_CustRefCustID;
	protected Listheader listheader_CustRefStaffID;

	protected Panel customerAdditionalDetailSeekPanel;
	protected Panel customerAdditionalDetailListPanel;

	protected Textbox custCIF;
	protected Textbox custAcademicLevel;
	protected Textbox academicDecipline;
	protected Longbox custRefCustID;
	protected Textbox custRefStaffID;
	
	protected Listbox sortOperator_custRefStaffID;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custAcademicLevel;
	protected Listbox sortOperator_academicDecipline;
	protected Listbox sortOperator_custRefCustID;

	protected Label label_CustomerAdditionalDetailSearch_RecordStatus;
	protected Label label_CustomerAdditionalDetailSearch_RecordType;
	protected Label label_CustomerAdditionalDetailSearchResult;

	protected Button button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail;
	protected Button button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog;

	private transient CustomerAdditionalDetailService customerAdditionalDetailService;

	/**
	 * default constructor.<br>
	 */
	public CustomerAdditionalDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerAdditionalDetail";
		super.pageRightName = "CustomerAdditionalDetailList";
		super.tableName = "CustAdditionalDetails_AView";
		super.queueTableName = "CustAdditionalDetails_View";
		super.enquiryTableName = "CustAdditionalDetails_TView";
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerAdditionalDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerAdditionalDetailList, borderLayout_CustomerAdditionalDetailList,
				listBoxCustomerAdditionalDetail, pagingCustomerAdditionalDetailList);
		setItemRender(new CustomerAdditionalDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail, null, true);
		registerButton(button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog);

		registerField("lovDescCustCIF", listheader_CustRefCustID, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("custAcademicLevel", listheader_CustAcademicLevel, SortOrder.NONE, custAcademicLevel,
				sortOperator_custAcademicLevel, Operators.STRING);
		registerField("academicDecipline", listheader_AcademicDecipline, SortOrder.NONE, academicDecipline,
				sortOperator_academicDecipline, Operators.STRING);
		registerField("lovDescAcademicDeciplineName");
		registerField("custRefCustID", listheader_CustRefCustID, SortOrder.NONE, custRefCustID,
				sortOperator_custRefCustID, Operators.STRING);
		registerField("custRefStaffID", listheader_CustRefStaffID, SortOrder.NONE, custRefStaffID,
				sortOperator_custRefStaffID, Operators.STRING);
		registerField("lovDescCustAcademicLevelName");

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
	public void onClick$button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog(Event event)
			throws Exception {
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
	public void onClick$button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail(Event event) {
		logger.debug("Entering");
		// create a new CustomerAdditionalDetail object, We GET it from the back
		// end.
		final CustomerAdditionalDetail customerAdditionalDetail = new CustomerAdditionalDetail();
		customerAdditionalDetail.setNewRecord(true);
		customerAdditionalDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerAdditionalDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerAdditionalDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected CustomerAdditionalDetail object
		final Listitem item = this.listBoxCustomerAdditionalDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			CustomerAdditionalDetail customerAdditionalDetail = customerAdditionalDetailService
					.getCustomerAdditionalDetailById(id);

			if (customerAdditionalDetail == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND custId='" + customerAdditionalDetail.getCustID() + "' AND version="
					+ customerAdditionalDetail.getVersion() + " ";

			if (doCheckAuthority(customerAdditionalDetail, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerAdditionalDetail.getWorkflowId() == 0) {
					customerAdditionalDetail.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerAdditionalDetail);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerAdditionalDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerAdditionalDetail customerAdditionalDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerAdditionalDetail", customerAdditionalDetail);
		arg.put("customerAdditionalDetailListCtrl", this);
		arg.put("newRecord", customerAdditionalDetail.isNew());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail/CustomerAdditionalDetailDialog.zul", null,
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

	public void setCustomerAdditionalDetailService(CustomerAdditionalDetailService customerAdditionalDetailService) {
		this.customerAdditionalDetailService = customerAdditionalDetailService;
	}
}