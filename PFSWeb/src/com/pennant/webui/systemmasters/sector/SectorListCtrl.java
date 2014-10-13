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
 * FileName    		:  SectorListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.sector;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zul.Decimalbox;
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
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.service.systemmasters.SectorService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.sector.model.SectorListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/Sector/SectorList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SectorListCtrl extends GFCBaseListCtrl<Sector> implements Serializable {

	private static final long serialVersionUID = -4561944744750744817L;
	private final static Logger logger = Logger.getLogger(SectorListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SectorList; 				// autoWired
	protected Borderlayout 	borderLayout_SectorList; 		// autoWired
	protected Paging 		pagingSectorList; 				// autoWired
	protected Listbox 		listBoxSector; 					// autoWired


	protected Textbox  		sectorCode; 					// autoWired
	protected Listbox  		sortOperator_sectorCode; 		// autoWired
	protected Textbox  		sectorDesc; 					// autoWired
	protected Listbox  		sortOperator_sectorDesc; 		// autoWired
	protected Decimalbox  	sectorLimit; 					// autoWired
	protected Listbox  		sortOperator_sectorLimit; 		// autoWired
	protected Checkbox 		sectorIsActive; 				// autoWired
	protected Listbox  		sortOperator_sectorIsActive; 	// autoWired
	protected Textbox  		recordStatus; 					// autoWired
	protected Listbox  		recordType;						// autoWired
	protected Listbox  		sortOperator_recordStatus; 		// autoWired
	protected Listbox  		sortOperator_recordType; 		// autoWired

	// List headers
	protected Listheader listheader_SectorCode; 			// autoWired
	protected Listheader listheader_SectorDesc; 			// autoWired
	protected Listheader listheader_SectorLimit; 			// autoWired
	protected Listheader listheader_SectorIsActive; 		// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 								// autoWired
	protected Button button_SectorList_NewSector; 			// autoWired
	protected Button button_SectorList_SectorSearchDialog;  // autoWired
	protected Button button_SectorList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Sector> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient SectorService sectorService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SectorListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SectorCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SectorList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Sector");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Sector");

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

		this.sortOperator_sectorDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_sectorDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_sectorLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_sectorLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_sectorIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_sectorIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_SectorList.setHeight(getBorderLayoutHeight());
		this.listBoxSector.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingSectorList.setPageSize(getListRows());
		this.pagingSectorList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_SectorCode.setSortAscending(new FieldComparator("sectorCode", true));
		this.listheader_SectorCode.setSortDescending(new FieldComparator("sectorCode", false));
		this.listheader_SectorDesc.setSortAscending(new FieldComparator("sectorDesc", true));
		this.listheader_SectorDesc.setSortDescending(new FieldComparator("sectorDesc", false));
		this.listheader_SectorLimit.setSortAscending(new FieldComparator("sectorLimit", true));
		this.listheader_SectorLimit.setSortDescending(new FieldComparator("sectorLimit", false));
		this.listheader_SectorIsActive.setSortAscending(new FieldComparator("sectorIsActive", true));
		this.listheader_SectorIsActive.setSortDescending(new FieldComparator("sectorIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Sector>(Sector.class, getListRows());
		this.searchObj.addSort("SectorCode", false);
		this.searchObj.addFilter(new Filter("SectorCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		this.searchObj.addField("sectorCode");
		this.searchObj.addField("sectorDesc");
		this.searchObj.addField("sectorLimit");
		this.searchObj.addField("sectorIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSectors_View");
			if (isFirstTask()) {
				button_SectorList_NewSector.setVisible(true);
			} else {
				button_SectorList_NewSector.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTSectors_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SectorList_NewSector.setVisible(false);
			this.button_SectorList_SectorSearchDialog.setVisible(false);
			this.button_SectorList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxSector.setItemRenderer(new SectorListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SectorList");

		this.button_SectorList_NewSector.setVisible(getUserWorkspace()
				.isAllowed("button_SectorList_NewSector"));
		this.button_SectorList_SectorSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SectorList_SectorFindDialog"));
		this.button_SectorList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SectorList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.bmtmasters.sector.model.SectorListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSectorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Sector object
		final Listitem item = this.listBoxSector.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Sector aSector = (Sector) item.getAttribute("data");
			final Sector sector = getSectorService().getSectorById(aSector.getId());

			if (sector == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aSector.getSectorCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_Sector_Code") + ":" + aSector.getSectorCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND SectorCode='" + sector.getSectorCode()
				+ "' AND version=" + sector.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Sector", whereCond, sector.getTaskId(), sector.getNextTaskId());
					if (userAcces) {
						showDetailView(sector);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(sector);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Sector dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SectorList_NewSector(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Sector object, We GET it from the backEnd.
		final Sector aSector = getSectorService().getNewSector();
		aSector.setSectorLimit(BigDecimal.ZERO); // initialize
		showDetailView(aSector);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Sector
	 *            (aSector)
	 * @throws Exception
	 */
	private void showDetailView(Sector aSector) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aSector.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSector.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sector", aSector);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SectorListbox from the dialog
		 * when we do a delete, edit or insert a Sector.
		 */
		map.put("sectorListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Sector/SectorDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_SectorList);
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
		this.sortOperator_sectorDesc.setSelectedIndex(0);
		this.sectorDesc.setValue("");
		this.sortOperator_sectorLimit.setSelectedIndex(0);
		this.sectorLimit.setText("");
		this.sortOperator_sectorIsActive.setSelectedIndex(0);
		this.sectorIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears All the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxSector, this.pagingSectorList);

		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the Sector dialog
	 * 
	 * @param event
	 * 
	 * @throws Exception
	 */
	public void onClick$button_SectorList_SectorSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the sector print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SectorList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Sector", getSearchObj(),this.pagingSectorList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.sectorCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_sectorCode.getSelectedItem(),this.sectorCode.getValue(), "SectorCode");
		}
		if (!StringUtils.trimToEmpty(this.sectorDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_sectorDesc.getSelectedItem(),this.sectorDesc.getValue(), "SectorDesc");
		}
		if (this.sectorLimit.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_sectorLimit.getSelectedItem(),this.sectorLimit.getValue(), "SectorLimit");
		}

		// Active
		int intActive=0;
		if(this.sectorIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_sectorIsActive.getSelectedItem(),intActive, "SectorIsActive");

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
		getPagedListWrapper().init(this.searchObj, this.listBoxSector,this.pagingSectorList);
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public void setSectorService(SectorService sectorService) {
		this.sectorService = sectorService;
	}
	public SectorService getSectorService() {
		return this.sectorService;
	}

	public JdbcSearchObject<Sector> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Sector> searchObj) {
		this.searchObj = searchObj;
	}

}