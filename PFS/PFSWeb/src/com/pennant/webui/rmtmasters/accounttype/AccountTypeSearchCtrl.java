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
 * FileName    		:  AccountTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accounttype;

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
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.AccountType;
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
 * WEB-INF/pages/SolutionFactory/AccountType/AccountTypeSearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -1255625811728534720L;
	private final static Logger logger = Logger.getLogger(AccountTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_AccountTypeSearch; 			// auto wired

	protected Textbox 	acType; 							// auto wired
	protected Listbox 	sortOperator_acType; 				// auto wired
	protected Textbox 	acTypeDesc; 						// auto wired
	protected Listbox 	sortOperator_acTypeDesc; 			// auto wired
	protected Combobox 	acPurpose; 							// auto wired
	protected Listbox 	sortOperator_acPurpose; 			// auto wired
	protected Checkbox 	internalAc; 						// auto wired
	protected Listbox 	sortOperator_internalAc; 			// auto wired
	protected Checkbox 	acTypeIsActive; 					// auto wired
	protected Listbox 	sortOperator_acTypeIsActive; 		// auto wired
	protected Textbox 	recordStatus; 						// auto wired
	protected Listbox 	recordType;							// auto wired
	protected Listbox 	sortOperator_recordStatus; 			// auto wired
	protected Listbox 	sortOperator_recordType; 			// auto wired

	protected Label label_AccountTypeSearch_RecordStatus; 	// auto wired
	protected Label label_AccountTypeSearch_RecordType; 	// auto wired
	protected Label label_AccountTypeSearchResult; 			// auto wired

	// not auto wired Var's
	private transient AccountTypeListCtrl 	accountTypeCtrl; // over handed per parameter
	private transient WorkFlowDetails 		workFlowDetails = WorkFlowUtil
	.getWorkFlowDetails("AccountType");
	private List<ValueLabel>           listAccPurposeType = PennantAppUtil.getAccountPurpose();
	/**
	 * constructor
	 */
	public AccountTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AccountTypeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("accountTypeCtrl")) {
			this.accountTypeCtrl = (AccountTypeListCtrl) args
			.get("accountTypeCtrl");
		} else {
			this.accountTypeCtrl = null;
		}
		setListAccountPurpose();
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_acType.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_acType.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTypeDesc.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_acTypeDesc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_acPurpose.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_acPurpose.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_internalAc.setModel(new ListModelList(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_internalAc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTypeIsActive.setModel(new ListModelList(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_acTypeIsActive.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_AccountTypeSearch_RecordStatus.setVisible(false);
			this.label_AccountTypeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<AccountType> searchObj = (JdbcSearchObject<AccountType>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("acType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_acType, filter);
					this.acType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("acTypeDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_acTypeDesc, filter);
					this.acTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("acPurpose")) {
					SearchOperators.restoreStringOperator(this.sortOperator_acPurpose, filter);
					//Upgraded to ZK-6.5.1.1 Changed from get children to get items 	
					List<Comboitem> items=this.acPurpose.getItems();
					for(Comboitem comboItem:items){
						if(StringUtils.equals(comboItem.getValue().toString(),filter.getValue().toString())){
							this.acPurpose.setSelectedItem(comboItem);
						}
					}
				} else if (filter.getProperty().equals("internalAc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_internalAc, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.internalAc.setChecked(true);
					}else{
						this.internalAc.setChecked(false);
					}
				} else if (filter.getProperty().equals("acTypeIsActive")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_acTypeIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.acTypeIsActive.setChecked(true);
					}else{
						this.acTypeIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordStatus, filter);
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
		showAccountTypeSeekDialog();
		logger.debug("Leaving" + event.toString());
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
		this.window_AccountTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showAccountTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_AccountTypeSearch.doModal();
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

		final JdbcSearchObject<AccountType> so = new JdbcSearchObject<AccountType>(
				AccountType.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTAccountTypes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			so.addTabelName("RMTAccountTypes_AView");
		}

		if (StringUtils.isNotEmpty(this.acType.getValue())) {

			// get the search operator
			final Listitem item_AcType = this.sortOperator_acType
			.getSelectedItem();

			if (item_AcType != null) {
				final int searchOpId = ((SearchOperators) item_AcType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acType", "%"
							+ this.acType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acType", this.acType.getValue(),
							searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.acTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_AcTypeDesc = this.sortOperator_acTypeDesc
			.getSelectedItem();

			if (item_AcTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_AcTypeDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("acTypeDesc", "%"
							+ this.acTypeDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("acTypeDesc", this.acTypeDesc
							.getValue(), searchOpId));
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
		final Listitem item_InternalAc = this.sortOperator_internalAc
		.getSelectedItem();

		if (item_InternalAc != null) {
			final int searchOpId = ((SearchOperators) item_InternalAc
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.internalAc.isChecked()) {
					so.addFilter(new Filter("internalAc", 1, searchOpId));
				} else {
					so.addFilter(new Filter("internalAc", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_AcTypeIsActive = this.sortOperator_acTypeIsActive
		.getSelectedItem();

		if (item_AcTypeIsActive != null) {
			final int searchOpId = ((SearchOperators) item_AcTypeIsActive
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.acTypeIsActive.isChecked()) {
					so.addFilter(new Filter("acTypeIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("acTypeIsActive", 0, searchOpId));
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
							+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus
							.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType
			.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType
						.getAttribute("data")).getSearchOperatorId();

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
		so.addSort("AcType", false);

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
		this.accountTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.accountTypeCtrl.listBoxAccountType;
		final Paging paging = this.accountTypeCtrl.pagingAccountTypeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<AccountType>) listBox.getModel()).init(so, listBox,paging);
		this.accountTypeCtrl.setSearchObj(so);

		this.label_AccountTypeSearchResult.setValue(Labels
				.getLabel("label_AccountTypeSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}