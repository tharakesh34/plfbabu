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
 * FileName    		:  CheckListSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.checklist;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.applicationmaster.CheckListService;
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
 * /WEB-INF/pages/ApplicationMaster/CheckList/checkListSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CheckListSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CheckListSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CheckListSearch; // autowired
	
	protected Longbox checkListId; // autowired
	protected Listbox sortOperator_checkListId; // autowired
	protected Textbox checkListDesc; // autowired
	protected Listbox sortOperator_checkListDesc; // autowired
  	protected Intbox checkMinCount; // autowired
  	protected Listbox sortOperator_checkMinCount; // autowired
  	protected Intbox checkMaxCount; // autowired
  	protected Listbox sortOperator_checkMaxCount; // autowired
	protected Checkbox remarks; // autowired
	protected Listbox sortOperator_remarks; // autowired
	protected Checkbox active; // autowired
	protected Listbox sortOperator_active; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_CheckListSearch_RecordStatus; // autowired
	protected Label label_CheckListSearch_RecordType; // autowired
	protected Label label_CheckListSearchResult; // autowired

	// not auto wired vars
	private transient CheckListListCtrl checkListCtrl; // overhanded per param
	private transient CheckListService checkListService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CheckList");
	
	/**
	 * constructor
	 */
	public CheckListSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CheckListSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("checkListCtrl")) {
			this.checkListCtrl = (CheckListListCtrl) args.get("checkListCtrl");
		} else {
			this.checkListCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_checkListId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_checkListId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_checkListDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_checkListDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_checkMinCount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_checkMinCount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_checkMaxCount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_checkMaxCount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_remarks.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_remarks.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_active.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_CheckListSearch_RecordStatus.setVisible(false);
			this.label_CheckListSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<CheckList> searchObj = (JdbcSearchObject<CheckList>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("checkListId")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_checkListId, filter);
			    	this.checkListId.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("checkListDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_checkListDesc, filter);
					this.checkListDesc.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("checkMinCount")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_checkMinCount, filter);
			    	this.checkMinCount.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("checkMaxCount")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_checkMaxCount, filter);
			    	this.checkMaxCount.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("remarks")) {
					SearchOperators.restoreStringOperator(this.sortOperator_remarks, filter);
					this.remarks.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("active")) {
					SearchOperators.restoreStringOperator(this.sortOperator_active, filter);
					//this.active.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.active.setChecked(true);
					}else{
						this.active.setChecked(false);
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
		showCheckListSeekDialog();
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
		this.window_CheckListSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCheckListSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_CheckListSearch.doModal();
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

		final JdbcSearchObject<CheckList> so = new JdbcSearchObject<CheckList>(CheckList.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTCheckList_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("BMTCheckList_AView");
		}
		
		
	  if (this.checkListId.getValue()!=null) {	  
	    final Listitem item_CheckListId = this.sortOperator_checkListId.getSelectedItem();
	  	if (item_CheckListId != null) {
	 		final int searchOpId = ((SearchOperators) item_CheckListId.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.checkListId.getValue()!=null){
	 				so.addFilter(new Filter("checkListId",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("checkListId",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.checkListDesc.getValue())) {

			// get the search operator
			final Listitem item_CheckListDesc = this.sortOperator_checkListDesc.getSelectedItem();

			if (item_CheckListDesc != null) {
				final int searchOpId = ((SearchOperators) item_CheckListDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("checkListDesc", "%" + this.checkListDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("checkListDesc", this.checkListDesc.getValue(), searchOpId));
				}
			}
		}
	  if (this.checkMinCount.getValue()!=null) {	  
	    final Listitem item_CheckMinCount = this.sortOperator_checkMinCount.getSelectedItem();
	  	if (item_CheckMinCount != null) {
	 		final int searchOpId = ((SearchOperators) item_CheckMinCount.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.checkMinCount.getValue()!=null){
	 				so.addFilter(new Filter("checkMinCount",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("checkMinCount",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
	  if (this.checkMaxCount.getValue()!=null) {	  
	    final Listitem item_CheckMaxCount = this.sortOperator_checkMaxCount.getSelectedItem();
	  	if (item_CheckMaxCount != null) {
	 		final int searchOpId = ((SearchOperators) item_CheckMaxCount.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.checkMaxCount.getValue()!=null){
	 				so.addFilter(new Filter("checkMaxCount",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("checkMaxCount",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem item_Remarks = this.sortOperator_remarks.getSelectedItem();

		if (item_Remarks != null) {
			final int searchOpId = ((SearchOperators) item_Remarks.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.remarks.isChecked()){
					so.addFilter(new Filter("remarks",1, searchOpId));
				}else{
					so.addFilter(new Filter("remarks",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_Active = this.sortOperator_active.getSelectedItem();

		if (item_Active != null) {
			final int searchOpId = ((SearchOperators) item_Active.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.active.isChecked()){
					so.addFilter(new Filter("active",1, searchOpId));
				}else{
					so.addFilter(new Filter("active",0, searchOpId));	
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
		so.addSort("CheckListId", false);

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
		this.checkListCtrl.setSearchObj(so);

		final Listbox listBox = this.checkListCtrl.listBoxCheckList;
		final Paging paging = this.checkListCtrl.pagingCheckListList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<CheckList>) listBox.getModel()).init(so, listBox, paging);
		this.checkListCtrl.setSearchObj(so);

		this.label_CheckListSearchResult.setValue(Labels.getLabel("label_CheckListSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCheckListService(CheckListService checkListService) {
		this.checkListService = checkListService;
	}

	public CheckListService getCheckListService() {
		return this.checkListService;
	}
}