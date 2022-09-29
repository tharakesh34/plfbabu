/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : BeneficiarySelectCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-05-2020 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-05-2020 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.beneficiary.beneficiary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.beneficiary.beneficiary.model.BeneficiarySelectItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Beneficiary/BeneficiarySelect.zul file.
 */
public class BeneficiarySelectCtrl extends GFCBaseCtrl<Beneficiary> {
	private static final long serialVersionUID = -2873070081817788952L;
	private static final Logger logger = LogManager.getLogger(BeneficiarySelectCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BeneficiarySelect; // autowired
	protected ExtendedCombobox custCIF; // autowired
	protected Listbox sortOperator_custCIF; // autowired
	protected Textbox bankName; // autowired
	protected Listbox sortOperator_bankName; // autowired
	protected Textbox branchDesc; // autowired
	protected Listbox sortOperator_branchDesc; // autowired
	protected Textbox city; // autowired
	protected Listbox sortOperator_city; // autowired
	protected Textbox accNumber; // autowired
	protected Listbox sortOperator_accNumber; // autowired
	protected Textbox accHolderName; // autowired
	protected Listbox sortOperator_accHolderName; // autowired
	protected Paging pagingBeneficiaryList; // autowired
	protected Listbox listBoxBeneficiary; // autowired
	protected Grid searchGrid; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_BankName; // autowired
	protected Listheader listheader_BranchDesc; // autowired
	protected Listheader listheader_City; // autowired
	protected Listheader listheader_AccNumber; // autowired
	protected Listheader listheader_AccHolderName; // autowired
	protected Borderlayout borderLayout_BeneficiarySelect;

	// not auto wired vars
	private transient Object dialogCtrl = null;
	private JdbcSearchObject<Beneficiary> searchObj;
	private List<Filter> filterList = new ArrayList<Filter>();
	protected Button btnClear;
	private List<String> custCIFs = null;
	private String primaryCIF = null;

	/**
	 * Default constructor
	 */
	public BeneficiarySelectCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the Search window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_BeneficiarySelect(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BeneficiarySelect);

		doSetSearchOperators();

		if (arguments.containsKey("DialogCtrl")) {
			setDialogCtrl(arguments.get("DialogCtrl"));
		}
		if (arguments.containsKey("filtersList")) {
			filterList = (List<Filter>) arguments.get("filtersList");
		}
		if (arguments.containsKey("custCIFs")) {
			custCIFs = (List<String>) arguments.get("custCIFs");
		}
		if (arguments.containsKey("custCIF")) {
			primaryCIF = (String) arguments.get("custCIF");
		}

		doSetFieldProperties();
		// Stored search object and paging
		this.borderLayout_BeneficiarySelect.setHeight(borderLayoutHeight + "px");
		this.listBoxBeneficiary.setHeight(getListBoxHeight(this.searchGrid.getRows().getVisibleItemCount() + 1));
		this.pagingBeneficiaryList.setPageSize(getListRows());
		this.pagingBeneficiaryList.setDetailed(true);
		doSetFilters();
		showBeneficiarySeekDialog();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSetSearchOperators() {
		// DropDown ListBox
		List<SearchOperators> list = new SearchOperators().getSimpleStringOperators();
		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_bankName.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_bankName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_branchDesc.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_branchDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_city.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_city.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_accHolderName.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_accHolderName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_accNumber.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_accNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());
	}

