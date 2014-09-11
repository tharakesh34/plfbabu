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
 * FileName    		:  ScoringGroupSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.scoringgroup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
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
 * /WEB-INF/pages/RulesFactory/ScoringGroup/scoringGroupSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScoringGroupSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 270252267299143666L;
	private final static Logger logger = Logger.getLogger(ScoringGroupSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ScoringGroupSearch;            // autoWired

	protected Longbox scoreGroupId;                        // autoWired
	protected Listbox sortOperator_scoreGroupId;           // autoWired
	protected Textbox scoreGroupCode;                      // autoWired
	protected Listbox sortOperator_scoreGroupCode;         // autoWired
	protected Textbox scoreGroupName;                      // autoWired
	protected Listbox sortOperator_scoreGroupName;         // autoWired
	protected Intbox  minScore;                            // autoWired
	protected Listbox sortOperator_minScore;               // autoWired
	protected Checkbox isoverride;                         // autoWired
	protected Listbox sortOperator_isoverride;             // autoWired
	protected Intbox  overrideScore;                       // autoWired
	protected Listbox sortOperator_overrideScore;          // autoWired
	protected Textbox recordStatus;                        // autoWired
	protected Listbox recordType;	                       // autoWired
	protected Listbox sortOperator_recordStatus;           // autoWired
	protected Listbox sortOperator_recordType;             // autoWired

	protected Label label_ScoringGroupSearch_RecordStatus; // autoWired
	protected Label label_ScoringGroupSearch_RecordType;   // autoWired
	protected Label label_ScoringGroupSearchResult;        // autoWired

	// not auto wired variables
	private transient ScoringGroupListCtrl scoringGroupCtrl; // over handed per parameters
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ScoringGroup");

	/**
	 * constructor
	 */
	public ScoringGroupSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringGroup object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ScoringGroupSearch(Event event) throws Exception {
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

		if (args.containsKey("scoringGroupCtrl")) {
			this.scoringGroupCtrl = (ScoringGroupListCtrl) args.get("scoringGroupCtrl");
		} else {
			this.scoringGroupCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_scoreGroupId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_scoreGroupId.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_scoreGroupCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_scoreGroupCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_scoreGroupName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_scoreGroupName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_minScore.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_minScore.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_isoverride.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isoverride.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_overrideScore.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_overrideScore.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_ScoringGroupSearch_RecordStatus.setVisible(false);
			this.label_ScoringGroupSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<ScoringGroup> searchObj = (JdbcSearchObject<ScoringGroup>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("scoreGroupCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_scoreGroupCode, filter);
					this.scoreGroupCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("scoreGroupName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_scoreGroupName, filter);
					this.scoreGroupName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("minScore")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_minScore, filter);
					this.minScore.setText(filter.getValue().toString());
				} else if (filter.getProperty().equals("isoverride")) {
					SearchOperators.restoreStringOperator(this.sortOperator_isoverride, filter);
					this.isoverride.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("overrideScore")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_overrideScore, filter);
					this.overrideScore.setText(filter.getValue().toString());
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
		showScoringGroupSeekDialog();
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
		this.window_ScoringGroupSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showScoringGroupSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_ScoringGroupSearch.doModal();
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

		final JdbcSearchObject<ScoringGroup> so = new JdbcSearchObject<ScoringGroup>(ScoringGroup.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("RMTScoringGroup_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("RMTScoringGroup_AView");
		}

		if (StringUtils.isNotEmpty(this.scoreGroupCode.getValue())) {

			// get the search operator
			final Listitem item_ScoreGroupCode = this.sortOperator_scoreGroupCode.getSelectedItem();

			if (item_ScoreGroupCode != null) {
				final int searchOpId = ((SearchOperators) item_ScoreGroupCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("scoreGroupCode", "%" + this.scoreGroupCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("scoreGroupCode", this.scoreGroupCode.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.scoreGroupName.getValue())) {

			// get the search operator
			final Listitem item_ScoreGroupName = this.sortOperator_scoreGroupName.getSelectedItem();

			if (item_ScoreGroupName != null) {
				final int searchOpId = ((SearchOperators) item_ScoreGroupName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("scoreGroupName", "%" + this.scoreGroupName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("scoreGroupName", this.scoreGroupName.getValue(), searchOpId));
				}
			}
		}

		if (this.minScore.getValue()!=null) {	  
			final Listitem item_MinScore = this.sortOperator_minScore.getSelectedItem();
			if (item_MinScore != null) {
				final int searchOpId = ((SearchOperators) item_MinScore.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.minScore.getValue()!=null ){
						so.addFilter(new Filter("minScore",1, searchOpId));
					}else{
						so.addFilter(new Filter("minScore",0, searchOpId));	
					}
				}
			}
		}	

		// get the search operator
		final Listitem item_Isoverride = this.sortOperator_isoverride.getSelectedItem();

		if (item_Isoverride != null) {
			final int searchOpId = ((SearchOperators) item_Isoverride.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.isoverride.isChecked()){
					so.addFilter(new Filter("isoverride",1, searchOpId));
				}else{
					so.addFilter(new Filter("isoverride",0, searchOpId));	
				}
			}
		}

		if (this.overrideScore.getValue()!=null) {	  
			final Listitem item_OverrideScore = this.sortOperator_overrideScore.getSelectedItem();
			if (item_OverrideScore != null) {
				final int searchOpId = ((SearchOperators) item_OverrideScore.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.overrideScore.getValue()!=null){
						so.addFilter(new Filter("overrideScore",1, searchOpId));
					}else{
						so.addFilter(new Filter("overrideScore",0, searchOpId));	
					}
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
		so.addSort("ScoreGroupId", false);

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
		this.scoringGroupCtrl.setSearchObj(so);
		final Listbox listBox = this.scoringGroupCtrl.listBoxScoringGroup;
		final Paging paging = this.scoringGroupCtrl.pagingScoringGroupList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<ScoringGroup>) listBox.getModel()).init(so, listBox, paging);
		this.label_ScoringGroupSearchResult.setValue(Labels.getLabel("label_ScoringGroupSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}