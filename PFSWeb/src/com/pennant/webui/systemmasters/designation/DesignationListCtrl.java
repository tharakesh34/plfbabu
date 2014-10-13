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
 * FileName    		:  DesignationListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.designation;

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
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.systemmasters.DesignationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.designation.model.DesignationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Designation/DesignationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DesignationListCtrl extends GFCBaseListCtrl<Designation> implements Serializable {

	private static final long serialVersionUID = -4747695952669967067L;
	private final static Logger logger = Logger.getLogger(DesignationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DesignationList; 		// autoWired
	protected Borderlayout 	borderLayout_DesignationList; 	// autoWired
	protected Paging 		pagingDesignationList; 			// autoWired
	protected Listbox 		listBoxDesignation; 			// autoWired

	// List headers
	protected Listheader listheader_DesgCode; 		// autoWired
	protected Listheader listheader_DesgDesc; 		// autoWired
	protected Listheader listheader_DesgIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	protected Textbox desgCode ;
	protected Listbox sortOperator_desgCode;

	protected Listbox sortOperator_desgDesc;
	protected Textbox desgDesc;

	protected Listbox sortOperator_desgIsActive;
	protected Checkbox desgIsActive;

	protected Listbox sortOperator_recordStatus;
	protected Textbox recordStatus;

	protected Listbox sortOperator_recordType;
	protected Listbox recordType;

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_DesignationList_NewDesignation; 			// autoWired
	protected Button button_DesignationList_DesignationSearchDialog; 	// autoWired
	protected Button button_DesignationList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Designation> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient DesignationService designationService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public DesignationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Designation object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DesignationList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Designation");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Designation");

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
		this.sortOperator_desgCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_desgCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_desgDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_desgDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_desgIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_desgIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_DesignationList.setHeight(getBorderLayoutHeight());
		this.listBoxDesignation.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingDesignationList.setPageSize(getListRows());
		this.pagingDesignationList.setDetailed(true);

		this.listheader_DesgCode.setSortAscending(new FieldComparator("desgCode", true));
		this.listheader_DesgCode.setSortDescending(new FieldComparator("desgCode", false));
		this.listheader_DesgDesc.setSortAscending(new FieldComparator("desgDesc", true));
		this.listheader_DesgDesc.setSortDescending(new FieldComparator("desgDesc", false));
		this.listheader_DesgIsActive.setSortAscending(new FieldComparator("desgIsActive", true));
		this.listheader_DesgIsActive.setSortDescending(new FieldComparator("desgIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Designation>(Designation.class, getListRows());;
		this.searchObj.addSort("DesgCode",false);
		this.searchObj.addField("desgCode");
		this.searchObj.addField("desgDesc");
		this.searchObj.addField("desgIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTDesignations_View");
			if (isFirstTask()) {
				button_DesignationList_NewDesignation.setVisible(true);
			} else {
				button_DesignationList_NewDesignation.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTDesignations_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_DesignationList_NewDesignation.setVisible(false);
			this.button_DesignationList_DesignationSearchDialog.setVisible(false);
			this.button_DesignationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxDesignation.setItemRenderer(new DesignationListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("DesignationList");

		this.button_DesignationList_NewDesignation.setVisible(getUserWorkspace()
				.isAllowed("button_DesignationList_NewDesignation"));
		this.button_DesignationList_DesignationSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_DesignationList_DesignationFindDialog"));
		this.button_DesignationList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_DesignationList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.designation.model.
	 * DesignationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDesignationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Designation object
		final Listitem item = this.listBoxDesignation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Designation aDesignation = (Designation) item.getAttribute("data");
			final Designation designation = getDesignationService().getDesignationById(aDesignation.getId());

			if (designation == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aDesignation.getDesgCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_DesgCode") + ":"	+ aDesignation.getDesgCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND DesgCode='" + designation.getDesgCode() 
				+ "' AND version="	+ designation.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Designation", whereCond, designation.getTaskId(),designation.getNextTaskId());
					if (userAcces) {
						showDetailView(designation);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(designation);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Designation dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DesignationList_NewDesignation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Designation object, We GET it from the back end.
		final Designation aDesignation = getDesignationService().getNewDesignation();
		showDetailView(aDesignation);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Designation
	 *            (aDesignation)
	 * @throws Exception
	 */
	private void showDetailView(Designation aDesignation) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aDesignation.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDesignation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("designation", aDesignation);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the DesignationListbox from the
		 * dialog when we do a delete, edit or insert a Designation.
		 */
		map.put("designationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Designation/DesignationDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_DesignationList);
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
		this.sortOperator_desgCode.setSelectedIndex(0);
		this.desgCode.setValue("");
		this.sortOperator_desgDesc.setSelectedIndex(0);
		this.desgDesc.setValue("");
		this.sortOperator_desgIsActive.setSelectedIndex(0);
		this.desgIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear all the fields
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxDesignation,	this.pagingDesignationList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Designation dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DesignationList_DesignationSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the designation print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_DesignationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		 new PTListReportUtils("Designation", getSearchObj(),this.pagingDesignationList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");
		this.searchObj.clearFilters();
		if (!StringUtils.trimToEmpty(this.desgCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_desgCode.getSelectedItem(),this.desgCode.getValue(), "DesgCode");
		}
		if (!StringUtils.trimToEmpty(this.desgDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_desgDesc.getSelectedItem(),this.desgDesc.getValue(), "DesgDesc");
		}

		// Active
		int intActive=0;
		if(this.desgIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_desgIsActive.getSelectedItem(),intActive, "DesgIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType	.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxDesignation,this.pagingDesignationList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDesignationService(DesignationService designationService) {
		this.designationService = designationService;
	}
	public DesignationService getDesignationService() {
		return this.designationService;
	}

	public JdbcSearchObject<Designation> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Designation> searchObj) {
		this.searchObj = searchObj;
	}
}