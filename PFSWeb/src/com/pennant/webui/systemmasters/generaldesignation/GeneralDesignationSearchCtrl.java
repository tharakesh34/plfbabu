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
 * FileName    		:  GeneralDesignationSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.generaldesignation;

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
import com.pennant.backend.model.systemmasters.GeneralDesignation;
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
 * /WEB-INF/pages/SystemMaster/GeneralDesignation/GeneralDesignationSearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class GeneralDesignationSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1607010033517693903L;
	private final static Logger logger = Logger.getLogger(GeneralDesignationSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_GeneralDesignationSearch; 	// autoWired

	protected Textbox genDesignation; 					// autoWired
	protected Listbox sortOperator_genDesignation; 		// autoWired
	protected Textbox genDesgDesc; 						// autoWired
	protected Listbox sortOperator_genDesgDesc; 		// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType; 						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired

	protected Label label_GeneralDesignationSearch_RecordStatus; 	// autoWired
	protected Label label_GeneralDesignationSearch_RecordType; 		// autoWired
	protected Label label_GeneralDesignationSearchResult; 			// autoWired

	// not auto wired Var's
	private transient GeneralDesignationListCtrl generalDesignationCtrl; // overHanded per param
	private transient WorkFlowDetails 			 workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("GeneralDesignation");

	/**
	 * constructor
	 */
	public GeneralDesignationSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected GeneralDesignation
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_GeneralDesignationSearch(Event event)
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

		// get the params map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("generalDesignationCtrl")) {
			this.generalDesignationCtrl = (GeneralDesignationListCtrl) args
					.get("generalDesignationCtrl");
		} else {
			this.generalDesignationCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_genDesignation.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_genDesignation.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_genDesgDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_genDesgDesc.setItemRenderer(
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
			this.label_GeneralDesignationSearch_RecordStatus.setVisible(false);
			this.label_GeneralDesignationSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<GeneralDesignation> searchObj = (JdbcSearchObject<GeneralDesignation>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("genDesignation")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_genDesignation, filter);
					this.genDesignation.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("genDesgDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_genDesgDesc, filter);
					this.genDesgDesc.setValue(filter.getValue().toString());
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
		showGeneralDesignationSeekDialog();
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
		this.window_GeneralDesignationSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showGeneralDesignationSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_GeneralDesignationSearch.doModal();
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
		final JdbcSearchObject<GeneralDesignation> so = new JdbcSearchObject<GeneralDesignation>(
				GeneralDesignation.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTGenDesignations_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		} else {
			so.addTabelName("RMTGenDesignations_AView");
		}

		if (StringUtils.isNotEmpty(this.genDesignation.getValue())) {

			// get the search operator
			final Listitem item_GenDesignation = this.sortOperator_genDesignation
					.getSelectedItem();

			if (item_GenDesignation != null) {
				final int searchOpId = ((SearchOperators) item_GenDesignation
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("genDesignation", "%"
							+ this.genDesignation.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("genDesignation",
							this.genDesignation.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.genDesgDesc.getValue())) {

			// get the search operator
			final Listitem item_GenDesgDesc = this.sortOperator_genDesgDesc
					.getSelectedItem();

			if (item_GenDesgDesc != null) {
				final int searchOpId = ((SearchOperators) item_GenDesgDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("genDesgDesc", "%"
						+ this.genDesgDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("genDesgDesc", this.genDesgDesc
							.getValue(), searchOpId));
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
		so.addSort("GenDesignation", false);

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
		this.generalDesignationCtrl.setSearchObj(so);

		final Listbox listBox = this.generalDesignationCtrl.listBoxGeneralDesignation;
		final Paging paging = this.generalDesignationCtrl.pagingGeneralDesignationList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<GeneralDesignation>) listBox.getModel()).init(so,
				listBox, paging);
		this.generalDesignationCtrl.setSearchObj(so);

		this.label_GeneralDesignationSearchResult.setValue(Labels
				.getLabel("label_GeneralDesignationSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}
	
}