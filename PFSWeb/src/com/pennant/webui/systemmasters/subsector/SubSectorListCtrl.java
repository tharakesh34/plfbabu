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
 * FileName    		:  SubSectorListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.subsector;

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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.systemmasters.SubSectorService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.subsector.model.SubSectorListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMasters/SubSector/SubSectorList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SubSectorListCtrl extends GFCBaseListCtrl<SubSector> implements Serializable {

	private static final long serialVersionUID = -244988667564615833L;
	private final static Logger logger = Logger.getLogger(SubSectorListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SubSectorList; 				// autoWired
	protected Borderlayout 	borderLayout_SubSectorList;  		// autoWired
	protected Paging 		pagingSubSectorList; 				// autoWired
	protected Listbox 		listBoxSubSector; 					// autoWired

	// List headers
	protected Listheader listheader_SectorCode; 				// autoWired
	protected Listheader listheader_SubSectorCode; 				// autoWired
	protected Listheader listheader_SubSectorDesc; 				// autoWired
	protected Listheader listheader_SubSectorIsActive; 			// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	protected Window 	window_SubSectorSearch; 			// autoWired
	protected Textbox 	sectorCode; 						// autoWired
	protected Listbox 	sortOperator_sectorCode; 			// autoWired
	protected Textbox 	subSectorCode; 						// autoWired
	protected Listbox 	sortOperator_subSectorCode; 		// autoWired
	protected Textbox 	subSectorDesc; 						// autoWired
	protected Listbox 	sortOperator_subSectorDesc; 		// autoWired
	protected Checkbox 	subSectorIsActive; 					// autoWired
	protected Listbox 	sortOperator_subSectorIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType;							// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired
	protected Panel subSectorSeekPanel; 						// autoWired
	protected Panel subSectorListPanel; 

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_SubSectorList_NewSubSector; 		// autoWired
	protected Button button_SubSectorList_SubSectorSearchDialog;// autoWired
	protected Button button_SubSectorList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SubSector> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	private transient SubSectorService subSectorService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public SubSectorListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSectorCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SubSectorList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SubSector");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SubSector");

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
		this.sortOperator_sectorCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sectorCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subSectorCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_subSectorCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subSectorDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_subSectorDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subSectorIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_subSectorIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType =setRecordType(this.recordType);
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
		this.borderLayout_SubSectorList.setHeight(getBorderLayoutHeight());
		this.listBoxSubSector.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingSubSectorList.setPageSize(getListRows());
		this.pagingSubSectorList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_SectorCode.setSortAscending(new FieldComparator("sectorCode", true));
		this.listheader_SectorCode.setSortDescending(new FieldComparator("sectorCode", false));
		this.listheader_SubSectorCode.setSortAscending(new FieldComparator("subSectorCode", true));
		this.listheader_SubSectorCode.setSortDescending(new FieldComparator("subSectorCode", false));
		this.listheader_SubSectorDesc.setSortAscending(new FieldComparator("subSectorDesc", true));
		this.listheader_SubSectorDesc.setSortDescending(new FieldComparator("subSectorDesc", false));
		this.listheader_SubSectorIsActive.setSortAscending(new FieldComparator("subSectorIsActive", true));
		this.listheader_SubSectorIsActive.setSortDescending(new FieldComparator("subSectorIsActive", false));

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
		this.searchObj = new JdbcSearchObject<SubSector>(SubSector.class, getListRows());
		this.searchObj.addSort("SectorCode", false);
		this.searchObj.addFilter(new Filter("SectorCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		this.searchObj.addField("sectorCode");
		this.searchObj.addField("subSectorCode");
		this.searchObj.addField("subSectorDesc");
		this.searchObj.addField("subSectorIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSubSectors_View");
			if (isFirstTask()) {
				button_SubSectorList_NewSubSector.setVisible(true);
			} else {
				button_SubSectorList_NewSubSector.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTSubSectors_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SubSectorList_NewSubSector.setVisible(false);
			this.button_SubSectorList_SubSectorSearchDialog.setVisible(false);
			this.button_SubSectorList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxSubSector.setItemRenderer(new SubSectorListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SubSectorList");

		this.button_SubSectorList_NewSubSector.setVisible(getUserWorkspace()
				.isAllowed("button_SubSectorList_NewSubSector"));
		this.button_SubSectorList_SubSectorSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SubSectorList_SubSectorFindDialog"));
		this.button_SubSectorList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SubSectorList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.subsector.model.
	 * SubSectorListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSubSectorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected SubSector object
		final Listitem item = this.listBoxSubSector.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SubSector aSubSector = (SubSector) item.getAttribute("data");
			final SubSector subSector = getSubSectorService().getSubSectorById(aSubSector.getId(), aSubSector.getSubSectorCode());

			if (subSector == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aSubSector.getSectorCode();
				valueParm[1] = aSubSector.getSubSectorCode();

				errParm[0] = PennantJavaUtil.getLabel("label_SectorCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_SubSectorCode") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND SectorCode='" + subSector.getSectorCode() 
						+ "' AND version=" + subSector.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"SubSector", whereCond, subSector.getTaskId(), subSector.getNextTaskId());
					if (userAcces) {
						showDetailView(subSector);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(subSector);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the SubSector dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SubSectorList_NewSubSector(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new SubSector object, We GET it from the backEnd.
		final SubSector aSubSector = getSubSectorService().getNewSubSector();
		showDetailView(aSubSector);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param SubSector
	 *            (aSubSector)
	 * @throws Exception
	 */
	private void showDetailView(SubSector aSubSector) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aSubSector.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSubSector.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("subSector", aSubSector);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SubSectorListbox from the
		 * dialog when we do a delete, edit or insert a SubSector.
		 */
		map.put("subSectorListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/SubSector/SubSectorDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_SubSectorList);
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
		this.sortOperator_sectorCode.setSelectedIndex(0);
		this.sectorCode.setValue("");
		this.sortOperator_subSectorCode.setSelectedIndex(0);
		this.subSectorCode.setValue("");
		this.sortOperator_subSectorDesc.setSelectedIndex(0);
		this.subSectorDesc.setValue("");
		this.sortOperator_subSectorIsActive.setSelectedIndex(0);
		this.subSectorIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears All the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxSubSector, this.pagingSubSectorList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the SubSector dialog
	 * 
	 * @param event
	 *            (Event)
	 * @throws Exception
	 */
	public void onClick$button_SubSectorList_SubSectorSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the subSector print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SubSectorList_PrintList(Event event)	throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("SubSector", getSearchObj(),this.pagingSubSectorList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");
		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.sectorCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_sectorCode.getSelectedItem(),this.sectorCode.getValue(), "SectorCode");
		}
		if (!StringUtils.trimToEmpty(this.subSectorCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_subSectorCode.getSelectedItem(),this.subSectorCode.getValue(), "subSectorCode");
		}
		if (!StringUtils.trimToEmpty(this.subSectorDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_subSectorDesc.getSelectedItem(),this.subSectorDesc.getValue(), "subSectorDesc");
		}

		// Active
		int intActive=0;
		if(this.subSectorIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_subSectorIsActive.getSelectedItem(),intActive, "subSectorIsActive");

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
		getPagedListWrapper().init(this.searchObj, this.listBoxSubSector,this.pagingSubSectorList);
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	
	public void setSubSectorService(SubSectorService subSectorService) {
		this.subSectorService = subSectorService;
	}
	public SubSectorService getSubSectorService() {
		return this.subSectorService;
	}

	public JdbcSearchObject<SubSector> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SubSector> searchObj) {
		this.searchObj = searchObj;
	}

}