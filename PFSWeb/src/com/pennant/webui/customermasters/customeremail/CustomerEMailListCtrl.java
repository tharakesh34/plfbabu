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
 * FileName    		:  CustomerEMailListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeremail;

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
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customeremail.model.CustomerEMailListModelItemRenderer;
import com.pennant.webui.customermasters.customeremail.model.CustomerEmailComparater;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerEMailListCtrl extends GFCBaseListCtrl<CustomerEMail> implements Serializable {

	private static final long serialVersionUID = -5818545488371155444L;
	private final static Logger logger = Logger.getLogger(CustomerEMailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerEMailList; 			// autoWired
	protected Panel 		panel_CustomerEMailList; 			// autoWired
	protected Borderlayout 	borderLayout_CustomerEMailList; 	// autoWired
	protected Paging 		pagingCustomerEMailList; 			// autoWired
	protected Listbox 		listBoxCustomerEMail; 				// autoWired
	
	// List headers
	protected Listheader listheader_CustEMailTypeCode; 		// autoWired
	protected Listheader listheader_CustEMailPriority; 		// autoWired
	protected Listheader listheader_CustEMail; 			// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_CustomerEMailList_NewCustomerEMail;			// autoWired
	protected Button button_CustomerEMailList_CustomerEMailSearchDialog;// autoWired
	protected Button button_CustomerEMailList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerEMail> searchObj;
	private transient PagedListService pagedListService;	

	private transient CustomerEMailService customerEMailService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CustomerEMailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_CustomerEMailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerEMail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerEMail");

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
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerEMailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerEMailList.setPageSize(getListRows());
		this.pagingCustomerEMailList.setDetailed(true);

		this.listheader_CustEMailTypeCode.setSortAscending(new FieldComparator("custEMailTypeCode", true));
		this.listheader_CustEMailTypeCode.setSortDescending(new FieldComparator("custEMailTypeCode", false));
		this.listheader_CustEMailPriority.setSortAscending(new FieldComparator("custEMailPriority", true));
		this.listheader_CustEMailPriority.setSortDescending(new FieldComparator("custEMailPriority", false));
		this.listheader_CustEMail.setSortAscending(new FieldComparator("custEMail", true));
		this.listheader_CustEMail.setSortDescending(new FieldComparator("custEMail", false));

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
		this.searchObj = new JdbcSearchObject<CustomerEMail>(CustomerEMail.class,getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("CustomerEMails_View");


		// Work flow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerEMailList_NewCustomerEMail.setVisible(true);
			} else {
				button_CustomerEMailList_NewCustomerEMail.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerEMailList_NewCustomerEMail.setVisible(false);
			this.button_CustomerEMailList_CustomerEMailSearchDialog.setVisible(false);
			this.button_CustomerEMailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxCustomerEMail.setItemRenderer(new CustomerEMailListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Rendering List
	 */
	public void findSearchObject(){
		logger.debug("Entering");		
		final SearchResult<CustomerEMail> searchResult = getPagedListService().getSRBySearchObject(this.searchObj);
		listBoxCustomerEMail.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new CustomerEmailComparater()));
		logger.debug("Leaving");
	}
	
	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerEMailList");

		this.button_CustomerEMailList_NewCustomerEMail.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEMailList_NewCustomerEMail"));
		this.button_CustomerEMailList_CustomerEMailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEMailList_CustomerEMailFindDialog"));
		this.button_CustomerEMailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEMailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customeremail.model.
	 * CustomerEMailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerEMailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerEMail object
		final Listitem item = this.listBoxCustomerEMail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEMail aCustomerEMail = (CustomerEMail) item.getAttribute("data");
			final CustomerEMail customerEMail = getCustomerEMailService().getCustomerEMailById(
					aCustomerEMail.getId(),aCustomerEMail.getCustEMailTypeCode());

			if (customerEMail == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerEMail.getCustID());
				valueParm[1] = aCustomerEMail.getCustEMailTypeCode();

				errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"	+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustEMailTypeCode")+ ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				customerEMail.setLovDescCustEMailTypeCode(aCustomerEMail.getLovDescCustEMailTypeCode());
				String whereCond =  " AND CustID='"+ customerEMail.getCustID()+
									"' AND version=" + customerEMail.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),"CustomerEMail", 
							whereCond, customerEMail.getTaskId(), customerEMail.getNextTaskId());
					if (userAcces){
						showDetailView(customerEMail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerEMail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerEMail dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerEMailList_NewCustomerEMail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerEMail object, We GET it from the back end.
		final CustomerEMail aCustomerEMail = getCustomerEMailService().getNewCustomerEMail();
		showDetailView(aCustomerEMail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerEMail (aCustomerEMail)
	 * @throws Exception
	 */
	private void showDetailView(CustomerEMail aCustomerEMail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aCustomerEMail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerEMail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEMail", aCustomerEMail);
		map.put("customerEMailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerEMailList);
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
		this.pagingCustomerEMailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerEMailList, event);
		this.window_CustomerEMailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the CustomerEMail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerEMailList_CustomerEMailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerEMailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerEMail. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerEMail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEMailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerEMail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerEMailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());		
		PTReportUtils.getReport("CustomerEMail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}
	public CustomerEMailService getCustomerEMailService() {
		return this.customerEMailService;
	}

	public JdbcSearchObject<CustomerEMail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerEMail> searchObj) {
		this.searchObj = searchObj;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}
}