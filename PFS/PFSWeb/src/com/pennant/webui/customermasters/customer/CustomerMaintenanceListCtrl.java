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
 * FileName    		:  CustomerMaintenanceListCtrl.java                                                   * 	  
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
import java.util.HashMap;

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
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.customermasters.customer.model.CustomerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerMaintenaceList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerMaintenanceListCtrl extends GFCBaseListCtrl<Customer> implements Serializable {

	private static final long serialVersionUID = 9086034736503097868L;
	private final static Logger logger = Logger.getLogger(CustomerMaintenanceListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerBasicDetailList; 		// autoWired
	protected Borderlayout 	borderLayout_CustomerMaintenanceList; 	// autoWired
	protected Paging 		pagingCustomerMaintenanceList; 			// autoWired
	protected Listbox 		listBoxCustomer; 						// autoWired

	// List headers
	protected Listheader listheader_CustCIF; 		// autoWired
	protected Listheader listheader_CustCoreBank; 	// autoWired
	protected Listheader listheader_CustShrtName;	// autoWired
	protected Listheader listheader_CustDftBranch;	// autoWired
	protected Listheader listheader_CustCtgCode; 	// autoWired
	protected Listheader listheader_CustTypeCode;	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autoWired
	//protected Button button_CustomerMaintenaceList_NewCustomer; 			// autoWired
	protected Button button_CustomerMaintenanceList_CustomerSearchDialog; 	// autoWired
	protected Button button_CustomerMaintenanceList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerService customerService;
	private transient CustomerDetailsService customerDetailsService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	//Module Type Details
	protected Textbox moduleType;

	/**
	 * default constructor.<br>
	 */
	public CustomerMaintenanceListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBasicDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerMaintence");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerMaintence");

			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerMaintenanceList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerMaintenanceList.setPageSize(getListRows());
		this.pagingCustomerMaintenanceList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("custCIF", true));
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

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<Customer>(Customer.class,getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addTabelName("Customers_View");
		this.searchObj.addFilterOr(new Filter("recordType", "NEW" , Filter.OP_NOT_EQUAL));

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerMaintenanceList_CustomerSearchDialog.setVisible(false);
			this.button_CustomerMaintenanceList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCustomer,this.pagingCustomerMaintenanceList);
			// set the itemRenderer
			this.listBoxCustomer.setItemRenderer(new CustomerListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerMaintenanceList");

		this.button_CustomerMaintenanceList_CustomerSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerMaintenanceList_CustomerFindDialog"));
		this.button_CustomerMaintenanceList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerMaintenanceList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customer.model.CustomerMaintenaceListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCustomer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Customer aCustomer = (Customer) item.getAttribute("data");
			Customer customer = null;
			CustomerDetails customerDetails = null;
			if("ENQ".equals(this.moduleType.getValue())){
				customerDetails = getCustomerDetailsService().getCustomerById(aCustomer.getId());
				customer = customerDetails.getCustomer();
			}else{
				customer = getCustomerService().getCustomerById(aCustomer.getId());
			}

			if(customer == null){
				String[] valueParm = new String[2];
				String[] errParm= new String[2];

				valueParm[0] = aCustomer.getCustCIF();
				errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
								errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());

			}else{
				String whereCond =  " AND CustID='"+ aCustomer.getCustID()+
										"' AND version=" + aCustomer.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Customer", 
							whereCond, aCustomer.getTaskId(), aCustomer.getNextTaskId());
					if (userAcces){
						showDetailView(customer,customerDetails);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customer,customerDetails);
				}
			}
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerMaintenaceList_NewCustomer(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final Customer aCustomer = getCustomerService().getNewCustomer(); 
		showDetailView(aCustomer, null);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CustomerDetails (aCustomerDetails)
	 * @throws Exception
	 */
	private void showDetailView(Customer aCustomer, CustomerDetails customerDetails) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aCustomer.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customer", aCustomer);
		map.put("customerDetails", customerDetails);
		map.put("customerMaintenanceListCtrl", this);
		map.put("moduleType", this.moduleType.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			if("ENQ".equals(this.moduleType.getValue())){
				Executions.createComponents(
						"/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul",
						null,map);
			}else{
				Executions.createComponents(
						"/WEB-INF/pages/CustomerMasters/CustomerBasicDetail/CustomerMaintenanceDialog.zul",
						null,map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		PTMessageUtils.showHelpWindow(event, window_CustomerBasicDetailList);
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
		this.pagingCustomerMaintenanceList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerBasicDetailList, event);
		this.window_CustomerBasicDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the Customer dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerMaintenanceList_CustomerSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerDetails. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerDetails object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerMaintenanceCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerBasicDetail/CustomerMaintenanceSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerMaintenaceList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("Customer", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<Customer> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
}