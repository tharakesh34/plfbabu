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
 * FileName    		:  GroupStatusCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.groupstatuscode;

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
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.service.systemmasters.GroupStatusCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.groupstatuscode.model.GroupStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GroupStatusCode/GroupStatusCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GroupStatusCodeListCtrl extends GFCBaseListCtrl<GroupStatusCode> implements Serializable {

	private static final long serialVersionUID = -8159846804242209891L;
	private final static Logger logger = Logger.getLogger(GroupStatusCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_GroupStatusCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_GroupStatusCodeList; 	// autoWired
	protected Paging 		pagingGroupStatusCodeList; 			// autoWired
	protected Listbox 		listBoxGroupStatusCode; 			// autoWired

	protected Textbox 	grpStsCode; 						// autoWired
	protected Listbox 	sortOperator_grpStsCode; 			// autoWired
	protected Textbox 	grpStsDescription; 					// autoWired
	protected Listbox 	sortOperator_grpStsDescription; 	// autoWired
	protected Checkbox 	grpStsIsActive; 					// autoWired
	protected Listbox 	sortOperator_grpStsIsActive; 		// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_GrpStsCode; 		// autoWired
	protected Listheader listheader_GrpStsDescription; 	// autoWired
	protected Listheader listheader_GrpStsIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_GroupStatusCodeList_NewGroupStatusCode; 			// autoWired
	protected Button button_GroupStatusCodeList_GroupStatusCodeSearchDialog;   	// autoWired
	protected Button button_GroupStatusCodeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<GroupStatusCode> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient GroupStatusCodeService groupStatusCodeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public GroupStatusCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AcademicCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GroupStatusCodeList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("GroupStatusCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GroupStatusCode");

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
		this.sortOperator_grpStsCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpStsCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_grpStsDescription.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpStsDescription.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_grpStsIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_grpStsIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_GroupStatusCodeList.setHeight(getBorderLayoutHeight());
		this.listBoxGroupStatusCode.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingGroupStatusCodeList.setPageSize(getListRows());
		this.pagingGroupStatusCodeList.setDetailed(true);

		this.listheader_GrpStsCode.setSortAscending(new FieldComparator("grpStsCode", true));
		this.listheader_GrpStsCode.setSortDescending(new FieldComparator("grpStsCode", false));
		this.listheader_GrpStsDescription.setSortAscending(new FieldComparator("grpStsDescription", true));
		this.listheader_GrpStsDescription.setSortDescending(new FieldComparator("grpStsDescription",false));
		this.listheader_GrpStsIsActive.setSortAscending(new FieldComparator("grpStsIsActive", true));
		this.listheader_GrpStsIsActive.setSortDescending(new FieldComparator("grpStsIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<GroupStatusCode>(GroupStatusCode.class, getListRows());
		this.searchObj.addSort("GrpStsCode",false);
		this.searchObj.addFilter(new Filter("GrpStsCode",PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		this.searchObj.addField("grpStsCode");
		this.searchObj.addField("grpStsDescription");
		this.searchObj.addField("grpStsIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTGrpStatusCodes_View");
			if (isFirstTask()) {
				button_GroupStatusCodeList_NewGroupStatusCode.setVisible(true);
			} else {
				button_GroupStatusCodeList_NewGroupStatusCode.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTGrpStatusCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_GroupStatusCodeList_NewGroupStatusCode.setVisible(false);
			this.button_GroupStatusCodeList_GroupStatusCodeSearchDialog.setVisible(false);
			this.button_GroupStatusCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxGroupStatusCode.setItemRenderer(new GroupStatusCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("GroupStatusCodeList");

		this.button_GroupStatusCodeList_NewGroupStatusCode.setVisible(getUserWorkspace()
				.isAllowed("button_GroupStatusCodeList_NewGroupStatusCode"));
		this.button_GroupStatusCodeList_GroupStatusCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_GroupStatusCodeList_GroupStatusCodeFindDialog"));
		this.button_GroupStatusCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_GroupStatusCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.groupstatuscode.model.
	 * GroupStatusCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onGroupStatusCodeItemDoubleClicked(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected GroupStatusCode object
		final Listitem item = this.listBoxGroupStatusCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final GroupStatusCode aGroupStatusCode = (GroupStatusCode) item.getAttribute("data");
			final GroupStatusCode groupStatusCode = getGroupStatusCodeService().getGroupStatusCodeById(aGroupStatusCode.getId());

			if (groupStatusCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aGroupStatusCode.getGrpStsCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_GrpStsCode")+ ":" + aGroupStatusCode.getGrpStsCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND GrpStsCode='"+ groupStatusCode.getGrpStsCode() 
				+ "' AND version="+ groupStatusCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"GroupStatusCode", whereCond,groupStatusCode.getTaskId(),groupStatusCode.getNextTaskId());
					if (userAcces) {
						showDetailView(groupStatusCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(groupStatusCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the GroupStatusCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GroupStatusCodeList_NewGroupStatusCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new GroupStatusCode object, We GET it from the back end.
		final GroupStatusCode aGroupStatusCode = getGroupStatusCodeService().getNewGroupStatusCode();
		showDetailView(aGroupStatusCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param GroupStatusCode
	 *            (aGroupStatusCode)
	 * @throws Exception
	 */
	private void showDetailView(GroupStatusCode aGroupStatusCode)throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aGroupStatusCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aGroupStatusCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("groupStatusCode", aGroupStatusCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the GroupStatusCodeListbox from
		 * the dialog when we do a delete, edit or insert a GroupStatusCode.
		 */
		map.put("groupStatusCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/GroupStatusCode/GroupStatusCodeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_GroupStatusCodeList);
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
		this.sortOperator_grpStsCode.setSelectedIndex(0);
		this.grpStsCode.setValue("");
		this.sortOperator_grpStsDescription.setSelectedIndex(0);
		this.grpStsDescription.setValue("");
		this.sortOperator_grpStsIsActive.setSelectedIndex(0);
		this.grpStsIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxGroupStatusCode,this.pagingGroupStatusCodeList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the GroupStatusCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GroupStatusCodeList_GroupStatusCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the groupStatusCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_GroupStatusCodeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("GroupStatusCode", getSearchObj(),this.pagingGroupStatusCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.grpStsCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_grpStsCode.getSelectedItem(),this.grpStsCode.getValue(), "GrpStsCode");
		}
		if (!StringUtils.trimToEmpty(this.grpStsDescription.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_grpStsDescription.getSelectedItem(),this.grpStsDescription.getValue(), "GrpStsDescription");
		}

		// Active
		int intActive=0;
		if(this.grpStsIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_grpStsIsActive.getSelectedItem(),intActive, "GrpStsIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),"RecordType");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxGroupStatusCode,this.pagingGroupStatusCodeList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGroupStatusCodeService(
			GroupStatusCodeService groupStatusCodeService) {
		this.groupStatusCodeService = groupStatusCodeService;
	}
	public GroupStatusCodeService getGroupStatusCodeService() {
		return this.groupStatusCodeService;
	}

	public JdbcSearchObject<GroupStatusCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<GroupStatusCode> searchObj) {
		this.searchObj = searchObj;
	}
}