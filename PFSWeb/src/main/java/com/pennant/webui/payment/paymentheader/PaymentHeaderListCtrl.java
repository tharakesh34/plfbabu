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
 * * FileName : PaymentHeaderListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.payment.paymentheader;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.webui.payment.paymentheader.model.PaymentHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.payment/PaymentHeader/PaymentHeaderList.zul file.
 * 
 */
public class PaymentHeaderListCtrl extends GFCBaseListCtrl<PaymentHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_PaymentHeaderList;
	protected Borderlayout borderLayout_PaymentHeaderList;
	protected Paging pagingPaymentHeaderList;
	protected Listbox listBoxPaymentHeader;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_PaymentType;
	protected Listheader listheader_ApprovedOn;

	// checkRights
	protected Button button_PaymentHeaderList_NewPaymentHeader;
	protected Button button_PaymentHeaderList_PaymentHeaderSearch;

	// Search Fields
	protected Combobox paymentType; // autowired
	protected ExtendedCombobox finReference; // autowired
	protected Datebox approvedOn; // autowired

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_PaymentType;
	protected Listbox sortOperator_ApprovedOn;
	private transient PaymentHeaderService paymentHeaderService;

	/**
	 * default constructor.<br>
	 */
	public PaymentHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PaymentHeader";
		super.pageRightName = "PaymentHeaderList";
		super.tableName = "PaymentHeader_AView";
		super.queueTableName = "PaymentHeader_View";
		super.enquiryTableName = "PaymentHeader_AView";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilterNotEqual("recordStatus", PennantConstants.RCD_STATUS_APPROVED);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PaymentHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_PaymentHeaderList, borderLayout_PaymentHeaderList, listBoxPaymentHeader,
				pagingPaymentHeaderList);
		setItemRender(new PaymentHeaderListModelItemRenderer());

		// Register buttons and fields.
		doSetFieldProperties();
		registerButton(button_PaymentHeaderList_PaymentHeaderSearch);
		registerButton(button_PaymentHeaderList_NewPaymentHeader, "button_PaymentHeaderList_NewPaymentHeader", true);

		if (enqiryModule) {
			registerButton(button_PaymentHeaderList_NewPaymentHeader, "button_PaymentHeaderList_NewPaymentHeader",
					false);
		}
		registerField("paymentId");
		registerField("createdOn");
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("paymentInstrType", listheader_PaymentType, SortOrder.NONE, paymentType, sortOperator_PaymentType,
				Operators.STRING);
		registerField("approvedOn", listheader_ApprovedOn, SortOrder.NONE, approvedOn, sortOperator_ApprovedOn,
				Operators.DATE_RANGE_BETWEEN);

		// Render the page and display the data.
		doRenderPage();

		search();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// finReference
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setModuleName("FinanceMainMaintenance");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		// paymentType
		fillComboBox(this.paymentType, "", PennantStaticListUtil.getPaymentTypesWithIST(), "");

		// SetFormats
		this.approvedOn.setFormat(PennantConstants.dateFormat);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PaymentHeaderList_PaymentHeaderSearch(Event event) {
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
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PaymentHeaderList_NewPaymentHeader(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		PaymentHeader paymentHeader = new PaymentHeader();
		paymentHeader.setNewRecord(true);
		paymentHeader.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("paymentHeader", paymentHeader);
		arg.put("paymentheaderListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Payment/SelectPaymentHeaderDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onPaymentHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPaymentHeader.getSelectedItem();
		final long paymentId = (long) selectedItem.getAttribute("paymentId");
		PaymentHeader paymentheader = paymentHeaderService.getPaymentHeader(paymentId);

		if (paymentheader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  PaymentId =? ");

		if (doCheckAuthority(paymentheader, whereCond.toString(), new Object[] { paymentheader.getPaymentId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && paymentheader.getWorkflowId() == 0) {
				paymentheader.setWorkflowId(getWorkFlowId());
			}

			logUserAccess("menu_Item_PaymentInstructions", paymentheader.getFinReference());

			doShowDialogPage(paymentheader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param paymentheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PaymentHeader paymentheader) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = paymentHeaderService.getFinanceDetails(paymentheader.getFinID());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("paymentHeader", paymentheader);
		arg.put("paymentHeaderListCtrl", this);
		arg.put("financeMain", financeMain);
		arg.put("enqiryModule", enqiryModule);
		try {
			Executions.createComponents("/WEB-INF/pages/Payment/PaymentHeaderDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

}