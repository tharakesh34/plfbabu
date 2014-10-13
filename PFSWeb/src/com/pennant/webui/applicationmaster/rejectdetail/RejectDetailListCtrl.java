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
 * FileName    		:  RejectDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.rejectdetail;

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
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.service.applicationmaster.RejectDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.rejectdetail.model.RejectDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/RejectDetail/RejectDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class RejectDetailListCtrl extends GFCBaseListCtrl<RejectDetail> implements Serializable {

	private static final long serialVersionUID = 858161292428279969L;
	private final static Logger logger = Logger.getLogger(RejectDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RejectDetailList; 		// autoWired
	protected Borderlayout 	borderLayout_RejectDetailList;  // autoWired
	protected Paging 		pagingRejectDetailList; 		// autoWired
	protected Listbox 		listBoxRejectDetail; 			// autoWired

	protected Textbox 	rejectCode; 							// autoWired
	protected Listbox 	sortOperator_rejectCode; 				// autoWired
	protected Textbox 	rejectDesc; 							// autoWired
	protected Listbox 	sortOperator_rejectDesc; 				// autoWired
	protected Checkbox 	rejectIsActive; 						// autoWired
	protected Listbox 	sortOperator_rejectIsActive;  			// autoWired
	protected Textbox 	recordStatus; 							// autoWired
	protected Listbox 	recordType;								// autoWired
	protected Listbox 	sortOperator_recordStatus; 				// autoWired
	protected Listbox 	sortOperator_recordType; 				// autoWired

	// List headers
	protected Listheader 	listheader_RejectCode; 			// autoWired
	protected Listheader 	listheader_RejectDesc; 			// autoWired
	protected Listheader 	listheader_RejectIsActive; 		// autoWired
	protected Listheader 	listheader_RecordStatus; 		// autoWired
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button 		btnHelp; 											// autoWired
	protected Button 		button_RejectDetailList_NewRejectDetail; 			// autoWired
	protected Button 		button_RejectDetailList_RejectDetailSearchDialog; 	// autoWired
	protected Button 		button_RejectDetailList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<RejectDetail> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient RejectDetailService rejectDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public RejectDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected RejectedCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RejectDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("RejectDetail");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RejectDetail");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		this.sortOperator_rejectCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rejectCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_rejectDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rejectDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_rejectIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_rejectIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		/* set components visible dependent of the users rights */
		doCheckRights();
		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_RejectDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxRejectDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingRejectDetailList.setPageSize(getListRows());
		this.pagingRejectDetailList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_RejectCode.setSortAscending(new FieldComparator("rejectCode", true));
		this.listheader_RejectCode.setSortDescending(new FieldComparator("rejectCode", false));
		this.listheader_RejectDesc.setSortAscending(new FieldComparator("rejectDesc", true));
		this.listheader_RejectDesc.setSortDescending(new FieldComparator("rejectDesc", false));
		this.listheader_RejectIsActive.setSortAscending(new FieldComparator("rejectIsActive", true));
		this.listheader_RejectIsActive.setSortDescending(new FieldComparator("rejectIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<RejectDetail>(RejectDetail.class,getListRows());
		this.searchObj.addSort("RejectCode", false);
		this.searchObj.addFilter(new Filter("RejectCode",PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		this.searchObj.addField("rejectCode");
		this.searchObj.addField("rejectDesc");
		this.searchObj.addField("rejectIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTRejectCodes_View");
			if (isFirstTask()) {
				button_RejectDetailList_NewRejectDetail.setVisible(true);
			} else {
				button_RejectDetailList_NewRejectDetail.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTRejectCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_RejectDetailList_NewRejectDetail.setVisible(false);
			this.button_RejectDetailList_RejectDetailSearchDialog.setVisible(false);
			this.button_RejectDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxRejectDetail.setItemRenderer(new RejectDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RejectDetailList");

		this.button_RejectDetailList_NewRejectDetail.setVisible(getUserWorkspace()
				.isAllowed("button_RejectDetailList_NewRejectDetail"));
		this.button_RejectDetailList_RejectDetailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_RejectDetailList_RejectDetailFindDialog"));
		this.button_RejectDetailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_RejectDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.rejectdetail.model.
	 * RejectDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRejectDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected RejectDetail object
		final Listitem item = this.listBoxRejectDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final RejectDetail aRejectDetail = (RejectDetail) item.getAttribute("data");
			final RejectDetail rejectDetail = getRejectDetailService().getRejectDetailById(aRejectDetail.getId());

			if (rejectDetail == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aRejectDetail.getRejectCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_RejectCode")+ ":" + aRejectDetail.getRejectCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND RejectCode='"+ rejectDetail.getRejectCode() + "'" + " AND version="
				+ rejectDetail.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"RejectDetail", whereCond,rejectDetail.getTaskId(),rejectDetail.getNextTaskId());
					if (userAcces) {
						showDetailView(rejectDetail);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(rejectDetail);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the RejectDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_RejectDetailList_NewRejectDetail(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new RejectDetail object, We GET it from the backEnd.
		final RejectDetail aRejectDetail = getRejectDetailService().getNewRejectDetail();
		showDetailView(aRejectDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param RejectDetail
	 *            (aRejectDetail)
	 * @throws Exception
	 */
	private void showDetailView(RejectDetail aRejectDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aRejectDetail.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aRejectDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rejectDetail", aRejectDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the RejectDetailListbox from the
		 * dialog when we do a delete, edit or insert a RejectDetail.
		 */
		map.put("rejectDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/RejectDetail/RejectDetailDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_RejectDetailList);
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
		this.sortOperator_rejectCode.setSelectedIndex(0);
		this.rejectCode.setValue("");
		this.sortOperator_rejectDesc.setSelectedIndex(0);
		this.rejectDesc.setValue("");
		this.sortOperator_rejectIsActive.setSelectedIndex(0);
		this.rejectIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxRejectDetail, this.pagingRejectDetailList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the RejectDetail dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_RejectDetailList_RejectDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the rejectDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_RejectDetailList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("RejectDetail", getSearchObj(),this.pagingRejectDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.rejectCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_rejectCode.getSelectedItem(),this.rejectCode.getValue(), "RejectCode");
		}
		if (!StringUtils.trimToEmpty(this.rejectDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_rejectDesc.getSelectedItem(),this.rejectDesc.getValue(), "RejectDesc");
		}

		int intActive=0;
		if(this.rejectIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_rejectIsActive.getSelectedItem(),intActive, "RejectIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxRejectDetail,this.pagingRejectDetailList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public void setRejectDetailService(RejectDetailService rejectDetailService) {
		this.rejectDetailService = rejectDetailService;
	}
	public RejectDetailService getRejectDetailService() {
		return this.rejectDetailService;
	}

	public JdbcSearchObject<RejectDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<RejectDetail> searchObj) {
		this.searchObj = searchObj;
	}

}