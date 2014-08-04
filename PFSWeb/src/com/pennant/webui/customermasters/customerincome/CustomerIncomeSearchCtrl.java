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
 * FileName    		:  CustomerIncomeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerincome;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class CustomerIncomeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -1790568396596835038L;
	private final static Logger logger = Logger.getLogger(CustomerIncomeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerIncomeSearch; 		// autoWired
	
	protected Textbox 		custCIF; 							// autoWired
	protected Listbox 		sortOperator_custCIF; 				// autoWired
	protected Textbox 		custIncomeType; 					// autoWired
	protected Listbox 		sortOperator_custIncomeType; 		// autoWired
	protected Decimalbox 	custIncome; 						// autoWired
	protected Listbox 		sortOperator_custIncome; 			// autoWired
	protected Textbox 		custIncomeCountry; 					// autoWired
	protected Listbox 		sortOperator_custIncomeCountry; 	// autoWired
	protected Textbox 		recordStatus; 						// autoWired
	protected Listbox 		recordType;							// autoWired
	protected Listbox 		sortOperator_recordStatus; 			// autoWired
	protected Listbox 		sortOperator_recordType; 			// autoWired
	
	protected Label label_CustomerIncomeSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerIncomeSearch_RecordType; 		// autoWired
	protected Label label_CustomerIncomeSearchResult; 			// autoWired

	// not autoWired variables
	private transient CustomerIncomeListCtrl customerIncomeCtrl; // overHanded per parameter
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerIncome");
	
	/**
	 * constructor
	 */
	public CustomerIncomeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerIncome object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerIncomeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerIncomeCtrl")) {
			this.customerIncomeCtrl = (CustomerIncomeListCtrl) args
			.get("customerIncomeCtrl");
		} else {
			this.customerIncomeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custIncomeType.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custIncomeType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custIncome.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_custIncome.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custIncomeCountry.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custIncomeCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerIncomeSearch_RecordStatus.setVisible(false);
			this.label_CustomerIncomeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<CustomerIncome> searchObj = (JdbcSearchObject<CustomerIncome>) args
							.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("custCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custIncomeType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIncomeType, filter);
					this.custIncomeType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custIncome")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_custIncome, filter);
					this.custIncome.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custIncomeCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIncomeCountry, filter);
					this.custIncomeCountry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showCustomerIncomeSeekDialog();
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_CustomerIncomeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerIncomeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerIncomeSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");

		final JdbcSearchObject<CustomerIncome> so = new JdbcSearchObject<CustomerIncome>(CustomerIncome.class);
		so.addTabelName("CustomerIncomes_View");
		so.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		if (isWorkFlowEnabled()) {
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem item_CustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (item_CustCIF != null) {
				final int searchOpId = ((SearchOperators) item_CustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%"+ this.custCIF.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue(),searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custIncomeType.getValue())) {

			// get the search operator
			final Listitem item_CustIncomeType = this.sortOperator_custIncomeType.getSelectedItem();

			if (item_CustIncomeType != null) {
				final int searchOpId = ((SearchOperators) item_CustIncomeType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custIncomeType", "%"+ this.custIncomeType.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custIncomeType",this.custIncomeType.getValue(), searchOpId));
				}
			}
		}
		if (this.custIncome.getValue() != null) {

			// get the search operator
			final Listitem item_CustIncome = this.sortOperator_custIncome.getSelectedItem();

			if (item_CustIncome != null) {
				final int searchOpId = ((SearchOperators) item_CustIncome.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custIncome", this.custIncome.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custIncomeCountry.getValue())) {

			// get the search operator
			final Listitem item_CustIncomeCountry = this.sortOperator_custIncomeCountry.getSelectedItem();

			if (item_CustIncomeCountry != null) {
				final int searchOpId = ((SearchOperators) item_CustIncomeCountry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custIncomeCountry", "%"+ this.custIncomeCountry.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custIncomeCountry",this.custIncomeCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%"+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("lovDescCustCIF", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.customerIncomeCtrl.setSearchObj(so);

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		 final Listbox listBox = this.customerIncomeCtrl.listBoxCustomerIncome;
			final Paging paging = this.customerIncomeCtrl.pagingCustomerIncomeList;
			
			((PagedListWrapper<CustomerIncome>) listBox.getModel()).init(so, listBox, paging);
			

		this.label_CustomerIncomeSearchResult.setValue(Labels.getLabel(
				"label_CustomerIncomeSearchResult.value")+ " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CustomerIncomeListCtrl getCustomerIncomeCtrl() {
		return customerIncomeCtrl;
	}

	public void setCustomerIncomeCtrl(CustomerIncomeListCtrl customerIncomeCtrl) {
		this.customerIncomeCtrl = customerIncomeCtrl;
	}
}