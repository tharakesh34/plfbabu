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
 * FileName    		:  PoolExecutionDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-08-2013    														*
 *                                                                  						*
 * Modified Date    :  01-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
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
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.finance.enquiry.model.FinApprovalStsInquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	 
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/PoolExecution/PoolExecutionDetail/PoolExecutionDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinApprovalStsInquiryListCtrl extends GFCBaseListCtrl<CustomerFinanceDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinApprovalStsInquiryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinApprovalStsInquiryList; // autowired
	protected Borderlayout borderLayout_FinApprovalStsInquiryList; // autowired
	protected Paging pagingFinApprovalStsInquiryList; // autowired
	protected Listbox listBoxCustFinanceDetail; // autowired

	// List headers
	protected Listheader listheader_FinApprovalStsInquiryList_CustCIF; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_CustShrtName; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_FinReference; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_FinType; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_CustDocTitle; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_MobileNO; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_EmailID; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_CurrentRole; // autowired
	protected Listheader listheader_FinApprovalStsInquiryList_PreviousRole;

	// checkRights
	protected Button btnHelp; // autowired
 	protected Button button_FinApprovalStsInquiryList_Search; // autowired
	protected Button button_FinApprovalStsInquiryList_PrintList; // autowired
 	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerFinanceDetail> searchObj;
	
	private transient WorkFlowDetails workFlowDetails=null;
    protected Textbox moduleType; 						// autowired
	
	protected Textbox finReference; // autowired
	protected Listbox sortOperator_finReference; // autowired
	
	protected Radio fromApproved; // autowired
	protected Radio fromWorkFlow; // autowired

	protected Textbox custCIF; // autowired
	protected Listbox sortOperator_custCIF; // autowired
 
	protected Textbox custShrtName; // autowired
	protected Listbox sortOperator_cusShrtName; // autowired
	
	protected Textbox finType; // autowired
	protected Listbox sortOperator_finType; // autowired
	
	protected Textbox custID; // autowired
	protected Listbox sortOperator_custID; // autowired
	
	protected Textbox mobileNo; // autowired
	protected Listbox sortOperator_mobileNo; // autowired
	
	protected Textbox emailID; // autowired
	protected Listbox sortOperator_emailID; // autowired
	
	protected Grid searchGrid;
	
    protected Row	workFlowFrom;
	
	private transient boolean  approvedList=false;
	
	
	private ApprovalStatusEnquiryService approvalStatusEnquiryService; 	
	boolean facility=false;
	
	private Label      label_FinApprovalStsInquiryList_FinReference;
	
	/**
	 * default constructor.<br>
	 */
	public FinApprovalStsInquiryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_FinApprovalStsInquiryList(Event event) throws Exception {
		logger.debug("Entering");
/*		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		String currentTab = tabbox.getSelectedTab().getId().toString();*/
		if (getCurrentTab().equals("tab_FacilityApprovalStsInquiry")) {
			facility=true;
		}else{
			facility=false;
		}
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceMain");
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceMain");
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}
	// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_cusShrtName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_cusShrtName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mobileNo.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mobileNo.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_emailID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_emailID.setItemRenderer(new SearchOperatorListModelItemRenderer());
		 		
		if (isWorkFlowEnabled()){
 		}else{
			this.fromApproved.setVisible(false);
			this.fromWorkFlow.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_FinApprovalStsInquiryList.setHeight(getBorderLayoutHeight());
		this.listBoxCustFinanceDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount())); 
		
		// set the paging parameters
		
		this.pagingFinApprovalStsInquiryList.setPageSize(getListRows());
		this.pagingFinApprovalStsInquiryList.setDetailed(true);
 
		this.listheader_FinApprovalStsInquiryList_CustCIF.setSortAscending(new FieldComparator("custCIF", true));
		this.listheader_FinApprovalStsInquiryList_CustCIF.setSortDescending(new FieldComparator("custCIF", false));
		this.listheader_FinApprovalStsInquiryList_CustShrtName.setSortAscending(new FieldComparator("custShrtName", true));
		this.listheader_FinApprovalStsInquiryList_CustShrtName.setSortDescending(new FieldComparator("custShrtName", false));
		this.listheader_FinApprovalStsInquiryList_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinApprovalStsInquiryList_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_FinApprovalStsInquiryList_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinApprovalStsInquiryList_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_FinApprovalStsInquiryList_CustDocTitle.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_FinApprovalStsInquiryList_CustDocTitle.setSortDescending(new FieldComparator("finAmount", false));
		this.listheader_FinApprovalStsInquiryList_MobileNO.setSortAscending(new FieldComparator("finStartDate", true));
		this.listheader_FinApprovalStsInquiryList_MobileNO.setSortDescending(new FieldComparator("finStartDate", false));
		this.listheader_FinApprovalStsInquiryList_EmailID.setSortAscending(new FieldComparator("lastMntByUser", true));
		this.listheader_FinApprovalStsInquiryList_EmailID.setSortDescending(new FieldComparator("lastMntByUser", false));
		this.listheader_FinApprovalStsInquiryList_CurrentRole.setSortAscending(new FieldComparator("NextRoleDesc", true));
		this.listheader_FinApprovalStsInquiryList_CurrentRole.setSortDescending(new FieldComparator("NextRoleDesc", false));
		this.listheader_FinApprovalStsInquiryList_PreviousRole.setSortAscending(new FieldComparator("RoleDesc", true));
		this.listheader_FinApprovalStsInquiryList_PreviousRole.setSortDescending(new FieldComparator("RoleDesc", false));
		
		// set the itemRenderer
		this.listBoxCustFinanceDetail.setItemRenderer(new FinApprovalStsInquiryListModelItemRenderer());
		
		doSearch();
		if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
			this.workFlowFrom.setVisible(false);
			this.fromApproved.setSelected(true);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.poolexecution.poolexecutiondetail.model.PoolExecutionDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onFinApprovalStsInquiryItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected PoolExecutionDetail object
		
		final Listitem item = this.listBoxCustFinanceDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerFinanceDetail aCustomerFinanceDetail = (CustomerFinanceDetail) item.getAttribute("data");
			CustomerFinanceDetail customerFinanceDetail = null;
			//TODO
			customerFinanceDetail = new CustomerFinanceDetail();
			BeanUtils.copyProperties(aCustomerFinanceDetail, customerFinanceDetail);
			if(approvedList){
				if (facility) {
					customerFinanceDetail = getApprovalStatusEnquiryService().getApprovedCustomerFacilityById(aCustomerFinanceDetail.getFinReference());
				}else {
					customerFinanceDetail = getApprovalStatusEnquiryService().getApprovedCustomerFinanceById(aCustomerFinanceDetail.getFinReference());
				}				
			}else{	
				if (facility) {
					customerFinanceDetail = getApprovalStatusEnquiryService().getCustomerFacilityById(aCustomerFinanceDetail.getFinReference());
				}else {
					customerFinanceDetail = getApprovalStatusEnquiryService().getCustomerFinanceById(aCustomerFinanceDetail.getFinReference());
				}	
			}
			//getJobControlId()
			if(customerFinanceDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCustomerFinanceDetail.getFinReference();
				errParm[0]=PennantJavaUtil.getLabel("label_Finance")+":"+valueParm[0];
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(customerFinanceDetail.getWorkflowId()==0){
						customerFinanceDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}
					WorkflowLoad flowLoad= new WorkflowLoad(customerFinanceDetail.getWorkflowId(), customerFinanceDetail.getNextTaskId(),
							getUserWorkspace().getUserRoleSet());
					boolean userAcces =  validateUserAccess("FinApprovalStsInquiry", new String[] {"FinReference"}, flowLoad.getRole(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), customerFinanceDetail);
					
					if (userAcces){
						showDetailView(customerFinanceDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerFinanceDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_FinApprovalStsInquiryList_Search(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doSearch();
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
		logger.debug(event.toString());
 	
  		this.sortOperator_custCIF.setSelectedIndex(0);
	  	this.custCIF.setValue("");
  		this.sortOperator_cusShrtName.setSelectedIndex(0);
	  	this.custShrtName.setValue("");
  		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
  		this.sortOperator_custID.setSelectedIndex(0);
		this.custID.setValue("");
  		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_mobileNo.setSelectedIndex(0);
		this.mobileNo.setValue("");
		this.sortOperator_emailID.setSelectedIndex(0);
		this.emailID.setValue("");

		if (isWorkFlowEnabled()){
			this.fromWorkFlow.setSelected(true);
		}
		doSearch();

		logger.debug("Leaving");
	}

	/*
	 * Invoke Search 
	 */
	
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinApprovalStsInquiryList);
		logger.debug("Leaving");
	}

	/**
	 * When the poolExecutionDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinApprovalStsInquiryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("FinanceMain", getSearchObj(),this.pagingFinApprovalStsInquiryList.getTotalSize()+1);
		logger.debug("Leaving");
	}
	
	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		//TODO
		/*getUserWorkspace().alocateAuthorities("PoolExecutionDetailList");
		this.button_FinApprovalStsInquiryList_PrintList.setVisible(getUserWorkspace().isAllowed("button_PoolExecutionDetailList_PrintList"));*/
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param PoolExecutionDetail (aPoolExecutionDetail)
	 * @throws Exception
	 */
	private void showDetailView(CustomerFinanceDetail aCustomerFinanceDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerFinanceDetail", aCustomerFinanceDetail);

		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the PoolExecutionDetailListbox from the
		 * dialog when we do a delete, edit or insert a PoolExecutionDetail.
		 */
		map.put("FinApprovalStsInquiryListCtrl", this);
		map.put("approvedList", this.approvedList);
		map.put("facility", this.facility);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceEnquiry/FinApprovalStsInquiry/FinApprovalStsInquiryDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	
	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerFinanceDetail>(CustomerFinanceDetail.class,getListRows());
		
		this.searchObj.addSort("FinReference", false);
 		
		this.searchObj.addField("FinReference");
		this.searchObj.addField("FinBranch");
		this.searchObj.addField("CUSTID");
		this.searchObj.addField("CustCIF");
		this.searchObj.addField("CustShrtName");
		this.searchObj.addField("RoleCode");
		this.searchObj.addField("RoleDesc");
		this.searchObj.addField("NextRoleCode");
		this.searchObj.addField("NextRoleDesc");
		this.searchObj.addField("RecordType");
		this.searchObj.addField("DeptDesc");
		this.searchObj.addField("FinType");
		this.searchObj.addField("FinAmount");
		this.searchObj.addField("FinStartDate");
		this.searchObj.addField("ccyFormat");
		this.searchObj.addField("lastMntByUser");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("FinTypeDesc");
		
 		// Workflow
		
		if (isWorkFlowEnabled()) {
			if(isFirstTask() && moduleType == null){
				this.searchObj.addFilterEqual("Approved", '0');
			}
				
			if(this.fromApproved.isSelected()){
				approvedList=true;
			}else{
				if (facility) {
					this.searchObj.addTabelName("CustomerFacilityDetails_View");
				}else{
					this.searchObj.addTabelName("CustomerFinanceDetails_View");
				}
				approvedList=false;
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			if (facility) {
				this.searchObj.addTabelName("CustomerFacilityDetails_AView");
			}else{
				this.searchObj.addTabelName("CustomerFinanceDetails_AView");
			}
		}
		
	// Finance Reference
		if (!StringUtils.trimToEmpty(this.finReference.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finReference.getSelectedItem(), this.finReference.getValue(), "FinReference");
		}
  
	// Customer CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custCIF.getSelectedItem(), this.custCIF.getValue(), "CustCIF");
		}
	// Customer Short Name
		if (!StringUtils.trimToEmpty(this.custShrtName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_cusShrtName.getSelectedItem(), this.custShrtName.getValue(), "CustShrtName");
		}
	// Customer Document Type
		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finType.getSelectedItem(), this.finType.getValue(), "FinType");
		}
	// Customer ID
		if (!StringUtils.trimToEmpty(this.custID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custID.getSelectedItem(), this.custID.getValue(), "CustID");
		}
	// Mobile Number
		if (!StringUtils.trimToEmpty(this.mobileNo.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_mobileNo.getSelectedItem(), this.mobileNo.getValue(), "MobileNo");
		}
	// Email ID	
		if (!StringUtils.trimToEmpty(this.emailID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_emailID.getSelectedItem(), this.emailID.getValue(), "EmailID");
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
	 	
	 	doDesignByMode();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxCustFinanceDetail,this.pagingFinApprovalStsInquiryList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public JdbcSearchObject<CustomerFinanceDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CustomerFinanceDetail> searchObj) {
		this.searchObj = searchObj;
	}

	public ApprovalStatusEnquiryService getApprovalStatusEnquiryService() {
		return approvalStatusEnquiryService;
	}

	public void setApprovalStatusEnquiryService(ApprovalStatusEnquiryService approvalStatusEnquiryService) {
		this.approvalStatusEnquiryService = approvalStatusEnquiryService;
	}
	private void doDesignByMode(){
		if (facility) {
			this.label_FinApprovalStsInquiryList_FinReference.setValue(Labels.getLabel("label_FacilityApprovalStsInquiryList_CAFReference.value"));
			this.listheader_FinApprovalStsInquiryList_FinReference.setLabel(Labels.getLabel("label_FacilityApprovalStsInquiryList_CAFReference.value"));
			this.listheader_FinApprovalStsInquiryList_CustDocTitle.setVisible(false);
			this.listheader_FinApprovalStsInquiryList_FinType.setLabel(Labels.getLabel("label_FacilityApprovalStsInquiryList_FacilityType.value"));
		}
	}
 
}