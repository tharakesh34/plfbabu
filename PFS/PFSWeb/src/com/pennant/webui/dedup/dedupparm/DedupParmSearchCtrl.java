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
 * FileName    		:  DedupParmSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.dedup.dedupparm;

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
import com.pennant.backend.model.dedup.DedupParm;
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
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/DedupParm/DedupParmSearch.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DedupParmSearchCtrl extends GFCBaseCtrl implements Serializable {
	
	private static final long serialVersionUID = -120763179812627999L;
	private final static Logger logger = Logger.getLogger(DedupParmSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DedupParmSearch; 	// autoWired
	
	protected Textbox queryCode; 				// autoWired
	protected Listbox sortOperator_queryCode; 	// autoWired
	protected Textbox queryModule; 				// autoWired
	protected Listbox sortOperator_queryModule; // autoWired
	protected Textbox sQLQuery; 				// autoWired
	protected Listbox sortOperator_sQLQuery; 	// autoWired
	protected Textbox recordStatus; 			// autoWired
	protected Listbox recordType;				// autoWired
	protected Listbox sortOperator_recordStatus;// autoWired
	protected Listbox sortOperator_recordType; 	// autoWired
	
	protected Label label_DedupParmSearch_RecordStatus; // autoWired
	protected Label label_DedupParmSearch_RecordType; 	// autoWired
	protected Label label_DedupParmSearchResult; 		// autoWired

	// not auto wired vars
	private transient DedupParmListCtrl dedupParmCtrl; // over handed per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DedupParm");
	
	/**
	 * constructor
	 */
	public DedupParmSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected DedupParm object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DedupParmSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("dedupParmCtrl")) {
			this.dedupParmCtrl = (DedupParmListCtrl) args.get("dedupParmCtrl");
		} else {
			this.dedupParmCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_queryCode.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_queryCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_queryModule.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_queryModule.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_sQLQuery.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_sQLQuery.setItemRenderer(
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
			this.label_DedupParmSearch_RecordStatus.setVisible(false);
			this.label_DedupParmSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<DedupParm> searchObj = (JdbcSearchObject<DedupParm>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("queryCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_queryCode, filter);
					this.queryCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("queryModule")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_queryModule, filter);
					this.queryModule.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("sQLQuery")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_sQLQuery, filter);
					this.sQLQuery.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(
								filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
	
				}
			}
			
		}
		showDedupParmSeekDialog();
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
		logger.debug("Entering ");
		this.window_DedupParmSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showDedupParmSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_DedupParmSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		final JdbcSearchObject<DedupParm> so = new JdbcSearchObject<DedupParm>(
				DedupParm.class);
		so.addTabelName("DedupParams_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());	
		}
		
		if (StringUtils.isNotEmpty(this.queryCode.getValue())) {

			// get the search operator
			final Listitem item_QueryCode = this.sortOperator_queryCode
					.getSelectedItem();

			if (item_QueryCode != null) {
				final int searchOpId = ((SearchOperators) item_QueryCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("queryCode", "%"
							+ this.queryCode.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("queryCode", this.queryCode
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.queryModule.getValue())) {

			// get the search operator
			final Listitem item_QueryModule = this.sortOperator_queryModule
					.getSelectedItem();

			if (item_QueryModule != null) {
				final int searchOpId = ((SearchOperators) item_QueryModule
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("queryModule", "%"
							+ this.queryModule.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("queryModule", this.queryModule
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.sQLQuery.getValue())) {

			// get the search operator
			final Listitem item_SQLQuery = this.sortOperator_sQLQuery.getSelectedItem();

			if (item_SQLQuery != null) {
				final int searchOpId = ((SearchOperators) item_SQLQuery
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sQLQuery", "%"
							+ this.sQLQuery.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sQLQuery", this.sQLQuery
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus
						.getAttribute("data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus
							.getValue(), searchOpId));
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
				final int searchOpId = ((SearchOperators) item_RecordType
						.getAttribute("data")).getSearchOperatorId();
	
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
		so.addSort("QueryCode", false);

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
		this.dedupParmCtrl.setSearchObj(so);

		final Listbox listBox = this.dedupParmCtrl.listBoxDedupParm;
		final Paging paging = this.dedupParmCtrl.pagingDedupParmList;
		
		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<DedupParm>) listBox.getModel()).init(so, listBox, paging);

		this.label_DedupParmSearchResult.setValue(Labels.getLabel("label_DedupParmSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

}