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
 * FileName    		:  CustomerBalanceSheetSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerbalancesheet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
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
 * /WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/CustomerBalanceSheetSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerBalanceSheetSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7212909250520157129L;
	private final static Logger logger = Logger.getLogger(CustomerBalanceSheetSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerBalanceSheetSearch; 	// autowired
	
	protected Longbox 		custID; 							// autowired
	protected Listbox 		sortOperator_custID; 				// autowired
	protected Textbox 		financialYear; 						// autowired
	protected Listbox 		sortOperator_financialYear; 		// autowired
  	protected Decimalbox 	totalAssets; 						// autowired
  	protected Listbox 		sortOperator_totalAssets; 			// autowired
  	protected Decimalbox 	totalLiabilities; 					// autowired
  	protected Listbox 		sortOperator_totalLiabilities; 		// autowired
  	protected Decimalbox 	netProfit; 							// autowired
  	protected Listbox 		sortOperator_netProfit; 			// autowired
  	protected Decimalbox 	netSales; 							// autowired
  	protected Listbox 		sortOperator_netSales; 				// autowired
  	protected Decimalbox 	netIncome; 							// autowired
  	protected Listbox 		sortOperator_netIncome; 			// autowired
  	protected Decimalbox 	operatingProfit; 					// autowired
  	protected Listbox 		sortOperator_operatingProfit; 		// autowired
  	protected Decimalbox 	cashFlow; 							// autowired
  	protected Listbox 		sortOperator_cashFlow; 				// autowired
  	protected Decimalbox 	bookValue; 							// autowired
  	protected Listbox 		sortOperator_bookValue; 			// autowired
  	protected Decimalbox 	marketValue; 						// autowired
  	protected Listbox 		sortOperator_marketValue; 			// autowired
	protected Textbox 		recordStatus; 						// autowired
	protected Listbox 		recordType;							// autowired
	protected Listbox 		sortOperator_recordStatus; 			// autowired
	protected Listbox 		sortOperator_recordType;		 	// autowired
	
	protected Label label_CustomerBalanceSheetSearch_RecordStatus;	// autowired
	protected Label label_CustomerBalanceSheetSearch_RecordType; 	// autowired
	protected Label label_CustomerBalanceSheetSearchResult; 		// autowired

	// not auto wired vars
	private transient CustomerBalanceSheetListCtrl customerBalanceSheetCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails(
			"CustomerBalanceSheet");
	
	/**
	 * Default Constructor
	 */
	public CustomerBalanceSheetSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerBalanceSheet object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBalanceSheetSearch(Event event) throws Exception {
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

		if (args.containsKey("customerBalanceSheetCtrl")) {
			this.customerBalanceSheetCtrl = (CustomerBalanceSheetListCtrl) args.get(
					"customerBalanceSheetCtrl");
		} else {
			this.customerBalanceSheetCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_custID.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_financialYear.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_financialYear.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_totalAssets.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_totalAssets.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_totalLiabilities.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_totalLiabilities.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_netProfit.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_netProfit.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_netSales.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_netSales.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_netIncome.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_netIncome.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_operatingProfit.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_operatingProfit.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_cashFlow.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_cashFlow.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_bookValue.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_bookValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_marketValue.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_marketValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerBalanceSheetSearch_RecordStatus.setVisible(false);
			this.label_CustomerBalanceSheetSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerBalanceSheet> searchObj = (JdbcSearchObject<CustomerBalanceSheet>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("custId")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_custID, filter);
			    	this.custID.setValue(Long.valueOf(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("financialYear")) {
					SearchOperators.restoreStringOperator(this.sortOperator_financialYear, filter);
					this.financialYear.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("totalAssets")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_totalAssets, filter);
			    	this.totalAssets.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("totalLiabilities")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_totalLiabilities, filter);
			    	this.totalLiabilities.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("netProfit")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_netProfit, filter);
			    	this.netProfit.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("netSales")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_netSales, filter);
			    	this.netSales.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("netIncome")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_netIncome, filter);
			    	this.netIncome.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("operatingProfit")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_operatingProfit, filter);
			    	this.operatingProfit.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("cashFlow")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_cashFlow, filter);
			    	this.cashFlow.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("bookValue")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_bookValue, filter);
			    	this.bookValue.setValue(new BigDecimal(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("marketValue")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_marketValue, filter);
			    	this.marketValue.setValue(new BigDecimal(filter.getValue().toString()));
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(
								filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showCustomerBalanceSheetSeekDialog();
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
		this.window_CustomerBalanceSheetSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerBalanceSheetSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerBalanceSheetSearch.doModal();
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
		
		final JdbcSearchObject<CustomerBalanceSheet> so = new JdbcSearchObject<CustomerBalanceSheet>(
				CustomerBalanceSheet.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("CustomerBalanceSheet_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("CustomerBalanceSheet_AView");
		}
		
	  if (this.custID.longValue()!=0) {	  
	    final Listitem itemCustID = this.sortOperator_custID.getSelectedItem();
	  	if (itemCustID != null) {
	 		final int searchOpId = ((SearchOperators) itemCustID.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == Filter.OP_LIKE) {
				so.addFilter(new Filter("custId", "%" + this.custID.longValue()+ "%", searchOpId));
			} else if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("custId", this.custID.longValue(), searchOpId));
			}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.financialYear.getValue())) {

			// get the search operator
			final Listitem itemFinancialYear = this.sortOperator_financialYear.getSelectedItem();
			if (itemFinancialYear != null) {
				final int searchOpId = ((SearchOperators) itemFinancialYear.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("financialYear", "%" + 
							this.financialYear.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("financialYear", this.financialYear.getValue(), searchOpId));
				}
			}
		}
	  if (this.totalAssets.getValue()!=null) {	  
	    final Listitem itemTotalAssets = this.sortOperator_totalAssets.getSelectedItem();
	  	if (itemTotalAssets != null) {
	 		final int searchOpId = ((SearchOperators) itemTotalAssets.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("totalAssets", this.totalAssets.getValue(),
						searchOpId));
			}
	 	}
	  }	
	  if (this.totalLiabilities.getValue()!=null) {	  
	    final Listitem itemTotalLiabilities = this.sortOperator_totalLiabilities.getSelectedItem();
	  	if (itemTotalLiabilities != null) {
	 		final int searchOpId = ((SearchOperators) itemTotalLiabilities.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("totalLiabilities",this.totalLiabilities.getValue(), searchOpId));
	 		}
	 	}
	  }	
	  if (this.netProfit.getValue()!=null) {	  
	    final Listitem itemNetProfit = this.sortOperator_netProfit.getSelectedItem();
	  	if (itemNetProfit != null) {
	 		final int searchOpId = ((SearchOperators) itemNetProfit.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("netProfit",this.netProfit.getValue(), searchOpId));	
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
	 			so.addFilter(new Filter("netSales",this.netSales.getValue(), searchOpId));	
	 		}
	 	}
	  }	
	  
	  if (this.netIncome.getValue()!=null) {	  
	    final Listitem itemNetIncome = this.sortOperator_netIncome.getSelectedItem();
	  	if (itemNetIncome != null) {
	 		final int searchOpId = ((SearchOperators) itemNetIncome.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("netIncome",this.netIncome.getValue(), searchOpId));	
	 		}
	 	}
	  }	
	  
	  if (this.operatingProfit.getValue()!=null) {	  
	    final Listitem itemOperatingProfit = this.sortOperator_operatingProfit.getSelectedItem();
	  	if (itemOperatingProfit != null) {
	 		final int searchOpId = ((SearchOperators) itemOperatingProfit.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("operatingProfit",this.operatingProfit.getValue(), searchOpId));	
	 		}
	 	}
	  }	
	  
	  if (this.cashFlow.getValue()!=null) {	  
	    final Listitem itemCashFlow = this.sortOperator_cashFlow.getSelectedItem();
	  	if (itemCashFlow != null) {
	 		final int searchOpId = ((SearchOperators) itemCashFlow.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("cashFlow",this.cashFlow.getValue(), searchOpId));	
	 		}
	 	}
	  }	
	  if (this.bookValue.getValue()!=null) {	  
	    final Listitem itemBookValue = this.sortOperator_bookValue.getSelectedItem();
	  	if (itemBookValue != null) {
	 		final int searchOpId = ((SearchOperators) itemBookValue.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("bookValue",this.bookValue.getValue(), searchOpId));	
	 		}
	 	}
	  }	
	  if (this.marketValue.getValue()!=null) {	  
	    final Listitem itemMarketValue = this.sortOperator_marketValue.getSelectedItem();
	  	if (itemMarketValue != null) {
	 		final int searchOpId = ((SearchOperators) itemMarketValue.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("marketValue",this.marketValue.getValue(), searchOpId));	
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
		this.customerBalanceSheetCtrl.setSearchObj(so);
		final Listbox listBox = this.customerBalanceSheetCtrl.listBoxCustomerBalanceSheet;
		final Paging paging = this.customerBalanceSheetCtrl.pagingCustomerBalanceSheetList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<CustomerBalanceSheet>) listBox.getModel()).init(so, listBox, paging);
		this.customerBalanceSheetCtrl.setSearchObj(so);

		this.label_CustomerBalanceSheetSearchResult.setValue(Labels.getLabel(
				"label_CustomerBalanceSheetSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

}