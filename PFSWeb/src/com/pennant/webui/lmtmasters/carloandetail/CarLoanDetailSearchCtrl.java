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
 * FileName    		:  CarLoanDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.carloandetail;

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
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class CarLoanDetailSearchCtrl extends GFCBaseCtrl implements Serializable {
	
	private static final long serialVersionUID = 3198252711176479319L;
	private final static Logger logger = Logger.getLogger(CarLoanDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CarLoanDetailSearch;   // autowired
	
	protected Textbox loanRefNumber; 				// autowired
	protected Listbox sortOperator_loanRefNumber; 	// autowired
	protected Textbox carLoanFor; 					// autowired
	protected Listbox sortOperator_carLoanFor; 		// autowired
	protected Textbox carVersion; 					// autowired
	protected Listbox sortOperator_carVersion; 		// autowired
	protected Textbox carMakeYear; 					// autowired
	protected Listbox sortOperator_carMakeYear; 	// autowired
	protected Textbox carDealer; 					// autowired
	protected Listbox sortOperator_carDealer; 		// autowired
	protected Textbox recordStatus; 				// autowired
	protected Listbox recordType;					// autowired
	protected Listbox sortOperator_recordStatus; 	// autowired
	protected Listbox sortOperator_recordType; 		// autowired
	
	protected Label label_CarLoanDetailSearch_RecordStatus; // autowired
	protected Label label_CarLoanDetailSearch_RecordType; 	// autowired
	protected Label label_CarLoanDetailSearchResult; 		// autowired

	// not auto wired vars
	private transient CarLoanDetailListCtrl carLoanDetailCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CarLoanDetail");
	
	/**
	 * constructor
	 */
	public CarLoanDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CarLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CarLoanDetailSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("carLoanDetailCtrl")) {
			this.carLoanDetailCtrl = (CarLoanDetailListCtrl) args.get("carLoanDetailCtrl");
		} else {
			this.carLoanDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_loanRefNumber.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_loanRefNumber.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_carLoanFor.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_carLoanFor.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_carVersion.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_carVersion.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_carMakeYear.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_carMakeYear.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_carDealer.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_carDealer.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CarLoanDetailSearch_RecordStatus.setVisible(false);
			this.label_CarLoanDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CarLoanDetail> searchObj = (JdbcSearchObject<CarLoanDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("loanRefNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefNumber, filter);
					this.loanRefNumber.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("carLoanFor")) {
					SearchOperators.restoreStringOperator(this.sortOperator_carLoanFor, filter);
					this.carLoanFor.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("carVersion")) {
					SearchOperators.restoreStringOperator(this.sortOperator_carVersion, filter);
					this.carVersion.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("carMakeYear")) {
					SearchOperators.restoreStringOperator(this.sortOperator_carMakeYear, filter);
					this.carMakeYear.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("carDealer")) {
					SearchOperators.restoreStringOperator(this.sortOperator_carDealer, filter);
					this.carDealer.setValue(filter.getValue().toString());
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
		showCarLoanDetailSeekDialog();
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering");
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
		this.window_CarLoanDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCarLoanDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_CarLoanDetailSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
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
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering ");
		final JdbcSearchObject<CarLoanDetail> so = new JdbcSearchObject<CarLoanDetail>(
				CarLoanDetail.class);
		
		if (isWorkFlowEnabled()){
			so.addTabelName("LMTCarLoanDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("LMTCarLoanDetail_AView");
		}
		
		if (StringUtils.isNotEmpty(this.loanRefNumber.getValue())) {

			// get the search operator
			final Listitem itemLoanRefNumber = this.sortOperator_loanRefNumber.getSelectedItem();
			if (itemLoanRefNumber != null) {
				final int searchOpId = ((SearchOperators) itemLoanRefNumber
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("loanRefNumber",
							"%" + this.loanRefNumber.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("loanRefNumber", this.loanRefNumber
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.carLoanFor.getValue())) {

			// get the search operator
			final Listitem itemCarLoanFor = this.sortOperator_carLoanFor.getSelectedItem();
			if (itemCarLoanFor != null) {
				final int searchOpId = ((SearchOperators) itemCarLoanFor
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("carLoanFor", "%"
							+ this.carLoanFor.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("carLoanFor", this.carLoanFor.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.carVersion.getValue())) {

			// get the search operator
			final Listitem itemCarVersion = this.sortOperator_carVersion.getSelectedItem();
			if (itemCarVersion != null) {
				final int searchOpId = ((SearchOperators) itemCarVersion
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("carVersion", "%"
							+ this.carVersion.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("carVersion", this.carVersion.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.carMakeYear.getValue())) {

			// get the search operator
			final Listitem itemCarMakeYear = this.sortOperator_carMakeYear.getSelectedItem();
			if (itemCarMakeYear != null) {
				final int searchOpId = ((SearchOperators) itemCarMakeYear
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("carMakeYear", "%"
							+ this.carMakeYear.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("carMakeYear", this.carMakeYear.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.carDealer.getValue())) {

			// get the search operator
			final Listitem itemCarDealer = this.sortOperator_carDealer.getSelectedItem();
			if (itemCarDealer != null) {
				final int searchOpId = ((SearchOperators) itemCarDealer
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("carDealer", "%"
							+ this.carDealer.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("carDealer", this.carDealer.getValue(), searchOpId));
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
							+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType
						.getAttribute("data")).getSearchOperatorId();
	
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
		so.addSort("CarLoanId", false);

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
		this.carLoanDetailCtrl.setSearchObj(so);
		final Listbox listBox = this.carLoanDetailCtrl.listBoxCarLoanDetail;
		final Paging paging = this.carLoanDetailCtrl.pagingCarLoanDetailList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<CarLoanDetail>) listBox.getModel()).init(so, listBox, paging);
		this.label_CarLoanDetailSearchResult.setValue(Labels.getLabel(
				"label_CarLoanDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

}