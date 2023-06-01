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

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.financemanagement.receipts.model.ReceiptCancellationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/ReceiptCancellation/ReceiptCancellationList.zul
 * file.
 */
public class ReceiptCancellationListCtrl extends GFCBaseListCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 5327118548986437717L;

	protected Window window_ReceiptCancellationList;
	protected Borderlayout borderLayout_ReceiptCancellationList;
	protected Listbox listBoxReceiptCancellation;
	protected Paging pagingReceiptCancellationList;

	protected Listheader listheader_ReceiptCancellationReference;
	protected Listheader listheader_ReceiptCancellationPurpose;
	protected Listheader listheader_ReceiptCancellationMode;
	protected Listheader listheader_ReceiptCancellationAmount;
	protected Listheader listheader_ReceiptCancellationAllocattionType;
	protected Listheader listheader_ReceiptCancellationFinType;
	protected Listheader listheader_ReceiptCancellationFinBranch;
	protected Listheader listheader_ReceiptCancellationCusomer;
	protected Listheader listheader_ReceiptCancellationCustName;
	protected Listheader listheader_ReceiptCancellationReceivedDate;
	protected Listheader listheader_ReceiptExtReference;

	protected Button btnNew;
	protected Button btnSearch;

	protected Textbox externalReference;
	protected Textbox customer;
	protected Combobox purpose;
	protected Combobox receiptMode;
	protected Combobox allocationType;
	protected Textbox finType;
	protected Textbox finBranch;
	protected Uppercasebox transactionRef;

	protected Listbox sortOperator_ReceiptCancellationReference;
	protected Listbox sortOperator_ReceiptCancellationCustomer;
	protected Listbox sortOperator_ReceiptCancellationPurpose;
	protected Listbox sortOperator_ReceiptCancellationReceiptMode;
	protected Listbox sortOperator_ReceiptCancellationAllocationType;
	protected Listbox sortOperator_ReceiptCancellationFinType;
	protected Listbox sortOperator_ReceiptCancellationFinBranch;
	protected Listbox sortOperator_ReceiptCancellationTranRef;
	protected Listbox sortOperator_ExternalReference;

	protected int oldVar_sortOperator_custCIF;
	protected int oldVar_sortOperator_finType;
	protected int oldVar_sortOperator_finBranch;

	private transient ReceiptCancellationService receiptCancellationService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private String module;

	// Adding Promotion Details to the List Header
	protected Listheader listheader_ReceiptCancellation_ReceiptRef;
	protected Listheader listheader_ReceiptCancellation_PromotionCode;
	protected Listheader listheader_ReceiptCancellation_ReceiptDate;

	/**
	 * The default constructor.
	 */
	public ReceiptCancellationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

		this.module = getArgument("module");
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			super.moduleCode = "ReceiptBounce";
			super.pageRightName = "ReceiptBounceList";
		} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			super.moduleCode = "ReceiptCancellation";
			super.pageRightName = "ReceiptCancellationList";
		} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			super.moduleCode = "ReceiptCancellation";
			super.pageRightName = "ReceiptCancellationList";
		}

		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			super.tableName = "FinReceiptHeader_FCDView";
			super.queueTableName = "FinReceiptHeader_FCDView";
			super.enquiryTableName = "FinReceiptHeader_FCDView";
		} else {
			super.tableName = "FinReceiptHeader_View";
			super.queueTableName = "FinReceiptHeader_View";
			super.enquiryTableName = "FinReceiptHeader_View";
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReceiptCancellationList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReceiptCancellationList, borderLayout_ReceiptCancellationList,
				listBoxReceiptCancellation, pagingReceiptCancellationList);
		setItemRender(new ReceiptCancellationListModelItemRenderer());
		registerButton(btnNew, "button_ReceiptCancellationList_NewReceiptCancellation", false);
		registerButton(btnSearch);

		registerField("receiptID");
		registerField("finCcy");
		registerField("Reference");
		registerField("ExtReference", listheader_ReceiptCancellationReference, SortOrder.ASC, externalReference,
				sortOperator_ReceiptCancellationReference, Operators.STRING);
		registerField("custCIF", listheader_ReceiptCancellationCusomer, SortOrder.NONE, customer,
				sortOperator_ReceiptCancellationCustomer, Operators.STRING);
		fillComboBox(this.purpose, "", PennantStaticListUtil.getReceiptPurpose(), "");
		registerField("receiptPurpose", listheader_ReceiptCancellationPurpose, SortOrder.NONE, purpose,
				sortOperator_ReceiptCancellationPurpose, Operators.STRING);
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptModesByFeePayment(), "");
		} else {
			fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptModes(), "");
		}
		registerField("receiptMode", listheader_ReceiptCancellationMode, SortOrder.NONE, receiptMode,
				sortOperator_ReceiptCancellationReceiptMode, Operators.STRING);
		registerField("receiptAmount", listheader_ReceiptCancellationAmount);
		fillComboBox(this.allocationType, "", PennantStaticListUtil.getAllocationMethods(), "");
		registerField("allocationType", listheader_ReceiptCancellationAllocattionType, SortOrder.NONE, allocationType,
				sortOperator_ReceiptCancellationAllocationType, Operators.STRING);
		registerField("finType", listheader_ReceiptCancellationFinType, SortOrder.NONE, finType,
				sortOperator_ReceiptCancellationFinType, Operators.STRING);
		registerField("finBranch", listheader_ReceiptCancellationFinBranch, SortOrder.NONE, finBranch,
				sortOperator_ReceiptCancellationFinBranch, Operators.STRING);
		registerField("transactionRef", listheader_ReceiptCancellation_ReceiptRef, SortOrder.NONE, transactionRef,
				sortOperator_ReceiptCancellationTranRef, Operators.STRING);
		registerField("promotionCode", listheader_ReceiptCancellation_PromotionCode, SortOrder.NONE);
		registerField("receiptDate", listheader_ReceiptCancellation_ReceiptDate, SortOrder.NONE);
		registerField("productCategory");

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)
				|| StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {

			StringBuilder whereClause = new StringBuilder();
			whereClause = whereClause.append(" FinIsActive = 1 ");
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
				whereClause = whereClause.append(" AND ReceiptMode IN( '");
				whereClause = whereClause.append(ReceiptMode.CHEQUE);
				whereClause = whereClause.append("','");
				whereClause = whereClause.append(ReceiptMode.DD);
				whereClause = whereClause.append("') ");
			}

			whereClause = whereClause.append(" AND  ReceiptPurpose = '");
			whereClause = whereClause.append(FinServiceEvent.SCHDRPY);
			whereClause = whereClause.append("' AND (ReceiptModeStatus = '");
			whereClause = whereClause.append(RepayConstants.PAYSTATUS_APPROVED);
			whereClause = whereClause.append("' OR (ReceiptModeStatus = '");
			whereClause = whereClause.append(RepayConstants.PAYSTATUS_REALIZED);
			if (App.DATABASE == Database.ORACLE) {
				whereClause = whereClause.append("' AND RecordType IS NULL ) OR ( ReceiptModeStatus = '");
			} else {
				whereClause = whereClause.append("' AND RecordType = '' ) OR ( ReceiptModeStatus = '");
			}

			// Module Parameter
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
				whereClause = whereClause.append(RepayConstants.PAYSTATUS_BOUNCE);
			} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
				whereClause = whereClause.append(RepayConstants.PAYSTATUS_CANCEL);
			}

			if (App.DATABASE == Database.ORACLE) {
				whereClause = whereClause.append("' AND RecordType IS NOT NULL )) ");
			} else {
				whereClause = whereClause.append("' AND RecordType <> '' )) ");
			}

			this.searchObject.addWhereClause(whereClause.toString());

		} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {

			StringBuilder whereClause = new StringBuilder();
			whereClause = whereClause.append(" ((RECAGAINST In ('C','O') And FinIsActive = 0) OR(FinIsActive = 1))");
			whereClause = whereClause.append("  AND  ReceiptPurpose = '");
			whereClause = whereClause.append(FinServiceEvent.FEEPAYMENT);
			whereClause = whereClause.append("' AND ((ReceiptModeStatus = '");
			whereClause = whereClause.append(RepayConstants.PAYSTATUS_FEES);
			if (App.DATABASE == Database.ORACLE) {
				whereClause = whereClause.append("' AND RecordType IS NULL ) OR ( ReceiptModeStatus = '");
			} else {
				whereClause = whereClause.append("' AND RecordType = '' ) OR ( ReceiptModeStatus = '");
			}
			whereClause = whereClause.append(RepayConstants.PAYSTATUS_CANCEL);
			if (App.DATABASE == Database.ORACLE) {
				whereClause = whereClause.append("' AND RecordType IS NOT NULL )) ");
			} else {
				whereClause = whereClause.append("' AND RecordType <> '' )) ");
			}

			this.searchObject.addWhereClause(whereClause.toString());
		}
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
		this.sortOperator_ReceiptCancellationCustomer.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_ReceiptCancellationFinType.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_ReceiptCancellationFinBranch.setSelectedIndex(0);
		this.listBoxReceiptCancellation.getItems().clear();
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
	public void onClick$button_ReceiptCancellationList_NewReceiptCancellation(Event event) {
		logger.debug("Entering");

		FinReceiptData receiptData = new FinReceiptData();
		// Create a new entity.
		FinReceiptHeader receiptHeader = new FinReceiptHeader();
		receiptHeader.setNewRecord(true);
		receiptHeader.setWorkflowId(getWorkFlowId());
		receiptData.setReceiptHeader(receiptHeader);

		// Display the dialog page.
		doShowDialogPage(receiptData);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onReceiptCancellationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReceiptCancellation.getSelectedItem();

		// Get the selected entity.
		FinReceiptHeader headerListItem = (FinReceiptHeader) selectedItem.getAttribute("data");
		boolean isFeePayment = false;
		if (StringUtils.equals(headerListItem.getReceiptPurpose(), FinServiceEvent.FEEPAYMENT)) {
			isFeePayment = true;
		}

		FinReceiptHeader header = receiptCancellationService.getFinReceiptHeaderById(headerListItem.getReceiptID(),
				isFeePayment);

		if (header == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// If record is in Deposit Process, not allowed to do the Process on Realization
		if (header.isDepositProcess()) {

			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = header.getReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "65034", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
			return;
		}

		String whereCond = " where ReceiptID=?";

		// Check whether the user has authority to change/view the record.
		if (!enqiryModule && SysParamUtil.isAllowed(SMTParameterConstants.CHECK_USER_ACCESS_AUTHORITY)
				&& (StringUtils.isNotEmpty(header.getRecordStatus())
						&& !StringUtils.equals(PennantConstants.RCD_STATUS_SAVED, header.getRecordStatus())
						&& !StringUtils.equals(PennantConstants.RCD_STATUS_APPROVED, header.getRecordStatus())
						&& !StringUtils.equals(PennantConstants.RCD_STATUS_RESUBMITTED, header.getRecordStatus())
						&& doCheckAuthority(header.getLastMntBy()))) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		if (doCheckAuthority(header, whereCond, new Object[] { header.getReceiptID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && header.getWorkflowId() == 0) {
				header.setWorkflowId(getWorkFlowId());
			}

			FinReceiptData receiptData = new FinReceiptData();

			receiptData.setReceiptHeader(header);

			logUserAccess("menu_Item_FeeReceiptCancellation", header.getExtReference());
			doShowDialogPage(receiptData);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Checking whether the user has authority to change/view the record.
	 */
	private boolean doCheckAuthority(long lastMntBy) {
		if (lastMntBy == getUserWorkspace().getUserDetails().getUserId()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param header The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinReceiptData receiptData) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("receiptHeader", receiptData.getReceiptHeader());
		arg.put("receiptData", receiptData);
		arg.put("module", this.module);
		arg.put("moduleCode", this.moduleCode);
		arg.put("receiptCancellationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptCancellationDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.customer.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.customer.setValue(customer.getCustCIF());
		} else {
			this.customer.setValue("");
		}
		logger.debug("Leaving ");
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_ReceiptCancellationList,
					"FinanceType", this.finType.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finType.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_ReceiptCancellationList, "FinanceType");
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_ReceiptCancellationList,
					"Branch", this.finBranch.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finBranch.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_ReceiptCancellationList, "Branch");
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

	public void onSelect$sortOperator_ReceiptCancellationCustomer(Event event) {
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_ReceiptCancellationCustomer,
				oldVar_sortOperator_custCIF, this.customer);
	}

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_ReceiptCancellationFinType,
				oldVar_sortOperator_finType, this.finType);
	}

	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_ReceiptCancellationFinBranch,
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

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}
}