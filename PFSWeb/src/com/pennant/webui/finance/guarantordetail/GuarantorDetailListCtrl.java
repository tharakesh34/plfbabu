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
 * FileName    		:  GuarantorDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.guarantordetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.finance.guarantordetail.model.GuarantorDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GuarantorDetailListCtrl extends GFCBaseListCtrl<GuarantorDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(GuarantorDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_GuarantorDetailList; // autowired
	protected Borderlayout borderLayout_GuarantorDetailList; // autowired
	protected Paging pagingGuarantorDetailList; // autowired
	protected Listbox listBoxGuarantorDetail; // autowired

	// List headers
	protected Listheader listheader_BankCustomer; // autowired
	protected Listheader listheader_GuarantorCIF; // autowired
	protected Listheader listheader_GuarantorIDType; // autowired
	protected Listheader listheader_GuarantorIDNumber; // autowired
	protected Listheader listheader_Name; // autowired
	protected Listheader listheader_GuranteePercentage; // autowired
	protected Listheader listheader_MobileNo; // autowired
	protected Listheader listheader_EmailId; // autowired
	protected Listheader listheader_GuarantorProof; // autowired
	protected Listheader listheader_GuarantorProofName; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_GuarantorDetailList_NewGuarantorDetail; // autowired
	protected Button button_GuarantorDetailList_GuarantorDetailSearch; // autowired
	protected Button button_GuarantorDetailList_PrintList; // autowired
	protected Label  label_GuarantorDetailList_RecordStatus; 							// autoWired
	protected Label  label_GuarantorDetailList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<GuarantorDetail> searchObj;
	
	private transient GuarantorDetailService guarantorDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired
	protected Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;
	
	private transient boolean  approvedList=false;
	
	/**
	 * default constructor.<br>
	 */
	public GuarantorDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_GuarantorDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("GuarantorDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GuarantorDetail");
			
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
		
		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(1);
			this.recordType.setSelectedIndex(0);

		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_GuarantorDetailList_RecordStatus.setVisible(false);
			this.label_GuarantorDetailList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_GuarantorDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxGuarantorDetail.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingGuarantorDetailList.setPageSize(getListRows());
		this.pagingGuarantorDetailList.setDetailed(true);

		this.listheader_BankCustomer.setSortAscending(new FieldComparator("bankCustomer", true));
		this.listheader_BankCustomer.setSortDescending(new FieldComparator("bankCustomer", false));
		this.listheader_GuarantorCIF.setSortAscending(new FieldComparator("guarantorCIF", true));
		this.listheader_GuarantorCIF.setSortDescending(new FieldComparator("guarantorCIF", false));
		this.listheader_GuarantorIDType.setSortAscending(new FieldComparator("guarantorIDType", true));
		this.listheader_GuarantorIDType.setSortDescending(new FieldComparator("guarantorIDType", false));
		this.listheader_GuarantorIDNumber.setSortAscending(new FieldComparator("guarantorIDNumber", true));
		this.listheader_GuarantorIDNumber.setSortDescending(new FieldComparator("guarantorIDNumber", false));
		this.listheader_Name.setSortAscending(new FieldComparator("name", true));
		this.listheader_Name.setSortDescending(new FieldComparator("name", false));
		this.listheader_GuranteePercentage.setSortAscending(new FieldComparator("guranteePercentage", true));
		this.listheader_GuranteePercentage.setSortDescending(new FieldComparator("guranteePercentage", false));
		this.listheader_MobileNo.setSortAscending(new FieldComparator("mobileNo", true));
		this.listheader_MobileNo.setSortDescending(new FieldComparator("mobileNo", false));
		this.listheader_EmailId.setSortAscending(new FieldComparator("emailId", true));
		this.listheader_EmailId.setSortDescending(new FieldComparator("emailId", false));
		this.listheader_GuarantorProof.setSortAscending(new FieldComparator("guarantorProof", true));
		this.listheader_GuarantorProof.setSortDescending(new FieldComparator("guarantorProof", false));
		this.listheader_GuarantorProofName.setSortAscending(new FieldComparator("guarantorProofName", true));
		this.listheader_GuarantorProofName.setSortDescending(new FieldComparator("guarantorProofName", false));
		// set the itemRenderer
		this.listBoxGuarantorDetail.setItemRenderer(new GuarantorDetailListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_GuarantorDetailList_NewGuarantorDetail.setVisible(false);
			this.button_GuarantorDetailList_GuarantorDetailSearch.setVisible(false);
			this.button_GuarantorDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		}else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.guarantordetail.model.GuarantorDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onGuarantorDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected GuarantorDetail object
		final Listitem item = this.listBoxGuarantorDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final GuarantorDetail aGuarantorDetail = (GuarantorDetail) item.getAttribute("data");
			GuarantorDetail guarantorDetail = null;
			if(approvedList){
				guarantorDetail = getGuarantorDetailService().getApprovedGuarantorDetailById(aGuarantorDetail.getId());
			}else{
				guarantorDetail = getGuarantorDetailService().getGuarantorDetailById(aGuarantorDetail.getId());
			}
			
			if(guarantorDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aGuarantorDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_GuarantorId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(guarantorDetail.getWorkflowId()==0){
						guarantorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(guarantorDetail.getWorkflowId(), guarantorDetail.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					
					boolean userAcces =  validateUserAccess("GuarantorDetail", new String[] {"GuarantorId"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), guarantorDetail);
					if (userAcces){
						showDetailView(guarantorDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(guarantorDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the GuarantorDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_GuarantorDetailList_NewGuarantorDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new GuarantorDetail object, We GET it from the backend.
		final GuarantorDetail aGuarantorDetail = getGuarantorDetailService().getNewGuarantorDetail();
		showDetailView(aGuarantorDetail);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_GuarantorDetailList_GuarantorDetailSearch(Event event) throws Exception {
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
	/*		this.pagingGuarantorDetailList.setActivePage(0);
			Events.postEvent("onCreate", this.window_GuarantorDetailList, event);
			this.window_GuarantorDetailList.invalidate();
	*/		


		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
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
		PTMessageUtils.showHelpWindow(event, window_GuarantorDetailList);
		logger.debug("Leaving");
	}

	/**
	 * When the guarantorDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_GuarantorDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("GuarantorDetail", getSearchObj(),this.pagingGuarantorDetailList.getTotalSize()+1);
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
		getUserWorkspace().alocateAuthorities("GuarantorDetailList");
		
		if(moduleType==null){
			this.button_GuarantorDetailList_NewGuarantorDetail.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailList_NewGuarantorDetail"));
		}else{
			this.button_GuarantorDetailList_NewGuarantorDetail.setVisible(false);
		}	
		this.button_GuarantorDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param GuarantorDetail (aGuarantorDetail)
	 * @throws Exception
	 */
	private void showDetailView(GuarantorDetail aGuarantorDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("guarantorDetail", aGuarantorDetail);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the GuarantorDetailListbox from the
		 * dialog when we do a delete, edit or insert a GuarantorDetail.
		 */
		map.put("guarantorDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<GuarantorDetail>(GuarantorDetail.class,getListRows());
		this.searchObj.addSort("GuarantorId", false);
		this.searchObj.addTabelName("FinGuarantorsDetails_View");
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("bankCustomer");
		this.searchObj.addField("guarantorCIF");
		this.searchObj.addField("guarantorIDType");
		this.searchObj.addField("guarantorIDNumber");
		this.searchObj.addField("name");
		this.searchObj.addField("guranteePercentage");
		this.searchObj.addField("mobileNo");
		this.searchObj.addField("emailId");
		this.searchObj.addField("guarantorProof");
		this.searchObj.addField("guarantorProofName");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_GuarantorDetailList_NewGuarantorDetail.setVisible(true);
			} else {
				button_GuarantorDetailList_NewGuarantorDetail.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("FinGuarantorsDetails_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("FinGuarantorsDetails_AView");
		}
		
	
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}
		
		// Record Type
		if (this.recordType.getSelectedItem()!=null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
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

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxGuarantorDetail,this.pagingGuarantorDetailList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public GuarantorDetailService getGuarantorDetailService() {
		return this.guarantorDetailService;
	}

	public JdbcSearchObject<GuarantorDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<GuarantorDetail> searchObj) {
		this.searchObj = searchObj;
	}
}