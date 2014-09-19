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
 * FileName    		:  FrequencySearchCtrl.java                                                   * 	  
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

package com.pennant.webui.staticparms.frequency;

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
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.service.staticparms.FrequencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class FrequencySearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5558828798169970613L;
	private final static Logger logger = Logger.getLogger(FrequencySearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_FrequencySearch; 	// autoWired

	protected Textbox 	frqCode; 					// autoWired
	protected Listbox 	sortOperator_frqCode; 		// autoWired
	protected Textbox 	frqDesc;		 			// autoWired
	protected Listbox 	sortOperator_frqDesc; 		// autoWired
	protected Checkbox	frqIsActive; 				// autoWired
	protected Listbox 	sortOperator_frqIsActive;	// autoWired
	protected Textbox 	recordStatus; 				// autoWired
	protected Listbox 	recordType; 				// autoWired
	protected Listbox 	sortOperator_recordStatus; 	// autoWired
	protected Listbox 	sortOperator_recordType; 	// autoWired

	protected Label label_FrequencySearch_RecordStatus; // autoWired
	protected Label label_FrequencySearch_RecordType; 	// autoWired
	protected Label label_FrequencySearchResult; 		// autoWired

	// not autoWired variables
	private transient FrequencyListCtrl frequencyCtrl; // over handed per parameter
	private transient FrequencyService frequencyService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Frequency");

	/**
	 * constructor
	 */
	public FrequencySearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Frequency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FrequencySearch(Event event) throws Exception {
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

		if (args.containsKey("frequencyCtrl")) {
			this.frequencyCtrl = (FrequencyListCtrl) args.get("frequencyCtrl");
		} else {
			this.frequencyCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_frqCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_frqCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_frqDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_frqDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_frqIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_frqIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_FrequencySearch_RecordStatus.setVisible(false);
			this.label_FrequencySearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<Frequency> searchObj = (JdbcSearchObject<Frequency>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("frqCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_frqCode, filter);
					this.frqCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("frqDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_frqDesc, filter);
					this.frqDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("frqIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_frqIsActive, filter);
					//this.frqIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.frqIsActive.setChecked(true);
					}else{
						this.frqIsActive.setChecked(false);
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
		showFrequencySeekDialog();
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
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_FrequencySearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFrequencySeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_FrequencySearch.doModal();
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

		final JdbcSearchObject<Frequency> so = new JdbcSearchObject<Frequency>(Frequency.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTFrequencies_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		} else {
			so.addTabelName("BMTFrequencies_AView");
		}
		if (StringUtils.isNotEmpty(this.frqCode.getValue())) {

			// get the search operator
			final Listitem itemFrqCode = this.sortOperator_frqCode.getSelectedItem();
			if (itemFrqCode != null) {
				final int searchOpId = ((SearchOperators) itemFrqCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("frqCode", "%" + this.frqCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("frqCode", this.frqCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.frqDesc.getValue())) {

			// get the search operator
			final Listitem itemFrqDesc = this.sortOperator_frqDesc.getSelectedItem();
			if (itemFrqDesc != null) {
				final int searchOpId = ((SearchOperators) itemFrqDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("frqDesc", "%" + this.frqDesc.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("frqDesc", this.frqDesc.getValue(),	searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemFrqIsActive = this.sortOperator_frqIsActive.getSelectedItem();
		if (itemFrqIsActive != null) {
			final int searchOpId = ((SearchOperators) itemFrqIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.frqIsActive.isChecked()) {
					so.addFilter(new Filter("frqIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("frqIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"	+ this.recordStatus.getValue().toUpperCase() + "%",	searchOpId));
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType != null) {
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
		so.addSort("FrqCode", false);

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
		this.frequencyCtrl.setSearchObj(so);

		final Listbox listBox = this.frequencyCtrl.listBoxFrequency;
		final Paging paging = this.frequencyCtrl.pagingFrequencyList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<Frequency>) listBox.getModel()).init(so, listBox, paging);
		this.frequencyCtrl.setSearchObj(so);

		this.label_FrequencySearchResult.setValue(Labels.getLabel(
		"label_FrequencySearchResult.value") + " "	+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFrequencyService(FrequencyService frequencyService) {
		this.frequencyService = frequencyService;
	}
	public FrequencyService getFrequencyService() {
		return this.frequencyService;
	}
}