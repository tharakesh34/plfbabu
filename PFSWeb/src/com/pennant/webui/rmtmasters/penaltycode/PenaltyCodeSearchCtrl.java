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
 * FileName    		:  PenaltyCodeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.rmtmasters.penaltycode;

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
import com.pennant.backend.model.rmtmasters.PenaltyCode;
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
 * /WEB-INF/pages/RMTMasters/PenaltyCode/PenaltyCodeSearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class PenaltyCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5802488495597813076L;
	private final static Logger logger = Logger.getLogger(PenaltyCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_PenaltyCodeSearch; 		// autowired
	protected Textbox penaltyType; 					// autowired
	protected Listbox sortOperator_penaltyType; 	// autowired
	protected Textbox penaltyDesc; 					// autowired
	protected Listbox sortOperator_penaltyDesc; 	// autowired
	protected Checkbox penaltyIsActive; 			// autowired
	protected Listbox sortOperator_penaltyIsActive; // autowired
	protected Textbox recordStatus; 				// autowired
	protected Listbox recordType;					// autowired
	protected Listbox sortOperator_recordStatus; 	// autowired
	protected Listbox sortOperator_recordType; 		// autowired

	protected Label label_PenaltyCodeSearch_RecordStatus; 	// autowired
	protected Label label_PenaltyCodeSearch_RecordType; 	// autowired
	protected Label label_PenaltyCodeSearchResult; 			// autowired

	// not auto wired vars
	private transient PenaltyCodeListCtrl 	penaltyCodeCtrl; // overhanded per param
	private transient WorkFlowDetails 		workFlowDetails=WorkFlowUtil.getWorkFlowDetails("PenaltyCode");

	/**
	 * constructor
	 */
	public PenaltyCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected PenaltyCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PenaltyCodeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the params map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("penaltyCodeCtrl")) {
			this.penaltyCodeCtrl = (PenaltyCodeListCtrl) args
					.get("penaltyCodeCtrl");
		} else {
			this.penaltyCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_penaltyType.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_penaltyType
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_penaltyDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_penaltyDesc
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_penaltyIsActive.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_penaltyIsActive
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus
					.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType
					.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_PenaltyCodeSearch_RecordStatus.setVisible(false);
			this.label_PenaltyCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<PenaltyCode> searchObj = (JdbcSearchObject<PenaltyCode>)
					args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("penaltyType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_penaltyType, filter);
					this.penaltyType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("penaltyDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_penaltyDesc, filter);
					this.penaltyDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("penaltyIsActive")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_penaltyIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.penaltyIsActive.setChecked(true);
					}else{
						this.penaltyIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue()
								.equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}

				}
			}

		}
		showPenaltyCodeSeekDialog();
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
		this.window_PenaltyCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showPenaltyCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_PenaltyCodeSearch.doModal();
		} catch (final Exception e) {
			logger.debug("Leaving");
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
		final JdbcSearchObject<PenaltyCode> so = new JdbcSearchObject<PenaltyCode>(
				PenaltyCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTPenaltyCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			so.addTabelName("RMTPenaltyCodes_AView");
		}

		if (StringUtils.isNotEmpty(this.penaltyType.getValue())) {

			// get the search operator
			final Listitem item_PenaltyType = this.sortOperator_penaltyType
					.getSelectedItem();

			if (item_PenaltyType != null) {
				final int searchOpId = ((SearchOperators) item_PenaltyType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("penaltyType", "%"
						+ this.penaltyType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("penaltyType", this.penaltyType
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.penaltyDesc.getValue())) {

			// get the search operator
			final Listitem item_PenaltyDesc = this.sortOperator_penaltyDesc
					.getSelectedItem();

			if (item_PenaltyDesc != null) {
				final int searchOpId = ((SearchOperators) item_PenaltyDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("penaltyDesc", "%"
						+ this.penaltyDesc.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("penaltyDesc", this.penaltyDesc
							.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_PenaltyIsActive = this.sortOperator_penaltyIsActive
				.getSelectedItem();

		if (item_PenaltyIsActive != null) {
			final int searchOpId = ((SearchOperators) item_PenaltyIsActive
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.penaltyIsActive.isChecked()) {
					so.addFilter(new Filter("penaltyIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("penaltyIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus
					.getSelectedItem();
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

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue()
					.toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType
					.getSelectedItem();
			if (item_RecordType != null) {
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
		so.addSort("PenaltyType", false);

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
		this.penaltyCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.penaltyCodeCtrl.listBoxPenaltyCode;
		final Paging paging = this.penaltyCodeCtrl.pagingPenaltyCodeList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<PenaltyCode>) listBox.getModel()).init(so, listBox,paging);
		this.penaltyCodeCtrl.setSearchObj(so);

		this.label_PenaltyCodeSearchResult.setValue(Labels
				.getLabel("label_PenaltyCodeSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}
	
}