/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceMainListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified
 * Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FinanceWorkflowRoleUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.finance.FinChangeCustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceEligibility;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.NpaAndProvisionExtension;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul file.
 */
public class FinanceMainListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = -5901195042041627750L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceMainList; // autoWired
	protected Borderlayout borderLayout_FinanceMainList; // autoWired
	protected Paging pagingFinanceMainList; // autoWired
	protected Listbox listBoxFinanceMain; // autoWired

	protected Textbox finReference; // autoWired
	protected Listbox sortOperator_finReference; // autoWired
	protected Textbox finType; // autoWired
	protected Listbox sortOperator_finType; // autoWired
	protected Textbox custCIF; // autoWired
	protected Listbox sortOperator_custID; // autoWired
	protected Longbox custID; // autoWired
	protected Textbox fincustName; // autoWired
	protected Listbox sortOperator_custName; // autoWired
	protected Textbox finMobileNumber; // autoWired
	protected Listbox sortOperator_mobileNumber; // autoWired
	protected Textbox finEIDNumber; // autoWired
	protected Listbox sortOperator_eidNumber; // autoWired
	protected Datebox finDateofBirth; // autoWired
	protected Listbox sortOperator_finDateofBirth; // autoWired
	protected Datebox finRequestDate; // autoWired
	protected Listbox sortOperator_finRequestDate; // autoWired
	protected Listbox sortOperator_finPromotion; // autoWired
	protected Textbox finPromotion; // autoWired
	protected Listbox sortOperator_finRequestStage; // autoWired
	protected Combobox finRequestStage; // autoWired
	protected Listbox sortOperator_finQueuePriority; // autoWired
	protected Combobox finQueuePriority; // autoWired
	protected Textbox recordStatus; // autoWired
	protected Listbox recordType; // autoWired
	protected Listbox sortOperator_recordStatus; // autoWired
	protected Listbox sortOperator_recordType; // autoWired
	protected Datebox initiateDate; // autoWired
	protected Listbox sortOperator_InitiateDate; // autoWired
	protected Textbox branchCode; // autoWired
	protected Listbox sortOperator_Branch; // autowired
	protected Listbox sortOperator_applicationNo; // autowired
	protected Textbox applicationNo; // autowired
	protected Listbox sortOperator_offerId; // autowired
	protected Textbox offerId; // autowired
	protected Listbox sortOperator_passPort;

	// List headers
	protected Listheader listheader_CustomerCIF; // autoWired
	protected Listheader listheader_CustomerName; // autoWired
	protected Listheader listheader_FinReference; // autoWired
	protected Listheader listheader_FinType; // autoWired
	protected Listheader listheader_FinCcy; // autoWired
	protected Listheader listheader_FinAmount; // autoWired
	protected Listheader listheader_FinancingAmount; // autoWired
	protected Listheader listheader_InitiateDate; // autoWired
	protected Listheader listheader_Promotion;
	protected Listheader listheader_Terms;
	protected Listheader listheader_RequestStage;
	protected Listheader listheader_Priority;
	protected Listheader listheader_RecordStatus; // autoWired
	protected Listheader listheader_RecordType; // autoWired
	protected Listheader listheader_ApplicationNo; // autoWired
	protected Listheader listheader_OfferId; // autoWired

	// checkRights
	protected Button btnHelp; // autoWired
	protected Button button_FinanceMainList_NewFinanceMain; // autoWired
	protected Button button_FinanceMainList_FinanceMainSearchDialog; // autoWired
	protected Button button_FinanceMainList_PrintList; // autoWired
	protected Button btnRefresh; // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;

	private String requestSource;
	private String productCode;
	private String menuItemRightName = null;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private boolean fromEligibleScreen = false;
	private FinanceEligibility finEligibility = null;
	protected int oldVar_sortOperator_finType; // autoWired
	protected int oldVar_sortOperator_Branch = -1; // autowired

	private String CREATE_CIF = "CREATECIF";
	private List<String> usrfinRolesList = new ArrayList<String>();

	private FinanceDetailService financeDetailService;
	private DedupParmService dedupParmService;
	private FinChangeCustomerService finChangeCustomerService;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private CollateralSetupDAO collateralSetupDAO;

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

	public void onCreate$window_FinanceMainList(Event event) {
		logger.debug(Literal.ENTERING);

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

		if (arguments.containsKey("product")) {
			productCode = (String) arguments.get("product");
		}

		usrfinRolesList = getUserFinanceRoles(new String[] { "FINANCE", "PROMOTION" }, requestSource);

		this.sortOperator_finReference
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getAlphaNumOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finType
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custName
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleStringOperators()));
		this.sortOperator_custName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_mobileNumber
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleStringOperators()));
		this.sortOperator_mobileNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_eidNumber
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleStringOperators()));
		this.sortOperator_eidNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_applicationNo
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleAlphaNumOperators()));
		this.sortOperator_applicationNo.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_offerId
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleAlphaNumOperators()));
		this.sortOperator_offerId.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/*
		 * this.sortOperator_passPort.setModel(new ListModelList<SearchOperators>(new SearchOperators()
		 * .getStringOperators())); this.sortOperator_passPort.setItemRenderer(new
		 * SearchOperatorListModelItemRenderer());
		 */

		if (this.sortOperator_passPort != null) {
			this.sortOperator_passPort
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_passPort.setItemRenderer(new SearchOperatorListModelItemRenderer());
		}

		this.sortOperator_finDateofBirth
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getSimpleNumericOperators()));
		this.sortOperator_finDateofBirth.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finRequestDate
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finRequestDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finPromotion
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finPromotion.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_InitiateDate
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_InitiateDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (this.sortOperator_Branch != null) {
			this.sortOperator_Branch
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
			this.sortOperator_Branch.setItemRenderer(new SearchOperatorListModelItemRenderer());
		}

		this.sortOperator_finRequestStage
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getEqualOrNotOperators()));
		ArrayList<ValueLabel> secRolesList = null;
		if (usrfinRolesList != null && !usrfinRolesList.isEmpty()) {
			Filter[] filters = new Filter[1];
			filters[0] = Filter.in("RoleCd", usrfinRolesList);
			secRolesList = PennantAppUtil.getSecRolesList(filters);
		}
		if (secRolesList == null) {
			secRolesList = new ArrayList<ValueLabel>();
		}

		fillComboBox(this.finRequestStage, "", secRolesList, "");
		this.sortOperator_finRequestStage.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finQueuePriority
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getEqualOrNotOperators()));
		fillComboBox(this.finQueuePriority, "", PennantStaticListUtil.getQueuePriority(), "");
		this.sortOperator_finQueuePriority.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_recordStatus
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_recordType
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
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

		this.listheader_FinancingAmount.setSortAscending(new FieldComparator("FinAssetValue", true));
		this.listheader_FinancingAmount.setSortDescending(new FieldComparator("FinAssetValue", false));

		this.listheader_InitiateDate.setSortAscending(new FieldComparator("InitiateDate", true));
		this.listheader_InitiateDate.setSortDescending(new FieldComparator("InitiateDate", false));

		this.listheader_ApplicationNo.setSortAscending(new FieldComparator("ApplicationNo", true));
		this.listheader_ApplicationNo.setSortDescending(new FieldComparator("ApplicationNo", false));

		this.listheader_OfferId.setSortAscending(new FieldComparator("OfferId", true));
		this.listheader_OfferId.setSortDescending(new FieldComparator("OfferId", false));

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
		this.searchObj.addSort("FinID", false);
		this.searchObj.addSort("PrevMntOn", true);

		// Field Declarations for Fetching List Data
		this.searchObj.addField("CustID");
		this.searchObj.addField("FinID");
		this.searchObj.addField("FinReference");
		this.searchObj.addField("FinAssetValue");
		this.searchObj.addField("FinType");
		this.searchObj.addField("LovDescFinTypeName");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("FinAmount");
		this.searchObj.addField("DownPayment");
		this.searchObj.addField("FeeChargeAmt");
		this.searchObj.addField("LovDescCustCIF");
		this.searchObj.addField("LovDescCustShrtName");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");
		this.searchObj.addField("NumberOfTerms");
		this.searchObj.addField("CalTerms");
		this.searchObj.addField("LovDescFinProduct");
		this.searchObj.addField("LovDescProductCodeName");
		this.searchObj.addField("NextRoleCode");
		this.searchObj.addField("LovDescRequestStage");
		this.searchObj.addField("Priority");
		this.searchObj.addField("WorkflowId");
		this.searchObj.addField("TaskId");
		this.searchObj.addField("NextTaskId");
		this.searchObj.addField("NextUserId");
		this.searchObj.addField("InitiateDate");
		this.searchObj.addField("RcdMaintainSts");
		this.searchObj.addField("FinCurrAssetValue");
		this.searchObj.addField("AdvEMITerms");
		this.searchObj.addField("Version");
		this.searchObj.addField("OfferId");
		this.searchObj.addField("ApplicationNo");

		this.searchObj.addTabelName("FinanceMain_LView");
		setSearchObj(this.searchObj);

		// rendering the list page data required or not.
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities("FinanceMainList");

		this.button_FinanceMainList_NewFinanceMain
				.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_NewFinanceMain"));
		this.button_FinanceMainList_PrintList
				.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_PrintList"));

		logger.debug("Leaving");
	}

	public void onFinanceMainItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected FinanceMain object
		final Listitem item = this.listBoxFinanceMain.getSelectedItem();

		if (item == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		// CAST AND STORE THE SELECTED OBJECT
		final FinanceMain afm = (FinanceMain) item.getAttribute("data");

		if (NpaAndProvisionExtension.ALLOW_EXTENDEDFIELDS_IN_WORKFLOW) {
			financeDetailService.addExtFieldsToAttributes(afm);
		}

		String screenEvent = "";
		if (FinServiceEvent.PREAPPROVAL.equals(this.requestSource)) {
			screenEvent = FinServiceEvent.PREAPPROVAL;
		} else {
			screenEvent = FinServiceEvent.ORG;
		}

		boolean custInMaintain = checkCustomerStatus(afm.getCustID());
		if (custInMaintain) {
			MessageUtil.showMessage("Customer is under maintainance");
			return;
		}

		if (FinServiceEvent.ORG.equals(screenEvent)
				&& SysParamUtil.isAllowed(SMTParameterConstants.CHECK_COLL_MAINTENANCE)) {
			String finReference = afm.getFinReference();
			List<CollateralAssignment> caList = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, TableType.TEMP_TAB.getSuffix());
			for (CollateralAssignment collateralAssignment : caList) {
				boolean isRcdMaintenance = collateralSetupDAO.isCollateralInMaintenance(
						collateralAssignment.getCollateralRef(), TableType.TEMP_TAB.getSuffix());
				if (isRcdMaintenance) {
					MessageUtil.showMessage("Collateral is Maintainance");
					return;
				}
			}
		}

		doLoadWorkFlow(afm.isWorkflow(), afm.getWorkflowId(), afm.getNextTaskId());
		final FinanceDetail financeDetail = financeDetailService.getOriginationFinance(afm.getFinID(),
				afm.getNextRoleCode(), screenEvent, getRole());

		if (financeDetail == null) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = afm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
			logger.debug(Literal.LEAVING);
			return;
		}

		financeDetail.getFinScheduleData().getFinanceMain().addAttributes(afm.getAttributes());

		// Check whether the record was locked by any other user.
		String userId = financeDetail.getFinScheduleData().getFinanceMain().getNextUserId();
		if (StringUtils.isNotBlank(userId)) {
			// Due to parallel workflow getting multiple userId's
			String[] userIds = StringUtils.split(userId, PennantConstants.DELIMITER_COMMA);
			List<String> list = (userIds != null) ? Arrays.asList(userIds) : null;
			if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))
					&& CollectionUtils.isNotEmpty(list)
					&& !(list.contains(Long.toString(getUserWorkspace().getUserId())))) {
				SecurityUser user = PennantAppUtil.getUser(Long.valueOf(list.get(0)));
				String userName = "";

				if (user != null) {
					userName = user.getUsrLogin();
				}

				MessageUtil.showMessage(Labels.getLabel("label_Finance_Record_Locked", new String[] { userName }));
				return;
			}
		}

		// Check swap customer or not
		boolean finReferenceProcess = finChangeCustomerService.isFinReferenceProcess(afm.getFinID());

		if (finReferenceProcess) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = afm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41095", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
			logger.debug(Literal.LEAVING);
			return;
		}

		// Check QDP Case
		boolean allowProcess = DisbursementInstCtrl.checkQDPProceeed(financeDetail);
		if (!allowProcess) {
			MessageUtil.showMessage(Labels.getLabel("label_Finance_QuickDisb_Queue"));
			return;
		}

		if (afm.getWorkflowId() != 0 && !PennantConstants.RCD_STATUS_RESUBMITTED.equals(afm.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_SAVED.equals(afm.getRecordStatus())) {

			if (!inReassignmentQueue(afm.getFinReference())) {
				String[] nextTasks = afm.getNextTaskId().split(";");

				if (nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {
						String baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
						if (!"".equals(baseRole) && usrfinRolesList.contains(baseRole) && afm.getNextUserId() != null
								&& afm.getNextUserId()
										.contains(String.valueOf(getUserWorkspace().getLoggedInUser().getUserId()))) {
							break;
						}
					}
				}

				String whereCond = " Where FinID = ?";

				if (doCheckAuthority(afm, whereCond, new Object[] { afm.getFinID() })) {
					if (tATProcess(afm, financeDetail)) {
						validateCustExistance(financeDetail);
					}
				} else {
					MessageUtil.showError(Labels.getLabel("info.not_authorized"));
					return;
				}
			} else {
				MessageUtil.showError(Labels.getLabel("RECORD_IN_REASSIGNMENT_QUEUE"));
				return;
			}
		} else {
			if (tATProcess(afm, financeDetail)) {
				validateCustExistance(financeDetail);
			}
		}
		logUserAccess("menu_Item_NewFinanceMain", financeDetail.getFinReference());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for checking if customer is in maintainance
	 * 
	 * @param financeDetail
	 * @return
	 */
	private boolean checkCustomerStatus(Long custID) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Customer> searchObject = new JdbcSearchObject<Customer>(Customer.class);
		searchObject.addTabelName("Customers_Temp");
		searchObject.addFilterEqual("CustID", custID);

		List<Customer> rightList = pagedListService.getBySearchObject(searchObject);
		if (rightList != null && !rightList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for Validating Customer is Exists in Core Banking Account created against Customer in Core banking System
	 * if required
	 * 
	 * @param financeDetail
	 */
	private void validateCustExistance(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		if (checkUserAccess(financeDetail)) {
			if (ImplementationConstants.VALIDATE_CORE_CUST_UPDATE) {

				if (StringUtils.isBlank(customer.getCustCoreBank())) {
					// check retail or corporate customer?
					if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)
							|| StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)
							|| customer.isSalariedCustomer()) {
						MessageUtil.showError(Labels.getLabel("label_FinanceMainDialog_Mandatory_Prospect.value"));
						return;
					}

					// CIF not Exists
					doReserveCIF(financeDetail, CREATE_CIF);
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
		logger.debug(Literal.ENTERING);

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
		filters[2] = new Filter("UsrID", getUserWorkspace().getLoggedInUser().getUserId(), Filter.OP_EQUAL);
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
	 * @param fm
	 * @param fd
	 * @return
	 */
	private boolean tATProcess(FinanceMain fm, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinanceReferenceDetail frd = new FinanceReferenceDetail();
		frd.setMandInputInStage(fm.getNextRoleCode() + ",");
		frd.setFinType(fm.getFinType());

		List<FinanceReferenceDetail> frdList = dedupParmService.getQueryCodeList(frd, "_TATView");

		if (frdList.isEmpty()) {
			logger.debug(Literal.LEAVING);
			return true;
		}

		TATDetail tatDetail = financeDetailService.getTATDetail(fm.getFinReference(), fm.getNextRoleCode());

		if (tatDetail == null || (tatDetail != null && tatDetail.gettATStartTime() == null)) {
			final String msg = Labels.getLabel("label_TATProcess_UserAction");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				if (tatDetail == null) {
					tatDetail = new TATDetail();
					tatDetail.setModule(FinServiceEvent.ORG);
					tatDetail.setReference(fm.getFinReference());
					tatDetail.setRoleCode(fm.getNextRoleCode());
					tatDetail.setFinType(fm.getFinType());
					tatDetail.settATStartTime(new Timestamp(System.currentTimeMillis()));
					financeDetailService.saveTATDetail(tatDetail);
				} else {
					tatDetail.settATStartTime(new Timestamp(System.currentTimeMillis()));
					financeDetailService.updateTATDetail(tatDetail);
				}
				return true;
			} else {
				return false;
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceMainList_NewFinanceMain(Event event) {
		logger.debug(Literal.ENTERING);

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceMain. For handed over these parameter only a Map is accepted. So we put the FinanceMain object in a
		 * HashMap.
		 */
		if (usrfinRolesList == null || usrfinRolesList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("USER_FINROLES_NOTASSIGNED"));
			return;
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("financeMainListCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("requestSource", this.requestSource);
		map.put("role", usrfinRolesList);
		map.put("menuItemRightName", menuItemRightName);
		if (fromEligibleScreen) {
			map.put("fromEligibleScreen", true);
			map.put("finEligibility", finEligibility);
		}

		// call the ZUL-file with the parameters packed in a map
		try {
			if (StringUtils.equals(FinanceConstants.PRODUCT_CD, productCode)) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectCDFinanceSchemeDialog.zul", null,
						map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectFinanceTypeDialog.zul", null,
						map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		fromEligibleScreen = false;
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	protected void showDetailView(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

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
			productType = (productType.substring(0, 1)).concat(productType.substring(1));
			productType = productType.toUpperCase();

			StringBuilder zulPath = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			boolean zulFound = false;

			if ("QDE".equals(StringUtils.trimToEmpty(this.requestSource))) {
				zulPath.append("QDEFinanceMainDialog.zul");
				zulFound = true;
			} else if (FinServiceEvent.PREAPPROVAL.equals(this.requestSource)) {
				zulPath.append("FinancePreApprovalDialog.zul");
				zulFound = true;
			}

			if (zulFound) {
				Executions.createComponents(zulPath.toString(), this.window_FinanceMainList, map);
				return;
			}

			switch (productType) {
			case FinanceConstants.PRODUCT_CONVENTIONAL:
				zulPath.append("ConvFinanceMainDialog.zul");
				break;
			case FinanceConstants.PRODUCT_CD:
				zulPath = new StringBuilder("/WEB-INF/pages/Finance/Cd/");
				zulPath.append("CDFinanceMainDialog.zul");
				break;
			case FinanceConstants.PRODUCT_ODFACILITY:
				zulPath = new StringBuilder("/WEB-INF/pages/Finance/Overdraft/");
				zulPath.append("OverdraftFinanceMainDialog.zul");
				break;
			case FinanceConstants.PRODUCT_DISCOUNT:
				zulPath.append("DiscountFinanceMainDialog.zul");
				break;
			default:
				zulPath.append("FinanceMainDialog.zul");
				break;
			}

			Executions.createComponents(zulPath.toString(), this.window_FinanceMainList, map);
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
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_FinanceMainList);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		this.sortOperator_custID.setSelectedIndex(0);
		this.custCIF.setValue("");
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_applicationNo.setSelectedIndex(0);
		this.applicationNo.setValue("");
		this.sortOperator_offerId.setSelectedIndex(0);
		this.offerId.setValue("");
		this.branchCode.setValue("");
		this.sortOperator_custName.setSelectedIndex(0);
		this.fincustName.setValue("");
		this.sortOperator_mobileNumber.setSelectedIndex(0);
		this.finMobileNumber.setValue("");
		this.sortOperator_eidNumber.setSelectedIndex(0);
		this.finEIDNumber.setValue("");
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
		this.sortOperator_Branch.setSelectedIndex(0);
		this.oldVar_sortOperator_Branch = -1;
		this.oldVar_sortOperator_finType = 0;
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		this.pagingFinanceMainList.setActivePage(0);
		doSearch();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for call the FinanceMain dialog
	 */
	public void onClick$button_FinanceMainList_FinanceMainSearchDialog(Event event) {
		logger.debug(Literal.ENTERING);
		doSearch();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When the financeMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		new PTListReportUtils(this.requestSource + "FinanceMain", searchObj,
				this.pagingFinanceMainList.getTotalSize() + 1);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Searching List based on Filters
	 */
	public void doSearch() {
		logger.debug(Literal.ENTERING);

		this.searchObj.clearFilters();
		this.searchObj.clearSorts();

		if (usrfinRolesList == null || usrfinRolesList.isEmpty()) {
			return;
		}

		this.searchObj.addFilter(new Filter("InvestmentRef", "", Filter.OP_EQUAL));
		this.searchObj.addFilter(new Filter("DeviationApproval", 0, Filter.OP_EQUAL));

		if (StringUtils.equals(FinanceConstants.PRODUCT_CD, productCode)) {
			this.searchObj.addFilter(new Filter("ProductCategory", FinanceConstants.PRODUCT_CD, Filter.OP_EQUAL));
		} else {
			this.searchObj.addFilter(new Filter("ProductCategory", FinanceConstants.PRODUCT_CD, Filter.OP_NOT_EQUAL));
		}

		this.searchObj.addFilter(new Filter("RcdMaintainSts", "", Filter.OP_EQUAL));

		StringBuilder whereClause = new StringBuilder();

		for (String role : usrfinRolesList) {
			if (whereClause.length() > 0) {
				whereClause.append(" OR ");
			}

			whereClause.append("(',' ");

			whereClause.append(QueryUtil.getQueryConcat());
			whereClause.append(" nextRoleCode ");
			whereClause.append(QueryUtil.getQueryConcat());
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

		this.searchObj.addSortDesc("Priority");
		this.searchObj.addSortDesc("LastMntOn");

		// CustId
		if (StringUtils.isNotBlank(this.custCIF.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custID.getSelectedItem(),
					this.custCIF.getValue().trim(), "LovDescCustCIF");
		}

		// FinReference
		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
			this.searchObj = setFinReferences(this.searchObj, this.searchObj.getWhereClause());
		} else {

			if (StringUtils.isNotEmpty(this.finReference.getValue())) {
				// get the search operator
				final Listitem itemFinReference = this.sortOperator_finReference.getSelectedItem();

				if (itemFinReference != null) {
					final int searchOpId = ((SearchOperators) itemFinReference.getAttribute("data"))
							.getSearchOperatorId();

					if (searchOpId == -1) {
						// do nothing
					} else if (searchOpId == Filter.OP_LIKE) {
						searchObj.addFilter(new Filter("FinReference",
								"%" + this.finReference.getValue().trim().toUpperCase() + "%", searchOpId));
					} else if (searchOpId == Filter.OP_IN) {
						this.searchObj.addFilter(new Filter("FinReference",
								this.finReference.getValue().trim().split(","), Filter.OP_IN));
					} else if (searchOpId == Filter.OP_NOT_IN) {
						this.searchObj.addFilter(new Filter("FinReference",
								this.finReference.getValue().trim().split(","), Filter.OP_NOT_IN));
					} else {
						searchObj
								.addFilter(new Filter("FinReference", this.finReference.getValue().trim(), searchOpId));
					}
				}
			}
		}

		if (StringUtils.isNotBlank(this.fincustName.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custName.getSelectedItem(),
					this.fincustName.getValue().trim(), "lovDescCustShrtName");
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
					searchObj.addFilter(
							new Filter("FinType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj
							.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(
							new Filter("FinType", this.finType.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObj.addFilter(new Filter("FinType", this.finType.getValue(), searchOpId));
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
					this.searchObj.addFilter(new Filter("finBranch",
							"%" + this.branchCode.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObj.addFilter(
							new Filter("FinBranch", this.branchCode.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObj.addFilter(
							new Filter("FinBranch", this.branchCode.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					this.searchObj.addFilter(new Filter("FinBranch", this.branchCode.getValue().trim(), searchOpId));
				}
			}
		}

		// finDOB
		if (this.finDateofBirth.getValue() != null) {
			searchObj = getSearchFilter(searchObj, sortOperator_finDateofBirth.getSelectedItem(),
					finDateofBirth.getValue(), "LovDescCustDOB");
		}
		// finEIDNumber
		if (StringUtils.isNotBlank(this.finEIDNumber.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_eidNumber.getSelectedItem(),
					PennantApplicationUtil.unFormatEIDNumber(this.finEIDNumber.getValue().trim()), "LovDescCustCRCPR");
		}
		// finMobileNumber
		if (StringUtils.isNotBlank(this.finMobileNumber.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_mobileNumber.getSelectedItem(),
					this.finMobileNumber.getValue().trim(), "PhoneNumber");
		}
		// APPlicationNumber
		if (StringUtils.isNotBlank(this.applicationNo.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_applicationNo.getSelectedItem(),
					this.applicationNo.getValue().trim(), "ApplicationNo");
		}
		// OfferId
		if (StringUtils.isNotBlank(this.offerId.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_offerId.getSelectedItem(),
					this.offerId.getValue().trim(), "offerId");
		}

		// InitiateDate
		// Here added filters for If InitiateDate value is null, when search
		// with opreator(<>) value not papulated.
		if (this.initiateDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, sortOperator_InitiateDate.getSelectedItem(), initiateDate.getValue(),
					"InitiateDate");
		}
		// finPromotion
		if (StringUtils.isNotBlank(this.finPromotion.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finPromotion.getSelectedItem(),
					this.finPromotion.getValue().trim(), "FinType");
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
			searchObj = getSearchFilter(searchObj, sortOperator_finRequestDate.getSelectedItem(),
					finRequestDate.getValue(), "FinContractDate");
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
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}

		// Set the ListModel for the articles.
		pagedListWrapper.init(this.searchObj, this.listBoxFinanceMain, this.pagingFinanceMainList);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.oldVar_sortOperator_Branch == Filter.OP_IN || this.oldVar_sortOperator_Branch == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceMainList, "Branch",
					this.branchCode.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.branchCode.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainList, "Branch");
			if (dataObject instanceof String) {
				this.branchCode.setValue("");
			} else {
				Branch details = (Branch) dataObject;
				if (details != null) {
					this.branchCode.setValue(details.getBranchCode());
				}
			}
		}
		logger.debug(Literal.LEAVING);
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

	public void onSelect$sortOperator_Branch(Event event) {
		this.oldVar_sortOperator_Branch = doChangeStringOperator(sortOperator_Branch, oldVar_sortOperator_Branch,
				this.branchCode);
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
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.ENTERING);
		doSearchCustomerCIF();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
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
	@Override
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
		logger.debug(Literal.ENTERING);

		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.fincustName.setMaxlength(50);
		this.finMobileNumber.setMaxlength(LengthConstants.LEN_MOBILE);
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
	public JdbcSearchObject<FinanceMain> setFinReferences(JdbcSearchObject<FinanceMain> searchObject,
			String whereClause) {
		logger.debug(Literal.ENTERING);

		String reference = StringUtils.trimToEmpty(finReference.getValue());

		// Prepare the additional where clause.
		StringBuilder sql = new StringBuilder();
		if (StringUtils.isNotEmpty(whereClause)) {
			sql.append(whereClause);
			sql.append(" and ");
		}
		sql.append("FinReference in (select Reference");
		sql.append(" from Task_Owners");
		sql.append(" where (");
		if (StringUtils.isNotEmpty(reference)) {
			int operatorId = 0;
			String operatorSign = "=";

			// TODO: Only allow equals to search on Finance Reference and remove
			// the below two IF blocks.
			if (sortOperator_finReference.getSelectedItem() != null) {
				operatorId = ((SearchOperators) sortOperator_finReference.getSelectedItem().getAttribute("data"))
						.getSearchOperatorId();
				operatorSign = ((SearchOperators) sortOperator_finReference.getSelectedItem().getAttribute("data"))
						.getSearchOperatorSign();
			}

			if (operatorId == 6) {
				reference = "%" + reference + "%";
				operatorSign = "like";
			}

			sql.append(" (Reference ").append(operatorSign).append(" '").append(reference.replace("'", "''"))
					.append("') and");
		}
		sql.append(" (Processed = 0)");
		sql.append(" and (CurrentOwner = ").append(getUserWorkspace().getLoggedInUser().getUserId())
				.append(" or CurrentOwner = 0)");
		if (!usrfinRolesList.isEmpty()) {
			sql.append(" and (RoleCode in ('");
			sql.append(StringUtils.join(usrfinRolesList, "','"));
			sql.append("')");
		}
		sql.append(")))");

		searchObject.addWhereClause(sql.toString());

		logger.debug("Leaving");
		return searchObject;
	}

	private boolean inReassignmentQueue(String finreference) {
		logger.debug(Literal.ENTERING);
		JdbcSearchObject<QueueAssignment> referenceSearchObj = new JdbcSearchObject<QueueAssignment>(
				QueueAssignment.class);
		referenceSearchObj.addTabelName("Task_Assignments_Temp");
		referenceSearchObj.addField("Reference");
		referenceSearchObj.addFilterEqual("Reference", finreference);
		List<QueueAssignment> taskOwnerList = getPagedListWrapper().getPagedListService()
				.getBySearchObject(referenceSearchObj);
		if (!taskOwnerList.isEmpty()) {
			logger.debug(Literal.ENTERING);
			return true;
		}

		logger.debug(Literal.ENTERING);
		return false;
	}

	public ArrayList<String> getUserFinanceRoles(String[] moduleNames, String finEvent) {
		Set<String> finRoleSet = FinanceWorkflowRoleUtil.getFinanceRoles(moduleNames, finEvent);
		ArrayList<String> arrayRoleCode = new ArrayList<String>();
		Object[] roles = getUserWorkspace().getUserRoleSet().toArray();

		for (Object role : roles) {
			if (finRoleSet.contains(role.toString())) {
				arrayRoleCode.add(role.toString());
			}
		}
		return arrayRoleCode;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceMain> searchObj) {
		this.searchObj = searchObj;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public void setFinChangeCustomerService(FinChangeCustomerService finChangeCustomerService) {
		this.finChangeCustomerService = finChangeCustomerService;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

}