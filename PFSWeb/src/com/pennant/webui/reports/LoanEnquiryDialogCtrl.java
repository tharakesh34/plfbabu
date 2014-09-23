
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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LoanEnquiryDialogCtrl.java                                              * 	  
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
 *//*

package com.pennant.webui.reports;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.reports.LoanEnquiry;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.PaymentService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.reports.model.LoanEnquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Reports/LoanEnquiryDialog.zul. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 *//*
public class LoanEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {

	private static final long serialVersionUID = -6646226859133636932L;
	private final static Logger logger = Logger.getLogger(LoanEnquiryDialogCtrl.class);
	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	private   Window     window_LoanEnquiry;   	   // autoWired
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
	protected Menu		 menu_filter;			   // autoWired
	protected Menupopup  menupopup_filter;	       // autoWired

	protected Listbox    listBoxEnquiryResult;	   // autoWired
	protected Paging     pagingEnquiryList;	       // autoWired

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

	protected Grid      grid_enquiryDetails;       // autowired

	// not auto wired variables
	private   LoanEnquiry loanEnquiry=new LoanEnquiry();
	protected JdbcSearchObject<Customer>    newSearchObject;
	protected JdbcSearchObject<FinanceMain> searchObj;

	private FinanceDetailService financeDetailService;
	private SuspenseService suspenseService;
	private OverdueChargeRecoveryService overdueChargeRecoveryService;
	private PaymentService paymentService;
	private Map<Date,List<FinanceRepayments>> paymentDetailsMap;
	private List<ValueLabel> enquiryList = PennantStaticListUtil.getEnquiryFilters();
	int listRows;

