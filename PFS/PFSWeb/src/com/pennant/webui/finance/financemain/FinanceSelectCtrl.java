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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.finance.FinanceDetailService;
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
	protected Paging  pagingFinanceList; 				// autowired
	protected Listbox listBoxFinance; 					// autowired
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
	protected Listheader listheader_FinReference; 		// autowired
	protected Listheader listheader_FinType; 			// autowired
	protected Listheader listheader_FinCcy; 			// autowired
	protected Listheader listheader_ScheduleMethod; 	// autowired
	protected Listheader listheader_ProfitDaysBasis; 	// autowired

	protected Label label_FinanceSelectResult; 			// autowired
	private String moduleDefiner = "";
	private String eventCodeRef = "";
	private Tab tab;
	private Tabbox	tabbox;

	// not auto wired vars
	private transient Object dialogCtrl   =   null;
	private transient WorkFlowDetails workFlowDetails  =  WorkFlowUtil.getWorkFlowDetails("FinanceMain");
	protected JdbcSearchObject<FinanceMain> searchObject;
	private List<Filter> filterList;
	protected Button btnClear;
	private transient FinanceDetailService financeDetailService;
	private int listRows;
	
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
			checkAndSetModDef(tabbox);
	    }
		
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

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
		this.pagingFinanceList.setPageSize(20);
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

		showFinanceSeekDialog();

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
			final String modifiedFilterValue = StringUtils.replaceChars(filterValue, "%", "");
			return modifiedFilterValue;
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCloseWindow(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
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
				if (dialogCtrl.getClass().getMethod("closeTab", null) != null) {
					dialogCtrl.getClass().getMethod("closeTab", null).invoke(dialogCtrl, null);
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

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.borderlayout_FinanceSelect.setHeight(getBorderLayoutHeight());
			int dialogHeight =  grid_FinanceDetails.getRows().getVisibleItemCount()* 20 + 120; 
			int listboxHeight = borderLayoutHeight-dialogHeight;
			listBoxFinance.setHeight(listboxHeight+"px");
			listRows = Math.round(listboxHeight/ 23);
			this.pagingFinanceList.setPageSize(listRows);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
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

		this.searchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class); 
		this.searchObject.addTabelName("FinanceEnquiry_View");
		this.searchObject.addFilterEqual("FinIsActive", "1");
		
		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem item_CustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (item_CustCIF != null) {
				final int searchOpId = ((SearchOperators) item_CustCIF.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinReference = this.sortOperator_finReference.getSelectedItem();

			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinType = this.sortOperator_finType.getSelectedItem();

			if (item_FinType != null) {
				final int searchOpId = ((SearchOperators) item_FinType.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinCcy = this.sortOperator_finCcy.getSelectedItem();

			if (item_FinCcy != null) {
				final int searchOpId = ((SearchOperators) item_FinCcy.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinBranch = this.sortOperator_finBranch.getSelectedItem();

			if (item_FinBranch != null) {
				final int searchOpId = ((SearchOperators) item_FinBranch.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_ScheduleMethod = this.sortOperator_scheduleMethod.getSelectedItem();

			if (item_ScheduleMethod != null) {
				final int searchOpId = ((SearchOperators) item_ScheduleMethod.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_ProfitDaysBasis = this.sortOperator_profitDaysBasis.getSelectedItem();

			if (item_ProfitDaysBasis != null) {
				final int searchOpId = ((SearchOperators) item_ProfitDaysBasis.getAttribute("data")).getSearchOperatorId();

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

		searchObject.addFilter(new Filter("FinAmount - FinRepaymentAmount", 0, Filter.OP_NOT_EQUAL));
		searchObject.addFilter(new Filter("Blacklisted", 0, Filter.OP_EQUAL));
		
		// Default Sort on the table
		searchObject.addSort("FinReference", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = searchObject.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());
				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

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
		getPagedBindingListWrapper().init(searchObj, this.listBoxFinance, this.pagingFinanceList);
		this.listBoxFinance.setItemRenderer(new FinanceMainSelectItemRenderer());
		//this.label_FinanceSelectResult.setValue(Labels.getLabel("label_FinanceSelectResult") + " " + String.valueOf(pagingFinanceList.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++ when item double clicked ++++++++++++++++++//
	@SuppressWarnings("rawtypes")
	public void onFinanceItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final Listitem item = this.listBoxFinance.getSelectedItem();
		if (!moduleDefiner.equals("") && !moduleDefiner.equals(PennantConstants.SCH_REPAY) && 
				!moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) && 
				!moduleDefiner.equals(PennantConstants.WRITEOFF)) {
			openFinanceMainDialog(item);
		}else if(moduleDefiner.equals(PennantConstants.SCH_REPAY) || 
				moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) || 
				moduleDefiner.equals(PennantConstants.WRITEOFF)) {
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("selectedItem", item.getAttribute("data"));
			map.put("moduleDefiner", moduleDefiner);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/ManualPayment.zul",null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
			
		}else {
			if (this.listBoxFinance.getSelectedItem() != null) {
				final Listitem li = this.listBoxFinance.getSelectedItem();
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
		//doClose();
		logger.debug("Leaving"+ event.toString());
	}
	
	
	public void openFinanceMainDialog(Listitem item) throws Exception {
		logger.debug("Entering ");
		// get the selected FinanceMain object
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			final FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(aFinanceMain.getId(),false,eventCodeRef);

			if(financeDetail==null){
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
							, "FinanceMain", whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
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
	
	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	protected void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
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
		map.put("tabbox",tab);
		map.put("moduleDefiner",moduleDefiner);
		map.put("eventCode",eventCodeRef);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceMainDialog.zul",null,map);
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
		}
		logger.debug("Leaving" + event.toString());
		
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		if(searchObject==null){
			searchObject=new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		}
		
		
		if (filterList != null && filterList.size() > 0) {
			for (int k = 0; k < filterList.size(); k++) {
				searchObject.addFilter(filterList.get(k));
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
	
	/**
	 * @return the financeDetailService
	 */
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	/**
	 * @param financeDetailService the financeDetailService to set
	 */
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
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
				if(tab.getId().equals("tab_AddRateChange")) {
					moduleDefiner = PennantConstants.ADD_RATE_CHG;
					eventCodeRef  = "RATCHG";
				}else if(tab.getId().equals("tab_ChangeRepayment")) {
					moduleDefiner = PennantConstants.CHG_REPAY;
					eventCodeRef  = "REPAY";
				}else if(tab.getId().equals("tab_AddDisbursment")) {
					moduleDefiner = PennantConstants.ADD_DISB;
					Filter filter = new Filter("lovDescFinIsAlwMD", 1, Filter.OP_EQUAL);
					filterList.add(filter);
					eventCodeRef  = "";
				}else if(tab.getId().equals("tab_AddDefferment")) {
					moduleDefiner = PennantConstants.ADD_DEFF;
					Filter filter = new Filter("lovDescFinAlwDeferment", 1, Filter.OP_EQUAL);
					filterList.add(filter);
					eventCodeRef  = "DEFRPY";
				}else if(tab.getId().equals("tab_RmvDefferment")) {
					moduleDefiner = PennantConstants.RMV_DEFF;
					Filter filter = new Filter("lovDescFinAlwDeferment", 1, Filter.OP_EQUAL);
					filterList.add(filter);
					eventCodeRef  = "DEFRPY";
				}else if(tab.getId().equals("tab_AddTerms")) {
					moduleDefiner = PennantConstants.ADD_TERMS;
					eventCodeRef  = "SCDCHG";
				}else if(tab.getId().equals("tab_RmvTerms")) {
					moduleDefiner = PennantConstants.RMV_TERMS;
					eventCodeRef  = "SCDCHG";
				}else if(tab.getId().equals("tab_Recalculate")) {
					moduleDefiner = PennantConstants.RECALC;
					eventCodeRef  = "SCDCHG";
				}else if(tab.getId().equals("tab_SchdlRepayment")) {
					moduleDefiner = PennantConstants.SCH_REPAY;
					setDialogCtrl("ManualPaymentDialogCtrl");
				}else if(tab.getId().equals("tab_EarlySettlement")) {
					moduleDefiner = PennantConstants.SCH_EARLYPAY;
					setDialogCtrl("ManualPaymentDialogCtrl");
				}else if(tab.getId().equals("tab_WriteOff")) {
					moduleDefiner = PennantConstants.WRITEOFF;
					setDialogCtrl("ManualPaymentDialogCtrl");
				}
				return;
			}
		}else{
			moduleDefiner="";
			return;
		}
		logger.debug("Leaving");
	}
}