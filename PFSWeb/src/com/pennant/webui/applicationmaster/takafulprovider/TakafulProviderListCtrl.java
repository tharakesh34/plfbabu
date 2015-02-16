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
 * FileName    		:  TakafulProviderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.takafulprovider;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.service.applicationmaster.TakafulProviderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmasters.takafulprovider.model.TakafulProviderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/TakafulProvider/TakafulProviderList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class TakafulProviderListCtrl extends GFCBaseListCtrl<TakafulProvider> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TakafulProviderListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_TakafulProviderList; // autowired
	protected Borderlayout borderLayout_TakafulProviderList; // autowired
	protected Paging pagingTakafulProviderList; // autowired
	protected Listbox listBoxTakafulProvider; // autowired
	
	protected Textbox takafulCode; // autowired
	protected Listbox sortOperator_TakafulCode; // autowired/

	protected Textbox takafulName; // autowired
	protected Listbox sortOperator_TakafulName; // autowired


	protected Combobox takafulType; // autowired
	protected Listbox sortOperator_TakafulType; // autowired


	protected Textbox takafulRate; // autowired
	protected Listbox sortOperator_TakafulRate; // autowired

	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired

	// List headers
	protected Listheader listheader_TakafulCode; // autowired
	protected Listheader listheader_TakafulName; // autowired
	protected Listheader listheader_TakafulType; // autowired
	protected Listheader listheader_TakafulRate; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_TakafulProviderList_NewTakafulProvider; // autowired
	protected Button button_TakafulProviderList_TakafulProviderSearch; // autowired
	protected Button button_TakafulProviderList_PrintList; // autowired
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<TakafulProvider> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid 			searchGrid;	
	
	private transient TakafulProviderService takafulProviderService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public TakafulProviderListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_TakafulProviderList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("TakafulProvider");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("TakafulProvider");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		
		this.sortOperator_TakafulCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_TakafulCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_TakafulName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_TakafulName.setItemRenderer(new SearchOperatorListModelItemRenderer());


		this.sortOperator_TakafulType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_TakafulType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_TakafulRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_TakafulRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);
			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		}else{
			this.row_AlwWorkflow.setVisible(false);
		}


		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_TakafulProviderList.setHeight(getBorderLayoutHeight());
		this.listBoxTakafulProvider.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount())); 
		
		// set the paging parameters
		this.pagingTakafulProviderList.setPageSize(getListRows());
		this.pagingTakafulProviderList.setDetailed(true);

		this.listheader_TakafulCode.setSortAscending(new FieldComparator("takafulCode", true));
		this.listheader_TakafulCode.setSortDescending(new FieldComparator("takafulCode", false));
		this.listheader_TakafulName.setSortAscending(new FieldComparator("takafulName", true));
		this.listheader_TakafulName.setSortDescending(new FieldComparator("takafulName", false));
		this.listheader_TakafulType.setSortAscending(new FieldComparator("takafulType", true));
		this.listheader_TakafulType.setSortDescending(new FieldComparator("takafulType", false));
		this.listheader_TakafulRate.setSortAscending(new FieldComparator("takafulRate", true));
		this.listheader_TakafulRate.setSortDescending(new FieldComparator("takafulRate", false));
		
		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		fillComboBox(this.takafulType, "", PennantStaticListUtil.getTakafulTypes(), "");
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<TakafulProvider>(TakafulProvider.class,getListRows());
		this.searchObj.addSort("TakafulCode", false);
		this.searchObj.addField("TakafulCode");
		this.searchObj.addField("TakafulName");
		this.searchObj.addField("TakafulType");
		this.searchObj.addField("TakafulRate");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("TakafulProvider_View");
			if (isFirstTask()) {
				button_TakafulProviderList_NewTakafulProvider.setVisible(true);
			} else {
				button_TakafulProviderList_NewTakafulProvider.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("TakafulProvider_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_TakafulProviderList_NewTakafulProvider.setVisible(false);
			this.button_TakafulProviderList_TakafulProviderSearch.setVisible(false);
			this.button_TakafulProviderList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxTakafulProvider.setItemRenderer(new TakafulProviderListModelItemRenderer());
		}

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.applicationmaster.takafulprovide.model.TakafulProviderListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onTakafulProviderItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected TakafulProvider object
		final Listitem item = this.listBoxTakafulProvider.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final TakafulProvider aTakafulProvider = (TakafulProvider) item.getAttribute("data");
			TakafulProvider takafulProvider = getTakafulProviderService().getTakafulProviderById(aTakafulProvider.getId());

			if (takafulProvider == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aTakafulProvider.getTakafulCode();
				valueParm[1] = aTakafulProvider.getTakafulName();

				errParm[0] = PennantJavaUtil.getLabel("label_TakafulCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_TakafulName") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND TakafulCode='" + takafulProvider.getTakafulCode()
				+ "' AND version=" + takafulProvider.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"TakafulProvider", whereCond, takafulProvider.getTaskId(), takafulProvider.getNextTaskId());

					if (userAcces) {
						showDetailView(takafulProvider);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(takafulProvider);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the TakafulProvider dialog with a new empty entry. <br>
	 */
	public void onClick$button_TakafulProviderList_NewTakafulProvider(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new TakafulProvider object, We GET it from the backend.
		final TakafulProvider aTakafulProvider = getTakafulProviderService().getNewTakafulProvider();
		showDetailView(aTakafulProvider);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_TakafulProviderList_TakafulProviderSearch(Event event) throws Exception {

		logger.debug("Entering" + event.toString());
		doSearch();
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
		logger.debug(event.toString());
		this.sortOperator_TakafulCode.setSelectedIndex(0);
		this.takafulCode.setValue("");
		this.sortOperator_TakafulName.setSelectedIndex(0);
		this.takafulName.setValue("");
		this.sortOperator_TakafulType.setSelectedIndex(0);
		this.takafulType.setValue("");
		this.sortOperator_TakafulRate.setSelectedIndex(0);
		this.takafulRate.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears all the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxTakafulProvider,this.pagingTakafulProviderList);

		logger.debug("Leaving");
	}

	/*
	 * Invoke Search 
	 */
	
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_TakafulProviderList);
		logger.debug("Leaving");
	}

	/**
	 * When the takafulProvider print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_TakafulProviderList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		new PTListReportUtils("TakafulProvider", getSearchObj(),this.pagingTakafulProviderList.getTotalSize()+1);
		logger.debug("Leaving");
	}
	
	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("TakafulProviderList",getRole());
		this.button_TakafulProviderList_NewTakafulProvider.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderList_NewTakafulProvider"));
		this.button_TakafulProviderList_PrintList.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderList_PrintList"));
		logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param TakafulProvider (aTakafulProvider)
	 * @throws Exception
	 */
	private void showDetailView(TakafulProvider aTakafulProvider) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aTakafulProvider.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aTakafulProvider.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("takafulProvider", aTakafulProvider);

		/*
		 * we can additi-onally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the TakafulProviderListbox from the
		 * dialog when we do a delete, edit or insert a TakafulProvider.
		 */
		map.put("takafulProviderListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/TakafulProvider/TakafulProviderDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.takafulCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_TakafulCode.getSelectedItem(),
					this.takafulCode.getValue(), "TakafulCode");
		}
		if (!StringUtils.trimToEmpty(this.takafulName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_TakafulName.getSelectedItem(),
					this.takafulName.getValue(), "TakafulName");
		}

		if (!StringUtils.trimToEmpty(this.takafulRate.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_TakafulRate.getSelectedItem(),
					this.takafulRate.getValue(), "TakafulRate");
		}
		if (this.takafulType.getSelectedItem() != null && !this.takafulType.getSelectedItem().getValue().toString().equals("#")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_TakafulType.getSelectedItem(),
					this.takafulType.getSelectedItem().getValue(), "TakafulType");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_RecordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}
		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType
						.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_RecordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),
					"RecordType");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxTakafulProvider,this.pagingTakafulProviderList);

		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setTakafulProviderService(TakafulProviderService takafulProviderService) {
		this.takafulProviderService = takafulProviderService;
	}

	public TakafulProviderService getTakafulProviderService() {
		return this.takafulProviderService;
	}

	public JdbcSearchObject<TakafulProvider> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<TakafulProvider> searchObj) {
		this.searchObj = searchObj;
	}
}