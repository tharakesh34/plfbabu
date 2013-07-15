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
 * FileName    		:  CustomerIdentityListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeridentity;

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
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerIdentityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customeridentity.model.CustomerIdentityDetailsComparator;
import com.pennant.webui.customermasters.customeridentity.model.CustomerIdentityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /WEB-INF/pages/CustomerMasters/CustomerIdentity/CustomerIdentityList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerIdentityListCtrl extends GFCBaseListCtrl<CustomerIdentity> implements Serializable {

	private static final long serialVersionUID = -3970688148092697445L;
	private final static Logger logger = Logger.getLogger(CustomerIdentityListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerIdentityList; 		// autowired
	protected Borderlayout 	borderLayout_CustomerIdentityList; 	// autowired
	protected Paging 		pagingCustomerIdentityList; 		// autowired
	protected Listbox 		listBoxCustomerIdentity; 			// autowired

	// List headers
	protected Listheader listheader_CustIdCIF; 	// autowired
	protected Listheader listheader_IdType; 		// autowired
	protected Listheader listheader_IdIssuedBy; 	// autowired
	protected Listheader listheader_IdRef; 			// autowired
	protected Listheader listheader_IdIssueCountry; // autowired
	protected Listheader listheader_RecordStatus; 	// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autowired
	protected Button button_CustomerIdentityList_NewCustomerIdentity; 			// autowired
	protected Button button_CustomerIdentityList_CustomerIdentitySearchDialog; 	// autowired
	protected Button button_CustomerIdentityList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerIdentity> searchObj;
	private transient PagedListService pagedListService;
	
	private transient CustomerIdentityService customerIdentityService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerIdentityListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerIdentity object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerIdentityList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerIdentity");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerIdentity");
			
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
		this.borderLayout_CustomerIdentityList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerIdentityList.setPageSize(getListRows());
		this.pagingCustomerIdentityList.setDetailed(true);

		this.listheader_CustIdCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustIdCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_IdType.setSortAscending(new FieldComparator("idType", true));
		this.listheader_IdType.setSortDescending(new FieldComparator("idType", false));
		this.listheader_IdIssuedBy.setSortAscending(new FieldComparator("idIssuedBy", true));
		this.listheader_IdIssuedBy.setSortDescending(new FieldComparator("idIssuedBy", false));
		this.listheader_IdRef.setSortAscending(new FieldComparator("idRef", true));
		this.listheader_IdRef.setSortDescending(new FieldComparator("idRef", false));
		this.listheader_IdIssueCountry.setSortAscending(new FieldComparator("idIssueCountry", true));
		this.listheader_IdIssueCountry.setSortDescending(new FieldComparator("idIssueCountry", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerIdentity>(CustomerIdentity.class,getListRows());
		this.searchObj.addSort("IdCustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));

		this.searchObj.addTabelName("CustIdentities_View");
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerIdentityList_NewCustomerIdentity.setVisible(true);
			} else {
				button_CustomerIdentityList_NewCustomerIdentity.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerIdentityList_NewCustomerIdentity.setVisible(false);
			this.button_CustomerIdentityList_CustomerIdentitySearchDialog.setVisible(false);
			this.button_CustomerIdentityList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			findSearchObject();
			this.listBoxCustomerIdentity.setItemRenderer(new CustomerIdentityListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method For getting List of Objects and set Grouping
	 */
	public void findSearchObject(){
		logger.debug("Entering");
		final SearchResult<CustomerIdentity> searchResult = getPagedListService().getSRBySearchObject(this.searchObj);
		listBoxCustomerIdentity.setModel(new GroupsModelArray(searchResult.getResult().toArray(),new CustomerIdentityDetailsComparator()));
		logger.debug("Leaving");
	}
	
	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerIdentityList");
		
		this.button_CustomerIdentityList_NewCustomerIdentity.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerIdentityList_NewCustomerIdentity"));
		this.button_CustomerIdentityList_CustomerIdentitySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerIdentityList_CustomerIdentityFindDialog"));
		this.button_CustomerIdentityList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerIdentityList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customeridentity.model.CustomerIdentityListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerIdentityItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerIdentity object
		final Listitem item = this.listBoxCustomerIdentity.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerIdentity aCustomerIdentity = (CustomerIdentity) item.getAttribute("data");
			final CustomerIdentity customerIdentity = getCustomerIdentityService().getCustomerIdentityById(aCustomerIdentity.getId(),aCustomerIdentity.getIdType());
			if (customerIdentity == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerIdentity.getIdCustID());
				valueParm[1] = aCustomerIdentity.getIdType();

				errParm[0] = PennantJavaUtil.getLabel("label_IdCustID")+ ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_IdType")+ ":"+ valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond =  " AND IdCustID='"+ customerIdentity.getIdCustID()+
				"' AND IdType='"+ customerIdentity.getIdType()+
				"' AND version=" + customerIdentity.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CustomerIdentity", whereCond, customerIdentity.getTaskId(), customerIdentity.getNextTaskId());
					if (userAcces){
						showDetailView(customerIdentity);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerIdentity);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerIdentity dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerIdentityList_NewCustomerIdentity(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerIdentity object, We GET it from the backEnd.
		final CustomerIdentity aCustomerIdentity = getCustomerIdentityService().getNewCustomerIdentity();
		showDetailView(aCustomerIdentity);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerIdentity (aCustomerIdentity)
	 * @throws Exception
	 */
	private void showDetailView(CustomerIdentity aCustomerIdentity) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCustomerIdentity.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerIdentity.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerIdentity", aCustomerIdentity);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CustomerIdentityListbox from the
		 * dialog when we do a delete, edit or insert a CustomerIdentity.
		 */
		map.put("customerIdentityListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIdentity/CustomerIdentityDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerIdentityList);
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
		this.pagingCustomerIdentityList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerIdentityList, event);
		this.window_CustomerIdentityList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the CustomerIdentity dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerIdentityList_CustomerIdentitySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerIdentityDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerIdentity. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerIdentity object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerIdentityCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIdentity/CustomerIdentitySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerIdentity print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerIdentityList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CustomerIdentity", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerIdentityService(CustomerIdentityService customerIdentityService) {
		this.customerIdentityService = customerIdentityService;
	}
	public CustomerIdentityService getCustomerIdentityService() {
		return this.customerIdentityService;
	}

	public JdbcSearchObject<CustomerIdentity> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerIdentity> searchObj) {
		this.searchObj = searchObj;
	}
	
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}