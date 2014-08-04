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
 * FileName    		:  TreasuaryFinanceSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.treasuaryfinance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.TreasuryFinHeaderListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class TreasuaryFinanceSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TreasuaryFinanceSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_TreasuaryFinanceSearch; // autowired
	
	protected Textbox finReference;              // autowired
	protected Listbox sortOperator_finReference; // autowired
	protected Decimalbox totPriAmount;               // autowired
	protected Listbox sortOperator_totPriAmount;     // autowired
	protected Textbox finCcy;                    // autowired
	protected Listbox sortOperator_finCcy;          // autowired
	protected Datebox startDate;                    // autowired
	protected Listbox sortOperator_startDate;          // autowired
	protected Datebox maturityDate;                    // autowired
	protected Listbox sortOperator_maturityDate;          // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_TreasuaryFinanceSearch_RecordStatus; // autowired
	protected Label label_TreasuaryFinanceSearch_RecordType; // autowired
	protected Label label_TreasuaryFinanceSearchResult; // autowired

	// not auto wired vars
	private transient TreasuryFinHeaderListCtrl treasuryFinHeaderListCtrl; // overhanded per param
	private transient TreasuaryFinanceService treasuaryFinanceService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("InvestmentFinHeader");
	private JdbcSearchObject<InvestmentFinHeader> searchObj;
	
	/**
	 * constructor
	 */
	public TreasuaryFinanceSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public void onCreate$window_TreasuaryFinanceSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("treasuryFinHeaderListCtrl")) {
			this.treasuryFinHeaderListCtrl = (TreasuryFinHeaderListCtrl) args.get("treasuryFinHeaderListCtrl");
		} else {
			this.treasuryFinHeaderListCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_finReference.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_totPriAmount.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_totPriAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_finCcy.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_startDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_startDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_maturityDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_maturityDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_TreasuaryFinanceSearch_RecordStatus.setVisible(false);
			this.label_TreasuaryFinanceSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<InvestmentFinHeader>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List <Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if (filter.getProperty().equals("finReference")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
				}  else if(filter.getProperty().equals("totPriAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_totPriAmount, filter);
					this.totPriAmount.setValue(filter.getValue().toString());
				} else if(filter.getProperty().equals("finCcy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finCcy, filter);
					this.finCcy.setValue(filter.getValue().toString());
				} else if(filter.getProperty().equals("startDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_startDate, filter);
					this.startDate.setValue(new Date(filter.getValue().toString()));
				}   else if(filter.getProperty().equals("maturityDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_maturityDate, filter);
					this.maturityDate.setValue(new Date(filter.getValue().toString()));
				}   else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				}  else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
	
				}
			}
			for(int i =0 ; i < rmvFilter.size() ; i++){
				searchObj.removeFilter(rmvFilter.get(i));
			}			
		}
		showTreasuaryFinanceSeekDialog();
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
		this.window_TreasuaryFinanceSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showTreasuaryFinanceSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_TreasuaryFinanceSearch.doModal();
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
		logger.debug("Entering");
		final JdbcSearchObject<InvestmentFinHeader> so = new JdbcSearchObject<InvestmentFinHeader>(InvestmentFinHeader.class);
		
		List<Filter> filters =this.searchObj.getFilters();
		 for (int i = 0; i < filters.size(); i++) {
		 Filter filter= filters.get(i);
		 so.addFilter  (new   Filter(filter.getProperty(),filter.getValue(),filter.getOperator()));
		}
		 
		 if(!StringUtils.trimToEmpty(this.searchObj.getWhereClause()).equals("")){
			 so.addWhereClause(new String(this.searchObj.getWhereClause()));
			}

			 so.setSorts(this.searchObj.getSorts());
			 so.addTabelName(this.searchObj.getTabelName());
		 
		if (isWorkFlowEnabled()){
			so.addTabelName("InvestmentFinHeader_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("InvestmentFinHeader_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem item_FinReference = this.sortOperator_finReference.getSelectedItem();

			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("investmentRef", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("investmentRef", this.finReference.getValue(), searchOpId));
				}
			}
		}
		if (this.totPriAmount.getValue() != null) {
			
			// get the search operator
			final Listitem item_FinReference = this.sortOperator_totPriAmount.getSelectedItem();
			
			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("TotPrincipalAmt", "%" + this.totPriAmount.getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("TotPrincipalAmt", this.totPriAmount.getValue().toString().toUpperCase(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(String.valueOf(this.finCcy.getValue()))) {
			
			// get the search operator
			final Listitem item_FinReference = this.sortOperator_finCcy.getSelectedItem();
			
			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finCcy", "%" + this.finCcy.getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finCcy", this.finCcy.getValue().toString().toUpperCase(), searchOpId));
				}
			}
		}
		if (this.startDate.getValue() != null) {
			
			// get the search operator
			final Listitem item_FinReference = this.sortOperator_startDate.getSelectedItem();
			
			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("StartDate", "%" + this.startDate.getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("StartDate", this.startDate.getValue().toString().toUpperCase(), searchOpId));
				}
			}
		}
		if (this.maturityDate.getValue() != null) {
			
			// get the search operator
			final Listitem item_FinReference = this.sortOperator_maturityDate.getSelectedItem();
			
			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("MaturityDate", "%" + this.maturityDate.getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("MaturityDate", this.maturityDate.getValue().toString().toUpperCase(), searchOpId));
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
		so.addSort("investmentRef", false);

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
		this.treasuryFinHeaderListCtrl.setSearchObj(so);

		final Listbox listBox = this.treasuryFinHeaderListCtrl.listBoxTrFinHeader;
		final Paging paging = this.treasuryFinHeaderListCtrl.pagingTFinHeaderList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<InvestmentFinHeader>) listBox.getModel()).init(so, listBox, paging);
		this.treasuryFinHeaderListCtrl.setSearchObj(so);

		this.label_TreasuaryFinanceSearchResult.setValue(Labels.getLabel("label_TreasuaryFinanceSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setTreasuaryFinanceService(TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return this.treasuaryFinanceService;
	}
}