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
 * FileName    		:  BranchSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.branch;

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
import com.pennant.backend.model.applicationmaster.Branch;
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
 * /WEB-INF/pages/ApplicationMaster/Branch/BranchSearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class BranchSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long   serialVersionUID = -4077708415959825063L;
	private final static Logger logger = Logger.getLogger(BranchSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_BranchSearch; 				// auto wired
	
	protected Textbox 	branchCode; 						// auto wired
	protected Listbox 	sortOperator_branchCode; 			// auto wired
	protected Textbox 	branchDesc; 						// auto wired
	protected Listbox 	sortOperator_branchDesc; 			// auto wired
	protected Textbox 	branchCity; 						// auto wired
	protected Listbox 	sortOperator_branchCity; 			// auto wired
	protected Textbox 	branchProvince; 					// auto wired
	protected Listbox 	sortOperator_branchProvince; 		// auto wired
	protected Textbox 	branchCountry; 						// auto wired
	protected Listbox 	sortOperator_branchCountry; 		// auto wired
	protected Textbox 	branchSwiftBankCde; 				// auto wired
	protected Listbox 	sortOperator_branchSwiftBankCde; 	// auto wired
	protected Textbox 	branchSwiftCountry; 				// auto wired
	protected Listbox 	sortOperator_branchSwiftCountry; 	// auto wired
	protected Textbox 	branchSwiftLocCode; 				// auto wired
	protected Listbox 	sortOperator_branchSwiftLocCode; 	// auto wired
	protected Textbox 	branchSwiftBrnCde; 					// auto wired
	protected Listbox 	sortOperator_branchSwiftBrnCde; 	// auto wired
	protected Textbox 	branchSortCode; 					// auto wired
	protected Listbox 	sortOperator_branchSortCode; 		// auto wired
	protected Checkbox 	branchIsActive; 					// auto wired
	protected Listbox 	sortOperator_branchIsActive; 		// auto wired
	protected Textbox 	recordStatus; 						// auto wired
	protected Listbox 	recordType;							// auto wired
	protected Listbox 	sortOperator_recordStatus; 			// auto wired
	protected Listbox 	sortOperator_recordType; 			// auto wired
	
	protected Label label_BranchSearch_RecordStatus; 	// auto wired
	protected Label label_BranchSearch_RecordType; 		// auto wired
	protected Label label_BranchSearchResult; 			// auto wired

	// not auto wired Var's
	private transient BranchListCtrl 	branchCtrl; // over handed per parameter
	private transient WorkFlowDetails 	workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("Branch");
	
	/**
	 * constructor
	 */
	public BranchSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Branch object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_BranchSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("branchCtrl")) {
			this.branchCtrl = (BranchListCtrl) args.get("branchCtrl");
		} else {
			this.branchCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_branchCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchDesc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchCity.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchCity.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchProvince.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchProvince.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchCountry.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchCountry.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchSwiftBankCde.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchSwiftBankCde.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchSwiftCountry.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchSwiftCountry.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchSwiftLocCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchSwiftLocCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchSwiftBrnCde.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchSwiftBrnCde.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchSortCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_branchSortCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchIsActive.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_branchIsActive.setItemRenderer(
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
			this.label_BranchSearch_RecordStatus.setVisible(false);
			this.label_BranchSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<Branch> searchObj = (JdbcSearchObject<Branch>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("branchCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchCode, filter);
					this.branchCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branchDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchDesc, filter);
					this.branchDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branchCity")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchCity, filter);
					this.branchCity.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branchProvince")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchProvince, filter);
					this.branchProvince.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branchCountry")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchCountry, filter);
					this.branchCountry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branchSwiftBankCde")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchSwiftBankCde, filter);
					this.branchSwiftBankCde.setValue(filter.getValue()
							.toString());
				} else if (filter.getProperty().equals("branchSwiftCountry")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchSwiftCountry, filter);
					this.branchSwiftCountry.setValue(filter.getValue()
							.toString());
				} else if (filter.getProperty().equals("branchSwiftLocCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchSwiftLocCode, filter);
					this.branchSwiftLocCode.setValue(filter.getValue()
							.toString());
				} else if (filter.getProperty().equals("branchSwiftBrnCde")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchSwiftBrnCde, filter);
					this.branchSwiftBrnCde.setValue(filter.getValue()
							.toString());
				} else if (filter.getProperty().equals("branchSortCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchSortCode, filter);
					this.branchSortCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branchIsActive")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_branchIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.branchIsActive.setChecked(true);
					}else{
						this.branchIsActive.setChecked(false);
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
		showBranchSeekDialog();
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
		this.window_BranchSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showBranchSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_BranchSearch.doModal();
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
		final JdbcSearchObject<Branch> so = new JdbcSearchObject<Branch>(
				Branch.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTBranches_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		} else {
			so.addTabelName("RMTBranches_AView");
		}

		if (StringUtils.isNotEmpty(this.branchCode.getValue())) {

			// get the search operator
			final Listitem item_BranchCode = this.sortOperator_branchCode
					.getSelectedItem();

			if (item_BranchCode != null) {
				final int searchOpId = ((SearchOperators) item_BranchCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchCode", "%"
						+ this.branchCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchCode", this.branchCode
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchDesc.getValue())) {

			// get the search operator
			final Listitem item_BranchDesc = this.sortOperator_branchDesc
					.getSelectedItem();

			if (item_BranchDesc != null) {
				final int searchOpId = ((SearchOperators) item_BranchDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchDesc", "%"
						+ this.branchDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchDesc", this.branchDesc
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchCity.getValue())) {

			// get the search operator
			final Listitem item_BranchCity = this.sortOperator_branchCity
					.getSelectedItem();

			if (item_BranchCity != null) {
				final int searchOpId = ((SearchOperators) item_BranchCity
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchCity", "%"
						+ this.branchCity.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchCity", this.branchCity
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchProvince.getValue())) {

			// get the search operator
			final Listitem item_BranchProvince = this.sortOperator_branchProvince
					.getSelectedItem();

			if (item_BranchProvince != null) {
				final int searchOpId = ((SearchOperators) item_BranchProvince
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchProvince", "%"
							+ this.branchProvince.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchProvince",
							this.branchProvince.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchCountry.getValue())) {

			// get the search operator
			final Listitem item_BranchCountry = this.sortOperator_branchCountry
					.getSelectedItem();

			if (item_BranchCountry != null) {
				final int searchOpId = ((SearchOperators) item_BranchCountry
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchCountry",
							"%" + this.branchCountry.getValue().toUpperCase()
									+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchCountry", this.branchCountry
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchSwiftBankCde.getValue())) {

			// get the search operator
			final Listitem item_BranchSwiftBankCde = this.sortOperator_branchSwiftBankCde
					.getSelectedItem();

			if (item_BranchSwiftBankCde != null) {
				final int searchOpId = ((SearchOperators) item_BranchSwiftBankCde
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchSwiftBankCde", "%"
							+ this.branchSwiftBankCde.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchSwiftBankCde",
							this.branchSwiftBankCde.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchSwiftCountry.getValue())) {

			// get the search operator
			final Listitem item_BranchSwiftCountry = this.sortOperator_branchSwiftCountry
					.getSelectedItem();

			if (item_BranchSwiftCountry != null) {
				final int searchOpId = ((SearchOperators) item_BranchSwiftCountry
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchSwiftCountry", "%"
							+ this.branchSwiftCountry.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchSwiftCountry",
							this.branchSwiftCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchSwiftLocCode.getValue())) {

			// get the search operator
			final Listitem item_BranchSwiftLocCode = this.sortOperator_branchSwiftLocCode
					.getSelectedItem();

			if (item_BranchSwiftLocCode != null) {
				final int searchOpId = ((SearchOperators) item_BranchSwiftLocCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchSwiftLocCode", "%"
							+ this.branchSwiftLocCode.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchSwiftLocCode",
							this.branchSwiftLocCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchSwiftBrnCde.getValue())) {

			// get the search operator
			final Listitem item_BranchSwiftBrnCde = this.sortOperator_branchSwiftBrnCde
					.getSelectedItem();

			if (item_BranchSwiftBrnCde != null) {
				final int searchOpId = ((SearchOperators) item_BranchSwiftBrnCde
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchSwiftBrnCde", "%"
							+ this.branchSwiftBrnCde.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchSwiftBrnCde",
							this.branchSwiftBrnCde.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.branchSortCode.getValue())) {

			// get the search operator
			final Listitem item_BranchSortCode = this.sortOperator_branchSortCode
					.getSelectedItem();

			if (item_BranchSortCode != null) {
				final int searchOpId = ((SearchOperators) item_BranchSortCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("branchSortCode", "%"
							+ this.branchSortCode.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("branchSortCode",
							this.branchSortCode.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_BranchIsActive = this.sortOperator_branchIsActive
				.getSelectedItem();

		if (item_BranchIsActive != null) {
			final int searchOpId = ((SearchOperators) item_BranchIsActive
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.branchIsActive.isChecked()) {
					so.addFilter(new Filter("branchIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("branchIsActive", 0, searchOpId));
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
						+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("BranchCode", false);

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
		this.branchCtrl.setSearchObj(so);

		final Listbox listBox = this.branchCtrl.listBoxBranch;
		final Paging paging = this.branchCtrl.pagingBranchList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<Branch>) listBox.getModel()).init(so, listBox,paging);
		this.branchCtrl.setSearchObj(so);

		this.label_BranchSearchResult.setValue(Labels
				.getLabel("label_BranchSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}