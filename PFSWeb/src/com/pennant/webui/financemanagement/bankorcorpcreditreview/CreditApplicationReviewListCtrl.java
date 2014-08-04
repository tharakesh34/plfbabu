package com.pennant.webui.financemanagement.bankorcorpcreditreview;

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
 * FileName    		:  CreditReviewDetailsListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.financemanagement.bankorcorpcreditreview.model.CreditApplicationReviewListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/FinCreditReviewDetails/CreditReviewDetailsList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CreditApplicationReviewListCtrl extends GFCBaseListCtrl<FinCreditReviewDetails> implements Serializable {

	private static final long serialVersionUID	= 4322539879503951300L;
	private final static Logger logger = Logger.getLogger(CreditApplicationReviewListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	    window_CreditApplicationReviewList;	     // autowired
	protected Borderlayout	borderLayout_CreditApplicationReviewList;	 // autowired
	protected Paging	    pagingCreditApplicationReviewList;	     // autowired
	protected Listbox	    listBoxCreditApplicationReview;	         // autowired
	private List<ValueLabel> categoryesList = PennantStaticListUtil.getCategoryType();
	private List<ValueLabel> auditYearsList;

	//search
	protected Textbox custCIF; 							// autoWired
	protected Listbox sortOperator_custCIF; 				// autoWired
	protected Combobox custCreditReviewCode; 				// autoWired
	protected Listbox sortOperator_custCreditReviewCode; 	// autoWired
	protected Combobox custAuditYear; 				// autoWired
	protected Listbox sortOperator_custAuditYear; 	// autoWired
	protected Textbox custBankName; 					// autoWired
	protected Listbox sortOperator_custBankName; 		// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType;						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired
    protected Textbox moduleName;
	protected Label label_CreditApplicationReviewSearch_RecordStatus; 	// autoWired
	protected Label label_CreditApplicationReviewSearch_RecordType; 	// autoWired

	protected Grid	                       searchGrid;	                                                  // autowired
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;
	private transient boolean 			   approvedList=false;

	// List headers
	protected Listheader	listheader_DetailId;	         // autowired
	protected Listheader	listheader_CreditCustCIF;	         // autowired
	protected Listheader	listheader_CreditCustID;	         // autowired
	protected Listheader	listheader_CreditRevCode;	     // autowired
	protected Listheader	listheader_AuditedYear;	      // autowired
	protected Listheader	listheader_AuditPeriod;	      // autowired
	protected Listheader	listheader_BankName;	 // autowired
	protected Listheader	listheader_RecordStatus;	     // autowired
	protected Listheader	listheader_RecordType;          // autowired
	protected Listheader	listheader_CreditMaxAudYear;    // autowired
	protected Listheader	listheader_CreditMinAudYear;    // autowired
	protected Listheader	listheader_CreditCustShrtName;    // autowired

	// checkRights
	protected Button	    btnHelp;	                                        // autowired
	protected Button	    button_CreditApplicationReviewList_NewCreditApplicationReview;	        // autowired
	protected Button	    button_CreditAppReviewList_CreditAppReviewSearch;	// autowired
	protected Button	    button_CreditApplicationReviewList_PrintList;	                // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinCreditReviewDetails>	searchObjCreditReviewDetails;
	private transient CreditApplicationReviewService	  creditApplicationReviewService;

	private transient WorkFlowDetails	      workFlowDetails	= null;
	private boolean isMaintinence = false;

	int dateAppCurrentYear = DateUtility.getYear((Date)SystemParameterDetails.getSystemParameterValue("APP_DATE"));
	int dateAppPrevYear = dateAppCurrentYear-1;
	/**
	 * default constructor.<br>
	 */
	public CreditApplicationReviewListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinCreditReviewDetails object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CreditApplicationReviewList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinCreditReviewDetails");
		boolean wfAvailable = true;
		if(this.moduleName.getValue().equals("CreditReviewMaintinence")){
			isMaintinence = true;
		} else {
			isMaintinence = false;
		}
		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCreditReviewDetails");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCreditReviewCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCreditReviewCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custBankName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custBankName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custAuditYear.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_custAuditYear.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CreditApplicationReviewSearch_RecordStatus.setVisible(false);
			this.label_CreditApplicationReviewSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		fillComboBox(custCreditReviewCode, "", categoryesList, ",I,");
		fillComboBox(custAuditYear, "", getAuditYearsList(dateAppPrevYear, dateAppCurrentYear) ,"");
		doCheckRights();

		this.borderLayout_CreditApplicationReviewList.setHeight(getBorderLayoutHeight());
		this.listBoxCreditApplicationReview.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCreditApplicationReviewList.setPageSize(getListRows());
		this.pagingCreditApplicationReviewList.setDetailed(true);

		this.listheader_DetailId.setSortAscending(new FieldComparator("detailId", true));
		this.listheader_DetailId.setSortDescending(new FieldComparator("detailId", false));

		this.listheader_CreditCustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CreditCustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		
		this.listheader_CreditMaxAudYear.setSortAscending(new FieldComparator("lovDescMaxAuditYear", true));
		this.listheader_CreditMaxAudYear.setSortDescending(new FieldComparator("lovDescMaxAuditYear", false));
		
		this.listheader_CreditMinAudYear.setSortAscending(new FieldComparator("lovDescMinAuditYear", true));
		this.listheader_CreditMinAudYear.setSortDescending(new FieldComparator("lovDescMinAuditYear", false));
		
		this.listheader_CreditCustShrtName.setSortAscending(new FieldComparator("lovDescCustShrtName", true));
		this.listheader_CreditCustShrtName.setSortDescending(new FieldComparator("lovDescCustShrtName", false));
		
		this.listheader_CreditCustID.setSortAscending(new FieldComparator("customerId", true));
		this.listheader_CreditCustID.setSortDescending(new FieldComparator("customerId", false));
		if(isMaintinence) {
			this.listheader_CreditCustID.setVisible(false);
			this.listheader_CreditMaxAudYear.setVisible(true);
			this.listheader_CreditMinAudYear.setVisible(true);
			this.listheader_CreditCustShrtName.setVisible(true);
		}

		this.listheader_CreditRevCode.setSortAscending(new FieldComparator("creditRevCode", true));
		this.listheader_CreditRevCode.setSortDescending(new FieldComparator("creditRevCode", false));

		this.listheader_BankName.setSortAscending(new FieldComparator("bankName", true));
		this.listheader_BankName.setSortDescending(new FieldComparator("bankName", false));

		this.listheader_AuditedYear.setSortAscending(new FieldComparator("auditYear", true));
		this.listheader_AuditedYear.setSortDescending(new FieldComparator("auditYear", false));

		this.listheader_AuditPeriod.setSortAscending(new FieldComparator("auditPeriod", true));
		this.listheader_AuditPeriod.setSortDescending(new FieldComparator("auditPeriod", false));
		// set the itemRenderer
		this.listBoxCreditApplicationReview.setItemRenderer(new CreditApplicationReviewListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));

			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
			this.searchObjCreditReviewDetails = new JdbcSearchObject<FinCreditReviewDetails>(FinCreditReviewDetails.class, getListRows());
			this.searchObjCreditReviewDetails.addSort("DetailId", false);
			this.searchObjCreditReviewDetails.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());

		// WorkFlow
			if (isWorkFlowEnabled()) {
				this.searchObjCreditReviewDetails.addTabelName("FinCreditReviewDetails_View");
				if (isFirstTask()) {
					button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(true);
				} else {
					button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(false);
				}
			} else {
				this.searchObjCreditReviewDetails.addTabelName("FinCreditReviewDetails_AView");
			}

		setSearchObj(this.searchObjCreditReviewDetails);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(false);
			this.button_CreditAppReviewList_CreditAppReviewSearch.setVisible(false);
			this.button_CreditApplicationReviewList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CreditApplicationReviewList", getRole());
		if(!isMaintinence) {
			this.button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(getUserWorkspace().
					isAllowed("button_CreditApplicationReviewList_NewCreditApplicationReview"));
		}
		this.button_CreditAppReviewList_CreditAppReviewSearch.setVisible(getUserWorkspace().
				isAllowed("button_CreditApplicationReviewList_CreditAppReviewSearch"));
		this.button_CreditApplicationReviewList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_CreditApplicationReviewList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.accountingset.model.CreditApplicationReviewListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreditApplicationReviewItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FinCreditReviewDetails object
		final Listitem item = this.listBoxCreditApplicationReview.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) item.getAttribute("data");
			final FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewService().getCreditReviewDetailsById(
					aCreditReviewDetails.getDetailId());

			if (creditReviewDetails == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(aCreditReviewDetails.getDetailId());
				errParm[0] = PennantJavaUtil.getLabel("label_CreditReviewId") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005", errParm, valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				List<FinCreditReviewSummary> listOfFinCreditReviewSummary = getCreditApplicationReviewService().getListCreditReviewSummaryById(aCreditReviewDetails.getDetailId(), "_View", false);
				creditReviewDetails.setCreditReviewSummaryEntries(listOfFinCreditReviewSummary);
				if (isWorkFlowEnabled()) {
					String whereCond = " AND Detailid=" + creditReviewDetails.getDetailId() + 
					" AND version=" + creditReviewDetails.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), 
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinCreditReviewDetails", 
							whereCond, creditReviewDetails.getTaskId(), creditReviewDetails.getNextTaskId());
					if (userAcces) {
						showDetailView(creditReviewDetails);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(creditReviewDetails);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the FinCreditReviewDetails dialog with a new empty entry. <br>
	 */
	
	public void onClick$button_CreditApplicationReviewList_NewCreditApplicationReview(Event event) throws Exception {
		logger.debug("Entering" + event.toString());


		logger.debug("Entering" +event.toString());
		// create a new WIFFinanceMain object, We GET it from the backend.
		final FinCreditReviewDetails aCreditReviewDetails = getCreditApplicationReviewService().getNewCreditReviewDetails();
		aCreditReviewDetails.setNewRecord(true);

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("creditApplicationReviewDialogCtrl", new CreditApplicationReviewDialogCtrl());
		map.put("searchObject", this.searchObjCreditReviewDetails);
		map.put("aCreditReviewDetails", aCreditReviewDetails);
		map.put("creditApplicationReviewListCtrl", this);
		//map.put("loanType", this.loanType.getValue());
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationRevSelectCategoryType.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving"+event.toString());
		
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param FinCreditReviewDetails (aCreditReviewDetails)
	 * @throws Exception
	 */
	private void showDetailView(FinCreditReviewDetails aCreditReviewDetails) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aCreditReviewDetails.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCreditReviewDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("creditReviewDetails", aCreditReviewDetails);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listBox ListModel. This is fine for synchronizing the data in the CreditReviewDetailsListbox from the dialog when
		 * we do a delete, edit or insert a FinCreditReviewDetails.
		 */
		map.put("creditApplicationReviewListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		
		try {
			if(!isMaintinence){
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewDialog.zul", null, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewEnquiry.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CreditApplicationReviewList);
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
		refresh();
		Events.postEvent("onCreate", this.window_CreditApplicationReviewList, event);
		logger.debug("Leaving" + event.toString());
	}

	public void refresh(){
		
		logger.debug("Entering ");
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.custCIF.setValue("");
		this.sortOperator_custCreditReviewCode.setSelectedIndex(0);
		this.custCreditReviewCode.setSelectedIndex(0);
		this.sortOperator_custAuditYear.setSelectedIndex(0);
		this.custAuditYear.setSelectedIndex(0);
		this.sortOperator_custBankName.setSelectedIndex(0);
		this.custBankName.setValue("");

		this.pagingCreditApplicationReviewList.setActivePage(0);
		this.window_CreditApplicationReviewList.invalidate();
		doSearch();
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for calling the FinCreditReviewDetails dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CreditAppReviewList_CreditAppReviewSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our CreditApplicationReviewDialog ZUL-file with parameters. So we can call them with a object of the
		 * selected FinCreditReviewDetails. For handed over these parameter only a Map is accepted. So we put the FinCreditReviewDetails
		 * object in a HashMap.

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("accountingSetCtrl", this);
			map.put("searchObject", this.searchObj);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/BankOrCorpCredit/CreditApplicationReviewSearchDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}*/
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the creditReviewDetails print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CreditApplicationReviewList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("FinCreditReviewDetails", getSearchObj(),this.pagingCreditApplicationReviewList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("serial")
	public void doSearch() {
		logger.debug("Entering ");
		// ++ create the searchObject and initialize sorting ++//
		
		this.searchObjCreditReviewDetails = new JdbcSearchObject<FinCreditReviewDetails>(FinCreditReviewDetails.class, getListRows());
		
		if(isMaintinence){
			this.searchObjCreditReviewDetails.addTabelName("CreditReviewMaintinence_View");
			this.searchObjCreditReviewDetails.addFilterNotIn("RecordStatus", "'Approved'");
		}  else {
			this.searchObjCreditReviewDetails.addSort("lovDescCustCIF", false);
			this.searchObjCreditReviewDetails.addTabelName("FinCreditReviewDetails_View");
			this.searchObjCreditReviewDetails.addFilterIn("AuditYear", 
					new ArrayList<String>() {{
						add(String.valueOf(dateAppCurrentYear));
						add(String.valueOf(dateAppPrevYear));
						add(String.valueOf(dateAppPrevYear-1));
					}} ,true);
		}
		
		
		
		// Work flow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null && !isMaintinence) {
				button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(true);
			} else {
				button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObjCreditReviewDetails.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObjCreditReviewDetails.addTabelName("FinCreditReviewDetails_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObjCreditReviewDetails.addTabelName("FinCreditReviewDetails_AView");
		}

		// Cust CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObjCreditReviewDetails = getSearchFilter(searchObjCreditReviewDetails, this.sortOperator_custCIF.getSelectedItem(), this.custCIF.getValue(), "lovDescCustCIF");
		}
		// cust Credit Review Code
		if (!this.custCreditReviewCode.getSelectedItem().getValue().equals("#")) {
			searchObjCreditReviewDetails = getSearchFilter(searchObjCreditReviewDetails, this.sortOperator_custCreditReviewCode.getSelectedItem(), this.custCreditReviewCode.getSelectedItem().getValue(), "CreditRevCode");
		}

		// Cust Audit Year
		if(!this.custAuditYear.getSelectedItem().getValue().equals("#")){
			searchObjCreditReviewDetails = getSearchFilter(searchObjCreditReviewDetails, this.sortOperator_custAuditYear.getSelectedItem(), Integer.parseInt(this.custAuditYear.getSelectedItem().getValue().toString()), "AuditYear");
		}

		// Cust Bank Name
		if (!StringUtils.trimToEmpty(this.custBankName.getValue()).equals("")) {
			searchObjCreditReviewDetails = getSearchFilter(searchObjCreditReviewDetails, this.sortOperator_custBankName.getSelectedItem(), this.custBankName.getValue(), "BankName");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObjCreditReviewDetails = getSearchFilter(searchObjCreditReviewDetails, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObjCreditReviewDetails = getSearchFilter(searchObjCreditReviewDetails, this.sortOperator_recordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		  }
		
		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObjCreditReviewDetails.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObjCreditReviewDetails, this.listBoxCreditApplicationReview, this.pagingCreditApplicationReviewList);		
		logger.debug("Leaving ");
	}

	public List<ValueLabel> getAuditYearsList(int startYear, int endYear){
		auditYearsList = new ArrayList<ValueLabel>();
		for(;endYear >= startYear; endYear--){
			auditYearsList.add(new ValueLabel(String.valueOf(endYear), String.valueOf(endYear)));
		} 
		return auditYearsList;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//	


	public JdbcSearchObject<FinCreditReviewDetails> getSearchObj() {
		return this.searchObjCreditReviewDetails;
	}
	public void setSearchObj(JdbcSearchObject<FinCreditReviewDetails> searchObj) {
		this.searchObjCreditReviewDetails = searchObj;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(
			CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}	
}