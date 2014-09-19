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
 * FileName    		:  FinanceMainSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.investment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceMain;
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

public class DealFinanceSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4647934832219925649L;
	private final static Logger logger = Logger.getLogger(DealFinanceSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DealFinanceSearch; 			// autowired
	
	protected Textbox investmentRef; 					// autowired
	protected Textbox finReference; 					// autowired
	protected Listbox sortOperator_finReference; 		// autowired
	protected Listbox sortOperator_InvReference; 		// autowired
	protected Textbox finType; 							// autowired
	protected Listbox sortOperator_finType; 			// autowired
	protected Textbox finCcy; 							// autowired
	protected Listbox sortOperator_finCcy; 				// autowired
	protected Textbox scheduleMethod; 					// autowired
	protected Listbox sortOperator_scheduleMethod; 		// autowired
	protected Textbox profitDaysBasis; 					// autowired
	protected Listbox sortOperator_profitDaysBasis; 	// autowired
	protected Datebox finStartDate; 					// autowired
	protected Listbox sortOperator_finStartDate; 		// autowired
	protected Decimalbox finAmount; 					// autowired
	protected Listbox sortOperator_finAmount; 			// autowired
	protected Textbox custID; 							// autowired
	protected Listbox sortOperator_custID; 				// autowired
	protected Checkbox finIsActive; 					// autowired
	protected Listbox sortOperator_finIsActive; 		// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus; 		// autowired
	protected Listbox sortOperator_recordType; 			// autowired
	
	protected Label label_FinanceMainSearch_RecordStatus; 	// autowired
	protected Label label_Inv_RefNo; 	// autowired
	protected Label label_FinanceMainSearchResult; 			// autowired

	// not auto wired vars
	private transient DealFinanceListCtrl financeMainCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinanceMain");
	
	/**
	 * constructor
	 */
	public DealFinanceSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DealFinanceSearch(final Event event) throws Exception {
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

		if (args.containsKey("financeMainCtrl")) {
			this.financeMainCtrl = (DealFinanceListCtrl) args.get("financeMainCtrl");
		} else {
			this.financeMainCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_InvReference.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_InvReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_finReference.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finCcy.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_scheduleMethod.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_scheduleMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_profitDaysBasis.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_profitDaysBasis.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finStartDate.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_finStartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finAmount.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_finAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_FinanceMainSearch_RecordStatus.setVisible(false);
			this.label_Inv_RefNo.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<FinanceMain> searchObj = (JdbcSearchObject<FinanceMain>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("InvestmentRef")) {
					SearchOperators.restoreStringOperator(this.sortOperator_InvReference, filter);
					this.investmentRef.setValue(filter.getValue().toString());
				}else if (filter.getProperty().equals("finReference")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finType, filter);
					this.finType.setValue(filter.getValue().toString());
				}  else if (filter.getProperty().equals("finCcy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finCcy, filter);
					this.finCcy.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("scheduleMethod")) {
					SearchOperators.restoreStringOperator(this.sortOperator_scheduleMethod, filter);
					this.scheduleMethod.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("profitDaysBasis")) {
					SearchOperators.restoreStringOperator(this.sortOperator_profitDaysBasis, filter);
					this.profitDaysBasis.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finStartDate")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_finStartDate, filter);
					this.finStartDate.setValue(DateUtility.getUtilDate(
							filter.getValue().toString(),PennantConstants.DBDateFormat));
				} else if (filter.getProperty().equals("finAmount")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_finAmount, filter);
					this.finAmount.setValue(new BigDecimal(filter.getValue().toString()));
				} else if (filter.getProperty().equals("custID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custID, filter);
					this.custID.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finIsActive")) {
					SearchOperators.restoreBooleanOperators(this.sortOperator_finIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsActive.setChecked(true);
					}else{
						this.finIsActive.setChecked(false);
					}
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
		showFinanceMainSeekDialog();
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
	public void onClick$btnSearch(final Event event) {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(final Event event) throws InterruptedException {
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
		this.window_DealFinanceSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceMainSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_DealFinanceSearch.doModal();
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
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");
		
		final JdbcSearchObject<FinanceMain> so = new JdbcSearchObject<FinanceMain>(FinanceMain.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("FinanceMain_TView");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FinanceMain_AView");
		}
		
		if (StringUtils.isNotEmpty(this.investmentRef.getValue())) {

			// get the search operator
			final Listitem itemInvReference = this.sortOperator_InvReference.getSelectedItem();
			if (itemInvReference != null) {
				final int searchOpId = ((SearchOperators) itemInvReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("InvestmentRef", "%" + this.investmentRef.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("InvestmentRef", this.investmentRef.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem itemFinReference = this.sortOperator_finReference.getSelectedItem();
			if (itemFinReference != null) {
				final int searchOpId = ((SearchOperators) itemFinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finReference", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finReference", this.finReference.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem itemFinType = this.sortOperator_finType.getSelectedItem();
			if (itemFinType != null) {
				final int searchOpId = ((SearchOperators) itemFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finType", this.finType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finCcy.getValue())) {

			// get the search operator
			final Listitem itemFinCcy = this.sortOperator_finCcy.getSelectedItem();
			if (itemFinCcy != null) {
				final int searchOpId = ((SearchOperators) itemFinCcy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finCcy", "%" + this.finCcy.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finCcy", this.finCcy.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.scheduleMethod.getValue())) {

			// get the search operator
			final Listitem itemScheduleMethod = this.sortOperator_scheduleMethod.getSelectedItem();
			if (itemScheduleMethod != null) {
				final int searchOpId = ((SearchOperators) itemScheduleMethod.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("scheduleMethod", "%" + this.scheduleMethod.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("scheduleMethod", this.scheduleMethod.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.profitDaysBasis.getValue())) {

			// get the search operator
			final Listitem itemProfitDaysBasis = this.sortOperator_profitDaysBasis.getSelectedItem();
			if (itemProfitDaysBasis != null) {
				final int searchOpId = ((SearchOperators) itemProfitDaysBasis.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("profitDaysBasis", "%" + this.profitDaysBasis.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("profitDaysBasis", this.profitDaysBasis.getValue(), searchOpId));
				}
			}
		}
		if (this.finStartDate.getValue() != null) {

			// get the search operator
			final Listitem itemFinStartDate = this.sortOperator_finStartDate.getSelectedItem();
			if (itemFinStartDate != null) {
				final int searchOpId = ((SearchOperators) itemFinStartDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finStartDate",DateUtility.formatUtilDate(
							this.finStartDate.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
		if (this.finAmount.getValue() != null) {

			// get the search operator
			final Listitem itemFinAmount = this.sortOperator_finAmount.getSelectedItem();
			if (itemFinAmount != null) {
				final int searchOpId = ((SearchOperators) itemFinAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finAmount", this.finAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custID.getValue())) {

			// get the search operator
			final Listitem itemCustID = this.sortOperator_custID.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%" + this.custID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.custID.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFinIsActive = this.sortOperator_finIsActive.getSelectedItem();
		if (itemFinIsActive != null) {
			final int searchOpId = ((SearchOperators) itemFinIsActive.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.finIsActive.isChecked()){
					so.addFilter(new Filter("finIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("finIsActive",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).getSearchOperatorId();
	
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute("data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		
		// Filtering added based on user branch and division
		so.addWhereClause(getUsrFinAuthenticationQry(false));
		
		// Defualt Sort on the table
		so.addSort("FinReference", false);

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
		this.financeMainCtrl.setSearchObj(so);

		final Listbox listBox = this.financeMainCtrl.listBoxFinanceMain;
		final Paging paging = this.financeMainCtrl.pagingFinanceMainList;

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<FinanceMain>) listBox.getModel()).init(so, listBox, paging);
		this.financeMainCtrl.setSearchObj(so);

		this.label_FinanceMainSearchResult.setValue(Labels.getLabel("label_FinanceMainSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}