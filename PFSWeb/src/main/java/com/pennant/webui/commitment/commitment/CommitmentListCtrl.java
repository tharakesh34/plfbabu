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
 * * FileName : CommitmentListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified
 * Date : 25-03-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.commitment.commitment;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.commitment.commitment.model.CommitmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Commitment/Commitment/CommitmentList.zul file.
 */
public class CommitmentListCtrl extends GFCBaseListCtrl<Commitment> {
	private static final long serialVersionUID = 1L;

	protected Window window_CommitmentList;
	protected Borderlayout borderLayout_CommitmentList;
	protected Paging pagingCommitmentList;
	protected Listbox listBoxCommitment;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CmtReference;
	protected Listheader listheader_CmtBranch;
	protected Listheader listheader_CmtCcy;
	protected Listheader listheader_CustName;
	protected Listheader listheader_CmtExpDate;
	protected Listheader listheader_CmtRvwDate;
	protected Listheader listheader_CmtRevolving;
	protected Listheader listheader_CmtAmount;
	protected Listheader listheader_CmtUtilized;
	protected Listheader listheader_CmtAvailable;

	protected Button btnRefresh;
	protected Button button_CommitmentList_NewCommitment;
	protected Button button_CommitmentList_CommitmentSearch;

	protected Textbox custCIF;
	protected Textbox cmtReference;
	protected Textbox cmtBranch;
	protected Textbox cmtCcy;
	protected Datebox cmtExpDate_one;
	protected Datebox cmtExpDate_two;
	protected Datebox cmtRvwDate_one;
	protected Datebox cmtRvwDate_two;
	protected Textbox custName;
	protected Checkbox revolving;
	protected Decimalbox cmtAmount;
	protected Decimalbox cmtAvailable;
	protected Decimalbox cmtUtilizedAmount;
	protected Checkbox nonPerforming;
	protected int CcyEditField;

	protected Listbox sortOperator_CustCIF;
	protected Listbox sortOperator_CmtReference;
	protected Listbox sortOperator_CmtBranch;
	protected Listbox sortOperator_CmtCcy;
	protected Listbox sortOperator_CustName;
	protected Listbox sortOperator_CmtExpDate;
	protected Listbox sortOperator_CmtRvwDate;
	protected Listbox sortOperator_Revolving;
	protected Listbox sortOperator_CmtAmount;
	protected Listbox sortOperator_CmtUtilizedAmount;
	protected Listbox sortOperator_CmtAvailable;
	protected Listbox sortOperator_NonPerforming;

