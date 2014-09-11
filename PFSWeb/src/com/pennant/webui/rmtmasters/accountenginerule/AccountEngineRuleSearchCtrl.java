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
 * FileName    		:  AccountEngineRuleSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountenginerule;

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
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
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
 * /WEB-INF/pages/RMTMasters/AccountEngineRule/AccountEngineRuleSearch.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountEngineRuleSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4205599591178803605L;
	private final static Logger logger = Logger.getLogger(AccountEngineRuleSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  	window_AccountEngineRuleSearch; // auto wired
	
	protected Textbox 	aEEvent; 						// auto wired
	protected Listbox 	sortOperator_aEEvent; 			// auto wired
	protected Textbox 	aERule; 						// auto wired
	protected Listbox 	sortOperator_aERule; 			// auto wired
	protected Textbox 	aERuleDesc; 					// auto wired
	protected Listbox 	sortOperator_aERuleDesc; 		// auto wired
	protected Checkbox 	aEIsSysDefault; 				// auto wired
	protected Listbox 	sortOperator_aEIsSysDefault; 	// auto wired
	protected Textbox 	recordStatus; 					// auto wired
	protected Listbox 	recordType;						// auto wired
	protected Listbox 	sortOperator_recordStatus; 		// auto wired
	protected Listbox 	sortOperator_recordType; 		// auto wired
	
	protected Label label_AccountEngineRuleSearch_RecordStatus; // auto wired
	protected Label label_AccountEngineRuleSearch_RecordType; 	// auto wired
	protected Label label_AccountEngineRuleSearchResult; 		// auto wired

	// not auto wired Var's
	private transient AccountEngineRuleListCtrl accountEngineRuleCtrl; // over handed per parameter
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountEngineRule");
	
	/**
	 * constructor
	 */
	public AccountEngineRuleSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineRule
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AccountEngineRuleSearch(Event event) throws Exception {
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

		if (args.containsKey("accountEngineRuleCtrl")) {
			this.accountEngineRuleCtrl = (AccountEngineRuleListCtrl) args.get("accountEngineRuleCtrl");
		} else {
			this.accountEngineRuleCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_aEEvent.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_aEEvent.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_aERule.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_aERule.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_aERuleDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_aERuleDesc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_aEIsSysDefault.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_aEIsSysDefault.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_AccountEngineRuleSearch_RecordStatus.setVisible(false);
			this.label_AccountEngineRuleSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<AccountEngineRule> searchObj = (JdbcSearchObject<AccountEngineRule>)
					args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("aEEvent")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aEEvent, filter);
					this.aEEvent.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aERule")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aERule, filter);
					this.aERule.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aERuleDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aERuleDesc, filter);
					this.aERuleDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("aEIsSysDefault")) {
					SearchOperators.restoreStringOperator(this.sortOperator_aEIsSysDefault, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.aEIsSysDefault.setChecked(true);
					}else{
						this.aEIsSysDefault.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue()
								.equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showAccountEngineRuleSeekDialog();
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
		this.window_AccountEngineRuleSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showAccountEngineRuleSeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_AccountEngineRuleSearch.doModal();
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

		final JdbcSearchObject<AccountEngineRule> so = new JdbcSearchObject<AccountEngineRule>(
				AccountEngineRule.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTAERules_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			so.addTabelName("RMTAERules_AView");
		}

		if (StringUtils.isNotEmpty(this.aEEvent.getValue())) {

			// get the search operator
			final Listitem item_AEEvent = this.sortOperator_aEEvent.getSelectedItem();

			if (item_AEEvent != null) {
				final int searchOpId = ((SearchOperators) item_AEEvent
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aEEvent", "%"
						+ this.aEEvent.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aEEvent", this.aEEvent.getValue(),searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aERule.getValue())) {

			// get the search operator
			final Listitem item_AERule = this.sortOperator_aERule.getSelectedItem();

			if (item_AERule != null) {
				final int searchOpId = ((SearchOperators) item_AERule
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aERule", "%"
						+ this.aERule.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aERule", this.aERule.getValue(),searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.aERuleDesc.getValue())) {

			// get the search operator
			final Listitem item_AERuleDesc = this.sortOperator_aERuleDesc.getSelectedItem();

			if (item_AERuleDesc != null) {
				final int searchOpId = ((SearchOperators) item_AERuleDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("aERuleDesc", "%"
						+ this.aERuleDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("aERuleDesc", this.aERuleDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_AEIsSysDefault = this.sortOperator_aEIsSysDefault.getSelectedItem();

		if (item_AEIsSysDefault != null) {
			final int searchOpId = ((SearchOperators) item_AEIsSysDefault
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.aEIsSysDefault.isChecked()) {
					so.addFilter(new Filter("aEIsSysDefault", 1, searchOpId));
				} else {
					so.addFilter(new Filter("aEIsSysDefault", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus
					.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
						+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
				final int searchOpId = ((SearchOperators) item_RecordType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%"
							+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("AEEvent", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.accountEngineRuleCtrl.setSearchObj(so);

		final Listbox listBox = this.accountEngineRuleCtrl.listBoxAccountEngineRule;
		final Paging paging = this.accountEngineRuleCtrl.pagingAccountEngineRuleList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<AccountEngineRule>) listBox.getModel()).init(so,
				listBox, paging);

		this.label_AccountEngineRuleSearchResult.setValue(Labels
				.getLabel("label_AccountEngineRuleSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}