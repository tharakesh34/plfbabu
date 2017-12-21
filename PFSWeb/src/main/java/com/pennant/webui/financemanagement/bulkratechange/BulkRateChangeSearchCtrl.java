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
 * FileName    		:  BulkRateChangeSearchCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  11-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 11-12-2015		Satya					 0.2          PSD - Ticket: 123931				*
 *														  Changes related to Delete/Reject  *
 *	 													  Finance Module.	 				*
 *																				            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.bulkratechange;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.webui.finance.enquiry.model.BulkRateChangeDialogModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class BulkRateChangeSearchCtrl extends GFCBaseCtrl<BulkRateChangeDetails>  {

	private static final long serialVersionUID = -4647934832219925649L;
	private static final Logger logger = Logger.getLogger(BulkRateChangeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_BulkRateChangeSearch; 			

	protected Textbox finReference; 					
	protected Textbox custCIF; 							
	protected Textbox finBranch; 					
	protected Textbox finCcy; 							
	protected Decimalbox rate; 					
	protected Listbox sortOperator_finReference; 		
	protected Listbox sortOperator_custCIF; 			
	protected Listbox sortOperator_FinBranch; 		
	protected Listbox sortOperator_FinCcy; 				
	protected Listbox sortOperator_Rate; 

	protected JdbcSearchObject<BulkRateChangeDetails> searchObj;
	private PagedListService pagedListService;


	// not auto wired variables
	private transient BulkRateChangeDialogCtrl bulkRateChangeDialogCtrl; // overhanded per param
	private transient BulkRateChangeHeader bulkRateChangeHeader;
	
	private boolean		isNewFinList = false;

	/**
	 * constructor
	 */
	public BulkRateChangeSearchCtrl() {
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
	public void onCreate$window_BulkRateChangeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());


		if (arguments.containsKey("bulkRateChangeHeader")) {
			setBulkRateChangeHeader((BulkRateChangeHeader) arguments.get("bulkRateChangeHeader"));
		} else {
			setBulkRateChangeHeader(null);
		}

		if (arguments.containsKey("bulkRateChangeDialogCtrl")) {
			setBulkRateChangeDialogCtrl((BulkRateChangeDialogCtrl) arguments.get("bulkRateChangeDialogCtrl"));
		} else {
			setBulkRateChangeDialogCtrl(null);
		}

		if (arguments.containsKey("isNewFinList")) {
			isNewFinList = (Boolean) arguments.get("isNewFinList");
		} else {
			isNewFinList = false;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_FinBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_FinCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Rate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_Rate.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
	 * @throws InterruptedException 
	 */
	public void onClick$btnSearch(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if (getBulkRateChangeDialogCtrl().tab_FinancesList.isSelected()) {
			if(getBulkRateChangeDialogCtrl().financesList != null && !getBulkRateChangeDialogCtrl().financesList.isEmpty()) {
				doSearch(getBulkRateChangeDialogCtrl().listBox_FinancesList,
						getBulkRateChangeDialogCtrl().paging_FinancesList, getBulkRateChangeDialogCtrl().rateChangeFinList);
			}
		} else {
			if(getBulkRateChangeDialogCtrl().rateChangeFinList != null && !getBulkRateChangeDialogCtrl().rateChangeFinList.isEmpty()) {
				doSearch(getBulkRateChangeDialogCtrl().listBox_RateChangeFinList,
						getBulkRateChangeDialogCtrl().paging_RateChangeFinList, getBulkRateChangeDialogCtrl().financesList);
			}
		}

		closeDialog();

		logger.debug("Leaving" + event.toString());
	}


	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceMainSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {

			// open the dialog in modal mode
			this.window_BulkRateChangeSearch.doModal();
		}  catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filters
	 */
	@SuppressWarnings("unchecked")
	public void doSearch(Listbox listbox, Paging paging, List<BulkRateChangeDetails> financesList) {
		logger.debug("Entering");

		this.searchObj = new JdbcSearchObject<BulkRateChangeDetails>(BulkRateChangeDetails.class, getListRows());
		this.searchObj.addSort("FinReference", false);

		// Fields List
		this.searchObj.addField("distinct FinReference");
		this.searchObj.addField("FinType");
		this.searchObj.addField("FinBranch");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("FinAmount");
		this.searchObj.addField("OldProfitRate");
		this.searchObj.addField("OldProfit");
		this.searchObj.addField("CustCIF");
		this.searchObj.addField("lovDescFinFormatter");

		if(getBulkRateChangeHeader().isNewRecord() || isNewFinList) {
			this.searchObj.addTabelName("BulkRateChange_View");
		} else {
			this.searchObj.addTabelName("BulkRateChange_SView");
			this.searchObj.addFilter(new Filter("BulkRateChangeRef", getBulkRateChangeHeader().getBulkRateChangeRef(), Filter.OP_EQUAL));
			this.searchObj.addField("NewProfitRate");
			this.searchObj.addField("NewProfit");
		}

		// FinReference
		if (StringUtils.isNotBlank(this.finReference.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finReference.getSelectedItem(), this.finReference.getValue(), "FinReference");
		}
		// Customer CIF
		if (StringUtils.isNotBlank(this.custCIF.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custCIF.getSelectedItem(), this.custCIF.getValue(), "CustCIF");
		}

		// Finance Branch
		if (StringUtils.isNotBlank(finBranch.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_FinBranch.getSelectedItem(), this.finBranch.getValue(), "FinBranch");
		}

		// Finance Currency
		if (StringUtils.isNotBlank(finCcy.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_FinCcy.getSelectedItem(), this.finCcy.getValue(), "FinCcy");
		}

		// Rate
		if (this.rate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_Rate.getSelectedItem(), this.rate.getValue(), "OldProfitRate");
		}

		//Where Clause
		this.searchObj.addWhereClause(" FinType = '" + getBulkRateChangeHeader().getFinType() + "' AND " + "FromDate >= '" + getBulkRateChangeHeader().getFromDate() + "'");

		//Data Base List
		List<BulkRateChangeDetails> filterFinList = getPagedListService().getBySearchObject(searchObj);
		List<BulkRateChangeDetails> filterFinListCopy = new ArrayList<BulkRateChangeDetails>();
		filterFinListCopy.addAll(filterFinList);

		for(BulkRateChangeDetails finance : filterFinListCopy) {
			for (BulkRateChangeDetails bulkRateDetails : financesList) {
				if(StringUtils.equals(finance.getFinReference(), bulkRateDetails.getFinReference())) {
					filterFinList.remove(finance);
				}
			}
		}

		// Set the ListModel for the articles.
		((PagedListWrapper<BulkRateChangeDetails>) listbox.getModel()).initList(filterFinList, listbox, paging);
		listbox.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

		logger.debug("Leaving");
	}


	// Setters And Getters

	public BulkRateChangeDialogCtrl getBulkRateChangeDialogCtrl() {
		return bulkRateChangeDialogCtrl;
	}

	public void setBulkRateChangeDialogCtrl(BulkRateChangeDialogCtrl bulkRateChangeDialogCtrl) {
		this.bulkRateChangeDialogCtrl = bulkRateChangeDialogCtrl;
	}

	public JdbcSearchObject<BulkRateChangeDetails> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BulkRateChangeDetails> searchObj) {
		this.searchObj = searchObj;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public BulkRateChangeHeader getBulkRateChangeHeader() {
		return bulkRateChangeHeader;
	}

	public void setBulkRateChangeHeader(BulkRateChangeHeader bulkRateChangeHeader) {
		this.bulkRateChangeHeader = bulkRateChangeHeader;
	}
}