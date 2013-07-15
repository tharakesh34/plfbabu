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
 * FileName    		:  EducationalExpenseSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.educationalexpense;

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
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.service.lmtmasters.EducationalExpenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class EducationalExpenseSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(EducationalExpenseSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_EducationalExpenseSearch; // autowired
	
	protected Textbox eduExpDetailId; // autowired
	protected Listbox sortOperator_eduExpDetailId; // autowired
	protected Textbox eduLoanId; // autowired
	protected Listbox sortOperator_eduLoanId; // autowired
	protected Textbox eduExpDetail; // autowired
	protected Listbox sortOperator_eduExpDetail; // autowired
	protected Textbox eduExpAmount; // autowired
	protected Listbox sortOperator_eduExpAmount; // autowired
	protected Textbox eduExpDate; // autowired
	protected Listbox sortOperator_eduExpDate; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_EducationalExpenseSearch_RecordStatus; // autowired
	protected Label label_EducationalExpenseSearch_RecordType; // autowired
	protected Label label_EducationalExpenseSearchResult; // autowired

	// not auto wired vars
	private transient EducationalExpenseListCtrl educationalExpenseCtrl; // overhanded per param
	private transient EducationalExpenseService educationalExpenseService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("EducationalExpense");
	
	/**
	 * constructor
	 */
	public EducationalExpenseSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_EducationalExpenseSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("educationalExpenseCtrl")) {
			this.educationalExpenseCtrl = (EducationalExpenseListCtrl) args.get("educationalExpenseCtrl");
		} else {
			this.educationalExpenseCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_eduExpDetailId.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduExpDetailId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_eduLoanId.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduLoanId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_eduExpDetail.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduExpDetail.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_eduExpAmount.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduExpAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_eduExpDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduExpDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_EducationalExpenseSearch_RecordStatus.setVisible(false);
			this.label_EducationalExpenseSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<EducationalExpense> searchObj = (JdbcSearchObject<EducationalExpense>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("eduExpDetailId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduExpDetailId, filter);
					this.eduExpDetailId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("eduLoanId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduLoanId, filter);
					this.eduLoanId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("eduExpDetail")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduExpDetail, filter);
					this.eduExpDetail.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("eduExpAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduExpAmount, filter);
					this.eduExpAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("eduExpDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduExpDate, filter);
					this.eduExpDate.setValue(filter.getValue().toString());
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
		showEducationalExpenseSeekDialog();
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
		logger.debug(event.toString());
		doSearch();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		this.window_EducationalExpenseSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showEducationalExpenseSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_EducationalExpenseSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {

		final JdbcSearchObject<EducationalExpense> so = new JdbcSearchObject<EducationalExpense>(EducationalExpense.class);
		so.addTabelName("LMTEduExpenseDetail_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		
		if (StringUtils.isNotEmpty(this.eduExpDetailId.getValue())) {

			// get the search operator
			final Listitem item_EduExpDetailId = this.sortOperator_eduExpDetailId.getSelectedItem();

			if (item_EduExpDetailId != null) {
				final int searchOpId = ((SearchOperators) item_EduExpDetailId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduExpDetailId", "%" + this.eduExpDetailId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduExpDetailId", this.eduExpDetailId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduLoanId.getValue())) {

			// get the search operator
			final Listitem item_EduLoanId = this.sortOperator_eduLoanId.getSelectedItem();

			if (item_EduLoanId != null) {
				final int searchOpId = ((SearchOperators) item_EduLoanId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduLoanId", "%" + this.eduLoanId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduLoanId", this.eduLoanId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduExpDetail.getValue())) {

			// get the search operator
			final Listitem item_EduExpDetail = this.sortOperator_eduExpDetail.getSelectedItem();

			if (item_EduExpDetail != null) {
				final int searchOpId = ((SearchOperators) item_EduExpDetail.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduExpDetail", "%" + this.eduExpDetail.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduExpDetail", this.eduExpDetail.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduExpAmount.getValue())) {

			// get the search operator
			final Listitem item_EduExpAmount = this.sortOperator_eduExpAmount.getSelectedItem();

			if (item_EduExpAmount != null) {
				final int searchOpId = ((SearchOperators) item_EduExpAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduExpAmount", "%" + this.eduExpAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduExpAmount", this.eduExpAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduExpDate.getValue())) {

			// get the search operator
			final Listitem item_EduExpDate = this.sortOperator_eduExpDate.getSelectedItem();

			if (item_EduExpDate != null) {
				final int searchOpId = ((SearchOperators) item_EduExpDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduExpDate", "%" + this.eduExpDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduExpDate", this.eduExpDate.getValue(), searchOpId));
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
		
		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
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
		// Defualt Sort on the table
		so.addSort("EduExpDetailId", false);

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
		this.educationalExpenseCtrl.setSearchObj(so);

		final Listbox listBox = this.educationalExpenseCtrl.listBoxEducationalExpense;
		final Paging paging = this.educationalExpenseCtrl.pagingEducationalExpenseList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<EducationalExpense>) listBox.getModel()).init(so, listBox, paging);
		this.educationalExpenseCtrl.setSearchObj(so);

		this.label_EducationalExpenseSearchResult.setValue(Labels.getLabel("label_EducationalExpenseSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setEducationalExpenseService(EducationalExpenseService educationalExpenseService) {
		this.educationalExpenseService = educationalExpenseService;
	}

	public EducationalExpenseService getEducationalExpenseService() {
		return this.educationalExpenseService;
	}
}