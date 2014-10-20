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
 * FileName    		:  ExtendedFieldHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.extendedfieldheader;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.staticparms.ExtendedFieldHeaderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.staticparms.extendedfieldheader.model.ExtendedFieldHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMasters/ExtendedFieldHeader/ExtendedFieldHeaderList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ExtendedFieldHeaderListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> implements Serializable {

	private static final long	serialVersionUID	= -1751614637216289000L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldHeaderListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ExtendedFieldHeaderList; 		// autowired
	protected Borderlayout	borderLayout_ExtendedFieldHeaderList; 	// autowired
	protected Paging 		pagingExtendedFieldHeaderList; 			// autowired
	protected Listbox 		listBoxExtendedFieldHeader; 			// autowired

	protected Combobox 	moduleName; 						// autowired
	protected Listbox 	sortOperator_moduleName; 			// autowired
	protected Combobox 	subModuleName; 						// autowired
	protected Listbox 	sortOperator_subModuleName; 		// autowired
	protected Textbox 	tabHeading; 						// autowired
	protected Listbox 	sortOperator_tabHeading; 			// autowired
	protected Intbox 	numberOfColumns; 					// autowired
	protected Listbox 	sortOperator_numberOfColumns; 		// autowired
	protected Textbox 	recordStatus; 						// autowired
	protected Listbox 	recordType;							// autowired
	protected Listbox 	sortOperator_recordStatus; 			// autowired
	protected Listbox 	sortOperator_recordType; 			// autowired

	// List headers
	protected Listheader listheader_ModuleName; 		// autowired
	protected Listheader listheader_SubModuleName; 		// autowired
	protected Listheader listheader_TabHeading; 		// autowired
	protected Listheader listheader_NumberOfColumns; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															// autowired
	protected Button button_ExtendedFieldHeaderList_NewExtendedFieldHeader; 			// autowired
	protected Button button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog; 	// autowired
	protected Button button_ExtendedFieldHeaderList_PrintList; 							// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ExtendedFieldHeader> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;

	private transient ExtendedFieldHeaderService extendedFieldHeaderService;
	private transient WorkFlowDetails workFlowDetails=null;
	private Tabbox						   tabbox;
	private Tab							   tab;
	private final HashMap<String, HashMap<String, String>> moduleMap = PennantStaticListUtil.getModuleName();
	private List<ValueLabel> modulesList = null;
	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldHeaderListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ExtendedFeildHeader object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldHeaderList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// Filling Module Map
		if (modulesList == null) {
			ValueLabel valuLable = null;
			modulesList = new ArrayList<ValueLabel>(moduleMap.size());
			Set<String> moduleKeys = moduleMap.keySet();
			for (String key : moduleKeys) {
				valuLable = new ValueLabel(key,Labels.getLabel("label_ExtendedField_" + key));
				modulesList.add(valuLable);
			}
		}
		try {
			
			if(event.getTarget() != null && event.getTarget().getParent() != null 
					&& event.getTarget().getParent().getParent() != null
					&& event.getTarget().getParent().getParent().getParent() != null) {
				tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
				tab = tabbox.getSelectedTab();
			}
			
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ExtendedFieldHeader");
			boolean wfAvailable=true;

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ExtendedFieldHeader");

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


			this.sortOperator_moduleName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_moduleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
			fillComboBox(moduleName, null, modulesList, "");

			this.sortOperator_subModuleName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_subModuleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
			fillsubModule(subModuleName, "", "");

			this.sortOperator_tabHeading.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_tabHeading.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_numberOfColumns.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_numberOfColumns.setItemRenderer(new SearchOperatorListModelItemRenderer());

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

			this.borderLayout_ExtendedFieldHeaderList.setHeight(getBorderLayoutHeight());
			this.listBoxExtendedFieldHeader.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

			// set the paging parameters
			this.pagingExtendedFieldHeaderList.setPageSize(getListRows());
			this.pagingExtendedFieldHeaderList.setDetailed(true);

			this.listheader_ModuleName.setSortAscending(new FieldComparator("moduleName", true));
			this.listheader_ModuleName.setSortDescending(new FieldComparator("moduleName", false));

			this.listheader_SubModuleName.setSortAscending(new FieldComparator("subModuleName", true));
			this.listheader_SubModuleName.setSortDescending(new FieldComparator("subModuleName", false));

			this.listheader_TabHeading.setSortAscending(new FieldComparator("tabHeading", true));
			this.listheader_TabHeading.setSortDescending(new FieldComparator("tabHeading", false));

			this.listheader_NumberOfColumns.setSortAscending(new FieldComparator("numberOfColumns", true));
			this.listheader_NumberOfColumns.setSortDescending(new FieldComparator("numberOfColumns", false));

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
			this.searchObj = new JdbcSearchObject<ExtendedFieldHeader>(ExtendedFieldHeader.class,getListRows());
			this.searchObj.addSort("moduleName", false);
			this.searchObj.addSort("subModuleName", false);
			this.searchObj.addField("moduleName");
			this.searchObj.addField("subModuleName");
			this.searchObj.addField("moduleId");
			this.searchObj.addField("tabHeading");
			this.searchObj.addField("numberOfColumns");
			this.searchObj.addField("recordStatus");
			this.searchObj.addField("recordType");

			// Workflow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("ExtendedFieldHeader_View");
				if (isFirstTask()) {
					button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(true);
				} else {
					button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(false);
				}

				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
			}else{
				this.searchObj.addTabelName("ExtendedFieldHeader_AView");
			}

			setSearchObj(this.searchObj);
			if (!isWorkFlowEnabled() && wfAvailable){
				this.button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(false);
				this.button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog.setVisible(false);
				this.button_ExtendedFieldHeaderList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				doSearch();
				// set the itemRenderer
				this.listBoxExtendedFieldHeader.setItemRenderer(new ExtendedFieldHeaderListModelItemRenderer());
			}
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			tab.close();
		}
	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ExtendedFieldHeaderList");

		this.button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(getUserWorkspace()
				.isAllowed("button_ExtendedFieldHeaderList_NewExtendedFieldHeader"));
		this.button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ExtendedFieldHeaderList_ExtendedFieldHeaderFindDialog"));
		this.button_ExtendedFieldHeaderList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ExtendedFieldHeaderList_PrintList"));
		
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.staticparms.extendedfieldheader.model.ExtendedFieldHeaderListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onExtendedFieldHeaderItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ExtendedFieldHeader object
		final Listitem item = this.listBoxExtendedFieldHeader.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ExtendedFieldHeader aExtendedFieldHeader = (ExtendedFieldHeader) item.getAttribute("data");
			final ExtendedFieldHeader extendedFieldHeader = getExtendedFieldHeaderService().getExtendedFieldHeaderById(
					aExtendedFieldHeader.getId());
			
			if(extendedFieldHeader==null){
				
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aExtendedFieldHeader.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_ModuleId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", 
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND ModuleId="+ extendedFieldHeader.getModuleId()+
					" AND version=" + extendedFieldHeader.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "ExtendedFieldHeader",
							whereCond, extendedFieldHeader.getTaskId(), extendedFieldHeader.getNextTaskId());
					if (userAcces){
						showDetailView(extendedFieldHeader);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					if(StringUtils.trimToEmpty(extendedFieldHeader.getNextRoleCode()).equals("")){
						showDetailView(extendedFieldHeader);
					}else{
						PTMessageUtils.showErrorMessage("Record in WorkFlow. Not allowed to Maintain.");
					}
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the ExtendedFieldHeader dialog with a new empty entry. <br>
	 */
	public void onClick$button_ExtendedFieldHeaderList_NewExtendedFieldHeader(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new ExtendedFieldHeader object, We GET it from the backend.
		final ExtendedFieldHeader aExtendedFieldHeader = getExtendedFieldHeaderService().getNewExtendedFieldHeader();
		showDetailView(aExtendedFieldHeader);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param ExtendedFieldHeader (aExtendedFieldHeader)
	 * @throws Exception
	 */
	private void showDetailView(ExtendedFieldHeader aExtendedFieldHeader) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aExtendedFieldHeader.getWorkflowId()==0 && isWorkFlowEnabled()){
			aExtendedFieldHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldHeader", aExtendedFieldHeader);
		
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the ExtendedFieldHeaderListbox from the
		 * dialog when we do a delete, edit or insert a ExtendedFieldHeader.
		 */
		map.put("extendedFieldHeaderListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/ExtendedFieldHeader/ExtendedFieldHeaderDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldHeaderList);
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
		this.sortOperator_moduleName.setSelectedIndex(0);
		this.moduleName.setSelectedIndex(0);
		this.sortOperator_subModuleName.setSelectedIndex(0);
		this.subModuleName.setSelectedIndex(0);
		this.sortOperator_numberOfColumns.setSelectedIndex(0);
		this.numberOfColumns.setValue(null);
		this.sortOperator_tabHeading.setSelectedIndex(0);
		this.tabHeading.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears All Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxExtendedFieldHeader,this.pagingExtendedFieldHeaderList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the ExtendedFieldHeader dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the extendedFieldHeader print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ExtendedFieldHeaderList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("ExtendedFieldHeader", getSearchObj(),this.pagingExtendedFieldHeaderList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Searching List based on Filters
	 */
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		//Module Name
		if (this.moduleName.getValue()!= null && !PennantConstants.List_Select.equals(this.moduleName.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_moduleName.getSelectedItem(),this.moduleName.getSelectedItem().getValue().toString(), "ModuleName");
		}

		//SubModuleName
		if (this.subModuleName.getValue()!= null && !PennantConstants.List_Select.equals(this.subModuleName.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_subModuleName.getSelectedItem(),this.subModuleName.getSelectedItem().getValue().toString(), "SubModuleName");
		}

		//NumberofColumns
		if (this.numberOfColumns.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_numberOfColumns.getSelectedItem(),this.numberOfColumns.getValue(), "NumberOfColumns");
		}

		//TabHeading
		if (!StringUtils.trimToEmpty(this.tabHeading.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_tabHeading.getSelectedItem(),this.tabHeading.getValue(), "TabHeading");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxExtendedFieldHeader,this.pagingExtendedFieldHeaderList);
		logger.debug("Leaving");
	}

	/*
	 * Method For filling submodules
	 */
	private void fillsubModule(Combobox combobox, String moduleName, String value) {
		if (this.moduleName.getSelectedItem() != null) {
			HashMap<String, String> hashMap = PennantStaticListUtil.getModuleName().get(moduleName) == null ? new HashMap<String, String>()
					: PennantStaticListUtil.getModuleName().get(moduleName);
			ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
			subModuleName.getItems().clear();
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("----Select-----");
			comboitem.setValue("#");
			subModuleName.appendChild(comboitem);
			subModuleName.setSelectedItem(comboitem);
			if (arrayList != null) {
				for (int i = 0; i < arrayList.size(); i++) {
					comboitem = new Comboitem();
					comboitem.setLabel(Labels.getLabel("label_ExtendedField_"+arrayList.get(i)));
					comboitem.setValue(arrayList.get(i));
					subModuleName.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(arrayList.get(i))) {
						subModuleName.setSelectedItem(comboitem);
					}
				}
			}
		} else {
			subModuleName.getItems().clear();
		}
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setExtendedFieldHeaderService(ExtendedFieldHeaderService extendedFieldHeaderService) {
		this.extendedFieldHeaderService = extendedFieldHeaderService;
	}
	public ExtendedFieldHeaderService getExtendedFieldHeaderService() {
		return this.extendedFieldHeaderService;
	}

	public JdbcSearchObject<ExtendedFieldHeader> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ExtendedFieldHeader> searchObj) {
		this.searchObj = searchObj;
	}
	
}