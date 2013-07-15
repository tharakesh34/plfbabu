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
 * FileName    		:  CustomerSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.customermasters.customer.model.CustomerSelectItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customers/CustomerSelect.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerSelectCtrl extends GFCBaseListCtrl<Customer> implements Serializable {

	private static final long serialVersionUID = -2873070081817788952L;
	private final static Logger logger = Logger.getLogger(CustomerSelectCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the ZUL-file are getting autowired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerSelect; 			// autowired

	protected Textbox custCIF; 							// autowired
	protected Listbox sortOperator_custCIF; 			// autowired
	protected Textbox custCoreBank; 					// autowired
	protected Listbox sortOperator_custCoreBank; 		// autowired
	protected Textbox custCtgCode; 						// autowired
	protected Listbox sortOperator_custCtgCode; 		// autowired
	protected Textbox custTypeCode; 					// autowired
	protected Listbox sortOperator_custTypeCode; 		// autowired
	protected Textbox custSalutationCode; 				// autowired
	protected Listbox sortOperator_custSalutationCode; 	// autowired
	protected Textbox custFName; 						// autowired
	protected Listbox sortOperator_custFName; 			// autowired
	protected Textbox custMName; 						// autowired
	protected Listbox sortOperator_custMName; 			// autowired
	protected Textbox custLName; 						// autowired
	protected Listbox sortOperator_custLName;	 		// autowired
	protected Textbox custShrtName; 					// autowired
	protected Listbox sortOperator_custShrtName; 		// autowired
	protected Textbox custDftBranch; 					// autowired
	protected Listbox sortOperator_custDftBranch; 		// autowired

	protected Paging pagingCustomerList; 				// autowired
	protected Listbox listBoxCustomer; 					// autowired
	protected Button btnClose; 							// autowired

	// List headers
	protected Listheader listheader_CustID; 			// autowired
	protected Listheader listheader_CustCIF; 			// autowired
	protected Listheader listheader_CustCoreBank; 		// autowired
	protected Listheader listheader_CustCtgCode; 		// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	protected Label label_CustomerSearch_RecordStatus; 	// autowired
	protected Label label_CustomerSearch_RecordType; 	// autowired

	protected Borderlayout borderLayout_CustomerSelect;
	
	// not auto wired vars
	private transient CustomerService customerService;
	private transient Object dialogCtrl = null;

	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Customer");
	private JdbcSearchObject<Customer> searchObj;
	private List<Filter> filterList = new ArrayList<Filter>();
	protected Button btnClear;

	/**
	 * Default constructor
	 */
	public CustomerSelectCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerSelect(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCoreBank.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCoreBank.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCtgCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCtgCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custSalutationCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custSalutationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custFName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custFName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custMName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custMName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custLName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custLName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custShrtName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custShrtName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custDftBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custDftBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("DialogCtrl")) {
			setDialogCtrl(args.get("DialogCtrl"));
		}
		if (args.containsKey("filtersList")) {
			filterList = (List<Filter>) args.get("filtersList");
		}
	
		// +++++++++++++++++++++++ Stored search object and paging ++++++++++++++++++++++ //
			
		if (args.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<Customer>) args.get("searchObject");
		}
		
		this.borderLayout_CustomerSelect.setHeight(calculateBorderLayoutHeight()-15+"px");
		this.listBoxCustomer.setHeight(getListBoxHeight(6));
		this.pagingCustomerList.setPageSize(getListRows());
		this.pagingCustomerList.setDetailed(true);
		
		if (searchObj != null) {
			
			// Render Search Object
			paging(searchObj);
		
			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("custCIF")) {
					SearchOperators.resetOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCIF));
				} else if (filter.getProperty().equals("custCoreBank")) {
					SearchOperators.resetOperator(this.sortOperator_custCoreBank, filter);
					this.custCoreBank.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCoreBank));
				} else if (filter.getProperty().equals("custCtgCode")) {
					SearchOperators.resetOperator(this.sortOperator_custCtgCode, filter);
					this.custCtgCode.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCtgCode));
				} else if (filter.getProperty().equals("custTypeCode")) {
					SearchOperators.resetOperator(this.sortOperator_custTypeCode, filter);
					this.custTypeCode.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custTypeCode));
				} else if (filter.getProperty().equals("custSalutationCode")) {
					SearchOperators.resetOperator(this.sortOperator_custSalutationCode, filter);
					this.custSalutationCode.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custSalutationCode));
				} else if (filter.getProperty().equals("custFName")) {
					SearchOperators.resetOperator(this.sortOperator_custFName, filter);
					this.custFName.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custFName));
				} else if (filter.getProperty().equals("custMName")) {
					SearchOperators.resetOperator(this.sortOperator_custMName, filter);
					this.custMName.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custMName));
				} else if (filter.getProperty().equals("custLName")) {
					SearchOperators.resetOperator(this.sortOperator_custLName, filter);
					this.custLName.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custLName));
				} else if (filter.getProperty().equals("custShrtName")) {
					SearchOperators.resetOperator(this.sortOperator_custShrtName, filter);
					this.custShrtName.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custShrtName));
				} else if (filter.getProperty().equals("custDftBranch")) {
					SearchOperators.resetOperator(this.sortOperator_custDftBranch, filter);
					this.custDftBranch.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custDftBranch));
				}
			}
		}

		showCustomerSeekDialog();
		logger.debug("Leaving" + event.toString());
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
			final String modifiedFilterValue = StringUtils.replaceChars(filterValue, "%", "");
			return modifiedFilterValue;
		}
		return filterValue;
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
	public void onClick$btnCloseWindow(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * OnClick Event for Close button for Closing Window
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
	 * 
	 * @throws InterruptedException
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		this.window_CustomerSelect.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerSelect.doModal();
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
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	public void doSearch() {
		logger.debug("Entering");

		JdbcSearchObject<Customer> searchObject = getSearchObj();

		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem item_CustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (item_CustCIF != null) {
				final int searchOpId = ((SearchOperators) item_CustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custCIF", "%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custCoreBank.getValue())) {

			// get the search operator
			final Listitem item_CustCoreBank = this.sortOperator_custCoreBank.getSelectedItem();

			if (item_CustCoreBank != null) {
				final int searchOpId = ((SearchOperators) item_CustCoreBank.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custCoreBank", "%" + this.custCoreBank.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custCoreBank", this.custCoreBank.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custCtgCode.getValue())) {

			// get the search operator
			final Listitem item_CustCtgCode = this.sortOperator_custCtgCode.getSelectedItem();

			if (item_CustCtgCode != null) {
				final int searchOpId = ((SearchOperators) item_CustCtgCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custCtgCode", "%" + this.custCtgCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custCtgCode", this.custCtgCode.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custTypeCode.getValue())) {

			// get the search operator
			final Listitem item_CustTypeCode = this.sortOperator_custTypeCode.getSelectedItem();

			if (item_CustTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_CustTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custTypeCode", "%" + this.custTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custTypeCode", this.custTypeCode.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custSalutationCode.getValue())) {

			// get the search operator
			final Listitem item_CustSalutationCode = this.sortOperator_custSalutationCode.getSelectedItem();

			if (item_CustSalutationCode != null) {
				final int searchOpId = ((SearchOperators) item_CustSalutationCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custSalutationCode", "%" + this.custSalutationCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custSalutationCode", this.custSalutationCode.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custFName.getValue())) {

			// get the search operator
			final Listitem item_CustFName = this.sortOperator_custFName.getSelectedItem();

			if (item_CustFName != null) {
				final int searchOpId = ((SearchOperators) item_CustFName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custFName", "%" + this.custFName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custFName", this.custFName.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custMName.getValue())) {

			// get the search operator
			final Listitem item_CustMName = this.sortOperator_custMName.getSelectedItem();

			if (item_CustMName != null) {
				final int searchOpId = ((SearchOperators) item_CustMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custMName", "%" + this.custMName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custMName", this.custMName.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custLName.getValue())) {

			// get the search operator
			final Listitem item_CustLName = this.sortOperator_custLName.getSelectedItem();

			if (item_CustLName != null) {
				final int searchOpId = ((SearchOperators) item_CustLName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custLName", "%" + this.custLName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custLName", this.custLName.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custShrtName.getValue())) {

			// get the search operator
			final Listitem item_CustShrtName = this.sortOperator_custShrtName.getSelectedItem();

			if (item_CustShrtName != null) {
				final int searchOpId = ((SearchOperators) item_CustShrtName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custShrtName", "%" + this.custShrtName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custShrtName", this.custShrtName.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custDftBranch.getValue())) {

			// get the search operator
			final Listitem item_CustDftBranch = this.sortOperator_custDftBranch.getSelectedItem();

			if (item_CustDftBranch != null) {
				final int searchOpId = ((SearchOperators) item_CustDftBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custDftBranch", "%" + this.custDftBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custDftBranch", this.custDftBranch.getValue(), searchOpId));
				}
			}
		}

		// Default Sort on the table
		searchObject.addSort("CustID", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = searchObject.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());
				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

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
		logger.debug("Entering");
		this.pagingCustomerList.setDetailed(true);
		this.listBoxCustomer.setItemRenderer(new CustomerSelectItemRenderer());
		getPagedBindingListWrapper().init(searchObj, this.listBoxCustomer, this.pagingCustomerList);
		logger.debug("Leaving");
	}

	// ++++++++++++ when item double clicked ++++++++++++++++++//
	@SuppressWarnings("rawtypes")
	public void onCustomerItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (this.listBoxCustomer.getSelectedItem() != null) {
			final Listitem li = this.listBoxCustomer.getSelectedItem();
			final Object object = li.getAttribute("data");

			if (getDialogCtrl() != null) {
				dialogCtrl = (Object) getDialogCtrl();
			}
			try {

				Class[] paramType = { Class.forName("java.lang.Object"), Class.forName("com.pennant.backend.util.JdbcSearchObject") };
				Object[] stringParameter = { object, this.searchObj };
				if (dialogCtrl.getClass().getMethod("doSetCustomer", paramType) != null) {
					dialogCtrl.getClass().getMethod("doSetCustomer", paramType).invoke(dialogCtrl, stringParameter);
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
		doClose();
		logger.debug("Leaving");
	}
	
	
	public void onClick$btnClear(Event event){
		logger.debug("Entering");
		if (this.searchObj!=null) {	
			this.custCIF.setValue("");
			this.sortOperator_custCIF.setSelectedIndex(0);
			this.custCoreBank.setValue("");
			this.sortOperator_custCoreBank.setSelectedIndex(0);
			this.custCtgCode.setValue("");
			this.sortOperator_custCtgCode.setSelectedIndex(0);
			this.custTypeCode.setValue("");
			this.sortOperator_custTypeCode.setSelectedIndex(0);
			this.custSalutationCode.setValue("");
			this.sortOperator_custSalutationCode.setSelectedIndex(0);
			this.custFName.setValue("");
			this.sortOperator_custFName.setSelectedIndex(0);
			this.custMName.setValue("");
			this.sortOperator_custMName.setSelectedIndex(0);
			this.custLName.setValue("");
			this.sortOperator_custLName.setSelectedIndex(0);
			this.custDftBranch.setValue("");
			this.sortOperator_custDftBranch.setSelectedIndex(0);
			this.listBoxCustomer.getItems().clear();
			this.searchObj.clearFilters();	
			paging(getSearchObj());
		}
		logger.debug("Leaving");
		
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return this.customerService;
	}

	public JdbcSearchObject<Customer> getSearchObj() {
	
		searchObj=new JdbcSearchObject<Customer>(Customer.class,getListRows());
		searchObj.addTabelName("Customers_AView");
		if (filterList != null & filterList.size() > 0) {
			for (int k = 0; k < filterList.size(); k++) {
				searchObj.addFilter(filterList.get(k));
			}
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