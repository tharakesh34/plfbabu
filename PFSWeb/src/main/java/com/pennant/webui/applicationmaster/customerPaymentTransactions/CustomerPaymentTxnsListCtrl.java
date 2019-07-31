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
 * FileName    		:  CustomerPaymentTxnsListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.customerPaymentTransactions;

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

import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.model.CustomerPaymentTxnsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CustomerPaymentTxnsListCtrl extends GFCBaseListCtrl<FinAdvancePayments> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(CustomerPaymentTxnsListCtrl.class);

	protected Window window_CustomerPaymentTxnsList;
	protected Borderlayout borderLayout_CustomerPaymentTxnsList;
	protected Paging pagingCustomerPaymentTxnsList;
	protected Listbox listBoxCustomerPaymentTxns;

	// List headers
	protected Listheader listheader_TransactionModule;
	protected Listheader listheader_BatchId;
	protected Listheader listheader_PaymentId;
	protected Listheader listheader_TransactionStatus;

	// checkRights
	protected Button button_CustomerPaymentTxnsList_NewCustomerPaymentTxns;
	protected Button button_CustomerPaymentTxnsList_CustomerPaymentTxnsSearch;

	// Search Fields
	protected Textbox transactionModule;
	protected Textbox batchId;
	protected Textbox paymentId;
	protected Textbox transactionStatus;

	protected Listbox sortOperator_TransactionModule;
	protected Listbox sortOperator_BatchId;
	protected Listbox sortOperator_PaymentId;
	protected Listbox sortOperator_TransactionStatus;

	private transient FinanceMainService financeMainService;
	private transient FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private transient CustomerDAO customerDAO;


	/**
	 * default constructor.<br>
	 */
	public CustomerPaymentTxnsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinAdvancePayments";
		super.pageRightName = "FinAdvancePaymentsList";
		super.tableName = "FinAdvancePayments_AView";
		super.queueTableName = "FinAdvancePayments_View";
		super.enquiryTableName = "FinAdvancePayments_View";
	}

	public void onCreate$window_CustomerPaymentTxnsList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CustomerPaymentTxnsList, borderLayout_CustomerPaymentTxnsList,
				listBoxCustomerPaymentTxns, pagingCustomerPaymentTxnsList);
		setItemRender(new CustomerPaymentTxnsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerPaymentTxnsList_CustomerPaymentTxnsSearch);
		registerButton(button_CustomerPaymentTxnsList_NewCustomerPaymentTxns, RIGHT_NOT_ACCESSIBLE, true);

		registerField("transactionRef", listheader_TransactionModule, SortOrder.NONE, transactionModule,
				sortOperator_TransactionModule, Operators.STRING);
		registerField("finreference", listheader_BatchId, SortOrder.NONE, batchId, sortOperator_BatchId,
				Operators.STRING);
		registerField("paymentId", listheader_PaymentId, SortOrder.NONE, paymentId, sortOperator_PaymentId,
				Operators.STRING);
		registerField("status", listheader_TransactionStatus, SortOrder.NONE, transactionStatus,
				sortOperator_TransactionStatus, Operators.STRING);

		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CustomerPaymentTxnsList_CustomerPaymentTxnsSearch(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onCustomerPaymentTxnsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		Listitem selectedItem = this.listBoxCustomerPaymentTxns.getSelectedItem();
		FinAdvancePayments finAdvancePayments = (FinAdvancePayments) selectedItem.getAttribute("finAdvancePayments");

		if (finAdvancePayments == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		FinanceMain financeMain = this.financeMainService.getFinanceMainByFinRef(finAdvancePayments.getFinReference());
		Customer customer = customerDAO.getCustomerByID(financeMain.getCustID());
		financeMain.setLovDescCustCIF(customer.getCustCIF());
		finAdvancePayments = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments,
				"_View");

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  AND  Id = ");
		whereCond.append(finAdvancePayments.getId());
		whereCond.append(" AND  version=");
		whereCond.append(finAdvancePayments.getVersion());

		if (doCheckAuthority(finAdvancePayments, whereCond.toString())) {
			if (isWorkFlowEnabled() && finAdvancePayments.getWorkflowId() == 0) {
				finAdvancePayments.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finAdvancePayments, financeMain);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(FinAdvancePayments finAdvancePayments, FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("finAdvancePayments", finAdvancePayments);
		arg.put("financeMain", financeMain);
		arg.put("customerPaymentTxnsListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerPaymentTransactions/CustomerPaymentTxnsDialog.zul", null,
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

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

}