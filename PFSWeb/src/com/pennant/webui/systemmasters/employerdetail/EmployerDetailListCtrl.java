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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
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
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.systemmasters.employerdetail.model.EmployerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

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
	
	protected Intbox empID; // autowired
	protected Listbox sortOperator_EmpID; // autowired/
	
	protected Textbox empIndustry; // autowired
	protected Listbox sortOperator_EmpIndustry; // autowired/

	protected Textbox empName; // autowired
	protected Listbox sortOperator_EmpName; // autowired


	protected Textbox empPOBox; // autowired
	protected Listbox sortOperator_EmpPOBox; // autowired


	protected Textbox empCity; // autowired
	protected Listbox sortOperator_EmpCity; // autowired

	protected Combobox empAlocationType; // autowired
	protected Listbox sortOperator_EmpAlocationType; // autowired

	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired

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
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<EmployerDetail> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid 			searchGrid;	
	
	private transient EmployerDetailService employerDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

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
		
		this.sortOperator_EmpID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_EmpID.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_EmpIndustry.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpIndustry.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_EmpName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpName.setItemRenderer(new SearchOperatorListModelItemRenderer());


		this.sortOperator_EmpPOBox.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_EmpCity.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpCity.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_EmpAlocationType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		fillComboBox(this.empAlocationType,"",PennantStaticListUtil.getEmpAlocList(),"");
		this.sortOperator_EmpAlocationType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);
			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		}else{
			this.row_AlwWorkflow.setVisible(false);
		}


		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_EmployerDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxEmployerDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount())); 
		
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

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<EmployerDetail>(EmployerDetail.class,getListRows());
		this.searchObj.addSort("EmployerId", false);
		this.searchObj.addField("EmployerId");
		this.searchObj.addField("empIndustry");
		this.searchObj.addField("empName");
		this.searchObj.addField("establishDate");
		this.searchObj.addField("empPOBox");
		this.searchObj.addField("empCity");
		this.searchObj.addField("empAlocationType");
		this.searchObj.addField("lovDescIndustryDesc");
		this.searchObj.addField("lovDescCityName");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("EmployerDetail_View");
			if (isFirstTask()) {
				button_EmployerDetailList_NewEmployerDetail.setVisible(true);
			} else {
				button_EmployerDetailList_NewEmployerDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("EmployerDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_EmployerDetailList_NewEmployerDetail.setVisible(false);
			this.button_EmployerDetailList_EmployerDetailSearch.setVisible(false);
			this.button_EmployerDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxEmployerDetail.setItemRenderer(new EmployerDetailListModelItemRenderer());
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
			EmployerDetail employerDetail = getEmployerDetailService().getEmployerDetailById(aEmployerDetail.getId());

			if(employerDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aEmployerDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_EmployerId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){

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
		doSearch();
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
		this.sortOperator_EmpID.setSelectedIndex(0);
		this.empID.setText("");
		this.sortOperator_EmpIndustry.setSelectedIndex(0);
		this.empIndustry.setValue("");
		this.sortOperator_EmpName.setSelectedIndex(0);
		this.empName.setValue("");
		this.sortOperator_EmpPOBox.setSelectedIndex(0);
		this.empPOBox.setValue("");
		this.sortOperator_EmpCity.setSelectedIndex(0);
		this.empCity.setValue("");
		this.sortOperator_EmpAlocationType.setSelectedIndex(0);
		this.empAlocationType.setSelectedIndex(0);
		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears all the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxEmployerDetail,this.pagingEmployerDetailList);

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
	public void onClick$button_EmployerDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		new PTListReportUtils("EmployerDetail", getSearchObj(),this.pagingEmployerDetailList.getTotalSize()+1);
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
		this.button_EmployerDetailList_NewEmployerDetail.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailList_NewEmployerDetail"));
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
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aEmployerDetail.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aEmployerDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("employerDetail", aEmployerDetail);

		/*
		 * we can additi-onally handed over the listBox or the controller self,
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

		this.searchObj.clearFilters();

		if (this.empID.intValue() != 0) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_EmpID.getSelectedItem(),
					this.empID.intValue(), "EmployerId");
		}
		
		if (!StringUtils.trimToEmpty(this.empIndustry.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_EmpIndustry.getSelectedItem(),
					this.empIndustry.getValue(), "EmpIndustry");
		}
		if (!StringUtils.trimToEmpty(this.empName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_EmpName.getSelectedItem(),
					this.empName.getValue(), "EmpName");
		}

		if (this.empAlocationType.getValue()!= null && !PennantConstants.List_Select.equals(this.empAlocationType.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_EmpAlocationType.getSelectedItem(),
					this.empAlocationType.getSelectedItem().getValue().toString(), "EmpAlocationType");
		}
		if (!StringUtils.trimToEmpty(this.empCity.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_EmpCity.getSelectedItem(),
					this.empCity.getValue(), "EmpCity");
		}
		if (!StringUtils.trimToEmpty(this.empPOBox.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_EmpPOBox.getSelectedItem(),
					this.empPOBox.getValue(), "EmpPOBox");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_RecordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}
		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType
						.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_RecordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),
					"RecordType");
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