	private void doSetFilters() {
		// Render Search Object
		paging(getSearchObj());
		// get the filters from the searchObject
		final List<Filter> ft = searchObj.getFilters();
		for (final Filter filter : ft) {
			// restore founded properties
			if ("CustCIF".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custCIF, filter);
				this.custCIF.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCIF));
			} else if ("BankName".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_bankName, filter);
				this.bankName.setValue(restoreString(filter.getValue().toString(), this.sortOperator_bankName));
			} else if ("BranchDesc".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_branchDesc, filter);
				this.branchDesc.setValue(restoreString(filter.getValue().toString(), this.sortOperator_branchDesc));
			} else if ("City".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_city, filter);
				this.city.setValue(restoreString(filter.getValue().toString(), this.sortOperator_city));
			} else if ("AccNumber".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_accNumber, filter);
				this.accNumber.setValue(restoreString(filter.getValue().toString(), this.sortOperator_accNumber));
			} else if ("AccHolderName".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_accHolderName, filter);
				this.accHolderName
						.setValue(restoreString(filter.getValue().toString(), this.sortOperator_accHolderName));
			}
		}
	}

	/**
	 * Method for replacing LIKE '%' operator in String of SearchObject
	 * 
	 * @param filterValue
	 * @param listbox
	 * @return
	 */
	private String restoreString(String filterValue, Listbox listbox) {
		if (listbox.getSelectedIndex() == 3) {
			return StringUtils.replaceChars(filterValue, "%", "");
		}
		return filterValue;
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doSearch();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showBeneficiarySeekDialog() {
		logger.debug(Literal.ENTERING);
		try {
			// open the dialog in modal mode
			this.window_BeneficiarySelect.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	public void doSearch() {
		logger.debug(Literal.ENTERING);

		JdbcSearchObject<Beneficiary> searchObject = getSearchObj();
		searchObject.clearFilters();
		if (StringUtils.isNotBlank(this.custCIF.getValue())) {

			// get the search operator
			final Listitem itemCustCIF = this.sortOperator_custCIF.getSelectedItem();
			if (itemCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("CustCIF", "%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustCIF", this.custCIF.getValue(), searchOpId));
					searchObject.addFilter(new Filter("BeneficiaryActive", 1, Filter.OP_EQUAL));
				}
			}
		}

		if (StringUtils.isNotBlank(this.bankName.getValue())) {

			// get the search operator
			final Listitem itemCustDftBranch = this.sortOperator_bankName.getSelectedItem();
			if (itemCustDftBranch != null) {
				final int searchOpId = ((SearchOperators) itemCustDftBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("BankName", "%" + this.bankName.getValue() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("BankName", this.bankName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotBlank(this.branchDesc.getValue())) {
			// get the search operator
			final Listitem itemCustCtgCode = this.sortOperator_branchDesc.getSelectedItem();
			if (itemCustCtgCode != null) {
				final int searchOpId = ((SearchOperators) itemCustCtgCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("BranchDesc", "%" + this.branchDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("BranchDesc", this.branchDesc.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.city.getValue())) {

			// get the search operator
			final Listitem itemCustTypeCode = this.sortOperator_city.getSelectedItem();
			if (itemCustTypeCode != null) {
				final int searchOpId = ((SearchOperators) itemCustTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject
							.addFilter(new Filter("City", "%" + this.city.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("City", this.city.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.accNumber.getValue())) {

			// get the search operator
			final Listitem itemCustType = this.sortOperator_accNumber.getSelectedItem();
			if (itemCustType != null) {
				final int searchOpId = ((SearchOperators) itemCustType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("AccNumber", "%" + this.accNumber.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("AccNumber", this.accNumber.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.accHolderName.getValue())) {

			// get the search operator
			final Listitem itemCustMName = this.sortOperator_accHolderName.getSelectedItem();
			if (itemCustMName != null) {
				final int searchOpId = ((SearchOperators) itemCustMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("AccHolderName",
							"%" + this.accHolderName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("accHolderName", this.accHolderName.getValue(), searchOpId));
				}
			}
		}
		// we have to display only supplied customer beneficiaries only
		if (CollectionUtils.isNotEmpty(custCIFs)) {
			// Filters for the Customer CIF
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("CustCIF", custCIFs, Filter.OP_IN);
			searchObject.addFilters(filter);
		}
		// Default Sort on the table
		searchObject.addSort("CustCIF", false);

		setSearchObj(searchObject);
		paging(searchObject);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Render the getting list and set the pagination
	 * 
	 * @param searchObj
	 */
	private void paging(JdbcSearchObject<Beneficiary> searchObj) {
		this.pagingBeneficiaryList.setDetailed(true);
		this.listBoxBeneficiary.setItemRenderer(new BeneficiarySelectItemRenderer());
		getPagedListWrapper().init(searchObj, this.listBoxBeneficiary, this.pagingBeneficiaryList);
	}

	// when item double clicked
	@SuppressWarnings("rawtypes")
	public void onBeneficiaryItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (this.listBoxBeneficiary.getSelectedItem() != null) {
			final Listitem li = this.listBoxBeneficiary.getSelectedItem();
			final Object object = li.getAttribute("data");

			if (getDialogCtrl() != null) {
				dialogCtrl = (Object) getDialogCtrl();
			}
			try {

				Class[] paramType = { Class.forName("java.lang.Object") };
				Object[] stringParameter = { object };
				if (dialogCtrl.getClass().getMethod("doFillBeneficiaryDetails", paramType) != null) {
					dialogCtrl.getClass().getMethod("doFillBeneficiaryDetails", paramType).invoke(dialogCtrl,
							stringParameter);
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		doClose(false);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClear(Event event) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue("");
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.bankName.setValue("");
		this.sortOperator_bankName.setSelectedIndex(0);
		this.city.setValue("");
		this.sortOperator_city.setSelectedIndex(0);
		this.accHolderName.setValue("");
		this.sortOperator_accHolderName.setSelectedIndex(0);
		this.accNumber.setValue("");
		this.sortOperator_accNumber.setSelectedIndex(0);
		this.branchDesc.setValue("");
		this.sortOperator_branchDesc.setSelectedIndex(0);
		this.listBoxBeneficiary.getItems().clear();

		if (this.searchObj != null) {
			this.searchObj.clearFilters();
			paging(getSearchObj());
		}
		logger.debug(Literal.LEAVING);

	}

	private void doSetFieldProperties() {
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setDisplayStyle(2);
		this.custCIF.setTextBoxWidth(150);
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
		this.custCIF.setValue(StringUtils.trimToEmpty(primaryCIF));
		if (CollectionUtils.isNotEmpty(custCIFs)) {
			// Filters for the Customer CIF
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("CustCIF", custCIFs, Filter.OP_IN);
			this.custCIF.setFilters(filter);
		}

		this.bankName.setMaxlength(25);
		this.branchDesc.setMaxlength(25);
		this.city.setMaxlength(20);
		this.accHolderName.setMaxlength(25);
		this.accNumber.setMaxlength(25);
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<Beneficiary> getSearchObj() {
		searchObj = new JdbcSearchObject<Beneficiary>(Beneficiary.class, getListRows());
		searchObj.addTabelName("Beneficiary_AView");
		if (filterList != null && filterList.size() > 0) {
			for (int k = 0; k < filterList.size(); k++) {
				searchObj.addFilter(filterList.get(k));
			}
		}
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Beneficiary> searchObj) {
		this.searchObj = searchObj;
	}

	public Object getDialogCtrl() {
		return dialogCtrl;
	}

	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}

}