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
 * FileName    		:  WeekendMasterSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.smtmasters.weekendmaster;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/WeekendMaster/weekendMasterSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
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
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.service.smtmasters.WeekendMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class WeekendMasterSearchCtrl extends GFCBaseCtrl implements
Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
	.getLogger(WeekendMasterSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WeekendMasterSearch; // autowired

	protected Textbox weekendCode; 				// autowired
	protected Listbox sortOperator_weekendCode; // autowired
	protected Textbox weekendDesc; 				// autowired
	protected Listbox sortOperator_weekendDesc; // autowired
	protected Textbox weekend; 					// autowired
	protected Listbox sortOperator_weekend; 	// autowired
	protected Listbox sortOperator_recordStatus;// autowired
	protected Listbox sortOperator_recordType; 	// autowired

	protected Label label_WeekendMasterSearch_RecordStatus; // autowired
	protected Label label_WeekendMasterSearch_RecordType; 	// autowired
	protected Label label_WeekendMasterSearchResult; 		// autowired

	// not auto wired vars
	private transient WeekendMasterListCtrl weekendMasterCtrl; // overhanded per
	// param
	private transient WeekendMasterService weekendMasterService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
	.getWorkFlowDetails("WeekendMaster");

	/**
	 * constructor
	 */
	public WeekendMasterSearchCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected WeekendMaster object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_WeekendMasterSearch(Event event)
	throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("weekendMasterCtrl")) {
			this.weekendMasterCtrl = (WeekendMasterListCtrl) args
			.get("weekendMasterCtrl");
		} else {
			this.weekendMasterCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_weekendCode.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_weekendCode
		.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_weekendDesc.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_weekendDesc
		.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_weekend.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_weekend
		.setItemRenderer(new SearchOperatorListModelItemRenderer());

		

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<WeekendMaster> searchObj = (JdbcSearchObject<WeekendMaster>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("weekendCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_weekendCode, filter);
					this.weekendCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("weekendDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_weekendDesc, filter);
					this.weekendDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("weekend")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_weekend, filter);
					this.weekend.setValue(filter.getValue().toString());
				}
				
			}

		}
		showWeekendMasterSeekDialog();
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
		this.window_WeekendMasterSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showWeekendMasterSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_WeekendMasterSearch.doModal();
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
		final JdbcSearchObject<WeekendMaster> so = new JdbcSearchObject(
				WeekendMaster.class);
		so.addTabelName("SMTWeekendMaster_View");

		if (StringUtils.isNotEmpty(this.weekendCode.getValue())) {

			// get the search operator
			final Listitem item_WeekendCode = this.sortOperator_weekendCode
			.getSelectedItem();

			if (item_WeekendCode != null) {
				final int searchOpId = ((SearchOperators) item_WeekendCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("weekendCode", "%"
							+ this.weekendCode.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("weekendCode", this.weekendCode
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.weekendDesc.getValue())) {

			// get the search operator
			final Listitem item_WeekendDesc = this.sortOperator_weekendDesc
			.getSelectedItem();

			if (item_WeekendDesc != null) {
				final int searchOpId = ((SearchOperators) item_WeekendDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("weekendDesc", "%"
							+ this.weekendDesc.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("weekendDesc", this.weekendDesc
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.weekend.getValue())) {

			// get the search operator
			final Listitem item_Weekend = this.sortOperator_weekend
			.getSelectedItem();

			if (item_Weekend != null) {
				final int searchOpId = ((SearchOperators) item_Weekend
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("weekend", "%"
							+ this.weekend.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("weekend", this.weekend.getValue(),
							searchOpId));
				}
			}
		}
		// Defualt Sort on the table
		so.addSort("WeekendCode", false);

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
		this.weekendMasterCtrl.setSearchObj(so);

		final Listbox listBox = this.weekendMasterCtrl.listBoxWeekendMaster;
		final Paging paging = this.weekendMasterCtrl.pagingWeekendMasterList;

		// set the model to the listbox with the initial resultset get by the
		// DAO method.
		((PagedListWrapper<WeekendMaster>) listBox.getModel()).init(so,
				listBox, paging);
		this.weekendMasterCtrl.setSearchObj(so);

		this.label_WeekendMasterSearchResult.setValue(Labels
				.getLabel("label_WeekendMasterSearchResult.value")
				+ " "
				+ String.valueOf(paging.getTotalSize()));

		logger.debug("Leaving doSearch()");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setWeekendMasterService(
			WeekendMasterService weekendMasterService) {
		this.weekendMasterService = weekendMasterService;
	}
	public WeekendMasterService getWeekendMasterService() {
		return this.weekendMasterService;
	}
}