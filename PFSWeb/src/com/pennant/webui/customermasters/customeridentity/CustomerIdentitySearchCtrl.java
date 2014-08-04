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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerIdentitySearchCtrl.java                                                   * 	  
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
*//*

package com.pennant.webui.customermasters.customeridentity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.service.customermasters.CustomerIdentityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.util.AdvancedGroupsModelArray;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customeridentity.model.CustomerIdentityDetailsComparator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/customerIdentity/CustomerIdentitySearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 *//*
public class CustomerIdentitySearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -1417377971533088564L;
	private final static Logger logger = Logger.getLogger(CustomerIdentitySearchCtrl.class);

	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window  window_CustomerIdentitySearch;// autowired
	
	protected Textbox idCustCIF; 					// autowired
	protected Listbox sortOperator_idCustCIF; 		// autowired
	protected Textbox idType; 						// autowired
	protected Listbox sortOperator_idType; 			// autowired
	protected Textbox idIssuedBy; 					// autowired
	protected Listbox sortOperator_idIssuedBy; 		// autowired
	protected Textbox idRef; 						// autowired
	protected Listbox sortOperator_idRef; 			// autowired
	protected Textbox idIssueCountry; 				// autowired
	protected Listbox sortOperator_idIssueCountry; 	// autowired
	protected Datebox idIssuedOn;					// autowired
	protected Listbox sortOperator_idIssuedOn; 		// autowired
	protected Datebox idExpiresOn; 					// autowired
	protected Listbox sortOperator_idExpiresOn; 	// autowired
	protected Textbox idLocation; 					// autowired
	protected Listbox sortOperator_idLocation; 		// autowired
	protected Textbox recordStatus; 				// autowired
	protected Listbox recordType;					// autowired
	protected Listbox sortOperator_recordStatus; 	// autowired
	protected Listbox sortOperator_recordType; 		// autowired
	
	protected Label label_CustomerIdentitySearch_RecordStatus; 	// autowired
	protected Label label_CustomerIdentitySearch_RecordType; 	// autowired
	protected Label label_CustomerIdentitySearchResult; 		// autowired

	// not auto wired vars
	private transient CustomerIdentityListCtrl customerIdentityCtrl; // overhanded per param
	
	private transient CustomerIdentityService customerIdentityService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerIdentity");
	
	*//**
	 * constructor
	 *//*
	public CustomerIdentitySearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	*//**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerIdentity object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_CustomerIdentitySearch(Event event) throws Exception {
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

		if (args.containsKey("customerIdentityCtrl")) {
			this.customerIdentityCtrl = (CustomerIdentityListCtrl) args.get("customerIdentityCtrl");
			setCustomerIdentityCtrl(this.customerIdentityCtrl);
		} else {
			this.customerIdentityCtrl = null;
			setCustomerIdentityCtrl(this.customerIdentityCtrl);
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_idCustCIF.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_idCustCIF.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idType.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_idType.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idIssuedBy.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_idIssuedBy.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idRef.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_idRef.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idIssueCountry.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_idIssueCountry.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idIssuedOn.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_idIssuedOn.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idExpiresOn.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_idExpiresOn.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_idLocation.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_idLocation.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerIdentitySearch_RecordStatus.setVisible(false);
			this.label_CustomerIdentitySearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerIdentity> searchObj = (JdbcSearchObject<CustomerIdentity>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("idCustCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_idCustCIF, filter);
					this.idCustCIF.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("idType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_idType, filter);
					this.idType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("idIssuedBy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_idIssuedBy, filter);
					this.idIssuedBy.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("idRef")) {
					SearchOperators.restoreStringOperator(this.sortOperator_idRef, filter);
					this.idRef.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("idIssueCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_idIssueCountry, filter);
					this.idIssueCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("idIssuedOn")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_idIssuedOn, filter);
					this.idIssuedOn.setValue(DateUtility.getUtilDate(filter.getValue().toString(),PennantConstants.DBDateFormat));
			    } else if (filter.getProperty().equals("idExpiresOn")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_idExpiresOn, filter);
					this.idExpiresOn.setValue(DateUtility.getUtilDate(filter.getValue().toString(),PennantConstants.DBDateFormat));
			    } else if (filter.getProperty().equals("idLocation")) {
					SearchOperators.restoreStringOperator(this.sortOperator_idLocation, filter);
					this.idLocation.setValue(filter.getValue().toString());
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
		showCustomerIdentitySeekDialog();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 *//*
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * closes the dialog window
	 *//*
	private void doClose() {
		logger.debug("Entering");
		this.window_CustomerIdentitySearch.onClose();
		logger.debug("Leaving");
	}

	*//**
	 * Opens the SearchDialog window modal.
	 *//*
	private void showCustomerIdentitySeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerIdentitySearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 *//* 
	public void doSearch() {
		logger.debug("Entering");
		
		final JdbcSearchObject<CustomerIdentity> so = new JdbcSearchObject<CustomerIdentity>(CustomerIdentity.class);
		so.addTabelName("CustIdentities_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		if (StringUtils.isNotEmpty(this.idCustCIF.getValue())) {

			// get the search operator
			final Listitem item_IdCustCIF = this.sortOperator_idCustCIF.getSelectedItem();

			if (item_IdCustCIF != null) {
				final int searchOpId = ((SearchOperators) item_IdCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%" + this.idCustCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.idCustCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.idType.getValue())) {

			// get the search operator
			final Listitem item_IdType = this.sortOperator_idType.getSelectedItem();

			if (item_IdType != null) {
				final int searchOpId = ((SearchOperators) item_IdType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("idType", "%" + this.idType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idType", this.idType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.idIssuedBy.getValue())) {

			// get the search operator
			final Listitem item_IdIssuedBy = this.sortOperator_idIssuedBy.getSelectedItem();

			if (item_IdIssuedBy != null) {
				final int searchOpId = ((SearchOperators) item_IdIssuedBy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("idIssuedBy", "%" + this.idIssuedBy.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idIssuedBy", this.idIssuedBy.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.idRef.getValue())) {

			// get the search operator
			final Listitem item_IdRef = this.sortOperator_idRef.getSelectedItem();

			if (item_IdRef != null) {
				final int searchOpId = ((SearchOperators) item_IdRef.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("idRef", "%" + this.idRef.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idRef", this.idRef.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.idIssueCountry.getValue())) {

			// get the search operator
			final Listitem item_IdIssueCountry = this.sortOperator_idIssueCountry.getSelectedItem();

			if (item_IdIssueCountry != null) {
				final int searchOpId = ((SearchOperators) item_IdIssueCountry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("idIssueCountry", "%" + this.idIssueCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idIssueCountry", this.idIssueCountry.getValue(), searchOpId));
				}
			}
		}
		if (this.idIssuedOn.getValue() != null) {

			// get the search operator
			final Listitem item_IdIssuedOn = this.sortOperator_idIssuedOn.getSelectedItem();

			if (item_IdIssuedOn != null) {
				final int searchOpId = ((SearchOperators) item_IdIssuedOn.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idIssuedOn", DateUtility.formatUtilDate(
							this.idIssuedOn.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
		if (this.idExpiresOn.getValue() != null) {

			// get the search operator
			final Listitem item_IdExpiresOn = this.sortOperator_idExpiresOn.getSelectedItem();

			if (item_IdExpiresOn != null) {
				final int searchOpId = ((SearchOperators) item_IdExpiresOn.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idExpiresOn", DateUtility.formatUtilDate(
							this.idExpiresOn.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.idLocation.getValue())) {

			// get the search operator
			final Listitem item_IdLocation = this.sortOperator_idLocation.getSelectedItem();

			if (item_IdLocation != null) {
				final int searchOpId = ((SearchOperators) item_IdLocation.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("idLocation", "%" + this.idLocation.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("idLocation", this.idLocation.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();
	
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
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();
	
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
		this.customerIdentityCtrl.setSearchObj(so);

		// set the model to the listBox with the initial resultSet get by the DAO method.
		final SearchResult<CustomerIdentity> searchResult =this.customerIdentityCtrl.getPagedListService().getSRBySearchObject(so);
		this.customerIdentityCtrl.listBoxCustomerIdentity.setModel(new AdvancedGroupsModelArray(searchResult.getResult().toArray(),new CustomerIdentityDetailsComparator()));

		this.label_CustomerIdentitySearchResult.setValue(Labels.getLabel("label_CustomerIdentitySearchResult.value") + " "
				+ String.valueOf(searchResult.getTotalCount()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerIdentityService(CustomerIdentityService customerIdentityService) {
		this.customerIdentityService = customerIdentityService;
	}
	public CustomerIdentityService getCustomerIdentityService() {
		return this.customerIdentityService;
	}
	
	public CustomerIdentityListCtrl getCustomerIdentityCtrl() {
		return customerIdentityCtrl;
	}
	public void setCustomerIdentityCtrl(
			CustomerIdentityListCtrl customerIdentityCtrl) {
		this.customerIdentityCtrl = customerIdentityCtrl;
	}

}*/