	private CommitmentService commitmentService;
	private FinanceWorkFlowService financeWorkFlowService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CommitmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Commitment";
		super.pageRightName = "CommitmentList";
		super.tableName = "Commitments_AView";
		super.queueTableName = "Commitments_View";
		super.enquiryTableName = "Commitments_TView";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		// Commitment ExpiryDate
		if (this.cmtExpDate_one.getValue() != null || this.cmtExpDate_two.getValue() != null) {

			// get the search operator
			final Listitem itemCmtExpDate = this.sortOperator_CmtExpDate.getSelectedItem();
			if (itemCmtExpDate != null) {
				final int searchOpId = Integer.parseInt(((ValueLabel) itemCmtExpDate.getAttribute("data")).getValue());

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_BETWEEN) {
					if (this.cmtExpDate_one.getValue() != null) {
						this.searchObject.addFilter(new Filter("CmtExpDate",
								DateUtil.format(this.cmtExpDate_one.getValue(), PennantConstants.DBDateFormat),
								Filter.OP_GREATER_OR_EQUAL));
					}
					if (this.cmtExpDate_two.getValue() != null) {
						this.searchObject.addFilter(new Filter("CmtExpDate",
								DateUtil.format(this.cmtExpDate_two.getValue(), PennantConstants.DBDateFormat),
								Filter.OP_LESS_OR_EQUAL));
					}
				} else {
					this.searchObject.addFilter(new Filter("CmtExpDate",
							DateUtil.format(this.cmtExpDate_one.getValue(), PennantConstants.DBDateFormat),
							searchOpId));
				}
			}
		}

		// Value Date
		if (this.cmtRvwDate_one.getValue() != null || this.cmtRvwDate_two.getValue() != null) {

			// get the search operator
			final Listitem itemCmtRvwDate = this.sortOperator_CmtRvwDate.getSelectedItem();
			if (itemCmtRvwDate != null) {
				final int searchOpId = Integer.parseInt(((ValueLabel) itemCmtRvwDate.getAttribute("data")).getValue());

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_BETWEEN) {
					if (this.cmtRvwDate_one.getValue() != null) {
						this.searchObject.addFilter(new Filter("CmtRvwDate",
								DateUtil.format(this.cmtRvwDate_one.getValue(), PennantConstants.DBDateFormat),
								Filter.OP_GREATER_OR_EQUAL));
					}
					if (this.cmtRvwDate_two.getValue() != null) {
						this.searchObject.addFilter(new Filter("CmtRvwDate",
								DateUtil.format(this.cmtRvwDate_two.getValue(), PennantConstants.DBDateFormat),
								Filter.OP_LESS_OR_EQUAL));
					}
				} else {
					this.searchObject.addFilter(new Filter("CmtRvwDate",
							DateUtil.format(this.cmtRvwDate_one.getValue(), PennantConstants.DBDateFormat),
							searchOpId));
				}
			}
		}
	}

	@Override
	protected void doReset() {

		super.doReset();
		SearchFilterControl.resetFilters(cmtExpDate_one, sortOperator_CmtExpDate);
		SearchFilterControl.resetFilters(cmtExpDate_two);
		onChangeDateOperator(sortOperator_CmtExpDate, cmtExpDate_two);

		SearchFilterControl.resetFilters(cmtRvwDate_one, sortOperator_CmtRvwDate);
		SearchFilterControl.resetFilters(cmtRvwDate_two);
		onChangeDateOperator(sortOperator_CmtRvwDate, cmtRvwDate_two);
	}

	public void onSelect$sortOperator_CmtExpDate(Event event) {
		onChangeDateOperator(sortOperator_CmtExpDate, cmtExpDate_two);
	}

	public void onSelect$sortOperator_CmtRvwDate(Event event) {
		onChangeDateOperator(sortOperator_CmtRvwDate, cmtRvwDate_two);
	}

	private void onChangeDateOperator(Listbox sortOperator, Component component) {

		final Listitem item = sortOperator.getSelectedItem();
		final int searchOpId = Integer.parseInt(((ValueLabel) item.getAttribute("data")).getValue());

		if (component instanceof Datebox) {
			((Datebox) component).setText("");
			if (searchOpId == Filter.OP_BETWEEN) {
				((Datebox) component).setVisible(true);
			} else {
				((Datebox) component).setVisible(false);
			}

		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CommitmentList(Event event) {

		// Set the page level components.
		setPageComponents(window_CommitmentList, borderLayout_CommitmentList, listBoxCommitment, pagingCommitmentList);
		setItemRender(new CommitmentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CommitmentList_NewCommitment, "button_CommitmentList_NewCommitment", true);
		registerButton(button_CommitmentList_CommitmentSearch);

		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("CmtBranch", listheader_CmtBranch, SortOrder.NONE, cmtBranch, sortOperator_CmtBranch,
				Operators.STRING);

		registerField("CmtCcy", listheader_CmtCcy, SortOrder.NONE, cmtCcy, sortOperator_CmtCcy, Operators.STRING);
		registerField("CmtReference", listheader_CmtReference, SortOrder.ASC, cmtReference, sortOperator_CmtReference,
				Operators.STRING);

		registerField("CustShrtName", listheader_CustName, SortOrder.NONE, custName, sortOperator_CustName,
				Operators.STRING);
		registerField("Revolving", revolving, SortOrder.NONE, sortOperator_Revolving, Operators.BOOLEAN);

		registerField("CmtExpDate", listheader_CmtExpDate);
		SearchFilterControl.renderOperators(this.sortOperator_CmtExpDate, Operators.DATE_RANGE);

		registerField("CmtRvwDate", listheader_CmtRvwDate);
		SearchFilterControl.renderOperators(this.sortOperator_CmtRvwDate, Operators.DATE_RANGE);

		registerField("CmtAmount", listheader_CmtAmount);
		registerField("CmtUtilizedAmount", listheader_CmtUtilized);

		registerField("CmtAvailable", listheader_CmtAvailable, SortOrder.NONE, cmtAvailable, sortOperator_CmtAvailable,
				Operators.STRING);
		registerField("NonPerforming", nonPerforming, SortOrder.NONE, sortOperator_NonPerforming, Operators.BOOLEAN);

		// registerField("CcyEditField");
		registerField("nextRoleCode");

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CommitmentList_CommitmentSearch(Event event) {
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
	public void onClick$button_CommitmentList_NewCommitment(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Commitment aCommitment = new Commitment();
		aCommitment.setNewRecord(true);

		// Setting Workflow Details
		setWorkflowDetails(CommitmentConstants.WF_NEWCOMMITMENT);
		aCommitment.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCommitment);

		logger.debug("Leaving");
	}

	/**
	 * Method for Fetching Dynamic Workflow Details
	 * 
	 * @param commitmentType
	 */
	private void setWorkflowDetails(String commitmentType) {

		// Setting Workflow Details
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(commitmentType,
				FinServiceEvent.ORG, CommitmentConstants.MODULE_NAME);

		// Workflow Details Setup
		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);

			if (workFlowDetails.getFirstTaskOwner().contains(PennantConstants.DELIMITER_COMMA)) {
				String[] fisttask = workFlowDetails.getFirstTaskOwner().split(PennantConstants.DELIMITER_COMMA);
				for (String string : fisttask) {
					if (getUserWorkspace().isRoleContains(string)) {
						setFirstTask(true);
						break;
					}
				}
			} else {
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			}
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onCommitmentItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCommitment.getSelectedItem();

		// Get the selected entity.
		Commitment commitment = (Commitment) selectedItem.getAttribute("commitment");

		// Set Workflow Details
		String userRole = commitment.getNextRoleCode();
		if (StringUtils.isEmpty(commitment.getRecordType())) {
			setWorkflowDetails(CommitmentConstants.WF_NEWCOMMITMENT);
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}
		}

		Commitment aCommitment = commitmentService.getCommitmentByCmtRef(commitment.getCmtReference(), userRole,
				enqiryModule);

		if (aCommitment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Role Code State Checking
		String nextroleCode = aCommitment.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = aCommitment.getCmtReference();
			errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnRefresh, null);
			logger.debug("Leaving");
			return;
		}

		// Workflow ID Setup
		aCommitment.setWorkflowId(getWorkFlowId());

		// Check whether the user has authority to change/view the record.
		String whereCond = " where CustID = ?";

		if (doCheckAuthority(aCommitment, whereCond, new Object[] { aCommitment.getCustID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aCommitment.getWorkflowId() == 0) {
				aCommitment.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aCommitment);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCommitment The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Commitment aCommitment) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commitment", aCommitment);
		arg.put("commitmentListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws SuspendNotAllowedException
	 */
	public void onClick$btnSearchCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering  " + event.toString());

		doSearchCustomerCIF();
		// setSearchValue(sortOperator_CustCIF, this.custCIF, "Customer");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @throws InterruptedException
	 */
	private void doSearchCustomerCIF() throws InterruptedException {
		logger.debug("Entering");

		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
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
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCmtBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_CmtBranch, this.cmtBranch, "Branch");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCmtCcy(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_CmtCcy, this.cmtCcy, "Currency");

		logger.debug("Leaving" + event.toString());
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

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
}