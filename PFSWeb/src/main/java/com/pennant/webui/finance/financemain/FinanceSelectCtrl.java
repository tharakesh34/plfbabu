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
 * FileName    		:  FinanceSelectCtrl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.finance.FinCovenantMaintanceService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.RepaymentCancellationService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.financemain.model.FinanceMainSelectItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceSelect.zul file.
 */
public class FinanceSelectCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = -5081318673331825306L;
	private static final Logger logger = Logger.getLogger(FinanceSelectCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceSelect; 				// autowired
	protected Borderlayout	borderlayout_FinanceSelect; // autowired
	protected Textbox custCIF; 							// autowired
	protected Textbox finReference; 					// autowired
	protected Textbox finType; 							// autowired
	protected Textbox finCcy; 							// autowired
	protected Textbox finBranch;						// autowired
	protected Textbox scheduleMethod; 					// autowired
	protected Textbox profitDaysBasis; 					// autowired
	private Paging  pagingFinanceList; 					// autowired
	private Listbox listBoxFinance; 					// autowired
	protected Button  btnClose; 						// autowired
	protected Grid    grid_FinanceDetails;       		// autowired
	protected Div 	  div_ToolBar;       				// autowired

	protected Listbox sortOperator_custCIF; 			// autowired
	protected Listbox sortOperator_finReference; 		// autowired
	protected Listbox sortOperator_finType; 			// autowired
	protected Listbox sortOperator_finCcy; 				// autowired
	protected Listbox sortOperator_finBranch;			// autowired
	protected Listbox sortOperator_scheduleMethod; 		// autowired
	protected Listbox sortOperator_profitDaysBasis; 	// autowired

	protected int   oldVar_sortOperator_custCIF; 		// autowired
	protected int   oldVar_sortOperator_finReference;   // autowired
	protected int   oldVar_sortOperator_finType;		// autowired
	protected int   oldVar_sortOperator_finCcy;			// autowired
	protected int   oldVar_sortOperator_finBranch;		// autowired
	protected int   oldVar_sortOperator_scheduleMethod; // autowired
	protected int   oldVar_sortOperator_profitDaysBasis;// autowired
	
	// List headers
	protected Listheader listheader_FinType;		// autoWired
	protected Listheader listheader_FinProduct;		// autoWired
	protected Listheader listheader_CustCIF;		// autoWired
	protected Listheader listheader_FinRef;			// autoWired
	protected Listheader listheader_FinBranch;		// autoWired
	protected Listheader listheader_FinStartDate;	// autoWired
	protected Listheader listheader_NumberOfTerms;	// autoWired
	protected Listheader listheader_MaturityDate;	// autoWired
	protected Listheader listheader_FinCcy;			// autoWired
	protected Listheader listheader_FinAmount;		// autoWired
	protected Listheader listheader_CurFinAmount;	// autoWired
	protected Listheader listheader_RequestStage;	// autoWired
	protected Listheader listheader_RecordStatus;	// autoWired
	private String moduleDefiner = "";
	private String workflowCode = "";
	private String eventCodeRef = "";
	private String menuItemRightName = null;
	private Tab tab;
	private Tabbox	tabbox;

	// not auto wired vars
	private transient Object dialogCtrl   =   null;
	private transient WorkFlowDetails workFlowDetails  =  null;
	protected JdbcSearchObject<FinanceMain> searchObject;
	private List<Filter> filterList;
	protected Button btnClear;
	protected Button btnNew;
	private transient FinanceDetailService financeDetailService;
	private transient ManualPaymentService manualPaymentService;
	private transient ReceiptService receiptService;
	private transient FinanceWriteoffService financeWriteoffService;
	private transient FinanceCancellationService financeCancellationService;
	private transient FinanceMaintenanceService financeMaintenanceService;
	private transient RepaymentCancellationService repaymentCancellationService;
	private transient FinanceWorkFlowService  financeWorkFlowService;
	private transient FinanceTypeService financeTypeService;
	private transient FinCovenantMaintanceService finCovenantMaintanceService;
	
	private FinanceMain financeMain;
	private boolean isDashboard = false;
	private boolean isDetailScreen = false;
	private String buildedWhereCondition = "";
	final Map<String, Object> map = getDefaultArguments();
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	
	/**
	 * Default constructor
	 */
	public FinanceSelectCtrl() {
		super();
	}

	// Component Events

	/**
	 * Before binding the data and calling the Search window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceSelect(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if (event.getTarget() != null && event.getTarget().getParent() != null
				&& event.getTarget().getParent().getParent()!=null && 
				event.getTarget().getParent().getParent().getParent() != null && 
				event.getTarget().getParent().getParent().getParent().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			
			String menuItemName = tabbox.getSelectedTab().getId();
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");
			if (getUserWorkspace().getHasMenuRights().containsKey(menuItemName)) {
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}
			
			/* set components visible dependent on the users rights */
			doCheckRights();
			map.put("enqiryModule", super.enqiryModule);
			
			checkAndSetModDef(tabbox);
	    }
		
		//Listbox Sorting
		
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		
		this.listheader_FinProduct.setSortAscending(new FieldComparator("LovDescProductCodeName", true));
		this.listheader_FinProduct.setSortDescending(new FieldComparator("LovDescProductCodeName", false));
		
		this.listheader_CustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		
		this.listheader_FinRef.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinRef.setSortDescending(new FieldComparator("finReference", false));
		
		this.listheader_FinBranch.setSortAscending(new FieldComparator("finBranch", true));
		this.listheader_FinBranch.setSortDescending(new FieldComparator("finBranch", false));
		
		this.listheader_FinStartDate.setSortAscending(new FieldComparator("finStartDate", true));
		this.listheader_FinStartDate.setSortDescending(new FieldComparator("finStartDate", false));
		
		this.listheader_NumberOfTerms.setSortAscending(new FieldComparator("numberOfTerms", true));
		this.listheader_NumberOfTerms.setSortDescending(new FieldComparator("numberOfTerms", false));
		
		this.listheader_MaturityDate.setSortAscending(new FieldComparator("maturityDate", true));
		this.listheader_MaturityDate.setSortDescending(new FieldComparator("maturityDate", false));
		
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		
		this.listheader_FinAmount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("finAmount", false));
		
		this.listheader_CurFinAmount.setSortAscending(new FieldComparator("finRepaymentAmount", true));
		this.listheader_CurFinAmount.setSortDescending(new FieldComparator("finRepaymentAmount", false));
		
		this.listheader_RequestStage.setSortAscending(new FieldComparator("LovDescRequestStage", true));
		this.listheader_RequestStage.setSortDescending(new FieldComparator("LovDescRequestStage", false));
		
		this.listheader_RecordStatus.setSortAscending(new FieldComparator("RecordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("RecordStatus", false));

		// DropDown ListBox

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_finBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_scheduleMethod.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_scheduleMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_profitDaysBasis.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_profitDaysBasis.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (arguments.containsKey("DialogCtrl")) {
			setDialogCtrl(arguments.get("DialogCtrl"));
		}
		if (arguments.containsKey("filtersList")) {
			filterList = (List<Filter>) arguments.get("filtersList");
		}

		// Stored search object and paging
			
		if (arguments.containsKey("searchObject")) {
			searchObject = (JdbcSearchObject<FinanceMain>) arguments.get("searchObject");
		}
		this.borderlayout_FinanceSelect.setHeight(getBorderLayoutHeight());
		this.listBoxFinance.setHeight(getListBoxHeight(this.grid_FinanceDetails.getRows().getVisibleItemCount()+1));
		this.pagingFinanceList.setPageSize(getListRows());
		this.pagingFinanceList.setDetailed(true);
		
		if (searchObject != null) {
			// Render Search Object
			paging(searchObject);
			// get the filters from the searchObject
			final List<Filter> ft = searchObject.getFilters();
			for (final Filter filter : ft) {

				// restore founded properties
				if ("lovDescCustCIF".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCIF));
				} else if ("finReference".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finReference));
				} else if ("finType".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finType, filter);
					this.finType.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finType));
				} else if ("finCcy".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finCcy, filter);
					this.finCcy.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finCcy));
				}  else if ("finBranch".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finBranch, filter);
					this.finBranch.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finBranch));
				} else if ("scheduleMethod".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_scheduleMethod, filter);
					this.scheduleMethod.setValue(restoreString(filter.getValue().toString(), this.sortOperator_scheduleMethod));
				} else if ("profitDaysBasis".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_profitDaysBasis, filter);
					this.profitDaysBasis.setValue(restoreString(filter.getValue().toString(), this.sortOperator_profitDaysBasis));
				} 
			}
		}
		if (arguments.containsKey("isDashboard")) {
			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String)(arguments.get("moduleDefiner"));
			}
			this.isDashboard = true ;
			Events.postEvent("onClick$btnSearch", window_FinanceSelect,event);
			if (arguments.containsKey("detailScreen")) {
				this.isDetailScreen = true;
				if (arguments.containsKey("FinanceReference")) {
				this.finReference.setValue((String)(arguments.get("FinanceReference")));
				}
				Events.postEvent("onFinanceItemDoubleClicked", window_FinanceSelect,event);
			}
			
		}
        if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_TFPREMIUMEXCL)){
        	this.listheader_RecordStatus.setVisible(false);
        }
   
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinanceSelectList");
		logger.debug("Leaving");
	}

	/**
	 * Method for replacing LIKE '%' operator in String of SearchObject
	 * 
	 * @param filterValue
	 * @param listbox
	 * @return
	 */
	private String restoreString(String filterValue, Listbox listbox) {
		if (listbox.getSelectedIndex() == 3) {
			return StringUtils.replaceChars(filterValue, "%", "");
		}
		return filterValue;
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * OnClick Event for Close button for Closing Window
	 * 
	 * @param eventtab
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		if (tab!=null) {
			tab.close();
		}
		try {
			if(dialogCtrl != null){
				if (dialogCtrl.getClass().getMethod("closeTab") != null) {
					dialogCtrl.getClass().getMethod("closeTab").invoke(dialogCtrl);
				}
			}else{
				this.window_FinanceSelect.onClose();
			}
        } catch (Exception e) {
        	logger.error("Exception: ", e);
        }
		logger.debug("Leaving" + event.toString());
	}

	// GUI operations

	/**
	 * closes the dialog window
	 * 
	 * @throws InterruptedException
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		this.window_FinanceSelect.onClose();
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * @param event
	 */
	public void onClick$btnSearchFinRef(Event event){
		logger.debug("Entering " + event.toString());
		
		if(this.searchObject == null){
			doSearch(false);
		}

		Filter[] filters = this.searchObject.getFilters().toArray(new Filter[this.searchObject.getFilters().size()]);
		if(this.oldVar_sortOperator_finReference == Filter.OP_IN || this.oldVar_sortOperator_finReference == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "FinanceMaintenance", this.finReference.getValue(), filters,StringUtils.trimToNull(this.searchObject.getWhereClause()));
			if (selectedValues!= null) {
				this.finReference.setValue(selectedValues);
			}
			
		}else{
			Object dataObject  = ExtendedSearchListBox.show(this.window_FinanceSelect,"FinanceMaintenance",this.finReference.getValue(),filters,
					StringUtils.trimToNull(this.searchObject.getWhereClause()));

			if (dataObject instanceof String){
				this.finReference.setValue("");
			}else{
				FinanceMain details= (FinanceMain) dataObject;
				if (details != null) {
					this.finReference.setValue(details.getFinReference());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When user clicks on "btnSearchBranchCode" button
	 * This method displays ExtendedSearchListBox with branch details
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event){
		logger.debug("Entering  "+event.toString());

		if(this.oldVar_sortOperator_finBranch == Filter.OP_IN || this.oldVar_sortOperator_finBranch == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "Branch", this.finBranch.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finBranch.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect,"Branch");
			if (dataObject instanceof String){
				this.finBranch.setValue("");
			}else{
				Branch details= (Branch) dataObject;
				if (details != null) {
					this.finBranch.setValue(details.getBranchCode());
				}
			}
		}
		logger.debug("Leaving"+event.toString());
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
					this.window_FinanceSelect, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "FinanceType");
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
	
	/**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_finCcy == Filter.OP_IN || this.oldVar_sortOperator_finCcy == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "Currency", this.finCcy.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finCcy.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "Currency");
			if (dataObject instanceof String) {
				this.finCcy.setValue("");
			} else {
				Currency details = (Currency) dataObject;
				if (details != null) {
					this.finCcy.setValue(details.getCcyCode());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * When user clicks on  "btnSearchCustCIF" button
	 * @param event
	 */
	public void onClick$btnSearchSchdMethod(Event event) throws  SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		String whereClause ="";
		if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)){
			whereClause = new String( " SchdMethod NOT IN ('EQUAL','GRCNDPAY','MAN_PRI','MANUAL','PRI','PRI_PFT') ");
		}
		if(this.oldVar_sortOperator_scheduleMethod == Filter.OP_IN || this.oldVar_sortOperator_scheduleMethod == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues = null;
			if(StringUtils.isEmpty(whereClause)){
			 selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "ScheduleMethod", this.scheduleMethod.getValue(), new Filter[]{});
			}else{
				 selectedValues= (String) MultiSelectionSearchListBox.show(
							this.window_FinanceSelect, "ScheduleMethod", this.scheduleMethod.getValue(), new Filter[]{},whereClause);
			}
			if (selectedValues!= null) {
				this.scheduleMethod.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = null;
			if(StringUtils.isEmpty(whereClause)){
				dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod");
			}else{
				dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod","",whereClause);
			}
			if (dataObject instanceof String) {
				this.scheduleMethod.setValue("");
			} else {
				ScheduleMethod details = (ScheduleMethod) dataObject;
				if (details != null) {
					this.scheduleMethod.setValue(details.getSchdMethod());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When user clicks on  "btnSearchCustCIF" button
	 * @param event
	 */
	public void onClick$btnSearchPftDaysBasis(Event event) throws  SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_profitDaysBasis == Filter.OP_IN || this.oldVar_sortOperator_profitDaysBasis == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "InterestRateBasisCode", this.profitDaysBasis.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.profitDaysBasis.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "InterestRateBasisCode");
			if (dataObject instanceof String) {
				this.profitDaysBasis.setValue("");
			} else {
				InterestRateBasisCode details = (InterestRateBasisCode) dataObject;
				if (details != null) {
					this.profitDaysBasis.setValue(details.getIntRateBasisCode());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	// On Change Events for Multi-Selection Listbox's for Search operators

	public void onSelect$sortOperator_custCIF(Event event) {
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_custCIF, oldVar_sortOperator_custCIF, this.custCIF);
	}
	
	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_finBranch, oldVar_sortOperator_finBranch, this.finBranch);
	}
	
	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_finType, oldVar_sortOperator_finType, this.finType);
	}
	
	public void onSelect$sortOperator_finReference(Event event) {
		this.oldVar_sortOperator_finReference = doChangeStringOperator(sortOperator_finReference, oldVar_sortOperator_finReference, this.finReference);
	}
	
	public void onSelect$sortOperator_finCcy(Event event) {
		this.oldVar_sortOperator_finCcy = doChangeStringOperator(sortOperator_finCcy, oldVar_sortOperator_finCcy, this.finCcy);
	}
	
	public void onSelect$sortOperator_scheduleMethod(Event event) {
		this.oldVar_sortOperator_scheduleMethod = doChangeStringOperator(sortOperator_scheduleMethod, oldVar_sortOperator_scheduleMethod, this.scheduleMethod);
	}
	
	public void onSelect$sortOperator_profitDaysBasis(Event event) {
		this.oldVar_sortOperator_profitDaysBasis = doChangeStringOperator(sortOperator_profitDaysBasis, oldVar_sortOperator_profitDaysBasis, this.profitDaysBasis);
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

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	public void doSearch(boolean isFilterSearch) {
		logger.debug("Entering");
		
		getSearchObj(false);
		
		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem itemCustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (itemCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("lovDescCustCIF", "%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem itemFinReference = this.sortOperator_finReference.getSelectedItem();

			if (itemFinReference != null) {
				final int searchOpId = ((SearchOperators) itemFinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("FinReference", "%" + this.finReference.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("FinReference", this.finReference.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("FinReference", this.finReference.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinReference", this.finReference.getValue().trim(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem itemFinType = this.sortOperator_finType.getSelectedItem();

			if (itemFinType != null) {
				final int searchOpId = ((SearchOperators) itemFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("FinType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinType", this.finType.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finCcy.getValue())) {

			// get the search operator
			final Listitem itemFinCcy = this.sortOperator_finCcy.getSelectedItem();

			if (itemFinCcy != null) {
				final int searchOpId = ((SearchOperators) itemFinCcy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("FinCcy", "%" + this.finCcy.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinCcy", this.finCcy.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finBranch.getValue())) {

			// get the search operator
			final Listitem itemFinBranch = this.sortOperator_finBranch.getSelectedItem();

			if (itemFinBranch != null) {
				final int searchOpId = ((SearchOperators) itemFinBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("FinBranch", "%" + this.finBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("FinBranch", this.finBranch.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("FinBranch", this.finBranch.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinBranch", this.finBranch.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.scheduleMethod.getValue())) {

			// get the search operator
			final Listitem itemScheduleMethod = this.sortOperator_scheduleMethod.getSelectedItem();

			if (itemScheduleMethod != null) {
				final int searchOpId = ((SearchOperators) itemScheduleMethod.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("ScheduleMethod", "%" + this.scheduleMethod.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("ScheduleMethod", this.scheduleMethod.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("ScheduleMethod", this.scheduleMethod.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("ScheduleMethod", this.scheduleMethod.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.profitDaysBasis.getValue())) {

			// get the search operator
			final Listitem itemProfitDaysBasis = this.sortOperator_profitDaysBasis.getSelectedItem();

			if (itemProfitDaysBasis != null) {
				final int searchOpId = ((SearchOperators) itemProfitDaysBasis.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("ProfitDaysBasis", "%" + this.profitDaysBasis.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("ProfitDaysBasis", this.profitDaysBasis.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("ProfitDaysBasis", this.profitDaysBasis.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("ProfitDaysBasis", this.profitDaysBasis.getValue(), searchOpId));
				}
			}
		}

		// Default Sort on the table	
		Date appDate = DateUtility.getAppDate();
		StringBuilder whereClause = new StringBuilder(" FinIsActive = 1 ");
		if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFFPAY)){
			 whereClause = new StringBuilder(" FinIsActive = 0 ");
		}else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_BASICMAINTAIN)){
			 whereClause = new StringBuilder(" ");
		}
		
		if(StringUtils.isNotEmpty(buildedWhereCondition)){
			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_BASICMAINTAIN)){
				 whereClause = new StringBuilder(" ("+buildedWhereCondition +") ");
			}else{
				whereClause.append(" AND ("+buildedWhereCondition +") ");
			}
		}
		
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ROLLOVER)){
			whereClause.append(" AND (RcdMaintainSts = '"+moduleDefiner+"' ) "); 
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else {
			if (App.DATABASE == Database.ORACLE) {
				whereClause.append(" AND (RcdMaintainSts IS NULL OR RcdMaintainSts = '"+moduleDefiner+"' ) "); 
			}else{
				// for postgredb sometimes record type is null or empty('')
				whereClause.append(" AND ( (RcdMaintainSts IS NULL or RcdMaintainSts = '') OR RcdMaintainSts = '"
						+ moduleDefiner + "' ) ");
			}
		}
		
		int backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_RATECHG_BACK_DATE");
		Date backValueDate = DateUtility.addDays(appDate, backValueDays);
		
		if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RATECHG) ) {
			whereClause.append(" AND (AllowGrcPftRvw = 1 OR AllowRepayRvw = 1 OR RateChgAnyDay = 1) "); 
			whereClause.append(" AND FinCurrAssetValue > 0 "); 
			
			/*whereClause.append(" OR (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND "); 
			whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "'))");  */
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ADVRATECHG)) {
			whereClause.append(" AND (ProductCategory = '"+FinanceConstants.PRODUCT_STRUCTMUR +"')"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SUPLRENTINCRCOST)) {
			whereClause.append(" AND ProductCategory IN ( '" + FinanceConstants.PRODUCT_IJARAH +"','"+FinanceConstants.PRODUCT_FWIJARAH+"') " );
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGRPY)) {
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ADDDISB)) {
			whereClause.append(" AND AlwMultiDisb = 1  AND MaturityDate > '" + appDate + "'");  
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RLSDISB)) {
			whereClause.append(" AND AlwMultiDisb = 1  AND MaturityDate > '" + appDate + "'");  
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_POSTPONEMENT)) { 
			whereClause.append(" AND (Defferments - AvailedDefRpyChange > 0 OR RcdMaintainSts ='"+FinanceConstants.FINSER_EVENT_POSTPONEMENT+"' ) "); 
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_UNPLANEMIH)) { 
			whereClause.append(" AND (MaxUnplannedEmi - AvailedUnPlanEmi > 0 OR RcdMaintainSts ='"+FinanceConstants.FINSER_EVENT_UNPLANEMIH+"') "); 
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ADDTERM)){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RMVTERM)){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RECALCULATE)){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SUBSCHD)){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGPFT)){
			whereClause.append(" AND (AllowGrcPftRvw = 1 OR AllowRepayRvw = 1) "); 
			/*whereClause.append(" OR (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND "); 
			whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "'))");*/  
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGFRQ)){
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D +"' " );
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RESCHD)){
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D +"' " );
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)){
			if(ImplementationConstants.IMPLEMENTATION_ISLAMIC){
				whereClause.append(" AND ProductCategory IN ( '" + FinanceConstants.PRODUCT_IJARAH +"','"+FinanceConstants.PRODUCT_FWIJARAH+"') " );
			}else{
				whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
			}
			whereClause.append(" AND AllowGrcPeriod = 1");
			whereClause.append(" AND GrcPeriodEndDate >= '" + appDate+"' " );
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_COMPOUND)){
			whereClause.append(" AND (ProductCategory = '"+FinanceConstants.PRODUCT_SUKUK +"')"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)){
			whereClause.append(" AND FinStartDate < '" + appDate+"' " );
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RECEIPT)){
			//whereClause.append(" AND FinStartDate < '" + appDate+"' " );
			whereClause.append(" AND FinCurrAssetValue > 0 "); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY)){
			whereClause.append(" AND FinStartDate < '" + appDate+"' " );
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			whereClause.append(" AND FinStartDate < '" + appDate +"' " );
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)){
			whereClause.append(" AND FinStartDate < '" + appDate +"' " );
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFF)){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
			whereClause.append(" AND MaturityDate < '" + appDate + "'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFFPAY)){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELRPY)){
			//whereClause.append(" OR (FinIsActive = 0 AND ClosingStatus = 'M') ");
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELFIN)){
			backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_CANFIN_BACK_DATE");
			backValueDate = DateUtility.addDays(appDate, backValueDays);
			
			whereClause.append(" AND MigratedFinance = 0 ");
			whereClause.append(" AND (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND "); 
			whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "')");  
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_TFPREMIUMEXCL)) {
			whereClause.append(" AND FinReference IN(SELECT FinReference FROM FinFeeCharges WHERE FeeCode= 'TAKAFUL')");
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ROLLOVER)) {
			whereClause.append(" AND NextRolloverDate IS NOT NULL "); 
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELDISB)) {
			whereClause.append(" AND ( FinReference IN (select FinReference from FinDisbursementDetails where DisbDate >= '"+appDate+ "') "); 
			whereClause.append(" AND ProductCategory = '"+FinanceConstants.PRODUCT_ODFACILITY+"' )"); 
		}else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)) {
			whereClause.append(" AND FinStartDate < '" + appDate+"' AND MaturityDate > '"+ appDate+"'" );
			whereClause.append(" AND ProductCategory = '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if((moduleDefiner.equals(FinanceConstants.FINSER_EVENT_BASICMAINTAIN))){
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN)){
		}else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_INSCHANGE)) {
			whereClause.append("AND FinReference IN (select  Reference from FinInsurances where PaymentMethod="+"'"+InsuranceConstants.PAYTYPE_SCH_FRQ+"')");
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_PLANNEDEMI)){
			whereClause.append(" AND PlanEMIHAlw = 1");
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_REAGING)){
			whereClause.append(" AND (MaxReAgeHolidays - AvailedReAgeH > 0 OR RcdMaintainSts ='"+FinanceConstants.FINSER_EVENT_REAGING+"') "); 
			whereClause.append(" AND FinReference IN ( Select D.FinReference From FinODDetails D Where D.FinCurODAmt > 0 AND D.FinODSchdDate > GrcPeriodEndDate) "); 
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_HOLDEMI)) {
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'"); 
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD)) {
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D +"' AND StepFinance = 0 " );
			whereClause.append(" AND ProductCategory != '"+FinanceConstants.PRODUCT_ODFACILITY+"'");  
		}
	
		//Written Off Finance Reference Details Condition
		if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFFPAY)){
			whereClause.append(" AND FinReference IN (SELECT FinReference From FinWriteoffDetail) "); 
		} else{
			whereClause.append(" AND FinReference NOT IN (SELECT FinReference From FinWriteoffDetail) "); 
		}
		
		// Filtering added based on user branch and division
		whereClause.append(" ) AND ( " +getUsrFinAuthenticationQry(false));
		
		searchObject.addWhereClause(whereClause.toString());
		setSearchObj(searchObject);
		if(isFilterSearch){
			paging(searchObject);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Render the getting list and set the pagination
	 * 
	 * @param searchObj
	 */
	private void paging(JdbcSearchObject<FinanceMain> searchObj) {
		logger.debug("Entering");	
		getPagingFinanceList().setActivePage(0);
		getPagingFinanceList().setDetailed(true);
		getPagedListWrapper().init(searchObj, getListBoxFinance(), getPagingFinanceList());
		this.getListBoxFinance().setItemRenderer(new FinanceMainSelectItemRenderer());
		logger.debug("Leaving");
	}

	// When item double clicked
	@SuppressWarnings("rawtypes")
	public void onFinanceItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final Listitem item;
		if(isDashboard && isDetailScreen){
			List<FinanceMain> financeMainList = getPagedListWrapper().getPagedListService().getBySearchObject(this.searchObject);
			if(this.listBoxFinance != null && this.listBoxFinance.getItems().size() == 1 
					&& financeMainList != null && financeMainList.size() == 1){
				item = (Listitem)this.listBoxFinance.getFirstChild().getNextSibling();
				final FinanceMain aFinanceMain = (FinanceMain) financeMainList.get(0);
				item.setAttribute("data", aFinanceMain);
			}else{
				return;
			}
		}else{
			item = this.getListBoxFinance().getSelectedItem();
		}
		
		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			int eodProgressCount = getFinanceDetailService().getProgressCountByCust(aFinanceMain.getCustID());
			
			// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
			if(eodProgressCount > 0){
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("60203", null)));
				logger.debug("Leaving");
				return;
			}
		}
		
		if (StringUtils.isNotEmpty(moduleDefiner) && !moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY) && 
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RECEIPT) && 
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY) && 
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) && 
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFF) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELFIN) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_BASICMAINTAIN) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELRPY) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_TFPREMIUMEXCL) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFFPAY) &&
				!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_COVENANTS)) {
			
			openFinanceMainDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY) || 
				moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY) ||
				moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) || 
				moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			
			openFinanceRepaymentDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RECEIPT)) {
			
			openFinanceReceiptDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFF)) {
			
			openFinanceWriteoffDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELFIN)) {
			
			openFinanceCancellationDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_BASICMAINTAIN) || 
				moduleDefiner.equals(FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN) ||
				moduleDefiner.equals(FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			
			openFinMaintenanceDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CANCELRPY)) {
			
			openFinanceRepayCancelDialog(item);
			
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_TFPREMIUMEXCL)) {
			
			openTakafulPremiumExcludeDialog(item); 
			
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_COVENANTS)) {

			openFinCovenantMaintanceDialog(item);

		} else {
			if (this.getListBoxFinance().getSelectedItem() != null) {
				final Listitem li = this.getListBoxFinance().getSelectedItem();
				final Object object = li.getAttribute("data");

				if (getDialogCtrl() != null) {
					dialogCtrl = (Object) getDialogCtrl();
				}
				try {

					Class[] paramType = { Class.forName("java.lang.Object"), Class.forName("com.pennant.backend.util.JdbcSearchObject") };
					Object[] stringParameter = { object, this.searchObject };
					if (dialogCtrl.getClass().getMethod("doSetFinance", paramType) != null) {
						dialogCtrl.getClass().getMethod("doSetFinance", paramType).invoke(dialogCtrl, stringParameter);
					}
					doClose();

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		this.isDashboard = false;
		this.isDetailScreen = false;
		logger.debug("Leaving"+ event.toString());
	}
	
	private void setWorkflowDetails(String finType, boolean isPromotion){
		
		//Finance Maintenance Workflow Check & Assignment
		if(StringUtils.isNotEmpty(workflowCode)){
			String workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType, 
					workflowCode, isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			if (workflowTye != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(workflowTye);
			} 
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

	}
 
	private void openFinanceMainDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceDetail financeDetail = getFinanceDetailService().getServicingFinance(aFinanceMain.getId(),eventCodeRef, moduleDefiner,userRole);
			
			//Role Code State Checking
			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=aFinanceMain.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)){

				Date validFrom = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
				List<FinanceScheduleDetail> scheduelist = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
				for (int i = 1; i < scheduelist.size(); i++) {

					FinanceScheduleDetail curSchd = scheduelist.get(i);
					if(curSchd.getSchDate().compareTo(DateUtility.getAppDate()) < 0){
						validFrom = DateUtility.getAppDate();
						continue;
					}
					if(StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())){
						validFrom = curSchd.getSchDate();
						continue;
					}

					if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 
							|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0 
							|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0 
							|| curSchd.getSchdInsPaid().compareTo(BigDecimal.ZERO) > 0 
							|| curSchd.getSuplRentPaid().compareTo(BigDecimal.ZERO) > 0 
							|| curSchd.getIncrCostPaid().compareTo(BigDecimal.ZERO) > 0 ) {

						validFrom = curSchd.getSchDate();
						continue;
					}
				}
				
				if(financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate().compareTo(validFrom) <= 0){
					ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD,"41019", errParm,valueParm), getUserWorkspace().getUserLanguage());
					MessageUtil.showError(errorDetails.getError());
					
					logger.debug("Leaving");
					return;
				}
			}
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}
			
			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showDetailView(financeDetail);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(financeDetail);
				}
			}	
		}
		logger.debug("Leaving ");
	}
	
	private void openFinMaintenanceDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
			
			String userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}
			
			final FinanceDetail financeDetail = getFinanceMaintenanceService().getFinanceDetailById(aFinanceMain.getId(),"_View", userRole, moduleDefiner, eventCodeRef);
			financeDetail.setModuleDefiner(moduleDefiner);
			
			//Role Code State Checking
			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showMaintainDetailView(financeDetail);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showMaintainDetailView(financeDetail);
				}
			}	
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Fetching Finance Repayment Details
	 * @param item
	 * @throws Exception
	 */
	private void openFinanceRepaymentDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			// Set Workflow Details
			String userRole = "";
			if(!StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_EARLYSTLENQ)){
				setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
				if(workFlowDetails == null){
					MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
					return;
				}

				userRole = aFinanceMain.getNextRoleCode();
				if(StringUtils.isEmpty(userRole)){
					userRole = workFlowDetails.getFirstTaskOwner();
				}
			}
			
			final RepayData repayData = getManualPaymentService().getRepayDataById(aFinanceMain.getFinReference(),
					eventCodeRef, moduleDefiner, userRole);
			
			//Role Code State Checking
			String nextroleCode = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			String maintainSts = "";
			if(repayData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showRepayDetailView(repayData);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showRepayDetailView(repayData);
				}
			}	
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Fetching Finance Receipt Details
	 * @param item
	 * @throws Exception
	 */
	private void openFinanceReceiptDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			// Set Workflow Details
			String userRole = "";
			setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}
			
			final FinReceiptData receiptData = getReceiptService().getFinReceiptDataById(aFinanceMain.getFinReference(),
					eventCodeRef, moduleDefiner, userRole);
			
			//Role Code State Checking
			String nextroleCode = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			String maintainSts = "";
			if(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showReceiptDetailView(receiptData);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showReceiptDetailView(receiptData);
				}
			}	
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Fetching Finance Repayment Details
	 * @param item
	 * @throws Exception
	 */
	private void openFinanceWriteoffDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceWriteoffHeader writeoffHeader = getFinanceWriteoffService().getFinanceWriteoffDetailById(
					aFinanceMain.getFinReference(), "_View", userRole, moduleDefiner);
			
			//Role Code State Checking
			String nextroleCode = writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			String maintainSts = "";
			if(writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showWriteoffDetailView(writeoffHeader);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showWriteoffDetailView(writeoffHeader);
				}
			}	
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Fetching Finance Repayment Details
	 * @param item
	 * @throws Exception
	 */
	private void openTakafulPremiumExcludeDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			FinanceDetail finDetail = getFinanceMaintenanceService().getFinanceDetailById(aFinanceMain.getFinReference(), "_View", userRole, moduleDefiner, eventCodeRef);
			if(finDetail!=null){
				aFinanceMain = finDetail.getFinScheduleData().getFinanceMain();
			}
			setFinanceMain(aFinanceMain);
			
			//Role Code State Checking
			String nextroleCode = finDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			// Check For Auto Insurance Fee 
			final FeeRule feeRule = getFinanceDetailService().getFeeChargesByFinRefAndFeeCode(aFinanceMain.getFinReference(), RuleConstants.TAKAFUL_FEE, "");
			String maintainSts = "";
			
			if(feeRule == null || (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
				
				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";
					
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showTakafulPremiumExcludefDetailView(feeRule);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showTakafulPremiumExcludefDetailView(feeRule);
				}
			}	
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Fetching Finance Cancellation Details
	 * @param item
	 * @throws Exception
	 */
	private void openFinanceCancellationDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceDetail financeDetail = getFinanceCancellationService().getFinanceDetailById(
					aFinanceMain.getFinReference(), "_View", userRole, moduleDefiner);
			
			//Role Code State Checking
			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			// Schedule Date verification, As Installment date crossed or not
			List<FinanceScheduleDetail> schdList = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			for (int i = 1; i < schdList.size(); i++) {
				FinanceScheduleDetail curSchd = schdList.get(i);
				if(curSchd.getSchDate().compareTo(DateUtility.getAppDate()) <= 0){
					
					ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD,"60407", null,null), getUserWorkspace().getUserLanguage());
					MessageUtil.showError(errorDetails.getError());
					
					logger.debug("Leaving");
					return;
				}
			}
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}
			
			//Check Repayments on Finance when it is not in Maintenance
			if(StringUtils.isEmpty(maintainSts)){
				List<FinanceRepayments> listFinanceRepayments = new ArrayList<FinanceRepayments>();
				listFinanceRepayments = getFinanceDetailService().getFinanceRepaymentsByFinRef(aFinanceMain.getFinReference(), false);
				if (listFinanceRepayments != null && listFinanceRepayments.size() > 0) {
					MessageUtil.showError("Repayments done on this Finance. Cannot Proceed Further");
					return;
				}
			}
			
			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showCancellationDetailView(financeDetail);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showCancellationDetailView(financeDetail);
				}
			}	
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for Fetching Finance Repayment Details
	 * @param item
	 * @throws Exception
	 */
	private void openFinanceRepayCancelDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType() , StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceDetail financeDetail = getRepaymentCancellationService().getFinanceDetailById(aFinanceMain.getId(),"_View");
			
			//Role Code State Checking
			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
				
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoggedInUser().getUserId()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showRepayCancelView(financeDetail);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showRepayCancelView(financeDetail);
				}
			}	
		}
		logger.debug("Leaving ");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeSelectCtrl", this);
		map.put("tabbox",tab);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("eventCode",eventCodeRef);
		map.put("menuItemRightName",menuItemRightName);
		

		// call the ZUL-file with the parameters packed in a map
		try {

			String productType = aFinanceMain.getProductCategory();
			productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
			
			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			if(moduleDefiner.equalsIgnoreCase(FinanceConstants.FINSER_EVENT_ROLLOVER)) {
				fileLocaation.append("RolloverFinanceMainDialog.zul");
			} else if(productType.equalsIgnoreCase(FinanceConstants.PRODUCT_IJARAH)) {
				fileLocaation.append("IjarahFinanceMainDialog.zul");
			}else if(productType.equalsIgnoreCase(FinanceConstants.PRODUCT_FWIJARAH)) {
				fileLocaation.append("FwdIjarahFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
				fileLocaation.append("IstisnaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_MUDARABA)) {
				fileLocaation.append("MudarabaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_MURABAHA)) {
				fileLocaation.append("MurabahaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_MUSHARAKA)) {
				fileLocaation.append("MusharakFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_TAWARRUQ)) {
				fileLocaation.append("TawarruqFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUK)) {
				fileLocaation.append("SukukFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUKNRM)) {
				fileLocaation.append("SukuknrmFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_ISTNORM)) {
				fileLocaation.append("IstnormFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_MUSAWAMA)) {
				fileLocaation.append("MusawamaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_CONVENTIONAL)) {
				fileLocaation.append("ConvFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_QARDHASSAN)) {
				fileLocaation.append("QardHassanFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_STRUCTMUR)) {
				fileLocaation.append("StructuredMurabahaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_WAKALA)) {
				fileLocaation.append("CorporateWakalaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_ODFACILITY)) {
				fileLocaation.append("ODFacilityFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_DISCOUNT)) {
				fileLocaation.append("DiscountFinanceMainDialog.zul");
			} else {
				fileLocaation.append("FinanceMainDialog.zul");
			}
			
			Executions.createComponents(fileLocaation.toString(), null,map);
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showMaintainDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeSelectCtrl", this);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("eventCode",eventCodeRef);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceMaintenanceDialog.zul", null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showRepayDetailView(RepayData repayData) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		repayData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		map.put("repayData", repayData);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("eventCode",eventCodeRef);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/ManualPayment.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showReceiptDetailView(FinReceiptData receiptData) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		map.put("repayData", receiptData);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("eventCode",eventCodeRef);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showWriteoffDetailView(FinanceWriteoffHeader writeoffHeader) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		writeoffHeader.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeWriteoffHeader", writeoffHeader);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("eventCode",eventCodeRef);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/FinanceWriteoffDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showTakafulPremiumExcludefDetailView(FeeRule feeRule) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		map.put("feeRule", feeRule);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("financeMain", getFinanceMain());
		map.put("eventCode",eventCodeRef);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/TakaFulPremiumExcludeDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showCancellationDetailView(FinanceDetail financeDetail) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		financeDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		
		map.put("financeDetail", financeDetail);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("eventCode",eventCodeRef);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceCancellationDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	private void showRepayCancelView(FinanceDetail financeDetail) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		financeDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		
		map.put("financeDetail", financeDetail);
		map.put("moduleCode",moduleDefiner);
		map.put("moduleDefiner",moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("eventCode",eventCodeRef);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Cancellation/RepayCancellationDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	private void openFinCovenantMaintanceDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			// Set WorkFlow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			// FinMaintainInstruction
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			if (StringUtils.equals(aFinanceMain.getRcdMaintainSts(), moduleDefiner)) {
				finMaintainInstruction = finCovenantMaintanceService.getFinMaintainInstructionByFinRef(aFinanceMain.getFinReference(), moduleDefiner);
			} else {
				finMaintainInstruction.setNewRecord(true);
			}
			// FinanceDetails
			FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailForCovenants(aFinanceMain);

			// Covenants List
			finMaintainInstruction.setFinCovenantTypeList(financeDetail.getCovenantTypeList());

			// Role Code State Checking
			String userRole = finMaintainInstruction.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			String nextroleCode = finMaintainInstruction.getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aFinanceMain.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}

			String maintainSts = "";
			if (finMaintainInstruction != null) {
				maintainSts = StringUtils.trimToEmpty(finMaintainInstruction.getEvent());
			}

			if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aFinanceMain.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {

				if (isWorkFlowEnabled()) {
					String whereCond = " AND FinReference='" + aFinanceMain.getFinReference() + "' AND version="
							+ aFinanceMain.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
							aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces) {
						showFinCovenantMaintanceView(finMaintainInstruction, financeDetail);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showFinCovenantMaintanceView(finMaintainInstruction, financeDetail);
				}
			}
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * 
	 * @param finMaintainInstruction
	 * @param aFinanceMain
	 * @throws Exception
	 */
	private void showFinCovenantMaintanceView(FinMaintainInstruction finMaintainInstruction, FinanceDetail financeDetail)
			throws Exception {
		logger.debug("Entering");

		if (finMaintainInstruction.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			finMaintainInstruction.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		map.put("finMaintainInstruction", finMaintainInstruction);
		map.put("financeSelectCtrl", this);
		map.put("financeDetail", financeDetail);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", false);
		map.put("roleCode", getRole());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantMaintanceDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnClear(Event event){
		logger.debug("Entering" + event.toString());

		this.isDashboard = false;
		this.isDetailScreen = false;
		
		this.custCIF.setValue("");
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_finType.setSelectedIndex(0);
		this.finCcy.setValue("");
		this.sortOperator_finCcy.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_finBranch.setSelectedIndex(0);
		this.scheduleMethod.setValue("");
		this.sortOperator_scheduleMethod.setSelectedIndex(0);
		this.profitDaysBasis.setValue("");
		this.sortOperator_profitDaysBasis.setSelectedIndex(0);
		this.listBoxFinance.getItems().clear();
		this.oldVar_sortOperator_custCIF=0;
		this.oldVar_sortOperator_finType=0;
		this.oldVar_sortOperator_finBranch=0;
		this.oldVar_sortOperator_finCcy=0;
		this.oldVar_sortOperator_finReference=0;
		this.oldVar_sortOperator_profitDaysBasis=0;
		this.oldVar_sortOperator_scheduleMethod=0;
		if (this.searchObject!=null) {	
			this.pagingFinanceList.setActivePage(0);
			this.pagingFinanceList.setDetailed(true);
			doSearch(true);
		}

		logger.debug("Leaving" + event.toString());

	}
	
	/**
	 * Method to check and set module definer value
	 * 
	 * @param tab (Tab)
	 * */
	private void checkAndSetModDef(Tabbox tabbox) {
		logger.debug("Entering");
		filterList = new ArrayList<Filter>();
		
		if (tabbox != null) {			
			tab = tabbox.getSelectedTab();
			if( tab != null) {
				if("tab_BasicDetail".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_BASICMAINTAIN;
					eventCodeRef  = AccountEventConstants.ACCEVENT_AMENDMENT;
					workflowCode =  FinanceConstants.FINSER_EVENT_BASICMAINTAIN;
				}else if("tab_RpyBasicDetail".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SEGMENT;
					workflowCode =  FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN;
				}else if("tab_AddRateChange".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RATECHG;
					eventCodeRef  = AccountEventConstants.ACCEVENT_RATCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_RATECHG;
				}else if("tab_AdvPftRateChange".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_ADVRATECHG;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_ADVRATECHG;
				}else if("tab_SuplRentIncrCost".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_SUPLRENTINCRCOST;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_SUPLRENTINCRCOST;
				}else if("tab_InsChange".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_INSCHANGE;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_INSCHANGE;
				}else if("tab_ChangeRepayment".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CHGRPY;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_CHGRPY;
				}else if("tab_AddDisbursment".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_ADDDISB;
					eventCodeRef  = AccountEventConstants.ACCEVENT_ADDDBSN;
					workflowCode =  FinanceConstants.FINSER_EVENT_ADDDISB;
				}else if("tab_RlsHoldDisbursment".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RLSDISB;
					eventCodeRef  = "";
					workflowCode =  FinanceConstants.FINSER_EVENT_RLSDISB;
				}else if("tab_Postponement".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_POSTPONEMENT;
					eventCodeRef  = AccountEventConstants.ACCEVENT_DEFRPY;
					workflowCode =  FinanceConstants.FINSER_EVENT_POSTPONEMENT;
				}else if("tab_UnPlannedEmi".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_UNPLANEMIH;
					eventCodeRef  = AccountEventConstants.ACCEVENT_EMIHOLIDAY;
					workflowCode =  FinanceConstants.FINSER_EVENT_UNPLANEMIH;
				}else if("tab_AddTerms".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_ADDTERM;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_ADDTERM;
				}else if("tab_RmvTerms".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RMVTERM;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_RMVTERM;
				}else if("tab_Recalculate".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RECALCULATE;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_RECALCULATE;
				}else if("tab_SubSchedule".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_SUBSCHD;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_SUBSCHD;
				}else if("tab_ChangeProfit".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CHGPFT;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_CHGPFT;
				}else if("tab_ChangeFrequency".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CHGFRQ;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_CHGFRQ;
				}else if("tab_ReSchedule".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RESCHD;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_RESCHD;
				}else if("tab_ChangeGestation".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CHGGRCEND;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_CHGGRCEND;
				}else if("tab_FairValueRevaluation".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_COMPOUND;
					eventCodeRef  = AccountEventConstants.ACCEVENT_COMPOUND;
					workflowCode =  FinanceConstants.FINSER_EVENT_COMPOUND;
				}else if("tab_Receipts".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_RECEIPT;
					eventCodeRef  = AccountEventConstants.ACCEVENT_REPAY;
					setDialogCtrl("ReceiptDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_RECEIPT;
				}else if("tab_PartialSettlement".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYRPY;
					eventCodeRef  = AccountEventConstants.ACCEVENT_EARLYPAY;
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_EARLYRPY;
				}else if("tab_SchdRepayment".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_SCHDRPY;
					eventCodeRef  = AccountEventConstants.ACCEVENT_REPAY;
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_SCHDRPY;
				}else if("tab_EarlySettlement".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
					eventCodeRef  = AccountEventConstants.ACCEVENT_EARLYSTL;
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_EARLYSETTLE;
				}else if("tab_EarlySettlementEnq".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_EARLYSTLENQ;
					setDialogCtrl("ManualPaymentDialogCtrl");
				}else if("tab_WriteOff".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_WRITEOFF;
					setDialogCtrl("FinanceWriteoffDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_WRITEOFF;
					eventCodeRef  = AccountEventConstants.ACCEVENT_WRITEOFF;
				}else if ("tab_WriteoffPayment".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_WRITEOFFPAY;
					eventCodeRef  = AccountEventConstants.ACCEVENT_WRITEBK;
					workflowCode = FinanceConstants.FINSER_EVENT_WRITEOFFPAY;
				}else if("tab_CancelRepay".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CANCELRPY;
					setDialogCtrl("CancelRepayDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_CANCELRPY;
				}else if("tab_CancelFinance".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CANCELFIN;
					eventCodeRef  = AccountEventConstants.ACCEVENT_CANCELFIN;
					setDialogCtrl("CancelFinanceDialogCtrl");
					workflowCode =  FinanceConstants.FINSER_EVENT_CANCELFIN;
				} else if("tab_TakafulPremiumExclude".equals(tab.getId())){
					moduleDefiner = FinanceConstants.FINSER_EVENT_TFPREMIUMEXCL;
					setDialogCtrl("TakafulPremiumExcludeDialogCtrl");
					workflowCode="";
				} else if("tab_CancelDisbursement".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CANCELDISB;
					eventCodeRef  = "";
					workflowCode =  FinanceConstants.FINSER_EVENT_CANCELDISB;
				}  else if("tab_OverdraftSchedule".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD;
					eventCodeRef  = "";
					workflowCode =  FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD;
				} else if("tab_PlannedEMI".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_PLANNEDEMI;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_PLANNEDEMI;				
				} else if("tab_ReAgeHolidays".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_REAGING;
					eventCodeRef  = AccountEventConstants.ACCEVENT_REAGING;
					workflowCode =  FinanceConstants.FINSER_EVENT_REAGING;
				} else if("tab_RolloverFinance".equals(tab.getId())){
					moduleDefiner = FinanceConstants.FINSER_EVENT_ROLLOVER;
					eventCodeRef  = AccountEventConstants.ACCEVENT_ROLLOVER;
					workflowCode= FinanceConstants.FINSER_EVENT_ROLLOVER;
					setDialogCtrl("RolloverFinanceMainDialogCtrl");
					this.btnNew.setVisible(true);
					this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceSelectList_NewRollover"));
				}  else if("tab_HoldEMI".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_HOLDEMI;
					eventCodeRef  = AccountEventConstants.ACCEVENT_ROLLOVER;
					workflowCode =  FinanceConstants.FINSER_EVENT_HOLDEMI;
				} else if ("tab_FinCovenants".equals(tab.getId())) {
					eventCodeRef	= "";
					moduleDefiner	= FinanceConstants.FINSER_EVENT_COVENANTS;
					workflowCode	= FinanceConstants.FINSER_EVENT_COVENANTS;
				}
				else if ("tab_ChangeSchdMethod".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD;
					eventCodeRef  = AccountEventConstants.ACCEVENT_SCDCHG;
					workflowCode =  FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD;
				}
				return;
			}
		}else{
			moduleDefiner="";
			return;
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$btnNew(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("loanType",FinanceConstants.PRODUCT_MURABAHA);
		map.put("financeSelectCtrl", this);
		map.put("tabbox",tab);
		map.put("moduleDefiner",moduleDefiner);
		map.put("eventCode",eventCodeRef);
		map.put("menuItemRightName",menuItemRightName);
		map.put("role", getUserWorkspace().getUserRoles());
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Rollover/SelectRolloverFinanceDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<FinanceMain> getSearchObj(boolean isRefresh) {
		
		if(isRefresh){
			return searchObject;
		}
		
		if(searchObject==null){
			this.searchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class); 
		} else {
			searchObject.getFilters().clear();
			searchObject.getSorts().clear();
			searchObject.addWhereClause("");
		}
		
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_COVENANTS)) {
			this.searchObject.addTabelName("CovenantsMaintenance_View");
		} else {
			this.searchObject.addTabelName("FinanceMaintenance_View");
		}
		
		if(isDashboard){
			this.searchObject.addFilterEqual("RcdMaintainSts", moduleDefiner);
		}
		
		buildedWhereCondition = "";
		
		if(StringUtils.isNotEmpty(workflowCode)){
			
			if (App.DATABASE == Database.ORACLE) {
				buildedWhereCondition = " (NextRoleCode IS NULL ";
			} else {
				// for postgre db sometimes record type is null or empty('')
				buildedWhereCondition = " ( (NextRoleCode IS NULL or NextRoleCode = '') ";
			}
			buildedWhereCondition = buildedWhereCondition.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
			buildedWhereCondition = buildedWhereCondition.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
			buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");
			buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
			buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN (SELECT RoleCd FROM UserOperationRoles_View WHERE ");
			buildedWhereCondition = buildedWhereCondition.concat(" UsrID= "+getUserWorkspace().getUserDetails().getUserId()+" AND AppCode='"+App.CODE+"')");
			buildedWhereCondition = buildedWhereCondition.concat(")) OR NextRoleCode IN (SELECT RoleCd FROM UserOperationRoles_View WHERE ");
			buildedWhereCondition = buildedWhereCondition.concat(" UsrID= "+getUserWorkspace().getUserDetails().getUserId()+" AND AppCode='"+App.CODE+"')");
			
		}
		Filter[] productCodeFilter = new Filter[1];
		productCodeFilter[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_QARDHASSAN, Filter.OP_NOT_EQUAL);
		if(StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CHGPFT)
				|| StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_RATECHG)){
			this.searchObject.addFilterOr(productCodeFilter);
		}
		Filter[] rcdTypeFilter = new Filter[2];
		rcdTypeFilter[0] = new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL);
		rcdTypeFilter[1] = new Filter("RecordType", "", Filter.OP_EQUAL);
		if(!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ROLLOVER) && !moduleDefiner.equals(FinanceConstants.FINSER_EVENT_COVENANTS)){
			this.searchObject.addFilterOr(rcdTypeFilter);
		}
		
		if(filterList != null && !filterList.isEmpty()){
			for (Filter filter : filterList) {
				this.searchObject.addFilter(filter);
			}
		}
		
		return this.searchObject;
	}
	public void setSearchObj(JdbcSearchObject<FinanceMain> searchObj) {		
		this.searchObject = searchObj;
	}

	public Object getDialogCtrl() {
		return dialogCtrl;
	}
	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}
	
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	
	public ManualPaymentService getManualPaymentService() {
		return manualPaymentService;
	}
	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}
	
	public FinanceWriteoffService getFinanceWriteoffService() {
		return financeWriteoffService;
	}
	public void setFinanceWriteoffService(
			FinanceWriteoffService financeWriteoffService) {
		this.financeWriteoffService = financeWriteoffService;
	}
	
	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}
	public void setFinanceCancellationService(
			FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	public FinanceMaintenanceService getFinanceMaintenanceService() {
		return financeMaintenanceService;
	}
	public void setFinanceMaintenanceService(FinanceMaintenanceService financeMaintenanceService) {
		this.financeMaintenanceService = financeMaintenanceService;
	}
	
	public RepaymentCancellationService getRepaymentCancellationService() {
		return repaymentCancellationService;
	}
	public void setRepaymentCancellationService(
			RepaymentCancellationService repaymentCancellationService) {
		this.repaymentCancellationService = repaymentCancellationService;
	}
	
	public Paging getPagingFinanceList() {
		return pagingFinanceList;
	}
	public void setPagingFinanceList(Paging pagingFinanceList) {
		this.pagingFinanceList = pagingFinanceList;
	}

	public Listbox getListBoxFinance() {
		return listBoxFinance;
	}
	public void setListBoxFinance(Listbox listBoxFinance) {
		this.listBoxFinance = listBoxFinance;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	public void setFinanceWorkFlowService(
			FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}
	public FinCovenantMaintanceService getFinCovenantMaintanceService() {
		return finCovenantMaintanceService;
	}

	public void setFinCovenantMaintanceService(FinCovenantMaintanceService finCovenantMaintanceService) {
		this.finCovenantMaintanceService = finCovenantMaintanceService;
	}	
	
	
}