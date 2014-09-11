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
 * FileName    		:  ReportListSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.reports.reportlist;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.service.reports.ReportListService;
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
 * This is the controller class for the /WEB-INF/pages/Reports/ReportList/ReportListSearch.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ReportListSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1684027407173765117L;
	private final static Logger logger = Logger.getLogger(ReportListSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ReportListSearch; 		// autoWired
	
	protected Textbox module; 						// autoWired
	protected Listbox sortOperator_module; 			// autoWired
	protected Textbox fieldLabels; 					// autoWired
	protected Listbox sortOperator_fieldLabels; 	// autoWired
	protected Textbox fieldValues;	 				// autoWired
	protected Listbox sortOperator_fieldValues; 	// autoWired
	protected Textbox fieldType; 					// autoWired
	protected Listbox sortOperator_fieldType; 		// autoWired
	protected Textbox addfields; 					// autoWired
	protected Listbox sortOperator_addfields; 		// autoWired
	protected Textbox reportFileName; 				// autoWired
	protected Listbox sortOperator_reportFileName;	// autoWired
	protected Textbox reportHeading; 				// autoWired
	protected Listbox sortOperator_reportHeading; 	// autoWired
	protected Textbox moduleType; 					// autoWired
	protected Listbox sortOperator_moduleType; 		// autoWired
	protected Textbox recordStatus; 				// autoWired
	protected Listbox recordType;					// autoWired
	protected Listbox sortOperator_recordStatus; 	// autoWired
	protected Listbox sortOperator_recordType; 		// autoWired
	
	protected Label label_ReportListSearch_RecordStatus; // autoWired
	protected Label label_ReportListSearch_RecordType; // autoWired
	protected Label label_ReportListSearchResult; // autoWired

	// not auto wired variables
	private transient ReportListListCtrl reportListCtrl; // overHanded per parameter
	private transient ReportListService reportListService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ReportList");
	
	/**
	 * constructor
	 */
	public ReportListSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportList object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ReportListSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("reportListCtrl")) {
			this.reportListCtrl = (ReportListListCtrl) args.get("reportListCtrl");
		} else {
			this.reportListCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_module.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_module.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldLabels.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldLabels.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldValues.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldValues.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_addfields.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_addfields.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_reportFileName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_reportFileName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_reportHeading.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_reportHeading.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_moduleType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_moduleType.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_ReportListSearch_RecordStatus.setVisible(false);
			this.label_ReportListSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<ReportList> searchObj = (JdbcSearchObject<ReportList>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("module")) {
					SearchOperators.restoreStringOperator(this.sortOperator_module, filter);
					this.module.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldLabels")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldLabels, filter);
					this.fieldLabels.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldValues")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldValues, filter);
					this.fieldValues.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldType, filter);
					this.fieldType.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("addfields")) {
					SearchOperators.restoreStringOperator(this.sortOperator_addfields, filter);
					this.addfields.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("reportFileName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_reportFileName, filter);
					this.reportFileName.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("reportHeading")) {
					SearchOperators.restoreStringOperator(this.sortOperator_reportHeading, filter);
					this.reportHeading.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("moduleType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_moduleType, filter);
					this.moduleType.setValue(filter.getValue().toString());

					
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
		showReportListSeekDialog();
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
		this.window_ReportListSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showReportListSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_ReportListSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
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

		final JdbcSearchObject<ReportList> so = new JdbcSearchObject<ReportList>(ReportList.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("ReportList_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("ReportList_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.module.getValue())) {

			// get the search operator
			final Listitem item_Module = this.sortOperator_module.getSelectedItem();

			if (item_Module != null) {
				final int searchOpId = ((SearchOperators) item_Module.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("module", "%" + this.module.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("module", this.module.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldLabels.getValue())) {

			// get the search operator
			final Listitem item_FieldLabels = this.sortOperator_fieldLabels.getSelectedItem();

			if (item_FieldLabels != null) {
				final int searchOpId = ((SearchOperators) item_FieldLabels.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldLabels", "%" + this.fieldLabels.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldLabels", this.fieldLabels.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldValues.getValue())) {

			// get the search operator
			final Listitem item_FieldValues = this.sortOperator_fieldValues.getSelectedItem();

			if (item_FieldValues != null) {
				final int searchOpId = ((SearchOperators) item_FieldValues.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldValues", "%" + this.fieldValues.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldValues", this.fieldValues.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldType.getValue())) {

			// get the search operator
			final Listitem item_FieldType = this.sortOperator_fieldType.getSelectedItem();

			if (item_FieldType != null) {
				final int searchOpId = ((SearchOperators) item_FieldType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldType", "%" + this.fieldType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldType", this.fieldType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.addfields.getValue())) {

			// get the search operator
			final Listitem item_Addfields = this.sortOperator_addfields.getSelectedItem();

			if (item_Addfields != null) {
				final int searchOpId = ((SearchOperators) item_Addfields.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("addfields", "%" + this.addfields.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("addfields", this.addfields.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.reportFileName.getValue())) {

			// get the search operator
			final Listitem item_ReportFileName = this.sortOperator_reportFileName.getSelectedItem();

			if (item_ReportFileName != null) {
				final int searchOpId = ((SearchOperators) item_ReportFileName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("reportFileName", "%" + this.reportFileName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("reportFileName", this.reportFileName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.reportHeading.getValue())) {

			// get the search operator
			final Listitem item_ReportHeading = this.sortOperator_reportHeading.getSelectedItem();

			if (item_ReportHeading != null) {
				final int searchOpId = ((SearchOperators) item_ReportHeading.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("reportHeading", "%" + this.reportHeading.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("reportHeading", this.reportHeading.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.moduleType.getValue())) {

			// get the search operator
			final Listitem item_ModuleType = this.sortOperator_moduleType.getSelectedItem();

			if (item_ModuleType != null) {
				final int searchOpId = ((SearchOperators) item_ModuleType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("moduleType", "%" + this.moduleType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("moduleType", this.moduleType.getValue(), searchOpId));
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
		// Default Sort on the table
		so.addSort("Module", false);

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
		this.reportListCtrl.setSearchObj(so);

		final Listbox listBox = this.reportListCtrl.listBoxReportList;
		final Paging paging = this.reportListCtrl.pagingReportListList;
		

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<ReportList>) listBox.getModel()).init(so, listBox, paging);
		this.reportListCtrl.setSearchObj(so);

		this.label_ReportListSearchResult.setValue(Labels.getLabel("label_ReportListSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setReportListService(ReportListService reportListService) {
		this.reportListService = reportListService;
	}

	public ReportListService getReportListService() {
		return this.reportListService;
	}
}