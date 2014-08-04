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
 * FileName    		:  FacilityListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-11-2013    														*
 *                                                                  						*
 * Modified Date    :  25-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.facility.facility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.facility.facility.model.FacilityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Facility/Facility/FacilityList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FacilityListCtrl extends GFCBaseListCtrl<Facility> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FacilityListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FacilityList; // autowired
	protected Borderlayout borderLayout_FacilityList; // autowired
	protected Paging pagingFacilityList; // autowired
	protected Listbox listBoxFacility; // autowired

	// List headers
	protected Listheader listheader_CAFReference; // autowired
	protected Listheader listheader_CustID; // autowired
	protected Listheader listheader_StartDate; // autowired
	protected Listheader listheader_PresentingUnit; // autowired
	protected Listheader listheader_CountryOfDomicile; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_FacilityList_NewFacility; // autowired
	protected Button button_FacilityList_FacilitySearch; // autowired
	protected Button button_FacilityList_PrintList; // autowired
	protected Label  label_FacilityList_RecordStatus; 							// autoWired
	protected Label  label_FacilityList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Facility> searchObj;
	
	private transient FacilityService facilityService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
	protected Textbox cAFReference; // autowired
	protected Listbox sortOperator_CAFReference; // autowired

	protected Textbox custID; // autowired
	protected Listbox sortOperator_CustID; // autowired

	protected Datebox startDate; // autowired
	protected Listbox sortOperator_StartDate; // autowired

	protected Textbox presentingUnit; // autowired
	protected Listbox sortOperator_PresentingUnit; // autowired

	protected Textbox countryOfDomicile; // autowired
	protected Listbox sortOperator_CountryOfDomicile; // autowired

	protected Datebox deadLine; // autowired
	protected Listbox sortOperator_DeadLine; // autowired

	protected Textbox countryOfRisk; // autowired
	protected Listbox sortOperator_CountryOfRisk; // autowired

	protected Datebox establishedDate; // autowired
	protected Listbox sortOperator_EstablishedDate; // autowired

	protected Textbox natureOfBusiness; // autowired
	protected Listbox sortOperator_NatureOfBusiness; // autowired

	protected Textbox sICCode; // autowired
	protected Listbox sortOperator_SICCode; // autowired

	protected Textbox countryManager; // autowired
	protected Listbox sortOperator_CountryManager; // autowired

	protected Textbox customerRiskType; // autowired
	protected Listbox sortOperator_CustomerRiskType; // autowired

	protected Textbox relationshipManager; // autowired
	protected Listbox sortOperator_RelationshipManager; // autowired

	protected Textbox customerGroup; // autowired
	protected Listbox sortOperator_CustomerGroup; // autowired

	protected Datebox nextReviewDate; // autowired
	protected Listbox sortOperator_NextReviewDate; // autowired

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
	private Textbox cafType;
	
	/**
	 * default constructor.<br>
	 */
	public FacilityListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_FacilityList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Facility");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Facility");
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
	
		this.sortOperator_CAFReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CAFReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_CustID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CustID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_StartDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_StartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_PresentingUnit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_PresentingUnit.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_CountryOfDomicile.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CountryOfDomicile.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_DeadLine.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_DeadLine.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_CountryOfRisk.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CountryOfRisk.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EstablishedDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_EstablishedDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_NatureOfBusiness.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_NatureOfBusiness.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_SICCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_SICCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_CountryManager.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CountryManager.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_CustomerRiskType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CustomerRiskType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_RelationshipManager.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_RelationshipManager.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_CustomerGroup.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CustomerGroup.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_NextReviewDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_NextReviewDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_FacilityList_RecordStatus.setVisible(false);
			this.label_FacilityList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_FacilityList.setHeight(getBorderLayoutHeight());
		this.listBoxFacility.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount())); 
		
		// set the paging parameters
		this.pagingFacilityList.setPageSize(getListRows());
		this.pagingFacilityList.setDetailed(true);

		this.listheader_CAFReference.setSortAscending(new FieldComparator("cAFReference", true));
		this.listheader_CAFReference.setSortDescending(new FieldComparator("cAFReference", false));
		this.listheader_CustID.setSortAscending(new FieldComparator("CustCIF", true));
		this.listheader_CustID.setSortDescending(new FieldComparator("CustCIF", false));
		this.listheader_StartDate.setSortAscending(new FieldComparator("startDate", true));
		this.listheader_StartDate.setSortDescending(new FieldComparator("startDate", false));
		this.listheader_PresentingUnit.setSortAscending(new FieldComparator("presentingUnit", true));
		this.listheader_PresentingUnit.setSortDescending(new FieldComparator("presentingUnit", false));
		this.listheader_CountryOfDomicile.setSortAscending(new FieldComparator("countryOfDomicile", true));
		this.listheader_CountryOfDomicile.setSortDescending(new FieldComparator("countryOfDomicile", false));
		// set the itemRenderer
		this.listBoxFacility.setItemRenderer(new FacilityListModelItemRenderer());
		
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
			this.button_FacilityList_NewFacility.setVisible(false);
			this.button_FacilityList_FacilitySearch.setVisible(false);
			this.button_FacilityList_PrintList.setVisible(false);
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
	 * see: com.pennant.webui.facility.facility.model.FacilityListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onFacilityItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Facility object
		final Listitem item = this.listBoxFacility.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Facility aFacility = (Facility) item.getAttribute("data");
			Facility facility = null;
			if(approvedList){
				facility = getFacilityService().getApprovedFacilityById(aFacility.getId());				
			}else{
				facility = getFacilityService().getFacilityById(aFacility.getId());
				
			}
			if (facility!=null) {
				facility.setUserRole(getRole());
				facility=getFacilityService().setFacilityScoringDetails(facility);
				facility.setCustomerEligibilityCheck(getFacilityService().getCustomerEligibility(null,facility.getCustID()));
			}
			
			if(facility==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFacility.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_CAFReference")+":"+valueParm[0];
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				
			
				if(isWorkFlowEnabled() && moduleType==null){

					if(facility.getWorkflowId()==0){
						facility.setWorkflowId(workFlowDetails.getWorkFlowId());
					}
						showDetailView(facility);
//					WorkflowLoad flowLoad= new WorkflowLoad(facility.getWorkflowId(), facility.getNextTaskId(), getUserWorkspace().getUserRoleSet());
//					boolean userAcces =  validateUserAccess("Facility", new String[] {"CAFReference"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), facility);
//					if (userAcces){
//					}else{
//						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
//					}
				}else{
					showDetailView(facility);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Facility dialog with a new empty entry. <br>
	 */
	public void onClick$button_FacilityList_NewFacility(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Facility object, We GET it from the backend.
		final Facility aFacility = getFacilityService().getNewFacility();
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("facilityListCtrl", this);
		map.put("facility", aFacility);
		map.put("role", getUserWorkspace().getUserRoles());
		if (this.cafType != null) {
			if (this.cafType.getValue().equals("COMM")) {
				map.put("cafType", PennantConstants.FACILITY_COMMERCIAL);
			} else if (this.cafType.getValue().equals("CORP")) {
				map.put("cafType", PennantConstants.FACILITY_CORPORATE);
			}
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/SelectFacilityTypeDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_FacilityList_FacilitySearch(Event event) throws Exception {
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
	/*		this.pagingFacilityList.setActivePage(0);
			Events.postEvent("onCreate", this.window_FacilityList, event);
			this.window_FacilityList.invalidate();
	*/		

  		this.sortOperator_CAFReference.setSelectedIndex(0);
	  	this.cAFReference.setValue("");
  		this.sortOperator_CustID.setSelectedIndex(0);
	  	this.custID.setValue("");
  		this.sortOperator_StartDate.setSelectedIndex(0);
		this.startDate.setValue(null);
  		this.sortOperator_PresentingUnit.setSelectedIndex(0);
	  	this.presentingUnit.setValue("");
  		this.sortOperator_CountryOfDomicile.setSelectedIndex(0);
	  	this.countryOfDomicile.setValue("");
  		this.sortOperator_DeadLine.setSelectedIndex(0);
		this.deadLine.setValue(null);
  		this.sortOperator_CountryOfRisk.setSelectedIndex(0);
	  	this.countryOfRisk.setValue("");
  		this.sortOperator_EstablishedDate.setSelectedIndex(0);
		this.establishedDate.setValue(null);
  		this.sortOperator_NatureOfBusiness.setSelectedIndex(0);
	  	this.natureOfBusiness.setValue("");
  		this.sortOperator_SICCode.setSelectedIndex(0);
	  	this.sICCode.setValue("");
  		this.sortOperator_CountryManager.setSelectedIndex(0);
	  	this.countryManager.setValue("");
  		this.sortOperator_CustomerRiskType.setSelectedIndex(0);
	  	this.customerRiskType.setValue("");
  		this.sortOperator_RelationshipManager.setSelectedIndex(0);
	  	this.relationshipManager.setValue("");
  		this.sortOperator_CustomerGroup.setSelectedIndex(0);
	  	this.customerGroup.setValue("");
  		this.sortOperator_NextReviewDate.setSelectedIndex(0);
		this.nextReviewDate.setValue(null);

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
		PTMessageUtils.showHelpWindow(event, window_FacilityList);
		logger.debug("Leaving");
	}

	/**
	 * When the facility print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FacilityList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("Facility", getSearchObj(),this.pagingFacilityList.getTotalSize()+1);
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
		getUserWorkspace().alocateAuthorities("FacilityList");
		
		if(moduleType==null){
			this.button_FacilityList_NewFacility.setVisible(getUserWorkspace().isAllowed("button_FacilityList_NewFacility"));
		}else{
			this.button_FacilityList_NewFacility.setVisible(false);
		}	
		this.button_FacilityList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FacilityList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Facility (aFacility)
	 * @throws Exception
	 */
	private void showDetailView(Facility aFacility) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("facility", aFacility);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the FacilityListbox from the
		 * dialog when we do a delete, edit or insert a Facility.
		 */
		map.put("facilityListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<Facility>(Facility.class,getListRows());
		this.searchObj.addSort("CAFReference", false);
		this.searchObj.addTabelName("FacilityHeader_View");
		this.searchObj.addField("cAFReference");
		this.searchObj.addField("custCIF");
		this.searchObj.addField("custID");
		this.searchObj.addField("startDate");
		this.searchObj.addField("presentingUnit");
		this.searchObj.addField("countryOfDomicile");
		this.searchObj.addField("deadLine");
		this.searchObj.addField("countryOfRisk");
		this.searchObj.addField("establishedDate");
		this.searchObj.addField("natureOfBusiness");
		this.searchObj.addField("sICCode");
		this.searchObj.addField("countryManager");
		this.searchObj.addField("customerRiskType");
		this.searchObj.addField("relationshipManager");
		this.searchObj.addField("customerGroup");
		this.searchObj.addField("nextReviewDate");
		if (isWorkFlowEnabled()) {
			this.searchObj.addField("recordStatus");
			this.searchObj.addField("recordType");
		}
		if (this.cafType != null) {
			if (this.cafType.getValue().equals("COMM")) {
				this.searchObj.addFilterEqual("FacilityType", PennantConstants.FACILITY_COMMERCIAL);
			} else if (this.cafType.getValue().equals("CORP")) {
				this.searchObj.addFilterEqual("FacilityType", PennantConstants.FACILITY_CORPORATE);
			}
		}
		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_FacilityList_NewFacility.setVisible(true);
			} else {
				button_FacilityList_NewFacility.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("FacilityHeader_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("FacilityHeader_AView");
		}
		
		boolean accessToCreateNewFin = getFacilityService().checkFirstTaskOwnerAccess(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		if (accessToCreateNewFin) {
			button_FacilityList_NewFacility.setVisible(true);
		} else {
			button_FacilityList_NewFacility.setVisible(false);
		}
		
		if(moduleType!=null){
			this.button_FacilityList_NewFacility.setVisible(false);
		}	
		
	// CAF Reference
		if (!StringUtils.trimToEmpty(this.cAFReference.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CAFReference.getSelectedItem(), this.cAFReference.getValue(), "CAFReference");
		}
	// Customer
		if (!StringUtils.trimToEmpty(this.custID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CustID.getSelectedItem(), this.custID.getValue(), "CustCIF");
		}
	// Start Date
		if (this.startDate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_StartDate.getSelectedItem(), DateUtility.formatDate(this.startDate.getValue(), PennantConstants.DBDateFormat), "StartDate");
		}
	// Presenting Unit
		if (!StringUtils.trimToEmpty(this.presentingUnit.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_PresentingUnit.getSelectedItem(), this.presentingUnit.getValue(), "PresentingUnit");
		}
	// Country of Domicile
		if (!StringUtils.trimToEmpty(this.countryOfDomicile.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CountryOfDomicile.getSelectedItem(), this.countryOfDomicile.getValue(), "CountryOfDomicile");
		}
	// Dead Line
		if (this.deadLine.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_DeadLine.getSelectedItem(), DateUtility.formatDate(this.deadLine.getValue(), PennantConstants.DBDateFormat), "DeadLine");
		}
	// Country of Risk
		if (!StringUtils.trimToEmpty(this.countryOfRisk.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CountryOfRisk.getSelectedItem(), this.countryOfRisk.getValue(), "CountryOfRisk");
		}
	// Established Date
		if (this.establishedDate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_EstablishedDate.getSelectedItem(), DateUtility.formatDate(this.establishedDate.getValue(), PennantConstants.DBDateFormat), "EstablishedDate");
		}
	// Nature of Business
		if (!StringUtils.trimToEmpty(this.natureOfBusiness.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_NatureOfBusiness.getSelectedItem(), this.natureOfBusiness.getValue(), "NatureOfBusiness");
		}
	// SIC Code
		if (!StringUtils.trimToEmpty(this.sICCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_SICCode.getSelectedItem(), this.sICCode.getValue(), "SICCode");
		}
	// Country Manager
		if (!StringUtils.trimToEmpty(this.countryManager.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CountryManager.getSelectedItem(), this.countryManager.getValue(), "CountryManager");
		}
	// Customer Risk Type
		if (!StringUtils.trimToEmpty(this.customerRiskType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CustomerRiskType.getSelectedItem(), this.customerRiskType.getValue(), "CustomerRiskType");
		}
	// Relationship Manager
		if (!StringUtils.trimToEmpty(this.relationshipManager.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RelationshipManager.getSelectedItem(), this.relationshipManager.getValue(), "RelationshipManager");
		}
	// Customer Group
		if (!StringUtils.trimToEmpty(this.customerGroup.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CustomerGroup.getSelectedItem(), this.customerGroup.getValue(), "CustomerGroup");
		}
	// Next Review Date
		if (this.nextReviewDate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_NextReviewDate.getSelectedItem(), DateUtility.formatDate(this.nextReviewDate.getValue(), PennantConstants.DBDateFormat), "NextReviewDate");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxFacility,this.pagingFacilityList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FacilityService getFacilityService() {
		return this.facilityService;
	}

	public JdbcSearchObject<Facility> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Facility> searchObj) {
		this.searchObj = searchObj;
	}
}