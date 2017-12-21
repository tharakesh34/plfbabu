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
 * FileName    		:  AccountEngineEventSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.accountengineevent;

import java.util.List;

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
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.service.bmtmasters.AccountEngineEventService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class AccountEngineEventSearchCtrl extends GFCBaseCtrl<AccountEngineEvent>  {
	private static final long serialVersionUID = -5457162932741884160L;
	private static final Logger logger = Logger.getLogger(AccountEngineEventSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  window_AccountEngineEventSearch; 	
	protected Textbox aEEventCode; 						
	protected Listbox sortOperator_aEEventCode; 		
	protected Textbox aEEventCodeDesc; 					
	protected Listbox sortOperator_aEEventCodeDesc; 	
	protected Textbox recordStatus; 					
	protected Listbox recordType;						
	protected Listbox sortOperator_recordStatus; 		
	protected Listbox sortOperator_recordType; 			

	protected Label label_AccountEngineEventSearch_RecordStatus; 	
	protected Label label_AccountEngineEventSearch_RecordType; 		
	protected Label label_AccountEngineEventSearchResult; 			

	// not auto wired Var's
	private transient AccountEngineEventListCtrl accountEngineEventCtrl; // overHanded per parameter
	private transient AccountEngineEventService accountEngineEventService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountEngineEvent");

	/**
	 * constructor
	 */
	public AccountEngineEventSearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineEvent object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountEngineEventSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccountEngineEventSearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		if (arguments.containsKey("accountEngineEventCtrl")) {
			this.accountEngineEventCtrl = (AccountEngineEventListCtrl)arguments.get("accountEngineEventCtrl");
		} else {
			this.accountEngineEventCtrl = null;
		}

		// DropDown ListBox

		this.sortOperator_aEEventCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aEEventCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_aEEventCodeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aEEventCodeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_AccountEngineEventSearch_RecordStatus.setVisible(false);
			this.label_AccountEngineEventSearch_RecordType.setVisible(false);
		}

		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<AccountEngineEvent> searchObj = (JdbcSearchObject<AccountEngineEvent>) arguments.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if ("aEEventCode".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_aEEventCode, filter);
					this.aEEventCode.setValue(filter.getValue().toString());
				} else if ("aEEventCodeDesc".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_aEEventCodeDesc, filter);
					this.aEEventCodeDesc.setValue(filter.getValue().toString());
				} else if ("recordStatus".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if ("recordType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showAccountEngineEventSeekDialog();
		logger.debug("Leaving" + event.toString());
	}

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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showAccountEngineEventSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_AccountEngineEventSearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

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
		final JdbcSearchObject<AccountEngineEvent> so = new JdbcSearchObject<AccountEngineEvent>(AccountEngineEvent.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTAEEvents_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		} else {
			so.addTabelName("BMTAEEvents_AView");
		}

		if (StringUtils.isNotEmpty(this.aEEventCode.getValue())) {

			// get the search operator
			final Listitem listItemAEEventCode = this.sortOperator_aEEventCode.getSelectedItem();

			if (listItemAEEventCode != null) {
				final int searchOpId = ((SearchOperators) listItemAEEventCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aEEventCode", "%"	+ this.aEEventCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aEEventCode", this.aEEventCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aEEventCodeDesc.getValue())) {

			// get the search operator
			final Listitem listItemAEEventCodeDesc = this.sortOperator_aEEventCodeDesc.getSelectedItem();

			if (listItemAEEventCodeDesc != null) {
				final int searchOpId = ((SearchOperators) listItemAEEventCodeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aEEventCodeDesc", "%" + this.aEEventCodeDesc.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aEEventCodeDesc", this.aEEventCodeDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem listItemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (listItemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) listItemRecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
			final Listitem listItemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (listItemRecordType!= null) {
				final int searchOpId = ((SearchOperators) listItemRecordType.getAttribute("data")).getSearchOperatorId();

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
		so.addSort("AEEventCode", false);

		// store the searchObject for reReading
		this.accountEngineEventCtrl.setSearchObj(so);

		final Listbox listBox = this.accountEngineEventCtrl.listBoxAccountEngineEvent;
		final Paging paging = this.accountEngineEventCtrl.pagingAccountEngineEventList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<AccountEngineEvent>) listBox.getModel()).init(so, listBox, paging);
		this.accountEngineEventCtrl.setSearchObj(so);

		this.label_AccountEngineEventSearchResult.setValue(Labels.getLabel(
		"label_AccountEngineEventSearchResult.value")+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAccountEngineEventService(AccountEngineEventService accountEngineEventService) {
		this.accountEngineEventService = accountEngineEventService;
	}
	public AccountEngineEventService getAccountEngineEventService() {
		return this.accountEngineEventService;
	}
}