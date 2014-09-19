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
 * FileName    		:  CustomerAddresSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeraddres;

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
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerAddresSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6343810456299341200L;
	private final static Logger logger = Logger.getLogger(CustomerAddresSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerAddresSearch; 		// autoWired

	protected Textbox custCIF; 							// autoWired
	protected Listbox sortOperator_custCIF; 				// autoWired
	protected Textbox custAddrType; 					// autoWired
	protected Listbox sortOperator_custAddrType; 		// autoWired
	protected Textbox custAddrHNbr; 					// autoWired
	protected Listbox sortOperator_custAddrHNbr; 		// autoWired
	protected Textbox custFlatNbr; 						// autoWired
	protected Listbox sortOperator_custFlatNbr; 		// autoWired
	protected Textbox custAddrStreet; 					// autoWired
	protected Listbox sortOperator_custAddrStreet; 		// autoWired
	protected Textbox custPOBox; 						// autoWired
	protected Listbox sortOperator_custPOBox; 			// autoWired
	protected Textbox custAddrCountry; 					// autoWired
	protected Listbox sortOperator_custAddrCountry; 	// autoWired
	protected Textbox custAddrProvince; 				// autoWired
	protected Listbox sortOperator_custAddrProvince; 	// autoWired
	protected Textbox custAddrCity; 					// autoWired
	protected Listbox sortOperator_custAddrCity; 		// autoWired
	protected Textbox custAddrZIP; 						// autoWired
	protected Listbox sortOperator_custAddrZIP; 		// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType; 						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired

	protected Label label_CustomerAddresSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerAddresSearch_RecordType; 		// autoWired
	protected Label label_CustomerAddresSearchResult; 			// autoWired

	// not autoWired variables
	private transient CustomerAddresListCtrl customerAddresCtrl; // over handed per parameter
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerAddres");

	/**
	 * constructor
	 */
	public CustomerAddresSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerAddres object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAddresSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerAddresCtrl")) {
			this.customerAddresCtrl = (CustomerAddresListCtrl) args.get("customerAddresCtrl");
		} else {
			this.customerAddresCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrHNbr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrHNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custFlatNbr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custFlatNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrStreet.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrStreet.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custPOBox.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrCountry.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrProvince.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrCity.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrCity.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAddrZIP.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrZIP.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerAddresSearch_RecordStatus.setVisible(false);
			this.label_CustomerAddresSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerAddres> searchObj = (JdbcSearchObject<CustomerAddres>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("custCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrType, filter);
					this.custAddrType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrHNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrHNbr, filter);
					this.custAddrHNbr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custFlatNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custFlatNbr, filter);
					this.custFlatNbr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrStreet")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrStreet, filter);
					this.custAddrStreet.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custPOBox")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custPOBox, filter);
					this.custPOBox.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrCountry, filter);
					this.custAddrCountry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrProvince")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrProvince, filter);
					this.custAddrProvince.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrCity")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrCity, filter);
					this.custAddrCity.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custAddrZIP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrZIP, filter);
					this.custAddrZIP.setValue(filter.getValue().toString());
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
		showCustomerAddresSeekDialog();
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
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
		this.window_CustomerAddresSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerAddresSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerAddresSearch.doModal();
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
	 * 1. Checks for each TextBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<CustomerAddres> so = new JdbcSearchObject<CustomerAddres>(CustomerAddres.class);
		so.addTabelName("CustomerAddresses_View");
		
		if (isWorkFlowEnabled()) {
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem itemCustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (itemCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemCustCIF.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%" + 
							this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrType.getValue())) {

			// get the search operator
			final Listitem itemCustAddrType = this.sortOperator_custAddrType.getSelectedItem();
			if (itemCustAddrType != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrType.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrType", "%" + 
							this.custAddrType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrType", this.custAddrType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrHNbr.getValue())) {

			// get the search operator
			final Listitem itemCustAddrHNbr = this.sortOperator_custAddrHNbr.getSelectedItem();
			if (itemCustAddrHNbr != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrHNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrHNbr", "%" +
							this.custAddrHNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrHNbr", this.custAddrHNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custFlatNbr.getValue())) {

			// get the search operator
			final Listitem itemCustFlatNbr = this.sortOperator_custFlatNbr.getSelectedItem();
			if (itemCustFlatNbr != null) {
				final int searchOpId = ((SearchOperators) itemCustFlatNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custFlatNbr", "%" + 
							this.custFlatNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custFlatNbr", this.custFlatNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrStreet.getValue())) {

			// get the search operator
			final Listitem itemCustAddrStreet = this.sortOperator_custAddrStreet.getSelectedItem();
			if (itemCustAddrStreet != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrStreet.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrStreet", "%" + 
							this.custAddrStreet.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrStreet", this.custAddrStreet.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custPOBox.getValue())) {

			// get the search operator
			final Listitem itemCustPOBox = this.sortOperator_custPOBox.getSelectedItem();
			if (itemCustPOBox != null) {
				final int searchOpId = ((SearchOperators) itemCustPOBox.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custPOBox", "%" + 
							this.custPOBox.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custPOBox", this.custPOBox.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrCountry.getValue())) {

			// get the search operator
			final Listitem itemCustAddrCountry = this.sortOperator_custAddrCountry.getSelectedItem();
			if (itemCustAddrCountry != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrCountry.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrCountry", "%" +
							this.custAddrCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrCountry", this.custAddrCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrProvince.getValue())) {

			// get the search operator
			final Listitem itemCustAddrProvince = this.sortOperator_custAddrProvince.getSelectedItem();
			if (itemCustAddrProvince != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrProvince.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrProvince", "%" + 
							this.custAddrProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrProvince", this.custAddrProvince.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrCity.getValue())) {

			// get the search operator
			final Listitem itemCustAddrCity = this.sortOperator_custAddrCity.getSelectedItem();
			if (itemCustAddrCity != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrCity.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrCity", "%" + 
							this.custAddrCity.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrCity", this.custAddrCity.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrZIP.getValue())) {

			// get the search operator
			final Listitem itemCustAddrZIP = this.sortOperator_custAddrZIP.getSelectedItem();
			if (itemCustAddrZIP != null) {
				final int searchOpId = ((SearchOperators) itemCustAddrZIP.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrZIP", "%" + 
							this.custAddrZIP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrZIP", this.custAddrZIP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + 
							this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType != null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute(
						"data")).getSearchOperatorId();

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
		so.addSort("lovDescCustCIF", false);

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
		this.customerAddresCtrl.setSearchObj(so);

		// set the model to the listBox with the initial resultSet get by the DAO
		final Listbox listBox = this.customerAddresCtrl.listBoxCustomerAddres;
		final Paging paging = this.customerAddresCtrl.pagingCustomerAddresList;
		
		((PagedListWrapper<CustomerAddres>) listBox.getModel()).init(so, listBox, paging);
		this.customerAddresCtrl.setSearchObj(so);

		
		this.label_CustomerAddresSearchResult.setValue(Labels
				.getLabel("label_CustomerAddresSearchResult.value") + " " + 
				String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CustomerAddresListCtrl getCustomerAddresCtrl() {
		return customerAddresCtrl;
	}
	public void setCustomerAddresCtrl(CustomerAddresListCtrl customerAddresCtrl) {
		this.customerAddresCtrl = customerAddresCtrl;
	}

}