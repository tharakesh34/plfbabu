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
 * * FileName : CustomerSearchCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.model.CustomerSelectItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customers/CustomerSelect.zul file.
 */
public class CustomerSelectCtrl extends GFCBaseCtrl<Customer> {
	private static final long serialVersionUID = -2873070081817788952L;
	private static final Logger logger = LogManager.getLogger(CustomerSelectCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerSelect;

	protected Textbox custCIF;
	protected Listbox sortOperator_custCIF;
	protected Textbox custCoreBank;
	protected Listbox sortOperator_custCoreBank;
	protected Datebox custDob;
	protected Listbox sortOperator_custDob;
	protected Textbox custName;
	protected Listbox sortOperator_custName;
	protected Textbox custMobile;
	protected Listbox sortOperator_custMobile;
	protected Textbox custEid;
	protected Listbox sortOperator_custEID;
	protected Textbox custType;
	protected Listbox sortOperator_custType;
	protected Textbox custNationality;
	protected Listbox sortOperator_custNationality;
	protected ExtendedCombobox custTarget;
	protected Listbox sortOperator_custTarget;
	protected Combobox custCategory;
	protected Listbox sortOperator_custCategory;
	protected Paging pagingCustomerList;
	protected Listbox listBoxCustomer;
	protected Grid searchGrid;

	// List headers
	protected Listheader listheader_CustID;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustCoreBank;
	protected Listheader listheader_CustCtgCode;
	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_RecordType;

	protected Label label_CustomerSearch_RecordStatus;
	protected Label label_CustomerSearch_RecordType;

	protected Borderlayout borderLayout_CustomerSelect;

	// not auto wired vars
	private transient Object dialogCtrl = null;

	private JdbcSearchObject<Customer> searchObj;
	private List<Filter> filterList = new ArrayList<Filter>();
	private String whereClause = null;
	protected Button btnClear;
	private final List<ValueLabel> custCtgCodeList = PennantAppUtil.getcustCtgCodeList();

	/**
	 * Default constructor
	 */
	public CustomerSelectCtrl() {
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
	public void onCreate$window_CustomerSelect(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerSelect);

		// DropDown ListBox
		List<SearchOperators> list = new SearchOperators().getSimpleStringOperators();
		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCoreBank.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custCoreBank.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custDob
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleNumericOperators()));
		this.sortOperator_custDob.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custName.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custMobile.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custMobile.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custEID.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custEID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custType.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custNationality.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custNationality.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custTarget.setModel(new ListModelList<SearchOperators>(list));
		doSetTargetProperties();

		this.sortOperator_custTarget.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCategory.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custCategory.setItemRenderer(new SearchOperatorListModelItemRenderer());
		fillComboBox(this.custCategory, "", custCtgCodeList, "");

		if (arguments.containsKey("DialogCtrl")) {
			setDialogCtrl(arguments.get("DialogCtrl"));
		}

		if (arguments.containsKey("filtersList")) {
			filterList = (List<Filter>) arguments.get("filtersList");
		}

		if (arguments.containsKey("whereClause")) {
			whereClause = (String) arguments.get("whereClause");
		}

		doSetFieldProperties();
		// Stored search object and paging

