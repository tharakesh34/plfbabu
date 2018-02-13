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
 * FileName    		:  FinSuspHoldListCtrl.java                                                   * 	  
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
package com.pennant.webui.financemanagement.finsusphold;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.service.financemanagement.FinSuspHoldService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.financemanagement.finsusphold.model.FinSuspHoldListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FinSuspHold/FinSuspHoldList.zul file.
 */
public class FinSuspHoldListCtrl extends GFCBaseListCtrl<FinSuspHold> {
	private static final long serialVersionUID = -3571720185247491921L;
	private static final Logger logger = Logger.getLogger(FinSuspHoldListCtrl.class);

	protected Window window_FinSuspHoldList;
	protected Borderlayout borderLayout_FinSuspHoldList;
	protected Paging pagingFinSuspHoldList;
	protected Listbox listBoxFinSuspHold;

	protected Textbox product;
	protected Textbox finType;
	protected Textbox finReference;
	protected Textbox custCIF;
	protected Longbox custID;
	protected Textbox custShrtName;
	protected Checkbox active;

	protected Listbox sortOperator_active;
	protected Listbox sortOperator_product;
	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finReference;
	protected Listbox sortOperator_custID;
	protected Listbox sortOperator_custShrtName;

	protected Listheader listheader_Product;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustShrtName;
	protected Listheader listheader_Active;

	protected Button button_FinSuspHoldList_NewFinSuspHold;
	protected Button button_FinSuspHoldList_FinSuspHoldSearchDialog;

	private transient FinSuspHoldService finSuspHoldService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	/**
	 * default constructor.<br>
	 */
	public FinSuspHoldListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinSuspHold";
		super.pageRightName = "FinSuspHoldList";
		super.tableName = "FinSuspHold_AView";
		super.queueTableName = "FinSuspHold_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinSuspHoldList(Event event) {
		// Set the page level components.
		setPageComponents(window_FinSuspHoldList, borderLayout_FinSuspHoldList, listBoxFinSuspHold,
				pagingFinSuspHoldList);
		setItemRender(new FinSuspHoldListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinSuspHoldList_NewFinSuspHold, "button_FinSuspHoldList_NewFinSuspHold", true);
		registerButton(button_FinSuspHoldList_FinSuspHoldSearchDialog);

		registerField("SuspHoldID", SortOrder.ASC);
		registerField("Product", listheader_Product, SortOrder.NONE, product, sortOperator_product, Operators.STRING);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType, Operators.STRING);
		registerField("CustCIF", listheader_CustCIF, SortOrder.NONE, custID, sortOperator_custID, Operators.STRING);
		registerField("CustShrtName", listheader_CustShrtName, SortOrder.NONE, custShrtName, sortOperator_custShrtName,
				Operators.STRING);
		registerField("FinReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("Active", listheader_Active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
		doSetFieldProperties();

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FinSuspHoldList_FinSuspHoldSearchDialog(Event event) {
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
	public void onClick$button_FinSuspHoldList_NewFinSuspHold(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinSuspHold aFinSuspHold = new FinSuspHold();
		aFinSuspHold.setNewRecord(true);
		aFinSuspHold.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aFinSuspHold);

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * FinSuspHoldListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinSuspHoldItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinSuspHold.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		FinSuspHold finSuspHold = finSuspHoldService.getFinSuspHoldById(id);

		if (finSuspHold == null) {
			String[] errorParm = { getValidationMsg(finSuspHold) };

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",
					errorParm, null), getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SuspHoldID='" + Long.toString(finSuspHold.getSuspHoldID()) + "' AND version="
				+ finSuspHold.getVersion() + " ";

		if (doCheckAuthority(finSuspHold, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && finSuspHold.getWorkflowId() == 0) {
				finSuspHold.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finSuspHold);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFinSuspHold
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinSuspHold aFinSuspHold) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("finSuspHold", aFinSuspHold);
		arg.put("finSuspHoldListCtrl", this);
		arg.put("moduleCode", super.moduleCode);

		try {
			Executions
					.createComponents("/WEB-INF/pages/FinanceManagement/FinSuspHold/FinSuspHoldDialog.zul", null, arg);
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

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custID.setMaxlength(26);
		this.custCIF.setMaxlength(10);
		this.finReference.setMaxlength(20);
		this.product.setMaxlength(8);
		this.finType.setMaxlength(8);
		this.recordStatus.setMaxlength(50);
		logger.debug("Leaving");
	}

	private String getValidationMsg(FinSuspHold finSuspHold) {
		logger.debug("Entering");
		String errMsg = "";
		if (StringUtils.isNotEmpty(finSuspHold.getProduct())) {
			errMsg = Labels.getLabel("label_FinSuspHold_Product") + " : " + finSuspHold.getProduct();
		}
		if (StringUtils.isNotEmpty(finSuspHold.getFinType())) {
			if (StringUtils.isEmpty(errMsg)) {
				errMsg = Labels.getLabel("label_FinSuspHold_FinType") + " : " + finSuspHold.getFinType();
			} else {
				errMsg = errMsg + "," + Labels.getLabel("label_FinSuspHold_FinType") + " : " + finSuspHold.getFinType();
			}
		}
		if (StringUtils.isNotEmpty(finSuspHold.getFinReference())) {
			if (StringUtils.isEmpty(errMsg)) {
				errMsg = Labels.getLabel("label_FinSuspHold_FinReference") + " : " + finSuspHold.getFinReference();
			} else {
				errMsg = errMsg + "," + Labels.getLabel("label_FinSuspHold_FinReference") + " : "
						+ finSuspHold.getFinReference();
			}
		}
		if (StringUtils.isNotEmpty(finSuspHold.getCustCIF())) {
			if (StringUtils.isEmpty(errMsg)) {
				errMsg = Labels.getLabel("label_FinSuspHold_CustCIF") + " : " + finSuspHold.getCustCIF();
			} else {
				errMsg = errMsg + "," + Labels.getLabel("label_FinSuspHold_CustCIF") + " : " + finSuspHold.getCustCIF();
			}
		}
		logger.debug("Leaving");
		return errMsg;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinSuspHoldService(FinSuspHoldService finSuspHoldService) {
		this.finSuspHoldService = finSuspHoldService;
	}
}