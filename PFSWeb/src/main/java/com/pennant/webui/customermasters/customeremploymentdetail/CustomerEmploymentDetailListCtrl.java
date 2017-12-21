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
 * FileName    		:  CustomerEmploymentDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeremploymentdetail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail
 * /CustomerEmploymentDetailList.zul file.
 */
public class CustomerEmploymentDetailListCtrl extends GFCBaseListCtrl<CustomerEmploymentDetail> {
	private static final long serialVersionUID = 5652445153118844873L;
	private static final Logger logger = Logger.getLogger(CustomerEmploymentDetailListCtrl.class);

	protected Window window_CustomerEmploymentDetailList;
	protected Borderlayout borderLayout_CustomerEmploymentDetailList;
	protected Paging pagingCustomerEmploymentDetailList;
	protected Listbox listBoxCustomerEmploymentDetail;

	protected Listheader listheader_CustEmpCIF;
	protected Listheader listheader_CustEmpName;
	protected Listheader listheader_CustEmpDesg;
	protected Listheader listheader_CustEmpDept;
	protected Listheader listheader_CustEmpID;

	protected Textbox custCIF;
	protected Textbox custEmpName;
	protected Datebox custEmpFrom;
	protected Textbox custEmpDesg;
	protected Textbox custEmpDept;
	protected Textbox custEmpID;

	protected Listbox sortOperator_custEmpID;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custEmpName;
	protected Listbox sortOperator_custEmpFrom;
	protected Listbox sortOperator_custEmpDesg;
	protected Listbox sortOperator_custEmpDept;

	protected Button button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail;
	protected Button button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog;

	private transient CustomerEmploymentDetailService customerEmploymentDetailService;

	/**
	 * default constructor.<br>
	 */
	public CustomerEmploymentDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerEmploymentDetail";
		super.pageRightName = "CustomerEmploymentDetailList";
		super.tableName = "CustomerEmpDetails_AView";
		super.queueTableName = "CustomerEmpDetails_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerEmploymentDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerEmploymentDetailList, borderLayout_CustomerEmploymentDetailList,
				listBoxCustomerEmploymentDetail, pagingCustomerEmploymentDetailList);
		setItemRender(new CustomerEmploymentDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail,
				"button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail", true);
		registerButton(button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog);
		registerField("custid");
		registerField("custempName");
		registerField("lovDescCustCIF", listheader_CustEmpCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("lovDesccustEmpName", listheader_CustEmpName, SortOrder.NONE, custEmpName,
				sortOperator_custEmpName, Operators.STRING);
		registerField("lovDescCustEmpDesgName", listheader_CustEmpDesg, SortOrder.NONE, custEmpDesg,
				sortOperator_custEmpDesg, Operators.STRING);
		registerField("lovDescCustEmpDeptName", listheader_CustEmpDept, SortOrder.NONE, custEmpDept,
				sortOperator_custEmpDept, Operators.STRING);
		registerField("lovDescCustEmpTypeName", listheader_CustEmpID, SortOrder.NONE, custEmpID,
				sortOperator_custEmpID, Operators.STRING);
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
	public void onClick$button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog(Event event)
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
	public void onClick$button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail(Event event) {
		logger.debug("Entering" + event.toString());
		// create a new CustomerEmploymentDetail object, We GET it from the back
		// end.
		final CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setNewRecord(true);
		customerEmploymentDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerEmploymentDetail);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerEmploymentDetailItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerEmploymentDetail object
		final Listitem item = this.listBoxCustomerEmploymentDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
		
			long custEmpId=(long) item.getAttribute("custEmpId");
			final CustomerEmploymentDetail customerEmploymentDetail = customerEmploymentDetailService
					.getCustomerEmploymentDetailByCustEmpId(custEmpId);

			if (customerEmploymentDetail == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND custId='" + customerEmploymentDetail.getCustID() + "' AND version="
					+ customerEmploymentDetail.getVersion() + " ";

			if (doCheckAuthority(customerEmploymentDetail, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerEmploymentDetail.getWorkflowId() == 0) {
					customerEmploymentDetail.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerEmploymentDetail);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerEmploymentDetail customerEmploymentDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerEmploymentDetail", customerEmploymentDetail);
		arg.put("customerEmploymentDetailListCtrl", this);
		arg.put("newRecord", customerEmploymentDetail.isNew());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul", null,
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

	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}
}