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
 * FileName    		:  SubSegmentSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.subsegment;

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
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.systemmasters.SubSegmentService;
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
 * /WEB-INF/pages/SystemMasters/SubSegment/subSegmentSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class SubSegmentSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5339479562357848230L;
	private final static Logger logger = Logger.getLogger(SubSegmentSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  	window_SubSegmentSearch; 			// autoWired
	protected Textbox 	segmentCode; 						// autoWired
	protected Listbox 	sortOperator_segmentCode; 			// autoWired
	protected Textbox 	subSegmentCode; 					// autoWired
	protected Listbox 	sortOperator_subSegmentCode; 		// autoWired
	protected Textbox 	subSegmentDesc; 					// autoWired
	protected Listbox 	sortOperator_subSegmentDesc; 		// autoWired
	protected Checkbox 	subSegmentIsActive; 				// autoWired
	protected Listbox 	sortOperator_subSegmentIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType;							// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_SubSegmentSearch_RecordStatus; 	// autoWired
	protected Label label_SubSegmentSearch_RecordType;   	// autoWired
	protected Label label_SubSegmentSearchResult; 		 	// autoWired

	// not auto wired Var's
	private transient SubSegmentListCtrl subSegmentCtrl; 	// overHanded per parameter
	private transient SubSegmentService subSegmentService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SubSegment");

	/**
	 * Default constructor
	 */
	public SubSegmentSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSegment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SubSegmentSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("subSegmentCtrl")) {
			this.subSegmentCtrl = (SubSegmentListCtrl) args.get("subSegmentCtrl");
		} else {
			this.subSegmentCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_segmentCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_segmentCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subSegmentCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_subSegmentCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subSegmentDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_subSegmentDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subSegmentIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_subSegmentIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_SubSegmentSearch_RecordStatus.setVisible(false);
			this.label_SubSegmentSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<SubSegment> searchObj = (JdbcSearchObject<SubSegment>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("segmentCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_segmentCode, filter);
					this.segmentCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("subSegmentCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_subSegmentCode, filter);
					this.subSegmentCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("subSegmentDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_subSegmentDesc, filter);
					this.subSegmentDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("subSegmentIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_subSegmentIsActive, filter);
					//this.subSegmentIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.subSegmentIsActive.setChecked(true);
					}else{
						this.subSegmentIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showSubSegmentSeekDialog();
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
	 * 
	 * @throws InterruptedException
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
		this.window_SubSegmentSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 * 
	 * @throws InterruptedException
	 */
	private void showSubSegmentSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SubSegmentSearch.doModal();
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
	 * 1. Checks for each TextBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");
		final JdbcSearchObject<SubSegment> so = new JdbcSearchObject<SubSegment>(SubSegment.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTSubSegments_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTSubSegments_AView");
		}

		if (StringUtils.isNotEmpty(this.segmentCode.getValue())) {

			// get the search operator
			final Listitem item_SegmentCode = this.sortOperator_segmentCode.getSelectedItem();

			if (item_SegmentCode != null) {
				final int searchOpId = ((SearchOperators) item_SegmentCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("segmentCode", "%" + this.segmentCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("segmentCode", this.segmentCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.subSegmentCode.getValue())) {

			// get the search operator
			final Listitem item_SubSegmentCode = this.sortOperator_subSegmentCode.getSelectedItem();

			if (item_SubSegmentCode != null) {
				final int searchOpId = ((SearchOperators) item_SubSegmentCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subSegmentCode", "%" + this.subSegmentCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subSegmentCode", this.subSegmentCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.subSegmentDesc.getValue())) {

			// get the search operator
			final Listitem item_SubSegmentDesc = this.sortOperator_subSegmentDesc.getSelectedItem();

			if (item_SubSegmentDesc != null) {
				final int searchOpId = ((SearchOperators) item_SubSegmentDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subSegmentDesc", "%" + this.subSegmentDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subSegmentDesc", this.subSegmentDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_SubSegmentIsActive = this.sortOperator_subSegmentIsActive.getSelectedItem();

		if (item_SubSegmentIsActive != null) {
			final int searchOpId = ((SearchOperators) item_SubSegmentIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.subSegmentIsActive.isChecked()) {
					so.addFilter(new Filter("subSegmentIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("subSegmentIsActive", 0, searchOpId));
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

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
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
		so.addSort("SegmentCode", false);

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
		this.subSegmentCtrl.setSearchObj(so);

		final Listbox listBox = this.subSegmentCtrl.listBoxSubSegment;
		final Paging paging = this.subSegmentCtrl.pagingSubSegmentList;

		// set the model to the ListBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<SubSegment>) listBox.getModel()).init(so, listBox, paging);
		this.subSegmentCtrl.setSearchObj(so);
		this.label_SubSegmentSearchResult.setValue(Labels.getLabel(
		"label_SubSegmentSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSubSegmentService(SubSegmentService subSegmentService) {
		this.subSegmentService = subSegmentService;
	}
	public SubSegmentService getSubSegmentService() {
		return this.subSegmentService;
	}
}