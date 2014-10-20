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
 * FileName    		:  LovFieldCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.lovfieldcode;


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
import org.zkoss.zul.Checkbox;
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
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.service.staticparms.LovFieldCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.staticparms.lovfieldcode.model.LovFieldCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/StaticParms/LovFieldCode/LovFieldCodeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class LovFieldCodeListCtrl extends GFCBaseListCtrl<LovFieldCode> implements Serializable {

	private static final long serialVersionUID = 8396609468989226478L;
	private final static Logger logger = Logger.getLogger(LovFieldCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window        window_LovFieldCodeList; 			       // autoWired
	protected Borderlayout  borderLayout_LovFieldCodeList; 		       // autoWired
	protected Paging        pagingLovFieldCodeList; 			       // autoWired
	protected Listbox       listBoxLovFieldCode; 				       // autoWired

	protected Textbox  fieldCode; 						     // autoWired
	protected Listbox  sortOperator_fieldCode; 			     // autoWired
	protected Textbox  fieldCodeDesc; 					     // autoWired
	protected Listbox  sortOperator_fieldCodeDesc; 		     // autoWired
	protected Textbox  fieldCodeType; 					     // autoWired
	protected Listbox  sortOperator_fieldCodeType; 		     // autoWired
	protected Checkbox isActive; 						     // autoWired
	protected Listbox  sortOperator_isActive; 			     // autoWired
	protected Textbox  recordStatus; 					     // autoWired
	protected Listbox  recordType;						     // autoWired
	protected Listbox  sortOperator_recordStatus; 		     // autoWired
	protected Listbox  sortOperator_recordType; 			 // autoWired

	// List headers
	protected Listheader listheader_FieldCode; 					       // autoWired
	protected Listheader listheader_FieldCodeDesc; 				       // autoWired
	protected Listheader listheader_FieldCodeType; 				       // autoWired
	protected Listheader listheader_FieldEdit; 					       // autoWired
	protected Listheader listheader_isActive; 					       // autoWired
	protected Listheader listheader_RecordStatus; 				       // autoWired
	protected Listheader listheader_RecordType;                        // autoWired

	// checkRights
	protected Button btnHelp; 										   // autoWired
	protected Button button_LovFieldCodeList_NewLovFieldCode; 		   // autoWired
	protected Button button_LovFieldCodeList_LovFieldCodeSearchDialog; // autoWired
	protected Button button_LovFieldCodeList_PrintList; 		       // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<LovFieldCode>   searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;

	private transient LovFieldCodeService      lovFieldCodeService;
	private transient WorkFlowDetails          workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public LovFieldCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_LovFieldCodeList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("LovFieldCode");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LovFieldCode");

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

		this.sortOperator_fieldCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_fieldCodeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_fieldCodeType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_isActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = setRecordType(this.recordType);
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		} else {
			this.row_AlwWorkflow.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_LovFieldCodeList.setHeight(getBorderLayoutHeight());
		this.listBoxLovFieldCode.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		
		// set the paging parameters
		this.pagingLovFieldCodeList.setPageSize(getListRows());
		this.pagingLovFieldCodeList.setDetailed(true);

		this.listheader_FieldCode.setSortAscending(new FieldComparator("fieldCode", true));
		this.listheader_FieldCode.setSortDescending(new FieldComparator("fieldCode", false));
		this.listheader_FieldCodeDesc.setSortAscending(new FieldComparator("fieldCodeDesc", true));
		this.listheader_FieldCodeDesc.setSortDescending(new FieldComparator("fieldCodeDesc", false));
		this.listheader_FieldCodeType.setSortAscending(new FieldComparator("fieldCodeType", true));
		this.listheader_FieldCodeType.setSortDescending(new FieldComparator("fieldCodeType", false));
		this.listheader_isActive.setSortAscending(new FieldComparator("isActive", true));
		this.listheader_isActive.setSortDescending(new FieldComparator("isActive", false));
		this.listheader_FieldEdit.setSortAscending(new FieldComparator("fieldEdit", true));
		this.listheader_FieldEdit.setSortDescending(new FieldComparator("fieldEdit", false));
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<LovFieldCode>(LovFieldCode.class,getListRows());
		this.searchObj.addSort("FieldCode", false);
		this.searchObj.addField("fieldCode");
		this.searchObj.addField("fieldCodeDesc");
		this.searchObj.addField("fieldCodeType");
		this.searchObj.addField("isActive");
		this.searchObj.addField("fieldEdit");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTLovFieldCode_View");
			if (isFirstTask()) {
				button_LovFieldCodeList_NewLovFieldCode.setVisible(true);
			} else {
				button_LovFieldCodeList_NewLovFieldCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTLovFieldCode_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_LovFieldCodeList_NewLovFieldCode.setVisible(false);
			this.button_LovFieldCodeList_LovFieldCodeSearchDialog.setVisible(false);
			this.button_LovFieldCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxLovFieldCode.setItemRenderer(new LovFieldCodeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("LovFieldCodeList");
		this.button_LovFieldCodeList_NewLovFieldCode.setVisible(getUserWorkspace()
				.isAllowed("button_LovFieldCodeList_NewLovFieldCode"));
		this.button_LovFieldCodeList_LovFieldCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_LovFieldCodeList_LovFieldCodeFindDialog"));
		this.button_LovFieldCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_LovFieldCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.StaticParms.lovFieldcode.model.LovFieldCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onLovFieldCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected LovFieldCode object
		final Listitem item = this.listBoxLovFieldCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final LovFieldCode aLovFieldCode = (LovFieldCode) item.getAttribute("data");
			final LovFieldCode lovFieldCode = getLovFieldCodeService().getLovFieldCodeById(aLovFieldCode.getId());

			if(lovFieldCode==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aLovFieldCode.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FieldCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FieldCode='"+ lovFieldCode.getFieldCode()+"' AND version=" + lovFieldCode.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"LovFieldCode", whereCond, lovFieldCode.getTaskId(), lovFieldCode.getNextTaskId());
					if (userAcces){
						showDetailView(lovFieldCode);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(lovFieldCode);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the LovFieldCode dialog with a new empty entry. <br>
	 */
	public void onClick$button_LovFieldCodeList_NewLovFieldCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new LovFieldCode object, We GET it from the backEnd.
		final LovFieldCode aLovFieldCode = getLovFieldCodeService().getNewLovFieldCode();
		showDetailView(aLovFieldCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param LovFieldCode (aLovFieldCode)
	 * @throws Exception
	 */
	private void showDetailView(LovFieldCode aLovFieldCode) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aLovFieldCode.getWorkflowId()==0 && isWorkFlowEnabled()){
			aLovFieldCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("lovFieldCode", aLovFieldCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the LovFieldCodeListbox from the
		 * dialog when we do a delete, edit or insert a LovFieldCode.
		 */
		map.put("lovFieldCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/LovFieldCode/LovFieldCodeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_LovFieldCodeList);
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
		this.sortOperator_fieldCode.setSelectedIndex(0);
		this.fieldCode.setValue("");
		this.sortOperator_fieldCodeDesc.setSelectedIndex(0);
		this.fieldCodeDesc.setValue("");
		this.sortOperator_fieldCodeType.setSelectedIndex(0);
		this.fieldCodeType.setValue("");
		this.sortOperator_isActive.setSelectedIndex(0);
		this.isActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxLovFieldCode,this.pagingLovFieldCodeList);
		logger.debug("Leaving");
	}

	/*
	 * call the LovFieldCode dialog
	 */
	public void onClick$button_LovFieldCodeList_LovFieldCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the lovFieldCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_LovFieldCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("LovFieldCode", getSearchObj(),this.pagingLovFieldCodeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	/**
	 * Method for Searching List based on Filters
	 */
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		//FieldCode
		if (!StringUtils.trimToEmpty(this.fieldCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_fieldCode.getSelectedItem(),this.fieldCode.getValue(), "FieldCode");
		}
		//FieldCodeDesc
		if (!StringUtils.trimToEmpty(this.fieldCodeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_fieldCodeDesc.getSelectedItem(),this.fieldCodeDesc.getValue(), "FieldCodeDesc");
		}
		//FieldCodeType
		if (!StringUtils.trimToEmpty(this.fieldCodeType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_fieldCodeType.getSelectedItem(),this.fieldCodeType.getValue(), "FieldCodeType");
		}
		// Active
		int intActive=0;
		if(this.isActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_isActive.getSelectedItem(),intActive, "isActive");

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
		getPagedListWrapper().init(this.searchObj, this.listBoxLovFieldCode,this.pagingLovFieldCodeList);
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setLovFieldCodeService(LovFieldCodeService lovFieldCodeService) {
		this.lovFieldCodeService = lovFieldCodeService;
	}
	public LovFieldCodeService getLovFieldCodeService() {
		return this.lovFieldCodeService;
	}

	public JdbcSearchObject<LovFieldCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<LovFieldCode> searchObj) {
		this.searchObj = searchObj;
	}
}