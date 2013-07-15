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
 * FileName    		:  CustomerPRelationListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerprelation;

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
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerPRelationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customerprelation.model.CustomerPRComparater;
import com.pennant.webui.customermasters.customerprelation.model.CustomerPRelationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerPRelationListCtrl extends GFCBaseListCtrl<CustomerPRelation> implements Serializable {

	private static final long serialVersionUID = 823316129893394604L;
	private final static Logger logger = Logger.getLogger(CustomerPRelationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerPRelationList; 		// autowired
	protected Borderlayout 	borderLayout_CustomerPRelationList; // autowired
	protected Paging 		pagingCustomerPRelationList; 		// autowired
	protected Listbox 		listBoxCustomerPRelation; 			// autowired

	// List headers
	protected Listheader listheader_PRCustPRSNo; 		// autowired
	protected Listheader listheader_PRRelationCode; 	// autowired
	protected Listheader listheader_PRRelationCustID; 	// autowired
	protected Listheader listheader_PRisGuardian; 		// autowired
	protected Listheader listheader_PRSName; 			// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autowired
	protected Button button_CustomerPRelationList_NewCustomerPRelation; 		// autowired
	protected Button button_CustomerPRelationList_CustomerPRelationSearchDialog;// autowired
	protected Button button_CustomerPRelationList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerPRelation> searchObj;
	private transient PagedListService pagedListService;
	
	private transient CustomerPRelationService customerPRelationService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerPRelationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPRelation object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPRelationList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerPRelation");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerPRelation");
			
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
		this.borderLayout_CustomerPRelationList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerPRelationList.setPageSize(getListRows());
		this.pagingCustomerPRelationList.setDetailed(true);

		this.listheader_PRCustPRSNo.setSortAscending(new FieldComparator("pRCustPRSNo", true));
		this.listheader_PRCustPRSNo.setSortDescending(new FieldComparator("pRCustPRSNo", false));
		this.listheader_PRRelationCode.setSortAscending(new FieldComparator("pRRelationCode", true));
		this.listheader_PRRelationCode.setSortDescending(new FieldComparator("pRRelationCode", false));
		this.listheader_PRRelationCustID.setSortAscending(new FieldComparator("pRRelationCustID", true));
		this.listheader_PRRelationCustID.setSortDescending(new FieldComparator("pRRelationCustID", false));
		this.listheader_PRisGuardian.setSortAscending(new FieldComparator("pRisGuardian", true));
		this.listheader_PRisGuardian.setSortDescending(new FieldComparator("pRisGuardian", false));
		this.listheader_PRSName.setSortAscending(new FieldComparator("pRSName", true));
		this.listheader_PRSName.setSortDescending(new FieldComparator("pRSName", false));
		
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
		this.searchObj = new JdbcSearchObject<CustomerPRelation>(CustomerPRelation.class,getListRows());
		this.searchObj.addSort("PRCustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));

		this.searchObj.addTabelName("CustomersPRelations_View");
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerPRelationList_NewCustomerPRelation.setVisible(true);
			} else {
				button_CustomerPRelationList_NewCustomerPRelation.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerPRelationList_NewCustomerPRelation.setVisible(false);
			this.button_CustomerPRelationList_CustomerPRelationSearchDialog.setVisible(false);
			this.button_CustomerPRelationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxCustomerPRelation.setItemRenderer(new CustomerPRelationListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for set the Grouping for getting list of objects
	*/
	public void findSearchObject(){
		logger.debug("Entering");
		final SearchResult<CustomerPRelation> searchResult = getPagedListService().getSRBySearchObject(
				this.searchObj);
		listBoxCustomerPRelation.setModel(new GroupsModelArray(searchResult.getResult().toArray(),
				new CustomerPRComparater()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerPRelationList");
		
		this.button_CustomerPRelationList_NewCustomerPRelation.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerPRelationList_NewCustomerPRelation"));
		this.button_CustomerPRelationList_CustomerPRelationSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerPRelationList_CustomerPRelationFindDialog"));
		this.button_CustomerPRelationList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerPRelationList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customerprelation.model.
	 * CustomerPRelationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerPRelationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerPRelation object
		final Listitem item = this.listBoxCustomerPRelation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerPRelation aCustomerPRelation = (CustomerPRelation) item.getAttribute("data");
			final CustomerPRelation customerPRelation = getCustomerPRelationService().getCustomerPRelationById(aCustomerPRelation.getPRCustID(),aCustomerPRelation.getPRCustPRSNo());
			if (customerPRelation == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerPRelation.getPRCustID());
				valueParm[1] = String.valueOf(aCustomerPRelation.getPRCustPRSNo());

				errParm[0] = PennantJavaUtil.getLabel("label_PRCustID")+ ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_PRCustPRSNo")+ ":"+ valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond =  " AND PRCustID="+ customerPRelation.getPRCustID()+
									" AND PRCustPRSNo="+ customerPRelation.getPRCustPRSNo()+
									" AND version=" + customerPRelation.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CustomerPRelation", 
							whereCond, customerPRelation.getTaskId(), customerPRelation.getNextTaskId());
					if (userAcces){
						showDetailView(customerPRelation);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerPRelation);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerPRelation dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerPRelationList_NewCustomerPRelation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerPRelation object, We GET it from the backEnd.
		final CustomerPRelation aCustomerPRelation = getCustomerPRelationService().getNewCustomerPRelation();
		showDetailView(aCustomerPRelation);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerPRelation (aCustomerPRelation)
	 * @throws Exception
	 */
	private void showDetailView(CustomerPRelation aCustomerPRelation) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCustomerPRelation.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerPRelation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerPRelation", aCustomerPRelation);
		map.put("customerPRelationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationDialog.zul",
							null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerPRelationList);
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
		this.pagingCustomerPRelationList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerPRelationList, event);
		this.window_CustomerPRelationList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the CustomerPRelation dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerPRelationList_CustomerPRelationSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerPRelationDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerPRelation. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerPRelation object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerPRelationCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerPRelation print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerPRelationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CustomerPRelation", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerPRelationService(CustomerPRelationService customerPRelationService) {
		this.customerPRelationService = customerPRelationService;
	}
	public CustomerPRelationService getCustomerPRelationService() {
		return this.customerPRelationService;
	}

	public JdbcSearchObject<CustomerPRelation> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerPRelation> searchObj) {
		this.searchObj = searchObj;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}
}