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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
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

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.financemanagement.provision.model.ProvisionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * This is the controller class for the /WEB-INF/pages/Provision/Provision/ProvisionList.zul
 * file.
 */
public class ProvisionListCtrl extends GFCBaseListCtrl<Provision> {
	private static final long serialVersionUID = 4481377123949925578L;
	private static final Logger logger = Logger.getLogger(ProvisionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
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
	protected Listheader listheader_CalcProvisionDue; 	// autowired
	protected Listheader listheader_ProvisionedDue; 	// autowired
	protected Listheader listheader_LastFullyPaidDate; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType; 		// autowired

	// Filtering Fields

	protected Textbox 		finReference; 						// autowired
	protected Listbox 		sortOperator_finReference; 			// autowired
	protected Decimalbox 	custID; 							// autowired
	protected Listbox 		sortOperator_custID; 				// autowired
	protected Datebox 		provisionCalDate; 					// autowired
	protected Listbox 		sortOperator_provisionCalDate; 		// autowired
	protected Decimalbox 	provisionedAmt; 					// autowired
	protected Listbox 		sortOperator_provisionedAmt; 		// autowired
	protected Checkbox 		useNFProv; 							// autowired
	protected Listbox 		sortOperator_useNFProv; 			// autowired
	protected Datebox 		dueFromDate; 					    // autowired
	protected Listbox       sortOperator_dueFromDate;           // autowired
	protected Datebox 		lastFullyPaidDate;                  // autowired
	protected Listbox       sortOperator_lastFullyPaidDate;     // autowired
	private Grid 			searchGrid;							// autowired

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

	protected Textbox moduleName;
	private String module = "";
	private String menuItemRightName = null;
	int listRows;
	private transient ProvisionService provisionService;
	private transient FinanceDetailService financeDetailService;
	private CustomerDetailsService customerDetailsService; 


	private String moduleDefiner = "";
	private String buildedWhereCondition = "";

	/**
	 * default constructor.<br>
	 */
	public ProvisionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		Map<?, ?> args = Executions.getCurrent().getArg();
		
		if (args != null) {
			//Getting Menu Item Right Name
			menuItemRightName = (String) args.get("rightName");
		}
	}

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
		

		if("PROV".equals(moduleName.getValue())){
			this.module="Provision";
			moduleDefiner = FinanceConstants.FINSER_EVENT_PROVISION;
		}else if("PROVENQ".equals(moduleName.getValue())){
			this.module="ProvisionEnquiry";
		}

		// DropDown ListBox

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_provisionCalDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_provisionCalDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_provisionedAmt.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_provisionedAmt.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_useNFProv.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_useNFProv.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_dueFromDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_dueFromDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_lastFullyPaidDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_lastFullyPaidDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_ProvisionList.setHeight(getBorderLayoutHeight());
		this.listBoxProvision.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

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

		this.listheader_CalcProvisionDue.setSortAscending(new FieldComparator("provisionAmtCal", true));
		this.listheader_CalcProvisionDue.setSortDescending(new FieldComparator("provisionAmtCal", false));
		
		this.listheader_ProvisionedDue.setSortAscending(new FieldComparator("provisionedAmt", true));
		this.listheader_ProvisionedDue.setSortDescending(new FieldComparator("provisionedAmt", false));

		this.listheader_LastFullyPaidDate.setSortAscending(new FieldComparator("lastFullyPaidDate", true));
		this.listheader_LastFullyPaidDate.setSortDescending(new FieldComparator("lastFullyPaidDate", false));
		
