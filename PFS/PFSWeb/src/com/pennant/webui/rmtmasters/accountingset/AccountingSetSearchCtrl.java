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
 * FileName    		:  AccountingSetSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountingset;

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
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class AccountingSetSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -479456324219161718L;
	private final static Logger logger = Logger.getLogger(AccountingSetSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_AccountingSetSearch; 			// autowired
	
	protected Textbox eventCode; 							// autowired
	protected Listbox sortOperator_eventCode; 				// autowired
	protected Textbox accountSetCode; 						// autowired
	protected Listbox sortOperator_accountSetCode; 			// autowired
	protected Textbox accountSetCodeName; 					// autowired
	protected Listbox sortOperator_accountSetCodeName; 		// autowired
	protected Textbox recordStatus; 						// autowired
	protected Listbox recordType;							// autowired
	protected Listbox sortOperator_recordStatus; 			// autowired
	protected Listbox sortOperator_recordType; 				// autowired
	
	protected Label label_AccountingSetSearch_RecordStatus; // autowired
	protected Label label_AccountingSetSearch_RecordType; 	// autowired
	protected Label label_AccountingSetSearchResult; 		// autowired

	// not auto wired vars
	private transient AccountingSetListCtrl accountingSetCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("AccountingSet");
	
	/**
	 * Default Constructor
	 */
	public AccountingSetSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountingSet object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AccountingSetSearch(Event event) throws Exception {
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

		if (args.containsKey("accountingSetCtrl")) {
			this.accountingSetCtrl = (AccountingSetListCtrl) args.get("accountingSetCtrl");
		} else {
			this.accountingSetCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_eventCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eventCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_accountSetCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_accountSetCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_accountSetCodeName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_accountSetCodeName.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_AccountingSetSearch_RecordStatus.setVisible(false);
			this.label_AccountingSetSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<AccountingSet> searchObj = (JdbcSearchObject<AccountingSet>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("eventCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eventCode, filter);
					this.eventCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("accountSetCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_accountSetCode, filter);
					this.accountSetCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("accountSetCodeName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_accountSetCodeName, filter);
					this.accountSetCodeName.setValue(filter.getValue().toString());
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
		showAccountingSetSeekDialog();
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
		this.window_AccountingSetSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showAccountingSetSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_AccountingSetSearch.doModal();
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
		
		final JdbcSearchObject<AccountingSet> so = new JdbcSearchObject<AccountingSet>(AccountingSet.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("RMTAccountingSet_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("RMTAccountingSet_AView");
		}
		
		if (StringUtils.isNotEmpty(this.eventCode.getValue())) {

			// get the search operator
			final Listitem item_EventCode = this.sortOperator_eventCode.getSelectedItem();

			if (item_EventCode != null) {
				final int searchOpId = ((SearchOperators) item_EventCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eventCode", "%" + this.eventCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eventCode", this.eventCode.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.accountSetCode.getValue())) {

			// get the search operator
			final Listitem item_AccountSetCode = this.sortOperator_accountSetCode.getSelectedItem();

			if (item_AccountSetCode != null) {
				final int searchOpId = ((SearchOperators) item_AccountSetCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("accountSetCode", "%" + this.accountSetCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("accountSetCode", this.accountSetCode.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.accountSetCodeName.getValue())) {

			// get the search operator
			final Listitem item_AccountSetCodeName = this.sortOperator_accountSetCodeName.getSelectedItem();

			if (item_AccountSetCodeName != null) {
				final int searchOpId = ((SearchOperators) item_AccountSetCodeName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("accountSetCodeName", "%" + 
							this.accountSetCodeName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("accountSetCodeName", this.accountSetCodeName.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();
	
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
		so.addSort("AccountSetid", false);

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
		this.accountingSetCtrl.setSearchObj(so);

		final Listbox listBox = this.accountingSetCtrl.listBoxAccountingSet;
		final Paging paging = this.accountingSetCtrl.pagingAccountingSetList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<AccountingSet>) listBox.getModel()).init(so, listBox, paging);
		this.label_AccountingSetSearchResult.setValue(Labels.getLabel("label_AccountingSetSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}