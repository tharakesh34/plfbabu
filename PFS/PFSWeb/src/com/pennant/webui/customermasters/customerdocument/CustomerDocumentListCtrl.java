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
 * FileName    		:  CustomerDocumentListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerdocument;

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
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customerdocument.model.CustomerDocumentComparator;
import com.pennant.webui.customermasters.customerdocument.model.CustomerDocumentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerDocumentListCtrl extends GFCBaseListCtrl<CustomerDocument> implements Serializable {

	private static final long serialVersionUID = 6734364667796871684L;
	private final static Logger logger = Logger.getLogger(CustomerDocumentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerDocumentList; 		// autoWired
	protected Borderlayout 	borderLayout_CustomerDocumentList; 	// autoWired
	protected Paging 		pagingCustomerDocumentList; 		// autoWired
	protected Listbox 		listBoxCustomerDocument; 			// autoWired

	// List headers
	protected Listheader listheader_CustDocType; 	// autoWired
	protected Listheader listheader_CustDocTitle; 	// autoWired
	protected Listheader listheader_CustDocSysName; // autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_CustomerDocumentList_NewCustomerDocument; 			// autoWired
	protected Button button_CustomerDocumentList_CustomerDocumentSearchDialog; 	// autoWired
	protected Button button_CustomerDocumentList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerDocument> searchObj;
	private transient PagedListService pagedListService;
	private transient CustomerDocumentService customerDocumentService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerDocumentListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerDocument object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerDocumentList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerDocument");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerDocument");

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
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerDocumentList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerDocumentList.setPageSize(getListRows());
		this.pagingCustomerDocumentList.setDetailed(true);

		this.listheader_CustDocType.setSortAscending(new FieldComparator("custDocType", true));
		this.listheader_CustDocType.setSortDescending(new FieldComparator("custDocType", false));
		
		this.listheader_CustDocTitle.setSortAscending(new FieldComparator("custDocTitle", true));
		this.listheader_CustDocTitle.setSortDescending(new FieldComparator("custDocTitle", false));
		
		this.listheader_CustDocSysName.setSortAscending(new FieldComparator("custDocSysName", true));
		this.listheader_CustDocSysName.setSortDescending(new FieldComparator("custDocSysName", false));

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
		this.searchObj = new JdbcSearchObject<CustomerDocument>(CustomerDocument.class, getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("CustomerDocuments_View");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerDocumentList_NewCustomerDocument.setVisible(true);
			} else {
				button_CustomerDocumentList_NewCustomerDocument.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerDocumentList_NewCustomerDocument.setVisible(false);
			this.button_CustomerDocumentList_CustomerDocumentSearchDialog.setVisible(false);
			this.button_CustomerDocumentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxCustomerDocument.setItemRenderer(new CustomerDocumentListModelItemRenderer());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject() {
		logger.debug("Entering");
		final SearchResult<CustomerDocument> searchResult = getPagedListService()
				.getSRBySearchObject(this.searchObj);
		listBoxCustomerDocument.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(), new CustomerDocumentComparator()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerDocumentList");

		this.button_CustomerDocumentList_NewCustomerDocument.setVisible(getUserWorkspace().
				isAllowed("button_CustomerDocumentList_NewCustomerDocument"));
		this.button_CustomerDocumentList_CustomerDocumentSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_CustomerDocumentList_CustomerDocumentFindDialog"));
		this.button_CustomerDocumentList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_CustomerDocumentList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customerdocument.model.
	 * CustomerDocumentListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected CustomerDocument object
		final Listitem item = this.listBoxCustomerDocument.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerDocument aCustomerDocument = (CustomerDocument) item.getAttribute("data");
			final CustomerDocument customerDocument = getCustomerDocumentService()
					.getCustomerDocumentById(aCustomerDocument.getId(),aCustomerDocument.getCustDocType());
			
			if (customerDocument == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerDocument.getCustID());
				valueParm[1] = aCustomerDocument.getCustDocType();

				errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustDocType") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
			String whereCond = " AND CustID='" + customerDocument.getCustID()
					+"' AND custDocType='"+	customerDocument.getCustDocType()
					+"' AND version=" + customerDocument.getVersion() + " ";

			if (isWorkFlowEnabled()) {
				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CustomerDocument",
						whereCond, customerDocument.getTaskId(), customerDocument.getNextTaskId());
				if (userAcces) {
					showDetailView(customerDocument);
				} else {
					PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showDetailView(customerDocument);
			}
		  }
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the CustomerDocument dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerDocumentList_NewCustomerDocument(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new CustomerDocument object, We GET it from the backEnd.
		final CustomerDocument aCustomerDocument = getCustomerDocumentService().getNewCustomerDocument();
		showDetailView(aCustomerDocument);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CustomerDocument
	 *            (aCustomerDocument)
	 * @throws Exception
	 */
	private void showDetailView(CustomerDocument aCustomerDocument) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCustomerDocument.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerDocument.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerDocument", aCustomerDocument);
		map.put("customerDocumentListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_CustomerDocumentList);
		logger.debug("Leaving" +event.toString());
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
		this.pagingCustomerDocumentList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerDocumentList, event);
		this.window_CustomerDocumentList.invalidate();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Method for Calling the CustomerDocument dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerDocumentList_CustomerDocumentSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		/*
		 * we can call our CustomerDocumentDialog ZUL-file with parameters. So
		 * we can call them with a object of the selected CustomerDocument. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * CustomerDocument object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerDocumentCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentSearchDialog.zul",
							null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the customerDocument print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerDocumentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("CustomerDocument", getSearchObj());
		logger.debug("Leaving" +event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerDocumentService(
			CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}
	public CustomerDocumentService getCustomerDocumentService() {
		return this.customerDocumentService;
	}

	public JdbcSearchObject<CustomerDocument> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerDocument> searchObj) {
		this.searchObj = searchObj;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}