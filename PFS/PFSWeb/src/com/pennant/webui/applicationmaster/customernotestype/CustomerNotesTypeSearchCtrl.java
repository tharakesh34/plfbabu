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
 * FileName    		:  CustomerNotesTypeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.customernotestype;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.service.applicationmaster.CustomerNotesTypeService;
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
 * /WEB-INF/pages/ApplicationMaster/CustomerNotesType/CustomerNotesTypeSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */

public class CustomerNotesTypeSearchCtrl extends GFCBaseCtrl implements
Serializable {

	private static final long serialVersionUID = 4580053594518153591L;

	private final static Logger logger = Logger.getLogger(CustomerNotesTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CustomerNotesTypeSearch; 		// autoWired

	protected Textbox 	custNotesTypeCode; 						// autoWired
	protected Listbox 	sortOperator_custNotesTypeCode; 		// autoWired
	protected Textbox 	custNotesTypeDesc; 						// autoWired
	protected Listbox 	sortOperator_custNotesTypeDesc; 		// autoWired
	protected Checkbox 	custNotesTypeIsPerminent;	 			// autoWired
	protected Listbox 	sortOperator_custNotesTypeIsPerminent; 	// autoWired
	protected Checkbox 	custNotesTypeIsActive;	 				// autoWired
	protected Listbox 	sortOperator_custNotesTypeIsActive; 	// autoWired
	protected Textbox 	custNotesTypeArchiveFrq; 				// autoWired
	protected Listbox 	sortOperator_custNotesTypeArchiveFrq; 	// autoWired
	protected Textbox 	recordStatus; 							// autoWired
	protected Listbox 	recordType; 							// autoWired
	protected Listbox 	sortOperator_recordStatus; 				// autoWired
	protected Listbox 	sortOperator_recordType; 				// autoWired

	protected Label label_CustomerNotesTypeSearch_RecordStatus; // autoWired
	protected Label label_CustomerNotesTypeSearch_RecordType; 	// autoWired
	protected Label label_CustomerNotesTypeSearchResult; 		// autoWired

	// not autoWired variables
	private transient CustomerNotesTypeListCtrl customerNotesTypeCtrl; // over handed per parameters
	private transient CustomerNotesTypeService customerNotesTypeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerNotesType");

	/**
	 * constructor
	 */
	public CustomerNotesTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerNotesType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerNotesTypeSearch(Event event) throws Exception {
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

		if (args.containsKey("customerNotesTypeCtrl")) {
			this.customerNotesTypeCtrl = (CustomerNotesTypeListCtrl) args.get("customerNotesTypeCtrl");
		} else {
			this.customerNotesTypeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custNotesTypeCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custNotesTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custNotesTypeDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custNotesTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custNotesTypeIsPerminent.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custNotesTypeIsPerminent.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custNotesTypeIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custNotesTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custNotesTypeArchiveFrq.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custNotesTypeArchiveFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerNotesTypeSearch_RecordStatus.setVisible(false);
			this.label_CustomerNotesTypeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerNotesType> searchObj = (JdbcSearchObject<CustomerNotesType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("custNotesTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custNotesTypeCode, filter);
					this.custNotesTypeCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custNotesTypeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custNotesTypeDesc, filter);
					this.custNotesTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custNotesTypeIsPerminent")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custNotesTypeIsPerminent, filter);
					//this.custNotesTypeIsPerminent.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.custNotesTypeIsPerminent.setChecked(true);
					}else{
						this.custNotesTypeIsPerminent.setChecked(false);
					}
				} else if (filter.getProperty().equals("custNotesTypeIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custNotesTypeIsActive, filter);
					//this.custNotesTypeIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.custNotesTypeIsActive.setChecked(true);
					}else{
						this.custNotesTypeIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("custNotesTypeArchiveFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custNotesTypeArchiveFrq, filter);
					this.custNotesTypeArchiveFrq.setValue(filter.getValue().toString());
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
		showCustomerNotesTypeSeekDialog();
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
		this.window_CustomerNotesTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerNotesTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerNotesTypeSearch.doModal();
		} catch (final Exception e) {
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

		final JdbcSearchObject<CustomerNotesType> so = new JdbcSearchObject<CustomerNotesType>(CustomerNotesType.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTCustNotesTypes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTCustNotesTypes_AView");
		}

		if (StringUtils.isNotEmpty(this.custNotesTypeCode.getValue())) {

			// get the search operator
			final Listitem item_CustNotesTypeCode = this.sortOperator_custNotesTypeCode.getSelectedItem();

			if (item_CustNotesTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_CustNotesTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custNotesTypeCode", "%" + this.custNotesTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custNotesTypeCode", this.custNotesTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custNotesTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_CustNotesTypeDesc = this.sortOperator_custNotesTypeDesc.getSelectedItem();

			if (item_CustNotesTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_CustNotesTypeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custNotesTypeDesc", "%" + this.custNotesTypeDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custNotesTypeDesc", this.custNotesTypeDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_CustNotesTypeIsPerminent = this.sortOperator_custNotesTypeIsPerminent.getSelectedItem();

		if (item_CustNotesTypeIsPerminent != null) {
			final int searchOpId = ((SearchOperators) item_CustNotesTypeIsPerminent.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.custNotesTypeIsPerminent.isChecked()) {
					so.addFilter(new Filter("custNotesTypeIsPerminent", 1, searchOpId));
				} else {
					so.addFilter(new Filter("custNotesTypeIsPerminent", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_CustNotesTypeIsActive = this.sortOperator_custNotesTypeIsActive.getSelectedItem();

		if (item_CustNotesTypeIsActive != null) {
			final int searchOpId = ((SearchOperators) item_CustNotesTypeIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.custNotesTypeIsActive.isChecked()) {
					so.addFilter(new Filter("custNotesTypeIsActive", 1,	searchOpId));
				} else {
					so.addFilter(new Filter("custNotesTypeIsActive", 0,	searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custNotesTypeArchiveFrq.getValue())) {

			// get the search operator
			final Listitem item_CustNotesTypeArchiveFrq = this.sortOperator_custNotesTypeArchiveFrq.getSelectedItem();

			if (item_CustNotesTypeArchiveFrq != null) {
				final int searchOpId = ((SearchOperators) item_CustNotesTypeArchiveFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custNotesTypeArchiveFrq", "%" 
							+ this.custNotesTypeArchiveFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custNotesTypeArchiveFrq", this.custNotesTypeArchiveFrq.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"	+ this.recordStatus.getValue().toUpperCase() + "%",	searchOpId));
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
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("CustNotesTypeCode", false);

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
		this.customerNotesTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.customerNotesTypeCtrl.listBoxCustomerNotesType;
		final Paging paging = this.customerNotesTypeCtrl.pagingCustomerNotesTypeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<CustomerNotesType>) listBox.getModel()).init(so,	listBox, paging);
		this.customerNotesTypeCtrl.setSearchObj(so);

		this.label_CustomerNotesTypeSearchResult.setValue(Labels.getLabel(
		"label_CustomerNotesTypeSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerNotesTypeService(CustomerNotesTypeService customerNotesTypeService) {
		this.customerNotesTypeService = customerNotesTypeService;
	}
	public CustomerNotesTypeService getCustomerNotesTypeService() {
		return this.customerNotesTypeService;
	}
}