	*//**
	 * default constructor.<br>
	 *//*
	public LoanEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_LoanEnquiry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.sortOperator_custCIF.setModel(new ListModelList(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Branch.setModel(new ListModelList(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_Branch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinRef.setModel(new ListModelList(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinRef.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinType.setModel(new ListModelList(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_StartDate.setModel(new ListModelList(new SearchOperators().getMultiDateOperators()));
		this.sortOperator_StartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_MaturityDate.setModel(new ListModelList(new SearchOperators().getMultiDateOperators()));
		this.sortOperator_MaturityDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinProduct.setModel(new ListModelList(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinProduct.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_FinCcy.setModel(new ListModelList(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_FinCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		doFillFilterList(enquiryList);

		//Check the rights
		doCheckRights();

		//Set listbox height and set paging size
		getBorderLayoutHeight();
		this.grid_enquiryDetails.getRows().getVisibleItemCount();
		this.borderlayout_Enquiry.setHeight(getBorderLayoutHeight());
		int dialogHeight =  grid_enquiryDetails.getRows().getVisibleItemCount()* 20 + 130 ; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxEnquiryResult.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 24);
		this.pagingEnquiryList.setPageSize(listRows);

		logger.debug("Leavinging" + event.toString());

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * When user clicks on  "btnSearchCustCIF" button
	 * @param event
	 *//*
	public void onClick$btnSearchCustCIF(Event event) throws  SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_custCIF == 8 || this.oldVar_sortOperator_custCIF == 9){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_LoanEnquiry, "Customer", this.custCIF.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.custCIF.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_LoanEnquiry, "Customer");
			if (dataObject instanceof String) {
				this.custCIF.setValue("");
				this.loanEnquiry.setCustID(0);
			} else {
				Customer details = (Customer) dataObject;
				if (details != null) {
					this.custCIF.setValue(details.getCustCIF());
					this.loanEnquiry.setCustID(details.getCustID());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 *//*
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_FinType == 8 || this.oldVar_sortOperator_FinType == 9){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_LoanEnquiry, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_LoanEnquiry, "FinanceType");
			if (dataObject instanceof String) {
				this.finType.setValue("");
				this.loanEnquiry.setLovDescFinTypeName("");
			} else {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
					this.loanEnquiry.setLovDescFinTypeName(details.getFinTypeDesc());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * when clicks on button "SearchFinProduct"
	 * 
	 * @param event
	 *//*
	public void onClick$btnSearchFinProduct(Event event) {
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_FinProduct == 8 || this.oldVar_sortOperator_FinProduct == 9){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_LoanEnquiry, "Product", this.finProduct.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finProduct.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_LoanEnquiry, "Product");
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

	*//**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 *//*
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_FinCcy == 8 || this.oldVar_sortOperator_FinCcy == 9){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_LoanEnquiry, "Currency", this.finCcy.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finCcy.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_LoanEnquiry, "Currency");
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

	*//**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * @param event
	 *//*
	public void onClick$btnSearchFinRef(Event event){
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_FinCcy == 8 || this.oldVar_sortOperator_FinCcy == 9){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_LoanEnquiry, "FinanceMain", this.finRef.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finRef.setValue(selectedValues);
			}
			
		}else{
			Object dataObject  = ExtendedSearchListBox.show(this.window_LoanEnquiry,"FinanceMain");

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
	
	*//**
	 * When user clicks on "btnSearchBranchCode" button
	 * This method displays ExtendedSearchListBox with branch details
	 * @param event
	 *//*
	public void onClick$btnSearchBranch(Event event){
		logger.debug("Entering  "+event.toString());

		if(this.oldVar_sortOperator_Branch == 8 || this.oldVar_sortOperator_Branch == 9){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_LoanEnquiry, "Branch", this.branchCode.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.branchCode.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_LoanEnquiry,"Branch");
			if (dataObject instanceof String){
				this.branchCode.setValue("");
				this.loanEnquiry.setLovDescFinBranchName("");
			}else{
				Branch details= (Branch) dataObject;
				if (details != null) {
					this.branchCode.setValue(details.getBranchCode());
					this.loanEnquiry.setLovDescFinBranchName(details.getBranchDesc());
				}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	*//**
	 * When user clicks on button "button_Search" button
	 * @param event
	 *//*
	public void onClick$button_Search(Event event){
		logger.debug("Entering " + event.toString());
		doWriteComponentsToBean(this.loanEnquiry);
		doSearch();
		this.pagingEnquiryList.setDetailed(true);
		getPagedListWrapper().init(this.searchObj, this.listBoxEnquiryResult, this.pagingEnquiryList);
		this.listBoxEnquiryResult.setItemRenderer(new LoanEnquiryListModelItemRenderer());
		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * When user clicks on button "button_Print" button
	 * @param event
	 * @throws InterruptedException 
	 *//*
	public void onClick$button_Print(Event event) throws InterruptedException{
		logger.debug("Entering " + event.toString());
		if(getLoanEnquiry().getFinanceMainList()!=null && getLoanEnquiry().getFinanceMainList().size()>0){
			ReportGenerationUtil.generateReport("LoanEnquiry", getLoanEnquiry(),
					getLoanEnquiry().getFinanceMainList(),true, 1, getUserWorkspace().getUserDetails().getUsername(),null);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	*//**
	 * When user clicks on button "button_Reset" button
	 * @param event
	 * @throws InterruptedException 
	 *//*
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
		doFillFilterList(enquiryList);
		
		this.listBoxEnquiryResult.getItems().clear();
		logger.debug("Leaving " + event.toString());
	}
	
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
				menuitem.addForward("onClick", this.window_LoanEnquiry, "onFilterMenuItem", enquiry);

				//Menu Item Selection
				if(this.enquiryType.getValue().equals("SUSPENSE") && enquiry.getValue().equals("SUSFIN")){
					this.menupopup_filter.getChildren().clear();
					this.menupopup_filter.appendChild(menuitem);
					this.menu_filter.setLabel(enquiry.getLabel());
					break;
				}
				if(this.enquiryType.getValue().equals("OVERDUE") && enquiry.getValue().equals("ODCFIN")){
					this.menupopup_filter.getChildren().clear();
					this.menupopup_filter.appendChild(menuitem);
					this.menu_filter.setLabel(enquiry.getLabel());
					break;
				}
				this.menupopup_filter.appendChild(menuitem);
				if(enquiry.getValue().equals("ACTFIN")){
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
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +On Change Events for Multi-Selection Listbox's for Search operators+ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

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
	
	private int doChangeDateOperator(Listbox listbox,int oldOperator,Datebox datebox_one,Datebox datebox_two){
		
		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if(oldOperator == Filter.OP_BETWEEN && searchOpId != Filter.OP_BETWEEN){
			datebox_one.setText("");
			datebox_two.setText("");
		}else{
			if(oldOperator != Filter.OP_BETWEEN && searchOpId == Filter.OP_BETWEEN){
				datebox_one.setText("");
				datebox_two.setText("");
			}
		}
		return searchOpId;
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 *//*
	public void doWriteComponentsToBean(LoanEnquiry aLoanEnquiry) {
		logger.debug("Entering");
		aLoanEnquiry.setLovDescCustCIF(this.custCIF.getValue());
		aLoanEnquiry.setFinReference(this.finRef.getValue());
		aLoanEnquiry.setFinBranch(this.branchCode.getValue());
		aLoanEnquiry.setFinType(this.finType.getValue());
		setLoanEnquiry(aLoanEnquiry);
	}

	*//**
	 * This method Fetch the records from financemain table by adding filters 
	 * @return
	 *//*
	public void doSearch(){

		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		this.searchObj.addTabelName("FinanceMain_AView");
		
		String value = PennantAppUtil.getValueDesc(this.menu_filter.getLabel(), enquiryList);
		if("ALLFIN".equals(value)){
			//Nothing to do
		}else if("ACTFIN".equals(value)){
			this.searchObj.addFilter(new Filter("FinIsActive", 1, Filter.OP_EQUAL));
		}else if("MATFIN".equals(value)){
			this.searchObj.addFilter(new Filter("MaturityDate", SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR), Filter.OP_LESS_THAN));
		}else if("ODCFIN".equals(value)){
			List<String> list = getOverdueChargeRecoveryService().getOverDueFinanceList();
			if(list != null && list.size() > 0){
				String[] finList = list.toArray(new String[list.size()]);
				this.searchObj.addFilter(new Filter("FinReference", finList , Filter.OP_IN));
			}else{
				this.searchObj.addFilter(new Filter("FinReference", "" , Filter.OP_EQUAL));
			}
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
		}

		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_custCIF.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", "%"
							+ this.custCIF.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilterIn("lovDescCustCIF", this.custCIF.getValue().trim().split(","));
				}else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilterNotIn("lovDescCustCIF", this.custCIF.getValue().trim().split(","));
				}else {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim(), searchOpId));
				}
			}
		}
		if (!StringUtils.trimToEmpty(this.branchCode.getValue()).equals("")) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_Branch.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("finBranch", "%" 
							+ this.branchCode.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilterIn("finBranch", this.branchCode.getValue().trim().split(","));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilterNotIn("finBranch", this.branchCode.getValue().trim().split(","));
				} else {
					this.searchObj.addFilter(new Filter("finBranch",this.branchCode.getValue().trim(), searchOpId));
				}
			}
		}
		
		if (this.startDate_one.getValue() != null || this.startDate_two.getValue() != null) {

			// get the search operator
			final Listitem item_StartDate = this.sortOperator_StartDate.getSelectedItem();

			if (item_StartDate != null) {
				final int searchOpId = ((SearchOperators) item_StartDate
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
			final Listitem item_StartDate = this.sortOperator_MaturityDate.getSelectedItem();
			
			if (item_StartDate != null) {
				final int searchOpId = ((SearchOperators) item_StartDate
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

		if (!StringUtils.trimToEmpty(this.finRef.getValue()).equals("")) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_FinRef.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("FinReference", "%" 
							+ this.finRef.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilterIn("FinReference", this.finRef.getValue().trim().split(","));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilterNotIn("FinReference", this.finRef.getValue().trim().split(","));
				} else {
					this.searchObj.addFilter(new Filter("FinReference", this.finRef.getValue().trim(), searchOpId));
				}
			}
		}

		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_FinType.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("FinType", "%" 
							+ this.finType.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilterIn("FinType", this.finType.getValue().trim().split(","));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilterNotIn("FinType", this.finType.getValue().trim().split(","));
				} else {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim(), searchOpId));
				}
			}
		}
		if (!StringUtils.trimToEmpty(this.finProduct.getValue()).equals("")) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_FinProduct.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", "%" 
							+ this.finProduct.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilterIn("LovDescProductCodeName", this.finProduct.getValue().trim().split(","));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilterNotIn("LovDescProductCodeName", this.finProduct.getValue().trim().split(","));
				} else {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", this.finProduct.getValue().trim(), searchOpId));
				}
			}
		}
		if (!StringUtils.trimToEmpty(this.finCcy.getValue()).equals("")) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_FinCcy.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("FinCcy", "%" 
							+ this.finCcy.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilterIn("FinCcy", this.finCcy.getValue().trim().split(","));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilterNotIn("FinCcy", this.finCcy.getValue().trim().split(","));
				} else {
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim(), searchOpId));
				}
			}
		}
	}

