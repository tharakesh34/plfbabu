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
 * FileName    		:  InterestRateTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.interestratetype;

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
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.service.applicationmaster.InterestRateTypeService;
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
 * /WEB-INF/pages/ApplicationMaster/InterestRateType/InterestRateTypeSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class InterestRateTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5934458864351054159L;
	private final static Logger logger = Logger.getLogger(InterestRateTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_InterestRateTypeSearch; 		// autoWired

	protected Textbox 	intRateTypeCode; 					// autoWired
	protected Listbox 	sortOperator_intRateTypeCode; 		// autoWired
	protected Textbox 	intRateTypeDesc;				 	// autoWired
	protected Listbox 	sortOperator_intRateTypeDesc; 		// autoWired
	protected Checkbox 	intRateTypeIsActive; 				// autoWired
	protected Listbox 	sortOperator_intRateTypeIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_InterestRateTypeSearch_RecordStatus; 	// autoWired
	protected Label label_InterestRateTypeSearch_RecordType; 	// autoWired
	protected Label label_InterestRateTypeSearchResult; 		// autoWired

	// not autoWired variables
	private transient InterestRateTypeListCtrl interestRateTypeCtrl; // over handed per parameter
	private transient InterestRateTypeService interestRateTypeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
	.getWorkFlowDetails("InterestRateType");

	/**
	 * constructor
	 */
	public InterestRateTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected InterestRateType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_InterestRateTypeSearch(Event event)throws Exception {
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

		if (args.containsKey("interestRateTypeCtrl")) {
			this.interestRateTypeCtrl = (InterestRateTypeListCtrl) args.get("interestRateTypeCtrl");
		} else {
			this.interestRateTypeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_intRateTypeCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_intRateTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_intRateTypeDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_intRateTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_intRateTypeIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_intRateTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_InterestRateTypeSearch_RecordStatus.setVisible(false);
			this.label_InterestRateTypeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<InterestRateType> searchObj = (JdbcSearchObject<InterestRateType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("intRateTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_intRateTypeCode, filter);
					this.intRateTypeCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("intRateTypeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_intRateTypeDesc, filter);
					this.intRateTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("intRateTypeIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_intRateTypeIsActive, filter);
					//this.intRateTypeIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.intRateTypeIsActive.setChecked(true);
					}else{
						this.intRateTypeIsActive.setChecked(false);
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
		showInterestRateTypeSeekDialog();
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
		this.window_InterestRateTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showInterestRateTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_InterestRateTypeSearch.doModal();
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
		final JdbcSearchObject<InterestRateType> so = new JdbcSearchObject<InterestRateType>(InterestRateType.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTInterestRateTypes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		} else {
			so.addTabelName("BMTInterestRateTypes_AView");
		}

		if (StringUtils.isNotEmpty(this.intRateTypeCode.getValue())) {

			// get the search operator
			final Listitem item_IntRateTypeCode = this.sortOperator_intRateTypeCode.getSelectedItem();

			if (item_IntRateTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_IntRateTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("intRateTypeCode", "%"
							+ this.intRateTypeCode.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("intRateTypeCode",this.intRateTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.intRateTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_IntRateTypeDesc = this.sortOperator_intRateTypeDesc.getSelectedItem();

			if (item_IntRateTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_IntRateTypeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("intRateTypeDesc", "%"
							+ this.intRateTypeDesc.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("intRateTypeDesc",this.intRateTypeDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_IntRateTypeIsActive = this.sortOperator_intRateTypeIsActive.getSelectedItem();

		if (item_IntRateTypeIsActive != null) {
			final int searchOpId = ((SearchOperators) item_IntRateTypeIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.intRateTypeIsActive.isChecked()) {
					so.addFilter(new Filter("intRateTypeIsActive", 1,searchOpId));
				} else {
					so.addFilter(new Filter("intRateTypeIsActive", 0,searchOpId));
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
					so.addFilter(new Filter("recordType", "%"+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("IntRateTypeCode", false);

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
		this.interestRateTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.interestRateTypeCtrl.listBoxInterestRateType;
		final Paging paging = this.interestRateTypeCtrl.pagingInterestRateTypeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<InterestRateType>) listBox.getModel()).init(so,listBox, paging);
		this.interestRateTypeCtrl.setSearchObj(so);

		this.label_InterestRateTypeSearchResult.setValue(Labels.getLabel(
		"label_InterestRateTypeSearchResult.value")+ " "+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setInterestRateTypeService(
			InterestRateTypeService interestRateTypeService) {
		this.interestRateTypeService = interestRateTypeService;
	}
	public InterestRateTypeService getInterestRateTypeService() {
		return this.interestRateTypeService;
	}
}