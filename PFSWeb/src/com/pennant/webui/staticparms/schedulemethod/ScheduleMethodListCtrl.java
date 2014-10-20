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
 * FileName    		:  ScheduleMethodListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.schedulemethod;

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
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.staticparms.ScheduleMethodService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.staticparms.schedulemethod.model.ScheduleMethodListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/ScheduleMethod/ScheduleMethodList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ScheduleMethodListCtrl extends GFCBaseListCtrl<ScheduleMethod> implements Serializable {

	private static final long serialVersionUID = -7332745886128746110L;
	private final static Logger logger = Logger.getLogger(ScheduleMethodListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ScheduleMethodList; 			// autoWired
	protected Borderlayout 	borderLayout_ScheduleMethodList; 	// autoWired
	protected Paging 		pagingScheduleMethodList; 			// autoWired
	protected Listbox 		listBoxScheduleMethod; 				// autoWired

	protected Textbox schdMethod; 							// autoWired
	protected Listbox sortOperator_schdMethod; 				// autoWired
	protected Textbox schdMethodDesc; 						// autoWired
	protected Listbox sortOperator_schdMethodDesc; 			// autoWired
	protected Textbox recordStatus;	 						// autoWired
	protected Listbox recordType; 							// autoWired
	protected Listbox sortOperator_recordStatus; 			// autoWired
	protected Listbox sortOperator_recordType; 				// autoWired

	// List headers
	protected Listheader listheader_SchdMethod; 				// autoWired
	protected Listheader listheader_SchdMethodDesc; 			// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autoWired
	protected Button button_ScheduleMethodList_NewScheduleMethod; 			// autoWired
	protected Button button_ScheduleMethodList_ScheduleMethodSearchDialog; 	// autoWired
	protected Button button_ScheduleMethodList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ScheduleMethod> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;

	private transient ScheduleMethodService scheduleMethodService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public ScheduleMethodListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ScheduleMethod object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScheduleMethodList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ScheduleMethod");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ScheduleMethod");

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

		this.sortOperator_schdMethod.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_schdMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_schdMethodDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_schdMethodDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordStatus.setSelectedIndex(0);
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

		this.borderLayout_ScheduleMethodList.setHeight(getBorderLayoutHeight());
		this.listBoxScheduleMethod.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingScheduleMethodList.setPageSize(getListRows());
		this.pagingScheduleMethodList.setDetailed(true);

		this.listheader_SchdMethod.setSortAscending(new FieldComparator("schdMethod", true));
		this.listheader_SchdMethod.setSortDescending(new FieldComparator("schdMethod", false));
		this.listheader_SchdMethodDesc.setSortAscending(new FieldComparator("schdMethodDesc", true));
		this.listheader_SchdMethodDesc.setSortDescending(new FieldComparator("schdMethodDesc", false));

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
		this.searchObj = new JdbcSearchObject<ScheduleMethod>(ScheduleMethod.class, getListRows());
		this.searchObj.addSort("SchdMethod", false);
		this.searchObj.addField("schdMethod");
		this.searchObj.addField("schdMethodDesc");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSchdMethod_View");
			if (isFirstTask()) {
				button_ScheduleMethodList_NewScheduleMethod.setVisible(true);
			} else {
				button_ScheduleMethodList_NewScheduleMethod.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}else {
			this.searchObj.addTabelName("BMTSchdMethod_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_ScheduleMethodList_NewScheduleMethod.setVisible(false);
			this.button_ScheduleMethodList_ScheduleMethodSearchDialog.setVisible(false);
			this.button_ScheduleMethodList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxScheduleMethod.setItemRenderer(new ScheduleMethodListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ScheduleMethodList");

		this.button_ScheduleMethodList_NewScheduleMethod.setVisible(getUserWorkspace()
				.isAllowed("button_ScheduleMethodList_NewScheduleMethod"));
		this.button_ScheduleMethodList_ScheduleMethodSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ScheduleMethodList_ScheduleMethodFindDialog"));
		this.button_ScheduleMethodList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ScheduleMethodList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.StaticParms.schedulemethod.model.
	 * ScheduleMethodListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onScheduleMethodItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ScheduleMethod object
		final Listitem item = this.listBoxScheduleMethod.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ScheduleMethod aScheduleMethod = (ScheduleMethod) item.getAttribute("data");
			final ScheduleMethod scheduleMethod = getScheduleMethodService().getScheduleMethodById(aScheduleMethod.getId());

			if (scheduleMethod == null) {

				String[] errParm = new String[1];
				String[] valueParm = new String[1];

				valueParm[0] = aScheduleMethod.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_SchdMethod") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND SchdMethod='"+ scheduleMethod.getSchdMethod()
					+ "' AND version="+ scheduleMethod.getVersion() + " ";

					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"ScheduleMethod", whereCond,scheduleMethod.getTaskId(),scheduleMethod.getNextTaskId());
					if (userAcces) {
						showDetailView(scheduleMethod);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(scheduleMethod);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the ScheduleMethod dialog with a new empty entry. <br>
	 */
	public void onClick$button_ScheduleMethodList_NewScheduleMethod(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new ScheduleMethod object, We GET it from the backEnd.
		final ScheduleMethod aScheduleMethod = getScheduleMethodService().getNewScheduleMethod();
		showDetailView(aScheduleMethod);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param ScheduleMethod
	 *            (aScheduleMethod)
	 * @throws Exception
	 */
	private void showDetailView(ScheduleMethod aScheduleMethod)throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aScheduleMethod.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aScheduleMethod.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleMethod", aScheduleMethod);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the ScheduleMethodListbox from the
		 * dialog when we do a delete, edit or insert a ScheduleMethod.
		 */
		map.put("scheduleMethodListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/ScheduleMethod/ScheduleMethodDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_ScheduleMethodList);
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
		logger.debug("Entering" + event.toString());

		this.sortOperator_schdMethod.setSelectedIndex(0);
		this.schdMethod.setValue("");
		this.sortOperator_schdMethodDesc.setSelectedIndex(0);
		this.schdMethodDesc.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(getSearchObj(), this.listBoxScheduleMethod,this.pagingScheduleMethodList);
		logger.debug("Leaving");
	}

	/*
	 * call the ScheduleMethod dialog
	 */

	public void onClick$button_ScheduleMethodList_ScheduleMethodSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the scheduleMethod print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ScheduleMethodList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("ScheduleMethod", getSearchObj(),this.pagingScheduleMethodList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	/**
	 * Method for Searching List based on Filters
	 */
	private void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		//SchdMethod
		if (!StringUtils.trimToEmpty(this.schdMethod.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_schdMethod.getSelectedItem(),
					this.schdMethod.getValue(), "SchdMethod");
		}
		//SchdMethodDesc
		if (!StringUtils.trimToEmpty(this.schdMethodDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_schdMethodDesc.getSelectedItem(),
					this.schdMethodDesc.getValue(), "SchdMethodDesc");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxScheduleMethod,this.pagingScheduleMethodList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setScheduleMethodService(
			ScheduleMethodService scheduleMethodService) {
		this.scheduleMethodService = scheduleMethodService;
	}
	public ScheduleMethodService getScheduleMethodService() {
		return this.scheduleMethodService;
	}

	public JdbcSearchObject<ScheduleMethod> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ScheduleMethod> searchObj) {
		this.searchObj = searchObj;
	}
}