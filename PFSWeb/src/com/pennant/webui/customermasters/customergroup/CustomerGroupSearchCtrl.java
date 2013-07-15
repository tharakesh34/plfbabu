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
 * FileName    		:  CustomerGroupSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customergroup;

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
import com.pennant.backend.model.customermasters.CustomerGroup;
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
 * /WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerGroupSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 568045930991229490L;

	private final static Logger logger = Logger.getLogger(CustomerGroupSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CustomerGroupSearch; 			// autoWired

	protected Textbox	custGrpCode; 							// autoWired
	protected Listbox 	sortOperator_custGrpCode; 				// autoWired
	protected Textbox 	custGrpDesc;							// autoWired
	protected Listbox 	sortOperator_custGrpDesc;				// autoWired
	protected Checkbox 	custGrpIsActive; 						// autoWired
	protected Listbox 	sortOperator_custGrpIsActive; 			// autoWired
	protected Textbox 	recordStatus; 							// autoWired
	protected Listbox 	recordType;								// autoWired
	protected Listbox 	sortOperator_recordStatus; 				// autoWired
	protected Listbox 	sortOperator_recordType; 				// autoWired

	protected Label label_CustomerGroupSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerGroupSearch_RecordType; 		// autoWired
	protected Label label_CustomerGroupSearchResult; 			// autoWired

	// not auto wired variables
	private transient CustomerGroupListCtrl customerGroupCtrl; // overHanded per parameter
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerGroup");

	/**
	 * constructor
	 */
	public CustomerGroupSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerGroup object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerGroupSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerGroupCtrl")) {
			this.customerGroupCtrl = (CustomerGroupListCtrl) args.get("customerGroupCtrl");
		} else {
			this.customerGroupCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custGrpCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custGrpCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custGrpDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custGrpDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_custGrpIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custGrpIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CustomerGroupSearch_RecordStatus.setVisible(false);
			this.label_CustomerGroupSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerGroup> searchObj = (JdbcSearchObject<CustomerGroup>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("custGrpCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custGrpCode, filter);
					this.custGrpCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custGrpDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custGrpDesc, filter);
					this.custGrpDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("custGrpIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custGrpIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.custGrpIsActive.setChecked(true);
					}else{
						this.custGrpIsActive.setChecked(false);
					}
				}else if (filter.getProperty().equals("recordStatus")) {
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
		showCustomerGroupSeekDialog();
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
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_CustomerGroupSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerGroupSeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_CustomerGroupSearch.doModal();
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
	 * 1. Checks for each textGox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerGroup> so = new JdbcSearchObject<CustomerGroup>(CustomerGroup.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("CustomerGroups_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("CustomerGroups_AView");
		}

		if (StringUtils.isNotEmpty(this.custGrpCode.getValue())) {

			// get the search operator
			final Listitem item_CustGrpCode = this.sortOperator_custGrpCode.getSelectedItem();

			if (item_CustGrpCode != null) {
				final int searchOpId = ((SearchOperators) item_CustGrpCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custGrpCode", "%" + this.custGrpCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custGrpCode", this.custGrpCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custGrpDesc.getValue())) {

			// get the search operator
			final Listitem item_CustGrpDesc = this.sortOperator_custGrpDesc.getSelectedItem();

			if (item_CustGrpDesc != null) {
				final int searchOpId = ((SearchOperators) item_CustGrpDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custGrpDesc", "%" + this.custGrpDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custGrpDesc", this.custGrpDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_CustGrpIsActive = this.sortOperator_custGrpIsActive.getSelectedItem();

		if (item_CustGrpIsActive != null) {
			final int searchOpId = ((SearchOperators) item_CustGrpIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.custGrpIsActive.isChecked()){
					so.addFilter(new Filter("custGrpIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("custGrpIsActive",0, searchOpId));	
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
		so.addSort("CustGrpID", false);

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
		this.customerGroupCtrl.setSearchObj(so);

		final Listbox listBox = this.customerGroupCtrl.listBoxCustomerGroup;
		final Paging paging = this.customerGroupCtrl.pagingCustomerGroupList;
		this.customerGroupCtrl.pagingCustomerGroupList.getPageSize();

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<CustomerGroup>) listBox.getModel()).init(so, listBox, paging);
		this.customerGroupCtrl.setSearchObj(so);

		this.label_CustomerGroupSearchResult.setValue(Labels.getLabel("label_CustomerGroupSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}