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
 * FileName    		:  SuspenseSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.suspense;

import java.util.ArrayList;
import java.util.List;

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

import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class SuspenseSearchCtrl extends GFCBaseCtrl<FinanceSuspHead>  {
	private static final long serialVersionUID = 1933806562160029723L;
	private static final Logger logger = Logger.getLogger(SuspenseSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_SuspenseSearch; 			// autowired
	
	protected Textbox 		finReference; 						// autowired
	protected Listbox 		sortOperator_finReference; 			// autowired
	protected Textbox 		finBranch;	 						// autowired
	protected Listbox 		sortOperator_finBranch; 			// autowired
	protected Textbox 		finType; 							// autowired
	protected Listbox 		sortOperator_finType; 				// autowired
	protected Textbox 		custID; 							// autowired
	protected Listbox 		sortOperator_custID; 				// autowired
	protected Datebox 		finSuspDate; 					// autowired
	protected Listbox 		sortOperator_finSuspDate; 		// autowired
  	protected Decimalbox 	finSuspAmt; 					// autowired
  	protected Listbox 		sortOperator_finSuspAmt; 		// autowired
  	protected Decimalbox 	finCurSuspAmt; 					// autowired
  	protected Listbox 		sortOperator_finCurSuspAmt; 		// autowired
	protected Checkbox 		finIsInSusp; 							// autowired
	protected Listbox 		sortOperator_finIsInSusp; 			// autowired
	protected Checkbox 		manualSusp; 					// autowired
	protected Listbox 		sortOperator_manualSusp; 		// autowired
	
	protected Label label_SuspenseSearch_RecordStatus; 		// autowired
	protected Label label_SuspenseSearch_RecordType; 			// autowired
	protected Label label_SuspenseSearchResult; 				// autowired

	// not auto wired vars
	private transient SuspenseListCtrl suspenseCtrl; // overhanded per param
	private JdbcSearchObject<FinanceSuspHead> searchObj;
	
	/**
	 * constructor
	 */
	public SuspenseSearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Suspense object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SuspenseSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SuspenseSearch);

		if (arguments.containsKey("suspenseListCtrl")) {
			this.suspenseCtrl = (SuspenseListCtrl) arguments.get("suspenseListCtrl");
		} else {
			this.suspenseCtrl = null;
		}

		// DropDown ListBox	
		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finSuspDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finSuspDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finSuspAmt.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finSuspAmt.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finCurSuspAmt.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finCurSuspAmt.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finIsInSusp.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsInSusp.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_manualSusp.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_manualSusp.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<FinanceSuspHead>) arguments.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List<Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if ("finReference".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
					
			    } else if ("finBranch".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finBranch, filter);
					this.finBranch.setValue(filter.getValue().toString());
					
			    } else if ("finType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finType, filter);
					this.finType.setValue(filter.getValue().toString());
					
			    } else if ("lovDescCustCIFName".equals(filter.getProperty())) {
			    	SearchOperators.restoreStringOperator(this.sortOperator_custID, filter);
			    	this.custID.setText(filter.getValue().toString());
					
			    } else if ("finSuspDate".equals(filter.getProperty())) {
			    	SearchOperators.restoreStringOperator(this.sortOperator_finSuspDate, filter);
			    	this.finSuspDate.setText(filter.getValue().toString());
					
			    } else if ("finSuspAmt".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finSuspAmt, filter);
			    	this.finSuspAmt.setText(filter.getValue().toString());
					
			    } else if ("finCurSuspAmt".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finCurSuspAmt, filter);
			    	this.finCurSuspAmt.setText(filter.getValue().toString());
					
			    } else if ("finIsInSusp".equals(filter.getProperty())) {
		    		SearchOperators.restoreBooleanOperators(this.sortOperator_finIsInSusp, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.finIsInSusp.setChecked(true);
					}else{
						this.finIsInSusp.setChecked(false);
					}
			    } else if ("manualSusp".equals(filter.getProperty())) {
		    		SearchOperators.restoreBooleanOperators(this.sortOperator_manualSusp, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.manualSusp.setChecked(true);
					}else{
						this.manualSusp.setChecked(false);
					}
			    }
			}
			for(int i =0 ; i < rmvFilter.size() ; i++){
				searchObj.removeFilter(rmvFilter.get(i));
			}			
		}
		showSuspenseSeekDialog();
		logger.debug("Leaving" + event.toString());
	}

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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSuspenseSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SuspenseSearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

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
		
		final JdbcSearchObject<FinanceSuspHead> so = new JdbcSearchObject<FinanceSuspHead>(FinanceSuspHead.class);
		
		List<Filter> filters =this.searchObj.getFilters();
		 for (int i = 0; i < filters.size(); i++) {
		 Filter filter= filters.get(i);
		 so.addFilter  (new   Filter(filter.getProperty(),filter.getValue(),filter.getOperator()));
		}
		 
		 if(StringUtils.isNotBlank(this.searchObj.getWhereClause())){
			 so.addWhereClause(this.searchObj.getWhereClause());
			}

		 so.setSorts(this.searchObj.getSorts());
		 so.addTabelName(this.searchObj.getTabelName());
		 so.addTabelName("FinSuspenses_View");

		
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem item_FinReference = this.sortOperator_finReference.getSelectedItem();

			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finReference", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finReference", this.finReference.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finBranch.getValue())) {

			// get the search operator
			final Listitem item_FinBranch = this.sortOperator_finBranch.getSelectedItem();

			if (item_FinBranch != null) {
				final int searchOpId = ((SearchOperators) item_FinBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finBranch", "%" + this.finBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finBranch", this.finBranch.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem item_FinType = this.sortOperator_finType.getSelectedItem();

			if (item_FinType != null) {
				final int searchOpId = ((SearchOperators) item_FinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finType", this.finType.getValue(), searchOpId));
				}
			}
		}
	  if (this.custID.getValue()!=null) {	  
	    final Listitem item_CustID = this.sortOperator_custID.getSelectedItem();
	  	if (item_CustID != null) {
	 		final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == Filter.OP_LIKE) {
				so.addFilter(new Filter("lovDescCustCIFName", "%" + this.custID.getValue().toUpperCase() + "%", searchOpId));
			} else if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("lovDescCustCIFName", this.custID.getValue(), searchOpId));
			}
	 	}
	  }	
	  if (this.finSuspDate.getValue()!=null) {	  
	    final Listitem item_finSuspDate = this.sortOperator_finSuspDate.getSelectedItem();
	  	if (item_finSuspDate != null) {
	 		final int searchOpId = ((SearchOperators) item_finSuspDate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finSuspDate.getValue()!=null){
	 				so.addFilter(new Filter("finSuspDate",this.finSuspDate.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
	  if (this.finSuspAmt.getValue()!=null) {	  
	    final Listitem item_finSuspAmt = this.sortOperator_finSuspAmt.getSelectedItem();
	  	if (item_finSuspAmt != null) {
	 		final int searchOpId = ((SearchOperators) item_finSuspAmt.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finSuspAmt.getValue()!=null){
	 				so.addFilter(new Filter("finSuspAmt",this.finSuspAmt.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
	  if (this.finCurSuspAmt.getValue()!=null) {	  
	    final Listitem item_finCurSuspAmt = this.sortOperator_finCurSuspAmt.getSelectedItem();
	  	if (item_finCurSuspAmt != null) {
	 		final int searchOpId = ((SearchOperators) item_finCurSuspAmt.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finCurSuspAmt.getValue()!=null){
	 				so.addFilter(new Filter("finCurSuspAmt",this.finCurSuspAmt.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem item_finIsInSusp = this.sortOperator_finIsInSusp.getSelectedItem();

		if (item_finIsInSusp != null) {
			final int searchOpId = ((SearchOperators) item_finIsInSusp.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.finIsInSusp.isChecked()){
					so.addFilter(new Filter("finIsInSusp",1, searchOpId));
				}else{
					so.addFilter(new Filter("finIsInSusp",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_manualSusp = this.sortOperator_manualSusp.getSelectedItem();

		if (item_manualSusp != null) {
			final int searchOpId = ((SearchOperators) item_manualSusp.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.manualSusp.isChecked()){
					so.addFilter(new Filter("manualSusp",1, searchOpId));
				}else{
					so.addFilter(new Filter("manualSusp",0, searchOpId));	
				}
			}
		}

		// Defualt Sort on the table
		so.addSort("FinReference", false);

		// store the searchObject for reReading
		this.suspenseCtrl.setSearchObj(so);

		final Listbox listBox = this.suspenseCtrl.listBoxSuspense;
		final Paging paging = this.suspenseCtrl.pagingSuspenseList;

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<FinanceSuspHead>) listBox.getModel()).init(so, listBox, paging);
		this.suspenseCtrl.setSearchObj(so);

		this.label_SuspenseSearchResult.setValue(Labels.getLabel("label_SuspenseSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

}