	*//**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_LoanEnquiry);
		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * Methodd for fetching Finance Record
	 * @param event
	 * @throws Exception
	 *//*
	public void onLoanItemDoubleClicked(Event event) throws Exception {

		final Listitem item = this.listBoxEnquiryResult.getSelectedItem();
		if (item != null) {
			
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			
			String[] valueParm = new String[1];
			String[] errParm = new String[1];
			valueParm[0] = aFinanceMain.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
					PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
			
			final FinanceDetail financeDetails;
			final FinScheduleData finScheduleData;
			
			if("REPAYMENT".equals(this.enquiryType.getValue())) {
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(aFinanceMain.getFinReference(), "_AView");
				paymentDetailsMap = getPaymentService().getFinanceRepaymentsByFinRef(aFinanceMain.getFinReference(),"");
				if (finScheduleData == null) {
					PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
				} else {
					showDetailView(finScheduleData);
				}
			}else if("SUSPENSE".equals(this.enquiryType.getValue())) {
				final FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(aFinanceMain.getFinReference(),true);

				if(suspHead==null){
					PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
				}else{
					showDetailView(suspHead);
				}	
			}else if("OVERDUE".equals(this.enquiryType.getValue())) {
				showDetailView(null);
			}else {
				financeDetails = getFinanceDetailService().getStaticFinanceDetailById(aFinanceMain.getFinReference(), "_AView");
				if (financeDetails == null) {
					PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
				} else {
					showDetailView(financeDetails);
				}
			}
		}
	}

