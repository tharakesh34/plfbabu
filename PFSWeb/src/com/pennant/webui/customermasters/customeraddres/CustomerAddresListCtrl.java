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
 * FileName    		:  CustomerAddresListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeraddres;

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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customeraddres.model.CustomerAddresComparator;
import com.pennant.webui.customermasters.customeraddres.model.CustomerAddresListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerAddresListCtrl extends GFCBaseListCtrl<CustomerAddres>
		implements Serializable {

	private static final long serialVersionUID = -3065680573751828336L;
	private final static Logger logger = Logger.getLogger(CustomerAddresListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerAddresList; 				// autoWired
	protected Panel 		panel_CustomerAddresList; 				// autoWired
	protected Borderlayout 	borderLayout_CustomerAddresList; 		// autoWired
	protected Paging 		pagingCustomerAddresList; 				// autoWired
	protected Listbox 		listBoxCustomerAddres; 					// autoWired
	
	// List headers
	protected Listheader listheader_CustAddrType; 			// autoWired
	protected Listheader listheader_CustAddrHNbr; 			// autoWired
	protected Listheader listheader_CustFlatNbr; 			// autoWired
	protected Listheader listheader_CustAddrStreet; 		// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autoWired
	protected Button button_CustomerAddresList_NewCustomerAddres; 			// autoWired
	protected Button button_CustomerAddresList_CustomerAddresSearchDialog; 	// autoWired
	protected Button button_CustomerAddresList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerAddres> searchObj;
	private transient PagedListService pagedListService;
	private transient CustomerAddresService customerAddresService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerAddresListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerAddres object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAddresList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil
				.getModuleMap("CustomerAddres");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerAddres");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that
		 * are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerAddresList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerAddresList.setPageSize(getListRows());
		this.pagingCustomerAddresList.setDetailed(true);

		this.listheader_CustAddrType.setSortAscending(new FieldComparator("custAddrType", true));
		this.listheader_CustAddrType.setSortDescending(new FieldComparator("custAddrType", false));
		this.listheader_CustAddrHNbr.setSortAscending(new FieldComparator("custAddrHNbr", true));
		this.listheader_CustAddrHNbr.setSortDescending(new FieldComparator("custAddrHNbr", false));
		this.listheader_CustFlatNbr.setSortAscending(new FieldComparator("custFlatNbr", true));
		this.listheader_CustFlatNbr.setSortDescending(new FieldComparator("custFlatNbr", false));
		this.listheader_CustAddrStreet.setSortAscending(new FieldComparator("custAddrStreet", true));
		this.listheader_CustAddrStreet.setSortDescending(new FieldComparator("custAddrStreet", false));

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
		this.searchObj = new JdbcSearchObject<CustomerAddres>(CustomerAddres.class, getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("CustomerAddresses_View");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerAddresList_NewCustomerAddres.setVisible(true);
			} else {
				button_CustomerAddresList_NewCustomerAddres.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerAddresList_NewCustomerAddres.setVisible(false);
			this.button_CustomerAddresList_CustomerAddresSearchDialog.setVisible(false);
			this.button_CustomerAddresList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxCustomerAddres.setItemRenderer(new CustomerAddresListModelItemRenderer());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject() {
		logger.debug("Entering");
		final SearchResult<CustomerAddres> searchResult = getPagedListService()
				.getSRBySearchObject(this.searchObj);
		listBoxCustomerAddres.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new CustomerAddresComparator()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerAddresList");

		this.button_CustomerAddresList_NewCustomerAddres.setVisible(getUserWorkspace().
				isAllowed("button_CustomerAddresList_NewCustomerAddres"));
		this.button_CustomerAddresList_CustomerAddresSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_CustomerAddresList_CustomerAddresFindDialog"));
		this.button_CustomerAddresList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_CustomerAddresList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customeraddres.model.
	 * CustomerAddresListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerAddresItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected CustomerAddres object
		final Listitem item = this.listBoxCustomerAddres.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerAddres aCustomerAddres = (CustomerAddres) item.getAttribute("data");
			final CustomerAddres customerAddres = getCustomerAddresService()
					.getCustomerAddresById(aCustomerAddres.getId(),aCustomerAddres.getCustAddrType());

			if (customerAddres == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerAddres.getCustID());
				valueParm[1] = aCustomerAddres.getCustAddrType();

				errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustAddrType") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
			String whereCond = " AND CustID='" + customerAddres.getCustID()
					+ "'" + " AND CustAddrType ='  " + customerAddres.getCustAddrType() + 
					"'" + " AND version=" + customerAddres.getVersion() + " ";

			if (isWorkFlowEnabled()) {
				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CustomerAddres", 
						whereCond, customerAddres.getTaskId(), customerAddres.getNextTaskId());
				if (userAcces) {
					showDetailView(customerAddres);
				} else {
					PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showDetailView(customerAddres);
			}
		  }
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the CustomerAddres dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerAddresList_NewCustomerAddres(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new CustomerAddres object, We GET it from the BackEnd.
		final CustomerAddres aCustomerAddres = getCustomerAddresService().getNewCustomerAddres();
		showDetailView(aCustomerAddres);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerAddres
	 *            (aCustomerAddres)
	 * @throws Exception
	 */
	private void showDetailView(CustomerAddres aCustomerAddres) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCustomerAddres.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerAddres.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerAddres", aCustomerAddres);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerAddresListbox from the
		 * dialog when we do a delete, edit or insert a CustomerAddres.
		 */
		map.put("customerAddresListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul",
							null, map);
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
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerAddresList);
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
		logger.debug("Entering" +event.toString());
		this.pagingCustomerAddresList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerAddresList, event);
		this.window_CustomerAddresList.invalidate();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * call the CustomerAddressSearch dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerAddresList_CustomerAddresSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		/*
		 * we can call our CustomerAddresDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected CustomerAddres. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * CustomerAddres object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerAddresCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresSearchDialog.zul",
							null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the customerAddres print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerAddresList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("CustomerAddres", getSearchObj());
		logger.debug("Leaving" +event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerAddresService(
			CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}
	public CustomerAddresService getCustomerAddresService() {
		return this.customerAddresService;
	}

	public JdbcSearchObject<CustomerAddres> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerAddres> searchObj) {
		this.searchObj = searchObj;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}