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
 * FileName    		:  RatingCodeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.ratingcode;

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
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
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
 * /WEB-INF/pages/BMTMasters/Academic/AcademicSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class RatingCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1346430724276779148L;
	private final static Logger logger = Logger.getLogger(RatingCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_RatingCodeSearch; 			// autoWired

	protected Textbox  ratingType; 							// autoWired
	protected Listbox  sortOperator_ratingType; 			// autoWired
	protected Textbox  ratingCode; 							// autoWired
	protected Listbox  sortOperator_ratingCode; 			// autoWired
	protected Textbox  ratingCodeDesc; 						// autoWired
	protected Listbox  sortOperator_ratingCodeDesc;  		// autoWired
	protected Checkbox ratingIsActive; 						// autoWired
	protected Listbox  sortOperator_ratingIsActive;  		// autoWired
	protected Textbox  recordStatus; 						// autoWired
	protected Listbox  recordType;							// autoWired
	protected Listbox  sortOperator_recordStatus; 			// autoWired
	protected Listbox  sortOperator_recordType; 			// autoWired

	protected Label    label_RatingCodeSearch_RecordStatus; // autoWired
	protected Label    label_RatingCodeSearch_RecordType; 	// autoWired
	protected Label    label_RatingCodeSearchResult; 		// autoWired

	// not autoWired Var's
	private transient RatingCodeListCtrl ratingCodeCtrl; 	// over handed per parameter
	private transient RatingCodeService  ratingCodeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RatingCode");

	/**
	 * constructor
	 */
	public RatingCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RatingCodeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("ratingCodeCtrl")) {
			this.ratingCodeCtrl = (RatingCodeListCtrl) args.get("ratingCodeCtrl");
		} else {
			this.ratingCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_ratingType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ratingType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ratingCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ratingCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ratingCodeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ratingCodeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ratingIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_ratingIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_RatingCodeSearch_RecordStatus.setVisible(false);
			this.label_RatingCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<RatingCode> searchObj = (JdbcSearchObject<RatingCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("ratingType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_ratingType, filter);
					this.ratingType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ratingCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_ratingCode, filter);
					this.ratingCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ratingCodeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_ratingCodeDesc, filter);
					this.ratingCodeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ratingIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_ratingIsActive, filter);
					//this.ratingIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.ratingIsActive.setChecked(true);
					}else{
						this.ratingIsActive.setChecked(false);
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
		showRatingCodeSeekDialog();
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
		this.window_RatingCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showRatingCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_RatingCodeSearch.doModal();
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<RatingCode> so = new JdbcSearchObject<RatingCode>(RatingCode.class);
		so.addTabelName("BMTRatingCodes_View");

		if (isWorkFlowEnabled()) {
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		if (StringUtils.isNotEmpty(this.ratingType.getValue())) {

			// get the search operator
			final Listitem item_RatingType = this.sortOperator_ratingType.getSelectedItem();

			if (item_RatingType != null) {
				final int searchOpId = ((SearchOperators) item_RatingType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ratingType", "%" + this.ratingType.getValue().toUpperCase() + "%",	searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ratingType", this.ratingType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.ratingCode.getValue())) {

			// get the search operator
			final Listitem item_RatingCode = this.sortOperator_ratingCode.getSelectedItem();

			if (item_RatingCode != null) {
				final int searchOpId = ((SearchOperators) item_RatingCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ratingCode", "%" + this.ratingCode.getValue().toUpperCase() + "%",	searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ratingCode", this.ratingCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.ratingCodeDesc.getValue())) {

			// get the search operator
			final Listitem item_RatingCodeDesc = this.sortOperator_ratingCodeDesc.getSelectedItem();

			if (item_RatingCodeDesc != null) {
				final int searchOpId = ((SearchOperators) item_RatingCodeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ratingCodeDesc", "%" + this.ratingCodeDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ratingCodeDesc", this.ratingCodeDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_RatingIsActive = this.sortOperator_ratingIsActive.getSelectedItem();

		if (item_RatingIsActive != null) {
			final int searchOpId = ((SearchOperators) item_RatingIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.ratingIsActive.isChecked()) {
					so.addFilter(new Filter("ratingIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("ratingIsActive", 0, searchOpId));
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
		so.addSort("RatingType", false);

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
		this.ratingCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.ratingCodeCtrl.listBoxRatingCode;
		final Paging paging = this.ratingCodeCtrl.pagingRatingCodeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<RatingCode>) listBox.getModel()).init(so, listBox, paging);
		this.ratingCodeCtrl.setSearchObj(so);

		this.label_RatingCodeSearchResult.setValue(Labels.getLabel(
		"label_RatingCodeSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRatingCodeService(RatingCodeService ratingCodeService) {
		this.ratingCodeService = ratingCodeService;
	}
	public RatingCodeService getRatingCodeService() {
		return this.ratingCodeService;
	}
}