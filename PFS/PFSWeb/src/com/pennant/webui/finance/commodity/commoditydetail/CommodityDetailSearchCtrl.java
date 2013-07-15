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
 * FileName    		:  CommodityDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.commodity.commoditydetail;

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
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.service.finance.commodity.CommodityDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class CommodityDetailSearchCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = 8316534848725991654L;
	private final static Logger logger = Logger.getLogger(CommodityDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CommodityDetailSearch;               // autoWired

	protected Textbox commodityCode;                              // autoWired
	protected Listbox sortOperator_commodityCode;                 // autoWired
	protected Textbox commodityName;                              // autoWired
	protected Listbox sortOperator_commodityName;                 // autoWired
	protected Textbox commodityUnitCode;                          // autoWired
	protected Listbox sortOperator_commodityUnitCode;             // autoWired
	protected Textbox commodityUnitName;                          // autoWired
	protected Listbox sortOperator_commodityUnitName;             // autoWired
	protected Textbox recordStatus;                               // autoWired
	protected Listbox recordType;	                              // autoWired
	protected Listbox sortOperator_recordStatus;                  // autoWired
	protected Listbox sortOperator_recordType;                    // autoWired

	protected Label   label_CommodityDetailSearch_RecordStatus;   // autoWired
	protected Label   label_CommodityDetailSearch_RecordType;     // autoWired
	protected Label   label_CommodityDetailSearchResult;          // autoWired

	// not auto wired variables
	private transient CommodityDetailListCtrl commodityDetailCtrl; // over handed per parameters
	private transient CommodityDetailService commodityDetailService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CommodityDetail");

	/**
	 * constructor
	 */
	public CommodityDetailSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CommodityDetailSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("commodityDetailCtrl")) {
			this.commodityDetailCtrl = (CommodityDetailListCtrl) args.get("commodityDetailCtrl");
		} else {
			this.commodityDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_commodityCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_commodityCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_commodityName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_commodityName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_commodityUnitCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_commodityUnitCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_commodityUnitName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_commodityUnitName.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CommodityDetailSearch_RecordStatus.setVisible(false);
			this.label_CommodityDetailSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<CommodityDetail> searchObj = (JdbcSearchObject<CommodityDetail>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("commodityCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_commodityCode, filter);
					this.commodityCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("commodityName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_commodityName, filter);
					this.commodityName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("commodityUnitCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_commodityUnitCode, filter);
					this.commodityUnitCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("commodityUnitName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_commodityUnitName, filter);
					this.commodityUnitName.setValue(filter.getValue().toString());
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
		showCommodityDetailSeekDialog();
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
		this.window_CommodityDetailSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCommodityDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering ");

		try {
			// open the dialog in modal mode
			this.window_CommodityDetailSearch.doModal();
		} catch (final Exception e) {
			logger.debug("ERROR:"+e.toString());
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering ");

		final JdbcSearchObject<CommodityDetail> so = new JdbcSearchObject<CommodityDetail>(CommodityDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("FCMTCommodityDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FCMTCommodityDetail_AView");
		}


		if (StringUtils.isNotEmpty(this.commodityCode.getValue())) {

			// get the search operator
			final Listitem item_CommodityCode = this.sortOperator_commodityCode.getSelectedItem();

			if (item_CommodityCode != null) {
				final int searchOpId = ((SearchOperators) item_CommodityCode.getAttribute("data"))
				.getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("commodityCode", "%" + this.commodityCode.getValue().toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("commodityCode", this.commodityCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.commodityName.getValue())) {

			// get the search operator
			final Listitem item_CommodityName = this.sortOperator_commodityName.getSelectedItem();

			if (item_CommodityName != null) {
				final int searchOpId = ((SearchOperators) item_CommodityName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("commodityName", "%" + this.commodityName.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("commodityName", this.commodityName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.commodityUnitCode.getValue())) {

			// get the search operator
			final Listitem item_CommodityUnitCode = this.sortOperator_commodityUnitCode.getSelectedItem();

			if (item_CommodityUnitCode != null) {
				final int searchOpId = ((SearchOperators) item_CommodityUnitCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("commodityUnitCode", "%" + this.commodityUnitCode.getValue().toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("commodityUnitCode", this.commodityUnitCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.commodityUnitName.getValue())) {

			// get the search operator
			final Listitem item_CommodityUnitName = this.sortOperator_commodityUnitName.getSelectedItem();

			if (item_CommodityUnitName != null) {
				final int searchOpId = ((SearchOperators) item_CommodityUnitName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("commodityUnitName", "%" + this.commodityUnitName.getValue().toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("commodityUnitName", this.commodityUnitName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() 
							+ "%", searchOpId));
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
		so.addSort("CommodityCode", false);

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
		this.commodityDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.commodityDetailCtrl.listBoxCommodityDetail;
		final Paging paging = this.commodityDetailCtrl.pagingCommodityDetailList;


		// set the model to the list box with the initial result set get by the DAO method.
		((PagedListWrapper<CommodityDetail>) listBox.getModel()).init(so, listBox, paging);
		this.commodityDetailCtrl.setSearchObj(so);

		this.label_CommodityDetailSearchResult.setValue(Labels.getLabel("label_CommodityDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCommodityDetailService(CommodityDetailService commodityDetailService) {
		this.commodityDetailService = commodityDetailService;
	}

	public CommodityDetailService getCommodityDetailService() {
		return this.commodityDetailService;
	}
}