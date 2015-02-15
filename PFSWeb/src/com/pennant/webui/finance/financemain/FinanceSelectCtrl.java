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
 * FileName    		:  CustomerSearchCtrl.java                                                   * 	  
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

import java.io.Serializable;
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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.RepaymentCancellationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.finance.financemain.model.FinanceMainSelectItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceSelect.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceSelectCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {

	private static final long serialVersionUID = -5081318673331825306L;
	private final static Logger logger = Logger.getLogger(FinanceSelectCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
	private Paging  pagingFinanceList; 				// autowired
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
	private transient FinanceDetailService financeDetailService;
	private transient ManualPaymentService manualPaymentService;
	private transient FinanceWriteoffService financeWriteoffService;
	private transient FinanceCancellationService financeCancellationService;
	private transient FinanceMaintenanceService financeMaintenanceService;
	private transient RepaymentCancellationService repaymentCancellationService;
	private FinanceMain financeMain;
	private boolean isDashboard = false;
	private boolean isDetailScreen = false;
	
	/**
	 * Default constructor
	 */
	public FinanceSelectCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
			
			checkAndSetModDef(tabbox);
	    }
		
		//Finance Maintenance Workflow Check & Assignment
		if(!workflowCode.equals("")){
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(workflowCode);
			if (moduleMapping.getWorkflowType() != null) {
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails(workflowCode);
			} 
		}
		
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
		
		//Listbox Sorting
		
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		
		this.listheader_FinProduct.setSortAscending(new FieldComparator("lovDescProductCodeName", true));
		this.listheader_FinProduct.setSortDescending(new FieldComparator("lovDescProductCodeName", false));
		
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
		
		this.listheader_RecordStatus.setSortAscending(new FieldComparator("RecordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("RecordStatus", false));

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

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

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("DialogCtrl")) {
			setDialogCtrl(args.get("DialogCtrl"));
		}
		if (args.containsKey("filtersList")) {
			filterList = (List<Filter>) args.get("filtersList");
		}

		// +++++++++++++++++++++++ Stored search object and paging ++++++++++++++++++++++ //
			
		if (args.containsKey("searchObject")) {
			searchObject = (JdbcSearchObject<FinanceMain>) args.get("searchObject");
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
				if (filter.getProperty().equals("lovDescCustCIF")) {
					SearchOperators.resetOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCIF));
				} else if (filter.getProperty().equals("finReference")) {
					SearchOperators.resetOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finReference));
				} else if (filter.getProperty().equals("finType")) {
					SearchOperators.resetOperator(this.sortOperator_finType, filter);
					this.finType.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finType));
				} else if (filter.getProperty().equals("finCcy")) {
					SearchOperators.resetOperator(this.sortOperator_finCcy, filter);
					this.finCcy.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finCcy));
				}  else if (filter.getProperty().equals("finBranch")) {
					SearchOperators.resetOperator(this.sortOperator_finBranch, filter);
					this.finBranch.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finBranch));
				} else if (filter.getProperty().equals("scheduleMethod")) {
					SearchOperators.resetOperator(this.sortOperator_scheduleMethod, filter);
					this.scheduleMethod.setValue(restoreString(filter.getValue().toString(), this.sortOperator_scheduleMethod));
				} else if (filter.getProperty().equals("profitDaysBasis")) {
					SearchOperators.resetOperator(this.sortOperator_profitDaysBasis, filter);
					this.profitDaysBasis.setValue(restoreString(filter.getValue().toString(), this.sortOperator_profitDaysBasis));
				} 
			}
		}
		if (args.containsKey("isDashboard")) {
			if (args.containsKey("moduleDefiner")) {
			this.moduleDefiner = (String)(args.get("moduleDefiner"));
			}
			this.isDashboard = true ;
			Events.postEvent("onClick$btnSearch", window_FinanceSelect,event);
			if (args.containsKey("detailScreen")) {
				this.isDetailScreen = true;
				if (args.containsKey("FinanceReference")) {
				this.finReference.setValue((String)(args.get("FinanceReference")));
				}
				Events.postEvent("onFinanceItemDoubleClicked", window_FinanceSelect,event);
			}
			
		}
        if(moduleDefiner.equals(PennantConstants.TAKAFULPREMIUMEXCLUDE)){
        	this.listheader_RecordStatus.setVisible(false);
        }
		logger.debug("Leaving" + event.toString());
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch();
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
	     
	        e.printStackTrace();
        }
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * @param event
	 */
	public void onClick$btnSearchFinRef(Event event){
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_finReference == Filter.OP_IN || this.oldVar_sortOperator_finReference == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "FinanceMain", this.finReference.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finReference.setValue(selectedValues);
			}
			
		}else{
			Object dataObject  = ExtendedSearchListBox.show(this.window_FinanceSelect,"FinanceMain");

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
	 * When user clicks on  "btnSearchCustCIF" button
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws  SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_custCIF == Filter.OP_IN || this.oldVar_sortOperator_custCIF == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "Customer", this.custCIF.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.custCIF.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "Customer");
			if (dataObject instanceof String) {
				this.custCIF.setValue("");
			} else {
				Customer details = (Customer) dataObject;
				if (details != null) {
					this.custCIF.setValue(details.getCustCIF());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When user clicks on  "btnSearchCustCIF" button
	 * @param event
	 */
	public void onClick$btnSearchSchdMethod(Event event) throws  SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_scheduleMethod == Filter.OP_IN || this.oldVar_sortOperator_scheduleMethod == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceSelect, "ScheduleMethod", this.scheduleMethod.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.scheduleMethod.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod");
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
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +On Change Events for Multi-Selection Listbox's for Search operators+ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

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
	public void doSearch() {
		logger.debug("Entering");
		
		getSearchObj();
		
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
					searchObject.addFilter(new Filter("FinReference", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("FinReference", this.finReference.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("FinReference", this.finReference.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinReference", this.finReference.getValue(), searchOpId));
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
		Date appDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		
		searchObject.addSort("FinReference", false);
		StringBuilder whereClause = new StringBuilder( " FinIsActive = 1 " );
		whereClause.append(" AND (RcdMaintainSts = '' OR RcdMaintainSts = '"+moduleDefiner+"' ) "); 
		int backValueDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MAINTAIN_RATECHG_BACK_DATE").toString());
		Date backValueDate = DateUtility.addDays(appDate, backValueDays);
		
		if(moduleDefiner.equals(PennantConstants.ADD_RATE_CHG) ) {
			whereClause.append(" AND ((AllowGrcPftRvw = 1 OR AllowRepayRvw = 1) OR "); 
			whereClause.append("(FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND "); 
			whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "'))");  
		}else if(moduleDefiner.equals(PennantConstants.CHG_REPAY)) {
			
		}else if(moduleDefiner.equals(PennantConstants.ADD_DISB)) {
			whereClause.append(" AND LovDescFinIsAlwMD = '1'  AND MaturityDate > " + appDate);  
		}else if(moduleDefiner.equals(PennantConstants.ADD_DEFF)) { 
			whereClause.append(" AND (Defferments - AvailedDefRpyChange >= 0 ) "); 
		}else if(moduleDefiner.equals(PennantConstants.RMV_DEFF)) { 
			whereClause.append(" AND ( FinReference IN (select FinReference from FinanceMain where AvailedDefRpyChange > 0) ) "); 
		}else if(moduleDefiner.equals(PennantConstants.ADD_TERMS)){
			
		}else if(moduleDefiner.equals(PennantConstants.RMV_TERMS)){
			
		}else if(moduleDefiner.equals(PennantConstants.RECALC)){
			
		}else if(moduleDefiner.equals(PennantConstants.SUBSCH)){
			
		}else if(moduleDefiner.equals(PennantConstants.CHGPFT)){
			whereClause.append(" AND ((AllowGrcPftRvw = 1 OR AllowRepayRvw = 1) OR "); 
			whereClause.append("(FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND "); 
			whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "'))");  
		}else if(moduleDefiner.equals(PennantConstants.CHGFRQ)){
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D +"' " );
		}else if(moduleDefiner.equals(PennantConstants.RESCHD)){
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D +"' " );
		}else if(moduleDefiner.equals(PennantConstants.CHGGRC)){
			whereClause.append(" AND (LovDescProductCodeName = '" + PennantConstants.FINANCE_PRODUCT_IJARAH +"') " );
		}else if(moduleDefiner.equals(PennantConstants.DATEDSCHD)){
			whereClause.append(" AND MaturityDate > '" + DateUtility.addDays(appDate, 1)+"' " );
		}else if(moduleDefiner.equals(PennantConstants.COMPOUND)){
			whereClause.append(" AND (LovDescProductCodeName = '"+PennantConstants.FINANCE_PRODUCT_SUKUK +"')"); 
		}else if(moduleDefiner.equals(PennantConstants.SCH_REPAY)){
			whereClause.append(" AND FinStartDate < '" + appDate+"' " );
		}else if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			whereClause.append(" AND FinStartDate < '" + appDate +"' " );
		}else if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
			whereClause.append(" AND FinStartDate < '" + appDate +"' " );
		}else if(moduleDefiner.equals(PennantConstants.WRITEOFF)){
			
		}else if(moduleDefiner.equals(PennantConstants.CANCELREPAY)){
			//whereClause.append(" OR (FinIsActive = '0' AND ClosingStatus = 'M') ");
		}else if(moduleDefiner.equals(PennantConstants.CANCELFINANCE)){
			
			backValueDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MAINTAIN_CANFIN_BACK_DATE").toString());
			backValueDate = DateUtility.addDays(appDate, backValueDays);
			
			whereClause.append(" AND MigratedFinance = '0' ");
			whereClause.append(" AND (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND "); 
			whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "')");  
		} else if (moduleDefiner.equals(PennantConstants.TAKAFULPREMIUMEXCLUDE)) {
			whereClause.append(" AND FinReference IN(SELECT FinReference FROM FinFeeCharges WHERE FeeCode= 'TAKAFUL')");
		}
		
		// Filtering added based on user branch and division
		whereClause.append(" ) AND ( " +getUsrFinAuthenticationQry(false));
		
		searchObject.addWhereClause(whereClause.toString());
		setSearchObj(searchObject);
		paging(searchObject);
		logger.debug("Leaving");
	}

	/**
	 * Method for Render the getting list and set the pagination
	 * 
	 * @param searchObj
	 */
	private void paging(JdbcSearchObject<FinanceMain> searchObj) {
		logger.debug("Entering");	
		getPagedListWrapper().init(searchObj, getListBoxFinance(), getPagingFinanceList());
		this.getListBoxFinance().setItemRenderer(new FinanceMainSelectItemRenderer());
		logger.debug("Leaving");
	}

	// ++++++++++++ when item double clicked ++++++++++++++++++//
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
		if (!moduleDefiner.equals("") && !moduleDefiner.equals(PennantConstants.SCH_REPAY) && 
				!moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) && 
				!moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ) &&
				!moduleDefiner.equals(PennantConstants.WRITEOFF) &&
				!moduleDefiner.equals(PennantConstants.CANCELFINANCE) &&
				!moduleDefiner.equals(PennantConstants.MNT_BASIC_DETAIL) &&
				!moduleDefiner.equals(PennantConstants.CANCELREPAY) &&
				!moduleDefiner.equals(PennantConstants.TAKAFULPREMIUMEXCLUDE)) {
			
			openFinanceMainDialog(item);
			
		}else if(moduleDefiner.equals(PennantConstants.SCH_REPAY) || 
				moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) || 
				moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)) {
			
			openFinanceRepaymentDialog(item);
			
		}else if(moduleDefiner.equals(PennantConstants.WRITEOFF)) {
			
			openFinanceWriteoffDialog(item);
			
		}else if(moduleDefiner.equals(PennantConstants.CANCELFINANCE)) {
			
			openFinanceCancellationDialog(item);
			
		}else if(moduleDefiner.equals(PennantConstants.MNT_BASIC_DETAIL)) {
			
			openFinMaintenanceDialog(item);
			
		}else if(moduleDefiner.equals(PennantConstants.CANCELREPAY)) {
			
			openFinanceRepayCancelDialog(item);
			
		}else if(moduleDefiner.equals(PennantConstants.TAKAFULPREMIUMEXCLUDE)) {
			
			openTakafulPremiumExcludeDialog(item); 
			
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
					logger.error(e);
				}
			}
		}
		this.isDashboard = false;
		this.isDetailScreen = false;
		//doClose();
		logger.debug("Leaving"+ event.toString());
	}
	
	
	private void openFinanceMainDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			final FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(aFinanceMain.getId(),false,eventCodeRef,false);
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(financeDetail==null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showDetailView(financeDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
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
			final FinanceDetail financeDetail = getFinanceMaintenanceService().getFinanceDetailById(aFinanceMain.getId(),"_View");
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(financeDetail==null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showMaintainDetailView(financeDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
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
			final RepayData repayData = getManualPaymentService().getRepayDataById(aFinanceMain.getFinReference(), eventCodeRef);
			
			String maintainSts = "";
			if(repayData.getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(repayData.getFinanceMain().getRcdMaintainSts());
			}

			if(repayData == null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showRepayDetailView(repayData);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showRepayDetailView(repayData);
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
			final FinanceWriteoffHeader writeoffHeader = getFinanceWriteoffService().getFinanceWriteoffDetailById(aFinanceMain.getFinReference(), "_View");
			
			String maintainSts = "";
			if(writeoffHeader.getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(writeoffHeader.getFinanceMain().getRcdMaintainSts());
			}

			if(writeoffHeader == null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showWriteoffDetailView(writeoffHeader);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
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
			FinanceDetail finDetail = getFinanceMaintenanceService().getFinanceDetailById(aFinanceMain.getFinReference(), "_View");
			if(finDetail!=null){
				aFinanceMain = finDetail.getFinScheduleData().getFinanceMain();
			}
			setFinanceMain(aFinanceMain);
			
			final FeeRule feeRule = getFinanceDetailService().getFeeChargesByFinRefAndFeeCode(aFinanceMain.getFinReference(), "TAKAFUL", "");
			String maintainSts = "";
			/*if(writeoffHeader.getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(writeoffHeader.getFinanceMain().getRcdMaintainSts());
			}*/
			
			if(feeRule == null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
				
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";
					
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showTakafulPremiumExcludefDetailView(feeRule);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
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
			final FinanceDetail financeDetail = getFinanceCancellationService().getFinanceDetailById(aFinanceMain.getFinReference(), "_View");
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}
			
			//Check Repayments on Finance when it is not in Maintenance
			if(maintainSts.equals("")){
				List<FinanceRepayments> listFinanceRepayments = new ArrayList<FinanceRepayments>();
				listFinanceRepayments = getFinanceDetailService().getFinanceRepaymentsByFinRef(aFinanceMain.getFinReference(), false);
				if (listFinanceRepayments != null && listFinanceRepayments.size() > 0) {
					PTMessageUtils.showErrorMessage("Repayments done on this Finance. Cannot Proceed Further");
					return;
				}
			}
			
			if(financeDetail == null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showCancellationDetailView(financeDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
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
			final FinanceDetail financeDetail = getRepaymentCancellationService().getFinanceDetailById(aFinanceMain.getId(),"_View");
			
			String maintainSts = "";
			if(financeDetail.getFinScheduleData().getFinanceMain() != null){
				maintainSts = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if(financeDetail == null || (!maintainSts.equals("") && !maintainSts.equals(moduleDefiner))){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, workflowCode, whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces){
						showRepayCancelView(financeDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
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
		final HashMap<String, Object> map = new HashMap<String, Object>(6);
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeSelectCtrl", this);
		map.put("tabbox",tab);
		map.put("moduleDefiner",moduleDefiner);
		map.put("eventCode",eventCodeRef);
		map.put("menuItemRightName",menuItemRightName);

		// call the ZUL-file with the parameters packed in a map
		try {

			String productType = aFinanceMain.getLovDescProductCodeName();
			productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
			
			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_IJARAH)) {
				fileLocaation.append("IjarahFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_ISTISNA)) {
				fileLocaation.append("IstisnaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MUDARABA)) {
				fileLocaation.append("MudarabaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MURABAHA)) {
				fileLocaation.append("MurabahaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MUSHARAKA)) {
				fileLocaation.append("MusharakFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_TAWARRUQ)) {
				fileLocaation.append("TawarruqFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_SUKUK)) {
				fileLocaation.append("SukukFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_SUKUKNRM)) {
				fileLocaation.append("SukuknrmFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_ISTNORM)) {
				fileLocaation.append("IstnormFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_CONVENTIONAL)) {
				fileLocaation.append("ConvFinanceMainDialog.zul");
			} else {
				fileLocaation.append("FinanceMainDialog.zul");
			}
			
			Executions.createComponents(fileLocaation.toString(), null,map);
			
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeSelectCtrl", this);
		map.put("moduleDefiner",moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceMaintenanceDialog.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		FinanceMain aFinanceMain = repayData.getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		repayData.setFinanceMain(aFinanceMain);
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("repayData", repayData);
		map.put("moduleDefiner", moduleDefiner);
		map.put("eventCode",eventCodeRef);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/ManualPayment.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		FinanceMain aFinanceMain = writeoffHeader.getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		writeoffHeader.setFinanceMain(aFinanceMain);
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeWriteoffHeader", writeoffHeader);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/FinanceWriteoffDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
	/*	FinanceMain aFinanceMain = feeRule.getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		feeRule.setFinanceMain(aFinanceMain);*/
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("feeRule", feeRule);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("financeMain", getFinanceMain());
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/TakaFulPremiumExcludeDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", financeDetail);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceCancellationDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", financeDetail);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName",menuItemRightName);
		map.put("financeSelectCtrl", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Cancellation/RepayCancellationDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
		logger.debug("Leaving");
	}

	public void onClick$btnClear(Event event){
		logger.debug("Entering" + event.toString());
		
		if (this.searchObject!=null) {	
			this.searchObject.clearFilters();
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
			this.searchObject.clearFilters();		
			paging(getSearchObj());
			this.isDashboard = false;
			this.isDetailScreen = false;
		}
		logger.debug("Leaving" + event.toString());
		
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		
		if(searchObject==null){
			this.searchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class); 
		}else {
			searchObject.getFilters().clear();
		}
		this.searchObject.addTabelName("FinanceMaintenance_View");
		if(isDashboard){
			this.searchObject.addFilterEqual("RcdMaintainSts", moduleDefiner);
		}
		if(!workflowCode.equals("")){
			this.searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}
		this.searchObject.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
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
	
	/**
	 * Method to check and set moduuledefiner value
	 * 
	 * @param tab (Tab)
	 * */
	private void checkAndSetModDef(Tabbox tabbox) {
		logger.debug("Entering");
		filterList = new ArrayList<Filter>();
		
		if (tabbox != null) {			
			tab = tabbox.getSelectedTab();
			if( tab != null) {
				if(tab.getId().equals("tab_BasicDetail")) {
					moduleDefiner = PennantConstants.MNT_BASIC_DETAIL;
					eventCodeRef  = "";
					workflowCode =  PennantConstants.MNT_BASIC_DETAIL;
				}else if(tab.getId().equals("tab_AddRateChange")) {
					moduleDefiner = PennantConstants.ADD_RATE_CHG;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.ADD_RATE_CHG;
				}else if(tab.getId().equals("tab_ChangeRepayment")) {
					moduleDefiner = PennantConstants.CHG_REPAY;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.CHG_REPAY;
				}else if(tab.getId().equals("tab_AddDisbursment")) {
					moduleDefiner = PennantConstants.ADD_DISB;
					eventCodeRef  = "";
					workflowCode =  PennantConstants.ADD_DISB;
				}else if(tab.getId().equals("tab_AddDefferment")) {
					moduleDefiner = PennantConstants.ADD_DEFF;
					eventCodeRef  = "DEFRPY";
					workflowCode =  PennantConstants.ADD_DEFF;
				}else if(tab.getId().equals("tab_RmvDefferment")) {
					moduleDefiner = PennantConstants.RMV_DEFF;
					eventCodeRef  = "DEFRPY";
					workflowCode =  PennantConstants.RMV_DEFF;
				}else if(tab.getId().equals("tab_AddTerms")) {
					moduleDefiner = PennantConstants.ADD_TERMS;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.ADD_TERMS;
				}else if(tab.getId().equals("tab_RmvTerms")) {
					moduleDefiner = PennantConstants.RMV_TERMS;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.RMV_TERMS;
				}else if(tab.getId().equals("tab_Recalculate")) {
					moduleDefiner = PennantConstants.RECALC;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.RECALC;
				}else if(tab.getId().equals("tab_SubSchedule")) {
					moduleDefiner = PennantConstants.SUBSCH;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.SUBSCH;
				}else if(tab.getId().equals("tab_ChangeProfit")) {
					moduleDefiner = PennantConstants.CHGPFT;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.CHGPFT;
				}else if(tab.getId().equals("tab_ChangeFrequency")) {
					moduleDefiner = PennantConstants.CHGFRQ;
					eventCodeRef  = "SCDCHG";//TODO
					workflowCode =  PennantConstants.CHGFRQ;
				}else if(tab.getId().equals("tab_ReSchedule")) {
					moduleDefiner = PennantConstants.RESCHD;
					eventCodeRef  = "SCDCHG";//TODO
					workflowCode =  PennantConstants.RESCHD;
				}else if(tab.getId().equals("tab_ChangeGestation")) {
					moduleDefiner = PennantConstants.CHGGRC;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.CHGGRC;
				}else if(tab.getId().equals("tab_DatedSchedule")) {
					moduleDefiner = PennantConstants.DATEDSCHD;
					eventCodeRef  = "SCDCHG";
					workflowCode =  PennantConstants.DATEDSCHD;
				}else if(tab.getId().equals("tab_FairValueRevaluation")) {
					moduleDefiner = PennantConstants.COMPOUND;
					eventCodeRef  = "COMPOUND";
					workflowCode =  PennantConstants.COMPOUND;
				}else if(tab.getId().equals("tab_SchdlRepayment")) {
					moduleDefiner = PennantConstants.SCH_REPAY;
					eventCodeRef  = "REPAY";
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode =  PennantConstants.SCH_REPAY;
				}else if(tab.getId().equals("tab_EarlySettlement")) {
					moduleDefiner = PennantConstants.SCH_EARLYPAY;
					eventCodeRef  = "EARLYSTL";
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode =  PennantConstants.SCH_EARLYPAY;
				}else if(tab.getId().equals("tab_EarlySettlementEnq")) {
					moduleDefiner = PennantConstants.SCH_EARLYPAYENQ;
					setDialogCtrl("ManualPaymentDialogCtrl");
				}else if(tab.getId().equals("tab_WriteOff")) {
					moduleDefiner = PennantConstants.WRITEOFF;
					setDialogCtrl("FinanceWriteoffDialogCtrl");
					workflowCode =  PennantConstants.WRITEOFF;
				}else if(tab.getId().equals("tab_CancelRepay")) {
					moduleDefiner = PennantConstants.CANCELREPAY;
					setDialogCtrl("CancelRepayDialogCtrl");
					workflowCode =  PennantConstants.CANCELREPAY;
				}else if(tab.getId().equals("tab_CancelFinance")) {
					moduleDefiner = PennantConstants.CANCELFINANCE;
					setDialogCtrl("CancelFinanceDialogCtrl");
					workflowCode =  PennantConstants.CANCELFINANCE;
				} else if(tab.getId().equals("tab_TakafulPremiumExclude")){
					moduleDefiner = PennantConstants.TAKAFULPREMIUMEXCLUDE;
					setDialogCtrl("TakafulPremiumExcludeDialogCtrl");
					workflowCode="";
				}
				return;
			}
		}else{
			moduleDefiner="";
			return;
		}
		logger.debug("Leaving");
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
}