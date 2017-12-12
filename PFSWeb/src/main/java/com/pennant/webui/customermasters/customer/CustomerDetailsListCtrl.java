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
 * FileName    		:  CustomerMaintenaceListCtrl.java                                                   * 	  
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.webui.customermasters.customer.model.CustomerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/CustomerDetailsList.zul file.
 */
public class CustomerDetailsListCtrl extends GFCBaseListCtrl<Customer> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = Logger.getLogger(CustomerDetailsListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_CustomerBasicDetailsList; 	// autoWired
	protected Borderlayout 	borderLayout_CustomerDetailsList; 	// autoWired
	protected Paging		pagingCustomerDetailsList; 			// autoWired
	protected Listbox 		listBoxCustomerDetails; 			// autoWired
	protected Textbox		maintModule; 						// autoWired

	// List headers
	protected Listheader listheader_CustCIF; 		// autoWired
	protected Listheader listheader_CustCoreBank; 	// autoWired
	protected Listheader listheader_CustShrtName; 	// autoWired
	protected Listheader listheader_CustDftBranch; 	// autoWired
	protected Listheader listheader_CustCtgCode; 	// autoWired
	protected Listheader listheader_CustTypeCode; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp;	 										// autoWired
	protected Button button_CustomerDetailsList_NewCustomer; 			// autoWired
	protected Button button_CustomerDetailsList_CustomerSearchDialog; 	// autoWired
	protected Button button_CustomerDetailsList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerDetailsService customerDetailsService;

	/**
	 * default constructor.<br>
	 */
	public CustomerDetailsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "CustomerMaintence";
	}

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBasicDetailsList(Event event) throws Exception {
		logger.debug("Entering");

		/* set components visible dependent of the users rights */
		doCheckRights();

		this.borderLayout_CustomerDetailsList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerDetailsList.setPageSize(getListRows());
		this.pagingCustomerDetailsList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("custCIF",true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("custCIF", false));
		this.listheader_CustCoreBank.setSortAscending(new FieldComparator("custCoreBank", true));
		this.listheader_CustCoreBank.setSortDescending(new FieldComparator("custCoreBank", false));
		this.listheader_CustShrtName.setSortAscending(new FieldComparator("custShrtName", true));
		this.listheader_CustShrtName.setSortDescending(new FieldComparator("custShrtName", false));
		this.listheader_CustDftBranch.setSortAscending(new FieldComparator("custDftBranch", true));
		this.listheader_CustDftBranch.setSortDescending(new FieldComparator("custDftBranch", false));
		this.listheader_CustCtgCode.setSortAscending(new FieldComparator("custCtgCode", true));
		this.listheader_CustCtgCode.setSortDescending(new FieldComparator("custCtgCode", false));
		this.listheader_CustTypeCode.setSortAscending(new FieldComparator("custTypeCode", true));
		this.listheader_CustTypeCode.setSortDescending(new FieldComparator("custTypeCode", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<Customer>(Customer.class, getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addTabelName("Customers_View");
		button_CustomerDetailsList_NewCustomer.setVisible(false);
		this.searchObj.addFilter(new Filter("recordType", "NEW ",Filter.OP_NOT_EQUAL));

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxCustomerDetails, this.pagingCustomerDetailsList);
		// set the itemRenderer
		this.listBoxCustomerDetails.setItemRenderer(new CustomerListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("CustomerMaintenaceList");

		this.button_CustomerDetailsList_NewCustomer
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDetailsList_NewCustomer"));
		this.button_CustomerDetailsList_CustomerSearchDialog
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDetailsList_CustomerFindDialog"));
		this.button_CustomerDetailsList_PrintList
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDetailsList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customer.model.
	 * CustomerMaintenaceListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCustomerDetails.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Customer aCustomer = (Customer) item.getAttribute("data");
			final CustomerDetails customerDetails = getCustomerDetailsService().getCustomerById(aCustomer.getId());

			if (customerDetails == null) {
				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aCustomer.getCustCIF();
				valueParm[1] = aCustomer.getCustCtgCode();

				errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustCtgCode") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

			} else {
				String whereCond = " AND CustID='" + aCustomer.getCustID()
						+ "' AND version=" + aCustomer.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(aCustomer.getWorkflowId(), getUserWorkspace().getLoggedInUser()
							.getLoginUsrID(),"Customer", whereCond, aCustomer.getTaskId(),aCustomer.getNextTaskId());
					if (userAcces) {
						showDetailView(aCustomer);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(aCustomer);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CustomerDetails
	 *            (aCustomerDetails)
	 * @throws Exception
	 */
	private void showDetailView(Customer aCustomer) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		//Customer aCustomer = aCustomerDetails.getCustomer();
		if (aCustomer.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomer.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("customer", aCustomer);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CustomerMaintenaceListbox from
		 * the dialog when we do a delete, edit or insert a Customer.
		 */
		map.put("customerDetailsListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerBasicDetail/CustomerMaintenanceDialog.zul",null,map);
			/*if (condition) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerQDEDialog.zul",null, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul",null, map);
			}*/
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
		MessageUtil.showHelpWindow(event, window_CustomerBasicDetailsList);
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
		this.pagingCustomerDetailsList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerBasicDetailsList, event);
		this.window_CustomerBasicDetailsList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the Customer dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerDetailsList_CustomerSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerDetails. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * CustomerDetails object in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("customerCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/Customer/CustomerSearchDialog.zul", null, map);
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
	public void onClick$button_CustomerDetailsList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("Customer", getSearchObj(),this.pagingCustomerDetailsList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public JdbcSearchObject<Customer> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}
}