		this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		
		this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
		this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));

		// search Object
		doSearch();
		// set the itemRenderer
		this.listBoxProvision.setItemRenderer(new ProvisionListModelItemRenderer());
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("ProvisionList");

		if("Provision".equals(this.module)){
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
			if("Provision".equals(this.module)){
				isEnquiry = false;
			}
			String userRole = aProvision.getNextRoleCode();
			if (StringUtils.isBlank(aProvision.getRecordType()) && !isEnquiry) {

				try {
					doLoadWorkflow(aProvision.getFinType(), moduleDefiner);
				} catch (Exception e) {
					MessageUtil.showError(e);
					return;
				}
				if (StringUtils.isBlank(userRole)) {
					userRole = getFirstTaskRole();
				}

				aProvision.setWorkflowId(getWorkFlowId());
			}
			final Provision provision = getProvisionService().getProvisionById(aProvision.getId(),isEnquiry);
			
			if(provision==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aProvision.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{

				//Fetch Total Finance Details Object
				FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(provision.getFinReference(), "_View", false);
				financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(false);
				financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
						financeDetail.getFinScheduleData().getFinanceMain().getCustID(), true, "_View"));
				financeDetail.setDocumentDetailsList(getFinanceDetailService().getFinDocByFinRef(provision.getFinReference(), moduleDefiner, "_View"));
				financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail , userRole, "DDE",
						AccountEventConstants.ACCEVENT_PROVSN, moduleDefiner, false);
				if(financeDetail != null) {
					provision.setFinanceDetail(financeDetail);
				}

				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ provision.getFinReference()+
							"' AND version=" + provision.getVersion()+" ";

					boolean userAcces =  validateUserAccess(provision.getWorkflowId(),
							getUserWorkspace().getLoggedInUser().getUserId(), "Provision", 
							whereCond, provision.getTaskId(), provision.getNextTaskId());
					if (userAcces){
						showDetailView(provision);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
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

		if (aProvision.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aProvision.setWorkflowId(getWorkFlowId());
		}
		
		Map<String, Object> map = getDefaultArguments();
		map.put("provision", aProvision);
	
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the ProvisionListbox from the
		 * dialog when we do a delete, edit or insert a Provision.
		 */
		map.put("provisionListCtrl", this);
		map.put("menuItemRightName", menuItemRightName);
		map.put("moduleDefiner", moduleDefiner);
		map.put("eventCode", AccountEventConstants.ACCEVENT_PROVSN);


		// call the zul-file with the parameters packed in a map
		try {
			if("Provision".equals(this.module)){
				if(aProvision.isNew()){
					Executions.createComponents("/WEB-INF/pages/FinanceManagement/SelectFinance/SelectFinReferenceDialog.zul",null,map);	
				}else{
					Executions.createComponents("/WEB-INF/pages/FinanceManagement/Provision/ProvisionDialog.zul",
							null,map);	
				}
				
			}else{
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/ProvisionMovement/ProvisionMovementEnquiryDialog.zul",
						null,map);
			}
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
		MessageUtil.showHelpWindow(event, window_ProvisionList);
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
		this.custID.setText("");
		this.sortOperator_dueFromDate.setSelectedIndex(0);
		this.dueFromDate.setValue(null);
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue(null);
		this.sortOperator_lastFullyPaidDate.setSelectedIndex(0);
		this.lastFullyPaidDate.setValue(null);
		this.sortOperator_provisionCalDate.setSelectedIndex(0);
		this.provisionCalDate.setValue(null);
		this.sortOperator_provisionedAmt.setSelectedIndex(0);
		this.provisionedAmt.setText("");
		this.sortOperator_useNFProv.setSelectedIndex(0);
		this.useNFProv.setChecked(false);
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Provision dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProvisionList_ProvisionSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the provision print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_ProvisionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Provision", getSearchObj(),this.pagingProvisionList.getTotalSize()+1);
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

	// Helpers

	/**
	 * This method Fetch the records from financemain table by adding filters 
	 * @return
	 */
	public void doSearch(){
		logger.debug("Entering");

		this.searchObj = new JdbcSearchObject<Provision>(Provision.class, this.listRows);
		this.searchObj.addSort("finReference", false);
		
		if(StringUtils.isEmpty(this.moduleDefiner)){
			this.searchObj.addTabelName("FinProvisions_AView");
		}else{
			this.searchObj.addWhereClause(getUsrFinAuthenticationQry(false));
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),getUserWorkspace().isAllowed("button_ProvisionList_NewProvision"));
			this.searchObj.addTabelName("FinProvisions_View");
		}
		String rolecodeList = "";
		buildedWhereCondition = "";
		if (getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()) {
			for (String role : getUserWorkspace().getUserRoles()) {
				rolecodeList = rolecodeList.concat(role).concat("','");
			}

			if(StringUtils.isNotEmpty(rolecodeList)){
				rolecodeList = rolecodeList.substring(0,rolecodeList.length()-2);
				rolecodeList = "'".concat(rolecodeList);
			}
		}

		if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
			buildedWhereCondition = " (NextRoleCode IS NULL ";
		} else {
			buildedWhereCondition = " (NextRoleCode = '' ";
		}
		buildedWhereCondition = buildedWhereCondition.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
		buildedWhereCondition = buildedWhereCondition.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
		buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");
		buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
		buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat("))) OR NextRoleCode IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat(") ");

		if(StringUtils.isNotEmpty(buildedWhereCondition)){
			this.searchObj.addWhereClause(buildedWhereCondition); 
		}
		//Finance Reference 
		if (StringUtils.isNotBlank(this.finReference.getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finReference.getSelectedItem(), this.finReference.getValue() , "finReference");
		}
		// Customer ID 
		if (this.custID.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custID.getSelectedItem(), this.custID.getValue() , "custID");
		}

		// Provision Calculated Date
		if (this.provisionCalDate.getValue()!=null) {

			searchObj.addFilter(new Filter("provisionCalDate",DateUtility.formatUtilDate(
					this.provisionCalDate.getValue(),PennantConstants.DBDateFormat), Filter.OP_EQUAL));
		}

		// Provisioned Amount
		if (this.provisionedAmt.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_provisionedAmt.getSelectedItem(), this.provisionedAmt.getValue() , "provisionedAmt");
		}

		//  Use NV Provision
		if (this.sortOperator_useNFProv.getSelectedItem() != null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_useNFProv.getSelectedItem(), this.useNFProv.isChecked() ? 1 : 0, "useNFProv");
		}

		// Due From Date
		if (this.dueFromDate.getValue()!=null) {
			searchObj.addFilter(new Filter("dueFromDate",DateUtility.formatUtilDate(
					this.dueFromDate.getValue(),PennantConstants.DBDateFormat), Filter.OP_EQUAL));
		}

		// Last Fully Payed Date
		if (this.lastFullyPaidDate.getValue()!=null) {
			searchObj.addFilter(new Filter("dueFromDate",DateUtility.formatUtilDate(
					this.dueFromDate.getValue(),PennantConstants.DBDateFormat), Filter.OP_EQUAL));
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxProvision,this.pagingProvisionList);
		logger.debug("Leaving");
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}