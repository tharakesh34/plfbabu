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
 * FileName    		:  CustomerAdditionalDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeradditionaldetail;

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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.service.customermasters.CustomerAdditionalDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.customermasters.customeradditionaldetail.model.CustomerAdditionalDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail
 * /CustomerAdditionalDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerAdditionalDetailListCtrl extends 
GFCBaseListCtrl<CustomerAdditionalDetail> implements Serializable {
	
	private static final long serialVersionUID = -4292260671471272242L;
	private final static Logger logger = Logger.getLogger(CustomerAdditionalDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	   window_CustomerAdditionalDetailList; 		// autoWired
	protected Panel 	   panel_CustomerAdditionalDetailList; 			// autoWired
	protected Borderlayout borderLayout_CustomerAdditionalDetailList; 	// autoWired
	protected Paging 	   pagingCustomerAdditionalDetailList; 			// autoWired
	protected Listbox 	   listBoxCustomerAdditionalDetail; 			// autoWired

	// List headers
	protected Listheader listheader_CustAdditionalCIF; 	// autoWired
	protected Listheader listheader_CustAcademicLevel; 	// autoWired
	protected Listheader listheader_AcademicDecipline; 	// autoWired
	protected Listheader listheader_CustRefCustID; 		// autoWired
	protected Listheader listheader_CustRefStaffID; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;			// autoWired

	protected Panel customerAdditionalDetailSeekPanel; // autoWired
	protected Panel customerAdditionalDetailListPanel; // autoWired

	// checkRights
	protected Button btnHelp; 																   // autoWired
	protected Button button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail; 		   // autoWired
	protected Button button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog; // autoWired
	protected Button button_CustomerAdditionalDetailList_PrintList; 						   // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerAdditionalDetail> searchObj;
	
	private transient CustomerAdditionalDetailService customerAdditionalDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerAdditionalDetailListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer Additional Detail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAdditionalDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerAdditionalDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerAdditionalDetail");
			
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
		this.borderLayout_CustomerAdditionalDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerAdditionalDetailList.setPageSize(getListRows());
		this.pagingCustomerAdditionalDetailList.setDetailed(true);

		this.listheader_CustAdditionalCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustAdditionalCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_CustAcademicLevel.setSortAscending(new FieldComparator("custAcademicLevel", true));
		this.listheader_CustAcademicLevel.setSortDescending(new FieldComparator("custAcademicLevel", false));
		this.listheader_AcademicDecipline.setSortAscending(new FieldComparator("academicDecipline", true));
		this.listheader_AcademicDecipline.setSortDescending(new FieldComparator("academicDecipline", false));
		this.listheader_CustRefCustID.setSortAscending(new FieldComparator("custRefCustID", true));
		this.listheader_CustRefCustID.setSortDescending(new FieldComparator("custRefCustID", false));
		this.listheader_CustRefStaffID.setSortAscending(new FieldComparator("custRefStaffID", true));
		this.listheader_CustRefStaffID.setSortDescending(new FieldComparator("custRefStaffID", false));
		
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
		this.searchObj = new JdbcSearchObject<CustomerAdditionalDetail>(CustomerAdditionalDetail.class, getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("CustAdditionalDetails_View");
			if (isFirstTask()) {
				button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail.setVisible(true);
			} else {
				button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else {
			this.searchObj.addTabelName("CustAdditionalDetails_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail.setVisible(false);
			this.button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog.setVisible(false);
			this.button_CustomerAdditionalDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCustomerAdditionalDetail,	this.pagingCustomerAdditionalDetailList);
			// set the itemRenderer
			this.listBoxCustomerAdditionalDetail.setItemRenderer(new CustomerAdditionalDetailListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("CustomerAdditionalDetailList");
		
		this.button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail"));
		this.button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerAdditionalDetailList_CustomerAdditionalDetailFindDialog"));
		this.button_CustomerAdditionalDetailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerAdditionalDetailList_PrintList"));
		logger.debug("Leaving ");
		
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customeradditionaldetail.model.
	 * CustomerAdditionalDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerAdditionalDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CustomerAdditionalDetail object
		final Listitem item = this.listBoxCustomerAdditionalDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerAdditionalDetail aCustomerAdditionalDetail = (CustomerAdditionalDetail) item.getAttribute("data");
			final CustomerAdditionalDetail customerAdditionalDetail = getCustomerAdditionalDetailService()
			.getCustomerAdditionalDetailById(aCustomerAdditionalDetail.getId());

			if (customerAdditionalDetail == null) {

				String[] valueParm = new String[1];
				String[] errParm = new String[1];

				valueParm[0] = String.valueOf(aCustomerAdditionalDetail.getId());

				errParm[0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CustID='"+ customerAdditionalDetail.getCustID() 
					+ "' AND version=" + customerAdditionalDetail.getVersion() + " ";

				if(isWorkFlowEnabled()){
					boolean userAcces = validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "CustomerAdditionalDetail",whereCond,
							customerAdditionalDetail.getTaskId(),customerAdditionalDetail.getNextTaskId());
					if (userAcces){
						showDetailView(customerAdditionalDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerAdditionalDetail);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerAdditionalDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerAdditionalDetailList_NewCustomerAdditionalDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new CustomerAdditionalDetail object, We GET it from the back end.
		final CustomerAdditionalDetail aCustomerAdditionalDetail = getCustomerAdditionalDetailService().getNewCustomerAdditionalDetail();
		showDetailView(aCustomerAdditionalDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerAdditionalDetail (aCustomerAdditionalDetail)
	 * @throws Exception
	 */
	private void showDetailView(CustomerAdditionalDetail aCustomerAdditionalDetail) throws Exception {
		logger.debug("Entering ");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aCustomerAdditionalDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerAdditionalDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerAdditionalDetail", aCustomerAdditionalDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerAdditionalDetailListbox from the
		 * dialog when we do a delete, edit or insert a CustomerAdditionalDetail.
		 */
		map.put("customerAdditionalDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail/CustomerAdditionalDetailDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerAdditionalDetailList);
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
		this.pagingCustomerAdditionalDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerAdditionalDetailList, event);
		this.window_CustomerAdditionalDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the CustomerAdditionalDetail dialog
	 */
	public void onClick$button_CustomerAdditionalDetailList_CustomerAdditionalDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerAdditionalDetailDialog zul-file with parameters. So we can
		 * call them with a object of the selected CustomerAdditionalDetail. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerAdditionalDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerAdditionalDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail/CustomerAdditionalDetailSearchDialog.zul",	null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerAdditionalDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerAdditionalDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CustomerAdditionalDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerAdditionalDetailService(
			CustomerAdditionalDetailService customerAdditionalDetailService) {
		this.customerAdditionalDetailService = customerAdditionalDetailService;
	}
	public CustomerAdditionalDetailService getCustomerAdditionalDetailService() {
		return this.customerAdditionalDetailService;
	}

	public JdbcSearchObject<CustomerAdditionalDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerAdditionalDetail> searchObj) {
		this.searchObj = searchObj;
	}
}