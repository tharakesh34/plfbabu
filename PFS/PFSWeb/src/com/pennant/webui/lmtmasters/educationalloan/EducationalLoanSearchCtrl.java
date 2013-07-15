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
 * FileName    		:  EducationalLoanSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.educationalloan;

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
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class EducationalLoanSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2860434268181881196L;
	private final static Logger logger = Logger.getLogger(EducationalLoanSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_EducationalLoanSearch;          // autowired
	protected Textbox eduLoanId;                            // autowired
	protected Listbox sortOperator_eduLoanId;               // autowired
	protected Textbox loanRefNumber;                        // autowired
	protected Listbox sortOperator_loanRefNumber;           // autowired
	protected Checkbox loanRefType;                         // autowired
	protected Listbox sortOperator_loanRefType;             // autowired
	protected Textbox eduCourse;                            // autowired
	protected Listbox sortOperator_eduCourse;               // autowired
	protected Textbox eduSpecialization;                    // autowired
	protected Listbox sortOperator_eduSpecialization;       // autowired
	protected Textbox eduCourseType;                        // autowired
	protected Listbox sortOperator_eduCourseType;           // autowired
	protected Textbox eduCourseFrom;                        // autowired
	protected Listbox sortOperator_eduCourseFrom;           // autowired
	protected Textbox eduCourseFromBranch;                  // autowired
	protected Listbox sortOperator_eduCourseFromBranch;     // autowired
	protected Textbox eduAffiliatedTo;                      // autowired
	protected Listbox sortOperator_eduAffiliatedTo;         // autowired
	protected Textbox eduCommenceDate;                      // autowired
	protected Listbox sortOperator_eduCommenceDate;         // autowired
	protected Textbox eduCompletionDate;                    // autowired
	protected Listbox sortOperator_eduCompletionDate;       // autowired
	protected Textbox eduExpectedIncome;                    // autowired
	protected Listbox sortOperator_eduExpectedIncome;       // autowired
	protected Textbox eduLoanFromBranch;                    // autowired
	protected Listbox sortOperator_eduLoanFromBranch;       // autowired
	protected Textbox recordStatus;                         // autowired
	protected Listbox recordType;	                        // autowired
	protected Listbox sortOperator_recordStatus;            // autowired
	protected Listbox sortOperator_recordType;              // autowired
	protected Label label_EducationalLoanSearch_RecordStatus; // autowired
	protected Label label_EducationalLoanSearch_RecordType;   // autowired
	protected Label label_EducationalLoanSearchResult;        // autowired

	// not auto wired vars
	private transient EducationalLoanListCtrl educationalLoanCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("EducationalLoan");

	/**
	 * constructor
	 */
	public EducationalLoanSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EducationalLoanSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("educationalLoanCtrl")) {
			this.educationalLoanCtrl = (EducationalLoanListCtrl) args.get("educationalLoanCtrl");
		} else {
			this.educationalLoanCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_eduLoanId.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduLoanId.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_loanRefNumber.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_loanRefNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_loanRefType.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_loanRefType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduCourse.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduCourse.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduSpecialization.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduSpecialization.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduCourseType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduCourseType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduCourseFrom.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduCourseFrom.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduCourseFromBranch.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduCourseFromBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduAffiliatedTo.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduAffiliatedTo.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduCommenceDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduCommenceDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduCompletionDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduCompletionDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduExpectedIncome.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduExpectedIncome.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eduLoanFromBranch.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_eduLoanFromBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_EducationalLoanSearch_RecordStatus.setVisible(false);
			this.label_EducationalLoanSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<EducationalLoan> searchObj = (JdbcSearchObject<EducationalLoan>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("eduLoanId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduLoanId, filter);
					this.eduLoanId.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("loanRefNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefNumber, filter);
					this.loanRefNumber.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("loanRefType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefType, filter);
					this.loanRefType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduCourse")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduCourse, filter);
					this.eduCourse.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduSpecialization")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduSpecialization, filter);
					this.eduSpecialization.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduCourseType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduCourseType, filter);
					this.eduCourseType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduCourseFrom")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduCourseFrom, filter);
					this.eduCourseFrom.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduCourseFromBranch")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduCourseFromBranch, filter);
					this.eduCourseFromBranch.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduAffiliatedTo")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduAffiliatedTo, filter);
					this.eduAffiliatedTo.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduCommenceDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduCommenceDate, filter);
					this.eduCommenceDate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduCompletionDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduCompletionDate, filter);
					this.eduCompletionDate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduExpectedIncome")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduExpectedIncome, filter);
					this.eduExpectedIncome.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("eduLoanFromBranch")) {
					SearchOperators.restoreStringOperator(this.sortOperator_eduLoanFromBranch, filter);
					this.eduLoanFromBranch.setValue(filter.getValue().toString());
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
		showEducationalLoanSeekDialog();
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
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering ");
		this.window_EducationalLoanSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showEducationalLoanSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_EducationalLoanSearch.doModal();
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
		
		final JdbcSearchObject<EducationalLoan> so = new JdbcSearchObject<EducationalLoan>(
				EducationalLoan.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("LMTEducationLoanDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("LMTEducationLoanDetail_AView");
		}

		if (StringUtils.isNotEmpty(this.eduLoanId.getValue())) {

			// get the search operator
			final Listitem item_EduLoanId = this.sortOperator_eduLoanId.getSelectedItem();

			if (item_EduLoanId != null) {
				final int searchOpId = ((SearchOperators) item_EduLoanId.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduLoanId", "%" + this.eduLoanId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduLoanId", this.eduLoanId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.loanRefNumber.getValue())) {

			// get the search operator
			final Listitem item_LoanRefNumber = this.sortOperator_loanRefNumber.getSelectedItem();

			if (item_LoanRefNumber != null) {
				final int searchOpId = ((SearchOperators) item_LoanRefNumber.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("loanRefNumber", "%" +
							this.loanRefNumber.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("loanRefNumber", this.loanRefNumber.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_LoanRefType = this.sortOperator_loanRefType.getSelectedItem();

		if (item_LoanRefType != null) {
			final int searchOpId = ((SearchOperators) item_LoanRefType.getAttribute(
					"data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.loanRefType.isChecked()){
					so.addFilter(new Filter("loanRefType",1, searchOpId));
				}else{
					so.addFilter(new Filter("loanRefType",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduCourse.getValue())) {

			// get the search operator
			final Listitem item_EduCourse = this.sortOperator_eduCourse.getSelectedItem();

			if (item_EduCourse != null) {
				final int searchOpId = ((SearchOperators) item_EduCourse.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduCourse", "%" +
							this.eduCourse.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduCourse", this.eduCourse.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduSpecialization.getValue())) {

			// get the search operator
			final Listitem item_EduSpecialization = this.sortOperator_eduSpecialization.getSelectedItem();

			if (item_EduSpecialization != null) {
				final int searchOpId = ((SearchOperators) item_EduSpecialization.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduSpecialization", "%" + 
							this.eduSpecialization.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduSpecialization", 
							this.eduSpecialization.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduCourseType.getValue())) {

			// get the search operator
			final Listitem item_EduCourseType = this.sortOperator_eduCourseType.getSelectedItem();

			if (item_EduCourseType != null) {
				final int searchOpId = ((SearchOperators) item_EduCourseType.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduCourseType", "%" + 
							this.eduCourseType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduCourseType", this.eduCourseType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduCourseFrom.getValue())) {

			// get the search operator
			final Listitem item_EduCourseFrom = this.sortOperator_eduCourseFrom.getSelectedItem();

			if (item_EduCourseFrom != null) {
				final int searchOpId = ((SearchOperators) item_EduCourseFrom.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduCourseFrom", "%" +
							this.eduCourseFrom.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduCourseFrom", this.eduCourseFrom.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduCourseFromBranch.getValue())) {

			// get the search operator
			final Listitem item_EduCourseFromBranch = this.sortOperator_eduCourseFromBranch.getSelectedItem();

			if (item_EduCourseFromBranch != null) {
				final int searchOpId = ((SearchOperators) item_EduCourseFromBranch.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduCourseFromBranch", "%" + 
							this.eduCourseFromBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduCourseFromBranch",
							this.eduCourseFromBranch.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduAffiliatedTo.getValue())) {

			// get the search operator
			final Listitem item_EduAffiliatedTo = this.sortOperator_eduAffiliatedTo.getSelectedItem();

			if (item_EduAffiliatedTo != null) {
				final int searchOpId = ((SearchOperators) item_EduAffiliatedTo.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduAffiliatedTo", "%" + 
							this.eduAffiliatedTo.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduAffiliatedTo", this.eduAffiliatedTo.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduCommenceDate.getValue())) {

			// get the search operator
			final Listitem item_EduCommenceDate = this.sortOperator_eduCommenceDate.getSelectedItem();

			if (item_EduCommenceDate != null) {
				final int searchOpId = ((SearchOperators) item_EduCommenceDate.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduCommenceDate", "%" + 
							this.eduCommenceDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduCommenceDate", this.eduCommenceDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduCompletionDate.getValue())) {

			// get the search operator
			final Listitem item_EduCompletionDate = this.sortOperator_eduCompletionDate.getSelectedItem();

			if (item_EduCompletionDate != null) {
				final int searchOpId = ((SearchOperators) item_EduCompletionDate.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduCompletionDate", "%" + 
							this.eduCompletionDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduCompletionDate",
							this.eduCompletionDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduExpectedIncome.getValue())) {

			// get the search operator
			final Listitem item_EduExpectedIncome = this.sortOperator_eduExpectedIncome.getSelectedItem();

			if (item_EduExpectedIncome != null) {
				final int searchOpId = ((SearchOperators) item_EduExpectedIncome.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduExpectedIncome", "%" +
							this.eduExpectedIncome.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduExpectedIncome", 
							this.eduExpectedIncome.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.eduLoanFromBranch.getValue())) {

			// get the search operator
			final Listitem item_EduLoanFromBranch = this.sortOperator_eduLoanFromBranch.getSelectedItem();

			if (item_EduLoanFromBranch != null) {
				final int searchOpId = ((SearchOperators) item_EduLoanFromBranch.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("eduLoanFromBranch", "%" +
							this.eduLoanFromBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("eduLoanFromBranch",
							this.eduLoanFromBranch.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" +
							this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + 
							selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("EduLoanId", false);

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
		this.educationalLoanCtrl.setSearchObj(so);

		final Listbox listBox = this.educationalLoanCtrl.listBoxEducationalLoan;
		final Paging paging = this.educationalLoanCtrl.pagingEducationalLoanList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<EducationalLoan>) listBox.getModel()).init(so, listBox, paging);
		this.label_EducationalLoanSearchResult.setValue(Labels.getLabel(
				"label_EducationalLoanSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

}