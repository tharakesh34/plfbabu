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
 * FileName    		:  IncomeTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.incometype;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.systemmasters.IncomeTypeService;
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
 * /WEB-INF/pages/SystemMaster/IncomeType/IncomeTypeSearchDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class IncomeTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7000754561787229451L;
	private final static Logger logger = Logger.getLogger(IncomeTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_IncomeTypeSearch; 			// autoWired

	protected Textbox 	incomeTypeCode; 					// autoWired
	protected Listbox 	sortOperator_incomeTypeCode; 		// autoWired
	protected Textbox 	incomeTypeDesc; 					// autoWired
	protected Listbox 	sortOperator_incomeTypeDesc; 		// autoWired
	protected Checkbox 	incomeTypeIsActive; 				// autoWired
	protected Listbox 	sortOperator_incomeTypeIsActive;	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_IncomeTypeSearch_RecordStatus; 	// autoWired
	protected Label label_IncomeTypeSearch_RecordType; 		// autoWired
	protected Label label_IncomeTypeSearchResult; 			// autoWired

	// not autoWired variables
	private transient IncomeTypeListCtrl incomeTypeCtrl;  	// over handed per parameter
	private transient IncomeTypeService incomeTypeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("IncomeType");

	/**
	 * constructor
	 */
	public IncomeTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected IncomeType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IncomeTypeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("incomeTypeCtrl")) {
			this.incomeTypeCtrl = (IncomeTypeListCtrl) args.get("incomeTypeCtrl");
		} else {
			this.incomeTypeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_incomeTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_incomeTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_incomeTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_incomeTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_incomeTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_incomeTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_IncomeTypeSearch_RecordStatus.setVisible(false);
			this.label_IncomeTypeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<IncomeType> searchObj = (JdbcSearchObject<IncomeType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("incomeTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_incomeTypeCode, filter);
					this.incomeTypeCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("incomeTypeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_incomeTypeDesc, filter);
					this.incomeTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("incomeTypeIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_incomeTypeIsActive, filter);
					//this.incomeTypeIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.incomeTypeIsActive.setChecked(true);
					}else{
						this.incomeTypeIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showIncomeTypeSeekDialog();
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
		this.window_IncomeTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showIncomeTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_IncomeTypeSearch.doModal();
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<IncomeType> so = new JdbcSearchObject<IncomeType>(IncomeType.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTIncomeTypes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		} else {
			so.addTabelName("BMTIncomeTypes_AView");
		}

		if (StringUtils.isNotEmpty(this.incomeTypeCode.getValue())) {

			// get the search operator
			final Listitem item_IncomeTypeCode = this.sortOperator_incomeTypeCode.getSelectedItem();

			if (item_IncomeTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_IncomeTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("incomeTypeCode", "%"
							+ this.incomeTypeCode.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("incomeTypeCode",this.incomeTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.incomeTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_IncomeTypeDesc = this.sortOperator_incomeTypeDesc.getSelectedItem();

			if (item_IncomeTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_IncomeTypeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("incomeTypeDesc", "%"
							+ this.incomeTypeDesc.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("incomeTypeDesc",this.incomeTypeDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_IncomeTypeIsActive = this.sortOperator_incomeTypeIsActive.getSelectedItem();

		if (item_IncomeTypeIsActive != null) {
			final int searchOpId = ((SearchOperators) item_IncomeTypeIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.incomeTypeIsActive.isChecked()) {
					so.addFilter(new Filter("incomeTypeIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("incomeTypeIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}
		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}
		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%"
							+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("IncomeTypeCode", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.incomeTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.incomeTypeCtrl.listBoxIncomeType;
		final Paging paging = this.incomeTypeCtrl.pagingIncomeTypeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<IncomeType>) listBox.getModel()).init(so, listBox,paging);
		this.incomeTypeCtrl.setSearchObj(so);

		this.label_IncomeTypeSearchResult.setValue(Labels.getLabel(
		"label_IncomeTypeSearchResult.value")+ " "+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setIncomeTypeService(IncomeTypeService incomeTypeService) {
		this.incomeTypeService = incomeTypeService;
	}
	public IncomeTypeService getIncomeTypeService() {
		return this.incomeTypeService;
	}
}