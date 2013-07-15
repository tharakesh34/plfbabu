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
 * FileName    		:  CustomerListCtrl.java                                                   * 	  
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

package com.pennant.webui.coremasters.collateralassignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.coremasters.CollateralAssignment;
import com.pennant.webui.coremasters.collateralassignment.model.CollateralAssignmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CollateralAssignmentListCtrl extends GFCBaseListCtrl<CollateralAssignment> implements Serializable {

	private static final long serialVersionUID = 9086034736503097868L;
	private final static Logger logger = Logger.getLogger(CollateralAssignmentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CollateralAssignmentList; 			// autowired
	protected Borderlayout 	borderLayout_CollateralAssignmentList; 	// autowired
	protected Paging 		pagingCollateralAssignmentList; 		// autowired
	protected Listbox 		listBoxCollateralAssignment; 			// autowired

	// List headers
	protected Listheader listheader_DealType; 		// autowired
	protected Listheader listheader_Reference;	// autowired
	protected Listheader listheader_Branch;	// autowired
	protected Listheader listheader_AccountNum; 	// autowired
	protected Listheader listheader_CommitRef;	// autowired

	// checkRights
	protected Button btnHelp; 									// autowired
	protected Button button_CollateralAssignmentList_NewCollateralAssignment; 			// autowired
	protected Button button_CollateralAssignmentList_SearchDialog; 	// autowired
	protected Button button_CollateralAssignmentList_PrintList; 			// autowired
	
	/**
	 * default constructor.<br>
	 */
	public CollateralAssignmentListCtrl() {
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
	public void onCreate$window_CollateralAssignmentList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerLimit");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerLimit");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}*/
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CollateralAssignmentList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCollateralAssignmentList.setPageSize(getListRows());
		this.pagingCollateralAssignmentList.setDetailed(true);

		this.listheader_DealType.setSortAscending(new FieldComparator("dealType", true));
		this.listheader_DealType.setSortDescending(new FieldComparator("dealType", false));
		this.listheader_Reference.setSortAscending(new FieldComparator("reference", true));
		this.listheader_Reference.setSortDescending(new FieldComparator("reference", false));
		this.listheader_Branch.setSortAscending(new FieldComparator("branch", true));
		this.listheader_Branch.setSortDescending(new FieldComparator("branch", false));
		this.listheader_AccountNum.setSortAscending(new FieldComparator("accountNum", true));
		this.listheader_AccountNum.setSortDescending(new FieldComparator("accountNum", false));
		this.listheader_CommitRef.setSortAscending(new FieldComparator("commitRef", true));
		this.listheader_CommitRef.setSortDescending(new FieldComparator("commitRef", false));
		
		/*if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}*/
		
		// ++ create the searchObject and initialize sorting ++//
		/*this.searchObj = new JdbcSearchObject<CustomerLimit>(CustomerLimit.class,getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addTabelName("Customers_TView");
		this.searchObj.addFilterOr(new Filter("(custIsClosed=0 AND recordStatus=\"Approved\") OR recordStatus", "Approved" , Filter.OP_NOT_EQUAL));
		*/
		// WorkFlow
		/*if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CollateralAssignmentList_NewCustomerLimit.setVisible(true);
			} else {
				button_CollateralAssignmentList_NewCustomerLimit.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}*/

		//setSearchObj(this.searchObj);
		/*if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CollateralAssignmentList_NewCustomerLimit.setVisible(false);
			this.button_CollateralAssignmentList_CustomerLimitSearchDialog.setVisible(false);
			this.button_CollateralAssignmentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCustomerLimit,this.pagingCollateralAssignmentList);
			// set the itemRenderer
			this.listBoxCustomerLimit.setItemRenderer(new CollateralAssignmentListModelItemRenderer());
		}	*/
		getPagedListWrapper().initList(getAssignmentList(),this.listBoxCollateralAssignment,this.pagingCollateralAssignmentList);
		this.listBoxCollateralAssignment.setItemRenderer(new CollateralAssignmentListModelItemRenderer());
		logger.debug("Leaving" + event.toString());
	}
	
	
	private List<CollateralAssignment> getAssignmentList(){
		
		List<CollateralAssignment> list = new ArrayList<CollateralAssignment>();
		CollateralAssignment assignment = new CollateralAssignment();
		list.add(assignment);
		return list;
		
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerList");
		
		this.button_CollateralAssignmentList_NewCollateralAssignment.setVisible(false);
		this.button_CollateralAssignmentList_SearchDialog.setVisible(false);
		this.button_CollateralAssignmentList_PrintList.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customer.model.CustomerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCollateralAssignmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCollateralAssignment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			CollateralAssignment assignment = (CollateralAssignment) item.getAttribute("data");
			showDetailView(assignment);
		}	
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Build the Customer Dialog Window with Existing Core banking Data
	 * @throws Exception 
	 */
	/*public void buildDialogWindow(Customer customer) throws Exception{
		logger.debug("Entering");
		// create a new Customer object, We GET it from the backEnd.
		CustomerDetails aCustomerDetails = getCustomerDetailsService().getNewCustomer();
		customer = getCustomerDetailsService().fetchCustomerDetails(customer);
		aCustomerDetails.setCustomer(customer);		
		showDetailView(aCustomerDetails);
		logger.debug("Leaving");
	}*/

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param CustomerDetails (aCustomerDetails)
	 * @throws Exception
	 */
	private void showDetailView(CollateralAssignment assignment) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("assignment", assignment);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CoreMasters/CollateralAssignments/CollateralAssignmentDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_CollateralAssignmentList);
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
		this.pagingCollateralAssignmentList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CollateralAssignmentList, event);
		this.window_CollateralAssignmentList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the Customer dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CollateralAssignmentList_SearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerDialog zul-file with parameters. So we can
		 * call them with a object of the selected CustomerDetails. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerDetails object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerCtrl", this);
		//map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/Customer/CustomerSearchDialog.zul",
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
	public void onClick$button_CollateralAssignmentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
	//	PTReportUtils.getReport("Customer", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

}