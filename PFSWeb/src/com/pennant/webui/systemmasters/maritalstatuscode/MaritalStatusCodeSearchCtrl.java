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
 * FileName    		:  MaritalStatusCodeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.maritalstatuscode;

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
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.service.systemmasters.MaritalStatusCodeService;
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
 * /WEB-INF/pages/SystemMaster/MaritalStatusCode/MaritalStatusCodeSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class MaritalStatusCodeSearchCtrl extends GFCBaseCtrl implements	Serializable {

	private static final long serialVersionUID = 2490602050353638825L;
	private final static Logger logger = Logger.getLogger(MaritalStatusCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_MaritalStatusCodeSearch; 	// autoWired

	protected Textbox 	maritalStsCode; 					// autoWired
	protected Listbox 	sortOperator_maritalStsCode; 		// autoWired
	protected Textbox 	maritalStsDesc; 					// autoWired
	protected Listbox 	sortOperator_maritalStsDesc; 		// autoWired
	protected Checkbox 	maritalStsIsActive; 				// autoWired
	protected Listbox 	sortOperator_maritalStsIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_MaritalStatusCodeSearch_RecordStatus; // autoWired
	protected Label label_MaritalStatusCodeSearch_RecordType; 	// autoWired
	protected Label label_MaritalStatusCodeSearchResult; 		// autoWired

	// not autoWired variables
	private transient MaritalStatusCodeListCtrl maritalStatusCodeCtrl; // over handed per parameter
	private transient MaritalStatusCodeService maritalStatusCodeService;
	private transient WorkFlowDetails  workFlowDetails = WorkFlowUtil.getWorkFlowDetails("MaritalStatusCode");

	/**
	 * constructor
	 */
	public MaritalStatusCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected MaritalStatusCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_MaritalStatusCodeSearch(Event event) throws Exception {
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

		if (args.containsKey("maritalStatusCodeCtrl")) {
			this.maritalStatusCodeCtrl = (MaritalStatusCodeListCtrl) args.get("maritalStatusCodeCtrl");
		} else {
			this.maritalStatusCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_maritalStsCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_maritalStsCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_maritalStsDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_maritalStsDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_maritalStsIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_maritalStsIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_MaritalStatusCodeSearch_RecordStatus.setVisible(false);
			this.label_MaritalStatusCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<MaritalStatusCode> searchObj = (JdbcSearchObject<MaritalStatusCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("maritalStsCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_maritalStsCode, filter);
					this.maritalStsCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("maritalStsDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_maritalStsDesc, filter);
					this.maritalStsDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("maritalStsIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_maritalStsIsActive, filter);
					//this.maritalStsIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.maritalStsIsActive.setChecked(true);
					}else{
						this.maritalStsIsActive.setChecked(false);
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
		showMaritalStatusCodeSeekDialog();
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
		this.window_MaritalStatusCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showMaritalStatusCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_MaritalStatusCodeSearch.doModal();
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
		final JdbcSearchObject<MaritalStatusCode> so = new JdbcSearchObject<MaritalStatusCode>(MaritalStatusCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTMaritalStatusCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		} else {
			so.addTabelName("BMTMaritalStatusCodes_AView");
		}

		if (StringUtils.isNotEmpty(this.maritalStsCode.getValue())) {

			// get the search operator
			final Listitem item_MaritalStsCode = this.sortOperator_maritalStsCode.getSelectedItem();

			if (item_MaritalStsCode != null) {
				final int searchOpId = ((SearchOperators) item_MaritalStsCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("maritalStsCode", "%" + this.maritalStsCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("maritalStsCode", this.maritalStsCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.maritalStsDesc.getValue())) {

			// get the search operator
			final Listitem item_MaritalStsDesc = this.sortOperator_maritalStsDesc.getSelectedItem();

			if (item_MaritalStsDesc != null) {
				final int searchOpId = ((SearchOperators) item_MaritalStsDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("maritalStsDesc", "%" + this.maritalStsDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("maritalStsDesc", this.maritalStsDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_MaritalStsIsActive = this.sortOperator_maritalStsIsActive.getSelectedItem();

		if (item_MaritalStsIsActive != null) {
			final int searchOpId = ((SearchOperators) item_MaritalStsIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.maritalStsIsActive.isChecked()) {
					so.addFilter(new Filter("maritalStsIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("maritalStsIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
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
		so.addSort("MaritalStsCode", false);

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
		this.maritalStatusCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.maritalStatusCodeCtrl.listBoxMaritalStatusCode;
		final Paging paging = this.maritalStatusCodeCtrl.pagingMaritalStatusCodeList;
		this.maritalStatusCodeCtrl.pagingMaritalStatusCodeList.getPageSize();

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<MaritalStatusCode>) listBox.getModel()).init(so, listBox, paging);
		this.maritalStatusCodeCtrl.setSearchObj(so);

		this.label_MaritalStatusCodeSearchResult.setValue(Labels.getLabel(
		"label_MaritalStatusCodeSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setMaritalStatusCodeService(MaritalStatusCodeService maritalStatusCodeService) {
		this.maritalStatusCodeService = maritalStatusCodeService;
	}
	public MaritalStatusCodeService getMaritalStatusCodeService() {
		return this.maritalStatusCodeService;
	}

}