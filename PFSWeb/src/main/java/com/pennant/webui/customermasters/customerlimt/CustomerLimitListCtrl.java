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
 * * FileName : CustomerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customerlimt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.webui.customermasters.customerlimt.model.CustomerLimitListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.
 */
public class CustomerLimitListCtrl extends GFCBaseListCtrl<CustomerLimit> {
	private static final long serialVersionUID = 9086034736503097868L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUl-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerLimitList; // autowired
	protected Borderlayout borderLayout_CustomerLimitList; // autowired
	protected Paging pagingCustomerLimitList; // autowired
	protected Listbox listBoxCustomerLimit; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_CustShrtName; // autowired
	protected Listheader listheader_Country; // autowired
	protected Listheader listheader_custGroupName; // autowired
	protected Listheader listheader_Currency; // autowired

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CustomerLimitList_NewCustomerLimit; // autowired
	protected Button button_CustomerLimitList_CustomerLimitSearchDialog; // autowired
	protected Button button_CustomerLimitList_PrintList; // autowired

	private CustomerLimitIntefaceService custLimitIntefaceService;

	/**
	 * default constructor.<br>
	 */
	public CustomerLimitListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

	}

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerLimitList(Event event) {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		this.borderLayout_CustomerLimitList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		setPageComponents(window_CustomerLimitList, borderLayout_CustomerLimitList, listBoxCustomerLimit,
				pagingCustomerLimitList);
		this.pagingCustomerLimitList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("custCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("custCIF", false));
		this.listheader_CustShrtName.setSortAscending(new FieldComparator("custShrtName", true));
		this.listheader_CustShrtName.setSortDescending(new FieldComparator("custShrtName", false));
		this.listheader_Country.setSortAscending(new FieldComparator("country", true));
		this.listheader_Country.setSortDescending(new FieldComparator("country", false));
		this.listheader_custGroupName.setSortAscending(new FieldComparator("custGroupName", true));
		this.listheader_custGroupName.setSortDescending(new FieldComparator("custGroupName", false));
		this.listheader_Currency.setSortAscending(new FieldComparator("currency", true));
		this.listheader_Currency.setSortDescending(new FieldComparator("currency", false));

		Map<String, Object> custLimitMap = getCustLimitIntefaceService().fetchCustLimitEnqList(1, getListRows());
		if (custLimitMap != null) {
			List<CustomerLimit> list = new ArrayList<CustomerLimit>();
			if (custLimitMap.containsKey("CustLimitList")) {
				list = (List<CustomerLimit>) custLimitMap.get("CustLimitList");
			}

			getPagedListWrapper().initList(list, this.listBoxCustomerLimit, this.pagingCustomerLimitList);
			this.pagingCustomerLimitList.setTotalSize((Integer) custLimitMap.get("TotalSize"));
			this.pagingCustomerLimitList.setActivePage(((Integer) custLimitMap.get("PageNumber")) - 1);
		}
		this.listBoxCustomerLimit.setItemRenderer(new CustomerLimitListModelItemRenderer());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("CustomerList");

		this.button_CustomerLimitList_NewCustomerLimit
				.setVisible(getUserWorkspace().isAllowed("button_CustomerLimitList_NewCustomerLimit"));
		this.button_CustomerLimitList_CustomerLimitSearchDialog
				.setVisible(getUserWorkspace().isAllowed("button_CustomerLimitList_CustomerLimitSearchDialog"));
		this.button_CustomerLimitList_PrintList
				.setVisible(getUserWorkspace().isAllowed("button_CustomerLimitList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customer.model.CustomerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 */
	public void onCustomerItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCustomerLimit.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			CustomerLimit customerLimit = (CustomerLimit) item.getAttribute("data");
			showDetailView(customerLimit);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param CustomerDetails (aCustomerDetails)
	 */
	private void showDetailView(CustomerLimit customerLimit) {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */

		Map<String, Object> map = getDefaultArguments();
		map.put("customerLimit", customerLimit);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listBox ListModel. This is fine for synchronizing the data in the CustomerListbox from the dialog when we do
		 * a delete, edit or insert a Customer.
		 */
		map.put("customerListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerLimit/CustomerLimitEnquiryList.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CustomerLimitList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.pagingCustomerLimitList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerLimitList, event);
		this.window_CustomerLimitList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the Customer dialog
	 * 
	 * @param event
	 */
	public void onClick$button_CustomerList_CustomerSearchDialog(Event event) {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerDialog zul-file with parameters. So we can call them with a object of the selected
		 * CustomerDetails. For handed over these parameter only a Map is accepted. So we put the CustomerDetails object
		 * in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("customerCtrl", this);
		// map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSearchDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		// PTReportUtils.getReport("Customer", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
		this.custLimitIntefaceService = custLimitIntefaceService;
	}

	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
		return custLimitIntefaceService;
	}

}