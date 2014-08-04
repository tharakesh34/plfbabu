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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customeremail.model.CustomerEMailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


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
	protected Borderlayout 	borderLayout_CustomerEMailList; 	// autoWired
	protected Paging 		pagingCustomerEMailList; 			// autoWired
	protected Listbox 		listBoxCustomerEMail; 				// autoWired
	
	// List headers
	protected Listheader listheader_CustCIF;                // autoWired
	protected Listheader listheader_CustEMailTypeCode; 		// autoWired
	protected Listheader listheader_CustEMailPriority; 		// autoWired
	protected Listheader listheader_CustEMail; 			    // autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	//search
	protected Textbox custCIF; 							// autoWired
	protected Listbox sortOperator_custCIF; 			// autoWired
	protected Textbox custEMailTypeCode; 				// autoWired
	protected Listbox sortOperator_custEMailTypeCode; 	// autoWired
	protected Intbox  custEMailPriority; 				// autoWired
	protected Listbox sortOperator_custEMailPriority; 	// autoWired
	protected Listbox sortOperator_custEMailid;
	protected Textbox custEMailid;
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType;						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired

	protected Label label_CustomerEMailSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerEMailSearch_RecordType; 		// autoWired
	protected Label label_CustomerEMailSearchResult; 			// autoWired
	
	protected Grid	                       searchGrid;	                                                  // autowired
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;
	private transient boolean			   approvedList=false;
	
	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_CustomerEMailList_NewCustomerEMail;			// autoWired
	protected Button button_CustomerEMailList_CustomerEMailSearchDialog;// autoWired
	protected Button button_CustomerEMailList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerEMail> searchObj;
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

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custEMailTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custEMailTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custEMailPriority.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_custEMailPriority.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_custEMailid.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custEMailid.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerEMailSearch_RecordStatus.setVisible(false);
			this.label_CustomerEMailSearch_RecordType.setVisible(false);
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerEMailList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomerEMail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerEMailList.setPageSize(getListRows());
		this.pagingCustomerEMailList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("custID", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("custID", false));
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

		// set the itemRenderer
		this.listBoxCustomerEMail.setItemRenderer(new CustomerEMailListModelItemRenderer());
		
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerEMailList_NewCustomerEMail.setVisible(false);
			this.button_CustomerEMailList_CustomerEMailSearchDialog.setVisible(false);
			this.button_CustomerEMailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}	
		logger.debug("Leaving" + event.toString());
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
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.custCIF.setValue("");
		this.sortOperator_custEMailTypeCode.setSelectedIndex(0);
		this.custEMailTypeCode.setValue("");
		this.sortOperator_custEMailTypeCode.setSelectedIndex(0);
		this.custEMailTypeCode.setValue("");
		this.sortOperator_custEMailPriority.setSelectedIndex(0);
		this.custEMailPriority.setValue(null);
		this.sortOperator_custEMailid.setSelectedIndex(0);
		this.custEMailid.setValue("");
		this.sortOperator_recordStatus.setSelectedIndex(0);
		this.recordStatus.setValue("");
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
		doSearch();
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
		@SuppressWarnings("unused")
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerEMail", getSearchObj(),this.pagingCustomerEMailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	
	public void doSearch() {
		logger.debug("Entering");
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerEMail>(CustomerEMail.class,getListRows());
		this.searchObj.addSort("CustID", false);
		searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("CustomerEMails_View");
		
		// Work flow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerEMailList_NewCustomerEMail.setVisible(true);
			} else {
				button_CustomerEMailList_NewCustomerEMail.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("CustomerEMails_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("CustomerEMails_AView");
		}
		
		
		// Customer CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custCIF.getSelectedItem(), this.custCIF.getValue(), "lovDescCustCIF");
		}
		
		// Customer EmailTypeCode
		if (!StringUtils.trimToEmpty(this.custEMailTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEMailTypeCode.getSelectedItem(), this.custEMailTypeCode.getValue(), "custEMailTypeCode");
		}
		
		// Customer EmailPriority
		if (this.custEMailPriority.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEMailPriority.getSelectedItem(), this.custEMailPriority.getValue(), "custEMailPriority");
		}
		
		// Customer EmailId
		if (!StringUtils.trimToEmpty(this.custEMailid.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEMailid.getSelectedItem(), this.custEMailid.getValue(), "custEmail");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}
		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		
		getPagedListWrapper().init(this.searchObj, this.listBoxCustomerEMail, this.pagingCustomerEMailList);
		logger.debug("Leaving");
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

}