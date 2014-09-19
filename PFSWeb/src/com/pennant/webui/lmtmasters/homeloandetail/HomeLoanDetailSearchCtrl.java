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
 * FileName    		:  HomeLoanDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.homeloandetail;

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
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class HomeLoanDetailSearchCtrl extends GFCBaseCtrl implements Serializable {
	
	private static final long serialVersionUID = 6807258492599013165L;
	private final static Logger logger = Logger.getLogger(HomeLoanDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_HomeLoanDetailSearch; // autowired
	
	protected Textbox loanRefNumber; 				// autowired
	protected Listbox sortOperator_loanRefNumber; 	// autowired
	protected Checkbox loanRefType; 				// autowired
	protected Listbox sortOperator_loanRefType; 	// autowired
	protected Textbox homeDetails; 					// autowired
	protected Listbox sortOperator_homeDetails; 	// autowired
	protected Textbox homeBuilderName; 				// autowired
	protected Listbox sortOperator_homeBuilderName; // autowired
	protected Textbox homeCostPerFlat; 				// autowired
	protected Listbox sortOperator_homeCostPerFlat; // autowired
	protected Textbox recordStatus; 				// autowired
	protected Listbox recordType;					// autowired
	protected Listbox sortOperator_recordStatus; 	// autowired
	protected Listbox sortOperator_recordType; 		// autowired
	
	protected Label label_HomeLoanDetailSearch_RecordStatus; // autowired
	protected Label label_HomeLoanDetailSearch_RecordType; 	 // autowired
	protected Label label_HomeLoanDetailSearchResult; 		 // autowired

	// not auto wired vars
	private transient HomeLoanDetailListCtrl homeLoanDetailCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("HomeLoanDetail");
	
	/**
	 * constructor
	 */
	public HomeLoanDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected HomeLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HomeLoanDetailSearch(Event event) throws Exception {
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

		if (args.containsKey("homeLoanDetailCtrl")) {
			this.homeLoanDetailCtrl = (HomeLoanDetailListCtrl) args
					.get("homeLoanDetailCtrl");
		} else {
			this.homeLoanDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_loanRefNumber.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_loanRefNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_loanRefType.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_loanRefType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_homeDetails.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_homeDetails.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_homeBuilderName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_homeBuilderName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_homeCostPerFlat.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_homeCostPerFlat.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_HomeLoanDetailSearch_RecordStatus.setVisible(false);
			this.label_HomeLoanDetailSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<HomeLoanDetail> searchObj = (JdbcSearchObject<HomeLoanDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("loanRefNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefNumber, filter);
					this.loanRefNumber.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("loanRefType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefType, filter);
					this.loanRefType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("homeDetails")) {
					SearchOperators.restoreStringOperator(this.sortOperator_homeDetails, filter);
					this.homeDetails.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("homeBuilderName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_homeBuilderName, filter);
					this.homeBuilderName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("homeCostPerFlat")) {
					SearchOperators.restoreStringOperator(this.sortOperator_homeCostPerFlat, filter);
					this.homeCostPerFlat.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue()
								.equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showHomeLoanDetailSeekDialog();
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
		this.window_HomeLoanDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showHomeLoanDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_HomeLoanDetailSearch.doModal();
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
		final JdbcSearchObject<HomeLoanDetail> so = new JdbcSearchObject<HomeLoanDetail>(
				HomeLoanDetail.class);
		so.addTabelName("LMTHomeLoanDetail_View");

		if (isWorkFlowEnabled()) {
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}

		if (StringUtils.isNotEmpty(this.loanRefNumber.getValue())) {

			// get the search operator
			final Listitem itemLoanRefNumber = this.sortOperator_loanRefNumber.getSelectedItem();
			if (itemLoanRefNumber != null) {
				final int searchOpId = ((SearchOperators) itemLoanRefNumber
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("loanRefNumber",
							"%" + this.loanRefNumber.getValue().toUpperCase()
									+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("loanRefNumber", this.loanRefNumber
							.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemLoanRefType = this.sortOperator_loanRefType.getSelectedItem();
		if (itemLoanRefType != null) {
			final int searchOpId = ((SearchOperators) itemLoanRefType
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.loanRefType.isChecked()) {
					so.addFilter(new Filter("loanRefType", 1, searchOpId));
				} else {
					so.addFilter(new Filter("loanRefType", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.homeDetails.getValue())) {

			// get the search operator
			final Listitem itemHomeDetails = this.sortOperator_homeDetails.getSelectedItem();
			if (itemHomeDetails != null) {
				final int searchOpId = ((SearchOperators) itemHomeDetails
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("homeDetails", "%"
							+ this.homeDetails.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("homeDetails", this.homeDetails
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.homeBuilderName.getValue())) {

			// get the search operator
			final Listitem itemHomeBuilderName = this.sortOperator_homeBuilderName.getSelectedItem();
			if (itemHomeBuilderName != null) {
				final int searchOpId = ((SearchOperators) itemHomeBuilderName
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("homeBuilderName", "%"
							+ this.homeBuilderName.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("homeBuilderName",
							this.homeBuilderName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.homeCostPerFlat.getValue())) {

			// get the search operator
			final Listitem itemHomeCostPerFlat = this.sortOperator_homeCostPerFlat.getSelectedItem();
			if (itemHomeCostPerFlat != null) {
				final int searchOpId = ((SearchOperators) itemHomeCostPerFlat
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("homeCostPerFlat", "%"
							+ this.homeCostPerFlat.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("homeCostPerFlat",
							this.homeCostPerFlat.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType != null) {
				final int searchOpId = ((SearchOperators) itemRecordType
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
		so.addSort("HomeLoanId", false);

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
		this.homeLoanDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.homeLoanDetailCtrl.listBoxHomeLoanDetail;
		final Paging paging = this.homeLoanDetailCtrl.pagingHomeLoanDetailList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<HomeLoanDetail>) listBox.getModel()).init(so,
				listBox, paging);

		this.label_HomeLoanDetailSearchResult.setValue(Labels
				.getLabel("label_HomeLoanDetailSearchResult.value")
				+ " "+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}