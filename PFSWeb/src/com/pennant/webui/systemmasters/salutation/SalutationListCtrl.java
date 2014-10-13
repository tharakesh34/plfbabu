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
 * FileName    		:  SalutationListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.salutation;

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
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.systemmasters.SalutationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.salutation.model.SalutationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/Salutation/SalutationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SalutationListCtrl extends GFCBaseListCtrl<Salutation> implements Serializable {

	private static final long serialVersionUID = 1690558052025431845L;
	private final static Logger logger = Logger.getLogger(SalutationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	   window_SalutationList; 				// autoWired
	protected Borderlayout borderLayout_SalutationList; 		// autoWired
	protected Paging 	   pagingSalutationList; 				// autoWired
	protected Listbox 	   listBoxSalutation; 					// autoWired

	// List headers
	protected Listheader   listheader_SalutationCode; 			// autoWired
	protected Listheader   listheader_SaluationDesc; 			// autoWired
	protected Listheader   listheader_SalutationIsActive; 		// autoWired
	protected Listheader   listheader_RecordStatus; 			// autoWired
	protected Listheader   listheader_RecordType;

	protected Window  window_SalutationSearch; 				// autoWired
	protected Textbox salutationCode; 						// autoWired
	protected Listbox sortOperator_salutationCode; 			// autoWired
	protected Textbox saluationDesc; 						// autoWired
	protected Listbox sortOperator_saluationDesc; 			// autoWired
	protected Checkbox salutationIsActive; 					// autoWired
	protected Listbox sortOperator_salutationIsActive;  	// autoWired
	protected Textbox recordStatus; 						// autoWired
	protected Listbox recordType;							// autoWired
	protected Listbox sortOperator_recordStatus; 			// autoWired
	protected Listbox sortOperator_recordType; 				// autoWired

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_SalutationList_NewSalutation; 			// autoWired
	protected Button button_SalutationList_SalutationSearchDialog; 	// autoWired
	protected Button button_SalutationList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Salutation> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient SalutationService salutationService;
	private transient WorkFlowDetails   workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SalutationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SalutationCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalutationList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Salutation");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Salutation");

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
		this.sortOperator_salutationCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_salutationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_saluationDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_saluationDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_salutationIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_salutationIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_SalutationList.setHeight(getBorderLayoutHeight());
		this.listBoxSalutation.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingSalutationList.setPageSize(getListRows());
		this.pagingSalutationList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_SalutationCode.setSortAscending(new FieldComparator("salutationCode", true));
		this.listheader_SalutationCode.setSortDescending(new FieldComparator("salutationCode", false));
		this.listheader_SaluationDesc.setSortAscending(new FieldComparator("saluationDesc", true));
		this.listheader_SaluationDesc.setSortDescending(new FieldComparator("saluationDesc", false));
		this.listheader_SalutationIsActive.setSortAscending(new FieldComparator("salutationIsActive", true));
		this.listheader_SalutationIsActive.setSortDescending(new FieldComparator("salutationIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Salutation>(Salutation.class, getListRows());
		this.searchObj.addSort("SalutationCode",false);
		this.searchObj.addField("salutationCode");
		this.searchObj.addField("saluationDesc");
		this.searchObj.addField("salutationIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSalutations_View");
			if (isFirstTask()) {
				button_SalutationList_NewSalutation.setVisible(true);
			} else {
				button_SalutationList_NewSalutation.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTSalutations_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SalutationList_NewSalutation.setVisible(false);
			this.button_SalutationList_SalutationSearchDialog.setVisible(false);
			this.button_SalutationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxSalutation.setItemRenderer(new SalutationListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SalutationList");

		this.button_SalutationList_NewSalutation.setVisible(getUserWorkspace()
				.isAllowed("button_SalutationList_NewSalutation"));
		this.button_SalutationList_SalutationSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SalutationList_SalutationFindDialog"));
		this.button_SalutationList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SalutationList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.salutation.model.
	 * SalutationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSalutationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Salutation object
		final Listitem item = this.listBoxSalutation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Salutation aSalutation = (Salutation) item.getAttribute("data");
			final Salutation salutation = getSalutationService().getSalutationById(aSalutation.getId());

			if (salutation == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aSalutation.getSalutationCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_SalutationCode")	+ ":" + aSalutation.getSalutationCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND SalutationCode='" + salutation.getSalutationCode() 
				+ "' AND version=" + salutation.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Salutation", whereCond, salutation.getTaskId(), salutation.getNextTaskId());
					if (userAcces) {
						showDetailView(salutation);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(salutation);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Salutation dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SalutationList_NewSalutation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Salutation object, We GET it from the backEnd.
		final Salutation aSalutation = getSalutationService().getNewSalutation();
		showDetailView(aSalutation);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Salutation
	 *            (aSalutation)
	 * @throws Exception
	 */
	private void showDetailView(Salutation aSalutation) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aSalutation.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSalutation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("salutation", aSalutation);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SalutationListbox from the
		 * dialog when we do a delete, edit or insert a Salutation.
		 */
		map.put("salutationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Salutation/SalutationDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_SalutationList);
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
		this.sortOperator_salutationCode.setSelectedIndex(0);
		this.salutationCode.setValue("");
		this.sortOperator_saluationDesc.setSelectedIndex(0);
		this.saluationDesc.setValue("");
		this.sortOperator_salutationIsActive.setSelectedIndex(0);
		this.salutationIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxSalutation, this.pagingSalutationList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Salutation dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SalutationList_SalutationSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch(); 
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the salutation print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SalutationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Salutation", getSearchObj(),this.pagingSalutationList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.salutationCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_salutationCode.getSelectedItem(),this.salutationCode.getValue(), "SalutationCode");
		}
		if (!StringUtils.trimToEmpty(this.saluationDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_saluationDesc.getSelectedItem(),this.saluationDesc.getValue(), "SaluationDesc");
		}

		int intActive=0;
		if(this.salutationIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_salutationIsActive.getSelectedItem(),intActive, "SalutationIsActive");

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
		getPagedListWrapper().init(this.searchObj, this.listBoxSalutation,this.pagingSalutationList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSalutationService(SalutationService salutationService) {
		this.salutationService = salutationService;
	}
	public SalutationService getSalutationService() {
		return this.salutationService;
	}

	public JdbcSearchObject<Salutation> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Salutation> searchObj) {
		this.searchObj = searchObj;
	}
}