	*//**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 *//*
	protected void showDetailView(Object object) throws Exception {
		logger.debug("Entering");

		String path = "";
		final HashMap<String, Object> map = new HashMap<String, Object>();
		if("FINANCE".equals(this.enquiryType.getValue())){
			map.put("financeDetail", (FinanceDetail)object);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul";
		}else if("SUSPENSE".equals(this.enquiryType.getValue())){
			map.put("suspHead", (FinanceSuspHead)object);
			path = "/WEB-INF/pages/Enquiry/SuspInquiry/SuspDetailEnquiryDialog.zul";
		}else if("REPAYMENT".equals(this.enquiryType.getValue())){
			map.put("finSchData", (FinScheduleData)object);
			map.put("paymentDetailsMap", paymentDetailsMap);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul";
		}else if("OVERDUE".equals(this.enquiryType.getValue())){
			map.put("finReference", "");
			path = "/WEB-INF/pages/Enquiry/OverDueInquiry/OverdueDetailList.zul";
		}

		map.put("loanEnquiryDialogCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(path,null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	*//**
	 * SetVisible for components by checking if there's a right for it.
	 *//*
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("LoanEnquiryDialog");
		this.button_Print.setVisible(getUserWorkspace().isAllowed("button_LoanEnquiryDialog_PrintList"));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public LoanEnquiry getLoanEnquiry() {
		return loanEnquiry;
	}
	public void setLoanEnquiry(LoanEnquiry loanEnquiry) {
		this.loanEnquiry = loanEnquiry;
	}

	public JdbcSearchObject<FinanceMain> getSearchObjFinMain() {
		return searchObj;
	}
	public void setSearchObjFinMain(JdbcSearchObject<FinanceMain> searchObjFinMain) {
		this.searchObj = searchObjFinMain;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	public PaymentService getPaymentService() {
		return paymentService;
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
}
*/