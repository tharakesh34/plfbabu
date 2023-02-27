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

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.financemanagement.receipts.model.ReceiptRealizationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/FeeReceipt/FeeReceiptList.zul file.
 */
public class FeeReceiptListCtrl extends GFCBaseListCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 5327118548986437717L;

	protected Window window_FeeReceiptList;
	protected Borderlayout borderLayout_FeeReceiptList;
	protected Listbox listBoxFeeReceipt;
	protected Paging pagingFeeReceiptList;

	protected Listheader listheader_FeeReceiptId;
	protected Listheader listheader_FeeReceiptReference;
	protected Listheader listheader_FeeReceiptPurpose;
	protected Listheader listheader_FeeReceiptMode;
	protected Listheader listheader_FeeReceiptAmount;
	protected Listheader listheader_FeeReceiptAllocattionType;
	protected Listheader listheader_FeeReceiptFinType;
	protected Listheader listheader_FeeReceiptFinBranch;
	protected Listheader listheader_FeeReceiptCusomer;
	protected Listheader listheader_FeeReceiptCustName;
	protected Listheader listheader_ReceiptExtReference;

	protected Button btnNew;
	protected Button btnSearch;

	protected Longbox feeReceiptId;
	protected Textbox receiptReference;
	protected Textbox customer;
	protected Combobox purpose;
	protected Combobox receiptMode;
	protected Combobox allocationType;
	protected Textbox finType;
	protected Textbox finBranch;
	protected Uppercasebox externalReference;

	protected Listbox sortOperator_FeeReceiptId;
	protected Listbox sortOperator_FeeReceiptReference;
	protected Listbox sortOperator_FeeReceiptCustomer;
	protected Listbox sortOperator_FeeReceiptPurpose;
	protected Listbox sortOperator_FeeReceiptReceiptMode;
	protected Listbox sortOperator_FeeReceiptAllocationType;
	protected Listbox sortOperator_FeeReceiptFinType;
	protected Listbox sortOperator_FeeReceiptFinBranch;
	protected Listbox sortOperator_ExternalReference;

	protected int oldVar_sortOperator_custCIF;
	protected int oldVar_sortOperator_finType;
	protected int oldVar_sortOperator_finBranch;

	private FeeReceiptService feeReceiptService;

	// Added Promotion details
	protected Uppercasebox transactionRef;
	protected Listbox sortOperator_FeeReceiptSearchTranRef;
	protected Listheader listheader_FeeReceipt_PromotionCode;
	protected Listheader listheader_FeeReceipt_ReceiptRef;
	protected Listheader listheader_FeeReceipt_ReceiptDate;
	protected FinanceDetailService financeDetailService;

	/**
	 * The default constructor.
	 */
	public FeeReceiptListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FeeReceipt";
		super.pageRightName = "FeeReceiptList";
		super.tableName = "FinReceiptHeader_FDView";
		super.queueTableName = "FinReceiptHeader_FDView";
		super.enquiryTableName = "FinReceiptHeader_FDView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FeeReceiptList(Event event) {
		// Set the page level components.
		setPageComponents(window_FeeReceiptList, borderLayout_FeeReceiptList, listBoxFeeReceipt, pagingFeeReceiptList);
		setItemRender(new ReceiptRealizationListModelItemRenderer());
		registerButton(btnNew, "button_FeeReceiptList_NewFeeReceipt", true);
		registerButton(btnSearch);

		registerField("FinID");
		registerField("finCcy");
		registerField("RecAgainst");
		registerField("receiptID", listheader_FeeReceiptId, SortOrder.ASC, feeReceiptId, sortOperator_FeeReceiptId,
				Operators.NUMERIC);
		registerField("reference", listheader_FeeReceiptReference, SortOrder.ASC, receiptReference,
				sortOperator_FeeReceiptReference, Operators.STRING);
		registerField("custCIF", listheader_FeeReceiptCusomer, SortOrder.NONE, customer,
				sortOperator_FeeReceiptCustomer, Operators.STRING);
		fillComboBox(this.purpose, "", PennantStaticListUtil.getReceiptPurpose(),
				",SchdlRepayment,EarlyPayment,EarlySettlement,");
		registerField("receiptPurpose", listheader_FeeReceiptPurpose, SortOrder.NONE, purpose,
				sortOperator_FeeReceiptPurpose, Operators.STRING);
		fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptModesByFeePayment(), "");
		registerField("receiptMode", listheader_FeeReceiptMode, SortOrder.NONE, receiptMode,
				sortOperator_FeeReceiptReceiptMode, Operators.STRING);
		registerField("receiptAmount", listheader_FeeReceiptAmount);
		fillComboBox(this.allocationType, "", PennantStaticListUtil.getAllocationMethods(), "");
		registerField("allocationType", listheader_FeeReceiptAllocattionType, SortOrder.NONE, allocationType,
				sortOperator_FeeReceiptAllocationType, Operators.STRING);
		registerField("finType", listheader_FeeReceiptFinType, SortOrder.NONE, finType, sortOperator_FeeReceiptFinType,
				Operators.STRING);
		registerField("finBranch", listheader_FeeReceiptFinBranch, SortOrder.NONE, finBranch,
				sortOperator_FeeReceiptFinBranch, Operators.STRING);
		registerField("transactionRef", listheader_FeeReceipt_ReceiptRef, SortOrder.NONE, transactionRef,
				sortOperator_FeeReceiptSearchTranRef, Operators.STRING);
		registerField("promotionCode", listheader_FeeReceipt_PromotionCode, SortOrder.NONE);
		registerField("receiptDate", listheader_FeeReceipt_ReceiptDate, SortOrder.NONE);
		registerField("ExtReference", listheader_ReceiptExtReference, SortOrder.NONE, externalReference,
				sortOperator_ExternalReference, Operators.STRING);
		// Render the page and display the data.
		doRenderPage();

		search();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		this.searchObject.addFilter(new Filter("ReceiptModeStatus", RepayConstants.PAYSTATUS_FEES, Filter.OP_EQUAL));
		this.searchObject.addFilterNotNull("RecordType");
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
		this.sortOperator_FeeReceiptCustomer.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_FeeReceiptFinType.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_FeeReceiptFinBranch.setSelectedIndex(0);
		this.listBoxFeeReceipt.getItems().clear();
		this.oldVar_sortOperator_custCIF = 0;
		this.oldVar_sortOperator_finType = 0;
		this.oldVar_sortOperator_finBranch = 0;
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
		FinReceiptHeader receiptHeader = new FinReceiptHeader();
		receiptHeader.setNewRecord(true);
		receiptHeader.setWorkflowId(getWorkFlowId());

		Map<String, Object> map = getDefaultArguments();
		map.put("receiptHeader", receiptHeader);
		map.put("feeReceiptListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/SelectFeeReceiptList.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onReceiptItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFeeReceipt.getSelectedItem();

		// Get the selected entity.
		long receiptID = (long) selectedItem.getAttribute("id");
		FinReceiptHeader header = feeReceiptService.getFinReceiptHeaderById(receiptID, "_View");

		if (header == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where ReceiptID=?";

		if (doCheckAuthority(header, whereCond, new Object[] { header.getReceiptID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && header.getWorkflowId() == 0) {
				header.setWorkflowId(getWorkFlowId());
			}

			header.setFinCategory(financeDetailService.getOrgFinCategory(header.getReference()));

			logUserAccess("menu_Item_FeeReceipts", header.getReference());
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
	private void doShowDialogPage(FinReceiptHeader header) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("receiptHeader", header);
		arg.put("feeReceiptListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/FeeReceiptDialog.zul", null, arg);
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeReceiptList, "Customer",
					this.customer.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.customer.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FeeReceiptList, "Customer");
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeReceiptList, "FinanceType",
					this.finType.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finType.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FeeReceiptList, "FinanceType");
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeReceiptList, "Branch",
					this.finBranch.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finBranch.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FeeReceiptList, "Branch");
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
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_FeeReceiptCustomer,
				oldVar_sortOperator_custCIF, this.customer);
	}

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_FeeReceiptFinType,
				oldVar_sortOperator_finType, this.finType);
	}

	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_FeeReceiptFinBranch,
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

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}