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
 * FileName    		:  DedupFieldsSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dedup.dedupfields;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.service.dedup.DedupFieldsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class DedupFieldsSearchCtrl extends GFCBaseCtrl<DedupFields>  {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DedupFieldsSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DedupFieldsSearch; 
	
	protected Textbox fieldName; 
	protected Listbox sortOperator_fieldName; 
	protected Textbox fieldControl; 
	protected Listbox sortOperator_fieldControl; 
	protected Textbox recordStatus; 
	protected Listbox recordType;	
	protected Listbox sortOperator_recordStatus; 
	protected Listbox sortOperator_recordType; 
	
	protected Label label_DedupFieldsSearch_RecordStatus; 
	protected Label label_DedupFieldsSearch_RecordType; 
	protected Label label_DedupFieldsSearchResult; 

	// not auto wired vars
	private transient DedupFieldsListCtrl dedupFieldsCtrl; // overhanded per param
	private transient DedupFieldsService dedupFieldsService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DedupFields");
	
	/**
	 * constructor
	 */
	public DedupFieldsSearchCtrl() {
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
	public void onCreate$window_DedupFieldsSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DedupFieldsSearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		if (arguments.containsKey("dedupFieldsCtrl")) {
			this.dedupFieldsCtrl = (DedupFieldsListCtrl) arguments.get("dedupFieldsCtrl");
		} else {
			this.dedupFieldsCtrl = null;
		}

		// DropDown ListBox
	
		this.sortOperator_fieldName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldControl.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldControl.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_DedupFieldsSearch_RecordStatus.setVisible(false);
			this.label_DedupFieldsSearch_RecordType.setVisible(false);
		}
		
		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			final JdbcSearchObject<DedupFields> searchObj = (JdbcSearchObject<DedupFields>) arguments
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if ("fieldName".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldName, filter);
					this.fieldName.setValue(filter.getValue().toString());
			    } else if ("fieldControl".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldControl, filter);
					this.fieldControl.setValue(filter.getValue().toString());
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
			
		}
		showDedupFieldsSeekDialog();
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
	private void showDedupFieldsSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_DedupFieldsSearch.doModal();
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
	@SuppressWarnings("unchecked")
	public void doSearch() {

		final JdbcSearchObject<DedupFields> so = new JdbcSearchObject<DedupFields>(DedupFields.class);
		so.addTabelName("DedupFields_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		
		if (StringUtils.isNotEmpty(this.fieldName.getValue())) {

			// get the search operator
			final Listitem itemFieldName = this.sortOperator_fieldName.getSelectedItem();
			if (itemFieldName != null) {
				final int searchOpId = ((SearchOperators) itemFieldName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldName", "%" + this.fieldName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldName", this.fieldName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldControl.getValue())) {

			// get the search operator
			final Listitem itemFieldControl = this.sortOperator_fieldControl.getSelectedItem();
			if (itemFieldControl != null) {
				final int searchOpId = ((SearchOperators) itemFieldControl.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldControl", "%" + this.fieldControl.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldControl", this.fieldControl.getValue(), searchOpId));
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
		so.addSort("FieldName", false);

		// store the searchObject for reReading
		this.dedupFieldsCtrl.setSearchObj(so);

		final Listbox listBox = this.dedupFieldsCtrl.listBoxDedupFields;
		final Paging paging = this.dedupFieldsCtrl.pagingDedupFieldsList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<DedupFields>) listBox.getModel()).init(so, listBox, paging);
		this.dedupFieldsCtrl.setSearchObj(so);

		this.label_DedupFieldsSearchResult.setValue(Labels.getLabel("label_DedupFieldsSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDedupFieldsService(DedupFieldsService dedupFieldsService) {
		this.dedupFieldsService = dedupFieldsService;
	}

	public DedupFieldsService getDedupFieldsService() {
		return this.dedupFieldsService;
	}
}