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
 * FileName    		:  OverdueChargeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rulefactory.overduecharge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.service.rulefactory.OverdueChargeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class OverdueChargeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(OverdueChargeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_OverdueChargeSearch; // autowired
	
	protected Textbox oDCRuleCode; // autowired
	protected Listbox sortOperator_oDCRuleCode; // autowired
	protected Textbox oDCPLAccount; // autowired
	protected Listbox sortOperator_oDCPLAccount; // autowired
	protected Textbox oDCCharityAccount; // autowired
	protected Listbox sortOperator_oDCCharityAccount; // autowired
  	protected Decimalbox oDCPLShare; // autowired
  	protected Listbox sortOperator_oDCPLShare; // autowired
	protected Checkbox oDCSweepCharges; // autowired
	protected Listbox sortOperator_oDCSweepCharges; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_OverdueChargeSearch_RecordStatus; // autowired
	protected Label label_OverdueChargeSearch_RecordType; // autowired
	protected Label label_OverdueChargeSearchResult; // autowired

	// not auto wired vars
	private transient OverdueChargeListCtrl overdueChargeCtrl; // overhanded per param
	private transient OverdueChargeService overdueChargeService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("OverdueCharge");
	private JdbcSearchObject<OverdueCharge> searchObj;
	
	/**
	 * constructor
	 */
	public OverdueChargeSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_OverdueChargeSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("overdueChargeCtrl")) {
			this.overdueChargeCtrl = (OverdueChargeListCtrl) args.get("overdueChargeCtrl");
		} else {
			this.overdueChargeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_oDCRuleCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_oDCRuleCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCPLAccount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_oDCPLAccount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCCharityAccount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_oDCCharityAccount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCPLShare.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_oDCPLShare.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCSweepCharges.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_oDCSweepCharges.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_OverdueChargeSearch_RecordStatus.setVisible(false);
			this.label_OverdueChargeSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<OverdueCharge>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List <Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if (filter.getProperty().equals("oDCRuleCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_oDCRuleCode, filter);
					this.oDCRuleCode.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("oDCPLAccount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_oDCPLAccount, filter);
					this.oDCPLAccount.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("oDCCharityAccount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_oDCCharityAccount, filter);
					this.oDCCharityAccount.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("oDCPLShare")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_oDCPLShare, filter);
			    	this.oDCPLShare.setText(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("oDCSweepCharges")) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_oDCSweepCharges, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.oDCSweepCharges.setChecked(true);
					}else{
						this.oDCSweepCharges.setChecked(false);
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
			for(int i =0 ; i < rmvFilter.size() ; i++){
				searchObj.removeFilter(rmvFilter.get(i));
			}			
		}
		showOverdueChargeSeekDialog();
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
		this.window_OverdueChargeSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showOverdueChargeSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_OverdueChargeSearch.doModal();
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
		final JdbcSearchObject<OverdueCharge> so = new JdbcSearchObject<OverdueCharge>(OverdueCharge.class);
		
		List<Filter> filters =this.searchObj.getFilters();
		 for (int i = 0; i < filters.size(); i++) {
		 Filter filter= filters.get(i);
		 so.addFilter  (new   Filter(filter.getProperty(),filter.getValue(),filter.getOperator()));
		}
		 
		 if(!StringUtils.trimToEmpty(this.searchObj.getWhereClause()).equals("")){
			 so.addWhereClause(this.searchObj.getWhereClause());
			}

			 so.setSorts(this.searchObj.getSorts());
			 so.addTabelName(this.searchObj.getTabelName());
		 
		if (isWorkFlowEnabled()){
			so.addTabelName("FinODCHeader_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FinODCHeader_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.oDCRuleCode.getValue())) {

			// get the search operator
			final Listitem itemODCRuleCode = this.sortOperator_oDCRuleCode.getSelectedItem();
			if (itemODCRuleCode != null) {
				final int searchOpId = ((SearchOperators) itemODCRuleCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("oDCRuleCode", "%" + this.oDCRuleCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("oDCRuleCode", this.oDCRuleCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.oDCPLAccount.getValue())) {

			// get the search operator
			final Listitem itemODCPLAccount = this.sortOperator_oDCPLAccount.getSelectedItem();
			if (itemODCPLAccount != null) {
				final int searchOpId = ((SearchOperators) itemODCPLAccount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("oDCPLAccount", "%" + this.oDCPLAccount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("oDCPLAccount", this.oDCPLAccount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.oDCCharityAccount.getValue())) {

			// get the search operator
			final Listitem itemODCCharityAccount = this.sortOperator_oDCCharityAccount.getSelectedItem();
			if (itemODCCharityAccount != null) {
				final int searchOpId = ((SearchOperators) itemODCCharityAccount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("oDCCharityAccount", "%" + this.oDCCharityAccount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("oDCCharityAccount", this.oDCCharityAccount.getValue(), searchOpId));
				}
			}
		}
	  if (this.oDCPLShare.getValue()!=null) {	  
	    final Listitem itemODCPLShare = this.sortOperator_oDCPLShare.getSelectedItem();
	  	if (itemODCPLShare != null) {
	 		final int searchOpId = ((SearchOperators) itemODCPLShare.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.oDCPLShare.getValue()!=null){
	 				so.addFilter(new Filter("oDCPLShare",this.oDCPLShare.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem itemODCSweepCharges = this.sortOperator_oDCSweepCharges.getSelectedItem();
		if (itemODCSweepCharges != null) {
			final int searchOpId = ((SearchOperators) itemODCSweepCharges.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.oDCSweepCharges.isChecked()){
					so.addFilter(new Filter("oDCSweepCharges",1, searchOpId));
				}else{
					so.addFilter(new Filter("oDCSweepCharges",0, searchOpId));	
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
		// Defualt Sort on the table
		so.addSort("ODCRuleCode", false);

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
		this.overdueChargeCtrl.setSearchObj(so);

		final Listbox listBox = this.overdueChargeCtrl.listBoxOverdueCharge;
		final Paging paging = this.overdueChargeCtrl.pagingOverdueChargeList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<OverdueCharge>) listBox.getModel()).init(so, listBox, paging);
		this.overdueChargeCtrl.setSearchObj(so);

		this.label_OverdueChargeSearchResult.setValue(Labels.getLabel("label_OverdueChargeSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setOverdueChargeService(OverdueChargeService overdueChargeService) {
		this.overdueChargeService = overdueChargeService;
	}

	public OverdueChargeService getOverdueChargeService() {
		return this.overdueChargeService;
	}
}