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
 * FileName    		:  IncomeTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.incometype;

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
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.systemmasters.IncomeTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.incometype.model.IncomeTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/IncomeType/IncomeTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class IncomeTypeListCtrl extends GFCBaseListCtrl<IncomeType> implements Serializable {

	private static final long serialVersionUID = -3522599343656178315L;
	private final static Logger logger = Logger.getLogger(IncomeTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IncomeTypeList; 			        // autoWired
	protected Borderlayout 	borderLayout_IncomeTypeList; 	        // autoWired
	protected Paging 		pagingIncomeTypeList; 			        // autoWired
	protected Listbox 		listBoxIncomeType; 				        // autoWired

	protected Textbox 	incomeTypeCode; 					// autoWired
	protected Listbox 	sortOperator_incomeTypeCode; 		// autoWired
	protected Textbox 	incomeTypeDesc; 					// autoWired
	protected Listbox 	sortOperator_incomeTypeDesc; 		// autoWired
	protected Checkbox 	incomeTypeIsActive; 				// autoWired
	protected Listbox 	sortOperator_incomeTypeIsActive;	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_IncomeExpense; 		        	// autoWired
	protected Listheader listheader_IncomeTypeCategory; 		    // autoWired
	protected Listheader listheader_IncomeTypeCode; 		        // autoWired
	protected Listheader listheader_IncomeTypeDesc; 		        // autoWired
	protected Listheader listheader_IncomeTypeIsActive; 	        // autoWired
	protected Listheader listheader_RecordStatus; 			        // autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_IncomeTypeList_NewIncomeType; 			// autoWired
	protected Button button_IncomeTypeList_IncomeTypeSearchDialog; 	// autoWired
	protected Button button_IncomeTypeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<IncomeType> searchObj;
	protected Grid  searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient IncomeTypeService incomeTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public IncomeTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected IncomeType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IncomeTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("IncomeType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("IncomeType");

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
		this.sortOperator_incomeTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_incomeTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_incomeTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_incomeTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_incomeTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_incomeTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_IncomeTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxIncomeType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingIncomeTypeList.setPageSize(getListRows());
		this.pagingIncomeTypeList.setDetailed(true);

		this.listheader_IncomeTypeCode.setSortAscending(new FieldComparator("incomeTypeCode", true));
		this.listheader_IncomeTypeCode.setSortDescending(new FieldComparator("incomeTypeCode", false));
		this.listheader_IncomeExpense.setSortAscending(new FieldComparator("IncomeExpense", true));
		this.listheader_IncomeExpense.setSortDescending(new FieldComparator("IncomeExpense", false));
		this.listheader_IncomeTypeCategory.setSortAscending(new FieldComparator("Category", true));
		this.listheader_IncomeTypeCategory.setSortDescending(new FieldComparator("Category", false));
		this.listheader_IncomeTypeDesc.setSortAscending(new FieldComparator("incomeTypeDesc", true));
		this.listheader_IncomeTypeDesc.setSortDescending(new FieldComparator("incomeTypeDesc", false));
		this.listheader_IncomeTypeIsActive.setSortAscending(new FieldComparator("incomeTypeIsActive",true));
		this.listheader_IncomeTypeIsActive.setSortDescending(new FieldComparator("incomeTypeIsActive",false));

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
		this.searchObj = new JdbcSearchObject<IncomeType>(IncomeType.class,getListRows());
		this.searchObj.addSort("IncomeExpense",false);
		this.searchObj.addField("incomeExpense");
		this.searchObj.addField("category");
		this.searchObj.addField("incomeTypeCode");
		this.searchObj.addField("incomeTypeDesc");
		this.searchObj.addField("incomeTypeIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTIncomeTypes_View");
			if (isFirstTask()) {
				button_IncomeTypeList_NewIncomeType.setVisible(true);
			} else {
				button_IncomeTypeList_NewIncomeType.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTIncomeTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_IncomeTypeList_NewIncomeType.setVisible(false);
			this.button_IncomeTypeList_IncomeTypeSearchDialog.setVisible(false);
			this.button_IncomeTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxIncomeType.setItemRenderer(new IncomeTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("IncomeTypeList");
		this.button_IncomeTypeList_NewIncomeType.setVisible(getUserWorkspace()
				.isAllowed("button_IncomeTypeList_NewIncomeType"));
		this.button_IncomeTypeList_IncomeTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_IncomeTypeList_IncomeTypeFindDialog"));
		this.button_IncomeTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_IncomeTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.incometype.model.
	 * IncomeTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onIncomeTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected IncomeType object
		final Listitem item = this.listBoxIncomeType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final IncomeType aIncomeType = (IncomeType) item.getAttribute("data");
			final IncomeType incomeType = getIncomeTypeService().getIncomeTypeById(aIncomeType.getId(),aIncomeType.getIncomeExpense(),aIncomeType.getCategory());
			if (incomeType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aIncomeType.getIncomeTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_IncomeTypeCode")+ ":" + aIncomeType.getIncomeTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND IncomeTypeCode='"+ incomeType.getIncomeTypeCode() 
				+ "' AND version="+ incomeType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"IncomeType", whereCond, incomeType.getTaskId(),incomeType.getNextTaskId());
					if (userAcces) {
						showDetailView(incomeType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(incomeType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the IncomeType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_IncomeTypeList_NewIncomeType(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new IncomeType object, We GET it from the back end.
		final IncomeType aIncomeType = getIncomeTypeService().getNewIncomeType();
		showDetailView(aIncomeType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param IncomeType
	 *            (aIncomeType)
	 * @throws Exception
	 */
	private void showDetailView(IncomeType aIncomeType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aIncomeType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aIncomeType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("incomeType", aIncomeType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the IncomeTypeListbox from the
		 * dialog when we do a delete, edit or insert a IncomeType.
		 */
		map.put("incomeTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/IncomeType/IncomeTypeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_IncomeTypeList);
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
		this.sortOperator_incomeTypeCode.setSelectedIndex(0);
		this.incomeTypeCode.setValue("");
		this.sortOperator_incomeTypeDesc.setSelectedIndex(0);
		this.incomeTypeDesc.setValue("");
		this.sortOperator_incomeTypeIsActive.setSelectedIndex(0);
		this.incomeTypeIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//  Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxIncomeType,this.pagingIncomeTypeList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the IncomeType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_IncomeTypeList_IncomeTypeSearchDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the incomeType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_IncomeTypeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("IncomeType", getSearchObj(),this.pagingIncomeTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");
		
		this.searchObj.clearFilters();
		
		if (!StringUtils.trimToEmpty(this.incomeTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_incomeTypeCode.getSelectedItem(),this.incomeTypeCode.getValue(), "IncomeTypeCode");
		}
		if (!StringUtils.trimToEmpty(this.incomeTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_incomeTypeDesc.getSelectedItem(),this.incomeTypeDesc.getValue(), "IncomeTypeDesc");
		}

		// Active
		int intActive=0;
		if(this.incomeTypeIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_incomeTypeIsActive.getSelectedItem(),intActive, "IncomeTypeIsActive");

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
		getPagedListWrapper().init(this.searchObj, this.listBoxIncomeType,this.pagingIncomeTypeList);
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setIncomeTypeService(IncomeTypeService incomeTypeService) {
		this.incomeTypeService = incomeTypeService;
	}
	public IncomeTypeService getIncomeTypeService() {
		return this.incomeTypeService;
	}

	public JdbcSearchObject<IncomeType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<IncomeType> searchObj) {
		this.searchObj = searchObj;
	}
}