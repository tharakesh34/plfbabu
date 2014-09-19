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
 * FileName    		:  AgreementDefinitionSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.agreementdefinition;

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
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
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
 * /WEB-INF/pages/ApplicationMaster/AgreementDefinition/AgreementDefinitionSearch.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AgreementDefinitionSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3010749872521182659L;

	private final static Logger logger = Logger.getLogger(AgreementDefinitionSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_AgreementDefinitionSearch; 	// autoWired

	protected Textbox 	aggCode; 							// autoWired
	protected Listbox 	sortOperator_aggCode; 				// autoWired
	protected Textbox 	aggName; 							// autoWired
	protected Listbox 	sortOperator_aggName; 				// autoWired
	protected Textbox 	aggDesc; 							// autoWired
	protected Listbox 	sortOperator_aggDesc; 				// autoWired
	protected Textbox 	aggReportName; 						// autoWired
	protected Listbox 	sortOperator_aggReportName; 		// autoWired
	protected Textbox 	aggReportPath; 						// autoWired
	protected Listbox 	sortOperator_aggReportPath; 		// autoWired
	protected Checkbox 	aggIsActive; 						// autoWired
	protected Listbox 	sortOperator_aggIsActive; 			// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType;							// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_AgreementDefinitionSearch_RecordStatus; 	// autoWired
	protected Label label_AgreementDefinitionSearch_RecordType; 	// autoWired
	protected Label label_AgreementDefinitionSearchResult; 			// autoWired

	// not auto wired variables
	private transient AgreementDefinitionListCtrl agreementDefinitionCtrl; // overHanded per parameter
	private transient AgreementDefinitionService agreementDefinitionService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("AgreementDefinition");

	/**
	 * constructor
	 */
	public AgreementDefinitionSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AgreementDefinition object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AgreementDefinitionSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("agreementDefinitionCtrl")) {
			this.agreementDefinitionCtrl = (AgreementDefinitionListCtrl) args.get("agreementDefinitionCtrl");
		} else {
			this.agreementDefinitionCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_aggCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aggCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_aggName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aggName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_aggDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aggDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_aggReportName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aggReportName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_aggReportPath.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_aggReportPath.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_aggIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_aggIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_AgreementDefinitionSearch_RecordStatus.setVisible(false);
			this.label_AgreementDefinitionSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<AgreementDefinition> searchObj = (JdbcSearchObject<AgreementDefinition>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("aggCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aggCode, filter);
					this.aggCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aggName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aggName, filter);
					this.aggName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aggDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aggDesc, filter);
					this.aggDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aggReportName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aggReportName, filter);
					this.aggReportName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aggReportPath")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aggReportPath, filter);
					this.aggReportPath.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aggIsActive")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_aggIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.aggIsActive.setChecked(true);
					}else{
						this.aggIsActive.setChecked(false);
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
		showAgreementDefinitionSeekDialog();
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
		this.window_AgreementDefinitionSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showAgreementDefinitionSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_AgreementDefinitionSearch.doModal();
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

		final JdbcSearchObject<AgreementDefinition> so = new JdbcSearchObject<AgreementDefinition>(AgreementDefinition.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTAggrementDef_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("BMTAggrementDef_AView");
		}


		if (StringUtils.isNotEmpty(this.aggCode.getValue())) {

			// get the search operator
			final Listitem itemAggCode = this.sortOperator_aggCode.getSelectedItem();
			if (itemAggCode != null) {
				final int searchOpId = ((SearchOperators) itemAggCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aggCode", "%" + this.aggCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aggCode", this.aggCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aggName.getValue())) {

			// get the search operator
			final Listitem itemAggName = this.sortOperator_aggName.getSelectedItem();
			if (itemAggName != null) {
				final int searchOpId = ((SearchOperators) itemAggName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aggName", "%" + this.aggName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aggName", this.aggName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aggDesc.getValue())) {

			// get the search operator
			final Listitem itemAggDesc = this.sortOperator_aggDesc.getSelectedItem();
			if (itemAggDesc != null) {
				final int searchOpId = ((SearchOperators) itemAggDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aggDesc", "%" + this.aggDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aggDesc", this.aggDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aggReportName.getValue())) {

			// get the search operator
			final Listitem itemAggReportName = this.sortOperator_aggReportName.getSelectedItem();
			if (itemAggReportName != null) {
				final int searchOpId = ((SearchOperators) itemAggReportName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aggReportName", "%" + this.aggReportName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aggReportName", this.aggReportName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aggReportPath.getValue())) {

			// get the search operator
			final Listitem itemAggReportPath = this.sortOperator_aggReportPath.getSelectedItem();
			if (itemAggReportPath != null) {
				final int searchOpId = ((SearchOperators) itemAggReportPath.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aggReportPath", "%" + this.aggReportPath.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aggReportPath", this.aggReportPath.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemAggIsActive = this.sortOperator_aggIsActive.getSelectedItem();
		if (itemAggIsActive != null) {
			final int searchOpId = ((SearchOperators) itemAggIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.aggIsActive.isChecked()){
					so.addFilter(new Filter("aggIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("aggIsActive",0, searchOpId));	
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
		// Default Sort on the table
		so.addSort("AggCode", false);

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
		this.agreementDefinitionCtrl.setSearchObj(so);

		final Listbox listBox = this.agreementDefinitionCtrl.listBoxAgreementDefinition;
		final Paging paging = this.agreementDefinitionCtrl.pagingAgreementDefinitionList;


		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<AgreementDefinition>) listBox.getModel()).init(so, listBox, paging);
		this.agreementDefinitionCtrl.setSearchObj(so);

		this.label_AgreementDefinitionSearchResult.setValue(Labels.getLabel("label_AgreementDefinitionSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return this.agreementDefinitionService;
	}
}