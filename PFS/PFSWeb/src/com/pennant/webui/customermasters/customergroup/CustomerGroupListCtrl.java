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
 * FileName    		:  CustomerGroupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customergroup;

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
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.customermasters.customergroup.model.CustomerGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerGroupListCtrl extends GFCBaseListCtrl<CustomerGroup> implements Serializable {

	private static final long serialVersionUID = 8090581617957622077L;
	private final static Logger logger = Logger.getLogger(CustomerGroupListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerGroupList; 			// autoWired
	protected Borderlayout 	borderLayout_CustomerGroupList; 	// autoWired
	protected Paging 		pagingCustomerGroupList; 			// autoWired
	protected Listbox 		listBoxCustomerGroup; 				// autoWired

	// List headers
	protected Listheader listheader_CustGrpCode; 		// autoWired
	protected Listheader listheader_CustGrpDesc; 		// autoWired
	protected Listheader listheader_CustGrpRO1; 		// autoWired
	protected Listheader listheader_CustGrpIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autoWired
	protected Button button_CustomerGroupList_NewCustomerGroup; 			// autoWired
	protected Button button_CustomerGroupList_CustomerGroupSearchDialog; 	// autoWired
	protected Button button_CustomerGroupList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerGroup> searchObj;
	private transient CustomerGroupService customerGroupService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CustomerGroupListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer Group object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerGroupList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerGroup");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerGroup");

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

		this.borderLayout_CustomerGroupList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerGroupList.setPageSize(getListRows());
		this.pagingCustomerGroupList.setDetailed(true);

		this.listheader_CustGrpCode.setSortAscending(new FieldComparator("custGrpCode", true));
		this.listheader_CustGrpCode.setSortDescending(new FieldComparator("custGrpCode", false));
		this.listheader_CustGrpDesc.setSortAscending(new FieldComparator("custGrpDesc", true));
		this.listheader_CustGrpDesc.setSortDescending(new FieldComparator("custGrpDesc", false));
		this.listheader_CustGrpRO1.setSortAscending(new FieldComparator("custGrpRO1", true));
		this.listheader_CustGrpRO1.setSortDescending(new FieldComparator("custGrpRO1", false));
		this.listheader_CustGrpIsActive.setSortAscending(new FieldComparator("custGrpIsActive", true));
		this.listheader_CustGrpIsActive.setSortDescending(new FieldComparator("custGrpIsActive", false));

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
		this.searchObj = new JdbcSearchObject<CustomerGroup>(CustomerGroup.class,getListRows());
		this.searchObj.addSort("CustGrpID", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("CustomerGroups_View");
			if (isFirstTask()) {
				button_CustomerGroupList_NewCustomerGroup.setVisible(true);
			} else {
				button_CustomerGroupList_NewCustomerGroup.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("CustomerGroups_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerGroupList_NewCustomerGroup.setVisible(false);
			this.button_CustomerGroupList_CustomerGroupSearchDialog.setVisible(false);
			this.button_CustomerGroupList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCustomerGroup,this.pagingCustomerGroupList);
			// set the itemRenderer
			this.listBoxCustomerGroup.setItemRenderer(new CustomerGroupListModelItemRenderer());
		}	
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerGroupList");

		this.button_CustomerGroupList_NewCustomerGroup.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerGroupList_NewCustomerGroup"));
		this.button_CustomerGroupList_CustomerGroupSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerGroupList_CustomerGroupFindDialog"));
		this.button_CustomerGroupList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerGroupList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customergroup.model.CustomerGroupListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerGroupItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected CustomerGroup object
		final Listitem item = this.listBoxCustomerGroup.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerGroup aCustomerGroup = (CustomerGroup) item.getAttribute("data");
			final CustomerGroup customerGroup = getCustomerGroupService().getCustomerGroupById(aCustomerGroup.getId());

			if (customerGroup == null) {

				String[] valueParm = new String[1];
				String[] errParm = new String[1];

				valueParm[0] = String.valueOf(aCustomerGroup.getCustGrpID());
				errParm[0] = PennantJavaUtil.getLabel("label_CustGrpID")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond =  " AND CustGrpID="+ customerGroup.getCustGrpID()+" AND version=" + customerGroup.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "CustomerGroup", whereCond, customerGroup.getTaskId(),
							customerGroup.getNextTaskId());
					if (userAcces){
						showDetailView(customerGroup);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerGroup);
				}
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the CustomerGroup dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerGroupList_NewCustomerGroup(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new CustomerGroup object, We GET it from the back end.
		final CustomerGroup aCustomerGroup = getCustomerGroupService().getNewCustomerGroup();
		showDetailView(aCustomerGroup);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CustomerGroup (aCustomerGroup)
	 * @throws Exception
	 */
	private void showDetailView(CustomerGroup aCustomerGroup) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aCustomerGroup.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerGroup", aCustomerGroup);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerGroupListbox from the
		 * dialog when we do a delete, edit or insert a CustomerGroup.
		 */
		map.put("customerGroupListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupDialog.zul",
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
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerGroupList);
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
		this.pagingCustomerGroupList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerGroupList, event);
		this.window_CustomerGroupList.invalidate();
		logger.debug("Leaving" +event.toString());
	}

	/*
	 * call the CustomerGroup dialog
	 */
	public void onClick$button_CustomerGroupList_CustomerGroupSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		/*
		 * we can call our CustomerGroupDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerGroup. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerGroup object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerGroupCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupSearchDialog.zul",
					null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the customerGroup print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerGroupList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("CustomerGroup", getSearchObj());
		logger.debug("Leaving" +event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}
	public CustomerGroupService getCustomerGroupService() {
		return this.customerGroupService;
	}

	public JdbcSearchObject<CustomerGroup> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerGroup> searchObj) {
		this.searchObj = searchObj;
	}

}