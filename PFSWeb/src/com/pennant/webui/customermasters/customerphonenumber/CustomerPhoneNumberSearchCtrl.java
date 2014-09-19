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
 * FileName    		:  CustomerPhoneNumberSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerphonenumber;

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
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class CustomerPhoneNumberSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5094374487816747465L;
	private final static Logger logger = Logger.getLogger(CustomerPhoneNumberSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CustomerPhoneNumberSearch;	// autowired
	
	protected Textbox phoneCustCIF; 					// autowired
	protected Listbox sortOperator_phoneCustCIF; 		// autowired
	protected Textbox phoneTypeCode; 					// autowired
	protected Listbox sortOperator_phoneTypeCode; 		// autowired
	protected Textbox phoneCountryCode; 				// autowired
	protected Listbox sortOperator_phoneCountryCode; 	// autowired
	protected Textbox phoneAreaCode; 					// autowired
	protected Listbox sortOperator_phoneAreaCode; 		// autowired
	protected Textbox phoneNumber; 						// autowired
	protected Listbox sortOperator_phoneNumber; 		// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus;	 	// autowired
	protected Listbox sortOperator_recordType; 			// autowired
	
	protected Label label_CustomerPhoneNumberSearch_RecordStatus; 	// autowired
	protected Label label_CustomerPhoneNumberSearch_RecordType; 	// autowired
	protected Label label_CustomerPhoneNumberSearchResult; 			// autowired

	// not auto wired vars
	private transient CustomerPhoneNumberListCtrl customerPhoneNumberCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerPhoneNumber");
	
	/**
	 * constructor
	 */
	public CustomerPhoneNumberSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPhoneNumber object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPhoneNumberSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerPhoneNumberCtrl")) {
			this.customerPhoneNumberCtrl = (CustomerPhoneNumberListCtrl) args.get("customerPhoneNumberCtrl");
		} else {
			this.customerPhoneNumberCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_phoneCustCIF.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_phoneCustCIF.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_phoneTypeCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_phoneTypeCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_phoneCountryCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_phoneCountryCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_phoneAreaCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_phoneAreaCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_phoneNumber.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_phoneNumber.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerPhoneNumberSearch_RecordStatus.setVisible(false);
			this.label_CustomerPhoneNumberSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerPhoneNumber> searchObj = (JdbcSearchObject<CustomerPhoneNumber>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("phoneCustCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_phoneCustCIF, filter);
					this.phoneCustCIF.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("phoneTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_phoneTypeCode, filter);
					this.phoneTypeCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("phoneCountryCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_phoneCountryCode, filter);
					this.phoneCountryCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("phoneAreaCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_phoneAreaCode, filter);
					this.phoneAreaCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("phoneNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_phoneNumber, filter);
					this.phoneNumber.setValue(filter.getValue().toString());
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
		showCustomerPhoneNumberSeekDialog();
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
		this.window_CustomerPhoneNumberSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerPhoneNumberSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerPhoneNumberSearch.doModal();
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
		final JdbcSearchObject<CustomerPhoneNumber> so = new JdbcSearchObject<CustomerPhoneNumber>(
				CustomerPhoneNumber.class);
		so.addTabelName("CustomerPhoneNumbers_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		if (StringUtils.isNotEmpty(this.phoneCustCIF.getValue())) {

			// get the search operator
			final Listitem itemPhoneCustCIF = this.sortOperator_phoneCustCIF.getSelectedItem();
			if (itemPhoneCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemPhoneCustCIF.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%" + 
							this.phoneCustCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.phoneCustCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.phoneTypeCode.getValue())) {

			// get the search operator
			final Listitem itemPhoneTypeCode = this.sortOperator_phoneTypeCode.getSelectedItem();
			if (itemPhoneTypeCode != null) {
				final int searchOpId = ((SearchOperators) itemPhoneTypeCode.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("phoneTypeCode", "%" + 
							this.phoneTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("phoneTypeCode", this.phoneTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.phoneCountryCode.getValue())) {

			// get the search operator
			final Listitem itemPhoneCountryCode = this.sortOperator_phoneCountryCode.getSelectedItem();
			if (itemPhoneCountryCode != null) {
				final int searchOpId = ((SearchOperators) itemPhoneCountryCode.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("phoneCountryCode", "%" + 
							this.phoneCountryCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("phoneCountryCode", this.phoneCountryCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.phoneAreaCode.getValue())) {

			// get the search operator
			final Listitem itemPhoneAreaCode = this.sortOperator_phoneAreaCode.getSelectedItem();
			if (itemPhoneAreaCode != null) {
				final int searchOpId = ((SearchOperators) itemPhoneAreaCode.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("phoneAreaCode", "%" + 
							this.phoneAreaCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("phoneAreaCode", this.phoneAreaCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.phoneNumber.getValue())) {

			// get the search operator
			final Listitem itemPhoneNumber = this.sortOperator_phoneNumber.getSelectedItem();
			if (itemPhoneNumber != null) {
				final int searchOpId = ((SearchOperators) itemPhoneNumber.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("phoneNumber", "%" + 
							this.phoneNumber.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("phoneNumber", this.phoneNumber.getValue(), searchOpId));
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
		
		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute(
						"data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() 
							+ "%", searchOpId));
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
		this.customerPhoneNumberCtrl.setSearchObj(so);

		// set the model to the listBox with the initial resultSet get by the DAO method.
		 final Listbox listBox = this.customerPhoneNumberCtrl.listBoxCustomerPhoneNumber;
			final Paging paging = this.customerPhoneNumberCtrl.pagingCustomerPhoneNumberList;
			
			((PagedListWrapper<CustomerPhoneNumber>) listBox.getModel()).init(so, listBox, paging);
			
		this.label_CustomerPhoneNumberSearchResult.setValue(Labels.getLabel("label_CustomerPhoneNumberSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

}