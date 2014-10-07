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
 * FileName    		:  CustomerBalanceSheetListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerbalancesheet;

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
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerBalanceSheetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.customerbalancesheet.model.CustomerBalanceSheetComparator;
import com.pennant.webui.customermasters.customerbalancesheet.model.CustomerBalanceSheetListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerBalanceSheet
 * /CustomerBalanceSheetList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerBalanceSheetListCtrl extends GFCBaseListCtrl<CustomerBalanceSheet> 
			implements Serializable {

	private static final long serialVersionUID = 7572807238518910341L;
	private final static Logger logger = Logger.getLogger(CustomerBalanceSheetListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerBalanceSheetList; 		// autowired
	protected Borderlayout 	borderLayout_CustomerBalanceSheetList; 	// autowired
	protected Paging 		pagingCustomerBalanceSheetList; 		// autowired
	protected Listbox 		listBoxCustomerBalanceSheet; 			// autowired

	// List headers
	protected Listheader listheader_FinancialYear; 		// autowired
	protected Listheader listheader_TotalAssets; 		// autowired
	protected Listheader listheader_TotalLiabilities; 	// autowired
	protected Listheader listheader_NetProfit; 			// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															// autowired
	protected Button button_CustomerBalanceSheetList_NewCustomerBalanceSheet; 			// autowired
	protected Button button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog; 	// autowired
	protected Button button_CustomerBalanceSheetList_PrintList; 						// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerBalanceSheet> searchObj;
	private transient PagedListService pagedListService;
	private transient CustomerBalanceSheetService customerBalanceSheetService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerBalanceSheetListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerBalanceSheet object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBalanceSheetList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerBalanceSheet");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerBalanceSheet");
			
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
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerBalanceSheetList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerBalanceSheetList.setPageSize(getListRows());
		this.pagingCustomerBalanceSheetList.setDetailed(true);

		this.listheader_FinancialYear.setSortAscending(new FieldComparator("financialYear", true));
		this.listheader_FinancialYear.setSortDescending(new FieldComparator("financialYear", false));
		this.listheader_TotalAssets.setSortAscending(new FieldComparator("totalAssets", true));
		this.listheader_TotalAssets.setSortDescending(new FieldComparator("totalAssets", false));
		this.listheader_TotalLiabilities.setSortAscending(new FieldComparator("totalLiabilities", true));
		this.listheader_TotalLiabilities.setSortDescending(new FieldComparator("totalLiabilities", false));
		this.listheader_NetProfit.setSortAscending(new FieldComparator("netProfit", true));
		this.listheader_NetProfit.setSortDescending(new FieldComparator("netProfit", false));
		
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
		this.searchObj = new JdbcSearchObject<CustomerBalanceSheet>(CustomerBalanceSheet.class,
				getListRows());
		this.searchObj.addSort("CustId", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("CustomerBalanceSheet_View");
			if (isFirstTask()) {
				button_CustomerBalanceSheetList_NewCustomerBalanceSheet.setVisible(true);
			} else {
				button_CustomerBalanceSheetList_NewCustomerBalanceSheet.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("CustomerBalanceSheet_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerBalanceSheetList_NewCustomerBalanceSheet.setVisible(false);
			this.button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog.setVisible(false);
			this.button_CustomerBalanceSheetList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxCustomerBalanceSheet.setItemRenderer(new CustomerBalanceSheetListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject(){
		logger.debug("Entering");
		final SearchResult<CustomerBalanceSheet> searchResult = getPagedListService().getSRBySearchObject(
				this.searchObj);
		listBoxCustomerBalanceSheet.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new CustomerBalanceSheetComparator()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerBalanceSheetList");

		this.button_CustomerBalanceSheetList_NewCustomerBalanceSheet.setVisible(getUserWorkspace().
				isAllowed("button_CustomerBalanceSheetList_NewCustomerBalanceSheet"));
		this.button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_CustomerBalanceSheetList_CustomerBalanceSheetFindDialog"));
		this.button_CustomerBalanceSheetList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_CustomerBalanceSheetList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customerbalancesheet.model.
	 * CustomerBalanceSheetListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerBalanceSheetItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerBalanceSheet object
		final Listitem item = this.listBoxCustomerBalanceSheet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerBalanceSheet aCustomerBalanceSheet = (CustomerBalanceSheet) item.getAttribute(
					"data");
			final CustomerBalanceSheet customerBalanceSheet = getCustomerBalanceSheetService().
			getCustomerBalanceSheetById(aCustomerBalanceSheet.getId(),aCustomerBalanceSheet.getCustId());
			
			if(customerBalanceSheet==null){
				String[] errParm= new String[2];
				String[] valueParm= new String[2];
				valueParm[0]=aCustomerBalanceSheet.getId();
				valueParm[0]=String.valueOf(aCustomerBalanceSheet.getCustId());
				errParm[0]=PennantJavaUtil.getLabel("label_FinancialYear")+":"+valueParm[0];
				errParm[0]=PennantJavaUtil.getLabel("label_CustId")+":"+valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", 
								errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinancialYear='"+ customerBalanceSheet.getId()+"'" +
					 " AND CustId="+ customerBalanceSheet.getCustId()+
					 " AND version=" + customerBalanceSheet.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), 
							"CustomerBalanceSheet", whereCond, customerBalanceSheet.getTaskId(), 
							customerBalanceSheet.getNextTaskId());
					if (userAcces){
						showDetailView(customerBalanceSheet);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerBalanceSheet);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerBalanceSheet dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerBalanceSheetList_NewCustomerBalanceSheet(Event event) 
									throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerBalanceSheet object, We GET it from the backEnd.
		final CustomerBalanceSheet aCustomerBalanceSheet = getCustomerBalanceSheetService().
										getNewCustomerBalanceSheet();
		showDetailView(aCustomerBalanceSheet);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerBalanceSheet (aCustomerBalanceSheet)
	 * @throws Exception
	 */
	private void showDetailView(CustomerBalanceSheet aCustomerBalanceSheet) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCustomerBalanceSheet.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerBalanceSheet.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerBalanceSheet", aCustomerBalanceSheet);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CustomerBalanceSheetListbox from the
		 * dialog when we do a delete, edit or insert a CustomerBalanceSheet.
		 */
		map.put("customerBalanceSheetListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/CustomerBalanceSheetDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_CustomerBalanceSheetList);
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
		this.pagingCustomerBalanceSheetList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerBalanceSheetList, event);
		this.window_CustomerBalanceSheetList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the CustomerBalanceSheet dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog(Event event) 
			throws Exception {
		
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CustomerBalanceSheetDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CustomerBalanceSheet. For handed over
		 * these parameter only a Map is accepted. So we put the CustomerBalanceSheet object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerBalanceSheetCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/CustomerBalanceSheetSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerBalanceSheet print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerBalanceSheetList_PrintList(Event event) 
			throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("CustomerBalanceSheet", getSearchObj(),this.pagingCustomerBalanceSheetList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerBalanceSheetService(CustomerBalanceSheetService customerBalanceSheetService) {
		this.customerBalanceSheetService = customerBalanceSheetService;
	}
	public CustomerBalanceSheetService getCustomerBalanceSheetService() {
		return this.customerBalanceSheetService;
	}

	public JdbcSearchObject<CustomerBalanceSheet> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerBalanceSheet> searchObj) {
		this.searchObj = searchObj;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}
}