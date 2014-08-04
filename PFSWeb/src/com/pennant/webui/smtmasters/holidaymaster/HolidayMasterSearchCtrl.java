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
 * FileName    		:  HolidayMasterSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.smtmasters.holidaymaster;

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
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterSearchDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class HolidayMasterSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4868537812607319128L;

	private final static Logger logger = Logger.getLogger(HolidayMasterSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_HolidayMasterSearch; // autowired
	
	protected Textbox holidayCode; 				// autowired
	protected Listbox sortOperator_holidayCode; // autowired
	protected Textbox holidayYear; 				// autowired
	protected Listbox sortOperator_holidayYear; // autowired
	protected Textbox holidayType; 				// autowired
	protected Listbox sortOperator_holidayType; // autowired
	
	protected Label label_HolidayMasterSearchResult; 		// autowired

	// not auto wired vars
	private transient HolidayMasterListCtrl holidayMasterCtrl; // overhanded per param
	private transient HolidayMasterService holidayMasterService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("HolidayMaster");
	
	/**
	 * constructor
	 */
	public HolidayMasterSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Holiday Master object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_HolidayMasterSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("holidayMasterCtrl")) {
			this.holidayMasterCtrl = (HolidayMasterListCtrl) args.get("holidayMasterCtrl");
		} else {
			this.holidayMasterCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_holidayCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_holidayCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayYear.setModel(
				new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayYear.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayType.setModel(
				new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayType.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		/*this.sortOperator_holidays.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_holidays.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayDesc1.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayDesc1.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayDesc2.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayDesc2.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayDesc3.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayDesc3.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_HolidayMasterSearch_RecordStatus.setVisible(false);
			this.label_HolidayMasterSearch_RecordType.setVisible(false);
		}*/
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<HolidayMaster> searchObj = (JdbcSearchObject<HolidayMaster>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("holidayCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_holidayCode, filter);
					this.holidayCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("holidayYear")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_holidayYear, filter);
					this.holidayYear.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("holidayType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_holidayType, filter);
					this.holidayType.setValue(filter.getValue().toString());
			    } 
			    /*else if (filter.getProperty().equals("holidays")) {
					SearchOperators.restoreStringOperator(this.sortOperator_holidays, filter);
					this.holidays.setValue(filter.getValue().toString());
			    } 
			    else 
			    	if (filter.getProperty().equals("holidayDesc1")) {
					SearchOperators.restoreStringOperator(this.sortOperator_holidayDesc1, filter);
					this.holidayDesc1.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("holidayDesc2")) {
					SearchOperators.restoreStringOperator(this.sortOperator_holidayDesc2, filter);
					this.holidayDesc2.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("holidayDesc3")) {
					SearchOperators.restoreStringOperator(this.sortOperator_holidayDesc3, filter);
					this.holidayDesc3.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}*/
					
			}
			
		}
		showHolidayMasterSeekDialog();
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering ");
		this.window_HolidayMasterSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showHolidayMasterSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_HolidayMasterSearch.doModal();
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
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doSearch() {
		logger.debug("Entering ");
		final JdbcSearchObject<HolidayMaster> so = new JdbcSearchObject(
				HolidayMaster.class);
		so.addTabelName("SMTHolidayMaster_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());	
		}
		
		
		if (StringUtils.isNotEmpty(this.holidayCode.getValue())) {

			// get the search operator
			final Listitem item_HolidayCode = this.sortOperator_holidayCode
					.getSelectedItem();

			if (item_HolidayCode != null) {
				final int searchOpId = ((SearchOperators) item_HolidayCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidayCode", "%"
							+ this.holidayCode.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidayCode", this.holidayCode
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.holidayYear.getValue())) {

			// get the search operator
			final Listitem item_HolidayYear = this.sortOperator_holidayYear
					.getSelectedItem();

			if (item_HolidayYear != null) {
				final int searchOpId = ((SearchOperators) item_HolidayYear
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidayYear", "%"
							+ this.holidayYear.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidayYear", this.holidayYear
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.holidayType.getValue())) {

			// get the search operator
			final Listitem item_HolidayType = this.sortOperator_holidayType.getSelectedItem();

			if (item_HolidayType != null) {
				final int searchOpId = ((SearchOperators) item_HolidayType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidayType", "%"
							+ this.holidayType.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidayType", this.holidayType
							.getValue(), searchOpId));
				}
			}
		}
		/*if (StringUtils.isNotEmpty(this.holidays.getValue())) {

			// get the search operator
			final Listitem item_Holidays = this.sortOperator_holidays.getSelectedItem();

			if (item_Holidays != null) {
				final int searchOpId = ((SearchOperators) item_Holidays.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidays", "%" + this.holidays.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidays", this.holidays.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.holidayDesc1.getValue())) {

			// get the search operator
			final Listitem item_HolidayDesc1 = this.sortOperator_holidayDesc1.getSelectedItem();

			if (item_HolidayDesc1 != null) {
				final int searchOpId = ((SearchOperators) item_HolidayDesc1.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidayDesc1", "%" + this.holidayDesc1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidayDesc1", this.holidayDesc1.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.holidayDesc2.getValue())) {

			// get the search operator
			final Listitem item_HolidayDesc2 = this.sortOperator_holidayDesc2.getSelectedItem();

			if (item_HolidayDesc2 != null) {
				final int searchOpId = ((SearchOperators) item_HolidayDesc2.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidayDesc2", "%" + this.holidayDesc2.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidayDesc2", this.holidayDesc2.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.holidayDesc3.getValue())) {

			// get the search operator
			final Listitem item_HolidayDesc3 = this.sortOperator_holidayDesc3.getSelectedItem();

			if (item_HolidayDesc3 != null) {
				final int searchOpId = ((SearchOperators) item_HolidayDesc3.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("holidayDesc3", "%" + this.holidayDesc3.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("holidayDesc3", this.holidayDesc3.getValue(), searchOpId));
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
		}*/
		// Defualt Sort on the table
		so.addSort("HolidayCode", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.holidayMasterCtrl.setSearchObj(so);

		final Listbox listBox = this.holidayMasterCtrl.listBoxHolidayMaster;
		final Paging paging = this.holidayMasterCtrl.pagingHolidayMasterList;

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<HolidayMaster>) listBox.getModel()).init(so, listBox, paging);
		this.holidayMasterCtrl.setSearchObj(so);

		this.label_HolidayMasterSearchResult.setValue(Labels
				.getLabel("label_HolidayMasterSearchResult.value")
				+ " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setHolidayMasterService(HolidayMasterService holidayMasterService) {
		this.holidayMasterService = holidayMasterService;
	}
	public HolidayMasterService getHolidayMasterService() {
		return this.holidayMasterService;
	}
}