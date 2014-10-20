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
 * FileName    		:  PropertyTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.propertytype;


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
import com.pennant.backend.model.amtmasters.PropertyType;
import com.pennant.backend.service.amtmasters.PropertyTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.amtmasters.propertytype.model.PropertyTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/PropertyType/PropertyTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PropertyTypeListCtrl extends GFCBaseListCtrl<PropertyType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(PropertyTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_PropertyTypeList; // autowired
	protected Borderlayout borderLayout_PropertyTypeList; // autowired
	protected Paging pagingPropertyTypeList; // autowired
	protected Listbox listBoxPropertyType; // autowired

	protected Textbox propertyTypeId; // autowired
	protected Listbox sortOperator_propertyTypeId; // autowired
	protected Textbox propertyTypeName; // autowired
	protected Listbox sortOperator_propertyTypeName; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired

	// List headers
	protected Listheader listheader_PropertyTypeName; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_PropertyTypeList_NewPropertyType; // autowired
	protected Button button_PropertyTypeList_PropertyTypeSearchDialog; // autowired
	protected Button button_PropertyTypeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PropertyType> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;

	private transient PropertyTypeService propertyTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public PropertyTypeListCtrl() {
		super();
	}

	public void onCreate$window_PropertyTypeList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PropertyType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PropertyType");
			
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
		this.sortOperator_propertyTypeId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_propertyTypeId.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_propertyTypeName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_propertyTypeName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}else{
			this.row_AlwWorkflow.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_PropertyTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxPropertyType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingPropertyTypeList.setPageSize(getListRows());
		this.pagingPropertyTypeList.setDetailed(true);

		this.listheader_PropertyTypeName.setSortAscending(new FieldComparator("propertyTypeName", true));
		this.listheader_PropertyTypeName.setSortDescending(new FieldComparator("propertyTypeName", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<PropertyType>(PropertyType.class,getListRows());
		this.searchObj.addSort("PropertyTypeId", false);
		this.searchObj.addField("propertyTypeId");
		this.searchObj.addField("propertyTypeName");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		this.searchObj.addTabelName("AMTPropertyType_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_PropertyTypeList_NewPropertyType.setVisible(true);
			} else {
				button_PropertyTypeList_NewPropertyType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_PropertyTypeList_NewPropertyType.setVisible(false);
			this.button_PropertyTypeList_PropertyTypeSearchDialog.setVisible(false);
			this.button_PropertyTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxPropertyType.setItemRenderer(new PropertyTypeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PropertyTypeList");
		
		this.button_PropertyTypeList_NewPropertyType.setVisible(getUserWorkspace().isAllowed("button_PropertyTypeList_NewPropertyType"));
		this.button_PropertyTypeList_PropertyTypeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_PropertyTypeList_PropertyTypeFindDialog"));
		this.button_PropertyTypeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_PropertyTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.propertytype.model.PropertyTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onPropertyTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected PropertyType object
		final Listitem item = this.listBoxPropertyType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PropertyType aPropertyType = (PropertyType) item.getAttribute("data");
			final PropertyType propertyType = getPropertyTypeService().getPropertyTypeById(aPropertyType.getId());
			
			if(propertyType==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aPropertyType.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_PropertyTypeId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND PropertyTypeId="+ propertyType.getPropertyTypeId()+" AND version=" + propertyType.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "PropertyType", whereCond, propertyType.getTaskId(), propertyType.getNextTaskId());
					if (userAcces){
						showDetailView(propertyType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(propertyType);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the PropertyType dialog with a new empty entry. <br>
	 */
	public void onClick$button_PropertyTypeList_NewPropertyType(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new PropertyType object, We GET it from the backend.
		final PropertyType aPropertyType = getPropertyTypeService().getNewPropertyType();
		showDetailView(aPropertyType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param PropertyType (aPropertyType)
	 * @throws Exception
	 */
	private void showDetailView(PropertyType aPropertyType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aPropertyType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aPropertyType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("propertyType", aPropertyType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the PropertyTypeListbox from the
		 * dialog when we do a delete, edit or insert a PropertyType.
		 */
		map.put("propertyTypeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/PropertyType/PropertyTypeDialog.zul",null,map);
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_PropertyTypeList);
		logger.debug("Leaving");
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
		this.sortOperator_propertyTypeId.setSelectedIndex(0);
		this.propertyTypeId.setValue("");
		this.sortOperator_propertyTypeName.setSelectedIndex(0);
		this.propertyTypeName.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxPropertyType,this.pagingPropertyTypeList);
		logger.debug("Leaving");
	}

	/*
	 * call the PropertyType dialog
	 */
	
	public void onClick$button_PropertyTypeList_PropertyTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the propertyType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PropertyTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("PropertyType", getSearchObj(),this.pagingPropertyTypeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	/**
	 * Method for Searching List based on Filters
	 */
	private void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		// PropertyTypeId
		if (!StringUtils.trimToEmpty(this.propertyTypeId.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_propertyTypeId.getSelectedItem(),
					this.propertyTypeId.getValue(), "PropertyTypeId");
		}

		// PropertyTypeName
		if (!StringUtils.trimToEmpty(this.propertyTypeName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_propertyTypeName.getSelectedItem(),
					this.propertyTypeName.getValue(), "PropertyTypeName");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !PennantConstants.List_Select.equals(this.recordType
				.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxPropertyType,this.pagingPropertyTypeList);

		logger.debug("Leaving");

	}


	public void setPropertyTypeService(PropertyTypeService propertyTypeService) {
		this.propertyTypeService = propertyTypeService;
	}

	public PropertyTypeService getPropertyTypeService() {
		return this.propertyTypeService;
	}

	public JdbcSearchObject<PropertyType> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<PropertyType> searchObj) {
		this.searchObj = searchObj;
	}
}