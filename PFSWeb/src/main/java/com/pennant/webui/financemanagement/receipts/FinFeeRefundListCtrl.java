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
package com.pennant.webui.financemanagement.receipts;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinFeeRefundService;
import com.pennant.webui.financemanagement.receipts.model.FinFeeRefundsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/FeeReceipt/FinFeeRefundsList.zul file.
 */
public class FinFeeRefundListCtrl extends GFCBaseListCtrl<FinFeeRefundHeader> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = LogManager.getLogger(FinFeeRefundListCtrl.class);

	protected Window window_FinFeeRefundsList;
	protected Borderlayout borderLayout_FinFeeRefundsList;
	protected Listbox listBoxFinFeeRefunds;
	protected Paging pagingFinFeeRefundsList;

	protected Listheader listheader_FinReference;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinBranch;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustName;

	protected Button btnNew;
	protected Button btnSearch;

	protected Textbox finReference;
	protected Textbox customer;
	protected Textbox finType;
	protected Textbox finBranch;

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_Customer;
	protected Listbox sortOperator_FinType;
	protected Listbox sortOperator_FinBranch;

	protected int oldVar_sortOperator_custCIF;
	protected int oldVar_sortOperator_finType;
	protected int oldVar_sortOperator_finBranch;

	private FinFeeRefundService finFeeRefundService;

	/**
	 * The default constructor.
	 */
	public FinFeeRefundListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinFeeRefundHeader";
		super.pageRightName = "FinFeeRefundList";
		super.tableName = "FinFeeRefundHeader_View";
		super.queueTableName = "FinFeeRefundHeader_TView";
		super.enquiryTableName = "FinFeeRefundHeader_View";
	}

	@Override
	protected void doAddFilters() {

		super.doAddFilters();
		if (enqiryModule) {
			searchObject.addTabelName(enquiryTableName);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinFeeRefundsList(Event event) {
		// Set the page level components.
		setPageComponents(window_FinFeeRefundsList, borderLayout_FinFeeRefundsList, listBoxFinFeeRefunds,
				pagingFinFeeRefundsList);
		setItemRender(new FinFeeRefundsListModelItemRenderer());
		registerButton(btnNew, "button_FeeReceiptList_NewFeeReceipt", true);
		registerButton(btnSearch);
		registerField("HeaderID");
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.NONE, customer, sortOperator_Customer,
				Operators.STRING);
		registerField("LovDescCustShrtName", listheader_CustName, SortOrder.NONE);
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		registerField("finBranch", listheader_FinBranch, SortOrder.NONE, finBranch, sortOperator_FinBranch,
				Operators.STRING);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		this.customer.setValue("");
		this.sortOperator_Customer.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_FinType.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_FinBranch.setSelectedIndex(0);
		this.listBoxFinFeeRefunds.getItems().clear();
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinFeeRefundHeader finFeeRefundHeader = new FinFeeRefundHeader();
		finFeeRefundHeader.setNewRecord(true);
		finFeeRefundHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(finFeeRefundHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onFinFeeRefundItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinFeeRefunds.getSelectedItem();

		// Get the selected entity.
		long headerID = (long) selectedItem.getAttribute("headerID");
		FinFeeRefundHeader header = finFeeRefundService.getFinFeeRefundHeaderById(headerID, "_View");

		if (header == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		header.setFinFeeRefundDetails(finFeeRefundService.getFinFeeRefundDetailsByHeaderId(headerID, "_View"));

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND HeaderID='" + header.getHeaderId() + "' AND version=" + header.getVersion() + " ";

		if (doCheckAuthority(header, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && header.getWorkflowId() == 0) {
				header.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(header);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param header The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinFeeRefundHeader finFeeRefundHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("finFeeRefundHeader", finFeeRefundHeader);
		arg.put("finFeeRefundsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/FinFeeRefundDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		if (this.oldVar_sortOperator_custCIF == Filter.OP_IN || this.oldVar_sortOperator_custCIF == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinFeeRefundsList, "Customer",
					this.customer.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.customer.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FinFeeRefundsList, "Customer");
			if (dataObject instanceof String) {
				this.customer.setValue("");
			} else {
				Customer details = (Customer) dataObject;
				if (details != null) {
					this.customer.setValue(details.getCustCIF());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		if (this.oldVar_sortOperator_finType == Filter.OP_IN || this.oldVar_sortOperator_finType == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinFeeRefundsList,
					"FinanceType", this.finType.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finType.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FinFeeRefundsList, "FinanceType");
			if (dataObject instanceof String) {
				this.finType.setValue("");
			} else {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		if (this.oldVar_sortOperator_finBranch == Filter.OP_IN
				|| this.oldVar_sortOperator_finBranch == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinFeeRefundsList, "Branch",
					this.finBranch.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finBranch.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FinFeeRefundsList, "Branch");
			if (dataObject instanceof String) {
				this.finBranch.setValue("");
			} else {
				Branch details = (Branch) dataObject;
				if (details != null) {
					this.finBranch.setValue(details.getBranchCode());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// On Change Events for Multi-Selection Listbox's for Search operators

	public void onSelect$sortOperator_FeeReceiptCustomer(Event event) {
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_Customer, oldVar_sortOperator_custCIF,
				this.customer);
	}

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_FinType, oldVar_sortOperator_finType,
				this.finType);
	}

	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_FinBranch,
				oldVar_sortOperator_finBranch, this.finBranch);
	}

	private int doChangeStringOperator(Listbox listbox, int oldOperator, Textbox textbox) {

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = Integer.parseInt(((ValueLabel) item.getAttribute("data")).getValue());

		if (oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN) {
			if (!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)) {
				textbox.setValue("");
			}
		} else {
			if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
				textbox.setValue("");
			}
		}
		return searchOpId;

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

	public void setFinFeeRefundService(FinFeeRefundService finFeeRefundService) {
		this.finFeeRefundService = finFeeRefundService;
	}
}