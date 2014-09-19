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
 * FileName    		:  CustomerMaintenanceSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class CustomerMaintenanceSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2028931514840691644L;
	private final static Logger logger = Logger.getLogger(CustomerMaintenanceSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerMaintenanceSearch; 	// autoWired

	protected Textbox custCIF; 							// autoWired
	protected Listbox sortOperator_custCIF; 			// autoWired
	protected Textbox custCoreBank; 					// autoWired
	protected Listbox sortOperator_custCoreBank; 		// autoWired
	protected Textbox custCtgCode; 						// autoWired
	protected Listbox sortOperator_custCtgCode; 		// autoWired
	protected Textbox custTypeCode; 					// autoWired
	protected Listbox sortOperator_custTypeCode; 		// autoWired
	protected Textbox custSalutationCode; 				// autoWired
	protected Listbox sortOperator_custSalutationCode;  // autoWired
	protected Textbox custFName; 						// autoWired
	protected Listbox sortOperator_custFName; 			// autoWired
	protected Textbox custMName; 						// autoWired
	protected Listbox sortOperator_custMName; 			// autoWired
	protected Textbox custLName; 						// autoWired
	protected Listbox sortOperator_custLName; 			// autoWired
	protected Textbox custShrtName; 					// autoWired
	protected Listbox sortOperator_custShrtName; 		// autoWired
	protected Textbox custDftBranch; 					// autoWired
	protected Listbox sortOperator_custDftBranch; 		// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType;						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired

	protected Label label_CustomerSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerSearch_RecordType; 	// autoWired
	protected Label label_CustomerSearchResult; 		// autoWired

	// not auto wired variables
	private transient CustomerMaintenanceListCtrl customerMaintenanceCtrl; // overHanded per parameter
	private transient CustomerService customerService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Customer");
	protected JdbcSearchObject<Customer> searchObj;
	
	/**
	 * constructor
	 */
	public CustomerMaintenanceSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerMaintenanceSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerMaintenanceCtrl")) {
			this.customerMaintenanceCtrl = (CustomerMaintenanceListCtrl) args.get("customerMaintenanceCtrl");
		} else {
			this.customerMaintenanceCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCoreBank.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCoreBank.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCtgCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCtgCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custTypeCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custSalutationCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSalutationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custFName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custFName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custMName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custMName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custLName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custLName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custShrtName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custShrtName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custDftBranch.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custDftBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerSearch_RecordStatus.setVisible(false);
			this.label_CustomerSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<Customer>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("custCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custCoreBank")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCoreBank, filter);
					this.custCoreBank.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custCtgCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCtgCode, filter);
					this.custCtgCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custTypeCode, filter);
					this.custTypeCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custSalutationCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSalutationCode, filter);
					this.custSalutationCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custFName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custFName, filter);
					this.custFName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custMName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custMName, filter);
					this.custMName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custLName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custLName, filter);
					this.custLName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custShrtName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custShrtName, filter);
					this.custShrtName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custDftBranch")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custDftBranch, filter);
					this.custDftBranch.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showCustomerSeekDialog();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug(event.toString());
		doSearch();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		this.window_CustomerMaintenanceSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_CustomerMaintenanceSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {

		final JdbcSearchObject<Customer> so = new JdbcSearchObject<Customer>(Customer.class);
		so.addTabelName("Customers_View");
		so.addFilter(this.searchObj.getFilters().get(0));
		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem itemCustCIF = this.sortOperator_custCIF.getSelectedItem();
			if (itemCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCIF", "%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custCoreBank.getValue())) {

			// get the search operator
			final Listitem itemCustCoreBank = this.sortOperator_custCoreBank.getSelectedItem();
			if (itemCustCoreBank != null) {
				final int searchOpId = ((SearchOperators) itemCustCoreBank.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCoreBank", "%" + this.custCoreBank.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCoreBank", this.custCoreBank.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custCtgCode.getValue())) {

			// get the search operator
			final Listitem itemCustCtgCode = this.sortOperator_custCtgCode.getSelectedItem();
			if (itemCustCtgCode != null) {
				final int searchOpId = ((SearchOperators) itemCustCtgCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCtgCode", "%" + this.custCtgCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCtgCode", this.custCtgCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custTypeCode.getValue())) {

			// get the search operator
			final Listitem itemCustTypeCode = this.sortOperator_custTypeCode.getSelectedItem();
			if (itemCustTypeCode != null) {
				final int searchOpId = ((SearchOperators) itemCustTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custTypeCode", "%" + this.custTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custTypeCode", this.custTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSalutationCode.getValue())) {

			// get the search operator
			final Listitem itemCustSalutationCode = this.sortOperator_custSalutationCode.getSelectedItem();
			if (itemCustSalutationCode != null) {
				final int searchOpId = ((SearchOperators) itemCustSalutationCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSalutationCode", "%" + this.custSalutationCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSalutationCode", this.custSalutationCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custFName.getValue())) {

			// get the search operator
			final Listitem itemCustFName = this.sortOperator_custFName.getSelectedItem();
			if (itemCustFName != null) {
				final int searchOpId = ((SearchOperators) itemCustFName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custFName", "%" + this.custFName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custFName", this.custFName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custMName.getValue())) {

			// get the search operator
			final Listitem itemCustMName = this.sortOperator_custMName.getSelectedItem();
			if (itemCustMName != null) {
				final int searchOpId = ((SearchOperators) itemCustMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custMName", "%" + this.custMName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custMName", this.custMName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custLName.getValue())) {

			// get the search operator
			final Listitem itemCustLName = this.sortOperator_custLName.getSelectedItem();
			if (itemCustLName != null) {
				final int searchOpId = ((SearchOperators) itemCustLName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custLName", "%" + this.custLName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custLName", this.custLName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custShrtName.getValue())) {

			// get the search operator
			final Listitem itemCustShrtName = this.sortOperator_custShrtName.getSelectedItem();
			if (itemCustShrtName != null) {
				final int searchOpId = ((SearchOperators) itemCustShrtName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custShrtName", "%" + this.custShrtName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custShrtName", this.custShrtName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custDftBranch.getValue())) {

			// get the search operator
			final Listitem itemCustDftBranch = this.sortOperator_custDftBranch.getSelectedItem();
			if (itemCustDftBranch != null) {
				final int searchOpId = ((SearchOperators) itemCustDftBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custDftBranch", "%" + this.custDftBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custDftBranch", this.custDftBranch.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("CustID", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.customerMaintenanceCtrl.setSearchObj(so);

		final Listbox listBox = this.customerMaintenanceCtrl.listBoxCustomer;
		final Paging paging = this.customerMaintenanceCtrl.pagingCustomerMaintenanceList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<Customer>) listBox.getModel()).init(so, listBox, paging);
		this.customerMaintenanceCtrl.setSearchObj(so);

		this.label_CustomerSearchResult.setValue(Labels.getLabel("label_CustomerSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	public CustomerService getCustomerService() {
		return this.customerService;
	}
}