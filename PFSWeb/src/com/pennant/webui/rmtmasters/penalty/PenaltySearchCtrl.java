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
 * FileName    		:  PenaltySearchCtrl.java                                                   * 	  
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

package com.pennant.webui.rmtmasters.penalty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
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
 * /WEB-INF/pages/RMTMasters/Penalty/PenaltySearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class PenaltySearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -1229025740272813783L;
	private final static Logger logger = Logger.getLogger(PenaltySearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_PenaltySearch; 				// autowired
	protected Textbox 	penaltyType; 						// autowired
	protected Listbox 	sortOperator_penaltyType; 			// autowired
	protected Datebox 	penaltyEffDate; 					// autowired
	protected Listbox 	sortOperator_penaltyEffDate; 		// autowired
	protected Checkbox 	isPenaltyCapitalize; 				// autowired
	protected Listbox 	sortOperator_isPenaltyCapitalize; 	// autowired
	protected Checkbox 	isPenaltyOnPriOnly; 				// autowired
	protected Listbox 	sortOperator_isPenaltyOnPriOnly; 	// autowired
	protected Checkbox 	isPenaltyAftGrace; 					// autowired
	protected Listbox 	sortOperator_isPenaltyAftGrace; 	// autowired
	protected Intbox 	oDueGraceDays; 						// autowired
	protected Listbox 	sortOperator_oDueGraceDays; 		// autowired
	protected Checkbox 	penaltyIsActive; 					// autowired
	protected Listbox 	sortOperator_penaltyIsActive; 		// autowired
	protected Textbox 	recordStatus; 						// autowired
	protected Listbox 	recordType;							// autowired
	protected Listbox 	sortOperator_recordStatus; 			// autowired
	protected Listbox 	sortOperator_recordType; 			// autowired

	protected Label label_PenaltySearch_RecordStatus; 	// autowired
	protected Label label_PenaltySearch_RecordType; 	// autowired
	protected Label label_PenaltySearchResult; 			// autowired

	// not auto wired vars
	private transient PenaltyListCtrl 	penaltyCtrl; // overhanded per param
	private transient WorkFlowDetails 	workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("Penalty");

	/**
	 * constructor
	 */
	public PenaltySearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Penalty object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PenaltySearch(Event event) throws Exception {
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

		if (args.containsKey("penaltyCtrl")) {
			this.penaltyCtrl = (PenaltyListCtrl) args.get("penaltyCtrl");
		} else {
			this.penaltyCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_penaltyType.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_penaltyType.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_penaltyEffDate.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_penaltyEffDate.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_isPenaltyCapitalize.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_isPenaltyCapitalize.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_isPenaltyOnPriOnly.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_isPenaltyOnPriOnly.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_isPenaltyAftGrace.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_isPenaltyAftGrace.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_oDueGraceDays.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_oDueGraceDays.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_penaltyIsActive.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_penaltyIsActive.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_PenaltySearch_RecordStatus.setVisible(false);
			this.label_PenaltySearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<Penalty> searchObj = (JdbcSearchObject<Penalty>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("penaltyType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_penaltyType, filter);
					this.penaltyType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("penaltyEffDate")) {
					SearchOperators.restoreNumericOperator(
							this.sortOperator_penaltyEffDate, filter);
					this.penaltyEffDate.setValue(DateUtility.getUtilDate(
							filter.getValue().toString(),PennantConstants.DBDateFormat));
				} else if (filter.getProperty().equals("isPenaltyCapitalize")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_isPenaltyCapitalize, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.isPenaltyCapitalize.setChecked(true);
					}else{
						this.isPenaltyCapitalize.setChecked(false);
					}
				} else if (filter.getProperty().equals("isPenaltyOnPriOnly")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_isPenaltyOnPriOnly, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.isPenaltyOnPriOnly.setChecked(true);
					}else{
						this.isPenaltyOnPriOnly.setChecked(false);
					}
				} else if (filter.getProperty().equals("isPenaltyAftGrace")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_isPenaltyAftGrace, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.isPenaltyAftGrace.setChecked(true);
					}else{
						this.isPenaltyAftGrace.setChecked(false);
					}
				} else if (filter.getProperty().equals("oDueGraceDays")) {
					SearchOperators.restoreNumericOperator(
							this.sortOperator_oDueGraceDays, filter);
					this.oDueGraceDays.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("penaltyIsActive")) {
					SearchOperators.restoreBooleanOperators(
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
		showPenaltySeekDialog();
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
		this.window_PenaltySearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showPenaltySeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_PenaltySearch.doModal();
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
		final JdbcSearchObject<Penalty> so = new JdbcSearchObject<Penalty>(
				Penalty.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTPenalties_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			so.addTabelName("RMTPenalties_AView");
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
						+ this.penaltyType.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("penaltyType", this.penaltyType
							.getValue(), searchOpId));
				}
			}
		}
		if (this.penaltyEffDate.getValue()!=null) {

			// get the search operator
			final Listitem item_PenaltyEffDate = this.sortOperator_penaltyEffDate
					.getSelectedItem();

			if (item_PenaltyEffDate != null) {
				final int searchOpId = ((SearchOperators) item_PenaltyEffDate
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("penaltyEffDate",DateUtility.formatUtilDate(
							this.penaltyEffDate.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_IsPenaltyCapitalize = this.sortOperator_isPenaltyCapitalize
				.getSelectedItem();

		if (item_IsPenaltyCapitalize != null) {
			final int searchOpId = ((SearchOperators) item_IsPenaltyCapitalize
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.isPenaltyCapitalize.isChecked()) {
					so.addFilter(new Filter("isPenaltyCapitalize", 1, searchOpId));
				} else {
					so.addFilter(new Filter("isPenaltyCapitalize", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_IsPenaltyOnPriOnly = this.sortOperator_isPenaltyOnPriOnly
				.getSelectedItem();

		if (item_IsPenaltyOnPriOnly != null) {
			final int searchOpId = ((SearchOperators) item_IsPenaltyOnPriOnly
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.isPenaltyOnPriOnly.isChecked()) {
					so.addFilter(new Filter("isPenaltyOnPriOnly", 1, searchOpId));
				} else {
					so.addFilter(new Filter("isPenaltyOnPriOnly", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_IsPenaltyAftGrace = this.sortOperator_isPenaltyAftGrace
				.getSelectedItem();

		if (item_IsPenaltyAftGrace != null) {
			final int searchOpId = ((SearchOperators) item_IsPenaltyAftGrace
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.isPenaltyAftGrace.isChecked()) {
					so.addFilter(new Filter("isPenaltyAftGrace", 1, searchOpId));
				} else {
					so.addFilter(new Filter("isPenaltyAftGrace", 0, searchOpId));
				}
			}
		}
		if (this.oDueGraceDays.intValue()!=0) {

			// get the search operator
			final Listitem item_ODueGraceDays = this.sortOperator_oDueGraceDays
					.getSelectedItem();

			if (item_ODueGraceDays != null) {
				final int searchOpId = ((SearchOperators) item_ODueGraceDays
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("oDueGraceDays", this.oDueGraceDays
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
						+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
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
		this.penaltyCtrl.setSearchObj(so);

		final Listbox listBox = this.penaltyCtrl.listBoxPenalty;
		final Paging paging = this.penaltyCtrl.pagingPenaltyList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<Penalty>) listBox.getModel()).init(so, listBox,paging);
		this.penaltyCtrl.setSearchObj(so);

		this.label_PenaltySearchResult.setValue(Labels
				.getLabel("label_PenaltySearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}