		if (arguments.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<Customer>) arguments.get("searchObject");
		}

		this.borderLayout_CustomerSelect.setHeight(borderLayoutHeight + "px");
		this.listBoxCustomer.setHeight(getListBoxHeight(this.searchGrid.getRows().getVisibleItemCount() + 1));
		this.pagingCustomerList.setPageSize(getListRows());
		this.pagingCustomerList.setDetailed(true);

		if (searchObj == null) {
			showCustomerSeekDialog();
			logger.debug(Literal.LEAVING);
			return;
		}

		// Render Search Object
		paging(searchObj);

		// get the filters from the searchObject
		final List<Filter> ft = searchObj.getFilters();
		for (final Filter filter : ft) {
			// restore founded properties
			String value = filter.getValue().toString();
			if ("CustCIF".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custCIF, filter);
				this.custCIF.setValue(restoreString(value, this.sortOperator_custCIF));
			} else if ("CustCoreBank".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custCoreBank, filter);
				this.custCoreBank.setValue(restoreString(value, this.sortOperator_custCoreBank));
			} else if ("CustDOB".equals(filter.getProperty())) {
				SearchOperators.restoreNumericOperator(this.sortOperator_custDob, filter);
				this.custDob.setValue((Date) filter.getValue());
			} else if ("CustShrtName".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custName, filter);
				this.custName.setValue(restoreString(value, this.sortOperator_custName));
			} else if ("PhoneNumber".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custMobile, filter);
				this.custMobile.setValue(restoreString(value, this.sortOperator_custMobile));
			} else if ("CustCRCPR".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custEID, filter);
				this.custEid.setValue(restoreString(value, this.sortOperator_custEID));
			} else if ("CustTypeCode".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custType, filter);
				this.custType.setValue(restoreString(value, this.sortOperator_custType));
			} else if ("CustNationality".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custNationality, filter);
				this.custNationality.setValue(restoreString(value, this.sortOperator_custNationality));
			} else if ("Target".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custTarget, filter);
				this.custTarget.setValue(restoreString(value, this.sortOperator_custTarget));
			} else if ("CustCtgCode".equals(filter.getProperty())) {
				SearchOperators.resetOperator(this.sortOperator_custCategory, filter);
				this.custCategory.setValue(restoreString(value, this.sortOperator_custCategory));
			}
		}

		showCustomerSeekDialog();
		logger.debug(Literal.LEAVING);
	}

	private void doSetTargetProperties() {
		logger.debug("Entering");
		this.custTarget.setMaxlength(8);
		this.custTarget.setInputAllowed(false);
		this.custTarget.setTextBoxWidth(117);
		this.custTarget.getSpace().setVisible(false);
		this.custTarget.setModuleName("TargetDetail");
		this.custTarget.setValueColumn("TargetCode");
		this.custTarget.setDescColumn("TargetDesc");
		this.custTarget.setValidateColumns(new String[] { "TargetCode" });
		logger.debug("Leaving");
	}

	/**
	 * Method for replacing LIKE '%' operator in String of SearchObject
	 * 
	 * @param filterValue
	 * @param listbox
	 * @return
	 */
	private String restoreString(String filterValue, Listbox listbox) {
		if (listbox.getSelectedIndex() == 2) {
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
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
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
	private void showCustomerSeekDialog() {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerSelect.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");

		JdbcSearchObject<Customer> searchObject = getSearchObj();

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
				}
			}
		}

		if (StringUtils.isNotBlank(this.custCoreBank.getValue())) {

			// get the search operator
			final Listitem itemCustCoreBank = this.sortOperator_custCoreBank.getSelectedItem();
			if (itemCustCoreBank != null) {
				final int searchOpId = ((SearchOperators) itemCustCoreBank.getAttribute("data")).getSearchOperatorId();

				searchObject.addFilter(new Filter("CustCoreBank", this.custCoreBank.getValue(), searchOpId));
			}
		}

		if (this.custDob.getValue() != null) {

			// get the search operator
			final Listitem itemCustCoreBank = this.sortOperator_custDob.getSelectedItem();
			if (itemCustCoreBank != null) {
				final int searchOpId = ((SearchOperators) itemCustCoreBank.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustDOB", this.custDob.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.custName.getValue())) {

			// get the search operator
			final Listitem itemCustDftBranch = this.sortOperator_custName.getSelectedItem();
			if (itemCustDftBranch != null) {
				final int searchOpId = ((SearchOperators) itemCustDftBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject
							.addFilter(new Filter("CustShrtName", "%" + this.custName.getValue() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustShrtName", this.custName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotBlank(this.custMobile.getValue())) {
			// get the search operator
			final Listitem itemCustCtgCode = this.sortOperator_custMobile.getSelectedItem();
			if (itemCustCtgCode != null) {
				final int searchOpId = ((SearchOperators) itemCustCtgCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("PhoneNumber",
							"%" + this.custMobile.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("PhoneNumber", this.custMobile.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.custEid.getValue())) {

			// get the search operator
			final Listitem itemCustTypeCode = this.sortOperator_custEID.getSelectedItem();
			if (itemCustTypeCode != null) {
				final int searchOpId = ((SearchOperators) itemCustTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("CustCRCPR", "%" + this.custEid.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustCRCPR", this.custEid.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.custType.getValue())) {

			// get the search operator
			final Listitem itemCustType = this.sortOperator_custType.getSelectedItem();
			if (itemCustType != null) {
				final int searchOpId = ((SearchOperators) itemCustType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("CustTypeCode", "%" + this.custType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustTypeCode", this.custType.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.custNationality.getValue())) {

			// get the search operator
			final Listitem itemCustMName = this.sortOperator_custNationality.getSelectedItem();
			if (itemCustMName != null) {
				final int searchOpId = ((SearchOperators) itemCustMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustNationality",
							"%" + this.custNationality.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustNationality", this.custNationality.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotBlank(this.custTarget.getValue())) {
			// get the search operator
			final Listitem itemCustLName = this.sortOperator_custTarget.getSelectedItem();
			if (itemCustLName != null) {
				final int searchOpId = ((SearchOperators) itemCustLName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject
							.addFilter(new Filter("CustAddlVar82", "%" + this.custTarget.getValue() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustAddlVar82", this.custTarget.getValue(), searchOpId));
				}
			}
		}
		if (this.custCategory.getSelectedItem() != null
				&& !"#".equals(this.custCategory.getSelectedItem().getValue().toString())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custCategory.getSelectedItem(),
					this.custCategory.getSelectedItem().getLabel(), "lovDescCustCtgCodeName");
		}

		// Default Sort on the table
		searchObject.addSort("CustID", false);

		setSearchObj(searchObject);
		paging(searchObject);
		logger.debug("Leaving");
	}

	/**
	 * Method for Render the getting list and set the pagination
	 * 
	 * @param searchObj
	 */
	private void paging(JdbcSearchObject<Customer> searchObj) {

		if (searchObj.getFilters().isEmpty()) {
			MessageUtil.showError("Please enter at least one search criteria.");
			return;
		}

		this.pagingCustomerList.setDetailed(true);
		this.listBoxCustomer.setItemRenderer(new CustomerSelectItemRenderer(PennantAppUtil.getCustTargetValues()));
		getPagedListWrapper().init(searchObj, this.listBoxCustomer, this.pagingCustomerList);
	}

	// when item double clicked
	@SuppressWarnings("rawtypes")
	public void onCustomerItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.listBoxCustomer.getSelectedItem() != null) {
			final Listitem li = this.listBoxCustomer.getSelectedItem();
			final Object object = li.getAttribute("data");

			if (getDialogCtrl() != null) {
				dialogCtrl = (Object) getDialogCtrl();
			}
			try {

				Class[] paramType = { Class.forName("java.lang.Object"),
						Class.forName("com.pennant.backend.util.JdbcSearchObject") };
				Object[] stringParameter = { object, this.searchObj };
				if (dialogCtrl.getClass().getMethod("doSetCustomer", paramType) != null) {
					dialogCtrl.getClass().getMethod("doSetCustomer", paramType).invoke(dialogCtrl, stringParameter);
				}

			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		doClose(false);
		logger.debug("Leaving");
	}

	public void onClick$btnClear(Event event) {
		logger.debug("Entering");

		this.custCIF.setValue("");
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.custCoreBank.setValue("");
		this.sortOperator_custCoreBank.setSelectedIndex(0);
		this.custDob.setText("");
		this.sortOperator_custDob.setSelectedIndex(0);
		this.custName.setValue("");
		this.sortOperator_custName.setSelectedIndex(0);
		this.custMobile.setValue("");
		this.sortOperator_custMobile.setSelectedIndex(0);
		this.custEid.setValue("");
		this.sortOperator_custEID.setSelectedIndex(0);
		this.custType.setValue("");
		this.sortOperator_custType.setSelectedIndex(0);
		this.custNationality.setValue("");
		this.sortOperator_custNationality.setSelectedIndex(0);
		this.custTarget.setValue("", "");
		this.sortOperator_custTarget.setSelectedIndex(0);
		this.custCategory.setSelectedIndex(0);
		this.sortOperator_custCategory.setSelectedIndex(0);
		this.listBoxCustomer.getItems().clear();

		if (this.searchObj != null) {
			this.searchObj.clearFilters();
			paging(getSearchObj());
		}
		logger.debug("Leaving");

	}

	private void doSetFieldProperties() {
		this.custDob.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custName.setMaxlength(25);
		this.custMobile.setMaxlength(10);
		this.custEid.setMaxlength(20);
		this.custType.setMaxlength(8);
		this.custNationality.setMaxlength(2);
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<Customer> getSearchObj() {
		searchObj = new JdbcSearchObject<Customer>(Customer.class, getListRows());
		searchObj.addTabelName("Customers_AEView");
		if (filterList != null && filterList.size() > 0) {
			for (int k = 0; k < filterList.size(); k++) {
				searchObj.addFilter(filterList.get(k));
			}
		}

		if (whereClause != null) {
			searchObj.addWhereClause(whereClause);
		}

		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}

	public Object getDialogCtrl() {
		return dialogCtrl;
	}

	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}

}