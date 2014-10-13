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
 * FileName    		:  GeneralDesignationListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.generaldesignation;

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
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.service.systemmasters.GeneralDesignationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.generaldesignation.model.GeneralDesignationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GeneralDesignation/GeneralDesignationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GeneralDesignationListCtrl extends	GFCBaseListCtrl<GeneralDesignation> implements Serializable {

	private static final long serialVersionUID = -1695611844309365191L;
	private final static Logger logger = Logger.getLogger(GeneralDesignationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_GeneralDesignationList; 		// autoWired
	protected Borderlayout 	borderLayout_GeneralDesignationList;// autoWired
	protected Paging 		pagingGeneralDesignationList; 		// autoWired
	protected Listbox 		listBoxGeneralDesignation; 			// autoWired

	protected Textbox genDesignation; 					// autoWired
	protected Listbox sortOperator_genDesignation; 		// autoWired
	protected Textbox genDesgDesc; 						// autoWired
	protected Listbox sortOperator_genDesgDesc; 		// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType; 						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_GenDesignation; // autoWired
	protected Listheader listheader_GenDesgDesc; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 														// autoWired
	protected Button button_GeneralDesignationList_NewGeneralDesignation; 			// autoWired
	protected Button button_GeneralDesignationList_GeneralDesignationSearchDialog; 	// autoWired
	protected Button button_GeneralDesignationList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<GeneralDesignation> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient GeneralDesignationService generalDesignationService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public GeneralDesignationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected GeneralDesignation
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GeneralDesignationList(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("GeneralDesignation");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GeneralDesignation");

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
		this.sortOperator_genDesignation.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_genDesignation.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_genDesgDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_genDesgDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_GeneralDesignationList.setHeight(getBorderLayoutHeight());
		this.listBoxGeneralDesignation.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingGeneralDesignationList.setPageSize(getListRows());
		this.pagingGeneralDesignationList.setDetailed(true);

		this.listheader_GenDesignation.setSortAscending(new FieldComparator("genDesignation", true));
		this.listheader_GenDesignation.setSortDescending(new FieldComparator("genDesignation", false));
		this.listheader_GenDesgDesc.setSortAscending(new FieldComparator("genDesgDesc", true));
		this.listheader_GenDesgDesc.setSortDescending(new FieldComparator("genDesgDesc", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(	new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<GeneralDesignation>(GeneralDesignation.class, getListRows());
		this.searchObj.addSort("GenDesignation",false);
		this.searchObj.addField("genDesignation");
		this.searchObj.addField("genDesgDesc");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTGenDesignations_View");
			if (isFirstTask()) {
				button_GeneralDesignationList_NewGeneralDesignation.setVisible(true);
			} else {
				button_GeneralDesignationList_NewGeneralDesignation.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("RMTGenDesignations_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_GeneralDesignationList_NewGeneralDesignation.setVisible(false);
			this.button_GeneralDesignationList_GeneralDesignationSearchDialog.setVisible(false);
			this.button_GeneralDesignationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxGeneralDesignation.setItemRenderer(new GeneralDesignationListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("GeneralDesignationList");
		this.button_GeneralDesignationList_NewGeneralDesignation.setVisible(getUserWorkspace()
				.isAllowed("button_GeneralDesignationList_NewGeneralDesignation"));
		this.button_GeneralDesignationList_GeneralDesignationSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_GeneralDesignationList_GeneralDesignationFindDialog"));
		this.button_GeneralDesignationList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_GeneralDesignationList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.generaldesignation.model.
	 * GeneralDesignationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onGeneralDesignationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected GeneralDesignation object
		final Listitem item = this.listBoxGeneralDesignation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final GeneralDesignation aGeneralDesignation = (GeneralDesignation) item.getAttribute("data");
			final GeneralDesignation generalDesignation = getGeneralDesignationService()
								.getGeneralDesignationById(aGeneralDesignation.getId());
			if(generalDesignation==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aGeneralDesignation.getGenDesignation();
				errParm[0] = PennantJavaUtil.getLabel("label_GenDesignation") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond = " AND GenDesignation='"+ generalDesignation.getGenDesignation() +
				"' AND version="+ generalDesignation.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "GeneralDesignation",
							whereCond, generalDesignation.getTaskId(),generalDesignation.getNextTaskId());
					if (userAcces) {
						showDetailView(generalDesignation);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(generalDesignation);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the GeneralDesignation dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GeneralDesignationList_NewGeneralDesignation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new GeneralDesignation object, We GET it from the backEnd.
		final GeneralDesignation aGeneralDesignation = getGeneralDesignationService().getNewGeneralDesignation();
		showDetailView(aGeneralDesignation);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param GeneralDesignation
	 *            (aGeneralDesignation)
	 * @throws Exception
	 */
	private void showDetailView(GeneralDesignation aGeneralDesignation) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aGeneralDesignation.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aGeneralDesignation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("generalDesignation", aGeneralDesignation);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the GeneralDesignationListbox from
		 * the dialog when we do a delete, edit or insert a GeneralDesignation.
		 */
		map.put("generalDesignationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/GeneralDesignation/GeneralDesignationDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_GeneralDesignationList);
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
		this.sortOperator_genDesgDesc.setSelectedIndex(0);
		this.genDesgDesc.setValue("");
		this.sortOperator_genDesignation.setSelectedIndex(0);
		this.genDesignation.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxGeneralDesignation,this.pagingGeneralDesignationList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the GeneralDesignation dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GeneralDesignationList_GeneralDesignationSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When the generalDesignation print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_GeneralDesignationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("GeneralDesignation", 
				getSearchObj(),this.pagingGeneralDesignationList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.genDesignation.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_genDesignation.getSelectedItem(),this.genDesignation.getValue(), "GenDesignation");
		}
		if (!StringUtils.trimToEmpty(this.genDesgDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_genDesgDesc.getSelectedItem(),this.genDesgDesc.getValue(), "GenDesgDesc");
		}


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
		getPagedListWrapper().init(this.searchObj, this.listBoxGeneralDesignation,this.pagingGeneralDesignationList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGeneralDesignationService(GeneralDesignationService generalDesignationService) {
		this.generalDesignationService = generalDesignationService;
	}
	public GeneralDesignationService getGeneralDesignationService() {
		return this.generalDesignationService;
	}

	public JdbcSearchObject<GeneralDesignation> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<GeneralDesignation> searchObj) {
		this.searchObj = searchObj;
	}
}