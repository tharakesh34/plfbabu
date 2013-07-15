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
 * FileName    		:  CustomerEmploymentDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeremploymentdetail;

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
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail
 * /CustomerEmploymentDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerEmploymentDetailListCtrl extends GFCBaseListCtrl<CustomerEmploymentDetail> implements Serializable {

	private static final long serialVersionUID = 5652445153118844873L;
	private final static Logger logger = Logger.getLogger(CustomerEmploymentDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are gettingautoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerEmploymentDetailList; 		//autoWired
	protected Borderlayout 	borderLayout_CustomerEmploymentDetailList; 	//autoWired
	protected Paging 		pagingCustomerEmploymentDetailList; 		//autoWired
	protected Listbox 		listBoxCustomerEmploymentDetail; 			//autoWired

	// List headers
	protected Listheader listheader_CustEmpCIF; 	//autoWired
	protected Listheader listheader_CustEmpName; 	//autoWired
	protected Listheader listheader_CustEmpDesg; 	//autoWired
	protected Listheader listheader_CustEmpDept; 	//autoWired
	protected Listheader listheader_CustEmpID; 		//autoWired
	protected Listheader listheader_RecordStatus; 	//autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 																	//autoWired
	protected Button button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail; 			//autoWired
	protected Button button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog; 	//autoWired
	protected Button button_CustomerEmploymentDetailList_PrintList; //autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerEmploymentDetail> searchObj;
	private transient CustomerEmploymentDetailService customerEmploymentDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CustomerEmploymentDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEmploymentDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEmploymentDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerEmploymentDetail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerEmploymentDetail");

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
		this.borderLayout_CustomerEmploymentDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerEmploymentDetailList.setPageSize(getListRows());
		this.pagingCustomerEmploymentDetailList.setDetailed(true);

		this.listheader_CustEmpCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustEmpCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_CustEmpName.setSortAscending(new FieldComparator("custEmpName", true));
		this.listheader_CustEmpName.setSortDescending(new FieldComparator("custEmpName", false));
		this.listheader_CustEmpDesg.setSortAscending(new FieldComparator("custEmpDesg", true));
		this.listheader_CustEmpDesg.setSortDescending(new FieldComparator("custEmpDesg", false));
		this.listheader_CustEmpDept.setSortAscending(new FieldComparator("custEmpDept", true));
		this.listheader_CustEmpDept.setSortDescending(new FieldComparator("custEmpDept", false));
		this.listheader_CustEmpID.setSortAscending(new FieldComparator("custEmpID", true));
		this.listheader_CustEmpID.setSortDescending(new FieldComparator("custEmpID", false));

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
		this.searchObj = new JdbcSearchObject<CustomerEmploymentDetail>(CustomerEmploymentDetail.class,getListRows());
		this.searchObj.addSort("CustID", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));

		this.searchObj.addTabelName("CustomerEmpDetails_View");

		// Work flow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(true);
			} else {
				button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(false);
			this.button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog.setVisible(false);
			this.button_CustomerEmploymentDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			getPagedListWrapper().init(this.searchObj,this.listBoxCustomerEmploymentDetail,	this.pagingCustomerEmploymentDetailList);
			this.listBoxCustomerEmploymentDetail.setItemRenderer(new CustomerEmploymentDetailListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().alocateAuthorities("CustomerEmploymentDetailList");

		this.button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail"));
		this.button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEmploymentDetailList_CustomerEmploymentDetailFindDialog"));
		this.button_CustomerEmploymentDetailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEmploymentDetailList_PrintList"));

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerEmploymentDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerEmploymentDetail object
		final Listitem item = this.listBoxCustomerEmploymentDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEmploymentDetail aCustomerEmploymentDetail = (CustomerEmploymentDetail) 
								item.getAttribute("data");
			final CustomerEmploymentDetail customerEmploymentDetail = getCustomerEmploymentDetailService()
							.getCustomerEmploymentDetailById(aCustomerEmploymentDetail.getId());

			if (customerEmploymentDetail == null) {

				String[] valueParm = new String[1];
				String[] errParm = new String[1];

				valueParm[0] = String.valueOf(aCustomerEmploymentDetail.getCustID());
				errParm[0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond =  " AND CustID='"+ customerEmploymentDetail.getCustID()+
									"' AND version=" + customerEmploymentDetail.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),"CustomerEmploymentDetail",
							whereCond, customerEmploymentDetail.getTaskId(),customerEmploymentDetail.getNextTaskId());
					if (userAcces){
						showDetailView(customerEmploymentDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerEmploymentDetail);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerEmploymentDetail dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerEmploymentDetail object, We GET it from the back end.
		final CustomerEmploymentDetail aCustomerEmploymentDetail = getCustomerEmploymentDetailService().getNewCustomerEmploymentDetail();
		showDetailView(aCustomerEmploymentDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerEmploymentDetail (aCustomerEmploymentDetail)
	 * @throws Exception
	 */
	private void showDetailView(CustomerEmploymentDetail aCustomerEmploymentDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aCustomerEmploymentDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerEmploymentDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEmploymentDetail", aCustomerEmploymentDetail);
		map.put("customerEmploymentDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_CustomerEmploymentDetailList);
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
		this.pagingCustomerEmploymentDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerEmploymentDetailList, event);
		this.window_CustomerEmploymentDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the CustomerEmploymentDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerEmploymentDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerEmploymentDetail. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerEmploymentDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEmploymentDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerEmploymentDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerEmploymentDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CustomerEmploymentDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}
	public CustomerEmploymentDetailService getCustomerEmploymentDetailService() {
		return this.customerEmploymentDetailService;
	}

	public JdbcSearchObject<CustomerEmploymentDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerEmploymentDetail> searchObj) {
		this.searchObj = searchObj;
	}

}