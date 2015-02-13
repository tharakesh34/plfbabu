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
import com.pennant.backend.util.PennantConstants;
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
	protected Textbox custDob; 					// autowired
	protected Listbox sortOperator_custDob; 		// autowired
	protected Textbox custName; 						// autowired
	protected Listbox sortOperator_custName; 		// autowired
	protected Textbox custMobile; 					// autowired
	protected Listbox sortOperator_custMobile; 		// autowired
	protected Textbox custEid; 				// autowired
	protected Listbox sortOperator_custEID; 	// autowired
	protected Textbox custPassport; 						// autowired
	protected Listbox sortOperator_custPassport; 			// autowired
	protected Textbox custType; 						// autowired
	protected Listbox sortOperator_custType; 			// autowired
	protected Textbox custNationality; 						// autowired
	protected Listbox sortOperator_custNationality;	 		// autowired
	protected Textbox custSector; 					// autowired
	protected Listbox sortOperator_custSector; 		// autowired
	protected Textbox custSubSector; 					// autowired
	protected Listbox sortOperator_custSubSector; 		// autowired

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
    private String finDivision=null;
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
		List<SearchOperators> list = new SearchOperators().getStringOperators();
		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custDob.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custDob.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custName.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custName.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_custMobile.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custMobile.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custEID.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custEID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custPassport.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custPassport.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custType.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custNationality.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custNationality.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custSector.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custSector.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custSubSector.setModel(new ListModelList<SearchOperators>(list));
		this.sortOperator_custSubSector.setItemRenderer(new SearchOperatorListModelItemRenderer());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("DialogCtrl")) {
			setDialogCtrl(args.get("DialogCtrl"));
		}
	
		if (args.containsKey("finDivision")) {
			finDivision = (String) args.get("finDivision");
		}
		
		if (args.containsKey("filtersList")) {
			filterList = (List<Filter>) args.get("filtersList");
		}
		
		if(!StringUtils.trimToEmpty(finDivision).equals("")){
			if(finDivision.equals(PennantConstants.FIN_DIVISION_COMMERCIAL) || finDivision.equals(PennantConstants.FIN_DIVISION_RETAIL)) {
				filterList.add(new Filter("custDftBranch", PennantConstants.IBD_Branch, Filter.OP_NOT_EQUAL));
			} else if(finDivision.equals(PennantConstants.FIN_DIVISION_CORPORATE)){
				filterList.add(new Filter("custDftBranch", PennantConstants.IBD_Branch, Filter.OP_EQUAL));
			}
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
					SearchOperators.resetOperator(this.sortOperator_custDob, filter);
					this.custDob.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custDob));
				} else if (filter.getProperty().equals("custCtgCode")) {
					SearchOperators.resetOperator(this.sortOperator_custName, filter);
					this.custName.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custName));
				} else if (filter.getProperty().equals("custTypeCode")) {
					SearchOperators.resetOperator(this.sortOperator_custMobile, filter);
					this.custMobile.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custMobile));
				} else if (filter.getProperty().equals("custSalutationCode")) {
					SearchOperators.resetOperator(this.sortOperator_custEID, filter);
					this.custEid.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custEID));
				} else if (filter.getProperty().equals("custFName")) {
					SearchOperators.resetOperator(this.sortOperator_custPassport, filter);
					this.custPassport.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custPassport));
				} else if (filter.getProperty().equals("custMName")) {
					SearchOperators.resetOperator(this.sortOperator_custType, filter);
					this.custType.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custType));
				} else if (filter.getProperty().equals("custLName")) {
					SearchOperators.resetOperator(this.sortOperator_custNationality, filter);
					this.custNationality.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custNationality));
				} else if (filter.getProperty().equals("custShrtName")) {
					SearchOperators.resetOperator(this.sortOperator_custSector, filter);
					this.custSector.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custSector));
				} else if (filter.getProperty().equals("custDftBranch")) {
					SearchOperators.resetOperator(this.sortOperator_custSubSector, filter);
					this.custSubSector.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custSubSector));
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
			return StringUtils.replaceChars(filterValue, "%", "");
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
			final Listitem itemCustCIF = this.sortOperator_custCIF.getSelectedItem();
			if (itemCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("custCIF", "%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("custCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custDob.getValue())) {

			// get the search operator
			final Listitem itemCustCoreBank = this.sortOperator_custDob.getSelectedItem();
			if (itemCustCoreBank != null) {
				final int searchOpId = ((SearchOperators) itemCustCoreBank.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustDOB", "%" + this.custDob.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustDOB", this.custDob.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.custName.getValue())) {

			// get the search operator
			final Listitem itemCustDftBranch = this.sortOperator_custName.getSelectedItem();
			if (itemCustDftBranch != null) {
				final int searchOpId = ((SearchOperators) itemCustDftBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustFName", "%" + this.custName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustFName", this.custName.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custMobile.getValue())) {

			// get the search operator
			final Listitem itemCustCtgCode = this.sortOperator_custMobile.getSelectedItem();
			if (itemCustCtgCode != null) {
				final int searchOpId = ((SearchOperators) itemCustCtgCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("PhoneNumber", "%" + this.custMobile.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("PhoneNumber", this.custMobile.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custEid.getValue())) {

			// get the search operator
			final Listitem itemCustTypeCode = this.sortOperator_custEID.getSelectedItem();
			if (itemCustTypeCode != null) {
				final int searchOpId = ((SearchOperators) itemCustTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustCRCPR", "%" + this.custEid.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustCRCPR", this.custEid.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custPassport.getValue())) {

			// get the search operator
			final Listitem itemCustSalutationCode = this.sortOperator_custPassport.getSelectedItem();
			if (itemCustSalutationCode != null) {
				final int searchOpId = ((SearchOperators) itemCustSalutationCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustPassportNo", "%" + this.custPassport.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustPassportNo", this.custPassport.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custType.getValue())) {

			// get the search operator
			final Listitem itemCustFName = this.sortOperator_custType.getSelectedItem();
			if (itemCustFName != null) {
				final int searchOpId = ((SearchOperators) itemCustFName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustTypeCode", "%" + this.custType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustTypeCode", this.custType.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custNationality.getValue())) {

			// get the search operator
			final Listitem itemCustMName = this.sortOperator_custNationality.getSelectedItem();
			if (itemCustMName != null) {
				final int searchOpId = ((SearchOperators) itemCustMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustNationality", "%" + this.custNationality.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustNationality", this.custNationality.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custSector.getValue())) {

			// get the search operator
			final Listitem itemCustLName = this.sortOperator_custSector.getSelectedItem();
			if (itemCustLName != null) {
				final int searchOpId = ((SearchOperators) itemCustLName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustSector", "%" + this.custSector.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustSector", this.custSector.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.custSubSector.getValue())) {

			// get the search operator
			final Listitem itemCustShrtName = this.sortOperator_custSubSector.getSelectedItem();
			if (itemCustShrtName != null) {
				final int searchOpId = ((SearchOperators) itemCustShrtName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("CustSubSector", "%" + this.custSubSector.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					searchObject.addFilter(new Filter("CustSubSector", this.custSubSector.getValue(), searchOpId));
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
			this.custDob.setValue("");
			this.sortOperator_custDob.setSelectedIndex(0);
			this.custName.setValue("");
			this.sortOperator_custName.setSelectedIndex(0);
			this.custMobile.setValue("");
			this.sortOperator_custMobile.setSelectedIndex(0);
			this.custEid.setValue("");
			this.sortOperator_custEID.setSelectedIndex(0);
			this.custPassport.setValue("");
			this.sortOperator_custPassport.setSelectedIndex(0);
			this.custType.setValue("");
			this.sortOperator_custType.setSelectedIndex(0);
			this.custNationality.setValue("");
			this.sortOperator_custNationality.setSelectedIndex(0);
			this.custSector.setValue("");
			this.sortOperator_custSector.setSelectedIndex(0);
			this.custSubSector.setValue("");
			this.sortOperator_custSubSector.setSelectedIndex(0);
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