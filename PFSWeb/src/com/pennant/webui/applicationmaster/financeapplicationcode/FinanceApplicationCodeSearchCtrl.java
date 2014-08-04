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
 * FileName    		:  FinanceApplicationCodeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.financeapplicationcode;

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
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.service.applicationmaster.FinanceApplicationCodeService;
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
 * /WEB-INF/pages/ApplicationMaster/FinanceApplicationCode/FinanceApplicationCodeSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class FinanceApplicationCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -514275959820860154L;
	private final static Logger logger = Logger.getLogger(FinanceApplicationCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_FinanceApplicationCodeSearch;// autoWired

	protected Textbox 	finAppType; 						// autoWired
	protected Listbox 	sortOperator_finAppType; 			// autoWired
	protected Textbox 	finAppDesc; 						// autoWired
	protected Listbox 	sortOperator_finAppDesc; 			// autoWired
	protected Checkbox 	finAppIsActive; 					// autoWired
	protected Listbox 	sortOperator_finAppIsActive; 		// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_FinanceApplicationCodeSearch_RecordStatus;	// autoWired
	protected Label label_FinanceApplicationCodeSearch_RecordType; 		// autoWired
	protected Label label_FinanceApplicationCodeSearchResult; 			// autoWired

	// not autoWired variables
	private transient FinanceApplicationCodeListCtrl financeApplicationCodeCtrl; // over handed per parameter
	private transient FinanceApplicationCodeService financeApplicationCodeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceApplicationCode");

	/**
	 * constructor
	 */
	public FinanceApplicationCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSegment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceApplicationCodeSearch(Event event) throws Exception {
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

		if (args.containsKey("financeApplicationCodeCtrl")) {
			this.financeApplicationCodeCtrl = (FinanceApplicationCodeListCtrl) args.get("financeApplicationCodeCtrl");
		} else {
			this.financeApplicationCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_finAppType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finAppType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finAppDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finAppDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finAppIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finAppIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_FinanceApplicationCodeSearch_RecordStatus.setVisible(false);
			this.label_FinanceApplicationCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<FinanceApplicationCode> searchObj = (JdbcSearchObject<FinanceApplicationCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("finAppType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finAppType, filter);
					this.finAppType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finAppDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finAppDesc, filter);
					this.finAppDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finAppIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finAppIsActive, filter);
					//this.finAppIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finAppIsActive.setChecked(true);
					}else{
						this.finAppIsActive.setChecked(false);
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
		showFinanceApplicationCodeSeekDialog();
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
		this.window_FinanceApplicationCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceApplicationCodeSeekDialog()	throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_FinanceApplicationCodeSearch.doModal();
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

		final JdbcSearchObject<FinanceApplicationCode> so = new JdbcSearchObject<FinanceApplicationCode>(FinanceApplicationCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTFinanceApplicaitonCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTFinanceApplicaitonCodes_AView");
		}

		if (StringUtils.isNotEmpty(this.finAppType.getValue())) {

			// get the search operator
			final Listitem item_FinAppType = this.sortOperator_finAppType.getSelectedItem();

			if (item_FinAppType != null) {
				final int searchOpId = ((SearchOperators) item_FinAppType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finAppType", "%" + this.finAppType.getValue().toUpperCase() + "%",	searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finAppType", this.finAppType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finAppDesc.getValue())) {

			// get the search operator
			final Listitem item_FinAppDesc = this.sortOperator_finAppDesc.getSelectedItem();

			if (item_FinAppDesc != null) {
				final int searchOpId = ((SearchOperators) item_FinAppDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finAppDesc", "%" + this.finAppDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finAppDesc", this.finAppDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_FinAppIsActive = this.sortOperator_finAppIsActive.getSelectedItem();

		if (item_FinAppIsActive != null) {
			final int searchOpId = ((SearchOperators) item_FinAppIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {
				if (this.finAppIsActive.isChecked()) {
					so.addFilter(new Filter("finAppIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finAppIsActive", 0, searchOpId));
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
		so.addSort("FinAppType", false);

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
		this.financeApplicationCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.financeApplicationCodeCtrl.listBoxFinanceApplicationCode;
		final Paging paging = this.financeApplicationCodeCtrl.pagingFinanceApplicationCodeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<FinanceApplicationCode>) listBox.getModel()).init(so, listBox, paging);
		this.financeApplicationCodeCtrl.setSearchObj(so);

		this.label_FinanceApplicationCodeSearchResult.setValue(Labels.getLabel(
		"label_FinanceApplicationCodeSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceApplicationCodeService(FinanceApplicationCodeService financeApplicationCodeService) {
		this.financeApplicationCodeService = financeApplicationCodeService;
	}
	public FinanceApplicationCodeService getFinanceApplicationCodeService() {
		return this.financeApplicationCodeService;
	}
}