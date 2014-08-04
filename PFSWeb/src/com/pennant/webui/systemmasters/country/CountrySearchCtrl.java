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
 * FileName    		:  CountrySearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.country;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.systemmasters.CountryService;
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
 * /WEB-INF/pages/SystemMaster/Country/CountrySearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CountrySearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2579308742377096765L;

	private final static Logger logger = Logger.getLogger(CountrySearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CountrySearch; 					// autoWired

	protected Textbox 		countryCode; 							// autoWired
	protected Listbox 		sortOperator_countryCode; 				// autoWired
	protected Textbox 		countryDesc; 							// autoWired
	protected Listbox 		sortOperator_countryDesc; 				// autoWired
	protected Decimalbox 	countryParentLimit; 					// autoWired
	protected Listbox 		sortOperator_countryParentLimit; 		// autoWired
	protected Decimalbox 	countryResidenceLimit; 					// autoWired
	protected Listbox 		sortOperator_countryResidenceLimit; 	// autoWired
	protected Decimalbox 	countryRiskLimit; 						// autoWired
	protected Listbox 		sortOperator_countryRiskLimit; 			// autoWired
	protected Checkbox 		countryIsActive; 						// autoWired
	protected Listbox 		sortOperator_countryIsActive;	 		// autoWired
	protected Textbox 		recordStatus; 							// autoWired
	protected Listbox 		recordType; 							// autoWired
	protected Listbox 		sortOperator_recordStatus; 				// autoWired
	protected Listbox 		sortOperator_recordType; 				// autoWired

	protected Label label_CountrySearch_RecordStatus; 				// autoWired
	protected Label label_CountrySearch_RecordType; 				// autoWired
	protected Label label_CountrySearchResult; 						// autoWired

	// not autoWired variables
	private transient CountryListCtrl countryCtrl; // over handed per parameter
	private transient CountryService countryService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Country");

	/**
	 * Default constructor
	 */
	public CountrySearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Country object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CountrySearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("countryCtrl")) {
			this.countryCtrl = (CountryListCtrl) args.get("countryCtrl");
		} else {
			this.countryCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_countryCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryParentLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_countryParentLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryResidenceLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_countryResidenceLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryRiskLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_countryRiskLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_countryIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CountrySearch_RecordStatus.setVisible(false);
			this.label_CountrySearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<Country> searchObj = (JdbcSearchObject<Country>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("countryCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_countryCode, filter);
					this.countryCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("countryDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_countryDesc, filter);
					this.countryDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("countryParentLimit")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_countryParentLimit, filter);
					this.countryParentLimit.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("countryResidenceLimit")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_countryResidenceLimit, filter);
					this.countryResidenceLimit.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("countryRiskLimit")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_countryRiskLimit, filter);
					this.countryRiskLimit.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("countryIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_countryIsActive, filter);
					//this.countryIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.countryIsActive.setChecked(true);
					}else{
						this.countryIsActive.setChecked(false);
					}
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
		showCountrySeekDialog();
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
		this.window_CountrySearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCountrySeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CountrySearch.doModal();
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<Country> so = new JdbcSearchObject<Country>(Country.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTCountries_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTCountries_AView");
		}

		if (StringUtils.isNotEmpty(this.countryCode.getValue())) {

			// get the search operator
			final Listitem item_CountryCode = this.sortOperator_countryCode.getSelectedItem();

			if (item_CountryCode != null) {
				final int searchOpId = ((SearchOperators) item_CountryCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("countryCode", "%" + this.countryCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("countryCode", this.countryCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.countryDesc.getValue())) {

			// get the search operator
			final Listitem item_CountryDesc = this.sortOperator_countryDesc.getSelectedItem();

			if (item_CountryDesc != null) {
				final int searchOpId = ((SearchOperators) item_CountryDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("countryDesc", "%" + this.countryDesc.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("countryDesc", this.countryDesc.getValue(), searchOpId));
				}
			}
		}
		 
		if (this.countryParentLimit.getValue()!=null) {

			// get the search operator
			final Listitem item_CountryParentLimit = this.sortOperator_countryParentLimit.getSelectedItem();

			if (item_CountryParentLimit != null) {
				final int searchOpId = ((SearchOperators) item_CountryParentLimit.getAttribute("data")).getSearchOperatorId();

				 if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("countryParentLimit", this.countryParentLimit.getValue(), searchOpId));
				}
			}
		}
		if (this.countryResidenceLimit.getValue()!=null) {

			// get the search operator
			final Listitem item_CountryResidenceLimit = this.sortOperator_countryResidenceLimit.getSelectedItem();

			if (item_CountryResidenceLimit != null) {
				final int searchOpId = ((SearchOperators) item_CountryResidenceLimit.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("countryResidenceLimit", this.countryResidenceLimit.getValue(), searchOpId));
				}
			}
		}
		if (this.countryRiskLimit.getValue()!=null) {

			// get the search operator
			final Listitem item_CountryRiskLimit = this.sortOperator_countryRiskLimit.getSelectedItem();

			if (item_CountryRiskLimit != null) {
				final int searchOpId = ((SearchOperators) item_CountryRiskLimit.getAttribute("data")).getSearchOperatorId();

				 if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("countryRiskLimit",	this.countryRiskLimit.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_CountryIsActive = this.sortOperator_countryIsActive.getSelectedItem();

		if (item_CountryIsActive != null) {
			final int searchOpId = ((SearchOperators) item_CountryIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.countryIsActive.isChecked()) {
					so.addFilter(new Filter("countryIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("countryIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%",	searchOpId));
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
					so.addFilter(new Filter("recordType", "%"
							+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("CountryCode", false);

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
		this.countryCtrl.setSearchObj(so);

		final Listbox listBox = this.countryCtrl.listBoxCountry;
		final Paging paging = this.countryCtrl.pagingCountryList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<Country>) listBox.getModel()).init(so, listBox, paging);
		this.countryCtrl.setSearchObj(so);

		this.label_CountrySearchResult.setValue(Labels.getLabel(
		"label_CountrySearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}
	public CountryService getCountryService() {
		return this.countryService;
	}
}