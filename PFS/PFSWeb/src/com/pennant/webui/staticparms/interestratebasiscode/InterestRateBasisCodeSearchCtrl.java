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
 * FileName    		:  InterestRateBasisCodeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.staticparms.interestratebasiscode;

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
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.service.staticparms.InterestRateBasisCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class InterestRateBasisCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 559718327699406496L;
	private final static Logger logger = Logger.getLogger(InterestRateBasisCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_InterestRateBasisCodeSearch; // autoWired

	protected Textbox 	intRateBasisCode; 					// autoWired
	protected Listbox 	sortOperator_intRateBasisCode; 		// autoWired
	protected Textbox 	intRateBasisDesc; 					// autoWired
	protected Listbox 	sortOperator_intRateBasisDesc; 		// autoWired
	protected Checkbox 	intRateBasisIsActive; 				// autoWired
	protected Listbox 	sortOperator_intRateBasisIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType;							// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_InterestRateBasisCodeSearch_RecordStatus; // autoWired
	protected Label label_InterestRateBasisCodeSearch_RecordType; 	// autoWired
	protected Label label_InterestRateBasisCodeSearchResult; 		// autoWired

	// not autoWired variables
	private transient InterestRateBasisCodeListCtrl interestRateBasisCodeCtrl; // over handed per parameter
	private transient InterestRateBasisCodeService interestRateBasisCodeService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("InterestRateBasisCode");

	/**
	 * constructor
	 */
	public InterestRateBasisCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected InterestRateBasisCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InterestRateBasisCodeSearch(Event event)throws Exception {
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

		if (args.containsKey("interestRateBasisCodeCtrl")) {
			this.interestRateBasisCodeCtrl = (InterestRateBasisCodeListCtrl) args.get("interestRateBasisCodeCtrl");
		} else {
			this.interestRateBasisCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_intRateBasisCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_intRateBasisCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_intRateBasisDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_intRateBasisDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_intRateBasisIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_intRateBasisIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_InterestRateBasisCodeSearch_RecordStatus.setVisible(false);
			this.label_InterestRateBasisCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<InterestRateBasisCode> searchObj = 
				(JdbcSearchObject<InterestRateBasisCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("intRateBasisCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_intRateBasisCode, filter);
					this.intRateBasisCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("intRateBasisDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_intRateBasisDesc, filter);
					this.intRateBasisDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("intRateBasisIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_intRateBasisIsActive, filter);
					//this.intRateBasisIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.intRateBasisIsActive.setChecked(true);
					}else{
						this.intRateBasisIsActive.setChecked(false);
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
		showInterestRateBasisCodeSeekDialog();
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
		this.window_InterestRateBasisCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showInterestRateBasisCodeSeekDialog()throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_InterestRateBasisCodeSearch.doModal();
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

		final JdbcSearchObject<InterestRateBasisCode> so = new JdbcSearchObject<InterestRateBasisCode>(InterestRateBasisCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTIntRateBasisCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		} else {
			so.addTabelName("BMTIntRateBasisCodes_AView");
		}

		if (StringUtils.isNotEmpty(this.intRateBasisCode.getValue())) {

			// get the search operator
			final Listitem item_IntRateBasisCode = this.sortOperator_intRateBasisCode.getSelectedItem();

			if (item_IntRateBasisCode != null) {
				final int searchOpId = ((SearchOperators) item_IntRateBasisCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("intRateBasisCode", "%"
							+ this.intRateBasisCode.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("intRateBasisCode",this.intRateBasisCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.intRateBasisDesc.getValue())) {

			// get the search operator
			final Listitem item_IntRateBasisDesc = this.sortOperator_intRateBasisDesc.getSelectedItem();

			if (item_IntRateBasisDesc != null) {
				final int searchOpId = ((SearchOperators) item_IntRateBasisDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("intRateBasisDesc", "%"
							+ this.intRateBasisDesc.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("intRateBasisDesc",
							this.intRateBasisDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_IntRateBasisIsActive = this.sortOperator_intRateBasisIsActive.getSelectedItem();

		if (item_IntRateBasisIsActive != null) {
			final int searchOpId = ((SearchOperators) item_IntRateBasisIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.intRateBasisIsActive.isChecked()) {
					so.addFilter(new Filter("intRateBasisIsActive", 1,searchOpId));
				} else {
					so.addFilter(new Filter("intRateBasisIsActive", 0,searchOpId));
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
		so.addSort("IntRateBasisCode", false);

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
		this.interestRateBasisCodeCtrl.setSearchObj(so);
		final Listbox listBox = this.interestRateBasisCodeCtrl.listBoxInterestRateBasisCode;
		final Paging paging = this.interestRateBasisCodeCtrl.pagingInterestRateBasisCodeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<InterestRateBasisCode>) listBox.getModel()).init(so,listBox, paging);
		this.interestRateBasisCodeCtrl.setSearchObj(so);
		this.label_InterestRateBasisCodeSearchResult.setValue(Labels.getLabel(
		"label_InterestRateBasisCodeSearchResult.value")+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setInterestRateBasisCodeService(
			InterestRateBasisCodeService interestRateBasisCodeService) {
		this.interestRateBasisCodeService = interestRateBasisCodeService;
	}
	public InterestRateBasisCodeService getInterestRateBasisCodeService() {
		return this.interestRateBasisCodeService;
	}
}