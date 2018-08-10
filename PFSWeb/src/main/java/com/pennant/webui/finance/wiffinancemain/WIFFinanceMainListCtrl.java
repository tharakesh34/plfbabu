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
 * FileName    		:  WIFFinanceMainListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.wiffinancemain;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
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
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.finance.wiffinancemain.model.WIFFinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/WIFFinanceMain/WIFFinanceMainList.zul
 * file.
 */
public class WIFFinanceMainListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = 2808357374960437326L;
	private static final Logger logger = Logger.getLogger(WIFFinanceMainListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_WIFFinanceMainList; 			// autowired
	protected Borderlayout borderLayout_WIFFinanceMainList; // autowired
	protected Paging pagingWIFFinanceMainList; 				// autowired
	protected Listbox listBoxWIFFinanceMain; 				// autowired

	protected Textbox finReference; // autowired
	protected Listbox sortOperator_finReference; // autowired
	protected Textbox finType; // autowired
	protected Listbox sortOperator_finType; // autowired
	protected int     oldVar_sortOperator_finType;			// autoWired
	protected Textbox finCcy; // autowired
	protected Listbox sortOperator_finCcy; // autowired
	protected Textbox scheduleMethod; // autowired
	protected Listbox sortOperator_scheduleMethod; // autowired
	protected Textbox profitDaysBasis; // autowired
	protected Listbox sortOperator_profitDaysBasis; // autowired
	protected Datebox finStartDate; // autowired
	protected Listbox sortOperator_finStartDate; // autowired
	protected Textbox custID; // autowired
	protected Listbox sortOperator_custID; // autowired
	protected Checkbox finIsActive; // autowired
	protected Listbox sortOperator_finIsActive; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired

	// List headers
	protected Listheader listheader_FinReference; 			// autowired
	protected Listheader listheader_PromotionCode;
	protected Listheader listheader_FinType; 				// autowired
	protected Listheader listheader_FinCcy; 				// autowired
	protected Listheader listheader_ScheduleMethod; 		// autowired
	protected Listheader listheader_Amount; 				// autowired
	protected Listheader listheader_NoOfTerms; 				// autowired
	protected Listheader listheader_StartDate; 				// autowired
	protected Listheader listheader_GraceEndDate; 			// autowired
	protected Listheader listheader_MaturityDate; 			// autowired
	protected Listheader listheader_RecordStatus; 			// autowired

	// checkRights
	protected Button btnHelp; 												// autowired
	protected Button button_WIFFinanceMainList_NewWIFFinanceMain; 			// autowired
	protected Button button_WIFFinanceMainList_WIFFinanceMainSearchDialog; 	// autowired
	protected Button button_WIFFinanceMainList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;

	private transient FinanceDetailService financeDetailService;

	private Textbox loanType;//Field for Maintain Different Finance Product Types
	private boolean isFacilityWIF = false;
	/**
	 * default constructor.<br>
	 */
	public WIFFinanceMainListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		isFacilityWIF = StringUtils.trimToEmpty(this.loanType.getValue()).equals(FinanceConstants.FIN_DIVISION_FACILITY);
		
		if (isFacilityWIF) {
			moduleCode = "WIFFinanceMain";
		} else {
			moduleCode = null;
		}
		
		
	}

	public void onCreate$window_WIFFinanceMainList(Event event) throws Exception {
		logger.debug("Entering");

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_scheduleMethod.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_scheduleMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_profitDaysBasis.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_profitDaysBasis.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finStartDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finStartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
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

		this.borderLayout_WIFFinanceMainList.setHeight(getBorderLayoutHeight());
		this.listBoxWIFFinanceMain.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingWIFFinanceMainList.setPageSize(getListRows());
		this.pagingWIFFinanceMainList.setDetailed(true);

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_PromotionCode.setSortAscending(new FieldComparator("LovDescFinProduct", true));
		this.listheader_PromotionCode.setSortDescending(new FieldComparator("LovDescFinProduct", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		this.listheader_ScheduleMethod.setSortAscending(new FieldComparator("scheduleMethod", true));
		this.listheader_ScheduleMethod.setSortDescending(new FieldComparator("scheduleMethod", false));
		this.listheader_Amount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_Amount.setSortDescending(new FieldComparator("finAmount", false));
		this.listheader_NoOfTerms.setSortAscending(new FieldComparator("calTerms", true));
		this.listheader_NoOfTerms.setSortDescending(new FieldComparator("calTerms", false));
		this.listheader_StartDate.setSortAscending(new FieldComparator("finStartDate", true));
		this.listheader_StartDate.setSortDescending(new FieldComparator("finStartDate", false));
		this.listheader_GraceEndDate.setSortAscending(new FieldComparator("grcPeriodEndDate", true));
		this.listheader_GraceEndDate.setSortDescending(new FieldComparator("grcPeriodEndDate", false));
		this.listheader_MaturityDate.setSortAscending(new FieldComparator("maturityDate", true));
		this.listheader_MaturityDate.setSortDescending(new FieldComparator("maturityDate", false));

		if(isFacilityWIF){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		}

		doSearch();
		// set the itemRenderer
		this.listBoxWIFFinanceMain.setItemRenderer(new WIFFinanceMainListModelItemRenderer());
		
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("WIFFinanceMainList");
		this.button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainList_NewWIFFinanceMain"));
		this.button_WIFFinanceMainList_WIFFinanceMainSearchDialog.setVisible(true);
		this.button_WIFFinanceMainList_PrintList.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.wiffinancemain.model.WIFFinanceMainListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onWIFFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected WIFFinanceMain object
		final Listitem item = this.listBoxWIFFinanceMain.getSelectedItem();

		if (item != null) {

			boolean reqCustDetails = false;
			if(StringUtils.isNotEmpty(this.loanType.getValue()) && !this.loanType.getValue().equals(FinanceConstants.FIN_DIVISION_COMMERCIAL)){
				reqCustDetails = true;
			}

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aWIFFinanceMain = (FinanceMain) item.getAttribute("data");
			final FinanceDetail financeDetail = getFinanceDetailService().getWIFFinance(aWIFFinanceMain.getId(),reqCustDetails, FinanceConstants.FINSER_EVENT_ORG);
			if(!isFacilityWIF){
				financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(0);
			}
			if(financeDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aWIFFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				showDetailView(financeDetail);
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the WIFFinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_WIFFinanceMainList_NewWIFFinanceMain(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new WIFFinanceMain object, We GET it from the backend.
		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(true);
		aFinanceDetail.setNewRecord(true);
		if(!isFacilityWIF){
			aFinanceDetail.getFinScheduleData().getFinanceMain().setWorkflowId(0);
		}
		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("WIFFinanceMainDialogCtrl", new WIFFinanceMainDialogCtrl());
		map.put("searchObject", this.searchObj);
		map.put("financeDetail", aFinanceDetail);
		map.put("WIFFinanceMainListCtrl", this);
		map.put("loanType", this.loanType.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/WIFinanceTypeSelect.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param WIFFinanceMain (aWIFFinanceMain)
	 * @throws Exception
	 */
	private void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aWIFFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		
		if (aWIFFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aWIFFinanceMain.setWorkflowId(getWorkFlowId());
		}
		
		aWIFFinanceMain.setNewRecord(aFinanceDetail.isNewRecord());
		aFinanceDetail.getFinScheduleData().setFinanceMain(aWIFFinanceMain);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the WIFFinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a WIFFinanceMain.
		 */
		map.put("wIFFinanceMainListCtrl", this);
		map.put("loanType", this.loanType.getValue());

		// call the zul-file with the parameters packed in a map
		try {

			String productType = this.loanType.getValue();

			if(!productType.equals(FinanceConstants.FIN_DIVISION_RETAIL)){
				productType = "";
			}else{
				productType = "RETAIL";
				productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
			}
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/"+productType+"WIFFinanceMainDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		MessageUtil.showHelpWindow(event, window_WIFFinanceMainList);
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
		this.sortOperator_custID.setSelectedIndex(0);
		this.custID.setValue("");
		this.sortOperator_finCcy.setSelectedIndex(0);
		this.finCcy.setValue("");
		this.sortOperator_finIsActive.setSelectedIndex(0);
		this.finIsActive.setChecked(false);
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finStartDate.setSelectedIndex(0);
		this.finStartDate.setValue(null);
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_profitDaysBasis.setSelectedIndex(0);
		this.profitDaysBasis.setValue("");
		this.sortOperator_scheduleMethod.setSelectedIndex(0);
		this.scheduleMethod.setValue("");
		this.oldVar_sortOperator_finType=0;
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		//Clears all the filters
		this.searchObj.clearFilters();
		addDivisionFilters();
		this.pagingWIFFinanceMainList.setActivePage(0);
		this.pagingWIFFinanceMainList.setDetailed(true);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxWIFFinanceMain,this.pagingWIFFinanceMainList);

		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the WIFFinanceMain dialog
	 */

	public void onClick$button_WIFFinanceMainList_WIFFinanceMainSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the wIFFinanceMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_WIFFinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils(this.loanType.getValue()+"WIF", getSearchObj(),this.pagingWIFFinanceMainList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class,
				getListRows());
		this.searchObj.addSort("FinReference", false);
		this.searchObj.addField("finReference");
		this.searchObj.addField("finType");
		this.searchObj.addField("lovDescFinTypeName");
		this.searchObj.addField("finCcy");
		this.searchObj.addField("profitDaysBasis");
		this.searchObj.addField("custID");
		this.searchObj.addField("lovDescCustCIF");
		this.searchObj.addField("lovDescCustShrtName");
		this.searchObj.addField("finBranch");
		this.searchObj.addField("lovDescFinBranchName");
		this.searchObj.addField("LovDescFinProduct");
		this.searchObj.addField("grcPeriodEndDate");
		this.searchObj.addField("maturityDate");
		this.searchObj.addField("calTerms");
		this.searchObj.addField("scheduleMethod");
		this.searchObj.addField("finStartDate");
		this.searchObj.addField("finAmount");
		this.searchObj.addField("FinCurrAssetValue");
		this.searchObj.addField("FeeChargeAmt");
		this.searchObj.addField("InsuranceAmt");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		this.searchObj.addField("AdvEMITerms");
		this.searchObj.clearFilters();
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("WIFFinanceMain_View");
			if (isFirstTask()) {
				button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(true);
			} else {
				button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("WIFFinanceMain_View");
		}
		addDivisionFilters();
		//CustId
		if (StringUtils.isNotBlank(this.custID.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custID.getSelectedItem(),this.custID.getValue(), "CustID");
		}
		//ScheduleMethod
		if (StringUtils.isNotBlank(this.scheduleMethod.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_scheduleMethod.getSelectedItem(),this.scheduleMethod.getValue(), "ScheduleMethod");
		}

		//FinCcy
		if (StringUtils.isNotBlank(this.finCcy.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finCcy.getSelectedItem(),this.finCcy.getValue(), "FinCcy");
		}
		//FinReference
		if (StringUtils.isNotBlank(this.finReference.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finReference.getSelectedItem(),this.finReference.getValue().trim(), "FinReference");
		}
		if (StringUtils.isNotEmpty(this.finType.getValue())) {
			// get the search operator
			final Listitem itemFinType = this.sortOperator_finType.getSelectedItem();

			if (itemFinType != null) {
				final int searchOpId = ((SearchOperators) itemFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObj.addFilter(new Filter("FinType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObj.addFilter(new Filter("FinType", this.finType.getValue(), searchOpId));
				}
			}
		}
		//ProfitDayBasis
		if (StringUtils.isNotBlank(this.profitDaysBasis.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_profitDaysBasis.getSelectedItem(),this.profitDaysBasis.getValue(), "ProfitDaysBasis");
		}
		//FinStartDate
		if (this.finStartDate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finStartDate.getSelectedItem(),
					this.finStartDate.getValue(), "FinStartDate");
		}
		//FinIsActive
		int intActive=0;
		if(this.finIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_finIsActive.getSelectedItem(),intActive, "FinIsActive");

		// Record Status
		if (StringUtils.isNotBlank(recordStatus.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !"".equals(StringUtils.trimToEmpty(String.valueOf(this.recordType.getSelectedItem().getValue())))) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxWIFFinanceMain,this.pagingWIFFinanceMainList);

		logger.debug("Leaving");
	}
	public void addDivisionFilters(){
		logger.debug("Entering");
		if(StringUtils.trimToEmpty(this.loanType.getValue()).equals(FinanceConstants.FIN_DIVISION_RETAIL)){
			this.searchObj.addFilter(new Filter("LovDescFinDivisionName", FinanceConstants.FIN_DIVISION_RETAIL, Filter.OP_EQUAL));
		}else if(StringUtils.trimToEmpty(this.loanType.getValue()).equals(FinanceConstants.FIN_DIVISION_FACILITY)){
			this.searchObj.addFilter(new Filter("LovDescFinDivisionName", FinanceConstants.FIN_DIVISION_RETAIL, Filter.OP_NOT_EQUAL));
		}else if(StringUtils.trimToEmpty(this.loanType.getValue()).equals(FinanceConstants.FIN_DIVISION_COMMERCIAL)){
			this.searchObj.addFilter(new Filter("LovDescFinDivisionName", FinanceConstants.FIN_DIVISION_COMMERCIAL, Filter.OP_EQUAL));
		}
		if(isFacilityWIF){
			this.searchObj.addFilter(Filter.isNotNull("FacilityType"));
		}else{
			this.searchObj.addFilter(Filter.isNull("FacilityType"));
		}

		logger.debug("Leaving");
	}
	
	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_finType == Filter.OP_IN || this.oldVar_sortOperator_finType == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_WIFFinanceMainList, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}

		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainList, "FinanceType");
			if (dataObject instanceof String) {
				this.finType.setValue("");
			} else {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	// ************************************************************************ //
	// **On Change Events for Multi-Selection Listbox's for Search operators*** //
	// ************************************************************************ //

		
	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_finType, oldVar_sortOperator_finType, this.finType);
	}
	
	private int doChangeStringOperator(Listbox listbox,int oldOperator,Textbox textbox){

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if(oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN){
			if(!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)){
				textbox.setValue("");
			}
		}else{
			if(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN){
				textbox.setValue("");
			}
		}
		return searchOpId;

	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceMain> so) {
		this.searchObj = so;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}