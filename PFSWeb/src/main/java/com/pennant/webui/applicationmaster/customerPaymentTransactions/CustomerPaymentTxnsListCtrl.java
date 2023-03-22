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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerPaymentTxnsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-08-2019 * *
 * Modified Date : 25-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.customerPaymentTransactions;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.model.CustomerPaymentTxnsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CustomerPaymentTxnsListCtrl extends GFCBaseListCtrl<PaymentTransaction> {
	private static final long serialVersionUID = 1L;

	protected Window window_CustomerPaymentTxnsList;
	protected Borderlayout borderLayout_CustomerPaymentTxnsList;
	protected Paging pagingCustomerPaymentTxnsList;
	protected Listbox listBoxCustomerPaymentTxns;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_TransactionModule;
	protected Listheader listheader_PaymentId;
	protected Listheader listheader_TransactionStatus;

	// checkRights
	protected Button button_CustomerPaymentTxnsList_CustomerPaymentTxnsSearch;

	// Search Fields
	protected Textbox finReference;
	protected Textbox transactionModule;
	protected Longbox paymentId;
	// protected Textbox transactionStatus;

	protected Listbox sortOperator_Finreference;
	protected Listbox sortOperator_TransactionModule;
	protected Listbox sortOperator_PaymentId;
	// protected Listbox sortOperator_TransactionStatus;

	private transient FinanceMainService financeMainService;
	private transient FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private transient CustomerDAO customerDAO;
	private transient PaymentHeaderService paymentHeaderService;

	private boolean enqiryModule = false;
	String module = "";

	/**
	 * default constructor.<br>
	 */
	public CustomerPaymentTxnsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PaymentTransaction";
		super.pageRightName = "PaymentTransactionList";
		super.tableName = "PaymentTransaction_View";
		super.queueTableName = "PaymentTransaction_View";
		super.enquiryTableName = "PaymentTransaction_View";

		module = getArgument("module");
		String enqiryModule = getArgument("enqiryModule");
		if ("E".equals(enqiryModule)) {
			this.enqiryModule = true;
		}
		if ("PYMT".equals(module)) {
			super.moduleCode = "PaymentTransaction";
			super.pageRightName = "PaymentTransactionList";
			super.tableName = "PaymentTransaction_PView";
			super.queueTableName = "PaymentTransaction_PView";
			super.enquiryTableName = "PaymentTransaction_PView";
		} else {
			module = "DISB";
		}
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			searchObject.addFilterEqual("tranStatus", "T");
		}
		searchObject.addFilterEqual("tranmodule", module);
	}

	public void onCreate$window_CustomerPaymentTxnsList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CustomerPaymentTxnsList, borderLayout_CustomerPaymentTxnsList,
				listBoxCustomerPaymentTxns, pagingCustomerPaymentTxnsList);
		setItemRender(new CustomerPaymentTxnsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerPaymentTxnsList_CustomerPaymentTxnsSearch);

		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_Finreference,
				Operators.STRING);

		registerField("tranModule", listheader_TransactionModule, SortOrder.NONE, transactionModule,
				sortOperator_TransactionModule, Operators.STRING);

		registerField("paymentId", listheader_PaymentId, SortOrder.NONE, paymentId, sortOperator_PaymentId,
				Operators.NUMERIC);

		registerField("transactionId");
		registerField("tranStatus");
		registerField("tranReference");
		registerField("tranBatch");
		registerField("statusCode");
		registerField("statusDesc");

		/*
		 * registerField("tranStatus", listheader_TransactionStatus, SortOrder.NONE, transactionStatus,
		 * sortOperator_TransactionStatus, Operators.STRING);
		 */
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CustomerPaymentTxnsList_CustomerPaymentTxnsSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onCustomerPaymentTxnsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		Listitem selectedItem = this.listBoxCustomerPaymentTxns.getSelectedItem();
		PaymentTransaction paymentTransaction = (PaymentTransaction) selectedItem.getAttribute("paymentTransaction");

		if (paymentTransaction == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if ("PYMT".equals(module)) {
			PaymentHeader paymentheader = paymentHeaderService.getPaymentHeader(paymentTransaction.getPaymentId());

			StringBuilder whereCond = new StringBuilder();
			whereCond.append("  AND  PaymentId = ");
			whereCond.append(paymentTransaction.getPaymentId());
			whereCond.append(" AND  version=");
			whereCond.append(paymentheader.getVersion());

			doShowDialogPage(paymentheader, paymentTransaction);
		} else {
			FinanceMain financeMain = this.financeMainService
					.getFinanceMainByFinRef(paymentTransaction.getFinReference());
			Customer customer = customerDAO.getCustomerByID(financeMain.getCustID());
			financeMain.setLovDescCustCIF(customer.getCustCIF());
			FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
			finAdvancePayments.setPaymentId(paymentTransaction.getPaymentId());
			finAdvancePayments = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "_View");
			finAdvancePayments = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "_View");

			paymentTransaction.setFinAdvancePayments(finAdvancePayments);

			doShowDialogPage(paymentTransaction, financeMain);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(PaymentHeader paymentheader, PaymentTransaction paymentTransaction) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = paymentHeaderService.getFinanceDetails(paymentheader.getFinID());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("paymentHeader", paymentheader);
		arg.put("paymentTransaction", paymentTransaction);
		arg.put("financeMain", financeMain);
		arg.put("customerPaymentTxnsListCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("isFromCustomerPaymentMenu", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Payment/PaymentHeaderDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(PaymentTransaction paymentTransaction, FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("paymentTransaction", paymentTransaction);
		arg.put("financeMain", financeMain);
		arg.put("customerPaymentTxnsListCtrl", this);
		arg.put("enqiryModule", enqiryModule);

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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
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

	public PaymentHeaderService getPaymentHeaderService() {
		return paymentHeaderService;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

}