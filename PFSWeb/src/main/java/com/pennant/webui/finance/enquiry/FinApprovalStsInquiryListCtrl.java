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
 * * FileName : PoolExecutionDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-08-2013 * *
 * Modified Date : 01-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.enquiry.model.FinApprovalStsInquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/PoolExecution/PoolExecutionDetail/PoolExecutionDetailList.zul
 * file.
 */
public class FinApprovalStsInquiryListCtrl extends GFCBaseListCtrl<CustomerFinanceDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_FinApprovalStsInquiryList;
	protected Borderlayout borderLayout_FinApprovalStsInquiryList;
	protected Paging pagingFinApprovalStsInquiryList;
	protected Listbox listBoxCustFinanceDetail;

	protected Listheader listheader_FinApprovalStsInquiryList_CustCIF;
	protected Listheader listheader_FinApprovalStsInquiryList_CustShrtName;
	protected Listheader listheader_FinApprovalStsInquiryList_FinReference;
	protected Listheader listheader_FinApprovalStsInquiryList_FinType;
	protected Listheader listheader_FinApprovalStsInquiryList_FinAmount;
	protected Listheader listheader_FinApprovalStsInquiryList_FinStartDate;
	protected Listheader listheader_FinApprovalStsInquiryList_lastMntByUser;
	protected Listheader listheader_FinApprovalStsInquiryList_CurrentRole;
	protected Listheader listheader_FinApprovalStsInquiryList_PreviousRole;

	protected Button button_FinApprovalStsInquiryList_Search;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	// NEEDED for the ReUse in the SearchWindow

	protected Textbox moduleType;

	protected Textbox finReference;
	protected Listbox sortOperator_finReference;

	protected Textbox custCIF;
	protected Listbox sortOperator_custCIF;

	protected Textbox custShrtName;
	protected Listbox sortOperator_cusShrtName;

	protected Textbox finType;
	protected Listbox sortOperator_finType;

	protected Textbox custID;
	protected Listbox sortOperator_custID;

	protected Textbox mobileNo;
	protected Listbox sortOperator_mobileNo;

	protected Textbox emailID;
	protected Listbox sortOperator_emailID;

	protected Textbox branchCode;
	protected Listbox sortOperator_Branch;

	protected Textbox currentRole;
	protected Listbox sortOperator_CurrentRole;

	protected Textbox previousRole;
	protected Listbox sortOperator_PreviousRole;

	protected Row row_Role;

	protected Button btnSearchFinType;
	protected Button btnSearchBranch;
	protected Button btnSearchCurrentRole;
	protected Button btnSearchPreviousRole;

	private transient boolean approvedList = false;

	private ApprovalStatusEnquiryService approvalStatusEnquiryService;
	boolean facility = false;

	private Label label_FinApprovalStsInquiryList_FinReference;

	/**
	 * default constructor.<br>
	 */
	public FinApprovalStsInquiryListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		searchObject.addWhereClause(getUsrFinAuthenticationQry(false));
	}

	@Override
	protected void doPrintResults() {
		if (facility) {
			new PTListReportUtils("FacilityApprovalStatus", searchObject, this.paging.getTotalSize() + 1);
		} else {
			new PTListReportUtils("FinanceApprovalStatus", searchObject, this.paging.getTotalSize() + 1);
		}
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerFinanceDetail";
		super.pageRightName = "";
		if (facility) {
			super.tableName = "CustomerFacilityDetails_AView";
			super.queueTableName = "CustomerFacilityDetails_View";
			super.enquiryTableName = "CustomerFacilityDetails_View";
		} else {
			super.tableName = "CustomerFinanceDetails_AView";
			super.queueTableName = "CustomerFinanceDetails_View";
			super.enquiryTableName = "CustomerFinanceDetails_View";
		}
	}

	public void onCreate$window_FinApprovalStsInquiryList(Event event) {
		logger.debug("Entering");

		if (StringUtils.equals(moduleType.getValue(), PennantConstants.WORFLOW_MODULE_FACILITY)) {
			facility = true;
		} else {
			facility = false;
		}

		// Set the page level components.
		setPageComponents(window_FinApprovalStsInquiryList, borderLayout_FinApprovalStsInquiryList,
				listBoxCustFinanceDetail, pagingFinApprovalStsInquiryList);
		setItemRender(new FinApprovalStsInquiryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinApprovalStsInquiryList_Search);

		registerField("FinID");
		registerField("FinReference", listheader_FinApprovalStsInquiryList_FinReference, SortOrder.ASC, finReference,
				sortOperator_finReference, Operators.STRING);
		registerField("CustCIF", listheader_FinApprovalStsInquiryList_CustCIF, SortOrder.NONE, custCIF,
				sortOperator_custCIF, Operators.STRING);
		registerField("CustShrtName", listheader_FinApprovalStsInquiryList_CustShrtName, SortOrder.NONE, custShrtName,
				sortOperator_cusShrtName, Operators.STRING);
		registerField("FinType", listheader_FinApprovalStsInquiryList_FinType, SortOrder.NONE, finType,
				sortOperator_finType, Operators.MULTISELECT);
		if (branchCode != null) {
			registerField("FinBranch", branchCode, SortOrder.NONE, sortOperator_Branch, Operators.MULTISELECT);
		}

		if (currentRole != null) {
			registerField("NextRoleDesc", listheader_FinApprovalStsInquiryList_CurrentRole, SortOrder.NONE, currentRole,
					sortOperator_CurrentRole, Operators.STRING);
		}

		if (currentRole != null) {
			registerField("PrvRoleDesc", listheader_FinApprovalStsInquiryList_PreviousRole, SortOrder.NONE,
					previousRole, sortOperator_PreviousRole, Operators.STRING);
		}

		if (listheader_FinApprovalStsInquiryList_FinAmount != null) {
			registerField("FinAmount", listheader_FinApprovalStsInquiryList_FinAmount);
		}
		registerField("DownPayment");
		registerField("FeeChargeAmt");

		if (listheader_FinApprovalStsInquiryList_FinStartDate != null) {
			registerField("FinStartDate", listheader_FinApprovalStsInquiryList_FinStartDate);
		}

		registerField("CUSTID");
		registerField("RoleCode");
		registerField("NextRoleCode");
		registerField("DeptDesc");
		registerField("FinCcy");
		registerField("FinTypeDesc");

		// Render the page and display the data.
		doRenderPage();
		approvedList = true;
		this.listheader_FinApprovalStsInquiryList_CurrentRole.setVisible(false);
		this.listheader_FinApprovalStsInquiryList_PreviousRole.setVisible(false);

		if (this.row_Role != null) {
			this.row_Role.setVisible(false);
		}

		doDesignByMode();

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FinApprovalStsInquiryList_Search(Event event) {
		search();
		doDesignByMode();
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
	public void onFinApprovalStsInquiryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustFinanceDetail.getSelectedItem();
		List<ReasonDetailsLog> reasonDetailsList = null;

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CustomerFinanceDetail aCustomerFinanceDetail = null;

		if (approvedList) {
			if (facility) {
				aCustomerFinanceDetail = approvalStatusEnquiryService.getApprovedCustomerFacilityById(id);
			} else {
				aCustomerFinanceDetail = approvalStatusEnquiryService.getApprovedCustomerFinanceById(id, null);
			}
		} else {
			if (facility) {
				aCustomerFinanceDetail = approvalStatusEnquiryService.getCustomerFacilityById(id);
			} else {
				aCustomerFinanceDetail = approvalStatusEnquiryService.getCustomerFinanceById(id, null);
			}
		}

		if (StringUtils.trimToNull(id) != null) {
			reasonDetailsList = approvalStatusEnquiryService.getResonDetailsLog(id);
		}
		if (aCustomerFinanceDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		// concate the UserFName and the lastmntby user
		aCustomerFinanceDetail.setLastMntByUser(PennantApplicationUtil.getFullName(aCustomerFinanceDetail.getUsrFName(),
				aCustomerFinanceDetail.getLastMntByUser(), ""));

		logUserAccess("menu_Item_FinanceApprovalStsInquiry", aCustomerFinanceDetail.getFinReference());
		doShowDialogPage(aCustomerFinanceDetail, reasonDetailsList);
		// Since it is a inquiry role check may not be required.
		// // Check whether the user has authority to change/view the record.
		// String whereCond = " AND FinReference='" + aCustomerFinanceDetail.getFinReference() + "' AND version="
		// + aCustomerFinanceDetail.getVersion() + " ";
		//
		// if (doCheckAuthority(aCustomerFinanceDetail, whereCond)) {
		// // Set the latest work-flow id for the new maintenance request.
		// if (isWorkFlowEnabled() && aCustomerFinanceDetail.getWorkflowId() == 0) {
		// aCustomerFinanceDetail.setWorkflowId(getWorkFlowId());
		// }
		//
		// } else {
		// MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		// }

	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		approvedList = true;
		this.listheader_FinApprovalStsInquiryList_CurrentRole.setVisible(false);
		this.listheader_FinApprovalStsInquiryList_PreviousRole.setVisible(false);
		this.row_Role.setVisible(false);
		this.currentRole.setValue("");
		this.previousRole.setValue("");
		this.sortOperator_CurrentRole.setSelectedIndex(0);
		this.sortOperator_PreviousRole.setSelectedIndex(0);
		search();
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		approvedList = false;
		this.listheader_FinApprovalStsInquiryList_CurrentRole.setVisible(true);
		this.listheader_FinApprovalStsInquiryList_PreviousRole.setVisible(true);
		this.row_Role.setVisible(true);
		search();
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_finType, this.finType, "FinanceType");

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_Branch, this.branchCode, "Branch");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCurrentRole(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_CurrentRole, this.currentRole, "SecurityRoleEnq");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchPreviousRole(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_PreviousRole, this.previousRole, "SecurityRoleEnq");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCustomerFinanceDetail The entity that need to be passed to the dialog.
	 * @param reasonDetailsList
	 */
	private void doShowDialogPage(CustomerFinanceDetail aCustomerFinanceDetail,
			List<ReasonDetailsLog> reasonDetailsList) {
		logger.debug("Entering");

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("customerFinanceDetail", aCustomerFinanceDetail);
		arg.put("FinApprovalStsInquiryListCtrl", this);
		arg.put("facility", this.facility);
		arg.put("reasonDetailsList", reasonDetailsList);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceEnquiry/FinApprovalStsInquiry/FinApprovalStsInquiryDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	public void setApprovalStatusEnquiryService(ApprovalStatusEnquiryService approvalStatusEnquiryService) {
		this.approvalStatusEnquiryService = approvalStatusEnquiryService;
	}

	private void doDesignByMode() {
		if (facility) {
			this.label_FinApprovalStsInquiryList_FinReference
					.setValue(Labels.getLabel("label_FacilityApprovalStsInquiryList_CAFReference.value"));
			this.listheader_FinApprovalStsInquiryList_FinAmount.setVisible(false);
			this.listheader_FinApprovalStsInquiryList_FinReference
					.setLabel(Labels.getLabel("label_FacilityApprovalStsInquiryList_CAFReference.value"));
			this.listheader_FinApprovalStsInquiryList_FinType
					.setLabel(Labels.getLabel("label_FacilityApprovalStsInquiryList_FacilityType.value"));
		}
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
}