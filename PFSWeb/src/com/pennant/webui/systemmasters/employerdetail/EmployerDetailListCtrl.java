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
 * FileName    		:  EmployerDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.employerdetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
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
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.systemmasters.EmployerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.systemmasters.employerdetail.model.EmployerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class EmployerDetailListCtrl extends GFCBaseListCtrl<EmployerDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(EmployerDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_EmployerDetailList; // autowired
	protected Borderlayout borderLayout_EmployerDetailList; // autowired
	protected Paging pagingEmployerDetailList; // autowired
	protected Listbox listBoxEmployerDetail; // autowired

	// List headers
	protected Listheader listheader_EmployerId; // autowired
	protected Listheader listheader_EmpIndustry; // autowired
	protected Listheader listheader_EmpName; // autowired
	protected Listheader listheader_EstablishDate; // autowired
	protected Listheader listheader_EmpPOBox; // autowired
	protected Listheader listheader_EmpCity; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_EmployerDetailList_NewEmployerDetail; // autowired
	protected Button button_EmployerDetailList_EmployerDetailSearch; // autowired
	protected Button button_EmployerDetailList_PrintList; // autowired
	protected Label  label_EmployerDetailList_RecordStatus; 							// autoWired
	protected Label  label_EmployerDetailList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<EmployerDetail> searchObj;
	
	private transient EmployerDetailService employerDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
	protected Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;
	
	private transient boolean  approvedList=false;
	
	/**
	 * default constructor.<br>
	 */
	public EmployerDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_EmployerDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("EmployerDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EmployerDetail");
			
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
		
		this.borderLayout_EmployerDetailList.setHeight(getBorderLayoutHeight());
		//this.listBoxEmployerDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount())); 
		
		// set the paging parameters
		this.pagingEmployerDetailList.setPageSize(getListRows());
		this.pagingEmployerDetailList.setDetailed(true);

		this.listheader_EmployerId.setSortAscending(new FieldComparator("employerId", true));
		this.listheader_EmployerId.setSortDescending(new FieldComparator("employerId", false));
		this.listheader_EmpIndustry.setSortAscending(new FieldComparator("empIndustry", true));
		this.listheader_EmpIndustry.setSortDescending(new FieldComparator("empIndustry", false));
		this.listheader_EmpName.setSortAscending(new FieldComparator("empName", true));
		this.listheader_EmpName.setSortDescending(new FieldComparator("empName", false));
		this.listheader_EstablishDate.setSortAscending(new FieldComparator("establishDate", true));
		this.listheader_EstablishDate.setSortDescending(new FieldComparator("establishDate", false));
		this.listheader_EmpPOBox.setSortAscending(new FieldComparator("empPOBox", true));
		this.listheader_EmpPOBox.setSortDescending(new FieldComparator("empPOBox", false));
		this.listheader_EmpCity.setSortAscending(new FieldComparator("empCity", true));
		this.listheader_EmpCity.setSortDescending(new FieldComparator("empCity", false));
		
		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// set the itemRenderer
		this.listBoxEmployerDetail.setItemRenderer(new EmployerDetailListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_EmployerDetailList_NewEmployerDetail.setVisible(false);
			this.button_EmployerDetailList_EmployerDetailSearch.setVisible(false);
			this.button_EmployerDetailList_PrintList.setVisible(false);
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
	 * see: com.pennant.webui.systemmaster.employerdetail.model.EmployerDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onEmployerDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected EmployerDetail object
		final Listitem item = this.listBoxEmployerDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final EmployerDetail aEmployerDetail = (EmployerDetail) item.getAttribute("data");
			EmployerDetail employerDetail = null;
			if(approvedList){
				employerDetail = getEmployerDetailService().getApprovedEmployerDetailById(aEmployerDetail.getId());
			}else{
				employerDetail = getEmployerDetailService().getEmployerDetailById(aEmployerDetail.getId());
			}
			
			if(employerDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aEmployerDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_EmployerId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(employerDetail.getWorkflowId()==0){
						employerDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(employerDetail.getWorkflowId(), employerDetail.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					
					boolean userAcces =  validateUserAccess("EmployerDetail", new String[] {"EmployerId"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), employerDetail);
					if (userAcces){
						showDetailView(employerDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(employerDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the EmployerDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_EmployerDetailList_NewEmployerDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new EmployerDetail object, We GET it from the backend.
		final EmployerDetail aEmployerDetail = getEmployerDetailService().getNewEmployerDetail();
		showDetailView(aEmployerDetail);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_EmployerDetailList_EmployerDetailSearch(Event event) throws Exception {
		
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our EmploymentTypeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected EmploymentType. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * EmploymentType object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("EmployerDetailListCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailSearch.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
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
		logger.debug(event.toString());
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
		PTMessageUtils.showHelpWindow(event, window_EmployerDetailList);
		logger.debug("Leaving");
	}

	/**
	 * When the employerDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_EmployerDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		PTListReportUtils reportUtils = new PTListReportUtils("EmployerDetail", getSearchObj(),this.pagingEmployerDetailList.getTotalSize()+1);
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
		getUserWorkspace().alocateAuthorities("EmployerDetailList");
		
		if(moduleType==null){
			this.button_EmployerDetailList_NewEmployerDetail.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailList_NewEmployerDetail"));
		}else{
			this.button_EmployerDetailList_NewEmployerDetail.setVisible(false);
		}	
		this.button_EmployerDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param EmployerDetail (aEmployerDetail)
	 * @throws Exception
	 */
	private void showDetailView(EmployerDetail aEmployerDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("employerDetail", aEmployerDetail);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the EmployerDetailListbox from the
		 * dialog when we do a delete, edit or insert a EmployerDetail.
		 */
		map.put("employerDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<EmployerDetail>(EmployerDetail.class,getListRows());
		this.searchObj.addSort("EmployerId", false);
		this.searchObj.addTabelName("EmployerDetail_View");
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("EmployerId");
		this.searchObj.addField("empIndustry");
		this.searchObj.addField("empName");
		this.searchObj.addField("establishDate");
		this.searchObj.addField("empFlatNbr");
		this.searchObj.addField("empPOBox");
		this.searchObj.addField("empCountry");
		this.searchObj.addField("empProvince");
		this.searchObj.addField("empCity");
		this.searchObj.addField("empAlocationType");
		this.searchObj.addField("lovDescIndustryDesc");
		this.searchObj.addField("lovDescCityName");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_EmployerDetailList_NewEmployerDetail.setVisible(true);
			} else {
				button_EmployerDetailList_NewEmployerDetail.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("EmployerDetail_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("EmployerDetail_AView");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxEmployerDetail,this.pagingEmployerDetailList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setEmployerDetailService(EmployerDetailService employerDetailService) {
		this.employerDetailService = employerDetailService;
	}

	public EmployerDetailService getEmployerDetailService() {
		return this.employerDetailService;
	}

	public JdbcSearchObject<EmployerDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<EmployerDetail> searchObj) {
		this.searchObj = searchObj;
	}
}