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
 * FileName    		:  CustomerIncomeListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerincome;

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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customerincome.model.CustomerIncomeComparator;
import com.pennant.webui.customermasters.customerincome.model.CustomerIncomeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerIncomeListCtrl extends GFCBaseListCtrl<CustomerIncome> implements Serializable {

	private static final long serialVersionUID = -5018975982654527543L;
	private final static Logger logger = Logger.getLogger(CustomerIncomeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerIncomeList;   			// autoWired
	protected Borderlayout borderLayout_CustomerIncomeList; // autoWired
	protected Paging pagingCustomerIncomeList; 				// autoWired
	protected Listbox listBoxCustomerIncome; 				// autoWired

	// List headers
	protected Listheader listheader_CustIncomeType; 		// autoWired
	protected Listheader listheader_CustIncome; 			// autoWired
	protected Listheader listheader_CustIncomeCountry; 		// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_CustomerIncomeList_NewCustomerIncome; 				// autoWired
	protected Button button_CustomerIncomeList_CustomerIncomeSearchDialog; 		// autoWired
	protected Button button_CustomerIncomeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerIncome> searchObj;
	private transient PagedListService pagedListService;
	private transient CustomerIncomeService customerIncomeService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CustomerIncomeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_CustomerIncomeList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerIncome");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerIncome");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerIncomeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerIncomeList.setPageSize(getListRows());
		this.pagingCustomerIncomeList.setDetailed(true);

		this.listheader_CustIncomeType.setSortAscending(new FieldComparator("custIncomeType", true));
		this.listheader_CustIncomeType.setSortDescending(new FieldComparator("custIncomeType", false));
		this.listheader_CustIncome.setSortAscending(new FieldComparator("custIncome", true));
		this.listheader_CustIncome.setSortDescending(new FieldComparator("custIncome", false));
		this.listheader_CustIncomeCountry.setSortAscending(new FieldComparator("custIncomeCountry", true));
		this.listheader_CustIncomeCountry.setSortDescending(new FieldComparator("custIncomeCountry",false));

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
		this.searchObj = new JdbcSearchObject<CustomerIncome>(CustomerIncome.class, getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("CustomerIncomes_View");
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerIncomeList_NewCustomerIncome.setVisible(true);
			} else {
				button_CustomerIncomeList_NewCustomerIncome.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerIncomeList_NewCustomerIncome.setVisible(false);
			this.button_CustomerIncomeList_CustomerIncomeSearchDialog.setVisible(false);
			this.button_CustomerIncomeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxCustomerIncome.setItemRenderer(new CustomerIncomeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * Method for rendering list of Objects
	 */
	public void findSearchObject() {
		logger.debug("Entering");
		final SearchResult<CustomerIncome> searchResult = getPagedListService()
				.getSRBySearchObject(this.searchObj);
		listBoxCustomerIncome.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new CustomerIncomeComparator()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerIncomeList");

		this.button_CustomerIncomeList_NewCustomerIncome.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerIncomeList_NewCustomerIncome"));
		this.button_CustomerIncomeList_CustomerIncomeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerIncomeList_CustomerIncomeFindDialog"));
		this.button_CustomerIncomeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerIncomeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customerincome.model.
	 * CustomerIncomeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerIncomeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerIncome object
		final Listitem item = this.listBoxCustomerIncome.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerIncome aCustomerIncome = (CustomerIncome) item.getAttribute("data");
			final CustomerIncome customerIncome = getCustomerIncomeService()
						.getCustomerIncomeById(aCustomerIncome.getId(),
					aCustomerIncome.getCustIncomeType(),aCustomerIncome.getCustIncomeCountry());

			if (customerIncome == null) {

				String[] valueParm = new String[3];
				String[] errParm = new String[3];

				valueParm[0] = String.valueOf(aCustomerIncome.getCustID());
				valueParm[1] = aCustomerIncome.getCustIncomeType();
				valueParm[2] = aCustomerIncome.getCustIncomeCountry();

				errParm[0] = PennantJavaUtil.getLabel("label_CustID")+ ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustIncomeType")+ ":" + valueParm[1];
				errParm[2] = PennantJavaUtil.getLabel("label_CustIncomeCountry") + ":"+valueParm[2];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
									errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CustID='" + customerIncome.getCustID()
				+ "' AND version=" + customerIncome.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), 
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CustomerIncome", whereCond,customerIncome.getTaskId(),
							customerIncome.getNextTaskId());
					if (userAcces) {
						showDetailView(customerIncome);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerIncome);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerIncome dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerIncomeList_NewCustomerIncome(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerIncome object, We GET it from the backEnd.
		final CustomerIncome aCustomerIncome = getCustomerIncomeService().getNewCustomerIncome();
		showDetailView(aCustomerIncome);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CustomerIncome
	 *            (aCustomerIncome)
	 * @throws Exception
	 */
	private void showDetailView(CustomerIncome aCustomerIncome)
	throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aCustomerIncome.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerIncome.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerIncome", aCustomerIncome);
		map.put("customerIncomeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_CustomerIncomeList);
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
		this.pagingCustomerIncomeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerIncomeList, event);
		this.window_CustomerIncomeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the CustomerIncome dialog
	 */
	public void onClick$button_CustomerIncomeList_CustomerIncomeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerIncomeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected CustomerIncome. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * CustomerIncome object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerIncomeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeSearchDialog.zul",
							null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	/**
	 * When the customerIncome print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerIncomeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CustomerIncome", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerIncomeService(
			CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}
	public CustomerIncomeService getCustomerIncomeService() {
		return this.customerIncomeService;
	}

	public JdbcSearchObject<CustomerIncome> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerIncome> searchObj) {
		this.searchObj = searchObj;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}