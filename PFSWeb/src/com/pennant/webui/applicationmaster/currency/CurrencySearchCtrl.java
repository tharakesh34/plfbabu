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
 * FileName    		:  CurrencySearchCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.currency;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Currency;
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
 * /WEB-INF/pages/ApplicationMaster/Currency/CurrencySearchDialog.zul. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CurrencySearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2465012597926188148L;
	private final static Logger logger = Logger.getLogger(CurrencySearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_CurrencySearch; 			// autoWired

	protected Textbox  ccyCode; 						// autoWired
	protected Listbox  sortOperator_ccyCode; 			// autoWired
	protected Intbox   ccyNumber; 						// autoWired
	protected Listbox  sortOperator_ccyNumber; 			// autoWired
	protected Textbox  ccyDesc; 						// autoWired
	protected Listbox  sortOperator_ccyDesc; 			// autoWired
	protected Textbox  ccySwiftCode; 					// autoWired
	protected Listbox  sortOperator_ccySwiftCode; 		// autoWired
	protected Textbox  ccySymbol; 						// autoWired
	protected Listbox  sortOperator_ccySymbol; 			// autoWired
	protected Textbox  ccyMinorCcyDesc; 				// autoWired
	protected Listbox  sortOperator_ccyMinorCcyDesc; 	// autoWired
	protected Checkbox ccyIsAlwForLoans; 				// autoWired
	protected Listbox  sortOperator_ccyIsAlwForLoans; 	// autoWired
	protected Checkbox ccyIsAlwForDepo; 				// autoWired
	protected Listbox  sortOperator_ccyIsAlwForDepo; 	// autoWired
	protected Checkbox ccyIsAlwForAc; 					// autoWired
	protected Listbox  sortOperator_ccyIsAlwForAc; 		// autoWired
	protected Checkbox ccyIsActive; 					// autoWired
	protected Listbox  sortOperator_ccyIsActive; 		// autoWired
	protected Textbox  recordStatus; 					// autoWired
	protected Listbox  recordType;						// autoWired
	protected Listbox  sortOperator_recordStatus; 		// autoWired
	protected Listbox  sortOperator_recordType; 		// autoWired

	protected Label label_CurrencySearch_RecordStatus; 	// autoWired
	protected Label label_CurrencySearch_RecordType; 	// autoWired
	protected Label label_CurrencySearchResult; 		// autoWired

	// not auto wired vars
	private transient CurrencyListCtrl currencyCtrl; 	// overHanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Currency");

	/**
	 * constructor
	 */
	public CurrencySearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Segment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CurrencySearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the params map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("currencyCtrl")) {
			this.currencyCtrl = (CurrencyListCtrl) args.get("currencyCtrl");
		} else {
			this.currencyCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_ccyCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_ccyCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyNumber.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_ccyNumber.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_ccyDesc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccySwiftCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_ccySwiftCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccySymbol.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_ccySymbol.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyMinorCcyDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_ccyMinorCcyDesc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyIsAlwForLoans.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_ccyIsAlwForLoans.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyIsAlwForDepo.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_ccyIsAlwForDepo.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyIsAlwForAc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_ccyIsAlwForAc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_ccyIsActive.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_ccyIsActive.setItemRenderer(
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
			this.label_CurrencySearch_RecordStatus.setVisible(false);
			this.label_CurrencySearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<Currency> searchObj = (JdbcSearchObject<Currency>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("ccyCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_ccyCode, filter);
					this.ccyCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ccyNumber")) {
					SearchOperators.restoreNumericOperator(
							this.sortOperator_ccyNumber, filter);
					this.ccyNumber.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("ccyDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_ccyDesc, filter);
					this.ccyDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ccySwiftCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_ccySwiftCode, filter);
					this.ccySwiftCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ccySymbol")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_ccySymbol, filter);
					this.ccySymbol.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ccyMinorCcyDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_ccyMinorCcyDesc, filter);
					this.ccyMinorCcyDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("ccyIsAlwForLoans")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_ccyIsAlwForLoans, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.ccyIsAlwForLoans.setChecked(true);
					}else{
						this.ccyIsAlwForLoans.setChecked(false);
					}
				} else if (filter.getProperty().equals("ccyIsAlwForDepo")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_ccyIsAlwForDepo, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.ccyIsAlwForDepo.setChecked(true);
					}else{
						this.ccyIsAlwForDepo.setChecked(false);
					}
				} else if (filter.getProperty().equals("ccyIsAlwForAc")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_ccyIsAlwForAc, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.ccyIsAlwForAc.setChecked(true);
					}else{
						this.ccyIsAlwForAc.setChecked(false);
					}
				} else if (filter.getProperty().equals("ccyIsActive")) {
					SearchOperators.restoreBooleanOperators(
							this.sortOperator_ccyIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.ccyIsActive.setChecked(true);
					}else{
						this.ccyIsActive.setChecked(false);
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
		showCurrencySeekDialog();
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
		logger.debug("Entering ");
		this.window_CurrencySearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCurrencySeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_CurrencySearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
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
	 * 1. Checks for each TextBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering ");
		final JdbcSearchObject<Currency> so = new JdbcSearchObject<Currency>(
				Currency.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTCurrencies_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			so.addTabelName("RMTCurrencies_AView");
		}

		if (StringUtils.isNotEmpty(this.ccyCode.getValue())) {

			// get the search operator
			final Listitem itemCcyCode = this.sortOperator_ccyCode.getSelectedItem();
			if (itemCcyCode != null) {
				final int searchOpId = ((SearchOperators) itemCcyCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ccyCode", "%"
							+ this.ccyCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ccyCode", this.ccyCode.getValue(),
							searchOpId));
				}
			}
		}
		if (this.ccyNumber.intValue() != 0) {

			// get the search operator
			final Listitem itemCcyNumber = this.sortOperator_ccyNumber.getSelectedItem();
			if (itemCcyNumber != null) {
				final int searchOpId = ((SearchOperators) itemCcyNumber
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ccyNumber", String.valueOf(this.ccyNumber
							.intValue()), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.ccyDesc.getValue())) {

			// get the search operator
			final Listitem itemCcyDesc = this.sortOperator_ccyDesc.getSelectedItem();
			if (itemCcyDesc != null) {
				final int searchOpId = ((SearchOperators) itemCcyDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ccyDesc", "%"
							+ this.ccyDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ccyDesc", this.ccyDesc.getValue(),
							searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.ccySwiftCode.getValue())) {

			// get the search operator
			final Listitem itemCcySwiftCode = this.sortOperator_ccySwiftCode.getSelectedItem();
			if (itemCcySwiftCode != null) {
				final int searchOpId = ((SearchOperators) itemCcySwiftCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ccySwiftCode", "%"
							+ this.ccySwiftCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ccySwiftCode", this.ccySwiftCode
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.ccySymbol.getValue())) {

			// get the search operator
			final Listitem itemCcySymbol = this.sortOperator_ccySymbol.getSelectedItem();
			if (itemCcySymbol != null) {
				final int searchOpId = ((SearchOperators) itemCcySymbol
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ccySymbol", "%"
							+ this.ccySymbol.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ccySymbol", this.ccySymbol
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.ccyMinorCcyDesc.getValue())) {

			// get the search operator
			final Listitem itemCcyMinorCcyDesc = this.sortOperator_ccyMinorCcyDesc.getSelectedItem();
			if (itemCcyMinorCcyDesc != null) {
				final int searchOpId = ((SearchOperators) itemCcyMinorCcyDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("ccyMinorCcyDesc", "%"
							+ this.ccyMinorCcyDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("ccyMinorCcyDesc", this.ccyMinorCcyDesc
							.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemCcyIsAlwForLoans = this.sortOperator_ccyIsAlwForLoans.getSelectedItem();
		if (itemCcyIsAlwForLoans != null) {
			final int searchOpId = ((SearchOperators) itemCcyIsAlwForLoans
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.ccyIsAlwForLoans.isChecked()) {
					so.addFilter(new Filter("ccyIsAlwForLoans", 1, searchOpId));
				} else {
					so.addFilter(new Filter("ccyIsAlwForLoans", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemCcyIsAlwForDepo = this.sortOperator_ccyIsAlwForDepo.getSelectedItem();
		if (itemCcyIsAlwForDepo != null) {
			final int searchOpId = ((SearchOperators) itemCcyIsAlwForDepo
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.ccyIsAlwForDepo.isChecked()) {
					so.addFilter(new Filter("ccyIsAlwForDepo", 1, searchOpId));
				} else {
					so.addFilter(new Filter("ccyIsAlwForDepo", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemCcyIsAlwForAc = this.sortOperator_ccyIsAlwForAc.getSelectedItem();
		if (itemCcyIsAlwForAc != null) {
			final int searchOpId = ((SearchOperators) itemCcyIsAlwForAc
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.ccyIsAlwForAc.isChecked()) {
					so.addFilter(new Filter("ccyIsAlwForAc", 1, searchOpId));
				} else {
					so.addFilter(new Filter("ccyIsAlwForAc", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemCcyIsActive = this.sortOperator_ccyIsActive.getSelectedItem();
		if (itemCcyIsActive != null) {
			final int searchOpId = ((SearchOperators) itemCcyIsActive
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.ccyIsActive.isChecked()) {
					so.addFilter(new Filter("ccyIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("ccyIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType != null) {
				final int searchOpId = ((SearchOperators) itemRecordType
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
		so.addSort("CcyCode", false);

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
		this.currencyCtrl.setSearchObj(so);

		final Listbox listBox = this.currencyCtrl.listBoxCurrency;
		final Paging paging = this.currencyCtrl.pagingCurrencyList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<Currency>) listBox.getModel()).init(so, listBox,paging);
		this.currencyCtrl.setSearchObj(so);

		this.label_CurrencySearchResult.setValue(Labels
				.getLabel("label_CurrencySearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

}