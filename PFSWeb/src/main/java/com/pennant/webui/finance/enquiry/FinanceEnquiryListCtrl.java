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
 * FileName    		:  FinanceEnquiryListCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :   1-02-2011    														*
 *                                                                  						*
 * Modified Date    :   1-02-2011      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  1-02-2011  s       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.enquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReinstateFinanceService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Reports/FinanceEnquiryList.zul.
 */
public class FinanceEnquiryListCtrl extends GFCBaseListCtrl<FinanceEnquiry> {
	private static final long serialVersionUID = -6646226859133636932L;
	private static final Logger logger = Logger.getLogger(FinanceEnquiryListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected   Window     window_FinanceEnquiry;   	   // autoWired
	protected Borderlayout borderlayout_Enquiry;   // autoWired

	protected Textbox    custCIF;	               // autoWired
	protected Textbox    branchCode;               // autoWired
	protected Datebox    startDate_one;            // autoWired
	protected Datebox    startDate_two;            // autoWired
	protected Datebox    maturityDate_one;         // autoWired
	protected Datebox    maturityDate_two;         // autoWired
	protected Textbox    finRef;                   // autoWired
	protected Textbox    finProduct;               // autoWired
	protected Textbox    finType; 	               // autoWired
	protected Textbox    finCcy; 	               // autoWired
	protected Textbox    enquiryType; 	           // autoWired
	protected Label 	 label_startDate;		   // autoWired
	protected Label 	 label_maturityDate;	   // autoWired
	protected Row		 row_RadioGroup;		   // autoWired
	
	protected Label		 label_menu_filter;			   // autoWired
	protected Menu		 menu_filter;			   // autoWired
	protected Menupopup  menupopup_filter;	       // autoWired
	protected Listbox    listBoxEnquiryResult;	   // autoWired
	protected Paging     pagingEnquiryList;	       // autoWired
	
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

	protected Listbox   sortOperator_custCIF;      // autowired
	protected Listbox   sortOperator_Branch;       // autowired
	protected Listbox   sortOperator_StartDate;    // autowired
	protected Listbox   sortOperator_MaturityDate; // autowired
	protected Listbox   sortOperator_FinRef;       // autowired
	protected Listbox   sortOperator_FinProduct;   // autowired
	protected Listbox   sortOperator_FinType;      // autowired
	protected Listbox   sortOperator_FinCcy;       // autowired
	
	protected int   oldVar_sortOperator_custCIF = -1;      // autowired
	protected int   oldVar_sortOperator_Branch = -1;       // autowired
	protected int   oldVar_sortOperator_StartDate = -1;    // autowired
	protected int   oldVar_sortOperator_MaturityDate = -1; // autowired
	protected int   oldVar_sortOperator_FinRef = -1;       // autowired
	protected int   oldVar_sortOperator_FinProduct = -1;   // autowired
	protected int   oldVar_sortOperator_FinType = -1;      // autowired
	protected int   oldVar_sortOperator_FinCcy = -1;       // autowired

	protected Button     btnSearchCustCIF;	       // autoWired
	protected Button     btnSearchFinRef;	       // autoWired
	protected Button     btnSearchFinType;	       // autoWired
	protected Button     btnSearchBranch;          // autoWired
	protected Button     btnSearchFinProduct;      // autoWired
	protected Button     btnSearchFinCcy;      	   // autoWired

	protected Button     button_Search;	           // autoWired
	protected Button     button_Print;	           // autoWired
	protected Button     button_Reset;	           // autoWired

	protected Grid      grid_enquiryDetails;       // autoWired
	
	private transient boolean  approvedList=false; // autowired
	private transient boolean rejectedList= false;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	
	// not auto wired variables
	protected JdbcSearchObject<FinanceEnquiry> searchObj;

	private FinanceDetailService financeDetailService;
	private SuspenseService suspenseService;
	private OverdueChargeRecoveryService overdueChargeRecoveryService;
	
	private List<ValueLabel> enquiryList = PennantStaticListUtil.getEnquiryFilters(true);
	private transient ReinstateFinanceService reinstateFinanceService;
	/**
	 * default constructor.<br>
	 */
	public FinanceEnquiryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceEnquiry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		//Listbox Sorting
		
		this.listheader_FinType.setSortAscending(new FieldComparator("FinType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("FinType", false));
		
		this.listheader_FinProduct.setSortAscending(new FieldComparator("lovDescProductCodeName", true));
		this.listheader_FinProduct.setSortDescending(new FieldComparator("lovDescProductCodeName", false));
		
		this.listheader_CustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		
		this.listheader_FinRef.setSortAscending(new FieldComparator("FinReference", true));
		this.listheader_FinRef.setSortDescending(new FieldComparator("FinReference", false));
		
		this.listheader_FinBranch.setSortAscending(new FieldComparator("FinBranch", true));
		this.listheader_FinBranch.setSortDescending(new FieldComparator("FinBranch", false));
		
		this.listheader_FinStartDate.setSortAscending(new FieldComparator("FinStartDate", true));
		this.listheader_FinStartDate.setSortDescending(new FieldComparator("FinStartDate", false));
		
		this.listheader_NumberOfTerms.setSortAscending(new FieldComparator("NumberOfTerms", true));
		this.listheader_NumberOfTerms.setSortDescending(new FieldComparator("NumberOfTerms", false));
		
		this.listheader_MaturityDate.setSortAscending(new FieldComparator("MaturityDate", true));
		this.listheader_MaturityDate.setSortDescending(new FieldComparator("MaturityDate", false));
		
		this.listheader_FinCcy.setSortAscending(new FieldComparator("FinCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("FinCcy", false));
		
		this.listheader_FinAmount.setSortAscending(new FieldComparator("FinAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("FinAmount", false));
		
		this.listheader_CurFinAmount.setSortAscending(new FieldComparator("FinRepaymentAmount", true));
		this.listheader_CurFinAmount.setSortDescending(new FieldComparator("FinRepaymentAmount", false));
		
		//Search boxes Rendering and Storing Items into Listboxes

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Branch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_Branch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinRef.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinRef.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_StartDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiDateOperators()));
		this.sortOperator_StartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_MaturityDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiDateOperators()));
		this.sortOperator_MaturityDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinProduct.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinProduct.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (!"CHQPRNT".equals(enquiryType.getValue()) && !"FINENQ".equals(this.enquiryType.getValue())) {
			doFillFilterList(PennantStaticListUtil.getEnquiryFilters(false));
		} else {
			doFillFilterList(enquiryList);
		}

		//Check the rights
		doCheckRights();

		this.searchObj = new JdbcSearchObject<FinanceEnquiry>(FinanceEnquiry.class);
		
		//Set listbox height and set paging size
		this.borderlayout_Enquiry.setHeight(getBorderLayoutHeight());
		this.listBoxEnquiryResult.setHeight(getListBoxHeight(this.grid_enquiryDetails.getRows().getVisibleItemCount()+1));
		this.pagingEnquiryList.setPageSize(getListRows());
		this.pagingEnquiryList.setDetailed(true);
		this.listBoxEnquiryResult.setItemRenderer(new FinanceEnquiryListModelItemRenderer());

		logger.debug("Leaving" + event.toString());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinanceEnquiryList");
		logger.debug("Leaving");
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
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_FinType == Filter.OP_IN || this.oldVar_sortOperator_FinType == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceEnquiry, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceEnquiry, "FinanceType");
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
	 * when clicks on button "SearchFinProduct"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinProduct(Event event) {
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_FinProduct == Filter.OP_IN || this.oldVar_sortOperator_FinProduct == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceEnquiry, "Product", this.finProduct.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finProduct.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceEnquiry, "Product");
			if (dataObject instanceof String) {
				this.finProduct.setValue("");
			} else {
				Product details = (Product) dataObject;
				if (details != null) {
					this.finProduct.setValue(details.getProductCode());
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

		if(this.oldVar_sortOperator_FinCcy == Filter.OP_IN || this.oldVar_sortOperator_FinCcy == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceEnquiry, "Currency", this.finCcy.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finCcy.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceEnquiry, "Currency");
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
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * @param event
	 */
	public void onClick$btnSearchFinRef(Event event){
		logger.debug("Entering " + event.toString());
		String selectedValues=null;
		if(this.oldVar_sortOperator_FinCcy == Filter.OP_IN || this.oldVar_sortOperator_FinCcy == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
		if(this.fromApproved.isChecked()){
			 selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceEnquiry, "FinanceMainTemp", this.finRef.getValue(), new Filter[]{});
		}else{
			 selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceEnquiry, "FinanceMain", this.finRef.getValue(), new Filter[]{});
		}
		if (selectedValues!= null) {
			this.finRef.setValue(selectedValues);
		}
			
		}else{
			Object dataObject=null;
			if(this.fromApproved.isChecked()){
				 dataObject  = ExtendedSearchListBox.show(this.window_FinanceEnquiry,"FinanceMain");
				
			}else{
				 dataObject  = ExtendedSearchListBox.show(this.window_FinanceEnquiry,"FinanceMainTemp");
			}
			if (dataObject instanceof String){
				this.finRef.setValue("");
			}else{
				FinanceMain details= (FinanceMain) dataObject;
				if (details != null) {
					this.finRef.setValue(details.getFinReference());
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

		if(this.oldVar_sortOperator_Branch == Filter.OP_IN || this.oldVar_sortOperator_Branch == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_FinanceEnquiry, "Branch", this.branchCode.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.branchCode.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceEnquiry,"Branch");
			if (dataObject instanceof String){
				this.branchCode.setValue("");
			}else{
				Branch details= (Branch) dataObject;
				if (details != null) {
					this.branchCode.setValue(details.getBranchCode());
				}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When user clicks on button "button_Search" button
	 * @param event
	 */
	public void onClick$button_Search(Event event){
		logger.debug("Entering " + event.toString());
		
		doSearch();
		this.pagingEnquiryList.setDetailed(true);
		getPagedListWrapper().init(this.searchObj, this.listBoxEnquiryResult, this.pagingEnquiryList);
		this.listBoxEnquiryResult.setItemRenderer(new FinanceEnquiryListModelItemRenderer());
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "button_Print" button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$button_Print(Event event) throws InterruptedException{
		logger.debug("Entering " + event.toString());
		if(this.listBoxEnquiryResult.getItemCount()>0){			
			new PTListReportUtils(this.enquiryType.getValue(), getSearchObj(), this.pagingEnquiryList.getTotalSize()+1);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When user clicks on button "button_Reset" button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$button_Reset(Event event) throws InterruptedException{
		logger.debug("Entering " + event.toString());
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.sortOperator_Branch.setSelectedIndex(0);
		this.sortOperator_StartDate.setSelectedIndex(0);
		this.sortOperator_MaturityDate.setSelectedIndex(0);
		this.sortOperator_FinProduct.setSelectedIndex(0);
		this.sortOperator_FinType.setSelectedIndex(0);
		this.sortOperator_FinRef.setSelectedIndex(0);
		this.sortOperator_FinCcy.setSelectedIndex(0);
		
		this.custCIF.setValue("");
		this.branchCode.setValue("");
		this.startDate_one.setText("");
		this.startDate_two.setText("");
		this.label_startDate.setVisible(false);
		this.startDate_two.setVisible(false);
		this.maturityDate_one.setText("");
		this.maturityDate_two.setText("");
		this.label_maturityDate.setVisible(false);
		this.maturityDate_two.setVisible(false);
		this.finProduct.setValue("");
		this.finType.setValue("");
		this.finRef.setValue("");
		this.finCcy.setValue("");
		if (!"CHQPRNT".equals(enquiryType.getValue()) && !"FINENQ".equals(this.enquiryType.getValue())) {
			doFillFilterList(PennantStaticListUtil.getEnquiryFilters(false));
		} else {
			doFillFilterList(enquiryList);
		}

		oldVar_sortOperator_custCIF = -1;    
		this.oldVar_sortOperator_Branch = -1;      
		this.oldVar_sortOperator_StartDate = -1;   
		this.oldVar_sortOperator_MaturityDate = -1; 
		this.oldVar_sortOperator_FinRef = -1;      
		this.oldVar_sortOperator_FinProduct = -1;  
		this.oldVar_sortOperator_FinType = -1;     
		this.oldVar_sortOperator_FinCcy = -1;
		
		this.listBoxEnquiryResult.getItems().clear();
		this.pagingEnquiryList.setTotalSize(0);
		this.pagingEnquiryList.setActivePage(0);

		doSearch();
		this.row_RadioGroup.setVisible(true);
		this.pagingEnquiryList.setDetailed(true);
		getPagedListWrapper().init(this.searchObj, this.listBoxEnquiryResult, this.pagingEnquiryList);
		this.listBoxEnquiryResult.setItemRenderer(new FinanceEnquiryListModelItemRenderer());
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Rendering Menu item for Filtering
	 * @param list
	 */
	private void doFillFilterList(List<ValueLabel> list){
		logger.debug("Entering");
		
		this.menupopup_filter.getChildren().clear();
		if(list != null && list.size() > 0){
			Menuitem menuitem = null;
			for (ValueLabel enquiry : list) {
				menuitem = new Menuitem();
				menuitem.setLabel(enquiry.getLabel());
				menuitem.setValue(enquiry.getValue());
				menuitem.setStyle("font-weight:bold;");
				menuitem.addForward("onClick", this.window_FinanceEnquiry, "onFilterMenuItem", enquiry);

				//Menu Item Selection
				if("SUSENQ".equals(this.enquiryType.getValue()) && "SUSFIN".equals(enquiry.getValue())){
					this.menupopup_filter.getChildren().clear();
					this.menupopup_filter.appendChild(menuitem);
					this.menu_filter.setLabel(enquiry.getLabel());
					break;
				}
				if(("ODCENQ".equals(this.enquiryType.getValue()) || "LTPPENQ".equals(this.enquiryType.getValue()) || 
						"ODENQ".equals(this.enquiryType.getValue())) && "ODCFIN".equals(enquiry.getValue())){
					this.menupopup_filter.getChildren().clear();
					this.menupopup_filter.appendChild(menuitem);
					this.menu_filter.setLabel(enquiry.getLabel());
					break;
				}
				this.menupopup_filter.appendChild(menuitem);
				if("ACTFIN".equals(enquiry.getValue())){
					this.menu_filter.setLabel(enquiry.getLabel());
				}
			}
		}
		
		logger.debug("Leaving");
	}
	
	public void onFilterMenuItem(ForwardEvent event){
		if(event.getData() != null){
			ValueLabel enquiry = (ValueLabel) event.getData();
			this.menu_filter.setLabel(enquiry.getLabel());
		}
	}
	
	// On Change Events for Multi-Selection Listbox's for Search operators
	public void onSelect$sortOperator_custCIF(Event event){
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_custCIF, oldVar_sortOperator_custCIF, this.custCIF);
	}
	
	public void onSelect$sortOperator_Branch(Event event){
		this.oldVar_sortOperator_Branch = doChangeStringOperator(sortOperator_Branch, oldVar_sortOperator_Branch, this.branchCode);
	}
	
	public void onSelect$sortOperator_FinProduct(Event event){
		this.oldVar_sortOperator_FinProduct = doChangeStringOperator(sortOperator_FinProduct, oldVar_sortOperator_FinProduct, this.finProduct);
	}
	
	public void onSelect$sortOperator_FinType(Event event){
		this.oldVar_sortOperator_FinType = doChangeStringOperator(sortOperator_FinType, oldVar_sortOperator_FinType, this.finType);
	}
	
	public void onSelect$sortOperator_FinRef(Event event){
		this.oldVar_sortOperator_FinRef = doChangeStringOperator(sortOperator_FinRef, oldVar_sortOperator_FinRef, this.finRef);
	}
	
	public void onSelect$sortOperator_FinCcy(Event event){
		this.oldVar_sortOperator_FinCcy = doChangeStringOperator(sortOperator_FinCcy, oldVar_sortOperator_FinCcy, this.finCcy);
	}
	
	public void onSelect$sortOperator_StartDate(Event event){
		this.oldVar_sortOperator_StartDate = doChangeDateOperator(sortOperator_StartDate, oldVar_sortOperator_StartDate,
				this.startDate_one, this.startDate_two);
		this.startDate_two.setText("");
		if(oldVar_sortOperator_StartDate == Filter.OP_BETWEEN){
			this.startDate_two.setVisible(true);
			this.label_startDate.setVisible(true);
		}else{
			this.startDate_two.setVisible(false);
			this.label_startDate.setVisible(false);
			
		}
	}
	
	public void onSelect$sortOperator_MaturityDate(Event event){
		this.oldVar_sortOperator_MaturityDate = doChangeDateOperator(sortOperator_MaturityDate, oldVar_sortOperator_MaturityDate,
				this.maturityDate_one, this.maturityDate_two);
		this.maturityDate_two.setText("");
		if(oldVar_sortOperator_MaturityDate == Filter.OP_BETWEEN){
			this.maturityDate_two.setVisible(true);
			this.label_maturityDate.setVisible(true);
		}else{
			this.maturityDate_two.setVisible(false);
			this.label_maturityDate.setVisible(false);
		}
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
	
	private int doChangeDateOperator(Listbox listbox,int oldOperator,Datebox dateboxOne,Datebox dateboxTwo){
		
		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if(oldOperator == Filter.OP_BETWEEN && searchOpId != Filter.OP_BETWEEN){
			dateboxOne.setText("");
			dateboxTwo.setText("");
		}else{
			if(oldOperator != Filter.OP_BETWEEN && searchOpId == Filter.OP_BETWEEN){
				dateboxOne.setText("");
				dateboxTwo.setText("");
			}
		}
		return searchOpId;
	}

	// Helpers

	/**
	 * This method Fetch the records from finance main table by adding filters 
	 * @return
	 */
	public void doSearch(){
		logger.debug("Entering");
		
		this.searchObj = new JdbcSearchObject<FinanceEnquiry>(FinanceEnquiry.class);
		rejectedList=false;
		if(!"CHQPRNT".equals(enquiryType.getValue())){
			if(this.fromApproved.isSelected()){
				approvedList=true;
			}else{
				approvedList=false;
			}
		} else {
			approvedList = true;
		}
		if("ODENQ".equalsIgnoreCase(enquiryType.getValue())){
			this.searchObj.addTabelName("FinOverdueEnquiry_View");
		}else if("ODCENQ".equalsIgnoreCase(enquiryType.getValue())){
			this.searchObj.addTabelName("FinOverdueEnquiry_View");
		}else{
			if(approvedList){
				this.searchObj.addTabelName("FinanceEnquiry_View");
			}else{
				this.searchObj.addTabelName("FinanceEnquiry_TView");
			}
		}
		
		this.searchObj.addSort("finReference", false);
		if ("FINMANDENQ".equalsIgnoreCase(enquiryType.getValue())) {
			List<ValueLabel> mandates = PennantStaticListUtil.getMandateTypeList();
			List<String> list=new ArrayList<String>(mandates.size());
			for (ValueLabel valueLabel : mandates) {
				list.add(valueLabel.getValue());
			}
			this.searchObj.addFilterIn("FinRepayMethod",list ,false);
		}
		
		//Condition checking for Filter selection
		this.row_RadioGroup.setVisible(true);
		if("CHQPRNT".equals(this.enquiryType.getValue())) {
			this.searchObj.addFilter(new Filter("FinIsActive", 1, Filter.OP_EQUAL));
		}else{
			String value = "";
			if(!"FINENQ".equals(this.enquiryType.getValue())){
				value = PennantAppUtil.getValueDesc(this.menu_filter.getLabel(), PennantStaticListUtil.getEnquiryFilters(false));
			} else {
				value = PennantAppUtil.getValueDesc(this.menu_filter.getLabel(), enquiryList);
			}
			if("ALLFIN".equals(value)){
				//Nothing to do
			}else if("ACTFIN".equals(value)){
				this.searchObj.addFilter(new Filter("FinIsActive", 1, Filter.OP_EQUAL));
			}else if("MATFIN".equals(value)){
				this.searchObj.addFilter(new Filter("FinIsActive", 0, Filter.OP_EQUAL));
			}else if("ODCFIN".equals(value)){
				this.searchObj.addWhereClause(" FinReference IN (SELECT FinReference from FinODDetails where FinCurODAmt > 0 )");
 			}else if("SUSFIN".equals(value)){
				List<String> list = getSuspenseService().getSuspFinanceList();
				if(list != null && list.size() > 0){
					String[] finList = list.toArray(new String[list.size()]);
					this.searchObj.addFilter(new Filter("FinReference", finList, Filter.OP_IN));
				}else{
					this.searchObj.addFilter(new Filter("FinReference", "" , Filter.OP_EQUAL));
				}
			}else if("GPFIN".equals(value)){
				this.searchObj.addFilter(new Filter("AllowGrcPeriod", 1, Filter.OP_EQUAL));
			}else if("REJFIN".equals(value)){
				rejectedList = true;
				this.row_RadioGroup.setVisible(false);
				this.searchObj.addTabelName("RejectFinanceMain_View");
			}
		}

		if (StringUtils.isNotBlank(this.custCIF.getValue())) {

			// get the search operator
			final Listitem itemCustID = this.sortOperator_custCIF.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", "%"
							+ this.custCIF.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","),Filter.OP_IN));
				}else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","),Filter.OP_NOT_IN));
				}else {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotBlank(this.branchCode.getValue())) {

			// get the search operator
			final Listitem itemCustID = this.sortOperator_Branch.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("finBranch", "%" 
							+ this.branchCode.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("FinBranch", this.branchCode.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinBranch", this.branchCode.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("FinBranch",this.branchCode.getValue().trim(), searchOpId));
				}
			}
		}
		
		if (this.startDate_one.getValue() != null || this.startDate_two.getValue() != null) {

			// get the search operator
			final Listitem itemStartDate = this.sortOperator_StartDate.getSelectedItem();
			if (itemStartDate != null) {
				final int searchOpId = ((SearchOperators) itemStartDate
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_BETWEEN) {
					if (this.startDate_one.getValue() != null) {
						this.searchObj.addFilter(new Filter("FinStartDate",DateUtility.formatUtilDate(
								this.startDate_one.getValue(),PennantConstants.DBDateFormat), Filter.OP_GREATER_OR_EQUAL));
					}
					if (this.startDate_two.getValue() != null) {
						this.searchObj.addFilter(new Filter("FinStartDate",DateUtility.formatUtilDate(
								this.startDate_two.getValue(),PennantConstants.DBDateFormat), Filter.OP_LESS_OR_EQUAL));
					}
				} else {
					this.searchObj.addFilter(new Filter("FinStartDate",DateUtility.formatUtilDate(
							this.startDate_one.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
		
		if (this.maturityDate_one.getValue() != null || this.maturityDate_two.getValue() != null) {
			
			// get the search operator
			final Listitem itemStartDate = this.sortOperator_MaturityDate.getSelectedItem();
			if (itemStartDate != null) {
				final int searchOpId = ((SearchOperators) itemStartDate
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_BETWEEN) {
					if (this.maturityDate_one.getValue() != null) {
						this.searchObj.addFilter(new Filter("MaturityDate",DateUtility.formatUtilDate(
								this.maturityDate_one.getValue(),PennantConstants.DBDateFormat), Filter.OP_GREATER_OR_EQUAL));
					}
					if(this.maturityDate_two.getValue() != null){
						this.searchObj.addFilter(new Filter("MaturityDate",DateUtility.formatUtilDate(
								this.maturityDate_two.getValue(),PennantConstants.DBDateFormat), Filter.OP_LESS_OR_EQUAL));
					}
				} else {
					this.searchObj.addFilter(new Filter("MaturityDate",DateUtility.formatUtilDate(
							this.maturityDate_one.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.finRef.getValue())) {

			// get the search operator
			final Listitem itemCustID = this.sortOperator_FinRef.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("FinReference", "%" 
							+ this.finRef.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("FinReference", this.finRef.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinReference", this.finRef.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("FinReference", this.finRef.getValue().trim(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotBlank(this.finType.getValue())) {

			// get the search operator
			final Listitem itemCustID = this.sortOperator_FinType.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("FinType", "%" 
							+ this.finType.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotBlank(this.finProduct.getValue())) {

			// get the search operator
			final Listitem itemCustID = this.sortOperator_FinProduct.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", "%" 
							+ this.finProduct.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", this.finProduct.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", this.finProduct.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", this.finProduct.getValue().trim(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotBlank(this.finCcy.getValue())) {
			// get the search operator
			final Listitem itemCustID = this.sortOperator_FinCcy.getSelectedItem();
			if (itemCustID != null) {
				final int searchOpId = ((SearchOperators) itemCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("FinCcy", "%" 
							+ this.finCcy.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim(), searchOpId));
				}
			}
		}
		if (!rejectedList) {
			searchObj.addWhereClause(getUsrFinAuthenticationQry(false));
			if (getUserWorkspace().isAllowed("isUserSpeicfic_Enquiry") && !approvedList) {
				this.searchObj.removeFiltersOnProperty("FinReference");

				String whereClause = searchObj.getWhereClause();
				whereClause = whereClause.concat(setFinReferences());
				searchObj.addWhereClause(whereClause);
			}
		}
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxEnquiryResult,this.pagingEnquiryList);
		logger.debug("Leaving");
	}

	private String setFinReferences() {
		logger.debug("Entering");

		String reference = StringUtils.trimToEmpty(finRef.getValue());

		// Prepare the additional where clause.
		StringBuilder sql = new StringBuilder();
		sql.append(" and FinReference in (select Reference");
		sql.append(" from Task_Owners");
		sql.append(" where (");
		if (StringUtils.isNotEmpty(reference)) {
			int operatorId = 0;
			String operatorSign = "=";

			if (sortOperator_FinRef.getSelectedItem() != null) {
				operatorId = ((SearchOperators) sortOperator_FinRef.getSelectedItem().getAttribute("data"))
						.getSearchOperatorId();
				operatorSign = ((SearchOperators) sortOperator_FinRef.getSelectedItem().getAttribute("data"))
						.getSearchOperatorSign();
			}

			if (operatorId == 6) {
				reference = "%" + reference + "%";
				operatorSign = "like";
			}

			sql.append(" (Reference ").append(operatorSign).append(" '").append(reference.replace("'", "''"))
					.append("') and");
		}
		sql.append(" (CurrentOwner = ").append(getUserWorkspace().getLoggedInUser().getLoginUsrID()).append(')');
		sql.append("))");

		// Get the result set
		logger.debug("Leaving");
		return sql.toString();
	}

	/**
	 * when the "help" button is clicked. <br>
	 *
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_FinanceEnquiry);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for fetching Finance Record
	 * @param event
	 * @throws Exception
	 */
	public void onLoanItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxEnquiryResult.getSelectedItem();
		if (item != null) {

			final FinanceEnquiry aFinanceEnquiry = (FinanceEnquiry) item.getAttribute("data");
			if (!rejectedList) {

				Map<String, Object> map = getDefaultArguments();
				map.put("financeEnquiry", aFinanceEnquiry);
				map.put("financeEnquiryListCtrl", this);
				map.put("enquiryType", this.enquiryType.getValue());
				if (!StringUtils.equals(enquiryType.getValue(), "CHQPRNT")) {
					map.put("fromApproved", this.fromApproved.isChecked());
				}

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			} else {
				ReinstateFinance aReinstateFinance = reinstateFinanceService.getFinanceDetailsById(aFinanceEnquiry
						.getFinReference());
				Map<String, Object> arg = getDefaultArguments();
				arg.put("reinstateFinance", aReinstateFinance);
				arg.put("financeEnquiryListCtrl", this);
				arg.put("enqModule", true);
				arg.put("eventCode", FinanceConstants.FINSER_EVENT_REINSTATE);
				arg.put("rejectedList", rejectedList);
				try {

					Executions.createComponents("/WEB-INF/pages/Finance/ReinstateFinance/ReinstateFinanceDialog.zul",
							null, arg);

				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
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
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<FinanceEnquiry> getSearchObjFinMain() {
		return searchObj;
	}
	public void setSearchObjFinMain(JdbcSearchObject<FinanceEnquiry> searchObjFinMain) {
		this.searchObj = searchObjFinMain;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}
	public SuspenseService getSuspenseService() {
		return suspenseService;
	}

	public void setOverdueChargeRecoveryService(
			OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}
	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return overdueChargeRecoveryService;
	}

	public JdbcSearchObject<FinanceEnquiry> getSearchObj() {
		return searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceEnquiry> searchObj) {
		this.searchObj = searchObj;
	}
	
	public void setReinstateFinanceService(ReinstateFinanceService reinstateFinanceService) {
		this.reinstateFinanceService = reinstateFinanceService;
	}
	public ReinstateFinanceService getReinstateFinanceService() {
		return this.reinstateFinanceService;
	}
}
