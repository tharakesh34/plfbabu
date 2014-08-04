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
 * FileName    		:  SystemInternalAccountDefinitionSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.masters.systeminternalaccountdefinition;

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
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.service.masters.SystemInternalAccountDefinitionService;
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
 * This is the controller class for the /WEB-INF/pages/Account/SystemInternalAccountDefinition/SystemInternalAccountDefinitionSearch.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */

public class SystemInternalAccountDefinitionSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_SystemInternalAccountDefinitionSearch; // autowired
	
	protected Textbox sIACode; // autowired
	protected Listbox sortOperator_sIACode; // autowired
	protected Textbox sIAName; // autowired
	protected Listbox sortOperator_sIAName; // autowired
	protected Textbox sIAShortName; // autowired
	protected Listbox sortOperator_sIAShortName; // autowired
	protected Textbox sIAAcType; // autowired
	protected Listbox sortOperator_sIAAcType; // autowired
	protected Textbox sIANumber; // autowired
	protected Listbox sortOperator_sIANumber; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_SystemInternalAccountDefinitionSearch_RecordStatus; // autowired
	protected Label label_SystemInternalAccountDefinitionSearch_RecordType; // autowired
	protected Label label_SystemInternalAccountDefinitionSearchResult; // autowired

	// not auto wired vars
	private transient SystemInternalAccountDefinitionListCtrl systemInternalAccountDefinitionCtrl; // overhanded per param
	private transient SystemInternalAccountDefinitionService systemInternalAccountDefinitionService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SystemInternalAccountDefinition");
	
	/**
	 * constructor
	 */
	public SystemInternalAccountDefinitionSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SystemInternalAccountDefinitionSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("systemInternalAccountDefinitionCtrl")) {
			this.systemInternalAccountDefinitionCtrl = (SystemInternalAccountDefinitionListCtrl) args.get("systemInternalAccountDefinitionCtrl");
		} else {
			this.systemInternalAccountDefinitionCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_sIACode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sIACode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_sIAName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sIAName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_sIAShortName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sIAShortName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_sIAAcType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sIAAcType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_sIANumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sIANumber.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_SystemInternalAccountDefinitionSearch_RecordStatus.setVisible(false);
			this.label_SystemInternalAccountDefinitionSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<SystemInternalAccountDefinition> searchObj = (JdbcSearchObject<SystemInternalAccountDefinition>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("sIACode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_sIACode, filter);
					this.sIACode.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("sIAName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_sIAName, filter);
					this.sIAName.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("sIAShortName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_sIAShortName, filter);
					this.sIAShortName.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("sIAAcType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_sIAAcType, filter);
					this.sIAAcType.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("sIANumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_sIANumber, filter);
					this.sIANumber.setValue(filter.getValue().toString());

					
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
		showSystemInternalAccountDefinitionSeekDialog();
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
		this.window_SystemInternalAccountDefinitionSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSystemInternalAccountDefinitionSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_SystemInternalAccountDefinitionSearch.doModal();
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
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {

		final JdbcSearchObject<SystemInternalAccountDefinition> so = new JdbcSearchObject<SystemInternalAccountDefinition>(SystemInternalAccountDefinition.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("SystemInternalAccountDef_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("SystemInternalAccountDef_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.sIACode.getValue())) {

			// get the search operator
			final Listitem item_SIACode = this.sortOperator_sIACode.getSelectedItem();

			if (item_SIACode != null) {
				final int searchOpId = ((SearchOperators) item_SIACode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sIACode", "%" + this.sIACode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sIACode", this.sIACode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.sIAName.getValue())) {

			// get the search operator
			final Listitem item_SIAName = this.sortOperator_sIAName.getSelectedItem();

			if (item_SIAName != null) {
				final int searchOpId = ((SearchOperators) item_SIAName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sIAName", "%" + this.sIAName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sIAName", this.sIAName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.sIAShortName.getValue())) {

			// get the search operator
			final Listitem item_SIAShortName = this.sortOperator_sIAShortName.getSelectedItem();

			if (item_SIAShortName != null) {
				final int searchOpId = ((SearchOperators) item_SIAShortName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sIAShortName", "%" + this.sIAShortName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sIAShortName", this.sIAShortName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.sIAAcType.getValue())) {

			// get the search operator
			final Listitem item_SIAAcType = this.sortOperator_sIAAcType.getSelectedItem();

			if (item_SIAAcType != null) {
				final int searchOpId = ((SearchOperators) item_SIAAcType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sIAAcType", "%" + this.sIAAcType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sIAAcType", this.sIAAcType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.sIANumber.getValue())) {

			// get the search operator
			final Listitem item_SIANumber = this.sortOperator_sIANumber.getSelectedItem();

			if (item_SIANumber != null) {
				final int searchOpId = ((SearchOperators) item_SIANumber.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sIANumber", "%" + this.sIANumber.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sIANumber", this.sIANumber.getValue(), searchOpId));
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
		// Defualt Sort on the table
		so.addSort("SIACode", false);

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
		this.systemInternalAccountDefinitionCtrl.setSearchObj(so);

		final Listbox listBox = this.systemInternalAccountDefinitionCtrl.listBoxSystemInternalAccountDefinition;
		final Paging paging = this.systemInternalAccountDefinitionCtrl.pagingSystemInternalAccountDefinitionList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<SystemInternalAccountDefinition>) listBox.getModel()).init(so, listBox, paging);
		this.systemInternalAccountDefinitionCtrl.setSearchObj(so);

		this.label_SystemInternalAccountDefinitionSearchResult.setValue(Labels.getLabel("label_SystemInternalAccountDefinitionSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSystemInternalAccountDefinitionService(SystemInternalAccountDefinitionService systemInternalAccountDefinitionService) {
		this.systemInternalAccountDefinitionService = systemInternalAccountDefinitionService;
	}

	public SystemInternalAccountDefinitionService getSystemInternalAccountDefinitionService() {
		return this.systemInternalAccountDefinitionService;
	}
}