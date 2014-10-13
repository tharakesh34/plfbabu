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
 * FileName    		:  BaseRateListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.baserate;

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
import org.zkoss.zul.Datebox;
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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.baserate.model.BaseRateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BaseRate/BaseRateList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BaseRateListCtrl extends GFCBaseListCtrl<BaseRate> implements Serializable {

	private static final long serialVersionUID = 8263433171238545613L;
	private final static Logger logger = Logger.getLogger(BaseRateListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BaseRateList; 		// auto wired
	protected Borderlayout 	borderLayout_BaseRateList; 	// auto wired
	protected Paging 		pagingBaseRateList; 		// auto wired
	protected Listbox 		listBoxBaseRate; 			// auto wired

	protected Textbox bRType; 						// auto wired
	protected Listbox sortOperator_bRType; 			// auto wired
	protected Datebox bREffDate; 					// auto wired
	protected Listbox sortOperator_bREffDate; 		// auto wired
	protected Decimalbox bRRate; 					// auto wired
	protected Listbox sortOperator_bRRate; 			// auto wired
	protected Textbox recordStatus; 				// auto wired
	protected Listbox recordType;					// auto wired
	protected Listbox sortOperator_recordStatus; 	// auto wired
	protected Listbox sortOperator_recordType; 		// auto wired
	// List headers
	protected Listheader listheader_BRType; 		// auto wired
	protected Listheader listheader_BREffDate; 		// auto wired
	protected Listheader listheader_BRRate; 		// auto wired
	protected Listheader listheader_RecordStatus; 	// auto wired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// auto wired
	protected Button button_BaseRateList_NewBaseRate; 			// auto wired
	protected Button button_BaseRateList_BaseRateSearchDialog; 	// auto wired
	protected Button button_BaseRateList_PrintList; 			// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<BaseRate> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient BaseRateService baseRateService;
	private transient WorkFlowDetails workFlowDetails=null;
	/**
	 * default constructor.<br>
	 */
	public BaseRateListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected BaseRate object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BaseRateList(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("BaseRate");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BaseRate");
			
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
		this.sortOperator_bRType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_bRType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_bREffDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_bREffDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_bRRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_bRRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_BaseRateList.setHeight(getBorderLayoutHeight());
		this.listBoxBaseRate.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingBaseRateList.setPageSize(getListRows());
		this.pagingBaseRateList.setDetailed(true);

		this.listheader_BRType.setSortAscending(new FieldComparator("bRType", true));
		this.listheader_BRType.setSortDescending(new FieldComparator("bRType", false));
		this.listheader_BREffDate.setSortAscending(new FieldComparator("bREffDate", true));
		this.listheader_BREffDate.setSortDescending(new FieldComparator("bREffDate", false));
		this.listheader_BRRate.setSortAscending(new FieldComparator("bRRate", true));
		this.listheader_BRRate.setSortDescending(new FieldComparator("bRRate", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<BaseRate>(BaseRate.class,getListRows());
		this.searchObj.addSort("BRType",false);
		this.searchObj.addField("bRType");
		this.searchObj.addField("lovDescBRTypeName");
		this.searchObj.addField("bREffDate");
		this.searchObj.addField("bRRate");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTBaseRates_View");
			if (isFirstTask()) {
				button_BaseRateList_NewBaseRate.setVisible(true);
			} else {
				button_BaseRateList_NewBaseRate.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTBaseRates_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_BaseRateList_NewBaseRate.setVisible(false);
			this.button_BaseRateList_BaseRateSearchDialog.setVisible(false);
			this.button_BaseRateList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxBaseRate.setItemRenderer(new BaseRateListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("BaseRateList");
		
		this.button_BaseRateList_NewBaseRate.setVisible(getUserWorkspace()
				.isAllowed("button_BaseRateList_NewBaseRate"));
		this.button_BaseRateList_BaseRateSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_BaseRateList_BaseRateFindDialog"));
		this.button_BaseRateList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_BaseRateList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.baserate.model.BaseRateListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBaseRateItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		// get the selected BaseRate object
		final Listitem item = this.listBoxBaseRate.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final BaseRate aBaseRate = (BaseRate) item.getAttribute("data");
			final BaseRate baseRate = getBaseRateService().getBaseRateById(
					aBaseRate.getBRType(),aBaseRate.getBREffDate());
			if(baseRate==null){

				String[] valueParm = new String[2];
				String[] errParm= new String[2];

				valueParm[0] = aBaseRate.getBRType();
				valueParm[1] = aBaseRate.getBREffDate().toString();

				errParm[0] = PennantJavaUtil.getLabel("label_BRType") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_BREffDate") + ":"+valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND BRType='"+ baseRate.getBRType()+
				"' AND BREffDate='" +DateUtility.formatDate(baseRate.getBREffDate(),
						PennantConstants.DBDateFormat)+"' AND version=" + baseRate.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "BaseRate",
							whereCond, baseRate.getTaskId(), baseRate.getNextTaskId());
					if (userAcces){
						showDetailView(baseRate);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(baseRate);
				}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Call the BaseRate dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BaseRateList_NewBaseRate(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		// create a new BaseRate object, We GET it from the back end.
		final BaseRate aBaseRate = getBaseRateService().getNewBaseRate();
		showDetailView(aBaseRate);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param BaseRate (aBaseRate)
	 * @throws Exception
	 */
	private void showDetailView(BaseRate aBaseRate) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aBaseRate.getWorkflowId()==0 && isWorkFlowEnabled()){
			aBaseRate.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("baseRate", aBaseRate);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the BaseRateListbox from the
		 * dialog when we do a delete, edit or insert a BaseRate.
		 */
		map.put("baseRateListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/BaseRate/BaseRateDialog.zul",null,map);
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
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_BaseRateList);
		logger.debug("Leaving"+event.toString());
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
		logger.debug("Entering"+event.toString());
		this.bRType.setValue("");
		this.sortOperator_bRType.setSelectedIndex(0);
		this.bREffDate.setValue(null);
		this.sortOperator_bREffDate.setSelectedIndex(0);
		this.bRRate.setText("");
		this.sortOperator_bRRate.setSelectedIndex(0);
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears all the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxBaseRate,this.pagingBaseRateList);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * call the BaseRate dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BaseRateList_BaseRateSearchDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doSearch();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When the baseRate print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_BaseRateList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("BaseRate", getSearchObj(),this.pagingBaseRateList.getTotalSize()+1);
		logger.debug("Leaving"+event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		//BaseRateType
		if (!StringUtils.trimToEmpty(this.bRType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_bRType.getSelectedItem(),this.bRType.getValue(), "BRType");
		}

		//EffectiveDate
		if (this.bREffDate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_bREffDate.getSelectedItem(), DateUtility.formatDate(this.bREffDate.getValue(), PennantConstants.DBDateFormat), "BREffDate");
		}

		//BaseRate
		if (this.bRRate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_bRRate.getSelectedItem(),this.bRRate.getValue(), "BRRate");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxBaseRate,this.pagingBaseRateList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setBaseRateService(BaseRateService baseRateService) {
		this.baseRateService = baseRateService;
	}
	public BaseRateService getBaseRateService() {
		return this.baseRateService;
	}

	public JdbcSearchObject<BaseRate> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BaseRate> searchObj) {
		this.searchObj = searchObj;
	}
}