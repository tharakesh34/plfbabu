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
 * FileName    		:  ProfessionSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.profession;

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
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.service.systemmasters.ProfessionService;
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
 * /WEB-INF/pages/SystemMasters/Profession/ProfessionSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class ProfessionSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5053319803285563421L;

	private final static Logger logger = Logger.getLogger(ProfessionSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_ProfessionSearch; 				// autoWired

	protected Textbox 	professionCode; 						// autoWired
	protected Listbox 	sortOperator_professionCode; 			// autoWired
	protected Textbox 	professionDesc; 						// autoWired
	protected Listbox 	sortOperator_professionDesc; 			// autoWired
	protected Checkbox 	professionIsActive; 					// autoWired
	protected Listbox 	sortOperator_professionIsActive;  		// autoWired
	protected Textbox 	recordStatus; 							// autoWired
	protected Listbox 	recordType; 							// autoWired
	protected Listbox 	sortOperator_recordStatus; 				// autoWired
	protected Listbox 	sortOperator_recordType; 				// autoWired

	protected Label 	label_ProfessionSearch_RecordStatus;	// autoWired
	protected Label 	label_ProfessionSearch_RecordType; 		// autoWired
	protected Label 	label_ProfessionSearchResult; 			// autoWired

	// not autoWired variables
	private transient ProfessionListCtrl professionCtrl; 	// over handed per parameter
	private transient ProfessionService professionService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Profession");

	/**
	 * Default constructor
	 */
	public ProfessionSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Profession object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ProfessionSearch(Event event) throws Exception {
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

		if (args.containsKey("professionCtrl")) {
			this.professionCtrl = (ProfessionListCtrl) args.get("professionCtrl");
		} else {
			this.professionCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_professionCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_professionCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_professionDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_professionDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_professionIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_professionIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_ProfessionSearch_RecordStatus.setVisible(false);
			this.label_ProfessionSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<Profession> searchObj = (JdbcSearchObject<Profession>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("professionCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_professionCode, filter);
					this.professionCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("professionDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_professionDesc, filter);
					this.professionDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("professionIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_professionIsActive, filter);
					//this.professionIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.professionIsActive.setChecked(true);
					}else{
						this.professionIsActive.setChecked(false);
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
		showProfessionSeekDialog();
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
		this.window_ProfessionSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showProfessionSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_ProfessionSearch.doModal();
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

		final JdbcSearchObject<Profession> so = new JdbcSearchObject<Profession>(Profession.class);
		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTProfessions_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTProfessions_AView");
		}

		if (StringUtils.isNotEmpty(this.professionCode.getValue())) {

			// get the search operator
			final Listitem item_ProfessionCode = this.sortOperator_professionCode.getSelectedItem();

			if (item_ProfessionCode != null) {
				final int searchOpId = ((SearchOperators) item_ProfessionCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("professionCode", "%" + this.professionCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("professionCode", this.professionCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.professionDesc.getValue())) {

			// get the search operator
			final Listitem item_ProfessionDesc = this.sortOperator_professionDesc.getSelectedItem();

			if (item_ProfessionDesc != null) {
				final int searchOpId = ((SearchOperators) item_ProfessionDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("professionDesc", "%"+ this.professionDesc.getValue().toUpperCase()	+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("professionDesc", this.professionDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_ProfessionIsActive = this.sortOperator_professionIsActive.getSelectedItem();

		if (item_ProfessionIsActive != null) {
			final int searchOpId = ((SearchOperators) item_ProfessionIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.professionIsActive.isChecked()) {
					so.addFilter(new Filter("professionIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("professionIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("ProfessionCode", false);

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
		this.professionCtrl.setSearchObj(so);

		final Listbox listBox = this.professionCtrl.listBoxProfession;
		final Paging paging = this.professionCtrl.pagingProfessionList;
		this.professionCtrl.pagingProfessionList.getPageSize();

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<Profession>) listBox.getModel()).init(so, listBox, paging);
		this.professionCtrl.setSearchObj(so);

		this.label_ProfessionSearchResult.setValue(Labels.getLabel(
		"label_ProfessionSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setProfessionService(ProfessionService professionService) {
		this.professionService = professionService;
	}
	public ProfessionService getProfessionService() {
		return this.professionService;
	}
}