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
 * FileName    		:  FinanceMainListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExt;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceEligibility;
import com.pennant.backend.service.finance.FinanceMainExtService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer;
import com.pennant.webui.finance.payorderissue.FinAdvancePaymentsCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul file.
 */
public class FinanceMainListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long				serialVersionUID	= -5901195042041627750L;
	private final static Logger				logger				= Logger.getLogger(FinanceMainListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_FinanceMainList;											// autoWired
	protected Borderlayout					borderLayout_FinanceMainList;										// autoWired
	protected Paging						pagingFinanceMainList;												// autoWired
	protected Listbox						listBoxFinanceMain;												// autoWired

	protected Textbox						finReference;														// autoWired
	protected Listbox						sortOperator_finReference;											// autoWired
	protected Textbox						finType;															// autoWired
	protected Listbox						sortOperator_finType;												// autoWired
	protected Textbox						custCIF;															// autoWired
	protected Listbox						sortOperator_custID;												// autoWired
	protected Longbox						custID;															// autoWired
	protected Textbox						fincustName;														// autoWired
	protected Listbox						sortOperator_custName;												// autoWired
	protected Textbox						finMobileNumber;													// autoWired
	protected Listbox						sortOperator_mobileNumber;											// autoWired
	protected Textbox						finEIDNumber;														// autoWired
	protected Listbox						sortOperator_eidNumber;											// autoWired
	protected Textbox						finPassPort;														// autoWired
	protected Listbox						sortOperator_passPort;												// autoWired
	protected Datebox						finDateofBirth;													// autoWired
	protected Listbox						sortOperator_finDateofBirth;										// autoWired
	protected Datebox						finRequestDate;													// autoWired
	protected Listbox						sortOperator_finRequestDate;										// autoWired
	protected Listbox						sortOperator_finPromotion;											// autoWired
	protected Textbox						finPromotion;														// autoWired
	protected Listbox						sortOperator_finRequestStage;										// autoWired
	protected Combobox						finRequestStage;													// autoWired
	protected Listbox						sortOperator_finQueuePriority;										// autoWired
	protected Combobox						finQueuePriority;													// autoWired
	protected Textbox						phoneCountryCode;
	protected Textbox						phoneAreaCode;
	protected Textbox						recordStatus;														// autoWired
	protected Listbox						recordType;														// autoWired
	protected Listbox						sortOperator_recordStatus;											// autoWired
	protected Listbox						sortOperator_recordType;											// autoWired
	protected Datebox						initiateDate;														// autoWired
	protected Listbox						sortOperator_InitiateDate;											// autoWired

	// List headers
	protected Listheader					listheader_CustomerCIF;											// autoWired
	protected Listheader					listheader_CustomerName;											// autoWired
	protected Listheader					listheader_FinReference;											// autoWired
	protected Listheader					listheader_FinType;												// autoWired
	protected Listheader					listheader_FinCcy;													// autoWired
	protected Listheader					listheader_FinAmount;												// autoWired
	protected Listheader					listheader_FinancingAmount;										// autoWired
	protected Listheader					listheader_InitiateDate;											// autoWired
	protected Listheader					listheader_Promotion;
	protected Listheader					listheader_Terms;
	protected Listheader					listheader_RequestStage;
	protected Listheader					listheader_Priority;
	protected Listheader					listheader_RecordStatus;											// autoWired
	protected Listheader					listheader_RecordType;												// autoWired

	// checkRights
	protected Button						btnHelp;															// autoWired
	protected Button						button_FinanceMainList_NewFinanceMain;								// autoWired
	protected Button						button_FinanceMainList_FinanceMainSearchDialog;					// autoWired
	protected Button						button_FinanceMainList_PrintList;									// autoWired
	protected Button						btnRefresh;														// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain>	searchObj;
	protected Row							row_AlwWorkflow;
	protected Grid							searchGrid;
	private transient FinanceDetailService	financeDetailService;

	private String							requestSource;
	private String							menuItemRightName	= null;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	private boolean							fromEligibleScreen	= false;
	private FinanceEligibility				finEligibility		= null;
	protected int							oldVar_sortOperator_finType;										// autoWired

	private DedupParmService				dedupParmService;
	private FinanceMainExtService			financeMainExtService;

	private String							CREATE_CIF			= "CREATECIF";
	private String							CREATE_ACCOUNT		= "CREATACCOUNT";

	/**
	 * default constructor.<br>
	 */
	public FinanceMainListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "FinanceMain";
	}

	public void onCreate$window_FinanceMainList(Event event) throws Exception {
		logger.debug("Entering");

		// Getting Menu Item Right Name
		String menuItemName = getCurrentTab();
		if (menuItemName != null) {
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");

			if (getUserWorkspace().getHasMenuRights().containsKey(menuItemName)) {
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}
		}

		if (arguments.containsKey("requestSource")) {
			requestSource = (String) arguments.get("requestSource");
		}

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getAlphaNumOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getMultiStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getAlphaNumOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custName.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_custName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_mobileNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_mobileNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eidNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_eidNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_passPort.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_passPort.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finDateofBirth.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getNumericOperators()));
		this.sortOperator_finDateofBirth.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finRequestDate.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getNumericOperators()));
		this.sortOperator_finRequestDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finPromotion.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getMultiStringOperators()));
		this.sortOperator_finPromotion.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_InitiateDate.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getNumericOperators()));
		this.sortOperator_InitiateDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finRequestStage.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getEqualOrNotOperators()));
		Filter[] filters = new Filter[1];
		filters[0] = Filter.in("RoleCd", getUserWorkspace().getUserRoles());
		fillComboBox(this.finRequestStage, "", PennantAppUtil.getSecRolesList(filters), "");
		this.sortOperator_finRequestStage.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finQueuePriority.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getEqualOrNotOperators()));
		fillComboBox(this.finQueuePriority, "", PennantStaticListUtil.getQueuePriority(), "");
		this.sortOperator_finQueuePriority.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators()
				.getStringOperators()));
		this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.recordType = setRecordType(this.recordType);
		this.sortOperator_recordType.setSelectedIndex(0);
		this.recordType.setSelectedIndex(0);

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_FinanceMainList.setHeight(borderLayoutHeight - 2 + "px");
		this.listBoxFinanceMain.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingFinanceMainList.setPageSize(getListRows());
		this.pagingFinanceMainList.setDetailed(true);

		this.listheader_CustomerCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustomerCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));

		this.listheader_CustomerName.setSortDescending(new FieldComparator("LovDescCustShrtName", false));
		this.listheader_CustomerName.setSortAscending(new FieldComparator("LovDescCustShrtName", true));

		this.listheader_FinReference.setSortAscending(new FieldComparator("FinReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("FinReference", false));

		this.listheader_FinType.setSortAscending(new FieldComparator("FinType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("FinType", false));

		this.listheader_Promotion.setSortAscending(new FieldComparator("LovDescFinProduct", true));
		this.listheader_Promotion.setSortDescending(new FieldComparator("LovDescFinProduct", false));

		this.listheader_FinCcy.setSortAscending(new FieldComparator("FinCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("FinCcy", false));

		this.listheader_Terms.setSortAscending(new FieldComparator("NumberOfTerms", true));
		this.listheader_Terms.setSortDescending(new FieldComparator("NumberOfTerms", false));

		this.listheader_FinAmount.setSortAscending(new FieldComparator("FinAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("FinAmount", false));

		this.listheader_FinancingAmount.setSortAscending(new FieldComparator("FinAmount", true));
		this.listheader_FinancingAmount.setSortDescending(new FieldComparator("FinAmount", false));

		this.listheader_InitiateDate.setSortAscending(new FieldComparator("InitiateDate", true));
		this.listheader_InitiateDate.setSortDescending(new FieldComparator("InitiateDate", false));

		this.listheader_RequestStage.setSortAscending(new FieldComparator("LovDescRequestStage", true));
		this.listheader_RequestStage.setSortDescending(new FieldComparator("LovDescRequestStage", false));

		this.listheader_Priority.setSortAscending(new FieldComparator("Priority", true));
		this.listheader_Priority.setSortDescending(new FieldComparator("Priority", false));

		this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));

		this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
		this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));

		doSetFieldProperties();
		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class, getListRows());
		this.searchObj.addSort("FinReference", false);

		// Field Declarations for Fetching List Data
		this.searchObj.addField("FinReference");
		this.searchObj.addField("FinType");
		this.searchObj.addField("LovDescFinTypeName");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("FinAmount");
		this.searchObj.addField("DownPayment");
		this.searchObj.addField("FeeChargeAmt");
		this.searchObj.addField("InsuranceAmt");
		this.searchObj.addField("LovDescCustCIF");
		this.searchObj.addField("LovDescCustShrtName");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");
		this.searchObj.addField("NumberOfTerms");
		this.searchObj.addField("LovDescFinProduct");
		this.searchObj.addField("NextRoleCode");
		this.searchObj.addField("LovDescRequestStage");
		this.searchObj.addField("Priority");
		this.searchObj.addField("WorkflowId");
		this.searchObj.addField("TaskId");
		this.searchObj.addField("NextTaskId");
		this.searchObj.addField("NextUserId");
		this.searchObj.addField("InitiateDate");
		this.searchObj.addField("RcdMaintainSts");

		// FIXME: DELETE BELOW CODE AFTER TESTING
		/*
		 * String screenEvent = ""; if (FinanceConstants.FINSER_EVENT_PREAPPROVAL .equals(this.requestSource)) {
		 * screenEvent = FinanceConstants.FINSER_EVENT_PREAPPROVAL; } else { screenEvent =
		 * FinanceConstants.FINSER_EVENT_ORG; }
		 * 
		 * boolean accessToCreateNewFin = getFinanceDetailService().checkFirstTaskOwnerAccess(
		 * getUserWorkspace().getUserRoleSet(), screenEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		 * 
		 * 
		 * if (accessToCreateNewFin) { button_FinanceMainList_NewFinanceMain.setVisible(true); } else {
		 * button_FinanceMainList_NewFinanceMain.setVisible(false); }
		 */

		this.searchObj.addTabelName("FinanceMain_LView");
		setSearchObj(this.searchObj);
		doSearch();

		// set the itemRenderer
		this.listBoxFinanceMain.setItemRenderer(new FinanceMainListModelItemRenderer());

		// TODO: Clarification and testing only
		if (arguments.containsKey("fromEligibleScreen")) {
			fromEligibleScreen = true;
			finEligibility = (FinanceEligibility) arguments.get("finEligibility");
			Events.postEvent("onClick$button_FinanceMainList_NewFinanceMain", window_FinanceMainList, arguments);
		} else {
			fromEligibleScreen = false;
			finEligibility = null;
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinanceMainList");

		this.button_FinanceMainList_NewFinanceMain.setVisible(getUserWorkspace().isAllowed(
				"button_FinanceMainList_NewFinanceMain"));
		this.button_FinanceMainList_PrintList.setVisible(getUserWorkspace().isAllowed(
				"button_FinanceMainList_PrintList"));

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.finance.financemain.model. FinanceMainListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the selected FinanceMain object
		final Listitem item = this.listBoxFinanceMain.getSelectedItem();

		if (item == null) {
			logger.debug("Leaving " + event.toString());
			return;
		}

		// CAST AND STORE THE SELECTED OBJECT
		final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

		String screenEvent = "";
		if (StringUtils.equals(this.requestSource, FinanceConstants.FINSER_EVENT_PREAPPROVAL)) {
			screenEvent = FinanceConstants.FINSER_EVENT_PREAPPROVAL;
		} else {
			screenEvent = FinanceConstants.FINSER_EVENT_ORG;
		}

		doLoadWorkFlow(aFinanceMain.isWorkflow(), aFinanceMain.getWorkflowId(), aFinanceMain.getNextTaskId());
		final FinanceDetail financeDetail = getFinanceDetailService().getOriginationFinance(aFinanceMain.getId(),
				aFinanceMain.getNextRoleCode(), screenEvent, getRole());

		if (financeDetail == null) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = aFinanceMain.getId();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
			ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
					errParm, valueParm), getUserWorkspace().getUserLanguage());
			MessageUtil.showErrorMessage(errorDetails.getError());
			logger.debug("Leaving " + event.toString());
			return;
		}
		
		//Check QDP Case
		boolean allowProcess = FinAdvancePaymentsCtrl.checkQDPProceeed(financeDetail);
		if (!allowProcess) {
			MessageUtil.showMessage(Labels.getLabel("label_Finance_QuickDisb_Queue"));
			return;
		}
		

		if (aFinanceMain.getWorkflowId() != 0
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(aFinanceMain.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_SAVED.equals(aFinanceMain.getRecordStatus())) {

			boolean userAcces = true;
			if (!inReassignmentQueue(aFinanceMain.getFinReference())) {
				String[] nextTasks = aFinanceMain.getNextTaskId().split(";");

				if (nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {
						String baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
						if (!"".equals(baseRole)
								&& getUserWorkspace().getUserRoles().contains(baseRole)
								&& aFinanceMain.getNextUserId() != null
								&& aFinanceMain.getNextUserId().contains(
										String.valueOf(getUserWorkspace().getLoggedInUser().getLoginUsrID()))) {
							userAcces = true;
							break;
						}
					}
				}

				if (userAcces) {
					if (tATProcess(aFinanceMain, financeDetail)) {
						validateCustExistance(financeDetail);
					}
				} else {
					MessageUtil.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					return;
				}
			} else {
				MessageUtil.showErrorMessage(Labels.getLabel("RECORD_IN_REASSIGNMENT_QUEUE"));
				return;
			}
		} else {
			if (tATProcess(aFinanceMain, financeDetail)) {
				validateCustExistance(financeDetail);
			}
		}

		logger.debug("Leaving " + event.toString());
	}


	/**
	 * Method for Validating Customer is Exists in Core Banking Account created against Customer in Core banking System
	 * if required
	 * 
	 * @param financeDetail
	 * @throws Exception
	 */
	private void validateCustExistance(FinanceDetail financeDetail) throws Exception {
		logger.debug("Entering");

		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		if (checkUserAccess(financeDetail)) {
			if (ImplementationConstants.VALIDATE_CORE_CUST_UPDATE) {

				if (StringUtils.isBlank(customer.getCustCoreBank())) {
					// check retail or corporate customer?
					if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)
							|| StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)
							|| customer.isSalariedCustomer()) {
						MessageUtil.showErrorMessage(Labels
								.getLabel("label_FinanceMainDialog_Mandatory_Prospect.value"));
						return;
					}

					// CIF not Exists
					doReserveCIF(financeDetail, CREATE_CIF);
				} else {

					// CIF Exists & Validate Is Customer Account created or not
					String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
					boolean processFlag = true;
					FinanceMainExt financeMainExt = getFinanceMainExtService().getNstlAccNumber(finReference,
							processFlag);
					if (financeMainExt != null) {
						if (StringUtils.isBlank(financeMainExt.getNstlAccNum())) {
							doReserveCIF(financeDetail, CREATE_ACCOUNT);
						}
					}
				}
			}

			// Redirect to Dialog Window
			showDetailView(financeDetail);
		} else {

			showDetailView(financeDetail);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing CIF Reserve through New Dialog Process
	 * 
	 * @param financeDetail
	 * @param createFlag
	 */
	private void doReserveCIF(FinanceDetail financeDetail, String createFlag) {
		logger.debug("Entering");

		Map<String, Object> map = getDefaultArguments();
		map.put("financeDetail", financeDetail);
		map.put("financeMainListCtrl", this);
		map.put("CreateFlag", createFlag);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ReserveCIF.zul", this.window_FinanceMainList,
				map);

		logger.debug("Leaving");
	}

	/**
	 * Method for checking User Accessibility of the Record against the Role
	 * 
	 * @param financeDetail
	 * @return
	 */
	private boolean checkUserAccess(FinanceDetail financeDetail) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<SecurityUser> searchObject = new JdbcSearchObject<SecurityUser>(SecurityUser.class);
		searchObject.addTabelName("UserRights_View");
		searchObject.addSort("RightID", false);
		searchObject.addField("RightID");

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("RoleCd", financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode(),
				Filter.OP_EQUAL);
		filters[1] = new Filter("RightName", "create_FinanceMainList_CustomerCIF", Filter.OP_EQUAL);
		filters[2] = new Filter("UsrID", getUserWorkspace().getLoggedInUser().getLoginUsrID(), Filter.OP_EQUAL);
		searchObject.addFilterOr(filters);

		searchObject.addFilterAnd(filters);

		List<SecurityUser> rightList = pagedListService.getBySearchObject(searchObject);
		if (rightList != null && !rightList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for Checking User action maintained based On TAT defined at Process Editor If User agreed , Action will be
	 * logged for future purpose.
	 * 
	 * @param aFinanceMain
	 * @param financeDetail
	 * @return
	 */
	private boolean tATProcess(FinanceMain aFinanceMain, FinanceDetail financeDetail) {
		logger.debug("Entering");

		FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
		financeRefDetail.setMandInputInStage(aFinanceMain.getNextRoleCode() + ",");
		financeRefDetail.setFinType(aFinanceMain.getFinType());
		List<FinanceReferenceDetail> queryCodeList = getDedupParmService().getQueryCodeList(financeRefDetail,
				"_TATView");

		if (queryCodeList != null && !queryCodeList.isEmpty()) {

			TATDetail tatDetail = getFinanceDetailService().getTATDetail(aFinanceMain.getFinReference(),
					aFinanceMain.getNextRoleCode());

			if (tatDetail == null || (tatDetail != null && tatDetail.gettATStartTime() == null)) {
				final String msg = Labels.getLabel("label_TATProcess_UserAction");

				final String title = Labels.getLabel("label_TatProcess_Title");
				MultiLineMessageBox.doSetTemplate();

				int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
						Messagebox.QUESTION, true);

				if (conf == MultiLineMessageBox.YES) {
					if (tatDetail == null) {
						tatDetail = new TATDetail();
						tatDetail.setModule(FinanceConstants.FINSER_EVENT_ORG);
						tatDetail.setReference(aFinanceMain.getFinReference());
						tatDetail.setRoleCode(aFinanceMain.getNextRoleCode());
						tatDetail.setFinType(aFinanceMain.getFinType());
						tatDetail.settATStartTime(new Timestamp(System.currentTimeMillis()));
						getFinanceDetailService().saveTATDetail(tatDetail);
					} else {
						tatDetail.settATStartTime(new Timestamp(System.currentTimeMillis()));
						getFinanceDetailService().updateTATDetail(tatDetail);
					}
					return true;
				} else {
					return false;
				}
			}
		}
		logger.debug("leaving");
		return true;
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceMainList_NewFinanceMain(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceMain. For handed over these parameter only a Map is accepted. So we put the FinanceMain object in a
		 * HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("financeMainListCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("requestSource", this.requestSource);
		map.put("role", getUserWorkspace().getUserRoles());
		map.put("menuItemRightName", menuItemRightName);
		if (fromEligibleScreen) {
			map.put("fromEligibleScreen", true);
			map.put("finEligibility", finEligibility);
		}

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectFinanceTypeDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e.toString());
		}

		fromEligibleScreen = false;
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain
	 *            (aFinanceMain)
	 * @throws Exception
	 */
	protected void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> map = getDefaultArguments();
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * list box List model. This is fine for synchronizing the data in the FinanceMainListbox from the dialog when
		 * we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeMainListCtrl", this);
		map.put("menuItemRightName", menuItemRightName);

		// call the ZUL-file with the parameters packed in a map
		try {
			String productType = aFinanceMain.getProductCategory();
			productType = (productType.substring(0, 1)).toUpperCase() + (productType.substring(1)).toLowerCase();

			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			if ("QDE".equals(StringUtils.trimToEmpty(this.requestSource))) {
				fileLocaation.append("QDEFinanceMainDialog.zul");
			} else if (FinanceConstants.FINSER_EVENT_PREAPPROVAL.equals(this.requestSource)) {
				fileLocaation.append("FinancePreApprovalDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_IJARAH)) {
				fileLocaation.append("IjarahFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_FWIJARAH)) {
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

			Executions.createComponents(fileLocaation.toString(), this.window_FinanceMainList, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e.toString());
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
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_FinanceMainList);
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		this.sortOperator_custID.setSelectedIndex(0);
		this.custCIF.setValue("");
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_custName.setSelectedIndex(0);
		this.fincustName.setValue("");
		this.sortOperator_mobileNumber.setSelectedIndex(0);
		this.finMobileNumber.setValue("");
		this.sortOperator_eidNumber.setSelectedIndex(0);
		this.finEIDNumber.setValue("");
		this.sortOperator_passPort.setSelectedIndex(0);
		this.finPassPort.setValue("");
		this.sortOperator_finDateofBirth.setSelectedIndex(0);
		this.finDateofBirth.setValue(null);
		this.sortOperator_finRequestDate.setSelectedIndex(0);
		this.finRequestDate.setValue(null);
		this.sortOperator_InitiateDate.setSelectedIndex(0);
		this.initiateDate.setValue(null);
		this.sortOperator_finPromotion.setSelectedIndex(0);
		this.finPromotion.setValue("");
		this.sortOperator_finRequestStage.setSelectedIndex(0);
		this.finRequestStage.setSelectedIndex(0);
		this.sortOperator_finQueuePriority.setSelectedIndex(0);
		this.finQueuePriority.setSelectedIndex(0);
		this.phoneAreaCode.setValue("");
		this.phoneCountryCode.setValue("");
		this.oldVar_sortOperator_finType = 0;
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		this.pagingFinanceMainList.setActivePage(0);
		doSearch();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for call the FinanceMain dialog
	 */
	public void onClick$button_FinanceMainList_FinanceMainSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the financeMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		new PTListReportUtils(this.requestSource + "FinanceMain", getSearchObj(),
				this.pagingFinanceMainList.getTotalSize() + 1);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Searching List based on Filters
	 */
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		this.searchObj.clearSorts();

		// FIXME: Below fields are not part of ZUL or visible FALSE
		/*
		 * this.searchObj.addFilter(new Filter("InvestmentRef", "", Filter.OP_EQUAL)); this.searchObj.addFilter(new
		 * Filter("DeviationApproval", 0, Filter.OP_EQUAL)); this.searchObj.addFilter(new Filter("RecordType",
		 * PennantConstants.RECORD_TYPE_NEW, Filter.OP_EQUAL)); this.searchObj.addFilter(new Filter("RcdMaintainSts",
		 * "", Filter.OP_EQUAL));
		 */

		if (FinanceConstants.FINSER_EVENT_PREAPPROVAL.equals(this.requestSource)) {
			this.searchObj.addFilter(new Filter("FinPreApprovedRef", FinanceConstants.FINSER_EVENT_PREAPPROVAL,
					Filter.OP_EQUAL));
		} else {
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("FinPreApprovedRef", FinanceConstants.FINSER_EVENT_PREAPPROVAL, Filter.OP_NOT_EQUAL);
			filters[1] = new Filter("FinPreApprovedRef", FinanceConstants.FINSER_EVENT_PREAPPROVAL, Filter.OP_NULL);
			this.searchObj.addFilterOr(filters);
		}

		StringBuilder whereClause = new StringBuilder();
		if (getUserWorkspace().getUserRoles() != null && getUserWorkspace().getUserRoles().size() > 0) {
			for (String role : getUserWorkspace().getUserRoles()) {
				if (whereClause.length() > 0) {
					whereClause.append(" OR ");
				}

				whereClause.append("(',' ");

				whereClause.append(SysParamUtil.dbQueryConcat);
				whereClause.append(" nextRoleCode ");
				whereClause.append(SysParamUtil.dbQueryConcat);
				whereClause.append(" ',' LIKE '%,");
				whereClause.append(role);
				whereClause.append(",%')");
			}

			whereClause.append(" ) AND ( ");
			whereClause.append("(");
			whereClause.append(getUsrFinAuthenticationQry(false));
			whereClause.append(")");

			if (!"".equals(whereClause.toString())) {
				this.searchObj.addWhereClause(whereClause.toString());
			}
		}

		this.searchObj.addSortDesc("Priority");

		// CustId
		if (StringUtils.isNotBlank(this.custCIF.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custID.getSelectedItem(), this.custCIF.getValue()
					.trim(), "LovDescCustCIF");
		}

		// FinReference
		this.searchObj = setFinReferences(this.searchObj, this.searchObj.getWhereClause());

		if (StringUtils.isNotBlank(this.fincustName.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custName.getSelectedItem(), this.fincustName
					.getValue().trim(), "lovDescCustShrtName");
		}
		// FinType
		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem itemFinType = this.sortOperator_finType.getSelectedItem();

			if (itemFinType != null) {
				final int searchOpId = ((SearchOperators) itemFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObj.addFilter(new Filter("FinType", "%" + this.finType.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),
							Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","),
							Filter.OP_NOT_IN));
				} else {
					searchObj.addFilter(new Filter("FinType", this.finType.getValue(), searchOpId));
				}
			}
		}
		// finPassport
		if (StringUtils.isNotBlank(this.finPassPort.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_passPort.getSelectedItem(), this.finPassPort
					.getValue().trim(), "lovDescCustPassportNo");
		}
		// finDOB
		if (this.finDateofBirth.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finDateofBirth.getSelectedItem(),
					DateUtility.formatDate(this.finDateofBirth.getValue(), PennantConstants.DBDateFormat),
					"LovDescCustDOB");
		}
		// finEIDNumber
		if (StringUtils.isNotBlank(this.finEIDNumber.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_eidNumber.getSelectedItem(),
					PennantApplicationUtil.unFormatEIDNumber(this.finEIDNumber.getValue().trim()), "LovDescCustCRCPR");
		}
		// finMobileNumber
		if (StringUtils.isNotBlank(this.finMobileNumber.getValue())
				&& StringUtils.isNotBlank(this.phoneAreaCode.getValue())
				&& StringUtils.isNotBlank(this.phoneCountryCode.getValue())) {
			String phoneNumber = PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
					this.phoneAreaCode.getValue(), this.finMobileNumber.getValue());
			searchObj = getSearchFilter(searchObj, this.sortOperator_mobileNumber.getSelectedItem(),
					phoneNumber.trim(), "PhoneNumber");
		}

		// InitiateDate
		// Here added filters for If InitiateDate value is null, when search
		// with opreator(<>) value not papulated.
		if (this.initiateDate.getValue() != null) {
			if (this.sortOperator_InitiateDate.getSelectedIndex() == Filter.OP_NOT_EQUAL) {
				Filter[] initiateDate = new Filter[2];
				initiateDate[0] = new Filter("InitiateDate", DateUtility.formatDate(this.initiateDate.getValue(),
						PennantConstants.DBDateTimeFormat2), Filter.OP_NOT_EQUAL);
				if (App.DATABASE == Database.ORACLE) {
					initiateDate[1] = Filter.isEmpty("InitiateDate");
				} else {
					initiateDate[1] = Filter.isNull("InitiateDate");
				}
				searchObj.addFilterOr(initiateDate);
			} else {
				searchObj = getSearchFilter(searchObj, this.sortOperator_InitiateDate.getSelectedItem(),
						DateUtility.formatDate(this.initiateDate.getValue(), PennantConstants.DBDateTimeFormat),
						"InitiateDate");
			}

		}
		// finPromotion
		if (StringUtils.isNotBlank(this.finPromotion.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finPromotion.getSelectedItem(), this.finPromotion
					.getValue().trim(), "FinType");
			searchObj.addFilter(new Filter("LovDescFinProduct", "", Filter.OP_NOT_EQUAL));
		}
		// finRequestStage
		if (this.finRequestStage.getSelectedIndex() > 0
				&& !PennantConstants.List_Select.equals(this.finRequestStage.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finRequestStage.getSelectedItem(),
					this.finRequestStage.getSelectedItem().getValue().toString(), "NextRoleCode");
		}
		// finRequestDate
		if (this.finRequestDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finRequestDate.getSelectedItem(),
					DateUtility.formatDate(this.finRequestDate.getValue(), PennantConstants.DBDateFormat),
					"FinContractDate");
		}
		// finQueuePriority
		if (this.finQueuePriority.getSelectedIndex() > 0
				&& !PennantConstants.List_Select.equals(this.finQueuePriority.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finQueuePriority.getSelectedItem(),
					this.finQueuePriority.getSelectedItem().getValue().toString(), "Priority");
		}
		// Record Status
		if (StringUtils.isNotBlank(recordStatus.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Types
		if (this.recordType.getSelectedItem() != null
				&& !"".equals(StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()))) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(), this.recordType
					.getSelectedItem().getValue().toString(), "RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceMain, this.pagingFinanceMainList);

		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		if (this.oldVar_sortOperator_finType == Filter.OP_IN || this.oldVar_sortOperator_finType == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceMainList,
					"FinanceType", this.finType.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finType.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainList, "FinanceType");
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

	// ************************************************************************
	// //
	// **On Change Events for Multi-Selection Listbox's for Search operators***
	// //
	// ************************************************************************
	// //

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_finType, oldVar_sortOperator_finType,
				this.finType);
	}

	/**
	 * On Change Search Operators resetting Data entered by User
	 * 
	 * @param listbox
	 * @param oldOperator
	 * @param textbox
	 * @return
	 */
	private int doChangeStringOperator(Listbox listbox, int oldOperator, Textbox textbox) {

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if (oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN) {
			if (!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)) {
				textbox.setValue("");
			}
		} else {
			if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
				textbox.setValue("");
			}
		}
		return searchOpId;

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
	 * Method for Fetching Current Selected Tab in Screen
	 */
	public String getCurrentTab() {
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		return tabbox.getSelectedTab().getId();
	}

	/**
	 * Method for Setting default properties
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.custID.setMaxlength(LengthConstants.LEN_CIF);
		this.custCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.fincustName.setMaxlength(50);
		this.phoneAreaCode.setMaxlength(3);
		this.phoneCountryCode.setMaxlength(3);
		this.finMobileNumber.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.finRequestDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finDateofBirth.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finPromotion.setMaxlength(8);
		this.recordStatus.setMaxlength(50);
		this.initiateDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	/**
	 * Method for Searching Finance Reference as user entered Reference exists in the Queue or not
	 */
	public JdbcSearchObject<FinanceMain> setFinReferences(JdbcSearchObject<FinanceMain> searchObject, String whereClause) {
		logger.debug("Entering");

		JdbcSearchObject<TaskOwners> referenceSearchObj = new JdbcSearchObject<TaskOwners>(TaskOwners.class);
		referenceSearchObj.addTabelName("Task_Owners");
		referenceSearchObj.addField("Reference");

		// Add filter if the user requested filtering on Finance Reference
		String finReference = StringUtils.trimToEmpty(this.finReference.getValue());

		if (StringUtils.isNotEmpty(finReference)) {
			int operatorid = 0;

			if (this.sortOperator_finReference.getSelectedItem() != null) {
				operatorid = ((SearchOperators) this.sortOperator_finReference.getSelectedItem().getAttribute("data"))
						.getSearchOperatorId();
			}

			if (operatorid == 6) {
				finReference = "%" + finReference + "%";
			}

			referenceSearchObj.addFilter(new Filter("Reference", finReference, operatorid));
		}

		referenceSearchObj.addFilterEqual("Processed", 0);
		referenceSearchObj.addFilterOr(
				Filter.equal("CurrentOwner", getUserWorkspace().getLoggedInUser().getLoginUsrID()),
				Filter.equal("CurrentOwner", 0));
		referenceSearchObj.addFilterIn("RoleCode", getUserWorkspace().getUserRoles());

		// Get the result set
		String sql = "FinReference in ("
				+ getPagedListWrapper().getPagedListService().getQueryBySearchObject(referenceSearchObj) + ")";

		if (StringUtils.isEmpty(whereClause)) {
			whereClause = "";
		} else {
			whereClause += " AND ";
		}

		searchObject.addWhereClause(whereClause + sql);

		logger.debug("Leaving");
		return searchObject;
	}

	/**
	 * Method for Searching Reference is exists in Re-Assignment Queue or not
	 */
	private boolean inReassignmentQueue(String finreference) {
		logger.debug("Entering");
		JdbcSearchObject<QueueAssignment> referenceSearchObj = new JdbcSearchObject<QueueAssignment>(
				QueueAssignment.class);
		referenceSearchObj.addTabelName("Task_Assignments_Temp");
		referenceSearchObj.addField("Reference");
		referenceSearchObj.addFilterEqual("Reference", finreference);
		List<QueueAssignment> taskOwnerList = getPagedListWrapper().getPagedListService().getBySearchObject(
				referenceSearchObj);
		if (!taskOwnerList.isEmpty()) {
			logger.debug("Leaving");
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceMain> searchObj) {
		this.searchObj = searchObj;
	}

	public FinanceMainExtService getFinanceMainExtService() {
		return financeMainExtService;
	}

	public void setFinanceMainExtService(FinanceMainExtService financeMainExtService) {
		this.financeMainExtService = financeMainExtService;
	}

}