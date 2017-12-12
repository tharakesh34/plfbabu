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
 * FileName    		:  ScoringTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-11-2011    														*
 *                                                                  						*
 * Modified Date    :  08-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.scoringtype;

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
import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/ScoringType/ScoringTypeSearch.zul file.
 */
public class ScoringTypeSearchCtrl extends GFCBaseCtrl<ScoringType>  {
	private static final long serialVersionUID = 8749922359221017261L;
	private static final Logger logger = Logger.getLogger(ScoringTypeSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_ScoringTypeSearch; 		

	protected Textbox 	scoType; 						
	protected Listbox 	sortOperator_scoType; 			
	protected Textbox 	scoDesc; 						
	protected Listbox 	sortOperator_scoDesc; 			
	protected Textbox 	recordStatus; 					
	protected Listbox 	recordType;						
	protected Listbox 	sortOperator_recordStatus; 		
	protected Listbox 	sortOperator_recordType; 		

	protected Label label_ScoringTypeSearch_RecordStatus;	 	
	protected Label label_ScoringTypeSearch_RecordType; 		
	protected Label label_ScoringTypeSearchResult; 				

	// not auto wired variables
	private transient ScoringTypeListCtrl scoringTypeCtrl; // overHanded per parameter
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ScoringType");

	/**
	 * Default Constructor
	 */
	public ScoringTypeSearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringTypeSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringTypeSearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		if (arguments.containsKey("scoringTypeCtrl")) {
			this.scoringTypeCtrl = (ScoringTypeListCtrl) arguments.get("scoringTypeCtrl");
		} else {
			this.scoringTypeCtrl = null;
		}

		// DropDown ListBox

		this.sortOperator_scoType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_scoType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_scoDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_scoDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_ScoringTypeSearch_RecordStatus.setVisible(false);
			this.label_ScoringTypeSearch_RecordType.setVisible(false);
		}

		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<ScoringType> searchObj = (JdbcSearchObject<ScoringType>) arguments
									.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if ("scoType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_scoType, filter);
					this.scoType.setValue(filter.getValue().toString());
				} else if ("scoDesc".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_scoDesc, filter);
					this.scoDesc.setValue(filter.getValue().toString());
				} else if ("recordStatus".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if ("recordType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showScoringTypeSeekDialog();
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
	private void showScoringTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_ScoringTypeSearch.doModal();
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
		final JdbcSearchObject<ScoringType> so = new JdbcSearchObject<ScoringType>(ScoringType.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTScoringType_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("BMTScoringType_AView");
		}

		if (StringUtils.isNotEmpty(this.scoType.getValue())) {

			// get the search operator
			final Listitem listItemScoType = this.sortOperator_scoType.getSelectedItem();

			if (listItemScoType != null) {
				final int searchOpId = ((SearchOperators) listItemScoType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("scoType", "%" + this.scoType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("scoType", this.scoType.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.scoDesc.getValue())) {

			// get the search operator
			final Listitem listItemScoDesc = this.sortOperator_scoDesc.getSelectedItem();

			if (listItemScoDesc != null) {
				final int searchOpId = ((SearchOperators) listItemScoDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("scoDesc", "%" + this.scoDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("scoDesc", this.scoDesc.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem listItemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (listItemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) listItemRecordStatus.getAttribute("data")).getSearchOperatorId();

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
		so.addSort("ScoType", false);

		// store the searchObject for reReading
		this.scoringTypeCtrl.setSearchObj(so);
		final Listbox listBox = this.scoringTypeCtrl.listBoxScoringType;
		final Paging paging = this.scoringTypeCtrl.pagingScoringTypeList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<ScoringType>) listBox.getModel()).init(so, listBox, paging);

		this.label_ScoringTypeSearchResult.setValue(Labels.getLabel("label_ScoringTypeSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}