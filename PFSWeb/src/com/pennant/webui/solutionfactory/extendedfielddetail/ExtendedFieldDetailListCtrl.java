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
 * FileName    		:  ExtendedFieldDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.solutionfactory.extendedfielddetail;


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
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.solutionfactory.extendedfielddetail.model.ExtendedFieldDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/ExtendedFieldDetail/ExtendedFieldDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ExtendedFieldDetailListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> implements Serializable {

	private static final long serialVersionUID = 7866684540841299572L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ExtendedFieldDetailList; 		// autowired
	protected Borderlayout 	borderLayout_ExtendedFieldDetailList; 	// autowired
	protected Paging 		pagingExtendedFieldDetailList; 			// autowired
	protected Listbox 		listBoxExtendedFieldDetail; 			// autowired

	// List headers
	protected Listheader listheader_FieldName; 		// autowired
	protected Listheader listheader_FieldType; 		// autowired
	protected Listheader listheader_RecordStatus; 	// autowired
	protected Listheader listheader_RecordType;
	
	//search
	protected Combobox moduleName; // autowired
	protected Listbox sortOperator_moduleName; // autowired
	protected Combobox subModuleName; // autowired
	protected Listbox sortOperator_subModuleName; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_ExtendedFieldDetailSearch_RecordStatus; // autowired
	protected Label label_ExtendedFieldDetailSearch_RecordType; // autowired
	protected Label label_ExtendedFieldDetailSearchResult; // autowired
	
	protected Grid	                       searchGrid;	
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;

	// checkRights
	protected Button btnHelp; 														// autowired
	protected Button button_ExtendedFieldDetailList_NewExtendedFieldDetail; 		// autowired
	protected Button button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog;// autowired
	protected Button button_ExtendedFieldDetailList_PrintList; 						// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ExtendedFieldHeader> searchObj;
	private transient ExtendedFieldDetailService extendedFieldDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	private HashMap<String, HashMap<String, String>> moduleMap = PennantStaticListUtil.getModuleName();
	private List<ValueLabel> modulesList = null;

	private Tabbox						   tabbox;
	private Tab							   tab;
	
	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldDetailListCtrl() {
		super();
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ExtendedFieldDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
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
		
		try{
			if(event.getTarget() != null && event.getTarget().getParent() != null 
					&& event.getTarget().getParent().getParent() != null
					&& event.getTarget().getParent().getParent().getParent() != null) {
				tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
				tab = tabbox.getSelectedTab();
			}
			
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ExtendedFieldDetail");
			boolean wfAvailable=true;

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ExtendedFieldDetail");

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
			this.sortOperator_moduleName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_moduleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
			this.sortOperator_subModuleName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_subModuleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
			if (isWorkFlowEnabled()){
				this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
				this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
				this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
				this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
				this.recordType=PennantAppUtil.setRecordType(this.recordType);	
			}else{
				this.recordStatus.setVisible(false);
				this.recordType.setVisible(false);
				this.sortOperator_recordStatus.setVisible(false);
				this.sortOperator_recordType.setVisible(false);
				this.label_ExtendedFieldDetailSearch_RecordStatus.setVisible(false);
				this.label_ExtendedFieldDetailSearch_RecordType.setVisible(false);
			}

			fillComboBox(moduleName, null, modulesList, "");
			fillsubModule(subModuleName, "", "");
			/* set components visible dependent on the users rights */
			doCheckRights();

			this.borderLayout_ExtendedFieldDetailList.setHeight(getBorderLayoutHeight());
			this.listBoxExtendedFieldDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

			// set the paging parameters
			this.pagingExtendedFieldDetailList.setPageSize(getListRows());
			this.pagingExtendedFieldDetailList.setDetailed(true);

			this.listheader_FieldName.setSortAscending(new FieldComparator("moduleName", true));
			this.listheader_FieldName.setSortDescending(new FieldComparator("moduleName", false));
			this.listheader_FieldType.setSortAscending(new FieldComparator("subModuleName", true));
			this.listheader_FieldType.setSortDescending(new FieldComparator("subModuleName", false));


			if (isWorkFlowEnabled()){
				this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
				this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
				this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
				this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
			}else{
				this.listheader_RecordStatus.setVisible(false);
				this.listheader_RecordType.setVisible(false);
			}
			// set the itemRenderer
			this.listBoxExtendedFieldDetail.setItemRenderer(new ExtendedFieldDetailListModelItemRenderer());
			if (!isWorkFlowEnabled() && wfAvailable){
				//this.button_ExtendedFieldDetailList_NewExtendedFieldDetail.setVisible(false);
				this.button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog.setVisible(false);
				this.button_ExtendedFieldDetailList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				doSearch();
				if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
					this.workFlowFrom.setVisible(false);
					this.fromApproved.setSelected(true);
				}
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
		getUserWorkspace().alocateAuthorities("ExtendedFieldDetailList");
		this.button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_ExtendedFieldDetailList_ExtendedFieldDetailFindDialog"));
		this.button_ExtendedFieldDetailList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_ExtendedFieldDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.solutionfactory.extendedfielddetail.model.ExtendedFieldDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onExtendedFieldDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ExtendedFieldDetail object
		final Listitem item = this.listBoxExtendedFieldDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			ExtendedFieldHeader aExtendedFieldHeader  = (ExtendedFieldHeader) item.getAttribute("data");
			aExtendedFieldHeader = getExtendedFieldDetailService().getExtendedFieldHeaderById(
					aExtendedFieldHeader);
			showDetailView(aExtendedFieldHeader);

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param ExtendedFieldDetail (aExtendedFieldDetail)
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
		 * fine for synchronizing the data in the ExtendedFieldDetailListbox from the
		 * dialog when we do a delete, edit or insert a ExtendedFieldDetail.
		 */
		map.put("extendedFieldDetailListCtrl", this);
		map.put("moduleid", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldDetailList);
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
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		doSearch();
		/*this.pagingExtendedFieldDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ExtendedFieldDetailList, event);
		this.window_ExtendedFieldDetailList.invalidate();*/
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the ExtendedFieldDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the extendedFieldDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ExtendedFieldDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("ExtendedFieldHeader", getSearchObj(),this.pagingExtendedFieldDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<ExtendedFieldHeader>(ExtendedFieldHeader.class,getListRows());
		this.searchObj.addSort("moduleName", false);
		this.searchObj.addSort("subModuleName", false);
		this.searchObj.addTabelName("ExtendedFieldHeader_View");
		if (isWorkFlowEnabled()) {

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("ExtendedFieldHeader_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("ExtendedFieldHeader_AView");
		}
		//Module
		if (!this.moduleName.getSelectedItem().getValue().toString().equals("#") ) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_moduleName.getSelectedItem(), this.moduleName.getSelectedItem().getValue().toString(), "moduleName");
		}
		
		// SubModule Name
		if (!this.subModuleName.getSelectedItem().getValue().toString().equals("#")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_subModuleName.getSelectedItem(), this.subModuleName.getSelectedItem().getValue().toString(), "subModuleName");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}
		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxExtendedFieldDetail,this.pagingExtendedFieldDetailList);
		logger.debug("Leaving" );
	}
	
	/*
	 * onChange Event For combobox moduleName
	 */
	public void onChange$moduleName(Event event){
		logger.debug("Entering  :" + event.toString());
        if(!this.moduleName.getSelectedItem().getValue().toString().equals("#")){
             fillsubModule(subModuleName, this.moduleName.getSelectedItem().getValue().toString(), "");
        } else {
             fillsubModule(subModuleName, "", "");
        }
		logger.debug("Leaving  :" + event.toString());
	}
	
	/*
	 * method For filling submodules list
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
	
	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}
	public ExtendedFieldDetailService getExtendedFieldDetailService() {
		return this.extendedFieldDetailService;
	}

	public JdbcSearchObject<ExtendedFieldHeader> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ExtendedFieldHeader> searchObj) {
		this.searchObj = searchObj;
	}
}