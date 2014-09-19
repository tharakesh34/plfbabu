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
 * FileName    		:  CollateralTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collateraltype;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.coremasters.CollateralType;
import com.pennant.backend.service.coremasters.CollateralTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class CollateralTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralTypeSearch; // autowired
	
	protected Textbox hWCLP; // autowired
	protected Listbox sortOperator_hWCLP; // autowired
	protected Textbox hWCPD; // autowired
	protected Listbox sortOperator_hWCPD; // autowired
  	protected Decimalbox hWBVM; // autowired
  	protected Listbox sortOperator_hWBVM; // autowired
	protected Textbox hWINS; // autowired
	protected Listbox sortOperator_hWINS; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_CollateralTypeSearch_RecordStatus; // autowired
	protected Label label_CollateralTypeSearch_RecordType; // autowired
	protected Label label_CollateralTypeSearchResult; // autowired

	// not auto wired vars
	private transient CollateralTypeListCtrl collateralTypeCtrl; // overhanded per param
	private transient CollateralTypeService collateralTypeService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CollateralType");
	private JdbcSearchObject<CollateralType> searchObj;
	
	/**
	 * constructor
	 */
	public CollateralTypeSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CollateralTypeSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("collateralTypeCtrl")) {
			this.collateralTypeCtrl = (CollateralTypeListCtrl) args.get("collateralTypeCtrl");
		} else {
			this.collateralTypeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_hWCLP.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hWCLP.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hWCPD.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hWCPD.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hWBVM.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_hWBVM.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hWINS.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hWINS.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_CollateralTypeSearch_RecordStatus.setVisible(false);
			this.label_CollateralTypeSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<CollateralType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List <Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if (filter.getProperty().equals("hWCLP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hWCLP, filter);
					this.hWCLP.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hWCPD")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hWCPD, filter);
					this.hWCPD.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hWBVM")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_hWBVM, filter);
			    	this.hWBVM.setText(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hWINS")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hWINS, filter);
					this.hWINS.setValue(filter.getValue().toString());
					
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
		showCollateralTypeSeekDialog();
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
		this.window_CollateralTypeSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCollateralTypeSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_CollateralTypeSearch.doModal();
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
		final JdbcSearchObject<CollateralType> so = new JdbcSearchObject<CollateralType>(CollateralType.class);
		
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
			so.addTabelName("HWPF_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("HWPF_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.hWCLP.getValue())) {

			// get the search operator
			final Listitem itemHWCLP = this.sortOperator_hWCLP.getSelectedItem();
			if (itemHWCLP != null) {
				final int searchOpId = ((SearchOperators) itemHWCLP.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hWCLP", "%" + this.hWCLP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hWCLP", this.hWCLP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hWCPD.getValue())) {

			// get the search operator
			final Listitem itemHWCPD = this.sortOperator_hWCPD.getSelectedItem();
			if (itemHWCPD != null) {
				final int searchOpId = ((SearchOperators) itemHWCPD.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hWCPD", "%" + this.hWCPD.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hWCPD", this.hWCPD.getValue(), searchOpId));
				}
			}
		}
	  if (this.hWBVM.getValue()!=null) {	  
	    final Listitem itemHWBVM = this.sortOperator_hWBVM.getSelectedItem();
	  	if (itemHWBVM != null) {
	 		final int searchOpId = ((SearchOperators) itemHWBVM.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.hWBVM.getValue()!=null){
	 				so.addFilter(new Filter("hWBVM",this.hWBVM.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.hWINS.getValue())) {

			// get the search operator
			final Listitem itemHWINS = this.sortOperator_hWINS.getSelectedItem();
			if (itemHWINS != null) {
				final int searchOpId = ((SearchOperators) itemHWINS.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hWINS", "%" + this.hWINS.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hWINS", this.hWINS.getValue(), searchOpId));
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
		so.addSort("HWCLP", false);

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
		this.collateralTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.collateralTypeCtrl.listBoxCollateralType;
		final Paging paging = this.collateralTypeCtrl.pagingCollateralTypeList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<CollateralType>) listBox.getModel()).init(so, listBox, paging);
		this.collateralTypeCtrl.setSearchObj(so);

		this.label_CollateralTypeSearchResult.setValue(Labels.getLabel("label_CollateralTypeSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCollateralTypeService(CollateralTypeService collateralTypeService) {
		this.collateralTypeService = collateralTypeService;
	}

	public CollateralTypeService getCollateralTypeService() {
		return this.collateralTypeService;
	}
}