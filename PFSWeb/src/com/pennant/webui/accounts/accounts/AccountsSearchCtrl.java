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
 * FileName    		:  AccountsSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.accounts.accounts;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class AccountsSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1223104962889927686L;
	private final static Logger logger = Logger.getLogger(AccountsSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL -file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window    window_AcountsSearch;                // autoWired

	protected Textbox     accountId;                         // autoWired
	protected Listbox     sortOperator_accountId;            // autoWired
	protected Textbox     acCcy;                             // autoWired
	protected Listbox     sortOperator_acCcy;                // autoWired
	protected Textbox     acType;                            // autoWired
	protected Listbox     sortOperator_acType;               // autoWired
	protected Textbox     acBranch;                          // autoWired
	protected Listbox     sortOperator_acBranch;             // autoWired
	protected Textbox     acCustCIF;                         // autoWired
	protected Listbox     sortOperator_acCustCIF;            // autoWired
	protected Textbox     acFullName;                        // autoWired
	protected Listbox     sortOperator_acFullName;           // autoWired
	protected Textbox     acShortName;                       // autoWired
	protected Listbox     sortOperator_acShortName;          // autoWired
	protected Combobox    acPurpose;                         // autoWired
	protected Listbox     sortOperator_acPurpose;            // autoWired
	protected Checkbox    internalAc;                        // autoWired
	protected Listbox     sortOperator_internalAc;           // autoWired
	protected Checkbox    custSysAc;                         // autoWired
	protected Listbox     sortOperator_custSysAc;            // autoWired
	protected Decimalbox  acPrvDayBal;                       // autoWired
	protected Listbox     sortOperator_acPrvDayBal;          // autoWired
	protected Decimalbox  acTodayDr;                         // autoWired
	protected Listbox     sortOperator_acTodayDr;            // autoWired
	protected Decimalbox  acTodayCr;                         // autoWired
	protected Listbox     sortOperator_acTodayCr;            // autoWired
	protected Decimalbox  acTodayNet;                        // autoWired
	protected Listbox     sortOperator_acTodayNet;           // autoWired
	protected Decimalbox  acAccrualBal;                      // autoWired
	protected Listbox     sortOperator_acAccrualBal;         // autoWired
	protected Decimalbox  acTodayBal;                        // autoWired
	protected Listbox     sortOperator_acTodayBal;           // autoWired
	protected Datebox     acOpenDate;                        // autoWired
	protected Listbox     sortOperator_acOpenDate;           // autoWired
	protected Datebox     acLastCustTrnDate;                 // autoWired
	protected Listbox     sortOperator_acLastCustTrnDate;    // autoWired
	protected Datebox     acLastSysTrnDate;                  // autoWired
	protected Listbox     sortOperator_acLastSysTrnDate;     // autoWired
	protected Checkbox    acActive;                          // autoWired
	protected Listbox     sortOperator_acActive;             // autoWired
	protected Checkbox    acBlocked;                         // autoWired
	protected Listbox     sortOperator_acBlocked;            // autoWired
	protected Checkbox    acClosed;                          // autoWired
	protected Listbox     sortOperator_acClosed;             // autoWired
	protected Textbox     hostAcNumber;                      // autoWired
	protected Listbox     sortOperator_hostAcNumber;         // autoWired
	protected Textbox     recordStatus;                      // autoWired
	protected Listbox     recordType;	                     // autoWired
	protected Listbox     sortOperator_recordStatus;         // autoWired
	protected Listbox     sortOperator_recordType;           // autoWired

	protected Label label_AcountsSearch_RecordStatus;        // autoWired
	protected Label label_AcountsSearch_RecordType;          // autoWired
	protected Label label_AcountsSearchResult;               // autoWired
	protected Row row_AcCustCIF;
	protected Row row_AcFullName;
	protected Row row_AcShortName;
	// not auto wired variables
	private transient AccountsListCtrl acountsCtrl; // over handed per parameters
	private transient AccountsService accountsService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Accounts");
	private List<ValueLabel>           listAccPurposeType = PennantStaticListUtil.getAccountPurpose();
	/**
	 * constructor
	 */
	public AccountsSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AcountsSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("acountsCtrl")) {
			this.acountsCtrl = (AccountsListCtrl) args.get("acountsCtrl");
		} else {
			this.acountsCtrl = null;
		}
		setListAccountPurpose();
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_accountId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_accountId.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acCustCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acCustCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acFullName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acFullName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acShortName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acShortName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acPurpose.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acPurpose.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_internalAc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_internalAc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custSysAc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custSysAc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acPrvDayBal.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acPrvDayBal.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTodayDr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acTodayDr.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTodayCr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acTodayCr.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTodayNet.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acTodayNet.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acAccrualBal.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acAccrualBal.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTodayBal.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acTodayBal.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acOpenDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acOpenDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acLastCustTrnDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acLastCustTrnDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acLastSysTrnDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_acLastSysTrnDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_acActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acBlocked.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_acBlocked.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acClosed.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_acClosed.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_hostAcNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_hostAcNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_AcountsSearch_RecordStatus.setVisible(false);
			this.label_AcountsSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<Accounts> searchObj = (JdbcSearchObject<Accounts>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("accountId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_accountId, filter);
					this.accountId.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acCcy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acCcy, filter);
					this.acCcy.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acType, filter);
					this.acType.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acBranch")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acBranch, filter);
					this.acBranch.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acCustId")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acCustCIF, filter);
					this.acCustCIF.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acFullName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acFullName, filter);
					this.acFullName.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acShortName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acShortName, filter);
					this.acShortName.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acPurpose")) {

					SearchOperators.restoreStringOperator(this.sortOperator_acPurpose, filter);
					//Upgraded to ZK-6.5.1.1 Changed from get children to get Items
					List<Comboitem> items=this.acPurpose.getItems();
					for(Comboitem comboItem:items){
						if(StringUtils.equals(comboItem.getValue().toString(),filter.getValue().toString())){
							this.acPurpose.setSelectedItem(comboItem);
						}
					}

				} else if (filter.getProperty().equals("internalAc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_internalAc, filter);
					if(StringUtils.trimToEmpty(filter.getValue().toString()).equals("1")){
						this.internalAc.setChecked(true);
					}else{
						this.internalAc.setChecked(false);
					}
					this.internalAc.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("custSysAc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSysAc, filter);
					this.custSysAc.setValue(filter.getValue().toString());
					if(StringUtils.trimToEmpty(filter.getValue().toString()).equals("1")){
						this.custSysAc.setChecked(true);
					}else{
						this.custSysAc.setChecked(false);
					}
				} else if (filter.getProperty().equals("acPrvDayBal")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acPrvDayBal, filter);
					this.acPrvDayBal.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acTodayDr")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acTodayDr, filter);
					this.acTodayDr.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acTodayCr")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acTodayCr, filter);
					this.acTodayCr.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acTodayNet")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acTodayNet, filter);
					this.acTodayNet.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acAccrualBal")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acAccrualBal, filter);
					this.acAccrualBal.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acTodayBal")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acTodayBal, filter);
					this.acTodayBal.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acOpenDate")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acOpenDate, filter);
					this.acOpenDate.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acLastCustTrnDate")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acLastCustTrnDate, filter);
					this.acLastCustTrnDate.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acLastSysTrnDate")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_acLastSysTrnDate, filter);
					this.acLastSysTrnDate.setText(filter.getValue().toString());


				} else if (filter.getProperty().equals("acActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acActive, filter);
					this.acActive.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acBlocked")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acBlocked, filter);
					this.acBlocked.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("acClosed")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acClosed, filter);
					this.acClosed.setValue(filter.getValue().toString());


				} else if (filter.getProperty().equals("hostAcNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hostAcNumber, filter);
					this.hostAcNumber.setValue(filter.getValue().toString());


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
		showAcountsSeekDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setListAccountPurpose() {
		logger.debug("Entering ");
		for (int i = 0; i < listAccPurposeType.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listAccPurposeType.get(i).getLabel());
			comboitem.setValue(listAccPurposeType.get(i).getValue());
			this.acPurpose.appendChild(comboitem);


		}
		this.acPurpose.setSelectedIndex(0);
		logger.debug("Leaving ");
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
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering ");
		this.window_AcountsSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showAcountsSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			if(	this.internalAc.isChecked()){
				this.row_AcCustCIF.setVisible(false);
				this.row_AcFullName.setVisible(false);
				this.row_AcShortName.setVisible(false);
			}
			// open the dialog in modal mode
			this.window_AcountsSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		final JdbcSearchObject<Accounts> so = new JdbcSearchObject<Accounts>(Accounts.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("Accounts_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("Accounts_AView");
		}


		if (StringUtils.isNotEmpty(this.accountId.getValue())) {

			// get the search operator
			final Listitem item_AccountId = this.sortOperator_accountId.getSelectedItem();

			if (item_AccountId != null) {
				final int searchOpId = ((SearchOperators) item_AccountId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("accountId", "%" + this.accountId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("accountId", this.accountId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.acCcy.getValue())) {

			// get the search operator
			final Listitem item_AcCcy = this.sortOperator_acCcy.getSelectedItem();

			if (item_AcCcy != null) {
				final int searchOpId = ((SearchOperators) item_AcCcy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acCcy", "%" + this.acCcy.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acCcy", this.acCcy.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.acType.getValue())) {

			// get the search operator
			final Listitem item_AcType = this.sortOperator_acType.getSelectedItem();

			if (item_AcType != null) {
				final int searchOpId = ((SearchOperators) item_AcType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acType", "%" + this.acType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acType", this.acType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.acBranch.getValue())) {

			// get the search operator
			final Listitem item_AcBranch = this.sortOperator_acBranch.getSelectedItem();

			if (item_AcBranch != null) {
				final int searchOpId = ((SearchOperators) item_AcBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acBranch", "%" + this.acBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acBranch", this.acBranch.getValue(), searchOpId));
				}
			}
		}
		if (this.acCustCIF.getValue()!=null) {	  
			final Listitem item_AcCustId = this.sortOperator_acCustCIF.getSelectedItem();
			if (item_AcCustId != null) {
				final int searchOpId = ((SearchOperators) item_AcCustId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acCustCIF.getValue()!=null){
						so.addFilter(new Filter("lovDescCustCIF", "%" +this.acCustCIF.getValue().toUpperCase() + "%", searchOpId));
					}else{
						so.addFilter(new Filter("lovDescCustCIF",this.acCustCIF.getValue(), searchOpId));	
					}
				}
			}
		}	
		if (StringUtils.isNotEmpty(this.acFullName.getValue())) {

			// get the search operator
			final Listitem item_AcFullName = this.sortOperator_acFullName.getSelectedItem();

			if (item_AcFullName != null) {
				final int searchOpId = ((SearchOperators) item_AcFullName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acFullName", "%" + this.acFullName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acFullName", this.acFullName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.acShortName.getValue())) {

			// get the search operator
			final Listitem item_AcShortName = this.sortOperator_acShortName.getSelectedItem();

			if (item_AcShortName != null) {
				final int searchOpId = ((SearchOperators) item_AcShortName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acShortName", "%" + this.acShortName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acShortName", this.acShortName.getValue(), searchOpId));
				}
			}
		}

		if ((this.acPurpose.getSelectedItem().getValue()!=null )
				&& (!StringUtils.equals(this.acPurpose.getSelectedItem().getLabel()
						,Labels.getLabel("common.Select")))) {
			// get the search operator
			final Listitem item_AcPurpose = this.sortOperator_acPurpose.getSelectedItem();


			if (item_AcPurpose != null) {
				final int searchOpId = ((SearchOperators) item_AcPurpose.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					if (searchOpId == Filter.OP_LIKE) {
						so.addFilter(new Filter("acPurpose", "%" + this.acPurpose.getSelectedItem().getValue() + "%", searchOpId));
					} else if (searchOpId == -1) {
						// do nothing
					} else {
						so.addFilter(new Filter("acPurpose", this.acPurpose.getSelectedItem().getValue(), searchOpId));
					}
				}
			}
		}
		// get the search operator
		final Listitem item_InternalAc = this.sortOperator_internalAc.getSelectedItem();

		if (item_InternalAc != null) {
			final int searchOpId = ((SearchOperators) item_InternalAc.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.internalAc.isChecked()){
					so.addFilter(new Filter("internalAc",1, searchOpId));
				}else{
					so.addFilter(new Filter("internalAc",0, searchOpId));	
				}
			}
		}
		// get the search operator
		final Listitem item_CustSysAc = this.sortOperator_custSysAc.getSelectedItem();

		if (item_CustSysAc != null) {
			final int searchOpId = ((SearchOperators) item_CustSysAc.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.custSysAc.isChecked()){
					so.addFilter(new Filter("custSysAc",1, searchOpId));
				}else{
					so.addFilter(new Filter("custSysAc",0, searchOpId));	
				}
			}
		}
		if (this.acPrvDayBal.getValue()!=null) {	  
			final Listitem item_AcPrvDayBal = this.sortOperator_acPrvDayBal.getSelectedItem();
			if (item_AcPrvDayBal != null) {
				final int searchOpId = ((SearchOperators) item_AcPrvDayBal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acPrvDayBal.getValue()!=null){
						so.addFilter(new Filter("acPrvDayBal",1, searchOpId));
					}else{
						so.addFilter(new Filter("acPrvDayBal",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acTodayDr.getValue()!=null) {	  
			final Listitem item_AcTodayDr = this.sortOperator_acTodayDr.getSelectedItem();
			if (item_AcTodayDr != null) {
				final int searchOpId = ((SearchOperators) item_AcTodayDr.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acTodayDr.getValue()!=null){
						so.addFilter(new Filter("acTodayDr",1, searchOpId));
					}else{
						so.addFilter(new Filter("acTodayDr",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acTodayCr.getValue()!=null) {	  
			final Listitem item_AcTodayCr = this.sortOperator_acTodayCr.getSelectedItem();
			if (item_AcTodayCr != null) {
				final int searchOpId = ((SearchOperators) item_AcTodayCr.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acTodayCr.getValue()!=null){
						so.addFilter(new Filter("acTodayCr",1, searchOpId));
					}else{
						so.addFilter(new Filter("acTodayCr",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acTodayNet.getValue()!=null) {	  
			final Listitem item_AcTodayNet = this.sortOperator_acTodayNet.getSelectedItem();
			if (item_AcTodayNet != null) {
				final int searchOpId = ((SearchOperators) item_AcTodayNet.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acTodayNet.getValue()!=null){
						so.addFilter(new Filter("acTodayNet",1, searchOpId));
					}else{
						so.addFilter(new Filter("acTodayNet",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acAccrualBal.getValue()!=null) {	  
			final Listitem item_AcAccrualBal = this.sortOperator_acAccrualBal.getSelectedItem();
			if (item_AcAccrualBal != null) {
				final int searchOpId = ((SearchOperators) item_AcAccrualBal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acAccrualBal.getValue()!=null){
						so.addFilter(new Filter("acAccrualBal",1, searchOpId));
					}else{
						so.addFilter(new Filter("acAccrualBal",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acTodayBal.getValue()!=null) {	  
			final Listitem item_AcTodayBal = this.sortOperator_acTodayBal.getSelectedItem();
			if (item_AcTodayBal != null) {
				final int searchOpId = ((SearchOperators) item_AcTodayBal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acTodayBal.getValue()!=null){
						so.addFilter(new Filter("acTodayBal",1, searchOpId));
					}else{
						so.addFilter(new Filter("acTodayBal",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acOpenDate.getValue()!=null) {	  
			final Listitem item_AcOpenDate = this.sortOperator_acOpenDate.getSelectedItem();
			if (item_AcOpenDate != null) {
				final int searchOpId = ((SearchOperators) item_AcOpenDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acOpenDate.getValue()!=null){
						so.addFilter(new Filter("acOpenDate",1, searchOpId));
					}else{
						so.addFilter(new Filter("acOpenDate",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acLastCustTrnDate.getValue()!=null) {	  
			final Listitem item_AcLastCustTrnDate = this.sortOperator_acLastCustTrnDate.getSelectedItem();
			if (item_AcLastCustTrnDate != null) {
				final int searchOpId = ((SearchOperators) item_AcLastCustTrnDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acLastCustTrnDate.getValue()!=null){
						so.addFilter(new Filter("acLastCustTrnDate",1, searchOpId));
					}else{
						so.addFilter(new Filter("acLastCustTrnDate",0, searchOpId));	
					}
				}
			}
		}	
		if (this.acLastSysTrnDate.getValue()!=null) {	  
			final Listitem item_AcLastSysTrnDate = this.sortOperator_acLastSysTrnDate.getSelectedItem();
			if (item_AcLastSysTrnDate != null) {
				final int searchOpId = ((SearchOperators) item_AcLastSysTrnDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {

					if(this.acLastSysTrnDate.getValue()!=null){
						so.addFilter(new Filter("acLastSysTrnDate",1, searchOpId));
					}else{
						so.addFilter(new Filter("acLastSysTrnDate",0, searchOpId));	
					}
				}
			}
		}	
		// get the search operator
		final Listitem item_AcInactive = this.sortOperator_acActive.getSelectedItem();

		if (item_AcInactive != null) {
			final int searchOpId = ((SearchOperators) item_AcInactive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.acActive.isChecked()){
					so.addFilter(new Filter("acInactive",1, searchOpId));
				}else{
					so.addFilter(new Filter("acInactive",0, searchOpId));	
				}
			}
		}
		// get the search operator
		final Listitem item_AcBlocked = this.sortOperator_acBlocked.getSelectedItem();

		if (item_AcBlocked != null) {
			final int searchOpId = ((SearchOperators) item_AcBlocked.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.acBlocked.isChecked()){
					so.addFilter(new Filter("acBlocked",1, searchOpId));
				}else{
					so.addFilter(new Filter("acBlocked",0, searchOpId));	
				}
			}
		}
		// get the search operator
		final Listitem item_AcClosed = this.sortOperator_acClosed.getSelectedItem();

		if (item_AcClosed != null) {
			final int searchOpId = ((SearchOperators) item_AcClosed.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.acClosed.isChecked()){
					so.addFilter(new Filter("acClosed",1, searchOpId));
				}else{
					so.addFilter(new Filter("acClosed",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hostAcNumber.getValue())) {

			// get the search operator
			final Listitem item_HostAcNumber = this.sortOperator_hostAcNumber.getSelectedItem();

			if (item_HostAcNumber != null) {
				final int searchOpId = ((SearchOperators) item_HostAcNumber.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hostAcNumber", "%" + this.hostAcNumber.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hostAcNumber", this.hostAcNumber.getValue(), searchOpId));
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
		so.addSort("accountId", false);

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
		this.acountsCtrl.setSearchObj(so);

		final Listbox listBox = this.acountsCtrl.listBoxAcounts;
		final Paging paging = this.acountsCtrl.pagingAcountsList;


		// set the model to the list box with the initial result set get by the DAO method.
		((PagedListWrapper<Accounts>) listBox.getModel()).init(so, listBox, paging);
		this.acountsCtrl.setSearchObj(so);

		this.label_AcountsSearchResult.setValue(Labels.getLabel("label_AcountsSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	logger.debug("Leaving ");
}



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
// ++++++++++++++++++ getter / setter +++++++++++++++++++//
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

public void setAccountsService(AccountsService accountsService) {
	this.accountsService = accountsService;
}

public AccountsService getAccountsService() {
	return this.accountsService;
}
}