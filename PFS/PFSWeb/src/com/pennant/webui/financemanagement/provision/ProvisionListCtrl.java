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
 * FileName    		:  ProvisionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.provision;


import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.financemanagement.provision.model.ProvisionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Provision/Provision/ProvisionList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ProvisionListCtrl extends GFCBaseListCtrl<Provision> implements Serializable {

	private static final long serialVersionUID = 4481377123949925578L;
	private final static Logger logger = Logger.getLogger(ProvisionListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ProvisionList; 		// autowired
	protected Borderlayout 	borderLayout_ProvisionList; // autowired
	protected Paging 		pagingProvisionList; 		// autowired
	protected Listbox 		listBoxProvision; 			// autowired

	// List headers
	protected Listheader listheader_FinReference; 	// autowired
	protected Listheader listheader_CustID; 		// autowired
	protected Listheader listheader_UseNFProv; 		// autowired
	protected Listheader listheader_DueFromDate; 	// autowired
	protected Listheader listheader_ProvisionCalDate; 	// autowired
	protected Listheader listheader_ProvisionDue; 	// autowired
	protected Listheader listheader_LastFullyPaidDate; 	// autowired

	// checkRights
	protected Button btnHelp; 										// autowired
	protected Button button_ProvisionList_NewProvision; 			// autowired
	protected Button button_ProvisionList_ProvisionSearchDialog; 	// autowired
	protected Button button_ProvisionList_PrintList; 				// autowired
	
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
	protected Button     button_Reset;	           // autoWired

	protected Grid      grid_enquiryDetails;       // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Provision> searchObj;
	private transient ProvisionService provisionService;
	private transient WorkFlowDetails workFlowDetails=null;
	protected Textbox moduleName;
	private String module = "";
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public ProvisionListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Provision object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvisionList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(moduleName.getValue().equals("PROV")){
			this.module="Provision";
		}else if(moduleName.getValue().equals("PROVENQ")){
			this.module="ProvisionEnquiry";
		}

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Provision");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Provision");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_ProvisionList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingProvisionList.setPageSize(getListRows());
		this.pagingProvisionList.setDetailed(true);
		
		//Listbox Sorting

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		
		this.listheader_CustID.setSortAscending(new FieldComparator("custID", true));
		this.listheader_CustID.setSortDescending(new FieldComparator("custID", false));
		
		this.listheader_UseNFProv.setSortAscending(new FieldComparator("useNFProv", true));
		this.listheader_UseNFProv.setSortDescending(new FieldComparator("useNFProv", false));
		
		this.listheader_DueFromDate.setSortAscending(new FieldComparator("dueFromDate", true));
		this.listheader_DueFromDate.setSortDescending(new FieldComparator("dueFromDate", false));
		
		this.listheader_ProvisionCalDate.setSortAscending(new FieldComparator("provisionCalDate", true));
		this.listheader_ProvisionCalDate.setSortDescending(new FieldComparator("provisionCalDate", false));
		
		this.listheader_ProvisionDue.setSortAscending(new FieldComparator("provisionDue", true));
		this.listheader_ProvisionDue.setSortDescending(new FieldComparator("provisionDue", false));
		
		this.listheader_LastFullyPaidDate.setSortAscending(new FieldComparator("lastFullyPaidDate", true));
		this.listheader_LastFullyPaidDate.setSortDescending(new FieldComparator("lastFullyPaidDate", false));
		
		if("Provision".equals(this.module)){
			// ++ create the searchObject and init sorting ++//
			this.searchObj = new JdbcSearchObject<Provision>(Provision.class,getListRows());
			this.searchObj.addSort("FinReference", false);

			// Workflow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("FinProvisions_View");
				if (isFirstTask()) {
					button_ProvisionList_NewProvision.setVisible(true);
				} else {
					button_ProvisionList_NewProvision.setVisible(false);
				}

				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
			}else{
				this.searchObj.addTabelName("FinProvisions_AView");
			}

			setSearchObj(this.searchObj);
			if (!isWorkFlowEnabled() && wfAvailable){
				this.button_ProvisionList_NewProvision.setVisible(false);
				this.button_ProvisionList_ProvisionSearchDialog.setVisible(false);
				this.button_ProvisionList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				// Set the ListModel for the articles.
				getPagedListWrapper().init(this.searchObj,this.listBoxProvision,this.pagingProvisionList);
				// set the itemRenderer
				this.listBoxProvision.setItemRenderer(new ProvisionListModelItemRenderer());
			}
		}else{
			//Search boxes Rendering and Storing Items into Listboxes

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
			
			//Set listbox height and set paging size
			int dialogHeight =  grid_enquiryDetails.getRows().getVisibleItemCount()* 20 + 128 ; 
			int listboxHeight = borderLayoutHeight-dialogHeight;
			listBoxProvision.setHeight(listboxHeight+"px");
			listRows = Math.round(listboxHeight/ 24);
			this.pagingProvisionList.setPageSize(listRows);
			
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ProvisionList");

		if(this.module.equals("Provision")){
			this.button_ProvisionList_NewProvision.setVisible(getUserWorkspace()
					.isAllowed("button_ProvisionList_NewProvision"));
			this.button_ProvisionList_ProvisionSearchDialog.setVisible(getUserWorkspace()
					.isAllowed("button_ProvisionList_ProvisionFindDialog"));
		}else{
			this.button_ProvisionList_NewProvision.setVisible(false);
		}
		
		this.button_ProvisionList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ProvisionList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.provision.provision.model.ProvisionListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onProvisionItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Provision object
		final Listitem item = this.listBoxProvision.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Provision aProvision = (Provision) item.getAttribute("data");

			boolean isEnquiry = true;
			if(this.module.equals("Provision")){
				isEnquiry = false;
			}
			final Provision provision = getProvisionService().getProvisionById(aProvision.getId(),isEnquiry);

			if(provision==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aProvision.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ provision.getFinReference()+
					"' AND version=" + provision.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Provision", 
							whereCond, provision.getTaskId(), provision.getNextTaskId());
					if (userAcces){
						showDetailView(provision);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(provision);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Provision dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProvisionList_NewProvision(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Provision object, We GET it from the backend.
		final Provision aProvision = getProvisionService().getNewProvision();
		showDetailView(aProvision);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Provision (aProvision)
	 * @throws Exception
	 */
	private void showDetailView(Provision aProvision) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aProvision.getWorkflowId()==0 && isWorkFlowEnabled()){
			aProvision.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("provision", aProvision);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the ProvisionListbox from the
		 * dialog when we do a delete, edit or insert a Provision.
		 */
		map.put("provisionListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			if(this.module.equals("Provision")){
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Provision/ProvisionDialog.zul",
						null,map);
			}else{
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/ProvisionMovement/ProvisionMovementEnquiryDialog.zul",
						null,map);
			}
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
		PTMessageUtils.showHelpWindow(event, window_ProvisionList);
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
		this.pagingProvisionList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ProvisionList, event);
		this.window_ProvisionList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Provision dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProvisionList_ProvisionSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our ProvisionDialog zul-file with parameters. So we can
		 * call them with a object of the selected Provision. For handed over
		 * these parameter only a Map is accepted. So we put the Provision object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("provisionCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Provision/ProvisionSearchDialog.zul",
					null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the provision print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ProvisionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("Provision", getSearchObj());
		logger.debug("Leaving" + event.toString());
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
					this.window_ProvisionList, "Customer", this.custCIF.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.custCIF.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_ProvisionList, "Customer");
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
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());
		
		if(this.oldVar_sortOperator_FinType == Filter.OP_IN || this.oldVar_sortOperator_FinType == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_ProvisionList, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_ProvisionList, "FinanceType");
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
					this.window_ProvisionList, "Product", this.finProduct.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finProduct.setValue(selectedValues);
			}
			
		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_ProvisionList, "Product");
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
					this.window_ProvisionList, "Currency", this.finCcy.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finCcy.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_ProvisionList, "Currency");
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

		if(this.oldVar_sortOperator_FinCcy == Filter.OP_IN || this.oldVar_sortOperator_FinCcy == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_ProvisionList, "FinanceMain", this.finRef.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finRef.setValue(selectedValues);
			}
			
		}else{
			Object dataObject  = ExtendedSearchListBox.show(this.window_ProvisionList,"FinanceMain");

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
					this.window_ProvisionList, "Branch", this.branchCode.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.branchCode.setValue(selectedValues);
			}
			
		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_ProvisionList,"Branch");
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
		this.pagingProvisionList.setDetailed(true);
		getPagedListWrapper().init(this.searchObj, this.listBoxProvision, this.pagingProvisionList);
		this.listBoxProvision.setItemRenderer(new ProvisionListModelItemRenderer());
		
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
		
		this.listBoxProvision.getItems().clear();
		this.pagingProvisionList.setTotalSize(0);
		this.pagingProvisionList.setActivePage(0);
		
		logger.debug("Leaving " + event.toString());
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

	/**
	 * This method Fetch the records from financemain table by adding filters 
	 * @return
	 */
	public void doSearch(){
		logger.debug("Entering");
		
		this.searchObj = new JdbcSearchObject<Provision>(Provision.class, this.listRows);
		this.searchObj.addSort("finReference", false);
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinProvisions_View");
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FinProvisions_AView");
		}
		setSearchObj(this.searchObj);
		
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
					this.searchObj.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","),Filter.OP_IN));
				}else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","),Filter.OP_NOT_IN));
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
					this.searchObj.addFilter(new Filter("finBranch", this.branchCode.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("finBranch", this.branchCode.getValue().trim().split(","),Filter.OP_NOT_IN));
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
					this.searchObj.addFilter(new Filter("FinReference", this.finRef.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinReference", this.finRef.getValue().trim().split(","),Filter.OP_NOT_IN));
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
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),Filter.OP_NOT_IN));
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
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", this.finProduct.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("LovDescProductCodeName", this.finProduct.getValue().trim().split(","),Filter.OP_NOT_IN));
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
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","),Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","),Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim(), searchOpId));
				}
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}
	public ProvisionService getProvisionService() {
		return this.provisionService;
	}

	public JdbcSearchObject<Provision> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Provision> searchObj) {
		this.searchObj = searchObj;
	}
}