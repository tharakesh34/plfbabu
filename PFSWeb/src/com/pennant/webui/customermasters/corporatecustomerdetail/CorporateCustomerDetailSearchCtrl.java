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
 * FileName    		:  CorporateCustomerDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.corporatecustomerdetail;

import java.io.Serializable;
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

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
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

public class CorporateCustomerDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6934733332123850099L;
	private final static Logger logger = Logger.getLogger(CorporateCustomerDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CorporateCustomerDetailSearch; 	// autowired
	
	protected Textbox 		name; 									// autowired
	protected Listbox 		sortOperator_name; 						// autowired
	protected Textbox 		phoneNumber; 							// autowired
	protected Listbox 		sortOperator_phoneNumber; 				// autowired
	protected Textbox 		emailId; 								// autowired
	protected Listbox 		sortOperator_emailId; 					// autowired
	protected Datebox 		bussCommenceDate; 						// autowired
	protected Listbox 		sortOperator_bussCommenceDate; 			// autowired
	protected Datebox 		servCommenceDate; 						// autowired
	protected Listbox 		sortOperator_servCommenceDate; 			// autowired
	protected Datebox 		bankRelationshipDate; 					// autowired
	protected Listbox 		sortOperator_bankRelationshipDate; 		// autowired
  	protected Decimalbox 	paidUpCapital; 							// autowired
  	protected Listbox 		sortOperator_paidUpCapital; 			// autowired
  	protected Decimalbox 	authorizedCapital; 						// autowired
  	protected Listbox 		sortOperator_authorizedCapital; 		// autowired
  	protected Decimalbox 	reservesAndSurPlus; 					// autowired
  	protected Listbox 		sortOperator_reservesAndSurPlus; 		// autowired
  	protected Decimalbox 	intangibleAssets; 						// autowired
  	protected Listbox 		sortOperator_intangibleAssets; 			// autowired
  	protected Decimalbox 	tangibleNetWorth; 						// autowired
  	protected Listbox 		sortOperator_tangibleNetWorth; 			// autowired
  	protected Decimalbox 	longTermLiabilities; 					// autowired
  	protected Listbox 		sortOperator_longTermLiabilities; 		// autowired
  	protected Decimalbox 	capitalEmployed; 						// autowired
  	protected Listbox 		sortOperator_capitalEmployed; 			// autowired
  	protected Decimalbox 	investments; 							// autowired
  	protected Listbox 		sortOperator_investments; 				// autowired
  	protected Decimalbox 	nonCurrentAssets; 						// autowired
  	protected Listbox 		sortOperator_nonCurrentAssets; 			// autowired
  	protected Decimalbox 	netWorkingCapital; 						// autowired
  	protected Listbox 		sortOperator_netWorkingCapital; 		// autowired
  	protected Decimalbox 	netSales; 								// autowired
  	protected Listbox 		sortOperator_netSales; 					// autowired
  	protected Decimalbox 	otherIncome; 							// autowired
  	protected Listbox 		sortOperator_otherIncome; 				// autowired
  	protected Decimalbox 	netProfitAfterTax; 						// autowired
  	protected Listbox 		sortOperator_netProfitAfterTax; 		// autowired
  	protected Decimalbox 	depreciation; 							// autowired
  	protected Listbox 		sortOperator_depreciation; 				// autowired
  	protected Decimalbox 	cashAccurals; 							// autowired
  	protected Listbox 		sortOperator_cashAccurals; 				// autowired
  	protected Decimalbox 	annualTurnover; 						// autowired
  	protected Listbox 		sortOperator_annualTurnover; 			// autowired
  	protected Decimalbox 	returnOnCapitalEmp; 					// autowired
  	protected Listbox 		sortOperator_returnOnCapitalEmp; 		// autowired
  	protected Decimalbox 	currentAssets; 							// autowired
  	protected Listbox 		sortOperator_currentAssets; 			// autowired
  	protected Decimalbox 	currentLiabilities; 					// autowired
  	protected Listbox 		sortOperator_currentLiabilities; 		// autowired
  	protected Decimalbox 	currentBookValue; 						// autowired
  	protected Listbox 		sortOperator_currentBookValue; 			// autowired
  	protected Decimalbox 	currentMarketValue; 					// autowired
  	protected Listbox 		sortOperator_currentMarketValue; 		// autowired
  	protected Decimalbox 	promotersShare; 						// autowired
  	protected Listbox 		sortOperator_promotersShare; 			// autowired
  	protected Decimalbox 	associatesShare; 						// autowired
  	protected Listbox 		sortOperator_associatesShare; 			// autowired
  	protected Decimalbox 	publicShare; 							// autowired
  	protected Listbox 		sortOperator_publicShare; 				// autowired
  	protected Decimalbox 	finInstShare; 							// autowired
  	protected Listbox 		sortOperator_finInstShare; 				// autowired
  	protected Decimalbox 	others; 								// autowired
  	protected Listbox 		sortOperator_others; 					// autowired
	protected Textbox 		recordStatus; 							// autowired
	protected Listbox 		recordType;								// autowired
	protected Listbox 		sortOperator_recordStatus; 				// autowired
	protected Listbox 		sortOperator_recordType; 				// autowired
	
	protected Label label_CorporateCustomerDetailSearch_RecordStatus; 	// autowired
	protected Label label_CorporateCustomerDetailSearch_RecordType; 	// autowired
	protected Label label_CorporateCustomerDetailSearchResult; 			// autowired

	// not auto wired vars
	private transient CorporateCustomerDetailListCtrl corporateCustomerDetailCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails(
			"CorporateCustomerDetail");
	
	/**
	 * constructor
	 */
	public CorporateCustomerDetailSearchCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CorporateCustomerDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorporateCustomerDetailSearch(Event event) throws Exception {
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

		if (args.containsKey("corporateCustomerDetailCtrl")) {
			this.corporateCustomerDetailCtrl = (CorporateCustomerDetailListCtrl) args.get(
					"corporateCustomerDetailCtrl");
		} else {
			this.corporateCustomerDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_name.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_name.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_phoneNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_phoneNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_emailId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_emailId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_bussCommenceDate.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_bussCommenceDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_servCommenceDate.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_servCommenceDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_bankRelationshipDate.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_bankRelationshipDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_paidUpCapital.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_paidUpCapital.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_authorizedCapital.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_authorizedCapital.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_reservesAndSurPlus.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_reservesAndSurPlus.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_intangibleAssets.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_intangibleAssets.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_tangibleNetWorth.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_tangibleNetWorth.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_longTermLiabilities.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_longTermLiabilities.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_capitalEmployed.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_capitalEmployed.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_investments.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_investments.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_nonCurrentAssets.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_nonCurrentAssets.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_netWorkingCapital.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_netWorkingCapital.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_netSales.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_netSales.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_otherIncome.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_otherIncome.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_netProfitAfterTax.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_netProfitAfterTax.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_depreciation.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_depreciation.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_cashAccurals.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_cashAccurals.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_annualTurnover.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_annualTurnover.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_returnOnCapitalEmp.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_returnOnCapitalEmp.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_currentAssets.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_currentAssets.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_currentLiabilities.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_currentLiabilities.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_currentBookValue.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_currentBookValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_currentMarketValue.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_currentMarketValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_promotersShare.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_promotersShare.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_associatesShare.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_associatesShare.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_publicShare.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_publicShare.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finInstShare.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_finInstShare.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_others.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_others.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CorporateCustomerDetailSearch_RecordStatus.setVisible(false);
			this.label_CorporateCustomerDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CorporateCustomerDetail> searchObj = (JdbcSearchObject<CorporateCustomerDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("name")) {
					SearchOperators.restoreStringOperator(this.sortOperator_name, filter);
					this.name.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("phoneNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_phoneNumber, filter);
					this.phoneNumber.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("emailId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_emailId, filter);
					this.emailId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("bussCommenceDate")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_bussCommenceDate, filter);
					this.bussCommenceDate.setValue(DateUtility.getUtilDate(
							filter.getValue().toString(),PennantConstants.DBDateFormat));
			    } else if (filter.getProperty().equals("servCommenceDate")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_servCommenceDate, filter);
					this.servCommenceDate.setValue(DateUtility.getUtilDate(
							filter.getValue().toString(),PennantConstants.DBDateFormat));
			    } else if (filter.getProperty().equals("bankRelationshipDate")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_bankRelationshipDate, filter);
					this.bankRelationshipDate.setValue(DateUtility.getUtilDate(
							filter.getValue().toString(),PennantConstants.DBDateFormat));
			    } else if (filter.getProperty().equals("paidUpCapital")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_paidUpCapital, filter);
					this.paidUpCapital.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("authorizedCapital")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_authorizedCapital, filter);
					this.authorizedCapital.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("reservesAndSurPlus")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_reservesAndSurPlus, filter);
					this.reservesAndSurPlus.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("intangibleAssets")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_intangibleAssets, filter);
					this.intangibleAssets.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("tangibleNetWorth")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_tangibleNetWorth, filter);
					this.tangibleNetWorth.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("longTermLiabilities")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_longTermLiabilities, filter);
					this.longTermLiabilities.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("capitalEmployed")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_capitalEmployed, filter);
					this.capitalEmployed.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("investments")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_investments, filter);
					this.investments.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("nonCurrentAssets")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_nonCurrentAssets, filter);
					this.nonCurrentAssets.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("netWorkingCapital")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_netWorkingCapital, filter);
					this.netWorkingCapital.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("netSales")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_netSales, filter);
					this.netSales.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("otherIncome")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_otherIncome, filter);
					this.otherIncome.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("netProfitAfterTax")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_netProfitAfterTax, filter);
					this.netProfitAfterTax.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("depreciation")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_depreciation, filter);
					this.depreciation.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("cashAccurals")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_cashAccurals, filter);
					this.cashAccurals.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("annualTurnover")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_annualTurnover, filter);
					this.annualTurnover.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("returnOnCapitalEmp")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_returnOnCapitalEmp, filter);
					this.returnOnCapitalEmp.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("currentAssets")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_currentAssets, filter);
					this.currentAssets.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("currentLiabilities")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_currentLiabilities, filter);
					this.currentLiabilities.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("currentBookValue")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_currentBookValue, filter);
					this.currentBookValue.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("currentMarketValue")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_currentMarketValue, filter);
					this.currentMarketValue.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("promotersShare")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_promotersShare, filter);
					this.promotersShare.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("associatesShare")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_associatesShare, filter);
					this.associatesShare.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("publicShare")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_publicShare, filter);
					this.publicShare.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("finInstShare")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finInstShare, filter);
					this.finInstShare.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("others")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_others, filter);
					this.others.setValue(filter.getValue().toString());
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
		showCorporateCustomerDetailSeekDialog();
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
		this.window_CorporateCustomerDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCorporateCustomerDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CorporateCustomerDetailSearch.doModal();
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
		
		final JdbcSearchObject<CorporateCustomerDetail> so = new JdbcSearchObject<CorporateCustomerDetail>(
				CorporateCustomerDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("CustomerCorporateDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("CustomerCorporateDetail_AView");
		}
		
		if (StringUtils.isNotEmpty(this.name.getValue())) {

			// get the search operator
			final Listitem itemName = this.sortOperator_name.getSelectedItem();
			if (itemName != null) {
				final int searchOpId = ((SearchOperators) itemName.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("name", "%" + this.name.getValue().toUpperCase() + 
							"%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("name", this.name.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.phoneNumber.getValue())) {

			// get the search operator
			final Listitem itemPhoneNumber = this.sortOperator_phoneNumber.getSelectedItem();
			if (itemPhoneNumber != null) {
				final int searchOpId = ((SearchOperators) itemPhoneNumber.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("phoneNumber", "%" + this.phoneNumber.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("phoneNumber", this.phoneNumber.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.emailId.getValue())) {

			// get the search operator
			final Listitem itemEmailId = this.sortOperator_emailId.getSelectedItem();
			if (itemEmailId != null) {
				final int searchOpId = ((SearchOperators) itemEmailId.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("emailId", "%" + this.emailId.getValue().toUpperCase() +
							"%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("emailId", this.emailId.getValue(), searchOpId));
				}
			}
		}
	  if (this.bussCommenceDate.getValue()!=null) {	  
	    final Listitem itemBussCommenceDate = this.sortOperator_bussCommenceDate.getSelectedItem();
	  	if (itemBussCommenceDate != null) {
	 		final int searchOpId = ((SearchOperators) itemBussCommenceDate.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("bussCommenceDate",DateUtility.formatUtilDate(
						this.bussCommenceDate.getValue(),PennantConstants.DBDateFormat), searchOpId));
			}
	 	}
	  }	
	  if (this.servCommenceDate.getValue()!=null) {	  
	    final Listitem itemServCommenceDate = this.sortOperator_servCommenceDate.getSelectedItem();
	  	if (itemServCommenceDate != null) {
	 		final int searchOpId = ((SearchOperators) itemServCommenceDate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("servCommenceDate",DateUtility.formatUtilDate(
						this.servCommenceDate.getValue(),PennantConstants.DBDateFormat), searchOpId));
			}
	 	}
	  }	
	  if (this.bankRelationshipDate.getValue()!=null) {	  
	    final Listitem itemBankRelationshipDate = this.sortOperator_bankRelationshipDate.getSelectedItem();
	  	if (itemBankRelationshipDate != null) {
	 		final int searchOpId = ((SearchOperators) itemBankRelationshipDate.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("bankRelationshipDate",DateUtility.formatUtilDate(
						this.bankRelationshipDate.getValue(),PennantConstants.DBDateFormat), searchOpId));
			}
	 	}
	  }	
	  if (this.paidUpCapital.getValue()!=null) {	  
	    final Listitem itemPaidUpCapital = this.sortOperator_paidUpCapital.getSelectedItem();
	  	if (itemPaidUpCapital != null) {
	 		final int searchOpId = ((SearchOperators) itemPaidUpCapital.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("paidUpCapital", this.paidUpCapital.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.authorizedCapital.getValue()!=null) {	  
	    final Listitem itemAuthorizedCapital = this.sortOperator_authorizedCapital.getSelectedItem();
	  	if (itemAuthorizedCapital != null) {
	 		final int searchOpId = ((SearchOperators) itemAuthorizedCapital.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("authorizedCapital", this.authorizedCapital.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.reservesAndSurPlus.getValue()!=null) {	  
	    final Listitem itemReservesAndSurPlus = this.sortOperator_reservesAndSurPlus.getSelectedItem();
	  	if (itemReservesAndSurPlus != null) {
	 		final int searchOpId = ((SearchOperators) itemReservesAndSurPlus.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("reservesAndSurPlus", this.reservesAndSurPlus.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.intangibleAssets.getValue()!=null) {	  
	    final Listitem itemIntangibleAssets = this.sortOperator_intangibleAssets.getSelectedItem();
	  	if (itemIntangibleAssets != null) {
	 		final int searchOpId = ((SearchOperators) itemIntangibleAssets.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("intangibleAssets", this.intangibleAssets.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.tangibleNetWorth.getValue()!=null) {	  
	    final Listitem itemTangibleNetWorth = this.sortOperator_tangibleNetWorth.getSelectedItem();
	  	if (itemTangibleNetWorth != null) {
	 		final int searchOpId = ((SearchOperators) itemTangibleNetWorth.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("tangibleNetWorth", this.tangibleNetWorth.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.longTermLiabilities.getValue()!=null) {	  
	    final Listitem itemLongTermLiabilities = this.sortOperator_longTermLiabilities.getSelectedItem();
	  	if (itemLongTermLiabilities != null) {
	 		final int searchOpId = ((SearchOperators) itemLongTermLiabilities.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("longTermLiabilities", this.longTermLiabilities.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.capitalEmployed.getValue()!=null) {	  
	    final Listitem itemCapitalEmployed = this.sortOperator_capitalEmployed.getSelectedItem();
	  	if (itemCapitalEmployed != null) {
	 		final int searchOpId = ((SearchOperators) itemCapitalEmployed.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("capitalEmployed", this.capitalEmployed.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.investments.getValue()!=null) {	  
	    final Listitem itemInvestments = this.sortOperator_investments.getSelectedItem();
	  	if (itemInvestments != null) {
	 		final int searchOpId = ((SearchOperators) itemInvestments.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("investments", this.investments.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.nonCurrentAssets.getValue()!=null) {	  
	    final Listitem itemNonCurrentAssets = this.sortOperator_nonCurrentAssets.getSelectedItem();
	  	if (itemNonCurrentAssets != null) {
	 		final int searchOpId = ((SearchOperators) itemNonCurrentAssets.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("nonCurrentAssets", this.nonCurrentAssets.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.netWorkingCapital.getValue()!=null) {	  
	    final Listitem itemNetWorkingCapital = this.sortOperator_netWorkingCapital.getSelectedItem();
	  	if (itemNetWorkingCapital != null) {
	 		final int searchOpId = ((SearchOperators) itemNetWorkingCapital.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("netWorkingCapital", this.netWorkingCapital.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.netSales.getValue()!=null) {	  
	    final Listitem itemNetSales = this.sortOperator_netSales.getSelectedItem();
	  	if (itemNetSales != null) {
	 		final int searchOpId = ((SearchOperators) itemNetSales.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("netSales", this.netSales.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.otherIncome.getValue()!=null) {	  
	    final Listitem itemOtherIncome = this.sortOperator_otherIncome.getSelectedItem();
	  	if (itemOtherIncome != null) {
	 		final int searchOpId = ((SearchOperators) itemOtherIncome.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("otherIncome", this.otherIncome.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.netProfitAfterTax.getValue()!=null) {	  
	    final Listitem itemNetProfitAfterTax = this.sortOperator_netProfitAfterTax.getSelectedItem();
	  	if (itemNetProfitAfterTax != null) {
	 		final int searchOpId = ((SearchOperators) itemNetProfitAfterTax.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("netProfitAfterTax", this.netProfitAfterTax.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.depreciation.getValue()!=null) {	  
	    final Listitem itemDepreciation = this.sortOperator_depreciation.getSelectedItem();
	  	if (itemDepreciation != null) {
	 		final int searchOpId = ((SearchOperators) itemDepreciation.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("depreciation", this.depreciation.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.cashAccurals.getValue()!=null) {	  
	    final Listitem itemCashAccurals = this.sortOperator_cashAccurals.getSelectedItem();
	  	if (itemCashAccurals != null) {
	 		final int searchOpId = ((SearchOperators) itemCashAccurals.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("cashAccurals", this.cashAccurals.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.annualTurnover.getValue()!=null) {	  
	    final Listitem itemAnnualTurnover = this.sortOperator_annualTurnover.getSelectedItem();
	  	if (itemAnnualTurnover != null) {
	 		final int searchOpId = ((SearchOperators) itemAnnualTurnover.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("annualTurnover", this.annualTurnover.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.returnOnCapitalEmp.getValue()!=null) {	  
	    final Listitem itemReturnOnCapitalEmp = this.sortOperator_returnOnCapitalEmp.getSelectedItem();
	  	if (itemReturnOnCapitalEmp != null) {
	 		final int searchOpId = ((SearchOperators) itemReturnOnCapitalEmp.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("returnOnCapitalEmp", this.returnOnCapitalEmp.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.currentAssets.getValue()!=null) {	  
	    final Listitem itemCurrentAssets = this.sortOperator_currentAssets.getSelectedItem();
	  	if (itemCurrentAssets != null) {
	 		final int searchOpId = ((SearchOperators) itemCurrentAssets.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("currentAssets", this.currentAssets.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.currentLiabilities.getValue()!=null) {	  
	    final Listitem itemCurrentLiabilities = this.sortOperator_currentLiabilities.getSelectedItem();
	  	if (itemCurrentLiabilities != null) {
	 		final int searchOpId = ((SearchOperators) itemCurrentLiabilities.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("currentLiabilities", this.currentLiabilities.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.currentBookValue.getValue()!=null) {	  
	    final Listitem itemCurrentBookValue = this.sortOperator_currentBookValue.getSelectedItem();
	  	if (itemCurrentBookValue != null) {
	 		final int searchOpId = ((SearchOperators) itemCurrentBookValue.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("currentBookValue", this.currentBookValue.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.currentMarketValue.getValue()!=null) {	  
	    final Listitem itemCurrentMarketValue = this.sortOperator_currentMarketValue.getSelectedItem();
	  	if (itemCurrentMarketValue != null) {
	 		final int searchOpId = ((SearchOperators) itemCurrentMarketValue.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("currentMarketValue", this.currentMarketValue.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.promotersShare.getValue()!=null) {	  
	    final Listitem itemPromotersShare = this.sortOperator_promotersShare.getSelectedItem();
	  	if (itemPromotersShare != null) {
	 		final int searchOpId = ((SearchOperators) itemPromotersShare.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("promotersShare", this.promotersShare.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.associatesShare.getValue()!=null) {	  
	    final Listitem itemAssociatesShare = this.sortOperator_associatesShare.getSelectedItem();
	  	if (itemAssociatesShare != null) {
	 		final int searchOpId = ((SearchOperators) itemAssociatesShare.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("associatesShare", this.associatesShare.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.publicShare.getValue()!=null) {	  
	    final Listitem itemPublicShare = this.sortOperator_publicShare.getSelectedItem();
	  	if (itemPublicShare != null) {
	 		final int searchOpId = ((SearchOperators) itemPublicShare.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("publicShare", this.publicShare.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.finInstShare.getValue()!=null) {	  
	    final Listitem itemFinInstShare = this.sortOperator_finInstShare.getSelectedItem();
	  	if (itemFinInstShare != null) {
	 		final int searchOpId = ((SearchOperators) itemFinInstShare.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("finInstShare", this.finInstShare.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
	  if (this.others.getValue()!=null) {	  
	    final Listitem itemOthers = this.sortOperator_others.getSelectedItem();
	  	if (itemOthers != null) {
	 		final int searchOpId = ((SearchOperators) itemOthers.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("others", this.others.getValue(),
						searchOpId));
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute(
						"data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() 
							+ "%", searchOpId));
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
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute(
						"data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() +
							"%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("CustId", false);

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
		this.corporateCustomerDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.corporateCustomerDetailCtrl.listBoxCorporateCustomerDetail;
		final Paging paging = this.corporateCustomerDetailCtrl.pagingCorporateCustomerDetailList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<CorporateCustomerDetail>) listBox.getModel()).init(so, listBox, paging);
		this.corporateCustomerDetailCtrl.setSearchObj(so);

		this.label_CorporateCustomerDetailSearchResult.setValue(Labels.getLabel(
				"label_CorporateCustomerDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

}