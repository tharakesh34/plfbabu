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
 * FileName    		:  SalesOfficerSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.salesofficer;

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
import com.pennant.backend.model.applicationmaster.SalesOfficer;
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
 * /WEB-INF/pages/RMTMasters/SalesOfficer/SalesOfficerSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SalesOfficerSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1937450179315010403L;
	private final static Logger logger = Logger.getLogger(SalesOfficerSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_SalesOfficerSearch;    		// autoWired

	protected Textbox salesOffCode; 					// autoWired
	protected Listbox sortOperator_salesOffCode; 		// autoWired
	protected Textbox salesOffFName; 					// autoWired
	protected Listbox sortOperator_salesOffFName; 		// autoWired
	protected Textbox salesOffDept; 					// autoWired
	protected Listbox sortOperator_salesOffDept; 		// autoWired
	protected Checkbox salesOffIsActive; 				// autoWired
	protected Listbox sortOperator_salesOffIsActive; 	// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType; 						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType;			// autoWired

	protected Label label_SalesOfficerSearch_RecordStatus; 	// autoWired
	protected Label label_SalesOfficerSearch_RecordType; 	// autoWired
	protected Label label_SalesOfficerSearchResult; 		// autoWired

	// not auto wired variables
	private transient SalesOfficerListCtrl salesOfficerCtrl; // overHanded per parameter
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("SalesOfficer");

	/**
	 * constructor
	 */
	public SalesOfficerSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SalesOfficer object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SalesOfficerSearch(Event event)
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

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("salesOfficerCtrl")) {
			this.salesOfficerCtrl = (SalesOfficerListCtrl) args
					.get("salesOfficerCtrl");
		} else {
			this.salesOfficerCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_salesOffCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_salesOffCode
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_salesOffFName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_salesOffFName
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_salesOffDept.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_salesOffDept
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_salesOffIsActive.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_salesOffIsActive
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
			this.label_SalesOfficerSearch_RecordStatus.setVisible(false);
			this.label_SalesOfficerSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<SalesOfficer> searchObj = (JdbcSearchObject<SalesOfficer>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("salesOffCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_salesOffCode, filter);
					this.salesOffCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("salesOffFName")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_salesOffFName, filter);
					this.salesOffFName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("salesOffDept")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_salesOffDept, filter);
					this.salesOffDept.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("salesOffIsActive")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_salesOffIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.salesOffIsActive.setChecked(true);
					}else{
						this.salesOffIsActive.setChecked(false);
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
		showSalesOfficerSeekDialog();
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
		this.window_SalesOfficerSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSalesOfficerSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SalesOfficerSearch.doModal();
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
		final JdbcSearchObject<SalesOfficer> so = new JdbcSearchObject<SalesOfficer>(
				SalesOfficer.class);
		
		if (isWorkFlowEnabled()) {
			so.addTabelName("SalesOfficers_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}
		else{
			so.addTabelName("SalesOfficers_AView");
		}

		if (StringUtils.isNotEmpty(this.salesOffCode.getValue())) {

			// get the search operator
			final Listitem item_SalesOffCode = this.sortOperator_salesOffCode
					.getSelectedItem();

			if (item_SalesOffCode != null) {
				final int searchOpId = ((SearchOperators) item_SalesOffCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("salesOffCode", "%"
							+ this.salesOffCode.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("salesOffCode", this.salesOffCode
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.salesOffFName.getValue())) {

			// get the search operator
			final Listitem item_SalesOffFName = this.sortOperator_salesOffFName
					.getSelectedItem();

			if (item_SalesOffFName != null) {
				final int searchOpId = ((SearchOperators) item_SalesOffFName
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("salesOffFName",
							"%" + this.salesOffFName.getValue().toUpperCase()
									+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("salesOffFName", this.salesOffFName
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.salesOffDept.getValue())) {

			// get the search operator
			final Listitem item_SalesOffDept = this.sortOperator_salesOffDept
					.getSelectedItem();

			if (item_SalesOffDept != null) {
				final int searchOpId = ((SearchOperators) item_SalesOffDept
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("salesOffDept", "%"
							+ this.salesOffDept.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("salesOffDept", this.salesOffDept
							.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_SalesOffIsActive = this.sortOperator_salesOffIsActive
				.getSelectedItem();

		if (item_SalesOffIsActive != null) {
			final int searchOpId = ((SearchOperators) item_SalesOffIsActive
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.salesOffIsActive.isChecked()) {
					so.addFilter(new Filter("salesOffIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("salesOffIsActive", 0, searchOpId));
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
					so.addFilter(new Filter("recordType", selectedValue,
							searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("SalesOffCode", false);

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
		this.salesOfficerCtrl.setSearchObj(so);

		final Listbox listBox = this.salesOfficerCtrl.listBoxSalesOfficer;
		final Paging paging = this.salesOfficerCtrl.pagingSalesOfficerList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<SalesOfficer>) listBox.getModel()).init(so, listBox,
				paging);
		this.salesOfficerCtrl.setSearchObj(so);

		this.label_SalesOfficerSearchResult.setValue(Labels
				.getLabel("label_SalesOfficerSearchResult.value")
				+ " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}