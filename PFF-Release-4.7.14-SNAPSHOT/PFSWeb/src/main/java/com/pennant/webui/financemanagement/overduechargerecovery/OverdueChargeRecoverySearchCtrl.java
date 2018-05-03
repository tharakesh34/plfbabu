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
 * FileName    		:  OverdueChargeRecoverySearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.overduechargerecovery;

import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.AdvancedGroupsModelArray;
import com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryComparator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class OverdueChargeRecoverySearchCtrl extends GFCBaseCtrl<OverdueChargeRecovery>  {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(OverdueChargeRecoverySearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OverdueChargeRecoverySearch; // autowired
	
	protected Textbox finReference; // autowired
	protected Listbox sortOperator_finReference; // autowired
	protected Datebox finSchdDate; // autowired
	protected Listbox sortOperator_finSchdDate; // autowired
	protected Textbox finBrnm; // autowired
	protected Listbox sortOperator_finBrnm; // autowired
	protected Textbox finType; // autowired
	protected Listbox sortOperator_finType; // autowired
	protected Datebox finODDate; // autowired
	protected Listbox sortOperator_finODDate; // autowired
	protected Textbox finODCCustCtg; // autowired
	protected Listbox sortOperator_finODCCustCtg; // autowired
  	protected Decimalbox finODCWaived; // autowired
  	protected Listbox sortOperator_finODCWaived; // autowired
	protected Datebox finODCLastPaidDate; // autowired
	protected Listbox sortOperator_finODCLastPaidDate; // autowired
	protected Textbox finODCRecoverySts; // autowired
	protected Listbox sortOperator_finODCRecoverySts; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_OverdueChargeRecoverySearch_RecordStatus; // autowired
	protected Label label_OverdueChargeRecoverySearch_RecordType; // autowired
	protected Label label_OverdueChargeRecoverySearchResult; // autowired

	// not auto wired vars
	private transient OverdueChargeRecoveryListCtrl overdueChargeRecoveryCtrl; // overhanded per param
	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("OverdueChargeRecovery");
	private JdbcSearchObject<OverdueChargeRecovery> searchObj;
	
	/**
	 * constructor
	 */
	public OverdueChargeRecoverySearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_OverdueChargeRecoverySearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_OverdueChargeRecoverySearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	

		if (arguments.containsKey("overdueChargeRecoveryCtrl")) {
			this.overdueChargeRecoveryCtrl = (OverdueChargeRecoveryListCtrl) arguments.get("overdueChargeRecoveryCtrl");
		} else {
			this.overdueChargeRecoveryCtrl = null;
		}

		// DropDown ListBox	
		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finSchdDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finSchdDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finBrnm.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finBrnm.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finODDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finODDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finODCCustCtg.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finODCCustCtg.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finODCWaived.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finODCWaived.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finODCLastPaidDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finODCLastPaidDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finODCRecoverySts.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finODCRecoverySts.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_OverdueChargeRecoverySearch_RecordStatus.setVisible(false);
			this.label_OverdueChargeRecoverySearch_RecordType.setVisible(false);
		}
		
		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<OverdueChargeRecovery>) arguments.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List<Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if ("finReference".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
					
			    } else if ("finSchdDate".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finSchdDate, filter);
			    	this.finSchdDate.setText(filter.getValue().toString());
					
			    } else if ("finBrnm".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finBrnm, filter);
					this.finBrnm.setValue(filter.getValue().toString());
					
			    } else if ("finType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finType, filter);
					this.finType.setValue(filter.getValue().toString());
					
			    } else if ("finODDate".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finODDate, filter);
			    	this.finODDate.setText(filter.getValue().toString());
					
			    } else if ("finODCCustCtg".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finODCCustCtg, filter);
					this.finODCCustCtg.setValue(filter.getValue().toString());
					
			    } else if ("finODCWaived".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finODCWaived, filter);
			    	this.finODCWaived.setText(filter.getValue().toString());
					
			    } else if ("finODCLastPaidDate".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_finODCLastPaidDate, filter);
			    	this.finODCLastPaidDate.setText(filter.getValue().toString());
					
			    } else if ("finODCRecoverySts".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finODCRecoverySts, filter);
					this.finODCRecoverySts.setValue(filter.getValue().toString());
					
				} else if ("recordStatus".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if ("recordType".equals(filter.getProperty())) {
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
		showOverdueChargeRecoverySeekDialog();
	}

	// Components events

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
	private void showOverdueChargeRecoverySeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_OverdueChargeRecoverySearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	public void doSearch() {
		logger.debug("Entering");
		final JdbcSearchObject<OverdueChargeRecovery> so = new JdbcSearchObject<OverdueChargeRecovery>(OverdueChargeRecovery.class);
		
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
		 
		if (isWorkFlowEnabled()){
			so.addTabelName("FinODCRecovery_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FinODCRecovery_AView");
		}
		
		
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
	  if (this.finSchdDate.getValue()!=null) {	  
	    final Listitem item_FinSchdDate = this.sortOperator_finSchdDate.getSelectedItem();
	  	if (item_FinSchdDate != null) {
	 		final int searchOpId = ((SearchOperators) item_FinSchdDate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finSchdDate.getValue()!=null){
	 				so.addFilter(new Filter("finSchdDate",this.finSchdDate.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.finBrnm.getValue())) {

			// get the search operator
			final Listitem item_FinBrnm = this.sortOperator_finBrnm.getSelectedItem();

			if (item_FinBrnm != null) {
				final int searchOpId = ((SearchOperators) item_FinBrnm.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finBrnm", "%" + this.finBrnm.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finBrnm", this.finBrnm.getValue(), searchOpId));
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
	  if (this.finODDate.getValue()!=null) {	  
	    final Listitem item_FinODDate = this.sortOperator_finODDate.getSelectedItem();
	  	if (item_FinODDate != null) {
	 		final int searchOpId = ((SearchOperators) item_FinODDate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finODDate.getValue()!=null){
	 				so.addFilter(new Filter("finODDate",this.finODDate.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.finODCCustCtg.getValue())) {

			// get the search operator
			final Listitem item_FinODCCustCtg = this.sortOperator_finODCCustCtg.getSelectedItem();

			if (item_FinODCCustCtg != null) {
				final int searchOpId = ((SearchOperators) item_FinODCCustCtg.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finODCCustCtg", "%" + this.finODCCustCtg.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finODCCustCtg", this.finODCCustCtg.getValue(), searchOpId));
				}
			}
		}
	  if (this.finODCWaived.getValue()!=null) {	  
	    final Listitem item_FinODCWaived = this.sortOperator_finODCWaived.getSelectedItem();
	  	if (item_FinODCWaived != null) {
	 		final int searchOpId = ((SearchOperators) item_FinODCWaived.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finODCWaived.getValue()!=null){
	 				so.addFilter(new Filter("finODCWaived",this.finODCWaived.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
	  if (this.finODCLastPaidDate.getValue()!=null) {	  
	    final Listitem item_FinODCLastPaidDate = this.sortOperator_finODCLastPaidDate.getSelectedItem();
	  	if (item_FinODCLastPaidDate != null) {
	 		final int searchOpId = ((SearchOperators) item_FinODCLastPaidDate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.finODCLastPaidDate.getValue()!=null){
	 				so.addFilter(new Filter("finODCLastPaidDate",this.finODCLastPaidDate.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.finODCRecoverySts.getValue())) {

			// get the search operator
			final Listitem item_FinODCRecoverySts = this.sortOperator_finODCRecoverySts.getSelectedItem();

			if (item_FinODCRecoverySts != null) {
				final int searchOpId = ((SearchOperators) item_FinODCRecoverySts.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finODCRecoverySts", "%" + this.finODCRecoverySts.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finODCRecoverySts", this.finODCRecoverySts.getValue(), searchOpId));
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

		// store the searchObject for reReading
		this.overdueChargeRecoveryCtrl.setSearchObj(so);
		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		final SearchResult<OverdueChargeRecovery> searchResult = this.overdueChargeRecoveryCtrl.
						getPagedListService().getSRBySearchObject(so);
		this.overdueChargeRecoveryCtrl.listBoxOverdueChargeRecovery.setModel(new AdvancedGroupsModelArray(
				searchResult.getResult().toArray(), new OverdueChargeRecoveryComparator()));

		getOverdueChargeRecoveryCtrl().findSearchObject();
		this.label_OverdueChargeRecoverySearchResult.setValue(Labels.getLabel("label_OverdueChargeRecoverySearchResult.value") + " "
				+ String.valueOf(searchResult.getTotalCount()));
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return this.overdueChargeRecoveryService;
	}

	public OverdueChargeRecoveryListCtrl getOverdueChargeRecoveryCtrl() {
		return overdueChargeRecoveryCtrl;
	}

	public void setOverdueChargeRecoveryCtrl(
			OverdueChargeRecoveryListCtrl overdueChargeRecoveryCtrl) {
		this.overdueChargeRecoveryCtrl = overdueChargeRecoveryCtrl;
	}
}