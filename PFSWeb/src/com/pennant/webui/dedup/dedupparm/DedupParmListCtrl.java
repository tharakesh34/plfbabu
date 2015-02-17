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
 * FileName    		:  DedupParmListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.dedup.dedupparm;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.dedup.dedupparm.model.DedupParmListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/DedupParm/DedupParmList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DedupParmListCtrl extends GFCBaseListCtrl<DedupParm> implements Serializable {
	
	private static final long serialVersionUID = -2577445041575201178L;
	private final static Logger logger = Logger.getLogger(DedupParmListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DedupParmList; 			     // autoWired
	protected Borderlayout 	borderLayout_DedupParmList; 	     // autoWired
	protected Paging 		pagingDedupParmList; 			     // autoWired
	protected Listbox 		listBoxDedupParm; 				     // autoWired
	protected Textbox 		queryModule;						 // autoWired

	// List headers
	protected Listheader listheader_QueryCode; 			         // autoWired
	protected Listheader listheader_QueryDesc; 			         // autoWired
	protected Listheader listheader_CustCtgCode; 		         // autoWired	
	protected Listheader listheader_RecordStatus; 		         // autoWired
	protected Listheader listheader_RecordType;
	
	//Search
	protected Textbox queryCode; 				// autoWired
	protected Listbox sortOperator_queryCode; 	// autoWired
	protected Textbox queryDesc; 				// autoWired
	protected Listbox sortOperator_queryDesc; 	// autoWired
	protected Textbox queryModules; 			// autoWired
	protected Listbox sortOperator_queryModule; // autoWired
	protected Textbox sQLQuery; 				// autoWired
	protected Combobox querySubCode;				// autoWired
	protected Listbox sortOperator_querySubCode;// autoWired 
	protected Listbox sortOperator_sQLQuery; 	// autoWired
	protected Textbox recordStatus; 			// autoWired
	protected Listbox recordType;				// autoWired
	protected Listbox sortOperator_recordStatus;// autoWired
	protected Listbox sortOperator_recordType; 	// autoWired
	protected  Row    row_AlwWorkflow;
	
	protected Grid	                       searchGrid;	
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;

	// Check Rights
	protected Button btnHelp; 									 // autoWired
	protected Button button_DedupParmList_NewDedupParm; 		 // autoWired
	protected Button button_DedupParmList_DedupParmSearchDialog; // autoWired
	protected Button button_DedupParmList_PrintList; 			 // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DedupParm> searchObj;
	private transient DedupParmService dedupParmService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public DedupParmListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DedupParam object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DedupParmList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DedupParm");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DedupParm");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		
		this.sortOperator_queryCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_queryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_queryDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_queryDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_queryModule.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_queryModule.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_querySubCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_querySubCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		fillComboBox(this.querySubCode, "", PennantStaticListUtil.getCategoryType(), "");
	
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);	
		}else{
			this.row_AlwWorkflow.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_DedupParmList.setHeight(getBorderLayoutHeight());
		this.listBoxDedupParm.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingDedupParmList.setPageSize(getListRows());
		this.pagingDedupParmList.setDetailed(true);

		this.listheader_QueryCode.setSortAscending(new FieldComparator("queryCode", true));
		this.listheader_QueryCode.setSortDescending(new FieldComparator("queryCode", false));
				
		this.listheader_QueryDesc.setSortAscending(new FieldComparator("queryDesc", true));
		this.listheader_QueryDesc.setSortDescending(new FieldComparator("queryDesc", false));
		
		this.listheader_CustCtgCode.setSortAscending(new FieldComparator("querySubCode", true));
		this.listheader_CustCtgCode.setSortDescending(new FieldComparator("querySubCode", false));
		
		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));

			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		// set the itemRenderer
		this.listBoxDedupParm.setItemRenderer(new DedupParmListModelItemRenderer());
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_DedupParmList_NewDedupParm.setVisible(false);
			this.button_DedupParmList_DedupParmSearchDialog.setVisible(false);
			this.button_DedupParmList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
	
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("DedupParmList");

		this.button_DedupParmList_NewDedupParm.setVisible(getUserWorkspace().
				isAllowed("button_DedupParmList_New"+this.queryModule.getValue()+"Dedup"));
		this.button_DedupParmList_DedupParmSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_DedupParmList_"+this.queryModule.getValue()+"DedupFindDialog"));
		this.button_DedupParmList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_DedupParmList_"+this.queryModule.getValue()+"PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.dedup.dedupparm.model.DedupParmListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDedupParmItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the selected DedupParm object
		final Listitem item = this.listBoxDedupParm.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DedupParm aDedupParm = (DedupParm) item.getAttribute("data");
			final DedupParm dedupParm = getDedupParmService().getDedupParmById(
					aDedupParm.getQueryCode(),aDedupParm.getQueryModule(),aDedupParm.getQuerySubCode());

			if (dedupParm == null) {
				String[] valueParm = new String[3];
				String[] errParm = new String[3];

				valueParm[0] = aDedupParm.getQueryCode();
				valueParm[1] = aDedupParm.getQueryModule();
				if(aDedupParm.getQueryModule().equalsIgnoreCase(PennantConstants.DedupCust) ||
						aDedupParm.getQueryModule().equalsIgnoreCase(PennantConstants.DedupBlackList)){
					valueParm[2] = PennantAppUtil.getlabelDesc(aDedupParm.getQuerySubCode(), PennantStaticListUtil.getCategoryType());
				}
				
				errParm[0] = PennantJavaUtil.getLabel("label_QueryCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_QueryModule") + ":" + valueParm[1];
				if(aDedupParm.getQueryModule().equalsIgnoreCase(PennantConstants.DedupCust) ||
						aDedupParm.getQueryModule().equalsIgnoreCase(PennantConstants.DedupBlackList)){
					errParm[2] = PennantJavaUtil.getLabel("label_QuerySubCode") + ":" + valueParm[2];
				}

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005",errParm, valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				dedupParm.setNewRecord(false);
				if (isWorkFlowEnabled()) {
					String whereCond = " AND QueryCode='"+ dedupParm.getQueryCode() + 
										"' AND QueryModule='"+ dedupParm.getQueryModule() + 
										"' AND QuerySubCode='"+ dedupParm.getQuerySubCode() + 
										"' AND version="+ dedupParm.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), 
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),"DedupParm", 
							whereCond, dedupParm.getTaskId(),dedupParm.getNextTaskId());
					if (userAcces) {
						showDetailView(dedupParm);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(dedupParm);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the DedupParm dialog with a new empty entry. <br>
	 */
	public void onClick$button_DedupParmList_NewDedupParm(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new DedupParm object, We GET it from the backEnd.
		final DedupParm aDedupParm = getDedupParmService().getNewDedupParm();
		aDedupParm.setNewRecord(true);
		showDetailView(aDedupParm);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param DedupParm
	 *            (aDedupParm)
	 * @throws Exception
	 */
	private void showDetailView(DedupParm aDedupParm) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aDedupParm.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDedupParm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aDedupParm.setQueryModule(this.queryModule.getValue());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("queryModule", this.queryModule.getValue());
		map.put("dedupParm", aDedupParm);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the DedupParmListbox from the
		 * dialog when we do a delete, edit or insert a DedupParm.
		 */
		map.put("dedupParmListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/DedupParm/DedupParmDialog.zul", null,map);
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
		PTMessageUtils.showHelpWindow(event, window_DedupParmList);
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
		this.sortOperator_queryCode.setSelectedIndex(0);
		this.queryCode.setValue("");
		this.sortOperator_queryDesc.setSelectedIndex(0);
		this.queryDesc.setValue("");
		this.sortOperator_queryModule.setSelectedIndex(0);
		this.queryModules.setValue("");
		this.sortOperator_querySubCode.setSelectedIndex(0);
		this.querySubCode.setSelectedIndex(0);
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the DedupParm dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DedupParmList_DedupParmSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the dedupParm print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_DedupParmList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("DedupParm", getSearchObj(),this.pagingDedupParmList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<DedupParm>(DedupParm.class, getListRows());
		this.searchObj.addSort("QueryCode", false);
		this.searchObj.addTabelName("DedupParams_View");
		this.searchObj.addFilterEqual("QueryModule", this.queryModule.getValue());
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_DedupParmList_NewDedupParm.setVisible(true);
			} else {
				button_DedupParmList_NewDedupParm.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("DedupParams_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("DedupParams_AView");
		}
		// De-dup parameter query code
		if (!StringUtils.trimToEmpty(this.queryCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_queryCode.getSelectedItem(), this.queryCode.getValue(), "queryCode");
		}
		
		// De-dup parameter query module

		if (!StringUtils.trimToEmpty(this.queryModules.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_queryModule.getSelectedItem(), this.queryModules.getValue(), "queryModule");
		}
		// De-dup parameter query desc
		if (!StringUtils.trimToEmpty(this.queryDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_queryDesc.getSelectedItem(), this.queryDesc.getValue(), "queryDesc");
		}
		// De-dup parameter query code
		if (this.querySubCode.getValue()!= null && !PennantConstants.List_Select.equals(this.querySubCode.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_querySubCode.getSelectedItem(), this.querySubCode.getSelectedItem().getValue().toString(), "querySubCode");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}
		// Record Type
		if (this.recordType.getSelectedItem()!= null && !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxDedupParm, this.pagingDedupParmList);
		logger.debug("Leaving" );
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}
	public DedupParmService getDedupParmService() {
		return this.dedupParmService;
	}

	public JdbcSearchObject<DedupParm> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<DedupParm> searchObj) {
		this.searchObj = searchObj;
	}
}