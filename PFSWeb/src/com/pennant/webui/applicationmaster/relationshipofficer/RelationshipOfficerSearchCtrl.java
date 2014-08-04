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
 * FileName    		:  RelationshipOfficerSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.relationshipofficer;

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
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
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
 * /WEB-INF/pages/ApplicationMaster/RelationshipOfficer/RelationshipOfficerSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class RelationshipOfficerSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -183338099300918441L;
	private final static Logger logger = Logger.getLogger(RelationshipOfficerSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_RelationshipOfficerSearch;  			// autoWired

	protected Textbox rOfficerCode; 								// autoWired
	protected Listbox sortOperator_rOfficerCode; 					// autoWired
	protected Textbox rOfficerDesc; 								// autoWired
	protected Listbox sortOperator_rOfficerDesc; 					// autoWired
	protected Textbox rOfficerDeptCode; 							// autoWired
	protected Listbox sortOperator_rOfficerDeptCode; 				// autoWired
	protected Checkbox rOfficerIsActive; 							// autoWired
	protected Listbox sortOperator_rOfficerIsActive; 				// autoWired
	protected Textbox recordStatus; 								// autoWired
	protected Listbox recordType;									// autoWired
	protected Listbox sortOperator_recordStatus; 					// autoWired
	protected Listbox sortOperator_recordType; 						// autoWired

	protected Label label_RelationshipOfficerSearch_RecordStatus; 	// autoWired
	protected Label label_RelationshipOfficerSearch_RecordType;   	// autoWired
	protected Label label_RelationshipOfficerSearchResult; 			// autoWired

	// not auto wired variables
	private transient RelationshipOfficerListCtrl relationshipOfficerCtrl; // overHanded per parameter
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("RelationshipOfficer");

	/**
	 * constructor
	 */
	public RelationshipOfficerSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerNotesType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RelationshipOfficerSearch(Event event) throws Exception {
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

		if (args.containsKey("relationshipOfficerCtrl")) {
			this.relationshipOfficerCtrl = (RelationshipOfficerListCtrl) args.get("relationshipOfficerCtrl");
		} else {
			this.relationshipOfficerCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_rOfficerCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rOfficerCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_rOfficerDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rOfficerDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_rOfficerDeptCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rOfficerDeptCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_rOfficerIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_rOfficerIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_RelationshipOfficerSearch_RecordStatus.setVisible(false);
			this.label_RelationshipOfficerSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<RelationshipOfficer> searchObj = (JdbcSearchObject<RelationshipOfficer>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("rOfficerCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rOfficerCode, filter);
					this.rOfficerCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("rOfficerDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rOfficerDesc, filter);
					this.rOfficerDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("rOfficerDeptCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rOfficerDeptCode, filter);
					this.rOfficerDeptCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("rOfficerIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rOfficerIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.rOfficerIsActive.setChecked(true);
					}else{
						this.rOfficerIsActive.setChecked(false);
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
		showRelationshipOfficerSeekDialog();
		logger.debug("Leaving");
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
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_RelationshipOfficerSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showRelationshipOfficerSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_RelationshipOfficerSearch.doModal();
		} catch (final Exception e) {
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

		final JdbcSearchObject<RelationshipOfficer> so = new JdbcSearchObject<RelationshipOfficer>(RelationshipOfficer.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RelationshipOfficers_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else{
			so.addTabelName("RelationshipOfficers_AView");
		}

		if (StringUtils.isNotEmpty(this.rOfficerCode.getValue())) {

			// get the search operator
			final Listitem item_ROfficerCode = this.sortOperator_rOfficerCode.getSelectedItem();

			if (item_ROfficerCode != null) {
				final int searchOpId = ((SearchOperators) item_ROfficerCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("rOfficerCode", "%" + this.rOfficerCode.getValue().toUpperCase() + "%",	searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("rOfficerCode", this.rOfficerCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.rOfficerDesc.getValue())) {

			// get the search operator
			final Listitem item_ROfficerDesc = this.sortOperator_rOfficerDesc.getSelectedItem();

			if (item_ROfficerDesc != null) {
				final int searchOpId = ((SearchOperators) item_ROfficerDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("rOfficerDesc", "%" + this.rOfficerDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("rOfficerDesc", this.rOfficerDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.rOfficerDeptCode.getValue())) {

			// get the search operator
			final Listitem item_ROfficerDeptCode = this.sortOperator_rOfficerDeptCode.getSelectedItem();

			if (item_ROfficerDeptCode != null) {
				final int searchOpId = ((SearchOperators) item_ROfficerDeptCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("rOfficerDeptCode", "%" + this.rOfficerDeptCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("rOfficerDeptCode", this.rOfficerDeptCode.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_ROfficerIsActive = this.sortOperator_rOfficerIsActive.getSelectedItem();

		if (item_ROfficerIsActive != null) {
			final int searchOpId = ((SearchOperators) item_ROfficerIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.rOfficerIsActive.isChecked()) {
					so.addFilter(new Filter("rOfficerIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("rOfficerIsActive", 0, searchOpId));
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
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("ROfficerCode", false);

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
		this.relationshipOfficerCtrl.setSearchObj(so);

		final Listbox listBox = this.relationshipOfficerCtrl.listBoxRelationshipOfficer;
		final Paging paging = this.relationshipOfficerCtrl.pagingRelationshipOfficerList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<RelationshipOfficer>) listBox.getModel()).init(so, listBox, paging);
		this.relationshipOfficerCtrl.setSearchObj(so);

		this.label_RelationshipOfficerSearchResult.setValue(Labels.getLabel(
		"label_RelationshipOfficerSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}