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
 * FileName    		:  LovFieldDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.lovfielddetail;


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
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.lovfielddetail.model.LovFieldDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/LovFieldDetail/LovFieldDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class LovFieldDetailListCtrl extends GFCBaseListCtrl<LovFieldDetail> implements Serializable {

	private static final long serialVersionUID = 3047814941939865707L;
	private final static Logger logger = Logger.getLogger(LovFieldDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_LovFieldDetailList; 			            // autoWired
	protected Borderlayout 	borderLayout_LovFieldDetailList; 	            // autoWired
	protected Paging 		pagingLovFieldDetailList; 			            // autoWired
	protected Listbox 		listBoxLovFieldDetail; 				            // autoWired

	protected Textbox 	fieldCodeId; 					// autoWired
	protected Listbox 	sortOperator_fieldCodeId; 		// autoWired
	protected Textbox 	fieldCodeValue; 				// autoWired
	protected Listbox 	sortOperator_fieldCodeValue; 	// autoWired
	protected Checkbox 	isActive; 						// autoWired
	protected Listbox 	sortOperator_isActive; 			// autoWired
	protected Textbox 	recordStatus; 					// autoWired
	protected Listbox 	recordType;						// autoWired
	protected Listbox 	sortOperator_recordStatus; 		// autoWired
	protected Listbox 	sortOperator_recordType; 		// autoWired
	// List headers
	protected Listheader listheader_FieldCode; 			                    // autoWired
	protected Listheader listheader_FieldCodeValue; 	                    // autoWired
	protected Listheader listheader_isActive; 			                    // autoWired
	protected Listheader listheader_RecordStatus; 	                     	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autoWired
	protected Button button_LovFieldDetailList_NewLovFieldDetail; 			// autoWired
	protected Button button_LovFieldDetailList_LovFieldDetailSearchDialog; 	// autoWired
	protected Button button_LovFieldDetailList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<LovFieldDetail> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient LovFieldDetailService lovFieldDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public LovFieldDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected LovFieldDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LovFieldDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("LovFieldDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LovFieldDetail");
			
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
		this.sortOperator_fieldCodeId.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeId.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_fieldCodeValue.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeValue.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_isActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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

		this.borderLayout_LovFieldDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxLovFieldDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingLovFieldDetailList.setPageSize(getListRows());
		this.pagingLovFieldDetailList.setDetailed(true);

		this.listheader_FieldCode.setSortAscending(new FieldComparator("fieldCode", true));
		this.listheader_FieldCode.setSortDescending(new FieldComparator("fieldCode", false));
		this.listheader_FieldCodeValue.setSortAscending(new FieldComparator("fieldCodeValue", true));
		this.listheader_FieldCodeValue.setSortDescending(new FieldComparator("fieldCodeValue", false));
		this.listheader_isActive.setSortAscending(new FieldComparator("isActive", true));
		this.listheader_isActive.setSortDescending(new FieldComparator("isActive", false));
		
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
		this.searchObj = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class,getListRows());
		this.searchObj.addSort("FieldCodeId",false);
		this.searchObj.addField("fieldCodeId");
		this.searchObj.addField("fieldCode");
		this.searchObj.addField("lovDescFieldCodeName");
		this.searchObj.addField("fieldCodeValue");
		this.searchObj.addField("isActive");

		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTLovFieldDetail_View");
			if (isFirstTask()) {
				button_LovFieldDetailList_NewLovFieldDetail.setVisible(true);
			} else {
				button_LovFieldDetailList_NewLovFieldDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTLovFieldDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_LovFieldDetailList_NewLovFieldDetail.setVisible(false);
			this.button_LovFieldDetailList_LovFieldDetailSearchDialog.setVisible(false);
			this.button_LovFieldDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxLovFieldDetail.setItemRenderer(new LovFieldDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("LovFieldDetailList");

		this.button_LovFieldDetailList_NewLovFieldDetail.setVisible(getUserWorkspace().
				isAllowed("button_LovFieldDetailList_NewLovFieldDetail"));
		this.button_LovFieldDetailList_LovFieldDetailSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_LovFieldDetailList_LovFieldDetailFindDialog"));
		this.button_LovFieldDetailList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_LovFieldDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.lovFielddetail.model.
	 * LovFieldDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLovFieldDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected LovFieldDetail object
		final Listitem item = this.listBoxLovFieldDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final LovFieldDetail aLovFieldDetail = (LovFieldDetail) item.getAttribute("data");
			final LovFieldDetail lovFieldDetail = getLovFieldDetailService().getLovFieldDetailById(
					aLovFieldDetail.getId());
			
			if(lovFieldDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aLovFieldDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_FieldCodeId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FieldCodeId="+ lovFieldDetail.getFieldCodeId()+
										" AND version=" + lovFieldDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"LovFieldDetail", whereCond, lovFieldDetail.getTaskId(), 
							lovFieldDetail.getNextTaskId());
					if (userAcces){
						showDetailView(lovFieldDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(lovFieldDetail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the LovFieldDetail dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_LovFieldDetailList_NewLovFieldDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new LovFieldDetail object, We GET it from the back end.
		final LovFieldDetail aLovFieldDetail = getLovFieldDetailService().getNewLovFieldDetail();
		showDetailView(aLovFieldDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param LovFieldDetail (aLovFieldDetail)
	 * @throws Exception
	 */
	private void showDetailView(LovFieldDetail aLovFieldDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aLovFieldDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aLovFieldDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("lovFieldDetail", aLovFieldDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the LovFieldDetailListbox from the
		 * dialog when we do a delete, edit or insert a LovFieldDetail.
		 */
		map.put("lovFieldDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/LovFieldDetail/LovFieldDetailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_LovFieldDetailList);
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
		this.sortOperator_fieldCodeId.setSelectedIndex(0);
		this.fieldCodeId.setValue("");
		this.sortOperator_fieldCodeValue.setSelectedIndex(0);
		this.fieldCodeValue.setValue("");
		this.sortOperator_isActive.setSelectedIndex(0);
		this.isActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxLovFieldDetail,this.pagingLovFieldDetailList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the LovFieldDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_LovFieldDetailList_LovFieldDetailSearchDialog(Event event) 
								throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the lovFieldDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_LovFieldDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("LovFieldDetail", getSearchObj(),this.pagingLovFieldDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.fieldCodeId.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_fieldCodeId.getSelectedItem(),
					this.fieldCodeId.getValue(), "FieldCode");
		}
		if (!StringUtils.trimToEmpty(this.fieldCodeValue.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_fieldCodeValue.getSelectedItem(),
					this.fieldCodeValue.getValue(), "FieldCodeValue");
		}
		// Active
		int intActive=0;
		if(this.isActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_isActive.getSelectedItem(),intActive, "IsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType
						.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),
					"RecordType");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxLovFieldDetail,this.pagingLovFieldDetailList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}
	public LovFieldDetailService getLovFieldDetailService() {
		return this.lovFieldDetailService;
	}

	public JdbcSearchObject<LovFieldDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<LovFieldDetail> searchObj) {
		this.searchObj = searchObj;
	}
}